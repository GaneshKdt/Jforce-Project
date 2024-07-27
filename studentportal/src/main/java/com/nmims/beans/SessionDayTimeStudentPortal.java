package com.nmims.beans;

import java.io.Serializable;

/**
 * old name - SessionDayTimeBean
 * @author
 *
 */
public class SessionDayTimeStudentPortal implements Serializable{
	
	private String id;
	private String year;
	private String month;
	private String date;
	private String day;
	private String startTime;
	private String endTime;
	private String duration;
	private String sessionName;
	private String subject;
	private String facultyId;
	private String room;
	private String ciscoStatus;
	private String createdBy;
	private String createdDate;
	private String lastModifiedBy;
	private String lastModifiedDate;
	private String errorMessage = "";
	private String track;
	private boolean errorRecord = false;
	private String firstName;
	private String lastName;
	private String email;
	private String mobile;

	private Integer tmsConfId;
	private String tmsConfLink;
	private String meetingKey;
	private String meetingPwd;
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
	private String group1Program;
	private String group2Program;
	private String group3Program;
	private String group4Program;

	private String byPassChecks = "N";

	private String corporateName;
	private String altFacultyId;
	private String altFacultyId2;
	private String altFacultyId3;

	private String prgmSemSubId;

	public String getCorporateName() {
		return corporateName;
	}
	public void setCorporateName(String corporateName) {
		this.corporateName = corporateName;
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

	public String getTrack() {
		return track;
	}

	public void setTrack(String track) {
		this.track = track;
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
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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

	public String getAltFacultyId2() {
		return altFacultyId2;
	}

	public void setAltFacultyId2(String altFacultyId2) {
		this.altFacultyId2 = altFacultyId2;
	}

	public String getAltFacultyId3() {
		return altFacultyId3;
	}

	public void setAltFacultyId3(String altFacultyId3) {
		this.altFacultyId3 = altFacultyId3;
	}
	
	


	public String getPrgmSemSubId() {
		return prgmSemSubId;
	}
	public void setPrgmSemSubId(String prgmSemSubId) {
		this.prgmSemSubId = prgmSemSubId;
	}
	@Override
	public String toString() {
		return "SessionDayTimeStudentPortal{" +
				"id='" + id + '\'' +
				", year='" + year + '\'' +
				", month='" + month + '\'' +
				", date='" + date + '\'' +
				", day='" + day + '\'' +
				", startTime='" + startTime + '\'' +
				", endTime='" + endTime + '\'' +
				", duration='" + duration + '\'' +
				", sessionName='" + sessionName + '\'' +
				", subject='" + subject + '\'' +
				", facultyId='" + facultyId + '\'' +
				", room='" + room + '\'' +
				", ciscoStatus='" + ciscoStatus + '\'' +
				", createdBy='" + createdBy + '\'' +
				", createdDate='" + createdDate + '\'' +
				", lastModifiedBy='" + lastModifiedBy + '\'' +
				", lastModifiedDate='" + lastModifiedDate + '\'' +
				", errorMessage='" + errorMessage + '\'' +
				", errorRecord=" + errorRecord +
				", track='" + track + '\'' +
				", firstName='" + firstName + '\'' +
				", lastName='" + lastName + '\'' +
				", email='" + email + '\'' +
				", mobile='" + mobile + '\'' +
				", tmsConfId=" + tmsConfId +
				", tmsConfLink='" + tmsConfLink + '\'' +
				", meetingKey='" + meetingKey + '\'' +
				", meetingPwd='" + meetingPwd + '\'' +
				", joinUrl='" + joinUrl + '\'' +
				", hostUrl='" + hostUrl + '\'' +
				", hostKey='" + hostKey + '\'' +
				", localTollNumber='" + localTollNumber + '\'' +
				", localTollFree='" + localTollFree + '\'' +
				", globalCallNumber='" + globalCallNumber + '\'' +
				", pstnDialNumber='" + pstnDialNumber + '\'' +
				", participantCode='" + participantCode + '\'' +
				", hostId='" + hostId + '\'' +
				", hostPassword='" + hostPassword + '\'' +
				", isAdditionalSession='" + isAdditionalSession + '\'' +
				", group1Sem='" + group1Sem + '\'' +
				", group2Sem='" + group2Sem + '\'' +
				", group3Sem='" + group3Sem + '\'' +
				", group4Sem='" + group4Sem + '\'' +
				", group1Program='" + group1Program + '\'' +
				", group2Program='" + group2Program + '\'' +
				", group3Program='" + group3Program + '\'' +
				", group4Program='" + group4Program + '\'' +
				", byPassChecks='" + byPassChecks + '\'' +
				", corporateName='" + corporateName + '\'' +
				", altFacultyId='" + altFacultyId + '\'' +
				", altFacultyId2='" + altFacultyId2 + '\'' +
				", altFacultyId3='" + altFacultyId3 + '\'' +
				'}';
	}


}
