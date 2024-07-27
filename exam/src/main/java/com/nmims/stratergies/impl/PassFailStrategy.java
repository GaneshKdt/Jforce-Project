/**
 * 
 */
package com.nmims.stratergies.impl;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.RemarksGradeBean;
import com.nmims.daos.RemarksGradeDAO;
import com.nmims.stratergies.PassFailStrategyInterface;

/**
 * @author vil_m
 *
 */
@Service("passFailStrategy")
public class PassFailStrategy implements PassFailStrategyInterface {
	
	@Autowired
	private RemarksGradeDAO remarksGradeDAO;

	public static final Logger logger = LoggerFactory.getLogger("checkListRG");

	@Override
	public Map<String, Integer> fetchSummary(RemarksGradeBean bean, String examMode) {
		// TODO Auto-generated method stub
		logger.info("Entering PassFailStrategy : fetchSummary");
		LinkedHashMap<String, Integer> summaryMap = null;
		Integer countOfStudentsAttempted = null;
		Integer countOfStudentsRIA = null;
		Integer countOfStudentsNV = null;
		Integer countOfStudentsAbsent = null;
		Integer countOfStudentsCopyCases = null;
		countOfStudentsAttempted = findStudentsAttempted(bean, examMode);
		countOfStudentsNV = findStudentsWithStatusNV(bean, examMode);
		countOfStudentsRIA = findStudentsWithStatusRIA(bean, examMode);
		countOfStudentsAbsent = findStudentsAbsent(bean, examMode);
		countOfStudentsCopyCases = findStudentsInCopyCase(bean, examMode);
		
		summaryMap = new LinkedHashMap<String, Integer>();
		summaryMap.put("Absent Student Records", countOfStudentsAbsent);
		summaryMap.put("NV / RIA", (countOfStudentsNV + countOfStudentsRIA));
		//summaryMap.put("Assignment Submitted Records Online", 0);
		summaryMap.put("Copy Cases", countOfStudentsCopyCases);
		summaryMap.put("Attempted", countOfStudentsAttempted);
		return summaryMap;
	}

	@Override
	public Integer findStudentsAttempted(RemarksGradeBean bean, String examMode) {
		// TODO Auto-generated method stub
		logger.info("Entering PassFailStrategy : findStudentsAttempted");
		Integer totalRows = null;
		totalRows = remarksGradeDAO.findStudents(bean, RemarksGradeBean.ATTEMPTED, RemarksGradeBean.ROWS_NOT_DELETED, RemarksGradeBean.NOTPROCESSED);
		return totalRows;
	}

	@Override
	public Integer findStudentsWithStatusNV(RemarksGradeBean bean, String examMode) {
		// TODO Auto-generated method stub
		logger.info("Entering PassFailStrategy : findStudentsWithStatusNV");
		Integer totalRows = null;
		totalRows = remarksGradeDAO.findStudents(bean, RemarksGradeBean.NV, RemarksGradeBean.ROWS_NOT_DELETED, RemarksGradeBean.NOTPROCESSED);
		return totalRows;
	}

	@Override
	public Integer findStudentsWithStatusRIA(RemarksGradeBean bean, String examMode) {
		// TODO Auto-generated method stub
		logger.info("Entering PassFailStrategy : findStudentsWithStatusRIA");
		Integer totalRows = null;
		totalRows = remarksGradeDAO.findStudents(bean, RemarksGradeBean.RIA, RemarksGradeBean.ROWS_NOT_DELETED, RemarksGradeBean.NOTPROCESSED);
		return totalRows;
	}

	@Override
	public Integer findStudentsAbsent(RemarksGradeBean bean, String examMode) {
		// TODO Auto-generated method stub
		logger.info("Entering PassFailStrategy : findStudentsAbsent");
		Integer totalRows = null;
		totalRows = remarksGradeDAO.findStudents(bean, RemarksGradeBean.ABSENT, RemarksGradeBean.ROWS_NOT_DELETED, RemarksGradeBean.NOTPROCESSED);
		return totalRows;
	}

	@Override
	public Integer findStudentsInCopyCase(RemarksGradeBean bean, String examMode) {
		// TODO Auto-generated method stub
		logger.info("Entering PassFailStrategy : findStudentsInCopyCase");
		Integer totalRows = null;
		totalRows = remarksGradeDAO.findStudents(bean, RemarksGradeBean.CC, RemarksGradeBean.ROWS_NOT_DELETED, RemarksGradeBean.NOTPROCESSED);
		return totalRows;
	}

	@Override
	public Map<String, Integer> processSummary(RemarksGradeBean bean, String examMode, String userId) {
		// TODO Auto-generated method stub
		logger.info("Entering PassFailStrategy : processSummary");
		LinkedHashMap<String, Integer> summaryMap = null;
		Integer processedStudentsAttempted = null;
		Integer processedStudentsCopyCase = null;
		Integer processedStudentsAbsent = null;
		summaryMap = new LinkedHashMap<String, Integer>();
		processedStudentsAttempted = processStudentsAttempted(bean, examMode, userId);
		processedStudentsCopyCase = processStudentsInCopyCase(bean, examMode, userId);
		processedStudentsAbsent = processStudentsInAbsent(bean, examMode, userId);
		summaryMap.put("Absent", processedStudentsAbsent);
		summaryMap.put("CopyCase", processedStudentsCopyCase);
		summaryMap.put("Attempted", processedStudentsAttempted);
		return summaryMap;
	}

	@Override
	public Integer processStudentsAttempted(RemarksGradeBean bean, String examMode, String userId) {
		// TODO Auto-generated method stub
		logger.info("Entering PassFailStrategy : processStudentsAttempted");
		boolean isProcessed = Boolean.FALSE;
		Integer totalProcessedRows = 0;
		List<RemarksGradeBean> list = null;
		list = remarksGradeDAO.fetchStudents(bean, RemarksGradeBean.ATTEMPTED, RemarksGradeBean.ROWS_NOT_DELETED,
				RemarksGradeBean.NOTPROCESSED);
		if(null != list && !list.isEmpty()) {
			evaluateStudentsAttempted(list, examMode);
			isProcessed = remarksGradeDAO.markPassFail(list, RemarksGradeBean.ATTEMPTED, RemarksGradeBean.PROCESSED,
					RemarksGradeBean.RESULT_NOT_LIVE, userId);
			if(isProcessed) {
				totalProcessedRows = list.size();
			}
		}
		return totalProcessedRows;
	}

	@Override
	public Integer processStudentsInCopyCase(RemarksGradeBean bean, String examMode, String userId) {
		// TODO Auto-generated method stub
		logger.info("Entering PassFailStrategy : processStudentsInCopyCase");
		boolean isProcessed = Boolean.FALSE;
		Integer totalProcessedRowsCC = 0;
		List<RemarksGradeBean> list = null;
		list = remarksGradeDAO.fetchStudents(bean, RemarksGradeBean.CC, RemarksGradeBean.ROWS_NOT_DELETED,
				RemarksGradeBean.NOTPROCESSED);
		if(null != list && !list.isEmpty()) {
			evaluateStudentsInCopyCase(list, examMode);

			isProcessed = remarksGradeDAO.markPassFailForCopyCase(list, RemarksGradeBean.CC, RemarksGradeBean.PROCESSED,
					RemarksGradeBean.RESULT_NOT_LIVE, userId);
			if(isProcessed) {
				totalProcessedRowsCC = list.size();
			}
		}
		return totalProcessedRowsCC;
	}

	@Override
	public Integer processStudentsInAbsent(RemarksGradeBean bean, String examMode, String userId) {
		// TODO Auto-generated method stub
		logger.info("Entering PassFailStrategy : processStudentsInAbsent");
		boolean isProcessed = Boolean.FALSE;
		Integer totalProcessedRowsAB = 0;
		List<RemarksGradeBean> list = null;
		list = remarksGradeDAO.fetchStudents(bean, RemarksGradeBean.ABSENT, RemarksGradeBean.ROWS_NOT_DELETED,
				RemarksGradeBean.NOTPROCESSED);
		if(null != list && !list.isEmpty()) {
			evaluateStudentsInAbsent(list, examMode);

			isProcessed = remarksGradeDAO.markPassFailForAbsent(list, RemarksGradeBean.ABSENT, RemarksGradeBean.PROCESSED,
					RemarksGradeBean.RESULT_NOT_LIVE, userId);
			if(isProcessed) {
				totalProcessedRowsAB = list.size();
			}
		}
		return totalProcessedRowsAB;
	}

	@Override
	public void evaluateStudentsAttempted(List<RemarksGradeBean> list, String examMode) {
		// TODO Auto-generated method stub
		logger.info("Entering PassFailStrategy : evaluateStudentsAttempted");
		Integer passScore = null;
		Integer scoreTotal = null;
		//Integer scoreIA = null;
		//Integer graceMarks = null;
		RemarksGradeBean bean = null;
		Map<Integer, Integer> passScoreMap = null;
		
		passScoreMap = populatePassScores(list);

		for(int i = 0; i < list.size(); i++) {
			bean = list.get(i);
			passScore = passScoreMap.get(bean.getProgramSemSubjectId());
			
			if(null != bean.getScoreIA() && bean.getScoreIA() > 0) {
				scoreTotal = bean.getScoreIA();
			} else {
				scoreTotal = 0;
			}
			//if(null != bean.getGraceMarks() && bean.getGraceMarks() > 0) {
				//scoreTotal += bean.getGraceMarks();
			//}
			
			if(scoreTotal >= passScore) {
				bean.setScoreTotal(scoreTotal);
				bean.setPass(Boolean.TRUE);
				bean.setFailReason(PassFailStrategyInterface.MSG_PASS);
			} else {
				bean.setScoreTotal(scoreTotal);
				bean.setPass(Boolean.FALSE);
				bean.setFailReason(PassFailStrategyInterface.MSG_LESSER_THAN_PASSSCORE);
			}
		}
	}
	
	Map<Integer, Integer> populatePassScores(List<RemarksGradeBean> list) {
		logger.info("Entering PassFailStrategy : populatePassScores");
		Set<Integer> programSemSubjectIdSet = null;
		Map<Integer, Integer> passScoreMap = null;
		RemarksGradeBean bean = null;
		
		programSemSubjectIdSet = new HashSet<Integer>();
		for(int h = 0; h < list.size(); h++) {
			bean = list.get(h);
			programSemSubjectIdSet.add(bean.getProgramSemSubjectId());
		}
		passScoreMap = remarksGradeDAO.fetchPassScores(programSemSubjectIdSet, RemarksGradeBean.ROWS_NOT_DELETED);
		programSemSubjectIdSet.clear();
		programSemSubjectIdSet = null;
		
		return passScoreMap;
	}

	@Override
	public void evaluateStudentsInCopyCase(List<RemarksGradeBean> list, String examMode) {
		// TODO Auto-generated method stub
		logger.info("Entering PassFailStrategy : evaluateStudentsInCopyCase");
		
		final Integer scoreTotal = 0;
		RemarksGradeBean bean = null;
		
		for(int i = 0; i < list.size(); i++) {
			bean = list.get(i);
			bean.setScoreTotal(scoreTotal);
			bean.setPass(Boolean.FALSE);
			bean.setRemarks(RemarksGradeBean.REMARK_CC);
		}
	}

	@Override
	public void evaluateStudentsInAbsent(List<RemarksGradeBean> list, String examMode) {
		// TODO Auto-generated method stub
		logger.info("Entering PassFailStrategy : evaluateStudentsInAbsent");
		
		//final Integer scoreTotal = 0;
		RemarksGradeBean bean = null;
		
		for(int i = 0; i < list.size(); i++) {
			bean = list.get(i);
			//bean.setScoreTotal(scoreTotal);
			//bean.setPass(Boolean.FALSE);
			bean.setRemarks(RemarksGradeBean.REMARK_ANS);
		}
	}

}
