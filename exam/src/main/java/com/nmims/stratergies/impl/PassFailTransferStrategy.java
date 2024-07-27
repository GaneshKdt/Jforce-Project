/**
 * 
 */
package com.nmims.stratergies.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.RemarksGradeBean;
import com.nmims.daos.RemarksGradeDAO;
import com.nmims.stratergies.PassFailTransferStrategyInterface;

/**
 * @author vil_m
 *
 */

@Service("passFailTransferStrategy")
public class PassFailTransferStrategy implements PassFailTransferStrategyInterface {

	@Autowired
	private RemarksGradeDAO remarksGradeDAO;

	public static final Logger logger = LoggerFactory.getLogger("checkListRG");
	
	@Override
	public List<RemarksGradeBean> searchStudentsForTransfer(RemarksGradeBean bean) {
		// TODO Auto-generated method stub
		logger.info("Entering PassFailTransferStrategy : searchStudentsForTransfer");
		List<RemarksGradeBean> list1 = null;
		list1 = searchStudents(bean);
		return list1;
	}
	
	protected List<RemarksGradeBean> searchStudents(RemarksGradeBean bean) {
		logger.info("Entering PassFailTransferStrategy : searchStudents");
		
		List<RemarksGradeBean> list1 = null;
		List<RemarksGradeBean> list2 = null;
		List<RemarksGradeBean> list3 = null;
		List<RemarksGradeBean> listMain = null;
		
		list1 = remarksGradeDAO.searchStudentsAfterGrading(bean, RemarksGradeBean.ATTEMPTED,
				RemarksGradeBean.STATUS_RESET_PASSFAIL, RemarksGradeBean.ROWS_NOT_DELETED);
		if(null != list1 && !list1.isEmpty()) {
			listMain = list1;
		} else {
			listMain = new ArrayList<RemarksGradeBean>();
		}
		list2 = remarksGradeDAO.searchStudentsAfterGrading(bean, RemarksGradeBean.CC,
				RemarksGradeBean.STATUS_RESET_PASSFAIL, RemarksGradeBean.ROWS_NOT_DELETED);
		listMain.addAll(list2);
		
		//no use passing RemarksGradeBean.STATUS_RESET_PASSFAIL
		list3 = remarksGradeDAO.searchStudentsAfterGrading(bean, RemarksGradeBean.ABSENT,
				RemarksGradeBean.STATUS_RESET_PASSFAIL, RemarksGradeBean.ROWS_NOT_DELETED);
		listMain.addAll(list3);
		
		return listMain;
	}

	@Override
	public List<RemarksGradeBean> downloadStudentsForTransfer(RemarksGradeBean bean) {
		// TODO Auto-generated method stub
		logger.info("Entering PassFailTransferStrategy : downloadStudentsForTransfer");
		List<RemarksGradeBean> list1 = null;
		list1 = searchStudents(bean);
		return list1;
	}

	@Override
	public Integer searchTransferStudents(RemarksGradeBean bean, String userId) {
		// TODO Auto-generated method stub
		logger.info("Entering PassFailTransferStrategy : searchTransferStudents");
		Integer rowsSaved = 0;
		List<RemarksGradeBean> list1 = null;
		List<RemarksGradeBean> list2 = null;
		List<RemarksGradeBean> list3 = null;
		Map<String, List<RemarksGradeBean>> dataMap = null;
		
		logger.info("RemarksGradeDAO : searchTransferStudents : fetch ATTEMPTED.");
		list1 = remarksGradeDAO.searchStudentsAfterGrading(bean, RemarksGradeBean.ATTEMPTED,
				RemarksGradeBean.STATUS_RESET_PASSFAIL, RemarksGradeBean.ROWS_NOT_DELETED);
		
		logger.info("RemarksGradeDAO : searchTransferStudents : fetch CC.");
		list2 = remarksGradeDAO.searchStudentsAfterGrading(bean, RemarksGradeBean.CC,
				RemarksGradeBean.STATUS_RESET_PASSFAIL, RemarksGradeBean.ROWS_NOT_DELETED);
		
		logger.info("RemarksGradeDAO : searchTransferStudents : fetch AB.");
		//no use passing RemarksGradeBean.STATUS_RESET_PASSFAIL
		list3 = remarksGradeDAO.searchStudentsAfterGrading(bean, RemarksGradeBean.ABSENT,
				RemarksGradeBean.STATUS_RESET_PASSFAIL, RemarksGradeBean.ROWS_NOT_DELETED);
		
		dataMap = new HashMap<String, List<RemarksGradeBean>>();
		dataMap.put(RemarksGradeBean.ATTEMPTED, list1);
		dataMap.put(RemarksGradeBean.CC, list2);
		dataMap.put(RemarksGradeBean.ABSENT, list3);
		
		rowsSaved = remarksGradeDAO.transferPassFail(bean, dataMap, RemarksGradeBean.RESULT_NOT_LIVE, RemarksGradeBean.ROWS_NOT_DELETED, userId);
		
		dataMap.clear();
		dataMap = null;
		return rowsSaved;
	}
	
	/*@Deprecated
	@Override
	public List<RemarksGradeBean> searchStudentsGradedForTransfer(RemarksGradeBean bean) {
		// TODO Auto-generated method stub
		logger.info("Entering PassFailStrategy : searchStudentsGradedForTransfer");
		List<RemarksGradeBean> list1 = null;
		//list1 = remarksGradeDAO.searchStudentsGraded(bean, RemarksGradeBean.ATTEMPTED,
		//		RemarksGradeBean.STATUS_RESET_PASSFAIL, RemarksGradeBean.ROWS_NOT_DELETED);
		
		list1 = searchStudentsForTransfer(bean);
				
		return list1;
	}
	
	@Deprecated
	protected List<RemarksGradeBean> searchStudentsForTransfer(RemarksGradeBean bean) {
		logger.info("Entering PassFailStrategy : searchStudentsForTransfer");
		
		List<RemarksGradeBean> list1 = null;
		List<RemarksGradeBean> list2 = null;
		List<RemarksGradeBean> listMain = null;
		
		list1 = remarksGradeDAO.searchStudentsGraded(bean, RemarksGradeBean.ATTEMPTED,
				RemarksGradeBean.STATUS_RESET_PASSFAIL, RemarksGradeBean.ROWS_NOT_DELETED);
		if(null != list1 && !list1.isEmpty()) {
			listMain = list1;
		} else {
			listMain = new ArrayList<RemarksGradeBean>();
		}
		list2 = remarksGradeDAO.searchStudentsNotGraded(bean, RemarksGradeBean.CC, RemarksGradeBean.ROWS_NOT_DELETED);
		listMain.addAll(list2);
		
		return listMain;
	}
	
	@Deprecated
	@Override
	public List<RemarksGradeBean> downloadStudentsGradedForTransfer(RemarksGradeBean bean) {
		// TODO Auto-generated method stub
		logger.info("Entering PassFailStrategy : downloadStudentsGradedForTransfer");
		List<RemarksGradeBean> list1 = null;
		//list1 = remarksGradeDAO.searchStudentsGraded(bean, RemarksGradeBean.ATTEMPTED,
		//		RemarksGradeBean.STATUS_RESET_PASSFAIL, RemarksGradeBean.ROWS_NOT_DELETED);
		
		list1 = searchStudentsForTransfer(bean);
		return list1;
	}
	
	@Deprecated
	@Override
	public Integer searchTransferStudentsGraded(RemarksGradeBean bean, String userId) {
		// TODO Auto-generated method stub
		logger.info("Entering PassFailStrategy : searchTransferStudentsGraded");
		Integer rowsSaved = 0;
		List<RemarksGradeBean> list1 = null;
		List<RemarksGradeBean> list2 = null;
		Map<String, List<RemarksGradeBean>> dataMap = null;
		
		logger.info("RemarksGradeDAO : searchTransferStudentsGraded : fetch ATTEMPTED.");
		list1 = remarksGradeDAO.searchStudentsGraded(bean, RemarksGradeBean.ATTEMPTED, RemarksGradeBean.STATUS_RESET_PASSFAIL, RemarksGradeBean.ROWS_NOT_DELETED);
		
		logger.info("RemarksGradeDAO : searchTransferStudentsGraded : fetch NON_ATTEMPTED.");
		list2 = remarksGradeDAO.searchStudentsNotGraded(bean, RemarksGradeBean.CC, RemarksGradeBean.ROWS_NOT_DELETED);
		
		dataMap = new HashMap<String, List<RemarksGradeBean>>();
		dataMap.put(RemarksGradeBean.ATTEMPTED, list1);
		dataMap.put("NON_ATTEMPTED", list2);
		
		rowsSaved = remarksGradeDAO.transferPassFail(bean, dataMap, RemarksGradeBean.RESULT_NOT_LIVE, RemarksGradeBean.ROWS_NOT_DELETED, userId);
		
		//rowsSaved = remarksGradeDAO.transferPassFail(bean, RemarksGradeBean.RESULT_NOT_LIVE, RemarksGradeBean.ATTEMPTED,
				//RemarksGradeBean.STATUS_RESET_PASSFAIL, RemarksGradeBean.ROWS_NOT_DELETED, userId);
		
		dataMap.clear();
		dataMap = null;
		return rowsSaved;
	}*/

}
