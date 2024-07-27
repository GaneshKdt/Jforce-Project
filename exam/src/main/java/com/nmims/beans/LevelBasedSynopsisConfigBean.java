package com.nmims.beans;

import java.io.Serializable;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class LevelBasedSynopsisConfigBean  implements Serializable   {
	private String year;
	private String month;
	private String live;
	private String consumer_type;
	private String program;
	private String program_structure;
	private CommonsMultipartFile fileData;
	private int consumer_program_structure_id;
	private int program_sem_subject_id;
	private int max_attempt;
	private String program_sem_subject_ids;
	private String question_filePath;
	private String question_previewPath;
	private String payment_amount;
	private String payment_applicable;
	private String subject;
	private String start_date;
	private String end_date;
	private String created_at;
	private String updated_at;
	private String created_by;
	private String updated_by;
	
	
	
	public String getProgram_sem_subject_ids() {
		return program_sem_subject_ids;
	}
	public void setProgram_sem_subject_ids(String program_sem_subject_ids) {
		this.program_sem_subject_ids = program_sem_subject_ids;
	}
	public String getConsumer_type() {
		return consumer_type;
	}
	public void setConsumer_type(String consumer_type) {
		this.consumer_type = consumer_type;
	}
	public String getProgram() {
		return program;
	}
	public void setProgram(String program) {
		this.program = program;
	}
	public String getProgram_structure() {
		return program_structure;
	}
	public void setProgram_structure(String program_structure) {
		this.program_structure = program_structure;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public CommonsMultipartFile getFileData() {
		return fileData;
	}
	public void setFileData(CommonsMultipartFile fileData) {
		this.fileData = fileData;
	}
	
	public int getProgram_sem_subject_id() {
		return program_sem_subject_id;
	}
	public void setProgram_sem_subject_id(int program_sem_subject_id) {
		this.program_sem_subject_id = program_sem_subject_id;
	}
	public String getQuestion_filePath() {
		return question_filePath;
	}
	public void setQuestion_filePath(String question_filePath) {
		this.question_filePath = question_filePath;
	}
	public String getQuestion_previewPath() {
		return question_previewPath;
	}
	public void setQuestion_previewPath(String question_previewPath) {
		this.question_previewPath = question_previewPath;
	}
	public String getCreated_at() {
		return created_at;
	}
	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}
	public String getUpdated_at() {
		return updated_at;
	}
	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}
	public String getCreated_by() {
		return created_by;
	}
	public void setCreated_by(String created_by) {
		this.created_by = created_by;
	}
	public String getUpdated_by() {
		return updated_by;
	}
	public void setUpdated_by(String updated_by) {
		this.updated_by = updated_by;
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
	public int getConsumer_program_structure_id() {
		return consumer_program_structure_id;
	}
	public void setConsumer_program_structure_id(int consumer_program_structure_id) {
		this.consumer_program_structure_id = consumer_program_structure_id;
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
	public String getLive() {
		return live;
	}
	public void setLive(String live) {
		this.live = live;
	}
	@Override
	public String toString() {
		return "LevelBasedSynopsisConfigBean [year=" + year + ", month=" + month + ", live=" + live + ", consumer_type="
				+ consumer_type + ", program=" + program + ", program_structure=" + program_structure + ", fileData="
				+ fileData + ", consumer_program_structure_id=" + consumer_program_structure_id
				+ ", program_sem_subject_id=" + program_sem_subject_id + ", max_attempt=" + max_attempt
				+ ", program_sem_subject_ids=" + program_sem_subject_ids + ", question_filePath=" + question_filePath
				+ ", question_previewPath=" + question_previewPath + ", payment_amount=" + payment_amount
				+ ", payment_applicable=" + payment_applicable + ", subject=" + subject + ", start_date=" + start_date
				+ ", end_date=" + end_date + ", created_at=" + created_at + ", updated_at=" + updated_at
				+ ", created_by=" + created_by + ", updated_by=" + updated_by + "]";
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
}
