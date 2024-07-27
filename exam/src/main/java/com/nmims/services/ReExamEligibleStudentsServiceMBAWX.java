package com.nmims.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.ReExamEligibleStudentsResponseBean;
import com.nmims.interfaces.ReExamEligibleStudentsReportServiceInterface;
import com.nmims.stratergies.impl.ReExamEligibleStudentsStrategyMBAWX;

@Service("reExamEligibleStudentsServiceMBAWX")
public class ReExamEligibleStudentsServiceMBAWX implements ReExamEligibleStudentsReportServiceInterface {

	@Autowired
	ReExamEligibleStudentsStrategyMBAWX reExamEligibleStudentsStrategyMBAWX;
	
	@Override
	public ReExamEligibleStudentsResponseBean getReExamEligibleStudents(ReExamEligibleStudentsResponseBean searchBean) throws Exception {
		return reExamEligibleStudentsStrategyMBAWX.getReExamEligibleStudents(searchBean);
	}

}
