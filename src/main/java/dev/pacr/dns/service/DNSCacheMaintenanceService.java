package dev.pacr.dns.service;

import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Scheduled maintenance tasks for DNS caching per RFC 9520.
 * <p>
 * This service performs periodic cleanup of expired cache entries for both positive and negative
 * caches as recommended by RFC 9520.
 *
 * @author DNS Shield Team
 */
@Startup
@ApplicationScoped
public class DNSCacheMaintenanceService {
	
	private static final Logger LOG = Logger.getLogger(DNSCacheMaintenanceService.class);
	/**
	 * Scheduled executor for maintenance tasks
	 */
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	/**
	 * DNS resolver service
	 */
	@Inject
	DNSResolver dnsResolver;
	
	/**
	 * Initialize scheduled tasks on startup.
	 */
	@PostConstruct
	public void init() {
		// Schedule cache cleanup every 60 seconds
		scheduler.scheduleAtFixedRate(this::cleanupExpiredEntries, 60, 60, TimeUnit.SECONDS);
		
		// Schedule statistics logging every 5 minutes
		scheduler.scheduleAtFixedRate(this::logCacheStatistics, 300, 300, TimeUnit.SECONDS);
		
		LOG.info("DNS cache maintenance service initialized");
	}
	
	/**
	 * Periodically clear expired cache entries.
	 * <p>
	 * RFC 9520 Section 3.2 recommends cleaning up expired entries to maintain cache efficiency and
	 * prevent memory bloat.
	 */
	void cleanupExpiredEntries() {
		LOG.debug("Running scheduled cache cleanup");
		
		try {
			dnsResolver.clearExpiredCache();
		} catch (RuntimeException e) {
			LOG.errorf(e, "Error during cache cleanup");
		}
	}
	
	/**
	 * Periodically log cache statistics for monitoring.
	 * <p>
	 * Helps operators monitor cache performance and detect potential issues per RFC 9520
	 * operational guidance.
	 */
	void logCacheStatistics() {
		try {
			var stats = dnsResolver.getCacheStats();
			LOG.infof("DNS Cache Statistics: %s", stats);
		} catch (RuntimeException e) {
			LOG.errorf(e, "Error logging cache statistics");
		}
	}
}

