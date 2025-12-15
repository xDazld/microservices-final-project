package dev.pacr.dns;

import dev.pacr.dns.service.DNSFilterService;
import dev.pacr.dns.service.RFC5358AccessControlService;
import dev.pacr.dns.service.SecurityService;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

/**
 * Application initialization service that runs on startup to initialize default data and
 * configurations.
 * <p>
 * This service is responsible for setting up the DNS Shield application by:
 * - Initializing RFC 5358 access control to prevent amplification attacks
 * - Loading default filtering rules for DNS queries
 * - Setting up threat database for security analysis
 * <p>
 * The initialization is performed during application startup using Quarkus's {@link StartupEvent}.
 *
 * @author Patrick Rafferty
 */
@ApplicationScoped
public class ApplicationInitializer {
	
	/**
	 * Logger for this class
	 */
	private static final Logger LOG = Logger.getLogger(ApplicationInitializer.class);
	
	/** DNS filter service for managing filtering rules */
	@Inject
	DNSFilterService filterService;
	
	/** Security service for threat detection and analysis */
	@Inject
	SecurityService securityService;
	
	/** RFC 5358 access control service for preventing DDoS amplification attacks */
	@Inject
	RFC5358AccessControlService rfc5358AccessControl;
	
	/**
	 * Initialize the application on startup.
	 * <p>
	 * This method is called automatically by Quarkus when the application starts up.
	 * It performs all necessary initialization tasks in the correct order.
	 *
	 * @param event The startup event (provided by Quarkus CDI)
	 */
	void onStart(@Observes StartupEvent event) {
		LOG.info("=================================================");
		LOG.info("DNS Filtering and Security Service Starting...");
		LOG.info("=================================================");
		
		// Initialize RFC 5358 access control
		LOG.info("Initializing RFC 5358 access control (preventing amplification attacks)...");
		rfc5358AccessControl.initialize();
		RFC5358AccessControlService.RFC5358Status status =
				rfc5358AccessControl.getComplianceStatus();
		LOG.infof("RFC 5358 Status: %s", status);
		if (!status.isCompliant()) {
			LOG.warn(
					"WARNING: RFC 5358 compliance may not be fully enforced. Consider enabling " +
							"default-deny policy.");
		}
		
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
