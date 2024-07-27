package com.nmims.beans;

import java.io.Serializable;

public class SessionFeedback implements Serializable {

	private String sessionId;
	private String sessionName;
	private String sessionDate;
	private String sessionType;
	private String facultyName;
	private String facultyId;
	private String feedbackCount;
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
	public String getSessionDate() {
		return sessionDate;
	}
	public void setSessionDate(String sessionDate) {
		this.sessionDate = sessionDate;
	}
	public String getSessionType() {
		return sessionType;
	}
	public void setSessionType(String sessionType) {
		this.sessionType = sessionType;
	}
	public String getFacultyName() {
		return facultyName;
	}
	public void setFacultyName(String facultyName) {
		this.facultyName = facultyName;
	}
	public String getFacultyId() {
		return facultyId;
	}
	public void setFacultyId(String facultyId) {
		this.facultyId = facultyId;
	}
	public String getFeedbackCount() {
		return feedbackCount;
	}
	public void setFeedbackCount(String feedbackCount) {
		this.feedbackCount = feedbackCount;
	}
}
