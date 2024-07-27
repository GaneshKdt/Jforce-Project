package com.nmims.beans;

import java.io.Serializable;
import java.util.Date;

public class MBASlotBean  implements Serializable  {
	
	private String examYear;
	private String examMonth;
	
	
	private Long slotId;
	private Long centerId;
	private Long timeboundId;
	private Long timeTableId;
	
	private int capacity;
	private int bookedSlots;
	private int availableSlots;

	private Date examStartDateTime;
	private Date examEndDateTime;
	private String examDate;
	private String examEndTime;
	private String examStartTime;

	private String centerName;
	private String centerCity;
	private String centerAddress;

	private String subjectName;
	private String batchName;
	private String term;
	
	private String scheduleId;
	private String scheduleName;

	private String createdBy;
	private String lastModifiedBy;

	private String error;
	private String active;
	public String getExamYear() {
		return examYear;
	}
	public void setExamYear(String examYear) {
		this.examYear = examYear;
	}
	public String getExamMonth() {
		return examMonth;
	}
	public void setExamMonth(String examMonth) {
		this.examMonth = examMonth;
	}
	public Long getSlotId() {
		return slotId;
	}
	public void setSlotId(Long slotId) {
		this.slotId = slotId;
	}
	public Long getCenterId() {
		return centerId;
	}
	public void setCenterId(Long centerId) {
		this.centerId = centerId;
	}
	public Long getTimeboundId() {
		return timeboundId;
	}
	public void setTimeboundId(Long timeboundId) {
		this.timeboundId = timeboundId;
	}
	public Long getTimeTableId() {
		return timeTableId;
	}
	public void setTimeTableId(Long timeTableId) {
		this.timeTableId = timeTableId;
	}
	public int getCapacity() {
		return capacity;
	}
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
	public int getBookedSlots() {
		return bookedSlots;
	}
	public void setBookedSlots(int bookedSlots) {
		this.bookedSlots = bookedSlots;
	}
	public int getAvailableSlots() {
		return availableSlots;
	}
	public void setAvailableSlots(int availableSlots) {
		this.availableSlots = availableSlots;
	}
	public Date getExamStartDateTime() {
		return examStartDateTime;
	}
	public void setExamStartDateTime(Date examStartDateTime) {
		this.examStartDateTime = examStartDateTime;
	}
	public Date getExamEndDateTime() {
		return examEndDateTime;
	}
	public void setExamEndDateTime(Date examEndDateTime) {
		this.examEndDateTime = examEndDateTime;
	}
	public String getExamDate() {
		return examDate;
	}
	public void setExamDate(String examDate) {
		this.examDate = examDate;
	}
	public String getExamEndTime() {
		return examEndTime;
	}
	public void setExamEndTime(String examEndTime) {
		this.examEndTime = examEndTime;
	}
	public String getExamStartTime() {
		return examStartTime;
	}
	public void setExamStartTime(String examStartTime) {
		this.examStartTime = examStartTime;
	}
	public String getCenterName() {
		return centerName;
	}
	public void setCenterName(String centerName) {
		this.centerName = centerName;
	}
	public String getCenterCity() {
		return centerCity;
	}
	public void setCenterCity(String centerCity) {
		this.centerCity = centerCity;
	}
	public String getCenterAddress() {
		return centerAddress;
	}
	public void setCenterAddress(String centerAddress) {
		this.centerAddress = centerAddress;
	}
	public String getSubjectName() {
		return subjectName;
	}
	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}
	public String getBatchName() {
		return batchName;
	}
	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}
	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
	}
	public String getScheduleId() {
		return scheduleId;
	}
	public void setScheduleId(String scheduleId) {
		this.scheduleId = scheduleId;
	}
	public String getScheduleName() {
		return scheduleName;
	}
	public void setScheduleName(String scheduleName) {
		this.scheduleName = scheduleName;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getLastModifiedBy() {
		return lastModifiedBy;
	}
	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getActive() {
		return active;
	}
	public void setActive(String active) {
		this.active = active;
	}
	@Override
	public String toString() {
		return "MBASlotBean [examYear=" + examYear + ", examMonth=" + examMonth + ", slotId=" + slotId + ", centerId="
				+ centerId + ", timeboundId=" + timeboundId + ", timeTableId=" + timeTableId + ", capacity=" + capacity
				+ ", bookedSlots=" + bookedSlots + ", availableSlots=" + availableSlots + ", examStartDateTime="
				+ examStartDateTime + ", examEndDateTime=" + examEndDateTime + ", examDate=" + examDate
				+ ", examEndTime=" + examEndTime + ", examStartTime=" + examStartTime + ", centerName=" + centerName
				+ ", centerCity=" + centerCity + ", centerAddress=" + centerAddress + ", subjectName=" + subjectName
				+ ", batchName=" + batchName + ", term=" + term + ", scheduleId=" + scheduleId + ", scheduleName="
				+ scheduleName + ", createdBy=" + createdBy + ", lastModifiedBy=" + lastModifiedBy + ", error=" + error
				+ ", active=" + active + "]";
	}
	
}
