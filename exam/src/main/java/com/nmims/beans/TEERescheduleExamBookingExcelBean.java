package com.nmims.beans;

import java.io.Serializable;

public class TEERescheduleExamBookingExcelBean implements Serializable {
	private String sapId;
	private String emailId;
	private String subject;
	private String updatedExamDate;
	private String updatedExamTime;
	private String updatedBooked;
	private String oldExamDate;
	private String oldExamTime;
	private String oldBooked;
	private Integer subjectCode;
	private Integer oldScheduleId;
	private Integer newScheduleId;
	private Integer assessmentId;
	private String testName;
	private String newScheduleAccessKey;
	private String oldScheduleAccessKey;
	private String programType;
	
	
	@Override
	public String toString() {
		return "TEERescheduleExamBookingExcelBean [sapId=" + sapId + ", emailId=" + emailId + ", subject=" + subject
				+ ", updatedExamDate=" + updatedExamDate + ", updatedExamTime=" + updatedExamTime + ", updatedBooked="
				+ updatedBooked + ", oldExamDate=" + oldExamDate + ", oldExamTime=" + oldExamTime + ", oldBooked="
				+ oldBooked + ", subjectCode=" + subjectCode + ", oldScheduleId=" + oldScheduleId + ", newScheduleId="
				+ newScheduleId + ", assessmentId=" + assessmentId + ", testName=" + testName
				+ ", newScheduleAccessKey=" + newScheduleAccessKey + ", oldScheduleAccessKey=" + oldScheduleAccessKey
				+ ", programType=" + programType + "]";
	}
	
	
	public String getProgramType() {
		return programType;
	}


	public void setProgramType(String programType) {
		this.programType = programType;
	}


	public String getNewScheduleAccessKey() {
		return newScheduleAccessKey;
	}


	public void setNewScheduleAccessKey(String newScheduleAccessKey) {
		this.newScheduleAccessKey = newScheduleAccessKey;
	}


	public String getOldScheduleAccessKey() {
		return oldScheduleAccessKey;
	}


	public void setOldScheduleAccessKey(String oldScheduleAccessKey) {
		this.oldScheduleAccessKey = oldScheduleAccessKey;
	}


	public String getSapId() {
		return sapId;
	}
	public void setSapId(String sapId) {
		this.sapId = sapId;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getUpdatedExamDate() {
		return updatedExamDate;
	}
	public void setUpdatedExamDate(String updatedExamDate) {
		this.updatedExamDate = updatedExamDate;
	}
	public String getUpdatedExamTime() {
		return updatedExamTime;
	}
	public void setUpdatedExamTime(String updatedExamTime) {
		this.updatedExamTime = updatedExamTime;
	}
	public String getUpdatedBooked() {
		return updatedBooked;
	}
	public void setUpdatedBooked(String updatedBooked) {
		this.updatedBooked = updatedBooked;
	}
	public String getOldExamDate() {
		return oldExamDate;
	}
	public void setOldExamDate(String oldExamDate) {
		this.oldExamDate = oldExamDate;
	}
	public String getOldExamTime() {
		return oldExamTime;
	}
	public void setOldExamTime(String oldExamTime) {
		this.oldExamTime = oldExamTime;
	}
	public String getOldBooked() {
		return oldBooked;
	}
	public void setOldBooked(String oldBooked) {
		this.oldBooked = oldBooked;
	}
	public Integer getSubjectCode() {
		return subjectCode;
	}
	public void setSubjectCode(Integer subjectCode) {
		this.subjectCode = subjectCode;
	}
	public Integer getOldScheduleId() {
		return oldScheduleId;
	}
	public void setOldScheduleId(Integer oldScheduleId) {
		this.oldScheduleId = oldScheduleId;
	}
	
	
	
	public Integer getNewScheduleId() {
		return newScheduleId;
	}


	public void setNewScheduleId(Integer newScheduleId) {
		this.newScheduleId = newScheduleId;
	}


	public Integer getAssessmentId() {
		return assessmentId;
	}
	public void setAssessmentId(Integer assessmentId) {
		this.assessmentId = assessmentId;
	}
	public String getTestName() {
		return testName;
	}
	public void setTestName(String testName) {
		this.testName = testName;
	}
	
	
	
}
