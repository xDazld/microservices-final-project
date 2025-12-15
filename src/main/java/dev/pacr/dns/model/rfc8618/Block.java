package dev.pacr.dns.model.rfc8618;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * RFC 8618 C-DNS Block
 * <p>
 * Represents a block of DNS data in the C-DNS format.
 *
 * @see <a href="https://tools.ietf.org/html/rfc8618">RFC 8618</a>
 * @author Patrick Rafferty
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Block {
	
	/**
	 * The block preamble.
	 */
	@JsonProperty("block-preamble")
	private BlockPreamble blockPreamble;
	
	/**
	 * The block statistics.
	 */
	@JsonProperty("block-statistics")
	private BlockStatistics blockStatistics;
	
	/**
	 * The block tables.
	 */
	@JsonProperty("block-tables")
	private BlockTables blockTables;
	
	/**
	 * The query responses.
	 */
	@JsonProperty("query-responses")
	private List<QueryResponse> queryResponses;
	
	/**
	 * The address event counts.
	 */
	@JsonProperty("address-event-counts")
	private List<AddressEventCount> addressEventCounts;
	
	/**
	 * The malformed messages.
	 */
	@JsonProperty("malformed-messages")
	private List<MalformedMessage> malformedMessages;
	
	// Constructors
	
	/**
	 * Default constructor.
	 */
	public Block() {
	}
	
	// Getters and Setters
	
	/**
	 * Gets the block preamble.
	 *
	 * @return the blockPreamble
	 */
	public BlockPreamble getBlockPreamble() {
		return blockPreamble;
	}
	
	/**
	 * Sets the block preamble.
	 *
	 * @param blockPreamble the blockPreamble to set
	 */
	public void setBlockPreamble(BlockPreamble blockPreamble) {
		this.blockPreamble = blockPreamble;
	}
	
	/**
	 * Gets the block statistics.
	 *
	 * @return the blockStatistics
	 */
	public BlockStatistics getBlockStatistics() {
		return blockStatistics;
	}
	
	/**
	 * Sets the block statistics.
	 *
	 * @param blockStatistics the blockStatistics to set
	 */
	public void setBlockStatistics(BlockStatistics blockStatistics) {
		this.blockStatistics = blockStatistics;
	}
	
	/**
	 * Gets the block tables.
	 *
	 * @return the blockTables
	 */
	public BlockTables getBlockTables() {
		return blockTables;
	}
	
	/**
	 * Sets the block tables.
	 *
	 * @param blockTables the blockTables to set
	 */
	public void setBlockTables(BlockTables blockTables) {
		this.blockTables = blockTables;
	}
	
	/**
	 * Gets the query responses.
	 *
	 * @return the queryResponses
	 */
	public List<QueryResponse> getQueryResponses() {
		return queryResponses;
	}
	
	/**
	 * Sets the query responses.
	 *
	 * @param queryResponses the queryResponses to set
	 */
	public void setQueryResponses(List<QueryResponse> queryResponses) {
		this.queryResponses = queryResponses;
	}
	
	/**
	 * Gets the address event counts.
	 *
	 * @return the addressEventCounts
	 */
	public List<AddressEventCount> getAddressEventCounts() {
		return addressEventCounts;
	}
	
	/**
	 * Sets the address event counts.
	 *
	 * @param addressEventCounts the addressEventCounts to set
	 */
	public void setAddressEventCounts(List<AddressEventCount> addressEventCounts) {
		this.addressEventCounts = addressEventCounts;
	}
	
	/**
	 * Gets the malformed messages.
	 *
	 * @return the malformedMessages
	 */
	public List<MalformedMessage> getMalformedMessages() {
		return malformedMessages;
	}
	
	/**
	 * Sets the malformed messages.
	 *
	 * @param malformedMessages the malformedMessages to set
	 */
	public void setMalformedMessages(List<MalformedMessage> malformedMessages) {
		this.malformedMessages = malformedMessages;
	}
}
