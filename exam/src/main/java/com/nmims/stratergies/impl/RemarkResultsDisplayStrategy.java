/**
 * 
 */
package com.nmims.stratergies.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.RemarksGradeResultsBean;
import com.nmims.daos.RemarksGradeResultsDAO;
import com.nmims.stratergies.RemarkResultsDisplayStrategyInterface;

/**
 * @author vil_m
 *
 */
@Service("RemarkResultsDisplayStrategy")
public class RemarkResultsDisplayStrategy implements RemarkResultsDisplayStrategyInterface {
	
	@Autowired
	private RemarksGradeResultsDAO remarksGradeResultsDAO;
	
	public static final Logger logger = LoggerFactory.getLogger(RemarkResultsDisplayStrategy.class);

	@Override
	public List<RemarksGradeResultsBean> fetchStudentsResult(String sapid,
			Integer resultLive, String status, Integer statusPassFail, String activeFlag) {
		// TODO Auto-generated method stub
		logger.info("Entering RemarkResultsDisplayStrategy : fetchStudentsResult");
		List<RemarksGradeResultsBean> list1 = null;
		list1 = remarksGradeResultsDAO.fetchStudentsResult(sapid, resultLive, status, statusPassFail, activeFlag);
		return list1;
	}

	@Override
	public boolean checkResultsAvailable(String sapid, Integer resultLive,
			String status, Integer statusPassFail, Integer processedFlag, String activeFlag) {
		// TODO Auto-generated method stub
		logger.info("Entering RemarkResultsDisplayStrategy : checkResultsAvailable");
		boolean areResultsAvailable = Boolean.FALSE;
		areResultsAvailable = remarksGradeResultsDAO.checkResultsAvailable(sapid, resultLive, status,
				statusPassFail, processedFlag, activeFlag);
		return areResultsAvailable;
	}
}
