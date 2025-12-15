package dev.pacr.dns.model.rfc8618;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * RFC 8618 C-DNS Query Response Extended
 * <p>
 * Extended information for queries and responses.
 *
 * @see <a href="https://tools.ietf.org/html/rfc8618">RFC 8618</a>
 * @author Patrick Rafferty
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QueryResponseExtended {
	
	/**
	 * The question index.
	 */
	@JsonProperty("question-index")
	private Integer questionIndex;
	
	/**
	 * The answer index.
	 */
	@JsonProperty("answer-index")
	private Integer answerIndex;
	
	/**
	 * The authority index.
	 */
	@JsonProperty("authority-index")
	private Integer authorityIndex;
	
	/**
	 * The additional index.
	 */
	@JsonProperty("additional-index")
	private Integer additionalIndex;
	
	// Constructors
	
	/**
	 * Default constructor.
	 */
	public QueryResponseExtended() {
	}
	
	// Getters and Setters
	
	/**
	 * Gets the question index.
	 *
	 * @return the questionIndex
	 */
	public Integer getQuestionIndex() {
		return questionIndex;
	}
	
	/**
	 * Sets the question index.
	 *
	 * @param questionIndex the questionIndex to set
	 */
	public void setQuestionIndex(Integer questionIndex) {
		this.questionIndex = questionIndex;
	}
	
	/**
	 * Gets the answer index.
	 *
	 * @return the answerIndex
	 */
	public Integer getAnswerIndex() {
		return answerIndex;
	}
	
	/**
	 * Sets the answer index.
	 *
	 * @param answerIndex the answerIndex to set
	 */
	public void setAnswerIndex(Integer answerIndex) {
		this.answerIndex = answerIndex;
	}
	
	/**
	 * Gets the authority index.
	 *
	 * @return the authorityIndex
	 */
	public Integer getAuthorityIndex() {
		return authorityIndex;
	}
	
	/**
	 * Sets the authority index.
	 *
	 * @param authorityIndex the authorityIndex to set
	 */
	public void setAuthorityIndex(Integer authorityIndex) {
		this.authorityIndex = authorityIndex;
	}
	
	/**
	 * Gets the additional index.
	 *
	 * @return the additionalIndex
	 */
	public Integer getAdditionalIndex() {
		return additionalIndex;
	}
	
	/**
	 * Sets the additional index.
	 *
	 * @param additionalIndex the additionalIndex to set
	 */
	public void setAdditionalIndex(Integer additionalIndex) {
		this.additionalIndex = additionalIndex;
	}
}
