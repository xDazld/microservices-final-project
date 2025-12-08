package dev.pacr.dns.api;

import io.smallrye.jwt.build.Jwt;
import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.time.Duration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Authentication Resource
 * <p>
 * Handles user authentication and JWT token generation
 */
@Path("/api/v1/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {
	
	private static final Logger LOG = Logger.getLogger(AuthResource.class);
	
	@ConfigProperty(name = "mp.jwt.verify.issuer", defaultValue = "https://dns-shield.local")
	String issuer;
	
	/**
	 * Login endpoint - generates JWT token for valid credentials
	 *
	 * @param request Login request with username and password
	 * @return JWT token and user info
	 */
	@POST
	@Path("/login")
	@PermitAll
	public Response login(LoginRequest request) {
		LOG.infof("Login attempt for user: %s", request.username);
		
		if (request.username == null || request.password == null) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(Map.of("error", "Username and password are required")).build();
		}
		
		// Validate credentials
		UserInfo userInfo = validateCredentials(request.username, request.password);
		
		if (userInfo == null) {
			LOG.warnf("Failed login attempt for user: %s", request.username);
			return Response.status(Response.Status.UNAUTHORIZED)
					.entity(Map.of("error", "Invalid username or password")).build();
		}
		
		// Generate JWT token
		try {
			String token = generateToken(userInfo);
			
			LOG.infof("Successful login for user: %s with role: %s", userInfo.username,
					userInfo.role);
			
			return Response.ok(
					Map.of("token", token, "username", userInfo.username, "role", userInfo.role,
							"expiresIn", 3600 // 1 hour
					)).build();
			
		} catch (Exception e) {
			LOG.errorf(e, "Error generating token for user: %s", request.username);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(Map.of("error", "Failed to generate authentication token")).build();
		}
	}
	
	/**
	 * Validate user credentials
	 * <p>
	 * In production, this should query a database with hashed passwords For development, we use
	 * hardcoded credentials
	 */
	private UserInfo validateCredentials(String username, String password) {
		// TODO: In production, replace with database lookup and password hash verification
		
		// Development credentials
		if ("admin".equals(username) && "admin".equals(password)) {
			return new UserInfo("admin", "admin");
		}
		
		if ("user".equals(username) && "user".equals(password)) {
			return new UserInfo("user", "user");
		}
		
		return null;
	}
	
	/**
	 * Generate JWT token for authenticated user
	 */
	private String generateToken(UserInfo userInfo) {
		Set<String> groups = new HashSet<>();
		groups.add(userInfo.role);
		
		return Jwt.issuer(issuer).upn(userInfo.username).groups(groups)
				.expiresIn(Duration.ofHours(1)).sign();
	}
	
	/**
	 * Login request model
	 */
	public static class LoginRequest {
		public String username;
		public String password;
	}
	
	/**
	 * User information model
	 */
		private record UserInfo(String username, String role) {
	}
}

