/**
 * 
 */
package com.nmims.stratergies;

import java.util.List;
import java.util.Map;

import com.nmims.beans.RemarksGradeBean;

/**
 * @author vil_m
 *
 */
public interface PassFailStrategyInterface {
	
	public static final String EXAM_MODE_ONLINE = "ONLINE";
	public static final String EXAM_MODE_OFFLINE = "OFFLINE";
	public static final String MSG_PASS = "Total Marks more than Pass Score.";
	public static final String MSG_LESSER_THAN_PASSSCORE = "Total Marks less than Pass Score.";

	public Integer findStudentsAttempted(RemarksGradeBean bean, String examMode);

	public Integer findStudentsWithStatusNV(RemarksGradeBean bean, String examMode);

	public Integer findStudentsWithStatusRIA(RemarksGradeBean bean, String examMode);
	
	public Integer findStudentsAbsent(RemarksGradeBean bean, String examMode);
	
	public Integer findStudentsInCopyCase(RemarksGradeBean bean, String examMode);
	
	/*public void findTotalStudents(RemarksGradeBean bean, String examMode);

	public void findStudentsWithGrace(RemarksGradeBean bean, String examMode);*/

	public Map<String, Integer> fetchSummary(RemarksGradeBean bean, String examMode);
	
	public Map<String, Integer> processSummary(RemarksGradeBean bean, String examMode, String userId);
	
	public Integer processStudentsInAbsent(RemarksGradeBean bean, String examMode, String userId);
	
	public Integer processStudentsInCopyCase(RemarksGradeBean bean, String examMode, String userId);
	
	public Integer processStudentsAttempted(RemarksGradeBean bean, String examMode, String userId);
	
	public void evaluateStudentsAttempted(List<RemarksGradeBean> list, String examMode);
	
	public void evaluateStudentsInCopyCase(List<RemarksGradeBean> list, String examMode);
	
	public void evaluateStudentsInAbsent(List<RemarksGradeBean> list, String examMode);
	
}
