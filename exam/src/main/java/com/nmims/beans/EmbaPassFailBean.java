package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

public class EmbaPassFailBean extends BaseExamBean implements Serializable{
	private String  timeboundId;
	private String  sapid;
	private String  studentName;
	private String  attempt;
	private String  sem;
	private String  iaScore;
	private Integer  teeScore;
	private String  total;
	private String  graceMarks;
	private String  isPass;
	private String  failReason;
	private String  isResultLive;
	private String schedule_id;
	private String batch_id;
	private String batchName;
	private String subject;

	private String max_score;
	private String grade;
	private String points;
	private String program;	
	private String status;
	private String processed;
	private String  psssem;
	private String logoRequired;
	private String lc;

	private Integer pssId;
	private Integer consumerProgramStructureId;
	private String assessmentName;
	/**Added by Siddheshwar_Khanse for Capstone Project Marks Upload */
	private Float simulation_score;
	private Integer simulation_max_score;
	private Float compXM_score;
	private Integer compXM_max_score;
	private Float simulation_previous_score;
	private Float compXM_previous_score;
	private int attemptedIA;
	private String simulation_status;
	private String compXM_status;
	  
	
	public String getAssessmentName() {
		return assessmentName;
	}
	public void setAssessmentName(String assessmentName) {
		this.assessmentName = assessmentName;
	}
	public String getLc() {
		return lc;
	}
	public void setLc(String lc) {
		this.lc = lc;
	}
	public String getLogoRequired() {
		return logoRequired;
	}
	public void setLogoRequired(String logoRequired) {
		this.logoRequired = logoRequired;
	}
	public String getPsssem() {
		return psssem;
	}
	public void setPsssem(String psssem) {
		this.psssem = psssem;
	}

	private int project;
	private List<EmbaPassFailBean> results ; 
	private int prgm_sem_subj_id;
	private double credits = 4;
	private float gpa;
	public float getGpa() {
		return gpa;
	}
	public void setGpa(float gpa) {
		this.gpa = gpa;
	}
	public float getCgpa() {
		return cgpa;
	}
	public void setCgpa(float cgpa) {
		this.cgpa = cgpa;
	}

	private float cgpa;



	
	public double getCredits() {
		return credits;
	}
	public void setCredits(double credits) {
		this.credits = credits;
	}

	//	following added for marksheet sr admin side
	private String serviceRequestIdList;

	public String getServiceRequestIdList() {
		return serviceRequestIdList;
	}
	public void setServiceRequestIdList(String serviceRequestIdList) {
		this.serviceRequestIdList = serviceRequestIdList;
	}

// following added for marksheet sr, to get cleared sems of a student
	private String examYear;
	private String examMonth;
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

	private String year;
	private String month;
	private String id;

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getExamYear() {
		return examYear;
	}
	public void setExamYear(String examYear) {
		this.examYear = examYear;
	}
	public String getExamMonth() {
		return examMonth;
	}
	public void setExamMonth(String examMonth) {
		this.examMonth = examMonth;
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
	public int getPrgm_sem_subj_id() {
		return prgm_sem_subj_id;
	}
	public void setPrgm_sem_subj_id(int prgm_sem_subj_id) {
		this.prgm_sem_subj_id = prgm_sem_subj_id;

	}
	public int getProject() {
		return project;
	}
	public void setProject(int project) {
		this.project = project;
	}
	public String getBatchName() {
		return batchName;
	}
	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}
	public Integer getPssId() {
		return pssId;
	}
	public void setPssId(Integer pssId) {
		this.pssId = pssId;
	}
	public Integer getConsumerProgramStructureId() {
		return consumerProgramStructureId;
	}
	public void setConsumerProgramStructureId(Integer consumerProgramStructureId) {
		this.consumerProgramStructureId = consumerProgramStructureId;
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
	public int getAttemptedIA() {
		return attemptedIA;
	}
	public void setAttemptedIA(int attemptedIA) {
		this.attemptedIA = attemptedIA;
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
		return "EmbaPassFailBean [timeboundId=" + timeboundId + ", sapid=" + sapid + ", studentName=" + studentName
				+ ", attempt=" + attempt + ", sem=" + sem + ", iaScore=" + iaScore + ", teeScore=" + teeScore
				+ ", total=" + total + ", graceMarks=" + graceMarks + ", isPass=" + isPass + ", failReason="
				+ failReason + ", isResultLive=" + isResultLive + ", schedule_id=" + schedule_id + ", batch_id="
				+ batch_id + ", batchName=" + batchName + ", subject=" + subject + ", max_score=" + max_score
				+ ", grade=" + grade + ", points=" + points + ", program=" + program + ", status=" + status
				+ ", processed=" + processed + ", psssem=" + psssem + ", logoRequired=" + logoRequired + ", lc=" + lc
				+ ", pssId=" + pssId + ", consumerProgramStructureId=" + consumerProgramStructureId
				+ ", simulation_score=" + simulation_score + ", simulation_max_score=" + simulation_max_score
				+ ", compXM_score=" + compXM_score + ", compXM_max_score=" + compXM_max_score
				+ ", simulation_previous_score=" + simulation_previous_score + ", compXM_previous_score="
				+ compXM_previous_score + ", attemptedIA=" + attemptedIA + ", simulation_status=" + simulation_status
				+ ", compXM_status=" + compXM_status + ", project=" + project + ", results=" + results
				+ ", prgm_sem_subj_id=" + prgm_sem_subj_id + ", credits=" + credits + ", gpa=" + gpa + ", cgpa=" + cgpa
				+ ", serviceRequestIdList=" + serviceRequestIdList + ", examYear=" + examYear + ", examMonth="
				+ examMonth + ", year=" + year + ", month=" + month + ", id=" + id + "]";
	}
}
