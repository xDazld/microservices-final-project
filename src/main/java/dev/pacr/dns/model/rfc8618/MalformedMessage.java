package dev.pacr.dns.model.rfc8618;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
	 * RFC 8618 C-DNS Malformed Message
 * <p>
 * Record of a malformed DNS message.
 *
 * @see <a href="https://tools.ietf.org/html/rfc8618">RFC 8618</a>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MalformedMessage {
	
	@JsonProperty("time-offset")
	private Long timeOffset;
	
	@JsonProperty("client-address-index")
	private Integer clientAddressIndex;
	
	@JsonProperty("client-port")
	private Integer clientPort;
	
	@JsonProperty("message-data-index")
	private Integer messageDataIndex;
	
	// Constructors
	
	/**
	 * Constructs a new MalformedMessage.
	 */
	public MalformedMessage() {
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
	 * Gets the MessageDataIndex.
	 * @return the MessageDataIndex
	 */
	public Integer getMessageDataIndex() {
		return messageDataIndex;
	}
	
	/**
	 * Sets the MessageDataIndex.
	 * @param messageDataIndex the MessageDataIndex to set
	 */
	public void setMessageDataIndex(Integer messageDataIndex) {
		this.messageDataIndex = messageDataIndex;
	}
}

