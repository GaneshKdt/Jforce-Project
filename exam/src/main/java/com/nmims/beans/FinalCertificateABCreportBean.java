package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

public class FinalCertificateABCreportBean implements Serializable{

	private String sapid;
	private String monthyear;
	private String examYear;
	private String examMonth;
	private String programName;
	private String enrollmentYear;
	private String enrollmentMonth;
	private String studentName;
	private String gender;
	private String dateOfBirth;
	private String mobile;
	private String email;
	private String fatherName;
	private String motherName ;
	private String result;
	private String passingYear;
	private String passingMonth ;
	private String declareDate;
	private String certificateNumber ;
	
	public String getDeclareDate() {
		return declareDate;
	}
	public void setDeclareDate(String declareDate) {
		this.declareDate = declareDate;
	}
	public String getMonthyear() {
		return monthyear;
	}
	public void setMonthyear(String monthyear) {
		this.monthyear = monthyear;
	}
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}

	public String getExamYear() {
		return examYear;
	}
	public void setExamYear(String examYear) {
		this.examYear = examYear;
	}
	public String getExamMonth() {
		return examMonth;
	}
	public void setExamMonth(String examMonth) {
		this.examMonth = examMonth;
	}

	public String getProgramName() {
		return programName;
	}
	public void setProgramName(String programName) {
		this.programName = programName;
	}
	public String getEnrollmentYear() {
		return enrollmentYear;
	}
	public void setEnrollmentYear(String enrollmentYear) {
		this.enrollmentYear = enrollmentYear;
	}
	public String getEnrollmentMonth() {
		return enrollmentMonth;
	}
	public void setEnrollmentMonth(String enrollmentMonth) {
		this.enrollmentMonth = enrollmentMonth;
	}
	public String getStudentName() {
		return studentName;
	}
	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getDateOfBirth() {
		return dateOfBirth;
	}
	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getFatherName() {
		return fatherName;
	}
	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
	}
	public String getMotherName() {
		return motherName;
	}
	public void setMotherName(String motherName) {
		this.motherName = motherName;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}

	public String getPassingYear() {
		return passingYear;
	}
	public void setPassingYear(String passingYear) {
		this.passingYear = passingYear;
	}
	public String getPassingMonth() {
		return passingMonth;
	}
	public void setPassingMonth(String passingMonth) {
		this.passingMonth = passingMonth;
	}

	public String getCertificateNumber() {
		return certificateNumber;
	}
	public void setCertificateNumber(String certificateNumber) {
		this.certificateNumber = certificateNumber;
	}
	@Override
	public String toString() {
		return "FinalCertificateABCreportBean [examYear=" + examYear + ", examMonth=" + examMonth
				+ ", programName=" + programName + ", enrollmentYear="
				+ enrollmentYear + ", enrollmentMonth=" + enrollmentMonth + ", studentName=" + studentName + ", gender="
				+ gender + ", dateOfBirth=" + dateOfBirth + ", mobile=" + mobile + ", email=" + email + ", fatherName="
				+ fatherName + ", motherName=" + motherName + ", result=" + result  +" "
				+ ", passingYear=" + passingYear + ", passingMonth=" + passingMonth  + ", certificateNumber=" + certificateNumber + "]";
	}
	
	
	
	
}
