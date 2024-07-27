package com.nmims.beans;

import java.io.Serializable;

public class SessionPlanModulePg implements Serializable{

	private static final long serialVersionUID = 2149415937376045822L;
	
	private int id;
	private String sessionPlanId;
	private int chapterId;
	private String chapter;
	private String title;
	private String topic;
	private String outcomes;
	private String pedagogicalTool;
	private String createdBy;
	private String lastModifiedBy;
	private String lastModifiedDate;
	private Long quizScore;
	private String attemptStatus;
	private int testId;
	private int testReferenceId;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getSessionPlanId() {
		return sessionPlanId;
	}
	public void setSessionPlanId(String sessionPlanId) {
		this.sessionPlanId = sessionPlanId;
	}
	public int getChapterId() {
		return chapterId;
	}
	public void setChapterId(int chapterId) {
		this.chapterId = chapterId;
	}
	public String getChapter() {
		return chapter;
	}
	public void setChapter(String chapter) {
		this.chapter = chapter;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public String getOutcomes() {
		return outcomes;
	}
	public void setOutcomes(String outcomes) {
		this.outcomes = outcomes;
	}
	public String getPedagogicalTool() {
		return pedagogicalTool;
	}
	public void setPedagogicalTool(String pedagogicalTool) {
		this.pedagogicalTool = pedagogicalTool;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
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
	public Long getQuizScore() {
		return quizScore;
	}
	public void setQuizScore(Long quizScore) {
		this.quizScore = quizScore;
	}
	public String getAttemptStatus() {
		return attemptStatus;
	}
	public void setAttemptStatus(String attemptStatus) {
		this.attemptStatus = attemptStatus;
	}
	public int getTestId() {
		return testId;
	}
	public void setTestId(int testId) {
		this.testId = testId;
	}	
	public int getTestReferenceId() {
		return testReferenceId;
	}
	public void setTestReferenceId(int testReferenceId) {
		this.testReferenceId = testReferenceId;
	}
	@Override
	public String toString() {
		return "SessionPlanModulePg [id=" + id + ", sessionPlanId=" + sessionPlanId + ", chapterId=" + chapterId
				+ ", chapter=" + chapter + ", title=" + title + ", topic=" + topic + ", outcomes=" + outcomes
				+ ", pedagogicalTool=" + pedagogicalTool + ", createdBy=" + createdBy + ", lastModifiedBy="
				+ lastModifiedBy + ", lastModifiedDate=" + lastModifiedDate + ", quizScore=" + quizScore
				+ ", attemptStatus=" + attemptStatus + "]";
	}
}
