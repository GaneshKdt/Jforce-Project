package com.nmims.beans;

import java.io.Serializable;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class ModuleContentAcadsBean  implements Serializable {
	
	/**
	 * Change Name from ModuleContentBean to ModuleContentAcadsBean for serializable issue
	 */
	
	
	private Integer id;
	private Integer moduleId;
	private String subject;
	private String moduleName;
	private String description;
	private String title;
	private String filePath;
	private String createdBy;
	private String lastModifiedBy;
	private String lastModifiedDate;
	private String createdDate;
	private String dueDate;
	private String active;
	private Integer videoId;
	private String startTime;
	private String moduleVideoUrl;
	private Integer percentageCombined;
	private String errorMessage = "";
	private boolean errorRecord = false;
	
	//count of module content
	private Integer noOfModuleDocuments;
	private Integer noOfModuleVideos;
	
	//used for module documents start
	private String documentName;
	private String folderPath;
	private String type;
	private Integer noOfPages;
	private CommonsMultipartFile fileData;
	//used for module documents end
	

	//used for module videos start
	private Integer videoSubtopicId;
	private Integer noOfVideoSubtopics;
	//used for module videos end
	

	//used for  pageViewed start
	private Integer pageViewed;
	private Integer year;
	private String month;
	private String sapId;
	//used for  pageViewed End
	
	private int percentage;
	private int percentComplete;
	private Integer contentId;
	
	private String documentPath;
	private String previewPath;
	private Integer videoPercentage;
	

	
	public Integer getNoOfVideoSubtopics() {
		return noOfVideoSubtopics;
	}
	public void setNoOfVideoSubtopics(Integer noOfVideoSubtopics) {
		this.noOfVideoSubtopics = noOfVideoSubtopics;
	}
	public Integer getNoOfModuleDocuments() {
		return noOfModuleDocuments;
	}
	public void setNoOfModuleDocuments(Integer noOfModuleDocuments) {
		this.noOfModuleDocuments = noOfModuleDocuments;
	}
	public Integer getNoOfModuleVideos() {
		return noOfModuleVideos;
	}
	public void setNoOfModuleVideos(Integer noOfModuleVideos) {
		this.noOfModuleVideos = noOfModuleVideos;
	}
	public Integer getVideoPercentage() {
		return videoPercentage;
	}
	public void setVideoPercentage(Integer videoPercentage) {
		this.videoPercentage = videoPercentage;
	}
	
	public Integer getPercentageCombined() {
		return percentageCombined;
	}
	public void setPercentageCombined(Integer percentageCombined) {
		this.percentageCombined = percentageCombined;
	}
	public String getDocumentPath() {
		return documentPath;
	}
	public void setDocumentPath(String documentPath) {
		this.documentPath = documentPath;
	}
	public String getPreviewPath() {
		return previewPath;
	}
	public void setPreviewPath(String previewPath) {
		this.previewPath = previewPath;
	}
	public int getPercentComplete() {
		return percentComplete;
	}
	public void setPercentComplete(int percentComplete) {
		this.percentComplete = percentComplete;
	}
	public Integer getContentId() {
		return contentId;
	}
	public void setContentId(Integer contentId) {
		this.contentId = contentId;
	}
	public int getPercentage() {
		return percentage;
	}
	public void setPercentage(int percentage) {
		this.percentage = percentage;
	}
	public Integer getId() {
		return id;
	}
	public CommonsMultipartFile getFileData() {
		return fileData;
	}
	public void setFileData(CommonsMultipartFile fileData) {
		this.fileData = fileData;
	}
	public void setId(Integer id) {
		this.id = id;
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
	public Integer getVideoId() {
		return videoId;
	}
	public void setVideoId(Integer videoId) {
		this.videoId = videoId;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getModuleVideoUrl() {
		return moduleVideoUrl;
	}
	public void setModuleVideoUrl(String moduleVideoUrl) {
		this.moduleVideoUrl = moduleVideoUrl;
	}
	public Integer getModuleId() {
		return moduleId;
	}
	public void setModuleId(Integer moduleId) {
		this.moduleId = moduleId;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
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
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public String getDueDate() {
		return dueDate;
	}
	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}
	public String getActive() {
		return active;
	}
	public void setActive(String active) {
		this.active = active;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDocumentName() {
		return documentName;
	}
	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}
	public String getFolderPath() {
		return folderPath;
	}
	public void setFolderPath(String folderPath) {
		this.folderPath = folderPath;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Integer getNoOfPages() {
		return noOfPages;
	}
	public void setNoOfPages(Integer noOfPages) {
		this.noOfPages = noOfPages;
	}
	public Integer getVideoSubtopicId() {
		return videoSubtopicId;
	}
	public void setVideoSubtopicId(Integer videoSubtopicId) {
		this.videoSubtopicId = videoSubtopicId;
	}
	public Integer getPageViewed() {
		return pageViewed;
	}
	public void setPageViewed(Integer pageViewed) {
		this.pageViewed = pageViewed;
	}
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getSapId() {
		return sapId;
	}
	public void setSapId(String sapId) {
		this.sapId = sapId;
	}
	
	
	
}
