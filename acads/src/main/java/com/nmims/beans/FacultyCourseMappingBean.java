package com.nmims.beans;

import java.io.Serializable;

public class FacultyCourseMappingBean implements Serializable{
	
	private String id;
	private String year;
	private String month;
	private String subject;
	private String facultyIdPref1;
	private String facultyIdPref2;
	private String facultyIdPref3;
	private String session;
	private String scheduled;
	private String duration;
	private String createdBy;
	private String createdDate;
	private String lastModifiedBy;
	private String lastModifiedDate;
	private String errorMessage = "";
	private boolean errorRecord = false;
	private String isAdditionalSession = "N";
	
	
	public String getIsAdditionalSession() {
		return isAdditionalSession;
	}
	public void setIsAdditionalSession(String isAdditionalSession) {
		this.isAdditionalSession = isAdditionalSession;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getScheduled() {
		return scheduled;
	}
	public void setScheduled(String scheduled) {
		this.scheduled = scheduled;
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
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getFacultyIdPref1() {
		return facultyIdPref1;
	}
	public void setFacultyIdPref1(String facultyIdPref1) {
		this.facultyIdPref1 = facultyIdPref1;
	}
	public String getFacultyIdPref2() {
		return facultyIdPref2;
	}
	public void setFacultyIdPref2(String facultyIdPref2) {
		this.facultyIdPref2 = facultyIdPref2;
	}
	public String getFacultyIdPref3() {
		return facultyIdPref3;
	}
	public void setFacultyIdPref3(String facultyIdPref3) {
		this.facultyIdPref3 = facultyIdPref3;
	}
	public String getSession() {
		return session;
	}
	public void setSession(String session) {
		this.session = session;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
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
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public boolean isErrorRecord() {
		return errorRecord;
	}
	public void setErrorRecord(boolean errorRecord) {
		this.errorRecord = errorRecord;
	}
	
	
	
}
