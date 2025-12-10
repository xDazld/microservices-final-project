package dev.pacr.dns.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.pacr.dns.model.FilterResult;
import dev.pacr.dns.model.rfc8427.DnsMessage;
import dev.pacr.dns.model.rfc8618.Block;
import dev.pacr.dns.model.rfc8618.CdnsConverter;
import dev.pacr.dns.model.rfc8618.CdnsFile;
import io.smallrye.reactive.messaging.kafka.api.OutgoingKafkaRecordMetadata;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for logging DNS queries in RFC 8618 C-DNS format to Kafka
 */
@ApplicationScoped
public class DNSLoggingService {
	
	private static final Logger LOG = Logger.getLogger(DNSLoggingService.class);
	private final ObjectMapper objectMapper;
	private final CdnsConverter cdnsConverter;
	private Block currentBlock;
	private Instant blockStartTime;
	
	@Inject
	@Channel("dns-query-logs")
	Emitter<String> logsEmitter;
	
	public DNSLoggingService() {
		this.objectMapper = new ObjectMapper();
		this.objectMapper.registerModule(new JavaTimeModule());
		this.cdnsConverter = new CdnsConverter();
		this.blockStartTime = Instant.now();
		this.currentBlock = cdnsConverter.createBlock(blockStartTime);
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
			
			// Add to C-DNS block
			cdnsConverter.addQueryResponse(currentBlock,
					"unknown", // Client IP not available in DnsMessage
					query.getQname(), query.getQtype(), query.getQclass(), Instant.now(), answers,
					0L // Response time not tracked in this simplified version
			);
			
			// Send block to Kafka when it reaches a threshold
			if (currentBlock.getQueryResponses().size() >= 100) {
				flushBlock();
			}
			
			LOG.debugf("Logged DNS query: %s (rcode=%d)", query.getQname(), response.getRcode());
			
		} catch (Exception e) {
			LOG.errorf(e, "Failed to log DNS query: %s", query.getQname());
		}
	}
	
	/**
	 * Flush current C-DNS block to Kafka
	 */
	private void flushBlock() {
		try {
			CdnsFile file = cdnsConverter.createCdnsFile();
			file.setFileBlocks(List.of(currentBlock));
			
			String jsonLog = objectMapper.writeValueAsString(file);
			
			Message<String> message = Message.of(jsonLog).addMetadata(
					OutgoingKafkaRecordMetadata.<String>builder()
							.withKey("cdns-block-" + blockStartTime.toEpochMilli())
							.withTopic("dns-query-logs").build());
			
			logsEmitter.send(message);
			
			LOG.infof("Flushed C-DNS block with %d query/response pairs",
					currentBlock.getQueryResponses().size());
			
			// Start new block
			blockStartTime = Instant.now();
			currentBlock = cdnsConverter.createBlock(blockStartTime);
			
		} catch (JsonProcessingException e) {
			LOG.errorf(e, "Failed to serialize C-DNS block");
		}
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
