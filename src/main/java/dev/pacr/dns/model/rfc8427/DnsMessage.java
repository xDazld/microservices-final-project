package dev.pacr.dns.model.rfc8427;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * RFC 8427 compliant DNS message representation in JSON
 * <p>
 * This represents the complete DNS message format as defined in RFC 8427. All field names match the
 * RFC specification exactly.
 *
 * @see <a href="https://tools.ietf.org/html/rfc8427">RFC 8427</a>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DnsMessage {
	
	@JsonProperty("ID")
	private Integer id;
	
	@JsonProperty("QR")
	private Integer qr;
	
	@JsonProperty("Opcode")
	private Integer opcode;
	
	@JsonProperty("AA")
	private Integer aa;
	
	@JsonProperty("TC")
	private Integer tc;
	
	@JsonProperty("RD")
	private Integer rd;
	
	@JsonProperty("RA")
	private Integer ra;
	
	@JsonProperty("AD")
	private Integer ad;
	
	@JsonProperty("CD")
	private Integer cd;
	
	@JsonProperty("RCODE")
	private Integer rcode;
	
	@JsonProperty("QDCOUNT")
	private Integer qdcount;
	
	@JsonProperty("ANCOUNT")
	private Integer ancount;
	
	@JsonProperty("NSCOUNT")
	private Integer nscount;
	
	@JsonProperty("ARCOUNT")
	private Integer arcount;
	
	@JsonProperty("QNAME")
	private String qname;
	
	@JsonProperty("QTYPE")
	private Integer qtype;
	
	@JsonProperty("QCLASS")
	private Integer qclass;
	
	@JsonProperty("answerRRs")
	private List<ResourceRecord> answerRRs;
	
	@JsonProperty("authorityRRs")
	private List<ResourceRecord> authorityRRs;
	
	@JsonProperty("additionalRRs")
	private List<ResourceRecord> additionalRRs;
	
	// Constructors
	
	public DnsMessage() {
	}
	
	// Getters and Setters
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Integer getQr() {
		return qr;
	}
	
	public void setQr(Integer qr) {
		this.qr = qr;
	}
	
	public Integer getOpcode() {
		return opcode;
	}
	
	public void setOpcode(Integer opcode) {
		this.opcode = opcode;
	}
	
	public Integer getAa() {
		return aa;
	}
	
	public void setAa(Integer aa) {
		this.aa = aa;
	}
	
	public Integer getTc() {
		return tc;
	}
	
	public void setTc(Integer tc) {
		this.tc = tc;
	}
	
	public Integer getRd() {
		return rd;
	}
	
	public void setRd(Integer rd) {
		this.rd = rd;
	}
	
	public Integer getRa() {
		return ra;
	}
	
	public void setRa(Integer ra) {
		this.ra = ra;
	}
	
	public Integer getAd() {
		return ad;
	}
	
	public void setAd(Integer ad) {
		this.ad = ad;
	}
	
	public Integer getCd() {
		return cd;
	}
	
	public void setCd(Integer cd) {
		this.cd = cd;
	}
	
	public Integer getRcode() {
		return rcode;
	}
	
	public void setRcode(Integer rcode) {
		this.rcode = rcode;
	}
	
	public Integer getQdcount() {
		return qdcount;
	}
	
	public void setQdcount(Integer qdcount) {
		this.qdcount = qdcount;
	}
	
	public Integer getAncount() {
		return ancount;
	}
	
	public void setAncount(Integer ancount) {
		this.ancount = ancount;
	}
	
	public Integer getNscount() {
		return nscount;
	}
	
	public void setNscount(Integer nscount) {
		this.nscount = nscount;
	}
	
	public Integer getArcount() {
		return arcount;
	}
	
	public void setArcount(Integer arcount) {
		this.arcount = arcount;
	}
	
	public String getQname() {
		return qname;
	}
	
	public void setQname(String qname) {
		this.qname = qname;
	}
	
	public Integer getQtype() {
		return qtype;
	}
	
	public void setQtype(Integer qtype) {
		this.qtype = qtype;
	}
	
	public Integer getQclass() {
		return qclass;
	}
	
	public void setQclass(Integer qclass) {
		this.qclass = qclass;
	}
	
	public List<ResourceRecord> getAnswerRRs() {
		return answerRRs;
	}
	
	public void setAnswerRRs(List<ResourceRecord> answerRRs) {
		this.answerRRs = answerRRs;
	}
	
	public List<ResourceRecord> getAuthorityRRs() {
		return authorityRRs;
	}
	
	public void setAuthorityRRs(List<ResourceRecord> authorityRRs) {
		this.authorityRRs = authorityRRs;
	}
	
	public List<ResourceRecord> getAdditionalRRs() {
		return additionalRRs;
	}
	
	public void setAdditionalRRs(List<ResourceRecord> additionalRRs) {
		this.additionalRRs = additionalRRs;
	}
}

