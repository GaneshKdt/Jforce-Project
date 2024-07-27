package com.nmims.beans;

import java.io.Serializable;

public class ExamBookingCancelBean implements Serializable{
	//From Exambooking
	private String sapid;
	private String year;
	private String month;
	private String subject;
	private String program;
	private String sem;
	private String respAmount;
	private String booked;
	private String releaseReason;
	private String tranStatus;
	private String lastModifiedDate;
	//From Students
	private String firstName;
	private String lastName;
	private String emailId;
	private String mobile;
	private String centerCode;
	private String centerName;
	private String enrollmentYear;
	private String enrollmentMonth;
	private String validityEndYear;
	private String validityEndMonth;
	//From centers
	private String lc;

	
	public String getSapid() {
		return sapid;
	}

	public void setSapid(String sapid) {
		this.sapid = sapid;
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

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getProgram() {
		return program;
	}

	public void setProgram(String program) {
		this.program = program;
	}

	public String getSem() {
		return sem;
	}

	public void setSem(String sem) {
		this.sem = sem;
	}

	public String getRespAmount() {
		return respAmount;
	}

	public void setRespAmount(String respAmount) {
		this.respAmount = respAmount;
	}

	public String getBooked() {
		return booked;
	}

	public void setBooked(String booked) {
		this.booked = booked;
	}

	public String getReleaseReason() {
		return releaseReason;
	}

	public void setReleaseReason(String releaseReason) {
		this.releaseReason = releaseReason;
	}

	public String getTranStatus() {
		return tranStatus;
	}

	public void setTranStatus(String tranStatus) {
		this.tranStatus = tranStatus;
	}

	public String getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(String lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
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

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getCenterCode() {
		return centerCode;
	}

	public void setCenterCode(String centerCode) {
		this.centerCode = centerCode;
	}

	public String getCenterName() {
		return centerName;
	}

	public void setCenterName(String centerName) {
		this.centerName = centerName;
	}

	public String getValidityEndYear() {
		return validityEndYear;
	}

	public void setValidityEndYear(String validityEndYear) {
		this.validityEndYear = validityEndYear;
	}

	public String getValidityEndMonth() {
		return validityEndMonth;
	}

	public void setValidityEndMonth(String validityEndMonth) {
		this.validityEndMonth = validityEndMonth;
	}

	public String getLc() {
		return lc;
	}

	public void setLc(String lc) {
		this.lc = lc;
	}

	public String getEnrollmentYear() {
		return enrollmentYear;
	}

	public void setEnrollmentYear(String enrollmentYear) {
		this.enrollmentYear = enrollmentYear;
	}

	public String getEnrollmentMonth() {
		return enrollmentMonth;
	}

	public void setEnrollmentMonth(String enrollmentMonth) {
		this.enrollmentMonth = enrollmentMonth;
	}

	
	@Override
	public String toString() {
		return "ExamBookingCancelBean [sapid=" + sapid + ", year=" + year + ", month=" + month + ", subject=" + subject
				+ ", program=" + program + ", sem=" + sem + ", respAmount=" + respAmount + ", booked=" + booked
				+ ", releaseReason=" + releaseReason + ", tranStatus=" + tranStatus + ", lastModifiedDate="
				+ lastModifiedDate + ", firstName=" + firstName + ", lastName=" + lastName + ", emailId=" + emailId
				+ ", mobile=" + mobile + ", centerCode=" + centerCode + ", centerName=" + centerName
				+ ", enrollmentYear=" + enrollmentYear + ", enrollmentMonth=" + enrollmentMonth + ", validityEndYear="
				+ validityEndYear + ", validityEndMonth=" + validityEndMonth + ", lc=" + lc + "]";
	}
	
}