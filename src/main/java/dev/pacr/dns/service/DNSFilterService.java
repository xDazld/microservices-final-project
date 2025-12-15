package dev.pacr.dns.service;

import dev.pacr.dns.model.FilterResult;
import dev.pacr.dns.model.FilterRule;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * DNS filtering service that applies filtering rules to domains.
 * <p>
 * This service provides DNS-based content filtering capabilities by applying configurable rules
 * to domain names. It supports different rule types (BLOCK, ALLOW, REDIRECT) with priority-based
 * evaluation and wildcard pattern matching.
 * <p>
 * Features:
 * - In-memory rule storage with thread-safe operations
 * - Regex pattern compilation for performance
 * - Metrics collection for monitoring filter effectiveness
 * - Priority-based rule evaluation
 * <p>
 * Rules are evaluated in descending priority order, with higher priority rules taking precedence.
 *
 * @author Patrick Rafferty
 */
@ApplicationScoped
public class DNSFilterService {
	
	/**
	 * Logger for this class
	 */
	private static final Logger LOG = Logger.getLogger(DNSFilterService.class);
	
	/** In-memory storage for filter rules (in production, use database) */
	private final Map<String, FilterRule> rules = new ConcurrentHashMap<>();
	
	/** Compiled regex patterns for performance optimization */
	private final Map<String, Pattern> compiledPatterns = new ConcurrentHashMap<>();
	
	/** Micrometer registry for metrics collection */
	@Inject
	MeterRegistry registry;
	
	/**
	 * Initialize with default filtering rules
	 */
	public void initializeDefaultRules() {
		// Ad blockers
		addRule("Block Ads - DoubleClick", "*.doubleclick.net", FilterRule.RuleType.BLOCK, "ads",
				100);
		addRule("Block Ads - Google Ads", "*.googleadservices.com", FilterRule.RuleType.BLOCK,
				"ads", 100);
		addRule("Block Ads - Facebook Ads", "*.facebook.com/ads/*", FilterRule.RuleType.BLOCK,
				"ads", 100);
		
		// Tracking blockers
		addRule("Block Tracking - Google Analytics", "*.google-analytics.com",
				FilterRule.RuleType.BLOCK, "tracking", 90);
		addRule("Block Tracking - Mixpanel", "*.mixpanel.com", FilterRule.RuleType.BLOCK,
				"tracking", 90);
		
		// Malware domains (examples)
		addRule("Block Malware - Example", "*.malware-example.com", FilterRule.RuleType.BLOCK,
				"malware", 200);
		
		LOG.info("Default filtering rules initialized");
	}
	
	/**
	 * Apply filtering rules to a domain
	 */
	@Counted(value = "dns.filter.checks", description = "Number of filter checks performed")
	public FilterResult applyFilters(CharSequence domain) {
		LOG.debugf("Applying filters to domain: %s", domain);
		
		// Explicitly increment counter (in case @Counted annotation doesn't work)
		registry.counter("dns.filter.checks").increment();
		
		// Get all enabled rules sorted by priority (descending)
		List<FilterRule> enabledRules = rules.values().stream().filter(FilterRule::isEnabled)
				.sorted((r1, r2) -> Integer.compare(r2.getPriority(), r1.getPriority())).toList();
		
		for (FilterRule rule : enabledRules) {
			if (matchesPattern(domain, rule.getPattern())) {
				LOG.infof("Domain %s matched rule: %s (type: %s)", domain, rule.getName(),
						rule.getType());
				
				// Increment metrics
				registry.counter("dns.filter.matches", "type", rule.getType().toString(),
						"category", rule.getCategory()).increment();
				
				return switch (rule.getType()) {
					case BLOCK -> FilterResult.block(
							"Blocked by rule: " + rule.getName() + " (category: " +
									rule.getCategory() + ')', rule);
					case REDIRECT -> FilterResult.redirect(rule.getRedirectTo(), rule);
					case ALLOW -> FilterResult.allow();
				};
			}
		}
		
		// No rules matched, allow by default
		return FilterResult.allow();
	}
	
	/**
	 * Check if domain matches a pattern
	 */
	private boolean matchesPattern(CharSequence domain, String pattern) {
		// Convert wildcard pattern to regex if not already cached
		Pattern compiledPattern = compiledPatterns.computeIfAbsent(pattern, p -> {
			String regex = p.replace(".", "\\.").replace("*", ".*");
			return Pattern.compile('^' + regex + '$', Pattern.CASE_INSENSITIVE);
		});
		
		return compiledPattern.matcher(domain).matches();
	}
	
	/**
	 * Add a new filtering rule
	 */
	public FilterRule addRule(String name, String pattern, FilterRule.RuleType type,
							  String category, int priority) {
		FilterRule rule = new FilterRule();
		rule.setName(name);
		rule.setPattern(pattern);
		rule.setType(type);
		rule.setCategory(category);
		rule.setPriority(priority);
		rule.setEnabled(true);
		
		rules.put(rule.getId(), rule);
		LOG.infof("Added filter rule: %s", rule);
		
		return rule;
	}
	
	/**
	 * Update an existing rule
	 */
	public FilterRule updateRule(String ruleId, FilterRule updatedRule) {
		FilterRule existingRule = rules.get(ruleId);
		if (existingRule == null) {
			throw new IllegalArgumentException("Rule not found: " + ruleId);
		}
		
		updatedRule.setId(ruleId);
		updatedRule.setUpdatedAt(java.time.Instant.now());
		rules.put(ruleId, updatedRule);
		
		// Clear cached pattern
		compiledPatterns.remove(existingRule.getPattern());
		
		LOG.infof("Updated filter rule: %s", ruleId);
		return updatedRule;
	}
	
	/**
	 * Delete a rule
	 */
	public void deleteRule(String ruleId) {
		FilterRule rule = rules.remove(ruleId);
		if (rule != null) {
			compiledPatterns.remove(rule.getPattern());
			LOG.infof("Deleted filter rule: %s", ruleId);
		}
	}
	
	/**
	 * Get all rules
	 */
	public List<FilterRule> getAllRules() {
		return new ArrayList<>(rules.values());
	}
	
	/**
	 * Get rules by category
	 */
	public List<FilterRule> getRulesByCategory(String category) {
		return rules.values().stream().filter(rule -> category.equals(rule.getCategory())).toList();
	}
	
	/**
	 * Get a specific rule
	 */
	public FilterRule getRule(String ruleId) {
		return rules.get(ruleId);
	}
	
	/**
	 * Enable/disable a rule
	 */
	public void toggleRule(String ruleId, boolean enabled) {
		FilterRule rule = rules.get(ruleId);
		if (rule != null) {
			rule.setEnabled(enabled);
			rule.setUpdatedAt(java.time.Instant.now());
			LOG.infof("Toggled rule %s to %s", ruleId, enabled ? "enabled" : "disabled");
		}
	}
	
	/**
	 * Get filtering statistics
	 */
	public Map<String, Object> getFilterStats() {
		Map<String, Long> categoryCounts = new HashMap<>();
		Map<String, Long> typeCounts = new HashMap<>();
		
		for (FilterRule rule : rules.values()) {
			if (rule.isEnabled()) {
				categoryCounts.merge(rule.getCategory(), 1L, Long::sum);
				typeCounts.merge(rule.getType().toString(), 1L, Long::sum);
			}
		}
		
		return Map.of("totalRules", rules.size(), "enabledRules",
				rules.values().stream().filter(FilterRule::isEnabled).count(), "byCategory",
				categoryCounts, "byType", typeCounts);
	}
}
