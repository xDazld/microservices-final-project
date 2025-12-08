package dev.pacr.dns.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for DNS Query model
 */
class DNSQueryTest {
	
	@Test
	void testCreateDNSQueryWithDefaults() {
		DNSQuery query = new DNSQuery();
		
		assertNotNull(query.getId());
		assertNotNull(query.getTimestamp());
		assertTrue(query.getId().length() > 0);
	}
	
	@Test
	void testCreateDNSQueryWithParameters() {
		String domain = "example.com";
		String queryType = "A";
		String clientIp = "192.168.1.1";
		String protocol = "UDP";
		
		DNSQuery query = new DNSQuery(domain, queryType, clientIp, protocol);
		
		assertEquals(domain, query.getDomain());
		assertEquals(queryType, query.getQueryType());
		assertEquals(clientIp, query.getClientIp());
		assertEquals(protocol, query.getProtocol());
		assertNotNull(query.getId());
		assertNotNull(query.getTimestamp());
	}
	
	@Test
	void testDNSQueryGettersSetters() {
		DNSQuery query = new DNSQuery();
		
		query.setDomain("test.com");
		query.setQueryType("AAAA");
		query.setClientIp("10.0.0.1");
		query.setProtocol("TCP");
		
		assertEquals("test.com", query.getDomain());
		assertEquals("AAAA", query.getQueryType());
		assertEquals("10.0.0.1", query.getClientIp());
		assertEquals("TCP", query.getProtocol());
	}
	
	@Test
	void testDNSQueryIdUniqueness() {
		DNSQuery query1 = new DNSQuery();
		DNSQuery query2 = new DNSQuery();
		
		assertNotEquals(query1.getId(), query2.getId());
	}
	
	@Test
	void testDNSQueryTimestamp() {
		DNSQuery query = new DNSQuery();
		Instant timestamp = query.getTimestamp();
		
		assertNotNull(timestamp);
		assertTrue(timestamp.isBefore(Instant.now().plusSeconds(1)));
		assertTrue(timestamp.isAfter(Instant.now().minusSeconds(1)));
	}
	
	@Test
	void testDNSQueryToString() {
		DNSQuery query = new DNSQuery("example.com", "A", "192.168.1.1", "UDP");
		String toString = query.toString();
		
		assertNotNull(toString);
		assertTrue(toString.contains("example.com"));
		assertTrue(toString.contains("A"));
	}
	
	@Test
	void testSetId() {
		DNSQuery query = new DNSQuery();
		String customId = "custom-id-123";
		
		query.setId(customId);
		assertEquals(customId, query.getId());
	}
	
	@Test
	void testSetTimestamp() {
		DNSQuery query = new DNSQuery();
		Instant customTime = Instant.ofEpochSecond(0);
		
		query.setTimestamp(customTime);
		assertEquals(customTime, query.getTimestamp());
	}
	
	@Test
	void testDNSQueryWithAllFieldsSet() {
		DNSQuery query = new DNSQuery("google.com", "AAAA", "192.168.0.1", "DoH");
		query.setId("query-123");
		query.setTimestamp(Instant.now());
		
		assertEquals("query-123", query.getId());
		assertEquals("google.com", query.getDomain());
		assertEquals("AAAA", query.getQueryType());
		assertEquals("192.168.0.1", query.getClientIp());
		assertEquals("DoH", query.getProtocol());
	}
	
	@Test
	void testDNSQueryNullValues() {
		DNSQuery query = new DNSQuery();
		
		query.setDomain(null);
		query.setQueryType(null);
		query.setClientIp(null);
		query.setProtocol(null);
		
		assertNull(query.getDomain());
		assertNull(query.getQueryType());
		assertNull(query.getClientIp());
		assertNull(query.getProtocol());
	}
	
	@Test
	void testDNSQueryDifferentProtocols() {
		String[] protocols = {"UDP", "TCP", "DoH", "DoT"};
		
		for (String protocol : protocols) {
			DNSQuery query = new DNSQuery("test.com", "A", "192.168.1.1", protocol);
			assertEquals(protocol, query.getProtocol());
		}
	}
	
	@Test
	void testDNSQueryDifferentRecordTypes() {
		String[] recordTypes = {"A", "AAAA", "CNAME", "MX", "TXT", "NS"};
		
		for (String type : recordTypes) {
			DNSQuery query = new DNSQuery("test.com", type, "192.168.1.1", "UDP");
			assertEquals(type, query.getQueryType());
		}
	}
}

