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
	
	public BlockParameters() {
	}
	
	// Getters and Setters
	
	public StorageParameters getStorageParameters() {
		return storageParameters;
	}
	
	public void setStorageParameters(StorageParameters storageParameters) {
		this.storageParameters = storageParameters;
	}
	
	public CollectionParameters getCollectionParameters() {
		return collectionParameters;
	}
	
	public void setCollectionParameters(CollectionParameters collectionParameters) {
		this.collectionParameters = collectionParameters;
	}
}

