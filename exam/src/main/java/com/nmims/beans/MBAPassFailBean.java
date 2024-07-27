package com.nmims.beans;

import java.io.Serializable;

public class MBAPassFailBean  implements Serializable  {

	private String sapid;
	private String subject;
	private String timeboundId;
	private String term;
	
	private String iaScore;
	private String teeScore;
	private String graceMarks;
	private String totalMarks;
	
	private String isPass;
	private String failReason;
	private String isResultLive;
	private String status;
	private String grade;
	private String points;
	private String credits;
	
	private String prgm_sem_subj_id;

	private String examMonth;
	private String acadMonth;
	private String examYear;
	private String acadYear;
	
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public String getTimeboundId() {
		return timeboundId;
	}
	public void setTimeboundId(String timeboundId) {
		this.timeboundId = timeboundId;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
	}
	public String getIaScore() {
		return iaScore;
	}
	public void setIaScore(String iaScore) {
		this.iaScore = iaScore;
	}
	public String getTeeScore() {
		return teeScore;
	}
	public void setTeeScore(String teeScore) {
		this.teeScore = teeScore;
	}
	public String getGraceMarks() {
		return graceMarks;
	}
	public void setGraceMarks(String graceMarks) {
		this.graceMarks = graceMarks;
	}
	public String getTotalMarks() {
		return totalMarks;
	}
	public void setTotalMarks(String totalMarks) {
		this.totalMarks = totalMarks;
	}
	public String getIsPass() {
		return isPass;
	}
	public void setIsPass(String isPass) {
		this.isPass = isPass;
	}
	public String getFailReason() {
		return failReason;
	}
	public void setFailReason(String failReason) {
		this.failReason = failReason;
	}
	public String getIsResultLive() {
		return isResultLive;
	}
	public void setIsResultLive(String isResultLive) {
		this.isResultLive = isResultLive;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}
	public String getPoints() {
		return points;
	}
	public void setPoints(String points) {
		this.points = points;
	}
	public String getPrgm_sem_subj_id() {
		return prgm_sem_subj_id;
	}
	public void setPrgm_sem_subj_id(String prgm_sem_subj_id) {
		this.prgm_sem_subj_id = prgm_sem_subj_id;
	}
	public String getExamMonth() {
		return examMonth;
	}
	public void setExamMonth(String examMonth) {
		this.examMonth = examMonth;
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
	public String getAcadYear() {
		return acadYear;
	}
	public void setAcadYear(String acadYear) {
		this.acadYear = acadYear;
	}
	public String getCredits() {
		return credits;
	}
	public void setCredits(String credits) {
		this.credits = credits;
	}
}
