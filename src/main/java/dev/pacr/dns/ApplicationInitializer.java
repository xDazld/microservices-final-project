package dev.pacr.dns;

import dev.pacr.dns.service.DNSFilterService;
import dev.pacr.dns.service.SecurityService;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

/**
 * Application initialization service Runs on startup to initialize default data and configurations
 */
@ApplicationScoped
public class ApplicationInitializer {
	
	private static final Logger LOG = Logger.getLogger(ApplicationInitializer.class);
	
	@Inject
	DNSFilterService filterService;
	
	@Inject
	SecurityService securityService;
	
	/**
	 * Initialize the application on startup
	 */
	void onStart(@Observes StartupEvent event) {
		LOG.info("=================================================");
		LOG.info("DNS Filtering and Security Service Starting...");
		LOG.info("=================================================");
		
		// Initialize default filtering rules
		LOG.info("Initializing default filtering rules...");
		filterService.initializeDefaultRules();
		
		// Initialize threat database
		LOG.info("Initializing security threat database...");
		securityService.initializeThreatDatabase();
		
		LOG.info("=================================================");
		LOG.info("DNS Filtering Service initialized successfully!");
		LOG.info("API available at: http://localhost:8080/api/v1");
		LOG.info("Metrics available at: http://localhost:8080/metrics");
		LOG.info("Health check at: http://localhost:8080/api/v1/admin/health");
		LOG.info("=================================================");
	}
}
