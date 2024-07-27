package com.nmims.beans;

import java.io.Serializable;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class SyllabusBean  implements Serializable  {

	private long id;
	private long subjectCodeMappingId;
	private String subjectname;
	private long sem;
	private String chapter;
	private String title;
	private String topic;
	private String outcomes;
	private String pedagogicalTool;
	private String createdBy;
	private String createdDate;
	private String lastModifiedBy;
	private String lastModifiedDate;
	private CommonsMultipartFile file;
	private boolean errorRecord;
	private String message;
	private String year;
	private String month;
	private String subjectcode;
	private String subjectCodeId;
	
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getSubjectCodeMappingId() {
		return subjectCodeMappingId;
	}
	public void setSubjectCodeMappingId(long subjectCodeMappingId) {
		this.subjectCodeMappingId = subjectCodeMappingId;
	}
	public String getSubjectname() {
		return subjectname;
	}
	public void setSubjectname(String subjectname) {
		this.subjectname = subjectname;
	}
	public long getSem() {
		return sem;
	}
	public void setSem(long sem) {
		this.sem = sem;
	}
	public String getChapter() {
		return chapter;
	}
	public void setChapter(String chapter) {
		this.chapter = chapter;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public String getOutcomes() {
		return outcomes;
	}
	public void setOutcomes(String outcomes) {
		this.outcomes = outcomes;
	}
	public String getPedagogicalTool() {
		return pedagogicalTool;
	}
	public void setPedagogicalTool(String pedagogicalTool) {
		this.pedagogicalTool = pedagogicalTool;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public String getLastModifiedBy() {
		return lastModifiedBy;
	}
	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}
	public String getLastModifiedDate() {
		return lastModifiedDate;
	}
	public void setLastModifiedDate(String lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	public CommonsMultipartFile getFile() {
		return file;
	}
	public void setFile(CommonsMultipartFile file) {
		this.file = file;
	}
	public boolean isErrorRecord() {
		return errorRecord;
	}
	public void setErrorRecord(boolean errorRecord) {
		this.errorRecord = errorRecord;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
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
	public String getSubjectcode() {
		return subjectcode;
	}
	public void setSubjectcode(String subjectcode) {
		this.subjectcode = subjectcode;
	}
	public String getSubjectCodeId() {
		return subjectCodeId;
	}
	public void setSubjectCodeId(String subjectCodeId) {
		this.subjectCodeId = subjectCodeId;
	}
	@Override
	public String toString() {
		return "SyllabusBean [subjectCodeMappingId=" + subjectCodeMappingId + ", subjectname=" + subjectname
				+ ", subjectcode=" + subjectcode + "]";
	}
	
}
