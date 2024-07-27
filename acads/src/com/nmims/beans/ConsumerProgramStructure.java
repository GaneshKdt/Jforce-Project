package com.nmims.beans;

public class ConsumerProgramStructure {

	private String id;
	private String programId;
	private String programStructureId;
	private String name;
	
	private String code;
	private String consumerTypeId;
	
	//Added By Riya For subject codes
	private String subjectCodeId;
	
	private String subjectName;
	
	//For masterKey mapped subject code with name
	private String subjectcode;
	private String program;
	private String programStructure;
	private String consumerType;
	private String subject;
	private String pssId;
	
	
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getProgramId() {
		return programId;
	}
	public void setProgramId(String programId) {
		this.programId = programId;
	}
	public String getProgramStructureId() {
		return programStructureId;
	}
	public void setProgramStructureId(String programStructureId) {
		this.programStructureId = programStructureId;
	}
	public String getConsumerTypeId() {
		return consumerTypeId;
	}
	public void setConsumerTypeId(String consumerTypeId) {
		this.consumerTypeId = consumerTypeId;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	
	public String getSubjectCodeId() {
		return subjectCodeId;
	}
	public void setSubjectCodeId(String subjectCodeId) {
		this.subjectCodeId = subjectCodeId;
	}
	
	public String getSubjectName() {
		return subjectName;
	}
	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}
	public String getSubjectcode() {
		return subjectcode;
	}
	public void setSubjectcode(String subjectcode) {
		this.subjectcode = subjectcode;
	}
	public String getProgram() {
		return program;
	}
	public void setProgram(String program) {
		this.program = program;
	}
	public String getProgramStructure() {
		return programStructure;
	}
	public void setProgramStructure(String programStructure) {
		this.programStructure = programStructure;
	}
	public String getConsumerType() {
		return consumerType;
	}
	public void setConsumerType(String consumerType) {
		this.consumerType = consumerType;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getPssId() {
		return pssId;
	}
	public void setPssId(String pssId) {
		this.pssId = pssId;
	}
	@Override
	public String toString() {
		return "ConsumerProgramStructure [id=" + id + ", programId=" + programId + ", programStructureId="
				+ programStructureId + ", name=" + name + ", code=" + code + ", consumerTypeId=" + consumerTypeId
				+ ", subjectCodeId=" + subjectCodeId + ", " + ", subjectName=" + subjectName
				+ ", subjectcode=" + subjectcode + ", program=" + program + ", programStructure=" + programStructure
				+ ", consumerType=" + consumerType + ", subject=" + subject + ", pssId=" + pssId + "]";
	}
	
}
