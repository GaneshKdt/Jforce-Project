package com.nmims.stratergies.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.daos.ExamsAssessmentsDAO;
import com.nmims.stratergies.ITimeboundSubjectPSSIdFetchingStrategy;

/**
 * 
 * @author Siddheshwar_Khanse
 *
 */
@Service("timeboundSubjectPSSIdFetchingStrategy")
public class TimeboundSubjectPSSIdFetchingStrategy implements ITimeboundSubjectPSSIdFetchingStrategy {

	@Autowired
	private ExamsAssessmentsDAO examsAssessmentsDAO;
	
	@Override
	public Optional<Integer> getTimeboundSubjectPSSId(String sapId, String timeboundId) {
		return examsAssessmentsDAO.getSubjectPSSId(sapId, timeboundId);
	}

}
