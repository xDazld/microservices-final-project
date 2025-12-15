package dev.pacr.dns.model.rfc8618;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * RFC 8618 C-DNS Query Response
 * <p>
 * Represents a query/response transaction.
 *
 * @see <a href="https://tools.ietf.org/html/rfc8618">RFC 8618</a>
 * @author Patrick Rafferty
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QueryResponse {
	
	/**
	 * The time offset.
	 */
	@JsonProperty("time-offset")
	private Long timeOffset;
	
	/**
	 * The client address index.
	 */
	@JsonProperty("client-address-index")
	private Integer clientAddressIndex;
	
	/**
	 * The client port.
	 */
	@JsonProperty("client-port")
	private Integer clientPort;
	
	/**
	 * The transaction id.
	 */
	@JsonProperty("transaction-id")
	private Integer transactionId;
	
	/**
	 * The qr signature index.
	 */
	@JsonProperty("qr-signature-index")
	private Integer qrSignatureIndex;
	
	/**
	 * The client hoplimit.
	 */
	@JsonProperty("client-hoplimit")
	private Integer clientHoplimit;
	
	/**
	 * The response delay.
	 */
	@JsonProperty("response-delay")
	private Long responseDelay;
	
	/**
	 * The query name index.
	 */
	@JsonProperty("query-name-index")
	private Integer queryNameIndex;
	
	/**
	 * The query size.
	 */
	@JsonProperty("query-size")
	private Integer querySize;
	
	/**
	 * The response size.
	 */
	@JsonProperty("response-size")
	private Integer responseSize;
	
	/**
	 * The response processing data.
	 */
	@JsonProperty("response-processing-data")
	private ResponseProcessingData responseProcessingData;
	
	/**
	 * The query extended.
	 */
	@JsonProperty("query-extended")
	private QueryResponseExtended queryExtended;
	
	/**
	 * The response extended.
	 */
	@JsonProperty("response-extended")
	private QueryResponseExtended responseExtended;
	
	// Constructors
	
	/**
	 * Default constructor.
	 */
	public QueryResponse() {
	}
	
	// Getters and Setters
	
	/**
	 * Gets the time offset.
	 *
	 * @return the timeOffset
	 */
	public Long getTimeOffset() {
		return timeOffset;
	}
	
	/**
	 * Sets the time offset.
	 *
	 * @param timeOffset the timeOffset to set
	 */
	public void setTimeOffset(Long timeOffset) {
		this.timeOffset = timeOffset;
	}
	
	/**
	 * Gets the client address index.
	 *
	 * @return the clientAddressIndex
	 */
	public Integer getClientAddressIndex() {
		return clientAddressIndex;
	}
	
	/**
	 * Sets the client address index.
	 *
	 * @param clientAddressIndex the clientAddressIndex to set
	 */
	public void setClientAddressIndex(Integer clientAddressIndex) {
		this.clientAddressIndex = clientAddressIndex;
	}
	
	/**
	 * Gets the client port.
	 *
	 * @return the clientPort
	 */
	public Integer getClientPort() {
		return clientPort;
	}
	
	/**
	 * Sets the client port.
	 *
	 * @param clientPort the clientPort to set
	 */
	public void setClientPort(Integer clientPort) {
		this.clientPort = clientPort;
	}
	
	/**
	 * Gets the transaction id.
	 *
	 * @return the transactionId
	 */
	public Integer getTransactionId() {
		return transactionId;
	}
	
	/**
	 * Sets the transaction id.
	 *
	 * @param transactionId the transactionId to set
	 */
	public void setTransactionId(Integer transactionId) {
		this.transactionId = transactionId;
	}
	
	/**
	 * Gets the qr signature index.
	 *
	 * @return the qrSignatureIndex
	 */
	public Integer getQrSignatureIndex() {
		return qrSignatureIndex;
	}
	
	/**
	 * Sets the qr signature index.
	 *
	 * @param qrSignatureIndex the qrSignatureIndex to set
	 */
	public void setQrSignatureIndex(Integer qrSignatureIndex) {
		this.qrSignatureIndex = qrSignatureIndex;
	}
	
	/**
	 * Gets the client hoplimit.
	 *
	 * @return the clientHoplimit
	 */
	public Integer getClientHoplimit() {
		return clientHoplimit;
	}
	
	/**
	 * Sets the client hoplimit.
	 *
	 * @param clientHoplimit the clientHoplimit to set
	 */
	public void setClientHoplimit(Integer clientHoplimit) {
		this.clientHoplimit = clientHoplimit;
	}
	
	/**
	 * Gets the response delay.
	 *
	 * @return the responseDelay
	 */
	public Long getResponseDelay() {
		return responseDelay;
	}
	
	/**
	 * Sets the response delay.
	 *
	 * @param responseDelay the responseDelay to set
	 */
	public void setResponseDelay(Long responseDelay) {
		this.responseDelay = responseDelay;
	}
	
	/**
	 * Gets the query name index.
	 *
	 * @return the queryNameIndex
	 */
	public Integer getQueryNameIndex() {
		return queryNameIndex;
	}
	
	/**
	 * Sets the query name index.
	 *
	 * @param queryNameIndex the queryNameIndex to set
	 */
	public void setQueryNameIndex(Integer queryNameIndex) {
		this.queryNameIndex = queryNameIndex;
	}
	
	/**
	 * Gets the query size.
	 *
	 * @return the querySize
	 */
	public Integer getQuerySize() {
		return querySize;
	}
	
	/**
	 * Sets the query size.
	 *
	 * @param querySize the querySize to set
	 */
	public void setQuerySize(Integer querySize) {
		this.querySize = querySize;
	}
	
	/**
	 * Gets the response size.
	 *
	 * @return the responseSize
	 */
	public Integer getResponseSize() {
		return responseSize;
	}
	
	/**
	 * Sets the response size.
	 *
	 * @param responseSize the responseSize to set
	 */
	public void setResponseSize(Integer responseSize) {
		this.responseSize = responseSize;
	}
	
	/**
	 * Gets the response processing data.
	 *
	 * @return the responseProcessingData
	 */
	public ResponseProcessingData getResponseProcessingData() {
		return responseProcessingData;
	}
	
	/**
	 * Sets the response processing data.
	 *
	 * @param responseProcessingData the responseProcessingData to set
	 */
	public void setResponseProcessingData(ResponseProcessingData responseProcessingData) {
		this.responseProcessingData = responseProcessingData;
	}
	
	/**
	 * Gets the query extended.
	 *
	 * @return the queryExtended
	 */
	public QueryResponseExtended getQueryExtended() {
		return queryExtended;
	}
	
	/**
	 * Sets the query extended.
	 *
	 * @param queryExtended the queryExtended to set
	 */
	public void setQueryExtended(QueryResponseExtended queryExtended) {
		this.queryExtended = queryExtended;
	}
	
	/**
	 * Gets the response extended.
	 *
	 * @return the responseExtended
	 */
	public QueryResponseExtended getResponseExtended() {
		return responseExtended;
	}
	
	/**
	 * Sets the response extended.
	 *
	 * @param responseExtended the responseExtended to set
	 */
	public void setResponseExtended(QueryResponseExtended responseExtended) {
		this.responseExtended = responseExtended;
	}
}
