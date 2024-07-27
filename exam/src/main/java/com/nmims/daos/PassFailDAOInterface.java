package com.nmims.daos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.nmims.beans.PassFailExamBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentMarksBean;
/**
 * 
 * @author shivam.pandey.EXT
 *
 */
public interface PassFailDAOInterface {
	
	//To Get Result Declared Date List By Year and Month
	public List<PassFailExamBean> getResultDeclaredDateByYearAndMonth(ArrayList<String> listYearAndMonth)throws Exception;

	public List<StudentMarksBean> getPendingRecordsForOnlineOfflineProject(StudentExamBean searchBean);

	public List<StudentMarksBean> getAbsentRecord(StudentExamBean searchBean);

	public List<StudentMarksBean> getPendingListForNVRIA(StudentExamBean searchBean);

	public List<StudentMarksBean> getPendingListForANS(StudentExamBean searchBean);

	public List<StudentMarksBean> getPendingRecordsForOnlineOfflineWritten(StudentExamBean searchBean);

	public List<StudentMarksBean> getPendingRecordsForOnlineOfflineAssignment(StudentExamBean searchBean);

	public List<StudentMarksBean> getPendingRecordsForOnlineOffline(StudentExamBean searchBean);

	public List<StudentExamBean> getStudentList(StudentExamBean searchBean);

	public int getPendingCountForAbsent(StudentExamBean searchBean);

	public int getPendingCountForNVRIA(StudentExamBean searchBean);

	public int getPendingCountForANS(StudentExamBean searchBean);

	public List<StudentMarksBean> getStudentNotBookedStudent(StudentExamBean searchBean);

	public List<StudentMarksBean> getProjectAbsentList(StudentExamBean searchBean);
	
}
