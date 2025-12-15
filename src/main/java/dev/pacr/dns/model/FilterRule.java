package dev.pacr.dns.model;

import java.time.Instant;
import java.util.UUID;

/**
	 * Represents a DNS filtering rule
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
	 * The redirectTo.
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
	 * The createdAt.
	 */
	private Instant createdAt;
	/**
	 * The updatedAt.
	 */
	private Instant updatedAt;
	
	/**
	 * Constructs a new FilterRule.
	 */
	public FilterRule() {
		this.id = UUID.randomUUID().toString();
		this.createdAt = Instant.now();
		this.updatedAt = Instant.now();
		this.enabled = true;
		this.priority = 0;
	}
	
	/**
	 * Gets the Id.
	 * @return the Id
	 */
	public String getId() {
		return id;
	}
	
	// Getters and Setters
	
	/**
	 * Sets the Id.
	 * @param id the Id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * Gets the Name.
	 * @return the Name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the Name.
	 * @param name the Name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Gets the Pattern.
	 * @return the Pattern
	 */
	public String getPattern() {
		return pattern;
	}
	
	/**
	 * Sets the Pattern.
	 * @param pattern the Pattern to set
	 */
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	
	/**
	 * Gets the Type.
	 * @return the Type
	 */
	public RuleType getType() {
		return type;
	}
	
	/**
	 * Sets the Type.
	 * @param type the Type to set
	 */
	public void setType(RuleType type) {
		this.type = type;
	}
	
	/**
	 * Gets the Category.
	 * @return the Category
	 */
	public String getCategory() {
		return category;
	}
	
	/**
	 * Sets the Category.
	 * @param category the Category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}
	
	/**
	 * Gets the RedirectTo.
	 * @return the RedirectTo
	 */
	public String getRedirectTo() {
		return redirectTo;
	}
	
	/**
	 * Sets the RedirectTo.
	 * @param redirectTo the RedirectTo to set
	 */
	public void setRedirectTo(String redirectTo) {
		this.redirectTo = redirectTo;
	}
	
	/**
	 * Checks if Enabled.
	 * @return true if Enabled, false otherwise
	 */
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Sets the Enabled.
	 * @param enabled the Enabled to set
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	/**
	 * Gets the Priority.
	 * @return the Priority
	 */
	public int getPriority() {
		return priority;
	}
	
	/**
	 * Sets the Priority.
	 * @param priority the Priority to set
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	/**
	 * Gets the CreatedAt.
	 * @return the CreatedAt
	 */
	public Instant getCreatedAt() {
		return createdAt;
	}
	
	/**
	 * Sets the CreatedAt.
	 * @param createdAt the CreatedAt to set
	 */
	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}
	
	/**
	 * Gets the UpdatedAt.
	 * @return the UpdatedAt
	 */
	public Instant getUpdatedAt() {
		return updatedAt;
	}
	
	/**
	 * Sets the UpdatedAt.
	 * @param updatedAt the UpdatedAt to set
	 */
	public void setUpdatedAt(Instant updatedAt) {
		this.updatedAt = updatedAt;
	}
	
	/**
	 * toString method.
	 */
	public String toString() {
		return "FilterRule{" + "id='" + id + '\'' + ", name='" + name + '\'' + ", pattern='" +
				pattern + '\'' + ", type=" + type + ", category='" + category + '\'' +
				", enabled=" + enabled + ", priority=" + priority + '}';
	}
	
	/**
	 * RuleType enum.
	 */
	public enum RuleType {
		/**
	 * BLOCK constant.
		 */

		BLOCK, ALLOW, REDIRECT
	}
}
