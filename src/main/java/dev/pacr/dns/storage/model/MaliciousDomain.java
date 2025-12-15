package dev.pacr.dns.storage.model;

import io.quarkus.mongodb.panache.common.MongoEntity;

import java.time.Instant;

@MongoEntity(collection = "malicious_domains", database = "dns_shield")
public class MaliciousDomain {
	public String id;
	public String domain;
	public String source;
	public String description;
	public Instant addedAt;
	public Instant updatedAt;
	
	public MaliciousDomain() {
	}
	
	public MaliciousDomain(String domain) {
		this.domain = domain;
		this.addedAt = Instant.now();
		this.updatedAt = Instant.now();
	}
	
	public MaliciousDomain(String domain, String source, String description) {
		this.domain = domain;
		this.source = source;
		this.description = description;
		this.addedAt = Instant.now();
		this.updatedAt = Instant.now();
	}
}
