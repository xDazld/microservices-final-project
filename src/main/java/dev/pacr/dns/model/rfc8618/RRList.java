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
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RRList {
	
	@JsonProperty("rr-index")
	private List<Integer> rrIndex;
	
	// Constructors
	
	/**
	 * Constructs a new RRList.
	 */
	public RRList() {
	}
	
	/**
	 * RRList method.
	 */
	public RRList(List<Integer> rrIndex) {
		this.rrIndex = rrIndex;
	}
	
	// Getters and Setters
	
	/**
	 * Gets the RrIndex.
	 * @return the RrIndex
	 */
	public List<Integer> getRrIndex() {
		return rrIndex;
	}
	
	/**
	 * Sets the RrIndex.
	 * @param rrIndex the RrIndex to set
	 */
	public void setRrIndex(List<Integer> rrIndex) {
		this.rrIndex = rrIndex;
	}
}

