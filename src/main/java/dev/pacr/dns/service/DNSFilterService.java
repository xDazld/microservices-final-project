package dev.pacr.dns.service;

import dev.pacr.dns.model.FilterResult;
import dev.pacr.dns.storage.FilterRuleRepository;
import dev.pacr.dns.storage.model.FilterRule;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

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
	
	/**
	 * Repository for persisting filter rules to MongoDB
	 */
	@Inject
	FilterRuleRepository ruleRepository;
	
	/** Compiled regex patterns for performance optimization */
	private final Map<String, Pattern> compiledPatterns = new ConcurrentHashMap<>();
	
	/** Micrometer registry for metrics collection */
	@Inject
	MeterRegistry registry;
	
	/**
	 * Initialize with default filtering rules
	 */
	public void initializeDefaultRules() {
		// Check if rules already exist to avoid duplicates
		if (ruleRepository.count() > 0) {
			LOG.info("Filter rules already initialized, skipping default rule creation");
			return;
		}
		
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
		
		// Get all enabled rules sorted by priority (descending) from database
		List<FilterRule> enabledRules = ruleRepository.findEnabledRulesSortedByPriority();
		
		for (FilterRule rule : enabledRules) {
			if (matchesPattern(domain, rule.pattern)) {
				LOG.infof("Domain %s matched rule: %s (type: %s)", domain, rule.name, rule.type);
				
				// Increment metrics
				registry.counter("dns.filter.matches", "type", rule.type.toString(), "category",
						rule.category).increment();
				
				return switch (rule.type) {
					case BLOCK -> FilterResult.block(
							"Blocked by rule: " + rule.name + " (category: " + rule.category + ')',
							rule);
					case REDIRECT -> FilterResult.redirect(rule.redirectTo, rule);
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
		rule.name = name;
		rule.pattern = pattern;
		rule.type = type;
		rule.category = category;
		rule.priority = priority;
		rule.enabled = true;
		
		ruleRepository.persist(rule);
		LOG.infof("Added filter rule: %s", rule);
		
		return rule;
	}
	
	/**
	 * Update an existing rule
	 */
	public FilterRule updateRule(String ruleId, FilterRule updatedRule) {
		FilterRule existingRule = ruleRepository.findByRuleId(ruleId);
		if (existingRule == null) {
			throw new IllegalArgumentException("Rule not found: " + ruleId);
		}
		
		// Update fields
		existingRule.name = updatedRule.name;
		existingRule.pattern = updatedRule.pattern;
		existingRule.type = updatedRule.type;
		existingRule.category = updatedRule.category;
		existingRule.redirectTo = updatedRule.redirectTo;
		existingRule.enabled = updatedRule.enabled;
		existingRule.priority = updatedRule.priority;
		existingRule.updatedAt = java.time.Instant.now();
		
		ruleRepository.update(existingRule);
		
		// Clear cached pattern
		compiledPatterns.remove(existingRule.pattern);
		
		LOG.infof("Updated filter rule: %s", ruleId);
		return existingRule;
	}
	
	/**
	 * Delete a rule
	 */
	public void deleteRule(String ruleId) {
		FilterRule rule = ruleRepository.findByRuleId(ruleId);
		if (rule != null) {
			compiledPatterns.remove(rule.pattern);
			ruleRepository.delete(rule);
			LOG.infof("Deleted filter rule: %s", ruleId);
		}
	}
	
	/**
	 * Get all rules
	 */
	public List<FilterRule> getAllRules() {
		return ruleRepository.listAll();
	}
	
	/**
	 * Get rules by category
	 */
	public List<FilterRule> getRulesByCategory(String category) {
		return ruleRepository.findByCategory(category);
	}
	
	/**
	 * Get a specific rule
	 */
	public FilterRule getRule(String ruleId) {
		return ruleRepository.findByRuleId(ruleId);
	}
	
	/**
	 * Enable/disable a rule
	 */
	public void toggleRule(String ruleId, boolean enabled) {
		FilterRule rule = ruleRepository.findByRuleId(ruleId);
		if (rule != null) {
			rule.enabled = enabled;
			rule.updatedAt = java.time.Instant.now();
			ruleRepository.update(rule);
			LOG.infof("Toggled rule %s to %s", ruleId, enabled ? "enabled" : "disabled");
		}
	}
	
	/**
	 * Get filtering statistics
	 */
	public Map<String, Object> getFilterStats() {
		Map<String, Long> categoryCounts = new HashMap<>();
		Map<String, Long> typeCounts = new HashMap<>();
		
		List<FilterRule> allRules = ruleRepository.listAll();
		for (FilterRule rule : allRules) {
			if (rule.enabled) {
				categoryCounts.merge(rule.category, 1L, Long::sum);
				typeCounts.merge(rule.type.toString(), 1L, Long::sum);
			}
		}
		
		return Map.of("totalRules", (long) allRules.size(), "enabledRules",
				ruleRepository.countEnabled(), "byCategory",
				categoryCounts, "byType", typeCounts);
	}
}
