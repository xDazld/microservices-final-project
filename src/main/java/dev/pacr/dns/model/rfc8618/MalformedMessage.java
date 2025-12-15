package dev.pacr.dns.model.rfc8618;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * RFC 8618 C-DNS Malformed Message
 * <p>
 * Record of a malformed DNS message.
 *
 * @see <a href="https://tools.ietf.org/html/rfc8618">RFC 8618</a>
 * @author Patrick Rafferty
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MalformedMessage {
	
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
	 * The message data index.
	 */
	@JsonProperty("message-data-index")
	private Integer messageDataIndex;
	
	// Constructors
	
	/**
	 * Default constructor.
	 */
	public MalformedMessage() {
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
	 * Gets the message data index.
	 *
	 * @return the messageDataIndex
	 */
	public Integer getMessageDataIndex() {
		return messageDataIndex;
	}
	
	/**
	 * Sets the message data index.
	 *
	 * @param messageDataIndex the messageDataIndex to set
	 */
	public void setMessageDataIndex(Integer messageDataIndex) {
		this.messageDataIndex = messageDataIndex;
	}
}
