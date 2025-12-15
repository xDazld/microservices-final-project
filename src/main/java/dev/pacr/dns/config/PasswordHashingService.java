package dev.pacr.dns.config;

import jakarta.enterprise.context.ApplicationScoped;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Service for hashing and verifying passwords using SHA-256 with salt and multiple iterations
 *
 * @author Patrick Rafferty
 */
@ApplicationScoped
public class PasswordHashingService {
	
	/**
	 * Number of iterations for password hashing
	 */
	private static final int ITERATIONS = 10000;
	
	/**
	 * Salt length in bytes
	 */
	private static final int SALT_LENGTH = 32;
	
	/**
	 * Hashing algorithm
	 */
	private static final String ALGORITHM = "SHA-256";
	
	/**
	 * Hash a password using SHA-256 with salt and multiple iterations
	 *
	 * @param password the plain text password
	 * @return the hashed password with salt (format: salt$hash)
	 */
	public String hashPassword(String password) {
		try {
			byte[] salt = new byte[SALT_LENGTH];
			SecureRandom random = new SecureRandom();
			random.nextBytes(salt);
			
			byte[] hash = hashPasswordWithSalt(password.toCharArray(), salt);
			
			String saltBase64 = Base64.getEncoder().encodeToString(salt);
			String hashBase64 = Base64.getEncoder().encodeToString(hash);
			
			return saltBase64 + '$' + hashBase64;
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Error hashing password", e);
		}
	}
	
	/**
	 * Hash password with salt using multiple iterations
	 *
	 * @param password the password chars
	 * @param salt     the salt bytes
	 * @return the hashed bytes
	 * @throws NoSuchAlgorithmException if algorithm is not available
	 */
	private byte[] hashPasswordWithSalt(char[] password, byte[] salt)
			throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
		
		// Add salt to digest
		digest.update(salt);
		
		// Perform iterations
		byte[] hash = new String(password).getBytes(StandardCharsets.UTF_8);
		for (int i = 0; i < ITERATIONS; i++) {
			digest.update(hash);
			hash = digest.digest();
			digest.reset();
		}
		
		return hash;
	}
	
	/**
	 * Verify a password against a hash
	 *
	 * @param password the plain text password
	 * @param hash     the hashed password (format: salt$hash)
	 * @return true if password matches, false otherwise
	 */
	public boolean verifyPassword(String password, String hash) {
		try {
			String[] parts = hash.split("\\$");
			if (parts.length != 2) {
				return false;
			}
			
			byte[] salt = Base64.getDecoder().decode(parts[0]);
			byte[] storedHash = Base64.getDecoder().decode(parts[1]);
			
			byte[] computedHash = hashPasswordWithSalt(password.toCharArray(), salt);
			
			return constantTimeEquals(storedHash, computedHash);
		} catch (NoSuchAlgorithmException | IllegalArgumentException e) {
			return false;
		}
	}
	
	/**
	 * Constant time comparison to prevent timing attacks
	 *
	 * @param a first byte array
	 * @param b second byte array
	 * @return true if arrays are equal, false otherwise
	 */
	private boolean constantTimeEquals(byte[] a, byte[] b) {
		if (a.length != b.length) {
			return false;
		}
		int result = 0;
		for (int i = 0; i < a.length; i++) {
			result |= a[i] ^ b[i];
		}
		return result == 0;
	}
}

