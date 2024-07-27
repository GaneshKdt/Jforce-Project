package com.nmims.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

//spring security related changes rename UpgradAssessmentBean to UpgradAssessmentExamBean
public class UpgradAssessmentExamBean implements Serializable {
	
	@Override
	public String toString() {
		return "UpgradAssessmentBean [id=" + id + ", sapid=" + sapid + ", testId=" + testId + ", attempt=" + attempt
				+ ", testStartedOn=" + testStartedOn + ", remainingTime=" + remainingTime + ", testEndedOn="
				+ testEndedOn + ", testCompleted=" + testCompleted + ", score=" + score + ", testQuestionsApplicable="
				+ testQuestionsApplicable + ", noOfQuestionsAttempted=" + noOfQuestionsAttempted + ", showResult="
				+ showResult + ", createdBy=" + createdBy + ", createdDate=" + createdDate + ", lastModifiedBy="
				+ lastModifiedBy + ", lastModifiedDate=" + lastModifiedDate + ", testQuestionsAnsDetails="
				+ testQuestionsAnsDetails + ", testDetails=" + testDetails + ", batchId=" + batchId +", batchName="+ batchName +"]";
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
	private List<UpgradQuestionAnsweredDetailsBean> testQuestionsAnsDetails;
	private List<UpgradAssessmentExamBean> testDetails ; 
	
//	following fields added to validate test details for upgrad api
	private String testName;
	private String testQuestions;
	private int questionNo;
	private String question;
	private String studentAnswer;
	private String marksObtained;
	private String beforeNormalizeScore;
//	following fields added to Find subject details for upgrad api	
	private String batchName;
	private Integer batchId;
	private String subject;
	private Integer subjectId;
	private Integer acadYear;
	private String acadMonth;
	private Integer examYear;
	private String examMonth;

// 	following field added for counts in Verify Test Data For Upgrad
	private Integer sapIdCount;
	private Integer showResultCountY;
	private Integer attemptCount;
	private Integer copyCaseCount;
	private Integer questionNoCount;
	private Integer expectedSapIdCount;
	
	private Integer maxScore;
	private String name;
	
	private String peerPenalty;
	private String onlinePenalty;
	private String emailId;
	private String mobile;
	
	//added for upgrad_test bean
		private Integer maxQuestnToShow;
		private String showResultsToStudents;
		private Integer maxAttempt;
		private Integer duration;

	private String action;
	
	private Integer questionTypeId;
	
	
	private String remark;
	
	// start added by Abhay For Plagiarism Flag
	private String plagiarised;
	private String attemptStatus;
	// end added by Abhay For Plagiarism Flag
	
	
	public Integer getQuestionTypeId() {
		return questionTypeId;
	}
	public void setQuestionTypeId(Integer questionTypeId) {
		this.questionTypeId = questionTypeId;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	
	}
	
	public Integer getDuration() {
		return duration;
	}
	public void setDuration(Integer duration) {
		this.duration = duration;
	}
	public Integer getMaxAttempt() {
		return maxAttempt;
	}
	public void setMaxAttempt(Integer maxAttempt) {
		this.maxAttempt = maxAttempt;
	}
	public String getShowResultsToStudents() {
		return showResultsToStudents;
	}
	public void setShowResultsToStudents(String showResultsToStudents) {
		this.showResultsToStudents = showResultsToStudents;
	}
	public Integer getMaxQuestnToShow() {
		return maxQuestnToShow;
	}
	public void setMaxQuestnToShow(Integer maxQuestnToShow) {
		this.maxQuestnToShow = maxQuestnToShow;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public String getPeerPenalty() {
		return peerPenalty;
	}
	public void setPeerPenalty(String peerPenalty) {
		this.peerPenalty = peerPenalty;
	}
	public String getOnlinePenalty() {
		return onlinePenalty;
	}
	public void setOnlinePenalty(String onlinePenalty) {
		this.onlinePenalty = onlinePenalty;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getMaxScore() {
		return maxScore;
	}
	public void setMaxScore(Integer maxScore) {
		this.maxScore = maxScore;
	}
	public Integer getSubjectId() {
		return subjectId;
	}
	public void setSubjectId(Integer subjectId) {
		this.subjectId = subjectId;
	}
	public Integer getQuestionNoCount() {
		return questionNoCount;
	}
	public void setQuestionNoCount(Integer questionNoCount) {
		this.questionNoCount = questionNoCount;
	}
	public String getTestName() {
		return testName;
	}
	public void setTestName(String testName) {
		this.testName = testName;
	}
	public Integer getSapIdCount() {
		return sapIdCount;
	}
	public void setSapIdCount(Integer sapIdCount) {
		this.sapIdCount = sapIdCount;
	}
	public Integer getShowResultCountY() {
		return showResultCountY;
	}
	public void setShowResultCountY(Integer showResultCountY) {
		this.showResultCountY = showResultCountY;
	}
	
	public Integer getAttemptCount() {
		return attemptCount;
	}
	public void setAttemptCount(Integer attemptCount) {
		this.attemptCount = attemptCount;
	}
	public String getBatchName() {
		return batchName;
	}
	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}
	public Integer getBatchId() {
		return batchId;
	}
	public void setBatchId(Integer batchId) {
		this.batchId = batchId;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public Integer getAcadYear() {
		return acadYear;
	}
	public void setAcadYear(Integer acadYear) {
		this.acadYear = acadYear;
	}
	public String getAcadMonth() {
		return acadMonth;
	}
	public void setAcadMonth(String acadMonth) {
		this.acadMonth = acadMonth;
	}
	public Integer getExamYear() {
		return examYear;
	}
	public void setExamYear(Integer examYear) {
		this.examYear = examYear;
	}
	public String getExamMonth() {
		return examMonth;
	}
	public void setExamMonth(String examMonth) {
		this.examMonth = examMonth;
	}
	public String getBeforeNormalizeScore() {
		return beforeNormalizeScore;
	}
	public void setBeforeNormalizeScore(String beforeNormalizeScore) {
		this.beforeNormalizeScore = beforeNormalizeScore;
	}
	public int getQuestionNo() {
		return questionNo;
	}
	public void setQuestionNo(int questionNo) {
		this.questionNo = questionNo;
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getStudentAnswer() {
		return studentAnswer;
	}
	public void setStudentAnswer(String studentAnswer) {
		this.studentAnswer = studentAnswer;
	}
	public String getMarksObtained() {
		return marksObtained;
	}
	public void setMarksObtained(String marksObtained) {
		this.marksObtained = marksObtained;
	}
	private JSONObject validatedTestDetails;
//
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
	public List<UpgradAssessmentExamBean> getTestDetails() {
		return testDetails;
	}
	public void setTestDetails(List<UpgradAssessmentExamBean> testDetails) {
		this.testDetails = testDetails;
	}
	public List<UpgradQuestionAnsweredDetailsBean> getTestQuestionsAnsDetails() {
		return testQuestionsAnsDetails;
	}
	public void setTestQuestionsAnsDetails( List<UpgradQuestionAnsweredDetailsBean> testQuestionsAnsDetails) {
		this.testQuestionsAnsDetails = testQuestionsAnsDetails;
	}
	public String getTestQuestions() {
		return testQuestions;
	}
	public void setTestQuestions(String testQuestions) {
		this.testQuestions = testQuestions;
	}
//	public String getQuestion() {
//		return question;
//	}
//	public void setQuestion(String question) {
//		this.question = question;
//	}
//	public int getQuestionNo() {
//		return questionNo;
//	}
//	public void setQuestionNo(int questionNo) {
//		this.questionNo = questionNo;
//	}
//	public String getStudentAnswer() {
//		return studentAnswer;
//	}
//	public void setStudentAnswer(String studentAnswer) {
//		this.studentAnswer = studentAnswer;
//	}
//	public String getMarksObtained() {
//		return marksObtained;
//	}
//	public void setMarksObtained(String marksObtained) {
//		this.marksObtained = marksObtained;
//	}
	public JSONObject getValidatedTestDetails() {
		return validatedTestDetails;
	}
	public void setValidatedTestDetails(JSONObject validatedTestDetails) {
		this.validatedTestDetails = validatedTestDetails;
	}
	public Integer getExpectedSapIdCount() {
		return expectedSapIdCount;
	}
	public void setExpectedSapIdCount(Integer expectedSapIdCount) {
		this.expectedSapIdCount = expectedSapIdCount;
	}
	public String getPlagiarised() {
		return plagiarised;
	}
	public void setPlagiarised(String plagiarised) {
		this.plagiarised = plagiarised;
	}
	public String getAttemptStatus() {
		return attemptStatus;
	}
	public void setAttemptStatus(String attemptStatus) {
		this.attemptStatus = attemptStatus;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Integer getCopyCaseCount() {
		return copyCaseCount;
	}
	public void setCopyCaseCount(Integer copyCaseCount) {
		this.copyCaseCount = copyCaseCount;
	}
	
	
	
}