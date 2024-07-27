package com.nmims.beans;

import java.io.Serializable;

public class SearchTimeBoundContent implements Serializable
{
	private String createdDate;
	private String lastModifiedDate;
	private String delayDays;
	private String sessionId;
	private String sessionName;
	private String month;
	private String year;
	private String startTime;
	private String day;
	private String subject;
	private String subjectcode;
	private String facultyId;
	private String facultyLocation;
	private String track;
	private String hasModuleId;
	private String subjectCodeId;
	private String date;
	
	private String facultyName;
	private String facultyEmail;
	private String altFacultyName;
	private String altFacultyId;
	private String altFacultyEmail;
	private String contentName;
	private String corporateName;
	private String programSemSubjectId;
	private String batchName;
	private String moduleid;
	private String batchId;
	
	
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public String getLastModifiedDate() {
		return lastModifiedDate;
	}
	public void setLastModifiedDate(String lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	public String getDelayDays() {
		return delayDays;
	}
	public void setDelayDays(String delayDays) {
		this.delayDays = delayDays;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public String getSessionName() {
		return sessionName;
	}
	public void setSessionName(String sessionName) {
		this.sessionName = sessionName;
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
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getSubjectcode() {
		return subjectcode;
	}
	public void setSubjectcode(String subjectcode) {
		this.subjectcode = subjectcode;
	}
	public String getFacultyId() {
		return facultyId;
	}
	public void setFacultyId(String facultyId) {
		this.facultyId = facultyId;
	}
	public String getFacultyLocation() {
		return facultyLocation;
	}
	public void setFacultyLocation(String facultyLocation) {
		this.facultyLocation = facultyLocation;
	}
	public String getTrack() {
		return track;
	}
	public void setTrack(String track) {
		this.track = track;
	}
	public String getHasModuleId() {
		return hasModuleId;
	}
	public void setHasModuleId(String hasModuleId) {
		this.hasModuleId = hasModuleId;
	}
	
	
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getSubjectCodeId() {
		return subjectCodeId;
	}
	public void setSubjectCodeId(String subjectCodeId) {
		this.subjectCodeId = subjectCodeId;
	}
	
	public String getFacultyName() {
		return facultyName;
	}
	public void setFacultyName(String facultyName) {
		this.facultyName = facultyName;
	}
	public String getFacultyEmail() {
		return facultyEmail;
	}
	public void setFacultyEmail(String facultyEmail) {
		this.facultyEmail = facultyEmail;
	}
	public String getAltFacultyName() {
		return altFacultyName;
	}
	public void setAltFacultyName(String altFacultyName) {
		this.altFacultyName = altFacultyName;
	}
	public String getAltFacultyEmail() {
		return altFacultyEmail;
	}
	public void setAltFacultyEmail(String altFacultyEmail) {
		this.altFacultyEmail = altFacultyEmail;
	}
	public String getContentName() {
		return contentName;
	}
	public void setContentName(String contentName) {
		this.contentName = contentName;
	}
	public String getCorporateName() {
		return corporateName;
	}
	public void setCorporateName(String corporateName) {
		this.corporateName = corporateName;
	}
	public String getAltFacultyId() {
		return altFacultyId;
	}
	public void setAltFacultyId(String altFacultyId) {
		this.altFacultyId = altFacultyId;
	}
	public String getProgramSemSubjectId() {
		return programSemSubjectId;
	}
	public void setProgramSemSubjectId(String programSemSubjectId) {
		this.programSemSubjectId = programSemSubjectId;
	}
	public String getBatchName() {
		return batchName;
	}
	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}

	
	public String getBatchId() {
		return batchId;
	}
	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}
	public String getModuleid() {
		return moduleid;
	}
	public void setModuleid(String moduleid) {
		this.moduleid = moduleid;
	}
	@Override
	public String toString() {
		return "SearchTimeBoundContent [createdDate=" + createdDate + ", lastModifiedDate=" + lastModifiedDate
				+ ", delayDays=" + delayDays + ", sessionId=" + sessionId + ", sessionName=" + sessionName + ", month="
				+ month + ", year=" + year + ", startTime=" + startTime + ", day=" + day + ", subject=" + subject
				+ ", subjectcode=" + subjectcode + ", facultyId=" + facultyId + ", facultyLocation=" + facultyLocation
				+ ", track=" + track + ", hasModuleId=" + hasModuleId + ", subjectCodeId=" + subjectCodeId + ", date="
				+ date + ", facultyName=" + facultyName + ", facultyEmail=" + facultyEmail + ", altFacultyName="
				+ altFacultyName + ", altFacultyId=" + altFacultyId + ", altFacultyEmail=" + altFacultyEmail
				+ ", contentName=" + contentName + ", corporateName=" + corporateName + ", programSemSubjectId="
				+ programSemSubjectId + ", batchName=" + batchName + ", moduleid=" + moduleid + ", batchId=" + batchId
				+ "]";
	}


	
	
	

}
