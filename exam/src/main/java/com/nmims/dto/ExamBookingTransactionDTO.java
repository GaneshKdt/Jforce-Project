package com.nmims.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * 
 * @author Siddheshwar_K
 *
 */
@SuppressWarnings("serial")
//@Data
public class ExamBookingTransactionDTO implements Serializable{
	
	private String subject;
	
	private String sapid;
	private String lastName;
	private String firstName;
	private String emailId;
	private String mobile;
	private String altPhone;
	
	private String year;
	private String month;
	
	private List<String> releaseSubjects = new ArrayList<>();
	private List<String> releaseReasonsList = new ArrayList<>();
	
	private String trackId;
	private String tranStatus;
	private String booked;
	
	private String centerId;
	private String examDate;
	private String examTime;
	
	private String productType;
	private String chargesStatus;
	private String lastModifiedBy;
	
	private String description;
	
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getLastModifiedBy() {
		return lastModifiedBy;
	}
	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getAltPhone() {
		return altPhone;
	}
	public void setAltPhone(String altPhone) {
		this.altPhone = altPhone;
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
	public List<String> getReleaseSubjects() {
		return releaseSubjects;
	}
	public void setReleaseSubjects(List<String> releaseSubjects) {
		this.releaseSubjects = releaseSubjects;
	}
	public List<String> getReleaseReasonsList() {
		return releaseReasonsList;
	}
	public void setReleaseReasonsList(List<String> releaseReasonsList) {
		this.releaseReasonsList = releaseReasonsList;
	}
	public String getTrackId() {
		return trackId;
	}
	public void setTrackId(String trackId) {
		this.trackId = trackId;
	}
	public String getTranStatus() {
		return tranStatus;
	}
	public void setTranStatus(String tranStatus) {
		this.tranStatus = tranStatus;
	}
	public String getBooked() {
		return booked;
	}
	public void setBooked(String booked) {
		this.booked = booked;
	}
	public String getCenterId() {
		return centerId;
	}
	public void setCenterId(String centerId) {
		this.centerId = centerId;
	}
	public String getExamDate() {
		return examDate;
	}
	public void setExamDate(String examDate) {
		this.examDate = examDate;
	}
	public String getExamTime() {
		return examTime;
	}
	public void setExamTime(String examTime) {
		this.examTime = examTime;
	}
	public String getProductType() {
		return productType;
	}
	public void setProductType(String productType) {
		this.productType = productType;
	}
	public String getChargesStatus() {
		return chargesStatus;
	}
	public void setChargesStatus(String chargesStatus) {
		this.chargesStatus = chargesStatus;
	}
	
}
