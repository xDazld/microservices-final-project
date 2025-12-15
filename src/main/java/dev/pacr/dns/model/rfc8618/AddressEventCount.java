package dev.pacr.dns.model.rfc8618;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
	  * RFC 8618 C-DNS Address Event Count
  * <p>
  * Count of events for a particular address.
  *
  * @see <a href="https://tools.ietf.org/html/rfc8618">RFC 8618</a>
  */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddressEventCount {
	
	@JsonProperty("ae-type")
	private Integer aeType;
	
	@JsonProperty("ae-code")
	private Integer aeCode;
	
	@JsonProperty("ae-address-index")
	private Integer aeAddressIndex;
	
	@JsonProperty("ae-count")
	private Long aeCount;
	
	// Constructors
	
	/**
	  * Constructs a new AddressEventCount.
	  */
	public AddressEventCount() {
	}
	
	// Getters and Setters
	
	/**
	  * Gets the AeType.
	  * @return the AeType
	  */
	public Integer getAeType() {
		return aeType;
	}
	
	/**
	  * Sets the AeType.
	  * @param aeType the AeType to set
	  */
	public void setAeType(Integer aeType) {
		this.aeType = aeType;
	}
	
	/**
	  * Gets the AeCode.
	  * @return the AeCode
	  */
	public Integer getAeCode() {
		return aeCode;
	}
	
	/**
	  * Sets the AeCode.
	  * @param aeCode the AeCode to set
	  */
	public void setAeCode(Integer aeCode) {
		this.aeCode = aeCode;
	}
	
	/**
	  * Gets the AeAddressIndex.
	  * @return the AeAddressIndex
	  */
	public Integer getAeAddressIndex() {
		return aeAddressIndex;
	}
	
	/**
	  * Sets the AeAddressIndex.
	  * @param aeAddressIndex the AeAddressIndex to set
	  */
	public void setAeAddressIndex(Integer aeAddressIndex) {
		this.aeAddressIndex = aeAddressIndex;
	}
	
	/**
	  * Gets the AeCount.
	  * @return the AeCount
	  */
	public Long getAeCount() {
		return aeCount;
	}
	
	/**
	  * Sets the AeCount.
	  * @param aeCount the AeCount to set
	  */
	public void setAeCount(Long aeCount) {
		this.aeCount = aeCount;
	}
}

