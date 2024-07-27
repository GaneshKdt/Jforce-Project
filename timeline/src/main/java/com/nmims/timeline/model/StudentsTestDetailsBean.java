package com.nmims.timeline.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="exam.test_student_testdetails", schema="exam")
public class StudentsTestDetailsBean {
	/*
	id, sapid, testId, attempt, attemptStatus, 
	active, testStartedOn, remainingTime, testEndedOn, testCompleted, 
	score, testQuestions, noOfQuestionsAttempted, currentQuestion, showResult, 
	createdBy, createdDate, lastModifiedBy, lastModifiedDate, resultDeclaredOn, 
	attemptStatusModifiedDate, copyCaseMatchedPercentage, countOfRefreshPage
	*/
	@Id  
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long testId;
	private int attempt;
	private String active;
	private String testStartedOn;
	private String testEndedOn;
	private String testCompleted;
	private int score; 
	private String testQuestions;
	private String showResult;
	//added to use same field in all API
	@Transient
	private String showResultsToStudents;
	
	private int remainingTime;
	private int noOfQuestionsAttempted;
	private int currentQuestion;
	
	@Transient
	private Integer maxScore;
	
	@Transient
	private Integer scoreInInteger;
	
	@Transient
	private String testName;
	
	@Transient
	private boolean isScoreSelectedForBestOf7;
	

	@Transient
	private Long sessionPlanId;
	@Transient
	private String topic;
	@Transient
	private String outcomes;
	@Transient
	private String pedagogicalTool;
	@Transient
	private String chapter;
	@Transient
	private Long sessionModuleNo;
	@Transient
	private Integer referenceId;
	

	@Transient
	private String firstName;
	@Transient
	private String lastName;
	@Transient
	private String startDate;
	@Transient
	private String isTestGiven;
	@Transient
	private String subject;
	@Transient
	private String scoreInString;
	@Transient
	private String endDate;

	private double copyCaseMatchedPercentage;


	private String attemptStatus;
	
	private String attemptStatusModifiedDate;
	
	private Integer countOfRefreshPage;
	
	@Transient
	private String consideredForLeadsResult;
	

	private String sapid;
	private String createdBy;
	private String createdDate;
	private String lastModifiedBy;
	private String lastModifiedDate;
	
	private String answersMovedFromCacheToDB;
	
	private Integer noOfAnswersInCache;
	
	
	
	
	public Integer getNoOfAnswersInCache() {
		return noOfAnswersInCache;
	}
	public void setNoOfAnswersInCache(Integer noOfAnswersInCache) {
		this.noOfAnswersInCache = noOfAnswersInCache;
	}
	public String getAnswersMovedFromCacheToDB() {
		return answersMovedFromCacheToDB;
	}
	public void setAnswersMovedFromCacheToDB(String answersMovedFromCacheToDB) {
		this.answersMovedFromCacheToDB = answersMovedFromCacheToDB;
	}
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
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
	public String getConsideredForLeadsResult() {
		return consideredForLeadsResult;
	}
	public void setConsideredForLeadsResult(String consideredForLeadsResult) {
		this.consideredForLeadsResult = consideredForLeadsResult;
	}
	public Integer getCountOfRefreshPage() {
		return countOfRefreshPage;
	}
	public void setCountOfRefreshPage(Integer countOfRefreshPage) {
		this.countOfRefreshPage = countOfRefreshPage;
	}
	public double getCopyCaseMatchedPercentage() {
		return copyCaseMatchedPercentage;
	}
	public void setCopyCaseMatchedPercentage(double copyCaseMatchedPercentage) {
		this.copyCaseMatchedPercentage = copyCaseMatchedPercentage;
	}
	public String getAttemptStatusModifiedDate() {
		return attemptStatusModifiedDate;
	}
	public void setAttemptStatusModifiedDate(String attemptStatusModifiedDate) {
		this.attemptStatusModifiedDate = attemptStatusModifiedDate;
	}
	public String getScoreInString() {
		return scoreInString;
	}
	public void setScoreInString(String scoreInString) {
		this.scoreInString = scoreInString;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getIsTestGiven() {
		return isTestGiven;
	}
	public void setIsTestGiven(String isTestGiven) {
		this.isTestGiven = isTestGiven;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public Integer getReferenceId() {
		return referenceId;
	}
	public void setReferenceId(Integer referenceId) {
		this.referenceId = referenceId;
	}
	public Long getSessionPlanId() {
		return sessionPlanId;
	}
	public void setSessionPlanId(Long sessionPlanId) {
		this.sessionPlanId = sessionPlanId;
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
	public String getChapter() {
		return chapter;
	}
	public void setChapter(String chapter) {
		this.chapter = chapter;
	}
	public Long getSessionModuleNo() {
		return sessionModuleNo;
	}
	public void setSessionModuleNo(Long sessionModuleNo) {
		this.sessionModuleNo = sessionModuleNo;
	}
	public boolean isScoreSelectedForBestOf7() {
		return isScoreSelectedForBestOf7;
	}
	public void setScoreSelectedForBestOf7(boolean isScoreSelectedForBestOf7) {
		this.isScoreSelectedForBestOf7 = isScoreSelectedForBestOf7;
	}
	public String getTestName() {
		return testName;
	}
	public void setTestName(String testName) {
		this.testName = testName;
	}
	public Integer getScoreInInteger() {
		return scoreInInteger;
	}
	public void setScoreInInteger(Integer scoreInInteger) {
		this.scoreInInteger = scoreInInteger;
	}
	public Integer getMaxScore() {
		return maxScore;
	}
	public void setMaxScore(Integer maxScore) {
		this.maxScore = maxScore;
	}
	public int getCurrentQuestion() {
		return currentQuestion;
	}
	public void setCurrentQuestion(int currentQuestion) {
		this.currentQuestion = currentQuestion;
	}
	public int getNoOfQuestionsAttempted() {
		return noOfQuestionsAttempted;
	}
	public void setNoOfQuestionsAttempted(int noOfQuestionsAttempted) {
		this.noOfQuestionsAttempted = noOfQuestionsAttempted;
	}
	public int getRemainingTime() {
		return remainingTime;
	}
	public void setRemainingTime(int remainingTime) {
		this.remainingTime = remainingTime;
	}
	public String getShowResult() {
		return showResult;
	}
	public void setShowResult(String showResult) {
		this.showResult = showResult;
	}
	public String getTestQuestions() {
		return testQuestions;
	}
	public void setTestQuestions(String testQuestions) {
		this.testQuestions = testQuestions;
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
	public int getAttempt() {
		return attempt;
	}
	public void setAttempt(int attempt) {
		this.attempt = attempt;
	}
	public String getActive() {
		return active;
	}
	public void setActive(String active) {
		this.active = active;
	}
	public String getTestStartedOn() {
		return testStartedOn;
	}
	public void setTestStartedOn(String testStartedOn) {
		this.testStartedOn = testStartedOn;
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
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public String getShowResultsToStudents() {
		return showResultsToStudents;
	}
	public void setShowResultsToStudents(String showResultsToStudents) {
		this.showResultsToStudents = showResultsToStudents;
	}
	public String getAttemptStatus() {
		return attemptStatus;
	}
	public void setAttemptStatus(String attemptStatus) {
		this.attemptStatus = attemptStatus;
	}	
	@Override
	public String toString() {
		return "\nStudentsTestDetailsBean [id=" + id + " sapid ; "+ sapid +", testId=" + testId + ", attempt=" + attempt + ", active="
				+ active + ", testStartedOn=" + testStartedOn + ", testEndedOn=" + testEndedOn + ", testCompleted="
				+ testCompleted + ", score=" + score + ", testQuestions=" + testQuestions + ", showResult=" + showResult
				+ ", showResultsToStudents=" + showResultsToStudents + ", remainingTime=" + remainingTime
				+ ", noOfQuestionsAttempted=" + noOfQuestionsAttempted + ", currentQuestion=" + currentQuestion
				+ ", maxScore=" + maxScore + ", scoreInInteger=" + scoreInInteger + ", testName=" + testName
				+ ", isScoreSelectedForBestOf7=" + isScoreSelectedForBestOf7 + ", sessionPlanId=" + sessionPlanId
				+ ", topic=" + topic + ", outcomes=" + outcomes + ", pedagogicalTool=" + pedagogicalTool + ", chapter="
				+ chapter + ", sessionModuleNo=" + sessionModuleNo + ", referenceId=" + referenceId + ", firstName="
				+ firstName + ", lastName=" + lastName + ", startDate=" + startDate + ", isTestGiven=" + isTestGiven
				+ ", subject=" + subject + ", scoreInString=" + scoreInString + ", endDate=" + endDate
				+ ", copyCaseMatchedPercentage=" + copyCaseMatchedPercentage + ", attemptStatus=" + attemptStatus
				+ ", attemptStatusModifiedDate=" + attemptStatusModifiedDate + "]\n";
	}
	
	
	

}
