package com.nmims.daos;

import java.util.ArrayList;

import com.nmims.beans.AssignmentFileBean;

public interface RemovalOfFacultyFromRevaluationDao {
	
	
	public int removalOfactultyFromAssignmentSubmissionStageTwo(String examYear,String examMonth, String subject , String facultyid,String userId);
	
	public int removalOfactultyFromAssignmentSubmissionStageThree(String examYear,String examMonth, String subject , String facultyid,String userId);
	
	public int removalOfactultyFromAssignmentSubmissionStageFour(String examYear,String examMonth, String subject , String facultyid,String userId);
	
	public int removalOfactultyFromQAssignmentSubmissionStageTwo(String examYear,String examMonth, String subject , String facultyid,String userId);
	
	public int removalOfactultyFromQAssignmentSubmissionStageThree(String examYear,String examMonth, String subject , String facultyid,String userId);
	
	public int removalOfactultyFromQAssignmentSubmissionStageFour(String examYear,String examMonth, String subject , String facultyid,String userId);
	
	public ArrayList<AssignmentFileBean> SearchFacultyFromAllStages(String examYear ,String examMonth ,String subject ,String facultyId);

}
