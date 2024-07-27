package com.nmims.beans;

import java.io.Serializable;

public class ParticipantReportBean implements Serializable{
	private String id;
	private String user_id;
	private String name;
	private String user_email;
	private String sapId;
	private String join_time;
	private String leave_time;
	private float duration;
	private float totalDuration;
	
	private int sem;
	private String subject;
	private String subjectName;
	private String sessionName;
	private String date;
	private String webinarId;
	private String sessionType;
	private String sessionId;
	private String facultyId;
	private String sessionDate;
	private String dateFrom;
	private String dateTo;
	private String year;
	private String month;
	private String subjectCode;
	private String subjectCodeId;
	private String acadDateFormat;
	
	public String getWebinarId() {
		return webinarId;
	}
	public void setWebinarId(String webinarId) {
		this.webinarId = webinarId;
	}

	
	public String getId() {
		return id;
	}
	public String getSapId() {
		return sapId;
	}
	public void setSapId(String sapId) {
		this.sapId = sapId;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUser_email() {
		return user_email;
	}
	public void setUser_email(String user_email) {
		this.user_email = user_email;
	}
	public String getJoin_time() {
		return join_time;
	}
	public void setJoin_time(String join_time) {
		this.join_time = join_time;
	}
	public String getLeave_time() {
		return leave_time;
	}
	public void setLeave_time(String leave_time) {
		this.leave_time = leave_time;
	}
	public float getDuration() {
		return duration;
	}
	public void setDuration(float duration) {
		this.duration = duration;
	}
	
	public int getSem() {
		return sem;
	}
	public void setSem(int sem) {
		this.sem = sem;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getSubjectName() {
		return subjectName;
	}
	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	
	public String getSessionName() {
		return sessionName;
	}
	public void setSessionName(String sessionName) {
		this.sessionName = sessionName;
	}
	public float getTotalDuration() {
		return totalDuration;
	}
	public void setTotalDuration(float totalDuration) {
		this.totalDuration = totalDuration;
	}	
	public String getSessionType() {
		return sessionType;
	}
	public void setSessionType(String sessionType) {
		this.sessionType = sessionType;
	}	
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public String getFacultyId() {
		return facultyId;
	}
	public void setFacultyId(String facultyId) {
		this.facultyId = facultyId;
	}
	public String getSessionDate() {
		return sessionDate;
	}
	public void setSessionDate(String sessionDate) {
		this.sessionDate = sessionDate;
	}	
	public String getDateFrom() {
		return dateFrom;
	}
	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}
	public String getDateTo() {
		return dateTo;
	}
	public void setDateTo(String dateTo) {
		this.dateTo = dateTo;
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
	public String getSubjectCode() {
		return subjectCode;
	}
	public void setSubjectCode(String subjectCode) {
		this.subjectCode = subjectCode;
	}
	
	public String getSubjectCodeId() {
		return subjectCodeId;
	}
	public void setSubjectCodeId(String subjectCodeId) {
		this.subjectCodeId = subjectCodeId;
	}
	public String getAcadDateFormat() {
		return acadDateFormat;
	}
	public void setAcadDateFormat(String acadDateFormat) {
		this.acadDateFormat = acadDateFormat;
	}
	@Override
	public String toString() {
		return "ParticipantReportBean [id=" + id + ", user_id=" + user_id + ", name=" + name + ", user_email="
				+ user_email + ", sapId=" + sapId + ", join_time=" + join_time + ", leave_time=" + leave_time
				+ ", duration=" + duration + ", totalduration=" + totalDuration + ", sem=" + sem + ", subject="
				+ subject + ", subjectName=" + subjectName + ", sessionName=" + sessionName + ", date=" + date
				+ ", webinarId=" + webinarId + ", sessionType=" + sessionType + ", sessionId=" + sessionId
				+ ", facultyId=" + facultyId + ", sessionDate=" + sessionDate + ", dateFrom=" + dateFrom + ", dateTo="
				+ dateTo + ", year=" + year + ", month=" + month + "]";
	}
}
