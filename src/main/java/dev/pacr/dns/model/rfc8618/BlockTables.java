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
	
	public BlockTables() {
	}
	
	// Getters and Setters
	
	public List<String> getIpAddress() {
		return ipAddress;
	}
	
	public void setIpAddress(List<String> ipAddress) {
		this.ipAddress = ipAddress;
	}
	
	public List<ClassType> getClasstype() {
		return classtype;
	}
	
	public void setClasstype(List<ClassType> classtype) {
		this.classtype = classtype;
	}
	
	public List<String> getNameRdata() {
		return nameRdata;
	}
	
	public void setNameRdata(List<String> nameRdata) {
		this.nameRdata = nameRdata;
	}
	
	public List<QueryResponseSignature> getQrSig() {
		return qrSig;
	}
	
	public void setQrSig(List<QueryResponseSignature> qrSig) {
		this.qrSig = qrSig;
	}
	
	public List<QuestionList> getQlist() {
		return qlist;
	}
	
	public void setQlist(List<QuestionList> qlist) {
		this.qlist = qlist;
	}
	
	public List<RR> getQrr() {
		return qrr;
	}
	
	public void setQrr(List<RR> qrr) {
		this.qrr = qrr;
	}
	
	public List<RRList> getRrlist() {
		return rrlist;
	}
	
	public void setRrlist(List<RRList> rrlist) {
		this.rrlist = rrlist;
	}
	
	public List<RR> getRr() {
		return rr;
	}
	
	public void setRr(List<RR> rr) {
		this.rr = rr;
	}
}

