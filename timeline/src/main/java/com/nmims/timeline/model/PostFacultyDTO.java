package com.nmims.timeline.model;

import java.util.List;

public class PostFacultyDTO {

	private String post_id;

	private String userId;

	private Integer subject_config_id;
	private String role; 
	private String type; 

	private String content;
	private String fileName;
	private String FilePath;
	private String FileType;
	private String referenceId;
	private String visibility;

	private Integer acadYear;
	private String acadMonth;
	private Integer examYear;
	private String examMonth;

	private String url;

	private String scheduledDate;

	private String scheduledTime;

	private String scheduleFlag="N";

	private String session_plan_module_id;
	private String group_id;

	private String embedImage;
	private String embedUrl;
	private String embedDescription;
	private String embedTitle;
	private String hashtags;

	private String createdBy;
	private String lastModifiedBy;

	//For Session
	private String sessionDate;
	private String subject;
	private String videolink;
	private String thumbnailUrl;
	private String mobileUrlHd;

	//For Announcements
	private String startDate;
	private String endDate;
	private String active;
	private String category;
	private String attachment1;
	private String attachment2;
	private String attachment3;

	//For faculty details
	private String firstName;
	private String lastName;
	private String profilePicFilePath;

	private String consumerType;

	private String programStructure;

	private String program;

	private List<Comments> comments;

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

	public Integer getSubject_config_id() {
		return subject_config_id;
	}

	public void setSubject_config_id(Integer subject_config_id) {
		this.subject_config_id = subject_config_id;
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

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	public String getVisibility() {
		return visibility;
	}

	public void setVisibility(String visibility) {
		this.visibility = visibility;
	}

	public Integer getAcadYear() {
		return acadYear;
	}

	public void setAcadYear(Integer acadYear) {
		this.acadYear = acadYear;
	}

	public String getAcadMonth() {
		return acadMonth;
	}

	public void setAcadMonth(String acadMonth) {
		this.acadMonth = acadMonth;
	}

	public Integer getExamYear() {
		return examYear;
	}

	public void setExamYear(Integer examYear) {
		this.examYear = examYear;
	}

	public String getExamMonth() {
		return examMonth;
	}

	public void setExamMonth(String examMonth) {
		this.examMonth = examMonth;
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

	public String getScheduleFlag() {
		return scheduleFlag;
	}

	public void setScheduleFlag(String scheduleFlag) {
		this.scheduleFlag = scheduleFlag;
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

	public String getEmbedImage() {
		return embedImage;
	}

	public void setEmbedImage(String embedImage) {
		this.embedImage = embedImage;
	}

	public String getEmbedUrl() {
		return embedUrl;
	}

	public void setEmbedUrl(String embedUrl) {
		this.embedUrl = embedUrl;
	}

	public String getEmbedDescription() {
		return embedDescription;
	}

	public void setEmbedDescription(String embedDescription) {
		this.embedDescription = embedDescription;
	}

	public String getEmbedTitle() {
		return embedTitle;
	}

	public void setEmbedTitle(String embedTitle) {
		this.embedTitle = embedTitle;
	}

	public String getHashtags() {
		return hashtags;
	}

	public void setHashtags(String hashtags) {
		this.hashtags = hashtags;
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

	public String getSessionDate() {
		return sessionDate;
	}

	public void setSessionDate(String sessionDate) {
		this.sessionDate = sessionDate;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getVideolink() {
		return videolink;
	}

	public void setVideolink(String videolink) {
		this.videolink = videolink;
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

	public String getProfilePicFilePath() {
		return profilePicFilePath;
	}

	public void setProfilePicFilePath(String profilePicFilePath) {
		this.profilePicFilePath = profilePicFilePath;
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

	public List<Comments> getComments() {
		return comments;
	}

	public void setComments(List<Comments> comments) {
		this.comments = comments;
	}

	@Override
	public String toString() {
		return "PostFacultyDTO [post_id=" + post_id + ", userId=" + userId + ", subject_config_id=" + subject_config_id
				+ ", role=" + role + ", type=" + type + ", content=" + content + ", fileName=" + fileName
				+ ", FilePath=" + FilePath + ", FileType=" + FileType + ", referenceId=" + referenceId + ", visibility="
				+ visibility + ", acadYear=" + acadYear + ", acadMonth=" + acadMonth + ", examYear=" + examYear
				+ ", examMonth=" + examMonth + ", url=" + url + ", scheduledDate=" + scheduledDate + ", scheduledTime="
				+ scheduledTime + ", scheduleFlag=" + scheduleFlag + ", session_plan_module_id="
				+ session_plan_module_id + ", group_id=" + group_id + ", embedImage=" + embedImage + ", embedUrl="
				+ embedUrl + ", embedDescription=" + embedDescription + ", embedTitle=" + embedTitle + ", hashtags="
				+ hashtags + ", createdBy=" + createdBy + ", lastModifiedBy=" + lastModifiedBy + ", sessionDate="
				+ sessionDate + ", subject=" + subject + ", videolink=" + videolink + ", thumbnailUrl=" + thumbnailUrl
				+ ", mobileUrlHd=" + mobileUrlHd + ", startDate=" + startDate + ", endDate=" + endDate + ", active="
				+ active + ", category=" + category + ", attachment1=" + attachment1 + ", attachment2=" + attachment2
				+ ", attachment3=" + attachment3 + ", firstName=" + firstName + ", lastName=" + lastName
				+ ", profilePicFilePath=" + profilePicFilePath + ", consumerType=" + consumerType
				+ ", programStructure=" + programStructure + ", program=" + program + ", comments=" + comments + "]";
	}
	
	
	

}
