package com.nmims.timeline.repository;

import java.util.List;

import com.nmims.timeline.model.TestQuestionBean;

public interface TestQuestionRepositoryForRedis {

	String save(List<TestQuestionBean> testQuestions);

	String delete(Long testId);

	List<TestQuestionBean> findAllByTestId(Long testId);

}
