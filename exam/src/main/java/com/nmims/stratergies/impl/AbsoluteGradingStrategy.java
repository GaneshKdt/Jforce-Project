/**
 * 
 */
package com.nmims.stratergies.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.RemarksGradeBean;
import com.nmims.daos.RemarksGradeDAO;
import com.nmims.stratergies.AbsoluteGradingStrategyInterface;

/**
 * @author vil_m
 *
 */

@Service("absoluteGradingStrategy")
public class AbsoluteGradingStrategy implements AbsoluteGradingStrategyInterface {

	@Autowired
	private RemarksGradeDAO remarksGradeDAO;

	public static final Logger logger = LoggerFactory.getLogger("checkListRG");

	@Override
	public Integer searchProcessStudentForGrade(RemarksGradeBean bean, String userId) {
		// TODO Auto-generated method stub
		logger.info("Entering AbsoluteGradingStrategy : searchProcessStudentForGrade");
		boolean isSaved = Boolean.FALSE;
		Integer totalRowsChanged = 0;
		List<RemarksGradeBean> list1 = null;
		List<RemarksGradeBean> listCC = null;
		List<RemarksGradeBean> listAB = null;
		list1 = searchStudentForGrading(bean);
		listCC = searchStudentInCopyCaseForGrading(bean);
		listAB = searchStudentInAbsentForGrading(bean);
		//if(null != listCC && !listCC.isEmpty()) {
		//	list1.addAll(listCC);
		//}
		list1 = mergeLists(list1, listCC);
		list1 = mergeLists(list1, listAB);
		
		assignStudentsGrade(list1);
		isSaved = saveStudentsGrade(list1, userId);
		if(isSaved) {
			totalRowsChanged = list1.size();
		}
		return totalRowsChanged;
	}
	
	protected List<RemarksGradeBean> mergeLists(List<RemarksGradeBean> listA, List<RemarksGradeBean> listB) {
		logger.info("Entering AbsoluteGradingStrategy : mergeLists");
		List<RemarksGradeBean> listR = null;
		if(null != listA && !listA.isEmpty()) {
			listR = listA;
			listR.addAll(listB);
		} else {
			listR = listB;
		}
		return listR;
	}

	@Override
	public List<RemarksGradeBean> searchStudentForGrading(RemarksGradeBean bean) {
		// TODO Auto-generated method stub
		logger.info("Entering AbsoluteGradingStrategy : searchStudentForGrading");
		List<RemarksGradeBean> list = null;
		list = remarksGradeDAO.searchStudentForGrading(bean, RemarksGradeBean.ATTEMPTED,
				RemarksGradeBean.STATUS_RESET_PASSFAIL, RemarksGradeBean.ROWS_NOT_DELETED);
		return list;
	}

	@Override
	public void assignStudentsGrade(List<RemarksGradeBean> list) {
		// TODO Auto-generated method stub
		logger.info("Entering AbsoluteGradingStrategy : assignStudentsGrade");
		String status = null;
		for (int i = 0; i < list.size(); i++) {
			status = list.get(i).getStatus();
			if(RemarksGradeBean.ATTEMPTED.equals(status) || RemarksGradeBean.CC.equals(status)) {
				if (list.get(i).isPass()) {
					list.get(i).setGrade(RemarksGradeBean.GRADE_SATISFACTORY);
				} else {
					list.get(i).setGrade(RemarksGradeBean.GRADE_NOTSATISFACTORY);
				}
			}//nothing to do for RemarksGradeBean.ABSENT
			list.get(i).setResultLive(RemarksGradeBean.RESULT_NOT_LIVE);
		}
	}

	@Override
	public boolean saveStudentsGrade(List<RemarksGradeBean> list, String userId) {
		// TODO Auto-generated method stub
		logger.info("Entering AbsoluteGradingStrategy : saveStudentsGrade");
		boolean isSuccess = Boolean.TRUE;
		isSuccess = remarksGradeDAO.batchSaveStudentsGrade(list, userId);
		return isSuccess;
	}

	@Override
	public List<RemarksGradeBean> searchStudentInCopyCaseForGrading(RemarksGradeBean bean) {
		// TODO Auto-generated method stub
		logger.info("Entering AbsoluteGradingStrategy : searchStudentInCopyCaseForGrading");
		List<RemarksGradeBean> list = null;
		list = remarksGradeDAO.searchStudentForGrading(bean, RemarksGradeBean.CC,
				RemarksGradeBean.STATUS_RESET_PASSFAIL, RemarksGradeBean.ROWS_NOT_DELETED);
		return list;
	}

	@Override
	public List<RemarksGradeBean> searchStudentInAbsentForGrading(RemarksGradeBean bean) {
		// TODO Auto-generated method stub
		logger.info("Entering AbsoluteGradingStrategy : searchStudentInAbsentForGrading");
		List<RemarksGradeBean> list = null;
		list = remarksGradeDAO.searchStudentForGrading(bean, RemarksGradeBean.ABSENT,
				RemarksGradeBean.STATUS_RESET_PASSFAIL, RemarksGradeBean.ROWS_NOT_DELETED);
		return list;
	}
}
