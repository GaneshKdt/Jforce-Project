package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

public class MBAXPassFailBean extends BaseExamBean implements Serializable{
	private Integer  timeboundId;
	private String  sapid;
	private String  studentName;
	private Integer  attempt;
	private Integer  sem;
	private Integer  iaScore;
	private Integer  teeScore;
	private String  total;
	private Integer  graceMarks;	
	private String  isPass;
	private String  failReason;
	private String  isResultLive;
	private String schedule_id;
	private String batch_id;
	private String subject;

	private String max_score;
	private String grade;
	private Double points;
	private String program;	
	private String status;
	private String processed;
	private List<EmbaPassFailBean> results ; 

	private Integer courseId;
	private Integer sessionPlanId;


	
	

	public Integer getCourseId() {
		return courseId;
	}
	public void setCourseId(Integer courseId) {
		this.courseId = courseId;
	}
	public Double getPoints() {
		return points;
	}
	public void setPoints(Double points) {
		this.points = points;
	}
	public Integer getSessionPlanId() {
		return sessionPlanId;
	}
	public void setSessionPlanId(Integer sessionPlanId) {
		this.sessionPlanId = sessionPlanId;
	}
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
	public String getBatch_id() {
		return batch_id;
	}
	public void setBatch_id(String batch_id) {
		this.batch_id = batch_id;
	}


	public Integer getTimeboundId() {
		return timeboundId;
	}
	public void setTimeboundId(Integer timeboundId) {
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
	
	public Integer getAttempt() {
		return attempt;
	}
	public void setAttempt(Integer attempt) {
		this.attempt = attempt;
	}


	public Integer getSem() {
		return sem;
	}
	public void setSem(Integer sem) {
		this.sem = sem;
	}
	
	
	public Integer getIaScore() {
		return iaScore;
	}
	public void setIaScore(Integer iaScore) {
		this.iaScore = iaScore;
	}
	public Integer getTeeScore() {
		return teeScore;
	}
	public void setTeeScore(Integer teeScore) {
		this.teeScore = teeScore;
	}
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	
	
	public Integer getGraceMarks() {
		return graceMarks;
	}
	public void setGraceMarks(Integer graceMarks) {
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

	public List<EmbaPassFailBean> getResults() {
		return results;
	}
	public void setResults(List<EmbaPassFailBean> results) {
		this.results = results;
	}
	@Override
	public String toString() {
		return "MBAXPassFailBean [timeboundId=" + timeboundId + ", sapid=" + sapid + ", studentName=" + studentName
				+ ", attempt=" + attempt + ", sem=" + sem + ", iaScore=" + iaScore + ", teeScore=" + teeScore
				+ ", total=" + total + ", graceMarks=" + graceMarks + ", isPass=" + isPass + ", failReason="
				+ failReason + ", isResultLive=" + isResultLive + ", schedule_id=" + schedule_id + ", batch_id="
				+ batch_id + ", subject=" + subject + ", max_score=" + max_score + ", grade=" + grade + ", points="
				+ points + ", program=" + program + ", status=" + status + ", processed=" + processed + ", results="
				+ results + ", courseId=" + courseId + ", sessionPlanId=" + sessionPlanId + "]";
	}



	
}
