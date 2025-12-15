package dev.pacr.dns.storage.model;

import io.quarkus.mongodb.panache.common.MongoEntity;

import java.time.Instant;

/**
 * Represents a security alert event stored in MongoDB.
 * <p>
 * Contains information about security threats detected during DNS query processing,
 * including the domain name, alert type, and timestamp.
 */
@MongoEntity(collection = "security_alerts", database = "dns_service")
public class SecurityAlertEntry {
	/**
	 * Unique identifier for the security alert.
	 */
	public String id;
	/**
	 * Domain name associated with the security alert.
	 */
	public String domain;
	/**
	 * Type of security alert (e.g., "MALWARE", "PHISHING", "SUSPICIOUS").
	 */
	public String alertType;
	/**
	 * Detailed description of the security alert.
	 */
	public String description;
	/**
	 * Timestamp when the security alert was created.
	 */
	public Instant timestamp;
}
