package com.nmims.beans;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ExecutiveTimetableBean extends BaseExamBean implements Serializable{
	private String examMonth;
	private String examYear;
	private String subject;
	private String date;
	private String startTime;
	private String endTime;
	private String prgmStructApplicable;
	private String program;
	private String sem;
	private boolean errorRecord = false;
	private String errorMessage = "";
	private String centerId;

	private String enrollmentMonth;
	private Integer enrollmentYear;
	
	
	
	public String getEnrollmentMonth() {
		return enrollmentMonth;
	}
	public void setEnrollmentMonth(String enrollmentMonth) {
		this.enrollmentMonth = enrollmentMonth;
	}
	public Integer getEnrollmentYear() {
		return enrollmentYear;
	}
	public void setEnrollmentYear(Integer enrollmentYear) {
		this.enrollmentYear = enrollmentYear;
	}
	public String getCenterId() {
		return centerId;
	}
	public void setCenterId(String centerId) {
		this.centerId = centerId;
	}

	public boolean isErrorRecord() {
		return errorRecord;
	}
	public void setErrorRecord(boolean errorRecord) {
		this.errorRecord = errorRecord;
	}
	
	public String getSem() {
		return sem;
	}
	public void setSem(String sem) {
		this.sem = sem;
	}
	public String getProgram() {
		return program;
	}
	public void setProgram(String program) {
		this.program = program;
	}
	public String getPrgmStructApplicable() {
		return prgmStructApplicable;
	}
	public void setPrgmStructApplicable(String prgmStructApplicable) {
		this.prgmStructApplicable = prgmStructApplicable;
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
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	@Override
	public String toString() {
		return "SASTimetableBean [examMonth=" + examMonth + ", examYear="
				+ examYear + ", subject=" + subject + ", date=" + date
				+ ", startTime=" + startTime + ", endTime=" + endTime
				+ ", prgmStructApplicable=" + prgmStructApplicable
				+ ", program=" + program + ", sem=" + sem + ", errorRecord="
				+ errorRecord + ", errorMessage=" + errorMessage
				+ ", centerId=" + centerId + "]";
	}


	
	
	
}
