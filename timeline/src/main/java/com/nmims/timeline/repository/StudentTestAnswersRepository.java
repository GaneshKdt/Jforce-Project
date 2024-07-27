package com.nmims.timeline.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.nmims.timeline.model.StudentQuestionResponseBean;
import com.nmims.timeline.model.TestBean;

@Repository
public interface StudentTestAnswersRepository extends CrudRepository<StudentQuestionResponseBean, Long>   {

	boolean existsBySapidAndTestIdAndAttemptAndQuestionId(String sapid, Long testId, int attempt, Long questionId);
	
	StudentQuestionResponseBean findFirstBySapidAndTestIdAndAttemptAndQuestionId(String sapid, Long testId, int attempt, Long questionId);
	
}
