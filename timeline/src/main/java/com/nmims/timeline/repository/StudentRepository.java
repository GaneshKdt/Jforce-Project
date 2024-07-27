package com.nmims.timeline.repository;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.nmims.timeline.model.Student;

@Repository
public interface StudentRepository extends CrudRepository<Student, String>   {
	 

	@Query(nativeQuery=true,value=" SELECT * from exam.students s  WHERE s.sapid=?1 and s.sem = (Select max(sem) from exam.students where sapid =?1 ) ")
	Student getSingleStudentsData(String sapid);
	
	Student findFirstBySapidOrderBySemDesc(String sapid);
	
	@Query(nativeQuery = true, value = "select role from lti.timebound_user_mapping where userId = ?1 and timebound_subject_config_id = ?2 ")
	String getStudentType(String sapid, String timeboundId);

	@Query(nativeQuery = true, value = "select s.* from exam.students s "
			+ " inner join "
			+ " exam.passfail pf on s.sapid = pf.sapid"
			+ " where pf.resultProcessedYear = ?1 " + 
			"	and pf.resultProcessedMonth = ?2 "
			+ " group by s.sapid ")
	List<Student> getDistinctSapidsForResultProcessingYearAndMonth(String year, String month);

	@Query(nativeQuery = true, value = "select s.* from exam.students s "
			+ " inner join "
			+ " exam.passfail pf on s.sapid = pf.sapid" 
			+ " group by s.sapid ")
	List<Student> getDistinctSapidsFromPassFail();
	

	@Query(nativeQuery = true, value = "SELECT   " + 
			" s.*  " + 
			"FROM  " + 
			"    exam.students s  " + 
			"        INNER JOIN  " + 
			"    lti.timebound_user_mapping tum ON tum.userId = s.sapid  " + 
			"        INNER JOIN  " + 
			"    lti.student_subject_config ssc ON ssc.id = tum.timebound_subject_config_id  " + 
			"WHERE  " +  
			"	ssc.id = ?  " + 
			"					 ")
	List<Student> getAllStudentsByTimeboundId(Integer timeboundId);

	
	
}
