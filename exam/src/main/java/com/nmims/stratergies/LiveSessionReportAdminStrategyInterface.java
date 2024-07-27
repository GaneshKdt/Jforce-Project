/**
 * 
 */
package com.nmims.stratergies;

import java.util.List;

import com.nmims.beans.LiveSessionReportAdminBean;

/**
 * @author vil_m
 *
 */
public interface LiveSessionReportAdminStrategyInterface {

	public List<LiveSessionReportAdminBean> fetchLiveSessionReport(
			final LiveSessionReportAdminBean liveSessionReportAdminBean, final List<String> centerCodesList,
			final String consumerTypeName);

}
