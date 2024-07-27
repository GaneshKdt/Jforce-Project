package com.nmims.beans;

import java.io.Serializable;

public class MBAStudentSubjectMarksDetailsBean implements Serializable  {
	
	private String sapid;
	private String timeboundId;
	private String slotId;
	private String subjectName;
	private String term;
	private String year;
	private String month;
	private String bookingAmount;
	private String slotChangeAmount;
	private String trackId;
	private boolean resit;
	private String status;
	private MBAExamBookingRequest previousBookingDetails;
	
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public String getTimeboundId() {
		return timeboundId;
	}
	public void setTimeboundId(String timeboundId) {
		this.timeboundId = timeboundId;
	}
	public String getSlotId() {
		return slotId;
	}
	public void setSlotId(String slotId) {
		this.slotId = slotId;
	}
	public String getSubjectName() {
		return subjectName;
	}
	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}
	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
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
	public String getBookingAmount() {
		return bookingAmount;
	}
	public void setBookingAmount(String bookingAmount) {
		this.bookingAmount = bookingAmount;
	}
	public String getSlotChangeAmount() {
		return slotChangeAmount;
	}
	public void setSlotChangeAmount(String slotChangeAmount) {
		this.slotChangeAmount = slotChangeAmount;
	}
	public String getTrackId() {
		return trackId;
	}
	public void setTrackId(String trackId) {
		this.trackId = trackId;
	}
	public MBAExamBookingRequest getPreviousBookingDetails() {
		return previousBookingDetails;
	}
	public void setPreviousBookingDetails(MBAExamBookingRequest previousBookingDetails) {
		this.previousBookingDetails = previousBookingDetails;
	}
	@Override
	public String toString() {
		return "MBAWXStudentSubjectMarksDetailsBean [sapid=" + sapid + ", timeboundId=" + timeboundId + ", slotId="
				+ slotId + ", subjectName=" + subjectName + ", term=" + term + ", year=" + year + ", month=" + month
				+ ", bookingAmount=" + bookingAmount + ", slotChangeAmount=" + slotChangeAmount + ", trackId=" + trackId
				+ ", previousBookingDetails=" + previousBookingDetails + "]";
	}
	public boolean isResit() {
		return resit;
	}
	public void setResit(boolean resit) {
		this.resit = resit;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
}
