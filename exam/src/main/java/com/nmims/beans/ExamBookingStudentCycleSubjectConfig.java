package com.nmims.beans;

import java.io.Serializable;

public class ExamBookingStudentCycleSubjectConfig  implements Serializable  {

	private String year;
	private String month;
	private String sapid;
	private String program;
	private String sem;
	private String subject;
	private String programSemSubjId;
	private String role;
	private String bookingStartDateTime;
	private String bookingEndDateTime;
	
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
	public String getProgramSemSubjId() {
		return programSemSubjId;
	}
	public void setProgramSemSubjId(String programSemSubjId) {
		this.programSemSubjId = programSemSubjId;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getBookingStartDateTime() {
		return bookingStartDateTime;
	}
	public void setBookingStartDateTime(String bookingStartDateTime) {
		this.bookingStartDateTime = bookingStartDateTime;
	}
	public String getBookingEndDateTime() {
		return bookingEndDateTime;
	}
	public void setBookingEndDateTime(String bookingEndDateTime) {
		this.bookingEndDateTime = bookingEndDateTime;
	}
	
}
