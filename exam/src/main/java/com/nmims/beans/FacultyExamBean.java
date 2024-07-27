package com.nmims.beans;

import java.io.Serializable;

//spring security related changes rename FacultyBean to FacultyExamBean
public class FacultyExamBean   implements Serializable  {
	private String id;
	private String facultyId;
	private String firstName;
	private String lastName;
	private String email;
	private String active;
	private String mobile;
	private String createdBy;
	private String createdDate;
	private String lastModifiedBy;
	private String lastModifiedDate;
	private String assignmentsAllocated;
	private int available;
	private String facultyType;
	private String title;
	
	private String batchYear;
	private String batchMonth;
	
	private String facultyDetails;
	
	public String getBatchYear() {
		return batchYear;
	}
	public void setBatchYear(String batchYear) {
		this.batchYear = batchYear;
	}
	public String getBatchMonth() {
		return batchMonth;
	}
	public void setBatchMonth(String batchMonth) {
		this.batchMonth = batchMonth;
	}
	public String getFacultyType() {
		return facultyType;
	}
	public void setFacultyType(String facultyType) {
		this.facultyType = facultyType;
	}
	
	
	public int getAvailable() {
		return available;
	}
	public void setAvailable(int available) {
		this.available = available;
	}
	public String getAssignmentsAllocated() {
		return assignmentsAllocated;
	}
	public void setAssignmentsAllocated(String assignmentsAllocated) {
		this.assignmentsAllocated = assignmentsAllocated;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public String getLastModifiedBy() {
		return lastModifiedBy;
	}
	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}
	public String getLastModifiedDate() {
		return lastModifiedDate;
	}
	public void setLastModifiedDate(String lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
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
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getActive() {
		return active;
	}
	public void setActive(String active) {
		this.active = active;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getFacultyDetails() {
		return facultyDetails;
	}
	public void setFacultyDetails(String facultyDetails) {
		this.title = facultyDetails;
	}
	
	@Override
	public String toString() {
		return "FacultyBean [facultyId=" + facultyId
				+ ", assignmentsAllocated=" + assignmentsAllocated + "]\n";
	}

	
	
}
