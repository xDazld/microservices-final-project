package dev.pacr.dns.model.rfc8618;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
	  * RFC 8618 C-DNS ClassType
  * <p>
  * Represents a DNS class and type pair.
  *
  * @see <a href="https://tools.ietf.org/html/rfc8618">RFC 8618</a>
  */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClassType {
	
	@JsonProperty("type")
	private Integer type;
	
	@JsonProperty("class")
	private Integer rclass;
	
	// Constructors
	
	/**
	  * Constructs a new ClassType.
	  */
	public ClassType() {
	}
	
	/**

	
	 * Constructs a new ClassType.

	
	 */

	
	public ClassType(Integer type, Integer rclass) {
		this.type = type;
		this.rclass = rclass;
	}
	
	// Getters and Setters
	
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
}

