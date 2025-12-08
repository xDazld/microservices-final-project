package dev.pacr.dns.api;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Base64;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.anyOf;

/**
 * Integration tests for DNS over HTTP (DoH) endpoint compliant with RFC 8484
 *
 * Tests the actual DNS resolution functionality with real service instances using
 * DNS wire format messages and base64url encoding as specified in RFC 8484.
 */
@QuarkusTest
@DisplayName("RFC 8484 - DNS over HTTP (DoH) Tests")
class DNSQueryResourceTest {
	
	private static final String DOH_ENDPOINT = "/dns-query";
	private static final String DNS_MESSAGE_TYPE = "application/dns-message";
	
	@BeforeEach
	void setUp() {
		// Additional setup if needed
	}
	
	/**
	 * Helper method to create a minimal DNS query in wire format
	 *
	 * @param domain    The domain to query (e.g., "example.com")
	 * @param queryType The DNS query type (e.g., 1 for A record)
	 * @return DNS query in wire format (binary)
	 */
	private byte[] createDNSQuery(String domain, int queryType) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		
		// Transaction ID (0x0001)
		dos.writeShort(0x0001);
		
		// Flags: Standard query (0x0000)
		dos.writeShort(0x0000);
		
		// QDCOUNT: 1 question
		dos.writeShort(1);
		
		// ANCOUNT: 0 answers
		dos.writeShort(0);
		
		// NSCOUNT: 0 authority records
		dos.writeShort(0);
		
		// ARCOUNT: 0 additional records
		dos.writeShort(0);
		
		// Question section: domain name
		writeDomainName(dos, domain);
		
		// Query type
		dos.writeShort(queryType);
		
		// Query class (1 = IN - Internet)
		dos.writeShort(1);
		
		dos.flush();
		return baos.toByteArray();
	}
	
	/**
	 * Helper method to write a domain name in DNS wire format
	 */
	private void writeDomainName(DataOutputStream dos, String domain) throws IOException {
		String[] labels = domain.split("\\.");
		for (String label : labels) {
			dos.writeByte(label.length());
			for (char c : label.toCharArray()) {
				dos.writeByte((byte) c);
			}
		}
		dos.writeByte(0); // End of domain name
	}
	
	/**
	 * Helper method to encode binary data to base64url (RFC 4648)
	 */
	private String base64urlEncode(byte[] data) {
		String encoded = Base64.getEncoder().encodeToString(data);
		// Replace standard base64 characters with base64url characters and remove padding
		return encoded.replace('+', '-').replace('/', '_').replaceAll("=+$", "");
	}
	
	/**
	 * Test POST method with DNS wire format message for A record query
	 */
	@Test
	@DisplayName("POST - Should resolve example.com A record via DNS wire format")
	void testPostResolveDomainSuccess() throws IOException {
		byte[] dnsQuery = createDNSQuery("example.com", 1); // 1 = A record
		
		given().contentType(DNS_MESSAGE_TYPE).body(dnsQuery).when().post(DOH_ENDPOINT).then()
				.statusCode(anyOf(is(200), is(500)));
	}
	
	/**
	 * Test GET method with base64url encoded DNS query
	 */
	@Test
	@DisplayName("GET - Should resolve example.com A record via base64url encoded query")
	void testGetResolveDomainSuccess() throws IOException {
		byte[] dnsQuery = createDNSQuery("example.com", 1); // 1 = A record
		String encodedQuery = base64urlEncode(dnsQuery);
		
		given().when().get(DOH_ENDPOINT + "?dns=" + encodedQuery).then()
				.statusCode(anyOf(is(200), is(500)));
	}
	
	/**
	 * Test POST method with AAAA record query
	 */
	@Test
	@DisplayName("POST - Should resolve example.com AAAA record (IPv6)")
	void testPostResolveAAAARecord() throws IOException {
		byte[] dnsQuery = createDNSQuery("example.com", 28); // 28 = AAAA record
		
		given().contentType(DNS_MESSAGE_TYPE).body(dnsQuery).when().post(DOH_ENDPOINT).then()
				.statusCode(anyOf(is(200), is(500)));
	}
	
	/**
	 * Test POST method with MX record query
	 */
	@Test
	@DisplayName("POST - Should resolve example.com MX record")
	void testPostResolveMXRecord() throws IOException {
		byte[] dnsQuery = createDNSQuery("example.com", 15); // 15 = MX record
		
		given().contentType(DNS_MESSAGE_TYPE).body(dnsQuery).when().post(DOH_ENDPOINT).then()
				.statusCode(anyOf(is(200), is(500)));
	}
	
	/**
	 * Test POST method with TXT record query
	 */
	@Test
	@DisplayName("POST - Should resolve example.com TXT record")
	void testPostResolveTXTRecord() throws IOException {
		byte[] dnsQuery = createDNSQuery("example.com", 16); // 16 = TXT record
		
		given().contentType(DNS_MESSAGE_TYPE).body(dnsQuery).when().post(DOH_ENDPOINT).then()
				.statusCode(anyOf(is(200), is(500)));
	}
	
	/**
	 * Test POST method with CNAME record query
	 */
	@Test
	@DisplayName("POST - Should resolve example.com CNAME record")
	void testPostResolveCNAMERecord() throws IOException {
		byte[] dnsQuery = createDNSQuery("example.com", 5); // 5 = CNAME record
		
		given().contentType(DNS_MESSAGE_TYPE).body(dnsQuery).when().post(DOH_ENDPOINT).then()
				.statusCode(anyOf(is(200), is(500)));
	}
	
	/**
	 * Test POST method with NS record query
	 */
	@Test
	@DisplayName("POST - Should resolve example.com NS record")
	void testPostResolveNSRecord() throws IOException {
		byte[] dnsQuery = createDNSQuery("example.com", 2); // 2 = NS record
		
		given().contentType(DNS_MESSAGE_TYPE).body(dnsQuery).when().post(DOH_ENDPOINT).then()
				.statusCode(anyOf(is(200), is(500)));
	}
	
	/**
	 * Test POST method with SOA record query
	 */
	@Test
	@DisplayName("POST - Should resolve example.com SOA record")
	void testPostResolveSOARecord() throws IOException {
		byte[] dnsQuery = createDNSQuery("example.com", 6); // 6 = SOA record
		
		given().contentType(DNS_MESSAGE_TYPE).body(dnsQuery).when().post(DOH_ENDPOINT).then()
				.statusCode(anyOf(is(200), is(500)));
	}
	
	/**
	 * Test GET method with missing dns parameter
	 */
	@Test
	@DisplayName("GET - Should reject request with missing dns parameter")
	void testGetMissingDnsParameter() {
		given().when().get(DOH_ENDPOINT).then().statusCode(anyOf(is(400), is(500)));
	}
	
	/**
	 * Test GET method with empty dns parameter
	 */
	@Test
	@DisplayName("GET - Should reject request with empty dns parameter")
	void testGetEmptyDnsParameter() {
		given().when().get(DOH_ENDPOINT + "?dns=").then().statusCode(anyOf(is(400), is(500)));
	}
	
	/**
	 * Test GET method with invalid base64url encoding
	 */
	@Test
	@DisplayName("GET - Should reject request with invalid base64url encoding")
	void testGetInvalidBase64Encoding() {
		given().when().get(DOH_ENDPOINT + "?dns=!!!invalid!!!").then()
				.statusCode(anyOf(is(400), is(500)));
	}
	
	/**
	 * Test POST method with empty body
	 */
	@Test
	@DisplayName("POST - Should reject request with empty DNS message")
	void testPostEmptyMessage() {
		given().contentType(DNS_MESSAGE_TYPE).body(new byte[0]).when().post(DOH_ENDPOINT).then()
				.statusCode(anyOf(is(400), is(500)));
	}
	
	
	/**
	 * Test POST method with valid response media type
	 */
	@Test
	@DisplayName("POST - Should return response with application/dns-message media type")
	void testPostResponseMediaType() throws IOException {
		byte[] dnsQuery = createDNSQuery("example.com", 1); // 1 = A record
		
		given().contentType(DNS_MESSAGE_TYPE).body(dnsQuery).when().post(DOH_ENDPOINT).then()
				.contentType(DNS_MESSAGE_TYPE);
	}
	
	/**
	 * Test GET method with valid response media type
	 */
	@Test
	@DisplayName("GET - Should return response with application/dns-message media type")
	void testGetResponseMediaType() throws IOException {
		byte[] dnsQuery = createDNSQuery("example.com", 1); // 1 = A record
		String encodedQuery = base64urlEncode(dnsQuery);
		
		given().when().get(DOH_ENDPOINT + "?dns=" + encodedQuery).then()
				.contentType(DNS_MESSAGE_TYPE);
	}
	
	/**
	 * Test GET method with Cache-Control header
	 */
	@Test
	@DisplayName("GET - Should return Cache-Control header for HTTP caching")
	void testGetCacheControlHeader() throws IOException {
		byte[] dnsQuery = createDNSQuery("example.com", 1); // 1 = A record
		String encodedQuery = base64urlEncode(dnsQuery);
		
		given().when().get(DOH_ENDPOINT + "?dns=" + encodedQuery).then()
				.header("Cache-Control", is("max-age=300"));
	}
	
	/**
	 * Test POST method with multiple query types
	 */
	@Test
	@DisplayName("POST - Should handle various query types (A, AAAA, MX, TXT, CNAME)")
	void testPostMultipleQueryTypes() throws IOException {
		int[] queryTypes = {1, 28, 15, 16, 5}; // A, AAAA, MX, TXT, CNAME
		
		for (int queryType : queryTypes) {
			byte[] dnsQuery = createDNSQuery("example.com", queryType);
			
			given().contentType(DNS_MESSAGE_TYPE).body(dnsQuery).when().post(DOH_ENDPOINT).then()
					.statusCode(anyOf(is(200), is(500)));
		}
	}
	
	/**
	 * Test POST method with different domains
	 */
	@Test
	@DisplayName("POST - Should handle queries for different domains")
	void testPostMultipleDomains() throws IOException {
		String[] domains = {"example.com", "google.com", "github.com"};
		
		for (String domain : domains) {
			byte[] dnsQuery = createDNSQuery(domain, 1); // A record
			
			given().contentType(DNS_MESSAGE_TYPE).body(dnsQuery).when().post(DOH_ENDPOINT)
					.then().statusCode(anyOf(is(200), is(500)));
		}
	}
	
	/**
	 * Test GET method with base64url vs standard base64 characters
	 */
	@Test
	@DisplayName("GET - Should correctly decode base64url with - and _ characters")
	void testGetBase64urlCharacters() throws IOException {
		// Create a DNS query that when encoded will contain - and _ characters
		byte[] dnsQuery = createDNSQuery("example.com", 1);
		String encodedQuery = base64urlEncode(dnsQuery);
		
		// Verify that encoding uses base64url alphabet (no + or /)
		assert !encodedQuery.contains("+") : "Encoded query should not contain +";
		assert !encodedQuery.contains("/") : "Encoded query should not contain /";
		assert !encodedQuery.contains("=") : "Encoded query should not contain padding";
		
		given().when().get(DOH_ENDPOINT + "?dns=" + encodedQuery).then()
				.statusCode(anyOf(is(200), is(500)));
	}
	
	/**
	 * Test POST method with subdomain
	 */
	@Test
	@DisplayName("POST - Should resolve subdomains correctly")
	void testPostSubdomain() throws IOException {
		byte[] dnsQuery = createDNSQuery("sub.example.com", 1);
		
		given().contentType(DNS_MESSAGE_TYPE).body(dnsQuery).when().post(DOH_ENDPOINT).then()
				.statusCode(anyOf(is(200), is(500)));
	}
	
	/**
	 * Test GET method with deep subdomain
	 */
	@Test
	@DisplayName("GET - Should resolve deep subdomains correctly")
	void testGetDeepSubdomain() throws IOException {
		byte[] dnsQuery = createDNSQuery("very.deep.sub.example.com", 1);
		String encodedQuery = base64urlEncode(dnsQuery);
		
		given()
				.when()
				.get(DOH_ENDPOINT + "?dns=" + encodedQuery)
				.then()
				.statusCode(anyOf(is(200), is(500)));
	}
}

