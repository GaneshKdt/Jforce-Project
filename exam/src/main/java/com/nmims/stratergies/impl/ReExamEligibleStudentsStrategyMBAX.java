package com.nmims.stratergies.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.nmims.beans.ReExamEligibleStudentBean;
import com.nmims.beans.ReExamEligibleStudentsResponseBean;
import com.nmims.daos.MBAXReportsDAO;
import com.nmims.stratergies.ReExamEligibleStudentsStrategyInterface;

@Service("reExamEligibleStudentsStrategyMBAX")
public class ReExamEligibleStudentsStrategyMBAX implements ReExamEligibleStudentsStrategyInterface {

	@Value("${CURRENT_MBAX_ACAD_MONTH}")
	private String CURRENT_MBAX_ACAD_MONTH;
	
	@Value("${CURRENT_MBAX_ACAD_YEAR}")
	private String CURRENT_MBAX_ACAD_YEAR;
	
	@Autowired
	MBAXReportsDAO reportsDao;
	
	@Override
	public ReExamEligibleStudentsResponseBean getReExamEligibleStudents(ReExamEligibleStudentsResponseBean searchBean) throws Exception {

		List<ReExamEligibleStudentBean> listOfFailedOrResitStudentResults = new ArrayList<ReExamEligibleStudentBean>();
		List<String> listOfFailedStudentSapids;
		try {
			listOfFailedStudentSapids = reportsDao.getListOfFailedStudents(searchBean.getAuthorizedCenterCodes());
		} catch (Exception e) {
			
			throw new Exception("Error getting list of failed student sapids" + " Error Message : " + e.getMessage());
		}

		for (String failedStudentSapid : listOfFailedStudentSapids) {
			try {

				String eligibleForReExam;
//				String notEligibleReason;

				List<ReExamEligibleStudentBean> listOfFailedStudentResults = reportsDao.getListOfFailedSubjectsForStudent(failedStudentSapid);
				
				boolean currentCycleSubject = false;
				
				for (ReExamEligibleStudentBean bean : listOfFailedStudentResults) {
					if(bean.getAcadMonth().equals(CURRENT_MBAX_ACAD_MONTH) && bean.getAcadYear().equals(CURRENT_MBAX_ACAD_YEAR)) {
						currentCycleSubject = true;
						searchBean.setExamMonth(bean.getExamMonth());
						searchBean.setExamYear(bean.getExamYear());
					}
				}
				
				if(currentCycleSubject) {
					if(listOfFailedStudentResults.size() <= 2) {
						eligibleForReExam = "Y";
//						notEligibleReason = "";
					} else {
						eligibleForReExam = "N";
//						notEligibleReason = "Failed subject count = " + listOfFailedStudentResults.size();
					}
				} else {
					eligibleForReExam = "Y";
//					notEligibleReason = "";
				}
				for (ReExamEligibleStudentBean bean : listOfFailedStudentResults) {
//					bean.setEligibleForReExam(eligibleForReExam);
//					bean.setNotEligibleReason(notEligibleReason);
					bean.setNumberOfSubjects(listOfFailedStudentResults.size());
				}
				if(eligibleForReExam.equals("Y")) {
					listOfFailedOrResitStudentResults.addAll(listOfFailedStudentResults);		
				}
			} catch (Exception e) {
				
				throw new Exception("Error getting list of failed subject info for sapid : " + failedStudentSapid + " Error Message : " + e.getMessage());
			}
		}
		
		searchBean.setListOfFailedOrResitStudentResults(listOfFailedOrResitStudentResults);
		return searchBean;
	}
}
