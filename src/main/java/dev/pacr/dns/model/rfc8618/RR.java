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
	
	/**
	 * Constructs a new RR.
	 */
	public RR() {
	}
	
	/**
	 * RR method.
	 */
	public RR(Integer nameIndex, Integer classtypeIndex, Long ttl, Integer rdataIndex) {
		this.nameIndex = nameIndex;
		this.classtypeIndex = classtypeIndex;
		this.ttl = ttl;
		this.rdataIndex = rdataIndex;
	}
	
	// Getters and Setters
	
	/**
	 * Gets the NameIndex.
	 * @return the NameIndex
	 */
	public Integer getNameIndex() {
		return nameIndex;
	}
	
	/**
	 * Sets the NameIndex.
	 * @param nameIndex the NameIndex to set
	 */
	public void setNameIndex(Integer nameIndex) {
		this.nameIndex = nameIndex;
	}
	
	/**
	 * Gets the ClasstypeIndex.
	 * @return the ClasstypeIndex
	 */
	public Integer getClasstypeIndex() {
		return classtypeIndex;
	}
	
	/**
	 * Sets the ClasstypeIndex.
	 * @param classtypeIndex the ClasstypeIndex to set
	 */
	public void setClasstypeIndex(Integer classtypeIndex) {
		this.classtypeIndex = classtypeIndex;
	}
	
	/**
	 * Gets the Ttl.
	 * @return the Ttl
	 */
	public Long getTtl() {
		return ttl;
	}
	
	/**
	 * Sets the Ttl.
	 * @param ttl the Ttl to set
	 */
	public void setTtl(Long ttl) {
		this.ttl = ttl;
	}
	
	/**
	 * Gets the RdataIndex.
	 * @return the RdataIndex
	 */
	public Integer getRdataIndex() {
		return rdataIndex;
	}
	
	/**
	 * Sets the RdataIndex.
	 * @param rdataIndex the RdataIndex to set
	 */
	public void setRdataIndex(Integer rdataIndex) {
		this.rdataIndex = rdataIndex;
	}
}

