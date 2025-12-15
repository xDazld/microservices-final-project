package dev.pacr.dns.service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.pacr.dns.model.rfc8427.DnsMessage;
import dev.pacr.dns.model.rfc8427.DnsMessageConverter;
import dev.pacr.dns.model.rfc8427.ResourceRecord;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import io.quarkus.cache.Cache;
import io.quarkus.cache.CacheName;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.io.Serializable;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Core DNS resolution service that handles DNS queries.
 * RFC 9520 compliant with negative caching for resolution failures.
 * RFC 8767 compliant with serve-stale for improved DNS resiliency.
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc9520.html">RFC 9520</a>
 * @see <a href="https://www.rfc-editor.org/rfc/rfc8767.html">RFC 8767</a>
 */
@ApplicationScoped
public class DNSResolver {
	
	private static final Logger LOG = Logger.getLogger(DNSResolver.class);
	private static final long CACHE_TTL_SECONDS = 300; // 5 minutes
	
	// RFC 8767 Section 5: Maximum stale timer - recommended 1-3 days
	// "The suggested value is between 1 and 3 days"
	private static final long MAX_STALE_SECONDS = 86400; // 1 day (24 hours)
	
	// RFC 8767 Section 5: Client response timer
	// "recommended value of 1.8 seconds as being just under a common timeout value of 2 seconds"
	private static final long CLIENT_RESPONSE_TIMEOUT_MS = 1800; // 1.8 seconds
	
	// RFC 8767 Section 5: Failure recheck timer
	// "Attempts to refresh from non-responsive or otherwise failing authoritative nameservers
	// are recommended to be done no more frequently than every 30 seconds"
	private static final long FAILURE_RECHECK_SECONDS = 30;
	
	// RFC 8767 Section 4: TTL to set on stale records in response
	// "RECOMMENDED value of 30 seconds"
	private static final long STALE_RESPONSE_TTL = 30;
	
	// RFC 9520 Section 3.1: Maximum retry limit
	// "A resolver MUST NOT retry a given query to a server address over a given DNS transport
	// more than twice (i.e., three queries in total)"
	private static final int MAX_RETRIES = 2; // 2 retries = 3 total queries
	
	@Inject
	@CacheName("dns-response-cache")
	Cache dnsCache;
	
	// RFC 8767: Track last failure time for failure recheck timer
	private final Map<String, Instant> lastFailureTime = new ConcurrentHashMap<>();
	
	// Track cache statistics manually since Quarkus Cache doesn't expose them
	private final AtomicLong cacheHits = new AtomicLong(0);
	private final AtomicLong cacheMisses = new AtomicLong(0);
	private final AtomicLong cacheWrites = new AtomicLong(0);
	private final Instant startTime = Instant.now();
	
	@Inject
	MeterRegistry registry;
	
	@Inject
	NegativeCacheService negativeCacheService;
	
	/**
	 * Resolve a DNS query with RFC 9520 compliant negative caching and RFC 8767 serve-stale.
	 *
	 * Per RFC 9520 Section 3.2: "When an incoming query matches a cached resolution failure,
	 * the resolver MUST NOT send any corresponding outgoing queries until after the cache entries
	 * expire."
	 *
	 * Per RFC 8767 Section 4: "If the data is unable to be authoritatively refreshed when the TTL
	 * expires, the record MAY be used as though it is unexpired."
	 */
	@Timed(value = "dns.query.resolution", description = "Time taken to resolve DNS query")
	@Counted(value = "dns.query.count", description = "Number of DNS queries processed")
	public DnsMessage resolve(DnsMessage query) {
		LOG.infof("Resolving DNS query: %s (type: %d)", query.getQname(), query.getQtype());
		
		// Explicitly increment counter (in case @Counted annotation doesn't work)
		registry.counter("dns.query.count").increment();
		
		long startTime = System.currentTimeMillis();
		String cacheKey = getCacheKey(query);
		
		// RFC 9520 Section 3.2: Check negative cache for resolution failures FIRST
		if (negativeCacheService.isCachedFailure(query.getQname(), query.getQtype())) {
			LOG.infof("Query blocked due to cached resolution failure: %s (type %d)",
					query.getQname(), query.getQtype());
			// Return SERVFAIL for cached failures
			return DnsMessageConverter.createResponse(query.getQname(), query.getQtype(),
					query.getQclass(), 2, // SERVFAIL
					new ArrayList<>(), 0L);
		}
		
		// Check positive cache for unexpired records
		CachedResponse cachedResponse = null;
		try {
			// Try to get from cache - if key doesn't exist, throw exception to signal cache miss
			Object cachedObj = dnsCache.get(cacheKey, k -> {
				// This function is only called on cache miss
				// We don't want to cache null, so we throw an exception to signal cache miss
				throw new RuntimeException("Cache miss");
			}).await().indefinitely();
			cachedResponse =
					cachedObj instanceof CachedResponse ? (CachedResponse) cachedObj : null;
			if (cachedResponse != null) {
				cacheHits.incrementAndGet();
			}
		} catch (RuntimeException e) {
			// Cache miss - continue with fresh lookup
			cacheMisses.incrementAndGet();
			LOG.debugf("Cache miss for domain: %s", query.getQname());
		}
		
		if (cachedResponse != null && !cachedResponse.isExpired()) {
			LOG.debugf("Cache hit for domain: %s", query.getQname());
			return cachedResponse.response;
		}
		
		// RFC 8767 Section 5: Check failure recheck timer
		// "no more frequently than every 30 seconds"
		Instant lastFailure = lastFailureTime.get(cacheKey);
		boolean withinFailureRecheckPeriod = lastFailure != null &&
				Duration.between(lastFailure, Instant.now()).getSeconds() < FAILURE_RECHECK_SECONDS;
		
		// RFC 8767 Section 5: If within failure recheck period, immediately use stale data if
		// available
		if (withinFailureRecheckPeriod && cachedResponse != null && cachedResponse.isStale()) {
			LOG.infof("Within failure recheck period for %s, serving stale data (age: %ds)",
					query.getQname(), cachedResponse.getAgeSeconds());
			return createStaleResponse(cachedResponse.response, query);
		}
		
		// RFC 8767 Section 5: Client response timer - try to get fresh data but use stale if
		// timeout
		DnsMessage response = null;
		boolean timedOut = false;
		
		try {
			// Start resolution attempt
			response = performResolutionWithTimeout(query, startTime);
			
			// Clear last failure time on success
			lastFailureTime.remove(cacheKey);
			
		} catch (ClientTimeoutException e) {
			// RFC 8767 Section 5: Client response timer expired
			timedOut = true;
			LOG.infof("Client response timeout for %s, checking for stale data", query.getQname());
		}
		
		// RFC 8767 Section 5: If timed out or failed, try to serve stale data
		if (timedOut && cachedResponse != null && cachedResponse.isStale()) {
			LOG.infof("Serving stale data for %s due to timeout (age: %ds)", query.getQname(),
					cachedResponse.getAgeSeconds());
			
			// RFC 8767 Section 5: Continue resolution in background (simulated here)
			// In production, this would spawn an async task
			// For now, we just return stale and the next request will retry
			
			return createStaleResponse(cachedResponse.response, query);
		}
		
		// If no response obtained and no stale data available, return SERVFAIL
		if (response == null) {
			LOG.warnf("No response and no stale data available for %s", query.getQname());
			return DnsMessageConverter.createResponse(query.getQname(), query.getQtype(),
					query.getQclass(), 2, // SERVFAIL
					new ArrayList<>(), 0L);
		}
		
		return response;
	}
	
	/**
	 * RFC 8767 Section 5: Perform resolution with client response timeout.
	 *
	 * @throws ClientTimeoutException if client response timer expires
	 */
	private DnsMessage performResolutionWithTimeout(DnsMessage query, long startTime)
			throws ClientTimeoutException {
		String qtypeStr = DnsMessageConverter.getQtypeString(query.getQtype());
		String cacheKey = getCacheKey(query);
		
		try {
			// Check if we've exceeded client response timeout
			long elapsed = System.currentTimeMillis() - startTime;
			if (elapsed > CLIENT_RESPONSE_TIMEOUT_MS) {
				throw new ClientTimeoutException("Client response timeout exceeded");
			}
			
			List<String> addresses = performLookupWithRetry(query.getQname(), qtypeStr);
			DnsMessage response = DnsMessageConverter.createResponse(query.getQname(),
					query.getQtype(),
					query.getQclass(), 0, // NOERROR
					addresses, 300L // TTL 5 minutes
			);
			
			// Cache the successful response using Quarkus Cache
			CachedResponse cachedResp = new CachedResponse(response);
			dnsCache.get(cacheKey, k -> cachedResp).subscribe().with(item -> {
				cacheWrites.incrementAndGet();
				LOG.debugf("Cached response for %s", query.getQname());
			}, failure -> LOG.warnf("Failed to cache response for %s: %s", query.getQname(),
					failure.getMessage()));
			
			LOG.infof("Successfully resolved %s to %s", query.getQname(), addresses);
			return response;
			
		} catch (UnknownHostException e) {
			LOG.warnf("Domain not found: %s", query.getQname());
			// NXDOMAIN is NOT a resolution failure per RFC 9520, so don't cache in negative cache
			return DnsMessageConverter.createResponse(query.getQname(),
					query.getQtype(),
					query.getQclass(), 3, // NXDOMAIN
					new ArrayList<>(), 300L);
			
		} catch (DNSResolutionException e) {
			// RFC 9520 compliant failure handling
			LOG.errorf(e, "DNS resolution failure for domain: %s", query.getQname());
			
			// RFC 8767: Record failure time for failure recheck timer
			lastFailureTime.put(cacheKey, Instant.now());
			
			// Cache the resolution failure per RFC 9520 Section 3.2
			negativeCacheService.cacheFailure(query.getQname(), query.getQtype(),
					e.getFailureType());
			
			return DnsMessageConverter.createResponse(query.getQname(),
					query.getQtype(),
					query.getQclass(), 2, // SERVFAIL
					new ArrayList<>(), 0L);
			
		} catch (RuntimeException e) {
			LOG.errorf(e, "Unexpected error resolving domain: %s", query.getQname());
			
			// RFC 8767: Record failure time
			lastFailureTime.put(cacheKey, Instant.now());
			
			// Cache as OTHER failure type
			negativeCacheService.cacheFailure(query.getQname(), query.getQtype(),
					NegativeCacheService.FailureType.OTHER);
			
			return DnsMessageConverter.createResponse(query.getQname(),
					query.getQtype(),
					query.getQclass(), 2, // SERVFAIL
					new ArrayList<>(), 0L);
		}
	}
	
	/**
	 * RFC 8767 Section 4: Create a response using stale data with TTL set to 30 seconds.
	 * <p>
	 * "When returning a response containing stale records, a recursive resolver MUST set the
	 * TTL of
	 * each expired record in the message to a value greater than 0, with a RECOMMENDED value of 30
	 * seconds."
	 */
	private DnsMessage createStaleResponse(DnsMessage originalResponse, DnsMessage query) {
		// Clone the response and update TTLs to 30 seconds
		DnsMessage staleResponse = new DnsMessage();
		
		// Copy all header fields
		staleResponse.setId(query.getId());
		staleResponse.setQr(1); // Response
		staleResponse.setOpcode(originalResponse.getOpcode());
		staleResponse.setAa(0); // Not authoritative for stale data
		staleResponse.setTc(originalResponse.getTc());
		staleResponse.setRd(query.getRd());
		staleResponse.setRa(1); // Recursion available
		staleResponse.setAd(0); // Not authenticated for stale data
		staleResponse.setCd(query.getCd());
		staleResponse.setRcode(originalResponse.getRcode());
		
		// Copy question section
		staleResponse.setQname(query.getQname());
		staleResponse.setQtype(query.getQtype());
		staleResponse.setQclass(query.getQclass());
		staleResponse.setQdcount(1);
		
		// Copy answer RRs with updated TTL per RFC 8767
		if (originalResponse.getAnswerRRs() != null) {
			List<ResourceRecord> staleAnswers = new ArrayList<>();
			for (ResourceRecord rr : originalResponse.getAnswerRRs()) {
				ResourceRecord staleRR =
						new ResourceRecord(rr.getName(), rr.getType(), rr.getRclass(),
								STALE_RESPONSE_TTL, // RFC 8767: Set to 30 seconds
								rr.getRdata());
				staleAnswers.add(staleRR);
			}
			staleResponse.setAnswerRRs(staleAnswers);
			staleResponse.setAncount(staleAnswers.size());
		} else {
			staleResponse.setAncount(0);
		}
		
		// Copy authority and additional sections (also with updated TTL)
		staleResponse.setNscount(0); // Don't include authority for stale
		staleResponse.setArcount(0); // Don't include additional for stale
		
		return staleResponse;
	}
	
	/**
	 * Perform DNS lookup with RFC 9520 compliant retry logic.
	 *
	 * Per RFC 9520 Section 3.1: "A resolver MUST NOT retry a given query to a server address
	 * over a given DNS transport more than twice (i.e., three queries in total) before
	 * considering the server address unresponsive over that DNS transport for that query."
	 *
	 * @throws DNSResolutionException if all retries fail
	 */
	private List<String> performLookupWithRetry(String domain, String queryType)
			throws UnknownHostException, DNSResolutionException {
		
		Exception lastException = null;
		
		// RFC 9520 Section 3.1: Try up to 3 times (1 initial + 2 retries)
		for (int attempt = 0; attempt <= MAX_RETRIES; attempt++) {
			try {
				if (attempt > 0) {
					LOG.debugf("Retry attempt %d for domain: %s", attempt, domain);
				}
				
				return performLookup(domain, queryType);
				
			} catch (UnknownHostException e) {
				// UnknownHostException is NXDOMAIN, not a resolution failure
				// Don't retry, just throw it up
				throw e;
			} catch (Exception e) {
				lastException = e;
				LOG.warnf("Lookup attempt %d failed for %s: %s", attempt + 1, domain,
						e.getMessage());
				
				// Don't retry on the last attempt
				if (attempt < MAX_RETRIES) {
					// Small delay before retry (implementation dependent per RFC 9520)
					try {
						Thread.sleep(100 * (attempt + 1)); // 100ms, 200ms backoff
					} catch (InterruptedException ie) {
						Thread.currentThread().interrupt();
						break;
					}
				}
			}
		}
		
		// All retries failed - determine failure type and throw
		NegativeCacheService.FailureType failureType = determineFailureType(lastException);
		throw new DNSResolutionException("All retry attempts failed for domain: " + domain,
				lastException, failureType);
	}
	
	/**
	 * Determine the failure type from an exception for RFC 9520 categorization.
	 */
	private NegativeCacheService.FailureType determineFailureType(Exception e) {
		if (e instanceof SocketTimeoutException) {
			return NegativeCacheService.FailureType.TIMEOUT;
		} else if (e instanceof NoRouteToHostException || e instanceof ConnectException) {
			return NegativeCacheService.FailureType.UNREACHABLE;
		} else if (e != null && e.getMessage() != null) {
			String msg = e.getMessage().toLowerCase();
			if (msg.contains("refused")) {
				return NegativeCacheService.FailureType.REFUSED;
			} else if (msg.contains("servfail")) {
				return NegativeCacheService.FailureType.SERVFAIL;
			}
		}
		return NegativeCacheService.FailureType.OTHER;
	}
	
	/**
	 * Perform actual DNS lookup (unchanged from original implementation).
	 */
	private List<String> performLookup(String domain, String queryType)
			throws UnknownHostException {
		// For this implementation, we'll use Java's built-in DNS resolution
		// In a production system, you'd use a proper DNS library like dnsjava
		
		if ("A".equals(queryType) || "AAAA".equals(queryType)) {
			InetAddress[] addresses = InetAddress.getAllByName(domain);
			return Arrays.stream(addresses).map(InetAddress::getHostAddress).toList();
		}
		
		// For other record types, return a placeholder
		// In production, implement proper support for MX, TXT, CNAME, etc.
		LOG.warnf("Query type %s not fully implemented, performing basic A record lookup",
				queryType);
		InetAddress address = InetAddress.getByName(domain);
		return List.of(address.getHostAddress());
	}
	
	/**
	 * Clear expired cache entries from both positive and negative caches.
	 * Per RFC 9520 Section 3.2, expired entries should be cleaned up.
	 * Per RFC 8767 Section 5, stale data should be retained up to MAX_STALE_SECONDS.
	 */
	public void clearExpiredCache() {
		// RFC 8767 Section 5: Only remove data that exceeds maximum stale timer
		// "The suggested value is between 1 and 3 days"
		// Note: With Quarkus Cache (Redis), expiration is handled automatically by the cache
		// backend
		// based on the expire-after-write configuration. Manual cleanup is not needed.
		LOG.debugf("Cache cleanup - expiration handled by cache backend");
		
		// Clear negative cache
		negativeCacheService.clearExpiredEntries();
	}
	
	/**
	 * Generate cache key for a query
	 */
	private String getCacheKey(DnsMessage query) {
		return query.getQname() + ':' + query.getQtype();
	}
	
	/**
	 * Get cache statistics including both positive and negative caches.
	 * Per RFC 9520, monitoring of negative cache is important for operators.
	 * Per RFC 8767, monitoring of stale data usage is also important.
	 */
	public Map<String, Object> getCacheStats() {
		long hits = cacheHits.get();
		long misses = cacheMisses.get();
		long writes = cacheWrites.get();
		long total = hits + misses;
		
		double hitRate = total > 0 ? ((double) hits / total) * 100.0 : 0.0;
		long uptimeSeconds = java.time.Duration.between(startTime, Instant.now()).getSeconds();
		
		Map<String, Object> positiveCache =
				Map.of("cacheName", "dns-response-cache (Redis)", "backend", "Redis", "cacheHits",
						hits, "cacheMisses", misses, "totalLookups", total, "hitRate",
						String.format("%.1f%%", hitRate), "cacheWrites", writes, "ttl",
						"300 seconds (5 minutes)", "uptimeSeconds", uptimeSeconds);
		
		return Map.of("positiveCache", positiveCache, "negativeCache",
				negativeCacheService.getCacheStats()
		);
	}
	
	/**
	 * Custom exception for DNS resolution failures per RFC 9520.
	 */
	public static class DNSResolutionException extends Exception {
		private final NegativeCacheService.FailureType failureType;
		
		public DNSResolutionException(String message, Throwable cause,
									  NegativeCacheService.FailureType failureType) {
			super(message, cause);
			this.failureType = failureType;
		}
		
		public NegativeCacheService.FailureType getFailureType() {
			return failureType;
		}
	}
	
	/**
	 * Internal class for cached responses with RFC 8767 stale data support
	 */
	public static class CachedResponse implements Serializable {
		private static final long serialVersionUID = 1L;
		
		@JsonProperty("response")
		DnsMessage response;
		
		@JsonProperty("cachedAt")
		Instant cachedAt;
		
		// No-arg constructor for serialization
		public CachedResponse() {
		}
		
		@JsonCreator
		public CachedResponse(@JsonProperty("response") DnsMessage response,
							  @JsonProperty("cachedAt") Instant cachedAt) {
			this.response = response;
			this.cachedAt = cachedAt != null ? cachedAt : Instant.now();
		}
		
		CachedResponse(DnsMessage response) {
			this.response = response;
			this.cachedAt = Instant.now();
		}
		
		/**
		 * RFC 1035 Section 3.2.1: Check if data is expired based on original TTL
		 */
		boolean isExpired() {
			return java.time.Duration.between(cachedAt, Instant.now()).getSeconds() >
					CACHE_TTL_SECONDS;
		}
		
		/**
		 * RFC 8767 Section 5: Check if data is stale but still usable Data is stale if expired but
		 * within MAX_STALE_SECONDS
		 */
		boolean isStale() {
			long age = getAgeSeconds();
			return age > CACHE_TTL_SECONDS && age <= MAX_STALE_SECONDS;
		}
		
		/**
		 * RFC 8767 Section 5: Check if data exceeds maximum stale timer "The suggested value is
		 * between 1 and 3 days"
		 */
		boolean isTooStale() {
			return getAgeSeconds() > MAX_STALE_SECONDS;
		}
		
		/**
		 * Get the age of this cache entry in seconds
		 */
		long getAgeSeconds() {
			return java.time.Duration.between(cachedAt, Instant.now()).getSeconds();
		}
	}
	
	/**
	 * Exception thrown when client response timeout is exceeded (RFC 8767)
	 */
	private static class ClientTimeoutException extends Exception {
		public ClientTimeoutException(String message) {
			super(message);
		}
	}
}
