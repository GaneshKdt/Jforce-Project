package com.nmims.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
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
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import com.nmims.beans.EndPointBean;
import com.nmims.beans.ExamOrderAcadsBean;
import com.nmims.beans.FacultyAcadsBean;
import com.nmims.beans.FacultyCourseMappingBean;
import com.nmims.beans.ForumAcadsBean;
import com.nmims.beans.PageAcads;
import com.nmims.beans.PersonAcads;
import com.nmims.beans.PostMyQueryMBAWXBean;
import com.nmims.beans.ProgramBean;
import com.nmims.beans.ProgramSubjectMappingAcadsBean;
import com.nmims.beans.QueryAnswerListBean;
import com.nmims.beans.SessionAttendanceFeedbackAcads;
import com.nmims.beans.SessionDayTimeAcadsBean;
import com.nmims.beans.SessionQueryAnswer;
import com.nmims.beans.StudentAcadsBean;
import com.nmims.daos.ContentDAO;
import com.nmims.daos.FacultyDAO;
import com.nmims.daos.ForumDAO;
import com.nmims.daos.LDAPDao;
import com.nmims.daos.SessionQueryAnswerDAO;
import com.nmims.daos.TimeTableDAO;
import com.nmims.helpers.AESencrp;
import com.nmims.helpers.ConferenceBookingClient;
import com.nmims.helpers.MailSender;
import com.nmims.helpers.SMSSender;
import com.nmims.helpers.SalesforceHelper;
import com.nmims.helpers.SubjectAbbreviationHelper;
import com.nmims.helpers.WebExMeetingManager;
import com.nmims.services.ContentService;
import com.nmims.services.QnAOfLiveSessionsService;
import com.nmims.services.QueryAnswerService;
import com.nmims.util.ContentUtil;

@Controller
@CrossOrigin(origins="*", allowedHeaders="*")
public class QueryAnswerController extends BaseController {

	@Autowired(required = false)
	ApplicationContext act;

	@Autowired
	private SessionQueryAnswerDAO sessionQueryAnswerDAO;
	@Autowired
	private ConferenceBookingClient conferenceBookingClient;
	@Autowired
	private WebExMeetingManager webExManager;

	@Autowired
	private SalesforceHelper salesforceHelper;
	
	@Autowired
	private QnAOfLiveSessionsService qnaOfLiveSessionsService;
	
	@Autowired
	private SMSSender smsSender;
	
	@Autowired
	SubjectAbbreviationHelper subjectAbbreviationHelper;
	
	@Value( "${SERVER_PATH}" )
	private String SERVER_PATH;

	@Value("${WEB_EX_API_URL}")
	private String WEB_EX_API_URL;

	@Value("${WEB_EX_LOGIN_API_URL}")
	private String WEB_EX_LOGIN_API_URL;

	@Value("${WEBEX_ID}")
	private String WEBEX_ID;

	@Value("${WEBEX_PASS}")
	private String WEBEX_PASS;
	
	
	@Value("${MAX_WEBEX_USERS}")
	private int MAX_WEBEX_USERS2;
	private int MAX_WEBEX_USERS=2000;

	@Value("${CURRENT_ACAD_MONTH}")
	private String CURRENT_ACAD_MONTH;

	@Value("${CURRENT_ACAD_YEAR}")
	private int CURRENT_ACAD_YEAR;

	@Value("#{'${ACAD_YEAR_LIST}'.split(',')}")
	private List<String> ACAD_YEAR_LIST;
	
	@Value("${SERVER}")
	private String SERVER;
	
	@Value("${ENVIRONMENT}")
	private String ENVIRONMENT;

	@Autowired
	private ContentService contentService;
	
	@Autowired
	QnAOfLiveSessionsService qnaService;
	
	@Autowired
	ForumDAO fDao;
	
	@Autowired
	QueryAnswerService queryAnswerService;
	
	private ArrayList<String> programList = null;
	private ArrayList<String> subjectList = null;

	private ArrayList<String> facultyList = null;
	private ArrayList<String> sessionList = null;	
	private ArrayList<EndPointBean> endPointList = null;
	private HashMap<String, String> mapOfProgramCodeAndMasterKey = null;
	
	private final int pageSize = 10;
	private final int postMBAWXPageSize = 500;
	private static final Logger logger = LoggerFactory.getLogger(QueryAnswerController.class);
	
	private static final Logger logger1 = LoggerFactory.getLogger("queryAnswerService");

	private ArrayList<String> monthList = new ArrayList<String>(Arrays.asList("Jan", "Apr", "Jul", "Sep", "Dec"));

	private ArrayList<String> yearList = new ArrayList<String>(
			Arrays.asList("2015", "2016", "2017", "2018", "2019", "2020"));
	private HashMap<String, ProgramSubjectMappingAcadsBean> subjectProgramMap = null;
	
	private ArrayList<String> nonPG_ProgramList = new ArrayList<String>(Arrays.asList("BBA", "B.Com", "PD - WM","PD - DM",
			"M.Sc. (App. Fin.)", "CP-WL", "BBA-BA"));

	private HashMap<String, ArrayList<String>> programAndProgramStructureAndSubjectsMap = new HashMap<>();

	public ArrayList<ProgramSubjectMappingAcadsBean> getSubjectProgramList() {
		TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
		ArrayList<ProgramSubjectMappingAcadsBean> subjectProgramList = dao.getSubjectProgramList();
		return subjectProgramList;
	}

	@ModelAttribute("subjectList")
	public ArrayList<String> getSubjectList() {
		if (this.subjectList == null) {
			TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
			this.subjectList = dao.getAllSubjects();
		}
		return subjectList;
	}

	public ArrayList<String> getProgramList() {
		if (this.programList == null) {
			TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
			this.programList = dao.getAllPrograms();
		}
		return programList;
	}

	public ArrayList<String> getFacultyList() {
		// if(this.facultyList == null){
		TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
		this.facultyList = dao.getAllFaculties();
		// }
		return facultyList;
	}

	public ArrayList<String> getSessionList() {
		// if(this.facultyList == null){
		TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
		this.sessionList = dao.getAllSessions();
		// }
		return sessionList;
	}

	public ArrayList<EndPointBean> getEndPointList() {
		if (this.endPointList == null) {
			TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
			this.endPointList = dao.getAllFacultyRoomEndPoints();
		}
		return endPointList;
	}
	
	public HashMap<String, String> mapOfProgramCodeAndMasterKey(){
		if (mapOfProgramCodeAndMasterKey == null) {
			List<ProgramBean> programAndMasterKey = sessionQueryAnswerDAO.getProgramCodeAndMaterKeyList();
			for (ProgramBean bean : programAndMasterKey) {
				this.mapOfProgramCodeAndMasterKey.put(bean.getId(), bean.getCode());
			}
		}
		return mapOfProgramCodeAndMasterKey;
	}

	@RequestMapping(value = "/admin/viewAllUnAnsweredQueriesForm", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView viewAllUnAnsweredQueriesForm() {
		ModelAndView mav = new ModelAndView("viewAllQueries");
		FacultyDAO fDao = (FacultyDAO) act.getBean("facultyDAO");
		List<FacultyAcadsBean> listOfFaculty = new ArrayList<FacultyAcadsBean>();
		listOfFaculty = fDao.getAllFacultyRecords();
		mav.addObject("queryAnswer", new SessionQueryAnswer());
		mav.addObject("yearList", ACAD_YEAR_LIST);
		mav.addObject("listOfFaculty", listOfFaculty);
		return mav;
	}

	@RequestMapping(value = "/admin/allocateFacultyToQueries", method = { RequestMethod.POST })
	public ModelAndView allocateFacultyToQueries(@ModelAttribute SessionQueryAnswer allocateAnswer,
			HttpServletRequest request) {
		String userId = (String) request.getSession().getAttribute("userId_acads");
		try {
			allocateAnswer.setLastModifiedBy(userId);
			sessionQueryAnswerDAO.allocateFacultyToAnswer(allocateAnswer.getListOfRecordIdToBeAssigned(),
					allocateAnswer);
			setSuccess(request, "Allocated Users Successfully");
			return viewAllUnAnsweredQueriesForm();
		} catch (Exception e) {
			  
			setError(request, "Error in allocating");
			return viewAllUnAnsweredQueriesForm();
		}

	}

	@RequestMapping(value = "/admin/viewAllUnAnsweredQueries", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView viewAllUnAnsweredQueries(@ModelAttribute SessionQueryAnswer queryAnswer,
			HttpServletRequest request) {
		List<SessionQueryAnswer> listOfSessionQueryAnswerForMonthAndYear = sessionQueryAnswerDAO
				.getListOfSessionQueryAnswerForMonthAndYear(queryAnswer);
		ModelAndView mav = new ModelAndView("viewAllQueries");
		SessionQueryAnswer allocateAnswer = new SessionQueryAnswer();

		allocateAnswer.setFacultyId(queryAnswer.getFacultyId());
		int rowCount = 0;
		if (listOfSessionQueryAnswerForMonthAndYear != null && listOfSessionQueryAnswerForMonthAndYear.size() > 0) {
			rowCount = listOfSessionQueryAnswerForMonthAndYear.size();
			mav.addObject("listOfSessionQueryAnswerForMonthAndYear", listOfSessionQueryAnswerForMonthAndYear);
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

	@RequestMapping(value = "/api/updatePostMyQueryFromSalesforce", method = { RequestMethod.GET, RequestMethod.POST })
	public void updatePostMyQueryFromSalesforce(HttpServletRequest request, HttpServletResponse response) {
		String id = request.getParameter("id");
		String answer = request.getParameter("answer");
		MailSender mailSender = (MailSender) act.getBean("mailer");
		TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
		dao.updateSessionQueryAnsById(id, answer);

		SessionQueryAnswer sessionQuery = dao.findSessionQueryAnswerById(id);
		SessionDayTimeAcadsBean session = dao.findScheduledSessionById(sessionQuery.getSessionId());
		// sending notification to Student after Query is Answer by Faculty
		StudentAcadsBean student = dao.getSingleStudentsData(sessionQuery.getSapId());
		mailSender.sendQueryAnswerPostedEmailToStudent(sessionQuery, student, session);
	}

	@RequestMapping(value = "/student/postQueryForm", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView postQueryForm(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(name = "statusNo", required = false,defaultValue = "0") Integer statusNo) throws Exception {

		ModelAndView modelnView = new ModelAndView("postQuery");
		if (!checkSession(request, response)) {
			return new ModelAndView("studentPortalRediret");
		}
		String sapId = (String) request.getSession().getAttribute("userId_acads");
		
		if(statusNo == 1) {
			setError(request, "This Query Already asked by you please check in My Queries Tab.");
		}else if(statusNo == 2) {
			setSuccess(request, "Query submitted successfully");
		}else if(statusNo == 3) {
			setError(request, "Unable to submit your query. Please try again");
		}
		
		String action = request.getParameter("action");
		modelnView.addObject("action", action);

		String id = request.getParameter("id");
		String pssId = request.getParameter("pssId");
		TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
		SessionDayTimeAcadsBean session = dao.findScheduledSessionById(id);
		SessionQueryAnswer sessionQuery = new SessionQueryAnswer();
		sessionQuery.setProgramSemSubjectId(pssId);
		sessionQuery.setSessionId(id);
		sessionQuery.setSapId(sapId);
		
		modelnView.addObject("session", session);
		modelnView.addObject("sessionQuery", sessionQuery);

		List<SessionQueryAnswer> myQueries = getMyQueries(sessionQuery);
		modelnView.addObject("myQueries", myQueries);

		List<SessionQueryAnswer> publicQueries = getPublicQueries(sessionQuery);
		modelnView.addObject("publicQueries", publicQueries);

		return modelnView;
	}
	
	@RequestMapping(value = "/student/postQuery", method = { RequestMethod.GET, RequestMethod.POST })
	public RedirectView postQuery(RedirectAttributes  model,HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute SessionQueryAnswer sessionQuery) {
		
		TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
		FacultyDAO fDao = (FacultyDAO) act.getBean("facultyDAO");
		model.addAttribute("id", sessionQuery.getSessionId());
		model.addAttribute("action", "postQueries");
		String pssId=sessionQuery.getProgramSemSubjectId();
		
		SessionDayTimeAcadsBean session = dao.findScheduledSessionById(sessionQuery.getSessionId());

		String userIdEncrypted = request.getParameter("eid");
		String userIdFromURL = null;
		try {
			if (userIdEncrypted != null) {
				userIdFromURL = AESencrp.decrypt(userIdEncrypted);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		String sapId = (String) request.getSession().getAttribute("userId_acads");
		// login in Acads as student
		if (userIdFromURL != null) {
			sapId = userIdFromURL;
		}

		sessionQuery.setSapId(sapId);

		StudentAcadsBean student = (StudentAcadsBean) request.getSession().getAttribute("student_acads");

		String queryAssignedToFacultyId = null;

		String acadDateFormat = ContentUtil.prepareAcadDateFormat(session.getMonth(), session.getYear());
		SessionAttendanceFeedbackAcads attendance = dao.checkSessionAttendance(sapId, session.getId(), acadDateFormat);

		if (attendance != null) {
			// Student had attended session, so allocate it to Faculty whose session was
			// attended
			queryAssignedToFacultyId = attendance.getFacultyId();

		} else {
			// Student did not attend session, allocate it to First Faculty
			queryAssignedToFacultyId = session.getFacultyId();
		}
		sessionQuery.setAssignedToFacultyId(queryAssignedToFacultyId);
		sessionQuery.setSubject(session.getSubject());
		sessionQuery.setYear(session.getYear());
		sessionQuery.setMonth(session.getMonth());
		sessionQuery.setCreatedBy(sapId);
		sessionQuery.setLastModifiedBy(sapId);
		
		if("Orientation".equals(sessionQuery.getSubject()) || "Assignment".equals(sessionQuery.getSubject())) {
			pssId="0";
		}
		
		model.addAttribute("pssId", pssId);
		
		sessionQuery.setProgramSemSubjectId(pssId);
		
		
		Integer count = sessionQueryAnswerDAO.checkForSameAskQuery(sessionQuery);
		if(count > 0) {
			model.addAttribute("statusNo", "1");
			return new RedirectView("postQueryForm");
		}
		
		try {
		long studentZone_QueryId = sessionQueryAnswerDAO.saveQuery(sessionQuery);
		model.addAttribute("statusNo", "2");
		sessionQuery.setId(String.valueOf(studentZone_QueryId));
		}catch(Exception e) {
			  
			model.addAttribute("statusNo", "3");
			return new RedirectView("postQueryForm");
		}
		

		FacultyAcadsBean faculty = fDao.findfacultyByFacultyId(queryAssignedToFacultyId);

		MailSender mailSender = (MailSender) act.getBean("mailer");
		
		if("Orientation".equals(sessionQuery.getSubject()) || "Assignment".equals(sessionQuery.getSubject())) {
			
			mailSender.sendQueryPostedEmail(session, sessionQuery, faculty, faculty.getEmail());
		// Only Academic Query goes to Faculty
		
		}else {

			
			if ("Academic".equals(sessionQuery.getQueryType())) {
				
				HashMap<String,ProgramSubjectMappingAcadsBean> programSubjectPassingConfigurationMap = dao.getProgramSubjectPassingConfigurationMap();
				String programSubjectProgramStructureKey = student.getProgram()+"-"+session.getSubject()+"-"+student.getPrgmStructApplicable();
				ProgramSubjectMappingAcadsBean passingConfiguration = programSubjectPassingConfigurationMap.get(programSubjectProgramStructureKey);
				
				//Added by steffi to create case in salesforce instead of sending email for VA/EMiner/EGuide.
				if("Y".equalsIgnoreCase(passingConfiguration.getCreateCaseForQuery())){
					// Create Case In Salesforce
					sessionQuery = salesforceHelper.createCaseInSalesforce(student, sessionQuery, session);
					sessionQueryAnswerDAO.updateSalesforceErrorMessage(sessionQuery);// update CaseId and ErrorMessage in Table
				}else{
					mailSender.sendQueryPostedEmail(session, sessionQuery, faculty, faculty.getEmail());
				}
				
			} else {
				// Create Case In Salesforce
				sessionQuery = salesforceHelper.createCaseInSalesforce(student, sessionQuery, session);
				sessionQueryAnswerDAO.updateSalesforceErrorMessage(sessionQuery);// update CaseId and ErrorMessage in Table
			}
			
		}


		return new RedirectView("postQueryForm");
	}

	private List<SessionQueryAnswer> getMyQueries(SessionQueryAnswer sessionQuery) {
		List<SessionQueryAnswer> myQueries = sessionQueryAnswerDAO.getQueriesForSessionByStudent(sessionQuery);
		if (myQueries != null) {
			for (SessionQueryAnswer sessionQueryAnswer : myQueries) {
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

	private List<SessionQueryAnswer> getPublicQueries(SessionQueryAnswer sessionQuery) {
		List<SessionQueryAnswer> publicQueries = sessionQueryAnswerDAO.getPublicQueriesForSession(sessionQuery);
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

	@RequestMapping(value = "/admin/viewQueryForm", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView viewQueryForm(HttpServletRequest request, HttpServletResponse response, @RequestParam String id)
			throws Exception {

		ModelAndView modelnView = new ModelAndView("respondQuery");

		String userIdEncrypted = request.getParameter("eid");
		String userIdFromURL = null;
		try {
			if (userIdEncrypted != null) {
				userIdFromURL = AESencrp.decrypt(userIdEncrypted);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		if (userIdFromURL != null) {
			loginFaculty(userIdFromURL, request);
		}

		if (!checkSession(request, response)) {
			return new ModelAndView("login");
		}
		String userId = (String) request.getSession().getAttribute("userId_acads");

		PersonAcads user = (PersonAcads) request.getSession().getAttribute("user_acads");

		TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
		SessionDayTimeAcadsBean session = dao.findScheduledSessionById(id);

		SessionQueryAnswer sessionQuery = new SessionQueryAnswer();
		sessionQuery.setSessionId(id);

		modelnView.addObject("session", session);
		modelnView.addObject("sessionQuery", sessionQuery);

		/*
		 * List<SessionQueryAnswer> allQueries =
		 * sessionQueryAnswerDAO.getQueriesForSessionByFaculty(sessionQuery,userId);
		 */
		List<SessionQueryAnswer> allQueries = sessionQueryAnswerDAO.getQueriesForSession(sessionQuery, userId, user);
		modelnView.addObject("allQueries", allQueries);

		List<SessionQueryAnswer> unansweredQueries = new ArrayList<SessionQueryAnswer>();
		List<SessionQueryAnswer> answeredQueries = new ArrayList<SessionQueryAnswer>();
		if (allQueries != null) {
			for (SessionQueryAnswer sessionQueryAnswer : allQueries) {
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
		request.getSession().setAttribute("userId_acads", userIdFromURL);

		LDAPDao dao = (LDAPDao) act.getBean("ldapdao");
		PersonAcads person = dao.findPerson(userIdFromURL);
		// Person person = new Person();
		person.setUserId(userIdFromURL);
		request.getSession().setAttribute("user_acads", person);
		request.getSession().setAttribute("userId_acads", userIdFromURL);

	}

	@RequestMapping(value = "/admin/saveAnswer", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView saveAnswer(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute SessionQueryAnswer sessionQuery) throws Exception {
		String userId = (String) request.getSession().getAttribute("userId_acads");
		MailSender mailSender = (MailSender) act.getBean("mailer");
		TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");

		sessionQuery.setLastModifiedBy(userId);
		sessionQuery.setAnsweredByFacultyId(userId);
		sessionQueryAnswerDAO.updateAnswer(sessionQuery);
		setSuccess(request, "Answer saved successfully");

		StudentAcadsBean student = dao.getSingleStudentsData(sessionQuery.getSapId());
		if ("Course Query".equals(sessionQuery.getQueryType())) {

			// sending notification to Student after Course Query is Answer by Faculty
			mailSender.sendCourseQueryAnswerPostedEmailToStudent(sessionQuery, student);
			return new ModelAndView("query/selfClose");// Send back to Course Query Page
		} 
		else if("WX_Academic".equalsIgnoreCase(sessionQuery.getQueryType())) {
			mailSender.sendWXQueryAnswerPostedEmailToStudent(sessionQuery, student);
			return new ModelAndView("query/selfClose");// Send back to Course Query Page
		}
		else {
			// sending notification to Student after Session Query is Answer by Faculty
			SessionDayTimeAcadsBean session = dao.findScheduledSessionById(sessionQuery.getSessionId());
			mailSender.sendQueryAnswerPostedEmailToStudent(sessionQuery, student, session);
		}
		// Send Back to Session Query Page	
		//return viewQueryForm(request, response, sessionQuery.getSessionId());
		return new ModelAndView("query/selfClose");
	}

	@RequestMapping(value = "/admin/searchQueriesForm", method = { RequestMethod.GET, RequestMethod.POST })
	public String searchQueriesForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {

		SessionQueryAnswer searchBean = new SessionQueryAnswer();
		m.addAttribute("searchBean", searchBean);
		m.addAttribute("yearList", ACAD_YEAR_LIST);

		return "searchQuery";
	}

	@RequestMapping(value = "/admin/searchQueries", method = { RequestMethod.POST })
	public ModelAndView searchQueries(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute SessionQueryAnswer searchBean) {
		ModelAndView modelnView = new ModelAndView("searchQuery");

		PageAcads<SessionQueryAnswer> page = sessionQueryAnswerDAO.getQueries(1, Integer.MAX_VALUE, searchBean);

		List<SessionQueryAnswer> queryList = page.getPageItems();
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

	@RequestMapping(value = "/admin/downloadQueries", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView downloadQueries(HttpServletRequest request, HttpServletResponse response) {

		List<SessionQueryAnswer> queryList = (List<SessionQueryAnswer>) request.getSession().getAttribute("queryList");
		return new ModelAndView("queryAnswerExcelView", "queryList", queryList);
	}

	/* Course Query Start */

	@RequestMapping(value = "/student/courseQueryForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView courseQueryForm(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("subject") String subject,
			@RequestParam(name = "statusNo", required = false,defaultValue = "0") Integer statusNo
			) throws Exception {

		ModelAndView modelnView = new ModelAndView("/courseQueries");
		List<FacultyAcadsBean> facultyForQuery = new ArrayList<>();
		
		if(statusNo == 1) {
			setError(request, "Error in posting query try again later.");
		}else if(statusNo == 2) {
			setError(request, "Faculty Not Yet Assgined To This Course Try Again Later");
		}else if(statusNo == 3) {
			setError(request, "Faculty Not Yet Assgined To This Course Try Again Later");
		}else if(statusNo == 4) {
			setSuccess(request, "Query submitted successfully");
		}else if(statusNo == 5) {
			setError(request, "Unable to submit your query. Please try again");
		}else if(statusNo == 6) {
			setError(request, "This Query Already asked by you please check in My Queries Tab.");
		}
		
		if (!checkSession(request, response)) {
			return new ModelAndView("studentPortalRediret");
		}
		String sapId = (String) request.getSession().getAttribute("userId_acads");
		String pssId = request.getParameter("pssId");
		StudentAcadsBean student = (StudentAcadsBean) request.getSession().getAttribute("student_acads");
		StudentAcadsBean studentRegData = (StudentAcadsBean)request.getSession().getAttribute("studentRegData");
		
		String currentYearMonthArr[] = contentService.getRecordedvsLiveCurrentYearMonthForSapid(studentRegData);
		
		String currentAcadYear = currentYearMonthArr[0];
		String currentAcadMonth = currentYearMonthArr[1];

		facultyForQuery = sessionQueryAnswerDAO.getFacultyForASubject(subject,student.getConsumerProgramStructureId(),
				currentAcadYear, currentAcadMonth
				);
		
		if (facultyForQuery.size() == 0) {
			setError(request, "Faculty Not Yet Assgined To This Course");
		}
		modelnView.addObject("programSemSubjectId", pssId);
		modelnView.addObject("facultyForQuery", facultyForQuery);		

		return getModelNViewCommonForFormNPostCourseQuery(modelnView, sapId, subject,student.getConsumerProgramStructureId());
	}

	@RequestMapping(value = "/student/postCourseQuery", method = { RequestMethod.GET, RequestMethod.POST})
	public RedirectView  postCourseQuery(RedirectAttributes  model,HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute SessionQueryAnswer sessionQuery) {
		String sapId = (String) request.getSession().getAttribute("userId_acads");
		model.addAttribute("subject",  sessionQuery.getSubject());

		// Get Latest Acads Year, month via ExamOrderBean having max order
		ExamOrderAcadsBean examorderBean = sessionQueryAnswerDAO.getExamOrderBeanWhereContentLive();
		
		String pssId=sessionQuery.getProgramSemSubjectId();
		model.addAttribute("pssId", pssId);
		
		if (examorderBean == null) {
			model.addAttribute("statusNo", "1");
			return new RedirectView("courseQueryForm");
		}
		
		

		// Get facultyId of faculty that query will be assigned to
		
		String facultyId = sessionQuery.getFacultyId();
		sessionQuery.setFacultyId(facultyId);
		
		if (StringUtils.isBlank(facultyId)) {
			facultyId = sessionQueryAnswerDAO.getFaultyIdToAnswerCourseQuery(sessionQuery.getSubject());
			model.addAttribute("statusNo", "2");
			return new RedirectView("courseQueryForm");
		}
		
		
		if (facultyId == null) {
			model.addAttribute("statusNo", "3");
			return new RedirectView("courseQueryForm");
		}
		sessionQuery.setFacultyId(facultyId);
		sessionQuery.setAssignedToFacultyId(facultyId);
		sessionQuery.setSapId(sapId);
		sessionQuery.setQueryType("Course Query");
		
		sessionQuery.setYear(String.valueOf(examorderBean.getYear()));
		if(twoAcadCycleCourses(request)) {
			StudentAcadsBean studentReg=(StudentAcadsBean) request.getSession().getAttribute("studentRegData");
			sessionQuery.setYear(studentReg.getYear());
			sessionQuery.setMonth(studentReg.getMonth());
		}else {
			sessionQuery.setYear(String.valueOf(examorderBean.getYear()));
			sessionQuery.setMonth(examorderBean.getAcadMonth());
		}
		
		long studentZone_QueryId;
		try {
		    Integer count = sessionQueryAnswerDAO.checkForSameAskQuery(sessionQuery);
			if(count > 0) {
				model.addAttribute("statusNo", "6");
				return new RedirectView("courseQueryForm");
			}
		    studentZone_QueryId = sessionQueryAnswerDAO.saveQuery(sessionQuery);
			model.addAttribute("statusNo", "4");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			
			model.addAttribute("statusNo", "5");
			return new RedirectView("courseQueryForm");
		}

		sessionQuery.setId(String.valueOf(studentZone_QueryId));

		FacultyDAO fDao = (FacultyDAO) act.getBean("facultyDAO");
		TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
		
		MailSender mailSender = (MailSender) act.getBean("mailer");
		
		StudentAcadsBean student = (StudentAcadsBean) request.getSession().getAttribute("student_acads");
		
		try {
			FacultyAcadsBean faculty = fDao.findfacultyByFacultyId(facultyId);
			HashMap<String,ProgramSubjectMappingAcadsBean> programSubjectPassingConfigurationMap = dao.getProgramSubjectPassingConfigurationMap();
			String programSubjectProgramStructureKey = student.getProgram()+"-"+sessionQuery.getSubject()+"-"+student.getPrgmStructApplicable();
			ProgramSubjectMappingAcadsBean passingConfiguration = programSubjectPassingConfigurationMap.get(programSubjectProgramStructureKey);
			//Added by steffi to create case in salesforce instead of sending email for VA/EMiner/EGuide/ABC.
			if("Y".equalsIgnoreCase(passingConfiguration.getCreateCaseForQuery())){
				// Create Case In Salesforce
				sessionQuery = salesforceHelper.createCaseInSalesforce(student, sessionQuery);
				sessionQueryAnswerDAO.updateSalesforceErrorMessage(sessionQuery);// update CaseId and ErrorMessage in Table
			}else{
			mailSender.sendCourseQueryPostedEmail(sessionQuery, faculty.getEmail());
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			  
		}
		return new RedirectView("courseQueryForm") ;
	}

	private ModelAndView getModelNViewCommonForFormNPostCourseQuery(ModelAndView modelnView, String sapId,
			String subject, String consumerProgramStructureId  ) {

		modelnView.addObject("sessionQuery", new SessionQueryAnswer());
		modelnView.addObject("action", "postCourseQueries");
		modelnView.addObject("subject", subject);

		List<SessionQueryAnswer> myQueries = getMyCourseQueries(sapId, subject);
		modelnView.addObject("myQueries", myQueries);

		List<SessionQueryAnswer> publicQueries = getPublicCourseQueries(sapId, subject, consumerProgramStructureId);
		modelnView.addObject("publicQueries", publicQueries);

		return modelnView;
	}

	private List<SessionQueryAnswer> getMyCourseQueries(String sapId, String subject) {
		List<SessionQueryAnswer> myQueries = sessionQueryAnswerDAO.getQueriesForCourseByStudent(sapId, subject);
		if (myQueries != null) {
			for (SessionQueryAnswer sessionQueryAnswer : myQueries) {
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
	

	private List<SessionQueryAnswer> getPublicCourseQueries(String sapId, String Subject, String consumerProgramStructureId ) {
		List<SessionQueryAnswer> publicQueries = sessionQueryAnswerDAO.getPublicQueriesForCourse(sapId, Subject, consumerProgramStructureId);
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

	private List<SessionQueryAnswer> getAllQueriresForPostQueryReport(SessionQueryAnswer searchBean,
			String authorizedCenterCodes) {
		List<SessionQueryAnswer> allQueriesBySapId = sessionQueryAnswerDAO.getAllQueriesForPostQueryReport(searchBean,
				authorizedCenterCodes);
		if (allQueriesBySapId != null) {
			for (SessionQueryAnswer sessionQueryAnswer : allQueriesBySapId) {
				if ("Y".equals(sessionQueryAnswer.getIsAnswered())) {
					sessionQueryAnswer.setIsAnswered("Y");
					if(sessionQueryAnswer.getAnswer()!=null) {
						if(sessionQueryAnswer.getAnswer().contains("<a href")) {
						sessionQueryAnswer.setHasAttachment("Yes");
						}else {
						sessionQueryAnswer.setHasAttachment("No");
						}
					}
				} else {
					sessionQueryAnswer.setAnswer("Not Answered Yet");
					sessionQueryAnswer.setIsAnswered("N");
				}
			}
		}
		return allQueriesBySapId;
	}
	
	private List<SessionQueryAnswer> getAllQnAReport(SessionQueryAnswer searchBean) {
		List<SessionQueryAnswer> allQueriesBySapId = sessionQueryAnswerDAO.getAllQnAReport(searchBean);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (allQueriesBySapId != null) {
			for (SessionQueryAnswer sessionQueryAnswer : allQueriesBySapId) {
				if ("Answered".equals(sessionQueryAnswer.getIsAnswered())) {
					try {
						Date date1 = simpleDateFormat.parse(sessionQueryAnswer.getQacreatedDate());   
						Date date2 = simpleDateFormat.parse(sessionQueryAnswer.getDateModified());
						long time_difference = date2.getTime() - date1.getTime();
						long days_difference = (time_difference / (1000*60*60*24)) % 365;
						sessionQueryAnswer.setDateSinceNotAnswered(days_difference + "");
					}
					catch (Exception e) {
						  
					}
					sessionQueryAnswer.setIsAnswered("Y");
				} else {
					try {
						Date date1 = simpleDateFormat.parse(sessionQueryAnswer.getQacreatedDate());   
						Date date2 = simpleDateFormat.parse(simpleDateFormat.format(new Date()));
						long time_difference = date2.getTime() - date1.getTime();
						long days_difference = (time_difference / (1000*60*60*24)) % 365;
						sessionQueryAnswer.setDateSinceNotAnswered(days_difference + "");
					}
					catch (Exception e) {
						  
					}
					sessionQueryAnswer.setAnswer("Not Answered Yet");
					sessionQueryAnswer.setIsAnswered("N");
				}
			}
		}
		return allQueriesBySapId;
	}

	@RequestMapping(value = "/admin/assignedCourseQueries", method = RequestMethod.GET)
	public ModelAndView assignedCourseQueries(HttpServletRequest request, HttpServletResponse response) {
		if (!checkSession(request, response)) {
			return new ModelAndView("studentPortalRediret");
		}
		ModelAndView modelnView = new ModelAndView("respondCourseQuery");
		String facultyId = (String) request.getSession().getAttribute("userId_acads");
		String status = request.getParameter("status");
	
		if(status!=null && status.equals("1")) {
			setSuccess(request, "Answer saved successfully");
		}
		
		try {
			QueryAnswerListBean allQueriesListBean=queryAnswerService.getAssignedqueriesForFaculty(nonPG_ProgramList, facultyId);
			
			modelnView.addObject("unansweredWXQueries", allQueriesListBean.getUnansweredWXQueries());
			modelnView.addObject("answeredWXQueries", allQueriesListBean.getAnsweredWXQueries());
			
			modelnView.addObject("unansweredQueries", allQueriesListBean.getUnansweredQueries());
			modelnView.addObject("answeredQueries", allQueriesListBean.getAnsweredQueries());

			modelnView.addObject("unansweredQueriesSize", allQueriesListBean.getUnansweredQueries().size());
			modelnView.addObject("answeredQueriesSize", allQueriesListBean.getAnsweredQueries().size());
			
			modelnView.addObject("unansweredWXQueriesSize", allQueriesListBean.getUnansweredWXQueries().size());
			modelnView.addObject("answeredWXQueriesSize", allQueriesListBean.getAnsweredWXQueries().size());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			setError(request, "Something went wrong. Unable to fetch assigned queries!");
			StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            logger1.info("Error while getting Queries for faculty : "+errors.toString() +" "+facultyId);
		}
//
//		List<SessionQueryAnswer> allQueries = sessionQueryAnswerDAO.getAllCourseQueriresByFaculty(facultyId);
//		modelnView.addObject("allQueries", allQueries);
//		List<SessionQueryAnswer> unansweredQueries = new ArrayList<SessionQueryAnswer>();
//		List<SessionQueryAnswer> answeredQueries = new ArrayList<SessionQueryAnswer>();
//		List<SessionQueryAnswer> unansweredWXQueries = new ArrayList<SessionQueryAnswer>();
//		List<SessionQueryAnswer> answeredWXQueries = new ArrayList<SessionQueryAnswer>();
//		HashMap<String, StudentAcadsBean> mapOfStudentAcadsBean=new HashMap<String, StudentAcadsBean>();
//		
//		if (allQueries != null) {
//			for (SessionQueryAnswer sessionQueryAnswer : allQueries) {
//
//				if ("N".equals(sessionQueryAnswer.getHasTimeBoundId())) {
//
//					if (sessionQueryAnswer.getSapId() != null) {
//						StudentAcadsBean studentBean=new StudentAcadsBean();
//						if (mapOfStudentAcadsBean.containsKey(sessionQueryAnswer.getSapId())) {
//							studentBean=mapOfStudentAcadsBean.get(sessionQueryAnswer.getSapId());
//						} else {
//							studentBean = sessionQueryAnswerDAO.getStudentDataBySapId(sessionQueryAnswer.getSapId());
//							mapOfStudentAcadsBean.put(sessionQueryAnswer.getSapId(), studentBean);
//						}
//
//						if (!nonPG_ProgramList.contains(studentBean.getProgram())) {
//							String enrollement = ContentUtil.prepareAcadDateFormat(studentBean.getEnrollmentMonth(),
//							studentBean.getEnrollmentYear());
//							try {
//								SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//								Date date;
//								date = formatter.parse(enrollement);
//								Date dateCompare = formatter.parse("2021-07-01");
//								
//								if (date.compareTo(dateCompare) >= 0) {
//									//student after Jul2021 enrollment
//									boolean check = sessionQueryAnswerDAO.checkForPaidStudentOnsapIdAndPssId(sessionQueryAnswer.getSapId(), sessionQueryAnswer.getProgramSemSubjectId());
//									if (check) {
//										sessionQueryAnswer.setIsLiveAccess("Y");
//									} else {
//										sessionQueryAnswer.setIsLiveAccess("N");
//									}
//								} 
////								else {
////									//student before Jul2021 enrollment
////									sessionQueryAnswer.setIsLiveAccess("Y");
////								}
//							} catch (Exception e) {
//								// TODO: handle exception
//							}
//						} 
////						else {
////							//for non-PG program
////							sessionQueryAnswer.setIsLiveAccess("Y");
////						}
//					}
//				}
//				
//				if("Y".equalsIgnoreCase(sessionQueryAnswer.getHasTimeBoundId())) {
//					if ("Y".equals(sessionQueryAnswer.getIsAnswered())) {
//						answeredWXQueries.add(sessionQueryAnswer);
//					} else {
//						unansweredWXQueries.add(sessionQueryAnswer);
//					}
//				}else {
//					if ("Y".equals(sessionQueryAnswer.getIsAnswered())) {
//						answeredQueries.add(sessionQueryAnswer);
//					} else {
//						unansweredQueries.add(sessionQueryAnswer);
//					}
//				}
//				
//			}
//		}
//
		

		modelnView.addObject("sessionQuery", new SessionQueryAnswer());
		return modelnView;

	}

	@RequestMapping(value = "/admin/queryTATReportPage", method = RequestMethod.GET)
	public ModelAndView queryTATReportPage(HttpServletRequest request, HttpServletResponse respnse) {
		if (!checkSession(request, respnse)) {
			return new ModelAndView("studentPortalRediret");
		}
		ModelAndView m = new ModelAndView("queryTATReportPage");
		SessionQueryAnswer searchBean = new SessionQueryAnswer();
		m.addObject("searchBean", searchBean);
		m.addObject("yearList", ACAD_YEAR_LIST);
		m.addObject("subjectList", getSubjectList());
		
		m.addObject("unansweredQueries", null);
		m.addObject("answeredQueries", null);
		request.getSession().setAttribute("unansweredQueries", null);
		request.getSession().setAttribute("answeredQueries", null);

		m.addObject("unansweredQueriesSize", 0);
		m.addObject("answeredQueriesSize", 0);
		
		return m;
	}
	@RequestMapping(value = "/admin/qnAReportPage", method = RequestMethod.GET)
	public ModelAndView qnAReportPage(HttpServletRequest request, HttpServletResponse respnse) {
		if (!checkSession(request, respnse)) {
			return new ModelAndView("studentPortalRediret");
		}
		ModelAndView m = new ModelAndView("query/qnAReportPage");
		SessionQueryAnswer searchBean = new SessionQueryAnswer();
		m.addObject("searchBean", searchBean);
		m.addObject("yearList", ACAD_YEAR_LIST);
		m.addObject("subjectList", getSubjectList());
		
		m.addObject("unansweredQueries", null);
		m.addObject("answeredQueries", null);
		request.getSession().setAttribute("unansweredQueries", null);
		request.getSession().setAttribute("answeredQueries", null);

		m.addObject("unansweredQueriesSize", 0);
		m.addObject("answeredQueriesSize", 0);
		
		return m;
	}

	@RequestMapping(value = "/admin/queryTATReport", method = RequestMethod.POST)
	public ModelAndView queryTATReport(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute SessionQueryAnswer searchBean) {
		if (!checkSession(request, response)) {
			return new ModelAndView("studentPortalRediret");
		}

		ModelAndView modelnView = new ModelAndView("queryTATReportPage");
		request.getSession().setAttribute("searchBean_acads", searchBean);

		ArrayList<SessionQueryAnswer> allQueries = new ArrayList<SessionQueryAnswer>();
		List<SessionQueryAnswer> studentsMyQueries = getAllQueriresForPostQueryReport(searchBean,
				getAuthorizedCodes(request));
		allQueries.addAll(studentsMyQueries);
		if (allQueries != null && allQueries.size() > 0) {
		}

		modelnView.addObject("allQueries", allQueries);

		List<SessionQueryAnswer> unansweredQueries = new ArrayList<SessionQueryAnswer>();
		List<SessionQueryAnswer> answeredQueries = new ArrayList<SessionQueryAnswer>();
		if (allQueries != null) {
			for (SessionQueryAnswer sessionQueryAnswer : allQueries) {
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
		modelnView.addObject("subjectList", getSubjectList());

		if (allQueries == null || allQueries.size() == 0) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No records found.");
		}
		return modelnView;
	}
	@RequestMapping(value = "/admin/qnAReport", method = RequestMethod.POST)
	public ModelAndView qnAReport(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute SessionQueryAnswer searchBean) {
		if (!checkSession(request, response)) {
			return new ModelAndView("studentPortalRediret");
		}

		ModelAndView modelnView = new ModelAndView("query/qnAReportPage");
		request.getSession().setAttribute("searchBean_acads", searchBean);

		ArrayList<SessionQueryAnswer> allQueries = new ArrayList<SessionQueryAnswer>();
		List<SessionQueryAnswer> studentsMyQueries = getAllQnAReport(searchBean);
		
		allQueries.addAll(studentsMyQueries);
		
		modelnView.addObject("allQueries", allQueries);

		List<SessionQueryAnswer> unansweredQueries = new ArrayList<SessionQueryAnswer>();
		List<SessionQueryAnswer> answeredQueries = new ArrayList<SessionQueryAnswer>();
		if (allQueries != null) {
			for (SessionQueryAnswer sessionQueryAnswer : allQueries) {
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
		modelnView.addObject("subjectList", getSubjectList());

		if (allQueries == null || allQueries.size() == 0) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No records found.");
		}
		return modelnView;
	}
	@RequestMapping(value = "/admin/UnAnsweredQueryReport", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView downloadUnAnsweredQueryReport(HttpServletRequest request, HttpServletResponse response) {
		
		if (!checkSession(request, response)) {
			return new ModelAndView("login");
		}
		List<SessionQueryAnswer> queryList = (List<SessionQueryAnswer>) request.getSession()
				.getAttribute("unansweredQueries");
		return new ModelAndView("postQueryReportExcelView", "queryList", queryList);
	}

	@RequestMapping(value = "/admin/AnsweredQueryReport", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView downloadAnsweredQueryReport(HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)){
			return new ModelAndView("studentPortalRediret");
			
					}
		List<SessionQueryAnswer> queryList = (List<SessionQueryAnswer>) request.getSession()
				.getAttribute("answeredQueries");
		return new ModelAndView("postQueryReportExcelView", "queryList", queryList);
	}

	/* Course Query End */

	@RequestMapping(value = "/admin/UnAnsweredQnAReport", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView downloadUnAnsweredQnAReport(HttpServletRequest request, HttpServletResponse response) {
		
		if (!checkSession(request, response)) {
			return new ModelAndView("login");
		}
		List<SessionQueryAnswer> queryList = (List<SessionQueryAnswer>) request.getSession()
				.getAttribute("unansweredQueries");
		return new ModelAndView("QnAReportExcelView", "queryList", queryList);
	}

	@RequestMapping(value = "/admin/AnsweredQnAReport", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView downloadAnsweredQnAReport(HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)){
			return new ModelAndView("studentPortalRediret");
			
					}
		List<SessionQueryAnswer> queryList = (List<SessionQueryAnswer>) request.getSession()
				.getAttribute("answeredQueries");
		return new ModelAndView("QnAReportExcelView", "queryList", queryList);
	}
/*
 * Commented by Stef and moved to Review Controller
 * 
 * 
 * 
 * @RequestMapping(value = "/viewReviewForFacultyForm", method =
 * {RequestMethod.GET, RequestMethod.POST})
 * 
 * public ModelAndView viewReviewForFacultyForm(HttpServletRequest request,
 * HttpServletResponse respnse) { ModelAndView mav = new
 * ModelAndView("viewReviewForFaculty"); TimeTableDAO tDao =
 * (TimeTableDAO)act.getBean("timeTableDAO"); String action =
 * (String)request.getParameter("action"); String userId =
 * (String)request.getSession().getAttribute("userId_acads"); List<SessionDayTimeBean>
 * reviewListFromFacultyId = tDao.reviewListByFacultyId(userId,action);
 * 
 * size()); mav.addObject("rowCount", reviewListFromFacultyId.size());
 * mav.addObject("action", action);
 * mav.addObject("reviewFacultyList",reviewListFromFacultyId); return mav;
 * 
 * }
 * 
 * @RequestMapping(value="/reviewFacultyForm",method={RequestMethod.GET,
 * RequestMethod.POST}) public ModelAndView reviewFacultyForm(@RequestParam
 * String reviewId,@RequestParam String action){
 * (TimeTableDAO)act.getBean("timeTableDAO"); ModelAndView mav = new
 * ModelAndView("reviewFaculty"); SessionReviewBean reviewBean =
 * tDao.findSessionReviewById(reviewId); mav.addObject("reviewId", reviewId);
 * mav.addObject("action", action); mav.addObject("reviewBean",reviewBean);
 * return mav; }
 * 
 * stef commented on Sep-2017
 * 
 * @RequestMapping(value="/searchFacultyReviewForm",method={RequestMethod.GET,
 * RequestMethod.POST}) public ModelAndView
 * searchFacultyReviewForm(HttpServletRequest request, HttpServletResponse
 * response){ ModelAndView mav = new ModelAndView("searchFacultyReview");
 * mav.addObject("reviewBean",new SessionReviewBean()); return mav; }
 * 
 * stef added on Sep
 * 
 * @RequestMapping(value = "/searchFacultyReviewForm", method =
 * {RequestMethod.GET, RequestMethod.POST}) public ModelAndView
 * searchFacultyReviewForm(HttpServletRequest request, HttpServletResponse
 * response){ ModelAndView mav = new ModelAndView("searchFacultyReview");
 * mav.addObject("reviewBean",new SessionReviewBean());
 * mav.addObject("yearList", yearList); mav.addObject("monthList", monthList);
 * mav.addObject("subjectList", getSubjectList()); return mav; }
 * 
 * @RequestMapping(value="/searchFacultyReview",method={RequestMethod.GET,
 * RequestMethod.POST}) public ModelAndView
 * searchFacultyReview(HttpServletRequest request, HttpServletResponse
 * response,@ModelAttribute SessionReviewBean reviewBean){ TimeTableDAO tDao =
 * (TimeTableDAO)act.getBean("timeTableDAO"); ModelAndView mav = new
 * ModelAndView("searchFacultyReview"); mav.addObject("reviewBean",reviewBean);
 * mav.addObject("yearList", yearList); mav.addObject("monthList", monthList);
 * mav.addObject("subjectList", getSubjectList()); try{ List<SessionReviewBean>
 * reviewListBasedOnCriteria = tDao.reviewListBasedOnCriteria(reviewBean);
 * 
 * mav.addObject("reviewBean",reviewBean);
 * mav.addObject("rowCount",reviewListBasedOnCriteria.size());
 * 
 * if(reviewListBasedOnCriteria.isEmpty()) { setError(request,
 * "No Record Found"); }
 * request.getSession().setAttribute("reviewListBasedOnCriteria",
 * reviewListBasedOnCriteria);
 * 
 * 
 * double q5Average = 0.0; double q6Average = 0.0;
 * 
 * 
 * if(reviewListBasedOnCriteria != null && reviewListBasedOnCriteria.size() >
 * 0){ for (SessionReviewBean sessionReviewBean : reviewListBasedOnCriteria) {
 * q5Average +=
 * Integer.parseInt(!StringUtils.isEmpty(sessionReviewBean.getQ5Response()) ?
 * sessionReviewBean.getQ5Response() :"0"); q6Average +=
 * Integer.parseInt(!StringUtils.isEmpty(sessionReviewBean.getQ6Response()) ?
 * sessionReviewBean.getQ6Response() :"0"); }
 * 
 * q5Average = q5Average / reviewListBasedOnCriteria.size(); q6Average =
 * q6Average / reviewListBasedOnCriteria.size();
 * 
 * mav.addObject("q5Average",q5Average); mav.addObject("q6Average",q6Average);
 * 
 * }
 * 
 * }catch(Exception e){    setError(request,
 * "Error Occurse While retriving Details "+e.getMessage()); return
 * searchFacultyReviewForm(request,response); } return mav; }
 * ---------------------------
 * 
 * 
 * stef commented on Sep-2017
 * 
 * @RequestMapping(value = "/downloadSessionFacultyReviews", method =
 * {RequestMethod.GET, RequestMethod.POST}) public ModelAndView
 * downloadSessionFacultyReviews(HttpServletRequest request, HttpServletResponse
 * response) { List<SessionReviewBean> reviewListBasedOnCriteria =
 * (List<SessionReviewBean>)request.getSession().getAttribute(
 * "reviewListBasedOnCriteria"); TimeTableDAO tDao =
 * (TimeTableDAO)act.getBean("timeTableDAO");
 * request.getSession().setAttribute("facultyIdAndFacultyBeanMap",
 * tDao.getAllFacultyMapper()); return new
 * ModelAndView("SessionFacultyReviewsExcelView","reviewListBasedOnCriteria",
 * reviewListBasedOnCriteria); }
 * 
 * stef added on Sep
 * 
 * @RequestMapping(value = "/downloadSessionFacultyReviews", method =
 * {RequestMethod.GET, RequestMethod.POST}) public ModelAndView
 * downloadSessionFacultyReviews(HttpServletRequest request, HttpServletResponse
 * response,@ModelAttribute SessionReviewBean reviewBean) {
 * 
 * TimeTableDAO tDao = (TimeTableDAO)act.getBean("timeTableDAO"); FacultyDAO
 * fDao = (FacultyDAO)act.getBean("facultyDAO");
 * 
 * List<SessionReviewBean> reviewListBasedOnCriteria =
 * tDao.reviewListBasedOnCriteria(reviewBean); ArrayList<FacultyBean>
 * allFacultyRecords = fDao.getAllFacultyRecords();
 * 
 * try{ if(reviewListBasedOnCriteria.isEmpty()) { setError(request,
 * "No Record Found"); return searchFacultyReviewForm(request,response); }
 * request.getSession().setAttribute("facultyIdAndFacultyBeanMap",
 * tDao.getAllFacultyMapper());
 * request.getSession().setAttribute("reviewListBasedOnCriteria",
 * reviewListBasedOnCriteria);
 * request.getSession().setAttribute("allFacultyRecords", allFacultyRecords);
 * 
 * }catch(Exception e){    setError(request,
 * "Error Occurse While retriving Details "+e.getMessage()); return
 * searchFacultyReviewForm(request,response); } return new
 * ModelAndView("SessionFacultyReviewsExcelView","reviewListBasedOnCriteria",
 * reviewListBasedOnCriteria); }
 * 
 * --------------------------------------------------------
 * 
 * @RequestMapping(value="/saveReviewForFaculty",method={RequestMethod.GET,
 * RequestMethod.POST}) public ModelAndView
 * saveReviewForFaculty(HttpServletRequest request, HttpServletResponse
 * response,@ModelAttribute SessionReviewBean reviewBean){
 * 
 * String userId = (String)request.getSession().getAttribute("userId_acads");
 * TimeTableDAO tDao = (TimeTableDAO)act.getBean("timeTableDAO"); try{
 * reviewBean.setLastModifiedBy(userId);
 * reviewBean.setReviewed(SessionReviewBean.REVIEWED);
 * reviewBean.setLastModifiedDate(new Timestamp(System.currentTimeMillis()));
 * tDao.updateFacultyReview(reviewBean);
 * setSuccess(request,"Review Saved Successfully");
 * 
 * }catch(Exception e){   
 * setError(request,"Error in saving Review"); }
 * 
 * return viewReviewForFacultyForm(request,response);
 * 
 * 
 * }
 * 
 * 
 * 
 * @RequestMapping(value = "/uploadSessionReviewFacultyMappingForm", method =
 * {RequestMethod.GET, RequestMethod.POST}) public String
 * uploadSessionReviewFacultyMappingForm(HttpServletRequest request,
 * HttpServletResponse respnse, Model m) { FileBean fileBean = new FileBean();
 * m.addAttribute("fileBean",fileBean); return
 * "uploadSessionReviewFacultyMapping"; }
 * 
 * @RequestMapping(value = "/uploadSessionReviewFacultyMapping", method =
 * {RequestMethod.GET, RequestMethod.POST}) public ModelAndView
 * uploadSessionReviewFacultyMapping(FileBean fileBean, BindingResult
 * result,HttpServletRequest request, Model m){ ModelAndView modelnView = new
 * ModelAndView("uploadSessionReviewFacultyMapping"); try{ String userId =
 * (String)request.getSession().getAttribute("userId_acads"); TimeTableDAO tDao =
 * (TimeTableDAO)act.getBean("timeTableDAO"); ExcelHelper excelHelper = new
 * ExcelHelper(); ArrayList<List> resultList =
 * excelHelper.readSessionReviewFacultyMappingExcel(fileBean, getFacultyList(),
 * getSubjectList(), userId); List<SessionReviewBean>
 * sessionReviewBeanForBatchInsert = new ArrayList<SessionReviewBean>();
 * List<SessionReviewBean> sessionReviewFacultyMapingList =
 * (ArrayList<SessionReviewBean>)resultList.get(0); List<SessionReviewBean>
 * errorBeanList = (ArrayList<SessionReviewBean>)resultList.get(1); Set<String>
 * setOfSubject = new HashSet<String>(); Map<String,String>
 * mapOfSubjectNamesAndReviewer = new HashMap<String,String>();
 * for(SessionReviewBean sessionReview : sessionReviewFacultyMapingList){
 * setOfSubject.add(sessionReview.getSubject().trim());
 * mapOfSubjectNamesAndReviewer.put(sessionReview.getSubject().trim(),
 * sessionReview.getReviewerFacultyId()); }
 * 
 * HashMap<String,SessionDayTimeBean> mapOfIdAndSessionBean =
 * tDao.mapOfSessionIdAndSessionBeanFromGivenSubjectList(setOfSubject,
 * CURRENT_ACAD_MONTH,CURRENT_ACAD_YEAR);
 * 
 * for(String sessionId : mapOfIdAndSessionBean.keySet()){
 * 
 * if(mapOfIdAndSessionBean.get(sessionId)!=null){ String subjectName =
 * mapOfIdAndSessionBean.get(sessionId).getSubject();
 * 
 * String reviewerFacultyId =
 * mapOfSubjectNamesAndReviewer.get(subjectName.trim());
 * 
 * SessionDayTimeBean sessionDayTime = mapOfIdAndSessionBean.get(sessionId);
 * if(!StringUtils.isBlank(sessionDayTime.getFacultyId())){ SessionReviewBean
 * reviewBean1 = new SessionReviewBean(); reviewBean1.setSessionId(sessionId);
 * reviewBean1.setReviewerFacultyId(reviewerFacultyId);
 * reviewBean1.setReviewed(SessionReviewBean.NOT_REVIEWED);
 * reviewBean1.setFacultyId(sessionDayTime.getFacultyId());
 * reviewBean1.setLastModifiedBy(userId); reviewBean1.setCreatedBy(userId);
 * sessionReviewBeanForBatchInsert.add(reviewBean1); }
 * if(!StringUtils.isBlank(sessionDayTime.getAltFacultyId())){ SessionReviewBean
 * reviewBean2 = new SessionReviewBean(); reviewBean2.setSessionId(sessionId);
 * reviewBean2.setReviewerFacultyId(reviewerFacultyId);
 * reviewBean2.setReviewed(SessionReviewBean.NOT_REVIEWED);
 * reviewBean2.setFacultyId(sessionDayTime.getAltFacultyId());
 * reviewBean2.setLastModifiedBy(userId); reviewBean2.setCreatedBy(userId);
 * sessionReviewBeanForBatchInsert.add(reviewBean2); }
 * if(!StringUtils.isBlank(sessionDayTime.getAltFacultyId2())){
 * SessionReviewBean reviewBean3 = new SessionReviewBean();
 * reviewBean3.setSessionId(sessionId);
 * reviewBean3.setReviewerFacultyId(reviewerFacultyId);
 * reviewBean3.setReviewed(SessionReviewBean.NOT_REVIEWED);
 * reviewBean3.setFacultyId(sessionDayTime.getAltFacultyId2());
 * reviewBean3.setLastModifiedBy(userId); reviewBean3.setCreatedBy(userId);
 * sessionReviewBeanForBatchInsert.add(reviewBean3); }
 * if(!StringUtils.isBlank(sessionDayTime.getAltFacultyId3())){
 * SessionReviewBean reviewBean4 = new SessionReviewBean();
 * reviewBean4.setSessionId(sessionId);
 * reviewBean4.setReviewerFacultyId(reviewerFacultyId);
 * reviewBean4.setReviewed(SessionReviewBean.NOT_REVIEWED);
 * reviewBean4.setFacultyId(sessionDayTime.getAltFacultyId3());
 * reviewBean4.setLastModifiedBy(userId); reviewBean4.setCreatedBy(userId);
 * sessionReviewBeanForBatchInsert.add(reviewBean4); } }
 * 
 * 
 * fileBean = new FileBean(); m.addAttribute("fileBean",fileBean);
 * 
 * if(errorBeanList.size() > 0){ request.setAttribute("errorBeanList",
 * errorBeanList); return modelnView; }
 * 
 * 
 * ArrayList<String> errorList =
 * tDao.batchUpdateSessionReviewFacultyMapping(sessionReviewBeanForBatchInsert);
 * 
 * if(errorList.size() == 0){ request.setAttribute("success","true");
 * request.setAttribute("successMessage",sessionReviewFacultyMapingList.size()
 * +" rows out of "+
 * sessionReviewFacultyMapingList.size()+" inserted successfully."); }else{
 * request.setAttribute("error", "true"); request.setAttribute("errorMessage",
 * errorList.size() +
 * " records were NOT inserted. Please see row number of rows not inserted. Error row numbers "
 * +errorList); }
 * 
 * }catch(Exception e){    request.setAttribute("error",
 * "true"); request.setAttribute("errorMessage", "Error in inserting rows.");
 * 
 * }
 * 
 * return modelnView; } }
 */

//Mobile Api Start

//Mobile  Course Query Start
// m/postCourseQuery 
// m/postQuery
// m/sessionQueryList
// m/courseDetailsQueries


//	to be deleted, api shifted to rest controller
//	@RequestMapping(value = "m/getFacultyListForPostCourseQuery", method = RequestMethod.POST, consumes="application/json", produces="application/json" )
//	public  ResponseEntity <List<FacultyBean>> mgetFacultyListForPostCourseQuery(@RequestBody SessionQueryAnswer sessionQuery) {
//		HttpHeaders headers = new HttpHeaders();
//	    headers.add("Content-Type", "application/json");
//	    
//		String subject = sessionQuery.getSubject();
//		List<FacultyBean> facultyForQuery = new ArrayList<>();
//		TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
//		ContentDAO cDao = (ContentDAO)act.getBean("contentDAO");
//		
//		StudentBean student = dao.getSingleStudentsData(sessionQuery.getSapId());
//		StudentBean studentRegData = cDao.getStudentMaxSemRegistrationData(sessionQuery.getSapId());
//		
//		String currentYearMonthArr[] = contentService.getRecordedvsLiveCurrentYearMonthForSapid(studentRegData);
//		
//		String currentAcadYear = currentYearMonthArr[0];
//		String currentAcadMonth = currentYearMonthArr[1];
//		
//		
//		facultyForQuery = sessionQueryAnswerDAO.getFacultyForASubject(subject, student.getConsumerProgramStructureId(),
//				currentAcadYear, currentAcadMonth
//				);
//
//		return new ResponseEntity<List<FacultyBean>>(facultyForQuery, headers,  HttpStatus.OK);
//	}

//	to be deleted, api shifted to rest controller
//@RequestMapping(value = "m/postCourseQuery", method = RequestMethod.POST, consumes="application/json", produces="application/json" )
//public  ResponseEntity <Map<String, String>> mpostCourseQuery(@RequestBody SessionQueryAnswer sessionQuery) {
//	HttpHeaders headers = new HttpHeaders();
//    headers.add("Content-Type", "application/json");
//	TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
//
//	// Get Latest Acads Year, month via ExamOrderBean having max order
//	ExamOrderBean examorderBean = sessionQueryAnswerDAO.getExamOrderBeanWhereContentLive();
//	StudentBean student = dao.getSingleStudentsData(sessionQuery.getSapId());
//	
//	
//	HashMap<String, String> query_post_result = new HashMap<>();
//	if (examorderBean == null) {
//		query_post_result.put("status", "error");
//		query_post_result.put("message", "Error in posting query try again later.");
//		return new ResponseEntity<Map<String, String>>(query_post_result, headers,  HttpStatus.OK);
//	}
//
//	// Get facultyId of faculty that query will be assigned to
//
//	String facultyIdToAssignQuery = null;
//	if(sessionQuery.getFacultyId() != null) {
//		facultyIdToAssignQuery = sessionQuery.getFacultyId();
//	} else {
//		ContentDAO cDao = (ContentDAO)act.getBean("contentDAO");
//		StudentBean studentRegData = cDao.getStudentMaxSemRegistrationData(sessionQuery.getSapId());
//		
//		String currentYearMonthArr[] = contentService.getRecordedvsLiveCurrentYearMonthForSapid(studentRegData);
//		
//		String currentAcadYear = currentYearMonthArr[0];
//		String currentAcadMonth = currentYearMonthArr[1];
//		// fallback in case no faculty id is found.
//		List<FacultyBean> facultyForQuery = sessionQueryAnswerDAO.getFacultyForASubject(sessionQuery.getSubject(),student.getConsumerProgramStructureId(),
//				currentAcadYear, currentAcadMonth
//				);
//		
//		if(facultyForQuery != null && facultyForQuery.size() > 0) {
//			facultyIdToAssignQuery = facultyForQuery.get(0).getFacultyId();
//		}
////		else {
////			//final fallback in case no faculty is found.
////			facultyIdToAssignQuery = sessionQueryAnswerDAO.getFaultyIdToAnswerCourseQuery(sessionQuery.getSubject());
////		}
//	}
//	if (facultyIdToAssignQuery == null) {
//		query_post_result.put("status", "error");
//		query_post_result.put("message", "Faculty Not Yet Assgined To This Course Try Again Later");
//		return new ResponseEntity<Map<String, String>>(query_post_result, headers,  HttpStatus.OK);
//	}
//	sessionQuery.setFacultyId(facultyIdToAssignQuery);
//	sessionQuery.setAssignedToFacultyId(facultyIdToAssignQuery);
//	sessionQuery.setQueryType("Course Query");
//	sessionQuery.setYear(String.valueOf(examorderBean.getYear()));
//	sessionQuery.setMonth(examorderBean.getAcadMonth());
//	
//	
//	Integer count = sessionQueryAnswerDAO.checkForSameAskQuery(sessionQuery);
//	if(count > 0) {
//		query_post_result.put("status", "error");
//		query_post_result.put("message", "This Query Already asked by you please check in My Queries Tab.");
//		return new ResponseEntity<Map<String, String>>(query_post_result, headers,  HttpStatus.OK);
//	}
//	
//	long studentZone_QueryId = sessionQueryAnswerDAO.saveQuery(sessionQuery);
//
//	sessionQuery.setId(String.valueOf(studentZone_QueryId));
//	query_post_result.put("status", "success");
//	query_post_result.put("message", "Query submitted successfully");
//	query_post_result.put("query_id", sessionQuery.getId());
//	
//	return new ResponseEntity<Map<String, String>>(query_post_result, headers,  HttpStatus.OK);
//}

/* Course Mobile Api Query End */


/* Course Mobile Api Query End */

//Session Query Post 
//Session Query Get

//	to be deleted, api shifted to rest controller
//@RequestMapping(value = "m/postQuery", method = RequestMethod.POST, consumes="application/json", produces="application/json" )
//public  ResponseEntity <Map<String, String>>  mpostQuery(@RequestBody SessionQueryAnswer sessionQuery) {
//	HttpHeaders headers = new HttpHeaders();
//    headers.add("Content-Type", "application/json");
//    
//	// Get Latest Acads Year, month via ExamOrderBean having max order
//	ExamOrderBean examorderBean = sessionQueryAnswerDAO.getExamOrderBeanWhereContentLive();
//	HashMap<String, String> query_post_result = new HashMap<>();
//	
//	
//	//ModelAndView modelnView = new ModelAndView("postQuery");
//
//	TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
//	FacultyDAO fDao = (FacultyDAO) act.getBean("facultyDAO");
//
//	SessionDayTimeBean session = dao.findScheduledSessionById(sessionQuery.getSessionId());
//	
//	//modelnView.addObject("session", session);
//
////	String userIdEncrypted = sessionQuery.get;
////	String userIdFromURL = null;
////	try {
////		if (userIdEncrypted != null) {
////			userIdFromURL = AESencrp.decrypt(userIdEncrypted);
////		}
////	} catch (Exception e) {
////		// TODO: handle exception
////	}
//
//	String sapId = (String) sessionQuery.getSapId();
//	// login in Acads as student
////	if (userIdFromURL != null) {
////		sapId = userIdFromURL;
////	}
//
//	sessionQuery.setSapId(sapId);
//
//StudentBean student = dao.getSingleStudentsData(sessionQuery.getSapId());;
//
//	String queryAssignedToFacultyId = null;
//	SessionAttendanceFeedback attendance = dao.checkSessionAttendance(sapId, session.getId());
//	if (attendance != null) {
//		// Student had attended session, so allocate it to Faculty whose session was
//		// attended
//		queryAssignedToFacultyId = attendance.getFacultyId();
//
//	} else {
//		// Student did not attend session, allocate it to First Faculty
//		queryAssignedToFacultyId = session.getFacultyId();
//	}
//	sessionQuery.setAssignedToFacultyId(queryAssignedToFacultyId);
//	sessionQuery.setSubject(session.getSubject());
//	sessionQuery.setYear(session.getYear());
//	sessionQuery.setMonth(session.getMonth());
//	
//	Integer count = sessionQueryAnswerDAO.checkForSameAskQuery(sessionQuery);
//	if(count > 0) {
//		query_post_result.put("status", "error");
//		query_post_result.put("message", "This Query Already asked by you please check in My Queries Tab.");
//		return new ResponseEntity<Map<String, String>>(query_post_result, headers,  HttpStatus.OK);
//	}
//
//	long studentZone_QueryId = sessionQueryAnswerDAO.saveQuery(sessionQuery);
//	
//	query_post_result.put("status", "success");
//	query_post_result.put("message", "Query submitted successfully");
//	
//	sessionQuery.setId(String.valueOf(studentZone_QueryId));
//
//	FacultyBean faculty = fDao.findfacultyByFacultyId(queryAssignedToFacultyId);
//
//	MailSender mailSender = (MailSender) act.getBean("mailer");
//
//	// Only Academic Query goes to Faculty
//	if ("Academic".equals(sessionQuery.getQueryType())) {
//		mailSender.sendQueryPostedEmail(session, sessionQuery, faculty, faculty.getEmail());
//	} else {
//		// Create Case In Salesforce
//		sessionQuery = salesforceHelper.createCaseInSalesforce(student, sessionQuery, session);
//		sessionQueryAnswerDAO.updateSalesforceErrorMessage(sessionQuery);// update CaseId and ErrorMessage in Table
//	}
//
////	modelnView.addObject("sessionQuery", sessionQuery);
////
////	modelnView.addObject("action", "postQueries");
//
//	return new ResponseEntity<Map<String, String>>(query_post_result, headers,  HttpStatus.OK);
//}



 
	

//to be deleted api shifted to rest controller
//@CrossOrigin(origins="*", allowedHeaders="*")
//@RequestMapping(value = "m/postQueryMBAWX", method = RequestMethod.POST, consumes="application/json", produces="application/json" )
//public  ResponseEntity <Map<String, String>>  mpostQueryMBAWX(@RequestBody SessionQueryAnswer sessionQueryBean) {
//	HttpHeaders headers = new HttpHeaders();
//    headers.add("Content-Type", "application/json");
//    
//    HashMap<String, String> queryPostResponseMap = new HashMap<>();
//    // validation 
//    if(sessionQueryBean.getSapId() == null) {
//    	queryPostResponseMap.put("status", "error");
//    	queryPostResponseMap.put("message", "Sapid not found");
//    	return new ResponseEntity<Map<String, String>>(queryPostResponseMap, headers,  HttpStatus.OK);
//    }
//    if(sessionQueryBean.getTimeBoundId() == null) {
//    	queryPostResponseMap.put("status", "error");
//    	queryPostResponseMap.put("message", "timebound not found");
//    	return new ResponseEntity<Map<String, String>>(queryPostResponseMap, headers,  HttpStatus.OK);
//    }
//    if(sessionQueryBean.getAssignedToFacultyId() == null) {
//    	queryPostResponseMap.put("status", "error");
//    	queryPostResponseMap.put("message", "faculty id not found");
//    	return new ResponseEntity<Map<String, String>>(queryPostResponseMap, headers,  HttpStatus.OK);
//    }
//    if(sessionQueryBean.getQuery() == null) {
//    	queryPostResponseMap.put("status", "error");
//    	queryPostResponseMap.put("message", "query not found");
//    	return new ResponseEntity<Map<String, String>>(queryPostResponseMap, headers,  HttpStatus.OK);
//    }
//    if(sessionQueryBean.getSubject() == null) {
//    	queryPostResponseMap.put("status", "error");
//    	queryPostResponseMap.put("message", "subject not found");
//    	return new ResponseEntity<Map<String, String>>(queryPostResponseMap, headers,  HttpStatus.OK);
//    }
//    if(sessionQueryBean.getConsumerProgramStructureId() == null) {
//    	
//    	String consumerProgramStructureId=sessionQueryAnswerDAO.getConsumerProgramStructureIdBySapId(sessionQueryBean.getSapId());
//    	sessionQueryBean.setConsumerProgramStructureId(consumerProgramStructureId);
//    }
//    // end validation logic
//    FacultyDAO fDao = (FacultyDAO) act.getBean("facultyDAO");
//    sessionQueryBean.setQueryType("Academic");
//    sessionQueryBean.setYear("" + CURRENT_ACAD_YEAR + "");
//    sessionQueryBean.setMonth(CURRENT_ACAD_MONTH);
//    sessionQueryBean.setHasTimeBoundId("Y");
//	try {
//		
//		Integer count = sessionQueryAnswerDAO.checkForSameAskQuery(sessionQueryBean);
//		if(count > 0) {
//			queryPostResponseMap.put("status", "error");
//	    	queryPostResponseMap.put("message", "This Query Already asked by you please check in My Queries Tab.");
//			return new ResponseEntity<Map<String, String>>(queryPostResponseMap, headers,  HttpStatus.OK);
//		}
//		
//		long studentZone_QueryId = sessionQueryAnswerDAO.saveQuery(sessionQueryBean);
//		
//		queryPostResponseMap.put("status", "success");
//		queryPostResponseMap.put("message", "Query submitted successfully");
//		
//		sessionQueryBean.setId(String.valueOf(studentZone_QueryId));
//	
//		FacultyBean faculty = fDao.findfacultyByFacultyId(sessionQueryBean.getAssignedToFacultyId());
//	
//		MailSender mailSender = (MailSender) act.getBean("mailer");
//	
//		mailSender.sendWXQueryPostedEmail(sessionQueryBean, faculty, faculty.getEmail());
//		
//		sendQueryPostedSMSToFaculty(sessionQueryBean , faculty);
//		
//		return new ResponseEntity<Map<String, String>>(queryPostResponseMap, headers,  HttpStatus.OK);
//	}
//	catch (Exception e) {
//		// TODO: handle exception
//		queryPostResponseMap.put("status", "error");
//    	queryPostResponseMap.put("message", "Something went wrong,While creating query");
//    	return new ResponseEntity<Map<String, String>>(queryPostResponseMap, headers,  HttpStatus.OK);
//	}
//}




//Api to retrieve queries for Session Details (Includes Public, Private Session Queries )
//to be deletd, api shifted to rest controller
//@RequestMapping(value = "m/sessionQueryList", method = RequestMethod.POST, consumes="application/json", produces="application/json")
//public ResponseEntity<Map<String, List<SessionQueryAnswer>>> msessionQueryList(@RequestBody SessionQueryAnswer sessionQuery) throws Exception {
//	HttpHeaders headers = new HttpHeaders();
//    headers.add("Content-Type", "application/json");
//	TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
//	Map<String, List<SessionQueryAnswer>> reponse= new HashMap<String, List<SessionQueryAnswer>>();
//	try {
//	List<SessionQueryAnswer> myQueries = getMyQueries(sessionQuery);
//	reponse.put("myQueries", myQueries);
//	List<SessionQueryAnswer> publicQueries = getPublicQueries(sessionQuery);
//	reponse.put("publicQueries", publicQueries);
//	return new ResponseEntity<Map<String, List<SessionQueryAnswer>>>( reponse, headers,  HttpStatus.OK);
//	} catch (EmptyResultDataAccessException e) {
//		return new ResponseEntity<Map<String, List<SessionQueryAnswer>>>( reponse, headers,  HttpStatus.OK);	
//		}
//}
 
// Api to retrieve queries for Course Details (Includes Public, Private Course Queries and Public, Private Session Course Queries)
// to be deleted, api shifted to rest controller
//@RequestMapping(value = "/m/courseDetailsQueries", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")	
//public ResponseEntity<Map<String, List<SessionQueryAnswer>>> m_courseDetailsQueries(@RequestBody StudentBean student) throws Exception {
//	HttpHeaders headers = new HttpHeaders();
//    headers.add("Content-Type", "application/json"); 
//	TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
//
//	Map<String, List<SessionQueryAnswer>> response= new HashMap<String, List<SessionQueryAnswer>>();
//	StudentBean studentForMasterKey = dao.getSingleStudentsData(student.getSapid());
//    List<SessionQueryAnswer> private_queries = getMyCourseQueries(student.getSapid(), student.getSubject());
//    response.put("private_queries", private_queries);
//    List<SessionQueryAnswer> public_queries = new ArrayList<SessionQueryAnswer>();
//    public_queries = getPublicCourseQueries(student.getSapid(), student.getSubject(),studentForMasterKey.getConsumerProgramStructureId());
//    response.put("public_queries", public_queries);
//    return new ResponseEntity<>(response, headers,  HttpStatus.OK);
//}
	

//to be deleted , api shifted to rest controller
//	@CrossOrigin(origins="*", allowedHeaders="*")
//	@RequestMapping(value = "/m/getMyQueryMBAWX", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")	
//	public ResponseEntity<PostMyQueryMBAWXBean> mgetMyQueryMBAWX(HttpServletRequest request,@RequestBody SessionQueryAnswer sessionQuery) throws Exception {
//		HttpHeaders headers = new HttpHeaders();
//	    headers.add("Content-Type", "application/json"); 
//	    
//	    PostMyQueryMBAWXBean postMyQueryMBAWXBean = new PostMyQueryMBAWXBean();
//	    if(sessionQuery.getSapId() == null) {
//	    	postMyQueryMBAWXBean.setStatus("error");
//	    	postMyQueryMBAWXBean.setMessage("Sapid not found");
//	    	return new ResponseEntity<>(postMyQueryMBAWXBean, headers,  HttpStatus.OK);
//	    }
//	    if(sessionQuery.getTimeBoundId() == null) {
//	    	postMyQueryMBAWXBean.setStatus("error");
//	    	postMyQueryMBAWXBean.setMessage("timebound id not found");
//	    	return new ResponseEntity<>(postMyQueryMBAWXBean, headers,  HttpStatus.OK);
//	    }
//	    TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
//	    FacultyDAO fDao = (FacultyDAO) act.getBean("facultyDAO");
//	    int page = request.getParameter("page") != null ? Integer.parseInt(request.getParameter("page")) : 1;
//	    int startFrom = (page - 1) * postMBAWXPageSize;
//	    List<SessionQueryAnswer> privateQueriesList = sessionQueryAnswerDAO.getMyPostQueries(sessionQuery.getSapId(), sessionQuery.getTimeBoundId(),startFrom,postMBAWXPageSize);
//	    //StudentBean student = dao.getSingleStudentsData(sessionQuery.getSapId());
//	    for (SessionQueryAnswer sessionQueryAnswer : privateQueriesList) {
//	    	//sessionQueryAnswer.setName(student.getFirstName() + " " + student.getLastName());
//	    	/*FacultyBean faculty = fDao.findfacultyByFacultyId(sessionQueryAnswer.getAssignedToFacultyId());
//			
//			
//			if(faculty != null) {
//				sessionQueryAnswer.setFacultyName(faculty.getFirstName() + " " + faculty.getLastName());
//			}else {
//				sessionQueryAnswer.setFacultyName(" ");
//			}*/
//			
//	    	if (!"Y".equals(sessionQueryAnswer.getIsAnswered())) {
//				sessionQueryAnswer.setAnswer("Not Answered Yet");
//				sessionQueryAnswer.setIsAnswered("N");
//			}
//		}
//	    postMyQueryMBAWXBean.setStatus("success");
//	    postMyQueryMBAWXBean.setSessionQueryAnswerList(privateQueriesList);
//	    return new ResponseEntity<>(postMyQueryMBAWXBean, headers,  HttpStatus.OK);
//	}
	
//to be deleted , api shifted to rest controller
//	@CrossOrigin(origins="*", allowedHeaders="*")
//	@RequestMapping(value = "/m/getPublicQueryMBAWX", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")	
//	public ResponseEntity<PostMyQueryMBAWXBean> mgetPublicQueryMBAWX(HttpServletRequest request, @RequestBody SessionQueryAnswer sessionQuery) throws Exception {
//		HttpHeaders headers = new HttpHeaders();
//	    headers.add("Content-Type", "application/json"); 
//	    
//	    PostMyQueryMBAWXBean postMyQueryMBAWXBean = new PostMyQueryMBAWXBean();
//	    if(sessionQuery.getTimeBoundId() == null) {
//	    	postMyQueryMBAWXBean.setStatus("error");
//	    	postMyQueryMBAWXBean.setMessage("timebound id not found");
//	    	return new ResponseEntity<>(postMyQueryMBAWXBean, headers,  HttpStatus.OK);
//	    }
//	    TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
//	    FacultyDAO fDao = (FacultyDAO) act.getBean("facultyDAO");
//	    int page = request.getParameter("page") != null ? Integer.parseInt(request.getParameter("page")) : 1;
//	    int startFrom = (page - 1) * postMBAWXPageSize;
//	    List<SessionQueryAnswer> publicQueriesList = sessionQueryAnswerDAO.getPublicPostQueries(sessionQuery.getTimeBoundId(),startFrom,postMBAWXPageSize);
//	    for (SessionQueryAnswer sessionQueryAnswer : publicQueriesList) {
//			/*StudentBean student = dao.getSingleStudentsData(sessionQueryAnswer.getSapId());
//			FacultyBean faculty = fDao.findfacultyByFacultyId(sessionQueryAnswer.getAssignedToFacultyId());
//			if(student != null) {
//				sessionQueryAnswer.setName(student.getFirstName() + " " + student.getLastName());
//			}else {
//				sessionQueryAnswer.setName(" ");
//			}
//			
//			if(faculty != null) {
//				sessionQueryAnswer.setFacultyName(faculty.getFirstName() + " " + faculty.getLastName());
//			}else {
//				sessionQueryAnswer.setFacultyName(" ");
//			}*/
//			
//	    	if (!"Y".equals(sessionQueryAnswer.getIsAnswered())) {
//				sessionQueryAnswer.setAnswer("Not Answered Yet");
//				sessionQueryAnswer.setIsAnswered("N");
//			}
//			
//		}
//	    postMyQueryMBAWXBean.setStatus("success");
//	    postMyQueryMBAWXBean.setSessionQueryAnswerList(publicQueriesList);
//	    return new ResponseEntity<>(postMyQueryMBAWXBean, headers,  HttpStatus.OK);
//	}

	
	
	public void sendQueryPostedSMSToFaculty(SessionQueryAnswer sessionQuery, FacultyAcadsBean faculty) {

		String subject = "";
		String query = "";
		String programName="";
		
		/*
		if(("111".equals(sessionQuery.getConsumerProgramStructureId())) || ("151".equals(sessionQuery.getConsumerProgramStructureId()))) {
			programName="MBA - WX";
		}else if ("131".equals(sessionQuery.getConsumerProgramStructureId())) {
			programName="M.Sc. (AI & ML Ops)";
		}
		*/
		
		programName = mapOfProgramCodeAndMasterKey().get(sessionQuery.getConsumerProgramStructureId());

		if( sessionQuery.getSubject().length() > 30 ) {
			subject = subjectAbbreviationHelper.createAbbreviation(sessionQuery.getSubject());
		}else {
			subject = sessionQuery.getSubject();
		}

		if( sessionQuery.getQuery().length() > 30 ) {
			query = sessionQuery.getQuery().substring(0, 27)+"...";
		}else {
			query = sessionQuery.getQuery();
		}

		try {
			String message =  "Dear Faculty, A query has been received for " + subject +" - "+ programName + ". \n"
					+ "Query: "+query+". Please login to Student Zone to respond to query."
					+"Thanks & Regards-SVKM's NGASCE";
			
			String result = smsSender.sendPostedQuerySMSToFaculty(faculty ,message);
			if("OK".equalsIgnoreCase(result)){
			}else{
				// sending Error Email if SMS does not sent due to Password change or Username change 
				ArrayList<String> recipent = new ArrayList<String>(Arrays.asList("sanketpanaskar@gmail.com","sneha.utekar@nmims.edu","jforce.solution@gmail.com"));
				MailSender mailSender = (MailSender)act.getBean("mailer");
				mailSender.sendEmail("POST QUERY SMS NOT SENT","Post Query SMS Not Sent Due to <br><br>"+result,recipent);
			}
		} catch (Exception e) {
			  
		}

	}
	// get all QA of webinar sessions held today

		@RequestMapping(value = "/admin/gotoFacultySessionList", method = { RequestMethod.GET, RequestMethod.POST })
		public ModelAndView gotoFacultySessionList(HttpServletRequest request, HttpServletResponse response, Model m) {
			
			if (!checkSession(request, response)) {
				return new ModelAndView("login");
			}
			
			String facultyId = (String) request.getSession().getAttribute("userId_acads");
			ContentDAO cDao = (ContentDAO) act.getBean("contentDAO");

			List<SessionDayTimeAcadsBean> sessionListPg = null;
			List<SessionDayTimeAcadsBean> sessionListMbawx = null;
			try {
				sessionListPg = cDao.getCompletedSessionsOfFacultyForPg(facultyId);
				sessionListMbawx = cDao.getCompletedSessionsOfFacultyForMbaWx(facultyId);
			} catch (Exception e) {
				  
			}
			
			sessionListPg = qnaOfLiveSessionsService.setMirrorSessionQnACount(sessionListPg, facultyId);
			sessionListMbawx = qnaOfLiveSessionsService.setMirrorSessionQnACount(sessionListMbawx, facultyId);
			

			m.addAttribute("pgSessions", sessionListPg);
			m.addAttribute("pgSessionsSize", sessionListPg.size());
			m.addAttribute("mbawxSessions", sessionListMbawx);
			m.addAttribute("mbawxSessionsSize", sessionListMbawx.size());
			return new ModelAndView("completedSessions");
		}

		@RequestMapping(value = "/admin/gotoChatandQA", method = { RequestMethod.GET, RequestMethod.POST })
		public ModelAndView gotoChatandQA(HttpServletRequest request, HttpServletResponse response, Model m,
				@RequestParam("sessionId") String sessionId) {
			
			if (!checkSession(request, response)) {
				return new ModelAndView("login");
			}
			int unansweredCount = 0;
			int answeredCount = 0;
			
			String facultyId = (String) request.getSession().getAttribute("userId_acads");
			List<SessionQueryAnswer>  sessionQA = qnaOfLiveSessionsService.getSingleSessionsQnAForFaculty(sessionId, facultyId);
			
			for (SessionQueryAnswer un : sessionQA) {
				if ("Open".equals(un.getStatus())) {
					unansweredCount++;
				} else {
					answeredCount++;
				}
			}
			m.addAttribute("sessionQA", sessionQA);
			m.addAttribute("unansweredCount", unansweredCount);
			m.addAttribute("answeredCount", answeredCount);
			SessionQueryAnswer sessionQn = new SessionQueryAnswer();
			m.addAttribute("sessionQn", sessionQn); // bean to save answer
			return new ModelAndView("sessionChatnQA");
		}

		@RequestMapping(value = "/admin/saveQAAnswer", method = { RequestMethod.GET, RequestMethod.POST })
		public ModelAndView saveQAAnswer(HttpServletRequest request, HttpServletResponse response, Model m,
				@RequestParam("isPublic") String isPublic, @ModelAttribute SessionQueryAnswer sessionQn) throws Exception {
			String userId = (String) request.getSession().getAttribute("userId_acads");
			sessionQn.setLastModifiedBy(userId);
			sessionQn.setAnsweredByFacultyId(userId);
			sessionQn.setFacultyId(userId);
			ContentDAO cDao = (ContentDAO) act.getBean("contentDAO");
			try {
				cDao.updateAnswer(sessionQn);
				setSuccess(request, "Answer saved successfully");
			} catch (Exception e) {
				  
			}
			String sessionId = sessionQn.getSessionId();
			// Send Back to Session QA Page
			return gotoChatandQA(request, response, m, sessionId);
		}
		
		// get all QA of webinar sessions held today
		@GetMapping(value = "/admin/getWebinarQAReport")
		public void getWebinarQAReport(HttpServletRequest request) {
			if (!"tomcat4".equalsIgnoreCase(SERVER) || !"PROD".equalsIgnoreCase(ENVIRONMENT)) {
				System.out.println("Not running validateUpcomingSessions since this is not tomcat4. This is " + SERVER);
				return;
			}
			String sessionDate=request.getParameter("sessionDate");
			try {
				qnaService.webinarQnAReport(sessionDate);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				  
			}

		}
		
		@RequestMapping(value="/admin/queryReplyForm",method={RequestMethod.GET,RequestMethod.POST})
		public ModelAndView queryReplyForm(HttpServletRequest request, HttpServletResponse response){
			
			ModelAndView modelnview = new ModelAndView("query/queryReplyForm");
			String queryAnswerId = request.getParameter("queryAnswerId");

			SessionQueryAnswer sessionQueryAnswer = sessionQueryAnswerDAO.getQueryById(queryAnswerId);
			
			modelnview.addObject("sessionQueryAnswer", sessionQueryAnswer);
			
			return modelnview;
		}
		
		@RequestMapping(value = "/admin/postQueryAsForum", method = { RequestMethod.POST})
		public ModelAndView postQueryAsForum(HttpServletRequest request, HttpServletResponse response, @ModelAttribute SessionQueryAnswer sessionQuery) {

			try {
				ExamOrderAcadsBean bean=queryAnswerService.getForumCurrentlyLive();
				queryAnswerService.postQueryAsForum(sessionQuery, bean.getYear(), bean.getAcadMonth());
				queryAnswerService.updateForumStatus(sessionQuery.getAssignedToFacultyId(), sessionQuery.getId());
				setSuccess(request, "Forum Created Successfully!");
			}catch (Exception e) {
				// TODO: handle exception
				  
				setError(request, "Error in creating thread!");
			}
			return assignedCourseQueries(request, response);
		}
		
		public boolean twoAcadCycleCourses(HttpServletRequest request){
			 
		 	boolean current = false;
			try {
				
				   
				double current_order=(double) request.getSession().getAttribute("current_order"); 
			    double reg_order = (double)request.getSession().getAttribute("reg_order");
			    double acadContentLiveOrder=(double) request.getSession().getAttribute("acadContentLiveOrder");
			    
			    if(current_order == reg_order || acadContentLiveOrder == reg_order)
			    {
			    	current = true;
			    }
			    
				
			}catch(Exception e)
			{
				e.printStackTrace();
				//
			}
			System.out.println("current>>>"+current);
			return current;
		}

}