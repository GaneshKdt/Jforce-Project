package com.nmims.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.nmims.beans.ServiceRequestCustomPDFContentBean;
//@JsonInclude(Include.NON_NULL)
public class StudentSrDTO implements Serializable{
	private String sapId;
	private String firstName;
	private String lastName;
	private String program;
	private String enrollmentMonth;
	private int enrollmentYear;
	private String additionalInfo1;
	private String requestStatus;
	private String filePath;
	private String programDurationUnit;
	private String programDuration;
	private String programName;
	private String programStatus;
	private int sem;
	private String gender;
	private Map<String, String> customPDFContent;
    private String validityEndMonth;
    private String validityEndYear;
	
	public String getProgramStatus() {
		return programStatus;
	}
	public void setProgramStatus(String programStatus) {
		this.programStatus = programStatus;
	}
	public String getProgramName() {
		return programName;
	}
	public void setProgramName(String programName) {
		this.programName = programName;
	}
	public int getSem() {
		return sem;
	}
	public void setSem(int sem) {
		this.sem = sem;
	}
	public String getProgramDurationUnit() {
		return programDurationUnit;
	}
	public void setProgramDurationUnit(String programDurationUnit) {
		this.programDurationUnit = programDurationUnit;
	}
	public String getProgramDuration() {
		return programDuration;
	}
	public void setProgramDuration(String programDuration) {
		this.programDuration = programDuration;
	}
	public String getRequestStatus() {
		return requestStatus;
	}
	public void setRequestStatus(String requestStatus) {
		this.requestStatus = requestStatus;
	}
	public String getSapId() {
		return sapId;
	}
	public void setSapId(String sapId) {
		this.sapId = sapId;
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
	public String getProgram() {
		return program;
	}
	public void setProgram(String program) {
		this.program = program;
	}
	public String getEnrollmentMonth() {
		return enrollmentMonth;
	}
	public void setEnrollmentMonth(String enrollmentMonth) {
		this.enrollmentMonth = enrollmentMonth;
	}
	public int getEnrollmentYear() {
		return enrollmentYear;
	}
	public void setEnrollmentYear(int enrollmentYear) {
		this.enrollmentYear = enrollmentYear;
	}
	public String getAdditionalInfo1() {
		return additionalInfo1;
	}
	public void setAdditionalInfo1(String additionalInfo1) {
		this.additionalInfo1 = additionalInfo1;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public Map<String, String> getCustomPDFContent() {
		return customPDFContent;
	}
	public void setCustomPDFContent(Map<String, String> customPDFContent) {
		this.customPDFContent = customPDFContent;
	}
	public String getValidityEndMonth() {
		return validityEndMonth;
	}
	public void setValidityEndMonth(String validityEndMonth) {
		this.validityEndMonth = validityEndMonth;
	}
	public String getValidityEndYear() {
		return validityEndYear;
	}
	public void setValidityEndYear(String validityEndYear) {
		this.validityEndYear = validityEndYear;
	}
	@Override
	public String toString() {
		return "StudentSrDTO [sapId=" + sapId + ", firstName=" + firstName + ", lastName=" + lastName + ", program="
				+ program + ", enrollmentMonth=" + enrollmentMonth + ", enrollmentYear=" + enrollmentYear
				+ ", additionalInfo1=" + additionalInfo1 + ", requestStatus=" + requestStatus + ", filePath=" + filePath
				+ ", programDurationUnit=" + programDurationUnit + ", programDuration=" + programDuration
				+ ", programName=" + programName + ", programStatus=" + programStatus + ", sem=" + sem + ", gender="
				+ gender + ", customPDFContent=" + customPDFContent + ", validityEndMonth=" + validityEndMonth
				+ ", validityEndYear=" + validityEndYear + "]";
	}
	
	
}