package com.nmims.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.nmims.beans.PassFailExamBean;
import com.nmims.beans.ProgramSubjectMappingExamBean;
import com.nmims.beans.StudentExamBean;

import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.exceptions.NoRecordFoundException;
/**
 * 
 * @author shivam.pandey.EXT
 *
 */
public interface PassFailService {
	// To add result Declared Date in studentMarksList and return updated list which
	// included result declared date
	public List<PassFailExamBean> setResultDecDateInPassFailList(List<PassFailExamBean> studentMarksList);

	public ArrayList<PassFailExamBean> executePassFailLogicForIndividualStudent(HashMap<String, ArrayList> keysMap,
			HashMap<String, StudentExamBean> studentsMap,
			HashMap<String, ProgramSubjectMappingExamBean> programSubjectPassingConfigurationMap,
			HashMap<String, Integer> programSubjectPassScoreMap);

	public Integer transferRecordsFromStagingToPassFail(String year, String month) throws NoRecordFoundException;
	
	public void executeAssignmentLogicPostPassFailProcess(String examYear, String examMonth);
	
	public HashMap<String, Integer> getPendingCountForOnlineOfflineProject(StudentExamBean searchBean, HashMap<String, StudentExamBean> studentList);
	
	public HashMap<String, Integer> getPendingCountForOnlineOfflineWritten(StudentExamBean searchBean, HashMap<String, StudentExamBean> studentList);

	public HashMap<String, Integer> getPendingCountForOnlineOfflineAssignment(StudentExamBean searchBean,HashMap<String, StudentExamBean> studentList);
	
	public int getPendingRecordsForAbsent(StudentExamBean searchBean);

	public int getPendingRecordsForNVRIA(StudentExamBean searchBean);

	public int getPendingRecordsForANS(StudentExamBean searchBean);

	public HashMap<String, Integer> getPendingCountForOnlineOffline(StudentExamBean searchBean,HashMap<String,StudentExamBean> student);
	
	public List<StudentMarksBean> getstudentProjectList(StudentExamBean searchBean, String recordType);
	
	public List<StudentMarksBean> getAbsentStundentRecord(StudentExamBean searchBean);

	public List<StudentMarksBean> getPendingCountForNVRIA(StudentExamBean searchBean);

	public List<StudentMarksBean> getPendingListForANS(StudentExamBean searchBean);

	public List<StudentMarksBean> getWrittenScoreRecords(StudentExamBean searchBean,String recordType);

	public List<StudentMarksBean> getAssignmentSubmittedRecords(StudentExamBean searchBean,String recordType);

	public List<StudentMarksBean> getRecordsForOnlineOffline(StudentExamBean searchBean,String recordType);

	public HashMap<String, StudentExamBean> getAllStudents(StudentExamBean searchBean);

	public List<StudentMarksBean> getStudentNotBookedStudent(StudentExamBean searchBean);

	public List<StudentMarksBean> getStudentProjectAbsentList(StudentExamBean searchBean);

	public Map<String, Integer> getPassFailTranferReportCount(String year, String month);

	public List<PassFailExamBean> getPassFailReportForType(String type, String year, String month);

	public List<PassFailExamBean> filterApplyGraceStudents(List<PassFailExamBean> passFailStudentList);

	public Map<String,String> getLatestYear(List<PassFailExamBean> passFailStudentList) throws Exception;

	//public HashMap<String, StudentExamBean> getAllStudents(StudentExamBean searchBean);
}

