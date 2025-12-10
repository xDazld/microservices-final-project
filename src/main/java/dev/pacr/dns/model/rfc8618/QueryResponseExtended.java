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
	
	public QueryResponseExtended() {
	}
	
	// Getters and Setters
	
	public Integer getQuestionIndex() {
		return questionIndex;
	}
	
	public void setQuestionIndex(Integer questionIndex) {
		this.questionIndex = questionIndex;
	}
	
	public Integer getAnswerIndex() {
		return answerIndex;
	}
	
	public void setAnswerIndex(Integer answerIndex) {
		this.answerIndex = answerIndex;
	}
	
	public Integer getAuthorityIndex() {
		return authorityIndex;
	}
	
	public void setAuthorityIndex(Integer authorityIndex) {
		this.authorityIndex = authorityIndex;
	}
	
	public Integer getAdditionalIndex() {
		return additionalIndex;
	}
	
	public void setAdditionalIndex(Integer additionalIndex) {
		this.additionalIndex = additionalIndex;
	}
}

