package com.nmims.beans;

import java.io.Serializable;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

//spring security related changes rename ProgramBean to ProgramExamBean
public class ProgramExamBean  extends BaseExamBean implements Serializable{
	private String id;
	private String program;
	private String programname;
	private String programcode;
	private String programDuration;
	private String modeOfLearning;
	private String programDurationUnit;
	private String programType;
	private String noOfSubjectsToClear;
	private String noOfSubjectsToClearLateral;
	private String programStructure;
	private String examDurationInMinutes;
	private String noOfSemesters;
	private String noOfSubjectsToClearSem;
	private String SpecializationId;
	private String active;
	private String consumerProgramStructureId;
	private String code;
	private String name;
	private boolean errorRecord;
	private String errorMessage;
	private String specializationType;
	private CommonsMultipartFile fileData;
	private String specializationName;
	private String specialization;
	public String getSpecialization() {
		return specialization;
	}

	public void setSpecialization(String specialization) {
		this.specialization = specialization;
	}


	public String getSpecializationId() {
		return SpecializationId;
	}

	public void setSpecializationId(String specializationId) {
		SpecializationId = specializationId;
	}

	public String getModeOfLearning() {
		return modeOfLearning;
	}

	public void setModeOfLearning(String modeOfLearning) {
		this.modeOfLearning = modeOfLearning;
	}

	public boolean getErrorRecord() {
		return errorRecord;
	}

	public void setErrorRecord(boolean errorRecord) {
		this.errorRecord = errorRecord;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

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

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public String getConsumerProgramStructureId() {
		return consumerProgramStructureId;
	}

	public void setConsumerProgramStructureId(String consumerProgramStructureId) {
		this.consumerProgramStructureId = consumerProgramStructureId;
	}



	
	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
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
	public String getProgramType() {
		return programType;
	}
	public void setProgramType(String programType) {
		this.programType = programType;
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
	public String getNoOfSemesters() {
		return noOfSemesters;
	}
	public void setNoOfSemesters(String noOfSemesters) {
		this.noOfSemesters = noOfSemesters;
	}
	public String getNoOfSubjectsToClearSem() {
		return noOfSubjectsToClearSem;
	}
	public void setNoOfSubjectsToClearSem(String noOfSubjectsToClearSem) {
		this.noOfSubjectsToClearSem = noOfSubjectsToClearSem;
	}

	public String getSpecializationType() {
		return specializationType;
	}

	public void setSpecializationType(String specializationType) {
		this.specializationType = specializationType;
	}

	public CommonsMultipartFile getFileData() {
		return fileData;
	}

	public void setFileData(CommonsMultipartFile fileData) {
		this.fileData = fileData;
	}

	public String getSpecializationName() {
		return specializationName;
	}

	public void setSpecializationName(String specializationName) {
		this.specializationName = specializationName;
	}

	@Override
	public String toString() {
		return "ProgramExamBean [id=" + id + ", program=" + program + ", programname=" + programname + ", programcode="
				+ programcode + ", programDuration=" + programDuration + ", modeOfLearning=" + modeOfLearning
				+ ", programDurationUnit=" + programDurationUnit + ", programType=" + programType
				+ ", noOfSubjectsToClear=" + noOfSubjectsToClear + ", noOfSubjectsToClearLateral="
				+ noOfSubjectsToClearLateral + ", programStructure=" + programStructure + ", examDurationInMinutes="
				+ examDurationInMinutes + ", noOfSemesters=" + noOfSemesters + ", noOfSubjectsToClearSem="
				+ noOfSubjectsToClearSem + ", Specializationid= "+SpecializationId +", active=" + active + ", consumerProgramStructureId="
				+ consumerProgramStructureId + ", code=" + code + ", name=" + name + ", errorRecord=" + errorRecord
				+ ", errorMessage=" + errorMessage + ", specializationType=" + specializationType + ", fileData="
				+ fileData + ", specializationName=" + specializationName + ", specialization=" + specialization + "]";
	}
	
	
	
}