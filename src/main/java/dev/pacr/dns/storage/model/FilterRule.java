package dev.pacr.dns.storage.model;

import io.quarkus.mongodb.panache.common.MongoEntity;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import java.time.Instant;

/**
 * Represents a DNS filtering rule stored in MongoDB
 *
 * @author Patrick Rafferty
 */
@MongoEntity(collection = "filter_rules", database = "dns_service")
public class FilterRule {
	
	/**
	 * MongoDB document ID
	 */
	@BsonId
	public ObjectId id;
	
	/**
	 * Human-readable unique identifier
	 */
	public String ruleId;
	
	/**
	 * The name.
	 */
	public String name;
	
	/**
	 * The pattern.
	 */
	public String pattern; // Domain pattern (e.g., "*.ads.com", "tracker.example.com")
	
	/**
	 * The type.
	 */
	public RuleType type; // BLOCK, ALLOW, REDIRECT
	
	/**
	 * The category.
	 */
	public String category; // ads, tracking, malware, custom, etc.
	
	/**
	 * The redirect to.
	 */
	public String redirectTo; // For REDIRECT type
	
	/**
	 * The enabled.
	 */
	public boolean enabled;
	
	/**
	 * The priority.
	 */
	public int priority; // Higher priority rules evaluated first
	
	/**
	 * The created at.
	 */
	public Instant createdAt;
	
	/**
	 * The updated at.
	 */
	public Instant updatedAt;
	
	/**
	 * Default constructor.
	 */
	public FilterRule() {
		this.ruleId = java.util.UUID.randomUUID().toString();
		this.createdAt = Instant.now();
		this.updatedAt = Instant.now();
		this.enabled = true;
		this.priority = 0;
	}
	
	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		return "FilterRule{" + "id='" + id + '\'' + ", ruleId='" + ruleId + '\'' + ", name='" +
				name + '\'' + ", pattern='" + pattern + '\'' + ", type=" + type + ", category='" +
				category + '\'' + ", enabled=" + enabled + ", priority=" + priority + '}';
	}
	
	/**
	 * The enum Rule type.
	 */
	public enum RuleType {
		/**
		 * Block
		 */
		BLOCK,
		/**
		 * Allow
		 */
		ALLOW,
		/**
		 * Redirect
		 */
		REDIRECT
	}
}

