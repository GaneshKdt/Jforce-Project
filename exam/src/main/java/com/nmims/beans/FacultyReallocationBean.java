package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

public class FacultyReallocationBean implements Serializable
{
	/*Variables*/
	private String facultyId;
	private String toFacultyId;
	private String firstName;
	private String lastName;
	private String facultyName;
	private String year;
	private String month;
	private String evaluated;
	private String yetEvaluated;
	private String projectsAllocated;
	private int countReallocate;
	private List<String> sapids;
	private String sapid;
	private String consumerProgramStructureId;
	private String programStructure;
	private String programCode;
	private String programName;
	private String user;
	
	
	/*Getters And Setters*/
	public String getFacultyId() {
		return facultyId;
	}
	public void setFacultyId(String facultyId) {
		this.facultyId = facultyId;
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
	public String getEvaluated() {
		return evaluated;
	}
	public void setEvaluated(String evaluated) {
		this.evaluated = evaluated;
	}
	public String getYetEvaluated() {
		return yetEvaluated;
	}
	public void setYetEvaluated(String yetEvaluated) {
		this.yetEvaluated = yetEvaluated;
	}
	public String getProjectsAllocated() {
		return projectsAllocated;
	}
	public void setProjectsAllocated(String projectsAllocated) {
		this.projectsAllocated = projectsAllocated;
	}
	public String getToFacultyId() {
		return toFacultyId;
	}
	public void setToFacultyId(String toFacultyId) {
		this.toFacultyId = toFacultyId;
	}
	public int getCountReallocate() {
		return countReallocate;
	}
	public void setCountReallocate(int countReallocate) {
		this.countReallocate = countReallocate;
	}
	public String getFacultyName() {
		return facultyName;
	}
	public void setFacultyName(String facultyName) {
		this.facultyName = facultyName;
	}
	public List<String> getSapids() {
		return sapids;
	}
	public void setSapids(List<String> sapids) {
		this.sapids = sapids;
	}
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public String getConsumerProgramStructureId() {
		return consumerProgramStructureId;
	}
	public void setConsumerProgramStructureId(String consumerProgramStructureId) {
		this.consumerProgramStructureId = consumerProgramStructureId;
	}
	public String getProgramStructure() {
		return programStructure;
	}
	public void setProgramStructure(String programStructure) {
		this.programStructure = programStructure;
	}
	public String getProgramCode() {
		return programCode;
	}
	public void setProgramCode(String programCode) {
		this.programCode = programCode;
	}
	public String getProgramName() {
		return programName;
	}
	public void setProgramName(String programName) {
		this.programName = programName;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	
	/*To String Method*/
	@Override
	public String toString() {
		return "FacultyReallocationBean [facultyId=" + facultyId + ", firstName=" + firstName + ", lastName=" + lastName
				+ ", year=" + year + ", month=" + month + "]";
	}
	
}
