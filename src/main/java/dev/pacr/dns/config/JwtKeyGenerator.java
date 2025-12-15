package dev.pacr.dns.config;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
	  * JWT Key Generator
  * <p>
  * Automatically generates RSA key pairs for JWT signing on application startup if they don't
  * already exist.
  *
  * @author Patrick Rafferty
  */
@ApplicationScoped
public class JwtKeyGenerator {
	
	/**
	  * Logger instance
	  */
	private static final Logger LOG = Logger.getLogger(JwtKeyGenerator.class);
	
	/**
	  * Directory to store keys
	  */
	private static final String KEYS_DIR = "src/main/resources/keys";
	
	/**
	  * Private key filename
	  */
	private static final String PRIVATE_KEY_FILE = "privateKey.pem";
	
	/**
	  * Public key filename
	  */
	private static final String PUBLIC_KEY_FILE = "publicKey.pem";
	
	/**
	  * RSA key size in bits
	  */
	private static final int RSA_KEY_SIZE = 2048;
	
	/**
	  * PEM line length for key encoding
	  */
	private static final int PEM_LINE_LENGTH = 64;
	
	/**
	  * Initialize JWT keys on application startup.
	  *
	  * @param ev The startup event
	  */
	void onStart(@Observes StartupEvent ev) {
		try {
			initializeKeys();
		} catch (IOException | NoSuchAlgorithmException e) {
			LOG.errorf(e, "Failed to initialize JWT keys");
			throw new RuntimeException("Failed to initialize JWT keys", e);
		}
	}
	
	/**
	  * Initialize keys if they don't exist.
	  *
	  * @throws IOException              if file writing fails
	  * @throws NoSuchAlgorithmException if RSA algorithm is not available
	  */
	private void initializeKeys() throws IOException, NoSuchAlgorithmException {
		Path keysPath = Paths.get(KEYS_DIR);
		Path privateKeyPath = keysPath.resolve(PRIVATE_KEY_FILE);
		Path publicKeyPath = keysPath.resolve(PUBLIC_KEY_FILE);
		
		// Check if keys already exist and are valid (not just placeholder files)
		if (Files.exists(privateKeyPath) && Files.exists(publicKeyPath)) {
			String content = Files.readString(privateKeyPath);
			if (content.contains("BEGIN PRIVATE KEY") ||
					content.contains("BEGIN RSA PRIVATE KEY")) {
				LOG.info("JWT keys already exist, skipping generation");
				return;
			}
		}
		
		// Create keys directory if it doesn't exist
		if (!Files.exists(keysPath)) {
			Files.createDirectories(keysPath);
			LOG.infof("Created keys directory: %s", KEYS_DIR);
		}
		
		// Generate key pair
		LOG.info("Generating RSA 2048-bit key pair for JWT signing...");
		KeyPair keyPair = generateKeyPair();
		
		// Write private key
		writePrivateKeyFile(privateKeyPath, keyPair.getPrivate());
		LOG.infof("Private key written to: %s", privateKeyPath);
		
		// Write public key
		writePublicKeyFile(publicKeyPath, keyPair.getPublic());
		LOG.infof("Public key written to: %s", publicKeyPath);
		
		LOG.info("JWT keys initialized successfully");
	}
	
	/**
	  * Generate RSA 2048-bit key pair.
	  *
	  * @return the generated key pair
	  * @throws NoSuchAlgorithmException if RSA algorithm is not available
	  */
	private KeyPair generateKeyPair() throws NoSuchAlgorithmException {
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
		keyGen.initialize(RSA_KEY_SIZE);
		return keyGen.generateKeyPair();
	}
	
	/**
	  * Write private key to PEM file in PKCS#8 format.
	  *
	  * @param path       the path to write to
	  * @param privateKey the private key to write
	  * @throws IOException if writing fails
	  */
	private void writePrivateKeyFile(Path path, Key privateKey) throws IOException {
		String encoded = Base64.getEncoder().encodeToString(privateKey.getEncoded());
		StringBuilder pem = new StringBuilder();
		pem.append("-----BEGIN PRIVATE KEY-----\n");
		
		// Split into 64-character lines
		int index = 0;
		while (index < encoded.length()) {
			pem.append(encoded, index, Math.min(index + PEM_LINE_LENGTH, encoded.length()));
			pem.append('\n');
			index += PEM_LINE_LENGTH;
		}
		
		pem.append("-----END PRIVATE KEY-----\n");
		
		Files.writeString(path, pem.toString(), StandardCharsets.UTF_8);
	}
	
	/**
	  * Write public key to PEM file.
	  *
	  * @param path      the path to write to
	  * @param publicKey the public key to write
	  * @throws IOException if writing fails
	  */
	private void writePublicKeyFile(Path path, Key publicKey) throws IOException {
		String encoded = Base64.getEncoder().encodeToString(publicKey.getEncoded());
		StringBuilder pem = new StringBuilder();
		pem.append("-----BEGIN PUBLIC KEY-----\n");
		
		// Split into 64-character lines
		int index = 0;
		while (index < encoded.length()) {
			pem.append(encoded, index, Math.min(index + PEM_LINE_LENGTH, encoded.length()));
			pem.append('\n');
			index += PEM_LINE_LENGTH;
		}
		
		pem.append("-----END PUBLIC KEY-----\n");
		
		Files.writeString(path, pem.toString(), StandardCharsets.UTF_8);
	}
}

