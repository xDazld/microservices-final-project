package dev.pacr.dns.api;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;

/**
 * Integration test for JWT Authentication
 * <p>
 * Tests the complete authentication flow from login to accessing protected endpoints
 */
@QuarkusTest
class JwtAuthenticationIntegrationTest {
	
	private static final String LOGIN_ENDPOINT = "/api/v1/auth/login";
	private static final String PROTECTED_ENDPOINT = "/api/v1/admin/stats";
	
	@Test
	@DisplayName("Should login successfully with valid credentials")
	void testLoginSuccess() {
		given().contentType(ContentType.JSON)
				.body("{\"username\":\"admin\",\"password\":\"admin\"}").when().post(LOGIN_ENDPOINT)
				.then().statusCode(200).body("token", notNullValue()).body("token", not(""))
				.body("username", equalTo("admin")).body("role", equalTo("admin"))
				.body("expiresIn", equalTo(3600));
	}
	
	@Test
	@DisplayName("Should reject login with invalid credentials")
	void testLoginInvalidCredentials() {
		given().contentType(ContentType.JSON)
				.body("{\"username\":\"admin\",\"password\":\"wrongpassword\"}").when()
				.post(LOGIN_ENDPOINT).then().statusCode(401).body("error", notNullValue());
	}
	
	@Test
	@DisplayName("Should reject login with missing username")
	void testLoginMissingUsername() {
		given().contentType(ContentType.JSON).body("{\"password\":\"admin\"}").when()
				.post(LOGIN_ENDPOINT).then().statusCode(400)
				.body("error", containsString("required"));
	}
	
	@Test
	@DisplayName("Should reject login with missing password")
	void testLoginMissingPassword() {
		given().contentType(ContentType.JSON).body("{\"username\":\"admin\"}").when()
				.post(LOGIN_ENDPOINT).then().statusCode(400)
				.body("error", containsString("required"));
	}
	
	@Test
	@DisplayName("Should reject login with empty credentials")
	void testLoginEmptyCredentials() {
		given().contentType(ContentType.JSON).body("{}").when().post(LOGIN_ENDPOINT).then()
				.statusCode(400);
	}
	
	@Test
	@DisplayName("Should generate JWT token with valid format")
	void testTokenFormat() {
		String token = given().contentType(ContentType.JSON)
				.body("{\"username\":\"admin\",\"password\":\"admin\"}").when().post(LOGIN_ENDPOINT)
				.then().statusCode(200).extract().path("token");
		
		// JWT should have 3 parts separated by dots
		String[] parts = token.split("\\.");
		assert parts.length == 3 : "JWT should have 3 parts";
		
		// Each part should be base64 encoded
		for (String part : parts) {
			assert !part.isEmpty() : "JWT parts should not be empty";
		}
	}
	
	@Test
	@DisplayName("Should access protected endpoint with valid token")
	void testAccessProtectedEndpointWithToken() {
		// First, login to get token
		String token = given().contentType(ContentType.JSON)
				.body("{\"username\":\"admin\",\"password\":\"admin\"}").when().post(LOGIN_ENDPOINT)
				.then().statusCode(200).extract().path("token");
		
		// Then, access protected endpoint with token
		given().header("Authorization", "Bearer " + token).when().get(PROTECTED_ENDPOINT).then()
				.statusCode(200).body("totalQueries", notNullValue());
	}
	
	@Test
	@DisplayName("Should reject protected endpoint without token")
	void testAccessProtectedEndpointWithoutToken() {
		given().when().get(PROTECTED_ENDPOINT).then().statusCode(401);
	}
	
	@Test
	@DisplayName("Should reject protected endpoint with invalid token")
	void testAccessProtectedEndpointWithInvalidToken() {
		given().header("Authorization", "Bearer invalid.token.here").when().get(PROTECTED_ENDPOINT)
				.then().statusCode(401);
	}
	
	@Test
	@DisplayName("Should reject protected endpoint with malformed token")
	void testAccessProtectedEndpointWithMalformedToken() {
		given().header("Authorization", "Bearer not-a-jwt-token").when().get(PROTECTED_ENDPOINT)
				.then().statusCode(401);
	}
	
	@Test
	@DisplayName("Should reject protected endpoint with missing Bearer prefix")
	void testAccessProtectedEndpointWithoutBearerPrefix() {
		// First, login to get token
		String token = given().contentType(ContentType.JSON)
				.body("{\"username\":\"admin\",\"password\":\"admin\"}").when().post(LOGIN_ENDPOINT)
				.then().statusCode(200).extract().path("token");
		
		// Try to access with token but no Bearer prefix
		given().header("Authorization", token).when().get(PROTECTED_ENDPOINT).then()
				.statusCode(401);
	}
	
	@Test
	@DisplayName("Should allow user role to access protected endpoint")
	void testUserRoleAccess() {
		// Login as user
		String token = given().contentType(ContentType.JSON)
				.body("{\"username\":\"user\",\"password\":\"user\"}").when().post(LOGIN_ENDPOINT)
				.then().statusCode(200).body("role", equalTo("user")).extract().path("token");
		
		// Access endpoint that allows user role
		given().header("Authorization", "Bearer " + token).when().get(PROTECTED_ENDPOINT).then()
				.statusCode(200);
	}
	
	@Test
	@DisplayName("Should generate different tokens for different users")
	void testDifferentTokensForDifferentUsers() {
		String adminToken = given().contentType(ContentType.JSON)
				.body("{\"username\":\"admin\",\"password\":\"admin\"}").when().post(LOGIN_ENDPOINT)
				.then().statusCode(200).extract().path("token");
		
		String userToken = given().contentType(ContentType.JSON)
				.body("{\"username\":\"user\",\"password\":\"user\"}").when().post(LOGIN_ENDPOINT)
				.then().statusCode(200).extract().path("token");
		
		assert !adminToken.equals(userToken) : "Tokens for different users should be different";
	}
	
	@Test
	@DisplayName("Should include correct claims in JWT token")
	void testTokenClaims() {
		String token = given().contentType(ContentType.JSON)
				.body("{\"username\":\"admin\",\"password\":\"admin\"}").when().post(LOGIN_ENDPOINT)
				.then().statusCode(200).extract().path("token");
		
		// Decode payload (second part of JWT)
		String[] parts = token.split("\\.");
		String payload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
		
		// Verify claims
		assert payload.contains("\"sub\":\"admin\"") : "Token should have sub claim";
		assert payload.contains("\"upn\":\"admin\"") : "Token should have upn claim";
		assert payload.contains("\"iss\":\"https://dns-shield.local\"") :
				"Token should have correct issuer";
		assert payload.contains("\"groups\"") : "Token should have groups claim";
		assert payload.contains("\"exp\"") : "Token should have expiration";
		assert payload.contains("\"iat\"") : "Token should have issued at time";
	}
	
	@Test
	@DisplayName("Should have RS256 algorithm in JWT header")
	void testTokenAlgorithm() {
		String token = given().contentType(ContentType.JSON)
				.body("{\"username\":\"admin\",\"password\":\"admin\"}").when().post(LOGIN_ENDPOINT)
				.then().statusCode(200).extract().path("token");
		
		// Decode header (first part of JWT)
		String[] parts = token.split("\\.");
		String header = new String(Base64.getUrlDecoder().decode(parts[0]), StandardCharsets.UTF_8);
		
		// Verify algorithm
		assert header.contains("\"alg\":\"RS256\"") : "Token should use RS256 algorithm";
		assert header.contains("\"typ\":\"JWT\"") : "Token should have JWT type";
	}
	
	@Test
	@DisplayName("Should handle concurrent login requests")
	void testConcurrentLogins() throws InterruptedException {
		int numberOfThreads = 10;
		Thread[] threads = new Thread[numberOfThreads];
		boolean[] results = new boolean[numberOfThreads];
		
		for (int i = 0; i < numberOfThreads; i++) {
			final int index = i;
			threads[i] = new Thread(() -> {
				try {
					given().contentType(ContentType.JSON)
							.body("{\"username\":\"admin\",\"password\":\"admin\"}").when()
							.post(LOGIN_ENDPOINT).then().statusCode(200);
					results[index] = true;
				} catch (Exception e) {
					results[index] = false;
				}
			});
			threads[i].start();
		}
		
		// Wait for all threads to complete
		for (Thread thread : threads) {
			thread.join();
		}
		
		// Verify all succeeded
		for (boolean result : results) {
			assert result : "All concurrent login requests should succeed";
		}
	}
}

