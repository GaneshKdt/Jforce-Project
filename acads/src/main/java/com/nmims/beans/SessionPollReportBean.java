package com.nmims.beans;

import java.io.Serializable;

public class SessionPollReportBean implements Serializable {
	
	
	private String year;
	private String month;
	private String subject;
	private String date;
	private String facultyId;
	private String meetingKey;
	private String sessionName;
	private String webinarId;//meeting which is created in poll table
	private String title;// Name of the Poll
	
	private String subjectcode;
	private String facultyName;
	private String hostKey;
	private String track;
	private String startTime;
	private String facultyIdPoll;//facultyId who created poll in that meeting Key
	
	private String isLaunched ;
	private String noofQuest;
	private String sessionId;
	
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

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	

	public String getFacultyId() {
		return facultyId;
	}

	public void setFacultyId(String facultyId) {
		this.facultyId = facultyId;
	}

	

	public String getSessionName() {
		return sessionName;
	}

	public void setSessionName(String sessionName) {
		this.sessionName = sessionName;
	}

	public String getMeetingKey() {
		return meetingKey;
	}

	public void setMeetingKey(String meetingKey) {
		this.meetingKey = meetingKey;
	}

	

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubjectcode() {
		return subjectcode;
	}

	public void setSubjectcode(String subjectcode) {
		this.subjectcode = subjectcode;
	}

	public String getFacultyName() {
		return facultyName;
	}

	public void setFacultyName(String facultyName) {
		this.facultyName = facultyName;
	}

	public String getHostKey() {
		return hostKey;
	}

	public void setHostKey(String hostKey) {
		this.hostKey = hostKey;
	}

	public String getTrack() {
		return track;
	}

	public void setTrack(String track) {
		this.track = track;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	

	public String getWebinarId() {
		return webinarId;
	}

	public void setWebinarId(String webinarId) {
		this.webinarId = webinarId;
	}

	

	public String getFacultyIdPoll() {
		return facultyIdPoll;
	}

	public void setFacultyIdPoll(String facultyIdPoll) {
		this.facultyIdPoll = facultyIdPoll;
	}
	
	

	

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public String getIsLaunched() {
		return isLaunched;
	}

	public void setIsLaunched(String isLaunched) {
		this.isLaunched = isLaunched;
	}

	public String getNoofQuest() {
		return noofQuest;
	}

	public void setNoofQuest(String noofQuest) {
		this.noofQuest = noofQuest;
	}

	@Override
	public String toString() {
		return "SessionPollReportBean [year=" + year + ", month=" + month + ", subject=" + subject + ", date=" + date
				+ ", facultyId=" + facultyId + ", meetingKey=" + meetingKey + ", sessionName=" + sessionName
				+ ", webinarId=" + webinarId + ", title=" + title + ", subjectcode=" + subjectcode + ", facultyName="
				+ facultyName + ", hostKey=" + hostKey + ", track=" + track + ", startTime=" + startTime
				+ ", facultyIdPoll=" + facultyIdPoll + ", isLaunched=" + isLaunched + ", noofQuest=" + noofQuest
				+ ", sessionId=" + sessionId + "]";
	}

	
	

	

	

}
