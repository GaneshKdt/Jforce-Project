package com.nmims.beans;

import java.io.Serializable;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class MBAXMarksBean  implements Serializable  {
	private CommonsMultipartFile fileData;
	private int prgm_sem_subj_id;
	private String sapid;
	private int timebound_id;
	private String email;
	private String student_name;
	private int score;
	private int max_score;
	private String report_link;
	private String test_date;
	private String test_time;
	private String schedule_id;
	private String status;
	private String lastModifiedBy;
	private String created_at;
	private String updated_at;
	private String subject;
	
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public int getPrgm_sem_subj_id() {
		return prgm_sem_subj_id;
	}
	public void setPrgm_sem_subj_id(int prgm_sem_subj_id) {
		this.prgm_sem_subj_id = prgm_sem_subj_id;
	}
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public int getTimebound_id() {
		return timebound_id;
	}
	public void setTimebound_id(int timebound_id) {
		this.timebound_id = timebound_id;
	}
	public int getMax_score() {
		return max_score;
	}
	public void setMax_score(int max_score) {
		this.max_score = max_score;
	}
	public String getReport_link() {
		return report_link;
	}
	public void setReport_link(String report_link) {
		this.report_link = report_link;
	}
	public String getTest_date() {
		return test_date;
	}
	public void setTest_date(String test_date) {
		this.test_date = test_date;
	}
	public String getTest_time() {
		return test_time;
	}
	public void setTest_time(String test_time) {
		this.test_time = test_time;
	}
	public String getSchedule_id() {
		return schedule_id;
	}
	public void setSchedule_id(String schedule_id) {
		this.schedule_id = schedule_id;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public CommonsMultipartFile getFileData() {
		return fileData;
	}
	public void setFileData(CommonsMultipartFile fileData) {
		this.fileData = fileData;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	
	public String getStudent_name() {
		return student_name;
	}
	public void setStudent_name(String student_name) {
		this.student_name = student_name;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public String getLastModifiedBy() {
		return lastModifiedBy;
	}
	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
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
	
	
}
