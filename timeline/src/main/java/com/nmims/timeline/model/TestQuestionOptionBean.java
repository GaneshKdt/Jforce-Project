package com.nmims.timeline.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="exam.test_question_options", schema="exam")
public class TestQuestionOptionBean  implements Serializable{

private static final long serialVersionUID = 1L;
	/*
	 * id, questionId, optionData, isCorrect
	 * */
	@Id  
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long questionId;
	private String optionData;
	private String isCorrect;
	
	@Transient
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
