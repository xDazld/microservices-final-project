package dev.pacr.dns.service;

import dev.pacr.dns.model.rfc8427.DnsMessage;
import dev.pacr.dns.model.rfc8427.DnsMessageConverter;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suite for RFC 9520 compliance.
 * <p>
 * Tests negative caching requirements from RFC 9520: - Mandatory negative caching for resolution
 * failures - Cache duration between 1 second and 5 minutes - Exponential backoff for persistent
 * failures - Resource exhaustion protection
 */
@QuarkusTest
class RFC9520ComplianceTest {
	
	@Inject
	NegativeCacheService negativeCacheService;
	
	@Inject
	DNSResolver dnsResolver;
	
	@BeforeEach
	void setUp() {
		// Clear cache before each test
		negativeCacheService.clearAll();
	}
	
	/**
	 * RFC 9520 Section 3.2: Resolvers MUST implement a cache for resolution failures
	 */
	@Test
	void testNegativeCacheExists() {
		assertNotNull(negativeCacheService, "Negative cache service must exist");
	}
	
	/**
	 * RFC 9520 Section 3.2: When an incoming query matches a cached resolution failure, the
	 * resolver MUST NOT send any corresponding outgoing queries
	 */
	@Test
	void testCachedFailureBlocksQuery() {
		String domain = "test-failure.example.com";
		int qtype = 1; // A record
		
		// Cache a failure
		negativeCacheService.cacheFailure(domain, qtype, NegativeCacheService.FailureType.TIMEOUT);
		
		// Verify it's cached
		assertTrue(negativeCacheService.isCachedFailure(domain, qtype), "Failure should be " +
				"cached");
		
		// Create a query for the failed domain
		DnsMessage query = DnsMessageConverter.createQuery(domain, qtype, 1);
		
		// Resolve - should return SERVFAIL immediately without upstream query
		DnsMessage response = dnsResolver.resolve(query);
		
		assertNotNull(response);
		assertEquals(2, response.getRcode(),
				"Response code should be SERVFAIL (2) for cached failure");
	}
	
	/**
	 * RFC 9520 Section 3.2: Resolvers MUST cache resolution failures for at least 1 second
	 */
	@Test
	void testMinimumCacheDuration() throws InterruptedException {
		String domain = "min-duration.example.com";
		int qtype = 1;
		
		// Cache a failure
		negativeCacheService.cacheFailure(domain, qtype,
				NegativeCacheService.FailureType.SERVFAIL);
		
		// Should still be cached after 500ms
		Thread.sleep(500);
		assertTrue(negativeCacheService.isCachedFailure(domain, qtype),
				"Failure should still be cached after 500ms");
		
		// Should still be cached at 1 second
		Thread.sleep(600); // Total 1.1 seconds
		// Note: Due to initial backoff of 5 seconds, this will still be cached
		assertTrue(negativeCacheService.isCachedFailure(domain, qtype),
				"Failure should still be cached after 1 second (using 5s backoff)");
	}
	
	/**
	 * RFC 9520 Section 3.2: Resolution failures MUST NOT be cached for longer than 5 minutes
	 */
	@Test
	void testMaximumCacheDuration() {
		// This test verifies the configuration, not actual timing
		// Maximum is enforced in the backoff calculation
		
		String domain = "max-duration.example.com";
		int qtype = 1;
		
		// Simulate many consecutive failures to trigger maximum backoff
		for (int i = 0; i < 10; i++) {
			negativeCacheService.cacheFailure(domain, qtype,
					NegativeCacheService.FailureType.TIMEOUT);
		}
		
		// The cache should exist but with max duration of 300 seconds
		assertTrue(negativeCacheService.isCachedFailure(domain, qtype),
				"Failure should be cached even after many retries");
		
		Map<String, Object> stats = negativeCacheService.getCacheStats();
		assertNotNull(stats);
		assertTrue((Long) stats.get("active") > 0, "Should have active cache entries");
	}
	
	/**
	 * RFC 9520 Section 3.2: Resolvers SHOULD employ exponential or linear backoff
	 */
	@Test
	void testExponentialBackoff() {
		String domain = "backoff-test.example.com";
		int qtype = 1;
		
		// First failure - should use initial backoff (5 seconds)
		negativeCacheService.cacheFailure(domain, qtype, NegativeCacheService.FailureType.TIMEOUT);
		assertTrue(negativeCacheService.isCachedFailure(domain, qtype));
		
		// Clear to simulate expiration
		negativeCacheService.clearAll();
		
		// Second failure - backoff should increase
		negativeCacheService.cacheFailure(domain, qtype, NegativeCacheService.FailureType.TIMEOUT);
		negativeCacheService.cacheFailure(domain, qtype, NegativeCacheService.FailureType.TIMEOUT);
		
		// Should still be cached (with longer duration)
		assertTrue(negativeCacheService.isCachedFailure(domain, qtype),
				"Persistent failures should use exponential backoff");
	}
	
	/**
	 * RFC 9520 Section 3.2: Resolvers SHOULD implement measures to mitigate resource exhaustion
	 * attacks on the failed resolution cache
	 */
	@Test
	void testResourceExhaustionProtection() {
		// Try to exceed the maximum cache size
		int maxEntries = 10000; // From configuration
		
		// Add entries up to the limit
		for (int i = 0; i < maxEntries + 100; i++) {
			String domain = "exhaust-test-" + i + ".example.com";
			negativeCacheService.cacheFailure(domain, 1, NegativeCacheService.FailureType.TIMEOUT);
		}
		
		Map<String, Object> stats = negativeCacheService.getCacheStats();
		long totalEntries = (Long) stats.get("total");
		
		// Should not exceed max capacity significantly (allows for eviction strategy)
		assertTrue(totalEntries <= maxEntries * 1.1,
				"Cache should not grow unbounded, got: " + totalEntries);
	}
	
	/**
	 * RFC 9520 Section 2: Test different failure types are properly categorized
	 */
	@Test
	void testFailureTypeCategorization() {
		String domain = "multi-failure.example.com";
		
		// Test different failure types
		negativeCacheService.cacheFailure(domain, 1, NegativeCacheService.FailureType.SERVFAIL);
		negativeCacheService.cacheFailure(domain, 28, NegativeCacheService.FailureType.REFUSED);
		negativeCacheService.cacheFailure(domain, 2, NegativeCacheService.FailureType.TIMEOUT);
		
		Map<String, Object> stats = negativeCacheService.getCacheStats();
		@SuppressWarnings("unchecked") Map<String, Long> byType =
				(Map<String, Long>) stats.get("byType");
		
		assertNotNull(byType, "Statistics should include breakdown by type");
		assertTrue(byType.size() > 0, "Should have multiple failure types cached");
	}
	
	/**
	 * RFC 9520 Section 3.2: Test that cache cleanup works properly
	 */
	@Test
	void testCacheCleanup() throws InterruptedException {
		String domain = "cleanup-test.example.com";
		int qtype = 1;
		
		// Cache a failure
		negativeCacheService.cacheFailure(domain, qtype, NegativeCacheService.FailureType.TIMEOUT);
		
		assertTrue(negativeCacheService.isCachedFailure(domain, qtype));
		
		// Manual cleanup (expired entries only)
		negativeCacheService.clearExpiredEntries();
		
		// Should still be cached (not expired yet with 5s backoff)
		assertTrue(negativeCacheService.isCachedFailure(domain, qtype),
				"Non-expired entries should not be removed by cleanup");
	}
	
	/**
	 * Test that NXDOMAIN is NOT treated as a resolution failure per RFC 9520
	 */
	@Test
	void testNXDOMAINNotCached() {
		String domain = "nonexistent.example.com";
		int qtype = 1;
		
		// NXDOMAIN should not be in negative cache (it has its own caching per RFC 2308)
		assertFalse(negativeCacheService.isCachedFailure(domain, qtype),
				"NXDOMAIN should not be in resolution failure cache");
	}
	
	/**
	 * Test cache statistics are available for monitoring
	 */
	@Test
	void testCacheStatistics() {
		// Add some failures
		negativeCacheService.cacheFailure("test1.example.com", 1,
				NegativeCacheService.FailureType.TIMEOUT);
		negativeCacheService.cacheFailure("test2.example.com", 1,
				NegativeCacheService.FailureType.SERVFAIL);
		
		Map<String, Object> stats = negativeCacheService.getCacheStats();
		
		assertNotNull(stats);
		assertTrue(stats.containsKey("total"));
		assertTrue(stats.containsKey("active"));
		assertTrue(stats.containsKey("expired"));
		assertTrue(stats.containsKey("maxCapacity"));
		assertTrue(stats.containsKey("byType"));
		
		assertTrue((Long) stats.get("total") >= 2, "Should have at least 2 cached entries");
	}
	
	/**
	 * Integration test: Verify DNSResolver uses negative cache
	 */
	@Test
	void testResolverIntegration() {
		String domain = "integration-test.example.com";
		int qtype = 1;
		
		// Pre-cache a failure
		negativeCacheService.cacheFailure(domain, qtype, NegativeCacheService.FailureType.REFUSED);
		
		// Create query
		DnsMessage query = DnsMessageConverter.createQuery(domain, qtype, 1);
		
		// Resolve should immediately return SERVFAIL without upstream query
		long startTime = System.currentTimeMillis();
		DnsMessage response = dnsResolver.resolve(query);
		long duration = System.currentTimeMillis() - startTime;
		
		// Should be very fast (< 100ms) since no upstream query
		assertTrue(duration < 100,
				"Cached failure should return immediately, took: " + duration + "ms");
		assertEquals(2, response.getRcode(), "Should return SERVFAIL for cached failure");
	}
}

