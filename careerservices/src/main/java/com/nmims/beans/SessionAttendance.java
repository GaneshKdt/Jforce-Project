package com.nmims.beans;

import java.io.Serializable;

public class SessionAttendance implements Serializable {
	
	private String id;
	private String sapId;
	private String sessionId;
	private String attended;
	private String attendTime;
	private String reAttendTime;
	
	private String device;
	private String feedbackId;
	
	private String createdBy;
	private String createdDate;
	private String lastModifiedBy;
	private String lastModifiedDate;
	private String errorMessage = "";
	private boolean errorRecord = false;
	
	private boolean feedbackSubmitted = false;
	
	private String facultyId;
	private int remainingSeats;
	
	public String getFacultyId() {
		return facultyId;
	}
	public void setFacultyId(String facultyId) {
		this.facultyId = facultyId;
	}
	public int getRemainingSeats() {
		return remainingSeats;
	}
	public void setRemainingSeats(int remainingSeats) {
		this.remainingSeats = remainingSeats;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSapId() {
		return sapId;
	}
	public void setSapId(String sapId) {
		this.sapId = sapId;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
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
	public String getReAttendTime() {
		return reAttendTime;
	}
	public void setReAttendTime(String reAttendTime) {
		this.reAttendTime = reAttendTime;
	}
	public String getDevice() {
		return device;
	}
	public void setDevice(String device) {
		this.device = device;
	}
	public String getFeedbackId() {
		return feedbackId;
	}
	public void setFeedbackId(String feedbackId) {
		this.feedbackId = feedbackId;
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
	
	@Override
	public String toString() {
		return "SessionAttendanceFeedback [id=" + id + ", sapId=" + sapId + ", sessionId=" + sessionId + ", attended="
				+ attended + ", attendTime=" + attendTime + ", reAttendTime=" + reAttendTime + ", device=" + device
				+ ", feedbackId=" + feedbackId + ", createdBy=" + createdBy + ", createdDate=" + createdDate
				+ ", lastModifiedBy=" + lastModifiedBy + ", lastModifiedDate=" + lastModifiedDate + ", errorMessage="
				+ errorMessage + ", errorRecord=" + errorRecord + "]";
	}
	public boolean isFeedbackSubmitted() {
		return feedbackSubmitted;
	}
	public void setFeedbackSubmitted(boolean feedbackSubmitted) {
		this.feedbackSubmitted = feedbackSubmitted;
	}
	
	
}
