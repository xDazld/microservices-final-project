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
/**
	 * StorageParameters class.
 */
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
	
	/**
	 * Constructs a new StorageParameters.
	 */
	public StorageParameters() {
	}
	
	// Getters and Setters
	
	/**
	 * Gets the TicksPerSecond.
	 * @return the TicksPerSecond
	 */
	public Long getTicksPerSecond() {
		return ticksPerSecond;
	}
	
	/**
	 * Sets the TicksPerSecond.
	 * @param ticksPerSecond the TicksPerSecond to set
	 */
	public void setTicksPerSecond(Long ticksPerSecond) {
		this.ticksPerSecond = ticksPerSecond;
	}
	
	/**
	 * Gets the MaxBlockItems.
	 * @return the MaxBlockItems
	 */
	public Long getMaxBlockItems() {
		return maxBlockItems;
	}
	
	/**
	 * Sets the MaxBlockItems.
	 * @param maxBlockItems the MaxBlockItems to set
	 */
	public void setMaxBlockItems(Long maxBlockItems) {
		this.maxBlockItems = maxBlockItems;
	}
	
	/**
	 * Gets the StorageHints.
	 * @return the StorageHints
	 */
	public StorageHints getStorageHints() {
		return storageHints;
	}
	
	/**
	 * Sets the StorageHints.
	 * @param storageHints the StorageHints to set
	 */
	public void setStorageHints(StorageHints storageHints) {
		this.storageHints = storageHints;
	}
	
	/**
	 * Gets the Opcodes.
	 * @return the Opcodes
	 */
	public int[] getOpcodes() {
		return opcodes;
	}
	
	/**
	 * Sets the Opcodes.
	 * @param opcodes the Opcodes to set
	 */
	public void setOpcodes(int[] opcodes) {
		this.opcodes = opcodes;
	}
	
	/**
	 * Gets the RrTypes.
	 * @return the RrTypes
	 */
	public int[] getRrTypes() {
		return rrTypes;
	}
	
	/**
	 * Sets the RrTypes.
	 * @param rrTypes the RrTypes to set
	 */
	public void setRrTypes(int[] rrTypes) {
		this.rrTypes = rrTypes;
	}
}

