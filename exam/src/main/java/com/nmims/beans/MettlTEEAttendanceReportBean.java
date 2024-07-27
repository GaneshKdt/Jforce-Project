package com.nmims.beans;

import java.io.Serializable;

public class MettlTEEAttendanceReportBean implements Serializable{
	//Variable
	private String year;
	private String month;
	private String programName;
	private String programCode;
	private String sapid;
	private String trackId;
	private Integer sifySubjectCode;
	private String subject;
	private Integer sem;
	private String ic;
	private String lc;
	private String centerCode;
	private String centerName;
	private String firstName;
	private String lastName;
	private String emailId;
	private String examDate;
	private String examTime;
	private String examStartDateTime;
	private String examEndDateTime;
	private Integer centerId;
	private String testTaken;
	private String examStatus;
	private String teeAttendance;
	
	//Getters and Setters
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public Integer getSifySubjectCode() {
		return sifySubjectCode;
	}
	public void setSifySubjectCode(Integer sifySubjectCode) {
		this.sifySubjectCode = sifySubjectCode;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public Integer getSem() {
		return sem;
	}
	public void setSem(Integer sem) {
		this.sem = sem;
	}
	public String getIc() {
		return ic;
	}
	public void setIc(String ic) {
		this.ic = ic;
	}
	public String getLc() {
		return lc;
	}
	public void setLc(String lc) {
		this.lc = lc;
	}
	public String getCenterName() {
		return centerName;
	}
	public void setCenterName(String centerName) {
		this.centerName = centerName;
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
	public String getExamDate() {
		return examDate;
	}
	public void setExamDate(String examDate) {
		this.examDate = examDate;
	}
	public String getExamTime() {
		return examTime;
	}
	public void setExamTime(String examTime) {
		this.examTime = examTime;
	}
	public Integer getCenterId() {
		return centerId;
	}
	public void setCenterId(Integer centerId) {
		this.centerId = centerId;
	}
	public String getExamStatus() {
		return examStatus;
	}
	public void setExamStatus(String examStatus) {
		this.examStatus = examStatus;
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
	public String getTrackId() {
		return trackId;
	}
	public void setTrackId(String trackId) {
		this.trackId = trackId;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
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
	public String getTestTaken() {
		return testTaken;
	}
	public void setTestTaken(String testTaken) {
		this.testTaken = testTaken;
	}
	public String getTeeAttendance() {
		return teeAttendance;
	}
	public void setTeeAttendance(String teeAttendance) {
		this.teeAttendance = teeAttendance;
	}
	public String getCenterCode() {
		return centerCode;
	}
	public void setCenterCode(String centerCode) {
		this.centerCode = centerCode;
	}
	public String getProgramName() {
		return programName;
	}
	public void setProgramName(String programName) {
		this.programName = programName;
	}
	public String getProgramCode() {
		return programCode;
	}
	public void setProgramCode(String programCode) {
		this.programCode = programCode;
	}
	
	//To String
	@Override
	public String toString() {
		return "MettlTEEAttendanceReportBean [sapid=" + sapid + ", sifySubjectCode=" + sifySubjectCode + ", subject=" + subject
				+ ", sem=" + sem + ", ic=" + ic + ", lc=" + lc + ", centerName=" + centerName + ", firstName="
				+ firstName + ", lastName=" + lastName + ", examDate="
				+ examDate + ", examTime=" + examTime + ", centerId=" + centerId + ", examStatus=" + examStatus + "]";
	}
	
}
