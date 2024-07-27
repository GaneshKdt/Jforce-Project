package com.nmims.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.beans.ExamCenterBean;
import com.nmims.beans.ExecutiveBean;
import com.nmims.beans.ExecutiveExamCenter;
import com.nmims.beans.ExecutiveExamOrderBean;
import com.nmims.beans.ProgramSubjectMappingExamBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.UserAuthorizationExamBean;
import com.nmims.daos.ExamCenterDAO;
import com.nmims.daos.ExecutiveConfigurationDao;
import com.nmims.daos.ExecutiveExamBookingDao;
import com.nmims.daos.ExecutiveExamDao;
import com.nmims.helpers.ExecutiveExamBookingPdfCreator;
import com.nmims.helpers.MailSender;
import com.nmims.views.ExecutiveBookingExcelView;


@Controller
public class ExecutiveExamBookingController extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(ExecutiveExamBookingController.class);
	private ArrayList<String> eligibleSubjectsList = null;
	private HashMap<String,ArrayList<String>> programSubjectMapping = new HashMap<String,ArrayList<String>>();
	private HashMap<String, String> examCenterIdNameMap = null;
	private boolean refreshCache = false;

	@Autowired
	private ExecutiveExamBookingDao executiveExamBookingDao;

	@Value("${FEE_RECEIPT_PATH}")
	private String FEE_RECEIPT_PATH;
	
	@Value("#{'${SAS_EXAM_MONTH_LIST}'.split(',')}") 
	private List<String> SAS_EXAM_MONTH_LIST; 
	
	@Value("#{'${ACAD_YEAR_SAS_LIST}'.split(',')}") 
	private List<String> ACAD_YEAR_SAS_LIST; 
	
	/**
	 * Refresh Cache function to refresh cache
	 * @param 
	 * none
	 * @return 
	 * none
	 * */
	
	@Value("${ICLCRESTRICTED_USER_LIST}")
	private List<String> ICLCRESTRICTED_USER_LIST;
	
	public String RefreshCache() {
		examCenterIdNameMap = null;
		getExamCenterIdNameMap();
		
		
		return null;
	}
	
	public void getProgramSubjectMapping(){
		if(this.programSubjectMapping.isEmpty() || this.programSubjectMapping == null){
			ExecutiveExamBookingDao dao = (ExecutiveExamBookingDao)act.getBean("executiveExamBookingDao");
			this.programSubjectMapping = dao.getProgramSubjectMapping();
		}
	}

	public HashMap<String, String> getExamCenterIdNameMap(){
		if(this.examCenterIdNameMap == null || this.examCenterIdNameMap.size() == 0){
			this.examCenterIdNameMap = executiveExamBookingDao.getExamCenterIdNameMap();
		}
		return examCenterIdNameMap;
	}

	@Autowired
	ApplicationContext act;


	@RequestMapping(value = "/executiveRegistrationForm", method = { RequestMethod.GET , RequestMethod.POST})
	private ModelAndView exceutiveRegistrationForm(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute ExecutiveBean executiveBean) {

		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		String errorMessage ="";
		
		request.getSession().setAttribute("onlineSeatBookingComplete", null);
		request.getSession().setAttribute("subjects", null);
		
		ModelAndView mav = new ModelAndView("executiveRegistrationForm");
		mav.addObject("executiveBean", executiveBean);
		StudentExamBean student=(StudentExamBean)request.getSession().getAttribute("studentExam");
		HashMap<String,ExecutiveBean> mapOfBookedSubjects = new HashMap<String,ExecutiveBean>();
		request.setAttribute("mapOfBookedSubjects", mapOfBookedSubjects);

		try {

			//Check Exam Cycle Live Currently for student's Admission Batch. Get Details
			ExecutiveExamOrderBean examLiveCurrently = executiveExamBookingDao.getCurrentLiveExamSetup(student);
			request.getSession().setAttribute("examLiveCurrently", examLiveCurrently);
			//If No exam is live for logged in students batch, then show error message
			if(examLiveCurrently == null){
				setError(request, "Exam Registration is Not Live.");
				request.getSession().setAttribute("isExamRegistrationLive", false);
				return mav;
			}

			//Fetch subjects already booked
			mapOfBookedSubjects = executiveExamBookingDao.getBookedSubjects(student, examLiveCurrently);
			request.setAttribute("mapOfBookedSubjects", mapOfBookedSubjects);

			//If Exam Registration is live for students batch, check his pending subjects (Not passed, as well as available for exam booking)
			ArrayList<String> subjectsEligibleForExam = executiveExamBookingDao.getSubjectsEligibleForExam(student, examLiveCurrently);

			//Pending Logic to Add
			//1. Add Subjects passed, but can be given improvement (program_subject)
			//2. Remove subjects  where attempts are exhausted
			
			//1. Add Subjects passed, but can be given improvement (program_subject) start
			if("MPDV".equalsIgnoreCase(student.getProgram())) {
				if(!subjectsEligibleForExam.contains("Visual Analytics")) {
					if(executiveExamBookingDao.isSubjectLiveForRegistration("Visual Analytics",student, examLiveCurrently)) {	
						subjectsEligibleForExam.add("Visual Analytics");
					}
				}
			}
			
			if("EPBM".equalsIgnoreCase(student.getProgram())) {
				if(!subjectsEligibleForExam.contains("Enterprise Miner")) {
					if(executiveExamBookingDao.isSubjectLiveForRegistration("Enterprise Miner",student, examLiveCurrently)) {	
						subjectsEligibleForExam.add("Enterprise Miner");
					}
				}
			}
			
			//1. Add Subjects passed, but can be given improvement (program_subject) end
			
			
			//2. Remove subjects  where attempts are exhausted start
			if(subjectsEligibleForExam.contains("Business Statistics- EP") || subjectsEligibleForExam.contains("Business Statistics- MP")) {
				//check if attempts are exhauste
				String subject = "";
				if("MPDV".equalsIgnoreCase(student.getProgram())) {
					subject = "Business Statistics- MP";
					
				}else if("EPBM".equalsIgnoreCase(student.getProgram())) {
					subject = "Business Statistics- EP";
				}
				int attempts = executiveExamBookingDao.noOfAttemptsBySapidNSubject(student.getSapid(),subject);
				if(attempts >= 3) {
					subjectsEligibleForExam.remove(subject);
					errorMessage += "You have exhausted all 3 exam attempts for "+subject+". You cannot register for the said subject in the upcoming examination. ";
					setError(request, errorMessage);
					
				}else {
					int bestMarks = executiveExamBookingDao.getMaxMarksBySapidNSubject(student.getSapid(),subject);
					if(bestMarks >= 50) {
						subjectsEligibleForExam.remove(subject);
					}
				}
				
			}
			//2. Remove subjects  where attempts are exhausted end
 			
			
			
			//If No pending subjects or No Timetable for those subjects, then show error
			if(subjectsEligibleForExam == null || subjectsEligibleForExam.size() == 0){
				errorMessage += "No subjects pending Exam Registration";
				//setError(request, errorMessage);
				request.getSession().setAttribute("isExamRegistrationLive", false);
				return mav;
			}else{
				request.setAttribute("currentSemApplicableLiveSubject", subjectsEligibleForExam);
				request.getSession().setAttribute("isExamRegistrationLive", true);
			}

			//If Timetable is set up for pending subjects then allow students to see those subjects and ask to select
		} catch (Exception e) {
			
			setError(request, e.getMessage());
		}

		return mav;
	}


	@RequestMapping(value = "/executiveChangeExamBookingForm",  method = { RequestMethod.GET , RequestMethod.POST})
	public String executiveChangeExamBookingForm(HttpServletRequest request, HttpServletResponse response
			, Model mav) {


		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}

		request.getSession().setAttribute("onlineSeatBookingComplete", null);
		request.getSession().setAttribute("subjects", null);

		ExecutiveBean executiveBean = new ExecutiveBean();
		executiveBean.setChangeOfCenter("true");
		mav.addAttribute("executiveBean", executiveBean);
		StudentExamBean student=(StudentExamBean)request.getSession().getAttribute("studentExam");
		HashMap<String,ExecutiveBean> mapOfBookedSubjects = new HashMap<String,ExecutiveBean>();
		request.setAttribute("mapOfBookedSubjects", mapOfBookedSubjects);

		try {
			//Check Exam Cycle Live Currently for student's Admission Batch. Get Details
			ExecutiveExamOrderBean examLiveCurrently = executiveExamBookingDao.getCurrentLiveExamSetup(student);
			request.getSession().setAttribute("examLiveCurrently", examLiveCurrently);
			//If No exam is live for logged in students batch, then show error message
			if(examLiveCurrently == null){
				setError(request, "Exam Registration is Not Live.");
				request.getSession().setAttribute("isExamRegistrationLive", false);
				return "changeExecutiveBookingForm";
			}

			//Fetch subjects already booked
			mapOfBookedSubjects = executiveExamBookingDao.getBookedSubjects(student, examLiveCurrently);
			request.setAttribute("mapOfBookedSubjects", mapOfBookedSubjects);

			//If Exam Registration is live for students batch, check his pending subjects (Not passed, as well as available for exam booking)
			ArrayList<String> subjectsEligibleForExam = executiveExamBookingDao.getSubjectsEligibleForExam(student, examLiveCurrently);

			//If No pending subjects or No Timetable for those subjects, then show error
			if(subjectsEligibleForExam == null || subjectsEligibleForExam.size() == 0){
				setError(request, "No subjects pending Exam Registration");
				request.getSession().setAttribute("isExamRegistrationLive", false);
				return "changeExecutiveBookingForm";
			}else{
				request.setAttribute("currentSemApplicableLiveSubject", subjectsEligibleForExam);
				request.getSession().setAttribute("isExamRegistrationLive", true);
			}

			//If Timetable is set up for pending subjects then allow students to see those subjects and ask to select
		} catch (Exception e) {
			
			setError(request, e.getMessage());
		}



		return "changeExecutiveBookingForm";
	}


	@RequestMapping(value="/executiveSelectCenterForm", method= {RequestMethod.GET,RequestMethod.POST})
	public ModelAndView executiveSelectCenterForm(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute ExecutiveBean executiveBean ) {

		ModelAndView mav=new ModelAndView("executiveSelectCenterForm");
		mav.addObject("executiveBean", executiveBean);

		StudentExamBean student=(StudentExamBean)request.getSession().getAttribute("studentExam");
		ExecutiveExamOrderBean examLiveCurrently = (ExecutiveExamOrderBean)request.getSession().getAttribute("examLiveCurrently");
		ArrayList<String> subjects = executiveBean.getApplicableSubjects();

		request.getSession().setAttribute("subjects", subjects);

		getAvailableCenters(request, student, subjects, examLiveCurrently);

		return mav;
	}




	private void getAvailableCenters(HttpServletRequest request,  StudentExamBean student, ArrayList<String> subjects, ExecutiveExamOrderBean examLiveCurrently) {

		Map<String, List<ExamCenterBean>> subjectAvailableCentersMap = new HashMap<String, List<ExamCenterBean>>();


		subjectAvailableCentersMap = executiveExamBookingDao.getAvailableCenters(examLiveCurrently, student, subjects);

		request.getSession().setAttribute("subjectAvailableCentersMap", subjectAvailableCentersMap);

	}



	@RequestMapping(value="/getAvailableCentersForExecutiveExam", method = {RequestMethod.GET, RequestMethod.POST})
	public @ResponseBody String getAvailableCentersForCity(HttpServletRequest request, HttpServletResponse response ) throws ParseException {
		String output = null;
		try {


			String subjectCenter = request.getParameter("depdrop_parents[0]"); //This is the name used by plugin
			String[] tempArray = subjectCenter.split("\\|");
			String subject = tempArray[0];
			String centerId = tempArray[1];

			StudentExamBean student = (StudentExamBean)request.getSession().getAttribute("studentExam");
			ExecutiveExamOrderBean examLiveCurrently = (ExecutiveExamOrderBean)request.getSession().getAttribute("examLiveCurrently");

			List<ExamCenterBean> availableCenters = executiveExamBookingDao.getAvailableCentersForExecutiveExam(examLiveCurrently, student);

			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE, dd-MMM-yyyy");


			output =  "{\"output\":"
					+ "[";
			for (ExamCenterBean examCenterBean : availableCenters) {

				if(examCenterBean.getCenterId().equals(centerId)){

					String city = examCenterBean.getCity();
					String date = examCenterBean.getDate();
					String startTime = examCenterBean.getStarttime();
					int available = examCenterBean.getAvailable();
					String capacity = examCenterBean.getCapacity();
					Date formattedDate = formatter.parse(date);
					String formattedDateString = dateFormatter.format(formattedDate);

					String dropDownValue = startTime  + "|" + date + "|" + city;

					String dropDownLabel = formattedDateString  + ", " + startTime + " (" + available + "/" + capacity + ")";

					output += "{\"id\":\"" + dropDownValue + "\", \"name\":\"" + dropDownLabel + "\"},";

				}
			}

			if(output.endsWith(",")){
				output = output.substring(0, output.length() - 1);
			}

			output +=  "]"+
					//    ", \"selected\":\"sub-cat-id-1\""
					"} ";


		} catch (Exception e) {
			
		}

		return output;
	}


	@RequestMapping(value="/saveSeatsForExecutiveExam", method= {RequestMethod.GET,RequestMethod.POST})
	public String saveSeatsForExecutiveExam(HttpServletRequest request, HttpServletResponse response, @ModelAttribute ExecutiveBean executiveBean, Model modelnView) {
		StudentExamBean student=(StudentExamBean)request.getSession().getAttribute("studentExam");
		ExecutiveExamOrderBean examLiveCurrently = (ExecutiveExamOrderBean)request.getSession().getAttribute("examLiveCurrently");
		ArrayList<String> subjects = (ArrayList<String>)request.getSession().getAttribute("subjects");
		ExamCenterDAO edao = (ExamCenterDAO)act.getBean("examCenterDAO");
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}


		request.setAttribute("executiveBean", executiveBean);
		
		String onlineSeatBookingComplete = (String)request.getSession().getAttribute("onlineSeatBookingComplete");
		if("true".equals(onlineSeatBookingComplete)){
			return "executiveExamBookingStatus";
		}

		int noOfSubjects = 0;

		//Pending, to query Subject sem mapping
		HashMap<String, ProgramSubjectMappingExamBean> subjectProgramSemMap = (HashMap<String, ProgramSubjectMappingExamBean>)
				request.getSession().getAttribute("subjectProgramSemMap");

		try{

			List<ExamBookingTransactionBean> bookingsList = new ArrayList<ExamBookingTransactionBean>();

			ArrayList<String> selectedCenters = executiveBean.getSelectedCenters();

			noOfSubjects = selectedCenters.size();

			if(noOfSubjects == 0 && !subjects.contains("Project") && !subjects.contains("Module 4 - Project")){
				throw new Exception("We are sorry, Seats selected could not be saved, please try again!");
			}

			if(subjects.contains("Project")){
				noOfSubjects++;
			}
			if(subjects.contains("Module 4 - Project")){
				noOfSubjects++;
			}
			
			for (int i = 0; i < selectedCenters.size(); i++) {

				String subjectCenter = selectedCenters.get(i);

				ExamBookingTransactionBean bean = new ExamBookingTransactionBean();
				populateExamBookingBean(bean, subjectCenter, student);

				bean.setYear(examLiveCurrently.getYear());
				bean.setMonth(examLiveCurrently.getMonth());
				bean.setProgram(student.getProgram());
				bean.setPrgmStructApplicable(student.getPrgmStructApplicable());
				//bean.setSem(subjectProgramSemMap.get(subject).getSem());
				bean.setSem("1"); //To be made dynamic later

				bean.setBooked("Y");

				bookingsList.add(bean);
				boolean isSeatAlreadyBookedForDateNTime= executiveExamBookingDao.isSeatAlreadyBookedForDateNTime(bean.getSapid(),
																												 bean.getExamDate(),
																												 bean.getExamTime(),
																												 bean.getSubject());
				if(isSeatAlreadyBookedForDateNTime) {
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "For  Date: "+bean.getExamDate()+" and Time: "+bean.getExamTime()
							+ " other subject is already booked, Please revise your selections and book again. ");


					getAvailableCenters(request, student, subjects, examLiveCurrently);


					return "executiveSelectCenterForm";
				
				}
			}

			boolean centerStillAvailable = checkIfCenterStillAvailable(student, examLiveCurrently, selectedCenters,request);
			if(!centerStillAvailable){
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Center you selected for one of the subject is no longer available. "
						+ "Please make fresh selection of exam centers");


				getAvailableCenters(request, student, subjects, examLiveCurrently);


				return "executiveSelectCenterForm";
			}


			if(noOfSubjects != bookingsList.size()){
				throw new Exception("We are sorry, Seats selected could not be saved, please try selecting exam center again.");
			}

			//Check if student also requested change of center
			if("true".equalsIgnoreCase(executiveBean.getChangeOfCenter())) {
				executiveExamBookingDao.releaseExistingBookings(examLiveCurrently, bookingsList, student);
			}

			List<ExamBookingTransactionBean> examBookings = executiveExamBookingDao.saveExamBooking(bookingsList);
			request.getSession().setAttribute("examBookings", examBookings);
			request.getSession().setAttribute("onlineSeatBookingComplete", "true");
			request.getSession().setAttribute("examCenterIdNameMap", getExamCenterIdNameMap());

			MailSender mailSender = (MailSender)act.getBean("mailer");
			mailSender.sendExecutiveExamBookingSummaryEmail(request, examBookings, examLiveCurrently,edao);

			
			setSuccess(request,"Exam Center & Slot booking successfull");
			return "executiveExamBookingStatus";

		}catch(Exception e){
			

			request.setAttribute("executiveBean", executiveBean);
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in booking Exam Seats. Error: "+e.getMessage());
			return "executiveSelectCenterForm";
		}

	}



	

	private boolean checkIfCenterStillAvailable( StudentExamBean student,	ExecutiveExamOrderBean examLiveCurrently, ArrayList<String> selectedCenters,HttpServletRequest request) {
		boolean centerStillAvailable = true;
		String studentProgramStructure = student.getPrgmStructApplicable();

		List<ExamCenterBean> availableCenters = executiveExamBookingDao.getAvailableCentersForExecutiveExam(examLiveCurrently, student);

		for (int i = 0; i < selectedCenters.size(); i++) {
			String subjectCenter = selectedCenters.get(i);

			String[] data = subjectCenter.split("\\|");

			String subject = data[0];
			String centerId = data[1];
			String examStartTime = data[2];
			String examDate = data[3];

			ArrayList<String> centerIdList = new ArrayList<>();
			for (ExamCenterBean center : availableCenters) {
				if(center.getDate().equals(examDate) && center.getStarttime().equals(examStartTime)){
					centerIdList.add(center.getCenterId());
				}
			}

			if(!centerIdList.contains(centerId)){
				centerStillAvailable = false;
				break;
			}
		}

		return centerStillAvailable;
	}



	private void populateExamBookingBean(ExamBookingTransactionBean bean,
			String subjectCenter, StudentExamBean student) throws ParseException {

		String[] parts = subjectCenter.split("\\|");
		String subject = parts[0];
		String centerId = parts[1];
		String startTime = parts[2];

		bean.setSapid(student.getSapid());
		bean.setSubject(subject);
		bean.setCenterId(centerId);

		String examDate = parts[3];
		bean.setExamDate(examDate);
		bean.setExamTime(startTime);
		String examEndTime = getEndTime(startTime, student);
		bean.setExamEndTime(examEndTime);

	}


	private String getEndTime(String examStartTime, StudentExamBean student) throws ParseException {

		String program = student.getProgram();
		String programStructure = student.getPrgmStructApplicable();
		//String key = program + "-" + programStructure;
		String key = student.getConsumerProgramStructureId();
		int examDurationInMinutes = Integer.parseInt(getProgramDetails().get(key).getExamDurationInMinutes());
		SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
		Date d = df.parse(examStartTime); 
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		cal.add(Calendar.MINUTE, examDurationInMinutes);//2 and half hours of exam
		String endTime = df.format(cal.getTime());

		return endTime;
	}




	//Exam booking pdf
	@RequestMapping(value = "/printExecutiveBookingStatus", method = {RequestMethod.GET, RequestMethod.POST})
	public String printBookingStatus(HttpServletRequest request, HttpServletResponse response,RedirectAttributes redirectAttributes) {

		try{
			String userId = (String)request.getSession().getAttribute("userId");
			ExecutiveConfigurationDao eDao = (ExecutiveConfigurationDao)act.getBean("executiveConfigurationDao");
			List<ExamBookingTransactionBean> examBookings = (List<ExamBookingTransactionBean>) request.getSession().getAttribute("examBookings");
			String fileName = "";

			if(userId == null){
				//Link clicked from Support portal by Agent
				userId = request.getParameter("userId");
			}

			StudentExamBean student = (StudentExamBean)request.getSession().getAttribute("studentExam");

			if(examBookings == null || examBookings.isEmpty()){
				setError(request, "No Exam Bookings found for current Exam Cycle");
				return "examBookingReceipt";

			}
			//ExecutiveExamBookingPdfCreator
			//ExamBookingPDFCreator pdfCreator = new ExamBookingPDFCreator();
			ExecutiveExamBookingPdfCreator pdfCreatorEx = new ExecutiveExamBookingPdfCreator();

			//fileName = pdfCreator.createPDF(examBookings, getExamCenterIdNameMap(), FEE_RECEIPT_PATH, student, confirmedOrReleasedExamBookings);
			fileName = pdfCreatorEx.createPDF(examBookings, getExamCenterIdNameMap(), FEE_RECEIPT_PATH, student);


			File fileToDownload = new File(fileName);
			InputStream inputStream = new FileInputStream(fileToDownload);
			response.setContentType("application/pdf");
			response.setHeader("Content-Disposition", "attachment; filename="+userId+"_exam_Fee_Receipt.pdf"); 
			IOUtils.copy(inputStream, response.getOutputStream());
			response.flushBuffer();

		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in generating Exam Fee Receipt: "+e.getMessage());
			return "examBookingReceipt";
		}
		return null;

	}
	
	//Exam booking pdf quick links 
		@RequestMapping(value = "/printExecutiveBookingStatusQuickLinks", method = {RequestMethod.GET, RequestMethod.POST})
		public String printExecutiveBookingStatusQuickLinks(HttpServletRequest request, HttpServletResponse response,RedirectAttributes redirectAttributes) {

			try{
				String userId = (String)request.getSession().getAttribute("userId");
				ExecutiveConfigurationDao eDao = (ExecutiveConfigurationDao)act.getBean("executiveConfigurationDao");
				List<ExamBookingTransactionBean> examBookings = executiveExamBookingDao.getConfirmedBooking(userId);
				String fileName = "";

				if(userId == null){
					//Link clicked from Support portal by Agent
					userId = request.getParameter("userId");
				}

				StudentExamBean student = (StudentExamBean)request.getSession().getAttribute("studentExam");

				if(examBookings == null || examBookings.isEmpty()){
					setError(request, "No Exam Bookings found for current Exam Cycle");
					return "examBookingReceipt";

				}
				//ExecutiveExamBookingPdfCreator
				//ExamBookingPDFCreator pdfCreator = new ExamBookingPDFCreator();
				ExecutiveExamBookingPdfCreator pdfCreatorEx = new ExecutiveExamBookingPdfCreator();

				//fileName = pdfCreator.createPDF(examBookings, getExamCenterIdNameMap(), FEE_RECEIPT_PATH, student, confirmedOrReleasedExamBookings);
				fileName = pdfCreatorEx.createPDF(examBookings, getExamCenterIdNameMap(), FEE_RECEIPT_PATH, student);


				File fileToDownload = new File(fileName);
				InputStream inputStream = new FileInputStream(fileToDownload);
				response.setContentType("application/pdf");
				response.setHeader("Content-Disposition", "attachment; filename="+userId+"_exam_Fee_Receipt.pdf"); 
				IOUtils.copy(inputStream, response.getOutputStream());
				response.flushBuffer();

			}catch(Exception e){
				
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Error in generating Exam Fee Receipt: "+e.getMessage());
				return "examBookingReceipt";
			}
			return null;

		}


	//Code executiveExamBookingReport Start
	@RequestMapping(value="/admin/executiveExamBookingReportForm", method=RequestMethod.GET)
	public String executiveExamBookingReportForm(HttpServletRequest request, HttpServletResponse response, Model m) {
		
		ExamBookingTransactionBean searchBean = new ExamBookingTransactionBean();
		m.addAttribute("searchBean",searchBean);
		m.addAttribute("rowCount",0);
		return "report/executiveExamBookingReportForm";
	}
	
	//executiveExamBookingReport start
	@RequestMapping(value="/admin/executiveExamBookingReport", method=RequestMethod.POST)
	public String executiveExamBookingReport(HttpServletRequest request, HttpServletResponse response, Model m, @ModelAttribute ExamBookingTransactionBean searchBean) {
		
		m.addAttribute("searchBean",searchBean);
		int rowCount=0;
		try {
			ExecutiveConfigurationDao eDao = (ExecutiveConfigurationDao)act.getBean("executiveConfigurationDao");
			List<ExamBookingTransactionBean> examBookingsForReport =  eDao.getAllExamBookingsByYearMonth(searchBean,getAuthorizedCodes(request));
			m.addAttribute("examBookingsForReport",examBookingsForReport);
			request.getSession().setAttribute("examBookingsForReport",examBookingsForReport);
			rowCount = examBookingsForReport !=null ? examBookingsForReport.size() : 0;
		
			if(examBookingsForReport != null && examBookingsForReport.size() > 0){
				request.setAttribute("success","true");
				request.setAttribute("successMessage","Report generated successfully. Please click link below to download excel.");
			}else{
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No records found.");
			}
		} catch (BeansException e) {
			// TODO Auto-generated catch block
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in generating report.");
		}
		
		m.addAttribute("rowCount",rowCount);
		
		
		return "report/executiveExamBookingReportForm";
	}
	//executiveExamBookingReport end
	
	@RequestMapping(value="/admin/downloadExecutiveExamBookingReport", method=RequestMethod.GET)
	public String downloadExecutiveExamBookingReport(HttpServletRequest request, HttpServletResponse response, Model m) {
		

		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		request.getSession().setAttribute("ICLCRESTRICTED_USER_LIST", ICLCRESTRICTED_USER_LIST);
		
		List<ExamBookingTransactionBean> examBookingsForReport = (List<ExamBookingTransactionBean>)request.getSession().getAttribute("examBookingsForReport");
		m.addAttribute("examBookingsForReport",examBookingsForReport);
		return "executiveExamBookingReportExcelView";
	}
	
	//Code executiveExamBookingReport end
	
	
	//Code executiveExamCenterSlotCapacityReport Start
	@RequestMapping(value="/admin/executiveExamCenterSlotCapacityReportForm", method=RequestMethod.GET)
	public String executiveExamCenterSlotCapacityReportForm(HttpServletRequest request, HttpServletResponse response, Model m) {
		
		ExecutiveExamCenter searchBean = new ExecutiveExamCenter();
		m.addAttribute("searchBean",searchBean);
		m.addAttribute("rowCount",0);
		return "report/executiveExamCenterSlotCapacityReportForm";
	}
	
	//executiveExamCenterSlotCapacityReport start
	@RequestMapping(value="/admin/executiveExamCenterSlotCapacityReport", method=RequestMethod.POST)
	public String executiveExamCenterSlotCapacityReport(HttpServletRequest request, HttpServletResponse response, Model m, @ModelAttribute ExecutiveExamCenter searchBean) {
		
		m.addAttribute("searchBean",searchBean);
		ExecutiveConfigurationDao eDao = (ExecutiveConfigurationDao)act.getBean("executiveConfigurationDao");
		List<ExecutiveExamCenter> examCenterCapacityList= eDao.getAllExamCenterSlotsCapacityByYearMonth(searchBean);
		
		m.addAttribute("examCenterCapacityList",examCenterCapacityList);
		request.getSession().setAttribute("examCenterCapacityList",examCenterCapacityList);
		int rowCount = examCenterCapacityList !=null ? examCenterCapacityList.size() : 0;
		m.addAttribute("rowCount",rowCount);
		return "report/executiveExamCenterSlotCapacityReportForm";
	}
	//executiveExamCenterSlotCapacityReport: end
	
	//Executive exam booking pending report start
	
	//Executive exam booking pending report end
	
	@RequestMapping(value="/admin/downloadExecutiveExamCenterSlotCapacityReport", method=RequestMethod.GET)
	public String downloadexecutiveExamCenterSlotCapacityReport(HttpServletRequest request, HttpServletResponse response, Model m) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		List<ExecutiveExamCenter> examCenterCapacityList = (List<ExecutiveExamCenter>)request.getSession().getAttribute("examCenterCapacityList");
		m.addAttribute("examCenterCapacityList",examCenterCapacityList);
		return "executiveExamCenterSlotCapacityReportExcelView";
	}
	//Code executiveExamCenterSlotCapacityReport end

	//Generate Password code for booked='Y' :START :
		/*@RequestMapping(value = "/generateExecutivePasswordForm", method =  RequestMethod.GET)
		public String generateExecutivePasswordForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {

			ExecutiveBean executiveBean=new ExecutiveBean();
			m.addAttribute("executiveBean", executiveBean);
			request.getSession().setAttribute("yearList",ACAD_YEAR_SAS_LIST);
			request.getSession().setAttribute("monthList",SAS_EXAM_MONTH_LIST);
			
			return "generateExecutivePasswordForm";
		}
		
		@RequestMapping(value = "/generateExecutivePasswordFormData", method = RequestMethod.POST)
		public String generateExecutivePasswordFormData(HttpServletRequest request, HttpServletResponse response, 
				@ModelAttribute ExecutiveBean executiveBean){
			ModelAndView mav=new ModelAndView();
			request.getSession().setAttribute("executiveBean", executiveBean);
			try{
				
				ExecutiveExamDao dao=(ExecutiveExamDao)act.getBean("executiveExamDao");
				ArrayList<ExecutiveBean> listOfExecutiveExamBookings=dao.listOfExecutiveExamBookings(executiveBean);
				String assignPass=null;
				ArrayList<ExecutiveBean> listOfAllExecutiveBookings=null;
				for(ExecutiveBean listOfBeans:listOfExecutiveExamBookings) {
					generatePass();
                    assignPass=generatePass();
                    executiveBean.setPassword(assignPass);
                    executiveBean.setSapid(listOfBeans.getSapid());
                    executiveBean.setExamMonth(listOfBeans.getMonth());
                    executiveBean.setExamYear((Integer.parseInt(listOfBeans.getYear())));
                    executiveBean.setExamDate(listOfBeans.getExamDate());
                    executiveBean.setExamTime(listOfBeans.getExamTime());
                    dao.assignPassword(executiveBean);
				}
				
				listOfAllExecutiveBookings=dao.listOfBookings(executiveBean);
				request.getSession().setAttribute("listOfAllExecutiveBookings",listOfAllExecutiveBookings);
				if(listOfAllExecutiveBookings != null && listOfAllExecutiveBookings.size() > 0){
					request.setAttribute("rowCount",listOfAllExecutiveBookings.size());
					request.setAttribute("success","true");
					request.setAttribute("successMessage","Report generated successfully. Please click link below to download excel.");
				}else{
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "No records found.");
				}
			}catch(Exception e){
				
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Error in generating Report.");
			}
			mav.addObject("executiveBean", executiveBean);

			return "generateExecutivePasswordForm";
		}
		
		@RequestMapping(value = "/downloadExecutiveExamPassword", method = {RequestMethod.GET, RequestMethod.POST})
		public String downloadExecutiveExamPassword(HttpServletRequest request, HttpServletResponse response,
				Model m) throws IOException {
			ArrayList<ExecutiveBean> reportList = (ArrayList<ExecutiveBean>)request.getSession().getAttribute("listOfAllExecutiveBookings");
			m.addAttribute("reportList",reportList);
			
			return "executiveBookingExcelView";
		
		}
		
		public String generatePass() {
			String generatedString =null;
			try {
				generatedString=  RandomStringUtils.randomNumeric(10);
			}
			catch(Exception e) {
				
				return generatedString;
			}
		
		return generatedString;
		}*/
		
		//END
}

