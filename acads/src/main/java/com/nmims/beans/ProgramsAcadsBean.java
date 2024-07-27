package com.nmims.beans;

import java.io.Serializable;

public class ProgramsAcadsBean  implements Serializable  {
	
	/**
	 * Change Name from ProgramsBean to ProgramsAcadsBean for serializable issue
	 */
	
	private String id;
 private String program;
 private String programname;
 private String programcode;
 private String programDuration;
 private String programDurationUnit;
 private String programType;
 private String noOfSubjectsToClear;
 private String noOfSubjectsToClearLateral;	
 private String programStructure;
 private String examDurationInMinutes;
 private String noOfSemesters;
 private String noOfSubjectsToClearSem;
 private String active;
 private String code;
 private String name;
 private String consumerType;
 private String subject;
 private String consumerProgramStructureId;
public String getCode() {
	return code;
}
public void setCode(String code) {
	this.code = code;
}
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
public String getNoOfSemesters() {
	return noOfSemesters;
}
public void setNoOfSemesters(String noOfSemesters) {
	this.noOfSemesters = noOfSemesters;
}


public String getProgram() {
	return program;
}
public void setProgram(String program) {
	this.program = program;
}
public String getProgramname() {
	return programname;
}
public void setProgramname(String programname) {
	this.programname = programname;
}
public String getProgramcode() {
	return programcode;
}
public void setProgramcode(String programcode) {
	this.programcode = programcode;
}
public String getProgramDuration() {
	return programDuration;
}
public void setProgramDuration(String programDuration) {
	this.programDuration = programDuration;
}
public String getProgramDurationUnit() {
	return programDurationUnit;
}
public void setProgramDurationUnit(String programDurationUnit) {
	this.programDurationUnit = programDurationUnit;
}
public String getProgramType() {
	return programType;
}
public void setProgramType(String programType) {
	this.programType = programType;
}
public String getNoOfSubjectsToClear() {
	return noOfSubjectsToClear;
}
public void setNoOfSubjectsToClear(String noOfSubjectsToClear) {
	this.noOfSubjectsToClear = noOfSubjectsToClear;
}
public String getNoOfSubjectsToClearLateral() {
	return noOfSubjectsToClearLateral;
}
public void setNoOfSubjectsToClearLateral(String noOfSubjectsToClearLateral) {
	this.noOfSubjectsToClearLateral = noOfSubjectsToClearLateral;
}
public String getProgramStructure() {
	return programStructure;
}
public void setProgramStructure(String programStructure) {
	this.programStructure = programStructure;
}
public String getExamDurationInMinutes() {
	return examDurationInMinutes;
}
public void setExamDurationInMinutes(String examDurationInMinutes) {
	this.examDurationInMinutes = examDurationInMinutes;
}

public String getNoOfSubjectsToClearSem() {
	return noOfSubjectsToClearSem;
}
public void setNoOfSubjectsToClearSem(String noOfSubjectsToClearSem) {
	this.noOfSubjectsToClearSem = noOfSubjectsToClearSem;
}
public String getActive() {
	return active;
}
public void setActive(String active) {
	this.active = active;
}
@Override
public String toString() {
	return "ProgramsBean [id=" + id + ", program=" + program + ", programname=" + programname + ", programcode="
			+ programcode + ", programDuration=" + programDuration + ", programDurationUnit=" + programDurationUnit
			+ ", programType=" + programType + ", noOfSubjectsToClear=" + noOfSubjectsToClear
			+ ", noOfSubjectsToClearLateral=" + noOfSubjectsToClearLateral + ", programStructure=" + programStructure
			+ ", examDurationInMinutes=" + examDurationInMinutes + ", noOfSemesters=" + noOfSemesters
			+ ", noOfSubjectsToClearSem=" + noOfSubjectsToClearSem + ", active=" + active + "]";
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
public String getConsumerProgramStructureId() {
	return consumerProgramStructureId;
}
public void setConsumerProgramStructureId(String consumerProgramStructureId) {
	this.consumerProgramStructureId = consumerProgramStructureId;
}
 

}
