package com.nmims.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.google.common.base.Throwables;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.nmims.beans.ExamAdhocPaymentBean;
import com.nmims.beans.AssignmentFileBean;
import com.nmims.beans.BookedResponse;
import com.nmims.beans.DDDetails;
import com.nmims.beans.ExamAdhocPaymentBean;
import com.nmims.beans.ExamBookingExamBean;
import com.nmims.beans.ExamBookingRefundRequestReportBean;
import com.nmims.beans.ExamBookingStudentCycleSubjectConfig;
import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.beans.ExamCenterBean;
import com.nmims.beans.ExamConflictTransactionBean;
import com.nmims.beans.ExamFeeExemptSubjectBean;
import com.nmims.beans.ExamGoToGatewayBeanAPIResponse;
import com.nmims.beans.ExamRegistrationBeanAPIRequest;
import com.nmims.beans.FileBean;
import com.nmims.beans.PGReexamEligibleStudentsBean;
import com.nmims.beans.Page;
import com.nmims.beans.PassFailExamBean;
import com.nmims.beans.ProgramSubjectMappingExamBean;
import com.nmims.beans.RefundRequestBean;
import com.nmims.beans.RequestFormBean;
import com.nmims.beans.ResponseBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.beans.StudentSubjectMappingConfigBean;
import com.nmims.beans.TimetableBean;
import com.nmims.beans.TransactionsBean;
import com.nmims.daos.DashboardDAO;
import com.nmims.daos.ExamBookingDAO;
import com.nmims.daos.ExamCenterDAO;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.helpers.ExamBookingHelper;
import com.nmims.helpers.ExamBookingPDFCreator;
import com.nmims.helpers.ExcelHelper;
import com.nmims.helpers.HallTicketPDFCreator;
import com.nmims.helpers.MailSender;
import com.nmims.helpers.PaymentHelper;
import com.nmims.helpers.XMLParser;
import com.nmims.listeners.ExamBookingScheduler;
import com.nmims.listeners.TEELinkScheduler;
import com.nmims.services.ExamBookingEligibilityService;
import com.nmims.services.ExamBookingStudentService;
import com.nmims.services.ProjectStudentEligibilityService;
import com.nmims.services.RescheduledCancelledSlotChangeReportService;
import com.nmims.services.StudentService;
import com.nmims.stratergies.ExamRegistrationRealTimeStrategy;
import com.nmims.util.RequestResponseUtils;

@Controller
public class ExamBookingController extends BaseController{

	@Autowired(required=false)
	ApplicationContext act;

	@Autowired
	PaymentHelper paymentHelper;
	
	@Autowired
	StudentService studentService;

	@Autowired
	ExamBookingStudentService examBookingStudentService;
	
	@Autowired
	ExamBookingEligibilityService examBookingEligibilityService;

	@Autowired
	ProjectStudentEligibilityService eligibilityService;
	
	@Autowired
	private RescheduledCancelledSlotChangeReportService exambookingAuditService;
	
	@Autowired
	private  ExamRegistrationRealTimeStrategy examRegistrationRealTimeStrategy;
	
	private ArrayList<String> programList = null;
	private ArrayList<String> subjectList = null;
	private ArrayList<String> masterKeyList = null; 

	private int examFeesPerSubjectFirstAttempt = 600;
	private int examFeesPerSubjectResitAttempt = 600;
	private final static String changeSlotFees = "500";
	private final static String changeSlotFeesAfterRegistrationWindowIsClosed = "1000";
	private final static String NUMBER_ONE = "1";
	private final  int examFeesPerSubject = 600;
	//int totalExamFees = 0;

	private int totalFeesForRebooking =500;
	private int totalFeesForRebookingWhenExamIsNotLive = 1000;
	private final String ONLINE_PAYMENT_INITIATED = "Online Payment Initiated"; 
	private final String ONLINE_PAYMENT_MANUALLY_APPROVED = "Online Payment Manually Approved"; 
	private final String ONLINE_PAYMENT_SUCCESS = "Online Payment Successfull";
	private final String ONLINE_PAYMENT_FAILED = "Transaction Failed"; 
	private final static String PAYMENT_SUCCESSFUL = "Payment Successfull";
	private final static String PAYMENT_FAILED = "Payment Failed";
	
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
			+ "Please click <a href=\"selectSubjectsForm\"> here </a> to verify subjects pending to be booked.";
	public SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");

	@Value( "${SECURE_SECRET}" )
	private String SECURE_SECRET="214cb32eed243b72501f3edc818d9737"; // secret key;
	@Value( "${ACCOUNT_ID}" )
	private String ACCOUNT_ID;
	@Value( "${V3URL}" )
	private String V3URL;
	@Value( "${RETURN_URL}" )
	private String RETURN_URL;
	
	@Value( "${MRETURN_URL}" )
	private String MRETURN_URL;

	@Value("${SERVICE_TAX_RULE}")
	private String SERVICE_TAX_RULE;

	@Value("${MARKSHEETS_PATH}")
	private String MARKSHEETS_PATH;

	@Value("${FEE_RECEIPT_PATH}")
	private String FEE_RECEIPT_PATH;

	@Value("${SERVER_PATH}")
	private String SERVER_PATH;

	@Value("${STUDENT_PHOTOS_PATH}")
	private String STUDENT_PHOTOS_PATH;
	@Value("${HALLTICKET_PATH}")
	private String HALLTICKET_PATH;

	@Value("#{'${CORPORATE_CENTERS}'.split(',')}")
	private List<String> corporateCenterList;
	
	@Value("#{'${ACAD_YEAR_LIST}'.split(',')}") 
	private List<String> ACAD_YEAR_LIST; 
	
	@Value( "${ENVIRONMENT}" )
	private String ENVIRONMENT;
	
	@Value("${SERVER}")
	private String SERVER;
	
	@Autowired
	HallTicketPDFCreator hallTicketCreator;

	@Autowired
	ExamBookingPDFCreator examFeeReceiptCreator;

	@Autowired
	ExamBookingHelper examBookingHelper;
	
	@Autowired
	TEELinkScheduler scheduler;
	
	@Autowired
	private ExamCenterDAO examCenterDAO;

	private static final Logger logger = LoggerFactory.getLogger("examBookingPayments");
	
	private static final Logger webhookLogger = LoggerFactory.getLogger("webhook_payments");
	
	public static final Logger ebAuditLogger = LoggerFactory.getLogger("examBookingAudit");
	
	public static final Logger examRegisterlogger = LoggerFactory.getLogger("examRegisterPG");

	private ArrayList<String> yearList = new ArrayList<String>(Arrays.asList( 
			"2014","2015","2016","2017","2018","2019","2020","2021","2022")); 
	private List<String> demoSapids = Arrays.asList( "77777777770","77777777771","77777777772","77777777773",
										"77777777774","77777777775","77777777776","77777777132",
										"77777777778", "77777788888", "77777799999", "77777777159");
	
	private ArrayList<ProgramSubjectMappingExamBean> programSubjectMappingList = null;
	private HashMap<String, String> examCenterIdNameMap = null;
	private HashMap<String,String> examCenterIdNameHashMap = null;
	private HashMap<String,String> programCodeNameMap = null;
	HashMap<String,String> corporateCenterUserMapping = null;
	HashMap<String, ExamCenterBean> examCenterIdCenterMap = null;
	private String mostRecentTimetablePeriod = null;
	HashMap<String, ArrayList<String>>	studentFreeSubjectsMap = null;
	//HashMap<String, ProgramSubjectMappingBean> subjectProgramSemMap = new HashMap<String, ProgramSubjectMappingBean>();

	private ArrayList<StudentExamBean> exemptStudentList = null;
	private boolean refreshCache = false;

	private List<ExamCenterBean> allExamCenterList = null;

	public HashMap<String, ArrayList<String>> getStudentFreeSubjectsMap(){
		if(this.studentFreeSubjectsMap == null || this.studentFreeSubjectsMap.size() == 0 || refreshCache){
			ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
			this.studentFreeSubjectsMap = eDao.getStudentFreeSubjectsMap();
			refreshCache = false;
		}
		return this.studentFreeSubjectsMap;
	}
	/*public String getMostRecentTimetablePeriod(){
		if(this.mostRecentTimetablePeriod == null || refreshCache){
			StudentMarksDAO sDao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			this.mostRecentTimetablePeriod = sDao.getMostRecentTimeTablePeriod();
			refreshCache = false;
		}

		return this.mostRecentTimetablePeriod;
	}*/

	public ArrayList<ProgramSubjectMappingExamBean> getProgramSubjectMappingList(){
		if(this.programSubjectMappingList == null || this.programSubjectMappingList.size() == 0 || refreshCache){
			ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
			this.programSubjectMappingList = eDao.getProgramSubjectMappingList();
			refreshCache = false;
		}
		return programSubjectMappingList;
	}
	public HashMap<String, String> getProgramMap(){
		if(this.programCodeNameMap == null || this.programCodeNameMap.size() == 0 || refreshCache){
			StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			this.programCodeNameMap = dao.getProgramDetails();
			refreshCache = false;
		}
		return programCodeNameMap;
	}
	
	
                       
	public HashMap<String, String> getExamCenterIdNameHashMap(){
		if(this.examCenterIdNameHashMap == null || this.examCenterIdNameHashMap.size() == 0 || refreshCache){
			ExamCenterDAO dao = (ExamCenterDAO)act.getBean("examCenterDAO");
			this.examCenterIdNameHashMap = dao.getExamCenterIdNameMap();
			refreshCache = false;
		}

		return this.examCenterIdNameHashMap;
	}

	public HashMap<String, String> getCorporateCenterUserMapping(){
		if(this.corporateCenterUserMapping == null || this.corporateCenterUserMapping.size() == 0 || refreshCache){
			ExamCenterDAO dao = (ExamCenterDAO)act.getBean("examCenterDAO");
			this.corporateCenterUserMapping = dao.getCorporateCenterUserMapping();
			refreshCache = false;
		}

		return this.corporateCenterUserMapping;
	}

	public ArrayList<StudentExamBean> getExemptStudentList(){
		if(this.exemptStudentList == null || this.exemptStudentList.size() == 0 || refreshCache){
			ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
			this.exemptStudentList = eDao.getExemptStudentList();
			refreshCache = false;
		}
		return exemptStudentList;
	}
	/*public HashMap<String, ExamCenterBean> getExamCenterCenterDetailsMap(boolean isCorporate){
		//if(this.examCenterIdCenterMap == null || this.examCenterIdCenterMap.size() == 0){
		ExamCenterDAO dao = (ExamCenterDAO)act.getBean("examCenterDAO");
		this.examCenterIdCenterMap = dao.getExamCenterCenterDetailsMap(isCorporate);
		//}
		return examCenterIdCenterMap;
	}*/

	public HashMap<String, String> getExamCenterIdNameMap(){
		if(this.examCenterIdNameMap == null || examCenterIdNameMap.size() == 0 || refreshCache){
			ExamCenterDAO dao = (ExamCenterDAO)act.getBean("examCenterDAO");
			this.examCenterIdNameMap = dao.getExamCenterIdNameMap();
			refreshCache = false;
		}
		return examCenterIdNameMap;
	}



	public ArrayList<String> getSubjectList(){
		if(this.subjectList == null || refreshCache){
			StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			this.subjectList = dao.getAllSubjects();
			refreshCache = false;
		}
		return subjectList;
	}

	public ArrayList<String> getProgramList(){
		if(this.programList == null || refreshCache){
			StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			this.programList = dao.getAllPrograms();
			refreshCache = false;
		}
		return programList;
	}
	
	public ArrayList<String> getMasterKeyList(){
		if(this.masterKeyList == null || refreshCache){
			DashboardDAO dao = (DashboardDAO)act.getBean("dashboardDAO");
			this.masterKeyList = dao.getMasterKeysList();
			refreshCache = false;
		}
		return masterKeyList;
	}
	
	public List<ExamCenterBean> getAllExamCenters(){
		if(this.allExamCenterList == null || refreshCache){
			ExamCenterDAO dao = (ExamCenterDAO)act.getBean("examCenterDAO");
			this.allExamCenterList = dao.getAllExamCenters();
			refreshCache = false;
		}
		return allExamCenterList;
	}

	public void refreshCache(){
		refreshCache = true;
		getStudentFreeSubjectsMap();
		
		refreshCache = true;
		getProgramSubjectMappingList();
		
		refreshCache = true;
		getProgramMap();
		
		refreshCache = true;
		getExamCenterIdNameHashMap();
		
		refreshCache = true;
		getCorporateCenterUserMapping();
		
		refreshCache = true;
		getExemptStudentList();
		
		refreshCache = true;
		getExamCenterIdNameMap();
		
		refreshCache = true;
		getSubjectList();
		
		refreshCache = true;
		getProgramList();
		
	}


	private boolean checkIfExamRegistrationLive(String sapid, String sapIdEncrypted) {
		// Enter empty encrypted sapid field for mobile users
		return examBookingStudentService.isExamRegistraionLive(sapid, sapIdEncrypted);
	}
	
//	@RequestMapping(value = "m/generateStudentMappingsForCycle", method = {RequestMethod.GET})
//	public ResponseEntity<String> generateStudentMappingsForCycle(HttpServletRequest request, HttpServletResponse response) {
//		
//		try {
//			long startTime = Calendar.getInstance().getTimeInMillis();
//			List<ExamBookingStudentCycleSubjectConfig> errors = examBookingEligibilityService.generateStudentSubjectMappingsForUnmappedStudents("2022", "Dec");
//			return ResponseEntity.ok("OK. Time taken : " + (Calendar.getInstance().getTimeInMillis() - startTime)
//					+ " Error list (Size : " + errors.size() +  " ) - " + new Gson().toJson(errors) 
//			);
//		} catch (Exception e) {
//			
//			return ResponseEntity.ok(e.getMessage());
//		}
//		
//	}
	
	@RequestMapping(value = "m/generateStudentMappingsForCycleInPhase", method = {RequestMethod.GET})
	public ResponseEntity<String> generateStudentMappingsForCycle(HttpServletRequest request, HttpServletResponse response) {
		
		if(!"tomcat6".equalsIgnoreCase(SERVER)){
			return new ResponseEntity<>(" Not populating data since server isn't tomcat6 it is : " +  SERVER,HttpStatus.OK);
		}
		
		try {
			
			final String defaultBookingStartTime = "2023-05-15 00:00:00"; 
			final String defaultBookingEndTime = "2023-05-27 23:59:59"; 
			
			StudentSubjectMappingConfigBean configBean = new StudentSubjectMappingConfigBean();
			configBean.setDefaultBookingStartTime(defaultBookingStartTime);
			configBean.setDefaultBookingEndTime(defaultBookingEndTime);
			configBean.setExamYear("2023");
			configBean.setExamMonth("Jun");
			configBean.getPhaseList().add(configBean. new StudentSubjectMappingPhaseBean("2023","Jan",
					"2023-05-31 00:00:00","2023-06-12 23:59:59"));
			
			long startTime = Calendar.getInstance().getTimeInMillis();
			
			List<ExamBookingStudentCycleSubjectConfig> errors = examBookingEligibilityService
					.generateStudentSubjectMappingsForUnmappedStudents(configBean);
			
			return ResponseEntity.ok("OK. Time taken : " + (Calendar.getInstance().getTimeInMillis() - startTime)
					+ " Error list (Size : " + errors.size() +  " ) - " + new Gson().toJson(errors) 
			);
		} catch (Exception e) {
			
			return ResponseEntity.ok(e.getMessage());
		}
		
	}
		
	@RequestMapping(value = "/selectSubjectsForm", method = {RequestMethod.GET,RequestMethod.POST})
	public ModelAndView selectSubjectsForm(HttpServletRequest request, HttpServletResponse response) {
		long startTime = Calendar.getInstance().getTimeInMillis();
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}

		request.getSession().setAttribute("ddSeatBookingComplete", null);
		request.getSession().setAttribute("onlineSeatBookingComplete", null);
		request.getSession().setAttribute("freeSeatBookingComplete", null);
		request.getSession().setAttribute("totalExamFees", 0);
		//request.getSession().setAttribute("bookingsToRelease", null);

		ModelAndView modelnView = new ModelAndView("selectSubjects");
		String sapid = (String)request.getSession().getAttribute("userId");
		
		ArrayList<ProgramSubjectMappingExamBean> applicableSubjectsList = new ArrayList<>();
		ArrayList<ProgramSubjectMappingExamBean> notApplicableSubjectsList = new ArrayList<>();
		List<String> eligibleSubjectsList = new ArrayList<>();
		ArrayList<String> applicableSubjects = new ArrayList<>();
		ArrayList<String> bookedSubjects = new ArrayList<>();
		ArrayList<String> releasedSubjects = new ArrayList<>();
		ArrayList<String> releasedNoChargeSubjects = new ArrayList<>();
		ArrayList<String> releasedPassedSubjects = new ArrayList<>();
		//ArrayList<String> approvedDDSubjects = new ArrayList<>();
		//ArrayList<String> rejectedDDSubjects = new ArrayList<>();
		//ArrayList<String> pendingDDApprovalSubjects = new ArrayList<>();
		ArrayList<String> approvedOnlineTransactionSubjects = new ArrayList<>();
		//boolean hasApprovedDD = false;
		boolean hasApprovedOnlineTransactions = false;
		boolean hasFreeSubjects = false;
		boolean hasReleasedSubjects = false;
		boolean hasReleasedNoChargeSubjects = false;
		boolean isExamSlotChangeLiveForStudent = false;
		boolean canBook = false;
		String changeSlotFees = this.changeSlotFees;
		
		// exam registration is set live when student has a booked subject in future so
		// they can release subjects because of 24 hrs prior policy so students were
		// able to book subjects which were released from admin's end with and without
		// charges. so adding this check to only allow to book released subjects when
		// exam booking is extended or exam is live NOT for 24 hrs prior policy
		boolean canBookReleasedSubjects = false;
		
		String canBookFreeSubjects = "true";
		ArrayList<String> freeApplicableSubjects = new ArrayList<>(); 

		int subjectsToPay = 0;
		StudentExamBean student = (StudentExamBean)request.getSession().getAttribute("studentExam");
		if("EPBM".equalsIgnoreCase(student.getProgram()) || "MPDV".equalsIgnoreCase(student.getProgram()) ) {
			redirectToPortalApp(response);
			return null;
			
		}
		ExamBookingExamBean examBooking = new ExamBookingExamBean();
		try{
			ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
			
			String liveExamMonth = eDao.getLiveExamMonth();
			String liveExamYear = eDao.getLiveExamYear();
			
			boolean  isExamRegistraionLive = false;
			if(demoSapids.contains(sapid)) {
				isExamRegistraionLive = true;
				canBook = true;
				modelnView.addObject("isExamRegistraionLive", isExamRegistraionLive);
				canBookReleasedSubjects = true;
			} else {

				isExamRegistraionLive = isExamRegistraionLive(request, sapid);

				if (isExamRegistraionLive && request.getParameterMap().containsKey("eid")) {
					changeSlotFees = ExamBookingController.changeSlotFeesAfterRegistrationWindowIsClosed;
					canBook = true;
					canBookReleasedSubjects = true;
				} else if (!isExamRegistraionLive) {
					isExamSlotChangeLiveForStudent = eDao.isStudentAllowedToChangeSlotPriorNumberOfDays(sapid,NUMBER_ONE);

					if (isExamSlotChangeLiveForStudent) {
						canBookFreeSubjects = "false";
						changeSlotFees = ExamBookingController.changeSlotFeesAfterRegistrationWindowIsClosed;
						isExamRegistraionLive = true;
					}
				} else {
					canBook = true;
					canBookReleasedSubjects = true;
				}

			}
			
			ebAuditLogger.info("{} selectSubjectsForm - canBookFreeSubjects : {} || changeSlotFees : {} || canBook : {} || canBookReleasedSubjects : {}  || isExamRegistraionLive : {}",
										sapid,
										canBookFreeSubjects,
										changeSlotFees,
										canBook,
										canBookReleasedSubjects,
										isExamRegistraionLive);
			
			request.setAttribute("canBookFreeSubjects", canBookFreeSubjects);
			modelnView.addObject("releaseFees", changeSlotFees);
			modelnView.addObject("canBookSubjects", canBook);
			modelnView.addObject("isExamRegistraionLive", isExamRegistraionLive);
			modelnView.addObject("canBookReleasedSubjects", canBookReleasedSubjects);
			
			boolean isCarryForwardRefundRequestFlagLive = eDao.isCarryForwardRefundRequestFlagLive(liveExamYear, liveExamMonth); 
			boolean isBookingFound = eDao.isBookingFound(liveExamYear, liveExamMonth, sapid);
			
			if(!isExamRegistraionLive && isCarryForwardRefundRequestFlagLive && isBookingFound) {
				// If exam registration is not live but carry forward and refund flag is live for the current cycle
				response.sendRedirect("examBookingRequestForm");
				return null;
			}

			String mostRecentTimetablePeriod = liveExamMonth + "-" + liveExamYear;
			
			if(("2023".equals(student.getEnrollmentYear()) && "Jul".equals(student.getEnrollmentMonth())) || "Diageo".equalsIgnoreCase(student.getConsumerType()) && mostRecentTimetablePeriod.contains("Sep") && mostRecentTimetablePeriod.contains("2019") ){
				modelnView.addObject("isExamRegistraionLive", false);
				setError(request, "Your registration is not live currently");
				return modelnView;
			}
			
			modelnView.addObject("mostRecentTimetablePeriod", mostRecentTimetablePeriod);

			//Block offline students from Resit Exam Registration: START
			if(liveExamMonth.equals("Apr") || liveExamMonth.equals("Sep")){
			/*Commented by Steffi to allow offline students for Apr/Sep registration.
			 * 
			 * 	if("Offline".equals(student.getExamMode()) || "ACBM".equals(student.getProgram())){*/
					
				// ACBM students cant register
				if("ACBM".equals(student.getProgram())){
					setError(request, "You are not authorized to register for Resit Examination");
					modelnView.addObject("isExamRegistraionLive",false);
					modelnView.addObject("examBooking", examBooking);
					return modelnView;
				}
			}
			//Block offline students from Resit Exam Registration: START


			//Generate list of confirmed bookings, so that same Date-Time cannot be used for bookings remaining subjects : START
			//ArrayList<String> dateTimeBookedList = new ArrayList<String>();
			/*ArrayList<ExamBookingTransactionBean> confirmedBookings = eDao.getConfirmedBookings(sapid);//Query only confirmed bookings.
			if(confirmedBookings != null && confirmedBookings.size() > 0){
				request.setAttribute("hasConfirmedBookings", "true");
			}


			for(ExamBookingTransactionBean bean : confirmedBookings){
				dateTimeBookedList.add(bean.getExamTime()+"|"+bean.getExamDate());//Added in pipe format since thats the format used on the page//
			}*/
			//request.getSession().setAttribute("dateTimeBookedList",dateTimeBookedList);//Placing the list in attribute//
			//Generate list of confirmed bookings, so that same Date-Time cannot be used for bookings remaining subjects : END
			//List<String> failAssgnSubmitted = new ArrayList<String>();
			eligibleSubjectsList = getAllEligibleSubjects(student, liveExamMonth, liveExamYear, eDao);
			
			/* Commented by Siddheshwar_K as no need to get ANS subjects list because we already have assignment submitted list
			 based on the submitted list we are marking the assignment submitted or not in previous cycles for particular subject.
			 failAssgnSubmitted = eDao.getFailedAssignSubmittedSubjectsList(sapid); */
			
			//Get free subjects for Exam, either for entire sem, or selectively allowed free for specific subject:START
			ArrayList<String> freeSubjects = getFreeSubjects(student);
			//
			ArrayList<String> individualFreeSubjects = getStudentFreeSubjectsMap().get(sapid);
			if(individualFreeSubjects != null && individualFreeSubjects.size() > 0){
				freeSubjects.addAll(individualFreeSubjects);
			}
			//Get free subjects for Exam, either for entire sem, or selectively allowed free for specific subject:END



			//Generate Map of Center ID and Name based on if student is mapped to Corporate/Regular Exam Centers: START
			corporateCenterUserMapping = getCorporateCenterUserMapping();//Get details of Students mapped to Corporate Exam Centers
			request.getSession().setAttribute("corporateCenterUserMapping", corporateCenterUserMapping);
			Map<String, String> examCenterIdNameMap = new HashMap<String,String>();

			if(corporateCenterUserMapping.containsKey(student.getSapid())){
				examCenterIdNameMap = eDao.getCorporateExamCenterIdNameMap();//Move this method to cache
				student.setCorporateExamCenterStudent(true);
				student.setMappedCorporateExamCenterId(corporateCenterUserMapping.get(sapid));
			}else{
				examCenterIdNameMap = getExamCenterIdNameMap();
				student.setCorporateExamCenterStudent(false);
			}
			request.getSession().setAttribute("examCenterIdNameMap", examCenterIdNameMap);
			//Generate Map of Center ID and Name based on if student is mapped to Corporate/Regular Exam Centers: END


			//Add subjects in different bucket, based on their current Exam Registration Status: START
			HashMap<String, String> subjectCenterMap = new HashMap<>();
			//Generate list of confirmed bookings, so that same Date-Time cannot be used for bookings remaining subjects : START
			ArrayList<String> dateTimeBookedList = new ArrayList<String>();

			ArrayList<ExamBookingTransactionBean> subjectsBooked = eDao.getConfirmedOrRelesedBooking(sapid);
			
			
			
			for (ExamBookingTransactionBean bean : subjectsBooked) {
				String subject = bean.getSubject();
				if(!eligibleSubjectsList.contains(bean.getSubject())) {
					// Dont let student view subjects that have been passed but student booked exam for
					continue;
				}
				if("Y".equals(bean.getBooked())){
					request.setAttribute("hasConfirmedBookings", "true");
					dateTimeBookedList.add(bean.getExamTime()+"|"+bean.getExamDate());//Added in pipe format since thats the format used on the page//

					bookedSubjects.add(subject);
					subjectCenterMap.put(subject, examCenterIdNameMap.get(bean.getCenterId()) + " ("+ bean.getExamDate()+ ", "+ bean.getExamTime()+")");
				}else if("RL".equals(bean.getBooked())  && SEAT_RELEASED.equalsIgnoreCase(bean.getTranStatus())  && (!releasedSubjects.contains(subject)) && eligibleSubjectsList.contains(subject)){
					releasedSubjects.add(subject);
				}else if("RL".equals(bean.getBooked())  && SEAT_RELEASED_NO_CHARGES.equalsIgnoreCase(bean.getTranStatus())  && (!releasedNoChargeSubjects.contains(subject))){
					releasedNoChargeSubjects.add(subject);
				}else if("RL".equals(bean.getBooked())  && SEAT_RELEASED_SUBJECT_CLEARED.equalsIgnoreCase(bean.getTranStatus())  && (!releasedPassedSubjects.contains(subject))){
					releasedPassedSubjects.add(subject);
				}
			}
			request.getSession().setAttribute("dateTimeBookedList",dateTimeBookedList);//Placing the list in attribute//
			//Add subjects in different bucket, based on their current Exam Registration Status: END


			//Logic to handle, if student seat is released, then booked and then again released and again booked: START 
			//In this case old released subjects will again pop up as to be booked, whereas there are separate rows for it as booked well.
			for (ExamBookingTransactionBean bean : subjectsBooked) {
				String subject = bean.getSubject();
				String booked = bean.getBooked();

				if("Y".equals(booked)){
					releasedSubjects.remove(subject);
					releasedNoChargeSubjects.remove(subject);
				}
				
				if("RL".equals(booked)){
					if(releasedNoChargeSubjects.contains(subject)){
						releasedSubjects.remove(subject);
					}
				}
			}
			
			//Logic to handle, if student seat is released, then booked and then again released and again booked: END


			//Add subjects in different bucket based on Payment Status: START
			approvedOnlineTransactionSubjects = eDao.getApprovedOnlineTransSubjects(sapid);
			removeInvalidSubjectsForExamBookingFromList(approvedOnlineTransactionSubjects, student);
			request.getSession().setAttribute("approvedOnlineTransactionSubjects", approvedOnlineTransactionSubjects);
			//Add subjects in different bucket based on Payment Status: END

			//Iterate through each subject and create Exam applicable+registered subjects list: START
			HashMap<String, ProgramSubjectMappingExamBean> subjectProgramSemMap = new HashMap<String, ProgramSubjectMappingExamBean>();
			ArrayList<ProgramSubjectMappingExamBean> programSubjectMappingList = getProgramSubjectMappingList();
			for (int i = 0; i < programSubjectMappingList.size(); i++) {
				ProgramSubjectMappingExamBean bean = programSubjectMappingList.get(i);
				if(bean.getPrgmStructApplicable().equals(student.getPrgmStructApplicable()) 
						&& bean.getProgram().equals(student.getProgram())
						&& !student.getWaivedOffSubjects().contains(bean.getSubject()) ){
					subjectProgramSemMap.put(bean.getSubject(), bean);//Needed for displaying Sem and Program on various pages

					if(eligibleSubjectsList.contains(bean.getSubject().trim())){
						//bean.setAssignmentSubmitted("No");
						bean.setCanBook("Yes");
						bean.setBookingStatus(NOT_BOOKED);
						applicableSubjectsList.add(bean);
						applicableSubjects.add(bean.getSubject());
						subjectsToPay++;
					}


					if(bookedSubjects.contains(bean.getSubject())){
						if(bean.getSubject().equalsIgnoreCase("Project")){
							continue;
						}

						if(bean.getSubject().equalsIgnoreCase("Module 4 - Project") && "PD - WM".equalsIgnoreCase(student.getProgram()) ){
							continue;
						}

						if(bean.getSubject().equalsIgnoreCase("Simulation: Mimic Pro") && "PD - DM".equalsIgnoreCase(student.getProgram()) ){
							continue;
						}
						if(bean.getSubject().equalsIgnoreCase("Simulation: Mimic Social") && "PD - DM".equalsIgnoreCase(student.getProgram()) ){
							continue;
						}
						bean.setCanBook("No");
						bean.setCanFreeBook("No");
						bean.setBookingStatus(BOOKED);
						bean.setCenterName(subjectCenterMap.get(bean.getSubject()));
						applicableSubjects.remove(bean.getSubject());
						subjectsToPay--;
					}else if(approvedOnlineTransactionSubjects.contains(bean.getSubject())){
						hasApprovedOnlineTransactions = true;
						bean.setCanBook("No");
						bean.setBookingStatus(ONLINE_PAYMENT_MANUALLY_APPROVED);
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
					}
					
					if(freeSubjects.contains(bean.getSubject()) && applicableSubjects.contains(bean.getSubject())
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
			
			removeInvalidSubjectsForExamBookingFromList(freeApplicableSubjects, student);
			removeInvalidSubjectsForExamBookingFromList(releasedSubjects, student);
			removeInvalidSubjectsForExamBookingFromList(releasedNoChargeSubjects, student);
			
			if(subjectProgramSemMap.containsKey("Project")){
				subjectProgramSemMap.remove("Project");
			}
			if(subjectProgramSemMap.containsKey("Module 4 - Project") && "PD - WM".equalsIgnoreCase(student.getProgram()) ){
				subjectProgramSemMap.remove("Module 4 - Project");
			}
			if(subjectProgramSemMap.containsKey("Simulation: Mimic Pro") && "PD - DM".equalsIgnoreCase(student.getProgram()) ){
				subjectProgramSemMap.remove("Simulation: Mimic Pro");
			}
			if(subjectProgramSemMap.containsKey("Simulation: Mimic Social") && "PD - DM".equalsIgnoreCase(student.getProgram()) ){
				subjectProgramSemMap.remove("Simulation: Mimic Social");
			}
			request.getSession().setAttribute("freeApplicableSubjects", freeApplicableSubjects);
			request.getSession().setAttribute("releasedSubjects", releasedSubjects);
			request.getSession().setAttribute("releasedNoChargeSubjects", releasedNoChargeSubjects);
			request.getSession().setAttribute("subjectProgramSemMap", subjectProgramSemMap);
			//Iterate through each subject and create Exam applicable+registered subjects list: END



			//Set various flags based on subjects found in different bucket: START
			/*if(hasApprovedDD){
				request.setAttribute("hasApprovedDD", "true");
			}*/
			if(hasApprovedOnlineTransactions){
				request.setAttribute("hasApprovedOnlineTransactions", "true");
			}
			if(hasFreeSubjects){
				request.setAttribute("hasFreeSubjects", "true");
			}
			if(hasReleasedSubjects){
				request.setAttribute("hasReleasedSubjects", "true");
			}
			if(hasReleasedNoChargeSubjects){
				request.setAttribute("hasReleasedNoChargeSubjects", "true");
			}
			//Set various flags based on subjects found in different bucket: END



			//Logic for deciding if Regular Exam fees should be charged or Resit fees should be charged
			HashMap<String,Integer> mapOfSubjectNameAndExamFee = new HashMap<String,Integer>();
			ArrayList<StudentMarksBean> writtenAttemptsList = (ArrayList<StudentMarksBean>)eDao.getWrittenAttempts(sapid);
			HashMap<String, String> subejctWrittenScoreMap = new HashMap<>();
			for (StudentMarksBean marksBean : writtenAttemptsList) {//This will store data if student ever had written attempt, for deciding exam fees
				if(marksBean.getWritenscore() != null ){
					subejctWrittenScoreMap.put(marksBean.getSubject(), marksBean.getWritenscore());
				}
			}

			ArrayList<String> assignmentSubmittedSubjects = (ArrayList<String>)eDao.getAssignSubmittedSubjectsList(sapid);
			HashMap<String,Boolean> confirmedBookingsExcludingCurrentCycleMap = eDao.getConfirmedBookingsExcludingCurrentCycleMap(sapid);
			for (ProgramSubjectMappingExamBean bean : applicableSubjectsList) {
				String writtenScore = subejctWrittenScoreMap.get(bean.getSubject());


				if(writtenScore == null || "".equals(writtenScore.trim())){
					bean.setExamFees(examFeesPerSubjectFirstAttempt + "" );
					mapOfSubjectNameAndExamFee.put(bean.getSubject(),examFeesPerSubject);//Regular Exam Fees
				}else{
					bean.setExamFees(examFeesPerSubjectResitAttempt + "" );
					mapOfSubjectNameAndExamFee.put(bean.getSubject(),examFeesPerSubjectResitAttempt);//Resit Fees
				}

				//If result is not declared, then consider exam bookings for charging Resit fees
				if(confirmedBookingsExcludingCurrentCycleMap.get(bean.getSubject())!=null){
					bean.setExamFees(examFeesPerSubjectResitAttempt + "" );
					mapOfSubjectNameAndExamFee.put(bean.getSubject(),examFeesPerSubjectResitAttempt);
				}

				//Set Assignment status for each subject
				if(assignmentSubmittedSubjects.contains(bean.getSubject())){
					bean.setAssignmentSubmitted("Yes");
				}else{
					bean.setAssignmentSubmitted("No");
				}
				if("Project".equals(bean.getSubject())){
					bean.setAssignmentSubmitted("NA");
					notApplicableSubjectsList.add(bean);
				}
				if("Module 4 - Project".equals(bean.getSubject()) && "PD - WM".equalsIgnoreCase(student.getProgram())){
					bean.setAssignmentSubmitted("NA");
					notApplicableSubjectsList.add(bean);
				}
				if("Simulation: Mimic Pro".equals(bean.getSubject()) && "PD - DM".equalsIgnoreCase(student.getProgram()) ){
					bean.setAssignmentSubmitted("NA");
					notApplicableSubjectsList.add(bean);
				}
				if("Simulation: Mimic Social".equals(bean.getSubject()) && "PD - DM".equalsIgnoreCase(student.getProgram()) ){
					bean.setAssignmentSubmitted("NA");
					notApplicableSubjectsList.add(bean);
				}
			}

			if(mapOfSubjectNameAndExamFee.containsKey("Project")){
				mapOfSubjectNameAndExamFee.remove("Project");
			}

			if(mapOfSubjectNameAndExamFee.containsKey("Module 4 - Project") && "PD - WM".equalsIgnoreCase(student.getProgram())){
				mapOfSubjectNameAndExamFee.remove("Module 4 - Project");
			}

			if(mapOfSubjectNameAndExamFee.containsKey("Simulation: Mimic Pro") && "PD - DM".equalsIgnoreCase(student.getProgram()) ){
				mapOfSubjectNameAndExamFee.remove("Simulation: Mimic Pro");
			}
			if(mapOfSubjectNameAndExamFee.containsKey("Simulation: Mimic Social") && "PD - DM".equalsIgnoreCase(student.getProgram()) ){
				mapOfSubjectNameAndExamFee.remove("Simulation: Mimic Social");
			}
			if(applicableSubjects.contains("Project")){
				applicableSubjects.remove("Project");
				subjectsToPay--;
			}
			if(applicableSubjects.contains("Module 4 - Project") && "PD - WM".equalsIgnoreCase(student.getProgram())){
				applicableSubjects.remove("Module 4 - Project");
				subjectsToPay--;
			}
			if(applicableSubjects.contains("Simulation: Mimic Pro") && "PD - DM".equalsIgnoreCase(student.getProgram()) ){
				applicableSubjects.remove("Simulation: Mimic Pro");
				subjectsToPay--;
			}
			if(applicableSubjects.contains("Simulation: Mimic Social") && "PD - DM".equalsIgnoreCase(student.getProgram()) ){
				applicableSubjects.remove("Simulation: Mimic Social");
				subjectsToPay--;
			}
			
			removeInvalidSubjectsForExamBookingFromList(freeApplicableSubjects, student);
			
			applicableSubjectsList.removeAll(notApplicableSubjectsList);
				
			
			
			request.getSession().setAttribute("mapOfSubjectNameAndExamFee", mapOfSubjectNameAndExamFee);

			examBooking.setApplicableSubjects(applicableSubjects);
			examBooking.setFreeApplicableSubjects(freeApplicableSubjects);

			modelnView.addObject("applicableSubjectsList", applicableSubjectsList);
			modelnView.addObject("applicableSubjectsListCount", applicableSubjectsList.size());

			request.getSession().setAttribute("applicableSubjectsList", applicableSubjectsList);

			modelnView.addObject("examBooking", examBooking);
			modelnView.addObject("subjectsToPay", subjectsToPay);

		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in getting subjects.");
			ebAuditLogger.error("{} selectSubjectsForm ERROR : {}", 
									sapid, 
									Throwables.getStackTraceAsString(e));
		}
		//for testing only
		/*for (ProgramSubjectMappingExamBean bean : applicableSubjectsList) {
		
		}*/
		//testn only
		
		return modelnView;
	}
	
	private void removeInvalidSubjectsForExamBookingFromList(List<String> subjects, StudentExamBean student) {
		// Function removes subjects which are not eligible for ExamBooking
		if(subjects.contains("Project")){
			subjects.remove("Project");
		}
		if(subjects.contains("Module 4 - Project") && "PD - WM".equalsIgnoreCase(student.getProgram())){
			subjects.remove("Module 4 - Project");
		}
		if(subjects.contains("Simulation: Mimic Pro") && "PD - DM".equalsIgnoreCase(student.getProgram()) ){
			subjects.remove("Simulation: Mimic Pro");
		}
		if(subjects.contains("Simulation: Mimic Social") && "PD - DM".equalsIgnoreCase(student.getProgram()) ){
			subjects.remove("Simulation: Mimic Social");
		}

		if(subjects.contains("Design Thinking") && ("BBA".equalsIgnoreCase(student.getProgram()) || "B.Com".equalsIgnoreCase(student.getProgram())) ){
			subjects.remove("Design Thinking");
		}
		
		if(subjects.contains("Employability Skills - II Tally") && ("BBA".equalsIgnoreCase(student.getProgram()) || "B.Com".equalsIgnoreCase(student.getProgram())) ){
			subjects.remove("Employability Skills - II Tally");
		}
	}
	
	private List<String> getAllEligibleSubjects(StudentExamBean student, String liveExamMonth, String liveExamYear, ExamBookingDAO eDao) {
		String sapid = student.getSapid();
		List<String> eligibleSubjectsList = eDao.getExamBookingEligibleSubjectsForCycle(sapid, liveExamMonth, liveExamYear);
		removeInvalidSubjectsForExamBookingFromList(eligibleSubjectsList, student);
		return eligibleSubjectsList;
	}

	
//	to be deleted, api shifted to rest controller
//	@RequestMapping(value="/m/examTestTakenStatus", method=RequestMethod.POST)
//	public ResponseEntity<Map<String, String>> examTestTakenStatus(@RequestBody ExamBookingTransactionBean examBookingTransactionBean){
//		ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type","application/json");
//		Map<String, String> resp = new HashMap<String, String>();
//		try {
//			String count = dao.getCountTestTaken(examBookingTransactionBean.getExamDate(), examBookingTransactionBean.getExamTime());
//			resp.put("status", "success");
//			resp.put("count", count);
//		}catch (Exception e) {
//			resp.put("status", "error");
//			resp.put("count", "Error " + e.getMessage());
//		}
//		return new ResponseEntity<Map<String, String>>(resp, headers, HttpStatus.OK);
//	}
	
	
//	@RequestMapping(value="/m/selectSubjectsForm", method=RequestMethod.POST, consumes="application/json", produces="application/json")
//	public ResponseEntity<ExamRegistrationBeanAPIResponse> mselectSubjectsForm(@RequestBody StudentBean input) throws Exception {
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type","application/json");
//		ExamRegistrationBeanAPIResponse response = new ExamRegistrationBeanAPIResponse();
//		if(input.getSapid() == null) {
//			response.setStatus("fail");
//			response.setError("Invalid sapId found");
//			return new ResponseEntity<ExamRegistrationBeanAPIResponse>(response, HttpStatus.OK);
//		}
//		
//		ArrayList<String> eligibleSubjectsList = new ArrayList<>();
//		ArrayList<String> releasedSubjects = new ArrayList<>();
//		ArrayList<String> releasedNoChargeSubjects = new ArrayList<>();
//		ArrayList<String> releasedPassedSubjects = new ArrayList<>();
//		ArrayList<String> approvedOnlineTransactionSubjects = new ArrayList<>();
//		ArrayList<ProgramSubjectMappingBean> applicableSubjectsList = new ArrayList<>();
//		ArrayList<ProgramSubjectMappingBean> notApplicableSubjectsList = new ArrayList<>();
//		ArrayList<String> applicableSubjects = new ArrayList<>();
//		ArrayList<String> bookedSubjects = new ArrayList<>();
//		boolean hasApprovedOnlineTransactions = false;
//		boolean hasFreeSubjects = false;
//		boolean hasReleasedSubjects = false;
//		boolean hasReleasedNoChargeSubjects = false;
//		ArrayList<String> freeApplicableSubjects = new ArrayList<>(); 
//
//		int subjectsToPay = 0;
//		ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
//		StudentBean student ;
//		student = dao.getSingleStudentsData(input.getSapid());
//		ExamBookingBean examBooking = new ExamBookingBean();
//		try {
//			ExamBookingDAO eDao = (ExamBookingDAO) act.getBean("examBookingDAO");
//			//Check for Exam Registration Status 
//			boolean isExamRegistraionLive = misExamRegistraionLive(student.getSapid());
//			response.setIsExamRegistraionLive(isExamRegistraionLive);
//			//Get Current Cycle
//			String mostRecentTimetablePeriod = eDao.getLiveExamMonth() + "-" + eDao.getLiveExamYear();
//			response.setMostRecentTimetablePeriod(mostRecentTimetablePeriod);
//			//Block offline students from Resit Exam Registration: START
//			
//
//			//For Resit consider only failed subjects :START
//			if((mostRecentTimetablePeriod.contains("Apr") || mostRecentTimetablePeriod.contains("Sep")) && !"Diageo".equalsIgnoreCase(student.getConsumerType()) ){ //for diageo apr/sep is first attempt.
//				/*Commented by Steffi to allow offline students for Apr/Sep registration.
//				 * 
//				 * 	if("Offline".equals(student.getExamMode()) || "ACBM".equals(student.getProgram())){*/
//						
//						if("ACBM".equals(student.getProgram())){
//						response.setStatus("fail");
//						response.setError("You are not authorized to register for Resit Examination");	
//						response.setIsExamRegistraionLive(isExamRegistraionLive);
////						modelnView.addObject("examBooking", examBooking);
//						return new ResponseEntity(response, headers, HttpStatus.OK);
//					}
//				}
//			
//			//Fetch current Sem subjects : START
//			int lastSem = 1;
//			List<StudentMarksBean> registrationList = eDao.getActiveRegistrations(student.getSapid());
//			
//			//Student who has registration data. Take the last semester and bring all subjects before that
//			for (StudentMarksBean registrationBean : registrationList) {
//				int sem = Integer.parseInt(registrationBean.getSem());
//				if(sem >= lastSem){
//					lastSem = sem;
//				}
//			}
//			if(!registrationList.isEmpty()) {
//				eligibleSubjectsList = eDao.getSubjectsForStudents(student, lastSem);
//			}
//			//add waived in subjects
//			ArrayList<String> waivedInSubjects = studentService.mgetWaivedInSubjects(student);
//			for(String subject : waivedInSubjects) {
//				if(!eligibleSubjectsList.contains(subject)) {
//					eligibleSubjectsList.add(subject);
//				}
//			}
//			
//			//Fetch current Sem subjects : END
//
//			//Also take failed subjects for Exam Registration: START
//			ArrayList<PassFailBean> passFailList = (ArrayList<PassFailBean>)eDao.getPassFailedSubjectsList(student.getSapid());
//			ArrayList<PassFailBean> failList = new ArrayList<>();
//			ArrayList<String> passList = new ArrayList<>();
//			ArrayList<String> failedSubjectList = new ArrayList<>();
//			for(PassFailBean item:passFailList) {
//				if("Y".equals(item.getIsPass())) {
//					passList.add(item.getSubject());
//				}else if("N".equals(item.getIsPass())) {
//					failList.add(item);
//					failedSubjectList.add(item.getSubject());
//				}
//			}
//			eligibleSubjectsList.addAll(failedSubjectList);//Failed Subjects are also eligible for Registration
//			
//			//Failed Assignment Subjects
//			ArrayList<String> failedAssignmentSubjectList = new ArrayList<>();
//			for(PassFailBean item:failList) {
//				if(!"ANS".equalsIgnoreCase(item.getAssignmentscore())){
//					failedAssignmentSubjectList.add(item.getSubject());
//				}
//			}
//			//Also take failed subjects for Exam Registration: End
//			
//			//For Resit consider only failed subjects :START
//			if((mostRecentTimetablePeriod.contains("Jun") || mostRecentTimetablePeriod.contains("Dec")) && "Diageo".equalsIgnoreCase(student.getConsumerType()) ){//for diageo apr/sep is resit attempt.
//				eligibleSubjectsList = new ArrayList<String>();//Clear Current Sem Subjects from Eligible list
//				eligibleSubjectsList.addAll(failedSubjectList);
//			}
//			if((mostRecentTimetablePeriod.contains("Apr") || mostRecentTimetablePeriod.contains("Sep")) && !"Diageo".equalsIgnoreCase(student.getConsumerType()) ){ //for diageo apr/sep is first attempt.
//			
//				eligibleSubjectsList = new ArrayList<String>();//Clear Current Sem Subjects from Eligible list
//				eligibleSubjectsList.addAll(failedSubjectList);
//				
//				// commented because portal logic is commented 
//				/*boolean hasClearedProject = true;
//				if(lastSem == 4){
//					hasClearedProject = eDao.checkIfProjectIsCleared(student.getSapid());
//					Students who have registered in Jul 2017 for sem 4, then they are applicable for project only in December since 6 months gap is
//					  required for registration of project hence taking difference between the exam order of most recent registration	
//					  IF student has registered fr sem 4 in Jul drive, then hasClearedProject will be false for September cycle, and will appear in
//					  eligibile list. To avoid that we check for minimum 6 months gap
//					 
//					StudentMarksBean recentRegistrationBean = eDao.getRegistrationForYearMonthSem(student.getSapid(),String.valueOf(lastSem));
//					double diff = eDao.getExamOrderFromExamMonthAndYear(eDao.getLiveExamMonth(),eDao.getLiveExamYear()) - eDao.getExamOrderFromAcadMonthAndYear(recentRegistrationBean.getMonth(),recentRegistrationBean.getYear());
//					if(!hasClearedProject && diff >=0.0){
//						eligibleSubjectsList.add("Project");
//					}
//				}*/
//			}
//			//For Resit consider only failed subjects :END
//			if(eligibleSubjectsList.contains("Project")){
//				eligibleSubjectsList.remove("Project");
//			}
//			if(eligibleSubjectsList.contains("Module 4 - Project") && "PD - WM".equalsIgnoreCase(student.getProgram())){
//				eligibleSubjectsList.remove("Module 4 - Project");
//			}
//			//Get free subjects for Exam, either for entire sem, or selectively allowed free for specific subject:START
//			ArrayList<String> freeSubjects = getFreeSubjects(student);
//			ArrayList<String> individualFreeSubjects = getStudentFreeSubjectsMap().get(student.getSapid());
//			if(individualFreeSubjects != null && individualFreeSubjects.size() > 0){
//				freeSubjects.addAll(individualFreeSubjects);
//			}
//			//Get free subjects for Exam, either for entire sem, or selectively allowed free for specific subject:END
//			
//			//Generate Map of Center ID and Name based on if student is mapped to Corporate/Regular Exam Centers: START
//			corporateCenterUserMapping = getCorporateCenterUserMapping();//Get details of Students mapped to Corporate Exam Centers
//			response.setCorporateCenterUserMapping(corporateCenterUserMapping);
//			Map<String, String> examCenterIdNameMap = new HashMap<String,String>();
//			if(corporateCenterUserMapping.containsKey(student.getSapid())){
//				examCenterIdNameMap = eDao.getCorporateExamCenterIdNameMap();//Move this method to cache
//				student.setCorporateExamCenterStudent(true);
//				student.setMappedCorporateExamCenterId(corporateCenterUserMapping.get(student.getSapid()));
//			}else {
//				examCenterIdNameMap = getExamCenterIdNameMap();
//				student.setCorporateExamCenterStudent(false);
//			}
//			response.setExamCenterIdNameMap(examCenterIdNameMap);
//			//Generate Map of Center ID and Name based on if student is mapped to Corporate/Regular Exam Centers: END
//			
//			//Add subjects in different bucket, based on their current Exam Registration Status: START
//			HashMap<String, String> subjectCenterMap = new HashMap<>();
//			//Generate list of confirmed bookings, so that same Date-Time cannot be used for bookings remaining subjects : START
//			ArrayList<String> dateTimeBookedList = new ArrayList<String>();
//			ArrayList<ExamBookingTransactionBean> subjectsBooked = eDao.getConfirmedBooking(student.getSapid());
//
//			for (ExamBookingTransactionBean bean : subjectsBooked) {
//				String subject = bean.getSubject();
//				if("Y".equals(bean.getBooked())){
//					response.setHasConfirmedBookings("true");
//					dateTimeBookedList.add(bean.getExamTime()+"|"+bean.getExamDate());//Added in pipe format since thats the format used on the page//
//
//					bookedSubjects.add(subject);
//					subjectCenterMap.put(subject, examCenterIdNameMap.get(bean.getCenterId()) + " ("+ bean.getExamDate()+ ", "+ bean.getExamTime()+")");
//				}else if("RL".equals(bean.getBooked())  && SEAT_RELEASED.equalsIgnoreCase(bean.getTranStatus())  && (!releasedSubjects.contains(subject)) && (!passList.contains(subject))){
//					releasedSubjects.add(subject);
//				}else if("RL".equals(bean.getBooked())  && SEAT_RELEASED_NO_CHARGES.equalsIgnoreCase(bean.getTranStatus())  && (!releasedNoChargeSubjects.contains(subject))){
//					releasedNoChargeSubjects.add(subject);
//				}else if("RL".equals(bean.getBooked())  && SEAT_RELEASED_SUBJECT_CLEARED.equalsIgnoreCase(bean.getTranStatus())  && (!releasedPassedSubjects.contains(subject))){
//					releasedPassedSubjects.add(subject);
//				}
//			}
//			response.setDateTimeBookedList(dateTimeBookedList);//Placing the list in attribute//
//			//Add subjects in different bucket, based on their current Exam Registration Status: END
//
//			//Logic to handle, if student seat is released, then booked and then again released and again booked: START 
//			//In this case old released subjects will again pop up as to be booked, whereas there are separate rows for it as booked well.
//			for (ExamBookingTransactionBean bean : subjectsBooked) {
//				String subject = bean.getSubject();
//				String booked = bean.getBooked();
//
//				if("Y".equals(bean.getBooked())){
//					releasedSubjects.remove(subject);
//					releasedNoChargeSubjects.remove(subject);
//				}
//				
//				if("RL".equals(bean.getBooked())){
//					if(releasedNoChargeSubjects.contains(subject)){
//						releasedSubjects.remove(subject);
//					}
//				}
//			}
//			//Logic to handle, if student seat is released, then booked and then again released and again booked: END
//			
//			approvedOnlineTransactionSubjects = eDao.getApprovedOnlineTransSubjects(student.getSapid());
//			if(approvedOnlineTransactionSubjects.contains("Project")){
//				approvedOnlineTransactionSubjects.remove("Project");
//			}
//			if(approvedOnlineTransactionSubjects.contains("Module 4 - Project") && "PD - WM".equalsIgnoreCase(student.getProgram())){
//				approvedOnlineTransactionSubjects.remove("Module 4 - Project");
//			}
//			
//			response.setApprovedOnlineTransactionSubjects(approvedOnlineTransactionSubjects);
//			//Add subjects in different bucket based on Payment Status: END
//
//			//Iterate through each subject and create Exam applicable+registered subjects list: START
//			HashMap<String, ProgramSubjectMappingBean> subjectProgramSemMap = new HashMap<String, ProgramSubjectMappingBean>();
//			ArrayList<ProgramSubjectMappingBean> programSubjectMappingList = getProgramSubjectMappingList();
//			for (int i = 0; i < programSubjectMappingList.size(); i++) {
//				ProgramSubjectMappingBean bean = programSubjectMappingList.get(i);
//
//
//
//				if(bean.getPrgmStructApplicable().equals(student.getPrgmStructApplicable()) 
//						&& bean.getProgram().equals(student.getProgram())
//						&& !student.getWaivedOffSubjects().contains(bean.getSubject()) ){
//					if(passList.contains(bean.getSubject())){
//						//Not applicable to book if already cleared. Do not remove this condition
//						continue;
//					}
//
//					subjectProgramSemMap.put(bean.getSubject(), bean);//Needed for displaying Sem and Program on various pages
//
//					if(eligibleSubjectsList.contains(bean.getSubject().trim())){
//						//bean.setAssignmentSubmitted("No");
//
//						bean.setCanBook("Yes");
//						bean.setBookingStatus(NOT_BOOKED);
//						applicableSubjectsList.add(bean);
//						applicableSubjects.add(bean.getSubject());
//						subjectsToPay++;
//					}
//
//
//					if(bookedSubjects.contains(bean.getSubject())){
//						if(bean.getSubject().equalsIgnoreCase("Project")){
//							continue;
//						}
//						if(bean.getSubject().equalsIgnoreCase("Module 4 - Project") && "PD - WM".equalsIgnoreCase(student.getProgram())){
//							continue;
//						}
//						bean.setCanBook("No");
//						bean.setCanFreeBook("No");
//						bean.setBookingStatus(BOOKED);
//						bean.setCenterName(subjectCenterMap.get(bean.getSubject()));
//						applicableSubjects.remove(bean.getSubject());
//						subjectsToPay--;
//					}else if(approvedOnlineTransactionSubjects.contains(bean.getSubject())){
//						hasApprovedOnlineTransactions = true;
//						bean.setCanBook("No");
//						bean.setBookingStatus(ONLINE_PAYMENT_MANUALLY_APPROVED);
//						applicableSubjects.remove(bean.getSubject());
//						subjectsToPay--;
//					}else if(releasedSubjects.contains(bean.getSubject())){
//						hasReleasedSubjects = true;
//						bean.setCanBook("No");
//						bean.setCanFreeBook("No");
//						bean.setBookingStatus(SEAT_RELEASED);
//						applicableSubjects.remove(bean.getSubject());
//						subjectsToPay--;
//					}else if(releasedNoChargeSubjects.contains(bean.getSubject())){
//						hasReleasedNoChargeSubjects = true;
//						bean.setCanBook("No");
//						bean.setCanFreeBook("No");
//						bean.setBookingStatus(SEAT_RELEASED_NO_CHARGES);
//						applicableSubjects.remove(bean.getSubject());
//						subjectsToPay--;
//					}else if(releasedPassedSubjects.contains(bean.getSubject())){
//						hasReleasedNoChargeSubjects = true;
//						bean.setCanBook("No");
//						bean.setCanFreeBook("No");
//						bean.setBookingStatus(SEAT_RELEASED_SUBJECT_CLEARED);
//						applicableSubjects.remove(bean.getSubject());
//						subjectsToPay--;
//					}/*else if(approvedDDSubjects.contains(bean.getSubject())){
//						hasApprovedDD = true;
//						bean.setCanBook("No");
//						bean.setBookingStatus(DD_APPROVED);
//						applicableSubjects.remove(bean.getSubject());
//						subjectsToPay--;
//					}else if(rejectedDDSubjects.contains(bean.getSubject())){
//						//hasApprovedDD = true;
//						bean.setCanBook("No");
//						bean.setBookingStatus(DD_REJECTED);
//						applicableSubjects.remove(bean.getSubject());
//						subjectsToPay--;
//					}else if(pendingDDApprovalSubjects.contains(bean.getSubject())){
//						bean.setCanBook("No");
//						bean.setBookingStatus(DD_APPROVAL_PENDING);
//						applicableSubjects.remove(bean.getSubject());
//						subjectsToPay--;
//					}*/
//
//
//					if(freeSubjects.contains(bean.getSubject()) && applicableSubjects.contains(bean.getSubject())
//							&& "Yes".equals(bean.getCanBook())){
//						hasFreeSubjects = true;
//						bean.setCanBook("No");
//						bean.setCanFreeBook("Yes");
//						bean.setBookingStatus(FEE_IN_ADMISSION);
//						applicableSubjects.remove(bean.getSubject());
//						subjectsToPay--;
//						freeApplicableSubjects.add(bean.getSubject());
//					}
//
//				}
//			}
//			if(freeApplicableSubjects.contains("Project")){
//				freeApplicableSubjects.remove("Project");
//			}
//			if(freeApplicableSubjects.contains("Module 4 - Project") && "PD - WM".equalsIgnoreCase(student.getProgram())){
//				freeApplicableSubjects.remove("Module 4 - Project");
//			}
//			
//			if(releasedSubjects.contains("Project")){
//				releasedSubjects.remove("Project");
//			}
//			if(releasedSubjects.contains("Module 4 - Project") && "PD - WM".equalsIgnoreCase(student.getProgram())){
//				releasedSubjects.remove("Module 4 - Project");
//			}
//			
//			if(releasedNoChargeSubjects.contains("Project")){
//				releasedNoChargeSubjects.remove("Project");
//			}
//			if(releasedNoChargeSubjects.contains("Module 4 - Project") && "PD - WM".equalsIgnoreCase(student.getProgram())){
//				releasedNoChargeSubjects.remove("Module 4 - Project");
//			}
//			
//			if(subjectProgramSemMap.containsKey("Project")){
//				subjectProgramSemMap.remove("Project");
//			}
//			if(subjectProgramSemMap.containsKey("Module 4 - Project") && "PD - WM".equalsIgnoreCase(student.getProgram())){
//				subjectProgramSemMap.remove("Module 4 - Project");
//			}
//			
//			response.setFreeApplicableSubjects(freeApplicableSubjects);
//			response.setReleasedSubjects(releasedSubjects);
//			response.setReleasedNoChargeSubjects(releasedNoChargeSubjects);
//			response.setSubjectProgramSemMap(subjectProgramSemMap);
//			//Iterate through each subject and create Exam applicable+registered subjects list: END
//
//
//
//			//Set various flags based on subjects found in different bucket: START
//			/*if(hasApprovedDD){
//				request.setAttribute("hasApprovedDD", "true");
//			}*/
//			if(hasApprovedOnlineTransactions){
//				response.setHasApprovedOnlineTransactions(hasApprovedOnlineTransactions);
//			}
//			if(hasFreeSubjects){
//				response.setHasFreeSubjects(hasFreeSubjects);
//			}
//			if(hasReleasedSubjects){
//				response.setHasReleasedSubjects(hasReleasedSubjects);
//			}
//			if(hasReleasedNoChargeSubjects){
//				response.setHasReleasedNoChargeSubjects(hasReleasedNoChargeSubjects);
//			}
//			//Set various flags based on subjects found in different bucket: END
//
//
//
//			//Logic for deciding if Regular Exam fees should be charged or Resit fees should be charged
//			HashMap<String,Integer> mapOfSubjectNameAndExamFee = new HashMap<String,Integer>();
//			ArrayList<StudentMarksBean> writtenAttemptsList = (ArrayList<StudentMarksBean>)eDao.getWrittenAttempts(student.getSapid());
//			HashMap<String, String> subejctWrittenScoreMap = new HashMap<>();
//			for (StudentMarksBean marksBean : writtenAttemptsList) {//This will store data if student ever had written attempt, for deciding exam fees
//				if(marksBean.getWritenscore() != null ){
//					subejctWrittenScoreMap.put(marksBean.getSubject(), marksBean.getWritenscore());
//				}
//			}
//
//			ArrayList<String> assignmentSubmittedSubjects = (ArrayList<String>)eDao.getAssignSubmittedSubjectsList(student.getSapid());
//			HashMap<String,Boolean> confirmedBookingsExcludingCurrentCycleMap = eDao.getConfirmedBookingsExcludingCurrentCycleMap(student.getSapid());
//			for (ProgramSubjectMappingBean bean : applicableSubjectsList) {
//				String writtenScore = subejctWrittenScoreMap.get(bean.getSubject());
//
//
//				if(writtenScore == null || "".equals(writtenScore.trim())){
//					bean.setExamFees(examFeesPerSubjectFirstAttempt + "" );
//					mapOfSubjectNameAndExamFee.put(bean.getSubject(),examFeesPerSubject);//Regular Exam Fees
//				}else{
//					bean.setExamFees(examFeesPerSubjectResitAttempt + "" );
//					mapOfSubjectNameAndExamFee.put(bean.getSubject(),examFeesPerSubjectResitAttempt);//Resit Fees
//				}
//
//				//If result is not declared, then consider exam bookings for charging Resit fees
//				if(confirmedBookingsExcludingCurrentCycleMap.get(bean.getSubject())!=null){
//					bean.setExamFees(examFeesPerSubjectResitAttempt + "" );
//					mapOfSubjectNameAndExamFee.put(bean.getSubject(),examFeesPerSubjectResitAttempt);
//				}
//
//				//Set Assignment status for each subject
//				if(assignmentSubmittedSubjects.contains(bean.getSubject()) || failedAssignmentSubjectList.contains(bean.getSubject())){
//					bean.setAssignmentSubmitted("Yes");
//				}else{
//					bean.setAssignmentSubmitted("No");
//				}
//				if("Project".equals(bean.getSubject())){
//					bean.setAssignmentSubmitted("NA");
//					notApplicableSubjectsList.add(bean);
//				}
//				if("Module 4 - Project".equals(bean.getSubject()) && "PD - WM".equalsIgnoreCase(student.getProgram())){
//					bean.setAssignmentSubmitted("NA");
//					notApplicableSubjectsList.add(bean);
//				}
//			}
//			
//
//			if(mapOfSubjectNameAndExamFee.containsKey("Project")){
//				mapOfSubjectNameAndExamFee.remove("Project");
//			}
//			if(mapOfSubjectNameAndExamFee.containsKey("Module 4 - Project") && "PD - WM".equalsIgnoreCase(student.getProgram())){
//				mapOfSubjectNameAndExamFee.remove("Module 4 - Project");
//			}
//			
//			if(applicableSubjects.contains("Project")){
//				applicableSubjects.remove("Project");
//				subjectsToPay--;
//			}
//			if(applicableSubjects.contains("Module 4 - Project") && "PD - WM".equalsIgnoreCase(student.getProgram())){
//				applicableSubjects.remove("Module 4 - Project");
//				subjectsToPay--;
//			}
//			
//			if(freeApplicableSubjects.contains("Project")){
//				freeApplicableSubjects.remove("Project");
//			}
//			if(freeApplicableSubjects.contains("Module 4 - Project") && "PD - WM".equalsIgnoreCase(student.getProgram())){
//				freeApplicableSubjects.remove("Module 4 - Project");
//			}
//			
//			applicableSubjectsList.removeAll(notApplicableSubjectsList);
//			
//			response.setMapOfSubjectNameAndExamFee(mapOfSubjectNameAndExamFee);
//			examBooking.setApplicableSubjects(applicableSubjects);
//			examBooking.setFreeApplicableSubjects(freeApplicableSubjects);
//			response.setApplicableSubjectsList(applicableSubjectsList);
//			response.setApplicableSubjectsListCount( applicableSubjectsList.size());
//			response.setExamBookingBean(examBooking);
//			response.setSubjectsToPay(subjectsToPay);
//			response.setStatus("success");
//		} catch (Exception e) {
//			// TODO: handle exception
//			
//			response.setError("fail");
//			response.setError("Error in getting subjects.");
//		}
//		return new ResponseEntity(response, headers, HttpStatus.OK);
//	}
	
	private boolean isExamRegistraionLive(HttpServletRequest request, String sapid) {
		String sapIdEncrypted = request.getParameter("eid");
		return checkIfExamRegistrationLive(sapid, sapIdEncrypted);
	}
//	private boolean isExamRegistraionLive(HttpServletRequest request, String sapid) {
//		/***** Logic for Allowing or Blocking Exam Registration : START *****/
//		ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
//		boolean isExamRegistraionLive = eDao.isConfigurationLive("Exam Registration");
//		String sapIdEncrypted = request.getParameter("eid");
//		StudentBean student = eDao.getSingleStudentsData(sapid);
//
//		String sapIdFromURL = null;
//		try {
//			if(sapIdEncrypted != null){
//				sapIdFromURL = AESencrp.decrypt(sapIdEncrypted);
//			}
//		} catch (Exception e) {
//			
//		}
//		// checking if system.now is between extended date time after exam registration End allowed to book seat 
//		boolean isExtendedExamRegistrationLive = eDao.isExtendedExamRegistrationConfigurationLive("Exam Registration");
//
//		if(sapIdFromURL != null && sapid.equals(sapIdFromURL) && isExtendedExamRegistrationLive){
//			//If additional encrypted parameter is sent in URL, then allow to book after end date as well.
//			isExamRegistraionLive = true;
//		}
//		try {
//			if (isStudentValid(student, sapid)) {
//				
//				if(!isExamRegistraionLive){
//					setError(request, "Exam Registration is not Live Currently");
//				}
//			}
//			else{
//				setError(request, "Your validity date is expired.");
//				return false;
//			}
//			
//		} catch (Exception e) {
//			
//		}
//				
//		return isExamRegistraionLive;
//		/***** Logic for Allowing or Blocking Exam Registration : END *****/
//	}
	private boolean misExamRegistraionLive(String sapid) {
		return checkIfExamRegistrationLive(sapid, "");
	}
//	private boolean misExamRegistraionLive(String sapid) {
//		/***** Logic for Allowing or Blocking Exam Registration : START *****/
//		ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
//		boolean isExamRegistraionLive = eDao.isConfigurationLive("Exam Registration");
////		String sapIdEncrypted = request.getParameter("eid");
////		String sapIdFromURL = null;
////		try {
////			if(sapIdEncrypted != null){
////				sapIdFromURL = AESencrp.decrypt(sapIdEncrypted);
////			}
////		} catch (Exception e) {
////			
////		}
//		// checking if system.now is between extended date time after exam registration End allowed to book seat
//		if(!isExamRegistraionLive) {	// if examRegistrationLive is false then only check extended live flag 
//			boolean isExtendedExamRegistrationLive = eDao.isExtendedExamRegistrationConfigurationLive("Exam Registration");
//			
//			if(isExtendedExamRegistrationLive) {
//				isExamRegistraionLive = true;	// make live if extended startDate and endDate is set and currentDate in between
//			}
//		}
//		/*if(sapIdFromURL != null && sapid.equals(sapIdFromURL) && isExtendedExamRegistrationLive){
//			//If additional encrypted parameter is sent in URL, then allow to book after end date as well.
//			isExamRegistraionLive = true;
//		}
//		if(!isExamRegistraionLive){
////			setError(request, "Exam Registration is not Live currently");
//			isExamRegistraionLive = true;
//
//		}*/
//		return isExamRegistraionLive;
//		/***** Logic for Allowing or Blocking Exam Registration : END *****/
//	}

	private ArrayList<String> getFreeSubjects(StudentExamBean student) {
		ArrayList<String> freeSubjects = new ArrayList<>();
		ArrayList<StudentExamBean> exemptStudentList = getExemptStudentList();
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

	@RequestMapping(value = "/selectSubjects", method = {RequestMethod.POST})//changed to post since it was getting fired twice.
	public ModelAndView selectSubjects(HttpServletRequest request, HttpServletResponse response, @ModelAttribute ExamBookingExamBean examBooking) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		Integer provAdmission = ExamBookingController.toInteger(ExamBookingController.toString(request.getSession().getAttribute("isProvisionalAdmission")));
		if (HomeController.PROVISIONAL_ADMISSION_EXAMBOOKING_NOT_ALLOWED == provAdmission) {
			String redirectPath = null;
			StringBuffer strBuf = request.getRequestURL();
			logger.info("Student has Provisional Admission : " + provAdmission + ", Original RequestURL : " + strBuf);
			try {
				strBuf.replace(strBuf.lastIndexOf("/") + 1, strBuf.length(), "selectSubjectsForm");
				redirectPath = strBuf.toString();
				logger.info("Redirecting : " + redirectPath);
				response.sendRedirect(redirectPath);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				
				logger.error("Error while Redirecting : " + e.getMessage() + " StackTrace : " + e.getStackTrace());
			}
			return null;
		}

		ModelAndView modelnView = new ModelAndView("selectExamCenter");
		request.getSession().setAttribute("ddSeatBookingComplete", null);
		request.getSession().setAttribute("onlineSeatBookingComplete", null);
		request.getSession().setAttribute("freeSeatBookingComplete", null);
		
		try{
			ArrayList<String> subjects = examBooking.getApplicableSubjects();
			int totalExamFees = 0;
			HashMap<String,Integer> mapOfSubjectNameAndExamFee = (HashMap<String,Integer>)request.getSession().getAttribute("mapOfSubjectNameAndExamFee"); 
			for(String subject :subjects){
				totalExamFees = totalExamFees + mapOfSubjectNameAndExamFee.get(subject);
			}
			request.getSession().setAttribute("totalExamFees", totalExamFees);
			//Remove duplicates using Set
			Set<String> set = new HashSet<String>(subjects);
			subjects = new ArrayList<String>(set);
			request.getSession().setAttribute("subjects", subjects);

			StudentExamBean student = (StudentExamBean)request.getSession().getAttribute("studentExam");
			String studentProgramStructure = student.getPrgmStructApplicable();

			ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
			List<TimetableBean> timeTableList = dao.getTimetableListForGivenSubjects(subjects,student);
			request.getSession().setAttribute("timeTableList", timeTableList);
			

			ExamCenterBean examCenter = new ExamCenterBean();
			examCenter.setPaymentMode("Online");
			modelnView.addObject("examCenter", examCenter);

			modelnView.addObject("subjects", subjects);
			modelnView.addObject("noOfSubjects", subjects.size()+"");
			//modelnView.addObject("examFeesPerSubject", examFeesPerSubject);
			//modelnView.addObject("totalFees", (examFeesPerSubject * subjects.size()) + "");
			modelnView.addObject("totalFees", totalExamFees + "");


			getAvailableCenters(request, timeTableList, student);

			modelnView.addObject("examBooking", examBooking);
			
			ebAuditLogger.info("{} selectSubjects - totalExamFees: {} || subjects : {}",
									student.getSapid(),
									totalExamFees,
									subjects);


		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in getting subjects.");
			
			ebAuditLogger.error("{} ERROR processing selectSubjects - {}", 
								(String)request.getSession().getAttribute("userId"),
								Throwables.getStackTraceAsString(e));
			
			modelnView = new ModelAndView("selectSubjects");
		}
		return modelnView;
	}
	
	
//	to be deleted, api shifted to rest controller
//	@RequestMapping(value = "/m/selectSubjects", method = RequestMethod.POST, consumes="application/json", produces="application/json")
//	public ResponseEntity<ExamSelectSubjectBeanAPIResponse> mselectSubjects(@RequestBody ExamRegistrationBeanAPIRequest input) {
//		ExamSelectSubjectBeanAPIResponse response = new ExamSelectSubjectBeanAPIResponse();
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json");
////		request.getSession().setAttribute("ddSeatBookingComplete", null);
////		request.getSession().setAttribute("onlineSeatBookingComplete", null);
////		request.getSession().setAttribute("freeSeatBookingComplete", null);
//// 
//		if(input.getSapid() == null) {
//			response.setStatus("fail");
//			response.setError("Invalid sapId found");
//			return new ResponseEntity<ExamSelectSubjectBeanAPIResponse>(response, HttpStatus.OK);
//		}
//		
//		if(input.getApplicableSubjects() == null) {
//			response.setStatus("fail");
//			response.setError("Invalid applicationSubjectList found");
//			return new ResponseEntity<ExamSelectSubjectBeanAPIResponse>(response, HttpStatus.OK);
//		}
//		
//		if(input.getMapOfSubjectNameAndExamFee() == null) {
//			response.setStatus("fail");
//			response.setError("Invalid mapOfSubjectNameAndExamFee found");
//			return new ResponseEntity<ExamSelectSubjectBeanAPIResponse>(response, HttpStatus.OK);
//		}
//		
//		try{
//			ArrayList<String> subjects = new ArrayList<String>();
//			subjects.addAll(input.getApplicableSubjects());
//			int totalExamFees = 0;
//			
//			HashMap<String,Integer> mapOfSubjectNameAndExamFee = (HashMap<String,Integer>)input.getMapOfSubjectNameAndExamFee(); 
//			for(String subject :subjects){
//				totalExamFees = totalExamFees + mapOfSubjectNameAndExamFee.get(subject);
//			}
//			response.setTotalExamFees(totalExamFees);
//			//Remove duplicates using Set
//			Set<String> set = new HashSet<String>(subjects);
//			subjects = new ArrayList<String>(set);
//			ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
//			StudentBean student ;
//			student = dao.getSingleStudentsData(input.getSapid());
//			//String studentProgramStructure = student.getPrgmStructApplicable();
//			List<TimetableBean> timeTableList = dao.getTimetableListForGivenSubjects(subjects,student);
//			response.setTimeTableList(timeTableList); 
//			ExamCenterBean examCenter = new ExamCenterBean();
//			examCenter.setPaymentMode("Online");
//			response.setExamCenter(examCenter);
//			response.setSubjects(subjects);
//			response.setNoOfSubjects(subjects.size());
//			//modelnView.addObject("examFeesPerSubject", examFeesPerSubject);
//			//modelnView.addObject("totalFees", (examFeesPerSubject * subjects.size()) + "");
//			Map<String, List<ExamCenterBean>> centerSubjectMapping = mgetAvailableCenters(input, timeTableList, student, subjects);	
//			response.setCenterSubjectMapping(centerSubjectMapping);
//			response.setStatus("success");
////			modelnView.addObject("examBooking", examBooking);
//		}catch(Exception e){
//			
//			response.setStatus("fail");
//			response.setError(e.getMessage());
//			//request.setAttribute("error", "true");
//			//request.setAttribute("errorMessage", "Error in getting subjects.");
//			//modelnView = new ModelAndView("selectSubjects");
//		}
//		return new ResponseEntity<ExamSelectSubjectBeanAPIResponse>(response, headers, HttpStatus.OK);
//	}

	/*
	@RequestMapping(value = "/selectPaymentMode", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView selectPaymentMode(HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		try{
			ArrayList<String> subjects = (ArrayList<String>)request.getSession().getAttribute("subjects");
			ExamBookingBean examBooking = new ExamBookingBean();
			examBooking.setApplicableSubjects(subjects);
			return selectSubjects(request, response, examBooking);

		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in getting subjects.");
		}
		return selectSubjectsForm(request, response);
	}

	@RequestMapping(value = "/selectExamCenter", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView selectExamCenter(HttpServletRequest request, HttpServletResponse response, Model m) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}



		ArrayList<String> subjects = (ArrayList<String>)request.getSession().getAttribute("subjects");

		int totalExamFees = (Integer)request.getSession().getAttribute("totalExamFees");
		String paymentMode = request.getParameter("paymentMode");

		if("DD".equalsIgnoreCase(paymentMode)){
			ModelAndView modelnView = new ModelAndView("ddDetails");
			DDDetails ddDetails = new DDDetails();
			//ddDetails.setAmount(examFeesPerSubject * subjects.size()+"");
			ddDetails.setAmount(totalExamFees +"");
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
	 */


	@RequestMapping(value = "/selectExamCenterSinceSeatNotAvailable", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView selectExamCenterSinceSeatNotAvailable(HttpServletRequest request, HttpServletResponse response, Model m) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ArrayList<String> subjects = (ArrayList<String>)request.getSession().getAttribute("subjects");
		int totalExamFees = (Integer)request.getSession().getAttribute("totalExamFees");

		String paymentMode = request.getParameter("paymentMode");
		if("DD".equalsIgnoreCase(paymentMode)){
			ModelAndView modelnView = new ModelAndView("ddDetails");
			DDDetails ddDetails = new DDDetails();
			//ddDetails.setAmount(examFeesPerSubject * subjects.size()+"");
			ddDetails.setAmount(totalExamFees+"");
			modelnView.addObject("ddDetails", ddDetails);
			return modelnView;
		}else if("Online".equalsIgnoreCase(paymentMode)){
			List<TimetableBean> timeTableList = (List<TimetableBean>)request.getSession().getAttribute("timeTableList");
			StudentExamBean student = (StudentExamBean)request.getSession().getAttribute("studentExam");
			getAvailableCenters(request, timeTableList, student);
		}

		ExamBookingExamBean examBooking = new ExamBookingExamBean();

		ModelAndView modelnView = new ModelAndView("selectExamCenter");
		modelnView.addObject("examBooking", examBooking);


		return modelnView;
	}


	@RequestMapping(value = "/selectExamCenterForDD", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView selectExamCenterForDD(HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ArrayList<String> subjects = (ArrayList<String>)request.getSession().getAttribute("approvedDDSubjects");
		request.getSession().setAttribute("subjects", subjects);

		StudentExamBean student = (StudentExamBean)request.getSession().getAttribute("studentExam");
		String studentProgramStructure = student.getPrgmStructApplicable();

		ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");

		List<TimetableBean> timeTableList = dao.getTimetableListForGivenSubjects(subjects,student);
		request.getSession().setAttribute("timeTableList", timeTableList);

		getAvailableCenters(request, timeTableList, student);

		request.setAttribute("ddPaid", "true");

		ExamBookingExamBean examBooking = new ExamBookingExamBean();
		ModelAndView modelnView = new ModelAndView("selectExamCenter");
		modelnView.addObject("examBooking", examBooking);
		return modelnView;
	}

	@RequestMapping(value = "/selectExamCenterForOnline", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView selectExamCenterForOnline(HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}

		ArrayList<String> subjects = (ArrayList<String>)request.getSession().getAttribute("approvedOnlineTransactionSubjects");
		request.getSession().setAttribute("subjects", subjects);

		StudentExamBean student = (StudentExamBean)request.getSession().getAttribute("studentExam");
		String studentProgramStructure = student.getPrgmStructApplicable();
		ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");

		List<TimetableBean> timeTableList = dao.getTimetableListForGivenSubjects(subjects,student);
		request.getSession().setAttribute("timeTableList", timeTableList);

		getAvailableCenters(request, timeTableList, student);
		
		request.setAttribute("hasApprovedOnlineTransactions", "true");

		ExamBookingExamBean examBooking = new ExamBookingExamBean();
		ModelAndView modelnView = new ModelAndView("selectExamCenter");
		modelnView.addObject("examBooking", examBooking);
		
		ebAuditLogger.info("{} selectExamCenterForOnline - subjects : {}",
								student.getSapid(),
								subjects);
		
		return modelnView;
	}

	@RequestMapping(value = "/selectExamCenterForRelesedSubjects", method = {RequestMethod.GET,RequestMethod.POST})
	public ModelAndView selectExamCenterForRelesedSubjects(HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		ModelAndView modelnView = new ModelAndView("selectExamCenter");
		ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
		StudentExamBean student = (StudentExamBean)request.getSession().getAttribute("studentExam");

		if (request.getAttribute("selectionForReleasedSeats") == null) {
			boolean isExamRegistraionLive = isExamRegistraionLive(request, student.getSapid());
			if (!isExamRegistraionLive)
				request.setAttribute("isBookingSeatAfterExamIsNotLive", "true");
		}
		
		ArrayList<String> subjects = (ArrayList<String>)request.getSession().getAttribute("releasedSubjects");
		request.getSession().setAttribute("subjects", subjects);

		ebAuditLogger.info("{} selectExamCenterForRelesedSubjects - subjects : {}", student.getSapid(), subjects);
		
		String studentProgramStructure = student.getPrgmStructApplicable();

		List<TimetableBean> timeTableList = dao.getTimetableListForGivenSubjects(subjects,student);
		request.getSession().setAttribute("timeTableList", timeTableList);

		getAvailableCenters(request, timeTableList, student);
		request.setAttribute("hasReleasedSubjects", "true");

		ExamBookingExamBean examBooking = new ExamBookingExamBean();
		modelnView.addObject("examBooking", examBooking);
		return modelnView;
	}


	@RequestMapping(value = "/selectExamCenterForRelesedNoChargeSubjects", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView selectExamCenterForRelesedNoChargeSubjects(HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		List<TimetableBean> timeTableList = null;
		ArrayList<String> subjects = (ArrayList<String>)request.getSession().getAttribute("releasedNoChargeSubjects");
		request.getSession().setAttribute("subjects", subjects);

		StudentExamBean student = (StudentExamBean)request.getSession().getAttribute("studentExam");
		String studentProgramStructure = student.getPrgmStructApplicable();
		ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
		HashMap<String,String> corporateCenterUserMapping = (HashMap<String,String>)request.getSession().getAttribute("corporateCenterUserMapping");
		
		ebAuditLogger.info("{} - selectExamCenterForRelesedNoChargeSubjects - subjects  : {}",student.getSapid(), subjects);
		
		if(corporateCenterUserMapping.containsKey(student.getSapid())){
			timeTableList = dao.getTimeTableListForCorporateStudents(subjects,student);

		}else{
			timeTableList = dao.getTimetableListForGivenSubjects(subjects,student);

		}
		request.getSession().setAttribute("timeTableList", timeTableList);

		getAvailableCenters(request, timeTableList, student);
		request.setAttribute("hasReleasedNoChargeSubjects", "true");

		ExamBookingExamBean examBooking = new ExamBookingExamBean();
		ModelAndView modelnView = new ModelAndView("selectExamCenter");
		modelnView.addObject("examBooking", examBooking);
		return modelnView;
	}


	@RequestMapping(value = "/selectExamCenterForFree", method = {RequestMethod.POST})
	public ModelAndView selectExamCenterForFree(HttpServletRequest request, HttpServletResponse response, @ModelAttribute ExamBookingExamBean examBooking) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		List<TimetableBean> timeTableList = null;
		ArrayList<String> subjects = examBooking.getFreeApplicableSubjects();
		request.getSession().setAttribute("subjects", subjects);

		StudentExamBean student = (StudentExamBean)request.getSession().getAttribute("studentExam");
		String studentProgramStructure = student.getPrgmStructApplicable();
		ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
		HashMap<String,String> corporateCenterUserMapping = (HashMap<String,String>)request.getSession().getAttribute("corporateCenterUserMapping");

		ebAuditLogger.info("{} selectExamCenterForFree - subjects : {}", student.getSapid(), subjects);
		
		if(corporateCenterUserMapping.containsKey(student.getSapid())){
			timeTableList = dao.getTimeTableListForCorporateStudents(subjects,student);

		}else{
			timeTableList = dao.getTimetableListForGivenSubjects(subjects,student);

		}

		try{
			getAvailableCenters(request, timeTableList, student);
		}catch(Exception e){
			
		}

		//addProjectIfNeeded(timeTableList, subjects);
		request.getSession().setAttribute("timeTableList", timeTableList);


		request.setAttribute("hasFreeSubjects", "true");

		examBooking = new ExamBookingExamBean();
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
		if(subjects.contains("Module 4 - Project")){
			TimetableBean bean = new TimetableBean();
			bean.setSubject("Module 4 - Project");
			bean.setSem("2");
			bean.setDate("NA");
			bean.setStartTime("NA");
			bean.setEndTime("NA");
			timeTableList.add(bean);
		}
		
	}

	private void getAvailableCenters(HttpServletRequest request, List<TimetableBean> timeTableList, StudentExamBean student) {
		ExamCenterDAO ecDao = (ExamCenterDAO)act.getBean("examCenterDAO");
		List<ExamCenterBean> offlineExamCenters = new ArrayList<>();
		List<ExamCenterBean> corporateExamCenters = new ArrayList<>();
		String centerId = "";
		ArrayList<String> subjects  = new ArrayList<String>();
		Map<String, List<ExamCenterBean>> subjectAvailableCentersMap = new HashMap<String, List<ExamCenterBean>>();
		HashMap<String,String> corporateCenterUserMapping = (HashMap<String,String>)request.getSession().getAttribute("corporateCenterUserMapping");

		if(!corporateCenterUserMapping.containsKey(student.getSapid())){
			if("Online".equals(student.getExamMode())){
				subjects = (ArrayList<String>)request.getSession().getAttribute("subjects");
				subjectAvailableCentersMap = ecDao.getAvailableCentersForGivenSubjects(subjects,student.getSapid());
			}else{
				offlineExamCenters = ecDao.getAllOfflineExamCenters();

				for (int i = 0; i < timeTableList.size(); i++) {
					TimetableBean bean = timeTableList.get(i);
					subjectAvailableCentersMap.put(bean.getSubject() + bean.getStartTime(),  offlineExamCenters);
				}
			}
		}else if(corporateCenterUserMapping.containsKey(student.getSapid()) && student.getConsumerType().equals("Diageo")){
			subjects = (ArrayList<String>)request.getSession().getAttribute("subjects");
			subjectAvailableCentersMap = ecDao.getAvailableCentersForGivenSubjectsCorporateDiageo(subjects,student.getSapid());
		} else {
			centerId = corporateCenterUserMapping.get(student.getSapid());
			subjects = (ArrayList<String>)request.getSession().getAttribute("subjects");
			subjectAvailableCentersMap = ecDao.getAvailableCentersForGivenSubjectsCorporate(subjects,centerId);
		}
		
		request.getSession().setAttribute("subjectAvailableCentersMap", subjectAvailableCentersMap);

	}
	
	
	private Map<String, List<ExamCenterBean>> mgetAvailableCenters(ExamRegistrationBeanAPIRequest input, List<TimetableBean> timeTableList, StudentExamBean student, ArrayList<String> subject) {
		ExamCenterDAO ecDao = (ExamCenterDAO)act.getBean("examCenterDAO");
		List<ExamCenterBean> offlineExamCenters = new ArrayList<>();
		String centerId = "";
		ArrayList<String> subjects  = new ArrayList<String>();
		subjects.addAll(input.getApplicableSubjects());
		Map<String, List<ExamCenterBean>> subjectAvailableCentersMap = new HashMap<String, List<ExamCenterBean>>();
		HashMap<String,String> corporateCenterUserMapping = getCorporateCenterUserMapping();

		if(!corporateCenterUserMapping.containsKey(student.getSapid())){
			if("Online".equals(student.getExamMode())){
				subjectAvailableCentersMap = ecDao.getAvailableCentersForGivenSubjects(subjects);
			}else{
				offlineExamCenters = ecDao.getAllOfflineExamCenters();

				for (int i = 0; i < timeTableList.size(); i++) {
					TimetableBean bean = timeTableList.get(i);
					subjectAvailableCentersMap.put(bean.getSubject() + bean.getStartTime(),  offlineExamCenters);
				}
			}
		}
		else if(corporateCenterUserMapping.containsKey(student.getSapid()) && student.getConsumerType().equals("Diageo")) {
			subjectAvailableCentersMap = ecDao.getAvailableCentersForGivenSubjectsCorporateDiageo(subjects,student.getSapid());
		}
		else{
			centerId = corporateCenterUserMapping.get(student.getSapid());
			subjectAvailableCentersMap = ecDao.getAvailableCentersForGivenSubjectsCorporate(subjects,centerId);
		}
		
		
		
		return subjectAvailableCentersMap;

	}



	@RequestMapping(value = "/saveSeatsForDD", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView saveSeatsForDD(HttpServletRequest request, HttpServletResponse response,	@ModelAttribute ExamBookingExamBean examBooking, ModelMap model) throws ParseException {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}

		ModelAndView modelnView = new ModelAndView("bookingStatus");
		String ddSeatBookingComplete = (String)request.getSession().getAttribute("ddSeatBookingComplete");
		if("true".equals(ddSeatBookingComplete)){
			return modelnView;
		}

		String sapid = (String)request.getSession().getAttribute("userId");
		ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
		ExamCenterDAO edao = (ExamCenterDAO)act.getBean("examCenterDAO");
		
		/*int[] clearingResult = dao.clearOldOnlineInitiationTransaction(sapid, null);
		 * */

		List<ExamBookingTransactionBean> bookingsList = new ArrayList<>();

		ArrayList<String> subjects = (ArrayList<String>)request.getSession().getAttribute("subjects");

		ArrayList<String> selectedCenters = examBooking.getSelectedCenters();


		List<TimetableBean> timeTableList = (List<TimetableBean>)request.getSession().getAttribute("timeTableList");
		Map<String, TimetableBean> subjectTimetableMap = getSubjectTimetableMap(timeTableList);
		StudentExamBean student = (StudentExamBean)request.getSession().getAttribute("studentExam");
		for (int i = 0; i < selectedCenters.size(); i++) {
			String subjectCenter = selectedCenters.get(i);
			/*String[] parts = subjectCenter.split("\\|");

			//String subject = subjectCenter.substring(0,subjectCenter.indexOf("|"));
			//String centerId = subjectCenter.substring(subjectCenter.indexOf("|")+1, subjectCenter.length() );

			String subject = parts[0];
			String centerId = parts[1];
			String startTime = parts[2];

			 * */
			ExamBookingTransactionBean bean = new ExamBookingTransactionBean();
			populateExamBookingBean(bean, subjectCenter, subjectTimetableMap, student);

			/*TimetableBean ttBean = subjectTimetableMap.get(subject + startTime);

			bean.setSapid(sapid);
			bean.setSubject(subject);
			bean.setCenterId(centerId);
			bean.setExamDate(ttBean.getDate());
			bean.setExamTime(ttBean.getStartTime());
			bean.setExamEndTime(ttBean.getEndTime());*/

			bookingsList.add(bean);
		}
		int year = Calendar.getInstance().get(Calendar.YEAR);
		String examYear = year+"";
		if(subjects.contains("Project")){
			ExamBookingTransactionBean bean = new ExamBookingTransactionBean();

			bean.setSapid(sapid);
			bean.setSubject("Project");
			bean.setCenterId("-1");
			bean.setExamDate(dao.getLiveExamYear() + "/01/01");
			bean.setExamTime("00:00");
			bean.setExamEndTime("00:00");
			bookingsList.add(bean);
		}
		if(subjects.contains("Module 4 - Project")){
			ExamBookingTransactionBean bean = new ExamBookingTransactionBean();

			bean.setSapid(sapid);
			bean.setSubject("Module 4 - Project");
			bean.setCenterId("-1");
			bean.setExamDate(dao.getLiveExamYear() + "/01/01");
			bean.setExamTime("00:00");
			bean.setExamEndTime("00:00");
			bookingsList.add(bean);
		}
		

		boolean centerStillAvailable = checkIfCenterStillAvailable(student, timeTableList, selectedCenters,request);
		if(!centerStillAvailable){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Center you selected for one of the subject is no longer available. "
					+ "Please make fresh selection of exam centers");

			return selectExamCenterForDD(request, response);
		}


		List<ExamBookingTransactionBean> examBookings = dao.updateSeatsForDD(sapid, bookingsList, student.isCorporateExamCenterStudent());
		request.getSession().setAttribute("examBookings", examBookings);
		request.getSession().setAttribute("ddSeatBookingComplete", "true");
		request.setAttribute("success","true");
		request.setAttribute("successMessage",BOOKING_SUCCESS_MSG);

		MailSender mailSender = (MailSender)act.getBean("mailer");
		mailSender.sendBookingSummaryEmail(request, dao,edao);
		examBookingHelper.createAndUploadHallTicketAndExamFeeReceipt(sapid,student.isCorporateExamCenterStudent());
		return modelnView;
	}

	private String getEndTime(String examStartTime, StudentExamBean student) throws ParseException {

		char CertificateChar='C';
		String program = student.getProgram();
		String programStructure = student.getPrgmStructApplicable();
		//String key = program + "-" + programStructure;
		String key = student.getConsumerProgramStructureId();
		int examDurationInMinutes = Integer.parseInt(getProgramDetails().get(key).getExamDurationInMinutes());
		SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
		Date d = df.parse(examStartTime); 
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		//cal.add(Calendar.MINUTE, examDurationInMinutes);//2 and half hours of exam
		if((Character.compare(program.charAt(0),CertificateChar)==0) && (programStructure.equalsIgnoreCase("Jul2019") || programStructure.equalsIgnoreCase("Jul2020"))) {
			int extendedExamDurationCertificate = examDurationInMinutes+30;
			cal.add(Calendar.MINUTE, extendedExamDurationCertificate);
		}
		else{
			cal.add(Calendar.MINUTE, examDurationInMinutes);
		}
		String endTime = df.format(cal.getTime());

		return endTime;
	}

	@RequestMapping(value = "/saveSeatsForOnline", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView saveSeatsForOnline(HttpServletRequest request, HttpServletResponse response,	@ModelAttribute ExamBookingExamBean examBooking, ModelMap model) throws ParseException {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView modelnView = new ModelAndView("bookingStatus");
		String onlineSeatBookingComplete = (String)request.getSession().getAttribute("onlineSeatBookingComplete");
		if("true".equals(onlineSeatBookingComplete)){
			return modelnView;
		}

		String sapid = (String)request.getSession().getAttribute("userId");
		ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
		ExamCenterDAO edao = (ExamCenterDAO)act.getBean("examCenterDAO");
		List<ExamBookingTransactionBean> bookingsList = new ArrayList<>();

		ArrayList<String> subjects = (ArrayList<String>)request.getSession().getAttribute("subjects");

		ArrayList<String> selectedCenters = examBooking.getSelectedCenters();

		/*HashMap<String,String> corporateCenterUserMapping = (HashMap<String,String>)request.getSession().getAttribute("corporateCenterUserMapping");
		boolean isCorporate = false;
		if(corporateCenterUserMapping.containsKey(sapid)){
			isCorporate = true;
		}
		 */
		List<TimetableBean> timeTableList = (List<TimetableBean>)request.getSession().getAttribute("timeTableList");
		Map<String, TimetableBean> subjectTimetableMap = getSubjectTimetableMap(timeTableList);
		StudentExamBean student = (StudentExamBean)request.getSession().getAttribute("studentExam");

		for (int i = 0; i < selectedCenters.size(); i++) {
			String subjectCenter = selectedCenters.get(i);

			/*String[] parts = subjectCenter.split("\\|");

			String subject = parts[0];
			String centerId = parts[1];
			String startTime = parts[2];*/


			ExamBookingTransactionBean bean = new ExamBookingTransactionBean();

			populateExamBookingBean(bean, subjectCenter, subjectTimetableMap, student);
			bean.setYear(dao.getLiveExamYear());
			bean.setMonth(dao.getLiveExamMonth());
			/*			
			TimetableBean ttBean = subjectTimetableMap.get(subject + startTime);

			bean.setSapid(sapid);
			bean.setSubject(subject);
			bean.setCenterId(centerId);
			bean.setExamDate(ttBean.getDate());
			bean.setExamTime(ttBean.getStartTime());
			bean.setExamEndTime(ttBean.getEndTime());*/
			bookingsList.add(bean);
		}

		int year = Calendar.getInstance().get(Calendar.YEAR);

		String examYear = year+"";

		if(subjects.contains("Project")){
			ExamBookingTransactionBean bean = new ExamBookingTransactionBean();

			bean.setSapid(sapid);
			bean.setSubject("Project");
			bean.setCenterId("-1");
			bean.setExamDate(dao.getLiveExamYear() + "/01/01");
			bean.setExamTime("00:00");
			bean.setExamEndTime("00:00");
			bean.setYear(dao.getLiveExamYear());
			bean.setMonth(dao.getLiveExamMonth());
			bookingsList.add(bean);
		}
		if(subjects.contains("Module 4 - Project")){
			ExamBookingTransactionBean bean = new ExamBookingTransactionBean();

			bean.setSapid(sapid);
			bean.setSubject("Module 4 - Project");
			bean.setCenterId("-1");
			bean.setExamDate(dao.getLiveExamYear() + "/01/01");
			bean.setExamTime("00:00");
			bean.setExamEndTime("00:00");
			bean.setYear(dao.getLiveExamYear());
			bean.setMonth(dao.getLiveExamMonth());
			bookingsList.add(bean);
		}
		
		boolean centerStillAvailable = checkIfCenterStillAvailable(student, timeTableList, selectedCenters,request);
		if(!centerStillAvailable){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Center you selected for one of the subject is no longer available. "
					+ "Please make fresh selection of exam centers");

			return selectExamCenterForOnline(request, response);
		}

		List<ExamBookingTransactionBean> examBookings = dao.updateSeatsForOnlineApprovedTransaction(sapid, bookingsList, student.isCorporateExamCenterStudent());
		request.getSession().setAttribute("examBookings", examBookings);
		request.getSession().setAttribute("onlineSeatBookingComplete", "true");

		examRegisterlogger.info("Real Time Registartion called from saveSeatsForOnline method"+dao.getIsExtendedExamRegistrationLiveForRealTime());
		if(dao.getIsExtendedExamRegistrationLiveForRealTime()) {
			examRegisterlogger.info("Real Time Registartion called");
			examRegistrationRealTimeStrategy.registrationOnMettlAndReleaseBooking(bookingsList,null);
			}
		
		request.setAttribute("success","true");
		request.setAttribute("successMessage",BOOKING_SUCCESS_MSG);
		MailSender mailSender = (MailSender)act.getBean("mailer");
		mailSender.sendBookingSummaryEmail(request, dao, edao);

		examBookingHelper.createAndUploadHallTicketAndExamFeeReceipt(sapid,student.isCorporateExamCenterStudent());

		return modelnView;
	}


	private void populateExamBookingBean(ExamBookingTransactionBean bean, String subjectCenter,Map<String, TimetableBean> subjectTimetableMap, StudentExamBean student) throws ParseException {
		String[] parts = subjectCenter.split("\\|");
		String subject = parts[0];
		String centerId = parts[1];
		String startTime = parts[2];
		String examDate = parts[3];

		bean.setSapid(student.getSapid());
		bean.setSubject(subject);
		bean.setCenterId(centerId);

		boolean isOnline = false;
		//if("Jul2014".equalsIgnoreCase(student.getPrgmStructApplicable()) || "Jul2013".equalsIgnoreCase(student.getPrgmStructApplicable())){
		if("Online".equals(student.getExamMode())){
			isOnline = true;
		}


		if(isOnline){
			//String examDate = parts[3]; Changed temporarily for dec2018 exam
			bean.setExamDate(examDate);
			bean.setExamTime(startTime);
			String examEndTime = getEndTime(startTime, student);
			bean.setExamEndTime(examEndTime);
		}else{
			TimetableBean ttBean = subjectTimetableMap.get(subject + startTime + examDate); //Changed temporarily for dec2018 exam added examDate
			bean.setExamDate(ttBean.getDate());
			bean.setExamTime(ttBean.getStartTime());
			bean.setExamEndTime(ttBean.getEndTime());
		}
	}

	@RequestMapping(value = "/saveSeatsForReleasedSeatsNoCharges", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView saveSeatsForReleasedSeatsNoCharges(HttpServletRequest request, HttpServletResponse response,	@ModelAttribute ExamBookingExamBean examBooking, ModelMap model) throws ParseException {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView modelnView = new ModelAndView("bookingStatus");
		String onlineSeatBookingComplete = (String)request.getSession().getAttribute("onlineSeatBookingComplete");
		if("true".equals(onlineSeatBookingComplete)){
			return modelnView;
		}

		String sapid = (String)request.getSession().getAttribute("userId");
		ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
		ExamCenterDAO edao = (ExamCenterDAO)act.getBean("examCenterDAO");
		List<ExamBookingTransactionBean> bookingsList = new ArrayList<>();
		

		ArrayList<String> subjects = (ArrayList<String>)request.getSession().getAttribute("subjects");

		ArrayList<String> selectedCenters = examBooking.getSelectedCenters();
		
		ebAuditLogger.info("{} saveSeatsForReleasedSeatsNoCharges - subjects : {} || selectedCenters : {}",
								sapid,
								subjects,
								selectedCenters);

		/*HashMap<String,String> corporateCenterUserMapping = (HashMap<String,String>)request.getSession().getAttribute("corporateCenterUserMapping");
		boolean isCorporate = false;
		if(corporateCenterUserMapping.containsKey(sapid)){
			isCorporate = true;
		}*/

		List<TimetableBean> timeTableList = (List<TimetableBean>)request.getSession().getAttribute("timeTableList");
		Map<String, TimetableBean> subjectTimetableMap = getSubjectTimetableMap(timeTableList);
		StudentExamBean student = (StudentExamBean)request.getSession().getAttribute("studentExam");
		for (int i = 0; i < selectedCenters.size(); i++) {
			String subjectCenter = selectedCenters.get(i);
			/*
			String[] parts = subjectCenter.split("\\|");
			//String subject = subjectCenter.substring(0,subjectCenter.indexOf("|"));
			//String centerId = subjectCenter.substring(subjectCenter.indexOf("|")+1, subjectCenter.length() );

			String subject = parts[0];
			String centerId = parts[1];
			String startTime = parts[2];

			 * */
			ExamBookingTransactionBean bean = new ExamBookingTransactionBean();
			populateExamBookingBean(bean, subjectCenter, subjectTimetableMap, student);
			bean.setYear(dao.getLiveExamYear());
			bean.setMonth(dao.getLiveExamMonth());
			/*
			TimetableBean ttBean = subjectTimetableMap.get(subject + startTime);

			bean.setSapid(sapid);
			bean.setSubject(subject);
			bean.setCenterId(centerId);
			bean.setExamDate(ttBean.getDate());
			bean.setExamTime(ttBean.getStartTime());
			bean.setExamEndTime(ttBean.getEndTime());*/
			bookingsList.add(bean);
		}


		if(subjects.contains("Project")){
			ExamBookingTransactionBean bean = new ExamBookingTransactionBean();

			bean.setSapid(sapid);
			bean.setSubject("Project");
			bean.setCenterId("-1");
			bean.setExamDate(dao.getLiveExamYear() + "/01/01");
			bean.setExamTime("00:00");
			bean.setExamEndTime("00:00");
			bean.setYear(dao.getLiveExamYear());
			bean.setMonth(dao.getLiveExamMonth());
			bookingsList.add(bean);
		}
		if(subjects.contains("Module 4 - Project")){
			ExamBookingTransactionBean bean = new ExamBookingTransactionBean();

			bean.setSapid(sapid);
			bean.setSubject("Module 4 - Project");
			bean.setCenterId("-1");
			bean.setExamDate(dao.getLiveExamYear() + "/01/01");
			bean.setExamTime("00:00");
			bean.setExamEndTime("00:00");
			bean.setYear(dao.getLiveExamYear());
			bean.setMonth(dao.getLiveExamMonth());
			bookingsList.add(bean);
		}
		
		boolean centerStillAvailable = checkIfCenterStillAvailable(student, timeTableList, selectedCenters,request);
		if(!centerStillAvailable){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Center you selected for one of the subject is no longer available. "
					+ "Please make fresh selection of exam centers");

			return selectExamCenterForRelesedNoChargeSubjects(request, response);
		}

		List<ExamBookingTransactionBean> examBookings = dao.updateSeatsForRealeasedNoCharges(sapid, bookingsList, student.isCorporateExamCenterStudent());
		request.getSession().setAttribute("examBookings", examBookings);
		request.getSession().setAttribute("onlineSeatBookingComplete", "true");
		
		examRegisterlogger.info("Real Time Registartion called from saveSeatsForReleasedSeatsNoCharges method"+dao.getIsExtendedExamRegistrationLiveForRealTime());
		if(dao.getIsExtendedExamRegistrationLiveForRealTime()) {
			examRegisterlogger.info("Real Time Registartion called");
			examRegistrationRealTimeStrategy.registrationOnMettlAndReleaseBooking(bookingsList,null);
			}

		request.setAttribute("success","true");
		request.setAttribute("successMessage",BOOKING_SUCCESS_MSG);
		MailSender mailSender = (MailSender)act.getBean("mailer");
		mailSender.sendBookingSummaryEmail(request, dao, edao);

		examBookingHelper.createAndUploadHallTicketAndExamFeeReceipt(sapid, student.isCorporateExamCenterStudent());

		return modelnView;
	}

	@RequestMapping(value = "/saveSeatsForFree", method = {RequestMethod.POST})
	public ModelAndView saveSeatsForFree(HttpServletRequest request, HttpServletResponse response,	@ModelAttribute ExamBookingExamBean examBooking, ModelMap model) throws ParseException {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}

		ModelAndView modelnView = new ModelAndView("bookingStatus");
		String freeSeatBookingComplete = (String)request.getSession().getAttribute("freeSeatBookingComplete");
		if("true".equals(freeSeatBookingComplete)){
			return modelnView;
		}

		StudentExamBean student = (StudentExamBean)request.getSession().getAttribute("studentExam");
		String sapid = (String)request.getSession().getAttribute("userId");
		ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
		ExamCenterDAO edao = (ExamCenterDAO)act.getBean("examCenterDAO");
		List<ExamBookingTransactionBean> bookingsList = new ArrayList<>();

		ArrayList<String> subjects = (ArrayList<String>)request.getSession().getAttribute("subjects");

		HashMap<String, ProgramSubjectMappingExamBean> subjectProgramSemMap = (HashMap<String, ProgramSubjectMappingExamBean>)
				request.getSession().getAttribute("subjectProgramSemMap");

		ArrayList<String> selectedCenters = examBooking.getSelectedCenters();
		List<TimetableBean> timeTableList = (List<TimetableBean>)request.getSession().getAttribute("timeTableList");

		Map<String, TimetableBean> subjectTimetableMap = getSubjectTimetableMap(timeTableList);

		/*HashMap<String,String> corporateCenterUserMapping = (HashMap<String,String>)request.getSession().getAttribute("corporateCenterUserMapping");
		boolean isCorporate = false;
		if(corporateCenterUserMapping.containsKey(sapid)){
			isCorporate = true;
		}*/

		String prgrmStructApplicable = student.getPrgmStructApplicable();

		int year = Calendar.getInstance().get(Calendar.YEAR);
		int month = Calendar.getInstance().get(Calendar.MONTH);

		String examYear = year+"";
		String examMonth = "";

		if(month > 6){
			examMonth = "Dec";
		}else{
			examMonth = "Jun";
		}
		List<String> examBookingTransactionBeans = edao.getBookedSubjectList(sapid, examYear, examMonth);

		String trackId = sapid + System.currentTimeMillis() ;

		List<String> subjectListInBean_tmp = new ArrayList<String>();  
		for (int i = 0; i < selectedCenters.size(); i++) {
			String subjectCenter = selectedCenters.get(i);

			String[] parts = subjectCenter.split("\\|");

			//String subject = subjectCenter.substring(0,subjectCenter.indexOf("|"));
			//String centerId = subjectCenter.substring(subjectCenter.indexOf("|")+1, subjectCenter.length() );

			String subject = parts[0];
			//String centerId = parts[1];
			//String startTime = parts[2];
			
			ExamBookingTransactionBean bean = new ExamBookingTransactionBean();
			populateExamBookingBean(bean, subjectCenter, subjectTimetableMap, student);

			/*TimetableBean ttBean = subjectTimetableMap.get(subject + startTime);
			examYear = ttBean.getExamYear();
			examMonth = ttBean.getExamMonth();*/


			/*bean.setSapid(sapid);
			bean.setSubject(subject);
			bean.setCenterId(centerId);
			bean.setExamDate(ttBean.getDate());
			bean.setExamTime(ttBean.getStartTime());
			bean.setExamEndTime(ttBean.getEndTime());*/


			bean.setYear(dao.getLiveExamYear());
			bean.setMonth(dao.getLiveExamMonth());
			bean.setProgram(student.getProgram());
			bean.setSem(subjectProgramSemMap.get(subject).getSem());

			bean.setTrackId(trackId);
			bean.setAmount("0");
			bean.setTranStatus(FEE_IN_ADMISSION);
			bean.setBooked("Y");
			bean.setPaymentMode("FREE");
			bean.setExamMode(student.getExamMode());
			
			if(!examBookingTransactionBeans.contains(bean.getSubject()) && !subjectListInBean_tmp.contains(bean.getSubject())) {
				bookingsList.add(bean);
				subjectListInBean_tmp.add(subject); 
			}
			
		}

		if(subjects.contains("Project")){
			ExamBookingTransactionBean bean = new ExamBookingTransactionBean();

			bean.setSapid(sapid);
			bean.setSubject("Project");
			bean.setCenterId("-1");
			bean.setYear(dao.getLiveExamYear());
			bean.setMonth(dao.getLiveExamMonth());
			bean.setProgram(student.getProgram());
			bean.setSem(eligibilityService.getProjectApplicableProgramSem(student.getProgram()));
			bean.setExamDate(dao.getLiveExamYear() + "/01/01");
			bean.setExamTime("00:00");
			bean.setExamEndTime("00:00");
			bean.setTrackId(trackId);
			bean.setAmount("0");
			bean.setTranStatus(FEE_IN_ADMISSION);
			bean.setBooked("Y");
			bean.setPaymentMode("FREE");
			bean.setExamMode(student.getExamMode());


			bookingsList.add(bean);
		}
		if(subjects.contains("Module 4 - Project")){
			ExamBookingTransactionBean bean = new ExamBookingTransactionBean();

			bean.setSapid(sapid);
			bean.setSubject("Module 4 - Project");
			bean.setCenterId("-1");
			bean.setYear(dao.getLiveExamYear());
			bean.setMonth(dao.getLiveExamMonth());
			bean.setProgram(student.getProgram());
			bean.setSem("2");
			bean.setExamDate(dao.getLiveExamYear() + "/01/01");
			bean.setExamTime("00:00");
			bean.setExamEndTime("00:00");
			bean.setTrackId(trackId);
			bean.setAmount("0");
			bean.setTranStatus(FEE_IN_ADMISSION);
			bean.setBooked("Y");
			bean.setPaymentMode("FREE");
			bean.setExamMode(student.getExamMode());


			bookingsList.add(bean);
		}

		
		boolean centerStillAvailable = checkIfCenterStillAvailable(student, timeTableList, selectedCenters,request);
		if(!centerStillAvailable){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Center you selected for one of the subject is no longer available. "
					+ "Please make fresh selection of exam centers");
			examBooking.setFreeApplicableSubjects(subjects);
			return selectExamCenterForFree(request, response, examBooking);
		}
		
		if(bookingsList.size() == 0) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No Booking Found or Subject Already Booked");
			return modelnView;
		}

		List<ExamBookingTransactionBean> examBookings = dao.insertSeatsForFreeSubjects(sapid, trackId, bookingsList, student.isCorporateExamCenterStudent());
		request.getSession().setAttribute("examBookings", examBookings);
		request.getSession().setAttribute("freeSeatBookingComplete", "true");
		
		examRegisterlogger.info("Real Time Registartion called from saveSeatsForFree method"+dao.getIsExtendedExamRegistrationLiveForRealTime());
		if(dao.getIsExtendedExamRegistrationLiveForRealTime()) {
			examRegisterlogger.info("Real Time Registartion called");
			examRegistrationRealTimeStrategy.registrationOnMettlAndReleaseBooking(bookingsList,null);
			}
		
		request.setAttribute("success","true");
		request.setAttribute("successMessage",BOOKING_SUCCESS_MSG);
		MailSender mailSender = (MailSender)act.getBean("mailer");
		mailSender.sendBookingSummaryEmail(request, dao, edao);

		examBookingHelper.createAndUploadHallTicketAndExamFeeReceipt(sapid, student.isCorporateExamCenterStudent());

		return modelnView;
	}


	@RequestMapping(value = "/saveDDDetails", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView saveDDDetails(HttpServletRequest request, HttpServletResponse response, @ModelAttribute DDDetails ddDetails) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		int totalExamFees = (Integer)request.getSession().getAttribute("totalExamFees");
		Map<String,String> mapOfSubjectNameAndExamFee = (Map<String,String>)request.getSession().getAttribute("mapOfSubjectNameAndExamFee");
		ModelAndView modelnView = new ModelAndView("ddPending");
		ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
		String sapid = (String)request.getSession().getAttribute("userId");

		HashMap<String, ProgramSubjectMappingExamBean> subjectProgramSemMap = (HashMap<String, ProgramSubjectMappingExamBean>)
				request.getSession().getAttribute("subjectProgramSemMap");


		ArrayList<String> bookdSubjects = dao.getSubjectsBooked(sapid);
		if(bookdSubjects == null){
			bookdSubjects = new ArrayList<>();
		}

		try{

			//String trackId = sapid+ddDetails.getDdno();
			String trackId = sapid + System.currentTimeMillis() ;
			//ArrayList<ProgramSubjectMappingBean> applicableSubjectsList = (ArrayList<ProgramSubjectMappingBean>)request.getSession().getAttribute("applicableSubjectsList");
			List<ExamBookingTransactionBean> bookingsList = new ArrayList<>();

			List<TimetableBean> timeTableList = (List<TimetableBean>)request.getSession().getAttribute("timeTableList");
			Map<String, TimetableBean> subjectTimetableMap = getSubjectTimetableMap(timeTableList);

			ArrayList<String> subjects = (ArrayList<String>)request.getSession().getAttribute("subjects");
			StudentExamBean student = (StudentExamBean)request.getSession().getAttribute("studenExam");
			String prgrmStructApplicable = student.getPrgmStructApplicable();

			String centerId = "-1"; //Center id not applicable since student is not yet allowed to select exam center

			/*int year = Calendar.getInstance().get(Calendar.YEAR);
			int month = Calendar.getInstance().get(Calendar.MONTH);

			String examYear = year+"";
			String examMonth = "";

			if(month > 6){
				examMonth = "Dec";
			}else{
				examMonth = "Jun";
			}

			 * */

			for (int i = 0; i < subjects.size(); i++) {
				String subject = subjects.get(i);
				if("Project".equalsIgnoreCase(subject)){
					continue;
				}				
				if("Module 4 - Project".equalsIgnoreCase(subject) && "PD - WM".equalsIgnoreCase(student.getProgram())){
					continue;
				}
				
				if(bookdSubjects.contains(subject)){
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "You have already entered DD details for "+subject+". Please select different subjects if you wish to enter new DD details.");
					return modelnView;
				}

				ExamBookingTransactionBean bean = new ExamBookingTransactionBean();

				TimetableBean ttBean = subjectTimetableMap.get(subject + "10:00:00");

				if(ttBean == null){
					ttBean = subjectTimetableMap.get(subject + "11:00:00");
				}

				if(ttBean == null){
					ttBean = subjectTimetableMap.get(subject + "15:00:00");
				}


				/*examYear = ttBean.getExamYear();
				examMonth = ttBean.getExamMonth();*/

				bean.setSapid(sapid);
				bean.setSubject(subject);
				bean.setCenterId(centerId);
				bean.setYear(dao.getLiveExamYear());
				bean.setMonth(dao.getLiveExamMonth());
				bean.setProgram(student.getProgram());
				bean.setSem(subjectProgramSemMap.get(subject).getSem());
				/*bean.setExamDate(ttBean.getDate());
				bean.setExamTime(ttBean.getStartTime());
				bean.setExamEndTime(ttBean.getEndTime());*/
				bean.setTrackId(trackId);
				//bean.setAmount(examFeesPerSubject * subjects.size()+"");
				bean.setAmount(totalExamFees +"");
				bean.setTranStatus(DD_APPROVAL_PENDING);
				bean.setBooked("P");
				bean.setDdno(ddDetails.getDdno());
				bean.setBank(ddDetails.getBank());
				bean.setDdAmount(ddDetails.getAmount());
				bean.setPaymentMode("DD");
				bean.setTrackId(trackId);
				bean.setDdDate(ddDetails.getDdDate());
				bean.setExamMode(student.getExamMode());
				/*if("Jul2014".equals(prgrmStructApplicable) || "Jul2013".equals(prgrmStructApplicable)){
					bean.setExamMode("Online");
				}else{
					bean.setExamMode("Offline");
				}*/

				bookingsList.add(bean);
			}


			if(subjects.contains("Project")){
				ExamBookingTransactionBean bean = new ExamBookingTransactionBean();

				bean.setSapid(sapid);
				bean.setSubject("Project");
				bean.setCenterId(centerId);
				bean.setYear(dao.getLiveExamYear());
				bean.setMonth(dao.getLiveExamMonth());
				bean.setProgram(student.getProgram());
				bean.setSem(eligibilityService.getProjectApplicableProgramSem(student.getProgram()));
				bean.setExamDate(dao.getLiveExamYear() + "/01/01");
				bean.setExamTime("00:00");
				bean.setExamEndTime("00:00");
				bean.setTrackId(trackId);
				//bean.setAmount(examFeesPerSubject * subjects.size()+"");
				bean.setAmount(totalExamFees +"");
				bean.setTranStatus(DD_APPROVAL_PENDING);
				bean.setBooked("P");
				bean.setDdno(ddDetails.getDdno());
				bean.setBank(ddDetails.getBank());
				bean.setDdAmount(ddDetails.getAmount());
				bean.setPaymentMode("DD");
				bean.setTrackId(trackId);
				bean.setDdDate(ddDetails.getDdDate());
				bean.setExamMode(student.getExamMode());

				/*if("Jul2014".equals(prgrmStructApplicable) || "Jul2013".equals(prgrmStructApplicable)){
					bean.setExamMode("Online");
				}else{
					bean.setExamMode("Offline");
				}*/
				bookingsList.add(bean);
			}
			
			if(subjects.contains("Module 4 - Project") && "PD - WM".equalsIgnoreCase(student.getProgram())){
				ExamBookingTransactionBean bean = new ExamBookingTransactionBean();

				bean.setSapid(sapid);
				bean.setSubject("Module 4 - Project");
				bean.setCenterId(centerId);
				bean.setYear(dao.getLiveExamYear());
				bean.setMonth(dao.getLiveExamMonth());
				bean.setProgram(student.getProgram());
				bean.setSem("2");
				bean.setExamDate(dao.getLiveExamYear() + "/01/01");
				bean.setExamTime("00:00");
				bean.setExamEndTime("00:00");
				bean.setTrackId(trackId);
				//bean.setAmount(examFeesPerSubject * subjects.size()+"");
				bean.setAmount(totalExamFees +"");
				bean.setTranStatus(DD_APPROVAL_PENDING);
				bean.setBooked("P");
				bean.setDdno(ddDetails.getDdno());
				bean.setBank(ddDetails.getBank());
				bean.setDdAmount(ddDetails.getAmount());
				bean.setPaymentMode("DD");
				bean.setTrackId(trackId);
				bean.setDdDate(ddDetails.getDdDate());
				bean.setExamMode(student.getExamMode());

				/*if("Jul2014".equals(prgrmStructApplicable) || "Jul2013".equals(prgrmStructApplicable)){
					bean.setExamMode("Online");
				}else{
					bean.setExamMode("Offline");
				}*/
				bookingsList.add(bean);
			}
			

			dao.insertExamBookingTransaction(bookingsList);
			ddDetails.setTranStatus(DD_APPROVAL_PENDING);
			modelnView.addObject("ddDetails", ddDetails);

		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in saving DD Details");
		}
		return modelnView;
	}

	@RequestMapping(value="/getAvailableCentersForCityForOnlineExam", method = {RequestMethod.GET, RequestMethod.POST})
	public @ResponseBody String getAvailableCentersForCity(HttpServletRequest request, HttpServletResponse response ) throws ParseException {
		String output = null;
		try {


			String subjectCenter = request.getParameter("depdrop_parents[0]"); //This is the name used by plugin
			String[] tempArray = subjectCenter.split("\\|");
			String subject = tempArray[0];
			String centerId = tempArray[1];

			ExamCenterDAO ecDao = (ExamCenterDAO)act.getBean("examCenterDAO");
			String sapid = (String)request.getSession().getAttribute("userId");
			StudentExamBean student = (StudentExamBean)request.getSession().getAttribute("studentExam");

			List<ExamCenterBean> availableCenters = new ArrayList<ExamCenterBean>();

			if(!student.getConsumerType().equals("Diageo")){
				availableCenters = ecDao.getAvailableCentersForRegularOnlineExam(sapid, student.isCorporateExamCenterStudent(), student.getMappedCorporateExamCenterId());

			} else {
				availableCenters = ecDao.getAvailableCentersForRegularOnlineExamCorporateDiageo(sapid, student.isCorporateExamCenterStudent(), student.getMappedCorporateExamCenterId());
			}	
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
	
//	@RequestMapping(value="/m/getAvailableCentersForCityForOnlineExam/v2", method = RequestMethod.POST, consumes="application/json", produces="application/json", headers="content-type=application/json")
//	public ResponseEntity<Map<String, Map<String, List<ExamCenterBean>>>> mgetAvailableCentersForCityV2(HttpServletRequest request,@RequestBody StudentBean input ) {
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type","application/json");
//
//		Map<String, Map<String, List<ExamCenterBean>>> response = new HashMap<String, Map<String, List<ExamCenterBean>>>();
//		String sapid = input.getSapid();
//		String centerId = input.getCenterCode();
//		try {
//			ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
//			ExamCenterDAO ecDao = (ExamCenterDAO)act.getBean("examCenterDAO");
//			StudentBean student = (StudentBean)request.getSession().getAttribute("student");
//			
//			//Check for Coporate Student Start
//			
//			//Generate Map of Center ID and Name based on if student is mapped to Corporate/Regular Exam Centers: START
//			corporateCenterUserMapping = getCorporateCenterUserMapping();//Get details of Students mapped to Corporate Exam Centers
//	
//			if(corporateCenterUserMapping.containsKey(sapid)){
//				student.setCorporateExamCenterStudent(true);
//				student.setMappedCorporateExamCenterId(corporateCenterUserMapping.get(sapid));
//				
//			}else {
//				student.setCorporateExamCenterStudent(false);
//			} 
//			//Check for Coporate Student End
//			
//			//List<ExamCenterBean> availableCenters = ecDao.getAvailableCentersForRegularOnlineExam(sapid, student.isCorporateExamCenterStudent(), student.getMappedCorporateExamCenterId());
//			//Create Exam Center Time List
//
//			List<ExamCenterBean> availableCenters = new ArrayList<ExamCenterBean>();
//			Date dateobj = new Date();
//			SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
//			String currentDate = sdfDate.format(dateobj);
////			if(!student.getConsumerType().equals("Diageo")){
//				availableCenters = ecDao.getAvailableCentersForRegularOnlineExam(sapid, student.isCorporateExamCenterStudent(), student.getMappedCorporateExamCenterId());
//				availableCenters.removeIf(c -> (currentDate.equals(c.getDate()) ));
////			} else {
////				availableCenters = ecDao.getAvailableCentersForRegularOnlineExamCorporateDiageo(sapid, student.isCorporateExamCenterStudent(), student.getMappedCorporateExamCenterId());
////			}	
//			
//			ArrayList<ExamCenterBean> listOfAvailableCenters = new ArrayList<ExamCenterBean>();
//			for (ExamCenterBean examCenterBean : availableCenters) {
//				if(examCenterBean.getCenterId().equals(centerId)){
//					listOfAvailableCenters.add(examCenterBean);
//				}
//			}
//
//			Map<Integer, Map<Integer, List<ExamCenterBean>>> yearWeekendAndCentersList = new HashMap<Integer, Map<Integer, List<ExamCenterBean>>>();
//			
//			for (ExamCenterBean examCenterBean : listOfAvailableCenters) {
//
//				
//				String dateStr = examCenterBean.getDate();
//				SimpleDateFormat inputDateFormatter = new SimpleDateFormat("yyyy-MM-dd");
//				Date date = inputDateFormatter.parse(dateStr);
//				
//				Calendar cal = Calendar.getInstance();
//				cal.setTime(date);
//				int week = cal.get(Calendar.WEEK_OF_YEAR);
//				int dayOfYear = cal.get(Calendar.DAY_OF_YEAR);
//				int dayNum = cal.get(Calendar.DAY_OF_WEEK);
//				int year = cal.get(Calendar.YEAR);
//
//				int weekendNumber = week;
//				
//				// for the last weekend of the year.
//				if(week == 1 && dayOfYear > 8) {
//					weekendNumber = 53;
//				}
//				
//				// if Sunday, subtract week number by 1
//				if(dayNum == 1) {
//					if(dayOfYear == 1) {
//						// for first day of the year, count it in the last weekend of the previous year.
//						year = year - 1;
//						weekendNumber = 53;
//					} else {
//						// count it in the previous weekend.
//						weekendNumber = weekendNumber - 1;
//					}
//				}
//				
//				// if data exists for this year
//				Map<Integer, List<ExamCenterBean>> weekendAndCentersList = yearWeekendAndCentersList.containsKey(year)
//						? yearWeekendAndCentersList.get(year) : new HashMap<Integer, List<ExamCenterBean>>();
//
//				// if a list for this weekend number exists, get the value, else create new list
//				List<ExamCenterBean> slotsForTheWeekend = weekendAndCentersList.containsKey(weekendNumber)
//						? weekendAndCentersList.get(weekendNumber) 
//						: new ArrayList<ExamCenterBean>();
//						
//				slotsForTheWeekend.add(examCenterBean);
//				weekendAndCentersList.put(weekendNumber, slotsForTheWeekend);
//				yearWeekendAndCentersList.put(year, weekendAndCentersList);
//			}
//			// Sort by Year
//			yearWeekendAndCentersList = new TreeMap<Integer, Map<Integer, List<ExamCenterBean>>>(yearWeekendAndCentersList);
//			
//			int i = 1;
//			for (Map.Entry<Integer, Map<Integer, List<ExamCenterBean>>> yearEntry  : yearWeekendAndCentersList.entrySet()) {
//
//				// Sort by weekend and 
//				Map<Integer, List<ExamCenterBean>> weekendAndCentersList = new TreeMap<Integer, List<ExamCenterBean>>(yearEntry.getValue());
//				for (Map.Entry<Integer, List<ExamCenterBean>> weekendEntry  : weekendAndCentersList.entrySet()) {
//					String weekendString = "" + i;
//					response.put(weekendString, getDateSortedSlotsList(weekendEntry.getValue()));
//					i++;
//				}
//			}
//			
//			// Sort by name(weekend number).
//			response = new TreeMap<String, Map<String, List<ExamCenterBean>>>(response);
//			
//			return new ResponseEntity<Map<String, Map<String, List<ExamCenterBean>>>>(response, headers, HttpStatus.OK);
//		} catch (Exception e) {
//			
//			return new ResponseEntity<Map<String, Map<String, List<ExamCenterBean>>>>(response, headers, HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//	}
	
	private Map<String, List<ExamCenterBean>> getDateSortedSlotsList(List<ExamCenterBean> slotList) {
		Map<String, List<ExamCenterBean>> toReturn = new HashMap<String, List<ExamCenterBean>>();
		
		for (ExamCenterBean examCenterBean : slotList) {
			String dateStr = examCenterBean.getDate();
			List<ExamCenterBean> slotsForDate = toReturn.containsKey(dateStr)
					? toReturn.get(dateStr) : new ArrayList<ExamCenterBean>();
			slotsForDate.add(examCenterBean);
			toReturn.put(dateStr, slotsForDate);
		}

		// now sort dates
		return new TreeMap<String, List<ExamCenterBean>>(toReturn);
	}
	
	
	/*@RequestMapping(value="/m/getAvailableCentersForCityForOnlineExam", method = RequestMethod.POST, consumes="application/json", produces="application/json", headers="content-type=application/json")
	public ResponseEntity<ArrayList<ExamCenterBean>> mgetAvailableCentersForCity(@RequestBody StudentBean input ) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type","application/json");
		ArrayList<ExamCenterBean> response = new ArrayList<ExamCenterBean>();
		if(input.getSapid() == null || input.getCenterCode() == null) {
			return new ResponseEntity<ArrayList<ExamCenterBean>>(response, HttpStatus.OK);
		}
		
		
		String sapid = input.getSapid();
		String centerId = input.getCenterCode();
		try {
			ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
			ExamCenterDAO ecDao = (ExamCenterDAO)act.getBean("examCenterDAO");
			StudentBean student = dao.getSingleStudentsData(sapid);
			
			//Check for Coporate Student Start
			
			//Generate Map of Center ID and Name based on if student is mapped to Corporate/Regular Exam Centers: START
			corporateCenterUserMapping = getCorporateCenterUserMapping();//Get details of Students mapped to Corporate Exam Centers
	
			if(corporateCenterUserMapping.containsKey(sapid)){
				student.setCorporateExamCenterStudent(true);
				student.setMappedCorporateExamCenterId(corporateCenterUserMapping.get(sapid));
				
			}else {
				student.setCorporateExamCenterStudent(false);
			} 
			//Check for Coporate Student End
			
			List<ExamCenterBean> availableCenters = ecDao.getAvailableCentersForRegularOnlineExam(sapid, student.isCorporateExamCenterStudent(), student.getMappedCorporateExamCenterId());
			//Create Exam Center Time List
			for (ExamCenterBean examCenterBean : availableCenters) {
				if(examCenterBean.getCenterId().equals(centerId)){
					response.add(examCenterBean);
				}
			}
			return new ResponseEntity<ArrayList<ExamCenterBean>>(response, headers, HttpStatus.OK);
		} catch (Exception e) {
			
			return new ResponseEntity<ArrayList<ExamCenterBean>>(response, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}*/
	
//	to be deleted, api shifted to rest controller
//	@RequestMapping(value="/m/getAvailableCentersForCityForOnlineExam", method = RequestMethod.POST, consumes="application/json", produces="application/json", headers="content-type=application/json")
//	public ResponseEntity<ArrayList<ExamCenterBean>> mgetAvailableCentersForCity(@RequestBody StudentBean input) {
//		try {
//			if(input.getSapid() == null || input.getCenterCode() == null) {
//				return new ResponseEntity<ArrayList<ExamCenterBean>>(new ArrayList<ExamCenterBean>(), HttpStatus.OK);
//			}
//			String centerId = input.getCenterCode();
//			String sapid = input.getSapid();
//			ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
//			ExamCenterDAO ecDao = (ExamCenterDAO)act.getBean("examCenterDAO");
//			StudentBean student = dao.getSingleStudentsData(sapid);
//			
//			List<ExamCenterBean> availableCenters = new ArrayList<ExamCenterBean>();
//			ArrayList<ExamCenterBean> responseCenter = new ArrayList<ExamCenterBean>();
//			if(!student.getConsumerType().equals("Diageo")){
//				availableCenters = ecDao.getAvailableCentersForRegularOnlineExam(sapid, student.isCorporateExamCenterStudent(), student.getMappedCorporateExamCenterId());
//
//			} else {
//				availableCenters = ecDao.getAvailableCentersForRegularOnlineExamCorporateDiageo(sapid, student.isCorporateExamCenterStudent(), student.getMappedCorporateExamCenterId());
//			}
//			
//			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//			SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE, dd-MMM-yyyy");
//			
//			for(ExamCenterBean examCenterBean : availableCenters) {
//				if(examCenterBean.getCenterId().equals(centerId)){
//					String city = examCenterBean.getCity();
//					String date = examCenterBean.getDate();
//					String startTime = examCenterBean.getStarttime();
//					int available = examCenterBean.getAvailable();
//					String capacity = examCenterBean.getCapacity();
//					Date formattedDate = formatter.parse(date);
//					String formattedDateString = dateFormatter.format(formattedDate);
//
//					String dropDownValue = startTime  + "|" + date + "|" + city;
//
//					String dropDownLabel = formattedDateString  + ", " + startTime + " (" + available + "/" + capacity + ")";
//					examCenterBean.setId(dropDownValue);
//					examCenterBean.setName(dropDownLabel);
//					responseCenter.add(examCenterBean);
//				}
//			}
//			return new ResponseEntity<ArrayList<ExamCenterBean>>(responseCenter, HttpStatus.OK);
//		}
//		catch (Exception e) {
//			// TODO: handle exception
//			
//			return new ResponseEntity<ArrayList<ExamCenterBean>>(new ArrayList<ExamCenterBean>(), HttpStatus.OK);
//		}
//	}
	
	
	public String getCommaSeparatedString(ArrayList<ExamBookingTransactionBean>  list){
		int count=0;
		String subjectCommaSeparted="";
		if(list !=null) {
			for(ExamBookingTransactionBean bean: list) {
				if(count == 0) {
					subjectCommaSeparted ="'"+bean.getSubject()+"'";
				}else {
					subjectCommaSeparted = subjectCommaSeparted+",'"+bean.getSubject()+"'";
				}
				count++;
			}
			return subjectCommaSeparted;
		}else {
			return "''";
		}
	}
	//check transaction Status
	@RequestMapping(value = "/billDeskStatus",method = {RequestMethod.GET})
	public ResponseEntity<String> billDeskStatus(HttpServletRequest request){
		String response = paymentHelper.getBillDeskTransactionStatus(request.getParameter("trackId"));
		return new ResponseEntity<String>(response, HttpStatus.OK);
	}

	/*
	 * This method will save selection from user for exam center. 
	 * Delete any old track ids 
	 * Generate new track id and save it in database
	 * Create transaction parameters and redirect to gateway.
	 * */
	
	@RequestMapping(value = "/goToGateway", method = {RequestMethod.POST})
	public ModelAndView goToGateway(HttpServletRequest request, HttpServletResponse response, 
			@ModelAttribute ExamBookingExamBean examBooking, ModelMap model/*,RedirectAttributes ra*/) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}

		ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
		boolean hasProject = false;
		int totalExamFees = (Integer)request.getSession().getAttribute("totalExamFees");
		String hasReleasedSubjects = request.getParameter("hasReleasedSubjects");
		String isBookingSeatAfterExamIsNotLive = request.getParameter("isBookingSeatAfterExamIsNotLive");
		ModelAndView modelnView = new ModelAndView("bookingStatus");
		String onlineSeatBookingComplete = (String)request.getSession().getAttribute("onlineSeatBookingComplete");
		if("true".equals(onlineSeatBookingComplete)){
			return modelnView;
		}

		int noOfSubjects = 0;
		//int feesToCharge = examFeesPerSubject;

		HashMap<String, ProgramSubjectMappingExamBean> subjectProgramSemMap = (HashMap<String, ProgramSubjectMappingExamBean>)
				request.getSession().getAttribute("subjectProgramSemMap");

		try{
			ArrayList<String> subjects = (ArrayList<String>)request.getSession().getAttribute("subjects");
			String sapid = (String)request.getSession().getAttribute("userId");
			sapid = sapid.trim();
			String trackId = sapid + System.currentTimeMillis() ;
			request.getSession().setAttribute("trackId", trackId);

			String message = "Exam fees for "+sapid;
			if("true".equalsIgnoreCase(hasReleasedSubjects)){
				message = "Exam Center Change fees for "+sapid;
			}
			List<TimetableBean> timeTableList = (List<TimetableBean>)request.getSession().getAttribute("timeTableList");
			Map<String, TimetableBean> subjectTimetableMap = getSubjectTimetableMap(timeTableList);
			StudentExamBean student = (StudentExamBean)request.getSession().getAttribute("studentExam");
			String prgrmStructApplicable = student.getPrgmStructApplicable();

			List<ExamBookingTransactionBean> bookingsList = new ArrayList<>();

			ArrayList<String> selectedCenters = examBooking.getSelectedCenters();
			ArrayList<String> selectedCentersTemp = new ArrayList<>();
			selectedCentersTemp.addAll(selectedCenters);
			//added by ps 14 Nov to be removed later Dec 18 registration : Start
			for(String c : selectedCentersTemp) {
				if(StringUtils.isBlank(c)) {
					selectedCenters.remove(c);
				}
			}
			
			ebAuditLogger.info("{} goToGateway - selectedCenters : {}", sapid, selectedCenters);
			
			//added by ps 14 Nov to be removed later Dec 18 registration : end
			
			int year = Calendar.getInstance().get(Calendar.YEAR);
			int month = Calendar.getInstance().get(Calendar.MONTH);

			String examYear = year+"";
			String examMonth = "";

			if(month > 6){
				examMonth = "Dec";
			}else{
				examMonth = "Jun";
			}


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
			
			//added by PS 18th Aug start
			ArrayList<ExamBookingTransactionBean> bookingsToReleaseForCheck = (ArrayList<ExamBookingTransactionBean>)request.getSession().getAttribute("bookingsToRelease");
			String bookingsToReleaseCommaSeparated = getCommaSeparatedString(bookingsToReleaseForCheck);
			//added by PS 18th augh end

			for (int i = 0; i < selectedCenters.size(); i++) {

				String subjectCenter = selectedCenters.get(i);
				String[] parts = subjectCenter.split("\\|");

				//String subject = subjectCenter.substring(0,subjectCenter.indexOf("|"));
				//String centerId = subjectCenter.substring(subjectCenter.indexOf("|")+1, subjectCenter.length() );

				String subject = parts[0];
				/*String centerId = parts[1];
				String startTime = parts[2];*/
				ExamBookingTransactionBean bean = new ExamBookingTransactionBean();
				populateExamBookingBean(bean, subjectCenter, subjectTimetableMap, student);
				/*
				TimetableBean ttBean = subjectTimetableMap.get(subject + startTime);
				examYear = ttBean.getExamYear();
				examMonth = ttBean.getExamMonth();*/

				/*bean.setSapid(sapid);
				bean.setSubject(subject);
				bean.setCenterId(centerId);
				bean.setExamDate(ttBean.getDate());
				bean.setExamTime(ttBean.getStartTime());
				bean.setExamEndTime(ttBean.getEndTime());*/
				bean.setYear(dao.getLiveExamYear());
				bean.setMonth(dao.getLiveExamMonth());
				bean.setProgram(student.getProgram());
				bean.setSem(subjectProgramSemMap.get(subject).getSem());
				bean.setTrackId(trackId);

				if("true".equalsIgnoreCase(hasReleasedSubjects)){
					if("true".equalsIgnoreCase(isBookingSeatAfterExamIsNotLive))
						bean.setAmount(totalFeesForRebookingWhenExamIsNotLive + "");
					else 
					bean.setAmount(totalFeesForRebooking + "");
				}else{

					bean.setAmount(totalExamFees +"");
				}

				bean.setTranStatus(ONLINE_PAYMENT_INITIATED);
				bean.setBooked("N");
				bean.setPaymentMode("Online");
				bean.setExamMode(student.getExamMode());
				bean.setPaymentOption(request.getParameter("paymentOption"));
				
				bookingsList.add(bean);
				boolean seatBookedAtSameDateTime = dao.checkIfAlreadyBookedAtSameDateTime(sapid,bookingsToReleaseCommaSeparated,bean.getExamDate(),bean.getExamTime());
				if(seatBookedAtSameDateTime) {
					modelnView = new ModelAndView("selectExamCenter");
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "Seat already booked on "+bean.getExamDate()+" at "+bean.getExamTime()+" . Please select a different Date/Time for "+bean.getSubject());
					return modelnView;
				
				}
			}

			if(subjects.contains("Project")){
				hasProject = true;
				ExamBookingTransactionBean bean = new ExamBookingTransactionBean();

				bean.setSapid(sapid);
				bean.setSubject("Project");
				bean.setCenterId("-1");
				bean.setYear(dao.getLiveExamYear());
				bean.setMonth(dao.getLiveExamMonth());
				bean.setProgram(student.getProgram());
				bean.setSem(eligibilityService.getProjectApplicableProgramSem(student.getProgram()));
				bean.setExamDate(dao.getLiveExamYear() + "/01/01");
				bean.setExamTime("00:00");
				bean.setExamEndTime("00:00");
				bean.setTrackId(trackId);
				bean.setExamMode(student.getExamMode());


				if("true".equalsIgnoreCase(hasReleasedSubjects)){
					bean.setAmount(totalFeesForRebooking + "");
				}else{

					bean.setAmount(totalExamFees +"");
				}
				bean.setTranStatus(ONLINE_PAYMENT_INITIATED);
				bean.setBooked("N");
				bean.setPaymentMode("Online");
				bookingsList.add(bean);
			}

			if(subjects.contains("Module 4 - Project")){
				hasProject = true;
				ExamBookingTransactionBean bean = new ExamBookingTransactionBean();

				bean.setSapid(sapid);
				bean.setSubject("Module 4 - Project");
				bean.setCenterId("-1");
				bean.setYear(dao.getLiveExamYear());
				bean.setMonth(dao.getLiveExamMonth());
				bean.setProgram(student.getProgram());
				bean.setSem("2");
				bean.setExamDate(dao.getLiveExamYear() + "/01/01");
				bean.setExamTime("00:00");
				bean.setExamEndTime("00:00");
				bean.setTrackId(trackId);
				bean.setExamMode(student.getExamMode());


				if("true".equalsIgnoreCase(hasReleasedSubjects)){
					bean.setAmount(totalFeesForRebooking + "");
				}else{

					bean.setAmount(totalExamFees +"");
				}
				bean.setTranStatus(ONLINE_PAYMENT_INITIATED);
				bean.setBooked("N");
				bean.setPaymentMode("Online");
				bookingsList.add(bean);
			}
			
			boolean centerStillAvailable = checkIfCenterStillAvailable(student, timeTableList, selectedCenters,request);
			if(!centerStillAvailable){
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Center you selected for one of the subject is no longer available. "
						+ "Please make fresh selection of exam centers");


				getAvailableCenters(request, timeTableList, student);
				examBooking = new ExamBookingExamBean();

				modelnView = new ModelAndView("selectExamCenter");
				modelnView.addObject("examBooking", examBooking);

				return modelnView;
			}

			boolean isOnline = false;
			if("Online".equals(student.getExamMode())){
				isOnline = true;
			}
			
			int totalFees = 0;

			if("true".equalsIgnoreCase(hasReleasedSubjects)){
				if("true".equalsIgnoreCase(isBookingSeatAfterExamIsNotLive)) 
					totalFees = totalFeesForRebookingWhenExamIsNotLive;
				else
					totalFees = totalFeesForRebooking;
			}else{

				totalFees = totalExamFees;
			}
			if(totalFees != Integer.parseInt(examBooking.getTotalFeesAmount())) {
				throw new Exception("Invalid total amount found,close all browser windows and try to re-login!");
			}
			
			if(noOfSubjects != bookingsList.size()){
				throw new Exception("We are sorry, Seats selected could not be saved, please try selecting exam center again.");
			}
			
			logger.info(trackId + " " + sapid + " Save Initiation Transaction " + bookingsList +  hasProject + isOnline);
			dao.upsertOnlineInitiationTransaction(sapid, bookingsList, hasProject, isOnline);




			//totalFees = 2;	//remove these before move to prod

			request.getSession().setAttribute("totalFees", totalFees + "");
			//fillPaymentParametersInMap(model, student, totalFees, trackId, message);

			request.getSession().setAttribute("SECURE_SECRET", SECURE_SECRET);
			//return new ModelAndView(new RedirectView("pay"), model);
			//request.getSession().setAttribute("samesite", "none");
//			String paymentOption = request.getParameter("paymentOption");
//			if(paymentOption == null) {
//				paymentOption = "hdfc";	//set default gateway hdfc
//			}
			
//			if("hdfc".equalsIgnoreCase(paymentOption)) {
//				request.getSession().setAttribute("paymentOption","hdfc");
//				fillPaymentParametersInMap(model, student, totalFees, trackId, message);
//				return new ModelAndView(new RedirectView("pay"), model);
//			}
			ModelAndView mv = new ModelAndView("payment");	
			mv.addObject("track_id", trackId);
			mv.addObject("sapid", sapid);
			mv.addObject("type", "Exam_Booking");
			mv.addObject("amount", totalFees);
			mv.addObject("description", message);
			mv.addObject("source", "web");
			mv.addObject("portal_return_url", RETURN_URL);
			mv.addObject("created_by", sapid);
			mv.addObject("updated_by", sapid);
			mv.addObject("mobile", student.getMobile());
			mv.addObject("email_id", student.getEmailId());
			mv.addObject("first_name", student.getFirstName());
			return mv;
			
			
			/*else if(request.getParameter("paymentOption").equals("payu")) {
				ModelAndView mv = new ModelAndView("payu");
				PaymentHelper paymentHelper = new PaymentHelper("exam_registration");
				String checkSum = paymentHelper.generatePayuCheckSum(request, student, totalFees, trackId, message);
				if(checkSum != "true") {
					modelnView = new ModelAndView("selectExamCenter");
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "Error while generating checkSum");
					return modelnView;
				}
				request.getSession().setAttribute("paymentOption","payu");
				mv = paymentHelper.setPayuModelData(mv,request,student, totalFees, trackId, message);
				return mv;
			}
			return proceedToPayOptions(model,requestId,ra);
			ModelAndView mv = new ModelAndView("paytmPay");
			PaymentHelper paymentHelper = new PaymentHelper("exam_registration");
			String checkSum = paymentHelper.generateCheckSum(request, student, totalFees, trackId, message);
			if(checkSum != "true") {
				modelnView = new ModelAndView("selectExamCenter");
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Error while generating checkSum");
				return modelnView;
			}
			request.getSession().setAttribute("paymentOption","paytm");
			mv = paymentHelper.setModelData(mv,request,student, totalFees, trackId, message);
			return mv;*/


		}catch(Exception e){
			

			modelnView = new ModelAndView("selectExamCenter");
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in initiating Online transaction. Error: "+e.getMessage());
			return modelnView;
		}

	}
	
	/*
	 * This method will save selection from user for exam center. 
	 * Delete any old track ids 
	 * Generate new track id and save it in database
	 * Create transaction parameters and redirect to gateway.
	 * */
	
	@RequestMapping(value = "/m/goToGateway", method = RequestMethod.GET)
	public ModelAndView mgoToGateway(ModelMap model,
									@RequestParam("selectedCentersList") List<String> selectedCentersList,
									@RequestParam("subjects") List<String> Subjects,
									HttpServletRequest request) {
		String sapid = request.getParameter("sapid");
		String hasReleasedSubjects = request.getParameter("hasReleasedSubjects");
		String onlineSeatBookingComplete = request.getParameter("onlineSeatBookingComplete"); 
		int totalExamFees =  Integer.parseInt(request.getParameter("totalExamFees")); 
		//String subjectsString = request.getParameter("subjects");
		//String data_tmp[] = subjectsString.split(",");  
		
		//ArrayList<String> Subjects = new ArrayList<String>(input.getSubjects());
		
		/*for(int i=0;i < data_tmp.length;i++) {
			Subjects.add(data_tmp[i]);
		}*/
		
		//ArrayList<String> selectedCentersList = new ArrayList<String>(input.getSelectedCentersList());
		/*String subjectsCenterString = request.getParameter("selectedCentersList");
		data_tmp = subjectsCenterString.split(",");
		
		for(int i=0;i < data_tmp.length;i++) {
			selectedCentersList.add(data_tmp[i]);
		}*/
		/*selectedCentersList.add("Business Economics|405|12:30:00|2018-06-15|PUNE");
		selectedCentersList.add("Business Communication and Etiquette|405|03:30:00|2018-06-15|PUNE");*/
		ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
		boolean hasProject = false;
		
		ModelAndView modelnView = new ModelAndView("bookingStatus");
//		ModelAndView modelnView = new ModelAndView("bookingStatus");
		if("true".equals(onlineSeatBookingComplete)){ 
			return modelnView;
		}
		int noOfSubjects = 0;
		//int feesToCharge = examFeesPerSubject;
		//HashMap<String, ProgramSubjectMappingBean> subjectProgramSemMap = input.getSubjectProgramSemMap();

		try{
			ArrayList<String> subjects =  new ArrayList<String>();
			subjects.addAll(Subjects);
			sapid = sapid.trim(); 
			String trackId = sapid + System.currentTimeMillis() ;
			
			request.getSession().setAttribute("sapid", sapid);
			request.getSession().setAttribute("trackId", trackId);
			request.getSession().setAttribute("totalFees", totalExamFees);
			
			
			
			request.getSession().setAttribute("trackId", trackId);

			String message = "Exam fees for "+sapid;
			if("true".equalsIgnoreCase(hasReleasedSubjects)){
				message = "Exam Center Change fees for "+sapid;
			}
			StudentExamBean student = dao.getSingleStudentsData(sapid);
			
			
			List<TimetableBean> timeTableList = dao.getTimetableListForGivenSubjects(subjects,student);
			Map<String, TimetableBean> subjectTimetableMap = getSubjectTimetableMap(timeTableList);
			String prgrmStructApplicable = student.getPrgmStructApplicable();

			List<ExamBookingTransactionBean> bookingsList = new ArrayList<>();
 
			ArrayList<String> selectedCenters = new ArrayList<String>();
			selectedCenters.addAll(selectedCentersList);
			int year = Calendar.getInstance().get(Calendar.YEAR);
			int month = Calendar.getInstance().get(Calendar.MONTH);

			String examYear = year+"";
			String examMonth = "";

			if(month > 6){
				examMonth = "Dec";
			}else{
				examMonth = "Jun";
			}


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
				String[] parts = subjectCenter.split("\\|");

				//String subject = subjectCenter.substring(0,subjectCenter.indexOf("|"));
				//String centerId = subjectCenter.substring(subjectCenter.indexOf("|")+1, subjectCenter.length() );

				String subject = parts[0];
				/*String centerId = parts[1];
				String startTime = parts[2];*/
				ExamBookingTransactionBean bean = new ExamBookingTransactionBean();
				populateExamBookingBean(bean, subjectCenter, subjectTimetableMap, student);
				/*
				TimetableBean ttBean = subjectTimetableMap.get(subject + startTime);
				examYear = ttBean.getExamYear();
				examMonth = ttBean.getExamMonth();*/

				/*bean.setSapid(sapid);
				bean.setSubject(subject);
				bean.setCenterId(centerId);
				bean.setExamDate(ttBean.getDate());
				bean.setExamTime(ttBean.getStartTime());
				bean.setExamEndTime(ttBean.getEndTime());*/
				bean.setYear(dao.getLiveExamYear());
				bean.setMonth(dao.getLiveExamMonth());
				bean.setProgram(student.getProgram());
				//Iterate through each subject and create Exam applicable+registered subjects list: START
				HashMap<String, ProgramSubjectMappingExamBean> subjectProgramSemMap = new HashMap<String, ProgramSubjectMappingExamBean>();
				ArrayList<ProgramSubjectMappingExamBean> programSubjectMappingList = getProgramSubjectMappingList();
				ArrayList<PassFailExamBean> passFailList = (ArrayList<PassFailExamBean>)dao.getPassFailedSubjectsList(student.getSapid());
				//Also take failed subjects for Exam Registration: START
				ArrayList<String> passList = new ArrayList<>();
				for(PassFailExamBean item:passFailList) {
					if("Y".equals(item.getIsPass())) {
						passList.add(item.getSubject());
					}
				}
				for (int j = 0; j < programSubjectMappingList.size(); j++) {
					ProgramSubjectMappingExamBean programSubjectMappingBean = programSubjectMappingList.get(j);
					if(programSubjectMappingBean.getPrgmStructApplicable().equals(student.getPrgmStructApplicable()) 
							&& programSubjectMappingBean.getProgram().equals(student.getProgram())
							&& !student.getWaivedOffSubjects().contains(programSubjectMappingBean.getSubject()) ){
						if(passList.contains(programSubjectMappingBean.getSubject())){
							//Not applicable to book if already cleared. Do not remove this condition
							continue;
						}
						
						subjectProgramSemMap.put(programSubjectMappingBean.getSubject(), programSubjectMappingBean);//Needed for displaying Sem and Program on various pages
					}
				}
				
				
				bean.setSem(subjectProgramSemMap.get(subject).getSem());
				bean.setTrackId(trackId);

				if("true".equalsIgnoreCase(hasReleasedSubjects)){
					bean.setAmount(totalFeesForRebooking + "");
				}else{

					bean.setAmount(totalExamFees +"");
				}

				bean.setTranStatus(ONLINE_PAYMENT_INITIATED);
				bean.setBooked("N");
				bean.setPaymentMode("Online");
				bean.setExamMode(student.getExamMode());


				bookingsList.add(bean);
		}

			if(subjects.contains("Project")){
				hasProject = true;
				ExamBookingTransactionBean bean = new ExamBookingTransactionBean();

				bean.setSapid(sapid);
				bean.setSubject("Project");
				bean.setCenterId("-1");
				bean.setYear(dao.getLiveExamYear());
				bean.setMonth(dao.getLiveExamMonth());
				bean.setProgram(student.getProgram());
				bean.setSem(eligibilityService.getProjectApplicableProgramSem(student.getProgram()));
				bean.setExamDate(dao.getLiveExamYear() + "/01/01");
				bean.setExamTime("00:00");
				bean.setExamEndTime("00:00");
				bean.setTrackId(trackId);
				bean.setExamMode(student.getExamMode());


				if("true".equalsIgnoreCase(hasReleasedSubjects)){
					bean.setAmount(totalFeesForRebooking + "");
				}else{

					bean.setAmount(totalExamFees +"");
				}
				bean.setTranStatus(ONLINE_PAYMENT_INITIATED);
				bean.setBooked("N");
				bean.setPaymentMode("Online");
				bookingsList.add(bean);
			}
			if(subjects.contains("Module 4 - Project")){
				hasProject = true;
				ExamBookingTransactionBean bean = new ExamBookingTransactionBean();

				bean.setSapid(sapid);
				bean.setSubject("Module 4 - Project");
				bean.setCenterId("-1");
				bean.setYear(dao.getLiveExamYear());
				bean.setMonth(dao.getLiveExamMonth());
				bean.setProgram(student.getProgram());
				bean.setSem("2");
				bean.setExamDate(dao.getLiveExamYear() + "/01/01");
				bean.setExamTime("00:00");
				bean.setExamEndTime("00:00");
				bean.setTrackId(trackId);
				bean.setExamMode(student.getExamMode());


				if("true".equalsIgnoreCase(hasReleasedSubjects)){
					bean.setAmount(totalFeesForRebooking + "");
				}else{

					bean.setAmount(totalExamFees +"");
				}
				bean.setTranStatus(ONLINE_PAYMENT_INITIATED);
				bean.setBooked("N");
				bean.setPaymentMode("Online");
				bookingsList.add(bean);
			}
			
			boolean centerStillAvailable = mcheckIfCenterStillAvailable(student, timeTableList, selectedCenters);
//			if(!centerStillAvailable){
//				response.setError("true");
//				response.setErrorMessage("Center you selected for one of the subject is no longer available. "
//						+ "Please make fresh selection of exam centers");
//				getAvailableCenters(request, timeTableList, student);
//				examBooking = new ExamBookingBean();
//
//				modelnView = new ModelAndView("selectExamCenter");
//				modelnView.addObject("examBooking", examBooking);
//
//				return new ResponseEntity<ExamGoToGatewayBeanAPIResponse>(response, headers, HttpStatus.OK);
//
//			}

			boolean isOnline = false;
			if("Online".equals(student.getExamMode())){
				isOnline = true;
			}

			if(noOfSubjects != bookingsList.size()){
				throw new Exception("We are sorry, Seats selected could not be saved, please try selecting exam center again.");
			}
			dao.upsertOnlineInitiationTransaction(sapid, bookingsList, hasProject, isOnline);
			int totalFees = 0;

			if("true".equalsIgnoreCase(hasReleasedSubjects)){
				totalFees = totalFeesForRebooking;
			}else{

				totalFees = totalExamFees;
			}
//			response.setTotalFees(totalFees);
//			fillPaymentParametersInMap(model, student, totalFees, trackId, message);
//
//			mfillPaymentParametersInMap(response, student, totalFees, trackId, message);
//			response.setSECURE_SECRET(SECURE_SECRET);
			
			request.getSession().setAttribute("totalFees", totalFees + "");
			fillPaymentParametersInMap(model, student, totalFees, trackId, message);
			request.getSession().setAttribute("SECURE_SECRET", SECURE_SECRET);

			//response.setSECURE_SECRET(SECURE_SECRET);
			return new ModelAndView(new RedirectView("pay?sapid=" + sapid + "&trackId=" + trackId), model);
			
			
			
//				String md5HashData = SECURE_SECRET;
//			    // retrieve all the parameters into a hash map
//			   
//				HashMap testMap = new HashMap();
//				testMap.put("account_id", response.getACCOUNT_ID());
//	            testMap.put("address", response.getAddress());
//	            testMap.put("algo", response.getAlgo());
//	            testMap.put("amount", response.getAmount());
//	            testMap.put("channel",response.getChannel());
//	            testMap.put("city", response.getCity());
//	            testMap.put("country", response.getCountry());
//	            testMap.put("currency", response.getCurrency());
//	            testMap.put("currency_code", response.getCurrency_code());
//	            testMap.put("description", response.getDescription());
//	            testMap.put("email", response.getEmail());
//	            testMap.put("mode", response.getMode());
//	            testMap.put("name", response.getName());
//	            testMap.put("phone", response.getPhone());
//	            testMap.put("postal_code", response.getPostal_code());
//	            testMap.put("reference_no", response.getReference_no());
//	            testMap.put("return_url", response.getReturn_url());
//			  //Sort the HashMap
//			    Map requestFields = new TreeMap(testMap);
//			   
//			            
//			        
//
//			    
//				
//				
//			      for (Iterator i = requestFields.keySet().iterator(); i.hasNext(); ) {
//			            
//			            String key = (String)i.next();
//			            String value = (String)requestFields.get(key);
//						md5HashData += "|"+value;
//						
//			    	}
//
//			
//
//			String hvalue = "MD5";
//
//			String hashedvalue ="";
//
//			if ( hvalue.equals("MD5") )
//			{
//			    hashedvalue = mmd5(md5HashData);
//			    response.setSecure_hash(hashedvalue);
//			    
//			}
//
//			return new ResponseEntity<ExamGoToGatewayBeanAPIResponse>(response, headers, HttpStatus.OK);

		}catch(Exception e){
			

			modelnView = new ModelAndView("selectExamCenter");
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in initiating Online transaction. Error: "+e.getMessage());
			return modelnView;
//		return new ResponseEntity<ExamGoToGatewayBeanAPIResponse>(response, headers, HttpStatus.OK);

		}
	}
	
	private String mmd5(String str) throws Exception {
		MessageDigest m = MessageDigest.getInstance("MD5");

		byte[] data = str.getBytes();
		
		m.update(data,0,data.length);
		
		BigInteger i = new BigInteger(1,m.digest());
		
		String hash = String.format("%1$032X", i);
		
		return hash;
	}
	/*@Async
	public void createAndUploadHallTicketAndExamFeeReceipt(String sapid,boolean isCorporate){

		createFeeReceiptAndEntryInReceiptHallTicketTable(sapid,isCorporate);
		createHallTicketAndEntryInReceiptHallTicketTable(sapid,isCorporate);

	}

	public void createFeeReceiptAndEntryInReceiptHallTicketTable(String sapid,boolean isCorporate){

		String year=null, month=null, fileName="";
		ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
		ExamCenterDAO ecDao = (ExamCenterDAO)act.getBean("examCenterDAO");

		StudentBean student = dao.getSingleStudentWithValidity(sapid);
		List<ExamBookingTransactionBean> examBookings = dao.getConfirmedBooking(sapid);

		HashMap<String, String> corporateExamCenterIdNameMap = ecDao.getCorporateExamCenterIdNameMap();

		if(examBookings!=null && examBookings.size()>0){
			year = examBookings.get(0).getYear();
			month = examBookings.get(0).getMonth();
		}
		try{
			if(isCorporate){
				fileName = examFeeReceiptCreator.createPDF(examBookings, corporateExamCenterIdNameMap, FEE_RECEIPT_PATH, student);
			}else{
				fileName = examFeeReceiptCreator.createPDF(examBookings, getExamCenterIdNameHashMap(), FEE_RECEIPT_PATH, student);
			}


			dao.insertDocumentRecord(fileName, year, month, sapid, "Exam Fee Receipt");
		}catch(Exception e){
			
		}



	}
	public void createHallTicketAndEntryInReceiptHallTicketTable(String sapid,boolean isCorporate){
		ArrayList<String> subjects = new ArrayList<>();
		String year = null,month = null;
		ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
		ArrayList<ExamBookingTransactionBean> subjectsBooked = eDao.getConfirmedBooking(sapid);
		StudentBean student = eDao.getSingleStudentWithValidity(sapid);
		HashMap<String, ExamBookingTransactionBean> subjectBookingMap = new HashMap<>();

		for (int i = 0; i < subjectsBooked.size(); i++) {
			ExamBookingTransactionBean bean = subjectsBooked.get(i);
			subjectBookingMap.put(bean.getSubject(), bean);
		}

		if(subjectsBooked!=null && subjectsBooked.size()>0){
			year = subjectsBooked.get(0).getYear();
			month = subjectsBooked.get(0).getMonth();
		}
		for (int i = 0; i < subjectsBooked.size(); i++) {
			subjects.add(subjectsBooked.get(i).getSubject());
		}

		ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
		List<TimetableBean> timeTableList = dao.getTimetableListForGivenSubjects(subjects, student);
		String fileName = "";
		try{
			fileName = hallTicketCreator.createHallTicket(timeTableList, subjectsBooked, getProgramMap(), student, 
					HALLTICKET_PATH, getMostRecentTimetablePeriod(),getExamCenterCenterDetailsMap(isCorporate), subjectBookingMap, STUDENT_PHOTOS_PATH);;

			dao.insertDocumentRecord(fileName, year, month, sapid, "Hall Ticket"); 	


		}catch(Exception e){
			
		}

	}*/
	/*private boolean checkIfCenterStillAvailable(StudentBean student,	List<TimetableBean> timeTableList, ArrayList<String> selectedCenters) {
		String studentProgramStructure = student.getPrgmStructApplicable();
		Map<String, ArrayList<String>> subjectCenterIdListMap = new HashMap<String, ArrayList<String>>();
		ExamCenterDAO ecDao = (ExamCenterDAO)act.getBean("examCenterDAO");
		boolean centerStillAvailable = true;

		if("Jul2014".equals(studentProgramStructure) || "Jul2013".equals(studentProgramStructure)){
			//subjectCenterIdListMap = ecDao.getAvailableCenterIDSForGivenSubjects(timeTableList);

			for (int i = 0; i < selectedCenters.size(); i++) {
				String subjectCenter = selectedCenters.get(i);

				String[] parts = subjectCenter.split("\\|");

				//String subject = subjectCenter.substring(0,subjectCenter.indexOf("|"));
				//String centerId = subjectCenter.substring(subjectCenter.indexOf("|")+1, subjectCenter.length() );

				String subject = parts[0];
				String centerId = parts[1];
				String startTime = parts[2];

				ArrayList<String> centerIdList = subjectCenterIdListMap.get(subject+startTime);

				if(!centerIdList.contains(centerId)){
					centerStillAvailable = false;
					break;
				}
			}

			return centerStillAvailable;
		}else{
			return true;
		}
	}
	 */

	private boolean checkIfCenterStillAvailable( StudentExamBean student,	List<TimetableBean> timeTableList, ArrayList<String> selectedCenters,HttpServletRequest request) {
		ExamCenterDAO ecDao = (ExamCenterDAO)act.getBean("examCenterDAO");
		boolean centerStillAvailable = true;
		String studentProgramStructure = student.getPrgmStructApplicable();
		if("Online".equals(student.getExamMode())){

			List<ExamCenterBean> availableCenters = new ArrayList<ExamCenterBean>();

//			if(!student.getConsumerType().equals("Diageo")){
				availableCenters = ecDao.getAvailableCentersForRegularOnlineExam(student.getSapid(), student.isCorporateExamCenterStudent(), student.getMappedCorporateExamCenterId());

//			} else {
//				availableCenters = ecDao.getAvailableCentersForRegularOnlineExamCorporateDiageo(student.getSapid(), student.isCorporateExamCenterStudent(), student.getMappedCorporateExamCenterId());
//			}	
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
		}else{
			return true;
		}
		return centerStillAvailable;
	}
	
	private boolean mcheckIfCenterStillAvailable( StudentExamBean student,	List<TimetableBean> timeTableList, ArrayList<String> selectedCenters) {
		ExamCenterDAO ecDao = (ExamCenterDAO)act.getBean("examCenterDAO");
		boolean centerStillAvailable = true;
		String studentProgramStructure = student.getPrgmStructApplicable();
		if("Online".equals(student.getExamMode())){

			List<ExamCenterBean> availableCenters = ecDao.getAvailableCentersForRegularOnlineExam(student.getSapid(), student.isCorporateExamCenterStudent(), student.getMappedCorporateExamCenterId());

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
		}else{
			return true;
		}
		return centerStillAvailable;
	}
	private void fillPaymentParametersInMap(ModelMap model,
			StudentExamBean student, int totalFees, String trackId, String message) {

		/*String address = student.getAddress();
		if(address == null || address.trim().length() == 0){
			address = "Not Available";
		}else if(address.length() > 200){
			address = address.substring(0, 200);
		}*/
		String address = "NGASCE,V L Mehta Rd,Vileparle,Mumbai"; //Not taking student address to avoid junk character issue in address. HDFC blocks such payments

		/*String city = student.getCity();
		if(city == null || city.trim().length() == 0){
			city = "Not Available";
		}else if(city.length() > 30){
			city = city.substring(0, 30);
		}*/

		String city = "Mumbai";//Not taking student city to avoid junk character issue in address. HDFC blocks such payments

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

		model.addAttribute("udf1", message);
		model.addAttribute("channel", "10");
		model.addAttribute("account_id", ACCOUNT_ID);
		model.addAttribute("reference_no", trackId);
		model.addAttribute("amount",totalFees);
		model.addAttribute("mode", "LIVE");
		model.addAttribute("currency", "INR");
		model.addAttribute("currency_code", "INR");
		model.addAttribute("description", message);
		model.addAttribute("return_url", RETURN_URL);
		model.addAttribute("name", student.getFirstName()+ " "+student.getLastName());
		model.addAttribute("address",URLEncoder.encode(address));
		model.addAttribute("city", city);
		model.addAttribute("country", "IND");
		model.addAttribute("postal_code", pin);
		model.addAttribute("phone", mobile);
		model.addAttribute("email", emailId);
		model.addAttribute("algo", "MD5");
		model.addAttribute("V3URL", V3URL);
		/*model.addAttribute("studentNumber", student.getSapid());*/

	}

	private void mfillPaymentParametersInMap(ExamGoToGatewayBeanAPIResponse response ,
			StudentExamBean student, int totalFees, String trackId, String message) {

		/*String address = student.getAddress();
		if(address == null || address.trim().length() == 0){
			address = "Not Available";
		}else if(address.length() > 200){
			address = address.substring(0, 200);
		}*/
		String address = "NGASCE,V L Mehta Rd,Vileparle,Mumbai"; //Not taking student address to avoid junk character issue in address. HDFC blocks such payments

		/*String city = student.getCity();
		if(city == null || city.trim().length() == 0){
			city = "Not Available";
		}else if(city.length() > 30){
			city = city.substring(0, 30);
		}*/

		String city = "Mumbai";//Not taking student city to avoid junk character issue in address. HDFC blocks such payments

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


		response.setChannel("10");
		response.setACCOUNT_ID(ACCOUNT_ID);
		response.setReference_no(trackId);
		response.setAmount("600");
		response.setMode("LIVE");
		response.setCurrency("INR");
		response.setCurrency_code("INR");
		response.setDescription(message);
		response.setReturn_url(MRETURN_URL);
		response.setName(student.getFirstName()+ " "+student.getLastName());
		response.setAddress(URLEncoder.encode(address));
		response.setCity(city);
		response.setCountry("IND");
		response.setPostal_code(pin);
		response.setPhone(mobile);
		response.setEmail(emailId);
		response.setAlgo("MD5");
		response.setV3URL(V3URL);

		/*model.addAttribute("studentNumber", student.getSapid());*/

	}


	@RequestMapping(value = "/pay", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView pay(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		logger.info(request.getSession().getAttribute("trackId") + " " + (String)request.getSession().getAttribute("userId") + " Pay JSP called ");
		return new ModelAndView("pay");
	}
	
	
	@RequestMapping(value = "/m/pay", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView mpay(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
//		if(!checkSession(request, response)){
//			redirectToPortalApp(response);
//			return null;
//		}
		return new ModelAndView("pay");
	}
	



	private Map<String, TimetableBean> getSubjectTimetableMap(List<TimetableBean> timeTableList) {
		HashMap<String, TimetableBean> subjectTimetableMap = new HashMap<>();
		for (int i = 0; i < timeTableList.size(); i++) {
			TimetableBean bean = timeTableList.get(i);
			String subject = bean.getSubject();
			subjectTimetableMap.put(subject + bean.getStartTime() + bean.getDate(), bean); //Changed temporarily for dec2018 exam Added date in key
		}

		return subjectTimetableMap;
	}

	/**
	 * API to update exam bookings and seat release upon transaction success mostly
	 * for webhooks.
	 * 
	 * @author Swarup Singh Rajpurohit
	 */
	@RequestMapping(value = "/m/examGatewayResponse", consumes = "application/json",produces = "application/json")
	@ResponseBody
	public ResponseEntity<String> examGatewayresponse(@RequestBody TransactionsBean requestBean) {
		String returnString = null;
		try {

			List<Double> releaseBookingsFees = Arrays.asList(500.00, 1000.00);

			webhookLogger.info("received payload : " + requestBean);
			// return response in case track id or sapid is blank
			if (StringUtils.isBlank(requestBean.getTrack_id()) || StringUtils.isBlank(requestBean.getSapid())
					|| StringUtils.isBlank(requestBean.getTransaction_status())
					|| !(PAYMENT_FAILED.equalsIgnoreCase(requestBean.getTransaction_status())
							|| PAYMENT_SUCCESSFUL.equalsIgnoreCase(requestBean.getTransaction_status()))) {
				webhookLogger.info(requestBean.getTrack_id() + " invalid payload : " + requestBean);
				return sendOkayResponse("INVALID REQUEST");
			}

			ExamBookingDAO dao = (ExamBookingDAO) act.getBean("examBookingDAO");
			ExamCenterDAO edao = (ExamCenterDAO) act.getBean("examCenterDAO");

			// avoid updating transaction when exam registration is not live
			// reference :
			// https://bitbucket.org/ngasceteam/exam/branch/feature/Payment_Gateway_Webhooks_Configuration_MS
//			boolean isExamRegistraionLive = dao.isConfigurationLive("Exam Registration");
//			// suggestion add cache instead of dao call every time we receive webhook
//			if (!isExamRegistraionLive) {
//				returnString = "Exam registration is not live so sending back payload : " + requestBean;
//				webhookLogger.info(returnString);
//				return sendOkayResponse(returnString);
//			}

			StudentExamBean student = new StudentExamBean();

			webhookLogger.info(requestBean.getTrack_id() + " fetching all bookings of sapid : {}",
					requestBean.getSapid());
			// Getting all the bookings except project for a sapid
			List<ExamBookingTransactionBean> allBookings = dao.getAllBookingsBySapId(requestBean.getSapid());

			// if no bookings were made for this sapid so returning response 200
			if (allBookings.size() < 1) {
				returnString = requestBean.getTrack_id() + " No transactions found for sapid : "
						+ requestBean.getSapid();
				webhookLogger.info(returnString);
				return sendOkayResponse(returnString);
			}

			// filtering all records for current transaction
			// check for empty array list
			List<ExamBookingTransactionBean> currentTransactionBean = allBookings.stream()
					.filter(bean -> bean.getTrackId().equalsIgnoreCase(requestBean.getTrack_id()))
					.collect(Collectors.toList());

			// no transactions found for the track id received so sending back 200
			if (currentTransactionBean.size() < 1) {
				returnString = requestBean.getTrack_id() + " No records for track id : " + requestBean.getTrack_id();
				webhookLogger.info(returnString);
				return sendOkayResponse(returnString);
			}
			webhookLogger.info(requestBean.getTrack_id() + " Records found : " + currentTransactionBean);

			// checking if the transaction for webhook received was already updated
			boolean ifAlreadyUpdated = checkIfAlreadyUpdated(currentTransactionBean, requestBean);
			// in case it was already updated will send 200 to avoid getting hits
			if (ifAlreadyUpdated) {
				returnString = requestBean.getTrack_id() + " Payments already updated";
				webhookLogger.info(returnString);
				return sendOkayResponse(returnString);
			}

			ArrayList<ExamBookingTransactionBean> toReleaseBookingsList = new ArrayList<>();

			ArrayList<ExamBookingTransactionBean> doubleBookedList = new ArrayList<>();

			// filtering to release bookings and double bookings from all bookings by
			// comparing it with
			// current received transaction records, check comment on method body
			allBookings.stream().filter(k -> "Y".equalsIgnoreCase(k.getBooked())).forEach(k -> {
				currentTransactionBean.stream().forEach(i -> {
//					if (Double.compare(500, Double.valueOf(requestBean.getAmount())) == 0
//							|| Double.compare(1000, Double.valueOf(requestBean.getAmount())) == 0) {
					if (releaseBookingsFees.contains(Double.valueOf(requestBean.getAmount()))) {
						if (isSeatReleasedBean(i, k))
							toReleaseBookingsList.add(k);
					} else if (isDoubleBooked(i, k))
						doubleBookedList.add(i);
				});
			});

			// maps required by email method
			Map<String, String> examCenterIdNameMap = new HashMap<>();

			if (corporateCenterUserMapping == null)
				corporateCenterUserMapping = getCorporateCenterUserMapping();

			if (corporateCenterUserMapping.containsKey(student.getSapid())) {
				student.setCorporateExamCenterStudent(true);
				examCenterIdNameMap = edao.getCorporateExamCenterIdNameMap();
			} else {
				examCenterIdNameMap = getExamCenterIdNameMap();
				student.setCorporateExamCenterStudent(false);
			}

			webhookLogger.info(requestBean.getTrack_id() + " fetching user data for sapid : {}",
					requestBean.getSapid());
			student = dao.getSingleStudentsData(requestBean.getSapid());

			webhookLogger.info(requestBean.getTrack_id() + " fetched data for sapid  : {} : " + student,
					requestBean.getSapid());
			ExamBookingTransactionBean bean = new ExamBookingTransactionBean();

			// successful payment bean to be marked as successful
			if (PAYMENT_SUCCESSFUL.equalsIgnoreCase(requestBean.getTransaction_status())) {
				bean.setSapid(requestBean.getSapid());
				bean.setTrackId(requestBean.getTrack_id());

				bean.setResponseMessage(requestBean.getResponse_message());
				bean.setTransactionID(requestBean.getTransaction_id());
				bean.setRequestID(requestBean.getRequest_id());
				bean.setMerchantRefNo(requestBean.getMerchant_ref_no());
				bean.setSecureHash(requestBean.getSecure_hash());
				bean.setRespAmount(requestBean.getResponse_amount());
				bean.setRespTranDateTime(requestBean.getResponse_transaction_date_time());
				bean.setResponseCode(requestBean.getResponse_code());
				bean.setRespPaymentMethod(requestBean.getResponse_payment_method());
//				bean.setIsFlagged(request.getParameter("IsFlagged"));
				bean.setPaymentID(requestBean.getPayment_id());
				bean.setError(requestBean.getError());
				bean.setDescription(requestBean.getDescription());
				bean.setPaymentOption(requestBean.getPayment_option());
				webhookLogger.info(
						requestBean.getTrack_id() + " Created bean to update exam bookings table : " + bean.toString());
				bean.setLastModifiedBy(requestBean.getSapid());
				webhookLogger.info(requestBean.getTrack_id() +" Created bean to update exam bookings table : {}", bean.toString());

				// sending mail and updating conflict transaction in case double booking was
				// done
				if (doubleBookedList.size() > 0) {
					webhookLogger.info(requestBean.getTrack_id()
							+ " Double bookings were found  so setting action as refund and updating conflict tables");
					bean.setAction("REFUND");

					fillParametersForTableUpdate(bean, doubleBookedList, student);

					webhookLogger.info(requestBean.getTrack_id()
							+ " setting conflicted transaction as not booked and to be refunded");
					dao.updateSeatsForAlreadyBookedConflictUsingSingleConnection(bean);

					webhookLogger.info(
							requestBean.getTrack_id() + " inserting transaction to exam conflict table : " + bean);
					dao.updateProjectConfilctTransactionDetails(
							new ArrayList<ExamBookingTransactionBean>(Arrays.asList(bean)));
					returnString = requestBean.getTrack_id() + " Double booking was done ";

					webhookLogger.info(returnString + " now returning okay response");

					try {
						MailSender mailSender = (MailSender) act.getBean("mailer");
						mailSender.sendConflictsEmail(new ArrayList<>(),
								new ArrayList<ExamBookingTransactionBean>(Arrays.asList(bean)));
					} catch (Exception e) {
						String stackTraceAsString = Throwables.getStackTraceAsString(e);
						webhookLogger.info(requestBean.getTrack_id() + " Error occurred while sending conflict mail "
								+ stackTraceAsString);
					}
					return sendOkayResponse(returnString);

				} else {
					// Below method is made using single Connection to ensure Commit and
					// RollbackreturnString
					List<ExamBookingTransactionBean> examBookings = dao.updateSeatsForOnlineUsingSingleConnection(bean,
							student.isCorporateExamCenterStudent());

					if (toReleaseBookingsList != null && toReleaseBookingsList.size() > 0) {

						webhookLogger.info(requestBean.getTrack_id() + " released bookings found  so releasing seat");
						ExamCenterDAO ecDao = (ExamCenterDAO) act.getBean("examCenterDAO");
						ecDao.releaseBookings(requestBean.getSapid(), toReleaseBookingsList, bean, "false", false,
								student.isCorporateExamCenterStudent());

						// Added by shivam.pandey.EXT - START
						exambookingAuditService.asyncInsertExamBookingAudit(toReleaseBookingsList, "false",
								requestBean.getSapid());
						// Added by shivam.pandey.EXT - END

					}

					try {
						MailSender mailSender = (MailSender) act.getBean("mailer");
						mailSender.sendBookingSummaryEmailFromWebhook(student, dao, edao, examCenterIdNameMap);
					} catch (Exception e) {
						String stackTraceAsString = Throwables.getStackTraceAsString(e);
						webhookLogger.info(requestBean.getTrack_id()
								+ " Error occurred while sending booking summary mail " + stackTraceAsString);
					}

					examBookingHelper.createAndUploadHallTicketAndExamFeeReceipt(requestBean.getSapid(),
							student.isCorporateExamCenterStudent());

					if (toReleaseBookingsList == null || toReleaseBookingsList.size() == 0) {

						try {
							MailSender mailSender = (MailSender) act.getBean("mailer");
							mailSender.sendEmailForDemoExamReminderAfterRegistration(student);
						} catch (Exception e) {
							String stackTraceAsString = Throwables.getStackTraceAsString(e);
							webhookLogger.info(requestBean.getTrack_id()
									+ " Error occurred while sending exam reminder after registration mail "
									+ stackTraceAsString);
						}

					}

					examRegisterlogger.info("Real Time Registartion called from examGatewayresponse method"+dao.getIsExtendedExamRegistrationLiveForRealTime());
					if(dao.getIsExtendedExamRegistrationLiveForRealTime()) {
						examRegisterlogger.info("Real Time Registartion called");
					examRegistrationRealTimeStrategy.registrationOnMettlAndReleaseBooking(examBookings,toReleaseBookingsList);
					}
					
					returnString = "table updated and mail send for track id : " + requestBean.getTrack_id();

					returnString = requestBean.getTrack_id() + " table updated and mail sent";

					webhookLogger.info(returnString);
					return sendOkayResponse(returnString);

				}
			} else if (PAYMENT_FAILED.equalsIgnoreCase(requestBean.getTransaction_status())) {
				bean.setMonth(currentTransactionBean.get(0).getMonth());
				bean.setYear(currentTransactionBean.get(0).getYear());
				bean.setSapid(requestBean.getSapid());
				bean.setTrackId(requestBean.getTrack_id());
				bean.setPaymentOption(requestBean.getPayment_option());
				webhookLogger.info(requestBean.getTrack_id() + " Found payment status failed ");
				bean.setTranStatus(ONLINE_PAYMENT_FAILED);
				bean.setError(requestBean.getError());
				dao.markTransactionsFailed(bean);
				returnString = requestBean.getTrack_id() + " table updated as failed";
				webhookLogger.info(returnString);
				return sendOkayResponse(returnString);
			}

		} catch (Exception e) {
			String stackTraceAsString = Throwables.getStackTraceAsString(e);
			returnString = requestBean.getTrack_id() + " Error updating table " + stackTraceAsString;
			webhookLogger.info(returnString);
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			try {
				MailSender mailSender = (MailSender) act.getBean("mailer");
				mailSender.mailStackTrace("Error in Saving  Transaction", e);
			} catch (Exception e2) {
				String stackTraceAsStringForE2 = Throwables.getStackTraceAsString(e2);
				webhookLogger.info(
						requestBean.getTrack_id() + " Error occurred while sending stack trace mail for payload :  "
								+ requestBean + " " + stackTraceAsStringForE2);
			}
			return sendOkayResponse(returnString);
		}
		return sendOkayResponse("Default 200 okay response");
	}
	
	private void fillParametersForTableUpdate(ExamBookingTransactionBean bean,
			ArrayList<ExamBookingTransactionBean> doubleBookedList, StudentExamBean student) {
		ExamBookingTransactionBean currentBean = doubleBookedList.get(0);
		bean.setTranDateTime(currentBean.getTranDateTime());
		bean.setYear(currentBean.getYear());
		bean.setMonth(currentBean.getMonth());
		bean.setEmailId(student.getEmailId());
		bean.setMobile(student.getMobile());
		bean.setAmount(currentBean.getAmount());
		bean.setAltPhone(student.getAltPhone());
		bean.setLastModifiedBy(bean.getSapid());
	}

	private boolean checkIfAlreadyUpdated(List<ExamBookingTransactionBean> bookings, TransactionsBean requestBean) {
		boolean alreadyMarked = false;
		if (bookings.stream().allMatch(k -> "Y".equalsIgnoreCase(k.getBooked()))
				|| bookings.stream().allMatch(k -> "Online Payment Successful".equalsIgnoreCase(k.getTranStatus()))
				|| bookings.stream()
						.allMatch(k -> "Online Payment Manually Approved".equalsIgnoreCase(k.getTranStatus()))) {
			alreadyMarked = true;

		} else if (PAYMENT_FAILED.equalsIgnoreCase(requestBean.getTransaction_status())) {
			alreadyMarked = bookings.stream().allMatch(k -> ONLINE_PAYMENT_FAILED.equalsIgnoreCase(k.getTranStatus()));
		}
		return alreadyMarked;
	}

	// comparing all successful records with records with current transaction
	// record, if exam data and exam time are NOT same
	// this means this we need to release previous succesful bookings
	private boolean isSeatReleasedBean(ExamBookingTransactionBean beanOne, ExamBookingTransactionBean beanTwo) {
		if (beanOne.getSem().equalsIgnoreCase(beanTwo.getSem())
				&& beanOne.getSubject().equalsIgnoreCase(beanTwo.getSubject())
					&& !(beanOne.getTrackId().equalsIgnoreCase(beanTwo.getTrackId())))
			return true;
		return false;
	}

	// comparing all successful records with records with current transaction
	// record, if exam data and exam time are same
	// this means this we need to refund this transaction
	private boolean isDoubleBooked(ExamBookingTransactionBean beanOne, ExamBookingTransactionBean beanTwo) {
		if (beanOne.getSem().equalsIgnoreCase(beanTwo.getSem())
				&& beanOne.getSubject().equalsIgnoreCase(beanTwo.getSubject())
				&& !(beanOne.getTrackId().equalsIgnoreCase(beanTwo.getTrackId())))
			return true;
		return false;
	}
	
	private ResponseEntity<String> sendOkayResponse(String responseParam){
		return new ResponseEntity<>(responseParam, HttpStatus.OK);
	}


	@RequestMapping(value = "/examFeesReponse", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView examFeeFinalPaymentResponse(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		

		logger.info( request.getSession().getAttribute("userId") + " " + (String)request.getSession().getAttribute("trackId") +  " Exam Response Callback  ");
		String sapid = (String)request.getSession().getAttribute("userId");
		
		ebAuditLogger.info("{} examFeesReponse - {}",
							sapid,
							RequestResponseUtils.getRequestPayloadAsString(request));
		
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		//PENDING WHETHER TO SAVE ON FAIL OR NOT
	//	saveAllTransactionDetails(request);
		saveAllTransactionDetailsFromResponse(request);
	
		String onlineSeatBookingComplete = (String)request.getSession().getAttribute("onlineSeatBookingComplete");
		if("true".equals(onlineSeatBookingComplete)){
			return new ModelAndView("bookingStatus");
		}
		
		String trackId = (String)request.getSession().getAttribute("trackId");
		String totalFees = (String)request.getSession().getAttribute("totalFees");
		String errorMessage = null;
	
		boolean isAmountMatching = isAmountMatching(request, totalFees);
		boolean isTrackIdMatching = isTrackIdMatching(request, trackId);
		boolean isSuccessful = isTransactionSuccessful(request);
	
	//	if(!"Wallet".equals(typeOfPayment)){
	//		 isAmountMatching = isAmountMatching(request, totalFees);
	//		 isTrackIdMatching = isTrackIdMatching(request, trackId);
	//	}
	
		if (!isSuccessful) {
			errorMessage = "Error in processing payment. Error: " + request.getParameter("Error") + " Code: "
					+ request.getParameter("response_code");
		}
	
		if (!isAmountMatching) {
			errorMessage = "Error in processing payment. Error: Fees " + totalFees + " not matching with amount paid "
					+ request.getParameter("response_amount");
		}
	
		if (!isTrackIdMatching) {
			errorMessage = "Error in processing payment. Error: Track ID: " + trackId
					+ " not matching with Merchant Ref No. " + request.getParameter("merchant_ref_no");
		}
		
		logger.info( request.getSession().getAttribute("userId") + " " + (String)request.getSession().getAttribute("trackId") +  " Check Error In Payment " + errorMessage);
		if (errorMessage != null) {
			ModelAndView modelnView = new ModelAndView("bookingStatus");
			ExamBookingDAO dao = (ExamBookingDAO) act.getBean("examBookingDAO");
			if ("razorpay".equalsIgnoreCase(request.getParameter("payment_option"))
					|| "paytm".equalsIgnoreCase(request.getParameter("payment_option"))) {

				List<ExamBookingTransactionBean> beans = dao.getExamBookingTransactionBean(sapid, trackId);

				if (beans.stream()
						.allMatch(bookings -> "Online Payment Successful".equalsIgnoreCase(bookings.getTranStatus())
								|| "Online Payment Manually Approved".equalsIgnoreCase(bookings.getTranStatus())))
					return processSuccessTransaction(request, dao, modelnView, trackId);
			}
			return sendBackToExamCenterPage(request, response, errorMessage);
		}else{
			
			return saveSuccessfulTransaction(request, response, model);
		}
}
	
	
	
	
	
private boolean isTransactionSuccessful(HttpServletRequest request) {
		
		String transaction_status = request.getParameter("transaction_status");
		if("Payment Successfull".equalsIgnoreCase(transaction_status)) {
			return true;
		}else {
			return false;
		}
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
	
	@RequestMapping(value = "/m/examFeesReponse", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView mexamFeeFinalPaymentResponse(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		/*if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}*/
		String typeOfPayment = (String)request.getParameter("PaymentMethod");
		saveAllTransactionDetails(request);

		paymentHelper.setPaymentReturnUrl("exam_registration");
		String errorMessage = paymentHelper.checkErrorInPayment(request);
		if(errorMessage != null){
			ModelAndView mv = new ModelAndView("examresponse");
			mv.addObject("responseType","error");
			mv.addObject("response",errorMessage);
			return mv;
		}else{
			return msaveSuccessfulTransaction(request, response, model);
		}
	}
	
//	to be deleted, api shifted to rest controller
//	@RequestMapping(value = "/m/examFeesReponseSDK", method = {RequestMethod.GET, RequestMethod.POST})
//	public ResponseEntity<HashMap<String, String>> mexamFeeFinalPaymentResponseSDK(HttpServletRequest request) {
//		/*if(!checkSession(request, response)){
//			redirectToPortalApp(response);
//			return null;
//		}*/
//		/*String typeOfPayment = (String)request.getParameter("PaymentMethod");
//		 * */
//		saveAllTransactionDetails(request);
//		HashMap<String, String> responseData = new HashMap<String,String>();
//		paymentHelper.setPaymentReturnUrl("exam_registration");
//		String errorMessage = paymentHelper.checkErrorInPayment(request);
//		if(errorMessage != null){
//			responseData.put("status", "error");
//			responseData.put("error", errorMessage);
//			return new ResponseEntity<HashMap<String,String>>(responseData, HttpStatus.OK);
//		}/*else{
//			return msaveSuccessfulTransaction(request, response, model);
//		}*/
//		return null;	//remove after msaveSuccessfulTransaction function
//	}


	private void saveAllTransactionDetails(HttpServletRequest request) {

		try {
			String sapid = (String)request.getSession().getAttribute("userId");
			String trackId = (String)request.getSession().getAttribute("trackId");
			ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
			paymentHelper.setPaymentReturnUrl("exam_registration");
			ExamBookingTransactionBean bean = paymentHelper.CreateResponseBean(request);
			/*bean.setSapid(sapid);
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
			bean.setDescription(request.getParameter("Description"));*/
			
			logger.info(trackId + " " + sapid + "Save Trnaction Details  " + bean.toString());

			dao.insertOnlineTransaction(bean);

		} catch (Exception e) {
			
			logger.info(request.getSession().getAttribute("trackId") + " " + request.getSession().getAttribute("userId") + "Save Trnaction Details  Error " + e.getMessage());

		}
	}
	private void saveAllTransactionDetailsFromResponse(HttpServletRequest request) {

		try {
			String sapid = (String)request.getSession().getAttribute("userId");
			String trackId = (String)request.getSession().getAttribute("trackId");
			ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
//			paymentHelper.setPaymentReturnUrl("exam_registration");
//			ExamBookingTransactionBean bean = paymentHelper.CreateResponseBean(request);
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
//			bean.setIsFlagged(request.getParameter("IsFlagged"));
			bean.setPaymentID(request.getParameter("payment_id"));
			bean.setError(request.getParameter("error"));
			bean.setDescription(request.getParameter("description"));
			bean.setPaymentOption(request.getParameter("payment_option"));
			
			logger.info(trackId + " " + sapid + "Save Trnaction Details  " + bean.toString());

			dao.insertOnlineTransaction(bean);

		} catch (Exception e) {
			
			logger.info(request.getSession().getAttribute("trackId") + " " + request.getSession().getAttribute("userId") + "Save Trnaction Details  Error " + e.getMessage());

		}
	}
	
	/*private void msaveAllTransactionDetails(ExamBookingTransactionBean bean) {

		try {
			ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
			paymentHelper.setPaymentReturnUrl("exam_registration");
			ExamBookingTransactionBean bean = paymentHelper.CreateResponseBean(request);
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
	}*/

	// to do discuss adding double booking and 
	public ModelAndView saveSuccessfulTransaction(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}

		ModelAndView modelnView = new ModelAndView("bookingStatus");

		String sapid = (String)request.getSession().getAttribute("userId");
		String trackId = (String)request.getSession().getAttribute("trackId");
		StudentExamBean student = (StudentExamBean)request.getSession().getAttribute("studentExam");

		/*HashMap<String,String> corporateCenterUserMapping = (HashMap<String,String>)request.getSession().getAttribute("corporateCenterUserMapping");
		ExamCenterDAO examCenterDao = (ExamCenterDAO)act.getBean("examCenterDAO");
		HashMap<String, String> getCorporateExamCenterIdNameMap = new HashMap<String, String>();
		boolean isCorporate = false;
		if(corporateCenterUserMapping.containsKey(sapid)){
			isCorporate = true;
			getCorporateExamCenterIdNameMap = examCenterDao.getCorporateExamCenterIdNameMap();
		}*/

		try {
			ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
			
			if ("razorpay".equalsIgnoreCase(request.getParameter("payment_option"))
					|| "paytm".equalsIgnoreCase(request.getParameter("payment_option"))) {

				List<ExamBookingTransactionBean> beans = dao.getExamBookingTransactionBean(sapid, trackId);

				if (beans.stream()
						.allMatch(bookings -> "Online Payment Successful".equalsIgnoreCase(bookings.getTranStatus())
								|| "Online Payment Manually Approved".equalsIgnoreCase(bookings.getTranStatus())))
					return processSuccessTransaction(request, dao, modelnView, trackId);
			}
			
			ExamCenterDAO edao = (ExamCenterDAO)act.getBean("examCenterDAO");
//			paymentHelper.setPaymentReturnUrl("exam_registration");
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
//			bean.setIsFlagged(request.getParameter("IsFlagged"));
			bean.setPaymentID(request.getParameter("payment_id"));
			bean.setError(request.getParameter("error"));
			bean.setDescription(request.getParameter("description"));
			bean.setPaymentOption(request.getParameter("payment_option"));
			
			if(bean.getLastModifiedBy() == null)
				bean.setLastModifiedBy(sapid);

			//List<ExamBookingTransactionBean> examBookings = dao.updateSeatsForOnlineTransaction(bean);

			//Below method is made using single Connection to ensure Commit and Rollback
			List<ExamBookingTransactionBean> examBookings = dao.updateSeatsForOnlineUsingSingleConnection(bean, student.isCorporateExamCenterStudent());
			logger.info(trackId + " " + student.getSapid()  + "examBookings Updated " + examBookings.toString());

			request.getSession().setAttribute("examBookings", examBookings);
			request.getSession().setAttribute("onlineSeatBookingComplete", "true");

			ArrayList<ExamBookingTransactionBean> bookingsList = (ArrayList<ExamBookingTransactionBean>)request.getSession().getAttribute("bookingsToRelease");
			MailSender mailSender = (MailSender)act.getBean("mailer");
			List<ExamBookingTransactionBean> confirmRL = new ArrayList<ExamBookingTransactionBean>();
			if(bookingsList != null ){
				logger.info("{} {} received to-release bookings subject : {}", sapid, trackId, bookingsList);
				
				confirmRL = bookingsList.stream()
				.filter(k -> examCenterDAO.checkReleasingSubjectPresent(k.getSapid(), k.getSubject(), trackId))
				.collect(Collectors.toList());
				
				logger.info("{} {} confirm Release bookings : {}", sapid, trackId,confirmRL);
				
				if(confirmRL.size() > 0) {
					examCenterDAO.releaseBookings(sapid, confirmRL, bean, "false", false, student.isCorporateExamCenterStudent());
					
					//if(dao.getIsExtendedExamRegistrationLiveForRealTime()) {
						//examRegisterlogger.info("{} {} Real Time Registartion called", sapid, trackId);
						//examRegistrationRealTimeStrategy.registrationOnMettlAndReleaseBooking(examBookings,confirmRL);
					//}
					
					//Added by shivam.pandey.EXT - START
					exambookingAuditService.asyncInsertExamBookingAudit(bookingsList, "false", sapid);
					//Added by shivam.pandey.EXT - START	
				}
				else {
					mailSender.sendEmailForDemoExamReminderAfterRegistration(student);
				}
			}
			
			examRegisterlogger.info("Real Time Registartion called from saveSuccessfulTransaction method"+dao.getIsExtendedExamRegistrationLiveForRealTime());
			if(dao.getIsExtendedExamRegistrationLiveForRealTime()) {
			examRegisterlogger.info("{} {} Real Time Registartion called", sapid, trackId);
			examRegistrationRealTimeStrategy.registrationOnMettlAndReleaseBooking(examBookings,confirmRL);
			}
			
			request.setAttribute("success","true");
			request.setAttribute("successMessage",BOOKING_SUCCESS_MSG);

			mailSender.sendBookingSummaryEmail(request, dao,edao);
			examBookingHelper.createAndUploadHallTicketAndExamFeeReceipt(sapid, student.isCorporateExamCenterStudent());
				
		} catch (Exception e) {
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			logger.info(trackId + " " + student.getSapid()  + " examBookings Updated " + errors.toString());
			logger.info(trackId + " " + student.getSapid()  + " Error in Saving Successful Transaction: " + errors);
			MailSender mailSender = (MailSender)act.getBean("mailer");
			mailSender.mailStackTrace("Error in Saving Successful Transaction", e);

			request.getSession().setAttribute("onlineSeatBookingComplete", "false");
			/*if(isCorporate){
				request.getSession().setAttribute("examCenterIdNameMap", getCorporateExamCenterIdNameMap);
			}else{
				request.getSession().setAttribute("examCenterIdNameMap", getExamCenterIdNameMap());

			}*/

			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Seats NOT booked. Error in recording your transaction details. Please contact Head Office to get it sorted out.");
			return new ModelAndView("selectExamCenter");
		}
		request.getSession().setAttribute("onlineSeatBookingComplete", "true");
		/*if(isCorporate){
			request.getSession().setAttribute("examCenterIdNameMap", getCorporateExamCenterIdNameMap);
		}else{
			request.getSession().setAttribute("examCenterIdNameMap", getExamCenterIdNameMap());

		}*/
		return modelnView;
	}

	
	private ModelAndView processSuccessTransaction(HttpServletRequest request, ExamBookingDAO dao, ModelAndView view, String trackId) {
		logger.info("Payment for track id : {} was already marked as successful", trackId);
		List<ExamBookingTransactionBean> examBookings = dao.getSubjectsCentersForTrackId(trackId);
		request.getSession().setAttribute("examBookings", examBookings);
		request.getSession().setAttribute("onlineSeatBookingComplete", "true");
		request.setAttribute("success","true");
		request.setAttribute("successMessage",BOOKING_SUCCESS_MSG);
		return view;	
	}
	
	public ModelAndView msaveSuccessfulTransaction(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		/*if(!checkSession(request, response)){
		redirectToPortalApp(response);
		return null;
	}*/

	ModelAndView mv = new ModelAndView("examresponse");

	String sapid = (String)request.getSession().getAttribute("userId");
	String trackId = (String)request.getSession().getAttribute("trackId");
	ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
	StudentExamBean student = dao.getSingleStudentsData(sapid);

	/*HashMap<String,String> corporateCenterUserMapping = (HashMap<String,String>)request.getSession().getAttribute("corporateCenterUserMapping");
	ExamCenterDAO examCenterDao = (ExamCenterDAO)act.getBean("examCenterDAO");
	HashMap<String, String> getCorporateExamCenterIdNameMap = new HashMap<String, String>();
	boolean isCorporate = false;
	if(corporateCenterUserMapping.containsKey(sapid)){
		isCorporate = true;
		getCorporateExamCenterIdNameMap = examCenterDao.getCorporateExamCenterIdNameMap();
	}*/

	try {
		ExamBookingTransactionBean bean = new ExamBookingTransactionBean();
		ExamCenterDAO edao = (ExamCenterDAO)act.getBean("examCenterDAO");
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
		
		if(bean.getLastModifiedBy() == null)
			bean.setLastModifiedBy(sapid);

		//List<ExamBookingTransactionBean> examBookings = dao.updateSeatsForOnlineTransaction(bean);

		//Below method is made using single Connection to ensure Commit and Rollback
		List<ExamBookingTransactionBean> examBookings = dao.updateSeatsForOnlineUsingSingleConnection(bean, student.isCorporateExamCenterStudent());

		request.getSession().setAttribute("examBookings", examBookings);
		request.getSession().setAttribute("onlineSeatBookingComplete", "true");


		ArrayList<ExamBookingTransactionBean> bookingsList = (ArrayList<ExamBookingTransactionBean>)request.getSession().getAttribute("bookingsToRelease");
		if(bookingsList != null && bookingsList.size() > 0){
			ExamCenterDAO ecDao = (ExamCenterDAO)act.getBean("examCenterDAO");
			ecDao.releaseBookings(sapid, bookingsList, bean, "false", false, student.isCorporateExamCenterStudent());
		}
		mv.addObject("responseType","success");
		mv.addObject("response",BOOKING_SUCCESS_MSG);

		MailSender mailSender = (MailSender)act.getBean("mailer");
		mailSender.sendBookingSummaryEmail(request, dao,edao);
		examBookingHelper.createAndUploadHallTicketAndExamFeeReceipt(sapid, student.isCorporateExamCenterStudent());

	} catch (Exception e) {
		MailSender mailSender = (MailSender)act.getBean("mailer");
		mailSender.mailStackTrace("Error in Saving Successful Transaction", e);

		request.getSession().setAttribute("onlineSeatBookingComplete", "false");
		/*if(isCorporate){
			request.getSession().setAttribute("examCenterIdNameMap", getCorporateExamCenterIdNameMap);
		}else{
			request.getSession().setAttribute("examCenterIdNameMap", getExamCenterIdNameMap());

		}*/
		
		mv.addObject("responseType", "error");
		mv.addObject("response", "Seats NOT booked. Error in recording your transaction details. Please contact Head Office to get it sorted out.");
		return mv;
	}
	request.getSession().setAttribute("onlineSeatBookingComplete", "true");
	/*if(isCorporate){
		request.getSession().setAttribute("examCenterIdNameMap", getCorporateExamCenterIdNameMap);
	}else{
		request.getSession().setAttribute("examCenterIdNameMap", getExamCenterIdNameMap());

	}*/
	return mv;
}
	
	private ModelAndView processSuccessTransaction(HttpServletRequest request, ModelAndView modelnView, List<ExamBookingTransactionBean> subjectsCentersForTrackId) {
		List<ExamBookingTransactionBean> examBookings = subjectsCentersForTrackId.stream().sorted((beanOne, beanTwo) -> beanOne.getExamDate().compareTo(beanTwo.getExamDate())).collect(Collectors.toList());
 		request.getSession().setAttribute("examBookings", examBookings);
		request.setAttribute("success", "true");
		request.setAttribute("successMessage", BOOKING_SUCCESS_MSG);
		request.getSession().setAttribute("onlineSeatBookingComplete", "true");
		return modelnView;
	}
	
	
	
	
	@RequestMapping(value = "/downloadBookingPdf", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadBookingPdf(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		ModelAndView modelnView = new ModelAndView("bookingStatus");
		response.setContentType("application/pdf");
		return modelnView;
	}



	private ModelAndView sendBackToExamCenterPage(HttpServletRequest request,HttpServletResponse response, String errorMessage) {
		if(!checkSession(request, null)){
			redirectToPortalApp(response);
			return null;
		}
		request.setAttribute("error", "true");
		request.setAttribute("errorMessage", errorMessage);

		List<TimetableBean> timeTableList = (List<TimetableBean>)request.getSession().getAttribute("timeTableList");
		StudentExamBean student = (StudentExamBean)request.getSession().getAttribute("studentExam");
		getAvailableCenters(request, timeTableList, student);


		ExamBookingExamBean examBooking = new ExamBookingExamBean();
		ModelAndView modelnView = new ModelAndView("selectExamCenter");
		modelnView.addObject("examBooking", examBooking);
		return modelnView;
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



	@RequestMapping(value = "/searchDDsToApproveForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView searchDDsToApproveForm(HttpServletRequest request, HttpServletResponse response, Model m) {
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
	public ModelAndView searchExamBookingTOChangeCenterForm(HttpServletRequest request, HttpServletResponse response, Model m) {
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

		StudentExamBean student = dao.getSingleStudentsData(examBooking.getSapid());
		ArrayList<String> subjects = new ArrayList<>();
		for (ExamBookingTransactionBean bean : releasedBookingsList) {
			subjects.add(bean.getSubject());
		}
		List<TimetableBean> timeTableList = dao.getTimetableListForGivenSubjects(subjects, student);
		getAvailableCenters(request, timeTableList, student);
		modelnView.addObject("student", student);
		return modelnView;
	}

	/*
	@RequestMapping(value = "/changeCenterForStudents",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView changeCenterForStudents(HttpServletRequest request, HttpServletResponse response, @ModelAttribute ExamBookingTransactionBean examBooking){
		ModelAndView modelnView = new ModelAndView("searchExamBooking");
		ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
		request.getSession().setAttribute("examBooking", examBooking);

		List<ExamBookingTransactionBean> bookingsList = new ArrayList<>();
		ArrayList<String> selectedCenters = examBooking.getSelectedCenters();
		List<TimetableBean> timeTableList = (List<TimetableBean>)request.getSession().getAttribute("timeTableList");
		Map<String, TimetableBean> subjectTimetableMap = getSubjectTimetableMap(timeTableList);

		for (int i = 0; i < selectedCenters.size(); i++) {
			String subjectCenter = selectedCenters.get(i);

			String[] parts = subjectCenter.split("\\|");

			//String subject = subjectCenter.substring(0,subjectCenter.indexOf("|"));
			//String centerId = subjectCenter.substring(subjectCenter.indexOf("|")+1, subjectCenter.length() );

			String subject = parts[0];
			String centerId = parts[1];
			String startTime = parts[2];

			ExamBookingTransactionBean bean = new ExamBookingTransactionBean();
			TimetableBean ttBean = subjectTimetableMap.get(subject + startTime);

			bean.setYear(examBooking.getYear());
			bean.setMonth(examBooking.getMonth());
			bean.setSapid(examBooking.getSapid());
			bean.setSubject(subject);
			bean.setCenterId(centerId);
			bean.setExamDate(ttBean.getDate());
			bean.setExamTime(ttBean.getStartTime());
			bean.setExamEndTime(ttBean.getEndTime());

			bookingsList.add(bean);
		}

		List<ExamBookingTransactionBean> completeBookings = dao.updateCenterForReleasedSeatsForStudents(examBooking.getSapid(), 
				examBooking.getYear(), examBooking.getMonth(), bookingsList);

		setSuccess(request, "Exam Center Changed Successfully");
		return searchExamBookingTOChangeCenter(request, response, examBooking);
	}*/


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
	public ModelAndView queryTransactionStatusForm(HttpServletRequest request, HttpServletResponse response, Model m) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
		}
		ModelAndView modelnView = new ModelAndView("transactionStatus");
		return modelnView;
	}


	@RequestMapping(value = "/queryTransactionStatus",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView queryTransactionStatus(HttpServletRequest request, HttpServletResponse response){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
		}
		ModelAndView modelnView = new ModelAndView("transactionStatus");
		List<ExamBookingTransactionBean> transactionResponseList = new ArrayList<>();
		HashMap<String, ExamBookingTransactionBean> trackIdTransactionMap = new HashMap<>();
		try{
			String sapid = request.getParameter("sapid");
			ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
			ArrayList<ExamBookingTransactionBean> unSuccessfulExamBookings = dao.getUnSuccessfulExamBookings(sapid);
			
			RestTemplate restTemplate = new RestTemplate();
			
			for (int i = 0; i < unSuccessfulExamBookings.size(); i++) {
				ExamBookingTransactionBean bean = unSuccessfulExamBookings.get(i);
				String trackId = bean.getTrackId();
				XMLParser parser = new XMLParser();
				String subject = bean.getSubject();

				TransactionsBean transactionsBean = new TransactionsBean();

				transactionsBean.setTrack_id(bean.getTrackId());

				String url = SERVER_PATH + "paymentgateways/m/getTransactionStatus";
//				String url = "http://localhost:8080/" + "paymentgateways/m/getTransactionStatus";
				HttpHeaders headers = new HttpHeaders();

				headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
				HttpEntity<TransactionsBean> entity = new HttpEntity<TransactionsBean>(transactionsBean, headers);

				transactionsBean = restTemplate.exchange(url, HttpMethod.POST, entity, TransactionsBean.class)
						.getBody();

				if (PAYMENT_SUCCESSFUL.equalsIgnoreCase(transactionsBean.getTransaction_status())) {
					bean.setResponseMessage(transactionsBean.getResponse_message());
					bean.setTransactionID(transactionsBean.getTransaction_id());
					// bean.setRequestID(request.getParameter("ORDERID"));
					bean.setMerchantRefNo(transactionsBean.getMerchant_ref_no());
					bean.setSecureHash(transactionsBean.getSecure_hash());
					bean.setRespAmount(transactionsBean.getResponse_amount());
					bean.setRespTranDateTime(transactionsBean.getResponse_transaction_date_time());
					bean.setResponseCode(transactionsBean.getResponse_code());
					bean.setRespPaymentMethod(transactionsBean.getResponse_payment_method());
					// bean.setIsFlagged(request.getParameter("IsFlagged"));
					bean.setPaymentID(transactionsBean.getPayment_id());
					bean.setBankName(transactionsBean.getBank_name());
//					bean.setError(transactionsBean.getError());
					bean.setDescription("Exam fees for " + bean.getSapid());
					bean.setStatus(transactionsBean.getTransaction_status());
				} else {
					bean.setError(transactionsBean.getError());
					bean.setErrorCode(transactionsBean.getResponse_message());
				}
//				else {
//					String xmlResponse = parser.queryTransactionStatus(trackId, ACCOUNT_ID, SECURE_SECRET);
//					parser.parseResponse(xmlResponse, bean);
//				}

				bean.setTrackId(trackId);
				bean.setSapid(sapid);
				bean.setSubject(subject);
				bean.setLastModifiedBy(sapid);
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
//			e.printStackTrace();
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in retriving details. Please try again");
		}
		return modelnView;
	}

	@RequestMapping(value = "/approveTransactionsForTrackId",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView approveTransactionsForTrackId(HttpServletRequest request, HttpServletResponse response){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView modelnView = new ModelAndView("transactionStatus");
		ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
		String userId = (String)request.getSession().getAttribute("userId");
		HashMap<String, ExamBookingTransactionBean> trackIdTransactionMap = (HashMap<String, ExamBookingTransactionBean>)request.getSession().getAttribute("trackIdTransactionMap");
		String sapIdForApprovedTransaction = (String)request.getSession().getAttribute("sapIdForApprovedTransaction");
		
		try{
			String trackId = request.getParameter("trackId");
			ExamBookingTransactionBean bean = trackIdTransactionMap.get(trackId);
			ArrayList<ExamBookingTransactionBean> confirmedBookings = new ArrayList<ExamBookingTransactionBean>();
			if("Project".equalsIgnoreCase(bean.getSubject()) || "Module 4 - Project".equalsIgnoreCase(bean.getSubject())){
//				confirmedBookings = dao.getConfirmedProjectBooking(bean.getSapid()); // Commented to make two cycle live
				String method = "approveTransactionsForTrackId()";
				AssignmentFileBean examMonthYearBean = eligibilityService.getStudentApplicableExamMonthYear(bean.getSapid(), bean.getSubject(), method);
				confirmedBookings = (ArrayList<ExamBookingTransactionBean>) eligibilityService.getConfirmedProjectBookingApplicableCycle(bean.getSapid(), examMonthYearBean.getMonth(), examMonthYearBean.getYear());
			}else{
				confirmedBookings = dao.getConfirmedBooking(bean.getSapid());
			}
			
			bean.setLastModifiedBy(userId);
			int noOfRowsUpdated = dao.approveOnlineTransactions(trackId, bean, confirmedBookings);
			request.setAttribute("success","true");
			request.setAttribute("successMessage","Transaction approved successfully for "+noOfRowsUpdated+" subjects. Please ask student to choose center and book seat");

			StudentExamBean student = dao.getSingleStudentsData(sapIdForApprovedTransaction);

			MailSender mailSender = (MailSender)act.getBean("mailer");
			if("Project".equalsIgnoreCase(bean.getSubject()) || "Module 4 - Project".equalsIgnoreCase(bean.getSubject())){
			mailSender.sendProjectTransactionApproveEmail(student, bean);
			}else{
				mailSender.sendTransactionApproveEmail(student, bean);
			}
			request.getSession().setAttribute("sapIdForApprovedTransaction", null);
		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in approving transaction");
		}
		return modelnView;
	}
	@RequestMapping(value="/adHocRefundForm",method={RequestMethod.GET,RequestMethod.POST})
	public String adHocRefundForm(HttpServletRequest request, HttpServletResponse response){

		return "adHocRefundForm";
	}

	@RequestMapping(value="/adHocRefundExamFees",method={RequestMethod.POST})
	public ModelAndView adHocRefundExamFees(HttpServletRequest request, HttpServletResponse response){
		ModelAndView modelAndView = new ModelAndView("adHocRefundForm");
		ExamAdhocPaymentBean refundBean = new ExamAdhocPaymentBean();
		ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
		String sapid = request.getParameter("sapid");
		String trackId = request.getParameter("trackId");
		refundBean.setSapId(sapid);
		refundBean.setMerchantRefNo(trackId);
		refundBean.setCreatedBy(sapid);
		refundBean.setLastModifiedBy(sapid);
		ArrayList<ExamBookingTransactionBean> getConfirmedBooking = dao.getConfirmedBooking(refundBean.getSapId());
		List<ExamBookingTransactionBean> transactionResponseList = new ArrayList<>();
		HashMap<String, ExamBookingTransactionBean> trackIdTransactionMap = new HashMap<>();


		for(int i = 0;i<getConfirmedBooking.size();i++){
			ExamBookingTransactionBean examBookingTransaction = getConfirmedBooking.get(i);

			String xmlResponse = "";
			XMLParser parser = new XMLParser();
			try{
				xmlResponse = parser.initiateAdHocRefund(trackId, ACCOUNT_ID, SECURE_SECRET,examBookingTransaction);
				dao.insertAdHocRefundRecord(refundBean);
			}catch(Exception e){
				
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Error in retriving details. Please try again");
			}

			parser.parseResponse(xmlResponse,examBookingTransaction);
			transactionResponseList.add(examBookingTransaction);

		}
		modelAndView.addObject("refundBean", refundBean);
		return modelAndView;

	}


	@RequestMapping(value = "/queryFeesPaidForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView queryFeesPaidForm(HttpServletRequest request, HttpServletResponse response, Model m) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
		}
		ModelAndView modelnView = new ModelAndView("refund");
		return modelnView;
	}


	@RequestMapping(value = "/queryFeesPaid",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView queryFeesPaid(HttpServletRequest request, HttpServletResponse response){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
		}
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
				try {
					if("paytm".equalsIgnoreCase(bean.getPaymentOption())) {
						paymentHelper.setPaymentReturnUrl("exam_registration");
						JsonObject jsonObject = paymentHelper.getPaytmTransactionStatus(trackId);
						String STATUS = jsonObject.get("STATUS").getAsString();
						if("TXN_SUCCESS".equalsIgnoreCase(STATUS)) {
							bean.setResponseMessage(jsonObject.get("RESPMSG").getAsString());
							bean.setTransactionID(jsonObject.get("TXNID").getAsString());
							//bean.setRequestID(request.getParameter("ORDERID"));
							bean.setMerchantRefNo(jsonObject.get("ORDERID").getAsString());
							bean.setSecureHash(jsonObject.get("CHECKSUMHASH").getAsString());
							bean.setRespAmount(jsonObject.get("TXNAMOUNT").getAsString());
							bean.setRespTranDateTime(jsonObject.get("TXNDATE").getAsString());
							bean.setResponseCode(jsonObject.get("RESPCODE").getAsString());
							bean.setRespPaymentMethod(jsonObject.get("PAYMENTMODE").getAsString());
							//bean.setIsFlagged(request.getParameter("IsFlagged"));
							if(jsonObject.get("BANKTXNID") != null) {
								bean.setPaymentID(jsonObject.get("BANKTXNID").getAsString());
							}
							if(jsonObject.get("BANKNAME") != null) {
								bean.setBankName(jsonObject.get("BANKNAME").getAsString());
							}
							//bean.setError(request.getParameter("Error"));
							bean.setDescription("Exam Fees for " + sapid);
							bean.setStatus(jsonObject.get("STATUS").getAsString());
						}else {
							bean.setError(jsonObject.get("RESPMSG").getAsString());
							bean.setErrorCode(jsonObject.get("RESPCODE").getAsString());
						}
					}
					else if("payu".equalsIgnoreCase(bean.getPaymentOption())) {
						paymentHelper.setPaymentReturnUrl("exam_registration");
						JsonObject jsonObj = paymentHelper.getPayuTransactionStatus(trackId);
						String STATUS = jsonObj.get("status").getAsString();
						JsonObject transaction_details = jsonObj.get("transaction_details").getAsJsonObject();
						JsonObject dataObj = transaction_details.get(trackId).getAsJsonObject();
						String MSG = dataObj.get("status").getAsString();
						if("1".equalsIgnoreCase(STATUS) && "success".equalsIgnoreCase(MSG)) {
							bean.setStatus(dataObj.get("status").getAsString());
							bean.setTransactionID(dataObj.get("mihpayid").getAsString());
							bean.setMerchantRefNo(dataObj.get("txnid").getAsString());
							bean.setRespAmount(dataObj.get("transaction_amount").getAsString());
							bean.setRespTranDateTime(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
							bean.setResponseMessage(dataObj.get("status").getAsString());
							bean.setRespPaymentMethod(dataObj.get("mode").getAsString());
							//bean.setIsFlagged(request.getParameter("IsFlagged"));
							if(dataObj.get("bank_ref_num") != null) {
								bean.setPaymentID(dataObj.get("bank_ref_num").getAsString());
							}
							if(dataObj.get("bankcode") != null) {
								bean.setBankName(dataObj.get("bankcode").getAsString());
							}
							
							bean.setDescription(dataObj.get("productinfo").getAsString());
							bean.setTransactionType(dataObj.get("PG_TYPE").getAsString());
						}else {
							bean.setError(dataObj.get("error_Message").getAsString());
							bean.setErrorCode(dataObj.get("error_code").getAsString());
						}
					}else {
						XMLParser parser = new XMLParser();
						String xmlResponse = parser.queryTransactionStatus(trackId, ACCOUNT_ID, SECURE_SECRET);
						parser.parseResponse(xmlResponse, bean);
					}
					bean.setTrackId(trackId);
					bean.setSapid(sapid);
					transactionResponseList.add(bean);
					trackIdTransactionMap.put(trackId, bean);
				}
				catch (Exception e) {
					// TODO: handle exception
					
				}
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
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
		}
		ModelAndView modelnView = new ModelAndView("refund");

		HashMap<String, ExamBookingTransactionBean> trackIdTransactionMap = (HashMap<String, ExamBookingTransactionBean>)request.getSession().getAttribute("trackIdTransactionMap");
		String sapIdForRefund = (String)request.getSession().getAttribute("sapIdForRefund");
		try{
			String trackId = request.getParameter("trackId");
			ExamBookingTransactionBean bean = trackIdTransactionMap.get(trackId);
			ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
			XMLParser parser = new XMLParser();
			String xmlResponse = parser.initiateRefund(trackId, ACCOUNT_ID, SECURE_SECRET, bean);
			String transactionType = parser.getTransactionTypeFromResponse(xmlResponse, bean);

			if(transactionType != null &&  "Refunded".equals(transactionType)){
				int noOfRowsUpdated = dao.updateRefundTransactions(trackId, bean);
				request.setAttribute("success","true");
				request.setAttribute("successMessage","Refund Initiated Successfully.");
			}else{
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", transactionType);
			}


			StudentExamBean student = dao.getSingleStudentsData(sapIdForRefund);

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
	public String uploadExamFeeExemptForm(HttpServletRequest request, HttpServletResponse response, Model m) {

		FileBean fileBean = new FileBean();
		m.addAttribute("fileBean",fileBean);
		m.addAttribute("yearList", ACAD_YEAR_LIST);

		return "uploadExamFeeExempt";
	}



	@RequestMapping(value = "/uploadExamFeeExempt", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView uploadExamFeeExempt(FileBean fileBean, BindingResult result,HttpServletRequest request, Model m){
		ModelAndView modelnView = new ModelAndView("uploadExamFeeExempt");
		try{

			String userId = (String)request.getSession().getAttribute("userId");
			ExcelHelper excelHelper = new ExcelHelper();
			ArrayList<List> resultList = excelHelper.readExamFeeExemptExcel(fileBean, userId);

			List<StudentMarksBean> examFeeExemptList = (ArrayList<StudentMarksBean>)resultList.get(0);
			List<StudentMarksBean> errorBeanList = (ArrayList<StudentMarksBean>)resultList.get(1);

			fileBean = new FileBean();
			m.addAttribute("fileBean",fileBean);
			m.addAttribute("yearList", ACAD_YEAR_LIST);

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

	@RequestMapping(value = "/uploadExamFeeExemptSubjectsForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String uploadExamFeeExemptSubjectsForm(HttpServletRequest request, HttpServletResponse response, Model m) {

		FileBean fileBean = new FileBean();
		m.addAttribute("fileBean",fileBean);
		m.addAttribute("yearList", ACAD_YEAR_LIST);

		return "uploadExamFeeExemptSubjects";
	}

	@RequestMapping(value = "/uploadExamFeeExemptSubjects", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView uploadExamFeeExemptSubjects(FileBean fileBean, BindingResult result,HttpServletRequest request, Model m){
		ModelAndView modelnView = new ModelAndView("uploadExamFeeExemptSubjects");
		try{

			String userId = (String)request.getSession().getAttribute("userId");
			ExcelHelper excelHelper = new ExcelHelper();
			ArrayList<List> resultList = excelHelper.readExamFeeExemptSubjectsExcel(fileBean, userId, getSubjectList());

			List<StudentMarksBean> examFeeExemptList = (ArrayList<StudentMarksBean>)resultList.get(0);
			List<StudentMarksBean> errorBeanList = (ArrayList<StudentMarksBean>)resultList.get(1);

			fileBean = new FileBean();
			m.addAttribute("fileBean",fileBean);
			m.addAttribute("yearList", ACAD_YEAR_LIST);

			if(errorBeanList.size() > 0){
				request.setAttribute("errorBeanList", errorBeanList);
				return modelnView;
			}

			ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");

			ArrayList<String> errorList = dao.batchUpdateExemptFeeSubjectsList(examFeeExemptList);

			if(errorList.size() == 0){
				this.TryRefreshCacheToAllServer(0,"ExamBooking");
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
	public ModelAndView takeDemoTest(HttpServletRequest request, HttpServletResponse response) {

		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}

		ModelAndView modelnView = new ModelAndView("demoTestRedirect");

		String userId = request.getParameter("userId");
		modelnView.addObject("password", "password" );
		modelnView.addObject("userId", userId );

		return modelnView;
	}

	/*Commented By Siddheswar_K because logic moved to the ExamBookingReleaseController
	@RequestMapping(value = "/admin/searchBookingsToReleaseForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String searchBookingsToReleaseForm(HttpServletRequest request, HttpServletResponse response, Model m) {
	
		ExamBookingTransactionBean booking = new ExamBookingTransactionBean();
		m.addAttribute("booking",booking);
		m.addAttribute("yearList", ACAD_YEAR_LIST);
	
		return "releaseBooking";
	}

	@RequestMapping(value = "/admin/searchBookingsToRelease",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView searchBookingsToRelease(HttpServletRequest request, HttpServletResponse response, @ModelAttribute ExamBookingTransactionBean booking){
		ModelAndView modelnView = new ModelAndView("releaseBooking");
		ExamCenterDAO dao = (ExamCenterDAO)act.getBean("examCenterDAO");
		request.getSession().setAttribute("booking", booking);

		//ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
		ArrayList<ExamBookingTransactionBean> confirmedBookings = dao.getConfirmedOrReleasedBooking(booking);
		request.getSession().setAttribute("confirmedBookings", confirmedBookings);

		modelnView.addObject("confirmedBookings", confirmedBookings);

		modelnView.addObject("booking", booking);

		modelnView.addObject("yearList", ACAD_YEAR_LIST);
		if(confirmedBookings == null || confirmedBookings.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No booked subjects found for this student.");
		}else{
			modelnView.addObject("rowCount", confirmedBookings.size());
		}
		Map<String,String> corporateCenterUserMapping = getCorporateCenterUserMapping();
		if(corporateCenterUserMapping.containsKey(booking.getSapid())){
			request.getSession().setAttribute("examCenterIdNameMap", dao.getCorporateExamCenterIdNameMap());
		}else{
			request.getSession().setAttribute("examCenterIdNameMap", getExamCenterIdNameMap());
		}
		return modelnView;
	}*/

	@RequestMapping(value = "/searchBookingsToReleaseStudent",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView searchBookingsToReleaseStudent(HttpServletRequest request, HttpServletResponse response){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
		String sapid = (String)request.getSession().getAttribute("userId");
		ModelAndView modelnView = new ModelAndView("releaseBookingStudent");
		
		boolean  isExamRegistraionLive = false;
		if(demoSapids.contains(sapid)) {
			isExamRegistraionLive = true;
			modelnView.addObject("isExamRegistraionLive", isExamRegistraionLive);
		}else {
			isExamRegistraionLive = isExamRegistraionLive(request, sapid);
			
			if(!isExamRegistraionLive ) {
				
				isExamRegistraionLive = eDao.isStudentAllowedToChangeSlotPriorNumberOfDays(sapid, NUMBER_ONE);
				
			}
			
			modelnView.addObject("isExamRegistraionLive", isExamRegistraionLive);
		}
		

		ArrayList<ExamBookingTransactionBean> confirmedBookings = eDao.getConfirmedBookings(sapid);
		ArrayList<ExamBookingTransactionBean> doNotConsiderForReleaseSubjectsInExamBooking = new ArrayList<ExamBookingTransactionBean>();
		for(ExamBookingTransactionBean e : confirmedBookings){
			if("Project".equalsIgnoreCase(e.getSubject()) || "Module 4 - Project".equalsIgnoreCase(e.getSubject())){
				doNotConsiderForReleaseSubjectsInExamBooking.add(e);
			}
		}
		
		// To remove subjects from seat release UI that were booked for past date and if
		// next slot is before 24 hrs prior
		Date currentTimeDate = new Date();
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(currentTimeDate);
		calendar.add(Calendar.DATE, 1);
		Date dateTimePlusOneDate = calendar.getTime();
		
		confirmedBookings.removeIf(k -> 
		{
			Date examDateTime = null;
			String examDateAndTime = k.getExamDate() + " " + k.getExamTime();
			try {
				examDateTime = format.parse(examDateAndTime);
			} catch (Exception e) {
			}
			return dateTimePlusOneDate.after(examDateTime);
		});
		
		confirmedBookings.removeAll(doNotConsiderForReleaseSubjectsInExamBooking);
		
		ebAuditLogger.info("{} searchBookingsToReleaseStudent - confirmedBookings : {}", 
								sapid,
								logSubjectsFromList(confirmedBookings));

		ExamBookingTransactionBean booking = new ExamBookingTransactionBean();
		modelnView.addObject("booking", booking);


		modelnView.addObject("confirmedBookings", confirmedBookings);
		modelnView.addObject("SEAT_RELEASED", SEAT_RELEASED);
		if(confirmedBookings == null || confirmedBookings.size() == 0){
			modelnView.addObject("rowCount", 0);
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No booked subjects found.");
		}else{
			modelnView.addObject("rowCount", confirmedBookings.size());
		}
		Map<String,String> corporateCenterUserMapping = (HashMap<String,String>)request.getSession().getAttribute("corporateCenterUserMapping");

		if(corporateCenterUserMapping.containsKey(sapid)){
			request.getSession().setAttribute("examCenterIdNameMap", eDao.getCorporateExamCenterIdNameMap());
		}else{
			request.getSession().setAttribute("examCenterIdNameMap", getExamCenterIdNameMap());
		}
		return modelnView;
	}

	
//	 to be deleted, api shifted to rest controller
//	@RequestMapping(value = "/m/searchBookingsToReleaseStudent",  method = {RequestMethod.GET, RequestMethod.POST})
//	public ResponseEntity<BookedResponse> msearchBookingsToReleaseStudent(HttpServletRequest request, HttpServletResponse response){
//		//ArrayList<HashMap> response_api = new ArrayList<>();
//		BookedResponse api_response = new BookedResponse();
//		String sapid = (String)request.getParameter("sapid");
//
//		boolean isExamRegistraionLive = misExamRegistraionLive(sapid);
//		api_response.setExamregistration("false");
//		if(isExamRegistraionLive){
//			ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
//			api_response.setExamregistration("true");
//			ArrayList<ExamBookingTransactionBean> confirmedBookings = eDao.getConfirmedBookings(sapid);
//			
//
//			if(confirmedBookings == null || confirmedBookings.size() == 0){
//				api_response.setData(new ArrayList<ExamBookingTransactionBean>());
//			}else{
//				api_response.setData(confirmedBookings);
//			}
//			ExamCenterDAO dao = (ExamCenterDAO)act.getBean("examCenterDAO");
//			Map<String, String> corporateCenterUserMapping = dao.getCorporateCenterUserMapping();
//			
//			if(corporateCenterUserMapping.containsKey(sapid)){
//				api_response.setCenters(eDao.getCorporateExamCenterIdNameMap());
//			}else{
//				api_response.setCenters(getExamCenterIdNameMap());
//			}
//		}
//		//message examregistration is not live
//		return new ResponseEntity<BookedResponse>(api_response, HttpStatus.OK);
//	}
	
	
	/*Commented By Siddheswar_K because logic moved to the ExamBookingReleaseController 
	@RequestMapping(value = "/admin/releaseBookings",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView releaseBookings(HttpServletRequest request, HttpServletResponse response, @ModelAttribute ExamBookingTransactionBean booking){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}

		ModelAndView modelnView = new ModelAndView("releaseBooking");
		ExamCenterDAO dao = (ExamCenterDAO)act.getBean("examCenterDAO");

		String noCharges = request.getParameter("noCharges");

		String sapid = booking.getSapid();
		ArrayList<ExamBookingTransactionBean> confirmedBookings = (ArrayList<ExamBookingTransactionBean>)request.getSession().getAttribute("confirmedBookings");
		ArrayList<String> releaseSubjects = booking.getReleaseSubjects();

		List<ExamBookingTransactionBean> bookingsList = new ArrayList<>();
		String email = "";
		for (int i = 0; i < confirmedBookings.size(); i++) {
			ExamBookingTransactionBean bean = confirmedBookings.get(i);
			String str = bean.getSubject()+"|"+bean.getTrackId();
			if(releaseSubjects.contains(str)){
				bookingsList.add(bean);
				email = bean.getEmailId();
			}
		}

		boolean isCorporateStudent = getCorporateCenterUserMapping().containsKey(booking.getSapid());
		confirmedBookings = (ArrayList<ExamBookingTransactionBean>)dao.releaseBookings(sapid, bookingsList, booking, noCharges, true, isCorporateStudent);
		request.setAttribute("success","true");
		request.setAttribute("successMessage","Seats released Successfully");

		modelnView.addObject("booking", booking);
		modelnView.addObject("confirmedBookings", confirmedBookings);
		request.getSession().setAttribute("confirmedBookings", confirmedBookings);
		modelnView.addObject("rowCount", confirmedBookings.size());
		
		
		corporateCenterUserMapping = getCorporateCenterUserMapping();//Get details of Students mapped to Corporate Exam Centers
		request.getSession().setAttribute("corporateCenterUserMapping", corporateCenterUserMapping);
		
		
		//Map<String,String> corporateCenterUserMapping = (HashMap<String,String>)request.getSession().getAttribute("corporateCenterUserMapping");
		if(corporateCenterUserMapping.containsKey(sapid)){
			request.getSession().setAttribute("examCenterIdNameMap", dao.getCorporateExamCenterIdNameMap());
		}else{
			request.getSession().setAttribute("examCenterIdNameMap", getExamCenterIdNameMap());
		}

		modelnView.addObject("yearList", ACAD_YEAR_LIST);

		MailSender mailSender = (MailSender)act.getBean("mailer");
		mailSender.sendSeatsRealseEmail(sapid, releaseSubjects, email, noCharges);



		return modelnView;
	}*/

	private static List<String> logSubjectsFromList(ArrayList<ExamBookingTransactionBean> confirmedBookings) {
		return confirmedBookings.stream().map(ExamBookingTransactionBean::getSubject).collect(Collectors.toList());
	}

	@RequestMapping(value = "/releaseBookingsStudent",  method = {RequestMethod.POST})
	public ModelAndView releaseBookingsStudent(HttpServletRequest request, HttpServletResponse response, @ModelAttribute ExamBookingTransactionBean booking){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView modelnView = new ModelAndView("releaseBookingStudent");
		ExamCenterDAO dao = (ExamCenterDAO)act.getBean("examCenterDAO");
		ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");

		String sapid = (String)request.getSession().getAttribute("userId");

		modelnView.addObject("booking", booking);

		Map<String,String> corporateCenterUserMapping = (HashMap<String,String>)request.getSession().getAttribute("corporateCenterUserMapping");
		if(corporateCenterUserMapping.containsKey(sapid)){
			request.getSession().setAttribute("examCenterIdNameMap", dao.getCorporateExamCenterIdNameMap());
		}else{
			request.getSession().setAttribute("examCenterIdNameMap", getExamCenterIdNameMap());
		}
		
		boolean  isExamRegistraionLive = false;
		if(demoSapids.contains(sapid)) {
			isExamRegistraionLive = true;
			modelnView.addObject("isExamRegistraionLive", isExamRegistraionLive);
		}else {
		
			isExamRegistraionLive = isExamRegistraionLive(request, sapid);
			
			if(!isExamRegistraionLive) {
				boolean isStudentReleasingTwentyFourHrsPrior= eDao.isStudentAllowedToChangeSlotPriorNumberOfDays(sapid, NUMBER_ONE);
				
				if(isStudentReleasingTwentyFourHrsPrior) {
					request.setAttribute("isBookingSeatAfterExamIsNotLive","true");
					isExamRegistraionLive = true;
				}
			}
			
			modelnView.addObject("isExamRegistraionLive", isExamRegistraionLive);
		}

		try {


			StudentExamBean student = (StudentExamBean)request.getSession().getAttribute("studentExam");

			ArrayList<ExamBookingTransactionBean> confirmedBookings = eDao.getConfirmedBookings(sapid);

			ArrayList<String> releasedSubjects = booking.getReleaseSubjects();
			
			request.getSession().setAttribute("releasedSubjects", releasedSubjects);


			request.setAttribute("success","true");
			request.setAttribute("successMessage","Seats marked for Release. Please proceed to selection of New Exam Centers. Seats will be released after you complete Payment");

			ArrayList<ExamBookingTransactionBean> bookingsList = new ArrayList<>();
			for (int i = 0; i < confirmedBookings.size(); i++) {
				ExamBookingTransactionBean bean = confirmedBookings.get(i);
				//String str =bean.getSubject()+"|"+bean.getTrackId();
				if(releasedSubjects.contains(bean.getSubject())){
				//if(releasedSubjects.contains(str)){
					bookingsList.add(bean);
				}
			}

			request.getSession().setAttribute("bookingsToRelease", bookingsList);
			/*confirmedBookings = (ArrayList<ExamBookingTransactionBean>)dao.releaseBookings(sapid, bookingsList, booking, "false");
			MailSender mailSender = (MailSender)act.getBean("mailer");
			mailSender.sendSeatsRealseEmail(sapid, releasedSubjects, student.getEmailId(), "false");*/

		}catch (Exception e) {
			
			setError(request, "Error in Releasing Seats : "+ e.getMessage());
			return modelnView;
		}

		request.setAttribute("selectionForReleasedSeats", "true");
		return selectExamCenterForRelesedSubjects(request, response);

	}
	
	
//	to be deleted, api shifted to rest controller
//	@RequestMapping(value = "/m/releaseBookingsStudent",  method = {RequestMethod.POST})
//	public ResponseEntity<mreleaseBookingsStudent> mreleaseBookingsStudent(
//			@RequestBody ExamSelectSubjectAPIRequest input,
//			HttpServletRequest request, HttpServletResponse response){
//		mreleaseBookingsStudent api_response = new mreleaseBookingsStudent();
//		HashMap<String, String> centersList = new HashMap<String,String>();
//		ArrayList<ExamBookingTransactionBean> bookingsList = new ArrayList<>();
//		String message;
//		int status = 200; 
//		
//		ExamCenterDAO dao = (ExamCenterDAO)act.getBean("examCenterDAO");
//		ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
//
//		String sapid = input.getSapid();
//		ArrayList<String> releasedSubjects = new ArrayList<String>(input.getSubjects());
//		//modelnView.addObject("booking", booking);
// 
//		Map<String, String> corporateCenterUserMapping = dao.getCorporateCenterUserMapping();
//		
//		if(corporateCenterUserMapping.containsKey(sapid)){
//			centersList = dao.getCorporateExamCenterIdNameMap();
//		}else{
//			centersList = getExamCenterIdNameMap();
//		}
//
//		boolean isExamRegistraionLive = misExamRegistraionLive(sapid);
//		//modelnView.addObject("isExamRegistraionLive", isExamRegistraionLive);
//
//		try {
//
//
//			//StudentBean student = (StudentBean)request.getSession().getAttribute("student");
//
//			ArrayList<ExamBookingTransactionBean> confirmedBookings = eDao.getConfirmedBookings(sapid);
//
//
//
//			
//			message = "Seats marked for Release. Please proceed to selection of New Exam Centers. Seats will be released after you complete Payment";
//
//			
//			for (int i = 0; i < confirmedBookings.size(); i++) {
//				ExamBookingTransactionBean bean = confirmedBookings.get(i);
//				if(releasedSubjects.contains(bean.getSubject())){
//					bookingsList.add(bean);
//				}
//			}
//			/*confirmedBookings = (ArrayList<ExamBookingTransactionBean>)dao.releaseBookings(sapid, bookingsList, booking, "false");
//			MailSender mailSender = (MailSender)act.getBean("mailer");
//			mailSender.sendSeatsRealseEmail(sapid, releasedSubjects, student.getEmailId(), "false");*/
//
//		}catch (Exception e) {
//			
//			status = 500;
//			message = "Error in Releasing Seats : "+ e.getMessage();
//		}
//		
//		api_response.setCenters(centersList);
//		api_response.setExamregistration(isExamRegistraionLive);
//		api_response.setMessage(message);
//		api_response.setStatus(status);
//		api_response.setData(bookingsList);
//		api_response.setTotalExamfees("" + totalFeesForRebooking + "");
//		
//		return new ResponseEntity<mreleaseBookingsStudent>(api_response,HttpStatus.OK);
//		
//	}
	
	

	@RequestMapping(value = "/admin/uploadSeatRealeseFileForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String uploadSeatRealesFileForm(HttpServletRequest request, HttpServletResponse response, Model m) {

		FileBean fileBean = new FileBean();
		m.addAttribute("fileBean",fileBean);
		m.addAttribute("yearList", yearList);

		return "uploadSeatRelease";
	}

	@RequestMapping(value = "/admin/uploadSeatRelease", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView uploadSeatRelease(FileBean fileBean, BindingResult result,HttpServletRequest request, Model m){
		ModelAndView modelnView = new ModelAndView("uploadSeatRelease");
		try{
			StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			String userId = (String)request.getSession().getAttribute("userId");
			ExcelHelper excelHelper = new ExcelHelper();
			ArrayList<List> resultList = excelHelper.readSeatReleaseExcel(fileBean, getSubjectList());
			//List<StudentMarksBean> marksBeanList = excelHelper.readMarksExcel(fileBean, programList, subjectList);

			List<ExamBookingTransactionBean> studentsList = (ArrayList<ExamBookingTransactionBean>)resultList.get(0);
			List<ExamBookingTransactionBean> errorBeanList = (ArrayList<ExamBookingTransactionBean>)resultList.get(1);

			if(errorBeanList.size() > 0){
				request.setAttribute("errorBeanList", errorBeanList);
				return modelnView;
			}
			HashMap<String, ArrayList<String>> studentSubjectsMap = new HashMap<>();
			for (ExamBookingTransactionBean examBookingBean : studentsList) {
				String sapid = examBookingBean.getSapid();
				String subject = examBookingBean.getSubject();

				if(studentSubjectsMap.containsKey(sapid)){
					ArrayList<String> subjectsList = studentSubjectsMap.get(sapid);
					subjectsList.add(subject);
					studentSubjectsMap.put(sapid, subjectsList);
				}else{
					ArrayList<String> subjectsList = new ArrayList<>();
					subjectsList.add(subject);
					studentSubjectsMap.put(sapid, subjectsList);
				}
			}

			ExamCenterDAO ecDao = (ExamCenterDAO)act.getBean("examCenterDAO");
			
			//Added by shivam.pandey.EXT - Declared list to catch all toRelease subjects with their details
			List<ExamBookingTransactionBean> toReleaseSubjectList = new ArrayList<ExamBookingTransactionBean>();

			int successCount = 0;
			for (String sapId : studentSubjectsMap.keySet()) {
				ExamBookingTransactionBean booking = new ExamBookingTransactionBean();
				booking.setSapid(sapId);
				booking.setYear(fileBean.getYear());
				booking.setMonth(fileBean.getMonth());
				
				if(booking.getLastModifiedBy() == null &&  userId != null) 
					booking.setLastModifiedBy(userId);

				ArrayList<ExamBookingTransactionBean> confirmedBookings = ecDao.getConfirmedNotReleaseBooking(booking);


				ArrayList<String> releaseSubjects = studentSubjectsMap.get(sapId);

				List<ExamBookingTransactionBean> bookingsList = new ArrayList<>();
				for (int i = 0; i < confirmedBookings.size(); i++) {
					ExamBookingTransactionBean bean = confirmedBookings.get(i);
					if(releaseSubjects.contains(bean.getSubject())){
						bookingsList.add(bean);
					}
				}
				
				//Added by shivam.pandey.EXT - catching all toRelease subjects with their details
				toReleaseSubjectList.addAll(bookingsList);
				
				ecDao.releaseBookings(sapId, bookingsList, booking, "Passed", false, getCorporateCenterUserMapping().containsKey(sapId));
				
				examRegisterlogger.info("Real Time Registartion called from uploadSeatRelease method"+ecDao.getIsExtendedExamRegistrationLiveForRealTime());
				if(ecDao.getIsExtendedExamRegistrationLiveForRealTime()) {
					examRegisterlogger.info("Real Time Registartion called");
				examRegistrationRealTimeStrategy.registrationOnMettlAndReleaseBooking(null,bookingsList);
				}
				successCount++;
			}
			setSuccess(request, "Seats released for " + successCount + " Students.");

			//Added by shivam.pandey.EXT - START
			exambookingAuditService.asyncInsertExamBookingAudit(toReleaseSubjectList, "Passed", userId);
			//Added by shivam.pandey.EXT - END
			
		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in releasing seats.");
		}
		fileBean = new FileBean();
		m.addAttribute("fileBean",fileBean);
		m.addAttribute("yearList", yearList);
		return modelnView;
	}

	@RequestMapping(value = "/searchExamBookingConflictForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView searchExamBookingConflictForm( HttpServletRequest request,HttpServletResponse response, Model m){

		ModelAndView modelnView = new ModelAndView ("examBookingConflict");
		ExamConflictTransactionBean transaction = new ExamConflictTransactionBean();
		m.addAttribute("transaction",transaction);

		m.addAttribute("yearList", yearList);

		return modelnView ;
	}

	@RequestMapping(value = "/searchConflictTransaction", method = {RequestMethod.POST})
	public ModelAndView searchConflictTransaction( HttpServletRequest request,HttpServletResponse response, @ModelAttribute ExamConflictTransactionBean transaction){

		ModelAndView modelnView = new ModelAndView ("examBookingConflict");
		String year = request.getParameter("year");
		String month = request.getParameter("month");
		ExamBookingDAO edao = (ExamBookingDAO)act.getBean("examBookingDAO");

		ArrayList<ExamConflictTransactionBean> transactionList = edao.getAllConflictTransactions(year, month);
		int rowCount = 0;
		if (!(transactionList.size() == 0 || transactionList == null) ){
			rowCount = transactionList.size();
		}else{
		}

		modelnView.addObject("rowCount", rowCount);
		modelnView.addObject("transaction",transaction);
		modelnView.addObject("transactionList",transactionList);
		modelnView.addObject("yearList", yearList);

		return modelnView ;
	}

	//Check Student validity for disable Exam Registration link after validity expired  
	private boolean isStudentValid(StudentExamBean student, String userId) throws ParseException {
		String date = "";
		String validityEndMonthStr = student.getValidityEndMonth();
		int validityEndYear = Integer.parseInt(student.getValidityEndYear());
		
		String enrollmentMonth = student.getEnrollmentMonth();
		int enrollmentYear = Integer.parseInt(student.getEnrollmentYear());

		Date lastAllowedAcccessDate = null;
		Date enrollmentDate = null;
		String register = student.getRegDate();
		int validityEndMonth = 0;
		if("Jun".equals(validityEndMonthStr)){
			validityEndMonth = 6;
			date = validityEndYear + "/" + validityEndMonth + "/" + "30";
			lastAllowedAcccessDate = formatter.parse(date);
		}else if("Dec".equals(validityEndMonthStr)){
			validityEndMonth = 12;
			date = validityEndYear + "/" + validityEndMonth + "/" + "31";
			lastAllowedAcccessDate = formatter.parse(date);
		}else if("Feb".equals(validityEndMonthStr)){
			validityEndMonth = 2;
			date = validityEndYear + "/" + validityEndMonth + "/" + "28";
			lastAllowedAcccessDate = formatter.parse(date);
		}else if("Mar".equals(validityEndMonthStr)){
			validityEndMonth = 3;
			date = validityEndYear + "/" + validityEndMonth + "/" + "31";
			lastAllowedAcccessDate = formatter.parse(date);
		}else if("Apr".equals(validityEndMonthStr)){
			validityEndMonth = 4;
			date = validityEndYear + "/" + validityEndMonth + "/" + "30";
			lastAllowedAcccessDate = formatter.parse(date);
		}else if("Aug".equals(validityEndMonthStr)){
			validityEndMonth = 8;
			date = validityEndYear + "/" + validityEndMonth + "/" + "31";
			lastAllowedAcccessDate = formatter.parse(date);
		}else if("Sep".equals(validityEndMonthStr)){
			validityEndMonth = 9;
			date = validityEndYear + "/" + validityEndMonth + "/" + "30";
			lastAllowedAcccessDate = formatter.parse(date);
		}else if("Oct".equals(validityEndMonthStr)){
			validityEndMonth = 10;
			date = validityEndYear + "/" + validityEndMonth + "/" + "31";
			lastAllowedAcccessDate = formatter.parse(date);
		}else if("Jan".equals(validityEndMonthStr)){
			validityEndMonth = 1;
			date = validityEndYear + "/" + validityEndMonth + "/" + "31";
			lastAllowedAcccessDate = formatter.parse(date);
		}else if("May".equals(validityEndMonthStr)){
			validityEndMonth = 5;
			date = validityEndYear + "/" + validityEndMonth + "/" + "31";
			lastAllowedAcccessDate = formatter.parse(date);
		}else if("Jul".equals(validityEndMonthStr)){
			validityEndMonth = 7;
			date = validityEndYear + "/" + validityEndMonth + "/" + "31";
			lastAllowedAcccessDate = formatter.parse(date);
		}

		Calendar now = Calendar.getInstance();
		int currentYear = now.get(Calendar.YEAR);
		int currentMonth = (now.get(Calendar.MONTH) + 1);
		
		if(currentYear < validityEndYear  ){
			return true;
		}else if(currentYear == validityEndYear && currentMonth <= validityEndMonth){
			return true;
		}
			return false;

	}
	
	@RequestMapping(value = "/examBookingRequest", method = {RequestMethod.POST})
	public ModelAndView examBookingRequest(HttpServletRequest request,HttpServletResponse response, @ModelAttribute RequestFormBean requestFormBean) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ExamBookingDAO edao = (ExamBookingDAO)act.getBean("examBookingDAO");
		StudentExamBean student = (StudentExamBean)request.getSession().getAttribute("studentExam");
		requestFormBean.setSapid(student.getSapid());
		if(edao.checkAlreadyRefundRequestSubmitted(requestFormBean)) {
			if(!"true".equalsIgnoreCase((String) request.getAttribute("success"))) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "We appreciate and thank you for your selection, exam fees will be processed for refund once we resume work post lock down situation ends. Refund process thereafter will take 15-20 days");
			}
			return examBookingRequestForm(request,response); 
		}
		else if(edao.checkAlreadyCarryForwardRequestSubmitted(requestFormBean)) {
			if(!"true".equalsIgnoreCase((String) request.getAttribute("success"))) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "We appreciate and thank you for your selection, this amount will be adjusted against the bookings you make for June 2020 exam cycle. Any additional bookings made during June exam cycle will incur the applicable charges");
			}
			return examBookingRequestForm(request,response);
		}
		List<ExamBookingTransactionBean> examBookingBeanList = edao.getBookedSubjectList(requestFormBean);
		String subject = "";
		if("refund".equalsIgnoreCase(requestFormBean.getRequest_action())) {
			for (ExamBookingTransactionBean examBookingTransactionBean : examBookingBeanList) {
				if(!edao.insertIntoRefund(examBookingTransactionBean)) {
					subject = subject + examBookingTransactionBean.getSubject() + ",";
				}
			}
		}else {
			for (ExamBookingTransactionBean examBookingTransactionBean : examBookingBeanList) {
				examBookingTransactionBean.setMonth("Jun");
				if(!edao.insertIntoExcept(examBookingTransactionBean)) {
					subject = subject + examBookingTransactionBean.getSubject() + ",";
				}
			}
		}
		if("".equalsIgnoreCase(subject)) {
			request.setAttribute("success", "true");
			if("refund".equalsIgnoreCase(requestFormBean.getRequest_action())) {
				request.setAttribute("successMessage", "Successfully Initiated Refund for ExamBooking Apr-2020 ,Amount: " + calculateTotalBookingAmount(examBookingBeanList));
			}else {
				request.setAttribute("successMessage", "Successfully, Carry forward ExamBooking Apr-2020 amount to Jun-2020.");
			}
			
		}else {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Failed to submit request for some subject: " + subject);
		}
		MailSender mailSender = (MailSender)act.getBean("mailer");
		mailSender.sendExamBookingRequestEmail(student, requestFormBean.getRequest_action());
		return examBookingRequestForm(request,response);
	}
	
	@RequestMapping(value = "/examBookingRequestForm", method = {RequestMethod.GET})
	public ModelAndView examBookingRequestForm(HttpServletRequest request,HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView mv = new ModelAndView("examBookingRequestForm");
		//mv.addObject("errorBookingBeanList", examBookingTransactionBeanListRequest);
		ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
		RequestFormBean requestFormBean = new RequestFormBean();
		StudentExamBean student = (StudentExamBean)request.getSession().getAttribute("studentExam");
		requestFormBean.setSapid(student.getSapid());
		mv.addObject("requestFormBean", requestFormBean);
		if(!eDao.isCarryForwardRefundRequestFlagLive(eDao.getLiveExamYear(), eDao.getLiveExamMonth())) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Carry forward/Refund request window is not live currently");
			mv.addObject("examBookingBeansList", new ArrayList<ExamBookingTransactionBean>());
			return mv;
		}
		List<ExamBookingTransactionBean> examBookingBeansList = eDao.getBookedSubjectList(requestFormBean);
		
		if(eDao.checkAlreadyRefundRequestSubmitted(requestFormBean)) {
			if(!"true".equalsIgnoreCase((String) request.getAttribute("success"))) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "We appreciate and thank you for your selection, exam fees will be processed for refund once we resume work post lock down situation ends. Refund process thereafter will take 15-20 days");
			}
			mv.addObject("examBookingBeansList", new ArrayList<ExamBookingTransactionBean>());
		}
		else if(eDao.checkAlreadyCarryForwardRequestSubmitted(requestFormBean)) {
			if(!"true".equalsIgnoreCase((String) request.getAttribute("success"))) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "We appreciate and thank you for your selection, this amount will be adjusted against the bookings you make for June 2020 exam cycle. Any additional bookings made during June exam cycle will incur the applicable charges");
			}
			mv.addObject("examBookingBeansList", new ArrayList<ExamBookingTransactionBean>());
		}else {
			requestFormBean.setTotal_amount(calculateTotalBookingAmount(examBookingBeansList));
			mv.addObject("examBookingBeansList", examBookingBeansList);
		}
		return mv;
		
	}
	
	private int calculateTotalBookingAmount(List<ExamBookingTransactionBean> examBookingBeansList) {
		int totalAmount = 0;
		ArrayList<String> trackId = new ArrayList<String>();
		for (ExamBookingTransactionBean examBookingTransactionBean : examBookingBeansList) {
			if(!trackId.contains(examBookingTransactionBean.getTrackId())) {
				totalAmount = totalAmount + Integer.parseInt(examBookingTransactionBean.getAmount());
				trackId.add(examBookingTransactionBean.getTrackId());
			}
		}
		return totalAmount;
	}
	
	@RequestMapping(value = "/examBookingRefundRequestReport", method = { RequestMethod.GET })
	public ModelAndView examBookingRefundRequestReport(HttpServletRequest request) {
		ModelAndView modelAndView = new ModelAndView("examBookingRefundRequestReport");
		ExamBookingDAO eDao = (ExamBookingDAO) act.getBean("examBookingDAO");
		List<RefundRequestBean> refundRequestBeans = eDao.getExamBookingRefundRequests();
		List<ExamBookingTransactionBean> examBookingApr2020RefundNotApplyStudentAndCarryForward = eDao.getExamBookingPPRefundAmont(); 
		List<ExamFeeExemptSubjectBean> examFeeExemptSubjectBeans = eDao.getCarryForwardExamBookingRefundRequest();
		
		List<ExamBookingRefundRequestReportBean> list = new ArrayList<>();
		List<String> sapids = new ArrayList<String>();
		for(RefundRequestBean refundRequestBean:refundRequestBeans) {
			String option = "Will be Refunded";
			//sapid, trackId, sum(amount) as amount, description, created_at, updated_at
			ExamBookingRefundRequestReportBean examBookingRefundRequestReportBean = new ExamBookingRefundRequestReportBean();
			examBookingRefundRequestReportBean.setSapid(refundRequestBean.getSapid());
			examBookingRefundRequestReportBean.setTrackId(refundRequestBean.getTrackId());
			examBookingRefundRequestReportBean.setAmount(refundRequestBean.getAmount());
			examBookingRefundRequestReportBean.setDescription(refundRequestBean.getDescription());
			examBookingRefundRequestReportBean.setName(refundRequestBean.getName());
			examBookingRefundRequestReportBean.setEmailId(refundRequestBean.getEmailId());
			examBookingRefundRequestReportBean.setMobile(refundRequestBean.getMobile());
			examBookingRefundRequestReportBean.setOptions(option);
			examBookingRefundRequestReportBean.setDescription(refundRequestBean.getDescription());
			examBookingRefundRequestReportBean.setSubmissionDate(refundRequestBean.getUpdated_at());
			List<ExamBookingTransactionBean> examBookingTransactionBeanList =  eDao.getSlotChangeBooking(refundRequestBean.getSapid(),"2020","Apr");
			if(!"0".equalsIgnoreCase(examBookingRefundRequestReportBean.getAmount())) {
				list.add(examBookingRefundRequestReportBean);
			}
			for(ExamBookingTransactionBean examBookingTransactionBean : examBookingTransactionBeanList) {
				ExamBookingRefundRequestReportBean examBookingRefundRequestReportBean_tmp = new ExamBookingRefundRequestReportBean();
				examBookingRefundRequestReportBean_tmp.setSapid(refundRequestBean.getSapid());
				examBookingRefundRequestReportBean_tmp.setName(refundRequestBean.getName());
				examBookingRefundRequestReportBean_tmp.setEmailId(refundRequestBean.getEmailId());
				examBookingRefundRequestReportBean_tmp.setMobile(refundRequestBean.getMobile());
				examBookingRefundRequestReportBean_tmp.setOptions(option);
				examBookingRefundRequestReportBean_tmp.setTrackId(examBookingTransactionBean.getTrackId());
				examBookingRefundRequestReportBean_tmp.setAmount(examBookingTransactionBean.getAmount());
				examBookingRefundRequestReportBean_tmp.setDescription(examBookingTransactionBean.getDescription() + " Seat Release");
				examBookingRefundRequestReportBean_tmp.setSubmissionDate(examBookingTransactionBean.getCreatedDate());
				if(!"0".equalsIgnoreCase(examBookingRefundRequestReportBean_tmp.getAmount())) {
					list.add(examBookingRefundRequestReportBean_tmp);
				}
			} 
		}
		
		for(ExamBookingTransactionBean examBookingTransactionBean : examBookingApr2020RefundNotApplyStudentAndCarryForward) {
			String option = "Will be Refunded";
			ExamBookingRefundRequestReportBean examBookingRefundRequestReportBean_tmp = new ExamBookingRefundRequestReportBean();
			examBookingRefundRequestReportBean_tmp.setSapid(examBookingTransactionBean.getSapid());
			examBookingRefundRequestReportBean_tmp.setName(examBookingTransactionBean.getFirstName() + " " + examBookingTransactionBean.getLastName());
			examBookingRefundRequestReportBean_tmp.setEmailId(examBookingTransactionBean.getEmailId());
			examBookingRefundRequestReportBean_tmp.setMobile(examBookingTransactionBean.getMobile());
			examBookingRefundRequestReportBean_tmp.setOptions(option);
			examBookingRefundRequestReportBean_tmp.setTrackId(examBookingTransactionBean.getTrackId());
			examBookingRefundRequestReportBean_tmp.setAmount(examBookingTransactionBean.getAmount());
			examBookingRefundRequestReportBean_tmp.setDescription(examBookingTransactionBean.getDescription() + " Seat Release");
			examBookingRefundRequestReportBean_tmp.setSubmissionDate(examBookingTransactionBean.getCreatedDate());
			if(!"0".equalsIgnoreCase(examBookingRefundRequestReportBean_tmp.getAmount())) {
				list.add(examBookingRefundRequestReportBean_tmp);
			}
		}
		
		
		
		List<String> trackIds = new ArrayList<String>();
		ArrayList<ExamBookingRefundRequestReportBean> examBookingRefundRequestReportBeanList = new ArrayList<ExamBookingRefundRequestReportBean>();
		for(ExamBookingRefundRequestReportBean examBookingRefundRequestReportBean : list) {
			sapids.add(examBookingRefundRequestReportBean.getSapid());
			if(!trackIds.contains(examBookingRefundRequestReportBean.getTrackId())) {
				examBookingRefundRequestReportBeanList.add(examBookingRefundRequestReportBean);
				trackIds.add(examBookingRefundRequestReportBean.getTrackId());
			}
		}
		
		
		for(ExamFeeExemptSubjectBean examFeeExemptSubjectBean:examFeeExemptSubjectBeans) {
			String option = "Carry Forward";
			sapids.add(examFeeExemptSubjectBean.getSapid());
			ExamBookingRefundRequestReportBean examBookingRefundRequestReportBean = new ExamBookingRefundRequestReportBean();
			examBookingRefundRequestReportBean.setSapid(examFeeExemptSubjectBean.getSapid());
			//examBookingRefundRequestReportBean.setAmount(examFeeExemptSubjectBean.ge);
			examBookingRefundRequestReportBean.setName(examFeeExemptSubjectBean.getName());
			examBookingRefundRequestReportBean.setEmailId(examFeeExemptSubjectBean.getEmailId());
			examBookingRefundRequestReportBean.setMobile(examFeeExemptSubjectBean.getMobile());
			examBookingRefundRequestReportBean.setOptions(option);
			examBookingRefundRequestReportBean.setSubmissionDate(examFeeExemptSubjectBean.getLastModifiedDate());
			examBookingRefundRequestReportBean.setSubject(examFeeExemptSubjectBean.getSubject());
			examBookingRefundRequestReportBeanList.add(examBookingRefundRequestReportBean);
		}
		
		
//		for(ExamBookingTransactionBean examBookingTransactionBean : examBookingApr2020) {
//			if(!sapids.contains(examBookingTransactionBean.getSapid())) {
//				String option = "Carry Forward";
//				ExamBookingRefundRequestReportBean examBookingRefundRequestReportBean = new ExamBookingRefundRequestReportBean();
//				examBookingRefundRequestReportBean.setSapid(examBookingTransactionBean.getSapid());
//				//examBookingRefundRequestReportBean.setAmount(examFeeExemptSubjectBean.ge);
//				examBookingRefundRequestReportBean.setName(examBookingTransactionBean);
//				examBookingRefundRequestReportBean.setEmailId(examFeeExemptSubjectBean.getEmailId());
//				examBookingRefundRequestReportBean.setMobile(examFeeExemptSubjectBean.getMobile());
//				examBookingRefundRequestReportBean.setOptions(option);
//				examBookingRefundRequestReportBean.setSubmissionDate(examFeeExemptSubjectBean.getLastModifiedDate());
//				examBookingRefundRequestReportBean.setSubject(examFeeExemptSubjectBean.getSubject());
//			}
//		}
		

		request.getSession().setAttribute("examBookingRefundRequestReportList", examBookingRefundRequestReportBeanList);
		modelAndView.addObject("reportData", examBookingRefundRequestReportBeanList);
		return modelAndView;
	}
	@RequestMapping(value = "/downloadExamBookingRefundRequestReport", method = { RequestMethod.GET,
			RequestMethod.POST })
	public ModelAndView downloadAssignmentStatus(HttpServletRequest request, HttpServletResponse response) {
		if (!checkSession(request, response)) {
			redirectToPortalApp(response);
			return null;
		}

		List<ExamBookingRefundRequestReportBean> examBookingRefundRequestReportBeanList = (List<ExamBookingRefundRequestReportBean>) request
				.getSession().getAttribute("examBookingRefundRequestReportList");

		return new ModelAndView("examBookingRefundRequestReportExcelView", "reportList",
				examBookingRefundRequestReportBeanList);
	}
	
	@RequestMapping(value = "/sendOnlineExamTestLinkUrlDayAgo", method = RequestMethod.GET,produces = "application/json")
	public ResponseEntity<HashMap<String,String>> runStudentSyncJobApi() throws Exception{
		  
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		HashMap<String,String> response = new HashMap<>();
		
		if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){ response.put("Status", "Success");
		response.put("message", "Will not run on prod !"); return new
		ResponseEntity<>(response,headers, HttpStatus.OK); }
	 
		scheduler.sendOnlineExamTestLinkUrlDayAgo();				
		response.put("Status", "Success");
		response.put("message", "runStudentSyncJobApi Successfully !");
		return new ResponseEntity<>(response,headers, HttpStatus.OK);	
				
	}

	@Autowired
	ExamBookingScheduler examBookingScheduler;
	@RequestMapping(value = "/runDoAutoBookingForConflictTransactions", method = RequestMethod.GET,produces = "application/json")
	public ResponseEntity<String> runDoAutoBookingForConflictTransactions() throws Exception{
		examBookingScheduler.doAutoBookingForConflictTransactions();
		return new ResponseEntity<>("",HttpStatus.OK);	
				
	}	
	
	@RequestMapping(value = "/pgReExamEligibleStudentReport",method=RequestMethod.GET)
	public ModelAndView pgReExamEligibleStudentReport(HttpServletRequest request,HttpServletResponse response) {
		ExamBookingDAO eDao = (ExamBookingDAO) act.getBean("examBookingDAO");
		List<PGReexamEligibleStudentsBean> pgReExamEligibleAllStudentsList = eDao.getFailedStudentList();
		List<PGReexamEligibleStudentsBean> pgReExamEligibilePassFailedToProcced = eDao.getPassFailedNotProcessed();
		pgReExamEligibleAllStudentsList.addAll(pgReExamEligibilePassFailedToProcced);
		List<PGReexamEligibleStudentsBean> pgReExamEligibleStudentsList = new ArrayList<PGReexamEligibleStudentsBean>();
		for (PGReexamEligibleStudentsBean pgReexamEligibleStudentsBean : pgReExamEligibleAllStudentsList) {
			StudentExamBean studentBean = new StudentExamBean();
			studentBean.setSapid(pgReexamEligibleStudentsBean.getSapid());
			studentBean.setProgram(pgReexamEligibleStudentsBean.getProgram());
			studentBean.setValidityEndMonth(pgReexamEligibleStudentsBean.getStudentValidityEndMonth());
			studentBean.setValidityEndYear(pgReexamEligibleStudentsBean.getStudentValidityEndYear());
			studentBean.setProgram(pgReexamEligibleStudentsBean.getProgram());
			studentBean.setEnrollmentMonth(pgReexamEligibleStudentsBean.getEnrollmentMonth());
			studentBean.setEnrollmentYear(pgReexamEligibleStudentsBean.getEnrollmentYear());
			try {
				if(isStudentValid(studentBean, studentBean.getSapid())) {
					pgReExamEligibleStudentsList.add(pgReexamEligibleStudentsBean);
				}
			}
			catch (Exception e) {
				// TODO: handle exception
			}
		}
		return new ModelAndView("pgReexamEligibleStudentsExcelView","pgReexamEligibleStudentsList",pgReExamEligibleStudentsList);
	}

	
	public static String toString(Object arg) {
		return String.valueOf(arg);
	}
	
	protected static Integer toInteger(String arg) {
		return Integer.valueOf(arg);
	}
	
	@PostMapping(value = "/admin/m/getExamStatusFromContent")
	public ResponseEntity<ResponseBean> getExamStatus(@RequestBody ExamBookingTransactionBean examBookingTransactionBean) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json"); 
		ResponseBean responseBean = new ResponseBean();
		final String contentUrl = "https://ngasce-content.nmims.edu/exam/m/getExamStatus";
        try {
        	RestTemplate restTemplate = new RestTemplate();
        	ResponseEntity<ResponseBean> postForEntity = restTemplate.postForEntity(contentUrl,
        			examBookingTransactionBean, ResponseBean.class);
        	responseBean = postForEntity.getBody();
	        return new ResponseEntity<ResponseBean>(responseBean, headers,  HttpStatus.OK);
        }
        catch (Exception e) {
        	responseBean.setCode(422);
        	responseBean.setMessage(e.getMessage());
	        return new ResponseEntity<ResponseBean>(responseBean, headers,  HttpStatus.OK);
		}
	}
	
}
