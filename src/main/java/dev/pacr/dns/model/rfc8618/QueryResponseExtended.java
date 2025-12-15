package dev.pacr.dns.model.rfc8618;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
	 * RFC 8618 C-DNS Query Response Extended
 * <p>
 * Extended information for queries and responses.
 *
 * @see <a href="https://tools.ietf.org/html/rfc8618">RFC 8618</a>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QueryResponseExtended {
	
	@JsonProperty("question-index")
	private Integer questionIndex;
	
	@JsonProperty("answer-index")
	private Integer answerIndex;
	
	@JsonProperty("authority-index")
	private Integer authorityIndex;
	
	@JsonProperty("additional-index")
	private Integer additionalIndex;
	
	// Constructors
	
	/**
	 * Constructs a new QueryResponseExtended.
	 */
	public QueryResponseExtended() {
	}
	
	// Getters and Setters
	
	/**
	 * Gets the QuestionIndex.
	 * @return the QuestionIndex
	 */
	public Integer getQuestionIndex() {
		return questionIndex;
	}
	
	/**
	 * Sets the QuestionIndex.
	 * @param questionIndex the QuestionIndex to set
	 */
	public void setQuestionIndex(Integer questionIndex) {
		this.questionIndex = questionIndex;
	}
	
	/**
	 * Gets the AnswerIndex.
	 * @return the AnswerIndex
	 */
	public Integer getAnswerIndex() {
		return answerIndex;
	}
	
	/**
	 * Sets the AnswerIndex.
	 * @param answerIndex the AnswerIndex to set
	 */
	public void setAnswerIndex(Integer answerIndex) {
		this.answerIndex = answerIndex;
	}
	
	/**
	 * Gets the AuthorityIndex.
	 * @return the AuthorityIndex
	 */
	public Integer getAuthorityIndex() {
		return authorityIndex;
	}
	
	/**
	 * Sets the AuthorityIndex.
	 * @param authorityIndex the AuthorityIndex to set
	 */
	public void setAuthorityIndex(Integer authorityIndex) {
		this.authorityIndex = authorityIndex;
	}
	
	/**
	 * Gets the AdditionalIndex.
	 * @return the AdditionalIndex
	 */
	public Integer getAdditionalIndex() {
		return additionalIndex;
	}
	
	/**
	 * Sets the AdditionalIndex.
	 * @param additionalIndex the AdditionalIndex to set
	 */
	public void setAdditionalIndex(Integer additionalIndex) {
		this.additionalIndex = additionalIndex;
	}
}

