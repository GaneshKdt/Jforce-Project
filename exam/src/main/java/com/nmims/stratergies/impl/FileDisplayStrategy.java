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
import com.nmims.stratergies.FileDisplayStrategyInterface;

/**
 * @author vil_m
 *
 */

@Service("fileDisplayStrategy")
public class FileDisplayStrategy implements FileDisplayStrategyInterface {
	
	@Autowired
	private RemarksGradeDAO remarksGradeDAO;
	
	public static final Logger logger = LoggerFactory.getLogger("checkListRG");
	
	@Override
	public List<RemarksGradeBean> displayFileMarks(RemarksGradeBean bean) {
		// TODO Auto-generated method stub
		logger.info("Entering FileDisplayStrategy : displayFileMarks");
		List<RemarksGradeBean> list = null;
		// for processed = 0 not processed, 1 processed. Check for not processed.
		list = remarksGradeDAO.viewMarks(bean, RemarksGradeBean.ROWS_NOT_DELETED, RemarksGradeBean.NOTPROCESSED);
		return list;
	}

}
