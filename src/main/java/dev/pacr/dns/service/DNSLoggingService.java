package dev.pacr.dns.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.pacr.dns.model.FilterResult;
import dev.pacr.dns.model.rfc8427.DnsMessage;
import dev.pacr.dns.model.rfc8618.CdnsConverter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for logging DNS queries in RFC 8618 C-DNS format to RabbitMQ
 */
@ApplicationScoped
public class DNSLoggingService {

	private static final Logger LOG = Logger.getLogger(DNSLoggingService.class);
	private final ObjectMapper objectMapper;
	private final CdnsConverter cdnsConverter;

	@Inject
	@Channel("dns-query-logs")
	Emitter<String> logsEmitter;

	@Inject
	@Channel("dns-security-alerts")
	Emitter<String> alertsEmitter;

	public DNSLoggingService() {
		this.objectMapper = new ObjectMapper();
		this.objectMapper.registerModule(new JavaTimeModule());
		this.cdnsConverter = new CdnsConverter();
	}

	/**
	 * Log a DNS query and its response in RFC 8618 C-DNS format to RabbitMQ
	 */
	public void logQuery(DnsMessage query, DnsMessage response, FilterResult filterResult) {
		try {
			// Extract answers from response
			List<String> answers = new ArrayList<>();
			if (response.getAnswerRRs() != null) {
				response.getAnswerRRs().forEach(rr -> answers.add(rr.getRdata()));
			}

			// Create log entry
			var logEntry = new QueryLogEntry(query.getQname(), filterResult.getAction().toString(),
					response.getRcode(), answers);
			String jsonLog = objectMapper.writeValueAsString(logEntry);

			// Send to RabbitMQ
			logsEmitter.send(jsonLog);

			LOG.debugf("Logged DNS query: %s (rcode=%d)", query.getQname(), response.getRcode());

		} catch (Exception e) {
			LOG.errorf(e, "Failed to log DNS query: %s", query.getQname());
		}
	}

	/**
	 * Log a security alert to RabbitMQ
	 */
	public void logSecurityAlert(String domain, String alertType, String description) {
		try {
			var alert = new SecurityAlert(domain, alertType, description);
			String jsonAlert = objectMapper.writeValueAsString(alert);

			// Send to RabbitMQ
			alertsEmitter.send(jsonAlert);

			LOG.warnf("Security alert logged: %s - %s", alertType, domain);

		} catch (Exception e) {
			LOG.errorf(e, "Failed to log security alert for domain: %s", domain);
		}
	}

	/**
	 * Inner class for query log entries
	 */
	private static class QueryLogEntry {
		public final String domain;
		public final String status;
		public final int rcode;
		public final List<String> answers;
		public final String timestamp;

		public QueryLogEntry(String domain, String status, int rcode, List<String> answers) {
			this.domain = domain;
			this.status = status;
			this.rcode = rcode;
			this.answers = answers;
			this.timestamp = java.time.Instant.now().toString();
		}
	}

	/**
	 * Inner class for security alerts
	 */
	private static class SecurityAlert {
		public final String domain;
		public final String alertType;
		public final String description;
		public final String timestamp;

		public SecurityAlert(String domain, String alertType, String description) {
			this.domain = domain;
			this.alertType = alertType;
			this.description = description;
			this.timestamp = java.time.Instant.now().toString();
		}
	}
}
