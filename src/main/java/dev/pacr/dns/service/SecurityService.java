package dev.pacr.dns.service;

import dev.pacr.dns.storage.MaliciousDomainRepository;
import dev.pacr.dns.storage.MaliciousIPRepository;
import dev.pacr.dns.storage.model.MaliciousDomain;
import dev.pacr.dns.storage.model.MaliciousIP;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Security service for threat intelligence and malware detection
 */
@ApplicationScoped
public class SecurityService {
	
	private static final Logger LOG = Logger.getLogger(SecurityService.class);
	
	@Inject
	MeterRegistry registry;
	
	@Inject
	MaliciousDomainRepository maliciousDomainRepository;
	
	@Inject
	MaliciousIPRepository maliciousIPRepository;
	
	/**
	 * Initialize with known malicious domains
	 */
	public void initializeThreatDatabase() {
		// Check if database already has entries
		if (maliciousDomainRepository.getTotalCount() > 0 ||
				maliciousIPRepository.getTotalCount() > 0) {
			LOG.info("Threat database already initialized");
			return;
		}
		
		// Example malicious domains (in production, integrate with real threat intelligence)
		maliciousDomainRepository.persist(
				new MaliciousDomain("malware-test.com", "test", "Test malware domain"));
		maliciousDomainRepository.persist(
				new MaliciousDomain("phishing-example.net", "test", "Test phishing domain"));
		maliciousDomainRepository.persist(
				new MaliciousDomain("trojan-site.org", "test", "Test trojan domain"));
		
		// Example malicious IPs
		maliciousIPRepository.persist(new MaliciousIP("198.51.100.1", "test", "Test malicious " +
				"IP"));
		maliciousIPRepository.persist(new MaliciousIP("203.0.113.1", "test", "Test malicious IP"));
		
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
				if (maliciousIPRepository.existsByIPAddress(ip)) {
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
		if (maliciousDomainRepository.existsByDomain(domain)) {
			return true;
		}
		
		// Check for subdomain matches - get all domains and check if any match
		List<MaliciousDomain> maliciousDomains = maliciousDomainRepository.listAll();
		for (MaliciousDomain maliciousDomain : maliciousDomains) {
			if (domain.endsWith('.' + maliciousDomain.domain)) {
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
		if (!maliciousDomainRepository.existsByDomain(domain)) {
			maliciousDomainRepository.persist(new MaliciousDomain(domain));
			LOG.infof("Added malicious domain: %s", domain);
		}
	}
	
	/**
	 * Remove a domain from the threat database
	 */
	public void removeMaliciousDomain(String domain) {
		maliciousDomainRepository.deleteByDomain(domain);
		LOG.infof("Removed malicious domain: %s", domain);
	}
	
	/**
	 * Add an IP to the threat database
	 */
	public void addMaliciousIP(String ip) {
		if (!maliciousIPRepository.existsByIPAddress(ip)) {
			maliciousIPRepository.persist(new MaliciousIP(ip));
			LOG.infof("Added malicious IP: %s", ip);
		}
	}
	
	/**
	 * Remove an IP from the threat database
	 */
	public void removeMaliciousIP(String ip) {
		maliciousIPRepository.deleteByIPAddress(ip);
		LOG.infof("Removed malicious IP: %s", ip);
	}
	
	/**
	 * Get all malicious domains
	 */
	public Set<String> getMaliciousDomains() {
		return maliciousDomainRepository.listAll().stream().map(md -> md.domain)
				.collect(Collectors.toSet());
	}
	
	/**
	 * Get all malicious IPs
	 */
	public Set<String> getMaliciousIPs() {
		return maliciousIPRepository.listAll().stream().map(mi -> mi.ipAddress)
				.collect(Collectors.toSet());
	}
	
	/**
	 * Get threat statistics
	 */
	public Map<String, Object> getThreatStats() {
		return Map.of("maliciousDomains", maliciousDomainRepository.getTotalCount(),
				"maliciousIPs",
				maliciousIPRepository.getTotalCount());
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
