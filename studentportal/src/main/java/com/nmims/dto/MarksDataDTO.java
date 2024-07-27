package com.nmims.dto;

public class MarksDataDTO extends BaseDTO{
	private String year;
	private String month;
	
	private String sem;
	private String subject;
	private String writenscore;
	private String assignmentscore;
	private String mcq;
	private String part4marks;
	private String remarks;
	
	public MarksDataDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	public MarksDataDTO(String sapid) {
		super(sapid);
		// TODO Auto-generated constructor stub
	}
	
	public MarksDataDTO(String sapid, String year, String month, String sem, String subject, String writenscore,
			String assignmentscore, String mcq, String part4marks, String remarks) {
		super(sapid);
		this.year = year;
		this.month = month;
		this.sem = sem;
		this.subject = subject;
		this.writenscore = writenscore;
		this.assignmentscore = assignmentscore;
		this.mcq = mcq;
		this.part4marks = part4marks;
		this.remarks = remarks;
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
	public String getWritenscore() {
		return writenscore;
	}
	public void setWritenscore(String writenscore) {
		this.writenscore = writenscore;
	}
	public String getAssignmentscore() {
		return assignmentscore;
	}
	public void setAssignmentscore(String assignmentscore) {
		this.assignmentscore = assignmentscore;
	}
	public String getMcq() {
		return mcq;
	}
	public void setMcq(String mcq) {
		this.mcq = mcq;
	}
	public String getPart4marks() {
		return part4marks;
	}
	public void setPart4marks(String part4marks) {
		this.part4marks = part4marks;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
}
