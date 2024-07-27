package com.nmims.beans;

import java.io.Serializable;

//spring security related changes rename TestQuestionOptionBean to TestQuestionOptionExamBean
public class TestQuestionOptionExamBean implements Serializable {
	private Long id;
	private Long questionId;
	private String optionData;
	private String isCorrect;
	private String selected;
	
	
	public String getSelected() {
		return selected;
	}
	public void setSelected(String selected) {
		this.selected = selected;
	}
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
	public String getOptionData() {
		return optionData;
	}
	public void setOptionData(String optionData) {
		this.optionData = optionData;
	}
	public String getIsCorrect() {
		return isCorrect;
	}
	public void setIsCorrect(String isCorrect) {
		this.isCorrect = isCorrect;
	}
	@Override
	public String toString() {
		return "[option:"+optionData+",isCorrect:"+isCorrect+"]";
	}
}
