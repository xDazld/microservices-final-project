package dev.pacr.dns.api;

import dev.pacr.dns.model.FilterRule;
import dev.pacr.dns.service.DNSFilterService;
import dev.pacr.dns.service.DNSResolver;
import dev.pacr.dns.service.SecurityService;
import io.micrometer.core.instrument.MeterRegistry;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.Map;

/**
 * Frontend UI Resource
 * <p>
 * Serves the web frontend using Qute templates and provides HTML fragment endpoints for HTMX
 * dynamic updates.
 */
@Path("/ui")
public class FrontendResource {
	
	private static final Logger LOG = Logger.getLogger(FrontendResource.class);
	
	@Inject
	Template dashboard;
	
	@Inject
	Template filters;
	
	@Inject
	Template query;
	
	@Inject
	Template agent;
	
	@Inject
	Template admin;
	
	@Inject
	Template login;
	
	@Inject
	DNSResolver dnsResolver;
	
	@Inject
	DNSFilterService filterService;
	
	@Inject
	SecurityService securityService;
	
	@Inject
	MeterRegistry meterRegistry;
	
	// ============== Page Routes ==============
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	@PermitAll
	public TemplateInstance getDashboard() {
		LOG.debug("Serving dashboard page");
		return dashboard.data("active", "dashboard");
	}
	
	@GET
	@Path("/filters")
	@Produces(MediaType.TEXT_HTML)
	@PermitAll
	public TemplateInstance getFilters() {
		LOG.debug("Serving filters page");
		return filters.data("active", "filters");
	}
	
	@GET
	@Path("/query")
	@Produces(MediaType.TEXT_HTML)
	@PermitAll
	public TemplateInstance getQuery() {
		LOG.debug("Serving query page");
		return query.data("active", "query");
	}
	
	@GET
	@Path("/agent")
	@Produces(MediaType.TEXT_HTML)
	@PermitAll
	public TemplateInstance getAgent() {
		LOG.debug("Serving agent page");
		return agent.data("active", "agent");
	}
	
	@GET
	@Path("/admin")
	@Produces(MediaType.TEXT_HTML)
	@PermitAll
	public TemplateInstance getAdmin() {
		LOG.debug("Serving admin page");
		return admin.data("active", "admin");
	}
	
	@GET
	@Path("/login")
	@Produces(MediaType.TEXT_HTML)
	@PermitAll
	public TemplateInstance getLogin() {
		LOG.debug("Serving login page");
		return login.data("active", "login");
	}
	
	// ============== HTMX Fragment Endpoints ==============
	
	@GET
	@Path("/stats/queries")
	@Produces(MediaType.TEXT_HTML)
	@PermitAll
	public String getQueryCount() {
		double count = meterRegistry.counter("dns.query.count").count();
		return formatNumber(count);
	}
	
	@GET
	@Path("/stats/cache-hits")
	@Produces(MediaType.TEXT_HTML)
	@PermitAll
	public String getCacheHits() {
		Map<String, Object> stats = dnsResolver.getCacheStats();
		Object hits = stats.get("hits");
		return hits != null ? formatNumber(((Number) hits).doubleValue()) : "0";
	}
	
	@GET
	@Path("/stats/blocked")
	@Produces(MediaType.TEXT_HTML)
	@PermitAll
	public String getBlockedCount() {
		double count = meterRegistry.counter("dns.filter.checks").count();
		return formatNumber(count);
	}
	
	@GET
	@Path("/stats/threats")
	@Produces(MediaType.TEXT_HTML)
	@PermitAll
	public String getThreatsCount() {
		Map<String, Object> stats = securityService.getThreatStats();
		Object detected = stats.get("threatsDetected");
		return detected != null ? formatNumber(((Number) detected).doubleValue()) : "0";
	}
	
	@GET
	@Path("/stats/cache")
	@Produces(MediaType.TEXT_HTML)
	@RolesAllowed({"admin", "user"})
	public String getCacheStats() {
		Map<String, Object> stats = dnsResolver.getCacheStats();
		StringBuilder html = new StringBuilder();
		html.append("<div style='display: grid; gap: 15px;'>");
		
		for (Map.Entry<String, Object> entry : stats.entrySet()) {
			html.append(
					"<div style='display: flex; justify-content: space-between; padding: 10px 0; " +
							"border-bottom: 1px solid var(--border-color);'>");
			html.append("<span style='color: var(--text-secondary);'>")
					.append(formatLabel(entry.getKey())).append("</span>");
			html.append("<span style='font-weight: bold;'>").append(entry.getValue())
					.append("</span>");
			html.append("</div>");
		}
		
		html.append("</div>");
		return html.toString();
	}
	
	@GET
	@Path("/stats/cache-rate")
	@Produces(MediaType.TEXT_HTML)
	@PermitAll
	public String getCacheRate() {
		Map<String, Object> stats = dnsResolver.getCacheStats();
		Object hits = stats.get("hits");
		Object misses = stats.get("misses");
		
		if (hits != null && misses != null) {
			double h = ((Number) hits).doubleValue();
			double m = ((Number) misses).doubleValue();
			double total = h + m;
			if (total > 0) {
				double rate = (h / total) * 100;
				return String.format("%.1f%%", rate);
			}
		}
		return "N/A";
	}
	
	@GET
	@Path("/stats/security")
	@Produces(MediaType.TEXT_HTML)
	@RolesAllowed({"admin", "user"})
	public String getSecurityStats() {
		Map<String, Object> stats = securityService.getThreatStats();
		StringBuilder html = new StringBuilder();
		html.append("<div style='display: grid; gap: 15px;'>");
		
		for (Map.Entry<String, Object> entry : stats.entrySet()) {
			html.append(
					"<div style='display: flex; justify-content: space-between; padding: 10px 0; " +
							"border-bottom: 1px solid var(--border-color);'>");
			html.append("<span style='color: var(--text-secondary);'>")
					.append(formatLabel(entry.getKey())).append("</span>");
			html.append("<span style='font-weight: bold;'>").append(entry.getValue())
					.append("</span>");
			html.append("</div>");
		}
		
		html.append("</div>");
		return html.toString();
	}
	
	@GET
	@Path("/stats/filters")
	@Produces(MediaType.TEXT_HTML)
	@PermitAll
	public String getFiltersSummary() {
		List<FilterRule> rules = filterService.getAllRules();
		long enabled = rules.stream().filter(FilterRule::isEnabled).count();
		long blocked =
				rules.stream().filter(r -> r.getType() == FilterRule.RuleType.BLOCK).count();
		
		String html = "<div style='display: grid; gap: 15px;'>" +
				"<div style='display: flex; justify-content: space-between;'>" +
				"<span style='color: var(--text-secondary);'>Total Rules</span>" +
				"<span style='font-weight: bold;'>" + rules.size() + "</span>" + "</div>" +
				"<div style='display: flex; justify-content: space-between;'>" +
				"<span style='color: var(--text-secondary);'>Enabled</span>" +
				"<span style='font-weight: bold; color: var(--success-color);'>" + enabled +
				"</span>" + "</div>" +
				"<div style='display: flex; justify-content: space-between;'>" +
				"<span style='color: var(--text-secondary);'>Block Rules</span>" +
				"<span style='font-weight: bold; color: var(--danger-color);'>" + blocked +
				"</span>" + "</div>" + "</div>";
		
		return html;
	}
	
	@GET
	@Path("/stats/system")
	@Produces(MediaType.TEXT_HTML)
	@RolesAllowed("admin")
	public String getSystemInfo() {
		Runtime runtime = Runtime.getRuntime();
		long maxMemory = runtime.maxMemory() / (1024 * 1024);
		long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
		int processors = runtime.availableProcessors();
		
		String html = "<div style='display: grid; gap: 15px;'>" +
				"<div style='display: flex; justify-content: space-between;'>" +
				"<span style='color: var(--text-secondary);'>Java Version</span>" +
				"<span style='font-weight: bold;'>" + System.getProperty("java.version") +
				"</span>" + "</div>" +
				"<div style='display: flex; justify-content: space-between;'>" +
				"<span style='color: var(--text-secondary);'>Available Processors</span>" +
				"<span style='font-weight: bold;'>" + processors + "</span>" + "</div>" +
				"<div style='display: flex; justify-content: space-between;'>" +
				"<span style='color: var(--text-secondary);'>Memory Usage</span>" +
				"<span style='font-weight: bold;'>" + usedMemory + " / " + maxMemory +
				" MB</span>" + "</div>" +
				"<div style='display: flex; justify-content: space-between;'>" +
				"<span style='color: var(--text-secondary);'>OS</span>" +
				"<span style='font-weight: bold;'>" + System.getProperty("os.name") + "</span>" +
				"</div>" + "</div>";
		return html;
	}
	
	@GET
	@Path("/filters/list")
	@Produces(MediaType.TEXT_HTML)
	@PermitAll
	public String getFiltersList(@QueryParam("category") String category) {
		List<FilterRule> rules;
		if (category != null && !category.isBlank()) {
			rules = filterService.getRulesByCategory(category);
		} else {
			rules = filterService.getAllRules();
		}
		
		if (rules.isEmpty()) {
			return "<tr><td colspan='7' style='text-align: center; color: var(--text-secondary);" +
					"'>No filter rules found</td></tr>";
		}
		
		StringBuilder html = new StringBuilder();
		for (FilterRule rule : rules) {
			html.append("<tr>");
			
			// Status toggle
			html.append("<td>");
			html.append("<label class='toggle-switch'>");
			html.append("<input type='checkbox' ").append(rule.isEnabled() ? "checked" : "");
			html.append(" onchange=\"toggleRule('").append(rule.getId())
					.append("', this.checked)\">");
			html.append("<span class='toggle-slider'></span>");
			html.append("</label>");
			html.append("</td>");
			
			// Name
			html.append("<td>").append(escapeHtml(rule.getName())).append("</td>");
			
			// Pattern
			html.append("<td><code style='color: var(--secondary-color);'>")
					.append(escapeHtml(rule.getPattern())).append("</code></td>");
			
			// Type badge
			html.append("<td><span class='badge badge-").append(getTypeBadgeClass(rule.getType()))
					.append("'>");
			html.append(rule.getType()).append("</span></td>");
			
			// Category
			html.append("<td>").append(escapeHtml(rule.getCategory())).append("</td>");
			
			// Priority
			html.append("<td>").append(rule.getPriority()).append("</td>");
			
			// Actions
			html.append("<td class='actions'>");
			html.append("<button class='btn btn-sm btn-secondary' onclick=\"openEditRuleModal('")
					.append(rule.getId()).append("')\">Edit</button>");
			html.append("<button class='btn btn-sm btn-danger' onclick=\"deleteRule('")
					.append(rule.getId()).append("', '").append(escapeHtml(rule.getName()))
					.append("')\">Delete</button>");
			html.append("</td>");
			
			html.append("</tr>");
		}
		
		return html.toString();
	}
	
	// ============== Helper Methods ==============
	
	private String formatNumber(double num) {
		if (num >= 1_000_000) {
			return String.format("%.1fM", num / 1_000_000);
		}
		if (num >= 1_000) {
			return String.format("%.1fK", num / 1_000);
		}
		return String.format("%.0f", num);
	}
	
	private String formatLabel(String key) {
		// Convert camelCase to Title Case with spaces
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < key.length(); i++) {
			char c = key.charAt(i);
			if (i == 0) {
				result.append(Character.toUpperCase(c));
			} else if (Character.isUpperCase(c)) {
				result.append(' ').append(c);
			} else {
				result.append(c);
			}
		}
		return result.toString();
	}
	
	private String getTypeBadgeClass(FilterRule.RuleType type) {
		return switch (type) {
			case BLOCK -> "danger";
			case ALLOW -> "success";
			case REDIRECT -> "warning";
		};
	}
	
	private String escapeHtml(String text) {
		if (text == null) {
			return "";
		}
		return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
				.replace("\"", "&quot;").replace("'", "&#39;");
	}
}

