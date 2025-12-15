package dev.pacr.dns.model.rfc8618;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * RFC 8618 C-DNS Response Processing Data
 * <p>
 * Additional processing data for DNS responses.
 *
 * @see <a href="https://tools.ietf.org/html/rfc8618">RFC 8618</a>
 * @author Patrick Rafferty
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseProcessingData {
	
	/**
	 * The bailiwick index.
	 */
	@JsonProperty("bailiwick-index")
	private Integer bailiwickIndex;
	
	/**
	 * The processing flags.
	 */
	@JsonProperty("processing-flags")
	private Long processingFlags;
	
	// Constructors
	
	/**
	 * Default constructor.
	 */
	public ResponseProcessingData() {
	}
	
	// Getters and Setters
	
	/**
	 * Gets the bailiwick index.
	 *
	 * @return the bailiwickIndex
	 */
	public Integer getBailiwickIndex() {
		return bailiwickIndex;
	}
	
	/**
	 * Sets the bailiwick index.
	 *
	 * @param bailiwickIndex the bailiwickIndex to set
	 */
	public void setBailiwickIndex(Integer bailiwickIndex) {
		this.bailiwickIndex = bailiwickIndex;
	}
	
	/**
	 * Gets the processing flags.
	 *
	 * @return the processingFlags
	 */
	public Long getProcessingFlags() {
		return processingFlags;
	}
	
	/**
	 * Sets the processing flags.
	 *
	 * @param processingFlags the processingFlags to set
	 */
	public void setProcessingFlags(Long processingFlags) {
		this.processingFlags = processingFlags;
	}
}
