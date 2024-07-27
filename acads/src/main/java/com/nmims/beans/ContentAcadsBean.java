package com.nmims.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import com.nmims.factory.ContentFactory.StudentType;

public class ContentAcadsBean implements Serializable {
	
	/**
	 * Change Name from ContentBean to ContentAcadsBean for serializable issue
	 */
	
	
	private String id;
	private String name;
	private String subject;
	private String description;
	private String createdBy;
	private String createdDate;
	private String lastModifiedBy;
	private String lastModifiedDate;
	private String filePath;
	private CommonsMultipartFile fileData;
	private String previewPath;
	private String webFileurl;
	private String urlType;
	private String contentType;
	
	private String year;
	private String month;
	private String programStructure;
	
	private String toYear;
	private String toMonth;
	
	private ArrayList<String> contentToTransfer = new ArrayList<>();
	
	private String consumerProgramStructureId;
	private String programSemSubjectId;
	private Long sessionPlanModuleId;

	private String consumerTypeId;
	private String programStructureId;
	private String programId;
	

	private String consumerType;
	private String program;
	private Integer countOfProgramsApplicableTo;
	
	private String allowedToUpdate;
	private String editSingleContentFromCommonSetup;	
	private String sessionPlanModuleName;
	private String count;
	
	private String bookmarked;
	private String sapId;
	
	private String title;
	
	public String getSessionPlanModuleName() {
		return sessionPlanModuleName;
	}
	public void setSessionPlanModuleName(String sessionPlanModuleName) {
		this.sessionPlanModuleName = sessionPlanModuleName;
	}
	
	/**
	 * @return the editSingleContentFromCommonSetup
	 */
	public String getEditSingleContentFromCommonSetup() {
		return editSingleContentFromCommonSetup;
	}
	/**
	 * @param editSingleContentFromCommonSetup the editSingleContentFromCommonSetup to set
	 */
	public void setEditSingleContentFromCommonSetup(String editSingleContentFromCommonSetup) {
		this.editSingleContentFromCommonSetup = editSingleContentFromCommonSetup;
	}
	/**
	 * @return the allowedToUpdate
	 */
	public String getAllowedToUpdate() {
		return allowedToUpdate;
	}
	/**
	 * @param allowedToUpdate the allowedToUpdate to set
	 */
	public void setAllowedToUpdate(String allowedToUpdate) {
		this.allowedToUpdate = allowedToUpdate;
	}
	/**
	 * @return the countOfProgramsApplicableTo
	 */
	public Integer getCountOfProgramsApplicableTo() {
		return countOfProgramsApplicableTo;
	}
	/**
	 * @param countOfProgramsApplicableTo the countOfProgramsApplicableTo to set
	 */
	public void setCountOfProgramsApplicableTo(Integer countOfProgramsApplicableTo) {
		this.countOfProgramsApplicableTo = countOfProgramsApplicableTo;
	}
	/**
	 * @return the consumerType
	 */
	public String getConsumerType() {
		return consumerType;
	}
	/**
	 * @param consumerType the consumerType to set
	 */
	public void setConsumerType(String consumerType) {
		this.consumerType = consumerType;
	}
	/**
	 * @return the program
	 */
	public String getProgram() {
		return program;
	}
	/**
	 * @param program the program to set
	 */
	public void setProgram(String program) {
		this.program = program;
	}
	/**
	 * @return the programStructureId
	 */
	public String getProgramStructureId() {
		return programStructureId;
	}
	/**
	 * @param programStructureId the programStructureId to set
	 */
	public void setProgramStructureId(String programStructureId) {
		this.programStructureId = programStructureId;
	}
	/**
	 * @return the programId
	 */
	public String getProgramId() {
		return programId;
	}
	/**
	 * @param programId the programId to set
	 */
	public void setProgramId(String programId) {
		this.programId = programId;
	}
	/**
	 * @return the consumerTypeId
	 */
	public String getConsumerTypeId() {
		return consumerTypeId;
	}
	/**
	 * @param consumerTypeId the consumerTypeId to set
	 */
	public void setConsumerTypeId(String consumerTypeId) {
		this.consumerTypeId = consumerTypeId;
	}
	/**
	 * @return the consumerProgramStructureId
	 */
	public String getConsumerProgramStructureId() {
		return consumerProgramStructureId;
	}
	/**
	 * @param consumerProgramStructureId the consumerProgramStructureId to set
	 */
	public void setConsumerProgramStructureId(String consumerProgramStructureId) {
		this.consumerProgramStructureId = consumerProgramStructureId;
	}
	public String getProgramSemSubjectId() {
		return programSemSubjectId;
	}
	public void setProgramSemSubjectId(String programSemSubjectId) {
		this.programSemSubjectId = programSemSubjectId;
	}
	public Long getSessionPlanModuleId() {
		return sessionPlanModuleId;
	}
	public void setSessionPlanModuleId(Long sessionPlanModuleId) {
		this.sessionPlanModuleId = sessionPlanModuleId;
	}
	public ArrayList<String> getContentToTransfer() {
		return contentToTransfer;
	}
	public void setContentToTransfer(ArrayList<String> contentToTransfer) {
		this.contentToTransfer = contentToTransfer;
	}
	public String getToYear() {
		return toYear;
	}
	public void setToYear(String toYear) {
		this.toYear = toYear;
	}
	public String getToMonth() {
		return toMonth;
	}
	public void setToMonth(String toMonth) {
		this.toMonth = toMonth;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
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
	public String getProgramStructure() {
		return programStructure;
	}
	public void setProgramStructure(String programStructure) {
		this.programStructure = programStructure;
	}
	public String getUrlType() {
		return urlType;
	}
	public void setUrlType(String urlType) {
		this.urlType = urlType;
	}
	public String getWebFileurl() {
		return webFileurl;
	}
	public void setWebFileurl(String webFileurl) {
		this.webFileurl = webFileurl;
	}
	public String getPreviewPath() {
		return previewPath;
	}
	public void setPreviewPath(String previewPath) {
		this.previewPath = previewPath;
	}
	public CommonsMultipartFile getFileData() {
		return fileData;
	}
	public void setFileData(CommonsMultipartFile fileData) {
		this.fileData = fileData;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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

	
	public String getBookmarked() {
		return bookmarked;
	}
	public void setBookmarked(String bookmarked) {
		this.bookmarked = bookmarked;
	}
	
	public String getSapId() {
		return sapId;
	}
	public void setSapId(String sapId) {
		this.sapId = sapId;
	}
	
	public String getCount() {
		return count;
	}
	public void setCount(String count) {
		this.count = count;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ContentBean [id=" + id + ", name=" + name + ", subject=" + subject + ", description=" + description
				+ ", createdBy=" + createdBy + ", createdDate=" + createdDate + ", lastModifiedBy=" + lastModifiedBy
				+ ", lastModifiedDate=" + lastModifiedDate + ", filePath=" + filePath + ", fileData=" + fileData
				+ ", previewPath=" + previewPath + ", webFileurl=" + webFileurl + ", urlType=" + urlType
				+ ", contentType=" + contentType + ", year=" + year + ", month=" + month + ", programStructure="
				+ programStructure + ", toYear=" + toYear + ", toMonth=" + toMonth + ", contentToTransfer="
				+ contentToTransfer + ", consumerProgramStructureId=" + consumerProgramStructureId
				+ ", programSemSubjectId=" + programSemSubjectId + ", sessionPlanModuleId=" + sessionPlanModuleId
				+ ", consumerTypeId=" + consumerTypeId + ", programStructureId=" + programStructureId + ", programId="
				+ programId + ", consumerType=" + consumerType + ", program=" + program
				+ ", countOfProgramsApplicableTo=" + countOfProgramsApplicableTo + ", allowedToUpdate="
				+ allowedToUpdate + ", editSingleContentFromCommonSetup=" + editSingleContentFromCommonSetup
				+ ", sessionPlanModuleName=" + sessionPlanModuleName + ", count=" + count + ", bookmarked=" + bookmarked
				+ ", sapId=" + sapId + ", title=" + title + ", subjectCodeId=" + subjectCodeId + ", acadDateFormat="
				+ acadDateFormat + "]";
	}
	
	private String subjectCodeId;

	public String getSubjectCodeId() {
		return subjectCodeId;
	}
	public void setSubjectCodeId(String subjectCodeId) {
		this.subjectCodeId = subjectCodeId;
	}
	

	private String subjectcode;

	public String getSubjectcode() {
		return subjectcode;
	}
	public void setSubjectcode(String subjectcode) {
		this.subjectcode = subjectcode;
	}


	
	//Added For Partitioning

	private String acadDateFormat;

	public String getAcadDateFormat() {
		return acadDateFormat;
	}
	public void setAcadDateFormat(String acadDateFormat) {
		this.acadDateFormat = acadDateFormat;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	private StudentType StudentType;

	public StudentType getStudentType() {
		return StudentType;
	}
	public void setStudentType(StudentType studentType) {
		StudentType = studentType;
	}
	
	private String activeDate;

	public String getActiveDate() {
		return activeDate;
	}
	public void setActiveDate(String activeDate) {
		this.activeDate = activeDate;
	}
	
}

