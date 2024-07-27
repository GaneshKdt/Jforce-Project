/**
 * 
 */
package com.nmims.beans;

import java.io.Serializable;

/**
 * @author vil_m
 *
 */
public class RemarksGradeResultsDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String year;
	private String month;
	private String sapid;

	private String sem;
	private String subject;
	private String grade;
	private String remarks;
	private String failReason;
	private String scoreTotal;
	
	public RemarksGradeResultsDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public RemarksGradeResultsDTO(String year, String month, String sapid, String sem, String subject) {
		super();
		this.year = year;
		this.month = month;
		this.sapid = sapid;
		this.sem = sem;
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

	public String getSapid() {
		return sapid;
	}

	public void setSapid(String sapid) {
		this.sapid = sapid;
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

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getFailReason() {
		return failReason;
	}

	public void setFailReason(String failReason) {
		this.failReason = failReason;
	}

	public String getScoreTotal() {
		return scoreTotal;
	}

	public void setScoreTotal(String scoreTotal) {
		this.scoreTotal = scoreTotal;
	}
	
}
