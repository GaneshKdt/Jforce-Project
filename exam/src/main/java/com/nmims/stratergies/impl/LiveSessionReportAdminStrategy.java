/**
 * 
 */
package com.nmims.stratergies.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.LiveSessionReportAdminBean;
import com.nmims.daos.LiveSessionReportAdminDAO;
import com.nmims.stratergies.LiveSessionReportAdminStrategyInterface;

/**
 * @author vil_m
 *
 */

@Service("liveSessionReportAdminStrategy")
public class LiveSessionReportAdminStrategy implements LiveSessionReportAdminStrategyInterface {
	@Autowired
	LiveSessionReportAdminDAO liveSessionReportAdminDAO;
	
	public static final Logger logger = LoggerFactory.getLogger(LiveSessionReportAdminStrategy.class);
	
	@Override
	public List<LiveSessionReportAdminBean> fetchLiveSessionReport(
			LiveSessionReportAdminBean liveSessionReportAdminBean, List<String> centerCodesList,
			String consumerTypeName) {
		// TODO Auto-generated method stub
		logger.info("Entering LiveSessionReportAdminStrategy : fetchLiveSessionReport");
		List<LiveSessionReportAdminBean> list = null;
		list = liveSessionReportAdminDAO.fetchLiveSessionReport(liveSessionReportAdminBean, centerCodesList, consumerTypeName);
		return list;
	}

}
