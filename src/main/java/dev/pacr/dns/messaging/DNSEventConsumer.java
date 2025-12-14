package dev.pacr.dns.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.pacr.dns.agent.DNSIntelligenceAgent;
import dev.pacr.dns.service.DNSFilterService;
import dev.pacr.dns.service.SecurityService;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import java.util.concurrent.CompletionStage;

/**
 * RabbitMQ message consumer for processing DNS query logs and security alerts
 * <p>
 * This service consumes messages from RabbitMQ topics and performs: - Real-time
 * analytics on DNS
 * queries - Autonomous threat detection - Automatic filter rule updates -
 * Security event
 * correlation
 */
@ApplicationScoped
public class DNSEventConsumer {

    private static final Logger LOG = Logger.getLogger(DNSEventConsumer.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Inject
    DNSIntelligenceAgent agent;
    @Inject
    SecurityService securityService;
    @Inject
    DNSFilterService filterService;
    @Inject
    MeterRegistry registry;

    /**
     * Consume and analyze DNS query logs
     * <p>
     * This consumer processes query logs in real-time to: - Detect suspicious
     * patterns - Update
     * threat intelligence - Generate analytics
     */
    @Incoming("dns-query-logs-in")
    public CompletionStage<Void> processQueryLog(Message<String> message) {
        String payload = message.getPayload();

        try {
            JsonNode log = objectMapper.readTree(payload);
            String domain = log.get("domain").asText();
            String status = log.get("status").asText();

            LOG.debugf("Processing query log: %s (%s)", domain, status);

            // Track metrics
            registry.counter("dns.events.processed", "type", "query_log").increment();

            // Analyze for suspicious patterns
            if ("BLOCKED".equals(status)) {
                // AI agent can learn from blocked domains
                DNSIntelligenceAgent.ThreatAnalysisResult analysis = agent.analyzeDomainWithAI(domain);

                if ("HIGH".equals(analysis.threatLevel)) {
                    LOG.warnf("High-threat domain detected in logs: %s", domain);
                    securityService.addMaliciousDomain(domain);
                }
            }

            // Check for DGA patterns in allowed domains
            if ("ALLOWED".equals(status) && isDGAPattern(domain)) {
                LOG.warnf("Possible DGA domain detected: %s", domain);
                registry.counter("dns.security.dga_detected").increment();
            }

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            LOG.errorf(e, "Error processing query log: %s", payload);
            registry.counter("dns.events.processing_errors", "type", "query_log").increment();
        }

        return message.ack();
    }

    /**
     * Consume and respond to security alerts
     * <p>
     * This consumer handles security alerts and takes autonomous actions: -
     * Automatic blocking of
     * malicious domains - Alert correlation and pattern detection - Escalation of
     * critical threats
     */
    @Incoming("dns-security-alerts-in")
    public CompletionStage<Void> processSecurityAlert(Message<String> message) {
        String payload = message.getPayload();

        try {
            JsonNode alert = objectMapper.readTree(payload);
            String domain = alert.get("domain").asText();
            String alertType = alert.get("alertType").asText();

            LOG.warnf("Processing security alert: %s - %s", alertType, domain);

            // Track metrics
            registry.counter("dns.events.processed", "type", "security_alert").increment();
            registry.counter("dns.security.alerts", "type", alertType).increment();

            // Autonomous response based on alert type
            switch (alertType) {
                case "MALWARE_DETECTED":
                    handleMalwareAlert(domain);
                    break;
                case "DGA_DETECTED":
                    handleDGAAlert(domain);
                    break;
                case "PHISHING_DETECTED":
                    handlePhishingAlert(domain);
                    break;
                default:
                    LOG.infof("Unknown alert type: %s", alertType);
            }

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            LOG.errorf(e, "Error processing security alert: %s", payload);
            registry.counter("dns.events.processing_errors", "type", "security_alert").increment();
        }

        return message.ack();
    }

    /**
     * Consume threat intelligence updates from external sources
     */
    @Incoming("threat-intelligence-updates")
    public CompletionStage<Void> processThreatIntelligence(Message<String> message) {
        String payload = message.getPayload();

        try {
            JsonNode update = objectMapper.readTree(payload);
            String domain = update.get("domain").asText();
            String threatType = update.get("threat_type").asText();

            LOG.infof("Received threat intelligence update: %s (%s)", domain, threatType);

            // Add to threat database
            securityService.addMaliciousDomain(domain);

            // Optionally create blocking rule
            if ("malware".equalsIgnoreCase(threatType) || "phishing".equalsIgnoreCase(threatType)) {

                filterService.addRule("Auto-block: " + domain, domain,
                        dev.pacr.dns.model.FilterRule.RuleType.BLOCK, threatType.toLowerCase(), 90
                // High priority
                );

                LOG.infof("Auto-created blocking rule for threat: %s", domain);
            }

            registry.counter("dns.threat_intel.updates_processed").increment();

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            LOG.errorf(e, "Error processing threat intelligence: %s", payload);
        }

        return message.ack();
    }

    /**
     * Handle malware alerts - autonomous blocking
     */
    private void handleMalwareAlert(String domain) {
        LOG.warnf("Autonomous response: Blocking malware domain: %s", domain);

        // Add to threat database
        securityService.addMaliciousDomain(domain);

        // Create high-priority blocking rule
        filterService.addRule("Auto-block malware: " + domain, domain,
                dev.pacr.dns.model.FilterRule.RuleType.BLOCK, "malware", 95);

        registry.counter("dns.autonomous.blocks", "reason", "malware").increment();
    }

    /**
     * Handle DGA alerts - pattern-based blocking
     */
    private void handleDGAAlert(String domain) {
        LOG.warnf("DGA pattern detected: %s", domain);

        // Use AI agent to analyze and recommend blocking rules
        DNSIntelligenceAgent.ThreatAnalysisResult analysis = agent.analyzeDomainWithAI(domain);

        if ("HIGH".equals(analysis.threatLevel)) {
            securityService.addMaliciousDomain(domain);
            registry.counter("dns.autonomous.blocks", "reason", "dga").increment();
        }
    }

    /**
     * Handle phishing alerts
     */
    private void handlePhishingAlert(String domain) {
        LOG.warnf("Phishing domain detected: %s", domain);

        securityService.addMaliciousDomain(domain);

        filterService.addRule("Auto-block phishing: " + domain, domain,
                dev.pacr.dns.model.FilterRule.RuleType.BLOCK, "phishing", 90);

        registry.counter("dns.autonomous.blocks", "reason", "phishing").increment();
    }

    /**
     * Simple DGA detection heuristic
     */
    private boolean isDGAPattern(String domain) {
        // Check for long random-looking strings
        String domainPart = domain.split("\\.")[0];

        if (domainPart.length() > 15) {
            // Count consonant clusters
            int consonantClusters = 0;
            for (int i = 0; i < domainPart.length() - 2; i++) {
                if (isVowel(domainPart.charAt(i)) && isVowel(domainPart.charAt(i + 1)) &&
                        isVowel(domainPart.charAt(i + 2))) {
                    consonantClusters++;
                }
            }

            // DGA domains often have many consonant clusters
            return consonantClusters > 3;
        }

        return false;
    }

    private boolean isVowel(char c) {
        return "aeiou".indexOf(Character.toLowerCase(c)) < 0;
    }
}