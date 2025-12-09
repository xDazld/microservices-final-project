package dev.pacr.dns.service;

import dev.pacr.dns.model.DNSQuery;
import dev.pacr.dns.model.DNSResponse;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Core DNS resolution service that handles DNS queries
 */
@ApplicationScoped
public class DNSResolver {
	
	private static final Logger LOG = Logger.getLogger(DNSResolver.class);
	private static final long CACHE_TTL_SECONDS = 300; // 5 minutes
	// Simple in-memory cache for DNS responses
	private final Map<String, CachedResponse> cache = new ConcurrentHashMap<>();
	@Inject
	MeterRegistry registry;
	
	/**
	 * Resolve a DNS query
	 */
	@Timed(value = "dns.query.resolution", description = "Time taken to resolve DNS query")
	@Counted(value = "dns.query.count", description = "Number of DNS queries processed")
	public DNSResponse resolve(DNSQuery query) {
		Instant startTime = Instant.now();
		LOG.infof("Resolving DNS query: %s (type: %s) from %s", query.getDomain(),
				query.getQueryType(), query.getClientIp());
		
		// Check cache first
		CachedResponse cachedResponse = cache.get(getCacheKey(query));
		if (cachedResponse != null && !cachedResponse.isExpired()) {
			LOG.debugf("Cache hit for domain: %s", query.getDomain());
			DNSResponse response = cachedResponse.response;
			response.setCached(true);
			response.setResponseTimeMs(Duration.between(startTime, Instant.now()).toMillis());
			return response;
		}
		
		// Perform actual DNS resolution
		DNSResponse response = new DNSResponse();
		response.setQueryId(query.getId());
		response.setDomain(query.getDomain());
		
		try {
			List<String> addresses = performLookup(query.getDomain(), query.getQueryType());
			response.setResolvedAddresses(addresses);
			response.setStatus("ALLOWED");
			
			// Cache the response
			cache.put(getCacheKey(query), new CachedResponse(response));
			
			LOG.infof("Successfully resolved %s to %s", query.getDomain(), addresses);
			
		} catch (UnknownHostException e) {
			LOG.warnf("Domain not found: %s", query.getDomain());
			response.setResolvedAddresses(new ArrayList<>());
			response.setStatus("NXDOMAIN");
		} catch (RuntimeException e) {
			LOG.errorf(e, "Error resolving domain: %s", query.getDomain());
			response.setResolvedAddresses(new ArrayList<>());
			response.setStatus("ERROR");
		}
		
		response.setResponseTimeMs(Duration.between(startTime, Instant.now()).toMillis());
		response.setCached(false);
		
		return response;
	}
	
	/**
	 * Perform actual DNS lookup
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
	 * Generate cache key for a query
	 */
	private String getCacheKey(DNSQuery query) {
		return query.getDomain() + ':' + query.getQueryType();
	}
	
	/**
	 * Clear expired cache entries
	 */
	public void clearExpiredCache() {
		cache.entrySet().removeIf(entry -> entry.getValue().isExpired());
		LOG.debugf("Cache cleanup completed. Current size: %d", cache.size());
	}
	
	/**
	 * Get cache statistics
	 */
	public Map<String, Object> getCacheStats() {
		long expired = cache.values().stream().filter(CachedResponse::isExpired).count();
		return Map.of("total", cache.size(), "expired", expired, "active", cache.size() - expired);
	}
	
	/**
	 * Internal class for cached responses
	 */
	private static class CachedResponse {
		final DNSResponse response;
		final Instant cachedAt;
		
		CachedResponse(DNSResponse response) {
			this.response = new DNSResponse();
			this.response.setQueryId(response.getQueryId());
			this.response.setDomain(response.getDomain());
			this.response.setResolvedAddresses(new ArrayList<>(response.getResolvedAddresses()));
			this.response.setStatus(response.getStatus());
			this.cachedAt = Instant.now();
		}
		
		boolean isExpired() {
			return Duration.between(cachedAt, Instant.now()).getSeconds() > CACHE_TTL_SECONDS;
		}
	}
}
