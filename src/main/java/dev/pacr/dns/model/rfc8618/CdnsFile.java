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
  */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CdnsFile {
	
	@JsonProperty("file-type-id")
	private String fileTypeId = "C-DNS";
	
	@JsonProperty("file-preamble")
	private FilePreamble filePreamble;
	
	@JsonProperty("file-blocks")
	private List<Block> fileBlocks;
	
	// Constructors
	
	/**
	  * Constructs a new CdnsFile.
	  */
	public CdnsFile() {
	}
	
	// Getters and Setters
	
	/**
	  * Gets the FileTypeId.
	  * @return the FileTypeId
	  */
	public String getFileTypeId() {
		return fileTypeId;
	}
	
	/**
	  * Sets the FileTypeId.
	  * @param fileTypeId the FileTypeId to set
	  */
	public void setFileTypeId(String fileTypeId) {
		this.fileTypeId = fileTypeId;
	}
	
	/**
	  * Gets the FilePreamble.
	  * @return the FilePreamble
	  */
	public FilePreamble getFilePreamble() {
		return filePreamble;
	}
	
	/**
	  * Sets the FilePreamble.
	  * @param filePreamble the FilePreamble to set
	  */
	public void setFilePreamble(FilePreamble filePreamble) {
		this.filePreamble = filePreamble;
	}
	
	/**
	  * Gets the FileBlocks.
	  * @return the FileBlocks
	  */
	public List<Block> getFileBlocks() {
		return fileBlocks;
	}
	
	/**
	  * Sets the FileBlocks.
	  * @param fileBlocks the FileBlocks to set
	  */
	public void setFileBlocks(List<Block> fileBlocks) {
		this.fileBlocks = fileBlocks;
	}
}

