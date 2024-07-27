package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

public class searchResultBean implements Serializable{

	private String id;
	private String subject;
	private String description;
	private String createdBy;
	private String createdDate;
	private String lastModifiedBy;
	private String lastModifiedDate;
	private String name;
	private String filePath;
	private String year;
	private String month;
	private String urlType;
	private String programStructure;
	private String previewPath;
	private String webFileurl;
	private String contentType;
	private String webFileUrlDownload;
	private String sessionPlanModuleId;
	private String activeDate;
	private String consumerProgramStructureId;
	private String programSemSubjectId;
	private String subjectcode;
	private String question;
	private String answer;

	
	
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
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
	public String getUrlType() {
		return urlType;
	}
	public void setUrlType(String urlType) {
		this.urlType = urlType;
	}
	public String getProgramStructure() {
		return programStructure;
	}
	public void setProgramStructure(String programStructure) {
		this.programStructure = programStructure;
	}
	public String getPreviewPath() {
		return previewPath;
	}
	public void setPreviewPath(String previewPath) {
		this.previewPath = previewPath;
	}
	public String getWebFileurl() {
		return webFileurl;
	}
	public void setWebFileurl(String webFileurl) {
		this.webFileurl = webFileurl;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public String getWebFileUrlDownload() {
		return webFileUrlDownload;
	}
	public void setWebFileUrlDownload(String webFileUrlDownload) {
		this.webFileUrlDownload = webFileUrlDownload;
	}
	public String getSessionPlanModuleId() {
		return sessionPlanModuleId;
	}
	public void setSessionPlanModuleId(String sessionPlanModuleId) {
		this.sessionPlanModuleId = sessionPlanModuleId;
	}
	public String getActiveDate() {
		return activeDate;
	}
	public void setActiveDate(String activeDate) {
		this.activeDate = activeDate;
	}
	public String getConsumerProgramStructureId() {
		return consumerProgramStructureId;
	}
	public void setConsumerProgramStructureId(String consumerProgramStructureId) {
		this.consumerProgramStructureId = consumerProgramStructureId;
	}
	public String getProgramSemSubjectId() {
		return programSemSubjectId;
	}
	public void setProgramSemSubjectId(String programSemSubjectId) {
		this.programSemSubjectId = programSemSubjectId;
	}
	public String getSubjectcode() {
		return subjectcode;
	}
	public void setSubjectcode(String subjectcode) {
		this.subjectcode = subjectcode;
	}
	

	@Override
	public String toString() {
		return "searchResultBean [id=" + id + ", subject=" + subject + ", description=" + description + ", createdBy="
				+ createdBy + ", createdDate=" + createdDate + ", lastModifiedBy=" + lastModifiedBy
				+ ", lastModifiedDate=" + lastModifiedDate + ", name=" + name + ", filePath=" + filePath + ", year="
				+ year + ", month=" + month + ", urlType=" + urlType + ", programStructure=" + programStructure
				+ ", previewPath=" + previewPath + ", webFileurl=" + webFileurl + ", contentType=" + contentType
				+ ", webFileUrlDownload=" + webFileUrlDownload + ", sessionPlanModuleId=" + sessionPlanModuleId
				+ ", activeDate=" + activeDate + ", consumerProgramStructureId=" + consumerProgramStructureId
				+ ", programSemSubjectId=" + programSemSubjectId + ", subjectcode=" + subjectcode + ", question="
				+ question + ", answer=" + answer  + "]";
	}
}