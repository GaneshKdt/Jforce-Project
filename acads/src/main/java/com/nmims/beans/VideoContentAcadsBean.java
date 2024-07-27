package com.nmims.beans;

import java.io.Serializable;

public class VideoContentAcadsBean extends BaseAcadsBean  implements Serializable  {
	/**
	 * Change Name from VideoContentBean to VideoContentAcadsBean for serializable issue
	 */
	


	public Integer video_content_id;
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
	private String facultyId;
	private String sessionDate;
	private String facultyName;
	private String academicCycle;
	private String track;
	private String trackGroup;
	private String editSingleContentFromCommonSetup;
	
	private String meetingKey;
	
	private Long sessionPlanModuleId;
	private Long post_id;
	private String programSemSubjectId;
	
	//Added for subTopics
	private Long startTimeInSeconds;
	private Long endTimeInSeconds;

	private String videoTranscriptUrl;	
	private String programType;

	
	private String vimeoId;
	private String audioFile;

	private String bookmarked;
	private String sapId;
	private String imgUrl;
	

	private String firstName;
	private String lastName;
	private Integer subjectCodeId;
	
	private String audioUrl_status;
	private int audioUrl_retry;
	private String oldaudioUrl;
	private String acadYear;
	private String acadMonth;

	
	private String programName;
	private String sessionName;
	private String timeStamp;
	private String timeBound;	
	
	private String group1Sem;
	private String group2Sem;
	private String group3Sem;
	private String group4Sem;
	private String group5Sem;
	private String group6Sem;
	private String group1Program;
	private String group2Program;
	private String group3Program;
	private String group4Program;
	private String group5Program;
	private String group6Program;
	
	private String subjectCode;	
	private String fromSessionDate;
	private String toSessionDate;

	public String getSubjectCode() {
		return subjectCode;
	}
	public void setSubjectCode(String subjectCode) {
		this.subjectCode = subjectCode;
	}
	public String getFromSessionDate() {
		return fromSessionDate;
	}
	public void setFromSessionDate(String fromSessionDate) {
		this.fromSessionDate = fromSessionDate;
	}
	public String getToSessionDate() {
		return toSessionDate;
	}
	public void setToSessionDate(String toSessionDate) {
		this.toSessionDate = toSessionDate;
	}	
	public String getProgramName() {
		return programName;
	}
	public void setProgramName(String programName) {
		this.programName = programName;
	}
	public String getSessionName() {
		return sessionName;
	}
	public void setSessionName(String sessionName) {
		this.sessionName = sessionName;
	}
	public String getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
	public String getTimeBound() {
		return timeBound;
	}
	public void setTimeBound(String timeBound) {
		this.timeBound = timeBound;
	}
	public String getGroup1Sem() {
		return group1Sem;
	}
	public void setGroup1Sem(String group1Sem) {
		this.group1Sem = group1Sem;
	}
	public String getGroup2Sem() {
		return group2Sem;
	}
	public void setGroup2Sem(String group2Sem) {
		this.group2Sem = group2Sem;
	}
	public String getGroup3Sem() {
		return group3Sem;
	}
	public void setGroup3Sem(String group3Sem) {
		this.group3Sem = group3Sem;
	}
	public String getGroup4Sem() {
		return group4Sem;
	}
	public void setGroup4Sem(String group4Sem) {
		this.group4Sem = group4Sem;
	}
	public String getGroup5Sem() {
		return group5Sem;
	}
	public void setGroup5Sem(String group5Sem) {
		this.group5Sem = group5Sem;
	}
	public String getGroup6Sem() {
		return group6Sem;
	}
	public void setGroup6Sem(String group6Sem) {
		this.group6Sem = group6Sem;
	}
	public String getGroup1Program() {
		return group1Program;
	}
	public void setGroup1Program(String group1Program) {
		this.group1Program = group1Program;
	}
	public String getGroup2Program() {
		return group2Program;
	}
	public void setGroup2Program(String group2Program) {
		this.group2Program = group2Program;
	}
	public String getGroup3Program() {
		return group3Program;
	}
	public void setGroup3Program(String group3Program) {
		this.group3Program = group3Program;
	}
	public String getGroup4Program() {
		return group4Program;
	}
	public void setGroup4Program(String group4Program) {
		this.group4Program = group4Program;
	}
	public String getGroup5Program() {
		return group5Program;
	}
	public void setGroup5Program(String group5Program) {
		this.group5Program = group5Program;
	}
	public String getGroup6Program() {
		return group6Program;
	}
	public void setGroup6Program(String group6Program) {
		this.group6Program = group6Program;
	}
	public String getOldaudioUrl() {
		return oldaudioUrl;
	}
	public void setOldaudioUrl(String oldaudioUrl) {
		this.oldaudioUrl = oldaudioUrl;
	}
	public String getAudioUrl_status() {
		return audioUrl_status;
	}
	public void setAudioUrl_status(String audioUrl_status) {
		this.audioUrl_status = audioUrl_status;
	}
	public int getAudioUrl_retry() {
		return audioUrl_retry;
	}
	public void setAudioUrl_retry(int audioUrl_retry) {
		this.audioUrl_retry = audioUrl_retry;
	}
	public Integer getVideo_content_id() {
		return video_content_id;
	}
	public void setVideo_content_id(Integer video_content_id) {
		this.video_content_id = video_content_id;
	}
	
	public String getAudioFile() {
		return audioFile;
	}
	public void setAudioFile(String audioFile) {
		this.audioFile = audioFile;
	}
	public String getVimeoId() {
		return vimeoId;
	}
	public void setVimeoId(String vimeoId) {
		this.vimeoId = vimeoId;
	}
	public String getVideoTranscriptUrl() {
		return videoTranscriptUrl;
	}
	public void setVideoTranscriptUrl(String videoTranscriptUrl) {
		this.videoTranscriptUrl = videoTranscriptUrl;
	}
	public Long getEndTimeInSeconds() {
		return endTimeInSeconds;
	}
	public void setEndTimeInSeconds(Long endTimeInSeconds) {
		this.endTimeInSeconds = endTimeInSeconds;
	}
	public Long getStartTimeInSeconds() {
		return startTimeInSeconds;
	}
	public void setStartTimeInSeconds(Long startTimeInSeconds) {
		this.startTimeInSeconds = startTimeInSeconds;
	}
	public Long getPost_id() {
		return post_id;
	}
	public void setPost_id(Long post_id) {
		this.post_id = post_id;
	}
	public Long getSessionPlanModuleId() {
		return sessionPlanModuleId;
	}
	public void setSessionPlanModuleId(Long sessionPlanModuleId) {
		this.sessionPlanModuleId = sessionPlanModuleId;
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
	public String getTrackGroup() {
		return trackGroup;
	}
	public void setTrackGroup(String trackGroup) {
		this.trackGroup = trackGroup;
	}
	public String getTrack() {
		return track;
	}
	public void setTrack(String track) {
		this.track = track;
	}
	public String getAcademicCycle() {
		return academicCycle;
	}
	public void setAcademicCycle(String academicCycle) {
		this.academicCycle = academicCycle;
	}
	public String getFacultyName() {
		return facultyName;
	}
	public void setFacultyName(String facultyName) {
		this.facultyName = facultyName;
	}
	public String getSessionDate() {
		return sessionDate;
	}
	public void setSessionDate(String sessionDate) {
		this.sessionDate = sessionDate;
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



	public String getMeetingKey() {
		return meetingKey;
	}
	public void setMeetingKey(String meetingKey) {
		this.meetingKey = meetingKey;
	}

	public String getProgramType() {
		return programType;
	}
	public void setProgramType(String programType) {
		this.programType = programType;
	}
	public String getProgramSemSubjectId() {
		return programSemSubjectId;
	}
	public void setProgramSemSubjectId(String programSemSubjectId) {
		this.programSemSubjectId = programSemSubjectId;
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

	public String getImgUrl() {
		return imgUrl;
	}
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
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
	
	public Integer getSubjectCodeId() {
		return subjectCodeId;
	}
	public void setSubjectCodeId(Integer subjectCodeId) {
		this.subjectCodeId = subjectCodeId;
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
	@Override
	public String toString() {
		return "VideoContentAcadsBean [video_content_id=" + video_content_id + ", year=" + year + ", month=" + month
				+ ", subject=" + subject + ", fileName=" + fileName + ", keywords=" + keywords + ", description="
				+ description + ", defaultVideo=" + defaultVideo + ", errorMessage=" + errorMessage + ", errorRecord="
				+ errorRecord + ", duration=" + duration + ", viewCount=" + viewCount + ", addedBy=" + addedBy
				+ ", addedOn=" + addedOn + ", videoLink=" + videoLink + ", parentVideoId=" + parentVideoId
				+ ", startTime=" + startTime + ", endTime=" + endTime + ", thumbnailUrl=" + thumbnailUrl
				+ ", mobileUrl=" + mobileUrl + ", sessionId=" + sessionId + ", mobileUrlHd=" + mobileUrlHd
				+ ", mobileUrlSd1=" + mobileUrlSd1 + ", mobileUrlSd2=" + mobileUrlSd2 + ", facultyId=" + facultyId
				+ ", sessionDate=" + sessionDate + ", facultyName=" + facultyName + ", academicCycle=" + academicCycle
				+ ", track=" + track + ", trackGroup=" + trackGroup + ", editSingleContentFromCommonSetup="
				+ editSingleContentFromCommonSetup + ", meetingKey=" + meetingKey + ", sessionPlanModuleId="
				+ sessionPlanModuleId + ", post_id=" + post_id + ", programSemSubjectId=" + programSemSubjectId
				+ ", startTimeInSeconds=" + startTimeInSeconds + ", endTimeInSeconds=" + endTimeInSeconds
				+ ", videoTranscriptUrl=" + videoTranscriptUrl + ", programType=" + programType + ", vimeoId=" + vimeoId
				+ ", audioFile=" + audioFile + ", bookmarked=" + bookmarked + ", sapId=" + sapId + ", imgUrl=" + imgUrl
				+ ", firstName=" + firstName + ", lastName=" + lastName + ", subjectCodeId=" + subjectCodeId
				+ ", audioUrl_status=" + audioUrl_status + ", audioUrl_retry=" + audioUrl_retry + ", oldaudioUrl="
				+ oldaudioUrl + ", acadYear=" + acadYear + ", acadMonth=" + acadMonth + ", programName=" + programName
				+ ", sessionName=" + sessionName + ", timeStamp=" + timeStamp + ", timeBound=" + timeBound
				+ ", group1Sem=" + group1Sem + ", group2Sem=" + group2Sem + ", group3Sem=" + group3Sem + ", group4Sem="
				+ group4Sem + ", group5Sem=" + group5Sem + ", group6Sem=" + group6Sem + ", group1Program="
				+ group1Program + ", group2Program=" + group2Program + ", group3Program=" + group3Program
				+ ", group4Program=" + group4Program + ", group5Program=" + group5Program + ", group6Program="
				+ group6Program + ", subjectCode=" + subjectCode + ", fromSessionDate=" + fromSessionDate
				+ ", toSessionDate=" + toSessionDate + "]";
	}
}
