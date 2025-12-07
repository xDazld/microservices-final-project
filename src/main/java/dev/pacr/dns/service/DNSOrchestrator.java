package dev.pacr.dns.service;

import dev.pacr.dns.model.DNSQuery;
import dev.pacr.dns.model.DNSResponse;
import dev.pacr.dns.model.FilterResult;
import io.micrometer.core.annotation.Timed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

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
	public DNSResponse processQuery(DNSQuery query) {
		LOG.infof("Processing DNS query for: %s from %s", query.getDomain(), query.getClientIp());
		
		// Step 1: Apply filtering rules
		FilterResult filterResult = filterService.applyFilters(query.getDomain());
		
		DNSResponse response;
		
		// Step 2: Check if domain is blocked
		if (filterResult.isBlocked()) {
			LOG.infof("Domain blocked: %s - %s", query.getDomain(), filterResult.getReason());
			response = createBlockedResponse(query, filterResult);
			
			// Log the blocked query
			loggingService.logQuery(query, response, filterResult);
			
			return response;
		}
		
		// Step 3: Check for redirects
		if (filterResult.getAction() == FilterResult.FilterAction.REDIRECT) {
			LOG.infof("Domain redirected: %s -> %s", query.getDomain(),
					filterResult.getRedirectTo());
			// Modify the query to resolve the redirect target
			query.setDomain(filterResult.getRedirectTo());
		}
		
		// Step 4: Resolve the DNS query
		response = dnsResolver.resolve(query);
		
		// Step 5: Security check on resolved addresses
		if (response.getStatus().equals("ALLOWED") && response.getResolvedAddresses() != null) {
			boolean isThreat = securityService.checkForThreats(query.getDomain(),
					response.getResolvedAddresses());
			
			if (isThreat) {
				LOG.warnf("Security threat detected for domain: %s", query.getDomain());
				response.setStatus("BLOCKED");
				response.setBlockReason("Security threat detected");
				response.setResolvedAddresses(List.of());
				
				// Log security alert
				loggingService.logSecurityAlert(query.getDomain(), "MALWARE_DETECTED",
						"Domain identified as malicious");
			}
		}
		
		// Step 6: Log the query
		loggingService.logQuery(query, response, filterResult);
		
		LOG.debugf("Query processing completed for: %s (status: %s)", query.getDomain(),
				response.getStatus());
		
		return response;
	}
	
	/**
	 * Create a blocked response
	 */
	private DNSResponse createBlockedResponse(DNSQuery query, FilterResult filterResult) {
		DNSResponse response = new DNSResponse();
		response.setQueryId(query.getId());
		response.setDomain(query.getDomain());
		response.setStatus("BLOCKED");
		response.setBlockReason(filterResult.getReason());
		response.setResolvedAddresses(List.of());
		response.setResponseTimeMs(0);
		response.setCached(false);
		
		return response;
	}
}
