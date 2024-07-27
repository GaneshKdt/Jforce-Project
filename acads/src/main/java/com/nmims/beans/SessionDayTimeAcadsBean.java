package com.nmims.beans;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class SessionDayTimeAcadsBean implements Serializable{
	
	/**
	 * Change Name from SessionDayTimeBean to SessionDayTimeAcadsBean for serializable issue
	 */
	
	
	private String id;
	private String year;
	private String month;
	private String date;
	private String day;
	private String reScheduleDate;
	private String reScheduleStartTime;
	private String reScheduleEndTime;
	private String startTime;
	private String endTime;
	private String duration;
	private String sessionName;
	private String subject;
	private String answeredByFacultyId;
	private String room;
	private String ciscoStatus;
	private String createdBy;
	private String createdDate;
	private String lastModifiedBy;
	private String lastModifiedDate;
	private String errorMessage = "";
	private boolean errorRecord = false;
	
	
	private String firstName;
	private String lastName;
	private String email;
	private String mobile;
	
	private Integer tmsConfId;
	private String tmsConfLink;
	/**
	 * These variables are used for session recording only 
	 * */
	private String vimeoId;
	private String meetingId;
	private String sessionId;
	private String status;
	private String error;
	private String vimeoStatus;
	
	/**
	 * End
	 * */
	
	
	private String joinUrl;
	private String hostUrl;
	private String hostKey;
	private String localTollNumber;
	private String localTollFree;
	private String globalCallNumber;
	private String pstnDialNumber;
	private String participantCode;
	
	private String hostId;
	private String hostPassword;
	private String isAdditionalSession = "N";
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
	
	private String facultyId;
	private String meetingKey;
	private String meetingPwd;
	
	
	private String altMeetingKey;
	private String altMeetingPwd;
	private String altFacultyId;
	
	private String altMeetingKey2;
	private String altMeetingPwd2;
	private String altFacultyId2;
	
	private String altMeetingKey3;
	private String altMeetingPwd3;
	private String altFacultyId3;
	
	private String isVerified;

	private String byPassChecks = "N";
	private String isCommon;
	private String sem;
	private String corporateName;
	
	private String programList;
	private String reviewed;
	private int noOfParallelSession;
	private String isCancelled;
	private String reasonForCancellation;
	private String cancellationSMSBody;
	private String cancellationEmailBody;
	private String cancellationSubject;
	private String startDate;
	private String endDate;
	
	private String altHostKey;
	private String altHostId;
	private String altHostPassword;
	private String altHostKey2;
	private String altHostId2;
	private String altHostPassword2;
	private String altHostKey3;
	private String altHostId3;
	private String altHostPassword3;
	
	private String facultyLocation;
	private String altFacultyLocation;
	private String altFaculty2Location;
	private String altFaculty3Location;
	
	private String track;
	private List<VideoContentAcadsBean> videosOfSession;
	private String facultyName;
	private String attended;
	private String device;
	
	/*Added For Zoom*/
	private String zoomHostID;
	private String zoomStatus;
	private String zoomMeetingID;
	private String hostJoinUrl;
	private String studentJoinUrl;
	
	private String altZoomHostID;
	private String altZoomMeetingID;
	private String altHostJoinUrl;
	private String altStudentJoinUrl;
	
	private String alt2ZoomHostID;
	private String alt2ZoomMeetingID;
	private String alt2HostJoinUrl;
	private String alt2tStudentJoinUrl;
	
	private String alt3ZoomHostID;
	private String alt3ZoomMeetingID;
	private String alt3HostJoinUrl;
	private String alt3StudentJoinUrl;
	
	private String session_id;
	private String sapId;
	private String question;
	private String answer ;

	private String isAnswered;
	private String isPublic;
	private String query;
	
	/*Added For Session Subject Mapping*/
	//private Integer sessionId;
	private Integer prgmSemSubId;
	private String sessionModuleNo;
	private String hasModuleId;
	private String studentType;
	private String forMobile;
	private String moduleId;
	private String timeboundId;
	private String consumerTypeId;
	private String programId;
	private String programStructureId;
	private String consumerProgramStructureId;
	private String consumerType;
	private String programStructure;
	private String program;
	private String count;
	
	private String imgUrl;
	
 	
 	private int moduleSessionPlanId;
 	private String sessionplanMonth;
 	private String sessionplanYear;
 	private String sessionplanCreatedBy;
 	private String sessionplanLastModifiedBy;
 	private String sessionplanSubject;
 	private String dateTime;	
 	private String timebondFacultyId;
 	private String isRecommendationSession = "N";
 	private String batchUpload = "N";
 	private String subject_group;
 	private String uniqueSemSubject;
 	private String selectAllOptions;
 	private String masterKey;
 	private String subjectCode;
 	private Integer subjectCodeId;
 	private String processFlag;
 	private String video_deleted;
 	private String byPassFaculty = "N";
 	private String videoLink;
 	private String sessionDuration;
 	private String sessionPlanModuleCreatedDate;
 	private String sessionPlanModuleLastModifiedDate;
 	private Integer slots;
 	private String fromDate;
 	private String toDate;
 	private String dayTime;
 	private String programSemSubjectId;
 	private List<String> sessionList;
 	private String sessionType;
 	private String dateTo;
 	private String dateFrom;
 	private String specializationType;
 	private String batchName;
 	
	public String getVideoLink() {
		return videoLink;
	}
	public void setVideoLink(String videoLink) {
		this.videoLink = videoLink;
	}
	public String getSessionDuration() {
		return sessionDuration;
	}
	public void setSessionDuration(String sessionDuration) {
		this.sessionDuration = sessionDuration;
	}
	public String getSessionPlanModuleCreatedDate() {
		return sessionPlanModuleCreatedDate; 
	}
	public void setSessionPlanModuleCreatedDate(String sessionPlanModuleCreatedDate) {
		this.sessionPlanModuleCreatedDate = sessionPlanModuleCreatedDate;
	}
	public String getSessionPlanModuleLastModifiedDate() {
		return sessionPlanModuleLastModifiedDate;
	}
	public void setSessionPlanModuleLastModifiedDate(String sessionPlanModuleLastModifiedDate) {
		this.sessionPlanModuleLastModifiedDate = sessionPlanModuleLastModifiedDate;
	}
	public int getModuleSessionPlanId() {
		return moduleSessionPlanId;
	}
	public void setModuleSessionPlanId(int moduleSessionPlanId) {
		this.moduleSessionPlanId = moduleSessionPlanId;
	}
	public String getSessionplanMonth() {
		return sessionplanMonth;
	}
	public void setSessionplanMonth(String sessionplanMonth) {
		this.sessionplanMonth = sessionplanMonth;
	}
	public String getSessionplanYear() {
		return sessionplanYear;
	}
	public void setSessionplanYear(String sessionplanYear) {
		this.sessionplanYear = sessionplanYear;
	}
	public String getSessionplanCreatedBy() {
		return sessionplanCreatedBy;
	}
	public void setSessionplanCreatedBy(String sessionplanCreatedBy) {
		this.sessionplanCreatedBy = sessionplanCreatedBy;
	}
	public String getSessionplanLastModifiedBy() {
		return sessionplanLastModifiedBy;
	}
	public void setSessionplanLastModifiedBy(String sessionplanLastModifiedBy) {
		this.sessionplanLastModifiedBy = sessionplanLastModifiedBy;
	}
	public String getSessionplanSubject() {
		return sessionplanSubject;
	}
	public void setSessionplanSubject(String sessionplanSubject) {
		this.sessionplanSubject = sessionplanSubject;
	}
	public String getTimebondFacultyId() {
		return timebondFacultyId;
	}
	public void setTimebondFacultyId(String timebondFacultyId) {
		this.timebondFacultyId = timebondFacultyId;
	}
	public String getVimeoId() {
		return vimeoId;
	}
	public void setVimeoId(String vimeoId) {
		this.vimeoId = vimeoId;
	}
	public String getMeetingId() {
		return meetingId;
	}
	public void setMeetingId(String meetingId) {
		this.meetingId = meetingId;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getVimeoStatus() {
		return vimeoStatus;
	}
	public void setVimeoStatus(String vimeoStatus) {
		this.vimeoStatus = vimeoStatus;
	}


	
	
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public String getIsAnswered() {
		return isAnswered;
	}
	public void setIsAnswered(String isAnswered) {
		this.isAnswered = isAnswered;
	}
	public String getIsPublic() {
		return isPublic;
	}
	public void setIsPublic(String isPublic) {
		this.isPublic = isPublic;
	}
	public String getAnsweredByFacultyId() {
		return answeredByFacultyId;
	}
	public void setAnsweredByFacultyId(String answeredByFacultyId) {
		answeredByFacultyId = answeredByFacultyId;
	}
	public String getSession_id() {
		return session_id;
	}
	public void setSession_id(String session_id) {
		this.session_id = session_id;
	}
	public String getSapId() {
		return sapId;
	}
	public void setSapId(String sapId) {
		this.sapId = sapId;
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	public String getFacultyName() {
		return facultyName;
	}
	public void setFacultyName(String facultyName) {
		this.facultyName = facultyName;
	}
	public List<VideoContentAcadsBean> getVideosOfSession() {
		return videosOfSession;
	}
	public void setVideosOfSession(List<VideoContentAcadsBean> videosOfSession) {
		this.videosOfSession = videosOfSession;
	}
	public String getTrack() {
		return track;
	}
	public void setTrack(String track) {
		this.track = track;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFacultyLocation() {
		return facultyLocation;
	}
	public void setFacultyLocation(String facultyLocation) {
		this.facultyLocation = facultyLocation;
	}
	public String getAltFacultyLocation() {
		return altFacultyLocation;
	}
	public void setAltFacultyLocation(String altFacultyLocation) {
		this.altFacultyLocation = altFacultyLocation;
	}
	public String getAltFaculty2Location() {
		return altFaculty2Location;
	}
	public void setAltFaculty2Location(String altFaculty2Location) {
		this.altFaculty2Location = altFaculty2Location;
	}
	public String getAltFaculty3Location() {
		return altFaculty3Location;
	}
	public void setAltFaculty3Location(String altFaculty3Location) {
		this.altFaculty3Location = altFaculty3Location;
	}
	public String getAltHostId() {
		return altHostId;
	}
	public void setAltHostId(String altHostId) {
		this.altHostId = altHostId;
	}
	public String getAltHostPassword() {
		return altHostPassword;
	}
	public void setAltHostPassword(String altHostPassword) {
		this.altHostPassword = altHostPassword;
	}
	public String getAltHostId2() {
		return altHostId2;
	}
	public void setAltHostId2(String altHostId2) {
		this.altHostId2 = altHostId2;
	}
	public String getAltHostPassword2() {
		return altHostPassword2;
	}
	public void setAltHostPassword2(String altHostPassword2) {
		this.altHostPassword2 = altHostPassword2;
	}
	public String getAltHostId3() {
		return altHostId3;
	}
	public void setAltHostId3(String altHostId3) {
		this.altHostId3 = altHostId3;
	}
	public String getAltHostPassword3() {
		return altHostPassword3;
	}
	public void setAltHostPassword3(String altHostPassword3) {
		this.altHostPassword3 = altHostPassword3;
	}
	public String getReScheduleStartTime() {
		return reScheduleStartTime;
	}
	public void setReScheduleStartTime(String reScheduleStartTime) {
		this.reScheduleStartTime = reScheduleStartTime;
	}
	public String getReScheduleEndTime() {
		return reScheduleEndTime;
	}
	public void setReScheduleEndTime(String reScheduleEndTime) {
		this.reScheduleEndTime = reScheduleEndTime;
	}
	public String getReScheduleDate() {
		return reScheduleDate;
	}
	public void setReScheduleDate(String reScheduleDate) {
		this.reScheduleDate = reScheduleDate;
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
	public String getCancellationSMSBody() {
		return cancellationSMSBody;
	}
	public void setCancellationSMSBody(String cancellationSMSBody) {
		this.cancellationSMSBody = cancellationSMSBody;
	}
	public String getCancellationEmailBody() {
		return cancellationEmailBody;
	}
	public void setCancellationEmailBody(String cancellationEmailBody) {
		this.cancellationEmailBody = cancellationEmailBody;
	}
	public String getCancellationSubject() {
		return cancellationSubject;
	}
	public void setCancellationSubject(String cancellationSubject) {
		this.cancellationSubject = cancellationSubject;
	}
	public String getIsCancelled() {
		return isCancelled;
	}
	public void setIsCancelled(String isCancelled) {
		this.isCancelled = isCancelled;
	}
	public String getReasonForCancellation() {
		return reasonForCancellation;
	}
	public void setReasonForCancellation(String reasonForCancellation) {
		this.reasonForCancellation = reasonForCancellation;
	}
	public int getNoOfParallelSession() {
		int count =0;
		if(!StringUtils.isBlank(facultyId)){
			count++;
		}
		if(!StringUtils.isBlank(altFacultyId)){
			count++;
		}
		if(!StringUtils.isBlank(altFacultyId2)){
			count++;
		}
		if(!StringUtils.isBlank(altFacultyId3)){
			count++;
		}
		return count;
	}
	public void setNoOfParallelSession(int noOfParallelSession) {
		this.noOfParallelSession = noOfParallelSession;
	}
	public String getReviewed() {
		return reviewed;
	}
	public void setReviewed(String reviewed) {
		this.reviewed = reviewed;
	}
	public String getProgramList() {
		return programList;
	}
	public void setProgramList(String programList) {
		this.programList = programList;
	}
	public String getCorporateName() {
		return corporateName;
	}
	public void setCorporateName(String corporateName) {
		this.corporateName = corporateName;
	}
	public String getAltMeetingKey2() {
		return altMeetingKey2;
	}
	public void setAltMeetingKey2(String altMeetingKey2) {
		this.altMeetingKey2 = altMeetingKey2;
	}
	public String getAltMeetingPwd2() {
		return altMeetingPwd2;
	}
	public void setAltMeetingPwd2(String altMeetingPwd2) {
		this.altMeetingPwd2 = altMeetingPwd2;
	}
	public String getAltFacultyId2() {
		return altFacultyId2;
	}
	public void setAltFacultyId2(String altFacultyId2) {
		this.altFacultyId2 = altFacultyId2;
	}
	public String getAltMeetingKey3() {
		return altMeetingKey3;
	}
	public void setAltMeetingKey3(String altMeetingKey3) {
		this.altMeetingKey3 = altMeetingKey3;
	}
	public String getAltMeetingPwd3() {
		return altMeetingPwd3;
	}
	public void setAltMeetingPwd3(String altMeetingPwd3) {
		this.altMeetingPwd3 = altMeetingPwd3;
	}
	public String getAltFacultyId3() {
		return altFacultyId3;
	}
	public void setAltFacultyId3(String altFacultyId3) {
		this.altFacultyId3 = altFacultyId3;
	}
	public String getIsVerified() {
		return isVerified;
	}
	public void setIsVerified(String isVerified) {
		this.isVerified = isVerified;
	}

	public String getIsCommon() {
		return isCommon;
	}
	public void setIsCommon(String isCommon) {
		this.isCommon = isCommon;
	}
	public String getSem() {
		return sem;
	}
	public void setSem(String sem) {
		this.sem = sem;
	}
	public String getAltMeetingKey() {
		return altMeetingKey;
	}
	public void setAltMeetingKey(String altMeetingKey) {
		this.altMeetingKey = altMeetingKey;
	}
	public String getAltMeetingPwd() {
		return altMeetingPwd;
	}
	public void setAltMeetingPwd(String altMeetingPwd) {
		this.altMeetingPwd = altMeetingPwd;
	}
	public String getByPassChecks() {
		return byPassChecks;
	}
	public void setByPassChecks(String byPassChecks) {
		this.byPassChecks = byPassChecks;
	}
	public String getGroup4Sem() {
		return group4Sem;
	}
	public void setGroup4Sem(String group4Sem) {
		this.group4Sem = group4Sem;
	}
	public String getGroup4Program() {
		return group4Program;
	}
	public void setGroup4Program(String group4Program) {
		this.group4Program = group4Program;
	}
	public String getGroup3Sem() {
		return group3Sem;
	}
	public void setGroup3Sem(String group3Sem) {
		this.group3Sem = group3Sem;
	}
	public String getGroup3Program() {
		return group3Program;
	}
	public void setGroup3Program(String group3Program) {
		this.group3Program = group3Program;
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
	public String getIsAdditionalSession() {
		return isAdditionalSession;
	}
	public void setIsAdditionalSession(String isAdditionalSession) {
		this.isAdditionalSession = isAdditionalSession;
	}
	
	
	public String getHostId() {
		return hostId;
	}
	public void setHostId(String hostId) {
		this.hostId = hostId;
	}
	public String getHostPassword() {
		return hostPassword;
	}
	public void setHostPassword(String hostPassword) {
		this.hostPassword = hostPassword;
	}
	public String getRoom() {
		return room;
	}
	public void setRoom(String room) {
		this.room = room;
	}
	public String getCiscoStatus() {
		return ciscoStatus;
	}
	public void setCiscoStatus(String ciscoStatus) {
		this.ciscoStatus = ciscoStatus;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public Integer getTmsConfId() {
		return tmsConfId;
	}
	public void setTmsConfId(Integer tmsConfId) {
		this.tmsConfId = tmsConfId;
	}
	public String getTmsConfLink() {
		return tmsConfLink;
	}
	public void setTmsConfLink(String tmsConfLink) {
		this.tmsConfLink = tmsConfLink;
	}

	public String getMeetingKey() {
		return meetingKey;
	}
	public void setMeetingKey(String meetingKey) {
		this.meetingKey = meetingKey;
	}
	public String getMeetingPwd() {
		return meetingPwd;
	}
	public void setMeetingPwd(String meetingPwd) {
		this.meetingPwd = meetingPwd;
	}
	public String getJoinUrl() {
		return joinUrl;
	}
	public void setJoinUrl(String joinUrl) {
		this.joinUrl = joinUrl;
	}
	public String getHostUrl() {
		return hostUrl;
	}
	public void setHostUrl(String hostUrl) {
		this.hostUrl = hostUrl;
	}
	public String getHostKey() {
		return hostKey;
	}
	public void setHostKey(String hostKey) {
		this.hostKey = hostKey;
	}
	public String getLocalTollNumber() {
		return localTollNumber;
	}
	public void setLocalTollNumber(String localTollNumber) {
		this.localTollNumber = localTollNumber;
	}
	public String getLocalTollFree() {
		return localTollFree;
	}
	public void setLocalTollFree(String localTollFree) {
		this.localTollFree = localTollFree;
	}
	public String getGlobalCallNumber() {
		return globalCallNumber;
	}
	public void setGlobalCallNumber(String globalCallNumber) {
		this.globalCallNumber = globalCallNumber;
	}
	public String getPstnDialNumber() {
		return pstnDialNumber;
	}
	public void setPstnDialNumber(String pstnDialNumber) {
		this.pstnDialNumber = pstnDialNumber;
	}
	public String getParticipantCode() {
		return participantCode;
	}
	public void setParticipantCode(String participantCode) {
		this.participantCode = participantCode;
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
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getFacultyId() {
		return facultyId;
	}
	public void setFacultyId(String facultyId) {
		this.facultyId = facultyId;
	}
	public String getSessionName() {
		return sessionName;
	}
	public void setSessionName(String sessionName) {
		this.sessionName = sessionName;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String dt) {
		this.date = dt;
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
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
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
	public String getAltFacultyId() {
		return altFacultyId;
	}
	public void setAltFacultyId(String altFacultyId) {
		this.altFacultyId = altFacultyId;
	}
	public String getAttended() {
		return attended;
	}
	public void setAttended(String attended) {
		this.attended = attended;
	}
	
	
	public String getZoomHostID() {
		return zoomHostID;
	}
	public void setZoomHostID(String zoomHostID) {
		this.zoomHostID = zoomHostID;
	}
	public String getZoomStatus() {
		return zoomStatus;
	}
	public void setZoomStatus(String zoomStatus) {
		this.zoomStatus = zoomStatus;
	}
	public String getZoomMeetingID() {
		return zoomMeetingID;
	}
	public void setZoomMeetingID(String zoomMeetingID) {
		this.zoomMeetingID = zoomMeetingID;
	}
	public String getHostJoinUrl() {
		return hostJoinUrl;
	}
	public void setHostJoinUrl(String hostJoinUrl) {
		this.hostJoinUrl = hostJoinUrl;
	}
	public String getStudentJoinUrl() {
		return studentJoinUrl;
	}
	public void setStudentJoinUrl(String studentJoinUrl) {
		this.studentJoinUrl = studentJoinUrl;
	}
	public String getAltZoomHostID() {
		return altZoomHostID;
	}
	public void setAltZoomHostID(String altZoomHostID) {
		this.altZoomHostID = altZoomHostID;
	}
	public String getAltZoomMeetingID() {
		return altZoomMeetingID;
	}
	public void setAltZoomMeetingID(String altZoomMeetingID) {
		this.altZoomMeetingID = altZoomMeetingID;
	}
	public String getAltHostJoinUrl() {
		return altHostJoinUrl;
	}
	public void setAltHostJoinUrl(String altHostJoinUrl) {
		this.altHostJoinUrl = altHostJoinUrl;
	}
	public String getAltStudentJoinUrl() {
		return altStudentJoinUrl;
	}
	public void setAltStudentJoinUrl(String altStudentJoinUrl) {
		this.altStudentJoinUrl = altStudentJoinUrl;
	}
	public String getAlt2ZoomHostID() {
		return alt2ZoomHostID;
	}
	public void setAlt2ZoomHostID(String alt2ZoomHostID) {
		this.alt2ZoomHostID = alt2ZoomHostID;
	}
	public String getAlt2ZoomMeetingID() {
		return alt2ZoomMeetingID;
	}
	public void setAlt2ZoomMeetingID(String alt2ZoomMeetingID) {
		this.alt2ZoomMeetingID = alt2ZoomMeetingID;
	}
	public String getAlt2HostJoinUrl() {
		return alt2HostJoinUrl;
	}
	public void setAlt2HostJoinUrl(String alt2HostJoinUrl) {
		this.alt2HostJoinUrl = alt2HostJoinUrl;
	}
	public String getAl2tStudentJoinUrl() {
		return alt2tStudentJoinUrl;
	}
	public void setAl2tStudentJoinUrl(String al2tStudentJoinUrl) {
		this.alt2tStudentJoinUrl = al2tStudentJoinUrl;
	}
	public String getAlt3ZoomHostID() {
		return alt3ZoomHostID;
	}
	public void setAlt3ZoomHostID(String alt3ZoomHostID) {
		this.alt3ZoomHostID = alt3ZoomHostID;
	}
	public String getAlt3ZoomMeetingID() {
		return alt3ZoomMeetingID;
	}
	public void setAlt3ZoomMeetingID(String alt3ZoomMeetingID) {
		this.alt3ZoomMeetingID = alt3ZoomMeetingID;
	}
	public String getAlt3HostJoinUrl() {
		return alt3HostJoinUrl;
	}
	public void setAlt3HostJoinUrl(String alt3HostJoinUrl) {
		this.alt3HostJoinUrl = alt3HostJoinUrl;
	}
	public String getAlt3StudentJoinUrl() {
		return alt3StudentJoinUrl;
	}
	public void setAlt3StudentJoinUrl(String alt3StudentJoinUrl) {
		this.alt3StudentJoinUrl = alt3StudentJoinUrl;
	}
	public String getDevice() {
		return device;
	}
	public void setDevice(String device) {
		this.device = device;
	}
	public Integer getPrgmSemSubId() {
		return prgmSemSubId;
	}
	public void setPrgmSemSubId(Integer prgmSemSubId) {
		this.prgmSemSubId = prgmSemSubId;
	}
	public String getSessionModuleNo() {
		return sessionModuleNo;
	}
	public void setSessionModuleNo(String sessionModuleNo) {
		this.sessionModuleNo = sessionModuleNo;
	}
	public String getHasModuleId() {
		return hasModuleId;
	}
	public void setHasModuleId(String hasModuleId) {
		this.hasModuleId = hasModuleId;
	}
	public String getStudentType() {
		return studentType;
	}
	public void setStudentType(String studentType) {
		this.studentType = studentType;
	}
	public String getForMobile() {
		return forMobile;
	}
	public void setForMobile(String forMobile) {
		this.forMobile = forMobile;
	}
	public String getModuleId() {
		return moduleId;
	}
	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}
	public String getImgUrl() {
		return imgUrl;
	}
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	public String getTimeboundId() {
		return timeboundId;
	}
	public void setTimeboundId(String timeboundId) {
		this.timeboundId = timeboundId;
	}
	public String getDateTime() {
		return dateTime;
	}
	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}
	public String getConsumerTypeId() {
		return consumerTypeId;
	}
	public void setConsumerTypeId(String consumerTypeId) {
		this.consumerTypeId = consumerTypeId;
	}
	public String getProgramId() {
		return programId;
	}
	public void setProgramId(String programId) {
		this.programId = programId;
	}
	public String getProgramStructureId() {
		return programStructureId;
	}
	public void setProgramStructureId(String programStructureId) {
		this.programStructureId = programStructureId;
	}
	public String getConsumerProgramStructureId() {
		return consumerProgramStructureId;
	}
	public void setConsumerProgramStructureId(String consumerProgramStructureId) {
		this.consumerProgramStructureId = consumerProgramStructureId;
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
	public String getCount() {
		return count;
	}
	public void setCount(String count) {
		this.count = count;
	}
	public String getIsRecommendationSession() {
		return isRecommendationSession;
	}
	public void setIsRecommendationSession(String isRecommendationSession) {
		this.isRecommendationSession = isRecommendationSession;
	}
	public String getBatchUpload() {
		return batchUpload;
	}
	public void setBatchUpload(String batchUpload) {
		this.batchUpload = batchUpload;
	}
	public String getSubject_group() {
		return subject_group;
	}
	public void setSubject_group(String subject_group) {
		this.subject_group = subject_group;
	}
	public String getUniqueSemSubject() {
		return uniqueSemSubject;
	}
	public void setUniqueSemSubject(String uniqueSemSubject) {
		this.uniqueSemSubject = uniqueSemSubject;
	}
	public String getAltHostKey() {
		return altHostKey;
	}
	public void setAltHostKey(String altHostKey) {
		this.altHostKey = altHostKey;
	}
	public String getAltHostKey2() {
		return altHostKey2;
	}
	public void setAltHostKey2(String altHostKey2) {
		this.altHostKey2 = altHostKey2;
	}
	public String getAltHostKey3() {
		return altHostKey3;
	}
	public void setAltHostKey3(String altHostKey3) {
		this.altHostKey3 = altHostKey3;
	}
	
	public String getSelectAllOptions() {
		return selectAllOptions;
	}
	public void setSelectAllOptions(String selectAllOptions) {
		this.selectAllOptions = selectAllOptions;
	}
	public String getMasterKey() {
		return masterKey;
	}
	public void setMasterKey(String masterKey) {
		this.masterKey = masterKey;
	}
	public String getSubjectCode() {
		return subjectCode;
	}
	public void setSubjectCode(String subjectCode) {
		this.subjectCode = subjectCode;
	}
	public Integer getSubjectCodeId() {
		return subjectCodeId;
	}
	public void setSubjectCodeId(Integer subjectCodeId) {
		this.subjectCodeId = subjectCodeId;
	}
	
	public String getProcessFlag() {
		return processFlag;
	}
	public void setProcessFlag(String processFlag) {
		this.processFlag = processFlag;
	}
	public String getVideo_deleted() {
		return video_deleted;
	}
	public void setVideo_deleted(String video_deleted) {
		this.video_deleted = video_deleted;
	}
	public String getByPassFaculty() {
		return byPassFaculty;
	}
	public void setByPassFaculty(String byPassFaculty) {
		this.byPassFaculty = byPassFaculty;
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
	public Integer getSlots() {
		return slots;
	}
	public void setSlots(Integer slots) {
		this.slots = slots;
	}
	public String getFromDate() {
		return fromDate;
	}
	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}
	public String getToDate() {
		return toDate;
	}
	public void setToDate(String toDate) {
		this.toDate = toDate;
	}
	public String getAlt2tStudentJoinUrl() {
		return alt2tStudentJoinUrl;
	}
	public void setAlt2tStudentJoinUrl(String alt2tStudentJoinUrl) {
		this.alt2tStudentJoinUrl = alt2tStudentJoinUrl;
	}
	public String getProgramSemSubjectId() {
		return programSemSubjectId;
	}
	public void setProgramSemSubjectId(String programSemSubjectId) {
		this.programSemSubjectId = programSemSubjectId;
	}
	public String getDayTime() {
		return dayTime;
	}
	public void setDayTime(String dayTime) {
		this.dayTime = dayTime;
	}

	public List<String> getSessionList() {
		return sessionList;
	}
	public void setSessionList(List<String> sessionList) {
		this.sessionList = sessionList;
	}
	public String getSessionType() {
		return sessionType;
	}
	public void setSessionType(String sessionType) {
		this.sessionType = sessionType;
	}

	public String getDateTo() {
		return dateTo;
	}
	public void setDateTo(String dateTo) {
		this.dateTo = dateTo;
	}
	public String getDateFrom() {
		return dateFrom;
	}
	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}

	public String getSpecializationType() {
		return specializationType;
	}
	public void setSpecializationType(String specializationType) {
		this.specializationType = specializationType;
	}
	
	public String getBatchName() {
		return batchName;
	}
	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}
	
	@Override
	public String toString() {
		return "SessionDayTimeAcadsBean [id=" + id + ", year=" + year + ", month=" + month + ", date=" + date + ", day="
				+ day + ", reScheduleDate=" + reScheduleDate + ", reScheduleStartTime=" + reScheduleStartTime
				+ ", reScheduleEndTime=" + reScheduleEndTime + ", startTime=" + startTime + ", endTime=" + endTime
				+ ", duration=" + duration + ", sessionName=" + sessionName + ", subject=" + subject
				+ ", answeredByFacultyId=" + answeredByFacultyId + ", room=" + room + ", ciscoStatus=" + ciscoStatus
				+ ", createdBy=" + createdBy + ", createdDate=" + createdDate + ", lastModifiedBy=" + lastModifiedBy
				+ ", lastModifiedDate=" + lastModifiedDate + ", errorMessage=" + errorMessage + ", errorRecord="
				+ errorRecord + ", firstName=" + firstName + ", lastName=" + lastName + ", email=" + email + ", mobile="
				+ mobile + ", tmsConfId=" + tmsConfId + ", tmsConfLink=" + tmsConfLink + ", vimeoId=" + vimeoId
				+ ", meetingId=" + meetingId + ", sessionId=" + sessionId + ", status=" + status + ", error=" + error
				+ ", vimeoStatus=" + vimeoStatus + ", joinUrl=" + joinUrl + ", hostUrl=" + hostUrl + ", hostKey="
				+ hostKey + ", localTollNumber=" + localTollNumber + ", localTollFree=" + localTollFree
				+ ", globalCallNumber=" + globalCallNumber + ", pstnDialNumber=" + pstnDialNumber + ", participantCode="
				+ participantCode + ", hostId=" + hostId + ", hostPassword=" + hostPassword + ", isAdditionalSession="
				+ isAdditionalSession + ", group1Sem=" + group1Sem + ", group2Sem=" + group2Sem + ", group3Sem="
				+ group3Sem + ", group4Sem=" + group4Sem + ", group5Sem=" + group5Sem + ", group6Sem=" + group6Sem
				+ ", group1Program=" + group1Program + ", group2Program=" + group2Program + ", group3Program="
				+ group3Program + ", group4Program=" + group4Program + ", group5Program=" + group5Program
				+ ", group6Program=" + group6Program + ", facultyId=" + facultyId + ", meetingKey=" + meetingKey
				+ ", meetingPwd=" + meetingPwd + ", altMeetingKey=" + altMeetingKey + ", altMeetingPwd=" + altMeetingPwd
				+ ", altFacultyId=" + altFacultyId + ", altMeetingKey2=" + altMeetingKey2 + ", altMeetingPwd2="
				+ altMeetingPwd2 + ", altFacultyId2=" + altFacultyId2 + ", altMeetingKey3=" + altMeetingKey3
				+ ", altMeetingPwd3=" + altMeetingPwd3 + ", altFacultyId3=" + altFacultyId3 + ", isVerified="
				+ isVerified + ", byPassChecks=" + byPassChecks + ", isCommon=" + isCommon + ", sem=" + sem
				+ ", corporateName=" + corporateName + ", programList=" + programList + ", reviewed=" + reviewed
				+ ", noOfParallelSession=" + noOfParallelSession + ", isCancelled=" + isCancelled
				+ ", reasonForCancellation=" + reasonForCancellation + ", cancellationSMSBody=" + cancellationSMSBody
				+ ", cancellationEmailBody=" + cancellationEmailBody + ", cancellationSubject=" + cancellationSubject
				+ ", startDate=" + startDate + ", endDate=" + endDate + ", altHostKey=" + altHostKey + ", altHostId="
				+ altHostId + ", altHostPassword=" + altHostPassword + ", altHostKey2=" + altHostKey2 + ", altHostId2="
				+ altHostId2 + ", altHostPassword2=" + altHostPassword2 + ", altHostKey3=" + altHostKey3
				+ ", altHostId3=" + altHostId3 + ", altHostPassword3=" + altHostPassword3 + ", facultyLocation="
				+ facultyLocation + ", altFacultyLocation=" + altFacultyLocation + ", altFaculty2Location="
				+ altFaculty2Location + ", altFaculty3Location=" + altFaculty3Location + ", track=" + track
				+ ", videosOfSession=" + videosOfSession + ", facultyName=" + facultyName + ", attended=" + attended
				+ ", device=" + device + ", zoomHostID=" + zoomHostID + ", zoomStatus=" + zoomStatus
				+ ", zoomMeetingID=" + zoomMeetingID + ", hostJoinUrl=" + hostJoinUrl + ", studentJoinUrl="
				+ studentJoinUrl + ", altZoomHostID=" + altZoomHostID + ", altZoomMeetingID=" + altZoomMeetingID
				+ ", altHostJoinUrl=" + altHostJoinUrl + ", altStudentJoinUrl=" + altStudentJoinUrl
				+ ", alt2ZoomHostID=" + alt2ZoomHostID + ", alt2ZoomMeetingID=" + alt2ZoomMeetingID
				+ ", alt2HostJoinUrl=" + alt2HostJoinUrl + ", alt2tStudentJoinUrl=" + alt2tStudentJoinUrl
				+ ", alt3ZoomHostID=" + alt3ZoomHostID + ", alt3ZoomMeetingID=" + alt3ZoomMeetingID
				+ ", alt3HostJoinUrl=" + alt3HostJoinUrl + ", alt3StudentJoinUrl=" + alt3StudentJoinUrl
				+ ", session_id=" + session_id + ", sapId=" + sapId + ", question=" + question + ", answer=" + answer
				+ ", isAnswered=" + isAnswered + ", isPublic=" + isPublic + ", query=" + query + ", prgmSemSubId="
				+ prgmSemSubId + ", sessionModuleNo=" + sessionModuleNo + ", hasModuleId=" + hasModuleId
				+ ", studentType=" + studentType + ", forMobile=" + forMobile + ", moduleId=" + moduleId
				+ ", timeboundId=" + timeboundId + ", consumerTypeId=" + consumerTypeId + ", programId=" + programId
				+ ", programStructureId=" + programStructureId + ", consumerProgramStructureId="
				+ consumerProgramStructureId + ", consumerType=" + consumerType + ", programStructure="
				+ programStructure + ", program=" + program + ", count=" + count + ", imgUrl=" + imgUrl
				+ ", moduleSessionPlanId=" + moduleSessionPlanId + ", sessionplanMonth=" + sessionplanMonth
				+ ", sessionplanYear=" + sessionplanYear + ", sessionplanCreatedBy=" + sessionplanCreatedBy
				+ ", sessionplanLastModifiedBy=" + sessionplanLastModifiedBy + ", sessionplanSubject="
				+ sessionplanSubject + ", dateTime=" + dateTime + ", timebondFacultyId=" + timebondFacultyId
				+ ", isRecommendationSession=" + isRecommendationSession + ", batchUpload=" + batchUpload
				+ ", subject_group=" + subject_group + ", uniqueSemSubject=" + uniqueSemSubject + ", selectAllOptions="
				+ selectAllOptions + ", masterKey=" + masterKey + ", subjectCode=" + subjectCode + ", subjectCodeId="
				+ subjectCodeId + ", processFlag=" + processFlag + ", video_deleted=" + video_deleted
				+ ", byPassFaculty=" + byPassFaculty + ", videoLink=" + videoLink + ", sessionDuration="
				+ sessionDuration + ", sessionPlanModuleCreatedDate=" + sessionPlanModuleCreatedDate
				+ ", sessionPlanModuleLastModifiedDate=" + sessionPlanModuleLastModifiedDate + ", slots=" + slots
				+ ", fromDate=" + fromDate + ", toDate=" + toDate + ", programSemSubjectId=" + programSemSubjectId
				+ ", sessionType=" + sessionType + ", dateTo=" + dateTo + ", dateFrom=" + dateFrom + "]";
	}
	
}
