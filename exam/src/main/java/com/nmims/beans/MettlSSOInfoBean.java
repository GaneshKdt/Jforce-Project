package com.nmims.beans;

import java.io.Serializable;

public class MettlSSOInfoBean implements Serializable  {

	// Student details
	private String sapid;
	private String firstname;
	private String lastname;
	private String emailId;
	private String sem;
	private String mobile;
	private String imageURL;
	
	// Booking details
	private String year;
	private String month;
	private String trackId;
	private String subject;
	private String booked;
	
	// Access and exam details
	private String examStartDateTime;
	private String examEndDateTime;
	private String accessStartDateTime;
	private String accessEndDateTime;
	private String reporting_start_date_time;
	private String reporting_finish_date_time;
	
	// Schedule Details
	private String sifySubjectCode;
	
	private String acessKey;
	private String joinURL;
	private String joinKey;
	private String scheduleId;
	private String assessmentId;
	private String maxScore;
	private String scheduleName;
	private String testTaken;
	private String createdBy;	
	private String createdDateTime;
	
	private String error;
	
	private String formattedDateStringForEmail;
	
	private String examCenterName;
	private String googleMapUrl;
	private Integer centerId;
	
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getCreatedDateTime() {
		return createdDateTime;
	}
	public void setCreatedDateTime(String createdDateTime) {
		this.createdDateTime = createdDateTime;
	}
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public String getSem() {
		return sem;
	}
	public void setSem(String sem) {
		this.sem = sem;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getImageURL() {
		return imageURL;
	}
	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
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
	public String getTrackId() {
		return trackId;
	}
	public void setTrackId(String trackId) {
		this.trackId = trackId;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getBooked() {
		return booked;
	}
	public void setBooked(String booked) {
		this.booked = booked;
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
	public String getSifySubjectCode() {
		return sifySubjectCode;
	}
	public void setSifySubjectCode(String sifySubjectCode) {
		this.sifySubjectCode = sifySubjectCode;
	}
	public String getAcessKey() {
		return acessKey;
	}
	public void setAcessKey(String acessKey) {
		this.acessKey = acessKey;
	}
	public String getJoinURL() {
		return joinURL;
	}
	public void setJoinURL(String joinURL) {
		this.joinURL = joinURL;
	}
	public String getJoinKey() {
		return joinKey;
	}
	public void setJoinKey(String joinKey) {
		this.joinKey = joinKey;
	}
	public String getScheduleId() {
		return scheduleId;
	}
	public void setScheduleId(String scheduleId) {
		this.scheduleId = scheduleId;
	}
	public String getAssessmentId() {
		return assessmentId;
	}
	public void setAssessmentId(String assessmentId) {
		this.assessmentId = assessmentId;
	}
	public String getMaxScore() {
		return maxScore;
	}
	public void setMaxScore(String maxScore) {
		this.maxScore = maxScore;
	}
	public String getScheduleName() {
		return scheduleName;
	}
	public void setScheduleName(String scheduleName) {
		this.scheduleName = scheduleName;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	@Override
	public String toString() {
		return "MettlSSOInfoBean [sapid=" + sapid + ", firstname=" + firstname + ", lastname=" + lastname + ", emailId="
				+ emailId + ", sem=" + sem + ", mobile=" + mobile + ", imageURL=" + imageURL + ", year=" + year
				+ ", month=" + month + ", trackId=" + trackId + ", subject=" + subject + ", booked=" + booked
				+ ", examStartDateTime=" + examStartDateTime + ", examEndDateTime=" + examEndDateTime
				+ ", accessStartDateTime=" + accessStartDateTime + ", accessEndDateTime=" + accessEndDateTime
				+ ", sifySubjectCode=" + sifySubjectCode + ", acessKey=" + acessKey + ", joinURL=" + joinURL
				+ ", joinKey=" + joinKey + ", scheduleId=" + scheduleId + ", assessmentId=" + assessmentId
				+ ", maxScore=" + maxScore + ", scheduleName=" + scheduleName + ", error=" + error + "]";
	}
	public String getTestTaken() {
		return testTaken;
	}
	public void setTestTaken(String testTaken) {
		this.testTaken = testTaken;
	}
	public String getFormattedDateStringForEmail() {
		return formattedDateStringForEmail;
	}
	public void setFormattedDateStringForEmail(String formattedDateStringForEmail) {
		this.formattedDateStringForEmail = formattedDateStringForEmail;
	}
	public String getExamCenterName() {
		return examCenterName;
	}
	public void setExamCenterName(String examCenterName) {
		this.examCenterName = examCenterName;
	}
	public Integer getCenterId() {
		return centerId;
	}
	public void setCenterId(Integer centerId) {
		this.centerId = centerId;
	}
	public String getGoogleMapUrl() {
		return googleMapUrl;
	}
	public void setGoogleMapUrl(String googleMapUrl) {
		this.googleMapUrl = googleMapUrl;
	}
	public String getReporting_start_date_time() {
		return reporting_start_date_time;
	}
	public void setReporting_start_date_time(String reporting_start_date_time) {
		this.reporting_start_date_time = reporting_start_date_time;
	}
	public String getReporting_finish_date_time() {
		return reporting_finish_date_time;
	}
	public void setReporting_finish_date_time(String reporting_finish_date_time) {
		this.reporting_finish_date_time = reporting_finish_date_time;
	}
	
}
