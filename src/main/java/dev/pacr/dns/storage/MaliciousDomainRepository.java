package dev.pacr.dns.storage;

import dev.pacr.dns.storage.model.MaliciousDomain;
import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Repository for persisting malicious domains to MongoDB
 */
@ApplicationScoped
public class MaliciousDomainRepository
		implements PanacheMongoRepositoryBase<MaliciousDomain, String> {
	
	/**
	 * Find a malicious domain by its domain name
	 */
	public MaliciousDomain findByDomain(String domain) {
		return find("domain", domain).firstResult();
	}
	
	/**
	 * Check if a domain exists in the database
	 */
	public boolean existsByDomain(String domain) {
		return find("domain", domain).count() > 0;
	}
	
	/**
	 * Delete a domain by its domain name
	 */
	public long deleteByDomain(String domain) {
		return delete("domain", domain);
	}
	
	/**
	 * Get the count of malicious domains
	 */
	public long getTotalCount() {
		return count();
	}
}
