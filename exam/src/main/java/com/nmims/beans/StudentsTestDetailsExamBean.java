package com.nmims.beans;

import java.io.Serializable;

//spring security related changes rename StudentsTestDetailsBean to StudentsTestDetailsExamBean
public class StudentsTestDetailsExamBean extends BaseExamBean  implements Serializable  {
	
	private Long id;
	//sapid is in BaseBean
	private Long testId;
	private int attempt;
	private String active;
	private String testStartedOn;
	private String testEndedOn;
	private String testCompleted;
	private double score; 
	//createdBy, createdDate, lastModifiedBy, lastModifiedDate is in BaseBean
	private String testQuestions;
	private String showResult;
	//added to use same field in all API
	private String showResultsToStudents;
	
	private int remainingTime;
	private int noOfQuestionsAttempted;
	private int currentQuestion;
	private Integer maxScore;
	

	private Double scoreInInteger;
	
	private String testName;
	
	private boolean isScoreSelectedForBestOf7;
	

	private Long sessionPlanId;
	private String topic;
	private String outcomes;
	private String pedagogicalTool;
	private String chapter;
 	private Long sessionModuleNo;
	private Integer referenceId;
	

	private String firstName;
	private String lastName;
	private String startDate;
	private String isTestGiven;
	private String subject;
	private String scoreInString;
	private String endDate;

	private double copyCaseMatchedPercentage;


	private String attemptStatus;
	
	private String attemptStatusModifiedDate;
	
	private Integer countOfRefreshPage;
	
	private String consideredForLeadsResult;
	
	private String acadsMonth;
	private Integer acadsYear;
	private String isCurrentAcadMonthYear;

	private String answersMovedFromCacheToDB;
	
	private Integer noOfAnswersInDB;

	private Integer noOfAnswersInCache;

	private String testEndedStatus;
	

	private String sapidForUrl;

	private String testIdForUrl;
  	private String contactedSupport;
  	private String reason;
  	private String resultDeclaredOn;
	
  	private String noOfRefreshAuditTrails ;
  	
  	private String consumerProgramStructureIdForUrl; // added by Abhay for Program Specific IA Instructions Page 

	public String getNoOfRefreshAuditTrails() {
		return noOfRefreshAuditTrails;
	}
	public void setNoOfRefreshAuditTrails(String noOfRefreshAuditTrails) {
		this.noOfRefreshAuditTrails = noOfRefreshAuditTrails;
	}
	public String getAcadsMonth() {
		return acadsMonth;
	}
	public void setAcadsMonth(String acadsMonth) {
		this.acadsMonth = acadsMonth;
	}
	public Integer getAcadsYear() {
		return acadsYear;
	}
	public void setAcadsYear(Integer acadsYear) {
		this.acadsYear = acadsYear;
	}
	public String getIsCurrentAcadMonthYear() {
		return isCurrentAcadMonthYear;
	}
	public void setIsCurrentAcadMonthYear(String isCurrentAcadMonthYear) {
		this.isCurrentAcadMonthYear = isCurrentAcadMonthYear;
	}	
	
	public String getSapidForUrl() {
		return sapidForUrl;
	}
	public void setSapidForUrl(String sapidForUrl) {
		this.sapidForUrl = sapidForUrl;
	}
	public String getTestIdForUrl() {
		return testIdForUrl;
	}
	public void setTestIdForUrl(String testIdForUrl) {
		this.testIdForUrl = testIdForUrl;
	}
	public String getTestEndedStatus() {
		return testEndedStatus;
	}
	public void setTestEndedStatus(String testEndedStatus) {
		this.testEndedStatus = testEndedStatus;
	}
	public Integer getNoOfAnswersInDB() {
		return noOfAnswersInDB;
	}
	public void setNoOfAnswersInDB(Integer noOfAnswersInDB) {
		this.noOfAnswersInDB = noOfAnswersInDB;
	}
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
	public Double getScoreInInteger() {
		return scoreInInteger;
	}
	public void setScoreInInteger(Double scoreInInteger) {
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
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
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
	public String getContactedSupport() {
		return contactedSupport;
	}
	public void setContactedSupport(String contactedSupport) {
		this.contactedSupport = contactedSupport;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getResultDeclaredOn() {
		return resultDeclaredOn;
	}
	public void setResultDeclaredOn(String resultDeclaredOn) {
		this.resultDeclaredOn = resultDeclaredOn;
	}
	
	public String getConsumerProgramStructureIdForUrl() {
		return consumerProgramStructureIdForUrl;
	}
	public void setConsumerProgramStructureIdForUrl(String consumerProgramStructureIdForUrl) {
		this.consumerProgramStructureIdForUrl = consumerProgramStructureIdForUrl;
	}
	@Override
	public String toString() {
		return "\nStudentsTestDetailsBean [id=" + id + " sapid ; "+ super.getSapid() +", testId=" + testId + ", attempt=" + attempt + ", active="
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
				+ ", attemptStatusModifiedDate=" + attemptStatusModifiedDate + ", testEndedStatus="+testEndedStatus+"]\n";
	}
	
	
	

}
