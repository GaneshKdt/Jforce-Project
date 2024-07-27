package com.nmims.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StudentCourseMappingBean 
{	
	private HashMap<String,String> currentSemSubjectsmap;
	
	private HashMap<String,String> failedSubjectListsmap;
	
	private HashMap<String,String> listOfApplicableSUbjectssmap;
	
	private List<Integer> subjectCodeId;
	
	public boolean getIsRedis() {
		return isRedis;
	}

	public void setIsRedis(boolean isRedis) {
		this.isRedis = isRedis;
	}

	private boolean isRedis;


	public HashMap<String, String> getCurrentSemSubjectsmap() {
		return currentSemSubjectsmap;
	}

	public void setCurrentSemSubjectsmap(HashMap<String, String> currentSemSubjectsmap) {
		this.currentSemSubjectsmap = currentSemSubjectsmap;
	}

	public HashMap<String, String> getFailedSubjectListsmap() {
		return failedSubjectListsmap;
	}

	public void setFailedSubjectListsmap(HashMap<String, String> failedSubjectListsmap) {
		this.failedSubjectListsmap = failedSubjectListsmap;
	}

	public HashMap<String, String> getListOfApplicableSUbjectssmap() {
		return listOfApplicableSUbjectssmap;
	}

	public void setListOfApplicableSUbjectssmap(HashMap<String, String> listOfApplicableSUbjectssmap) {
		this.listOfApplicableSUbjectssmap = listOfApplicableSUbjectssmap;
	}

	public List<Integer> getSubjectCodeId() {
		return subjectCodeId;
	}

	public void setSubjectCodeId(List<Integer> subjectCodeId) {
		this.subjectCodeId = subjectCodeId;
	}

	public void setRedis(boolean isRedis) {
		this.isRedis = isRedis;
	}
	
	

}
