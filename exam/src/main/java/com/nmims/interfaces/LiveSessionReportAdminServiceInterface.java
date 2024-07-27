/**
 * 
 */
package com.nmims.interfaces;

import java.util.List;

import com.nmims.beans.LiveSessionReportAdminBean;

/**
 * @author vil_m
 *
 */
public interface LiveSessionReportAdminServiceInterface {

	public List<LiveSessionReportAdminBean> fetchLiveSessionReport(
			final LiveSessionReportAdminBean liveSessionReportAdminBean, final List<String> centerCodesList,
			final String consumerTypeName);
}
