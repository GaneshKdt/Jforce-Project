package com.nmims.beans;

import java.io.Serializable;

public class ProjectStudentStatus  implements Serializable  {

	private String sapid;
	private String consumerProgramStructureId;
	private String subject;
	private String programSemSubjId;
	private String examYear;
	private String examMonth;
	private String canSubmit;
	private String cantSubmitError;
	private String paymentPending;
	private String hasTitle;
	private ProjectModuleStatusBean titleStatus;
	private String hasSOP;
	private ProjectModuleStatusBean sopStatus;
	private String hasSynopsis;
	private ProjectModuleStatusBean synopsisStatus;
	private String hasSubmission;
	private ProjectModuleStatusBean submissionStatus;

	private String hasViva;
	private ProjectModuleStatusBean vivaStatus;	
	private String error;
	private String titleSelected;
	private String titleSelectionEndDate; 
	private String titleSelectionActive;
	
	public String getTitleSelectionActive() {
		return titleSelectionActive;
	}
	public void setTitleSelectionActive(String titleSelectionActive) {
		this.titleSelectionActive = titleSelectionActive;
	}
	public String getTitleSelectionEndDate() {
		return titleSelectionEndDate;
	}
	public void setTitleSelectionEndDate(String titleSelectionEndDate) {
		this.titleSelectionEndDate = titleSelectionEndDate;
	}
	public String getTitleSelected() {
		return titleSelected;
	}
	public void setTitleSelected(String titleSelected) {
		this.titleSelected = titleSelected;
	}
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public String getConsumerProgramStructureId() {
		return consumerProgramStructureId;
	}
	public void setConsumerProgramStructureId(String consumerProgramStructureId) {
		this.consumerProgramStructureId = consumerProgramStructureId;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getProgramSemSubjId() {
		return programSemSubjId;
	}
	public void setProgramSemSubjId(String programSemSubjId) {
		this.programSemSubjId = programSemSubjId;
	}
	public String getExamYear() {
		return examYear;
	}
	public void setExamYear(String examYear) {
		this.examYear = examYear;
	}
	public String getExamMonth() {
		return examMonth;
	}
	public void setExamMonth(String examMonth) {
		this.examMonth = examMonth;
	}
	public String getCanSubmit() {
		return canSubmit;
	}
	public void setCanSubmit(String canSubmit) {
		this.canSubmit = canSubmit;
	}
	public String getCantSubmitError() {
		return cantSubmitError;
	}
	public void setCantSubmitError(String cantSubmitError) {
		this.cantSubmitError = cantSubmitError;
	}
	public String getPaymentPending() {
		return paymentPending;
	}
	public void setPaymentPending(String paymentPending) {
		this.paymentPending = paymentPending;
	}
	public String getHasTitle() {
		return hasTitle;
	}
	public void setHasTitle(String hasTitle) {
		this.hasTitle = hasTitle;
	}
	public ProjectModuleStatusBean getTitleStatus() {
		return titleStatus;
	}
	public void setTitleStatus(ProjectModuleStatusBean titleStatus) {
		this.titleStatus = titleStatus;
	}
	public String getHasSOP() {
		return hasSOP;
	}
	public void setHasSOP(String hasSOP) {
		this.hasSOP = hasSOP;
	}
	public ProjectModuleStatusBean getSopStatus() {
		return sopStatus;
	}
	public void setSopStatus(ProjectModuleStatusBean sopStatus) {
		this.sopStatus = sopStatus;
	}
	public String getHasSynopsis() {
		return hasSynopsis;
	}
	public void setHasSynopsis(String hasSynopsis) {
		this.hasSynopsis = hasSynopsis;
	}
	public ProjectModuleStatusBean getSynopsisStatus() {
		return synopsisStatus;
	}
	public void setSynopsisStatus(ProjectModuleStatusBean synopsisStatus) {
		this.synopsisStatus = synopsisStatus;
	}
	public String getHasSubmission() {
		return hasSubmission;
	}
	public void setHasSubmission(String hasSubmission) {
		this.hasSubmission = hasSubmission;
	}
	public ProjectModuleStatusBean getSubmissionStatus() {
		return submissionStatus;
	}
	public void setSubmissionStatus(ProjectModuleStatusBean submissionStatus) {
		this.submissionStatus = submissionStatus;
	}
	public String getHasViva() {
		return hasViva;
	}
	public void setHasViva(String hasViva) {
		this.hasViva = hasViva;
	}
	public ProjectModuleStatusBean getVivaStatus() {
		return vivaStatus;
	}
	public void setVivaStatus(ProjectModuleStatusBean vivaStatus) {
		this.vivaStatus = vivaStatus;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	@Override
	public String toString() {
		return "ProjectStudentStatus [sapid=" + sapid + ", consumerProgramStructureId=" + consumerProgramStructureId
				+ ", subject=" + subject + ", programSemSubjId=" + programSemSubjId + ", examYear=" + examYear
				+ ", examMonth=" + examMonth + ", canSubmit=" + canSubmit + ", cantSubmitError=" + cantSubmitError
				+ ", paymentPending=" + paymentPending + ", hasTitle=" + hasTitle + ", titleStatus=" + titleStatus
				+ ", hasSOP=" + hasSOP + ", sopStatus=" + sopStatus + ", hasSynopsis=" + hasSynopsis
				+ ", synopsisStatus=" + synopsisStatus + ", hasSubmission=" + hasSubmission + ", submissionStatus="
				+ submissionStatus + ", hasViva=" + hasViva + ", vivaStatus=" + vivaStatus + ", error=" + error + "]";
	}
}
