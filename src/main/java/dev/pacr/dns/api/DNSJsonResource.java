package dev.pacr.dns.api;

import dev.pacr.dns.model.DNSQuery;
import dev.pacr.dns.model.DNSResponse;
import dev.pacr.dns.service.DNSOrchestrator;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

import java.util.Map;

/**
 * JSON-based DNS Query API for frontend consumption
 * <p>
 * This endpoint provides a simpler JSON-based interface for DNS queries, complementing the RFC 8484
 * compliant binary endpoint.
 */
@Path("/api/v1/dns")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DNSJsonResource {
	
	private static final Logger LOG = Logger.getLogger(DNSJsonResource.class);
	
	@Inject
	DNSOrchestrator orchestrator;
	
	/**
	 * Perform a DNS query and return JSON response
	 *
	 * @param domain The domain name to query
	 * @param type   The DNS record type (A, AAAA, MX, TXT, CNAME, NS, etc.)
	 * @return JSON response with DNS resolution results
	 */
	@GET
	@Path("/query")
	public Response query(@QueryParam("domain") String domain, @QueryParam("type") String type) {
		
		if (domain == null || domain.isBlank()) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(Map.of("error", "Domain parameter is required")).build();
		}
		
		// Default to A record if type not specified
		String queryType = (type != null && !type.isBlank()) ? type.toUpperCase() : "A";
		
		LOG.infof("JSON DNS query: domain=%s, type=%s", domain, queryType);
		
		try {
			// Create DNS query
			DNSQuery query = new DNSQuery(domain, queryType, "frontend", "JSON-API");
			
			// Process through orchestrator
			DNSResponse response = orchestrator.processQuery(query);
			
			return Response.ok(response).build();
			
		} catch (RuntimeException e) {
			LOG.errorf(e, "Error processing DNS query for domain: %s", domain);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(Map.of("error", "Failed to process DNS query", "message",
							e.getMessage())).build();
		}
	}
	
	/**
	 * Perform a DNS query using POST with JSON body
	 *
	 * @param request The DNS query request
	 * @return JSON response with DNS resolution results
	 */
	@POST
	@Path("/query")
	public Response queryPost(QueryRequest request) {
		
		if (request == null || request.domain == null || request.domain.isBlank()) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(Map.of("error", "Domain is required")).build();
		}
		
		String queryType =
				(request.type != null && !request.type.isBlank()) ? request.type.toUpperCase() :
						"A";
		
		LOG.infof("JSON DNS query (POST): domain=%s, type=%s", request.domain, queryType);
		
		try {
			// Create DNS query
			DNSQuery query = new DNSQuery(request.domain, queryType, "frontend", "JSON-API");
			
			// Process through orchestrator
			DNSResponse response = orchestrator.processQuery(query);
			
			return Response.ok(response).build();
			
		} catch (RuntimeException e) {
			LOG.errorf(e, "Error processing DNS query for domain: %s", request.domain);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(Map.of("error", "Failed to process DNS query", "message",
							e.getMessage())).build();
		}
	}
	
	/**
	 * Batch DNS query for multiple domains
	 *
	 * @param request The batch query request containing multiple domains
	 * @return JSON response with all DNS resolution results
	 */
	@POST
	@Path("/batch")
	public Response batchQuery(BatchQueryRequest request) {
		
		if (request == null || request.domains == null || request.domains.length == 0) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(Map.of("error", "At least one domain is required")).build();
		}
		
		String queryType =
				(request.type != null && !request.type.isBlank()) ? request.type.toUpperCase() :
						"A";
		
		LOG.infof("Batch DNS query for %d domains", request.domains.length);
		
		try {
			DNSResponse[] responses = new DNSResponse[request.domains.length];
			
			for (int i = 0; i < request.domains.length; i++) {
				DNSQuery query =
						new DNSQuery(request.domains[i], queryType, "frontend", "JSON-API-BATCH");
				responses[i] = orchestrator.processQuery(query);
			}
			
			return Response.ok(Map.of("results", responses)).build();
			
		} catch (RuntimeException e) {
			LOG.errorf(e, "Error processing batch DNS query");
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(Map.of("error", "Failed to process batch DNS query", "message",
							e.getMessage())).build();
		}
	}
	
	/**
	 * Request class for POST queries
	 */
	public static class QueryRequest {
		public String domain;
		public String type;
	}
	
	/**
	 * Request class for batch queries
	 */
	public static class BatchQueryRequest {
		public String[] domains;
		public String type;
	}
}

