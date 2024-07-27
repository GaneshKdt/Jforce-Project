package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class UFMNoticeBean implements Serializable {
	public static final String UFM_STAGE_SHOW_CAUSE_AWAITING_STUDENT_RESPONSE = "Show Cause - Awaiting Student Response";
	public static final String UFM_STAGE_SHOW_CAUSE_STUDENT_RESPONDED = "Show Cause - Student Responded";
	public static final String UFM_STAGE_PENALTY_ISSUED = "Penalty Issued";
	public static final String UFM_STAGE_WARNING_ISSUED = "Warning";

	private CommonsMultipartFile fileData;
	private CommonsMultipartFile incidentUploadFileData;
	private int Id;
	private String sapid;
	private String subject;
	private List<UFMNoticeBean> subjectsList;
	private String year;
	private String month;
	private List<UFMIncidentBean> incidentBean;

	private String examDate;
	private String examTime;

	private String stage;
	private String active;

	private String ufmMarkReason;
	private String showCauseGenerationDate;
	private String showCauseDeadline;
	private String showCauseResponse;
	private String showCauseSubmissionDate;
	private String showCauseNoticeURL;
	private String decisionNoticeURL;

	private String createdBy;
	private String createdDate;
	private String lastModifiedBy;
	private String lastModifiedDate;
	private String firstName;
	private String lastName;
	private String lcName;
	private String program;
	private String programStructure;
	private String consumerType;
	private String consumerProgramStructureId;
	private int IncidentRowUpdated;

	private String status;
	private String canSubmitResponse;

	private String emailId;

	private String error;
	// Added for UFM Category by shivam.pandey.EXT
	private String category;
	// Added by shivam.pandey.EXT
	private String icName;

	public List<UFMIncidentBean> getIncidentBean() {
		return incidentBean;
	}

	public void setIncidentBean(List<UFMIncidentBean> incidentBean) {
		this.incidentBean = incidentBean;
	}

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public int getIncidentRowUpdated() {
		return IncidentRowUpdated;
	}

	public void setIncidentRowUpdated(int incidentRowUpdated) {
		IncidentRowUpdated = incidentRowUpdated;
	}

	public CommonsMultipartFile getIncidentUploadFileData() {
		return incidentUploadFileData;
	}

	public void setIncidentUploadFileData(CommonsMultipartFile incidentUploadFileData) {
		this.incidentUploadFileData = incidentUploadFileData;
	}

	public CommonsMultipartFile getFileData() {
		return fileData;
	}

	public void setFileData(CommonsMultipartFile fileData) {
		this.fileData = fileData;
	}

	public String getSapid() {
		return sapid;
	}

	public void setSapid(String sapid) {
		this.sapid = sapid;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getExamDate() {
		return examDate;
	}

	public void setExamDate(String examDate) {
		this.examDate = examDate;
	}

	public String getExamTime() {
		return examTime;
	}

	public void setExamTime(String examTime) {
		this.examTime = examTime;
	}

	public String getStage() {
		return stage;
	}

	public void setStage(String stage) {
		this.stage = stage;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public String getUfmMarkReason() {
		return ufmMarkReason;
	}

	public void setUfmMarkReason(String ufmMarkReason) {
		this.ufmMarkReason = ufmMarkReason;
	}

	public String getShowCauseGenerationDate() {
		return showCauseGenerationDate;
	}

	public void setShowCauseGenerationDate(String showCauseGenerationDate) {
		this.showCauseGenerationDate = showCauseGenerationDate;
	}

	public String getShowCauseDeadline() {
		return showCauseDeadline;
	}

	public void setShowCauseDeadline(String showCauseDeadline) {
		this.showCauseDeadline = showCauseDeadline;
	}

	public String getShowCauseResponse() {
		return showCauseResponse;
	}

	public void setShowCauseResponse(String showCauseResponse) {
		this.showCauseResponse = showCauseResponse;
	}

	public String getShowCauseSubmissionDate() {
		return showCauseSubmissionDate;
	}

	public void setShowCauseSubmissionDate(String showCauseSubmissionDate) {
		this.showCauseSubmissionDate = showCauseSubmissionDate;
	}

	public String getShowCauseNoticeURL() {
		return showCauseNoticeURL;
	}

	public void setShowCauseNoticeURL(String showCauseNoticeURL) {
		this.showCauseNoticeURL = showCauseNoticeURL;
	}

	public String getDecisionNoticeURL() {
		return decisionNoticeURL;
	}

	public void setDecisionNoticeURL(String decisionNoticeURL) {
		this.decisionNoticeURL = decisionNoticeURL;
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

	public String getLcName() {
		return lcName;
	}

	public void setLcName(String lcName) {
		this.lcName = lcName;
	}

	public String getProgram() {
		return program;
	}

	public void setProgram(String program) {
		this.program = program;
	}

	public String getProgramStructure() {
		return programStructure;
	}

	public void setProgramStructure(String programStructure) {
		this.programStructure = programStructure;
	}

	public String getConsumerType() {
		return consumerType;
	}

	public void setConsumerType(String consumerType) {
		this.consumerType = consumerType;
	}

	public String getConsumerProgramStructureId() {
		return consumerProgramStructureId;
	}

	public void setConsumerProgramStructureId(String consumerProgramStructureId) {
		this.consumerProgramStructureId = consumerProgramStructureId;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public List<UFMNoticeBean> getSubjectsList() {
		return subjectsList;
	}

	public void setSubjectsList(List<UFMNoticeBean> subjectsList) {
		this.subjectsList = subjectsList;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCanSubmitResponse() {
		return canSubmitResponse;
	}

	public void setCanSubmitResponse(String canSubmitResponse) {
		this.canSubmitResponse = canSubmitResponse;
	}

	// Added UFM Category Getter and Setter By shivam.pandey.Ext
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	// Added By shivam.pandey.Ext
	public String getIcName() {
		return icName;
	}

	public void setIcName(String icName) {
		this.icName = icName;
	}

	@Override
	public String toString() {
		return "UFMNoticeBean [fileData=" + fileData + ", incidentUploadFileData=" + incidentUploadFileData + ", Id="
				+ Id + ", sapid=" + sapid + ", subject=" + subject + ", subjectsList=" + subjectsList + ", year=" + year
				+ ", month=" + month + ", incidentBean=" + incidentBean + ", examDate=" + examDate + ", examTime="
				+ examTime + ", stage=" + stage + ", active=" + active + ", ufmMarkReason=" + ufmMarkReason
				+ ", showCauseGenerationDate=" + showCauseGenerationDate + ", showCauseDeadline=" + showCauseDeadline
				+ ", showCauseResponse=" + showCauseResponse + ", showCauseSubmissionDate=" + showCauseSubmissionDate
				+ ", showCauseNoticeURL=" + showCauseNoticeURL + ", decisionNoticeURL=" + decisionNoticeURL
				+ ", createdBy=" + createdBy + ", createdDate=" + createdDate + ", lastModifiedBy=" + lastModifiedBy
				+ ", lastModifiedDate=" + lastModifiedDate + ", firstName=" + firstName + ", lastName=" + lastName
				+ ", lcName=" + lcName + ", program=" + program + ", programStructure=" + programStructure
				+ ", consumerType=" + consumerType + ", consumerProgramStructureId=" + consumerProgramStructureId
				+ ", IncidentRowUpdated=" + IncidentRowUpdated + ", status=" + status + ", canSubmitResponse="
				+ canSubmitResponse + ", emailId=" + emailId + ", error=" + error + ", category=" + category
				+ ", icName=" + icName + "]";
	}

	



}
