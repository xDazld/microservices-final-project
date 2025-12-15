package dev.pacr.dns.model.rfc8618;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * RFC 8618 C-DNS Storage Hints
 * <p>
 * Hints about what data is stored in the C-DNS file.
 *
 * @see <a href="https://tools.ietf.org/html/rfc8618">RFC 8618</a>
 * @author Patrick Rafferty
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StorageHints {
	
	/**
	 * The query response hints.
	 */
	@JsonProperty("query-response-hints")
	private Long queryResponseHints;
	
	/**
	 * The query response signature hints.
	 */
	@JsonProperty("query-response-signature-hints")
	private Long queryResponseSignatureHints;
	
	/**
	 * The rr hints.
	 */
	@JsonProperty("rr-hints")
	private Long rrHints;
	
	/**
	 * The other data hints.
	 */
	@JsonProperty("other-data-hints")
	private Long otherDataHints;
	
	// Constructors
	
	/**
	 * Default constructor.
	 */
	public StorageHints() {
	}
	
	// Getters and Setters
	
	/**
	 * Gets the query response hints.
	 *
	 * @return the queryResponseHints
	 */
	public Long getQueryResponseHints() {
		return queryResponseHints;
	}
	
	/**
	 * Sets the query response hints.
	 *
	 * @param queryResponseHints the queryResponseHints to set
	 */
	public void setQueryResponseHints(Long queryResponseHints) {
		this.queryResponseHints = queryResponseHints;
	}
	
	/**
	 * Gets the query response signature hints.
	 *
	 * @return the queryResponseSignatureHints
	 */
	public Long getQueryResponseSignatureHints() {
		return queryResponseSignatureHints;
	}
	
	/**
	 * Sets the query response signature hints.
	 *
	 * @param queryResponseSignatureHints the queryResponseSignatureHints to set
	 */
	public void setQueryResponseSignatureHints(Long queryResponseSignatureHints) {
		this.queryResponseSignatureHints = queryResponseSignatureHints;
	}
	
	/**
	 * Gets the rr hints.
	 *
	 * @return the rrHints
	 */
	public Long getRrHints() {
		return rrHints;
	}
	
	/**
	 * Sets the rr hints.
	 *
	 * @param rrHints the rrHints to set
	 */
	public void setRrHints(Long rrHints) {
		this.rrHints = rrHints;
	}
	
	/**
	 * Gets the other data hints.
	 *
	 * @return the otherDataHints
	 */
	public Long getOtherDataHints() {
		return otherDataHints;
	}
	
	/**
	 * Sets the other data hints.
	 *
	 * @param otherDataHints the otherDataHints to set
	 */
	public void setOtherDataHints(Long otherDataHints) {
		this.otherDataHints = otherDataHints;
	}
}
