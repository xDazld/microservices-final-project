package dev.pacr.dns.service;

import dev.pacr.dns.model.rfc8427.DnsMessage;
import dev.pacr.dns.model.rfc8427.DnsMessageConverter;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.net.ConnectException;
import java.net.InetAddress;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Core DNS resolution service that handles DNS queries.
 * RFC 9520 compliant with negative caching for resolution failures.
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc9520.html">RFC 9520</a>
 */
@ApplicationScoped
public class DNSResolver {
	
	private static final Logger LOG = Logger.getLogger(DNSResolver.class);
	private static final long CACHE_TTL_SECONDS = 300; // 5 minutes
	
	// RFC 9520 Section 3.1: Maximum retry limit
	// "A resolver MUST NOT retry a given query to a server address over a given DNS transport
	// more than twice (i.e., three queries in total)"
	private static final int MAX_RETRIES = 2; // 2 retries = 3 total queries
	
	// Simple in-memory cache for DNS responses
	private final Map<String, CachedResponse> cache = new ConcurrentHashMap<>();
	
	@Inject
	MeterRegistry registry;
	
	@Inject
	NegativeCacheService negativeCacheService;
	
	/**
	 * Resolve a DNS query with RFC 9520 compliant negative caching.
	 *
	 * Per RFC 9520 Section 3.2: "When an incoming query matches a cached resolution failure,
	 * the resolver MUST NOT send any corresponding outgoing queries until after the cache entries
	 * expire."
	 */
	@Timed(value = "dns.query.resolution", description = "Time taken to resolve DNS query")
	@Counted(value = "dns.query.count", description = "Number of DNS queries processed")
	public DnsMessage resolve(DnsMessage query) {
		LOG.infof("Resolving DNS query: %s (type: %d)", query.getQname(), query.getQtype());
		
		// RFC 9520 Section 3.2: Check negative cache for resolution failures FIRST
		if (negativeCacheService.isCachedFailure(query.getQname(), query.getQtype())) {
			LOG.infof("Query blocked due to cached resolution failure: %s (type %d)",
					query.getQname(), query.getQtype());
			// Return SERVFAIL for cached failures
			return DnsMessageConverter.createResponse(query.getQname(), query.getQtype(),
					query.getQclass(), 2, // SERVFAIL
					new ArrayList<>(), 0L);
		}
		
		// Check positive cache
		CachedResponse cachedResponse = cache.get(getCacheKey(query));
		if (cachedResponse != null && !cachedResponse.isExpired()) {
			LOG.debugf("Cache hit for domain: %s", query.getQname());
			return cachedResponse.response;
		}
		
		// Perform actual DNS resolution with retry logic
		DnsMessage response;
		String qtypeStr = DnsMessageConverter.getQtypeString(query.getQtype());
		
		try {
			List<String> addresses = performLookupWithRetry(query.getQname(), qtypeStr);
			response = DnsMessageConverter.createResponse(query.getQname(), query.getQtype(),
					query.getQclass(), 0, // NOERROR
					addresses, 300L // TTL 5 minutes
			);
			
			// Cache the successful response
			cache.put(getCacheKey(query), new CachedResponse(response));
			
			LOG.infof("Successfully resolved %s to %s", query.getQname(), addresses);
			
		} catch (UnknownHostException e) {
			LOG.warnf("Domain not found: %s", query.getQname());
			// NXDOMAIN is NOT a resolution failure per RFC 9520, so don't cache in negative cache
			response = DnsMessageConverter.createResponse(query.getQname(), query.getQtype(),
					query.getQclass(), 3, // NXDOMAIN
					new ArrayList<>(), 300L);
		} catch (DNSResolutionException e) {
			// RFC 9520 compliant failure handling
			LOG.errorf(e, "DNS resolution failure for domain: %s", query.getQname());
			
			// Cache the resolution failure per RFC 9520 Section 3.2
			negativeCacheService.cacheFailure(query.getQname(), query.getQtype(),
					e.getFailureType());
			
			response = DnsMessageConverter.createResponse(query.getQname(), query.getQtype(),
					query.getQclass(), 2, // SERVFAIL
					new ArrayList<>(), 0L);
		} catch (RuntimeException e) {
			LOG.errorf(e, "Unexpected error resolving domain: %s", query.getQname());
			
			// Cache as OTHER failure type
			negativeCacheService.cacheFailure(query.getQname(), query.getQtype(),
					NegativeCacheService.FailureType.OTHER);
			
			response = DnsMessageConverter.createResponse(query.getQname(), query.getQtype(),
					query.getQclass(), 2, // SERVFAIL
					new ArrayList<>(), 0L);
		}
		
		return response;
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
	 */
	public void clearExpiredCache() {
		// Clear positive cache
		cache.entrySet().removeIf(entry -> entry.getValue().isExpired());
		LOG.debugf("Positive cache cleanup completed. Current size: %d", cache.size());
		
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
	 */
	public Map<String, Object> getCacheStats() {
		long expired = cache.values().stream().filter(CachedResponse::isExpired).count();
		
		Map<String, Object> positiveCache =
				Map.of("total", cache.size(), "expired", expired, "active",
						cache.size() - expired);
		
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
	 * Internal class for cached responses
	 */
	private static class CachedResponse {
		final DnsMessage response;
		final Instant cachedAt;
		
		CachedResponse(DnsMessage response) {
			this.response = response;
			this.cachedAt = Instant.now();
		}
		
		boolean isExpired() {
			return java.time.Duration.between(cachedAt, Instant.now()).getSeconds() >
					CACHE_TTL_SECONDS;
		}
	}
}
