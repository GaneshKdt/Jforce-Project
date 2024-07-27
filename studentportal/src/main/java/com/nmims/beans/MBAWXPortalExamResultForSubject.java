package com.nmims.beans;

import java.io.Serializable;

public class MBAWXPortalExamResultForSubject implements Serializable  {

	private String timeboundId;
	private String sapid;
	private String term;
	private String subject;
	// month/year
	private String examMonth;
	private String examYear;
	private String acadsMonth;
	private String acadsYear;
	// ia
	private String iaScore;
	private String iaScoreMax;
	// tee
	private String teeIsPass;
	private String teeScore;
	private String teeScoreMax;
	// grace
	private String graceMarks;
	// resit
	private String resitIsPass;
	private String resitScore;
	private String resitScoreMax;
	// total
	private String total;
	private String totalMax;
	// status
	private String isPass;
	// for sorting subjects
	private String startDate;
	// flags
	private String showResults;					// show results flag. ia is displayed using its own flag
	private String showResultsForIA;			// show IA results flag.
	
	private String examDate;
	
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
	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
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
	public String getAcadsMonth() {
		return acadsMonth;
	}
	public void setAcadsMonth(String acadsMonth) {
		this.acadsMonth = acadsMonth;
	}
	public String getAcadsYear() {
		return acadsYear;
	}
	public void setAcadsYear(String acadsYear) {
		this.acadsYear = acadsYear;
	}
	public String getIaScore() {
		return iaScore;
	}
	public void setIaScore(String iaScore) {
		this.iaScore = iaScore;
	}
	public String getIaScoreMax() {
		return iaScoreMax;
	}
	public void setIaScoreMax(String iaScoreMax) {
		this.iaScoreMax = iaScoreMax;
	}
	public String getTeeIsPass() {
		return teeIsPass;
	}
	public void setTeeIsPass(String teeIsPass) {
		this.teeIsPass = teeIsPass;
	}
	public String getTeeScore() {
		return teeScore;
	}
	public void setTeeScore(String teeScore) {
		this.teeScore = teeScore;
	}
	public String getTeeScoreMax() {
		return teeScoreMax;
	}
	public void setTeeScoreMax(String teeScoreMax) {
		this.teeScoreMax = teeScoreMax;
	}
	public String getGraceMarks() {
		return graceMarks;
	}
	public void setGraceMarks(String graceMarks) {
		this.graceMarks = graceMarks;
	}
	public String getResitIsPass() {
		return resitIsPass;
	}
	public void setResitIsPass(String resitIsPass) {
		this.resitIsPass = resitIsPass;
	}
	public String getResitScore() {
		return resitScore;
	}
	public void setResitScore(String resitScore) {
		this.resitScore = resitScore;
	}
	public String getResitScoreMax() {
		return resitScoreMax;
	}
	public void setResitScoreMax(String resitScoreMax) {
		this.resitScoreMax = resitScoreMax;
	}
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	public String getTotalMax() {
		return totalMax;
	}
	public void setTotalMax(String totalMax) {
		this.totalMax = totalMax;
	}
	public String getIsPass() {
		return isPass;
	}
	public void setIsPass(String isPass) {
		this.isPass = isPass;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getShowResults() {
		return showResults;
	}
	public void setShowResults(String showResults) {
		this.showResults = showResults;
	}
	public String getShowResultsForIA() {
		return showResultsForIA;
	}
	public void setShowResultsForIA(String showResultsForIA) {
		this.showResultsForIA = showResultsForIA;
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
		return "MBAWXExamResultForSubject [timeboundId=" + timeboundId + ", sapid=" + sapid + ", term=" + term
				+ ", subject=" + subject + ", examMonth=" + examMonth + ", examYear=" + examYear + ", acadsMonth="
				+ acadsMonth + ", acadsYear=" + acadsYear + ", iaScore=" + iaScore + ", iaScoreMax=" + iaScoreMax
				+ ", teeIsPass=" + teeIsPass + ", teeScore=" + teeScore + ", teeScoreMax=" + teeScoreMax
				+ ", graceMarks=" + graceMarks + ", resitIsPass=" + resitIsPass + ", resitScore=" + resitScore
				+ ", resitScoreMax=" + resitScoreMax + ", total=" + total + ", totalMax=" + totalMax + ", isPass="
				+ isPass + ", startDate=" + startDate + ", showResults=" + showResults + ", showResultsForIA="
				+ showResultsForIA + ", examDate=" + examDate + ", simulation_score=" + simulation_score
				+ ", simulation_max_score=" + simulation_max_score + ", compXM_score=" + compXM_score
				+ ", compXM_max_score=" + compXM_max_score + ", prgm_sem_subj_id=" + prgm_sem_subj_id + "]";
	}
	public String getExamDate() {
		return examDate;
	}
	public void setExamDate(String examDate) {
		this.examDate = examDate;
	}
	
}
