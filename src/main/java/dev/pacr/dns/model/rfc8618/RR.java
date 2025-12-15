package dev.pacr.dns.model.rfc8618;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * RFC 8618 C-DNS Resource Record
 * <p>
 * Represents a DNS resource record in C-DNS format.
 *
 * @see <a href="https://tools.ietf.org/html/rfc8618">RFC 8618</a>
 * @author Patrick Rafferty
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RR {
	
	/**
	 * The name index.
	 */
	@JsonProperty("name-index")
	private Integer nameIndex;
	
	/**
	 * The classtype index.
	 */
	@JsonProperty("classtype-index")
	private Integer classtypeIndex;
	
	/**
	 * The ttl.
	 */
	@JsonProperty("ttl")
	private Long ttl;
	
	/**
	 * The rdata index.
	 */
	@JsonProperty("rdata-index")
	private Integer rdataIndex;
	
	// Constructors
	
	/**
	 * Default constructor.
	 */
	public RR() {
	}
	
	/**
	 * Constructor with name index, classtype index, ttl, and rdata index.
	 *
	 * @param nameIndex the name index
	 * @param classtypeIndex the classtype index
	 * @param ttl the ttl
	 * @param rdataIndex the rdata index
	 */
	public RR(Integer nameIndex, Integer classtypeIndex, Long ttl, Integer rdataIndex) {
		this.nameIndex = nameIndex;
		this.classtypeIndex = classtypeIndex;
		this.ttl = ttl;
		this.rdataIndex = rdataIndex;
	}
	
	// Getters and Setters
	
	/**
	 * Gets the name index.
	 *
	 * @return the nameIndex
	 */
	public Integer getNameIndex() {
		return nameIndex;
	}
	
	/**
	 * Sets the name index.
	 *
	 * @param nameIndex the nameIndex to set
	 */
	public void setNameIndex(Integer nameIndex) {
		this.nameIndex = nameIndex;
	}
	
	/**
	 * Gets the classtype index.
	 *
	 * @return the classtypeIndex
	 */
	public Integer getClasstypeIndex() {
		return classtypeIndex;
	}
	
	/**
	 * Sets the classtype index.
	 *
	 * @param classtypeIndex the classtypeIndex to set
	 */
	public void setClasstypeIndex(Integer classtypeIndex) {
		this.classtypeIndex = classtypeIndex;
	}
	
	/**
	 * Gets the ttl.
	 *
	 * @return the ttl
	 */
	public Long getTtl() {
		return ttl;
	}
	
	/**
	 * Sets the ttl.
	 *
	 * @param ttl the ttl to set
	 */
	public void setTtl(Long ttl) {
		this.ttl = ttl;
	}
	
	/**
	 * Gets the rdata index.
	 *
	 * @return the rdataIndex
	 */
	public Integer getRdataIndex() {
		return rdataIndex;
	}
	
	/**
	 * Sets the rdata index.
	 *
	 * @param rdataIndex the rdataIndex to set
	 */
	public void setRdataIndex(Integer rdataIndex) {
		this.rdataIndex = rdataIndex;
	}
}
