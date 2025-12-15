package dev.pacr.dns.model.rfc8618;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * RFC 8618 C-DNS Block Parameters
 * <p>
 * Defines parameters for data blocks in the C-DNS file.
 *
 * @see <a href="https://tools.ietf.org/html/rfc8618">RFC 8618</a>
 * @author Patrick Rafferty
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BlockParameters {
	
	/**
	 * The storage parameters.
	 */
	@JsonProperty("storage-parameters")
	private StorageParameters storageParameters;
	
	/**
	 * The collection parameters.
	 */
	@JsonProperty("collection-parameters")
	private CollectionParameters collectionParameters;
	
	// Constructors
	
	/**
	 * Default constructor.
	 */
	public BlockParameters() {
	}
	
	// Getters and Setters
	
	/**
	 * Gets the storage parameters.
	 *
	 * @return the storageParameters
	 */
	public StorageParameters getStorageParameters() {
		return storageParameters;
	}
	
	/**
	 * Sets the storage parameters.
	 *
	 * @param storageParameters the storageParameters to set
	 */
	public void setStorageParameters(StorageParameters storageParameters) {
		this.storageParameters = storageParameters;
	}
	
	/**
	 * Gets the collection parameters.
	 *
	 * @return the collectionParameters
	 */
	public CollectionParameters getCollectionParameters() {
		return collectionParameters;
	}
	
	/**
	 * Sets the collection parameters.
	 *
	 * @param collectionParameters the collectionParameters to set
	 */
	public void setCollectionParameters(CollectionParameters collectionParameters) {
		this.collectionParameters = collectionParameters;
	}
}
