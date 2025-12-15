package dev.pacr.dns.api;

import dev.pacr.dns.config.JwtSigningService;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

/**
	  * Authentication Resource
  * <p>
  * Handles user authentication and JWT token generation
  */
@Path("/api/v1/auth")
@Produces(MediaType.APPLICATION_JSON)
/**
  * AuthResource class.
  */
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {
	
	/**
	  * The LOG.
	  */
	private static final Logger LOG = Logger.getLogger(AuthResource.class);
	
	@Inject
	JwtSigningService jwtSigningService;
	
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
	/**
	  * login method.
	  */
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
			
		} catch (RuntimeException e) {
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
		Collection<String> groups = new HashSet<>();
		groups.add(userInfo.role);
		
		return jwtSigningService.generateToken(issuer, userInfo.username, groups);
	}
	
	/**
	  * Login request model
	  */
	public static class LoginRequest {
		/**
	  * The username.
		  */
		public String username;
		/**
	  * The password.
		  */
		public String password;
	}
	
	/**
	  * User information model
	  */
		private record UserInfo(String username, String role) {
	}
}

