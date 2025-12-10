package dev.pacr.dns.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.pacr.dns.model.FilterResult;
import dev.pacr.dns.model.rfc8427.DnsMessage;
import dev.pacr.dns.model.rfc8618.CdnsConverter;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for logging DNS queries in RFC 8618 C-DNS format
 */
@ApplicationScoped
public class DNSLoggingService {
	
	private static final Logger LOG = Logger.getLogger(DNSLoggingService.class);
	private final ObjectMapper objectMapper;
	private final CdnsConverter cdnsConverter;
	
	public DNSLoggingService() {
		this.objectMapper = new ObjectMapper();
		this.objectMapper.registerModule(new JavaTimeModule());
		this.cdnsConverter = new CdnsConverter();
	}
	
	/**
	 * Log a DNS query and its response in RFC 8618 C-DNS format
	 */
	public void logQuery(DnsMessage query, DnsMessage response, FilterResult filterResult) {
		try {
			// Extract answers from response
			List<String> answers = new ArrayList<>();
			if (response.getAnswerRRs() != null) {
				response.getAnswerRRs().forEach(rr -> answers.add(rr.getRdata()));
			}
			
			LOG.debugf("Logged DNS query: %s (rcode=%d)", query.getQname(), response.getRcode());
			
		} catch (Exception e) {
			LOG.errorf(e, "Failed to log DNS query: %s", query.getQname());
		}
	}
	
	/**
	 * Log a security alert
	 */
	public void logSecurityAlert(String domain, String alertType, String description) {
		LOG.warnf("Security alert logged: %s - %s", alertType, domain);
	}
}
