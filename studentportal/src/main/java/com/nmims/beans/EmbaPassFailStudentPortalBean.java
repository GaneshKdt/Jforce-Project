package com.nmims.beans;

import java.io.Serializable;
/**
 * old name - EmbaPassFailBean
 * @author
 *
 */
public class EmbaPassFailStudentPortalBean extends BaseStudentPortalBean implements Serializable{
	private String  timeboundId;
	private String  sapid;
	private String  studentName;
	private String  attempt;
	private String  sem;
	private String  iaScore;
	private int  teeScore;
	private String  total;
	private String  graceMarks;
	private String  isPass;
	private String  failReason;
	private String  isResultLive;
	private String schedule_id;
	private String batch_id;
	private String subject;
	private String max_score;
	private String grade;
	private String points;
	private String program;	
	private String status;
	private String processed;

	public String getProgram() {
		return program;
	}
	public void setProgram(String program) {
		this.program = program;
	}
	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}
	public String getPoints() {
		return points;
	}
	public void setPoints(String points) {
		this.points = points;
	}

	public String getBatch_id() {
		return batch_id;
	}
	public void setBatch_id(String batch_id) {
		this.batch_id = batch_id;
	}
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
	public int getTeeScore() {
		return teeScore;
	}
	public void setTeeScore(int teeScore) {
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
	@Override
	public String toString() {
		return "EmbaPassFailStudentPortalBean [timeboundId=" + timeboundId + ", sapid=" + sapid + ", studentName=" + studentName
				+ ", attempt=" + attempt + ", sem=" + sem + ", iaScore=" + iaScore + ", teeScore=" + teeScore
				+ ", total=" + total + ", graceMarks=" + graceMarks + ", isPass=" + isPass + ", failReason="
				+ failReason + ", isResultLive=" + isResultLive + "]";
	}
	public String getSchedule_id() {
		return schedule_id;
	}
	public void setSchedule_id(String schedule_id) {
		this.schedule_id = schedule_id;
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

	
}
