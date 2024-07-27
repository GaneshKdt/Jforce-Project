package com.nmims.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StudentCourseMappingBean 
{
	private ArrayList<String> currentSemSubjects;
	
	private ArrayList<String> failedSubjectList;
	
	private ArrayList<String> listOfApplicableSUbjects;

	public ArrayList<String> getCurrentSemSubjects() {
		return currentSemSubjects;
	}

	public void setCurrentSemSubjects(ArrayList<String> currentSemSubjects) {
		this.currentSemSubjects = currentSemSubjects;
	}

	public ArrayList<String> getFailedSubjectList() {
		return failedSubjectList;
	}

	public void setFailedSubjectList(ArrayList<String> failedSubjectList) {
		this.failedSubjectList = failedSubjectList;
	}

	public ArrayList<String> getListOfApplicableSUbjects() {
		return listOfApplicableSUbjects;
	}

	public void setListOfApplicableSUbjects(ArrayList<String> listOfApplicableSUbjects) {
		this.listOfApplicableSUbjects = listOfApplicableSUbjects;
	}
	

}
