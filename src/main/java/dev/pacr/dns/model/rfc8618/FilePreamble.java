package dev.pacr.dns.model.rfc8618;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
	 * RFC 8618 C-DNS File Preamble
 * <p>
 * Contains metadata about the C-DNS file.
 *
 * @see <a href="https://tools.ietf.org/html/rfc8618">RFC 8618</a>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FilePreamble {
	
	@JsonProperty("major-format-version")
	private Integer majorFormatVersion = 1;
	
	@JsonProperty("minor-format-version")
	private Integer minorFormatVersion = 0;
	
	@JsonProperty("private-version")
	private Integer privateVersion;
	
	@JsonProperty("block-parameters")
	private List<BlockParameters> blockParameters;
	
	// Constructors
	
	/**
	 * Constructs a new FilePreamble.
	 */
	public FilePreamble() {
	}
	
	// Getters and Setters
	
	/**
	 * Gets the MajorFormatVersion.
	 * @return the MajorFormatVersion
	 */
	public Integer getMajorFormatVersion() {
		return majorFormatVersion;
	}
	
	/**
	 * Sets the MajorFormatVersion.
	 * @param majorFormatVersion the MajorFormatVersion to set
	 */
	public void setMajorFormatVersion(Integer majorFormatVersion) {
		this.majorFormatVersion = majorFormatVersion;
	}
	
	/**
	 * Gets the MinorFormatVersion.
	 * @return the MinorFormatVersion
	 */
	public Integer getMinorFormatVersion() {
		return minorFormatVersion;
	}
	
	/**
	 * Sets the MinorFormatVersion.
	 * @param minorFormatVersion the MinorFormatVersion to set
	 */
	public void setMinorFormatVersion(Integer minorFormatVersion) {
		this.minorFormatVersion = minorFormatVersion;
	}
	
	/**
	 * Gets the PrivateVersion.
	 * @return the PrivateVersion
	 */
	public Integer getPrivateVersion() {
		return privateVersion;
	}
	
	/**
	 * Sets the PrivateVersion.
	 * @param privateVersion the PrivateVersion to set
	 */
	public void setPrivateVersion(Integer privateVersion) {
		this.privateVersion = privateVersion;
	}
	
	/**
	 * Gets the BlockParameters.
	 * @return the BlockParameters
	 */
	public List<BlockParameters> getBlockParameters() {
		return blockParameters;
	}
	
	/**
	 * Sets the BlockParameters.
	 * @param blockParameters the BlockParameters to set
	 */
	public void setBlockParameters(List<BlockParameters> blockParameters) {
		this.blockParameters = blockParameters;
	}
}

