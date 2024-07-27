package com.nmims.beans;

import java.io.Serializable;

public class MettlEvaluatorInfo  implements Serializable   {

	private String sapid;
	private String student_name;
	private String firstName;
	private String lastName;
	private String subject;
	private String questionText;
	private String scheduleAccessKey;
	private String sectionName;
	private String questionId;
	private String evaluatorEmail;
	private String evaluatorName;
	private double marksAwarded;
	private String evaluationComments;
	private String evaluationTime;
	private String evaluatorRole;
	
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
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
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getQuestionText() {
		return questionText;
	}
	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}
	public String getScheduleAccessKey() {
		return scheduleAccessKey;
	}
	public void setScheduleAccessKey(String scheduleAccessKey) {
		this.scheduleAccessKey = scheduleAccessKey;
	}
	public String getSectionName() {
		return sectionName;
	}
	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}
	public String getQuestionId() {
		return questionId;
	}
	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}
	public String getEvaluatorEmail() {
		return evaluatorEmail;
	}
	public void setEvaluatorEmail(String evaluatorEmail) {
		this.evaluatorEmail = evaluatorEmail;
	}
	public String getEvaluatorName() {
		return evaluatorName;
	}
	public void setEvaluatorName(String evaluatorName) {
		this.evaluatorName = evaluatorName;
	}
	public double getMarksAwarded() {
		return marksAwarded;
	}
	public void setMarksAwarded(double marksAwarded) {
		this.marksAwarded = marksAwarded;
	}
	public String getEvaluationComments() {
		return evaluationComments;
	}
	public void setEvaluationComments(String evaluationComments) {
		this.evaluationComments = evaluationComments;
	}
	public String getEvaluationTime() {
		return evaluationTime;
	}
	public void setEvaluationTime(String evaluationTime) {
		this.evaluationTime = evaluationTime;
	}
	public String getEvaluatorRole() {
		return evaluatorRole;
	}
	public void setEvaluatorRole(String evaluatorRole) {
		this.evaluatorRole = evaluatorRole;
	}
	@Override
	public String toString() {
		return "MettlEvaluatorInfo [sapid=" + sapid + ", firstName=" + firstName + ", lastName=" + lastName
				+ ", subject=" + subject + ", questionText=" + questionText + ", scheduleAccessKey=" + scheduleAccessKey
				+ ", sectionName=" + sectionName + ", questionId=" + questionId + ", evaluatorEmail=" + evaluatorEmail
				+ ", evaluatorName=" + evaluatorName + ", marksAwarded=" + marksAwarded + ", evaluationComments="
				+ evaluationComments + ", evaluationTime=" + evaluationTime + ", evaluatorRole=" + evaluatorRole + "]";
	}
	public String getStudent_name() {
		return student_name;
	}
	public void setStudent_name(String student_name) {
		this.student_name = student_name;
	}
}
