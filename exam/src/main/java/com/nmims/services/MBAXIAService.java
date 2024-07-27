package com.nmims.services;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.SectionBean;
import com.nmims.beans.StudentQuestionResponseExamBean;
import com.nmims.beans.StudentsTestDetailsExamBean;
import com.nmims.beans.TestExamBean;
import com.nmims.beans.TestQuestionExamBean;
import com.nmims.daos.MBAXIADAO;

@Service("mbaxIAService")
public class MBAXIAService {

	@Autowired
	MBAXIADAO mbaxIADao;
	
	public List<TestExamBean> getAllLiveTestsBySapId(String sapId) throws Exception {
		List<TestExamBean> testsList = mbaxIADao.getTestsBySapIdNTimeBoundIds(sapId);
		return testsList;

	}

	public LinkedHashMap<String, List<TestQuestionExamBean>> setSectionForTestQuestion( List<TestQuestionExamBean> testQuestionsList ){

		LinkedHashMap<String, List<TestQuestionExamBean>> hashmap =  new LinkedHashMap<String, List<TestQuestionExamBean>>();
		
		try {				
			
			Integer count = 0;
			
			for(TestQuestionExamBean bean : testQuestionsList) {
				
				count++;
				bean.setSrNoForSections(count);
				
				if( hashmap.containsKey(bean.getSectionName()) ) {
					List<TestQuestionExamBean> templist = hashmap.get(bean.getSectionName());
					templist.add(bean);
					hashmap.put(bean.getSectionName(), templist);
				}else{
					List<TestQuestionExamBean> templist = new ArrayList<TestQuestionExamBean>();
					templist.add(bean);
					hashmap.put(bean.getSectionName(), templist);
				}  

			}

			return hashmap;
			
		}catch(Exception e) {

			return hashmap;
		}
	}

	public  List<SectionBean> getApplicableSectionList(Long testId){
		
		List<SectionBean> list = new ArrayList<SectionBean>();
		try {
			list = mbaxIADao.getApplicableSectionList(testId);
			return list;
		}catch (Exception e) {
			return list;
		}
		
	}
	
	/**
	 * mba-x internal assessment scores on Exam Results tab<br>
	 * fetches student test details and inserts into upgrad_student_assesmentscore<br>
	 * to show results in MBAX Exam Results tab
	 * 
	 * @param testId 
	 * @param userId
	 * @return void
	 * @author harsh
	 */
	public void fetchStudentDetailsAndUpsertIntoAssesmentscore(Long testId, String userId) throws Exception {

		List<StudentsTestDetailsExamBean> testDetails = new ArrayList<>();
		testDetails = mbaxIADao.getStudentsTestDetailsToUpsertIntoAssesmentscore(testId);

		testDetails.stream().forEach( details -> {
			mbaxIADao.upsertScorsIntoAssesmentscore(details, userId);
		});
		
	}

	/**
	 * mba-x internal assessment scores on Exam Results tab<br>
	 * fetches student test answers and inserts into upgrad_student_assementsdetails<br>
	 * to show results in MBAX Exam Results tab<br>
	 * not in use, upgrad_student_assementsdetails studentAnswer field is varchar(900) causing issue in 
	 * insert and updating table  
	 * 
	 * @param testId
	 * @param userId
	 * @return void
	 * @author harsh
	 */
	public void fetchAnswersAndUpsertIntoAssementsdetails(Long testId, String userId) throws Exception {

		List<StudentQuestionResponseExamBean> testAnswers = new ArrayList<>();
		testAnswers = mbaxIADao.getStudentAnswersToUpsertIntoAssesmentscore(testId);

		testAnswers.stream().forEach( answer -> {
			mbaxIADao.upsertAnswersIntoAssementsdetails(answer, userId);
		});
		
	}

}
