package com.nmims.beans;

import java.io.Serializable;

public class LevelBasedSOPConfigBean  implements Serializable  {
	
	private String year;
	private String month;
	private int consumer_program_structure_id;
	private int program_sem_subject_id;
	private String live;
	private int max_attempt;
	private String payment_applicable;
	private String payment_amount;
	private String start_date;
	private String end_date;
	private String consumer_type;
	private String program_structure;
	private String subject;
	private String program;
	private String faculty_id;
	private String facultyName;
	private String createdBy;
	private String lastModifiedBy;
	
	private String error;

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

	public int getConsumer_program_structure_id() {
		return consumer_program_structure_id;
	}

	public void setConsumer_program_structure_id(int consumer_program_structure_id) {
		this.consumer_program_structure_id = consumer_program_structure_id;
	}

	public String getLive() {
		return live;
	}

	public void setLive(String live) {
		this.live = live;
	}

	public int getMax_attempt() {
		return max_attempt;
	}

	public void setMax_attempt(int max_attempt) {
		this.max_attempt = max_attempt;
	}

	public String getPayment_applicable() {
		return payment_applicable;
	}

	public void setPayment_applicable(String payment_applicable) {
		this.payment_applicable = payment_applicable;
	}

	public String getPayment_amount() {
		return payment_amount;
	}

	public void setPayment_amount(String payment_amount) {
		this.payment_amount = payment_amount;
	}

	public String getStart_date() {
		return start_date;
	}

	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}

	public String getEnd_date() {
		return end_date;
	}

	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}

	public String getConsumer_type() {
		return consumer_type;
	}

	public void setConsumer_type(String consumer_type) {
		this.consumer_type = consumer_type;
	}

	public String getProgram_structure() {
		return program_structure;
	}

	public void setProgram_structure(String program_structure) {
		this.program_structure = program_structure;
	}

	public String getProgram() {
		return program;
	}

	public void setProgram(String program) {
		this.program = program;
	}
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}


	public String getFaculty_id() {
		return faculty_id;
	}

	public void setFaculty_id(String faculty_id) {
		this.faculty_id = faculty_id;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}

	public String getFacultyName() {
		return facultyName;
	}

	public void setFacultyName(String facultyName) {
		this.facultyName = facultyName;
	}

	public int getProgram_sem_subject_id() {
		return program_sem_subject_id;
	}

	public void setProgram_sem_subject_id(int program_sem_subject_id) {
		this.program_sem_subject_id = program_sem_subject_id;
	}
}
