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
 * @author Patrick Rafferty
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FilePreamble {
	
	/**
	 * The major format version.
	 */
	@JsonProperty("major-format-version")
	private Integer majorFormatVersion = 1;
	
	/**
	 * The minor format version.
	 */
	@JsonProperty("minor-format-version")
	private Integer minorFormatVersion = 0;
	
	/**
	 * The private version.
	 */
	@JsonProperty("private-version")
	private Integer privateVersion;
	
	/**
	 * The block parameters.
	 */
	@JsonProperty("block-parameters")
	private List<BlockParameters> blockParameters;
	
	// Constructors
	
	/**
	 * Default constructor.
	 */
	public FilePreamble() {
	}
	
	// Getters and Setters
	
	/**
	 * Gets the major format version.
	 *
	 * @return the majorFormatVersion
	 */
	public Integer getMajorFormatVersion() {
		return majorFormatVersion;
	}
	
	/**
	 * Sets the major format version.
	 *
	 * @param majorFormatVersion the majorFormatVersion to set
	 */
	public void setMajorFormatVersion(Integer majorFormatVersion) {
		this.majorFormatVersion = majorFormatVersion;
	}
	
	/**
	 * Gets the minor format version.
	 *
	 * @return the minorFormatVersion
	 */
	public Integer getMinorFormatVersion() {
		return minorFormatVersion;
	}
	
	/**
	 * Sets the minor format version.
	 *
	 * @param minorFormatVersion the minorFormatVersion to set
	 */
	public void setMinorFormatVersion(Integer minorFormatVersion) {
		this.minorFormatVersion = minorFormatVersion;
	}
	
	/**
	 * Gets the private version.
	 *
	 * @return the privateVersion
	 */
	public Integer getPrivateVersion() {
		return privateVersion;
	}
	
	/**
	 * Sets the private version.
	 *
	 * @param privateVersion the privateVersion to set
	 */
	public void setPrivateVersion(Integer privateVersion) {
		this.privateVersion = privateVersion;
	}
	
	/**
	 * Gets the block parameters.
	 *
	 * @return the blockParameters
	 */
	public List<BlockParameters> getBlockParameters() {
		return blockParameters;
	}
	
	/**
	 * Sets the block parameters.
	 *
	 * @param blockParameters the blockParameters to set
	 */
	public void setBlockParameters(List<BlockParameters> blockParameters) {
		this.blockParameters = blockParameters;
	}
}
