package dev.pacr.dns.storage.model;

import io.quarkus.mongodb.panache.common.MongoEntity;

import java.time.Instant;

@MongoEntity(collection = "security_alerts", database = "dns_service")
public class SecurityAlertEntry {
	public String id;
	public String domain;
	public String alertType;
	public String description;
	public Instant timestamp;
}
