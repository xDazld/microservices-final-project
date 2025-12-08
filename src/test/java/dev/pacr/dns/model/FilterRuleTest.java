package dev.pacr.dns.model;

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
		
		assertNotNull(rule.getId());
		assertNotNull(rule.getCreatedAt());
		assertNotNull(rule.getUpdatedAt());
		assertTrue(rule.isEnabled());
		assertEquals(0, rule.getPriority());
	}
	
	@Test
	void testFilterRuleGettersSetters() {
		FilterRule rule = new FilterRule();
		
		rule.setName("Test Rule");
		rule.setPattern("*.example.com");
		rule.setType(FilterRule.RuleType.BLOCK);
		rule.setCategory("ads");
		rule.setPriority(100);
		rule.setEnabled(false);
		
		assertEquals("Test Rule", rule.getName());
		assertEquals("*.example.com", rule.getPattern());
		assertEquals(FilterRule.RuleType.BLOCK, rule.getType());
		assertEquals("ads", rule.getCategory());
		assertEquals(100, rule.getPriority());
		assertFalse(rule.isEnabled());
	}
	
	@Test
	void testFilterRuleIdUniqueness() {
		FilterRule rule1 = new FilterRule();
		FilterRule rule2 = new FilterRule();
		
		assertNotEquals(rule1.getId(), rule2.getId());
	}
	
	@Test
	void testFilterRuleRedirectTo() {
		FilterRule rule = new FilterRule();
		
		rule.setType(FilterRule.RuleType.REDIRECT);
		rule.setRedirectTo("127.0.0.1");
		
		assertEquals(FilterRule.RuleType.REDIRECT, rule.getType());
		assertEquals("127.0.0.1", rule.getRedirectTo());
	}
	
	@Test
	void testFilterRuleBlockType() {
		FilterRule rule = new FilterRule();
		rule.setType(FilterRule.RuleType.BLOCK);
		
		assertEquals(FilterRule.RuleType.BLOCK, rule.getType());
	}
	
	@Test
	void testFilterRuleAllowType() {
		FilterRule rule = new FilterRule();
		rule.setType(FilterRule.RuleType.ALLOW);
		
		assertEquals(FilterRule.RuleType.ALLOW, rule.getType());
	}
	
	@Test
	void testFilterRulePriority() {
		FilterRule rule = new FilterRule();
		
		rule.setPriority(500);
		assertEquals(500, rule.getPriority());
		
		rule.setPriority(0);
		assertEquals(0, rule.getPriority());
		
		rule.setPriority(-1);
		assertEquals(-1, rule.getPriority());
	}
	
	@Test
	void testFilterRuleToggleEnabled() {
		FilterRule rule = new FilterRule();
		
		assertTrue(rule.isEnabled());
		
		rule.setEnabled(false);
		assertFalse(rule.isEnabled());
		
		rule.setEnabled(true);
		assertTrue(rule.isEnabled());
	}
	
	@Test
	void testFilterRuleCreatedAt() {
		FilterRule rule = new FilterRule();
		assertNotNull(rule.getCreatedAt());
	}
	
	@Test
	void testFilterRuleUpdatedAt() {
		FilterRule rule = new FilterRule();
		assertNotNull(rule.getUpdatedAt());
	}
	
	@Test
	void testFilterRuleSetCreatedAt() {
		FilterRule rule = new FilterRule();
		rule.setCreatedAt(rule.getCreatedAt().minusSeconds(3600));
		
		assertNotNull(rule.getCreatedAt());
	}
	
	@Test
	void testFilterRuleSetUpdatedAt() {
		FilterRule rule = new FilterRule();
		rule.setUpdatedAt(rule.getUpdatedAt().plusSeconds(3600));
		
		assertNotNull(rule.getUpdatedAt());
	}
	
	@Test
	void testFilterRuleCompleteSetup() {
		FilterRule rule = new FilterRule();
		rule.setId("rule-123");
		rule.setName("Block Ads");
		rule.setPattern("*.ads.com");
		rule.setType(FilterRule.RuleType.BLOCK);
		rule.setCategory("ads");
		rule.setRedirectTo(null);
		rule.setEnabled(true);
		rule.setPriority(100);
		
		assertEquals("rule-123", rule.getId());
		assertEquals("Block Ads", rule.getName());
		assertEquals("*.ads.com", rule.getPattern());
		assertEquals(FilterRule.RuleType.BLOCK, rule.getType());
		assertEquals("ads", rule.getCategory());
		assertTrue(rule.isEnabled());
		assertEquals(100, rule.getPriority());
	}
	
	@Test
	void testFilterRuleCategories() {
		String[] categories = {"ads", "tracking", "malware", "custom", "phishing"};
		
		for (String category : categories) {
			FilterRule rule = new FilterRule();
			rule.setCategory(category);
			assertEquals(category, rule.getCategory());
		}
	}
	
	@Test
	void testFilterRulePatternWithWildcards() {
		FilterRule rule = new FilterRule();
		
		rule.setPattern("*.ads.com");
		assertEquals("*.ads.com", rule.getPattern());
		
		rule.setPattern("*.*.tracking.com");
		assertEquals("*.*.tracking.com", rule.getPattern());
	}
	
	@Test
	void testFilterRuleNullValues() {
		FilterRule rule = new FilterRule();
		
		rule.setName(null);
		rule.setPattern(null);
		rule.setCategory(null);
		rule.setRedirectTo(null);
		
		assertNull(rule.getName());
		assertNull(rule.getPattern());
		assertNull(rule.getCategory());
		assertNull(rule.getRedirectTo());
	}
}

