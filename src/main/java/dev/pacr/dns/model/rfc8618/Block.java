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
	
	public Block() {
	}
	
	// Getters and Setters
	
	public BlockPreamble getBlockPreamble() {
		return blockPreamble;
	}
	
	public void setBlockPreamble(BlockPreamble blockPreamble) {
		this.blockPreamble = blockPreamble;
	}
	
	public BlockStatistics getBlockStatistics() {
		return blockStatistics;
	}
	
	public void setBlockStatistics(BlockStatistics blockStatistics) {
		this.blockStatistics = blockStatistics;
	}
	
	public BlockTables getBlockTables() {
		return blockTables;
	}
	
	public void setBlockTables(BlockTables blockTables) {
		this.blockTables = blockTables;
	}
	
	public List<QueryResponse> getQueryResponses() {
		return queryResponses;
	}
	
	public void setQueryResponses(List<QueryResponse> queryResponses) {
		this.queryResponses = queryResponses;
	}
	
	public List<AddressEventCount> getAddressEventCounts() {
		return addressEventCounts;
	}
	
	public void setAddressEventCounts(List<AddressEventCount> addressEventCounts) {
		this.addressEventCounts = addressEventCounts;
	}
	
	public List<MalformedMessage> getMalformedMessages() {
		return malformedMessages;
	}
	
	public void setMalformedMessages(List<MalformedMessage> malformedMessages) {
		this.malformedMessages = malformedMessages;
	}
}

