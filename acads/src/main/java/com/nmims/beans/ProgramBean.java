package com.nmims.beans;

import java.io.Serializable;

public class ProgramBean  implements Serializable  {
	private String program;
	private String programname;
	private String programcode;
	private String code;
	private String name;
	private String id;
	
	private String programId;
	private String programStructure;
	private String consumerType;
	
	public String getConsumerType() {
		return consumerType;
	}
	public void setConsumerType(String consumerType) {
		this.consumerType = consumerType;
	}
	public String getProgramId() {
		return programId;
	}
	public void setProgramId(String programId) {
		this.programId = programId;
	}
	public String getProgramStructure() {
		return programStructure;
	}
	public void setProgramStructure(String programStructure) {
		this.programStructure = programStructure;
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
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	
	@Override
	public String toString() {
		return "ProgramBean [program=" + program + ", programname=" + programname + ", programcode=" + programcode
				+ ", code=" + code + ", name=" + name + ", id=" + id + ", programId=" + programId
				+ ", programStructure=" + programStructure + ", consumerType=" + consumerType + "]";
	}
	
}
