package com.nmims.beans;

import java.io.Serializable;
import java.util.Date;

public class Post  implements Serializable  {

	private int post_id;

	private String userId;
	private String role; 
	private String type; 
	private String content;
	private String fileName;
	private String FilePath;
	private String FileType;
	private String testUrl;
	private String videoContentId;
	private String visibility;

	private Date createdDate;
	private String createdBy;
	private Date lastModifiedDate;
	private String lastModifiedBy;

	private int year;
	private String month;
	private String url;
	private String scheduledDate;
	private String scheduledTime;
	
	private String program_sem_subject_id;
	private String consumerType;
	private String programStructure;
	private String program;

	private String session_plan_module_id;
	private String group_id;
	private String hashtag_id;
	private String subject_config_id;

	private Integer timeboundId;
	
	
	
	public Integer getTimeboundId() {
		return timeboundId;
	}
	public void setTimeboundId(Integer timeboundId) {
		this.timeboundId = timeboundId;
	}
	public String getSubject_config_id() {
		return subject_config_id;
	}
	public void setSubject_config_id(String subject_config_id) {
		this.subject_config_id = subject_config_id;
	}
	public int getPost_id() {
		return post_id;
	}
	public void setPost_id(int post_id) {
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
		return FilePath;
	}
	public void setFilePath(String filePath) {
		FilePath = filePath;
	}
	public String getFileType() {
		return FileType;
	}
	public void setFileType(String fileType) {
		FileType = fileType;
	}
	public String getTestUrl() {
		return testUrl;
	}
	public void setTestUrl(String testUrl) {
		this.testUrl = testUrl;
	}
	public String getVideoContentId() {
		return videoContentId;
	}
	public void setVideoContentId(String videoContentId) {
		this.videoContentId = videoContentId;
	}
	public String getVisibility() {
		return visibility;
	}
	public void setVisibility(String visibility) {
		this.visibility = visibility;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}
	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	public String getLastModifiedBy() {
		return lastModifiedBy;
	}
	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getScheduledDate() {
		return scheduledDate;
	}
	public void setScheduledDate(String scheduledDate) {
		this.scheduledDate = scheduledDate;
	}
	public String getScheduledTime() {
		return scheduledTime;
	}
	public void setScheduledTime(String scheduledTime) {
		this.scheduledTime = scheduledTime;
	}
	public String getProgram_sem_subject_id() {
		return program_sem_subject_id;
	}
	public void setProgram_sem_subject_id(String program_sem_subject_id) {
		this.program_sem_subject_id = program_sem_subject_id;
	}
	public String getConsumerType() {
		return consumerType;
	}
	public void setConsumerType(String consumerType) {
		this.consumerType = consumerType;
	}
	public String getProgramStructure() {
		return programStructure;
	}
	public void setProgramStructure(String programStructure) {
		this.programStructure = programStructure;
	}
	public String getProgram() {
		return program;
	}
	public void setProgram(String program) {
		this.program = program;
	}
	public String getSession_plan_module_id() {
		return session_plan_module_id;
	}
	public void setSession_plan_module_id(String session_plan_module_id) {
		this.session_plan_module_id = session_plan_module_id;
	}
	public String getGroup_id() {
		return group_id;
	}
	public void setGroup_id(String group_id) {
		this.group_id = group_id;
	}
	public String getHashtag_id() {
		return hashtag_id;
	}
	public void setHashtag_id(String hashtag_id) {
		this.hashtag_id = hashtag_id;
	}
	@Override
	public String toString() {
		return "Post [post_id=" + post_id + ", userId=" + userId + ", role=" + role + ", type=" + type + ", content="
				+ content + ", fileName=" + fileName + ", FilePath=" + FilePath + ", FileType=" + FileType
				+ ", testUrl=" + testUrl + ", videoContentId=" + videoContentId + ", visibility=" + visibility
				+ ", createdDate=" + createdDate + ", createdBy=" + createdBy + ", lastModifiedDate=" + lastModifiedDate
				+ ", lastModifiedBy=" + lastModifiedBy + ", year=" + year + ", month=" + month + ", url=" + url
				+ ", scheduledDate=" + scheduledDate + ", scheduledTime=" + scheduledTime + ", program_sem_subject_id="
				+ program_sem_subject_id + ", consumerType=" + consumerType + ", programStructure=" + programStructure
				+ ", program=" + program + ", session_plan_module_id=" + session_plan_module_id + ", group_id="
				+ group_id + ", hashtag_id=" + hashtag_id + ", subject_config_id=" + subject_config_id + "]";
	}
	

	
}
