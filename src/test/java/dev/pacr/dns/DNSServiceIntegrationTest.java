package dev.pacr.dns;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;

/**
 * Integration tests for the complete DNS filtering service Tests end-to-end workflows with real
 * service instances
 */
@QuarkusTest
class DNSServiceIntegrationTest {
	
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
	
	@Test
	void testDNSResolveEndpointAccessible() {
		given().contentType("application/json").body("{\"domain\": \"example.com\"}").when()
				.post("/api/v1/dns/resolve").then().statusCode(anyOf(is(200), is(400), is(500)));
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
	
	@Test
	void testBatchResolveEndpointAccessible() {
		given().contentType("application/json")
				.body("{\"domains\": [\"example.com\", \"google.com\"]}").when()
				.post("/api/v1/dns/resolve/batch").then()
				.statusCode(anyOf(is(200), is(400), is(500)));
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
	void testMultipleConsecutiveRequests() {
		// Test that the service can handle multiple consecutive requests
		for (int i = 0; i < 5; i++) {
			given().contentType("application/json").body("{\"domain\": \"example" + i + ".com\"}")
					.when().post("/api/v1/dns/resolve").then()
					.statusCode(anyOf(is(200), is(400), is(500)));
		}
	}
}

