/*package com.nmims.controllers;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.StringArrayPropertyEditor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.nmims.beans.AssignmentStatusBean;
import com.nmims.beans.DDDetails;
import com.nmims.beans.ExamBookingBean;
import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.beans.ExamCenterBean;
import com.nmims.beans.ExamOrderBean;
import com.nmims.beans.FileBean;
import com.nmims.beans.Page;
import com.nmims.beans.PassFailBean;
import com.nmims.beans.Person;
import com.nmims.beans.ProgramSubjectMappingBean;
import com.nmims.beans.StudentBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.beans.TimetableBean;
import com.nmims.daos.ExamBookingDAO;
import com.nmims.daos.ExamCenterDAO;
import com.nmims.daos.LDAPDao;
import com.nmims.daos.ResitExamBookingDAO;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.helpers.AESencrp;
import com.nmims.helpers.ExcelHelper;
import com.nmims.helpers.MailSender;
import com.nmims.helpers.XMLParser;

@Controller
public class ResitExamBookingController extends BaseController{

	@Autowired(required=false)
	ApplicationContext act;

	@Autowired
	ResitExamBookingDAO resitExamBookingDAO; 

	private ArrayList<String> programList = null;
	private ArrayList<String> subjectList = null; 

	private int examFeesPerSubject = 600;
	private int totalFeesForRebooking = 200;
	private final String ONLINE_PAYMENT_INITIATED = "Online Payment Initiated"; 
	private final String ONLINE_PAYMENT_MANUALLY_APPROVED = "Online Payment Manually Approved"; 

	private final String FEE_IN_ADMISSION = "Exam Fees part of Registration Fees/Exam Fees Exempted";
	private final String DD_APPROVAL_PENDING = "DD Approval Pending";
	private final String DD_APPROVED = "DD Approved";
	private final String DD_REJECTED = "DD Rejected";
	private final String NOT_BOOKED = "Not Booked";
	private final String BOOKED = "Booked";
	private final String SEAT_RELEASED = "Seat Released";
	private final String SEAT_RELEASED_NO_CHARGES = "Seat Released - No Charges";
	private final String SEAT_RELEASED_SUBJECT_CLEARED = "Seat Released - Subject Cleared";
	private final String CENTER_CHANGED_BOOKED = "Center Changed and Booked";
	private final String NOT_ELIGIBLE_TO_BOOK = "Not Eligible to Book";
	private final String BOOKING_SUCCESS_MSG = "Your seats are booked. Hall ticket will be available for download shortly. "
			+ "Please click <a href=\"selectResitSubjectsForm\"> here </a> to verify subjects pending to be booked.";

	@Value( "${SECURE_SECRET}" )
	private String SECURE_SECRET; // secret key;
	@Value( "${ACCOUNT_ID}" )
	private String ACCOUNT_ID;
	@Value( "${V3URL}" )
	private String V3URL;
	@Value( "${RESIT_RETURN_URL}" )
	private String RESIT_RETURN_URL;

	private static final Logger logger = Logger.getLogger(ResitExamBookingController.class);

	private ArrayList<String> yearList = new ArrayList<String>(Arrays.asList( 
			"2014","2015","2016")); 

	private ArrayList<ProgramSubjectMappingBean> programSubjectMappingList = null;
	private Map<String, String> examCenterIdNameMap = null;

	private ArrayList<StudentBean> exemptStudentList = null;

	public ArrayList<ProgramSubjectMappingBean> getProgramSubjectMappingList(){
		if(this.programSubjectMappingList == null || this.programSubjectMappingList.size() == 0){
			ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
			this.programSubjectMappingList = eDao.getProgramSubjectMappingList();
		}
		return programSubjectMappingList;
	}

	public ArrayList<StudentBean> getExemptStudentList(){
		if(this.exemptStudentList == null || this.exemptStudentList.size() == 0){
			ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
			this.exemptStudentList = eDao.getExemptStudentList();
		}
		return exemptStudentList;
	}

	public Map<String, String> getExamCenterIdNameMap(){
		//if(this.examCenterIdNameMap == null || examCenterIdNameMap.size() == 0){
		ExamCenterDAO dao = (ExamCenterDAO)act.getBean("examCenterDAO");
		this.examCenterIdNameMap = dao.getExamCenterIdNameMap();
		//}
		return examCenterIdNameMap;
	}

	public ArrayList<String> getSubjectList(){
		if(this.subjectList == null){
			StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			this.subjectList = dao.getAllSubjects();
		}
		return subjectList;
	}

	public ArrayList<String> getProgramList(){
		if(this.programList == null){
			StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			this.programList = dao.getAllPrograms();
		}
		return programList;
	}

		
	@RequestMapping(value = "/updateContactInfo", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView updateContactInfo(HttpServletRequest request, HttpServletResponse respnse) {

		if(!checkSession(request, respnse)){
			return new ModelAndView("studentPortalRediret");
		}

		String sapid = (String)request.getSession().getAttribute("userId");
		try{
			String email = request.getParameter("emailId");
			String mobile = request.getParameter("mobile");
			String altPhone = request.getParameter("altPhone");

			ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");

			eDao.updateStudentContact(sapid, email, mobile, altPhone);
			request.setAttribute("success","true");
			request.setAttribute("successMessage","Contact Details updated successfully");

		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in updating information. Please contact head office");
		}
		return verifyInformation(request,respnse);
	}



	@RequestMapping(value = "/cCAED", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView changeCenterAfterEndDate(HttpServletRequest request, HttpServletResponse respnse) {

		LDAPDao dao = (LDAPDao)act.getBean("ldapdao");
		ModelAndView modelnView = new ModelAndView("studentHome");

		String sapIdEncrypted = request.getParameter("sapId");
		String sapId = "NA";
		try {
			sapId = AESencrp.decrypt(sapIdEncrypted);
			request.getSession().setAttribute("userId", sapId);
			Person person = dao.findPerson(sapId);
			//Person person = new Person();
			person.setUserId(sapId);
			modelnView.addObject("displayName", person.getDisplayName() );
			request.getSession().setAttribute("user", person);
			request.getSession().setAttribute("userId", sapId);
		}catch (Exception e) {
			
		}

		return verifyInformation(request, respnse);

	}

	@RequestMapping(value = "/selectResitSubjectsForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView selectResitSubjectsForm(HttpServletRequest request, HttpServletResponse respnse)  {

		if(!checkSession(request, respnse)){
			return new ModelAndView("studentPortalRediret");
		}

		request.getSession().setAttribute("ddSeatBookingComplete", null);
		request.getSession().setAttribute("onlineSeatBookingComplete", null);
		request.getSession().setAttribute("freeSeatBookingComplete", null);

		ModelAndView modelnView = new ModelAndView("resitExam/selectSubjects");
		String sapid = (String)request.getSession().getAttribute("userId");
		ArrayList<ProgramSubjectMappingBean> applicableSubjectsList = new ArrayList<>();
		ArrayList<String> applicableSubjects = new ArrayList<>();
		ArrayList<String> bookedSubjects = new ArrayList<>();
		ArrayList<String> releasedSubjects = new ArrayList<>();
		ArrayList<String> releasedNoChargeSubjects = new ArrayList<>();
		ArrayList<String> releasedPassedSubjects = new ArrayList<>();
		ArrayList<String> freeApplicableSubjects = new ArrayList<>(); 

		ArrayList<String> approvedOnlineTransactionSubjects = new ArrayList<>();
		boolean hasApprovedOnlineTransactions = false;
		boolean hasReleasedSubjects = false;
		boolean hasReleasedNoChargeSubjects = false;
		boolean hasFreeSubjects = false;
		
		int subjectsToPay = 0;
		
		
		ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
		boolean isExamRegistraionLive = dao.isConfigurationLive("Re-sit Exam Registation");
		
		
		String sapIdEncrypted = request.getParameter("eid");
		String sapIdFromURL = null;
		try {
			if(sapIdEncrypted != null){
				sapIdFromURL = AESencrp.decrypt(sapIdEncrypted);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		if(sapIdFromURL != null && sapid.equals(sapIdFromURL)){
			//If additional encrypted parameter is sent in URL, then allow to book after end date as well.
			isExamRegistraionLive = false;//Temporarily made false. Make true in next cycle
		}
		modelnView.addObject("isExamRegistraionLive", isExamRegistraionLive);
		if(!isExamRegistraionLive){
			setError(request, "Re-Sit Exam Registration is not Live currently");
		}

		try{
			ExamBookingBean examBooking = new ExamBookingBean();
			StudentMarksDAO sDao = (StudentMarksDAO)act.getBean("studentMarksDAO");

			StudentBean student = (StudentBean)request.getSession().getAttribute("student");
			
			if("Offline".equals(student.getExamMode())){
				modelnView = new ModelAndView("studentPortalHome");
				setError(request, "You are not authorized to register for Resit Examination");
				modelnView.addObject("examBooking", examBooking);
			}

			String mostRecentTimetablePeriod = sDao.getMostRecentTimeTablePeriod();
			ExamOrderBean exam = sDao.getUpcomingExam();
			examBooking.setYear(exam.getYear());
			examBooking.setMonth(exam.getMonth());
			modelnView.addObject("mostRecentTimetablePeriod", mostRecentTimetablePeriod);

			
			boolean hasClearedProject = true;
			ArrayList<PassFailBean> failList = (ArrayList<PassFailBean>)dao.getFailedSubjectsList(sapid);
			
			String lastRegisteredSem = dao.getLastSemRegisteredBeforeResitExam(sapid);
			if("4".equals(lastRegisteredSem)){
				hasClearedProject = dao.checkIfProjectIsCleared(sapid);
			}

			ArrayList<String> failAssgnSubmitted = new ArrayList<>();
			ArrayList<String> failAssgnNotSubmitted = new ArrayList<>();
			ArrayList<String> allFailSubjectsList = new ArrayList<>();

			if(failList == null ){
				failList = new ArrayList<>();
			}
			
			if(!hasClearedProject){
				failAssgnSubmitted.add("Project");
			}
			for (int i = 0; i < failList.size(); i++) {
				if("ANS".equalsIgnoreCase(failList.get(i).getAssignmentscore())){
					failAssgnNotSubmitted.add(failList.get(i).getSubject());
				}else{
					failAssgnSubmitted.add(failList.get(i).getSubject());
				}
				allFailSubjectsList.add(failList.get(i).getSubject());
			}

			ArrayList<String> tempAssgnSubmittedSubjectsList = (ArrayList<String>)dao.getAssignSubmittedSubjectsList(sapid);
			
			
			ArrayList<ExamBookingTransactionBean> subjectsBooked = dao.getConfirmedOrRelesedBooking(sapid);

			
			ArrayList<String> freeSubjects = dao.getIndividualFreeSubjects(student);
			Map<String, String> examCenterIdNameMap = getExamCenterIdNameMap();
			HashMap<String, String> subjectCenterMap = new HashMap<>();

			for (int i = 0; i < subjectsBooked.size(); i++) {

				ExamBookingTransactionBean bean = subjectsBooked.get(i);
				String subject = bean.getSubject();
				if("Y".equals(bean.getBooked())){
					bookedSubjects.add(subject);
					subjectCenterMap.put(subject, examCenterIdNameMap.get(bean.getCenterId()));
				}else if("RL".equals(bean.getBooked())  && SEAT_RELEASED.equalsIgnoreCase(bean.getTranStatus())  && (!releasedSubjects.contains(subject))){
					releasedSubjects.add(subject);
				}else if("RL".equals(bean.getBooked())  && SEAT_RELEASED_NO_CHARGES.equalsIgnoreCase(bean.getTranStatus())  && (!releasedNoChargeSubjects.contains(subject))){
					releasedNoChargeSubjects.add(subject);
				}else if("RL".equals(bean.getBooked())  && SEAT_RELEASED_SUBJECT_CLEARED.equalsIgnoreCase(bean.getTranStatus())  && (!releasedPassedSubjects.contains(subject))){
					releasedPassedSubjects.add(subject);
				}
			}

			//Doing below code, if student seat is released, then booked and then again released. 
			//In this case old released subjects will again pop up as to be booked, whereas there are separate rows for it as booked well.
			for (int i = 0; i < subjectsBooked.size(); i++) {
				ExamBookingTransactionBean bean = subjectsBooked.get(i);
				String subject = bean.getSubject();
				String booked = bean.getBooked();

				if("Y".equals(bean.getBooked())){
					releasedSubjects.remove(subject);
					releasedNoChargeSubjects.remove(subject);
				}
			}

			//Get subjects whose online transaction was manually approved
			ArrayList<ExamBookingTransactionBean> ddSubjectsList = dao.getDDAndApprovedOnlineTransSubjects(sapid);
			for (int i = 0; i < ddSubjectsList.size(); i++) {
				ExamBookingTransactionBean bean = ddSubjectsList.get(i);
				if(ONLINE_PAYMENT_MANUALLY_APPROVED.equals(bean.getTranStatus())){
					approvedOnlineTransactionSubjects.add(bean.getSubject());
				}
			}

			request.getSession().setAttribute("approvedOnlineTransactionSubjects", approvedOnlineTransactionSubjects);

			ArrayList<ProgramSubjectMappingBean> programSubjectMappingList = getProgramSubjectMappingList();

			for (int i = 0; i < programSubjectMappingList.size(); i++) {
				ProgramSubjectMappingBean bean = programSubjectMappingList.get(i);

				if(bean.getPrgmStructApplicable().equals(student.getPrgmStructApplicable()) && bean.getProgram().equals(student.getProgram())
						&& !student.getWaivedOffSubjects().contains(bean.getSubject()) ){

					
					if(failAssgnSubmitted.contains(bean.getSubject())){
						bean.setAssignmentSubmitted("Yes");
						bean.setCanBook("Yes");
						
						bean.setBookingStatus(NOT_BOOKED);
						applicableSubjectsList.add(bean);
						applicableSubjects.add(bean.getSubject());
						subjectsToPay++;
						
						if("Project".equals(bean.getSubject())){
							bean.setAssignmentSubmitted("NA");
						}
					}else if(allFailSubjectsList.contains(bean.getSubject()) && tempAssgnSubmittedSubjectsList.contains(bean.getSubject())){
						//If Jun entry is ANS, and student submitted assignment in Sep, then he should be allowed to book
						bean.setAssignmentSubmitted("Yes");
						bean.setCanBook("Yes");
						bean.setBookingStatus(NOT_BOOKED);
						applicableSubjectsList.add(bean);
						applicableSubjects.add(bean.getSubject());
						subjectsToPay++;
					}else if(failAssgnNotSubmitted.contains(bean.getSubject())){
						bean.setAssignmentSubmitted("No");
						bean.setCanBook("No");
						bean.setBookingStatus(NOT_ELIGIBLE_TO_BOOK);
						applicableSubjectsList.add(bean);
					}

					if(bookedSubjects.contains(bean.getSubject())){
						bean.setCanBook("No");
						bean.setCanFreeBook("No");
						bean.setBookingStatus(BOOKED);
						bean.setCenterName(subjectCenterMap.get(bean.getSubject()));
						applicableSubjects.remove(bean.getSubject());
						subjectsToPay--;
					}else if(releasedSubjects.contains(bean.getSubject())){
						hasReleasedSubjects = true;
						bean.setCanBook("No");
						bean.setCanFreeBook("No");
						bean.setBookingStatus(SEAT_RELEASED);
						applicableSubjects.remove(bean.getSubject());
						subjectsToPay--;
					}else if(releasedNoChargeSubjects.contains(bean.getSubject())){
						hasReleasedNoChargeSubjects = true;
						bean.setCanBook("No");
						bean.setCanFreeBook("No");
						bean.setBookingStatus(SEAT_RELEASED_NO_CHARGES);
						applicableSubjects.remove(bean.getSubject());
						subjectsToPay--;
					}else if(releasedPassedSubjects.contains(bean.getSubject())){
						hasReleasedNoChargeSubjects = true;
						bean.setCanBook("No");
						bean.setCanFreeBook("No");
						bean.setBookingStatus(SEAT_RELEASED_SUBJECT_CLEARED);
						applicableSubjects.remove(bean.getSubject());
						subjectsToPay--;
					}else if(approvedOnlineTransactionSubjects.contains(bean.getSubject())){
						hasApprovedOnlineTransactions = true;
						bean.setCanBook("No");
						bean.setBookingStatus(ONLINE_PAYMENT_MANUALLY_APPROVED);
						applicableSubjects.remove(bean.getSubject());
						subjectsToPay--;
					}else if(freeSubjects.contains(bean.getSubject()) && applicableSubjects.contains(bean.getSubject())
							&& "Yes".equals(bean.getCanBook())){
						hasFreeSubjects = true;
						bean.setCanBook("No");
						bean.setCanFreeBook("Yes");
						bean.setBookingStatus(FEE_IN_ADMISSION);
						applicableSubjects.remove(bean.getSubject());
						subjectsToPay--;
						freeApplicableSubjects.add(bean.getSubject());
					}
				}
			}
			for (ProgramSubjectMappingBean bean : applicableSubjectsList) {
				String canBook = bean.getCanBook();
				bean.setExamFees(examFeesPerSubject+"");
			}

			if(hasApprovedOnlineTransactions){
				request.setAttribute("hasApprovedOnlineTransactions", "true");
			}
			if(hasReleasedSubjects){
				request.setAttribute("hasReleasedSubjects", "true");
			}
			if(hasReleasedNoChargeSubjects){
				request.setAttribute("hasReleasedNoChargeSubjects", "true");
			}
			if(hasFreeSubjects){
				request.setAttribute("hasFreeSubjects", "true");
			}

			request.getSession().setAttribute("releasedSubjects", releasedSubjects);
			request.getSession().setAttribute("releasedNoChargeSubjects", releasedNoChargeSubjects);

			examBooking.setApplicableSubjects(applicableSubjects);

			modelnView.addObject("applicableSubjectsList", applicableSubjectsList);
			modelnView.addObject("applicableSubjectsListCount", applicableSubjectsList.size());

			request.getSession().setAttribute("applicableSubjectsList", applicableSubjectsList);
			request.getSession().setAttribute("examCenterIdNameMap", getExamCenterIdNameMap());
			modelnView.addObject("examBooking", examBooking);
			modelnView.addObject("subjectsToPay", subjectsToPay);

		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in getting subjects.");
		}
		return modelnView;
	}

	@RequestMapping(value = "/selectResitExamCenter", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView selectResitExamCenter(HttpServletRequest request, HttpServletResponse respnse, Model m, @ModelAttribute ExamBookingBean examBooking) {
		if(!checkSession(request, respnse)){
			return new ModelAndView("studentPortalRediret");
		}

		ArrayList<String> subjects = examBooking.getApplicableSubjects();
		request.getSession().setAttribute("subjects", subjects);

		List<TimetableBean> timeTableList = resitExamBookingDAO.getTimetableForResit();
		request.getSession().setAttribute("timeTableList", timeTableList);

		getAvailableCenters(request, subjects);

		ModelAndView modelnView = new ModelAndView("resitExam/selectExamCenter");
		modelnView.addObject("examBooking", examBooking);


		return modelnView;
	}

	@RequestMapping(value="/getAvailableCentersForCity", method = {RequestMethod.GET, RequestMethod.POST})
	public @ResponseBody String getAvailableCentersForCity(HttpServletRequest request, HttpServletResponse response ) throws ParseException {
		String output = null;
		try {
			
		
		String subjectCenter = request.getParameter("depdrop_parents[0]"); //This is the name used by plugin
		String[] tempArray = subjectCenter.split("\\|");
		String subject = tempArray[0];
		String centerId = tempArray[1];
		
		ExamCenterDAO ecDao = (ExamCenterDAO)act.getBean("examCenterDAO");
		String sapid = (String)request.getSession().getAttribute("userId");
		List<ExamCenterBean> availableCenters = ecDao.getAvailableCentersForResitExam(sapid);
		
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
				
				String dropDownValue = date + "|" + startTime + "|" + city;
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

	private void getAvailableCenters(HttpServletRequest request, ArrayList<String> subjects) {

		ExamCenterDAO ecDao = (ExamCenterDAO)act.getBean("examCenterDAO");
		Map<String, List<ExamCenterBean>> subjectAvailableCentersMap = new HashMap<String, List<ExamCenterBean>>();
		String sapid = (String)request.getSession().getAttribute("userId");
		List<ExamCenterBean> availableCenters = ecDao.getAvailableCentersForResitExam(sapid);
		for (String subject : subjects) {
			subjectAvailableCentersMap.put(subject, availableCenters);
		}
		request.getSession().setAttribute("subjectAvailableCentersMap", subjectAvailableCentersMap);

	}


	@RequestMapping(value = "/resitGoToGateway", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView goToGateway(HttpServletRequest request, HttpServletResponse respnse, @ModelAttribute ExamBookingBean examBooking, ModelMap model) {

		if(!checkSession(request, respnse)){
			return new ModelAndView("studentPortalRediret");
		}
		
		boolean hasProject = false;
		
		String hasReleasedSubjects = request.getParameter("hasReleasedSubjects");
		ModelAndView modelnView = new ModelAndView("resitExam/bookingStatus");
		String onlineSeatBookingComplete = (String)request.getSession().getAttribute("onlineSeatBookingComplete");
		if("true".equals(onlineSeatBookingComplete)){
			return modelnView;
		}
		int noOfSubjects = 0;

		try{

			ArrayList<String> subjects = (ArrayList<String>)request.getSession().getAttribute("subjects");
			String sapid = (String)request.getSession().getAttribute("userId");

			String trackId = sapid + System.currentTimeMillis() ;
			request.getSession().setAttribute("trackId", trackId);

			String message = "Exam fees for "+sapid;
			if("true".equalsIgnoreCase(hasReleasedSubjects)){
				message = "Exam Center Change fees for "+sapid;
			}
			Map<String, PassFailBean> subjectPassFailBeanMap = getSubjectPassFailBeanMap(sapid);
			StudentBean student = (StudentBean)request.getSession().getAttribute("student");
			String prgrmStructApplicable = student.getPrgmStructApplicable();
			
			List<ExamBookingTransactionBean> bookingsList = new ArrayList<>();

			ArrayList<String> selectedCenters = examBooking.getSelectedCenters();

			String examYear = examBooking.getYear();
			String examMonth = examBooking.getMonth();


			noOfSubjects = selectedCenters.size();
			if(subjects.contains("Project")){
				noOfSubjects++;
			}
			for (int i = 0; i < selectedCenters.size(); i++) {

				String subjectCenter = selectedCenters.get(i);

				String[] data = subjectCenter.split("\\|");

				String subject = data[0];
				String centerId = data[1];
				String examDate = data[2];
				String examStartTime = data[3];
				//String examEndTime = data[4];


				ExamBookingTransactionBean bean = new ExamBookingTransactionBean();
				PassFailBean failBean = subjectPassFailBeanMap.get(subject);
				bean.setSapid(sapid);
				bean.setSubject(subject);
				bean.setCenterId(centerId);
				bean.setYear(examYear);
				bean.setMonth(examMonth);
				bean.setProgram(failBean.getProgram());
				bean.setSem(failBean.getSem());
				bean.setExamDate(examDate);
				bean.setExamTime(examStartTime);
				String examEndTime = getEndTime(examStartTime);
				bean.setExamEndTime(examEndTime);
				bean.setTrackId(trackId);

				if("true".equalsIgnoreCase(hasReleasedSubjects)){
					bean.setAmount(totalFeesForRebooking + "");
				}else{
					bean.setAmount(examFeesPerSubject * noOfSubjects+"");
				}

				bean.setTranStatus(ONLINE_PAYMENT_INITIATED);
				bean.setBooked("N");
				bean.setPaymentMode("Online");
				bean.setExamMode("Online");

				bookingsList.add(bean);
			}
			
			if(subjects.contains("Project")){
				hasProject = true;
				
				ExamBookingTransactionBean bean = new ExamBookingTransactionBean();

				bean.setSapid(sapid);
				bean.setSubject("Project");
				bean.setCenterId("-1");
				bean.setYear(examYear);
				bean.setMonth(examMonth);
				bean.setProgram(student.getProgram());
				bean.setSem("4");
				bean.setExamDate(examYear + "/01/01");
				bean.setExamTime("00:00");
				bean.setExamEndTime("00:00");
				bean.setTrackId(trackId);
				
				if("Online".equals(student.getExamMode())){
					bean.setExamMode("Online");
				}else{
					bean.setExamMode("Offline");
				}
				
				if("true".equalsIgnoreCase(hasReleasedSubjects)){
					bean.setAmount(totalFeesForRebooking + "");
				}else{
					bean.setAmount(examFeesPerSubject * noOfSubjects+"");
				}

				bean.setTranStatus(ONLINE_PAYMENT_INITIATED);
				bean.setBooked("N");
				bean.setPaymentMode("Online");

				bookingsList.add(bean);
			}


			ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");

			int[] clearingResult = dao.clearOldOnlineInitiationTransaction(sapid, null);

			boolean centerStillAvailable = checkIfCenterStillAvailable(selectedCenters, sapid);
			if(!centerStillAvailable){
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Center you selected for one of the subject is no longer available. "
						+ "Please make fresh selection of exam centers");

				getAvailableCenters(request, subjects);

				modelnView = new ModelAndView("resitExam/selectExamCenter");
				modelnView.addObject("examBooking", examBooking);

				return modelnView;
			}

			boolean isOnline = false;
			if("Online".equals(student.getExamMode())){
				isOnline = true;
			}
			
			//int[] result = dao.upsertOnlineInitiationTransaction(sapid, bookingsList, hasProject);
			dao.upsertOnlineInitiationTransaction(sapid, bookingsList, hasProject, isOnline);
			int totalFees = 0;

			if("true".equalsIgnoreCase(hasReleasedSubjects)){
				totalFees = totalFeesForRebooking;
			}else{
				totalFees = examFeesPerSubject * noOfSubjects;
			}

			request.getSession().setAttribute("totalFees", totalFees + "");
			fillPaymentParametersInMap(model, student, totalFees, trackId, message);

			request.getSession().setAttribute("SECURE_SECRET", SECURE_SECRET);
			return new ModelAndView(new RedirectView("pay"), model);

		}catch(Exception e){
			

			modelnView = new ModelAndView("resitExam/selectExamCenter");
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in initiating Online transaction. Error: "+e.getMessage());
			modelnView.addObject("examBooking", examBooking);
			return modelnView;
		}

	}


	private String getEndTime(String examStartTime) throws ParseException {
		
		 SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
		 Date d = df.parse(examStartTime); 
		 Calendar cal = Calendar.getInstance();
		 cal.setTime(d);
		 cal.add(Calendar.MINUTE, 150);//2 and half hours of exam
		 String endTime = df.format(cal.getTime());
		 
		 return endTime;
	}

	private Map<String, PassFailBean> getSubjectPassFailBeanMap(String sapid) {
		ArrayList<PassFailBean> failList = (ArrayList<PassFailBean>)resitExamBookingDAO.getFailedSubjectsList(sapid);
		Map<String, PassFailBean> subjectPassFailBeanMap = new HashMap<String, PassFailBean>();
		if(failList != null){
			for (PassFailBean passFailBean : failList) {
				subjectPassFailBeanMap.put(passFailBean.getSubject(), passFailBean);
			}
		}

		return subjectPassFailBeanMap;
	}

	private boolean checkIfCenterStillAvailable( ArrayList<String> selectedCenters, String sapId) {
		ExamCenterDAO ecDao = (ExamCenterDAO)act.getBean("examCenterDAO");
		boolean centerStillAvailable = true;

		List<ExamCenterBean> availableCenters = ecDao.getAvailableCentersForResitExam(sapId);

		for (int i = 0; i < selectedCenters.size(); i++) {
			String subjectCenter = selectedCenters.get(i);

			String[] data = subjectCenter.split("\\|");

			String subject = data[0];
			String centerId = data[1];
			String examDate = data[2];
			String examStartTime = data[3];
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


	private void fillPaymentParametersInMap(ModelMap model,	StudentBean student, int totalFees, String trackId, String message) {

		String address = student.getAddress();
		if(address == null || address.trim().length() == 0){
			address = "Not Available";
		}else if(address.length() > 200){
			address = address.substring(0, 200);
		}

		String city = student.getCity();
		if(city == null || city.trim().length() == 0){
			city = "Not Available";
		}else if(city.length() > 30){
			city = city.substring(0, 30);
		}

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


		model.addAttribute("channel", "10");
		model.addAttribute("account_id", ACCOUNT_ID);
		model.addAttribute("reference_no", trackId);
		model.addAttribute("amount", totalFees);
		model.addAttribute("mode", "LIVE");
		model.addAttribute("currency", "INR");
		model.addAttribute("currency_code", "INR");
		model.addAttribute("description", message);
		model.addAttribute("return_url", RESIT_RETURN_URL);
		model.addAttribute("name", student.getFirstName()+ " "+student.getLastName());
		model.addAttribute("address",URLEncoder.encode(address));
		model.addAttribute("city", city);
		model.addAttribute("country", "IND");
		model.addAttribute("postal_code", pin);
		model.addAttribute("phone", mobile);
		model.addAttribute("email", emailId);
		model.addAttribute("algo", "MD5");
		model.addAttribute("V3URL", V3URL);

	}

	@RequestMapping(value = "/resitExamFeesReponse", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView resitExamFeesReponse(HttpServletRequest request, HttpServletResponse respnse, ModelMap model) throws Exception {
		if(!checkSession(request, respnse)){
			return new ModelAndView("studentPortalRediret");
		}

		saveAllTransactionDetails(request);

		String onlineSeatBookingComplete = (String)request.getSession().getAttribute("onlineSeatBookingComplete");
		if("true".equals(onlineSeatBookingComplete)){
			return new ModelAndView("resitExam/bookingStatus");
		}

		String trackId = (String)request.getSession().getAttribute("trackId");
		String totalFees = (String)request.getSession().getAttribute("totalFees");

		boolean isSuccessful = isTransactionSuccessful(request);
		boolean isHashMatching = isHashMatching(request);
		boolean isAmountMatching = isAmountMatching(request, totalFees);
		boolean isTrackIdMatching = isTrackIdMatching(request, trackId);
		String errorMessage = null;

		if(!isSuccessful){
			errorMessage = "Error in processing payment. Error: " + request.getParameter("Error")+ " Code: "+request.getParameter("ResponseCode");
		}

		if(!isHashMatching){
			errorMessage = "Error in processing payment. Error: Hashvalue not matching. Tampering in response found. Track ID: "+trackId;
		}

		if(!isAmountMatching){
			errorMessage = "Error in processing payment. Error: Fees " + totalFees + " not matching with amount paid "+request.getParameter("Amount");
		}

		if(!isTrackIdMatching){
			errorMessage = "Error in processing payment. Error: Track ID: "+trackId + " not matching with Merchant Ref No. "+request.getParameter("MerchantRefNo");
		}
		if(errorMessage != null){
			setError(request, errorMessage);
			return selectResitSubjectsForm(request, respnse);
		}else{
			return saveSuccessfulTransaction(request, respnse, model);
		}
	}





	public ModelAndView saveSuccessfulTransaction(HttpServletRequest request, HttpServletResponse respnse, ModelMap model) {
		if(!checkSession(request, respnse)){
			return new ModelAndView("studentPortalRediret");
		}

		ModelAndView modelnView = new ModelAndView("resitExam/bookingStatus");

		String sapid = (String)request.getSession().getAttribute("userId");
		String trackId = (String)request.getSession().getAttribute("trackId");

		try {
			ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
			ExamBookingTransactionBean bean = new ExamBookingTransactionBean();
			bean.setSapid(sapid);
			bean.setTrackId(trackId);

			bean.setResponseMessage(request.getParameter("ResponseMessage"));
			bean.setTransactionID(request.getParameter("TransactionID"));
			bean.setRequestID(request.getParameter("RequestID"));
			bean.setMerchantRefNo(request.getParameter("MerchantRefNo"));
			bean.setSecureHash(request.getParameter("SecureHash"));
			bean.setRespAmount(request.getParameter("Amount"));
			bean.setRespTranDateTime(request.getParameter("DateCreated"));
			bean.setResponseCode(request.getParameter("ResponseCode"));
			bean.setRespPaymentMethod(request.getParameter("PaymentMethod"));
			bean.setIsFlagged(request.getParameter("IsFlagged"));
			bean.setPaymentID(request.getParameter("PaymentID"));
			bean.setError(request.getParameter("Error"));
			bean.setDescription(request.getParameter("Description"));

			//List<ExamBookingTransactionBean> examBookings = dao.updateSeatsForOnlineTransaction(bean);

			//Below method is made using single Connection to ensure Commit and Rollback
			List<ExamBookingTransactionBean> examBookings = dao.updateSeatsForOnlineUsingSingleConnection(bean);

			request.getSession().setAttribute("examBookings", examBookings);
			request.getSession().setAttribute("onlineSeatBookingComplete", "true");

			request.setAttribute("success","true");
			request.setAttribute("successMessage",BOOKING_SUCCESS_MSG);

			MailSender mailSender = (MailSender)act.getBean("mailer");
			mailSender.sendBookingSummaryEmail(request, dao);

		} catch (Exception e) {
			MailSender mailSender = (MailSender)act.getBean("mailer");
			mailSender.mailStackTrace("Error in Saving Successful Transaction", e);

			request.getSession().setAttribute("onlineSeatBookingComplete", "false");
			request.getSession().setAttribute("examCenterIdNameMap", getExamCenterIdNameMap());
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Seats NOT booked. Error in recording your transaction details. Please contact Head Office to get it sorted out.");
			return new ModelAndView("resitExam/selectExamCenter");
		}
		request.getSession().setAttribute("onlineSeatBookingComplete", "true");
		request.getSession().setAttribute("examCenterIdNameMap", getExamCenterIdNameMap());
		return modelnView;
	}

	private void saveAllTransactionDetails(HttpServletRequest request) {

		try {
			String sapid = (String)request.getSession().getAttribute("userId");
			String trackId = (String)request.getSession().getAttribute("trackId");
			ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
			ExamBookingTransactionBean bean = new ExamBookingTransactionBean();
			bean.setSapid(sapid);
			bean.setTrackId(trackId);

			bean.setResponseMessage(request.getParameter("ResponseMessage"));
			bean.setTransactionID(request.getParameter("TransactionID"));
			bean.setRequestID(request.getParameter("RequestID"));
			bean.setMerchantRefNo(request.getParameter("MerchantRefNo"));
			bean.setSecureHash(request.getParameter("SecureHash"));
			bean.setRespAmount(request.getParameter("Amount"));
			bean.setRespTranDateTime(request.getParameter("DateCreated"));
			bean.setResponseCode(request.getParameter("ResponseCode"));
			bean.setRespPaymentMethod(request.getParameter("PaymentMethod"));
			bean.setIsFlagged(request.getParameter("IsFlagged"));
			bean.setPaymentID(request.getParameter("PaymentID"));
			bean.setError(request.getParameter("Error"));
			bean.setDescription(request.getParameter("Description"));

			dao.insertOnlineTransaction(bean);

		} catch (Exception e) {
			
		}
	}
	
	
	@RequestMapping(value = "/selectResitExamCenterForOnline", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView selectResitExamCenterForOnline(HttpServletRequest request, HttpServletResponse respnse, @ModelAttribute ExamBookingBean examBooking) {
		if(!checkSession(request, respnse)){
			return new ModelAndView("studentPortalRediret");
		}

		ArrayList<String> subjects = (ArrayList<String>)request.getSession().getAttribute("approvedOnlineTransactionSubjects");
		request.getSession().setAttribute("subjects", subjects);

		getAvailableCenters(request, subjects);
		request.setAttribute("hasApprovedOnlineTransactions", "true");

		ModelAndView modelnView = new ModelAndView("resitExam/selectExamCenter");
		modelnView.addObject("examBooking", examBooking);
		return modelnView;
	}

	@RequestMapping(value = "/saveResitSeatsForOnline", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView saveSeatsForOnline(HttpServletRequest request, HttpServletResponse respnse,	@ModelAttribute ExamBookingBean examBooking, ModelMap model) throws ParseException {
		if(!checkSession(request, respnse)){
			return new ModelAndView("studentPortalRediret");
		}
		ModelAndView modelnView = new ModelAndView("resitExam/bookingStatus");
		String onlineSeatBookingComplete = (String)request.getSession().getAttribute("onlineSeatBookingComplete");
		if("true".equals(onlineSeatBookingComplete)){
			return modelnView;
		}

		String sapid = (String)request.getSession().getAttribute("userId");
		ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");

		List<ExamBookingTransactionBean> bookingsList = new ArrayList<>();

		ArrayList<String> subjects = (ArrayList<String>)request.getSession().getAttribute("subjects");

		ArrayList<String> selectedCenters = examBooking.getSelectedCenters();

		int year = Calendar.getInstance().get(Calendar.YEAR);

		String examYear = year+"";

		for (int i = 0; i < selectedCenters.size(); i++) {
			String subjectCenter = selectedCenters.get(i);
			
			String[] data = subjectCenter.split("\\|");

			String subject = data[0];
			String centerId = data[1];
			String examDate = data[2];
			String examStartTime = data[3];
			
			ExamBookingTransactionBean bean = new ExamBookingTransactionBean();

			bean.setSapid(sapid);
			bean.setSubject(subject);
			bean.setCenterId(centerId);
			
			bean.setExamDate(examDate);
			bean.setExamTime(examStartTime);
			String examEndTime = getEndTime(examStartTime);
			bean.setExamEndTime(examEndTime);

			bookingsList.add(bean);
		}
		
		if(subjects.contains("Project")){
			ExamBookingTransactionBean bean = new ExamBookingTransactionBean();

			bean.setSapid(sapid);
			bean.setSubject("Project");
			bean.setCenterId("-1");
			bean.setExamDate(examYear + "/01/01");
			bean.setExamTime("00:00");
			bean.setExamEndTime("00:00");
			
			bookingsList.add(bean);
		}


		boolean centerStillAvailable = checkIfCenterStillAvailable(selectedCenters, sapid);
		if(!centerStillAvailable){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Center you selected for one of the subject is no longer available. "
					+ "Please make fresh selection of exam centers");

			return selectResitExamCenterForOnline(request, respnse, examBooking);
		}

		List<ExamBookingTransactionBean> examBookings = resitExamBookingDAO.updateSeatsForOnlineApprovedTransaction(sapid, bookingsList);
		request.getSession().setAttribute("examBookings", examBookings);
		request.getSession().setAttribute("onlineSeatBookingComplete", "true");

		request.setAttribute("success","true");
		request.setAttribute("successMessage",BOOKING_SUCCESS_MSG);
		request.getSession().setAttribute("examCenterIdNameMap", getExamCenterIdNameMap());
		MailSender mailSender = (MailSender)act.getBean("mailer");
		mailSender.sendBookingSummaryEmail(request, dao);
		
		return modelnView;
	}
	
	@RequestMapping(value = "/saveResitSeatsForFree", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView saveResitSeatsForFree(HttpServletRequest request, HttpServletResponse respnse,	@ModelAttribute ExamBookingBean examBooking, ModelMap model) throws ParseException {
		if(!checkSession(request, respnse)){
			return new ModelAndView("studentPortalRediret");
		}
		
		ModelAndView modelnView = new ModelAndView("resitExam/bookingStatus");
		String freeSeatBookingComplete = (String)request.getSession().getAttribute("freeSeatBookingComplete");
		if("true".equals(freeSeatBookingComplete)){
			return modelnView;
		}

		StudentBean student = (StudentBean)request.getSession().getAttribute("student");
		String sapid = (String)request.getSession().getAttribute("userId");
		ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");

		List<ExamBookingTransactionBean> bookingsList = new ArrayList<>();

		ArrayList<String> subjects = (ArrayList<String>)request.getSession().getAttribute("subjects");

		ArrayList<String> selectedCenters = examBooking.getSelectedCenters();
		String prgrmStructApplicable = student.getPrgmStructApplicable();
		
		Map<String, PassFailBean> subjectPassFailBeanMap = getSubjectPassFailBeanMap(sapid);
		
		String examYear = examBooking.getYear();
		String examMonth = examBooking.getMonth();
		
		String trackId = sapid + System.currentTimeMillis() ;
		
		for (int i = 0; i < selectedCenters.size(); i++) {
			String subjectCenter = selectedCenters.get(i);
			
			String[] data = subjectCenter.split("\\|");

			String subject = data[0];
			String centerId = data[1];
			String examDate = data[2];
			String examStartTime = data[3];
			
			ExamBookingTransactionBean bean = new ExamBookingTransactionBean();
			
			PassFailBean failBean = subjectPassFailBeanMap.get(subject);

			bean.setSapid(sapid);
			bean.setSubject(subject);
			bean.setCenterId(centerId);
			bean.setExamDate(examDate);
			bean.setExamTime(examStartTime);
			String examEndTime = getEndTime(examStartTime);
			bean.setExamEndTime(examEndTime);
			bean.setYear(examYear);
			bean.setMonth(examMonth);
			bean.setProgram(failBean.getProgram());
			bean.setSem(failBean.getSem());
			bean.setTrackId(trackId);
			bean.setAmount("0");
			bean.setTranStatus(FEE_IN_ADMISSION);
			bean.setBooked("Y");
			bean.setPaymentMode("FREE");
			bean.setExamMode("Online");
			
			bookingsList.add(bean);
		}

		if(subjects.contains("Project")){
			ExamBookingTransactionBean bean = new ExamBookingTransactionBean();
			PassFailBean failBean = subjectPassFailBeanMap.get("Project");
			bean.setSapid(sapid);
			bean.setSubject("Project");
			bean.setCenterId("-1");
			bean.setYear(examYear);
			bean.setMonth(examMonth);
			bean.setProgram(failBean.getProgram());
			bean.setSem("4");
			bean.setExamDate(examYear + "/01/01");
			bean.setExamTime("00:00");
			bean.setExamEndTime("00:00");
			bean.setTrackId(trackId);
			bean.setAmount("0");
			bean.setTranStatus(FEE_IN_ADMISSION);
			bean.setBooked("Y");
			bean.setPaymentMode("FREE");
			bean.setExamMode("Online");
			
			bookingsList.add(bean);
		}

		
		boolean centerStillAvailable = checkIfCenterStillAvailable(selectedCenters, sapid);
		if(!centerStillAvailable){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Center you selected for one of the subject is no longer available. "
					+ "Please make fresh selection of exam centers");
			examBooking.setFreeApplicableSubjects(subjects);
			return selectResitExamCenterForFree(request, respnse, examBooking);
		}

		List<ExamBookingTransactionBean> examBookings = dao.insertSeatsForFreeSubjects(sapid, trackId, bookingsList,request);
		request.getSession().setAttribute("examBookings", examBookings);
		request.getSession().setAttribute("freeSeatBookingComplete", "true");
		request.setAttribute("success","true");
		request.setAttribute("successMessage",BOOKING_SUCCESS_MSG);
		request.getSession().setAttribute("examCenterIdNameMap", getExamCenterIdNameMap());
		MailSender mailSender = (MailSender)act.getBean("mailer");
		mailSender.sendBookingSummaryEmail(request, dao);
		
		return modelnView;
	}
	
	@RequestMapping(value = "/saveResitSeatsForReleasedSeatsNoCharges", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView saveResitSeatsForReleasedSeatsNoCharges(HttpServletRequest request, HttpServletResponse respnse,	@ModelAttribute ExamBookingBean examBooking, ModelMap model) throws ParseException {
		if(!checkSession(request, respnse)){
			return new ModelAndView("studentPortalRediret");
		}
		ModelAndView modelnView = new ModelAndView("resitExam/bookingStatus");
		String onlineSeatBookingComplete = (String)request.getSession().getAttribute("onlineSeatBookingComplete");
		if("true".equals(onlineSeatBookingComplete)){
			return modelnView;
		}

		String sapid = (String)request.getSession().getAttribute("userId");
		ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");

		List<ExamBookingTransactionBean> bookingsList = new ArrayList<>();

		ArrayList<String> subjects = (ArrayList<String>)request.getSession().getAttribute("subjects");

		ArrayList<String> selectedCenters = examBooking.getSelectedCenters();



		for (int i = 0; i < selectedCenters.size(); i++) {
			String subjectCenter = selectedCenters.get(i);
			
			String[] data = subjectCenter.split("\\|");

			String subject = data[0];
			String centerId = data[1];
			String examDate = data[2];
			String examStartTime = data[3];
			
			ExamBookingTransactionBean bean = new ExamBookingTransactionBean();

			bean.setSapid(sapid);
			bean.setSubject(subject);
			bean.setCenterId(centerId);
			
			bean.setExamDate(examDate);
			bean.setExamTime(examStartTime);
			String examEndTime = getEndTime(examStartTime);
			bean.setExamEndTime(examEndTime);

			bookingsList.add(bean);
		}


		boolean centerStillAvailable = checkIfCenterStillAvailable(selectedCenters, sapid);
		if(!centerStillAvailable){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Center you selected for one of the subject is no longer available. "
					+ "Please make fresh selection of exam centers");

			return selectResitExamCenterForOnline(request, respnse, examBooking);
		}

		List<ExamBookingTransactionBean> examBookings = resitExamBookingDAO.updateSeatsForRealeasedNoCharges(sapid, bookingsList);
		request.getSession().setAttribute("examBookings", examBookings);
		request.getSession().setAttribute("onlineSeatBookingComplete", "true");

		request.setAttribute("success","true");
		request.setAttribute("successMessage",BOOKING_SUCCESS_MSG);
		request.getSession().setAttribute("examCenterIdNameMap", getExamCenterIdNameMap());
		MailSender mailSender = (MailSender)act.getBean("mailer");
		mailSender.sendBookingSummaryEmail(request, dao);
		
		return modelnView;
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


	//=======This is GetTextBetweenTags function which return the value between two XML tags or two string =====
	public String GetTextBetweenTags(String InputText,String Tag1,String Tag2)
	{
		String Result;

		int index1 = InputText.indexOf(Tag1);
		int index2 = InputText.indexOf(Tag2);
		index1=index1+Tag1.length();
		Result=InputText.substring(index1, index2);
		return Result;

	}   

	public String GetSHA256(String str)
	{	
		StringBuffer strhash=new StringBuffer();
		try
		{
			//-------- Tampering code starts here -----
			String message = str;
			MessageDigest messagedigest = MessageDigest.getInstance("SHA-256");
			messagedigest.update(message.getBytes());
			byte digest[] = messagedigest.digest();
			strhash = new StringBuffer(digest.length*2);
			int length = digest.length;

			for (int n=0; n < length; n++)
			{
				int number = digest[n];
				if(number < 0)
				{			   
					number= number + 256;
				}
				//number = (number < 0) ? (number + 256) : number; // shift to positive range
				String str1="";
				if(Integer.toString(number,16).length()==1)
				{
					str1="0"+String.valueOf(Integer.toString(number,16));
				}
				else
				{
					str1=String.valueOf(Integer.toString(number,16));
				}
				strhash.append(str1);
			}		   
		}catch(Exception e)
		{
		} 	  
		return strhash.toString(); 
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
			double amountReceived = Double.parseDouble(request.getParameter("Amount"));

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
		if(trackId != null && trackId.equals(request.getParameter("MerchantRefNo"))){
			return true;
		}else{
			return false;
		}
	}

	private String md5(String str) throws Exception {
		MessageDigest m = MessageDigest.getInstance("MD5");

		byte[] data = str.getBytes();

		m.update(data,0,data.length);

		BigInteger i = new BigInteger(1,m.digest());

		String hash = String.format("%1$032X", i);

		return hash;
	}

	@RequestMapping(value = "/selectResitExamCenterForRelesedSubjects", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView selectResitExamCenterForRelesedSubjects(HttpServletRequest request, HttpServletResponse respnse, @ModelAttribute ExamBookingBean examBooking) {
		if(!checkSession(request, respnse)){
			return new ModelAndView("studentPortalRediret");
		}

		ArrayList<String> subjects = (ArrayList<String>)request.getSession().getAttribute("releasedSubjects");
		request.getSession().setAttribute("subjects", subjects);

		StudentBean student = (StudentBean)request.getSession().getAttribute("student");

		getAvailableCenters(request, subjects);
		request.setAttribute("hasReleasedSubjects", "true");

		ModelAndView modelnView = new ModelAndView("resitExam/selectExamCenter");
		modelnView.addObject("examBooking", examBooking);
		return modelnView;
	}
	
	@RequestMapping(value = "/selectResitExamCenterForRelesedNoChargeSubjects", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView selectResitExamCenterForRelesedNoChargeSubjects(HttpServletRequest request, HttpServletResponse respnse, @ModelAttribute ExamBookingBean examBooking) {
		if(!checkSession(request, respnse)){
			return new ModelAndView("studentPortalRediret");
		}

		ArrayList<String> subjects = (ArrayList<String>)request.getSession().getAttribute("releasedNoChargeSubjects");
		request.getSession().setAttribute("subjects", subjects);

		StudentBean student = (StudentBean)request.getSession().getAttribute("student");

		getAvailableCenters(request, subjects);
		request.setAttribute("hasReleasedNoChargeSubjects", "true");

		ModelAndView modelnView = new ModelAndView("resitExam/selectExamCenter");
		modelnView.addObject("examBooking", examBooking);
		return modelnView;
	}

	@RequestMapping(value = "/selectResitExamCenterForFree", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView selectResitExamCenterForFree(HttpServletRequest request, HttpServletResponse respnse, @ModelAttribute ExamBookingBean examBooking) {
		if(!checkSession(request, respnse)){
			return new ModelAndView("studentPortalRediret");
		}
		
		//ArrayList<String> subjects = (ArrayList<String>)request.getSession().getAttribute("freeApplicableSubjects");
		ArrayList<String> subjects = examBooking.getFreeApplicableSubjects();
		request.getSession().setAttribute("subjects", subjects);

		getAvailableCenters(request, subjects);
		request.setAttribute("hasFreeSubjects", "true");

		ModelAndView modelnView = new ModelAndView("resitExam/selectExamCenter");
		modelnView.addObject("examBooking", examBooking);
		return modelnView;
	}


	
	@RequestMapping(value = "/selectSubjects", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView selectSubjects(HttpServletRequest request, HttpServletResponse respnse, @ModelAttribute ExamBookingBean examBooking) {
		if(!checkSession(request, respnse)){
			return new ModelAndView("studentPortalRediret");
		}

		ModelAndView modelnView = new ModelAndView("selectPaymentMode");
		request.getSession().setAttribute("ddSeatBookingComplete", null);
		request.getSession().setAttribute("onlineSeatBookingComplete", null);
		request.getSession().setAttribute("freeSeatBookingComplete", null);

		try{
			ArrayList<String> subjects = examBooking.getApplicableSubjects();
			//Remove duplicates using Set
			Set<String> set = new HashSet<String>(subjects);
			subjects = new ArrayList<String>(set);

			request.getSession().setAttribute("subjects", subjects);

			StudentBean student = (StudentBean)request.getSession().getAttribute("student");
			String studentProgramStructure = student.getPrgmStructApplicable();

			ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
			List<TimetableBean> timeTableList = dao.getTimetableListForGivenSubjects(subjects, student.getProgram(), studentProgramStructure);
			request.getSession().setAttribute("timeTableList", timeTableList);



			ExamCenterBean examCenter = new ExamCenterBean();
			examCenter.setPaymentMode("Online");
			modelnView.addObject("examCenter", examCenter);

			modelnView.addObject("subjects", subjects);
			modelnView.addObject("noOfSubjects", subjects.size()+"");
			modelnView.addObject("examFeesPerSubject", examFeesPerSubject);
			modelnView.addObject("totalFees", (examFeesPerSubject * subjects.size()) + "");

		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in getting subjects.");
		}
		return modelnView;
	}


	@RequestMapping(value = "/selectPaymentMode", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView selectPaymentMode(HttpServletRequest request, HttpServletResponse respnse) {
		if(!checkSession(request, respnse)){
			return new ModelAndView("studentPortalRediret");
		}
		try{
			ArrayList<String> subjects = (ArrayList<String>)request.getSession().getAttribute("subjects");
			ExamBookingBean examBooking = new ExamBookingBean();
			examBooking.setApplicableSubjects(subjects);
			return selectSubjects(request, respnse, examBooking);

		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in getting subjects.");
		}
		return selectResitSubjectsForm(request, respnse);
	}

	@RequestMapping(value = "/selectExamCenter", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView selectExamCenter(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		if(!checkSession(request, respnse)){
			return new ModelAndView("studentPortalRediret");
		}
		ArrayList<String> subjects = (ArrayList<String>)request.getSession().getAttribute("subjects");

		String paymentMode = request.getParameter("paymentMode");
		if("DD".equalsIgnoreCase(paymentMode)){
			ModelAndView modelnView = new ModelAndView("ddDetails");
			DDDetails ddDetails = new DDDetails();
			ddDetails.setAmount(examFeesPerSubject * subjects.size()+"");
			modelnView.addObject("ddDetails", ddDetails);
			return modelnView;
		}else if("Online".equalsIgnoreCase(paymentMode)){
			List<TimetableBean> timeTableList = (List<TimetableBean>)request.getSession().getAttribute("timeTableList");
			StudentBean student = (StudentBean)request.getSession().getAttribute("student");
			getAvailableCenters(request, timeTableList, student);
		}

		ExamBookingBean examBooking = new ExamBookingBean();

		ModelAndView modelnView = new ModelAndView("selectExamCenter");
		modelnView.addObject("examBooking", examBooking);


		return modelnView;
	}


	@RequestMapping(value = "/selectExamCenterSinceSeatNotAvailable", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView selectExamCenterSinceSeatNotAvailable(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		if(!checkSession(request, respnse)){
			return new ModelAndView("studentPortalRediret");
		}
		ArrayList<String> subjects = (ArrayList<String>)request.getSession().getAttribute("subjects");

		String paymentMode = request.getParameter("paymentMode");
		if("DD".equalsIgnoreCase(paymentMode)){
			ModelAndView modelnView = new ModelAndView("ddDetails");
			DDDetails ddDetails = new DDDetails();
			ddDetails.setAmount(examFeesPerSubject * subjects.size()+"");
			modelnView.addObject("ddDetails", ddDetails);
			return modelnView;
		}else if("Online".equalsIgnoreCase(paymentMode)){
			List<TimetableBean> timeTableList = (List<TimetableBean>)request.getSession().getAttribute("timeTableList");
			StudentBean student = (StudentBean)request.getSession().getAttribute("student");
			getAvailableCenters(request, timeTableList, student);
		}

		ExamBookingBean examBooking = new ExamBookingBean();

		ModelAndView modelnView = new ModelAndView("selectExamCenter");
		modelnView.addObject("examBooking", examBooking);


		return modelnView;
	}




	private void addProjectIfNeeded(List<TimetableBean> timeTableList,
			ArrayList<String> subjects) {
		if(subjects.contains("Project")){
			TimetableBean bean = new TimetableBean();
			bean.setSubject("Project");
			bean.setSem("4");
			bean.setDate("NA");
			bean.setStartTime("NA");
			bean.setEndTime("NA");
			timeTableList.add(bean);
		}

	}




	@RequestMapping(value = "/pay", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView pay(HttpServletRequest request, HttpServletResponse respnse, ModelMap model) {
		if(!checkSession(request, respnse)){
			return new ModelAndView("studentPortalRediret");
		}
		return new ModelAndView("pay");
	}


	private Map<String, TimetableBean> getSubjectTimetableMap(List<TimetableBean> timeTableList) {
		HashMap<String, TimetableBean> subjectTimetableMap = new HashMap<>();
		for (int i = 0; i < timeTableList.size(); i++) {
			TimetableBean bean = timeTableList.get(i);
			String subject = bean.getSubject();
			subjectTimetableMap.put(subject, bean);
		}

		return subjectTimetableMap;
	}








	@RequestMapping(value = "/downloadBookingPdf", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadBookingPdf(HttpServletRequest request, HttpServletResponse respnse, ModelMap model) {
		ModelAndView modelnView = new ModelAndView("bookingStatus");
		respnse.setContentType("application/pdf");
		return modelnView;
	}
















	@RequestMapping(value = "/searchDDsToApproveForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView searchDDsToApproveForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		ModelAndView modelnView = new ModelAndView("searchDD");
		ExamBookingTransactionBean transaction = new ExamBookingTransactionBean();
		m.addAttribute("transaction", transaction);
		m.addAttribute("yearList", yearList);
		return modelnView;
	}



	@RequestMapping(value = "/searchDD",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView searchDD(HttpServletRequest request, HttpServletResponse response, @ModelAttribute ExamBookingTransactionBean transaction){
		ModelAndView modelnView = new ModelAndView("searchDD");
		ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
		request.getSession().setAttribute("transaction", transaction);

		Page<ExamBookingTransactionBean> page = dao.getDDsPage(1, Integer.MAX_VALUE, transaction);
		List<ExamBookingTransactionBean> ddsList = page.getPageItems();

		modelnView.addObject("page", page);
		modelnView.addObject("rowCount", page.getRowCount());
		modelnView.addObject("transaction", transaction);

		modelnView.addObject("yearList", yearList);
		if(ddsList == null || ddsList.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No DD Details found.");
		}

		modelnView.addObject("ddsList", ddsList);
		return modelnView;
	}

	@RequestMapping(value = "/searchExamBookingTOChangeCenterForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView searchExamBookingTOChangeCenterForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		ModelAndView modelnView = new ModelAndView("searchExamBooking");
		ExamBookingTransactionBean examBooking = new ExamBookingTransactionBean();
		m.addAttribute("examBooking", examBooking);
		m.addAttribute("yearList", yearList);
		m.addAttribute("subjectList", subjectList);
		return modelnView;
	}

	@RequestMapping(value = "/searchExamBookingTOChangeCenter",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView searchExamBookingTOChangeCenter(HttpServletRequest request, HttpServletResponse response, @ModelAttribute ExamBookingTransactionBean examBooking){
		ModelAndView modelnView = new ModelAndView("searchExamBooking");
		ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
		request.getSession().setAttribute("examBooking", examBooking);


		List<ExamBookingTransactionBean> releasedBookingsList = dao.getReleasedExamBookingsForStudent(examBooking);

		modelnView.addObject("rowCount", releasedBookingsList.size());
		modelnView.addObject("releasedBookingsList", releasedBookingsList);
		modelnView.addObject("examBooking", examBooking);

		modelnView.addObject("yearList", yearList);
		if(releasedBookingsList == null || releasedBookingsList.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No Released Bookings found.");
		}

		StudentBean student = dao.getSingleStudentsData(examBooking.getSapid());
		ArrayList<String> subjects = new ArrayList<>();
		for (ExamBookingTransactionBean bean : releasedBookingsList) {
			subjects.add(bean.getSubject());
		}
		List<TimetableBean> timeTableList = dao.getTimetableListForGivenSubjects(subjects, student.getProgram(), student.getPrgmStructApplicable());
		getAvailableCenters(request, timeTableList, student);
		modelnView.addObject("student", student);
		return modelnView;
	}

	@RequestMapping(value = "/changeCenterForStudents",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView changeCenterForStudents(HttpServletRequest request, HttpServletResponse response, @ModelAttribute ExamBookingTransactionBean examBooking){
		ModelAndView modelnView = new ModelAndView("searchExamBooking");
		ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
		request.getSession().setAttribute("examBooking", examBooking);

		List<ExamBookingTransactionBean> bookingsList = new ArrayList<>();
		ArrayList<String> selectedCenters = examBooking.getSelectedCenters();

		for (int i = 0; i < selectedCenters.size(); i++) {
			String subjectCenter = selectedCenters.get(i);
			String subject = subjectCenter.substring(0,subjectCenter.indexOf("|"));
			String centerId = subjectCenter.substring(subjectCenter.indexOf("|")+1, subjectCenter.length() );
			ExamBookingTransactionBean bean = new ExamBookingTransactionBean();
			bean.setYear(examBooking.getYear());
			bean.setMonth(examBooking.getMonth());
			bean.setSapid(examBooking.getSapid());
			bean.setSubject(subject);
			bean.setCenterId(centerId);

			bookingsList.add(bean);
		}

		List<ExamBookingTransactionBean> completeBookings = dao.updateCenterForReleasedSeatsForStudents(examBooking.getSapid(), 
				examBooking.getYear(), examBooking.getMonth(), bookingsList);

		setSuccess(request, "Exam Center Changed Successfully");
		return searchExamBookingTOChangeCenter(request, response, examBooking);
	}

	@RequestMapping(value = "/approveDD",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView approveDD(HttpServletRequest request, HttpServletResponse response){
		ModelAndView modelnView = new ModelAndView("searchDD");

		String sapid = request.getParameter("sapid");
		String ddno = request.getParameter("ddno");
		String year = request.getParameter("year");
		String month = request.getParameter("month");
		String email = request.getParameter("email");
		String trackId = request.getParameter("trackId");

		ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
		ExamBookingTransactionBean transaction = (ExamBookingTransactionBean)request.getSession().getAttribute("transaction");

		dao.approveDD(sapid, ddno, year, month, trackId);

		Page<ExamBookingTransactionBean> page = dao.getDDsPage(1, Integer.MAX_VALUE, transaction);
		List<ExamBookingTransactionBean> ddsList = page.getPageItems();

		modelnView.addObject("page", page);
		modelnView.addObject("rowCount", page.getRowCount());
		modelnView.addObject("transaction", transaction);

		modelnView.addObject("yearList", yearList);
		request.setAttribute("success","true");
		request.setAttribute("successMessage","DD Approved Successfully");

		modelnView.addObject("ddsList", ddsList);

		MailSender mailSender = (MailSender)act.getBean("mailer");
		mailSender.sendDDStatusChangeEmail(email, ddno, "APPROVE", null, sapid);

		//Code to send email here
		return modelnView;
	}

	@RequestMapping(value = "/rejectDD",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView rejectDD(HttpServletRequest request, HttpServletResponse response){
		ModelAndView modelnView = new ModelAndView("searchDD");

		String sapid = request.getParameter("sapid");
		String ddno = request.getParameter("ddno");
		String year = request.getParameter("year");
		String month = request.getParameter("month");
		String reason = request.getParameter("reason");
		String email = request.getParameter("email");
		String trackId = request.getParameter("trackId");

		ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
		ExamBookingTransactionBean transaction = (ExamBookingTransactionBean)request.getSession().getAttribute("transaction");

		dao.rejectDD(sapid, ddno, year, month, reason, trackId);

		Page<ExamBookingTransactionBean> page = dao.getDDsPage(1, Integer.MAX_VALUE, transaction);
		List<ExamBookingTransactionBean> ddsList = page.getPageItems();

		modelnView.addObject("page", page);
		modelnView.addObject("rowCount", page.getRowCount());
		modelnView.addObject("transaction", transaction);

		modelnView.addObject("yearList", yearList);
		request.setAttribute("success","true");
		request.setAttribute("successMessage","DD Rejected Successfully");

		modelnView.addObject("ddsList", ddsList);
		MailSender mailSender = (MailSender)act.getBean("mailer");
		mailSender.sendDDStatusChangeEmail(email, ddno, "REJECT", reason, sapid);
		//Code to send email here

		return modelnView;
	}



	@RequestMapping(value = "/queryTransactionStatusForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView queryTransactionStatusForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		ModelAndView modelnView = new ModelAndView("transactionStatus");
		return modelnView;
	}


	@RequestMapping(value = "/queryTransactionStatus",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView queryTransactionStatus(HttpServletRequest request, HttpServletResponse response){
		ModelAndView modelnView = new ModelAndView("transactionStatus");
		List<ExamBookingTransactionBean> transactionResponseList = new ArrayList<>();
		HashMap<String, ExamBookingTransactionBean> trackIdTransactionMap = new HashMap<>();
		try{
			String sapid = request.getParameter("sapid");
			ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
			ArrayList<ExamBookingTransactionBean> unSuccessfulExamBookings = dao.getUnSuccessfulExamBookings(sapid);
			for (int i = 0; i < unSuccessfulExamBookings.size(); i++) {
				ExamBookingTransactionBean bean = unSuccessfulExamBookings.get(i);
				String trackId = bean.getTrackId();
				XMLParser parser = new XMLParser();
				String xmlResponse = parser.queryTransactionStatus(trackId, ACCOUNT_ID, SECURE_SECRET);
				parser.parseResponse(xmlResponse, bean);
				bean.setTrackId(trackId);
				bean.setSapid(sapid);
				transactionResponseList.add(bean);
				trackIdTransactionMap.put(trackId, bean);
			}
			request.getSession().setAttribute("trackIdTransactionMap", trackIdTransactionMap);
			request.getSession().setAttribute("sapIdForApprovedTransaction", sapid);
			request.setAttribute("transactionResponseList", transactionResponseList);

			if(trackIdTransactionMap.size() == 0){
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No Unsuccessful Transactions found for "+sapid);
			}

		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in retriving details. Please try again");
		}
		return modelnView;
	}

	@RequestMapping(value = "/approveTransactionsForTrackId",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView approveTransactionsForTrackId(HttpServletRequest request, HttpServletResponse response){
		ModelAndView modelnView = new ModelAndView("transactionStatus");

		HashMap<String, ExamBookingTransactionBean> trackIdTransactionMap = (HashMap<String, ExamBookingTransactionBean>)request.getSession().getAttribute("trackIdTransactionMap");
		String sapIdForApprovedTransaction = (String)request.getSession().getAttribute("sapIdForApprovedTransaction");
		try{
			String trackId = request.getParameter("trackId");
			ExamBookingTransactionBean bean = trackIdTransactionMap.get(trackId);
			ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
			int noOfRowsUpdated = dao.approveOnlineTransactions(trackId, bean);
			request.setAttribute("success","true");
			request.setAttribute("successMessage","Transaction approved successfully for "+noOfRowsUpdated+" subjects. Please ask student to choose center and book seat");

			StudentBean student = dao.getSingleStudentsData(sapIdForApprovedTransaction);

			MailSender mailSender = (MailSender)act.getBean("mailer");
			mailSender.sendTransactionApproveEmail(student, bean);

			request.getSession().setAttribute("sapIdForApprovedTransaction", null);
		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in approving transaction");
		}
		return modelnView;
	}


	@RequestMapping(value = "/queryFeesPaidForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView queryFeesPaidForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		ModelAndView modelnView = new ModelAndView("refund");
		return modelnView;
	}


	@RequestMapping(value = "/queryFeesPaid",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView queryFeesPaid(HttpServletRequest request, HttpServletResponse response){
		ModelAndView modelnView = new ModelAndView("refund");
		List<ExamBookingTransactionBean> transactionResponseList = new ArrayList<>();
		HashMap<String, ExamBookingTransactionBean> trackIdTransactionMap = new HashMap<>();
		try{
			String sapid = request.getParameter("sapid");
			ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
			ArrayList<ExamBookingTransactionBean> unSuccessfulExamBookings = dao.getUnSuccessfulExamBookings(sapid);
			for (int i = 0; i < unSuccessfulExamBookings.size(); i++) {
				ExamBookingTransactionBean bean = unSuccessfulExamBookings.get(i);
				String trackId = bean.getTrackId();
				XMLParser parser = new XMLParser();
				String xmlResponse = parser.queryTransactionStatus(trackId, ACCOUNT_ID, SECURE_SECRET);
				parser.parseResponse(xmlResponse, bean);
				bean.setTrackId(trackId);
				bean.setSapid(sapid);
				transactionResponseList.add(bean);
				trackIdTransactionMap.put(trackId, bean);
			}
			request.getSession().setAttribute("trackIdTransactionMap", trackIdTransactionMap);
			request.getSession().setAttribute("sapIdForRefund", sapid);
			request.setAttribute("transactionResponseList", transactionResponseList);

			if(trackIdTransactionMap.size() == 0){
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No Unsuccessful Transactions found for "+sapid);
			}

		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in retriving details. Please try again");
		}
		return modelnView;
	}


	@RequestMapping(value = "/refundExamFees",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView refundExamFees(HttpServletRequest request, HttpServletResponse response){
		ModelAndView modelnView = new ModelAndView("refund");

		HashMap<String, ExamBookingTransactionBean> trackIdTransactionMap = (HashMap<String, ExamBookingTransactionBean>)request.getSession().getAttribute("trackIdTransactionMap");
		String sapIdForRefund = (String)request.getSession().getAttribute("sapIdForRefund");
		try{
			String trackId = request.getParameter("trackId");
			ExamBookingTransactionBean bean = trackIdTransactionMap.get(trackId);
			ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
			int noOfRowsUpdated = dao.updateRefundTransactions(trackId, bean);
			XMLParser parser = new XMLParser();
			String xmlResponse = parser.initiateRefund(trackId, ACCOUNT_ID, SECURE_SECRET, bean);
			String transactionType = parser.getTransactionTypeFromResponse(xmlResponse, bean);

			if(transactionType != null &&  "Refunded".equals(transactionType)){
				request.setAttribute("success","true");
				request.setAttribute("successMessage","Refund Initiated Successfully.");
			}else{
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", transactionType);
			}


			StudentBean student = dao.getSingleStudentsData(sapIdForRefund);

			MailSender mailSender = (MailSender)act.getBean("mailer");
			mailSender.sendRefundEmail(student, bean);

			request.getSession().setAttribute("sapIdForRefund", null);
		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in initiating Refund");
		}
		return modelnView;
	}


	@RequestMapping(value = "/uploadExamFeeExemptForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String uploadExamFeeExemptForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {

		FileBean fileBean = new FileBean();
		m.addAttribute("fileBean",fileBean);
		m.addAttribute("yearList", yearList);

		return "uploadExamFeeExempt";
	}



	@RequestMapping(value = "/uploadExamFeeExempt", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView uploadAssignmentStatus(FileBean fileBean, BindingResult result,HttpServletRequest request, Model m){
		ModelAndView modelnView = new ModelAndView("uploadExamFeeExempt");
		try{

			String userId = (String)request.getSession().getAttribute("userId");
			ExcelHelper excelHelper = new ExcelHelper();
			ArrayList<List> resultList = excelHelper.readExamFeeExemptExcel(fileBean, userId);

			List<StudentMarksBean> examFeeExemptList = (ArrayList<StudentMarksBean>)resultList.get(0);
			List<StudentMarksBean> errorBeanList = (ArrayList<StudentMarksBean>)resultList.get(1);

			fileBean = new FileBean();
			m.addAttribute("fileBean",fileBean);
			m.addAttribute("yearList", yearList);

			if(errorBeanList.size() > 0){
				request.setAttribute("errorBeanList", errorBeanList);
				return modelnView;
			}



			ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");

			ArrayList<String> errorList = dao.batchUpdateExemptFeeList(examFeeExemptList);

			if(errorList.size() == 0){
				request.setAttribute("success","true");
				request.setAttribute("successMessage",examFeeExemptList.size() +" rows out of "+ examFeeExemptList.size()+" inserted successfully.");
			}else{
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", errorList.size() + " records were NOT inserted. Please see row number of rows not inserted. Error row numbers "+errorList);
			}

		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in inserting Exam Fee Exempt Status rows.");

		}

		return modelnView;
	}



	@RequestMapping(value = "/takeDemoTest", method = RequestMethod.GET)
	public ModelAndView takeDemoTest(HttpServletRequest request, HttpServletResponse respnse) {

		if(!checkSession(request, respnse)){
			return new ModelAndView("studentPortalRediret");
		}

		ModelAndView modelnView = new ModelAndView("demoTestRedirect");

		String userId = (String)request.getSession().getAttribute("userId");
		modelnView.addObject("password", "password" );
		modelnView.addObject("userId", userId );

		return modelnView;
	}

}
*/