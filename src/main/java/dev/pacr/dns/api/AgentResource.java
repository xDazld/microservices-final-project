package dev.pacr.dns.api;

import dev.pacr.dns.agent.DNSIntelligenceAgent;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

import java.util.Map;

/**
 * REST API endpoint for AI Agent operations
 * <p>
 * This endpoint exposes the AI-powered DNS intelligence agent for autonomous threat analysis and
 * security operations
 *
 * @author Patrick Rafferty
 */
@Path("/api/v1/agent")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AgentResource {
	
	/**
	 * The log.
	 */
	private static final Logger LOG = Logger.getLogger(AgentResource.class);
	
	/**
	 * The agent.
	 */
	@Inject
	DNSIntelligenceAgent agent;
	
	/**
	 * Analyze a domain using AI-powered threat intelligence (GET with path parameter)
	 *
	 * @param domain the domain
	 * @return the response
	 */
	@GET
	@Path("/analyze/{domain}")
	@RolesAllowed({"admin", "user"})
	public Response analyzeDomainByPath(@PathParam("domain") String domain) {
		if (domain == null || domain.isBlank()) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(Map.of("error", "Domain is required")).build();
		}
		
		LOG.infof("AI Agent analyzing domain: %s", domain);
		
		DNSIntelligenceAgent.ThreatAnalysisResult result = agent.analyzeDomainWithAI(domain);
		
		return Response.ok(result).build();
	}
	
	/**
	 * Analyze a domain using AI-powered threat intelligence (POST with body)
	 *
	 * @param request the request
	 * @return the response
	 */
	@POST
	@Path("/analyze")
	@RolesAllowed({"admin", "user"})
	public Response analyzeDomain(Map<String, String> request) {
		String domain = request.get("domain");
		
		if (domain == null || domain.isBlank()) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(Map.of("error", "Domain is required")).build();
		}
		
		LOG.infof("AI Agent analyzing domain: %s", domain);
		
		DNSIntelligenceAgent.ThreatAnalysisResult result = agent.analyzeDomainWithAI(domain);
		
		return Response.ok(result).build();
	}
	
	/**
	 * Get AI-generated filter rule recommendations
	 *
	 * @param request the request
	 * @return the response
	 */
	@POST
	@Path("/recommend-filters")
	@RolesAllowed("admin")
	public Response recommendFilters(Map<String, String[]> request) {
		String[] domains = request.get("domains");
		
		if (domains == null || domains.length == 0) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(Map.of("error", "Domain list is required")).build();
		}
		
		LOG.infof("Generating filter recommendations for %d domains", domains.length);
		
		DNSIntelligenceAgent.FilterRuleRecommendation recommendations =
				agent.getFilterRecommendations(domains);
		
		return Response.ok(recommendations).build();
	}
	
	/**
	 * Correlate security events using AI
	 *
	 * @param request the request
	 * @return the response
	 */
	@POST
	@Path("/correlate-events")
	@RolesAllowed("admin")
	public Response correlateEvents(Map<String, String[]> request) {
		String[] events = request.get("events");
		
		if (events == null || events.length == 0) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(Map.of("error", "Events list is required")).build();
		}
		
		LOG.infof("Correlating %d security events", events.length);
		
		DNSIntelligenceAgent.SecurityPatternAnalysis analysis =
				agent.correlateSecurityEvents(events);
		
		return Response.ok(analysis).build();
	}
	
	/**
	 * Get agent status and capabilities
	 *
	 * @return the response
	 */
	@GET
	@Path("/status")
	@RolesAllowed({"admin", "user"})
	public Response getAgentStatus() {
		return Response.ok(Map.of("status", "active", "capabilities",
						new String[] {"threat_analysis", "filter_recommendations",
								"event_correlation",
								"pattern_recognition"}, "provider", "LangChain4j", "mode",
						"autonomous"))
				.build();
	}
}

