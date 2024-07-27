package com.nmims.timeline.service;

import java.util.Map;

import com.nmims.timeline.model.MBAXGetAnswersFromRedisByStudentsTestDetailsResponseBean;
import com.nmims.timeline.model.MBAXStudentQuestionResponseBean;
import com.nmims.timeline.model.MBAXStudentsTestDetailsBean;

public interface MBAXTestAnswerService {

	Map<String, String> saveDQAnswerInCache(MBAXStudentQuestionResponseBean bean);

	MBAXGetAnswersFromRedisByStudentsTestDetailsResponseBean getAnswersFromRedisByStudentsTestDetails(
			MBAXStudentsTestDetailsBean studentsTestDetails);

	MBAXGetAnswersFromRedisByStudentsTestDetailsResponseBean getAnswersFromRedisBySapidAndTestIdAndAttempt(String sapid,
			Long testId, int attempt);

	String deleteAnswersFromRedisBySapidAndTestIdAndAttempt(String sapid, Long questionId, int attempt);

	
	
}
