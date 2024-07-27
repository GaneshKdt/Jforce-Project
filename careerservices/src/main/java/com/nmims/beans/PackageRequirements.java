package com.nmims.beans;

import java.io.Serializable;

public class PackageRequirements implements Serializable {

	private String requirementsId;
	private String packageId;
	private String packageName;
	private String consumerProgramStructureId;
	private String consumerType;
	private String programStructureName;
	private String programName;
	private String programCode;
	private int requiredSemMin;
	private int requiredSemMax;
	private int minSubjectsClearedTotal;
	private int minSubjectsClearedPerSem;
	private boolean availableForAlumni;
	private boolean availableForAlumniOnly;
	private int alumniMaxMonthsAfterLastRegistration;
	
	
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
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
	public String getConsumerProgramStructureId() {
		return consumerProgramStructureId;
	}
	public void setConsumerProgramStructureId(String consumerProgramStructureId) {
		this.consumerProgramStructureId = consumerProgramStructureId;
	}
	public String getPackageId() {
		return packageId;
	}
	public void setPackageId(String packageId) {
		this.packageId = packageId;
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
	public String getProgramCode() {
		return programCode;
	}
	public void setProgramCode(String programCode) {
		this.programCode = programCode;
	}
	public boolean isAvailableForAlumniOnly() {
		return availableForAlumniOnly;
	}
	public void setAvailableForAlumniOnly(boolean availableForAlumniOnly) {
		this.availableForAlumniOnly = availableForAlumniOnly;
	}
}
