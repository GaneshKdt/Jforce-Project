package com.nmims.beans;

import java.io.Serializable;

public class SessionBean implements Serializable  {
	private String id;
	private String date;
	private String time;
	private String day;
	private String subject;
	private String sessionName;
	private String createdBy;
	private String createdDate;
	private String lastModifiedBy;
	private String lastModifiedDate;
	private String year;
	private String month;
	private String faculty;
	private String meetingKey;
	private String facultyId;
	private String email;
	private Long moduleId;
	
	private String batchName;
	
	private String name;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getBatchName() {
		return batchName;
	}
	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}

	private String altMeetingKey;
	private String altFacultyId;
	
	private String altMeetingKey2;
	private String altFacultyId2;
	
	private String altMeetingKey3;
	private String altFacultyId3;
	
	private String facultyName;
	private String startTime;
	  
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getFacultyName() {
		return facultyName;
	}
	public void setFacultyName(String facultyName) {
		this.facultyName = facultyName;
	}
	public String getAltMeetingKey() {
		return altMeetingKey;
	}
	public void setAltMeetingKey(String altMeetingKey) {
		this.altMeetingKey = altMeetingKey;
	}
	public String getAltFacultyId() {
		return altFacultyId;
	}
	public void setAltFacultyId(String altFacultyId) {
		this.altFacultyId = altFacultyId;
	}
	public String getAltMeetingKey2() {
		return altMeetingKey2;
	}
	public void setAltMeetingKey2(String altMeetingKey2) {
		this.altMeetingKey2 = altMeetingKey2;
	}
	public String getAltFacultyId2() {
		return altFacultyId2;
	}
	public void setAltFacultyId2(String altFacultyId2) {
		this.altFacultyId2 = altFacultyId2;
	}
	public String getAltMeetingKey3() {
		return altMeetingKey3;
	}
	public void setAltMeetingKey3(String altMeetingKey3) {
		this.altMeetingKey3 = altMeetingKey3;
	}
	public String getAltFacultyId3() {
		return altFacultyId3;
	}
	public void setAltFacultyId3(String altFacultyId3) {
		this.altFacultyId3 = altFacultyId3;
	}
	public Long getModuleId() {
		return moduleId;
	}
	public void setModuleId(Long moduleId) {
		this.moduleId = moduleId;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getFacultyId() {
		return facultyId;
	}
	public void setFacultyId(String facultyId) {
		this.facultyId = facultyId;
	}
	public String getMeetingKey() {
		return meetingKey;
	}
	public void setMeetingKey(String meetingKey) {
		this.meetingKey = meetingKey;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
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
	public String getSessionName() {
		return sessionName;
	}
	public void setSessionName(String sessionName) {
		this.sessionName = sessionName;
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
	public String getFaculty() {
		return faculty;
	}
	public void setFaculty(String faculty) {
		this.faculty = faculty;
	}
	
	@Override
	public String toString() {
		return "SessionBean [id=" + id + ", date=" + date + ", time=" + time + ", day=" + day + ", subject=" + subject
				+ ", sessionName=" + sessionName + ", createdBy=" + createdBy + ", createdDate=" + createdDate
				+ ", lastModifiedBy=" + lastModifiedBy + ", lastModifiedDate=" + lastModifiedDate + ", year=" + year
				+ ", month=" + month + ", faculty=" + faculty + ", meetingKey=" + meetingKey + ", facultyId="
				+ facultyId + ", email=" + email + ", moduleId=" + moduleId + ", batchName=" + batchName + ", name="
				+ name + ", altMeetingKey=" + altMeetingKey + ", altFacultyId=" + altFacultyId + ", altMeetingKey2="
				+ altMeetingKey2 + ", altFacultyId2=" + altFacultyId2 + ", altMeetingKey3=" + altMeetingKey3
				+ ", altFacultyId3=" + altFacultyId3 + ", facultyName=" + facultyName + ", startTime=" + startTime
				+ "]";
	}	
}
