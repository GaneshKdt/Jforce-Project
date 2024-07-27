package com.nmims.timeline.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@IdClass(StudentCompositeKey.class)
@Table(name="exam.students", schema="exam")
public class Student {

	@Id
	@Column(name="sapid")
	private String sapid;

	@Id
	private String sem;
	private String program;
	private String PrgmStructApplicable;
	private String consumerType;
    private String consumerProgramStructureId;
    
    private String enrollmentMonth; 
    private String enrollmentYear;
    private String firstName;
    private String lastName;
    private String mobile;
	private String imageUrl;
	private String ugQualification;
	private String highestQualification;
	private String industry;
	private String city;
	private String emailId;
	
	@Transient
	private Long groupid;
	@Transient
	private String year;
	@Transient
	private String month;
	
	@Transient
	private String fromAdmin;

	
	
	public String getFromAdmin() {
		return fromAdmin;
	}
	public void setFromAdmin(String fromAdmin) {
		this.fromAdmin = fromAdmin;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getUgQualification() {
		return ugQualification;
	}
	public String getHighestQualification() {
		return highestQualification;
	}
	public String getIndustry() {
		return industry;
	}
	public void setUgQualification(String ugQualification) {
		this.ugQualification = ugQualification;
	}
	public void setHighestQualification(String highestQualification) {
		this.highestQualification = highestQualification;
	}
	public void setIndustry(String industry) {
		this.industry = industry;
	}
	
	
	
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getFirstName() {
		return firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public String getMobile() {
		return mobile;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getEnrollmentMonth() {
		return enrollmentMonth;
	}
	public String getEnrollmentYear() {
		return enrollmentYear;
	}

	public void setEnrollmentMonth(String enrollmentMonth) {
		this.enrollmentMonth = enrollmentMonth;
	}
	public void setEnrollmentYear(String enrollmentYear) {
		this.enrollmentYear = enrollmentYear;
	}

	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public String getSem() {
		return sem;
	}
	public void setSem(String sem) {
		this.sem = sem;
	}
	public String getProgram() {
		return program;
	}
	public void setProgram(String program) {
		this.program = program;
	}
	
	public String getPrgmStructApplicable() {
		return PrgmStructApplicable;
	}
	public void setPrgmStructApplicable(String prgmStructApplicable) {
		PrgmStructApplicable = prgmStructApplicable;
	}
	public String getConsumerType() {
		return consumerType;
	}
	public void setConsumerType(String consumerType) {
		this.consumerType = consumerType;
	}
	public String getConsumerProgramStructureId() {
		return consumerProgramStructureId;
	}
	public void setConsumerProgramStructureId(String consumerProgramStructureId) {
		this.consumerProgramStructureId = consumerProgramStructureId;
	}
	public Long getGroupid() {
		return groupid;
	}
	public void setGroupid(Long groupid) {
		this.groupid = groupid;
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
	
	
	@Override
	public String toString() {
		return "Student [sapid=" + sapid + ", sem=" + sem + ", program=" + program + ", PrgmStructApplicable="
				+ PrgmStructApplicable + ", consumerType=" + consumerType + ", consumerProgramStructureId="
				+ consumerProgramStructureId + ", enrollmentMonth=" + enrollmentMonth + ", enrollmentYear="
				+ enrollmentYear + ", firstName=" + firstName + ", lastName=" + lastName + ", mobile=" + mobile
				+ ", imageUrl=" + imageUrl + ", ugQualification=" + ugQualification + ", highestQualification="
				+ highestQualification + ", industry=" + industry + ", emailId=" + emailId + ", groupid=" + groupid
				+ ", year=" + year + ", month=" + month + " ]";
	}
	
}
