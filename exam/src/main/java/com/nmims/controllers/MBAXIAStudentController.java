package com.nmims.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.AssignmentFileBean;
import com.nmims.beans.ProgramSubjectMappingExamBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentQuestionResponseExamBean;
import com.nmims.beans.StudentsTestDetailsExamBean;
import com.nmims.beans.TestExamBean;
import com.nmims.beans.TestQuestionConfigBean;
import com.nmims.beans.TestQuestionExamBean;
import com.nmims.beans.TestQuestionOptionExamBean;
import com.nmims.beans.TestTypeBean;
import com.nmims.beans.ViewTestDetailsForStudentsAPIResponse;
import com.nmims.daos.AssignmentsDAO;
import com.nmims.daos.AuditTrailsDAO;
import com.nmims.daos.ExamBookingDAO;
import com.nmims.daos.PassFailDAO;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.daos.MBAXIADAO;
import com.nmims.helpers.AESencrp;
import com.nmims.helpers.MailSender;
import com.nmims.services.IATestService;
import com.nmims.services.StudentService;

@Controller
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(value = "/mbax/ia/s")
public class MBAXIAStudentController  extends BaseController {
	

	private static final Logger logger = LoggerFactory.getLogger(MBAXIAStudentController.class);
	
	
	private static final String COPY_CASE_REMARK = "Marked For Copy Case.";
	private List<String> programList;
	private List<String> subjectList;
	private ArrayList<ProgramSubjectMappingExamBean> programSubjectMappingList = null;

	@Value( "${ENVIRONMENT}" )
	private String ENVIRONMENT;

	@Value( "${SERVER}" )
	private String SERVER;
	
	@Value( "${IA_ASSIGNMENT_FILES_PATH}" )
	private String IA_ASSIGNMENT_FILES_PATH;
	
	//private String TEST_ANSWER_BASE_PATH= "E:/TESTASSIGNMENTS_ANSWER_FILES/";
	@Value( "${SERVER_PATH}" )
	private String SERVER_PATH;
	
	@Autowired
	StudentService studentService;

	@Autowired
	private IATestService iaTestService;
	
	@Autowired
	AuditTrailsDAO auditDao;
	
	private static final int BUFFER_SIZE = 4096;


	private static final int MINS_BUFFER_TO_START_IA = 2;
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
		
		typeIdBeanMap = null;
		getTypeIdNBeanMap();
		
		return null;
	}

	public List<String> getProgramList(){
		if(this.programList == null){
			MBAXIADAO dao = (MBAXIADAO)act.getBean("mbaxIADao");
			this.programList = dao.getAllPrograms();
		}
		return programList;
	}
	
	public List<String> getSubjectList(){
		if(this.subjectList == null){
			MBAXIADAO dao = (MBAXIADAO)act.getBean("mbaxIADao");
			this.subjectList = dao.getActiveSubjects();
		}
		return subjectList;
	}
	public HashMap<Long,TestTypeBean> typeIdBeanMap = null;
	public HashMap<Long,TestTypeBean> getTypeIdNBeanMap(){
		if(this.typeIdBeanMap == null){
			MBAXIADAO dao = (MBAXIADAO)act.getBean("mbaxIADao");
			this.typeIdBeanMap = dao.getTypeIdNTypeMap();
		}
		return typeIdBeanMap;
	}
	
	

	private List<String> sapidsToBeAllowedForIA59Nov = new ArrayList<String>(Arrays.asList("77119347398","77119182418","77119214722","77119765569","77119911589","77119264473","77119875130","77119423529","77119866888","77119113958","77119745620","77119116306","77119737926","77119896167","77119576655","77119752111","77119899718","77119719269","77119763514","77119118128","77119662067","77119137800","77119608884","77119998426","77119792913","77119804801","77119194108","77119636041","77119576785","77119923128","77119411725","77119355344","77119253128","77119660771","77119715704","77119500062","77119367512","77119360908","77119562606","77119973140","77119556144","77119229885","77119956092","77119124260","77119972757","77119769623","77119283096","77119113100","77119538225","77119219256","77119101904","77119524348","77119938941","77119987621"));
	
	private List<String> sapidsIA8_ME_Didnot_Attempted_DQ = new ArrayList<String>(Arrays.asList("77119707997","77719321883","77719538978","77719773698","77719639063","77719807129","77719342106","77719819015","77719121884","77719759456","77719857838","77719477300","77719672438","77719872858","77719944455","77719834971","77719280959","77719684707","77719787806","77719853697","77719166159","77719516976","77719983669","77719359604","77719133069","77719643774","77719514855","77719778184","77719772283","77719888543","77719943867","77719386877","77719285217","77119270047","77719692239","77719590181","77719730342","77119353102","77719595652","77719866506","77719929617","77719788778","77719870130","77719846362","77719799297","77119618675","77719411268","77719255961","77719128636","77719771556","77719402427","77719359191","77719819110","77719228616","77719535047","77719105839","77719690026","77719643231","77119878415","77719259602","77719258315","77719595862","77719965918","77719995235","77719378602","77719726971","77119853726","77719557048","77719660104","77719892717","77719409310","77719815774","77719309705","77719935259","77719937566"));
	private List<String> sapidsIA8_ME_Have_Attempted_DQ = new ArrayList<String>(Arrays.asList("77719615855","77719597098","77719594418","77719570984","77719311887","77719944720","77719308951","77719602854","77719982485","77719589009","77719166679","77719602457","77719310766","77719138279","77719330382","77719977043","77719421468","77719326660","77719707687","77719745022","77719801057","77719904791","77719106461","77719721927","77719547546","77719206874","77719770355","77719168964","77719650118","77719160087","77719794802","77719466533","77719747080","77719602042","77719154081","77119378675","77719842548","77719927824","77719887784","77719168657"));
	//
	private List<String> sapidsIA8_ME_RESTOFSAPID = new ArrayList<String>(Arrays.asList("77719207671","77719512509","77719570026","77719713813","77719755732","77719764097","77719811113","77719850027","77719900041","77719911780","77719957218","77119948639","77119318457"));
	
	
	@RequestMapping(value = "/viewTestsForStudent", method =  RequestMethod.GET)
	public String viewTestsForStudent(HttpServletRequest request, HttpServletResponse response, Model m) {

		String sapId = (String)request.getSession().getAttribute("userId");
		List<TestExamBean> testsList = getTestDataForStudent(sapId);
		m.addAttribute("testsList", testsList);
		return "mbaxia/testList";
	}
/*	@RequestMapping(value = "/m/viewTestsForStudent", method =  RequestMethod.GET)
	public String m_viewTestsForStudent(HttpServletRequest request, HttpServletResponse response, Model m) {

		String sapId = (String)request.getSession().getAttribute("userId");
		List<TestBean> testsList = getTestDataForStudent(sapId);
		m.addAttribute("testsList", testsList);
		return "mbaxia/testList";
	}*/
	
//	to be deleted , api shifted to rest controller
//	@RequestMapping(value = "/m/viewTestsForStudent", method = RequestMethod.POST, consumes = "application/json", produces = "application/json; charset=UTF-8")
//	public ResponseEntity<List<TestBean>> m_viewTestsForStudent(@RequestBody StudentBean student){
//		
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json");
//		
//		List<TestBean> testsList = getTestDataForStudent(student.getSapid());
//		return new ResponseEntity<>(testsList,headers, HttpStatus.OK);	
//	}

	
	public List<TestExamBean> getTestDataForStudent(String sapId){
		MBAXIADAO dao = (MBAXIADAO)act.getBean("mbaxIADao");
		//List<String> applicableSubjects = applicableSubjectsForTest(sapId);
		List<TestExamBean> testsList = getLiveApplicableTestBySapid(sapId);
		HashMap<Long, StudentsTestDetailsExamBean>  testIdAndTestByStudentsMap = dao.getStudentsTestDetailsAndTestIdMapBySapid(sapId);
		
		for(TestExamBean test : testsList) {
			StudentsTestDetailsExamBean tempTest = testIdAndTestByStudentsMap.get(test.getId());
			
			if(tempTest !=null) {
				test.setAttempt(tempTest.getAttempt());
			}else {
				test.setAttempt(0);
			}
			
		}
		
		return testsList;
	}
	
	public List<TestExamBean> getLiveApplicableTestBySapid(String sapId){
		


		StudentMarksDAO sMarksDao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		StudentExamBean student = sMarksDao.getSingleStudentsData(sapId);
		MBAXIADAO tDao = (MBAXIADAO)act.getBean("mbaxIADao");
		
		setLiveYearMonthForTest(tDao,student);
		
		
		/*if("Offline".equalsIgnoreCase(student.getExamMode())) {
			return viewPreviousAssignments(request,response, new AssignmentFileBean());
		}*/
		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		PassFailDAO pdao = (PassFailDAO)act.getBean("passFailDAO");
		boolean isOnline = isOnline(student);

		ArrayList<String> currentSemSubjects = new ArrayList<>();
		ArrayList<String> failSubjects = new ArrayList<>();
		ArrayList<String> applicableSubjects = new ArrayList<>();
		ArrayList<String> ANSSubjects = new ArrayList<>();

		
		HashMap<String, String> subjectSemMap = new HashMap<>();
		
		StudentExamBean studentRegistrationData = tDao.getStudentRegistrationDataForTest(sapId);
	
		String currentSem = null;

		
		if(student == null){
			return new ArrayList<>();
		}else{
			// removed waived off logic in favor of common helper logic
			studentService.mgetWaivedOffSubjects(student);
		}

		if(studentRegistrationData != null){
			//Take program from Registration data and not Student data. 
			student.setProgram(studentRegistrationData.getProgram());
			student.setSem(studentRegistrationData.getSem());
			currentSem = studentRegistrationData.getSem();
			currentSemSubjects = getSubjectsForStudent(student, subjectSemMap);
		}

		ArrayList<String> passSubjectsList = getPassSubjects(student,pdao);
		if(!passSubjectsList.isEmpty() && passSubjectsList != null){
			for(String subject:passSubjectsList){
				if(currentSemSubjects.contains(subject)){
					currentSemSubjects.remove(subject);
				}
			}
		}
		
		
		failSubjects = new ArrayList<>();
		
		ArrayList<AssignmentFileBean> failSubjectsBeans = getFailSubjectsForTest(student);
		if(failSubjectsBeans != null && failSubjectsBeans.size() > 0){
		
			for (AssignmentFileBean bean : failSubjectsBeans) {
				String subject = bean.getSubject();
				String sem = bean.getSem();
				failSubjects.add(bean.getSubject());
				subjectSemMap.put(subject, sem);
				
				if("ANS".equalsIgnoreCase(bean.getAssignmentscore())){
					ANSSubjects.add(subject);
				}
			}
		}

		//}

		ArrayList<AssignmentFileBean> failANSSubjectsBeans = getANSNotProcessed(student);
		if(failANSSubjectsBeans != null && failANSSubjectsBeans.size() > 0){

			for (int i = 0; i < failANSSubjectsBeans.size(); i++) {
				String subject = failANSSubjectsBeans.get(i).getSubject();
				String sem = failANSSubjectsBeans.get(i).getSem();
				failSubjects.add(failANSSubjectsBeans.get(i).getSubject());
				subjectSemMap.put(subject, sem);
				
				ANSSubjects.add(subject);
			}
		}
		ArrayList<AssignmentFileBean> currentSemResultAwaitedSubjectsList = new ArrayList<AssignmentFileBean>();
		
		//Check if result is live for last submission cycle
		boolean isResultLiveForLastSubmissionCycle = sMarksDao.isResultLiveForLastAssignmentSubmissionCycle();
		ArrayList<String> subjectsNotAllowedToSubmit = new ArrayList<String>();
		if(!isResultLiveForLastSubmissionCycle){
			ArrayList<String> subjectsSubmittedInLastCycle = dao.getFailedSubjectsSubmittedInLastCycle(sapId, failSubjects);
			ArrayList<String> subjectsExamBookedInLastCycle = dao.getFailedSubjectsExamBookedInLastCycle(sapId, failSubjects);
			
			
			for (String subject : subjectsSubmittedInLastCycle) {
				ANSSubjects.remove(subject);
			}
			if(subjectsSubmittedInLastCycle.size() > 0 || subjectsExamBookedInLastCycle.size() > 0){
				//There are failed subjects submitted in last submission cycle 
				
					//If result is not live then subjects submitted in last cycle cannot be submitted till results are live
					subjectsNotAllowedToSubmit.addAll(subjectsSubmittedInLastCycle);
					subjectsNotAllowedToSubmit.addAll(subjectsExamBookedInLastCycle);
			}


		}
		
		
		

		for (String failedSubject : failSubjects) {
			//For ANS cases, where result is not declared, failed subject will also be present in Current sem subject.
			//Give preference to it as Failed, so that assignment can be submitted and remove  from Current list
			if(currentSemSubjects.contains(failedSubject)){
				currentSemSubjects.remove(failedSubject);
			}
		}
		
		currentSemSubjects.remove("Project");
		failSubjects.remove("Project");
		currentSemSubjects.remove("Module 4 - Project");
		failSubjects.remove("Module 4 - Project");
		applicableSubjects.addAll(currentSemSubjects);
		applicableSubjects.addAll(failSubjects);
		applicableSubjects.remove("Project");
		applicableSubjects.remove("Module 4 - Project");

		List<TestExamBean> allAapplicableTestList = new ArrayList<>();
	
			List<TestExamBean> currentSemTests = null;
			List<TestExamBean> failSubjecTests = null;
			
			if(currentSemSubjects != null && currentSemSubjects.size()>0){
				currentSemTests = tDao.getLiveTestForCurrentSemSubjectsBySubjectsAndMasterkey(currentSemSubjects, student);
			}
			if(failSubjects != null && failSubjects.size()>0){
				failSubjecTests = tDao.getLiveTestForFailedSubjecsBySubjectsAndMasterkey(failSubjects, student);
			}

			if(currentSemTests != null){
				allAapplicableTestList.addAll(currentSemTests);
			}

			if(failSubjecTests != null){

				allAapplicableTestList.addAll(failSubjecTests);
			}
			
			return allAapplicableTestList;
	}

	private ArrayList<String> getSubjectsForStudent(StudentExamBean student, HashMap<String, String> subjectSemMap) {

		ArrayList<ProgramSubjectMappingExamBean> programSubjectMappingList = getProgramSubjectMappingList();
		
		ArrayList<String> subjects = new ArrayList<>();
		for (int i = 0; i < programSubjectMappingList.size(); i++) {
			ProgramSubjectMappingExamBean bean = programSubjectMappingList.get(i);

			if(
					bean.getConsumerProgramStructureId().equalsIgnoreCase(student.getConsumerProgramStructureId())
					&& bean.getSem().equals(student.getSem())
					&& !student.getWaivedOffSubjects().contains(bean.getSubject())//Subjects has not already cleared it
					&& "Y".equalsIgnoreCase(bean.getHasIA())
					&& "Y".equalsIgnoreCase(bean.getHasTest())
					){
				subjects.add(bean.getSubject());
				subjectSemMap.put(bean.getSubject(), bean.getSem());

			}
			
			
			//Below code is for creating map of subject and sem
			if(
					bean.getConsumerProgramStructureId().equalsIgnoreCase(student.getConsumerProgramStructureId())
					){
				subjectSemMap.put(bean.getSubject(), bean.getSem());

			}
		}
		if(student.getWaivedInSubjects() != null) {
			subjects.addAll(student.getWaivedInSubjects());
			subjectSemMap.putAll(student.getWaivedInSubjectSemMapping());
		}
		
		
		return subjects;
	}
	private ArrayList<String> getPassSubjects(StudentExamBean student, PassFailDAO dao) {
		ArrayList<String> passSubjectList = dao.getPassSubjectsNamesForSingleStudent(student.getSapid());
		return passSubjectList;
	}	
	
	private ArrayList<AssignmentFileBean> getFailSubjectsForTest(StudentExamBean student) {
		MBAXIADAO dao = (MBAXIADAO)act.getBean("mbaxIADao");
		ArrayList<AssignmentFileBean> failSubjectList = dao.getFailSubjectsForAStudentApplicableForTest(student.getSapid());
		return failSubjectList;
	}

	private ArrayList<AssignmentFileBean> getANSNotProcessed(StudentExamBean student) {
		PassFailDAO dao = (PassFailDAO)act.getBean("passFailDAO");
		ArrayList<AssignmentFileBean> failSubjectList = dao.getANSNotProcessed(student.getSapid());
		return failSubjectList;
	}

	public boolean isOnline(StudentExamBean student) {
		String programStucture = student.getPrgmStructApplicable();
		boolean isOnline = false;

		if("Online".equals(student.getExamMode())){
			//New batch students and certificate program students will be considered online and with 4 attempts for assginmnet submission
			isOnline = true; 
			//NA
		}
		return isOnline;
	}
	
	@RequestMapping(value = "/viewTestDetailsForStudents", method =  RequestMethod.GET)
	public String viewTestDetailsForStudents(HttpServletRequest request,
						   HttpServletResponse response,
						   Model m,
						   @RequestParam("id") Long id,
						   @RequestParam("message") String message
						   ){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		String userId = (String) request.getSession().getAttribute("userId");
		
		
		ViewTestDetailsForStudentsAPIResponse responseBean =  getViewTestDetailsForStudentsAPIResponse(userId,id,message);
		
		m.addAttribute("assignmentPaymentPending",responseBean.getAssignmentPaymentPending());
		m.addAttribute("test", responseBean.getTest());
		m.addAttribute("studentsTestDetails", responseBean.getStudentsTestDetails());
		m.addAttribute("messageDetails", responseBean.getMessageDetails());
		m.addAttribute("attemptsDetails", responseBean.getAttemptsDetails());
		m.addAttribute("attemptNoNQuestionsMap", responseBean.getAttemptNoNQuestionsMap());
		m.addAttribute("continueAttempt", responseBean.getContinueAttempt());
		m.addAttribute("subject",responseBean.getSubject());
		m.addAttribute("paymentPendingForSecondOrHigherAttempt", responseBean.getPaymentPendingForSecondOrHigherAttempt());
		return "mbaxia/testDetailsForStudents";
	}
	
	//Page to show IA details with encrypted testId n sapid start
	@RequestMapping(value = "/iaTestDetails", method =  RequestMethod.GET)
	public String iaTestDetails(HttpServletRequest request,
						   HttpServletResponse response,
						   Model m,
						   @RequestParam("t") String testIdForUrl,
						   @RequestParam("s") String sapidForUrl
						   ){
		
		String sapId="";
		Long testId=null;

		try {
			sapId = decryptWithOutSpecialCharacters(sapidForUrl);
			testId = Long.parseLong(decryptWithOutSpecialCharacters(testIdForUrl));
		} catch (Exception e) {
			logger.info("\n"+SERVER+": "+new Date()+" IN catch() of decrypt iaTestDetails got testIdForUrl "+testIdForUrl+" sapidForUrl: "+sapidForUrl+" error : "+e.getMessage());
			sapId="";
			testId=null;
		}

		return viewTestDetailsForStudentsForAllViews(request,response,m,testId,"",sapId, 111) ;
	}
	//Page to show IA details with encrypted testId n sapid end
	
	@RequestMapping(value = "/viewTestDetailsForStudentsForAllViews", method =  RequestMethod.GET)
	public String viewTestDetailsForStudentsForAllViews(HttpServletRequest request,
						   HttpServletResponse response,
						   Model m,
						   @RequestParam("id") Long id,
						   @RequestParam("message") String message,
						   @RequestParam("userId") String userId,
						   @RequestParam(name = "consumerProgramStructureId", defaultValue = "111") Integer consumerProgramStructureId
						   ){
		
		
		logger.info("\n"+SERVER+": "+new Date()+" IN viewTestDetailsForStudents got id "+id+" userId: "+userId+" message: "+message+" consumerProgramStructureId: "+consumerProgramStructureId);
		
		ViewTestDetailsForStudentsAPIResponse responseBean =  getViewTestDetailsForStudentsAPIResponse(userId,id,message);
		
		m.addAttribute("assignmentPaymentPending",responseBean.getAssignmentPaymentPending());
		m.addAttribute("userId", userId);
		m.addAttribute("test", responseBean.getTest());
		m.addAttribute("showStartTestButton", responseBean.isShowStartTestButton());
		m.addAttribute("studentsTestDetails", responseBean.getStudentsTestDetails());
		m.addAttribute("messageDetails", responseBean.getMessageDetails());
		m.addAttribute("attemptsDetails", responseBean.getAttemptsDetails());
		m.addAttribute("attemptNoNQuestionsMap", responseBean.getAttemptNoNQuestionsMap());
		m.addAttribute("continueAttempt", responseBean.getContinueAttempt());
		m.addAttribute("subject",responseBean.getSubject());
		m.addAttribute("paymentPendingForSecondOrHigherAttempt", responseBean.getPaymentPendingForSecondOrHigherAttempt());

		try {
			m.addAttribute("sapidForUrl", encryptWithOutSpecialCharacters(userId));
			m.addAttribute("testIdForUrl", encryptWithOutSpecialCharacters(id+""));
			m.addAttribute("consumerProgramStructureIdForUrl", encryptWithOutSpecialCharacters(consumerProgramStructureId+""));
		} catch (Exception e) {
			logger.info("\n"+SERVER+": "+new Date()+" IN catch() of encrypt viewTestDetailsForStudents got id "+id+" userId: "+userId+" consumerProgramStructureId: "+consumerProgramStructureId+" error : "+e.getMessage());
			m.addAttribute("sapidForUrl", "");
			m.addAttribute("testIdForUrl", "");
			m.addAttribute("consumerProgramStructureIdForUrl", "");
		}
		
		return "mbaxia/testDetailsForStudentsForAllView";
	}
	

	@RequestMapping(value = "/testOverPage", method =  RequestMethod.GET)
	public String testOverPage(HttpServletRequest request,
						   HttpServletResponse response,
						   Model m
						   ){
		
		
		return "mbaxia/testOverPage";
	}
	
//	to be deleted, api shifted to rest controller
//	@RequestMapping(value = "/m/viewTestDetailsForStudents", method = RequestMethod.POST, consumes = "application/json", produces = "application/json; charset=UTF-8")
//	public ResponseEntity<ViewTestDetailsForStudentsAPIResponse> m_viewTestDetailsForStudents(@RequestParam("sapId") String userId,
//																	   @RequestParam("id") Long id,
//																	   @RequestParam("message") String message){
//																
//		
//		ViewTestDetailsForStudentsAPIResponse responseBean =  getViewTestDetailsForStudentsAPIResponse(userId,id,message);
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json");
//		
//		return new ResponseEntity<ViewTestDetailsForStudentsAPIResponse>(responseBean,headers, HttpStatus.OK);	
//	}
	public ViewTestDetailsForStudentsAPIResponse getViewTestDetailsForStudentsAPIResponse(String userId,Long id,String message) {
		ViewTestDetailsForStudentsAPIResponse returnBean = new ViewTestDetailsForStudentsAPIResponse();
		MBAXIADAO dao = (MBAXIADAO)act.getBean("mbaxIADao");
		logger.info("\n"+SERVER+": "+"IN getViewTestDetailsForStudentsAPIResponse got id "+id+" userId: "+userId+" message: "+message);
		
		
		TestExamBean test = dao.getTestById(id);
		
		test = updateStartEndTimeIfExtended(test,userId);

		//test.setRemainingTime(getDurationOfTestWRTCurrentTime(test.getDuration(),test.getEndDate()));
		//test.setRemainingTime(test.getDuration());
		test.setRemainingTime(getDurationTimeByIAType(test.getTestType(),test.getDuration(),test.getEndDate()));
		
		StudentsTestDetailsExamBean studentsTestDetails =  dao.getStudentsTestDetailsBySapidAndTestId(userId,test.getId());
		
		String continueAttempt = "N";
		boolean canContinueAttempt = canContinueAttempt(test,studentsTestDetails);
		if(canContinueAttempt) {
			continueAttempt= "Y";
			studentsTestDetails.setAttempt(studentsTestDetails.getAttempt()-1);
			//test.setRemainingTime(getDurationOfTestWRTTestStartedOn(test.getDuration(),studentsTestDetails.getTestStartedOn()));
			test.setRemainingTime(getRemainTimeByIAType(test.getTestType(),test.getDuration(),studentsTestDetails.getTestStartedOn(),test.getEndDate()));
		}
		
		studentsTestDetails = checkIfCanShowResultsAndAttemptedQuestions(studentsTestDetails,test);
		

		/*
		 * int score=studentsTestDetails.getScore();
		 * if("Y".equalsIgnoreCase(studentsTestDetails.getShowResult())) {
		 * if(studentsTestDetails.getScore() == 0) { //if student didnot click submit
		 * test and left test page
		 * studentsTestDetails.setScore(dao.caluclateTestScore(studentsTestDetails.
		 * getSapid(),studentsTestDetails.getTestId()));
		 * +studentsTestDetails.getScore()); score=studentsTestDetails.getScore();
		 * boolean updatedSore=dao.updateStudentsTestDetails(studentsTestDetails);
		 * +updatedSore); } } studentsTestDetails.setScore(score);
		 */

		double score=studentsTestDetails.getScore();
		/*if("Y".equalsIgnoreCase(studentsTestDetails.getShowResult())) {
			if(studentsTestDetails.getScore() == 0) { //if student didnot click submit test and left test page
				studentsTestDetails.setScore(dao.caluclateTestScore(studentsTestDetails.getSapid(),studentsTestDetails.getTestId()));
				score=studentsTestDetails.getScore();
				boolean updatedSore=dao.updateStudentsTestDetails(studentsTestDetails);
			}
		}*/
		studentsTestDetails.setScore(score);

		
		List<StudentsTestDetailsExamBean> attemptsDetails =  dao.getAttemptsDetailsBySapidNTestId(userId, test.getId());
		Map<Integer, List<TestQuestionExamBean>> attemptNoNQuestionsMap = new HashMap<>();
		
		ArrayList<TestQuestionExamBean> attemptDetail1 = new ArrayList<TestQuestionExamBean>();
		ArrayList<TestQuestionExamBean> attemptDetail2 = new ArrayList<TestQuestionExamBean>();
		ArrayList<TestQuestionExamBean> attemptDetail3 = new ArrayList<TestQuestionExamBean>();
		
		 
		//Updated : 17Feb19 by Pranit to only take attemptdetails after results are live start
		
		if("Y".equalsIgnoreCase(studentsTestDetails.getShowResult())) {
		
			 attemptNoNQuestionsMap = getAttemptNoNQuestionsMap(test.getId(),userId);
			
			for (Map.Entry<Integer, List<TestQuestionExamBean>> entry : attemptNoNQuestionsMap.entrySet()) {			
				if(entry.getKey() == 1) {
				attemptDetail1 = (ArrayList<TestQuestionExamBean>) entry.getValue();
				}
				if(entry.getKey() == 2) {
					attemptDetail2 = (ArrayList<TestQuestionExamBean>) entry.getValue();
					}
				if(entry.getKey() == 3) {
					attemptDetail3 = (ArrayList<TestQuestionExamBean>) entry.getValue();
					}
					
			}
		}
		//Updated : 17Feb19 by Pranit to only take attemptdetails after results are live start
		
		if("testTimeOut".equalsIgnoreCase(message)) {
			message = "Time Over";
		}
		if("testEnded".equalsIgnoreCase(message)) {
			message = "Test Ended";
		}
		
		
		String paymentPendingForSecondOrHigherAttempt = "";
		
		if("old".equalsIgnoreCase(test.getApplicableType())) {
			StudentMarksDAO sMarksDao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			StudentExamBean student = sMarksDao.getSingleStudentsData(userId);
			
			//Logic to check 3rd attempt of subject and enable payment if it is 3rd attempt start
			
			AssignmentsDAO aDao = (AssignmentsDAO)act.getBean("asignmentsDAO");
			boolean isOnline = new AssignmentSubmissionController().isOnline(student);
			//m.addAttribute("assignmentPaymentPending","false");
			returnBean.setAssignmentPaymentPending("false");
			if(isOnline){//Applicble for online students only
				int pastCycleAssignmentAttempts = aDao.getPastCycleAssignmentAttempts(test.getSubject(),userId);
				int pastCycleTestAttempts = dao.getPastCycleTestAttempts(test.getSubject(),userId);
				
				if((pastCycleAssignmentAttempts + pastCycleTestAttempts) >=2){
					boolean hasPaidForAssignment = aDao.checkIfAssignmentFeesPaid(test.getSubject(), userId); //check if Assignment Fee Paid for Current drive 
					if(!hasPaidForAssignment){
						//m.addAttribute("assignmentPaymentPending","true");
						returnBean.setAssignmentPaymentPending("true");
					}
				}
			}
			//Logic to check 3rd attempt of subject and enable payment if it is 3rd attempt end
			
			//code to take charge for 2nd or higher attempt start
			paymentPendingForSecondOrHigherAttempt = "N";
			if(studentsTestDetails.getAttempt()+1 <= test.getMaxAttempt() && (studentsTestDetails.getAttempt()+1) > 1) {
			
			if( (studentsTestDetails.getAttempt() > 0) && (!"Y".equalsIgnoreCase(continueAttempt)) ) {
				boolean checkIfTestFeesPaidForAttempt = dao.checkIfTestFeesPaidForAttempt(test.getSubject(), userId, test.getId(), studentsTestDetails.getAttempt()+1);
				if(!checkIfTestFeesPaidForAttempt) {
					paymentPendingForSecondOrHigherAttempt="Y";
				}
			}
			}
			//code to take charge for 2nd or higher attempt end
			
			
		}else {
			paymentPendingForSecondOrHigherAttempt = "N";
			returnBean.setAssignmentPaymentPending("false");
			
		}
				
		
		
		//Send mail after test is completed start
		try {


			if(("Time Over".equalsIgnoreCase(message) || "Test Ended".equalsIgnoreCase(message)) && "PROD".equalsIgnoreCase(ENVIRONMENT)) {
				MailSender mailSender = (MailSender)act.getBean("mailer");
				StudentMarksDAO sMarksDao = (StudentMarksDAO)act.getBean("studentMarksDAO");
				StudentExamBean student = sMarksDao.getSingleStudentsData(userId);
				


				mailSender.sendTestEndedEmail(student,test,studentsTestDetails);

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//
			//mailSender.mailStackTrace("Error in Saving Successful Transaction", e);
			logger.info("\n"+SERVER+": "+"IN Send mail error got sapid  "+studentsTestDetails.getSapid()+" testId: "+test.getId()+" Error: "+e.getMessage());
			
		}
		//Send mail after test is completed end
		
		//Check show start test button start
		boolean showStartTestButton = false;

		if( !("Y".equalsIgnoreCase(continueAttempt))) {
			//check for test between given startDate and endDate start
			try {
				String startDate = test.getStartDate();
				String endDate = test.getEndDate();


				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date sDate = sdf.parse(startDate.replaceAll("T", " "));
				Date cDate = new Date();
				Date eDateWithOutBuffer = sdf.parse(endDate.replaceAll("T", " "));
				Date eDate = addMinutesToDate(MINS_BUFFER_TO_START_IA,eDateWithOutBuffer);// adding 2mins to end datetime to give buffer for late joining.
				


				
				if(cDate.after(sDate) && cDate.before(eDate)) {


					showStartTestButton = true;
				}else {
					if(cDate.before(sDate)) {


						showStartTestButton = false;
					}
					else if(cDate.after(eDate)) {

						showStartTestButton = false;;
					}
					else {

						showStartTestButton = false;
					}
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				
				showStartTestButton = false;
			}
			//check for test between given startDate and endDate end
		}
		else {
			showStartTestButton = true;
		}
		//Check show start test button end
		
		/*
		if(showStartTestButton && "Y".equalsIgnoreCase(continueAttempt)) {
			String endDateTimeFromStudentDetails = getEndDateInStringByStartDate(test.getDuration(),studentsTestDetails.getTestStartedOn());

			String endDateTimeForCalculatingRemainingTime = "";
			boolean takeEndDateFromTestDetails = getIfTakeEndDateFromTestDetails(test.getEndDate(),endDateTimeFromStudentDetails);

			if(takeEndDateFromTestDetails) {
				endDateTimeForCalculatingRemainingTime = test.getEndDate();
			}else{
				endDateTimeForCalculatingRemainingTime = endDateTimeFromStudentDetails;
			}
			test.setRemainingTime(getDurationOfTestWRTCurrentTime(test.getDuration(), endDateTimeForCalculatingRemainingTime));
		}
		*/
		
		returnBean.setShowStartTestButton(showStartTestButton);
		
		//m.addAttribute("test", test);
		returnBean.setTest(test);
		
		//m.addAttribute("studentsTestDetails", studentsTestDetails);
		returnBean.setStudentsTestDetails(studentsTestDetails);
		
		//m.addAttribute("messageDetails", message);
		returnBean.setMessageDetails(message);
		
		//m.addAttribute("attemptsDetails", attemptsDetails);
		returnBean.setAttemptsDetails(attemptsDetails);
		
		//m.addAttribute("attemptNoNQuestionsMap", attemptNoNQuestionsMap);
		returnBean.setAttemptNoNQuestionsMap(attemptNoNQuestionsMap);
		
		//m.addAttribute("continueAttempt", continueAttempt);
		returnBean.setContinueAttempt(continueAttempt);
		
		//m.addAttribute("subject",test.getSubject());
		returnBean.setSubject(test.getSubject());
		
		//m.addAttribute("paymentPendingForSecondOrHigherAttempt", paymentPendingForSecondOrHigherAttempt);
		returnBean.setPaymentPendingForSecondOrHigherAttempt(paymentPendingForSecondOrHigherAttempt);
		
		returnBean.setAttemptDetail1(attemptDetail1);
		
		returnBean.setAttemptDetail2(attemptDetail2);
		
		returnBean.setAttemptDetail3(attemptDetail3);
		
		
		


		logger.info("\n"+SERVER+": "+new Date()+" IN getViewTestDetailsForStudentsAPIResponse got id "+id+" userId: "+userId+" returnBean: "+returnBean.toString());
		
		return returnBean;
	}

	
	private boolean getIfTakeEndDateFromTestDetails(String testEndDateTime, String endDateTimeFromStudentDetails) {
		try {
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date testEndDate = sdf.parse(testEndDateTime.replaceAll("T", " "));
			Date endDateFromStudentDetails = sdf.parse(endDateTimeFromStudentDetails.replaceAll("T", " "));
			
			
			if(testEndDate.before(endDateFromStudentDetails)) {
				return true;
			}else {
				return false;
			}
			
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			
			return true;
		}
		
	}


	private String getEndDateInStringByStartDate(Integer duration, String startDateTime) {
		try {
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date sDateTime = sdf.parse(startDateTime.replaceAll("T", " "));
			Date eDateTime = addMinutesToDate(duration,sDateTime);
			
			return sdf.format(eDateTime);
		} catch (ParseException e) {
			
			return null;
		}
		
	}

	private TestExamBean updateStartEndTimeIfExtended(TestExamBean test, String userId) {
		MBAXIADAO dao = (MBAXIADAO)act.getBean("mbaxIADao");

		TestExamBean extendedTime = dao.getExtendedTimeBySapidNUserId(userId,test.getId());
		
		if(extendedTime != null) {
			test.setStartDate(extendedTime.getExtendedStartTime());
			test.setEndDate(extendedTime.getExtendedEndTime());

		}
		
		
		return test;
	}

	private StudentsTestDetailsExamBean checkIfCanShowResultsAndAttemptedQuestions(
			StudentsTestDetailsExamBean studentsTestDetails, TestExamBean test) {
		
		
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date eDateTime = sdf.parse(test.getEndDate().replace("T", " "));
			Date cDateTime = new Date();

			if(cDateTime.after(eDateTime) && "Y".equalsIgnoreCase(studentsTestDetails.getShowResult()) ) {
				studentsTestDetails.setShowResult("Y");
			}else {
				studentsTestDetails.setShowResult("N");
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//
			logger.info("\n"+SERVER+": "+"IN checkIfCanShowResultsAndAttemptedQuestions got sapid  "+studentsTestDetails.getSapid()+" testId: "+test.getSapid()+" Error: "+e.getMessage());
			
		}
		

		return studentsTestDetails;
	}

	public boolean canContinueAttempt(TestExamBean test,StudentsTestDetailsExamBean studentsTestDetails) {
		
		//check for test attempt still open start
		try {
			if(studentsTestDetails.getId() != null) { //id is null that means it would be 1st attempt and studentsTestDetails would not be present
				if(!"Y".equalsIgnoreCase(studentsTestDetails.getTestCompleted())) {
						String startDateTime = studentsTestDetails.getTestStartedOn();
						Integer duration = test.getDuration();
						

						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						Date sDateTime = sdf.parse(startDateTime);
						Date cDateTime = new Date();
						Date eDateTime = addMinutesToDate(duration.intValue(),sDateTime);
						

						
						if(cDateTime.before(eDateTime)) {
							return true;
						}
				}else {

				}
			}else {


			}
		} catch (Exception e1) {
			logger.info("\n"+SERVER+": "+"IN canContinueAttempt got sapid  "+studentsTestDetails.getSapid()+" testId: "+test.getSapid()+" Error: "+e1.getMessage());
			
		}
		//check for test attempt still open end
		
		
		return false;
	}
	
	public Map<Integer, List<TestQuestionExamBean>> getAttemptNoNQuestionsMap(Long testId, String sapId){
		MBAXIADAO dao = (MBAXIADAO)act.getBean("mbaxIADao");
		List<StudentsTestDetailsExamBean> attemptsDetails =  dao.getAttemptsDetailsBySapidNTestId(sapId, testId);
		HashMap<Integer,List<StudentQuestionResponseExamBean>> attemptsAnswerMap = dao.getAttemptAnswersMapBySapidNTestId(sapId, testId);
		Map<Integer, List<TestQuestionExamBean>> attemptNoNQuestionsMap= new HashMap<>();
		for(StudentsTestDetailsExamBean b : attemptsDetails) {


			List<TestQuestionExamBean> qList = dao.getTestQuestionsPerAttempt(b.getTestQuestions());
			List<StudentQuestionResponseExamBean> answersByAttempt = attemptsAnswerMap.get(b.getAttempt());
			
			
			for(TestQuestionExamBean q : qList) {
				
				List<StudentQuestionResponseExamBean> answerListByQuestionId = new ArrayList<StudentQuestionResponseExamBean>(); 
				
				if(q.getType() == 1 || q.getType() == 2 || q.getType() == 5 || q.getType() == 6  || q.getType() == 7 ) {
					if(answersByAttempt !=null) {
						for(StudentQuestionResponseExamBean a : answersByAttempt) {
							
							for(TestQuestionOptionExamBean o : q.getOptionsList()) {

								if(a.getAnswer().equalsIgnoreCase(o.getId().toString())) {


									o.setSelected("Y");
									q.setIsAttempted("Y");
								}
							}							
						
							
							if(q.getId().equals(a.getQuestionId())) {								
							 answerListByQuestionId.add(a);		
								
								}
							
								
									
						}
					}
				}else if(q.getType() == 3) {
					for(TestQuestionExamBean sq : q.getSubQuestionsList()) {
						if(answersByAttempt !=null) {
							for(StudentQuestionResponseExamBean a : answersByAttempt) {
								for(TestQuestionOptionExamBean o : sq.getOptionsList()) {


									if(a.getAnswer().equalsIgnoreCase(o.getId().toString())) {


										o.setSelected("Y");
										sq.setIsAttempted("Y");
										q.setIsAttempted("Y");
									
									}
								}
							}
						}
					}
					
				}else if(q.getType() == 4) {
					if(answersByAttempt !=null) {
						for(StudentQuestionResponseExamBean a : answersByAttempt) {


							

							if((q.getId()+"").equalsIgnoreCase((a.getQuestionId()+""))) {


								q.setIsAttempted("Y");
								q.setAnswer(a.getAnswer());
								q.setMarksObtained(a.getMarks());

								if("CopyCase".equalsIgnoreCase(b.getAttemptStatus())) {
									q.setRemarks(COPY_CASE_REMARK);
								}else {
									q.setRemarks(a.getRemark());	
								}

								q.setRemarks(a.getRemark());
							}else {
								if(!"Y".equalsIgnoreCase(q.getIsAttempted())) {
									q.setIsAttempted("N");
								}
							}	
								
						}
					}
				
				
				}
				else if(q.getType() == 8) {
					if(answersByAttempt !=null) {
						for(StudentQuestionResponseExamBean a : answersByAttempt) {

							if((q.getId()+"").equalsIgnoreCase((a.getQuestionId()+""))) {


								q.setIsAttempted("Y");
								q.setAnswer(a.getAnswer());
								q.setMarksObtained(a.getMarks());
								q.setRemarks(a.getRemark());
							}else {
								q.setIsAttempted("N");
							}	
								
						}
						


					}
				
				
				}
			 
				try {
					


						 double score = dao.checkType1n2Question(q, answerListByQuestionId);
						
						if(q.getMarks() == score) {
							q.setStudentAnswerCorrect(1);
						}else {
							q.setStudentAnswerCorrect(0);
						}
						}catch(Exception e) {
							//
							logger.info("\n"+SERVER+": "+" IN getAttemptNoNQuestionsMap   got testId : "+testId+" sapId : "+sapId+", Error :  "+e.getMessage());
							
							
						}																
				
					answerListByQuestionId = new ArrayList<StudentQuestionResponseExamBean>();
					
			
			}
			attemptNoNQuestionsMap.put(b.getAttempt(), qList);
		}
		
		/*
		 * for(TestQuestionBean bean:attemptNoNQuestionsMap.get(1)) {
		 * }

		 */
		
		return attemptNoNQuestionsMap;
	}
	
	//code for assignmentGuidelines start
	@RequestMapping(value = "/assignmentGuidelines", method =  RequestMethod.GET)
	public String assignmentGuidelines(HttpServletRequest request,
						   HttpServletResponse response,
						   Model m,
						   @RequestParam("testId") Long id
						   ){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		




		m.addAttribute("testId", id);
		return "mbaxia/assignmentGuidelines";
	}
	//code for assignmentGuidelines end
	
	
	//code for assignmentGuidelinesForAllViews start
	@RequestMapping(value = "/assignmentGuidelinesForAllViews", method =  RequestMethod.GET)
	public String assignmentGuidelinesForAllViews(HttpServletRequest request,
						   HttpServletResponse response,
						   Model m,
						   @RequestParam("testIdForUrl") String testIdForUrl,
						   @RequestParam("sapidForUrl") String sapidForUrl,
						   @RequestParam(name="consumerProgramStructureIdForUrl", defaultValue = "1Q22CzXZpMm6Xg7Neccm6g==") String consumerProgramStructureIdForUrl
						   ){
		/*if(!checkSession(request, response)){
			redirectToPortalApp(response);
		}*/
		
		

		
		String userId="";
		String testId="";
		String consumerProgramStructureId="";

		try {
			userId = decryptWithOutSpecialCharacters(sapidForUrl);
			testId = decryptWithOutSpecialCharacters(testIdForUrl);
			consumerProgramStructureId = decryptWithOutSpecialCharacters(consumerProgramStructureIdForUrl);
		} catch (Exception e) {
			logger.info("\n"+SERVER+": "+new Date()+" IN catch() of decrypt assignmentGuidelinesForAllViews got testIdForUrl "+testIdForUrl+" sapidForUrl: "+sapidForUrl+" consumerProgramStructureId:"+consumerProgramStructureIdForUrl+" error : "+e.getMessage());

			userId = "";
			testId = "";
			consumerProgramStructureId = "";
		}
		



		
		m.addAttribute("sapidForUrl", sapidForUrl);
		m.addAttribute("testIdForUrl", testIdForUrl);
		m.addAttribute("consumerProgramStructureIdForUrl", consumerProgramStructureIdForUrl);
		
		m.addAttribute("testId", testId);
		m.addAttribute("userId", userId);
		m.addAttribute("consumerProgramStructureId", consumerProgramStructureId);
		return "mbaxia/assignmentGuidelinesForAllViews";
	}
	//code for assignmentGuidelines end
	
	//Code for start test start
	@RequestMapping(value = "/startStudentTest", method =  RequestMethod.GET)
	public String startStudentTest(HttpServletRequest request, 
								   HttpServletResponse response,
								   Model m,
								   @RequestParam("testId") Long testId,
								   @RequestParam(value = "sapId", required=false) final String sapId
								   ) {
		
		MBAXIADAO dao = (MBAXIADAO)act.getBean("mbaxIADao");
		StudentExamBean student = new StudentExamBean();
		if(StringUtils.isBlank(sapId)) {
			student = dao.getStudentRegistrationData(sapId);
		}else {
		 student = (StudentExamBean)request.getSession().getAttribute("studentExam");
		}
		TestExamBean test = dao.getTestById(testId);
		List<TestQuestionExamBean> testQuestions = new ArrayList<>();
		StudentsTestDetailsExamBean studentsTestDetails= new StudentsTestDetailsExamBean();
		StudentsTestDetailsExamBean studentsTestDetailsCheck =  dao.getStudentsTestDetailsBySapidAndTestId(student.getSapid(),testId);
		
		/*
		 * Below checkValidityOfTest() has following checks :
		 * 1. check for test between given startDate and endDate
		 * 2. check for test attempt still open 
		 * 3. check for no of attempts
		 */
		String validityErrorMessage = checkValidityOfTest(test,studentsTestDetailsCheck);



		if( (!StringUtils.isBlank((validityErrorMessage))) && !"ATTEMPT_STILL_OPEN".equalsIgnoreCase(validityErrorMessage) ) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage",validityErrorMessage);
			return viewTestDetailsForStudents(request, response, m, test.getId(),"openTestDetails");
		}
		String continueAttempt = "N";
		
		if("ATTEMPT_STILL_OPEN".equalsIgnoreCase(validityErrorMessage)) {
			studentsTestDetails = studentsTestDetailsCheck;


			test.setDuration(studentsTestDetails.getRemainingTime());
			testQuestions = getAttemptNoNQuestionsMap(testId, student.getSapid()).get(studentsTestDetails.getAttempt());
			continueAttempt = "Y";
		}else {
		
		try {
			testQuestions = getQuestionsAsPerTestDetails(test,sapId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in starting test, Unable to configure test questions.");
			
			return viewTestDetailsForStudents(request, response, m, test.getId(),"openTestDetails");
		}
		StringBuilder questionsForTestStringBuilder=new StringBuilder();
		int i=0;
		for(TestQuestionExamBean bean : testQuestions) {


			if(i==0) {
				questionsForTestStringBuilder.append(bean.getId().toString());
			}else {
				questionsForTestStringBuilder.append(","+bean.getId().toString());
			}
			i++;
		}
		String questionsForTest= questionsForTestStringBuilder.toString();


		studentsTestDetails.setSapid(student.getSapid());
		studentsTestDetails.setTestId(testId);
		studentsTestDetails.setActive("Y");
		studentsTestDetails.setTestCompleted("N");
		studentsTestDetails.setScore(0);
		studentsTestDetails.setRemainingTime(test.getDuration().intValue());
		studentsTestDetails.setTestQuestions(questionsForTest);
		studentsTestDetails.setCreatedBy(student.getSapid());
		studentsTestDetails.setLastModifiedBy(student.getSapid());
		if("Y".equalsIgnoreCase(test.getShowResultsToStudents())) {
			studentsTestDetails.setShowResult("Y");
		}else {
			studentsTestDetails.setShowResult("N");
		}
		
		if(studentsTestDetailsCheck.getId() == null) { //checking id here as it will be null if student is taking test for 1st time.
			studentsTestDetails.setAttempt(1);
		}else {
			studentsTestDetails.setAttempt((studentsTestDetailsCheck.getAttempt()+1));
		}
		long saved =  dao.saveStudentsTestDetails(studentsTestDetails);
		if(saved==0) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in starting test, Unable to create a test details entry.");

			return viewTestDetailsForStudents(request, response, m, test.getId(),"openTestDetails");
			}
		}
		if(testQuestions.isEmpty()) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in starting test, Unable to configure test questions.");
			return viewTestDetailsForStudents(request, response, m, test.getId(),"openTestDetails");
		}
		
		
		
		m.addAttribute("studentsTestDetails", studentsTestDetails);
		m.addAttribute("testQuestions", testQuestions);
		m.addAttribute("noOfQuestions", testQuestions!=null?testQuestions.size():0);
		m.addAttribute("test", test);
		m.addAttribute("continueAttempt", continueAttempt);
		return "mbaxia/studentTest";
	}
	//Code for start test end
	
	//Code for start test for all views start
		@RequestMapping(value = "/startStudentTestForAllViews", method =  RequestMethod.POST)
		public String startStudentTestForAllViews(HttpServletRequest request, 
									   HttpServletResponse response,
									   Model m, 
									   // @RequestParam("sapidForUrl") String sapidForUrl,
									   // @RequestParam("testIdForUrl")  String testIdForUrl
									   @ModelAttribute StudentsTestDetailsExamBean requestBean
									   ) {
			
			MBAXIADAO dao = (MBAXIADAO)act.getBean("mbaxIADao");
			String sapidForUrl = requestBean.getSapidForUrl();
			String testIdForUrl = requestBean.getTestIdForUrl();
			String consumerProgramStructureIdForUrl = requestBean.getConsumerProgramStructureIdForUrl();
			

			
			String sapId="";
			Long testId=null;
			Integer consumerProgramStructureId=null;

			try {
				sapId = decryptWithOutSpecialCharacters(sapidForUrl);
				testId = Long.parseLong(decryptWithOutSpecialCharacters(testIdForUrl));
				consumerProgramStructureId = Integer.parseInt(decryptWithOutSpecialCharacters(consumerProgramStructureIdForUrl));
			} catch (Exception e) {
				logger.info("\n"+SERVER+": "+new Date()+" IN catch() of decrypt startStudentTestForAllViews got testIdForUrl "+testIdForUrl+" sapidForUrl: "+sapidForUrl+" consumerProgramStructureIdForUrl: "+consumerProgramStructureIdForUrl+" error : "+e.getMessage());

				sapId="";
				testId=null;
				consumerProgramStructureId=111;
			}



			
			if( (StringUtils.isBlank((sapId))) || (testId==null) ) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage","Something Went Wrong, Unable to get test related data. Please refresh and try again. Contact Course Coordinator on chat.");
				return viewTestDetailsForStudentsForAllViews(request, response, m, testId,"openTestDetails",sapId, consumerProgramStructureId);
			}
			
			logger.info("\n"+SERVER+": "+new Date()+" IN startStudentTestForAllViews got testId : "+testId+" sapId : "+sapId);
			
			TestExamBean test = dao.getTestById(testId);
			test = updateStartEndTimeIfExtended(test,sapId);
			
			List<TestQuestionExamBean> testQuestions = new ArrayList<>();
			StudentsTestDetailsExamBean studentsTestDetails= new StudentsTestDetailsExamBean();
			StudentsTestDetailsExamBean studentsTestDetailsCheck =  dao.getStudentsTestDetailsBySapidAndTestId(sapId,testId);
			
			/*
			 * Below checkValidityOfTest() has following checks :
			 * 1. check for test between given startDate and endDate
			 * 2. check for test attempt still open 
			 * 3. check for no of attempts
			 */
			String validityErrorMessage = checkValidityOfTest(test,studentsTestDetailsCheck);


			if( (!StringUtils.isBlank((validityErrorMessage))) && !"ATTEMPT_STILL_OPEN".equalsIgnoreCase(validityErrorMessage) ) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage",validityErrorMessage);
				return viewTestDetailsForStudentsForAllViews(request, response, m, test.getId(),"openTestDetails",sapId,consumerProgramStructureId);
			}
			String continueAttempt = "N";
			boolean canRefreshTheTestPage=true;
			if("ATTEMPT_STILL_OPEN".equalsIgnoreCase(validityErrorMessage)) {
				studentsTestDetails = studentsTestDetailsCheck;


				//test.setDuration(studentsTestDetails.getRemainingTime());
				testQuestions = getAttemptNoNQuestionsMap(testId, sapId).get(studentsTestDetails.getAttempt());
				continueAttempt = "Y";
				
				//check if countOfRefreshPage <= noOfRefreshAllowed
				//canRefreshTheTestPage = checkIfCanRefreshTheTestPage(studentsTestDetails.getCountOfRefreshPage(),test.getNoOfRefreshAllowed());
				
				//increment countOfRefreshPage
				String incrementCountOfRefreshPage = dao.incrementCountOfRefreshPage(sapId,testId); 
				
			}else {
			
			try {
				
				canRefreshTheTestPage = true;
				 
				testQuestions = getQuestionsAsPerTestDetails(test,sapId);
				
				if(testQuestions == null) {
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "Error in starting test, Unable to configure test questions.");

					return viewTestDetailsForStudentsForAllViews(request, response, m, test.getId(),"openTestDetails",sapId, consumerProgramStructureId);
				}
				


				if(testQuestions.size() == 0) {
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "Error in starting test, Unable to configure test questions.");

					return viewTestDetailsForStudentsForAllViews(request, response, m, test.getId(),"openTestDetails",sapId, consumerProgramStructureId);
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				
				logger.info("\n"+SERVER+": "+" IN startStudentTestForAllViews > getQuestionsAsPerTestDetails  got testId : "+testId+" sapId:  "+sapId+", Error :  "+e.getMessage());
				
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Error in starting test, Unable to configure test questions.");
				
				return viewTestDetailsForStudentsForAllViews(request, response, m, test.getId(),"openTestDetails",sapId, consumerProgramStructureId);
			}
			StringBuilder questionsForTestStringBuilder=new StringBuilder();
			int i=0;
			for(TestQuestionExamBean bean : testQuestions) {


				if(i==0) {
					questionsForTestStringBuilder.append(bean.getId().toString());
				}else {
					questionsForTestStringBuilder.append(","+bean.getId().toString());
				}
				i++;
			}
			String questionsForTest= questionsForTestStringBuilder.toString();


			studentsTestDetails.setSapid(sapId);
			studentsTestDetails.setTestId(testId);
			studentsTestDetails.setActive("Y");
			studentsTestDetails.setTestCompleted("N");
			studentsTestDetails.setScore(0);
			
			//studentsTestDetails.setRemainingTime(getDurationOfTestWRTCurrentTime(test.getDuration(),test.getEndDate()));//New addition to give remaining time by endtime minus current time test.getDuration().intValue()
			//test.setDuration(studentsTestDetails.getRemainingTime());
			test.setDuration(getDurationTimeByIAType(test.getTestType(),test.getDuration(),test.getEndDate()));
			
			studentsTestDetails.setRemainingTime(test.getDuration());
			studentsTestDetails.setTestQuestions(questionsForTest);
			studentsTestDetails.setCreatedBy(sapId);
			studentsTestDetails.setLastModifiedBy(sapId);
			if("Y".equalsIgnoreCase(test.getShowResultsToStudents())) {
				studentsTestDetails.setShowResult("Y");
			}else {
				studentsTestDetails.setShowResult("N");
			}
			
			if(studentsTestDetailsCheck.getId() == null) { //checking id here as it will be null if student is taking test for 1st time.
				studentsTestDetails.setAttempt(1);
			}else {
				studentsTestDetails.setAttempt((studentsTestDetailsCheck.getAttempt()+1));
			}
			long saved =  dao.saveStudentsTestDetails(studentsTestDetails);
			if(saved==0) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Error in starting test, Unable to create a test details entry.");

				return viewTestDetailsForStudentsForAllViews(request, response, m, test.getId(),"openTestDetails",sapId, consumerProgramStructureId);
				}
				
				//for showing start datetime on test page start
				
				try {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date cDate = new Date();
					studentsTestDetails.setTestStartedOn(sdf.format(cDate));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					
				}
				
				//for showing start datetime on test page end
			}
			
			

			if("Y".equalsIgnoreCase(continueAttempt)) {
				/*
				String endDateTimeFromStudentDetails = getEndDateInStringByStartDate(test.getDuration(),studentsTestDetails.getTestStartedOn());


				String endDateTimeForCalculatingRemainingTime = "";
				boolean takeEndDateFromTestDetails = getIfTakeEndDateFromTestDetails(test.getEndDate(),endDateTimeFromStudentDetails);


				if(takeEndDateFromTestDetails) {
					endDateTimeForCalculatingRemainingTime = test.getEndDate();
				}else{
					endDateTimeForCalculatingRemainingTime = endDateTimeFromStudentDetails;
				}
				*/
				//test.setRemainingTime(getDurationOfTestWRTTestStartedOn(test.getDuration(), studentsTestDetails.getTestStartedOn()));
				
				test.setRemainingTime(getRemainTimeByIAType(test.getTestType(),test.getDuration(),studentsTestDetails.getTestStartedOn(),test.getEndDate()));
				
				studentsTestDetails.setRemainingTime(test.getRemainingTime());
				test.setDuration(test.getRemainingTime());
			}
			
			// get section wise question logic
			
			Map<String, List<TestQuestionExamBean>> sectionHashMap = iaTestService.setSectionForTestQuestion(testQuestions);
			
			m.addAttribute("userId", sapId);
			m.addAttribute("studentsTestDetails", studentsTestDetails);
			m.addAttribute("testQuestions", testQuestions);
			m.addAttribute("sectionHashMap", sectionHashMap);


			m.addAttribute("noOfQuestions", testQuestions!=null?testQuestions.size():0);
			m.addAttribute("test", test);
			m.addAttribute("continueAttempt", continueAttempt);
			m.addAttribute("serverPath", SERVER_PATH);
			m.addAttribute("ENVIRONMENT", ENVIRONMENT);

			m.addAttribute("canRefreshTheTestPage", canRefreshTheTestPage);
			
			logger.info("\n"+SERVER+": "+new Date()+" IN startStudentTestForAllViews got testId : "+testId+" sapId : "+sapId+" returnBean : "+m.asMap());
			
			return "mbaxia/studentTestPageForAllViews";
		}
		
		private boolean checkIfCanRefreshTheTestPage(Integer countOfRefreshPage, Integer noOfRefreshAllowed) {
			if(countOfRefreshPage <= noOfRefreshAllowed) {
				return true;
			}else {
				return false;
			}
		}

		private int getDurationOfTestWRTCurrentTime(Integer duration,String endDateTime) {
			
				int remainingTime;
				
				//get time left im min
				try {
					

					
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date eDateTime = sdf.parse(endDateTime.replaceAll("T", " "));
					Date cDateTime = new Date();
					
					

					
					if(cDateTime.before(eDateTime)) {
						 remainingTime = differenceInMinutesBetweenTwoDates(cDateTime, eDateTime);


						 return remainingTime > duration ? duration : remainingTime;
					}else {


						return 0;
					}
					
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					//
					logger.info("\n"+SERVER+": "+"IN getDurationOfTestWRTCurrentTime got duration  "+duration+" endDateTime: "+endDateTime+" Error: "+e.getMessage());
					return 0;
				}
			
	}

		private int getRemainTimeByIAType(String testType,Integer duration,String testStartedOn,String testEndDateTime) {
			
				
				try {
					if("Test".equalsIgnoreCase(testType)) {
						return getDurationOfTestWRTTestStartedOn(duration,testStartedOn);
					}else if("Assignment".equalsIgnoreCase(testType)) {
						return getDurationOfTestWRTCurrentTime(duration,testEndDateTime);
					}else {
						return getDurationOfTestWRTTestStartedOn(duration,testStartedOn);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					//
					logger.info("\n"+SERVER+": "+"IN getDurationOfTestWRTTestStartedOn got testType "+testType+" duration  "+duration+" endDateTime: "+testStartedOn+" Error: "+e.getMessage());
					return 0;
				}
			
	}
		

		private int getDurationTimeByIAType(String testType,Integer duration,String testEndDateTime) {
			
				
				try {
					if("Test".equalsIgnoreCase(testType)) {
						return duration;
					}else if("Assignment".equalsIgnoreCase(testType)) {
						return getDurationOfTestWRTCurrentTime(duration,testEndDateTime);
					}else {
						return duration;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					//
					logger.info("\n"+SERVER+": "+"IN getDurationTimeByIAType got testType "+testType+" duration  "+duration+" Error: "+e.getMessage());
					return 0;
				}
			
	}		
		
		private int getDurationOfTestWRTTestStartedOn(Integer duration,String testStartedOn) {
			
			int remainingTime;
			
			//get time left im min
			try {
				

				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date eDateTime = addMinutesToDate(duration,sdf.parse(testStartedOn.replaceAll("T", " ")));
				Date cDateTime = new Date();
				
				


				
				if(cDateTime.before(eDateTime)) {
					 remainingTime = differenceInMinutesBetweenTwoDates(cDateTime, eDateTime);

					 return remainingTime > duration ? duration : remainingTime;
				}else {

					return 0;
				}
				
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//
				logger.info("\n"+SERVER+": "+"IN getDurationOfTestWRTTestStartedOn got duration  "+duration+" endDateTime: "+testStartedOn+" Error: "+e.getMessage());
				return 0;
			}
		
}

		//Code for start test end

	
	public String checkValidityOfTest(TestExamBean test,StudentsTestDetailsExamBean studentsTestDetailsCheck) {
		
		if(test==null) {
			return "Test details not found.";
		}else {

			//check for test attempt still open start
				boolean canContinueAttempt = canContinueAttempt(test,studentsTestDetailsCheck);
				if(canContinueAttempt) {
					return "ATTEMPT_STILL_OPEN";
				}
			//check for test attempt still open end
			
			//check for test between given startDate and endDate start
			try {
				String startDate = test.getStartDate();
				String endDate = test.getEndDate();


				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date sDate = sdf.parse(startDate.replaceAll("T", " "));
				Date cDate = new Date();
				Date eDateWithOutBuffer = sdf.parse(endDate.replaceAll("T", " "));
				Date eDate = addMinutesToDate(MINS_BUFFER_TO_START_IA,eDateWithOutBuffer);// adding 2mins to end datetime to give buffer for late joining.
				


				
				if(cDate.after(sDate) && cDate.before(eDate)) {


				}else {
					if(cDate.before(sDate)) {


						return "Test has not started yet.";
					}
					else if(cDate.after(eDate)) {


						return "Test has ended.";
					}
					else {


						return "Error in starting Test";
					}
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//
				logger.info("\n"+SERVER+": "+" IN checkValidityOfTest > check for test between given startDate and endDate  got testId : "+test.getId()+" sapId : "+studentsTestDetailsCheck.getSapid()+", Error :  "+e.getMessage());
				
				return "Error in Starting Test. ";
			}
			//check for test between given startDate and endDate end

			
			//check for no of attempts start
			try {
				int maxAttempts = test.getMaxAttempt();
				int attempts = studentsTestDetailsCheck.getAttempt();
				
				if(attempts >= maxAttempts) {
					return "You have exhausted your attempts.";
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//
				logger.info("\n"+SERVER+": "+" IN checkValidityOfTest > check for no of attempts  got testId : "+test.getId()+" sapId : "+studentsTestDetailsCheck.getSapid()+", Error :  "+e.getMessage());
				
				return "Error in Starting Test. ";
			}
			//check for no of attempts end 
		}


		return null;
	}
	
	public List<TestQuestionExamBean> getQuestionsAsPerTestDetails(TestExamBean test, String sapid){
		MBAXIADAO dao = (MBAXIADAO)act.getBean("mbaxIADao");
		List<TestQuestionExamBean> testQuestions = dao.getTestQuestions(test.getId());
		
		//get map of questions list with key = testId+sectionId+type+marks. updated on 26Apr21 by pranit
		HashMap<String,List<TestQuestionExamBean>> typeNQuestionMap= getTypeAndTestIdSectionIdMarksKeyMapFromQuestions(testQuestions);
		
		if(testQuestions==null){
			return null;	
		}else{
			if(testQuestions.size() == 0){
				return null;	
			}
			List<TestQuestionExamBean> mandatoryQuestions = new ArrayList<>();
			List<TestQuestionExamBean> tempQuestions = new ArrayList<>();
			List<TestQuestionExamBean> questionsLeftAfterRemovingMandatory = new ArrayList<>();
			questionsLeftAfterRemovingMandatory.addAll(testQuestions);
			
			List<TestQuestionConfigBean> configs = dao.getQuestionConfigsListByTestId(test.getId());
			


			
			for(TestQuestionConfigBean b : configs) {


				int minLimit = b.getMinNoOfQuestions();
				int maxLimit = b.getMaxNoOfQuestions();
				String keyOfTestIdSectionIdTypeMarks = b.getTestId()+"-"+b.getSectionId()+"-"+b.getType()+"-"+b.getQuestionMarks();
				
				
				if(minLimit > 0) { //minLimit is set 
					if(maxLimit > 0 ) { //maxLimit is set too
						
						if(typeNQuestionMap.get(keyOfTestIdSectionIdTypeMarks) != null ) {
							tempQuestions = getQuestionsWithMinAndMaxLimitSet(typeNQuestionMap.get(keyOfTestIdSectionIdTypeMarks), minLimit, maxLimit);
							mandatoryQuestions.addAll(tempQuestions);
						}
						
					}else { //maxLimit is not set
						if(typeNQuestionMap.get(keyOfTestIdSectionIdTypeMarks) != null ) {
							tempQuestions = getQuestionsWithMinLimitSet(typeNQuestionMap.get(keyOfTestIdSectionIdTypeMarks), minLimit);
							mandatoryQuestions.addAll(tempQuestions);
							
						}
					}
				}else if( (minLimit < 1) && (maxLimit > 0) ) {
					if(typeNQuestionMap.get(keyOfTestIdSectionIdTypeMarks) != null ) {
						tempQuestions = getQuestionsWithMaxLimitSet(typeNQuestionMap.get(keyOfTestIdSectionIdTypeMarks),  maxLimit);
						mandatoryQuestions.addAll(tempQuestions);
						
					}
				}
			}
			questionsLeftAfterRemovingMandatory.removeAll(mandatoryQuestions);
			int mandatoryQuestionsSize = mandatoryQuestions.size();
			int maxQuestionsToShow = test.getMaxQuestnToShow();
			
			if(mandatoryQuestionsSize < maxQuestionsToShow ) {
				int questionToAdd = maxQuestionsToShow - mandatoryQuestionsSize;
				questionsLeftAfterRemovingMandatory = getRandomizedQuestions(questionsLeftAfterRemovingMandatory);
				mandatoryQuestions.addAll(questionsLeftAfterRemovingMandatory.subList(0, questionToAdd));
			}else if(mandatoryQuestionsSize > maxQuestionsToShow ) {

				mandatoryQuestions = mandatoryQuestions.subList(0, maxQuestionsToShow);
			} else {
			}
			
			testQuestions = mandatoryQuestions;
			

			return testQuestions;	
		}
	}


	public List<TestQuestionExamBean> getRandomizedQuestions(List<TestQuestionExamBean> testQuestions){
		int noOfQuestions = testQuestions.size();
		TestQuestionExamBean tempBean;
		List<TestQuestionExamBean> tempList=new ArrayList<>();
		for(int i=noOfQuestions; i>0; i-- ) {
			int random = new Random().nextInt(i);


			tempBean=testQuestions.get(random);
			testQuestions.remove(tempBean);
			tempList.add(tempBean);


		}
		return tempList;
	}
	
	public List<TestQuestionExamBean> getQuestionsWithMinAndMaxLimitSet(List<TestQuestionExamBean> questions, int min, int max) {
		

		
		questions = getRandomizedQuestions(questions);
		int random = new Random().nextInt(max);
		int noOfQuestionsToSelect = random;
		
		if(random > min && random < max ) {
			noOfQuestionsToSelect = random;
		}else if(random < min) {
			if(random+min >= max) {

				noOfQuestionsToSelect = max;
			}else{

				noOfQuestionsToSelect = random+min;	
			}
		}else {

			noOfQuestionsToSelect = min;
		}


		List<TestQuestionExamBean> mandatoryQtns = questions.subList(0, noOfQuestionsToSelect);
		
		return mandatoryQtns;
	}
	
	public List<TestQuestionExamBean> getQuestionsWithMaxLimitSet(List<TestQuestionExamBean> questions, int max) {



		questions = getRandomizedQuestions(questions);
		int random = new Random().nextInt(max);
		int noOfQuestionsToSelect = 1;
		if(random > 0) {
			noOfQuestionsToSelect = random;
		}


		List<TestQuestionExamBean> mandatoryQtns = questions.subList(0, noOfQuestionsToSelect);
		return mandatoryQtns;
	}
	public List<TestQuestionExamBean> getQuestionsWithMinLimitSet(List<TestQuestionExamBean> questions, int min) {


		questions = getRandomizedQuestions(questions);

		List<TestQuestionExamBean> mandatoryQtns = questions.subList(0, min);
		return mandatoryQtns;
	}
	
	public HashMap<Integer,List<TestQuestionExamBean>> getMapFromQuestions(List<TestQuestionExamBean> testQuestions){
		HashMap<Integer,List<TestQuestionExamBean>> typeNQuestionMap= new HashMap<>();
		List<TestQuestionExamBean> temp = null;
		for(TestQuestionExamBean q : testQuestions) {
			if(!typeNQuestionMap.containsKey(q.getType())) {
				temp = new ArrayList<>();
			}else {
				temp = typeNQuestionMap.get(q.getType());
			}
			temp.add(q);
			typeNQuestionMap.put(q.getType(), temp);
		
		}
		
		return typeNQuestionMap;
	}
//	//code to addStudentsQuestionResponse start
//	@RequestMapping(value = "/m/addStudentsQuestionResponse", method = RequestMethod.POST, consumes = "application/json", produces = "application/json; charset=UTF-8")
//	public ResponseEntity<HashMap<String,String>> m_addStudentsQuestionResponse(@RequestBody StudentQuestionResponseBean answer){
//		
//		logger.info("\n"+SERVER+": "+new Date()+" IN m_addStudentsQuestionResponse got sapid :  "+answer.getSapid()+" questionId : "+answer.getQuestionId()+". answer : "+answer.getAnswer());
//		
//		
//
//
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json");
//		MBAXIADAO dao = (MBAXIADAO)act.getBean("mbaxIADao");
//		HashMap<String,String> response = new HashMap<>();
//		try {
//			
//			String updateAttemptsReaminingTime = updateAttemptsReaminingTime(answer.getSapid(),answer.getTestId());
//			if(!StringUtils.isBlank(updateAttemptsReaminingTime)) {
//
//
//					
//				response.put("Status", updateAttemptsReaminingTime);
//				return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
//			}
//			
//			//List<StudentQuestionResponseBean> answers =  dao.getTestAnswerBySapidAndQuestionId(answer.getSapid(), answer.getQuestionId(), answer.getAttempt());
//			
//			int noOfAnswersAlreadySaved = dao.getCountOfAnswersBySapidAndQuestionIdNAttempt(answer.getSapid(), answer.getQuestionId(), answer.getAttempt());
//			
//			if(noOfAnswersAlreadySaved == 0) {
//				//Do insert
//	
//				if(answer.getType() == 2) {
//					String savedAns=saveType2Answers(answer);
//					if("error".equalsIgnoreCase(savedAns)) {
//						response.put("Status", "Fail in saveType2Answers ");
//						return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
//					}
//				}else {
//					long saved = dao.saveStudentsTestAnswer(answer);
//					if(saved==0) {
//						response.put("Status", "Fail in saveStudentsTestAnswer");
//						return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
//					}
//				}
//				
//				//update noOfQuestionsAttempted
//				boolean updateStudentsTestDetailsNoOfQuestionsAttempted = dao.updateStudentsTestDetailsNoOfQuestionsAttempted(answer.getTestId(),answer.getSapid(),answer.getAttempt(),answer.getQuestionId());
//				if(!updateStudentsTestDetailsNoOfQuestionsAttempted) {
//					response.put("Status", "Fail in updateStudentsTestDetailsNoOfQuestionsAttempted ");
//					return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
//				}
//			}else {
//				//Do update
//				if(answer.getType() == 2) {
//					//if type is 2 i.e. multiselect delete old answers first
//					boolean deletedAns = dao.deleteStudentsAnswersBySapidQuestionId(answer.getSapid(), answer.getQuestionId());
//					if(!deletedAns) {
//						response.put("Status", "Fail deleteStudentsAnswersBySapidQuestionId ");
//						return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
//					}
//					String savedAns = saveType2Answers(answer);
//					if("error".equalsIgnoreCase(savedAns)) {
//						response.put("Status", "Fail saveType2Answers");
//						return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
//					}
//					
//				}else if(answer.getType() == 1 || answer.getType() == 3 || answer.getType() == 4 || answer.getType() == 5 || answer.getType() == 6 || answer.getType() == 7 || answer.getType() == 8) {
//				boolean updated = dao.updateStudentsQuestionResponse(answer);
//				if(!updated) {
//					response.put("Status", "Fail updateStudentsQuestionResponse ");
//					return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
//					}
//				}else {
//					response.put("Status", "Fail type no mentioned ");
//					return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
//					
//				}
//			}
//			//update currentQuestion
//			boolean updateStudentsTestDetailsCurrentQuestion = dao.updateStudentsTestDetailsCurrentQuestion(answer.getQuestionId(),answer.getTestId(),answer.getSapid(),answer.getAttempt());
//			if(!updateStudentsTestDetailsCurrentQuestion) {
//				response.put("Status", "Fail updateStudentsTestDetailsCurrentQuestion");
//				return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
//			}
//			response.put("Status", "Success");
//			return new ResponseEntity<>(response,headers, HttpStatus.OK);
//		}catch(Exception e) {
//
//
//			StringWriter errors = new StringWriter();
//			e.printStackTrace(new PrintWriter(errors));
//			String apiCalled = "studentTestController/m/addStudentsQuestionResponse";
//			String stackTrace = "apiCalled="+ apiCalled + ",data= StudentQuestionResponseBean: "+answer.toString() +
//					",errors=" + errors.toString();
//			dao.setObjectAndCallLogError(stackTrace,answer.getSapid());
//			response.put("Status", "Fail");
//			return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//	}
//	//code to addStudentsQuestionResponse end
	
	
	//code to updateRemainingTime start
	@RequestMapping(value = "/updateRemainingTime", method = RequestMethod.POST, consumes = "application/json", produces = "application/json; charset=UTF-8")
	public ResponseEntity<HashMap<String,String>> updateRemainingTime(@RequestBody StudentQuestionResponseExamBean answer){
		

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		MBAXIADAO dao = (MBAXIADAO)act.getBean("mbaxIADao");
		HashMap<String,String> response = new HashMap<>();
		try {
		
				String updateAttemptsReaminingTime = updateAttemptsReaminingTime(answer.getSapid(),answer.getTestId());


				if(!StringUtils.isBlank(updateAttemptsReaminingTime)) {
					response.put("Status", updateAttemptsReaminingTime);
					return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
				}
		
				response.put("Status", "Success");
				return new ResponseEntity<>(response,headers, HttpStatus.OK);
			}catch(Exception e) {


				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				String apiCalled = "studentTestController/updateRemainingTime";
				String stackTrace = "apiCalled="+ apiCalled + ",data= StudentQuestionResponseBean: "+answer.toString() + 
						",errors=" + errors.toString();
				dao.setObjectAndCallLogError(stackTrace,answer.getSapid());
				response.put("Status", "Fail");
				return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
			}
	}
	//code to updateRemainingTime end
	
	
	public String updateAttemptsReaminingTime(String sapId, Long testId) {


		MBAXIADAO dao = (MBAXIADAO)act.getBean("mbaxIADao");
		StudentsTestDetailsExamBean studentsTestDetails =  dao.getStudentsTestDetailsBySapidAndTestId(sapId,testId);
		TestExamBean test = dao.getTestById(testId);
		test = updateStartEndTimeIfExtended(test,sapId);
		
		Integer duration = test.getDuration();
		int remainingTime;
		
		//get time left im min
		try {
			String startDateTime = getIAStartDateTimeByTestType(test.getTestType(),test.getStartDate(),studentsTestDetails.getTestStartedOn()); //studentsTestDetails.getTestStartedOn();
			//String endDateTime = studentsTestDetails.getTestEndedOn();
			
			//String testEndDateTimeString = test.getEndDate().replace('T', ' ');
			

			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date sDateTime = sdf.parse(startDateTime);
			//Date testEndDateTime = sdf.parse(testEndDateTimeString);
			Date cDateTime = new Date();
			Date eDateTime = addMinutesToDate(duration.intValue(),sDateTime);
			
			//adding buffer to enddate to avoid saving error
			Date eDateTimeWithBuffer = addMinutesToDate(5,eDateTime);
			//Date testEndDateTimeWithBuffer = addMinutesToDate(5,testEndDateTime);
			

			
			if(cDateTime.before(eDateTimeWithBuffer)) {
				 //remainingTime = differenceInMinutesBetweenTwoDates(cDateTime, eDateTime);


			}else {


				return "TimeOver! Your Test Was Started at "+startDateTime+". Test EndTime Was "+eDateTime+". Current Time : "+cDateTime;
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//
			logger.info("\n"+SERVER+": "+" IN updateAttemptsReaminingTime  sapId:  "+sapId+" testId : "+testId+" Error : "+e.getMessage());
			
			return "Error in calculating remining time.";
		}
		
		/*
		//update into db
		boolean updateRemainingTIme = dao.updateStudentsTestDetailsRemainingTime(remainingTime, studentsTestDetails.getId());
		if(updateRemainingTIme) {
			return "";
		}else {

			return "Error in updateing remaining time to db.";
		}
		*/
		return "";
		
	}
	
	
	private String getIAStartDateTimeByTestType(String testType, String testStartDate, String studentsTestStartedOn) {
		try {
			if("Test".equalsIgnoreCase(testType)) {
				return studentsTestStartedOn;
			}else if("Assignment".equalsIgnoreCase(testType)) {
				return testStartDate.replace('T', ' ');
			}else {
				return studentsTestStartedOn;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//
			logger.info("\n"+SERVER+": "+"IN getIAStartDateTimeByTestType got testType "+testType+" studentsTestStartedOn  "+studentsTestStartedOn+" testStartDate "+testStartDate+" Error: "+e.getMessage());
			return studentsTestStartedOn;
		}
	

	
	}

	private Integer getIADurationByTestType(String testType, Integer duration) {
	
		try {
			if("Test".equalsIgnoreCase(testType)) {
				return duration;
			}else if("Assignment".equalsIgnoreCase(testType)) {
				return 0;
			}else {
				return duration;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//
			logger.info("\n"+SERVER+": "+"IN getIADurationByTestType got testType "+testType+" duration  "+duration+" Error: "+e.getMessage());
			return duration;
		}
	
}

	private  Date addMinutesToDate(int minutes, Date beforeTime){
	    final long ONE_MINUTE_IN_MILLIS = 60000;//millisecs

	    long curTimeInMs = beforeTime.getTime();
	    Date afterAddingMins = new Date(curTimeInMs + (minutes * ONE_MINUTE_IN_MILLIS));
	    return afterAddingMins;
	}
	private int differenceInMinutesBetweenTwoDates(Date sDate, Date eDate) {


		long diff = eDate.getTime() - sDate.getTime();
		
		if(diff <= 0) {
			return 0;
		}
		
	    //long diffSeconds = diff / 1000 % 60;
	    float diffMinutes = (float)diff / (float)(60 * 1000);
	    //long diffHours = diff / (60 * 60 * 1000);
	    //int diffInDays = (int) ((dt2.getTime() - dt1.getTime()) / (1000 * 60 * 60 * 24));

	    
		return (int) Math.ceil(diffMinutes);
	}
	public String saveType2Answers(StudentQuestionResponseExamBean answer) {
		try {
			MBAXIADAO dao = (MBAXIADAO)act.getBean("mbaxIADao");
			
			String[] studentsAnswers = answer.getAnswer().split("~",-1);
			//insert all answers onebyone
			for(int i = 0; i<studentsAnswers.length;i++) {


				answer.setAnswer(studentsAnswers[i]);
				long saved = dao.saveStudentsTestAnswer(answer);
				if(saved==0) {
					return "error";
				}
			}
			return "success";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			

			return "error";
		}
	}
	
//	//code to saveStudentsTestDetails start
//	@RequestMapping(value = "/m/saveStudentsTestDetails", method = RequestMethod.POST, consumes = "application/json", produces = "application/json; charset=UTF-8")
//	public ResponseEntity<HashMap<String,String>> m_saveStudentsTestDetails(@RequestBody StudentsTestDetailsBean bean){
//		
//
//
//		logger.info("\n"+SERVER+": "+new Date()+" IN m_saveStudentsTestDetails got sapid :  "+bean.getSapid()+" testId : "+bean.getTestId());
//		
//			HttpHeaders headers = new HttpHeaders();
//			headers.add("Content-Type", "application/json");
//			MBAXIADAO dao = (MBAXIADAO)act.getBean("mbaxIADao");
//			HashMap<String,String> response = new HashMap<>();
//			try {
//				StudentsTestDetailsBean studentsTestDetails =  dao.getStudentsTestDetailsBySapidAndTestId(bean.getSapid(), bean.getTestId());
//			
//				if(studentsTestDetails.getId() != null) {
//					//Do update
//					studentsTestDetails.setAttempt(studentsTestDetails.getAttempt());
//					studentsTestDetails.setTestCompleted("Y");
//					studentsTestDetails.setLastModifiedBy(bean.getSapid());
//					studentsTestDetails.setScore(0);
//					studentsTestDetails.setTestEndedStatus(bean.getTestEndedStatus());
//					
//					/* Conmmented on 18 feb to not calculate score here
//					try {
//						studentsTestDetails.setScore(dao.caluclateTestScore(bean.getSapid(),bean.getTestId()));
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						
//						studentsTestDetails.setScore(0);
//					}
//					*/
//					
//					boolean updated = dao.updateStudentsTestDetails(studentsTestDetails);
//					if(!updated) {
//						response.put("Status", "Fail");
//						return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
//					}
//					
//
//					//Send mail after test is completed start
//					try {
//							MailSender mailSender = (MailSender)act.getBean("mailer");
//							StudentMarksDAO sMarksDao = (StudentMarksDAO)act.getBean("studentMarksDAO");
//							StudentBean student = sMarksDao.getSingleStudentsData(bean.getSapid());
//							TestBean test = dao.getTestById(bean.getTestId());
//							StudentsTestDetailsBean studentsTestDetailsForMail =  dao.getStudentsTestDetailsBySapidAndTestId(bean.getSapid(), bean.getTestId());
//							
//							mailSender.sendTestEndedEmail(student,test,studentsTestDetailsForMail);
//						
//					} catch (Exception e) {
//						//
//						//mailSender.mailStackTrace("Error in Saving Successful Transaction", e);
//						logger.info("\n"+SERVER+": "+"IN Send mail error got sapid: "+studentsTestDetails.getSapid()+" testId: "+bean.getId()+" Error: "+e.getMessage());
//						
//					}
//					//Send mail after test is completed end
//					
//					
//				}else {			
//					response.put("Status", "Fail");
//					return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
//				}
//				response.put("Status", "Success");
//				
//		
//				return new ResponseEntity<>(response,headers, HttpStatus.OK);
//			}catch(Exception e) {
//
//
//				StringWriter errors = new StringWriter();
//				e.printStackTrace(new PrintWriter(errors));
//				String apiCalled = "studentTestController/m/saveStudentsTestDetails";
//				String stackTrace = "apiCalled="+ apiCalled + ",data= testId: "+ bean.getTestId()+ 
//						",errors=" + errors.toString();
//				dao.setObjectAndCallLogError(stackTrace,bean.getSapid());
//				response.put("Status", "Fail");
//				return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
//			}
//		
//	}
//	//code to saveStudentsTestDetails end

	
	//Get Applicable subjects start
	public ArrayList<String> applicableSubjectsForTest(String sapId) {
		
		MBAXIADAO dao = (MBAXIADAO)act.getBean("mbaxIADao");
		
		
		ArrayList<String> applicableSubjects=new ArrayList<>();
		List<String> failSubjectsBeans = new ArrayList<>();
		ArrayList<ProgramSubjectMappingExamBean> allsubjects = new ArrayList<>();
		
		AssignmentsDAO adao = (AssignmentsDAO)act.getBean("assignmentsDAO");
		
		StudentExamBean studentRegistrationData = dao.getStudentRegistrationData(sapId);

		ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
		StudentExamBean student = eDao.getSingleStudentsData(sapId);

		if(studentRegistrationData == null){
			//To implemented later by PS 
			//Get fail subjects content if studnet does not have registration for current sem.
			failSubjectsBeans = getFailSubjects(student);

			if(failSubjectsBeans != null && failSubjectsBeans.size() > 0){
				applicableSubjects.addAll(failSubjectsBeans);
			}

			//To implemented later by PS 
			
		}else{
			//Take program from Registration data and not Student data. 


			student.setProgram(studentRegistrationData.getProgram());
			student.setSem(studentRegistrationData.getSem());
			//student.setPrgmStructApplicable(studentRegistrationData.getPrgmStructApplicable());
			//student.setWaivedOffSubjects(studentRegistrationData.getWaivedOffSubjects());
			ArrayList<ProgramSubjectMappingExamBean> currentSemSubjects = getSubjectsForStudent(student);
			if(currentSemSubjects != null && currentSemSubjects.size() > 0){
				allsubjects.addAll(currentSemSubjects);
			}



			
			//If current sem is 1, then there will be no failed subjects. Get failed subjects only when he is in higher semesters
			if(!"1".equals(studentRegistrationData.getSem())){
				failSubjectsBeans = getFailSubjects(student);

				if(failSubjectsBeans != null && failSubjectsBeans.size() > 0){
					applicableSubjects.addAll(failSubjectsBeans);
				}
			}



		}
		
		/* to be implemented later By PS 6th Jun
		 * //Get subjects never attempted or results not declared
		unAttemptedSubjectsBeans = adao.getUnAttemptedSubjects(sapId);
		if(unAttemptedSubjectsBeans != null && unAttemptedSubjectsBeans.size() > 0){
			allsubjects.addAll(unAttemptedSubjectsBeans);
		}*/

		
		for(ProgramSubjectMappingExamBean psmb:allsubjects){
			applicableSubjects.add(psmb.getSubject());
		}
		return applicableSubjects;
				
	}
	
	private void setLiveYearMonthForTest(MBAXIADAO dao,StudentExamBean student) {
	
		
		try {
			//Check for hasTest start
			boolean hasTest = dao.checkHasTest(student.getConsumerProgramStructureId());
			if(hasTest) {
				TestExamBean testLiveSettingRegular = dao.getCurrentLiveTestConfigByMasterKeyAndLivetype(student.getConsumerProgramStructureId(), "Regular");
				if(testLiveSettingRegular != null) {
					dao.setLiveRegularTestYear(testLiveSettingRegular.getAcadsYear());
					dao.setLiveRegularTestMonth(testLiveSettingRegular.getAcadsMonth());
				}
				
				TestExamBean testLiveSettingResit = dao.getCurrentLiveTestConfigByMasterKeyAndLivetype(student.getConsumerProgramStructureId(), "Regular");
				if(testLiveSettingResit != null) {
					dao.setLiveResitTestYear(testLiveSettingResit.getAcadsYear());
					dao.setLiveResitTestMonth(testLiveSettingResit.getAcadsMonth());
				}		
			}
			//Check for hasTest end
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
		}
		
	}
	
	private List<String> getFailSubjects(StudentExamBean student) {
		MBAXIADAO dao = (MBAXIADAO)act.getBean("mbaxIADao");
		List<String> tempfailSubjectList =new ArrayList<String>();
		List<String> failSubjectList =new ArrayList<String>();
		
		try {
			tempfailSubjectList = dao.getFailSubjectsForAStudent(student.getSapid());

			failSubjectList = getSubjectsForStudentFromList(student,tempfailSubjectList);

		} catch (Exception e) {
			
		}
		
		return failSubjectList;
	}
	private ArrayList<ProgramSubjectMappingExamBean> getSubjectsForStudent(StudentExamBean student) {
		ArrayList<ProgramSubjectMappingExamBean> programSubjectMappingList = getProgramSubjectMappingList();
		ArrayList<ProgramSubjectMappingExamBean> subjects = new ArrayList<>();
		for (int i = 0; i < programSubjectMappingList.size(); i++) {
			ProgramSubjectMappingExamBean bean = programSubjectMappingList.get(i);

			if(
					bean.getConsumerProgramStructureId().equalsIgnoreCase(student.getConsumerProgramStructureId())
					&& bean.getSem().equals(student.getSem())
					&& !student.getWaivedOffSubjects().contains(bean.getSubject())//Subjects has not already cleared it
					&& "Y".equals(bean.getHasTest())
					&& "Y".equals(bean.getHasIA())
				){
				subjects.add(bean);

			}
		}
		return subjects;
	}
	
	private List<String> getSubjectsForStudentFromList(StudentExamBean student, List<String> listOfSubjects) {
		ArrayList<ProgramSubjectMappingExamBean> programSubjectMappingList = getProgramSubjectMappingList();
		List<String> subjects = new ArrayList<>();
		for(String s : listOfSubjects) {
			for (int i = 0; i < programSubjectMappingList.size(); i++) {
				ProgramSubjectMappingExamBean bean = programSubjectMappingList.get(i);
	
				if(
					bean.getConsumerProgramStructureId().equalsIgnoreCase(student.getConsumerProgramStructureId())
					&& !student.getWaivedOffSubjects().contains(bean.getSubject())//Subjects has not already cleared it
					&& "Y".equals(bean.getHasTest())
					&& "Y".equals(bean.getHasIA())
					&& s.equalsIgnoreCase(bean.getSubject())
				){
					subjects.add(bean.getSubject());
					break;
				}
			}
		}
		return subjects;
	}
	
	public ArrayList<ProgramSubjectMappingExamBean> getProgramSubjectMappingList(){
		if(this.programSubjectMappingList == null || this.programSubjectMappingList.size() == 0){

			MBAXIADAO dao = (MBAXIADAO)act.getBean("mbaxIADao");
			this.programSubjectMappingList = dao.getProgramSubjectMappingList();
		}
		return programSubjectMappingList;
	} 


	//Get Applicable subjects end

	//code to getSubjectsApplicableForTestFromExamApp start
	@RequestMapping(value = "/getSubjectsApplicableForTestFromExamApp", method = RequestMethod.GET, consumes = "application/json", produces = "application/json; charset=UTF-8")
	public ResponseEntity<HashMap<String,List<String>>> getSubjectsApplicableForTestFromExamApp(@RequestParam("sapId") String sapId){
		

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		HashMap<String,List<String>> response = new HashMap<>();
		
		try {



			List<String> applicableSubjects = applicableSubjectsForTest(sapId);
			response.put("subjectsForTest", applicableSubjects);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
			return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(response,headers, HttpStatus.OK);
		
	}
	//code to getSubjectsApplicableForTestFromExamApp end
	
	//code to getTestsForStudentFromExamApp start
	@RequestMapping(value = "/getTestsForStudentFromExamApp", method = RequestMethod.GET, consumes = "application/json", produces = "application/json; charset=UTF-8")
	public ResponseEntity<HashMap<String,List<TestExamBean>>> getTestsForStudentFromExamApp(@RequestParam("sapId") String sapId){
		


		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		HashMap<String,List<TestExamBean>> response = new HashMap<>();
		
		try {


			List<TestExamBean> testsForStudent = getTestDataForStudent(sapId);
			response.put("testsForStudent", testsForStudent);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
			return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(response,headers, HttpStatus.OK);
		
	}
	//code to getTestsForStudentFromExamApp end
	
	
	//getApplicableTestsByModuleid start

	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@RequestMapping(value = "/getIABySapIdNModuleId", method = RequestMethod.POST, consumes = "application/json", produces = "application/json; charset=UTF-8")
	public ResponseEntity<HashMap<String,List<TestExamBean>>> getIABySapIdNModuleId(@RequestBody TestExamBean bean){
		


		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		HashMap<String,List<TestExamBean>> response = new HashMap<>();
		
		try {

			List<TestExamBean> testsForStudent = getTestsBySapIdNModuleId(bean.getUserId(),bean.getReferenceId());
			response.put("testsForStudent", testsForStudent);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
			return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(response,headers, HttpStatus.OK);
		
	}
	//getApplicableTestsByModuleId end
	
	public List<TestExamBean> getTestsBySapIdNModuleId(String sapId, Integer referenceId){
		MBAXIADAO dao = (MBAXIADAO)act.getBean("mbaxIADao");
		//List<String> applicableSubjects = applicableSubjectsForTest(sapId);
		//List<TestBean> testsList = getLiveApplicableTestBySapid(sapId);
		List<TestExamBean> testsList = dao.getTestsBySapIdNModuleId(sapId,referenceId);
		
		HashMap<Long, StudentsTestDetailsExamBean>  testIdAndTestByStudentsMap = dao.getStudentsTestDetailsAndTestIdMapBySapid(sapId);
		
		List<TestExamBean> returnTestsList = new ArrayList<TestExamBean>();
		for(TestExamBean test : testsList) {
			StudentsTestDetailsExamBean tempTest = testIdAndTestByStudentsMap.get(test.getId());
			TestExamBean temp = new TestExamBean();
			if(tempTest !=null) {
				test.setAttempt(tempTest.getAttempt());
			}else {
				test.setAttempt(0);
			}
		
			temp = updateStartEndTimeIfExtended(test,sapId);
			returnTestsList.add(temp);
			
		}
		
		return returnTestsList;
	}
	
	//code to deleteAnswerBySapIdQuestionId start
	@RequestMapping(value = "/deleteAnswerBySapIdQuestionId", method = RequestMethod.POST, consumes = "application/json", produces = "application/json; charset=UTF-8")
	public ResponseEntity<HashMap<String,String>> deleteAnswerBySapIdQuestionId(@RequestBody StudentQuestionResponseExamBean answer){
		

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		MBAXIADAO dao = (MBAXIADAO)act.getBean("mbaxIADao");
		HashMap<String,String> response = new HashMap<>();
		try {
				boolean deletedAns = dao.deleteStudentsAnswersBySapidQuestionId(answer.getSapid(), answer.getQuestionId());
				if(!deletedAns) {
				
					response.put("Status", "Fail");
					return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
				}
				boolean updatedAns = dao.updateNoOfQuestionAttemptedBySapidTestId(answer.getSapid(), answer.getTestId(),answer.getAttempt());
				if(!updatedAns) {
				
					response.put("Status", "Fail");
					return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
				}
		
				response.put("Status", "Success");
				return new ResponseEntity<>(response,headers, HttpStatus.OK);
			}catch(Exception e) {

				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				String apiCalled = "studentTestController/deleteAnswerBySapIdQuestionId";
				String stackTrace = "apiCalled="+ apiCalled +  ",data= StudentQuestionResponseBean: "+ answer.toString()  +
						",errors=" + errors.toString();
				dao.setObjectAndCallLogError(stackTrace,answer.getSapid());
				response.put("Status", "Fail");
				return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
			}
	}
	//code to deleteAnswerBySapIdQuestionId end
	
/*
 * Moved to MBAWX Exam Results Controller by Ashutosh on 22-11-2019
 * This logic is now redundant and was used to fetch old IA, TEE marks data for MBAWX
	@RequestMapping(value = "/api/getIAResultsForSubjectsBySapid", method = {RequestMethod.POST})
	public ResponseEntity<GetIAResultsBySapidCollectionBean> getIAResultsForSubjectsBySapid(@RequestBody TestBean test) {
		


		GetIAResultsBySapidCollectionBean responseBean = getIAResultsBySapidCollectionBean(test.getSapid());
		
		return new ResponseEntity<GetIAResultsBySapidCollectionBean>(responseBean,HttpStatus.OK);
	}
	//getIAResultsForSubjectsBySapid end

	//getAllIAForSubjectBySapid start
	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@RequestMapping(value = "/api/getAllAttemptedIAForSubject", method = {RequestMethod.POST})
	public ResponseEntity<String> getAllAttemptedIAForSubject(@RequestBody StudentsTestDetailsBean test) {
		
		Map<String, Object> response = new HashMap<String, Object>();

		MBAXIADAO dao = (MBAXIADAO)act.getBean("mbaxIADao");

		try {
			List<StudentsTestDetailsBean> attemptedTestsBySapidNSubject = new ArrayList<StudentsTestDetailsBean>();
			attemptedTestsBySapidNSubject = dao.getApplicableFinishedTestsWithAttemptDetailsBySapidNSubject(test.getId(), test.getSapid());
			getSubjectsForBestOf7(attemptedTestsBySapidNSubject);

			response.put("tests", attemptedTestsBySapidNSubject);
			response.put("status", "success");
		}catch (Exception e) {
			// TODO: handle exception
			
			response.put("status", "failure");
			response.put("tests", new ArrayList<StudentsTestDetailsBean>());
		}
		return new ResponseEntity<String>(new Gson().toJson(response),HttpStatus.OK);
	}
	//getAllIAForSubjectBySapid end
	
	private GetIAResultsBySapidCollectionBean getIAResultsBySapidCollectionBean(String sapid) {
		MBAXIADAO dao = (MBAXIADAO) act.getBean("mbaxIADao");
		ExamsAssessmentsDAO examsAssessmentsDAO = (ExamsAssessmentsDAO) act.getBean("examsAssessmentsDAO");
		GetIAResultsBySapidCollectionBean responseBean = new GetIAResultsBySapidCollectionBean();
		List<GetIAResultsBySapidResponseBean> subjects = new ArrayList<>();

		// 1. get all subjects applicable to student with program config details.
		List<TestBean> subjectDetails = dao.getSubjectsForIAResultsBySapid(sapid);



		// 2. get all test for the subjects alongwith students attempt details.
		if (subjectDetails.size() > 0) {
			for (TestBean subject : subjectDetails) {

				GetIAResultsBySapidResponseBean tempResponseBean = new GetIAResultsBySapidResponseBean();

				MBAWXPassFailStatus attemptedTestDetails = dao.attemptedTestsBySapidNTimeboundId(sapid, subject.getId());
				if(attemptedTestDetails != null) {
					if(!StringUtils.isBlank(attemptedTestDetails.getStatus())) {
						String status = attemptedTestDetails.getStatus();
						if(!"Attempted".equalsIgnoreCase(status)) {
							attemptedTestDetails.setTeeScore(status);
						}
					}

					if(!StringUtils.isBlank(attemptedTestDetails.getOldStatus())) {
						String status = attemptedTestDetails.getOldStatus();
						if(!"Attempted".equalsIgnoreCase(status)) {
							attemptedTestDetails.setOldScore(status);
						}
					}
					
					if(!StringUtils.isBlank(attemptedTestDetails.getMax_score())) {
						String maxScore = attemptedTestDetails.getMax_score();
						if(!"100".equalsIgnoreCase(maxScore)) {
							attemptedTestDetails.setOldMaxScore(null);
							attemptedTestDetails.setOldScore(null);
						}
					}
					setTotalScore(attemptedTestDetails);
				}
				
				List<StudentsTestDetailsBean> attemptedTestsBySapidNSubject = dao.getApplicableFinishedTestsWithAttemptDetailsBySapidNSubject(subject.getId(), sapid);
				subject = setAggregateScoreForSubject(subject, attemptedTestsBySapidNSubject);
				
				// 3. get TEE Marks & ResultLive Flag For subject
				
					// TODO : Comment this bit after mobile results update is live --- start
						String TEEScore = examsAssessmentsDAO.getTEEScore(sapid,subject.getId());
						subject.settEEScore(TEEScore);
						
						String isResultLive = examsAssessmentsDAO.isResultLiveFlag(subject.getId());
						subject.setIsResultLive(isResultLive);
					// ------- end
				
				tempResponseBean.setSubjectDetails(subject);
				tempResponseBean.setAttemptedTestsList(attemptedTestsBySapidNSubject);
				
				tempResponseBean.setAttemptedTestDetails(attemptedTestDetails);
				subjects.add(tempResponseBean);

			}
		}

		responseBean.setSubjects(subjects);

		return responseBean;
	}
	private void setTotalScore(MBAWXPassFailStatus attemptedTestDetails) {
		if (attemptedTestDetails != null) {
			int teeScore = parseIfNumericScore(attemptedTestDetails.getTeeScore());
			int iaScore = parseIfNumericScore(attemptedTestDetails.getIaScore());
			int total = teeScore + iaScore;
			attemptedTestDetails.setTotal(Integer.toString(total));
		}
	}

	private int parseIfNumericScore(String score) {
		if (!StringUtils.isBlank(score) && StringUtils.isNumeric(score)) {
			return Integer.parseInt(score);
		}
		return 0;
	}
	private void getSubjectsForBestOf7(List<StudentsTestDetailsBean> attemptedTestsBySapidNSubject) {
		if (attemptedTestsBySapidNSubject != null && attemptedTestsBySapidNSubject.size() > 0) {
			Map<Long, StudentsTestDetailsBean> selectedForBestOf7Calculation = new HashMap<>();

			List<StudentsTestDetailsBean> selectedAttempts = attemptedTestsBySapidNSubject;

			if (attemptedTestsBySapidNSubject.size() > 7) {
				// Sort results in descending order by score
				List<StudentsTestDetailsBean> descScoreSortedList = sortTestResultsByScoreDesc(attemptedTestsBySapidNSubject);

				// Get the 7 subjects with the highest score
				selectedAttempts = descScoreSortedList.subList(0, 7);
			}


			for (StudentsTestDetailsBean b : selectedAttempts) {
				selectedForBestOf7Calculation.put(b.getId(), b);
			}




			for (StudentsTestDetailsBean a : attemptedTestsBySapidNSubject) {
				if (selectedForBestOf7Calculation.containsKey(a.getId())) {
					a.setScoreSelectedForBestOf7(true);
				}
			}
		}
	}
	private TestBean setAggregateScoreForSubject(TestBean subject, List<StudentsTestDetailsBean> attemptedTestsBySapidNSubject) {
		
		getSubjectsForBestOf7(attemptedTestsBySapidNSubject);
		
		int score = 0;
		int numberOfSelectedAttempts = 0;
		for (StudentsTestDetailsBean b : attemptedTestsBySapidNSubject) {
			// Calculate total score and add subject to selected subjects array
			if(b.isScoreSelectedForBestOf7()) {
				score = score + b.getScore();
				numberOfSelectedAttempts++;
			}
		}
		subject.setScore(Long.parseLong(score + ""));
		subject.setMaxScore(numberOfSelectedAttempts * 10);
		return subject;
	}
*/
	
//	private TestBean setAggregateScoreForSubject(TestBean subject, List<StudentsTestDetailsBean> attemptedTestsBySapidNSubject) {
//	
////		int totalFor7OrBelowTests = 0;
////		int scoreFor7OrBelowTests = 0;
//
//			for(StudentsTestDetailsBean test : attemptedTestsBySapidNSubject) {
//				test =  checkIfCanShowResultsAndAttemptedQuestions(test,subject);
//				if("Y".equalsIgnoreCase(test.getShowResultsToStudents())) {
//					
//					scoreFor7OrBelowTests = scoreFor7OrBelowTests + test.getScore();
//					totalFor7OrBelowTests = totalFor7OrBelowTests + test.getMaxScore();
//					



//				}else {
//					test.setScore(0);
//				}
//			}
//			
//			subject.setScore(Long.parseLong(scoreFor7OrBelowTests+""));
//			subject.setMaxScore(totalFor7OrBelowTests);


//			
//		}else if(noOfTestsForSubject > 8 && noOfTestsForSubject > 0) {
//			subject = calculateBestOf7Scores(subject, attemptedTestsBySapidNSubject);
//		} 
//		return subject;
//
////		if(noOfTestsForSubject < 8 && noOfTestsForSubject > 0) {
////
////			for(StudentsTestDetailsBean test : attemptedTestsBySapidNSubject) {
////				test =  checkIfCanShowResultsAndAttemptedQuestions(test,subject);
////				if("Y".equalsIgnoreCase(test.getShowResultsToStudents())) {
////					
////					scoreFor7OrBelowTests = scoreFor7OrBelowTests + test.getScore();
////					totalFor7OrBelowTests = totalFor7OrBelowTests + test.getMaxScore();
////					


////				}else {
////					test.setScore(0);
////				}
////			}
////			
////			subject.setScore(Long.parseLong(scoreFor7OrBelowTests+""));
////			subject.setMaxScore(totalFor7OrBelowTests);


////			
////		}else if(noOfTestsForSubject > 8 && noOfTestsForSubject > 0) {
////			subject = calculateBestOf7Scores(subject, attemptedTestsBySapidNSubject);
////		} 
//
////		return subject;
//	}

	private List<StudentsTestDetailsExamBean> sortTestResultsByScoreDesc(List<StudentsTestDetailsExamBean> attempts) {

		List<StudentsTestDetailsExamBean> descScoreSortedList = new LinkedList<>();
		descScoreSortedList.addAll(attempts);

		Comparator<StudentsTestDetailsExamBean> compareByScore = new Comparator<StudentsTestDetailsExamBean>() {
			@Override
			public int compare(StudentsTestDetailsExamBean o1, StudentsTestDetailsExamBean o2) {
				return o1.getScoreInInteger().compareTo(o2.getScoreInInteger());
			}
		};

		// Sort all subjects in desc order of score
		Collections.sort(descScoreSortedList, compareByScore.reversed());

		return descScoreSortedList;
	}

//	Commented out by Ashutosh.

	/*
	 * private TestBean calculateBestOf7Scores(TestBean subject,
	 * List<StudentsTestDetailsBean> attemptedTestsBySapidNSubject) {
	 * 
	 * Map<Long,StudentsTestDetailsBean> selectedForBestOf7Calculation = new
	 * HashMap<>(); List<StudentsTestDetailsBean> descScoreSortedList = new
	 * LinkedList<>(); descScoreSortedList.addAll(attemptedTestsBySapidNSubject);
	 * 
	 * for(StudentsTestDetailsBean test : attemptedTestsBySapidNSubject) { test =
	 * checkIfCanShowResultsAndAttemptedQuestions(test,subject);
	 * if("Y".equalsIgnoreCase(test.getShowResultsToStudents())) {
	 * 
	 * }else { test.setScore(0); } }
	 * 
	 * //To be conmmted later by PS


	 * for(StudentsTestDetailsBean attempt : descScoreSortedList) {


	 * getScore()); } //To be conmmted later by PS
	 * 
	 * Comparator<StudentsTestDetailsBean> compareByScore = new
	 * Comparator<StudentsTestDetailsBean>() {
	 * 
	 * @Override public int compare(StudentsTestDetailsBean o1,
	 * StudentsTestDetailsBean o2) { return
	 * o1.getScoreInInteger().compareTo(o2.getScoreInInteger()); } };
	 * 
	 * 
	 * Collections.sort(descScoreSortedList, compareByScore.reversed());
	 * 
	 * 
	 * //To be conmmted later by PS



	 * for(StudentsTestDetailsBean attempt : descScoreSortedList) {


	 * getScore()); } //To be conmmted later by PS
	 * 
	 * if(descScoreSortedList.size() > 7) { List<StudentsTestDetailsBean>
	 * best7Attempts = descScoreSortedList.subList(0, 7); int score = 0;
	 * for(StudentsTestDetailsBean b : best7Attempts) { score = score +
	 * b.getScore(); selectedForBestOf7Calculation.put(b.getId(), b); }
	 * 


	 * subject.setScore(Long.parseLong(score+"")); subject.setMaxScore(70); }
	 * 
	 * for(StudentsTestDetailsBean a : attemptedTestsBySapidNSubject) {
	 * if(selectedForBestOf7Calculation.containsKey(a.getId())) {
	 * a.setScoreSelectedForBestOf7(true); } }
	 * 
	 * 
	 * 
	 * for(StudentsTestDetailsBean attempt : attemptedTestsBySapidNSubject) {
	 * if(descScoreSortedList.size() == 0) { descScoreSortedList.add(attempt);
	 * tempList.add(attempt); currentLowest = attempt; }else
	 * if(descScoreSortedList.size() > 0) { tempList = new LinkedList<>();
	 * 
	 * for(StudentsTestDetailsBean bean : descScoreSortedList ) {
	 * if(currentLowest.getScore() < attempt.getScore() ) {
	 * 
	 * tempList.add(attempt); tempList.add(bean); currentLowest = bean; }else {
	 * descScoreSortedList.add(attempt); tempList.add(attempt);
	 * 
	 * } } }
	 * 
	 * 
	 * descScoreSortedList = new LinkedList<>();
	 * descScoreSortedList.addAll(tempList); }
	 * 
	 * 
	 * return subject; }
	 */
//	@RequestMapping(value = "/m/getTestDataForTODO", method = RequestMethod.POST, consumes = "application/json", produces = "application/json; charset=UTF-8")
//	public ResponseEntity<List<TestBean>> getTestDataForTODO(@RequestBody TestBean testBean){
//		
//
//
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json");
//		
//		List<TestBean> testsList = getTestDataForStudentTODO(testBean.getId(),testBean.getSapid());
//		return new ResponseEntity<>(testsList,headers, HttpStatus.OK);	
//	}
	
	public List<TestExamBean> getTestDataForStudentTODO(Long id, String sapId){
		MBAXIADAO dao = (MBAXIADAO)act.getBean("mbaxIADao");
		// get all test for the subjects alongwith students attempt details.
						
		//List<TestBean> attemptedTestsBySapidNSubject = dao.getApplicableTestsWithAttemptDetailsBySapidNSubject_todo(id,sapId);
		List<TestExamBean> attemptedTestsBySapidNSubject = getPendingTestDataForStudentTODO(id,sapId);
						
				


		
		return attemptedTestsBySapidNSubject;
		
	}
	


//@RequestMapping(value = "/m/getIABySapIdNTimeBoundIds", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
//public ResponseEntity<HashMap<String,List<TestBean>>> getIABySapIdNTimeBoundIds(@RequestBody TestBean bean){
//	HttpHeaders headers = new HttpHeaders();
//	headers.add("Content-Type", "application/json");
//	HashMap<String,List<TestBean>> response = new HashMap<>();
//	
//	try {
//
//		List<TestBean> testsForStudent = iaTestService.getAllLiveTestsBySapId(bean.getUserId());
//		response.put("testsForStudent", testsForStudent);
//		return new ResponseEntity<>(response,headers, HttpStatus.OK);
//	} catch (Exception e) {
//		// TODO Auto-generated catch block
//		
//		return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
//	}
//	
//}
////getIABySapIdNTimeBoundIds end

	
//	@RequestMapping(value = "/m/getDueTestDataForTODO", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
//	public ResponseEntity<List<TestBean>> getDueTestDataForTODO(@RequestBody TestBean testBean){
//		
//
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json");
//		
//		List<TestBean> testsList = getDueTestDataForStudentTODO(testBean.getId(),testBean.getSapid());
//		return new ResponseEntity<>(testsList,headers, HttpStatus.OK);	
//	}
	
	public List<TestExamBean> getDueTestDataForStudentTODO(Long id, String sapId){
		MBAXIADAO dao = (MBAXIADAO)act.getBean("mbaxIADao");
		// get all test for the subjects alongwith students attempt details.
						
		//List<TestBean> attemptedTestsBySapidNSubject = dao.getApplicableDueTestsWithAttemptDetailsBySapidNSubject_todo(id,sapId);
		List<TestExamBean> attemptedTestsBySapidNSubject = getPendingTestDataForStudentTODO(id,sapId);
				

		
		return attemptedTestsBySapidNSubject;
		
	}
	
//	@RequestMapping(value = "/m/getPendingTestDataForTODO", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
//	public ResponseEntity<List<TestBean>> getPendingTestDataForTODO(@RequestBody TestBean testBean){
//		
//
//
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json");
//		
//		List<TestBean> testsList = getPendingTestDataForStudentTODO(testBean.getId(),testBean.getSapid());
//		return new ResponseEntity<>(testsList,headers, HttpStatus.OK);	
//	}
	
	public List<TestExamBean> getPendingTestDataForStudentTODO(Long id, String sapId){
		MBAXIADAO dao = (MBAXIADAO)act.getBean("mbaxIADao");
		// get all test for the subjects alongwith students attempt details.
		List<TestExamBean> returnList = new ArrayList<>();
		List<TestExamBean> attemptedTestsBySapidNSubject = dao.getApplicablePendingTestsWithAttemptDetailsBySapidNSubject_todo(id,sapId);

		List<TestExamBean> onGoingTestsBySapidNSubject = dao.getOngoingTestsWithAttemptDetailsBySapidNSubject_todo(id,sapId);
		List<TestExamBean> extendedTestsBySapidNSubject = dao.getExtendedTestsWithAttemptDetailsBySapidNSubject_todo(id,sapId);

				

		
		if(attemptedTestsBySapidNSubject !=null) {
			returnList.addAll(attemptedTestsBySapidNSubject);
		}
		if(onGoingTestsBySapidNSubject !=null) {
			returnList.addAll(onGoingTestsBySapidNSubject);
		}
		if(extendedTestsBySapidNSubject !=null) {
			returnList.addAll(extendedTestsBySapidNSubject);
		}
		
		
		return returnList;
		
	}
	
//	@RequestMapping(value = "/m/getFinishedTestDataForTODO", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
//	public ResponseEntity<List<TestBean>> getFinishedTestDataForTODO(@RequestBody TestBean testBean){
//		
//
//
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json");
//		
//		List<TestBean> testsList = getFinishedTestDataForStudentTODO(testBean.getId(),testBean.getSapid());
//		return new ResponseEntity<>(testsList,headers, HttpStatus.OK);	
//	}
	
	public List<TestExamBean> getFinishedTestDataForStudentTODO(Long id, String sapId){
		MBAXIADAO dao = (MBAXIADAO)act.getBean("mbaxIADao");
		// get all test for the subjects alongwith students attempt details.
						
		List<TestExamBean> attemptedTestsBySapidNSubject = dao.getApplicableFinishedTestsWithAttemptDetailsBySapidNSubject_todo(id,sapId);
		
		


		
				


		
		return attemptedTestsBySapidNSubject;
		
	}
	

	
//	public boolean updatePreviewedByFaculty(String sapid, Long testId) {
//		boolean updatedPreviewFlag = false;
//		MBAXIADAO dao = (MBAXIADAO)act.getBean("mbaxIADao");
//		
//		try {
//			String testStudentId = getTestStudentForTestByTestId(testId);


//			if(sapid.equals(testStudentId)) {



//				TestBean test = dao.getTestById(testId);
//				test.setPreviewedByFaculty("Y");



//				updatedPreviewFlag = dao.updatePreviewedByFaculty(test);
//			}
//		}
//		catch(Exception e) {
//			
//		}
//		



//		return updatedPreviewFlag;
//		
//	}
	
	public List<TestQuestionExamBean> getQuestionsAsPerTestDetailsForFacultyPreview(TestExamBean test, String sapid){
		MBAXIADAO dao = (MBAXIADAO)act.getBean("mbaxIADao");
		List<TestQuestionExamBean> testQuestions = dao.getTestQuestions(test.getId());
		if(testQuestions==null){
			return null;	
		}else{			
			return testQuestions;	
		}
	}

	
	
//	@RequestMapping(value = "/m/uploadTestAnswerFile", method = RequestMethod.POST,  produces = "application/json")
//	public ResponseEntity<HashMap<String,String>> uploadTestAnswerFile(MultipartHttpServletRequest request){
//		
//
//
//
//		
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json");
//		MBAXIADAO dao = (MBAXIADAO)act.getBean("mbaxIADao");
//		String testId= (String)request.getParameter("testId");
//		String userId= (String)request.getParameter("userId");
//
//
//
//
//
//
//
//
//		HashMap<String,String> response = new HashMap<>();
//		
//		String errorMessage = "";
//		String returnLink="";
//		Iterator<String> it = request.getFileNames();
//        while (it.hasNext()) {
//            String uploadFile = it.next();
//
//
//
//            MultipartFile file = request.getFile(uploadFile);
//
//
//
//            returnLink= uploadTestAnsAssignmentToServer(file,testId,userId);
//        }
//	
//		if(StringUtils.isBlank(returnLink)) {
//
//
//
//			errorMessage ="Error";
//		}
//        if("Error".equalsIgnoreCase(errorMessage)) {
//			response.put("Status", "Error");
//			response.put("errroMessage", "Error in uploading answer file.");
//			return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//        
//		else {
//			response.put("Status", "Success");
//			response.put("imageUrl", returnLink);
//			response.put("successMessage", errorMessage);
//			return new ResponseEntity<>(response,headers, HttpStatus.OK);	
//		}
//	}
	
	
	private String uploadTestAnsAssignmentToServer(MultipartFile imageBean,String userId,String testId) {

        String fileName = imageBean.getOriginalFilename();  
        
		//File file = convertFile(imageBean);
        MultipartFile file = imageBean;
		/*
		 * if(!(fileName.toUpperCase().endsWith(".PDF")) ){
		 * 
		 * return ""; }
		 */
		fileName = fileName.replaceAll("'", "_");
		fileName = fileName.replaceAll(",", "_");
		fileName = fileName.replaceAll("&", "and");
		fileName = fileName.replaceAll(" ", "_");
		fileName = fileName.replaceAll(":", "");
		

        InputStream inputStream = null;   
        OutputStream outputStream = null;
		String returnUrl = "";
		try {
            inputStream = file.getInputStream();   
            //String timeStamp = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
            String extention =fileName.substring(fileName.lastIndexOf("."), fileName.length()); 
            String imagePathToReturn ="assignment_"+testId+"_"+userId+"_"+ RandomStringUtils.randomAlphanumeric(12)+extention;
             			
            
            String filePath = IA_ASSIGNMENT_FILES_PATH+imagePathToReturn;



			File folderPath = new File(IA_ASSIGNMENT_FILES_PATH);
			      if (!folderPath.exists()) {



			            boolean created = folderPath.mkdirs();



			      }else {
			    	  
			      }
			      

			      File newFile = new File(filePath);   
			      outputStream = new FileOutputStream(newFile);   
			      int read = 0;   
			      byte[] bytes = new byte[1024];   

			      while ((read = inputStream.read(bytes)) != -1) {   
			            outputStream.write(bytes, 0, read);   
			      }
			      outputStream.close();
			      inputStream.close();
			      returnUrl=SERVER_PATH+"IATestAssignmentFiles/"+imagePathToReturn;



		} catch (Exception e) {
			// TODO Auto-generated catch block
			
			returnUrl="";
		}
		
		return returnUrl;
	}
	
	public File convertFile(MultipartFile file)
	{    
	    File convFile = new File(file.getOriginalFilename());
	    try {
			convFile.createNewFile(); 
			FileOutputStream fos = new FileOutputStream(convFile); 
			fos.write(file.getBytes());
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			
		} 
	    return convFile;
	}
	
	
	@RequestMapping(value = "/downloadStudentSubmittedAssignmentFile", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView  downloadStudentSubmittedAssignmentFile(HttpServletRequest request, HttpServletResponse response , Model m){
		ModelAndView modelnView = new ModelAndView("downloadAssignmentFile");
		String fullPath = request.getParameter("filePath");
		//String subject = request.getParameter("subject");

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
		return modelnView;
	}
	
	
//	For faculty test preview
	@RequestMapping(value = "/startStudentTestForAllViewsForFacultyPreview", method =  RequestMethod.GET)
	public String startStudentTestForAllViewsForFacultyPreview(HttpServletRequest request, 
								   HttpServletResponse response,
								   Model m,
								   @RequestParam("testId") Long testId,
								   @RequestParam("sapId") final String sapId,
								   @RequestParam(name = "consumerProgramStructureId", defaultValue = "111") Integer consumerProgramStructureId
								   ) {
		
		MBAXIADAO dao = (MBAXIADAO)act.getBean("mbaxIADao");

		TestExamBean test = dao.getTestById(testId);
		test = updateStartEndTimeIfExtended(test,sapId);
//		setting testtime to current time
		Date startDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String sDate = sdf.format(startDate);
		


		test.setStartDate(sDate);
		test.setDuration(120);



		
		List<TestQuestionExamBean> testQuestions = new ArrayList<>();
		StudentsTestDetailsExamBean studentsTestDetails= new StudentsTestDetailsExamBean();
		StudentsTestDetailsExamBean studentsTestDetailsCheck =  dao.getStudentsTestDetailsBySapidAndTestId(sapId,testId);



		/*
		 * Below checkValidityOfTest() has following checks :
		 * 1. check for test between given startDate and endDate
		 * 2. check for test attempt still open 
		 * 3. check for no of attempts
		 */
		//studentsTestDetailsCheck.setAttempt(0);
		String validityErrorMessage = checkValidityOfTest(test,studentsTestDetailsCheck);



		if( (!StringUtils.isBlank((validityErrorMessage))) && !"ATTEMPT_STILL_OPEN".equalsIgnoreCase(validityErrorMessage) ) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage",validityErrorMessage);
			return viewTestDetailsForStudentsForAllViews(request, response, m, test.getId(),"openTestDetails",sapId, consumerProgramStructureId);
		}
		String continueAttempt = "N";



		if("ATTEMPT_STILL_OPEN".equalsIgnoreCase(validityErrorMessage)) {
			studentsTestDetails = studentsTestDetailsCheck;


			//test.setDuration(studentsTestDetails.getRemainingTime());
			testQuestions = getAttemptNoNQuestionsMap(testId, sapId).get(studentsTestDetails.getAttempt());
			continueAttempt = "Y";
		}else {
		
		try {
//			String testStudentId = dao.getTestStudentForTestByTestId(testId);


//			if(sapId.equals(testStudentId)) {


				testQuestions = getQuestionsAsPerTestDetailsForFacultyPreview(test,sapId);
//			}
//			else {
//				testQuestions = getQuestionsAsPerTestDetails(test,sapId);
//			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block


			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in starting test, Unable to configure test questions.");
			
			return viewTestDetailsForStudentsForAllViews(request, response, m, test.getId(),"openTestDetails",sapId, consumerProgramStructureId);
		}


		StringBuilder questionsForTestStringBuilder=new StringBuilder();
		int i=0;
		for(TestQuestionExamBean bean : testQuestions) {


			if(i==0) {
				questionsForTestStringBuilder.append(bean.getId().toString());
			}else {
				questionsForTestStringBuilder.append(","+bean.getId().toString());
			}
			i++;
		}


		String questionsForTest= questionsForTestStringBuilder.toString();


		studentsTestDetails.setSapid(sapId);
		studentsTestDetails.setTestId(testId);
		studentsTestDetails.setActive("Y");
		studentsTestDetails.setTestCompleted("N");
		studentsTestDetails.setScore(0);
		//studentsTestDetails.setRemainingTime(getDurationOfTestWRTCurrentTime(test.getDuration(),test.getEndDate()));//New addition to give remaining time by endtime minus current time test.getDuration().intValue()
		studentsTestDetails.setRemainingTime(120);//New addition to give remaining time by endtime minus current time test.getDuration().intValue()
		test.setDuration(studentsTestDetails.getRemainingTime());
		studentsTestDetails.setTestQuestions(questionsForTest);
		studentsTestDetails.setCreatedBy(sapId);
		studentsTestDetails.setLastModifiedBy(sapId);


		if("Y".equalsIgnoreCase(test.getShowResultsToStudents())) {
			studentsTestDetails.setShowResult("Y");
		}else {
			studentsTestDetails.setShowResult("N");
		}


		if(studentsTestDetailsCheck.getId() == null) { //checking id here as it will be null if student is taking test for 1st time.
			studentsTestDetails.setAttempt(1);
		}else {
			studentsTestDetails.setAttempt((studentsTestDetailsCheck.getAttempt()+1));
		}


		long saved =  dao.saveStudentsTestDetails(studentsTestDetails);
		if(saved==0) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in starting test, Unable to create a test details entry.");


			return viewTestDetailsForStudentsForAllViews(request, response, m, test.getId(),"openTestDetails",sapId, consumerProgramStructureId);
			}
		}



		if(testQuestions.size() == 0) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in starting test, Unable to configure test questions.");


			return viewTestDetailsForStudentsForAllViews(request, response, m, test.getId(),"openTestDetails",sapId, consumerProgramStructureId);
		}
		

		if("Y".equalsIgnoreCase(continueAttempt)) {


			String endDateTimeFromStudentDetails = getEndDateInStringByStartDate(test.getDuration(),studentsTestDetails.getTestStartedOn());


			String endDateTimeForCalculatingRemainingTime = "";
			boolean takeEndDateFromTestDetails = getIfTakeEndDateFromTestDetails(test.getEndDate(),endDateTimeFromStudentDetails);


			if(takeEndDateFromTestDetails) {
				endDateTimeForCalculatingRemainingTime = test.getEndDate();
			}else{
				endDateTimeForCalculatingRemainingTime = endDateTimeFromStudentDetails;
			}


			//test.setRemainingTime(getDurationOfTestWRTCurrentTime(test.getDuration(), endDateTimeForCalculatingRemainingTime));
			test.setRemainingTime(120);
			studentsTestDetails.setRemainingTime(test.getRemainingTime());
			test.setDuration(test.getRemainingTime());
		}


		m.addAttribute("userId", sapId);
		m.addAttribute("studentsTestDetails", studentsTestDetails);
		m.addAttribute("testQuestions", testQuestions);


		m.addAttribute("noOfQuestions", testQuestions!=null?testQuestions.size():0);
		m.addAttribute("test", test);
		m.addAttribute("continueAttempt", continueAttempt);
		m.addAttribute("serverPath", SERVER_PATH);
		
		return "mbaxia/studentTestPageForAllViewsForFacultyPreview";
	}
	
	
//	IAPreviewQuestionsForFacultyView start
	@RequestMapping(value = "/IAPreviewQuestionsForFacultyView", method =  RequestMethod.GET)
	public String startStudentTestForAllViewsForFacultyPreview(HttpServletRequest request, 
								   HttpServletResponse response,
								   Model m,
								   @RequestParam("id") Long testId,
								   @RequestParam(name = "consumerProgramStructureId", defaultValue = "111") Integer consumerProgramStructureId
								   ) {
		
		String sapId = "55555555555";
		Integer attempt = 1;
		MBAXIADAO dao = (MBAXIADAO)act.getBean("mbaxIADao");

		TestExamBean test = dao.getTestById(testId);
		
		//delete old attempt start
		String errorMessage = dao.deleteTestAttemptDataBySapidAndTestIdAndAttempt(sapId,testId,attempt);
		//delete old attempt end
		
		
		
		//Extend IA attempt start
		try {
			TestExamBean beanForUpdateExtendTime = new TestExamBean();
			beanForUpdateExtendTime.setSapid(sapId);
			beanForUpdateExtendTime.setTestId(testId);
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			beanForUpdateExtendTime.setTestStartedOn(sdf.format(new Date()));
			
			auditDao.updateDate(beanForUpdateExtendTime);
		}catch (Exception e) {
			// TODO Auto-generated catch block
			
		}
		//Extend IA attempt end
		
		test = updateStartEndTimeIfExtended(test,sapId);
//		setting testtime to current time
		Date startDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String sDate = sdf.format(startDate);
		


		test.setStartDate(sDate);
		test.setDuration(120);



		
		List<TestQuestionExamBean> testQuestions = new ArrayList<>();
		StudentsTestDetailsExamBean studentsTestDetails= new StudentsTestDetailsExamBean();
		StudentsTestDetailsExamBean studentsTestDetailsCheck =  dao.getStudentsTestDetailsBySapidAndTestId(sapId,testId);



		/*
		 * Below checkValidityOfTest() has following checks :
		 * 1. check for test between given startDate and endDate
		 * 2. check for test attempt still open 
		 * 3. check for no of attempts
		 */
		//studentsTestDetailsCheck.setAttempt(0);
		String validityErrorMessage = checkValidityOfTest(test,studentsTestDetailsCheck);



		if( (!StringUtils.isBlank((validityErrorMessage))) && !"ATTEMPT_STILL_OPEN".equalsIgnoreCase(validityErrorMessage) ) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage",validityErrorMessage);
			return viewTestDetailsForStudentsForAllViews(request, response, m, test.getId(),"openTestDetails",sapId, consumerProgramStructureId);
		}
		String continueAttempt = "N";



		if("ATTEMPT_STILL_OPEN".equalsIgnoreCase(validityErrorMessage)) {
			studentsTestDetails = studentsTestDetailsCheck;


			//test.setDuration(studentsTestDetails.getRemainingTime());
			testQuestions = getAttemptNoNQuestionsMap(testId, sapId).get(studentsTestDetails.getAttempt());
			continueAttempt = "Y";
		}else {
		
		try {
//			String testStudentId = dao.getTestStudentForTestByTestId(testId);


//			if(sapId.equals(testStudentId)) {


				testQuestions = getQuestionsAsPerTestDetailsForFacultyPreview(test,sapId);
//			}
//			else {
//				testQuestions = getQuestionsAsPerTestDetails(test,sapId);
//			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block


			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in starting test, Unable to configure test questions.");
			
			return viewTestDetailsForStudentsForAllViews(request, response, m, test.getId(),"openTestDetails",sapId, consumerProgramStructureId);
		}


		StringBuilder questionsForTestStringBuilder=new StringBuilder();
		int i=0;
		for(TestQuestionExamBean bean : testQuestions) {


			if(i==0) {
				questionsForTestStringBuilder.append(bean.getId().toString());
			}else {
				questionsForTestStringBuilder.append(","+bean.getId().toString());
			}
			i++;
		}


		String questionsForTest= questionsForTestStringBuilder.toString();


		studentsTestDetails.setSapid(sapId);
		studentsTestDetails.setTestId(testId);
		studentsTestDetails.setActive("Y");
		studentsTestDetails.setTestCompleted("N");
		studentsTestDetails.setScore(0);
		//studentsTestDetails.setRemainingTime(getDurationOfTestWRTCurrentTime(test.getDuration(),test.getEndDate()));//New addition to give remaining time by endtime minus current time test.getDuration().intValue()
		studentsTestDetails.setRemainingTime(120);//New addition to give remaining time by endtime minus current time test.getDuration().intValue()
		test.setDuration(studentsTestDetails.getRemainingTime());
		studentsTestDetails.setTestQuestions(questionsForTest);
		studentsTestDetails.setCreatedBy(sapId);
		studentsTestDetails.setLastModifiedBy(sapId);


		if("Y".equalsIgnoreCase(test.getShowResultsToStudents())) {
			studentsTestDetails.setShowResult("Y");
		}else {
			studentsTestDetails.setShowResult("N");
		}


		if(studentsTestDetailsCheck.getId() == null) { //checking id here as it will be null if student is taking test for 1st time.
			studentsTestDetails.setAttempt(1);
		}else {
			studentsTestDetails.setAttempt((studentsTestDetailsCheck.getAttempt()+1));
		}


		long saved =  dao.saveStudentsTestDetails(studentsTestDetails);
		if(saved==0) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in starting test, Unable to create a test details entry.");


			return viewTestDetailsForStudentsForAllViews(request, response, m, test.getId(),"openTestDetails",sapId, consumerProgramStructureId);
			}
		}



		if(testQuestions.size() == 0) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in starting test, Unable to configure test questions.");


			return viewTestDetailsForStudentsForAllViews(request, response, m, test.getId(),"openTestDetails",sapId, consumerProgramStructureId);
		}
		

		if("Y".equalsIgnoreCase(continueAttempt)) {


			String endDateTimeFromStudentDetails = getEndDateInStringByStartDate(test.getDuration(),studentsTestDetails.getTestStartedOn());


			String endDateTimeForCalculatingRemainingTime = "";
			boolean takeEndDateFromTestDetails = getIfTakeEndDateFromTestDetails(test.getEndDate(),endDateTimeFromStudentDetails);


			if(takeEndDateFromTestDetails) {
				endDateTimeForCalculatingRemainingTime = test.getEndDate();
			}else{
				endDateTimeForCalculatingRemainingTime = endDateTimeFromStudentDetails;
			}


			//test.setRemainingTime(getDurationOfTestWRTCurrentTime(test.getDuration(), endDateTimeForCalculatingRemainingTime));
			test.setRemainingTime(120);
			studentsTestDetails.setRemainingTime(test.getRemainingTime());
			test.setDuration(test.getRemainingTime());
		}


		m.addAttribute("userId", sapId);
		m.addAttribute("studentsTestDetails", studentsTestDetails);
		m.addAttribute("testQuestions", testQuestions);


		m.addAttribute("noOfQuestions", testQuestions!=null?testQuestions.size():0);
		m.addAttribute("test", test);
		m.addAttribute("continueAttempt", continueAttempt);
		m.addAttribute("serverPath", SERVER_PATH);
		
		return "mbaxia/studentTestPageForAllViewsForFacultyPreview";
	}
	//IAPreviewQuestionsForFacultyView end
	

//	to be deleted, api shifted to rest controller
//	//log auto save descriptive start
//		@RequestMapping(value = "/m/logAutoSaveApiHit", method = RequestMethod.POST, consumes = "application/json", produces = "application/json; charset=UTF-8")
//		public ResponseEntity<HashMap<String,String>> logAutoSaveApiHit(@RequestBody StudentQuestionResponseBean answer){
//			
//			logger.info("\n"+SERVER+": "+new Date()+" IN logAutoSaveApiHit got sapid :  "+answer.getSapid()+" answer : "+answer.getQuestionId());
//
//
//			HttpHeaders headers = new HttpHeaders();
//			headers.add("Content-Type", "application/json");
//			HashMap<String,String> response = new HashMap<>();
//			response.put("Status", "Success");
//			return new ResponseEntity<>(response,headers, HttpStatus.OK);
//			
//		}
	//log auto save descriptive end
		//changes by Pranit on 2May20 for marks addition in test config start
		public HashMap<String,List<TestQuestionExamBean>> getTypeAndMarksKeyMapFromQuestions(List<TestQuestionExamBean> testQuestions){
			HashMap<String,List<TestQuestionExamBean>> typeNQuestionMap= new HashMap<>();
			List<TestQuestionExamBean> temp = null;
			for(TestQuestionExamBean q : testQuestions) {
				if(!typeNQuestionMap.containsKey(q.getType()+"-"+q.getMarks())) {
					temp = new ArrayList<>();
				}else {
					temp = typeNQuestionMap.get(q.getType()+"-"+q.getMarks());
				}
				temp.add(q);
				typeNQuestionMap.put(q.getType()+"-"+q.getMarks(), temp);
			
			}
			
			return typeNQuestionMap;
		}
	//changes by Pranit on 2May20 for marks addition in test config end
	
		//changes by Pranit on 26Apr21 for sectionwise questions addition in test config start
		public HashMap<String,List<TestQuestionExamBean>> getTypeAndTestIdSectionIdMarksKeyMapFromQuestions(List<TestQuestionExamBean> testQuestions){
			HashMap<String,List<TestQuestionExamBean>> typeNQuestionMap= new HashMap<>();
			List<TestQuestionExamBean> temp = null;
			for(TestQuestionExamBean q : testQuestions) {
				if(!typeNQuestionMap.containsKey(q.getTestId()+"-"+q.getSectionId()+"-"+q.getType()+"-"+q.getMarks())) {
					temp = new ArrayList<>();
				}else {
					temp = typeNQuestionMap.get(q.getTestId()+"-"+q.getSectionId()+"-"+q.getType()+"-"+q.getMarks());
				}
				temp.add(q);
				typeNQuestionMap.put(q.getTestId()+"-"+q.getSectionId()+"-"+q.getType()+"-"+q.getMarks(), temp);
			
			}
			return typeNQuestionMap;
		}
		//changes by Pranit on 26Apr21 for sectionwise questions addition in test config  end
	
		public String encryptWithOutSpecialCharacters(String stringToBeEncrypted) throws Exception{

			return AESencrp.encrypt(stringToBeEncrypted).replaceAll("\\+", "_plus_");
		}
		public String decryptWithOutSpecialCharacters(String stringToBeDecrypted) throws Exception{
			
			return AESencrp.decrypt(stringToBeDecrypted.replaceAll("_plus_", "\\+"));
		}
		
		
		@RequestMapping(value = "/viewTestDetailsForStudentsForAllViewsForLeads", method =  RequestMethod.GET)
		public String viewTestDetailsForStudentsForAllViewsForLeads(HttpServletRequest request,
							   HttpServletResponse response,
							   Model m,
							   @RequestParam("id") Long id,
							   @RequestParam("message") String message,
							   @RequestParam("userId") String userId
							   ){
			
			


			logger.info("\n"+SERVER+": "+new Date()+" IN viewTestDetailsForStudentsForAllViewsForLeads got id "+id+" userId: "+userId+" message: "+message);
			
			ViewTestDetailsForStudentsAPIResponse responseBean =  getViewTestDetailsForStudentsAPIResponseForLeads(userId,id,message);
			
			m.addAttribute("assignmentPaymentPending",responseBean.getAssignmentPaymentPending());
			m.addAttribute("userId", userId);
			m.addAttribute("test", responseBean.getTest());
			m.addAttribute("showStartTestButton", responseBean.isShowStartTestButton());
			m.addAttribute("studentsTestDetails", responseBean.getStudentsTestDetails());
			m.addAttribute("messageDetails", responseBean.getMessageDetails());
			m.addAttribute("attemptsDetails", responseBean.getAttemptsDetails());
			m.addAttribute("attemptNoNQuestionsMap", responseBean.getAttemptNoNQuestionsMap());
			m.addAttribute("continueAttempt", responseBean.getContinueAttempt());
			m.addAttribute("subject",responseBean.getSubject());
			m.addAttribute("paymentPendingForSecondOrHigherAttempt", responseBean.getPaymentPendingForSecondOrHigherAttempt());

			try {
				m.addAttribute("sapidForUrl", encryptWithOutSpecialCharacters(userId));
				m.addAttribute("testIdForUrl", encryptWithOutSpecialCharacters(id+""));
			} catch (Exception e) {
				logger.info("\n"+SERVER+": "+new Date()+" IN catch() of encrypt viewTestDetailsForStudents got id "+id+" userId: "+userId+" error : "+e.getMessage());
				m.addAttribute("sapidForUrl", "");
				m.addAttribute("testIdForUrl", "");
			}
			
			return "mbaxia/testDetailsForStudentsForAllViewForLeads";
		}
		
		
		public ViewTestDetailsForStudentsAPIResponse getViewTestDetailsForStudentsAPIResponseForLeads(String userId,Long id,String message) {
			ViewTestDetailsForStudentsAPIResponse returnBean = new ViewTestDetailsForStudentsAPIResponse();
			MBAXIADAO dao = (MBAXIADAO)act.getBean("mbaxIADao");
			logger.info("\n"+SERVER+": "+"IN getViewTestDetailsForStudentsAPIResponseForLeads got id "+id+" userId: "+userId+" message: "+message);
			
			
			TestExamBean test = dao.getTestByIdForLeads(id);
			
			//test = updateStartEndTimeIfExtended(test,userId);

			//test.setRemainingTime(getDurationOfTestWRTCurrentTime(test.getDuration(),test.getEndDate()));
			test.setRemainingTime(test.getDuration());
			
			StudentsTestDetailsExamBean studentsTestDetails =  dao.getStudentsTestDetailsBySapidAndTestIdForLeads(userId,test.getId());
			
			String continueAttempt = "N";
			boolean canContinueAttempt = canContinueAttempt(test,studentsTestDetails);
			if(canContinueAttempt) {
				continueAttempt= "Y";
				studentsTestDetails.setAttempt(studentsTestDetails.getAttempt()-1);
				test.setRemainingTime(getDurationOfTestWRTTestStartedOn(test.getDuration(),studentsTestDetails.getTestStartedOn()));
			}
			
			//studentsTestDetails = checkIfCanShowResultsAndAttemptedQuestions(studentsTestDetails,test);
			

			/*
			 * int score=studentsTestDetails.getScore();
			 * if("Y".equalsIgnoreCase(studentsTestDetails.getShowResult())) {
			 * if(studentsTestDetails.getScore() == 0) { //if student didnot click submit
			 * test and left test page
			 * studentsTestDetails.setScore(dao.caluclateTestScore(studentsTestDetails.
			 * getSapid(),studentsTestDetails.getTestId()));


			 * +studentsTestDetails.getScore()); score=studentsTestDetails.getScore();
			 * boolean updatedSore=dao.updateStudentsTestDetails(studentsTestDetails);


			 * +updatedSore); } } studentsTestDetails.setScore(score);
			 */

			//int score=studentsTestDetails.getScore();
			/*if("Y".equalsIgnoreCase(studentsTestDetails.getShowResult())) {
				if(studentsTestDetails.getScore() == 0) { //if student didnot click submit test and left test page
					studentsTestDetails.setScore(dao.caluclateTestScore(studentsTestDetails.getSapid(),studentsTestDetails.getTestId()));


					score=studentsTestDetails.getScore();
					boolean updatedSore=dao.updateStudentsTestDetails(studentsTestDetails);


				}
			}*/
			//studentsTestDetails.setScore(score);

			
			List<StudentsTestDetailsExamBean> attemptsDetails =  dao.getAttemptsDetailsBySapidNTestIdForLeads(userId, test.getId());
			
			double scoreForLeads=-1;
			int positionOfAttemptConsiderred =0;
			int countForAttempts=0;
			for(StudentsTestDetailsExamBean attempt : attemptsDetails ) {
				if(scoreForLeads < attempt.getScore()) {

					//attempt.setConsideredForLeadsResult("Y");
					scoreForLeads = attempt.getScore();
					positionOfAttemptConsiderred=countForAttempts;
				}
				//else {
					//attempt.setConsideredForLeadsResult("N");
				//}
				countForAttempts++;
			
			}
			
			if(attemptsDetails !=null && attemptsDetails.size()>0 ) {
				attemptsDetails.get(positionOfAttemptConsiderred).setConsideredForLeadsResult("Y");
			}
			
			
			Map<Integer, List<TestQuestionExamBean>> attemptNoNQuestionsMap = new HashMap<>();
			
			ArrayList<TestQuestionExamBean> attemptDetail1 = new ArrayList<TestQuestionExamBean>();
			ArrayList<TestQuestionExamBean> attemptDetail2 = new ArrayList<TestQuestionExamBean>();
			ArrayList<TestQuestionExamBean> attemptDetail3 = new ArrayList<TestQuestionExamBean>();
			
			 
			//Updated : 17Feb19 by Pranit to only take attemptdetails after results are live start
			
			if("Y".equalsIgnoreCase(studentsTestDetails.getShowResult())) {
			
				 attemptNoNQuestionsMap = getAttemptNoNQuestionsMapForLeads(test.getId(),userId);
				
				for (Map.Entry<Integer, List<TestQuestionExamBean>> entry : attemptNoNQuestionsMap.entrySet()) {			


					if(entry.getKey() == 1) {
					attemptDetail1 = (ArrayList<TestQuestionExamBean>) entry.getValue();
					}
					if(entry.getKey() == 2) {
						attemptDetail2 = (ArrayList<TestQuestionExamBean>) entry.getValue();
						}
					if(entry.getKey() == 3) {
						attemptDetail3 = (ArrayList<TestQuestionExamBean>) entry.getValue();
						}
						
				}
			}
			//Updated : 17Feb19 by Pranit to only take attemptdetails after results are live start
			
			if("testTimeOut".equalsIgnoreCase(message)) {
				message = "Time Over";
			}
			if("testEnded".equalsIgnoreCase(message)) {
				message = "Test Ended";
			}
			
			
			String paymentPendingForSecondOrHigherAttempt = "";
			
			if("old".equalsIgnoreCase(test.getApplicableType())) {
				/*
				StudentMarksDAO sMarksDao = (StudentMarksDAO)act.getBean("studentMarksDAO");
				StudentBean student = sMarksDao.getSingleStudentsData(userId);
				
				//Logic to check 3rd attempt of subject and enable payment if it is 3rd attempt start
				
				AssignmentsDAO aDao = (AssignmentsDAO)act.getBean("asignmentsDAO");
				boolean isOnline = new AssignmentSubmissionController().isOnline(student);


				//m.addAttribute("assignmentPaymentPending","false");
				returnBean.setAssignmentPaymentPending("false");
				if(isOnline){//Applicble for online students only
					int pastCycleAssignmentAttempts = aDao.getPastCycleAssignmentAttempts(test.getSubject(),userId);
					int pastCycleTestAttempts = dao.getPastCycleTestAttempts(test.getSubject(),userId);


					
					if((pastCycleAssignmentAttempts + pastCycleTestAttempts) >=2){
						boolean hasPaidForAssignment = aDao.checkIfAssignmentFeesPaid(test.getSubject(), userId); //check if Assignment Fee Paid for Current drive 
						if(!hasPaidForAssignment){
							//m.addAttribute("assignmentPaymentPending","true");
							returnBean.setAssignmentPaymentPending("true");


						}
					}
				}
				//Logic to check 3rd attempt of subject and enable payment if it is 3rd attempt end
				
				//code to take charge for 2nd or higher attempt start
				paymentPendingForSecondOrHigherAttempt = "N";
				if(studentsTestDetails.getAttempt()+1 <= test.getMaxAttempt() && (studentsTestDetails.getAttempt()+1) > 1) {
				
				if( (studentsTestDetails.getAttempt() > 0) && (!"Y".equalsIgnoreCase(continueAttempt)) ) {
					boolean checkIfTestFeesPaidForAttempt = dao.checkIfTestFeesPaidForAttempt(test.getSubject(), userId, test.getId(), studentsTestDetails.getAttempt()+1);
					if(!checkIfTestFeesPaidForAttempt) {
						paymentPendingForSecondOrHigherAttempt="Y";
					}
				}


				}
				//code to take charge for 2nd or higher attempt end
				
				*/	
				paymentPendingForSecondOrHigherAttempt = "N";
				returnBean.setAssignmentPaymentPending("false");
				
			}else {
				paymentPendingForSecondOrHigherAttempt = "N";
				returnBean.setAssignmentPaymentPending("false");
				
			}
					
			
			
			//Send mail after test is completed start
			/*try {



				if(("Time Over".equalsIgnoreCase(message) || "Test Ended".equalsIgnoreCase(message)) && "PROD".equalsIgnoreCase(ENVIRONMENT)) {
					MailSender mailSender = (MailSender)act.getBean("mailer");
					StudentMarksDAO sMarksDao = (StudentMarksDAO)act.getBean("studentMarksDAO");
					StudentBean student = sMarksDao.getSingleStudentsData(userId);
					


					mailSender.sendTestEndedEmail(student,test,studentsTestDetails);


					
					
					
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//
				//mailSender.mailStackTrace("Error in Saving Successful Transaction", e);
				logger.info("\n"+SERVER+": "+"IN Send mail error got sapid  "+studentsTestDetails.getSapid()+" testId: "+test.getId()+" Error: "+e.getMessage());
				
			}*/
			//Send mail after test is completed end
			
			//Check show start test button start
			boolean showStartTestButton = false;

			if( !("Y".equalsIgnoreCase(continueAttempt))) {
				//check for test between given startDate and endDate start
				try {
					String startDate = test.getStartDate();
					String endDate = test.getEndDate();


					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date sDate = sdf.parse(startDate.replaceAll("T", " "));
					Date cDate = new Date();
					Date eDate = sdf.parse(endDate.replaceAll("T", " "));
					


					
					if(cDate.after(sDate) && cDate.before(eDate)) {


						showStartTestButton = true;
					}else {
						if(cDate.before(sDate)) {


							showStartTestButton = false;
						}
						else if(cDate.after(eDate)) {


							showStartTestButton = false;;
						}
						else {


							showStartTestButton = false;
						}
					}
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					
					showStartTestButton = false;
				}
				//check for test between given startDate and endDate end
			}
			else {
				showStartTestButton = true;
			}
			//Check show start test button end
			
			/*
			if(showStartTestButton && "Y".equalsIgnoreCase(continueAttempt)) {
				String endDateTimeFromStudentDetails = getEndDateInStringByStartDate(test.getDuration(),studentsTestDetails.getTestStartedOn());


				String endDateTimeForCalculatingRemainingTime = "";
				boolean takeEndDateFromTestDetails = getIfTakeEndDateFromTestDetails(test.getEndDate(),endDateTimeFromStudentDetails);


				if(takeEndDateFromTestDetails) {
					endDateTimeForCalculatingRemainingTime = test.getEndDate();
				}else{
					endDateTimeForCalculatingRemainingTime = endDateTimeFromStudentDetails;
				}
				test.setRemainingTime(getDurationOfTestWRTCurrentTime(test.getDuration(), endDateTimeForCalculatingRemainingTime));
			}
			*/
			
			returnBean.setShowStartTestButton(showStartTestButton);
			
			//m.addAttribute("test", test);
			returnBean.setTest(test);
			
			//m.addAttribute("studentsTestDetails", studentsTestDetails);
			returnBean.setStudentsTestDetails(studentsTestDetails);
			
			//m.addAttribute("messageDetails", message);
			returnBean.setMessageDetails(message);
			
			//m.addAttribute("attemptsDetails", attemptsDetails);
			returnBean.setAttemptsDetails(attemptsDetails);
			
			//m.addAttribute("attemptNoNQuestionsMap", attemptNoNQuestionsMap);
			returnBean.setAttemptNoNQuestionsMap(attemptNoNQuestionsMap);
			
			//m.addAttribute("continueAttempt", continueAttempt);
			returnBean.setContinueAttempt(continueAttempt);
			
			//m.addAttribute("subject",test.getSubject());
			returnBean.setSubject(test.getSubject());
			
			//m.addAttribute("paymentPendingForSecondOrHigherAttempt", paymentPendingForSecondOrHigherAttempt);
			returnBean.setPaymentPendingForSecondOrHigherAttempt(paymentPendingForSecondOrHigherAttempt);
			
			returnBean.setAttemptDetail1(attemptDetail1);
			
			returnBean.setAttemptDetail2(attemptDetail2);
			
			returnBean.setAttemptDetail3(attemptDetail3);
			
			
			


			logger.info("\n"+SERVER+": "+new Date()+" IN getViewTestDetailsForStudentsAPIResponseForLeads got id "+id+" userId: "+userId+" returnBean: "+returnBean.toString());
			
			return returnBean;
		}
		
		@RequestMapping(value = "/assignmentGuidelinesForAllViewsForLeads", method =  RequestMethod.GET)
		public String assignmentGuidelinesForAllViewsForLeads(HttpServletRequest request,
							   HttpServletResponse response,
							   Model m,
							   @RequestParam("testIdForUrl") String testIdForUrl,
							   @RequestParam("sapidForUrl") String sapidForUrl
							   ){
			/*if(!checkSession(request, response)){
				redirectToPortalApp(response);
			}*/
			
			



			
			String userId="";
			String testId="";

			try {
				userId = decryptWithOutSpecialCharacters(sapidForUrl);
				testId = decryptWithOutSpecialCharacters(testIdForUrl);
			} catch (Exception e) {
				logger.info("\n"+SERVER+": "+new Date()+" IN catch() of decrypt assignmentGuidelinesForAllViews got testIdForUrl "+testIdForUrl+" sapidForUrl: "+sapidForUrl+" error : "+e.getMessage());

				userId = "";
				testId = "";
			}
			



			
			m.addAttribute("sapidForUrl", sapidForUrl);
			m.addAttribute("testIdForUrl", testIdForUrl);
			
			m.addAttribute("testId", testId);
			m.addAttribute("userId", userId);
			return "mbaxia/assignmentGuidelinesForAllViewsForLeads";
		}

		@RequestMapping(value = "/startStudentTestForAllViewsForLeads", method =  RequestMethod.GET)
		public String startStudentTestForAllViewsForLeads(HttpServletRequest request, 
									   HttpServletResponse response,
									   Model m,
									   @RequestParam("sapidForUrl") String sapidForUrl,
									   @RequestParam("testIdForUrl")  String testIdForUrl
									   ) {
			
			MBAXIADAO dao = (MBAXIADAO)act.getBean("mbaxIADao");


			
			String sapId="";
			Long testId=null;

			try {
				sapId = decryptWithOutSpecialCharacters(sapidForUrl);
				testId = Long.parseLong(decryptWithOutSpecialCharacters(testIdForUrl));
			} catch (Exception e) {
				logger.info("\n"+SERVER+": "+new Date()+" IN catch() of decrypt startStudentTestForAllViewsForLeads got testIdForUrl "+testIdForUrl+" sapidForUrl: "+sapidForUrl+" error : "+e.getMessage());

				sapId="";
				testId=null;
			}


			
			if( (StringUtils.isBlank((sapId))) || (testId==null) ) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage","Something Went Wrong, Unable to get test related data. Please refresh and try again. Contact Course Coordinator on chat.");
				return viewTestDetailsForStudentsForAllViewsForLeads(request, response, m, testId,"openTestDetails",sapId);
			}
			
			logger.info("\n"+SERVER+": "+new Date()+" IN startStudentTestForAllViewsForLeads got testId : "+testId+" sapId : "+sapId);
			
			TestExamBean test = dao.getTestByIdForLeads(testId);
			//test = updateStartEndTimeIfExtended(test,sapId);
			
			List<TestQuestionExamBean> testQuestions = new ArrayList<>();
			StudentsTestDetailsExamBean studentsTestDetails= new StudentsTestDetailsExamBean();
			StudentsTestDetailsExamBean studentsTestDetailsCheck =  dao.getStudentsTestDetailsBySapidAndTestIdForLeads(sapId,testId);
			
			/*
			 * Below checkValidityOfTest() has following checks :
			 * 1. check for test between given startDate and endDate
			 * 2. check for test attempt still open 
			 * 3. check for no of attempts
			 */
			String validityErrorMessage = checkValidityOfTest(test,studentsTestDetailsCheck);
			


			
			if( (!StringUtils.isBlank((validityErrorMessage))) && !"ATTEMPT_STILL_OPEN".equalsIgnoreCase(validityErrorMessage) ) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage",validityErrorMessage);
				return viewTestDetailsForStudentsForAllViewsForLeads(request, response, m, test.getId(),"openTestDetails",sapId);
			}
			String continueAttempt = "N";
			boolean canRefreshTheTestPage=true;
			if("ATTEMPT_STILL_OPEN".equalsIgnoreCase(validityErrorMessage)) {
				studentsTestDetails = studentsTestDetailsCheck;


				//test.setDuration(studentsTestDetails.getRemainingTime());
				testQuestions = getAttemptNoNQuestionsMapForLeads(testId, sapId).get(studentsTestDetails.getAttempt());
				continueAttempt = "Y";
				
				//check if countOfRefreshPage <= noOfRefreshAllowed
				//canRefreshTheTestPage = checkIfCanRefreshTheTestPage(studentsTestDetails.getCountOfRefreshPage(),test.getNoOfRefreshAllowed());
				
				//increment countOfRefreshPage
				String incrementCountOfRefreshPage = dao.incrementCountOfRefreshPageForLeads(sapId,testId); 
				
			}else {
			
			try {

				canRefreshTheTestPage = true;
				
				
				testQuestions = getQuestionsAsPerTestDetailsForLeads(test,sapId,studentsTestDetailsCheck);
				
				if(testQuestions == null) {
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "Error in starting test, Unable to configure test questions.");

					return viewTestDetailsForStudentsForAllViewsForLeads(request, response, m, test.getId(),"openTestDetails",sapId);
				}
				


				if(testQuestions.size() == 0) {
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "Error in starting test, Unable to configure test questions.");

					return viewTestDetailsForStudentsForAllViewsForLeads(request, response, m, test.getId(),"openTestDetails",sapId);
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//
				logger.info("\n"+SERVER+": "+" IN startStudentTestForAllViews > getQuestionsAsPerTestDetails  got testId : "+testId+" sapId:  "+sapId+", Error :  "+e.getMessage());
				
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Error in starting test, Unable to configure test questions.");
				
				return viewTestDetailsForStudentsForAllViewsForLeads(request, response, m, test.getId(),"openTestDetails",sapId);
			}
			StringBuilder questionsForTestStringBuilder=new StringBuilder();
			int i=0;
			for(TestQuestionExamBean bean : testQuestions) {


				if(i==0) {
					questionsForTestStringBuilder.append(bean.getId().toString());
				}else {
					questionsForTestStringBuilder.append(","+bean.getId().toString());
				}
				i++;
			}
			String questionsForTest= questionsForTestStringBuilder.toString();


			studentsTestDetails.setSapid(sapId);
			studentsTestDetails.setTestId(testId);
			studentsTestDetails.setActive("Y");
			studentsTestDetails.setTestCompleted("N");
			studentsTestDetails.setScore(0);
			//studentsTestDetails.setRemainingTime(getDurationOfTestWRTCurrentTime(test.getDuration(),test.getEndDate()));//New addition to give remaining time by endtime minus current time test.getDuration().intValue()
			//test.setDuration(studentsTestDetails.getRemainingTime());
			studentsTestDetails.setRemainingTime(test.getDuration());
			studentsTestDetails.setTestQuestions(questionsForTest);
			studentsTestDetails.setCreatedBy(sapId);
			studentsTestDetails.setLastModifiedBy(sapId);
			//if("Y".equalsIgnoreCase(test.getShowResultsToStudents())) {
			//	studentsTestDetails.setShowResult("Y");
			//}else {
				studentsTestDetails.setShowResult("N");
			//}
			
			if(studentsTestDetailsCheck.getId() == null) { //checking id here as it will be null if student is taking test for 1st time.
				studentsTestDetails.setAttempt(1);
			}else {
				studentsTestDetails.setAttempt((studentsTestDetailsCheck.getAttempt()+1));
			}
			long saved =  dao.saveStudentsTestDetailsForLeads(studentsTestDetails);
			if(saved==0) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Error in starting test, Unable to create a test details entry.");

				return viewTestDetailsForStudentsForAllViewsForLeads(request, response, m, test.getId(),"openTestDetails",sapId);
				}
				
				//for showing start datetime on test page start
				
				try {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date cDate = new Date();
					studentsTestDetails.setTestStartedOn(sdf.format(cDate));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					
				}
				
				//for showing start datetime on test page end
			}
			
			

			if("Y".equalsIgnoreCase(continueAttempt)) {
				/*
				String endDateTimeFromStudentDetails = getEndDateInStringByStartDate(test.getDuration(),studentsTestDetails.getTestStartedOn());


				String endDateTimeForCalculatingRemainingTime = "";
				boolean takeEndDateFromTestDetails = getIfTakeEndDateFromTestDetails(test.getEndDate(),endDateTimeFromStudentDetails);


				if(takeEndDateFromTestDetails) {
					endDateTimeForCalculatingRemainingTime = test.getEndDate();
				}else{
					endDateTimeForCalculatingRemainingTime = endDateTimeFromStudentDetails;
				}
				*/
				test.setRemainingTime(getDurationOfTestWRTTestStartedOn(test.getDuration(), studentsTestDetails.getTestStartedOn()));
				studentsTestDetails.setRemainingTime(test.getRemainingTime());
				test.setDuration(test.getRemainingTime());
			}
			
			m.addAttribute("userId", sapId);
			m.addAttribute("studentsTestDetails", studentsTestDetails);
			m.addAttribute("testQuestions", testQuestions);


			m.addAttribute("noOfQuestions", testQuestions!=null?testQuestions.size():0);
			m.addAttribute("test", test);
			m.addAttribute("continueAttempt", continueAttempt);
			m.addAttribute("serverPath", SERVER_PATH);
			m.addAttribute("ENVIRONMENT", ENVIRONMENT);

			m.addAttribute("canRefreshTheTestPage", canRefreshTheTestPage);
			
			logger.info("\n"+SERVER+": "+new Date()+" IN startStudentTestForAllViewsForLeads got testId : "+testId+" sapId : "+sapId+" returnBean : "+m.asMap());
			
			return "mbaxia/studentTestPageForAllViewsForLeads";
		}
		
		
		public Map<Integer, List<TestQuestionExamBean>> getAttemptNoNQuestionsMapForLeads(Long testId, String sapId){
			MBAXIADAO dao = (MBAXIADAO)act.getBean("mbaxIADao");
			List<StudentsTestDetailsExamBean> attemptsDetails =  dao.getAttemptsDetailsBySapidNTestIdForLeads(sapId, testId);
			HashMap<Integer,List<StudentQuestionResponseExamBean>> attemptsAnswerMap = dao.getAttemptAnswersMapBySapidNTestIdForLeads(sapId, testId);
			Map<Integer, List<TestQuestionExamBean>> attemptNoNQuestionsMap= new HashMap<>();
			for(StudentsTestDetailsExamBean b : attemptsDetails) {


				List<TestQuestionExamBean> qList = dao.getTestQuestionsPerAttemptForLeads(b.getTestQuestions());
				List<StudentQuestionResponseExamBean> answersByAttempt = attemptsAnswerMap.get(b.getAttempt());
				
				
				for(TestQuestionExamBean q : qList) {
					
					List<StudentQuestionResponseExamBean> answerListByQuestionId = new ArrayList<StudentQuestionResponseExamBean>(); 
					
					if(q.getType() == 1 || q.getType() == 2 || q.getType() == 5 || q.getType() == 6  || q.getType() == 7 ) {
						if(answersByAttempt !=null) {
							for(StudentQuestionResponseExamBean a : answersByAttempt) {
								
								for(TestQuestionOptionExamBean o : q.getOptionsList()) {


									if(a.getAnswer().equalsIgnoreCase(o.getId().toString())) {


										o.setSelected("Y");
										q.setIsAttempted("Y");
									}
								}							
							
								
								if(q.getId().equals(a.getQuestionId())) {								
								 answerListByQuestionId.add(a);		
									
									}
								
									
										
							}
						}
					}else if(q.getType() == 3) {
						for(TestQuestionExamBean sq : q.getSubQuestionsList()) {
							if(answersByAttempt !=null) {
								for(StudentQuestionResponseExamBean a : answersByAttempt) {
									for(TestQuestionOptionExamBean o : sq.getOptionsList()) {


										if(a.getAnswer().equalsIgnoreCase(o.getId().toString())) {


											o.setSelected("Y");
											sq.setIsAttempted("Y");
											q.setIsAttempted("Y");
										
										}
									}
								}
							}
						}
						
					}else if(q.getType() == 4) {
						if(answersByAttempt !=null) {
							for(StudentQuestionResponseExamBean a : answersByAttempt) {



								


								if((q.getId()+"").equalsIgnoreCase((a.getQuestionId()+""))) {


									q.setIsAttempted("Y");
									q.setAnswer(a.getAnswer());
									q.setMarksObtained(a.getMarks());

									if("CopyCase".equalsIgnoreCase(b.getAttemptStatus())) {
										q.setRemarks(COPY_CASE_REMARK);
									}else {
										q.setRemarks(a.getRemark());	
									}

									q.setRemarks(a.getRemark());
								}else {
									q.setIsAttempted("N");

								}	
									
							}
						}
					
					
					}
					else if(q.getType() == 8) {
						if(answersByAttempt !=null) {
							for(StudentQuestionResponseExamBean a : answersByAttempt) {

								if((q.getId()+"").equalsIgnoreCase((a.getQuestionId()+""))) {

									q.setIsAttempted("Y");
									q.setAnswer(a.getAnswer());
									q.setMarksObtained(a.getMarks());
									q.setRemarks(a.getRemark());
								}else {
									q.setIsAttempted("N");
								}	
									
							}
							


						}
					
					
					}
				 
					try {
						

							 double score = dao.checkType1n2Question(q, answerListByQuestionId);
							
							if(q.getMarks() == score) {
								q.setStudentAnswerCorrect(1);
							}else {
								q.setStudentAnswerCorrect(0);
							}
							}catch(Exception e) {
								//
								logger.info("\n"+SERVER+": "+" IN getAttemptNoNQuestionsMap   got testId : "+testId+" sapId : "+sapId+", Error :  "+e.getMessage());
								
								
							}																
					
						answerListByQuestionId = new ArrayList<StudentQuestionResponseExamBean>();
						
				
				}
				attemptNoNQuestionsMap.put(b.getAttempt(), qList);
			}
			
			/*
			 * for(TestQuestionBean bean:attemptNoNQuestionsMap.get(1)) {

			 * }

			 */
			
			return attemptNoNQuestionsMap;
		}
		
		
		
		public List<TestQuestionExamBean> getQuestionsAsPerTestDetailsForLeads(TestExamBean test, String sapid, StudentsTestDetailsExamBean studentsTestDetailsCheck){
			MBAXIADAO dao = (MBAXIADAO)act.getBean("mbaxIADao");
			List<TestQuestionExamBean> testQuestions =new ArrayList<>();
			
			if(studentsTestDetailsCheck ==null || studentsTestDetailsCheck.getId() == null) {
				
				testQuestions =  dao.getTestQuestionsForLeads(test.getId());
				
			}else {


				testQuestions =  dao.getTestQuestionsWithOutQuestionFromOldAttmptForLeads(test.getId(),studentsTestDetailsCheck.getSapid());

					
			}


			
			HashMap<String,List<TestQuestionExamBean>> typeNQuestionMap= getTypeAndMarksKeyMapFromQuestions(testQuestions);
			if(testQuestions==null){
				return null;	
			}else{
				if(testQuestions.size() == 0){
					return null;	
				}
				List<TestQuestionExamBean> mandatoryQuestions = new ArrayList<>();
				List<TestQuestionExamBean> tempQuestions = new ArrayList<>();
				List<TestQuestionExamBean> questionsLeftAfterRemovingMandatory = new ArrayList<>();
				questionsLeftAfterRemovingMandatory.addAll(testQuestions);
				
				List<TestQuestionConfigBean> configs = dao.getQuestionConfigsListByTestIdForLeads(test.getId());
				

				
				for(TestQuestionConfigBean b : configs) {


					int minLimit = b.getMinNoOfQuestions();
					int maxLimit = b.getMaxNoOfQuestions();
					
					if(minLimit > 0) { //minLimit is set 
						if(maxLimit > 0 ) { //maxLimit is set too
							
							if(typeNQuestionMap.get(b.getType()+"-"+b.getQuestionMarks()) != null ) {
								tempQuestions = getQuestionsWithMinAndMaxLimitSet(typeNQuestionMap.get(b.getType()+"-"+b.getQuestionMarks()), minLimit, maxLimit);
								mandatoryQuestions.addAll(tempQuestions);
							}
							
						}else { //maxLimit is not set
							if(typeNQuestionMap.get(b.getType()+"-"+b.getQuestionMarks()) != null ) {
								tempQuestions = getQuestionsWithMinLimitSet(typeNQuestionMap.get(b.getType()+"-"+b.getQuestionMarks()), minLimit);
								mandatoryQuestions.addAll(tempQuestions);
								
							}
						}
					}else if( (minLimit < 1) && (maxLimit > 0) ) {
						if(typeNQuestionMap.get(b.getType()+"-"+b.getQuestionMarks()) != null ) {
							tempQuestions = getQuestionsWithMaxLimitSet(typeNQuestionMap.get(b.getType()+"-"+b.getQuestionMarks()),  maxLimit);
							mandatoryQuestions.addAll(tempQuestions);
							
						}
					}
				}
				questionsLeftAfterRemovingMandatory.removeAll(mandatoryQuestions);
				int mandatoryQuestionsSize = mandatoryQuestions.size();
				int maxQuestionsToShow = test.getMaxQuestnToShow();
				
				if(mandatoryQuestionsSize < maxQuestionsToShow ) {
					int questionToAdd = maxQuestionsToShow - mandatoryQuestionsSize;
					questionsLeftAfterRemovingMandatory = getRandomizedQuestions(questionsLeftAfterRemovingMandatory);
					mandatoryQuestions.addAll(questionsLeftAfterRemovingMandatory.subList(0, questionToAdd));
				}else if(mandatoryQuestionsSize > maxQuestionsToShow ) {

					mandatoryQuestions = mandatoryQuestions.subList(0, maxQuestionsToShow);
				} else {
				}
				
				testQuestions = mandatoryQuestions;
				


				
				return testQuestions;	
			}
		}

//		@RequestMapping(value = "/m/addStudentsQuestionResponseForLeads", method = RequestMethod.POST, consumes = "application/json", produces = "application/json; charset=UTF-8")
//		public ResponseEntity<HashMap<String,String>> m_addStudentsQuestionResponseForLeads(@RequestBody StudentQuestionResponseBean answer){
//			
//			logger.info("\n"+SERVER+": "+new Date()+" IN m_addStudentsQuestionResponseForLeads got sapid :  "+answer.getSapid()+" questionId : "+answer.getQuestionId()+". answer : "+answer.getAnswer());
//			
//			
//
//
//			HttpHeaders headers = new HttpHeaders();
//			headers.add("Content-Type", "application/json");
//			MBAXIADAO dao = (MBAXIADAO)act.getBean("mbaxIADao");
//			HashMap<String,String> response = new HashMap<>();
//			try {
//				
//				String updateAttemptsReaminingTime = updateAttemptsReaminingTimeForLeads(answer.getSapid(),answer.getTestId());
//				if(!StringUtils.isBlank(updateAttemptsReaminingTime)) {
//
//					response.put("Status", updateAttemptsReaminingTime);
//					return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
//				}
//				
//				//List<StudentQuestionResponseBean> answers =  dao.getTestAnswerBySapidAndQuestionId(answer.getSapid(), answer.getQuestionId(), answer.getAttempt());
//				
//				int noOfAnswersAlreadySaved = dao.getCountOfAnswersBySapidAndQuestionIdNAttemptForLeads(answer.getSapid(), answer.getQuestionId(), answer.getAttempt());
//				
//				if(noOfAnswersAlreadySaved == 0) {
//					//Do insert
//		
//					if(answer.getType() == 2) {
//						String savedAns=saveType2AnswersForLeads(answer);
//						if("error".equalsIgnoreCase(savedAns)) {
//							response.put("Status", "Fail in saveType2Answers ");
//							return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
//						}
//					}else {
//						long saved = dao.saveStudentsTestAnswerForLeads(answer);
//						if(saved==0) {
//							response.put("Status", "Fail in saveStudentsTestAnswer");
//							return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
//						}
//					}
//					
//					//update noOfQuestionsAttempted
//					boolean updateStudentsTestDetailsNoOfQuestionsAttempted = dao.updateStudentsTestDetailsNoOfQuestionsAttemptedForLeads(answer.getTestId(),answer.getSapid(),answer.getAttempt(),answer.getQuestionId());
//					if(!updateStudentsTestDetailsNoOfQuestionsAttempted) {
//						response.put("Status", "Fail in updateStudentsTestDetailsNoOfQuestionsAttempted ");
//						return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
//					}
//				}else {
//					//Do update
//					if(answer.getType() == 2) {
//						//if type is 2 i.e. multiselect delete old answers first
//						boolean deletedAns = dao.deleteStudentsAnswersBySapidQuestionIdForLeads(answer.getSapid(), answer.getQuestionId());
//						if(!deletedAns) {
//							response.put("Status", "Fail deleteStudentsAnswersBySapidQuestionId ");
//							return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
//						}
//						String savedAns = saveType2AnswersForLeads(answer);
//						if("error".equalsIgnoreCase(savedAns)) {
//							response.put("Status", "Fail saveType2Answers");
//							return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
//						}
//						
//					}else if(answer.getType() == 1 || answer.getType() == 3 || answer.getType() == 4 || answer.getType() == 5 || answer.getType() == 6 || answer.getType() == 7 || answer.getType() == 8) {
//					boolean updated = dao.updateStudentsQuestionResponseForLeads(answer);
//					if(!updated) {
//						response.put("Status", "Fail updateStudentsQuestionResponse ");
//						return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
//						}
//					}else {
//						response.put("Status", "Fail type no mentioned ");
//						return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
//						
//					}
//				}
//				//update currentQuestion
//				boolean updateStudentsTestDetailsCurrentQuestion = dao.updateStudentsTestDetailsCurrentQuestionForLeads(answer.getQuestionId(),answer.getTestId(),answer.getSapid(),answer.getAttempt());
//				if(!updateStudentsTestDetailsCurrentQuestion) {
//					response.put("Status", "Fail updateStudentsTestDetailsCurrentQuestion");
//					return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
//				}
//				response.put("Status", "Success");
//				return new ResponseEntity<>(response,headers, HttpStatus.OK);
//			}catch(Exception e) {
//
//				StringWriter errors = new StringWriter();
//				e.printStackTrace(new PrintWriter(errors));
//				String apiCalled = "studentTestController/m/addStudentsQuestionResponse";
//				String stackTrace = "apiCalled="+ apiCalled + ",data= StudentQuestionResponseBean: "+answer.toString() +
//						",errors=" + errors.toString();
//				dao.setObjectAndCallLogError(stackTrace,answer.getSapid());
//				response.put("Status", "Fail");
//				return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
//			}
//		}
//		
		
		public String updateAttemptsReaminingTimeForLeads(String sapId, Long testId) {

			MBAXIADAO dao = (MBAXIADAO)act.getBean("mbaxIADao");
			StudentsTestDetailsExamBean studentsTestDetails =  dao.getStudentsTestDetailsBySapidAndTestIdForLeads(sapId,testId);
			TestExamBean test = dao.getTestByIdForLeads(testId);
			//test = updateStartEndTimeIfExtended(test,sapId);
			
			Integer duration = test.getDuration();
			int remainingTime;
			
			//get time left im min
			try {
				String startDateTime = studentsTestDetails.getTestStartedOn();
				//String endDateTime = studentsTestDetails.getTestEndedOn();
				
				String testEndDateTimeString = test.getEndDate().replace('T', ' ');
				

				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date sDateTime = sdf.parse(startDateTime);
				Date testEndDateTime = sdf.parse(testEndDateTimeString);
				Date cDateTime = new Date();
				Date eDateTime = addMinutesToDate(duration.intValue(),sDateTime);
				
				//adding buffer to enddate to avoid saving error
				Date eDateTimeWithBuffer = addMinutesToDate(5,eDateTime);
				//Date testEndDateTimeWithBuffer = addMinutesToDate(5,testEndDateTime);
				

				if(cDateTime.before(eDateTimeWithBuffer)) {
					 remainingTime = differenceInMinutesBetweenTwoDates(cDateTime, eDateTime);

				}else {


					return "TimeOver! Your Test Was Started at "+startDateTime+". Test EndTime Was "+eDateTime+". Current Time : "+cDateTime;
				}
				
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//
				logger.info("\n"+SERVER+": "+" IN updateAttemptsReaminingTime  sapId:  "+sapId+" testId : "+testId+" Error : "+e.getMessage());
				
				return "Error in calculating remining time.";
			}
			
			//update into db
			boolean updateRemainingTIme = dao.updateStudentsTestDetailsRemainingTimeForLeads(remainingTime, studentsTestDetails.getId());
			if(updateRemainingTIme) {
				return "";
			}else {

				return "Error in updateing remaining time to db.";
			}
			
			
		}


		public String saveType2AnswersForLeads(StudentQuestionResponseExamBean answer) {
			try {
				MBAXIADAO dao = (MBAXIADAO)act.getBean("mbaxIADao");
				
				String[] studentsAnswers = answer.getAnswer().split("~",-1);
				//insert all answers onebyone
				for(int i = 0; i<studentsAnswers.length;i++) {

					answer.setAnswer(studentsAnswers[i]);
					long saved = dao.saveStudentsTestAnswerForLeads(answer);
					if(saved==0) {
						return "error";
					}
				}
				return "success";
			} catch (Exception e) {
				// TODO Auto-generated catch block
				

				return "error";
			}
		}
		
		@RequestMapping(value = "/deleteAnswerBySapIdQuestionIdForLeads", method = RequestMethod.POST, consumes = "application/json", produces = "application/json; charset=UTF-8")
		public ResponseEntity<HashMap<String,String>> deleteAnswerBySapIdQuestionIdForLeads(@RequestBody StudentQuestionResponseExamBean answer){
			


			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");
			MBAXIADAO dao = (MBAXIADAO)act.getBean("mbaxIADao");
			HashMap<String,String> response = new HashMap<>();
			try {
					boolean deletedAns = dao.deleteStudentsAnswersBySapidQuestionIdForLeads(answer.getSapid(), answer.getQuestionId());
					if(!deletedAns) {
					
						response.put("Status", "Fail");
						return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
					}
					boolean updatedAns = dao.updateNoOfQuestionAttemptedBySapidTestIdForLeads(answer.getSapid(), answer.getTestId(),answer.getAttempt());
					if(!updatedAns) {
					
						response.put("Status", "Fail");
						return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
					}
			
					response.put("Status", "Success");
					return new ResponseEntity<>(response,headers, HttpStatus.OK);
				}catch(Exception e) {

					StringWriter errors = new StringWriter();
					e.printStackTrace(new PrintWriter(errors));
					String apiCalled = "studentTestController/deleteAnswerBySapIdQuestionIdForLeads";
					String stackTrace = "apiCalled="+ apiCalled +  ",data= StudentQuestionResponseBean: "+ answer.toString()  +
							",errors=" + errors.toString();
					dao.setObjectAndCallLogError(stackTrace,answer.getSapid());
					response.put("Status", "Fail");
					return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
				}
		}

		
//		@RequestMapping(value = "/m/saveStudentsTestDetailsForLeads", method = RequestMethod.POST, consumes = "application/json", produces = "application/json; charset=UTF-8")
//		public ResponseEntity<HashMap<String,String>> m_saveStudentsTestDetailsForLeads(@RequestBody StudentsTestDetailsBean bean){
//			
//
//			logger.info("\n"+SERVER+": "+new Date()+" IN m_saveStudentsTestDetailsForLeads got sapid :  "+bean.getSapid()+" testId : "+bean.getTestId());
//			
//				HttpHeaders headers = new HttpHeaders();
//				headers.add("Content-Type", "application/json");
//				MBAXIADAO dao = (MBAXIADAO)act.getBean("mbaxIADao");
//				HashMap<String,String> response = new HashMap<>();
//				try {
//					StudentsTestDetailsBean studentsTestDetails =  dao.getStudentsTestDetailsBySapidAndTestIdForLeads(bean.getSapid(), bean.getTestId());
//				
//					if(studentsTestDetails.getId() != null) {
//						//Do update
//						studentsTestDetails.setAttempt(studentsTestDetails.getAttempt());
//						studentsTestDetails.setTestCompleted("Y");
//						studentsTestDetails.setLastModifiedBy(bean.getSapid());
//						studentsTestDetails.setScore(0);
//						
//						try {
//							studentsTestDetails.setScore(dao.caluclateTestScoreForLeads(bean.getSapid(),bean.getTestId()));
//							studentsTestDetails.setShowResult("Y");
//						} catch (Exception e) {
//							// TODO Auto-generated catch block
//							
//							studentsTestDetails.setScore(0);
//							studentsTestDetails.setShowResult("N");
//						}
//						
//						boolean updated = dao.updateStudentsTestDetailsForLeads(studentsTestDetails);
//						if(!updated) {
//							response.put("Status", "Fail");
//							return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
//						}
//						
//
//						//Send mail after test is completed start
//						try {
//								MailSender mailSender = (MailSender)act.getBean("mailer");
//								StudentMarksDAO sMarksDao = (StudentMarksDAO)act.getBean("studentMarksDAO");
//								//StudentBean student = sMarksDao.getSingleStudentsData(bean.getSapid());
//								StudentBean student = dao.getLeadsDataByLeadId(bean.getSapid());
//								student.setSapid(student.getLeadId());
//								TestBean test = dao.getTestByIdForLeads(bean.getTestId());
//								StudentsTestDetailsBean studentsTestDetailsForMail =  dao.getStudentsTestDetailsBySapidAndTestIdForLeads(bean.getSapid(), bean.getTestId());
//								
//								mailSender.sendTestEndedEmailForLeads(student,test,studentsTestDetailsForMail);
//							
//						} catch (Exception e) {
//							//
//							//mailSender.mailStackTrace("Error in Saving Successful Transaction", e);
//							logger.info("\n"+SERVER+": "+"IN Send mail error got sapid: "+studentsTestDetails.getSapid()+" testId: "+bean.getId()+" Error: "+e.getMessage());
//							
//						}
//						//Send mail after test is completed end
//						
//						
//					}else {			
//						response.put("Status", "Fail");
//						return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
//					}
//					response.put("Status", "Success");
//					
//			
//					return new ResponseEntity<>(response,headers, HttpStatus.OK);
//				}catch(Exception e) {
//
//
//
//					StringWriter errors = new StringWriter();
//					e.printStackTrace(new PrintWriter(errors));
//					String apiCalled = "studentTestController/m/saveStudentsTestDetailsForLeads";
//					String stackTrace = "apiCalled="+ apiCalled + ",data= testId: "+ bean.getTestId()+ 
//							",errors=" + errors.toString();
//					dao.setObjectAndCallLogError(stackTrace,bean.getSapid());
//					response.put("Status", "Fail");
//					return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
//				}
//			
//		}


		@RequestMapping(value = "/studentsTestPreviewForAllViews", method =  RequestMethod.GET)
		public String studentsTestPreviewForAllViews(HttpServletRequest request,
							   HttpServletResponse response,
							   Model m,
							   @RequestParam("id") Long id,
							   @RequestParam("message") String message,
							   @RequestParam("userId") String userId,
							   @RequestParam(name = "consumerProgramStructureId", defaultValue = "111") Integer consumerProgramStructureId
							   ){
			
			try {
				MBAXIADAO dao = (MBAXIADAO)act.getBean("mbaxIADao");
				TestExamBean beanForUpdateExtendTime = new TestExamBean();
				beanForUpdateExtendTime.setSapid(userId);
				beanForUpdateExtendTime.setTestId(id);
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				
				beanForUpdateExtendTime.setTestStartedOn(sdf.format(new Date()));
				
				dao.updateDate(beanForUpdateExtendTime);
			}catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return viewTestDetailsForStudentsForAllViews(request,response,m,
					        id,
					        message,
					        userId,
					        consumerProgramStructureId
					   );

		}

}
