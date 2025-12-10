package dev.pacr.dns.model.rfc8618;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * RFC 8618 C-DNS Block Statistics
 * <p>
 * Statistics about DNS traffic in this block.
 *
 * @see <a href="https://tools.ietf.org/html/rfc8618">RFC 8618</a>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BlockStatistics {
	
	@JsonProperty("processed-messages")
	private Long processedMessages;
	
	@JsonProperty("qr-data-items")
	private Long qrDataItems;
	
	@JsonProperty("unmatched-queries")
	private Long unmatchedQueries;
	
	@JsonProperty("unmatched-responses")
	private Long unmatchedResponses;
	
	@JsonProperty("discarded-opcode")
	private Long discardedOpcode;
	
	@JsonProperty("malformed-items")
	private Long malformedItems;
	
	// Constructors
	
	public BlockStatistics() {
	}
	
	// Getters and Setters
	
	public Long getProcessedMessages() {
		return processedMessages;
	}
	
	public void setProcessedMessages(Long processedMessages) {
		this.processedMessages = processedMessages;
	}
	
	public Long getQrDataItems() {
		return qrDataItems;
	}
	
	public void setQrDataItems(Long qrDataItems) {
		this.qrDataItems = qrDataItems;
	}
	
	public Long getUnmatchedQueries() {
		return unmatchedQueries;
	}
	
	public void setUnmatchedQueries(Long unmatchedQueries) {
		this.unmatchedQueries = unmatchedQueries;
	}
	
	public Long getUnmatchedResponses() {
		return unmatchedResponses;
	}
	
	public void setUnmatchedResponses(Long unmatchedResponses) {
		this.unmatchedResponses = unmatchedResponses;
	}
	
	public Long getDiscardedOpcode() {
		return discardedOpcode;
	}
	
	public void setDiscardedOpcode(Long discardedOpcode) {
		this.discardedOpcode = discardedOpcode;
	}
	
	public Long getMalformedItems() {
		return malformedItems;
	}
	
	public void setMalformedItems(Long malformedItems) {
		this.malformedItems = malformedItems;
	}
}

