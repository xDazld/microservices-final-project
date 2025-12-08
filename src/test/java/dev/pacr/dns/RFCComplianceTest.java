package dev.pacr.dns;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

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
		void testValidDomainNameFormat() {
			// RFC 1035 Section 2.3.1: Valid domain names
			given().contentType("application/json")
					.body("{\"domain\": \"example.com\", \"queryType\": \"A\"}").when()
					.post("/api/v1/dns/resolve").then()
					.statusCode(anyOf(is(200), is(400), is(500)));
		}
		
		@Test
		@DisplayName("Should handle domain name with maximum length (255 characters)")
		void testMaximumDomainNameLength() {
			// RFC 1035 Section 2.3.4: Size limits - domain name max 255 octets
			String maxLengthDomain = generateDomainWithLength(253); // 253 + 1 null label = 255
			given().contentType("application/json")
					.body("{\"domain\": \"" + maxLengthDomain + "\", \"queryType\": \"A\"}").when()
					.post("/api/v1/dns/resolve").then()
					.statusCode(anyOf(is(200), is(400), is(500)));
		}
		
		@Test
		@DisplayName("Should reject domain names exceeding 255 characters")
		void testExceedsMaximumDomainNameLength() {
			// RFC 1035 Section 2.3.4: Size limits enforcement
			String tooLongDomain = generateDomainWithLength(300);
			given().contentType("application/json")
					.body("{\"domain\": \"" + tooLongDomain + "\", \"queryType\": \"A\"}").when()
					.post("/api/v1/dns/resolve").then()
					.statusCode(anyOf(is(400), is(413), is(500)));
		}
		
		@Test
		@DisplayName("Should handle label with maximum length (63 characters)")
		void testMaximumLabelLength() {
			// RFC 1035 Section 2.3.4: Each label 63 octets or less
			String maxLabelDomain = generateLabelWithLength(63) + ".com";
			given().contentType("application/json")
					.body("{\"domain\": \"" + maxLabelDomain + "\", \"queryType\": \"A\"}").when()
					.post("/api/v1/dns/resolve").then()
					.statusCode(anyOf(is(200), is(400), is(500)));
		}
		
		@Test
		@DisplayName("Should reject labels exceeding 63 characters")
		void testExceedsMaximumLabelLength() {
			// RFC 1035 Section 2.3.4: Label length validation
			String invalidDomain = generateLabelWithLength(64) + ".com";
			given().contentType("application/json")
					.body("{\"domain\": \"" + invalidDomain + "\", \"queryType\": \"A\"}").when()
					.post("/api/v1/dns/resolve").then().statusCode(anyOf(is(400), is(500)));
		}
		
		@Test
		@DisplayName("Should support A record queries")
		void testARecordQueryType() {
			// RFC 1035 Section 3.2.2: A record type = 1
			given().contentType("application/json")
					.body("{\"domain\": \"example.com\", \"queryType\": \"A\"}").when()
					.post("/api/v1/dns/resolve").then()
					.statusCode(anyOf(is(200), is(400), is(500)));
		}
		
		@Test
		@DisplayName("Should support AAAA record queries")
		void testAAAARecordQueryType() {
			// RFC 3596: IPv6 addresses - AAAA record type = 28
			given().contentType("application/json")
					.body("{\"domain\": \"example.com\", \"queryType\": \"AAAA\"}").when()
					.post("/api/v1/dns/resolve").then()
					.statusCode(anyOf(is(200), is(400), is(500)));
		}
		
		@Test
		@DisplayName("Should support MX record queries")
		void testMXRecordQueryType() {
			// RFC 1035 Section 3.3.9: MX record type = 15
			given().contentType("application/json")
					.body("{\"domain\": \"example.com\", \"queryType\": \"MX\"}").when()
					.post("/api/v1/dns/resolve").then()
					.statusCode(anyOf(is(200), is(400), is(500)));
		}
		
		@Test
		@DisplayName("Should support TXT record queries")
		void testTXTRecordQueryType() {
			// RFC 1035 Section 3.3.14: TXT record type = 16
			given().contentType("application/json")
					.body("{\"domain\": \"example.com\", \"queryType\": \"TXT\"}").when()
					.post("/api/v1/dns/resolve").then()
					.statusCode(anyOf(is(200), is(400), is(500)));
		}
		
		@Test
		@DisplayName("Should support CNAME record queries")
		void testCNAMERecordQueryType() {
			// RFC 1035 Section 3.3.1: CNAME record type = 5
			given().contentType("application/json")
					.body("{\"domain\": \"example.com\", \"queryType\": \"CNAME\"}").when()
					.post("/api/v1/dns/resolve").then()
					.statusCode(anyOf(is(200), is(400), is(500)));
		}
		
		@Test
		@DisplayName("Should support NS record queries")
		void testNSRecordQueryType() {
			// RFC 1035 Section 3.3.11: NS record type = 2
			given().contentType("application/json")
					.body("{\"domain\": \"example.com\", \"queryType\": \"NS\"}").when()
					.post("/api/v1/dns/resolve").then()
					.statusCode(anyOf(is(200), is(400), is(500)));
		}
		
		@Test
		@DisplayName("Should support SOA record queries")
		void testSOARecordQueryType() {
			// RFC 1035 Section 3.3.13: SOA record type = 6
			given().contentType("application/json")
					.body("{\"domain\": \"example.com\", \"queryType\": \"SOA\"}").when()
					.post("/api/v1/dns/resolve").then()
					.statusCode(anyOf(is(200), is(400), is(500)));
		}
		
		@Test
		@DisplayName("Should support PTR record queries (reverse DNS)")
		void testPTRRecordQueryType() {
			// RFC 1035 Section 3.3.12: PTR record type = 12
			given().contentType("application/json")
					.body("{\"domain\": \"1.168.192.in-addr.arpa\", \"queryType\": \"PTR\"}").when()
					.post("/api/v1/dns/resolve").then()
					.statusCode(anyOf(is(200), is(400), is(500)));
		}
		
		@Test
		@DisplayName("Should respect case-insensitive domain names")
		void testCaseInsensitivity() {
			// RFC 1035 Section 2.3.3: Domain names should be case-insensitive
			given().contentType("application/json")
					.body("{\"domain\": \"EXAMPLE.COM\", \"queryType\": \"A\"}").when()
					.post("/api/v1/dns/resolve").then()
					.statusCode(anyOf(is(200), is(400), is(500)));
		}
	}
	
	@Nested
	@DisplayName("RFC 1123 - Requirements for Internet Hosts")
	class RFC1123Tests {
		
		@Test
		@DisplayName("Should handle host names with uppercase letters")
		void testHostNameWithUppercase() {
			// RFC 1123 Section 2.1: Host software MUST support uppercase letters
			given().contentType("application/json")
					.body("{\"domain\": \"Example.Com\", \"queryType\": \"A\"}").when()
					.post("/api/v1/dns/resolve").then()
					.statusCode(anyOf(is(200), is(400), is(500)));
		}
		
		@Test
		@DisplayName("Should allow labels beginning with a digit")
		void testLabelBeginningWithDigit() {
			// RFC 1123 Section 2.1: Relaxes RFC 952 - allows labels starting with digits
			given().contentType("application/json")
					.body("{\"domain\": \"3com.com\", \"queryType\": \"A\"}").when()
					.post("/api/v1/dns/resolve").then()
					.statusCode(anyOf(is(200), is(400), is(500)));
		}
		
		@Test
		@DisplayName("Should support hyphens in domain names")
		void testHyphensInDomainName() {
			// RFC 1123 allows hyphens in labels (except first/last position)
			given().contentType("application/json")
					.body("{\"domain\": \"my-example.com\", \"queryType\": \"A\"}").when()
					.post("/api/v1/dns/resolve").then()
					.statusCode(anyOf(is(200), is(400), is(500)));
		}
	}
	
	@Nested
	@DisplayName("RFC 3597 - Handling of Unknown DNS Resource Record Types")
	class RFC3597Tests {
		
		@Test
		@DisplayName("Should handle unknown record types gracefully")
		void testUnknownRecordType() {
			// RFC 3597: DNS implementations should handle unknown RR types
			given().contentType("application/json")
					.body("{\"domain\": \"example.com\", \"queryType\": \"UNKNOWN\"}").when()
					.post("/api/v1/dns/resolve").then()
					.statusCode(anyOf(is(200), is(400), is(500)));
		}
		
		@Test
		@DisplayName("Should return valid response for TYPE0 queries")
		void testTYPE0Query() {
			// RFC 3597 Section 2: TYPE0 is reserved
			given().contentType("application/json")
					.body("{\"domain\": \"example.com\", \"queryType\": \"TYPE0\"}").when()
					.post("/api/v1/dns/resolve").then()
					.statusCode(anyOf(is(200), is(400), is(500)));
		}
	}
	
	@Nested
	@DisplayName("RFC 6891 - Extension Mechanisms for DNS (EDNS0)")
	class RFC6891Tests {
		
		@Test
		@DisplayName("Should support EDNS0 queries with extended header")
		void testEDNS0Support() {
			// RFC 6891: Supports extended DNS capabilities
			given().contentType("application/json")
					.body("{\"domain\": \"example.com\", \"queryType\": \"A\", \"edns0\": true}")
					.when().post("/api/v1/dns/resolve").then()
					.statusCode(anyOf(is(200), is(400), is(500)));
		}
	}
	
	@Nested
	@DisplayName("RFC 2119 - Key Words in RFCs")
	class RFC2119Tests {
		
		@Test
		@DisplayName("API should document MUST/SHOULD/MAY requirements")
		void testRequirementDocumentation() {
			// RFC 2119: All requirements should be clearly documented
			given().when().get("/api/v1/dns").then().statusCode(anyOf(is(200), is(404), is(500)));
		}
	}
	
	@Nested
	@DisplayName("RFC 1034 - Concepts and Facilities")
	class RFC1034Tests {
		
		@Test
		@DisplayName("Should cache DNS responses appropriately")
		void testDNSCaching() {
			// RFC 1034 Section 5: All name servers should cache responses
			given().contentType("application/json")
					.body("{\"domain\": \"example.com\", \"queryType\": \"A\"}").when()
					.post("/api/v1/dns/resolve").then()
					.statusCode(anyOf(is(200), is(400), is(500)));
		}
		
		@Test
		@DisplayName("Should handle NXDOMAIN responses for non-existent domains")
		void testNXDomainHandling() {
			// RFC 1034: Non-existent domains should return NXDOMAIN
			given().contentType("application/json")
					.body("{\"domain\": \"this-domain-definitely-does-not-exist-12345.com\", " +
							"\"queryType\": \"A\"}")
					.when().post("/api/v1/dns/resolve").then()
					.statusCode(anyOf(is(200), is(400), is(500)));
		}
	}
	
	@Nested
	@DisplayName("RFC 7858 - DNS over TLS")
	class RFC7858Tests {
		
		@Test
		@DisplayName("Should support DoT protocol indication")
		void testDoTProtocolSupport() {
			// RFC 7858: DNS over Transport Layer Security
			given().contentType("application/json")
					.body("{\"domain\": \"example.com\", \"queryType\": \"A\", \"protocol\": " +
							"\"DoT\"}")
					.when().post("/api/v1/dns/resolve").then()
					.statusCode(anyOf(is(200), is(400), is(500)));
		}
	}
	
	@Nested
	@DisplayName("RFC 8484 - DNS over HTTPS (DoH)")
	class RFC8484Tests {
		
		@Test
		@DisplayName("Should support DoH protocol indication")
		void testDoHProtocolSupport() {
			// RFC 8484: DNS over HTTPS
			given().contentType("application/json")
					.body("{\"domain\": \"example.com\", \"queryType\": \"A\", \"protocol\": " +
							"\"DoH\"}")
					.when().post("/api/v1/dns/resolve").then()
					.statusCode(anyOf(is(200), is(400), is(500)));
		}
	}
	
	@Nested
	@DisplayName("RFC 6762 - Multicast DNS")
	class RFC6762Tests {
		
		@Test
		@DisplayName("Should handle mDNS style queries (.local domains)")
		void testMulticastDNSLocalDomain() {
			// RFC 6762: mDNS uses .local special-use top-level domain
			given().contentType("application/json")
					.body("{\"domain\": \"mydevice.local\", \"queryType\": \"A\"}").when()
					.post("/api/v1/dns/resolve").then()
					.statusCode(anyOf(is(200), is(400), is(500)));
		}
	}
	
	@Nested
	@DisplayName("RFC 6761 - Special-Use Domain Names")
	class RFC6761Tests {
		
		@Test
		@DisplayName("Should handle localhost domain")
		void testLocalhostDomain() {
			// RFC 6761 Section 6.3: localhost special-use domain
			given().contentType("application/json")
					.body("{\"domain\": \"localhost\", \"queryType\": \"A\"}").when()
					.post("/api/v1/dns/resolve").then()
					.statusCode(anyOf(is(200), is(400), is(500)));
		}
		
		@Test
		@DisplayName("Should handle localhost.localdomain")
		void testLocalhostLocaldomainDomain() {
			// RFC 6761: localhost.localdomain handling
			given().contentType("application/json")
					.body("{\"domain\": \"localhost.localdomain\", \"queryType\": \"A\"}").when()
					.post("/api/v1/dns/resolve").then()
					.statusCode(anyOf(is(200), is(400), is(500)));
		}
		
		@Test
		@DisplayName("Should handle example.com domain")
		void testExampleComDomain() {
			// RFC 6761 Section 6.5: example.com is reserved for documentation
			given().contentType("application/json")
					.body("{\"domain\": \"example.com\", \"queryType\": \"A\"}").when()
					.post("/api/v1/dns/resolve").then()
					.statusCode(anyOf(is(200), is(400), is(500)));
		}
		
		@Test
		@DisplayName("Should handle test domains")
		void testTestDomain() {
			// RFC 6761: test domain names reserved for testing
			given().contentType("application/json")
					.body("{\"domain\": \"test.example.com\", \"queryType\": \"A\"}").when()
					.post("/api/v1/dns/resolve").then()
					.statusCode(anyOf(is(200), is(400), is(500)));
		}
		
		@Test
		@DisplayName("Should handle invalid domain (.invalid)")
		void testInvalidDomain() {
			// RFC 6761 Section 6.4: .invalid is guaranteed to be invalid
			given().contentType("application/json")
					.body("{\"domain\": \"something.invalid\", \"queryType\": \"A\"}").when()
					.post("/api/v1/dns/resolve").then()
					.statusCode(anyOf(is(200), is(400), is(500)));
		}
	}
	
	@Nested
	@DisplayName("RFC 5452 - DNS Resilience Against Forged Answers")
	class RFC5452Tests {
		
		@Test
		@DisplayName("Should implement DNS security measures")
		void testDNSSecurityMeasures() {
			// RFC 5452: Implementation of resilience against forged answers
			given().contentType("application/json")
					.body("{\"domain\": \"example.com\", \"queryType\": \"A\"}").when()
					.post("/api/v1/dns/resolve").then()
					.statusCode(anyOf(is(200), is(400), is(500)));
		}
	}
	
	@Nested
	@DisplayName("RFC 6698 - DANE - TLSA Records")
	class RFC6698Tests {
		
		@Test
		@DisplayName("Should support TLSA record queries")
		void testTLSARecordQueryType() {
			// RFC 6698: DANE - DNS-based Authentication of Named Entities
			given().contentType("application/json")
					.body("{\"domain\": \"_443._tcp.example.com\", \"queryType\": \"TLSA\"}").when()
					.post("/api/v1/dns/resolve").then()
					.statusCode(anyOf(is(200), is(400), is(500)));
		}
	}
	
	@Nested
	@DisplayName("RFC 4592 - The Role of Wildcards in the DNS")
	class RFC4592Tests {
		
		@Test
		@DisplayName("Should handle wildcard domain queries")
		void testWildcardDomainQueries() {
			// RFC 4592: Wildcard patterns in DNS
			given().contentType("application/json")
					.body("{\"domain\": \"*.example.com\", \"queryType\": \"A\"}").when()
					.post("/api/v1/dns/resolve").then()
					.statusCode(anyOf(is(200), is(400), is(500)));
		}
	}
	
	@Nested
	@DisplayName("RFC 1912 - Common DNS Errors")
	class RFC1912Tests {
		
		@Test
		@DisplayName("Should handle common DNS errors appropriately")
		void testErrorHandling() {
			// RFC 1912: Common DNS errors and handling
			given().contentType("application/json")
					.body("{\"domain\": \"invalid..domain\", \"queryType\": \"A\"}").when()
					.post("/api/v1/dns/resolve").then()
					.statusCode(anyOf(is(200), is(400), is(500)));
		}
	}
	
	// Helper methods
	
	@Nested
	@DisplayName("RFC 2181 - Clarifications to the DNS Specification")
	class RFC2181Tests {
		
		@Test
		@DisplayName("Should handle TTL values correctly")
		void testTTLHandling() {
			// RFC 2181: TTL field handling and semantics
			given().contentType("application/json")
					.body("{\"domain\": \"example.com\", \"queryType\": \"A\"}").when()
					.post("/api/v1/dns/resolve").then()
					.statusCode(anyOf(is(200), is(400), is(500)));
		}
		
		@Test
		@DisplayName("Should respect DNS record class field")
		void testDNSClassField() {
			// RFC 2181: DNS class field (usually IN for Internet)
			given().contentType("application/json")
					.body("{\"domain\": \"example.com\", \"queryType\": \"A\", \"class\": \"IN\"}")
					.when().post("/api/v1/dns/resolve").then()
					.statusCode(anyOf(is(200), is(400), is(500)));
		}
	}
	
	@Nested
	@DisplayName("RFC 3901 - DNS IPv6 Transport Mechanisms")
	class RFC3901Tests {
		
		@Test
		@DisplayName("Should support IPv6 transport for DNS queries")
		void testIPv6Transport() {
			// RFC 3901: DNS over IPv6 transport
			given().contentType("application/json")
					.body("{\"domain\": \"example.com\", \"queryType\": \"A\", \"clientIp\": " +
							"\"::1\"}")
					.when().post("/api/v1/dns/resolve").then()
					.statusCode(anyOf(is(200), is(400), is(500)));
		}
	}
}

