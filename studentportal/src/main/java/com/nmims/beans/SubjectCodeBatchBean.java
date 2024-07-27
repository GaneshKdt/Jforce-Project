package com.nmims.beans;

public class SubjectCodeBatchBean {
	private String id;
	private String programId;
	private String programStructureId;
	private String consumerTypeId;
	private String programSemSubjectId;
	private String consumerProgramStructureId;
	private String subjectcode;
	
	private String subjectName;
	private String subjectCodeId;
	private String program;
	private String program_structure;
	private String consumer_type;
	private String batchId;
	private String batchName;
	private String timeboundId;
	private String acadMonth;
	private String acadYear;
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
	public String getProgramSemSubjectId() {
		return programSemSubjectId;
	}
	public void setProgramSemSubjectId(String programSemSubjectId) {
		this.programSemSubjectId = programSemSubjectId;
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
	public String getBatchId() {
		return batchId;
	}
	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}
	@Override
	public String toString() {
		return "AnnouncementMasterMappingBean [id=" + id + ", programId=" + programId + ", programStructureId="
				+ programStructureId + ", consumerTypeId=" + consumerTypeId + ", programSemSubjectId="
				+ programSemSubjectId + ", consumerProgramStructureId=" + consumerProgramStructureId + ", subjectcode="
				+ subjectcode + ", subjectName=" + subjectName + ", subjectCodeId=" + subjectCodeId + ", batchId="
				+ batchId +", acadMonth=" + acadMonth + ", acadYear="
						+ acadYear + "]";
	}
	public String getSubjectName() {
		return subjectName;
	}
	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}
	public String getSubjectCodeId() {
		return subjectCodeId;
	}
	public void setSubjectCodeId(String subjectCodeId) {
		this.subjectCodeId = subjectCodeId;
	}
	public String getBatchName() {
		return batchName;
	}
	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}
	public String getProgram() {
		return program;
	}
	public void setProgram(String program) {
		this.program = program;
	}
	public String getProgram_structure() {
		return program_structure;
	}
	public void setProgram_structure(String program_structure) {
		this.program_structure = program_structure;
	}
	public String getConsumer_type() {
		return consumer_type;
	}
	public void setConsumer_type(String consumer_type) {
		this.consumer_type = consumer_type;
	}
	public String getTimeboundId() {
		return timeboundId;
	}
	public void setTimeboundId(String timeboundId) {
		this.timeboundId = timeboundId;
	}
	public String getAcadMonth() {
		return acadMonth;
	}
	public void setAcadMonth(String acadMonth) {
		this.acadMonth = acadMonth;
	}
	public String getAcadYear() {
		return acadYear;
	}
	public void setAcadYear(String acadYear) {
		this.acadYear = acadYear;
	}
}
