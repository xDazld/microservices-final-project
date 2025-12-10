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
	
	public QueryResponseSignature() {
	}
	
	// Getters and Setters
	
	public Integer getServerAddressIndex() {
		return serverAddressIndex;
	}
	
	public void setServerAddressIndex(Integer serverAddressIndex) {
		this.serverAddressIndex = serverAddressIndex;
	}
	
	public Integer getServerPort() {
		return serverPort;
	}
	
	public void setServerPort(Integer serverPort) {
		this.serverPort = serverPort;
	}
	
	public Long getQrTransportFlags() {
		return qrTransportFlags;
	}
	
	public void setQrTransportFlags(Long qrTransportFlags) {
		this.qrTransportFlags = qrTransportFlags;
	}
	
	public Integer getQrType() {
		return qrType;
	}
	
	public void setQrType(Integer qrType) {
		this.qrType = qrType;
	}
	
	public Long getQrSigFlags() {
		return qrSigFlags;
	}
	
	public void setQrSigFlags(Long qrSigFlags) {
		this.qrSigFlags = qrSigFlags;
	}
	
	public Integer getQueryOpcode() {
		return queryOpcode;
	}
	
	public void setQueryOpcode(Integer queryOpcode) {
		this.queryOpcode = queryOpcode;
	}
	
	public Integer getQrDnsFlags() {
		return qrDnsFlags;
	}
	
	public void setQrDnsFlags(Integer qrDnsFlags) {
		this.qrDnsFlags = qrDnsFlags;
	}
	
	public Integer getQueryRcode() {
		return queryRcode;
	}
	
	public void setQueryRcode(Integer queryRcode) {
		this.queryRcode = queryRcode;
	}
	
	public Integer getQueryClasstypeIndex() {
		return queryClasstypeIndex;
	}
	
	public void setQueryClasstypeIndex(Integer queryClasstypeIndex) {
		this.queryClasstypeIndex = queryClasstypeIndex;
	}
	
	public Integer getQueryQdCount() {
		return queryQdCount;
	}
	
	public void setQueryQdCount(Integer queryQdCount) {
		this.queryQdCount = queryQdCount;
	}
	
	public Integer getQueryAnCount() {
		return queryAnCount;
	}
	
	public void setQueryAnCount(Integer queryAnCount) {
		this.queryAnCount = queryAnCount;
	}
	
	public Integer getQueryNsCount() {
		return queryNsCount;
	}
	
	public void setQueryNsCount(Integer queryNsCount) {
		this.queryNsCount = queryNsCount;
	}
	
	public Integer getQueryArCount() {
		return queryArCount;
	}
	
	public void setQueryArCount(Integer queryArCount) {
		this.queryArCount = queryArCount;
	}
	
	public Integer getEdnsVersion() {
		return ednsVersion;
	}
	
	public void setEdnsVersion(Integer ednsVersion) {
		this.ednsVersion = ednsVersion;
	}
	
	public Integer getUdpBufSize() {
		return udpBufSize;
	}
	
	public void setUdpBufSize(Integer udpBufSize) {
		this.udpBufSize = udpBufSize;
	}
	
	public Integer getOptRdataIndex() {
		return optRdataIndex;
	}
	
	public void setOptRdataIndex(Integer optRdataIndex) {
		this.optRdataIndex = optRdataIndex;
	}
	
	public Integer getResponseRcode() {
		return responseRcode;
	}
	
	public void setResponseRcode(Integer responseRcode) {
		this.responseRcode = responseRcode;
	}
}

