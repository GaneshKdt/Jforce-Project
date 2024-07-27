package com.nmims.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.tomcat.util.digester.ArrayStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.nmims.beans.DissertationResultBean;
import com.nmims.beans.ProgramSubjectMappingExamBean;
import com.nmims.beans.StudentsTestDetailsExamBean;
import com.nmims.beans.TEEResultBean;
import com.nmims.beans.TestExamBean;
import com.nmims.beans.TimeBoundUserMapping;
import com.nmims.dto.DissertationResultProcessingDTO;
import com.nmims.interfaces.DissertationFilterService;

@Service
public class DissertationFilterServiceImpl implements DissertationFilterService {

	private static final Logger dissertationLogs = (Logger) LoggerFactory.getLogger("dissertationResultProcess");

	
	@Override
	public List<TEEResultBean> filterIAEligibleStudent(List<TimeBoundUserMapping> timboundUser, String subjectId, 
			DissertationResultProcessingDTO program, List<DissertationResultBean> errorList){
		List<TEEResultBean> studentList = new ArrayList<TEEResultBean>();
		//All the student return by timeBoundId from timebound_student_config should  have the timbound id in
		//student_subject_config table 
		
		timboundUser.stream().forEach(user -> {
			try {
				TEEResultBean bean = new TEEResultBean();
				bean.setSapid(user.getUserId());
				bean.setPassScore(program.getPassScore());
				bean.setPrgm_sem_subj_id(program.getId());
				bean.setTimebound_id(String.valueOf(user.getTimebound_subject_config_id()));
				studentList.add(bean);
			} catch (Exception e) {
				DissertationResultBean errorBean =  new DissertationResultBean();
				errorBean.setSapid(Long.parseLong(user.getUserId()));
				errorBean.setFailReason(e.getMessage());
				errorList.add(errorBean);
				return;
			}
		});
		return studentList;
	}

	
	@Override
	public List<DissertationResultBean> filterIAScores(TEEResultBean sapId, List<DissertationResultProcessingDTO> iaTestScore, List<TestExamBean> examTest, String loggedInUser) {
		//To collect IAScores
		List<StudentsTestDetailsExamBean> iaMarks = new ArrayList<StudentsTestDetailsExamBean>();
		
		//To return score after filtering
		List<DissertationResultBean> ScoreListForSapid =  new ArrayList<DissertationResultBean>();
		
		DissertationResultBean testData = new DissertationResultBean();
		testData.setTimeBoundId(Integer.parseInt(sapId.getTimebound_id()));
		testData.setSapid(Long.parseLong(sapId.getSapId()));
		testData.setPrgm_sem_subj_id(sapId.getPrgm_sem_subj_id());
		testData.setCreatedBy(loggedInUser);
		testData.setLastModifiedBy(loggedInUser);

		testData.setComponent_b_max_score(40);
		testData.setComponent_a_max_score(20);

		testData.setComponent_a_status("Not Attempted");
		testData.setComponent_b_status("Not Attempted");
	
		
		//to retrive examtest Details by testId
		try {
		Map<Long, TestExamBean> examTestMap =examTest.stream().collect(Collectors.toMap(TestExamBean:: getId,Function.identity()));
		dissertationLogs.info("IA scores found for"+testData.getSapid()+" "+iaTestScore.size());
		iaTestScore.stream().forEach(iaScore -> {
			long  testId = (long) iaScore.getTestId();
			

			examTestMap.computeIfPresent(testId, (key,value) -> {
				//getting test Data by testId
				if (iaScore.getTestId() == value.getId()) {		
					StudentsTestDetailsExamBean student = new StudentsTestDetailsExamBean();
					student.setSapid(iaScore.getSapid());
					student.setShowResult(iaScore.getShowResult());
					student.setShowResultsToStudents(value.getShowResultsToStudents());
					student.setScore(iaScore.getScore());
					student.setStartDate(value.getStartDate());
					student.setEndDate(value.getEndDate());
					student.setScoreInInteger(Double.valueOf(iaScore.getScoreInInteger()));
					student.setShowResultsToStudents(value.getShowResultsToStudents());
					student.setTestName(value.getTestName());
					student.setMaxScore(value.getMaxScore());
					iaMarks.add(student);
				}
				return value;
			});
		});

		if(!iaMarks.isEmpty()) {
			iaMarks.stream().forEach(iaTest ->{
			
				//if result is not live than result will not processed for that batch or timebound
			
				
				if(iaTest.getShowResult().equalsIgnoreCase("N") || iaTest.getShowResultsToStudents().equalsIgnoreCase("N")) {
					ScoreListForSapid.clear();
					throw new NoSuchElementException("Result not live for the particular batch");
					
					//if testName is Solution Component -
				}else if(iaTest.getTestName().startsWith("Solution Component")) {
					testData.setComponent_b_score(iaTest.getScore());
					//testData.setComponent_b_max_score(iaTest.getMaxScore());
					testData.setComponent_b_status("Attempted");

					//if testName is Approach Component  -
				}else if(iaTest.getTestName().startsWith("Approach Component")){
					testData.setComponent_a_score(iaTest.getScore());
					//testData.setComponent_a_max_score(iaTest.getMaxScore());
					testData.setComponent_a_status("Attempted");

					// if test Name not equal to above subject name than the insertion will be done as not attempt 
				}else {
					testData.setComponent_a_status("Not Attempted");
					testData.setComponent_b_status("Not Attempted");
				}
				
			});
		}
		}catch(Exception e) {
			dissertationLogs.info("Error Found For"+testData.getSapid()+" "+e.getMessage());

		}
		
			ScoreListForSapid.add(testData);
			
		
		
		
	return ScoreListForSapid;
	}
	
	
}
