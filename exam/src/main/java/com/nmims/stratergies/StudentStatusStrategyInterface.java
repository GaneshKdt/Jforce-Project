/**
 * 
 */
package com.nmims.stratergies;

import java.util.List;

import com.nmims.beans.RemarksGradeBean;

/**
 * @author vil_m
 *
 */
public interface StudentStatusStrategyInterface {
	
	public List<RemarksGradeBean> downloadAbsentStudents(RemarksGradeBean bean);
	
	public List<RemarksGradeBean> searchForAbsentStudents(RemarksGradeBean bean);
	
	public RemarksGradeBean moveAbsentStudents(RemarksGradeBean bean, String userId);
	
	public List<RemarksGradeBean> downloadCopyCaseStudents(RemarksGradeBean bean);
	
	/**
	 * Search Students marked Copycase from Assignments. 
	 * @param bean Year and Month populated.
	 * @return List of Bean with marks of Students marked Copycase.
	 */
	public List<RemarksGradeBean> searchForCopyCaseStudents(RemarksGradeBean bean);
	
	/**
	 * Move Students marked Copycase to Marks and other tables.
	 * @param bean bean Year and Month populated.
	 * @param userId user performing processing.
	 * @return Status Success/Failure with message.
	 */
	public RemarksGradeBean moveCopyCaseStudents(RemarksGradeBean bean, String userId);
	
	public List<RemarksGradeBean> searchForStudentMarksStatus(RemarksGradeBean bean);
	
	public Boolean updateForStudentMarksStatus(RemarksGradeBean bean, String userId);
	
	public List<RemarksGradeBean> searchStudentsForAssignments(final RemarksGradeBean bean);
	public List<RemarksGradeBean> downloadStudentsForAssignments(final RemarksGradeBean bean);

}
