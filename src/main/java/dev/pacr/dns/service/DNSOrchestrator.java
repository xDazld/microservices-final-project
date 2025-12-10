package dev.pacr.dns.service;

import dev.pacr.dns.model.FilterResult;
import dev.pacr.dns.model.rfc8427.DnsMessage;
import dev.pacr.dns.model.rfc8427.DnsMessageConverter;
import io.micrometer.core.annotation.Timed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Orchestrator service that coordinates DNS resolution, filtering, and logging
 */
@ApplicationScoped
public class DNSOrchestrator {
	
	private static final Logger LOG = Logger.getLogger(DNSOrchestrator.class);
	
	@Inject
	DNSResolver dnsResolver;
	
	@Inject
	DNSFilterService filterService;
	
	@Inject
	DNSLoggingService loggingService;
	
	@Inject
	SecurityService securityService;
	
	/**
	 * Process a complete DNS query: filter, resolve, and log
	 */
	@Timed(value = "dns.query.total", description = "Total time to process DNS query")
	public DnsMessage processQuery(DnsMessage query) {
		String qname = query.getQname();
		LOG.infof("Processing DNS query for: %s (type=%s)", qname, query.getQtype());
		
		// Step 1: Apply filtering rules
		FilterResult filterResult = filterService.applyFilters(qname);
		
		DnsMessage response;
		
		// Step 2: Check if domain is blocked
		if (filterResult.isBlocked()) {
			LOG.infof("Domain blocked: %s - %s", qname, filterResult.getReason());
			response = createBlockedResponse(query, filterResult);
			
			// Log the blocked query
			loggingService.logQuery(query, response, filterResult);
			
			return response;
		}
		
		// Step 3: Check for redirects
		if (filterResult.getAction() == FilterResult.FilterAction.REDIRECT) {
			LOG.infof("Domain redirected: %s -> %s", qname, filterResult.getRedirectTo());
			// Modify the query to resolve the redirect target
			query.setQname(filterResult.getRedirectTo());
		}
		
		// Step 4: Resolve the DNS query
		response = dnsResolver.resolve(query);
		
		// Step 5: Security check on resolved addresses
		if (response.getRcode() != null && response.getRcode() == 0 &&
				response.getAnswerRRs() != null) {
			List<String> addresses = new ArrayList<>();
			response.getAnswerRRs().forEach(rr -> addresses.add(rr.getRdata()));
			
			boolean isThreat = securityService.checkForThreats(qname, addresses);
			
			if (isThreat) {
				LOG.warnf("Security threat detected for domain: %s", qname);
				response.setRcode(3); // NXDOMAIN
				response.setAnswerRRs(new ArrayList<>());
				response.setAncount(0);
				
				// Log security alert
				loggingService.logSecurityAlert(qname, "MALWARE_DETECTED",
						"Domain identified as malicious");
			}
		}
		
		// Step 6: Log the query
		loggingService.logQuery(query, response, filterResult);
		
		LOG.debugf("Query processing completed for: %s (rcode: %s)", qname, response.getRcode());
		
		return response;
	}
	
	/**
	 * Create a blocked response
	 */
	private DnsMessage createBlockedResponse(DnsMessage query, FilterResult filterResult) {
		// Return NXDOMAIN for blocked domains
		return DnsMessageConverter.createResponse(query.getQname(), query.getQtype(),
				query.getQclass(), 3, // NXDOMAIN
				new ArrayList<>(), 0L);
	}
}
