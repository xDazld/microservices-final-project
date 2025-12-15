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
	
	/**
	 * Constructs a new BlockStatistics.
	 */
	public BlockStatistics() {
	}
	
	// Getters and Setters
	
	/**
	 * Gets the ProcessedMessages.
	 * @return the ProcessedMessages
	 */
	public Long getProcessedMessages() {
		return processedMessages;
	}
	
	/**
	 * Sets the ProcessedMessages.
	 * @param processedMessages the ProcessedMessages to set
	 */
	public void setProcessedMessages(Long processedMessages) {
		this.processedMessages = processedMessages;
	}
	
	/**
	 * Gets the QrDataItems.
	 * @return the QrDataItems
	 */
	public Long getQrDataItems() {
		return qrDataItems;
	}
	
	/**
	 * Sets the QrDataItems.
	 * @param qrDataItems the QrDataItems to set
	 */
	public void setQrDataItems(Long qrDataItems) {
		this.qrDataItems = qrDataItems;
	}
	
	/**
	 * Gets the UnmatchedQueries.
	 * @return the UnmatchedQueries
	 */
	public Long getUnmatchedQueries() {
		return unmatchedQueries;
	}
	
	/**
	 * Sets the UnmatchedQueries.
	 * @param unmatchedQueries the UnmatchedQueries to set
	 */
	public void setUnmatchedQueries(Long unmatchedQueries) {
		this.unmatchedQueries = unmatchedQueries;
	}
	
	/**
	 * Gets the UnmatchedResponses.
	 * @return the UnmatchedResponses
	 */
	public Long getUnmatchedResponses() {
		return unmatchedResponses;
	}
	
	/**
	 * Sets the UnmatchedResponses.
	 * @param unmatchedResponses the UnmatchedResponses to set
	 */
	public void setUnmatchedResponses(Long unmatchedResponses) {
		this.unmatchedResponses = unmatchedResponses;
	}
	
	/**
	 * Gets the DiscardedOpcode.
	 * @return the DiscardedOpcode
	 */
	public Long getDiscardedOpcode() {
		return discardedOpcode;
	}
	
	/**
	 * Sets the DiscardedOpcode.
	 * @param discardedOpcode the DiscardedOpcode to set
	 */
	public void setDiscardedOpcode(Long discardedOpcode) {
		this.discardedOpcode = discardedOpcode;
	}
	
	/**
	 * Gets the MalformedItems.
	 * @return the MalformedItems
	 */
	public Long getMalformedItems() {
		return malformedItems;
	}
	
	/**
	 * Sets the MalformedItems.
	 * @param malformedItems the MalformedItems to set
	 */
	public void setMalformedItems(Long malformedItems) {
		this.malformedItems = malformedItems;
	}
}

