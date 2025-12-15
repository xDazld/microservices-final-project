package dev.pacr.dns.model.rfc8618;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * RFC 8618 C-DNS ClassType
 * <p>
 * Represents a DNS class and type pair.
 *
 * @see <a href="https://tools.ietf.org/html/rfc8618">RFC 8618</a>
 * @author Patrick Rafferty
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClassType {
	
	/**
	 * The type.
	 */
	@JsonProperty("type")
	private Integer type;
	
	/**
	 * The rclass.
	 */
	@JsonProperty("class")
	private Integer rclass;
	
	// Constructors
	
	/**
	 * Default constructor.
	 */
	public ClassType() {
	}
	
	/**
	 * Constructor with type and rclass.
	 *
	 * @param type the type
	 * @param rclass the rclass
	 */
	public ClassType(Integer type, Integer rclass) {
		this.type = type;
		this.rclass = rclass;
	}
	
	// Getters and Setters
	
	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public Integer getType() {
		return type;
	}
	
	/**
	 * Sets the type.
	 *
	 * @param type the type to set
	 */
	public void setType(Integer type) {
		this.type = type;
	}
	
	/**
	 * Gets the rclass.
	 *
	 * @return the rclass
	 */
	public Integer getRclass() {
		return rclass;
	}
	
	/**
	 * Sets the rclass.
	 *
	 * @param rclass the rclass to set
	 */
	public void setRclass(Integer rclass) {
		this.rclass = rclass;
	}
}
