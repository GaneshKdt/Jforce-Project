package com.nmims.beans;

import java.io.Serializable;

public class LeadModuleStatusBean implements Serializable {

	private String consumerProgramStructureId;
	private int program_sem_subject_id;
	private String leads_id;
	private int sem;
	

	private String subjectName;
	private int sessionId;

	private String registrationDate;
	private String accessTillDate;

	private String quizId;
	private int quizAttemptsTaken;
	private int quizAttemptsLeft;
	private long quizScore;
	private long quizPassScore;
	private long quizMaxScore;
	private boolean quizPassed;
	
	private String completionStatus;
	private boolean completed;
	private boolean unlocked;

	private String subjectDescription;
	
	private String certificateType;
	private boolean programComplete;
	private String programName;
	
	public String getConsumerProgramStructureId() {
		return consumerProgramStructureId;
	}
	public void setConsumerProgramStructureId(String consumerProgramStructureId) {
		this.consumerProgramStructureId = consumerProgramStructureId;
	}
	public int getProgram_sem_subject_id() {
		return program_sem_subject_id;
	}
	public void setProgram_sem_subject_id(int program_sem_subject_id) {
		this.program_sem_subject_id = program_sem_subject_id;
	}
	public String getLeads_id() {
		return leads_id;
	}
	public void setLeads_id(String leads_id) {
		this.leads_id = leads_id;
	}
	public int getSem() {
		return sem;
	}
	public void setSem(int sem) {
		this.sem = sem;
	}
	public String getSubjectName() {
		return subjectName;
	}
	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}
	public int getSessionId() {
		return sessionId;
	}
	public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}
	public String getRegistrationDate() {
		return registrationDate;
	}
	public void setRegistrationDate(String registrationDate) {
		this.registrationDate = registrationDate;
	}
	public String getAccessTillDate() {
		return accessTillDate;
	}
	public void setAccessTillDate(String accessTillDate) {
		this.accessTillDate = accessTillDate;
	}
	public String getQuizId() {
		return quizId;
	}
	public void setQuizId(String quizId) {
		this.quizId = quizId;
	}
	public int getQuizAttemptsTaken() {
		return quizAttemptsTaken;
	}
	public void setQuizAttemptsTaken(int quizAttemptsTaken) {
		this.quizAttemptsTaken = quizAttemptsTaken;
	}
	public int getQuizAttemptsLeft() {
		return quizAttemptsLeft;
	}
	public void setQuizAttemptsLeft(int quizAttemptsLeft) {
		this.quizAttemptsLeft = quizAttemptsLeft;
	}
	public long getQuizScore() {
		return quizScore;
	}
	public void setQuizScore(long quizScore) {
		this.quizScore = quizScore;
	}
	public long getQuizPassScore() {
		return quizPassScore;
	}
	public void setQuizPassScore(long quizPassScore) {
		this.quizPassScore = quizPassScore;
	}
	public long getQuizMaxScore() {
		return quizMaxScore;
	}
	public void setQuizMaxScore(long quizMaxScore) {
		this.quizMaxScore = quizMaxScore;
	}
	public boolean isQuizPassed() {
		return quizPassed;
	}
	public void setQuizPassed(boolean quizPassed) {
		this.quizPassed = quizPassed;
	}
	public String getCompletionStatus() {
		return completionStatus;
	}
	public void setCompletionStatus(String completionStatus) {
		this.completionStatus = completionStatus;
	}
	public boolean isCompleted() {
		return completed;
	}
	public void setCompleted(boolean completed) {
		this.completed = completed;
	}
	public boolean isUnlocked() {
		return unlocked;
	}
	public void setUnlocked(boolean unlocked) {
		this.unlocked = unlocked;
	}
	public String getSubjectDescription() {
		return subjectDescription;
	}
	public void setSubjectDescription(String subjectDescription) {
		this.subjectDescription = subjectDescription;
	}
	public String getCertificateType() {
		return certificateType;
	}
	public void setCertificateType(String certificateType) {
		this.certificateType = certificateType;
	}
	public boolean isProgramComplete() {
		return programComplete;
	}
	public void setProgramComplete(boolean programComplete) {
		this.programComplete = programComplete;
	}
	public String getProgramName() {
		return programName;
	}
	public void setProgramName(String programName) {
		this.programName = programName;
	}
	@Override
	public String toString() {
		return "LeadModuleStatusBean [consumerProgramStructureId=" + consumerProgramStructureId
				+ ", program_sem_subject_id=" + program_sem_subject_id + ", leads_id=" + leads_id + ", sem=" + sem
				+ ", subjectName=" + subjectName + ", sessionId=" + sessionId + ", registrationDate=" + registrationDate
				+ ", accessTillDate=" + accessTillDate + ", quizId=" + quizId + ", quizAttemptsTaken="
				+ quizAttemptsTaken + ", quizAttemptsLeft=" + quizAttemptsLeft + ", quizScore=" + quizScore
				+ ", quizPassScore=" + quizPassScore + ", quizMaxScore=" + quizMaxScore + ", quizPassed=" + quizPassed
				+ ", completionStatus=" + completionStatus + ", completed=" + completed + ", unlocked=" + unlocked
				+ ", subjectDescription=" + subjectDescription + ", certificateType=" + certificateType
				+ ", programComplete=" + programComplete + ", programName=" + programName + "]";
	}
}
