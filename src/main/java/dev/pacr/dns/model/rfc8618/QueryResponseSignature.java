package dev.pacr.dns.model.rfc8618;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
	 * RFC 8618 C-DNS Query Response Signature
 * <p>
 * Signature identifying common properties of query/response pairs.
 *
 * @see <a href="https://tools.ietf.org/html/rfc8618">RFC 8618</a>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
/**
	 * QueryResponseSignature class.
 */
public class QueryResponseSignature {
	
	@JsonProperty("server-address-index")
	private Integer serverAddressIndex;
	
	@JsonProperty("server-port")
	private Integer serverPort;
	
	@JsonProperty("qr-transport-flags")
	private Long qrTransportFlags;
	
	@JsonProperty("qr-type")
	private Integer qrType;
	
	@JsonProperty("qr-sig-flags")
	private Long qrSigFlags;
	
	@JsonProperty("query-opcode")
	private Integer queryOpcode;
	
	@JsonProperty("qr-dns-flags")
	private Integer qrDnsFlags;
	
	@JsonProperty("query-rcode")
	private Integer queryRcode;
	
	@JsonProperty("query-classtype-index")
	private Integer queryClasstypeIndex;
	
	@JsonProperty("query-qd-count")
	private Integer queryQdCount;
	
	@JsonProperty("query-an-count")
	private Integer queryAnCount;
	
	@JsonProperty("query-ns-count")
	private Integer queryNsCount;
	
	@JsonProperty("query-ar-count")
	private Integer queryArCount;
	
	@JsonProperty("edns-version")
	private Integer ednsVersion;
	
	@JsonProperty("udp-buf-size")
	private Integer udpBufSize;
	
	@JsonProperty("opt-rdata-index")
	private Integer optRdataIndex;
	
	@JsonProperty("response-rcode")
	private Integer responseRcode;
	
	// Constructors
	
	/**
	 * Constructs a new QueryResponseSignature.
	 */
	public QueryResponseSignature() {
	}
	
	// Getters and Setters
	
	/**
	 * Gets the ServerAddressIndex.
	 * @return the ServerAddressIndex
	 */
	public Integer getServerAddressIndex() {
		return serverAddressIndex;
	}
	
	/**
	 * Sets the ServerAddressIndex.
	 * @param serverAddressIndex the ServerAddressIndex to set
	 */
	public void setServerAddressIndex(Integer serverAddressIndex) {
		this.serverAddressIndex = serverAddressIndex;
	}
	
	/**
	 * Gets the ServerPort.
	 * @return the ServerPort
	 */
	public Integer getServerPort() {
		return serverPort;
	}
	
	/**
	 * Sets the ServerPort.
	 * @param serverPort the ServerPort to set
	 */
	public void setServerPort(Integer serverPort) {
		this.serverPort = serverPort;
	}
	
	/**
	 * Gets the QrTransportFlags.
	 * @return the QrTransportFlags
	 */
	public Long getQrTransportFlags() {
		return qrTransportFlags;
	}
	
	/**
	 * Sets the QrTransportFlags.
	 * @param qrTransportFlags the QrTransportFlags to set
	 */
	public void setQrTransportFlags(Long qrTransportFlags) {
		this.qrTransportFlags = qrTransportFlags;
	}
	
	/**
	 * Gets the QrType.
	 * @return the QrType
	 */
	public Integer getQrType() {
		return qrType;
	}
	
	/**
	 * Sets the QrType.
	 * @param qrType the QrType to set
	 */
	public void setQrType(Integer qrType) {
		this.qrType = qrType;
	}
	
	/**
	 * Gets the QrSigFlags.
	 * @return the QrSigFlags
	 */
	public Long getQrSigFlags() {
		return qrSigFlags;
	}
	
	/**
	 * Sets the QrSigFlags.
	 * @param qrSigFlags the QrSigFlags to set
	 */
	public void setQrSigFlags(Long qrSigFlags) {
		this.qrSigFlags = qrSigFlags;
	}
	
	/**
	 * Gets the QueryOpcode.
	 * @return the QueryOpcode
	 */
	public Integer getQueryOpcode() {
		return queryOpcode;
	}
	
	/**
	 * Sets the QueryOpcode.
	 * @param queryOpcode the QueryOpcode to set
	 */
	public void setQueryOpcode(Integer queryOpcode) {
		this.queryOpcode = queryOpcode;
	}
	
	/**
	 * Gets the QrDnsFlags.
	 * @return the QrDnsFlags
	 */
	public Integer getQrDnsFlags() {
		return qrDnsFlags;
	}
	
	/**
	 * Sets the QrDnsFlags.
	 * @param qrDnsFlags the QrDnsFlags to set
	 */
	public void setQrDnsFlags(Integer qrDnsFlags) {
		this.qrDnsFlags = qrDnsFlags;
	}
	
	/**
	 * Gets the QueryRcode.
	 * @return the QueryRcode
	 */
	public Integer getQueryRcode() {
		return queryRcode;
	}
	
	/**
	 * Sets the QueryRcode.
	 * @param queryRcode the QueryRcode to set
	 */
	public void setQueryRcode(Integer queryRcode) {
		this.queryRcode = queryRcode;
	}
	
	/**
	 * Gets the QueryClasstypeIndex.
	 * @return the QueryClasstypeIndex
	 */
	public Integer getQueryClasstypeIndex() {
		return queryClasstypeIndex;
	}
	
	/**
	 * Sets the QueryClasstypeIndex.
	 * @param queryClasstypeIndex the QueryClasstypeIndex to set
	 */
	public void setQueryClasstypeIndex(Integer queryClasstypeIndex) {
		this.queryClasstypeIndex = queryClasstypeIndex;
	}
	
	/**
	 * Gets the QueryQdCount.
	 * @return the QueryQdCount
	 */
	public Integer getQueryQdCount() {
		return queryQdCount;
	}
	
	/**
	 * Sets the QueryQdCount.
	 * @param queryQdCount the QueryQdCount to set
	 */
	public void setQueryQdCount(Integer queryQdCount) {
		this.queryQdCount = queryQdCount;
	}
	
	/**
	 * Gets the QueryAnCount.
	 * @return the QueryAnCount
	 */
	public Integer getQueryAnCount() {
		return queryAnCount;
	}
	
	/**
	 * Sets the QueryAnCount.
	 * @param queryAnCount the QueryAnCount to set
	 */
	public void setQueryAnCount(Integer queryAnCount) {
		this.queryAnCount = queryAnCount;
	}
	
	/**
	 * Gets the QueryNsCount.
	 * @return the QueryNsCount
	 */
	public Integer getQueryNsCount() {
		return queryNsCount;
	}
	
	/**
	 * Sets the QueryNsCount.
	 * @param queryNsCount the QueryNsCount to set
	 */
	public void setQueryNsCount(Integer queryNsCount) {
		this.queryNsCount = queryNsCount;
	}
	
	/**
	 * Gets the QueryArCount.
	 * @return the QueryArCount
	 */
	public Integer getQueryArCount() {
		return queryArCount;
	}
	
	/**
	 * Sets the QueryArCount.
	 * @param queryArCount the QueryArCount to set
	 */
	public void setQueryArCount(Integer queryArCount) {
		this.queryArCount = queryArCount;
	}
	
	/**
	 * Gets the EdnsVersion.
	 * @return the EdnsVersion
	 */
	public Integer getEdnsVersion() {
		return ednsVersion;
	}
	
	/**
	 * Sets the EdnsVersion.
	 * @param ednsVersion the EdnsVersion to set
	 */
	public void setEdnsVersion(Integer ednsVersion) {
		this.ednsVersion = ednsVersion;
	}
	
	/**
	 * Gets the UdpBufSize.
	 * @return the UdpBufSize
	 */
	public Integer getUdpBufSize() {
		return udpBufSize;
	}
	
	/**
	 * Sets the UdpBufSize.
	 * @param udpBufSize the UdpBufSize to set
	 */
	public void setUdpBufSize(Integer udpBufSize) {
		this.udpBufSize = udpBufSize;
	}
	
	/**
	 * Gets the OptRdataIndex.
	 * @return the OptRdataIndex
	 */
	public Integer getOptRdataIndex() {
		return optRdataIndex;
	}
	
	/**
	 * Sets the OptRdataIndex.
	 * @param optRdataIndex the OptRdataIndex to set
	 */
	public void setOptRdataIndex(Integer optRdataIndex) {
		this.optRdataIndex = optRdataIndex;
	}
	
	/**
	 * Gets the ResponseRcode.
	 * @return the ResponseRcode
	 */
	public Integer getResponseRcode() {
		return responseRcode;
	}
	
	/**
	 * Sets the ResponseRcode.
	 * @param responseRcode the ResponseRcode to set
	 */
	public void setResponseRcode(Integer responseRcode) {
		this.responseRcode = responseRcode;
	}
}

