package dev.pacr.dns.model;

import dev.pacr.dns.model.rfc8427.DnsMessage;
import dev.pacr.dns.model.rfc8427.DnsMessageConverter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Unit tests for RFC 8427 compliant DNS Message model
 */
class DnsMessageTest {
	
	@Test
	void testCreateDnsMessageQuery() {
		DnsMessage message = DnsMessageConverter.createQuery("example.com", 1, 1);
		
		assertNotNull(message);
		assertNotNull(message.getId());
		assertEquals("example.com", message.getQname());
		assertEquals(1, message.getQtype()); // A record
		assertEquals(1, message.getQclass()); // IN class
		assertEquals(0, message.getQr()); // Query
		assertEquals(1, message.getQdcount());
	}
	
	@Test
	void testCreateDnsMessageResponse() {
		DnsMessage message = DnsMessageConverter.createResponse("example.com", 1, 1, 0,
				java.util.List.of("93.184.216.34"), 300L);
		
		assertNotNull(message);
		assertEquals("example.com", message.getQname());
		assertEquals(1, message.getQr()); // Response
		assertEquals(0, message.getRcode()); // NOERROR
		assertNotNull(message.getAnswerRRs());
		assertEquals(1, message.getAnswerRRs().size());
		assertEquals(1, message.getAncount());
	}
	
	@Test
	void testDnsMessageGettersSetters() {
		DnsMessage message = new DnsMessage();
		
		message.setId(12345);
		message.setQname("test.com");
		message.setQtype(28); // AAAA
		message.setQclass(1); // IN
		message.setQr(0); // Query
		message.setOpcode(0); // Standard query
		message.setRd(1); // Recursion desired
		
		assertEquals(12345, message.getId());
		assertEquals("test.com", message.getQname());
		assertEquals(28, message.getQtype());
		assertEquals(1, message.getQclass());
		assertEquals(0, message.getQr());
		assertEquals(0, message.getOpcode());
		assertEquals(1, message.getRd());
	}
	
	@Test
	void testDnsMessageNullValues() {
		DnsMessage message = new DnsMessage();
		
		message.setQname(null);
		message.setQtype(null);
		message.setQclass(null);
		
		assertNull(message.getQname());
		assertNull(message.getQtype());
		assertNull(message.getQclass());
	}
	
	@Test
	void testDnsMessageConverterTypeConversion() {
		assertEquals(1, DnsMessageConverter.getQtypeCode("A"));
		assertEquals(28, DnsMessageConverter.getQtypeCode("AAAA"));
		assertEquals(5, DnsMessageConverter.getQtypeCode("CNAME"));
		assertEquals(15, DnsMessageConverter.getQtypeCode("MX"));
		assertEquals(16, DnsMessageConverter.getQtypeCode("TXT"));
		assertEquals(2, DnsMessageConverter.getQtypeCode("NS"));
		
		assertEquals("A", DnsMessageConverter.getQtypeString(1));
		assertEquals("AAAA", DnsMessageConverter.getQtypeString(28));
		assertEquals("CNAME", DnsMessageConverter.getQtypeString(5));
		assertEquals("MX", DnsMessageConverter.getQtypeString(15));
		assertEquals("TXT", DnsMessageConverter.getQtypeString(16));
		assertEquals("NS", DnsMessageConverter.getQtypeString(2));
	}
	
	@Test
	void testDnsMessageDifferentRecordTypes() {
		String[] recordTypes = {"A", "AAAA", "CNAME", "MX", "TXT", "NS"};
		
		for (String type : recordTypes) {
			int qtypeCode = DnsMessageConverter.getQtypeCode(type);
			DnsMessage message = DnsMessageConverter.createQuery("test.com", qtypeCode, 1);
			assertEquals(qtypeCode, message.getQtype());
			assertEquals("test.com", message.getQname());
		}
	}
	
	@Test
	void testDnsMessageRcodes() {
		// Test different response codes
		DnsMessage noerror = DnsMessageConverter.createResponse("example.com", 1, 1, 0,
				java.util.List.of("1.2.3.4"), 300L);
		assertEquals(0, noerror.getRcode()); // NOERROR
		
		DnsMessage nxdomain =
				DnsMessageConverter.createResponse("nonexistent.com", 1, 1, 3, java.util.List.of(),
						300L);
		assertEquals(3, nxdomain.getRcode()); // NXDOMAIN
	}
	
	@Test
	void testDnsMessageFlags() {
		DnsMessage message = new DnsMessage();
		
		// Test all DNS flags
		message.setQr(1); // Response
		message.setAa(1); // Authoritative Answer
		message.setTc(0); // Not Truncated
		message.setRd(1); // Recursion Desired
		message.setRa(1); // Recursion Available
		message.setAd(0); // Not Authenticated
		message.setCd(0); // Checking Disabled
		
		assertEquals(1, message.getQr());
		assertEquals(1, message.getAa());
		assertEquals(0, message.getTc());
		assertEquals(1, message.getRd());
		assertEquals(1, message.getRa());
		assertEquals(0, message.getAd());
		assertEquals(0, message.getCd());
	}
	
	@Test
	void testDnsMessageSectionCounts() {
		DnsMessage message = new DnsMessage();
		
		message.setQdcount(1);
		message.setAncount(2);
		message.setNscount(3);
		message.setArcount(4);
		
		assertEquals(1, message.getQdcount());
		assertEquals(2, message.getAncount());
		assertEquals(3, message.getNscount());
		assertEquals(4, message.getArcount());
	}
	
	@Test
	void testRfc8427FieldNaming() {
		// Verify RFC 8427 compliant field naming (uppercase where specified)
		DnsMessage message = new DnsMessage();
		
		// These methods use RFC 8427 naming convention
		message.setId(1);
		message.setQname("example.com");
		message.setQtype(1);
		message.setQclass(1);
		
		// Verify the fields are accessible
		assertNotNull(message.getId());
		assertNotNull(message.getQname());
		assertNotNull(message.getQtype());
		assertNotNull(message.getQclass());
	}
}

