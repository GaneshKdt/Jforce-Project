package com.nmims.timeline.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.nmims.timeline.model.StudentsTestDetailsBean;
import com.nmims.timeline.model.TestBean;

@Repository
public interface StudentTestDetailsRepository extends CrudRepository<StudentsTestDetailsBean, Long>   {
	
	StudentsTestDetailsBean findFirstBySapidAndTestIdOrderByIdDesc(String sapid,Long id);
	
	StudentsTestDetailsBean findFirstBySapidAndTestIdAndAttemptOrderByIdDesc(String sapid,Long id,int attempt);
	

	@Query(nativeQuery=true,value=" SELECT  " + 
			"    tst.* " + 
			"FROM " + 
			//"    exam.test t " + 
			//"        INNER JOIN " + 
			//"    exam.test_student_testdetails tst ON t.id = tst.testId " + 
			"    exam.test_student_testdetails tst  " + 
			"WHERE " + 
			//"    TIMESTAMPDIFF(MINUTE, t.endDate, NOW()) > 180 " + 
			"    TIMESTAMPDIFF(MINUTE, tst.testStartedOn, NOW()) > 120 " + 
			"        AND (tst.answersMovedFromCacheToDB <> 'Y' " + 
			"        OR tst.answersMovedFromCacheToDB IS NULL)")
	List<StudentsTestDetailsBean> getStudentTestDetailsForAnswersMovedToDb();
	
	
}
