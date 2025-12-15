package dev.pacr.dns.storage;

import dev.pacr.dns.storage.model.MaliciousIP;
import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Repository for persisting malicious IPs to MongoDB
 */
@ApplicationScoped
public class MaliciousIPRepository implements PanacheMongoRepositoryBase<MaliciousIP, String> {
	
	/**
	 * Find a malicious IP by its address
	 */
	public MaliciousIP findByIPAddress(String ipAddress) {
		return find("ipAddress", ipAddress).firstResult();
	}
	
	/**
	 * Check if an IP exists in the database
	 */
	public boolean existsByIPAddress(String ipAddress) {
		return find("ipAddress", ipAddress).count() > 0;
	}
	
	/**
	 * Delete an IP by its address
	 */
	public long deleteByIPAddress(String ipAddress) {
		return delete("ipAddress", ipAddress);
	}
	
	/**
	 * Get the count of malicious IPs
	 */
	public long getTotalCount() {
		return count();
	}
}
