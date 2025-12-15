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
	
	/**
	  * Constructs a new DnsMessage.
	  */
	public DnsMessage() {
	}
	
	// Getters and Setters
	
	/**
	  * Gets the Id.
	  * @return the Id
	  */
	public Integer getId() {
		return id;
	}
	
	/**
	  * Sets the Id.
	  * @param id the Id to set
	  */
	public void setId(Integer id) {
		this.id = id;
	}
	
	/**
	  * Gets the Qr.
	  * @return the Qr
	  */
	public Integer getQr() {
		return qr;
	}
	
	/**
	  * Sets the Qr.
	  * @param qr the Qr to set
	  */
	public void setQr(Integer qr) {
		this.qr = qr;
	}
	
	/**
	  * Gets the Opcode.
	  * @return the Opcode
	  */
	public Integer getOpcode() {
		return opcode;
	}
	
	/**
	  * Sets the Opcode.
	  * @param opcode the Opcode to set
	  */
	public void setOpcode(Integer opcode) {
		this.opcode = opcode;
	}
	
	/**
	  * Gets the Aa.
	  * @return the Aa
	  */
	public Integer getAa() {
		return aa;
	}
	
	/**
	  * Sets the Aa.
	  * @param aa the Aa to set
	  */
	public void setAa(Integer aa) {
		this.aa = aa;
	}
	
	/**
	  * Gets the Tc.
	  * @return the Tc
	  */
	public Integer getTc() {
		return tc;
	}
	
	/**
	  * Sets the Tc.
	  * @param tc the Tc to set
	  */
	public void setTc(Integer tc) {
		this.tc = tc;
	}
	
	/**
	  * Gets the Rd.
	  * @return the Rd
	  */
	public Integer getRd() {
		return rd;
	}
	
	/**
	  * Sets the Rd.
	  * @param rd the Rd to set
	  */
	public void setRd(Integer rd) {
		this.rd = rd;
	}
	
	/**
	  * Gets the Ra.
	  * @return the Ra
	  */
	public Integer getRa() {
		return ra;
	}
	
	/**
	  * Sets the Ra.
	  * @param ra the Ra to set
	  */
	public void setRa(Integer ra) {
		this.ra = ra;
	}
	
	/**
	  * Gets the Ad.
	  * @return the Ad
	  */
	public Integer getAd() {
		return ad;
	}
	
	/**
	  * Sets the Ad.
	  * @param ad the Ad to set
	  */
	public void setAd(Integer ad) {
		this.ad = ad;
	}
	
	/**
	  * Gets the Cd.
	  * @return the Cd
	  */
	public Integer getCd() {
		return cd;
	}
	
	/**
	  * Sets the Cd.
	  * @param cd the Cd to set
	  */
	public void setCd(Integer cd) {
		this.cd = cd;
	}
	
	/**
	  * Gets the Rcode.
	  * @return the Rcode
	  */
	public Integer getRcode() {
		return rcode;
	}
	
	/**
	  * Sets the Rcode.
	  * @param rcode the Rcode to set
	  */
	public void setRcode(Integer rcode) {
		this.rcode = rcode;
	}
	
	/**
	  * Gets the Qdcount.
	  * @return the Qdcount
	  */
	public Integer getQdcount() {
		return qdcount;
	}
	
	/**
	  * Sets the Qdcount.
	  * @param qdcount the Qdcount to set
	  */
	public void setQdcount(Integer qdcount) {
		this.qdcount = qdcount;
	}
	
	/**
	  * Gets the Ancount.
	  * @return the Ancount
	  */
	public Integer getAncount() {
		return ancount;
	}
	
	/**
	  * Sets the Ancount.
	  * @param ancount the Ancount to set
	  */
	public void setAncount(Integer ancount) {
		this.ancount = ancount;
	}
	
	/**
	  * Gets the Nscount.
	  * @return the Nscount
	  */
	public Integer getNscount() {
		return nscount;
	}
	
	/**
	  * Sets the Nscount.
	  * @param nscount the Nscount to set
	  */
	public void setNscount(Integer nscount) {
		this.nscount = nscount;
	}
	
	/**
	  * Gets the Arcount.
	  * @return the Arcount
	  */
	public Integer getArcount() {
		return arcount;
	}
	
	/**
	  * Sets the Arcount.
	  * @param arcount the Arcount to set
	  */
	public void setArcount(Integer arcount) {
		this.arcount = arcount;
	}
	
	/**
	  * Gets the Qname.
	  * @return the Qname
	  */
	public String getQname() {
		return qname;
	}
	
	/**
	  * Sets the Qname.
	  * @param qname the Qname to set
	  */
	public void setQname(String qname) {
		this.qname = qname;
	}
	
	/**
	  * Gets the Qtype.
	  * @return the Qtype
	  */
	public Integer getQtype() {
		return qtype;
	}
	
	/**
	  * Sets the Qtype.
	  * @param qtype the Qtype to set
	  */
	public void setQtype(Integer qtype) {
		this.qtype = qtype;
	}
	
	/**
	  * Gets the Qclass.
	  * @return the Qclass
	  */
	public Integer getQclass() {
		return qclass;
	}
	
	/**
	  * Sets the Qclass.
	  * @param qclass the Qclass to set
	  */
	public void setQclass(Integer qclass) {
		this.qclass = qclass;
	}
	
	/**
	  * Gets the AnswerRRs.
	  * @return the AnswerRRs
	  */
	public List<ResourceRecord> getAnswerRRs() {
		return answerRRs;
	}
	
	/**
	  * Sets the AnswerRRs.
	  * @param answerRRs the AnswerRRs to set
	  */
	public void setAnswerRRs(List<ResourceRecord> answerRRs) {
		this.answerRRs = answerRRs;
	}
	
	/**
	  * Gets the AuthorityRRs.
	  * @return the AuthorityRRs
	  */
	public List<ResourceRecord> getAuthorityRRs() {
		return authorityRRs;
	}
	
	/**
	  * Sets the AuthorityRRs.
	  * @param authorityRRs the AuthorityRRs to set
	  */
	public void setAuthorityRRs(List<ResourceRecord> authorityRRs) {
		this.authorityRRs = authorityRRs;
	}
	
	/**
	  * Gets the AdditionalRRs.
	  * @return the AdditionalRRs
	  */
	public List<ResourceRecord> getAdditionalRRs() {
		return additionalRRs;
	}
	
	/**
	  * Sets the AdditionalRRs.
	  * @param additionalRRs the AdditionalRRs to set
	  */
	public void setAdditionalRRs(List<ResourceRecord> additionalRRs) {
		this.additionalRRs = additionalRRs;
	}
}

