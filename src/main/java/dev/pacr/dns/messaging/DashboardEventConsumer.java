package dev.pacr.dns.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.pacr.dns.api.DashboardWebSocket;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import java.util.concurrent.CompletionStage;

/**
	 * Consumer for dashboard event streams
 * <p>
 * Receives all event types from the unified dashboard events topic and broadcasts them to all
 * connected WebSocket dashboard clients for real-time updates.
 */
@ApplicationScoped
/**
	 * DashboardEventConsumer class.
 */
public class DashboardEventConsumer {
	
	/**
	 * The LOG.
	 */
	private static final Logger LOG = Logger.getLogger(DashboardEventConsumer.class);
	/**
	 * The objectMapper.
	 */
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	/**
	 * Consume and broadcast all dashboard events
	 * <p>
	 * Unified consumer handles all event types: - METRICS_UPDATE: Query count, filter checks,
     * cache
	 * hit rate - QUERY_LOG: Individual DNS query log entries - STATS_UPDATE: Cache and security
	 * statistics - SECURITY_ALERT: Security threat notifications
	 */
	@Incoming("metrics-events-in")
	/**
	 * consumeEvent method.
	 */
	public CompletionStage<Void> consumeEvent(Message<String> message) {
		String payload = message.getPayload();
		
		try {
			JsonNode event = objectMapper.readTree(payload);
			String eventType = event.get("type").asText();
			
			// Log based on event type for debugging
			switch (eventType) {
				case "METRICS_UPDATE":
					JsonNode metric = event.get("metric");
					if (metric != null) {
						LOG.debugf("Received metrics event: %s = %s", metric.asText(),
								event.get("value").asText());
					}
					break;
				case "QUERY_LOG":
					JsonNode log = event.get("log");
					if (log != null) {
						JsonNode domain = log.get("domain");
						if (domain != null) {
							LOG.debugf("Received log event for domain: %s", domain.asText());
						}
					}
					break;
				case "STATS_UPDATE":
					JsonNode statsType = event.get("statsType");
					if (statsType != null) {
						LOG.debugf("Received stats update: %s", statsType.asText());
					}
					break;
				case "SECURITY_ALERT":
					JsonNode alertType = event.get("alertType");
					JsonNode alertDomain = event.get("domain");
					if (alertType != null && alertDomain != null) {
						LOG.warnf("Received security alert: %s for %s", alertType.asText(),
								alertDomain.asText());
					}
					break;
				default:
					LOG.debugf("Received event type: %s", eventType);
			}
			
			// Broadcast all events to connected WebSocket clients
			DashboardWebSocket.broadcast(event);
			
		} catch (Exception e) {
			LOG.warnf(e, "Error consuming dashboard event: %s", payload);
		}
		
		return message.ack();
	}
}
