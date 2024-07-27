package com.nmims.beans;

import java.io.Serializable;

public class DissertationResultBean implements Serializable {

	

	private static final long serialVersionUID = 1L;

	private Long sapid;
	private int timeBoundId;
	private int prgm_sem_subj_id;
	private double component_a_score;
	private int component_a_max_score;
	private double component_b_score;
	private int component_b_max_score;
	private double component_c_score;
	private int component_c_max_score;
	private String processed;
	private String createdBy;
	private String lastModifiedBy;
	private String component_a_status;
	private String component_b_status;
	private String component_c_status;
	private String failReason;
	private String isResultLive;
	private String grade;
	private String isPass;
	private String consumerProgramStrcutureId;
	private int graceMarks;
	private int total;
	private float gradePoints;


	
	

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
	public String getProcessed() {
		return processed;
	}
	public void setProcessed(String processed) {
		this.processed = processed;
	}
	public String getFailReason() {
		return failReason;
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
	
	
	public String getComponent_c_status() {
		return component_c_status;
	}
	public void setComponent_c_status(String component_c_status) {
		this.component_c_status = component_c_status;
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
	@Override
	public String toString() {
		return "DissertationResultBean [sapid=" + sapid + ", timeBoundId=" + timeBoundId + ", prgm_sem_subj_id="
				+ prgm_sem_subj_id + ", component_a_score=" + component_a_score + ", component_a_max_score="
				+ component_a_max_score + ", component_b_score=" + component_b_score + ", component_b_max_score="
				+ component_b_max_score + ", component_c_score=" + component_c_score + ", component_c_max_score="
				+ component_c_max_score + ", processed=" + processed + ", createdBy=" + createdBy + ", lastModifiedBy="
				+ lastModifiedBy + ", component_a_status=" + component_a_status + ", component_b_status="
				+ component_b_status + ", component_c_status=" + component_c_status + ", failReason=" + failReason
				+ ", isResultLive=" + isResultLive + ", grade=" + grade + ", isPass=" + isPass
				+ ", consumerProgramStrcutureId=" + consumerProgramStrcutureId + "]";
	}

	
	
	

	
}
