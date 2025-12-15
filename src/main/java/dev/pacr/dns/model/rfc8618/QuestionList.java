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
	
	/**
	  * Constructs a new QuestionList.
	  */
	public QuestionList() {
	}
	
	/**

	
	 * Constructs a new QuestionList.

	
	 */

	
	public QuestionList(Integer qnameIndex, Integer classtypeIndex) {
		this.qnameIndex = qnameIndex;
		this.classtypeIndex = classtypeIndex;
	}
	
	// Getters and Setters
	
	/**
	  * Gets the QnameIndex.
	  * @return the QnameIndex
	  */
	public Integer getQnameIndex() {
		return qnameIndex;
	}
	
	/**
	  * Sets the QnameIndex.
	  * @param qnameIndex the QnameIndex to set
	  */
	public void setQnameIndex(Integer qnameIndex) {
		this.qnameIndex = qnameIndex;
	}
	
	/**
	  * Gets the ClasstypeIndex.
	  * @return the ClasstypeIndex
	  */
	public Integer getClasstypeIndex() {
		return classtypeIndex;
	}
	
	/**
	  * Sets the ClasstypeIndex.
	  * @param classtypeIndex the ClasstypeIndex to set
	  */
	public void setClasstypeIndex(Integer classtypeIndex) {
		this.classtypeIndex = classtypeIndex;
	}
}

