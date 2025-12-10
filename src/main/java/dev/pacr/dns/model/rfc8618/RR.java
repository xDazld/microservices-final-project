package dev.pacr.dns.model.rfc8618;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * RFC 8618 C-DNS Resource Record
 * <p>
 * Represents a DNS resource record in C-DNS format.
 *
 * @see <a href="https://tools.ietf.org/html/rfc8618">RFC 8618</a>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RR {
	
	@JsonProperty("name-index")
	private Integer nameIndex;
	
	@JsonProperty("classtype-index")
	private Integer classtypeIndex;
	
	@JsonProperty("ttl")
	private Long ttl;
	
	@JsonProperty("rdata-index")
	private Integer rdataIndex;
	
	// Constructors
	
	public RR() {
	}
	
	public RR(Integer nameIndex, Integer classtypeIndex, Long ttl, Integer rdataIndex) {
		this.nameIndex = nameIndex;
		this.classtypeIndex = classtypeIndex;
		this.ttl = ttl;
		this.rdataIndex = rdataIndex;
	}
	
	// Getters and Setters
	
	public Integer getNameIndex() {
		return nameIndex;
	}
	
	public void setNameIndex(Integer nameIndex) {
		this.nameIndex = nameIndex;
	}
	
	public Integer getClasstypeIndex() {
		return classtypeIndex;
	}
	
	public void setClasstypeIndex(Integer classtypeIndex) {
		this.classtypeIndex = classtypeIndex;
	}
	
	public Long getTtl() {
		return ttl;
	}
	
	public void setTtl(Long ttl) {
		this.ttl = ttl;
	}
	
	public Integer getRdataIndex() {
		return rdataIndex;
	}
	
	public void setRdataIndex(Integer rdataIndex) {
		this.rdataIndex = rdataIndex;
	}
}

