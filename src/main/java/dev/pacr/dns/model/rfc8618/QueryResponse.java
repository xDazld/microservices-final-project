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
	
	/**
	  * Constructs a new QueryResponse.
	  */
	public QueryResponse() {
	}
	
	// Getters and Setters
	
	/**
	  * Gets the TimeOffset.
	  * @return the TimeOffset
	  */
	public Long getTimeOffset() {
		return timeOffset;
	}
	
	/**
	  * Sets the TimeOffset.
	  * @param timeOffset the TimeOffset to set
	  */
	public void setTimeOffset(Long timeOffset) {
		this.timeOffset = timeOffset;
	}
	
	/**
	  * Gets the ClientAddressIndex.
	  * @return the ClientAddressIndex
	  */
	public Integer getClientAddressIndex() {
		return clientAddressIndex;
	}
	
	/**
	  * Sets the ClientAddressIndex.
	  * @param clientAddressIndex the ClientAddressIndex to set
	  */
	public void setClientAddressIndex(Integer clientAddressIndex) {
		this.clientAddressIndex = clientAddressIndex;
	}
	
	/**
	  * Gets the ClientPort.
	  * @return the ClientPort
	  */
	public Integer getClientPort() {
		return clientPort;
	}
	
	/**
	  * Sets the ClientPort.
	  * @param clientPort the ClientPort to set
	  */
	public void setClientPort(Integer clientPort) {
		this.clientPort = clientPort;
	}
	
	/**
	  * Gets the TransactionId.
	  * @return the TransactionId
	  */
	public Integer getTransactionId() {
		return transactionId;
	}
	
	/**
	  * Sets the TransactionId.
	  * @param transactionId the TransactionId to set
	  */
	public void setTransactionId(Integer transactionId) {
		this.transactionId = transactionId;
	}
	
	/**
	  * Gets the QrSignatureIndex.
	  * @return the QrSignatureIndex
	  */
	public Integer getQrSignatureIndex() {
		return qrSignatureIndex;
	}
	
	/**
	  * Sets the QrSignatureIndex.
	  * @param qrSignatureIndex the QrSignatureIndex to set
	  */
	public void setQrSignatureIndex(Integer qrSignatureIndex) {
		this.qrSignatureIndex = qrSignatureIndex;
	}
	
	/**
	  * Gets the ClientHoplimit.
	  * @return the ClientHoplimit
	  */
	public Integer getClientHoplimit() {
		return clientHoplimit;
	}
	
	/**
	  * Sets the ClientHoplimit.
	  * @param clientHoplimit the ClientHoplimit to set
	  */
	public void setClientHoplimit(Integer clientHoplimit) {
		this.clientHoplimit = clientHoplimit;
	}
	
	/**
	  * Gets the ResponseDelay.
	  * @return the ResponseDelay
	  */
	public Long getResponseDelay() {
		return responseDelay;
	}
	
	/**
	  * Sets the ResponseDelay.
	  * @param responseDelay the ResponseDelay to set
	  */
	public void setResponseDelay(Long responseDelay) {
		this.responseDelay = responseDelay;
	}
	
	/**
	  * Gets the QueryNameIndex.
	  * @return the QueryNameIndex
	  */
	public Integer getQueryNameIndex() {
		return queryNameIndex;
	}
	
	/**
	  * Sets the QueryNameIndex.
	  * @param queryNameIndex the QueryNameIndex to set
	  */
	public void setQueryNameIndex(Integer queryNameIndex) {
		this.queryNameIndex = queryNameIndex;
	}
	
	/**
	  * Gets the QuerySize.
	  * @return the QuerySize
	  */
	public Integer getQuerySize() {
		return querySize;
	}
	
	/**
	  * Sets the QuerySize.
	  * @param querySize the QuerySize to set
	  */
	public void setQuerySize(Integer querySize) {
		this.querySize = querySize;
	}
	
	/**
	  * Gets the ResponseSize.
	  * @return the ResponseSize
	  */
	public Integer getResponseSize() {
		return responseSize;
	}
	
	/**
	  * Sets the ResponseSize.
	  * @param responseSize the ResponseSize to set
	  */
	public void setResponseSize(Integer responseSize) {
		this.responseSize = responseSize;
	}
	
	/**
	  * Gets the ResponseProcessingData.
	  * @return the ResponseProcessingData
	  */
	public ResponseProcessingData getResponseProcessingData() {
		return responseProcessingData;
	}
	
	/**
	  * Sets the ResponseProcessingData.
	  * @param responseProcessingData the ResponseProcessingData to set
	  */
	public void setResponseProcessingData(ResponseProcessingData responseProcessingData) {
		this.responseProcessingData = responseProcessingData;
	}
	
	/**
	  * Gets the QueryExtended.
	  * @return the QueryExtended
	  */
	public QueryResponseExtended getQueryExtended() {
		return queryExtended;
	}
	
	/**
	  * Sets the QueryExtended.
	  * @param queryExtended the QueryExtended to set
	  */
	public void setQueryExtended(QueryResponseExtended queryExtended) {
		this.queryExtended = queryExtended;
	}
	
	/**
	  * Gets the ResponseExtended.
	  * @return the ResponseExtended
	  */
	public QueryResponseExtended getResponseExtended() {
		return responseExtended;
	}
	
	/**
	  * Sets the ResponseExtended.
	  * @param responseExtended the ResponseExtended to set
	  */
	public void setResponseExtended(QueryResponseExtended responseExtended) {
		this.responseExtended = responseExtended;
	}
}

