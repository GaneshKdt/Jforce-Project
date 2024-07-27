package com.nmims.beans;

import java.io.Serializable;
import java.util.ArrayList;

public class TestQuestionConfigBean implements Serializable {

	//id, testId, minNoOfQuestions, maxNoOfQuestions
	private Long id;

	private Long testId;
	
	private Integer type;
	private String typeName;
	private Integer minNoOfQuestions;
	private Integer maxNoOfQuestions;
	private Integer noOfQuestions;
	private Integer count;
	private String testName;
    private String templateId;
    private String name;
    private String testType;
    private String duration;
    private Double questionMarks;
    private  ArrayList<SectionBean> sectionBean;
    private String sectionId;
    
    public String getSectionId() {
		return sectionId;
	}
	public void setSectionId(String sectionId) {
		this.sectionId = sectionId;
	}
	
	public Double getQuestionMarks() {
		return questionMarks;
	}
	public void setQuestionMarks(Double questionMarks) {
		this.questionMarks = questionMarks;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	public String getName() { 
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTestType() {
		return testType;
	}
	public void setTestType(String testType) {
		this.testType = testType;
	} 
	private ArrayList<TestQuestionConfigBean> testQuestionConfigBean;
    
	public ArrayList<TestQuestionConfigBean> getTestQuestionConfigBean() {
		return testQuestionConfigBean;
	}
	public void setTestQuestionConfigBean(ArrayList<TestQuestionConfigBean> testQuestionConfigBean) {
		this.testQuestionConfigBean = testQuestionConfigBean;
	}
	public String getTemplateId() {
		return templateId;
	}
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	} 
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



	public Integer getCount() {
		return count;
	}



	public void setCount(Integer count) {
		this.count = count;
	}



	public String getTestName() {
		return testName;
	}



	public void setTestName(String testName) {
		this.testName = testName;
	}
	
	public ArrayList<SectionBean> getSectionBean() {
		return sectionBean;
	}
	public void setSectionBean(ArrayList<SectionBean> sectionBean) {
		this.sectionBean = sectionBean;
	}
	@Override
	public String toString() {
		return "TestQuestionConfigBean [id=" + id + ", testId=" + testId + ", type=" + type + ", typeName=" + typeName
				+ ", minNoOfQuestions=" + minNoOfQuestions + ", maxNoOfQuestions=" + maxNoOfQuestions
				+ ", noOfQuestions=" + noOfQuestions + ", count=" + count + ", testName=" + testName + ", templateId="
				+ templateId + ", name=" + name + ", testType=" + testType + ", testQuestionConfigBean="
				+ testQuestionConfigBean + "]";
	}   
	
}
