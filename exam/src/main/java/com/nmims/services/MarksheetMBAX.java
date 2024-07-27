package com.nmims.services;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nmims.beans.EmbaPassFailBean;
import com.nmims.beans.MarksheetBean;
import com.nmims.beans.PassFailExamBean;
import com.nmims.daos.ExamsAssessmentsDAO;
import com.nmims.factory.MarksheetFactory;

@Component
public class MarksheetMBAX extends MarksheetFactory{
	
	@Autowired
	ExamsAssessmentsDAO examsAssessmentsDAO;

	@Override
	public MarksheetBean studentSelfMarksheet(PassFailExamBean studentMarks) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<EmbaPassFailBean> getClearedSemForStudent(String sapid) {
		// TODO Auto-generated method stub
		ArrayList<EmbaPassFailBean> subjectsToClearSem = new ArrayList<>();
		ArrayList<String> sems = new ArrayList<>();
		ArrayList<EmbaPassFailBean> passFailDataByTimeboundIdAndSapid = new ArrayList<>();
		String timeBoundIdList = "";
		String examYear = "",examMonth = "";
		ArrayList<EmbaPassFailBean> clearedSems = new ArrayList<>();
		
		try {
			sems = examsAssessmentsDAO.getSemsFromRegistrationMBAX(sapid);
			
			
			
			for(int i = 0 ;i < sems.size() ; i++) {
				
				subjectsToClearSem = examsAssessmentsDAO.getSubjectToClearBySemMBAXForStructureChangeStudent(sems.get(i),sapid);
				if(subjectsToClearSem.size() == 0) {
					subjectsToClearSem = examsAssessmentsDAO.getSubjectToClearBySemMBAX(sems.get(i),sapid);
				}
				
				if(examYear == "") {
					examYear = subjectsToClearSem.get(0).getExamYear();
				}
				if(examMonth == "") {
					examMonth = subjectsToClearSem.get(0).getExamMonth();
				}
				for(int j = 0;j < subjectsToClearSem.size(); j++) {

					
					timeBoundIdList = timeBoundIdList + subjectsToClearSem.get(j).getId() + ",";
					passFailDataByTimeboundIdAndSapid = examsAssessmentsDAO.getPassFailDataByTimeboundIdAndSapidMBAX(timeBoundIdList,sapid);
					if(subjectsToClearSem.size() == passFailDataByTimeboundIdAndSapid.size()) {
						EmbaPassFailBean pf = new EmbaPassFailBean();
						pf.setExamYear(examYear);
						pf.setExamMonth(examMonth);
						pf.setSem(sems.get(i));
						clearedSems.add(pf);
					}
				}
				examYear = "";
				examMonth = "";
				timeBoundIdList = "";
				
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
		}
				
		return clearedSems;
		
	}

}
