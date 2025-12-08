package dev.pacr.dns.agent;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.pacr.dns.service.DNSFilterService;
import dev.pacr.dns.service.SecurityService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

/**
 * AI Agent for DNS intelligence and autonomous threat analysis
 * <p>
 * This agent uses LangChain4j to provide: - Autonomous threat analysis - Intelligent filter rule
 * recommendations - Pattern recognition in DNS queries - Security alert correlation
 */
@ApplicationScoped
public class DNSIntelligenceAgent {
	
	private static final Logger LOG = Logger.getLogger(DNSIntelligenceAgent.class);
	
	@Inject
	DNSFilterService filterService;
	
	@Inject
	SecurityService securityService;
	
	private DNSAssistant assistant;
	
	@PostConstruct
	public void init() {
		LOG.info("Initializing DNS Intelligence Agent");
		
		// Note: In production, configure with actual LLM provider
		// For now, this provides the structure for agent-based services
		try {
			assistant = AiServices.builder(DNSAssistant.class)
					.chatMemory(MessageWindowChatMemory.withMaxMessages(10))
					.tools(new DNSAnalysisTools()).build();
			
			LOG.info("DNS Intelligence Agent initialized successfully");
		} catch (Exception e) {
			LOG.warn("Could not initialize AI services (LLM provider not configured): " +
					e.getMessage());
			// Agent will operate in degraded mode with rule-based analysis
		}
	}
	
	/**
	 * Analyze a domain using AI-powered threat intelligence
	 */
	public ThreatAnalysisResult analyzeDomainWithAI(String domain) {
		LOG.infof("AI Agent analyzing domain: %s", domain);
		
		ThreatAnalysisResult result = new ThreatAnalysisResult();
		result.domain = domain;
		
		// Rule-based analysis (fallback when AI is not available)
		if (assistant == null) {
			return performRuleBasedAnalysis(domain);
		}
		
		try {
			String analysis = assistant.analyzeThreat(domain);
			result.aiAnalysis = analysis;
			result.confidence = 0.85;
			result.threatLevel = determineThreatLevel(analysis);
			
		} catch (Exception e) {
			LOG.errorf(e, "AI analysis failed for domain: %s", domain);
			return performRuleBasedAnalysis(domain);
		}
		
		return result;
	}
	
	/**
	 * Get recommendations for new filter rules based on observed patterns
	 */
	public FilterRuleRecommendation getFilterRecommendations(String[] recentDomains) {
		LOG.info("Generating filter recommendations from observed patterns");
		
		FilterRuleRecommendation recommendation = new FilterRuleRecommendation();
		
		if (assistant == null) {
			recommendation.recommendations = "AI agent not available for recommendations";
			return recommendation;
		}
		
		try {
			String prompt = "Analyze these domains and recommend filter rules: " +
					String.join(", ", recentDomains);
			recommendation.recommendations = assistant.recommendFilters(prompt);
			recommendation.confidence = 0.75;
			
		} catch (Exception e) {
			LOG.errorf(e, "Failed to generate filter recommendations");
			recommendation.recommendations = "Error generating recommendations";
		}
		
		return recommendation;
	}
	
	/**
	 * Correlate security events and identify attack patterns
	 */
	public SecurityPatternAnalysis correlateSecurityEvents(String[] events) {
		LOG.info("Correlating security events using AI");
		
		SecurityPatternAnalysis analysis = new SecurityPatternAnalysis();
		analysis.eventsAnalyzed = events.length;
		
		if (assistant == null) {
			analysis.patterns = "Rule-based detection only";
			analysis.attackType = "UNKNOWN";
			return analysis;
		}
		
		try {
			String eventSummary = String.join("; ", events);
			String pattern = assistant.identifyAttackPattern(eventSummary);
			
			analysis.patterns = pattern;
			analysis.attackType = extractAttackType(pattern);
			analysis.confidence = 0.80;
			
		} catch (Exception e) {
			LOG.errorf(e, "Failed to correlate security events");
			analysis.patterns = "Analysis failed";
		}
		
		return analysis;
	}
	
	/**
	 * Fallback rule-based analysis when AI is not available
	 */
	private ThreatAnalysisResult performRuleBasedAnalysis(String domain) {
		ThreatAnalysisResult result = new ThreatAnalysisResult();
		result.domain = domain;
		result.aiAnalysis = "Rule-based analysis (AI not available)";
		
		// Check domain characteristics
		boolean hasNumbers = domain.matches(".*\\d{5,}.*");
		boolean hasRandomChars = domain.matches(".*[a-z]{15,}.*");
		boolean isNewTLD = domain.matches(".+\\.(xyz|top|club|online)$");
		
		int suspicionScore = 0;
		if (hasNumbers) {
			suspicionScore += 30;
		}
		if (hasRandomChars) {
			suspicionScore += 25;
		}
		if (isNewTLD) {
			suspicionScore += 20;
		}
		
		if (suspicionScore >= 50) {
			result.threatLevel = "HIGH";
			result.confidence = 0.70;
		} else if (suspicionScore >= 30) {
			result.threatLevel = "MEDIUM";
			result.confidence = 0.60;
		} else {
			result.threatLevel = "LOW";
			result.confidence = 0.50;
		}
		
		return result;
	}
	
	private String determineThreatLevel(String analysis) {
		String lower = analysis.toLowerCase();
		if (lower.contains("high") || lower.contains("danger") || lower.contains("malicious")) {
			return "HIGH";
		} else if (lower.contains("suspicious") || lower.contains("moderate")) {
			return "MEDIUM";
		}
		return "LOW";
	}
	
	private String extractAttackType(String pattern) {
		String lower = pattern.toLowerCase();
		if (lower.contains("dga") || lower.contains("domain generation")) {
			return "DGA";
		}
		if (lower.contains("phish")) {
			return "PHISHING";
		}
		if (lower.contains("ddos")) {
			return "DDOS";
		}
		if (lower.contains("exfiltration")) {
			return "DATA_EXFILTRATION";
		}
		return "UNKNOWN";
	}
	
	/**
	 * AI Assistant interface for DNS analysis
	 */
	public interface DNSAssistant {
		
		@SystemMessage("You are a DNS security expert. Analyze domains for threats, " +
				"considering DGA patterns, phishing indicators, malware distribution, " +
				"and other security concerns. Provide concise threat assessments.")
		String analyzeThreat(String domain);
		
		@SystemMessage("You are a DNS filtering expert. Based on observed domain patterns, " +
				"recommend effective filter rules. Focus on blocking ads, tracking, and malware.")
		String recommendFilters(String observedDomains);
		
		@SystemMessage("You are a cybersecurity analyst. Analyze security events and identify " +
				"attack patterns, correlate events, and determine attack types.")
		String identifyAttackPattern(String events);
	}
	
	// Result classes
	public static class ThreatAnalysisResult {
		public String domain;
		public String aiAnalysis;
		public String threatLevel;
		public double confidence;
	}
	
	public static class FilterRuleRecommendation {
		public String recommendations;
		public double confidence;
	}
	
	public static class SecurityPatternAnalysis {
		public int eventsAnalyzed;
		public String patterns;
		public String attackType;
		public double confidence;
	}
	
	/**
	 * Tools available to the AI agent
	 */
	public class DNSAnalysisTools {
		
		@Tool("Check if a domain matches existing filter rules")
		public String checkFilterMatch(String domain) {
			boolean isBlocked = filterService.applyFilters(domain).isBlocked();
			return isBlocked ? "Domain is currently blocked" : "Domain is allowed";
		}
		
		@Tool("Check if a domain is in the malicious domain database")
		public String checkThreatDatabase(String domain) {
			SecurityService.ThreatAnalysis analysis = securityService.analyzeDomain(domain);
			return analysis.isMalicious ? "Domain is marked as malicious" :
					"Domain is not in threat database";
		}
		
		@Tool("Get statistics about filter rules")
		public String getFilterStats() {
			return "Total filter rules: " + filterService.getAllRules().size();
		}
	}
}

