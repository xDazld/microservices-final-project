package dev.pacr.dns.model.rfc8618;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * RFC 8618 C-DNS (Compacted-DNS) Block
 * <p>
 * Represents the top-level C-DNS file structure as defined in RFC 8618. C-DNS is a compact binary
 * format for DNS data capture and storage.
 *
 * @see <a href="https://tools.ietf.org/html/rfc8618">RFC 8618</a>
 * @author Patrick Rafferty
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CdnsFile {
	
	/**
	 * The file type id.
	 */
	@JsonProperty("file-type-id")
	private String fileTypeId = "C-DNS";
	
	/**
	 * The file preamble.
	 */
	@JsonProperty("file-preamble")
	private FilePreamble filePreamble;
	
	/**
	 * The file blocks.
	 */
	@JsonProperty("file-blocks")
	private List<Block> fileBlocks;
	
	// Constructors
	
	/**
	 * Default constructor.
	 */
	public CdnsFile() {
	}
	
	// Getters and Setters
	
	/**
	 * Gets the file type id.
	 *
	 * @return the fileTypeId
	 */
	public String getFileTypeId() {
		return fileTypeId;
	}
	
	/**
	 * Sets the file type id.
	 *
	 * @param fileTypeId the fileTypeId to set
	 */
	public void setFileTypeId(String fileTypeId) {
		this.fileTypeId = fileTypeId;
	}
	
	/**
	 * Gets the file preamble.
	 *
	 * @return the filePreamble
	 */
	public FilePreamble getFilePreamble() {
		return filePreamble;
	}
	
	/**
	 * Sets the file preamble.
	 *
	 * @param filePreamble the filePreamble to set
	 */
	public void setFilePreamble(FilePreamble filePreamble) {
		this.filePreamble = filePreamble;
	}
	
	/**
	 * Gets the file blocks.
	 *
	 * @return the fileBlocks
	 */
	public List<Block> getFileBlocks() {
		return fileBlocks;
	}
	
	/**
	 * Sets the file blocks.
	 *
	 * @param fileBlocks the fileBlocks to set
	 */
	public void setFileBlocks(List<Block> fileBlocks) {
		this.fileBlocks = fileBlocks;
	}
}
