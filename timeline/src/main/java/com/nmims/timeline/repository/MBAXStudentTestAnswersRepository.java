package com.nmims.timeline.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.nmims.timeline.model.MBAXStudentQuestionResponseBean;

@Repository
public interface MBAXStudentTestAnswersRepository extends CrudRepository<MBAXStudentQuestionResponseBean, Long>   {

	boolean existsBySapidAndTestIdAndAttemptAndQuestionId(String sapid, Long testId, int attempt, Long questionId);
	
	MBAXStudentQuestionResponseBean findFirstBySapidAndTestIdAndAttemptAndQuestionId(String sapid, Long testId, int attempt, Long questionId);
	
}
