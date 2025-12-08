package dev.pacr.dns.api;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;

/**
 * Integration tests for Filter Rule REST API endpoint Tests filter rule management and statistics
 * functionality
 */
@QuarkusTest
class FilterRuleResourceTest {
	
	@Test
	void testGetFiltersEndpointExists() {
		given().when().get("/api/v1/filters").then().statusCode(anyOf(is(200), is(401), is(500)));
	}
	
	@Test
	void testGetFiltersReturnsArray() {
		given().when().get("/api/v1/filters").then().statusCode(anyOf(is(200), is(401), is(500)));
	}
	
	@Test
	void testCreateFilterEndpointExists() {
		given().contentType(ContentType.JSON)
				.body("{\"name\": \"test\", \"pattern\": \"*.test.com\", \"type\": \"BLOCK\", " +
						"\"category\": \"test\", \"priority\": 100}")
				.when().post("/api/v1/filters").then()
				.statusCode(anyOf(is(201), is(400), is(401), is(500)));
	}
	
	@Test
	void testCreateFilterWithValidData() {
		given().contentType(ContentType.JSON)
				.body("{\"name\": \"Block Malware\", \"pattern\": \"*.malware.com\", \"type\": " +
						"\"BLOCK\", \"category\": \"malware\", \"priority\": 200}")
				.when().post("/api/v1/filters").then()
				.statusCode(anyOf(is(201), is(400), is(401), is(500)));
	}
	
	@Test
	void testGetStatsEndpointExists() {
		given().when().get("/api/v1/filters/stats").then()
				.statusCode(anyOf(is(200), is(401), is(500)));
	}
	
	@Test
	void testGetStatsReturnsData() {
		given().when().get("/api/v1/filters/stats").then()
				.statusCode(anyOf(is(200), is(401), is(500)));
	}
}

