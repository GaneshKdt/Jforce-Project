package com.nmims.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import com.nmims.beans.AssignmentFileBean;
import com.nmims.beans.AssignmentFilesSetbean;
import com.nmims.beans.AssignmentHistoryResponseBean;
import com.nmims.beans.ExamBookingExamBean;
import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.beans.ExamOrderExamBean;
import com.nmims.beans.FacultyExamBean;
import com.nmims.beans.LevelBasedProjectBean;
import com.nmims.beans.Page;
import com.nmims.beans.PassFailExamBean;
import com.nmims.beans.Person;
import com.nmims.beans.ProgramSubjectMappingExamBean;
import com.nmims.beans.ProjectConfiguration;
import com.nmims.beans.ProjectTitle;
import com.nmims.beans.ReadCopyCasesListBean;
import com.nmims.beans.ResultDomain;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.beans.TimetableBean;
import com.nmims.beans.TransactionsBean;
import com.nmims.beans.UploadProjectSynopsisBean;
import com.nmims.beans.UserAuthorizationExamBean;
import com.nmims.daos.AssignmentsDAO;
import com.nmims.daos.ExamBookingDAO;
import com.nmims.daos.ExamCenterDAO;
import com.nmims.daos.FacultyDAO;
import com.nmims.daos.LevelBasedProjectDAO;
import com.nmims.daos.PassFailDAO;
import com.nmims.daos.ProjectSubmissionDAO;
import com.nmims.daos.ResitExamBookingDAO;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.daos.TestDAOForRedis;
import com.nmims.factory.CopyCaseFactory;
import com.nmims.helpers.CopyCaseHelper;
import com.nmims.helpers.ExamBookingHelper;
import com.nmims.helpers.ExamBookingPDFCreator;
import com.nmims.helpers.FileUploadHelper;
import com.nmims.helpers.MailSender;
import com.nmims.interfaces.CopyCaseInterface;
import com.nmims.services.IProjectSubmissionService;
import com.nmims.services.LevelBasedProjectService;
import com.nmims.services.ProjectStudentEligibilityService;
import com.nmims.views.PDWMProjectEligibleStudentsRequestExcelView;
import com.nmims.helpers.AmazonS3Helper;

@Controller
public class ProjectSubmissionController extends BaseController{

	@Autowired
	ApplicationContext act;
	
	@Autowired
	ProjectSubmissionDAO projectSubmissionDAO;
	
	@Value("#{'${ACAD_YEAR_LIST}'.split(',')}") 
	private List<String> ACAD_YEAR_LIST; 
	
	@Value( "${ASSIGNMENT_FILES_PATH}" )
	private String ASSIGNMENT_FILES_PATH;

	@Value( "${SUBMITTED_ASSIGNMENT_FILES_PATH}" )
	private String SUBMITTED_ASSIGNMENT_FILES_PATH;

	@Value( "${ASSIGNMENT_SUBMISSIONS_ATTEMPTS}" )
	private String ASSIGNMENT_SUBMISSIONS_ATTEMPTS;
	
	@Value( "${MAX_ASSIGNMENTS_PER_FACULTY}" )
	private String MAX_ASSIGNMENTS_PER_FACULTY;
	
	@Value("${FEE_RECEIPT_PATH}")
	private String FEE_RECEIPT_PATH;
	
	//---Added for project payment----
	@Value( "${SECURE_SECRET}" )
	private String SECURE_SECRET="214cb32eed243b72501f3edc818d9737"; // secret key;
	@Value( "${ACCOUNT_ID}" )
	private String ACCOUNT_ID;
	@Value( "${V3URL}" )
	private String V3URL;
	@Value( "${RETURN_URL_PROJECT}" )
	private String RETURN_URL_PROJECT;
	@Value("${SERVICE_TAX_RULE}")
	private String SERVICE_TAX_RULE;
	@Value("${SERVER_PATH}")
	private String SERVER_PATH;
	
	@Value("${CURRENT_ACAD_MONTH}")
	private String CURRENT_ACAD_MONTH;
	
	@Value("${CURRENT_ACAD_YEAR}")
	private String CURRENT_ACAD_YEAR;
	
	@Autowired
	ResitExamBookingDAO resitExamBookingDAO;
	
	@Autowired
	CopyCaseHelper copyCaseHelper;
	
	@Autowired
	AmazonS3Helper amazonS3Helper;
	
	@Autowired
	LevelBasedProjectService levelBasedProjectService;
	
	@Autowired
	PDWMProjectEligibleStudentsRequestExcelView PDWMProjectEligibleStudentsRequestExcelView;
	
	@Autowired
	LevelBasedProjectDAO levelBasedProjectDAO;
	
	@Autowired
	ProjectStudentEligibilityService eligibilityService;
	
	@Autowired
	CopyCaseFactory CCFactory;
	
	@Autowired
	IProjectSubmissionService projectSubmissionService;
	
	private static final Logger projectPaymentsLogger = LoggerFactory.getLogger("project_payments");
	
	private static final Logger razorpayLogger = LoggerFactory.getLogger("webhook_payments");
	
	private static final Logger facultyEvaluationLogger = LoggerFactory.getLogger("facultyEvaluation");
	
	private static final Logger projectSubmissionLogger = LoggerFactory.getLogger("projectSubmission");
	
	private static final Logger projectCCLogger = LoggerFactory.getLogger("projectCopyCase");
	
	private final String ONLINE_PAYMENT_INITIATED = "Online Payment Initiated"; 
	private final String BOOKING_SUCCESS_MSG = "Your payment is complete. "
			+ "Please click <a href=\"viewProject?subject=Project\"> here </a> to submit project";
	private final String PDWM_BOOKING_SUCCESS_MSG = "Your payment is complete. "
			+ "Please click <a href=\"viewProject?subject=Module 4 - Project\"> here </a> to submit project";
	private int examFeesPerSubject = 750;
	private final List<String> SYNOPSIS_STATUS = Arrays.asList("Payment pending","Payment failed","Submitted","Rejected","Approved");
	
	private final String GATEWAY_STATUS_FAILED = "Payment Failed";
	private final String GATEWAY_STATUS_SUCCESSFUL = "Payment Successfull";
	private final String PROJECT_PAYMENT_SUCCESSFUL = "Online Payment Successful";
	private final String PROJECT_TRANSACTION_FAILED  = "Transaction Failed";
	private final String PROJECT_MANUALLY_APPROVED  = "Online Payment Manually Approved";
	private final String PROJECT_PAYMENT_INITIATED = "Online Payment Initiated";
	
	private HashMap<String, String> examCenterIdNameMap = null;
	
	private List<String> projectSubjectList =  new ArrayList<String>(Arrays.asList("Module 4 - Project","Project"));
	
	@Autowired
	ExamBookingHelper examBookingHelper;
	
	@Autowired
	FileUploadHelper fileUploadHelper;
	/**
	 * Refresh Cache function to refresh cache
	 * @param 
	 * none
	 * @return 
	 * none
	 * */
	public String RefreshCache() {
		programSubjectMappingList = null;
		getProgramSubjectMappingList();	
		
		subjectList = null;
		getSubjectList();
		
		programList = null;
		getProgramList();
		
		examCenterIdNameMap = null;
		getExamCenterIdNameMap();
		
		return null;
	}
	
	public HashMap<String, String> getExamCenterIdNameMap(){
		if(this.examCenterIdNameMap == null || this.examCenterIdNameMap.size() == 0){
			ExamCenterDAO dao = (ExamCenterDAO)act.getBean("examCenterDAO");
			this.examCenterIdNameMap = dao.getExamCenterIdNameMap();
		}
		return examCenterIdNameMap;
	}
	private Map<String, PassFailExamBean> getSubjectPassFailBeanMap(String sapid) {
		ArrayList<PassFailExamBean> failList = (ArrayList<PassFailExamBean>)resitExamBookingDAO.getFailedSubjectsList(sapid);
		Map<String, PassFailExamBean> subjectPassFailBeanMap = new HashMap<String, PassFailExamBean>();
		if(failList != null){
			for (PassFailExamBean passFailBean : failList) {
				subjectPassFailBeanMap.put(passFailBean.getSubject(), passFailBean);
			}
		}

		return subjectPassFailBeanMap;
	}
	private ArrayList<StudentExamBean> exemptStudentList = null;
	
	
	public ArrayList<StudentExamBean> getProjectExemptStudentList(){
		//if(this.exemptStudentList == null || this.exemptStudentList.size() == 0){
			ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
			this.exemptStudentList = eDao.getProjectExemptStudentList();
		//}
		return exemptStudentList;
	}
	HashMap<String, ArrayList<String>>	studentFreeSubjectsMap = null;
	public HashMap<String, ArrayList<String>> getStudentFreeSubjectsMap(){
		//if(this.studentFreeSubjectsMap == null || this.studentFreeSubjectsMap.size() == 0){
			ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
			this.studentFreeSubjectsMap = eDao.getStudentFreeProjectMap();
		//}
		return this.studentFreeSubjectsMap;
	}
	//---project payment end-----

	private static final int BUFFER_SIZE = 4096;
	private static final Logger logger = LoggerFactory.getLogger(ProjectSubmissionController.class);

	@Value("#{'${CURRENT_YEAR_LIST}'.split(',')}")
	private ArrayList<String> currentYearList; 
	
	@Value("#{'${EXAM_MONTH_LIST}'.split(',')}")
	private List<String> EXAM_MONTH_LIST;
	
	private ArrayList<String> subjectList = null; 
	private final int pageSize = 20;
	private ArrayList<ProgramSubjectMappingExamBean> programSubjectMappingList = null;
	private static final Long MAX_FILE_SIZE_LIMIT = 11534336L;				//11 MB in bytes (11 * 1024 * 1024)
	private ArrayList<String> programList = null;
	
	
	@ModelAttribute("q1MarksOptionsList")
	public ArrayList<String> getQ1MarksOptionsList(){
		return new ArrayList<String>(Arrays.asList( "0","1", "2","3","4","5","6","7","8","9","10")); 
	}
	
	@ModelAttribute("q2MarksOptionsList")
	public ArrayList<String> getQ2MarksOptionsList(){
		return new ArrayList<String>(Arrays.asList( "0","1", "2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20")); 
	}
	
	@ModelAttribute("q3MarksOptionsList")
	public ArrayList<String> getQ3MarksOptionsList(){
		return new ArrayList<String>(Arrays.asList( "0","1", "2","3","4","5","6","7","8","9","10","11","12","13","14","15")); 
	}
	
	@ModelAttribute("q4MarksOptionsList")
	public ArrayList<String> getQ4MarksOptionsList(){
		return new ArrayList<String>(Arrays.asList( "0","1", "2","3","4","5","6","7","8","9","10")); 
	}
	
	@ModelAttribute("q5MarksOptionsList")
	public ArrayList<String> getQ5MarksOptionsList(){
		return new ArrayList<String>(Arrays.asList( "0","1", "2","3","4","5","6","7","8","9","10","11","12","13","14","15")); 
	}
	
	@ModelAttribute("q6MarksOptionsList")
	public ArrayList<String> getQ6MarksOptionsList(){
		return new ArrayList<String>(Arrays.asList( "0","1", "2","3","4","5","6","7","8","9","10")); 
	}

	@ModelAttribute("q7MarksOptionsList")
	public ArrayList<String> getQ7MarksOptionsList(){
		return new ArrayList<String>(Arrays.asList( "0","1", "2","3","4","5","6","7","8","9","10")); 
	}
	
	@ModelAttribute("q8MarksOptionsList")
	public ArrayList<String> getQ8MarksOptionsList(){
		return new ArrayList<String>(Arrays.asList( "0","1", "2","3","4","5")); 
	}
	
	
	@ModelAttribute("q9MarksOptionsList")
	public ArrayList<String> getQ9MarksOptionsList(){
		return new ArrayList<String>(Arrays.asList( "0","1", "2","3","4","5")); 
	}
	
	@ModelAttribute("common10MarksOptionsList")
	public ArrayList<String> getCommon10MarksOptionsList(){
		return new ArrayList<String>(Arrays.asList( "0","1", "2","3","4","5","6","7","8","9","10")); 
	}
	
	
	public ArrayList<String> getSubjectList(){
		if(this.subjectList == null){
			StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			this.subjectList = dao.getActiveSubjects();
		}
		return subjectList;
	}

	public HashMap<String, String> getFacultyList(){
		FacultyDAO dao = (FacultyDAO)act.getBean("facultyDAO");
		HashMap<String, String> map = dao.getFacultyIdNameMap();
		return (HashMap<String, String>)sortByValue(map);
	}
	
	public  <K, V extends Comparable<? super V>> Map<K, V> 
	sortByValue( Map<K, V> map )
	{
		List<Map.Entry<K, V>> list =
				new LinkedList<Map.Entry<K, V>>( map.entrySet() );
		Collections.sort( list, new Comparator<Map.Entry<K, V>>()
				{
			public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 )
			{
				return (o1.getValue()).compareTo( o2.getValue() );
			}
				} );

		Map<K, V> result = new LinkedHashMap<K, V>();
		for (Map.Entry<K, V> entry : list)
		{
			result.put( entry.getKey(), entry.getValue() );
		}
		return result;
	}
	
	@ModelAttribute("programList")
	public ArrayList<String> getProgramList(){
		if(this.programList == null){
			StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			this.programList = dao.getAllPrograms();
		}
		return programList;
	}
	
	private HashMap<String, String> programNameMap = null;
	@ModelAttribute("programNameMap")
	public HashMap<String, String> getProgramNameMap(){
		if(this.programList == null){
			StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			this.programNameMap = dao.getProgramDetails();
		}
		return programNameMap;
	}
	
	public ProjectSubmissionController(){
	}

	public ArrayList<ProgramSubjectMappingExamBean> getProgramSubjectMappingList(){
		if(this.programSubjectMappingList == null || this.programSubjectMappingList.size() == 0){
			ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
			this.programSubjectMappingList = eDao.getProgramSubjectMappingList();
		}
		return programSubjectMappingList;
	}




	@RequestMapping(value = "/student/downloadStudentProjectFile", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadStudentProjectFile(HttpServletRequest request, HttpServletResponse response , Model m){
		ModelAndView modelnView = new ModelAndView("downloadAssignmentFile");

		String fullPath = request.getParameter("filePath");
		//String subject = request.getParameter("subject");

		ArrayList<String> subjects = (ArrayList<String>)request.getSession().getAttribute("subjectsForStudent");
		try{
			// get absolute path of the application
			ServletContext context = request.getSession().getServletContext();
			String appPath = context.getRealPath("");

			// construct the complete absolute path of the file
			//String fullPath = appPath + filePath;		
			File downloadFile = new File(fullPath);
			FileInputStream inputStream = new FileInputStream(downloadFile);

			// get MIME type of the file
			String mimeType = context.getMimeType(fullPath);
			if (mimeType == null) {
				// set to binary type if MIME mapping not found
				mimeType = "application/octet-stream";
			}

			// set content attributes for the response
			response.setContentType(mimeType);
			response.setContentLength((int) downloadFile.length());

			// set headers for the response
			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"",
					downloadFile.getName());
			response.setHeader(headerKey, headerValue);

			// get output stream of the response
			OutputStream outStream = response.getOutputStream();

			byte[] buffer = new byte[BUFFER_SIZE];
			int bytesRead = -1;

			// write bytes read from the input stream into the output stream
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, bytesRead);
			}
			inputStream.close();
			outStream.close();
		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in downloading file.");
		}
		AssignmentFileBean searchBean = (AssignmentFileBean)request.getSession().getAttribute("searchBean");
		modelnView.addObject("searchBean",searchBean);
		modelnView.addObject("yearList", ACAD_YEAR_LIST);
		modelnView.addObject("subjectList", getSubjectList());
		return modelnView;
	}
	/*old project submission code
	 * @RequestMapping(value = "/viewProject", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView viewProject(HttpServletRequest request, HttpServletResponse response, @ModelAttribute AssignmentFileBean assignmentFile) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView modelnView = new ModelAndView("project/submitProject");
		String sapId = (String)request.getSession().getAttribute("userId");
		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
		List<StudentMarksBean> registrationList = eDao.getRegistrations(sapId);
		assignmentFile = projectSubmissionDAO.findById(assignmentFile);
		if(assignmentFile == null){
			request.setAttribute("canSubmit","false");
			setError(request, "Project Submission is not live currently");
			return modelnView;
		}
		int lastSem = -1;
		for (int i = 0; i < registrationList.size(); i++) {
			StudentMarksBean bean = registrationList.get(i);
			int sem = Integer.parseInt(bean.getSem());
			if(sem >= lastSem){
				lastSem = sem;
			}
		}
		boolean  canSubmit = false;
		if(lastSem < 4 && lastSem != -1){ 
			setError(request, "Project Submission is applicable for Sem 4 Registration only");
			request.setAttribute("canSubmit", "false");
		}else{
			PassFailDAO passFailDAO = (PassFailDAO)act.getBean("passFailDAO");
			ArrayList<String> passedSubjects = passFailDAO.getPassSubjectsNamesForAStudent(sapId);
			if(passedSubjects != null && passedSubjects.contains("Project")){ 
				setError(request, "You have already cleared Project.");
				request.setAttribute("canSubmit", "false");
			}else{
					ArrayList<String>  examBookedSubjets = eDao.getConfirmedBookingSubjects(sapId);
					if(examBookedSubjets.contains("Project")){
						canSubmit= true;
						request.setAttribute("canSubmit","true");
					}else{
						request.setAttribute("canSubmit", "false");
						setError(request, "You can not submit Project yet, as you have not registered for Project Submission for current Exam cycle");
					}
			}
		}
		
		String status = "Not Submitted";
		String attempts = "0";
		AssignmentFileBean studentSubmissionStatus = projectSubmissionDAO.getProjectSubmissionStatus(assignmentFile.getSubject(), sapId);
		if(studentSubmissionStatus != null){
			status = studentSubmissionStatus.getStatus();
			attempts = studentSubmissionStatus.getAttempts();
			assignmentFile.setPreviewPath(studentSubmissionStatus.getPreviewPath());
			assignmentFile.setStudentFilePath(studentSubmissionStatus.getStudentFilePath());
		}
		assignmentFile.setStatus(status);
		assignmentFile.setAttempts(attempts);

		String startDate = assignmentFile.getStartDate();
		startDate = startDate.replaceAll("T", " ");
		assignmentFile.setStartDate(startDate.substring(0,19));
		
		String endDate = assignmentFile.getEndDate();
		endDate = endDate.replaceAll("T", " ");
		
		assignmentFile.setEndDate(endDate.substring(0,19));
		int maxAttempts = Integer.parseInt(ASSIGNMENT_SUBMISSIONS_ATTEMPTS);
		
		ArrayList<String> timeExtendedStudentIdSubjectList =  dao.assignmentExtendedSubmissionTime();
		modelnView.addObject("timeExtendedStudentIdSubjectList", timeExtendedStudentIdSubjectList);
		request.getSession().setAttribute("timeExtendedStudentIdSubjectList",timeExtendedStudentIdSubjectList);
		String subject = request.getParameter("subject");
		request.getSession().setAttribute("subject",subject);
		modelnView.addObject("maxAttempts",maxAttempts);
		modelnView.addObject("assignmentFile",assignmentFile);
		modelnView.addObject("yearList", ACAD_YEAR_LIST);
		modelnView.addObject("subjectList", getSubjectList());
		modelnView.addObject("canSubmit",canSubmit);
		return modelnView;
	}*/

	//Added by Steffi for project payment
	  @RequestMapping(value = "/student/viewProject", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView viewProject(HttpServletRequest request, HttpServletResponse response, @ModelAttribute AssignmentFileBean assignmentFile) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}

		TestDAOForRedis daoForRedis = (TestDAOForRedis)act.getBean("testDaoForRedis");
			if(daoForRedis.checkForFlagValueInCache("movingResultsToCache","Y") ) {
				return new ModelAndView("noDataAvailable");
			}
		String successMessage = (String) request.getSession().getAttribute("successMsg");
		request.getSession().removeAttribute("successMsg");
		if(successMessage != null){
			request.setAttribute("success", "true");
			request.setAttribute("successMessage", successMessage);
		}
		
		String sapId = (String)request.getSession().getAttribute("userId");
		projectSubmissionLogger.info("Pg visit /viewProject Sapid - {}",sapId);
		ModelAndView modelnView = new ModelAndView("project/submitProject");
		try {
			
		//Make two live cycle code -----START-----
		String isClearedOrResultAwaited = "N";
		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
		StudentExamBean studentDetails = (StudentExamBean) request.getSession().getAttribute("studentExam");
		List<StudentMarksBean> registrationList = eDao.getRegistrations(sapId);
		ArrayList<String> timeExtendedStudentIdSubjectList =  dao.assignmentExtendedSubmissionTime(); // to check for students for whom the submission time has been extended.
		//ArrayList<String> applicableStudentIdSubjectList =  projectSubmissionDAO.getProjectApplicableStudentList(eDao.getLiveExamYear(),eDao.getLiveExamMonth()); // to check for students for whom applicable
		String subject = request.getParameter("subject");
		assignmentFile.setSubject(subject);
        String key = sapId+subject;
        String programSem = eligibilityService.getProjectApplicableProgramSem(studentDetails.getProgram());
		//Get free subjects for Exam, either for entire sem, or selectively allowed free for specific subject:START
		StudentExamBean student = (StudentExamBean)request.getSession().getAttribute("studentExam");
		String consumerProgramStructureId = null;
		if(student.getConsumerProgramStructureId() != null || !StringUtils.isBlank(student.getConsumerProgramStructureId())) {
			consumerProgramStructureId= student.getConsumerProgramStructureId();
			if(consumerProgramStructureId != null || !StringUtils.isBlank(consumerProgramStructureId)) {
				assignmentFile.setConsumerProgramStructureId(consumerProgramStructureId);
			}
		} else{
			setError(request, "Program Structure ID Missing");
			return modelnView;
		}
		
		// Set Student Applicable Exam Month Year
		String method = "viewProject()";
		AssignmentFileBean examMonthYearBean = eligibilityService.getStudentApplicableExamMonthYear(sapId, subject, method);
		assignmentFile.setMonth(examMonthYearBean.getMonth());
		assignmentFile.setYear(examMonthYearBean.getYear());
		
		//Get free subjects for Exam, either for entire sem, or selectively allowed free for specific subject
		ArrayList<String> freeSubjects = getFreeSubjectsNew(student,assignmentFile.getYear(),assignmentFile.getMonth());
		ArrayList<String> individualFreeSubjects = getStudentFreeSubjectsMapNew(assignmentFile.getYear(),assignmentFile.getMonth()).get(sapId);
		if(individualFreeSubjects != null && individualFreeSubjects.size() > 0){
			freeSubjects.addAll(individualFreeSubjects);
		}
		
		//Get student project submission for previous exam cycle start
		boolean isResultLiveForLastProjectSubmissionCycle = eDao.isResultLiveForLastProjectSubmissionCycle(); //check if result for last exam cycle is live
		boolean resultsAwaited = false;
		
		//If last cycle(resit) result is not live and currently submission is going on then those who have submitted they should get Result Awaited Status. -Prashant(20-03-2023)
		if(!isResultLiveForLastProjectSubmissionCycle){
			ArrayList<AssignmentFileBean> projectSubmittedButNotProcessed = dao.getResultAwaitedProjectSubmittedList(student.getSapid());
//			ArrayList<ExamBookingExamBean> projectExamBookedInLastCycleNotSubmitted = dao.getProjectExamBookedInLastCycleButNotSubmitted(sapId);
//			ArrayList<ExamBookingExamBean> projectExamBookedInLastCycle = dao.getProjectExamBookedInLastCycle(sapId);
//			if(projectSubmittedButNotProcessed.size()>0 && projectExamBookedInLastCycle.size()>0){
			if(projectSubmittedButNotProcessed.size()>0){ // if submitted in prev cycle then show result awaited -Prashant 20-04-2023
				resultsAwaited = true;
			}
		}
		//Get student project submission for previous exam cycle end
		
		int lastSem = -1;
		for (int i = 0; i < registrationList.size(); i++) {
			StudentMarksBean bean = registrationList.get(i);
			int sem = Integer.parseInt(bean.getSem());
			if(sem >= lastSem){
				lastSem = sem;
			}
		}
		boolean  canSubmit = false;
		boolean  paymentApplicable = false;
		boolean  isApplicableToSubmit = false;
		boolean  submitted = false;
		
		boolean isPGorMBAStudent = studentDetails.getProgram().startsWith("PG") || studentDetails.getProgram().startsWith("MBA")  && !"Diageo".equalsIgnoreCase(studentDetails.getConsumerType()); //Only sem 4 (MBA or PG) students allowed to submit
		boolean pg_mba_SemCheck = lastSem >= 4 && lastSem != -1;
		boolean isPdWmStudent = "PD - WM".equalsIgnoreCase(studentDetails.getProgram()) && "Retail".equalsIgnoreCase(studentDetails.getConsumerType());
		boolean pdWmSemCheck = lastSem > 1 && lastSem != -1;
		boolean isUGStudent = studentDetails.getProgram().startsWith("B") && !"Diageo".equalsIgnoreCase(studentDetails.getConsumerType()); //Only sem 5 (UG) students allowed to submit
		boolean UGSemCheck = lastSem >= 5 && lastSem != -1;
		
		if(!isPGorMBAStudent && !isPdWmStudent && !isUGStudent) {
			setError(request, "Project Submission not applicable for current registration!");
			request.setAttribute("canSubmit", "false");
			request.setAttribute("paymentApplicable", "false");
			request.setAttribute("submitted", submitted);
			return modelnView;
		}else if( (isUGStudent && !UGSemCheck) || (isPGorMBAStudent && !pg_mba_SemCheck) || (isPdWmStudent && !pdWmSemCheck) ){
			setError(request, "Project Submission is applicable for Sem " +programSem+" Registration only");
			request.setAttribute("canSubmit", "false");
			request.setAttribute("paymentApplicable", "false");
			request.setAttribute("submitted", submitted);
			return modelnView;
		}else{
			
			PassFailDAO passFailDAO = (PassFailDAO)act.getBean("passFailDAO");
			ArrayList<String> passedSubjects = passFailDAO.getPassSubjectsNamesForAStudent(sapId);
			
			if(resultsAwaited){
				setError(request, "Your previous project Submission Results Awaited.");
				request.setAttribute("canSubmit", "false");
				request.setAttribute("paymentApplicable", "false");
				isClearedOrResultAwaited ="Y";
			}else if(passedSubjects != null && passedSubjects.contains(subject)){ //Check if student already cleared project
				setError(request, "You have already cleared "+subject);
				request.setAttribute("canSubmit", "false");
				request.setAttribute("paymentApplicable", "false");
				isClearedOrResultAwaited ="Y";
			}else {
			
//				if(!timeExtendedStudentIdSubjectList.contains(key)){
//					if(consumerProgramStructureId != null || !StringUtils.isBlank(consumerProgramStructureId)) {
//						assignmentFile.setConsumerProgramStructureId(consumerProgramStructureId);	
//	        		}
					
					// Commented due to changing PD WM flow as noraml PG project submission -Prashant 20-03-2023
					/*if(isPdWmStudent) {
						AssignmentFileBean assignmentFile2 = new AssignmentFileBean();
						LevelBasedProjectBean recentMapping = levelBasedProjectDAO.getRecentStudentGuideMapping(student.getSapid());
						assignmentFile2.setSubject(subject);
						assignmentFile2.setConsumerProgramStructureId(assignmentFile.getConsumerProgramStructureId());
						if(recentMapping != null) {
							assignmentFile2.setYear(recentMapping.getYear());
							assignmentFile2.setMonth(recentMapping.getMonth());
						}else {
							setError(request, "Student Guide Mapping not found");
							return modelnView;
						}
						
						assignmentFile = projectSubmissionDAO.findProjectGuidelinesForApplicableCycle(assignmentFile2);
//						projectSubmissionLogger.info("guideline>>"+assignmentFile);
						if(assignmentFile == null){	
						AssignmentFileBean studentSubmissionStatus = projectSubmissionDAO.getProjectSubmissionStatusForCycle(subject, sapId,assignmentFile2.getYear(),assignmentFile2.getMonth());
							if(studentSubmissionStatus != null){
//								projectSubmissionLogger.info("studentSubmissionStatus>>"+studentSubmissionStatus);
								submitted =true;
								modelnView.addObject("assignmentFile",studentSubmissionStatus);
								modelnView.addObject("submitted", submitted);
								modelnView.addObject("canSubmit", canSubmit);
							}else{
								modelnView.addObject("assignmentFile",new AssignmentFileBean());
								modelnView.addObject("submitted", submitted);
								modelnView.addObject("canSubmit", canSubmit);
							}
							setError(request, "Project Submission is not live currently");
							return modelnView;
						}
					} else */
						if (!timeExtendedStudentIdSubjectList.contains(key)) {
						assignmentFile = projectSubmissionDAO.findProjectGuidelinesForApplicableCycle(assignmentFile);
//						projectSubmissionLogger.info("project by id"+assignmentFile);
						if (assignmentFile == null) {
							assignmentFile.setMonth(examMonthYearBean.getMonth());
							assignmentFile.setYear(examMonthYearBean.getYear());
							AssignmentFileBean studentSubmissionStatus = projectSubmissionDAO.getProjectSubmissionStatusForCycle(subject, sapId, assignmentFile.getYear(),assignmentFile.getMonth());
							if (studentSubmissionStatus != null) {
//								projectSubmissionLogger.info("studentSubmissionStatus>>"+studentSubmissionStatus);
								submitted = true;
								modelnView.addObject("assignmentFile", studentSubmissionStatus);
								modelnView.addObject("submitted", submitted);
								modelnView.addObject("canSubmit", canSubmit);
							} else {
								modelnView.addObject("assignmentFile", new AssignmentFileBean());
								modelnView.addObject("submitted", submitted);
								modelnView.addObject("canSubmit", canSubmit);
							}
							setError(request, "Project Submission is not live currently");
							return modelnView;
						}
//					}
					}else{
						// for extended student guideline
						assignmentFile = projectSubmissionDAO.findProjectGuidelinesFromLiveSettingForApplicableCycle(assignmentFile);
					}

				String year = assignmentFile.getYear();
				String month = assignmentFile.getMonth();
				if(student.getExamMode().equalsIgnoreCase("Online")){
					if("Diageo".equalsIgnoreCase(studentDetails.getConsumerType())){
						isApplicableToSubmit = eDao.checkIfStudentApplicableForSubmission(sapId,year,month,"2"); //Only sudents who completed min 5 months in sem 2 allowed to submit
					} 
					else if(isPdWmStudent) {
						ProjectConfiguration projectConfiguration = null;
						projectConfiguration = (ProjectConfiguration) request.getSession().getAttribute("projectConfiguration");
						if (projectConfiguration != null && projectConfiguration.getHasSynopsis().equalsIgnoreCase("Y")) {
							LevelBasedProjectDAO levelBasedProjectDAO = (LevelBasedProjectDAO) act.getBean("levelBasedProjectDAO");
							boolean synopsis_submitted= false;
							UploadProjectSynopsisBean inputBean = new UploadProjectSynopsisBean();
							inputBean.setYear(year);
							inputBean.setMonth(month);
							inputBean.setSapid(sapId);
							synopsis_submitted = levelBasedProjectDAO.checkSynopsisSubmissionCount(inputBean,SYNOPSIS_STATUS);
							if(!synopsis_submitted) {
								request.setAttribute("canSubmit","false");
								canSubmit=false;
								setError(request, "Synopsis Not Yet Submitted.");
							}
						}
						isApplicableToSubmit = eDao.checkIfStudentApplicableForSubmission(sapId, year, month, programSem); // Sem 2 students
//					}else if(isUGStudent){
//						isApplicableToSubmit = eDao.checkIfStudentApplicableForSubmission(sapId,year,month,"5"); //Only sudents who completed min 5 months in sem 5 allowed to submit
					}
					else {
						isApplicableToSubmit = eDao.checkIfStudentApplicableForSubmission(sapId,year,month,programSem); //Only sudents who completed min 5 months in sem 4/5/6 allowed to submit
					}
				}
				if(student.getExamMode().equalsIgnoreCase("Offline")){
					isApplicableToSubmit = true; //Only for offline students as their registration table entry is blank.
				}
				if(timeExtendedStudentIdSubjectList.contains(key)){
					isApplicableToSubmit = true; //Only for extended list students as their registration is allowed by admin after date
				}
					
				if(isApplicableToSubmit){
					ArrayList<String>  examBookedSubjets = projectSubmissionDAO.getProjectBookingforCurrentLiveExamNew(sapId,assignmentFile.getYear(),assignmentFile.getMonth()); //Check if project payment done for current live project submission cycle.
					/*if(examBookedSubjets.contains("Project") || freeSubjects.contains("Project")){
						canSubmit= true;
						request.setAttribute("canSubmit","true");
						request.setAttribute("paymentApplicable", "false");
//					}else if(examBookedSubjets.contains("Module 4 - Project") || synopsis_submitted || freeSubjects.contains("Module 4 - Project")){
					}else if(assignmentFile.getSubject().equalsIgnoreCase("Module 4 - Project") && !levelBasedProjectService.isResitCycleMonth(month)){
						canSubmit= true;
						request.setAttribute("canSubmit","true");
						request.setAttribute("paymentApplicable", "false");
					}else if(examBookedSubjets.contains("Module 4 - Project") && levelBasedProjectService.isResitCycleMonth(month)){
						canSubmit= true;
						request.setAttribute("canSubmit","true");
						request.setAttribute("paymentApplicable", "false");
					}*/
					if(examBookedSubjets.contains(subject) || freeSubjects.contains(subject)){
						canSubmit= true;
						request.setAttribute("canSubmit","true");
						request.setAttribute("paymentApplicable", "false");
					}else{
						paymentApplicable= true;
						request.setAttribute("canSubmit", "false");
						request.setAttribute("paymentApplicable", "true");
						//setError(request, "You can not submit Project yet, as you have not registered for Project Submission for current Exam cycle");
					}
				}else{
					request.setAttribute("canSubmit","false");
					canSubmit=false;
					setError(request, "Project Submission is not applicable currently");
					//return modelnView;
				}
				
				String status = "Not Submitted";
				String attempts = "0";
				AssignmentFileBean studentSubmissionStatus = projectSubmissionDAO.getProjectSubmissionStatusForACycle(assignmentFile.getSubject(), sapId,assignmentFile.getYear(),assignmentFile.getMonth());
				if(studentSubmissionStatus != null){
					status = studentSubmissionStatus.getStatus();
					attempts = studentSubmissionStatus.getAttempts();
					submitted=true;
					assignmentFile.setPreviewPath(studentSubmissionStatus.getPreviewPath());
					assignmentFile.setStudentFilePath(studentSubmissionStatus.getStudentFilePath());
				}
				assignmentFile.setStatus(status);
				assignmentFile.setAttempts(attempts);

				String startDate = assignmentFile.getStartDate();
				startDate = startDate.replaceAll("T", " ");
				assignmentFile.setStartDate(startDate.substring(0,19));
		
				String endDate = assignmentFile.getEndDate();
				endDate = endDate.replaceAll("T", " ");
				assignmentFile.setEndDate(endDate.substring(0,19));
			}
			if(isClearedOrResultAwaited.equalsIgnoreCase("Y")){
				assignmentFile = projectSubmissionDAO.findProjectGuidelinesCurrentLiveCycle(assignmentFile);
			}
		}
		//Make two live cycle code -----END-----
		
			
		// Normal single live cycle code -----START-----
			//String sapId = (String)request.getSession().getAttribute("userId");
			/*
			AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
			ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
			StudentExamBean studentDetails = (StudentExamBean) request.getSession().getAttribute("studentExam");
			List<StudentMarksBean> registrationList = eDao.getRegistrations(sapId);
			ArrayList<String> timeExtendedStudentIdSubjectList =  dao.assignmentExtendedSubmissionTime(); // to check for students for whom the submission time has been extended.
			//ArrayList<String> applicableStudentIdSubjectList =  projectSubmissionDAO.getProjectApplicableStudentList(eDao.getLiveExamYear(),eDao.getLiveExamMonth()); // to check for students for whom applicable
			String subject = request.getParameter("subject");
	        String key = sapId+subject;
			//Get free subjects for Exam, either for entire sem, or selectively allowed free for specific subject:START
			StudentExamBean student = (StudentExamBean)request.getSession().getAttribute("studentExam");
			ArrayList<String> freeSubjects = getFreeSubjects(student);
			ArrayList<String> individualFreeSubjects = getStudentFreeSubjectsMap().get(sapId);
			if(individualFreeSubjects != null && individualFreeSubjects.size() > 0){
				freeSubjects.addAll(individualFreeSubjects);
			}
			//Get free subjects for Exam, either for entire sem, or selectively allowed free for specific subject:END
			//Get student project submission for previous exam cycle start
			boolean isResultLiveForLastProjectSubmissionCycle = eDao.isResultLiveForLastProjectSubmissionCycle(); //check if result for last exam cycle is live
			boolean resultsAwaited = false;
			if(!isResultLiveForLastProjectSubmissionCycle){
				ArrayList<AssignmentFileBean> projectSubmittedButNotProcessed = dao.getResultAwaitedProjectSubmittedList(student.getSapid());
				ArrayList<ExamBookingExamBean> projectExamBookedInLastCycleNotSubmitted = dao.getProjectExamBookedInLastCycleButNotSubmitted(sapId);
				if(projectSubmittedButNotProcessed.size()>0 || projectExamBookedInLastCycleNotSubmitted.size()>0){
					resultsAwaited = true;
				}
			}//Get student project submission for previous exam cycle end
			
			int lastSem = -1;
			for (int i = 0; i < registrationList.size(); i++) {
				StudentMarksBean bean = registrationList.get(i);
				int sem = Integer.parseInt(bean.getSem());
				if(sem >= lastSem){
					lastSem = sem;
				}
			}
			boolean  canSubmit = false;
			boolean  paymentApplicable = false;
			boolean  isApplicableToSubmit = false;
			boolean  submitted = false;
			
			boolean isPGorMBAStudent = studentDetails.getProgram().startsWith("PG") || studentDetails.getProgram().startsWith("MBA")  && !"Diageo".equalsIgnoreCase(studentDetails.getConsumerType()); //Only sem 4 (MBA or PG) students allowed to submit
			boolean pg_mba_SemCheck = lastSem >= 4 && lastSem != -1;
			boolean isPdWmStudent = "PD - WM".equalsIgnoreCase(studentDetails.getProgram()) && "Retail".equalsIgnoreCase(studentDetails.getConsumerType());
			boolean pdWmSemCheck = lastSem > 1 && lastSem != -1;
			boolean isUGStudent = studentDetails.getProgram().startsWith("B") && !"Diageo".equalsIgnoreCase(studentDetails.getConsumerType()); //Only sem 5 (UG) students allowed to submit
			boolean UGSemCheck = lastSem >= 5 && lastSem != -1;
			
			if(!isPGorMBAStudent && !isPdWmStudent && !isUGStudent) {
				setError(request, "Project Submission not applicable for current registration!");
				request.setAttribute("canSubmit", "false");
				request.setAttribute("paymentApplicable", "false");
				request.setAttribute("submitted", submitted);
				return modelnView;
			}else if(isUGStudent && !UGSemCheck && studentDetails.getProgram().equalsIgnoreCase("BBA-BA")){ 
				setError(request, "Project Submission is applicable for Sem 6 Registration only");
				request.setAttribute("canSubmit", "false");
				request.setAttribute("paymentApplicable", "false");
				request.setAttribute("submitted", submitted);
				return modelnView;
			}else if(isUGStudent && !UGSemCheck){ 
				setError(request, "Project Submission is applicable for Sem 5 Registration only");
				request.setAttribute("canSubmit", "false");
				request.setAttribute("paymentApplicable", "false");
				request.setAttribute("submitted", submitted);
				return modelnView;
			}else if(isPGorMBAStudent && !pg_mba_SemCheck){ 
				setError(request, "Project Submission is applicable for Sem 4 Registration only");
				request.setAttribute("canSubmit", "false");
				request.setAttribute("paymentApplicable", "false");
				request.setAttribute("submitted", submitted);
				return modelnView;
			}else if(isPdWmStudent && !pdWmSemCheck){ 
				setError(request, "Project Submission is applicable for Sem 2 Registration only");
				request.setAttribute("canSubmit", "false");
				request.setAttribute("paymentApplicable", "false");
				request.setAttribute("submitted", submitted);
				return modelnView;
//			}else if(isPdWmStudent && pdWmSemCheck && !applicableStudentIdSubjectList.contains(key)){
//				// not applicable for project submittion
//				setError(request, "Project Submission is not applicable for you, if any query please contact support");
//				request.setAttribute("canSubmit", "false");
//				request.setAttribute("paymentApplicable", "false");
//				request.setAttribute("submitted", submitted);
//				return modelnView;
			}
			else{
				
		        if(!timeExtendedStudentIdSubjectList.contains(key)){
		        	if(student.getConsumerProgramStructureId() != null || !StringUtils.isBlank(student.getConsumerProgramStructureId())) {
		        		assignmentFile.setConsumerProgramStructureId(student.getConsumerProgramStructureId());
					} else{
						setError(request, "Program Structure ID Missing");
		    			return modelnView;
					}        	
		        	assignmentFile = projectSubmissionDAO.findById(assignmentFile);
		    		if(assignmentFile == null){
		    			request.setAttribute("canSubmit","false");
		    			assignmentFile = projectSubmissionDAO.findProjectGuidelines(assignmentFile);
		    			AssignmentFileBean studentSubmissionStatus = projectSubmissionDAO.getProjectSubmissionStatus(subject, sapId);
		    			if(studentSubmissionStatus != null){
		    				submitted =true;
		    				modelnView.addObject("assignmentFile",studentSubmissionStatus);
		    				modelnView.addObject("submitted", submitted);
		    				modelnView.addObject("canSubmit", canSubmit);
		    			}else{
		    			modelnView.addObject("assignmentFile",new AssignmentFileBean());
		    			modelnView.addObject("submitted", submitted);
		    			modelnView.addObject("canSubmit", canSubmit);
		    			}
		    			setError(request, "Project Submission is not live currently");
		    			return modelnView;
		    		}
		        }else{
		        	if(student.getConsumerProgramStructureId() != null || !StringUtils.isBlank(student.getConsumerProgramStructureId())) {
		        		assignmentFile.setConsumerProgramStructureId(student.getConsumerProgramStructureId());
					} else{
						setError(request, "Program Structure ID Missing");
		    			return modelnView;
					} 
		        	assignmentFile = projectSubmissionDAO.findProjectGuidelines(assignmentFile);
		        }
				
				PassFailDAO passFailDAO = (PassFailDAO)act.getBean("passFailDAO");
				ArrayList<String> passedSubjects = passFailDAO.getPassSubjectsNamesForAStudent(sapId);
//				boolean synopsis_submitted= false; -Prashant check synopis submission is unused
				if(resultsAwaited){
					setError(request, "Your previous project Submission Results Awaited.");
					request.setAttribute("canSubmit", "false");
					request.setAttribute("paymentApplicable", "false");
				}else if(passedSubjects != null && passedSubjects.contains("Project")){ //Check if student already cleared project
					setError(request, "You have already cleared Project.");
					request.setAttribute("canSubmit", "false");
					request.setAttribute("paymentApplicable", "false");
				}else if(passedSubjects != null && passedSubjects.contains("Module 4 - Project")){ //Check if student already cleared project
					setError(request, "You have already cleared Module 4 - Project.");
					request.setAttribute("canSubmit", "false");
					request.setAttribute("paymentApplicable", "false");
				}else{
						String year = assignmentFile.getYear();
						String month = assignmentFile.getMonth();
						if(student.getExamMode().equalsIgnoreCase("Online")){
							if("Diageo".equalsIgnoreCase(studentDetails.getConsumerType())){
								isApplicableToSubmit = eDao.checkIfStudentApplicableForSubmission(sapId,year,month,"2"); //Only sudents who completed min 5 months in sem 2 allowed to submit

							} else if(isPdWmStudent) {
//								LevelBasedProjectDAO levelBasedProjectDAO = (LevelBasedProjectDAO)act.getBean("levelBasedProjectDAO");
								isApplicableToSubmit = eDao.checkIfStudentApplicableForSubmission(sapId,year,month,"2"); //Sem 2+ students
//								UploadProjectSynopsisBean inputBean= new UploadProjectSynopsisBean();
//								inputBean.setYear(year);
//								inputBean.setMonth(month);
//								inputBean.setSapid(sapId);
//								synopsis_submitted = levelBasedProjectDAO.checkSynopsisSubmissionCount(inputBean,SYNOPSIS_STATUS); -Prashant check synopis submission is unused
							
							}else  if(isUGStudent){
								isApplicableToSubmit = eDao.checkIfStudentApplicableForSubmission(sapId,year,month,"5"); //Only sudents who completed min 5 months in sem 5 allowed to submit
							}
							else {
								isApplicableToSubmit = eDao.checkIfStudentApplicableForSubmission(sapId,year,month,"4"); //Only sudents who completed min 5 months in sem 4 allowed to submit
							}
						}
						if(student.getExamMode().equalsIgnoreCase("Offline")){
							isApplicableToSubmit = true; //Only for offline students as their registration table entry is blank.
						}
						if(timeExtendedStudentIdSubjectList.contains(key)){
							isApplicableToSubmit = true; //Only for extended list students as their registration is allowed by admin after date
						}
						
					if(isApplicableToSubmit){
						ArrayList<String>  examBookedSubjets = eDao.getProjectBookingforCurrentLiveExam(sapId); //Check if project payment done for current live project submission cycle.
						if(examBookedSubjets.contains("Project") || freeSubjects.contains("Project")){
							canSubmit= true;
							request.setAttribute("canSubmit","true");
							request.setAttribute("paymentApplicable", "false");
//						}else if(examBookedSubjets.contains("Module 4 - Project") || synopsis_submitted || freeSubjects.contains("Module 4 - Project")){ -Prashant check synopis submission is unused
						}else if(assignmentFile.getSubject().equalsIgnoreCase("Module 4 - Project") && !levelBasedProjectService.isResitCycleMonth(month)){
							paymentApplicable= true;
							request.setAttribute("canSubmit", "true");
							request.setAttribute("paymentApplicable", "false");
						}else if(examBookedSubjets.contains("Module 4 - Project") && levelBasedProjectService.isResitCycleMonth(month)){
							canSubmit= true;
							request.setAttribute("canSubmit","true");
							request.setAttribute("paymentApplicable", "false");
						}else{
							paymentApplicable= true;
							request.setAttribute("canSubmit", "false");
							request.setAttribute("paymentApplicable", "true");
							//setError(request, "You can not submit Project yet, as you have not registered for Project Submission for current Exam cycle");
						}
					}else{
						request.setAttribute("canSubmit","false");
						canSubmit=false;
						setError(request, "Project Submission is not applicable currently");
						//return modelnView;
					}
				}
			}
			
			String status = "Not Submitted";
			String attempts = "0";
			AssignmentFileBean studentSubmissionStatus = projectSubmissionDAO.getProjectSubmissionStatus(assignmentFile.getSubject(), sapId);
			if(studentSubmissionStatus != null){
				status = studentSubmissionStatus.getStatus();
				attempts = studentSubmissionStatus.getAttempts();
				submitted=true;
				assignmentFile.setPreviewPath(studentSubmissionStatus.getPreviewPath());
				assignmentFile.setStudentFilePath(studentSubmissionStatus.getStudentFilePath());
			}
			assignmentFile.setStatus(status);
			assignmentFile.setAttempts(attempts);

			String startDate = assignmentFile.getStartDate();
			startDate = startDate.replaceAll("T", " ");
			assignmentFile.setStartDate(startDate.substring(0,19));
			
			String endDate = assignmentFile.getEndDate();
			endDate = endDate.replaceAll("T", " ");
			
			assignmentFile.setEndDate(endDate.substring(0,19));
			*/	
		// Normal single live cycle code -----END-----
		
		int maxAttempts = Integer.parseInt(ASSIGNMENT_SUBMISSIONS_ATTEMPTS);
		
//		request.getSession().setAttribute("subject",subject);
		modelnView.addObject("timeExtendedStudentIdSubjectList", timeExtendedStudentIdSubjectList);
		request.getSession().setAttribute("timeExtendedStudentIdSubjectList",timeExtendedStudentIdSubjectList);
		
        modelnView.addObject("key",key);
		modelnView.addObject("maxAttempts",maxAttempts);
		modelnView.addObject("assignmentFile",assignmentFile);
		modelnView.addObject("yearList", ACAD_YEAR_LIST);
		modelnView.addObject("subjectList", getSubjectList());
//		modelnView.addObject("subject", subject);
		modelnView.addObject("canSubmit",canSubmit);
		modelnView.addObject("submitted",submitted);
		modelnView.addObject("paymentApplicable",paymentApplicable);
	  }catch (Exception e) {
		  projectSubmissionLogger.error("Exception error pg visit /viewProject Sapid - {} Error - {}",sapId,e);
		  //e.printStackTrace();
		// TODO: handle exception
	}
		modelnView.addObject("subject", assignmentFile.getSubject());
		request.getSession().setAttribute("subject",assignmentFile.getSubject());
		return modelnView;
	}
	//Project payment end

	@RequestMapping(value = "/student/submitProject",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView submitProject(HttpServletRequest request, HttpServletResponse response, @ModelAttribute AssignmentFileBean assignmentFile){

		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}

		ModelAndView modelnView = new ModelAndView("project/submitProject");
		String sapId = (String)request.getSession().getAttribute("userId");
		assignmentFile.setSapId(sapId);
		projectSubmissionLogger.info("Pg visit /submitProject Sapid - {}",sapId);
		try {
			
		String endDate = assignmentFile.getEndDate();
		
		//Send Email to student
		ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
		StudentExamBean student = eDao.getSingleStudentsData(sapId);

		int maxAttempts = Integer.parseInt(ASSIGNMENT_SUBMISSIONS_ATTEMPTS);
		int attempts = 0;
		
		/*StudentExamBean registration = projectSubmissionDAO.getRecentRegisterationByStudent(sapId);
		logger.info("registration>>"+registration);
		logger.info(registration.getYear()+"--"+registration.getMonth());
		boolean freshStudent = false;
		if(registration.getYear().equalsIgnoreCase("2022") && registration.getMonth().equalsIgnoreCase("Jul")) {
			freshStudent=true;
		}
		if(freshStudent) {
			assignmentFile.setYear("2022");
			assignmentFile.setMonth("Dec");
		}else {
			assignmentFile.setYear("2022");
			assignmentFile.setMonth("Sep");
		}*/
		AssignmentFileBean submissionStatus = projectSubmissionDAO.getProjectSubmissionStatusForCycle(assignmentFile.getSubject(), sapId,assignmentFile.getYear(),assignmentFile.getMonth());

		if(submissionStatus != null){
			attempts = Integer.parseInt(submissionStatus.getAttempts());
			assignmentFile.setPreviewPath(submissionStatus.getPreviewPath());
			assignmentFile.setStudentFilePath(submissionStatus.getStudentFilePath());
			assignmentFile.setEndDate(assignmentFile.getEndDate()); // by - Prashant Set end date from api paramtere for email purpose due to commented latest dao call for /submit api.
		}
		assignmentFile.setAttempts(attempts+"");

		modelnView.addObject("maxAttempts",maxAttempts);
		modelnView.addObject("yearList", ACAD_YEAR_LIST);
		modelnView.addObject("subjectList", getSubjectList());
		//modelnView.addObject("assignmentFile",assignmentFile);

		if(attempts >= maxAttempts){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Maximum Attempts reached to submit Project");
			modelnView.addObject("assignmentFile",assignmentFile);
			return modelnView;
		}


		//Check if student is extended or not
		ArrayList<String> timeExtendedStudentIdSubjectList = (ArrayList<String>) request.getSession().getAttribute("timeExtendedStudentIdSubjectList");
		if (!timeExtendedStudentIdSubjectList.contains(sapId + assignmentFile.getSubject())) {
			//Check if end date is expired or not
			boolean isEndDateExpired = false;
			isEndDateExpired = fileUploadHelper.checkIsEndDateExpired(endDate);
			if (isEndDateExpired) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Unable to submit "+ assignmentFile.getSubject() + " as deadline is over.");
				modelnView.addObject("assignmentFile", assignmentFile);
				projectSubmissionLogger.error("Submission Date expired : Sapid = {} - Month Year = {}/{} - Subject = {}",sapId,assignmentFile.getMonth(),assignmentFile.getYear(),assignmentFile.getSubject());
				return modelnView;
			}
		}
		
		String fileName = assignmentFile.getFileData().getOriginalFilename();  
		if(fileName == null || "".equals(fileName.trim()) ){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in file Upload: No File Selected");
			enableSubmitButton(modelnView,assignmentFile);// on submit api page student should get submit button
			return modelnView;
		}

		String errorMessage = uploadProjectSubmissionFile(assignmentFile, assignmentFile.getYear(), assignmentFile.getMonth(), sapId);

		if(errorMessage == null){
			
			maxAttempts = Integer.parseInt(ASSIGNMENT_SUBMISSIONS_ATTEMPTS);
			attempts = Integer.parseInt(assignmentFile.getAttempts()) + 1;
			assignmentFile.setStatus("Submitted");
			String userId = (String)request.getSession().getAttribute("userId");
			assignmentFile.setCreatedBy(userId);
			assignmentFile.setLastModifiedBy(userId);
			try {
				projectSubmissionDAO.saveProjectSubmissionDetails(assignmentFile, maxAttempts);
				
				projectSubmissionLogger.info("Successfully executed saveProjectSubmissionDetails: Sapid = {} - Month Year = {}/{} - Subject = {} - Attempt = {} - StudentFilePath = {}", 
												sapId,assignmentFile.getMonth(),assignmentFile.getYear(),assignmentFile.getSubject(),attempts,assignmentFile.getStudentFilePath());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				projectSubmissionLogger.error("Error saveProjectSubmissionDetails : Sapid = {} - Month Year = {}/{} - Subject = {} - Attempt = {} - ErrorMessage = {}",
						sapId,assignmentFile.getMonth(),assignmentFile.getYear(),assignmentFile.getSubject(),attempts,e);
			}
			
		}else{
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in file Upload: "+errorMessage);
			modelnView.addObject("assignmentFile",assignmentFile);
			projectSubmissionLogger.error("Error uploading File : Sapid = {} - Month Year = {}/{} - Subject = {} - Attempt = {} - ErrorMessage = {}",
					sapId,assignmentFile.getMonth(),assignmentFile.getYear(),assignmentFile.getSubject(),attempts,errorMessage);
			enableSubmitButton(modelnView,assignmentFile);// on submit api page student should get submit button
			return modelnView;
		}
		
		  if(student.getConsumerProgramStructureId() != null || !StringUtils.isBlank(student.getConsumerProgramStructureId())) {
      		assignmentFile.setConsumerProgramStructureId(student.getConsumerProgramStructureId());
			} else{
				setError(request, "Program Structure ID Missing");
  			return modelnView;
			}  

		//assignmentFile = projectSubmissionDAO.getProjectDetailsForStudent(assignmentFile);
		request.setAttribute("success","true");
		//int usedAttempts = Integer.parseInt(assignmentFile.getAttempts());
		int usedAttempts = attempts;
		String successMessage = "Files Uploaded successfully. Please cross verify the Preview of the uploaded Project file. "
				+ "Take Printscreen for your records. Attempt "+usedAttempts +" Exhausted. <br>";

		if(usedAttempts < maxAttempts){
			successMessage = successMessage + "Incase of incorrect/incomplete file: you can use the remaining attempts and be cautious while resubmitting the Project file.";
		}else{
			successMessage = successMessage + "You have exhausted all Project submission attempts";
		}

		//request.setAttribute("successMessage",successMessage);
		request.getSession().setAttribute("successMsg", successMessage);
		//modelnView.addObject("assignmentFile",assignmentFile);

		
		
		MailSender mailSender = (MailSender)act.getBean("mailer");
		
        assignmentFile.setEndDate(endDate);
        assignmentFile.setAttempts(String.valueOf(attempts));
		mailSender.sendProjectReceivedEmail(student, assignmentFile);

		response.sendRedirect(SERVER_PATH+"exam/student/viewProject?subject="+ assignmentFile.getSubject());

		}catch (Exception e) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in file Upload: "+e.getMessage());
			projectSubmissionLogger.error("Exception Error uploading File : Sapid = {} - Month Year = {}/{} - Subject = {} - ErrorMessage = {}",
					sapId,assignmentFile.getMonth(),assignmentFile.getYear(),assignmentFile.getSubject(),e);
			//e.printStackTrace();
			return modelnView;
		}
		//return modelnView; to avoid twice assignment submission-by Prashant
		return null;
	}

	private void enableSubmitButton(ModelAndView modelnView, AssignmentFileBean assignmentFile) {
		String key = assignmentFile.getSapId()+assignmentFile.getSubject();
		modelnView.addObject("assignmentFile",assignmentFile);
		modelnView.addObject("key",key);
		modelnView.addObject("canSubmit",true);
		modelnView.addObject("submitted",false);
		modelnView.addObject("paymentApplicable",false);
		// TODO Auto-generated method stub
		
	}

	private String uploadProjectSubmissionFile(AssignmentFileBean bean, String year, String month, String sapId) {

		String errorMessage = null;
		InputStream inputStream = null;   
		OutputStream outputStream = null;   

		CommonsMultipartFile file = bean.getFileData(); 
		String fileName = file.getOriginalFilename();   

		long fileSizeInBytes = bean.getFileData().getSize();
		if(fileSizeInBytes > MAX_FILE_SIZE_LIMIT) {
			errorMessage = "File size exceeds 10MB. Please upload a file with size less than 10MB";
			return errorMessage;
		}
		
		if(!FileUploadHelper.checkPdfFileContentType(file.getContentType())) {		//File Header Validation added as per card: 10722
			projectSubmissionLogger.error("Error while uploading Project Submission file with Content-Type: {} for Student: {}", file.getContentType(), bean.getSapId());
			return "File type not supported. Please upload a PDF file!";
		}

		//Replace special characters in file name
		String subject = bean.getSubject();
		subject = subject.replaceAll("'", "_");
		subject = subject.replaceAll(",", "_");
		subject = subject.replaceAll("&", "and");
		subject = subject.replaceAll(" ", "_");


		if(!(fileName.toUpperCase().endsWith(".PDF") ) ){
			errorMessage = "File type not supported. Please upload .pdf file.";
			return errorMessage;
		}
		String folderPath =  "Submissions/"+month + year + "/" + subject + "/";
		//Add Random number to avoid student guessing names of other student's assignment files
		fileName = sapId + "_" + subject + "_" + RandomStringUtils.randomAlphanumeric(12) + ".pdf";
		 
		bean.setStudentFilePath(month + year + "/" + subject + "/"+fileName);
		bean.setPreviewPath(month + year + "/" + subject + "/"+fileName);
		
		/*
		try {  
			//PDF stores first 4 letters as %PDF, which can be used to check if a file is actually a pdf file and not just going by extension
			InputStream tempInputStream = file.getInputStream();  ;
			byte[] initialbytes = new byte[4];   
			tempInputStream.read(initialbytes);

			tempInputStream.close();
			String fileType = new String(initialbytes);

			if(!"%PDF".equalsIgnoreCase(fileType)){
				errorMessage = "File is not a PDF file. Please upload .pdf file.";
				return errorMessage;
			}


			inputStream = file.getInputStream();   
			String filePath = SUBMITTED_ASSIGNMENT_FILES_PATH + month + year + "/" + bean.getSubject() + "/" + fileName;
			String previewPath = month + year + "/" + subject + "/" + fileName;
			//Check if Folder exists which is one folder per Exam (Jun2015, Dec2015 etc.) 
			File folderPath = new File(SUBMITTED_ASSIGNMENT_FILES_PATH  + month + year + "/" + bean.getSubject());
			if (!folderPath.exists()) {
				boolean created = folderPath.mkdirs();
			}   

			File newFile = new File(filePath);   

			outputStream = new FileOutputStream(newFile);   
			int read = 0;   
			byte[] bytes = new byte[1024];   

			while ((read = inputStream.read(bytes)) != -1) {   
				outputStream.write(bytes, 0, read);   
			}
			bean.setStudentFilePath(filePath);
			bean.setPreviewPath(previewPath);
			outputStream.close();
			inputStream.close();
		} catch (IOException e) {   
			errorMessage = "Error in uploading file for "+bean.getSubject() + " : "+ e.getMessage();
			   
		}  */ 
		projectSubmissionLogger.info("Uploading project file to s3 for sapId : "+sapId+" with fileName - "+fileName +" for year/momth : "+year+"/"+month);
		HashMap<String,String> s3_response = amazonS3Helper.uploadFiles(file,folderPath,"assignment-files",folderPath+fileName);
		if(!s3_response.get("status").equalsIgnoreCase("success")) {
		errorMessage =  "Error in uploading file "+s3_response.get("fileUrl");
		//logger.error(errorMessage);
		return errorMessage;
		}
		boolean isFileCorupted = fileUploadHelper.isFileCorrupted(s3_response.get("url"));
		if(isFileCorupted) {
			projectSubmissionLogger.error("File is corrupted or not readable which is uploaded on S3 with URL/filepath : "+s3_response.get("url"));
			errorMessage="Unable to read PDF, the uploaded file must be corrupt or blank. Please verify and submit an updated file.";
			return errorMessage;
		}
		boolean isQPFileUploaded = fileUploadHelper.isQPFileUploaded(s3_response.get("url"),ASSIGNMENT_FILES_PATH+ bean.getQuestionFilePreviewPath(),subject,SERVER_PATH);
		if(isQPFileUploaded) {
			//logger.error("Student uploaded file is the same as a question paper file by sapid: {} month : {} year: {} subject: {} on {} attempt  S3 URL/filepath: {}",sapId,month,year, bean.getSubject(),Integer.parseInt(bean.getAttempts()) + 1, s3_response.get("url"));
			errorMessage = "Invalid Submission, the uploaded file is the same as a question paper file. Please upload a valid file with answer/s.";
			return errorMessage;
		}
		return errorMessage;
	}
	
	@RequestMapping(value = "/admin/searchProjectSubmissionForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String searchProjectSubmissionForm(HttpServletRequest request, HttpServletResponse response, Model m) {

		AssignmentFileBean searchBean = new AssignmentFileBean();
		m.addAttribute("searchBean",searchBean);
		m.addAttribute("yearList", ACAD_YEAR_LIST);
		m.addAttribute("subjectList", getSubjectList());

		return "project/searchProjectSubmission";
	}
	
	@RequestMapping(value = "/admin/searchProjectSubmission", method = {RequestMethod.POST})
	public ModelAndView searchProjectSubmission(HttpServletRequest request, HttpServletResponse response, @ModelAttribute AssignmentFileBean searchBean){
		ModelAndView modelnView = new ModelAndView("project/searchProjectSubmission");
		request.getSession().setAttribute("searchBean", searchBean);

		Page<AssignmentFileBean> page = projectSubmissionDAO.getProjectSubmissionPage(1, pageSize, searchBean);
		List<AssignmentFileBean> assignmentFilesList = page.getPageItems();

		modelnView.addObject("assignmentFilesList", assignmentFilesList);
		modelnView.addObject("page", page);

		modelnView.addObject("searchBean", searchBean);
		modelnView.addObject("rowCount", page.getRowCount());

		modelnView.addObject("yearList", ACAD_YEAR_LIST);
		modelnView.addObject("subjectList", getSubjectList());
		if(assignmentFilesList == null || assignmentFilesList.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No records found.");
		}
		return modelnView;
	}

	@RequestMapping(value = "/admin/searchProjectSubmissionPage", method = {RequestMethod.GET})
	public ModelAndView searchProjectSubmissionPage(HttpServletRequest request, HttpServletResponse response){
		ModelAndView modelnView = new ModelAndView("project/searchProjectSubmission");
		AssignmentFileBean searchBean = (AssignmentFileBean)request.getSession().getAttribute("searchBean");
		int pageNo = Integer.parseInt(request.getParameter("pageNo"));
		Page<AssignmentFileBean> page = projectSubmissionDAO.getProjectSubmissionPage(pageNo, pageSize, searchBean);
		List<AssignmentFileBean> assignmentFilesList = page.getPageItems();

		modelnView.addObject("assignmentFilesList", assignmentFilesList);
		modelnView.addObject("page", page);

		modelnView.addObject("searchBean", searchBean);
		modelnView.addObject("rowCount", page.getRowCount());

		modelnView.addObject("yearList", ACAD_YEAR_LIST);
		modelnView.addObject("subjectList", getSubjectList());
		if(assignmentFilesList == null || assignmentFilesList.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No records found.");
		}
		return modelnView;
	}
	
	@RequestMapping(value = "/admin/downloadProjectSubmittedExcel", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadProjectSubmittedExcel(HttpServletRequest request, HttpServletResponse response) {

		AssignmentFileBean searchBean = (AssignmentFileBean)request.getSession().getAttribute("searchBean");

		Page<AssignmentFileBean> page = projectSubmissionDAO.getProjectSubmissionPage(1, Integer.MAX_VALUE, searchBean);
		List<AssignmentFileBean> assignmentSubmittedList = page.getPageItems();
		ModelAndView mv =new ModelAndView("assignmentSubmittedExcelView");
		mv.addObject("assignmentSubmittedList",assignmentSubmittedList);
		mv.addObject("SUBMITTED_ASSIGNMENT_FILES_PATH",SUBMITTED_ASSIGNMENT_FILES_PATH);
		return mv; 
	}
	
	
	@RequestMapping(value = "/admin/searchProjectToEvaluateForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String searchProjectToEvaluateForm(HttpServletRequest request, HttpServletResponse response, Model m) {
		if(!checkSession(request, response)){
			return "login";
		}
		
		AssignmentFileBean searchBean = new AssignmentFileBean();
		m.addAttribute("searchBean",searchBean);
		m.addAttribute("yearList", ACAD_YEAR_LIST);
		m.addAttribute("subjectList", getSubjectList());

		String facultyId = (String)request.getSession().getAttribute("userId");
		Person user = (Person)request.getSession().getAttribute("user");
		String roles = user != null ? user.getRoles() : "";
		
		
		/*//**********TEMP***********
		roles = "Faculty";
		user = new Person();
		user.setRoles(roles);
		request.getSession().setAttribute("user", user);
		//**********TEMP***********
*/
		if(roles.indexOf("Faculty") != -1){
			//When its faculty show only their assignment, otherwise show all assignments
			searchBean.setFacultyId(facultyId);
		}
		//searchBean.setEvaluated("N");
		request.getSession().setAttribute("searchBean", searchBean);
/*
		Page<AssignmentFileBean> page = projectSubmissionDAO.getProjectsForFacultyPage(1, pageSize, searchBean);
		
		List<AssignmentFileBean> projectFilesList = page.getPageItems();*/
		
		/*page = projectSubmissionDAO.getProjectsForFacultyRevaluationPage(1, pageSize, searchBean);//Find project for reval
		
		
		projectFilesList.addAll(page.getPageItems());*/
	/*	
		m.addAttribute("projectFilesList", projectFilesList);
		m.addAttribute("pendingEvaluations", page.getRowCount());
		m.addAttribute("rowCount", page.getRowCount());
		m.addAttribute("page", page);*/

		return "project/searchProjectToEvaluate";
	}
	@RequestMapping(value="/admin/downloadProjectEvaluatedExcel",method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView downloadProjectEvaluationReport(HttpServletRequest request, HttpServletResponse response){
		
		AssignmentFileBean searchFielBean = (AssignmentFileBean)request.getSession().getAttribute("searchBean");
		Page<AssignmentFileBean> page = projectSubmissionDAO.getProjectsForFacultyPage(1, Integer.MAX_VALUE, searchFielBean);
		List<AssignmentFileBean> projectFilesList = page.getPageItems();
		/*
		page = projectSubmissionDAO.getProjectsForFacultyRevaluationPage(1, Integer.MAX_VALUE, searchFielBean);
		projectFilesList.addAll(page.getPageItems());*/
		return new ModelAndView("projectEvaluationReportExcelView","projectFileList",projectFilesList);
	}
	@RequestMapping(value = "/admin/searchProjectToEvaluate", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView searchProjectToEvaluate(HttpServletRequest request, HttpServletResponse response, @ModelAttribute AssignmentFileBean searchBean){
		
		if(!checkSession(request, response)){
			return new ModelAndView("login");
		}

		
		ModelAndView modelnView = new ModelAndView("project/searchProjectToEvaluate");
		request.getSession().setAttribute("searchBean", searchBean);

		String facultyId = (String)request.getSession().getAttribute("userId");
		//String facultyId = "NMSCEMU200303521";



		Person user = (Person)request.getSession().getAttribute("user");
		String roles = user != null ? user.getRoles() : "";
		//String roles = "Faculty";
		searchBean.setLastModifiedBy(user.getUserId());
		if(roles.indexOf("Faculty") != -1){
			//When its faculty show only their assignment, otherwise show all assignments
			searchBean.setFacultyId(facultyId);
			searchBean.setLastModifiedBy(facultyId);
		}

		
		Page<AssignmentFileBean> page = projectSubmissionDAO.getProjectsForFacultyPage(1, pageSize, searchBean);
		List<AssignmentFileBean> projectFilesList = page.getPageItems();
		/*Page<AssignmentFileBean> page2 = projectSubmissionDAO.getProjectsForFacultyRevaluationPage(1, pageSize, searchBean);//Find project for reval
		projectFilesList.addAll(page2.getPageItems());*/

		modelnView.addObject("projectFilesList", projectFilesList);
		modelnView.addObject("page", page);

		modelnView.addObject("searchBean", searchBean);
		modelnView.addObject("rowCount", page.getRowCount());

		modelnView.addObject("yearList", ACAD_YEAR_LIST);
		modelnView.addObject("subjectList", getSubjectList());

		if(projectFilesList == null || projectFilesList.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No records found.");
		}

		return modelnView;
	}
	
	@RequestMapping(value = "/admin/searchProjectToEvaluatePage", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView searchProjectToEvaluatePage(HttpServletRequest request, HttpServletResponse response){
		if(!checkSession(request, response)){
			return new ModelAndView("login");
		}

		ModelAndView modelnView = new ModelAndView("project/searchProjectToEvaluate");
		AssignmentFileBean searchBean = (AssignmentFileBean)request.getSession().getAttribute("searchBean");

		String pageNoStr = request.getParameter("pageNo");

		int pageNo = 1; 
		if(pageNoStr != null){
			pageNo = Integer.parseInt(pageNoStr);
		}

		String facultyId = (String)request.getSession().getAttribute("userId");
		Person user = (Person)request.getSession().getAttribute("user");
		String roles = user != null ? user.getRoles() : "";
		//String roles = "Faculty";

		if(roles.indexOf("Faculty") != -1){
			//When its faculty show only their assignment, otherwise show all assignments
			searchBean.setFacultyId(facultyId);
		}


		//String facultyId = "NMSCEMU200303521";
		//searchBean.setFacultyId(facultyId);
		
		Page<AssignmentFileBean> page = projectSubmissionDAO.getProjectsForFacultyPage(pageNo, pageSize, searchBean);
		List<AssignmentFileBean> projectFilesList = page.getPageItems();
		/*Page<AssignmentFileBean> page2 = projectSubmissionDAO.getProjectsForFacultyRevaluationPage(pageNo, pageSize, searchBean);//Find project for reval
		projectFilesList.addAll(page2.getPageItems());*/

		modelnView.addObject("projectFilesList", projectFilesList);
		modelnView.addObject("page", page);
		
		modelnView.addObject("searchBean", searchBean);
		modelnView.addObject("rowCount", page.getRowCount());

		modelnView.addObject("yearList", ACAD_YEAR_LIST);
		modelnView.addObject("subjectList", getSubjectList());
		if(projectFilesList == null || projectFilesList.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No records found.");
		}
		return modelnView;
	}

	
	@RequestMapping(value = "/admin/evaluateProjectForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView evaluateProjectForm(HttpServletRequest request, HttpServletResponse response, @ModelAttribute AssignmentFileBean searchBean) {
		if(!checkSession(request, response)){
			return new ModelAndView("login");
		}

		HttpSession session = request.getSession();
		session.setMaxInactiveInterval(60*60);//Set session timeout to 60 minutes only for faculties when they evaluate Project
		
	
		String facultyId = (String)request.getSession().getAttribute("userId");
		//String facultyId = "NMSCEMU200303521";
		searchBean.setFacultyId(facultyId);
		AssignmentFileBean assignmentFile = projectSubmissionDAO.getSingleProjectForFaculty(searchBean);
		
		ModelAndView modelnView = new ModelAndView("project/evaluateProject");
	
		
		if(facultyId.equals(assignmentFile.getFacultyIdRevaluation())){
			modelnView = new ModelAndView("project/revaluateProject");
		}
		
		// PD - WM 50 marks evaluation now should be same as PG evaluation of 100 marks
//		if("PD - WM".equalsIgnoreCase(assignmentFile.getProgram())) {
//			modelnView = new ModelAndView("project/evaluateProjectPDWM");
//		}

		modelnView.addObject("searchBean",searchBean);
		modelnView.addObject("yearList", ACAD_YEAR_LIST);
		modelnView.addObject("subjectList", getSubjectList());
		modelnView.addObject("assignmentFile", assignmentFile);
		return modelnView; 
	}


	@RequestMapping(value = "/admin/evaluateProject", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView evaluateProject(HttpServletRequest request, HttpServletResponse response, @ModelAttribute AssignmentFileBean searchBean) {
		if(!checkSession(request, response)){
			return new ModelAndView("login");
		}

		String facultyId = (String)request.getSession().getAttribute("userId");
		//String facultyId = "NMSCEMU200303521";
		searchBean.setFacultyId(facultyId);
		ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
		StudentExamBean studentBean = eDao.getSingleStudentsData(searchBean.getSapId());
		
		ModelAndView modelnView = new ModelAndView("project/evaluateProject");
		// PD - WM 50 marks evaluation now should be same as PG evaluation of 100 marks
//		if("PD - WM".equalsIgnoreCase(studentBean.getProgram())) {
//			modelnView = new ModelAndView("project/evaluateProjectPDWM");
//		} 
		modelnView.addObject("searchBean",searchBean);
		modelnView.addObject("yearList", ACAD_YEAR_LIST);
		modelnView.addObject("subjectList", getSubjectList());
		AssignmentFileBean assignmentFile = projectSubmissionDAO.getSingleProjectForFaculty(searchBean);
		if(searchBean.getScore() == null || "".equals(searchBean.getScore().trim())){
			setError(request, "Score cannot be empty. Please enter a score.");
			
			modelnView.addObject("assignmentFile", assignmentFile);
			return modelnView;
		}
		try {

			projectSubmissionDAO.evaluateProject(searchBean);
			StudentExamBean student = projectSubmissionDAO.getSingleStudentsData(searchBean.getSapId());


			ArrayList<ProgramSubjectMappingExamBean> programSubjectMappingList = getProgramSubjectMappingList();

			for (int i = 0; i < programSubjectMappingList.size(); i++) {
				ProgramSubjectMappingExamBean bean = programSubjectMappingList.get(i);

				if(bean.getPrgmStructApplicable().equals(student.getPrgmStructApplicable()) && bean.getProgram().equals(student.getProgram())
						&& bean.getSubject().equals(searchBean.getSubject())){
					//Setting the semester for Subject 
					student.setSem(bean.getSem());
					break;
				}
			}

			StudentMarksBean marksBean = new StudentMarksBean();
			marksBean.setSapid(student.getSapid());
			marksBean.setSem(student.getSem());
			marksBean.setAssignmentscore("");
			marksBean.setWritenscore(searchBean.getScore());
			marksBean.setTotal(searchBean.getScore());
			marksBean.setStudentname(student.getFirstName() + " " + student.getLastName());
			marksBean.setSubject(searchBean.getSubject());
			marksBean.setProgram(student.getProgram());
			marksBean.setSyllabusYear("");
			marksBean.setGrno("Not Available");
			marksBean.setYear(searchBean.getYear());
			marksBean.setMonth(searchBean.getMonth());
			marksBean.setCreatedBy(facultyId);
			marksBean.setLastModifiedBy(facultyId);

			projectSubmissionDAO.upsertProjectMarks(marksBean);

			assignmentFile = projectSubmissionDAO.getSingleProjectForFaculty(searchBean);
			
			modelnView.addObject("assignmentFile", assignmentFile);

			setSuccess(request, "Project score saved successfully");
		} catch (Exception e) {
			facultyEvaluationLogger.error("Project Evaluation Error :- FacultyId-  {} Subject- {} Month- {}  Year- {} Sapid- {} Error- {}", searchBean.getFacultyId(), searchBean.getSubject(), searchBean.getMonth(),searchBean.getYear(),searchBean.getSapId(),e);
			facultyEvaluationLogger.error("Project Evaluation Remark 1 :- FacultyId-  {} Subject- {} Month- {}  Year- {} Sapid- {} RemarkQ1- {}", searchBean.getFacultyId(), searchBean.getSubject(), searchBean.getMonth(),searchBean.getYear(),searchBean.getSapId(),searchBean.getQ1Remarks());
			facultyEvaluationLogger.error("Project Evaluation Remark 2 :- FacultyId-  {} Subject- {} Month- {}  Year- {} Sapid- {} RemarkQ2- {}", searchBean.getFacultyId(), searchBean.getSubject(), searchBean.getMonth(),searchBean.getYear(),searchBean.getSapId(),searchBean.getQ2Remarks());
			facultyEvaluationLogger.error("Project Evaluation Remark 3 :- FacultyId-  {} Subject- {} Month- {}  Year- {} Sapid- {} RemarkQ3- {}", searchBean.getFacultyId(), searchBean.getSubject(), searchBean.getMonth(),searchBean.getYear(),searchBean.getSapId(),searchBean.getQ3Remarks());
			facultyEvaluationLogger.error("Project Evaluation Remark 4 :- FacultyId-  {} Subject- {} Month- {}  Year- {} Sapid- {} RemarkQ4- {}", searchBean.getFacultyId(), searchBean.getSubject(), searchBean.getMonth(),searchBean.getYear(),searchBean.getSapId(),searchBean.getQ4Remarks());
			facultyEvaluationLogger.error("Project Evaluation Remark 5 :- FacultyId-  {} Subject- {} Month- {}  Year- {} Sapid- {} RemarkQ5- {}", searchBean.getFacultyId(), searchBean.getSubject(), searchBean.getMonth(),searchBean.getYear(),searchBean.getSapId(),searchBean.getQ5Remarks());
			facultyEvaluationLogger.error("Project Evaluation Remark 6 :- FacultyId-  {} Subject- {} Month- {}  Year- {} Sapid- {} RemarkQ6- {}", searchBean.getFacultyId(), searchBean.getSubject(), searchBean.getMonth(),searchBean.getYear(),searchBean.getSapId(),searchBean.getQ6Remarks());
			facultyEvaluationLogger.error("Project Evaluation Remark 7 :- FacultyId-  {} Subject- {} Month- {}  Year- {} Sapid- {} RemarkQ7- {}", searchBean.getFacultyId(), searchBean.getSubject(), searchBean.getMonth(),searchBean.getYear(),searchBean.getSapId(),searchBean.getQ7Remarks());
			facultyEvaluationLogger.error("Project Evaluation Remark 8 :- FacultyId-  {} Subject- {} Month- {}  Year- {} Sapid- {} RemarkQ8- {}", searchBean.getFacultyId(), searchBean.getSubject(), searchBean.getMonth(),searchBean.getYear(),searchBean.getSapId(),searchBean.getQ8Remarks());
			facultyEvaluationLogger.error("Project Evaluation Remark 9 :- FacultyId-  {} Subject- {} Month- {}  Year- {} Sapid- {} RemarkQ9- {}", searchBean.getFacultyId(), searchBean.getSubject(), searchBean.getMonth(),searchBean.getYear(),searchBean.getSapId(),searchBean.getQ9Remarks());
			facultyEvaluationLogger.error("Project Evaluation Remark 10 :- FacultyId-  {} Subject- {} Month- {}  Year- {} Sapid- {} RemarkQ10- {}", searchBean.getFacultyId(), searchBean.getSubject(), searchBean.getMonth(),searchBean.getYear(),searchBean.getSapId(),searchBean.getQ10Remarks());
			setError(request, "Error in saving Project score");
			modelnView.addObject("assignmentFile", assignmentFile);
			
		}
		return modelnView; 
	}
	
	@RequestMapping(value = "/admin/allocateProjectEvaluationForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView allocateProjectEvaluationForm(HttpServletRequest request, HttpServletResponse response, Model m) {
		if(!checkSession(request, response)){
			return new ModelAndView("login");
		}
		
		AssignmentFileBean searchBean = new AssignmentFileBean();
		m.addAttribute("searchBean",searchBean);
		m.addAttribute("yearList", ACAD_YEAR_LIST);
		m.addAttribute("subjectList", getSubjectList());

		FacultyDAO dao = (FacultyDAO)act.getBean("facultyDAO");
		ArrayList<FacultyExamBean> facultyList = dao.getFaculties();
		m.addAttribute("facultyList", facultyList);


		return new ModelAndView("project/allocateProjectEvaluation");
	}
	
	@RequestMapping(value="/admin/getNoOfProjects", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView getNoOfProjects(HttpServletRequest request, HttpServletResponse response , @ModelAttribute AssignmentFileBean searchBean) throws JsonParseException, JsonMappingException, IOException {
		if(!checkSession(request, response)){
			return new ModelAndView("login");
		}
		ModelAndView modelnView = new ModelAndView("project/allocateProjectEvaluation");
		String level = searchBean.getLevel();
		int numberOfSubjects = 0;

		
		numberOfSubjects = projectSubmissionDAO.getNumberOfProjects(searchBean);
		ArrayList<FacultyExamBean> facultyList = getFacultyAndCurrentProjectsNumber(searchBean, level);
		try{

		}catch(Exception e){
			
		}

		modelnView.addObject("yearList", ACAD_YEAR_LIST);
		modelnView.addObject("subjectList", getSubjectList());
		modelnView.addObject("searchBean", searchBean);
		modelnView.addObject("facultyList", facultyList);
		modelnView.addObject("numberOfSubjects", numberOfSubjects);
		modelnView.addObject("showFaculties", "true");
		return modelnView;

	}
	
	private ArrayList<FacultyExamBean> getFacultyAndCurrentProjectsNumber(AssignmentFileBean searchBean, String level) {
		FacultyDAO fDao = (FacultyDAO)act.getBean("facultyDAO");
		ArrayList<FacultyExamBean> facultyListWithAssignmentCount = fDao.getFacultiesWithProjectCount(searchBean);
		
		HashMap<String, String> facultyAssignmentCountMap = new HashMap<>();
		for (FacultyExamBean facultyBean : facultyListWithAssignmentCount) {
			facultyAssignmentCountMap.put(facultyBean.getFacultyId(), facultyBean.getAssignmentsAllocated());
		}
		ArrayList<FacultyExamBean> facultyList = fDao.getFaculties();

		for (FacultyExamBean facultyBean : facultyList) {
			if(facultyAssignmentCountMap.containsKey(facultyBean.getFacultyId())){
				facultyBean.setAssignmentsAllocated(facultyAssignmentCountMap.get(facultyBean.getFacultyId()));
				facultyBean.setAvailable(Integer.parseInt(MAX_ASSIGNMENTS_PER_FACULTY) - Integer.parseInt(facultyBean.getAssignmentsAllocated()));
			}else{
				facultyBean.setAssignmentsAllocated("0");
				facultyBean.setAvailable(Integer.parseInt(MAX_ASSIGNMENTS_PER_FACULTY));
			}
		}

		return facultyList;
	}
	
	@RequestMapping(value="/admin/allocateProjectEvaluation", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView allocateProjectEvaluation(HttpServletRequest request, HttpServletResponse response , @ModelAttribute AssignmentFileBean searchBean, Model m) {
		if(!checkSession(request, response)){
			return new ModelAndView("login");
		}

		ModelAndView modelnView = new ModelAndView("project/allocateProjectEvaluation");
		String level = searchBean.getLevel();


		modelnView.addObject("searchBean",searchBean);
		modelnView.addObject("yearList", ACAD_YEAR_LIST);
		modelnView.addObject("subjectList", getSubjectList());

		try{

			ArrayList<String> faculties = searchBean.getFaculties();
			ArrayList<String> numberOfProjects = searchBean.getNumberOfAssignments();
			ArrayList<String> indexes = searchBean.getIndexes();
			List<AssignmentFileBean> assignments = projectSubmissionDAO.getProjects(searchBean);

			int startIndex = 0;


			startIndex = 0;
			for (int i = 0; i < indexes.size(); i++) {
				int index = Integer.parseInt(indexes.get(i));
				String facultyId = faculties.get(index);

				int lastIndex = startIndex + Integer.parseInt(numberOfProjects.get(i));

				List<AssignmentFileBean> assignmentsSubSet = assignments.subList(startIndex, lastIndex);
				projectSubmissionDAO.allocateProject(assignmentsSubSet, facultyId);

				startIndex = startIndex + Integer.parseInt(numberOfProjects.get(i));
			}

			setSuccess(request, "Project Evaluation allocated successfully!");

		}catch(Exception e){
			
			setError(request, "Error in allocating Project Evaluation");
		}

		modelnView.addObject("showFaculties", "true");
		modelnView.addObject("numberOfAssignments", null);
		modelnView.addObject("indexes", null);

		ArrayList<FacultyExamBean> facultyList = getFacultyAndCurrentProjectsNumber(searchBean, level);
		modelnView.addObject("facultyList", facultyList);

		int numberOfSubjects = projectSubmissionDAO.getNumberOfProjects(searchBean);
		modelnView.addObject("numberOfSubjects", numberOfSubjects);

		return modelnView;
	}
	
	@RequestMapping(value = "/admin/allocateProjectRevaluationForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView allocateProjectRevaluationForm(HttpServletRequest request, HttpServletResponse response, Model m) {

		AssignmentFileBean searchBean = new AssignmentFileBean();
		m.addAttribute("searchBean",searchBean);
		m.addAttribute("yearList", ACAD_YEAR_LIST);

		FacultyDAO dao = (FacultyDAO)act.getBean("facultyDAO");
		ArrayList<FacultyExamBean> facultyList = dao.getFaculties();
		m.addAttribute("facultyList", facultyList);

		return new ModelAndView("project/allocateProjectRevaluation");
	}

	@RequestMapping(value="/admin/searchProjectsForReval", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView searchProjectsForReval(HttpServletRequest request, HttpServletResponse response , @ModelAttribute AssignmentFileBean searchBean)  {
		if(!checkSession(request, response)){
			return new ModelAndView("login");
		}
		ModelAndView modelnView = new ModelAndView("project/allocateProjectRevaluation");
		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		int rowCount = 0;

		ArrayList<AssignmentFileBean> revalAssignments = (ArrayList<AssignmentFileBean>)dao.getProjectsForReval(searchBean);
		searchBean.setRevalAssignments(revalAssignments);
		rowCount = revalAssignments != null ? revalAssignments.size() : 0;
		try{

		}catch(Exception e){
			
		}

		modelnView.addObject("yearList", ACAD_YEAR_LIST);
		modelnView.addObject("searchBean", searchBean);
		modelnView.addObject("rowCount", rowCount);
		modelnView.addObject("revalAssignments", revalAssignments);
		
		request.getSession().setAttribute("facultyMap", getFacultyList());
		return modelnView;

		
	}
	
	@RequestMapping(value="/admin/allocateProjectsForReval", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView allocateProjectsForReval(HttpServletRequest request, HttpServletResponse response , @ModelAttribute AssignmentFileBean searchBean, Model m) {
		if(!checkSession(request, response)){
			return new ModelAndView("login");
		}

		ModelAndView modelnView = new ModelAndView("project/allocateProjectRevaluation");

		modelnView.addObject("searchBean",searchBean);
		modelnView.addObject("yearList", ACAD_YEAR_LIST);

		try{
			AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");

			List<AssignmentFileBean> revalAssignments = searchBean.getRevalAssignments();
			List<AssignmentFileBean> assignments = new ArrayList<>();
			
			for (AssignmentFileBean assignmentFileBean : revalAssignments) {
				if(assignmentFileBean.getFacultyId() != null && !"".equals(assignmentFileBean.getFacultyId())){
					assignments.add(assignmentFileBean);
				}
			}

			for (AssignmentFileBean assignmentFileBean : assignments) {
				boolean validFaculty =  dao.isValidFacultyForProjectReval(assignmentFileBean);
				if(!validFaculty){
					setError(request, "Faculty " + searchBean.getFacultyId() + " is already assigned same student evaluation. Please reassign to some other faculty.");
					return modelnView;
				}
			}
			
			dao.allocateProjectForReval(assignments);

			setSuccess(request, "Project Revaluation allocated successfully ");

		}catch(Exception e){
			
			setError(request, "Error in allocating Assignment Evaluation");
			return modelnView;
		}

		return searchProjectsForReval(request, response, searchBean);
	}
	
	@RequestMapping(value = "/admin/revaluateProject", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView revaluateProject(HttpServletRequest request, HttpServletResponse response, @ModelAttribute AssignmentFileBean searchBean) {
		if(!checkSession(request, response)){
			return new ModelAndView("login");
		}

		String facultyId = (String)request.getSession().getAttribute("userId");
		//String facultyId = "NMSCEMU200303521";
		searchBean.setFacultyId(facultyId);

		ModelAndView modelnView = new ModelAndView("project/revaluateProject");
		modelnView.addObject("searchBean",searchBean);
		modelnView.addObject("yearList", ACAD_YEAR_LIST);
		modelnView.addObject("subjectList", getSubjectList());

		if(searchBean.getRevaluationScore() == null || "".equals(searchBean.getRevaluationScore().trim())){
			setError(request, "Score cannot be empty. Please enter a score.");
			AssignmentFileBean assignmentFile = projectSubmissionDAO.getSingleProjectForFaculty(searchBean);
			modelnView.addObject("assignmentFile", assignmentFile);
			return modelnView;
		}
		try {

			projectSubmissionDAO.revaluateProject(searchBean);
			StudentExamBean student = projectSubmissionDAO.getSingleStudentsData(searchBean.getSapId());


			ArrayList<ProgramSubjectMappingExamBean> programSubjectMappingList = getProgramSubjectMappingList();

			for (int i = 0; i < programSubjectMappingList.size(); i++) {
				ProgramSubjectMappingExamBean bean = programSubjectMappingList.get(i);

				if(bean.getPrgmStructApplicable().equals(student.getPrgmStructApplicable()) && bean.getProgram().equals(student.getProgram())
						&& bean.getSubject().equals(searchBean.getSubject())){
					//Setting the semester for Subject 
					student.setSem(bean.getSem());
					break;
				}
			}

			
			AssignmentFileBean assignmentFile = projectSubmissionDAO.getSingleProjectForFaculty(searchBean);
			modelnView.addObject("assignmentFile", assignmentFile);

			setSuccess(request, "Project score saved successfully");
		} catch (Exception e) {
			
			setError(request, "Error in saving Project score");
		}
		return modelnView; 
	}
	
	//-----Project Payment Start-------

	@RequestMapping(value = "/student/goToProjectPaymentGateway", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView goToProjectPaymentGateway(HttpServletRequest request, HttpServletResponse response, 
			@ModelAttribute ExamBookingExamBean examBooking,Model model ) {
		projectPaymentsLogger.info("ProjectSubmissionController.goToProjectPaymentGateway() - START");
		
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
		
		int totalExamFees = examFeesPerSubject;
		
		ModelAndView modelnView = new ModelAndView("project/submitProject");

		int noOfSubjects = 1;
		
		try{

			StudentExamBean student = (StudentExamBean)request.getSession().getAttribute("studentExam");

			String subject = "Project";
			
			if("PD - WM".equals(student.getProgram())) {
				subject = "Module 4 - Project";
			}
			
			String sapid = (String)request.getSession().getAttribute("userId");
			sapid = sapid.trim();
			String trackId = sapid + System.currentTimeMillis() ;
			request.getSession().setAttribute("trackId", trackId);
			
			projectPaymentsLogger.info("Generated track_id for project submission is:"+trackId);

			String message = "Project fees for "+sapid;

			String prgrmStructApplicable = student.getPrgmStructApplicable();

			List<ExamBookingTransactionBean> bookingsList = new ArrayList<>();

			int year = Calendar.getInstance().get(Calendar.YEAR);
			int month = Calendar.getInstance().get(Calendar.MONTH);

			String examYear = year+"";
			String examMonth = "";

			if(month > 6){
				examMonth = "Dec";
			}else{
				examMonth = "Jun";
			}

			boolean hasProject = false;
//			if(subject.equalsIgnoreCase("Project")){
				 hasProject = true;
				ExamBookingTransactionBean bean = new ExamBookingTransactionBean();

				//Make two live cycle code -----START-----
				// Set Student Applicable Exam Mont Year
				String method = "goToProjectPaymentGateway()";
				AssignmentFileBean examMonthYearBean = eligibilityService.getStudentApplicableExamMonthYear(sapid, subject, method);
				bean.setMonth(examMonthYearBean.getMonth());
				bean.setYear(examMonthYearBean.getYear());
				//Make two live cycle code -----END-----
				
				// Revert Make two live cycle code -----START-----
//				bean.setYear(dao.getLiveProjectExamYear());
//				bean.setMonth(dao.getLiveProjectExamMonth());
				// Revert Make two live cycle code -----END-----
				
				bean.setSapid(sapid);
				bean.setSubject(subject);
				bean.setCenterId("-1");
				bean.setProgram(student.getProgram());
				bean.setSem(eligibilityService.getProjectApplicableProgramSem(student.getProgram()));
				bean.setExamDate(dao.getLiveExamYear() + "/01/01");
				bean.setExamTime("00:00");
				bean.setExamEndTime("00:00");
				bean.setTrackId(trackId);
				bean.setExamMode(student.getExamMode());
				bean.setAmount(totalExamFees +"");
				bean.setTranStatus(ONLINE_PAYMENT_INITIATED);
				bean.setBooked("N");
				bean.setPaymentMode("Online");
				bookingsList.add(bean);
//			}
			
//			if(subjects.equalsIgnoreCase("Module 4 - Project") && "PD - WM".equalsIgnoreCase(student.getProgram())){
//				 hasProject = true;
//				ExamBookingTransactionBean bean = new ExamBookingTransactionBean();
//
//				bean.setSapid(sapid);
//				bean.setSubject("Module 4 - Project");
//				bean.setCenterId("-1");
//				bean.setYear(dao.getLiveProjectExamYear());
//				bean.setMonth(dao.getLiveProjectExamMonth());
//				bean.setProgram(student.getProgram());
//				bean.setSem("2");
//				bean.setExamDate(dao.getLiveExamYear() + "/01/01");
//				bean.setExamTime("00:00");
//				bean.setExamEndTime("00:00");
//				bean.setTrackId(trackId);
//				bean.setExamMode(student.getExamMode());
//				bean.setAmount(totalExamFees +"");
//				bean.setTranStatus(ONLINE_PAYMENT_INITIATED);
//				bean.setBooked("N");
//				bean.setPaymentMode("Online");
//				bookingsList.add(bean);
//			}

			

			boolean isOnline = false;
			if("Online".equals(student.getExamMode())){
				isOnline = true;
			}

			if(noOfSubjects != bookingsList.size()){
				throw new Exception("We are sorry, Booking could not be saved, please try again.");
			}
			dao.upsertOnlineInitiationTransaction(sapid, bookingsList, hasProject, isOnline);

			int totalFees = 0;
			totalFees = totalExamFees;
			

			request.getSession().setAttribute("totalFees", totalFees + "");
			fillPaymentParametersInMap(model, student, totalFees, trackId, message);

			//request.getSession().setAttribute("SECURE_SECRET", SECURE_SECRET);
			return new ModelAndView("payment");


		}catch(Exception e){
			projectPaymentsLogger.error("Error in initiating Online transaction. Error: "+e);
			modelnView = new ModelAndView("project/submitProject");
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in initiating Online transaction. Error: "+e.getMessage());
			return modelnView;
		}

	}

	
	
	private void fillPaymentParametersInMap(Model model,
			StudentExamBean student, int totalFees, String trackId, String message) {
		projectPaymentsLogger.info("ProjectSubmissionController.fillPaymentParametersInMap() - START");
		
		/* Temporary Commented by Siddheshwar_Khanse on June 23, 2022 due not in use. 
		String address = "NGASCE,V L Mehta Rd,Vileparle,Mumbai"; //Not taking student address to avoid junk character issue in address. HDFC blocks such payments
		String city = "Mumbai";//Not taking student city to avoid junk character issue in address. HDFC blocks such payments*/
		
		String pin = student.getPin();
		
		if(pin == null || pin.trim().length() == 0){
			pin = "000000";
		}else if(pin.length() > 8){
			pin = pin.substring(0, 8);
		}

		String mobile = student.getMobile();
		if(mobile == null || mobile.trim().length() == 0){
			mobile = "0000000000";
		}

		String emailId = student.getEmailId();
		if(emailId == null || emailId.trim().length() == 0){
			emailId = "notavailable@email.com";
		}else if(emailId.length() > 100){
			emailId = emailId.substring(0, 100);
		}

		/* Temporary Commented by Siddheshwar_Khanse on June 23, 2022 due HDFC maintenance issue. 
		model.addAttribute("udf1", message);
		model.addAttribute("channel", "10");
		model.addAttribute("account_id", ACCOUNT_ID);
		model.addAttribute("reference_no", trackId);
		model.addAttribute("amount",totalFees);
		model.addAttribute("mode", "LIVE");
		model.addAttribute("currency", "INR");
		model.addAttribute("currency_code", "INR");
		model.addAttribute("description", message);
		model.addAttribute("return_url", RETURN_URL_PROJECT);
		model.addAttribute("name", student.getFirstName()+ " "+student.getLastName());
		model.addAttribute("address",URLEncoder.encode(address));
		model.addAttribute("city", city);
		model.addAttribute("country", "IND");
		model.addAttribute("postal_code", pin);
		model.addAttribute("phone", mobile);
		model.addAttribute("email", emailId);
		model.addAttribute("algo", "MD5");
		model.addAttribute("V3URL", V3URL);
		model.addAttribute("studentNumber", student.getSapid());*/
		
		projectPaymentsLogger.info("Setting required values to model as:");
		projectPaymentsLogger.info("track_id:"+trackId);
		projectPaymentsLogger.info("sapid:"+student.getSapid());
		projectPaymentsLogger.info("amount"+totalFees);
		projectPaymentsLogger.info("description"+ message);
		projectPaymentsLogger.info("portal_return_url"+ RETURN_URL_PROJECT);
		projectPaymentsLogger.info("mobile"+ mobile);
		projectPaymentsLogger.info("email_id"+ emailId);
		projectPaymentsLogger.info("first_name"+ student.getFirstName());

		
		model.addAttribute("track_id",trackId);
		model.addAttribute("sapid", student.getSapid());
		model.addAttribute("type", "Project_Submission");
		model.addAttribute("amount",totalFees);
		model.addAttribute("description", message);
		model.addAttribute("portal_return_url", RETURN_URL_PROJECT);
		model.addAttribute("created_by", student.getSapid());
		model.addAttribute("updated_by", student.getSapid());
		model.addAttribute("mobile", mobile);
		model.addAttribute("email_id", emailId);
		model.addAttribute("first_name", student.getFirstName());
		model.addAttribute("source", "web");

		projectPaymentsLogger.info("ProjectSubmissionController.fillPaymentParametersInMap() - END");
	}

	@RequestMapping(value = "/student/payProject", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView pay(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		projectPaymentsLogger.info("ProjectSubmissionController.pay()");
		return new ModelAndView("payment");
	}
	
	
	@RequestMapping(value = "/student/projectFeesReponse", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView projectFeesReponse(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		projectPaymentsLogger.info("ProjectSubmissionController.projectFeesReponse() - START");
		//String typeOfPayment = (String)request.getParameter("PaymentMethod");
		saveAllProjectTransactionDetails(request);

		String onlineSeatBookingComplete = (String)request.getSession().getAttribute("onlineSeatBookingComplete");
		if("true".equals(onlineSeatBookingComplete)){
			projectPaymentsLogger.info("Online Project Booking Completed redirecting to the booking status.");
			return new ModelAndView("projectBookingStatus");
		}
		String trackId = (String)request.getSession().getAttribute("trackId");
		String totalFees = (String)request.getSession().getAttribute("totalFees");
		String errorMessage = null;
		
		projectPaymentsLogger.info("trackId:"+trackId);
		projectPaymentsLogger.info("totalFees:"+totalFees);
		
		//boolean isHashMatching = isHashMatching(request);
		boolean isAmountMatching = isAmountMatching(request, totalFees);
		boolean isTrackIdMatching = isTrackIdMatching(request, trackId);
		//boolean isSuccessful = isTransactionSuccessful(request);
		boolean isSuccessful = "Payment Successfull".equalsIgnoreCase(request.getParameter("transaction_status"));
		
		projectPaymentsLogger.info("isSuccessful:"+isSuccessful);
		projectPaymentsLogger.info("isAmountMatching:"+isAmountMatching);
		projectPaymentsLogger.info("isTrackIdMatching:"+isTrackIdMatching);
		
		if (!isSuccessful) {
			errorMessage = "Error in processing payment. Error: " + request.getParameter("error") + " Code: "
			+ request.getParameter("response_code");
		}
		
		/*if (!isHashMatching) {
			errorMessage = "Error in processing payment. Error: Hashvalue not matching. Tampering in response found. Track ID: "
			+ trackId;
		}*/
		
		if (!isAmountMatching) {
			errorMessage =" Error in processing payment. Error: Fees " + totalFees + " not matching with amount paid "
			+ request.getParameter("response_amount");
		}
		
		if (!isTrackIdMatching) {
			errorMessage = "Error in processing payment. Error: Track ID: " + trackId
		    + " not matching with Merchant Ref No. " + request.getParameter("merchant_ref_no");
		}
		if(errorMessage != null){
			projectPaymentsLogger.info("ErrorMessage:"+errorMessage);
			if(!"paytm".equalsIgnoreCase(request.getParameter("payment_option"))) {
				try {
					ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
					String sapid = (String)request.getSession().getAttribute("userId");
					ExamBookingTransactionBean responseBean = new ExamBookingTransactionBean();
					
					responseBean.setSapid(sapid);
					responseBean.setPaymentOption(request.getParameter("payment_option"));
					responseBean.setTrackId(trackId);
					responseBean.setError(errorMessage);
	
					dao.saveProjectBookingTransactionFailed(responseBean);
					
				} catch (Exception e) {
					projectPaymentsLogger.error("Error in saving transaction failed status,:"+e);
				}
			}
			projectPaymentsLogger.info("Redirecting back to project view form with error message.");
			return sendBackToViewProjectPage(request,response, errorMessage);
		}else{

			return saveSuccessfulProjectTransaction(request, response, model);
		}
	}
	
	/**
	 * Exposed API to update project payments coming from payment gateway
	 * 
	 * @param transaction bean
	 * @return ResponseEntity with message
	 */
	@RequestMapping(value = "/m/projectGatewayResponse",consumes = "application/json")
	@ResponseBody
	public ResponseEntity<String> projectGatewayResponse(@RequestBody TransactionsBean bean) {
		razorpayLogger.info("received webhook for project payment : " + bean);

		String responseMessage = null;
		try {

			if (bean.getTrack_id() == null) {
				return sendOkayResponse("INVALID REQUEST");
			}

			ExamBookingDAO dao = (ExamBookingDAO) act.getBean("examBookingDAO");
			ExamBookingTransactionBean examBean = new ExamBookingTransactionBean();

			examBean = dao.getProjectTransactionStatusByTrackId(bean.getTrack_id());

			razorpayLogger.info("bean received for track id : {} : " + examBean, bean.getTrack_id());

			Map<String, String> errorMap = new HashMap<>();

			errorMap = checkIfTransactionHasError(examBean, bean);

			if (errorMap.containsKey("error")) {
				responseMessage = errorMap.get("error") + " for track id " + bean.getTrack_id();
				razorpayLogger.info(responseMessage);
				return sendOkayResponse(responseMessage);
			}

			if (GATEWAY_STATUS_SUCCESSFUL.equalsIgnoreCase(bean.getTransaction_status())) {

				razorpayLogger.info("success status received for track id : {} so preparing bean to update db",
						bean.getTrack_id());

				AssignmentFileBean assignmentFile = new AssignmentFileBean();

				StudentExamBean studentBean = projectSubmissionDAO.getSingleStudentsData(bean.getSapid());

				if ("PD - WM".equals(studentBean.getProgram())) {
					assignmentFile.setSubject("Module 4 - Project");
				} else {
					assignmentFile.setSubject("Project");
				}

				examBean.setResponseMessage(bean.getResponse_message());
				examBean.setTransactionID(bean.getTransaction_id());
//				examBean.setRequestID(request.getParameter("request_id"));
				examBean.setMerchantRefNo(bean.getMerchant_ref_no());
				examBean.setSecureHash(bean.getSecure_hash());
				examBean.setRespAmount(bean.getResponse_amount());
				examBean.setRespTranDateTime(bean.getResponse_transaction_date_time());
				examBean.setResponseCode(bean.getResponse_code());
				examBean.setRespPaymentMethod(bean.getResponse_payment_method());
				// examBean.setIsFlagged(request.getParameter("IsFlagged"));
				examBean.setPaymentID(bean.getPayment_id());
				examBean.setError(bean.getError());
				examBean.setDescription(bean.getDescription());
				examBean.setPaymentOption(bean.getPayment_option());
				examBean.setBankName(bean.getBank_name());
				examBean.setEmailId(bean.getEmail_id());

				ExamCenterDAO eDao = (ExamCenterDAO) act.getBean("examCenterDAO");

				HashMap<String, String> corporateCenterUserMapping = eDao.getCorporateCenterUserMapping();

				if (corporateCenterUserMapping.containsKey(studentBean.getSapid()))
					studentBean.setCorporateExamCenterStudent(true);
				else
					studentBean.setCorporateExamCenterStudent(false);

				razorpayLogger.info("Project booking examBean details post payment: " + examBean);

				// Below method is made using single Connection to ensure Commit and Rollback
				dao.updateSeatsForOnlineUsingSingleConnection(examBean, studentBean.isCorporateExamCenterStudent());

				razorpayLogger.info("database updated now sending mail for track id " + bean.getTrack_id());


				String status = examBookingHelper.createAndUploadProjectFeeReceipt(bean.getSapid(), bean.getTrack_id(), assignmentFile.getSubject());

				if (status.equalsIgnoreCase("error")) {
					razorpayLogger.info("Error in Project Fee Receipt Generation for track id " + bean.getTrack_id());
				}
				try {
					MailSender mailSender = (MailSender) act.getBean("mailer");
					mailSender.sendProjectBookingSummaryEmailFromGateway(studentBean, dao, projectSubmissionDAO,
							assignmentFile);
				} catch (Exception e) {
					razorpayLogger.info("error occurred while sending mail for track id : " + bean.getTrack_id());
				}

				responseMessage = "table updated and mail sent for track id " + bean.getTrack_id();

			} else if (GATEWAY_STATUS_FAILED.equalsIgnoreCase(bean.getTransaction_status())) {

				razorpayLogger.info("failed status received for track id : {} so marking as failed",
						bean.getTrack_id());

				examBean.setPaymentOption(bean.getPayment_option());
				examBean.setError(bean.getError());
				dao.markTransactionsFailed(examBean);

				responseMessage = "track id " + bean.getTrack_id() + " marked as failed";
			} else {
				responseMessage = "INVALID TRANSACTION STATUS " + bean.getTransaction_status() + " FOR TRACK ID "
						+ bean.getTrack_id();
			}

			razorpayLogger.info(responseMessage);
			return sendOkayResponse(responseMessage);

		} catch (Exception e) {
			// e.printStackTrace();
			razorpayLogger
					.error("Error in updating project details of trackid: " + bean.getTrack_id() + " with error: " + e);

			try {
				MailSender mailSender = (MailSender) act.getBean("mailer");
				mailSender.mailStackTrace("Error in Saving Successful Transaction", e);
			} catch (Exception e2) {
				razorpayLogger.info("error occurred while sending error stack trace mail : " + e.getMessage() + " for payload" + bean);
			}
			responseMessage = "Error in Saving Successful Transaction : " + e + " for track id : " + bean.getTrack_id();
			return sendOkayResponse(responseMessage);
		}
	}
	
	private ResponseEntity<String> sendOkayResponse(String response) {
		return new ResponseEntity<String>(response, HttpStatus.OK);
	}

	private Map<String, String> checkIfTransactionHasError(ExamBookingTransactionBean bean,
			TransactionsBean requestBean) {

		Map<String, String> errorMap = new HashMap<>();

		try {
			if (bean.getTrackId() == null) {
				errorMap.put("error", "No transaction found");
				return errorMap;
			}

			if (PROJECT_PAYMENT_SUCCESSFUL.equalsIgnoreCase(bean.getTranStatus())
							|| PROJECT_MANUALLY_APPROVED.equalsIgnoreCase(bean.getTranStatus())
							|| "Y".equalsIgnoreCase(bean.getBooked())) {
				
				errorMap.put("error", "payment was already marked as successful");
				return errorMap;
				
			} else if (GATEWAY_STATUS_FAILED.equalsIgnoreCase(requestBean.getTransaction_status())
					&& PROJECT_TRANSACTION_FAILED.equalsIgnoreCase(bean.getTranStatus())) {
				
				errorMap.put("error", "Payment was already marked as failed");
				return errorMap;
			}

		} catch (Exception e) {
			errorMap.put("error", "Error while trying to check error in transaction");
			return errorMap;
		}
		return errorMap;
	}

	private ModelAndView sendBackToViewProjectPage(HttpServletRequest request,HttpServletResponse response, String errorMessage) {
		if(!checkSession(request, null)){
			redirectToPortalApp(response);
			return null;
		}
		request.setAttribute("error", "true");
		request.setAttribute("errorMessage", errorMessage);

		AssignmentFileBean assignmentFile = new AssignmentFileBean();
		ModelAndView modelnView = new ModelAndView("project/submitProject");
		modelnView.addObject("canSubmit",false);
		modelnView.addObject("paymentApplicable",true);
		modelnView.addObject("assignmentFile",assignmentFile);
		return modelnView;
	}
	
	private ModelAndView processSuccessTransaction(String trackId,HttpServletRequest request, ExamBookingDAO dao) {
		
			String sapid = (String)request.getSession().getAttribute("userId");
			
			request.setAttribute("success", "true");
			request.setAttribute("successMessage", BOOKING_SUCCESS_MSG);
			request.getSession().setAttribute("onlineSeatBookingComplete", "true");
			
			List<ExamBookingTransactionBean> examBookings = dao.getExamBookingTransactionBean(sapid, trackId);
			
			request.getSession().setAttribute("examBookings", examBookings);
			return new ModelAndView("projectBookingStatus");			
		}
	
	public ModelAndView saveSuccessfulProjectTransaction(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		logger.info("saveSuccessfulProjectTransaction"); 
		projectPaymentsLogger.info("ProjectSubmissionController.saveSuccessfulProjectTransaction() - START");
		
		ModelAndView modelnView = new ModelAndView("projectBookingStatus");
		
		String sapid = (String)request.getSession().getAttribute("userId");
		String trackId = (String)request.getSession().getAttribute("trackId");
		StudentExamBean student = (StudentExamBean)request.getSession().getAttribute("studentExam");
		String fileName = "";
		
		try {
			ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
			ExamBookingTransactionBean bean = dao.getProjectTransactionStatusByTrackId(trackId);
			
			if ("Y".equalsIgnoreCase(bean.getBooked())) {
				projectPaymentsLogger.info(
						"payment was already marked successfull by webhook so returning model and view for track id"
								+ trackId);
				return processSuccessTransaction(trackId, request, dao);
			}
			
			AssignmentFileBean assignmentFile= new AssignmentFileBean();
			
			if("PD - WM".equals(student.getProgram())) {
				assignmentFile.setSubject("Module 4 - Project");
			} else {
				assignmentFile.setSubject("Project");
			}
					
			bean.setSapid(sapid);
			bean.setTrackId(trackId);
			
			bean.setResponseMessage(request.getParameter("response_message"));
			bean.setTransactionID(request.getParameter("transaction_id"));
			bean.setRequestID(request.getParameter("request_id"));
			bean.setMerchantRefNo(request.getParameter("merchant_ref_no"));
			bean.setSecureHash(request.getParameter("secure_hash"));
			bean.setRespAmount(request.getParameter("response_amount"));
			bean.setRespTranDateTime(request.getParameter("response_transaction_date_time"));
			bean.setResponseCode(request.getParameter("response_code"));
			bean.setRespPaymentMethod(request.getParameter("response_payment_method"));
			//bean.setIsFlagged(request.getParameter("IsFlagged"));
			bean.setPaymentID(request.getParameter("payment_id"));
			bean.setError(request.getParameter("error"));
			bean.setDescription(request.getParameter("description"));
			bean.setPaymentOption(request.getParameter("payment_option"));
			
			projectPaymentsLogger.info("Project booking bean details post payment:"+bean);

			//Below method is made using single Connection to ensure Commit and Rollback
			List<ExamBookingTransactionBean> examBookings = dao.updateSeatsForOnlineUsingSingleConnection(bean, student.isCorporateExamCenterStudent());
			request.getSession().setAttribute("examBookings", examBookings);
			request.getSession().setAttribute("onlineSeatBookingComplete", "true");
			request.setAttribute("success","true");
			request.setAttribute("successMessage",BOOKING_SUCCESS_MSG);
			
			if("PD - WM".equals(student.getProgram())) {
				request.setAttribute("subject", "Module 4 - Project");
				request.setAttribute("successMessage",PDWM_BOOKING_SUCCESS_MSG);
			} else {
				request.setAttribute("subject", "Project");
				request.setAttribute("successMessage",BOOKING_SUCCESS_MSG);
			}
			MailSender mailSender = (MailSender)act.getBean("mailer");
			String status = examBookingHelper.createAndUploadProjectFeeReceipt(sapid,trackId,assignmentFile.getSubject());
			
			if(status.equalsIgnoreCase("error")) {
				projectSubmissionLogger.info("Error in Project Fee Receipt Generation Sapid-"+sapid);
			}else {
				fileName=status; 
				request.getSession().setAttribute("projectFeeReceiptFile", fileName);  
				modelnView.addObject("fileName", fileName);  
			}
			
			projectPaymentsLogger.info("Sending project booking mail to:"+student.getEmailId());
			mailSender.sendProjectBookingSummaryEmail(request, dao, projectSubmissionDAO, assignmentFile);
			
		} catch (Exception e) {
			//e.printStackTrace();
			projectPaymentsLogger.error("Error in updating project details of trackid:"+trackId+" with error:"+e);
			MailSender mailSender = (MailSender)act.getBean("mailer");
			mailSender.mailStackTrace("Error in Saving Successful Transaction", e);

			request.getSession().setAttribute("onlineSeatBookingComplete", "false");
			request.getSession().setAttribute("paymentApplicable", "false");
			if("PD - WM".equals(student.getProgram())) {
				request.setAttribute("subject", "Module 4 - Project");
			} else {
				request.setAttribute("subject", "Project");
			}
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Project NOT booked. Error in recording your transaction details. Please contact Head Office to get it sorted out.");
			return new ModelAndView("projectBookingStatus");
		}
		request.getSession().setAttribute("onlineSeatBookingComplete", "true");
		
		return modelnView;
	}
	
	

	private void saveAllProjectTransactionDetails(HttpServletRequest request) {
		projectPaymentsLogger.info("ProjectSubmissionController.saveAllProjectTransactionDetails() - START");
		try {
			String sapid = (String)request.getSession().getAttribute("userId");
			String trackId = (String)request.getSession().getAttribute("trackId");
			ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
			ExamBookingTransactionBean bean = new ExamBookingTransactionBean();
			bean.setSapid(sapid);
			bean.setTrackId(trackId);

			bean.setResponseMessage(request.getParameter("response_message"));
			bean.setTransactionID(request.getParameter("transaction_id"));
			bean.setRequestID(request.getParameter("request_id"));
			bean.setMerchantRefNo(request.getParameter("merchant_ref_no"));
			bean.setSecureHash(request.getParameter("secure_hash"));
			bean.setRespAmount(request.getParameter("response_amount"));
			bean.setRespTranDateTime(request.getParameter("response_transaction_date_time"));
			bean.setResponseCode(request.getParameter("response_code"));
			bean.setRespPaymentMethod(request.getParameter("response_payment_method"));
			//bean.setIsFlagged(request.getParameter("IsFlagged"));
			bean.setPaymentID(request.getParameter("payment_id"));
			bean.setError(request.getParameter("error"));
			bean.setDescription(request.getParameter("description"));

			projectPaymentsLogger.info("Project Payment bean before inserting in online transactions: "+bean);	
			dao.insertOnlineTransaction(bean);

		} catch (Exception e) {
			projectPaymentsLogger.error("Error while inserting online transaction details. Error Message:"+e);
		}
		projectPaymentsLogger.info("ProjectSubmissionController.saveAllProjectTransactionDetails() - END");
	}
	
	private String md5(String str) throws Exception {
		MessageDigest m = MessageDigest.getInstance("MD5");

		byte[] data = str.getBytes();

		m.update(data,0,data.length);

		BigInteger i = new BigInteger(1,m.digest());

		String hash = String.format("%1$032X", i);

		return hash;
	}

	
	private boolean isHashMatching(HttpServletRequest request) {
		try{
			String md5HashData = SECURE_SECRET;
			HashMap testMap = new HashMap();
			Enumeration<String> en = request.getParameterNames();

			while(en.hasMoreElements()) {
				String fieldName = (String) en.nextElement();
				String fieldValue = request.getParameter(fieldName);
				if ((fieldValue != null) && (fieldValue.length() > 0)) {
					testMap.put(fieldName, fieldValue);
				}
			}

			//Sort the HashMap
			Map requestFields = new TreeMap<>(testMap);

			String V3URL = (String) requestFields.remove("V3URL");
			requestFields.remove("submit");
			requestFields.remove("SecureHash");

			for (Iterator i = requestFields.keySet().iterator(); i.hasNext(); ) {

				String key = (String)i.next();
				String value = (String)requestFields.get(key);
				md5HashData += "|"+value;

			}

			String hashedvalue = md5(md5HashData);
			String receivedHashValue = request.getParameter("SecureHash");

			if(receivedHashValue != null && receivedHashValue.equals(hashedvalue)){
				return true;
			}else{
				return false;
			}
		}catch(Exception e){
			
		}
		return false;
	}
	
	private boolean isAmountMatching(HttpServletRequest request, String totalFees) {
		try {
			double feesSent = Double.parseDouble(totalFees);
			double amountReceived = Double.parseDouble(request.getParameter("response_amount"));

			if(feesSent == amountReceived){
				return true;
			}else{
				return false;
			}
		} catch (Exception e) {
			
		}
		return false;
	}

	private boolean isTrackIdMatching(HttpServletRequest request, String trackId) {
		if(trackId != null && trackId.equals(request.getParameter("merchant_ref_no"))){
			return true;
		}else{
			return false;
		}
	}
	
	private boolean isTransactionSuccessful(HttpServletRequest request) {
		String error = request.getParameter("Error");
		//Error parameter should be absent to call it successful 
		if(error == null){
			//Response code should be 0 to call it successful
			String responseCode = request.getParameter("ResponseCode");
			if("0".equals(responseCode)){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}

	}
	
	@RequestMapping(value = "/student/printProjectBookingStatus", method = {RequestMethod.GET})
	public String printBookingStatus(HttpServletRequest request, HttpServletResponse response,RedirectAttributes redirectAttributes) {

		try{

			ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
			StudentMarksDAO sDao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			ExamCenterDAO examCenterDao = (ExamCenterDAO)act.getBean("examCenterDAO");
			String userId = (String)request.getSession().getAttribute("userId");
			//Added this avoid null in reciept download for students ending validity in Oct.
			StudentExamBean student = (StudentExamBean)request.getSession().getAttribute("studentExam");
			AssignmentFileBean bean = new AssignmentFileBean();
			if("PD - WM".equals(student.getProgram())) {
				bean.setSubject("Module 4 - Project");
			} else {
				bean.setSubject("Project");
			}
			bean.setConsumerProgramStructureId(student.getConsumerProgramStructureId());
			AssignmentFileBean projectFile = new AssignmentFileBean();
//			projectFile = projectSubmissionDAO.findById(bean);// Commented to make two cycle live
			String method = "printBookingStatus()";
			AssignmentFileBean examMonthYearBean = eligibilityService.getStudentApplicableExamMonthYear(userId, bean.getSubject(), method);
			projectFile.setMonth(examMonthYearBean.getMonth());
			projectFile.setYear(examMonthYearBean.getYear());
			projectFile = projectSubmissionDAO.findProjectGuidelinesForApplicableCycle(projectFile);
			String endDate = projectFile.getEndDate();
			endDate = endDate.replaceAll("T", " ");
			endDate = endDate.substring(0,10);
			
			
			HashMap<String,String> corporateCenterUserMapping = new HashMap<String,String>();
			HashMap<String,String> getCorporateExamCenterIdNameMap = new HashMap<String,String>();
			corporateCenterUserMapping = examCenterDao.getCorporateCenterUserMapping();
			boolean isCorporate = false;
			String fileName = "";
			
		
			String trackId = (String)request.getSession().getAttribute("trackId");
			if(userId == null){
				//Link clicked from Support portal by Agent
				userId = request.getParameter("userId");
			}
			
			
			if(corporateCenterUserMapping.containsKey(student.getSapid())){
				isCorporate = true;
			}
			getCorporateExamCenterIdNameMap = examCenterDao.getCorporateExamCenterIdNameMap();
			List<ExamBookingTransactionBean> examBookings = new ArrayList<ExamBookingTransactionBean>();
//			examBookings = dao.getConfirmedProjectBooking(userId); // Commented to make two cycle live
			
			examBookings = eligibilityService.getConfirmedProjectBookingApplicableCycle(userId, examMonthYearBean.getMonth(), examMonthYearBean.getYear());
			List<ExamBookingTransactionBean> confirmedOrReleasedProjectExamBookings = dao.getConfirmedOrReleasedProjectExamBookings(userId,trackId);
			if(examBookings == null || examBookings.isEmpty()){
				setError(request, "No Exam Bookings found for current Exam Cycle");
				
		        return "examBookingReceipt";
		        
			}
			ExamBookingPDFCreator pdfCreator = new ExamBookingPDFCreator();

			if(isCorporate){
				fileName = pdfCreator.createProjectBookedPDF(examBookings, getCorporateExamCenterIdNameMap, FEE_RECEIPT_PATH, student, confirmedOrReleasedProjectExamBookings, endDate);
			}else{
				fileName = pdfCreator.createProjectBookedPDF(examBookings, getExamCenterIdNameMap(), FEE_RECEIPT_PATH, student, confirmedOrReleasedProjectExamBookings,endDate);
			}
			File fileToDownload = new File(fileName);
			InputStream inputStream = new FileInputStream(fileToDownload);
			response.setContentType("application/pdf");
			response.setHeader("Content-Disposition", "attachment; filename="+userId+"_project_Fee_Receipt.pdf"); 
			IOUtils.copy(inputStream, response.getOutputStream());
			response.flushBuffer();

		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in generating Exam Fee Receipt: "+e.getMessage());
			return "examBookingReceipt";
		}
		return null;

	}
	
	private ArrayList<String> getFreeSubjects(StudentExamBean student) {
		ArrayList<String> freeSubjects = new ArrayList<>();
		ArrayList<StudentExamBean> exemptStudentList = getProjectExemptStudentList();
		HashMap<String, String> exemptSAPids = new HashMap<>();
		for (int i = 0; i < exemptStudentList.size(); i++) {
			StudentExamBean bean = exemptStudentList.get(i);
			exemptSAPids.put(bean.getSapid(), bean.getSem());
		}
		if(exemptSAPids.containsKey(student.getSapid())){
			String exemptSem = exemptSAPids.get(student.getSapid());

			ArrayList<ProgramSubjectMappingExamBean> programSubjectMappingList = getProgramSubjectMappingList();
			for (int i = 0; i < programSubjectMappingList.size(); i++) {
				ProgramSubjectMappingExamBean bean = programSubjectMappingList.get(i);

				if(bean.getPrgmStructApplicable().equals(student.getPrgmStructApplicable()) && bean.getProgram().equals(student.getProgram())
						&& bean.getSem().equals(exemptSem)){
					freeSubjects.add(bean.getSubject());
				}
			}
		}
		return freeSubjects;
	}
		//-----Project Payment End-------

	@RequestMapping(value = "/admin/projectCopyCaseCheckForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String projectCopyCaseCheckForm(HttpServletRequest request, HttpServletResponse response , Model m){
		m.addAttribute("yearList",currentYearList);
		m.addAttribute("subjectList",projectSubjectList);
		AssignmentFileBean searchBean = new AssignmentFileBean();
		m.addAttribute("searchBean",searchBean);
		return "project/copyCaseCheck";
	}
	@RequestMapping(value = "/admin/projectCopyCaseCheck", method = {RequestMethod.POST})
	public ModelAndView projectCopyCaseCheck(HttpServletRequest request, HttpServletResponse response, @ModelAttribute AssignmentFileBean searchBean){
		ModelAndView modelnView = new ModelAndView("project/copyCaseCheck");

		logger.info("called copycase check method...");
		if(StringUtils.isBlank(searchBean.getYear()) || StringUtils.isBlank(searchBean.getMonth()) || StringUtils.isBlank(searchBean.getSubject())){
			//error msg
			setError(request, "Please enter all the fields before processing");
			return modelnView;
		}
		
		Page<AssignmentFileBean> page =  projectSubmissionDAO.getProjectSubmissionPage(1, Integer.MAX_VALUE, searchBean);
		List<AssignmentFileBean> projecttFilesList = page.getPageItems();
		logger.info("project files found:"+projecttFilesList.size());
		modelnView.addObject("yearList",currentYearList);  
		modelnView.addObject("subjectList",projectSubjectList);
		modelnView.addObject("searchBean",searchBean);
		
		if(projecttFilesList == null || projecttFilesList.size() == 0){
			//error msg
			setError(request, "No records found.");
			return modelnView;
		}
		try {
			logger.info("started project copycase algorithm ..");
			copyCaseHelper.checkCopyCasesForProject(projecttFilesList, searchBean);
			setSuccess(request, "Copy check procedure initiated successfully. File will be created and saved on Server disk");
			return modelnView;
		} catch (Exception e) {
			logger.info("Error: " + e.getMessage());
			
			setError(request,  "Error: " + e.getMessage());
			return modelnView;
		}
	}
	@RequestMapping(value = "/admin/projectMarkCopyCasesForm", method = {RequestMethod.GET})
	public String markCopyCasesForm(HttpServletRequest request, HttpServletResponse response, Model m) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		AssignmentFileBean searchBean = new AssignmentFileBean();
		m.addAttribute("searchBean",searchBean);
		m.addAttribute("yearList", currentYearList);
		//m.addAttribute("subjectList", getSubjectList());

		return "project/markCopyCases";
	}
	private List<String> generateSapidList(String sapIdList) {
		String commaSeparatedList = sapIdList.replaceAll("(\\r|\\n|\\r\\n)+",
				",");
		if (commaSeparatedList.endsWith(",")) {
			commaSeparatedList = commaSeparatedList.substring(0,
					commaSeparatedList.length() - 1);
		}
		List<String> sapidList = new ArrayList<String>(Arrays.asList(commaSeparatedList.split(",")));
		return sapidList;
	}
	@RequestMapping(value = "/admin/markProjectCopyCases", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView markCopyCases(HttpServletRequest request, HttpServletResponse response, @ModelAttribute AssignmentFileBean searchBean){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		ModelAndView modelnView = new ModelAndView("project/markCopyCases");
		request.getSession().setAttribute("searchBean", searchBean);
		modelnView.addObject("searchBean", searchBean);
		modelnView.addObject("yearList", currentYearList);
		//modelnView.addObject("subjectList", getSubjectList());

		if(searchBean.getSapIdList() == null || "".equalsIgnoreCase(searchBean.getSapIdList())){
			setError(request, "Please enter Student Ids to mark copy cases");
			return modelnView;
		}
		final List<String> sapidList = generateSapidList(searchBean.getSapIdList());
		try {
			String userId = (String)request.getSession().getAttribute("userId");
			ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
			
			projectCCLogger.info("Marking CC Month-Year:{}{} Subject:{} Sapid List:{}", searchBean.getMonth(), searchBean.getYear(), searchBean.getSubject(), sapidList);
			for(String sapid : sapidList) {
				searchBean.setLastModifiedBy(userId);
				searchBean.setSapId(sapid);
				
				projectCCLogger.info("Marking CC in projectsubmission Month-Year:{}{} Subject:{} Sapid:{}", searchBean.getMonth(), searchBean.getYear(), searchBean.getSubject(), sapid);
				projectSubmissionDAO.markCopyCases(searchBean);
				
				StudentExamBean studentDetails = eDao.getSingleStudentsData(sapid);
				String sem = projectSubmissionDAO.getCurrentSemForStudent(sapid);
				
				//update copy case mark in marks table
				StudentMarksBean bean =new StudentMarksBean();
				bean.setWritenscore("0");
				bean.setLastModifiedBy(userId);
				bean.setCreatedBy(userId);
				bean.setYear(searchBean.getYear());
				bean.setMonth(searchBean.getMonth());
				bean.setSapid(sapid);
				bean.setSubject(searchBean.getSubject());
				bean.setSem(sem);
				bean.setStudentname(studentDetails.getFirstName()+" "+studentDetails.getLastName());
				bean.setProgram(studentDetails.getProgram());
				bean.setStudentType(studentDetails.getConsumerType());
				bean.setGrno("Not Available");
				
				projectCCLogger.info("Upserting project marks CC Month-Year:{}{} Subject:{} Sapid:{}", searchBean.getMonth(), searchBean.getYear(), searchBean.getSubject(), sapid);
				projectSubmissionDAO.upsertProjectMarks(bean);
			}
			projectCCLogger.info("Copy cases marked successfully");
			setSuccess(request, "Copy cases marked successfully");
		} catch (Exception e) {
			projectCCLogger.error("Exception Error marking Copy Cases Month-Year:{}{} Subject:{} SapidList:{} Error:{}", searchBean.getMonth(), searchBean.getYear(), searchBean.getSubject(), sapidList, e);
			setError(request, "Error in marking Copy Cases");
		}

		return modelnView;
	}
	
	@RequestMapping(value = "/admin/searchProjectCopyCases", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView searchCopyCases(HttpServletRequest request, HttpServletResponse response, @ModelAttribute AssignmentFileBean searchBean){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView modelnView = new ModelAndView("project/markCopyCases");
		request.getSession().setAttribute("searchBean", searchBean);

		Page<AssignmentFileBean> page = projectSubmissionDAO.searchCopyCases(1, pageSize, searchBean);
		List<AssignmentFileBean> assignmentFilesList = page.getPageItems();
		
		modelnView.addObject("assignmentFilesList", assignmentFilesList);
		modelnView.addObject("page", page);

		modelnView.addObject("searchBean", searchBean);
		modelnView.addObject("rowCount", page.getRowCount());

		modelnView.addObject("yearList", currentYearList);
		//modelnView.addObject("subjectList", getSubjectList());
		if(assignmentFilesList == null || assignmentFilesList.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No records found.");
		}
		return modelnView;
	}
	@RequestMapping(value = "/admin/downloadProjectCopyCases", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadCopyCases(HttpServletRequest request, HttpServletResponse response, @ModelAttribute AssignmentFileBean searchBean) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		List<AssignmentFileBean> assignmentSubmittedList = projectSubmissionDAO.getCopyCases(searchBean);

		return new ModelAndView("assignmentCopyCasesExcelView","assignmentSubmittedList",assignmentSubmittedList);
	}
	

	@RequestMapping(value = "/student/viewPreviousProjects", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView viewPreviousAssignments(HttpServletRequest request, HttpServletResponse response, @ModelAttribute AssignmentFileBean searchBean){
		ModelAndView modelnView = new ModelAndView("project/viewPreviousProjectFiles");
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		StudentExamBean student = (StudentExamBean)request.getSession().getAttribute("studentExam");
		searchBean.setSapId(student.getSapid());
		searchBean.setConsumerProgramStructureId(student.getConsumerProgramStructureId());
		request.getSession().setAttribute("searchBean", searchBean);
		
		Page<AssignmentFileBean> page = projectSubmissionDAO.getProjectSubmissionPage(1, Integer.MAX_VALUE, searchBean);
		List<AssignmentFileBean> assignmentFilesList = page.getPageItems();
		modelnView.addObject("assignmentFilesList", assignmentFilesList);
		modelnView.addObject("page", page);
		modelnView.addObject("searchBean", searchBean);
		modelnView.addObject("rowCount", page.getRowCount());
		modelnView.addObject("yearList", currentYearList);
		modelnView.addObject("subjectList", getSubjectList());
		if(assignmentFilesList == null || assignmentFilesList.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No Previous Project Submissions found.");
		}
		return modelnView;
  }

//
//	@RequestMapping(value = "/m/viewPreviousProjects", method = {RequestMethod.GET, RequestMethod.POST})
//	public ResponseEntity<AssignmentHistoryResponseBean> mViewPreviousAssignments(HttpServletRequest request,
//			@RequestBody Person input){
//		
//		AssignmentHistoryResponseBean response = new AssignmentHistoryResponseBean();
//
//		AssignmentFileBean searchBean = new AssignmentFileBean();
//		searchBean.setSapId(input.getSapId());
//		Page<AssignmentFileBean> page = projectSubmissionDAO.getProjectSubmissionPage(1, Integer.MAX_VALUE, searchBean);
//		List<AssignmentFileBean> assignmentFilesList = page.getPageItems();
//		if(assignmentFilesList == null || assignmentFilesList.size() == 0){
//			response.setError("true");
//			response.setErrorMessage("No Assignment Submissions found.");
//		} else {
//			response.setError("false");
//			response.setData(assignmentFilesList);
//		}
//		return new ResponseEntity<AssignmentHistoryResponseBean>(response, HttpStatus.OK);
//	}
	
	@RequestMapping(value = "/levelBasedProjectCheckList", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView levelBasedProjectCheckList(HttpServletRequest request, HttpServletResponse response){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		return new ModelAndView("project/viewPreviousProjectFiles");
	}

	@RequestMapping(value = "/selectProjectTitleForm",  method = {RequestMethod.GET, RequestMethod.POST})
	private ModelAndView selectProjectTitleForm(HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView modelnView = new ModelAndView("project/title/select");
		ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
		StudentExamBean student = (StudentExamBean)request.getSession().getAttribute("studentExam");
		String liveExamMonth = eDao.getLiveExamMonth();
		String liveExamYear = eDao.getLiveExamYear();
		String selectedTitle = projectSubmissionDAO.getProjectTitleForStudent(liveExamYear,liveExamMonth,student.getSapid(),"project");
		modelnView.addObject("selectedTitle", selectedTitle);
		List<ProjectTitle> titles = projectSubmissionDAO.getProjectTitleListForStudent(liveExamYear, liveExamMonth, student.getConsumerProgramStructureId(), "Project");
		modelnView.addObject("titleList", titles);
		ProjectTitle projectTitle = new ProjectTitle();
		projectTitle.setExamMonth(liveExamMonth);
		projectTitle.setExamYear(liveExamYear);
		modelnView.addObject("projectTitle", projectTitle);		
		return modelnView;
	}
		
	@RequestMapping(value = "/selectProjectTitle",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView selectProjectTitle(HttpServletRequest request, HttpServletResponse response, RedirectAttributes ra, @ModelAttribute ProjectTitle projectTitle){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		try {
			String sapid = (String) request.getSession().getAttribute("userId");
			projectTitle.setCreatedBy(sapid);
			projectTitle.setUpdatedBy(sapid);
			projectTitle.setSapid(sapid);
			projectSubmissionDAO.saveProjectTitleSelectionForStudent(projectTitle);
//			String subject = (String) request.getSession().getAttribute("projectSubject");
//			ModelAndView mv = new ModelAndView("redirect: viewProject?subject=" + subject);
//			mv.addObject("subject", subject);
			request.setAttribute("success","true");
			request.setAttribute("successMessage","Successfully title selected");
		}catch (Exception e) {
			request.setAttribute("error","true");
			request.setAttribute("errorMessage","Failed to select title. Error: " + e.getMessage());
		}
		return selectProjectTitleForm(request,response);
	}

	public ArrayList<StudentExamBean> getProjectExemptStudentListNew(String year,String month){
		//if(this.exemptStudentList == null || this.exemptStudentList.size() == 0){
			ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
			this.exemptStudentList = projectSubmissionDAO.getProjectExemptStudentListNew(year,month);
		//}
		return exemptStudentList;
	}
	private ArrayList<String> getFreeSubjectsNew(StudentExamBean student,String year,String month) {
		ArrayList<String> freeSubjects = new ArrayList<>();
		ArrayList<StudentExamBean> exemptStudentList = getProjectExemptStudentListNew(year,month);
		HashMap<String, String> exemptSAPids = new HashMap<>();
		for (int i = 0; i < exemptStudentList.size(); i++) {
			StudentExamBean bean = exemptStudentList.get(i);
			exemptSAPids.put(bean.getSapid(), bean.getSem());
		}
		if(exemptSAPids.containsKey(student.getSapid())){
			String exemptSem = exemptSAPids.get(student.getSapid());
			ArrayList<ProgramSubjectMappingExamBean> programSubjectMappingList = getProgramSubjectMappingList();
			for (int i = 0; i < programSubjectMappingList.size(); i++) {
				ProgramSubjectMappingExamBean bean = programSubjectMappingList.get(i);

				if(bean.getPrgmStructApplicable().equals(student.getPrgmStructApplicable()) && bean.getProgram().equals(student.getProgram())
						&& bean.getSem().equals(exemptSem)){
					freeSubjects.add(bean.getSubject());
				}
			}
		}
		return freeSubjects;
	}
	@RequestMapping(value = "/admin/projectPendingReportForm", method = {RequestMethod.GET})
	public String projectPendingReportForm(HttpServletRequest request, HttpServletResponse response, Model m) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		AssignmentFilesSetbean searchBean = new AssignmentFilesSetbean();
		m.addAttribute("searchBean",searchBean);
		m.addAttribute("yearList", currentYearList);
		//m.addAttribute("subjectList", getSubjectList());

		return "project/projectPendingReport";
	}
	
	@RequestMapping(value = "/admin/projectPendingReport",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView projectPendingReport(HttpServletRequest request, HttpServletResponse response,@ModelAttribute AssignmentFilesSetbean filesSet){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		List<StudentExamBean> eligiblelist = new ArrayList<StudentExamBean>();
		ModelAndView mv = new ModelAndView("PDWMProjectEligibleStudentsRequestExcelView");
		
		//getting all eligible list from ProjectSubmissionServiceImpl class
		try {
			UserAuthorizationExamBean userAuthorization = (UserAuthorizationExamBean)request.getSession().getAttribute("userAuthorization");
		eligiblelist=projectSubmissionService.getProjectPendingReport(userAuthorization, filesSet);
		}
		catch (Exception e) {
			request.setAttribute("error","true");
			request.setAttribute("errorMessage","Failed to select title. Error: " + e.getMessage());
		}
		
		if(eligiblelist == null || eligiblelist.size() == 0) {
			mv.addObject("errorFlag", "true");
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error! No records found.");
			return new ModelAndView("project/levelBasedProjectCheckList");
		}else {
			return new ModelAndView(PDWMProjectEligibleStudentsRequestExcelView, "eligiblelist", eligiblelist);
		}
	}
	
	
	public HashMap<String, ArrayList<String>> getStudentFreeSubjectsMapNew(String year,String month){
		//if(this.studentFreeSubjectsMap == null || this.studentFreeSubjectsMap.size() == 0){
			ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
			this.studentFreeSubjectsMap = projectSubmissionDAO.getStudentFreeProjectMapNew(year,month);
		//}
		return this.studentFreeSubjectsMap;
	}
	
	@RequestMapping(value = "/admin/getProjectCopyCaseReportForm", method = {RequestMethod.GET})
	public String getProjectCCReportForm(HttpServletRequest request, HttpServletResponse response, Model m) {
		ResultDomain searchBean = new ResultDomain();
		m.addAttribute("searchBean", searchBean);
		m.addAttribute("yearList", currentYearList);
		m.addAttribute("examMonthList", EXAM_MONTH_LIST);
		m.addAttribute("subjectList", projectSubjectList);
		return "project/projectCopyCaseReport";
	}
	
	
	@RequestMapping(value = "/admin/downloadProjectCopyCaseReport", method = {RequestMethod.POST})
	public ModelAndView downloadProjectCCReport(HttpServletRequest request, HttpServletResponse response, @ModelAttribute ResultDomain searchBean) throws Exception {
		ModelAndView model = new ModelAndView("project/projectCopyCaseReport");
		model.addObject("searchBean", searchBean);
		model.addObject("yearList", currentYearList);
		model.addObject("examMonthList", EXAM_MONTH_LIST);
		model.addObject("subjectList", projectSubjectList);
		ModelAndView excelView = new ModelAndView("AssignmentCopyCaseReportExcelView");
		excelView.addObject("searchBean", searchBean);
		excelView.addObject("CCLogger",projectCCLogger);
		ReadCopyCasesListBean listBean = new  ReadCopyCasesListBean();
		CopyCaseInterface CCInterface = CCFactory.getProductType(CopyCaseFactory.ProductType.PROJECT);
		
		//get all unique and above 90 students
		listBean = CCInterface.readCopyCasesList(searchBean, listBean, projectCCLogger);
		if(listBean.getErrorMessage() != null && !listBean.getErrorMessage().equals("")){
			setError(request, "Error getting Copy Cases records for Month-Year:"+searchBean.getMonth()+searchBean.getYear()+" Subject:"+searchBean.getSubject()+" Error:"+listBean.getErrorMessage());
			return model;
		}
		
		// If no records found in any threshold
		if(listBean.getUnique1CCList().size() == 0 && listBean.getAbove90CCList().size() == 0) {
			setError(request, "Error No Copy Cases records for Month-Year:"+searchBean.getMonth()+searchBean.getYear()+" Subject:"+searchBean.getSubject());
			return model;
		}
		
		// create excel report
		CCInterface.createCopyCasesList(searchBean, listBean, excelView, projectCCLogger);
		if(listBean.getErrorMessage() != null && !listBean.getErrorMessage().equals("")){
			setError(request, "Error creating Copy Cases report for Month-Year:"+searchBean.getMonth()+searchBean.getYear()+" Subject:"+searchBean.getSubject()+" Error:"+listBean.getErrorMessage());
			return model;
		}
		
		return excelView;
	}
	
	@RequestMapping(value = "/admin/projectDetailedThresholdCCForm", method = {RequestMethod.GET})
	public String getProjectDetailedThresholdCCForm(HttpServletRequest request, HttpServletResponse response, Model m) {
		ResultDomain searchBean = new ResultDomain();
		m.addAttribute("searchBean", searchBean);
		m.addAttribute("yearList", currentYearList);
		m.addAttribute("examMonthList", EXAM_MONTH_LIST);
		m.addAttribute("subjectList", projectSubjectList);
		return "project/detailedThresholdCopyCases";
	}
	
	@RequestMapping(value="/admin/searchProjectDetailedThresholdCC", method= {RequestMethod.POST,RequestMethod.GET})
	public ModelAndView getProjectDetailedThresholdCC(HttpServletRequest request, HttpServletResponse response, @ModelAttribute ResultDomain searchBean) {
		ModelAndView view = new ModelAndView("project/detailedThresholdCopyCases");
		view.addObject("yearList", currentYearList);
		view.addObject("examMonthList", EXAM_MONTH_LIST);
		view.addObject("subjectList", projectSubjectList);
		view.addObject("searchBean", searchBean);
		try {
			ReadCopyCasesListBean listBean = new  ReadCopyCasesListBean();
			CopyCaseInterface CCInterface = CCFactory.getProductType(CopyCaseFactory.ProductType.PROJECT);
			
			//get all Detailed Threshold 1 and 2 students
			CCInterface.readDetailedThresholdCCList(searchBean, listBean, projectCCLogger);
			if(listBean.getErrorMessage() != null && !listBean.getErrorMessage().equals("")){
				setError(request, "Error getting Detailed Threshold records for Month-Year:"+searchBean.getMonth()+searchBean.getYear()+" Subject:"+searchBean.getSubject()+" Error:"+listBean.getErrorMessage());
				return view;
			}
			
			//create filter report in front end
			CCInterface.createDetailedThresholdCCList(searchBean, listBean, view, projectCCLogger);
			if(listBean.getErrorMessage() != null && !listBean.getErrorMessage().equals("")){
				setError(request, listBean.getErrorMessage());
			}
		} catch (Exception e) {
			setError(request, "Error getting Detailed Threshold data for Month:"+searchBean.getMonth()+" Year:"+searchBean.getYear()+" Subject:"+searchBean.getSubject()+" Error:"+e.getMessage());
			projectCCLogger.error("Exception Error getting Detailed Threshold data Month-Year:{}{} Subject:{} Sapid:{} Error:{}",
					searchBean.getMonth(), searchBean.getYear(), searchBean.getSubject(), searchBean.getSapId1(), e);
		}
		return view;
	}
}

