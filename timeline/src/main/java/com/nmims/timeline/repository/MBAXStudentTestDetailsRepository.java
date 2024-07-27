package com.nmims.timeline.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.nmims.timeline.model.MBAXStudentsTestDetailsBean;

@Repository
public interface MBAXStudentTestDetailsRepository extends CrudRepository<MBAXStudentsTestDetailsBean, Long>   {
	
	MBAXStudentsTestDetailsBean findFirstBySapidAndTestIdOrderByIdDesc(String sapid,Long id);
	
	MBAXStudentsTestDetailsBean findFirstBySapidAndTestIdAndAttemptOrderByIdDesc(String sapid,Long id,int attempt);
	

	@Query(nativeQuery=true,value=" SELECT  " + 
			"    tst.* " + 
			"FROM " + 
			//"    exam.test t " + 
			//"        INNER JOIN " + 
			//"    exam.test_student_testdetails tst ON t.id = tst.testId " + 
			"    exam.upgrad_test_student_testdetails tst  " + 
			"WHERE " + 
			//"    TIMESTAMPDIFF(MINUTE, t.endDate, NOW()) > 180 " + 
			"    TIMESTAMPDIFF(MINUTE, tst.testStartedOn, NOW()) > 120 " + 
			"        AND (tst.answersMovedFromCacheToDB <> 'Y' " + 
			"        OR tst.answersMovedFromCacheToDB IS NULL)")
	List<MBAXStudentsTestDetailsBean> getStudentTestDetailsForAnswersMovedToDb();
	
	
}
