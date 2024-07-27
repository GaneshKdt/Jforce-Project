package com.nmims.beans;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

public class StudentCourseDetailsBean implements Serializable {

	//Bean is used to store the students course details to check if the package is applicable to them
	private String sapid;
	
	private String programType;
	private String programCleared;
	private int noOfSubjectsToClear;
	private int noOfSubjectsToClearLateral;
	private int noOfSubjectsToClearSem;
	private int currentSem;
	private boolean isLateral;
	private String consumerProgramStructureId;
	private Date lastRegistrationDate; 
	private int noOfSemesters;
	
	//check if the student is an alumni
	private boolean alumni;
	private int lastRegistration;
	
	//store results, semester-wise
	private Map<Integer, Integer> semResults;
	
	

	public int getNoOfSemesters() {
		return noOfSemesters;
	}
	public void setNoOfSemesters(int noOfSemesters) {
		this.noOfSemesters = noOfSemesters;
	}
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public Map<Integer, Integer> getSemResults() {
		return semResults;
	}
	public void setSemResults(Map<Integer, Integer> semResults) {
		this.semResults = semResults;
	}
	public String getProgramType() {
		return programType;
	}
	public void setProgramType(String programType) {
		this.programType = programType;
	}
	public String getProgramCleared() {
		return programCleared;
	}
	public void setProgramCleared(String programCleared) {
		this.programCleared = programCleared;
	}
	public int getNoOfSubjectsToClear() {
		return noOfSubjectsToClear;
	}
	public void setNoOfSubjectsToClear(int noOfSubjectsToClear) {
		this.noOfSubjectsToClear = noOfSubjectsToClear;
	}
	public int getNoOfSubjectsToClearLateral() {
		return noOfSubjectsToClearLateral;
	}
	public void setNoOfSubjectsToClearLateral(int noOfSubjectsToClearLateral) {
		this.noOfSubjectsToClearLateral = noOfSubjectsToClearLateral;
	}
	public int getNoOfSubjectsToClearSem() {
		return noOfSubjectsToClearSem;
	}
	public void setNoOfSubjectsToClearSem(int noOfSubjectsToClearSem) {
		this.noOfSubjectsToClearSem = noOfSubjectsToClearSem;
	}
	public int getCurrentSem() {
		return currentSem;
	}
	public void setCurrentSem(int currentSem) {
		this.currentSem = currentSem;
	}
	public boolean isLateral() {
		return isLateral;
	}
	public void setLateral(boolean isLateral) {
		this.isLateral = isLateral;
	}
	public String getConsumerProgramStructureId() {
		return consumerProgramStructureId;
	}
	public void setConsumerProgramStructureId(String consumerProgramStructureId) {
		this.consumerProgramStructureId = consumerProgramStructureId;
	}
	public boolean isAlumni() {
		return alumni;
	}
	public void setAlumni(boolean alumni) {
		this.alumni = alumni;
	}
	public int getLastRegistration() {
		return lastRegistration;
	}
	public void setLastRegistration(int lastRegistration) {
		this.lastRegistration = lastRegistration;
	}
	public Date getLastRegistrationDate() {
		return lastRegistrationDate;
	}
	public void setLastRegistrationDate(Date lastRegistrationDate) {
		this.lastRegistrationDate = lastRegistrationDate;
	}
}
