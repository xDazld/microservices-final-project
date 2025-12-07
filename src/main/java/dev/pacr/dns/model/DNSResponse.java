package dev.pacr.dns.model;

import java.time.Instant;
import java.util.List;

/**
 * Represents a DNS response with resolution results
 */
public class DNSResponse {
	
	private String queryId;
	private String domain;
	private List<String> resolvedAddresses;
	private String status; // ALLOWED, BLOCKED, ERROR, NXDOMAIN
	private String blockReason; // null if allowed, otherwise reason for blocking
	private long responseTimeMs;
	private Instant timestamp;
	private boolean cached;
	
	public DNSResponse() {
		this.timestamp = Instant.now();
	}
	
	public DNSResponse(String queryId, String domain, List<String> resolvedAddresses,
					   String status) {
		this();
		this.queryId = queryId;
		this.domain = domain;
		this.resolvedAddresses = resolvedAddresses;
		this.status = status;
	}
	
	// Getters and Setters
	
	public String getQueryId() {
		return queryId;
	}
	
	public void setQueryId(String queryId) {
		this.queryId = queryId;
	}
	
	public String getDomain() {
		return domain;
	}
	
	public void setDomain(String domain) {
		this.domain = domain;
	}
	
	public List<String> getResolvedAddresses() {
		return resolvedAddresses;
	}
	
	public void setResolvedAddresses(List<String> resolvedAddresses) {
		this.resolvedAddresses = resolvedAddresses;
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
	
	public long getResponseTimeMs() {
		return responseTimeMs;
	}
	
	public void setResponseTimeMs(long responseTimeMs) {
		this.responseTimeMs = responseTimeMs;
	}
	
	public Instant getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(Instant timestamp) {
		this.timestamp = timestamp;
	}
	
	public boolean isCached() {
		return cached;
	}
	
	public void setCached(boolean cached) {
		this.cached = cached;
	}
	
	@Override
	public String toString() {
		return "DNSResponse{" + "queryId='" + queryId + '\'' + ", domain='" + domain + '\'' +
				", resolvedAddresses=" + resolvedAddresses + ", status='" + status + '\'' +
				", blockReason='" + blockReason + '\'' + ", responseTimeMs=" + responseTimeMs +
				", timestamp=" + timestamp + ", cached=" + cached + '}';
	}
}
