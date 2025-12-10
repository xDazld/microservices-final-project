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
	
	public ClassType() {
	}
	
	public ClassType(Integer type, Integer rclass) {
		this.type = type;
		this.rclass = rclass;
	}
	
	// Getters and Setters
	
	public Integer getType() {
		return type;
	}
	
	public void setType(Integer type) {
		this.type = type;
	}
	
	public Integer getRclass() {
		return rclass;
	}
	
	public void setRclass(Integer rclass) {
		this.rclass = rclass;
	}
}

