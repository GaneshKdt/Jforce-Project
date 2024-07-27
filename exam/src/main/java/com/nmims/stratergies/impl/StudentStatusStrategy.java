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
import com.nmims.stratergies.StudentStatusStrategyInterface;

/**
 * @author vil_m
 *
 */

@Service("studentStatusStrategy")
public class StudentStatusStrategy implements StudentStatusStrategyInterface {

	@Autowired
	private RemarksGradeDAO remarksGradeDAO;

	public static final Logger logger = LoggerFactory.getLogger("checkListRG");


	@Override
	public List<RemarksGradeBean> downloadAbsentStudents(RemarksGradeBean bean) {
		// TODO Auto-generated method stub
		logger.info("Entering StudentStatusStrategy : downloadAbsentStudents");
		List<RemarksGradeBean> list = null;

		list = remarksGradeDAO.fetchAbsentStudents(bean);
		return list;
	}
	
	@Override
	public List<RemarksGradeBean> searchForAbsentStudents(RemarksGradeBean bean) {
		// TODO Auto-generated method stub
		logger.info("Entering StudentStatusStrategy : searchForAbsentStudents");
		List<RemarksGradeBean> list = null;

		list = remarksGradeDAO.fetchAbsentStudents(bean);
		return list;
	}

	@Override
	public RemarksGradeBean moveAbsentStudents(RemarksGradeBean bean, String userId) {
		// TODO Auto-generated method stub
		logger.info("Entering StudentStatusStrategy : moveAbsentStudents");
		boolean isSaved = Boolean.FALSE;
		List<RemarksGradeBean> list1 = null;
		RemarksGradeBean resultBean = null;
		
		logger.info("StudentStatusStrategy : moveAbsentStudents : Fetch absent students");
		list1 = this.searchForAbsentStudents(bean);
		for(int j = 0; j < list1.size();  j++) {
			list1.get(j).setStatus(RemarksGradeBean.ABSENT);

			list1.get(j).setResultLive(RemarksGradeBean.RESULT_NOT_LIVE);
			list1.get(j).setProcessed(RemarksGradeBean.NOTPROCESSED);
			list1.get(j).setActive(RemarksGradeBean.ROWS_NOT_DELETED);
		}
		
		logger.info("StudentStatusStrategy : moveAbsentStudents : Save absent students");
		isSaved = remarksGradeDAO.saveMarksForAbsent(list1, bean.getYear(), bean.getMonth(), userId);
		resultBean = new RemarksGradeBean();
		if(isSaved) {
			resultBean.setStatus(RemarksGradeBean.KEY_SUCCESS);
			resultBean.setMessage("SUCCESS : MOVE : Students with Absent : "+list1.size());
		} else {
			resultBean.setStatus(RemarksGradeBean.KEY_ERROR);
			resultBean.setMessage("FAILURE : MOVE : Students with Absent.");
		}
		return resultBean;
	}

	@Override
	public List<RemarksGradeBean> downloadCopyCaseStudents(RemarksGradeBean bean) {
		// TODO Auto-generated method stub
		logger.info("Entering StudentStatusStrategy : downloadCopyCaseStudents");
		List<RemarksGradeBean> list = null;

		list = remarksGradeDAO.fetchCopyCaseStudents(bean);
		return list;
	}
	
	@Override
	public List<RemarksGradeBean> searchForCopyCaseStudents(RemarksGradeBean bean) {
		// TODO Auto-generated method stub
		logger.info("Entering StudentStatusStrategy : searchForCopyCaseStudents");
		List<RemarksGradeBean> list1 = null;
		list1 = remarksGradeDAO.fetchCopyCaseStudents(bean);
		//list1 = fetchCopyCaseStudents(bean);
		return list1;
	}

	@Override
	public RemarksGradeBean moveCopyCaseStudents(RemarksGradeBean bean, String userId) {
		// TODO Auto-generated method stub
		logger.info("Entering StudentStatusStrategy : moveCopyCaseStudents");
		boolean isSaved = Boolean.FALSE;
		List<RemarksGradeBean> list1 = null;
		RemarksGradeBean resultBean = null;
		
		logger.info("StudentStatusStrategy : moveCopyCaseStudents : Fetch copy case students");
		list1 = this.searchForCopyCaseStudents(bean);
		for(int j = 0; j < list1.size();  j++) {
			list1.get(j).setStatus(RemarksGradeBean.CC);
			//list1.get(j).setRemarks(RemarksGradeBean.REMARK_CC);
			list1.get(j).setResultLive(RemarksGradeBean.RESULT_NOT_LIVE);
			list1.get(j).setProcessed(RemarksGradeBean.NOTPROCESSED);
			list1.get(j).setActive(RemarksGradeBean.ROWS_NOT_DELETED);
		}
		
		logger.info("StudentStatusStrategy : moveCopyCaseStudents : Save copy case students");
		isSaved = remarksGradeDAO.saveMarksForCopyCase(list1, bean.getYear(), bean.getMonth(), userId);
		resultBean = new RemarksGradeBean();
		if(isSaved) {
			resultBean.setStatus(RemarksGradeBean.KEY_SUCCESS);
			resultBean.setMessage("SUCCESS : MOVE : Students with Copy Case : "+list1.size());
		} else {
			resultBean.setStatus(RemarksGradeBean.KEY_ERROR);
			resultBean.setMessage("FAILURE : MOVE : Students with Copy Case.");
		}
		return resultBean;
	}
	
	@Override
	public List<RemarksGradeBean> searchForStudentMarksStatus(RemarksGradeBean bean) {
		// TODO Auto-generated method stub
		logger.info("Entering StudentStatusStrategy : searchForStudentMarksStatus");
		List<RemarksGradeBean> list = null;
		// for processed = 0 not processed, 1 processed. Check for not processed.
		list = remarksGradeDAO.searchForStudentMarksStatus(bean, RemarksGradeBean.ROWS_NOT_DELETED, RemarksGradeBean.NOTPROCESSED);
		/*for(int k = 0; k < list.size(); k++) {
			if ("RIA".equalsIgnoreCase(list.get(k).getStatus())) {
				list.get(k).setScoreIA("0");
				list.get(k).setScoreTotal("0");
			} else if ("NV".equalsIgnoreCase(list.get(k).getStatus())) {
				list.get(k).setScoreIA("0");
				list.get(k).setScoreTotal("0");
			}
		}*/
		return list;
	}

	@Override
	public Boolean updateForStudentMarksStatus(RemarksGradeBean bean, String userId) {
		// TODO Auto-generated method stub
		logger.info("Entering StudentStatusStrategy : updateForStudentMarksStatus");
		Boolean isUpdated = Boolean.FALSE;
		// for processed = 0 not processed, 1 processed. Check for not processed.
		isUpdated = remarksGradeDAO.updateForStudentMarksStatus(bean, userId, RemarksGradeBean.ROWS_NOT_DELETED,
				RemarksGradeBean.NOTPROCESSED, RemarksGradeBean.ROWS_NOT_DELETED, RemarksGradeBean.NOTPROCESSED);
		return isUpdated;
	}

	@Override
	public List<RemarksGradeBean> searchStudentsForAssignments(final RemarksGradeBean bean) {
		// TODO Auto-generated method stub
		logger.info("Entering StudentStatusStrategy : searchStudentsForAssignments");
		List<RemarksGradeBean> list = null;
		list = remarksGradeDAO.searchStudentsForAssignments(bean);
		return list;
	}

	@Override
	public List<RemarksGradeBean> downloadStudentsForAssignments(final RemarksGradeBean bean) {
		// TODO Auto-generated method stub
		logger.info("Entering StudentStatusStrategy : downloadStudentsForAssignments");
		List<RemarksGradeBean> list = null;
		list = remarksGradeDAO.searchStudentsForAssignments(bean);
		return list;
	}

}
