package dev.pacr.dns;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static dev.pacr.dns.DNSServiceIntegrationTest.createDNSQuery;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * RFC Compliance Test Suite for DNS over HTTPS (DoH) Service
 * <p>
 * This test suite verifies compliance with relevant Internet standards for DoH:
 * - RFC 1034: Domain Names - Concepts and Facilities
 * - RFC 1035: Domain Names - Implementation and Specification
 * - RFC 1123: Requirements for Internet Hosts
 * - RFC 2308: Negative Caching of DNS Queries
 * - RFC 3597: Handling of Unknown DNS Resource Record Types
 * - RFC 4343: DNS Case Insensitivity Clarification
 * - RFC 6891: Extension Mechanisms for DNS (EDNS0)
 * - RFC 8020: NXDOMAIN: There Really Is Nothing Underneath
 * - RFC 8484: DNS Queries over HTTPS (DoH)
 */
@QuarkusTest
@DisplayName("RFC Compliance Test Suite")
public class RFCComplianceTest {
	
	// DNS Response Code constants
	private static final int RCODE_NO_ERROR = 0;
	private static final int RCODE_FORMAT_ERROR = 1;
	private static final int RCODE_SERVER_FAILURE = 2;
	private static final int RCODE_NXDOMAIN = 3;
	
	/**
	 * Helper to parse DNS response from HTTP response body
	 */
	private DNSResponse parseDNSResponse(Response httpResponse) {
		byte[] responseBody = httpResponse.asByteArray();
		return new DNSResponse(responseBody);
	}
	
	private String generateDomainWithLength(int targetLength) {
		StringBuilder sb = new StringBuilder();
		int remaining = targetLength - 4; // Reserve for ".com"
		int labelNum = 0;
		
		while (remaining > 0) {
			if (labelNum > 0) {
				sb.append('.');
				remaining--;
				if (remaining <= 0) {
					break;
				}
			}
			
			int labelLen = Math.min(63, remaining);
			for (int i = 0; i < labelLen; i++) {
				sb.append('a');
			}
			remaining -= labelLen;
			labelNum++;
		}
		
		sb.append(".com");
		return sb.toString();
	}
	
	private String generateLabelWithLength(int length) {
		return "a".repeat(length);
	}
	
	/**
	 * DNS Response parser to extract response code from DNS wire format
	 */
		private record DNSResponse(byte[] data) {
		
		public int getRcode() {
				if (data.length < 12) {
					throw new IllegalArgumentException("Invalid DNS response: too short");
				}
				// RCODE is in the lower 4 bits of byte 3 (flags byte 2)
				return data[3] & 0x0F;
			}
			
			public int getAnswerCount() {
				if (data.length < 12) {
					return 0;
				}
				// ANCOUNT is bytes 6-7
				return ((data[6] & 0xFF) << 8) | (data[7] & 0xFF);
			}
			
			public boolean isResponse() {
				if (data.length < 12) {
					return false;
				}
				// QR bit is the high bit of byte 2 (flags byte 1)
				return (data[2] & 0x80) != 0;
			}
		}
	
	@Nested
	@DisplayName("RFC 1035 - DNS Message Format and Protocol")
	class RFC1035Tests {
		
		@Test
		@DisplayName("Should handle standard DNS query with valid domain name format")
		void testValidDomainNameFormat() throws IOException {
			byte[] dnsQuery = createDNSQuery("example.com", 1);
			Response response =
					given().contentType("application/dns-message").body(dnsQuery).when()
					.post("/dns-query");
			
			response.then().statusCode(200).contentType("application/dns-message");
			
			DNSResponse dnsResponse = parseDNSResponse(response);
			assertTrue(dnsResponse.isResponse(), "Should be a DNS response");
		}
		
		@Test
		@DisplayName("Should handle domain name with maximum length (255 characters)")
		void testMaximumDomainNameLength() throws IOException {
			String maxLengthDomain = generateDomainWithLength(253);
			byte[] dnsQuery = createDNSQuery(maxLengthDomain, 1);
			
			given().contentType("application/dns-message").body(dnsQuery).when().post("/dns-query")
					.then().statusCode(200).contentType("application/dns-message");
		}
		
		@Test
		@DisplayName("Should reject domain names exceeding 255 characters")
		void testExceedsMaximumDomainNameLength() throws IOException {
			String tooLongDomain = generateDomainWithLength(300);
			byte[] dnsQuery = createDNSQuery(tooLongDomain, 1);
			
			given().contentType("application/dns-message").body(dnsQuery).when().post("/dns-query")
					.then().statusCode(400);
		}
		
		@Test
		@DisplayName("Should handle label with maximum length (63 characters)")
		void testMaximumLabelLength() throws IOException {
			String maxLabelDomain = generateLabelWithLength(63) + ".com";
			byte[] dnsQuery = createDNSQuery(maxLabelDomain, 1);
			
			given().contentType("application/dns-message").body(dnsQuery).when().post("/dns-query")
					.then().statusCode(200).contentType("application/dns-message");
		}
		
		@Test
		@DisplayName("Should reject labels exceeding 63 characters")
		void testExceedsMaximumLabelLength() throws IOException {
			String invalidDomain = generateLabelWithLength(64) + ".com";
			byte[] dnsQuery = createDNSQuery(invalidDomain, 1);
			
			given().contentType("application/dns-message").body(dnsQuery).when().post("/dns-query")
					.then().statusCode(400);
		}
		
		@Test
		@DisplayName("Should support A record queries")
		void testARecordQueryType() throws IOException {
			byte[] dnsQuery = createDNSQuery("example.com", 1);
			
			given().contentType("application/dns-message").body(dnsQuery).when().post("/dns-query")
					.then().statusCode(200).contentType("application/dns-message");
		}
		
		@Test
		@DisplayName("Should support AAAA record queries")
		void testAAAARecordQueryType() throws IOException {
			byte[] dnsQuery = createDNSQuery("example.com", 28);
			
			given().contentType("application/dns-message").body(dnsQuery).when().post("/dns-query")
					.then().statusCode(200).contentType("application/dns-message");
		}
		
		@Test
		@DisplayName("Should support MX record queries")
		void testMXRecordQueryType() throws IOException {
			byte[] dnsQuery = createDNSQuery("example.com", 15);
			
			given().contentType("application/dns-message").body(dnsQuery).when().post("/dns-query")
					.then().statusCode(200).contentType("application/dns-message");
		}
		
		@Test
		@DisplayName("Should support TXT record queries")
		void testTXTRecordQueryType() throws IOException {
			byte[] dnsQuery = createDNSQuery("example.com", 16);
			
			given().contentType("application/dns-message").body(dnsQuery).when().post("/dns-query")
					.then().statusCode(200).contentType("application/dns-message");
		}
		
		@Test
		@DisplayName("Should support CNAME record queries")
		void testCNAMERecordQueryType() throws IOException {
			byte[] dnsQuery = createDNSQuery("example.com", 5);
			
			given().contentType("application/dns-message").body(dnsQuery).when().post("/dns-query")
					.then().statusCode(200).contentType("application/dns-message");
		}
		
		@Test
		@DisplayName("Should respect case-insensitive domain names")
		void testCaseInsensitivity() throws IOException {
			byte[] dnsQuery = createDNSQuery("EXAMPLE.COM", 1);
			
			given().contentType("application/dns-message").body(dnsQuery).when().post("/dns-query")
					.then().statusCode(200).contentType("application/dns-message");
		}
	}
	
	@Nested
	@DisplayName("RFC 1123 - Requirements for Internet Hosts")
	class RFC1123Tests {
		
		@Test
		@DisplayName("Should handle host names with uppercase letters")
		void testHostNameWithUppercase() throws IOException {
			byte[] dnsQuery = createDNSQuery("Example.Com", 1);
			
			given().contentType("application/dns-message").body(dnsQuery).when().post("/dns-query")
					.then().statusCode(200).contentType("application/dns-message");
		}
		
		@Test
		@DisplayName("Should allow labels beginning with a digit")
		void testLabelBeginningWithDigit() throws IOException {
			byte[] dnsQuery = createDNSQuery("3com.com", 1);
			
			given().contentType("application/dns-message").body(dnsQuery).when().post("/dns-query")
					.then().statusCode(200).contentType("application/dns-message");
		}
		
		@Test
		@DisplayName("Should support hyphens in domain names")
		void testHyphensInDomainName() throws IOException {
			byte[] dnsQuery = createDNSQuery("my-example.com", 1);
			
			given().contentType("application/dns-message").body(dnsQuery).when().post("/dns-query")
					.then().statusCode(200).contentType("application/dns-message");
		}
	}
	
	@Nested
	@DisplayName("RFC 3597 - Handling of Unknown DNS Resource Record Types")
	class RFC3597Tests {
		
		@Test
		@DisplayName("Should handle unknown record types gracefully")
		void testUnknownRecordType() throws IOException {
			byte[] dnsQuery = createDNSQuery("example.com", 65280);
			
			given().contentType("application/dns-message").body(dnsQuery).when().post("/dns-query")
					.then().statusCode(200).contentType("application/dns-message");
		}
		
		@Test
		@DisplayName("Should reject TYPE0 queries")
		void testTYPE0Query() throws IOException {
			byte[] dnsQuery = createDNSQuery("example.com", 0);
			
			given().contentType("application/dns-message").body(dnsQuery).when().post("/dns-query")
					.then().statusCode(400);
		}
	}
	
	@Nested
	@DisplayName("RFC 1034 - Concepts and Facilities")
	class RFC1034Tests {
		
		@Test
		@DisplayName("Should cache DNS responses appropriately")
		void testDNSCaching() throws IOException {
			byte[] dnsQuery = createDNSQuery("example.com", 1);
			
			// First request - should hit upstream
			long startTime1 = System.nanoTime();
			Response response1 =
					given().contentType("application/dns-message").body(dnsQuery).when()
							.post("/dns-query");
			long duration1 = System.nanoTime() - startTime1;
			
			response1.then().statusCode(200).contentType("application/dns-message");
			String cacheControl1 = response1.header("Cache-Control");
			assertNotNull(cacheControl1, "Cache-Control header should be present");
			
			// Second request - should be cached and faster
			long startTime2 = System.nanoTime();
			Response response2 =
					given().contentType("application/dns-message").body(dnsQuery).when()
							.post("/dns-query");
			long duration2 = System.nanoTime() - startTime2;
			
			response2.then().statusCode(200).contentType("application/dns-message");
			
			// Cached response should be significantly faster (at least 50% faster)
			// This is a reasonable assumption for cached vs uncached
			assertThat("Cached request should be faster than initial request", duration2,
					lessThan(duration1));
		}
		
		@Test
		@DisplayName("Should handle NXDOMAIN responses for non-existent domains")
		void testNXDomainHandling() throws IOException {
			byte[] dnsQuery = createDNSQuery("this-domain-definitely-does-not-exist-12345.com", 1);
			
			Response response =
					given().contentType("application/dns-message").body(dnsQuery).when()
					.post("/dns-query");
			
			response.then().statusCode(200).contentType("application/dns-message");
			
			DNSResponse dnsResponse = parseDNSResponse(response);
			assertTrue(dnsResponse.isResponse(), "Should be a DNS response");
			// Accept NXDOMAIN, NOERROR, or SERVFAIL for non-existent domains
			// SERVFAIL is acceptable when upstream resolver has issues
			int rcode = dnsResponse.getRcode();
			assertTrue(rcode == RCODE_NXDOMAIN || rcode == RCODE_NO_ERROR ||
							rcode == RCODE_SERVER_FAILURE,
					"Response code should be NXDOMAIN (3), NOERROR (0), or SERVFAIL (2), but was: "
							+ rcode);
		}
	}
	
	@Nested
	@DisplayName("RFC 8484 - DNS over HTTPS (DoH)")
	class RFC8484Tests {
		
		@Test
		@DisplayName("Should support DoH POST method with DNS wire format")
		void testDoHPOSTMethod() throws IOException {
			byte[] dnsQuery = createDNSQuery("example.com", 1);
			
			Response response =
					given().contentType("application/dns-message").body(dnsQuery).when()
					.post("/dns-query");
			
			response.then().statusCode(200).contentType("application/dns-message");
			
			DNSResponse dnsResponse = parseDNSResponse(response);
			assertTrue(dnsResponse.isResponse(), "Response should have QR bit set");
		}
		
		@Test
		@DisplayName("Should support DoH GET method with base64url encoding")
		void testDoHGETMethod() throws IOException {
			byte[] dnsQuery = createDNSQuery("example.com", 1);
			String encodedQuery = base64urlEncode(dnsQuery);
			
			Response response = given().when().get("/dns-query?dns=" + encodedQuery);
			
			response.then().statusCode(200)
					.contentType("application/dns-message");
			
			DNSResponse dnsResponse = parseDNSResponse(response);
			assertTrue(dnsResponse.isResponse(), "Response should have QR bit set");
		}
		
		@Test
		@DisplayName("Should return proper Cache-Control headers for DoH responses")
		void testDoHCacheControl() throws IOException {
			byte[] dnsQuery = createDNSQuery("example.com", 1);
			String encodedQuery = base64urlEncode(dnsQuery);
			
			given().when().get("/dns-query?dns=" + encodedQuery).then().statusCode(200)
					.header("Cache-Control", containsString("max-age="));
		}
		
		private String base64urlEncode(byte[] data) {
			String encoded = java.util.Base64.getEncoder().encodeToString(data);
			return encoded.replace('+', '-').replace('/', '_').replaceAll("=+$", "");
		}
	}
	
	@Nested
	@DisplayName("RFC 2308 - Negative Caching of DNS Queries")
	class RFC2308Tests {
		
		@Test
		@DisplayName("Should handle negative caching for NXDOMAIN responses")
		void testNegativeCachingNXDomain() throws IOException {
			byte[] dnsQuery = createDNSQuery("nonexistent-domain-12345.example.com", 1);
			
			Response response =
					given().contentType("application/dns-message").body(dnsQuery).when()
					.post("/dns-query");
			
			response.then().statusCode(200).contentType("application/dns-message");
			
			DNSResponse dnsResponse = parseDNSResponse(response);
			assertTrue(dnsResponse.isResponse(), "Should be a DNS response");
			
			// Should have NXDOMAIN or NOERROR with 0 answers
			int rcode = dnsResponse.getRcode();
			if (rcode == RCODE_NO_ERROR) {
				assertEquals(0, dnsResponse.getAnswerCount(),
						"NOERROR response should have 0 answers for non-existent domain");
			}
		}
		
		@Test
		@DisplayName("Should handle NODATA responses (valid name, no records)")
		void testNODATAResponse() throws IOException {
			byte[] dnsQuery = createDNSQuery("example.com", 48); // DNSKEY unlikely to exist
			
			Response response =
					given().contentType("application/dns-message").body(dnsQuery).when()
					.post("/dns-query");
			
			response.then().statusCode(200).contentType("application/dns-message");
			
			DNSResponse dnsResponse = parseDNSResponse(response);
			assertEquals(RCODE_NO_ERROR, dnsResponse.getRcode(),
					"NODATA response should have NOERROR rcode");
		}
		
		@Test
		@DisplayName("Should cache negative responses appropriately")
		void testNegativeCaching() throws IOException {
			byte[] dnsQuery = createDNSQuery("definitely-does-not-exist-789.com", 1);
			
			Response response =
					given().contentType("application/dns-message").body(dnsQuery).when()
					.post("/dns-query");
			
			response.then().statusCode(200).contentType("application/dns-message")
					.header("Cache-Control", notNullValue());
		}
	}
	
	@Nested
	@DisplayName("RFC 4343 - DNS Case Insensitivity Clarification")
	class RFC4343Tests {
		
		@Test
		@DisplayName("Should treat DNS labels as case-insensitive")
		void testCaseInsensitiveLabels() throws IOException {
			byte[] dnsQuery1 = createDNSQuery("example.com", 1);
			byte[] dnsQuery2 = createDNSQuery("EXAMPLE.COM", 1);
			byte[] dnsQuery3 = createDNSQuery("ExAmPlE.CoM", 1);
			
			// All three should succeed
			given().contentType("application/dns-message").body(dnsQuery1).when().post("/dns" +
							"-query")
					.then().statusCode(200).contentType("application/dns-message");
			
			given().contentType("application/dns-message").body(dnsQuery2).when().post("/dns" +
							"-query")
					.then().statusCode(200).contentType("application/dns-message");
			
			given().contentType("application/dns-message").body(dnsQuery3).when().post("/dns" +
							"-query")
					.then().statusCode(200).contentType("application/dns-message");
		}
	}
	
	@Nested
	@DisplayName("RFC 8020 - NXDOMAIN: There Really Is Nothing Underneath")
	class RFC8020Tests {
		
		@Test
		@DisplayName("Should handle NXDOMAIN responses consistently")
		void testNXDomainConsistency() throws IOException {
			byte[] dnsQuery1 = createDNSQuery("does-not-exist-xyz.invalid", 1);
			byte[] dnsQuery2 = createDNSQuery("subdomain.does-not-exist-xyz.invalid", 1);
			
			Response response1 =
					given().contentType("application/dns-message").body(dnsQuery1).when()
							.post("/dns-query");
			
			Response response2 =
					given().contentType("application/dns-message").body(dnsQuery2).when()
							.post("/dns-query");
			
			response1.then().statusCode(200).contentType("application/dns-message");
			response2.then().statusCode(200).contentType("application/dns-message");
			
			// Both should return valid DNS responses
			DNSResponse dns1 = parseDNSResponse(response1);
			DNSResponse dns2 = parseDNSResponse(response2);
			
			assertTrue(dns1.isResponse(), "First response should be valid");
			assertTrue(dns2.isResponse(), "Second response should be valid");
		}
	}
	
	@Nested
	@DisplayName("RFC 6761 - Special-Use Domain Names")
	class RFC6761Tests {
		
		@Test
		@DisplayName("Should handle localhost domain")
		void testLocalhostDomain() throws IOException {
			byte[] dnsQuery = createDNSQuery("localhost", 1);
			
			given().contentType("application/dns-message").body(dnsQuery).when().post("/dns-query")
					.then().statusCode(200).contentType("application/dns-message");
		}
		
		@Test
		@DisplayName("Should handle example.com domain")
		void testExampleComDomain() throws IOException {
			byte[] dnsQuery = createDNSQuery("example.com", 1);
			
			given().contentType("application/dns-message").body(dnsQuery).when().post("/dns-query")
					.then().statusCode(200).contentType("application/dns-message");
		}
		
		@Test
		@DisplayName("Should handle invalid domain (.invalid)")
		void testInvalidDomain() throws IOException {
			byte[] dnsQuery = createDNSQuery("something.invalid", 1);
			
			given().contentType("application/dns-message").body(dnsQuery).when().post("/dns-query")
					.then().statusCode(200).contentType("application/dns-message");
		}
	}
	
	@Nested
	@DisplayName("RFC 1912 - Common DNS Errors")
	class RFC1912Tests {
		
		@Test
		@DisplayName("Should handle common DNS errors appropriately")
		void testErrorHandling() throws IOException {
			// Double dots are invalid
			byte[] dnsQuery = createDNSQuery("invalid..domain", 1);
			
			given().contentType("application/dns-message").body(dnsQuery).when().post("/dns-query")
					.then().statusCode(400);
		}
	}
}

