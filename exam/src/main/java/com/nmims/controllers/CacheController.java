package com.nmims.controllers;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.CacheRefreshExamBean;
import com.nmims.daos.AssignmentsDAO;
import com.nmims.daos.DashboardDAO;
import com.nmims.daos.ExamBookingDAO;
import com.nmims.daos.ExamCenterDAO;
import com.nmims.daos.FacultyDAO;
import com.nmims.daos.PassFailDAO;
import com.nmims.daos.ProjectSubmissionDAO;
import com.nmims.daos.ReportsDAO;
import com.nmims.daos.ResitExamBookingDAO;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.events.MakeExamLive;
import com.nmims.helpers.TeeSSOHelper;
import com.nmims.services.ICacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@EnableAsync
public class CacheController extends BaseController {


	@Value("${SERVER_PORT}")
	private int[] SERVER_PORT;

	@Autowired
	ApplicationContext act;
	
	@Autowired
	AssignmentsDAO assignmentsDAO;
	
	@Autowired
	DashboardDAO dashboardDAO;
	
	@Autowired
	ExamBookingDAO examBookingDAO;
	
	@Autowired
	ExamCenterDAO examCenterDAO;
	
	@Autowired
	FacultyDAO facultyDAO;
	
	@Autowired
	PassFailDAO passFailDAO;
	
	@Autowired
	ProjectSubmissionDAO projectSubmissionDAO;
	
	@Autowired
	ReportsDAO reportsDAO;
	
	@Autowired
	ResitExamBookingDAO resitExamBookingDAO;
	
	@Autowired
	StudentMarksDAO studentMarksDAO;
	
	@Autowired
	TeeSSOHelper teeSSOHelper;
	
	@Autowired
	private ICacheService cacheService;
	private static final Logger logger = LoggerFactory.getLogger(CacheController.class);
	
	//Make an entry in this method for every DAO that is made
	@RequestMapping(value = "/admin/refreshCache", method = {RequestMethod.GET})
	public ModelAndView refreshCache(HttpServletRequest request, HttpServletResponse respnse) {
		assignmentsDAO.refreshLiveFlagSettings();
		dashboardDAO.refreshLiveFlagSettings();
		examBookingDAO.refreshLiveFlagSettings();
		examCenterDAO.refreshLiveFlagSettings();
		facultyDAO.refreshLiveFlagSettings();
		passFailDAO.refreshLiveFlagSettings();
		projectSubmissionDAO.refreshLiveFlagSettings();
		reportsDAO.refreshLiveFlagSettings();
		resitExamBookingDAO.refreshLiveFlagSettings();
		studentMarksDAO.refreshLiveFlagSettings();
		
		return null;
	}
	
	@Async("makeLiveAsyncExecutor")
	@EventListener
	public void handleMakeLive(final MakeExamLive makeExamLive) {
		logger.info("Cache refresh started by Event - {}",makeExamLive.toString());
		
			String assignmentsDAOStatus = assignmentsDAO.refreshLiveFlagSettings();
			logger.info("Cache refresh assignmentsDAO status - {}",assignmentsDAOStatus);
			
			String dashboardDAOStatus = dashboardDAO.refreshLiveFlagSettings();
			logger.info("Cache refresh dashboardDAO status - {}",dashboardDAOStatus);
			
			String examBookingDAOStatus = examBookingDAO.refreshLiveFlagSettings();
			logger.info("Cache refresh examBookingDAO status - {}",examBookingDAOStatus);
			
			String examCenterDAOStatus = examCenterDAO.refreshLiveFlagSettings();
			logger.info("Cache refresh examCenterDAO status - {}",examCenterDAOStatus);
			
			String facultyDAOStatus = facultyDAO.refreshLiveFlagSettings();
			logger.info("Cache refresh facultyDAO status - {}",facultyDAOStatus);
			
			String passFailDAOStatus = passFailDAO.refreshLiveFlagSettings();
			logger.info("Cache refresh passFailDAO status - {}",passFailDAOStatus);
			
			String projectSubmissionDAOStatus = projectSubmissionDAO.refreshLiveFlagSettings();
			logger.info("Cache refresh projectSubmissionDAO status - {}",projectSubmissionDAOStatus);
			
			String reportsDAOStatus = reportsDAO.refreshLiveFlagSettings();
			logger.info("Cache refresh reportsDAO status - {}",reportsDAOStatus);
			
			String resitExamBookingDAOStatus = resitExamBookingDAO.refreshLiveFlagSettings();
			logger.info("Cache refresh resitExamBookingDAO status - {}",resitExamBookingDAOStatus);
			
			String studentMarksDAOStatus = studentMarksDAO.refreshLiveFlagSettings();
			logger.info("Cache refresh studentMarksDAO status - {}",studentMarksDAOStatus);
			
		logger.info("Cache refresh ended by Event - {}",makeExamLive.toString());
	}
	
	@RequestMapping(value = "/admin/refreshExamBookingCache", method = {RequestMethod.GET})
	public ModelAndView refreshExamBookingCache(HttpServletRequest request, HttpServletResponse respnse){
		
		ExamBookingController examBookingController = act.getBean(ExamBookingController.class);
		examBookingController.refreshCache();
		
		try {
			cacheService.refreshCache();
		} catch (Exception e) {
			
		}
		
		return new ModelAndView("RequestTesting");
	}
	
	@RequestMapping(value = "/admin/refreshAllServerExamBookingCache", method = {RequestMethod.GET})
	public ModelAndView refreshAllServerExamBookingCache(HttpServletRequest request, HttpServletResponse respnse) throws IOException {
		ModelAndView mv = new ModelAndView("RequestTesting");
		String error = this.TryRefreshCacheToAllServer(0,"ExamBooking");
		mv.addObject("error",error);
		return mv;
	}
	
	@RequestMapping(value = "/admin/cacheRefreshToOnlyServer", method = {RequestMethod.GET})
	public ModelAndView CacheRefreshToOnlyServer() {
		AssignmentController assignmentController = act.getBean(AssignmentController.class);
		assignmentController.RefreshCache();
		AssignmentPaymentController assignmentPaymentController = act.getBean(AssignmentPaymentController.class);
		assignmentPaymentController.RefreshCache();
		AssignmentSubmissionController assignmentSubmissionController = act.getBean(AssignmentSubmissionController.class);
		assignmentSubmissionController.RefreshCache();
		/*BaseController baseController = new BaseController();
		baseController.RefreshCache();*/
		ConfigurationController configurationController = act.getBean(ConfigurationController.class);
		configurationController.RefreshCache();
		DashboardController dashboardController = act.getBean(DashboardController.class);
		dashboardController.RefreshCache();
		EnrollmentController enrollmentController = act.getBean(EnrollmentController.class);
		enrollmentController.RefreshCache();
		ExamBookingController examBookingController = act.getBean(ExamBookingController.class);
		examBookingController.refreshCache();
		
		try {
			cacheService.refreshCache();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/*ExecutiveBaseController executiveBaseController = act.getBean(ExecutiveBaseController.class);
		executiveBaseController.RefreshCache();*/
		ExecutiveConfigurationController executiveConfigurationController = act.getBean(ExecutiveConfigurationController.class);
		executiveConfigurationController.RefreshCache();
		ExecutiveExamBookingController executiveExamBookingController = act.getBean(ExecutiveExamBookingController.class);
		executiveExamBookingController.RefreshCache();
		ExecutiveHallTicketController executiveHallTicketController = act.getBean(ExecutiveHallTicketController.class);
		executiveHallTicketController.RefreshCache();
		HallTicketController hallTicketController = act.getBean(HallTicketController.class);
		hallTicketController.RefreshCache();
		MarksheetController marksheetController = act.getBean(MarksheetController.class);
		marksheetController.RefreshCache();
		OnlineExamMarksController onlineExamMarksController = act.getBean(OnlineExamMarksController.class);
		onlineExamMarksController.RefreshCache();
		PassFailController passFailController = act.getBean(PassFailController.class);
		passFailController.RefreshCache();
		ProjectSubmissionController projectSubmissionController = act.getBean(ProjectSubmissionController.class);
		projectSubmissionController.RefreshCache();
		ReportsController reportsController = act.getBean(ReportsController.class);
		reportsController.RefreshCache();
		SifyController sifyController = act.getBean(SifyController.class);
		sifyController.RefreshCache();
		StudentMarksController studentMarksController = act.getBean(StudentMarksController.class);
		studentMarksController.RefreshCache();
		StudentTestController studentTestController = act.getBean(StudentTestController.class);
		studentTestController.RefreshCache();
		TEEController tEEController = act.getBean(TEEController.class);
		tEEController.RefreshCache();
		TestController testController = act.getBean(TestController.class);
		testController.RefreshCache();
		TimeTableController timeTableController = act.getBean(TimeTableController.class);
		timeTableController.RefreshCache();
		TranscriptController transcriptController = act.getBean(TranscriptController.class);
		transcriptController.RefreshCache();
		return new ModelAndView("RequestTesting");
	}
	
	@RequestMapping(value = "/admin/cacheRefreshToAllServer", method = {RequestMethod.GET})
	public @ResponseBody CacheRefreshExamBean CacheRefreshToAllServer() throws IOException {
		
		ModelAndView mv = new ModelAndView("RequestTesting");
		String error = this.TryRefreshCacheToAllServer(0,"All");
		CacheRefreshExamBean response = new CacheRefreshExamBean();
		response.setMessage("failed : " + error);
		if(error.isEmpty()) {
			response.setStatus("success");
		}else {
			response.setStatus("error");
		}
		return response;
	}
	
}
