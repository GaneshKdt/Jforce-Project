/**
 * 
 */
package com.nmims.interfaces;

import java.util.List;
import java.util.Map;

import com.nmims.beans.RemarksGradeBean;

/**
 * @author vil_m
 *
 */
public interface GradingTypeServiceInterface {
	
	public List<RemarksGradeBean> uploadMarksExcelFile(RemarksGradeBean databean, String userId);
	
	public List<RemarksGradeBean> displayMarksExcelFile(RemarksGradeBean databean);
	
	public List<RemarksGradeBean> searchForAbsentStudents(RemarksGradeBean bean);//AB
	public RemarksGradeBean moveAbsentStudents(RemarksGradeBean bean, String userId);//AB
	public List<RemarksGradeBean> searchForCopyCaseStudents(RemarksGradeBean bean);//CC
	public RemarksGradeBean moveCopyCaseStudents(RemarksGradeBean bean, String userId);//CC
	public List<RemarksGradeBean> searchForStudentMarksStatus(RemarksGradeBean bean);//RIA/NV
	public Boolean updateForStudentMarksStatus(RemarksGradeBean bean, String userId);//RIA/NV
	public List<RemarksGradeBean> downloadAbsentStudents(RemarksGradeBean bean);
	public List<RemarksGradeBean> downloadCopyCaseStudents(RemarksGradeBean bean);
	
	public Map<String, Integer> fetchStudentSummary(RemarksGradeBean bean);//PF Summary
	public Map<String, Integer> processStudentSummary(RemarksGradeBean bean, String userId);//PF Summary
	
	public Integer searchProcessStudentForGrade(RemarksGradeBean bean, String userId);//Absolute Grading
	
	
	public List<RemarksGradeBean> searchStudentsForTransfer(RemarksGradeBean bean);
	public List<RemarksGradeBean> downloadStudentsForTransfer(RemarksGradeBean bean);
	public Integer searchTransferStudents(RemarksGradeBean bean, String userId);
	
	public Boolean changeResultsLiveState(RemarksGradeBean bean, String userId);
	
	public List<RemarksGradeBean> searchResultsAsPassFailReport(RemarksGradeBean remarksGradeBean, Boolean countRequired);
	public List<RemarksGradeBean> downloadResultsAsPassFailReport(final RemarksGradeBean remarksGradeBean);
	
	public List<RemarksGradeBean> searchStudentsForAssignments(final RemarksGradeBean bean);
	public List<RemarksGradeBean> downloadStudentsForAssignments(final RemarksGradeBean bean);
}
