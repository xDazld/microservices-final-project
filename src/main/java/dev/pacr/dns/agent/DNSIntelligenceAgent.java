package dev.pacr.dns.agent;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.service.SystemMessage;
import dev.pacr.dns.service.DNSFilterService;
import dev.pacr.dns.service.SecurityService;
import io.quarkiverse.langchain4j.RegisterAiService;
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
	
	/**
	 * The LOG.
	 */
	private static final Logger LOG = Logger.getLogger(DNSIntelligenceAgent.class);
	
	@Inject
	DNSFilterService filterService;
	
	@Inject
	SecurityService securityService;
	
	@Inject
	DNSAssistant assistant;
	
	/**
	 * Analyze a domain using AI-powered threat intelligence
	 */
	public ThreatAnalysisResult analyzeDomainWithAI(String domain) {
		LOG.infof("AI Agent analyzing domain: %s", domain);
		
		ThreatAnalysisResult result = new ThreatAnalysisResult();
		result.domain = domain;
		
		
		try {
			String analysis = assistant.analyzeThreat(domain);
			result.aiAnalysis = analysis;
			result.confidence = 0.85;
			result.threatLevel = determineThreatLevel(analysis);
			
		} catch (RuntimeException e) {
			LOG.errorf(e, "AI analysis failed for domain: %s", domain);
			throw e;
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
			
		} catch (RuntimeException e) {
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
			
		} catch (RuntimeException e) {
			LOG.errorf(e, "Failed to correlate security events");
			analysis.patterns = "Analysis failed";
		}
		
		return analysis;
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
@RegisterAiService(tools = DNSAnalysisTools.class)
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
	/**
	 * ThreatAnalysisResult class.
	 */
	public static class ThreatAnalysisResult {
		/**
	 * The domain.
		 */
		public String domain;
		/**
	 * The aiAnalysis.
		 */
		public String aiAnalysis;
		/**
	 * The threatLevel.
		 */
		public String threatLevel;
		/**
	 * The confidence.
		 */
		public double confidence;
	}
	
	/**
	 * FilterRuleRecommendation class.
	 */
	public static class FilterRuleRecommendation {
		/**
	 * The recommendations.
		 */
		public String recommendations;
		/**
	 * The confidence.
		 */
		public double confidence;
	}
	
	/**
	 * SecurityPatternAnalysis class.
	 */
	public static class SecurityPatternAnalysis {
		/**
	 * The eventsAnalyzed.
		 */
		public int eventsAnalyzed;
		/**
	 * The patterns.
		 */
		public String patterns;
		/**
	 * The attackType.
		 */
		public String attackType;
		/**
	 * The confidence.
		 */
		public double confidence;
	}
	
	/**
	 * Tools available to the AI agent
	 */
	@ApplicationScoped
	public static class DNSAnalysisTools {
		
		@Inject
		DNSFilterService filterService;
		
		@Inject
		SecurityService securityService;
		
		@Tool("Check if a domain matches existing filter rules")
		/**
	 * checkFilterMatch method.
		 */
		public String checkFilterMatch(CharSequence domain) {
			boolean isBlocked = filterService.applyFilters(domain).isBlocked();
			return isBlocked ? "Domain is currently blocked" : "Domain is allowed";
		}
		
		@Tool("Check if a domain is in the malicious domain database")
		/**
	 * checkThreatDatabase method.
		 */
		public String checkThreatDatabase(String domain) {
			SecurityService.ThreatAnalysis analysis = securityService.analyzeDomain(domain);
			return analysis.isMalicious ? "Domain is marked as malicious" :
					"Domain is not in threat database";
		}
		
		@Tool("Get statistics about filter rules")
		/**
	 * Gets the FilterStats.
		 * @return the FilterStats
		 */
		public String getFilterStats() {
			return "Total filter rules: " + filterService.getAllRules().size();
		}
	}
}

