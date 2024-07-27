package com.nmims.beans;

import java.io.Serializable;

public class Job  implements Serializable{
	
	private String jobId;
	private String companyId;
	private String designation;
	private String jobDescription;
	private String desiredProfile;
	private String experience;
	
	private String location;
	private String keywords;
	private String contactMailId;
	private String contactPhone;
	private String jobPostDate;
	
	private String companyName;
	private String aboutCompany;
	private String industryType;
	private String websiteUrl;
	
	
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getAboutCompany() {
		return aboutCompany;
	}
	public void setAboutCompany(String aboutCompany) {
		this.aboutCompany = aboutCompany;
	}
	public String getIndustryType() {
		return industryType;
	}
	public void setIndustryType(String industryType) {
		this.industryType = industryType;
	}
	public String getWebsiteUrl() {
		return websiteUrl;
	}
	public void setWebsiteUrl(String websiteUrl) {
		this.websiteUrl = websiteUrl;
	}
	public String getJobId() {
		return jobId;
	}
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}
	public String getCompanyId() {
		return companyId;
	}
	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}
	public String getDesignation() {
		return designation;
	}
	public void setDesignation(String designation) {
		this.designation = designation;
	}
	public String getJobDescription() {
		return jobDescription;
	}
	public void setJobDescription(String jobDescription) {
		this.jobDescription = jobDescription;
	}
	public String getDesiredProfile() {
		return desiredProfile;
	}
	public void setDesiredProfile(String desiredProfile) {
		this.desiredProfile = desiredProfile;
	}
	public String getExperience() {
		return experience;
	}
	public void setExperience(String experience) {
		this.experience = experience;
	}

	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getKeywords() {
		return keywords;
	}
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	public String getContactMailId() {
		return contactMailId;
	}
	public void setContactMailId(String contactMailId) {
		this.contactMailId = contactMailId;
	}
	public String getContactPhone() {
		return contactPhone;
	}
	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}
	public String getJobPostDate() {
		return jobPostDate;
	}
	public void setJobPostDate(String jobPostDate) {
		this.jobPostDate = jobPostDate;
	}
	

	
}
