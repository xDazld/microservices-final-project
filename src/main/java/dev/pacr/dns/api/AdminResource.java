package dev.pacr.dns.api;

import dev.pacr.dns.service.DNSResolver;
import dev.pacr.dns.service.SecurityService;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * REST API endpoint for administrative functions and statistics
 */
@Path("/api/v1/admin")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AdminResource {
	
	private static final Logger LOG = Logger.getLogger(AdminResource.class);
	
	@Inject
	DNSResolver dnsResolver;
	
	@Inject
	SecurityService securityService;
	
	@Inject
	MeterRegistry meterRegistry;
	
	/**
	 * Get overall statistics
	 */
	@GET
	@Path("/stats")
	@RolesAllowed({"admin", "user"})
	public Response getStatistics() {
		LOG.debug("Fetching overall statistics");
		
		Map<String, Object> stats = new HashMap<>();
		
		// DNS cache statistics
		stats.put("cache", dnsResolver.getCacheStats());
		
		// Security statistics
		stats.put("security", securityService.getThreatStats());
		
		// Get metrics from Micrometer
		Double queryCount = meterRegistry.counter("dns.query.count").count();
		stats.put("totalQueries", queryCount);
		
		Double filterChecks = meterRegistry.counter("dns.filter.checks").count();
		stats.put("filterChecks", filterChecks);
		
		return Response.ok(stats).build();
	}
	
	/**
	 * Get cache statistics
	 */
	@GET
	@Path("/cache/stats")
	@RolesAllowed({"admin", "user"})
	public Response getCacheStats() {
		LOG.debug("Fetching cache statistics");
		
		Map<String, Object> stats = dnsResolver.getCacheStats();
		
		return Response.ok(stats).build();
	}
	
	/**
	 * Clear expired cache entries
	 */
	@POST
	@Path("/cache/clear")
	@RolesAllowed("admin")
	public Response clearCache() {
		LOG.info("Clearing expired cache entries");
		
		dnsResolver.clearExpiredCache();
		
		return Response.ok(Map.of("message", "Cache cleared successfully")).build();
	}
	
	/**
	 * Get security threat statistics
	 */
	@GET
	@Path("/security/stats")
	@RolesAllowed({"admin", "user"})
	public Response getSecurityStats() {
		LOG.debug("Fetching security statistics");
		
		Map<String, Object> stats = securityService.getThreatStats();
		
		return Response.ok(stats).build();
	}
	
	/**
	 * Analyze a domain for potential threats
	 */
	@POST
	@Path("/security/analyze")
	@RolesAllowed("admin")
	public Response analyzeDomain(Map<String, String> request) {
		String domain = request.get("domain");
		
		if (domain == null || domain.isBlank()) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(Map.of("error", "Domain is required")).build();
		}
		
		LOG.infof("Analyzing domain: %s", domain);
		
		SecurityService.ThreatAnalysis analysis = securityService.analyzeDomain(domain);
		
		return Response.ok(analysis).build();
	}
	
	/**
	 * Add a malicious domain to the threat database
	 */
	@POST
	@Path("/security/threats/domains")
	@RolesAllowed("admin")
	public Response addMaliciousDomain(Map<String, String> request) {
		String domain = request.get("domain");
		
		if (domain == null || domain.isBlank()) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(Map.of("error", "Domain is required")).build();
		}
		
		LOG.infof("Adding malicious domain: %s", domain);
		
		securityService.addMaliciousDomain(domain);
		
		return Response.ok(Map.of("message", "Malicious domain added successfully")).build();
	}
	
	/**
	 * Remove a domain from the threat database
	 */
	@DELETE
	@Path("/security/threats/domains/{domain}")
	@RolesAllowed("admin")
	public Response removeMaliciousDomain(@PathParam("domain") String domain) {
		LOG.infof("Removing malicious domain: %s", domain);
		
		securityService.removeMaliciousDomain(domain);
		
		return Response.ok(Map.of("message", "Malicious domain removed successfully")).build();
	}
	
	/**
	 * Get all malicious domains
	 */
	@GET
	@Path("/security/threats/domains")
	@RolesAllowed({"admin", "user"})
	public Response getMaliciousDomains() {
		LOG.debug("Fetching all malicious domains");
		
		var domains = securityService.getMaliciousDomains();
		
		return Response.ok(Map.of("domains", domains, "count", domains.size())).build();
	}
	
	/**
	 * Health check endpoint
	 */
	@GET
	@Path("/health")
	public Response health() {
		return Response.ok(Map.of("status", "UP", "timestamp", java.time.Instant.now().toString()))
				.build();
	}
}
