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
	
	public FilePreamble() {
	}
	
	// Getters and Setters
	
	public Integer getMajorFormatVersion() {
		return majorFormatVersion;
	}
	
	public void setMajorFormatVersion(Integer majorFormatVersion) {
		this.majorFormatVersion = majorFormatVersion;
	}
	
	public Integer getMinorFormatVersion() {
		return minorFormatVersion;
	}
	
	public void setMinorFormatVersion(Integer minorFormatVersion) {
		this.minorFormatVersion = minorFormatVersion;
	}
	
	public Integer getPrivateVersion() {
		return privateVersion;
	}
	
	public void setPrivateVersion(Integer privateVersion) {
		this.privateVersion = privateVersion;
	}
	
	public List<BlockParameters> getBlockParameters() {
		return blockParameters;
	}
	
	public void setBlockParameters(List<BlockParameters> blockParameters) {
		this.blockParameters = blockParameters;
	}
}

