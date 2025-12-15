package dev.pacr.dns.model.rfc8618;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * RFC 8618 C-DNS Storage Parameters
 * <p>
 * Defines storage hints and parameters for C-DNS blocks.
 *
 * @see <a href="https://tools.ietf.org/html/rfc8618">RFC 8618</a>
 * @author Patrick Rafferty
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StorageParameters {
	
	/**
	 * The ticks per second.
	 */
	@JsonProperty("ticks-per-second")
	private Long ticksPerSecond;
	
	/**
	 * The max block items.
	 */
	@JsonProperty("max-block-items")
	private Long maxBlockItems;
	
	/**
	 * The storage hints.
	 */
	@JsonProperty("storage-hints")
	private StorageHints storageHints;
	
	/**
	 * The opcodes.
	 */
	@JsonProperty("opcodes")
	private int[] opcodes;
	
	/**
	 * The rr types.
	 */
	@JsonProperty("rr-types")
	private int[] rrTypes;
	
	// Constructors
	
	/**
	 * Default constructor.
	 */
	public StorageParameters() {
	}
	
	// Getters and Setters
	
	/**
	 * Gets the ticks per second.
	 *
	 * @return the ticksPerSecond
	 */
	public Long getTicksPerSecond() {
		return ticksPerSecond;
	}
	
	/**
	 * Sets the ticks per second.
	 *
	 * @param ticksPerSecond the ticksPerSecond to set
	 */
	public void setTicksPerSecond(Long ticksPerSecond) {
		this.ticksPerSecond = ticksPerSecond;
	}
	
	/**
	 * Gets the max block items.
	 *
	 * @return the maxBlockItems
	 */
	public Long getMaxBlockItems() {
		return maxBlockItems;
	}
	
	/**
	 * Sets the max block items.
	 *
	 * @param maxBlockItems the maxBlockItems to set
	 */
	public void setMaxBlockItems(Long maxBlockItems) {
		this.maxBlockItems = maxBlockItems;
	}
	
	/**
	 * Gets the storage hints.
	 *
	 * @return the storageHints
	 */
	public StorageHints getStorageHints() {
		return storageHints;
	}
	
	/**
	 * Sets the storage hints.
	 *
	 * @param storageHints the storageHints to set
	 */
	public void setStorageHints(StorageHints storageHints) {
		this.storageHints = storageHints;
	}
	
	/**
	 * Gets the opcodes.
	 *
	 * @return the opcodes
	 */
	public int[] getOpcodes() {
		return opcodes;
	}
	
	/**
	 * Sets the opcodes.
	 *
	 * @param opcodes the opcodes to set
	 */
	public void setOpcodes(int[] opcodes) {
		this.opcodes = opcodes;
	}
	
	/**
	 * Gets the rr types.
	 *
	 * @return the rrTypes
	 */
	public int[] getRrTypes() {
		return rrTypes;
	}
	
	/**
	 * Sets the rr types.
	 *
	 * @param rrTypes the rrTypes to set
	 */
	public void setRrTypes(int[] rrTypes) {
		this.rrTypes = rrTypes;
	}
}
