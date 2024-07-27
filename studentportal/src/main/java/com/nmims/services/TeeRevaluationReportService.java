package com.nmims.services;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.nmims.beans.SRTeeRevaluationReportBean;
import com.nmims.beans.SRTeeRevaluationReportBean;
import com.nmims.daos.ServiceRequestDao;
@Component
public class TeeRevaluationReportService implements TeeRevaluationReportServiceInterface {
	@Autowired
	ServiceRequestDao serviceRequestDao;

	public ArrayList<SRTeeRevaluationReportBean> getSRTeeReport(String sapid) {
		ArrayList<SRTeeRevaluationReportBean> srTeeReportData = new ArrayList<SRTeeRevaluationReportBean>();
		try {
			srTeeReportData = serviceRequestDao.getSRTeeCurrentReportData(sapid);
			if (CollectionUtils.isEmpty(srTeeReportData)) {
				srTeeReportData = serviceRequestDao.getSRTeeHistoryReportData(sapid);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return srTeeReportData;
	}

}
