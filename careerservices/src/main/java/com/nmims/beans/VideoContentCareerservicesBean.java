package com.nmims.beans;

import java.io.Serializable;
import java.util.Date;

public class VideoContentCareerservicesBean implements Serializable {

	public String fileName;
	public String keywords;
	public String description;
	public String defaultVideo;

	private String id;
	private String videoTypeId;
	private String videoTypeName;
	private String duration;
	private String viewCount;
	private String videoLink;
	private String parentVideoId;
	private String thumbnailUrl;
	private String mobileUrl;
	private String mobileUrlHd;
	private String mobileUrlSd1;
	private String mobileUrlSd2;
	
	private Date createdDate;
	private Date lastModifiedDate;
	private String createdBy;
	private String lastModifiedBy;
	private String addedBy;
	private String addedOn;

	private String videoTitle;
	
	private String sessionId;
	private String sessionName;
	private String facultyId;
	private String facultyName;
	private String sessionDate;
	
	public String getSessionName() {
		return sessionName;
	}
	public void setSessionName(String sessionName) {
		this.sessionName = sessionName;
	}
	public String getFacultyName() {
		return facultyName;
	}
	public void setFacultyName(String facultyName) {
		this.facultyName = facultyName;
	}
	public String getFacultyId() {
		return facultyId;
	}
	public void setFacultyId(String facultyId) {
		this.facultyId = facultyId;
	}
	public String getMobileUrlHd() {
		return mobileUrlHd;
	}
	public void setMobileUrlHd(String mobileUrlHd) {
		this.mobileUrlHd = mobileUrlHd;
	}
	public String getMobileUrlSd1() {
		return mobileUrlSd1;
	}
	public void setMobileUrlSd1(String mobileUrlSd1) {
		this.mobileUrlSd1 = mobileUrlSd1;
	}
	public String getMobileUrlSd2() {
		return mobileUrlSd2;
	}
	public void setMobileUrlSd2(String mobileUrlSd2) {
		this.mobileUrlSd2 = mobileUrlSd2;
	}
	public String getMobileUrl() {
		return mobileUrl;
	}
	public void setMobileUrl(String mobileUrl) {
		this.mobileUrl = mobileUrl;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public void setViewCount(String viewCount) {
		this.viewCount = viewCount;
	}
	public void setParentVideoId(String parentVideoId) {
		this.parentVideoId = parentVideoId;
	}
	public String getThumbnailUrl() {
		return thumbnailUrl;
	}
	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}
	public String getParentVideoId() {
		return parentVideoId;
	}
	public String getVideoLink() {
		return videoLink;
	}
	public void setVideoLink(String videoLink) {
		this.videoLink = videoLink;
	}
	public String getAddedBy() {
		return addedBy;
	}
	public void setAddedBy(String addedBy) {
		this.addedBy = addedBy;
	}
	public String getAddedOn() {
		return addedOn;
	}
	public void setAddedOn(String addedOn) {
		this.addedOn = addedOn;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	public String getViewCount() {
		return viewCount;
	}
	public String getDefaultVideo() {
		return defaultVideo;
	}
	public void setDefaultVideo(String defaultVideo) {
		this.defaultVideo = defaultVideo;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getKeywords() {
		return keywords;
	}
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}
	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
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
	@Override
	public String toString() {
		return "VideoContentBean [ fileName=" + fileName
				+ ", keywords=" + keywords + ", description=" + description + "]";
	}
	public String getVideoTypeId() {
		return videoTypeId;
	}
	public void setVideoTypeId(String videoTypeId) {
		this.videoTypeId = videoTypeId;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getVideoTypeName() {
		return videoTypeName;
	}
	public void setVideoTypeName(String videoTypeName) {
		this.videoTypeName = videoTypeName;
	}
	public String getSessionDate() {
		return sessionDate;
	}
	public void setSessionDate(String sessionDate) {
		this.sessionDate = sessionDate;
	}
	public String getVideoTitle() {
		return videoTitle;
	}
	public void setVideoTitle(String videoTitle) {
		this.videoTitle = videoTitle;
	}
	
	
	
}
