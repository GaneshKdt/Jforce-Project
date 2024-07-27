package com.nmims.beans;

import java.io.Serializable;

public class AssignmentStatusBean extends BaseExamBean  implements Serializable{
	
	private String examMonth;
	private String examYear;
	private String submitted;
	private String sapid;
	private String subject;

	private String errorMessage = "";
	private boolean errorRecord = false;
	private String rowNumber;
	private String errorType;
	
	private String firstName;
	private String lastName;
	private String mobile;
	private String emailId;
	private String altPhone;
	private String centerCode;
	private String centerName;
	private String lc;
	private String program;
	private String enrollmentMonth;
    private String enrollmentYear;
	private String sem;
	
	private String validityEndMonth;
	private String validityEndYear;
	private String isLateral;
	
    
    
	public String getValidityEndMonth() {
		return validityEndMonth;
	}
	public void setValidityEndMonth(String validityEndMonth) {
		this.validityEndMonth = validityEndMonth;
	}
	public String getValidityEndYear() {
		return validityEndYear;
	}
	public void setValidityEndYear(String validityEndYear) {
		this.validityEndYear = validityEndYear;
	}
	public String getSem() {
		return sem;
	}
	public void setSem(String sem) {
		this.sem = sem;
	}
	
	public String getLc() {
		return lc;
	}
	public void setLc(String lc) {
		this.lc = lc;
	}
	public String getEnrollmentMonth() {
		return enrollmentMonth;
	}
	public void setEnrollmentMonth(String enrollmentMonth) {
		this.enrollmentMonth = enrollmentMonth;
	}
	public String getEnrollmentYear() {
		return enrollmentYear;
	}
	public void setEnrollmentYear(String enrollmentYear) {
		this.enrollmentYear = enrollmentYear;
	}
	public String getProgram() {
		return program;
	}
	public void setProgram(String program) {
		this.program = program;
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
	public String getAltPhone() {
		return altPhone;
	}
	public void setAltPhone(String altPhone) {
		this.altPhone = altPhone;
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
	public String getErrorType() {
		return errorType;
	}
	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}
	public String getRowNumber() {
		return rowNumber;
	}
	public void setRowNumber(String rowNumber) {
		this.rowNumber = rowNumber;
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
	public String getExamMonth() {
		return examMonth;
	}
	public void setExamMonth(String examMonth) {
		this.examMonth = examMonth;
	}
	public String getExamYear() {
		return examYear;
	}
	public void setExamYear(String examYear) {
		this.examYear = examYear;
	}
	public String getSubmitted() {
		return submitted;
	}
	public void setSubmitted(String submitted) {
		this.submitted = submitted;
	}
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getIsLateral() {
		return isLateral;
	}
	public void setIsLateral(String isLateral) {
		this.isLateral = isLateral;
	}


}
