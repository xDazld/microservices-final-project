package dev.pacr.dns.service;

import dev.pacr.dns.DNSResponseCodes;
import dev.pacr.dns.model.FilterResult;
import dev.pacr.dns.model.rfc8427.DnsMessage;
import dev.pacr.dns.model.rfc8427.DnsMessageConverter;
import io.micrometer.core.annotation.Timed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.List;

/**
  * Orchestrator service that coordinates DNS resolution, filtering, and logging
  *
  * @author Patrick Rafferty
  */
@ApplicationScoped
public class DNSOrchestrator {
	
	/**
	  * Logger for this class
	  */
	private static final Logger LOG = Logger.getLogger(DNSOrchestrator.class);
	
	/** DNS Resolver service */
	@Inject
	DNSResolver dnsResolver;
	
	/** DNS Filter service */
	@Inject
	DNSFilterService filterService;
	
	/** DNS Logging service instance */
	@Inject
	Instance<DNSLoggingService> loggingService;
	
	/** Query log service */
	@Inject
	QueryLogService queryLogService;
	
	/** Security service */
	@Inject
	SecurityService securityService;
	
	/**
	  * Process a complete DNS query: filter, resolve, and log
	  *
	  * @param query the DNS query message
	  * @return the DNS response message
	  */
	@Timed(value = "dns.query.total", description = "Total time to process DNS query")
	public DnsMessage processQuery(DnsMessage query) {
		return processQuery(query, "unknown");
	}
	
	/**
	  * Process a complete DNS query: filter, resolve, and log with client IP
	  *
	  * @param query the DNS query message
	  * @param clientIp the client IP address for logging
	  * @return the DNS response message
	  */
	@Timed(value = "dns.query.total", description = "Total time to process DNS query")
	public DnsMessage processQuery(DnsMessage query, String clientIp) {
		String qname = query.getQname();
		String queryType = query.getQtype() != null ? query.getQtype().toString() : "A";
		LOG.infof("Processing DNS query for: %s (type=%s) from %s", qname, queryType, clientIp);
		
		// Step 1: Apply filtering rules
		FilterResult filterResult = filterService.applyFilters(qname);
		
		DnsMessage response;
		
		// Step 2: Check if domain is blocked
		if (filterResult.isBlocked()) {
			LOG.infof("Domain blocked: %s - %s", qname, filterResult.getReason());
			response = createBlockedResponse(query);
			
			// Log the blocked query via streaming if available
			if (loggingService.isResolvable()) {
				loggingService.get().logQuery(query, response, filterResult);
			}
			queryLogService.logQuery(qname, queryType, "BLOCKED", DNSResponseCodes.NXDOMAIN,
					new ArrayList<>(), clientIp);
			
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
		if (response.getRcode() != null && response.getRcode() == DNSResponseCodes.NO_ERROR &&
				response.getAnswerRRs() != null) {
			List<String> addresses = new ArrayList<>();
			response.getAnswerRRs().forEach(rr -> addresses.add(rr.getRdata()));
			
			boolean isThreat = securityService.checkForThreats(qname, addresses);
			
			if (isThreat) {
				LOG.warnf("Security threat detected for domain: %s", qname);
				response.setRcode(DNSResponseCodes.NXDOMAIN); // NXDOMAIN
				response.setAnswerRRs(new ArrayList<>());
				response.setAncount(0);
				
				// Log security alert via streaming if available
				if (loggingService.isResolvable()) {
					loggingService.get().logSecurityAlert(qname, "MALWARE_DETECTED",
						"Domain identified as malicious");
				}
				queryLogService.logQuery(qname, queryType, "THREAT", DNSResponseCodes.NXDOMAIN,
						new ArrayList<>(),
						clientIp);
			} else {
				// Log allowed query with answers
				queryLogService.logQuery(qname, queryType, "ALLOWED", response.getRcode(),
						addresses, clientIp);
			}
		} else {
			// Log allowed query (no answers or error)
			String status = response.getRcode() != null &&
					response.getRcode() != DNSResponseCodes.NO_ERROR ? "ERROR" : "ALLOWED";
			queryLogService.logQuery(qname, queryType, status,
					response.getRcode() != null ? response.getRcode() : DNSResponseCodes.NO_ERROR,
					new ArrayList<>(),
					clientIp);
		}
		
		// Log the query via streaming if available
		if (loggingService.isResolvable()) {
			loggingService.get().logQuery(query, response, filterResult);
		}
		
		LOG.debugf("Query processing completed for: %s (rcode: %s)", qname, response.getRcode());
		
		return response;
	}
	
	/**
	  * Create a blocked response
	  *
	  * @param query the original DNS query
	  * @return a DNS response indicating the domain is blocked
	  */
	private DnsMessage createBlockedResponse(DnsMessage query) {
		// Return NXDOMAIN for blocked domains
		return DnsMessageConverter.createResponse(query.getQname(), query.getQtype(),
				query.getQclass(), DNSResponseCodes.NXDOMAIN, // NXDOMAIN
				new ArrayList<>(), 0L);
	}
}
