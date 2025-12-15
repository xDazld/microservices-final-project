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
  */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BlockTables {
	
	@JsonProperty("ip-address")
	private List<String> ipAddress;
	
	@JsonProperty("classtype")
	private List<ClassType> classtype;
	
	@JsonProperty("name-rdata")
	private List<String> nameRdata;
	
	@JsonProperty("qr-sig")
	private List<QueryResponseSignature> qrSig;
	
	@JsonProperty("qlist")
	private List<QuestionList> qlist;
	
	@JsonProperty("qrr")
	private List<RR> qrr;
	
	@JsonProperty("rrlist")
	private List<RRList> rrlist;
	
	@JsonProperty("rr")
	private List<RR> rr;
	
	// Constructors
	
	/**
	  * Constructs a new BlockTables.
	  */
	public BlockTables() {
	}
	
	// Getters and Setters
	
	/**
	  * Gets the IpAddress.
	  * @return the IpAddress
	  */
	public List<String> getIpAddress() {
		return ipAddress;
	}
	
	/**
	  * Sets the IpAddress.
	  * @param ipAddress the IpAddress to set
	  */
	public void setIpAddress(List<String> ipAddress) {
		this.ipAddress = ipAddress;
	}
	
	/**
	  * Gets the Classtype.
	  * @return the Classtype
	  */
	public List<ClassType> getClasstype() {
		return classtype;
	}
	
	/**
	  * Sets the Classtype.
	  * @param classtype the Classtype to set
	  */
	public void setClasstype(List<ClassType> classtype) {
		this.classtype = classtype;
	}
	
	/**
	  * Gets the NameRdata.
	  * @return the NameRdata
	  */
	public List<String> getNameRdata() {
		return nameRdata;
	}
	
	/**
	  * Sets the NameRdata.
	  * @param nameRdata the NameRdata to set
	  */
	public void setNameRdata(List<String> nameRdata) {
		this.nameRdata = nameRdata;
	}
	
	/**
	  * Gets the QrSig.
	  * @return the QrSig
	  */
	public List<QueryResponseSignature> getQrSig() {
		return qrSig;
	}
	
	/**
	  * Sets the QrSig.
	  * @param qrSig the QrSig to set
	  */
	public void setQrSig(List<QueryResponseSignature> qrSig) {
		this.qrSig = qrSig;
	}
	
	/**
	  * Gets the Qlist.
	  * @return the Qlist
	  */
	public List<QuestionList> getQlist() {
		return qlist;
	}
	
	/**
	  * Sets the Qlist.
	  * @param qlist the Qlist to set
	  */
	public void setQlist(List<QuestionList> qlist) {
		this.qlist = qlist;
	}
	
	/**
	  * Gets the Qrr.
	  * @return the Qrr
	  */
	public List<RR> getQrr() {
		return qrr;
	}
	
	/**
	  * Sets the Qrr.
	  * @param qrr the Qrr to set
	  */
	public void setQrr(List<RR> qrr) {
		this.qrr = qrr;
	}
	
	/**
	  * Gets the Rrlist.
	  * @return the Rrlist
	  */
	public List<RRList> getRrlist() {
		return rrlist;
	}
	
	/**
	  * Sets the Rrlist.
	  * @param rrlist the Rrlist to set
	  */
	public void setRrlist(List<RRList> rrlist) {
		this.rrlist = rrlist;
	}
	
	/**
	  * Gets the Rr.
	  * @return the Rr
	  */
	public List<RR> getRr() {
		return rr;
	}
	
	/**
	  * Sets the Rr.
	  * @param rr the Rr to set
	  */
	public void setRr(List<RR> rr) {
		this.rr = rr;
	}
}

