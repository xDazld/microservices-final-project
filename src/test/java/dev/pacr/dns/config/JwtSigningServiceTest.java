package dev.pacr.dns.config;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for JwtSigningService
 * <p>
 * Tests JWT token generation, signing, and validation
 */
@QuarkusTest
class JwtSigningServiceTest {
	
	private static final String TEST_ISSUER = "https://dns-shield.local";
	private static final String TEST_USERNAME = "testuser";
	private static final String TEST_ROLE = "admin";
	@Inject
	JwtSigningService jwtSigningService;
	
	@Test
	@DisplayName("Should generate valid JWT token with correct claims")
	void testGenerateToken() {
		// Arrange
		Set<String> groups = new HashSet<>();
		groups.add(TEST_ROLE);
		
		// Act
		String token = jwtSigningService.generateToken(TEST_ISSUER, TEST_USERNAME, groups);
		
		// Assert
		assertNotNull(token, "Token should not be null");
		assertFalse(token.isEmpty(), "Token should not be empty");
		
		// JWT should have 3 parts (header.payload.signature)
		String[] parts = token.split("\\.");
		assertEquals(3, parts.length, "JWT should have 3 parts separated by dots");
		
		// Each part should be base64 encoded (not empty)
		for (String part : parts) {
			assertFalse(part.isEmpty(), "JWT part should not be empty");
		}
	}
	
	@Test
	@DisplayName("Should generate token with username in claims")
	void testTokenContainsUsername() {
		// Arrange
		Set<String> groups = new HashSet<>();
		groups.add(TEST_ROLE);
		
		// Act
		String token = jwtSigningService.generateToken(TEST_ISSUER, TEST_USERNAME, groups);
		String payload = decodePayload(token);
		
		// Assert
		assertTrue(payload.contains(TEST_USERNAME), "Token payload should contain username");
		assertTrue(payload.contains("\"upn\":\"" + TEST_USERNAME + '"'),
				"Token should have upn claim with username");
		assertTrue(payload.contains("\"sub\":\"" + TEST_USERNAME + '"'),
				"Token should have sub claim with username");
	}
	
	@Test
	@DisplayName("Should generate token with issuer in claims")
	void testTokenContainsIssuer() {
		// Arrange
		Set<String> groups = new HashSet<>();
		groups.add(TEST_ROLE);
		
		// Act
		String token = jwtSigningService.generateToken(TEST_ISSUER, TEST_USERNAME, groups);
		String payload = decodePayload(token);
		
		// Assert
		assertTrue(payload.contains(TEST_ISSUER), "Token payload should contain issuer");
		assertTrue(payload.contains("\"iss\":\"" + TEST_ISSUER + '"'),
				"Token should have iss claim with correct issuer");
	}
	
	@Test
	@DisplayName("Should generate token with groups in claims")
	void testTokenContainsGroups() {
		// Arrange
		Set<String> groups = new HashSet<>();
		groups.add("admin");
		groups.add("user");
		
		// Act
		String token = jwtSigningService.generateToken(TEST_ISSUER, TEST_USERNAME, groups);
		String payload = decodePayload(token);
		
		// Assert
		assertTrue(payload.contains("\"groups\""), "Token should have groups claim");
		assertTrue(payload.contains("admin"), "Token should contain admin group");
		assertTrue(payload.contains("user"), "Token should contain user group");
	}
	
	@Test
	@DisplayName("Should generate token with expiration time")
	void testTokenContainsExpiration() {
		// Arrange
		Set<String> groups = new HashSet<>();
		groups.add(TEST_ROLE);
		
		// Act
		String token = jwtSigningService.generateToken(TEST_ISSUER, TEST_USERNAME, groups);
		String payload = decodePayload(token);
		
		// Assert
		assertTrue(payload.contains("\"exp\""), "Token should have exp (expiration) claim");
		assertTrue(payload.contains("\"iat\""), "Token should have iat (issued at) claim");
	}
	
	@Test
	@DisplayName("Should generate token with RS256 algorithm in header")
	void testTokenHasCorrectAlgorithm() {
		// Arrange
		Set<String> groups = new HashSet<>();
		groups.add(TEST_ROLE);
		
		// Act
		String token = jwtSigningService.generateToken(TEST_ISSUER, TEST_USERNAME, groups);
		String header = decodeHeader(token);
		
		// Assert
		assertTrue(header.contains("\"alg\":\"RS256\""),
				"Token header should specify RS256 algorithm");
		assertTrue(header.contains("\"typ\":\"JWT\""), "Token header should specify JWT type");
	}
	
	@Test
	@DisplayName("Should generate different tokens for different users")
	void testDifferentTokensForDifferentUsers() {
		// Arrange
		Set<String> groups = new HashSet<>();
		groups.add(TEST_ROLE);
		
		// Act
		String token1 = jwtSigningService.generateToken(TEST_ISSUER, "user1", groups);
		String token2 = jwtSigningService.generateToken(TEST_ISSUER, "user2", groups);
		
		// Assert
		assertNotEquals(token1, token2, "Tokens for different users should be different");
	}
	
	@Test
	@DisplayName("Should generate different tokens at different times")
	void testDifferentTokensAtDifferentTimes() throws InterruptedException {
		// Arrange
		Set<String> groups = new HashSet<>();
		groups.add(TEST_ROLE);
		
		// Act
		String token1 = jwtSigningService.generateToken(TEST_ISSUER, TEST_USERNAME, groups);
		Thread.sleep(1000); // Wait 1 second to ensure different iat
		String token2 = jwtSigningService.generateToken(TEST_ISSUER, TEST_USERNAME, groups);
		
		// Assert
		assertNotEquals(token1, token2,
				"Tokens generated at different times should have different iat claims");
	}
	
	@Test
	@DisplayName("Should handle empty groups collection")
	void testEmptyGroups() {
		// Arrange
		Set<String> groups = new HashSet<>();
		
		// Act
		String token = jwtSigningService.generateToken(TEST_ISSUER, TEST_USERNAME, groups);
		
		// Assert
		assertNotNull(token, "Token should be generated even with empty groups");
		String payload = decodePayload(token);
		assertTrue(payload.contains("\"groups\":[]"), "Token should have empty groups array");
	}
	
	@Test
	@DisplayName("Should throw exception when issuer is null")
	void testNullIssuer() {
		// Arrange
		Set<String> groups = new HashSet<>();
		groups.add(TEST_ROLE);
		
		// Act & Assert
		assertThrows(Exception.class, () -> {
			jwtSigningService.generateToken(null, TEST_USERNAME, groups);
		}, "Should throw exception when issuer is null");
	}
	
	@Test
	@DisplayName("Should throw exception when username is null")
	void testNullUsername() {
		// Arrange
		Set<String> groups = new HashSet<>();
		groups.add(TEST_ROLE);
		
		// Act & Assert
		assertThrows(Exception.class, () -> {
			jwtSigningService.generateToken(TEST_ISSUER, null, groups);
		}, "Should throw exception when username is null");
	}
	
	@Test
	@DisplayName("Should handle multiple roles in groups")
	void testMultipleRoles() {
		// Arrange
		Set<String> groups = new HashSet<>();
		groups.add("admin");
		groups.add("user");
		groups.add("moderator");
		
		// Act
		String token = jwtSigningService.generateToken(TEST_ISSUER, TEST_USERNAME, groups);
		String payload = decodePayload(token);
		
		// Assert
		assertTrue(payload.contains("admin"), "Token should contain admin role");
		assertTrue(payload.contains("user"), "Token should contain user role");
		assertTrue(payload.contains("moderator"), "Token should contain moderator role");
	}
	
	@Test
	@DisplayName("Should generate consistent token structure")
	void testTokenStructure() {
		// Arrange
		Set<String> groups = new HashSet<>();
		groups.add(TEST_ROLE);
		
		// Act
		String token = jwtSigningService.generateToken(TEST_ISSUER, TEST_USERNAME, groups);
		
		// Assert
		// Token should start with eyJ (base64 of {"alg":...)
		assertTrue(token.startsWith("eyJ"), "JWT token should start with eyJ");
		
		// Token should have reasonable length (not too short, not too long)
		assertTrue(token.length() > 100, "JWT token should have substantial length");
		assertTrue(token.length() < 2000, "JWT token should not be excessively long");
	}
	
	/**
	 * Helper method to decode JWT header (first part)
	 */
	private String decodeHeader(String token) {
		String[] parts = token.split("\\.");
		return new String(Base64.getUrlDecoder().decode(parts[0]), StandardCharsets.UTF_8);
	}
	
	/**
	 * Helper method to decode JWT payload (second part)
	 */
	private String decodePayload(String token) {
		String[] parts = token.split("\\.");
		return new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
	}
}

