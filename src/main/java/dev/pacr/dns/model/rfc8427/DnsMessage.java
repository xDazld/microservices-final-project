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
 * @author Patrick Rafferty
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DnsMessage {
	
	/**
	 * The id.
	 */
	@JsonProperty("ID")
	private Integer id;
	
	/**
	 * The qr.
	 */
	@JsonProperty("QR")
	private Integer qr;
	
	/**
	 * The opcode.
	 */
	@JsonProperty("Opcode")
	private Integer opcode;
	
	/**
	 * The aa.
	 */
	@JsonProperty("AA")
	private Integer aa;
	
	/**
	 * The tc.
	 */
	@JsonProperty("TC")
	private Integer tc;
	
	/**
	 * The rd.
	 */
	@JsonProperty("RD")
	private Integer rd;
	
	/**
	 * The ra.
	 */
	@JsonProperty("RA")
	private Integer ra;
	
	/**
	 * The ad.
	 */
	@JsonProperty("AD")
	private Integer ad;
	
	/**
	 * The cd.
	 */
	@JsonProperty("CD")
	private Integer cd;
	
	/**
	 * The rcode.
	 */
	@JsonProperty("RCODE")
	private Integer rcode;
	
	/**
	 * The qdcount.
	 */
	@JsonProperty("QDCOUNT")
	private Integer qdcount;
	
	/**
	 * The ancount.
	 */
	@JsonProperty("ANCOUNT")
	private Integer ancount;
	
	/**
	 * The nscount.
	 */
	@JsonProperty("NSCOUNT")
	private Integer nscount;
	
	/**
	 * The arcount.
	 */
	@JsonProperty("ARCOUNT")
	private Integer arcount;
	
	/**
	 * The qname.
	 */
	@JsonProperty("QNAME")
	private String qname;
	
	/**
	 * The qtype.
	 */
	@JsonProperty("QTYPE")
	private Integer qtype;
	
	/**
	 * The qclass.
	 */
	@JsonProperty("QCLASS")
	private Integer qclass;
	
	/**
	 * The answer r rs.
	 */
	@JsonProperty("answerRRs")
	private List<ResourceRecord> answerRRs;
	
	/**
	 * The authority r rs.
	 */
	@JsonProperty("authorityRRs")
	private List<ResourceRecord> authorityRRs;
	
	/**
	 * The additional r rs.
	 */
	@JsonProperty("additionalRRs")
	private List<ResourceRecord> additionalRRs;
	
	// Constructors
	
	/**
	 * Default constructor.
	 */
	public DnsMessage() {
	}
	
	// Getters and Setters
	
	/**
	 * Gets id.
	 *
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}
	
	/**
	 * Sets id.
	 *
	 * @param id the id
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	
	/**
	 * Gets qr.
	 *
	 * @return the qr
	 */
	public Integer getQr() {
		return qr;
	}
	
	/**
	 * Sets qr.
	 *
	 * @param qr the qr
	 */
	public void setQr(Integer qr) {
		this.qr = qr;
	}
	
	/**
	 * Gets opcode.
	 *
	 * @return the opcode
	 */
	public Integer getOpcode() {
		return opcode;
	}
	
	/**
	 * Sets opcode.
	 *
	 * @param opcode the opcode
	 */
	public void setOpcode(Integer opcode) {
		this.opcode = opcode;
	}
	
	/**
	 * Gets aa.
	 *
	 * @return the aa
	 */
	public Integer getAa() {
		return aa;
	}
	
	/**
	 * Sets aa.
	 *
	 * @param aa the aa
	 */
	public void setAa(Integer aa) {
		this.aa = aa;
	}
	
	/**
	 * Gets tc.
	 *
	 * @return the tc
	 */
	public Integer getTc() {
		return tc;
	}
	
	/**
	 * Sets tc.
	 *
	 * @param tc the tc
	 */
	public void setTc(Integer tc) {
		this.tc = tc;
	}
	
	/**
	 * Gets rd.
	 *
	 * @return the rd
	 */
	public Integer getRd() {
		return rd;
	}
	
	/**
	 * Sets rd.
	 *
	 * @param rd the rd
	 */
	public void setRd(Integer rd) {
		this.rd = rd;
	}
	
	/**
	 * Gets ra.
	 *
	 * @return the ra
	 */
	public Integer getRa() {
		return ra;
	}
	
	/**
	 * Sets ra.
	 *
	 * @param ra the ra
	 */
	public void setRa(Integer ra) {
		this.ra = ra;
	}
	
	/**
	 * Gets ad.
	 *
	 * @return the ad
	 */
	public Integer getAd() {
		return ad;
	}
	
	/**
	 * Sets ad.
	 *
	 * @param ad the ad
	 */
	public void setAd(Integer ad) {
		this.ad = ad;
	}
	
	/**
	 * Gets cd.
	 *
	 * @return the cd
	 */
	public Integer getCd() {
		return cd;
	}
	
	/**
	 * Sets cd.
	 *
	 * @param cd the cd
	 */
	public void setCd(Integer cd) {
		this.cd = cd;
	}
	
	/**
	 * Gets rcode.
	 *
	 * @return the rcode
	 */
	public Integer getRcode() {
		return rcode;
	}
	
	/**
	 * Sets rcode.
	 *
	 * @param rcode the rcode
	 */
	public void setRcode(Integer rcode) {
		this.rcode = rcode;
	}
	
	/**
	 * Gets qdcount.
	 *
	 * @return the qdcount
	 */
	public Integer getQdcount() {
		return qdcount;
	}
	
	/**
	 * Sets qdcount.
	 *
	 * @param qdcount the qdcount
	 */
	public void setQdcount(Integer qdcount) {
		this.qdcount = qdcount;
	}
	
	/**
	 * Gets ancount.
	 *
	 * @return the ancount
	 */
	public Integer getAncount() {
		return ancount;
	}
	
	/**
	 * Sets ancount.
	 *
	 * @param ancount the ancount
	 */
	public void setAncount(Integer ancount) {
		this.ancount = ancount;
	}
	
	/**
	 * Gets nscount.
	 *
	 * @return the nscount
	 */
	public Integer getNscount() {
		return nscount;
	}
	
	/**
	 * Sets nscount.
	 *
	 * @param nscount the nscount
	 */
	public void setNscount(Integer nscount) {
		this.nscount = nscount;
	}
	
	/**
	 * Gets arcount.
	 *
	 * @return the arcount
	 */
	public Integer getArcount() {
		return arcount;
	}
	
	/**
	 * Sets arcount.
	 *
	 * @param arcount the arcount
	 */
	public void setArcount(Integer arcount) {
		this.arcount = arcount;
	}
	
	/**
	 * Gets qname.
	 *
	 * @return the qname
	 */
	public String getQname() {
		return qname;
	}
	
	/**
	 * Sets qname.
	 *
	 * @param qname the qname
	 */
	public void setQname(String qname) {
		this.qname = qname;
	}
	
	/**
	 * Gets qtype.
	 *
	 * @return the qtype
	 */
	public Integer getQtype() {
		return qtype;
	}
	
	/**
	 * Sets qtype.
	 *
	 * @param qtype the qtype
	 */
	public void setQtype(Integer qtype) {
		this.qtype = qtype;
	}
	
	/**
	 * Gets qclass.
	 *
	 * @return the qclass
	 */
	public Integer getQclass() {
		return qclass;
	}
	
	/**
	 * Sets qclass.
	 *
	 * @param qclass the qclass
	 */
	public void setQclass(Integer qclass) {
		this.qclass = qclass;
	}
	
	/**
	 * Gets answer r rs.
	 *
	 * @return the answerRRs
	 */
	public List<ResourceRecord> getAnswerRRs() {
		return answerRRs;
	}
	
	/**
	 * Sets answer r rs.
	 *
	 * @param answerRRs the answerRRs
	 */
	public void setAnswerRRs(List<ResourceRecord> answerRRs) {
		this.answerRRs = answerRRs;
	}
	
	/**
	 * Gets authority r rs.
	 *
	 * @return the authorityRRs
	 */
	public List<ResourceRecord> getAuthorityRRs() {
		return authorityRRs;
	}
	
	/**
	 * Sets authority r rs.
	 *
	 * @param authorityRRs the authorityRRs
	 */
	public void setAuthorityRRs(List<ResourceRecord> authorityRRs) {
		this.authorityRRs = authorityRRs;
	}
	
	/**
	 * Gets additional r rs.
	 *
	 * @return the additionalRRs
	 */
	public List<ResourceRecord> getAdditionalRRs() {
		return additionalRRs;
	}
	
	/**
	 * Sets additional r rs.
	 *
	 * @param additionalRRs the additionalRRs
	 */
	public void setAdditionalRRs(List<ResourceRecord> additionalRRs) {
		this.additionalRRs = additionalRRs;
	}
}
