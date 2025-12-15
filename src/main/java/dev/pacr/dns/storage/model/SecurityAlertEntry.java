package dev.pacr.dns.storage.model;

import io.quarkus.mongodb.panache.common.MongoEntity;

import java.time.Instant;

@MongoEntity(collection = "security_alerts", database = "dns_service")
/**
	 * SecurityAlertEntry class.
 */
public class SecurityAlertEntry {
	/**
	 * The id.
	 */
	public String id;
	/**
	 * The domain.
	 */
	public String domain;
	/**
	 * The alertType.
	 */
	public String alertType;
	/**
	 * The description.
	 */
	public String description;
	/**
	 * The timestamp.
	 */
	public Instant timestamp;
}
