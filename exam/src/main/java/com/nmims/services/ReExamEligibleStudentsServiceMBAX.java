package com.nmims.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.ReExamEligibleStudentsResponseBean;
import com.nmims.interfaces.ReExamEligibleStudentsReportServiceInterface;
import com.nmims.stratergies.impl.ReExamEligibleStudentsStrategyMBAX;

@Service("reExamEligibleStudentsServiceMBAX")
public class ReExamEligibleStudentsServiceMBAX implements ReExamEligibleStudentsReportServiceInterface{

	@Autowired
	ReExamEligibleStudentsStrategyMBAX reExamEligibleStudentsStrategyMBAX;
	
	@Override
	public ReExamEligibleStudentsResponseBean getReExamEligibleStudents(ReExamEligibleStudentsResponseBean searchBean) throws Exception {
		return reExamEligibleStudentsStrategyMBAX.getReExamEligibleStudents(searchBean);
	}

}
