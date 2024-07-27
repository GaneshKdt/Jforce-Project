package com.nmims.timeline.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="exam.test_questions_configuration", schema="exam")
public class TestQuestionConfigBean {

	private static final long serialVersionUID = 1L;
	
	//id, testId, minNoOfQuestions, maxNoOfQuestions
	@Id  
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long testId;
	
	private Integer type;
	@Transient
	private String typeName;
	private Integer minNoOfQuestions;
	private Integer maxNoOfQuestions;
	@Transient
	private Integer noOfQuestions;
	
	
		
	public Long getId() {
		return id;
	}



	public void setId(Long id) {
		this.id = id;
	}



	public Long getTestId() {
		return testId;
	}



	public void setTestId(Long testId) {
		this.testId = testId;
	}



	public Integer getType() {
		return type;
	}



	public void setType(Integer type) {
		this.type = type;
	}



	public String getTypeName() {
		return typeName;
	}



	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}



	public Integer getMinNoOfQuestions() {
		if(minNoOfQuestions==null) {
			return 0;
		}else {
		return minNoOfQuestions;
		}
	}



	public void setMinNoOfQuestions(Integer minNoOfQuestions) {
		this.minNoOfQuestions = minNoOfQuestions;
	}



	public Integer getMaxNoOfQuestions() {
		if(maxNoOfQuestions == null) {
			return 0;
		}
		else {
		return maxNoOfQuestions;
		}
	}



	public void setMaxNoOfQuestions(Integer maxNoOfQuestions) {
		this.maxNoOfQuestions = maxNoOfQuestions;
	}



	public Integer getNoOfQuestions() {
		return noOfQuestions;
	}



	public void setNoOfQuestions(Integer noOfQuestions) {
		this.noOfQuestions = noOfQuestions;
	}



	@Override
	public String toString() {
		return "["
				+ " id="+id+", testId="+testId+" type="+type+" typeName="+typeName+""
				+ " noOfQuestions"+noOfQuestions+" minNoOfQuestions="+minNoOfQuestions+" maxNoOfQuestions="+maxNoOfQuestions
				+ "]";
	}
}
