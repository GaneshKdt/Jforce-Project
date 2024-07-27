package com.nmims.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.ExamLiveSettingMBAWX;
import com.nmims.beans.Person;
import com.nmims.beans.ExamOrderExamBean;
import com.nmims.beans.ProgramSubjectMappingExamBean;
import com.nmims.daos.ExecutiveExamDao;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.helpers.ResultsToCacheHelper;
import com.nmims.services.PassFailService;
import com.nmims.services.MakeLiveService;
import com.nmims.services.RedisResultsStoreService;
import com.nmims.services.RedisSaveResultsService;

/**
 * Handles requests for the application home page.
 */
@Controller
public class MakeLiveController extends BaseController{

	@Autowired(required=false)
	ApplicationContext act;

	@Autowired
	ResultsToCacheHelper resultsToCacheHelper;
	
	@Autowired
	private PassFailService passFailService;
	
	@Value("#{'${ACAD_YEAR_LIST}'.split(',')}")
	private List<String> ACAD_YEAR_LIST;
	
	@Value("#{'${ACAD_MONTH_LIST}'.split(',')}") 
	private List<String> ACAD_MONTH_LIST; 
	

	@Value( "${SERVER_PATH}" )
	private String SERVER_PATH;
	
	private static final Logger logger = LoggerFactory.getLogger(MakeLiveController.class);


	@Value("#{'${CURRENT_YEAR_LIST}'.split(',')}")
	private ArrayList<String> currentYearList; 

	
	private ArrayList<ProgramSubjectMappingExamBean> programSubjectMappingList = null;

	@Autowired
	MakeLiveService makeLiveService;
	
	
	@RequestMapping(value = "/admin/makeResultsLiveForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView makeResultsLiveForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		ModelAndView modelnView = new ModelAndView("makeResultsLive");
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		List<ExamOrderExamBean> examsList = dao.getExamsList(); 
		modelnView.addObject("examsList", examsList);
		ExamOrderExamBean exam = new ExamOrderExamBean();
		modelnView.addObject("exam", exam);
		m.addAttribute("exam", exam);
		m.addAttribute("yearList", currentYearList);
		return modelnView;
	}
	
	@RequestMapping(value = "/admin/makeHallTicketLiveFormMBAWX", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView makeResultsLiveFormMBAWX(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		ModelAndView modelnView = new ModelAndView("makeHallTicketLiveMBAWX");
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		ExamLiveSettingMBAWX exam = new ExamLiveSettingMBAWX();
		ExecutiveExamDao edao = (ExecutiveExamDao)act.getBean("executiveExamDao");
		List<ExamLiveSettingMBAWX> hallTicketsList = edao.getHallTicketLives();
		modelnView.addObject("hallTicketsList", hallTicketsList); 
		modelnView.addObject("acadsYearList", ACAD_YEAR_LIST);
		modelnView.addObject("acadsMonthList", ACAD_MONTH_LIST);
		modelnView.addObject("exam", exam);
		m.addAttribute("exam", exam);
		m.addAttribute("yearList", currentYearList);
		return modelnView;  
	}
	
	@RequestMapping(value = "/admin/makeAcadDetailsLiveForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView makeAcadDetailsLiveForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		ModelAndView modelnView = new ModelAndView("makeResultsLiveAcad");
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		List<ExamOrderExamBean> examsList = dao.getExamsList(); 
		modelnView.addObject("examsList", examsList);
		ExamOrderExamBean exam = new ExamOrderExamBean();
		modelnView.addObject("exam", exam);
		m.addAttribute("exam", exam);
		m.addAttribute("yearList", currentYearList);
		return modelnView;
	}
	
	@RequestMapping(value = "/admin/makeResultsLive", method = {RequestMethod.POST})
	public ModelAndView makeResultsLive(HttpServletRequest request, HttpServletResponse respnse, @ModelAttribute ExamOrderExamBean exam) {
		
		ModelAndView modelnView = new ModelAndView("makeResultsLive");
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		try{
			makeLiveService.makeResultsLive(exam);
			//dao.updateResultLiveIndividualStudent(exam);
			request.setAttribute("success","true");
			request.setAttribute("successMessage","Online Exam "+exam.getMonth()+" "+exam.getYear()+" Result made Live successfully");
			
//			modelnView.addObject("resultsMadeLive","Y");
			
		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in making results live.");
		}
		
		//Added api call to update data in redis
//			resultsToCacheHelper.restApiCallForSetResultsInCache(exam,SERVER_PATH);
		//Added api call to update data in redis end
		

//		Shifted to its own event listener class : RedisResultsStoreEventListener package : listeners in March 2023
		
//		RedisResultsStoreService redisResultsService = (RedisResultsStoreService)act.getBean(RedisResultsStoreService.class);
//		try {
//			redisResultsService.fetchAndStoreResultsInRedis(exam.getYear(), exam.getMonth());
//		} catch (Exception e) {
//			modelnView.addObject("errorMessage", "Error saving results to redis : " + e.getMessage());
//		}
		
		List<ExamOrderExamBean> examsList = dao.getExamsList(); 
		modelnView.addObject("examsList", examsList);
		modelnView.addObject("exam", exam);
		modelnView.addObject("yearList", currentYearList);
		return modelnView;
	}


	@RequestMapping(value = "/admin/makeHallTicketLiveMBAWX", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView makeHallTicketLiveMBAWX(HttpServletRequest request, HttpServletResponse respnse, @ModelAttribute ExamLiveSettingMBAWX exam) {
		
		ModelAndView modelnView = new ModelAndView("makeHallTicketLiveMBAWX");
		ExecutiveExamDao dao = (ExecutiveExamDao)act.getBean("executiveExamDao");
		try{
			String message;
			boolean success;
			if(exam.getLive().equalsIgnoreCase("Y")) {
				success = dao.updateHallTicketStats(exam); 
				message =(success)?" Hallticket made Live successfully":" Invalid year and month!";
			}else {
				success = dao.deleteFromHallTicketLive(exam);
				message =(success)?" Hallticket Live removed successfully":" Invalid year and month!";
			}
			if(success) { 
				request.setAttribute("success","true");
				request.setAttribute("successMessage","Online Exam "+exam.getExamMonth()+" "+exam.getExamYear()+message);
			}else {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", message);
			}
		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in making results live.");
		}
		modelnView.addObject("acadsYearList", ACAD_YEAR_LIST);
		modelnView.addObject("acadsMonthList", ACAD_MONTH_LIST);
		
		List<ExamLiveSettingMBAWX> hallTicketsList = dao.getHallTicketLives(); 
		modelnView.addObject("hallTicketsList", hallTicketsList);
		modelnView.addObject("exam", exam);
		modelnView.addObject("yearList", currentYearList);
		return modelnView; 
	}
	@RequestMapping(value="/admin/makeWrittenRevalLive",method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView makeWrittenRevalLive(HttpServletRequest request, HttpServletResponse respnse, @ModelAttribute ExamOrderExamBean exam){
		ModelAndView modelnView = new ModelAndView("makeResultsLive");
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		try{
			dao.updateWrittenRevalStats(exam);
			dao.updateRevaulationResultDeclaredFlagOnMarks(exam);
			request.setAttribute("success","true");
			request.setAttribute("successMessage","Updated Successfully");
		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in making results live.");
		}
		List<ExamOrderExamBean> examsList = dao.getExamsList(); 
		modelnView.addObject("examsList", examsList);
		modelnView.addObject("exam", exam);
		modelnView.addObject("yearList", currentYearList);
		return modelnView;
	}
	
	@RequestMapping(value="/admin/makeAssignmentRevalLive",method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView makeAssignmentRevalLive(HttpServletRequest request, HttpServletResponse respnse, @ModelAttribute ExamOrderExamBean exam){
		logger.info("Make results live Page");
		ModelAndView modelnView = new ModelAndView("makeResultsLive");
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		try{
			dao.updateAssignmentRevalStats(exam);
			dao.updateRevaulationResultDeclaredFlagOnAssignmentSubmission(exam);
			dao.updateRevaulationResultDeclaredFlagOnAssignmentSubmissionQuickTable(exam);
			request.setAttribute("success","true");
			request.setAttribute("successMessage","Updated Successfully");
		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in making results live.");
		}
		List<ExamOrderExamBean> examsList = dao.getExamsList(); 
		modelnView.addObject("examsList", examsList);
		modelnView.addObject("exam", exam);
		modelnView.addObject("yearList", currentYearList);
		return modelnView;
	}
	
	@RequestMapping(value = "/admin/makeOfflineResultsLive", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView makeOfflineResultsLive(HttpServletRequest request, HttpServletResponse respnse, @ModelAttribute ExamOrderExamBean exam) {
		logger.info("Make results live Page");
		ModelAndView modelnView = new ModelAndView("makeResultsLive");
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		try{
			dao.updateOfflineExamResultStatus(exam);
			//dao.updateResultLiveIndividualStudentStats(exam);
			request.setAttribute("success","true");
			request.setAttribute("successMessage","Offline Exam "+exam.getMonth()+" "+exam.getYear()+" Result made Live successfully");
		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in making results live.");
		}
		
		
		List<ExamOrderExamBean> examsList = dao.getExamsList(); 
		modelnView.addObject("examsList", examsList);
		modelnView.addObject("exam", exam);
		modelnView.addObject("yearList", currentYearList);
		return modelnView;
	}
	
	
	@RequestMapping(value = "/admin/makeTimetableLive", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView makeTimetableLive(HttpServletRequest request, HttpServletResponse respnse, @ModelAttribute ExamOrderExamBean exam) {
		logger.info("Make results live Page");
		ModelAndView modelnView = new ModelAndView("makeResultsLive");
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		try{
			dao.updateTimetableStats(exam);
			request.setAttribute("success","true");
			request.setAttribute("successMessage","Exam "+exam.getMonth()+" "+exam.getYear()+" time table made live successfully");
		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in making Time table live.");
		}
		
		
		List<ExamOrderExamBean> examsList = dao.getExamsList(); 
		modelnView.addObject("examsList", examsList);
		modelnView.addObject("exam", exam);
		modelnView.addObject("yearList", currentYearList);
		return modelnView;
	}
	
	@RequestMapping(value = "/admin/makeAssignmentSubmissionLive", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView makeAssignmentSubmissionLive(HttpServletRequest request, HttpServletResponse respnse, @ModelAttribute ExamOrderExamBean exam) {
		ModelAndView modelnView = new ModelAndView("makeResultsLive");
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		logger.info("Regular Assignment Submission Live -{} Month-{} Year-{}",exam.getAssignmentLive(),exam.getMonth(),exam.getYear());
		try{
			makeLiveService.makeAssignmentSubmissionLiveStatus(exam);
			request.setAttribute("success","true");
			request.setAttribute("successMessage","Exam "+exam.getMonth()+" "+exam.getYear()+" Assignment Submission made live successfully");
		}catch(Exception e){
			logger.error("Error Regular Assignment Submission Live -{} Month-{} Year-{} Error -",exam.getAssignmentLive(),exam.getMonth(),exam.getYear(),e);
			//e.printStackTrace();
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in making Assignment Submission live.");
		}
		
		
		List<ExamOrderExamBean> examsList = dao.getExamsList(); 
		modelnView.addObject("examsList", examsList);
		modelnView.addObject("exam", exam);
		modelnView.addObject("yearList", currentYearList);
		return modelnView;
	}
	
	@RequestMapping(value = "/admin/makeResitAssignmentSubmissionLive", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView makeResitAssignmentSubmissionLive(HttpServletRequest request, HttpServletResponse respnse, @ModelAttribute ExamOrderExamBean exam) {
		logger.info("Make results live Page");
		ModelAndView modelnView = new ModelAndView("makeResultsLive");
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		logger.info("Resit Assignment Submission Live -{} Month-{} Year-{}",exam.getResitAssignmentLive(),exam.getMonth(),exam.getYear());
		try{
			makeLiveService.makeResitAssignmentSubmissionLiveStatus(exam);
			request.setAttribute("success","true");
			request.setAttribute("successMessage","Exam "+exam.getMonth()+" "+exam.getYear()+" Fail Subject Assignment Submission status changed");
		}catch(Exception e){
			logger.error("Error Resit Assignment Submission Live -{} Month-{} Year-{} Error -",exam.getResitAssignmentLive(),exam.getMonth(),exam.getYear(),e);
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in making Assignment Submission live.");
		}
		
		
		List<ExamOrderExamBean> examsList = dao.getExamsList(); 
		modelnView.addObject("examsList", examsList);
		modelnView.addObject("exam", exam);
		modelnView.addObject("yearList", currentYearList);
		return modelnView;
	}
	
	
	@RequestMapping(value = "/admin/makeAssignmentMarksLive", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView makeAssignmentMarksLive(HttpServletRequest request, HttpServletResponse respnse, @ModelAttribute ExamOrderExamBean exam) {
		logger.info("Make results live Page");
		ModelAndView modelnView = new ModelAndView("makeResultsLive");
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		try{
			dao.updateAssignmentLiveStatus(exam);
			request.setAttribute("success","true");
			request.setAttribute("successMessage","Exam "+exam.getMonth()+" "+exam.getYear()+" Assignment Marks made live successfully");
		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in making Assignment Marks live.");
		}
		
		
		List<ExamOrderExamBean> examsList = dao.getExamsList(); 
		modelnView.addObject("examsList", examsList);
		modelnView.addObject("exam", exam);
		modelnView.addObject("yearList", currentYearList);
		return modelnView;
	}
	
	@RequestMapping(value = "/admin/makeAcademicSessionLive", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView makeAcademicSessionLive(HttpServletRequest request, HttpServletResponse respnse, @ModelAttribute ExamOrderExamBean exam) {
		logger.info("Make results live Page");
		ModelAndView modelnView = new ModelAndView("makeResultsLiveAcad");
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		try{
			dao.updateAcadSessionStats(exam);
			request.setAttribute("success","true");
			request.setAttribute("successMessage","Session "+exam.getAcadMonth()+" "+exam.getYear()+" Calendar made live successfully");
		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in making Calendar live.");
		}
		
		
		List<ExamOrderExamBean> examsList = dao.getExamsList(); 
		modelnView.addObject("examsList", examsList);
		modelnView.addObject("exam", exam);
		modelnView.addObject("yearList", currentYearList);
		return modelnView;
	}
	
	@RequestMapping(value = "/admin/makeForumLive", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView makeForumLive(HttpServletRequest request, HttpServletResponse respnse, @ModelAttribute ExamOrderExamBean exam) {
		ModelAndView modelnView = new ModelAndView("makeResultsLiveAcad");
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		try{
			dao.updateForumStats(exam);
			Map<String,String> requestmap = new HashMap<>();
			requestmap.put("month", exam.getAcadMonth());
			requestmap.put("year", exam.getYear());
			Person user = (Person)request.getSession().getAttribute("user");
			requestmap.put("roles", user.getRoles());
			request.setAttribute("success","true");
			request.setAttribute("successMessage","Session "+exam.getAcadMonth()+" "+exam.getYear()+" Forum made live successfully");
			HttpHeaders headers =  this.getHeaders();	
			HttpEntity<Map<String,String>> entity = new HttpEntity<Map<String,String>>(requestmap,headers);
			RestTemplate restTemplate = new RestTemplate();
			String url = SERVER_PATH+"forum/m/autoCreateThread";
			ResponseEntity<String> response = restTemplate.exchange(url,HttpMethod.POST, entity, String.class);
		}catch(Exception e){
			logger.error("Error in making forum live : ",e);
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in making Forum live.");
		}
		
		
		List<ExamOrderExamBean> examsList = dao.getExamsList(); 
		modelnView.addObject("examsList", examsList);
		modelnView.addObject("exam", exam);
		modelnView.addObject("yearList", currentYearList);
		return modelnView;
	}
	
	@RequestMapping(value = "/admin/makeAcademicContentLive", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView makeAcademicContentLive(HttpServletRequest request, HttpServletResponse respnse, @ModelAttribute ExamOrderExamBean exam) {
		logger.info("Make results live Page");
		ModelAndView modelnView = new ModelAndView("makeResultsLiveAcad");
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		try{
			dao.updateAcadContentStatus(exam);
			request.setAttribute("success","true");
			request.setAttribute("successMessage",exam.getAcadMonth()+" "+exam.getYear()+" Content made live successfully");
		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in making Content live.");
		}
		
		
		List<ExamOrderExamBean> examsList = dao.getExamsList(); 
		modelnView.addObject("examsList", examsList);
		modelnView.addObject("exam", exam);
		modelnView.addObject("yearList", currentYearList);
		return modelnView;
	}
	
	@RequestMapping(value = "/admin/makeProjectSubmissionLiveForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView makeProjectSubmissionLiveForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		logger.info("Make results live Page");
		ModelAndView modelnView = new ModelAndView("makeResultsLive");
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		List<ExamOrderExamBean> examsList = dao.getExamsList(); 
		modelnView.addObject("examsList", examsList);
		ExamOrderExamBean exam = new ExamOrderExamBean();
		modelnView.addObject("exam", exam);
		m.addAttribute("exam", exam);
		m.addAttribute("yearList", currentYearList);
		return modelnView;
	}
	
	@RequestMapping(value = "/admin/makeProjectSubmissionLive", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView makeProjectSubmissionLive(HttpServletRequest request, HttpServletResponse respnse, @ModelAttribute ExamOrderExamBean exam) {
		logger.info("Make results live Page");
		ModelAndView modelnView = new ModelAndView("makeResultsLive");
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		logger.info("Project Submission Live -{} Month-{} Year-{}",exam.getProjectSubmissionLive(),exam.getMonth(),exam.getYear());
		try{
			makeLiveService.makeProjectSubmissionLiveStatus(exam);
			request.setAttribute("success","true");
			request.setAttribute("successMessage","Exam "+exam.getMonth()+" "+exam.getYear()+" Project Submission live status updated successfully");
		}catch(Exception e){
			logger.error("Error Project Submission Live -{} Month-{} Year-{} Error -",exam.getProjectSubmissionLive(),exam.getMonth(),exam.getYear(),e);
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in changingProject Submission Live Status .");
		}
		
		List<ExamOrderExamBean> examsList = dao.getExamsList(); 
		modelnView.addObject("examsList", examsList);
		modelnView.addObject("exam", exam);
		modelnView.addObject("yearList", currentYearList);
		return modelnView;
	}
	private HttpHeaders getHeaders() {
		HttpHeaders headers =  new HttpHeaders();
		headers.add("Accept", "application/json");
		headers.add("Content-Type", "application/json");
		return headers;
	}
	
}

