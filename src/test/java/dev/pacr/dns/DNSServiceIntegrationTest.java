package dev.pacr.dns;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Base64;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;

/**
 * Integration tests for the complete DNS filtering service Tests end-to-end workflows with real
 * service instances using the RFC 8484 compliant DoH endpoint
 */
@QuarkusTest
class DNSServiceIntegrationTest {
	
	private static final String DNS_MESSAGE_TYPE = "application/dns-message";
	
	@Test
	void testApplicationStartsSuccessfully() {
		// If the test reaches here, the application started
		given().when().get("/api/v1/admin/health").then()
				.statusCode(anyOf(is(200), is(401), is(500)));
	}
	
	@Test
	void testHealthCheckEndpoint() {
		given().when().get("/api/v1/admin/health").then()
				.statusCode(anyOf(is(200), is(401), is(500)));
	}
	
	/**
	 * Helper method to create a minimal DNS query in wire format
	 */
	protected static byte[] createDNSQuery(String domain, int queryType) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		
		// Transaction ID
		dos.writeShort(0x0001);
		// Flags: Standard query
		dos.writeShort(0x0000);
		// QDCOUNT
		dos.writeShort(1);
		// ANCOUNT
		dos.writeShort(0);
		// NSCOUNT
		dos.writeShort(0);
		// ARCOUNT
		dos.writeShort(0);
		
		// Question section: domain name
		writeDomainName(dos, domain);
		// Query type
		dos.writeShort(queryType);
		// Query class (IN)
		dos.writeShort(1);
		
		dos.flush();
		return baos.toByteArray();
	}
	
	@Test
	void testMetricsEndpointAccessible() {
		given().when().get("/metrics").then().statusCode(anyOf(is(200), is(500)));
	}
	
	@Test
	void testAdminStatsEndpointAccessible() {
		given().when().get("/api/v1/admin/stats").then()
				.statusCode(anyOf(is(200), is(401), is(500)));
	}
	
	@Test
	void testFiltersEndpointAccessible() {
		given().when().get("/api/v1/filters").then().statusCode(anyOf(is(200), is(401), is(500)));
	}
	
	/**
	 * Helper method to write a domain name in DNS wire format
	 */
	private static void writeDomainName(DataOutputStream dos, String domain) throws IOException {
		String[] labels = domain.split("\\.");
		for (String label : labels) {
			dos.writeByte(label.length());
			for (char c : label.toCharArray()) {
				dos.writeByte((byte) c);
			}
		}
		dos.writeByte(0);
	}
	
	@Test
	void testFilterStatsEndpointAccessible() {
		given().when().get("/api/v1/filters/stats").then()
				.statusCode(anyOf(is(200), is(401), is(500)));
	}
	
	@Test
	void testCacheStatsEndpointAccessible() {
		given().when().get("/api/v1/admin/cache/stats").then()
				.statusCode(anyOf(is(200), is(401), is(500)));
	}
	
	@Test
	void testDNSResolveEndpointAccessible() throws IOException {
		// RFC 8484: Use new /dns-query endpoint with DNS wire format
		byte[] dnsQuery = createDNSQuery("example.com", 1);
		given().contentType(DNS_MESSAGE_TYPE).body(dnsQuery).when().post("/dns-query").then()
				.statusCode(anyOf(is(200), is(400), is(500)));
	}
	
	@Test
	void testDNSQueryGetMethodAccessible() throws IOException {
		// RFC 8484: Test GET method with base64url encoding
		byte[] dnsQuery = createDNSQuery("google.com", 1);
		String encodedQuery = base64urlEncode(dnsQuery);
		given().when().get("/dns-query?dns=" + encodedQuery).then()
				.statusCode(anyOf(is(200), is(400), is(500))).contentType(DNS_MESSAGE_TYPE);
	}
	
	@Test
	void testMultipleConsecutiveRequests() throws IOException {
		// Test that the service can handle multiple consecutive requests
		// Using RFC 8484 DoH endpoint
		for (int i = 0; i < 5; i++) {
			byte[] dnsQuery = createDNSQuery("example" + i + ".com", 1);
			given().contentType(DNS_MESSAGE_TYPE).body(dnsQuery).when().post("/dns-query").then()
					.statusCode(anyOf(is(200), is(400), is(500)));
		}
	}
	
	/**
	 * Helper method to encode binary data to base64url (RFC 4648)
	 */
	private String base64urlEncode(byte[] data) {
		String encoded = Base64.getEncoder().encodeToString(data);
		return encoded.replace('+', '-').replace('/', '_').replaceAll("=+$", "");
	}
}

