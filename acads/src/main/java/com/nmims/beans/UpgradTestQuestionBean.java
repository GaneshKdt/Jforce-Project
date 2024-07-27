package com.nmims.beans;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class UpgradTestQuestionBean  implements Serializable  {
	private Long questionNo;
	private Long testId;
	private Integer marks;
	private String question_type;
	private String chapter;
	private String question;
	private String description;
	private Integer isSubQuestion;
	private String active;
	private Integer  copyCaseThreshold;
	private String uploadType;
	private String createdBy;
	private Date createdDate;
	private String lastModifiedBy;
	private Date lastModifiedDate;
	private List<UpgradTestQuestionOptionBean> testQuestionOptions;
	
	
	
	
	
	@Override
	public String toString() {
		return "UpgradTestQuestionBean [ testId=" + testId + ", marks=" + marks + ", question_type="
				+ question_type + ", chapter=" + chapter + ", question=" + question + ", description=" + description
				+ ", isSubQuestion=" + isSubQuestion + ", active=" + active + ", copyCaseThreshold=" + copyCaseThreshold
				+ ", uploadType=" + uploadType + ", createdBy=" + createdBy + ", createdDate=" + createdDate
				+ ", lastModifiedBy=" + lastModifiedBy + ", lastModifiedDate=" + lastModifiedDate
				+ ", testQuestionOptions=" + testQuestionOptions +  "]";
	}
	
	
	
	public Long getQuestionNo() {
		return questionNo;
	}



	public void setQuestionNo(Long questionNo) {
		this.questionNo = questionNo;
	}



	public List<UpgradTestQuestionOptionBean> getTestQuestionOptions() {
		return testQuestionOptions;
	}



	public void setTestQuestionOptions(List<UpgradTestQuestionOptionBean> testQuestionOptions) {
		this.testQuestionOptions = testQuestionOptions;
	}


	public String getCreatedBy() {
		return createdBy;
	}



	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}



	public Date getCreatedDate() {
		return createdDate;
	}



	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}



	public String getLastModifiedBy() {
		return lastModifiedBy;
	}



	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}



	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}



	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}



	
	public Long getTestId() {
		return testId;
	}
	public void setTestId(Long testId) {
		this.testId = testId;
	}
	public Integer getMarks() {
		return marks;
	}
	public void setMarks(Integer marks) {
		this.marks = marks;
	}
	
	public String getQuestion_type() {
		return question_type;
	}



	public void setQuestion_type(String question_type) {
		this.question_type = question_type;
	}



	public String getChapter() {
		return chapter;
	}
	public void setChapter(String chapter) {
		this.chapter = chapter;
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Integer getIsSubQuestion() {
		return isSubQuestion;
	}
	public void setIsSubQuestion(Integer isSubQuestion) {
		this.isSubQuestion = isSubQuestion;
	}
	public String getActive() {
		return active;
	}
	public void setActive(String active) {
		this.active = active;
	}
	public Integer getCopyCaseThreshold() {
		return copyCaseThreshold;
	}
	public void setCopyCaseThreshold(Integer copyCaseThreshold) {
		this.copyCaseThreshold = copyCaseThreshold;
	}
	public String getUploadType() {
		return uploadType;
	}
	public void setUploadType(String uploadType) {
		this.uploadType = uploadType;
	}
	
	
	
}
