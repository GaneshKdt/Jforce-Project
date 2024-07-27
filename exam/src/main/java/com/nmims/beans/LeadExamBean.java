package com.nmims.beans;

import java.io.Serializable;

//spring security related changes rename LeadBean to LeadExamBean
public class LeadExamBean  implements Serializable  {
	private String leadId;
	private String Name;
	private String registrationNum;
	private String dob;
	private String email;
	private String number;
	private String program;
	private String code;
	private String session;
	private String year;
	private String programStructure;
	private String consumerProgramStructureId;
	private String imageUrl;
	
	private boolean error;
	
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public String getLeadId() {
		return leadId;
	}
	public void setLeadId(String leadId) {
		this.leadId = leadId;
	}
	public String getRegistrationNum() {
		return registrationNum;
	}
	public void setRegistrationNum(String registrationNum) {
		this.registrationNum = registrationNum;
	}
	public String getDob() {
		return dob;
	}
	public void setDob(String dob) {
		this.dob = dob;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getProgram() {
		return program;
	}
	public void setProgram(String program) {
		this.program = program;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getSession() {
		return session;
	}
	public void setSession(String session) {
		this.session = session;
	}
	public boolean getError() {
		return error;
	}
	public void setError(boolean error) {
		this.error = error;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getProgramStructure() {
		return programStructure;
	}
	public void setProgramStructure(String programStructure) {
		this.programStructure = programStructure;
	}
	public String getConsumerProgramStructureId() {
		return consumerProgramStructureId;
	}
	public void setConsumerProgramStructureId(String consumerProgramStructureId) {
		this.consumerProgramStructureId = consumerProgramStructureId;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	@Override
	public String toString() {
		return "LeadBean [leadId=" + leadId + ", Name=" + Name + ", registrationNum=" + registrationNum + ", email="
				+ email + ", code=" + code + ", year=" + year + ", programStructure=" + programStructure
				+ ", consumerProgramStructureId=" + consumerProgramStructureId + ", number=" + number + ", imageUrl=" + imageUrl + ", error="
				+ error + "]";
	}
	
}
