package dev.pacr.dns.storage;

import dev.pacr.dns.storage.model.DNSLogEntry;
import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

/**
	 * Repository for persisting DNS logs to MongoDB
 */
@ApplicationScoped
public class DNSLogRepository implements PanacheMongoRepositoryBase<DNSLogEntry, String> {
	
	/**
	 * Persist a DNS log entry
	 */
	public void saveLog(DNSLogEntry entry) {
		persist(entry);
	}
}
