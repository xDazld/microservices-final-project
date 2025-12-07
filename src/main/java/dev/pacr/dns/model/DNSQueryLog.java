package dev.pacr.dns.model;

import java.time.Instant;

/**
 * DNS query log entry for persistence and analytics
 */
public class DNSQueryLog {
	
	private String id;
	private String domain;
	private String queryType;
	private String clientIp;
	private String status; // ALLOWED, BLOCKED, ERROR
	private String blockReason;
	private String[] resolvedAddresses;
	private long responseTimeMs;
	private boolean cached;
	private String protocol;
	private Instant timestamp;
	private String filterCategory;
	
	public DNSQueryLog() {
	}
	
	public DNSQueryLog(String id, String domain, String queryType, String clientIp, String status,
					   String blockReason, String[] resolvedAddresses, long responseTimeMs,
					   boolean cached, String protocol, Instant timestamp, String filterCategory) {
		this.id = id;
		this.domain = domain;
		this.queryType = queryType;
		this.clientIp = clientIp;
		this.status = status;
		this.blockReason = blockReason;
		this.resolvedAddresses = resolvedAddresses;
		this.responseTimeMs = responseTimeMs;
		this.cached = cached;
		this.protocol = protocol;
		this.timestamp = timestamp;
		this.filterCategory = filterCategory;
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
	
	public String[] getResolvedAddresses() {
		return resolvedAddresses;
	}
	
	public void setResolvedAddresses(String[] resolvedAddresses) {
		this.resolvedAddresses = resolvedAddresses;
	}
	
	public long getResponseTimeMs() {
		return responseTimeMs;
	}
	
	public void setResponseTimeMs(long responseTimeMs) {
		this.responseTimeMs = responseTimeMs;
	}
	
	public boolean isCached() {
		return cached;
	}
	
	public void setCached(boolean cached) {
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
