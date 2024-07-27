package com.nmims.dto;

public class PassFailDataDTO extends BaseDTO{
	private String sem;
	private String subject;
	private String writtenscore;
	private String assignmentscore;
	
	private String writtenYear;
	private String writtenMonth;
	private String assignmentYear;
	private String assignmentMonth;

	private String program;
	
	private String gracemarks;
	private String total;
	private String isPass;
	
	private String remarks;

	public PassFailDataDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public PassFailDataDTO(String sapid) {
		super(sapid);
		// TODO Auto-generated constructor stub
	}

	public PassFailDataDTO(String sapid, String sem, String subject, String writtenscore, String assignmentscore,
			String writtenYear, String writtenMonth, String assignmentYear, String assignmentMonth, String program,
			String gracemarks, String total, String isPass, String remarks) {
		super(sapid);
		this.sem = sem;
		this.subject = subject;
		this.writtenscore = writtenscore;
		this.assignmentscore = assignmentscore;
		this.writtenYear = writtenYear;
		this.writtenMonth = writtenMonth;
		this.assignmentYear = assignmentYear;
		this.assignmentMonth = assignmentMonth;
		this.program = program;
		this.gracemarks = gracemarks;
		this.total = total;
		this.isPass = isPass;
		this.remarks = remarks;
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

	public String getWrittenscore() {
		return writtenscore;
	}

	public void setWrittenscore(String writtenscore) {
		this.writtenscore = writtenscore;
	}

	public String getAssignmentscore() {
		return assignmentscore;
	}

	public void setAssignmentscore(String assignmentscore) {
		this.assignmentscore = assignmentscore;
	}

	public String getWrittenYear() {
		return writtenYear;
	}

	public void setWrittenYear(String writtenYear) {
		this.writtenYear = writtenYear;
	}

	public String getWrittenMonth() {
		return writtenMonth;
	}

	public void setWrittenMonth(String writtenMonth) {
		this.writtenMonth = writtenMonth;
	}

	public String getAssignmentYear() {
		return assignmentYear;
	}

	public void setAssignmentYear(String assignmentYear) {
		this.assignmentYear = assignmentYear;
	}

	public String getAssignmentMonth() {
		return assignmentMonth;
	}

	public void setAssignmentMonth(String assignmentMonth) {
		this.assignmentMonth = assignmentMonth;
	}

	public String getProgram() {
		return program;
	}

	public void setProgram(String program) {
		this.program = program;
	}

	public String getGracemarks() {
		return gracemarks;
	}

	public void setGracemarks(String gracemarks) {
		this.gracemarks = gracemarks;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public String getIsPass() {
		return isPass;
	}

	public void setIsPass(String isPass) {
		this.isPass = isPass;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
}
