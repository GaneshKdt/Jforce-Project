package com.nmims.timeline.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.nmims.timeline.model.TestQuestionBean;
import com.nmims.timeline.model.TestQuestionOptionBean;

	
@Repository
public interface TestQuestionsOptionRepository extends CrudRepository<TestQuestionOptionBean, Long>   {
	
	List<TestQuestionOptionBean> findAllByQuestionIdOrderById(Long questionId);

}
