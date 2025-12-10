package dev.pacr.dns.api;

import dev.pacr.dns.service.DNSResolver;
import dev.pacr.dns.service.RFC5358AccessControlService;
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
	RFC5358AccessControlService rfc5358AccessControl;
	
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
		stats.put("cache", sanitizeStats(dnsResolver.getCacheStats()));
		
		// Security statistics
		stats.put("security", sanitizeStats(securityService.getThreatStats()));
		
		// Get metrics from Micrometer
		Double queryCount = meterRegistry.counter("dns.query.count").count();
		stats.put("totalQueries", sanitizeNumber(queryCount));
		
		Double filterChecks = meterRegistry.counter("dns.filter.checks").count();
		stats.put("filterChecks", sanitizeNumber(filterChecks));
		
		return Response.ok(stats).build();
	}
	
	/**
	 * Sanitize numeric values to prevent JSON serialization errors Replaces NaN and Infinite
	 * values
	 * with 0
	 */
	private Object sanitizeNumber(Object value) {
		if (value instanceof Double d) {
			if (d.isNaN() || d.isInfinite()) {
				return 0.0;
			}
		} else if (value instanceof Float f) {
			if (f.isNaN() || f.isInfinite()) {
				return 0.0f;
			}
		}
		return value;
	}
	
	/**
	 * Sanitize a map of statistics to prevent JSON serialization errors
	 */
	private Map<String, Object> sanitizeStats(Map<String, Object> stats) {
		Map<String, Object> sanitized = new HashMap<>();
		for (Map.Entry<String, Object> entry : stats.entrySet()) {
			Object value = entry.getValue();
			if (value instanceof Map) {
				@SuppressWarnings("unchecked") Map<String, Object> nestedMap =
						(Map<String, Object>) value;
				sanitized.put(entry.getKey(), sanitizeStats(nestedMap));
			} else {
				sanitized.put(entry.getKey(), sanitizeNumber(value));
			}
		}
		return sanitized;
	}
	
	/**
	 * Get cache statistics
	 */
	@GET
	@Path("/cache/stats")
	@RolesAllowed({"admin", "user"})
	public Response getCacheStats() {
		LOG.debug("Fetching cache statistics");
		
		Map<String, Object> stats = sanitizeStats(dnsResolver.getCacheStats());
		
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
		
		Map<String, Object> stats = sanitizeStats(securityService.getThreatStats());
		
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
	
	/**
	 * RFC 5358: Get access control status and compliance information
	 */
	@GET
	@Path("/rfc5358/status")
	@RolesAllowed({"admin", "user"})
	public Response getRFC5358Status() {
		LOG.debug("Fetching RFC 5358 compliance status");
		
		RFC5358AccessControlService.RFC5358Status status =
				rfc5358AccessControl.getComplianceStatus();
		
		Map<String, Object> response = new HashMap<>();
		response.put("recursionEnabled", status.recursionEnabled());
		response.put("defaultDeny", status.defaultDeny());
		response.put("allowedNetworkCount", status.allowedNetworkCount());
		response.put("allowedHostCount", status.allowedHostCount());
		response.put("deniedHostCount", status.deniedHostCount());
		response.put("compliant", status.isCompliant());
		response.put("recommendation", status.isCompliant() ? "RFC 5358 compliant configuration" :
				"Consider enabling default-deny or configuring ACLs for RFC 5358 compliance");
		
		return Response.ok(response).build();
	}
	
	/**
	 * RFC 5358: Get allowed networks
	 */
	@GET
	@Path("/rfc5358/allowed-networks")
	@RolesAllowed({"admin", "user"})
	public Response getAllowedNetworks() {
		LOG.debug("Fetching allowed networks");
		
		var networks = rfc5358AccessControl.getAllowedNetworks();
		
		return Response.ok(Map.of("networks", networks, "count", networks.size())).build();
	}
	
	/**
	 * RFC 5358: Add an allowed network
	 */
	@POST
	@Path("/rfc5358/allowed-networks")
	@RolesAllowed("admin")
	public Response addAllowedNetwork(Map<String, String> request) {
		String network = request.get("network");
		
		if (network == null || network.isBlank()) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(Map.of("error", "Network CIDR is required")).build();
		}
		
		LOG.infof("RFC 5358: Adding allowed network: %s", network);
		rfc5358AccessControl.allowNetwork(network);
		
		return Response.ok(Map.of("message", "Network added to allowed list", "network", network))
				.build();
	}
	
	/**
	 * RFC 5358: Remove an allowed network
	 */
	@DELETE
	@Path("/rfc5358/allowed-networks/{network}")
	@RolesAllowed("admin")
	public Response removeAllowedNetwork(@PathParam("network") String network) {
		LOG.infof("RFC 5358: Removing allowed network: %s", network);
		rfc5358AccessControl.denyNetwork(network);
		
		return Response.ok(
				Map.of("message", "Network removed from allowed list", "network", network)).build();
	}
	
	/**
	 * RFC 5358: Get allowed hosts
	 */
	@GET
	@Path("/rfc5358/allowed-hosts")
	@RolesAllowed({"admin", "user"})
	public Response getAllowedHosts() {
		LOG.debug("Fetching allowed hosts");
		
		var hosts = rfc5358AccessControl.getAllowedHosts();
		
		return Response.ok(Map.of("hosts", hosts, "count", hosts.size())).build();
	}
	
	/**
	 * RFC 5358: Add an allowed host
	 */
	@POST
	@Path("/rfc5358/allowed-hosts")
	@RolesAllowed("admin")
	public Response addAllowedHost(Map<String, String> request) {
		String host = request.get("host");
		
		if (host == null || host.isBlank()) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(Map.of("error", "Host IP address is required")).build();
		}
		
		LOG.infof("RFC 5358: Adding allowed host: %s", host);
		rfc5358AccessControl.allowHost(host);
		
		return Response.ok(Map.of("message", "Host added to allowed list", "host", host)).build();
	}
	
	/**
	 * RFC 5358: Get denied hosts
	 */
	@GET
	@Path("/rfc5358/denied-hosts")
	@RolesAllowed({"admin", "user"})
	public Response getDeniedHosts() {
		LOG.debug("Fetching denied hosts");
		
		var hosts = rfc5358AccessControl.getDeniedHosts();
		
		return Response.ok(Map.of("hosts", hosts, "count", hosts.size())).build();
	}
	
	/**
	 * RFC 5358: Add a denied host
	 */
	@POST
	@Path("/rfc5358/denied-hosts")
	@RolesAllowed("admin")
	public Response addDeniedHost(Map<String, String> request) {
		String host = request.get("host");
		
		if (host == null || host.isBlank()) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(Map.of("error", "Host IP address is required")).build();
		}
		
		LOG.infof("RFC 5358: Adding denied host: %s", host);
		rfc5358AccessControl.denyHost(host);
		
		return Response.ok(Map.of("message", "Host added to denied list", "host", host)).build();
	}
	
	/**
	 * RFC 5358: Enable or disable recursion globally
	 */
	@POST
	@Path("/rfc5358/recursion")
	@RolesAllowed("admin")
	public Response setRecursion(Map<String, Boolean> request) {
		Boolean enabled = request.get("enabled");
		
		if (enabled == null) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(Map.of("error", "enabled field is required (true/false)")).build();
		}
		
		LOG.infof("RFC 5358: Setting recursion enabled=%s", enabled);
		rfc5358AccessControl.setRecursionEnabled(enabled);
		
		return Response.ok(Map.of("message", "Recursion setting updated", "enabled", enabled))
				.build();
	}
}

