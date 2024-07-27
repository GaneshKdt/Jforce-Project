package com.nmims.stratergies;

import java.util.List;

import com.nmims.beans.ReportBean;

public interface ReportsInterface {
	
	public List<ReportBean> getAllPowerBIReportDetails(String roles);

}
