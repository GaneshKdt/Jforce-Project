package com.nmims.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.nmims.beans.DissertationResultBean;
import com.nmims.beans.StudentsTestDetailsExamBean;
import com.nmims.beans.TEEResultBean;
import com.nmims.beans.TestExamBean;
import com.nmims.dto.DissertationResultProcessingDTO;
import com.nmims.interfaces.DissertationQ8FilterService;


@Component
public class DissertationQ8FilterServiceImpl implements DissertationQ8FilterService {

	@Override
	public DissertationResultBean filterDissertationQ8IAScores(TEEResultBean id, List<DissertationResultProcessingDTO> iaTestScore,
			List<TestExamBean> examTest, String loggerInUser) {

	
		DissertationResultBean testDetails =  new DissertationResultBean();
		
		testDetails.setSapid(Long.valueOf(id.getSapid()));
		testDetails.setTimeBoundId(Integer.valueOf(id.getTimebound_id()));
		testDetails.setPrgm_sem_subj_id(id.getPrgm_sem_subj_id());
		testDetails.setCreatedBy(loggerInUser);
		testDetails.setLastModifiedBy(loggerInUser);
		testDetails.setComponent_c_status("Not Attempted");		
		
		//Created HashMap to get test Details by testId comparing
		Map<Long,TestExamBean> examTestMap = examTest.stream().collect(Collectors.toMap(TestExamBean :: getId, Function.identity()));
		
		iaTestScore.stream().forEach(test ->{
			long testId = (long) test.getTestId();
			//As testId get matched in exam.test the values will be set accordingly
			examTestMap.computeIfPresent(testId, (key,value) ->{
				testDetails.setComponent_c_max_score(value.getMaxScore());
				
				if("N".equalsIgnoreCase(test.getShowResult()) || "N".equalsIgnoreCase(value.getShowResultsToStudents())) {	
					throw new NoSuchElementException("Result Not Live for the particular batch");
				}
				
				//Score will be only set if component start with "Masters Dissertation-Defense Component"
				if(value.getTestName().startsWith("Masters Dissertation-Defense Component")) {
					testDetails.setComponent_c_score(test.getScore());				
					
					testDetails.setComponent_c_status("Attempted");	
					testDetails.setTotal((int)Math.round(test.getScore()));
				}	
				return value;
			});
		});
		
		//max score will be set here only if testId not found in exam.test
		if(testDetails.getComponent_c_max_score() == 0) {
			testDetails.setComponent_c_max_score(40);
		}
		

	return testDetails;
	}

}
