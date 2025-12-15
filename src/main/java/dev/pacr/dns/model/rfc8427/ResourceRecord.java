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
 * @author Patrick Rafferty
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResourceRecord {
	
	/**
	 * The name.
	 */
	@JsonProperty("NAME")
	private String name;
	
	/**
	 * The type.
	 */
	@JsonProperty("TYPE")
	private Integer type;
	
	/**
	 * The rclass.
	 */
	@JsonProperty("CLASS")
	private Integer rclass;
	
	/**
	 * The ttl.
	 */
	@JsonProperty("TTL")
	private Long ttl;
	
	/**
	 * The rdlength.
	 */
	@JsonProperty("RDLENGTH")
	private Integer rdlength;
	
	/**
	 * The rdata.
	 */
	@JsonProperty("RDATA")
	private String rdata;
	
	// Constructors
	
	/**
	 * Default constructor.
	 */
	public ResourceRecord() {
	}
	
	/**
	 * Constructor with name, type, rclass, ttl, rdata.
	 *
	 * @param name the name
	 * @param type the type
	 * @param rclass the rclass
	 * @param ttl the ttl
	 * @param rdata the rdata
	 */
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
	
	/**
	 * Gets name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets name.
	 *
	 * @param name the name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Gets type.
	 *
	 * @return the type
	 */
	public Integer getType() {
		return type;
	}
	
	/**
	 * Sets type.
	 *
	 * @param type the type
	 */
	public void setType(Integer type) {
		this.type = type;
	}
	
	/**
	 * Gets rclass.
	 *
	 * @return the rclass
	 */
	public Integer getRclass() {
		return rclass;
	}
	
	/**
	 * Sets rclass.
	 *
	 * @param rclass the rclass
	 */
	public void setRclass(Integer rclass) {
		this.rclass = rclass;
	}
	
	/**
	 * Gets ttl.
	 *
	 * @return the ttl
	 */
	public Long getTtl() {
		return ttl;
	}
	
	/**
	 * Sets ttl.
	 *
	 * @param ttl the ttl
	 */
	public void setTtl(Long ttl) {
		this.ttl = ttl;
	}
	
	/**
	 * Gets rdlength.
	 *
	 * @return the rdlength
	 */
	public Integer getRdlength() {
		return rdlength;
	}
	
	/**
	 * Sets rdlength.
	 *
	 * @param rdlength the rdlength
	 */
	public void setRdlength(Integer rdlength) {
		this.rdlength = rdlength;
	}
	
	/**
	 * Gets rdata.
	 *
	 * @return the rdata
	 */
	public String getRdata() {
		return rdata;
	}
	
	/**
	 * Sets rdata.
	 *
	 * @param rdata the rdata
	 */
	public void setRdata(String rdata) {
		this.rdata = rdata;
		if (rdata != null) {
			this.rdlength = rdata.length();
		}
	}
}
