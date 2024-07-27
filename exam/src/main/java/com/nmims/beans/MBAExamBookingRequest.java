package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

public class MBAExamBookingRequest extends MBAPaymentRequest  implements Serializable  {


	public static final String BOOKING_STATUS_BOOKED = "Y"; 
	public static final String BOOKING_STATUS_RELEASED = "RL"; 
	public static final String BOOKING_STATUS_FAIL = "N"; 
	
	private List<MBAStudentSubjectMarksDetailsBean> selectedSubjects;
	private String subjectName;
	private String year;
	private String month;
	private String timeboundId;
	private String term;
	private String centerName;
	private String centerCity;
	private String centerAddress;
	private String centerMapURL;
	private String examStartDateTime;
	private String examEndDateTime;
	private String slotId;
	private String paymentRecordId;
	private String bookingStatus;
	private String bookingId;
	private String firstName;
	private String lastName;
	private String mobile;
	private String emailId;
	private String sapid;

	private String examDay;
	private String examDate;
	private String examStartTime;
	private String examEndTime;

	private boolean isForSlotChange;
	
	private List<String> bookingsForSeatRelease;

	public List<MBAStudentSubjectMarksDetailsBean> getSelectedSubjects() {
		return selectedSubjects;
	}

	public void setSelectedSubjects(List<MBAStudentSubjectMarksDetailsBean> selectedSubjects) {
		this.selectedSubjects = selectedSubjects;
	}

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
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

	public String getTimeboundId() {
		return timeboundId;
	}

	public void setTimeboundId(String timeboundId) {
		this.timeboundId = timeboundId;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
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

	public String getCenterMapURL() {
		return centerMapURL;
	}

	public void setCenterMapURL(String centerMapURL) {
		this.centerMapURL = centerMapURL;
	}

	public String getExamStartDateTime() {
		return examStartDateTime;
	}

	public void setExamStartDateTime(String examStartDateTime) {
		this.examStartDateTime = examStartDateTime;
	}

	public String getExamEndDateTime() {
		return examEndDateTime;
	}

	public void setExamEndDateTime(String examEndDateTime) {
		this.examEndDateTime = examEndDateTime;
	}

	public String getSlotId() {
		return slotId;
	}

	public void setSlotId(String slotId) {
		this.slotId = slotId;
	}

	public String getPaymentRecordId() {
		return paymentRecordId;
	}

	public void setPaymentRecordId(String paymentRecordId) {
		this.paymentRecordId = paymentRecordId;
	}

	public String getBookingStatus() {
		return bookingStatus;
	}

	public void setBookingStatus(String bookingStatus) {
		this.bookingStatus = bookingStatus;
	}

	public String getBookingId() {
		return bookingId;
	}

	public void setBookingId(String bookingId) {
		this.bookingId = bookingId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getSapid() {
		return sapid;
	}

	public void setSapid(String sapid) {
		this.sapid = sapid;
	}

	public String getExamDay() {
		return examDay;
	}

	public void setExamDay(String examDay) {
		this.examDay = examDay;
	}

	public String getExamDate() {
		return examDate;
	}

	public void setExamDate(String examDate) {
		this.examDate = examDate;
	}

	public String getExamStartTime() {
		return examStartTime;
	}

	public void setExamStartTime(String examStartTime) {
		this.examStartTime = examStartTime;
	}

	public String getExamEndTime() {
		return examEndTime;
	}

	public void setExamEndTime(String examEndTime) {
		this.examEndTime = examEndTime;
	}

	public boolean isForSlotChange() {
		return isForSlotChange;
	}

	public void setForSlotChange(boolean isForSlotChange) {
		this.isForSlotChange = isForSlotChange;
	}

	public List<String> getBookingsForSeatRelease() {
		return bookingsForSeatRelease;
	}

	public void setBookingsForSeatRelease(List<String> bookingsForSeatRelease) {
		this.bookingsForSeatRelease = bookingsForSeatRelease;
	}

	@Override
	public String toString() {
		return "MBAExamBookingRequest [selectedSubjects=" + selectedSubjects + ", subjectName=" + subjectName
				+ ", year=" + year + ", month=" + month + ", timeboundId=" + timeboundId + ", term=" + term
				+ ", centerName=" + centerName + ", centerCity=" + centerCity + ", centerAddress=" + centerAddress
				+ ", centerMapURL=" + centerMapURL + ", examStartDateTime=" + examStartDateTime + ", examEndDateTime="
				+ examEndDateTime + ", slotId=" + slotId + ", paymentRecordId=" + paymentRecordId + ", bookingStatus="
				+ bookingStatus + ", bookingId=" + bookingId + ", firstName=" + firstName + ", lastName=" + lastName
				+ ", mobile=" + mobile + ", emailId=" + emailId + ", sapid=" + sapid + ", examDay=" + examDay
				+ ", examDate=" + examDate + ", examStartTime=" + examStartTime + ", examEndTime=" + examEndTime
				+ ", isForSlotChange=" + isForSlotChange + ", bookingsForSeatRelease=" + bookingsForSeatRelease + "]";
	}
}
