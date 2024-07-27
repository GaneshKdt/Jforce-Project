package com.nmims.beans;

import java.io.Serializable;

public class PostsAcads  implements Serializable  {
	
	/**
	 * Change Name from Posts to PostsAcads for serializable issue
	 */
	
	private String post_id;
	private String userId;
	private String role;
	private String type;
	private String content;
	private String fileName;
	private String filePath;
	private String fileType;
	private String subject;
	private String sessionDate;
	private long subject_config_id;
	
	private String url;
	private String videoLink;
	private String thumbnailUrl;
	private String mobileUrlHd;
	
	private String scheduleFlag;
	private String hashtags;
	
	private String testUrl;
	private long referenceId;
	private long session_plan_module_id;
	private String contentType;
	
	private String createdBy;
	private String createdDate;
	private String lastModifiedBy;
	private String lastModifiedDate;
	private int visibility;

	private String acadYear;
	private String acadMonth;
	private String examYear;
	private String examMonth;
	

	private Integer timeboundId;
	
	private String scheduledDate;
	
	public Integer getTimeboundId() {
		return timeboundId;
	}
	public String getScheduledDate() {
		return scheduledDate;
	}
	public void setScheduledDate(String scheduledDate) {
		this.scheduledDate = scheduledDate;
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
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getSessionDate() {
		return sessionDate;
	}
	public void setSessionDate(String sessionDate) {
		this.sessionDate = sessionDate;
	}
	public long getSubject_config_id() {
		return subject_config_id;
	}
	public void setSubject_config_id(long subject_config_id) {
		this.subject_config_id = subject_config_id;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getVideoLink() {
		return videoLink;
	}
	public void setVideoLink(String videoLink) {
		this.videoLink = videoLink;
	}
	public String getThumbnailUrl() {
		return thumbnailUrl;
	}
	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}
	public String getMobileUrlHd() {
		return mobileUrlHd;
	}
	public void setMobileUrlHd(String mobileUrlHd) {
		this.mobileUrlHd = mobileUrlHd;
	}
	public String getTestUrl() {
		return testUrl;
	}
	public void setTestUrl(String testUrl) {
		this.testUrl = testUrl;
	}
	public long getReferenceId() {
		return referenceId;
	}
	public void setReferenceId(long referenceId) {
		this.referenceId = referenceId;
	}
	public long getSession_plan_module_id() {
		return session_plan_module_id;
	}
	public void setSession_plan_module_id(long session_plan_module_id) {
		this.session_plan_module_id = session_plan_module_id;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
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
	public String getAcadYear() {
		return acadYear;
	}
	public void setAcadYear(String acadYear) {
		this.acadYear = acadYear;
	}
	public String getAcadMonth() {
		return acadMonth;
	}
	public void setAcadMonth(String acadMonth) {
		this.acadMonth = acadMonth;
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
	public String getScheduleFlag() {
		return scheduleFlag;
	}
	public void setScheduleFlag(String scheduleFlag) {
		this.scheduleFlag = scheduleFlag;
	}
	public String getHashtags() {
		return hashtags;
	}
	public void setHashtags(String hashtags) {
		this.hashtags = hashtags;
	}
	
	@Override
	public String toString() {
		return "PostsAcads [post_id=" + post_id + ", userId=" + userId + ", role=" + role + ", type=" + type
				+ ", fileName=" + fileName + ", filePath=" + filePath + ", fileType=" + fileType + ", subject="
				+ subject + ", sessionDate=" + sessionDate + ", subject_config_id=" + subject_config_id + ", url=" + url
				+ ", videoLink=" + videoLink + ", scheduleFlag=" + scheduleFlag + ", testUrl=" + testUrl
				+ ", referenceId=" + referenceId + ", session_plan_module_id=" + session_plan_module_id + ", acadYear="
				+ acadYear + ", acadMonth=" + acadMonth + ", examYear=" + examYear + ", examMonth=" + examMonth
				+ ", timeboundId=" + timeboundId + "]";
	}
	
}
