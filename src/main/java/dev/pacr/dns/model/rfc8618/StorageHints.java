package dev.pacr.dns.model.rfc8618;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
	 * RFC 8618 C-DNS Storage Hints
 * <p>
 * Hints about what data is stored in the C-DNS file.
 *
 * @see <a href="https://tools.ietf.org/html/rfc8618">RFC 8618</a>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
/**
	 * StorageHints class.
 */
public class StorageHints {
	
	@JsonProperty("query-response-hints")
	private Long queryResponseHints;
	
	@JsonProperty("query-response-signature-hints")
	private Long queryResponseSignatureHints;
	
	@JsonProperty("rr-hints")
	private Long rrHints;
	
	@JsonProperty("other-data-hints")
	private Long otherDataHints;
	
	// Constructors
	
	/**
	 * Constructs a new StorageHints.
	 */
	public StorageHints() {
	}
	
	// Getters and Setters
	
	/**
	 * Gets the QueryResponseHints.
	 * @return the QueryResponseHints
	 */
	public Long getQueryResponseHints() {
		return queryResponseHints;
	}
	
	/**
	 * Sets the QueryResponseHints.
	 * @param queryResponseHints the QueryResponseHints to set
	 */
	public void setQueryResponseHints(Long queryResponseHints) {
		this.queryResponseHints = queryResponseHints;
	}
	
	/**
	 * Gets the QueryResponseSignatureHints.
	 * @return the QueryResponseSignatureHints
	 */
	public Long getQueryResponseSignatureHints() {
		return queryResponseSignatureHints;
	}
	
	/**
	 * Sets the QueryResponseSignatureHints.
	 * @param queryResponseSignatureHints the QueryResponseSignatureHints to set
	 */
	public void setQueryResponseSignatureHints(Long queryResponseSignatureHints) {
		this.queryResponseSignatureHints = queryResponseSignatureHints;
	}
	
	/**
	 * Gets the RrHints.
	 * @return the RrHints
	 */
	public Long getRrHints() {
		return rrHints;
	}
	
	/**
	 * Sets the RrHints.
	 * @param rrHints the RrHints to set
	 */
	public void setRrHints(Long rrHints) {
		this.rrHints = rrHints;
	}
	
	/**
	 * Gets the OtherDataHints.
	 * @return the OtherDataHints
	 */
	public Long getOtherDataHints() {
		return otherDataHints;
	}
	
	/**
	 * Sets the OtherDataHints.
	 * @param otherDataHints the OtherDataHints to set
	 */
	public void setOtherDataHints(Long otherDataHints) {
		this.otherDataHints = otherDataHints;
	}
}

