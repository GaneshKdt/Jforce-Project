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
public interface PassFailTransferStrategyInterface {
	public List<RemarksGradeBean> searchStudentsForTransfer(RemarksGradeBean bean);
	
	public List<RemarksGradeBean> downloadStudentsForTransfer(RemarksGradeBean bean);
	
	public Integer searchTransferStudents(RemarksGradeBean bean, String userId);
	
	/*public List<RemarksGradeBean> searchStudentsGradedForTransfer(RemarksGradeBean bean);
	
	public List<RemarksGradeBean> downloadStudentsGradedForTransfer(RemarksGradeBean bean);
	
	public Integer searchTransferStudentsGraded(RemarksGradeBean bean, String userId);*/
}
