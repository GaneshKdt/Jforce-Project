package com.nmims.beans;

import java.io.Serializable;
import java.util.Date;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class MBATimeTableBean  implements Serializable  {

	private Long timeTableId;

	private Date examStartDateTime;
	private Date examEndDateTime;
	private String examDate;
	private String examEndTime;
	private String examStartTime;

	private String program;
	
	private String id;
	private String centerId;
	private String scheduleId;

	private String term;
	private String examYear;
	private String examMonth;
	private Long programSemSubjectId;
	
	private String subjectId;
	private String subjectName;

	private String createdBy;
	private String createdOn;
	private String lastModifiedBy;
	private String lastModifiedOn;

	private String error;
	private String active;

	private CommonsMultipartFile fileData;

	public Long getTimeTableId() {
		return timeTableId;
	}

	public void setTimeTableId(Long timeTableId) {
		this.timeTableId = timeTableId;
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

	public String getProgram() {
		return program;
	}

	public void setProgram(String program) {
		this.program = program;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCenterId() {
		return centerId;
	}

	public void setCenterId(String centerId) {
		this.centerId = centerId;
	}

	public String getScheduleId() {
		return scheduleId;
	}

	public void setScheduleId(String scheduleId) {
		this.scheduleId = scheduleId;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

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

	public Long getProgramSemSubjectId() {
		return programSemSubjectId;
	}

	public void setProgramSemSubjectId(Long programSemSubjectId) {
		this.programSemSubjectId = programSemSubjectId;
	}

	public String getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}

	public String getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public String getLastModifiedOn() {
		return lastModifiedOn;
	}

	public void setLastModifiedOn(String lastModifiedOn) {
		this.lastModifiedOn = lastModifiedOn;
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

	public CommonsMultipartFile getFileData() {
		return fileData;
	}

	public void setFileData(CommonsMultipartFile fileData) {
		this.fileData = fileData;
	}

	@Override
	public String toString() {
		return "MBATimeTableBean [timeTableId=" + timeTableId + ", examStartDateTime=" + examStartDateTime
				+ ", examEndDateTime=" + examEndDateTime + ", examDate=" + examDate + ", examEndTime=" + examEndTime
				+ ", examStartTime=" + examStartTime + ", id=" + id + ", centerId=" + centerId + ", scheduleId="
				+ scheduleId + ", term=" + term + ", examYear=" + examYear + ", examMonth=" + examMonth + ", subjectId="
				+ subjectId + ", subjectName=" + subjectName + ", createdBy=" + createdBy + ", createdOn=" + createdOn
				+ ", lastModifiedBy=" + lastModifiedBy + ", lastModifiedOn=" + lastModifiedOn + ", error=" + error
				+ ", active=" + active + ", fileData=" + fileData + "]";
	}
}
