package com.nmims.services.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.nmims.beans.EmbaPassFailBean;
import com.nmims.beans.TEEResultBean;
import com.nmims.services.ITimeboundProjectPassFailService;
import com.nmims.stratergies.ITimeboundEligibleStudentsForProjectPassFailStrategy;
import com.nmims.stratergies.ITimeboundProjectPassFailProcessingStrategy;
import com.nmims.stratergies.ITimeboundStudentProjectMarksFetchingStrategy;
import com.nmims.stratergies.ITimeboundSubjectPSSIdFetchingStrategy;
import com.nmims.stratergies.IUpsertTimeboundStudentProjectMarksStrategy;
import com.nmims.stratergies.IUpsertTimeboundStudentProjectPassFailStrategy;

/**
 * 
 * @author Siddheshwar_Khanse
 *
 */
@Service("mbawxProjectPassFailService")
public class MBAWXProjectPassFailService implements ITimeboundProjectPassFailService {

	@Autowired
	@Qualifier("mbawxStudentProjectMarksFetchingStrategy")
	private ITimeboundStudentProjectMarksFetchingStrategy mbawxStudentProjectMarksFetchingStrategy;
	
	@Autowired
	@Qualifier("mbawxUpsertTimeboundStudentProjectMarksStrategy")
	private IUpsertTimeboundStudentProjectMarksStrategy mbawxUpsertTimeboundStudentProjectMarksStrategy;
	
	@Autowired
	@Qualifier("timeboundSubjectPSSIdFetchingStrategy")
	private ITimeboundSubjectPSSIdFetchingStrategy timeboundSubjectPSSIdFetchingStrategy;
	
	@Autowired
	@Qualifier("mbawxEligibleStudentsForProjectPassFailStrategy")
	private ITimeboundEligibleStudentsForProjectPassFailStrategy mbawxEligibleStudentsForProjectPassFailStrategy;
	
	@Autowired
	@Qualifier("mbawxProjectPassFailProcessingStrategy")
	private ITimeboundProjectPassFailProcessingStrategy mbawxProjectPassFailProcessingStrategy;
	
	@Autowired
	@Qualifier("mbawxStudentUpsertProjectPassFailStrategy")
	private IUpsertTimeboundStudentProjectPassFailStrategy mbawxStudentUpsertProjectPassFailStrategy;
	
	
	@Override
	public List<TEEResultBean> getTimeboundStudentProjectMarks(String subjectName, Integer timeboundId) {
		return mbawxStudentProjectMarksFetchingStrategy.getTimeboundStudentProjectMarks(subjectName, timeboundId);
	}

	@Override
	public List<String> upsertTimeboundStudentProjectMarks(List<TEEResultBean> studentMarksList) {
		return mbawxUpsertTimeboundStudentProjectMarksStrategy.upsertTimeboundStudentProjectMarks(studentMarksList);
		
	}

	@Override
	public Optional<Integer> getTimeboundSubjectPSSId(String sapId, String timeboundId) {
		return timeboundSubjectPSSIdFetchingStrategy.getTimeboundSubjectPSSId(sapId, timeboundId);
	}

	@Override
	public List<TEEResultBean> getEligibleStudentsForProjectPassFail(String timeboundId) {
		return mbawxEligibleStudentsForProjectPassFailStrategy.getEligibleStudentsForProjectPassFail(timeboundId);
	}

	@Override
	public Map<String, List<EmbaPassFailBean>> processTimeboundStudentsProjectPassFail(List<TEEResultBean> eligibleStudentsForProjectPassFail, String loggedInUser) {
		return mbawxProjectPassFailProcessingStrategy.processTimeboundStudentsProjectPassFail(eligibleStudentsForProjectPassFail, loggedInUser);
	}

	@Override
	public List<String> upsertTimeboundStudentProjectPassFail(List<EmbaPassFailBean> studentPassFailList) {
		return mbawxStudentUpsertProjectPassFailStrategy.upsertTimeboundStudentProjectPassFail(studentPassFailList);
	}

}
