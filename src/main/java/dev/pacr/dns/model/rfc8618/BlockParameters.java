package dev.pacr.dns.model.rfc8618;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
	  * RFC 8618 C-DNS Block Parameters
  * <p>
  * Defines parameters for data blocks in the C-DNS file.
  *
  * @see <a href="https://tools.ietf.org/html/rfc8618">RFC 8618</a>
  */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BlockParameters {
	
	@JsonProperty("storage-parameters")
	private StorageParameters storageParameters;
	
	@JsonProperty("collection-parameters")
	private CollectionParameters collectionParameters;
	
	// Constructors
	
	/**
	  * Constructs a new BlockParameters.
	  */
	public BlockParameters() {
	}
	
	// Getters and Setters
	
	/**
	  * Gets the StorageParameters.
	  * @return the StorageParameters
	  */
	public StorageParameters getStorageParameters() {
		return storageParameters;
	}
	
	/**
	  * Sets the StorageParameters.
	  * @param storageParameters the StorageParameters to set
	  */
	public void setStorageParameters(StorageParameters storageParameters) {
		this.storageParameters = storageParameters;
	}
	
	/**
	  * Gets the CollectionParameters.
	  * @return the CollectionParameters
	  */
	public CollectionParameters getCollectionParameters() {
		return collectionParameters;
	}
	
	/**
	  * Sets the CollectionParameters.
	  * @param collectionParameters the CollectionParameters to set
	  */
	public void setCollectionParameters(CollectionParameters collectionParameters) {
		this.collectionParameters = collectionParameters;
	}
}

