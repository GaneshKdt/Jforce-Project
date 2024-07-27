package com.nmims.timeline.service;

import java.util.Map;

import com.nmims.timeline.model.GetAnswersFromRedisByStudentsTestDetailsResponseBean;
import com.nmims.timeline.model.StudentQuestionResponseBean;
import com.nmims.timeline.model.StudentsTestDetailsBean;

public interface TestAnswerService {

	Map<String, String> saveDQAnswerInCache(StudentQuestionResponseBean bean);

	GetAnswersFromRedisByStudentsTestDetailsResponseBean getAnswersFromRedisByStudentsTestDetails(
			StudentsTestDetailsBean studentsTestDetails);

	GetAnswersFromRedisByStudentsTestDetailsResponseBean getAnswersFromRedisBySapidAndTestIdAndAttempt(String sapid,
			Long testId, int attempt);

	String deleteAnswersFromRedisBySapidAndTestIdAndAttempt(String sapid, Long questionId, int attempt);

	
	
}
