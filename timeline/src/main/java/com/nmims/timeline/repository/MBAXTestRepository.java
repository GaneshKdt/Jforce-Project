package com.nmims.timeline.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.nmims.timeline.model.MBAXTestBean;

@Repository
public interface MBAXTestRepository extends CrudRepository<MBAXTestBean, Long>   {
	
	MBAXTestBean findFirstById(Long id);
	
	@Query(nativeQuery=true,value="SELECT   " +  
			" t.*  " + 
			"FROM  " + 
			"    exam.upgrad_test t  " + 
			"        INNER JOIN  " + 
			"    exam.upgrad_test_testid_configuration_mapping tcm ON t.id = tcm.testId  " + 
			"        INNER JOIN  " + 
			"    exam.upgrad_test_live_settings tls ON tls.referenceId = tcm.referenceId  " + 
			"        INNER JOIN  " + 
			"    acads.upgrad_sessionplan_module m ON m.id = tcm.referenceId  " + 
			"        INNER JOIN  " + 
			"    acads.upgrad_sessionplan s ON s.id = m.sessionPlanId  " + 
			"        INNER JOIN  " + 
			"    acads.upgrad_sessionplanid_timeboundid_mapping stm ON stm.sessionPlanId = s.id  " + 
			"        INNER JOIN  " + 
			"    lti.student_subject_config ssc ON ssc.id = stm.timeboundId  " + 
			"WHERE  " + 
			"    tls.liveType = 'Regular'  " + 
			"        AND tls.applicableType = 'module'  " + 
			"        AND t.testType = 'Test'  " + 
			"		 AND ssc.id = ?1  " + 
			"		 AND sysdate() <= DATE_ADD(t.endDate, INTERVAL t.duration MINUTE)  " +
			"					 ")
	List<MBAXTestBean> getUpcomingTestsForAfterLoginPage(Integer timeboundId);


}
