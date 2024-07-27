package com.nmims.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.FacultyCareerservicesBean;
import com.nmims.beans.PageCareerservicesBean;
import com.nmims.beans.PersonCareerservicesBean;
import com.nmims.beans.SessionDayTimeBean;
import com.nmims.beans.SessionQueryAnswerCareerservicesBean;
import com.nmims.beans.StudentCareerservicesBean;
import com.nmims.daos.FacultyDAO;
import com.nmims.daos.LDAPDao;
import com.nmims.daos.SessionQueryAnswerDAO;
import com.nmims.daos.SessionsDAO;
import com.nmims.helpers.AESencrp;
import com.nmims.helpers.MailSender;
import com.nmims.helpers.SalesforceHelper_Sessions;


@Controller
public class QueryAnswerController extends BaseController {

	@Autowired(required = false)
	ApplicationContext act;

	@Autowired
	private SessionQueryAnswerDAO sessionQueryAnswerDAO;
	
	@Autowired
	private SessionsDAO sessionsDAO;
	
	@Autowired
	private FacultyDAO facultyDAO;
	
	@Autowired
	private SalesforceHelper_Sessions salesforceHelper;

	@Value("${WEB_EX_API_URL}")
	private String WEB_EX_API_URL;

	@Value("${WEB_EX_LOGIN_API_URL}")
	private String WEB_EX_LOGIN_API_URL;

	@Value("${WEBEX_ID}")
	private String WEBEX_ID;

	@Value("${WEBEX_PASS}")
	private String WEBEX_PASS;

	@Value("${MAX_WEBEX_USERS}")
	private int MAX_WEBEX_USERS;

	@Value("${CURRENT_ACAD_MONTH}")
	private String CURRENT_ACAD_MONTH;

	@Value("${CURRENT_ACAD_YEAR}")
	private int CURRENT_ACAD_YEAR;

	@Value("#{'${ACAD_YEAR_LIST}'.split(',')}")
	private List<String> ACAD_YEAR_LIST;

	private static final Logger logger = LoggerFactory.getLogger(QueryAnswerController.class);
	
	@RequestMapping(value = "/viewAllUnAnsweredQueriesForm", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView viewAllUnAnsweredQueriesForm() {
		ModelAndView mav = new ModelAndView("viewAllQueries");
		FacultyDAO fDao = facultyDAO;
		List<FacultyCareerservicesBean> listOfFaculty = new ArrayList<FacultyCareerservicesBean>();
		listOfFaculty = fDao.getAllFacultyRecords();
		mav.addObject("queryAnswer", new SessionQueryAnswerCareerservicesBean());
		mav.addObject("yearList", ACAD_YEAR_LIST);
		mav.addObject("listOfFaculty", listOfFaculty);
		return mav;
	}

	@RequestMapping(value = "/allocateFacultyToQueries", method = { RequestMethod.POST })
	public ModelAndView allocateFacultyToQueries(@ModelAttribute SessionQueryAnswerCareerservicesBean allocateAnswer,
			HttpServletRequest request) {

		String userId = (String) request.getSession().getAttribute("userId");
		try {
			allocateAnswer.setLastModifiedBy(userId);
			sessionQueryAnswerDAO.allocateFacultyToAnswer(allocateAnswer.getListOfRecordIdToBeAssigned(),
					allocateAnswer);
			setSuccess(request, "Allocated Users Successfully");
			return viewAllUnAnsweredQueriesForm();
		} catch ( Throwable throwable ) {
			logger.info("in allocateFacultyToQueries got exception : "+ExceptionUtils.getFullStackTrace(throwable));
			setError(request, "Error in allocating");
			return viewAllUnAnsweredQueriesForm();
		}

	}

	@RequestMapping(value = "/viewAllUnAnsweredQueries", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView viewAllUnAnsweredQueries(@ModelAttribute SessionQueryAnswerCareerservicesBean queryAnswer,
			HttpServletRequest request) {
		List<SessionQueryAnswerCareerservicesBean> listOfSessionQueryAnswer = sessionQueryAnswerDAO
				.getListOfSessionQueryAnswer(queryAnswer);
		ModelAndView mav = new ModelAndView("viewAllQueries");
		SessionQueryAnswerCareerservicesBean allocateAnswer = new SessionQueryAnswerCareerservicesBean();

		allocateAnswer.setFacultyId(queryAnswer.getFacultyId());
		int rowCount = 0;
		if (listOfSessionQueryAnswer != null && listOfSessionQueryAnswer.size() > 0) {
			rowCount = listOfSessionQueryAnswer.size();
			mav.addObject("listOfSessionQueryAnswer", listOfSessionQueryAnswer);
			mav.addObject("rowCount", rowCount);
			mav.addObject("queryAnswer", queryAnswer);
			mav.addObject("allocateAnswer", allocateAnswer);
			return mav;
		} else {
			setError(request, "No Un-Answered Records Found");
			mav.addObject("queryAnswer", queryAnswer);
			return mav;
		}

	}

	@RequestMapping(value = "/updatePostMyQueryFromSalesforce", method = { RequestMethod.GET, RequestMethod.POST })
	public void updatePostMyQueryFromSalesforce(HttpServletRequest request, HttpServletResponse response) {
		String id = request.getParameter("id");
		String answer = request.getParameter("answer");
		MailSender mailSender = (MailSender) act.getBean("mailer");
		SessionsDAO dao = sessionsDAO;
		dao.updateSessionQueryAnsById(id, answer);

		SessionQueryAnswerCareerservicesBean sessionQuery = dao.findSessionQueryAnswerById(id);
		SessionDayTimeBean session = dao.findScheduledSessionById(sessionQuery.getSessionId());
		// sending notification to Student after Query is Answer by Faculty
		StudentCareerservicesBean student = dao.getSingleStudentsData(sessionQuery.getSapId());
		mailSender.sendQueryAnswerPostedEmailToStudent(sessionQuery, student, session);
	}

	@RequestMapping(value = "/postQueryForm", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView postQueryForm(HttpServletRequest request, HttpServletResponse response) throws Exception {

		ModelAndView modelnView = new ModelAndView("portal/career_forum/postQuery");
		if (!checkSession(request, response)) {
			return new ModelAndView("studentPortalRediret");
		}
		String sapId = (String) request.getSession().getAttribute("userId");

		String action = request.getParameter("action");
		modelnView.addObject("action", action);

		String id = request.getParameter("id");
		SessionsDAO dao = sessionsDAO;
		SessionDayTimeBean session = dao.findScheduledSessionById(id);
		SessionQueryAnswerCareerservicesBean sessionQuery = new SessionQueryAnswerCareerservicesBean();
		sessionQuery.setSessionId(id);
		sessionQuery.setSapId(sapId);

		modelnView.addObject("session", session);
		modelnView.addObject("sessionQuery", sessionQuery);

		List<SessionQueryAnswerCareerservicesBean> myQueries = getMyQueries(sessionQuery);
		modelnView.addObject("myQueries", myQueries);

		List<SessionQueryAnswerCareerservicesBean> publicQueries = getPublicQueries(sessionQuery);
		modelnView.addObject("publicQueries", publicQueries);

		return modelnView;
	}


	@RequestMapping(value = "/postQuery", method = RequestMethod.POST, consumes="application/json", produces="application/json" )
	public  ResponseEntity <Map<String, String>>  mpostQuery(@RequestBody SessionQueryAnswerCareerservicesBean sessionQuery) {
		HttpHeaders headers = new HttpHeaders();
	    headers.add("Content-Type", "application/json");
	    
		// Get Latest Acads Year, month via ExamOrderBean having max order
		HashMap<String, String> query_post_result = new HashMap<>();

		SessionsDAO dao = sessionsDAO;
		FacultyDAO fDao = facultyDAO;

		SessionDayTimeBean session = dao.findScheduledSessionById(sessionQuery.getSessionId());
		
	
		String sapId = (String) sessionQuery.getSapId();
		sessionQuery.setSapId(sapId);
	
		StudentCareerservicesBean student = dao.getSingleStudentsData(sessionQuery.getSapId());;
	
		String queryAssignedToFacultyId = session.getFacultyId();
		sessionQuery.setAssignedToFacultyId(queryAssignedToFacultyId);
		sessionQuery.setCreatedBy(sapId);
		sessionQuery.setLastModifiedBy(sapId);
		long studentZone_QueryId = sessionQueryAnswerDAO.saveQuery(sessionQuery);
		
		if(studentZone_QueryId == 0 ) {
			//if the id is 0 the database request failed.
			query_post_result.put("failure", "Query submission  failed");
			return new ResponseEntity<Map<String, String>>(query_post_result, headers,  HttpStatus.OK);
		}
		
		query_post_result.put("success", "Query submitted successfully");

		sessionQuery.setId(String.valueOf(studentZone_QueryId));

		FacultyCareerservicesBean faculty = fDao.findfacultyByFacultyId(queryAssignedToFacultyId);
		
		MailSender mailSender = (MailSender) act.getBean("mailer");

		// Only Academic Query goes to Faculty
		if ("Academic".equals(sessionQuery.getQueryType())) {
			mailSender.sendQueryPostedEmail(session, sessionQuery, faculty, faculty.getEmail());	
		} else {
			// Create Case In Salesforce
			sessionQuery = salesforceHelper.createCaseInSalesforce(student, sessionQuery, session);
			sessionQueryAnswerDAO.updateSalesforceErrorMessage(sessionQuery);// update CaseId and ErrorMessage in Table
		}
		
		return new ResponseEntity<Map<String, String>>(query_post_result, headers,  HttpStatus.OK);
	}
	
	@RequestMapping(value = "/postQuery", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView postQuery(HttpServletRequest request, HttpServletResponse response, @ModelAttribute SessionQueryAnswerCareerservicesBean sessionQuery) {
		ModelAndView modelnView = new ModelAndView("portal/career_forum/postQuery");
		
		SessionsDAO dao = sessionsDAO;
		FacultyDAO fDao = facultyDAO;

		SessionDayTimeBean session = dao.findScheduledSessionById(sessionQuery.getSessionId());
		modelnView.addObject("session", session);

		String userIdEncrypted = request.getParameter("eid");
		String userIdFromURL = null;
		try {
			if (userIdEncrypted != null) {
				userIdFromURL = AESencrp.decrypt(userIdEncrypted);
			}
		} catch (Exception e) {
			logger.info("in QueryAnswerController class got exception : "+e.getMessage());
		}

		String sapId = (String) request.getSession().getAttribute("userId");
		// login in Acads as student
		if (userIdFromURL != null) {
			sapId = userIdFromURL;
		}

		sessionQuery.setSapId(sapId);

		StudentCareerservicesBean student = (StudentCareerservicesBean) request.getSession().getAttribute("student_careerservices");

		String queryAssignedToFacultyId = session.getFacultyId();
		sessionQuery.setAssignedToFacultyId(queryAssignedToFacultyId);
//		sessionQuery.setYear(session.getYear());
//		sessionQuery.setMonth(session.getMonth());
		sessionQuery.setCreatedBy(sapId);
		sessionQuery.setLastModifiedBy(sapId);
		long studentZone_QueryId = sessionQueryAnswerDAO.saveQuery(sessionQuery);
		
		setSuccess(request, "Query submitted successfully");
		sessionQuery.setId(String.valueOf(studentZone_QueryId));

		FacultyCareerservicesBean faculty = fDao.findfacultyByFacultyId(queryAssignedToFacultyId);
		
		MailSender mailSender = (MailSender) act.getBean("mailer");

		// Only Academic Query goes to Faculty
		if ("Academic".equals(sessionQuery.getQueryType())) {
			mailSender.sendQueryPostedEmail(session, sessionQuery, faculty, faculty.getEmail());	
		} else {
			// Create Case In Salesforce
			sessionQuery = salesforceHelper.createCaseInSalesforce(student, sessionQuery, session);
			sessionQueryAnswerDAO.updateSalesforceErrorMessage(sessionQuery);// update CaseId and ErrorMessage in Table
		}

		modelnView.addObject("sessionQuery", sessionQuery);

		List<SessionQueryAnswerCareerservicesBean> myQueries = getMyQueries(sessionQuery);
		modelnView.addObject("myQueries", myQueries);

		List<SessionQueryAnswerCareerservicesBean> publicQueries = getPublicQueries(sessionQuery);
		modelnView.addObject("publicQueries", publicQueries);

		modelnView.addObject("action", "postQueries");

		return modelnView;
	}

	private List<SessionQueryAnswerCareerservicesBean> getMyQueries(SessionQueryAnswerCareerservicesBean sessionQuery) {
		List<SessionQueryAnswerCareerservicesBean> myQueries = sessionQueryAnswerDAO.getQueriesForSessionByStudent(sessionQuery);

		if (myQueries != null) {
			for (SessionQueryAnswerCareerservicesBean sessionQueryAnswer : myQueries) {
				if ("Y".equals(sessionQueryAnswer.getIsAnswered())) {
					sessionQueryAnswer.setIsAnswered("Y");
				} else {
					sessionQueryAnswer.setAnswer("Not Answered Yet");
					sessionQueryAnswer.setIsAnswered("N");
				}
			}
		}
		return myQueries;
	}

	private List<SessionQueryAnswerCareerservicesBean> getPublicQueries(SessionQueryAnswerCareerservicesBean sessionQuery) {
		List<SessionQueryAnswerCareerservicesBean> publicQueries = sessionQueryAnswerDAO.getPublicQueriesForSession(sessionQuery);

		if (publicQueries != null) {
			for (SessionQueryAnswerCareerservicesBean sessionQueryAnswer : publicQueries) {
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

	@RequestMapping(value = "/viewQueryForm", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView viewQueryForm(HttpServletRequest request, HttpServletResponse response, @RequestParam String id)
			throws Exception {

		ModelAndView modelnView = new ModelAndView("admin/session/respondQuery");

		String userIdEncrypted = request.getParameter("eid");

		String userIdFromURL = null;
		try {
			if (userIdEncrypted != null) {
				userIdFromURL = AESencrp.decrypt(userIdEncrypted);
			}
		} catch ( Throwable throwable ) {
			logger.info("in viewQueryForm got exception : "+ExceptionUtils.getFullStackTrace(throwable));
		}

		if (userIdFromURL != null) {
			loginFaculty(userIdFromURL, request);
		}

		if (!checkSession(request, response)) {
			return new ModelAndView("login");
		}
		String userId = (String) request.getSession().getAttribute("userId");

		PersonCareerservicesBean user = (PersonCareerservicesBean) request.getSession().getAttribute("user_careerservices");

		SessionsDAO dao = sessionsDAO;
		SessionDayTimeBean session = dao.findScheduledSessionById(id);

		SessionQueryAnswerCareerservicesBean sessionQuery = new SessionQueryAnswerCareerservicesBean();
		sessionQuery.setSessionId(id);

		modelnView.addObject("session", session);
		modelnView.addObject("sessionQuery", sessionQuery);

		/*
		 * List<SessionQueryAnswer> allQueries =
		 * sessionQueryAnswerDAO.getQueriesForSessionByFaculty(sessionQuery,userId);
		 */
		List<SessionQueryAnswerCareerservicesBean> allQueries = sessionQueryAnswerDAO.getQueriesForSession(sessionQuery, userId, user);
		modelnView.addObject("allQueries", allQueries);

		List<SessionQueryAnswerCareerservicesBean> unansweredQueries = new ArrayList<SessionQueryAnswerCareerservicesBean>();
		List<SessionQueryAnswerCareerservicesBean> answeredQueries = new ArrayList<SessionQueryAnswerCareerservicesBean>();
		if (allQueries != null) {
			for (SessionQueryAnswerCareerservicesBean sessionQueryAnswer : allQueries) {
				if ("Y".equals(sessionQueryAnswer.getIsAnswered())) {
					answeredQueries.add(sessionQueryAnswer);
				} else {
					unansweredQueries.add(sessionQueryAnswer);
				}
			}
		}

		modelnView.addObject("unansweredQueries", unansweredQueries);
		modelnView.addObject("answeredQueries", answeredQueries);

		modelnView.addObject("unansweredQueriesSize", unansweredQueries.size());
		modelnView.addObject("answeredQueriesSize", answeredQueries.size());

		return modelnView;
	}

	private void loginFaculty(String userIdFromURL, HttpServletRequest request) {
		request.getSession().setAttribute("userId", userIdFromURL);

		LDAPDao dao = (LDAPDao) act.getBean("ldapdao");
		PersonCareerservicesBean person = dao.findPerson(userIdFromURL);
		// Person person = new Person();
		person.setUserId(userIdFromURL);
		request.getSession().setAttribute("user_careerservices", person);
		request.getSession().setAttribute("userId", userIdFromURL);

	}

	@RequestMapping(value = "/saveAnswer", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView saveAnswer(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute SessionQueryAnswerCareerservicesBean sessionQuery) throws Exception {
		String userId = (String) request.getSession().getAttribute("userId");
		MailSender mailSender = (MailSender) act.getBean("mailer");
		SessionsDAO dao = sessionsDAO;

		sessionQuery.setLastModifiedBy(userId);
		sessionQuery.setAnsweredByFacultyId(userId);

		sessionQueryAnswerDAO.updateAnswer(sessionQuery);
		setSuccess(request, "Answer saved successfully");

		StudentCareerservicesBean student = dao.getSingleStudentsData(sessionQuery.getSapId());
		if ("Course Query".equals(sessionQuery.getQueryType())) {
			
		} else {

			// sending notification to Student after Session Query is Answer by Faculty
			SessionDayTimeBean session = dao.findScheduledSessionById(sessionQuery.getSessionId());
			mailSender.sendQueryAnswerPostedEmailToStudent(sessionQuery, student, session);
		}
		// Send Back to Session Query Page
		return viewQueryForm(request, response, sessionQuery.getSessionId());
	}

	@RequestMapping(value = "/searchQueriesForm", method = { RequestMethod.GET, RequestMethod.POST })
	public String searchQueriesForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {

		SessionQueryAnswerCareerservicesBean searchBean = new SessionQueryAnswerCareerservicesBean();
		m.addAttribute("searchBean", searchBean);
		m.addAttribute("yearList", ACAD_YEAR_LIST);

		return "admin/session/searchQuery";
	}

	@RequestMapping(value = "/searchQueries", method = { RequestMethod.POST })
	public ModelAndView searchQueries(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute SessionQueryAnswerCareerservicesBean searchBean) {
		ModelAndView modelnView = new ModelAndView("admin/session/searchQuery");

		PageCareerservicesBean<SessionQueryAnswerCareerservicesBean> page = sessionQueryAnswerDAO.getQueries(1, Integer.MAX_VALUE, searchBean);

		List<SessionQueryAnswerCareerservicesBean> queryList = page.getPageItems();
		request.getSession().setAttribute("queryList", queryList);
		modelnView.addObject("page", page);
		modelnView.addObject("rowCount", page.getRowCount());
		modelnView.addObject("searchBean", searchBean);
		if (queryList == null || queryList.size() == 0) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No Records Found.");
		}
		return modelnView;
	}

	@RequestMapping(value = "/downloadQueries", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView downloadQueries(HttpServletRequest request, HttpServletResponse response) {

		
		List<SessionQueryAnswerCareerservicesBean> queryList = (List<SessionQueryAnswerCareerservicesBean>) request.getSession().getAttribute("queryList");
		return new ModelAndView("queryAnswerExcelView", "queryList", queryList);
	}

	@RequestMapping(value = "/queryTATReportPage", method = RequestMethod.GET)
	public ModelAndView queryTATReportPage(HttpServletRequest request, HttpServletResponse respnse) {
		if (!checkSession(request, respnse)) {
			return new ModelAndView("studentPortalRediret");
		}
		ModelAndView m = new ModelAndView("queryTATReportPage");
		SessionQueryAnswerCareerservicesBean searchBean = new SessionQueryAnswerCareerservicesBean();
		m.addObject("searchBean", searchBean);
		m.addObject("yearList", ACAD_YEAR_LIST);
		
		m.addObject("unansweredQueries", null);
		m.addObject("answeredQueries", null);
		request.getSession().setAttribute("unansweredQueries", null);
		request.getSession().setAttribute("answeredQueries", null);

		m.addObject("unansweredQueriesSize", 0);
		m.addObject("answeredQueriesSize", 0);
		
		return m;
	}

	@RequestMapping(value = "/queryTATReport", method = RequestMethod.POST)
	public ModelAndView queryTATReport(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute SessionQueryAnswerCareerservicesBean searchBean) {
		if (!checkSession(request, response)) {
			return new ModelAndView("studentPortalRediret");
		}

		ModelAndView modelnView = new ModelAndView("queryTATReportPage");
		request.getSession().setAttribute("searchBean", searchBean);

		ArrayList<SessionQueryAnswerCareerservicesBean> allQueries = new ArrayList<SessionQueryAnswerCareerservicesBean>();
		List<SessionQueryAnswerCareerservicesBean> studentsMyQueries = getAllQueriresForPostQueryReport(searchBean);
		allQueries.addAll(studentsMyQueries);
		
		if (allQueries != null && allQueries.size() > 0) {
		}

		modelnView.addObject("allQueries", allQueries);

		List<SessionQueryAnswerCareerservicesBean> unansweredQueries = new ArrayList<SessionQueryAnswerCareerservicesBean>();
		List<SessionQueryAnswerCareerservicesBean> answeredQueries = new ArrayList<SessionQueryAnswerCareerservicesBean>();
		if (allQueries != null) {
			for (SessionQueryAnswerCareerservicesBean sessionQueryAnswer : allQueries) {
				if ("Y".equals(sessionQueryAnswer.getIsAnswered())) {
					answeredQueries.add(sessionQueryAnswer);
				} else {
					unansweredQueries.add(sessionQueryAnswer);
				}
			}
		}

		modelnView.addObject("unansweredQueries", unansweredQueries);
		modelnView.addObject("answeredQueries", answeredQueries);
		request.getSession().setAttribute("unansweredQueries", unansweredQueries);
		request.getSession().setAttribute("answeredQueries", answeredQueries);

		modelnView.addObject("unansweredQueriesSize", unansweredQueries.size());
		modelnView.addObject("answeredQueriesSize", answeredQueries.size());

		modelnView.addObject("searchBean", searchBean);
		modelnView.addObject("yearList", ACAD_YEAR_LIST);

		if (allQueries == null || allQueries.size() == 0) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No records found.");
		}
		return modelnView;
	}
	
	private List<SessionQueryAnswerCareerservicesBean> getAllQueriresForPostQueryReport(SessionQueryAnswerCareerservicesBean searchBean) {
		List<SessionQueryAnswerCareerservicesBean> allQueriesBySapId = sessionQueryAnswerDAO.getAllQueriesForPostQueryReport(searchBean);
		if (allQueriesBySapId != null) {
			for (SessionQueryAnswerCareerservicesBean sessionQueryAnswer : allQueriesBySapId) {
				if ("Y".equals(sessionQueryAnswer.getIsAnswered())) {
					sessionQueryAnswer.setIsAnswered("Y");
				} else {
					sessionQueryAnswer.setAnswer("Not Answered Yet");
					sessionQueryAnswer.setIsAnswered("N");
				}
			}
		}
		return allQueriesBySapId;
	}


	@RequestMapping(value = "/UnAnsweredQueryReport", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView downloadUnAnsweredQueryReport(HttpServletRequest request, HttpServletResponse response) {

		
		List<SessionQueryAnswerCareerservicesBean> queryList = (List<SessionQueryAnswerCareerservicesBean>) request.getSession()
				.getAttribute("unansweredQueries");
		return new ModelAndView("postQueryReportExcelView", "queryList", queryList);
	}

	@RequestMapping(value = "/AnsweredQueryReport", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView downloadAnsweredQueryReport(HttpServletRequest request, HttpServletResponse response) {

		
		List<SessionQueryAnswerCareerservicesBean> queryList = (List<SessionQueryAnswerCareerservicesBean>) request.getSession().getAttribute("answeredQueries");
		return new ModelAndView("postQueryReportExcelView", "queryList", queryList);
	}

	/* Course Query End */


	@RequestMapping(value = "/assignedCourseQueries", method = RequestMethod.GET)
	public ModelAndView assignedCourseQueries(HttpServletRequest request, HttpServletResponse response) {
		if (!checkSession(request, response)) {
			return new ModelAndView("studentPortalRediret");
		}
		ModelAndView modelnView = new ModelAndView("admin/session/respondCourseQuery");
		String facultyId = (String) request.getSession().getAttribute("userId");

		List<SessionQueryAnswerCareerservicesBean> allQueries = sessionQueryAnswerDAO.getAllCourseQueriresByFaculty(facultyId);
		modelnView.addObject("allQueries", allQueries);

		List<SessionQueryAnswerCareerservicesBean> unansweredQueries = new ArrayList<SessionQueryAnswerCareerservicesBean>();
		List<SessionQueryAnswerCareerservicesBean> answeredQueries = new ArrayList<SessionQueryAnswerCareerservicesBean>();
		if (allQueries != null) {
			for (SessionQueryAnswerCareerservicesBean sessionQueryAnswer : allQueries) {
				if ("Y".equals(sessionQueryAnswer.getIsAnswered())) {
					answeredQueries.add(sessionQueryAnswer);
				} else {
					unansweredQueries.add(sessionQueryAnswer);
				}
			}
		}

		modelnView.addObject("unansweredQueries", unansweredQueries);
		modelnView.addObject("answeredQueries", answeredQueries);

		modelnView.addObject("unansweredQueriesSize", unansweredQueries.size());
		modelnView.addObject("answeredQueriesSize", answeredQueries.size());

		modelnView.addObject("sessionQuery", new SessionQueryAnswerCareerservicesBean());

		return modelnView;

	}




/* Course Mobile Api Query End */

//Session Query Post 
//Session Query Get

//Api to retrieve queries for Session Details (Includes Public, Private Session Queries )

	@RequestMapping(value = "/sessionQueryList", method = RequestMethod.POST, consumes="application/json", produces="application/json")
	public ResponseEntity<Map<String, List<SessionQueryAnswerCareerservicesBean>>> msessionQueryList(@RequestBody SessionQueryAnswerCareerservicesBean sessionQuery) throws Exception {
		HttpHeaders headers = new HttpHeaders();
	    headers.add("Content-Type", "application/json");
		Map<String, List<SessionQueryAnswerCareerservicesBean>> reponse= new HashMap<String, List<SessionQueryAnswerCareerservicesBean>>();
		try {
			List<SessionQueryAnswerCareerservicesBean> myQueries = getMyQueries(sessionQuery);
			reponse.put("myQueries", myQueries);
			List<SessionQueryAnswerCareerservicesBean> publicQueries = getPublicQueries(sessionQuery);
			reponse.put("publicQueries", publicQueries);
			return new ResponseEntity<Map<String, List<SessionQueryAnswerCareerservicesBean>>>( reponse, headers,  HttpStatus.OK);
		} catch (EmptyResultDataAccessException e) {
			return new ResponseEntity<Map<String, List<SessionQueryAnswerCareerservicesBean>>>( reponse, headers,  HttpStatus.OK);	
		}
	}

	
}