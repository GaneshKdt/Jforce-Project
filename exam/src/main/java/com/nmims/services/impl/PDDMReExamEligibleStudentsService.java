package com.nmims.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.nmims.beans.ReExamEligibleStudentsResponseBean;
import com.nmims.interfaces.ReExamEligibleStudentsReportServiceInterface;
import com.nmims.stratergies.ReExamEligibleStudentsStrategyInterface;

/**
 * 
 * @author Siddheshwar_K
 *
 */
@Service("pddmReExamEligibleStudentsService")
public class PDDMReExamEligibleStudentsService implements ReExamEligibleStudentsReportServiceInterface {

	@Autowired
	@Qualifier("pddmReExamEligibleStudentsStrategy")
	private ReExamEligibleStudentsStrategyInterface pddmReExamEligibleStudentsStrategy;
	
	@Override
	public ReExamEligibleStudentsResponseBean getReExamEligibleStudents(ReExamEligibleStudentsResponseBean searchBean)
			throws Exception {
		//Return re-exam eligible students list along with subjects.
		return pddmReExamEligibleStudentsStrategy.getReExamEligibleStudents(searchBean);
	}

}
