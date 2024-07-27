package com.nmims.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;

//@Table(name="lti.test_lostfocuslogs") 
//spring security related changes rename LostFocusLogBean to LostFocusLogExamBean
public class LostFocusLogExamBean implements Serializable{
	
	private String sapid;
	private String testId;
	private String testName;
	private String initialTimeStamp;
	private String timeAway;
	private String ipAddress;
	private String createdBy;
	private Timestamp createdDate;
	private String lastModifiedBy;
	private Timestamp lastModifiedDate;
	private BigDecimal timeAwayInMins;
	private BigInteger timeAwayInSecs;
	private String emailId;
	private int count;
	private String errorMessaage;
	private String successMessage;
	private boolean error;
	private boolean success;
	private String firstName;
	private String lastName;
	private String mobile;	
	private String subject;
	private String testStartDate;
	private String testEndDate;
	private String batchId;
	private String batchName;
	private String attemptStatus;
	private Double totalTimeAwayInMins;
	private String reason;
	private String program_sem_subject_id;
	private String testEndedStatus;
	private String facultyId;
	
	public BigInteger getTimeAwayInSecs() {
		return timeAwayInSecs;
	}
	public void setTimeAwayInSecs(BigInteger timeAwayInSecs) {
		this.timeAwayInSecs = timeAwayInSecs;
	}
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public String getTestId() {
		return testId;
	}
	public void setTestId(String testId) {
		this.testId = testId;
	}
	public String getTestName() {
		return testName;
	}
	public void setTestName(String testName) {
		this.testName = testName;
	}
	public String getInitialTimeStamp() {
		return initialTimeStamp;
	}
	public void setInitialTimeStamp(String initialTimeStamp) {
		this.initialTimeStamp = initialTimeStamp;
	}
	public String getTimeAway() {
		return timeAway;
	}
	public void setTimeAway(String timeAway) {
		this.timeAway = timeAway;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public Timestamp getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}
	public String getLastModifiedBy() {
		return lastModifiedBy;
	}
	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}
	public BigDecimal getTimeAwayInMins() {
		return timeAwayInMins;
	}
	public void setTimeAwayInMins(BigDecimal timeAwayInMins) {
		this.timeAwayInMins = timeAwayInMins;
	}
	public Timestamp getLastModifiedDate() {
		return lastModifiedDate;
	}
	public void setLastModifiedDate(Timestamp lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public String getErrorMessaage() {
		return errorMessaage;
	}
	public void setErrorMessaage(String errorMessaage) {
		this.errorMessaage = errorMessaage;
	}
	public String getSuccessMessage() {
		return successMessage;
	}
	public void setSuccessMessage(String successMessage) {
		this.successMessage = successMessage;
	}
	public boolean isError() {
		return error;
	}
	public void setError(boolean error) {
		this.error = error;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
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
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getTestStartDate() {
		return testStartDate;
	}
	public void setTestStartDate(String testStartDate) {
		this.testStartDate = testStartDate;
	}
	public String getTestEndDate() {
		return testEndDate;
	}
	public void setTestEndDate(String testEndDate) {
		this.testEndDate = testEndDate;
	}
	public String getBatchId() {
		return batchId;
	}
	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}
	public String getBatchName() {
		return batchName;
	}
	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}
	public String getAttemptStatus() {
		return attemptStatus;
	}
	public void setAttemptStatus(String attemptStatus) {
		this.attemptStatus = attemptStatus;
	}
	public Double getTotalTimeAwayInMins() {
		return totalTimeAwayInMins;
	}
	public void setTotalTimeAwayInMins(Double totalTimeAwayInMins) {
		this.totalTimeAwayInMins = totalTimeAwayInMins;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getProgram_sem_subject_id() {
		return program_sem_subject_id;
	}
	public void setProgram_sem_subject_id(String program_sem_subject_id) {
		this.program_sem_subject_id = program_sem_subject_id;
	}
	public String getTestEndedStatus() {
		return testEndedStatus;
	}
	public void setTestEndedStatus(String testEndedStatus) {
		this.testEndedStatus = testEndedStatus;
	}
	public String getFacultyId() {
		return facultyId;
	}
	public void setFacultyId(String facultyId) {
		this.facultyId = facultyId;
	}
	@Override
	public String toString() {
		return "LostFocusLogBean [sapid=" + sapid + ", testId=" + testId + ", testName=" + testName
				+ ", initialTimeStamp=" + initialTimeStamp + ", timeAway=" + timeAway + ", ipAddress=" + ipAddress
				+ ", createdBy=" + createdBy + ", createdDate=" + createdDate + ", lastModifiedBy=" + lastModifiedBy
				+ ", lastModifiedDate=" + lastModifiedDate + ", timeAwayInMins=" + timeAwayInMins + ", timeAwayInSecs="
				+ timeAwayInSecs + ", emailId=" + emailId + ", count=" + count + ", errorMessaage=" + errorMessaage
				+ ", successMessage=" + successMessage + ", error=" + error + ", success=" + success + ", firstName="
				+ firstName + ", lastName=" + lastName + ", mobile=" + mobile + ", subject=" + subject
				+ ", testStartDate=" + testStartDate + ", testEndDate=" + testEndDate + ", batchId=" + batchId
				+ ", batchName=" + batchName + "]";
	}
}
