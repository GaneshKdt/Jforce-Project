/**
 * 
 */
package com.nmims.interfaces;

import java.util.List;

import com.nmims.beans.LiveSessionReportAdminDTO;

/**
 * @author vil_m
 *
 */
public interface LiveSessionReportAdminClientServiceInterface {
	
	public List<LiveSessionReportAdminDTO> fetchLiveSessionReport(
			final LiveSessionReportAdminDTO liveSessionReportAdminDTO, final List<String> centerCodesList,
			final String consumerTypeName);

}
