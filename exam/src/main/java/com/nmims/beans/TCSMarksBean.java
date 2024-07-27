package com.nmims.beans;

import java.io.Serializable;

public class TCSMarksBean extends BaseExamBean implements Serializable {
	private int id;
	private String name;
	private String examCode;
	private String subjectCode;
	private String subjectCount;
	private String examDate;
	private int centerCode;
	private double sectionOneMarks;
	private double sectionTwoMarks;
	private double sectionThreeMarks;
	private double sectionFourMarks;
	private String subject;
	private String year;
	private double totalScore;
	private String month;
	private String program;
	private String sem;
	private String studentType;
	private String applicationStatus;
	private String toDate;
	private String fromDate;
	private String uniqueRequestId;
	private String password;
	private int subjectId;
	private String firstName;
	private String lastName;
	private String center;
	private double sectionFiveMarks;
	private String attendanceStatus;
	private String userId;
	private String applicationSequenceNumber;
	private String examTime;
	private String consumerType;
	
	public String getApplicationStatus() {
		return applicationStatus;
	}
	public void setApplicationStatus(String applicationStatus) {
		this.applicationStatus = applicationStatus;
	}
	public String getToDate() {
		return toDate;
	}
	public void setToDate(String toDate) {
		this.toDate = toDate;
	}
	public String getFromDate() {
		return fromDate;
	}
	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}
	public String getUniqueRequestId() {
		return uniqueRequestId;
	}
	public void setUniqueRequestId(String uniqueRequestId) {
		this.uniqueRequestId = uniqueRequestId;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getSubjectId() {
		return subjectId;
	}
	public void setSubjectId(int subjectId) {
		this.subjectId = subjectId;
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
	public String getCenter() {
		return center;
	}
	public void setCenter(String center) {
		this.center = center;
	}
	public double getSectionFiveMarks() {
		return sectionFiveMarks;
	}
	public void setSectionFiveMarks(double sectionFiveMarks) {
		this.sectionFiveMarks = sectionFiveMarks;
	}
	public String getAttendanceStatus() {
		return attendanceStatus;
	}
	public void setAttendanceStatus(String attendanceStatus) {
		this.attendanceStatus = attendanceStatus;
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
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getExamCode() {
		return examCode;
	}
	public void setExamCode(String examCode) {
		this.examCode = examCode;
	}
	public String getSubjectCode() {
		return subjectCode;
	}
	public void setSubjectCode(String subjectCode) {
		this.subjectCode = subjectCode;
	}
	public String getExamDate() {
		return examDate;
	}
	public void setExamDate(String examDate) {
		this.examDate = examDate;
	}
	public int getCenterCode() {
		return centerCode;
	}
	public void setCenterCode(int centerCode) {
		this.centerCode = centerCode;
	}
	public double getSectionOneMarks() {
		return sectionOneMarks;
	}
	public void setSectionOneMarks(double sectionOneMarks) {
		this.sectionOneMarks = sectionOneMarks;
	}
	public double getSectionTwoMarks() {
		return sectionTwoMarks;
	}
	public void setSectionTwoMarks(double sectionTwoMarks) {
		this.sectionTwoMarks = sectionTwoMarks;
	}
	public double getSectionThreeMarks() {
		return sectionThreeMarks;
	}
	public void setSectionThreeMarks(double sectionThreeMarks) {
		this.sectionThreeMarks = sectionThreeMarks;
	}
	public double getSectionFourMarks() {
		return sectionFourMarks;
	}
	public void setSectionFourMarks(double sectionFourMarks) {
		this.sectionFourMarks = sectionFourMarks;
	}
	public String getSubjectCount() {
		return subjectCount;
	}
	public void setSubjectCount(String subjectCount) {
		this.subjectCount = subjectCount;
	}
	public double getTotalScore() {
		return totalScore;
	}
	public void setTotalScore(double totalScore) {
		this.totalScore = totalScore;
	}
	public String getStudentType() {
		return studentType;
	}
	public void setStudentType(String studentType) {
		this.studentType = studentType;
	}
//	@Override
//	public String toString() {
//		return "SifyMarksBean [id=" + id + ", name=" + name + ", examCode="
//				+ examCode + ", subjectCode=" + subjectCode + ", subjectCount="
//				+ subjectCount + ", examDate=" + examDate + ", centerCode="
//				+ centerCode + ",sectionOneMarks=" + sectionOneMarks
//				+ ", sectionTwoMarks=" + sectionTwoMarks
//				+ ", sectionFourMarks=" + sectionFourMarks + ", subject=" + subject + ", year=" + year
//				+ ", totalScore=" + totalScore + ", month=" + month + "]";
//	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getApplicationSequenceNumber() {
		return applicationSequenceNumber;
	}
	public void setApplicationSequenceNumber(String applicationSequenceNumber) {
		this.applicationSequenceNumber = applicationSequenceNumber;
	}
	public String getExamTime() {
		return examTime;
	}
	public void setExamTime(String examTime) {
		this.examTime = examTime;
	}
	@Override
	public String toString() {
		return "TCSMarksBean [id=" + id + ", sapid=" + getSapid() + ", Unique Request Id=" + uniqueRequestId 
				+ ", name=" + name + ", examDate=" + examDate + ",examTime=" + examTime  
				+ ", year=" + year +" ,month=" +month + " ,subjectCode=" + subjectCode
				+ ", subjectId=" + subjectId  +", subject=" + subject 
				+ " ,sectionOneMarks=" + sectionOneMarks
				+ ", sectionTwoMarks=" + sectionTwoMarks + ", sectionThreeMarks=" + sectionThreeMarks
				+ ", sectionFourMarks=" + sectionFourMarks +  ",sectionFiveMarks=" + sectionFiveMarks 
				+ ", totalScore=" + totalScore + ", centerCode="+ centerCode 
				+ ", Password=" + password
				+ " ,Attendance Status=" +attendanceStatus
				+ "]";
	}
	public String getConsumerType() {
		return consumerType;
	}
	public void setConsumerType(String consumerType) {
		this.consumerType = consumerType;
	}
	
	
	
	
	
	
	
}
