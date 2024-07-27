package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

public class PackageRequirementsMasterMapping implements Serializable {

	private String requirementsId;
	private String consumerType;
	private String programStructureName;
	private String programName;
	private int requiredSemMin;
	private int requiredSemMax;
	private int minSubjectsClearedTotal;
	private int minSubjectsClearedPerSem;
	private boolean availableForAlumni;
	private boolean availableForAlumniOnly;
	private int alumniMaxMonthsAfterLastRegistration;
	private String packageId;
	private List<String> programIds;
	private List<String> consumerProgramStructureId;
	
	//variable used to store all the info about the master mapping. ie the program name, id, consumer type id, etc
	private List<PackageRequirementsMasterMappingData> consumerProgramStructureMappingData;
	
	
	
	public List<PackageRequirementsMasterMappingData> getConsumerProgramStructureMappingData() {
		return consumerProgramStructureMappingData;
	}
	public void setConsumerProgramStructureMappingData(
			List<PackageRequirementsMasterMappingData> consumerProgramStructureMappingData) {
		this.consumerProgramStructureMappingData = consumerProgramStructureMappingData;
	}
	public String getRequirementsId() {
		return requirementsId;
	}
	public void setRequirementsId(String requirementsId) {
		this.requirementsId = requirementsId;
	}
	public String getConsumerType() {
		return consumerType;
	}
	public void setConsumerType(String consumerType) {
		this.consumerType = consumerType;
	}
	public String getProgramStructureName() {
		return programStructureName;
	}
	public void setProgramStructureName(String programStructureName) {
		this.programStructureName = programStructureName;
	}
	public String getProgramName() {
		return programName;
	}
	public void setProgramName(String programName) {
		this.programName = programName;
	}
	public int getRequiredSemMin() {
		return requiredSemMin;
	}
	public void setRequiredSemMin(int requiredSemMin) {
		this.requiredSemMin = requiredSemMin;
	}
	public int getRequiredSemMax() {
		return requiredSemMax;
	}
	public void setRequiredSemMax(int requiredSemMax) {
		this.requiredSemMax = requiredSemMax;
	}
	public int getMinSubjectsClearedTotal() {
		return minSubjectsClearedTotal;
	}
	public void setMinSubjectsClearedTotal(int minSubjectsClearedTotal) {
		this.minSubjectsClearedTotal = minSubjectsClearedTotal;
	}
	public int getMinSubjectsClearedPerSem() {
		return minSubjectsClearedPerSem;
	}
	public void setMinSubjectsClearedPerSem(int minSubjectsClearedPerSem) {
		this.minSubjectsClearedPerSem = minSubjectsClearedPerSem;
	}
	public boolean isAvailableForAlumni() {
		return availableForAlumni;
	}
	public void setAvailableForAlumni(boolean availableForAlumni) {
		this.availableForAlumni = availableForAlumni;
	}
	public int getAlumniMaxMonthsAfterLastRegistration() {
		return alumniMaxMonthsAfterLastRegistration;
	}
	public void setAlumniMaxMonthsAfterLastRegistration(int alumniMaxMonthsAfterLastRegistration) {
		this.alumniMaxMonthsAfterLastRegistration = alumniMaxMonthsAfterLastRegistration;
	}
	public String getPackageId() {
		return packageId;
	}
	public void setPackageId(String packageId) {
		this.packageId = packageId;
	}
	public List<String> getConsumerProgramStructureId() {
		return consumerProgramStructureId;
	}
	public void setConsumerProgramStructureId(List<String> consumerProgramStructureId) {
		this.consumerProgramStructureId = consumerProgramStructureId;
	}
	public List<String> getProgramIds() {
		return programIds;
	}
	public void setProgramIds(List<String> programIds) {
		this.programIds = programIds;
	}
	public boolean isAvailableForAlumniOnly() {
		return availableForAlumniOnly;
	}
	public void setAvailableForAlumniOnly(boolean availableForAlumniOnly) {
		this.availableForAlumniOnly = availableForAlumniOnly;
	}
}
