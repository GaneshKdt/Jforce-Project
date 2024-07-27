package com.nmims.beans;

import java.io.Serializable;

public class DissertationResultDTO implements Serializable {

	

	private static final long serialVersionUID = 1L;

	private Long sapid;
	private int timeBoundId;
	private int prgm_sem_subj_id;
	private double component_a_score=0;
	private int component_a_max_score;
	private double component_b_score=0;
	private int component_b_max_score;
	private double component_c_score=0;
	private int component_c_max_score;
	private String processed="";
	private String createdBy="";
	private String lastModifiedBy="";
	private String component_a_status="";
	private String component_b_status="";
	private String component_c_status="";
	private String failReason="";
	private String isResultLive="";
	private String grade="";
	private String isPass="";
	private String consumerProgramStrcutureId="";
	private int graceMarks;
	private int total;
	private float gradePoints;
	private String firstName="";
	private String lastName="";
	private String program="";
	private String centerCode="";
	private String centerName="";
	private String batchName="";
	private String batchId="";
	private String sem="";
	private String subject="";
	public Long getSapid() {
		return sapid;
	}
	public void setSapid(Long sapid) {
		this.sapid = sapid;
	}
	public int getTimeBoundId() {
		return timeBoundId;
	}
	public void setTimeBoundId(int timeBoundId) {
		this.timeBoundId = timeBoundId;
	}
	public int getPrgm_sem_subj_id() {
		return prgm_sem_subj_id;
	}
	public void setPrgm_sem_subj_id(int prgm_sem_subj_id) {
		this.prgm_sem_subj_id = prgm_sem_subj_id;
	}
	public double getComponent_a_score() {
		return component_a_score;
	}
	public void setComponent_a_score(double component_a_score) {
		this.component_a_score = component_a_score;
	}
	public int getComponent_a_max_score() {
		return component_a_max_score;
	}
	public void setComponent_a_max_score(int component_a_max_score) {
		this.component_a_max_score = component_a_max_score;
	}
	public double getComponent_b_score() {
		return component_b_score;
	}
	public void setComponent_b_score(double component_b_score) {
		this.component_b_score = component_b_score;
	}
	public int getComponent_b_max_score() {
		return component_b_max_score;
	}
	public void setComponent_b_max_score(int component_b_max_score) {
		this.component_b_max_score = component_b_max_score;
	}
	public double getComponent_c_score() {
		return component_c_score;
	}
	public void setComponent_c_score(double component_c_score) {
		this.component_c_score = component_c_score;
	}
	public int getComponent_c_max_score() {
		return component_c_max_score;
	}
	public void setComponent_c_max_score(int component_c_max_score) {
		this.component_c_max_score = component_c_max_score;
	}
	public String getProcessed() {
		return processed;
	}
	public void setProcessed(String processed) {
		this.processed = processed;
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
	public String getComponent_a_status() {
		return component_a_status;
	}
	public void setComponent_a_status(String component_a_status) {
		this.component_a_status = component_a_status;
	}
	public String getComponent_b_status() {
		return component_b_status;
	}
	public void setComponent_b_status(String component_b_status) {
		this.component_b_status = component_b_status;
	}
	public String getComponent_c_status() {
		return component_c_status;
	}
	public void setComponent_c_status(String component_c_status) {
		this.component_c_status = component_c_status;
	}
	public String getFailReason() {
		return failReason;
	}
	public String getBatchName() {
		return batchName;
	}
	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}
	public String getBatchId() {
		return batchId;
	}
	public void setBatchId(String batchId) {
		this.batchId = batchId;
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
	public void setFailReason(String failReason) {
		this.failReason = failReason;
	}
	public String getIsResultLive() {
		return isResultLive;
	}
	public void setIsResultLive(String isResultLive) {
		this.isResultLive = isResultLive;
	}
	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}
	public String getIsPass() {
		return isPass;
	}
	public void setIsPass(String isPass) {
		this.isPass = isPass;
	}
	public String getConsumerProgramStrcutureId() {
		return consumerProgramStrcutureId;
	}
	public void setConsumerProgramStrcutureId(String consumerProgramStrcutureId) {
		this.consumerProgramStrcutureId = consumerProgramStrcutureId;
	}
	public int getGraceMarks() {
		return graceMarks;
	}
	public void setGraceMarks(int graceMarks) {
		this.graceMarks = graceMarks;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public float getGradePoints() {
		return gradePoints;
	}
	public void setGradePoints(float gradePoints) {
		this.gradePoints = gradePoints;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getProgram() {
		return program;
	}
	public void setProgram(String program) {
		this.program = program;
	}
	public String getCenterCode() {
		return centerCode;
	}
	public void setCenterCode(String centerCode) {
		this.centerCode = centerCode;
	}
	public String getCenterName() {
		return centerName;
	}
	public void setCenterName(String centerName) {
		this.centerName = centerName;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	@Override
	public String toString() {
		return "DissertationResultDTO [sapid=" + sapid + ", timeBoundId=" + timeBoundId + ", prgm_sem_subj_id="
				+ prgm_sem_subj_id + ", component_a_score=" + component_a_score + ", component_a_max_score="
				+ component_a_max_score + ", component_b_score=" + component_b_score + ", component_b_max_score="
				+ component_b_max_score + ", component_c_score=" + component_c_score + ", component_c_max_score="
				+ component_c_max_score + ", processed=" + processed + ", createdBy=" + createdBy + ", lastModifiedBy="
				+ lastModifiedBy + ", component_a_status=" + component_a_status + ", component_b_status="
				+ component_b_status + ", component_c_status=" + component_c_status + ", failReason=" + failReason
				+ ", isResultLive=" + isResultLive + ", grade=" + grade + ", isPass=" + isPass
				+ ", consumerProgramStrcutureId=" + consumerProgramStrcutureId + ", graceMarks=" + graceMarks
				+ ", total=" + total + ", gradePoints=" + gradePoints + ", firstName=" + firstName + ", lastName="
				+ lastName + ", program=" + program + ", centerCode=" + centerCode + ", centerName=" + centerName
				+ ", batchName=" + batchName + ", batchId=" + batchId + ", sem=" + sem + ", subject=" + subject + "]";
	}

	
	
}
