package dev.pacr.dns.model.rfc8618;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * RFC 8618 C-DNS Block Tables
 * <p>
 * Index tables for compacting repeated data in the block.
 *
 * @see <a href="https://tools.ietf.org/html/rfc8618">RFC 8618</a>
 * @author Patrick Rafferty
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BlockTables {
	
	/**
	 * The ip address.
	 */
	@JsonProperty("ip-address")
	private List<String> ipAddress;
	
	/**
	 * The classtype.
	 */
	@JsonProperty("classtype")
	private List<ClassType> classtype;
	
	/**
	 * The name rdata.
	 */
	@JsonProperty("name-rdata")
	private List<String> nameRdata;
	
	/**
	 * The qr sig.
	 */
	@JsonProperty("qr-sig")
	private List<QueryResponseSignature> qrSig;
	
	/**
	 * The qlist.
	 */
	@JsonProperty("qlist")
	private List<QuestionList> qlist;
	
	/**
	 * The qrr.
	 */
	@JsonProperty("qrr")
	private List<RR> qrr;
	
	/**
	 * The rrlist.
	 */
	@JsonProperty("rrlist")
	private List<RRList> rrlist;
	
	/**
	 * The rr.
	 */
	@JsonProperty("rr")
	private List<RR> rr;
	
	// Constructors
	
	/**
	 * Default constructor.
	 */
	public BlockTables() {
	}
	
	// Getters and Setters
	
	/**
	 * Gets the ip address.
	 *
	 * @return the ipAddress
	 */
	public List<String> getIpAddress() {
		return ipAddress;
	}
	
	/**
	 * Sets the ip address.
	 *
	 * @param ipAddress the ipAddress to set
	 */
	public void setIpAddress(List<String> ipAddress) {
		this.ipAddress = ipAddress;
	}
	
	/**
	 * Gets the classtype.
	 *
	 * @return the classtype
	 */
	public List<ClassType> getClasstype() {
		return classtype;
	}
	
	/**
	 * Sets the classtype.
	 *
	 * @param classtype the classtype to set
	 */
	public void setClasstype(List<ClassType> classtype) {
		this.classtype = classtype;
	}
	
	/**
	 * Gets the name rdata.
	 *
	 * @return the nameRdata
	 */
	public List<String> getNameRdata() {
		return nameRdata;
	}
	
	/**
	 * Sets the name rdata.
	 *
	 * @param nameRdata the nameRdata to set
	 */
	public void setNameRdata(List<String> nameRdata) {
		this.nameRdata = nameRdata;
	}
	
	/**
	 * Gets the qr sig.
	 *
	 * @return the qrSig
	 */
	public List<QueryResponseSignature> getQrSig() {
		return qrSig;
	}
	
	/**
	 * Sets the qr sig.
	 *
	 * @param qrSig the qrSig to set
	 */
	public void setQrSig(List<QueryResponseSignature> qrSig) {
		this.qrSig = qrSig;
	}
	
	/**
	 * Gets the qlist.
	 *
	 * @return the qlist
	 */
	public List<QuestionList> getQlist() {
		return qlist;
	}
	
	/**
	 * Sets the qlist.
	 *
	 * @param qlist the qlist to set
	 */
	public void setQlist(List<QuestionList> qlist) {
		this.qlist = qlist;
	}
	
	/**
	 * Gets the qrr.
	 *
	 * @return the qrr
	 */
	public List<RR> getQrr() {
		return qrr;
	}
	
	/**
	 * Sets the qrr.
	 *
	 * @param qrr the qrr to set
	 */
	public void setQrr(List<RR> qrr) {
		this.qrr = qrr;
	}
	
	/**
	 * Gets the rrlist.
	 *
	 * @return the rrlist
	 */
	public List<RRList> getRrlist() {
		return rrlist;
	}
	
	/**
	 * Sets the rrlist.
	 *
	 * @param rrlist the rrlist to set
	 */
	public void setRrlist(List<RRList> rrlist) {
		this.rrlist = rrlist;
	}
	
	/**
	 * Gets the rr.
	 *
	 * @return the rr
	 */
	public List<RR> getRr() {
		return rr;
	}
	
	/**
	 * Sets the rr.
	 *
	 * @param rr the rr to set
	 */
	public void setRr(List<RR> rr) {
		this.rr = rr;
	}
}
