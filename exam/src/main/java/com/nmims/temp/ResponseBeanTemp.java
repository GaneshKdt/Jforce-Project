package com.nmims.temp;

import java.util.ArrayList;

import com.nmims.beans.ConsumerProgramStructureExam;

public class ResponseBeanTemp {

private ArrayList<ConsumerProgramStructureExam> programData;
private ArrayList<ConsumerProgramStructureExam> programStructureData;
private ArrayList<ConsumerProgramStructureExam> subjectsData;

private String status;




public ArrayList<ConsumerProgramStructureExam> getProgramStructureData() {
	return programStructureData;
}
public void setProgramStructureData(ArrayList<ConsumerProgramStructureExam> programStructureData) {
	this.programStructureData = programStructureData;
}
public ArrayList<ConsumerProgramStructureExam> getProgramsData() {
	return programData;
}
public void setProgramsData(ArrayList<ConsumerProgramStructureExam> programData) {
	this.programData = programData;
}
public String getStatus() {
	return status;
}
public void setStatus(String status) {
	this.status = status;
}
public ArrayList<ConsumerProgramStructureExam> getSubjectsData() {
	return subjectsData;
}
public void setSubjectsData(ArrayList<ConsumerProgramStructureExam> subjectsData) {
	this.subjectsData = subjectsData;
}



}
