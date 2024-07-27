/**
 * 
 */
package com.nmims.stratergies.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.RemarksGradeBean;
import com.nmims.daos.RemarksGradeDAO;
import com.nmims.stratergies.ResultsDisplayStrategyInterface;

/**
 * @author vil_m
 *
 */

@Service("resultsDisplayStrategy")
public class ResultsDisplayStrategy implements ResultsDisplayStrategyInterface {
	
	@Autowired
	private RemarksGradeDAO remarksGradeDAO;

	public static final Logger logger = LoggerFactory.getLogger("checkListRG");

	public Boolean changeResultsLiveState(RemarksGradeBean bean, String userId) {
		logger.info("Entering ResultsDisplayStrategy : changeResultsLiveState");
		Boolean isSuccess = Boolean.FALSE;
		Integer flagRL = -1;
		Set<String> statusSet = null;
		
		if(RemarksGradeBean.ASSIGNMENT_RESULT_LIVE.equals(bean.getAssignmentMarksLive())) {
			flagRL = RemarksGradeBean.RESULT_LIVE;
		} else if(RemarksGradeBean.ASSIGNMENT_RESULT_NOTLIVE.equals(bean.getAssignmentMarksLive())) {
			flagRL = RemarksGradeBean.RESULT_NOT_LIVE;
		}
		
		statusSet = new HashSet<String>();
		statusSet.add(RemarksGradeBean.ATTEMPTED);
		statusSet.add(RemarksGradeBean.CC);
		statusSet.add(RemarksGradeBean.ABSENT);
		
		isSuccess = remarksGradeDAO.changeResultsLiveState(bean, statusSet,
				RemarksGradeBean.STATUS_RESET_PASSFAIL, RemarksGradeBean.PROCESSED, flagRL,
				RemarksGradeBean.ROWS_NOT_DELETED, userId);
		
		statusSet.clear();
		statusSet = null;
		
		return isSuccess;
	}
	
	public List<RemarksGradeBean> searchResultsAsPassFailReport(final RemarksGradeBean remarksGradeBean,
			final Boolean countRequired) {
		logger.info("Entering ResultsDisplayStrategy : searchResultsAsPassFailReport");
		List<RemarksGradeBean> list = null;
		list = remarksGradeDAO.searchResultsAsPassFailReport(remarksGradeBean, RemarksGradeBean.ROWS_NOT_DELETED,
				countRequired);

		return list;
	}
	
	public List<RemarksGradeBean> downloadResultsAsPassFailReport(final RemarksGradeBean remarksGradeBean) {
		logger.info("Entering ResultsDisplayStrategy : downloadResultsAsPassFailReport");
		List<RemarksGradeBean> list = null;
		list = remarksGradeDAO.searchResultsAsPassFailReport(remarksGradeBean, RemarksGradeBean.ROWS_NOT_DELETED,
				Boolean.FALSE);

		return list;
	}
}
