package com.nmims.timeline.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.nmims.timeline.model.Post;
import com.nmims.timeline.model.TestQuestionBean;


@Repository
public interface TestQuestionRepository extends CrudRepository<TestQuestionBean, Long>   {
	
	
//	@Query(nativeQuery=true,value="select q.*,c.url as url, s.sectionName as sectionName "
//			+ " from exam.test_questions q " + 
//			"   LEFT JOIN exam.test_question_additionalcontent c " + 
//			"	on q.id=c.questionId " +
//			"   LEFT JOIN exam.test_sections s " + 
//			"	on q.sectionId = s.sectionId " + 
//			"	where q.testId=? and q.isSubQuestion = 0 "
//			+ " Order By q.sectionId, q.id ")
//	List<TestQuestionBean> getAllQuestionsByTestId(Long testId);
	
	@Query(value="SELECT "
			+ " new com.nmims.timeline.model.TestQuestionBean(  q.id, q.testId, q.marks, q.type, q.chapter,"
			+ " q.question, q.description, q.option1, q.option2, q.option3, q.option4, q.option5, q.option6,"
			+ " q.option7, q.option8, q.correctOption, q.isSubQuestion, q.active, q.copyCaseThreshold, q.uploadType,"
			+ " q.sectionId,  s.sectionName ) "
			+ " FROM TestQuestionBean q " + 
			"   LEFT JOIN q.testSectionsBean s " +
			"	where q.testId = ?1 and q.isSubQuestion = 0 "
			+ " Order By q.sectionId, q.id ")
	List<TestQuestionBean> getAllQuestionsByTestId(Long testId);
	
}
