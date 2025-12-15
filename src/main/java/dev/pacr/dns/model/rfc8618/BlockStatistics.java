package dev.pacr.dns.model.rfc8618;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * RFC 8618 C-DNS Block Statistics
 * <p>
 * Statistics about DNS traffic in this block.
 *
 * @see <a href="https://tools.ietf.org/html/rfc8618">RFC 8618</a>
 * @author Patrick Rafferty
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BlockStatistics {
	
	/**
	 * The processed messages.
	 */
	@JsonProperty("processed-messages")
	private Long processedMessages;
	
	/**
	 * The qr data items.
	 */
	@JsonProperty("qr-data-items")
	private Long qrDataItems;
	
	/**
	 * The unmatched queries.
	 */
	@JsonProperty("unmatched-queries")
	private Long unmatchedQueries;
	
	/**
	 * The unmatched responses.
	 */
	@JsonProperty("unmatched-responses")
	private Long unmatchedResponses;
	
	/**
	 * The discarded opcode.
	 */
	@JsonProperty("discarded-opcode")
	private Long discardedOpcode;
	
	/**
	 * The malformed items.
	 */
	@JsonProperty("malformed-items")
	private Long malformedItems;
	
	// Constructors
	
	/**
	 * Default constructor.
	 */
	public BlockStatistics() {
	}
	
	// Getters and Setters
	
	/**
	 * Gets the processed messages.
	 *
	 * @return the processedMessages
	 */
	public Long getProcessedMessages() {
		return processedMessages;
	}
	
	/**
	 * Sets the processed messages.
	 *
	 * @param processedMessages the processedMessages to set
	 */
	public void setProcessedMessages(Long processedMessages) {
		this.processedMessages = processedMessages;
	}
	
	/**
	 * Gets the qr data items.
	 *
	 * @return the qrDataItems
	 */
	public Long getQrDataItems() {
		return qrDataItems;
	}
	
	/**
	 * Sets the qr data items.
	 *
	 * @param qrDataItems the qrDataItems to set
	 */
	public void setQrDataItems(Long qrDataItems) {
		this.qrDataItems = qrDataItems;
	}
	
	/**
	 * Gets the unmatched queries.
	 *
	 * @return the unmatchedQueries
	 */
	public Long getUnmatchedQueries() {
		return unmatchedQueries;
	}
	
	/**
	 * Sets the unmatched queries.
	 *
	 * @param unmatchedQueries the unmatchedQueries to set
	 */
	public void setUnmatchedQueries(Long unmatchedQueries) {
		this.unmatchedQueries = unmatchedQueries;
	}
	
	/**
	 * Gets the unmatched responses.
	 *
	 * @return the unmatchedResponses
	 */
	public Long getUnmatchedResponses() {
		return unmatchedResponses;
	}
	
	/**
	 * Sets the unmatched responses.
	 *
	 * @param unmatchedResponses the unmatchedResponses to set
	 */
	public void setUnmatchedResponses(Long unmatchedResponses) {
		this.unmatchedResponses = unmatchedResponses;
	}
	
	/**
	 * Gets the discarded opcode.
	 *
	 * @return the discardedOpcode
	 */
	public Long getDiscardedOpcode() {
		return discardedOpcode;
	}
	
	/**
	 * Sets the discarded opcode.
	 *
	 * @param discardedOpcode the discardedOpcode to set
	 */
	public void setDiscardedOpcode(Long discardedOpcode) {
		this.discardedOpcode = discardedOpcode;
	}
	
	/**
	 * Gets the malformed items.
	 *
	 * @return the malformedItems
	 */
	public Long getMalformedItems() {
		return malformedItems;
	}
	
	/**
	 * Sets the malformed items.
	 *
	 * @param malformedItems the malformedItems to set
	 */
	public void setMalformedItems(Long malformedItems) {
		this.malformedItems = malformedItems;
	}
}
