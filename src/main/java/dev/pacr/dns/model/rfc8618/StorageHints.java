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
	
	public StorageHints() {
	}
	
	// Getters and Setters
	
	public Long getQueryResponseHints() {
		return queryResponseHints;
	}
	
	public void setQueryResponseHints(Long queryResponseHints) {
		this.queryResponseHints = queryResponseHints;
	}
	
	public Long getQueryResponseSignatureHints() {
		return queryResponseSignatureHints;
	}
	
	public void setQueryResponseSignatureHints(Long queryResponseSignatureHints) {
		this.queryResponseSignatureHints = queryResponseSignatureHints;
	}
	
	public Long getRrHints() {
		return rrHints;
	}
	
	public void setRrHints(Long rrHints) {
		this.rrHints = rrHints;
	}
	
	public Long getOtherDataHints() {
		return otherDataHints;
	}
	
	public void setOtherDataHints(Long otherDataHints) {
		this.otherDataHints = otherDataHints;
	}
}

