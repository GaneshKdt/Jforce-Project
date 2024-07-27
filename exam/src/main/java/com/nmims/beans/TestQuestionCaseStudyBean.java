package com.nmims.beans;

import java.io.Serializable;

public class TestQuestionCaseStudyBean  implements Serializable {

	private Long id;
	private Long questionId;
	private Long subQuestionId;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getQuestionId() {
		return questionId;
	}
	public void setQuestionId(Long questionId) {
		this.questionId = questionId;
	}
	public Long getSubQuestionId() {
		return subQuestionId;
	}
	public void setSubQuestionId(Long subQuestionId) {
		this.subQuestionId = subQuestionId;
	}
	
	
}
