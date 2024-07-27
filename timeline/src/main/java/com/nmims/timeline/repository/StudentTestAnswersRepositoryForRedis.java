package com.nmims.timeline.repository;

import com.nmims.timeline.model.StudentQuestionResponseBean;

public interface StudentTestAnswersRepositoryForRedis {

	String save(StudentQuestionResponseBean answer);

	String delete(String sapidQuestionIdAttemptConcatString);

	StudentQuestionResponseBean findBySapidQuestionIdAttemptConcatString(String sapidQuestionIdAttemptConcatString);

}
