package com.nmims.beans;
import java.util.ArrayList;

import java.io.Serializable;

public class SessionAttendanceFeedbackAcads  implements Serializable  {
	
	/**
	 * Change Name from SessionAttendanceFeedback to SessionAttendanceFeedbackAcads for serializable issue
	 */
	
	private String sapId;
	private String sessionId;
	private String attended;
	private String attendTime;
	private String q1Response;
	private String q2Response;
	private String q3Response;
	private String q4Response;
	private String q5Response;
	private String q6Response;
	private String q7Response;
	private String q8Response;
	
	private String q1Remark;
	private String q2Remark;
	private String q3Remark;
	private String q4Remark;
	private String q5Remark;
	private String q6Remark;
	private String q7Remark;
	private String q8Remark;
	
	
	private String q1Average;
	private String q2Average;
	private String q3Average;
	private String q4Average;
	private String q5Average;
	private String q6Average;
	private String q7Average;
	private String q8Average;
	
	private String reAttendTime;
	private String feedbackTime;
	private String feedbackGiven;
	private String feedbackRemarks;
	private String meetingKey;
	
	private String firstName;
	private String lastName;
	private String year;
	private String month;
	private String subject;
	private String sessionName;
	private String facultyFirstName;
	private String facultyLastName;
	private String facultyId;
	private String meetingPwd;
	private int numberOfAttendees;
	private int remainingSeats;
	private String studentConfirmationForAttendance;
	private String reasonForNotAttending;
	private String otherReasonForNotAttending;
	private String grandFacultyAverage;
	private String grandSessionAverage;
	private String corporateName ;
	private String totalResponse;
	private String facultyFullName;
	private String device;
	private String track;
	private String consumerType;
	
	private String hasModuleId;
	private String program;
	private String sem;
	private String date;
	private String isCommon;
	private String joinurl;
	private String id;
	
	private int applicableStudentsForSession;
	private int attendedStudentForSession;
	private String consumerProgramStructureId;
	private String programList;
	
	private String createdBy;
	private String lastModifiedBy;
	private String studentReviewAvg;
	private String day;
	private String startTime;
	
	public String getCorporateName() {
		return corporateName;
	}
	public void setCorporateName(String corporateName) {
		this.corporateName = corporateName;
	}
	public String getGrandFacultyAverage() {
		return grandFacultyAverage;
	}
	public void setGrandFacultyAverage(String grandFacultyAverage) {
		this.grandFacultyAverage = grandFacultyAverage;
	}
	public String getGrandSessionAverage() {
		return grandSessionAverage;
	}
	public void setGrandSessionAverage(String grandSessionAverage) {
		this.grandSessionAverage = grandSessionAverage;
	}
	public String getStudentConfirmationForAttendance() {
		return studentConfirmationForAttendance;
	}
	public void setStudentConfirmationForAttendance(String studentConfirmationForAttendance) {
		this.studentConfirmationForAttendance = studentConfirmationForAttendance;
	}
	public String getReasonForNotAttending() {
		return reasonForNotAttending;
	}
	public void setReasonForNotAttending(String reasonForNotAttending) {
		this.reasonForNotAttending = reasonForNotAttending;
	}
	public String getOtherReasonForNotAttending() {
		return otherReasonForNotAttending;
	}
	public void setOtherReasonForNotAttending(String otherReasonForNotAttending) {
		this.otherReasonForNotAttending = otherReasonForNotAttending;
	}
	public String getQ1Remark() {
		return q1Remark;
	}
	public void setQ1Remark(String q1Remark) {
		this.q1Remark = q1Remark;
	}
	public String getQ2Remark() {
		return q2Remark;
	}
	public void setQ2Remark(String q2Remark) {
		this.q2Remark = q2Remark;
	}
	public String getQ3Remark() {
		return q3Remark;
	}
	public void setQ3Remark(String q3Remark) {
		this.q3Remark = q3Remark;
	}
	public String getQ4Remark() {
		return q4Remark;
	}
	public void setQ4Remark(String q4Remark) {
		this.q4Remark = q4Remark;
	}
	public String getQ5Remark() {
		return q5Remark;
	}
	public void setQ5Remark(String q5Remark) {
		this.q5Remark = q5Remark;
	}
	public String getQ6Remark() {
		return q6Remark;
	}
	public void setQ6Remark(String q6Remark) {
		this.q6Remark = q6Remark;
	}
	public String getQ7Remark() {
		return q7Remark;
	}
	public void setQ7Remark(String q7Remark) {
		this.q7Remark = q7Remark;
	}
	public String getQ8Remark() {
		return q8Remark;
	}
	public void setQ8Remark(String q8Remark) {
		this.q8Remark = q8Remark;
	}
	public int getRemainingSeats() {
		if(remainingSeats <= 0){
			return 0;
		}
		return remainingSeats;
	}
	public void setRemainingSeats(int remainingSeats) {
		this.remainingSeats = remainingSeats;
	}
	public String getQ1Average() {
		return q1Average;
	}
	public void setQ1Average(String q1Average) {
		this.q1Average = q1Average;
	}
	public String getQ2Average() {
		return q2Average;
	}
	public void setQ2Average(String q2Average) {
		this.q2Average = q2Average;
	}
	public String getQ3Average() {
		return q3Average;
	}
	public void setQ3Average(String q3Average) {
		this.q3Average = q3Average;
	}
	public String getQ4Average() {
		return q4Average;
	}
	public void setQ4Average(String q4Average) {
		this.q4Average = q4Average;
	}
	public String getQ5Average() {
		return q5Average;
	}
	public void setQ5Average(String q5Average) {
		this.q5Average = q5Average;
	}
	public String getQ6Average() {
		return q6Average;
	}
	public void setQ6Average(String q6Average) {
		this.q6Average = q6Average;
	}
	public String getQ7Average() {
		return q7Average;
	}
	public void setQ7Average(String q7Average) {
		this.q7Average = q7Average;
	}
	public String getQ8Average() {
		return q8Average;
	}
	public void setQ8Average(String q8Average) {
		this.q8Average = q8Average;
	}
	public int getNumberOfAttendees() {
		return numberOfAttendees;
	}
	public void setNumberOfAttendees(int numberOfAttendees) {
		this.numberOfAttendees = numberOfAttendees;
	}
	public String getQ6Response() {
		return q6Response;
	}
	public void setQ6Response(String q6Response) {
		this.q6Response = q6Response;
	}
	public String getQ7Response() {
		return q7Response;
	}
	public void setQ7Response(String q7Response) {
		this.q7Response = q7Response;
	}
	public String getQ8Response() {
		return q8Response;
	}
	public void setQ8Response(String q8Response) {
		this.q8Response = q8Response;
	}
	public String getMeetingPwd() {
		return meetingPwd;
	}
	public void setMeetingPwd(String meetingPwd) {
		this.meetingPwd = meetingPwd;
	}
	public String getFacultyFirstName() {
		return facultyFirstName;
	}
	public void setFacultyFirstName(String facultyFirstName) {
		this.facultyFirstName = facultyFirstName;
	}
	public String getFacultyLastName() {
		return facultyLastName;
	}
	public void setFacultyLastName(String facultyLastName) {
		this.facultyLastName = facultyLastName;
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
	public String getFeedbackRemarks() {
		return feedbackRemarks;
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
	public void setFeedbackRemarks(String feedbackRemarks) {
		this.feedbackRemarks = feedbackRemarks;
	}
	public String getSapId() {
		return sapId;
	}
	public void setSapId(String sapId) {
		this.sapId = sapId;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public String getAttended() {
		return attended;
	}
	public void setAttended(String attended) {
		this.attended = attended;
	}
	public String getAttendTime() {
		return attendTime;
	}
	public void setAttendTime(String attendTime) {
		this.attendTime = attendTime;
	}
	
	public String getQ1Response() {
		return q1Response;
	}
	public void setQ1Response(String q1Response) {
		this.q1Response = q1Response;
	}
	public String getQ2Response() {
		return q2Response;
	}
	public void setQ2Response(String q2Response) {
		this.q2Response = q2Response;
	}
	public String getQ3Response() {
		return q3Response;
	}
	public void setQ3Response(String q3Response) {
		this.q3Response = q3Response;
	}
	public String getQ4Response() {
		return q4Response;
	}
	public void setQ4Response(String q4Response) {
		this.q4Response = q4Response;
	}
	public String getQ5Response() {
		return q5Response;
	}
	public void setQ5Response(String q5Response) {
		this.q5Response = q5Response;
	}
	public String getReAttendTime() {
		return reAttendTime;
	}
	public void setReAttendTime(String reAttendTime) {
		this.reAttendTime = reAttendTime;
	}
	public String getFeedbackTime() {
		return feedbackTime;
	}
	public void setFeedbackTime(String feedbackTime) {
		this.feedbackTime = feedbackTime;
	}
	public String getFeedbackGiven() {
		return feedbackGiven;
	}
	public void setFeedbackGiven(String feedbackGiven) {
		this.feedbackGiven = feedbackGiven;
	}
	public String getMeetingKey() {
		return meetingKey;
	}
	public void setMeetingKey(String meetingKey) {
		this.meetingKey = meetingKey;
	}
	public String getTotalResponse() {
		return totalResponse;
	}
	public void setTotalResponse(String totalResponse) {
		this.totalResponse = totalResponse;
	}
	public String getFacultyFullName() {
		return facultyFullName;
	}
	public void setFacultyFullName(String facultyFullName) {
		this.facultyFullName = facultyFullName;
	}
	public String getDevice() {
		return device;
	}
	public void setDevice(String device) {
		this.device = device;
	}
	

	@Override
	public String toString() {
		return "SessionAttendanceFeedback [sapId=" + sapId + ", sessionId=" + sessionId + ", attended=" + attended
				+ ", attendTime=" + attendTime + ", q1Response=" + q1Response + ", q2Response=" + q2Response
				+ ", q3Response=" + q3Response + ", q4Response=" + q4Response + ", q5Response=" + q5Response
				+ ", q6Response=" + q6Response + ", q7Response=" + q7Response + ", q8Response=" + q8Response
				+ ", q1Remark=" + q1Remark + ", q2Remark=" + q2Remark + ", q3Remark=" + q3Remark + ", q4Remark="
				+ q4Remark + ", q5Remark=" + q5Remark + ", q6Remark=" + q6Remark + ", q7Remark=" + q7Remark
				+ ", q8Remark=" + q8Remark + ", q1Average=" + q1Average + ", q2Average=" + q2Average + ", q3Average="
				+ q3Average + ", q4Average=" + q4Average + ", q5Average=" + q5Average + ", q6Average=" + q6Average
				+ ", q7Average=" + q7Average + ", q8Average=" + q8Average + ", reAttendTime=" + reAttendTime
				+ ", feedbackTime=" + feedbackTime + ", feedbackGiven=" + feedbackGiven + ", feedbackRemarks="
				+ feedbackRemarks + ", meetingKey=" + meetingKey + ", firstName=" + firstName + ", lastName=" + lastName
				+ ", year=" + year + ", month=" + month + ", subject=" + subject + ", sessionName=" + sessionName
				+ ", facultyFirstName=" + facultyFirstName + ", facultyLastName=" + facultyLastName + ", facultyId="
				+ facultyId + ", meetingPwd=" + meetingPwd + ", numberOfAttendees=" + numberOfAttendees
				+ ", remainingSeats=" + remainingSeats + ", studentConfirmationForAttendance="
				+ studentConfirmationForAttendance + ", reasonForNotAttending=" + reasonForNotAttending
				+ ", otherReasonForNotAttending=" + otherReasonForNotAttending + ", grandFacultyAverage="
				+ grandFacultyAverage + ", grandSessionAverage=" + grandSessionAverage + ", corporateName="
				+ corporateName + ", totalResponse=" + totalResponse + ", facultyFullName=" + facultyFullName
				+ ", device=" + device + ", track=" + track + ", consumerType=" + consumerType + ", hasModuleId="
				+ hasModuleId + ", program=" + program + ", sem=" + sem + ", date=" + date + ", isCommon=" + isCommon
				+ ", joinurl=" + joinurl + ", id=" + id + ", applicableStudentsForSession="
				+ applicableStudentsForSession + ", attendedStudentForSession=" + attendedStudentForSession
				+ ", consumerProgramStructureId=" + consumerProgramStructureId + ", programList=" + programList
				+ ", createdBy=" + createdBy + ", lastModifiedBy=" + lastModifiedBy + "]";
	}
	public String getTrack() {
		return track;
	}
	public void setTrack(String track) {
		this.track = track;
	}
	public String getConsumerType() {
		return consumerType;
	}
	public void setConsumerType(String consumerType) {
		this.consumerType = consumerType;
	}
	public String getHasModuleId() {
		return hasModuleId;
	}
	public void setHasModuleId(String hasModuleId) {
		this.hasModuleId = hasModuleId;
	}
	public String getProgram() {
		return program;
	}
	public void setProgram(String program) {
		this.program = program;
	}
	public String getSem() {
		return sem;
	}
	public void setSem(String sem) {
		this.sem = sem;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getIsCommon() {
		return isCommon;
	}
	public void setIsCommon(String isCommon) {
		this.isCommon = isCommon;
	}
	public String getJoinurl() {
		return joinurl;
	}
	public void setJoinurl(String joinurl) {
		this.joinurl = joinurl;
	}
	public int getApplicableStudentsForSession() {
		return applicableStudentsForSession;
	}
	public void setApplicableStudentsForSession(int applicableStudentsForSession) {
		this.applicableStudentsForSession = applicableStudentsForSession;
	}
	public int getAttendedStudentForSession() {
		return attendedStudentForSession;
	}
	public void setAttendedStudentForSession(int attendedStudentForSession) {
		this.attendedStudentForSession = attendedStudentForSession;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getConsumerProgramStructureId() {
		return consumerProgramStructureId;
	}
	public void setConsumerProgramStructureId(String consumerProgramStructureId) {
		this.consumerProgramStructureId = consumerProgramStructureId;
	}
	public String getProgramList() {
		return programList;
	}
	public void setProgramList(String programList) {
		this.programList = programList;
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
	public String getStudentReviewAvg() {
		return studentReviewAvg;
	}
	public void setStudentReviewAvg(String studentReviewAvg) {
		this.studentReviewAvg = studentReviewAvg;
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
	
}
