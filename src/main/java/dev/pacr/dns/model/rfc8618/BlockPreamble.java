package dev.pacr.dns.model.rfc8618;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
	  * RFC 8618 C-DNS Block Preamble
  * <p>
  * Contains metadata about a C-DNS block.
  *
  * @see <a href="https://tools.ietf.org/html/rfc8618">RFC 8618</a>
  */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BlockPreamble {
	
	@JsonProperty("earliest-time")
	private Long earliestTime;
	
	@JsonProperty("block-parameters-index")
	private Integer blockParametersIndex;
	
	// Constructors
	
	/**
	  * Constructs a new BlockPreamble.
	  */
	public BlockPreamble() {
	}
	
	// Getters and Setters
	
	/**
	  * Gets the EarliestTime.
	  * @return the EarliestTime
	  */
	public Long getEarliestTime() {
		return earliestTime;
	}
	
	/**
	  * Sets the EarliestTime.
	  * @param earliestTime the EarliestTime to set
	  */
	public void setEarliestTime(Long earliestTime) {
		this.earliestTime = earliestTime;
	}
	
	/**
	  * Gets the BlockParametersIndex.
	  * @return the BlockParametersIndex
	  */
	public Integer getBlockParametersIndex() {
		return blockParametersIndex;
	}
	
	/**
	  * Sets the BlockParametersIndex.
	  * @param blockParametersIndex the BlockParametersIndex to set
	  */
	public void setBlockParametersIndex(Integer blockParametersIndex) {
		this.blockParametersIndex = blockParametersIndex;
	}
}

