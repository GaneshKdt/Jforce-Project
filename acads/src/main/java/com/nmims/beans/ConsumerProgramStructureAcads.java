package com.nmims.beans;

import java.io.Serializable;

public class ConsumerProgramStructureAcads implements Serializable {

	
	public ConsumerProgramStructureAcads(String subjectCodeId, String subjectCode, String subject) {
		super();
		this.subjectCodeId = subjectCodeId;
		this.subjectCode = subjectCode;
		this.subject = subject;
	}
	public ConsumerProgramStructureAcads() {
		super();
	}
	/**
	 * Change Name from ConsumerProgramStructure to ConsumerProgramStructureAcads for serializable issue
	 */
	
	private String id;
	private String programId;
	private String programStructureId;
	private String name;
	private String code;
	private String consumerTypeId;
	private String programSemSubjectId;
	private String subjectCodeId;
	private String subjectCode;
	private String consumerProgramStructureId;
	//For masterKey mapped subject code with name
	private String subjectcode;
	
	private String program;
	private String programStructure;
	private String consumerType;
	private String subject;
	private String pssId;
	
	private String programType;
	private String batchId;
	private String sem;
	
	public String getSubjectName() {
		return subjectName;
	}
	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}
	private String subjectName;

	
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
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getProgramSemSubjectId() {
		return programSemSubjectId;
	}
	public void setProgramSemSubjectId(String programSemSubjectId) {
		this.programSemSubjectId = programSemSubjectId;
	}
	public String getSubjectCodeId() {
		return subjectCodeId;
	}
	public void setSubjectCodeId(String subjectCodeId) {
		this.subjectCodeId = subjectCodeId;
	}
	public String getSubjectCode() {
		return subjectCode;
	}
	public void setSubjectCode(String subjectCode) {
		this.subjectCode = subjectCode;
	}
	
	public String getConsumerProgramStructureId() {
		return consumerProgramStructureId;
	}
	public void setConsumerProgramStructureId(String consumerProgramStructureId) {
		this.consumerProgramStructureId = consumerProgramStructureId;
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
	public String getPssId() {
		return pssId;
	}
	public void setPssId(String pssId) {
		this.pssId = pssId;
	}
	@Override
	public String toString() {
		return "ConsumerProgramStructureAcads [id=" + id + ", programId=" + programId + ", programStructureId="
				+ programStructureId + ", name=" + name + ", code=" + code + ", consumerTypeId=" + consumerTypeId
				+ ", programSemSubjectId=" + programSemSubjectId + ", subjectCodeId=" + subjectCodeId + ", subjectCode="
				+ subjectCode + ", consumerProgramStructureId=" + consumerProgramStructureId + ", subjectcode="
				+ subjectcode + ", program=" + program + ", programStructure=" + programStructure + ", consumerType="
				+ consumerType + ", subject=" + subject + ", pssId=" + pssId + ", programType=" + programType
				+ ", batchId=" + batchId + ", sem=" + sem + ", subjectName=" + subjectName + ", month=" + month
				+ ", year=" + year + "]";
	}
	

	public String getSem() {
		return sem;
	}
	public void setSem(String sem) {
		this.sem = sem;
	}
	private String month;
	private String year;
	
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;

	}
	public String getProgramType() {
		return programType;
	}
	public void setProgramType(String programType) {
		this.programType = programType;
	}
	public String getBatchId() {
		return batchId;
	}
	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((programSemSubjectId == null) ? 0 : programSemSubjectId.hashCode());
		result = prime * result + ((subject == null) ? 0 : subject.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConsumerProgramStructureAcads other = (ConsumerProgramStructureAcads) obj;
		if (programSemSubjectId == null) {
			if (other.programSemSubjectId != null)
				return false;
		} else if (!programSemSubjectId.equals(other.programSemSubjectId))
			return false;
		if (subject == null) {
			if (other.subject != null)
				return false;
		} else if (!subject.equals(other.subject))
			return false;
		return true;
	}
}

