package com.nmims.dto;

import java.util.List;

public class StudentSubjectCodeDto {


	private String sapid;
	private String subjectCodeId;
	private String subject;
	private String programSemSubjectId;
	private List<String> programSemSubjectIdList;
	private String acadCycle;

	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public String getSubjectCodeId() {
		return subjectCodeId;
	}
	public void setSubjectCodeId(String subjectCodeId) {
		this.subjectCodeId = subjectCodeId;
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
	public String getAcadCycle() {
		return acadCycle;
	}
	public void setAcadCycle(String acadCycle) {
		this.acadCycle = acadCycle;
	}
	public List<String> getProgramSemSubjectIdList() {
		return programSemSubjectIdList;
	}
	public void setProgramSemSubjectIdList(List<String> programSemSubjectIdList) {
		this.programSemSubjectIdList = programSemSubjectIdList;
	}
	
}
