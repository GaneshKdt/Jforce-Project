package com.nmims.beans;

import java.io.Serializable;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

@SuppressWarnings("serial")
public class TEEResultBean implements Serializable{
	private CommonsMultipartFile fileData;
	private String timebound_id;
	private String schedule_id;
	private String schedule_accessKey;
	private String student_name;
	private String sapid;
	private int score;
	private String current_acad_year;
	private String current_acad_month;
	private String batch;
	private String createdBy;
	private String lastModifiedBy;
	private String max_score;
	private String batchId;
	private String subject;
	private String assessments_id;
	private String status;
	private String schedule_name;
	private String processed;
	private int prgm_sem_subj_id;
	private String sem;
	private String max_marks;
	private String examMonth;
	private String examYear;
	private String programType;
	
	private Integer passScore;
	
	/**Added by Siddheshwar_Khanse for Capstone Project Marks Upload */
	private Float simulation_score;
	private Integer simulation_max_score;
	private Float compXM_score;
	private Integer compXM_max_score;
	private Float simulation_previous_score;
	private Float compXM_previous_score;
	private String simulation_status;
	private String compXM_status;
	

	private Integer consumerProgramStructureId;
	
	private String assessmentName;
	
	public String getAssessmentName() {
		return assessmentName;
	}
	public void setAssessmentName(String assessmentName) {
		this.assessmentName = assessmentName;
	}
	public Integer getConsumerProgramStructureId() {
		return consumerProgramStructureId;
	}
	public void setConsumerProgramStructureId(Integer consumerProgramStructureId) {
		this.consumerProgramStructureId = consumerProgramStructureId;
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
	public String getStudent_name() {
		return student_name;
	}
	public void setStudent_name(String student_name) {
		this.student_name = student_name;
	}
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public String getBatchId() {
		return batchId;
	}
	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getMax_score() {
		return max_score;
	}
	public void setMax_score(String max_score) {
		this.max_score = max_score;
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
	public String getBatch() {
		return batch;
	}
	public void setBatch(String batch) {
		this.batch = batch;
	}
	public String getCurrent_acad_year() {
		return current_acad_year;
	}
	public void setCurrent_acad_year(String current_acad_year) {
		this.current_acad_year = current_acad_year;
	}
	public String getCurrent_acad_month() {
		return current_acad_month;
	}
	public void setCurrent_acad_month(String current_acad_month) {
		this.current_acad_month = current_acad_month;
	}
	
	public String getSchedule_id() {
		return schedule_id;
	}
	public void setSchedule_id(String schedule_id) {
		this.schedule_id = schedule_id;
	}
	public String getSchedule_accessKey() {
		return schedule_accessKey;
	}
	public void setSchedule_accessKey(String schedule_accessKey) {
		this.schedule_accessKey = schedule_accessKey;
	}
	public String getStudentname() {
		return student_name;
	}
	public void setStudentname(String studentname) {
		this.student_name = studentname;
	}
	public String getSapId() {
		return sapid;
	}
	public void setSapId(String sapid) {
		this.sapid = sapid;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public CommonsMultipartFile getFileData() {
		return fileData;
	}
	public void setFileData(CommonsMultipartFile fileData) {
		this.fileData = fileData;
	}
	public String getTimebound_id() {
		return timebound_id;
	}
	public void setTimebound_id(String timebound_id) {
		this.timebound_id = timebound_id;
	}
	public String getAssessments_id() {
		return assessments_id;
	}
	public void setAssessments_id(String assessments_id) {
		this.assessments_id = assessments_id;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getSchedule_name() {
		return schedule_name;
	}
	public void setSchedule_name(String schedule_name) {
		this.schedule_name = schedule_name;
	}
	public String getProcessed() {
		return processed;
	}
	public void setProcessed(String processed) {
		this.processed = processed;
	}
	public int getPrgm_sem_subj_id() {
		return prgm_sem_subj_id;
	}
	public void setPrgm_sem_subj_id(int prgm_sem_subj_id) {
		this.prgm_sem_subj_id = prgm_sem_subj_id;
	}
	public String getSem() {
		return sem;
	}
	public void setSem(String sem) {
		this.sem = sem;
	}
	public String getMax_marks() {
		return max_marks;
	}
	public void setMax_marks(String max_marks) {
		this.max_marks = max_marks;
	}
	public Integer getPassScore() {
		return passScore;
	}
	public void setPassScore(Integer passScore) {
		this.passScore = passScore;
	}
	public String getProgramType() {
		return programType;
	}
	public void setProgramType(String programType) {
		this.programType = programType;
	}
	public Float getSimulation_score() {
		return simulation_score;
	}
	public void setSimulation_score(Float simulation_score) {
		this.simulation_score = simulation_score;
	}
	public Integer getSimulation_max_score() {
		return simulation_max_score;
	}
	public void setSimulation_max_score(Integer simulation_max_score) {
		this.simulation_max_score = simulation_max_score;
	}
	public Float getCompXM_score() {
		return compXM_score;
	}
	public void setCompXM_score(Float compXM_score) {
		this.compXM_score = compXM_score;
	}
	public Integer getCompXM_max_score() {
		return compXM_max_score;
	}
	public void setCompXM_max_score(Integer compXM_max_score) {
		this.compXM_max_score = compXM_max_score;
	}
	public Float getSimulation_previous_score() {
		return simulation_previous_score;
	}
	public void setSimulation_previous_score(Float simulation_previous_score) {
		this.simulation_previous_score = simulation_previous_score;
	}
	public Float getCompXM_previous_score() {
		return compXM_previous_score;
	}
	public void setCompXM_previous_score(Float compXM_previous_score) {
		this.compXM_previous_score = compXM_previous_score;
	}
	public String getSimulation_status() {
		return simulation_status;
	}
	public void setSimulation_status(String simulation_status) {
		this.simulation_status = simulation_status;
	}
	public String getCompXM_status() {
		return compXM_status;
	}
	public void setCompXM_status(String compXM_status) {
		this.compXM_status = compXM_status;
	}
	@Override
	public String toString() {
		return "TEEResultBean [fileData=" + fileData + ", timebound_id=" + timebound_id + ", schedule_id=" + schedule_id
				+ ", schedule_accessKey=" + schedule_accessKey + ", student_name=" + student_name + ", sapid=" + sapid
				+ ", score=" + score + ", current_acad_year=" + current_acad_year + ", current_acad_month="
				+ current_acad_month + ", batch=" + batch + ", createdBy=" + createdBy + ", lastModifiedBy="
				+ lastModifiedBy + ", max_score=" + max_score + ", batchId=" + batchId + ", subject=" + subject
				+ ", assessments_id=" + assessments_id + ", status=" + status + ", schedule_name=" + schedule_name
				+ ", processed=" + processed + ", prgm_sem_subj_id=" + prgm_sem_subj_id + ", sem=" + sem
				+ ", max_marks=" + max_marks + ", examMonth=" + examMonth + ", examYear=" + examYear + ", programType="
				+ programType + ", passScore=" + passScore + ", simulation_score=" + simulation_score
				+ ", simulation_max_score=" + simulation_max_score + ", compXM_score=" + compXM_score
				+ ", compXM_max_score=" + compXM_max_score + ", simulation_previous_score=" + simulation_previous_score
				+ ", compXM_previous_score=" + compXM_previous_score + ", simulation_status=" + simulation_status
				+ ", compXM_status=" + compXM_status + ", consumerProgramStructureId=" + consumerProgramStructureId
				+ ", assessmentName=" + assessmentName + "]";
	}
	
	
	
}
