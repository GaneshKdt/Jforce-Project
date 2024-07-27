package com.nmims.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;

//import javax.security.auth.message.callback.PrivateKeyCallback.Request;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.nmims.beans.ExamBookingMBAWX;
import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.beans.ExamCenterBean;
import com.nmims.beans.ResponseBean;
import com.nmims.beans.ServiceRequestBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.TimetableBean;
import com.nmims.daos.ExamBookingDAO;
import com.nmims.daos.ExamCenterDAO;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.helpers.ExamBookingPDFCreator;
import com.nmims.helpers.HallTicketPDFCreator;
import com.nmims.helpers.MailSender;
import com.nmims.helpers.SalesforceHelper;
import com.nmims.services.HtServiceLayer;
import com.nmims.daos.ServiceRequestDAO;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@Controller
public class HallTicketController extends BaseController {

	@Autowired
	ApplicationContext act;

	@Autowired
	SalesforceHelper salesforceHelper;
	
	@Autowired
	HtServiceLayer htServiceLayer;

	@Value("${MARKSHEETS_PATH}")
	private String MARKSHEETS_PATH;

	@Value("${STUDENT_PHOTOS_PATH}")
	private String STUDENT_PHOTOS_PATH;

	@Value("${HALLTICKET_PATH}")
	private String HALLTICKET_PATH;

	@Value("${SERVER_PATH}")
	private String SERVER_PATH;

	@Value("${FEE_RECEIPT_PATH}")
	private String FEE_RECEIPT_PATH;

	@Value("#{'${CORPORATE_CENTERS}'.split(',')}")
	private List<String> corporateCenterList;

	@Autowired
	HallTicketPDFCreator hallTicketCreator;
	@Autowired
	ServiceRequestDAO sDao;
	private static final Logger logger = LoggerFactory.getLogger(HallTicketController.class);
	private final int pageSize = 10;
	private String mostRecentTimetablePeriod = null;
	private HashMap<String, String> programCodeNameMap = null;

	private HashMap<String, String> examCenterIdNameMap = null;
	HashMap<String, ExamCenterBean> examCenterIdCenterMap = null;
	TreeMap<String, String> offlineExamCenterMap = null;
	TreeMap<String, String> onlineExamCenterMap = null;
	private ArrayList<String> programList = null;
	private ArrayList<String> subjectList = null;
	HashMap<String, String> corporateCenterUserMapping = null;

	private ArrayList<String> yearList = new ArrayList<String>(
			Arrays.asList("2014", "2015", "2016", "2017", "2018", "2019" , "2020"));

	private ArrayList<String> stateList = new ArrayList<String>(Arrays.asList("Andhra Pradesh", "Arunachal Pradesh",
			"Assam", "Bihar", "Chhattisgarh", "Goa", "Gujarat", "Haryana", "Himachal Pradesh", "Jammu and Kashmir",
			"Jharkhand", "Karnataka", "Kerala", "Madhya Pradesh", "Maharashtra", "Manipur", "Meghalaya", "Mizoram",
			"Nagaland", "Odisha", "Punjab", "Rajasthan", "Sikkim", "Tamil Nadu", "Telangana", "Tripura",
			"Uttar Pradesh", "Uttarakhand", "West Bengal", "Andaman and Nicobar Islands", "Chandigarh",
			"Dadar and Nagar Haveli", "Daman and Diu", "Delhi", "Lakshadweep", "Pondicherry"));

	/**
	 * Refresh Cache function to refresh cache
	 * 
	 * @param none
	 * @return none
	 */
	public String RefreshCache() {
		subjectList = null;
		getSubjectList();

		programList = null;
		getProgramList();

		examCenterIdNameMap = null;
		getExamCenterIdNameMap();

		mostRecentTimetablePeriod = null;
		getMostRecentTimetablePeriod();

		offlineExamCenterMap = null;
		getOfflineExamCenterMapForDropDown();

		corporateCenterUserMapping = null;
		getCorporateCenterUserMapping();

		return null;
	}

	public ArrayList<String> getSubjectList() {
		if (this.subjectList == null || this.subjectList.size() == 0) {
			StudentMarksDAO dao = (StudentMarksDAO) act.getBean("studentMarksDAO");
			this.subjectList = dao.getAllSubjects();
		}
		return subjectList;
	}

	public ArrayList<String> getProgramList() {
		if (this.programList == null || this.programList.size() == 0) {
			StudentMarksDAO dao = (StudentMarksDAO) act.getBean("studentMarksDAO");
			this.programList = dao.getAllPrograms();
		}
		return programList;
	}

	public HashMap<String, String> getExamCenterIdNameMap() {
		if (this.examCenterIdNameMap == null || this.examCenterIdNameMap.size() == 0) {
			ExamCenterDAO dao = (ExamCenterDAO) act.getBean("examCenterDAO");
			this.examCenterIdNameMap = dao.getExamCenterIdNameMap();
		}
		return examCenterIdNameMap;
	}

	public HashMap<String, ExamCenterBean> getExamCenterCenterDetailsMap(boolean isCorporate) {

		ExamCenterDAO dao = (ExamCenterDAO) act.getBean("examCenterDAO");
		this.examCenterIdCenterMap = dao.getExamCenterCenterDetailsMap(isCorporate);
		return examCenterIdCenterMap;
	}

	public TreeMap<String, String> getOfflineExamCenterMapForDropDown() {
		if (this.offlineExamCenterMap == null || this.offlineExamCenterMap.size() == 0) {
			ExamCenterDAO dao = (ExamCenterDAO) act.getBean("examCenterDAO");
			HashMap<String, ExamCenterBean> examCenterIdCenterMap = getExamCenterCenterDetailsMap(false);
			this.offlineExamCenterMap = new TreeMap<>();
			for (Map.Entry<String, ExamCenterBean> entry : examCenterIdCenterMap.entrySet()) {
				String centerId = entry.getKey();
				ExamCenterBean bean = entry.getValue();
				if (!"Offline".equalsIgnoreCase(bean.getMode())) {
					continue;
				}
				offlineExamCenterMap.put(centerId,
						bean.getExamCenterName() + "," + bean.getCity() + " (" + bean.getMode() + ")");
			}
		}
		return offlineExamCenterMap;
	}

	public HashMap<String, String> getCorporateCenterUserMapping() {
		if (this.corporateCenterUserMapping == null || this.corporateCenterUserMapping.size() == 0) {
			ExamCenterDAO dao = (ExamCenterDAO) act.getBean("examCenterDAO");
			this.corporateCenterUserMapping = dao.getCorporateCenterUserMapping();
		}
		return this.corporateCenterUserMapping;
	}

	public TreeMap<String, String> getOnlineExamCenterMapForDropDown(boolean isCorporate) {
		if (this.onlineExamCenterMap == null || this.onlineExamCenterMap.size() == 0) {
			ExamCenterDAO dao = (ExamCenterDAO) act.getBean("examCenterDAO");
			HashMap<String, ExamCenterBean> examCenterIdCenterMap = getExamCenterCenterDetailsMap(isCorporate);
			this.onlineExamCenterMap = new TreeMap<>();
			for (Map.Entry<String, ExamCenterBean> entry : examCenterIdCenterMap.entrySet()) {
				String centerId = entry.getKey();
				ExamCenterBean bean = entry.getValue();
				if (!"Online".equalsIgnoreCase(bean.getMode())) {
					continue;
				}
				onlineExamCenterMap.put(centerId,
						bean.getExamCenterName() + "," + bean.getCity() + " (" + bean.getMode() + ")");
			}
		}
		return onlineExamCenterMap;
	}

	public String getMostRecentTimetablePeriod() {
		if (this.mostRecentTimetablePeriod == null) {
			StudentMarksDAO sDao = (StudentMarksDAO) act.getBean("studentMarksDAO");
			this.mostRecentTimetablePeriod = sDao.getMostRecentTimeTablePeriod();
		}

		return this.mostRecentTimetablePeriod;
	}

	public HashMap<String, String> getProgramMap() {
		if (this.programCodeNameMap == null || this.programCodeNameMap.size() == 0) {
			StudentMarksDAO dao = (StudentMarksDAO) act.getBean("studentMarksDAO");
			this.programCodeNameMap = dao.getProgramDetails();
		}
		return programCodeNameMap;
	}
	
	//Commented  by Riya as mapping is shifted in HallTicketStudentController

	/*@RequestMapping(value = "/myDocuments", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView myDocuments(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView modelAndView = new ModelAndView("myDocuments");
		StudentBean student = (StudentBean) request.getSession().getAttribute("student");

		getAdmissionFeeReceiptFromSapId(student, request);

		getExamFeeReceiptFromSapId(student, request);

		getHallTicketFromSapId(student, request);

		getPCPBookingsFromSapid(student, request);
		
		getAssignmentFeeReceiptFromSapid(student, request);

		getProjectFeeReceiptFromSapid(student, request);
		
		getSRFeeReceiptFromSapid(student, request);
		
		return modelAndView;

	}
	


	}
	private void getSRFeeReceiptFromSapid(StudentBean student, HttpServletRequest request) {
		ExamBookingDAO dao = (ExamBookingDAO) act.getBean("examBookingDAO");
		ArrayList<ExamBookingTransactionBean> listOfSrFeeReceiptsBasedOnSapid=new ArrayList<ExamBookingTransactionBean>();
		ArrayList<ExamBookingTransactionBean> feeReceiptList = dao
				.listOfDocumentsBasedOnSapidAndDocType(student.getSapid(), "SR Fee Receipt");
		for(ExamBookingTransactionBean receipt : feeReceiptList) {
			
			ServiceRequestBean sr = sDao.checkIfSrPaymentSuccessful(receipt.getReferenceId()+"");
			if(sr != null) { 
				receipt.setServiceRequestType(sr.getServiceRequestType());
				listOfSrFeeReceiptsBasedOnSapid.add(receipt);
			}
			
		}
		request.setAttribute("listOfSrFeeReceiptsBasedOnSapid", listOfSrFeeReceiptsBasedOnSapid);

	}*/
	private void getProjectFeeReceiptFromSapid(StudentExamBean student, HttpServletRequest request) {
		ExamBookingDAO dao = (ExamBookingDAO) act.getBean("examBookingDAO");
		ArrayList<ExamBookingTransactionBean> listOfProjectFeeReceiptsBasedOnSapid = new ArrayList<ExamBookingTransactionBean>();
		
		try {
			ArrayList<ExamBookingTransactionBean> feeReceiptsBasedOnSapid = dao
					.listOfDocumentsBasedOnSapidAndDocType(student.getSapid(), "Project Fee Receipt");
		    for(ExamBookingTransactionBean receipt : feeReceiptsBasedOnSapid) {
				if(dao.checkIfProjectPaymentSuccessful(receipt.getReferenceId())) {
					listOfProjectFeeReceiptsBasedOnSapid.add(receipt);
				}
			}
		} catch (Exception e) { }
		request.setAttribute("listOfProjectFeeReceiptsBasedOnSapid", listOfProjectFeeReceiptsBasedOnSapid);

	}
	private void getAssignmentFeeReceiptFromSapid(StudentExamBean student, HttpServletRequest request) {
		ExamBookingDAO dao = (ExamBookingDAO) act.getBean("examBookingDAO");
		ArrayList<ExamBookingTransactionBean> listOfAssignmentFeeReceiptsBasedOnSapid = new ArrayList<ExamBookingTransactionBean>();
		
		try {
			ArrayList<ExamBookingTransactionBean> feeReceiptsBasedOnSapid = dao
					.listOfDocumentsBasedOnSapidAndDocType(student.getSapid(), "Assignment Fee Receipt");
			for(ExamBookingTransactionBean receipt : feeReceiptsBasedOnSapid) {
				ExamBookingTransactionBean booking= dao.checkIfAssignmentPaymentSuccessful(receipt.getReferenceId());
				if(booking != null) {
					receipt.setSubject(booking.getSubject());
					listOfAssignmentFeeReceiptsBasedOnSapid.add(receipt);
				}
			}
		} catch (Exception e) {}
		
		request.setAttribute("listOfAssignmentFeeReceiptsBasedOnSapid", listOfAssignmentFeeReceiptsBasedOnSapid);

	}
	private void getPCPBookingsFromSapid(StudentExamBean student, HttpServletRequest request) {
		ExamBookingDAO dao = (ExamBookingDAO) act.getBean("examBookingDAO");
		ArrayList<ExamBookingTransactionBean> listOfPCPBookingsBasedOnSapid = dao
				.listOfDocumentsBasedOnSapidAndDocType(student.getSapid(), "PCP Fee Receipt");
		request.setAttribute("listOfPCPBookingsBasedOnSapid", listOfPCPBookingsBasedOnSapid);

	}

	private void getAdmissionFeeReceiptFromSapId(StudentExamBean student, HttpServletRequest request) {
		ArrayList<StudentExamBean> lstOfAdmissionPaymentReceipt = salesforceHelper.listOfPaymentsMade(student.getSapid());
		request.setAttribute("lstOfAdmissionPaymentReceipt", lstOfAdmissionPaymentReceipt);
	}

	public void getExamFeeReceiptFromSapId(StudentExamBean student, HttpServletRequest request) {
		ExamBookingDAO dao = (ExamBookingDAO) act.getBean("examBookingDAO");
		ArrayList<ExamBookingTransactionBean> listOfFeeReceiptsBasedOnSapid = dao
				.listOfDocumentsBasedOnSapidAndDocType(student.getSapid(), "Exam Fee Receipt");
		request.setAttribute("listOfFeeReceiptsBasedOnSapid", listOfFeeReceiptsBasedOnSapid);

	}

	public void getHallTicketFromSapId(StudentExamBean student, HttpServletRequest request) {
		ExamBookingDAO dao = (ExamBookingDAO) act.getBean("examBookingDAO");
		ArrayList<ExamBookingTransactionBean> listOfHallTicketsBasedOnSapid = dao
				.listOfDocumentsBasedOnSapidAndDocType(student.getSapid(), "Hall Ticket");
		request.setAttribute("listOfHallTicketsBasedOnSapid", listOfHallTicketsBasedOnSapid);

	}

	@RequestMapping(value = "/admin/massFeeReceiptDownloadForm", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView massFeeReceiptForm(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView("massFeeReceipt");
		model.addObject("examBookingBean", new ExamBookingTransactionBean());
		model.addObject("yearList", yearList);
		return model;
	}

	@RequestMapping(value = "/admin/massFeeReceiptDownload", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView massFeeReceiptDownload(@ModelAttribute ExamBookingTransactionBean examBookingBean,
			HttpServletRequest request, HttpServletResponse response) {
		ExamBookingDAO dao = (ExamBookingDAO) act.getBean("examBookingDAO");
		ExamCenterDAO ecDao = (ExamCenterDAO) act.getBean("examCenterDAO");
		ExamBookingPDFCreator pdfCreator = new ExamBookingPDFCreator();
		boolean alreadyDownloadedForSelectedYearAndMonth = false;
		alreadyDownloadedForSelectedYearAndMonth = dao.alreadyDownloadedDocument(examBookingBean, "Exam Fee Receipt");
		HashMap<String, String> mapOfSapIdAndCourseId = ecDao.getCorporateCenterUserMapping();

		ModelAndView model = new ModelAndView("massFeeReceipt");
		model.addObject("examBookingBean", new ExamBookingTransactionBean());
		model.addObject("yearList", yearList);

		if (alreadyDownloadedForSelectedYearAndMonth) {
			setError(request, "Exam Fee Receipt Already Generated For Selected Year-Month " + examBookingBean.getYear()
					+ "-" + examBookingBean.getMonth());
			return model;
		}

		try {
			HashMap<String, ArrayList<ExamBookingTransactionBean>> getMapOfSapIdAndConfirmedBookingFromMonthAndYear = dao
					.getMapOfSapIdAndConfirmedBookingFromMonthAndYear(examBookingBean);
			ArrayList<String> getConfirmedBookedSapIdListFromMonthAndYearWithoutTimeTableFlag = dao
					.getConfirmedBookedSapIdListFromMonthAndYearWithoutTimeTableFlag(examBookingBean.getMonth(),
							examBookingBean.getYear());
			ArrayList<ExamBookingTransactionBean> examBookingListToBeInserted = new ArrayList<ExamBookingTransactionBean>();

			for (String sapid : getConfirmedBookedSapIdListFromMonthAndYearWithoutTimeTableFlag) {
				StudentExamBean student = dao.getSingleStudentsData(sapid);
				HashMap<String, String> getExamCenterIdMap = new HashMap<String, String>();
				boolean isCorporate = false;
				String fileName = "";
				if (mapOfSapIdAndCourseId.containsKey(sapid)) {
					isCorporate = true;
					getExamCenterIdMap = ecDao.getExamCenterIdNameMapForGivenMonthAndYear(examBookingBean.getYear(),
							examBookingBean.getMonth(), isCorporate);
				} else {
					getExamCenterIdMap = ecDao.getExamCenterIdNameMapForGivenMonthAndYear(examBookingBean.getYear(),
							examBookingBean.getMonth(), isCorporate);
				}
				ExamBookingTransactionBean examBean = new ExamBookingTransactionBean();

				ArrayList<ExamBookingTransactionBean> bookingBeanList = new ArrayList<ExamBookingTransactionBean>();
				bookingBeanList = getMapOfSapIdAndConfirmedBookingFromMonthAndYear.get(sapid);
				List<ExamBookingTransactionBean> confirmedOrReleasedExamBookings = dao
						.getConfirmedOrRelesedBooking(sapid, examBookingBean.getMonth(), examBookingBean.getYear());
				fileName = pdfCreator.createPDF(bookingBeanList, getExamCenterIdMap, FEE_RECEIPT_PATH, student,
						confirmedOrReleasedExamBookings);
				// Set the parameters for batch insert//
				examBean.setSapid(sapid);
				examBean.setFilePath(fileName);
				examBean.setYear(examBookingBean.getYear());
				examBean.setMonth(examBookingBean.getMonth());
				// End//
				examBookingListToBeInserted.add(examBean);
			}
			dao.batchInsertOfDocumentRecords(examBookingListToBeInserted, "Exam Fee Receipt");
			setSuccess(request, examBookingListToBeInserted.size() + " Fee Receipts Generated SuccessFully.");
		} catch (Exception e) {
			
			setError(request, "Error in Generating Fee Receipt");
		}

		return model;

	}
	/*
	previewHallTicket
	@accepts sapid 
	@returns serviceRequestBean
	
	*/

	//Commented  by Riya as mapping is shifted in HallTicketStudentController
	
	//@SuppressWarnings("null")
	/*@RequestMapping(value = "/previewHallTicket", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView previewHallTicket(HttpServletRequest request, HttpServletResponse response, Model m) {

		ModelAndView modelnView = new ModelAndView("previewHallTicket");
		String sapid = (String) request.getSession().getAttribute("userId");
			
		ServiceRequestBean serviceRequestBean=new ServiceRequestBean();
		try {
			serviceRequestBean = htServiceLayer.getHallTicketData(sapid, getMostRecentTimetablePeriod(), getCorporateCenterUserMapping(), getProgramMap());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
		}  
	
		m.addAttribute("serviceLayerResponse", serviceRequestBean);
		
		return modelnView;
	}
*/
	/*
	 MpreviewHallTicket API
	 @accepts sapid
	 @returns serviceRequestBean 
	 */
	
//	to be deleted, api shifted to rest controller
//	@RequestMapping(value = "/m/previewHallTicket", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
//	public ResponseEntity<ServiceRequestBean> MpreviewHallTicket(@RequestBody StudentBean input) throws Exception {
//		
//		HttpHeaders headers = new HttpHeaders();
//		
//		headers.add("Content-Type", "application/json");
//				
//		ServiceRequestBean response = htServiceLayer.getHallTicketData(input.getSapid(), getMostRecentTimetablePeriod(), getCorporateCenterUserMapping(), getProgramMap());
//
//		return new ResponseEntity<ServiceRequestBean>(response, headers, HttpStatus.OK);
//
//	}
//	@RequestMapping(value = "/m/previewHallTicketForMbaWx", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
//	public ResponseEntity<ServiceRequestBean> MpreviewHallTicketForMbaWx(@RequestBody StudentBean input) throws Exception {
//		
//		HttpHeaders headers = new HttpHeaders();
//		
//		headers.add("Content-Type", "application/json");
//				
//		ServiceRequestBean response = htServiceLayer.getHallTicketDataForMbaWx(input.getSapid(), getProgramMap());
//
//		
//		return new ResponseEntity<ServiceRequestBean>(response, headers, HttpStatus.OK);
//
//	}

	@RequestMapping(value = "/admin/downloadHallTicket", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView downloadHallTicket(HttpServletRequest request, HttpServletResponse response, Model m) {
		ModelAndView modelnView = new ModelAndView("studentHallTicket");
		String sapid = (String) request.getSession().getAttribute("userId");
		String userDownloadingHallTicket = sapid; // Logged in user
		ExamBookingDAO dao = (ExamBookingDAO) act.getBean("examBookingDAO");
		List<String> blockedSapids = dao.getBlockedSapids();
		String fileName = "";
		StudentExamBean student;
		if (request.getParameter("userId") != null) {
			// Coming from Admin page used for downloaing student hall ticket
			sapid = request.getParameter("userId");
			modelnView = new ModelAndView("downloadHallTicket");
			ExamBookingDAO ebDao = (ExamBookingDAO) act.getBean("examBookingDAO");
			student = ebDao.getSingleStudentsData(sapid);
		} else if (!checkSession(request, response)) {
			return new ModelAndView("studentPortalRediret");
		} else {

			if (blockedSapids.contains(sapid)) {
				setError(request, "Your Hall Ticket is on hold. Please contact NGASCE to get access for same");
				return modelnView;
			}
			student = (StudentExamBean) request.getSession().getAttribute("studentExam");

			// if he is a student and is logged in , then check if it is available for
			// download

			boolean isHallTicketAvailable = dao.isConfigurationLive("Hall Ticket Download");
			modelnView.addObject("isHallTicketAvailable", isHallTicketAvailable);
			if (!isHallTicketAvailable) {
				setError(request, "Hall Ticket is not available for download currently");
				return modelnView;
			}
		}

		try {
			ArrayList<String> subjects = new ArrayList<>();

			ExamBookingDAO eDao = (ExamBookingDAO) act.getBean("examBookingDAO");
			ArrayList<ExamBookingTransactionBean> subjectsBooked = eDao.getConfirmedBooking(sapid);

			if (subjectsBooked.size() == 0) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No subjects booked for Exam. Hall Ticket not available.");
				return modelnView;

			}

			HashMap<String, ExamBookingTransactionBean> subjectBookingMap = new HashMap<>();

			HashMap<String, ExamBookingTransactionBean> subjectDoubleBookingMap = new HashMap<>();

			for (int i = 0; i < subjectsBooked.size(); i++) {
				ExamBookingTransactionBean bean = subjectsBooked.get(i);
				// Added by Steffi
				String key1 = bean.getSapid() + bean.getSubject();
				String key2 = bean.getSapid() + bean.getExamDate() + bean.getExamTime();
				if (!subjectDoubleBookingMap.containsKey(key1) && !subjectDoubleBookingMap.containsKey(key2)) {
					subjectDoubleBookingMap.put(key1, bean);
					subjectDoubleBookingMap.put(key2, bean);
					subjectBookingMap.put(bean.getSubject(), bean);
				} else {
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "Error");
					return modelnView;
				}
				// Added by Steffi end

			}
			List<ExamBookingTransactionBean> passwordPresent = new ArrayList<ExamBookingTransactionBean>();
			List<ExamBookingTransactionBean> passwordAbsent = new ArrayList<ExamBookingTransactionBean>();
			for (ExamBookingTransactionBean bean : subjectsBooked) {
				if (StringUtils.isBlank(bean.getPassword())) {
					passwordAbsent.add(bean);
				} else {
					passwordPresent.add(bean);
				}
			}
			String password = "";
			if (passwordAbsent.size() > 0 && passwordPresent.size() > 0) {
				password = passwordPresent.get(0).getPassword();
				String month = passwordPresent.get(0).getMonth();
				String year = passwordPresent.get(0).getYear();
				eDao.assignPass(sapid, password, month, year);
			}

			if (passwordAbsent.size() > 0 && passwordPresent.size() == 0) {
				String month = passwordAbsent.get(0).getMonth();
				String year = passwordAbsent.get(0).getYear();
				password = generateRandomPass(sapid);
				eDao.assignPass(sapid, password, month, year);
			}

			if (passwordAbsent.size() == 0 && passwordPresent.size() > 0) {
				password = passwordPresent.get(0).getPassword();

			}
			

			for (int i = 0; i < subjectsBooked.size(); i++) {
				subjects.add(subjectsBooked.get(i).getSubject());
			}

			List<TimetableBean> timeTableList = dao.getTimetableListForGivenSubjects(subjects, student);

			HallTicketPDFCreator hallTicketCreator = new HallTicketPDFCreator();
			corporateCenterUserMapping = getCorporateCenterUserMapping();

			if(corporateCenterUserMapping.containsKey(student.getSapid())){
				fileName = hallTicketCreator.createHallTicket(timeTableList, subjectsBooked, getProgramMap(), student, 
						HALLTICKET_PATH, getMostRecentTimetablePeriod(),getExamCenterCenterDetailsMap(true), subjectBookingMap, STUDENT_PHOTOS_PATH,password);
			}else{
				fileName = hallTicketCreator.createHallTicket(timeTableList, subjectsBooked, getProgramMap(), student, 
						HALLTICKET_PATH, getMostRecentTimetablePeriod(),getExamCenterCenterDetailsMap(false), subjectBookingMap, STUDENT_PHOTOS_PATH,password);

			}

			dao.saveHallTicketDownloaded(userDownloadingHallTicket, subjectsBooked);
			// String filePathToBeServed = HALLTICKET_PATH + fileName;
			File fileToDownload = new File(fileName);
			InputStream inputStream = new FileInputStream(fileToDownload);
			response.setContentType("application/pdf");
			response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
			IOUtils.copy(inputStream, response.getOutputStream());
			response.flushBuffer();

		} catch (Exception e) {
			
			MailSender mailSender = (MailSender) act.getBean("mailer");
			mailSender.mailStackTrace("Error in Generating Hall Ticket: " + sapid, e);
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in generating Hall Ticket.");
		}

		return modelnView;

	}

	
//	to be deleted, api shifted to rest controller
//	@RequestMapping(value = "/m/downloadHallTicket", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
//	public ResponseEntity<HashMap<String, String>> downloadHallTicket(@RequestBody StudentBean input) {
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json");
//		HashMap<String, String> response = new HashMap<String, String>();
//		ExamBookingDAO dao = (ExamBookingDAO) act.getBean("examBookingDAO");
//		List<String> blockedSapids = dao.getBlockedSapids();
//		String fileName = "";
//		String userDownloadingHallTicket = input.getSapid(); // Logged in user
//		StudentBean student;
//		student = dao.getSingleStudentsData(input.getSapid());
//		if (blockedSapids.contains(student.getSapid())) {
//			response.put("error", "Your Hall Ticket is on hold. Please contact NGASCE to get access for same");
//			return new ResponseEntity(response, headers, HttpStatus.OK);
//		}
//		boolean isHallTicketAvailable = dao.isConfigurationLive("Hall Ticket Download");
//		if (!isHallTicketAvailable) {
//			response.put("error", "Hall Ticket is not available for download currently");
//			return new ResponseEntity(response, headers, HttpStatus.OK);
//		}
//		try {
//			ArrayList<String> subjects = new ArrayList<>();
//			ExamBookingDAO eDao = (ExamBookingDAO) act.getBean("examBookingDAO");
//			ArrayList<ExamBookingTransactionBean> subjectsBooked = eDao.getConfirmedBooking(student.getSapid());
//			if (subjectsBooked.size() == 0) {
//				response.put("error", "No subjects booked for Exam. Hall Ticket not available.");
//				return new ResponseEntity(response, headers, HttpStatus.OK);
//			}
//			HashMap<String, ExamBookingTransactionBean> subjectBookingMap = new HashMap<>();
//			for (int i = 0; i < subjectsBooked.size(); i++) {
//				ExamBookingTransactionBean bean = subjectsBooked.get(i);
//				subjectBookingMap.put(bean.getSubject(), bean);
//
//			} 
//			List<ExamBookingTransactionBean> passwordPresent = new ArrayList<ExamBookingTransactionBean>();
//
//			List<ExamBookingTransactionBean> passwordAbsent = new ArrayList<ExamBookingTransactionBean>();
//			for (ExamBookingTransactionBean bean : subjectsBooked) {
//				if (StringUtils.isBlank(bean.getPassword())) {
//					passwordAbsent.add(bean);
//				} else {
//					passwordPresent.add(bean);
//				}
//			}
//			String password = "";
//			if (passwordAbsent.size() > 0 && passwordPresent.size() > 0) {
//				password = passwordPresent.get(0).getPassword();
//				String month = passwordPresent.get(0).getMonth();
//				String year = passwordPresent.get(0).getYear();
//				eDao.assignPass(student.getSapid(), password, month, year);
//			}
//
//			if (passwordAbsent.size() > 0 && passwordPresent.size() == 0) {
//				String month = passwordAbsent.get(0).getMonth();
//				String year = passwordAbsent.get(0).getYear();
//				password = generateRandomPass(student.getSapid());
//				eDao.assignPass(student.getSapid(), password, month, year);
//			}
//			if (passwordAbsent.size() == 0 && passwordPresent.size() > 0) {
//				password = passwordPresent.get(0).getPassword();
//			}
//			for (int i = 0; i < subjectsBooked.size(); i++) {
//				subjects.add(subjectsBooked.get(i).getSubject());
//			}
//
//			List<TimetableBean> timeTableList = dao.getTimetableListForGivenSubjects(subjects, student);
//
//			HallTicketPDFCreator hallTicketCreator = new HallTicketPDFCreator();
//			corporateCenterUserMapping = getCorporateCenterUserMapping();
//
//			if(corporateCenterUserMapping.containsKey(student.getSapid())){
//			fileName = hallTicketCreator.createHallTicket(timeTableList, subjectsBooked, getProgramMap(), student, 
//					HALLTICKET_PATH, getMostRecentTimetablePeriod(),getExamCenterCenterDetailsMap(true), subjectBookingMap, STUDENT_PHOTOS_PATH,password);
//		}else{
//			fileName = hallTicketCreator.createHallTicket(timeTableList, subjectsBooked, getProgramMap(), student, 
//					HALLTICKET_PATH, getMostRecentTimetablePeriod(),getExamCenterCenterDetailsMap(false), subjectBookingMap, STUDENT_PHOTOS_PATH,password);
//		}
//		dao.saveHallTicketDownloaded(userDownloadingHallTicket, subjectsBooked);
//		response.put("success", fileName);
//		return new ResponseEntity(response, headers, HttpStatus.OK);
//
//
//		} catch (Exception e) {
//			
//			MailSender mailSender = (MailSender) act.getBean("mailer");
//			mailSender.mailStackTrace("Error in Generating Hall Ticket: " + student.getSapid(), e);
//			response.put("error", "Error in generating Hall Ticket.");
//			return new ResponseEntity(response, headers, HttpStatus.OK);
//			// TODO Auto-generated catch block
//		}
//
//	}

//	@RequestMapping(value = "/m/downloadHallTicketForMbaWxStudent", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
//	public ResponseEntity<HashMap<String, String>> downloadHallTicketForMbaWxStudent(@RequestBody StudentBean input) {
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json");
//		HashMap<String, String> response = new HashMap<String, String>();
//		ExamBookingDAO dao = (ExamBookingDAO) act.getBean("examBookingDAO");
//		List<String> blockedSapids = dao.getBlockedSapids();
//		String fileName = "";
//		String userDownloadingHallTicket = input.getSapid(); // Logged in user
//		StudentBean student;
//		student = dao.getSingleStudentsData(input.getSapid());
//		if (blockedSapids.contains(student.getSapid())) {
//			response.put("error", "Your Hall Ticket is on hold. Please contact NGASCE to get access for same");
//			return new ResponseEntity(response, headers, HttpStatus.OK);
//		}
//		boolean isHallTicketAvailable = dao.isConfigurationLive("Hall Ticket Download");
//		if (!isHallTicketAvailable) {
//			response.put("error", "Hall Ticket is not available for download currently");
//			return new ResponseEntity(response, headers, HttpStatus.OK);
//		}
//		try {
//			ArrayList<String> subjects = new ArrayList<>();
//			ExamBookingDAO eDao = (ExamBookingDAO) act.getBean("examBookingDAO");
//			ArrayList<ExamBookingMBAWX> subjectsBookedMbaBean = eDao.getConfirmedBookingForMBAWx(student.getSapid());
//			
//			if (subjectsBookedMbaBean.size() == 0) {
//				response.put("error", "No subjects booked for Exam. Hall Ticket not available.");
//				return new ResponseEntity(response, headers, HttpStatus.OK);
//			}
//			HashMap<String, ExamBookingMBAWX> subjectBookingMap = new HashMap<>();
//			for (int i = 0; i < subjectsBookedMbaBean.size(); i++) {
//				ExamBookingMBAWX bean = subjectsBookedMbaBean.get(i);
//				subjectBookingMap.put(bean.getTimeboundId(), bean);
//			} 
//			
//			for (int i = 0; i < subjectsBookedMbaBean.size(); i++) {
//				subjects.add(subjectsBookedMbaBean.get(i).getTimeboundId());
//			}
//
//			//List<MbaWxTimeTableBean> timeTableList = dao.getTimetableListForGivenTimeboundIds(subjects, student);
//
//			HallTicketPDFCreator hallTicketCreator = new HallTicketPDFCreator();
//			ExamCenterDAO cDao = (ExamCenterDAO) act.getBean("examCenterDAO");
//			HashMap<String, ExamCenterBean>examcenters = cDao.getExamCenterDetailsMapForMbaWx();
//			
//			ExamBookingMBAWX sbean = subjectsBookedMbaBean.get(0);
//			String mostRecentTimetablePeriod = sbean.getExamMonth()+"-"+sbean.getExamYear();
//			
//			List<TimetableBean> timeTableList = new ArrayList<TimetableBean>();
//			ArrayList<ExamBookingTransactionBean> subjectsBooked = new ArrayList<ExamBookingTransactionBean>();
//			HashMap<String, ExamBookingTransactionBean> subjectBookingMap1 = new HashMap<>();
//			for (int i = 0; i < subjectsBookedMbaBean.size(); i++) {
//				ExamBookingTransactionBean bean =new ExamBookingTransactionBean();
//				bean.setYear(subjectsBookedMbaBean.get(i).getYear());
//				bean.setMonth(subjectsBookedMbaBean.get(i).getMonth());
//				
//				bean.setExamDate(subjectsBookedMbaBean.get(i).getExamDate());
//				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//				//SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//				
//				SimpleDateFormat dateformatter = new SimpleDateFormat("yyyy-MM-dd");
//				SimpleDateFormat timeformatter = new SimpleDateFormat("HH:mm")
//						;
//				Date formattedStartDate = formatter.parse(subjectsBookedMbaBean.get(i).getExamStartDateTime());
//
//				Date formattedEndDateTime = formatter.parse(subjectsBookedMbaBean.get(i).getExamEndDateTime());
//				
//				String startDate = dateformatter.format(formattedStartDate);
//				
//				String startTime = timeformatter.format(formattedStartDate);
//				
//
//				String endTime = timeformatter.format(formattedEndDateTime);
//				
//				
//				/*
//				Date formattedStartTime = timeformatter.parse(subjectsBookedMbaBean.get(i).getExamStartDateTime());
//				Date formattedEndTime = timeformatter.parse(subjectsBookedMbaBean.get(i).getExamEndDateTime());
//				*/
//				bean.setExamDate(startDate+"");
//				bean.setExamTime(startTime+"");
//				bean.setExamEndTime(endTime+""); 
//				bean.setCenterId(subjectsBookedMbaBean.get(i).getCenterId());
//				String subject = eDao.getSubjectByTimeboundId(subjectsBookedMbaBean.get(i).getTimeboundId());
//				bean.setSubject(subject); 
//				subjectsBooked.add(bean);
//			} 
//			String password=""; 
//			fileName = hallTicketCreator.createHallTicket(timeTableList, subjectsBooked, getProgramMap(), student, 
//					HALLTICKET_PATH, mostRecentTimetablePeriod,examcenters, subjectBookingMap1, STUDENT_PHOTOS_PATH,password);
//		
//		response.put("status", "success");  
//		response.put("fileName", fileName.replace("HallTicket", "hallticket"));
//		return new ResponseEntity(response, headers, HttpStatus.OK);
//
//
//		} catch (Exception e) {
//			
//			MailSender mailSender = (MailSender) act.getBean("mailer");
//			mailSender.mailStackTrace("Error in Generating Hall Ticket: " + student.getSapid(), e);
//			response.put("status", "error");
//			response.put("message", "Error in generating Hall Ticket.");
//			return new ResponseEntity(response, headers, HttpStatus.OK);
//			// TODO Auto-generated catch block
//		}
//
//	}
	
	//Commented  by Riya as mapping is shifted in HallTicketStudentController
	
	/*@RequestMapping(value = "/printBookingStatus", method = { RequestMethod.GET, RequestMethod.POST })
	public String printBookingStatus(HttpServletRequest request, HttpServletResponse response,
			RedirectAttributes redirectAttributes) {

		try {

			ExamBookingDAO dao = (ExamBookingDAO) act.getBean("examBookingDAO");
			StudentMarksDAO sDao = (StudentMarksDAO) act.getBean("studentMarksDAO");
			ExamCenterDAO examCenterDao = (ExamCenterDAO) act.getBean("examCenterDAO");
			HashMap<String, String> corporateCenterUserMapping = new HashMap<String, String>();
			HashMap<String, String> getCorporateExamCenterIdNameMap = new HashMap<String, String>();
			corporateCenterUserMapping = examCenterDao.getCorporateCenterUserMapping();
			boolean isCorporate = false;
			String fileName = "";

			String userId = (String) request.getSession().getAttribute("userId");

			if (userId == null) {
				// Link clicked from Support portal by Agent
				userId = request.getParameter("userId");
			}

			// Added this avoid null in reciept download for students ending validity in
			// Oct.
			StudentBean student = (StudentBean) request.getSession().getAttribute("student");
			// StudentBean student = dao.getSingleStudentWithValidity(userId);
			if (corporateCenterUserMapping.containsKey(student.getSapid())) {
				isCorporate = true;
			}
			getCorporateExamCenterIdNameMap = examCenterDao.getCorporateExamCenterIdNameMap();
			List<ExamBookingTransactionBean> examBookings = dao.getConfirmedBooking(userId);
			List<ExamBookingTransactionBean> confirmedOrReleasedExamBookings = dao.getConfirmedOrRelesedBooking(userId);
			if (examBookings == null || examBookings.isEmpty()) {
				setError(request, "No Exam Bookings found for current Exam Cycle");
				/*
				 * redirectAttributes.addFlashAttribute("error", "true");
				 * redirectAttributes.addFlashAttribute("errorMessage",
				 * "No Exam Bookings found current Exam Cycle");
				 * 
				 * String redirectUrl = SERVER_PATH + "studentportal/home";
				 */
			/*	return "examBookingReceipt";

			}
			ExamBookingPDFCreator pdfCreator = new ExamBookingPDFCreator();
			if (isCorporate) {
				fileName = pdfCreator.createPDF(examBookings, getCorporateExamCenterIdNameMap, FEE_RECEIPT_PATH,
						student, confirmedOrReleasedExamBookings);
			} else {
				fileName = pdfCreator.createPDF(examBookings, getExamCenterIdNameMap(), FEE_RECEIPT_PATH, student,
						confirmedOrReleasedExamBookings);
			}

			File fileToDownload = new File(fileName);
			InputStream inputStream = new FileInputStream(fileToDownload);
			response.setContentType("application/pdf");
			response.setHeader("Content-Disposition", "attachment; filename=" + userId + "_exam_Fee_Receipt.pdf");
			IOUtils.copy(inputStream, response.getOutputStream());
			response.flushBuffer();

		} catch (Exception e) {
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in generating Exam Fee Receipt: " + e.getMessage());
			return "examBookingReceipt";
		}
		return null;

	}*/

	@RequestMapping(value = "/admin/downloadHallTicketForm", method = { RequestMethod.GET, RequestMethod.POST })
	public String downloadHallTicketForm(HttpServletRequest request, HttpServletResponse response) {
		return "downloadHallTicket";
	}

	/*
	 * @RequestMapping(value = "/printDDForm", method = {RequestMethod.GET,
	 * RequestMethod.POST}) public void printDDForm(HttpServletRequest request,
	 * HttpServletResponse response) {
	 * 
	 * try{
	 * 
	 * List<ExamBookingTransactionBean> examBookings =
	 * (List<ExamBookingTransactionBean>)request.getSession().getAttribute(
	 * "examBookings"); StudentBean student =
	 * (StudentBean)request.getSession().getAttribute("student");
	 * 
	 * ExamBookingPDFCreator pdfCreator = new ExamBookingPDFCreator();
	 * pdfCreator.createPDF(examBookings, getExamCenterIdNameMap(),
	 * FEE_RECEIPT_PATH, student); //String fileName =
	 * (String)request.getSession().getAttribute("fileName"); String fileName =
	 * "testFile.pdf";
	 * String filePathToBeServed = MARKSHEETS_PATH + fileName; File fileToDownload = new
	 * File(filePathToBeServed); InputStream inputStream = new
	 * FileInputStream(fileToDownload); response.setContentType("application/pdf");
	 * response.setHeader("Content-Disposition", "attachment; filename="+fileName);
	 * IOUtils.copy(inputStream, response.getOutputStream());
	 * response.flushBuffer();
	 * 
	 * }catch(Exception e){
	 *  request.setAttribute("error", "true");
	 * request.setAttribute("errorMessage", "Unable to download file."); } }
	 */
	@RequestMapping(value = "/admin/attendanceSheetForm", method = { RequestMethod.GET, RequestMethod.POST })
	public String attendanceSheetForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		logger.info("Add New Company Page");

		ExamBookingTransactionBean bean = new ExamBookingTransactionBean();
		m.addAttribute("bean", bean);
		m.addAttribute("programList", getProgramList());
		m.addAttribute("offlineCentersList", getOfflineExamCenterMapForDropDown());
		m.addAttribute("onlineCentersList", getOnlineExamCenterMapForDropDown(false));
		m.addAttribute("yearList", yearList);
		m.addAttribute("subjectList", getSubjectList());
		request.getSession().setAttribute("attendanceSearchBean", bean);

		return "attendanceSheet";
	}

	@RequestMapping(value = "/admin/getAttendanceSheet", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView getAttendanceSheet(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute ExamBookingTransactionBean bean) {
		ModelAndView modelnView = new ModelAndView("attendanceSheet");
		request.getSession().setAttribute("attendanceSearchBean", bean);
		modelnView.addObject("bean", bean);
		modelnView.addObject("programList", getProgramList());
		modelnView.addObject("yearList", yearList);
		modelnView.addObject("subjectList", getSubjectList());

		if ("Offline".equals(bean.getExamMode())) {
			bean.setCenterId(bean.getOfflineCenterId());
		} else if ("Online".equals(bean.getExamMode())) {
			bean.setCenterId(bean.getOnlineCenterId());
		}

		modelnView.addObject("offlineCentersList", getOfflineExamCenterMapForDropDown());
		modelnView.addObject("onlineCentersList", getOnlineExamCenterMapForDropDown(false));

		String userId = (String) request.getSession().getAttribute("userId");
		try {
			ExamBookingDAO dao = (ExamBookingDAO) act.getBean("examBookingDAO");
			ArrayList<ExamBookingTransactionBean> bookingsList = dao.getBookingsForAttendanceSheet(bean);

			if (bookingsList == null || bookingsList.size() == 0) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage",
						"No bookings found for given criteria to generate Attendance Sheet.");
				return modelnView;
			}
			HallTicketPDFCreator pdfCreator = new HallTicketPDFCreator();
			String fileName = pdfCreator.createAttendanceSheet(bookingsList, getMostRecentTimetablePeriod(),
					HALLTICKET_PATH, userId, bean);
			request.getSession().setAttribute("attendanceFileName", fileName);

			request.setAttribute("success", "true");
			request.setAttribute("successMessage",
					"Attendance Sheet generated successfully. Please click link below to download.");

		} catch (Exception e) {
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in generating Attendance Sheet.");
			MailSender mailSender = (MailSender) act.getBean("mailer");
			mailSender.mailStackTrace("Error in Generating Attendance Sheet", e);
		}

		return modelnView;
	}

	@RequestMapping(value = "/admin/downloadAttendanceSheet", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView downloadAttendanceSheet(HttpServletRequest request, HttpServletResponse response, Model m) {
		ModelAndView modelnView = new ModelAndView("home");
		String fileName = (String) request.getSession().getAttribute("attendanceFileName");

		try {
			String filePathToBeServed = HALLTICKET_PATH + fileName;
			File fileToDownload = new File(filePathToBeServed);
			InputStream inputStream = new FileInputStream(fileToDownload);
			response.setContentType("application/pdf");
			response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
			IOUtils.copy(inputStream, response.getOutputStream());
			response.flushBuffer();

		} catch (Exception e) {
			

			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in generating Attendance Sheet.");
		}

		return modelnView;

	}

	@RequestMapping(value = "/admin/evaluationSheetForm", method = { RequestMethod.GET, RequestMethod.POST })
	public String evaluationSheetForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		logger.info("Add New Company Page");

		ExamBookingTransactionBean bean = new ExamBookingTransactionBean();
		m.addAttribute("bean", bean);
		m.addAttribute("programList", getProgramList());
		m.addAttribute("offlineCentersList", getOfflineExamCenterMapForDropDown());
		m.addAttribute("onlineCentersList", getOnlineExamCenterMapForDropDown(false));
		m.addAttribute("yearList", yearList);
		m.addAttribute("subjectList", getSubjectList());
		request.getSession().setAttribute("evaluationSearchBean", bean);

		return "evaluationSheet";
	}

	@RequestMapping(value = "/admin/getEvaluationSheet", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView getEvaluationSheet(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute ExamBookingTransactionBean bean) {
		ModelAndView modelnView = new ModelAndView("evaluationSheet");
		request.getSession().setAttribute("evaluationSearchBean", bean);
		modelnView.addObject("bean", bean);
		modelnView.addObject("programList", getProgramList());
		modelnView.addObject("yearList", yearList);
		modelnView.addObject("subjectList", getSubjectList());
		modelnView.addObject("offlineCentersList", getOfflineExamCenterMapForDropDown());
		modelnView.addObject("onlineCentersList", getOnlineExamCenterMapForDropDown(false));

		if ("Offline".equals(bean.getExamMode())) {
			bean.setCenterId(bean.getOfflineCenterId());
		} else if ("Online".equals(bean.getExamMode())) {
			bean.setCenterId(bean.getOnlineCenterId());
		}

		String userId = (String) request.getSession().getAttribute("userId");
		try {
			ExamBookingDAO dao = (ExamBookingDAO) act.getBean("examBookingDAO");
			ArrayList<ExamBookingTransactionBean> bookingsList = dao.getBookingsForAttendanceSheet(bean);
			if (bookingsList == null || bookingsList.size() == 0) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage",
						"No bookings found for given criteria to generate Evaluation Sheet.");
				return modelnView;
			}
			HallTicketPDFCreator pdfCreator = new HallTicketPDFCreator();
			String fileName = pdfCreator.createEvaluationSheet(bookingsList, getMostRecentTimetablePeriod(),
					HALLTICKET_PATH, userId, bean);
			request.getSession().setAttribute("evaluationFileName", fileName);
			request.setAttribute("success", "true");
			request.setAttribute("successMessage",
					"Evaluation Sheet generated successfully. Please click link below to download.");

		} catch (Exception e) {
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in generating Evaluation Sheet.");
			MailSender mailSender = (MailSender) act.getBean("mailer");
			mailSender.mailStackTrace("Error in Generating Evaluation Sheet", e);
		}

		return modelnView;
	}

	@RequestMapping(value = "/admin/massHallTicketDownloadForm", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView massHallTicketDownloadForm(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView modelAndView = new ModelAndView("massHallTicket");
		ExamBookingTransactionBean examBookingBean = new ExamBookingTransactionBean();
		modelAndView.addObject("examBookingBean", examBookingBean);
		modelAndView.addObject("yearList", yearList);

		return modelAndView;

	}

	@RequestMapping(value = "/admin/massHallTicketDownload", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView generateHallTickets(@ModelAttribute ExamBookingTransactionBean examTransactionBean,
			HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute ExamBookingTransactionBean examBookingBean) {
		ModelAndView modelAndView = new ModelAndView("massHallTicket");
		ExamBookingDAO eDao = (ExamBookingDAO) act.getBean("examBookingDAO");

		boolean alreadyDownloadedForSelectedYearAndMonth = false;
		alreadyDownloadedForSelectedYearAndMonth = eDao.alreadyDownloadedDocument(examBookingBean, "Hall Ticket");

		ModelAndView model = new ModelAndView("massFeeReceipt");
		model.addObject("examBookingBean", new ExamBookingTransactionBean());
		model.addObject("yearList", yearList);

		if (alreadyDownloadedForSelectedYearAndMonth) {
			setError(request, "Hall Ticket Already Generated For Selected Year-Month " + examBookingBean.getYear() + "-"
					+ examBookingBean.getMonth());
			return model;
		}

		ArrayList<String> listOfSapIdGeneratingError = new ArrayList<String>();
		ArrayList<ExamBookingTransactionBean> examBookingTransactionList = new ArrayList<ExamBookingTransactionBean>();
		ArrayList<String> listOfDistinctSapid = eDao
				.getConfirmedBookedSapIdListFromMonthAndYear(examBookingBean.getMonth(), examBookingBean.getYear());
		HashMap<String, ArrayList<ExamBookingTransactionBean>> getMapOfSapIdAndConfirmedBookingFromMonthAndYear = eDao
				.getMapOfSapIdAndConfirmedBookingFromMonthAndYear(examTransactionBean);

		for (String sapid : listOfDistinctSapid) {
			ExamBookingTransactionBean examBookingTransactionBean = new ExamBookingTransactionBean();
			ArrayList<ExamBookingTransactionBean> bookedList = getMapOfSapIdAndConfirmedBookingFromMonthAndYear
					.get(sapid);

			String fileName = createHallTicketAndReturnFileName(bookedList, sapid);

			if (!fileName.startsWith("Error")) {
				examBookingTransactionBean.setYear(examTransactionBean.getYear());
				examBookingTransactionBean.setMonth(examTransactionBean.getMonth());
				examBookingTransactionBean.setSapid(sapid);
				examBookingTransactionBean.setFilePath(fileName);
				examBookingTransactionList.add(examBookingTransactionBean);
			}

		}
		eDao.batchInsertOfDocumentRecords(examBookingTransactionList, "Hall Ticket");
		modelAndView.addObject("examBookingBean", examBookingBean);
		modelAndView.addObject("yearList", yearList);
		modelAndView.addObject("listOfSapIdGeneratingError", listOfSapIdGeneratingError);
		modelAndView.addObject("rowCount", listOfSapIdGeneratingError.size());
		return modelAndView;
	}

	public String createHallTicketAndReturnFileName(ArrayList<ExamBookingTransactionBean> subjectsBooked,
			String sapid) {
		ArrayList<String> subjects = new ArrayList<>();
		String fileName = "", year = "", month = "";
		ExamBookingDAO eDao = (ExamBookingDAO) act.getBean("examBookingDAO");
		StudentExamBean student = eDao.getSingleStudentWithValidity(sapid);
		HashMap<String, ExamBookingTransactionBean> subjectBookingMap = new HashMap<>();

		for (int i = 0; i < subjectsBooked.size(); i++) {
			ExamBookingTransactionBean bean = subjectsBooked.get(i);
			subjectBookingMap.put(bean.getSubject(), bean);
		}

		if (subjectsBooked != null && subjectsBooked.size() > 0) {
			year = subjectsBooked.get(0).getYear();
			month = subjectsBooked.get(0).getMonth();
		}
		for (int i = 0; i < subjectsBooked.size(); i++) {
			subjects.add(subjectsBooked.get(i).getSubject());
		}
		List<ExamBookingTransactionBean> passwordPresent = new ArrayList<ExamBookingTransactionBean>();
		List<ExamBookingTransactionBean> passwordAbsent = new ArrayList<ExamBookingTransactionBean>();
		for (ExamBookingTransactionBean bean : subjectsBooked) {
			if (StringUtils.isBlank(bean.getPassword())) {
				passwordAbsent.add(bean);
			} else {
				passwordPresent.add(bean);
			}
		}
		String password = "";
		if (passwordAbsent.size() > 0 && passwordPresent.size() > 0) {
			password = passwordPresent.get(0).getPassword();
			eDao.assignPass(sapid, password, month, year);
		}

		if (passwordAbsent.size() > 0 && passwordPresent.size() == 0) {
			password = generateRandomPass(sapid);
			eDao.assignPass(sapid, password, month, year);
		}

		if (passwordAbsent.size() == 0 && passwordPresent.size() > 0) {
			password = passwordPresent.get(0).getPassword();
		}

		
		ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");

		corporateCenterUserMapping = getCorporateCenterUserMapping();

		List<TimetableBean> timeTableList = dao.getTimetableListForGivenSubjects(subjects,student);
		try{
			if(corporateCenterUserMapping.containsKey(student.getSapid())){
				fileName = hallTicketCreator.createHallTicket(timeTableList, subjectsBooked, getProgramMap(), student, 
						HALLTICKET_PATH, getMostRecentTimetablePeriod(),getExamCenterCenterDetailsMap(true), subjectBookingMap, STUDENT_PHOTOS_PATH,password);
			}else{
				fileName = hallTicketCreator.createHallTicket(timeTableList, subjectsBooked, getProgramMap(), student, 
						HALLTICKET_PATH, getMostRecentTimetablePeriod(),getExamCenterCenterDetailsMap(false), subjectBookingMap, STUDENT_PHOTOS_PATH,password);

			}

			return fileName;

		} catch (Exception e) {
			
			return "Error" + student.getSapid();

		}

	}

	@RequestMapping(value = "/admin/downloadEvaluationSheet", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView downloadEvaluationSheet(HttpServletRequest request, HttpServletResponse response, Model m) {
		ModelAndView modelnView = new ModelAndView("home");
		String fileName = (String) request.getSession().getAttribute("evaluationFileName");

		try {
			String filePathToBeServed = HALLTICKET_PATH + fileName;
			File fileToDownload = new File(filePathToBeServed);
			InputStream inputStream = new FileInputStream(fileToDownload);
			response.setContentType("application/pdf");
			response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
			IOUtils.copy(inputStream, response.getOutputStream());
			response.flushBuffer();

		} catch (Exception e) {
			

			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in generating Evaluation Sheet.");
		}

		return modelnView;

	}

	@RequestMapping(value = "/admin/markCheckSheetForm", method = { RequestMethod.GET, RequestMethod.POST })
	public String markCheckSheetForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		logger.info("Add New Company Page");

		ExamBookingTransactionBean bean = new ExamBookingTransactionBean();
		m.addAttribute("bean", bean);
		m.addAttribute("programList", getProgramList());
		m.addAttribute("offlineCentersList", getOfflineExamCenterMapForDropDown());
		m.addAttribute("onlineCentersList", getOnlineExamCenterMapForDropDown(false));
		m.addAttribute("yearList", yearList);
		m.addAttribute("subjectList", getSubjectList());
		request.getSession().setAttribute("evaluationSearchBean", bean);

		return "markCheckSheet";
	}

	@RequestMapping(value = "/admin/getMarkCheckSheet", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView getMarkCheckSheet(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute ExamBookingTransactionBean bean) {
		ModelAndView modelnView = new ModelAndView("markCheckSheet");
		request.getSession().setAttribute("evaluationSearchBean", bean);
		modelnView.addObject("bean", bean);
		modelnView.addObject("programList", getProgramList());
		modelnView.addObject("yearList", yearList);
		modelnView.addObject("subjectList", getSubjectList());
		modelnView.addObject("offlineCentersList", getOfflineExamCenterMapForDropDown());
		modelnView.addObject("onlineCentersList", getOnlineExamCenterMapForDropDown(false));

		if ("Offline".equals(bean.getExamMode())) {
			bean.setCenterId(bean.getOfflineCenterId());
		} else if ("Online".equals(bean.getExamMode())) {
			bean.setCenterId(bean.getOnlineCenterId());
		}
		String userId = (String) request.getSession().getAttribute("userId");
		try {
			ExamBookingDAO dao = (ExamBookingDAO) act.getBean("examBookingDAO");
			ArrayList<ExamBookingTransactionBean> marksList = dao.getMarksCheckingSheet(bean);

			if (marksList == null || marksList.size() == 0) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage",
						"No record found for given criteria to generate Mark Check Sheet.");
				return modelnView;
			}
			HallTicketPDFCreator pdfCreator = new HallTicketPDFCreator();
			String fileName = pdfCreator.createMarksCheckingSheet(marksList, getMostRecentTimetablePeriod(),
					HALLTICKET_PATH, userId, bean);
			request.getSession().setAttribute("MarksChekingSheetFileName", fileName);
			request.setAttribute("success", "true");
			request.setAttribute("successMessage",
					"Marks Cheking Sheet generated successfully. Please click link below to download.");

		} catch (Exception e) {
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in generating Marks Cheking Sheet.");
			MailSender mailSender = (MailSender) act.getBean("mailer");
			mailSender.mailStackTrace("Error in Generating Marks Cheking Sheet", e);
		}

		return modelnView;
	}

	@RequestMapping(value = "/admin/downloadMarkCheckSheet", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView downloadMarkCheckSheet(HttpServletRequest request, HttpServletResponse response, Model m) {
		ModelAndView modelnView = new ModelAndView("home");
		String fileName = (String) request.getSession().getAttribute("MarksChekingSheetFileName");

		try {
			String filePathToBeServed = HALLTICKET_PATH + fileName;
			File fileToDownload = new File(filePathToBeServed);
			InputStream inputStream = new FileInputStream(fileToDownload);
			response.setContentType("application/pdf");
			response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
			IOUtils.copy(inputStream, response.getOutputStream());
			response.flushBuffer();

		} catch (Exception e) {
			

			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in generating Marks Cheking Sheet.");
		}

		return modelnView;

	}

	
	public String generateRandomPass(String sapid) {
		String generatedString =null;

		try {
			int randomNum = ThreadLocalRandom.current().nextInt(10, 99 + 1);
			String FirstString = String.valueOf(randomNum);
			String SecondString = RandomStringUtils.randomNumeric(8);
			generatedString = FirstString + SecondString;
		} catch (Exception e) {
			
			return generatedString;
		}
	
	return generatedString;
	}
	@RequestMapping(value = "/m/getFeeReceiptsMBAWX", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public ResponseEntity<ResponseBean> getFeeReceiptsMBAWX(@RequestBody StudentExamBean input) throws Exception {
	 			
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		ExamBookingDAO dao = (ExamBookingDAO) act.getBean("examBookingDAO");
		ArrayList<ExamBookingTransactionBean> listOfExamBookingFeeReceiptsBasedOnSapid = dao
				.listOfDocumentsBasedOnSapidAndDocType(input.getSapid(), "MBAWX Exam Booking Fee Receipt");
		ArrayList<ExamBookingTransactionBean> listOfSRFeeReceiptsBasedOnSapid = dao
				.listOfDocumentsBasedOnSapidAndDocType(input.getSapid(), "SR Fee Receipt");
		ResponseBean response = new  ResponseBean();
		ArrayList<ExamBookingTransactionBean> examReceipt = new ArrayList<ExamBookingTransactionBean>();
		ArrayList<ExamBookingTransactionBean> srReceipts = new ArrayList<ExamBookingTransactionBean>();
		for(ExamBookingTransactionBean srReceipt : listOfSRFeeReceiptsBasedOnSapid) {
			
			ServiceRequestBean sr = sDao.checkIfSrPaymentSuccessful(srReceipt.getReferenceId()+"");
			if(sr != null) { 
				srReceipt.setServiceRequestType(sr.getServiceRequestType());
				srReceipts.add(srReceipt);
			} 
		}
		response.setListOfExamBookingFeeReceiptsBasedOnSapid(examReceipt);
		response.setListOfSRFeeReceiptsBasedOnSapid(srReceipts);
		
		return new ResponseEntity<ResponseBean>(response, headers, HttpStatus.OK);
			 
	}
}
