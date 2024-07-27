package com.nmims.controllers;

import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.nmims.beans.BookedResponse;
import com.nmims.beans.ExamBookingExamBean;
import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.beans.ExamCenterBean;
import com.nmims.beans.ExamRegistrationBeanAPIRequest;
import com.nmims.beans.ExamRegistrationBeanAPIResponseExam;
import com.nmims.beans.ExamSelectSubjectAPIRequest;
import com.nmims.beans.ExamSelectSubjectBeanAPIResponse;
import com.nmims.beans.PassFailExamBean;
import com.nmims.beans.ProgramSubjectMappingExamBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.beans.TimetableBean;
import com.nmims.beans.mreleaseBookingsStudent;
import com.nmims.daos.ExamBookingDAO;
import com.nmims.daos.ExamCenterDAO;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.helpers.ExamBookingHelper;
import com.nmims.helpers.MailSender;
import com.nmims.helpers.PaymentHelper;
import com.nmims.services.ExamBookingEligibilityService;
import com.nmims.services.ExamBookingStudentService;
import com.nmims.services.StudentService;

@RestController
@RequestMapping("m")
public class ExamBookingRestController extends BaseController {
	
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
	ExamBookingHelper examBookingHelper;
	
	private static final Logger logger = LoggerFactory.getLogger("examBookingPayments");
	
	private ArrayList<String> programList = null;
	private ArrayList<String> subjectList = null; 

	private int examFeesPerSubjectFirstAttempt = 600;
	private int examFeesPerSubjectResitAttempt = 600;

	private final  int examFeesPerSubject = 600;
	//int totalExamFees = 0;

	private int totalFeesForRebooking =500;
	private final String ONLINE_PAYMENT_INITIATED = "Online Payment Initiated"; 
	private final String ONLINE_PAYMENT_MANUALLY_APPROVED = "Online Payment Manually Approved"; 
	private final String ONLINE_PAYMENT_SUCCESS = "Online Payment Successfull";
	private final String ONLINE_PAYMENT_FAILED = "Transaction Failed"; 

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
	
	public HashMap<String, ArrayList<String>> getStudentFreeSubjectsMap(){
		if(this.studentFreeSubjectsMap == null || this.studentFreeSubjectsMap.size() == 0 || refreshCache){
			ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
			this.studentFreeSubjectsMap = eDao.getStudentFreeSubjectsMap();
			refreshCache = false;
		}
		return this.studentFreeSubjectsMap;
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
	
	
	private Map<String, TimetableBean> getSubjectTimetableMap(List<TimetableBean> timeTableList) {
		HashMap<String, TimetableBean> subjectTimetableMap = new HashMap<>();
		for (int i = 0; i < timeTableList.size(); i++) {
			TimetableBean bean = timeTableList.get(i);
			String subject = bean.getSubject();
			subjectTimetableMap.put(subject + bean.getStartTime() + bean.getDate(), bean); //Changed temporarily for dec2018 exam Added date in key
		}

		return subjectTimetableMap;
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
	
	private boolean misExamRegistraionLive(String sapid) {
		return checkIfExamRegistrationLive(sapid, "");
	}
	
	private boolean checkIfExamRegistrationLive(String sapid, String sapIdEncrypted) {
		// Enter empty encrypted sapid field for mobile users
		return examBookingStudentService.isExamRegistraionLive(sapid, sapIdEncrypted);
	}
	
	@PostMapping(path="/selectSubjectsForm", consumes="application/json", produces="application/json")
	public ResponseEntity<ExamRegistrationBeanAPIResponseExam> mselectSubjectsForm(@RequestBody StudentExamBean input) throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type","application/json");
		ExamRegistrationBeanAPIResponseExam response = new ExamRegistrationBeanAPIResponseExam();
		if(input.getSapid() == null) {
			response.setStatus("fail");
			response.setError("Invalid sapId found");
			return new ResponseEntity<ExamRegistrationBeanAPIResponseExam>(response, HttpStatus.OK);
		}
		
		ArrayList<String> eligibleSubjectsList = new ArrayList<>();
		ArrayList<String> releasedSubjects = new ArrayList<>();
		ArrayList<String> releasedNoChargeSubjects = new ArrayList<>();
		ArrayList<String> releasedPassedSubjects = new ArrayList<>();
		ArrayList<String> approvedOnlineTransactionSubjects = new ArrayList<>();
		ArrayList<ProgramSubjectMappingExamBean> applicableSubjectsList = new ArrayList<>();
		ArrayList<ProgramSubjectMappingExamBean> notApplicableSubjectsList = new ArrayList<>();
		ArrayList<String> applicableSubjects = new ArrayList<>();
		ArrayList<String> bookedSubjects = new ArrayList<>();
		boolean hasApprovedOnlineTransactions = false;
		boolean hasFreeSubjects = false;
		boolean hasReleasedSubjects = false;
		boolean hasReleasedNoChargeSubjects = false;
		ArrayList<String> freeApplicableSubjects = new ArrayList<>(); 

		int subjectsToPay = 0;
		ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
		StudentExamBean student ;
		student = dao.getSingleStudentsData(input.getSapid());
		ExamBookingExamBean examBooking = new ExamBookingExamBean();
		try {
			ExamBookingDAO eDao = (ExamBookingDAO) act.getBean("examBookingDAO");
			//Check for Exam Registration Status 
			boolean isExamRegistraionLive = misExamRegistraionLive(student.getSapid());
			response.setIsExamRegistraionLive(isExamRegistraionLive);
			//Get Current Cycle
			String mostRecentTimetablePeriod = eDao.getLiveExamMonth() + "-" + eDao.getLiveExamYear();
			response.setMostRecentTimetablePeriod(mostRecentTimetablePeriod);
			//Block offline students from Resit Exam Registration: START
			

			//For Resit consider only failed subjects :START
			if((mostRecentTimetablePeriod.contains("Apr") || mostRecentTimetablePeriod.contains("Sep")) && !"Diageo".equalsIgnoreCase(student.getConsumerType()) ){ //for diageo apr/sep is first attempt.
				/*Commented by Steffi to allow offline students for Apr/Sep registration.
				 * 
				 * 	if("Offline".equals(student.getExamMode()) || "ACBM".equals(student.getProgram())){*/
						
						if("ACBM".equals(student.getProgram())){
						response.setStatus("fail");
						response.setError("You are not authorized to register for Resit Examination");	
						response.setIsExamRegistraionLive(isExamRegistraionLive);
//						modelnView.addObject("examBooking", examBooking);
						return new ResponseEntity(response, headers, HttpStatus.OK);
					}
				}
			
			//Fetch current Sem subjects : START
			int lastSem = 1;
			List<StudentMarksBean> registrationList = eDao.getActiveRegistrations(student.getSapid());
			
			//Student who has registration data. Take the last semester and bring all subjects before that
			for (StudentMarksBean registrationBean : registrationList) {
				int sem = Integer.parseInt(registrationBean.getSem());
				if(sem >= lastSem){
					lastSem = sem;
				}
			}
			if(!registrationList.isEmpty()) {
				eligibleSubjectsList = eDao.getSubjectsForStudents(student, lastSem);
			}
			//add waived in subjects
			ArrayList<String> waivedInSubjects = studentService.mgetWaivedInSubjects(student);
			for(String subject : waivedInSubjects) {
				if(!eligibleSubjectsList.contains(subject)) {
					eligibleSubjectsList.add(subject);
				}
			}
			
			//Fetch current Sem subjects : END

			//Also take failed subjects for Exam Registration: START
			ArrayList<PassFailExamBean> passFailList = (ArrayList<PassFailExamBean>)eDao.getPassFailedSubjectsList(student.getSapid());
			ArrayList<PassFailExamBean> failList = new ArrayList<>();
			ArrayList<String> passList = new ArrayList<>();
			ArrayList<String> failedSubjectList = new ArrayList<>();
			for(PassFailExamBean item:passFailList) {
				if("Y".equals(item.getIsPass())) {
					passList.add(item.getSubject());
				}else if("N".equals(item.getIsPass())) {
					failList.add(item);
					failedSubjectList.add(item.getSubject());
				}
			}
			eligibleSubjectsList.addAll(failedSubjectList);//Failed Subjects are also eligible for Registration
			
			//Failed Assignment Subjects
			ArrayList<String> failedAssignmentSubjectList = new ArrayList<>();
			for(PassFailExamBean item:failList) {
				if(!"ANS".equalsIgnoreCase(item.getAssignmentscore())){
					failedAssignmentSubjectList.add(item.getSubject());
				}
			}
			//Also take failed subjects for Exam Registration: End
			
			//For Resit consider only failed subjects :START
			if((mostRecentTimetablePeriod.contains("Jun") || mostRecentTimetablePeriod.contains("Dec")) && "Diageo".equalsIgnoreCase(student.getConsumerType()) ){//for diageo apr/sep is resit attempt.
				eligibleSubjectsList = new ArrayList<String>();//Clear Current Sem Subjects from Eligible list
				eligibleSubjectsList.addAll(failedSubjectList);
			}
			if((mostRecentTimetablePeriod.contains("Apr") || mostRecentTimetablePeriod.contains("Sep")) && !"Diageo".equalsIgnoreCase(student.getConsumerType()) ){ //for diageo apr/sep is first attempt.
			
				eligibleSubjectsList = new ArrayList<String>();//Clear Current Sem Subjects from Eligible list
				eligibleSubjectsList.addAll(failedSubjectList);
				
				// commented because portal logic is commented 
				/*boolean hasClearedProject = true;
				if(lastSem == 4){
					hasClearedProject = eDao.checkIfProjectIsCleared(student.getSapid());
					Students who have registered in Jul 2017 for sem 4, then they are applicable for project only in December since 6 months gap is
					  required for registration of project hence taking difference between the exam order of most recent registration	
					  IF student has registered fr sem 4 in Jul drive, then hasClearedProject will be false for September cycle, and will appear in
					  eligibile list. To avoid that we check for minimum 6 months gap
					 
					StudentMarksBean recentRegistrationBean = eDao.getRegistrationForYearMonthSem(student.getSapid(),String.valueOf(lastSem));
					double diff = eDao.getExamOrderFromExamMonthAndYear(eDao.getLiveExamMonth(),eDao.getLiveExamYear()) - eDao.getExamOrderFromAcadMonthAndYear(recentRegistrationBean.getMonth(),recentRegistrationBean.getYear());
					if(!hasClearedProject && diff >=0.0){
						eligibleSubjectsList.add("Project");
					}
				}*/
			}
			//For Resit consider only failed subjects :END
			if(eligibleSubjectsList.contains("Project")){
				eligibleSubjectsList.remove("Project");
			}
			if(eligibleSubjectsList.contains("Module 4 - Project") && "PD - WM".equalsIgnoreCase(student.getProgram())){
				eligibleSubjectsList.remove("Module 4 - Project");
			}
			//Get free subjects for Exam, either for entire sem, or selectively allowed free for specific subject:START
			ArrayList<String> freeSubjects = getFreeSubjects(student);
			ArrayList<String> individualFreeSubjects = getStudentFreeSubjectsMap().get(student.getSapid());
			if(individualFreeSubjects != null && individualFreeSubjects.size() > 0){
				freeSubjects.addAll(individualFreeSubjects);
			}
			//Get free subjects for Exam, either for entire sem, or selectively allowed free for specific subject:END
			
			//Generate Map of Center ID and Name based on if student is mapped to Corporate/Regular Exam Centers: START
			corporateCenterUserMapping = getCorporateCenterUserMapping();//Get details of Students mapped to Corporate Exam Centers
			response.setCorporateCenterUserMapping(corporateCenterUserMapping);
			Map<String, String> examCenterIdNameMap = new HashMap<String,String>();
			if(corporateCenterUserMapping.containsKey(student.getSapid())){
				examCenterIdNameMap = eDao.getCorporateExamCenterIdNameMap();//Move this method to cache
				student.setCorporateExamCenterStudent(true);
				student.setMappedCorporateExamCenterId(corporateCenterUserMapping.get(student.getSapid()));
			}else {
				examCenterIdNameMap = getExamCenterIdNameMap();
				student.setCorporateExamCenterStudent(false);
			}
			response.setExamCenterIdNameMap(examCenterIdNameMap);
			//Generate Map of Center ID and Name based on if student is mapped to Corporate/Regular Exam Centers: END
			
			//Add subjects in different bucket, based on their current Exam Registration Status: START
			HashMap<String, String> subjectCenterMap = new HashMap<>();
			//Generate list of confirmed bookings, so that same Date-Time cannot be used for bookings remaining subjects : START
			ArrayList<String> dateTimeBookedList = new ArrayList<String>();
			ArrayList<ExamBookingTransactionBean> subjectsBooked = eDao.getConfirmedBooking(student.getSapid());

			for (ExamBookingTransactionBean bean : subjectsBooked) {
				String subject = bean.getSubject();
				if("Y".equals(bean.getBooked())){
					response.setHasConfirmedBookings("true");
					dateTimeBookedList.add(bean.getExamTime()+"|"+bean.getExamDate());//Added in pipe format since thats the format used on the page//

					bookedSubjects.add(subject);
					subjectCenterMap.put(subject, examCenterIdNameMap.get(bean.getCenterId()) + " ("+ bean.getExamDate()+ ", "+ bean.getExamTime()+")");
				}else if("RL".equals(bean.getBooked())  && SEAT_RELEASED.equalsIgnoreCase(bean.getTranStatus())  && (!releasedSubjects.contains(subject)) && (!passList.contains(subject))){
					releasedSubjects.add(subject);
				}else if("RL".equals(bean.getBooked())  && SEAT_RELEASED_NO_CHARGES.equalsIgnoreCase(bean.getTranStatus())  && (!releasedNoChargeSubjects.contains(subject))){
					releasedNoChargeSubjects.add(subject);
				}else if("RL".equals(bean.getBooked())  && SEAT_RELEASED_SUBJECT_CLEARED.equalsIgnoreCase(bean.getTranStatus())  && (!releasedPassedSubjects.contains(subject))){
					releasedPassedSubjects.add(subject);
				}
			}
			response.setDateTimeBookedList(dateTimeBookedList);//Placing the list in attribute//
			//Add subjects in different bucket, based on their current Exam Registration Status: END

			//Logic to handle, if student seat is released, then booked and then again released and again booked: START 
			//In this case old released subjects will again pop up as to be booked, whereas there are separate rows for it as booked well.
			for (ExamBookingTransactionBean bean : subjectsBooked) {
				String subject = bean.getSubject();
				String booked = bean.getBooked();

				if("Y".equals(bean.getBooked())){
					releasedSubjects.remove(subject);
					releasedNoChargeSubjects.remove(subject);
				}
				
				if("RL".equals(bean.getBooked())){
					if(releasedNoChargeSubjects.contains(subject)){
						releasedSubjects.remove(subject);
					}
				}
			}
			//Logic to handle, if student seat is released, then booked and then again released and again booked: END
			
			approvedOnlineTransactionSubjects = eDao.getApprovedOnlineTransSubjects(student.getSapid());
			if(approvedOnlineTransactionSubjects.contains("Project")){
				approvedOnlineTransactionSubjects.remove("Project");
			}
			if(approvedOnlineTransactionSubjects.contains("Module 4 - Project") && "PD - WM".equalsIgnoreCase(student.getProgram())){
				approvedOnlineTransactionSubjects.remove("Module 4 - Project");
			}
			
			response.setApprovedOnlineTransactionSubjects(approvedOnlineTransactionSubjects);
			//Add subjects in different bucket based on Payment Status: END

			//Iterate through each subject and create Exam applicable+registered subjects list: START
			HashMap<String, ProgramSubjectMappingExamBean> subjectProgramSemMap = new HashMap<String, ProgramSubjectMappingExamBean>();
			ArrayList<ProgramSubjectMappingExamBean> programSubjectMappingList = getProgramSubjectMappingList();
			for (int i = 0; i < programSubjectMappingList.size(); i++) {
				ProgramSubjectMappingExamBean bean = programSubjectMappingList.get(i);



				if(bean.getPrgmStructApplicable().equals(student.getPrgmStructApplicable()) 
						&& bean.getProgram().equals(student.getProgram())
						&& !student.getWaivedOffSubjects().contains(bean.getSubject()) ){
					if(passList.contains(bean.getSubject())){
						//Not applicable to book if already cleared. Do not remove this condition
						continue;
					}

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
						if(bean.getSubject().equalsIgnoreCase("Module 4 - Project") && "PD - WM".equalsIgnoreCase(student.getProgram())){
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
					}/*else if(approvedDDSubjects.contains(bean.getSubject())){
						hasApprovedDD = true;
						bean.setCanBook("No");
						bean.setBookingStatus(DD_APPROVED);
						applicableSubjects.remove(bean.getSubject());
						subjectsToPay--;
					}else if(rejectedDDSubjects.contains(bean.getSubject())){
						//hasApprovedDD = true;
						bean.setCanBook("No");
						bean.setBookingStatus(DD_REJECTED);
						applicableSubjects.remove(bean.getSubject());
						subjectsToPay--;
					}else if(pendingDDApprovalSubjects.contains(bean.getSubject())){
						bean.setCanBook("No");
						bean.setBookingStatus(DD_APPROVAL_PENDING);
						applicableSubjects.remove(bean.getSubject());
						subjectsToPay--;
					}*/


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
			if(freeApplicableSubjects.contains("Project")){
				freeApplicableSubjects.remove("Project");
			}
			if(freeApplicableSubjects.contains("Module 4 - Project") && "PD - WM".equalsIgnoreCase(student.getProgram())){
				freeApplicableSubjects.remove("Module 4 - Project");
			}
			
			if(releasedSubjects.contains("Project")){
				releasedSubjects.remove("Project");
			}
			if(releasedSubjects.contains("Module 4 - Project") && "PD - WM".equalsIgnoreCase(student.getProgram())){
				releasedSubjects.remove("Module 4 - Project");
			}
			
			if(releasedNoChargeSubjects.contains("Project")){
				releasedNoChargeSubjects.remove("Project");
			}
			if(releasedNoChargeSubjects.contains("Module 4 - Project") && "PD - WM".equalsIgnoreCase(student.getProgram())){
				releasedNoChargeSubjects.remove("Module 4 - Project");
			}
			
			if(subjectProgramSemMap.containsKey("Project")){
				subjectProgramSemMap.remove("Project");
			}
			if(subjectProgramSemMap.containsKey("Module 4 - Project") && "PD - WM".equalsIgnoreCase(student.getProgram())){
				subjectProgramSemMap.remove("Module 4 - Project");
			}
			
			response.setFreeApplicableSubjects(freeApplicableSubjects);
			response.setReleasedSubjects(releasedSubjects);
			response.setReleasedNoChargeSubjects(releasedNoChargeSubjects);
			response.setSubjectProgramSemMap(subjectProgramSemMap);
			//Iterate through each subject and create Exam applicable+registered subjects list: END



			//Set various flags based on subjects found in different bucket: START
			/*if(hasApprovedDD){
				request.setAttribute("hasApprovedDD", "true");
			}*/
			if(hasApprovedOnlineTransactions){
				response.setHasApprovedOnlineTransactions(hasApprovedOnlineTransactions);
			}
			if(hasFreeSubjects){
				response.setHasFreeSubjects(hasFreeSubjects);
			}
			if(hasReleasedSubjects){
				response.setHasReleasedSubjects(hasReleasedSubjects);
			}
			if(hasReleasedNoChargeSubjects){
				response.setHasReleasedNoChargeSubjects(hasReleasedNoChargeSubjects);
			}
			//Set various flags based on subjects found in different bucket: END



			//Logic for deciding if Regular Exam fees should be charged or Resit fees should be charged
			HashMap<String,Integer> mapOfSubjectNameAndExamFee = new HashMap<String,Integer>();
			ArrayList<StudentMarksBean> writtenAttemptsList = (ArrayList<StudentMarksBean>)eDao.getWrittenAttempts(student.getSapid());
			HashMap<String, String> subejctWrittenScoreMap = new HashMap<>();
			for (StudentMarksBean marksBean : writtenAttemptsList) {//This will store data if student ever had written attempt, for deciding exam fees
				if(marksBean.getWritenscore() != null ){
					subejctWrittenScoreMap.put(marksBean.getSubject(), marksBean.getWritenscore());
				}
			}

			ArrayList<String> assignmentSubmittedSubjects = (ArrayList<String>)eDao.getAssignSubmittedSubjectsList(student.getSapid());
			HashMap<String,Boolean> confirmedBookingsExcludingCurrentCycleMap = eDao.getConfirmedBookingsExcludingCurrentCycleMap(student.getSapid());
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
				if(assignmentSubmittedSubjects.contains(bean.getSubject()) || failedAssignmentSubjectList.contains(bean.getSubject())){
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
			}
			

			if(mapOfSubjectNameAndExamFee.containsKey("Project")){
				mapOfSubjectNameAndExamFee.remove("Project");
			}
			if(mapOfSubjectNameAndExamFee.containsKey("Module 4 - Project") && "PD - WM".equalsIgnoreCase(student.getProgram())){
				mapOfSubjectNameAndExamFee.remove("Module 4 - Project");
			}
			
			if(applicableSubjects.contains("Project")){
				applicableSubjects.remove("Project");
				subjectsToPay--;
			}
			if(applicableSubjects.contains("Module 4 - Project") && "PD - WM".equalsIgnoreCase(student.getProgram())){
				applicableSubjects.remove("Module 4 - Project");
				subjectsToPay--;
			}
			
			if(freeApplicableSubjects.contains("Project")){
				freeApplicableSubjects.remove("Project");
			}
			if(freeApplicableSubjects.contains("Module 4 - Project") && "PD - WM".equalsIgnoreCase(student.getProgram())){
				freeApplicableSubjects.remove("Module 4 - Project");
			}
			
			applicableSubjectsList.removeAll(notApplicableSubjectsList);
			
			response.setMapOfSubjectNameAndExamFee(mapOfSubjectNameAndExamFee);
			examBooking.setApplicableSubjects(applicableSubjects);
			examBooking.setFreeApplicableSubjects(freeApplicableSubjects);
			response.setApplicableSubjectsList(applicableSubjectsList);
			response.setApplicableSubjectsListCount( applicableSubjectsList.size());
			response.setExamBookingBean(examBooking);
			response.setSubjectsToPay(subjectsToPay);
			response.setStatus("success");
		} catch (Exception e) {
			// TODO: handle exception
			
			response.setError("fail");
			response.setError("Error in getting subjects.");
		}
		return new ResponseEntity(response, headers, HttpStatus.OK);
	}
	
	@PostMapping(path = "/selectSubjects", consumes="application/json", produces="application/json")
	public ResponseEntity<ExamSelectSubjectBeanAPIResponse> mselectSubjects(@RequestBody ExamRegistrationBeanAPIRequest input) {
		ExamSelectSubjectBeanAPIResponse response = new ExamSelectSubjectBeanAPIResponse();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
//		request.getSession().setAttribute("ddSeatBookingComplete", null);
//		request.getSession().setAttribute("onlineSeatBookingComplete", null);
//		request.getSession().setAttribute("freeSeatBookingComplete", null);
// 
		if(input.getSapid() == null) {
			response.setStatus("fail");
			response.setError("Invalid sapId found");
			return new ResponseEntity<ExamSelectSubjectBeanAPIResponse>(response, HttpStatus.OK);
		}
		
		if(input.getApplicableSubjects() == null) {
			response.setStatus("fail");
			response.setError("Invalid applicationSubjectList found");
			return new ResponseEntity<ExamSelectSubjectBeanAPIResponse>(response, HttpStatus.OK);
		}
		
		if(input.getMapOfSubjectNameAndExamFee() == null) {
			response.setStatus("fail");
			response.setError("Invalid mapOfSubjectNameAndExamFee found");
			return new ResponseEntity<ExamSelectSubjectBeanAPIResponse>(response, HttpStatus.OK);
		}
		
		try{
			ArrayList<String> subjects = new ArrayList<String>();
			subjects.addAll(input.getApplicableSubjects());
			int totalExamFees = 0;
			
			HashMap<String,Integer> mapOfSubjectNameAndExamFee = (HashMap<String,Integer>)input.getMapOfSubjectNameAndExamFee(); 
			for(String subject :subjects){
				totalExamFees = totalExamFees + mapOfSubjectNameAndExamFee.get(subject);
			}
			response.setTotalExamFees(totalExamFees);
			//Remove duplicates using Set
			Set<String> set = new HashSet<String>(subjects);
			subjects = new ArrayList<String>(set);
			ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
			StudentExamBean student ;
			student = dao.getSingleStudentsData(input.getSapid());
			//String studentProgramStructure = student.getPrgmStructApplicable();
			List<TimetableBean> timeTableList = dao.getTimetableListForGivenSubjects(subjects,student);
			response.setTimeTableList(timeTableList); 
			ExamCenterBean examCenter = new ExamCenterBean();
			examCenter.setPaymentMode("Online");
			response.setExamCenter(examCenter);
			response.setSubjects(subjects);
			response.setNoOfSubjects(subjects.size());
			//modelnView.addObject("examFeesPerSubject", examFeesPerSubject);
			//modelnView.addObject("totalFees", (examFeesPerSubject * subjects.size()) + "");
			Map<String, List<ExamCenterBean>> centerSubjectMapping = mgetAvailableCenters(input, timeTableList, student, subjects);	
			response.setCenterSubjectMapping(centerSubjectMapping);
			response.setStatus("success");
//			modelnView.addObject("examBooking", examBooking);
		}catch(Exception e){
			
			response.setStatus("fail");
			response.setError(e.getMessage());
			//request.setAttribute("error", "true");
			//request.setAttribute("errorMessage", "Error in getting subjects.");
			//modelnView = new ModelAndView("selectSubjects");
		}
		return new ResponseEntity<ExamSelectSubjectBeanAPIResponse>(response, headers, HttpStatus.OK);
	}
	
	@PostMapping(path="/getAvailableCentersForCityForOnlineExam/v2", consumes="application/json", produces="application/json", headers="content-type=application/json")
	public ResponseEntity<Map<String, Map<String, List<ExamCenterBean>>>> mgetAvailableCentersForCityV2(HttpServletRequest request,@RequestBody StudentExamBean input ) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type","application/json");

		Map<String, Map<String, List<ExamCenterBean>>> response = new HashMap<String, Map<String, List<ExamCenterBean>>>();
		String sapid = input.getSapid();
		String centerId = input.getCenterCode();
		try {
			ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
			ExamCenterDAO ecDao = (ExamCenterDAO)act.getBean("examCenterDAO");
			StudentExamBean student = (StudentExamBean)request.getSession().getAttribute("studentExam");
			
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
			
			//List<ExamCenterBean> availableCenters = ecDao.getAvailableCentersForRegularOnlineExam(sapid, student.isCorporateExamCenterStudent(), student.getMappedCorporateExamCenterId());
			//Create Exam Center Time List

			List<ExamCenterBean> availableCenters = new ArrayList<ExamCenterBean>();
			
			// removed centers if they are from the past dates to avoid booking for past dates
			//added by Swarup in Feb 2023, 24  hrs  prior card
			availableCenters = removePastDatesFromSelectSlotsOption(sapid, ecDao, student);
//			} else {
//				availableCenters = ecDao.getAvailableCentersForRegularOnlineExamCorporateDiageo(sapid, student.isCorporateExamCenterStudent(), student.getMappedCorporateExamCenterId());
//			}	
			
			ArrayList<ExamCenterBean> listOfAvailableCenters = new ArrayList<ExamCenterBean>();
			for (ExamCenterBean examCenterBean : availableCenters) {
				if(examCenterBean.getCenterId().equals(centerId)){
					listOfAvailableCenters.add(examCenterBean);
				}
			}

			Map<Integer, Map<Integer, List<ExamCenterBean>>> yearWeekendAndCentersList = new HashMap<Integer, Map<Integer, List<ExamCenterBean>>>();
			
			for (ExamCenterBean examCenterBean : listOfAvailableCenters) {

				
				String dateStr = examCenterBean.getDate();
				SimpleDateFormat inputDateFormatter = new SimpleDateFormat("yyyy-MM-dd");
				Date date = inputDateFormatter.parse(dateStr);
				
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);
				int week = cal.get(Calendar.WEEK_OF_YEAR);
				int dayOfYear = cal.get(Calendar.DAY_OF_YEAR);
				int dayNum = cal.get(Calendar.DAY_OF_WEEK);
				int year = cal.get(Calendar.YEAR);

				int weekendNumber = week;
				
				// for the last weekend of the year.
				if(week == 1 && dayOfYear > 8) {
					weekendNumber = 53;
				}
				
				// if Sunday, subtract week number by 1
				if(dayNum == 1) {
					if(dayOfYear == 1) {
						// for first day of the year, count it in the last weekend of the previous year.
						year = year - 1;
						weekendNumber = 53;
					} else {
						// count it in the previous weekend.
						weekendNumber = weekendNumber - 1;
					}
				}
				
				// if data exists for this year
				Map<Integer, List<ExamCenterBean>> weekendAndCentersList = yearWeekendAndCentersList.containsKey(year)
						? yearWeekendAndCentersList.get(year) : new HashMap<Integer, List<ExamCenterBean>>();

				// if a list for this weekend number exists, get the value, else create new list
				List<ExamCenterBean> slotsForTheWeekend = weekendAndCentersList.containsKey(weekendNumber)
						? weekendAndCentersList.get(weekendNumber) 
						: new ArrayList<ExamCenterBean>();
						
				slotsForTheWeekend.add(examCenterBean);
				weekendAndCentersList.put(weekendNumber, slotsForTheWeekend);
				yearWeekendAndCentersList.put(year, weekendAndCentersList);
			}
			// Sort by Year
			yearWeekendAndCentersList = new TreeMap<Integer, Map<Integer, List<ExamCenterBean>>>(yearWeekendAndCentersList);
			
			int i = 1;
			for (Map.Entry<Integer, Map<Integer, List<ExamCenterBean>>> yearEntry  : yearWeekendAndCentersList.entrySet()) {

				// Sort by weekend and 
				Map<Integer, List<ExamCenterBean>> weekendAndCentersList = new TreeMap<Integer, List<ExamCenterBean>>(yearEntry.getValue());
				for (Map.Entry<Integer, List<ExamCenterBean>> weekendEntry  : weekendAndCentersList.entrySet()) {
					String weekendString = "" + i;
					response.put(weekendString, getDateSortedSlotsList(weekendEntry.getValue()));
					i++;
				}
			}
			
			// Sort by name(weekend number).
			response = new TreeMap<String, Map<String, List<ExamCenterBean>>>(response);
			
			return new ResponseEntity<Map<String, Map<String, List<ExamCenterBean>>>>(response, headers, HttpStatus.OK);
		} catch (Exception e) {
			
			return new ResponseEntity<Map<String, Map<String, List<ExamCenterBean>>>>(response, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * @param sapid
	 * @param ecDao
	 * @param student
	 * @return
	 */
	private List<ExamCenterBean> removePastDatesFromSelectSlotsOption(String sapid, ExamCenterDAO ecDao,
			StudentExamBean student) {
		
		List<ExamCenterBean> availableCenters;
		Date dateobj = new Date();
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dateobj);
		calendar.add(Calendar.DATE, 1);
		Date addedDate = calendar.getTime();
//			String currentDate = sdfDate.format(dateobj);
//			if(!student.getConsumerType().equals("Diageo")){
		availableCenters = ecDao.getAvailableCentersForRegularOnlineExam(sapid, student.isCorporateExamCenterStudent(),
				student.getMappedCorporateExamCenterId());

		availableCenters.removeIf(c -> {
			Date slotDate = null;
			try {
				slotDate = sdfDate.parse(c.getDate());
			} catch (ParseException e) {
			}
			return addedDate.after(slotDate);
		});
		return availableCenters;
	}
	
	@PostMapping(path="/examTestTakenStatus")
	public ResponseEntity<Map<String, String>> examTestTakenStatus(@RequestBody ExamBookingTransactionBean examBookingTransactionBean){
		ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type","application/json");
		Map<String, String> resp = new HashMap<String, String>();
		try {
			String count = dao.getCountTestTaken(examBookingTransactionBean.getExamDate(), examBookingTransactionBean.getExamTime());
			resp.put("status", "success");
			resp.put("count", count);
		}catch (Exception e) {
			resp.put("status", "error");
			resp.put("count", "Error " + e.getMessage());
		}
		return new ResponseEntity<Map<String, String>>(resp, headers, HttpStatus.OK);
	}
	
	@PostMapping(path="/getAvailableCentersForCityForOnlineExam", consumes="application/json", produces="application/json", headers="content-type=application/json")
	public ResponseEntity<ArrayList<ExamCenterBean>> mgetAvailableCentersForCity(@RequestBody StudentExamBean input) {
		try {
			if(input.getSapid() == null || input.getCenterCode() == null) {
				return new ResponseEntity<ArrayList<ExamCenterBean>>(new ArrayList<ExamCenterBean>(), HttpStatus.OK);
			}
			String centerId = input.getCenterCode();
			String sapid = input.getSapid();
			ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
			ExamCenterDAO ecDao = (ExamCenterDAO)act.getBean("examCenterDAO");
			StudentExamBean student = dao.getSingleStudentsData(sapid);
			
			List<ExamCenterBean> availableCenters = new ArrayList<ExamCenterBean>();
			ArrayList<ExamCenterBean> responseCenter = new ArrayList<ExamCenterBean>();
			if(!student.getConsumerType().equals("Diageo")){
				availableCenters = ecDao.getAvailableCentersForRegularOnlineExam(sapid, student.isCorporateExamCenterStudent(), student.getMappedCorporateExamCenterId());

			} else {
				availableCenters = ecDao.getAvailableCentersForRegularOnlineExamCorporateDiageo(sapid, student.isCorporateExamCenterStudent(), student.getMappedCorporateExamCenterId());
			}
			
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE, dd-MMM-yyyy");
			
			for(ExamCenterBean examCenterBean : availableCenters) {
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
					examCenterBean.setId(dropDownValue);
					examCenterBean.setName(dropDownLabel);
					responseCenter.add(examCenterBean);
				}
			}
			return new ResponseEntity<ArrayList<ExamCenterBean>>(responseCenter, HttpStatus.OK);
		}
		catch (Exception e) {
			// TODO: handle exception
			
			return new ResponseEntity<ArrayList<ExamCenterBean>>(new ArrayList<ExamCenterBean>(), HttpStatus.OK);
		}
	}
	
	
	/*
	 * This method will save selection from user for exam center. 
	 * Delete any old track ids 
	 * Generate new track id and save it in database
	 * Create transaction parameters and redirect to gateway.
	 * */
	
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
	
	
	@RequestMapping(value = "/examFeesReponseSDK", method = {RequestMethod.GET, RequestMethod.POST})
	public ResponseEntity<HashMap<String, String>> mexamFeeFinalPaymentResponseSDK(HttpServletRequest request) {
		/*if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}*/
		/*String typeOfPayment = (String)request.getParameter("PaymentMethod");
		 * */
		saveAllTransactionDetails(request);
		HashMap<String, String> responseData = new HashMap<String,String>();
		paymentHelper.setPaymentReturnUrl("exam_registration");
		String errorMessage = paymentHelper.checkErrorInPayment(request);
		if(errorMessage != null){
			responseData.put("status", "error");
			responseData.put("error", errorMessage);
			return new ResponseEntity<HashMap<String,String>>(responseData, HttpStatus.OK);
		}/*else{
			return msaveSuccessfulTransaction(request, response, model);
		}*/
		return null;	//remove after msaveSuccessfulTransaction function
	}
	
	
	@RequestMapping(value = "/searchBookingsToReleaseStudent",  method = {RequestMethod.GET, RequestMethod.POST})
	public ResponseEntity<BookedResponse> msearchBookingsToReleaseStudent(HttpServletRequest request, HttpServletResponse response){
		//ArrayList<HashMap> response_api = new ArrayList<>();
		BookedResponse api_response = new BookedResponse();
		String sapid = (String)request.getParameter("sapid");

		boolean isExamRegistraionLive = misExamRegistraionLive(sapid);
		api_response.setExamregistration("false");
		if(isExamRegistraionLive){
			ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
			api_response.setExamregistration("true");
			ArrayList<ExamBookingTransactionBean> confirmedBookings = eDao.getConfirmedBookings(sapid);
			

			if(confirmedBookings == null || confirmedBookings.size() == 0){
				api_response.setData(new ArrayList<ExamBookingTransactionBean>());
			}else{
				api_response.setData(confirmedBookings);
			}
			ExamCenterDAO dao = (ExamCenterDAO)act.getBean("examCenterDAO");
			Map<String, String> corporateCenterUserMapping = dao.getCorporateCenterUserMapping();
			
			if(corporateCenterUserMapping.containsKey(sapid)){
				api_response.setCenters(eDao.getCorporateExamCenterIdNameMap());
			}else{
				api_response.setCenters(getExamCenterIdNameMap());
			}
		}
		//message examregistration is not live
		return new ResponseEntity<BookedResponse>(api_response, HttpStatus.OK);
	}
	
	@PostMapping(path = "/releaseBookingsStudent")
	public ResponseEntity<mreleaseBookingsStudent> mreleaseBookingsStudent(
			@RequestBody ExamSelectSubjectAPIRequest input,
			HttpServletRequest request, HttpServletResponse response){
		mreleaseBookingsStudent api_response = new mreleaseBookingsStudent();
		HashMap<String, String> centersList = new HashMap<String,String>();
		ArrayList<ExamBookingTransactionBean> bookingsList = new ArrayList<>();
		String message;
		int status = 200; 
		
		ExamCenterDAO dao = (ExamCenterDAO)act.getBean("examCenterDAO");
		ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");

		String sapid = input.getSapid();
		ArrayList<String> releasedSubjects = new ArrayList<String>(input.getSubjects());
		//modelnView.addObject("booking", booking);
 
		Map<String, String> corporateCenterUserMapping = dao.getCorporateCenterUserMapping();
		
		if(corporateCenterUserMapping.containsKey(sapid)){
			centersList = dao.getCorporateExamCenterIdNameMap();
		}else{
			centersList = getExamCenterIdNameMap();
		}

		boolean isExamRegistraionLive = misExamRegistraionLive(sapid);
		//modelnView.addObject("isExamRegistraionLive", isExamRegistraionLive);

		try {


			//StudentBean student = (StudentBean)request.getSession().getAttribute("student");

			ArrayList<ExamBookingTransactionBean> confirmedBookings = eDao.getConfirmedBookings(sapid);



			
			message = "Seats marked for Release. Please proceed to selection of New Exam Centers. Seats will be released after you complete Payment";

			
			for (int i = 0; i < confirmedBookings.size(); i++) {
				ExamBookingTransactionBean bean = confirmedBookings.get(i);
				if(releasedSubjects.contains(bean.getSubject())){
					bookingsList.add(bean);
				}
			}
			/*confirmedBookings = (ArrayList<ExamBookingTransactionBean>)dao.releaseBookings(sapid, bookingsList, booking, "false");
			MailSender mailSender = (MailSender)act.getBean("mailer");
			mailSender.sendSeatsRealseEmail(sapid, releasedSubjects, student.getEmailId(), "false");*/

		}catch (Exception e) {
			
			status = 500;
			message = "Error in Releasing Seats : "+ e.getMessage();
		}
		
		api_response.setCenters(centersList);
		api_response.setExamregistration(isExamRegistraionLive);
		api_response.setMessage(message);
		api_response.setStatus(status);
		api_response.setData(bookingsList);
		api_response.setTotalExamfees("" + totalFeesForRebooking + "");
		
		return new ResponseEntity<mreleaseBookingsStudent>(api_response,HttpStatus.OK);
		
	}

}
