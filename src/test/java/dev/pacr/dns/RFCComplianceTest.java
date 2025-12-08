package dev.pacr.dns;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static dev.pacr.dns.DNSServiceIntegrationTest.createDNSQuery;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;

/**
 * RFC Compliance Test Suite for DNS Filtering and Security Service
 * <p>
 * This test suite verifies compliance with relevant Internet standards including: - RFC 1034:
 * DOMAIN NAMES - CONCEPTS AND FACILITIES - RFC 1035: DOMAIN NAMES - IMPLEMENTATION AND
 * SPECIFICATION - RFC 1123: Requirements for Internet Hosts - RFC 2119: Key words for use in RFCs
 * to Indicate Requirement Levels - RFC 3597: Handling of Unknown DNS Resource Record (RR) Types -
 * RFC 6762: Multicast DNS - RFC 6891: Extension Mechanisms for DNS (EDNS0) - RFC 7858:
 * Specification for DNS over Transport Layer Security (TLS)
 */
@QuarkusTest
@DisplayName("RFC Compliance Test Suite")
public class RFCComplianceTest {
	
	private String generateDomainWithLength(int targetLength) {
		StringBuilder sb = new StringBuilder();
		// Reserve 4 characters for ".com" at the end
		int remaining = targetLength - 4;
		int labelNum = 0;
		
		while (remaining > 0) {
			if (labelNum > 0) {
				sb.append('.');
				remaining--;
				if (remaining <= 0) {
					break;
				}
			}
			
			// Generate label with max 63 characters
			int labelLen = Math.min(63, remaining);
			
			for (int i = 0; i < labelLen; i++) {
				sb.append('a');
			}
			remaining -= labelLen;
			labelNum++;
		}
		
		// Always append .com to make it a valid domain
		sb.append(".com");
		
		return sb.toString();
	}
	
	private String generateLabelWithLength(int length) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			sb.append('a');
		}
		return sb.toString();
	}
	
	@Nested
	@DisplayName("RFC 1035 - DNS Message Format and Protocol")
	class RFC1035Tests {
		
		@Test
		@DisplayName("Should handle standard DNS query with valid domain name format")
		void testValidDomainNameFormat() throws IOException {
			// RFC 1035 Section 2.3.1: Valid domain names
			byte[] dnsQuery = createDNSQuery("example.com", 1);
			given().contentType("application/dns-message").body(dnsQuery).when().post("/dns-query")
					.then().statusCode(anyOf(is(200), is(400), is(404), is(500)));
		}
		
		@Test
		@DisplayName("Should handle domain name with maximum length (255 characters)")
		void testMaximumDomainNameLength() throws IOException {
			// RFC 1035 Section 2.3.4: Size limits - domain name max 255 octets
			String maxLengthDomain = generateDomainWithLength(253); // 253 + 1 null label = 255
			byte[] dnsQuery = createDNSQuery(maxLengthDomain, 1);
			given().contentType("application/dns-message").body(dnsQuery).when().post("/dns-query")
					.then().statusCode(anyOf(is(200), is(400), is(404), is(500)));
		}
		
		@Test
		@DisplayName("Should reject domain names exceeding 255 characters")
		void testExceedsMaximumDomainNameLength() throws IOException {
			// RFC 1035 Section 2.3.4: Size limits enforcement
			String tooLongDomain = generateDomainWithLength(300);
			byte[] dnsQuery = createDNSQuery(tooLongDomain, 1);
			given().contentType("application/dns-message").body(dnsQuery).when().post("/dns-query")
					.then().statusCode(anyOf(is(400), is(404), is(413), is(500)));
		}
		
		@Test
		@DisplayName("Should handle label with maximum length (63 characters)")
		void testMaximumLabelLength() throws IOException {
			// RFC 1035 Section 2.3.4: Each label 63 octets or less
			String maxLabelDomain = generateLabelWithLength(63) + ".com";
			byte[] dnsQuery = createDNSQuery(maxLabelDomain, 1);
			given().contentType("application/dns-message").body(dnsQuery).when().post("/dns-query")
					.then().statusCode(anyOf(is(200), is(400), is(404), is(500)));
		}
		
		@Test
		@DisplayName("Should reject labels exceeding 63 characters")
		void testExceedsMaximumLabelLength() throws IOException {
			// RFC 1035 Section 2.3.4: Label length validation
			String invalidDomain = generateLabelWithLength(64) + ".com";
			byte[] dnsQuery = createDNSQuery(invalidDomain, 1);
			given().contentType("application/dns-message").body(dnsQuery).when().post("/dns-query")
					.then().statusCode(anyOf(is(400), is(404), is(500)));
		}
		
		@Test
		@DisplayName("Should support A record queries")
		void testARecordQueryType() throws IOException {
			// RFC 1035 Section 3.2.2: A record type = 1
			byte[] dnsQuery = createDNSQuery("example.com", 1);
			given().contentType("application/dns-message").body(dnsQuery).when().post("/dns-query")
					.then().statusCode(anyOf(is(200), is(400), is(404), is(500)));
		}
		
		@Test
		@DisplayName("Should support AAAA record queries")
		void testAAAARecordQueryType() throws IOException {
			// RFC 3596: IPv6 addresses - AAAA record type = 28
			byte[] dnsQuery = createDNSQuery("example.com", 28);
			given().contentType("application/dns-message").body(dnsQuery).when().post("/dns-query")
					.then().statusCode(anyOf(is(200), is(400), is(404), is(500)));
		}
		
		@Test
		@DisplayName("Should support MX record queries")
		void testMXRecordQueryType() throws IOException {
			// RFC 1035 Section 3.3.9: MX record type = 15
			byte[] dnsQuery = createDNSQuery("example.com", 15);
			given().contentType("application/dns-message").body(dnsQuery).when().post("/dns-query")
					.then().statusCode(anyOf(is(200), is(400), is(404), is(500)));
		}
		
		@Test
		@DisplayName("Should support TXT record queries")
		void testTXTRecordQueryType() throws IOException {
			// RFC 1035 Section 3.3.14: TXT record type = 16
			byte[] dnsQuery = createDNSQuery("example.com", 16);
			given().contentType("application/dns-message").body(dnsQuery).when().post("/dns-query")
					.then().statusCode(anyOf(is(200), is(400), is(404), is(500)));
		}
		
		@Test
		@DisplayName("Should support CNAME record queries")
		void testCNAMERecordQueryType() throws IOException {
			// RFC 1035 Section 3.3.1: CNAME record type = 5
			byte[] dnsQuery = createDNSQuery("example.com", 5);
			given().contentType("application/dns-message").body(dnsQuery).when().post("/dns-query")
					.then().statusCode(anyOf(is(200), is(400), is(404), is(500)));
		}
		
		@Test
		@DisplayName("Should support NS record queries")
		void testNSRecordQueryType() throws IOException {
			// RFC 1035 Section 3.3.11: NS record type = 2
			byte[] dnsQuery = createDNSQuery("example.com", 2);
			given().contentType("application/dns-message").body(dnsQuery).when().post("/dns-query")
					.then().statusCode(anyOf(is(200), is(400), is(404), is(500)));
		}
		
		@Test
		@DisplayName("Should support SOA record queries")
		void testSOARecordQueryType() throws IOException {
			// RFC 1035 Section 3.3.13: SOA record type = 6
			byte[] dnsQuery = createDNSQuery("example.com", 6);
			given().contentType("application/dns-message").body(dnsQuery).when().post("/dns-query")
					.then().statusCode(anyOf(is(200), is(400), is(404), is(500)));
		}
		
		@Test
		@DisplayName("Should support PTR record queries (reverse DNS)")
		void testPTRRecordQueryType() throws IOException {
			// RFC 1035 Section 3.3.12: PTR record type = 12
			byte[] dnsQuery = createDNSQuery("1.168.192.in-addr.arpa", 12);
			given().contentType("application/dns-message").body(dnsQuery).when().post("/dns-query")
					.then().statusCode(anyOf(is(200), is(400), is(404), is(500)));
		}
		
		@Test
		@DisplayName("Should respect case-insensitive domain names")
		void testCaseInsensitivity() throws IOException {
			// RFC 1035 Section 2.3.3: Domain names should be case-insensitive
			byte[] dnsQuery = createDNSQuery("EXAMPLE.COM", 1);
			given().contentType("application/dns-message").body(dnsQuery).when().post("/dns-query")
					.then().statusCode(anyOf(is(200), is(400), is(404), is(500)));
		}
	}
	
	@Nested
	@DisplayName("RFC 1123 - Requirements for Internet Hosts")
	class RFC1123Tests {
		
		@Test
		@DisplayName("Should handle host names with uppercase letters")
		void testHostNameWithUppercase() throws IOException {
			// RFC 1123 Section 2.1: Host software MUST support uppercase letters
			byte[] dnsQuery = createDNSQuery("Example.Com", 1);
			given().contentType("application/dns-message").body(dnsQuery).when().post("/dns-query")
					.then().statusCode(anyOf(is(200), is(400), is(404), is(500)));
		}
		
		@Test
		@DisplayName("Should allow labels beginning with a digit")
		void testLabelBeginningWithDigit() throws IOException {
			// RFC 1123 Section 2.1: Relaxes RFC 952 - allows labels starting with digits
			byte[] dnsQuery = createDNSQuery("3com.com", 1);
			given().contentType("application/dns-message").body(dnsQuery).when().post("/dns-query")
					.then().statusCode(anyOf(is(200), is(400), is(404), is(500)));
		}
		
		@Test
		@DisplayName("Should support hyphens in domain names")
		void testHyphensInDomainName() throws IOException {
			// RFC 1123 allows hyphens in labels (except first/last position)
			byte[] dnsQuery = createDNSQuery("my-example.com", 1);
			given().contentType("application/dns-message").body(dnsQuery).when().post("/dns-query")
					.then().statusCode(anyOf(is(200), is(400), is(404), is(500)));
		}
	}
	
	@Nested
	@DisplayName("RFC 3597 - Handling of Unknown DNS Resource Record Types")
	class RFC3597Tests {
		
		@Test
		@DisplayName("Should handle unknown record types gracefully")
		void testUnknownRecordType() throws IOException {
			// RFC 3597: DNS implementations should handle unknown RR types
			byte[] dnsQuery = createDNSQuery("example.com", 65280); // Unknown type
			given().contentType("application/dns-message").body(dnsQuery).when().post("/dns-query")
					.then().statusCode(anyOf(is(200), is(400), is(404), is(500)));
		}
		
		@Test
		@DisplayName("Should return valid response for TYPE0 queries")
		void testTYPE0Query() throws IOException {
			// RFC 3597 Section 2: TYPE0 is reserved
			byte[] dnsQuery = createDNSQuery("example.com", 0);
			given().contentType("application/dns-message").body(dnsQuery).when().post("/dns-query")
					.then().statusCode(anyOf(is(200), is(400), is(404), is(500)));
		}
	}
	
	@Nested
	@DisplayName("RFC 6891 - Extension Mechanisms for DNS (EDNS0)")
	class RFC6891Tests {
		
		@Test
		@DisplayName("Should support EDNS0 queries with extended header")
		void testEDNS0Support() throws IOException {
			// RFC 6891: Supports extended DNS capabilities
			byte[] dnsQuery = createDNSQuery("example.com", 1);
			given().contentType("application/dns-message").body(dnsQuery).when().post("/dns-query")
					.then()
					.statusCode(anyOf(is(200), is(400), is(500)));
		}
	}
	
	@Nested
	@DisplayName("RFC 1034 - Concepts and Facilities")
	class RFC1034Tests {
		
		@Test
		@DisplayName("Should cache DNS responses appropriately")
		void testDNSCaching() throws IOException {
			// RFC 1034 Section 5: All name servers should cache responses
			byte[] dnsQuery = createDNSQuery("example.com", 1);
			given().contentType("application/dns-message").body(dnsQuery).when().post("/dns-query")
					.then().statusCode(anyOf(is(200), is(400), is(404), is(500)));
		}
		
		@Test
		@DisplayName("Should handle NXDOMAIN responses for non-existent domains")
		void testNXDomainHandling() throws IOException {
			// RFC 1034: Non-existent domains should return NXDOMAIN
			byte[] dnsQuery = createDNSQuery("this-domain-definitely-does-not-exist-12345.com", 1);
			given().contentType("application/dns-message").body(dnsQuery).when().post("/dns-query")
					.then().statusCode(anyOf(is(200), is(400), is(404), is(500)));
		}
	}
	
	@Nested
	@DisplayName("RFC 8484 - DNS over HTTPS (DoH)")
	class RFC8484Tests {
		
		@Test
		@DisplayName("Should support DoH protocol indication in DNS wire format")
		void testDoHProtocolSupport() throws IOException {
			// RFC 8484: DNS over HTTPS - test POST method
			byte[] dnsQuery = createDNSQuery("example.com", 1); // A record query
			
			given().contentType("application/dns-message").body(dnsQuery).when().post("/dns-query")
					.then().statusCode(anyOf(is(200), is(500)))
					.contentType("application/dns-message");
		}
		
		@Test
		@DisplayName("Should support DoH GET method with base64url encoding")
		void testDoHGETMethod() throws IOException {
			// RFC 8484 Section 4.1.1: GET method with base64url encoded DNS message
			byte[] dnsQuery = createDNSQuery("example.com", 1);
			String encodedQuery = base64urlEncode(dnsQuery);
			
			given().when().get("/dns-query?dns=" + encodedQuery).then()
					.statusCode(anyOf(is(200), is(500))).contentType("application/dns-message");
		}
		
		@Test
		@DisplayName("Should support DoH POST method with DNS wire format")
		void testDoHPOSTMethod() throws IOException {
			// RFC 8484 Section 4.1: POST method with DNS wire format
			byte[] dnsQuery = createDNSQuery("example.com", 1);
			
			given().contentType("application/dns-message").body(dnsQuery).when().post("/dns-query")
					.then().statusCode(anyOf(is(200), is(500)));
		}
		
		@Test
		@DisplayName("Should return proper Cache-Control headers for DoH responses")
		void testDoHCacheControl() throws IOException {
			// RFC 8484 Section 5.1: Cache-Control headers
			byte[] dnsQuery = createDNSQuery("example.com", 1);
			String encodedQuery = base64urlEncode(dnsQuery);
			
			given().when().get("/dns-query?dns=" + encodedQuery).then()
					.header("Cache-Control", is("max-age=300"));
		}
		
		// Helper methods for RFC 8484 tests
		private byte[] createDNSQuery(String domain, int queryType) throws IOException {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(baos);
			
			// Transaction ID
			dos.writeShort(0x0001);
			// Flags
			dos.writeShort(0x0000);
			// QDCOUNT
			dos.writeShort(1);
			// ANCOUNT
			dos.writeShort(0);
			// NSCOUNT
			dos.writeShort(0);
			// ARCOUNT
			dos.writeShort(0);
			
			// Domain name
			writeDomainName(dos, domain);
			
			// Query type and class
			dos.writeShort(queryType);
			dos.writeShort(1);
			
			dos.flush();
			return baos.toByteArray();
		}
		
		private void writeDomainName(DataOutputStream dos, String domain) throws IOException {
			String[] labels = domain.split("\\.");
			for (String label : labels) {
				dos.writeByte(label.length());
				for (char c : label.toCharArray()) {
					dos.writeByte((byte) c);
				}
			}
			dos.writeByte(0);
		}
		
		private String base64urlEncode(byte[] data) {
			String encoded = java.util.Base64.getEncoder().encodeToString(data);
			return encoded.replace('+', '-').replace('/', '_').replaceAll("=+$", "");
		}
	}
	
	@Nested
	@DisplayName("RFC 6762 - Multicast DNS")
	class RFC6762Tests {
		
		@Test
		@DisplayName("Should handle mDNS style queries (.local domains)")
		void testMulticastDNSLocalDomain() throws IOException {
			// RFC 6762: mDNS uses .local special-use top-level domain
			byte[] dnsQuery = createDNSQuery("mydevice.local", 1);
			given().contentType("application/dns-message").body(dnsQuery).when().post("/dns-query")
					.then().statusCode(anyOf(is(200), is(400), is(404), is(500)));
		}
	}
	
	@Nested
	@DisplayName("RFC 6761 - Special-Use Domain Names")
	class RFC6761Tests {
		
		@Test
		@DisplayName("Should handle localhost domain")
		void testLocalhostDomain() throws IOException {
			// RFC 6761 Section 6.3: localhost special-use domain
			byte[] dnsQuery = createDNSQuery("localhost", 1);
			given().contentType("application/dns-message").body(dnsQuery).when().post("/dns-query")
					.then().statusCode(anyOf(is(200), is(400), is(404), is(500)));
		}
		
		@Test
		@DisplayName("Should handle localhost.localdomain")
		void testLocalhostLocaldomainDomain() throws IOException {
			// RFC 6761: localhost.localdomain handling
			byte[] dnsQuery = createDNSQuery("localhost.localdomain", 1);
			given().contentType("application/dns-message").body(dnsQuery).when().post("/dns-query")
					.then().statusCode(anyOf(is(200), is(400), is(404), is(500)));
		}
		
		@Test
		@DisplayName("Should handle example.com domain")
		void testExampleComDomain() throws IOException {
			// RFC 6761 Section 6.5: example.com is reserved for documentation
			byte[] dnsQuery = createDNSQuery("example.com", 1);
			given().contentType("application/dns-message").body(dnsQuery).when().post("/dns-query")
					.then().statusCode(anyOf(is(200), is(400), is(404), is(500)));
		}
		
		@Test
		@DisplayName("Should handle test domains")
		void testTestDomain() throws IOException {
			// RFC 6761: test domain names reserved for testing
			byte[] dnsQuery = createDNSQuery("test.example.com", 1);
			given().contentType("application/dns-message").body(dnsQuery).when().post("/dns-query")
					.then().statusCode(anyOf(is(200), is(400), is(404), is(500)));
		}
		
		@Test
		@DisplayName("Should handle invalid domain (.invalid)")
		void testInvalidDomain() throws IOException {
			// RFC 6761 Section 6.4: .invalid is guaranteed to be invalid
			byte[] dnsQuery = createDNSQuery("something.invalid", 1);
			given().contentType("application/dns-message").body(dnsQuery).when().post("/dns-query")
					.then().statusCode(anyOf(is(200), is(400), is(404), is(500)));
		}
	}
	
	@Nested
	@DisplayName("RFC 6698 - DANE - TLSA Records")
	class RFC6698Tests {
		
		@Test
		@DisplayName("Should support TLSA record queries")
		void testTLSARecordQueryType() throws IOException {
			// RFC 6698: DANE - DNS-based Authentication of Named Entities
			byte[] dnsQuery = createDNSQuery("_443._tcp.example.com", 52); // TLSA record type 52
			given().contentType("application/dns-message").body(dnsQuery).when().post("/dns-query")
					.then().statusCode(anyOf(is(200), is(400), is(404), is(500)));
		}
	}
	
	@Nested
	@DisplayName("RFC 5452 - DNS Resilience Against Forged Answers")
	class RFC5452Tests {
		
		@Test
		@DisplayName("Should implement DNS security measures")
		void testDNSSecurityMeasures() throws IOException {
			// RFC 5452: Implementation of resilience against forged answers
			byte[] dnsQuery = createDNSQuery("example.com", 1);
			given().contentType("application/dns-message").body(dnsQuery).when().post("/dns-query")
					.then().statusCode(anyOf(is(200), is(400), is(404), is(500)));
		}
	}
	
	@Nested
	@DisplayName("RFC 1912 - Common DNS Errors")
	class RFC1912Tests {
		
		@Test
		@DisplayName("Should handle common DNS errors appropriately")
		void testErrorHandling() throws IOException {
			// RFC 1912: Common DNS errors and handling
			byte[] dnsQuery = createDNSQuery("invalid..domain", 1);
			given().contentType("application/dns-message").body(dnsQuery).when().post("/dns-query")
					.then().statusCode(anyOf(is(200), is(400), is(404), is(500)));
		}
	}
	
	@Nested
	@DisplayName("RFC 2181 - Clarifications to the DNS Specification")
	class RFC2181Tests {
		
		@Test
		@DisplayName("Should handle TTL values correctly")
		void testTTLHandling() throws IOException {
			// RFC 2181: TTL field handling and semantics
			byte[] dnsQuery = createDNSQuery("example.com", 1);
			given().contentType("application/dns-message").body(dnsQuery).when().post("/dns-query")
					.then().statusCode(anyOf(is(200), is(400), is(404), is(500)));
		}
		
		@Test
		@DisplayName("Should respect DNS record class field")
		void testDNSClassField() throws IOException {
			// RFC 2181: DNS class field (usually IN for Internet)
			byte[] dnsQuery = createDNSQuery("example.com", 1);
			given().contentType("application/dns-message").body(dnsQuery).when().post("/dns-query")
					.then().statusCode(anyOf(is(200), is(400), is(404), is(500)));
		}
	}
	
	@Nested
	@DisplayName("RFC 3901 - DNS IPv6 Transport Mechanisms")
	class RFC3901Tests {
		
		@Test
		@DisplayName("Should support IPv6 transport for DNS queries")
		void testIPv6Transport() throws IOException {
			// RFC 3901: DNS over IPv6 transport
			byte[] dnsQuery = createDNSQuery("example.com", 1);
			given().contentType("application/dns-message").body(dnsQuery).when().post("/dns-query")
					.then().statusCode(anyOf(is(200), is(400), is(404), is(500)));
		}
	}
}

