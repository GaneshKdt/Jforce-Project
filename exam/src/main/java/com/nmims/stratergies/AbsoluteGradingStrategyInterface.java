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
public interface AbsoluteGradingStrategyInterface {
	
	public Integer searchProcessStudentForGrade(final RemarksGradeBean bean, final String userId);
	
	public List<RemarksGradeBean> searchStudentInAbsentForGrading(RemarksGradeBean bean);
	
	public List<RemarksGradeBean> searchStudentInCopyCaseForGrading(RemarksGradeBean bean);

	public List<RemarksGradeBean> searchStudentForGrading(RemarksGradeBean bean);

	/**
	 * Grading done for Students with status ATTEMPTED only.
	 * @param list
	 */
	public void assignStudentsGrade(List<RemarksGradeBean> list);

	public boolean saveStudentsGrade(final List<RemarksGradeBean> list, final String userId);
}
