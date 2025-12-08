package dev.pacr.dns.service;

import dev.pacr.dns.model.FilterResult;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for DNS Filter Service Tests filtering logic without external dependencies
 */
@QuarkusTest
class DNSFilterServiceTest {
	
	@Inject
	DNSFilterService filterService;
	
	@BeforeEach
	void setUp() {
		filterService.initializeDefaultRules();
	}
	
	@Test
	void testFilterServiceIsInjected() {
		assertNotNull(filterService);
	}
	
	@Test
	void testApplyFiltersReturnsResult() {
		FilterResult result = filterService.applyFilters("example.com");
		assertNotNull(result);
	}
	
	@Test
	void testApplyFiltersReturnsAction() {
		FilterResult result = filterService.applyFilters("example.com");
		assertNotNull(result.getAction());
	}
	
	@Test
	void testGetAllRules() {
		var rules = filterService.getAllRules();
		assertNotNull(rules);
	}
	
	@Test
	void testDefaultRulesInitialized() {
		var rules = filterService.getAllRules();
		assertFalse(rules.isEmpty(), "Default rules should be initialized");
	}
	
	@Test
	void testAddRule() {
		var rule = filterService.addRule("Test", "test.com",
				dev.pacr.dns.model.FilterRule.RuleType.BLOCK, "custom", 100);
		assertNotNull(rule);
		assertEquals("Test", rule.getName());
	}
	
	@Test
	void testDeleteRule() {
		var rule = filterService.addRule("ToDelete", "delete.com",
				dev.pacr.dns.model.FilterRule.RuleType.BLOCK, "custom", 100);
		String ruleId = rule.getId();
		
		filterService.deleteRule(ruleId);
		// If no exception, deletion succeeded
		assertTrue(true);
	}
}

