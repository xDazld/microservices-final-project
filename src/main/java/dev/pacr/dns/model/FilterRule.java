package dev.pacr.dns.model;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents a DNS filtering rule
 *
 * @author Patrick Rafferty
 */
public class FilterRule {
	
	/**
	 * The id.
	 */
	private String id;
	
	/**
	 * The name.
	 */
	private String name;
	
	/**
	 * The pattern.
	 */
	private String pattern; // Domain pattern (e.g., "*.ads.com", "tracker.example.com")
	
	/**
	 * The type.
	 */
	private RuleType type; // BLOCK, ALLOW, REDIRECT
	
	/**
	 * The category.
	 */
	private String category; // ads, tracking, malware, custom, etc.
	
	/**
	 * The redirect to.
	 */
	private String redirectTo; // For REDIRECT type
	
	/**
	 * The enabled.
	 */
	private boolean enabled;
	
	/**
	 * The priority.
	 */
	private int priority; // Higher priority rules evaluated first
	
	/**
	 * The created at.
	 */
	private Instant createdAt;
	
	/**
	 * The updated at.
	 */
	private Instant updatedAt;
	
	/**
	 * Default constructor.
	 */
	public FilterRule() {
		this.id = UUID.randomUUID().toString();
		this.createdAt = Instant.now();
		this.updatedAt = Instant.now();
		this.enabled = true;
		this.priority = 0;
	}
	
	/**
	 * Gets id.
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	
	// Getters and Setters
	
	/**
	 * Sets id.
	 *
	 * @param id the id
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * Gets name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets name.
	 *
	 * @param name the name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Gets pattern.
	 *
	 * @return the pattern
	 */
	public String getPattern() {
		return pattern;
	}
	
	/**
	 * Sets pattern.
	 *
	 * @param pattern the pattern
	 */
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	
	/**
	 * Gets type.
	 *
	 * @return the type
	 */
	public RuleType getType() {
		return type;
	}
	
	/**
	 * Sets type.
	 *
	 * @param type the type
	 */
	public void setType(RuleType type) {
		this.type = type;
	}
	
	/**
	 * Gets category.
	 *
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}
	
	/**
	 * Sets category.
	 *
	 * @param category the category
	 */
	public void setCategory(String category) {
		this.category = category;
	}
	
	/**
	 * Gets redirect to.
	 *
	 * @return the redirect to
	 */
	public String getRedirectTo() {
		return redirectTo;
	}
	
	/**
	 * Sets redirect to.
	 *
	 * @param redirectTo the redirect to
	 */
	public void setRedirectTo(String redirectTo) {
		this.redirectTo = redirectTo;
	}
	
	/**
	 * Is enabled.
	 *
	 * @return the boolean
	 */
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Sets enabled.
	 *
	 * @param enabled the enabled
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	/**
	 * Gets priority.
	 *
	 * @return the priority
	 */
	public int getPriority() {
		return priority;
	}
	
	/**
	 * Sets priority.
	 *
	 * @param priority the priority
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	/**
	 * Gets created at.
	 *
	 * @return the created at
	 */
	public Instant getCreatedAt() {
		return createdAt;
	}
	
	/**
	 * Sets created at.
	 *
	 * @param createdAt the created at
	 */
	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}
	
	/**
	 * Gets updated at.
	 *
	 * @return the updated at
	 */
	public Instant getUpdatedAt() {
		return updatedAt;
	}
	
	/**
	 * Sets updated at.
	 *
	 * @param updatedAt the updated at
	 */
	public void setUpdatedAt(Instant updatedAt) {
		this.updatedAt = updatedAt;
	}
	
	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		return "FilterRule{" + "id='" + id + '\'' + ", name='" + name + '\'' + ", pattern='" +
				pattern + '\'' + ", type=" + type + ", category='" + category + '\'' +
				", enabled=" + enabled + ", priority=" + priority + '}';
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
