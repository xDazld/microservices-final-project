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
public class ResponseProcessingData {
	
	@JsonProperty("bailiwick-index")
	private Integer bailiwickIndex;
	
	@JsonProperty("processing-flags")
	private Long processingFlags;
	
	// Constructors
	
	public ResponseProcessingData() {
	}
	
	// Getters and Setters
	
	public Integer getBailiwickIndex() {
		return bailiwickIndex;
	}
	
	public void setBailiwickIndex(Integer bailiwickIndex) {
		this.bailiwickIndex = bailiwickIndex;
	}
	
	public Long getProcessingFlags() {
		return processingFlags;
	}
	
	public void setProcessingFlags(Long processingFlags) {
		this.processingFlags = processingFlags;
	}
}

