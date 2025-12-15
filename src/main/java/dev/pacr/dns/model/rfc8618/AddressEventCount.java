package dev.pacr.dns.model.rfc8618;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * RFC 8618 C-DNS Address Event Count
 * <p>
 * Count of events for a particular address.
 *
 * @see <a href="https://tools.ietf.org/html/rfc8618">RFC 8618</a>
 * @author Patrick Rafferty
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddressEventCount {
	
	/**
	 * The aeType.
	 */
	@JsonProperty("ae-type")
	private Integer aeType;
	
	/**
	 * The aeCode.
	 */
	@JsonProperty("ae-code")
	private Integer aeCode;
	
	/**
	 * The aeAddressIndex.
	 */
	@JsonProperty("ae-address-index")
	private Integer aeAddressIndex;
	
	/**
	 * The aeCount.
	 */
	@JsonProperty("ae-count")
	private Long aeCount;
	
	// Constructors
	
	/**
	 * Default constructor.
	 */
	public AddressEventCount() {
	}
	
	// Getters and Setters
	
	/**
	 * Gets the aeType.
	 *
	 * @return the aeType
	 */
	public Integer getAeType() {
		return aeType;
	}
	
	/**
	 * Sets the aeType.
	 *
	 * @param aeType the aeType to set
	 */
	public void setAeType(Integer aeType) {
		this.aeType = aeType;
	}
	
	/**
	 * Gets the aeCode.
	 *
	 * @return the aeCode
	 */
	public Integer getAeCode() {
		return aeCode;
	}
	
	/**
	 * Sets the aeCode.
	 *
	 * @param aeCode the aeCode to set
	 */
	public void setAeCode(Integer aeCode) {
		this.aeCode = aeCode;
	}
	
	/**
	 * Gets the aeAddressIndex.
	 *
	 * @return the aeAddressIndex
	 */
	public Integer getAeAddressIndex() {
		return aeAddressIndex;
	}
	
	/**
	 * Sets the aeAddressIndex.
	 *
	 * @param aeAddressIndex the aeAddressIndex to set
	 */
	public void setAeAddressIndex(Integer aeAddressIndex) {
		this.aeAddressIndex = aeAddressIndex;
	}
	
	/**
	 * Gets the aeCount.
	 *
	 * @return the aeCount
	 */
	public Long getAeCount() {
		return aeCount;
	}
	
	/**
	 * Sets the aeCount.
	 *
	 * @param aeCount the aeCount to set
	 */
	public void setAeCount(Long aeCount) {
		this.aeCount = aeCount;
	}
}
