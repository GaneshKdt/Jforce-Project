package com.nmims.services;

import java.util.ArrayList;

import com.nmims.beans.AssignmentFileBean;
import com.nmims.beans.RemovalOfFacultyFromAllStageOfRevaluationBean;

public interface RemovalOfFacultyFromRevaluationServiceInterface {
	
	public ArrayList<AssignmentFileBean> searchFacultyFromAllStagesOfRevaluation(String examYear, String examMonth ,String subject,String facultyId) ;
	
	public RemovalOfFacultyFromAllStageOfRevaluationBean removeFacultyFromAllStagesOfRevaluation(String examYear ,String examMonth , String subject ,String facultyId,String userId);
	
	public ArrayList<String> getAllFaculties();
	
	public ArrayList<String> getActiveSubjects();
}
