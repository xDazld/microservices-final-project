package dev.pacr.dns.model.rfc8618;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * RFC 8618 C-DNS Query Response Signature
 * <p>
 * Signature identifying common properties of query/response pairs.
 *
 * @see <a href="https://tools.ietf.org/html/rfc8618">RFC 8618</a>
 * @author Patrick Rafferty
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QueryResponseSignature {
	
	/**
	 * The server address index.
	 */
	@JsonProperty("server-address-index")
	private Integer serverAddressIndex;
	
	/**
	 * The server port.
	 */
	@JsonProperty("server-port")
	private Integer serverPort;
	
	/**
	 * The qr transport flags.
	 */
	@JsonProperty("qr-transport-flags")
	private Long qrTransportFlags;
	
	/**
	 * The qr type.
	 */
	@JsonProperty("qr-type")
	private Integer qrType;
	
	/**
	 * The qr sig flags.
	 */
	@JsonProperty("qr-sig-flags")
	private Long qrSigFlags;
	
	/**
	 * The query opcode.
	 */
	@JsonProperty("query-opcode")
	private Integer queryOpcode;
	
	/**
	 * The qr dns flags.
	 */
	@JsonProperty("qr-dns-flags")
	private Integer qrDnsFlags;
	
	/**
	 * The query rcode.
	 */
	@JsonProperty("query-rcode")
	private Integer queryRcode;
	
	/**
	 * The query classtype index.
	 */
	@JsonProperty("query-classtype-index")
	private Integer queryClasstypeIndex;
	
	/**
	 * The query qd count.
	 */
	@JsonProperty("query-qd-count")
	private Integer queryQdCount;
	
	/**
	 * The query an count.
	 */
	@JsonProperty("query-an-count")
	private Integer queryAnCount;
	
	/**
	 * The query ns count.
	 */
	@JsonProperty("query-ns-count")
	private Integer queryNsCount;
	
	/**
	 * The query ar count.
	 */
	@JsonProperty("query-ar-count")
	private Integer queryArCount;
	
	/**
	 * The edns version.
	 */
	@JsonProperty("edns-version")
	private Integer ednsVersion;
	
	/**
	 * The udp buf size.
	 */
	@JsonProperty("udp-buf-size")
	private Integer udpBufSize;
	
	/**
	 * The opt rdata index.
	 */
	@JsonProperty("opt-rdata-index")
	private Integer optRdataIndex;
	
	/**
	 * The response rcode.
	 */
	@JsonProperty("response-rcode")
	private Integer responseRcode;
	
	// Constructors
	
	/**
	 * Default constructor.
	 */
	public QueryResponseSignature() {
	}
	
	// Getters and Setters
	
	/**
	 * Gets the server address index.
	 *
	 * @return the serverAddressIndex
	 */
	public Integer getServerAddressIndex() {
		return serverAddressIndex;
	}
	
	/**
	 * Sets the server address index.
	 *
	 * @param serverAddressIndex the serverAddressIndex to set
	 */
	public void setServerAddressIndex(Integer serverAddressIndex) {
		this.serverAddressIndex = serverAddressIndex;
	}
	
	/**
	 * Gets the server port.
	 *
	 * @return the serverPort
	 */
	public Integer getServerPort() {
		return serverPort;
	}
	
	/**
	 * Sets the server port.
	 *
	 * @param serverPort the serverPort to set
	 */
	public void setServerPort(Integer serverPort) {
		this.serverPort = serverPort;
	}
	
	/**
	 * Gets the qr transport flags.
	 *
	 * @return the qrTransportFlags
	 */
	public Long getQrTransportFlags() {
		return qrTransportFlags;
	}
	
	/**
	 * Sets the qr transport flags.
	 *
	 * @param qrTransportFlags the qrTransportFlags to set
	 */
	public void setQrTransportFlags(Long qrTransportFlags) {
		this.qrTransportFlags = qrTransportFlags;
	}
	
	/**
	 * Gets the qr type.
	 *
	 * @return the qrType
	 */
	public Integer getQrType() {
		return qrType;
	}
	
	/**
	 * Sets the qr type.
	 *
	 * @param qrType the qrType to set
	 */
	public void setQrType(Integer qrType) {
		this.qrType = qrType;
	}
	
	/**
	 * Gets the qr sig flags.
	 *
	 * @return the qrSigFlags
	 */
	public Long getQrSigFlags() {
		return qrSigFlags;
	}
	
	/**
	 * Sets the qr sig flags.
	 *
	 * @param qrSigFlags the qrSigFlags to set
	 */
	public void setQrSigFlags(Long qrSigFlags) {
		this.qrSigFlags = qrSigFlags;
	}
	
	/**
	 * Gets the query opcode.
	 *
	 * @return the queryOpcode
	 */
	public Integer getQueryOpcode() {
		return queryOpcode;
	}
	
	/**
	 * Sets the query opcode.
	 *
	 * @param queryOpcode the queryOpcode to set
	 */
	public void setQueryOpcode(Integer queryOpcode) {
		this.queryOpcode = queryOpcode;
	}
	
	/**
	 * Gets the qr dns flags.
	 *
	 * @return the qrDnsFlags
	 */
	public Integer getQrDnsFlags() {
		return qrDnsFlags;
	}
	
	/**
	 * Sets the qr dns flags.
	 *
	 * @param qrDnsFlags the qrDnsFlags to set
	 */
	public void setQrDnsFlags(Integer qrDnsFlags) {
		this.qrDnsFlags = qrDnsFlags;
	}
	
	/**
	 * Gets the query rcode.
	 *
	 * @return the queryRcode
	 */
	public Integer getQueryRcode() {
		return queryRcode;
	}
	
	/**
	 * Sets the query rcode.
	 *
	 * @param queryRcode the queryRcode to set
	 */
	public void setQueryRcode(Integer queryRcode) {
		this.queryRcode = queryRcode;
	}
	
	/**
	 * Gets the query classtype index.
	 *
	 * @return the queryClasstypeIndex
	 */
	public Integer getQueryClasstypeIndex() {
		return queryClasstypeIndex;
	}
	
	/**
	 * Sets the query classtype index.
	 *
	 * @param queryClasstypeIndex the queryClasstypeIndex to set
	 */
	public void setQueryClasstypeIndex(Integer queryClasstypeIndex) {
		this.queryClasstypeIndex = queryClasstypeIndex;
	}
	
	/**
	 * Gets the query qd count.
	 *
	 * @return the queryQdCount
	 */
	public Integer getQueryQdCount() {
		return queryQdCount;
	}
	
	/**
	 * Sets the query qd count.
	 *
	 * @param queryQdCount the queryQdCount to set
	 */
	public void setQueryQdCount(Integer queryQdCount) {
		this.queryQdCount = queryQdCount;
	}
	
	/**
	 * Gets the query an count.
	 *
	 * @return the queryAnCount
	 */
	public Integer getQueryAnCount() {
		return queryAnCount;
	}
	
	/**
	 * Sets the query an count.
	 *
	 * @param queryAnCount the queryAnCount to set
	 */
	public void setQueryAnCount(Integer queryAnCount) {
		this.queryAnCount = queryAnCount;
	}
	
	/**
	 * Gets the query ns count.
	 *
	 * @return the queryNsCount
	 */
	public Integer getQueryNsCount() {
		return queryNsCount;
	}
	
	/**
	 * Sets the query ns count.
	 *
	 * @param queryNsCount the queryNsCount to set
	 */
	public void setQueryNsCount(Integer queryNsCount) {
		this.queryNsCount = queryNsCount;
	}
	
	/**
	 * Gets the query ar count.
	 *
	 * @return the queryArCount
	 */
	public Integer getQueryArCount() {
		return queryArCount;
	}
	
	/**
	 * Sets the query ar count.
	 *
	 * @param queryArCount the queryArCount to set
	 */
	public void setQueryArCount(Integer queryArCount) {
		this.queryArCount = queryArCount;
	}
	
	/**
	 * Gets the edns version.
	 *
	 * @return the ednsVersion
	 */
	public Integer getEdnsVersion() {
		return ednsVersion;
	}
	
	/**
	 * Sets the edns version.
	 *
	 * @param ednsVersion the ednsVersion to set
	 */
	public void setEdnsVersion(Integer ednsVersion) {
		this.ednsVersion = ednsVersion;
	}
	
	/**
	 * Gets the udp buf size.
	 *
	 * @return the udpBufSize
	 */
	public Integer getUdpBufSize() {
		return udpBufSize;
	}
	
	/**
	 * Sets the udp buf size.
	 *
	 * @param udpBufSize the udpBufSize to set
	 */
	public void setUdpBufSize(Integer udpBufSize) {
		this.udpBufSize = udpBufSize;
	}
	
	/**
	 * Gets the opt rdata index.
	 *
	 * @return the optRdataIndex
	 */
	public Integer getOptRdataIndex() {
		return optRdataIndex;
	}
	
	/**
	 * Sets the opt rdata index.
	 *
	 * @param optRdataIndex the optRdataIndex to set
	 */
	public void setOptRdataIndex(Integer optRdataIndex) {
		this.optRdataIndex = optRdataIndex;
	}
	
	/**
	 * Gets the response rcode.
	 *
	 * @return the responseRcode
	 */
	public Integer getResponseRcode() {
		return responseRcode;
	}
	
	/**
	 * Sets the response rcode.
	 *
	 * @param responseRcode the responseRcode to set
	 */
	public void setResponseRcode(Integer responseRcode) {
		this.responseRcode = responseRcode;
	}
}
