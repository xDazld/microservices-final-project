package dev.pacr.dns.service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.cache.Cache;
import io.quarkus.cache.CacheName;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RFC 9520 compliant negative caching service for DNS resolution failures.
 * <p>
 * This service implements the requirements from RFC 9520 "Negative Caching of DNS Resolution
 * Failures": - Caches resolution failures for at least 1 second (configurable) - Maximum cache
 * duration of 5 minutes (per RFC 9520 Section 3.2) - Implements exponential backoff for persistent
 * failures - Prevents resource exhaustion attacks - Handles various failure types: SERVFAIL,
 * REFUSED, timeouts, validation failures, etc.
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc9520.html">RFC 9520</a>
 */
@ApplicationScoped
public class NegativeCacheService {
	
	private static final Logger LOG = Logger.getLogger(NegativeCacheService.class);
	
	// RFC 9520 Section 3.2: Minimum cache duration is 1 second
	private static final long MIN_CACHE_DURATION_SECONDS = 1;
	
	// RFC 9520 Section 3.2: Maximum cache duration is 5 minutes (300 seconds)
	private static final long MAX_CACHE_DURATION_SECONDS = 300;
	
	// Initial backoff duration in seconds
	private static final long INITIAL_BACKOFF_SECONDS = 5;
	
	// Maximum number of cache entries to prevent resource exhaustion
	private static final int MAX_CACHE_ENTRIES = 10000;
	
	// Track size since Quarkus Cache doesn't expose size directly
	private final Map<String, FailureCacheEntry> sizeTracker = new ConcurrentHashMap<>();
	@Inject
	@CacheName("dns-negative-cache")
	Cache negativeCache;
	
	/**
	 * Check if a query should be blocked due to cached resolution failure.
	 * <p>
	 * Per RFC 9520 Section 3.2: "When an incoming query matches a cached resolution failure, the
	 * resolver MUST NOT send any corresponding outgoing queries until after the cache entries
	 * expire."
	 *
	 * @param domain The domain name being queried
	 * @param qtype  The query type
	 * @return true if the query should be blocked (cached failure), false otherwise
	 */
	public boolean isCachedFailure(String domain, int qtype) {
		String cacheKey = getCacheKey(domain, qtype);
		FailureCacheEntry entry = null;
		
		try {
			// Try to get from cache - throw exception on miss to avoid caching null
			Object cachedObj = negativeCache.get(cacheKey, k -> {
				throw new RuntimeException("Cache miss");
			}).await().indefinitely();
			entry = cachedObj instanceof FailureCacheEntry ? (FailureCacheEntry) cachedObj : null;
		} catch (RuntimeException e) {
			// Cache miss - no cached failure
			return false;
		}
		
		if (entry == null) {
			return false;
		}
		
		if (entry.isExpired()) {
			// Clean up expired entry
			negativeCache.invalidate(cacheKey).await().indefinitely();
			sizeTracker.remove(cacheKey);
			return false;
		}
		
		LOG.debugf("Cached resolution failure found for %s (type %d), blocking query", domain,
				qtype);
		return true;
	}
	
	/**
	 * Cache a resolution failure.
	 * <p>
	 * Per RFC 9520 Section 3.2: "Resolvers MUST implement a cache for resolution failures."
	 * Implements exponential backoff per RFC 9520 Section 3.2.
	 *
	 * @param domain      The domain name that failed
	 * @param qtype       The query type
	 * @param failureType The type of failure
	 */
	public void cacheFailure(String domain, int qtype, FailureType failureType) {
		// RFC 9520 Section 3.2: Protection against resource exhaustion
		if (sizeTracker.size() >= MAX_CACHE_ENTRIES) {
			LOG.warnf("Failure cache at maximum capacity (%d entries), evicting oldest entries",
					MAX_CACHE_ENTRIES);
			evictOldestEntries();
		}
		
		String cacheKey = getCacheKey(domain, qtype);
		FailureCacheEntry existingEntry = null;
		
		try {
			// Try to get existing entry - throw exception on miss
			Object cachedObj = negativeCache.get(cacheKey, k -> {
				throw new RuntimeException("Cache miss");
			}).await().indefinitely();
			existingEntry =
					cachedObj instanceof FailureCacheEntry ? (FailureCacheEntry) cachedObj : null;
		} catch (RuntimeException e) {
			// Cache miss - this is a new failure
		}
		
		long cacheDuration;
		int retryCount = 0;
		
		if (existingEntry != null && !existingEntry.isExpired()) {
			// Persistent failure - apply exponential backoff per RFC 9520 Section 3.2
			retryCount = existingEntry.retryCount + 1;
			cacheDuration = calculateBackoffDuration(retryCount);
			LOG.debugf("Persistent failure for %s (type %d), retry count: %d, backoff: %ds",
					domain,
					qtype, retryCount, cacheDuration);
		} else {
			// First failure - use initial backoff
			cacheDuration = INITIAL_BACKOFF_SECONDS;
			LOG.debugf("Initial failure for %s (type %d), caching for %ds", domain, qtype,
					cacheDuration);
		}
		
		FailureCacheEntry entry =
				new FailureCacheEntry(domain, qtype, failureType, cacheDuration, retryCount);
		
		// Store in Quarkus Cache and size tracker
		negativeCache.get(cacheKey, k -> entry).subscribe()
				.with(item -> LOG.debugf("Cached failure for %s (type %d)", domain, qtype),
						failure -> LOG.warnf("Failed to cache failure for %s: %s", domain,
								failure.getMessage()));
		sizeTracker.put(cacheKey, entry);
		
		LOG.infof("Cached %s failure for %s (type %d) for %d seconds (retry count: %d)",
				failureType, domain, qtype, cacheDuration, retryCount);
	}
	
	/**
	 * Calculate backoff duration using exponential backoff strategy. Per RFC 9520 Section 3.2:
	 * "Resolvers SHOULD employ an exponential or linear backoff algorithm to increase the cache
	 * duration for persistent resolution failures."
	 *
	 * @param retryCount Number of consecutive failures
	 * @return Cache duration in seconds, bounded by MAX_CACHE_DURATION_SECONDS
	 */
	private long calculateBackoffDuration(int retryCount) {
		// Exponential backoff: INITIAL * 2^retryCount
		long duration = INITIAL_BACKOFF_SECONDS * (1L << Math.min(retryCount, 6)); // Cap at 2^6
		
		// RFC 9520 Section 3.2: "not to exceed the 5-minute upper limit"
		return Math.min(duration, MAX_CACHE_DURATION_SECONDS);
	}
	
	/**
	 * Evict oldest cache entries to prevent resource exhaustion. Per RFC 9520 Section 3.2:
	 * "resolvers SHOULD implement measures to mitigate resource exhaustion attacks on the failed
	 * resolution cache."
	 */
	private void evictOldestEntries() {
		// Remove 10% of oldest entries
		int toRemove = MAX_CACHE_ENTRIES / 10;
		
		sizeTracker.entrySet().stream().sorted(Comparator.comparing(e -> e.getValue().cachedAt))
				.limit(toRemove).forEach(entry -> {
					negativeCache.invalidate(entry.getKey()).await().indefinitely();
					sizeTracker.remove(entry.getKey());
				});
		
		LOG.debugf("Evicted %d oldest entries from failure cache", toRemove);
	}
	
	/**
	 * Clear expired cache entries. Should be called periodically by a cleanup task.
	 */
	public void clearExpiredEntries() {
		int initialSize = sizeTracker.size();
		
		sizeTracker.entrySet().removeIf(entry -> {
			if (entry.getValue().isExpired()) {
				negativeCache.invalidate(entry.getKey()).await().indefinitely();
				return true;
			}
			return false;
		});
		
		int removed = initialSize - sizeTracker.size();
		
		if (removed > 0) {
			LOG.debugf("Cleared %d expired entries from failure cache", removed);
		}
	}
	
	/**
	 * Get cache statistics for monitoring/admin purposes.
	 *
	 * @return Map containing cache statistics
	 */
	public Map<String, Object> getCacheStats() {
		long expired = sizeTracker.values().stream().filter(FailureCacheEntry::isExpired).count();
		long total = sizeTracker.size();
		
		Map<String, Long> typeBreakdown = new ConcurrentHashMap<>();
		sizeTracker.values().stream().filter(entry -> !entry.isExpired())
				.forEach(entry -> typeBreakdown.merge(entry.failureType.name(), 1L, Long::sum));
		
		long active = total - expired;
		return Map.of("total", total, "expired", expired, "active", active, "maxCapacity",
				(long) MAX_CACHE_ENTRIES, "byType", typeBreakdown);
	}
	
	/**
	 * Generate cache key for a domain and query type.
	 */
	private String getCacheKey(String domain, int qtype) {
		return domain.toLowerCase() + ':' + qtype;
	}
	
	/**
	 * Clear all cache entries (for testing or admin purposes).
	 */
	public void clearAll() {
		int size = sizeTracker.size();
		negativeCache.invalidateAll().await().indefinitely();
		sizeTracker.clear();
		LOG.infof("Cleared all %d entries from failure cache", size);
	}
	
	/**
	 * Types of resolution failures per RFC 9520
	 */
	public enum FailureType {
		SERVFAIL,           // Server failure (Section 2.1)
		REFUSED,            // Query refused (Section 2.2)
		TIMEOUT,            // Server timeout (Section 2.3)
		UNREACHABLE,        // Server unreachable (Section 2.3)
		DELEGATION_LOOP,    // Delegation loop detected (Section 2.4)
		ALIAS_LOOP,         // CNAME/DNAME loop detected (Section 2.5)
		DNSSEC_VALIDATION,  // DNSSEC validation failure (Section 2.6)
		FORMERR,            // Format error (Section 2.7)
		OTHER               // Other resolution failures
	}
	
	/**
	 * Internal class representing a cached failure entry.
	 */
	public static class FailureCacheEntry implements Serializable {
		private static final long serialVersionUID = 1L;
		
		@JsonProperty("domain")
		String domain;
		
		@JsonProperty("qtype")
		int qtype;
		
		@JsonProperty("failureType")
		FailureType failureType;
		
		@JsonProperty("cachedAt")
		Instant cachedAt;
		
		@JsonProperty("cacheDurationSeconds")
		long cacheDurationSeconds;
		
		@JsonProperty("retryCount")
		int retryCount;
		
		// No-arg constructor for serialization
		public FailureCacheEntry() {
		}
		
		@JsonCreator
		public FailureCacheEntry(@JsonProperty("domain") String domain,
								 @JsonProperty("qtype") int qtype,
								 @JsonProperty("failureType") FailureType failureType,
								 @JsonProperty("cachedAt") Instant cachedAt,
								 @JsonProperty("cacheDurationSeconds") long cacheDurationSeconds,
								 @JsonProperty("retryCount") int retryCount) {
			this.domain = domain;
			this.qtype = qtype;
			this.failureType = failureType;
			this.cachedAt = cachedAt != null ? cachedAt : Instant.now();
			this.cacheDurationSeconds = cacheDurationSeconds;
			this.retryCount = retryCount;
		}
		
		FailureCacheEntry(String domain, int qtype, FailureType failureType,
						  long cacheDurationSeconds, int retryCount) {
			this.domain = domain;
			this.qtype = qtype;
			this.failureType = failureType;
			this.cachedAt = Instant.now();
			this.cacheDurationSeconds = cacheDurationSeconds;
			this.retryCount = retryCount;
		}
		
		boolean isExpired() {
			return Duration.between(cachedAt, Instant.now()).getSeconds() >= cacheDurationSeconds;
		}
	}
}

