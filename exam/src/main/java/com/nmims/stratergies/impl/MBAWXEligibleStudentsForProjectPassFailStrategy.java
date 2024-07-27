package com.nmims.stratergies.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.TEEResultBean;
import com.nmims.daos.ExamsAssessmentsDAO;
import com.nmims.stratergies.ITimeboundEligibleStudentsForProjectPassFailStrategy;

/**
 * 
 * @author Siddheshwar_Khanse
 *
 */
@Service("mbawxEligibleStudentsForProjectPassFailStrategy")
public class MBAWXEligibleStudentsForProjectPassFailStrategy
		implements ITimeboundEligibleStudentsForProjectPassFailStrategy {

	@Autowired
	private ExamsAssessmentsDAO examsAssessmentsDAO;
	
	@Override
	public List<TEEResultBean> getEligibleStudentsForProjectPassFail(String timeboundId) {
		return examsAssessmentsDAO.getEligibleStudentsForProjectPassFail(timeboundId);
	}
	

	
}
