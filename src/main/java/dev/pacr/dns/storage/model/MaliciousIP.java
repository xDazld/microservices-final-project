package dev.pacr.dns.storage.model;

import io.quarkus.mongodb.panache.common.MongoEntity;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import java.time.Instant;

@MongoEntity(collection = "malicious_ips", database = "dns_shield")
public class MaliciousIP {
	@BsonId
	public ObjectId id;
	public String ipAddress;
	public String source;
	public String description;
	public Instant addedAt;
	public Instant updatedAt;
	
	public MaliciousIP() {
	}
	
	public MaliciousIP(String ipAddress) {
		this.ipAddress = ipAddress;
		this.addedAt = Instant.now();
		this.updatedAt = Instant.now();
	}
	
	public MaliciousIP(String ipAddress, String source, String description) {
		this.ipAddress = ipAddress;
		this.source = source;
		this.description = description;
		this.addedAt = Instant.now();
		this.updatedAt = Instant.now();
	}
}
