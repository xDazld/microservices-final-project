package dev.pacr.dns.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.pacr.dns.model.DNSQuery;
import dev.pacr.dns.model.DNSQueryLog;
import dev.pacr.dns.model.DNSResponse;
import dev.pacr.dns.model.FilterResult;
import io.smallrye.reactive.messaging.kafka.api.OutgoingKafkaRecordMetadata;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

/**
 * Service for logging DNS queries to Kafka
 */
@ApplicationScoped
public class DNSLoggingService {
	
	private static final Logger LOG = Logger.getLogger(DNSLoggingService.class);
	private final ObjectMapper objectMapper;
	@Inject
	@Channel("dns-query-logs")
	Emitter<String> logsEmitter;
	
	public DNSLoggingService() {
		this.objectMapper = new ObjectMapper();
		this.objectMapper.registerModule(new JavaTimeModule());
	}
	
	/**
	 * Log a DNS query and its response
	 */
	public void logQuery(DNSQuery query, DNSResponse response, FilterResult filterResult) {
		try {
			DNSQueryLog log = createLogEntry(query, response, filterResult);
			String jsonLog = objectMapper.writeValueAsString(log);
			
			// Create message with metadata
			Message<String> message = Message.of(jsonLog).addMetadata(
					OutgoingKafkaRecordMetadata.<String>builder().withKey(query.getDomain())
							.withTopic("dns-query-logs").build());
			
			logsEmitter.send(message);
			
			LOG.debugf("Logged DNS query: %s (%s)", query.getDomain(), response.getStatus());
			
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		} catch (Exception e) {
			LOG.errorf(e, "Failed to log DNS query: %s", query.getDomain());
		}
	}
	
	/**
	 * Create a log entry from query, response, and filter result
	 */
	private DNSQueryLog createLogEntry(DNSQuery query, DNSResponse response,
									   FilterResult filterResult) {
		DNSQueryLog log = new DNSQueryLog();
		log.setId(query.getId());
		log.setDomain(query.getDomain());
		log.setQueryType(query.getQueryType());
		log.setClientIp(query.getClientIp());
		log.setProtocol(query.getProtocol());
		log.setTimestamp(query.getTimestamp());
		
		log.setStatus(response.getStatus());
		log.setResponseTimeMs(response.getResponseTimeMs());
		log.setCached(response.isCached());
		
		if (response.getResolvedAddresses() != null) {
			log.setResolvedAddresses(response.getResolvedAddresses().toArray(new String[0]));
		}
		
		if (filterResult != null && filterResult.isBlocked()) {
			log.setBlockReason(filterResult.getReason());
			if (filterResult.getMatchedRule() != null) {
				log.setFilterCategory(filterResult.getMatchedRule().getCategory());
			}
		}
		
		return log;
	}
	
	/**
	 * Log a security alert
	 */
	public void logSecurityAlert(String domain, String alertType, String description) {
		try {
			var alert = new SecurityAlert(domain, alertType, description);
			String jsonAlert = objectMapper.writeValueAsString(alert);
			
			Message<String> message = Message.of(jsonAlert).addMetadata(
					OutgoingKafkaRecordMetadata.<String>builder().withKey(domain)
							.withTopic("dns-security-alerts").build());
			
			logsEmitter.send(message);
			
			LOG.warnf("Security alert logged: %s - %s", alertType, domain);
			
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		} catch (Exception e) {
			LOG.errorf(e, "Failed to log security alert for domain: %s", domain);
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
