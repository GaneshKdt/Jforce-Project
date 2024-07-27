package com.nmims.stratergies.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.ReExamEligibleStudentBean;
import com.nmims.beans.ReExamEligibleStudentsResponseBean;
import com.nmims.daos.MBAWXReportsDAO;
import com.nmims.stratergies.ReExamEligibleStudentsStrategyInterface;
import com.nmims.util.ContentUtil;

/**
 * 
 * @author Siddheshwar_K
 *
 */
@Service("pddmReExamEligibleStudentsStrategy")
public class PDDMReExamEligibleStudentsStrategy implements ReExamEligibleStudentsStrategyInterface{
	//Certificate programs consumerProgramStructureIds list. 
	public static final List<Integer> MASTER_KEY_LIST = (List<Integer>) Arrays.asList(142,143,144,145,146,147,148,149);
	
	@Autowired
	private MBAWXReportsDAO reportsDao;
	
	@Override
	public ReExamEligibleStudentsResponseBean getReExamEligibleStudents(ReExamEligibleStudentsResponseBean searchBean)
			throws Exception {
		
		//Get all failed students list along with subjects means those are eligible for re-exam booking.
		List<ReExamEligibleStudentBean> reExamEligibleStudentList = reportsDao.getListOfFailedSubjects(
				searchBean.getAuthorizedCenterCodes(), ContentUtil.frameINClauseString(MASTER_KEY_LIST));

		//If students not found then throw exception
		if(reExamEligibleStudentList == null || reExamEligibleStudentList.isEmpty())
			throw new Exception("Re-Exam eligible students not found for "+searchBean.getProductType());
		
		//Count no of failed subjects of a student and maintain in a map of sapId as key and number of subjects as values 
		//because need to set failed subjects count for each subject of a student. 
		Map<String, Long> studentFailedSubjectsMap = reExamEligibleStudentList.stream().collect(
                Collectors.groupingBy(ReExamEligibleStudentBean::getSapid, Collectors.counting()));
		
		//Set number of failed subjects count to each subject of a particular subjects because need to show in report.
		reExamEligibleStudentList.forEach(reExamEligibleStudent ->{
			reExamEligibleStudent.setNumberOfSubjects(
					studentFailedSubjectsMap.get(reExamEligibleStudent.getSapid())
					.intValue());
		});
		
		//Set re-exam Eligible Student List to search bean.
		searchBean.setListOfFailedOrResitStudentResults(reExamEligibleStudentList);
		
		//return bean having re-exam Eligible Student List
		return searchBean;
	}

}
