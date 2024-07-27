package com.nmims.timeline.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.nmims.timeline.model.TestBean;
import com.nmims.timeline.model.Timebound;

@Repository
public interface TimeboundRepository extends CrudRepository<Timebound, Integer>   {
	
	@Query(nativeQuery=true,value="SELECT   " +  
			"  ssc.*  " + 
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
			"		 AND t.id = ?1  " + 
			"					 ")
	Timebound getTimeboundIdByTestId(Long testId);
	
}
