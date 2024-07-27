package com.nmims.beans;

import java.io.Serializable;

public class ConsumerProgramStructureCareerservicesBean  implements Serializable{

	private String consumerProgramStructureId;
	private String programId;
	private String programStructureId;
	private String consumerTypeId;
	private String consumerTypeName;
	private boolean consumerTypeIsCorporate;
	private String programStructureName;
	private String programCode;
	private String programName;
	
	public String getProgramName() {
		return programName;
	}
	public void setProgramName(String programName) {
		this.programName = programName;
	}
	public String getConsumerProgramStructureId() {
		return consumerProgramStructureId;
	}
	public void setConsumerProgramStructureId(String consumerProgramStructureId) {
		this.consumerProgramStructureId = consumerProgramStructureId;
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
	public String getConsumerTypeName() {
		return consumerTypeName;
	}
	public void setConsumerTypeName(String consumerTypeName) {
		this.consumerTypeName = consumerTypeName;
	}
	public boolean isConsumerTypeIsCorporate() {
		return consumerTypeIsCorporate;
	}
	public void setConsumerTypeIsCorporate(boolean consumerTypeIsCorporate) {
		this.consumerTypeIsCorporate = consumerTypeIsCorporate;
	}
	public String getProgramStructureName() {
		return programStructureName;
	}
	public void setProgramStructureName(String programStructureName) {
		this.programStructureName = programStructureName;
	}
	public String getProgramCode() {
		return programCode;
	}
	public void setProgramCode(String programCode) {
		this.programCode = programCode;
	}
}
