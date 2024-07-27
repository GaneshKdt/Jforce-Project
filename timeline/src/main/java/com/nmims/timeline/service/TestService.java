package com.nmims.timeline.service;

import java.util.List;

import com.nmims.timeline.model.GetTestQuestionsFromRedisByTestIdResponseBean;
import com.nmims.timeline.model.TestBean;

public interface TestService {

	String setTestQuestionsInRedisByTestId(Long testId);

	GetTestQuestionsFromRedisByTestIdResponseBean getTestQuestionsFromRedisByTestId(Long testId);

	String setUpcomingTestsPerStudentInRedisByTestId(Long testId);
	
	List<TestBean> getUpcomingTestsBySapid(String sapid);

	String setUpcomingTestsPerStudentInRedisByTimeboundId(Integer timeboundId);
	
}
