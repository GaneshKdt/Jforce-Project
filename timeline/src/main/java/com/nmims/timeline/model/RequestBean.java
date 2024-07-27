package com.nmims.timeline.model;

public class RequestBean {

    String userId="";
    int pageId=1;
    String subjectId="";
    String facultyId="";
    int post_id=0;
    String role;
    Long group_id;
    String prgm_sem_sub_id;
    String sapid;
    String comment;
    int comment_id;
    int limit;
    int offset; 
    int sessionPlanModuleId = 0;
    int timeBoundId = 0;
    String createdDate;
    int id=0;
    String keyword = "";
    String fileType="";
    String timeBoundIds;
    String postType="";
    String ReactionType;
    String firstName;
    String lastName;
    
	public String getFirstName() {
		return firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getReactionType() {
		return ReactionType;
	}
	public void setReactionType(String reactionType) {
		ReactionType = reactionType;
	}
	public String getPostType() {
		return postType;
	}
	public void setPostType(String postType) {
		this.postType = postType;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTimeBoundIds() {
		return timeBoundIds;
	}
	public void setTimeBoundIds(String timeBoundIds) {
		this.timeBoundIds = timeBoundIds;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public int getLimit() {
		return limit;
	}
	public int getOffset() {
		return offset;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
	public void setOffset(int offset) {
		this.offset = offset;
	}
	public int getComment_id() {
		return comment_id;
	}
	public void setComment_id(int comment_id) {
		this.comment_id = comment_id;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public String getPrgm_sem_sub_id() {
		return prgm_sem_sub_id;
	}
	public void setPrgm_sem_sub_id(String prgm_sem_sub_id) {
		this.prgm_sem_sub_id = prgm_sem_sub_id;
	}
	public Long getGroup_id() {
		return group_id;
	}
	public void setGroup_id(Long group_id) {
		this.group_id = group_id;
	}
    public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getUserId() {
		return userId;
	}
	public int getPageId() {
		return pageId;
	}
	public String getSubjectId() {
		return subjectId;
	}
	public String getFacultyId() {
		return facultyId;
	}
	public int getPost_id() {
		return post_id;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public void setPageId(int pageId) {
		this.pageId = pageId;
	}
	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}
	public void setFacultyId(String facultyId) {
		this.facultyId = facultyId;
	}
	public void setPost_id(int post_id) {
		this.post_id = post_id;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public int getSessionPlanModuleId() {
		return sessionPlanModuleId;
	}
	public void setSessionPlanModuleId(int sessionPlanModuleId) {
		this.sessionPlanModuleId = sessionPlanModuleId;
	}
	
	public int getTimeBoundId() {
		return timeBoundId;
	}
	public void setTimeBoundId(int timeBoundId) {
		this.timeBoundId = timeBoundId;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	
	
	@Override
	public String toString() {
		return "RequestBean [userId=" + userId + ", pageId=" + pageId + ", subjectId=" + subjectId + ", facultyId="
				+ facultyId + ", post_id=" + post_id + ", role=" + role + ", group_id=" + group_id
				+ ", prgm_sem_sub_id=" + prgm_sem_sub_id + ", sapid=" + sapid + ", comment=" + comment + ", comment_id="
				+ comment_id + ", limit=" + limit + ", offset=" + offset + ", sessionPlanModuleId="
				+ sessionPlanModuleId + ", timeBoundId=" + timeBoundId + ", createdDate=" + createdDate + ", id=" + id
				+ ", keyword=" + keyword + ", fileType=" + fileType + ", timeBoundIds=" + timeBoundIds + ", postType="
				+ postType + ", ReactionType=" + ReactionType + ", firstName=" + firstName + ", lastName=" + lastName
				+ "]";
	}
	
}
