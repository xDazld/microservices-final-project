package dev.pacr.dns.model;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents a DNS query with all relevant information
 */
public class DNSQuery {
	
	private String id;
	private String domain;
	private String queryType; // A, AAAA, MX, TXT, etc.
	private String clientIp;
	private Instant timestamp;
	private String protocol; // UDP, TCP, DoH, DoT
	
	public DNSQuery() {
		this.id = UUID.randomUUID().toString();
		this.timestamp = Instant.now();
	}
	
	public DNSQuery(String domain, String queryType, String clientIp, String protocol) {
		this();
		this.domain = domain;
		this.queryType = queryType;
		this.clientIp = clientIp;
		this.protocol = protocol;
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
	
	public Instant getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(Instant timestamp) {
		this.timestamp = timestamp;
	}
	
	public String getProtocol() {
		return protocol;
	}
	
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	
	@Override
	public String toString() {
		return "DNSQuery{" + "id='" + id + '\'' + ", domain='" + domain + '\'' + ", queryType='" +
				queryType + '\'' + ", clientIp='" + clientIp + '\'' + ", timestamp=" + timestamp +
				", protocol='" + protocol + '\'' + '}';
	}
}
