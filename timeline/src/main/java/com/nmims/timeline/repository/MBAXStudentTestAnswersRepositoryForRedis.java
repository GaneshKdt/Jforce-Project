package com.nmims.timeline.repository;

import com.nmims.timeline.model.MBAXStudentQuestionResponseBean;

public interface MBAXStudentTestAnswersRepositoryForRedis {

	String save(MBAXStudentQuestionResponseBean answer);

	String delete(String sapidQuestionIdAttemptConcatString);

	MBAXStudentQuestionResponseBean findBySapidQuestionIdAttemptConcatString(String sapidQuestionIdAttemptConcatString);

}
