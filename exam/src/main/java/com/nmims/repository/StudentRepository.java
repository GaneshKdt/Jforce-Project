package com.nmims.repository;

import java.util.List;

import com.nmims.beans.StudentExamBean;

public class StudentRepository {
	 
	public StudentExamBean getSingleStudentsData(String sapid) {
		return null;
	}
	
	public StudentExamBean findFirstBySapidOrderBySemDesc(String sapid) {
		return null;
	}
	
	public String getStudentType(String sapid, String timeboundId) {
		return null;
	}

	public List<StudentExamBean> getDistinctSapidsForResultProcessingYearAndMonth(String year, String month) {
		return null;
	}

	public List<StudentExamBean> getDistinctSapidsFromPassFail() {
		return null;
	}
	
	List<StudentExamBean> getAllStudentsByTimeboundId(Integer timeboundId) {
		return null;
	}	
}
