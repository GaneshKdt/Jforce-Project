package com.nmims.beans;

public class VideoContentBean extends BaseStudentPortalBean {

	private Long id;
	public String year;
	public String month;
	public String subject;
	public String fileName;
	public String keywords;
	public String description;
	public String defaultVideo;
	private String errorMessage = "";
	private boolean errorRecord = false;

	private String duration;
	private Long viewCount;
	private String addedBy;
	private String addedOn;
	private String videoLink;
	private long parentVideoId;
	private String startTime;
	private String endTime;
	private String thumbnailUrl;
	private String mobileUrl;
	private Integer sessionId;
	private String mobileUrlHd;
	private String mobileUrlSd1;
	private String mobileUrlSd2;
	private String type;
	
	private String facultyId;
	private String sessionDate;
	private String facultyName;
	private String academicCycle;
	private String track;
	
	private String meetingKey;
	
	private String bookmarked;
	private String sapId;
	private String programSemSubjectId;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	public Integer getSessionId() {
		return sessionId;
	}
	public void setSessionId(Integer sessionId) {
		this.sessionId = sessionId;
	}
	public void setViewCount(Long viewCount) {
		this.viewCount = viewCount;
	}
	public void setParentVideoId(long parentVideoId) {
		this.parentVideoId = parentVideoId;
	}
	public String getThumbnailUrl() {
		return thumbnailUrl;
	}
	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public Long getParentVideoId() {
		return parentVideoId;
	}
	public void setParentVideoId(Long parentVideoId) {
		this.parentVideoId = parentVideoId;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
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
	public Long getViewCount() {
		return viewCount;
	}
//	public void setViewCount(long viewCount) {
//		this.viewCount = viewCount;
//	}
	public String getDefaultVideo() {
		return defaultVideo;
	}
	public void setDefaultVideo(String defaultVideo) {
		this.defaultVideo = defaultVideo;
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
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
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
	public String getFacultyId() {
		return facultyId;
	}
	public void setFacultyId(String facultyId) {
		this.facultyId = facultyId;
	}
	public String getSessionDate() {
		return sessionDate;
	}
	public void setSessionDate(String sessionDate) {
		this.sessionDate = sessionDate;
	}
	public String getFacultyName() {
		return facultyName;
	}
	public void setFacultyName(String facultyName) {
		this.facultyName = facultyName;
	}
	public String getAcademicCycle() {
		return academicCycle;
	}
	public void setAcademicCycle(String academicCycle) {
		this.academicCycle = academicCycle;
	}
	public String getTrack() {
		return track;
	}
	public void setTrack(String track) {
		this.track = track;
	}
	
	
	
	public String getMeetingKey() {
		return meetingKey;
	}
	public void setMeetingKey(String meetingKey) {
		this.meetingKey = meetingKey;
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
	
	public String getProgramSemSubjectId() {
		return programSemSubjectId;
	}
	public void setProgramSemSubjectId(String programSemSubjectId) {
		this.programSemSubjectId = programSemSubjectId;
	}
	@Override
	public String toString() {
		return "VideoContentBean [id=" + id + ", year=" + year + ", month=" + month + ", subject=" + subject
				+ ", fileName=" + fileName + ", keywords=" + keywords + ", description=" + description
				+ ", defaultVideo=" + defaultVideo + ", errorMessage=" + errorMessage + ", errorRecord=" + errorRecord
				+ ", duration=" + duration + ", viewCount=" + viewCount + ", addedBy=" + addedBy + ", addedOn="
				+ addedOn + ", videoLink=" + videoLink + ", parentVideoId=" + parentVideoId + ", startTime=" + startTime
				+ ", endTime=" + endTime + ", thumbnailUrl=" + thumbnailUrl + ", mobileUrl=" + mobileUrl
				+ ", sessionId=" + sessionId + ", mobileUrlHd=" + mobileUrlHd + ", mobileUrlSd1=" + mobileUrlSd1
				+ ", mobileUrlSd2=" + mobileUrlSd2 + ", type=" + type + ", facultyId=" + facultyId + ", sessionDate="
				+ sessionDate + ", facultyName=" + facultyName + ", academicCycle=" + academicCycle + ", track=" + track
				+ ", meetingKey=" + meetingKey + ", bookmarked=" + bookmarked + ", sapId=" + sapId
				+ ", programSemSubjectId=" + programSemSubjectId + "]";
	}
	
}
