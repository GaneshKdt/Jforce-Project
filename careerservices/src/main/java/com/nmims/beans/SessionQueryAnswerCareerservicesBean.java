package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class SessionQueryAnswerCareerservicesBean implements Serializable{
	
	private String id;
	private String sessionId;
	private String sapId;
	private String query;
	private String answer;
	private String isPublic;
	private String createdBy;
	private String createdDate;
	private String lastModifiedBy;
	private String lastModifiedDate;
	private String isAnswered;

	private String studentName;
	private String facultyName;
	private String sessionName;
	private String date;
	private String day;
	private String startTime;
	private String facultyId;
	private String hoursSinceQuestions;
	private int hoursSinceFacultyReply;
	private String firstName;
	private String lastName;
	private String email;
	public List<Long> listOfRecordIdToBeAssigned;
	//public String assignedFaculty;
	private String assignedToFacultyId;
	private String answeredByFacultyId;
	public String queryType;
	private String errorMessage ; // store error message received from Salesforce while creating cases record in salesforce
	private String caseId; // salesforce Case Id
	/*public String getAssignedFaculty() {
		return assignedFaculty;
	}
	public void setAssignedFaculty(String assignedFaculty) {
		this.assignedFaculty = assignedFaculty;
	}*/
	
	private String status;
	private String meetingKey	;

	private String question;
	private String dateModified;
	
	
	public String getMeetingKey() {
		return meetingKey;
	}
	public void setMeetingKey(String meetingKey) {
		this.meetingKey = meetingKey;
	}

	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getDateModified() {
		return dateModified;
	}
	public void setDateModified(String dateModified) {
		this.dateModified = dateModified;
	}
	public String getStatus() {
		if(this.answer.isEmpty( ) ){  
			return "Open";
		}else{
			return "Answered";
		}
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	
	public List<Long> getListOfRecordIdToBeAssigned() {
		return listOfRecordIdToBeAssigned;
	}
	
	public String getCaseId() {
		return caseId;
	}

	public void setCaseId(String caseId) {
		this.caseId = caseId;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getQueryType() {
		return queryType;
	}

	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}

	public String getAssignedToFacultyId() {
		return assignedToFacultyId;
	}
	public void setAssignedToFacultyId(String assignedToFacultyId) {
		this.assignedToFacultyId = assignedToFacultyId;
	}
	public String getAnsweredByFacultyId() {
		return answeredByFacultyId;
	}
	public void setAnsweredByFacultyId(String answeredByFacultyId) {
		this.answeredByFacultyId = answeredByFacultyId;
	}
	public void setListOfRecordIdToBeAssigned(List<Long> listOfRecordIdToBeAssigned) {
		this.listOfRecordIdToBeAssigned = listOfRecordIdToBeAssigned;
	}
	public int getHoursSinceFacultyReply() {
		return hoursSinceFacultyReply;
	}
	public void setHoursSinceFacultyReply(int hoursSinceFacultyReply) {
		this.hoursSinceFacultyReply = hoursSinceFacultyReply;
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
	public String getHoursSinceQuestions() {
		return hoursSinceQuestions;
	}
	public void setHoursSinceQuestions(String hoursSinceQuestions) {
		this.hoursSinceQuestions = hoursSinceQuestions;
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
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
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
//	public String getYear() {
//		return year;
//	}
//	public void setYear(String year) {
//		this.year = year;
//	}
//	public String getMonth() {
//		return month;
//	}
//	public void setMonth(String month) {
//		this.month = month;
//	}
	public String getIsAnswered() {
		return isAnswered;
	}
	public void setIsAnswered(String isAnswered) {
		this.isAnswered = isAnswered;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public String getSapId() {
		return sapId;
	}
	public void setSapId(String sapId) {
		this.sapId = sapId;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	public String getIsPublic() {
		return isPublic;
	}
	public void setIsPublic(String isPublic) {
		this.isPublic = isPublic;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getCreatedDate() {
		if(createdDate != null){
			return createdDate.substring(0, 16);
		}
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
		if(lastModifiedDate != null){
			return lastModifiedDate.substring(0,16);
		}
		return lastModifiedDate;
	}
	public void setLastModifiedDate(String lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	@Override
	public String toString() {
		return "SessionQueryAnswer [id=" + id + ", sessionId=" + sessionId
				+ ", sapId=" + sapId + ", query=" + query + ", answer="
				+ answer + ", isPublic=" + isPublic + ", createdBy="
				+ createdBy + ", createdDate=" + createdDate
				+ ", lastModifiedBy=" + lastModifiedBy + ", lastModifiedDate="
				+ lastModifiedDate + ", isAnswered=" + isAnswered
				+ ", sessionName=" + sessionName + ", date=" + date + ", day="
				+ day + ", startTime=" + startTime + ", facultyId=" + facultyId
				+ ", hoursSinceQuestions=" + hoursSinceQuestions
				+ ", hoursSinceFacultyReply=" + hoursSinceFacultyReply
				+ ", firstName=" + firstName + ", lastName=" + lastName
				+ ", email=" + email + ", listOfRecordIdToBeAssigned="
				+ listOfRecordIdToBeAssigned + ", assignedToFacultyId="
				+ assignedToFacultyId + ", answeredByFacultyId="
				+ answeredByFacultyId + ", queryType=" + queryType
				+ ", errorMessage=" + errorMessage + ", caseId=" + caseId
				+ ", status=" + status + "]";
	}
	public String getFacultyName() {
		return facultyName;
	}
	public void setFacultyName(String facultyName) {
		this.facultyName = facultyName;
	}
	public String getStudentName() {
		return studentName;
	}
	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}
	
	

}
