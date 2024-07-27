package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

public class MettlSectionQuestionResponse  implements Serializable  {
	
	private String sapid;
	private String student_name;
	private String firstName;
	private String lastName;
	private String subject;
	private String questionText;
	private String scheduleAccessKey;
	private String sectionName;
	private String questionId;
	private String apiQuestionType;
	private String version;
	private String studentResponse;
	private double minMarks;
	private double maxMarks;
	private double marksScored;
	private boolean isAttempted;
	private int timeSpent;
	private boolean benfitOfDoubtQuestion;
	private double bonusMarks;
	
	private List<MettlEvaluatorInfo> evaluatorInfo;
	
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
	public String getApiQuestionType() {
		return apiQuestionType;
	}
	public void setApiQuestionType(String apiQuestionType) {
		this.apiQuestionType = apiQuestionType;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getStudentResponse() {
		return studentResponse;
	}
	public void setStudentResponse(String studentResponse) {
		this.studentResponse = studentResponse;
	}
	public double getMinMarks() {
		return minMarks;
	}
	public void setMinMarks(double minMarks) {
		this.minMarks = minMarks;
	}
	public double getMaxMarks() {
		return maxMarks;
	}
	public void setMaxMarks(double maxMarks) {
		this.maxMarks = maxMarks;
	}
	public double getMarksScored() {
		return marksScored;
	}
	public void setMarksScored(double marksScored) {
		this.marksScored = marksScored;
	}
	public boolean isAttempted() {
		return isAttempted;
	}
	public void setAttempted(boolean isAttempted) {
		this.isAttempted = isAttempted;
	}
	public int getTimeSpent() {
		return timeSpent;
	}
	public void setTimeSpent(int timeSpent) {
		this.timeSpent = timeSpent;
	}
	public boolean isBenfitOfDoubtQuestion() {
		return benfitOfDoubtQuestion;
	}
	public void setBenfitOfDoubtQuestion(boolean benfitOfDoubtQuestion) {
		this.benfitOfDoubtQuestion = benfitOfDoubtQuestion;
	}
	public double getBonusMarks() {
		return bonusMarks;
	}
	public void setBonusMarks(double bonusMarks) {
		this.bonusMarks = bonusMarks;
	}
	public List<MettlEvaluatorInfo> getEvaluatorInfo() {
		return evaluatorInfo;
	}
	public void setEvaluatorInfo(List<MettlEvaluatorInfo> evaluatorInfo) {
		this.evaluatorInfo = evaluatorInfo;
	}
	@Override
	public String toString() {
		return "MettlSectionQuestionResponse [sapid=" + sapid + ", firstName=" + firstName + ", lastName=" + lastName
				+ ", subject=" + subject + ", questionText=" + questionText + ", scheduleAccessKey=" + scheduleAccessKey
				+ ", sectionName=" + sectionName + ", questionId=" + questionId + ", apiQuestionType=" + apiQuestionType
				+ ", version=" + version + ", studentResponse=" + studentResponse + ", minMarks=" + minMarks
				+ ", maxMarks=" + maxMarks + ", marksScored=" + marksScored + ", isAttempted=" + isAttempted
				+ ", timeSpent=" + timeSpent + ", benfitOfDoubtQuestion=" + benfitOfDoubtQuestion + ", bonusMarks="
				+ bonusMarks + ", evaluatorInfo=" + evaluatorInfo + "]";
	}
	public String getStudent_name() {
		return student_name;
	}
	public void setStudent_name(String student_name) {
		this.student_name = student_name;
	}
}
