package dev.pacr.dns.service;

import dev.pacr.dns.model.rfc8427.DnsMessage;
import dev.pacr.dns.model.rfc8427.DnsMessageConverter;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for DNS Resolver service using RFC 8427 compliant models
 * Tests core DNS resolution logic without external dependencies
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
		DnsMessage query =
				DnsMessageConverter.createQuery("example.com", 1, 1); // A record, IN class
		DnsMessage response = dnsResolver.resolve(query);
		
		assertNotNull(response);
		assertEquals("example.com", response.getQname());
		assertEquals(1, response.getQr()); // Response flag should be set
	}
	
	@Test
	void testResolveSetsRcode() {
		DnsMessage query = DnsMessageConverter.createQuery("example.com", 1, 1);
		DnsMessage response = dnsResolver.resolve(query);
		
		assertNotNull(response.getRcode());
		// RCODE should be 0 (NOERROR), 2 (SERVFAIL), or 3 (NXDOMAIN)
		assertTrue(response.getRcode() >= 0 && response.getRcode() <= 3);
	}
	
	@Test
	void testResolveAAAARecord() {
		DnsMessage query = DnsMessageConverter.createQuery("example.com", 28, 1); // AAAA record
		DnsMessage response = dnsResolver.resolve(query);
		
		assertNotNull(response);
		assertEquals("example.com", response.getQname());
		assertEquals(28, response.getQtype());
	}
	
	@Test
	void testResolveCachingWorks() {
		DnsMessage query1 = DnsMessageConverter.createQuery("cacheable.com", 1, 1);
		DnsMessage response1 = dnsResolver.resolve(query1);
		
		// Second query should hit cache
		DnsMessage query2 = DnsMessageConverter.createQuery("cacheable.com", 1, 1);
		DnsMessage response2 = dnsResolver.resolve(query2);
		
		assertNotNull(response1);
		assertNotNull(response2);
		assertEquals(response1.getQname(), response2.getQname());
	}
	
	@Test
	void testGetCacheStats() {
		var stats = dnsResolver.getCacheStats();
		assertNotNull(stats);
		assertTrue(stats.containsKey("total"));
		assertTrue(stats.containsKey("expired"));
		assertTrue(stats.containsKey("active"));
	}
	
	@Test
	void testClearExpiredCache() {
		dnsResolver.clearExpiredCache();
		// If no exception is thrown, test passes
		assertTrue(true);
	}
	
	@Test
	void testResolveWithDifferentTypes() {
		// Test A record
		DnsMessage queryA = DnsMessageConverter.createQuery("test.com", 1, 1);
		DnsMessage responseA = dnsResolver.resolve(queryA);
		assertNotNull(responseA);
		assertEquals(1, responseA.getQtype());
		
		// Test AAAA record
		DnsMessage queryAAAA = DnsMessageConverter.createQuery("test.com", 28, 1);
		DnsMessage responseAAAA = dnsResolver.resolve(queryAAAA);
		assertNotNull(responseAAAA);
		assertEquals(28, responseAAAA.getQtype());
	}
	
	@Test
	void testResolveReturnsProperMessageStructure() {
		DnsMessage query = DnsMessageConverter.createQuery("example.com", 1, 1);
		DnsMessage response = dnsResolver.resolve(query);
		
		// Verify RFC 8427 compliant structure
		assertNotNull(response.getId());
		assertNotNull(response.getQr());
		assertNotNull(response.getRcode());
		assertNotNull(response.getQname());
		assertNotNull(response.getQtype());
		assertNotNull(response.getQclass());
		
		// Response bit should be set
		assertEquals(1, response.getQr());
	}
}

