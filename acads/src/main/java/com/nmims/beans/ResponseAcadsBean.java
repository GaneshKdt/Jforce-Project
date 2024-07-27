package com.nmims.beans;


import java.util.List;
import java.io.Serializable;
import java.util.ArrayList;

public class ResponseAcadsBean  implements Serializable  {
	

	/**
	 * Change Name from ResponseBean to ResponseAcadsBean for serializable issue
	 */
	
private int code;
private String status;
private String message;

private List<ConsumerProgramStructureAcads> programTypeData;
private List<ConsumerProgramStructureAcads> batchData;

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
public int getCode() {
	return code;
}
public void setCode(int code) {
	this.code = code;
}
public List<ConsumerProgramStructureAcads> getProgramTypeData() {
	return programTypeData;
}
public void setProgramTypeData(List<ConsumerProgramStructureAcads> programTypeData) {
	this.programTypeData = programTypeData;
}
public List<ConsumerProgramStructureAcads> getBatchData() {
	return batchData;
}
public void setBatchData(List<ConsumerProgramStructureAcads> batchData) {
	this.batchData = batchData;
}

private ArrayList<ConsumerProgramStructureAcads> programData;
private ArrayList<ConsumerProgramStructureAcads> programStructureData;
private ArrayList<ConsumerProgramStructureAcads> subjectsData;

public ArrayList<ConsumerProgramStructureAcads> getProgramStructureData() {
	return programStructureData;
}
public void setProgramStructureData(ArrayList<ConsumerProgramStructureAcads> programStructureData) {
	this.programStructureData = programStructureData;
}
public ArrayList<ConsumerProgramStructureAcads> getProgramData() {
	return programData;
}
public void setProgramsData(ArrayList<ConsumerProgramStructureAcads> programData) {
	this.programData = programData;
}
public ArrayList<ConsumerProgramStructureAcads> getSubjectsData() {
	return subjectsData;
}
public void setSubjectsData(ArrayList<ConsumerProgramStructureAcads> subjectsData) {
	this.subjectsData = subjectsData;
}


}
