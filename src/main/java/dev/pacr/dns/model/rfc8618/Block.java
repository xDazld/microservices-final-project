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
  */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Block {
	
	@JsonProperty("block-preamble")
	private BlockPreamble blockPreamble;
	
	@JsonProperty("block-statistics")
	private BlockStatistics blockStatistics;
	
	@JsonProperty("block-tables")
	private BlockTables blockTables;
	
	@JsonProperty("query-responses")
	private List<QueryResponse> queryResponses;
	
	@JsonProperty("address-event-counts")
	private List<AddressEventCount> addressEventCounts;
	
	@JsonProperty("malformed-messages")
	private List<MalformedMessage> malformedMessages;
	
	// Constructors
	
	/**
	  * Constructs a new Block.
	  */
	public Block() {
	}
	
	// Getters and Setters
	
	/**
	  * Gets the BlockPreamble.
	  * @return the BlockPreamble
	  */
	public BlockPreamble getBlockPreamble() {
		return blockPreamble;
	}
	
	/**
	  * Sets the BlockPreamble.
	  * @param blockPreamble the BlockPreamble to set
	  */
	public void setBlockPreamble(BlockPreamble blockPreamble) {
		this.blockPreamble = blockPreamble;
	}
	
	/**
	  * Gets the BlockStatistics.
	  * @return the BlockStatistics
	  */
	public BlockStatistics getBlockStatistics() {
		return blockStatistics;
	}
	
	/**
	  * Sets the BlockStatistics.
	  * @param blockStatistics the BlockStatistics to set
	  */
	public void setBlockStatistics(BlockStatistics blockStatistics) {
		this.blockStatistics = blockStatistics;
	}
	
	/**
	  * Gets the BlockTables.
	  * @return the BlockTables
	  */
	public BlockTables getBlockTables() {
		return blockTables;
	}
	
	/**
	  * Sets the BlockTables.
	  * @param blockTables the BlockTables to set
	  */
	public void setBlockTables(BlockTables blockTables) {
		this.blockTables = blockTables;
	}
	
	/**
	  * Gets the QueryResponses.
	  * @return the QueryResponses
	  */
	public List<QueryResponse> getQueryResponses() {
		return queryResponses;
	}
	
	/**
	  * Sets the QueryResponses.
	  * @param queryResponses the QueryResponses to set
	  */
	public void setQueryResponses(List<QueryResponse> queryResponses) {
		this.queryResponses = queryResponses;
	}
	
	/**
	  * Gets the AddressEventCounts.
	  * @return the AddressEventCounts
	  */
	public List<AddressEventCount> getAddressEventCounts() {
		return addressEventCounts;
	}
	
	/**
	  * Sets the AddressEventCounts.
	  * @param addressEventCounts the AddressEventCounts to set
	  */
	public void setAddressEventCounts(List<AddressEventCount> addressEventCounts) {
		this.addressEventCounts = addressEventCounts;
	}
	
	/**
	  * Gets the MalformedMessages.
	  * @return the MalformedMessages
	  */
	public List<MalformedMessage> getMalformedMessages() {
		return malformedMessages;
	}
	
	/**
	  * Sets the MalformedMessages.
	  * @param malformedMessages the MalformedMessages to set
	  */
	public void setMalformedMessages(List<MalformedMessage> malformedMessages) {
		this.malformedMessages = malformedMessages;
	}
}

