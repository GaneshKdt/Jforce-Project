/**
 * 
 */
package com.nmims.beans;

import java.io.Serializable;

/**
 * @author vil_m
 *
 */
public class LiveSessionReportAdminDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String acadYear;
	private String acadMonth;
	private String studentType;
	
	private String sapId;
	private String studentName;
	private String emailId;
	private String phone;
	private String subjectName;
	private String sem;
	
	private String centerName;
	private String consumerType;
	private String programStructure;
	private String program;
	
	private String sessionType;
	
	public LiveSessionReportAdminDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public LiveSessionReportAdminDTO(String acadYear, String acadMonth, String sapId, String studentName,
			String emailId, String phone, String subjectName, String sem, String centerName, String consumerType,
			String programStructure, String program, String sessionType) {
		super();
		this.acadYear = acadYear;
		this.acadMonth = acadMonth;
		this.sapId = sapId;
		this.studentName = studentName;
		this.emailId = emailId;
		this.phone = phone;
		this.subjectName = subjectName;
		this.sem = sem;
		this.centerName = centerName;
		this.consumerType = consumerType;
		this.programStructure = programStructure;
		this.program = program;
		this.sessionType= sessionType;
	}

	public String getAcadYear() {
		return acadYear;
	}
	public void setAcadYear(String acadYear) {
		this.acadYear = acadYear;
	}
	public String getAcadMonth() {
		return acadMonth;
	}
	public void setAcadMonth(String acadMonth) {
		this.acadMonth = acadMonth;
	}
	public String getStudentType() {
		return studentType;
	}
	public void setStudentType(String studentType) {
		this.studentType = studentType;
	}
	public String getSapId() {
		return sapId;
	}
	public void setSapId(String sapId) {
		this.sapId = sapId;
	}
	public String getStudentName() {
		return studentName;
	}
	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getSubjectName() {
		return subjectName;
	}
	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}
	public String getSem() {
		return sem;
	}
	public void setSem(String sem) {
		this.sem = sem;
	}
	public String getCenterName() {
		return centerName;
	}
	public void setCenterName(String centerName) {
		this.centerName = centerName;
	}
	public String getConsumerType() {
		return consumerType;
	}
	public void setConsumerTypeName(String consumerType) {
		this.consumerType = consumerType;
	}
	public String getProgramStructure() {
		return programStructure;
	}
	public void setProgramStructureName(String programStructure) {
		this.programStructure = programStructure;
	}
	public String getProgram() {
		return program;
	}
	public void setProgramName(String program) {
		this.program = program;
	}

	public String getSessionType() {
		return sessionType;
	}

	public void setSessionType(String sessionType) {
		this.sessionType = sessionType;
	}
	
}
