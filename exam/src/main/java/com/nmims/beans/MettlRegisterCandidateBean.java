/**
 * 
 */
package com.nmims.beans;

import java.io.Serializable;

/**
 * @author vil_m
 *
 */
public class MettlRegisterCandidateBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1892618888136545841L;

	// Data populated from Query or UI
	private String sapId;
	private String firstName;
	private String lastName;
	private String emailAddress;
	private String registrationImage;
	private String candidateImage;
	// compensatoryTimeFlag - Special Needs Student given extra time.
	private boolean compensatoryTimeFlag = Boolean.FALSE.booleanValue();
	private String compensatoryTime;
	
	// Response from Server
	private String status;
	private String message;
	private String email;
	private String url;
	
	private String scheduleAccessKey;
	private String booked;
	private String subject;
	private String month;
	private String year;
	private String examDate;
	private String examTime;
	private String examEndTime;
	
	private String trackId;
	private String testTaken;
	private String sifySubjectCode;
	private String examStartDateTime;
	private String examEndDateTime;
	private String accessStartDateTime;
	private String accessEndDateTime;
	private String reportStartDateTime;
	private String reportFinishDateTime;
	private String scheduleName;
	private String joinURL;
	private String assessmentName;
	private String batchName;

	private String scheduleAccessURL;//If candidateImage is null, general URL with scheduleAccessKey used.
	private Boolean openLinkFlag; //false - Candidate specific URL (ecc=), true scheduleAccessURL (authenticateKey).
	
	private String examCenterName;
	
	public MettlRegisterCandidateBean() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getAssessmentName() {
		return assessmentName;
	}

	public void setAssessmentName(String assessmentName) {
		this.assessmentName = assessmentName;
	}

	public String getBatchName() {
		return batchName;
	}

	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}
	
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getRegistrationImage() {
		return registrationImage;
	}

	public void setRegistrationImage(String registrationImage) {
		this.registrationImage = registrationImage;
	}

	public boolean isCompensatoryTimeFlag() {
		return compensatoryTimeFlag;
	}

	public void setCompensatoryTimeFlag(boolean compensatoryTimeFlag) {
		this.compensatoryTimeFlag = compensatoryTimeFlag;
	}

	public String getCompensatoryTime() {
		return compensatoryTime;
	}

	public void setCompensatoryTime(String compensatoryTime) {
		this.compensatoryTime = compensatoryTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSapId() {
		return sapId;
	}

	public void setSapId(String sapId) {
		this.sapId = sapId;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getCandidateImage() {
		return candidateImage;
	}

	public void setCandidateImage(String candidateImage) {
		this.candidateImage = candidateImage;
	}


	public String getScheduleAccessKey() {
		return scheduleAccessKey;
	}

	public void setScheduleAccessKey(String scheduleAccessKey) {
		this.scheduleAccessKey = scheduleAccessKey;
	}

	public String getBooked() {
		return booked;
	}

	public void setBooked(String booked) {
		this.booked = booked;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
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

	public String getExamEndTime() {
		return examEndTime;
	}

	public void setExamEndTime(String examEndTime) {
		this.examEndTime = examEndTime;
	}

	public String getTrackId() {
		return trackId;
	}

	public void setTrackId(String trackId) {
		this.trackId = trackId;
	}

	public String getTestTaken() {
		return testTaken;
	}

	public void setTestTaken(String testTaken) {
		this.testTaken = testTaken;
	}

	public String getSifySubjectCode() {
		return sifySubjectCode;
	}

	public void setSifySubjectCode(String sifySubjectCode) {
		this.sifySubjectCode = sifySubjectCode;
	}

	public String getExamStartDateTime() {
		return examStartDateTime;
	}

	public void setExamStartDateTime(String examStartDateTime) {
		this.examStartDateTime = examStartDateTime;
	}

	public String getExamEndDateTime() {
		return examEndDateTime;
	}

	public void setExamEndDateTime(String examEndDateTime) {
		this.examEndDateTime = examEndDateTime;
	}

	public String getAccessStartDateTime() {
		return accessStartDateTime;
	}

	public void setAccessStartDateTime(String accessStartDateTime) {
		this.accessStartDateTime = accessStartDateTime;
	}

	public String getAccessEndDateTime() {
		return accessEndDateTime;
	}

	public void setAccessEndDateTime(String accessEndDateTime) {
		this.accessEndDateTime = accessEndDateTime;
	}

	public String getScheduleName() {
		return scheduleName;
	}

	public void setScheduleName(String scheduleName) {
		this.scheduleName = scheduleName;
	}

	public String getJoinURL() {
		return joinURL;
	}

	public void setJoinURL(String joinURL) {
		this.joinURL = joinURL;
	}

	public String getScheduleAccessURL() {
		return scheduleAccessURL;
	}

	public void setScheduleAccessURL(String scheduleAccessURL) {
		this.scheduleAccessURL = scheduleAccessURL;
	}

	public Boolean getOpenLinkFlag() {
		return openLinkFlag;
	}

	public void setOpenLinkFlag(Boolean openLinkFlag) {
		this.openLinkFlag = openLinkFlag;
	}

	public String getExamCenterName() {
		return examCenterName;
	}

	public void setExamCenterName(String examCenterName) {
		this.examCenterName = examCenterName;
	}
	
	public String getReportStartDateTime() {
		return reportStartDateTime;
	}

	public void setReportStartDateTime(String reportStartDateTime) {
		this.reportStartDateTime = reportStartDateTime;
	}

	public String getReportFinishDateTime() {
		return reportFinishDateTime;
	}

	public void setReportFinishDateTime(String reportFinishDateTime) {
		this.reportFinishDateTime = reportFinishDateTime;
	}

	@Override
	public String toString() {
		return "MettlRegisterCandidateBean [sapId=" + sapId + ", firstName=" + firstName + ", lastName=" + lastName
				+ ", emailAddress=" + emailAddress + ", registrationImage=" + registrationImage + ", candidateImage="
				+ candidateImage + ", compensatoryTimeFlag=" + compensatoryTimeFlag + ", compensatoryTime="
				+ compensatoryTime + ", status=" + status + ", message=" + message + ", email=" + email + ", url=" + url
				+ ", scheduleAccessKey=" + scheduleAccessKey + ", booked=" + booked + ", subject=" + subject
				+ ", month=" + month + ", year=" + year + ", examDate=" + examDate + ", examTime=" + examTime
				+ ", examEndTime=" + examEndTime + ", trackId=" + trackId + ", testTaken=" + testTaken
				+ ", sifySubjectCode=" + sifySubjectCode + ", examStartDateTime=" + examStartDateTime
				+ ", examEndDateTime=" + examEndDateTime + ", accessStartDateTime=" + accessStartDateTime
				+ ", accessEndDateTime=" + accessEndDateTime + ", reportStartDateTime=" + reportStartDateTime
				+ ", reportFinishDateTime=" + reportFinishDateTime + ", scheduleName=" + scheduleName + ", joinURL="
				+ joinURL + ", assessmentName=" + assessmentName + ", batchName=" + batchName + ", scheduleAccessURL="
				+ scheduleAccessURL + ", openLinkFlag=" + openLinkFlag + ", examCenterName=" + examCenterName + "]";
	}
	
}