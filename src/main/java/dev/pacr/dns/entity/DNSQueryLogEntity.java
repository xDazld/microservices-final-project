package dev.pacr.dns.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * JPA Entity for persisting DNS query logs
 */
@Entity
@Table(name = "dns_query_logs", indexes = {@Index(name = "idx_domain", columnList = "domain"),
		@Index(name = "idx_timestamp", columnList = "timestamp"),
		@Index(name = "idx_client_ip", columnList = "clientIp"),
		@Index(name = "idx_status", columnList = "status")})
public class DNSQueryLogEntity {
	
	@Id
	private String id;
	
	@Column(nullable = false, length = 255)
	private String domain;
	
	@Column(length = 10)
	private String queryType;
	
	@Column(length = 45)
	private String clientIp;
	
	@Column(length = 20)
	private String status;
	
	@Column(length = 500)
	private String blockReason;
	
	@Column(length = 1000)
	private String resolvedAddresses;
	
	private Long responseTimeMs;
	
	private Boolean cached;
	
	@Column(length = 10)
	private String protocol;
	
	@Column(nullable = false)
	private Instant timestamp;
	
	@Column(length = 50)
	private String filterCategory;
	
	// Constructors
	
	public DNSQueryLogEntity() {
	}
	
	// Getters and Setters
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getDomain() {
		return domain;
	}
	
	public void setDomain(String domain) {
		this.domain = domain;
	}
	
	public String getQueryType() {
		return queryType;
	}
	
	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}
	
	public String getClientIp() {
		return clientIp;
	}
	
	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getBlockReason() {
		return blockReason;
	}
	
	public void setBlockReason(String blockReason) {
		this.blockReason = blockReason;
	}
	
	public String getResolvedAddresses() {
		return resolvedAddresses;
	}
	
	public void setResolvedAddresses(String resolvedAddresses) {
		this.resolvedAddresses = resolvedAddresses;
	}
	
	public Long getResponseTimeMs() {
		return responseTimeMs;
	}
	
	public void setResponseTimeMs(Long responseTimeMs) {
		this.responseTimeMs = responseTimeMs;
	}
	
	public Boolean getCached() {
		return cached;
	}
	
	public void setCached(Boolean cached) {
		this.cached = cached;
	}
	
	public String getProtocol() {
		return protocol;
	}
	
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	
	public Instant getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(Instant timestamp) {
		this.timestamp = timestamp;
	}
	
	public String getFilterCategory() {
		return filterCategory;
	}
	
	public void setFilterCategory(String filterCategory) {
		this.filterCategory = filterCategory;
	}
}
