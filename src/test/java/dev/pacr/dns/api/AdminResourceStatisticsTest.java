package dev.pacr.dns.api;

import dev.pacr.dns.service.EndpointStatisticsService;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Integration tests for endpoint statistics functionality
 */
@QuarkusTest
public class AdminResourceStatisticsTest {
	
	@Inject
	EndpointStatisticsService statisticsService;
	
	@BeforeEach
	public void setup() {
		// Reset statistics before each test
		statisticsService.resetStatistics();
	}
	
	@Test
	public void testGetAllStatisticsInitially() {
		// Initially should have empty or minimal statistics
		given().when().get("/api/v1/admin/endpoints/statistics").then().statusCode(200)
				.contentType(ContentType.JSON).body("summary", notNullValue())
				.body("summary.totalEndpointsAccessed", equalTo(0))
				.body("endpoints", equalTo(null));
	}
	
	@Test
	public void testRecordEndpointStatistics() {
		// Simulate recording an endpoint request
		statisticsService.recordRequest("GET", "/test-endpoint", 50, 200, 100, 500);
		
		// Get all statistics
		given().when().get("/api/v1/admin/endpoints/statistics").then().statusCode(200)
				.body("summary.totalEndpointsAccessed", equalTo(1))
				.body("summary.totalRequests", equalTo(1))
				.body("summary.totalSuccessful", equalTo(1))
				.body("summary.totalFailed", equalTo(0));
	}
	
	@Test
	public void testGetSpecificEndpointStatistics() {
		// Record some requests
		statisticsService.recordRequest("GET", "/api/v1/admin", 30, 200, 50, 400);
		statisticsService.recordRequest("GET", "/api/v1/admin", 45, 200, 50, 400);
		statisticsService.recordRequest("GET", "/api/v1/admin", 55, 200, 50, 400);
		
		// Get statistics for this specific endpoint
		given().when().get("/api/v1/admin/endpoints/statistics/GET/api/v1/admin").then()
				.statusCode(200).body("endpoint", equalTo("/api/v1/admin"))
				.body("method", equalTo("GET")).body("totalRequests", equalTo(3))
				.body("successfulRequests", equalTo(3)).body("failedRequests", equalTo(0));
	}
	
	@Test
	public void testGetEndpointCountBeforeAnyRequests() {
		given().when().get("/api/v1/admin/endpoints/count").then().statusCode(200)
				.body("totalUniqueEndpointsAccessed", equalTo(0));
	}
	
	@Test
	public void testGetEndpointCountAfterRequests() {
		// Record requests to different endpoints
		statisticsService.recordRequest("GET", "/endpoint1", 50, 200, 100, 500);
		statisticsService.recordRequest("POST", "/endpoint1", 60, 201, 150, 600);
		statisticsService.recordRequest("GET", "/endpoint2", 40, 200, 100, 500);
		
		// Should have 3 unique endpoints (GET /endpoint1, POST /endpoint1, GET /endpoint2)
		given().when().get("/api/v1/admin/endpoints/count").then().statusCode(200)
				.body("totalUniqueEndpointsAccessed", equalTo(3));
	}
	
	@Test
	public void testErrorRateCalculation() {
		// Record mix of successful and failed requests
		statisticsService.recordRequest("GET", "/test", 50, 200, 100, 500); // success
		statisticsService.recordRequest("GET", "/test", 50, 200, 100, 500); // success
		statisticsService.recordRequest("GET", "/test", 50, 200, 100, 500); // success
		statisticsService.recordRequest("GET", "/test", 100, 500, 100, 200); // failure
		
		given().when().get("/api/v1/admin/endpoints/statistics/GET/test").then().statusCode(200)
				.body("totalRequests", equalTo(4)).body("successfulRequests", equalTo(3))
				.body("failedRequests", equalTo(1)).body("successRate", containsString("75"));
	}
	
	@Test
	public void testStatusCodeDistribution() {
		// Record various status codes
		statisticsService.recordRequest("GET", "/test", 50, 200, 100, 500);
		statisticsService.recordRequest("GET", "/test", 50, 200, 100, 500);
		statisticsService.recordRequest("GET", "/test", 50, 404, 100, 200);
		statisticsService.recordRequest("GET", "/test", 100, 500, 100, 200);
		
		given().when().get("/api/v1/admin/endpoints/statistics/GET/test").then().statusCode(200)
				.body("statusCodeDistribution.'200'", equalTo(2))
				.body("statusCodeDistribution.'404'", equalTo(1))
				.body("statusCodeDistribution.'500'", equalTo(1));
	}
	
	@Test
	public void testResponseTimeMetrics() {
		// Record requests with different response times
		statisticsService.recordRequest("GET", "/test", 10, 200, 100, 500);
		statisticsService.recordRequest("GET", "/test", 20, 200, 100, 500);
		statisticsService.recordRequest("GET", "/test", 30, 200, 100, 500);
		
		given().when().get("/api/v1/admin/endpoints/statistics/GET/test").then().statusCode(200)
				.body("minResponseTime", containsString("10"))
				.body("maxResponseTime", containsString("30"))
				.body("averageResponseTime", containsString("20"));
	}
	
	@Test
	public void testGetStatisticsByPattern() {
		// Record requests to different endpoints
		statisticsService.recordRequest("GET", "/api/v1/filters", 50, 200, 100, 500);
		statisticsService.recordRequest("GET", "/api/v1/filters/test", 60, 200, 100, 500);
		statisticsService.recordRequest("GET", "/api/v1/admin", 40, 200, 100, 500);
		
		// Get stats for endpoints matching pattern
		given().when().get("/api/v1/admin/endpoints/statistics-by-pattern?pattern=/api/v1/filters")
				.then().statusCode(200).body("size()", greaterThanOrEqualTo(1));
	}
	
	@Test
	public void testResetAllStatistics() {
		// Record some requests
		statisticsService.recordRequest("GET", "/test", 50, 200, 100, 500);
		
		// Verify statistics exist
		given().when().get("/api/v1/admin/endpoints/count").then().statusCode(200)
				.body("totalUniqueEndpointsAccessed", equalTo(1));
		
		// Reset all statistics
		given().when().post("/api/v1/admin/endpoints/statistics/reset").then().statusCode(200);
		
		// Verify statistics are cleared
		given().when().get("/api/v1/admin/endpoints/count").then().statusCode(200)
				.body("totalUniqueEndpointsAccessed", equalTo(0));
	}
	
	@Test
	public void testResetSpecificEndpointStatistics() {
		// Record requests to multiple endpoints
		statisticsService.recordRequest("GET", "/endpoint1", 50, 200, 100, 500);
		statisticsService.recordRequest("GET", "/endpoint2", 50, 200, 100, 500);
		
		// Verify both exist
		given().when().get("/api/v1/admin/endpoints/count").then().statusCode(200)
				.body("totalUniqueEndpointsAccessed", equalTo(2));
		
		// Reset only endpoint1
		given().when().post("/api/v1/admin/endpoints/statistics/reset/GET/endpoint1").then()
				.statusCode(200);
		
		// endpoint1 should be gone
		given().when().get("/api/v1/admin/endpoints/statistics/GET/endpoint1").then()
				.statusCode(404);
		
		// endpoint2 should still exist
		given().when().get("/api/v1/admin/endpoints/statistics/GET/endpoint2").then()
				.statusCode(200);
	}
	
	@Test
	public void testMultipleHTTPMethods() {
		// Record requests with different HTTP methods
		statisticsService.recordRequest("GET", "/resource", 50, 200, 100, 500);
		statisticsService.recordRequest("POST", "/resource", 60, 201, 150, 600);
		statisticsService.recordRequest("PUT", "/resource", 70, 200, 200, 700);
		statisticsService.recordRequest("DELETE", "/resource", 40, 204, 100, 100);
		
		// All 4 should be tracked as separate endpoints
		given().when().get("/api/v1/admin/endpoints/count").then().statusCode(200)
				.body("totalUniqueEndpointsAccessed", equalTo(4));
		
		// Each should have correct status code
		given().when().get("/api/v1/admin/endpoints/statistics/GET/resource").then().statusCode(200)
				.body("statusCodeDistribution.'200'", equalTo(1));
		
		given().when().get("/api/v1/admin/endpoints/statistics/POST/resource").then()
				.statusCode(200).body("statusCodeDistribution.'201'", equalTo(1));
	}
	
	@Test
	public void testByteTracking() {
		// Record requests with specific byte counts
		statisticsService.recordRequest("POST", "/api/upload", 100, 200, 5000, 1000);
		statisticsService.recordRequest("POST", "/api/upload", 110, 200, 6000, 1100);
		
		given().when().get("/api/v1/admin/endpoints/statistics/POST/api/upload").then()
				.statusCode(200).body("totalBytesIn", equalTo(11000))
				.body("totalBytesOut", equalTo(2100));
	}
}

