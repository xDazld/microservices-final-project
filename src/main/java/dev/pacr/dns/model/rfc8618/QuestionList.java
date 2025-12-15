package dev.pacr.dns.model.rfc8618;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * RFC 8618 C-DNS Question List
 * <p>
 * List of questions in a DNS message.
 *
 * @see <a href="https://tools.ietf.org/html/rfc8618">RFC 8618</a>
 * @author Patrick Rafferty
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuestionList {
	
	/**
	 * The qname index.
	 */
	@JsonProperty("qname-index")
	private Integer qnameIndex;
	
	/**
	 * The classtype index.
	 */
	@JsonProperty("classtype-index")
	private Integer classtypeIndex;
	
	// Constructors
	
	/**
	 * Default constructor.
	 */
	public QuestionList() {
	}
	
	/**
	 * Constructor with qname index and classtype index.
	 *
	 * @param qnameIndex the qname index
	 * @param classtypeIndex the classtype index
	 */
	public QuestionList(Integer qnameIndex, Integer classtypeIndex) {
		this.qnameIndex = qnameIndex;
		this.classtypeIndex = classtypeIndex;
	}
	
	// Getters and Setters
	
	/**
	 * Gets the qname index.
	 *
	 * @return the qnameIndex
	 */
	public Integer getQnameIndex() {
		return qnameIndex;
	}
	
	/**
	 * Sets the qname index.
	 *
	 * @param qnameIndex the qnameIndex to set
	 */
	public void setQnameIndex(Integer qnameIndex) {
		this.qnameIndex = qnameIndex;
	}
	
	/**
	 * Gets the classtype index.
	 *
	 * @return the classtypeIndex
	 */
	public Integer getClasstypeIndex() {
		return classtypeIndex;
	}
	
	/**
	 * Sets the classtype index.
	 *
	 * @param classtypeIndex the classtypeIndex to set
	 */
	public void setClasstypeIndex(Integer classtypeIndex) {
		this.classtypeIndex = classtypeIndex;
	}
}
