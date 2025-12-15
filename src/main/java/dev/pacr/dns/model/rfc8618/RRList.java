package dev.pacr.dns.model.rfc8618;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * RFC 8618 C-DNS RR List
 * <p>
 * List of resource record indices.
 *
 * @see <a href="https://tools.ietf.org/html/rfc8618">RFC 8618</a>
 * @author Patrick Rafferty
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RRList {
	
	/**
	 * The rr index.
	 */
	@JsonProperty("rr-index")
	private List<Integer> rrIndex;
	
	// Constructors
	
	/**
	 * Default constructor.
	 */
	public RRList() {
	}
	
	/**
	 * Constructor with rr index.
	 *
	 * @param rrIndex the rr index
	 */
	public RRList(List<Integer> rrIndex) {
		this.rrIndex = rrIndex;
	}
	
	// Getters and Setters
	
	/**
	 * Gets the rr index.
	 *
	 * @return the rrIndex
	 */
	public List<Integer> getRrIndex() {
		return rrIndex;
	}
	
	/**
	 * Sets the rr index.
	 *
	 * @param rrIndex the rrIndex to set
	 */
	public void setRrIndex(List<Integer> rrIndex) {
		this.rrIndex = rrIndex;
	}
}
