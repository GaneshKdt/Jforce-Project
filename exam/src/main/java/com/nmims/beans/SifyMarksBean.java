package com.nmims.beans;

import java.io.Serializable;

public class SifyMarksBean extends BaseExamBean  implements Serializable   {
	private int id;
	private String name;
	private String examCode;
	private int subjectCode;
	private String subjectCount;
	private String examDate;
	private String centerCode;
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
	public int getSubjectCode() {
		return subjectCode;
	}
	public void setSubjectCode(int subjectCode) {
		this.subjectCode = subjectCode;
	}
	public String getExamDate() {
		return examDate;
	}
	public void setExamDate(String examDate) {
		this.examDate = examDate;
	}
	public String getCenterCode() {
		return centerCode;
	}
	public void setCenterCode(String centerCode) {
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
	@Override
	public String toString() {
		return "SifyMarksBean [id=" + id + ", name=" + name + ", examCode="
				+ examCode + ", subjectCode=" + subjectCode + ", subjectCount="
				+ subjectCount + ", examDate=" + examDate + ", centerCode="
				+ centerCode + ",sectionOneMarks=" + sectionOneMarks
				+ ", sectionTwoMarks=" + sectionTwoMarks
				+ ", sectionFourMarks=" + sectionFourMarks + ", subject=" + subject + ", year=" + year
				+ ", totalScore=" + totalScore + ", month=" + month + "]";
	}
	
	
	
	
	
	
	
}
