package dev.pacr.dns.storage;

import dev.pacr.dns.storage.model.SecurityAlertEntry;
import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

/**
	 * Repository for persisting security alerts to MongoDB
 */
@ApplicationScoped
public class SecurityAlertRepository
		implements PanacheMongoRepositoryBase<SecurityAlertEntry, String> {
	
	/**
	 * save method.
	 */
	public void save(SecurityAlertEntry alert) {
		persist(alert);
	}
}

