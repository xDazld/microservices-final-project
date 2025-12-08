package dev.pacr.dns.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Collection;

/**
 * JWT Signing Service
 * <p>
 * Handles JWT token generation with manual signing using loaded private key.
 *
 * @author DNS Shield Team
 */
@ApplicationScoped
public class JwtSigningService {
	
	/**
	 * Logger instance
	 */
	private static final Logger LOG = Logger.getLogger(JwtSigningService.class);
	
	/**
	 * JWT expiration time in seconds (1 hour)
	 */
	private static final long JWT_EXPIRATION_SECONDS = 3600;
	
	/**
	 * Cached private key
	 */
	private PrivateKey cachedPrivateKey;
	
	/**
	 * Generate a JWT token with the provided claims.
	 *
	 * @param issuer   the token issuer
	 * @param username the username (upn claim)
	 * @param groups   the user groups/roles
	 * @return the signed JWT token
	 * @throws RuntimeException if token generation fails
	 */
	public String generateToken(String issuer, String username, Collection<String> groups) {
		try {
			PrivateKey privateKey = getPrivateKey();
			
			// Create JWT header
			JsonObject header =
					Json.createObjectBuilder().add("alg", "RS256").add("typ", "JWT").build();
			
			// Create JWT payload
			long now = System.currentTimeMillis() / 1000;
			long expiration = now + JWT_EXPIRATION_SECONDS;
			
			JsonObject payload =
					Json.createObjectBuilder().add("sub", username).add("upn", username)
							.add("iss", issuer).add("iat", now).add("exp", expiration)
							.add("groups", Json.createArrayBuilder(groups).build()).build();
			
			// Encode header and payload
			String encodedHeader = Base64.getUrlEncoder().withoutPadding()
					.encodeToString(header.toString().getBytes(StandardCharsets.UTF_8));
			String encodedPayload = Base64.getUrlEncoder().withoutPadding()
					.encodeToString(payload.toString().getBytes(StandardCharsets.UTF_8));
			
			// Create signature input
			String signatureInput = encodedHeader + '.' + encodedPayload;
			
			// Sign
			Signature signature = Signature.getInstance("SHA256withRSA");
			signature.initSign(privateKey);
			signature.update(signatureInput.getBytes(StandardCharsets.UTF_8));
			byte[] signatureBytes = signature.sign();
			
			String encodedSignature =
					Base64.getUrlEncoder().withoutPadding().encodeToString(signatureBytes);
			
			return signatureInput + '.' + encodedSignature;
		} catch (RuntimeException e) {
			LOG.errorf(e, "Failed to generate JWT token for user: %s", username);
			throw e;
		} catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException e) {
			LOG.errorf(e, "Failed to generate JWT token for user: %s", username);
			throw new RuntimeException("Failed to generate JWT token", e);
		}
	}
	
	/**
	 * Get the private key, loading and caching it if necessary.
	 *
	 * @return the loaded private key
	 * @throws RuntimeException if loading or parsing fails
	 */
	private PrivateKey getPrivateKey() {
		if (cachedPrivateKey != null) {
			return cachedPrivateKey;
		}
		
		// Try to load from the generated keys directory
		String keyPath = "src/main/resources/keys/privateKey.pem";
		
		try {
			String keyContent = Files.readString(Paths.get(keyPath));
			cachedPrivateKey = parsePemPrivateKey(keyContent);
			LOG.infof("Loaded private key from: %s", keyPath);
			return cachedPrivateKey;
		} catch (IOException e) {
			LOG.errorf(e, "Failed to load private key from %s", keyPath);
			throw new RuntimeException("Private key not found at " + keyPath, e);
		}
	}
	
	/**
	 * Parse a PEM-formatted private key.
	 *
	 * @param keyContent the PEM key content
	 * @return the parsed private key
	 * @throws RuntimeException if parsing fails
	 */
	private PrivateKey parsePemPrivateKey(String keyContent) {
		try {
			// Remove PEM headers and newlines
			String keyData = keyContent.replace("-----BEGIN PRIVATE KEY-----", "")
					.replace("-----END PRIVATE KEY-----", "")
					.replace("-----BEGIN RSA PRIVATE KEY-----", "")
					.replace("-----END RSA PRIVATE KEY-----", "").replaceAll("\\s+", "");
			
			// Decode base64
			byte[] decodedKey = Base64.getDecoder().decode(keyData);
			
			// Parse as PKCS#8
			KeySpec spec = new PKCS8EncodedKeySpec(decodedKey);
			KeyFactory factory = KeyFactory.getInstance("RSA");
			
			return factory.generatePrivate(spec);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new RuntimeException("Failed to parse private key", e);
		}
	}
}

