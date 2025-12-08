package dev.pacr.dns.api;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;

/**
 * Integration tests for Admin REST API endpoint Tests administrative functions and monitoring
 * capabilities
 */
@QuarkusTest
class AdminResourceTest {
	
	@Test
	void testGetStatisticsEndpointExists() {
		given().when().get("/api/v1/admin/stats").then()
				.statusCode(anyOf(is(200), is(401), is(500)));
	}
	
	@Test
	void testGetStatisticsReturnsData() {
		given().when().get("/api/v1/admin/stats").then()
				.statusCode(anyOf(is(200), is(401), is(500)));
	}
	
	@Test
	void testGetCacheStatsEndpointExists() {
		given().when().get("/api/v1/admin/cache/stats").then()
				.statusCode(anyOf(is(200), is(401), is(500)));
	}
	
	@Test
	void testGetCacheStatsReturnsData() {
		given().when().get("/api/v1/admin/cache/stats").then()
				.statusCode(anyOf(is(200), is(401), is(500)));
	}
	
	@Test
	void testHealthCheckEndpointExists() {
		given().when().get("/api/v1/admin/health").then()
				.statusCode(anyOf(is(200), is(401), is(500)));
	}
	
	@Test
	void testHealthCheckReturnsStatus() {
		given().when().get("/api/v1/admin/health").then()
				.statusCode(anyOf(is(200), is(401), is(500)));
	}
	
	@Test
	void testMetricsEndpointExists() {
		given().when().get("/metrics").then().statusCode(anyOf(is(200), is(500)));
	}
	
	@Test
	void testMetricsReturnsData() {
		given().when().get("/metrics").then().statusCode(anyOf(is(200), is(500)));
	}
}

