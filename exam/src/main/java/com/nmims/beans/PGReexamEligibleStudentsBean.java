package com.nmims.beans;

import java.io.Serializable;

public class PGReexamEligibleStudentsBean  implements Serializable  {
	private String sapid;
	private String subject;
	private String examYear;
	private String examMonth;
	private String examSem;
	private String studentValidityEndMonth;
	private String studentValidityEndYear;
	private String enrollmentYear;
	private String enrollmentMonth;
	private String PrgmStructApplicable;
	private String consumerType;
	private String program;
	private String failReason;
	private String firstName;
	private String lastName;
	
	
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
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
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
	public String getExamSem() {
		return examSem;
	}
	public void setExamSem(String examSem) {
		this.examSem = examSem;
	}
	public String getStudentValidityEndMonth() {
		return studentValidityEndMonth;
	}
	public void setStudentValidityEndMonth(String studentValidityEndMonth) {
		this.studentValidityEndMonth = studentValidityEndMonth;
	}
	public String getStudentValidityEndYear() {
		return studentValidityEndYear;
	}
	public void setStudentValidityEndYear(String studentValidityEndYear) {
		this.studentValidityEndYear = studentValidityEndYear;
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
	public String getProgram() {
		return program;
	}
	public void setProgram(String program) {
		this.program = program;
	}
	public String getFailReason() {
		return failReason;
	}
	public void setFailReason(String failReason) {
		this.failReason = failReason;
	}
	
	
}
