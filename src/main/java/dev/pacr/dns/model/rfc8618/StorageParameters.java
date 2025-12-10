package dev.pacr.dns.model.rfc8618;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * RFC 8618 C-DNS Storage Parameters
 * <p>
 * Defines storage hints and parameters for C-DNS blocks.
 *
 * @see <a href="https://tools.ietf.org/html/rfc8618">RFC 8618</a>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StorageParameters {
	
	@JsonProperty("ticks-per-second")
	private Long ticksPerSecond;
	
	@JsonProperty("max-block-items")
	private Long maxBlockItems;
	
	@JsonProperty("storage-hints")
	private StorageHints storageHints;
	
	@JsonProperty("opcodes")
	private int[] opcodes;
	
	@JsonProperty("rr-types")
	private int[] rrTypes;
	
	// Constructors
	
	public StorageParameters() {
	}
	
	// Getters and Setters
	
	public Long getTicksPerSecond() {
		return ticksPerSecond;
	}
	
	public void setTicksPerSecond(Long ticksPerSecond) {
		this.ticksPerSecond = ticksPerSecond;
	}
	
	public Long getMaxBlockItems() {
		return maxBlockItems;
	}
	
	public void setMaxBlockItems(Long maxBlockItems) {
		this.maxBlockItems = maxBlockItems;
	}
	
	public StorageHints getStorageHints() {
		return storageHints;
	}
	
	public void setStorageHints(StorageHints storageHints) {
		this.storageHints = storageHints;
	}
	
	public int[] getOpcodes() {
		return opcodes;
	}
	
	public void setOpcodes(int[] opcodes) {
		this.opcodes = opcodes;
	}
	
	public int[] getRrTypes() {
		return rrTypes;
	}
	
	public void setRrTypes(int[] rrTypes) {
		this.rrTypes = rrTypes;
	}
}

