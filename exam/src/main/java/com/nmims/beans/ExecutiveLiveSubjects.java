package com.nmims.beans;

import java.io.Serializable;

public class ExecutiveLiveSubjects extends BaseExamBean  implements Serializable  {
	private int id;
	private String subject;
	private String program;
	private String prgmStructApplicable;
	private String acadYear;
	private String acadMonth;
	private String examYear;
	private String examMonth;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getProgram() {
		return program;
	}
	public void setProgram(String program) {
		this.program = program;
	}
	public String getPrgmStructApplicable() {
		return prgmStructApplicable;
	}
	public void setPrgmStructApplicable(String prgmStructApplicable) {
		this.prgmStructApplicable = prgmStructApplicable;
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
	@Override
	public String toString() {
		return "ExecutiveLiveSubjects [id=" + id + ", subject=" + subject
				+ ", program=" + program + ", prgmStructApplicable="
				+ prgmStructApplicable + ", acadYear=" + acadYear
				+ ", acadMonth=" + acadMonth + ", examYear=" + examYear
				+ ", examMonth=" + examMonth + "]";
	}
	
}
