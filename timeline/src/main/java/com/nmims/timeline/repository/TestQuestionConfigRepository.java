package com.nmims.timeline.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.nmims.timeline.model.TestQuestionConfigBean;

public interface TestQuestionConfigRepository extends CrudRepository<TestQuestionConfigBean, Long>   {
		
	List<TestQuestionConfigBean> findAllByTestId(Long testId);
	
}
