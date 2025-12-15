package dev.pacr.dns.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;

import java.util.Map;

/**
	 * Publisher for real-time events streamed to dashboard
 * <p>
 * Publishes metrics, logs, and alerts to RabbitMQ for WebSocket delivery to connected dashboard
 * clients
 */
@ApplicationScoped
public class EventPublisher {
	
	/**
	 * The LOG.
	 */
	private static final Logger LOG = Logger.getLogger(EventPublisher.class);
	/**
	 * The objectMapper.
	 */
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	@Inject
	@Channel("metrics-events-out")
	Emitter<String> metricsEmitter;
	
	@Inject
	@Channel("logs-events-out")
	Emitter<String> logsEmitter;
	
	@Inject
	@Channel("stats-updates-out")
	Emitter<String> statsEmitter;
	
	/**
	 * Publish a metrics update event
	 */
	public void publishMetricsUpdate(String metricName, double value) {
		try {
			Map<String, Object> event =
					Map.of("type", "METRICS_UPDATE", "metric", metricName, "value", value,
							"timestamp", System.currentTimeMillis());
			
			String json = objectMapper.writeValueAsString(event);
			metricsEmitter.send(json);
			LOG.debugf("Published metrics update: %s = %f", metricName, value);
		} catch (Exception e) {
			LOG.warnf(e, "Failed to publish metrics update: %s", metricName);
		}
	}
	
	/**
	 * Publish a query log event
	 */
	public void publishQueryLog(Map<String, Object> logEntry) {
		try {
			Map<String, Object> event = Map.of("type", "QUERY_LOG", "log", logEntry, "timestamp",
					System.currentTimeMillis());
			
			String json = objectMapper.writeValueAsString(event);
			logsEmitter.send(json);
			LOG.debugf("Published query log event for domain: %s", logEntry.get("domain"));
		} catch (Exception e) {
			LOG.warnf(e, "Failed to publish query log event");
		}
	}
	
	/**
	 * Publish a statistics update event
	 */
	public void publishStatsUpdate(String statsType, Map<String, Object> stats) {
		try {
			Map<String, Object> event =
					Map.of("type", "STATS_UPDATE", "statsType", statsType, "data", stats,
							"timestamp", System.currentTimeMillis());
			
			String json = objectMapper.writeValueAsString(event);
			statsEmitter.send(json);
			LOG.debugf("Published stats update: %s", statsType);
		} catch (Exception e) {
			LOG.warnf(e, "Failed to publish stats update: %s", statsType);
		}
	}
	
	/**
	 * Publish a security alert event
	 */
	public void publishSecurityAlert(String alertType, String domain,
									 Map<String, Object> details) {
		try {
			Map<String, Object> event =
					Map.of("type", "SECURITY_ALERT", "alertType", alertType, "domain", domain,
							"details", details, "timestamp", System.currentTimeMillis());
			
			String json = objectMapper.writeValueAsString(event);
			statsEmitter.send(json);
			LOG.warnf("Published security alert: %s for %s", alertType, domain);
		} catch (Exception e) {
			LOG.warnf(e, "Failed to publish security alert");
		}
	}
	
	/**
	 * Publish a cache statistics update
	 */
	public void publishCacheStatsUpdate(Map<String, Object> cacheStats) {
		publishStatsUpdate("cache", cacheStats);
	}
	
	/**
	 * Publish a security statistics update
	 */
	public void publishSecurityStatsUpdate(Map<String, Object> securityStats) {
		publishStatsUpdate("security", securityStats);
	}
	
	/**
	 * Publish a query count metric
	 */
	public void publishQueryCountMetric(double count) {
		publishMetricsUpdate("dns.query.count", count);
	}
	
	/**
	 * Publish a filter check metric
	 */
	public void publishFilterCheckMetric(double count) {
		publishMetricsUpdate("dns.filter.checks", count);
	}
	
	/**
	 * Publish a cache hit rate metric
	 */
	public void publishCacheHitRateMetric(double rate) {
		publishMetricsUpdate("dns.cache.hit.rate", rate);
	}
}
