package dev.pacr.dns.service;

import io.quarkus.runtime.Startup;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

/**
 * Scheduled maintenance tasks for DNS caching per RFC 9520.
 * <p>
 * This service performs periodic cleanup of expired cache entries for both positive and negative
 * caches as recommended by RFC 9520.
 *
 * @author Patrick Rafferty
 */
@Startup
@ApplicationScoped
public class DNSCacheMaintenanceService {
	
	/**
	 * Logger instance for this service
	 */
	private static final Logger LOG = Logger.getLogger(DNSCacheMaintenanceService.class);
	/**
	 * DNS resolver service
	 */
	@Inject
	DNSResolver dnsResolver;
	
	/**
	 * Periodically clear expired cache entries every 60 seconds.
	 * <p>
	 * RFC 9520 Section 3.2 recommends cleaning up expired entries to maintain cache efficiency and
	 * prevent memory bloat.
	 */
	@Scheduled(every = "60s")
	void cleanupExpiredEntries() {
		LOG.debug("Running scheduled cache cleanup");
		
		try {
			dnsResolver.clearExpiredCache();
		} catch (RuntimeException e) {
			LOG.errorf(e, "Error during cache cleanup");
		}
	}
	
	/**
	 * Periodically log cache statistics for monitoring every 5 minutes.
	 * <p>
	 * Helps operators monitor cache performance and detect potential issues per RFC 9520
	 * operational guidance.
	 */
	@Scheduled(every = "5m")
	void logCacheStatistics() {
		try {
			var stats = dnsResolver.getCacheStats();
			LOG.infof("DNS Cache Statistics: %s", stats);
		} catch (RuntimeException e) {
			LOG.errorf(e, "Error logging cache statistics");
		}
	}
}

