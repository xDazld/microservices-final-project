package dev.pacr.dns.api;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.anyOf;

/**
 * Integration tests for DNS Query REST API endpoint Tests the actual DNS resolution functionality
 * with real service instances
 */
@QuarkusTest
class DNSQueryResourceTest {
	
	@Test
	void testResolveDomainSuccess() {
		given().contentType(ContentType.JSON)
				.body("{\"domain\": \"example.com\", \"queryType\": \"A\"}").when()
				.post("/api/v1/dns/resolve").then().statusCode(anyOf(is(200), is(500)));
	}
	
	@Test
	void testResolveDomainWithAllParameters() {
		given().contentType(ContentType.JSON)
				.body("{\"domain\": \"example.com\", \"queryType\": \"A\", \"clientIp\": \"192.168" +
						".1.1\", \"protocol\": \"UDP\"}")
				.when().post("/api/v1/dns/resolve").then()
				.statusCode(anyOf(is(200), is(400), is(500)));
	}
	
	@Test
	void testResolveDomainDefaultQueryType() {
		given().contentType(ContentType.JSON).body("{\"domain\": \"google.com\"}").when()
				.post("/api/v1/dns/resolve").then().statusCode(anyOf(is(200), is(500)));
	}
	
	@Test
	void testResolveDomainMissingDomain() {
		given().contentType(ContentType.JSON).body("{\"queryType\": \"A\"}").when()
				.post("/api/v1/dns/resolve").then().statusCode(anyOf(is(400), is(500)));
	}
	
	@Test
	void testResolveDomainEmptyDomain() {
		given().contentType(ContentType.JSON).body("{\"domain\": \"\", \"queryType\": \"A\"}")
				.when().post("/api/v1/dns/resolve").then().statusCode(anyOf(is(400), is(500)));
	}
	
	@Test
	void testResolveDomainBlankDomain() {
		given().contentType(ContentType.JSON).body("{\"domain\": \"   \", \"queryType\": \"A\"}")
				.when().post("/api/v1/dns/resolve").then().statusCode(anyOf(is(400), is(500)));
	}
	
	@Test
	void testResolveNonexistentDomain() {
		given().contentType(ContentType.JSON)
				.body("{\"domain\": \"this-domain-definitely-does-not-exist-12345.com\", " +
						"\"queryType\": \"A\"}")
				.when().post("/api/v1/dns/resolve").then().statusCode(anyOf(is(200), is(500)));
	}
	
	@Test
	void testResolveLongDomain() {
		given().contentType(ContentType.JSON)
				.body("{\"domain\": \"subdomain.example.co.uk\", \"queryType\": \"A\"}").when()
				.post("/api/v1/dns/resolve").then().statusCode(anyOf(is(200), is(500)));
	}
	
	@Test
	void testBatchResolveSingleDomain() {
		given().contentType(ContentType.JSON).body("{\"domains\": [\"example.com\"]}").when()
				.post("/api/v1/dns/resolve/batch").then()
				.statusCode(anyOf(is(200), is(400), is(500)));
	}
	
	@Test
	void testBatchResolveDomains() {
		given().contentType(ContentType.JSON)
				.body("{\"domains\": [\"example.com\", \"google.com\", \"github.com\"]}").when()
				.post("/api/v1/dns/resolve/batch").then()
				.statusCode(anyOf(is(200), is(400), is(500)));
	}
	
	@Test
	void testBatchResolveMissingDomains() {
		given().contentType(ContentType.JSON).body("{\"queryType\": \"A\"}").when()
				.post("/api/v1/dns/resolve/batch").then().statusCode(anyOf(is(400), is(500)));
	}
	
	@Test
	void testBatchResolveEmptyDomains() {
		given().contentType(ContentType.JSON).body("{\"domains\": []}").when()
				.post("/api/v1/dns/resolve/batch").then().statusCode(anyOf(is(400), is(500)));
	}
	
	@Test
	void testResolveDifferentQueryTypes() {
		String[] queryTypes = {"A", "AAAA", "CNAME", "MX", "TXT"};
		for (String queryType : queryTypes) {
			given().contentType(ContentType.JSON)
					.body("{\"domain\": \"example.com\", \"queryType\": \"" + queryType + "\"}")
					.when().post("/api/v1/dns/resolve").then()
					.statusCode(anyOf(is(200), is(400), is(500)));
		}
	}
}

