package com.nmims.beans;

import java.io.Serializable;

public class ReExamEligibleStudentBean  implements Serializable  {
	private String sapid;
	private String subject;
	private String sem;
	private String timeboundId;
	private String acadYear;
	private String acadMonth;
	private String examYear;
	private String examMonth;
	private String isPass;
	private String role;
	private String eligibleForReExam;
	private String notEligibleReason;
	
	private int iaScore;
	private int teeScore;
	
	private int numberOfSubjects;

	public String getSapid() {
		return sapid;
	}

	public void setSapid(String sapid) {
		this.sapid = sapid;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getSem() {
		return sem;
	}

	public void setSem(String sem) {
		this.sem = sem;
	}

	public String getTimeboundId() {
		return timeboundId;
	}

	public void setTimeboundId(String timeboundId) {
		this.timeboundId = timeboundId;
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

	public String getExamYear() {
		return examYear;
	}

	public void setExamYear(String examYear) {
		this.examYear = examYear;
	}

	public String getExamMonth() {
		return examMonth;
	}

	public void setExamMonth(String examMonth) {
		this.examMonth = examMonth;
	}

	public String getIsPass() {
		return isPass;
	}

	public void setIsPass(String isPass) {
		this.isPass = isPass;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getEligibleForReExam() {
		return eligibleForReExam;
	}

	public void setEligibleForReExam(String eligibleForReExam) {
		this.eligibleForReExam = eligibleForReExam;
	}

	public String getNotEligibleReason() {
		return notEligibleReason;
	}

	public void setNotEligibleReason(String notEligibleReason) {
		this.notEligibleReason = notEligibleReason;
	}

	public int getIaScore() {
		return iaScore;
	}

	public void setIaScore(int iaScore) {
		this.iaScore = iaScore;
	}

	public int getTeeScore() {
		return teeScore;
	}

	public void setTeeScore(int teeScore) {
		this.teeScore = teeScore;
	}

	public int getNumberOfSubjects() {
		return numberOfSubjects;
	}

	public void setNumberOfSubjects(int numberOfSubjects) {
		this.numberOfSubjects = numberOfSubjects;
	}

	@Override
	public String toString() {
		return "ReExamEligibleStudentBean [sapid=" + sapid + ", subject=" + subject + ", sem=" + sem + ", timeboundId="
				+ timeboundId + ", acadYear=" + acadYear + ", acadMonth=" + acadMonth + ", examYear=" + examYear
				+ ", examMonth=" + examMonth + ", isPass=" + isPass + ", role=" + role + ", eligibleForReExam="
				+ eligibleForReExam + ", notEligibleReason=" + notEligibleReason + ", iaScore=" + iaScore
				+ ", teeScore=" + teeScore + ", numberOfSubjects=" + numberOfSubjects + "]";
	}
}
