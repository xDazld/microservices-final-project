package dev.pacr.dns.model;

import dev.pacr.dns.storage.model.FilterRule;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for Filter Rule model
 */
class FilterRuleTest {
	
	@Test
	void testCreateFilterRuleWithDefaults() {
		FilterRule rule = new FilterRule();
		
		assertNotNull(rule.ruleId);
		assertNotNull(rule.createdAt);
		assertNotNull(rule.updatedAt);
		assertTrue(rule.enabled);
		assertEquals(0, rule.priority);
	}
	
	@Test
	void testFilterRuleGettersSetters() {
		FilterRule rule = new FilterRule();
		
		rule.name = "Test Rule";
		rule.pattern = "*.example.com";
		rule.type = FilterRule.RuleType.BLOCK;
		rule.category = "ads";
		rule.priority = 100;
		rule.enabled = false;
		
		assertEquals("Test Rule", rule.name);
		assertEquals("*.example.com", rule.pattern);
		assertEquals(FilterRule.RuleType.BLOCK, rule.type);
		assertEquals("ads", rule.category);
		assertEquals(100, rule.priority);
		assertFalse(rule.enabled);
	}
	
	@Test
	void testFilterRuleIdUniqueness() {
		FilterRule rule1 = new FilterRule();
		FilterRule rule2 = new FilterRule();
		
		assertNotEquals(rule1.ruleId, rule2.ruleId);
	}
	
	@Test
	void testFilterRuleRedirectTo() {
		FilterRule rule = new FilterRule();
		
		rule.type = FilterRule.RuleType.REDIRECT;
		rule.redirectTo = "127.0.0.1";
		
		assertEquals(FilterRule.RuleType.REDIRECT, rule.type);
		assertEquals("127.0.0.1", rule.redirectTo);
	}
	
	@Test
	void testFilterRuleBlockType() {
		FilterRule rule = new FilterRule();
		rule.type = FilterRule.RuleType.BLOCK;
		
		assertEquals(FilterRule.RuleType.BLOCK, rule.type);
	}
	
	@Test
	void testFilterRuleAllowType() {
		FilterRule rule = new FilterRule();
		rule.type = FilterRule.RuleType.ALLOW;
		
		assertEquals(FilterRule.RuleType.ALLOW, rule.type);
	}
	
	@Test
	void testFilterRulePriority() {
		FilterRule rule = new FilterRule();
		
		rule.priority = 500;
		assertEquals(500, rule.priority);
		
		rule.priority = 0;
		assertEquals(0, rule.priority);
		
		rule.priority = -1;
		assertEquals(-1, rule.priority);
	}
	
	@Test
	void testFilterRuleToggleEnabled() {
		FilterRule rule = new FilterRule();
		
		assertTrue(rule.enabled);
		
		rule.enabled = false;
		assertFalse(rule.enabled);
		
		rule.enabled = true;
		assertTrue(rule.enabled);
	}
	
	@Test
	void testFilterRuleCreatedAt() {
		FilterRule rule = new FilterRule();
		assertNotNull(rule.createdAt);
	}
	
	@Test
	void testFilterRuleUpdatedAt() {
		FilterRule rule = new FilterRule();
		assertNotNull(rule.updatedAt);
	}
	
	@Test
	void testFilterRuleSetCreatedAt() {
		FilterRule rule = new FilterRule();
		rule.createdAt = rule.createdAt.minusSeconds(3600);
		
		assertNotNull(rule.createdAt);
	}
	
	@Test
	void testFilterRuleSetUpdatedAt() {
		FilterRule rule = new FilterRule();
		rule.updatedAt = rule.updatedAt.plusSeconds(3600);
		
		assertNotNull(rule.updatedAt);
	}
	
	@Test
	void testFilterRuleCompleteSetup() {
		FilterRule rule = new FilterRule();
		rule.ruleId = "rule-123";
		rule.name = "Block Ads";
		rule.pattern = "*.ads.com";
		rule.type = FilterRule.RuleType.BLOCK;
		rule.category = "ads";
		rule.redirectTo = null;
		rule.enabled = true;
		rule.priority = 100;
		
		assertEquals("rule-123", rule.ruleId);
		assertEquals("Block Ads", rule.name);
		assertEquals("*.ads.com", rule.pattern);
		assertEquals(FilterRule.RuleType.BLOCK, rule.type);
		assertEquals("ads", rule.category);
		assertTrue(rule.enabled);
		assertEquals(100, rule.priority);
	}
	
	@Test
	void testFilterRuleCategories() {
		String[] categories = {"ads", "tracking", "malware", "custom", "phishing"};
		
		for (String category : categories) {
			FilterRule rule = new FilterRule();
			rule.category = category;
			assertEquals(category, rule.category);
		}
	}
	
	@Test
	void testFilterRulePatternWithWildcards() {
		FilterRule rule = new FilterRule();
		
		rule.pattern = "*.ads.com";
		assertEquals("*.ads.com", rule.pattern);
		
		rule.pattern = "*.*.tracking.com";
		assertEquals("*.*.tracking.com", rule.pattern);
	}
	
	@Test
	void testFilterRuleNullValues() {
		FilterRule rule = new FilterRule();
		
		rule.name = null;
		rule.pattern = null;
		rule.category = null;
		rule.redirectTo = null;
		
		assertNull(rule.name);
		assertNull(rule.pattern);
		assertNull(rule.category);
		assertNull(rule.redirectTo);
	}
}

