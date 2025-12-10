package dev.pacr.dns.model.rfc8618;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * RFC 8618 C-DNS Query Response
 * <p>
 * Represents a query/response transaction.
 *
 * @see <a href="https://tools.ietf.org/html/rfc8618">RFC 8618</a>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QueryResponse {
	
	@JsonProperty("time-offset")
	private Long timeOffset;
	
	@JsonProperty("client-address-index")
	private Integer clientAddressIndex;
	
	@JsonProperty("client-port")
	private Integer clientPort;
	
	@JsonProperty("transaction-id")
	private Integer transactionId;
	
	@JsonProperty("qr-signature-index")
	private Integer qrSignatureIndex;
	
	@JsonProperty("client-hoplimit")
	private Integer clientHoplimit;
	
	@JsonProperty("response-delay")
	private Long responseDelay;
	
	@JsonProperty("query-name-index")
	private Integer queryNameIndex;
	
	@JsonProperty("query-size")
	private Integer querySize;
	
	@JsonProperty("response-size")
	private Integer responseSize;
	
	@JsonProperty("response-processing-data")
	private ResponseProcessingData responseProcessingData;
	
	@JsonProperty("query-extended")
	private QueryResponseExtended queryExtended;
	
	@JsonProperty("response-extended")
	private QueryResponseExtended responseExtended;
	
	// Constructors
	
	public QueryResponse() {
	}
	
	// Getters and Setters
	
	public Long getTimeOffset() {
		return timeOffset;
	}
	
	public void setTimeOffset(Long timeOffset) {
		this.timeOffset = timeOffset;
	}
	
	public Integer getClientAddressIndex() {
		return clientAddressIndex;
	}
	
	public void setClientAddressIndex(Integer clientAddressIndex) {
		this.clientAddressIndex = clientAddressIndex;
	}
	
	public Integer getClientPort() {
		return clientPort;
	}
	
	public void setClientPort(Integer clientPort) {
		this.clientPort = clientPort;
	}
	
	public Integer getTransactionId() {
		return transactionId;
	}
	
	public void setTransactionId(Integer transactionId) {
		this.transactionId = transactionId;
	}
	
	public Integer getQrSignatureIndex() {
		return qrSignatureIndex;
	}
	
	public void setQrSignatureIndex(Integer qrSignatureIndex) {
		this.qrSignatureIndex = qrSignatureIndex;
	}
	
	public Integer getClientHoplimit() {
		return clientHoplimit;
	}
	
	public void setClientHoplimit(Integer clientHoplimit) {
		this.clientHoplimit = clientHoplimit;
	}
	
	public Long getResponseDelay() {
		return responseDelay;
	}
	
	public void setResponseDelay(Long responseDelay) {
		this.responseDelay = responseDelay;
	}
	
	public Integer getQueryNameIndex() {
		return queryNameIndex;
	}
	
	public void setQueryNameIndex(Integer queryNameIndex) {
		this.queryNameIndex = queryNameIndex;
	}
	
	public Integer getQuerySize() {
		return querySize;
	}
	
	public void setQuerySize(Integer querySize) {
		this.querySize = querySize;
	}
	
	public Integer getResponseSize() {
		return responseSize;
	}
	
	public void setResponseSize(Integer responseSize) {
		this.responseSize = responseSize;
	}
	
	public ResponseProcessingData getResponseProcessingData() {
		return responseProcessingData;
	}
	
	public void setResponseProcessingData(ResponseProcessingData responseProcessingData) {
		this.responseProcessingData = responseProcessingData;
	}
	
	public QueryResponseExtended getQueryExtended() {
		return queryExtended;
	}
	
	public void setQueryExtended(QueryResponseExtended queryExtended) {
		this.queryExtended = queryExtended;
	}
	
	public QueryResponseExtended getResponseExtended() {
		return responseExtended;
	}
	
	public void setResponseExtended(QueryResponseExtended responseExtended) {
		this.responseExtended = responseExtended;
	}
}

