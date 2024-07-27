package com.nmims.beans;

import java.io.Serializable;

public class ExamBookingMBAWX  implements Serializable {

	private int bookingId;
	private String sapid;
	private String slotId;
	private String timeboundId;
	private String term;
	private String year;
	private String month;
	private String trackId;
	private String bookingStatus;
	private String createdBy;
	private String lastUpdatedBy;
	private String createdOn;
	private String lastUpdatedOn;
	private String password;
	private String examDate;
	private String examStartTime;
	private String examEndTime;
	private String  centerId;
	private String  examYear;
	private String  examMonth;
	private String  address;
	private String subject;
	private String ExamStartDateTime;
	private String examEndDateTime;
	
	
	public String getExamStartDateTime() {
		return ExamStartDateTime;
	}
	public String getExamEndDateTime() {
		return examEndDateTime;
	}
	public void setExamStartDateTime(String examStartDateTime) {
		ExamStartDateTime = examStartDateTime;
	}
	public void setExamEndDateTime(String examEndDateTime) {
		this.examEndDateTime = examEndDateTime;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getExamYear() {
		return examYear;
	}
	public String getExamMonth() {
		return examMonth;
	}
	public void setExamYear(String examYear) {
		this.examYear = examYear;
	}
	public void setExamMonth(String examMonth) {
		this.examMonth = examMonth;
	}
	public String getCenterId() {
		return centerId;
	}
	public void setCenterId(String centerId) {
		this.centerId = centerId;
	}
	public String getExamDate() {
		return examDate;
	}
	public String getExamStartTime() {
		return examStartTime;
	}
	public String getExamEndTime() {
		return examEndTime;
	}
	public void setExamDate(String examDate) {
		this.examDate = examDate;
	}
	public void setExamStartTime(String examStartTime) {
		this.examStartTime = examStartTime;
	}
	public void setExamEndTime(String examEndTime) {
		this.examEndTime = examEndTime;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getBookingId() {
		return bookingId;
	}
	public String getSapid() {
		return sapid;
	}
	public String getSlotId() {
		return slotId;
	}
	public String getTimeboundId() {
		return timeboundId;
	}
	public String getTerm() {
		return term;
	}
	public String getYear() {
		return year;
	}
	public String getMonth() {
		return month;
	}
	public String getTrackId() {
		return trackId;
	}
	public String getBookingStatus() {
		return bookingStatus;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public String getLastUpdatedBy() {
		return lastUpdatedBy;
	}
	public String getCreatedOn() {
		return createdOn;
	}
	public String getLastUpdatedOn() {
		return lastUpdatedOn;
	}
	public void setBookingId(int bookingId) {
		this.bookingId = bookingId;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public void setSlotId(String slotId) {
		this.slotId = slotId;
	}
	public void setTimeboundId(String timeboundId) {
		this.timeboundId = timeboundId;
	}
	public void setTerm(String term) {
		this.term = term;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public void setTrackId(String trackId) {
		this.trackId = trackId;
	}
	public void setBookingStatus(String bookingStatus) {
		this.bookingStatus = bookingStatus;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public void setLastUpdatedBy(String lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}
	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}
	public void setLastUpdatedOn(String lastUpdatedOn) {
		this.lastUpdatedOn = lastUpdatedOn;
	}
	@Override
	public String toString() {
		return "ExamBookingMBAWX [bookingId=" + bookingId + ", sapid=" + sapid + ", slotId=" + slotId + ", timeboundId="
				+ timeboundId + ", term=" + term + ", year=" + year + ", month=" + month + ", trackId=" + trackId
				+ ", bookingStatus=" + bookingStatus + ", createdBy=" + createdBy + ", lastUpdatedBy=" + lastUpdatedBy
				+ ", createdOn=" + createdOn + ", lastUpdatedOn=" + lastUpdatedOn + ", password=" + password
				+ ", examDate=" + examDate + ", examStartTime=" + examStartTime + ", examEndTime=" + examEndTime
				+ ", centerId=" + centerId + ", examYear=" + examYear + ", examMonth=" + examMonth + ", address="
				+ address + ", subject=" + subject + ", ExamStartDateTime=" + ExamStartDateTime + ", examEndDateTime="
				+ examEndDateTime + "]";
	}

	
	
}
