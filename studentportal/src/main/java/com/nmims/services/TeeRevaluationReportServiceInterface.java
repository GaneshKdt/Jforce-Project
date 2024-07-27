package com.nmims.services;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import com.nmims.beans.SRTeeRevaluationReportBean;
@Component
public interface TeeRevaluationReportServiceInterface {
	public ArrayList<SRTeeRevaluationReportBean> getSRTeeReport(String sapid);
}
