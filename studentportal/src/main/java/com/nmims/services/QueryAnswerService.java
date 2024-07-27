package com.nmims.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.nmims.beans.SessionQueryAnswerStudentPortal;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.daos.PortalDao;
import com.nmims.interfaces.QueryAnswerInterface;

@Service
public class QueryAnswerService implements QueryAnswerInterface{
	
	@Autowired
	ApplicationContext act;
	
	@Autowired
	PortalDao portalDAO;

	@Override
	public HashMap<String, List<SessionQueryAnswerStudentPortal>> getCourseQueriesMap(String sapId,String year, String month, String subject, String programSemSubjectId) {
		// TODO Auto-generated method stub
		//PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		HashMap<String, List<SessionQueryAnswerStudentPortal>> mapOfStudentQueries=new HashMap<String, List<SessionQueryAnswerStudentPortal>>();

		List<SessionQueryAnswerStudentPortal>  myQueries= portalDAO.getQueriesForSessionByStudentV2(sapId, programSemSubjectId);/*getQueriesForSessionByStudentV2- 10 ms execution time*/
		
		List<SessionQueryAnswerStudentPortal> publicQueries = new ArrayList<SessionQueryAnswerStudentPortal>();
		publicQueries = getPublicCourseQueries( subject,sapId, programSemSubjectId, year, month);
		
		mapOfStudentQueries.put("myQueries", myQueries);
		mapOfStudentQueries.put("publicQueries", publicQueries);
		return mapOfStudentQueries;
	}
	
	//fetching records with PssId added by Saurabh
		public List<SessionQueryAnswerStudentPortal> getPublicCourseQueries( String subject,String sapId, String programSemSubjectId, String year, String month) {
			//PortalDao pDao = (PortalDao)act.getBean("portalDAO");
			List<String> pssIdList = new ArrayList<>();
			
			pssIdList=portalDAO.getPssIdBySubjectCodeId(programSemSubjectId);/*getPssIdBySubjectCodeId- 6 ms execution time*/
			
			List<SessionQueryAnswerStudentPortal> publicQueries = portalDAO.getPublicQueriesForCourseV2(sapId, pssIdList, year, month);/*getPublicQueriesForCourseV2- 45 ms execution time*/
			if (publicQueries != null) {
				for (SessionQueryAnswerStudentPortal sessionQueryAnswer : publicQueries) {
					if ("Y".equals(sessionQueryAnswer.getIsAnswered())) {
						sessionQueryAnswer.setIsAnswered("Y");
					} else {
						sessionQueryAnswer.setAnswer("Not Answered Yet");
						sessionQueryAnswer.setIsAnswered("N");
					}
				}
			}
			return publicQueries;
		}

}
