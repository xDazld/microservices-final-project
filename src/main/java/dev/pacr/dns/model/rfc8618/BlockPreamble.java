package dev.pacr.dns.model.rfc8618;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * RFC 8618 C-DNS Block Preamble
 * <p>
 * Contains metadata about a C-DNS block.
 *
 * @see <a href="https://tools.ietf.org/html/rfc8618">RFC 8618</a>
 * @author Patrick Rafferty
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BlockPreamble {
	
	/**
	 * The earliest time.
	 */
	@JsonProperty("earliest-time")
	private Long earliestTime;
	
	/**
	 * The block parameters index.
	 */
	@JsonProperty("block-parameters-index")
	private Integer blockParametersIndex;
	
	// Constructors
	
	/**
	 * Default constructor.
	 */
	public BlockPreamble() {
	}
	
	// Getters and Setters
	
	/**
	 * Gets the earliest time.
	 *
	 * @return the earliestTime
	 */
	public Long getEarliestTime() {
		return earliestTime;
	}
	
	/**
	 * Sets the earliest time.
	 *
	 * @param earliestTime the earliestTime to set
	 */
	public void setEarliestTime(Long earliestTime) {
		this.earliestTime = earliestTime;
	}
	
	/**
	 * Gets the block parameters index.
	 *
	 * @return the blockParametersIndex
	 */
	public Integer getBlockParametersIndex() {
		return blockParametersIndex;
	}
	
	/**
	 * Sets the block parameters index.
	 *
	 * @param blockParametersIndex the blockParametersIndex to set
	 */
	public void setBlockParametersIndex(Integer blockParametersIndex) {
		this.blockParametersIndex = blockParametersIndex;
	}
}
