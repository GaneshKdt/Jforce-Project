/**
 * 
 */
package com.nmims.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.LiveSessionReportAdminBean;
import com.nmims.interfaces.LiveSessionReportAdminServiceInterface;
import com.nmims.stratergies.LiveSessionReportAdminStrategyInterface;

/**
 * @author vil_m
 *
 */

@Service("liveSessionReportAdminService")
public class LiveSessionReportAdminService implements LiveSessionReportAdminServiceInterface {
	@Autowired
	LiveSessionReportAdminStrategyInterface liveSessionReportAdminStrategy;
	
	public static final Logger logger = LoggerFactory.getLogger(LiveSessionReportAdminService.class);
	
	@Override
	public List<LiveSessionReportAdminBean> fetchLiveSessionReport(
			LiveSessionReportAdminBean liveSessionReportAdminBean, List<String> centerCodesList,
			String consumerTypeName) {
		// TODO Auto-generated method stub
		logger.info("Entering LiveSessionReportAdminService : fetchLiveSessionReport");
		List<LiveSessionReportAdminBean> list = null;
		list = liveSessionReportAdminStrategy.fetchLiveSessionReport(liveSessionReportAdminBean, centerCodesList,
				consumerTypeName);
		return list;
	}

}
