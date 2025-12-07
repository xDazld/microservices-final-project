package dev.pacr.dns.api;

import dev.pacr.dns.model.FilterRule;
import dev.pacr.dns.service.DNSFilterService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.Map;

/**
 * REST API endpoint for managing DNS filtering rules
 */
@Path("/api/v1/filters")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FilterRuleResource {
	
	private static final Logger LOG = Logger.getLogger(FilterRuleResource.class);
	
	@Inject
	DNSFilterService filterService;
	
	/**
	 * Get all filter rules
	 */
	@GET
	@RolesAllowed({"admin", "user"})
	public Response getAllRules(@QueryParam("category") String category) {
		LOG.debug("Fetching all filter rules");
		
		List<FilterRule> rules;
		if (category != null && !category.isBlank()) {
			rules = filterService.getRulesByCategory(category);
		} else {
			rules = filterService.getAllRules();
		}
		
		return Response.ok(rules).build();
	}
	
	/**
	 * Get a specific filter rule
	 */
	@GET
	@Path("/{ruleId}")
	@RolesAllowed({"admin", "user"})
	public Response getRule(@PathParam("ruleId") String ruleId) {
		LOG.debugf("Fetching filter rule: %s", ruleId);
		
		FilterRule rule = filterService.getRule(ruleId);
		if (rule == null) {
			return Response.status(Response.Status.NOT_FOUND)
					.entity(Map.of("error", "Rule not found")).build();
		}
		
		return Response.ok(rule).build();
	}
	
	/**
	 * Create a new filter rule
	 */
	@POST
	@RolesAllowed("admin")
	public Response createRule(FilterRuleRequest request) {
		LOG.infof("Creating new filter rule: %s", request.name);
		
		if (request.name == null || request.pattern == null || request.type == null) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(Map.of("error", "Name, pattern, and type are required")).build();
		}
		
		FilterRule rule = filterService.addRule(request.name, request.pattern,
				FilterRule.RuleType.valueOf(request.type),
				request.category != null ? request.category : "custom",
				request.priority != null ? request.priority : 50);
		
		return Response.status(Response.Status.CREATED).entity(rule).build();
	}
	
	/**
	 * Update an existing filter rule
	 */
	@PUT
	@Path("/{ruleId}")
	@RolesAllowed("admin")
	public Response updateRule(@PathParam("ruleId") String ruleId, FilterRuleRequest request) {
		LOG.infof("Updating filter rule: %s", ruleId);
		
		FilterRule existingRule = filterService.getRule(ruleId);
		if (existingRule == null) {
			return Response.status(Response.Status.NOT_FOUND)
					.entity(Map.of("error", "Rule not found")).build();
		}
		
		FilterRule updatedRule = new FilterRule();
		updatedRule.setName(request.name != null ? request.name : existingRule.getName());
		updatedRule.setPattern(
				request.pattern != null ? request.pattern : existingRule.getPattern());
		updatedRule.setType(request.type != null ? FilterRule.RuleType.valueOf(request.type) :
				existingRule.getType());
		updatedRule.setCategory(
				request.category != null ? request.category : existingRule.getCategory());
		updatedRule.setPriority(
				request.priority != null ? request.priority : existingRule.getPriority());
		updatedRule.setEnabled(
				request.enabled != null ? request.enabled : existingRule.isEnabled());
		updatedRule.setRedirectTo(
				request.redirectTo != null ? request.redirectTo : existingRule.getRedirectTo());
		
		FilterRule result = filterService.updateRule(ruleId, updatedRule);
		
		return Response.ok(result).build();
	}
	
	/**
	 * Delete a filter rule
	 */
	@DELETE
	@Path("/{ruleId}")
	@RolesAllowed("admin")
	public Response deleteRule(@PathParam("ruleId") String ruleId) {
		LOG.infof("Deleting filter rule: %s", ruleId);
		
		FilterRule rule = filterService.getRule(ruleId);
		if (rule == null) {
			return Response.status(Response.Status.NOT_FOUND)
					.entity(Map.of("error", "Rule not found")).build();
		}
		
		filterService.deleteRule(ruleId);
		
		return Response.ok(Map.of("message", "Rule deleted successfully")).build();
	}
	
	/**
	 * Toggle rule enabled/disabled
	 */
	@PATCH
	@Path("/{ruleId}/toggle")
	@RolesAllowed("admin")
	public Response toggleRule(@PathParam("ruleId") String ruleId, Map<String, Boolean> request) {
		LOG.infof("Toggling filter rule: %s", ruleId);
		
		FilterRule rule = filterService.getRule(ruleId);
		if (rule == null) {
			return Response.status(Response.Status.NOT_FOUND)
					.entity(Map.of("error", "Rule not found")).build();
		}
		
		boolean enabled = request.getOrDefault("enabled", true);
		filterService.toggleRule(ruleId, enabled);
		
		return Response.ok(Map.of("message", "Rule toggled successfully", "enabled", enabled))
				.build();
	}
	
	/**
	 * Get filter statistics
	 */
	@GET
	@Path("/stats")
	@RolesAllowed({"admin", "user"})
	public Response getFilterStats() {
		LOG.debug("Fetching filter statistics");
		
		Map<String, Object> stats = filterService.getFilterStats();
		
		return Response.ok(stats).build();
	}
	
	/**
	 * Request model for filter rule
	 */
	public static class FilterRuleRequest {
		public String name;
		public String pattern;
		public String type;
		public String category;
		public Integer priority;
		public Boolean enabled;
		public String redirectTo;
	}
}
