package dev.pacr.dns.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.pacr.dns.model.FilterResult;
import dev.pacr.dns.model.rfc8427.DnsMessage;
import dev.pacr.dns.model.rfc8618.CdnsConverter;
import dev.pacr.dns.storage.DNSLogRepository;
import dev.pacr.dns.storage.SecurityAlertRepository;
import dev.pacr.dns.storage.model.DNSLogEntry;
import dev.pacr.dns.storage.model.SecurityAlertEntry;
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
    
    @Inject
    DNSLogRepository logRepository;
    
    @Inject
    SecurityAlertRepository alertRepository;
    
    @Inject
    @Channel("dns-query-logs")
    Emitter<String> logsEmitter;
    
    @Inject
    @Channel("dns-security-alerts")
    Emitter<String> alertsEmitter;
    
    public DNSLoggingService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        CdnsConverter cdnsConverter = new CdnsConverter();
    }
    
    /**
     * Log a DNS query and its response in RFC 8618 C-DNS format and persist to DB
     */
    public void logQuery(DnsMessage query, DnsMessage response, FilterResult filterResult) {
        try {
            // Extract answers from response
            List<String> answers = new ArrayList<>();
            if (response.getAnswerRRs() != null) {
                response.getAnswerRRs().forEach(rr -> answers.add(rr.getRdata()));
            }
            
            // Create persistent log entry
            DNSLogEntry entry = new DNSLogEntry();
            entry.domain = query.getQname();
            entry.status = filterResult.getAction().toString();
            entry.rcode = response.getRcode();
            entry.answers = answers;
            entry.timestamp = java.time.Instant.now();
            
            // Persist to database
            logRepository.saveLog(entry);
			
			// Also emit to RabbitMQ for streaming/monitoring (non-blocking)
            String jsonLog = objectMapper.writeValueAsString(entry);
			try {
				logsEmitter.send(jsonLog);
			} catch (IllegalStateException e) {
				// No subscribers downstream - this is ok, continue without failing
				LOG.debugf("No downstream subscribers for DNS query logs: %s", e.getMessage());
			}
            
            LOG.debugf("Logged DNS query: %s (rcode=%d)", query.getQname(), response.getRcode());
            
        } catch (Exception e) {
            LOG.errorf(e, "Failed to log DNS query: %s", query.getQname());
        }
    }
    
    /**
     * Log a security alert and persist to DB
     */
    public void logSecurityAlert(String domain, String alertType, String description) {
        try {
            SecurityAlertEntry alert = new SecurityAlertEntry();
            alert.domain = domain;
            alert.alertType = alertType;
            alert.description = description;
            alert.timestamp = java.time.Instant.now();
            
            // Persist to database
            alertRepository.save(alert);
			
			// Emit to RabbitMQ for streaming/monitoring (non-blocking)
            String jsonAlert = objectMapper.writeValueAsString(alert);
			try {
				alertsEmitter.send(jsonAlert);
			} catch (IllegalStateException e) {
				// No subscribers downstream - this is ok, continue without failing
				LOG.debugf("No downstream subscribers for security alerts: %s", e.getMessage());
			}
            
            LOG.warnf("Security alert logged: %s - %s", alertType, domain);
            
        } catch (Exception e) {
            LOG.errorf(e, "Failed to log security alert for domain: %s", domain);
        }
    }
}
