package com.nmims.dto;

import java.io.Serializable;

public class DissertationResultProcessingDTO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1096263259083571450L;
	private int id ;
	private String sapid;
	private String showResult;
	private String showResultToStudent;
	private int testId;
	private double scoreInInteger;
	private double score;
	private int passScore;
	private String examMonth;
	private String examYear;
	private String acadMonth;
	private String acadYear;
	private int sem;
	private int consumerProgramStructureId;
	private String subject;
	
	
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public String getShowResult() {
		return showResult;
	}
	public void setShowResult(String showResult) {
		this.showResult = showResult;
	}
	public String getShowResultToStudent() {
		return showResultToStudent;
	}
	public void setShowResultToStudent(String showResultToStudent) {
		this.showResultToStudent = showResultToStudent;
	}
	public int getTestId() {
		return testId;
	}
	public void setTestId(int testId) {
		this.testId = testId;
	}
	public double getScoreInInteger() {
		return scoreInInteger;
	}
	public void setScoreInInteger(double scoreInInteger) {
		this.scoreInInteger = scoreInInteger;
	}
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	public int getPassScore() {
		return passScore;
	}
	public void setPassScore(int passScore) {
		this.passScore = passScore;
	}
	public String getExamMonth() {
		return examMonth;
	}
	public void setExamMonth(String examMonth) {
		this.examMonth = examMonth;
	}
	public String getExamYear() {
		return examYear;
	}
	public void setExamYear(String examYear) {
		this.examYear = examYear;
	}
	public String getAcadMonth() {
		return acadMonth;
	}
	public void setAcadMonth(String acadMonth) {
		this.acadMonth = acadMonth;
	}
	public String getAcadYear() {
		return acadYear;
	}
	public void setAcadYear(String acadYear) {
		this.acadYear = acadYear;
	}
	public int getSem() {
		return sem;
	}
	public void setSem(int sem) {
		this.sem = sem;
	}
	public int getConsumerProgramStructureId() {
		return consumerProgramStructureId;
	}
	public void setConsumerProgramStructureId(int consumerProgramStructureId) {
		this.consumerProgramStructureId = consumerProgramStructureId;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	@Override
	public String toString() {
		return "DissertationResultProcessingDTO [id=" + id + ", sapid=" + sapid + ", showResult=" + showResult
				+ ", showResultToStudent=" + showResultToStudent + ", testId=" + testId + ", scoreInInteger="
				+ scoreInInteger + ", score=" + score + ", passScore=" + passScore + ", examMonth=" + examMonth
				+ ", examYear=" + examYear + ", acadMonth=" + acadMonth + ", acadYear=" + acadYear + ", sem=" + sem
				+ ", consumerProgramStructureId=" + consumerProgramStructureId + ", subject=" + subject + "]";
	}
	
	
	
	
	
}