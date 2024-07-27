package com.nmims.beans;

import java.io.Serializable;

public class TestWeightageBean extends BaseExamBean implements Serializable {

	private Long id;
	
	private Long testId;
	private String maxMarks;
	private String noOfQuestionToMarks;
	private String active;
	private String chapter;
	
	private String errorMessage = "";
	private boolean errorRecord = false;
	
	
	
	
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public boolean isErrorRecord() {
		return errorRecord;
	}
	public void setErrorRecord(boolean errorRecord) {
		this.errorRecord = errorRecord;
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
	public String getMaxMarks() {
		return maxMarks;
	}
	public void setMaxMarks(String maxMarks) {
		this.maxMarks = maxMarks;
	}
	public String getNoOfQuestionToMarks() {
		return noOfQuestionToMarks;
	}
	public void setNoOfQuestionToMarks(String noOfQuestionToMarks) {
		this.noOfQuestionToMarks = noOfQuestionToMarks;
	}
	
	public String getActive() {
		return active;
	}
	public void setActive(String active) {
		this.active = active;
	}
	
	public String getChapter() {
		return chapter;
	}
	public void setChapter(String chapter) {
		this.chapter = chapter;
	}
	@Override
	public String toString() {
		return "TestQuestionsWeightage [testId=" + testId + ", maxMarks=" + maxMarks
				+ ", noOfQuestionToMarks=" + noOfQuestionToMarks + ", active="
				+ active + ", chapter=" + chapter + "]";
	}

}
