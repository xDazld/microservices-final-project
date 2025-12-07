package dev.pacr.dns.model;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents a DNS filtering rule
 */
public class FilterRule {
	
	private String id;
	private String name;
	private String pattern; // Domain pattern (e.g., "*.ads.com", "tracker.example.com")
	private RuleType type; // BLOCK, ALLOW, REDIRECT
	private String category; // ads, tracking, malware, custom, etc.
	private String redirectTo; // For REDIRECT type
	private boolean enabled;
	private int priority; // Higher priority rules evaluated first
	private Instant createdAt;
	private Instant updatedAt;
	
	public FilterRule() {
		this.id = UUID.randomUUID().toString();
		this.createdAt = Instant.now();
		this.updatedAt = Instant.now();
		this.enabled = true;
		this.priority = 0;
	}
	
	public String getId() {
		return id;
	}
	
	// Getters and Setters
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getPattern() {
		return pattern;
	}
	
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	
	public RuleType getType() {
		return type;
	}
	
	public void setType(RuleType type) {
		this.type = type;
	}
	
	public String getCategory() {
		return category;
	}
	
	public void setCategory(String category) {
		this.category = category;
	}
	
	public String getRedirectTo() {
		return redirectTo;
	}
	
	public void setRedirectTo(String redirectTo) {
		this.redirectTo = redirectTo;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public int getPriority() {
		return priority;
	}
	
	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	public Instant getCreatedAt() {
		return createdAt;
	}
	
	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}
	
	public Instant getUpdatedAt() {
		return updatedAt;
	}
	
	public void setUpdatedAt(Instant updatedAt) {
		this.updatedAt = updatedAt;
	}
	
	@Override
	public String toString() {
		return "FilterRule{" + "id='" + id + '\'' + ", name='" + name + '\'' + ", pattern='" +
				pattern + '\'' + ", type=" + type + ", category='" + category + '\'' +
				", enabled=" + enabled + ", priority=" + priority + '}';
	}
	
	public enum RuleType {
		BLOCK, ALLOW, REDIRECT
	}
}
