package dev.pacr.dns.service;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Security service for threat intelligence and malware detection
 */
@ApplicationScoped
public class SecurityService {
	
	private static final Logger LOG = Logger.getLogger(SecurityService.class);
	
	// In-memory threat database (in production, use external threat intelligence
	// APIs)
	private final Set<String> maliciousDomains = ConcurrentHashMap.newKeySet();
	private final Set<String> maliciousIPs = ConcurrentHashMap.newKeySet();
	
	@Inject
	MeterRegistry registry;
	
	/**
	 * Initialize with known malicious domains
	 */
	public void initializeThreatDatabase() {
		// Example malicious domains (in production, integrate with real threat
		// intelligence)
		maliciousDomains.add("malware-test.com");
		maliciousDomains.add("phishing-example.net");
		maliciousDomains.add("trojan-site.org");
		
		// Example malicious IPs
		maliciousIPs.add("198.51.100.1");
		maliciousIPs.add("203.0.113.1");
		
		LOG.info("Threat database initialized");
	}
	
	/**
	 * Check if a domain or its resolved IPs are known threats
	 */
	public boolean checkForThreats(String domain, Iterable<String> resolvedAddresses) {
		// Check domain against malicious domains
		if (isDomainMalicious(domain)) {
			LOG.warnf("Malicious domain detected: %s", domain);
			registry.counter("dns.security.threats.detected", "type", "malicious_domain")
					.increment();
			return true;
		}
		
		// Check resolved IPs against malicious IPs
		if (resolvedAddresses != null) {
			for (String ip : resolvedAddresses) {
				if (maliciousIPs.contains(ip)) {
					LOG.warnf("Malicious IP detected: %s for domain: %s", ip, domain);
					registry.counter("dns.security.threats.detected", "type", "malicious_ip")
							.increment();
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Check if domain is in malicious domain list or matches patterns
	 */
	private boolean isDomainMalicious(String domain) {
		// Direct match
		if (maliciousDomains.contains(domain)) {
			return true;
		}
		
		// Check for subdomain matches
		for (String maliciousDomain : maliciousDomains) {
			if (domain.endsWith('.' + maliciousDomain)) {
				return true;
			}
		}
		
		// Additional heuristics could be added here
		// - DGA detection
		// - Homograph attack detection
		// - Newly registered domain checks
		
		return false;
	}
	
	/**
	 * Add a domain to the threat database
	 */
	public void addMaliciousDomain(String domain) {
		maliciousDomains.add(domain);
		LOG.infof("Added malicious domain: %s", domain);
	}
	
	/**
	 * Remove a domain from the threat database
	 */
	public void removeMaliciousDomain(String domain) {
		maliciousDomains.remove(domain);
		LOG.infof("Removed malicious domain: %s", domain);
	}
	
	/**
	 * Add an IP to the threat database
	 */
	public void addMaliciousIP(String ip) {
		maliciousIPs.add(ip);
		LOG.infof("Added malicious IP: %s", ip);
	}
	
	/**
	 * Remove an IP from the threat database
	 */
	public void removeMaliciousIP(String ip) {
		maliciousIPs.remove(ip);
		LOG.infof("Removed malicious IP: %s", ip);
	}
	
	/**
	 * Get all malicious domains
	 */
	public Set<String> getMaliciousDomains() {
		return new HashSet<>(maliciousDomains);
	}
	
	/**
	 * Get all malicious IPs
	 */
	public Set<String> getMaliciousIPs() {
		return new HashSet<>(maliciousIPs);
	}
	
	/**
	 * Get threat statistics
	 */
	public Map<String, Object> getThreatStats() {
		return Map.of("maliciousDomains", maliciousDomains.size(), "maliciousIPs",
				maliciousIPs.size());
	}
	
	/**
	 * Analyze domain for potential threats (heuristic analysis)
	 */
	public ThreatAnalysis analyzeDomain(String domain) {
		ThreatAnalysis analysis = new ThreatAnalysis();
		analysis.domain = domain;
		analysis.isMalicious = isDomainMalicious(domain);
		
		// Check for suspicious patterns
		analysis.suspiciousPatterns = new ArrayList<>();
		
		// Check for excessive length (possible DGA)
		if (domain.length() > 50) {
			analysis.suspiciousPatterns.add("Unusually long domain name");
		}
		
		// Check for excessive hyphens
		long hyphenCount = domain.chars().filter(ch -> ch == '-').count();
		if (hyphenCount > 3) {
			analysis.suspiciousPatterns.add("Excessive hyphens");
		}
		
		// Check for numbers in domain (possible DGA)
		long digitCount = domain.chars().filter(Character::isDigit).count();
		if (digitCount > domain.length() / 2) {
			analysis.suspiciousPatterns.add("High digit ratio");
		}
		
		analysis.riskScore = calculateRiskScore(analysis);
		
		return analysis;
	}
	
	/**
	 * Calculate risk score based on analysis
	 */
	private int calculateRiskScore(ThreatAnalysis analysis) {
		int score = 0;
		
		if (analysis.isMalicious) {
			score += 100;
		}
		
		score += analysis.suspiciousPatterns.size() * 10;
		
		return Math.min(score, 100);
	}
	
	/**
	 * Threat analysis result
	 */
	public static class ThreatAnalysis {
		public String domain;
		public boolean isMalicious;
		public List<String> suspiciousPatterns;
		public int riskScore;
	}
}
