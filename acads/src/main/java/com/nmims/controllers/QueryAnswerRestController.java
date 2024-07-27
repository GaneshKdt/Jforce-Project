package com.nmims.controllers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.nmims.beans.ExamOrderAcadsBean;
import com.nmims.beans.FacultyAcadsBean;
import com.nmims.beans.PostMyQueryMBAWXBean;
import com.nmims.beans.ProgramBean;
import com.nmims.beans.SessionAttendanceFeedbackAcads;
import com.nmims.beans.SessionDayTimeAcadsBean;
import com.nmims.beans.SessionQueryAnswer;
import com.nmims.beans.StudentAcadsBean;
import com.nmims.daos.ContentDAO;
import com.nmims.daos.FacultyDAO;
import com.nmims.daos.SessionQueryAnswerDAO;
import com.nmims.daos.TimeTableDAO;
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

@RestController
@CrossOrigin(origins="*", allowedHeaders="*")
@RequestMapping("m")
public class QueryAnswerRestController extends BaseController {
	
	@Autowired(required = false)
	ApplicationContext act;

	@Autowired
	private SessionQueryAnswerDAO sessionQueryAnswerDAO;

	@Autowired
	private SalesforceHelper salesforceHelper;
	
	@Autowired
	SubjectAbbreviationHelper subjectAbbreviationHelper;
	
	@Autowired
	private ContentService contentService;
	
	@Autowired
	QnAOfLiveSessionsService qnaService;
	
	@Autowired
	QueryAnswerService queryAnswerService;
	
	@Autowired
	private SMSSender smsSender;
	
	@Value("${CURRENT_ACAD_MONTH}")
	private String CURRENT_ACAD_MONTH;

	@Value("${CURRENT_ACAD_YEAR}")
	private int CURRENT_ACAD_YEAR;

	@Value("#{'${ACAD_YEAR_LIST}'.split(',')}")
	private List<String> ACAD_YEAR_LIST;
	
	private final int postMBAWXPageSize = 500;
	
	private HashMap<String, String> mapOfProgramCodeAndMasterKey = new HashMap<String, String>();
	
	public HashMap<String, String> mapOfProgramCodeAndMasterKey(){
		if (mapOfProgramCodeAndMasterKey == null) {
			List<ProgramBean> programAndMasterKey = sessionQueryAnswerDAO.getProgramCodeAndMaterKeyList();
			for (ProgramBean bean : programAndMasterKey) {
				this.mapOfProgramCodeAndMasterKey.put(bean.getId(), bean.getCode());
			}
		}
		return mapOfProgramCodeAndMasterKey;
	}
	
	@PostMapping(value = "/getFacultyListForPostCourseQuery", consumes="application/json", produces="application/json" )
	public  ResponseEntity <List<FacultyAcadsBean>> mgetFacultyListForPostCourseQuery(@RequestBody SessionQueryAnswer sessionQuery) {
		HttpHeaders headers = new HttpHeaders();
	    headers.add("Content-Type", "application/json");
	    
		String subject = sessionQuery.getSubject();
		List<FacultyAcadsBean> facultyForQuery = new ArrayList<>();
		TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
		ContentDAO cDao = (ContentDAO)act.getBean("contentDAO");
		
		StudentAcadsBean student = dao.getSingleStudentsData(sessionQuery.getSapId());
		StudentAcadsBean studentRegData = cDao.getStudentMaxSemRegistrationData(sessionQuery.getSapId());
		
		String currentYearMonthArr[] = contentService.getRecordedvsLiveCurrentYearMonthForSapid(studentRegData);
		
		String currentAcadYear = currentYearMonthArr[0];
		String currentAcadMonth = currentYearMonthArr[1];
		
		
		facultyForQuery = sessionQueryAnswerDAO.getFacultyForASubject(subject, student.getConsumerProgramStructureId(),
				currentAcadYear, currentAcadMonth
				);

		return new ResponseEntity<List<FacultyAcadsBean>>(facultyForQuery, headers,  HttpStatus.OK);
	}

	
	@PostMapping(value = "/postCourseQuery", consumes="application/json", produces="application/json" )
	public  ResponseEntity <Map<String, String>> mpostCourseQuery(@RequestBody SessionQueryAnswer sessionQuery) {
		HttpHeaders headers = new HttpHeaders();
	    headers.add("Content-Type", "application/json");
		TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");

		// Get Latest Acads Year, month via ExamOrderBean having max order
		ExamOrderAcadsBean examorderBean = sessionQueryAnswerDAO.getExamOrderBeanWhereContentLive();
		StudentAcadsBean student = dao.getSingleStudentsData(sessionQuery.getSapId());
		
		
		HashMap<String, String> query_post_result = new HashMap<>();
		if (examorderBean == null) {
			query_post_result.put("status", "error");
			query_post_result.put("message", "Error in posting query try again later.");
			return new ResponseEntity<Map<String, String>>(query_post_result, headers,  HttpStatus.OK);
		}

		// Get facultyId of faculty that query will be assigned to

		String facultyIdToAssignQuery = null;
		
		ContentDAO cDao = (ContentDAO)act.getBean("contentDAO");
		StudentAcadsBean studentRegData = cDao.getStudentMaxSemRegistrationData(sessionQuery.getSapId());
		
		String currentYearMonthArr[] = contentService.getRecordedvsLiveCurrentYearMonthForSapid(studentRegData);
		
		String currentAcadYear = currentYearMonthArr[0];
		String currentAcadMonth = currentYearMonthArr[1];
		
		if(sessionQuery.getFacultyId() != null) {
			facultyIdToAssignQuery = sessionQuery.getFacultyId();
		} else {
			// fallback in case no faculty id is found.
			List<FacultyAcadsBean> facultyForQuery = sessionQueryAnswerDAO.getFacultyForASubject(sessionQuery.getSubject(),student.getConsumerProgramStructureId(),
					currentAcadYear, currentAcadMonth
					);
			
			if(facultyForQuery != null && facultyForQuery.size() > 0) {
				facultyIdToAssignQuery = facultyForQuery.get(0).getFacultyId();
			}
//			else {
//				//final fallback in case no faculty is found.
//				facultyIdToAssignQuery = sessionQueryAnswerDAO.getFaultyIdToAnswerCourseQuery(sessionQuery.getSubject());
//			}
		}
		if (facultyIdToAssignQuery == null) {
			query_post_result.put("status", "error");
			query_post_result.put("message", "Faculty Not Yet Assgined To This Course Try Again Later");
			return new ResponseEntity<Map<String, String>>(query_post_result, headers,  HttpStatus.OK);
		}
		
		/*if (sessionQuery.getProgramSemSubjectId()== null) {
			sessionQueryAnswerDAO.getProgramSemSubjectIdOfSubjectSemAndmasterKey(sessionQuery.getSubject());
		}*/
		
		sessionQuery.setFacultyId(facultyIdToAssignQuery);
		sessionQuery.setAssignedToFacultyId(facultyIdToAssignQuery);
		sessionQuery.setQueryType("Course Query");
		sessionQuery.setYear(currentAcadYear);
		sessionQuery.setMonth(currentAcadMonth);
		
		
		Integer count = sessionQueryAnswerDAO.checkForSameAskQuery(sessionQuery);
		if(count > 0) {
			query_post_result.put("status", "error");
			query_post_result.put("message", "This Query Already asked by you please check in My Queries Tab.");
			return new ResponseEntity<Map<String, String>>(query_post_result, headers,  HttpStatus.OK);
		}
		
		String programSemSubjectId="";
		if(sessionQuery.getProgramSemSubjectId()==null) {
			programSemSubjectId=sessionQueryAnswerDAO.getProgramSemSubjectIdOfSubjectAndSapId(sessionQuery.getSapId(), sessionQuery.getSubject());
			sessionQuery.setProgramSemSubjectId(programSemSubjectId);
		}
		
		long studentZone_QueryId = sessionQueryAnswerDAO.saveQuery(sessionQuery);

		sessionQuery.setId(String.valueOf(studentZone_QueryId));
		query_post_result.put("status", "success");
		query_post_result.put("message", "Query submitted successfully");
		query_post_result.put("query_id", sessionQuery.getId());
		
		return new ResponseEntity<Map<String, String>>(query_post_result, headers,  HttpStatus.OK);
	}

/* Course Mobile Api Query End */


/* Course Mobile Api Query End */

//Session Query Post 
//Session Query Get

	@PostMapping(value = "/postQuery", consumes="application/json", produces="application/json" )
	public  ResponseEntity <Map<String, String>>  mpostQuery(@RequestBody SessionQueryAnswer sessionQuery) {
		HttpHeaders headers = new HttpHeaders();
	    headers.add("Content-Type", "application/json");
	    
		// Get Latest Acads Year, month via ExamOrderBean having max order
		ExamOrderAcadsBean examorderBean = sessionQueryAnswerDAO.getExamOrderBeanWhereContentLive();
		HashMap<String, String> query_post_result = new HashMap<>();
		
		
		//ModelAndView modelnView = new ModelAndView("postQuery");

		TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
		FacultyDAO fDao = (FacultyDAO) act.getBean("facultyDAO");

		SessionDayTimeAcadsBean session = dao.findScheduledSessionById(sessionQuery.getSessionId());
		
		//modelnView.addObject("session", session);

//		String userIdEncrypted = sessionQuery.get;
//		String userIdFromURL = null;
//		try {
//			if (userIdEncrypted != null) {
//				userIdFromURL = AESencrp.decrypt(userIdEncrypted);
//			}
//		} catch (Exception e) {
//			// TODO: handle exception
//		}

		String sapId = (String) sessionQuery.getSapId();
		// login in Acads as student
//		if (userIdFromURL != null) {
//			sapId = userIdFromURL;
//		}

		sessionQuery.setSapId(sapId);

	StudentAcadsBean student = dao.getSingleStudentsData(sessionQuery.getSapId());;

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
		
		Integer count = sessionQueryAnswerDAO.checkForSameAskQuery(sessionQuery);
		if(count > 0) {
			query_post_result.put("status", "error");
			query_post_result.put("message", "This Query Already asked by you please check in My Queries Tab.");
			return new ResponseEntity<Map<String, String>>(query_post_result, headers,  HttpStatus.OK);
		}
		
		String programSemSubjectId="";
		if(sessionQuery.getProgramSemSubjectId()==null) {
			programSemSubjectId=sessionQueryAnswerDAO.getProgramSemSubjectIdOfSubjectAndSapId(sessionQuery.getSapId(), sessionQuery.getSubject());
			sessionQuery.setProgramSemSubjectId(programSemSubjectId);
		}

		long studentZone_QueryId = sessionQueryAnswerDAO.saveQuery(sessionQuery);
		
		query_post_result.put("status", "success");
		query_post_result.put("message", "Query submitted successfully");
		
		sessionQuery.setId(String.valueOf(studentZone_QueryId));

		FacultyAcadsBean faculty = fDao.findfacultyByFacultyId(queryAssignedToFacultyId);

		MailSender mailSender = (MailSender) act.getBean("mailer");

		// Only Academic Query goes to Faculty
		if ("Academic".equals(sessionQuery.getQueryType())) {
			mailSender.sendQueryPostedEmail(session, sessionQuery, faculty, faculty.getEmail());
		} else {
			// Create Case In Salesforce
			sessionQuery = salesforceHelper.createCaseInSalesforce(student, sessionQuery, session);
			sessionQueryAnswerDAO.updateSalesforceErrorMessage(sessionQuery);// update CaseId and ErrorMessage in Table
		}

//		modelnView.addObject("sessionQuery", sessionQuery);
	//
//		modelnView.addObject("action", "postQueries");

		return new ResponseEntity<Map<String, String>>(query_post_result, headers,  HttpStatus.OK);
	}

@CrossOrigin(origins="*", allowedHeaders="*")
@RequestMapping(value = "/postQueryMBAWX", method = RequestMethod.POST, consumes="application/json", produces="application/json" )
public  ResponseEntity <Map<String, String>>  mpostQueryMBAWX(@RequestBody SessionQueryAnswer sessionQueryBean) {
	HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Type", "application/json");
    
    HashMap<String, String> queryPostResponseMap = new HashMap<>();
    // validation 
    if(sessionQueryBean.getSapId() == null) {
    	queryPostResponseMap.put("status", "error");
    	queryPostResponseMap.put("message", "Sapid not found");
    	return new ResponseEntity<Map<String, String>>(queryPostResponseMap, headers,  HttpStatus.OK);
    }
    if(sessionQueryBean.getTimeBoundId() == null) {
    	queryPostResponseMap.put("status", "error");
    	queryPostResponseMap.put("message", "timebound not found");
    	return new ResponseEntity<Map<String, String>>(queryPostResponseMap, headers,  HttpStatus.OK);
    }
    if(sessionQueryBean.getAssignedToFacultyId() == null) {
    	queryPostResponseMap.put("status", "error");
    	queryPostResponseMap.put("message", "faculty id not found");
    	return new ResponseEntity<Map<String, String>>(queryPostResponseMap, headers,  HttpStatus.OK);
    }
    if(sessionQueryBean.getQuery() == null) {
    	queryPostResponseMap.put("status", "error");
    	queryPostResponseMap.put("message", "query not found");
    	return new ResponseEntity<Map<String, String>>(queryPostResponseMap, headers,  HttpStatus.OK);
    }
    if(sessionQueryBean.getSubject() == null) {
    	queryPostResponseMap.put("status", "error");
    	queryPostResponseMap.put("message", "subject not found");
    	return new ResponseEntity<Map<String, String>>(queryPostResponseMap, headers,  HttpStatus.OK);
    }
    if(sessionQueryBean.getConsumerProgramStructureId() == null) {
    	
    	String consumerProgramStructureId=sessionQueryAnswerDAO.getConsumerProgramStructureIdBySapId(sessionQueryBean.getSapId());
    	sessionQueryBean.setConsumerProgramStructureId(consumerProgramStructureId);
    }
    
    String programSemSubjectId="";
	if(sessionQueryBean.getProgramSemSubjectId()==null) {
		programSemSubjectId=sessionQueryAnswerDAO.getProgramSemSubjectIdByTimeBoundId(sessionQueryBean.getTimeBoundId());
		sessionQueryBean.setProgramSemSubjectId(programSemSubjectId);
	}
	
    // end validation logic
    FacultyDAO fDao = (FacultyDAO) act.getBean("facultyDAO");
    sessionQueryBean.setQueryType("Academic");
    sessionQueryBean.setYear("" + CURRENT_ACAD_YEAR + "");
    sessionQueryBean.setMonth(CURRENT_ACAD_MONTH);
    sessionQueryBean.setHasTimeBoundId("Y");
	try {
		
		Integer count = sessionQueryAnswerDAO.checkForSameAskQuery(sessionQueryBean);
		if(count > 0) {
			queryPostResponseMap.put("status", "error");
	    	queryPostResponseMap.put("message", "This Query Already asked by you please check in My Queries Tab.");
			return new ResponseEntity<Map<String, String>>(queryPostResponseMap, headers,  HttpStatus.OK);
		}
		
		long studentZone_QueryId = sessionQueryAnswerDAO.saveQuery(sessionQueryBean);
		
		queryPostResponseMap.put("status", "success");
		queryPostResponseMap.put("message", "Query submitted successfully");
		
		sessionQueryBean.setId(String.valueOf(studentZone_QueryId));
		sessionQueryBean.setProgramName(mapOfProgramCodeAndMasterKey().get(sessionQueryBean.getConsumerProgramStructureId()));
		FacultyAcadsBean faculty = fDao.findfacultyByFacultyId(sessionQueryBean.getAssignedToFacultyId());
	
		MailSender mailSender = (MailSender) act.getBean("mailer");
	
		mailSender.sendWXQueryPostedEmail(sessionQueryBean, faculty, faculty.getEmail());
		
		sendQueryPostedSMSToFaculty(sessionQueryBean , faculty);
		
		return new ResponseEntity<Map<String, String>>(queryPostResponseMap, headers,  HttpStatus.OK);
	}
	catch (Exception e) {
		// TODO: handle exception
		queryPostResponseMap.put("status", "error");
    	queryPostResponseMap.put("message", "Something went wrong,While creating query");
    	return new ResponseEntity<Map<String, String>>(queryPostResponseMap, headers,  HttpStatus.OK);
	}
}




//Api to retrieve queries for Session Details (Includes Public, Private Session Queries )

@PostMapping(value = "/sessionQueryList", consumes="application/json", produces="application/json")
public ResponseEntity<Map<String, List<SessionQueryAnswer>>> msessionQueryList(@RequestBody SessionQueryAnswer sessionQuery) throws Exception {
	HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Type", "application/json");
	TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
	Map<String, List<SessionQueryAnswer>> reponse= new HashMap<String, List<SessionQueryAnswer>>();
	try {
	List<SessionQueryAnswer> myQueries = getMyQueries(sessionQuery);
	reponse.put("myQueries", myQueries);
	List<SessionQueryAnswer> publicQueries = getPublicQueries(sessionQuery);
	reponse.put("publicQueries", publicQueries);
	return new ResponseEntity<Map<String, List<SessionQueryAnswer>>>( reponse, headers,  HttpStatus.OK);
	} catch (EmptyResultDataAccessException e) {
		return new ResponseEntity<Map<String, List<SessionQueryAnswer>>>( reponse, headers,  HttpStatus.OK);	
		}
}
 
// Api to retrieve queries for Course Details (Includes Public, Private Course Queries and Public, Private Session Course Queries)

@PostMapping(value = "/courseDetailsQueries", consumes = "application/json", produces = "application/json")	
public ResponseEntity<Map<String, List<SessionQueryAnswer>>> m_courseDetailsQueries(@RequestBody StudentAcadsBean student) throws Exception {
	HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Type", "application/json"); 
	TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");

	Map<String, List<SessionQueryAnswer>> response= new HashMap<String, List<SessionQueryAnswer>>();
	//StudentAcadsBean studentForMasterKey = dao.getSingleStudentsData(student.getSapid());
	ContentDAO cDao = (ContentDAO)act.getBean("contentDAO");
	StudentAcadsBean studentRegData = cDao.getStudentMaxSemRegistrationData(student.getSapid());
    List<SessionQueryAnswer> private_queries = getMyCourseQueries(student.getSapid(), student.getSubject());
    response.put("private_queries", private_queries);
    List<SessionQueryAnswer> public_queries = new ArrayList<SessionQueryAnswer>();
    if(student.getProgramSemSubjectId() == null) {
	student.setProgramSemSubjectId(sessionQueryAnswerDAO.getProgramSemSubjectIdOfSubjectAndSapId(student.getSapid(), student.getSubject()));
    }
   
    /*public_queries = getPublicCourseQueries(student.getSapid(), student.getSubject(),studentForMasterKey.getConsumerProgramStructureId());*/
    public_queries = queryAnswerService.getPublicCourseQueriesForMobile(student.getSapid(), student.getProgramSemSubjectId(), studentRegData.getYear(), studentRegData.getMonth());
    response.put("public_queries", public_queries);
    return new ResponseEntity<>(response, headers,  HttpStatus.OK);
}
	
@CrossOrigin(origins="*", allowedHeaders="*")
@RequestMapping(value = "/getMyQueryMBAWX", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")	
public ResponseEntity<PostMyQueryMBAWXBean> mgetMyQueryMBAWX(HttpServletRequest request,@RequestBody SessionQueryAnswer sessionQuery) throws Exception {
	HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Type", "application/json"); 
    
    PostMyQueryMBAWXBean postMyQueryMBAWXBean = new PostMyQueryMBAWXBean();
    if(sessionQuery.getSapId() == null) {
    	postMyQueryMBAWXBean.setStatus("error");
    	postMyQueryMBAWXBean.setMessage("Sapid not found");
    	return new ResponseEntity<>(postMyQueryMBAWXBean, headers,  HttpStatus.OK);
    }
    if(sessionQuery.getTimeBoundId() == null) {
    	postMyQueryMBAWXBean.setStatus("error");
    	postMyQueryMBAWXBean.setMessage("timebound id not found");
    	return new ResponseEntity<>(postMyQueryMBAWXBean, headers,  HttpStatus.OK);
    }
    TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
    FacultyDAO fDao = (FacultyDAO) act.getBean("facultyDAO");
    int page = request.getParameter("page") != null ? Integer.parseInt(request.getParameter("page")) : 1;
    int startFrom = (page - 1) * postMBAWXPageSize;
    List<SessionQueryAnswer> privateQueriesList = sessionQueryAnswerDAO.getMyPostQueries(sessionQuery.getSapId(), sessionQuery.getTimeBoundId(),startFrom,postMBAWXPageSize);
    //StudentBean student = dao.getSingleStudentsData(sessionQuery.getSapId());
    for (SessionQueryAnswer sessionQueryAnswer : privateQueriesList) {
    	//sessionQueryAnswer.setName(student.getFirstName() + " " + student.getLastName());
    	/*FacultyBean faculty = fDao.findfacultyByFacultyId(sessionQueryAnswer.getAssignedToFacultyId());
		
		
		if(faculty != null) {
			sessionQueryAnswer.setFacultyName(faculty.getFirstName() + " " + faculty.getLastName());
		}else {
			sessionQueryAnswer.setFacultyName(" ");
		}*/
		
    	if (!"Y".equals(sessionQueryAnswer.getIsAnswered())) {
			sessionQueryAnswer.setAnswer("Not Answered Yet");
			sessionQueryAnswer.setIsAnswered("N");
		}
	}
    postMyQueryMBAWXBean.setStatus("success");
    postMyQueryMBAWXBean.setSessionQueryAnswerList(privateQueriesList);
    return new ResponseEntity<>(postMyQueryMBAWXBean, headers,  HttpStatus.OK);
}
	
@CrossOrigin(origins="*", allowedHeaders="*")
@PostMapping(value = "/getPublicQueryMBAWX", consumes = "application/json", produces = "application/json")	
public ResponseEntity<PostMyQueryMBAWXBean> mgetPublicQueryMBAWX(HttpServletRequest request, @RequestBody SessionQueryAnswer sessionQuery) throws Exception {
	HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Type", "application/json"); 
    
    PostMyQueryMBAWXBean postMyQueryMBAWXBean = new PostMyQueryMBAWXBean();
    if(sessionQuery.getTimeBoundId() == null) {
    	postMyQueryMBAWXBean.setStatus("error");
    	postMyQueryMBAWXBean.setMessage("timebound id not found");
    	return new ResponseEntity<>(postMyQueryMBAWXBean, headers,  HttpStatus.OK);
    }
    TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
    FacultyDAO fDao = (FacultyDAO) act.getBean("facultyDAO");
    int page = request.getParameter("page") != null ? Integer.parseInt(request.getParameter("page")) : 1;
    int startFrom = (page - 1) * postMBAWXPageSize;
    List<SessionQueryAnswer> publicQueriesList = sessionQueryAnswerDAO.getPublicPostQueries(sessionQuery.getTimeBoundId(),startFrom,postMBAWXPageSize);
    for (SessionQueryAnswer sessionQueryAnswer : publicQueriesList) {
		/*StudentBean student = dao.getSingleStudentsData(sessionQueryAnswer.getSapId());
		FacultyBean faculty = fDao.findfacultyByFacultyId(sessionQueryAnswer.getAssignedToFacultyId());
		if(student != null) {
			sessionQueryAnswer.setName(student.getFirstName() + " " + student.getLastName());
		}else {
			sessionQueryAnswer.setName(" ");
		}
		
		if(faculty != null) {
			sessionQueryAnswer.setFacultyName(faculty.getFirstName() + " " + faculty.getLastName());
		}else {
			sessionQueryAnswer.setFacultyName(" ");
		}*/
		
    	if (!"Y".equals(sessionQueryAnswer.getIsAnswered())) {
			sessionQueryAnswer.setAnswer("Not Answered Yet");
			sessionQueryAnswer.setIsAnswered("N");
		}
		
	}
    postMyQueryMBAWXBean.setStatus("success");
    postMyQueryMBAWXBean.setSessionQueryAnswerList(publicQueriesList);
    return new ResponseEntity<>(postMyQueryMBAWXBean, headers,  HttpStatus.OK);
}
	
	
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
		List<SessionQueryAnswer> publicQueries = sessionQueryAnswerDAO.getPublicQueriesForCourseMobile(sapId, Subject, consumerProgramStructureId);
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
}
