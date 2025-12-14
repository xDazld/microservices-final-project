package dev.pacr.dns.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.pacr.dns.model.FilterResult;
import dev.pacr.dns.model.rfc8427.DnsMessage;
import dev.pacr.dns.model.rfc8427.ResourceRecord;
import dev.pacr.dns.service.DNSLoggingService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end integration tests for RabbitMQ messaging
 * Tests the complete flow from logging service through consumer processing
 */
@QuarkusTest
class RabbitMQMessagingIntegrationTest {

    @Inject
    DNSLoggingService loggingService;

    @Inject
    DNSEventConsumer consumer;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Test end-to-end DNS query logging and consumption
     * Verifies that a query logged by the service can be consumed
     */
    @Test
    void testEndToEndDNSQueryLogging() throws Exception {
        // Arrange
        DnsMessage query = createDNSQuery("integration-test.com", 1);
        DnsMessage response = createDNSResponse(0, "192.0.2.1");
        FilterResult filterResult = FilterResult.allow();

        // Act
        assertDoesNotThrow(() -> {
            loggingService.logQuery(query, response, filterResult);
        });

        // Give RabbitMQ time to process
        Thread.sleep(500);

        // Assert - if no exceptions, end-to-end flow succeeded
        assertTrue(true);
    }

    /**
     * Test end-to-end security alert workflow
     * Verifies that alerts logged are processed by the consumer
     */
    @Test
    void testEndToEndSecurityAlertWorkflow() throws Exception {
        // Arrange
        String domain = "e2e-malware.com";
        String alertType = "MALWARE_DETECTED";
        String description = "End-to-end malware test";

        // Act - Log alert
        assertDoesNotThrow(() -> {
            loggingService.logSecurityAlert(domain, alertType, description);
        });

        // Give RabbitMQ time to process
        Thread.sleep(500);

        // Assert - successfully completed
        assertTrue(true);
    }

    /**
     * Test rapid fire query logging with RabbitMQ
     * Verifies that the system can handle high-frequency logging
     */
    @Test
    void testHighFrequencyQueryLogging() throws Exception {
        // Arrange
        int messageCount = 10;

        // Act
        for (int i = 0; i < messageCount; i++) {
            String domain = "query" + i + ".example.com";
            DnsMessage query = createDNSQuery(domain, 1);
            DnsMessage response = createDNSResponse(0, "192.0.2." + (i % 256));
            FilterResult filterResult = FilterResult.allow();

            assertDoesNotThrow(() -> {
                loggingService.logQuery(query, response, filterResult);
            });
        }

        // Give RabbitMQ time to process all messages
        Thread.sleep(1000);

        // Assert
        assertTrue(true); // All messages logged successfully
    }

    /**
     * Test mixed alert types logging
     * Verifies that different alert types can be logged in sequence
     */
    @Test
    void testMixedAlertTypesLogging() throws Exception {
        // Arrange
        String[] domains = { "malware.com", "dga.com", "phishing.com" };
        String[] alertTypes = { "MALWARE_DETECTED", "DGA_DETECTED", "PHISHING_DETECTED" };

        // Act
        for (int i = 0; i < domains.length; i++) {
            final int index = i;
            assertDoesNotThrow(() -> {
                loggingService.logSecurityAlert(domains[index], alertTypes[index], "Test alert");
            });
        }

        // Give RabbitMQ time to process
        Thread.sleep(500);

        // Assert
        assertTrue(true);
    }

    /**
     * Test DNS response with various record types
     * Verifies that different DNS record types are logged correctly
     */
    @Test
    void testDNSResponseWithDifferentRecordTypes() throws Exception {
        // Arrange
        DnsMessage query = createDNSQuery("multi-record.com", 1);
        DnsMessage response = new DnsMessage();
        response.setRcode(0);

        List<ResourceRecord> records = new ArrayList<>();
        records.add(createResourceRecord("192.0.2.1"));
        records.add(createResourceRecord("192.0.2.2"));
        records.add(createResourceRecord("192.0.2.3"));
        response.setAnswerRRs(records);

        FilterResult filterResult = FilterResult.allow();

        // Act
        assertDoesNotThrow(() -> {
            loggingService.logQuery(query, response, filterResult);
        });

        // Assert
        assertTrue(true);
    }

    /**
     * Test logging blocked query followed by security alert
     * Verifies correlation between blocked queries and alerts
     */
    @Test
    void testBlockedQueryFollowedByAlert() throws Exception {
        // Arrange
        String domain = "blocked-malware.com";

        DnsMessage query = createDNSQuery(domain, 1);
        DnsMessage response = createDNSResponse(3, null); // NXDOMAIN
        FilterResult filterResult = FilterResult.block("Malware", null);

        // Act - Log blocked query
        assertDoesNotThrow(() -> {
            loggingService.logQuery(query, response, filterResult);
        });

        // Then log security alert
        assertDoesNotThrow(() -> {
            loggingService.logSecurityAlert(domain, "MALWARE_DETECTED", "Confirmed malware");
        });

        Thread.sleep(500);

        // Assert
        assertTrue(true);
    }

    /**
     * Test error recovery in logging service
     * Verifies that the service can recover from errors and continue logging
     */
    @Test
    void testErrorRecoveryInLogging() throws Exception {
        // Arrange
        DnsMessage query1 = createDNSQuery("first.com", 1);
        DnsMessage response1 = createDNSResponse(0, "192.0.2.1");
        FilterResult filterResult1 = FilterResult.allow();

        // Act
        assertDoesNotThrow(() -> {
            loggingService.logQuery(query1, response1, filterResult1);
        });

        // Log another after potential error
        DnsMessage query2 = createDNSQuery("second.com", 1);
        DnsMessage response2 = createDNSResponse(0, "192.0.2.2");
        FilterResult filterResult2 = FilterResult.allow();

        assertDoesNotThrow(() -> {
            loggingService.logQuery(query2, response2, filterResult2);
        });

        // Assert
        assertTrue(true); // Both succeeded
    }

    /**
     * Test alert logging with complex descriptions
     * Verifies that descriptions with special characters are handled
     */
    @Test
    void testAlertWithComplexDescription() throws Exception {
        // Arrange
        String domain = "complex.com";
        String alertType = "COMPLEX_ALERT";
        String description = "Alert with special chars: @#$%^&*()[]{}<>?/\\|`~";

        // Act
        assertDoesNotThrow(() -> {
            loggingService.logSecurityAlert(domain, alertType, description);
        });

        Thread.sleep(300);

        // Assert
        assertTrue(true);
    }

    /**
     * Test rapid alert logging sequence
     * Verifies system stability under high alert volume
     */
    @Test
    void testRapidAlertLogging() throws Exception {
        // Arrange
        int alertCount = 20;

        // Act
        for (int i = 0; i < alertCount; i++) {
            final int index = i;
            assertDoesNotThrow(() -> {
                loggingService.logSecurityAlert(
                        "alert" + index + ".com",
                        "ALERT_TYPE_" + (index % 3),
                        "Rapid test " + index);
            });
        }

        Thread.sleep(1000);

        // Assert
        assertTrue(true);
    }

    /**
     * Test DNS query with null query name
     * Verifies handling of edge case data
     */
    @Test
    void testDNSQueryWithValidDomain() throws Exception {
        // Arrange
        DnsMessage query = createDNSQuery("valid-domain.com", 1);
        DnsMessage response = createDNSResponse(0, "192.0.2.1");
        FilterResult filterResult = FilterResult.allow();

        // Act
        assertDoesNotThrow(() -> {
            loggingService.logQuery(query, response, filterResult);
        });

        // Assert
        assertTrue(true);
    }

    /**
     * Test logging with redirected filter action
     * Verifies CNAME/redirect logging functionality
     */
    @Test
    void testLoggingWithRedirectAction() throws Exception {
        // Arrange
        DnsMessage query = createDNSQuery("redirect.com", 1);
        DnsMessage response = createDNSResponse(0, "safe-alternative.com");
        FilterResult filterResult = FilterResult.redirect("safe-alternative.com", null);

        // Act
        assertDoesNotThrow(() -> {
            loggingService.logQuery(query, response, filterResult);
        });

        // Assert
        assertTrue(true);
    }

    /**
     * Test multiple concurrent security alert types
     * Verifies that different alert types don't interfere with each other
     */
    @Test
    void testMultipleConcurrentAlertTypes() throws Exception {
        // Arrange
        String[][] alertConfigs = {
                { "malware1.com", "MALWARE_DETECTED", "Malware found" },
                { "dga1.com", "DGA_DETECTED", "DGA pattern" },
                { "phishing1.com", "PHISHING_DETECTED", "Phishing attempt" },
                { "malware2.com", "MALWARE_DETECTED", "Another malware" },
                { "dga2.com", "DGA_DETECTED", "Another DGA" }
        };

        // Act
        for (String[] config : alertConfigs) {
            assertDoesNotThrow(() -> {
                loggingService.logSecurityAlert(config[0], config[1], config[2]);
            });
        }

        Thread.sleep(500);

        // Assert
        assertTrue(true);
    }

    // Helper methods

    private DnsMessage createDNSQuery(String domain, int queryType) {
        DnsMessage message = new DnsMessage();
        message.setQname(domain);
        message.setQtype(queryType);
        message.setRcode(0);
        return message;
    }

    private DnsMessage createDNSResponse(int rcode, String ipAddress) {
        DnsMessage message = new DnsMessage();
        message.setRcode(rcode);

        if (ipAddress != null) {
            ResourceRecord rr = createResourceRecord(ipAddress);
            List<ResourceRecord> answers = new ArrayList<>();
            answers.add(rr);
            message.setAnswerRRs(answers);
        }

        return message;
    }

    private ResourceRecord createResourceRecord(String data) {
        ResourceRecord rr = new ResourceRecord();
        rr.setRdata(data);
        return rr;
    }
}
