package com.nmims.timeline.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.nmims.timeline.model.TestBean;

@Repository
public interface TestRepository extends CrudRepository<TestBean, Long>   {
	
	TestBean findFirstById(Long id);
	
	@Query(nativeQuery=true,value="SELECT   " +  
			" t.*  " + 
			"FROM  " + 
			"    exam.test t  " + 
			"        INNER JOIN  " + 
			"    exam.test_testid_configuration_mapping tcm ON t.id = tcm.testId  " + 
			"        INNER JOIN  " + 
			"    exam.test_live_settings tls ON tls.referenceId = tcm.referenceId  " + 
			"        INNER JOIN  " + 
			"    acads.sessionplan_module m ON m.id = tcm.referenceId  " + 
			"        INNER JOIN  " + 
			"    acads.sessionplan s ON s.id = m.sessionPlanId  " + 
			"        INNER JOIN  " + 
			"    acads.sessionplanid_timeboundid_mapping stm ON stm.sessionPlanId = s.id  " + 
			"        INNER JOIN  " + 
			"    lti.student_subject_config ssc ON ssc.id = stm.timeboundId  " + 
			"WHERE  " + 
			"    tls.liveType = 'Regular'  " + 
			"        AND tls.applicableType = 'module'  " + 
			"        AND t.testType = 'Test'  " + 
			"		 AND ssc.id = ?1  " + 
			"		 AND sysdate() <= DATE_ADD(t.endDate, INTERVAL t.duration MINUTE)  " +
			"					 ")
	List<TestBean> getUpcomingTestsForAfterLoginPage(Integer timeboundId);


}
