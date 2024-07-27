package com.nmims.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
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
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.AssignmentFileBean;
import com.nmims.beans.AssignmentHistoryResponseBean;
import com.nmims.beans.AssignmentLiveSetting;
import com.nmims.beans.ExamAssignmentResponseBean;
import com.nmims.beans.Page;
import com.nmims.beans.Person;
import com.nmims.beans.ProgramSubjectMappingExamBean;
import com.nmims.beans.ResponseBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.daos.AssignmentsDAO;
import com.nmims.daos.ExamBookingDAO;
import com.nmims.daos.PassFailDAO;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.daos.TestDAOForRedis;
import com.nmims.helpers.AESencrp;
import com.nmims.helpers.FileScanner;
import com.nmims.helpers.FileUploadHelper;
import com.nmims.helpers.MailSender;
import com.nmims.services.AssignmentService;
import com.nmims.services.StudentService;
import com.nmims.beans.AssignmentHistoryResponseBean; 
import com.nmims.beans.AssignmentLiveSetting;
import com.nmims.helpers.AmazonS3Helper;
import com.nmims.helpers.ExamBookingHelper;
@Controller
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AssignmentSubmissionController extends BaseController {

	@Autowired
	ApplicationContext act;

	@Autowired
	StudentService studentService;

	@Value("${ASSIGNMENT_FILES_PATH}")
	private String ASSIGNMENT_FILES_PATH;

	@Value("${SUBMITTED_ASSIGNMENT_FILES_PATH}")
	private String SUBMITTED_ASSIGNMENT_FILES_PATH;

	@Value("${ASSIGNMENT_SUBMISSIONS_ATTEMPTS}")
	private String ASSIGNMENT_SUBMISSIONS_ATTEMPTS;

	@Autowired
	AssignmentService asgService;
	
	private static final int BUFFER_SIZE = 4096;
	private static final Logger logger = LoggerFactory.getLogger(AssignmentSubmissionController.class);
	private static final Logger aws_logger = LoggerFactory.getLogger("fileMigrationService");
	private static final Logger assg_logger = LoggerFactory.getLogger("assignmentSubmission");
	
	@Value("#{'${CURRENT_YEAR_LIST}'.split(',')}")
	private ArrayList<String> currentYearList;

	private ArrayList<String> subjectList = null;
	private final int pageSize = 20;
	private ArrayList<ProgramSubjectMappingExamBean> programSubjectMappingList = null;
	private static final Long MAX_FILE_SIZE_LIMIT = 6291456L;				//6 MB in bytes (6 * 1024 * 1024)
	@Value("${SERVER_PATH}")
	private String SERVER_PATH;

	@Autowired
	AmazonS3Helper amazonS3Helper;
	
	@Autowired
	FileUploadHelper fileUploadHelper;
	
	/**
	 * Refresh Cache function to refresh cache
	 * 
	 * @param none
	 * @return none
	 */
	public String RefreshCache() {
		programSubjectMappingList = null;
		getProgramSubjectMappingList();

		subjectList = null;
		getSubjectList();

		return null;
	}

	public ArrayList<String> getSubjectList() {
		if (this.subjectList == null) {
			StudentMarksDAO dao = (StudentMarksDAO) act.getBean("studentMarksDAO");
			this.subjectList = dao.getActiveSubjects();
		}
		return subjectList;
	}

	public AssignmentSubmissionController() {
	}

	public ArrayList<ProgramSubjectMappingExamBean> getProgramSubjectMappingList() {
		if (this.programSubjectMappingList == null || this.programSubjectMappingList.size() == 0) {
			ExamBookingDAO eDao = (ExamBookingDAO) act.getBean("examBookingDAO");
			this.programSubjectMappingList = eDao.getProgramSubjectMappingList();
		}
		return programSubjectMappingList;
	}

	@RequestMapping(value = "/student/viewPreviousAssignments", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView viewPreviousAssignments(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute AssignmentFileBean searchBean) {
		ModelAndView modelnView = new ModelAndView("assignment/viewPreviousAssignmentFilesDemo");
		if (!checkSession(request, response)) {
			redirectToPortalApp(response);
			return null;
		}
		StudentExamBean student = (StudentExamBean) request.getSession().getAttribute("studentExam");
		searchBean.setSapId(student.getSapid());
		searchBean.setConsumerProgramStructureId(student.getConsumerProgramStructureId());
		request.getSession().setAttribute("searchBean", searchBean);
		AssignmentsDAO dao = (AssignmentsDAO) act.getBean("asignmentsDAO");
		Page<AssignmentFileBean> page = dao.getAssignmentSubmissionPage(1, Integer.MAX_VALUE, searchBean);
		List<AssignmentFileBean> assignmentFilesList = page.getPageItems();
		modelnView.addObject("assignmentFilesList", assignmentFilesList);
		modelnView.addObject("page", page);
		modelnView.addObject("searchBean", searchBean);
		modelnView.addObject("rowCount", page.getRowCount());
		modelnView.addObject("yearList", currentYearList);
		modelnView.addObject("subjectList", getSubjectList());
		if (assignmentFilesList == null || assignmentFilesList.size() == 0) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No Assignment Submissions found.");
		}
		return modelnView;
	}

//	@RequestMapping(value = "/m/viewPreviousAssignments", method = { RequestMethod.GET, RequestMethod.POST })
//	public ResponseEntity<AssignmentHistoryResponseBean> mViewPreviousAssignments(HttpServletRequest request,
//			@RequestBody Person input) {
//
//		AssignmentHistoryResponseBean response = new AssignmentHistoryResponseBean();
//
//		AssignmentsDAO dao = (AssignmentsDAO) act.getBean("asignmentsDAO");
//
//		AssignmentFileBean searchBean = new AssignmentFileBean();
//		searchBean.setSapId(input.getSapId());
//		Page<AssignmentFileBean> page = dao.getAssignmentSubmissionPage(1, Integer.MAX_VALUE, searchBean);
//		List<AssignmentFileBean> assignmentFilesList = page.getPageItems();
//		if (assignmentFilesList == null || assignmentFilesList.size() == 0) {
//			response.setError("true");
//			response.setErrorMessage("No Assignment Submissions found.");
//		} else {
//			response.setError("false");
//			response.setData(assignmentFilesList);
//		}
//		return new ResponseEntity<AssignmentHistoryResponseBean>(response, HttpStatus.OK);
//	}

	/*
	 * @RequestMapping(value = "/viewAssignmentsForm", method = {RequestMethod.GET})
	 * public ModelAndView viewAssignmentsForm(HttpServletRequest request,
	 * HttpServletResponse response, Model m) {
	 * 
	 * if(!checkSession(request, response)){ redirectToPortalApp(response); return
	 * null; } String sapId = (String)request.getSession().getAttribute("userId");
	 * ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
	 * //StudentBean student = eDao.getSingleStudentsData(sapId); StudentMarksDAO
	 * sMarksDao = (StudentMarksDAO)act.getBean("studentMarksDAO"); StudentBean
	 * student = (StudentBean)request.getSession().getAttribute("student");
	 * AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
	 * 
	 * boolean isOnline = isOnline(student);
	 * 
	 * ArrayList<String> currentSemSubjects = new ArrayList<>(); ArrayList<String>
	 * failSubjects = new ArrayList<>(); ArrayList<String> applicableSubjects = new
	 * ArrayList<>();
	 * 
	 * List<AssignmentFileBean> failSubjectsAssignmentFilesList = new ArrayList<>();
	 * List<AssignmentFileBean> currentSemAssignmentFilesList = new ArrayList<>();
	 * 
	 * HashMap<String, String> subjectSemMap = new HashMap<>(); int
	 * currentSemSubmissionCount = 0; int failSubjectSubmissionCount = 0;
	 * 
	 * 
	 * StudentBean studentRegistrationData = dao.getStudentRegistrationData(sapId);
	 * String currentSem = null;
	 * 
	 * int maxAttempts = Integer.parseInt(ASSIGNMENT_SUBMISSIONS_ATTEMPTS);
	 * m.addAttribute("yearList", currentYearList); m.addAttribute("subjectList",
	 * getSubjectList());
	 * 
	 * if(student == null){ request.setAttribute("error", "true");
	 * request.setAttribute("errorMessage",
	 * "Student Enrollment not found. Please contact Administrator."); return new
	 * ModelAndView("assignment/viewAssignmentFiles"); }else{
	 * if(student.getPreviousStudentId() != null &&
	 * !"".equals(student.getPreviousStudentId()) &&
	 * !"Jul2009".equals(student.getPrgmStructApplicable()) ){ PassFailDAO pdao =
	 * (PassFailDAO)act.getBean("passFailDAO");
	 * student.setWaivedOffSubjects(pdao.getPassSubjectsNamesForAStudent(student.
	 * getPreviousStudentId())); } request.getSession().setAttribute("student",
	 * student); }
	 * 
	 * if(studentRegistrationData != null){ //Take program from Registration data
	 * and not Student data.
	 * student.setProgram(studentRegistrationData.getProgram());
	 * student.setSem(studentRegistrationData.getSem()); currentSem =
	 * studentRegistrationData.getSem(); currentSemSubjects =
	 * getSubjectsForStudent(student, subjectSemMap); }
	 * 
	 * failSubjects = new ArrayList<>(); //if((currentSem != null &&
	 * (!"1".equals(currentSem))) || studentRegistrationData == null){ //If current
	 * semester is 1, then there cannot be any failed subjects
	 * 
	 * ArrayList<AssignmentFileBean> failSubjectsBeans = getFailSubjects(student);
	 * if(failSubjectsBeans != null && failSubjectsBeans.size() > 0){
	 * 
	 * for (int i = 0; i < failSubjectsBeans.size(); i++) { String subject =
	 * failSubjectsBeans.get(i).getSubject(); String sem =
	 * failSubjectsBeans.get(i).getSem();
	 * failSubjects.add(failSubjectsBeans.get(i).getSubject());
	 * subjectSemMap.put(subject, sem); } }
	 * 
	 * //}
	 * 
	 * ArrayList<AssignmentFileBean> failANSSubjectsBeans =
	 * getANSNotProcessed(student); if(failANSSubjectsBeans != null &&
	 * failANSSubjectsBeans.size() > 0){
	 * 
	 * for (int i = 0; i < failANSSubjectsBeans.size(); i++) { String subject =
	 * failANSSubjectsBeans.get(i).getSubject(); String sem =
	 * failANSSubjectsBeans.get(i).getSem();
	 * failSubjects.add(failANSSubjectsBeans.get(i).getSubject());
	 * subjectSemMap.put(subject, sem); } }
	 * 
	 * ArrayList<AssignmentFileBean> currentSemResultAwaitedSubjectsList =
	 * dao.getCurrentSemResultAwaitedAssignmentSubmittedSubjectsList(student.
	 * getSapid());
	 * 
	 * for (AssignmentFileBean assignmentFileBean :
	 * currentSemResultAwaitedSubjectsList){ String subject =
	 * assignmentFileBean.getSubject(); if(!failSubjects.contains(subject)){
	 * failSubjects.add(subject); } }
	 * 
	 * for (String failedSubject : failSubjects) { //For ANS cases, where result is
	 * not declared, failed subject will also be present in Current sem subject.
	 * //Give preference to it as Failed, so that assignment can be submitted and
	 * remove from Current list if(currentSemSubjects.contains(failedSubject)){
	 * currentSemSubjects.remove(failedSubject); } }
	 * 
	 * currentSemSubjects.remove("Project"); failSubjects.remove("Project");
	 * 
	 * applicableSubjects.addAll(currentSemSubjects);
	 * applicableSubjects.addAll(failSubjects);
	 * applicableSubjects.remove("Project");
	 * 
	 * 
	 * request.getSession().setAttribute("subjectsForStudent", applicableSubjects);
	 * List<AssignmentFileBean> allAssignmentFilesList = new ArrayList<>();
	 * if(!isOnline){ allAssignmentFilesList =
	 * dao.getAssignmentsForSubjects(applicableSubjects, student); }else{
	 * List<AssignmentFileBean> currentSemFiles = null; List<AssignmentFileBean>
	 * failSubjectFiles = null; if(currentSemSubjects != null &&
	 * currentSemSubjects.size()>0){ currentSemFiles =
	 * dao.getAssignmentsForSubjects(currentSemSubjects, student); } if(failSubjects
	 * != null && failSubjects.size()>0){ failSubjectFiles =
	 * dao.getResitAssignmentsForSubjects(failSubjects, student); }
	 * 
	 * if(currentSemFiles != null){ allAssignmentFilesList.addAll(currentSemFiles);
	 * }
	 * 
	 * if(failSubjectFiles != null){
	 * 
	 * allAssignmentFilesList.addAll(failSubjectFiles); } }
	 * 
	 * if(allAssignmentFilesList != null ){
	 * 
	 * HashMap<String,AssignmentFileBean> subjectSubmissionMap = new HashMap<>();
	 * if(!isOnline){ subjectSubmissionMap =
	 * dao.getSubmissionStatus(applicableSubjects, sapId);//Assignments from Jun,
	 * Dec cycle }else{ //For online, resit i.e. fail subjects paper change after
	 * resit date is over.//Applicable cycle is all 4 i.e. Apr, Jun, Sep, Dec
	 * HashMap<String,AssignmentFileBean> currentSemSubjectSubmissionMap =
	 * dao.getSubmissionStatus(currentSemSubjects, sapId);
	 * HashMap<String,AssignmentFileBean> failSubjectSubmissionMap =
	 * dao.getResitSubmissionStatus(failSubjects, student);
	 * 
	 * if(currentSemSubjectSubmissionMap != null){
	 * subjectSubmissionMap.putAll(currentSemSubjectSubmissionMap); }
	 * 
	 * if(failSubjectSubmissionMap != null){
	 * subjectSubmissionMap.putAll(failSubjectSubmissionMap); } }
	 * for(AssignmentFileBean assignment : allAssignmentFilesList){ String subject =
	 * assignment.getSubject(); String status = "Not Submitted"; String attempts =
	 * "0"; String lastModifiedDate = "";
	 * 
	 * String pastCycleAssignmentDetails = "";
	 * 
	 * AssignmentFileBean studentSubmissionStatus =
	 * subjectSubmissionMap.get(subject); if(studentSubmissionStatus != null){
	 * status = studentSubmissionStatus.getStatus(); attempts =
	 * studentSubmissionStatus.getAttempts(); lastModifiedDate =
	 * studentSubmissionStatus.getLastModifiedDate(); lastModifiedDate =
	 * lastModifiedDate.replaceAll("T", " "); lastModifiedDate =
	 * lastModifiedDate.substring(0,19); }
	 * 
	 * assignment.setStatus(status); assignment.setAttempts(attempts);
	 * assignment.setAttemptsLeft((maxAttempts - Integer.parseInt(attempts))+"");
	 * assignment.setSem(subjectSemMap.get(subject));
	 * assignment.setLastModifiedDate(lastModifiedDate);
	 * 
	 * if(failSubjects.contains(subject)){
	 * failSubjectsAssignmentFilesList.add(assignment);
	 * if("Submitted".equals(status)){ failSubjectSubmissionCount++; } }else{
	 * currentSemAssignmentFilesList.add(assignment);
	 * if("Submitted".equals(status)){ currentSemSubmissionCount++; } } } }
	 * 
	 * String yearMonth = dao.getMostRecentAssignmentSubmissionPeriod();
	 * m.addAttribute("yearMonth", yearMonth);
	 * 
	 * String currentSemEndDateTime = ""; String failSubjectsEndDateTime = "";
	 * 
	 * 
	 * m.addAttribute("currentSemAssignmentFilesList",currentSemAssignmentFilesList)
	 * ; int currentSemSubjectsCount = (currentSemAssignmentFilesList == null ? 0 :
	 * currentSemAssignmentFilesList.size());
	 * m.addAttribute("currentSemSubjectsCount", currentSemSubjectsCount);
	 * m.addAttribute("currentSemSubmissionCount", currentSemSubmissionCount);
	 * 
	 * m.addAttribute("failSubjectsAssignmentFilesList",
	 * failSubjectsAssignmentFilesList); int failSubjectsCount =
	 * (failSubjectsAssignmentFilesList == null ? 0 :
	 * failSubjectsAssignmentFilesList.size()); m.addAttribute("failSubjectsCount",
	 * failSubjectsCount); m.addAttribute("failSubjectSubmissionCount",
	 * failSubjectSubmissionCount);
	 * 
	 * if(currentSemSubjectsCount > 0){ currentSemEndDateTime =
	 * currentSemAssignmentFilesList.get(0).getEndDate().substring(0, 19); }
	 * if(failSubjectsCount > 0){
	 * 
	 * 
	 * ArrayList<String> subjectsNotAllowedToSubmit = new ArrayList<String>();
	 * ArrayList<String> failedSubjectsSubmittedInLastCycle =
	 * dao.getFailedSubjectsSubmittedInLastCycle(sapId, failSubjects);
	 * ArrayList<String> failedSubjectsExamBookedInLastCycle =
	 * dao.getFailedSubjectsExamBookedInLastCycle(sapId, failSubjects);
	 * 
	 * if(failedSubjectsSubmittedInLastCycle.size() > 0 ||
	 * failedSubjectsExamBookedInLastCycle.size() > 0){ //There are failed subjects
	 * submitted in last submission cycle //Check if result is live for last
	 * submission cycle boolean isResultLiveForLastSubmissionCycle =
	 * sMarksDao.isResultLiveForLastAssignmentSubmissionCycle();
	 * if(!isResultLiveForLastSubmissionCycle){ //If result is not live then
	 * subjects submitted in last cycle cannot be submitted till results are live
	 * subjectsNotAllowedToSubmit.addAll(failedSubjectsSubmittedInLastCycle);
	 * subjectsNotAllowedToSubmit.addAll(failedSubjectsExamBookedInLastCycle); } }
	 * 
	 * 
	 * for (AssignmentFileBean assignmentFileBean : failSubjectsAssignmentFilesList)
	 * { if(subjectsNotAllowedToSubmit.contains(assignmentFileBean.getSubject())){
	 * assignmentFileBean.setSubmissionAllowed(false); }else{
	 * assignmentFileBean.setSubmissionAllowed(true); }
	 * 
	 * 
	 * int pastCycleAssignmentAttempts =
	 * dao.getPastCycleAssignmentAttempts(assignmentFileBean.getSubject(), sapId);
	 * if(pastCycleAssignmentAttempts >=2 &&
	 * !"Submitted".equals(assignmentFileBean.getStatus())){
	 * assignmentFileBean.setPaymentApplicable("Yes"); boolean hasPaidForAssignment
	 * = dao.checkIfAssignmentFeesPaid(assignmentFileBean.getSubject(), sapId);
	 * ////check if Assignment Fee Paid for Current drive if(!hasPaidForAssignment){
	 * assignmentFileBean.setPaymentDone("No"); }else{
	 * assignmentFileBean.setPaymentDone("Yes"); } }
	 * 
	 * } failSubjectsEndDateTime =
	 * failSubjectsAssignmentFilesList.get(0).getEndDate().substring(0, 19);
	 * //Offline Students dont have limits on their attempts//
	 * if("Offline".equals(student.getExamMode())){ for (AssignmentFileBean
	 * assignmentFileBean : failSubjectsAssignmentFilesList) {
	 * 
	 * assignmentFileBean.setPaymentDone("Yes"); } } }
	 * 
	 * 
	 * m.addAttribute("currentSemEndDateTime", currentSemEndDateTime);
	 * m.addAttribute("failSubjectsEndDateTime", failSubjectsEndDateTime);
	 * 
	 * if((currentSemSubjectsCount + failSubjectsCount)== 0){ setError(request,
	 * "No Assignments allocated to you."); }
	 * 
	 * ArrayList<String> timeExtendedStudentIdSubjectList =
	 * dao.assignmentExtendedSubmissionTime();
	 * m.addAttribute("timeExtendedStudentIdSubjectList",
	 * timeExtendedStudentIdSubjectList);
	 * request.getSession().setAttribute("timeExtendedStudentIdSubjectList",
	 * timeExtendedStudentIdSubjectList);
	 * 
	 * return new ModelAndView("assignment/viewAssignmentFiles"); }
	 */

	/*
	 * @RequestMapping(value = "/viewAssignmentsForm", method = {RequestMethod.GET})
	 * public ModelAndView viewAssignmentsForm(HttpServletRequest request,
	 * HttpServletResponse response, Model m) {
	 * 
	 * if(!checkSession(request, response)){ redirectToPortalApp(response); return
	 * null; }
	 * 
	 * TestDAOForRedis daoForRedis =
	 * (TestDAOForRedis)act.getBean("testDaoForRedis");
	 * if(daoForRedis.checkForFlagValueInCache("movingResultsToCache","Y") ) {
	 * return new ModelAndView("noDataAvailable"); }
	 * 
	 * String sapId = (String)request.getSession().getAttribute("userId");
	 * StudentMarksDAO sMarksDao = (StudentMarksDAO)act.getBean("studentMarksDAO");
	 * StudentBean student =
	 * (StudentBean)request.getSession().getAttribute("student");
	 * if("Offline".equalsIgnoreCase(student.getExamMode())) { return
	 * viewPreviousAssignments(request,response, new AssignmentFileBean()); }
	 * AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
	 * PassFailDAO pdao = (PassFailDAO)act.getBean("passFailDAO"); boolean isOnline
	 * = isOnline(student);
	 * 
	 * ArrayList<String> currentSemSubjects = new ArrayList<>(); ArrayList<String>
	 * failSubjects = new ArrayList<>(); ArrayList<String> applicableSubjects = new
	 * ArrayList<>(); ArrayList<String> ANSSubjects = new ArrayList<>();
	 * 
	 * List<AssignmentFileBean> failSubjectsAssignmentFilesList = new ArrayList<>();
	 * List<AssignmentFileBean> currentSemAssignmentFilesList = new ArrayList<>();
	 * 
	 * HashMap<String, String> subjectSemMap = new HashMap<>(); int
	 * currentSemSubmissionCount = 0; int failSubjectSubmissionCount = 0;
	 * StudentBean studentRegistrationData = new StudentBean();
	 * if("Diageo".equalsIgnoreCase(student.getConsumerType())) { // temp fix for
	 * diageo students studentRegistrationData =
	 * dao.getDiageoStudentRegistrationData(sapId); }else { studentRegistrationData
	 * = dao.getStudentRegistrationData(sapId); }
	 * 
	 * String currentSem = null;
	 * 
	 * int maxAttempts = Integer.parseInt(ASSIGNMENT_SUBMISSIONS_ATTEMPTS);
	 * m.addAttribute("yearList", currentYearList); m.addAttribute("subjectList",
	 * getSubjectList());
	 * 
	 * if(student == null){ request.setAttribute("error", "true");
	 * request.setAttribute("errorMessage",
	 * "Student Enrollment not found. Please contact Administrator."); return new
	 * ModelAndView("assignment/viewAssignmentFiles"); }else{ // Removed Waived Off
	 * logic from here in favor of the common logic in student helper.
	 * studentService.mgetWaivedOffSubjects(student);
	 * request.getSession().setAttribute("student", student); }
	 * 
	 * if(studentRegistrationData != null){ //Take program from Registration data
	 * and not Student data.
	 * student.setProgram(studentRegistrationData.getProgram());
	 * student.setSem(studentRegistrationData.getSem()); currentSem =
	 * studentRegistrationData.getSem(); currentSemSubjects =
	 * getSubjectsForStudent(student, subjectSemMap); }
	 * 
	 * //waived in subjects ArrayList<String> waivedInSubjects =
	 * student.getWaivedInSubjects(); if(currentSemSubjects != null) {
	 * if(waivedInSubjects != null) { for(String subject : waivedInSubjects) {
	 * if(!currentSemSubjects.contains(subject)) { currentSemSubjects.add(subject);
	 * } } } }else { currentSemSubjects = waivedInSubjects; }
	 * 
	 * 
	 * ArrayList<String> passSubjectsList = getPassSubjects(student,pdao);
	 * if(!passSubjectsList.isEmpty() && passSubjectsList != null){ for(String
	 * subject:passSubjectsList){ if(currentSemSubjects.contains(subject)){
	 * currentSemSubjects.remove(subject); } } }
	 * 
	 * 
	 * failSubjects = new ArrayList<>(); //if((currentSem != null &&
	 * (!"1".equals(currentSem))) || studentRegistrationData == null){ //If current
	 * semester is 1, then there cannot be any failed subjects
	 * 
	 * ArrayList<AssignmentFileBean> failSubjectsBeans = getFailSubjects(student);
	 * 
	 * if(student.getProgram().equals("BBA") ||
	 * student.getProgram().equals("B.Com")) { ArrayList<AssignmentFileBean>
	 * softSkillsFailSubjectsBeans = getUGFailSubjects(student);
	 * if(softSkillsFailSubjectsBeans != null && softSkillsFailSubjectsBeans.size()
	 * > 0){
	 * 
	 * for (AssignmentFileBean bean : softSkillsFailSubjectsBeans) { String subject
	 * = bean.getSubject(); String sem = bean.getSem();
	 * failSubjects.add(bean.getSubject()); subjectSemMap.put(subject, sem);
	 * 
	 * if("ANS".equalsIgnoreCase(bean.getRemarks())){ ANSSubjects.add(subject); } }
	 * }
	 * 
	 * List<String> ugPassSubjectsList = getUGPassSubjects(student); for(String
	 * subject:ugPassSubjectsList){ if(currentSemSubjects.contains(subject)){
	 * currentSemSubjects.remove(subject); } } }
	 * 
	 * if(failSubjectsBeans != null && failSubjectsBeans.size() > 0){
	 * 
	 * for (AssignmentFileBean bean : failSubjectsBeans) { String subject =
	 * bean.getSubject(); String sem = bean.getSem();
	 * failSubjects.add(bean.getSubject()); subjectSemMap.put(subject, sem);
	 * 
	 * if("ANS".equalsIgnoreCase(bean.getAssignmentscore())){
	 * ANSSubjects.add(subject); } } }
	 * 
	 * //}
	 * 
	 * ArrayList<AssignmentFileBean> failANSSubjectsBeans =
	 * getANSNotProcessed(student); if(failANSSubjectsBeans != null &&
	 * failANSSubjectsBeans.size() > 0){
	 * 
	 * for (int i = 0; i < failANSSubjectsBeans.size(); i++) { String subject =
	 * failANSSubjectsBeans.get(i).getSubject(); String sem =
	 * failANSSubjectsBeans.get(i).getSem();
	 * failSubjects.add(failANSSubjectsBeans.get(i).getSubject());
	 * subjectSemMap.put(subject, sem);
	 * 
	 * ANSSubjects.add(subject); } } ArrayList<AssignmentFileBean>
	 * currentSemResultAwaitedSubjectsList = new ArrayList<AssignmentFileBean>();
	 * 
	 * //Check if result is live for last submission cycle boolean
	 * isResultLiveForLastSubmissionCycle =
	 * sMarksDao.isResultLiveForLastAssignmentSubmissionCycle(); ArrayList<String>
	 * subjectsNotAllowedToSubmit = new ArrayList<String>();
	 * if(!isResultLiveForLastSubmissionCycle){ Commented so that current sem
	 * subjects should not show in ANS/Results Awaited/Failed Subjects Table
	 * currentSemResultAwaitedSubjectsList =
	 * dao.getResultAwaitedAssignmentSubmittedSubjectsList(student.getSapid()); for
	 * (AssignmentFileBean assignmentFileBean :
	 * currentSemResultAwaitedSubjectsList){ String subject =
	 * assignmentFileBean.getSubject(); if(!failSubjects.contains(subject)){
	 * failSubjects.add(subject); } }
	 * 
	 * ArrayList<String> subjectsSubmittedInLastCycle =
	 * dao.getFailedSubjectsSubmittedInLastCycle(sapId, failSubjects);
	 * ArrayList<String> subjectsExamBookedInLastCycle =
	 * dao.getFailedSubjectsExamBookedInLastCycle(sapId, failSubjects);
	 * 
	 * 
	 * for (String subject : subjectsSubmittedInLastCycle) {
	 * ANSSubjects.remove(subject); } if(subjectsSubmittedInLastCycle.size() > 0 ||
	 * subjectsExamBookedInLastCycle.size() > 0){ //There are failed subjects
	 * submitted in last submission cycle
	 * 
	 * //If result is not live then subjects submitted in last cycle cannot be
	 * submitted till results are live
	 * subjectsNotAllowedToSubmit.addAll(subjectsSubmittedInLastCycle);
	 * subjectsNotAllowedToSubmit.addAll(subjectsExamBookedInLastCycle); }
	 * 
	 * ArrayList<String> subjectsExamBookedInLastCycleANS = new ArrayList<String>();
	 * for(String subject:subjectsExamBookedInLastCycle ){
	 * if(ANSSubjects.contains(subject)){
	 * subjectsExamBookedInLastCycleANS.add(subject); } } for(String subject :
	 * subjectsExamBookedInLastCycleANS){
	 * if(subjectsNotAllowedToSubmit.contains(subject)){
	 * subjectsNotAllowedToSubmit.remove(subject); } } }
	 * 
	 * 
	 * 
	 * 
	 * for (String failedSubject : failSubjects) { //For ANS cases, where result is
	 * not declared, failed subject will also be present in Current sem subject.
	 * //Give preference to it as Failed, so that assignment can be submitted and
	 * remove from Current list if(currentSemSubjects.contains(failedSubject)){
	 * currentSemSubjects.remove(failedSubject); } }
	 * 
	 * currentSemSubjects.remove("Project");
	 * currentSemSubjects.remove("Module 4 - Project");
	 * 
	 * failSubjects.remove("Project"); failSubjects.remove("Module 4 - Project");
	 * applicableSubjects.addAll(currentSemSubjects);
	 * applicableSubjects.addAll(failSubjects);
	 * applicableSubjects.remove("Project");
	 * applicableSubjects.remove("Module 4 - Project");
	 * 
	 * request.getSession().setAttribute("subjectsForStudent", applicableSubjects);
	 * List<AssignmentFileBean> allAssignmentFilesList = new ArrayList<>();
	 * Commented by Steffi to allow offline students to submit assignments in
	 * APR/SEP if(!isOnline){ allAssignmentFilesList =
	 * dao.getAssignmentsForSubjects(applicableSubjects, student); }else{
	 * List<AssignmentFileBean> currentSemFiles = null; List<AssignmentFileBean>
	 * failSubjectFiles = null; if(currentSemSubjects != null &&
	 * currentSemSubjects.size()>0){ currentSemFiles =
	 * dao.getAssignmentsForSubjects(currentSemSubjects, student); } if(failSubjects
	 * != null && failSubjects.size()>0){ failSubjectFiles =
	 * dao.getResitAssignmentsForSubjects(failSubjects, student); }
	 * 
	 * if(currentSemFiles != null){ allAssignmentFilesList.addAll(currentSemFiles);
	 * }
	 * 
	 * if(failSubjectFiles != null){
	 * 
	 * allAssignmentFilesList.addAll(failSubjectFiles); } //}
	 * 
	 * if(allAssignmentFilesList != null ){
	 * 
	 * HashMap<String,AssignmentFileBean> subjectSubmissionMap = new HashMap<>();
	 * Commented by Steffi to allow offline students to submit assignments in
	 * APR/SEP if(!isOnline){ subjectSubmissionMap =
	 * dao.getSubmissionStatus(applicableSubjects, sapId);//Assignments from Jun,
	 * Dec cycle }else{ //For online, resit i.e. fail subjects paper change after
	 * resit date is over.//Applicable cycle is all 4 i.e. Apr, Jun, Sep, Dec
	 * HashMap<String,AssignmentFileBean> currentSemSubjectSubmissionMap =
	 * dao.getSubmissionStatus(currentSemSubjects, sapId);
	 * HashMap<String,AssignmentFileBean> failSubjectSubmissionMap =
	 * dao.getResitSubmissionStatus(failSubjects, student);
	 * 
	 * if(currentSemSubjectSubmissionMap != null){
	 * subjectSubmissionMap.putAll(currentSemSubjectSubmissionMap); }
	 * 
	 * if(failSubjectSubmissionMap != null){
	 * subjectSubmissionMap.putAll(failSubjectSubmissionMap); } //}
	 * for(AssignmentFileBean assignment : allAssignmentFilesList){ String subject =
	 * assignment.getSubject(); String status = "Not Submitted"; String attempts =
	 * "0"; String lastModifiedDate = "";
	 * 
	 * String pastCycleAssignmentDetails = "";
	 * 
	 * AssignmentFileBean studentSubmissionStatus =
	 * subjectSubmissionMap.get(subject); if(studentSubmissionStatus != null){
	 * status = studentSubmissionStatus.getStatus(); attempts =
	 * studentSubmissionStatus.getAttempts(); lastModifiedDate =
	 * studentSubmissionStatus.getLastModifiedDate(); lastModifiedDate =
	 * lastModifiedDate.replaceAll("T", " "); lastModifiedDate =
	 * lastModifiedDate.substring(0,19); }
	 * 
	 * assignment.setStatus(status); assignment.setAttempts(attempts);
	 * assignment.setAttemptsLeft((maxAttempts - Integer.parseInt(attempts))+"");
	 * assignment.setSem(subjectSemMap.get(subject));
	 * assignment.setLastModifiedDate(lastModifiedDate);
	 * 
	 * if(failSubjects.contains(subject)){
	 * failSubjectsAssignmentFilesList.add(assignment);
	 * if("Submitted".equals(status)){ failSubjectSubmissionCount++; } }else{
	 * currentSemAssignmentFilesList.add(assignment);
	 * if("Submitted".equals(status)){ currentSemSubmissionCount++; } } } }
	 * 
	 * //String yearMonth = dao.getMostRecentAssignmentSubmissionPeriod(); String
	 * yearMonth = dao.getLiveAssignmentMonth() + "-" + dao.getLiveAssignmentYear();
	 * m.addAttribute("yearMonth", yearMonth);
	 * 
	 * String currentSemEndDateTime = ""; String failSubjectsEndDateTime = "";
	 * 
	 * 
	 * m.addAttribute("currentSemAssignmentFilesList",currentSemAssignmentFilesList)
	 * ; int currentSemSubjectsCount = (currentSemAssignmentFilesList == null ? 0 :
	 * currentSemAssignmentFilesList.size());
	 * m.addAttribute("currentSemSubjectsCount", currentSemSubjectsCount);
	 * m.addAttribute("currentSemSubmissionCount", currentSemSubmissionCount);
	 * 
	 * m.addAttribute("failSubjectsAssignmentFilesList",
	 * failSubjectsAssignmentFilesList); int failSubjectsCount =
	 * (failSubjectsAssignmentFilesList == null ? 0 :
	 * failSubjectsAssignmentFilesList.size()); m.addAttribute("failSubjectsCount",
	 * failSubjectsCount); m.addAttribute("failSubjectSubmissionCount",
	 * failSubjectSubmissionCount);
	 * 
	 * if(currentSemSubjectsCount > 0){ currentSemEndDateTime =
	 * currentSemAssignmentFilesList.get(0).getEndDate().substring(0, 19); }
	 * if(failSubjectsCount > 0){
	 * 
	 * 
	 * for (AssignmentFileBean assignmentFileBean : failSubjectsAssignmentFilesList)
	 * { if(ANSSubjects.contains(assignmentFileBean.getSubject())){ //ANS cases will
	 * always be allowed to Submit assignmentFileBean.setSubmissionAllowed(true);
	 * subjectsNotAllowedToSubmit.remove(assignmentFileBean.getSubject()); }else
	 * if(subjectsNotAllowedToSubmit.contains(assignmentFileBean.getSubject())){
	 * assignmentFileBean.setSubmissionAllowed(false); }else{
	 * assignmentFileBean.setSubmissionAllowed(true); }
	 * 
	 * 
	 * int pastCycleAssignmentAttempts =
	 * dao.getPastCycleAssignmentAttempts(assignmentFileBean.getSubject(), sapId);
	 * logger.info("pastCycleAssignmentAttempts for "+assignmentFileBean.getSubject(
	 * )+">>"+pastCycleAssignmentAttempts); if(pastCycleAssignmentAttempts >=2 &&
	 * !"Submitted".equals(assignmentFileBean.getStatus())){
	 * logger.info("inside if for greater than or eql 2>>");
	 * logger.info("payment applicable");
	 * assignmentFileBean.setPaymentApplicable("Yes"); boolean hasPaidForAssignment
	 * = dao.checkIfAssignmentFeesPaid(assignmentFileBean.getSubject(), sapId);
	 * ////check if Assignment Fee Paid for Current drive
	 * logger.info("has paid for assg?>>>"+hasPaidForAssignment);
	 * if(!hasPaidForAssignment){ logger.info("No");
	 * assignmentFileBean.setPaymentDone("No"); }else{ logger.info("Yes");
	 * assignmentFileBean.setPaymentDone("Yes"); } }
	 * 
	 * }
	 * 
	 * request.getSession().setAttribute("subjectsNotAllowedToSubmit",
	 * subjectsNotAllowedToSubmit); failSubjectsEndDateTime =
	 * failSubjectsAssignmentFilesList.get(0).getEndDate().substring(0, 19);
	 * //Offline Students dont have limits on their attempts//
	 * if("Offline".equals(student.getExamMode())){ for (AssignmentFileBean
	 * assignmentFileBean : failSubjectsAssignmentFilesList) {
	 * 
	 * assignmentFileBean.setPaymentDone("Yes"); } } }
	 * 
	 * 
	 * m.addAttribute("currentSemEndDateTime", currentSemEndDateTime);
	 * m.addAttribute("failSubjectsEndDateTime", failSubjectsEndDateTime);
	 * 
	 * if((currentSemSubjectsCount + failSubjectsCount)== 0){ setError(request,
	 * "No Assignments allocated to you."); }
	 * 
	 * ArrayList<String> timeExtendedStudentIdSubjectList =
	 * dao.assignmentExtendedSubmissionTime();
	 * m.addAttribute("timeExtendedStudentIdSubjectList",
	 * timeExtendedStudentIdSubjectList);
	 * request.getSession().setAttribute("timeExtendedStudentIdSubjectList",
	 * timeExtendedStudentIdSubjectList);
	 * 
	 * return new ModelAndView("assignment/viewAssignmentFiles"); }
	 */

	@RequestMapping(value = "/student/viewAssignmentsForm", method = { RequestMethod.GET })
	public ModelAndView viewAssignmentsForm(HttpServletRequest request, HttpServletResponse response, Model m) {

		
		if (!checkSession(request, response)) {
			redirectToPortalApp(response);
			return null;
		}
		String sapId = (String) request.getSession().getAttribute("userId");
		assg_logger.info("Pg visit /viewAssignmentsForm Sapid - {}",sapId);
		try {
			TestDAOForRedis daoForRedis = (TestDAOForRedis) act.getBean("testDaoForRedis");
			if (daoForRedis.checkForFlagValueInCache("movingResultsToCache", "Y")) {
				return new ModelAndView("noDataAvailable");
			}
		} catch (Exception e) {
			assg_logger.error("exception in reddis flag check Sapid - {} Error -{}",sapId,e.getMessage());
			//e.printStackTrace();
		}
		
		StudentExamBean student = (StudentExamBean) request.getSession().getAttribute("studentExam");
		if ("Offline".equalsIgnoreCase(student.getExamMode())) {
			return viewPreviousAssignments(request, response, new AssignmentFileBean());
		}
		AssignmentsDAO dao = (AssignmentsDAO) act.getBean("asignmentsDAO");

		/*StudentExamBean studentRegistrationData = new StudentExamBean();
		if ("Diageo".equalsIgnoreCase(student.getConsumerType())) { // temp fix for diageo students
			studentRegistrationData = dao.getDiageoStudentRegistrationData(sapId);
		} else {
			studentRegistrationData = dao.getStudentRegistrationData(sapId);
		}*/

		m.addAttribute("yearList", currentYearList);
		m.addAttribute("subjectList", getSubjectList());

		if (student == null) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Student Enrollment not found. Please contact Administrator.");
			return new ModelAndView("assignment/viewAssignmentFiles");
		} 
		/*else {
			// Removed Waived Off logic from here in favor of the common logic in student
			// helper.
			studentService.mgetWaivedOffSubjects(student);
			request.getSession().setAttribute("studentExam", student);
		}*/

		// ExamAssignmentResponse respons =
		// asgService.getAssignments(student.getSapid());
		List<AssignmentFileBean> quickAssignments = dao.getQuickAssignmentsForSingleStudent(student.getSapid());
		request.getSession().setAttribute("quickAssignments", quickAssignments);
		assg_logger.info("quickAssignments size: {} Sapid - {}",quickAssignments.size(),sapId);
		// request.getSession().setAttribute("subjectsNotAllowedToSubmit",
		// respons.getSubjectsNotAllowedToSubmit());

		// request.getSession().setAttribute("subjectsForStudent",
		// respons.getApplicableSubjects());
		// m.addAttribute("yearMonth", respons.getYearMonth());
		// m.addAttribute("currentSemAssignmentFilesList",respons.getCurrentSemAssignmentFilesList());
		// m.addAttribute("currentSemSubjectsCount",
		// respons.getCurrentSemSubjectsCount());

		// m.addAttribute("currentSemSubmissionCount",
		// respons.getCurrentSemSubmissionCount());

		// m.addAttribute("failSubjectsAssignmentFilesList",respons.getFailSubjectsAssignmentFilesList());
//conflict frm master		
//vilpesh code starts 
//all checks moved to AssignmentController getAssignments()
		/*
		 * if(student.getProgram().equals("BBA") ||
		 * student.getProgram().equals("B.Com")) { ArrayList<AssignmentFileBean>
		 * softSkillsFailSubjectsBeans = getUGFailSubjects(student);
		 * if(softSkillsFailSubjectsBeans != null && softSkillsFailSubjectsBeans.size()
		 * > 0){
		 * 
		 * for (AssignmentFileBean bean : softSkillsFailSubjectsBeans) { String subject
		 * = bean.getSubject(); String sem = bean.getSem();
		 * failSubjects.add(bean.getSubject()); subjectSemMap.put(subject, sem);
		 * 
		 * if("ANS".equalsIgnoreCase(bean.getRemarks())){ ANSSubjects.add(subject); } }
		 * }
		 * 
		 * int[] failListIndexArr = new int[failSubjectsBeans.size()]; int index = 0;
		 * boolean isInFail = Boolean.FALSE;
		 * 
		 * List<String> ugPassSubjectsList = getUGPassSubjects(student); for(String
		 * subject:ugPassSubjectsList){ if(currentSemSubjects.contains(subject)){
		 * currentSemSubjects.remove(subject); }
		 * 
		 * for (int i = 0; i < failSubjectsBeans.size(); i++) {
		 * logger.info("Checking at (i, failSubjectBeans.subject, subject) : (" + i +
		 * ", " + failSubjectsBeans.get(i).getSubject() + ", " + subject + ")"); if
		 * (failSubjectsBeans.get(i).getSubject().contains(subject)) { isInFail =
		 * Boolean.TRUE; failListIndexArr[index++] = i; logger.info("Saving index : " +
		 * i); } } }
		 * 
		 * logger.info("Before (SapId, Size, failSubjectsBeans) : (" + sapId + ", " +
		 * failSubjectsBeans.size() + ", " + failSubjectsBeans + ")"); if (isInFail) {
		 * for (int y = 0; y < failListIndexArr.length; y++) {
		 * logger.info("Removing subject : " +
		 * failSubjectsBeans.get(failListIndexArr[y]));
		 * failSubjectsBeans.remove(failListIndexArr[y]); }
		 * logger.info("After (SapId, Size, failSubjectsBeans) : (" + sapId + ", " +
		 * failSubjectsBeans.size() + ", " + failSubjectsBeans + ")"); } else {
		 * logger.info("Not Removing subject (SapId, Size, failSubjectsBeans) : (" +
		 * sapId + ", " + failSubjectsBeans.size() + ", " + failSubjectsBeans + ")"); }
		 * 
		 * failListIndexArr = null; }
		 * 
		 * if(failSubjectsBeans != null && failSubjectsBeans.size() > 0){
		 * 
		 * for (AssignmentFileBean bean : failSubjectsBeans) { String subject =
		 * bean.getSubject(); String sem = bean.getSem();
		 * failSubjects.add(bean.getSubject()); subjectSemMap.put(subject, sem);
		 * 
		 * if("ANS".equalsIgnoreCase(bean.getAssignmentscore())){
		 * ANSSubjects.add(subject); } } }
		 */
//vilpesh code ends

		if (quickAssignments.size() == 0) {
			setError(request, "No Assignments allocated to you.");
		}
		// m.addAttribute("timeExtendedStudentIdSubjectList",
		// respons.getTimeExtendedStudentIdSubjectList());
		// request.getSession().setAttribute("timeExtendedStudentIdSubjectList",respons.getTimeExtendedStudentIdSubjectList());
		ArrayList<AssignmentFileBean> currentSemAssignmentFilesList = new ArrayList<AssignmentFileBean>();
		ArrayList<AssignmentFileBean> failSubjectsAssignmentFilesList = new ArrayList<AssignmentFileBean>();
		int failSubjectSubmissionCount = 0;
		int currentSemSubmissionCount = 0;
		ArrayList<String> failSubjects = new ArrayList<String>();
		AssignmentLiveSetting resitLive = dao.getCurrentLiveAssignment(student.getConsumerProgramStructureId(),"Resit");
		String resitLiveYear = resitLive.getExamYear();
		String resitLiveMonth = resitLive.getExamMonth();
		
		assg_logger.info("resitLive Year-Month: {}-{} Sapid - {}",resitLiveYear,resitLiveMonth,sapId);
		
		//get most recent marks live from cache
		String asgMarksLiveMonth = dao.getLiveAssignmentMarksMonth();
		String asgMarksLiveYear = dao.getLiveAssignmentMarksYear();  
		String marksLiveYearMonth = asgMarksLiveMonth+"-"+asgMarksLiveYear;
		
		assg_logger.info("marksLiveYearMonth Year-Month: {}-{} Sapid - {}",asgMarksLiveMonth,asgMarksLiveYear,sapId);
		
		try {
			//collecting fail subjects
			for (AssignmentFileBean q : quickAssignments) {
				if (!q.getCurrentSemSubject().equalsIgnoreCase("Y") 
						&& resitLiveYear.equalsIgnoreCase(q.getYear()) && resitLiveMonth.equalsIgnoreCase(q.getMonth()) ) {
					failSubjectsAssignmentFilesList.add(q);
					failSubjects.add(q.getSubject());
					if ("Submitted".equals(q.getStatus())) {
						failSubjectSubmissionCount++;
					}
				}
			}
			//collecting pass subjects
			for (AssignmentFileBean q : quickAssignments) {
				if (q.getCurrentSemSubject().equalsIgnoreCase("Y")
						// For ANS cases, where result is not declared, failed subject will also be
						// present in Current sem subject.
						// Give preference to it as Failed, so that assignment can be submitted and
						// remove from Current list
						// If result is live, hide assignments
						&& !(q.getMonth()+"-"+q.getYear()).equalsIgnoreCase(marksLiveYearMonth)
						&& !(failSubjects.contains(q.getSubject()))) {
					currentSemAssignmentFilesList.add(q);
					if ("Submitted".equals(q.getStatus())) {
						currentSemSubmissionCount++;
					}
				}
			}
			String currentSemEndDateTime = "";
			if (currentSemAssignmentFilesList.size() > 0) {
				currentSemEndDateTime = currentSemAssignmentFilesList.get(0).getEndDate().substring(0, 19);
				m.addAttribute("currentSemEndDateTime", currentSemEndDateTime);
			}
			String failSubjectsEndDateTime = "";
			if (failSubjectsAssignmentFilesList.size() > 0) {
				failSubjectsEndDateTime = failSubjectsAssignmentFilesList.get(0).getEndDate().substring(0, 19);
				m.addAttribute("failSubjectsEndDateTime", failSubjectsEndDateTime);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			assg_logger.error("Exception error /viewAssignmentsForm  Sapid - {} Error - {}",sapId,e);
		}
		
		m.addAttribute("failSubjectSubmissionCount", failSubjectSubmissionCount);
		m.addAttribute("currentSemSubmissionCount", currentSemSubmissionCount);
		m.addAttribute("currentSemAssignmentFilesList", currentSemAssignmentFilesList);
		m.addAttribute("failSubjectsAssignmentFilesList", failSubjectsAssignmentFilesList);
		return new ModelAndView("assignment/viewAssignmentFilesDemo");
	}

	/*
	 * @RequestMapping(value = "/viewResitAssignmentsForm", method =
	 * {RequestMethod.GET, RequestMethod.POST}) public ModelAndView
	 * viewResitAssignmentsForm(HttpServletRequest request, HttpServletResponse
	 * response, Model m) {
	 * 
	 * if(!checkSession(request, response)){ redirectToPortalApp(response); return
	 * null; }
	 * 
	 * String sapId = (String)request.getSession().getAttribute("userId");
	 * ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
	 * StudentBean student = eDao.getSingleStudentsData(sapId); boolean isOnline =
	 * isOnline(student);
	 * 
	 * AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
	 * 
	 * //ArrayList<String> currentSemSubjects = new ArrayList<>(); ArrayList<String>
	 * failSubjects = new ArrayList<>(); ArrayList<String> applicableSubjects = new
	 * ArrayList<>();
	 * 
	 * List<AssignmentFileBean> failSubjectsAssignmentFilesList = new ArrayList<>();
	 * //List<AssignmentFileBean> currentSemAssignmentFilesList = new ArrayList<>();
	 * 
	 * HashMap<String, String> subjectSemMap = new HashMap<>(); //int
	 * currentSemSubmissionCount = 0; int failSubjectSubmissionCount = 0;
	 * 
	 * 
	 * //StudentBean studentRegistrationData =
	 * dao.getStudentRegistrationData(sapId); //String currentSem = null;
	 * 
	 * int maxAttempts = Integer.parseInt(ASSIGNMENT_SUBMISSIONS_ATTEMPTS);
	 * m.addAttribute("yearList", currentYearList); m.addAttribute("subjectList",
	 * getSubjectList());
	 * 
	 * if(student == null){ request.setAttribute("error", "true");
	 * request.setAttribute("errorMessage",
	 * "Student Enrollment not found. Please contact Administrator."); return new
	 * ModelAndView("assignment/viewAssignmentFiles"); }else{
	 * request.getSession().setAttribute("student", student); }
	 * 
	 * if(studentRegistrationData != null){ //Take program from Registration data
	 * and not Student data.
	 * student.setProgram(studentRegistrationData.getProgram());
	 * student.setSem(studentRegistrationData.getSem()); currentSem =
	 * studentRegistrationData.getSem(); currentSemSubjects =
	 * getSubjectsForStudent(student, subjectSemMap);
	 * 
	 * }
	 * 
	 * 
	 * //if((currentSem != null && (!"1".equals(currentSem))) ||
	 * studentRegistrationData == null){ //If current semester is 1, then there
	 * cannot be any failed subjects
	 * 
	 * ArrayList<AssignmentFileBean> failSubjectsBeans = getFailSubjects(student);
	 * if(failSubjectsBeans != null && failSubjectsBeans.size() > 0){ failSubjects =
	 * new ArrayList<>(); for (int i = 0; i < failSubjectsBeans.size(); i++) {
	 * String subject = failSubjectsBeans.get(i).getSubject(); String sem =
	 * failSubjectsBeans.get(i).getSem();
	 * failSubjects.add(failSubjectsBeans.get(i).getSubject());
	 * subjectSemMap.put(subject, sem); } }
	 * 
	 * //} //applicableSubjects.addAll(currentSemSubjects);
	 * applicableSubjects.addAll(failSubjects);
	 * applicableSubjects.remove("Project");
	 * 
	 * request.getSession().setAttribute("subjectsForStudent", applicableSubjects);
	 * 
	 * List<AssignmentFileBean> allAssignmentFilesList =
	 * dao.getResitAssignmentsForSubjects(applicableSubjects);
	 * 
	 * if(allAssignmentFilesList != null ){
	 * 
	 * HashMap<String,AssignmentFileBean> subjectSubmissionMap =
	 * dao.getResitSubmissionStatus(applicableSubjects, sapId);
	 * for(AssignmentFileBean assignment : allAssignmentFilesList){ String subject =
	 * assignment.getSubject(); String status = "Not Submitted"; String attempts =
	 * "0"; String lastModifiedDate = "";
	 * 
	 * AssignmentFileBean studentSubmissionStatus =
	 * subjectSubmissionMap.get(subject); if(studentSubmissionStatus != null){
	 * status = studentSubmissionStatus.getStatus(); attempts =
	 * studentSubmissionStatus.getAttempts(); lastModifiedDate =
	 * studentSubmissionStatus.getLastModifiedDate(); lastModifiedDate =
	 * lastModifiedDate.replaceAll("T", " "); lastModifiedDate =
	 * lastModifiedDate.substring(0,19); }
	 * 
	 * assignment.setStatus(status); assignment.setAttempts(attempts);
	 * assignment.setAttemptsLeft((maxAttempts - Integer.parseInt(attempts))+"");
	 * assignment.setSem(subjectSemMap.get(subject));
	 * assignment.setLastModifiedDate(lastModifiedDate);
	 * 
	 * 
	 * failSubjectsAssignmentFilesList.add(assignment);
	 * if("Submitted".equals(status)){ failSubjectSubmissionCount++; }
	 * 
	 * } }
	 * 
	 * String yearMonth = dao.getMostRecentResitAssignmentSubmissionPeriod();
	 * m.addAttribute("yearMonth", yearMonth);
	 * 
	 * String endDateTime = "";
	 * 
	 * //m.addAttribute("currentSemAssignmentFilesList",
	 * currentSemAssignmentFilesList); //int currentSemSubjectsCount =
	 * (currentSemAssignmentFilesList == null ? 0 :
	 * currentSemAssignmentFilesList.size());
	 * //m.addAttribute("currentSemSubjectsCount", currentSemSubjectsCount);
	 * //m.addAttribute("currentSemSubmissionCount", currentSemSubmissionCount);
	 * 
	 * 
	 * 
	 * m.addAttribute("failSubjectsAssignmentFilesList",
	 * failSubjectsAssignmentFilesList); int failSubjectsCount =
	 * (failSubjectsAssignmentFilesList == null ? 0 :
	 * failSubjectsAssignmentFilesList.size()); m.addAttribute("failSubjectsCount",
	 * failSubjectsCount); m.addAttribute("failSubjectSubmissionCount",
	 * failSubjectSubmissionCount);
	 * 
	 * if(failSubjectsCount > 0){ endDateTime =
	 * failSubjectsAssignmentFilesList.get(0).getEndDate().substring(0, 19); }
	 * 
	 * m.addAttribute("endDateTime", endDateTime);
	 * 
	 * return new ModelAndView("assignment/viewResitAssignmentFiles"); }
	 */

	public boolean isOnline(StudentExamBean student) {
		String programStucture = student.getPrgmStructApplicable();
		boolean isOnline = false;

		if ("Online".equals(student.getExamMode())) {
			// New batch students and certificate program students will be considered online
			// and with 4 attempts for assginmnet submission
			isOnline = true;
			// NA
		}
		return isOnline;
	}

	private ArrayList<String> getPassSubjects(StudentExamBean student, PassFailDAO dao) {
		ArrayList<String> passSubjectList = dao.getPassSubjectsNamesForSingleStudent(student.getSapid());
		return passSubjectList;
	}

	private ArrayList<AssignmentFileBean> getFailSubjects(StudentExamBean student) {
		PassFailDAO dao = (PassFailDAO) act.getBean("passFailDAO");
		ArrayList<AssignmentFileBean> failSubjectList = dao.getFailSubjectsForAStudent(student.getSapid());
		return failSubjectList;
	}

	private ArrayList<AssignmentFileBean> getUGFailSubjects(StudentExamBean student) {
		PassFailDAO dao = (PassFailDAO) act.getBean("passFailDAO");
		ArrayList<AssignmentFileBean> failSubjectList = dao.getUGFailSubjectsForAStudent(student.getSapid());
		return failSubjectList;
	}

	private List<String> getUGPassSubjects(StudentExamBean student) {
		PassFailDAO dao = (PassFailDAO) act.getBean("passFailDAO");
		return dao.getUGPassSubjectsForAStudent(student.getSapid());
	}

	private ArrayList<AssignmentFileBean> getANSNotProcessed(StudentExamBean student) {
		PassFailDAO dao = (PassFailDAO) act.getBean("passFailDAO");
		ArrayList<AssignmentFileBean> failSubjectList = dao.getANSNotProcessed(student.getSapid());
		return failSubjectList;
	}

	private ArrayList<String> getFailSubjectsNames(StudentExamBean student) {
		PassFailDAO dao = (PassFailDAO) act.getBean("passFailDAO");
		ArrayList<String> failSubjectList = dao.getFailSubjectsNamesForAStudent(student.getSapid());
		return failSubjectList;
	}

	private ArrayList<String> getANSSubjectNames(StudentExamBean student) {
		PassFailDAO dao = (PassFailDAO) act.getBean("passFailDAO");
		ArrayList<String> failSubjectList = dao.getANSNotProcessedSubjectNames(student.getSapid());
		return failSubjectList;
	}

	/*
	 * @RequestMapping(value="/getAssignments", method = RequestMethod.GET)
	 * public @ResponseBody String getAssignments(HttpServletRequest request,
	 * HttpServletResponse response , Model m) throws JsonParseException,
	 * JsonMappingException, IOException {
	 * 
	 * ArrayList<AssignmentFileBean> allApplicableAssignmentSubjects = new
	 * ArrayList<>(); ArrayList<String> applicableSubjects = new ArrayList<>();
	 * ArrayList<String> failSubjects = new ArrayList<>();
	 * 
	 * String subjectsJSON = "["; try{
	 * 
	 * String userId = loginUser(request); HashMap<String, String> subjectSemMap =
	 * new HashMap<>();
	 * 
	 * ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
	 * StudentBean student = eDao.getSingleStudentsData(userId); AssignmentsDAO dao
	 * = (AssignmentsDAO)act.getBean("asignmentsDAO"); StudentBean
	 * studentRegistrationData = dao.getStudentRegistrationData(userId);
	 * 
	 * if(student == null){ request.setAttribute("error", "true");
	 * request.setAttribute("errorMessage",
	 * "Student Enrollment not found. Please contact Administrator.");
	 * 
	 * return errorJSON(allApplicableAssignmentSubjects,
	 * "Student Enrollment not found. Please contact Administrator.");
	 * 
	 * }else{ request.getSession().setAttribute("student", student); }
	 * 
	 * boolean isOnline = isOnline(student);
	 * 
	 * ArrayList<AssignmentFileBean> failSubjectsBeans = new ArrayList<>();
	 * ArrayList<String> currentSemSubjects = new ArrayList<>();
	 * 
	 * 
	 * if(studentRegistrationData != null){ //Take program and sem from Registration
	 * data and not Student data.
	 * student.setProgram(studentRegistrationData.getProgram());
	 * student.setSem(studentRegistrationData.getSem()); currentSemSubjects =
	 * getSubjectsForStudent(student, subjectSemMap);
	 * 
	 * request.getSession().setAttribute("subjectsForStudent", currentSemSubjects);
	 * 
	 * //If current sem is sem 1, there cannot be fail subjects.
	 * if(!"1".equals(studentRegistrationData.getSem().trim())){ failSubjectsBeans =
	 * getFailSubjects(student); } }else{ //If there is no registration for current
	 * session, then check for fail subjects. failSubjectsBeans =
	 * getFailSubjects(student); }
	 * 
	 * for (int i = 0; i < failSubjectsBeans.size(); i++) { String subject =
	 * failSubjectsBeans.get(i).getSubject(); String sem =
	 * failSubjectsBeans.get(i).getSem();
	 * failSubjects.add(failSubjectsBeans.get(i).getSubject());
	 * subjectSemMap.put(subject, sem); }
	 * 
	 * applicableSubjects.addAll(currentSemSubjects);
	 * applicableSubjects.addAll(failSubjects);
	 * 
	 * //List<AssignmentFileBean> allAssignmentFilesList =
	 * dao.getAssignmentsForSubjects(applicableSubjects,isOnline);
	 * 
	 * List<AssignmentFileBean> allAssignmentFilesList = new ArrayList<>();
	 * if(!isOnline){ allAssignmentFilesList =
	 * dao.getAssignmentsForSubjects(applicableSubjects); }else{
	 * List<AssignmentFileBean> currentSemFiles =
	 * dao.getAssignmentsForSubjects(currentSemSubjects); List<AssignmentFileBean>
	 * failSubjectFiles = dao.getResitAssignmentsForSubjects(failSubjects);
	 * 
	 * if(currentSemFiles != null){ allAssignmentFilesList.addAll(currentSemFiles);
	 * }
	 * 
	 * if(failSubjectFiles != null){
	 * allAssignmentFilesList.addAll(failSubjectFiles); } }
	 * 
	 * 
	 * if(allAssignmentFilesList != null){ for(AssignmentFileBean assignment:
	 * allAssignmentFilesList) { subjectsJSON = subjectsJSON +
	 * "{\"subject\":\""+assignment.getSubject()+"\"},"; } } //Create JSON
	 * for(String subject: currentSemSubjects) { subjectsJSON = subjectsJSON +
	 * "{\"subject\":\""+subject+"\"},"; }
	 * 
	 * 
	 * if(failSubjectsBeans != null && failSubjectsBeans.size() > 0){ for
	 * (AssignmentFileBean failedSubjectBean : failSubjectsBeans) { subjectsJSON =
	 * subjectsJSON + "{\"subject\":\""+failedSubjectBean.getSubject()+"\"},"; }
	 * 
	 * } //Remove additional , at the end if(subjectsJSON.endsWith(",")){
	 * subjectsJSON = subjectsJSON.substring(0, subjectsJSON.length() - 1); }
	 * 
	 * 
	 * //Add fail subjects in list
	 * allApplicableAssignmentSubjects.addAll(failSubjectsBeans);
	 * 
	 * //Add current sem subjects for (String subject : currentSemSubjects) {
	 * AssignmentFileBean assignment = new AssignmentFileBean();
	 * assignment.setSubject(subject);
	 * allApplicableAssignmentSubjects.add(assignment); }
	 * 
	 * }catch(Exception e){  return
	 * errorJSON(allApplicableAssignmentSubjects,
	 * "Error in getting assignments list: "+e.getMessage()); }
	 * 
	 * subjectsJSON = subjectsJSON + "]"; return subjectsJSON;
	 * 
	 * }
	 */

	/*
	 * private String errorJSON(ArrayList<AssignmentFileBean>
	 * allApplicableAssignmentSubjects, String errorMessage) { AssignmentFileBean
	 * assignment = new AssignmentFileBean(); assignment.setErrorRecord(true);
	 * assignment.
	 * setErrorMessage("Student Enrollment not found. Please contact Administrator."
	 * ); assignment.setSubject("Student Enrollment not found");
	 * allApplicableAssignmentSubjects.add(assignment); String errorJSON =
	 * "[{\"subject\":\"Error:"+errorMessage+"\"}]"; return errorJSON;
	 * 
	 * }
	 */

	private String loginUser(HttpServletRequest request) throws Exception {
		String userIdEncrypted = request.getParameter("uid");
		String userId = AESencrp.decrypt(userIdEncrypted);
		request.getSession().setAttribute("userId", userId);

		Person person = new Person();
		person.setUserId(userId);
		request.getSession().setAttribute("user", person);

		return userId;

	}

	@RequestMapping(value = {"/student/downloadStudentAssignmentFile","/downloadStudentAssignmentFile"}, method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView downloadStudentAssignmentFile(HttpServletRequest request, HttpServletResponse response,
			Model m) {
		ModelAndView modelnView = new ModelAndView("downloadAssignmentFile");

		String fullPath = request.getParameter("filePath");
		// String subject = request.getParameter("subject");

		ArrayList<String> subjects = (ArrayList<String>) request.getSession().getAttribute("subjectsForStudent");
		/*
		 * if(!subjects.contains(subject)){ request.setAttribute("error", "true");
		 * request.setAttribute("errorMessage",
		 * "You are not authorized to view assignment for subject: "+subject); return
		 * viewAssignmentsForm(request, response, m); }
		 */
		try {
			// get absolute path of the application
			ServletContext context = request.getSession().getServletContext();
			String appPath = context.getRealPath("");

			// construct the complete absolute path of the file
			// String fullPath = appPath + filePath;
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
			String headerValue = String.format("attachment; filename=\"%s\"", downloadFile.getName());
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
		} catch (Exception e) {
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in downloading file.");
		}
		AssignmentFileBean searchBean = (AssignmentFileBean) request.getSession().getAttribute("searchBean");
		modelnView.addObject("searchBean", searchBean);
		modelnView.addObject("yearList", currentYearList);
		modelnView.addObject("subjectList", getSubjectList());
		return modelnView;
	}

	private ArrayList<String> getSubjectsForStudent(StudentExamBean student, HashMap<String, String> subjectSemMap) {

		ArrayList<ProgramSubjectMappingExamBean> programSubjectMappingList = getProgramSubjectMappingList();

		ArrayList<String> subjects = new ArrayList<>();
		for (int i = 0; i < programSubjectMappingList.size(); i++) {
			ProgramSubjectMappingExamBean bean = programSubjectMappingList.get(i);

			if (bean.getPrgmStructApplicable().equals(student.getPrgmStructApplicable())
					&& bean.getProgram().equals(student.getProgram()) && bean.getSem().equals(student.getSem())
					&& !student.getWaivedOffSubjects().contains(bean.getSubject())// Subjects has not already cleared it
			) {
				subjects.add(bean.getSubject());
				subjectSemMap.put(bean.getSubject(), bean.getSem());

			}

			// Below code is for creating map of subject and sem
			if (bean.getPrgmStructApplicable().equals(student.getPrgmStructApplicable())
					&& bean.getProgram().equals(student.getProgram())) {
				subjectSemMap.put(bean.getSubject(), bean.getSem());

			}
		}

		return subjects;
	}

	@RequestMapping(value = "/student/viewSingleAssignment", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView viewSingleAssignment(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute AssignmentFileBean assignmentFile, Model m) {
		if (!checkSession(request, response)) {
			redirectToPortalApp(response);
			return null;
		}
		String successMessage = (String) request.getSession().getAttribute("successMsg");
		request.getSession().removeAttribute("successMsg");
		if(successMessage != null){
			request.setAttribute("success", "true");
			request.setAttribute("successMessage", successMessage);
		}
		
		ModelAndView modelnView = new ModelAndView("assignment/assignmentDemo");
		String sapId = (String) request.getSession().getAttribute("userId");

		assg_logger.info("Pg visit /viewSingleAssignment Sapid - {} Subject - {}",sapId,assignmentFile.getSubject());
		
		StudentExamBean student = (StudentExamBean) request.getSession().getAttribute("studentExam");
		try {
			if (student.getWaivedOffSubjects().contains(assignmentFile.getSubject())) {
				// If subject is waived off, dont go to assignment submission page.
				setError(request, assignmentFile.getSubject() + " subject is not applicable for you.");
				return viewAssignmentsForm(request, response, m);
			}
			boolean isOnline = isOnline(student);
			AssignmentsDAO dao = (AssignmentsDAO) act.getBean("asignmentsDAO");
			
			List<AssignmentFileBean> assignmentFiles = dao.getQuickAssignmentsForSingleStudent(sapId,
					assignmentFile.getSubject(), assignmentFile.getYear(), assignmentFile.getMonth());
			if (assignmentFiles.size() > 0) {
				for (AssignmentFileBean asg : assignmentFiles) {
					// if(asg.getCurrentSemSubject().equalsIgnoreCase("N")) {
					assignmentFile = asg;
					// }
				}
			}
			request.getSession().setAttribute("assignmentFile", assignmentFile);
			
			String startDate = assignmentFile.getStartDate();
			startDate = startDate.replaceAll("T", " ");
			assignmentFile.setStartDate(startDate.substring(0, 19));

			String endDate = assignmentFile.getEndDate();
			endDate = endDate.replaceAll("T", " ");
			assignmentFile.setEndDate(endDate.substring(0, 19));
			
			int maxAttempts = Integer.parseInt(ASSIGNMENT_SUBMISSIONS_ATTEMPTS);

			if (isOnline) {// Applicble for online students only
				int pastCycleAssignmentAttempts = dao.getPastCycleAssignmentAttempts(assignmentFile.getSubject(), sapId);
				if (pastCycleAssignmentAttempts >= 2 && !"Submitted".equals(assignmentFile.getStatus())) {// Same Condition
																											// added by
																											// Vikas
																											// 02/08/2016//
					boolean hasPaidForAssignment = dao.checkIfAssignmentFeesPaid(assignmentFile.getSubject(), sapId); // check
																														// if
																														// Assignment
																														// Fee
																														// Paid
																														// for
																														// Current
																														// drive
					if (!hasPaidForAssignment) {
						modelnView.addObject("assignmentPaymentPending", "Y");
					}
				}
			}
			modelnView.addObject("submissionAllowed", "Y");
			modelnView.addObject("maxAttempts", maxAttempts);
			modelnView.addObject("assignmentFile", assignmentFile);
			modelnView.addObject("yearList", currentYearList);
			modelnView.addObject("subjectList", getSubjectList());
			modelnView.addObject("subject", assignmentFile.getSubject());
			request.getSession().setAttribute("subjectForPayment", assignmentFile.getSubject());
			
			ArrayList<String> timeExtendedStudentIdSubjectList = dao.assignmentExtendedSubmissionTime();
			m.addAttribute("timeExtendedStudentIdSubjectList", timeExtendedStudentIdSubjectList);
			request.getSession().setAttribute("timeExtendedStudentIdSubjectList", timeExtendedStudentIdSubjectList);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			assg_logger.error("Exception Error /viewSingleAssignment  Sapid - {} Subject - {} Error - {}",sapId,assignmentFile.getSubject(),e);
		}
		
		return modelnView;
	}

	@RequestMapping(value = "/student/viewLastCycleSingleAssignment", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView viewLastCycleSingleAssignment(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute AssignmentFileBean assignmentFile, Model m) {
		/*
		 * if(!checkSession(request, response)){ redirectToPortalApp(response); return
		 * null; }
		 */

		ModelAndView modelnView = new ModelAndView("assignment/assignment");
		
		String sapId = (String) request.getSession().getAttribute("userId");

		StudentMarksDAO sMarksDao = (StudentMarksDAO) act.getBean("studentMarksDAO");
		StudentExamBean student = (StudentExamBean) request.getSession().getAttribute("studentExam");
		if (student.getWaivedOffSubjects().contains(assignmentFile.getSubject())) {
			// If subject is waived off, dont go to assignment submission page.
			setError(request, assignmentFile.getSubject() + " subject is not applicable for you.");
			return viewAssignmentsForm(request, response, m);
		}

		ArrayList<String> failSubjectList = getFailSubjectsNames(student);

		ArrayList<String> ansSubjects = getANSSubjectNames(student);

		if (student.getProgram().equals("BBA") || student.getProgram().equals("B.Com")) {
			ArrayList<AssignmentFileBean> softSkillsFailSubjectsBeans = getUGFailSubjects(student);
			if (softSkillsFailSubjectsBeans != null && softSkillsFailSubjectsBeans.size() > 0) {

				for (AssignmentFileBean bean : softSkillsFailSubjectsBeans) {
					String subject = bean.getSubject();
					String sem = bean.getSem();
					failSubjectList.add(bean.getSubject());

					if ("ANS".equalsIgnoreCase(bean.getRemarks())) {
						ansSubjects.add(subject);
					}
				}
			}

			List<String> passSubjects = getUGPassSubjects(student);
			if (passSubjects.contains(assignmentFile.getSubject())) {
				// If subject is waived off, dont go to assignment submission page.
				setError(request, assignmentFile.getSubject() + " subject is not applicable for you.");
				return viewAssignmentsForm(request, response, m);
			}
		}

		if (failSubjectList != null) {
			failSubjectList.addAll(ansSubjects);
		} else {
			failSubjectList = new ArrayList<>();
			failSubjectList.addAll(ansSubjects);
		}

		boolean isOnline = isOnline(student);
		AssignmentsDAO dao = (AssignmentsDAO) act.getBean("asignmentsDAO");
		/*
		 * Commented by Steffi to allow offline students to submit assignments in
		 * APR/SEP if(!isOnline){ assignmentFile = dao.findById(assignmentFile,
		 * student);//For offline students there is only one submission cycle per sem.
		 * No Resit for assignments }else{
		 */
		/*
		 * if(failSubjectList.size()>0 &&
		 * failSubjectList.contains(assignmentFile.getSubject())){ assignmentFile =
		 * dao.findResitAssignmentById(assignmentFile, student);//Fail subject details
		 * }else{ assignmentFile = dao.findById(assignmentFile, student);//Current sem
		 * subject details }
		 */
		/* } */
		assignmentFile.setConsumerProgramStructureId(student.getConsumerProgramStructureId());
		AssignmentFileBean inputBean = assignmentFile;
		assignmentFile = dao.findAssignment(assignmentFile);

		request.getSession().setAttribute("assignmentFile", assignmentFile);
		String status = "Not Submitted";
		String attempts = "0";

		AssignmentFileBean studentSubmissionStatus = null;

		/*
		 * Commented by Steffi to allow offline students to submit assignments in
		 * APR/SEP if(!isOnline){ studentSubmissionStatus =
		 * dao.getAssignmentStatusForASubject(assignmentFile.getSubject(), sapId);
		 * }else{
		 */
		assignmentFile.setSapId(sapId);
		studentSubmissionStatus = dao.getAssignmentSubmission(assignmentFile);// Current sem subject details

		/* } */

		if (studentSubmissionStatus != null) {
			status = studentSubmissionStatus.getStatus();
			attempts = studentSubmissionStatus.getAttempts();
			assignmentFile.setPreviewPath(studentSubmissionStatus.getPreviewPath());
			assignmentFile.setStudentFilePath(studentSubmissionStatus.getStudentFilePath());
		}
		assignmentFile.setStatus(status);
		assignmentFile.setAttempts(attempts);

		String startDate = assignmentFile.getStartDate();
		startDate = startDate.replaceAll("T", " ");
		assignmentFile.setStartDate(startDate.substring(0, 19));

		String endDate = assignmentFile.getEndDate();
		endDate = endDate.replaceAll("T", " ");
		assignmentFile.setEndDate(endDate.substring(0, 19));

		int maxAttempts = Integer.parseInt(ASSIGNMENT_SUBMISSIONS_ATTEMPTS);
		modelnView.addObject("assignmentPaymentPending", "N");
		if (isOnline) {// Applicble for online students only
			int pastCycleAssignmentAttempts = dao.getPastCycleAssignmentAttempts(assignmentFile.getSubject(), sapId);
			boolean hasPaidForAssignment ;
			if (pastCycleAssignmentAttempts >= 2 && !"Submitted".equals(assignmentFile.getStatus())) {// Same Condition
																										// added by
																										// Vikas
																										// 02/08/2016//
				 
					 hasPaidForAssignment = dao.checkIfAssignmentFeesPaid(assignmentFile.getSubject(), sapId); //check payment for assignment subjects -byPrashant
				   //check if Assignment Fee Paid for Current drive 
				   if(!hasPaidForAssignment){
				   modelnView.addObject("assignmentPaymentPending","Y"); }
			}
		}		
		if ((!inputBean.getStartDate().equalsIgnoreCase(assignmentFile.getStartDate()))
				|| (!inputBean.getEndDate().equalsIgnoreCase(assignmentFile.getEndDate()))
				|| (!inputBean.getSubject().equalsIgnoreCase(assignmentFile.getSubject()))
				|| (!inputBean.getYear().equalsIgnoreCase(assignmentFile.getYear()))
				|| (!inputBean.getMonth().equalsIgnoreCase(assignmentFile.getMonth()))) {
			modelnView.addObject("submissionAllowed", "N");
			modelnView.addObject("assignmentFile", assignmentFile);
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Submission Not allowed");
			return modelnView;
		}
		modelnView.addObject("submissionAllowed", "Y");
		/*
		 * if(failSubjectList != null &&
		 * failSubjectList.contains(assignmentFile.getSubject())){ ArrayList<String>
		 * failedSubjectsSubmittedInLastCycle =
		 * dao.getFailedSubjectsSubmittedInLastCycle(sapId, new
		 * ArrayList<String>(Arrays.asList(assignmentFile.getSubject())));
		 * if(failedSubjectsSubmittedInLastCycle.size() > 0 &&
		 * failedSubjectsSubmittedInLastCycle.contains(assignmentFile.getSubject())){
		 * //There are failed subjects submitted in last submission cycle //Check if
		 * result is live for last submission cycle boolean
		 * isResultLiveForLastSubmissionCycle =
		 * sMarksDao.isResultLiveForLastAssignmentSubmissionCycle();
		 * if(!isResultLiveForLastSubmissionCycle){ //If result is not live then
		 * subjects submitted in last cycle cannot be submitted till results are live
		 * modelnView.addObject("submissionAllowed","false"); } } }
		 */

		modelnView.addObject("maxAttempts", maxAttempts);
		modelnView.addObject("assignmentFile", assignmentFile);
		modelnView.addObject("yearList", currentYearList);
		modelnView.addObject("subjectList", getSubjectList());
		modelnView.addObject("subject", assignmentFile.getSubject());
		request.getSession().setAttribute("subjectForPayment", assignmentFile.getSubject());

		ArrayList<String> timeExtendedStudentIdSubjectList = dao.assignmentExtendedSubmissionTime();
		m.addAttribute("timeExtendedStudentIdSubjectList", timeExtendedStudentIdSubjectList);
		request.getSession().setAttribute("timeExtendedStudentIdSubjectList", timeExtendedStudentIdSubjectList);

		return modelnView;
	}

	/*
	 * @RequestMapping(value = "/viewSingleResitAssignment", method =
	 * {RequestMethod.GET, RequestMethod.POST}) public ModelAndView
	 * viewSingleResitAssignment(HttpServletRequest request, HttpServletResponse
	 * response, @ModelAttribute AssignmentFileBean assignmentFile) {
	 * if(!checkSession(request, response)){ redirectToPortalApp(response); return
	 * null; }
	 * 
	 * ModelAndView modelnView = new ModelAndView("assignment/resitAssignment");
	 * String sapId = (String)request.getSession().getAttribute("userId");
	 * 
	 * AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
	 * assignmentFile = dao.findResitAssignmentById(assignmentFile);
	 * 
	 * request.getSession().setAttribute("assignmentFile", assignmentFile); String
	 * status = "Not Submitted"; String attempts = "0";
	 * 
	 * AssignmentFileBean studentSubmissionStatus =
	 * dao.getResitAssignmentStatusForASubject(assignmentFile.getSubject(), sapId);
	 * if(studentSubmissionStatus != null){ status =
	 * studentSubmissionStatus.getStatus(); attempts =
	 * studentSubmissionStatus.getAttempts();
	 * assignmentFile.setPreviewPath(studentSubmissionStatus.getPreviewPath()); }
	 * assignmentFile.setStatus(status); assignmentFile.setAttempts(attempts);
	 * 
	 * String startDate = assignmentFile.getStartDate(); startDate =
	 * startDate.replaceAll("T", " ");
	 * assignmentFile.setStartDate(startDate.substring(0,19));
	 * 
	 * String endDate = assignmentFile.getEndDate(); endDate =
	 * endDate.replaceAll("T", " ");
	 * assignmentFile.setEndDate(endDate.substring(0,19)); int maxAttempts =
	 * Integer.parseInt(ASSIGNMENT_SUBMISSIONS_ATTEMPTS);
	 * 
	 * modelnView.addObject("maxAttempts",maxAttempts);
	 * modelnView.addObject("assignmentFile",assignmentFile);
	 * modelnView.addObject("yearList", currentYearList);
	 * modelnView.addObject("subjectList", getSubjectList());
	 * 
	 * return modelnView; }
	 */
//	@RequestMapping(value = "/m/viewAssignmentsForm", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
//	public ResponseEntity<ExamAssignmentResponseBean> mViewAssignmentsForm(HttpServletRequest request,
//			@RequestBody Person input) throws Exception {
//
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json");
//
//		TestDAOForRedis daoForRedis = (TestDAOForRedis) act.getBean("testDaoForRedis");
//
//		if (daoForRedis.checkForFlagValueInCache("movingResultsToCache", "Y")) {
//
//			return new ResponseEntity<>(new ExamAssignmentResponseBean(), headers, HttpStatus.OK);
//		}
//
//		String sapId = input.getSapId();
//		ExamAssignmentResponseBean response = new ExamAssignmentResponseBean();
//		// ExamAssignmentResponse response = asgService.getAssignments(sapId);
//		AssignmentsDAO dao = (AssignmentsDAO) act.getBean("asignmentsDAO");
//		List<AssignmentFileBean> quickAssignments = dao.getQuickAssignmentsForSingleStudent(sapId);
//		request.getSession().setAttribute("quickAssignments", quickAssignments);
//
//		if (quickAssignments.size() == 0) {
//			response.setError("true");
//			response.setErrorMessage("No Assignments allocated to you.");
//		}
//		ArrayList<AssignmentFileBean> currentSemAssignmentFilesList = new ArrayList<AssignmentFileBean>();
//		ArrayList<AssignmentFileBean> failSubjectsAssignmentFilesList = new ArrayList<AssignmentFileBean>();
//		int failSubjectSubmissionCount = 0;
//		int currentSemSubmissionCount = 0;
//		ArrayList<String> failSubjects = new ArrayList<String>();
//		ExamBookingDAO eDao = (ExamBookingDAO) act.getBean("examBookingDAO");
//		StudentExamBean student = eDao.getSingleStudentsData(sapId);
//		AssignmentLiveSetting resitLive = dao.getCurrentLiveAssignment(student.getConsumerProgramStructureId(),"Resit");
//		for (AssignmentFileBean q : quickAssignments) {
//			if (!q.getCurrentSemSubject().equalsIgnoreCase("Y") 
//					&& resitLive.getExamYear().equalsIgnoreCase(q.getYear()) && resitLive.getExamMonth().equalsIgnoreCase(q.getMonth()) ) {
//				failSubjectsAssignmentFilesList.add(q);
//				failSubjects.add(q.getSubject());
//				if ("Submitted".equals(q.getStatus())) {
//					failSubjectSubmissionCount++;
//				}
//			}
//		}
//		// For ANS cases, where result is not declared, failed subject will also be
//		// present in Current sem subject.
//		// Give preference to it as Failed, so that assignment can be submitted and
//		// remove from Current list
//		for (AssignmentFileBean q : quickAssignments) {
//			if (q.getCurrentSemSubject().equalsIgnoreCase("Y") && !(failSubjects.contains(q.getSubject()))) {
//				currentSemAssignmentFilesList.add(q);
//				if ("Submitted".equals(q.getStatus())) {
//					currentSemSubmissionCount++;
//				}
//			}
//		}
//		String currentSemEndDateTime = "";
//		if (currentSemAssignmentFilesList.size() > 0) {
//			currentSemEndDateTime = currentSemAssignmentFilesList.get(0).getEndDate().substring(0, 19);
//			response.setCurrentSemEndDateTime(currentSemEndDateTime);
//		}
//		String failSubjectsEndDateTime = "";
//		if (failSubjectsAssignmentFilesList.size() > 0) {
//			failSubjectsEndDateTime = failSubjectsAssignmentFilesList.get(0).getEndDate().substring(0, 19);
//			response.setFailSubjectsEndDateTime(failSubjectsEndDateTime);
//			//response.setFailedSemSubjectsCount(failSubjectsAssignmentFilesList.size());
//			response.setFailSubjectsCount(failSubjectsAssignmentFilesList.size());
//		}
//		response.setCurrentSemSubjectsCount(currentSemAssignmentFilesList.size());
//		response.setFailSubjectSubmissionCount(failSubjectSubmissionCount);
//		response.setCurrentSemSubmissionCount(currentSemSubmissionCount);
//		response.setCurrentSemAssignmentFilesList(currentSemAssignmentFilesList);
//		response.setFailSubjectsAssignmentFilesList(failSubjectsAssignmentFilesList);
//		return new ResponseEntity<>(response, headers, HttpStatus.OK);
//
//	}

//	@RequestMapping(value = "/m/submitAssignment", method = RequestMethod.POST, headers = "content-type=multipart/form-data")
//	public ResponseEntity<ExamAssignmentResponseBean> MsubmitAssignment(HttpServletRequest request,
//			AssignmentFileBean assignmentFile) throws Exception {
//
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json");
//		ExamAssignmentResponseBean response = new ExamAssignmentResponseBean();
//		// ModelAndView modelnView = new ModelAndView("assignment/assignment");
//		response = asgService.submitAssignment(assignmentFile);
//		if (response.getError() != null) {
//			return new ResponseEntity<>(response, headers, HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//		return new ResponseEntity<>(response, headers, HttpStatus.OK);
//
//	}

	/*
	 * @RequestMapping(value = "/submitAssignment", method = { RequestMethod.GET,
	 * RequestMethod.POST }) public ModelAndView submitAssignment(HttpServletRequest
	 * request, HttpServletResponse response,
	 * 
	 * @ModelAttribute AssignmentFileBean assignmentFile) {
	 * 
	 * if (!checkSession(request, response)) { redirectToPortalApp(response); return
	 * null; } ModelAndView modelnView = new ModelAndView("assignment/assignment");
	 * 
	 * String sapId = (String) request.getSession().getAttribute("userId");
	 * assignmentFile.setSapId(sapId);
	 * 
	 * ExamAssignmentResponse result = asgService.submitAssignment(assignmentFile);
	 * modelnView.addObject("maxAttempts", result.getMaxAttempts());
	 * modelnView.addObject("yearList", result.getCurrentYearList());
	 * modelnView.addObject("subjectList", result.getSubjectList());
	 * modelnView.addObject("assignmentFile", result.getAssignmentFile());
	 * modelnView.addObject("subject", result.getAssignmentFile().getSubject());
	 * 
	 * if (result.getError() != null) { request.setAttribute("error",
	 * result.getError()); request.setAttribute("errorMessage",
	 * result.getErrorMessage()); modelnView.addObject("assignmentFile",
	 * assignmentFile); } if (result.getSuccess() != null) {
	 * request.setAttribute("success",result.getSuccess());
	 * request.setAttribute("successMessage",result.getSuccessMessage());
	 * modelnView.addObject("assignmentFile",assignmentFile);
	 * 
	 * } return modelnView; }
	 */

	/*
	 * @RequestMapping(value = "/viewAssignmentsForm", method = {RequestMethod.GET})
	 * public ModelAndView viewAssignmentsForm(HttpServletRequest request,
	 * HttpServletResponse response, Model m) {
	 * 
	 * if(!checkSession(request, response)){ redirectToPortalApp(response); return
	 * null; } String sapId = (String)request.getSession().getAttribute("userId");
	 * StudentBean student = eDao.getSingleStudentsData(sapId);
	 * ExamAssignmentResponse result = asgService.getAssignments(sapId);
	 * 
	 * if(result.getError() !=null) { request.getSession().setAttribute("student",
	 * student); }
	 * 
	 * try { request.getSession().setAttribute("subjectsForStudent",
	 * result.getApplicableSubjects());
	 * request.getSession().setAttribute("subjectsNotAllowedToSubmit",
	 * result.getSubjectsNotAllowedToSubmit());
	 * request.getSession().setAttribute("timeExtendedStudentIdSubjectList",result.
	 * getTimeExtendedStudentIdSubjectList()); m.addAttribute("yearMonth",
	 * result.getYearMonth());
	 * m.addAttribute("currentSemAssignmentFilesList",result.
	 * getCurrentSemAssignmentFilesList());
	 * m.addAttribute("currentSemSubjectsCount",
	 * result.getCurrentSemSubjectsCount());
	 * m.addAttribute("currentSemSubmissionCount",
	 * result.getCurrentSemSubmissionCount());
	 * m.addAttribute("failSubjectsAssignmentFilesList",result.
	 * getFailSubjectsAssignmentFilesList()); m.addAttribute("failSubjectsCount",
	 * result.getFailSubjectsCount()); m.addAttribute("failSubjectSubmissionCount",
	 * result.getFailSubjectSubmissionCount());
	 * m.addAttribute("currentSemEndDateTime", result.getCurrentSemEndDateTime());
	 * m.addAttribute("failSubjectsEndDateTime",
	 * result.getFailSubjectsEndDateTime());
	 * m.addAttribute("timeExtendedStudentIdSubjectList",
	 * result.getTimeExtendedStudentIdSubjectList()); } catch (Exception e) {
	 *  }
	 * 
	 * return new ModelAndView("assignment/viewAssignmentFiles"); }
	 */
	@RequestMapping(value = "/student/submitAssignment", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView submitAssignment(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute AssignmentFileBean assignmentFile) {

		if (!checkSession(request, response)) {
			redirectToPortalApp(response);
			return null;
		}

		ModelAndView modelnView = new ModelAndView("assignment/assignmentDemo");
		String sapId = (String) request.getSession().getAttribute("userId");
		assg_logger.info("Pg visit /submitAssignment Sapid - {} Subject - {} ",sapId,assignmentFile.getSubject());
		ArrayList<String> timeExtendedStudentIdSubjectList = (ArrayList<String>) request.getSession().getAttribute("timeExtendedStudentIdSubjectList");
		try {
			assignmentFile.setSapId(sapId);
			AssignmentsDAO dao = (AssignmentsDAO) act.getBean("asignmentsDAO");

			StudentExamBean student = (StudentExamBean) request.getSession().getAttribute("studentExam");

			boolean isOnline = isOnline(student);

			int maxAttempts = Integer.parseInt(ASSIGNMENT_SUBMISSIONS_ATTEMPTS);
			int attempts = 0;

			AssignmentFileBean submissionStatus = null;

			submissionStatus = dao.getQuickAssignmentsForSingleStudent(sapId, assignmentFile.getSubject(),
					assignmentFile.getYear(), assignmentFile.getMonth()).get(0);

			if (submissionStatus != null) {
				attempts = Integer.parseInt(submissionStatus.getAttempts());
				assignmentFile.setEndDate(submissionStatus.getEndDate()); // by - Prashant Set end date from prev dao call for email purpose due to commented latest dao call for /submit api.
			}

			assignmentFile.setAttempts(attempts + "");

			modelnView.addObject("maxAttempts", maxAttempts);
			modelnView.addObject("yearList", currentYearList);
			modelnView.addObject("subjectList", getSubjectList());
			// modelnView.addObject("assignmentFile",assignmentFile);
			modelnView.addObject("subject", assignmentFile.getSubject());

			if (attempts >= maxAttempts) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage",
						"Maximum Attempts reached to submit assignment for " + assignmentFile.getSubject());
				modelnView.addObject("assignmentFile", assignmentFile);

				return modelnView;
			}
			
			//Check if student is extended or not
			if (!timeExtendedStudentIdSubjectList.contains(sapId + assignmentFile.getSubject())) {
				//Check if end date is expired or not
				boolean isEndDateExpired = false;
				isEndDateExpired = fileUploadHelper.checkIsEndDateExpired(assignmentFile.getEndDate());
				if (isEndDateExpired) {
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "Unable to submit assignment for "
							+ assignmentFile.getSubject() + " subject as deadline is over.");
					modelnView.addObject("assignmentFile", assignmentFile);
					assg_logger.error("Submission Date expired : Sapid = {} - Month Year = {}/{} - Subject = {}",
							sapId,assignmentFile.getMonth(),assignmentFile.getYear(),assignmentFile.getSubject());
					return modelnView;
				}
			}

			if (assignmentFile == null || assignmentFile.getFileData() == null) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Error in file Upload: No File Selected");
				modelnView.addObject("assignmentFile", assignmentFile);
				modelnView.addObject("submissionAllowed", "Y");
				return modelnView;
			}

			String fileName = assignmentFile.getFileData().getOriginalFilename();
			if (fileName == null || "".equals(fileName.trim())) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Error in file Upload Inner: No File Selected");
				modelnView.addObject("assignmentFile", assignmentFile);
				modelnView.addObject("submissionAllowed", "Y");
				return modelnView;
			}

			String errorMessage = uploadAssignmentSubmissionFile(assignmentFile, assignmentFile.getYear(),
					assignmentFile.getMonth(), sapId);

			// Check if file saved to Disk successfully & Antivirus check. Do not delete
			// below code
			if (errorMessage == null) {
				// Below is the code for Virus scan. Please do not delete it. It is commented,
				// since antivirus check is disabled as of now.
				// String scanStatus = "CLEAN";
				/*
				 * String scanStatus = scanFile(assignmentFile);
				 * if("ERROR_INPUT_STREAM_OPEN".equals(scanStatus)){ //Unable to open file. Try
				 * one more time, and it should works with Symantec Engine. This is a bug with
				 * Symantec Engine scanStatus = scanFile(assignmentFile); }
				 * 
				 * if("ERROR_INPUT_STREAM_OPEN".equalsIgnoreCase(scanStatus)){ //Ask student to
				 * retry. It will work assignmentFile.setPreviewPath(null);
				 * request.setAttribute("error", "true"); request.setAttribute("errorMessage",
				 * "A temporary error occured while scanning file "+
				 * assignmentFile.getFileData().getOriginalFilename() +". Error: "+scanStatus +
				 * ". Please try uploading file again.");
				 * modelnView.addObject("assignmentFile",assignmentFile);
				 * 
				 * return modelnView; }else if(!"CLEAN".equalsIgnoreCase(scanStatus)){
				 * assignmentFile.setPreviewPath(null); request.setAttribute("error", "true");
				 * request.setAttribute("errorMessage",
				 * "Virus Found in File "+assignmentFile.getFileData().getOriginalFilename()
				 * +". File not Saved. Error: "+scanStatus);
				 * modelnView.addObject("assignmentFile",assignmentFile);
				 * 
				 * return modelnView; }else{
				 */
				maxAttempts = Integer.parseInt(ASSIGNMENT_SUBMISSIONS_ATTEMPTS);
				attempts = Integer.parseInt(assignmentFile.getAttempts()) + 1;
				assignmentFile.setAttempts(attempts + "");
				assignmentFile.setStatus("Submitted");
				String userId = (String) request.getSession().getAttribute("userId");
				assignmentFile.setCreatedBy(userId);
				assignmentFile.setLastModifiedBy(userId);
				try {
					dao.saveAssignmentSubmissionDetails(assignmentFile, maxAttempts, student);
				} catch (Exception e) {
					assg_logger.error("Exception Error saveAssignmentSubmissionDetails() : Sapid = {} - Month Year = {}/{} - Subject = {} - Attempt = {} - Error = {}",
							sapId,assignmentFile.getMonth(),assignmentFile.getYear(),assignmentFile.getSubject(),assignmentFile.getAttempts(),e);
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
				try {
					dao.saveQuickAssignmentSubmissionDetails(assignmentFile, student);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					assg_logger.error("Exception Error saveQuickAssignmentSubmissionDetails() : Sapid = {} - Month Year = {}/{} - Subject = {} - Attempt = {} - StudentFilePath = {} - Error = {}",
							sapId,assignmentFile.getMonth(),assignmentFile.getYear(),assignmentFile.getSubject(),assignmentFile.getAttempts(),assignmentFile.getStudentFilePath(),e);
				}
				
				//modelnView.addObject("assignmentFile", assignmentFile); by Prashant -  commented due to not redirect to jsp for /submit api after success
				// }

			}
			else {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Error in file upload: " + errorMessage);
				assg_logger.error("Error uploading assignment file: Sapid:{} Month/Year:{}/{} Subject:{} On Attempt:{} StudentFilePath:{} ErrorMessage:{}",
									sapId,assignmentFile.getMonth(),assignmentFile.getYear(),assignmentFile.getSubject(),assignmentFile.getAttempts(),assignmentFile.getStudentFilePath(),errorMessage);
				modelnView.addObject("assignmentFile", assignmentFile);
				modelnView.addObject("submissionAllowed", "Y");
				return modelnView;
			}

			try {
				dao.upsertAssignmentStatus(assignmentFile);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				assg_logger.error("Exception Error upsertAssignmentStatus() : Sapid = {} - Month Year = {}/{} - Subject = {} - Attempt = {} - Error = {}",
						sapId,assignmentFile.getMonth(),assignmentFile.getYear(),assignmentFile.getSubject(),assignmentFile.getAttempts(),e);
			}

			assg_logger.info("Data saved in all assignment tables: Sapid = {} - Month Year = {}/{} - Subject = {} Status = {} - Attempt = {} - StudentFilePath = {}", 
					sapId,assignmentFile.getMonth(),assignmentFile.getYear(),assignmentFile.getSubject(), assignmentFile.getStatus(), assignmentFile.getAttempts(),assignmentFile.getStudentFilePath());

			
			//assignmentFile = dao.getQuickAssignmentsForSingleStudent(sapId, assignmentFile.getSubject(),
					//assignmentFile.getYear(), assignmentFile.getMonth()).get(0); by Prashant - remove unnecessary dao call which were used only for /submit api

			/*
			 * Commented by Steffi to allow offline to submit assignment in APR/SEP
			 * if(!isOnline){ assignmentFile =
			 * dao.getAssignmentDetailsForStudent(assignmentFile, student); }else{
			 */
			/*
			 * if((failSubjectList != null &&
			 * failSubjectList.contains(assignmentFile.getSubject())) || isANSNotProcessed
			 * == 1){ assignmentFile =
			 * dao.getResitAssignmentDetailsForStudent(assignmentFile, student);//Fail
			 * subject details }else{ assignmentFile =
			 * dao.getAssignmentDetailsForStudent(assignmentFile, student);//Current sem
			 * subject details }
			 */
			/* } */

			request.setAttribute("success", "true");
			//int usedAttempts = Integer.parseInt(assignmentFile.getAttempts()); by Prashant - to avoid getting value from dao call for /submit api
			int usedAttempts = attempts;
			String successMessage = "Files Uploaded successfully. Please cross verify the Preview of the uploaded assignment file. "
					+ "Take Printscreen for your records. Attempt " + usedAttempts + " Exhausted. <br>";

			if (usedAttempts < maxAttempts) {
				successMessage = successMessage
						+ "Incase of incorrect/incomplete file: you can use the remaining attempts and be cautious while resubmitting the assignment file.";
			} else {
				successMessage = successMessage + "You have exhausted all submission attempts of the subject";
			}

			//request.setAttribute("successMessage",successMessage);
			request.getSession().setAttribute("successMsg", successMessage);
			//modelnView.addObject("assignmentFile", assignmentFile); by Prashant - not redirecting to submit api to avoid twice submission

			// Send Email to student

			MailSender mailSender = (MailSender) act.getBean("mailer");
			try {
				mailSender.sendAssignmentReceivedEmail(student, assignmentFile);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				assg_logger.error("Exception Error sendAssignmentReceivedEmail() : Sapid = {} - Month Year = {}/{} - Subject = {} - Attempt = {} - StudentFilePath = {} - Error = {}",
						sapId,assignmentFile.getMonth(),assignmentFile.getYear(),assignmentFile.getSubject(),assignmentFile.getAttempts(),assignmentFile.getStudentFilePath(),e);
			}

			response.sendRedirect(SERVER_PATH+"exam/student/viewSingleAssignment?year="+ assignmentFile.getYear()+
					"&month="+ assignmentFile.getMonth() +
					"&subject="+ java.net.URLEncoder.encode(assignmentFile.getSubject(), "UTF-8")+
					"&status="+ assignmentFile.getStatus() +
					"&startDate="+ assignmentFile.getStartDate() +
					"&endDate="+ assignmentFile.getEndDate());
			
			//return modelnView; by Prashant - to avoid twice assignment submission
			return null;

		} catch (Exception e) {
			//e.printStackTrace();
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error :" + e.getMessage());
			assg_logger.error("Exception Error submitAssignment() : Sapid = {} - Month Year = {}/{} - Subject = {} - StudentFilePath = {} - Error = {}",
					sapId, assignmentFile.getMonth(), assignmentFile.getYear(), assignmentFile.getSubject(), assignmentFile.getStudentFilePath(), e);
			modelnView.addObject("assignmentFile", assignmentFile);
			return modelnView;
		}

	}

	@RequestMapping(value = "/insertMissingSapIdsForAssignmentStatus", method = { RequestMethod.GET })
	public String insertMissingSapIdsForAssignmentStatus(HttpServletRequest request, HttpServletResponse response) {
		boolean value = false;
		try {
			AssignmentsDAO dao = (AssignmentsDAO) act.getBean("asignmentsDAO");
			AssignmentFileBean assignmentFileBean = new AssignmentFileBean();

			List<AssignmentFileBean> assignmentFileBeanList = new ArrayList<>();
			assignmentFileBeanList = dao.getMissingSapIdsList();

			for (AssignmentFileBean assignments : assignmentFileBeanList) {
				assignmentFileBean.setMonth(assignments.getMonth());
				assignmentFileBean.setYear(assignments.getYear());
				assignmentFileBean.setSapId(assignments.getSapId());
				assignmentFileBean.setSubject(assignments.getSubject());
				assignmentFileBean.setCreatedBy(assignments.getCreatedBy());
				assignmentFileBean.setCreatedDate(assignments.getCreatedDate());
				assignmentFileBean.setLastModifiedBy(assignments.getLastModifiedBy());
				assignmentFileBean.setLastModifiedDate(assignments.getLastModifiedDate());

				value = dao.insertIntoAssignmentStatusMissingSapIds(assignmentFileBean);

			}

		} catch (Exception e) {
		}
		return null;

	}

	/*
	 * @RequestMapping(value = "/submitResitAssignment", method =
	 * {RequestMethod.GET, RequestMethod.POST}) public ModelAndView
	 * submitResitAssignment(HttpServletRequest request, HttpServletResponse
	 * response, @ModelAttribute AssignmentFileBean assignmentFile){
	 * 
	 * if(!checkSession(request, response)){ redirectToPortalApp(response); return
	 * null; }
	 * 
	 * ModelAndView modelnView = new ModelAndView("assignment/assignment");
	 * 
	 * try { String sapId = (String)request.getSession().getAttribute("userId");
	 * assignmentFile.setSapId(sapId); AssignmentsDAO dao =
	 * (AssignmentsDAO)act.getBean("asignmentsDAO");
	 * 
	 * int maxAttempts = Integer.parseInt(ASSIGNMENT_SUBMISSIONS_ATTEMPTS); int
	 * attempts = 0; AssignmentFileBean submissionStatus =
	 * dao.getResitAssignmentStatusForASubject(assignmentFile.getSubject(), sapId);
	 * 
	 * if(submissionStatus != null){ attempts =
	 * Integer.parseInt(submissionStatus.getAttempts());
	 * assignmentFile.setPreviewPath(submissionStatus.getPreviewPath()); }
	 * assignmentFile.setAttempts(attempts+"");
	 * 
	 * modelnView.addObject("maxAttempts",maxAttempts);
	 * modelnView.addObject("yearList", currentYearList);
	 * modelnView.addObject("subjectList", getSubjectList());
	 * modelnView.addObject("assignmentFile",assignmentFile);
	 * 
	 * if(attempts >= maxAttempts){ request.setAttribute("error", "true");
	 * request.setAttribute("errorMessage",
	 * "Maximum Attempts reached to submit assignment for "+assignmentFile.
	 * getSubject()); modelnView.addObject("assignmentFile",assignmentFile);
	 * 
	 * return modelnView; }
	 * 
	 * 
	 * 
	 * 
	 * if(assignmentFile == null || assignmentFile.getFileData() == null ){
	 * request.setAttribute("error", "true"); request.setAttribute("errorMessage",
	 * "Error in file Upload: No File Selected");
	 * modelnView.addObject("assignmentFile",assignmentFile);
	 * 
	 * return modelnView; }
	 * 
	 * String fileName = assignmentFile.getFileData().getOriginalFilename();
	 * if(fileName == null || "".equals(fileName.trim()) ){
	 * request.setAttribute("error", "true"); request.setAttribute("errorMessage",
	 * "Error in file Upload: No File Selected");
	 * modelnView.addObject("assignmentFile",assignmentFile);
	 * 
	 * return modelnView; }
	 * 
	 * String errorMessage = uploadAssignmentSubmissionFile(assignmentFile,
	 * assignmentFile.getYear(), assignmentFile.getMonth(), sapId);
	 * 
	 * //Check if file saved to Disk successfully & Antivirus check. Do not delete
	 * below code if(errorMessage == null){ maxAttempts =
	 * Integer.parseInt(ASSIGNMENT_SUBMISSIONS_ATTEMPTS);
	 * 
	 * assignmentFile.setStatus("Submitted"); String userId =
	 * (String)request.getSession().getAttribute("userId");
	 * assignmentFile.setCreatedBy(userId);
	 * assignmentFile.setLastModifiedBy(userId);
	 * dao.saveAssignmentSubmissionDetails(assignmentFile, maxAttempts); //}
	 * 
	 * }else{ request.setAttribute("error", "true");
	 * request.setAttribute("errorMessage", "Error in file Upload: "+errorMessage);
	 * modelnView.addObject("assignmentFile",assignmentFile); return modelnView; }
	 * 
	 * 
	 * dao.upsertAssignmentStatus(assignmentFile); assignmentFile =
	 * dao.getResitAssignmentDetailsForStudent(assignmentFile);
	 * request.setAttribute("success","true"); int usedAttempts =
	 * Integer.parseInt(assignmentFile.getAttempts()); String successMessage =
	 * "Files Uploaded successfully. Please cross verify the Preview of the uploaded assignment file. "
	 * + "Take Printscreen for your records. Attempt "+usedAttempts
	 * +" Exhausted. <br>";
	 * 
	 * if(usedAttempts < maxAttempts){ successMessage = successMessage +
	 * "Incase of incorrect/incomplete file: you can use the remaining attempts and be cautious while resubmitting the assignment file."
	 * ; }else{ successMessage = successMessage +
	 * "You have exhausted all submission attempts of the subject"; }
	 * 
	 * 
	 * request.setAttribute("successMessage",successMessage);
	 * modelnView.addObject("assignmentFile",assignmentFile);
	 * 
	 * //Send Email to student StudentBean student =
	 * (StudentBean)request.getSession().getAttribute("student"); MailSender
	 * mailSender = (MailSender)act.getBean("mailer");
	 * mailSender.sendAssignmentReceivedEmail(student, assignmentFile);
	 * 
	 * 
	 * 
	 * } catch (Exception e) { request.setAttribute("error", "true");
	 * request.setAttribute("errorMessage",
	 * "Error in file Upload: "+e.getMessage());
	 * modelnView.addObject("assignmentFile",assignmentFile);
	 * 
	 * return modelnView; }
	 * 
	 * return modelnView; }
	 */

	private String scanFile(AssignmentFileBean assignmentFile) {

		String[] params = new String[3];
		FileScanner scanner = new FileScanner();
		params[0] = "-file:" + assignmentFile.getStudentFilePath();
		params[1] = "-policy:scandelete";
		params[2] = "-api:2";

		String scanResult = scanner.scanFile(params);
		return scanResult;
	}

	private String uploadAssignmentSubmissionFile(AssignmentFileBean bean, String year, String month, String sapId) {

		String errorMessage = null;
		InputStream inputStream = null;
		OutputStream outputStream = null;

		CommonsMultipartFile file = bean.getFileData();
		String fileName = file.getOriginalFilename();

		long fileSizeInBytes = bean.getFileData().getSize();
		if (fileSizeInBytes > MAX_FILE_SIZE_LIMIT) {
			errorMessage = "File size exceeds 5 MB. Please upload a file with size less than 5 MB";
			return errorMessage;
		}
		
		if(!FileUploadHelper.checkPdfFileContentType(file.getContentType())) {		//File Header Validation added as per card: 12152
			assg_logger.error("Error Invalid file with Content-Type: {} for subject: {} by student: {}", file.getContentType(), bean.getSubject(), bean.getSapId());
			return "File type not supported. Please upload a PDF file!";
		}

		// Replace special characters in file name
		String subject = bean.getSubject();
		subject = subject.replaceAll("'", "_");
		subject = subject.replaceAll(",", "_");
		subject = subject.replaceAll("&", "and");
		subject = subject.replaceAll(" ", "_");
		subject = subject.replaceAll(":", "");

		if (!(fileName.toUpperCase().endsWith(".PDF"))) {
			errorMessage = "File type not supported. Please upload .pdf file.";
			return errorMessage;
		}
		String folderPath =  "Submissions/"+ month + year + "/" + subject + "/";
		// Add Random number to avoid student guessing names of other student's
		// assignment files
		fileName = sapId + "_" + subject + "_" + RandomStringUtils.randomAlphanumeric(12) + ".pdf";
		bean.setStudentFilePath(month + year + "/" + subject + "/"+fileName);
		bean.setPreviewPath(month + year + "/" + subject + "/"+fileName);
		/*try {
			// PDF stores first 4 letters as %PDF, which can be used to check if a file is
			// actually a pdf file and not just going by extension
			InputStream tempInputStream = file.getInputStream();
			;
			byte[] initialbytes = new byte[4];
			tempInputStream.read(initialbytes);

			tempInputStream.close();
			String fileType = new String(initialbytes);

			if (!"%PDF".equalsIgnoreCase(fileType)) {
				errorMessage = "File is not a PDF file. Please upload .pdf file.";
				return errorMessage;
			}

			inputStream = file.getInputStream();
			String filePath = SUBMITTED_ASSIGNMENT_FILES_PATH + month + year + "/" + subject + "/" + fileName;
			String previewPath = month + year + "/" + subject + "/" + fileName;
			// Check if Folder exists which is one folder per Exam (Jun2015, Dec2015 etc.)
			File folderPath = new File(SUBMITTED_ASSIGNMENT_FILES_PATH + month + year + "/" + subject);
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
			errorMessage = "Error in uploading file for " + bean.getSubject() + " : " + e.getMessage();
			
		}*/
		HashMap<String,String> s3_response = amazonS3Helper.uploadFiles(file,folderPath,"assignment-files",folderPath+fileName);
		if(!s3_response.get("status").equalsIgnoreCase("success")) {
			return "Unable to upload file: " + s3_response.get("url");
		}
		
		boolean isFileCorupted = fileUploadHelper.isFileCorrupted(s3_response.get("url"));
		if(isFileCorupted) {
			assg_logger.error("Error Corrupted/Unreadable File Sapid:{} Subject:{} FilePath URL:{}", sapId, bean.getSubject(), s3_response.get("url"));
			return "Unable to read PDF, the uploaded file must be corrupt or blank. Please verify and submit an updated file.";
		}
		
		boolean isQPFileUploaded = fileUploadHelper.isQPFileUploaded(s3_response.get("url"),ASSIGNMENT_FILES_PATH+ bean.getQuestionFilePreviewPath(),null,SERVER_PATH);
		if(isQPFileUploaded) {
			assg_logger.error("Error QP File Uploaded Sapid:{} Month/Year:{}/{} Subject:{} On Attempt:{} URL/filepath:{}",
					sapId, month, year, bean.getSubject(), Integer.parseInt(bean.getAttempts()) + 1, s3_response.get("url"));
			errorMessage = "Invalid Submission, the uploaded file is the same as a question paper file. Please upload a valid file with answer/s.";
		}
		
		return errorMessage; 
	}

	@RequestMapping(value = "/student/modelAnswers", method = { RequestMethod.GET })
	public ModelAndView modelAnswers(HttpServletRequest request, HttpServletResponse response, Model m) {

		if (!checkSession(request, response)) {
			redirectToPortalApp(response);
			return null;
		}

		return new ModelAndView("assignment/modelAnswersDemo");
	}

	@RequestMapping(value = "/admin/extendedAssignmentSubmission", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView assignmentAttemptsForm(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute AssignmentFileBean bean) {
		ModelAndView modelandView = new ModelAndView("changeAssignmentAttempts");
		AssignmentsDAO dao = (AssignmentsDAO) act.getBean("asignmentsDAO");
		
		String successMessage = (String) request.getSession().getAttribute("successMsg");
		request.getSession().removeAttribute("successMsg");
		if(successMessage != null){
			request.setAttribute("success", "true");
			request.setAttribute("successMessage", successMessage);
		}
		
		
		
		List<AssignmentFileBean> timeExtendedStudentIdSubjectList = dao.assignmentAttemptsSearch();
		int rowCount = timeExtendedStudentIdSubjectList.size();
		modelandView.addObject("subjectList", getSubjectList());
		modelandView.addObject("bean", bean);
		request.getSession().removeAttribute("deletedassignments");
		modelandView.addObject("rowCount", rowCount);
		modelandView.addObject("timeExtendedStudentIdSubjectList", timeExtendedStudentIdSubjectList);
		modelandView.addObject("yearList", currentYearList);
		return modelandView;
	}

	@RequestMapping(value = "/admin/updateAssignmentSubmissionTime", method = { RequestMethod.POST })
	public ModelAndView assignmentAttempts(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute AssignmentFileBean bean) {
		ModelAndView modelandView = new ModelAndView("changeAssignmentAttempts");
		AssignmentsDAO dao = (AssignmentsDAO) act.getBean("asignmentsDAO");

		String userid = (String) request.getSession().getAttribute("userId");
		dao.updateExtendedAssignmentSubmission(bean, userid);

		List<AssignmentFileBean> timeExtendedStudentIdSubjectList = dao.assignmentAttemptsSearch();
		int rowCount = timeExtendedStudentIdSubjectList.size();
		modelandView.addObject("subjectList", getSubjectList());
		modelandView.addObject("bean", bean);
		modelandView.addObject("rowCount", rowCount);
		modelandView.addObject("timeExtendedStudentIdSubjectList", timeExtendedStudentIdSubjectList);
		modelandView.addObject("yearList", currentYearList);

		return modelandView;
	}

	@RequestMapping(value = "/admin/deleteExtendedAssignmentSubmission", method = { RequestMethod.POST })
	public ModelAndView deleteExtendedAssignmentSubmission(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute AssignmentFileBean bean) {
		ModelAndView modelandView = new ModelAndView("changeAssignmentAttempts");
		AssignmentsDAO dao = (AssignmentsDAO) act.getBean("asignmentsDAO");

		modelandView.addObject("yearList", currentYearList);
		modelandView.addObject("subjectList", getSubjectList());
		modelandView.addObject("bean", bean);

		dao.deleteExtendedAssignmentSubmission(bean);
		List<AssignmentFileBean> timeExtendedStudentIdSubjectList = dao.assignmentAttemptsSearch();
		for(AssignmentFileBean bean1 :timeExtendedStudentIdSubjectList) {
			System.out.println("getId  "+bean1.getId());
		}
		int rowCount = timeExtendedStudentIdSubjectList.size();
		modelandView.addObject("rowCount", rowCount);
		modelandView.addObject("timeExtendedStudentIdSubjectList", timeExtendedStudentIdSubjectList);

		return modelandView;
	}

	@RequestMapping(value = "/admin/deleteExtendedAssignmentSubmissionNew", method = { RequestMethod.POST })
	public ResponseEntity<ResponseBean> updateProgramStructure(HttpServletRequest request,
			@RequestBody AssignmentFileBean bean) {
		ResponseBean response = new ResponseBean();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		try {
			bean.setLastModifiedBy((String) request.getSession().getAttribute("userId"));
			AssignmentsDAO dao = (AssignmentsDAO) act.getBean("asignmentsDAO");
			int responseData = dao.deleteExtendedAssignmentSubmission(bean);
			if (responseData > 0) {
				response.setStatus("Success");
				response.setMessage("Successfully data updated");
				response.setCode(responseData);
			} else {
				response.setStatus("Error");
				response.setMessage("" + responseData);
			}
		} catch (Exception e) {
			response.setStatus("Error");
			response.setMessage(e.getMessage());
		}
		return new ResponseEntity<ResponseBean>(response, headers, HttpStatus.OK);
	}

	public static void main(String[] args) {

		String fileName = "TestFile.myfile_test.pdf";
		fileName = fileName.substring(0, fileName.lastIndexOf(".")) + "_" + RandomStringUtils.randomAlphanumeric(10)
				+ fileName.substring(fileName.lastIndexOf("."), fileName.length());
	}

//	api to update registration data, add masterkey from Students
//	@RequestMapping(value = "/m/updateConsumerPrgStructIdForProgChange", method = RequestMethod.POST)
//	public void updateConsumerPrgStructIdForProgChange(HttpServletRequest request) throws Exception {
//
//		asgService.updateConsumerPrgStructIdForProgChange();
//
//	}

	@RequestMapping(value = "/admin/generateLastCycleAssgSubmissionLink", method = { RequestMethod.POST })
	public ModelAndView generateLastCycleAssgSubmissionLink(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute AssignmentFileBean bean) {
		ModelAndView modelandView = new ModelAndView("changeAssignmentAttempts");
		AssignmentsDAO dao = (AssignmentsDAO) act.getBean("asignmentsDAO");
		ExamBookingDAO eDao = (ExamBookingDAO) act.getBean("examBookingDAO");
		String year = bean.getYear();
		String month = bean.getMonth();
		String subject = URLEncoder.encode(bean.getSubject());
		String sapid = bean.getSapId();

		StudentExamBean student = eDao.getSingleStudentsData(sapid);
		modelandView.addObject("yearList", currentYearList);
		modelandView.addObject("subjectList", getSubjectList());
		modelandView.addObject("bean", bean);
		List<AssignmentFileBean> extendedStudent = dao.timeExtendedAssignmentForStudent(sapid);
		if (extendedStudent.size() > 0) {

			AssignmentFileBean assignmentFile = dao.getSingleAssignmentWithMasterkeyAndYearMonth(bean, student);
			if (assignmentFile != null) {
				String startDate = assignmentFile.getStartDate();
				startDate = startDate.replaceAll("T", " ");
				assignmentFile.setStartDate(startDate.substring(0, 19));

				String endDate = assignmentFile.getEndDate();
				endDate = endDate.replaceAll("T", " ");
				assignmentFile.setEndDate(endDate.substring(0, 19));
				String generatedUrl = SERVER_PATH + "exam/student/viewLastCycleSingleAssignment?year=" + year + "&month="
						+ month + "&subject=" + subject + "&startDate=" + assignmentFile.getStartDate() + "&endDate="
						+ assignmentFile.getEndDate();
				modelandView.addObject("generatedUrl", generatedUrl);
			} else {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No assignment found");
			}
		} else {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Student not found in extended student list");
		}
		List<AssignmentFileBean> timeExtendedStudentIdSubjectList = dao.assignmentAttemptsSearch();
		int rowCount = timeExtendedStudentIdSubjectList.size();
		modelandView.addObject("rowCount", rowCount);
		modelandView.addObject("timeExtendedStudentIdSubjectList", timeExtendedStudentIdSubjectList);

		modelandView.addObject("activeTab", 2);
		return modelandView;
	}
	@RequestMapping(value = "/admin/transferFilesFromLocalToS3", method = RequestMethod.GET)
	public void transferFilesFromLocalToS3(HttpServletRequest request, HttpServletResponse response) 
	{
		aws_logger.info(" Batch Job Of Transferring File Started");
			
			String month="Dec";
			String year="2021"; 
			
			AssignmentsDAO dao = (AssignmentsDAO) act.getBean("asignmentsDAO");
			List<AssignmentFileBean> assignmentFiles = dao.getAssignmentsForACycle(year,month);
			aws_logger.info("get all assignments count:"+assignmentFiles.size());
			batchJobForExistingFileData(assignmentFiles);
			aws_logger.info(" Batch Job Of Transferring Content Ended ");
	}
	public int  batchJobForExistingFileData(List<AssignmentFileBean> assignmentFiles) 
	{
		
		String bucketName ="assignment-files";
		HashMap<String,String> s3_response = new HashMap<String,String>();
		List<String> success_list  = new ArrayList<String>();
		List<String> error_list = new ArrayList<String>();
		List<String> success_delete_count = new ArrayList<String>();
		List<String> error_delete_count = new ArrayList<String>();
		aws_logger.info("inside batchjob calling loop");
		int counter = 0;
		
		for(AssignmentFileBean filesDetail:assignmentFiles) {
			
			try {
				final File folder = new File(filesDetail.getStudentFilePath());
				String prefix="AssignmentFiles/"; 
				String baseUrl = FilenameUtils.getPath(filesDetail.getStudentFilePath());
				if (baseUrl != null  && baseUrl.startsWith(prefix)) {
					baseUrl = baseUrl.substring(prefix.length());
				}
				String fileName = FilenameUtils.getBaseName(filesDetail.getStudentFilePath())
				 + "." + FilenameUtils.getExtension(filesDetail.getStudentFilePath());
				
				aws_logger.info(" baseUrl "+baseUrl);
				aws_logger.info(" fileName "+fileName);
				fileName = baseUrl + fileName;
			
				if(!folder.exists()) {
					aws_logger.info("folder exists?");	
				    //check whether file is present on s3 or not
				
					s3_response = amazonS3Helper.checkWhetherFilePresentOnS3(bucketName, fileName);
					
					if(s3_response.get("status").equals("success")) { 
						updateFileUrlLink(filesDetail,bucketName);
						success_list.add("File Details " +filesDetail.toString());
						continue;
					}
					
					aws_logger.info(" checkWhetherFilePresent response :-  "+s3_response.get("url"));
					error_list.add(s3_response.get("url") +filesDetail.toString());
					continue;
								
				} 
				s3_response = amazonS3Helper.uploadLocalFile(filesDetail.getStudentFilePath(),fileName,bucketName,baseUrl);
				
				String OriginalPath = filesDetail.getStudentFilePath();
				if(s3_response.get("status").equals("success")) {
					
					//Update FilePath In DB
					if((updateFileUrlLink(filesDetail,bucketName)) > 0) {
					//Delete File From Local
					if(deleteFileFromLocal(OriginalPath) == 1)
					{
									aws_logger.info("SuccessFully Deleted File From Local "+OriginalPath);
									success_delete_count.add(OriginalPath);
					}
					}else {
							aws_logger.info("Error in deleting File From Local "+OriginalPath);
							error_delete_count.add(OriginalPath);
										
					}
					aws_logger.info("SuccessFully added file in amazon s3 .  "+filesDetail.toString());
					success_list.add("File Details " +filesDetail.toString());
				}
			}catch(Exception e)
			{
		 		
		 		aws_logger.error("Upload File Error ",e);
		 		aws_logger.info("Error in uploading File."+filesDetail.toString());
		 		error_list.add("File Details " +filesDetail.toString());
		 		
			}//END OF TRY AND CATCH
		}//END OF LOOP
		
		aws_logger.info("Total Files "+assignmentFiles.size());
		aws_logger.info("Success_count "+success_list.size());
		aws_logger.info("success_list "+success_list.toString());
		aws_logger.info("Error Count "+error_list.size());
		aws_logger.info("Error_list "+error_list.toString());
		aws_logger.info("SUCCESS/FAILURE DELETE FROM LOCAL COUNT "+success_delete_count.size()+"/"+ error_delete_count.size());
		aws_logger.info("Error in Local Delete File  "+error_delete_count.toString());
		
		return 1;
		/*
		 * String subject="TestSubject"; String folderPath = "Submissions/"+ month +
		 * year + "/" + subject + "/"; String localFilePath =
		 * "C://Users/user/Downloads/business.pdf"; String fileName = "testFile.pdf";
		 */
		
		
	}
	public int deleteFileFromLocal(String filePath)
	{
		boolean result = false;
		try {
       	File file = new File(filePath);
       	result = file.delete();
       	
		}
		catch(Exception e)
		{
			aws_logger.error("Error While Deleting file From local ",e);
		}
        if(result)
        	return 1;
        else
        	return 0;
       
       
          
	}
	public int updateFileUrlLink(AssignmentFileBean fileData,String bucketName)
	{
		final int index = fileData.getStudentFilePath().indexOf("/", fileData.getStudentFilePath().indexOf("/") + 1);
		fileData.setStudentFilePath(fileData.getStudentFilePath().substring(index + 1));

		int update = 0;
		AssignmentsDAO dao = (AssignmentsDAO) act.getBean("asignmentsDAO");
		update = dao.changeSubmissionTableFilePath(fileData); 
		if(update > 0 ) {
			return 1;
		}else {
			aws_logger.info("Error In uploading in database.(S3 Uploaded SuccessFully) "+fileData.toString());
			return 0;
		}

		
	}
	

	@RequestMapping(value = "/admin/deleteExtendedAssignmentSubmissionNewly", method = { RequestMethod.POST })
	public ResponseEntity<ResponseBean> deleteProgramStructureByadmin(HttpServletRequest request,
			@RequestBody AssignmentFileBean bean) {
		ResponseBean response = new ResponseBean();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		try {
			bean.setLastModifiedBy((String) request.getSession().getAttribute("userId"));
			AssignmentsDAO dao = (AssignmentsDAO) act.getBean("asignmentsDAO");
			 int deletedEntries=0;	 
		   List<String> ids=bean.getIds();  
		   ArrayList<String>extendassignmentId=new ArrayList<String>();   
		   for(String id :ids) {
			   if(!StringUtils.isBlank(id)) {
				   
				   extendassignmentId.add(id);
			   }
			   
		   }
		   try {
		   deletedEntries= dao.getdeletedList(extendassignmentId);
		   }catch(Exception e) {
			   deletedEntries=0;
		   }
		   String successMessage= null;
			if (deletedEntries > 0) {
				
				response.setStatus("Success");
				response.setMessage("Successfully data deleted");
				response.setCode(deletedEntries);
				if(deletedEntries ==  1) {
					successMessage="Successfully deleted "+deletedEntries+" entry";
				}else {
					successMessage="Successfully deleted "+deletedEntries+" entries";
				}
				request.getSession().setAttribute("successMsg", successMessage);
			} else {
				successMessage="Error in getting delete your requested entries";
			}
		} catch (Exception e) {
			response.setStatus("Error");
			response.setMessage(e.getMessage());
		}
	 
		return new ResponseEntity<ResponseBean>(response, headers, HttpStatus.OK);
	}
	
	
	
}
