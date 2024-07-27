package com.nmims.beans;

import java.io.Serializable;

/**
 * old name - SessionQueryAnswer
 * @author
 *
 */
public class SessionQueryAnswerStudentPortal implements Serializable{
	
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
	
	private String year;
	private String month;
	private String subject;
	private String sessionName;
	private String date;
	private String day;
	private String startTime;
	private String facultyId;
	private String hoursSinceQuestions;
	private String firstName;
	private String lastName;
	private String email;
	private String queryType;
	private String status;

	private String assignedToFacultyId;
	private String errorMessage ; // store error message received from Salesforce while creating cases record in salesforce
	private String caseId; // salesforce Case Id
	private String timeBoundId;
	private String hasTimeBoundId;
	private Integer programSemSubjectId;
	
	
	public String getQueryType() {
		return queryType;
	}
	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}
	public String getStatus() {
		if(this.answer == null){
			return "Open";
		}else{
			return "Answered";
		}
	}
	public void setStatus(String status) {
		this.status = status;
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
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
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

	public String getAssignedToFacultyId() {
		return assignedToFacultyId;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public String getCaseId() {
		return caseId;
	}

	public String getTimeBoundId() {
		return timeBoundId;
	}

	public String getHasTimeBoundId() {
		return hasTimeBoundId;
	}

	public void setAssignedToFacultyId(String assignedToFacultyId) {
		this.assignedToFacultyId = assignedToFacultyId;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public void setCaseId(String caseId) {
		this.caseId = caseId;
	}

	public void setTimeBoundId(String timeBoundId) {
		this.timeBoundId = timeBoundId;
	}

	public void setHasTimeBoundId(String hasTimeBoundId) {
		this.hasTimeBoundId = hasTimeBoundId;
	}

	
	
	public Integer getProgramSemSubjectId() {
		return programSemSubjectId;
	}
	public void setProgramSemSubjectId(Integer programSemSubjectId) {
		this.programSemSubjectId = programSemSubjectId;
	}
	@Override
	public String toString() {
		return "SessionQueryAnswerStudentPortal{" +
				"id='" + id + '\'' +
				", sessionId='" + sessionId + '\'' +
				", sapId='" + sapId + '\'' +
				", query='" + query + '\'' +
				", answer='" + answer + '\'' +
				", isPublic='" + isPublic + '\'' +
				", createdBy='" + createdBy + '\'' +
				", createdDate='" + createdDate + '\'' +
				", lastModifiedBy='" + lastModifiedBy + '\'' +
				", lastModifiedDate='" + lastModifiedDate + '\'' +
				", isAnswered='" + isAnswered + '\'' +
				", year='" + year + '\'' +
				", month='" + month + '\'' +
				", subject='" + subject + '\'' +
				", sessionName='" + sessionName + '\'' +
				", date='" + date + '\'' +
				", day='" + day + '\'' +
				", startTime='" + startTime + '\'' +
				", facultyId='" + facultyId + '\'' +
				", hoursSinceQuestions='" + hoursSinceQuestions + '\'' +
				", firstName='" + firstName + '\'' +
				", lastName='" + lastName + '\'' +
				", email='" + email + '\'' +
				", queryType='" + queryType + '\'' +
				", status='" + status + '\'' +
				", assignedToFacultyId='" + assignedToFacultyId + '\'' +
				", errorMessage='" + errorMessage + '\'' +
				", caseId='" + caseId + '\'' +
				", timeBoundId='" + timeBoundId + '\'' +
				", hasTimeBoundId='" + hasTimeBoundId + '\'' +
				'}';
	}
}
