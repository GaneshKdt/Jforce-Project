package com.nmims.beans;

import java.io.Serializable;
import java.util.List;


public class UpgradAssessmentBean  implements Serializable {
	
	@Override
	public String toString() {
		return "UpgradAssessmentBean [id=" + id + ", sapid=" + sapid + ", testId=" + testId + ", attempt=" + attempt
				+ ", testStartedOn=" + testStartedOn + ", remainingTime=" + remainingTime + ", testEndedOn="
				+ testEndedOn + ", testCompleted=" + testCompleted + ", score=" + score + ", testQuestionsApplicable="
				+ testQuestionsApplicable + ", noOfQuestionsAttempted=" + noOfQuestionsAttempted + ", showResult="
				+ showResult + ", createdBy=" + createdBy + ", createdDate=" + createdDate + ", lastModifiedBy="
				+ lastModifiedBy + ", lastModifiedDate=" + lastModifiedDate + ", testDetails=" + testDetails + "]";
	}
	private int id;
	private String sapid;
	private Long testId;
	private int attempt;
	private String testStartedOn;
	private int remainingTime;
	private String testEndedOn;
	private String testCompleted;
	private Long  score;
	private String testQuestionsApplicable;
	private int noOfQuestionsAttempted;
	private String showResult;
	private String createdBy;
	private String createdDate;
	private String lastModifiedBy;
	private String lastModifiedDate;
	
	private List<UpgradAssessmentBean> testDetails ; 
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public Long getTestId() {
		return testId;
	}
	public void setTestId(Long testId) {
		this.testId = testId;
	}
	public int getAttempt() {
		return attempt;
	}
	public void setAttempt(int attempt) {
		this.attempt = attempt;
	}
	public String getTestStartedOn() {
		return testStartedOn;
	}
	public void setTestStartedOn(String testStartedOn) {
		this.testStartedOn = testStartedOn;
	}
	public int getRemainingTime() {
		return remainingTime;
	}
	public void setRemainingTime(int remainingTime) {
		this.remainingTime = remainingTime;
	}
	public String getTestEndedOn() {
		return testEndedOn;
	}
	public void setTestEndedOn(String testEndedOn) {
		this.testEndedOn = testEndedOn;
	}
	public String getTestCompleted() {
		return testCompleted;
	}
	public void setTestCompleted(String testCompleted) {
		this.testCompleted = testCompleted;
	}
	public Long getScore() {
		return score;
	}
	public void setScore(Long score) {
		this.score = score;
	}
	public String getTestQuestionsApplicable() {
		return testQuestionsApplicable;
	}
	public void setTestQuestionsApplicable(String testQuestionsApplicable) {
		this.testQuestionsApplicable = testQuestionsApplicable;
	}
	public int getNoOfQuestionsAttempted() {
		return noOfQuestionsAttempted;
	}
	public void setNoOfQuestionsAttempted(int noOfQuestionsAttempted) {
		this.noOfQuestionsAttempted = noOfQuestionsAttempted;
	}
	public String getShowResult() {
		return showResult;
	}
	public void setShowResult(String showResult) {
		this.showResult = showResult;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public String getLastModifiedBy() {
		return lastModifiedBy;
	}
	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}
	public String getLastModifiedDate() {
		return lastModifiedDate;
	}
	public void setLastModifiedDate(String lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	public List<UpgradAssessmentBean> getTestDetails() {
		return testDetails;
	}
	public void setTestDetails(List<UpgradAssessmentBean> testDetails) {
		this.testDetails = testDetails;
	}

}
