package com.nmims.beans;

public class OpenBadgeLectureAttendanceBean {
	private String subject;
	private Integer year;
	private String month;
	private String sessionName;
	private Integer sessionId;
	private String attended;
	private String attendTime;
	private String sessionTime;
	private String track;
	private Integer program_sem_subject_id;
	private Integer userId;
	private String sapid;	
	
	
	@Override
	public String toString() {
		return "OpenBadgeLectureAttendanceBean [subject=" + subject + ", year=" + year + ", month=" + month
				+ ", sessionName=" + sessionName + ", sessionId=" + sessionId + ", attended=" + attended
				+ ", attendTime=" + attendTime + ", sessionTime=" + sessionTime + ", track=" + track
				+ ", program_sem_subject_id=" + program_sem_subject_id + "]";
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	
	public String getSessionName() {
		return sessionName;
	}
	public void setSessionName(String sessionName) {
		this.sessionName = sessionName;
	}
	public Integer getSessionId() {
		return sessionId;
	}
	public void setSessionId(Integer sessionId) {
		this.sessionId = sessionId;
	}
	public String getAttended() {
		return attended;
	}
	public void setAttended(String attended) {
		this.attended = attended;
	}
	public String getAttendTime() {
		return attendTime;
	}
	public void setAttendTime(String attendTime) {
		this.attendTime = attendTime;
	}
	public String getSessionTime() {
		return sessionTime;
	}
	public void setSessionTime(String sessionTime) {
		this.sessionTime = sessionTime;
	}
	public String getTrack() {
		return track;
	}
	public void setTrack(String track) {
		this.track = track;
	}
	public Integer getProgram_sem_subject_id() {
		return program_sem_subject_id;
	}
	public void setProgram_sem_subject_id(Integer program_sem_subject_id) {
		this.program_sem_subject_id = program_sem_subject_id;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	
}
