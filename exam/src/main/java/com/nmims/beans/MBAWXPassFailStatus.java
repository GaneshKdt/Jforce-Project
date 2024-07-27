package com.nmims.beans;

import java.io.Serializable;

public class MBAWXPassFailStatus extends BaseExamBean  implements Serializable  {

	private String timeboundId;
	private String sapid;
	private String studentName;
	private String attempt;
	private String sem;
	private String iaScore;
	private String teeScore;
	private String total;
	private String graceMarks;
	private String isPass;
	private String failReason;
	private String isResultLive;
	private String schedule_id;
	private String batch_id;
	private String subject;
	private String max_score;
	private String status;
	private String processed;
	private String examStartTime;
	
	/**Added by Siddheshwar_Khanse for Capstone Project Marks Upload */
	private Float simulation_score;
	private Integer simulation_max_score;
	private Float compXM_score;
	private Integer compXM_max_score;
	private String prgm_sem_subj_id;
	
	public String getTimeboundId() {
		return timeboundId;
	}
	public void setTimeboundId(String timeboundId) {
		this.timeboundId = timeboundId;
	}
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public String getStudentName() {
		return studentName;
	}
	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}
	public String getAttempt() {
		return attempt;
	}
	public void setAttempt(String attempt) {
		this.attempt = attempt;
	}
	public String getSem() {
		return sem;
	}
	public void setSem(String sem) {
		this.sem = sem;
	}
	public String getIaScore() {
		return iaScore;
	}
	public void setIaScore(String iaScore) {
		this.iaScore = iaScore;
	}
	public String getTeeScore() {
		return teeScore;
	}
	public void setTeeScore(String teeScore) {
		this.teeScore = teeScore;
	}
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	public String getGraceMarks() {
		return graceMarks;
	}
	public void setGraceMarks(String graceMarks) {
		this.graceMarks = graceMarks;
	}
	public String getIsPass() {
		return isPass;
	}
	public void setIsPass(String isPass) {
		this.isPass = isPass;
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
	public String getSchedule_id() {
		return schedule_id;
	}
	public void setSchedule_id(String schedule_id) {
		this.schedule_id = schedule_id;
	}
	public String getBatch_id() {
		return batch_id;
	}
	public void setBatch_id(String batch_id) {
		this.batch_id = batch_id;
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getProcessed() {
		return processed;
	}
	public void setProcessed(String processed) {
		this.processed = processed;
	}

	public String getExamStartTime() {
		return examStartTime;
	}
	public void setExamStartTime(String examStartTime) {
		this.examStartTime = examStartTime;
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
	public String getPrgm_sem_subj_id() {
		return prgm_sem_subj_id;
	}
	public void setPrgm_sem_subj_id(String prgm_sem_subj_id) {
		this.prgm_sem_subj_id = prgm_sem_subj_id;
	}
	@Override
	public String toString() {
		return "MBAWXPassFailStatus [timeboundId=" + timeboundId + ", sapid=" + sapid + ", studentName=" + studentName
				+ ", attempt=" + attempt + ", sem=" + sem + ", iaScore=" + iaScore + ", teeScore=" + teeScore
				+ ", total=" + total + ", graceMarks=" + graceMarks + ", isPass=" + isPass + ", failReason="
				+ failReason + ", isResultLive=" + isResultLive + ", schedule_id=" + schedule_id + ", batch_id="
				+ batch_id + ", subject=" + subject + ", max_score=" + max_score + ", status=" + status + ", processed="
				+ processed + ", examStartTime=" + examStartTime + ", simulation_score=" + simulation_score
				+ ", simulation_max_score=" + simulation_max_score + ", compXM_score=" + compXM_score
				+ ", compXM_max_score=" + compXM_max_score + ", prgm_sem_subj_id=" + prgm_sem_subj_id + "]";
	}
}
