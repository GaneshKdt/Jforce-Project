package com.nmims.beans;

import java.io.Serializable;

public class ProjectModuleStatusBean  implements Serializable  {
 	
	private String sapid;
	private String module;
	private String facultyId;
	private String facultyName;
	private String subject;
	private String live;
	private int submissionsMade;
	private int maxSubmissions;
	private String submittedFilePath;
	private String selectionActive;
	private String reason;
	private String status;
	private String startDate;
	private String endDate;
	private String error;
	private String payment_applicable;
	private String payment_amount;
	private String score;

	public String getScore() {
		return score;
	}
	public void setScore(String score) {
		this.score = score;
	}
	private boolean allowSubmission;
	
	
	
	public String getPayment_amount() {
		return payment_amount;
	}
	public void setPayment_amount(String payment_amount) {
		this.payment_amount = payment_amount;
	}
	public String getPayment_applicable() {
		return payment_applicable;
	}
	public void setPayment_applicable(String payment_applicable) {
		this.payment_applicable = payment_applicable;
	}
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public String getFacultyId() {
		return facultyId;
	}
	public void setFacultyId(String facultyId) {
		this.facultyId = facultyId;
	}
	public String getFacultyName() {
		return facultyName;
	}
	public void setFacultyName(String facultyName) {
		this.facultyName = facultyName;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public int getSubmissionsMade() {
		return submissionsMade;
	}
	public void setSubmissionsMade(int submissionsMade) {
		this.submissionsMade = submissionsMade;
	}
	public int getMaxSubmissions() {
		return maxSubmissions;
	}
	public void setMaxSubmissions(int maxSubmissions) {
		this.maxSubmissions = maxSubmissions;
	}
	public String getSelectionActive() {
		return selectionActive;
	}
	public void setSelectionActive(String selectionActive) {
		this.selectionActive = selectionActive;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
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
	public String getModule() {
		return module;
	}
	public void setModule(String module) {
		this.module = module;
	}
	public boolean isAllowSubmission() {
		return allowSubmission;
	}
	public void setAllowSubmission(boolean allowSubmission) {
		this.allowSubmission = allowSubmission;
	}
	 
	@Override
	public String toString() {
		return "ProjectModuleStatusBean [sapid=" + sapid + ", module=" + module + ", facultyId=" + facultyId
				+ ", facultyName=" + facultyName + ", subject=" + subject + ", live=" + live + ", submissionsMade="
				+ submissionsMade + ", maxSubmissions=" + maxSubmissions + ", submittedFilePath=" + submittedFilePath
				+ ", selectionActive=" + selectionActive + ", reason=" + reason + ", status=" + status + ", startDate="
				+ startDate + ", endDate=" + endDate + ", error=" + error + ", payment_applicable=" + payment_applicable
				+ ", payment_amount=" + payment_amount + ", score=" + score + ", allowSubmission=" + allowSubmission
				+ "]";
	}
	public String getLive() {
		return live;
	}
	public void setLive(String live) {
		this.live = live;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getSubmittedFilePath() {
		return submittedFilePath;
	}
	public void setSubmittedFilePath(String submittedFilePath) {
		this.submittedFilePath = submittedFilePath;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}
