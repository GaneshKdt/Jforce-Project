package com.nmims.beans;

import java.io.Serializable;

public class UGConsentExcelReportBean implements Serializable{

	private String sapid;
	private String consent_option;
	private String type;
	private String program;
	private String studentName;
	private String dob;
	private String mobile;
	private String emailId;
	private String dateOfSubmission;
	private String option;
	private String lcName;
	private String centerCode;
	private String information_center;
	private String consent_optionid;
	private String programStatus;
	public String getProgramStatus() {
		return programStatus;
	}
	public void setProgramStatus(String programStatus) {
		this.programStatus = programStatus;
	}
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public String getConsent_option() {
		return consent_option;
	}
	public void setConsent_option(String consent_option) {
		this.consent_option = consent_option;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getProgram() {
		return program;
	}
	public void setProgram(String program) {
		this.program = program;
	}
	public String getStudentName() {
		return studentName;
	}
	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}
	public String getDob() {
		return dob;
	}
	public void setDob(String dob) {
		this.dob = dob;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public String getDateOfSubmission() {
		return dateOfSubmission;
	}
	public void setDateOfSubmission(String dateOfSubmission) {
		this.dateOfSubmission = dateOfSubmission;
	}
	public String getOption() {
		return option;
	}
	public void setOption(String option) {
		this.option = option;
	}
	public String getLcName() {
		return lcName;
	}
	public void setLcName(String lcName) {
		this.lcName = lcName;
	}
	
	public String getCenterCode() {
		return centerCode;
	}
	public void setCenterCode(String centerCode) {
		this.centerCode = centerCode;
	}
	
	public String getInformation_center() {
		return information_center;
	}
	public void setInformation_center(String information_center) {
		this.information_center = information_center;
	}
	public String getConsent_optionid() {
		return consent_optionid;
	}
	public void setConsent_optionid(String consent_optionid) {
		this.consent_optionid = consent_optionid;
	}
	@Override
	public String toString() {
		return "UGConsentExcelReportBean [sapid=" + sapid + ", consent_option=" + consent_option + ", type=" + type
				+ ", program=" + program + ", studentName=" + studentName + ", dob=" + dob + ", mobile=" + mobile
				+ ", emailId=" + emailId + ", dateOfSubmission=" + dateOfSubmission + ", option=" + option + ", lcName="
				+ lcName + ", centerCode=" + centerCode + ", information_center=" + information_center
				+ ", consent_optionid=" + consent_optionid + "]";
	}
}
