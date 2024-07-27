package com.nmims.services;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.ExamOrderAcadsBean;
import com.nmims.beans.QueryAnswerListBean;
import com.nmims.beans.SessionQueryAnswer;
import com.nmims.beans.StudentAcadsBean;
import com.nmims.daos.ForumDAO;
import com.nmims.daos.SessionQueryAnswerDAO;
import com.nmims.strategies.QueryAnswerStrategyInterface;
import com.nmims.util.ContentUtil;

@Service
public class QueryAnswerService implements QueryAnswerStrategyInterface {
	
	@Autowired
	private SessionQueryAnswerDAO sessionQueryAnswerDAO;
	
	@Autowired
	private ForumDAO forumDAO;
	
	private static final Logger logger = LoggerFactory.getLogger("queryAnswerService");
	
	@Override
	public void postQueryAsForum(SessionQueryAnswer sessionQuery, String year, String month) {
		// TODO Auto-generated method stub
		sessionQueryAnswerDAO.postQueryAsForum(sessionQuery,year,month);
	}

	@Override
	public void updateForumStatus(String assignetoFacultyId, String sessionQueryId) {
		// TODO Auto-generated method stub
		sessionQueryAnswerDAO.updateForumStatus(assignetoFacultyId, sessionQueryId);
	}

	@Override
	public ExamOrderAcadsBean getForumCurrentlyLive() throws Exception {
		// TODO Auto-generated method stub
		ExamOrderAcadsBean bean=forumDAO.getForumCurrentlyLive();
		return bean;
	}

	@Override
	public QueryAnswerListBean getAssignedqueriesForFaculty(ArrayList<String> nonPG_ProgramList, String facultyId) throws Exception {
		// TODO Auto-generated method stub
		QueryAnswerListBean allListsBean=new QueryAnswerListBean();
		List<SessionQueryAnswer> allQueries = sessionQueryAnswerDAO.getAllCourseQueriresByFaculty(facultyId);
		//modelnView.addObject("allQueries", allQueries);
		List<SessionQueryAnswer> unansweredQueries = new ArrayList<SessionQueryAnswer>();
		List<SessionQueryAnswer> answeredQueries = new ArrayList<SessionQueryAnswer>();
		List<SessionQueryAnswer> unansweredWXQueries = new ArrayList<SessionQueryAnswer>();
		List<SessionQueryAnswer> answeredWXQueries = new ArrayList<SessionQueryAnswer>();
		HashMap<String, StudentAcadsBean> mapOfStudentAcadsBean = new HashMap<String, StudentAcadsBean>();

		if (allQueries != null) {
			for (SessionQueryAnswer sessionQueryAnswer : allQueries) {

				if ("N".equals(sessionQueryAnswer.getHasTimeBoundId())) {

					if (sessionQueryAnswer.getSapId() != null) {
						StudentAcadsBean studentBean = new StudentAcadsBean();
						if (mapOfStudentAcadsBean.containsKey(sessionQueryAnswer.getSapId())) {
							studentBean = mapOfStudentAcadsBean.get(sessionQueryAnswer.getSapId());
						} else {
							/*1 ms execution time*/
							studentBean = sessionQueryAnswerDAO.getStudentDataBySapId(sessionQueryAnswer.getSapId());
							mapOfStudentAcadsBean.put(sessionQueryAnswer.getSapId(), studentBean);
						}

						if (!nonPG_ProgramList.contains(studentBean.getProgram())) {
							String enrollement = ContentUtil.prepareAcadDateFormat(studentBean.getEnrollmentMonth(),
									studentBean.getEnrollmentYear());
								SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
								Date date;
								date = formatter.parse(enrollement);
								Date dateCompare = formatter.parse("2021-07-01");

								if (date.compareTo(dateCompare) >= 0) {
									// student after Jul2021 enrollment
									/*1 ms execution time*/
									boolean check = sessionQueryAnswerDAO.checkForPaidStudentOnsapIdAndPssId(sessionQueryAnswer.getSapId(), sessionQueryAnswer.getProgramSemSubjectId());
									if (check) {
										sessionQueryAnswer.setIsLiveAccess("Y");
									} else {
										sessionQueryAnswer.setIsLiveAccess("N");
									}
								}else {
									//for enrollment before Jul2021 student
									sessionQueryAnswer.setIsLiveAccess("Y");
								}							
						}else {
							//for non-PG student
							sessionQueryAnswer.setIsLiveAccess("Y");
						}
					}
				}

				if ("Y".equalsIgnoreCase(sessionQueryAnswer.getHasTimeBoundId())) {
					if ("Y".equals(sessionQueryAnswer.getIsAnswered())) {
						answeredWXQueries.add(sessionQueryAnswer);
					} else {
						unansweredWXQueries.add(sessionQueryAnswer);
					}
				} else {
					if ("Y".equals(sessionQueryAnswer.getIsAnswered())) {
						answeredQueries.add(sessionQueryAnswer);
					} else {
						unansweredQueries.add(sessionQueryAnswer);
					}
				}

			}

		}
		
		allListsBean.setAnsweredQueries(answeredQueries);
		allListsBean.setUnansweredQueries(unansweredQueries);
		allListsBean.setAnsweredWXQueries(answeredWXQueries);
		allListsBean.setUnansweredWXQueries(unansweredWXQueries);

		return allListsBean;
	}

	@Override
	public List<SessionQueryAnswer> getPublicCourseQueriesForMobile(String sapId, String programSemSubjectId, String year,
			String month) {
		List<String> pssIdList = new ArrayList<>();
		List<SessionQueryAnswer> publicQueries=  new ArrayList<>();
		try {
		pssIdList=sessionQueryAnswerDAO.getPssIdBySubjectCodeId(programSemSubjectId);/*getPssIdBySubjectCodeId- 6 ms execution time*/
		publicQueries = sessionQueryAnswerDAO.getPublicQueriesForCourseV2(sapId, pssIdList, year, month);/*getPublicQueriesForCourseV2- 45 ms execution time*/
		}catch (Exception e) {
			logger.error("Error while fetching publicQueries mobile>>>"+e.getMessage()+ " for SapId : "+sapId);
			e.printStackTrace();
		}
		if (publicQueries != null) {
			for (SessionQueryAnswer sessionQueryAnswer : publicQueries) {
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
