package dev.pacr.dns.model.rfc8427;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * RFC 8427 compliant Resource Record representation
 * <p>
 * Represents a DNS Resource Record as defined in RFC 8427. Field names match the RFC
 * specification.
 *
 * @see <a href="https://tools.ietf.org/html/rfc8427">RFC 8427</a>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResourceRecord {
	
	@JsonProperty("NAME")
	private String name;
	
	@JsonProperty("TYPE")
	private Integer type;
	
	@JsonProperty("CLASS")
	private Integer rclass;
	
	@JsonProperty("TTL")
	private Long ttl;
	
	@JsonProperty("RDLENGTH")
	private Integer rdlength;
	
	@JsonProperty("RDATA")
	private String rdata;
	
	// Constructors
	
	public ResourceRecord() {
	}
	
	public ResourceRecord(String name, Integer type, Integer rclass, Long ttl, String rdata) {
		this.name = name;
		this.type = type;
		this.rclass = rclass;
		this.ttl = ttl;
		this.rdata = rdata;
		if (rdata != null) {
			this.rdlength = rdata.length();
		}
	}
	
	// Getters and Setters
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Integer getType() {
		return type;
	}
	
	public void setType(Integer type) {
		this.type = type;
	}
	
	public Integer getRclass() {
		return rclass;
	}
	
	public void setRclass(Integer rclass) {
		this.rclass = rclass;
	}
	
	public Long getTtl() {
		return ttl;
	}
	
	public void setTtl(Long ttl) {
		this.ttl = ttl;
	}
	
	public Integer getRdlength() {
		return rdlength;
	}
	
	public void setRdlength(Integer rdlength) {
		this.rdlength = rdlength;
	}
	
	public String getRdata() {
		return rdata;
	}
	
	public void setRdata(String rdata) {
		this.rdata = rdata;
		if (rdata != null) {
			this.rdlength = rdata.length();
		}
	}
}

