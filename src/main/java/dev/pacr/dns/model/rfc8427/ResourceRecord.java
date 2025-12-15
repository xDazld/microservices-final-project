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
	
	/**
	 * Constructs a new ResourceRecord.
	 */
	public ResourceRecord() {
	}
	
	/**
	 * ResourceRecord method.
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
	 * Gets the Name.
	 * @return the Name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the Name.
	 * @param name the Name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Gets the Type.
	 * @return the Type
	 */
	public Integer getType() {
		return type;
	}
	
	/**
	 * Sets the Type.
	 * @param type the Type to set
	 */
	public void setType(Integer type) {
		this.type = type;
	}
	
	/**
	 * Gets the Rclass.
	 * @return the Rclass
	 */
	public Integer getRclass() {
		return rclass;
	}
	
	/**
	 * Sets the Rclass.
	 * @param rclass the Rclass to set
	 */
	public void setRclass(Integer rclass) {
		this.rclass = rclass;
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
	 * Gets the Rdlength.
	 * @return the Rdlength
	 */
	public Integer getRdlength() {
		return rdlength;
	}
	
	/**
	 * Sets the Rdlength.
	 * @param rdlength the Rdlength to set
	 */
	public void setRdlength(Integer rdlength) {
		this.rdlength = rdlength;
	}
	
	/**
	 * Gets the Rdata.
	 * @return the Rdata
	 */
	public String getRdata() {
		return rdata;
	}
	
	/**
	 * Sets the Rdata.
	 * @param rdata the Rdata to set
	 */
	public void setRdata(String rdata) {
		this.rdata = rdata;
		if (rdata != null) {
			this.rdlength = rdata.length();
		}
	}
}

