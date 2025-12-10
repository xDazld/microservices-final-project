package dev.pacr.dns.service;

import dev.pacr.dns.model.rfc8427.DnsMessage;
import dev.pacr.dns.model.rfc8427.DnsMessageConverter;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.net.InetAddress;
import java.net.UnknownHostException;
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
	public DnsMessage resolve(DnsMessage query) {
		LOG.infof("Resolving DNS query: %s (type: %d)", query.getQname(), query.getQtype());
		
		// Check cache first
		CachedResponse cachedResponse = cache.get(getCacheKey(query));
		if (cachedResponse != null && !cachedResponse.isExpired()) {
			LOG.debugf("Cache hit for domain: %s", query.getQname());
			return cachedResponse.response;
		}
		
		// Perform actual DNS resolution
		DnsMessage response;
		String qtypeStr = DnsMessageConverter.getQtypeString(query.getQtype());
		
		try {
			List<String> addresses = performLookup(query.getQname(), qtypeStr);
			response = DnsMessageConverter.createResponse(query.getQname(), query.getQtype(),
					query.getQclass(), 0, // NOERROR
					addresses, 300L // TTL 5 minutes
			);
			
			// Cache the response
			cache.put(getCacheKey(query), new CachedResponse(response));
			
			LOG.infof("Successfully resolved %s to %s", query.getQname(), addresses);
			
		} catch (UnknownHostException e) {
			LOG.warnf("Domain not found: %s", query.getQname());
			response = DnsMessageConverter.createResponse(query.getQname(), query.getQtype(),
					query.getQclass(), 3, // NXDOMAIN
					new ArrayList<>(), 300L);
		} catch (RuntimeException e) {
			LOG.errorf(e, "Error resolving domain: %s", query.getQname());
			response = DnsMessageConverter.createResponse(query.getQname(), query.getQtype(),
					query.getQclass(), 2, // SERVFAIL
					new ArrayList<>(), 0L);
		}
		
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
	private String getCacheKey(DnsMessage query) {
		return query.getQname() + ':' + query.getQtype();
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
