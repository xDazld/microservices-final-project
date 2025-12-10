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
	
	public AddressEventCount() {
	}
	
	// Getters and Setters
	
	public Integer getAeType() {
		return aeType;
	}
	
	public void setAeType(Integer aeType) {
		this.aeType = aeType;
	}
	
	public Integer getAeCode() {
		return aeCode;
	}
	
	public void setAeCode(Integer aeCode) {
		this.aeCode = aeCode;
	}
	
	public Integer getAeAddressIndex() {
		return aeAddressIndex;
	}
	
	public void setAeAddressIndex(Integer aeAddressIndex) {
		this.aeAddressIndex = aeAddressIndex;
	}
	
	public Long getAeCount() {
		return aeCount;
	}
	
	public void setAeCount(Long aeCount) {
		this.aeCount = aeCount;
	}
}

