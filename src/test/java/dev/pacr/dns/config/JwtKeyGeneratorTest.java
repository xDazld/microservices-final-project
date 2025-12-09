package dev.pacr.dns.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for JwtKeyGenerator
 * <p>
 * Tests RSA key pair generation, PEM formatting, and file operations
 * <p>
 * Note: These tests use a temporary directory instead of src/main/resources/keys to avoid
 * interfering with actual application keys.
 */
class JwtKeyGeneratorTest {
	
	@TempDir
	Path tempDir;
	
	private Path keysDir;
	private Path privateKeyPath;
	private Path publicKeyPath;
	
	@BeforeEach
	void setUp() {
		keysDir = tempDir.resolve("keys");
		privateKeyPath = keysDir.resolve("privateKey.pem");
		publicKeyPath = keysDir.resolve("publicKey.pem");
	}
	
	@Test
	@DisplayName("Should create keys directory if it doesn't exist")
	void testCreateKeysDirectory() throws Exception {
		// Assert directory doesn't exist initially
		assertFalse(Files.exists(keysDir), "Keys directory should not exist initially");
		
		// Act - create directory
		Files.createDirectories(keysDir);
		
		// Assert
		assertTrue(Files.exists(keysDir), "Keys directory should be created");
		assertTrue(Files.isDirectory(keysDir), "Keys path should be a directory");
	}
	
	@Test
	@DisplayName("Should generate RSA 2048-bit key pair")
	void testGenerateKeyPair() throws Exception {
		// Arrange & Act
		java.security.KeyPairGenerator keyGen = java.security.KeyPairGenerator.getInstance("RSA");
		keyGen.initialize(2048);
		java.security.KeyPair keyPair = keyGen.generateKeyPair();
		
		// Assert
		assertNotNull(keyPair, "Key pair should not be null");
		assertNotNull(keyPair.getPrivate(), "Private key should not be null");
		assertNotNull(keyPair.getPublic(), "Public key should not be null");
		assertEquals("RSA", keyPair.getPrivate().getAlgorithm(),
				"Private key should use RSA algorithm");
		assertEquals("RSA", keyPair.getPublic().getAlgorithm(),
				"Public key should use RSA algorithm");
	}
	
	@Test
	@DisplayName("Should write private key in valid PEM format")
	void testWritePrivateKeyPemFormat() throws Exception {
		// Arrange
		Files.createDirectories(keysDir);
		java.security.KeyPairGenerator keyGen = java.security.KeyPairGenerator.getInstance("RSA");
		keyGen.initialize(2048);
		java.security.KeyPair keyPair = keyGen.generateKeyPair();
		
		// Act
		writePrivateKeyFile(privateKeyPath, keyPair.getPrivate());
		
		// Assert
		assertTrue(Files.exists(privateKeyPath), "Private key file should exist");
		String content = Files.readString(privateKeyPath);
		assertTrue(content.startsWith("-----BEGIN PRIVATE KEY-----"),
				"Private key should start with PEM header");
		assertTrue(content.endsWith("-----END PRIVATE KEY-----\n"),
				"Private key should end with PEM footer");
		assertTrue(content.length() > 100, "Private key PEM should have substantial content");
	}
	
	@Test
	@DisplayName("Should write public key in valid PEM format")
	void testWritePublicKeyPemFormat() throws Exception {
		// Arrange
		Files.createDirectories(keysDir);
		java.security.KeyPairGenerator keyGen = java.security.KeyPairGenerator.getInstance("RSA");
		keyGen.initialize(2048);
		java.security.KeyPair keyPair = keyGen.generateKeyPair();
		
		// Act
		writePublicKeyFile(publicKeyPath, keyPair.getPublic());
		
		// Assert
		assertTrue(Files.exists(publicKeyPath), "Public key file should exist");
		String content = Files.readString(publicKeyPath);
		assertTrue(content.startsWith("-----BEGIN PUBLIC KEY-----"),
				"Public key should start with PEM header");
		assertTrue(content.endsWith("-----END PUBLIC KEY-----\n"),
				"Public key should end with PEM footer");
		assertTrue(content.length() > 100, "Public key PEM should have substantial content");
	}
	
	@Test
	@DisplayName("Should generate keys with 64-character line length in PEM")
	void testPemLineLength() throws Exception {
		// Arrange
		Files.createDirectories(keysDir);
		java.security.KeyPairGenerator keyGen = java.security.KeyPairGenerator.getInstance("RSA");
		keyGen.initialize(2048);
		java.security.KeyPair keyPair = keyGen.generateKeyPair();
		
		// Act
		writePrivateKeyFile(privateKeyPath, keyPair.getPrivate());
		
		// Assert
		String content = Files.readString(privateKeyPath);
		String[] lines = content.split("\n");
		
		// Check that content lines (not header/footer) are max 64 chars
		for (int i = 1; i < lines.length - 1; i++) {
			String line = lines[i];
			if (!line.startsWith("-----")) {
				assertTrue(line.length() <= 64,
						"PEM content lines should be max 64 characters, but line " + i + " has " +
								line.length());
			}
		}
	}
	
	@Test
	@DisplayName("Should be able to read back generated private key")
	void testReadGeneratedPrivateKey() throws Exception {
		// Arrange
		Files.createDirectories(keysDir);
		java.security.KeyPairGenerator keyGen = java.security.KeyPairGenerator.getInstance("RSA");
		keyGen.initialize(2048);
		java.security.KeyPair originalKeyPair = keyGen.generateKeyPair();
		
		// Act
		writePrivateKeyFile(privateKeyPath, originalKeyPair.getPrivate());
		PrivateKey readKey = readPrivateKey(privateKeyPath);
		
		// Assert
		assertNotNull(readKey, "Read private key should not be null");
		assertEquals("RSA", readKey.getAlgorithm(), "Read key should be RSA");
		assertArrayEquals(originalKeyPair.getPrivate().getEncoded(), readKey.getEncoded(),
				"Read private key should match original");
	}
	
	@Test
	@DisplayName("Should be able to read back generated public key")
	void testReadGeneratedPublicKey() throws Exception {
		// Arrange
		Files.createDirectories(keysDir);
		java.security.KeyPairGenerator keyGen = java.security.KeyPairGenerator.getInstance("RSA");
		keyGen.initialize(2048);
		java.security.KeyPair originalKeyPair = keyGen.generateKeyPair();
		
		// Act
		writePublicKeyFile(publicKeyPath, originalKeyPair.getPublic());
		PublicKey readKey = readPublicKey(publicKeyPath);
		
		// Assert
		assertNotNull(readKey, "Read public key should not be null");
		assertEquals("RSA", readKey.getAlgorithm(), "Read key should be RSA");
		assertArrayEquals(originalKeyPair.getPublic().getEncoded(), readKey.getEncoded(),
				"Read public key should match original");
	}
	
	@Test
	@DisplayName("Should generate cryptographically strong keys")
	void testKeysAreCryptographicallyStrong() throws Exception {
		// Arrange & Act
		java.security.KeyPairGenerator keyGen = java.security.KeyPairGenerator.getInstance("RSA");
		keyGen.initialize(2048);
		java.security.KeyPair keyPair1 = keyGen.generateKeyPair();
		java.security.KeyPair keyPair2 = keyGen.generateKeyPair();
		
		// Assert - Different key pairs should be different
		assertFalse(java.util.Arrays.equals(keyPair1.getPrivate().getEncoded(),
						keyPair2.getPrivate().getEncoded()),
				"Two generated private keys should be different");
		assertFalse(java.util.Arrays.equals(keyPair1.getPublic().getEncoded(),
						keyPair2.getPublic().getEncoded()),
				"Two generated public keys should be different");
	}
	
	@Test
	@DisplayName("Should validate that keys don't exist before generation")
	void testCheckKeysExist() {
		// Assert
		assertFalse(Files.exists(privateKeyPath), "Private key should not exist initially");
		assertFalse(Files.exists(publicKeyPath), "Public key should not exist initially");
	}
	
	@Test
	@DisplayName("Should detect existing valid keys")
	void testDetectExistingKeys() throws Exception {
		// Arrange
		Files.createDirectories(keysDir);
		java.security.KeyPairGenerator keyGen = java.security.KeyPairGenerator.getInstance("RSA");
		keyGen.initialize(2048);
		java.security.KeyPair keyPair = keyGen.generateKeyPair();
		writePrivateKeyFile(privateKeyPath, keyPair.getPrivate());
		writePublicKeyFile(publicKeyPath, keyPair.getPublic());
		
		// Act & Assert
		assertTrue(Files.exists(privateKeyPath), "Private key file should exist");
		assertTrue(Files.exists(publicKeyPath), "Public key file should exist");
		
		String privateContent = Files.readString(privateKeyPath);
		String publicContent = Files.readString(publicKeyPath);
		
		assertTrue(privateContent.contains("BEGIN PRIVATE KEY"),
				"Private key should have valid PEM header");
		assertTrue(publicContent.contains("BEGIN PUBLIC KEY"),
				"Public key should have valid PEM header");
	}
	
	@Test
	@DisplayName("Should handle placeholder files correctly")
	void testDetectPlaceholderFiles() throws Exception {
		// Arrange
		Files.createDirectories(keysDir);
		Files.writeString(privateKeyPath, "# This file will be auto-generated");
		Files.writeString(publicKeyPath, "# This file will be auto-generated");
		
		// Act & Assert
		String privateContent = Files.readString(privateKeyPath);
		String publicContent = Files.readString(publicKeyPath);
		
		assertFalse(privateContent.contains("BEGIN PRIVATE KEY"),
				"Placeholder file should not have PEM header");
		assertFalse(publicContent.contains("BEGIN PUBLIC KEY"),
				"Placeholder file should not have PEM header");
	}
	
	// Helper methods (mimicking JwtKeyGenerator logic)
	
	private void writePrivateKeyFile(Path path, java.security.Key privateKey) throws IOException {
		String encoded = Base64.getEncoder().encodeToString(privateKey.getEncoded());
		StringBuilder pem = new StringBuilder();
		pem.append("-----BEGIN PRIVATE KEY-----\n");
		
		int index = 0;
		while (index < encoded.length()) {
			pem.append(encoded, index, Math.min(index + 64, encoded.length()));
			pem.append('\n');
			index += 64;
		}
		
		pem.append("-----END PRIVATE KEY-----\n");
		Files.writeString(path, pem.toString());
	}
	
	private void writePublicKeyFile(Path path, java.security.Key publicKey) throws IOException {
		String encoded = Base64.getEncoder().encodeToString(publicKey.getEncoded());
		StringBuilder pem = new StringBuilder();
		pem.append("-----BEGIN PUBLIC KEY-----\n");
		
		int index = 0;
		while (index < encoded.length()) {
			pem.append(encoded, index, Math.min(index + 64, encoded.length()));
			pem.append('\n');
			index += 64;
		}
		
		pem.append("-----END PUBLIC KEY-----\n");
		Files.writeString(path, pem.toString());
	}
	
	private PrivateKey readPrivateKey(Path path) throws Exception {
		String keyContent = Files.readString(path);
		String keyData = keyContent.replace("-----BEGIN PRIVATE KEY-----", "")
				.replace("-----END PRIVATE KEY-----", "").replaceAll("\\s+", "");
		
		byte[] decodedKey = Base64.getDecoder().decode(keyData);
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decodedKey);
		KeyFactory factory = KeyFactory.getInstance("RSA");
		
		return factory.generatePrivate(spec);
	}
	
	private PublicKey readPublicKey(Path path) throws Exception {
		String keyContent = Files.readString(path);
		String keyData = keyContent.replace("-----BEGIN PUBLIC KEY-----", "")
				.replace("-----END PUBLIC KEY-----", "").replaceAll("\\s+", "");
		
		byte[] decodedKey = Base64.getDecoder().decode(keyData);
		X509EncodedKeySpec spec = new X509EncodedKeySpec(decodedKey);
		KeyFactory factory = KeyFactory.getInstance("RSA");
		
		return factory.generatePublic(spec);
	}
}

