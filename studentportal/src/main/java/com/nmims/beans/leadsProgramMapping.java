package com.nmims.beans;

import java.io.Serializable;

public class leadsProgramMapping  implements Serializable {
	private String leads_id;
	private String programs_id;
	private String accessTillDate;
	private String created_at;
	private String updated_at;
	
	private String programName; 
	private String status;
	private String message;
	
	private String sem;
	private String consumerProgramStructureId;
	
	
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
	public String getSem() {
		return sem;
	}
	public void setSem(String sem) {
		this.sem = sem;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getAccessTillDate() {
		return accessTillDate;
	}
	public void setAccessTillDate(String accessTillDate) {
		this.accessTillDate = accessTillDate;
	}
	public String getLeads_id() {
		return leads_id;
	}
	public void setLeads_id(String leads_id) {
		this.leads_id = leads_id;
	}
	public String getPrograms_id() {
		return programs_id;
	}
	public void setPrograms_id(String programs_id) {
		this.programs_id = programs_id;
	}
	public String getCreated_at() {
		return created_at;
	}
	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}
	public String getUpdated_at() {
		return updated_at;
	}
	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}
	
	
}
