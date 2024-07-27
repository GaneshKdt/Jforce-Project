package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

public class MettlStudentTestInfo implements Serializable  {

	private String sapid;
	private String student_name;
	private String firstName;
	private String lastName;
	private String subject;
	private String scheduleAccessKey;
	private String emailId;
	private String startTime;
	private String endTime;
	private String completionMode;
	private double totalMarks;
	private double maxMarks;
	private double percentile;
	private int attemptTime;
	private String candidateCredibilityIndex;
	private int totalQuestion;
	private int totalCorrectAnswers;
	private int totalUnAnswered;
	private String pdfReport;
	private String htmlReport;
	private String errorQuestionId;
	private double bonusMarks;
	private List<MettlStudentSectionInfo> sections;
	private String error;
	
	public String getErrorQuestionId() {
		return errorQuestionId;
	}
	public void setErrorQuestionId(String errorQuestionId) {
		this.errorQuestionId = errorQuestionId;
	}
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
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
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getScheduleAccessKey() {
		return scheduleAccessKey;
	}
	public void setScheduleAccessKey(String scheduleAccessKey) {
		this.scheduleAccessKey = scheduleAccessKey;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getCompletionMode() {
		return completionMode;
	}
	public void setCompletionMode(String completionMode) {
		this.completionMode = completionMode;
	}
	public double getTotalMarks() {
		return totalMarks;
	}
	public void setTotalMarks(double totalMarks) {
		this.totalMarks = totalMarks;
	}
	public double getMaxMarks() {
		return maxMarks;
	}
	public void setMaxMarks(double maxMarks) {
		this.maxMarks = maxMarks;
	}
	public double getPercentile() {
		return percentile;
	}
	public void setPercentile(double percentile) {
		this.percentile = percentile;
	}
	public int getAttemptTime() {
		return attemptTime;
	}
	public void setAttemptTime(int attemptTime) {
		this.attemptTime = attemptTime;
	}
	public String getCandidateCredibilityIndex() {
		return candidateCredibilityIndex;
	}
	public void setCandidateCredibilityIndex(String candidateCredibilityIndex) {
		this.candidateCredibilityIndex = candidateCredibilityIndex;
	}
	public int getTotalQuestion() {
		return totalQuestion;
	}
	public void setTotalQuestion(int totalQuestion) {
		this.totalQuestion = totalQuestion;
	}
	public int getTotalCorrectAnswers() {
		return totalCorrectAnswers;
	}
	public void setTotalCorrectAnswers(int totalCorrectAnswers) {
		this.totalCorrectAnswers = totalCorrectAnswers;
	}
	public int getTotalUnAnswered() {
		return totalUnAnswered;
	}
	public void setTotalUnAnswered(int totalUnAnswered) {
		this.totalUnAnswered = totalUnAnswered;
	}
	public String getPdfReport() {
		return pdfReport;
	}
	public void setPdfReport(String pdfReport) {
		this.pdfReport = pdfReport;
	}
	public String getHtmlReport() {
		return htmlReport;
	}
	public void setHtmlReport(String htmlReport) {
		this.htmlReport = htmlReport;
	}
	public double getBonusMarks() {
		return bonusMarks;
	}
	public void setBonusMarks(double bonusMarks) {
		this.bonusMarks = bonusMarks;
	}
	public List<MettlStudentSectionInfo> getSections() {
		return sections;
	}
	public void setSections(List<MettlStudentSectionInfo> sections) {
		this.sections = sections;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	@Override
	public String toString() {
		return "MettlStudentTestInfo [sapid=" + sapid + ", firstName=" + firstName + ", lastName=" + lastName
				+ ", subject=" + subject + ", scheduleAccessKey=" + scheduleAccessKey + ", emailId=" + emailId
				+ ", startTime=" + startTime + ", endTime=" + endTime + ", completionMode=" + completionMode
				+ ", totalMarks=" + totalMarks + ", maxMarks=" + maxMarks + ", percentile=" + percentile
				+ ", attemptTime=" + attemptTime + ", candidateCredibilityIndex=" + candidateCredibilityIndex
				+ ", totalQuestion=" + totalQuestion + ", totalCorrectAnswers=" + totalCorrectAnswers
				+ ", totalUnAnswered=" + totalUnAnswered + ", pdfReport=" + pdfReport + ", htmlReport=" + htmlReport
				+ ", bonusMarks=" + bonusMarks + ", sections=" + sections + ", error=" + error + "]";
	}
	public String getStudent_name() {
		return student_name;
	}
	public void setStudent_name(String student_name) {
		this.student_name = student_name;
	}
}
