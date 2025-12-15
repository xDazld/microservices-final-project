package dev.pacr.dns.api;

import dev.pacr.dns.messaging.EventPublisher;
import dev.pacr.dns.model.FilterRule;
import dev.pacr.dns.service.DNSFilterService;
import dev.pacr.dns.service.DNSResolver;
import dev.pacr.dns.service.QueryLogService;
import dev.pacr.dns.service.SecurityService;
import io.micrometer.core.instrument.MeterRegistry;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
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
 * dynamic updates. Publishes real-time metrics via event streaming to WebSocket clients.
 */
@Path("/ui")
/**
	 * FrontendResource class.
 */
public class FrontendResource {
	
	/**
	 * The LOG.
	 */
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
	Template logs;
	
	@Inject
	DNSResolver dnsResolver;
	
	@Inject
	DNSFilterService filterService;
	
	@Inject
	SecurityService securityService;
	
	@Inject
	QueryLogService queryLogService;
	
	@Inject
	MeterRegistry meterRegistry;
	
	@Inject
	EventPublisher eventPublisher;
	
	// ============== Page Routes ==============
	@GET
	@Produces(MediaType.TEXT_HTML)
	@PermitAll
	/**
	 * Gets the Dashboard.
	 * @return the Dashboard
	 */
	public TemplateInstance getDashboard() {
		LOG.debug("Serving dashboard page");
		return dashboard.data("active", "dashboard");
	}
	
	@GET
	@Path("/filters")
	@Produces(MediaType.TEXT_HTML)
	@PermitAll
	/**
	 * Gets the Filters.
	 * @return the Filters
	 */
	public TemplateInstance getFilters() {
		LOG.debug("Serving filters page");
		return filters.data("active", "filters");
	}
	
	@GET
	@Path("/query")
	@Produces(MediaType.TEXT_HTML)
	@PermitAll
	/**
	 * Gets the Query.
	 * @return the Query
	 */
	public TemplateInstance getQuery() {
		LOG.debug("Serving query page");
		return query.data("active", "query");
	}
	
	@GET
	@Path("/agent")
	@Produces(MediaType.TEXT_HTML)
	@PermitAll
	/**
	 * Gets the Agent.
	 * @return the Agent
	 */
	public TemplateInstance getAgent() {
		LOG.debug("Serving agent page");
		return agent.data("active", "agent");
	}
	
	@GET
	@Path("/admin")
	@Produces(MediaType.TEXT_HTML)
	@PermitAll
	/**
	 * Gets the Admin.
	 * @return the Admin
	 */
	public TemplateInstance getAdmin() {
		LOG.debug("Serving admin page");
		return admin.data("active", "admin");
	}
	
	@GET
	@Path("/logs")
	@Produces(MediaType.TEXT_HTML)
	@PermitAll
	/**
	 * Gets the Logs.
	 * @return the Logs
	 */
	public TemplateInstance getLogs() {
		LOG.debug("Serving logs page");
		return logs.data("active", "logs");
	}
	
	@GET
	@Path("/login")
	@Produces(MediaType.TEXT_HTML)
	@PermitAll
	/**
	 * Gets the Login.
	 * @return the Login
	 */
	public TemplateInstance getLogin() {
		LOG.debug("Serving login page");
		return login.data("active", "login");
	}
	
	// ============== Query Logs Endpoints ==============
	
	@GET
	@Path("/logs/table")
	@Produces(MediaType.TEXT_HTML)
	@PermitAll
	public String getLogsTable(@QueryParam("limit") int limit, @QueryParam("status") String status,
							   @QueryParam("domain") String domain) {
		LOG.debug("Fetching logs table");
		
		List<Map<String, Object>> logs;
		if (limit <= 0) {
			limit = 100;
		}
		
		if (status != null && !status.isEmpty()) {
			logs = queryLogService.getQueriesByStatus(status, limit);
		} else if (domain != null && !domain.isEmpty()) {
			logs = queryLogService.getQueriesByDomain(domain, limit);
		} else {
			logs = queryLogService.getRecentQueries(limit);
		}
		
		// Publish each log entry as a real-time event
		logs.forEach(logEntry -> {
			try {
				eventPublisher.publishQueryLog(logEntry);
			} catch (Exception e) {
				LOG.warnf(e, "Failed to publish query log event");
			}
		});
		
		StringBuilder html = new StringBuilder();
		html.append("<table class='log-table'>");
		html.append("<thead><tr>");
		html.append("<th>Timestamp</th>");
		html.append("<th>Domain</th>");
		html.append("<th>Type</th>");
		html.append("<th>Status</th>");
		html.append("<th>Response Code</th>");
		html.append("<th>Answers</th>");
		html.append("<th>Source IP</th>");
		html.append("</tr></thead>");
		html.append("<tbody>");
		
		if (logs.isEmpty()) {
			html.append("<tr><td colspan='7' style='text-align: center; padding: 20px;'>");
			html.append("No query logs available</td></tr>");
		} else {
			for (Map<String, Object> log : logs) {
				html.append("<tr>");
				html.append("<td class='timestamp-cell'>")
						.append(escapeHtml(log.get("timestamp").toString())).append("</td>");
				html.append("<td class='domain-cell'>")
						.append(escapeHtml(log.get("domain").toString())).append("</td>");
				html.append("<td>").append(escapeHtml(log.get("queryType").toString()))
						.append("</td>");
				
				String statusStr = log.get("status").toString();
				String statusClass = "status-" + statusStr.toLowerCase();
				html.append("<td><span class='status-badge ").append(statusClass).append("'>");
				html.append(escapeHtml(statusStr)).append("</span></td>");
				
				html.append("<td>").append(log.get("rcode")).append("</td>");
				
				List<String> answers = (List<String>) log.get("answers");
				String answersStr =
						answers != null && !answers.isEmpty() ? String.join(", ", answers) : "--";
				html.append("<td class='answers-cell' title='").append(escapeHtml(answersStr))
						.append("'>");
				html.append(escapeHtml(answersStr)).append("</td>");
				
				html.append("<td>").append(escapeHtml(log.get("sourceIp").toString()))
						.append("</td>");
				html.append("</tr>");
			}
		}
		
		html.append("</tbody></table>");
		return html.toString();
	}
	
	@GET
	@Path("/logs/stats/total")
	@Produces(MediaType.TEXT_HTML)
	@PermitAll
	/**
	 * Gets the LogsTotalCount.
	 * @return the LogsTotalCount
	 */
	public String getLogsTotalCount() {
		Map<String, Object> stats = queryLogService.getQueryStats();
		return formatNumber(((Number) stats.get("totalQueries")).doubleValue());
	}
	
	@GET
	@Path("/logs/stats/blocked")
	@Produces(MediaType.TEXT_HTML)
	@PermitAll
	/**
	 * Gets the LogsBlockedCount.
	 * @return the LogsBlockedCount
	 */
	public String getLogsBlockedCount() {
		Map<String, Object> stats = queryLogService.getQueryStats();
		return formatNumber(((Number) stats.get("blockedQueries")).doubleValue());
	}
	
	@GET
	@Path("/logs/stats/threats")
	@Produces(MediaType.TEXT_HTML)
	@PermitAll
	/**
	 * Gets the LogsThreatsCount.
	 * @return the LogsThreatsCount
	 */
	public String getLogsThreatsCount() {
		Map<String, Object> stats = queryLogService.getQueryStats();
		return formatNumber(((Number) stats.get("threatQueries")).doubleValue());
	}
	
	@GET
	@Path("/logs/stats/rate")
	@Produces(MediaType.TEXT_HTML)
	@PermitAll
	/**
	 * Gets the LogsBlockRate.
	 * @return the LogsBlockRate
	 */
	public String getLogsBlockRate() {
		Map<String, Object> stats = queryLogService.getQueryStats();
		return stats.get("blockRate").toString();
	}
	
	@POST
	@Path("/logs/clear")
	@Produces(MediaType.TEXT_HTML)
	@RolesAllowed({"admin"})
	/**
	 * clearQueryLogs method.
	 */
	public String clearQueryLogs() {
		LOG.info("Clearing query logs");
		queryLogService.clearLogs();
		return "<div style='text-align: center; padding: 20px; color: var(--success-color);'>" +
				"<strong>✓ Query logs cleared successfully</strong></div>";
	}
	
	// ============== HTMX Fragment Endpoints ==============
	
	@GET
	@Path("/stats/queries")
	@Produces(MediaType.TEXT_HTML)
	@PermitAll
	/**
	 * Gets the QueryCount.
	 * @return the QueryCount
	 */
	public String getQueryCount() {
		double count = 0;
		try {
			// Get counter directly - this will create it if it doesn't exist
			count = meterRegistry.counter("dns.query.count").count();
		} catch (Exception e) {
			LOG.warnf("Error retrieving query count metric: %s", e.getMessage());
		}
		
		// Publish metrics update event for real-time dashboard updates
		eventPublisher.publishQueryCountMetric(count);
		
		String result = formatNumber(count);
		LOG.debugf("Query count endpoint returning: %s (raw: %f)", result, count);
		return result;
	}
	
	@GET
	@Path("/stats/cache-hits")
	@Produces(MediaType.TEXT_HTML)
	@PermitAll
	/**
	 * Gets the CacheHits.
	 * @return the CacheHits
	 */
	public String getCacheHits() {
		Map<String, Object> stats = dnsResolver.getCacheStats();
		LOG.debugf("Cache stats structure: %s", stats);
		Map<String, Object> positiveCache = (Map<String, Object>) stats.get("positiveCache");
		if (positiveCache != null) {
			LOG.debugf("Positive cache stats: %s", positiveCache);
			Object active = positiveCache.get("active");
			
			// Publish cache stats update
			eventPublisher.publishCacheStatsUpdate(stats);
			
			String result = active != null ? formatNumber(((Number) active).doubleValue()) : "0";
			LOG.debugf("Cache hits returning: %s (active: %s)", result, active);
			return result;
		}
		LOG.debugf("Positive cache is null!");
		return "0";
	}
	
	@GET
	@Path("/stats/blocked")
	@Produces(MediaType.TEXT_HTML)
	@PermitAll
	/**
	 * Gets the BlockedCount.
	 * @return the BlockedCount
	 */
	public String getBlockedCount() {
		double count = 0;
		try {
			// Get counter directly - this will create it if it doesn't exist
			count = meterRegistry.counter("dns.filter.checks").count();
		} catch (Exception e) {
			LOG.warnf("Error retrieving blocked count metric: %s", e.getMessage());
		}
		
		// Publish metrics update event for real-time dashboard updates
		eventPublisher.publishFilterCheckMetric(count);
		
		String result = formatNumber(count);
		LOG.debugf("Blocked count endpoint returning: %s (raw: %f)", result, count);
		return result;
	}
	
	@GET
	@Path("/stats/threats")
	@Produces(MediaType.TEXT_HTML)
	@PermitAll
	/**
	 * Gets the ThreatsCount.
	 * @return the ThreatsCount
	 */
	public String getThreatsCount() {
		Map<String, Object> stats = securityService.getThreatStats();
		LOG.debugf("Threat stats: %s", stats);
		Object domains = stats.get("maliciousDomains");
		Object ips = stats.get("maliciousIPs");
		
		// Publish security stats update
		eventPublisher.publishSecurityStatsUpdate(stats);
		
		double total = 0;
		if (domains != null) {
			total += ((Number) domains).doubleValue();
		}
		if (ips != null) {
			total += ((Number) ips).doubleValue();
		}
		String result = formatNumber(total);
		LOG.debugf("Threats count returning: %s (domains: %s, ips: %s, total: %f)", result,
				domains,
				ips, total);
		return result;
	}
	
	@GET
	@Path("/stats/cache")
	@Produces(MediaType.TEXT_HTML)
	@RolesAllowed({"admin", "user"})
	/**
	 * Gets the CacheStats.
	 * @return the CacheStats
	 */
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
	/**
	 * Gets the CacheRate.
	 * @return the CacheRate
	 */
	public String getCacheRate() {
		Map<String, Object> stats = dnsResolver.getCacheStats();
		Map<String, Object> positiveCache = (Map<String, Object>) stats.get("positiveCache");
		
		if (positiveCache != null) {
			Object active = positiveCache.get("active");
			Object total = positiveCache.get("total");
			
			if (active != null && total != null) {
				double a = ((Number) active).doubleValue();
				double t = ((Number) total).doubleValue();
				if (t > 0) {
					double rate = (a / t) * 100;
					return String.format("%.1f%%", rate);
				}
			}
		}
		return "N/A";
	}
	
	@GET
	@Path("/stats/security")
	@Produces(MediaType.TEXT_HTML)
	@RolesAllowed({"admin", "user"})
	/**
	 * Gets the SecurityStats.
	 * @return the SecurityStats
	 */
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
	/**
	 * Gets the FiltersSummary.
	 * @return the FiltersSummary
	 */
	public String getFiltersSummary() {
		List<FilterRule> rules = filterService.getAllRules();
		long enabled = rules.stream().filter(FilterRule::isEnabled).count();
		long blocked =
				rules.stream().filter(r -> r.getType() == FilterRule.RuleType.BLOCK).count();
		
		return "<div style='display: grid; gap: 15px;'>" +
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
	}
	
	@GET
	@Path("/stats/system")
	@Produces(MediaType.TEXT_HTML)
	@RolesAllowed("admin")
	/**
	 * Gets the SystemInfo.
	 * @return the SystemInfo
	 */
	public String getSystemInfo() {
		Runtime runtime = Runtime.getRuntime();
		long maxMemory = runtime.maxMemory() / (1024 * 1024);
		long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
		int processors = runtime.availableProcessors();
		
		return "<div style='display: grid; gap: 15px;'>" +
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
		// Handle NaN and Infinite values
		if (Double.isNaN(num) || Double.isInfinite(num)) {
			return "0";
		}
		if (num >= 1_000_000) {
			return String.format("%.1fM", num / 1_000_000);
		}
		if (num >= 1_000) {
			return String.format("%.1fK", num / 1_000);
		}
		return String.format("%.0f", num);
	}
	
	private String formatLabel(CharSequence key) {
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

