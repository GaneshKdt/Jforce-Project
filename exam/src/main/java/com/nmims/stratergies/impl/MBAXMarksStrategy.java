package com.nmims.stratergies.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.MBAPassFailBean;
import com.nmims.daos.MBAWXExamResultsDAO;
import com.nmims.stratergies.MBAStudentMarksStrategyInterface;

@Service
public class MBAXMarksStrategy implements MBAStudentMarksStrategyInterface {

	@Autowired
	private MBAWXExamResultsDAO dao;
	
	@Override
	public List<MBAPassFailBean> getMarksForStudentForSemester(String sapid, int term) throws Exception {
		
		List<MBAPassFailBean> passFailDataList = new ArrayList<MBAPassFailBean>();
		passFailDataList = dao.getPassFailBySapidForTermMBAXForStructureChangeStudent(sapid, term);
		if(passFailDataList.size() == 0) {
			passFailDataList = dao.getPassFailBySapidForTermMBAX(sapid, term);
		}
		
		for (MBAPassFailBean bean : passFailDataList) {
			if(!"Y".equals(bean.getIsResultLive())) {
				throw new Exception("Result for subject not live!");
			}
			
			if("1958".equals(bean.getPrgm_sem_subj_id()) || "1806".equals(bean.getPrgm_sem_subj_id())) { // Added by Abhay for Capstone Project Subject 
				bean.setCredits("20.0");
			}else if("1789".equals(bean.getPrgm_sem_subj_id())) { // Added by Abhay for Basics of Python Subject 
				bean.setCredits("2.0");
			}else {
				bean.setCredits("4.0");
			}
		}
		return passFailDataList;
	}
	
	@Override
	public List<MBAPassFailBean> getPassFailForCGPACalculationMBAX(String sapid, int term) throws Exception {
		
		List<MBAPassFailBean> passFailDataList = new ArrayList<MBAPassFailBean>();
		passFailDataList = dao.getPassFailForCGPACalculationMBAXForStructureChangeStudent(sapid, term);
		if(passFailDataList.size() == 0 ) {
			passFailDataList = dao.getPassFailForCGPACalculationMBAX(sapid, term);
		}
		for (MBAPassFailBean bean : passFailDataList) {
			if(!"Y".equals(bean.getIsResultLive())) {
				throw new Exception("Result for subject not live!");
			}
			
			if("1958".equals(bean.getPrgm_sem_subj_id()) || "1806".equals(bean.getPrgm_sem_subj_id())) { // Added by Abhay for Capstone Project Subject 
				bean.setCredits("20.0");
			}else if("1789".equals(bean.getPrgm_sem_subj_id())) { // Added by Abhay for Basics of Python Subject 
				bean.setCredits("2.0");
			}else {
				bean.setCredits("4.0");
			}
		}
		return passFailDataList;
	}

}
