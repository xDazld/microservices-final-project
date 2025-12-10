package dev.pacr.dns.model.rfc8618;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * RFC 8618 C-DNS Question List
 * <p>
 * List of questions in a DNS message.
 *
 * @see <a href="https://tools.ietf.org/html/rfc8618">RFC 8618</a>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuestionList {
	
	@JsonProperty("qname-index")
	private Integer qnameIndex;
	
	@JsonProperty("classtype-index")
	private Integer classtypeIndex;
	
	// Constructors
	
	public QuestionList() {
	}
	
	public QuestionList(Integer qnameIndex, Integer classtypeIndex) {
		this.qnameIndex = qnameIndex;
		this.classtypeIndex = classtypeIndex;
	}
	
	// Getters and Setters
	
	public Integer getQnameIndex() {
		return qnameIndex;
	}
	
	public void setQnameIndex(Integer qnameIndex) {
		this.qnameIndex = qnameIndex;
	}
	
	public Integer getClasstypeIndex() {
		return classtypeIndex;
	}
	
	public void setClasstypeIndex(Integer classtypeIndex) {
		this.classtypeIndex = classtypeIndex;
	}
}

