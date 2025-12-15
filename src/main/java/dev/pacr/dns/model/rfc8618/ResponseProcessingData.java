package dev.pacr.dns.model.rfc8618;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
	 * RFC 8618 C-DNS Response Processing Data
 * <p>
 * Additional processing data for DNS responses.
 *
 * @see <a href="https://tools.ietf.org/html/rfc8618">RFC 8618</a>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
/**
	 * ResponseProcessingData class.
 */
public class ResponseProcessingData {
	
	@JsonProperty("bailiwick-index")
	private Integer bailiwickIndex;
	
	@JsonProperty("processing-flags")
	private Long processingFlags;
	
	// Constructors
	
	/**
	 * Constructs a new ResponseProcessingData.
	 */
	public ResponseProcessingData() {
	}
	
	// Getters and Setters
	
	/**
	 * Gets the BailiwickIndex.
	 * @return the BailiwickIndex
	 */
	public Integer getBailiwickIndex() {
		return bailiwickIndex;
	}
	
	/**
	 * Sets the BailiwickIndex.
	 * @param bailiwickIndex the BailiwickIndex to set
	 */
	public void setBailiwickIndex(Integer bailiwickIndex) {
		this.bailiwickIndex = bailiwickIndex;
	}
	
	/**
	 * Gets the ProcessingFlags.
	 * @return the ProcessingFlags
	 */
	public Long getProcessingFlags() {
		return processingFlags;
	}
	
	/**
	 * Sets the ProcessingFlags.
	 * @param processingFlags the ProcessingFlags to set
	 */
	public void setProcessingFlags(Long processingFlags) {
		this.processingFlags = processingFlags;
	}
}

