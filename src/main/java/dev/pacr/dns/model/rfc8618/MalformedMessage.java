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
	
	public MalformedMessage() {
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
	
	public Integer getMessageDataIndex() {
		return messageDataIndex;
	}
	
	public void setMessageDataIndex(Integer messageDataIndex) {
		this.messageDataIndex = messageDataIndex;
	}
}

