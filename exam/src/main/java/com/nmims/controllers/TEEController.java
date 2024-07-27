package com.nmims.controllers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.nmims.beans.DemoExamAttendanceBean;
import com.nmims.beans.DemoExamBean;
import com.nmims.beans.Demoexam_keysBean;
import com.nmims.beans.ExamOrderExamBean;
import com.nmims.beans.FileBean;
import com.nmims.beans.MettlHookResponseBean;
import com.nmims.beans.MettlResponseBean;
import com.nmims.beans.Person;
import com.nmims.beans.ProgramSubjectMappingExamBean;
import com.nmims.beans.QuestionFileBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.daos.DemoExamDAO;
import com.nmims.daos.ExamBookingDAO;
import com.nmims.daos.MettlTeeDAO;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.helpers.EmailHelper;
import com.nmims.helpers.ExcelHelper;
import com.nmims.helpers.MettlHelper;
import com.nmims.helpers.TeeSSOHelper;
import com.nmims.services.DemoExamServices;

@Controller
public class TEEController extends BaseController{
	
	private static final Logger logger = LoggerFactory.getLogger(TEEController.class);
	
	@Autowired
	ApplicationContext act;
	
	@Autowired
	MettlHelper mettlHelper;//for /m/generatePGAssessment - Vilpesh 2022-03-24

	@Autowired
	EmailHelper emailHelper;
	
	@Autowired
	TeeSSOHelper teeSSOHelper;
	
	@Autowired
	 DemoExamServices demoService;
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
		
		return null;
	}
	
	@RequestMapping(value = "/uploadQuestionFileForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String uploadQuestionFileForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {

		FileBean fileBean = new FileBean();
		m.addAttribute("fileBean",fileBean);
		return "uploadQuestionFile";
	}
	
	@RequestMapping(value = "/uploadQuestionFile", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView uploadQuestionFile(FileBean fileBean, BindingResult result,HttpServletRequest request, Model m){
		ModelAndView modelnView = new ModelAndView("uploadQuestionFile");
		try{
			StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			String userId = (String)request.getSession().getAttribute("userId");
			ExcelHelper excelHelper = new ExcelHelper();
			ArrayList<List> resultList = excelHelper.readQuestionFileMCQExcel(fileBean);
			//List<StudentMarksBean> marksBeanList = excelHelper.readMarksExcel(fileBean, programList, subjectList);

			ArrayList<QuestionFileBean> mcqQuestionBeanList = (ArrayList<QuestionFileBean>)resultList.get(0);
			ArrayList<QuestionFileBean> errorBeanList = (ArrayList<QuestionFileBean>)resultList.get(1);

			if(errorBeanList.size() > 0){
				request.setAttribute("errorBeanList", errorBeanList);
				return modelnView;
			}


			/*ArrayList<String> errorList = dao.batchUpdate(marksBeanList, "Assignment");
*/
			processMCQQuestions(mcqQuestionBeanList);
			setSuccess(request, mcqQuestionBeanList.size() + " questions scanned successfully");
			
			resultList = excelHelper.readQuestionFileDescriptiveExcel(fileBean);
			ArrayList<QuestionFileBean> descriptiveQuestionBeanList = (ArrayList<QuestionFileBean>)resultList.get(0);
			errorBeanList = (ArrayList<QuestionFileBean>)resultList.get(1);

			if(errorBeanList.size() > 0){
				request.setAttribute("errorBeanList", errorBeanList);
				return modelnView;
			}
			processDescriptiveQuestions(descriptiveQuestionBeanList);
			
			ArrayList<List> questionBeanList = new ArrayList<>();
			questionBeanList.add(mcqQuestionBeanList);
			questionBeanList.add(descriptiveQuestionBeanList);
			
			return new ModelAndView("questionReviewExcelView","questionBeanList",questionBeanList);
			
		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in inserting marks records.");

		}
		fileBean = new FileBean();
		m.addAttribute("fileBean",fileBean);
		return modelnView;
		
		
	}

	private ArrayList<QuestionFileBean> processDescriptiveQuestions(ArrayList<QuestionFileBean> questionBeanList) {
		ArrayList<String> allQuestionsList = new ArrayList<>();
		
		for (QuestionFileBean questionBean : questionBeanList) {
			allQuestionsList.add(questionBean.getQuestionText());
		}
		
		for (QuestionFileBean currentQuestionBean : questionBeanList) {
			String currentQuestionText = currentQuestionBean.getQuestionText().toUpperCase().trim();
			String currentRowNumber = currentQuestionBean.getRowNumber();
			
			checkQuestionRules(currentQuestionBean);
			
			for (QuestionFileBean otherQuestionBean : questionBeanList) {
				String otherQuestionText = otherQuestionBean.getQuestionText().toUpperCase().trim();
				String otherRowNumber = otherQuestionBean.getRowNumber();
				
				if(!currentRowNumber.equals(otherRowNumber)){
					//1. Check if question text matches
					if(otherQuestionText.contains(currentQuestionText)){
						currentQuestionBean.setReviewRemarks(currentQuestionBean.getReviewRemarks() + "\nDuplicate Question : Row " + otherRowNumber);
					}
					
				}
			}
		}
		// TODO Auto-generated method stub
		return null;
	}
	
	private ArrayList<QuestionFileBean> processMCQQuestions(ArrayList<QuestionFileBean> questionBeanList) {
		ArrayList<String> allQuestionsList = new ArrayList<>();
		
		for (QuestionFileBean questionBean : questionBeanList) {
			allQuestionsList.add(questionBean.getQuestionText());
		}
		
		for (QuestionFileBean currentQuestionBean : questionBeanList) {
			String currentQuestionText = currentQuestionBean.getQuestionText().toUpperCase().trim();
			currentQuestionText = currentQuestionText.replaceAll("\\s","");//remove spaces
			currentQuestionText = currentQuestionText.replaceAll("_","");//remove spaces
			
			String currentRowNumber = currentQuestionBean.getRowNumber();
			
			String currentOption1 = currentQuestionBean.getOption1() != null ? currentQuestionBean.getOption1().toUpperCase().trim() : "";
			String currentOption2 = currentQuestionBean.getOption2() != null ? currentQuestionBean.getOption2().toUpperCase().trim() : "";
			String currentOption3 = currentQuestionBean.getOption3() != null ? currentQuestionBean.getOption3().toUpperCase().trim() : "";
			String currentOption4 = currentQuestionBean.getOption4() != null ? currentQuestionBean.getOption4().toUpperCase().trim() : "";
			String currentOption5 = currentQuestionBean.getOption5() != null ? currentQuestionBean.getOption5().toUpperCase().trim() : "";
			
			ArrayList<String> currentOptionsList = new ArrayList<>();
			currentOptionsList.add(currentOption1);
			currentOptionsList.add(currentOption2);
			currentOptionsList.add(currentOption3);
			currentOptionsList.add(currentOption4);
			currentOptionsList.add(currentOption5);
			
			if(!"".equals(currentOption5)){
				currentQuestionBean.setReviewRemarks(currentQuestionBean.getReviewRemarks() + "\nOption 5 is not Empty");
			}
			
			checkOptionRules(currentQuestionBean);
			checkCorrectOptionRules(currentQuestionBean);
			checkQuestionRules(currentQuestionBean);
			
			for (QuestionFileBean otherQuestionBean : questionBeanList) {
				String otherQuestionText = otherQuestionBean.getQuestionText().toUpperCase().trim();
				otherQuestionText = otherQuestionText.replaceAll("\\s","");//remove spaces
				otherQuestionText = otherQuestionText.replaceAll("_","");//remove spaces
				
				String otherRowNumber = otherQuestionBean.getRowNumber();
				
				String otherOption1 = otherQuestionBean.getOption1() != null ? otherQuestionBean.getOption1().toUpperCase().trim() : "";
				String otherOption2 = otherQuestionBean.getOption2() != null ? otherQuestionBean.getOption2().toUpperCase().trim() : "";
				String otherOption3 = otherQuestionBean.getOption3() != null ? otherQuestionBean.getOption3().toUpperCase().trim() : "";
				String otherOption4 = otherQuestionBean.getOption4() != null ? otherQuestionBean.getOption4().toUpperCase().trim() : "";
				String otherOption5 = otherQuestionBean.getOption5() != null ? otherQuestionBean.getOption5().toUpperCase().trim() : "";
				
				ArrayList<String> otherOptionsList = new ArrayList<>();
				otherOptionsList.add(otherOption1);
				otherOptionsList.add(otherOption2);
				otherOptionsList.add(otherOption3);
				otherOptionsList.add(otherOption4);
				otherOptionsList.add(otherOption5);
				
				if(!currentRowNumber.equals(otherRowNumber) && !"".equals(currentQuestionText)){
					//1. Check if question text matches
					if(otherQuestionText.contains(currentQuestionText)){
						
						if(otherOptionsList.contains(currentOption1) && otherOptionsList.contains(currentOption2) 
								&& otherOptionsList.contains(currentOption3) && otherOptionsList.contains(currentOption4) ){
							
							currentQuestionBean.setReviewRemarks(currentQuestionBean.getReviewRemarks() + "\nDQ: Dup Options :Row " + otherRowNumber);
						}else{
							currentQuestionBean.setReviewRemarks(currentQuestionBean.getReviewRemarks() + "\nDQ: Diff Options :Row " + otherRowNumber);
						}
						
					}
					
					
					
				}
			}
		}
		// TODO Auto-generated method stub
		return null;
	}

	private void checkOptionRules(QuestionFileBean currentQuestionBean) {
		String option1 = currentQuestionBean.getOption1() != null ? currentQuestionBean.getOption1().toUpperCase().trim() : "";
		String option2 = currentQuestionBean.getOption2() != null ? currentQuestionBean.getOption2().toUpperCase().trim() : "";
		String option3 = currentQuestionBean.getOption3() != null ? currentQuestionBean.getOption3().toUpperCase().trim() : "";
		String option4 = currentQuestionBean.getOption4() != null ? currentQuestionBean.getOption4().toUpperCase().trim() : "";
		String option5 = currentQuestionBean.getOption5() != null ? currentQuestionBean.getOption5().toUpperCase().trim() : "";
		
		//Please always add caution words in capital letters
		ArrayList<String> cautionWordsList = new ArrayList<>( Arrays.asList("NONE", "ALL", "BOTH", "ONLY", "NEITHER", "A.", "A)", "I.", "II.", "I)"));
		
		ArrayList<String> validCautionPhrases = new ArrayList<>( Arrays.asList("NONE OF THE GIVEN OPTIONS", 
				"ALL OF THE GIVEN OPTIONS"));
		
		for (String cautionWord : cautionWordsList) {
			List<String> option1Words = Arrays.asList(option1.split("\\s+"));
			List<String> option2Words = Arrays.asList(option2.split("\\s+"));
			List<String> option3Words = Arrays.asList(option3.split("\\s+"));
			List<String> option4Words = Arrays.asList(option4.split("\\s+"));
			
			if(option1Words.contains(cautionWord) || option2Words.contains(cautionWord)  || option3Words.contains(cautionWord)  || option4Words.contains(cautionWord)){
				
				if(!validCautionPhrases.contains(option1) && !validCautionPhrases.contains(option2) 
						&& !validCautionPhrases.contains(option3) && !validCautionPhrases.contains(option4)){
					currentQuestionBean.setReviewRemarks(currentQuestionBean.getReviewRemarks() + "\nOption contains "+cautionWord);
				}
				
			}
		}
		
		if(		(option1.equals(option2) && !"".equals(option1)) 
				|| (option1.equals(option3)  && !"".equals(option1)) 
				|| (option1.equals(option4)  && !"".equals(option1))  
				|| (option2.equals(option3)   && !"".equals(option2)) 
				|| (option2.equals(option4)   && !"".equals(option2)) 
				|| (option3.equals(option4) && !"".equals(option3)) 
				
				){
			currentQuestionBean.setReviewRemarks(currentQuestionBean.getReviewRemarks() + "\nDuplicate Options");
		}
		
	}
	
	private void checkQuestionRules(QuestionFileBean currentQuestionBean) {
		String questionText = currentQuestionBean.getQuestionText() != null ? currentQuestionBean.getQuestionText().toUpperCase().trim() : "";
		
		//Please always add caution words in capital letters
		ArrayList<String> cautionWordsList = new ArrayList<>( Arrays.asList("SHOW", "DRWA", "ILLUSTRATE", "DEPICT", "DIAGRAM", "FIGURE", "DIGRAM"));
		
		for (String cautionWord : cautionWordsList) {
			if(questionText.contains(cautionWord)){
				currentQuestionBean.setReviewRemarks(currentQuestionBean.getReviewRemarks() + "\nQuestion contains "+cautionWord);
			}
		}
		
		
	}
	
	private void checkCorrectOptionRules(QuestionFileBean currentQuestionBean) {
		String option1 = currentQuestionBean.getOption1() != null ? currentQuestionBean.getOption1().toUpperCase().trim() : "";
		String option2 = currentQuestionBean.getOption2() != null ? currentQuestionBean.getOption2().toUpperCase().trim() : "";
		String option3 = currentQuestionBean.getOption3() != null ? currentQuestionBean.getOption3().toUpperCase().trim() : "";
		String option4 = currentQuestionBean.getOption4() != null ? currentQuestionBean.getOption4().toUpperCase().trim() : "";
		String option5 = currentQuestionBean.getOption5() != null ? currentQuestionBean.getOption5().toUpperCase().trim() : "";
		
		String correctAnswer = currentQuestionBean.getCorrectAnswer();
		
		int numberOfOptions = 0;
		if(!"".equals(option1)){
			numberOfOptions++;
		}
		if(!"".equals(option2)){
			numberOfOptions++;
		}
		if(!"".equals(option3)){
			numberOfOptions++;
		}
		if(!"".equals(option4)){
			numberOfOptions++;
		}
		
		
		if(numberOfOptions == 3){
			currentQuestionBean.setReviewRemarks(currentQuestionBean.getReviewRemarks() + "\nQuestion has 3 options.");
		}
		
		if(numberOfOptions == 2 && !"1".equals(correctAnswer) && !"2".endsWith(correctAnswer)){
			currentQuestionBean.setReviewRemarks(currentQuestionBean.getReviewRemarks() + "\nTwo options, but correct answer is 3 or 4.");
		}
		
		if(numberOfOptions == 2 && !"1".equals(correctAnswer) && !"2".endsWith(correctAnswer) && !"3".endsWith(correctAnswer) && !"4".endsWith(correctAnswer)){
			currentQuestionBean.setReviewRemarks(currentQuestionBean.getReviewRemarks() + "\nCorrect answer is wrong.");
		}
		
	}
	
	
	@RequestMapping(value = "/student/viewModelQuestionForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView viewModelQuestionForm(HttpServletResponse response,HttpServletRequest request){
		ModelAndView modelnView = new ModelAndView("viewModelQuestion");
		String userId = (String)request.getSession().getAttribute("userId");
		StudentExamBean student = (StudentExamBean)request.getSession().getAttribute("studentExam");
		ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
		DemoExamDAO demoExamDAO = (DemoExamDAO) act.getBean("demoExamDAO");
		List<DemoExamBean> demoExamBeansList = demoExamDAO.getDemoExamList();
		List<String> attendDemoExamIds = demoExamDAO.getAttendanceList(userId);
		List<ExamOrderExamBean> liveFlagList = eDao.getLiveFlagDetails();
		ArrayList<StudentMarksBean> allStudentRegistrations = eDao.getAllRegistrationsFromSAPID(student.getSapid());
		HashMap<String, StudentMarksBean> monthYearAndStudentRegistrationMap = new HashMap<>();
		
		for (StudentMarksBean bean : allStudentRegistrations) {
			monthYearAndStudentRegistrationMap.put(bean.getMonth() + "-" + bean.getYear(), bean);
		}
		StudentMarksBean studentRegistrationForAcademicSession = getStudentRegistrationForForSpecificLiveSettings(monthYearAndStudentRegistrationMap, liveFlagList, "acadSessionLive");
		StudentMarksBean studentRegistrationForAssignment = getStudentRegistrationForForSpecificLiveSettings(monthYearAndStudentRegistrationMap, liveFlagList, "assignmentLive");
		String liveTypeForCourses = "acadContentLive";
		if("Yes".equalsIgnoreCase((String)request.getSession().getAttribute("earlyAccess"))){
			liveTypeForCourses = "acadContentLiveNextBatch";
		}
		StudentMarksBean studentRegistrationForCourses = getStudentRegistrationForForSpecificLiveSettings(monthYearAndStudentRegistrationMap, liveFlagList, liveTypeForCourses);

		getCourses(student,request,eDao, studentRegistrationForCourses);
		ArrayList<String> subjects = (ArrayList<String>)request.getSession().getAttribute("studentCourses");
		ArrayList<DemoExamBean> demoexamList_tmp = new ArrayList<DemoExamBean>();
		ArrayList<DemoExamBean> attempt_demoexamList_tmp = new ArrayList<DemoExamBean>();
		DemoExamBean nextDemoExamBean = null;
		for (DemoExamBean demoExambean : demoExamBeansList) {
			if(subjects.contains(demoExambean.getSubject())) {
				demoexamList_tmp.add(demoExambean);
			}
			if(attempt_demoexamList_tmp.size() == 1 && demoExambean.getSubject().indexOf("Attempt") != -1 && !attendDemoExamIds.contains("" + demoExambean.getId())) {
				nextDemoExamBean = demoExambean;
			}
			if(attempt_demoexamList_tmp.size() == 0 && demoExambean.getSubject().indexOf("Attempt") != -1 && !attendDemoExamIds.contains("" + demoExambean.getId())) {
				attempt_demoexamList_tmp.add(demoExambean);
			} 
		}
		modelnView.addObject("demoExamList", demoexamList_tmp);
		modelnView.addObject("demoExamListAttempt", attempt_demoexamList_tmp);
		modelnView.addObject("nextDemoExamBean", nextDemoExamBean);
	   // eDao.getSingleStudentsData(userId);
				return modelnView;
	
	}
	
	@RequestMapping(value = "/student/startDemoStart", method = {RequestMethod.POST})
	public ModelAndView startDemoStart(HttpServletRequest request, HttpServletResponse response, @ModelAttribute DemoExamBean demoExamBean) throws Exception
	{
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		StudentExamBean student = (StudentExamBean)request.getSession().getAttribute("studentExam");
		DemoExamDAO demoExamDAO = (DemoExamDAO) act.getBean("demoExamDAO");
		DemoExamAttendanceBean demoExamAttendanceBean = new DemoExamAttendanceBean();
		demoExamAttendanceBean.setSapid(student.getSapid());
		demoExamAttendanceBean.setAccessKey(demoExamBean.getKey());
		demoExamAttendanceBean.setDemoExamId(demoExamBean.getId());
		demoExamDAO.createExamAttendance(demoExamAttendanceBean);
		String responseUrl = teeSSOHelper.generateMettlLinkForDemoExam(demoExamBean, student);
		return new ModelAndView("redirect:" + responseUrl);
	}
	
	
	@RequestMapping(value = "/m/startMettlExamDemo", method = {RequestMethod.POST}, consumes = "application/json", produces = "application/json")
	public ResponseEntity<HashMap<String, String>> startMettlExamDemo(@RequestBody MettlHookResponseBean mettlHookResponseBean){
		HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json"); 
        HashMap<String, String> response = new HashMap<String, String>();
        logger.info("startWebhook: Student EmailId: " + mettlHookResponseBean.getEmail() + " | invitation key: " + mettlHookResponseBean.getInvitation_key());
        try {
	        DemoExamDAO demoExamDAO = (DemoExamDAO) act.getBean("demoExamDAO");
	        StudentExamBean studentBean = demoExamDAO.getStudentByEmailId(mettlHookResponseBean.getEmail());
	        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
	        Date dateobj = new Date();
	        DemoExamAttendanceBean demoExamAttendanceBean = new DemoExamAttendanceBean();
	        demoExamAttendanceBean.setSapid(studentBean.getSapid());
	        demoExamAttendanceBean.setAccessKey(mettlHookResponseBean.getInvitation_key());
	        demoExamAttendanceBean.setStartedTime(df.format(dateobj));
	        demoExamDAO.updateStartExamAttendance(demoExamAttendanceBean);
	        response.put("status", "success");
	        logger.info("---->>> startwebhook: completed start request with success : " + mettlHookResponseBean.getEmail() + " | " + mettlHookResponseBean.getInvitation_key());
	        return new ResponseEntity<HashMap<String, String>>(response, headers,  HttpStatus.OK);
        }
        catch (Exception e) {
			// TODO: handle exception
        	
        	response.put("status", "error");
        	logger.info("-------->>>> startwebhook: error in mettl webhook : " + e.getMessage());
        	return new ResponseEntity<HashMap<String, String>>(response, headers,  HttpStatus.OK);
		}
	}
	

	@RequestMapping(value = "/m/endMettlExamDemo", method = {RequestMethod.POST}, consumes = "application/json", produces = "application/json")
	public ResponseEntity<HashMap<String, String>> endMettlExamDemo(@RequestBody MettlHookResponseBean mettlHookResponseBean){
		HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json"); 
        HashMap<String, String> response = new HashMap<String, String>();
        logger.info("EndWebhook: Student EmailId: " + mettlHookResponseBean.getEmail() + " | invitation key: " + mettlHookResponseBean.getInvitation_key());
        try {
	        DemoExamDAO demoExamDAO = (DemoExamDAO) act.getBean("demoExamDAO");
	        StudentExamBean studentBean = demoExamDAO.getStudentByEmailId(mettlHookResponseBean.getEmail());
	        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
	        Date dateobj = new Date();
	        DemoExamAttendanceBean demoExamAttendanceBean = new DemoExamAttendanceBean();
	        demoExamAttendanceBean.setSapid(studentBean.getSapid());
	        demoExamAttendanceBean.setAccessKey(mettlHookResponseBean.getInvitation_key());
	        demoExamAttendanceBean.setEndTime(df.format(dateobj));
	        demoExamAttendanceBean.setMarkAttend("Y");
	        demoExamDAO.updateEndExamAttendance(demoExamAttendanceBean);
	        response.put("status", "success");
	        logger.info("---->>> endwebhook: completed request with success : " + mettlHookResponseBean.getEmail() + " | " + mettlHookResponseBean.getInvitation_key());
	        return new ResponseEntity<HashMap<String, String>>(response, headers,  HttpStatus.OK);
        }
        catch (Exception e) {
			// TODO: handle exception
        	
        	response.put("status", "error");
        	logger.info("-------->>>> endwebhook: error in mettl webhook : " + e.getMessage());
        	return new ResponseEntity<HashMap<String, String>>(response, headers,  HttpStatus.OK);
		}
	}
//	  to be deleted, api shifted to rest controller
//	@RequestMapping(value = "/m/viewModelQuestionForm", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")   
//	public ResponseEntity<HashMap<String, ArrayList<Demoexam_keysBean>>> viewMModelQuestionForm(HttpServletRequest request,
//			@RequestBody Person input) throws Exception {
//		
//		//ModelAndView modelnView = new ModelAndView("viewModelQuestion");
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json");
//		
//		ArrayList<Demoexam_keysBean> lstOfApplicableSubjects  =   demoService.retriveSubjects(input);
//		
//	
//		HashMap<String, ArrayList<Demoexam_keysBean>> Response= new HashMap<>();
//		Response.put("studentCourses", lstOfApplicableSubjects);
//		
//		return new ResponseEntity<HashMap<String, ArrayList<Demoexam_keysBean>>>(Response,headers, HttpStatus.OK);
//			}
/*-------------------------------------------------------------------------------------------------------------------------------------------------	*/
	
	private void getCourses(StudentExamBean student, HttpServletRequest request,ExamBookingDAO eDao, StudentMarksBean studentRegistrationData) {
		// TODO Auto-generated method stub
		ArrayList<String> allApplicableSubjects = new ArrayList<String>();
		ArrayList<String> currentSemSubjects = null;
		ArrayList<String> notPassedSubjects = null; //Subjects never appeared hence no entry in pass fail//
		
		if(studentRegistrationData != null){
			student.setSem(studentRegistrationData.getSem());
			student.setProgram(studentRegistrationData.getProgram());
			currentSemSubjects = getSubjectsForStudent(student);
		}

		if(currentSemSubjects == null){
			currentSemSubjects = new ArrayList<>();
		}
		allApplicableSubjects.addAll(currentSemSubjects);

		ArrayList<String> failedSubjects = eDao.getFailSubjectsNamesForAStudent(student.getSapid());


		if(failedSubjects != null){
			allApplicableSubjects.addAll(failedSubjects);
		}else{
			failedSubjects = new ArrayList<String>();
		}

		notPassedSubjects = eDao.getNotPassedSubjectsBasedOnSapid(student.getSapid());
		if(notPassedSubjects!=null && notPassedSubjects.size()>0){

			allApplicableSubjects.addAll(notPassedSubjects);
		}

		ArrayList<String> lstOfApplicableSubjects = new ArrayList<String>(new LinkedHashSet<String>(allApplicableSubjects));

		// remove WaiveOff Subject from applicable Subject list
		for(String subjects :allApplicableSubjects)
		{
			if(student.getWaivedOffSubjects().contains(subjects))
			{
				lstOfApplicableSubjects.remove(subjects);
			}
		}

	
		request.getSession().setAttribute("studentCourses", lstOfApplicableSubjects);
	}
	
	private ArrayList<String> getSubjectsForStudent(StudentExamBean student) {

		ArrayList<ProgramSubjectMappingExamBean> programSubjectMappingList = getProgramSubjectMappingList();
		ArrayList<String> subjects = new ArrayList<>();
		for (int i = 0; i < programSubjectMappingList.size(); i++) {
			ProgramSubjectMappingExamBean bean = programSubjectMappingList.get(i);

			if(
					bean.getPrgmStructApplicable().equals(student.getPrgmStructApplicable()) 
					&& bean.getProgram().equals(student.getProgram())
					&& bean.getSem().equals(student.getSem())
					&& !student.getWaivedOffSubjects().contains(bean.getSubject())//Subjects has not already cleared it
					){
				subjects.add(bean.getSubject());
			}
		}
		return subjects;
	}
	private ArrayList<ProgramSubjectMappingExamBean> programSubjectMappingList = null;
	public ArrayList<ProgramSubjectMappingExamBean> getProgramSubjectMappingList(){
		if(this.programSubjectMappingList == null || this.programSubjectMappingList.size() == 0){
			ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
			this.programSubjectMappingList = eDao.getProgramSubjectMappingList();
		}
		return programSubjectMappingList;
	}
	private StudentMarksBean getStudentRegistrationForForSpecificLiveSettings(
			HashMap<String, StudentMarksBean> monthYearAndStudentRegistrationMap,
			List<ExamOrderExamBean> liveFlagList, 
			String liveType) {

		double liveOrder = 0.0;
		String key = null;
		for (ExamOrderExamBean bean : liveFlagList) {
			double currentOrder = Double.parseDouble(bean.getOrder());

			if("acadSessionLive".equalsIgnoreCase(liveType)){
				if("Y".equalsIgnoreCase(bean.getAcadSessionLive()) && currentOrder > liveOrder){
					liveOrder = currentOrder;
					key = bean.getAcadMonth() + "-" + bean.getYear();
				}
			}else if("acadContentLive".equalsIgnoreCase(liveType)){
				if("Y".equalsIgnoreCase(bean.getAcadContentLive()) && currentOrder > liveOrder){
					liveOrder = currentOrder;
					key = bean.getAcadMonth() + "-" + bean.getYear();
				}
			}else if("assignmentLive".equalsIgnoreCase(liveType)){
				if("Y".equalsIgnoreCase(bean.getAssignmentLive()) && currentOrder > liveOrder){
					liveOrder = currentOrder;
					key = bean.getAcadMonth() + "-" + bean.getYear();
				}
			}else if("acadContentLiveNextBatch".equalsIgnoreCase(liveType)){
				if("Y".equalsIgnoreCase(bean.getAcadContentLive()) && currentOrder > liveOrder){
					liveOrder = currentOrder;
					key = bean.getAcadMonth() + "-" + bean.getYear();
				}
			}
		}

		if("acadContentLiveNextBatch".equalsIgnoreCase(liveType)){
			for (ExamOrderExamBean bean : liveFlagList) {
				double currentOrder = Double.parseDouble(bean.getOrder());
				if(currentOrder == (liveOrder + 1) ){
					key = bean.getAcadMonth() + "-" + bean.getYear();
				}
			}
		}

		return monthYearAndStudentRegistrationMap.get(key);
	}

	
	
	public static void main(String[] args) {
		String test = "This is line with &#39; html <p> tags and many <table> of them\n&nbsp; ";
		
		 test = StringEscapeUtils.unescapeHtml(test);
	}
	
	/*@RequestMapping(value = "/m/generatePGAssessment", method = RequestMethod.GET)   
	public ResponseEntity<HashMap<String,String>> generatePGAssessment(HttpServletRequest request){
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		HashMap<String, String> response = new HashMap<String, String>();
		JsonObject mettlResponse = mettlHelper.getPGAssessments();
		MettlTeeDAO mettlTeeDAO = (MettlTeeDAO) act.getBean("mettlTeeDAO");
		if(mettlResponse != null) {
			String status = mettlResponse.get("status").getAsString();
			String errorAssessmentIds = "";
			if("SUCCESS".equalsIgnoreCase(status)) {
				response.put("status", "success");
				JsonArray assessmentList = mettlResponse.get("assessments").getAsJsonArray();
				for (JsonElement assessmentElement : assessmentList) {
					JsonObject assessmentObject = assessmentElement.getAsJsonObject();
					String name = assessmentObject.get("name").getAsString();
					int assessmentId = assessmentObject.get("id").getAsInt();
					int duration = assessmentObject.get("duration").getAsInt();
					String[] name_tmp = name.split("_");
					if(name_tmp.length == 2) {
						//insert dao
						mettlTeeDAO.createPGAssessment(name_tmp[0], name_tmp[1], assessmentId,duration);
					}else {
						errorAssessmentIds = errorAssessmentIds + " {id: " + assessmentId + " , name: " + name + ", duration : " + duration + "},";
					}
				}
				response.put("errorJson", errorAssessmentIds);
			}else {
				response.put("status", "error");
				response.put("message","Not success");
			}
		}else {
			response.put("status", "error");
			response.put("message","Null Response");
		}
		return new ResponseEntity<HashMap<String,String>>(response,headers,HttpStatus.OK);
	}*/

}
