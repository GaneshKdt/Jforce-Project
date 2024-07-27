package com.nmims.beans;

import java.io.Serializable;

public class Posts   implements Serializable  {
	private String post_id;
	private String userId;
	private String role;
	private String type;
	private String content;
	private String fileName;
	private String filePath;
	private String fileType;
	private String thumbnailUrl;
	private String testUrl;
	private String referenceId;
	private String createdBy;
	private String createdDate;
	private String lastModifiedBy;
	private String lastModifiedDate;
	private int visibility;

	private String subject_config_id;
	private String startDate;
	private String endDate;
	private String active;
	private String category;
	private String attachment1;
	private String attachment2;
	private String attachment3;
	
	private String year;
	private String month;
	

	private Integer timeboundId;
	
	public Integer getTimeboundId() {
		return timeboundId;
	}
	public void setTimeboundId(Integer timeboundId) {
		this.timeboundId = timeboundId;
	}
	public String getPost_id() {
		return post_id;
	}
	public void setPost_id(String post_id) {
		this.post_id = post_id;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public String getThumbnailUrl() {
		return thumbnailUrl;
	}
	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}
	public String getTestUrl() {
		return testUrl;
	}
	public void setTestUrl(String testUrl) {
		this.testUrl = testUrl;
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
	public int getVisibility() {
		return visibility;
	}
	public void setVisibility(int visibility) {
		this.visibility = visibility;
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
	public String getReferenceId() {
		return referenceId;
	}
	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}
	public String getSubject_config_id() {
		return subject_config_id;
	}
	public void setSubject_config_id(String subject_config_id) {
		this.subject_config_id = subject_config_id;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getActive() {
		return active;
	}
	public void setActive(String active) {
		this.active = active;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getAttachment1() {
		return attachment1;
	}
	public void setAttachment1(String attachment1) {
		this.attachment1 = attachment1;
	}
	public String getAttachment2() {
		return attachment2;
	}
	public void setAttachment2(String attachment2) {
		this.attachment2 = attachment2;
	}
	public String getAttachment3() {
		return attachment3;
	}
	public void setAttachment3(String attachment3) {
		this.attachment3 = attachment3;
	}
	@Override
	public String toString() {
		return "Posts [post_id=" + post_id + ", userId=" + userId + ", role=" + role + ", type=" + type + ", content="
				+ content + ", fileName=" + fileName + ", filePath=" + filePath + ", fileType=" + fileType
				+ ", thumbnailUrl=" + thumbnailUrl + ", testUrl=" + testUrl + ", referenceId=" + referenceId
				+ ", createdBy=" + createdBy + ", createdDate=" + createdDate + ", lastModifiedBy=" + lastModifiedBy
				+ ", lastModifiedDate=" + lastModifiedDate + ", visibility=" + visibility + ", subject_config_id="
				+ subject_config_id + ", startDate=" + startDate + ", endDate=" + endDate + ", active=" + active
				+ ", category=" + category + ", attachment1=" + attachment1 + ", attachment2=" + attachment2
				+ ", attachment3=" + attachment3 + ", year=" + year + ", month=" + month + "]";
	}


	



	
	

	


	
}
