package com.nmims.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExamSelectSubjectBeanAPIResponse  implements Serializable  {
int	totalExamFees;
ArrayList<String> subjects;
List<TimetableBean> timeTableList;
ExamCenterBean examCenter;
int noOfSubjects;
Map<String, List<ExamCenterBean>> centerSubjectMapping;

private String status;
private String error;


 
public Map<String, List<ExamCenterBean>> getCenterSubjectMapping() {
	return centerSubjectMapping;
}

public void setCenterSubjectMapping(Map<String, List<ExamCenterBean>> centerSubjectMapping) {
	this.centerSubjectMapping = centerSubjectMapping;
}

public String getStatus() {
	return status;
}

public void setStatus(String status) {
	this.status = status;
}

public String getError() {
	return error;
}

public void setError(String error) {
	this.error = error;
}


public int getNoOfSubjects() {
	return noOfSubjects;
}

public void setNoOfSubjects(int noOfSubjects) {
	this.noOfSubjects = noOfSubjects;
}

public ExamCenterBean getExamCenter() {
	return examCenter;
}

public void setExamCenter(ExamCenterBean examCenter) {
	this.examCenter = examCenter;
}

public List<TimetableBean> getTimeTableList() {
	return timeTableList;
}

public void setTimeTableList(List<TimetableBean> timeTableList) {
	this.timeTableList = timeTableList;
}

public ArrayList<String> getSubjects() {
	return subjects;
}

public void setSubjects(ArrayList<String> subjects) {
	this.subjects = subjects;
}

public int getTotalExamFees() {
	return totalExamFees;
}

public void setTotalExamFees(int totalExamFees) {
	this.totalExamFees = totalExamFees;
}
}
