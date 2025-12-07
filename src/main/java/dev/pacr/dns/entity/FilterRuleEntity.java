package dev.pacr.dns.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * JPA Entity for persisting filter rules
 */
@Entity
@Table(name = "filter_rules", indexes = {@Index(name = "idx_category", columnList = "category"),
		@Index(name = "idx_enabled", columnList = "enabled"),
		@Index(name = "idx_priority", columnList = "priority")})
public class FilterRuleEntity {
	
	@Id
	private String id;
	
	@Column(nullable = false, length = 255)
	private String name;
	
	@Column(nullable = false, length = 500)
	private String pattern;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private RuleType type;
	
	@Column(length = 50)
	private String category;
	
	@Column(length = 255)
	private String redirectTo;
	
	@Column(nullable = false)
	private Boolean enabled;
	
	@Column(nullable = false)
	private Integer priority;
	
	@Column(nullable = false)
	private Instant createdAt;
	
	@Column(nullable = false)
	private Instant updatedAt;
	
	public FilterRuleEntity() {
	}
	
	// Constructors
	
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
	
	public Boolean getEnabled() {
		return enabled;
	}
	
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}
	
	public Integer getPriority() {
		return priority;
	}
	
	public void setPriority(Integer priority) {
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
	
	public enum RuleType {
		BLOCK, ALLOW, REDIRECT
	}
}
