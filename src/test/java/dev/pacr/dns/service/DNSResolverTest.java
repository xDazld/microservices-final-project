package dev.pacr.dns.service;

import dev.pacr.dns.model.DNSQuery;
import dev.pacr.dns.model.DNSResponse;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for DNS Resolver service Tests core DNS resolution logic without external
 * dependencies
 */
@QuarkusTest
class DNSResolverTest {
	
	@Inject
	DNSResolver dnsResolver;
	
	@BeforeEach
	void setUp() {
		// Setup is handled by Quarkus
	}
	
	@Test
	void testResolverIsInjected() {
		assertNotNull(dnsResolver);
	}
	
	@Test
	void testResolveReturnsResponse() {
		DNSQuery query = new DNSQuery("example.com", "A", "192.168.1.1", "UDP");
		DNSResponse response = dnsResolver.resolve(query);
		
		assertNotNull(response);
		assertEquals("example.com", response.getDomain());
	}
	
	@Test
	void testResolveSetsStatus() {
		DNSQuery query = new DNSQuery("example.com", "A", "192.168.1.1", "UDP");
		DNSResponse response = dnsResolver.resolve(query);
		
		assertNotNull(response.getStatus());
		assertTrue(response.getStatus().length() > 0);
	}
	
	@Test
	void testResolveRecordsResponseTime() {
		DNSQuery query = new DNSQuery("example.com", "A", "192.168.1.1", "UDP");
		DNSResponse response = dnsResolver.resolve(query);
		
		assertTrue(response.getResponseTimeMs() >= 0);
	}
	
	@Test
	void testGetCacheStats() {
		var stats = dnsResolver.getCacheStats();
		assertNotNull(stats);
	}
	
	@Test
	void testClearExpiredCache() {
		dnsResolver.clearExpiredCache();
		// If no exception is thrown, test passes
		assertTrue(true);
	}
}

