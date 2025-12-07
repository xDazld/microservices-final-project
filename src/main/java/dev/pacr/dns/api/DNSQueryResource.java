package dev.pacr.dns.api;

import dev.pacr.dns.model.DNSQuery;
import dev.pacr.dns.model.DNSResponse;
import dev.pacr.dns.service.DNSOrchestrator;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

import java.util.Map;

/**
 * REST API endpoint for DNS resolution
 */
@Path("/api/v1/dns")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DNSQueryResource {
	
	private static final Logger LOG = Logger.getLogger(DNSQueryResource.class);
	
	@Inject
	DNSOrchestrator orchestrator;
	
	/**
	 * Resolve a DNS query
	 */
	@POST
	@Path("/resolve")
	public Response resolve(DNSQueryRequest request) {
		LOG.infof("Received DNS query request for: %s", request.domain);
		
		if (request.domain == null || request.domain.isBlank()) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(Map.of("error", "Domain is required")).build();
		}
		
		DNSQuery query =
				new DNSQuery(request.domain, request.queryType != null ? request.queryType : "A",
						request.clientIp != null ? request.clientIp : "unknown",
						request.protocol != null ? request.protocol : "HTTP");
		
		DNSResponse response = orchestrator.processQuery(query);
		
		return Response.ok(response).build();
	}
	
	/**
	 * Batch resolve multiple domains
	 */
	@POST
	@Path("/resolve/batch")
	public Response resolveBatch(BatchDNSQueryRequest request) {
		LOG.infof("Received batch DNS query request for %d domains", request.domains.size());
		
		if (request.domains == null || request.domains.isEmpty()) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(Map.of("error", "Domains list is required")).build();
		}
		
		var responses = request.domains.stream().map(domain -> {
			DNSQuery query =
					new DNSQuery(domain, request.queryType != null ? request.queryType : "A",
							request.clientIp != null ? request.clientIp : "unknown", "HTTP");
			return orchestrator.processQuery(query);
		}).toList();
		
		return Response.ok(Map.of("results", responses)).build();
	}
	
	/**
	 * Request model for DNS query
	 */
	public static class DNSQueryRequest {
		public String domain;
		public String queryType;
		public String clientIp;
		public String protocol;
	}
	
	/**
	 * Request model for batch DNS queries
	 */
	public static class BatchDNSQueryRequest {
		public java.util.List<String> domains;
		public String queryType;
		public String clientIp;
	}
}
