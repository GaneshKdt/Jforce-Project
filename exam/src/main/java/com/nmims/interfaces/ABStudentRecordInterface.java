package com.nmims.interfaces;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.nmims.beans.ExamBookingExamBean;
import com.nmims.beans.StudentMarksBean;

public interface ABStudentRecordInterface {
	
	
	public List<StudentMarksBean>  getApplicableStudentForProject(StudentMarksBean bean, String commaSepratedSubject);
	
	public Set<String> getExamBookingDetailsByYearAndMonth(StudentMarksBean bean);
	
	public Set<String> getStudentMarksRecordsForCheckExists(StudentMarksBean bean, String commaSepratedSubject);
	
	public List<StudentMarksBean> checkingStudentInExamBookingAndMarks(List<StudentMarksBean> studentNotBookedProject, Set<String> examBookingDetails, Set<String> marksBean);
	
	public int insertOrUpdateAbsentRecordInDataBase(List<StudentMarksBean> finalStudentListForNotBooked);

	

}
