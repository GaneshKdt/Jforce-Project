package com.nmims.beans;

import java.io.Serializable;

public class ConsumerProgramStructureAcadsBean implements Serializable {

	@Override
	public String toString() {
		return "ConsumerProgramStructure [id=" + id + ", programId=" + programId + ", programStructureId="
				+ programStructureId + ", name=" + name + ", code=" + code + ", consumerTypeId=" + consumerTypeId + "]";
	}
	private String id;
	private String programId;
	private String programStructureId;
	private String name;
	private String code;
	private String consumerTypeId;
	
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
	
	
}
