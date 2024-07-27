package com.nmims.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import com.google.gson.JsonObject;
import com.nmims.beans.AdhocPaymentStudentPortalBean;
import com.nmims.beans.AssignmentStudentPortalFileBean;
import com.nmims.beans.ConfigurationStudentPortal;
import com.nmims.beans.ConsumerProgramStructureStudentPortal;
import com.nmims.beans.FeedbackBean;
import com.nmims.beans.MassUploadTrackingSRBean;
import com.nmims.beans.PageStudentPortal;
import com.nmims.beans.PassFailBean;
import com.nmims.beans.PaymentOptionsStudentPortalBean;
import com.nmims.beans.ProgramsStudentPortalBean;
import com.nmims.beans.RazorpayTransactionBean;
import com.nmims.beans.ResponseStudentPortalBean;
import com.nmims.beans.ServiceRequestDocumentBean;
import com.nmims.beans.ServiceRequestStudentPortal;
import com.nmims.beans.ServiceRequestType;
import com.nmims.beans.SrTypeMasterKeyMapping;
import com.nmims.beans.StudentMarksBean;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.beans.TransactionsBean;
import com.nmims.beans.UserAuthorizationStudentPortalBean;
import com.nmims.beans.WalletBean;
import com.nmims.daos.GatewayTransactionsDAO;
import com.nmims.daos.MassUploadTrackingSRDAO;
import com.nmims.daos.ServiceRequestDao;
import com.nmims.daos.WalletDAO;
import com.nmims.dto.ChangeDetailsSRDto;
import com.nmims.enums.ServiceRequestTypeEnum;
import com.nmims.factory.CertificateFactory;
import com.nmims.factory.ExitSrApplicableFactory;
import com.nmims.factory.FeeReceiptFactory;
import com.nmims.helpers.CreatePDF;
import com.nmims.helpers.LinkedInManager;
import com.nmims.helpers.MailSender;
import com.nmims.helpers.PaymentHelper;
import com.nmims.helpers.PersonStudentPortalBean;
import com.nmims.helpers.SalesforceHelper;
import com.nmims.helpers.XMLParser;
import com.nmims.interfaces.FeeReceiptInterface;
import com.nmims.services.CustomCourseWaiverServiceImpl;
import com.nmims.services.ServiceRequestService;
import com.nmims.views.AdHocPaymentExcelView;
import com.nmims.views.ServiceRequestExcelView;

/**
 * Handles requests for the application home page.
 */

@Controller
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ServiceRequestController extends BaseController {
	

	
	@Autowired
	ApplicationContext act;
	
	@Autowired
	ServiceRequestExcelView serviceRequestExcelView;
	
	@Autowired
	SalesforceHelper salesforceHelper;
	@Autowired
	PaymentHelper paymentHelper;
	
	@Autowired
	HomeController homeController;
	
	@Autowired
	CertificateFactory certificateFactory;
	 
	@Autowired  
	CreatePDF pdfHelper;
	
	@Autowired
	FeeReceiptFactory feeReceiptFactory ;
	  
	@Autowired
	LinkedInManager linkedInManager;  

	@Autowired
	AdHocPaymentExcelView adHocPyamentsExcelView; 
	
	@Autowired
	ServiceRequestService serviceRequest; 
	
	@Autowired
	MassUploadTrackingSRDAO massUploadTrackingSRDAO;
	
	@Autowired
	ExitSrApplicableFactory  exitSrApplicableFactory;
	
	@Autowired
	CustomCourseWaiverServiceImpl customCourseWaiverServiceImpl;

    @Value("${FEE_RECEIPT_PATH}") 
	private String FEE_RECEIPT_PATH;
    
    @Value("${GATEWAY_REFUND_URL}")
    private String GATEWAY_REFUND_URL;
    
    @Value("${GATEWAY_TRANSACTION_STATUS}")
    private String GATEWAY_TRANSACTION_STATUS;

	@Value("${CERTIFICATES_PATH}")
	private String CERTIFICATES_PATH;
	
	@Value("${ICLCRESTRICTED_USER_LIST}")
	private List<String> ICLCRESTRICTED_USER_LIST;
	
	@Value("${REMARK_GRADES_PROGRAMS}")
	private List<String> REMARK_GRADES_PROGRAMS;
	
	private static final Logger logger = LoggerFactory.getLogger(ServiceRequestController.class);
	private final int pageSize = 10;
	private static final int BUFFER_SIZE = 4096;
	private static final Logger adhocPaymentsLogger = LoggerFactory.getLogger("adhoc_payments");

	private static final Logger razorpayLogger = LoggerFactory.getLogger("razorpay_payments");
	
	private static final Logger PAYMENTS_LOGGER = LoggerFactory.getLogger("gateway_payments");
	
	private final String GATEWAY_STATUS_FAILED = "Payment Failed";
	private final String GATEWAY_STATUS_SUCCESSFUL = "Payment Successfull";
	private final String ADHOC_STATUS_SUCCESSFUL = "Payment Successful";
	private final String WEBHOOK_API = "Webhook API";
	

	private static final String HttpServletRequest = null;
	@Autowired
	private ServiceRequestDao serviceRequestDao;
	
	@Autowired GatewayTransactionsDAO gatewayTransactionsDAO;
	
	@Autowired
	ServiceRequestService servReqServ;
	@Value("${SECURE_SECRET}")
	private String SECURE_SECRET; // secret key;
	@Value("${ACCOUNT_ID}")
	private String ACCOUNT_ID;
	@Value("${V3URL}") 
	private String V3URL;
	@Value("${SR_RETURN_URL}")
	private String SR_RETURN_URL;
	@Value("${SR_RETURN_URL_MOBILE}")
	private String SR_RETURN_URL_MOBILE;
	@Value("${ADHOC_PAYMENT_RETURN_URL}") // AdhocpaymentReturn Url
	private String ADHOC_PAYMENT_RETURN_URL;
	@Value("${SERVER_PATH}")
	private String SERVER_PATH;
	@Value("${CURRENT_ACAD_MONTH}")
	private String CURRENT_ACAD_MONTH;
	@Value("${CURRENT_ACAD_YEAR}")
	private String CURRENT_ACAD_YEAR;
	
	
	@Value("${PENDING_PAYMENT_RETURN_URL}") // Pending payment URL
	private String PENDING_PAYMENT_RETURN_URL;
	
	
	@Value("${SERVICE_TAX_RULE}")
	private String SERVICE_TAX_RULE; 

	
	@Value("${SERVICE_REQUEST_FILES_PATH}")
	private String SERVICE_REQUEST_FILES_PATH;
	
	private final int REVALUATION_FEE_PER_SUBJECT = 1000;
	private final int DUPLICATE_CERTIFICATE_FEE = 1000;
	private final int PHOTOCOPY_FEE_PER_SUBJECT = 500;
	private final int SECOND_MARKSHEET_FEE_PER_SUBJECT = 500;
	private final int SECOND_DIPLOMA_FEE = 1000;
	private final int TRANSCRIPT_FEE = 1000;
	private final int EXTRA_TRANSCRIPT_FEE = 300;
	private final long MAX_FILE_SIZE = 5242880;
	
	//flag to check data inserted into serviceRequest or not
	private boolean ServiceRequestFlag = false;

	ArrayList<String> requestTypes = new ArrayList<>();

	private ArrayList<String> paymentTypeList =new ArrayList<String>(
			Arrays.asList("Exam Registration","PCP Booking","Service Request"));
	
	private ArrayList<String> refundPaymentTypeList =new ArrayList<String>(
			Arrays.asList("Exam Registration","Service Request","PCP Booking","Assignment Fees", "Exam Registration MBA - WX", "Exam Registration MBA - X","MSc AI&ML Ops Program Re-Exam"));
	
	private ArrayList<String> yearList = new ArrayList<String>(
			Arrays.asList("2008", "2009", "2010", "2011", "2012", "2013", "2014", "2015", "2016", "2017", "2018", "2019","2020","2021","2022","2023"));

	
	private String[] WX_MASTERKEYS = {"111","151"};
	
	private String[] Msci_MASTERKEYS = {"131","154","155","156","157","158"};
	
	private String generateCommaSeparatedList(String sapIdList) {
		String commaSeparatedList = sapIdList.replaceAll("(\\r|\\n|\\r\\n)+",
				",");
		if (commaSeparatedList.endsWith(",")) {
			commaSeparatedList = commaSeparatedList.substring(0,
					commaSeparatedList.length() - 1);
		}
		
		
		return commaSeparatedList;
	}	
	
	  @ModelAttribute("requestTypes")
      public ArrayList<String> getRequestTypes(HttpServletRequest request,String sapid) {
               //String sapid = (String) request.getSession().getAttribute("userId"); 
		  		// don't use session inside theses function,because mobile app using these function
               this.requestTypes = serviceRequestDao.getActiveSRTypes();
               this.requestTypes.addAll(serviceRequestDao.getSRTypesForExtendedTimeStudents(sapid)); //added on 6/2/2018 to allow students to raise SR after last date.
               StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(sapid);
               if (student != null) {
                         if("Jul2017".equals(student.getPrgmStructApplicable())){
                                  requestTypes.remove(ServiceRequestStudentPortal.TEE_REVALUATION);
                                  requestTypes.remove(ServiceRequestStudentPortal.OFFLINE_TEE_REVALUATION);
                                  requestTypes.remove(ServiceRequestStudentPortal.PHOTOCOPY_OF_ANSWERBOOK);
                         } else if (Arrays.asList("ACBM", "ADSCM", "CBM", "CCC", "CDM", "CPBM").contains(student.getProgram())) {
                             requestTypes.remove(ServiceRequestStudentPortal.TEE_REVALUATION);
                             requestTypes.remove(ServiceRequestStudentPortal.OFFLINE_TEE_REVALUATION);
                             requestTypes.remove(ServiceRequestStudentPortal.PHOTOCOPY_OF_ANSWERBOOK);
                         } else if ("Online".equals(student.getExamMode())) {
                                  //requestTypes.remove(ServiceRequest.TEE_REVALUATION);
                                   requestTypes.remove(ServiceRequestStudentPortal.OFFLINE_TEE_REVALUATION);
                                   requestTypes.remove(ServiceRequestStudentPortal.PHOTOCOPY_OF_ANSWERBOOK);
                                   if("EPBM".equalsIgnoreCase(student.getProgram()) || "MPDV".equalsIgnoreCase(student.getProgram())) {

                                       requestTypes.remove(ServiceRequestStudentPortal.ISSUEANCE_OF_CERTIFICATE);
                                       requestTypes.remove(ServiceRequestStudentPortal.ISSUEANCE_OF_MARKSHEET);
                                       requestTypes.remove(ServiceRequestStudentPortal.ISSUEANCE_OF_TRANSCRIPT);
                                   }
                         } 
                         else
                         {         requestTypes.remove(ServiceRequestStudentPortal.TEE_REVALUATION);
                                  // requestTypes.remove(ServiceRequest.ASSIGNMENT_REVALUATION);
                         }
               }
               
               return this.requestTypes;
      }

	
	
	public ArrayList<String> getAllRequestTypes() {
		// if(this.requestTypes.size() == 0){
		this.requestTypes = serviceRequestDao.getAllActiveSRTypes();
		// }
		return this.requestTypes;
	}
	
	public ArrayList<String> getAllAtiveRequestTypes() {
		// if(this.requestTypes.size() == 0){
		this.requestTypes = serviceRequestDao.getAllSRTypes();
		// }
		return this.requestTypes;
	}

	@RequestMapping(value = "/student/viewFeeReceipt", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView viewFeeReceipt(HttpServletRequest request, HttpServletResponse response, Model m) {

		ModelAndView modelAndView = new ModelAndView("jsp/serviceRequest/viewRegistrationFeeReceipt");
		StudentStudentPortalBean bean = (StudentStudentPortalBean) request.getSession().getAttribute("student_studentportal");
		ArrayList<StudentStudentPortalBean> listOfPaymentsMade = salesforceHelper.listOfPaymentsMade(bean.getSapid());

		if (listOfPaymentsMade.size() > 0 && listOfPaymentsMade != null) {
			m.addAttribute("listOfPaymentsMade", listOfPaymentsMade);
		} else {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No Fee Receipts to be dipslayed");
		}

		m.addAttribute("sapid", bean.getSapid());

		return modelAndView;
	}

	@RequestMapping(value = "/admin/changeConfigurationForm", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView makeResultsLiveForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		ModelAndView modelnView = new ModelAndView("jsp/serviceRequest/changeConfiguration");
		List<ConfigurationStudentPortal> currentConfList = serviceRequestDao.getCurrentConfigurationList();
		modelnView.addObject("currentConfList", currentConfList);

		ConfigurationStudentPortal configuration = new ConfigurationStudentPortal();
		m.addAttribute("configuration", configuration);
		m.addAttribute("allRequestTypes", getAllAtiveRequestTypes());
		return modelnView;
	}

	@RequestMapping(value = "/admin/changeConfiguration", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView changeConfiguration(HttpServletRequest request, HttpServletResponse respnse,
			@ModelAttribute ConfigurationStudentPortal configuration) {
		logger.info("Make results live Page");
		ModelAndView modelnView = new ModelAndView("jsp/serviceRequest/changeConfiguration");
		try {
			configuration.setLastModifiedBy((String) request.getSession().getAttribute("userId"));
			serviceRequestDao.updateConfiguration(configuration);
			request.setAttribute("success", "true");
			request.setAttribute("successMessage",
					"Date/Time changed successfully for " + configuration.getServiceRequestName());

		} catch (Exception e) {
//			e.printStackTrace();
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in changing configuration.");
		}

		List<ConfigurationStudentPortal> currentConfList = serviceRequestDao.getCurrentConfigurationList();
		modelnView.addObject("currentConfList", currentConfList);
		modelnView.addObject("configuration", configuration);
		modelnView.addObject("allRequestTypes", getAllRequestTypes());
		return modelnView;
	}

	@RequestMapping(value = "/admin/searchSRForm", method = { RequestMethod.GET, RequestMethod.POST })
	public String searchSRForm(HttpServletRequest request, HttpServletResponse response, Model m) {

		if (!checkSession(request, response)) {
			return "jsp/login";
		}
		
		ServiceRequestStudentPortal sr = new ServiceRequestStudentPortal();
		m.addAttribute("sr", sr);
		m.addAttribute("allRequestTypes", getAllAtiveRequestTypes());
		return "jsp/serviceRequest/searchSR";
	}
	@RequestMapping(value = "/admin/searchSRHistoryForm", method = { RequestMethod.GET, RequestMethod.POST })
	public String searchSRHistoryForm(HttpServletRequest request, HttpServletResponse response, Model m) {

		if (!checkSession(request, response)) {
			return "jsp/login";
		}
		
		ServiceRequestStudentPortal sr = new ServiceRequestStudentPortal();
		m.addAttribute("sr", sr);
		return "jsp/serviceRequest/searchSRHistory";
	}
	@RequestMapping(value="/admin/searchSRHistory",method={RequestMethod.POST})
	public ModelAndView searchSRHistory(@ModelAttribute ServiceRequestStudentPortal sr,HttpServletRequest request, HttpServletResponse response){
		if (!checkSession(request, response)) {
			return new ModelAndView("jsp/login");
		}
		ModelAndView modelAndView = new ModelAndView("jsp/serviceRequest/searchSRHistory");
		ArrayList<ServiceRequestStudentPortal> getServiceRequestHistoryList = serviceRequestDao.getServiceRequestHistoryList(sr, getAuthorizedCodes(request));
		modelAndView.addObject("rowCount", getServiceRequestHistoryList.size());
		
		if (getServiceRequestHistoryList == null || getServiceRequestHistoryList.size() == 0) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No Records found.");
		}
		modelAndView.addObject("srList", getServiceRequestHistoryList);
		modelAndView.addObject("sr", sr);
		return modelAndView;
	}
	@RequestMapping(value = "/admin/searchSR", method = { RequestMethod.POST })
	public ModelAndView searchSR(@ModelAttribute ServiceRequestStudentPortal sr, HttpServletRequest request,
			HttpServletResponse response) {
		if (!checkSession(request, response)) {
			return new ModelAndView("jsp/login");
		}
		UserAuthorizationStudentPortalBean userAuthorization = (UserAuthorizationStudentPortalBean) request.getSession()
				.getAttribute("userAuthorization_studentportal");
		String roles = "";
		if (userAuthorization != null) {
			roles = (userAuthorization.getRoles() != null && !"".equals(userAuthorization.getRoles()))
					? userAuthorization.getRoles()
					: roles;
		}
		request.getSession().setAttribute("roles", roles);

		ModelAndView modelnView = new ModelAndView("jsp/serviceRequest/searchSR");
		request.getSession().setAttribute("searchBean", sr);
		HashMap<String,String> mapOfActiveSRTypesAndTAT = serviceRequestDao.mapOfActiveSRTypesAndTAT();
		PageStudentPortal<ServiceRequestStudentPortal> page = serviceRequestDao.getServiceRequestPage(1, pageSize, sr,
				getAuthorizedCodes(request));
		
		List<ServiceRequestStudentPortal> srList = page.getPageItems();

		modelnView.addObject("page", page);
		modelnView.addObject("rowCount", page.getRowCount());
		if (srList == null || srList.size() == 0) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No Records found.");
		}
		List<Long> srIds = new ArrayList<Long>();
		Map<Long, MassUploadTrackingSRBean> trackingMailStatus = new HashMap<Long, MassUploadTrackingSRBean>();
		srIds=srList.stream().map(list->list.getId()).collect(Collectors.toList());
		
		if(srIds.size()>0)
			trackingMailStatus = massUploadTrackingSRDAO.getTrackingMailStatus(srIds);
		
		modelnView.addObject("srList", srList);
		modelnView.addObject("sr", sr);
		modelnView.addObject("trackingMailStatus", trackingMailStatus);
		modelnView.addObject("mapOfActiveSRTypesAndTAT", mapOfActiveSRTypesAndTAT);
		modelnView.addObject("allRequestTypes", getAllAtiveRequestTypes());
		return modelnView;
	}

	@RequestMapping(value = "/student/getDispatches", method = { RequestMethod.GET, RequestMethod.POST })
	public String getDispatches(HttpServletRequest request, HttpServletResponse response, Model m) {
		StudentStudentPortalBean student = (StudentStudentPortalBean) request.getSession().getAttribute("student_studentportal");
		HashMap<String, String> mapOfDispatchParameters = salesforceHelper
				.getMapOfDispatchParameterAndValues(student.getSapid());
		if (mapOfDispatchParameters.isEmpty()) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No Dispatches Made.");

		}
		m.addAttribute("dispatchOrders", mapOfDispatchParameters);
		return "jsp/serviceRequest/dispatchOrders";

	}
	
	
	@RequestMapping(value="/admin/downloadAdHocRefundPaymentReport",method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView downloadAdHocRefundPaymentReport(HttpServletRequest request, HttpServletResponse response){
		ArrayList<AdhocPaymentStudentPortalBean> listOfAdhocRefundPaymentsMade  = serviceRequestDao.listOfAdhocRefundPaymentsMade();
		
		return new ModelAndView("jsp/adHocRefundPaymentsExcelView","listOfAdhocRefundPaymentsMade",listOfAdhocRefundPaymentsMade);
	}
	
	@RequestMapping(value="/admin/downloadAdHocPaymentReportForm",method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView downloadAdHocPaymentReportForm(HttpServletRequest request, HttpServletResponse response){
		ModelAndView mv = new ModelAndView("jsp/downloadAdHocPaymentReportForm");
		return mv;
	}
	
	@RequestMapping(value="/admin/downloadAdHocPaymentFilterReport",method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView downloadAdHocPaymentFilterReport(HttpServletRequest request, HttpServletResponse response){
		if (!checkSession(request, response)) {
			return new ModelAndView("jsp/studentPortalRediret");
			
		}
		String trackId = request.getParameter("trackId");
		String dateRange = request.getParameter("daterange");
		String filterType = request.getParameter("filterType");
		ArrayList<AdhocPaymentStudentPortalBean> listOfAdhocPaymentsMade = new ArrayList<AdhocPaymentStudentPortalBean>();
		if("trackId".equalsIgnoreCase(filterType) && trackId == null) {
			//error 
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "TrackId cannot be empty");
			return new ModelAndView("jsp/downloadAdHocPaymentReportForm");
		}
		if("daterange".equalsIgnoreCase(filterType) && dateRange == null) {
			//error
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "DateRange cannot be empty");
			return new ModelAndView("jsp/downloadAdHocPaymentReportForm");
		}
		if("trackId".equalsIgnoreCase(filterType)) {
			List<String> trackIds = new ArrayList<String>(Arrays.asList(trackId.split(",")));
			String trackId_tmp = "";
			for (String string : trackIds) {
				if("".equalsIgnoreCase(trackId_tmp)) {
					trackId_tmp = "'" + string + "'";
					continue;
				}
				trackId_tmp = trackId_tmp + ",'" + string + "'";
			}
			listOfAdhocPaymentsMade  = serviceRequestDao.listOfAdhocPaymentsMadeByTrackId(trackId_tmp);
		}else if("daterange".equalsIgnoreCase(filterType)) {
			//String[] dates = dateRange.split("-", 1);
			try {
				request.getSession().setAttribute("ICLCRESTRICTED_USER_LIST", ICLCRESTRICTED_USER_LIST) ;
				String startDateTmp = dateRange.substring(0,10);
				String endTmp = dateRange.substring(13,23);
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
				Date startDate = dateFormat.parse(startDateTmp);
				Date endDate = dateFormat.parse(endTmp);
				SimpleDateFormat output = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				startDateTmp = output.format(startDate);
				endTmp = output.format(endDate);
				listOfAdhocPaymentsMade  = serviceRequestDao.listOfAdhocPaymentsMadeByDateRange(startDateTmp, endTmp);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Failed to parse Date param");
				return new ModelAndView("jsp/downloadAdHocPaymentReportForm");
			}
		}else {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Invalid filter type found");
			return new ModelAndView("jsp/downloadAdHocPaymentReportForm");
		} 	
		if(listOfAdhocPaymentsMade.size() == 0) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No Transaction Success record found");
			return new ModelAndView("jsp/downloadAdHocPaymentReportForm");
		}
		return new ModelAndView(adHocPyamentsExcelView, "listOfAdhocPaymentsMade",listOfAdhocPaymentsMade);
	}
	
	@RequestMapping(value="/admin/downloadAdHocPaymentReport",method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView downloadAdHocPaymentReport(HttpServletRequest request, HttpServletResponse response){
		
		if (!checkSession(request, response)) {
			return new ModelAndView("jsp/studentPortalRediret");
			
		}
		
		request.getSession().setAttribute("ICLCRESTRICTED_USER_LIST", ICLCRESTRICTED_USER_LIST);
		ArrayList<AdhocPaymentStudentPortalBean> listOfAdhocPaymentsMade  = serviceRequestDao.listOfAdhocPaymentsMade();
		
		return new ModelAndView(adHocPyamentsExcelView, "listOfAdhocPaymentsMade",listOfAdhocPaymentsMade);
	}
	
	@RequestMapping(value="/student/adhocPaymentForm", method={RequestMethod.GET , RequestMethod.POST})
	public ModelAndView adhocPaymentForm(HttpServletRequest request, HttpServletResponse response)
	{
		if("http://localhost:8080/".equals(SERVER_PATH)) {
			response.setHeader("Set-Cookie", "SESSION=" + request.getSession().getId() + "; Path=/studentportal/; HttpOnly; SameSite=none; Secure");
		} else {
			response.setHeader("Set-Cookie", "SESSION=" + request.getSession().getId() + "; Path=/studentportal/; HttpOnly; SameSite=none; Secure");
		}
		adhocPaymentsLogger.info("ServiceRequestController.adhocPaymentForm() - START");
		ModelAndView modelnView = new ModelAndView("jsp/adhocPaymentForm");
		AdhocPaymentStudentPortalBean adhocPaymentBean =new AdhocPaymentStudentPortalBean();
		modelnView.addObject("paymentType",paymentTypeList);
		modelnView.addObject("yearList",yearList);
		modelnView.addObject("adhocPaymentBean",adhocPaymentBean);
		adhocPaymentsLogger.info("ServiceRequestController.adhocPaymentForm() - END");
		return modelnView;
	}
    
	// method to handle adhoc payment request 
	@RequestMapping(value="/student/saveAdhocPaymentRequest", method={RequestMethod.GET , RequestMethod.POST})
	public ModelAndView saveAdhocPaymentRequest(HttpServletRequest request, HttpServletResponse response,@ModelAttribute AdhocPaymentStudentPortalBean adhocPaymentBean,Model model)
	{
		adhocPaymentsLogger.info("ServiceRequestController.saveAdhocPaymentRequest() - START");
		String userId = (String) request.getSession().getAttribute("userId");
		adhocPaymentBean.setTranStatus(ServiceRequestStudentPortal.TRAN_STATUS_INITIATED);
		adhocPaymentBean.setRequestStatus(ServiceRequestStudentPortal.REQUEST_STATUS_SUBMITTED);
		adhocPaymentBean.setCreatedBy(userId);
		adhocPaymentBean.setLastModifiedBy(userId);
		StudentStudentPortalBean student =new StudentStudentPortalBean();
		student.setEmailId(adhocPaymentBean.getEmailId());
		student.setMobile(adhocPaymentBean.getMobile());
		student.setCreatedBy(userId);
		student.setLastModifiedBy(userId);
		
		adhocPaymentsLogger.info("Input values:-\nEmailId:"+adhocPaymentBean.getEmailId());
		adhocPaymentsLogger.info("Mobile No:"+adhocPaymentBean.getMobile());
		adhocPaymentsLogger.info("Amount:"+adhocPaymentBean.getAmount());
		adhocPaymentsLogger.info("Payment Type:"+adhocPaymentBean.getPaymentType());
		
		//request.getSession().setAttribute("SECURE_SECRET", SECURE_SECRET);
		populateAdhocPaymentRequestObject(adhocPaymentBean,request);
		serviceRequestDao.InsertAdHocPaymentRequest(adhocPaymentBean); // insert adhoc request with track id for update record after making successful payment 
		fillPaymentParametersInMapForAdhocPayment(model, student, adhocPaymentBean);
		adhocPaymentsLogger.info("ServiceRequestController.saveAdhocPaymentRequest() - END");
		
		//adhocPaymentsLogger.info("Redirecting to 'payForAdHocPayment' mapping with model data.");
		//return new ModelAndView(new RedirectView("payForAdHocPayment"), model);
		
		response.setHeader("Set-Cookie", "SESSION=" + request.getSession().getId() + "; Path=/studentportal/; HttpOnly; SameSite=none; Secure");
		return new ModelAndView("jsp/payment");
	}
	
	// method to handle Student pending Amont 
	@RequestMapping(value="/student/savePendingAmountRequest", method={RequestMethod.GET , RequestMethod.POST})
	public ModelAndView savePendingAmountRequest(HttpServletRequest request, HttpServletResponse response,@ModelAttribute AdhocPaymentStudentPortalBean adhocPaymentBean,ModelMap model)
	{
		StudentStudentPortalBean student = (StudentStudentPortalBean)request.getSession().getAttribute("student_studentportal");
		adhocPaymentBean.setTranStatus(ServiceRequestStudentPortal.TRAN_STATUS_INITIATED);
		adhocPaymentBean.setRequestStatus(ServiceRequestStudentPortal.REQUEST_STATUS_SUBMITTED);
		adhocPaymentBean.setCreatedBy(adhocPaymentBean.getEmailId());
		adhocPaymentBean.setSapId(student.getSapid());
		adhocPaymentBean.setLastModifiedBy(student.getSapid());
		adhocPaymentBean.setCreatedBy(student.getSapid());
		adhocPaymentBean.setProgram(student.getProgram());
		adhocPaymentBean.setYear(student.getEnrollmentYear());
		adhocPaymentBean.setMonth(student.getEnrollmentMonth());
		serviceRequestDao.updateAdHocPayment(adhocPaymentBean);
		populateAdhocPaymentRequestObject(adhocPaymentBean,request);
		fillPaymentParametersInMapForPendingPayment(model, student, adhocPaymentBean);
		return new ModelAndView(new RedirectView("jsp/pay"), model);
	}
	
	@RequestMapping(value = "/admin/searchSRPage", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView searchSRPage(HttpServletRequest request, HttpServletResponse response) {
		if (!checkSession(request, response)) {
			return new ModelAndView("jsp/studentPortalRediret");
		}
		UserAuthorizationStudentPortalBean userAuthorization = (UserAuthorizationStudentPortalBean) request.getSession()
				.getAttribute("userAuthorization_studentportal");
		String roles = "";
		if (userAuthorization != null) {
			roles = (userAuthorization.getRoles() != null && !"".equals(userAuthorization.getRoles()))
					? userAuthorization.getRoles()
					: roles;
		}
		request.setAttribute("roles", roles);
		ModelAndView modelnView = new ModelAndView("jsp/serviceRequest/searchSR");
		ServiceRequestStudentPortal sr = (ServiceRequestStudentPortal) request.getSession().getAttribute("searchBean");
		int pageNo = Integer.parseInt(request.getParameter("pageNo"));
		HashMap<String,String> mapOfActiveSRTypesAndTAT = serviceRequestDao.mapOfActiveSRTypesAndTAT();
		PageStudentPortal<ServiceRequestStudentPortal> page = serviceRequestDao.getServiceRequestPage(pageNo, pageSize, sr,
				getAuthorizedCodes(request));
		List<ServiceRequestStudentPortal> srList = page.getPageItems();
		modelnView.addObject("page", page);
		modelnView.addObject("rowCount", page.getRowCount());
		if (srList == null || srList.size() == 0) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No Records found.");
		}

		modelnView.addObject("srList", srList);
		modelnView.addObject("sr", sr);
		modelnView.addObject("mapOfActiveSRTypesAndTAT", mapOfActiveSRTypesAndTAT);
		modelnView.addObject("allRequestTypes", getAllAtiveRequestTypes());
		return modelnView;
	}

	@RequestMapping(value = "/admin/downloadSRReport", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView downloadSRReport(HttpServletRequest request, HttpServletResponse response) {
		
		if (!checkSession(request, response)) {
			return new ModelAndView("jsp/studentPortalRediret");
			
		}

		ServiceRequestStudentPortal sr = (ServiceRequestStudentPortal) request.getSession().getAttribute("searchBean");

		PageStudentPortal<ServiceRequestStudentPortal> page = serviceRequestDao.getServiceRequestPage(1, Integer.MAX_VALUE, sr,
				getAuthorizedCodes(request));
		List<ServiceRequestStudentPortal> srList = page.getPageItems();

		return new ModelAndView(serviceRequestExcelView, "srList", srList);
	}
	@RequestMapping(value = "/admin/saveHistoryStatus", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String saveHistoryStatus(@RequestParam String value, @RequestParam Long pk,
			HttpServletRequest request) {
		try {
			
			serviceRequestDao.saveServiceHistoryStatus(value,pk);
			return "{\"status\": \"success\", \"msg\": \"Status saved successfully!\"}";
		} catch (Exception e) {
//			e.printStackTrace();
			return "{\"status\": \"error\", \"msg\": \"Error in saving Status!\"}";
		}

	}
	
	@RequestMapping(value = "/admin/saveRequestStatus", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String saveRequestStatus(@RequestParam String value, @RequestParam Long pk, @RequestParam String reason,
			HttpServletRequest request) {
		try {
			String userId = (String) request.getSession().getAttribute("userId");
			serviceRequestDao.updateSrReqStatusCancelReason(pk, value, reason, userId);
			serviceRequestDao.updateSrHistoryReqStatus(pk+"", value, userId);
			ServiceRequestStudentPortal sr = serviceRequestDao.findById(pk + "");
			serviceRequestDao.insertServiceRequestStatusHistory(sr, "Update");
			StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(sr.getSapId());
			if ("Closed".equalsIgnoreCase(value)) {
				if(ServiceRequestStudentPortal.EXIT_PROGRAM.equals(sr.getServiceRequestType())) {
//					serviceRequestRepository.updateProgramStatusForDeregisteredStudent(Long.valueOf(sr.getSapId()), userId);
				}
				serviceRequestDao.setClosedDateForServiceRequest(value, pk, userId);// Added
																					// to
																					// update
																					// Closed
																					// Date
																					// of
																					// Service
				// Request//
				//ServiceRequest sr = serviceRequestDao.findById(pk + "");
				//StudentBean student = serviceRequestDao.getSingleStudentsData(sr.getSapId());
				serviceRequestDao.insertServiceRequestStatusHistory(sr, "Update");
				MailSender mailer = (MailSender) act.getBean("mailer");
				mailer.sendSRClosureEmail(sr, student.getFirstName(), student.getEmailId());
			} else if ("Cancelled".equalsIgnoreCase(value)) {
				serviceRequestDao.deleteSRHistoryForSR(pk+"");
				
				if(ServiceRequestStudentPortal.ASSIGNMENT_REVALUATION.equals(sr.getServiceRequestType()))
				{
					UnMarkAssignmentRevaluationSubjectsForCancelled(sr);
				}else if(ServiceRequestStudentPortal.TEE_REVALUATION.equals(sr.getServiceRequestType())){
					UnmarkForTeeRevaluationSubjectsForCancelled(sr);
				}
				else if(ServiceRequestStudentPortal.EXIT_PROGRAM.equals(sr.getServiceRequestType()) || ServiceRequestStudentPortal.DE_REGISTERED.equals(sr.getServiceRequestType()) || ServiceRequestStudentPortal.PROGRAM_WITHDRAWAL.equals(sr.getServiceRequestType())){
//					serviceRequestRepository.updateProgramStatusAsActiveForDeregistrationCancellation(Long.valueOf(sr.getSapId()), userId);
				}
				
				MailSender mailer = (MailSender) act.getBean("mailer");
				mailer.sendSRCancellationEmail(sr, student.getFirstName(), student.getEmailId());
			}
			return "{\"status\": \"success\", \"msg\": \"Status saved successfully!\"}";
		} catch (Exception e) {
//			e.printStackTrace();
			return "{\"status\": \"error\", \"msg\": \"Error in saving Status!\"}";
		}
	}

	@RequestMapping(value = "/admin/refundPaymentForm", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView refundPaymentForm(HttpServletRequest request, HttpServletResponse response) {
		if (!checkSession(request)) {
			return new ModelAndView("jsp/login");
		}
		ModelAndView modelnView = new ModelAndView("jsp/refundPaymentForm");
		AdhocPaymentStudentPortalBean adhocPaymentBean = new AdhocPaymentStudentPortalBean();
		HashMap<String,String> mapOfTrackIdAndRefundDescription = new HashMap<String,String>();
		mapOfTrackIdAndRefundDescription = serviceRequestDao.mapOfTrackIdAndRefundDescription();
		request.getSession().setAttribute("mapOfTrackIdAndRefundDescription", mapOfTrackIdAndRefundDescription);
		modelnView.addObject("refundPaymentTypeList", refundPaymentTypeList);
		modelnView.addObject("adhocPaymentBean", adhocPaymentBean);
		return modelnView;
	}
	
	

	// refund Payment for Service Request ,Exam Booking,Exam Registration
	@RequestMapping(value = "/admin/queryRefundPayment", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView queryRefundPayment(@ModelAttribute("adhocPaymentBean") AdhocPaymentStudentPortalBean adhocPaymentBean , HttpServletRequest request,HttpServletResponse response, Model m) {
		if (!checkSession(request)) {
			return new ModelAndView("jsp/login");
		}
		
		ModelAndView modelnview = new ModelAndView("jsp/refundPaymentForm");
		
		HashMap<String,String> mapOfSapIdAndTrackIdOfRefundTable = (HashMap<String,String>)request.getSession().getAttribute("mapOfTrackIdAndRefundDescription");
		
		//This is done in order to prevent refunding for the same track Id//
		if(!"".equals(mapOfSapIdAndTrackIdOfRefundTable.get(adhocPaymentBean.getMerchantRefNo())) && mapOfSapIdAndTrackIdOfRefundTable.get(adhocPaymentBean.getMerchantRefNo()) != null){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Refund already initiated for Track Id :-"+adhocPaymentBean.getMerchantRefNo());
		}
		
		HashMap<String, AdhocPaymentStudentPortalBean> trackIdTransactionMap = new HashMap<>(); // mapOf TransactionId and AdhocPaymentBean 
		String trackId = "", sapId = "";
		trackId=adhocPaymentBean.getMerchantRefNo();
		ArrayList<AdhocPaymentStudentPortalBean> listOfFailedPayments = new ArrayList<AdhocPaymentStudentPortalBean>();
		
		// handle refund request based upon Request Type on page ie:- Service request, Exam Registration, PCP Booking
		if("Service Request".equals(adhocPaymentBean.getFeesType()))
		{
			listOfFailedPayments = serviceRequestDao.getSRPaymentFromMerchantId(adhocPaymentBean.getMerchantRefNo());
			
		}else if("Exam Registration".equals(adhocPaymentBean.getFeesType())){
			
		    listOfFailedPayments =serviceRequestDao.getExamBookingPaymentFromMerchantId(adhocPaymentBean.getMerchantRefNo());
		    if(listOfFailedPayments.size() <= 0) {
		    	listOfFailedPayments = serviceRequestDao.getExamBookingHistoryPaymentFromMerchantId(adhocPaymentBean.getMerchantRefNo());
		    }
		}else if("Exam Registration MBA - WX".equals(adhocPaymentBean.getFeesType()) || "MSc AI&ML Ops Program Re-Exam".equalsIgnoreCase(adhocPaymentBean.getFeesType())){
			
		    listOfFailedPayments =serviceRequestDao.getMBAWXExamBookingPaymentFromMerchantId(adhocPaymentBean.getMerchantRefNo());
		}else if("Exam Registration MBA - X".equals(adhocPaymentBean.getFeesType())){
			
		    listOfFailedPayments =serviceRequestDao.getMBAXExamBookingPaymentFromMerchantId(adhocPaymentBean.getMerchantRefNo());

		}else if("PCP Booking".equals(adhocPaymentBean.getFeesType())) 
		{
			listOfFailedPayments =serviceRequestDao.getPCPBookingPaymentFromMerchantId(adhocPaymentBean.getMerchantRefNo());
		}else if("Assignment Fees".equals(adhocPaymentBean.getFeesType())) 
		{
			listOfFailedPayments =serviceRequestDao.getAssignmentFeesPaymentFromMerchantId(adhocPaymentBean.getMerchantRefNo());
		}
		
		// if paymentoption is null check from transaction table and fetch payment option from transactions table
		
		try {
			if (listOfFailedPayments != null && listOfFailedPayments.size() > 0) {
				for (int i = 0; i < listOfFailedPayments.size(); i++) {
					AdhocPaymentStudentPortalBean bean = listOfFailedPayments.get(i);
					if(bean.getPaymentOption() == null || bean.getPaymentOption() == "") {
//						String paymentOption = serviceRequestDao.getPaymentOptionByTrackId(adhocPaymentBean.getMerchantRefNo());
						String paymentOption = gatewayTransactionsDAO.getPaymentOptionByTrackId(adhocPaymentBean.getMerchantRefNo());
						if(paymentOption != null || paymentOption != "")
							bean.setPaymentOption(paymentOption);
					}
					sapId = bean.getSapId();
					trackId = bean.getTrackId();
					adhocPaymentBean.setTrackId(trackId);
					if("paytm".equals(bean.getPaymentOption())) {
						JsonObject jsonObj = paymentHelper.getPaytmTransactionStatus(trackId);
						String STATUS = jsonObj.get("STATUS").getAsString();
						if(STATUS.equalsIgnoreCase("TXN_SUCCESS")) {
							adhocPaymentBean.setTransactionID(jsonObj.get("TXNID").getAsString());
							adhocPaymentBean.setPaymentID(jsonObj.get("BANKTXNID").getAsString());
							adhocPaymentBean.setRespAmount(jsonObj.get("TXNAMOUNT").getAsString());
							adhocPaymentBean.setRespTranDateTime(jsonObj.get("TXNDATE").getAsString());
							adhocPaymentBean.setMerchantRefNo(jsonObj.get("ORDERID").getAsString());
							adhocPaymentBean.setTransactionType(jsonObj.get("TXNTYPE").getAsString());
						}else {
							adhocPaymentBean.setError(jsonObj.get("RESPMSG").getAsString());
						}
						adhocPaymentBean.setPaymentOption("paytm");
					}
					else if("payu".equals(bean.getPaymentOption())) {
						JsonObject jsonObj = paymentHelper.getPayuTransactionStatus(trackId);
						String STATUS = jsonObj.get("status").getAsString();
						JsonObject transaction_detail = jsonObj.get("transaction_details").getAsJsonObject();
						transaction_detail = transaction_detail.get(trackId).getAsJsonObject();
						String MSG = transaction_detail.get("status").getAsString();
						if("1".equalsIgnoreCase(STATUS) && "success".equalsIgnoreCase(MSG)) {
							adhocPaymentBean.setTransactionID(transaction_detail.get("mihpayid").getAsString());
							adhocPaymentBean.setPaymentID(transaction_detail.get("bank_ref_num").getAsString());
							adhocPaymentBean.setRespAmount(transaction_detail.get("transaction_amount").getAsString());
							adhocPaymentBean.setRespTranDateTime(transaction_detail.get("addedon").getAsString());
							adhocPaymentBean.setMerchantRefNo(transaction_detail.get("txnid").getAsString());
							adhocPaymentBean.setTransactionType(transaction_detail.get("mode").getAsString());
						}else {
							adhocPaymentBean.setError(transaction_detail.get("error_Message").getAsString());
						}
						adhocPaymentBean.setPaymentOption("payu");
					}else if("billdesk".equals(bean.getPaymentOption())) {
						String billdeskResponse = paymentHelper.getBillDeskTransactionStatus(trackId);
						adhocPaymentBean.setPaymentOption("billdesk");
						if(billdeskResponse != null) {
							try {
								String[] responseList = billdeskResponse.split("\\|");
								if("0002".equalsIgnoreCase(responseList[15])) {
									adhocPaymentBean.setError("Pending billdesk");
								}
								else if("0300".equalsIgnoreCase(responseList[15])) {
									adhocPaymentBean.setTransactionID(responseList[3]);
									SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
									SimpleDateFormat parser = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
									adhocPaymentBean.setTranDateTime(formatter.format(parser.parse(responseList[14])));
									adhocPaymentBean.setRespAmount(responseList[5]);
								}
								else {
									adhocPaymentBean.setError("Invalid response from billdesk");
								}
							}
							catch (Exception e) {
								// TODO: handle exception
								adhocPaymentBean.setError("Error: " + e.getMessage());
							}
						}else {
							adhocPaymentBean.setError("Null response from billdesk");
						}
					}
					else if("razorpay".equals(bean.getPaymentOption())) {
						adhocPaymentBean.setPaymentOption("razorpay");
						RestTemplate restTemplate = new  RestTemplate();
						String url = GATEWAY_TRANSACTION_STATUS;
						Map<String, String> requestObject = new HashMap<>();
						
						requestObject.put("trackId", trackId);
						razorpayLogger.info("Creating request object for track id to find transaction on razorpay  with track id : {}",trackId);
						
						ResponseEntity<RazorpayTransactionBean> refundBeanEntity = restTemplate.postForEntity(url, requestObject, RazorpayTransactionBean.class);
						
						razorpayLogger.info("received response from res template  : {}", refundBeanEntity.toString());
						
						RazorpayTransactionBean refundBean = refundBeanEntity.getBody();
						
						razorpayLogger.info("status of transaction found on razorpay : {}", refundBean.getOrderStatus());
						if(refundBean.getOrderStatus() != null) {
							
							if("paid".equalsIgnoreCase(refundBean.getOrderStatus()) 
									&& "captured".equalsIgnoreCase(refundBean.getPaymentStatus())) {
								
								adhocPaymentBean.setTransactionID(refundBean.getTrackId());
								adhocPaymentBean.setPaymentID(refundBean.getPaymentId());
								adhocPaymentBean.setRespAmount(refundBean.getAmount()); 
								adhocPaymentBean.setRespTranDateTime(refundBean.getCreatedAt());
								adhocPaymentBean.setMerchantRefNo(refundBean.getMerchantRefNo());
								adhocPaymentBean.setTransactionType(refundBean.getTransactionMethod());
								razorpayLogger.info("Status was {}, so marking filling adhocpayment bean with data", refundBean.getOrderStatus());
								
							} else if(("attempted".equalsIgnoreCase(refundBean.getOrderStatus()) || "created".equalsIgnoreCase(refundBean.getOrderStatus())) 
									&& ("failed".equalsIgnoreCase(refundBean.getPaymentStatus()) || "created".equalsIgnoreCase(refundBean.getPaymentStatus()))) {
								
								razorpayLogger.info("order status is {} and payment status is {} so marking transaction as failed with error  : {}",
										refundBean.getOrderStatus(), refundBean.getPaymentStatus(),  refundBean.getError());
								adhocPaymentBean.setError(refundBean.getError());
								
							} else if(bean.getError() != null) {
								razorpayLogger.info("Error received from payment gateways {}", bean.getError());
								adhocPaymentBean.setError(bean.getError());
								
							}
						}else
							adhocPaymentsLogger.info("Order status was null for razorpay transaction");
					} else {
						XMLParser parser = new XMLParser();
					String xmlResponse = parser.queryTransactionStatus(trackId, ACCOUNT_ID, SECURE_SECRET); //Just checks if it is applicable for refund//
					
					adhocPaymentBean = parser.parseRefundResponse(xmlResponse, adhocPaymentBean);
					adhocPaymentBean.setPaymentOption("hdfc");
					}
					trackIdTransactionMap.put(trackId, adhocPaymentBean);
				}
				
				request.getSession().setAttribute("trackIdTransactionMap", trackIdTransactionMap);
				m.addAttribute("listOfFailedPayments", listOfFailedPayments);
				
				modelnview.addObject("refundAmount", adhocPaymentBean.getRefundAmount());
				modelnview.addObject("adhocPaymentBean", adhocPaymentBean);
				
			} else {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No Transactions found for " + trackId);
			}
		} catch (Exception e) {
//			e.printStackTrace();
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in retriving details. Please try again");
		}
		modelnview.addObject("refundPaymentTypeList", refundPaymentTypeList);
		return modelnview;
	}
	

	@RequestMapping(value="/admin/updateIssuedDocuments",method={RequestMethod.GET,RequestMethod.POST})
	public @ResponseBody String updateIssuedDocuments(@RequestParam String value, @RequestParam Long pk,
			HttpServletRequest request){
		try {
			String userId = (String) request.getSession().getAttribute("userId");
			serviceRequestDao.saveDoucmentCollectedStatus(value, pk, userId);
			ServiceRequestStudentPortal bean = serviceRequestDao.getServiceRequestBySrId(pk);
			serviceRequestDao.insertServiceRequestStatusHistory(bean, "Update");
			return "{\"status\": \"success\", \"msg\": \"Status saved successfully!\"}";
		} catch (Exception e) {
//			e.printStackTrace();
			return "{\"status\": \"error\", \"msg\": \"Error in saving Status!\"}";
		}
		
	}
	
	/**
	 * Updates the reason specified for the IssuanceOfBonafide SR 
	 * @param value - reason for raising IssuanceOfBonafide SR
	 * @param pk - unique id of the SR
	 * @param request - HttpServletRequest
	 * @return JSON response required by x-editable
	 */
	@RequestMapping(value="/admin/updateBonafideIssuanceReason", method={RequestMethod.POST})
	public @ResponseBody String updateBonafideIssuanceReason(@RequestParam String value, @RequestParam Long pk, HttpServletRequest request) {
		try {
			String userId = (String) request.getSession().getAttribute("userId");
			int countOfReasonChanged = serviceRequestDao.updateBonafideReason(value, pk, userId);
			
			ServiceRequestStudentPortal bean = serviceRequestDao.getServiceRequestBySrId(pk);
			serviceRequestDao.insertServiceRequestStatusHistory(bean, "Update");
			logger.info("Specified reason(" + countOfReasonChanged + ") for IssuanceOfBonafide SR " + pk + " updated successfully, by " + userId);
			return "{\"status\": \"success\", \"msg\": \"IssuanceOfBonafide SR reason updated successfully!\"}";
		} 
		catch (Exception ex) {
			ex.printStackTrace();
			logger.info("Failed to update reason for IssuanceOfBonafide SR " + pk);
			return "{\"status\": \"error\", \"msg\": \"Error in updating reason!\"}";
		}
	}

	/*
	 * This method refunds money either to Student Bank account or to his Wallet based on option selected  by Finance
	 */
	@RequestMapping(value = "/admin/refundPayment", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView refundPayment(HttpServletRequest request, HttpServletResponse response) {
		
		if (!checkSession(request)) {
			return new ModelAndView("jsp/login");
		}
		WalletDAO wDao = (WalletDAO)act.getBean("walletDao");
		ModelAndView modelnview = new ModelAndView("jsp/refundPaymentForm");
		
		String trackId = request.getParameter("trackId");
		String amountToBeRefunded = request.getParameter("refundAmount");
		String typeOfRefund = (String)request.getParameter("typeOfRefund");
		
		AdhocPaymentStudentPortalBean bean = null;
		WalletBean walletRecord = new WalletBean();
		
		int noOfRowsUpdated = 0;
		
		HashMap<String, AdhocPaymentStudentPortalBean> trackIdTransactionMap = (HashMap<String, AdhocPaymentStudentPortalBean>) request.getSession().getAttribute("trackIdTransactionMap");
		String transactionType = "";
		
		try {
			bean = trackIdTransactionMap.get(trackId);
			bean.setAmount(amountToBeRefunded);
			PersonStudentPortalBean person = (PersonStudentPortalBean) request.getSession().getAttribute("user_studentportal");
			bean.setCreatedBy(person.getUserId());
			bean.setLastModifiedBy(person.getUserId());
			//Wallet processing starts here//
			if("Wallet".equals(typeOfRefund)){
				walletRecord = wDao.getWalletRecord(bean.getSapId());
				if(walletRecord == null){
					walletRecord = wDao.insertWalletRecordAndReturnRecord(bean.getSapId());
				}
				walletRecord.setUserId((String)request.getSession().getAttribute("userId"));
				walletRecord.setWalletId(walletRecord.getId()+"");
				walletRecord.setCreatedBy((String)request.getSession().getAttribute("userId"));
				walletRecord.setLastModifiedBy((String)request.getSession().getAttribute("userId"));
				mapRecordToBeRefundedToWalletBean(bean,walletRecord);
				updateWalletBalance(walletRecord,WalletBean.CREDIT);
				try{
					wDao.updateWMoneyLoadAndInsertWTransactionAndUpdateWBalance(walletRecord, false);
					transactionType = "Refunded";
				}catch(Exception e){
//					e.printStackTrace();
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "Error in processing initiating refund to Wallet"+e.getMessage());
				}
			//Ends here. if the process is a success set the transaction type to refunded//	
			}else{
				if("paytm".equals(bean.getPaymentOption())) {
					JsonObject jsonObject = paymentHelper.refundInitiate(trackId, bean.getTransactionID(),amountToBeRefunded);
					JsonObject body = jsonObject.get("body").getAsJsonObject();
					JsonObject resultInfo = body.get("resultInfo").getAsJsonObject();
					String STATUS =  resultInfo.get("resultStatus").getAsString();
					if(STATUS.equals("TXN_SUCCESS")) {
						bean.setRefId(body.get("refId").getAsString());
						bean.setRefundId(body.get("refundId").getAsString());
						transactionType = "Refunded";	//successfully refund initiated
					}
					else if(STATUS.equals("PENDING")) {
						bean.setRefId(body.get("refId").getAsString());
						bean.setRefundId(body.get("refundId").getAsString());
						transactionType = "Pending";
					}
					else {
						transactionType = resultInfo.get("resultMsg").getAsString();	//error while refund initiated
					}
				}
				else if("payu".equals(bean.getPaymentOption())) {
					
					//PaymentHelper paymentHelper = new PaymentHelper();
					JsonObject jsonObject = paymentHelper.payuRefundInitiate(trackId, bean.getTransactionID(),amountToBeRefunded);
					String STATUS =  jsonObject.get("status").getAsString();
					String MSG = jsonObject.get("msg").getAsString();
					if(STATUS.equals("1") && MSG.equals("Refund Request Queued")) {
						bean.setRefId(jsonObject.get("refId").getAsString());
						bean.setRefundId(jsonObject.get("request_id").getAsString());
						transactionType = "Refunded";	//successfully refund initiated
					}
					else if(STATUS.equals("1")) {
						bean.setRefId(jsonObject.get("refId").getAsString());
						bean.setRefundId(jsonObject.get("request_id").getAsString());
						transactionType = "Pending";
					}
					else {
						transactionType = jsonObject.get("msg").getAsString();	//error while refund initiated
					}
				}
				else if("billdesk".equals(bean.getPaymentOption())) {
					Timestamp timestamp = new Timestamp(System.currentTimeMillis());
					Random rand = new Random(); 
					bean.setRefundId(timestamp.getTime() + "" + rand.nextInt(1000));
					String billdeskResponse = paymentHelper.billdeskRefundInitiate(trackId, bean.getTransactionID(), amountToBeRefunded, bean.getAmount(), bean.getTranDateTime(),bean.getRefundId());
					if(billdeskResponse != null) {
						try {
							String[] responseList = billdeskResponse.split("\\|");
							if("Y".equalsIgnoreCase(responseList[12]) && ("0699".equalsIgnoreCase(responseList[8]) || "0799".equalsIgnoreCase(responseList[8])) && "NA".equalsIgnoreCase(responseList[10])) {
								transactionType = "Refunded";
							}
							else {
								transactionType = "Error: " + responseList[11];
							}
						}
						catch (Exception e) {
							// TODO: handle exception
							transactionType = "Error: " + e.getMessage();
						}
					}else {
						transactionType = "Error: Null response from billdesk";
					}
				}
				else if("razorpay".equals(bean.getPaymentOption())) {
					
					razorpayLogger.info("Payment option of refund was razorpay with track id : {}", trackId);
					
					Map<String, String> requestBody = new HashMap<String, String>();
					RestTemplate restTemplate = new RestTemplate();
					
					requestBody.put("trackId", trackId);
					requestBody.put("paymentId", bean.getPaymentID());
					requestBody.put("amount", amountToBeRefunded);
					
					razorpayLogger.info("About to call url : {} to initiate refund with request body : {}", GATEWAY_REFUND_URL, requestBody.toString());
					String url = GATEWAY_REFUND_URL;
					
					ResponseEntity<RazorpayTransactionBean> refundEntity = restTemplate.postForEntity(url, requestBody, RazorpayTransactionBean.class);
					
					razorpayLogger.info("Received response from rest template : {}", refundEntity.toString());
					
					RazorpayTransactionBean refundBean = refundEntity.getBody();
					
					razorpayLogger.info("inside initiate razorpay refund initiate with entity" + refundEntity.toString());
					
					 if(refundBean.isHasError()) {
						 
							transactionType = "Error: " + refundBean.getError();
							razorpayLogger.info("has error so inside has error block with transaction type : {}" , transactionType);
							
					} else if(refundBean.getRefundStatus() != null && !refundBean.isHasError()) {
						
						razorpayLogger.info("Refund status : {}", refundBean.getRefundStatus());
						 if("processed".equalsIgnoreCase(refundBean.getRefundStatus())) {
							 
							bean.setRefId(refundBean.getTrackId());
							bean.setRefundId(refundBean.getRefundId());
							transactionType = "Refunded";
							
						} else if("pending".equalsIgnoreCase(refundBean.getRefundStatus())) {
							
							bean.setRefId(refundBean.getTrackId());
							bean.setRefundId(refundBean.getRefundId());
							transactionType = "Pending";
							
						} 
					}
					else {
						razorpayLogger.info("Received no response from razorpay");
						transactionType = "No response from razorpay";
					}
				} 
				else { 
					XMLParser parser = new XMLParser();
					String xmlResponse = parser.initiateRefund(trackId, ACCOUNT_ID, amountToBeRefunded, SECURE_SECRET, bean);
				    transactionType = parser.getTransactionTypeFromResponse(xmlResponse, bean);
				}
				
			}
			
			if (transactionType != null && "Refunded".equals(transactionType)) {
				request.setAttribute("success", "true");
				request.setAttribute("successMessage", "Refund Initiated Successfully.");
				bean.setRefundStatus("Y");
				bean.setRefundAmount(amountToBeRefunded);
				bean.setStatus("success");
				
				noOfRowsUpdated = serviceRequestDao.InsertRefundTransactions(bean);// Insert successfully refund payment records 
				StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(bean.getSapId());
				if("Exam Registration".equals(bean.getFeesType())){
					serviceRequestDao.updateExamBookingAmountForRefund(bean);
				}else if("Exam Registration MBA - WX".equals(bean.getFeesType()) || "MSc AI&ML Ops Program Re-Exam".equalsIgnoreCase(bean.getFeesType())){
					serviceRequestDao.updateMBAWXExamBookingAmountForRefund(bean);
				}else if("Exam Registration MBA - X".equals(bean.getFeesType())){
					serviceRequestDao.updateMBAXExamBookingAmountForRefund(bean);
				}else if("Service Request".equals(bean.getFeesType())){
					serviceRequestDao.updateServiceRequestAmountForRefund(bean);
					ServiceRequestStudentPortal srBean = serviceRequestDao.getServiceRequestBySrId(bean.getId());
					serviceRequestDao.insertServiceRequestStatusHistory(srBean, "Update");
				}else if("PCP Booking".equals(bean.getFeesType())){
					serviceRequestDao.updatePCPBookingAmountForRefund(bean);
				}else if("Assignment Fees".equals(bean.getFeesType())){
					serviceRequestDao.updateAssignmentFeesAmountForRefund(bean);
				}
				MailSender mailSender = (MailSender) act.getBean("mailer");
				
				mailSender.sendRefundEmail(student, bean);

			} 
			else if(transactionType != null && "Pending".equals(transactionType)) {
				bean.setRefundAmount(amountToBeRefunded);
				bean.setStatus("pending");
				serviceRequestDao.InsertRefundTransactions(bean);
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Refund request was raised for this transaction. But it is pending state");
			}
			else {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Error in processing Record: "+transactionType);
			}

			
		} catch (Exception e) {
//			e.printStackTrace();
			request.setAttribute("error","true");
			request.setAttribute("errorMessage", "Error in initiating Refund");
		}
		
		modelnview.addObject("refundPaymentTypeList", refundPaymentTypeList);
		modelnview.addObject("adhocPaymentBean", bean);
		return modelnview;
	}

	
	@RequestMapping(value = "student/selectSRForm", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView selectSRForm(@ModelAttribute ServiceRequestStudentPortal sr, HttpServletRequest request,
			HttpServletResponse response, Model m) {

		if (!checkSession(request, response)) {
			return new ModelAndView("jsp/login");
		}

		if(homeController.checkIfMovingResultsToCache()) {
			return new ModelAndView("jsp/noDataAvailable");
		}
		
		if("true".equals(request.getParameter("isError")))
		{
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", request.getParameter("errorMessage"));
		}
		
		request.getSession().setAttribute("SECURE_SECRET", SECURE_SECRET);

		String caseRaised = request.getParameter("caseRaised");
		if ("true".equals(caseRaised)) {
			setSuccess(request,
					"Your Query/Case is received successfully. We will get back to you shortly. Please check your email for Case Reference Number.");
		}

		String sapid = (String) request.getSession().getAttribute("userId");
		StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(sapid);
		
		request.getSession().setAttribute("student_studentportal", student);

		if (student == null) {
			setError(request, "Student Details not found for " + sapid);
			return new ModelAndView("jsp/home");
		}
		
		String programForHeader =  (String) request.getSession().getAttribute("programForHeaderPortal");
		student.setProgramForHeader(programForHeader);
		request.getSession().setAttribute("student_studentportal", student);
		
		ArrayList<ServiceRequestStudentPortal> srList = serviceRequestDao.getStudentsSR(sapid);
		int rowCount = srList != null ? srList.size() : 0;

		List<Long> srIds = new ArrayList<Long>();
		List<Long> srIdList = new ArrayList<Long>();
		srIds = srList.stream().map(bean -> bean.getId()).collect(Collectors.toList());
		//getSrIdList method returns matching srIds from servicerequest_trackingRecords table by srIds 
		if(srIds.size()>0)
			srIdList = massUploadTrackingSRDAO.getSrIdList(srIds);
		
		m.addAttribute("rowCount", rowCount);
		m.addAttribute("srList", srList);
		m.addAttribute("sr", sr);
		m.addAttribute("srIdList", srIdList);
		//ArrayList<String> srTypes =  getRequestTypes(request,sapid);
		ArrayList<String> srTypes =  servReqServ.getSrRequestTypes(sapid);
		srTypes.remove("Issuance of Gradesheet");
		m.addAttribute("requestTypes", srTypes);
		return new ModelAndView("jsp/serviceRequest/selectSRNew");
	}
	
//	to be deleted, api shifted to rest controller 
//	@RequestMapping(value = "/m/getStudentSRList", method = RequestMethod.POST,consumes = "application/json", produces = "application/json")
//	public ResponseEntity<ArrayList<ServiceRequest>> getStudentSRList(HttpServletRequest request,@RequestBody StudentBean student){
//		//student.setSapid(request.getParameter("sapid"));
//		String status = request.getParameter("status");
//		ArrayList<ServiceRequest> srList = new ArrayList<ServiceRequest>();
//		if(status.equalsIgnoreCase("pending")) {
//			srList = serviceRequestDao.getStudentsPendingSR(student.getSapid());
//		}
//		else if(status.equalsIgnoreCase("closed")) {
//			srList = serviceRequestDao.getStudentsClosedSR(student.getSapid());
//		}
//		for(ServiceRequest bean : srList) {
//			if(bean.getHasDocuments().equalsIgnoreCase("Y")) {
//				bean.setDocuments(serviceRequestDao.getDocuments(bean.getId()));
//			}
//		}
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json");
//		return new ResponseEntity<ArrayList<ServiceRequest>>(srList,headers,HttpStatus.OK);
//	}
	
//	@RequestMapping(value = "/m/getActiveSRList", method = RequestMethod.POST, headers = "content-type=multipart/form-data")
//	public ResponseEntity<ArrayList<String>> getActiveSRList(HttpServletRequest request,StudentBean student){
//		ArrayList<String> srList = getRequestTypes(request,student.getSapid());
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json");
//		return new ResponseEntity<ArrayList<String>>(srList,headers,HttpStatus.OK);
//	}
	
	/*
	 * @RequestMapping(value =
	 * "/proceedToPayForSR",method={RequestMethod.GET,RequestMethod.POST})
	 * public ModelAndView proceedToPayForSR(HttpServletRequest
	 * request,HttpServletResponse response,ModelMap model){ StudentBean student
	 * = (StudentBean)request.getSession().getAttribute("student_studentportal"); String srId
	 * = request.getParameter("srId"); ServiceRequest sr =
	 * serviceRequestDao.findById(srId); sr.setAmount("100");
	 * serviceRequestDao.updateAmountForReOpenedSRCase(srId);
	 * populateServiceRequestObject(sr,request);
	 * 
	 * 
	 * fillPaymentParametersInMap(model, student, sr ); return new
	 * ModelAndView(new RedirectView("pay"), model); }
	 */
	//marksheet passed sem list for mobile

	@RequestMapping(value = "/student/addSRForm", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView addSRForm(@ModelAttribute ServiceRequestStudentPortal sr, HttpServletRequest request, Model m) {
	
		if (!checkSession(request)) {
			return new ModelAndView("jsp/login");
		}

		if (sr.getId() != null) {
			m.addAttribute("edit", "true");
		}
		m.addAttribute("sr", sr);
		String serviceRequestType = sr.getServiceRequestType();
		//Prevent the student from raising a service request since father/mother name is incorrect//
		StudentStudentPortalBean student = (StudentStudentPortalBean)request.getSession().getAttribute("student_studentportal");
		
		
		
		List<String> NatureOfDisabilityTypes=new ArrayList<String>();
		NatureOfDisabilityTypes.add("PWD-Blindness");
		NatureOfDisabilityTypes.add("PWD-Low-vision");
		NatureOfDisabilityTypes.add("PWD-Leprosy Cured persons");
		NatureOfDisabilityTypes.add("PWD-Hearing Impairment");
		NatureOfDisabilityTypes.add("PWD-Locomotor Disability");
		NatureOfDisabilityTypes.add("PWD-Dwarfism");
		NatureOfDisabilityTypes.add("PWD-Intellectual Disability");
		NatureOfDisabilityTypes.add("PWD-Mental Illness");
		NatureOfDisabilityTypes.add("PWD-Autism Spectrum Disorder");
		NatureOfDisabilityTypes.add("PWD-Cerebral Palsy");
		NatureOfDisabilityTypes.add("PWD-Muscular Dystrophy");
		NatureOfDisabilityTypes.add("PWD-Chronic Neurological conditions");
		NatureOfDisabilityTypes.add("PWD-Specific Learning Disabilities");
		NatureOfDisabilityTypes.add("PWD-Multiple Sclerosis");
		NatureOfDisabilityTypes.add("PWD-Speech and language disability");
		NatureOfDisabilityTypes.add("PWD-Thalassemia");
		NatureOfDisabilityTypes.add("PWD-Hemophilia");
		NatureOfDisabilityTypes.add("PWD-Sickle Cell disease");
		NatureOfDisabilityTypes.add("PWD-Multiple Disabilities");
		NatureOfDisabilityTypes.add("PWD-Acid Attack victim");
		NatureOfDisabilityTypes.add("PWD-Parkinson's disease");
		m.addAttribute("NatureOfDisabilityTypes",NatureOfDisabilityTypes);
		if( student.getFatherName() == null || "".equals(student.getFatherName()) 
			|| student.getMotherName() == null || "".equals(student.getMotherName()) 
			|| student.getEmailId()== null || "".equals(student.getEmailId()) 
			|| student.getMobile() == null || "".equals(student.getMobile()) 
			|| student.getAddress() == null || "".equals(student.getAddress())
			|| student.getCity() == null || "".equals(student.getCity())){
			setError(request,"You will not be able to raise a Service request as your Profile is incomplete, kindly click <a href=\"updateProfile\">here </a> to update your information.");
			ModelAndView modelnView = new ModelAndView("jsp/serviceRequest/selectSRNew");
			modelnView.addObject("requestTypes", getAllRequestTypes());
			return modelnView;
		}
		
		
		
		if (ServiceRequestStudentPortal.DUPLICATE_FEE_RECEIPT.equals(serviceRequestType)) {
			return new ModelAndView("jsp/serviceRequest/duplicateFeeReceiptNew");
		} else if (ServiceRequestStudentPortal.ASSIGNMENT_REVALUATION.equals(serviceRequestType)
				|| ServiceRequestStudentPortal.OFFLINE_ASSIGNMENT_REVALUATION.equals(serviceRequestType)) {
			return assignmentRevaluationForm(sr, m, request);
		} else if (ServiceRequestStudentPortal.DUPLICATE_STUDY_KIT.equals(serviceRequestType)) {
			return new ModelAndView("jsp/serviceRequest/duplicateStudyKitNew");
		} else if (ServiceRequestStudentPortal.DUPLICATE_ID.equals(serviceRequestType)) {
			return new ModelAndView("jsp/serviceRequest/duplicateICard");
		} else if (ServiceRequestStudentPortal.CHANGE_IN_DOB.equals(serviceRequestType)) {
			return new ModelAndView("jsp/serviceRequest/changeinDOB");
		} else if (ServiceRequestStudentPortal.SINGLE_BOOK.equals(serviceRequestType)) {
			return singleBookForm(sr, m, request);
		} else if (ServiceRequestStudentPortal.TEE_REVALUATION.equals(serviceRequestType)) {
			return teeRevaluationForm(sr, m, request);
		} else if (ServiceRequestStudentPortal.OFFLINE_TEE_REVALUATION.equals(serviceRequestType)) {
			return teeRevaluationForm(sr, m, request);
		} else if (ServiceRequestStudentPortal.PHOTOCOPY_OF_ANSWERBOOK.equals(serviceRequestType)) {
			return teeRevaluationForm(sr, m, request);
		} else if (ServiceRequestStudentPortal.CHANGE_IN_ID.equals(serviceRequestType)) {
			return new ModelAndView("jsp/serviceRequest/changeInID");
			
		} else if (ServiceRequestStudentPortal.REDISPATCH_STUDY_KIT.equals(serviceRequestType)) {
//			return new ModelAndView("jsp/serviceRequest/redispatchStudyKit");
			ModelAndView mod  = new ModelAndView("jsp/serviceRequest/reDispatchRedirect");
			mod.addObject("serviceRequestType",sr.getServiceRequestType());
			if(("true".equals(request.getSession().getAttribute("error")))) {
				mod.addObject("error","true");
				mod.addObject("errorMessage",(String)request.getSession().getAttribute("errorMessage"));
			}
			return mod;
		} else if (ServiceRequestStudentPortal.CHANGE_IN_NAME.equals(serviceRequestType)) {
			return new ModelAndView("jsp/serviceRequest/changeInNameNew");
		} else if (ServiceRequestTypeEnum.CHANGE_FATHER_MOTHER_SPOUSE_NAME.getValue().equals(serviceRequestType)) {
			return changeFatherMotherSpouseNameSRForm(request);
		} else if (ServiceRequestTypeEnum.CHANGE_IN_CONTACT_DETAILS.getValue().equals(serviceRequestType)) {
			return changeInContactDetailsSRForm(request);
		} else if (ServiceRequestStudentPortal.ISSUEANCE_OF_TRANSCRIPT.equals(serviceRequestType)) {
			return issuanceOfTranscript(sr, request);

		}else if (ServiceRequestStudentPortal.ISSUEANCE_OF_BONAFIDE.equals(serviceRequestType)) {
			//return issuanceOfBonafideCertificateForm(sr, request);
			ModelAndView modelAndView = new ModelAndView("jsp/common/redirectPage");
			modelAndView.addObject("redirectURL", SERVER_PATH+"servicerequest/student/issuanceOfBonafideCertificateForm");
			return modelAndView;
		}
		else if (ServiceRequestStudentPortal.ISSUEANCE_OF_MARKSHEET.equals(serviceRequestType)) {
			sr.setAbcId(student.getAbcId());
			ArrayList<PassFailBean> yearMonthList = servReqServ.getPassedYearMonthList(student);
			ModelAndView modelnView = new ModelAndView("jsp/serviceRequest/marksheetNew");
			modelnView.addObject("programName", student.getProgram());
			modelnView.addObject("yearList", yearList);
			modelnView.addObject("courierAmount", 0);
			modelnView.addObject("yearMonthList",yearMonthList);
			return modelnView;
		} else if (ServiceRequestStudentPortal.ISSUEANCE_OF_CERTIFICATE.equals(serviceRequestType)) {
			//return checkFinalCertificateEligibility(sr, request);
			return checkFinalCertificateEligibilitynew(sr, request);
		}else if (ServiceRequestStudentPortal.DE_REGISTERED.equals(serviceRequestType)) {
			ModelAndView modelnView = new ModelAndView("jsp/serviceRequest/deRegister");
			return modelnView;
		} else if (ServiceRequestStudentPortal.CHANGE_IN_PHOTOGRAPH.equals(serviceRequestType)) {
			return new ModelAndView("jsp/serviceRequest/changeInPhotographNew");
		} else if (ServiceRequestStudentPortal.PROGRAM_WITHDRAWAL.equals(serviceRequestType)) {
			return checkIfStudentApplicableForWithdrawal(sr, request);
		}else if (ServiceRequestStudentPortal.EXIT_PROGRAM.equals(serviceRequestType)) {
			return checkIfStudentApplicableForExitProgram(sr, request);
		}else if (ServiceRequestStudentPortal.SPECIAL_NEED_SR.equals(serviceRequestType)) {
 			return checkIfStudentAlreadySubmittedOrApprovedForSpecialNeedSR(request,serviceRequestType);
 		}else if (ServiceRequestStudentPortal.SCRIBE_FOR_TERM_END_EXAM.equals(serviceRequestType)) {
 			return checkIfStudentAlreadySubmittedOrApprovedForScribe(request,serviceRequestType);
 		}

		return new ModelAndView("jsp/serviceRequest/selectSRNew");
	}
	

	@RequestMapping(value="/student/saveDeRegisteredRequest",method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView saveDeRegisteredRequest(HttpServletRequest request,HttpServletResponse response,@ModelAttribute ServiceRequestStudentPortal sr,Model m){
		String sapid = (String) request.getSession().getAttribute("userId");
		StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(sapid);
		
		sr.setDescription(sr.getServiceRequestType());
		sr.setTranStatus(ServiceRequestStudentPortal.TRAN_STATUS_FREE);
		sr.setCategory("Admission");
		sr.setSapId(sapid);
		sr.setRequestStatus(ServiceRequestStudentPortal.REQUEST_STATUS_SUBMITTED);
		sr.setCreatedBy(sapid);
		sr.setLastModifiedBy(sapid);
		sr.setIssued("N");
		sr.setSrAttribute("");
		serviceRequestDao.insertServiceRequest(sr);
		request.getSession().setAttribute("sr", sr);
		setSuccess(request,"Service Request for De-Registered Save Successfully..");
		MailSender mailer = (MailSender) act.getBean("mailer");
		mailer.sendSREmail(sr, student);
		return selectSRForm(sr, request, response, m);
	}

	
	
//	@RequestMapping(value="/student/issuanceOfBonafideCertificateForm",method={RequestMethod.GET,
//			RequestMethod.POST})
	public ModelAndView issuanceOfBonafideCertificateForm(@ModelAttribute ServiceRequestStudentPortal sr, HttpServletRequest request) {
//		String sapid = (String) request.getSession().getAttribute("userId");
				
//        int getBonafideIssuedCertificateBySapidCount = serviceRequestDao
//				.getBonafideIssuedCertificateBySapidCount(sapid);
//		if (getBonafideIssuedCertificateBySapidCount != 0) {
//			ModelAndView mAv = new ModelAndView("jsp/serviceRequest/selectSR");
//			request.setAttribute("error", "true");
//			request.setAttribute("errorMessage", "You already requested this service");
//			ArrayList<ServiceRequestStudentPortal> srList = serviceRequestDao.getStudentsSR(sapid);
//			ArrayList<String> srTypes = servReqServ.getSrRequestTypes(sapid);
//			srTypes.remove("Issuance of Gradesheet");
//			mAv.addObject("requestTypes", srTypes);
//			return mAv;
//		}
		
		ModelAndView modelAndView = new ModelAndView("jsp/serviceRequest/bonafideCertificate");
//		String sapid = (String) request.getSession().getAttribute("userId");
		StudentStudentPortalBean student = (StudentStudentPortalBean) request.getSession().getAttribute("student_studentportal");
//		int numberOfBonafiedCopiesIssued = 0;
		// Take only cases where the student has actually done payments or has made
		// service requests. Avoid initiated status//

//		if (getBonafideIssuedCertificateBySapid != null && getBonafideIssuedCertificateBySapid.size() > 0) {
//			for (ServiceRequestStudentPortal service : getBonafideIssuedCertificateBySapid) {
//				numberOfBonafiedCopiesIssued = numberOfBonafiedCopiesIssued + Integer.parseInt(service.getNoOfCopies());
//			}
//			modelAndView.addObject("numberOfBonafiedCopiesIssued", numberOfBonafiedCopiesIssued);
//		} else {
//			modelAndView.addObject("numberOfBonafiedCopiesIssued", 0);
//		}

		sr.setPostalAddress(student.getAddress());
		modelAndView.addObject("sr", sr);
//		modelAndView.addObject("charges", 0);
		return modelAndView;
	}

//	@RequestMapping(value="/student/saveBonafideRequest",method={RequestMethod.POST})
	public ModelAndView saveBonafideRequest(HttpServletRequest request,HttpServletResponse response,@ModelAttribute ServiceRequestStudentPortal sr, Model m){
		if (!checkSession(request)) {
			return new ModelAndView("jsp/login");
		}
		String sapid = (String) request.getSession().getAttribute("userId");
		
		// E-Bonafide request should be raise only 1 time for 5 purpose & 5 times for other purpose
		if(!serviceRequest.canRaiseEBonafideByPurpose(sapid, sr.getServiceRequestType(), sr.getPurpose()))
		{
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "You have already requested E-Bonafide SR with the reason : "+sr.getPurpose());
			
			return selectSRForm(sr, request, response, m);
		}
		
		sr.setIssued("N");
		sr.setModeOfDispatch("LC");
		serviceRequest.saveBonafideRequest(sr,request);
		return new ModelAndView("redirect:/student/srCreated/"+sr.getId());
	}
	
	@RequestMapping(value="/student/srCreated/{srId}",method={RequestMethod.GET})
	public ModelAndView srCreated(HttpServletRequest request, @PathVariable Long srId,Model m) {
		ModelAndView mv = new ModelAndView("jsp/serviceRequest/srCreated");
		ServiceRequestStudentPortal sr = serviceRequest.getServiceRequest(srId);
		request.setAttribute("success", "true");
		request.setAttribute("successMessage", "Service Request created successfully");	
		m.addAttribute("sr", sr);
		return mv;
	}
	
	@RequestMapping(value="/student/saveBonafideRequestAndProceedToPay",method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView saveBonafideRequestAndProceedToPay(HttpServletRequest request,HttpServletResponse response,@ModelAttribute ServiceRequestStudentPortal sr,ModelMap model/*,RedirectAttributes ra*/){
		ModelAndView modelAndView = new ModelAndView("jsp/serviceRequest/bonafideCertificate");
		String paymentType = (String)request.getParameter("typeOfPayment");
		
		StudentStudentPortalBean student = (StudentStudentPortalBean)request.getSession().getAttribute("student_studentportal");

		modelAndView.addObject("sr", sr);
		sr.setDescription(sr.getServiceRequestType() + " for student " + student.getSapid());
		sr.setTranStatus(ServiceRequestStudentPortal.TRAN_STATUS_INITIATED);
		sr.setCategory("Exam");
		
		if(sr.getAdditionalInfo1().equals("") || sr.getAdditionalInfo1().equals(null))
			sr.setAdditionalInfo1(sr.getPurpose());
		
		if("Yes".equals(sr.getWantAtAddress())){
			sr.setIssued("Y");
			sr.setModeOfDispatch("Courier");
		}else{
			sr.setIssued("N"); 
			sr.setModeOfDispatch("LC");
		}
		
		sr.setCreatedBy(student.getSapid());
		sr.setLastModifiedBy(student.getSapid());
		sr.setSrAttribute("");
		sr.setInformationForPostPayment(student.getSapid());// So that it is used post
		// payment for follow up steps
		populateServiceRequestObject(sr, request);
		serviceRequestDao.insertServiceRequest(sr);
		//fillPaymentParametersInMap(model, student, sr);
		String requestId = sr.getTrackId();
		
		if(request.getParameter("paymentOption").equals("hdfc")) {
			request.getSession().setAttribute("paymentOption","hdfc");
			fillPaymentParametersInMap(model, student, sr);
			return new ModelAndView(new RedirectView("jsp/pay"), model);
		}
		
		String paymentOption = request.getParameter("paymentOption");
		ModelAndView mv = new ModelAndView(paymentOption + "jsp/Pay");	//set payment jsp file name;
		//PaymentHelper paymentHelper = new PaymentHelper();
		String checkSum = paymentHelper.generateCommonCheckSum(sr, student, requestId,paymentOption);
		if(checkSum != "true") {
			setError(request, "Error: " + checkSum);
			return issuanceOfBonafideCertificateForm(sr, request);
		}
		request.getSession().setAttribute("paymentOption",paymentOption);
		mv = paymentHelper.setCommonModelData(mv, sr, student, requestId,paymentOption);
		return mv;
		
		/*else if(request.getParameter("paymentOption").equals("payu")) {
			ModelAndView mv = new ModelAndView("jsp/payu");
			PaymentHelper paymentHelper = new PaymentHelper();
			String checkSum = paymentHelper.generatePayuCheckSum(sr, student, requestId);
			if(checkSum != "true") {
				setError(request, "Error: " + checkSum);
				return issuanceOfBonafideCertificateForm(sr, request);
			}
			request.getSession().setAttribute("paymentOption","payu");
			mv = paymentHelper.setPayuModelData(mv, sr, student, requestId);
			return mv;
		}
		
		else if(request.getParameter("paymentOption").equals("billdesk")) {
			ModelAndView mv = new ModelAndView("jsp/billdeskPay");
			PaymentHelper paymentHelper = new PaymentHelper();
			String checkSum = paymentHelper.generateBillDeskCheckSum(sr);
			if(checkSum != "true") {
				setError(request, "Error: " + checkSum);
				return issuanceOfBonafideCertificateForm(sr, request);
			}
			request.getSession().setAttribute("paymentOption","billdesk");
			mv = paymentHelper.setBillDeskModelData(mv,sr,student,requestId);
			return mv;
		}
		
		return proceedToPayOptions(model,requestId,ra);
		ModelAndView mv = new ModelAndView("jsp/paytmPay");
		PaymentHelper paymentHelper = new PaymentHelper();
		String checkSum = paymentHelper.generateCheckSum(sr,student,requestId);
		if(checkSum != "true") {
			setError(request, "Error: " + checkSum);
			return issuanceOfBonafideCertificateForm(sr, request);
		}
		request.getSession().setAttribute("paymentOption","paytm");
		mv = paymentHelper.setModelData(mv,sr,student,requestId);
		return mv;*/
		
		
	}
	@RequestMapping(value = "/student/issuanceOfTranscript", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView issuanceOfTranscript(ServiceRequestStudentPortal sr, HttpServletRequest request) {
		if (!checkSession(request)) {
			return new ModelAndView("jsp/login");
		}

		ModelAndView modelnView = new ModelAndView("jsp/serviceRequest/transcriptNew");
		String sapid = (String) request.getSession().getAttribute("userId");
		String shippingAddress = "";

		sr.setSapId(sapid);
		StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(sapid);
		sr.setAbcId(student.getAbcId());
		// by pass student before Jul2014 from SFDC call 
		String enrollmentYearAndMonth = student.getEnrollmentMonth()+","+student.getEnrollmentYear();
		 try{
			 SimpleDateFormat month_yearFormat = new SimpleDateFormat("MMM,yyyy");
			 SimpleDateFormat fulldateFormat = new SimpleDateFormat("dd-MM-yyyy");
			 
			 // format year and Month into date 
		     Date dateR = month_yearFormat.parse(enrollmentYearAndMonth);
		     String fullFormatedDate = fulldateFormat.format(dateR);
		     
		     Date enrollmentDate = fulldateFormat.parse(fullFormatedDate);
		     Date salesforceUseStartDate = fulldateFormat.parse("01-06-2014"); // byPass Date for Student
		     if(enrollmentDate.after(salesforceUseStartDate))
			 {
		    	 HashMap<String, String> getShippingAddressMapFromSalesforce = salesforceHelper
		 				.getShippingAddressOfStudent(sapid);
		    	 
		 		shippingAddress = makeCommaSeperatedShippingAddressFromMap(getShippingAddressMapFromSalesforce);
		 		student.setShippingAddress(shippingAddress);
			 }else{
				 student.setShippingAddress(student.getAddress());
			 }
		 }catch(Exception e){
//			 e.printStackTrace();
		 }
		 
		
		
		modelnView.addObject("sr", sr);
		modelnView.addObject("student", student);
		modelnView.addObject("charges", TRANSCRIPT_FEE);
		modelnView.addObject("extraChargePerCopy", EXTRA_TRANSCRIPT_FEE);
		return modelnView;
	}

	@RequestMapping(value = "/student/saveTranscriptRequest", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView saveTranscriptRequest(@ModelAttribute ServiceRequestStudentPortal sr, HttpServletRequest request,
			HttpServletResponse response, ModelMap model/*,RedirectAttributes ra*/) {
		if (!checkSession(request)) {
			return new ModelAndView("jsp/login");
		}
		StudentStudentPortalBean student = (StudentStudentPortalBean) request.getSession().getAttribute("student_studentportal");
		String program = student.getProgram();
		String programStructure = student.getPrgmStructApplicable();
		String isLateral = student.getIsLateral();
		int noOfSubjectsToClearProgram = 0;
		String sapid = (String) request.getSession().getAttribute("userId");
		
		PAYMENTS_LOGGER.info("{} in saveTranscriptRequest", sapid);
		
		if("Yes".equals(sr.getWantAtAddress())){
			sr.setIssued("Y");
			sr.setModeOfDispatch("Courier");
		}else{
			setError(request, "Transcript at my address is not selected");
			ModelAndView m = new ModelAndView("jsp/serviceRequest/transcriptNew");
			m.addObject("sr", sr);
			return m;
		}
		String programType = "";

		int numberOfMarkEntries = serviceRequestDao.getNumberOfMarkEntries(student.getSapid());
		//If student does not have any marks against his sapid throw error//	
		if (numberOfMarkEntries == 0) {
			setError(request, "You donot have any mark entries.");
			ModelAndView m = new ModelAndView("jsp/serviceRequest/transcript");
			m.addObject("sr", sr);
			return m;
		}
		sr.setDescription(sr.getServiceRequestType() + " for student " + sapid);
		sr.setTranStatus(ServiceRequestStudentPortal.TRAN_STATUS_INITIATED);
		sr.setCategory("Exam");
		sr.setCreatedBy(sapid);
		sr.setLastModifiedBy(sapid);
		sr.setSrAttribute("");
		sr.setInformationForPostPayment(sapid);// So that it is used post
		sr.setDevice("WebApp");
		// checking if student applied for more than one transcript or not
		if("".equals(sr.getNoOfCopies()))
		{
			sr.setNoOfCopies("0");
		}
		// payment for follow up steps
		populateServiceRequestObject(sr, request);
		serviceRequestDao.insertServiceRequest(sr);
		//fillPaymentParametersInMap(model, student, sr);
		String requestId = sr.getTrackId();
		
		return paymentHelper.populateGoToGateway(sr, student);
	}


	

	@RequestMapping(value = "/student/checkFinalCertificateEligibility", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView checkFinalCertificateEligibility(@ModelAttribute ServiceRequestStudentPortal sr,
			HttpServletRequest request) {
		if (!checkSession(request)) {
			return new ModelAndView("jsp/login");
		}
		sr.setWantAtAddress("No");
		String sapid = (String) request.getSession().getAttribute("userId");
		sr.setSapId(sapid);
		StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(sapid);
		List<String> waivedOffSubjects = (List<String>) request.getSession().getAttribute("waivedOffSubjects");
		ArrayList<String> applicableSubjects = new ArrayList<>();
		applicableSubjects = serviceRequestDao.getApplicableSubject(student.getPrgmStructApplicable(), student.getProgram());
		try{
		applicableSubjects.removeAll(waivedOffSubjects);
		}catch(Exception e){
			//Do nothing
		}
		String program = student.getProgram();
		String programStructure = student.getPrgmStructApplicable();
		String isLateral = student.getIsLateral();
		/*String enrollmentYear = student.getEnrollmentYear();
		String enrollmentMonth = student.getEnrollmentMonth();*/
		int noOfSubjectsToClearProgram = 0;
		boolean lateralStudent = false;
		
		HashMap<String,ProgramsStudentPortalBean> programInfoList = serviceRequestDao.getProgramDetails();
		//ProgramsBean bean = programInfoList.get(program+"-"+programStructure);
		ProgramsStudentPortalBean bean = programInfoList.get(student.getConsumerProgramStructureId());
		/*Commented by Steffi on 7/28/2018
		 * 
		 * 
		if ("Online".equals(student.getExamMode())) {
			// Online students
			if (program.startsWith("P")) {
				noOfSubjectsToClearProgram = 24;
			} else if (program.startsWith("D")) {
				noOfSubjectsToClearProgram = 12;
			} else if (program.startsWith("C")) {
				noOfSubjectsToClearProgram = 5;
			} 
			if ((program.equalsIgnoreCase("CDM") || program.equalsIgnoreCase("CITM - ET") || program.equalsIgnoreCase("COM") || program.equalsIgnoreCase("CITM - DB") || program.equalsIgnoreCase("CITM - ES") )&& enrollmentYear.equals("2017") && enrollmentMonth.equals("Jul") ){
				noOfSubjectsToClearProgram = 6;
			}
			if (program.equalsIgnoreCase("CPM") && enrollmentYear.equals("2017") && enrollmentMonth.equals("Jul") ){
				noOfSubjectsToClearProgram = 2;
			}
			
		    } else {
			// Offline students
		    	if (program.equalsIgnoreCase("PGDHRM")&& enrollmentYear.equals("2009") && enrollmentMonth.equals("Jul") ){
				noOfSubjectsToClearProgram = 19;
			}
			else if (program.startsWith("P")) {
				noOfSubjectsToClearProgram = 20;
			} else if (program.startsWith("D")) {
				noOfSubjectsToClearProgram = 10;
			} else if (program.startsWith("C")) {
				noOfSubjectsToClearProgram = 5;
			}
			if (program.equalsIgnoreCase("CDM")&& enrollmentYear.equals("2017") && enrollmentMonth.equals("Jul") ){
				noOfSubjectsToClearProgram = 6;
			}
		}*/
		noOfSubjectsToClearProgram = Integer.parseInt(bean.getNoOfSubjectsToClear().trim());	
		int numberOfsubjectsCleared = serviceRequestDao.getNumberOfsubjectsCleared(sapid, lateralStudent);
		if ("Y".equals(isLateral)) {
			lateralStudent = true;
			//noOfSubjectsToClearProgram = noOfSubjectsToClearProgram / 2;
			noOfSubjectsToClearProgram =  Integer.parseInt(bean.getNoOfSubjectsToClearLateral().trim());
		}

		if (numberOfsubjectsCleared != noOfSubjectsToClearProgram) {
			noOfSubjectsToClearProgram = applicableSubjects.size();
		}
		
		if (numberOfsubjectsCleared != noOfSubjectsToClearProgram) {
			setError(request, "You have not yet cleared all subjects!");
			return new ModelAndView("jsp/serviceRequest/selectSR");
		} else {
			ModelAndView modelnView = new ModelAndView("jsp/serviceRequest/diplomaConfirmation");
			modelnView.addObject("sr", sr);
			modelnView.addObject("usersapId",sr.getSapId());
			int diplomaIssuedCount = serviceRequestDao.getDiplomaIssuedCount(sr);
			if (diplomaIssuedCount >= 1) {
				modelnView.addObject("charges", SECOND_DIPLOMA_FEE);
				modelnView.addObject("duplicateDiploma", "true");
			} else {
				modelnView.addObject("charges", 0);
				modelnView.addObject("duplicateDiploma", "false");
			}

			return modelnView;
		}
	}

	// Added by Vikas 01/08/2016//
	@RequestMapping(value = "/student/feedback", method = { RequestMethod.GET, RequestMethod.POST })
	public String feebackPage(@ModelAttribute FeedbackBean fb, Model m, HttpServletRequest request) {
		return "jsp/support/feedback";
	}

	@RequestMapping(value = "/student/saveFeedBack", method = { RequestMethod.GET, RequestMethod.POST })
	public String saveFeedBack(@ModelAttribute FeedbackBean fb, Model m, HttpServletRequest request) {
		StudentStudentPortalBean studentFeedback = (StudentStudentPortalBean) request.getSession().getAttribute("student_studentportal");
		String sapid = (String) request.getSession().getAttribute("userId");
		fb.setCategory((String) request.getParameter("category"));
		fb.setRating((String) request.getParameter("rating"));
		fb.setLastModifiedBy(sapid);
		serviceRequestDao.saveFeedBack(fb, studentFeedback);
		request.setAttribute("success", "true");
		request.setAttribute("successMessage", "Thank you for your valuable Suggestion");
		return "jsp/support/feedback";
	}

	// end//

	
	
	// Save Diploma request for Free if student is requesting that for the first 
	// time
	@RequestMapping(value = "/student/saveFinalCertificateRequest", method = { RequestMethod.POST })
	public ModelAndView saveFinalCertificateRequest(@ModelAttribute ServiceRequestStudentPortal sr, Model m,
			HttpServletRequest request, @RequestParam(required = true) MultipartFile nameOnCertificateDoc,
			ModelMap model) {

		if (!checkSession(request)) {
			return new ModelAndView("jsp/login");
		}
		sr.setSapId((String) request.getSession().getAttribute("userId")); 
		sr.setCourierAmount((String)request.getSession().getAttribute("courierAmount"));
		sr = servReqServ.saveFinalCertificateRequest(sr, nameOnCertificateDoc);
		request.getSession().setAttribute("sr", sr);
		m.addAttribute("sr", sr);
		return new ModelAndView("jsp/serviceRequest/srCreated");
	}


//	@RequestMapping(value = "/m/saveFinalCertificateAndPaymentWithfile", method = RequestMethod.POST, headers = "content-type=multipart/form-data" )
//	public ResponseEntity<ServiceRequest> MsaveFinalCertificateAndPaymentWithfile(HttpServletRequest request,
//			MultipartFile indemnityBond,MultipartFile firCopy,ServiceRequest sr)throws Exception {
//	
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json");
//
//		ServiceRequest response = servReqServ.saveFinalCertificateAndPayment(sr,indemnityBond,firCopy);
//		if(response.getError() !=null) {
//			return new ResponseEntity<>(response,headers, HttpStatus.OK);
//		}
//		return new ResponseEntity<>(response,headers, HttpStatus.OK);
//	}

	
	// Requestd Certificate for second time, so save transaction initiated and
	// proceed to payment
	@RequestMapping(value = "/student/saveFinalCertificateAndPayment", method = { RequestMethod.POST })
	public ModelAndView saveFinalCertificateAndPayment(@ModelAttribute ServiceRequestStudentPortal sr, Model m,
			HttpServletRequest request, ModelMap model, @RequestParam(required = false) MultipartFile firCopy,
			@RequestParam(required = false) MultipartFile indemnityBond/*,RedirectAttributes ra*/) {
		if (!checkSession(request)) {
			return new ModelAndView("jsp/login");
		}
		
		if(!"Yes".equals(sr.getWantAtAddress())){
			setError(request, "Error: final certificate at my address is not checked");
			//return checkFinalCertificateEligibility(sr, request);
			return checkFinalCertificateEligibilitynew(sr, request);
		}

		try {
			sr.setSapId((String) request.getSession().getAttribute("userId"));
			PAYMENTS_LOGGER.info("{} in saveFinalCertificateAndPayment ", sr.getSapId());
			
			StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(sr.getSapId());
			sr.setDevice("WebApp");
			sr = servReqServ.saveFinalCertificateAndPayment(sr,indemnityBond,firCopy,sr.getAffidavit());
			sr.setIndemnityBond(null);
			sr.setFirCopy(null);
			sr.setAffidavit(null);
			request.getSession().setAttribute("courierAmount", sr.getCourierAmount());
			request.getSession().setAttribute("trackId", sr.getTrackId());
			request.getSession().setAttribute("amount", sr.getAmount());
			request.getSession().setAttribute("sr", sr);// Must store in session to
			m.addAttribute("sr", sr);
			
			String requestId = sr.getTrackId()+ RandomStringUtils.randomAlphanumeric(20);
			
			return paymentHelper.populateGoToGateway(sr, student);
			
		} catch (Exception e) {
			PAYMENTS_LOGGER.info("{} : error while saveFinalCertificateAndPayment : {}",request.getSession().getAttribute("userId"), getStackTraceAsString(e));
			setError(request, "Error trying to generate final certificate!");
			return checkFinalCertificateEligibilitynew(sr, request);
		}
		
	}
	
	
	
	// Steps to be done after payment is done for Certificate
	private void saveFinalCertificateRequestPostPayment(StudentStudentPortalBean student, ServiceRequestStudentPortal sr) {
		// This also gets called from ServiceRequestPaymentScheduler via
		// handlePostPaymentAction method
		serviceRequestDao.insertServiceRequestHistory(sr);// For keeping track
		// how many times
		// same request was
		// made so that next
		// time they can be
		// charged
	}

/*	@RequestMapping(value = "/checkMarksheetHistory", method = {RequestMethod.POST })
	public ModelAndView checkMarksheetHistory(@ModelAttribute ServiceRequest sr, HttpServletRequest request) {
		if (!checkSession(request)) {
			return new ModelAndView("jsp/login");
		}
		ModelAndView modelnView = new ModelAndView("jsp/serviceRequest/marksheetSummary");
		try {
			
			String sapId = (String) request.getSession().getAttribute("userId");
			sr.setSapId(sapId);
			
			ServiceRequest result =  servReqServ.checkMarksheetHistory(sr,request);
			
			StudentBean student = (StudentBean) request.getSession().getAttribute("student_studentportal");
			modelnView.addObject("student", student);
			
			if(result.getError() !=null) {
				setError(request,sr.getError());
				modelnView = new ModelAndView("jsp/serviceRequest/marksheet");
				modelnView.addObject("yearList", yearList);
				modelnView.addObject("sr", result);
				return modelnView;
			}
			
			request.getSession().setAttribute("marksheetDetailAndAmountToBePaidList", result.getMarksheetDetailAndAmountToBePaidList());
			request.getSession().setAttribute("courierAmount", result.getCourierAmount());
			modelnView.addObject("sr", result);
			
			
		} catch (Exception e) {
//			e.printStackTrace();
		}
		return modelnView;
	}*/
	

//	@RequestMapping(value = "/m/srFeeResponse", method = { RequestMethod.GET, RequestMethod.POST })
//	public String mSrFeeResponse(HttpServletRequest request, HttpServletResponse respnse, ModelMap model,
//			Model m) {
//		
//		ServiceRequest sr = paymentHelper.CreateResponseBean(request);
//		request.getSession().setAttribute("amount", sr.getAmount());
//		
//		StudentBean student = (StudentBean)request.getSession().getAttribute("student_studentportal");
//    	CertificateInterface certificate = certificateFactory.getProductType(CertificateFactory.ProductType.valueOf(sr.getProductType())); 
//
//    	try {
//			sr = certificate.srFeeResponse(sr, student, request,respnse, model,m);
//			return "redirect:"+ sr.getReturn_url();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return "redirect:"+  "ssoservices/mbax/sRPaymentSuccess?status=error&message="+e.getMessage();
//		}
//		
//	
//
//
//	}

	
	@RequestMapping(value = "/student/checkMarksheetHistory", method = {RequestMethod.POST })
	public ModelAndView checkMarksheetHistory(@ModelAttribute ServiceRequestStudentPortal sr, HttpServletRequest request) {
		if (!checkSession(request)) {
			return new ModelAndView("jsp/login");
		}
		ModelAndView modelnView = new ModelAndView("jsp/serviceRequest/marksheetSummaryNew");
		try {
			
			String serviceRequestType = sr.getServiceRequestType();
			//boolean paymentRequired = false;
			String sapId = (String) request.getSession().getAttribute("userId");
			sr.setSapId(sapId);
			
			if("Yes".equals(sr.getWantAtAddress()) && sr.getWantAtAddress()!=null){
				sr.setIssued("Y");
				sr.setModeOfDispatch("Courier");
				String postalAddressForShipment = request.getParameter("postalAddress");
				sr.setPostalAddress(postalAddressForShipment);
				sr.setCourierAmount(100+"");
			}else{
				setError(request, "Marksheet at my address is not selected");
				modelnView = new ModelAndView("jsp/serviceRequest/marksheetNew");
				modelnView.addObject("yearList", yearList);
				modelnView.addObject("sr", sr);
				return modelnView;
			}
			sr.setPostalAddress((String)request.getParameter("postalAddress"));
			StudentStudentPortalBean student = (StudentStudentPortalBean) request.getSession().getAttribute("student_studentportal");
			//boolean isCertificate = isStudentOfCertificate(student.getProgram());
			boolean isCertificate = student.isCertificateStudent();
			
			modelnView.addObject("student", student);
			String resultDeclareDateOnline, resultDeclareDateOffline;
			String examMode = "";
			if ("Online".equalsIgnoreCase(student.getExamMode())) {
				examMode = "Online";

			} else {
				examMode = "Offline";

			}
			
			ArrayList<String> messageToBeShownForSemesterAppeared = serviceRequestDao.getSubjectsAppearedForSemesterMessageList(sr, examMode);//This will show an error message if the student has not appeared for a sem for given year and month//
			int subjectsAppeared = serviceRequestDao.getSubjectsAppeared(sr, examMode);//If he has given any exam//
			ArrayList<String> messageToShowForResultDeclareDate = serviceRequestDao.resultDeclaredMessage(sr);//Check if the result has been declared for the particular year,month and sem//
			ArrayList<String> marksheetsPrinted = serviceRequestDao.getMarksheetPrintedCount(sr);//Check number of marksheets issued and printed//
			
			//Check if result is declared.
			if (messageToShowForResultDeclareDate!=null && messageToShowForResultDeclareDate.size()>0) {
				String messageForResultDeclaration = "";
				for(String message : messageToShowForResultDeclareDate){
					if(!"Yes".equals(message)){
						messageForResultDeclaration = message + "\n";
					}
				}
				setError(request, messageForResultDeclaration+"Kindly click <a href=\"addSRForm\"> HERE </a> for raising service request.");
				modelnView = new ModelAndView("jsp/serviceRequest/marksheetNew");
				modelnView.addObject("yearList", yearList);
				modelnView.addObject("sr", sr);
				return modelnView;
			}
			//Check if marksheets are already printed and kept at LC//
			if(marksheetsPrinted!=null && marksheetsPrinted.size()>0){
				String messageForPrintedMarksheets = "";
				for(String message : marksheetsPrinted){
					
					messageForPrintedMarksheets = message + "\n";
					}
				setError(request,messageForPrintedMarksheets+" :- This marksheet is already printed, kindly connect with your Academic co-ordinator, if you wish to have this couriered then email us at <a href=\"#\"> ngasce@nmims.edu</a>. Please re-raise marksheet for other semesters by clicking on this <a href=\"addSRForm\"> LINK</a> ?");
				modelnView = new ModelAndView("jsp/serviceRequest/marksheetNew");
				modelnView.addObject("yearList", yearList);
				modelnView.addObject("sr", sr);
				return modelnView;
				}
				
				
				
			
			
			//Will check if any subjects were appeared by student//
			if (subjectsAppeared == 0) {
				setError(request, "There are no marks entries with respect to your SAPID"
						+ " Exam for you OR Results not yet declared.Kindly click <a href=\"addSRForm\"> HERE </a> for raising service request.");
				modelnView = new ModelAndView("jsp/serviceRequest/marksheet");
				modelnView.addObject("yearList", yearList);
				modelnView.addObject("sr", sr);
				return modelnView;
			}
			//Print the message for the semester appeared or not appeared//
			if(messageToBeShownForSemesterAppeared!=null && messageToBeShownForSemesterAppeared.size()>0){
				String messageToBeShown = "";
				for(String message : messageToBeShownForSemesterAppeared){
					messageToBeShown = message +"\n";
				}
				setError(request,messageToBeShown+"Kindly click <a href=\"addSRForm\"> HERE </a> for raising service request.");
				modelnView = new ModelAndView("jsp/serviceRequest/marksheet");
				ArrayList<PassFailBean> yearMonthList = servReqServ.getPassedYearMonthList(student);
				modelnView.addObject("yearMonthList",yearMonthList); 
				modelnView.addObject("yearList", yearList);
				modelnView.addObject("sr", sr);
				return modelnView;
			}
			
			String courierAmount = isCertificate ? generateAmountBasedOnCriteria(sr.getCourierAmount(),"GST") : sr.getCourierAmount();
			ArrayList<ServiceRequestStudentPortal> marksheetDetailAndAmountToBePaidList = serviceRequestDao.listOfMarksheetDetailsAndAmountToBePaid(sr,SECOND_MARKSHEET_FEE_PER_SUBJECT,request,isCertificate);//Map the service bean with semester and set in session.Returns a map which will show the description on marksheet summary.//
			
			request.getSession().setAttribute("marksheetDetailAndAmountToBePaidList", marksheetDetailAndAmountToBePaidList);
			request.getSession().setAttribute("courierAmount", courierAmount);
			modelnView.addObject("sr", sr);
		} catch (Exception e) {
//			e.printStackTrace();
		}
		return modelnView;
	}


/*		@RequestMapping(value="/confirmMarksheetRequest",method={RequestMethod.POST})
		public ModelAndView confirmMarksheetRequest(HttpServletRequest request){
			ModelAndView modelnView = new ModelAndView("jsp/serviceRequest/marksheetConfirmation");
			
			ArrayList<ServiceRequest> marksheetDetailAndAmountToBePaidList = (ArrayList<ServiceRequest>)request.getSession().getAttribute("marksheetDetailAndAmountToBePaidList");
			
			ServiceRequest sr = new ServiceRequest();
			sr.setMarksheetDetailAndAmountToBePaidList(marksheetDetailAndAmountToBePaidList);
			ServiceRequest response =  servReqServ.confirmMarksheetRequest(sr);
			
			modelnView.addObject("sr", response);
			modelnView.addObject("charges", request.getSession().getAttribute("amount"));
			modelnView.addObject("duplicateMarksheet", sr.getDuplicateMarksheet());
			return modelnView;
		}*/
	@RequestMapping(value="/student/confirmMarksheetRequest",method={RequestMethod.POST})
	public ModelAndView confirmMarksheetRequest(HttpServletRequest request){
		ModelAndView modelnView = new ModelAndView("jsp/serviceRequest/marksheetConfirmationNew");
		boolean isDuplicate = false;
		ArrayList<ServiceRequestStudentPortal> marksheetDetailAndAmountToBePaidList = (ArrayList<ServiceRequestStudentPortal>)request.getSession().getAttribute("marksheetDetailAndAmountToBePaidList");
		
		ServiceRequestStudentPortal sr = new ServiceRequestStudentPortal();
		//Running loop to check if any marksheet is duplicate and then set model attribute for page//
		for(int i =0;i<marksheetDetailAndAmountToBePaidList.size();i++){
			ServiceRequestStudentPortal serviceBean = marksheetDetailAndAmountToBePaidList.get(i);
			
			if("Duplicate".equals(serviceBean.getAdditionalInfo1())){
				isDuplicate = true; //Set to duplicate for showing doucument on marksheet Confirmation page//
			}
		}
		modelnView.addObject("sr", sr);
		modelnView.addObject("charges", request.getSession().getAttribute("amount"));
		modelnView.addObject("duplicateMarksheet", isDuplicate);
		modelnView.addObject("sapid",request.getSession().getAttribute("userId"));
		modelnView.addObject("reqest_type","Issuance of Marksheet");
		return modelnView;
	}
 
//	@CrossOrigin(origins = "http://localhost:3000")
//	@RequestMapping(value = "/m/saveMarksheetRequest", method = RequestMethod.POST,consumes = "application/json")
//	public ResponseEntity<ServiceRequest> MsaveMarksheetRequest(HttpServletRequest request,
//			@RequestBody ServiceRequest sr)throws Exception {
//		
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json");
//		ServiceRequest response =  servReqServ.saveMarksheetRequest(sr,request);
//		
//		return new ResponseEntity<>(response,headers, HttpStatus.OK);
//	}
	
//	to be deleted, api shifted to rest controller
//	@RequestMapping(value = "/m/saveMarksheetRequest", method = RequestMethod.POST,consumes = "application/json")
//	public ResponseEntity<ServiceRequest> MsaveMarksheetRequest(HttpServletRequest request,@RequestBody ServiceRequest sr)throws Exception {
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json");
//		ServiceRequest response =  servReqServ.saveMarksheetRequest(sr,request);
//		
//		return new ResponseEntity<>(response,headers, HttpStatus.OK);
//	}
	
/*	@RequestMapping(value = "/saveMarksheetRequest", method = { RequestMethod.POST })
	public ModelAndView saveMarksheetRequest( @ModelAttribute ServiceRequest sr,Model m, HttpServletRequest request,HttpServletResponse response,
			ModelMap model) {

		if (!checkSession(request)) {
			return new ModelAndView("jsp/login");
		}
		
		ModelAndView modelAndView = new ModelAndView("jsp/serviceRequest/srCreated");
		
		String courierAmount = (String)request.getSession().getAttribute("courierAmount");
		sr.setCourierAmount(courierAmount);
		
		ServiceRequest result =  servReqServ.saveMarksheetRequest(sr,request);
		
		
		modelAndView.addObject("sr", result);
		MailSender mailer = (MailSender) act.getBean("mailer");
		//mailer.sendSREmail(sr, student);
		return modelAndView;
	}*/
	
	@RequestMapping(value = "/student/saveMarksheetRequest", method = { RequestMethod.POST })
	public ModelAndView saveMarksheetRequest( @ModelAttribute ServiceRequestStudentPortal sr,Model m, HttpServletRequest request,HttpServletResponse response,
			ModelMap model) {
		if (!checkSession(request)) {
			return new ModelAndView("jsp/login");
		}
		ModelAndView modelAndView = new ModelAndView("jsp/serviceRequest/srCreated");
		ArrayList<ServiceRequestStudentPortal> marksheetDetailAndAmountToBePaidList = (ArrayList<ServiceRequestStudentPortal>)request.getSession().getAttribute("marksheetDetailAndAmountToBePaidList");
		ArrayList<ServiceRequestStudentPortal> listOfServiceRequestInserted = new ArrayList<ServiceRequestStudentPortal>();
		String CourierAmount = (String)request.getSession().getAttribute("courierAmount");
		//Method added to check if the amount from page and the actual amount to paid are matching//
		/*boolean checkAmountIsMatching = checkAmountIsMatching(marksheetDetailAndAmountToBePaidList,sr.getAmount(),CourierAmount);
		
		if(checkAmountIsMatching == false){
			setError(request,"Amount is a Mismatch. Kindly click <a href=\"addSRForm\">here </a>");
			ModelAndView modelNView = new ModelAndView("jsp/serviceRequest/marksheetConfirmation");
			modelNView.addObject("sr", sr);
			return modelNView;
		}*/
		for(ServiceRequestStudentPortal serviceRequest : marksheetDetailAndAmountToBePaidList){
			
			int countOfRecords = serviceRequestDao.getMarksheetIssuedCount(serviceRequest);
			if(countOfRecords >0){
				setError(request,"Free Marksheet request already received for "+serviceRequest.getMonth()+"-"+serviceRequest.getYear()+" : Sem : "+serviceRequest.getSem());
				return selectSRForm(sr, request, response, m);
			}
		}
		
		for(int i = 0;i<marksheetDetailAndAmountToBePaidList.size();i++){
			ServiceRequestStudentPortal service = new ServiceRequestStudentPortal();
			service = insertMarksheetServiceRequest(marksheetDetailAndAmountToBePaidList.get(i),request,marksheetDetailAndAmountToBePaidList.size());//Passing size to method to flag marksheet only if the size is greater than 1//
			listOfServiceRequestInserted.add(service);
			
		}
		
		if(listOfServiceRequestInserted!=null && listOfServiceRequestInserted.size()>0){
			ServiceRequestStudentPortal srBean = new ServiceRequestStudentPortal();
			srBean.setServiceRequestType(listOfServiceRequestInserted.get(0).getServiceRequestType());
			StringBuilder serviceDescription = new StringBuilder();
			StringBuilder serviceRequestId = new StringBuilder();
			
			if (listOfServiceRequestInserted.size() > 1) {
				for(ServiceRequestStudentPortal service : listOfServiceRequestInserted){
					serviceDescription.append(service.getDescription()).append(" ,");
					serviceRequestId.append(service.getId()).append(" ,");
				}
				srBean.setDescriptionList(serviceDescription.toString().substring(0, serviceDescription.toString().length()-1));
				srBean.setSrIdList(serviceRequestId.toString().substring(0, serviceRequestId.toString().length()-1));
				
			}else{
				for(ServiceRequestStudentPortal service : listOfServiceRequestInserted){
					serviceDescription.append(service.getDescription()) ;
					serviceRequestId.append(String.valueOf(service.getId())) ;
				}
				srBean.setDescription(serviceDescription.toString());
				srBean.setId(Long.parseLong(serviceRequestId.toString()));
			}
			modelAndView.addObject("sr", srBean);
		}
		
		
		MailSender mailer = (MailSender) act.getBean("mailer");
		//mailer.sendSREmail(sr, student);
		return modelAndView;
	}
	
	private boolean checkAmountIsMatching(ArrayList<ServiceRequestStudentPortal> srList,String amountToBePaid,String CourierAmount){
		double srAmount = Double.parseDouble(amountToBePaid),countOfRecords;
		double applicableAmount = Double.parseDouble(CourierAmount);//Add courier amount at start
	    for(ServiceRequestStudentPortal sr : srList){
	    	countOfRecords = serviceRequestDao.getMarksheetIssuedCount(sr);
	    	if(countOfRecords > 0){
	    		switch(sr.getServiceRequestType()){
	    		case "Issuance of Final Certificate":
	    			applicableAmount = applicableAmount + DUPLICATE_CERTIFICATE_FEE;
	    			break;
	    		case "Issuance of Marksheet":
	    			applicableAmount = applicableAmount + SECOND_MARKSHEET_FEE_PER_SUBJECT;
	    			break;
	    		}
	    	}
	    }
	    if(srAmount == applicableAmount){
	    	return true;
	    }else{
	    	return false;
	    }
		
	}
	
	public ServiceRequestStudentPortal insertMarksheetServiceRequest(ServiceRequestStudentPortal sr,HttpServletRequest request,int numberOfMarksheetRequests){
		String sapid = sr.getSapId();
		StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(sapid);
		sr.setDescription(sr.getServiceRequestType() + " for student " + sapid + " for Exam " + sr.getMonth() + "-"
				+ sr.getYear() + " & Sem : " + sr.getSem());
		sr.setTranStatus(ServiceRequestStudentPortal.TRAN_STATUS_FREE);
		sr.setCategory("Exam");
		sr.setSapId(sapid);
		sr.setRequestStatus(ServiceRequestStudentPortal.REQUEST_STATUS_SUBMITTED);
		sr.setCreatedBy(sapid);
		sr.setLastModifiedBy(sapid);
		sr.setSrAttribute("Multiple Marksheet");
		
		if(sr.getModeOfDispatch() ==null || "NO".equalsIgnoreCase(sr.getModeOfDispatch()))
		{
			sr.setModeOfDispatch("LC");
		}
		serviceRequestDao.insertServiceRequest(sr);
		serviceRequestDao.insertServiceRequestHistory(sr);// For keeping track
		return sr;
	}
	
	

	
	
//	@RequestMapping(value = "/m/proceedToPaymentGatewaySr", method = {RequestMethod.GET, RequestMethod.POST})
//	public ModelAndView proceedToPaymentGatewaySr(HttpServletRequest request,String sapId,String serviceRequestId,String paymentOptionName,  HttpServletResponse response) {
//	
//ModelMap model = new ModelMap();
//		StudentBean student = serviceRequestDao.getSingleStudentsData(sapId);
//		
//		ServiceRequest service = serviceRequestDao.findById(serviceRequestId);
//		String requestId = service.getTrackId();
//		service.setIsMobile(true);
//		
//		request.getSession().setAttribute("userId",sapId);
//		request.getSession().setAttribute("trackId",requestId);
//		request.getSession().setAttribute("sr",service);
//		request.getSession().setAttribute("student_studentportal",student);
//		request.getSession().setAttribute("amount", service.getAmount());
//		
//		if(paymentOptionName.equals("hdfc")) {
//			request.getSession().setAttribute("SECURE_SECRET", SECURE_SECRET);
//			request.getSession().setAttribute("paymentOption","hdfc");
//			fillPaymentParametersInMap(model, student, service);
//			return new ModelAndView(new RedirectView("/studentportal/pay"), model);
//		}
//		
//		String paymentOption = paymentOptionName; 
//		ModelAndView mv = new ModelAndView(paymentOption + "Pay");	//set payment jsp file name;
//		//PaymentHelper paymentHelper = new PaymentHelper();
//		String checkSum = paymentHelper.generateCommonCheckSum(service, student, requestId,paymentOption);
//		if(checkSum != "true") {
//			setError(request, "Error: " + checkSum);
//			//return checkMarksheetHistory(service, request);
//		}
//		request.getSession().setAttribute("paymentOption",paymentOption);
//
////		String sapid = (String) request.getSession().getAttribute("userId");
////		String trackId = (String) request.getSession().getAttribute("trackId");
////		ServiceRequest sr = (ServiceRequest) request.getSession().getAttribute("sr");
//		
//		
//		mv = paymentHelper.setCommonModelData(mv, service, student, requestId,paymentOption);
////		mv.addObject("CALLBACK_URL",SR_RETURN_URL_MOBILE);
//		return mv;
//		
//	}
	
	
//	to be deleted, api shifted to rest controller
//	@CrossOrigin(origins = "http://localhost:3000")
//	@RequestMapping(value = "/m/saveMarksheetAndPaymentForMBAWX", method = RequestMethod.POST, consumes="application/json", produces="application/json")
//	public ResponseEntity<ServiceRequest> MsaveMarksheetAndPaymentForMBAWX(HttpServletRequest request,
//			 @RequestBody ServiceRequest sr)throws Exception {
//		
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json");
//		
//		ServiceRequest response =  servReqServ.saveMarksheetAndPaymentForMBAWX(sr,request);
//		
//		return new ResponseEntity<>(response,headers, HttpStatus.OK);
//		
//	}
	
	
//	to be deleted, api shifted to rest controller
//	@CrossOrigin(origins = "http://localhost:3000")
//	@RequestMapping(value = "/m/saveMarksheetAndPaymentDocsForMBAWX", method = RequestMethod.POST,headers = "content-type=multipart/form-data")
//	public ResponseEntity<ServiceRequest> MsaveMarksheetAndPaymentDocsForMBAWX(HttpServletRequest request,
//			 ServiceRequest sr,  MultipartFile firCopy,
//			 MultipartFile indemnityBond, @RequestParam Integer listSize )throws Exception {
//		
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json");
//		
//		
//		ServiceRequest response =  servReqServ.saveMarksheetAndPaymentDocsForMBAWX(sr,request,firCopy,indemnityBond,listSize);
//		
//		return new ResponseEntity<>(response,headers, HttpStatus.OK);
//		
//	}
	// Requestd same marksheet for second time, so save transaction initiated
	// and proceed to payment
	@RequestMapping(value = "/student/saveMarksheetAndPayment", method = {RequestMethod.POST})
	public ModelAndView saveMarksheetAndPayment(@ModelAttribute ServiceRequestStudentPortal sr, Model m, HttpServletRequest request,
			ModelMap model, @RequestParam(required = false) MultipartFile firCopy,
			@RequestParam(required = false) MultipartFile indemnityBond/*,RedirectAttributes ra*/) {
		if (!checkSession(request)) {
			return new ModelAndView("jsp/login");
		}
		
		// Taking address from postal address since particular sapid 77114000467 was failing
		//String studentAddress = request.getParameter("postalAddress");
		StudentStudentPortalBean student = (StudentStudentPortalBean) request.getSession().getAttribute("student_studentportal");
		//student.setAddress(studentAddress);// Setting address since it was failing for case 77114000467
		sr.setSapId(student.getSapid());
		PAYMENTS_LOGGER.info("{} in saveMarksheetAndPayment ", sr.getSapId());
		
		sr.setTotalAmountToBePayed((String)request.getSession().getAttribute("amount"));
		sr.setMarksheetDetailAndAmountToBePaidList((ArrayList<ServiceRequestStudentPortal>)request.getSession().getAttribute("marksheetDetailAndAmountToBePaidList"));
		sr.setCourierAmount((String)request.getSession().getAttribute("courierAmount"));
		sr.setDevice("WebApp");
		ServiceRequestStudentPortal service =  servReqServ.saveMarksheetAndPayment(sr,firCopy,indemnityBond);
		sr.setTrackId(service.getTrackId());
		sr.setDescription(service.getDescription());
		String requestId = service.getTrackId();
		/*return proceedToPayOptions(model,requestId,ra);*/
		//return new ModelAndView(new RedirectView("pay"), model);
		request.getSession().setAttribute("trackId", service.getTrackId());
		request.getSession().setAttribute("sr", service);
		
		return paymentHelper.populateGoToGateway(sr, student);
	}


	/*@RequestMapping(value = "/checkMarksheetHistory", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView checkMarksheetHistory(@ModelAttribute ServiceRequest sr, HttpServletRequest request) {
		if (!checkSession(request)) {
			return new ModelAndView("jsp/login");
		}
		ModelAndView modelnView = new ModelAndView("jsp/serviceRequest/marksheetConfirmation");
		try {

			String serviceRequestType = sr.getServiceRequestType();
			sr.setWantAtAddress("No");
			String sapId = (String) request.getSession().getAttribute("userId");
			sr.setSapId(sapId);

			StudentBean student = (StudentBean) request.getSession().getAttribute("student_studentportal");
			
			
			
			modelnView.addObject("student", student);
			String resultDeclareDateOnline, resultDeclareDateOffline;
			String examMode = "";
			if ("Jul2014".equalsIgnoreCase(student.getPrgmStructApplicable())) {
				examMode = "Online";

			} else {
				examMode = "Offline";

			}

			int subjectsAppeared = serviceRequestDao.getSubjectsAppeared(sr, examMode);
			int subjectsAppearedForSemester = serviceRequestDao.getSubjectsAppearedForSemester(sr, examMode);
			String messageToShowForResultDeclareDate = serviceRequestDao.isResultDeclared(sr, examMode);

			if (subjectsAppeared == 0) {
				setError(request, "There are no marks entries for , Semester: " + sr.getSem()
						+ " Exam for you OR Results not yet declared.");
				modelnView = new ModelAndView("jsp/serviceRequest/marksheet");
				modelnView.addObject("yearList", yearList);
				modelnView.addObject("sr", sr);
				return modelnView;
			}
			if (subjectsAppearedForSemester == 0) {
				setError(request, "You have not appeared for Semester " + sr.getSem() + " in Year " + sr.getYear()
						+ " and month " + sr.getMonth());
				modelnView = new ModelAndView("jsp/serviceRequest/marksheet");
				modelnView.addObject("yearList", yearList);
				modelnView.addObject("sr", sr);
				return modelnView;
			}
			if (!"Yes".equals(messageToShowForResultDeclareDate)) {
				setError(request, messageToShowForResultDeclareDate);
				modelnView = new ModelAndView("jsp/serviceRequest/marksheet");
				modelnView.addObject("yearList", yearList);
				modelnView.addObject("sr", sr);
				return modelnView;
			}

			int marksheetIssuedCount = serviceRequestDao.getMarksheetIssuedCount(sr);
			if (marksheetIssuedCount >= 1) {
				modelnView.addObject("charges", SECOND_MARKSHEET_FEE_PER_SUBJECT);
				modelnView.addObject("duplicateMarksheet", "true");
			} else {
				modelnView.addObject("charges", 0);
			}

			modelnView.addObject("yearList", yearList);
			modelnView.addObject("sr", sr);
		} catch (Exception e) {
//			e.printStackTrace();
		}
		return modelnView;
	}

	// Save marksheet request for Free if student is requesting that for the
	// first time
	@RequestMapping(value = "/saveMarksheetRequest", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView saveMarksheetRequest(@ModelAttribute ServiceRequest sr, Model m, HttpServletRequest request,
			ModelMap model) {

		if (!checkSession(request)) {
			return new ModelAndView("jsp/login");
		}
		if ("Yes".equals(sr.getWantAtAddress())) {
			// if student goes back from payment page to SR page it does not ask
			// for payment and takes request for shipping at his address without
			// payment
			// Added this check to avoid that issue
			sr.setIssued("Y");
			setError(request, "Please proceed to payment if you wish to receive Marksheet at your address");
			return checkMarksheetHistory(sr, request);
		}else{
			sr.setIssued("N");
		}
		String sapid = (String) request.getSession().getAttribute("userId");
		StudentBean student = serviceRequestDao.getSingleStudentsData(sapid);
		sr.setDescription(sr.getServiceRequestType() + " for student " + sapid + " for Exam " + sr.getMonth() + "-"
				+ sr.getYear() + " & Sem : " + sr.getSem());
		sr.setTranStatus(ServiceRequest.TRAN_STATUS_FREE);
		sr.setCategory("Exam");
		sr.setSapId(sapid);
		sr.setRequestStatus(ServiceRequest.REQUEST_STATUS_SUBMITTED);
		sr.setCreatedBy(sapid);
		sr.setLastModifiedBy(sapid);

		serviceRequestDao.insertServiceRequest(sr);
		serviceRequestDao.insertServiceRequestHistory(sr);// For keeping track
		// how many times
		// same request was
		// made so that next
		// time they can be
		// charged
		request.getSession().setAttribute("sr", sr);
		m.addAttribute("sr", sr);

		MailSender mailer = (MailSender) act.getBean("mailer");
		mailer.sendSREmail(sr, student);

		return new ModelAndView("jsp/serviceRequest/srCreated");
	}

	// Requestd same marksheet for second time, so save transaction initiated
	// and proceed to payment
	@RequestMapping(value = "/saveMarksheetAndPayment", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView saveMarksheetAndPayment(@ModelAttribute ServiceRequest sr, Model m, HttpServletRequest request,
			ModelMap model, @RequestParam(required = false) MultipartFile firCopy,
			@RequestParam(required = false) MultipartFile indemnityBond) {
		if (!checkSession(request)) {
			return new ModelAndView("jsp/login");
		}

		String studentAddress = request.getParameter("postalAddress");// Taking
																		// address
																		// from
																		// postal
																		// address
																		// since
																		// particular
																		// sapid
																		// 77114000467
																		// was
																		// failing//
		StudentBean student = (StudentBean) request.getSession().getAttribute("student_studentportal");
		student.setAddress(studentAddress);// Setting address since it was
											// failing for case 77114000467
		String sapid = student.getSapid();
		if("Yes".equals(sr.getWantAtAddress())){
			sr.setIssued("Y");
		}else{
			sr.setIssued("N");
		}
		
		String subject = request.getParameter("subject");
		sr.setDescription(sr.getServiceRequestType() + " for student " + sapid + " for Exam " + sr.getMonth() + "-"
				+ sr.getYear() + " & Sem : " + sr.getSem());
		sr.setTranStatus(ServiceRequest.TRAN_STATUS_INITIATED);
		sr.setCategory("Exam");
		sr.setCreatedBy(sapid);
		sr.setLastModifiedBy(sapid);
		sr.setInformationForPostPayment(sapid + "~" + sr.getYear() + "~" + sr.getMonth() + "~" + sr.getSem());// So
																												// that
																												// it
																												// is
																												// used
																												// post
		// payment for follow up
		// steps
		populateServiceRequestObject(sr, request);

		serviceRequestDao.insertServiceRequest(sr);
		request.getSession().setAttribute("sr", sr);// Must store in session to
		// be used in post payment
		m.addAttribute("sr", sr);

		if (firCopy != null) {
			ServiceRequestDocumentBean document = new ServiceRequestDocumentBean();
			document.setServiceRequestId(sr.getId());
			uploadFile(document, firCopy, sapid + "_FIR");

			if (document.getErrorMessage() == null) {
				document.setDocumentName("FIR for Duplicate Marksheet");
				serviceRequestDao.insertServiceRequestDocument(document);
				sr.setHasDocuments("Y");
				serviceRequestDao.updateDocumentStatus(sr);
			} else {
				setError(request, "Error in uploading document " + document.getErrorMessage());
				return checkMarksheetHistory(sr, request);
			}
		}

		if (indemnityBond != null) {
			ServiceRequestDocumentBean document = new ServiceRequestDocumentBean();
			document.setServiceRequestId(sr.getId());
			uploadFile(document, firCopy, sapid + "_Indemnity_Bond");

			if (document.getErrorMessage() == null) {
				document.setDocumentName("Indemnity Bond for Duplicate Marksheet");
				serviceRequestDao.insertServiceRequestDocument(document);
				sr.setHasDocuments("Y");
				serviceRequestDao.updateDocumentStatus(sr);
			} else {
				setError(request, "Error in uploading document " + document.getErrorMessage());
				return checkMarksheetHistory(sr, request);
			}
		}

		fillPaymentParametersInMap(model, student, sr);
		return new ModelAndView(new RedirectView("pay"), model);
	}*/

	// Steps to be done after payment is done for Marksheet
	private void saveMarksheetRequestPostPayment(StudentStudentPortalBean student, ServiceRequestStudentPortal sr) {
		// This also gets called from ServiceRequestPaymentScheduler via
		// handlePostPaymentAction method
		ArrayList<String> postPaymentData = new ArrayList<String>(
				Arrays.asList(sr.getInformationForPostPayment().split("~")));
		String sapId = postPaymentData.get(0);
		String year = postPaymentData.get(1);
		String month = postPaymentData.get(2);
		String sem = postPaymentData.get(3);

		sr.setYear(year);
		sr.setMonth(month);
		sr.setSem(sem);
		
		serviceRequestDao.insertServiceRequestHistory(sr);// For keeping track
		
		
		// how many times
		// same request was
		// made so that next
		// time they can be
		// charged
	}

	private ModelAndView teeRevaluationForm(ServiceRequestStudentPortal sr, Model m, HttpServletRequest request) {
		if (!checkSession(request)) {
			return new ModelAndView("jsp/login");
		}
		request.getSession().setAttribute("revalSubjects", null);// Reset to null if student goes back and decides to change subjects
		String serviceRequestType = sr.getServiceRequestType();
		ModelAndView modelnView = new ModelAndView("jsp/serviceRequest/teeRevaluationNew");
		String sapid = (String) request.getSession().getAttribute("userId");
		
		if (ServiceRequestStudentPortal.TEE_REVALUATION.equals(serviceRequestType)) {
			modelnView.addObject("charges", REVALUATION_FEE_PER_SUBJECT);
		} else if (ServiceRequestStudentPortal.OFFLINE_TEE_REVALUATION.equals(serviceRequestType)) {
			modelnView.addObject("charges", REVALUATION_FEE_PER_SUBJECT);
		
		} else if (ServiceRequestStudentPortal.PHOTOCOPY_OF_ANSWERBOOK.equals(serviceRequestType)) {
			modelnView = new ModelAndView("jsp/serviceRequest/photocopyOfAnswerbook");
			modelnView.addObject("charges", PHOTOCOPY_FEE_PER_SUBJECT);
		}

		StudentStudentPortalBean student = (StudentStudentPortalBean)request.getSession().getAttribute("student_studentportal");

		String mostRecentResultPeriod = "";
		List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();
		if ("Online".equals(student.getExamMode())) {
			mostRecentResultPeriod = serviceRequestDao.getMostRecentOnlineResultPeriod();
			studentMarksList = serviceRequestDao.getAStudentsMostRecentOnlineExamMarks(sapid);
		} else {
			mostRecentResultPeriod = serviceRequestDao.getMostRecentOfflineResultPeriod();
			studentMarksList = serviceRequestDao.getAStudentsMostRecentOfflineExamMarks(sapid);
		}
		
		//If the subjects applicable to the student for revaluation contains the subject Project, then check if the Project subject is marked with a Copy Case remark.
		//If the subject is marked as copyCase then remove the subject from the applicable subjects list, so that the student cannot apply for revaluation for the particular subject.
		if(ServiceRequestStudentPortal.TEE_REVALUATION.equals(serviceRequestType) 
			|| ServiceRequestStudentPortal.OFFLINE_TEE_REVALUATION.equals(serviceRequestType)) {
			
			List<StudentMarksBean> copyCaseProjectSubjectsList = studentMarksList.stream()
																				.filter(marksBean -> "Project".equals(marksBean.getSubject()) || "Module 4 - Project".equals(marksBean.getSubject()))
																				.filter(marksBean -> checkIfProjectCopyCase(marksBean.getSapid(), Integer.valueOf(marksBean.getYear()), marksBean.getMonth(), 
																															Integer.valueOf(marksBean.getSem()), marksBean.getSubject()))
																				.collect(Collectors.toList());
			
			studentMarksList.removeAll(copyCaseProjectSubjectsList);
			modelnView.addObject("copyCaseProjectSubjectsList", copyCaseProjectSubjectsList);
		}
		
		if (ServiceRequestStudentPortal.PHOTOCOPY_OF_ANSWERBOOK.equals(serviceRequestType)) {
			// Student cannot have photo copy of Project
			for (StudentMarksBean studentMarksBean : studentMarksList) {
				if ("Project".equals(studentMarksBean.getSubject())) {
					studentMarksList.remove(studentMarksBean);
					break;
				}
				if("Module 4 - Project".equals(studentMarksBean.getSubject())) {
					studentMarksList.remove(studentMarksBean);
					break;
				}
			}
		}

		modelnView.addObject("mostRecentResultPeriod", mostRecentResultPeriod);
		modelnView.addObject("studentMarksList", studentMarksList);
		modelnView.addObject("size", studentMarksList.size());
		return modelnView;
	}

	@RequestMapping(value = "/student/teeRevaluationConfirmation", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView teeRevaluationConfirmation(@ModelAttribute ServiceRequestStudentPortal sr, Model m,
			HttpServletRequest request) {
		if (!checkSession(request)) {
			return new ModelAndView("jsp/login");
		}	
		
		int revaluationSubjectCount = sr.getRevaluationSubjects().size(); //Getting the count Of subject After replacing "," to "|"
		sr.getRevaluationSubjects().replaceAll(s -> s.replace("|", ",")); //Replacing "|" to "," again for porper subject name
	
		request.getSession().setAttribute("revalSubjects", sr.getRevaluationSubjects());
		boolean isCertificate = (boolean)request.getSession().getAttribute("isCertificate");
		String serviceRequestType = sr.getServiceRequestType();
		ModelAndView modelnView = new ModelAndView("jsp/serviceRequest/teeRevaluationConfirmationNew");
		String sapid = (String) request.getSession().getAttribute("userId");
		// sapid = "77214002240";
		double amount = 0.0;
		if (sr.getRevaluationSubjects().size() == 0) {
			setError(request, "Please select at least one subject to proceed for Revaluation");
			return assignmentRevaluationForm(sr, m, request);
		} else {
			if (ServiceRequestStudentPortal.TEE_REVALUATION.equals(serviceRequestType)) {
				amount = revaluationSubjectCount * REVALUATION_FEE_PER_SUBJECT;
				if(isCertificate){
					amount = Double.parseDouble(generateAmountBasedOnCriteria(String.valueOf(amount), SERVICE_TAX_RULE));
				}
				
			} else if (ServiceRequestStudentPortal.OFFLINE_TEE_REVALUATION.equals(serviceRequestType)) {
				amount = revaluationSubjectCount * REVALUATION_FEE_PER_SUBJECT;
				if(isCertificate){
					amount = Double.parseDouble(generateAmountBasedOnCriteria(String.valueOf(amount), SERVICE_TAX_RULE));
				}
			} else if (ServiceRequestStudentPortal.PHOTOCOPY_OF_ANSWERBOOK.equals(serviceRequestType)) {
				amount = revaluationSubjectCount * PHOTOCOPY_FEE_PER_SUBJECT;
				if(isCertificate){
					amount = Double.parseDouble(generateAmountBasedOnCriteria(String.valueOf(amount), SERVICE_TAX_RULE));
				}
			}
			modelnView.addObject("amount", amount);
		
			
			sr.setAmount(amount + "");
		}

		String mostRecentResultPeriod = "";
		List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();
		StudentStudentPortalBean student = (StudentStudentPortalBean) request.getSession().getAttribute("student_studentportal");
		if ("Online".equals(student.getExamMode())) {
			mostRecentResultPeriod = serviceRequestDao.getMostRecentOnlineResultPeriod();
			studentMarksList = serviceRequestDao.getAStudentsMostRecentOnlineExamMarks(sapid);
		} else {
			mostRecentResultPeriod = serviceRequestDao.getMostRecentOfflineResultPeriod();
			studentMarksList = serviceRequestDao.getAStudentsMostRecentOfflineExamMarks(sapid);
		}

		List<StudentMarksBean> revaluationList = new ArrayList<>();
		for (StudentMarksBean studentMarksBean : studentMarksList) {
			if (sr.getRevaluationSubjects().contains(studentMarksBean.getSubject())) {
				revaluationList.add(studentMarksBean);
			}
		}
		
		//If the revaluation subjects submitted by the student contains the Project subject, then check if the Project subject is marked with a Copy Case remark.
		//If the subject is marked as copyCase then remove the subject from the revaluation subjects list, so that the student cannot apply for revaluation for the particular subject.
		if(ServiceRequestStudentPortal.TEE_REVALUATION.equals(serviceRequestType) || ServiceRequestStudentPortal.OFFLINE_TEE_REVALUATION.equals(serviceRequestType)) {
			List<StudentMarksBean> listOfSubjectsToRemove = studentMarksList.stream()
																			.filter(marksBean -> "Project".equals(marksBean.getSubject()) || "Module 4 - Project".equals(marksBean.getSubject()))
																			.filter(marksBean -> checkIfProjectCopyCase(marksBean.getSapid(), Integer.valueOf(marksBean.getYear()), marksBean.getMonth(), 
																														Integer.valueOf(marksBean.getSem()), marksBean.getSubject()))
																			.collect(Collectors.toList());
			
			revaluationList.removeAll(listOfSubjectsToRemove);
		}
		request.getSession().setAttribute("studentWrittenScoreMarks", revaluationList);
		
		modelnView.addObject("mostRecentResultPeriod", mostRecentResultPeriod);
		modelnView.addObject("revaluationList", revaluationList);
		modelnView.addObject("size", studentMarksList.size());
		modelnView.addObject("sr", sr);
		return modelnView;
	}

	@RequestMapping(value = "/student/saveTEERevaluation", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView saveTEERevaluation(@ModelAttribute ServiceRequestStudentPortal sr, Model m, HttpServletRequest request,
			ModelMap model/*,RedirectAttributes ra*/) {
		if (!checkSession(request)) {
			return new ModelAndView("jsp/login");
		}
		ArrayList<String> revalSubjects = (ArrayList<String>) request.getSession().getAttribute("revalSubjects");
		String sapid = (String) request.getSession().getAttribute("userId");
		// sapid = "77214002240";
		PAYMENTS_LOGGER.info("{} in saveTEERevaluation", sapid);
		StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(sapid);
		String mostRecentResultPeriod="";
		if ("Online".equals(student.getExamMode())) {
			mostRecentResultPeriod = serviceRequestDao.getMostRecentOnlineResultPeriod();
		} else {
			mostRecentResultPeriod = serviceRequestDao.getMostRecentOfflineResultPeriod();
		}
		sr.setDescription(
				sr.getServiceRequestType() +" for "+mostRecentResultPeriod+" for Subject " + sr.getRevaluationSubjects() + " for student " + sapid);
		sr.setTranStatus(ServiceRequestStudentPortal.TRAN_STATUS_INITIATED);
		sr.setCategory("Exam");
		sr.setSrAttribute("");
		sr.setIssued("N");
		sr.setInformationForPostPayment(StringUtils.join(revalSubjects, '~'));
		try {
		//sr.setLandMark(student.getLandMark());
		sr.setPin(student.getPin());
		sr.setLocality(student.getLocality());
		sr.setStreet(student.getStreet());
		sr.setHouseNoName(student.getHouseNoName());
		sr.setCity(student.getCity());
		sr.setState(student.getState());
		sr.setCountry(student.getCountry());
		
		String[] examYearMonth = mostRecentResultPeriod.split("-");
		sr.setMonth(examYearMonth[0]);
		sr.setYear(examYearMonth[1]);
		}catch(Exception e ) {
//			e.printStackTrace();
		}
		populateServiceRequestObject(sr, request);

		serviceRequestDao.insertServiceRequest(sr);
		request.getSession().setAttribute("sr", sr);
		m.addAttribute("sr", sr);
		request.getSession().setAttribute("revaluationSubjects", sr.getRevaluationSubjects());
		String requestId = sr.getTrackId()+ RandomStringUtils.randomAlphanumeric(20);
		
		return paymentHelper.populateGoToGateway(sr, student);
		
	}
	
	/**
	 * Check if the Subject is marked with a Copy Case remark.
	 * @param sapid - student No. of the student
	 * @param year - exam Year
	 * @param month - exam Month
	 * @param sem - semester
	 * @param subject - subject
	 * @return boolean value indicating if the subject is marked as Copy Case
	 */
	private boolean checkIfProjectCopyCase(String sapid, int year, String month, int sem, String subject) {
		return serviceRequestDao.checkIfProjectCopyCaseRemark(sapid, year, month, sem, subject) > 0;
	}

	@RequestMapping(value = "/student/singleBookForm", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView singleBookForm(@ModelAttribute ServiceRequestStudentPortal sr, Model m, HttpServletRequest request) {
		if (!checkSession(request)) {
			return new ModelAndView("jsp/login");
		}
		StudentStudentPortalBean student = (StudentStudentPortalBean) request.getSession().getAttribute("student_studentportal");
		ModelAndView modelnView = new ModelAndView("jsp/serviceRequest/singleBookNew");
		String sapid = (String) request.getSession().getAttribute("userId");
		ArrayList<String> subjectList = serviceRequestDao.getFailedOrCurrentSubjects(sapid);
		if (subjectList != null) {
			subjectList.remove("Project");
			subjectList.remove("Module 4 - Project");
			
			if (student.getWaivedOffSubjects() != null && student.getWaivedOffSubjects().size() > 0) {
				ArrayList<String> waivedOffSubjects = student.getWaivedOffSubjects();
				for (String waivedOffSubject : waivedOffSubjects) {
					if (subjectList.contains(waivedOffSubject)) {
						subjectList.remove(waivedOffSubject);// Student cannot
						// order book
						// that is
						// waived off
					}
				}
			}

		}

		modelnView.addObject("subjectList", subjectList);
		modelnView.addObject("size", (subjectList != null ? subjectList.size() : 0));

		return modelnView;
	}

	@RequestMapping(value = "/student/saveSingleBook", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView saveSingleBook(@ModelAttribute ServiceRequestStudentPortal sr, Model m, HttpServletRequest request,
			ModelMap model/*,RedirectAttributes ra*/) {
		if (!checkSession(request)) {
			return new ModelAndView("jsp/login");
		}
		
		String sapid = (String) request.getSession().getAttribute("userId");
		PAYMENTS_LOGGER.info("{} in saveSingleBook",sapid);
		StudentStudentPortalBean student = (StudentStudentPortalBean) request.getSession().getAttribute("student_studentportal");
		String subject = request.getParameter("subject");
		sr.setDescription(sr.getServiceRequestType() + " for Subject " + subject + " for student " + sapid
				+ " for Program " + student.getProgram());
		sr.setTranStatus(ServiceRequestStudentPortal.TRAN_STATUS_INITIATED);
		sr.setCategory("Academics");
		try {
		//sr.setLandMark(student.getLandMark());
		sr.setPin(student.getPin());
		sr.setLocality(student.getLocality());
		sr.setStreet(student.getStreet());
		sr.setHouseNoName(student.getHouseNoName());
		sr.setCity(student.getCity());
		sr.setState(student.getState());
		sr.setCountry(student.getCountry());
		}catch(Exception e ) {
//			e.printStackTrace();
		}
		
		populateServiceRequestObject(sr, request);
		sr.setIssued("N");
		sr.setSrAttribute("");
		serviceRequestDao.insertServiceRequest(sr);
		request.getSession().setAttribute("sr", sr);
		m.addAttribute("sr", sr);
		//fillPaymentParametersInMap(model, student, sr);
		String requestId = sr.getTrackId()+ RandomStringUtils.randomAlphanumeric(20);
		/*return proceedToPayOptions(model,requestId,ra);*/
		//return new ModelAndView(new RedirectView("pay"), model);
		
		return paymentHelper.populateGoToGateway(sr, student);
		
	}

	@RequestMapping(value = "/student/assignmentRevaluationForm", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView assignmentRevaluationForm(@ModelAttribute ServiceRequestStudentPortal sr, Model m,
			HttpServletRequest request) {
		if (!checkSession(request)) {
			return new ModelAndView("jsp/login");
		}
		request.getSession().setAttribute("revalSubjects", null);// Reset to
		// null if
		// student
		// goes back
		// and
		// decides
		// to change
		// subjects
		ModelAndView modelnView = new ModelAndView("jsp/serviceRequest/assignmentRevaluationNew");
		String sapid = (String) request.getSession().getAttribute("userId");
		
		// sapid = "77214002240";
		try {
			String mostRecentResultPeriod = serviceRequestDao.getMostRecentAssignmentResultPeriod();
			List<StudentMarksBean> studentMarksList = serviceRequestDao.getAStudentsMostRecentAssignmentMarks(sapid);
			List<String> applicableSubjectList = serviceRequestDao.getApplicableSubjectList(sapid);
			servReqServ.removeNonApplicableSubject(studentMarksList, applicableSubjectList);
			
			// Add RemarkGrade Subjects for revaluation -Prashant(9-3-2023)
			StudentStudentPortalBean student = (StudentStudentPortalBean)request.getSession().getAttribute("student_studentportal");
			if(student != null && REMARK_GRADES_PROGRAMS.contains(student.getProgram()) ) {
				try {
					List<StudentMarksBean> studentRGMarksList = servReqServ.getAStudentsMostRecentRGAssignmentMarks(sapid);
					studentMarksList.addAll(studentRGMarksList);
				} catch (Exception e) {
					logger.error("Exception Error Assignment Revail SR getAStudentsMostRecentRGAssignmentMarks Sapid:{} Error:{} ",sapid,e);
				}
			}
			
			modelnView.addObject("mostRecentResultPeriod", mostRecentResultPeriod);
			modelnView.addObject("studentMarksList", studentMarksList);
			modelnView.addObject("size", studentMarksList.size());
			request.getSession().setAttribute("studentMarksList_studentportal", studentMarksList);
		} catch (Exception e) {
			logger.error("Error in getting subject list for Assignment Revaluation for sapid {} due to {}", sapid, e);
		}
		return modelnView;
	}

	@RequestMapping(value = "/student/assignmentRevaluationConfirmation", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView assignmentRevaluationConfirmation(@ModelAttribute ServiceRequestStudentPortal sr, Model m,
			HttpServletRequest request/*,RedirectAttributes ra*/) {
		if (!checkSession(request)) {
			return new ModelAndView("jsp/login");
		}
		ModelAndView modelnView = new ModelAndView("jsp/serviceRequest/assignmentRevaluationConfirmationNew");
		String sapid = (String) request.getSession().getAttribute("userId");
		try {
			List<String> subject_tmp = new ArrayList<String>();
			for(String subject : sr.getRevaluationSubjects()) {
				subject = subject.replace("~", ",");
				subject_tmp.add(subject);
			}
			sr.setRevaluationSubjects(subject_tmp);
			
			
			boolean isCertificate = (boolean)request.getSession().getAttribute("isCertificate");
			// sapid = "77214002240";
			double amount = 0.0;
			if (sr.getRevaluationSubjects().size() == 0) {
				setError(request, "Please select at least one subject to proceed for Revaluation");
				return assignmentRevaluationForm(sr, m, request);
			} else {
				ArrayList<String> finalRevalSubjectList= new ArrayList<String>();
				for (String str : sr.getRevaluationSubjects()) {
					if (str!=null) {
						finalRevalSubjectList.add(str);
					}
				}
				sr.setRevaluationSubjects(finalRevalSubjectList);
				String reval=StringUtils.join(sr.getRevaluationSubjects(),"~");
				request.getSession().setAttribute("revalSubjects", reval);// Store
				// reval
				// subjects
				// in
				// session
				amount = sr.getRevaluationSubjects().size() * REVALUATION_FEE_PER_SUBJECT;
				if(isCertificate){
					amount = Double.parseDouble(generateAmountBasedOnCriteria(String.valueOf(amount), SERVICE_TAX_RULE));
				}
				modelnView.addObject("amount", amount);
				sr.setAmount(amount + "");
			}

			
			String mostRecentResultPeriod = serviceRequestDao.getMostRecentAssignmentResultPeriod();
			List<StudentMarksBean> studentMarksList = serviceRequestDao.getAStudentsMostRecentAssignmentMarks(sapid);
			
			// Add RemarkGrade Subjects for revaluation -Prashant(9-3-2023)
			StudentStudentPortalBean student = (StudentStudentPortalBean)request.getSession().getAttribute("student_studentportal");
			if (student != null && REMARK_GRADES_PROGRAMS.contains(student.getProgram())) {
				try {
					List<StudentMarksBean> studentRGMarksList = servReqServ.getAStudentsMostRecentRGAssignmentMarks(sapid);
					studentMarksList.addAll(studentRGMarksList);
				} catch (Exception e) {
					logger.error("Exception Error Assignment Revail SR raising getAStudentsMostRecentRGAssignmentMarks Sapid:{} Error:{} ",sapid, e);
				}
			}
			
			List<StudentMarksBean> revaluationList = new ArrayList<>();
			for (StudentMarksBean studentMarksBean : studentMarksList) {
				if (sr.getRevaluationSubjects().contains(studentMarksBean.getSubject())) {
					revaluationList.add(studentMarksBean);
				}
			}
			
			//Reval subject marks kept in session and accessed only if the payment is successfull//
			request.getSession().setAttribute("studentAssignmentMarksList",revaluationList);
			modelnView.addObject("mostRecentResultPeriod", mostRecentResultPeriod);
			modelnView.addObject("revaluationList", revaluationList);
			modelnView.addObject("size", studentMarksList.size());
		} catch (Exception e) {
			logger.error("Exception Error Assignment Revail SR assignmentRevaluationConfirmation Sapid:{} Error:{} ",sapid,e);
			//e.printStackTrace();
			setError(request, "Error: " + e.getMessage());
		}
		modelnView.addObject("sr", sr);
		return modelnView;
	}

	@RequestMapping(value = "/student/saveAssignmentRevaluation", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView saveAssignmentRevaluation(@ModelAttribute ServiceRequestStudentPortal sr, Model m,
			HttpServletRequest request, ModelMap model/*,RedirectAttributes ra*/) {
		if (!checkSession(request)) {
			return new ModelAndView("jsp/login");
		}

		String revalSubjects = (String) request.getSession().getAttribute("revalSubjects");
		String sapid = (String) request.getSession().getAttribute("userId");
		PAYMENTS_LOGGER.info("{} in saveAssignmentRevaluation", sapid);
		// sapid = "77214002240";
		StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(sapid);
		String mostRecentResultPeriod = serviceRequestDao.getMostRecentAssignmentResultPeriod();
		sr.setDescription(
				sr.getServiceRequestType() +" for "+mostRecentResultPeriod+ " Exam for Subject " + sr.getRevaluationSubjects() + " for student " + sapid);
		String[] examYearMonth = mostRecentResultPeriod.split("-");
		sr.setMonth(examYearMonth[0]);
		sr.setYear(examYearMonth[1]);
		sr.setTranStatus(ServiceRequestStudentPortal.TRAN_STATUS_INITIATED);
		sr.setCategory("Exam");
		//String reval=StringUtils.join(sr.getRevaluationSubjects(),"~");
		//reval = StringUtils.replace(reval, ",", "~");
		
		sr.setInformationForPostPayment(revalSubjects);
		try {
			//sr.setLandMark(student.getLandMark());
			sr.setPin(student.getPin());
			sr.setLocality(student.getLocality());
			sr.setStreet(student.getStreet());
			sr.setHouseNoName(student.getHouseNoName());
			sr.setCity(student.getCity());
			sr.setState(student.getState());
			sr.setCountry(student.getCountry());
			}catch(Exception e ) {
//				e.printStackTrace();
			}
		populateServiceRequestObject(sr, request);
		sr.setIssued("N");
		sr.setSrAttribute("");
		serviceRequestDao.insertServiceRequest(sr);
		request.getSession().setAttribute("sr", sr);
		m.addAttribute("sr", sr);
		request.getSession().setAttribute("revaluationSubjects", sr.getRevaluationSubjects());
		String requestId = sr.getTrackId()+ RandomStringUtils.randomAlphanumeric(20);
		
		return paymentHelper.populateGoToGateway(sr, student);
		//return new ModelAndView(new RedirectView("pay"), model);  
		
	}

	@RequestMapping(value = "/student/saveDuplicateFeeReceipt", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView saveDuplicateFeeReceipt(@ModelAttribute ServiceRequestStudentPortal sr, Model m, HttpServletRequest request,
			ModelMap model/*,RedirectAttributes ra*/) {
		if (!checkSession(request)) {
			return new ModelAndView("jsp/login");
		}

		String sapid = (String) request.getSession().getAttribute("userId");
		// sapid = "77214002240";
		PAYMENTS_LOGGER.info("{} in saveDuplicateFeeReceipt", sapid);
		StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(sapid);
		String sem = request.getParameter("semester");
		sr.setDescription(sr.getServiceRequestType() + " for Semester " + sem + " for student " + sapid);
		sr.setTranStatus(ServiceRequestStudentPortal.TRAN_STATUS_INITIATED);
		sr.setCategory("Admission");
		try {
		//sr.setLandMark(student.getLandMark());
		sr.setPin(student.getPin());
		sr.setLocality(student.getLocality());
		sr.setStreet(student.getStreet());
		sr.setHouseNoName(student.getHouseNoName());
		sr.setCity(student.getCity());
		sr.setState(student.getState());
		sr.setCountry(student.getCountry());
		}catch(Exception e ) {
//			e.printStackTrace();
		}
		populateServiceRequestObject(sr, request);
		sr.setIssued("N");
		sr.setSrAttribute("");
		serviceRequestDao.insertServiceRequest(sr);
		request.getSession().setAttribute("sr", sr);
		m.addAttribute("sr", sr);
		//fillPaymentParametersInMap(model, student, sr);
		String requestId = sr.getTrackId()+ RandomStringUtils.randomAlphanumeric(20);
		/*return proceedToPayOptions(model,requestId,ra);*/
		//return new ModelAndView(new RedirectView("pay"), model);
		return paymentHelper.populateGoToGateway(sr, student);
		
	}

	@RequestMapping(value = "/student/saveDuplicateStudyKit", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView saveDuplicateStudyKit(@ModelAttribute ServiceRequestStudentPortal sr, Model m, HttpServletRequest request,
			ModelMap model/*,RedirectAttributes ra*/) {
		if (!checkSession(request)) {
			return new ModelAndView("jsp/login");
		}

		String sapid = (String) request.getSession().getAttribute("userId");
		// sapid = "77214002240";
		PAYMENTS_LOGGER.info("{} in saveDuplicateStudyKit", sapid);
		StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(sapid);
		String sem = request.getParameter("semester");
		sr.setDescription(sr.getServiceRequestType() + " for Semester " + sem + " for student " + sapid
				+ " for Program " + student.getProgram());
		sr.setTranStatus(ServiceRequestStudentPortal.TRAN_STATUS_INITIATED);
		sr.setCategory("Academics");
		populateServiceRequestObject(sr, request);
		sr.setIsFlagged("N");
		sr.setSrAttribute("");
		
		try {
		//sr.setLandMark(student.getLandMark());
		sr.setPin(student.getPin());
		sr.setLocality(student.getLocality());
		sr.setStreet(student.getStreet());
		sr.setHouseNoName(student.getHouseNoName());
		sr.setCity(student.getCity());
		sr.setState(student.getState());
		sr.setCountry(student.getCountry());
		}catch(Exception e ) {
		}
		
		serviceRequestDao.insertServiceRequest(sr);
		request.getSession().setAttribute("sr", sr);
		m.addAttribute("sr", sr);
		//fillPaymentParametersInMap(model, student, sr);
		String requestId = sr.getTrackId()+ RandomStringUtils.randomAlphanumeric(20);
		/*return proceedToPayOptions(model,requestId,ra);*/
		//return new ModelAndView(new RedirectView("pay"), model);
		
		return paymentHelper.populateGoToGateway(sr, student);
		
	}

	// Newly added for Re-Dispatch Study Kit// Vikas :-27/7/2016
	@RequestMapping(value = "/student/saveRedispatchStudyKit", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView redispatchStudyKit(@ModelAttribute ServiceRequestStudentPortal sr, Model m, HttpServletRequest request,
			ModelMap model/*,RedirectAttributes ra*/) {
		if (!checkSession(request)) {
			return new ModelAndView("jsp/login");
		}

		String sapid = (String) request.getSession().getAttribute("userId");
		// sapid = "77214002240";
		PAYMENTS_LOGGER.info("{} in redispatchStudyKit", sapid);
		StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(sapid);
		String sem = sr.getSem();//request.getParameter("semester");
		sr.setDescription(sr.getServiceRequestType() + " for Semester " + sem + " for student " + sapid
				+ " for Program " + student.getProgram());
		sr.setTranStatus(ServiceRequestStudentPortal.TRAN_STATUS_INITIATED);
		sr.setCategory("Academics");
		populateServiceRequestObject(sr, request);
		sr.setIssued("N");
		sr.setSrAttribute("");
		try {
		//sr.setLandMark(student.getLandMark());
		sr.setPin(student.getPin());
		sr.setLocality(student.getLocality());
		sr.setStreet(student.getStreet());
		sr.setHouseNoName(student.getHouseNoName());
		sr.setCity(student.getCity());
		sr.setState(student.getState());
		sr.setCountry(student.getCountry());
		}catch(Exception e ) {
			e.getStackTrace();
		}
		serviceRequestDao.insertServiceRequest(sr);
		request.getSession().setAttribute("sr", sr);
		m.addAttribute("sr", sr);
		//fillPaymentParametersInMap(model, student, sr);
		String requestId = sr.getTrackId()+ RandomStringUtils.randomAlphanumeric(20);
		/*return proceedToPayOptions(model,requestId,ra);*/
		//return new ModelAndView(new RedirectView("pay"), model);
		
		return paymentHelper.populateGoToGateway(sr, student);
		
	}

	// end//

	@RequestMapping(value = "/student/saveDuplicateICard", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView saveDuplicateICard(@ModelAttribute ServiceRequestStudentPortal sr,
			@RequestParam(required = false) MultipartFile photoGraph, Model m, HttpServletRequest request,
			ModelMap model/*,RedirectAttributes ra*/) {
		if (!checkSession(request)) {
			return new ModelAndView("jsp/login");
		}
		String sapid = (String) request.getSession().getAttribute("userId");
		String shippingAddress = (String) request.getParameter("shippingAddress");
		// sapid = "77214002240";
		StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(sapid); 
	    
		if("Yes".equalsIgnoreCase(sr.getWantAtAddress())){
			sr.setPostalAddress(shippingAddress);
			
		}
		sr.setDescription(
				sr.getServiceRequestType() + " for student " + sapid + " for Program " + student.getProgram());
		sr.setTranStatus(ServiceRequestStudentPortal.TRAN_STATUS_INITIATED);
		sr.setCategory("Academics");
		populateServiceRequestObject(sr, request);
		sr.setIssued("N");
		sr.setSrAttribute("");
		serviceRequestDao.insertServiceRequest(sr);
		request.getSession().setAttribute("sr", sr);
		m.addAttribute("sr", sr);

		if (photoGraph != null) {
			ServiceRequestDocumentBean document = new ServiceRequestDocumentBean();
			document.setServiceRequestId(sr.getId());
			uploadFile(document, photoGraph, sapid + "_Photograph");

			if (document.getErrorMessage() == null) {
				document.setDocumentName("Student Photograph for Duplicate ID");
				serviceRequestDao.insertServiceRequestDocument(document);
				sr.setHasDocuments("Y");
				serviceRequestDao.updateDocumentStatus(sr);
				
				ServiceRequestStudentPortal bean = serviceRequestDao.getServiceRequestBySrId(sr.getId());
				serviceRequestDao.insertServiceRequestStatusHistory(bean, "Update");

				MailSender mailer = (MailSender) act.getBean("mailer");
				mailer.sendSREmail(sr, student);

			} else {
				setError(m, "Error in uploading document " + document.getErrorMessage());
				return new ModelAndView("jsp/serviceRequest/duplicateICard");
			}
		}

		//fillPaymentParametersInMap(model, student, sr);
		String requestId = sr.getTrackId();
		/*return proceedToPayOptions(model,requestId,ra);*/
		//return new ModelAndView(new RedirectView("pay"), model);
		
		if(request.getParameter("paymentOption").equals("hdfc")) {
			request.getSession().setAttribute("paymentOption","hdfc");
			fillPaymentParametersInMap(model, student, sr);
			return new ModelAndView(new RedirectView("jsp/pay"), model);
		}
		
		String paymentOption = request.getParameter("paymentOption");
		ModelAndView mv = new ModelAndView("jsp/"+ paymentOption + "Pay");	//set payment jsp file name;
		//PaymentHelper paymentHelper = new PaymentHelper();
		String checkSum = paymentHelper.generateCommonCheckSum(sr, student, requestId,paymentOption);
		if(checkSum != "true") {
			setError(request, "Error: " + checkSum);
			return addSRForm(sr, request, m);
		}
		request.getSession().setAttribute("paymentOption",paymentOption);
		mv = paymentHelper.setCommonModelData(mv, sr, student, requestId,paymentOption);
		return mv;
		
		/*else if(request.getParameter("paymentOption").equals("payu")) {
			ModelAndView mv = new ModelAndView("jsp/payu");
			PaymentHelper paymentHelper = new PaymentHelper();
			String checkSum = paymentHelper.generatePayuCheckSum(sr, student, requestId);
			if(checkSum != "true") {
				setError(request, "Error: " + checkSum);
				return addSRForm(sr, request, m);
			}
			request.getSession().setAttribute("paymentOption","payu");
			mv = paymentHelper.setPayuModelData(mv, sr, student, requestId);
			return mv;
		}
		
		else if(request.getParameter("paymentOption").equals("billdesk")) {
			ModelAndView mv = new ModelAndView("jsp/billdeskPay");
			PaymentHelper paymentHelper = new PaymentHelper();
			String checkSum = paymentHelper.generateBillDeskCheckSum(sr);
			if(checkSum != "true") {
				setError(request, "Error: " + checkSum);
				return addSRForm(sr, request, m);
			}
			request.getSession().setAttribute("paymentOption","billdesk");
			mv = paymentHelper.setBillDeskModelData(mv,sr,student,requestId);
			return mv;
		}
		
		return proceedToPayOptions(model,requestId,ra);
		ModelAndView mv = new ModelAndView("jsp/paytmPay");
		PaymentHelper paymentHelper = new PaymentHelper();
		String checkSum = paymentHelper.generateCheckSum(sr,student,requestId);
		if(checkSum != "true") {
			setError(request, "Error: " + checkSum);
			return addSRForm(sr, request, m);
			
		}
		request.getSession().setAttribute("paymentOption","paytm");
		mv = paymentHelper.setModelData(mv,sr,student,requestId);
		return mv;*/
		
	}
	
	
//	to be deleted, api shifted to rest controller
//	@RequestMapping(value = "/m/saveChangeInICard", method = RequestMethod.POST, headers = "content-type=multipart/form-data" )
//	public ResponseEntity<ServiceRequest> MsaveChangeInICard(HttpServletRequest request,
//			MultipartFile changeInIDDoc,ServiceRequest sr,@RequestParam Map<String,String> mapOfInputs
//				 )throws Exception {
//		
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json");
//		String sapId= sr.getSapId();
//		sr.setDescription(sr.getServiceRequestType() + " for student " + sapId + ": First Name:" + mapOfInputs.get("firstName")
//		+ ", Last Name: " + mapOfInputs.get("lastName"));
//		
//		ServiceRequest response = servReqServ.saveNewICard(sr,changeInIDDoc,request);
//		if(response.getError() !=null) {
//		     return new ResponseEntity<>(response, headers, HttpStatus.INTERNAL_SERVER_ERROR);
//	       }
//		return new ResponseEntity<>(response,headers, HttpStatus.OK);
//	}
	
	@RequestMapping(value = "/student/saveChangeInICard", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView saveChangeInICard(@RequestParam Map<String,String> mapOfInputs,@ModelAttribute ServiceRequestStudentPortal sr,
			@RequestParam(required = true) MultipartFile changeInIDDoc, Model m, HttpServletRequest request,
			ModelMap model,HttpServletResponse response/*,RedirectAttributes ra*/) {
		if (!checkSession(request)) {
			return new ModelAndView("jsp/login");
		}
		

		String sapid = (String) request.getSession().getAttribute("userId");
		StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(sapid);
		sr.setDescription(sr.getServiceRequestType() + " for student " + sapid + ": First Name:" + mapOfInputs.get("firstName")
				+ ", Last Name: " + mapOfInputs.get("lastName"));
		sr.setTranStatus(ServiceRequestStudentPortal.TRAN_STATUS_INITIATED);
		sr.setCategory("Admission");
		populateServiceRequestObject(sr, request);
		sr.setIssued("N");
		sr.setSrAttribute("");
		
		
		serviceRequestDao.insertServiceRequest(sr);
		request.getSession().setAttribute("sr", sr);
		m.addAttribute("sr", sr);

		if (changeInIDDoc != null) {
			ServiceRequestDocumentBean document = new ServiceRequestDocumentBean();
			document.setServiceRequestId(sr.getId());
			uploadFile(document, changeInIDDoc, sapid + "_Change_In_ID_Document");

			if (document.getErrorMessage() == null) {
				document.setDocumentName("Document for Change in ID");
				serviceRequestDao.insertServiceRequestDocument(document);
				sr.setHasDocuments("Y");
				serviceRequestDao.updateDocumentStatus(sr);
				
				ServiceRequestStudentPortal bean = serviceRequestDao.getServiceRequestBySrId(sr.getId());
				serviceRequestDao.insertServiceRequestStatusHistory(bean, "Update");
				
				MailSender mailer = (MailSender) act.getBean("mailer");
				mailer.sendSREmail(sr, student);
			} else {
				setError(m, "Error in uploading document " + document.getErrorMessage());
				return new ModelAndView("jsp/serviceRequest/changeInID");
			}
		}
			
			
			
			//fillPaymentParametersInMap(model, student, sr);
			//return new ModelAndView(new RedirectView("pay"), model);
			/*return new ModelAndView(new RedirectView("pay"), model);*/
			/**/
			//Randomn request id generated. This is used as a uniquer identifier for api request.
			
			/**/
			/*This method is made common which inserts request id,populates transaction bean with model attributes
			 * API request is inserted
			 * The student is redirected to payoptions.
			 * 
			 * 
			
			 */ 
			
			String requestId = sr.getTrackId();
			/*return proceedToPayOptions(model,requestId,ra);*/
			
			if(request.getParameter("paymentOption").equals("hdfc")) {
				request.getSession().setAttribute("paymentOption","hdfc");
				fillPaymentParametersInMap(model, student, sr);
				return new ModelAndView(new RedirectView("jsp/pay"), model);
			}
			
			String paymentOption = request.getParameter("paymentOption");
			ModelAndView mv = new ModelAndView("jsp/"+ paymentOption + "Pay");	//set payment jsp file name;
			//PaymentHelper paymentHelper = new PaymentHelper();
			String checkSum = paymentHelper.generateCommonCheckSum(sr, student, requestId,paymentOption);
			if(checkSum != "true") {
				setError(request, "Error: " + checkSum);
				return addSRForm(sr, request, m);
			}
			request.getSession().setAttribute("paymentOption",paymentOption);
			mv = paymentHelper.setCommonModelData(mv, sr, student, requestId,paymentOption);
			return mv;
			
			/*else if(request.getParameter("paymentOption").equals("payu")) {
				ModelAndView mv = new ModelAndView("jsp/payu");
				PaymentHelper paymentHelper = new PaymentHelper();
				String checkSum = paymentHelper.generatePayuCheckSum(sr, student, requestId);
				if(checkSum != "true") {
					setError(request, "Error: " + checkSum);
					return addSRForm(sr, request, m);
				}
				request.getSession().setAttribute("paymentOption","payu");
				mv = paymentHelper.setPayuModelData(mv, sr, student, requestId);
				return mv;
			}
			
			else if(request.getParameter("paymentOption").equals("billdesk")) {
				ModelAndView mv = new ModelAndView("jsp/billdeskPay");
				PaymentHelper paymentHelper = new PaymentHelper();
				String checkSum = paymentHelper.generateBillDeskCheckSum(sr);
				if(checkSum != "true") {
					setError(request, "Error: " + checkSum);
					return addSRForm(sr, request, m);
				}
				request.getSession().setAttribute("paymentOption","billdesk");
				mv = paymentHelper.setBillDeskModelData(mv,sr,student,requestId);
				return mv;
			}
			
			return proceedToPayOptions(model,requestId,ra);
			ModelAndView mv = new ModelAndView("jsp/paytmPay");
			PaymentHelper paymentHelper = new PaymentHelper();
			String checkSum = paymentHelper.generateCheckSum(sr,student,requestId);
			if(checkSum != "true") {
				setError(request, "Error: " + checkSum);
				return addSRForm(sr, request, m);
			}
			request.getSession().setAttribute("paymentOption","paytm");
			mv = paymentHelper.setModelData(mv,sr,student,requestId);
			return mv;*/
		
	}



/*	@RequestMapping(value = "/saveChangeInICard", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView saveChangeInICard(@RequestParam Map<String,String> mapOfInputs,@ModelAttribute ServiceRequest sr,
			@RequestParam(required = true) MultipartFile changeInIDDoc, Model m, HttpServletRequest request,
			ModelMap model,HttpServletResponse response,RedirectAttributes ra) {
		if (!checkSession(request)) {
			return new ModelAndView("jsp/login");
		}
		

		String sapId = (String) request.getSession().getAttribute("userId");
		sr.setDescription(sr.getServiceRequestType() + " for student " + sapId + ": First Name:" + mapOfInputs.get("firstName")
		+ ", Last Name: " + mapOfInputs.get("lastName"));
		
		ServiceRequest result = servReqServ.saveNewICard(sr,changeInIDDoc,request);
		StudentBean student = serviceRequestDao.getSingleStudentsData(sapId);
		
		request.getSession().setAttribute("sr", result);
		m.addAttribute("sr", result);

		if (result.getError() != null) {
			
				return new ModelAndView("jsp/serviceRequest/changeInID");
		}	
		fillPaymentParametersInMap(model, student, result);
			
		return new ModelAndView(new RedirectView("pay"), model);
			return new ModelAndView(new RedirectView("pay"), model);
			
			//Randomn request id generated. This is used as a uniquer identifier for api request.
			
			
			This method is made common which inserts request id,populates transaction bean with model attributes
			 * API request is inserted
			 * The student is redirected to payoptions.
			 * 
			 * 
			
			 
			
			String requestId = sr.getTrackId();
			return proceedToPayOptions(model,requestId,ra);
			
		
		
	}*/



	
//	@RequestMapping(value = "/saveCorrectDOB", method = { RequestMethod.GET, RequestMethod.POST })
//	public ModelAndView saveCorrectDOB(@ModelAttribute ServiceRequest sr,
//			@RequestParam(required = true) MultipartFile sscMarksheet, @RequestParam(required = true) String dob,
//			Model m, HttpServletRequest request, ModelMap model) {
//		if (!checkSession(request)) {
//			return new ModelAndView("jsp/login");
//		}
//
//		String sapid = (String) request.getSession().getAttribute("userId");
//		// sapid = "77214002240";
//		StudentBean student = serviceRequestDao.getSingleStudentsData(sapid);
//		sr.setDescription(sr.getServiceRequestType() + " for student " + sapid + " : New DOB (YYYY-MM-DD) " + dob);
//		sr.setTranStatus(ServiceRequest.TRAN_STATUS_FREE);
//		sr.setCategory("Admission");
//		sr.setSapId(sapid);
//		sr.setRequestStatus(ServiceRequest.REQUEST_STATUS_SUBMITTED);
//		sr.setCreatedBy(sapid);
//		sr.setLastModifiedBy(sapid);
//		sr.setIssued("N");
//		sr.setSrAttribute("");
//		serviceRequestDao.insertServiceRequest(sr);
//		request.getSession().setAttribute("sr", sr);
//		m.addAttribute("sr", sr);
//
//		if (sscMarksheet != null) {
//			ServiceRequestDocumentBean document = new ServiceRequestDocumentBean();
//			document.setServiceRequestId(sr.getId());
//			uploadFile(document, sscMarksheet, sapid + "_SSC_Marksheet");
//
//			if (document.getErrorMessage() == null) {
//				document.setDocumentName("Student SSC Marksheet for correct Date of Birth");
//				serviceRequestDao.insertServiceRequestDocument(document);
//				sr.setHasDocuments("Y");
//				serviceRequestDao.updateDocumentStatus(sr);
//
//				MailSender mailer = (MailSender) act.getBean("mailer");
//				mailer.sendSREmail(sr, student);
//			} else {
//				setError(m, "Error in uploading document " + document.getErrorMessage());
//				return new ModelAndView("jsp/serviceRequest/changeinDOB");
//			}
//		}
//		m.addAttribute("sr", sr);
//		return new ModelAndView("jsp/serviceRequest/srCreated");
//	}

	
	@RequestMapping(value = "/student/saveCorrectDOB", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView saveCorrectDOB(@ModelAttribute ServiceRequestStudentPortal sr,
			@RequestParam(required = true) MultipartFile sscMarksheet, @RequestParam(required = true) String dob,
			Model m, HttpServletRequest request, ModelMap model) {
		if (!checkSession(request)) {
			return new ModelAndView("jsp/login");
		}
		String sapid = (String) request.getSession().getAttribute("userId");
		sr.setSapId(sapid);
		sr.setDob(dob);
		ServiceRequestStudentPortal result = servReqServ.saveDOB(sr,sscMarksheet);
		
		request.getSession().setAttribute("sr", result);
		m.addAttribute("sr", result);
		
		if (result.getError()==null) {
			return new ModelAndView("jsp/serviceRequest/srCreated");
		}else {
			return new ModelAndView("jsp/serviceRequest/changeinDOB");
		}
		
	}


	


	@RequestMapping(value = "/viewSRDocuments", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView viewSRDocuments(@RequestParam Long serviceRequestId, Model m, HttpServletRequest request,
			ModelMap model) {
		if (!checkSession(request)) {
			return new ModelAndView("jsp/login");
		}

		List<ServiceRequestDocumentBean> documents = serviceRequestDao.getDocuments(serviceRequestId);
		m.addAttribute("documents", documents);

		return new ModelAndView("jsp/serviceRequest/srDocuments");
	}

	@RequestMapping(value = "/student/saveChangeInName", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView saveChangeInName(@ModelAttribute ServiceRequestStudentPortal sr,
			@RequestParam(required = true) MultipartFile changeInNameDoc, Model m, HttpServletRequest request,
			ModelMap model) {
		if (!checkSession(request)) {
			return new ModelAndView("jsp/login");
		}

		sr.setFirstName(request.getParameter("firstName"));
		sr.setLastName(request.getParameter("lastName"));
//		sr.setMiddleName(request.getParameter("middleName"));
		String sapid = (String) request.getSession().getAttribute("userId");
		sr.setSapId(sapid);
		ServiceRequestStudentPortal response = servReqServ.saveChangeInName(sr,changeInNameDoc);
		
		request.getSession().setAttribute("sr", response);
		m.addAttribute("sr", response);

		
		if (response.getError() != null) {
			
			return new ModelAndView("jsp/serviceRequest/changeInNameNew");
		}
		ModelAndView mv = new ModelAndView("jsp/serviceRequest/srCreated");
		mv.addObject("sr", sr);
		//fillPaymentParametersInMap(model, student, sr);
		return mv;
	}

	@RequestMapping(value = "/student/viewSRDocumentsForStudents", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView viewSRDocumentsForStudents(@RequestParam Long serviceRequestId, Model m,
			HttpServletRequest request, ModelMap model) {
		if (!checkSession(request)) {
			return new ModelAndView("jsp/login");
		}

		List<ServiceRequestDocumentBean> documents = serviceRequestDao.getDocuments(serviceRequestId);
		m.addAttribute("documents", documents);
		return new ModelAndView("jsp/serviceRequest/srDocumentForStudents");
	}

	private void uploadFile(ServiceRequestDocumentBean document, MultipartFile file, String newFileName) {

		String errorMessage = null;
		InputStream inputStream = null;
		OutputStream outputStream = null;

		String fileName = file.getOriginalFilename();

		long fileSizeInBytes = file.getSize();
		if (fileSizeInBytes > MAX_FILE_SIZE) {
			errorMessage = "File size exceeds 5MB. Please upload a file with size less than 5MB";
			document.setErrorMessage(errorMessage);
		}


		// Add Random number to avoid student guessing names of other student's
		// assignment files
		fileName = newFileName + "_" + RandomStringUtils.randomAlphanumeric(10)
				+ fileName.substring(fileName.lastIndexOf("."), fileName.length());
		try {

			inputStream = file.getInputStream();
			String filePath = SERVICE_REQUEST_FILES_PATH + fileName;
			// Check if Folder exists which is one folder per Exam (Jun2015,
			// Dec2015 etc.)
			File folderPath = new File(SERVICE_REQUEST_FILES_PATH);
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
			document.setFilePath(filePath);
			outputStream.close();
			inputStream.close();
		} catch (IOException e) {
//			e.printStackTrace();
			document.setErrorMessage(e.getMessage());
		}

		document.setErrorMessage(errorMessage);
	}

	@RequestMapping(value = "/student/downloadFile", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView downloadFile(HttpServletRequest request,HttpServletResponse response, Model m) {
		ModelAndView modelnView = new ModelAndView("jsp/serviceRequest/searchSR");

		String fullPath = request.getParameter("filePath");

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
//			e.printStackTrace();
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in downloading file.");
		}
		return modelnView;
	}

	private void populateServiceRequestObject(ServiceRequestStudentPortal sr, HttpServletRequest request) {

		String sapid = (String) request.getSession().getAttribute("userId");
		// sapid = "77214002240";
		String trackId = sapid + System.currentTimeMillis();
		request.getSession().setAttribute("trackId", trackId);// IMP
		sr.setSapId(sapid);
		sr.setTrackId(trackId);
		sr.setRequestStatus(ServiceRequestStudentPortal.REQUEST_STATUS_PAYMENT_PENDING);
		sr.setCreatedBy(sapid);
		sr.setLastModifiedBy(sapid);
		request.getSession().setAttribute("amount",sr.getAmount());
		
		request.getSession().setAttribute("sr", sr);
	}

	private void populateAdhocPaymentRequestObject(AdhocPaymentStudentPortalBean adhocPaymentBean, HttpServletRequest request) {
		
		adhocPaymentsLogger.info("ServiceRequestController.populateAdhocPaymentRequestObject() - START");
		// currenttime in millisecond +random number
		String trackId = System.currentTimeMillis()+String.valueOf((int)(Math.random() * 99999 + 1));
		request.getSession().setAttribute("trackId", trackId);// IMP
		StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(adhocPaymentBean.getSapId());
		boolean isCertificate = false;
		String firstName="notavaliable";
		if(student != null)
		{
			//isCertificate = isStudentOfCertificate(student.getProgram());
			isCertificate = student.isCertificateStudent();
			firstName=student.getFirstName();
		}
		adhocPaymentBean.setTrackId(trackId);
		adhocPaymentBean.setRequestStatus(ServiceRequestStudentPortal.REQUEST_STATUS_PAYMENT_PENDING);
		adhocPaymentBean.setCreatedBy(adhocPaymentBean.getEmailId());
		adhocPaymentBean.setLastModifiedBy(adhocPaymentBean.getEmailId());
		adhocPaymentBean.setAmount(isCertificate ? generateAmountBasedOnCriteria(adhocPaymentBean.getAmount(),SERVICE_TAX_RULE):adhocPaymentBean.getAmount());
		adhocPaymentBean.setFirstName(firstName);
		
		request.getSession().setAttribute("amount",adhocPaymentBean.getAmount());
		request.getSession().setAttribute("adhocPaymentBean", adhocPaymentBean);
		
		adhocPaymentsLogger.info("trackId:"+trackId);
		adhocPaymentsLogger.info("Student First Name:"+firstName);
		adhocPaymentsLogger.info("Net amount to pay:"+adhocPaymentBean.getAmount());
		adhocPaymentsLogger.info("ServiceRequestController.populateAdhocPaymentRequestObject() - END");
	}
	
	private ServiceRequestStudentPortal MfillPaymentParametersInMap( StudentStudentPortalBean student, ServiceRequestStudentPortal sr) {

		String address = student.getAddress();

		if (address == null || address.trim().length() == 0) {
			address = "Not Available";
		} else if (address.length() > 200) {
			address = address.substring(0, 200);
		}
		String city = student.getCity();
		if (city == null || city.trim().length() == 0) {
			city = "Not Available";
		}

		String pin = student.getPin();
		if (pin == null || pin.trim().length() == 0) {
			pin = "400000";
		}

		String mobile = student.getMobile();
		if (mobile == null || mobile.trim().length() == 0) {
			mobile = "0000000000";
		}

		String emailId = student.getEmailId();
		if (emailId == null || emailId.trim().length() == 0) {
			emailId = "notavailable@email.com";
		}
		sr.setChannel("10");
		sr.setAccount_id(ACCOUNT_ID);
		sr.setMode("LIVE");
		sr.setCurrency("INR");
		sr.setCurrency_code("INR");
		sr.setReference_no(sr.getTrackId());
		sr.setDescription(sr.getServiceRequestType() + ":" + student.getSapid());
		//model.addAttribute("channel", "10");
		//model.addAttribute("account_id", ACCOUNT_ID);
/*		model.addAttribute("reference_no", sr.getTrackId());
		model.addAttribute("amount",sr.getAmount());*/
/*		model.addAttribute("mode", "LIVE");
		model.addAttribute("currency", "INR");
		model.addAttribute("currency_code", "INR");*/
		//model.addAttribute("description", sr.getServiceRequestType() + ":" + student.getSapid());// This
																									// should
																									// be
																									// used
		sr.setReturn_url(SR_RETURN_URL);																							// in
		sr.setFinalName(student.getFirstName() + " " + student.getLastName());	
		sr.setPostalAddress(address);
		sr.setMobile(mobile);
		sr.setEmailId(emailId);
		sr.setAlgo("MD5");
		sr.setV3URL(V3URL);
		// response
		return sr;

	}

	
	private void fillPaymentParametersInMap(ModelMap model, StudentStudentPortalBean student, ServiceRequestStudentPortal sr) {

		String address = student.getAddress();

		if (address == null || address.trim().length() == 0) {
			address = "Not Available";
		} else if (address.length() > 200) {
			address = address.substring(0, 200);
		}
		String city = student.getCity();
		if (city == null || city.trim().length() == 0) {
			city = "Not Available";
		}

		String pin = student.getPin();
		if (pin == null || pin.trim().length() == 0) {
			pin = "400000";
		}

		String mobile = student.getMobile();
		if (mobile == null || mobile.trim().length() == 0) {
			mobile = "0000000000";
		}

		String emailId = student.getEmailId();
		if (emailId == null || emailId.trim().length() == 0) {
			emailId = "notavailable@email.com";
		}
		
		model.addAttribute("udf1", "SR_" + sr.getServiceRequestType());
		model.addAttribute("channel", "10");
		model.addAttribute("account_id", ACCOUNT_ID);
		model.addAttribute("reference_no", sr.getTrackId());
		model.addAttribute("amount",sr.getAmount());
		model.addAttribute("mode", "LIVE");
		model.addAttribute("currency", "INR");
		model.addAttribute("currency_code", "INR");
		model.addAttribute("description", sr.getServiceRequestType() + ":" + student.getSapid());// This
		model.addAttribute("orderId",sr.getOrderId());																							// should
																									// be
																									// used
																									// in
																									// response
		if(sr.getIsMobile()) {
			model.addAttribute("return_url", SR_RETURN_URL_MOBILE);
		}else {
			model.addAttribute("return_url", SR_RETURN_URL);	
		}
		model.addAttribute("name", student.getFirstName() + " " + student.getLastName());
		model.addAttribute("address", address);
		model.addAttribute("city", city);
		model.addAttribute("country", "IND");
		model.addAttribute("postal_code", pin);
		model.addAttribute("phone", mobile);
		model.addAttribute("email", emailId);
		model.addAttribute("algo", "MD5");
		model.addAttribute("V3URL", V3URL);
		model.addAttribute("studentNumber", sr.getSapId());
	}
	
	public void fillPaymentParametersInMapForPendingPayment(ModelMap model, StudentStudentPortalBean student, AdhocPaymentStudentPortalBean adhocPaymentBean)
	{
		String address = student.getAddress();

		if (address == null || address.trim().length() == 0) {
			address = "Not Available";
		} else if (address.length() > 200) {
			address = address.substring(0, 200);
		}
		String city = student.getCity();
		if (city == null || city.trim().length() == 0) {
			city = "Not Available";
		}

		String pin = student.getPin();
		if (pin == null || pin.trim().length() == 0) {
			pin = "400000";
		}

		String mobile = student.getMobile();
		if (mobile == null || mobile.trim().length() == 0) {
			mobile = "0000000000";
		}

		String emailId = student.getEmailId();
		if (emailId == null || emailId.trim().length() == 0) {
			emailId = "notavailable@email.com";
		}
		
		String name =student.getFirstName() + " " + student.getLastName();
		if (name == null || name.trim().length() == 0) {
			name = "notavailable";
		}
		
		model.addAttribute("channel", "10");
		model.addAttribute("account_id", ACCOUNT_ID);
		model.addAttribute("reference_no", adhocPaymentBean.getTrackId());
		model.addAttribute("amount", generateAmountBasedOnCriteria(adhocPaymentBean.getAmount(),SERVICE_TAX_RULE));
		model.addAttribute("mode", "LIVE");
		model.addAttribute("currency", "INR");
		model.addAttribute("currency_code", "INR");
		model.addAttribute("description", adhocPaymentBean.getDescription());// This  should be used in response
		model.addAttribute("return_url", PENDING_PAYMENT_RETURN_URL);
		model.addAttribute("name", name);
		model.addAttribute("address", address);
		model.addAttribute("city", city);
		model.addAttribute("country", "IND");
		model.addAttribute("postal_code", pin);
		model.addAttribute("phone", mobile);
		model.addAttribute("email", emailId);
		model.addAttribute("algo", "MD5");
		model.addAttribute("V3URL", V3URL);
	}
	
	
	public void fillPaymentParametersInMapForAdhocPayment(Model model, StudentStudentPortalBean student, AdhocPaymentStudentPortalBean adhocPaymentBean)
	{
		adhocPaymentsLogger.info("ServiceRequestController.fillPaymentParametersInMapForAdhocPayment() - START");
		String address = student.getAddress();

		if (address == null || address.trim().length() == 0) {
			address = "Not Available";
		} else if (address.length() > 200) {
			address = address.substring(0, 200);
		}
		String city = student.getCity();
		if (city == null || city.trim().length() == 0) {
			city = "Not Available";
		}

		String pin = student.getPin();
		if (pin == null || pin.trim().length() == 0) {
			pin = "400000";
		}

		String mobile = student.getMobile();
		if (mobile == null || mobile.trim().length() == 0) {
			mobile = "0000000000";
		}

		String emailId = student.getEmailId();
		if (emailId == null || emailId.trim().length() == 0) {
			emailId = "notavailable@email.com";
		}
		
		/* Temporary Commented by Siddheshwar_Khanse on June 22, 2022 due HDFC maintenance issue.
		String name = "notavailable";
		
		
		model.addAttribute("channel", "10");
		model.addAttribute("account_id", ACCOUNT_ID);
		model.addAttribute("reference_no", adhocPaymentBean.getTrackId());
		model.addAttribute("amount", adhocPaymentBean.getAmount());
		model.addAttribute("mode", "LIVE");
		model.addAttribute("currency", "INR");
		model.addAttribute("currency_code", "INR");
		model.addAttribute("description", adhocPaymentBean.getPaymentType() );// This  should be used in response
		model.addAttribute("return_url", ADHOC_PAYMENT_RETURN_URL);
		model.addAttribute("name", name);
		model.addAttribute("address", address);
		model.addAttribute("city", city);
		model.addAttribute("country", "IND");
		model.addAttribute("postal_code", pin);
		model.addAttribute("phone", mobile);
		model.addAttribute("email", emailId);
		model.addAttribute("algo", "MD5");
		model.addAttribute("V3URL", V3URL);*/
		
		adhocPaymentsLogger.info("Payment Gateways required values adding to model as:");
		adhocPaymentsLogger.info("track_id:"+adhocPaymentBean.getTrackId());
		adhocPaymentsLogger.info("sapid:"+adhocPaymentBean.getSapId());
		adhocPaymentsLogger.info("amount:"+adhocPaymentBean.getAmount());
		adhocPaymentsLogger.info("description:"+"Adhoc Payment for " + adhocPaymentBean.getPaymentType()+" by "+adhocPaymentBean.getSapId());
		adhocPaymentsLogger.info("mobile"+mobile);
		adhocPaymentsLogger.info("email_id"+emailId);
		adhocPaymentsLogger.info("first_name:"+adhocPaymentBean.getFirstName());
		
		model.addAttribute("track_id", adhocPaymentBean.getTrackId());
		model.addAttribute("sapid", adhocPaymentBean.getSapId());
		model.addAttribute("type", "Adhoc");
		model.addAttribute("amount", adhocPaymentBean.getAmount());
		model.addAttribute("description", "Adhoc Payment for " + adhocPaymentBean.getPaymentType()+" by "+adhocPaymentBean.getSapId());
		model.addAttribute("portal_return_url", ADHOC_PAYMENT_RETURN_URL);
		model.addAttribute("created_by", adhocPaymentBean.getSapId());
		model.addAttribute("updated_by", adhocPaymentBean.getSapId());
		model.addAttribute("mobile", mobile);
		model.addAttribute("email_id", emailId);
		model.addAttribute("first_name", adhocPaymentBean.getFirstName());
		model.addAttribute("source", "web");
		model.addAttribute("response_method", "POST");
		adhocPaymentsLogger.info("ServiceRequestController.fillPaymentParametersInMapForAdhocPayment() - END");
	}
	
	@RequestMapping(value = "/student/pay", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView pay(HttpServletRequest request, HttpServletResponse respnse, ModelMap model) {
		
		if (!checkSession(request, respnse)) {
			return new ModelAndView("jsp/login");
		}
		
		return new ModelAndView("jsp/pay");
	}
	
	
	//not in use 
	@RequestMapping(value = "/student/payUsingWallet", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView payUsingWallet(HttpServletRequest request, HttpServletResponse respnse, ModelMap model) {
		
		if (!checkSession(request, respnse)) {
			return new ModelAndView("jsp/login");
		}
		return new ModelAndView("jsp/payUsingWallet");
	}
	
	
	@RequestMapping(value = "/student/payForAdHocPayment", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView payForAdHocPayment(HttpServletRequest request, HttpServletResponse respnse, ModelMap model) {
		respnse.setHeader("Set-Cookie", "SESSION=" + request.getSession().getId() + "; path=/studentportal/; HttpOnly; SameSite=none; Secure");
		adhocPaymentsLogger.info("ServiceRequestController.payForAdHocPayment()");
		return new ModelAndView("jsp/payment");
	}
	
	@RequestMapping(value = "/student/srFeeResponse", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView srFeeResponse(HttpServletRequest request, HttpServletResponse respnse, ModelMap model,
			Model m) {
		if (!checkSession(request, respnse)) {
			return new ModelAndView("jsp/login");
		}
		saveAllTransactionDetails(request);
//		String saveResponse = saveAllTransactionDetails(request);
//		if(!saveResponse.equalsIgnoreCase("success")) {
//			setError(request, saveResponse);
//			return sendBackToServiceRequestPage(request, saveResponse, m);
//		}
		//PaymentHelper paymentHelper = new PaymentHelper();
//		if(request.getSession().getAttribute("paymentOption").equals("paytm")) {
//			String amount = (String) request.getSession().getAttribute("amount");
//			String trackId = (String) request.getSession().getAttribute("trackId");
//			serviceRequestDao.saveCheckSum(trackId, amount, request);
//		}  
	 String errorMessage = paymentHelper.checkErrorInPayment(request);
	 	
	 if (errorMessage != null) {
		 return sendBackToServiceRequestPage(request, errorMessage, m);
	 } else { 
		 return saveSuccessfulTransaction(request, respnse, model);
	 }      
	}
	
	@RequestMapping(value = "/student/srFeeResponseGateway", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView srFeeResponseGateway(HttpServletRequest request, HttpServletResponse respnse, ModelMap model,
			Model m) {
		
		if (!checkSession(request, respnse)) {
			return new ModelAndView("jsp/login");
		}
		String trackId = (String)request.getSession().getAttribute("trackId"); // get tracking number from description as external user do not have login access 
		String amount = (String)request.getSession().getAttribute("amount");
		
		PAYMENTS_LOGGER.info("{} Received payload from payment gateway : {}", trackId, getRequestPayloadAsString(request));
		saveAllTransactionDetailsGateway(request);
		
		
		boolean isSuccessful = "Payment Successfull".equalsIgnoreCase(request.getParameter("transaction_status"));
		boolean isAmountMatching = isAmountMatching(request, amount);
		boolean isTrackIdMatching = isTrackIdMatching(request, trackId);
		String errorMessage = null;
		
		if (!isSuccessful) {
			errorMessage = "Error in processing payment. Error: " + request.getParameter("error") + " Code: "
					+ request.getParameter("response_code");
			PAYMENTS_LOGGER.info("{} isSuccessful errorMessage : {} ", trackId, errorMessage);
		}

		if (!isAmountMatching) {
			errorMessage = "Error in processing payment. Error: Fees " + amount + " not matching with amount paid "
					+ request.getParameter("response_amount");
			PAYMENTS_LOGGER.info("{} isAmountMatching errorMessage : {}",trackId, errorMessage);
		}

		if (!isTrackIdMatching) {
			errorMessage = "Error in processing payment. Error: Track ID: " + trackId
					+ " not matching with Merchant Ref No. " + request.getParameter("merchant_ref_no");
			PAYMENTS_LOGGER.info("{} isTrackIdMatching errorMessage : {} ", trackId,errorMessage);
		} 

		if (errorMessage != null) {
			return sendBackToServiceRequestPage(request, errorMessage, m);
		} else { 
			return saveSuccessfulTransaction(request, respnse, model);
		}      
	}
	
	
//	to be deleted/ api shifted to rest controller
//	@RequestMapping(value = "/m/paymentResponse", method = {RequestMethod.GET})
//	public ModelAndView paymentResponse(HttpServletRequest request, HttpServletResponse response,String status,String message) {
//	
////		request.setAttribute("status", status);
////		request.setAttribute("message", message);
//		
//		return new ModelAndView("jsp/paymentResponse");
//	}
	
	
//	@RequestMapping(value = "/m/examFeesReponse", method = {RequestMethod.GET, RequestMethod.POST})
//	public ModelAndView mexamFeeFinalPaymentResponse(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
//		/*if(!checkSession(request, response)){
//			redirectToPortalApp(response);
//			return null;
//		}*/
//		String typeOfPayment = (String)request.getParameter("PaymentMethod");
//		saveAllTransactionDetails(request);
//
//		paymentHelper.setPaymentReturnUrl("exam_registration_mobile");
//		String errorMessage = paymentHelper.checkErrorInPayment(request);
//		if(errorMessage != null){
//			ModelAndView mv = new ModelAndView("jsp/examresponse");
//			mv.addObject("responseType","error");
//			mv.addObject("response",errorMessage);
//			return new ModelAndView("redirect:" + "/m/paymentResponse?status=error&message="+errorMessage);
//		}else{
//			
//			return msaveSuccessfulTransaction(request, response, model);
//		}
//	}
	
	/*@RequestMapping(value = "/srFeeResponse", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView srFeeResponse(HttpServletRequest request, HttpServletResponse respnse, ModelMap model,
			Model m) {
		if (!checkSession(request, respnse)) {
			return new ModelAndView("jsp/login");
		}
		String typeOfPayment = (String)request.getParameter("PaymentMethod");
		String apiRequestId = (String)request.getParameter("apiRequestId");
		
		
		
		saveAllTransactionDetails(request);

		String trackId = (String) request.getSession().getAttribute("trackId");
		String amount = (String) request.getSession().getAttribute("amount");
		
		String errorMessage = null;
		
		
		boolean isHashMatching = isHashMatching(request);
		boolean isTrackIdMatching = isTrackIdMatching(request, trackId);
		boolean isAmountMatching = isAmountMatching(request, amount);
		boolean isSuccessful = isTransactionSuccessful(request);
		
		if(!"Wallet".equals(typeOfPayment)){
			 isHashMatching = isHashMatching(request);
			 isAmountMatching = isAmountMatching(request, amount);
			 isTrackIdMatching = isTrackIdMatching(request, trackId);
		}
		
		
		if (!isSuccessful) {
			errorMessage = "Error in processing payment. Error: " + request.getParameter("Error") + " Code: "
					+ request.getParameter("ResponseCode");
		}

		if (!isHashMatching) {
			errorMessage = "Error in processing payment. Error: Hashvalue not matching. Tampering in response found. Track ID: "
					+ trackId;
		}

		if (!isAmountMatching) {
			errorMessage = "Error in processing payment. Error: Fees " + amount + " not matching with amount paid "
					+ request.getParameter("Amount");
		}

		if (!isTrackIdMatching) {
			errorMessage = "Error in processing payment. Error: Track ID: " + trackId
					+ " not matching with Merchant Ref No. " + request.getParameter("MerchantRefNo");
		}
			
		if (errorMessage != null) {
			return sendBackToServiceRequestPage(request, errorMessage, m);
		} else {
			return saveSuccessfulTransaction(request, respnse, model);
		}
		
		
		
	
	}*/

	// added to handle pending Payment response 
		@RequestMapping(value = "/student/pendingPaymentFeeResponse", method = { RequestMethod.GET, RequestMethod.POST })
		public ModelAndView pendingPaymentFeeResponse(HttpServletRequest request, HttpServletResponse respnse, ModelMap model,Model m) {
			if (!checkSession(request, respnse)) {
				return new ModelAndView("jsp/login");
			}
			ModelAndView modelAndView = new ModelAndView("jsp/pendingPaymentForm");
			saveAllTransactionDetailsForAdhocPayment(request);
			
			String trackId = (String)request.getSession().getAttribute("trackId"); // get tracking number from description as external user do not have login access 
			String amount = (String)request.getSession().getAttribute("amount");

			boolean isSuccessful = isTransactionSuccessful(request);
			boolean isHashMatching = isHashMatching(request);
			boolean isAmountMatching = isAmountMatching(request, amount);
			boolean isTrackIdMatching = isTrackIdMatching(request, trackId);
			String errorMessage = null;

			if (!isSuccessful) {
				errorMessage = "Error in processing payment. Error: " + request.getParameter("Error") + " Code: "
						+ request.getParameter("ResponseCode");
			}

			if (!isHashMatching) {
				errorMessage = "Error in processing payment. Error: Hashvalue not matching. Tampering in response found. Track ID: "
						+ trackId;
			}

			if (!isAmountMatching) {
				errorMessage = "Error in processing payment. Error: Fees " + amount + " not matching with amount paid "
						+ request.getParameter("Amount");
			}

			if (!isTrackIdMatching) {
				errorMessage = "Error in processing payment. Error: Track ID: " + trackId
						+ " not matching with Merchant Ref No. " + request.getParameter("MerchantRefNo");
			}

			if (errorMessage != null) {
				return sendBackToPendingPaymentRequestPage(request, errorMessage, m);
			} else {
				return saveSuccessfulTransactionOfPendingPayment(request, respnse, model);
			}
			
		}
		
		// added to Handle Adhoc Payment Response 
		@RequestMapping(value = "/student/adhocFeeResponse", method = { RequestMethod.GET, RequestMethod.POST })
		public ModelAndView adhocFeeResponse(HttpServletRequest request, HttpServletResponse respnse, ModelMap model,Model m) {
			
			adhocPaymentsLogger.info("ServiceRequestController.adhocFeeResponse() - START");
			saveAllTransactionDetailsForAdhocPayment(request);
			
			String trackId = (String)request.getSession().getAttribute("trackId"); // get tracking number from description as external user do not have login access 
			String amount = (String)request.getSession().getAttribute("amount");
			
			adhocPaymentsLogger.info("trackId:"+trackId);
			adhocPaymentsLogger.info("amount:"+amount);
			
			//so returning model and view here
			//boolean isSuccessful = isTransactionSuccessful(request);
			//boolean isHashMatching = isHashMatching(request);
			boolean isSuccessful = "Payment Successfull".equalsIgnoreCase(request.getParameter("transaction_status"));
			boolean isAmountMatching = isAmountMatching(request, amount);
			boolean isTrackIdMatching = isTrackIdMatching(request, trackId);
			String errorMessage = null;

			adhocPaymentsLogger.info("isSuccessful:"+isSuccessful);
			adhocPaymentsLogger.info("isAmountMatching:"+isAmountMatching);
			adhocPaymentsLogger.info("isTrackIdMatching:"+isTrackIdMatching);
			
			if (!isSuccessful) {
				errorMessage = "Error in processing payment. Error: " + request.getParameter("error") + " Code: "
						+ request.getParameter("ResponseCode");
				adhocPaymentsLogger.info("isSuccessful errorMessage:"+errorMessage);
			}
			
			/*if (!isHashMatching) {
				errorMessage = "Error in processing payment. Error: Hashvalue not matching. Tampering in response found. Track ID: "
						+ trackId;
			}*/

			if (!isAmountMatching) {
				errorMessage = "Error in processing payment. Error: Fees " + amount + " not matching with amount paid "
						+ request.getParameter("response_amount");
				adhocPaymentsLogger.info("isAmountMatching errorMessage:"+errorMessage);
			}

			if (!isTrackIdMatching) {
				errorMessage = "Error in processing payment. Error: Track ID: " + trackId
						+ " not matching with Merchant Ref No. " + request.getParameter("merchant_ref_no");
				adhocPaymentsLogger.info("isTrackIdMatching errorMessage:"+errorMessage);
			}

			if (errorMessage != null) {
				adhocPaymentsLogger.info("Redirecting back to adhoc payment form with error message.");
				return sendBackToAdhocPaymentRequestPage(request, errorMessage, m);
			} else {
				return saveSuccessfulTransactionOfAdhocPayment(request, respnse, model);
			}
		}

		//private method that redirects user to page instead of updating table again in case webhook updates data first
		private ModelAndView processSuccessTransaction(String trackId, HttpServletRequest request,
				AdhocPaymentStudentPortalBean tempBean) {

			ModelAndView mav = new ModelAndView();

			PAYMENTS_LOGGER
					.info(" in processSuccessTransaction because payment was marked successful by webhook for track id "
							+ tempBean.getTrackId());
			
			mav.setViewName("adhocPaymentCreated");
			mav.addObject("adhocPaymentBean", tempBean);
			request.setAttribute("success", "true");
			request.setAttribute("successMessage", "Payment Made successfully");
			return mav;
		}

	private ModelAndView sendBackToServiceRequestPage(HttpServletRequest request, String errorMessage, Model m) {
		ServiceRequestStudentPortal sr = (ServiceRequestStudentPortal) request.getSession().getAttribute("sr");
		setError(request, errorMessage);
		return addSRForm(sr, request, m);
	}

	private ModelAndView sendBackToPendingPaymentRequestPage(HttpServletRequest request, String errorMessage, Model m) {
		setError(request, errorMessage);
		ModelAndView modelnView = new ModelAndView("jsp/pendingPaymentForm");
		String pendingAmount = (String)request.getSession().getAttribute("pendingAmount");
		AdhocPaymentStudentPortalBean adhocPaymentBean =new AdhocPaymentStudentPortalBean();
		adhocPaymentBean.setDescription("Exam Registration Pending Fee");
		adhocPaymentBean.setAmount(pendingAmount);
		modelnView.addObject("paymentType",paymentTypeList);
		modelnView.addObject("adhocPaymentBean",adhocPaymentBean);
		return modelnView;
	} 
	
	private ModelAndView sendBackToAdhocPaymentRequestPage(HttpServletRequest request, String errorMessage, Model m) {
		adhocPaymentsLogger.info("ServiceRequestController.sendBackToAdhocPaymentRequestPage() - START");
		setError(request, errorMessage);
		ModelAndView modelnView = new ModelAndView("jsp/adhocPaymentForm");
		AdhocPaymentStudentPortalBean adhocPaymentBean =new AdhocPaymentStudentPortalBean();
		modelnView.addObject("paymentType",paymentTypeList);
		modelnView.addObject("yearList",yearList);
		modelnView.addObject("adhocPaymentBean",adhocPaymentBean);
		adhocPaymentsLogger.info("ServiceRequestController.sendBackToAdhocPaymentRequestPage() - END");
		return modelnView;
		
	}
	

	private void saveAllTransactionDetailsForAdhocPayment(HttpServletRequest request) {
		adhocPaymentsLogger.info("ServiceRequestController.saveAllTransactionDetailsForAdhocPayment() - START");
		try {

			String sapid = (String) request.getSession().getAttribute("userId");
			StudentStudentPortalBean student =(StudentStudentPortalBean)request.getSession().getAttribute("student_studentportal");
			String trackId = (String)request.getSession().getAttribute("trackId"); // get tracking number from description as external user do not have login access 
			AdhocPaymentStudentPortalBean bean = (AdhocPaymentStudentPortalBean)request.getSession().getAttribute("adhocPaymentBean");
			
			bean.setSapId(sapid);
			bean.setTrackId(trackId);
			
			if(bean.getSapId() == null) 
				bean.setSapId(request.getParameter("sapid"));
			if(bean.getTrackId() == null)
				bean.setTrackId(request.getParameter("track_id"));
			
			bean.setResponseMessage(request.getParameter("response_message"));
			bean.setTransactionID(request.getParameter("transaction_id"));
			bean.setRequestID(request.getParameter("request_id"));
			bean.setMerchantRefNo(request.getParameter("merchant_ref_no"));
			bean.setSecureHash(request.getParameter("secure_hash"));
			bean.setRespAmount(request.getParameter("response_amount"));
			bean.setRespTranDateTime(request.getParameter("response_transaction_date_time"));
			bean.setResponseCode(request.getParameter("response_code"));
			bean.setRespPaymentMethod(request.getParameter("response_payment_method"));
			//bean.setIsFlagged(request.getParameter("IsFlagged"));
			bean.setPaymentID(request.getParameter("payment_id"));
			bean.setError(request.getParameter("error"));
			bean.setDescription(request.getParameter("description"));

			adhocPaymentsLogger.info("Adhoc Payment details to insert into online transactions:"+bean);
			serviceRequestDao.insertOnlineTransactionForAdhocPayment(bean);

		} catch (Exception e) {
			adhocPaymentsLogger.info("Error while inserting post payment details in online transaction table. Error Message:"+e);
		}
		adhocPaymentsLogger.info("ServiceRequestController.saveAllTransactionDetailsForAdhocPayment() - END");
	}
	
	private String saveAllTransactionDetails(HttpServletRequest request) {

		try {
			//PaymentHelper payment = new PaymentHelper();
			ServiceRequestStudentPortal bean = paymentHelper.CreateResponseBean(request);
			if(bean != null) {
				if(bean.getTrackId().equalsIgnoreCase(bean.getMerchantRefNo())) {
					serviceRequestDao.insertOnlineTransaction(bean);
					return "success";
				}else {
					return "Error: Invalid trackId found";
				}
				
			}else {
				return "Error: Invalid Payment Gateway Selected";
			}

		} catch (Exception e) {
//			e.printStackTrace();
			return "Error: " + e.getMessage();
		}
	}
	
	private String saveAllTransactionDetailsGateway(HttpServletRequest request) {

		try {
			ServiceRequestStudentPortal bean = new ServiceRequestStudentPortal();

			paymentHelper.createResponseBean(request, bean);

			if (bean.getTrackId().equalsIgnoreCase(bean.getMerchantRefNo())) {
				serviceRequestDao.insertOnlineTransaction(bean);
				return "success";
			} else {
				return "Error: Invalid trackId found";
			}

		} catch (Exception e) {
			return "Error: " + e.getMessage();
		}
	}

	public ModelAndView saveSuccessfulTransaction(HttpServletRequest request, HttpServletResponse respnse,
			ModelMap model) {
		if (!checkSession(request, respnse)) {
			return new ModelAndView("jsp/login");
		}
		ModelAndView modelnView = new ModelAndView("jsp/serviceRequest/srCreated");
		
		String sapid = (String) request.getSession().getAttribute("userId");
		String trackId = (String) request.getSession().getAttribute("trackId");
		ServiceRequestStudentPortal sr = (ServiceRequestStudentPortal) request.getSession().getAttribute("sr");
		ServiceRequestStudentPortal paymentResponseBean = (ServiceRequestStudentPortal) request.getSession().getAttribute("paymentResponseBean");
		ArrayList<ServiceRequestStudentPortal> listOfServiceRequestInserted = null;
		
		if("Issuance of Marksheet".equals(sr.getServiceRequestType())){
			listOfServiceRequestInserted = (ArrayList<ServiceRequestStudentPortal>)request.getSession().getAttribute("marksheetDetailAndAmountToBePaidList");
			if(listOfServiceRequestInserted!=null && listOfServiceRequestInserted.size()>0){
				StringBuilder serviceRequestIdList = new StringBuilder();
				StringBuilder descriptionList = new StringBuilder();
				for(ServiceRequestStudentPortal bean : listOfServiceRequestInserted){
					serviceRequestIdList.append(bean.getId()).append(",");
					descriptionList.append(bean.getDescription()).append(",");
				}
				sr.setSrIdList(serviceRequestIdList.toString().substring(0,serviceRequestIdList.toString().length()-1));
				sr.setDescriptionList(descriptionList.toString().substring(0, descriptionList.toString().length()-1));
			}
				
		}
		
		modelnView.addObject("sr", sr);
		//If else loop for storing old assignment/written score depending on the reval subjects opted for 
		if("Assignment Revaluation".equals(sr.getServiceRequestType())){
			ArrayList<StudentMarksBean> studentAssignmentMarksList = (ArrayList<StudentMarksBean>)request.getSession().getAttribute("studentAssignmentMarksList");
			serviceRequestDao.updateStudentAssignmentMarks(studentAssignmentMarksList);
		}else if("Revaluation of Term End Exam Marks".equals(sr.getServiceRequestType())){
			ArrayList<StudentMarksBean> studentWrittenScoreMarkList = (ArrayList<StudentMarksBean>)request.getSession().getAttribute("studentWrittenScoreMarks");
			serviceRequestDao.updateStudentWrittenMarks(studentWrittenScoreMarkList);
		}else if("Exit Program".equals(sr.getServiceRequestType())) {
			//update student table
   	   		servReqServ.updateStudentProgramStatus(sapid); 
		}
		try {
			/*ServiceRequest bean = new ServiceRequest();
			bean.setSapId(sapid);
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
			paymentResponseBean.setRequestStatus(ServiceRequestStudentPortal.REQUEST_STATUS_SUBMITTED);
			StudentStudentPortalBean student = (StudentStudentPortalBean) request.getSession().getAttribute("student_studentportal");
			String courierAmount = (String) request.getSession().getAttribute("courierAmount"); 
			ArrayList<ServiceRequestStudentPortal> marksheetDetailAndAmountToBePaidList = (ArrayList<ServiceRequestStudentPortal>) request.getSession().getAttribute("marksheetDetailAndAmountToBePaidList");
			serviceRequestDao.updateSRTransactionDetails(paymentResponseBean);
			
			ServiceRequestStudentPortal bean = serviceRequestDao.getServiceRequestBySrId(paymentResponseBean.getId());
			serviceRequestDao.insertServiceRequestStatusHistory(bean, "Update");
			
			//added for fee receipt
			//pdfHelper.createSrFeeReceipt(FEE_RECEIPT_PATH,sr,student,courierAmount,marksheetDetailAndAmountToBePaidList);

			FeeReceiptInterface feeReceipt = feeReceiptFactory.getSRType( FeeReceiptFactory.ProductType.valueOf(sr.getServiceRequestType().trim().toUpperCase().replaceAll("-","").replaceAll("\\s","" ))); 
            feeReceipt.createSrFeeReceipt(FEE_RECEIPT_PATH,sr,student,courierAmount,marksheetDetailAndAmountToBePaidList);
			request.setAttribute("success", "true");
			request.setAttribute("successMessage", "Service Request created successfully");

			if("Issuance of Marksheet".equals(sr.getServiceRequestType())){
				List<ServiceRequestStudentPortal> serviceRequestListBean = serviceRequestDao.getListOfServiceRequestBySapidAndTrackId(sapid, trackId);
				for(ServiceRequestStudentPortal serviceBean : serviceRequestListBean){
					handlePostPaymentAction(student, serviceBean);
				}
			}else {
				if(listOfServiceRequestInserted!=null && listOfServiceRequestInserted.size()>0 ){
					for(ServiceRequestStudentPortal serviceBean : listOfServiceRequestInserted){
						handlePostPaymentAction(student, serviceBean);
					}
					modelnView.addObject("listOfServiceRequestInserted", listOfServiceRequestInserted);
					modelnView.addObject("rowCount", listOfServiceRequestInserted.size());
				}else{
					modelnView.addObject("listOfServiceRequestInserted", null);
					modelnView.addObject("rowCount",0);
					handlePostPaymentAction(student, sr);
				}
			}
			

		} catch (Exception e) {
			PAYMENTS_LOGGER.error("{} ERROR trying to save sr response successfuly : {}",trackId, getStackTraceAsString(e));
		}
		return modelnView;
	}
	
	public void msaveSuccessfulTransaction(HttpServletRequest request, HttpServletResponse respnse, ModelMap model) {
 
  
		String sapid = (String) request.getSession().getAttribute("userId");
		String trackId = (String) request.getSession().getAttribute("trackId");
		ServiceRequestStudentPortal sr = (ServiceRequestStudentPortal) request.getSession().getAttribute("sr");
		ServiceRequestStudentPortal paymentResponseBean = (ServiceRequestStudentPortal) request.getSession().getAttribute("paymentResponseBean");
		ArrayList<ServiceRequestStudentPortal> listOfServiceRequestInserted = null;
		
		if("Issuance of Marksheet".equalsIgnoreCase(sr.getServiceRequestType()) || "Issuance of Gradesheet".equalsIgnoreCase(sr.getServiceRequestType())){
			listOfServiceRequestInserted = serviceRequestDao.findByTrackId(trackId);
			//listOfServiceRequestInserted = (ArrayList<ServiceRequest>)request.getSession().getAttribute("marksheetDetailAndAmountToBePaidList");
			
			
			if(listOfServiceRequestInserted!=null && listOfServiceRequestInserted.size()>0){
				StringBuilder serviceRequestIdList = new StringBuilder();
				StringBuilder descriptionList = new StringBuilder();
				for(ServiceRequestStudentPortal bean : listOfServiceRequestInserted){
					serviceRequestIdList.append(bean.getId()).append(",");
					descriptionList.append(bean.getDescription()).append(",");
				}
				sr.setSrIdList(serviceRequestIdList.toString().substring(0,serviceRequestIdList.toString().length()-1));
				sr.setDescriptionList(descriptionList.toString().substring(0, descriptionList.toString().length()-1));
			}
				
		}
		
		//modelnView.addObject("sr", sr);
		//If else loop for storing old assignment/written score depending on the reval subjects opted for 
		if("Assignment Revaluation".equals(sr.getServiceRequestType())){
			ArrayList<StudentMarksBean> studentAssignmentMarksList = (ArrayList<StudentMarksBean>)request.getSession().getAttribute("studentAssignmentMarksList");
			serviceRequestDao.updateStudentAssignmentMarks(studentAssignmentMarksList);
		}else if("Revaluation of Term End Exam Marks".equals(sr.getServiceRequestType())){
			ArrayList<StudentMarksBean> studentWrittenScoreMarkList = (ArrayList<StudentMarksBean>)request.getSession().getAttribute("studentWrittenScoreMarks");
			serviceRequestDao.updateStudentWrittenMarks(studentWrittenScoreMarkList);
		}
		try { 
			paymentResponseBean.setRequestStatus(ServiceRequestStudentPortal.REQUEST_STATUS_SUBMITTED);

			serviceRequestDao.updateSRTransactionDetails(paymentResponseBean);
			
			ServiceRequestStudentPortal bean = serviceRequestDao.getServiceRequestBySrId(paymentResponseBean.getId());
			serviceRequestDao.insertServiceRequestStatusHistory(bean, "Update");
			
			request.setAttribute("success", "true");
			request.setAttribute("successMessage", "Service Request created successfully");

			StudentStudentPortalBean student = (StudentStudentPortalBean) request.getSession().getAttribute("student_studentportal");
			if(listOfServiceRequestInserted!=null && listOfServiceRequestInserted.size()>0 ){
				for(ServiceRequestStudentPortal serviceBean : listOfServiceRequestInserted){
					handlePostPaymentAction(student, serviceBean);
				}
				//modelnView.addObject("listOfServiceRequestInserted", listOfServiceRequestInserted);
				//modelnView.addObject("rowCount", listOfServiceRequestInserted.size());
			}else{
				//modelnView.addObject("listOfServiceRequestInserted", null);
				//modelnView.addObject("rowCount",0);
				handlePostPaymentAction(student, sr);
			}
			
			

		} catch (Exception e) {
//			e.printStackTrace();
		}
		//return modelnView;
	}

	// method to handle Pending sucessful payment //
	public ModelAndView saveSuccessfulTransactionOfPendingPayment(HttpServletRequest request, HttpServletResponse respnse,ModelMap model) {
		

		ModelAndView modelnView = new ModelAndView("jsp/pendingPaymentCreated");

		String sapid = (String) request.getSession().getAttribute("userId");
		String trackId = (String)request.getSession().getAttribute("trackId"); // get tracking number from description as external user do not have login access 
		AdhocPaymentStudentPortalBean adhocPaymentBean  = (AdhocPaymentStudentPortalBean) request.getSession().getAttribute("adhocPaymentBean");
		modelnView.addObject("adhocPaymentBean", adhocPaymentBean);
		StudentStudentPortalBean student = (StudentStudentPortalBean) request.getSession().getAttribute("student_studentportal");
		try {
			AdhocPaymentStudentPortalBean bean = new AdhocPaymentStudentPortalBean();
			bean.setSapId(sapid);
			bean.setTrackId(trackId);
			bean.setEmailId(student.getEmailId());
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
			bean.setRequestStatus(ServiceRequestStudentPortal.REQUEST_STATUS_SUBMITTED);
			bean.setPendingAmount("");
			serviceRequestDao.updatePendingPaymentTransactionDetails(bean); // mark as Successful payment //
			
			
			serviceRequestDao.updateExamBookingAmount(bean);//Update amount of exambookings by adding pending amount

			request.setAttribute("success", "true");
			request.setAttribute("successMessage", "payment made successfully");
			adhocPaymentBean.setEmailId(student.getEmailId());
			adhocPaymentBean.setPaymentType("Exam Registration");
			//sending Email to Student after making Successful Payment //
			MailSender mailer = (MailSender) act.getBean("mailer");
			mailer.sendAdhocPaymentEmail(adhocPaymentBean);

		} catch (Exception e) {
//			e.printStackTrace();
		}
		return modelnView;
	}
	
	/**
	 * API to receive only adhoc transactions from payment gateway and update
	 * accordingly
	 * 
	 * @param Uniform Transaction bean used by payment gateway
	 * @return Okay response with messages
	 */

	@RequestMapping("/m/adhocGatewayResponse")
	@ResponseBody
	public ResponseEntity<String> saveSuccessfulTransaction(@RequestBody TransactionsBean bean) {

		PAYMENTS_LOGGER.info("webhook received payload from gateway : " + bean);

		if (bean.getTrack_id() == null) {
			PAYMENTS_LOGGER.info("Inavlid payload received : " + bean);
			return sendOkayResponse("Inavlid payload received : " + bean);
		}

		AdhocPaymentStudentPortalBean adhocBean = new AdhocPaymentStudentPortalBean();
		String responseMessage = null;

		adhocBean = serviceRequestDao.getAdhocPaymentBeanByTrackId(bean.getTrack_id());

		PAYMENTS_LOGGER.info("bean from database for track id : {}" + adhocBean, bean.getTrack_id());

		try {

			Map<String, String> errorMap = checkForErrorInTransaction(bean, adhocBean);
			
			//error key is only added when transaction has error
			if (errorMap.containsKey("error")) {
				return sendOkayResponse(errorMap.get("error"));
			}

			if (GATEWAY_STATUS_SUCCESSFUL.equalsIgnoreCase(bean.getTransaction_status())) {

				PAYMENTS_LOGGER.info("transaction received for success status for track id : {}, so populating bean",
						bean.getTrack_id());

				populateAdhocSuccessBean(bean, adhocBean);

				// updating adhoc payment for successful transaction
				serviceRequestDao.updateAdhocPaymentTransactionDetails(adhocBean);

				PAYMENTS_LOGGER.info("updated table sending mail now for track id : " + adhocBean.getTrackId() + " to "
						+ adhocBean.getEmailId());

				try {
					MailSender mailer = (MailSender) act.getBean("mailer");

					mailer.sendAdhocPaymentEmail(adhocBean);
				} catch (Exception e) {
					
					PAYMENTS_LOGGER.info("Error sending mail for track id : {} " + e.getMessage(), bean.getTrack_id());
				}
				responseMessage = "Table updated and mail sent for track id : " + bean.getTrack_id();

			} else if (GATEWAY_STATUS_FAILED.equalsIgnoreCase(bean.getTransaction_status())) {
				PAYMENTS_LOGGER.info(
						"transaction received as failed so marked in database for track id : " + bean.getTrack_id());

				adhocBean.setTranStatus(GATEWAY_STATUS_FAILED);
				adhocBean.setLastModifiedBy(WEBHOOK_API);
				
				//updating database for failed transaction
				int rowsAffected = serviceRequestDao.updateFailedAdhocPayments(adhocBean);

				responseMessage = rowsAffected + " rows Updated for adhoc payment as failed for track id : "
						+ bean.getTrack_id();

				PAYMENTS_LOGGER.info(responseMessage);

			} else {
				//in case we receive different transaction status than expected
				responseMessage = "INVALID TRANSACTION STATUS FOR TRACK ID : " + bean.getTrack_id();
			}
			
			PAYMENTS_LOGGER.info(responseMessage);
			return sendOkayResponse(responseMessage);

		} catch (Exception e) {

			responseMessage = "Error while updating transaction and sending mail from webhooks payload  " + bean + " " +  e.getMessage();
			PAYMENTS_LOGGER.info(responseMessage);
			return sendOkayResponse(responseMessage);

		}
	}

	//private method to send 200 response with message
	private ResponseEntity<String> sendOkayResponse(String responseMessage) {
		return new ResponseEntity<String>(responseMessage, HttpStatus.OK);
	}

	//populates adhoc bean with transaction bean
	private void populateAdhocSuccessBean(TransactionsBean bean, AdhocPaymentStudentPortalBean adhocBean) {

		adhocBean.setResponseMessage(bean.getResponse_message());
		adhocBean.setTransactionID(bean.getTransaction_id());
		adhocBean.setMerchantRefNo(bean.getMerchant_ref_no());
		adhocBean.setSecureHash(bean.getSecure_hash());
		adhocBean.setRespAmount(bean.getResponse_amount());
		adhocBean.setRespTranDateTime(bean.getResponse_transaction_date_time());
		adhocBean.setRespPaymentMethod(bean.getResponse_method());
		adhocBean.setPaymentID(bean.getPayment_id());
		adhocBean.setRequestStatus(ServiceRequestStudentPortal.REQUEST_STATUS_SUBMITTED);
		adhocBean.setPendingAmount("");
		adhocBean.setPaymentOption(bean.getPayment_option());

		PAYMENTS_LOGGER.info("created bean to update Database : " + adhocBean.toString());
	}

	//checks error in transaction and returns error key in case error is found
	private Map<String, String> checkForErrorInTransaction(TransactionsBean transactionsBean,
			AdhocPaymentStudentPortalBean adhocBean) {
		Map<String, String> errorMap = new HashMap<>();
		try {

			if (adhocBean.getTrackId() == null) {
				PAYMENTS_LOGGER.info("transaction not found for track id : " + transactionsBean.getTrack_id());
				errorMap.put("error", "transaction not found for track id : " + transactionsBean.getTrack_id());
			}
			// if already successful we'll send back error to avoid spam, multi updates
			else if (ADHOC_STATUS_SUCCESSFUL.equalsIgnoreCase(adhocBean.getTranStatus())) {

				PAYMENTS_LOGGER
						.info("Payment already marked as successful for track id : " + transactionsBean.getTrack_id());
				errorMap.put("error",
						"Payment already marked as successful for track id : " + transactionsBean.getTrack_id());

			//in case it's marked as failed and we get fail webhook again
			} else if (GATEWAY_STATUS_FAILED.equalsIgnoreCase(adhocBean.getTranStatus())
					&& (GATEWAY_STATUS_FAILED.equalsIgnoreCase(transactionsBean.getTransaction_status()))) {

				PAYMENTS_LOGGER
						.info("Payment already marked as failed for track id : " + transactionsBean.getTrack_id());
				errorMap.put("error",
						"Payment already marked as failed for track id : " + transactionsBean.getTrack_id());
			}

		} catch (Exception e) {
			PAYMENTS_LOGGER.info("error while checking for error for track id :  " + transactionsBean.getTrack_id()
					+ e.getMessage());
			errorMap.put("error",
					"error while checking for error for track id :  " + transactionsBean.getTrack_id() + e);
		}

		return errorMap;
	}

	// method to handle Adhoc successful payment request //
	public ModelAndView saveSuccessfulTransactionOfAdhocPayment(HttpServletRequest request, HttpServletResponse respnse,ModelMap model) {
		adhocPaymentsLogger.info("ServiceRequestController.saveSuccessfulTransactionOfAdhocPayment() - START");
		ModelAndView modelnView = new ModelAndView("jsp/adhocPaymentCreated");
		String trackId = (String)request.getSession().getAttribute("trackId"); // get tracking number from description as external user do not have login access 

		AdhocPaymentStudentPortalBean adhocPaymentBean  = (AdhocPaymentStudentPortalBean) request.getSession().getAttribute("adhocPaymentBean");
		StudentStudentPortalBean student = (StudentStudentPortalBean) request.getSession().getAttribute("student_studentportal");
		modelnView.addObject("adhocPaymentBean", adhocPaymentBean);

		
		try {
			//since razorpay and paytm has webhooks, added checks here in case transactions were already 
			//updated by webhooks
			if ("razorpay".equalsIgnoreCase(request.getParameter("payment_option"))
					|| "paytm".equalsIgnoreCase(request.getParameter("payment_option"))) {
				
				AdhocPaymentStudentPortalBean tempBean = new AdhocPaymentStudentPortalBean();
				
				tempBean = serviceRequestDao.getAdhocPaymentBeanByTrackId(trackId);

				if (ADHOC_STATUS_SUCCESSFUL.equalsIgnoreCase(tempBean.getTranStatus()))
					return processSuccessTransaction(trackId, request, tempBean);
			}
			
			AdhocPaymentStudentPortalBean bean = (AdhocPaymentStudentPortalBean) request.getSession().getAttribute("adhocPaymentBean");
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
			bean.setRequestStatus(ServiceRequestStudentPortal.REQUEST_STATUS_SUBMITTED);
			bean.setPendingAmount("");
			bean.setPaymentOption(request.getParameter("payment_option"));
//			modelnView.addObject("adhocPaymentBean", bean);	
			adhocPaymentsLogger.info("Adhoc Payment Bean Post Payment:"+bean);
			serviceRequestDao.updateAdhocPaymentTransactionDetails(bean); // mark Payment as Successful //
			
			adhocPaymentsLogger.info("Payment Made successfully");
			
			request.setAttribute("success", "true");
			request.setAttribute("successMessage", "Payment Made successfully");
			
			adhocPaymentsLogger.info("sending mail to User after making Successful Payment.");
			
			// sending to User after making Successful Payment //
			MailSender mailer = (MailSender) act.getBean("mailer");
			mailer.sendAdhocPaymentEmail(adhocPaymentBean);

		} catch (Exception e) {
			adhocPaymentsLogger.error("Error while saving post payment details:"+e);
		}
		adhocPaymentsLogger.info("ServiceRequestController.saveSuccessfulTransactionOfAdhocPayment() - END");
		return modelnView;
	}
	
	public void handlePostPaymentAction(StudentStudentPortalBean student, ServiceRequestStudentPortal sr) {
		
		String serviceRequestType = sr.getServiceRequestType();
		if (ServiceRequestStudentPortal.DUPLICATE_FEE_RECEIPT.equals(serviceRequestType)) {
			// No Action
		} else if (ServiceRequestStudentPortal.ASSIGNMENT_REVALUATION.equals(serviceRequestType)
				|| ServiceRequestStudentPortal.OFFLINE_ASSIGNMENT_REVALUATION.equals(serviceRequestType)) {
			saveAssignmentRevaluationSubjects(sr);
		} else if (ServiceRequestStudentPortal.DUPLICATE_STUDY_KIT.equals(serviceRequestType)) {
			// No Action
		} else if (ServiceRequestStudentPortal.TEE_REVALUATION.equals(serviceRequestType)) {
			saveTEERevaluationSubjects(student, sr);
		} else if (ServiceRequestStudentPortal.OFFLINE_TEE_REVALUATION.equals(serviceRequestType)) {
			saveTEERevaluationSubjects(student, sr);
		} else if (ServiceRequestStudentPortal.PHOTOCOPY_OF_ANSWERBOOK.equals(serviceRequestType)) {
			savePhotocopySubjects(student, sr);
		} else if (ServiceRequestStudentPortal.ISSUEANCE_OF_MARKSHEET.equals(serviceRequestType) || "Issuance of Gradesheet".equals(serviceRequestType) ) {
			saveMarksheetRequestPostPayment(student, sr);
		} else if (ServiceRequestStudentPortal.ISSUEANCE_OF_CERTIFICATE.equals(serviceRequestType)) {
			saveFinalCertificateRequestPostPayment(student, sr);
		} else if (ServiceRequestStudentPortal.ISSUEANCE_OF_TRANSCRIPT.equals(serviceRequestType)) {
			saveIssuanceOfTranscriptPayment(student, sr);
		}else if (ServiceRequestStudentPortal.ISSUEANCE_OF_BONAFIDE.equals(serviceRequestType)) {
			saveIssuanceOfBonafide(student, sr);
		}else if (ServiceRequestStudentPortal.SUBJECT_REPEAT_MBAWX.equals(serviceRequestType)) {
			// No Action
		}else if (ServiceRequestStudentPortal.EXIT_PROGRAM.equals(serviceRequestType)) {
			saveExitProgramPayment(student, sr);
 		}

		MailSender mailer = (MailSender) act.getBean("mailer");
		mailer.sendSREmail(sr, student);

	}
	private void saveExitProgramPayment(StudentStudentPortalBean student, ServiceRequestStudentPortal sr) {
		serviceRequestDao.insertServiceRequestHistory(sr);
	}
	private void saveIssuanceOfTranscriptPayment(StudentStudentPortalBean student, ServiceRequestStudentPortal sr) {
		serviceRequestDao.insertServiceRequestHistory(sr);

	}

	private void saveIssuanceOfBonafide(StudentStudentPortalBean student, ServiceRequestStudentPortal sr) {
		serviceRequestDao.insertServiceRequestHistory(sr);

	}
	
	private void savePhotocopySubjects(StudentStudentPortalBean student, ServiceRequestStudentPortal sr) {

		String sapid = sr.getSapId();

		ArrayList<String> revaluationSubjects = new ArrayList<String>(
				Arrays.asList(sr.getInformationForPostPayment().split("~")));
		for (String subject : revaluationSubjects) {
			serviceRequestDao.markForPhotocopy(sapid, subject);
		}
	}

	private void saveTEERevaluationSubjects(StudentStudentPortalBean student, ServiceRequestStudentPortal sr) {

		String sapid = sr.getSapId();

		ArrayList<String> revaluationSubjects = new ArrayList<String>(
				Arrays.asList(sr.getInformationForPostPayment().split("~")));
		for (String subject : revaluationSubjects) {
			if ("Online".equals(student.getExamMode())) {
				serviceRequestDao.markOnlineTEEForRevaluation(sapid, subject);
			} else {
				serviceRequestDao.markOfflineTEEForRevaluation(sapid, subject);
			}
		}
	}
	public String expectedClosedDateFromBeanParameter(ServiceRequestStudentPortal sr,HashMap<String,String> mapOfActiveSRTypesAndTAT){
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String expectedClosedDate = "";
		try{
			Date d = dateFormat.parse(sr.getCreatedDate());
	    	Calendar c = Calendar.getInstance();
	    	c.setTime(d);
	    	String tat = mapOfActiveSRTypesAndTAT.get(sr.getServiceRequestType());
	    	c.add(Calendar.DATE, Integer.parseInt(tat));
	    	expectedClosedDate = dateFormat.format(c.getTime()); 	
		}catch(Exception e){
//			e.printStackTrace();
		}
    	
    	return expectedClosedDate;
	}
	private void saveAssignmentRevaluationSubjects(ServiceRequestStudentPortal sr) {
		String sapid = sr.getSapId();
		ArrayList<String> revaluationSubjects = new ArrayList<String>(
				Arrays.asList(sr.getInformationForPostPayment().split("~")));

		for (String subject : revaluationSubjects) {
			serviceRequestDao.markForRevaluation(sapid, subject);
		}
	}
	
	//Deprecated, use unmarkAssignmentRevaluationForCancelledSubjects() method instead for reference massUploadSR(), as used in updateServiceRequestStatusAndReason()
	private void UnMarkAssignmentRevaluationSubjectsForCancelled(ServiceRequestStudentPortal sr) {
		String sapid = sr.getSapId();
		ArrayList<String> revaluationSubjects = new ArrayList<String>(
				Arrays.asList(sr.getInformationForPostPayment().split("~")));

		for (String subject : revaluationSubjects) {
			serviceRequestDao.unmarkForRevaluation(sapid, subject);
		}
	}
	
	//Deprecated, use unmarkTeeRevaluationForCancelledSubjects() method instead for reference massUploadSR(), as used in updateServiceRequestStatusAndReason()
	private void UnmarkForTeeRevaluationSubjectsForCancelled(ServiceRequestStudentPortal sr) {
		String sapid = sr.getSapId();
		ArrayList<String> revaluationSubjects = new ArrayList<String>(
				Arrays.asList(sr.getInformationForPostPayment().split("~")));

		for (String subject : revaluationSubjects) {
			serviceRequestDao.unmarkForTeeRevaluation(sapid, subject);
		}
	}
	
	public void setModelAndViewParametersForWallet(ModelAndView srModelAndView,WalletBean studentWallet,boolean walletExists,String srModelAndViewParameter){
		if(walletExists){
			srModelAndView.addObject("wallet", studentWallet);
			srModelAndView.addObject("walletExists", walletExists);
			srModelAndView.addObject("srModelAndViewParameter", "serviceRequest/duplicateStudyKit");
		}else{
			srModelAndView.addObject("wallet", null);
			srModelAndView.addObject("walletExists", walletExists);
			
		}
		
	}
	public String makeCommaSeperatedShippingAddressFromMap(HashMap<String, String> mapOfShippingFields) {
		return mapOfShippingFields.get("Nearest Landmark") + "," + mapOfShippingFields.get("Street") + ","
				+ mapOfShippingFields.get("City") + "," + mapOfShippingFields.get("State") + ","
				+ mapOfShippingFields.get("Postal Code") + "," + mapOfShippingFields.get("Country") + ","
				+ mapOfShippingFields.get("Locality Name") + "," + mapOfShippingFields.get("House Name");
	}
	public void mapRecordToBeRefundedToWalletBean(AdhocPaymentStudentPortalBean bean,WalletBean walletRecord){
		walletRecord.setTrackId(bean.getTrackId());
		walletRecord.setAmount(bean.getAmount());
		walletRecord.setSapid(bean.getSapId());
		walletRecord.setDescription(bean.getDescription());
		walletRecord.setMerchantRefNo(bean.getMerchantRefNo());
		walletRecord.setRespAmount(bean.getAmount());
		walletRecord.setPaymentID(bean.getPaymentID());
		
	}

	/*added on 6/2/2018*/
	@RequestMapping(value = "/admin/addStudentExtendedSRTimeForm", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView addStudentExtendedSRTimeForm(HttpServletRequest request, HttpServletResponse response,@ModelAttribute ServiceRequestType srBean) {

		ModelAndView modelnview = new ModelAndView("jsp/serviceRequest/srExtendTime");
	
		modelnview.addObject("allRequestTypes", getAllAtiveRequestTypes());
		modelnview.addObject("srBean", srBean);
		return modelnview;
	}
	
	@RequestMapping(value = "/admin/addStudentExtendedSRTime", method = {RequestMethod.POST })
	public ModelAndView addStudentExtendedSRTime(HttpServletRequest request, HttpServletResponse response,@ModelAttribute ServiceRequestType srBean) {
		ModelAndView modelnview = new ModelAndView("jsp/serviceRequest/srExtendTime");
		modelnview.addObject("allRequestTypes", getAllAtiveRequestTypes());
		modelnview.addObject("srBean", srBean);
		String sapidList = generateCommaSeparatedList(srBean
				.getSapIdList());
		String srName= srBean.getServiceRequestName();
		serviceRequestDao.UpdateSRTime(sapidList, srName);
		String successMessage = "SapId's added successfully";
		setSuccess(request, successMessage);
	
		return modelnview;
	}
	
	@RequestMapping(value = "/admin/removeStudentExtendedSRTime", method = {RequestMethod.GET,RequestMethod.POST })
	public ModelAndView removeStudentExtendedSRTime(HttpServletRequest request, HttpServletResponse response,@ModelAttribute ServiceRequestType srBean) {
		ModelAndView modelnview = new ModelAndView("jsp/serviceRequest/srExtendTime");
		modelnview.addObject("allRequestTypes", getAllAtiveRequestTypes());
		modelnview.addObject("srBean", srBean);
		String sapidList = generateCommaSeparatedList(srBean
				.getSapIdList());
		ArrayList<String> userList = new ArrayList<String>(Arrays.asList(sapidList.split(",")));
		String srName= srBean.getServiceRequestName();
		String DBlist = serviceRequestDao.selectExtendedSRTime(srName);
		ArrayList<String> studentList = new ArrayList<String>(Arrays.asList(DBlist.split(",")));
		studentList.removeAll(userList);
		StringBuilder sb = new StringBuilder();
		for (String st : studentList)
		{
		    sb.append(st);
		    sb.append(",");
		}
		String listToUpdate = sb.toString();
		if (listToUpdate.endsWith(",")) {
			listToUpdate = listToUpdate.substring(0,
					listToUpdate.length() - 1);
		}
		serviceRequestDao.UpdateSRTime(listToUpdate, srName);
		
		String successMessage = "SapId's deleted successfully";
		setSuccess(request, successMessage);
		return modelnview;  
	}
	
	/*	end*/
	
//	to be deleted, api shifted to rest controller
//    // Mobile Api Save Feedback
//    @RequestMapping(value = "/m/saveFeedback", method = RequestMethod.POST, consumes="application/json", produces="application/json")
//    public ResponseEntity<Map<String,String>> msaveFeedback(@RequestBody FeedbackBean fb,  @RequestParam("sapid") String sapid){
//         HttpHeaders headers = new HttpHeaders();
//        headers.add("Content-Type", "application/json");
//         Map<String,String> response = new HashMap<String,String>(); 
//         StudentBean studentFeedback = new  StudentBean();
//         studentFeedback.setSapid(sapid);
//         try {    
//         serviceRequestDao.saveFeedBack(fb, studentFeedback);
//         }catch(Exception e){
//               response.put("error","true");
//               response.put("errorMessage", "Error in posting.");
//         }
//         response.put("success", "true");
//         response.put("successMessage", "Thank you for your valuable Suggestion");
//         return new ResponseEntity<>(response,headers,HttpStatus.OK);
//    }
    


    @RequestMapping(value = "/student/saveChangeInPhotograph", method = { RequestMethod.GET, RequestMethod.POST })

	public ModelAndView saveChangeInPhotograph(@ModelAttribute ServiceRequestStudentPortal sr,
			@RequestParam(required = true) MultipartFile changeInPhotographDoc,@RequestParam(required = true) MultipartFile changeInPhotographProofDoc, Model m, HttpServletRequest request,
			ModelMap model) {
    	if (!checkSession(request)) {
			return new ModelAndView("jsp/login");
		}
    	String sapid = (String) request.getSession().getAttribute("userId");
    	sr.setSapId(sapid);
		ServiceRequestStudentPortal response = servReqServ.savePhograph(sr,changeInPhotographDoc,changeInPhotographProofDoc);
		m.addAttribute("sr", response);
		
		if(response.getError()!=null) {
			return new ModelAndView("jsp/serviceRequest/changeInPhotograph");
		}
        ModelAndView mv = new ModelAndView("jsp/serviceRequest/srCreated");
        mv.addObject("sr", sr);
        return mv;
        
	} 
    
//    @RequestMapping(value = "/saveChangeInPhotograph", method = { RequestMethod.GET, RequestMethod.POST })
//	public ModelAndView saveChangeInPhotograph(@ModelAttribute ServiceRequest sr,
//			@RequestParam(required = true) MultipartFile changeInPhotographDoc,@RequestParam(required = true) MultipartFile changeInPhotographProofDoc, Model m, HttpServletRequest request,
//			ModelMap model) {
//		if (!checkSession(request)) {
//			return new ModelAndView("jsp/login");
//		}
//
//		sr.setIssued("N");
//		String sapid = (String) request.getSession().getAttribute("userId");
//		// sapid = "77214002240";
//		StudentBean student = serviceRequestDao.getSingleStudentsData(sapid);
//		sr.setDescription(sr.getServiceRequestType() + " for student " + sapid);
//		sr.setTranStatus(ServiceRequest.TRAN_STATUS_FREE);
//		sr.setCategory("Admission");
//		sr.setSapId(sapid);
//		sr.setRequestStatus(ServiceRequest.REQUEST_STATUS_SUBMITTED);
//		sr.setCreatedBy(sapid);
//		sr.setLastModifiedBy(sapid);
//		sr.setSrAttribute("");
//		serviceRequestDao.insertServiceRequest(sr);
//		request.getSession().setAttribute("sr", sr);
//		m.addAttribute("sr", sr);
//
//		if (changeInPhotographDoc != null) {
//			ServiceRequestDocumentBean document = new ServiceRequestDocumentBean();
//			document.setServiceRequestId(sr.getId());
//			uploadFile(document, changeInPhotographDoc, sapid + "_Photo_Change_Document");
//
//			if (document.getErrorMessage() == null) {
//				document.setDocumentName("Document for Photo Change");
//				serviceRequestDao.insertServiceRequestDocument(document);
//				sr.setHasDocuments("Y");
//				serviceRequestDao.updateDocumentStatus(sr);
//			} else {
//				setError(m, "Error in uploading document " + document.getErrorMessage());
//				return new ModelAndView("jsp/serviceRequest/changeInPhotograph");
//			}
//		}
//		
//		if (changeInPhotographProofDoc != null) {
//			ServiceRequestDocumentBean document = new ServiceRequestDocumentBean();
//			document.setServiceRequestId(sr.getId());
//			uploadFile(document, changeInPhotographProofDoc, sapid + "_Photo_Change_Document_IDProof");
//
//			if (document.getErrorMessage() == null) {
//				document.setDocumentName("ID Proof Document for Photo Change");
//				serviceRequestDao.insertServiceRequestDocument(document);
//				MailSender mailer = (MailSender) act.getBean("mailer");
//				mailer.sendSREmail(sr, student);
//			} else {
//				setError(m, "Error in uploading document " + document.getErrorMessage());
//				return new ModelAndView("jsp/serviceRequest/changeInPhotograph");
//			}
//		}
//		m.addAttribute("sr", sr);
//		//fillPaymentParametersInMap(model, student, sr);
//		return new ModelAndView(new RedirectView("addSRForm"), model);
//	}

    
    @RequestMapping(value="/student/CheckFinalCertificateCount",method= {RequestMethod.POST})
    public @ResponseBody HashMap<String, String> CheckFinalCertificateCount(HttpServletRequest request) {
    	HashMap<String, String> response = new HashMap<>();
    	ServiceRequestStudentPortal sr = new ServiceRequestStudentPortal();
    	sr.setSapId((String) request.getParameter("sapId"));
    	sr.setServiceRequestType((String) request.getParameter("requestType"));
    	int diplomaIssuedCount = serviceRequestDao.getDiplomaIssuedCount(sr);
    	int diplomaCertInitiatedCount = serviceRequestDao.getDiplomaCertInintiatedCount(sr);
		if (diplomaIssuedCount >= 1) {
			response.put("result", "Already free service request there");
	    	response.put("status","500");
	    	return response;
		}
		if (diplomaCertInitiatedCount >= 1) {
			response.put("result", "Your Transaction is under Process! Please wait.");
	    	response.put("status","500");
	    	return response;
		}
		response.put("result", "Applicable for free final certificate service request");
    	response.put("status","200");
    	return response;
    }
    
    
//    to be deleted, api shifted to rest controller
//    @RequestMapping(value="/m/getCheckSumForPaytm",method= {RequestMethod.POST})
//    public @ResponseBody HashMap<String, String> getCheckSumForPaytm(@RequestBody ServiceRequest sr){
//    	
//    	if(sr == null) {
//    		HashMap<String, String> errorResponse = new HashMap<>();
//    		errorResponse.put("status", "500");
//    		errorResponse.put("message","Invalid Request Param formate found");
//    		return errorResponse;
//    	}
//    	
//    	if(sr.getSapId() == null) {
//    		HashMap<String, String> errorResponse = new HashMap<>();
//    		errorResponse.put("status", "500");
//    		errorResponse.put("message","Invalid Request Param SapId found");
//    		return errorResponse;
//    	}
//    	
//    	//PaymentHelper paymentHelper = new PaymentHelper();
//    	StudentInfoCheckDAO studentDao = (StudentInfoCheckDAO)act.getBean("stuentInfoCheckDAO");
//		StudentBean student = studentDao.getstudentData(sr.getSapId());
//    	paymentHelper.generateCheckSum(sr, student, "");
//    	HashMap<String, String> successResponse = new HashMap<>();
//    	successResponse.put("status", "200");
//    	successResponse.put("checkSum",sr.getSecureHash());
//    	return successResponse;
//    }

    @RequestMapping(value="/CheckServiceRequestCount",method= {RequestMethod.POST})
    public @ResponseBody HashMap<String, String> CheckServiceRequestCount(HttpServletRequest request) {
    	HashMap<String, String> response = new HashMap<>();
    	ServiceRequestStudentPortal sr = new ServiceRequestStudentPortal();
    	sr.setSapId((String) request.getParameter("sapId"));
    	sr.setServiceRequestType((String) request.getParameter("requestType"));
    	String isFreeRequest = request.getParameter("isFreeRequest"); 
    	int diplomaIssuedCount = serviceRequestDao.getDiplomaIssuedCount(sr);
    	int diplomaCertInitiatedCount = serviceRequestDao.getDiplomaCertInintiatedCount(sr);
		if (diplomaIssuedCount >= 1 && isFreeRequest == "true") {
			response.put("result", "Already free service request there");
	    	response.put("status","500");
	    	return response;
		}
		if (diplomaCertInitiatedCount >= 1) {
			response.put("result", "Your previous transaction status is in initiate stage. Please try again after 1 hour.");
	    	response.put("status","500");
	    	return response;
		}
		response.put("result", "Applicable for service request");
    	response.put("status","200");
    	return response;
    }
    
    @RequestMapping(value="/student/checkBillDeskStatus",method= {RequestMethod.GET})
    public @ResponseBody String checkBillDeskStatus(HttpServletRequest request) {
    	//PaymentHelper helper = new PaymentHelper();
    	return paymentHelper.getBillDeskTransactionStatus(request.getParameter("tracking_id"));
    }
    
    
    @RequestMapping(value={"/student/getPaymentOptions","/getPaymentOptions"},method= {RequestMethod.GET})
    public @ResponseBody ArrayList<PaymentOptionsStudentPortalBean> getPaymentOptions(HttpServletRequest request) {
    	if("examBooking".equalsIgnoreCase(request.getParameter("source"))) {
    		return serviceRequestDao.getExamBookingPaymentOptions();
    	} 
    	return new ArrayList<>(serviceRequestDao.getPaymentOptions().stream().filter(k -> "paytm".equalsIgnoreCase(k.getName())).collect(Collectors.toList()));
    }
    
//    to be deleted, api shifted to rest controller	
////    service request: program de-registration
//    @RequestMapping(value = "/m/saveProgramDeRegistration", method = RequestMethod.POST, consumes="application/json")
//   	public ResponseEntity<ServiceRequest> MsaveProgramDeRegistration(HttpServletRequest request,@RequestBody ServiceRequest sr)
//   		throws Exception {
//       	HttpHeaders headers = new HttpHeaders();
//   		headers.add("Content-Type", "application/json");
//   		ServiceRequest response = servReqServ.saveProgramDeRegistration(sr);
//   		if(response.getError() !=null) {
//   		     return new ResponseEntity<>(response, headers, HttpStatus.INTERNAL_SERVER_ERROR);
//   	       } 
//   		return new ResponseEntity<>(response,headers, HttpStatus.OK);
//   	} 
    

    
//    to be deleted, api shifted to rest controller
////    service request : Issuance Of Marksheet for MBA WX
//	@RequestMapping(value = "/m/checkMarksheetHistoryForMBAWX", method = RequestMethod.POST )
//	public ResponseEntity<ServiceRequest> McheckMarksheetHistoryForMBAWX(HttpServletRequest request,
//			@RequestBody ServiceRequest sr)throws Exception {
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json");
//		ServiceRequest response =  servReqServ.checkMarksheetHistoryForMBAWX(sr,request);
//		return new ResponseEntity<>(response,headers, HttpStatus.OK);
//	}
    

    
//    to be deleted, api shifted to rest controller
////  service request : Issuance Of Marksheet for MBA WX
//	@RequestMapping(value = "/m/checkMarksheetHistoryForMBAX", method = RequestMethod.POST )
//	public ResponseEntity<ServiceRequest> McheckMarksheetHistoryForMBAX(HttpServletRequest request,
//			@RequestBody ServiceRequest sr)throws Exception {
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json");
//		ServiceRequest response =  servReqServ.checkMarksheetHistoryForMBAX(sr,request);
//		return new ResponseEntity<>(response,headers, HttpStatus.OK);
//	}
  
    
    @RequestMapping(value = "/student/checkFinalCertificateEligibilitynew", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView checkFinalCertificateEligibilitynew(@ModelAttribute ServiceRequestStudentPortal sr,
			HttpServletRequest request) {
		if (!checkSession(request)) {
			return new ModelAndView("jsp/login");
		}
		String sapid = (String) request.getSession().getAttribute("userId");
		sr.setSapId(sapid);
		sr.setWantAtAddress("No");
		StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(sapid);
		sr.setAbcId(student.getAbcId());
			String isLateral ="";
			String cpsi="";
			String previousStudentId="";
		try {
			 isLateral = student.getIsLateral();
			 cpsi = student.getConsumerProgramStructureId();
			 previousStudentId = student.getPreviousStudentId();
			 if(student.getConsumerProgramStructureId().isEmpty() || student.getConsumerProgramStructureId()==null) {
					setError(request, "Unable to find masterKey.");
					return new ModelAndView("jsp/serviceRequest/selectSRNew");
				}
			 if(student.getIsLateral().isEmpty() || student.getIsLateral()==null) {
				 isLateral="N";
				}
			}catch(Exception e) {
//				e.printStackTrace();
				setError(request, "Unable to find previous student master key/islateral details");
				return new ModelAndView("jsp/serviceRequest/selectSRNew");
			}
		ArrayList<String> applicableSubjects = new ArrayList<>();
		applicableSubjects = serviceRequestDao.getApplicableSubjectNew(cpsi);
		if(applicableSubjects.size() ==  0 ) {
			setError(request, "No Applicable subjects found");
			return new ModelAndView("jsp/serviceRequest/selectSRNew");
			}
		List<String> subjectsCleared = serviceRequestDao.getSubjectsClearedCurrentProgramNew(sapid, isLateral,cpsi);
		int numberOfsubjectsCleared =subjectsCleared.size();
		if("Y".equalsIgnoreCase(isLateral)) {
			if(StringUtils.isEmpty(student.getPreviousStudentId())) {
				
				boolean sapidExist = customCourseWaiverServiceImpl.checkSapidExist(sapid);
				if(!sapidExist) {
					setError(request, "Unable to find previous student Id.");
					return new ModelAndView("jsp/serviceRequest/selectSRNew");
				}
			}
			List<String> waivedOffSubjectsbefore = (List<String>) request.getSession().getAttribute("waivedOffSubjects"); //contains all the passed subjects of previous program
			List<String> waivedOffSubjects =new ArrayList<String>();
			if(waivedOffSubjectsbefore!=null) {
	            if(student.getPrgmStructApplicable().equalsIgnoreCase("Jul2019") && waivedOffSubjectsbefore.contains("Strategic Management")) {
	                waivedOffSubjectsbefore.add("Decision Science");
	            }
	           
	            for(String subject : waivedOffSubjectsbefore) {
	                if(!waivedOffSubjects.contains(subject)){ 
	                    waivedOffSubjects.add(subject); 
	                }
	            }
			}
            int waivedOffSubjectsCount = 0;
			for(String str:waivedOffSubjects) {
				if(applicableSubjects.contains(str) && !subjectsCleared.contains(str)) {
					waivedOffSubjectsCount++;
				}
			}
			numberOfsubjectsCleared+=waivedOffSubjectsCount;
		}
		
		int noOfSubjectsToClearProgram = 0;
		
		HashMap<String,ProgramsStudentPortalBean> programInfoList = serviceRequestDao.getProgramDetails();
		ProgramsStudentPortalBean bean = programInfoList.get(student.getConsumerProgramStructureId());
		noOfSubjectsToClearProgram = Integer.parseInt(bean.getNoOfSubjectsToClear().trim());
		
		//for exit student
		if(student.getProgramStatus()!=null && student.getProgramStatus().equals("Program Withdrawal")) {
			boolean closedSr = serviceRequestDao.getStudentsClosedExitSR(sapid);
			if(closedSr) {
				numberOfsubjectsCleared = noOfSubjectsToClearProgram;
			}
//			ServiceRequestStudentPortal serReq = servReqServ.checkIfStudentApplicableForExitProgram(student);
			else {
				setError(request, "Please raise Exit Program Service Request first. If you already raised please wait for Approval");
				return new ModelAndView("jsp/serviceRequest/selectSRNew");			
			} 
		}
		System.out.println("numberOfsubjectsCleared"+numberOfsubjectsCleared);
		System.out.println("noOfSubjectsToClearProgram"+noOfSubjectsToClearProgram);
		if (numberOfsubjectsCleared != noOfSubjectsToClearProgram) {
			setError(request, "You have not yet cleared all subjects!");
			return new ModelAndView("jsp/serviceRequest/selectSRNew");
		} else {
			ModelAndView modelnView = new ModelAndView("jsp/serviceRequest/diplomaConfirmationNew");
			modelnView.addObject("sr", sr);
			modelnView.addObject("usersapId",sr.getSapId());
			int diplomaIssuedCount = serviceRequestDao.getDiplomaIssuedCount(sr);
			if (diplomaIssuedCount >= 1) {
				modelnView.addObject("charges", SECOND_DIPLOMA_FEE);
				modelnView.addObject("duplicateDiploma", "true");
			} else {
				modelnView.addObject("charges", 0);
				modelnView.addObject("duplicateDiploma", "false");
			}
			return modelnView;
		}
	}
    
    @RequestMapping(value = "/student/saveProgramWithdrawal")  
   	public ModelAndView saveProgramWithdrawal(HttpServletRequest request,ServiceRequestStudentPortal sr,Model m)  
   		throws Exception {
		if (!checkSession(request)) {
			return new ModelAndView("jsp/login");
		} 
    	String sapid = (String) request.getSession().getAttribute("userId");
    	sr.setSapId(sapid); 
    	sr.setDevice("WebApp");
    	//save SR
   		ServiceRequestStudentPortal response = servReqServ.saveProgramWithdrawal(sr);
   		if(response.getError()!="true" && sapid!=null) {
   			//update student table
   	   		int count = servReqServ.updateStudentProgramStatus(sapid); 
   	   		if(count>0) {
   	   			setSuccess(request, "Success!! Service request raised "); 
	   			request.getSession().setAttribute("sr", sr);
	   			m.addAttribute("sr", sr);
	   			return new ModelAndView("jsp/serviceRequest/srCreated");
   	   		}
   		}else {
   			setError(request, sr.getErrorMessage() );  
   	    } 
		return addSRForm(sr, request, m);
   	} 

    @RequestMapping(value = "/student/checkIfStudentApplicableForWithdrawal", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView checkIfStudentApplicableForWithdrawal(@ModelAttribute ServiceRequestStudentPortal sr,
			HttpServletRequest request) {
		if (!checkSession(request)) {
			return new ModelAndView("jsp/login");
		}
		StudentStudentPortalBean student = (StudentStudentPortalBean)request.getSession().getAttribute("student_studentportal");
		
		boolean alreadyRaisedForExit = servReqServ.checkAlreadyRaisedForExit(student.getSapid());
		
		//boolean flag = servReqServ.checkIfStudentApplicableForWithdrawal(student);
		 //if(flag) {
		
        if(alreadyRaisedForExit) {
        	setError(request, "Sorry! You are not Eligible to raise SR for Program WithDrawal");  
    	    return new ModelAndView("jsp/serviceRequest/selectSRNew"); 
		}
        
	    	ModelAndView modelnView = new ModelAndView("jsp/serviceRequest/programWithdrawalNew");
			modelnView.addObject("student", student);
			return modelnView;    
	    //} 
	    /*setError(request, "Error!! Couldn't raise Service request");  
	    return new ModelAndView("jsp/serviceRequest/selectSR");*/		
    }
    
    @RequestMapping(value = "/admin/printFormForWithdrawal")  
   	public ModelAndView printFormForWithdrawal(HttpServletRequest request, @ModelAttribute ServiceRequestStudentPortal sr,Model m)  
   		throws Exception {

		if (!checkSession(request)) {
			return new ModelAndView("jsp/login");
		} 
		
		ModelAndView modelnView = new ModelAndView("jsp/serviceRequest/printWithdrawalForm");
		ServiceRequestStudentPortal srbean = new ServiceRequestStudentPortal();
		modelnView.addObject("bean", srbean);  
		
		ResponseStudentPortalBean response = servReqServ.printFormForWithdrawal(sr.getSapId());
		
		if(response.isState()) {
			setSuccess(request, response.getMessage());
			modelnView.addObject("filePath", SERVICE_REQUEST_FILES_PATH+"Withdrawal_Form.pdf");  
		}else {
			setError(request, response.getMessage());  
		}
		
		return modelnView; 
    }
    
    @RequestMapping(value = "/admin/printWithdrawalForm", method = { RequestMethod.GET})
   	public ModelAndView printWithdrawalForm(HttpServletRequest request,Model m)  
   		throws Exception {
		if (!checkSession(request)) {
			ModelAndView modelnView = new ModelAndView("jsp/login");
			return modelnView;
		} 
		ModelAndView modelnView = new ModelAndView("jsp/serviceRequest/printWithdrawalForm");
		ServiceRequestStudentPortal sr = new ServiceRequestStudentPortal();
		modelnView.addObject("bean", sr); 
		return modelnView;
    }

    @RequestMapping(value = "/student/checkIfStudentApplicableForExitProgram", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView checkIfStudentApplicableForExitProgram(@ModelAttribute ServiceRequestStudentPortal sr, HttpServletRequest request) {
		if (!checkSession(request)) {
			return new ModelAndView("jsp/login");
		}
		StudentStudentPortalBean student = (StudentStudentPortalBean)request.getSession().getAttribute("student_studentportal");
//		OLD Logic to check Eligibility, Updated New Logic as per Card 12106
//		ServiceRequestStudentPortal serReq = servReqServ.checkIfStudentApplicableForExitProgram(student);
		ServiceRequestStudentPortal serReq = new ServiceRequestStudentPortal();
		String errorMessage = "";
		try {
			serReq = servReqServ.checkIfStudentIsApplicableForExitPrograms(student);
		}catch(Exception e) {
			serReq.setIsCertificate(false);
			serReq.setError("Internal Server Issue.");
		}
		
		if(serReq.getIsCertificate()) {
	    	ModelAndView modelnView = new ModelAndView("jsp/serviceRequest/exitProgramNew");
			modelnView.addObject("student", student);
			modelnView.addObject("serviceRequest", serReq);
			return modelnView;    
	    }
		
	    if(!StringUtils.isBlank(serReq.getError())) {
	    	errorMessage = serReq.getError();
	    }
	    
	    setError(request, "Sorry! You are not Eligible to raise SR for Exit Program "+errorMessage);  
	    return new ModelAndView("jsp/serviceRequest/selectSRNew"); 
    }

 
    @RequestMapping(value = "/student/saveExitProgram")  
   	public ModelAndView saveExitProgram(HttpServletRequest request,ServiceRequestStudentPortal sr,Model m)  
        throws Exception {
		if (!checkSession(request)) {
			return new ModelAndView("jsp/login");
		} 
    	String sapid = (String) request.getSession().getAttribute("userId");
    	sr.setSapId(sapid); 
    	
    	sr.setDevice("WebApp");
    	sr.setTranStatus(ServiceRequestStudentPortal.TRAN_STATUS_FREE);
    	sr.setModeOfDispatch("LC");
    	sr.setIssued("N");
		sr.setRequestStatus(ServiceRequestStudentPortal.REQUEST_STATUS_SUBMITTED);
    	//save SR 
		ServiceRequestStudentPortal response = servReqServ.saveExitProgram(sr);  
        if(response.getError()!="true" && sapid!=null) {
   			//update student table
   	   		int count = servReqServ.updateStudentProgramStatus(sapid); 
   	   		if(count>0) {
   	   			setSuccess(request, "Success!! Service request raised ");
   	   			request.getSession().setAttribute("sr", sr);
   	   			m.addAttribute("sr", sr);
   	   			return new ModelAndView("jsp/serviceRequest/srCreated");
   	   		}
   		}else {
   			setError(request, sr.getErrorMessage() );  
   	    } 
		return addSRForm(sr, request, m);
   	}  
 
    /*   @RequestMapping(value = "/student/saveExitProgramAndPayment", method = { RequestMethod.POST })
	public ModelAndView saveExitProgramAndPayment(@ModelAttribute ServiceRequestStudentPortal sr, Model m, @RequestParam(required = true)  MultipartFile nameOnCertificateDoc,
        HttpServletRequest request, ModelMap model) {
		if (!checkSession(request)) {
			return new ModelAndView("jsp/login");
		}
		
		String sapid = (String) request.getSession().getAttribute("userId");
		// sapid = "77214002240";
		StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(sapid);
		String sem = request.getParameter("semester");
		
		sr.setCategory("Admission");
		sr.setModeOfDispatch("Courier");
		sr.setIssued("Y");
		sr.setTranStatus(ServiceRequestStudentPortal.TRAN_STATUS_INITIATED);
		try {
		//sr.setLandMark(student.getLandMark());
		sr.setPin(student.getPin());
		sr.setLocality(student.getLocality());
		sr.setStreet(student.getStreet());
		sr.setHouseNoName(student.getHouseNoName());
		sr.setCity(student.getCity());
		sr.setState(student.getState());
		sr.setCountry(student.getCountry());
		}catch(Exception e ) {
		}
		
		populateServiceRequestObject(sr, request);
		
		ServiceRequestStudentPortal response = servReqServ.saveExitProgram(sr,nameOnCertificateDoc);
		if(response.getError()=="true" ) {
			setError(request, sr.getErrorMessage() ); 
			ModelAndView modelnView =new ModelAndView("jsp/serviceRequest/exitProgram");
			modelnView.addObject("student", student);
			modelnView.addObject("sr", sr); 
			return modelnView;
		}
		request.getSession().setAttribute("sr", sr);
		m.addAttribute("sr", sr);
		//fillPaymentParametersInMap(model, student, sr);
		String requestId = sr.getTrackId()+ RandomStringUtils.randomAlphanumeric(20);
		//return proceedToPayOptions(model,requestId,ra); 
		//return new ModelAndView(new RedirectView("pay"), model);
		
		if(request.getParameter("paymentOption").equals("hdfc")) {
			request.getSession().setAttribute("paymentOption","hdfc");
			fillPaymentParametersInMap(model, student, sr);
			return new ModelAndView(new RedirectView("pay"), model);
		}
		String paymentOption = request.getParameter("paymentOption");
		ModelAndView mv = new ModelAndView(paymentOption + "Pay");	//set payment jsp file name;
		//PaymentHelper paymentHelper = new PaymentHelper();
		String checkSum = paymentHelper.generateCommonCheckSum(sr, student, requestId,paymentOption);
		if(checkSum != "true") {
			setError(request, "Error: " + checkSum);
			return addSRForm(sr, request, m);
		}
		request.getSession().setAttribute("paymentOption",paymentOption);
		mv = paymentHelper.setCommonModelData(mv, sr, student, requestId,paymentOption);
		return mv; 
		
		
    }*/
    

    
//    to be deleted, api shifted to rest controller
////  service request : Issuance Of Marksheet for MBA WX
//	@RequestMapping(value = "/m/getStudentSubjectRepeatStatusMBAWX", method = RequestMethod.POST )
//	public ResponseEntity<ServiceRequest> MgetStudentSubjectRepeatStatusMBAWX(HttpServletRequest request, @RequestBody ServiceRequest sr)throws Exception {
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json");
//		ServiceRequest response =  servReqServ.getSubjectRepeatStatusForStudent(sr.getSapId());
//		return new ResponseEntity<>(response,headers, HttpStatus.OK);
//	}

    
//    to be deleted, api shifted to rest controller
//	@RequestMapping(value = "/m/saveSubjectRegistrationSRPaymentForMBAWX", method = RequestMethod.POST, consumes="application/json", produces="application/json")
//	public ResponseEntity<ServiceRequest> MsaveSubjectRegistrationSRPaymentForMBAWX(HttpServletRequest request, HttpServletResponse response, @RequestBody ServiceRequest sr)throws Exception {
//
//		if("http://localhost:8080/".equals(SERVER_PATH)) {
//			response.setHeader("Set-Cookie", "JSESSIONID=" + request.getSession().getId() + "; Path=/studentportal; HttpOnly; SameSite=none;");
//		} else {
//			response.setHeader("Set-Cookie", "JSESSIONID=" + request.getSession().getId() + "; Path=/studentportal; HttpOnly; SameSite=none; Secure");
//		}
//		ServiceRequest srResponse =  new ServiceRequest();
//		try {
//			srResponse =  servReqServ.saveSubjectRegistrationSRPaymentForMBAWX(sr);
//			srResponse.setError("false");
//		}catch (Exception e) {
//			srResponse.setError("true");
//			srResponse.setErrorMessage("Error Initiating Service Request!");
//		}
//		
//		return new ResponseEntity<>(srResponse, HttpStatus.OK);
//		
//	}
	
	@RequestMapping(value = "/initiateSRByTrackId", method = {RequestMethod.POST})
	public ModelAndView initiateSRByTrackId(@ModelAttribute ServiceRequestStudentPortal sr, Model m, HttpServletRequest request, ModelMap model) {


		
		StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(sr.getSapId());
		//student.setAddress(studentAddress);// Setting address since it was
											// failing for case 77114000467
//		sr.setSapId(student.getSapid());
//		sr.setTotalAmountToBePayed((String)request.getSession().getAttribute("amount"));
//		sr.setMarksheetDetailAndAmountToBePaidList((ArrayList<ServiceRequest>)request.getSession().getAttribute("marksheetDetailAndAmountToBePaidList"));
//		sr.setCourierAmount((String)request.getSession().getAttribute("courierAmount"));
//		sr.setDevice("WebApp");
		ServiceRequestStudentPortal service =  serviceRequestDao.getServiceRequestBySapidAndTrackId(sr.getSapId(), sr.getTrackId());
		service.setIsMobile(true);
		String requestId = service.getTrackId();
		service.setProductType(sr.getProductType());
		request.getSession().setAttribute("trackId", service.getTrackId());
		request.getSession().setAttribute("sr", service);
		request.getSession().setAttribute("amount", service.getAmount());
		
		if(request.getParameter("paymentOption").equals("hdfc")) {
			request.getSession().setAttribute("paymentOption","hdfc");
			fillPaymentParametersInMap(model, student, service);
			return new ModelAndView(new RedirectView("jsp/pay"), model);
		}
		
		String paymentOption = request.getParameter("paymentOption"); 

		ModelAndView mv = new ModelAndView("jsp/"+ paymentOption + "Pay");

		String checkSum = paymentHelper.generateCommonCheckSum(service, student, requestId,paymentOption);

		if(checkSum != "true") {
			setError(request, "Error: " + checkSum);
			return checkMarksheetHistory(service, request);
		}

		request.getSession().setAttribute("paymentOption",paymentOption);
		mv = paymentHelper.setCommonModelData(mv, service, student, requestId,paymentOption);

		request.getSession().setAttribute("sr", service);
		String programForHeader =  (String) request.getSession().getAttribute("programForHeaderPortal");
		student.setProgramForHeader(programForHeader);
		request.getSession().setAttribute("student_studentportal", student);
		request.getSession().setAttribute("userId", service.getSapId());
		request.getSession().setAttribute("trackId", service.getTrackId());
		return mv;
	}
	
	
//	to be deleted, api shifted to rest controller
//	@RequestMapping(value = "/m/findServiceRequest", method = RequestMethod.POST, consumes="application/json", produces="application/json")
//	public ResponseEntity<ServiceRequestResponse> findServiceRequest(HttpServletRequest request, @RequestBody ServiceRequest sr)throws Exception {
//
//		ServiceRequestResponse response =  servReqServ.getSRStatus(sr);
//		
//		
//		
//		return new ResponseEntity<>(response, HttpStatus.OK);
//		
//	}
	
	
	
	
	//Code shifted to mass Update sr controller
//	/**
//	 * Displays Mass update Service Request Status Form
//	 * @return massUpdateSR view
//	 */
//	@RequestMapping(value = "/admin/massUpdateSRStatusForm" ,method = {RequestMethod.GET ,RequestMethod.POST })
//	public String massUpdateSRStatusForm() {
//		return "jsp/serviceRequest/massUpdateSR";
//	}
//	
//	/**
//	 * Mass update Service Request status of Service Request IDs provided by the Admin user. 
//	 * @param request - HttpServletRequest
//	 * @param redirectAttributes - RedirectAttributes to display Model Attributes on redirect
//	 * @param serviceRequestIds - String containing the serviceRequestIds provided by the user
//	 * @param requestStatus - status to be applied to the provided Service Request records
//	 * @param cancellationReason - cancellation reason to be applied to the provided Service Request records
//	 * @return redirects to massUpdateSR view displaying a response message
//	 */
//	@PostMapping(value = "/admin/massUpdateSRStatus")
//	public ModelAndView massUpdateSRStatus(HttpServletRequest request, final RedirectAttributes redirectAttributes, @RequestParam String serviceRequestIds, 
//									@RequestParam String requestStatus, @RequestParam(required = false) String cancellationReason) {
//		String userId = (String) request.getSession().getAttribute("userId");
//		ModelAndView model = new ModelAndView("jsp/serviceRequest/massUpdateSR");
//		try {
//			String[] serviceRequestIdsArray = serviceRequestIds.split("\\R");												//split the string on line breaks (\R)
//			
//			Map<String, String> response = servReqServ.massUpdateSR(Arrays.asList(serviceRequestIdsArray), requestStatus, cancellationReason, userId);
//			logger.info("Mass updated Service Request status: {}, initiated by user: {}. Response status: {}, {} records updated successfully. Error message: {}", 
//						requestStatus, userId, response.get("status"), response.get("successCount"), response.get("errorMessage"));
//			
//			//Adding Redirect Attributes to display Messages to the user
//			model.addObject("status", response.get("status"));
//			model.addObject("successCount", response.get("successCount"));
//			model.addObject("statusMessage", response.get("errorMessage"));
//		}
//		catch(Exception ex) {
//			ex.printStackTrace();
//			logger.error("Error encountered while Mass updating Service Request status: {}, initiated by user: {}, Exception thrown: ", requestStatus, userId, ex);
//			
//			//Adding Redirect Attributes to display Error Message to the user. IllegalArgumentException thrown for invalid fields.
//			String errorMessage = (ex instanceof IllegalArgumentException) ? ex.getMessage() : "Error while Mass updating Service Request Status. Please try again!";
//			model.addObject("status", "error");
//			model.addObject("statusMessage", errorMessage);
//		}
//		
//		return model;
//	}
	
	/*
	 * mbawx:applicable-change in DOB,change in name,change in photograph,Program
	 * De-Registration,Issuance of Gradesheet,Change in Specialisation. Jul2017 program structure:remove
	 * -TEE_REVALUATION OFFLINE_TEE_REVALUATION PHOTOCOPY_OF_ANSWERBOOK. EPBM,MPDV:
	 * remove-ISSUEANCE_OF_CERTIFICATE,ISSUEANCE_OF_MARKSHEET,
	 * ISSUEANCE_OF_TRANSCRIPT. if not EPBM,MPDV:remove TEE_REVALUATION
	 */
	@ResponseBody
	@RequestMapping(value = "/insertSrTypeMasterKeyMapping", method = { RequestMethod.GET})
		public String insertSrTypeMaterKeyMapping(HttpServletRequest request,Model m)  
			throws Exception {
		
		ArrayList<String>  mbaWxSRs = new ArrayList<String>(Arrays.asList("Change in Specialisation","Program De-Registration","Subject Repeat MBA - WX","Issuance of Gradesheet"));
		
		ArrayList<String> SrsNotApplicableForJul2017=new ArrayList<String>(Arrays.asList("Revaluation of Term End Exam Marks","Photocopy of Written Exam Answer Books","Revaluation of Written Exam Answer Books"));
	
		ArrayList<String> SrsNotApplicableForEpbm=new ArrayList<String>(Arrays.asList("Issuance of Final Certificate","Issuance of Marksheet","Issuance of Transcript"));
		
		ArrayList<String> SrsCommonForAll=new ArrayList<String>(Arrays.asList("Change in DOB","Change in Name","Change in Photograph","Issuance of Final Certificate","Issuance of Bonafide","Issuance of Transcript"));
		
		ArrayList<String> SrsNotApplicableForNormalPg=new ArrayList<String>(Arrays.asList("Revaluation of Term End Exam Marks"));
		
		ArrayList<String>  MSci_SRS = new ArrayList<String>(Arrays.asList("Program De-Registration","Subject Repeat M.Sc. AI and ML Ops","Issuance of Gradesheet"));
		
		
	
		ArrayList<SrTypeMasterKeyMapping>  map = new ArrayList<SrTypeMasterKeyMapping>();
		
		
		try {
			ArrayList<ProgramsStudentPortalBean> consumerProgramStructureList = serviceRequestDao.getConsumerProgramStructureList();
	
			ArrayList<ServiceRequestType> srtypes = serviceRequestDao.getSrTypeList();
			
			for (ProgramsStudentPortalBean cpm : consumerProgramStructureList) {
				//mbawx
					if(cpm.getCode().equalsIgnoreCase("MBA - WX")) {
						for (ServiceRequestType srtype : srtypes) {
							if(
								(	mbaWxSRs.contains(srtype.getServiceRequestName())
								||  SrsCommonForAll.contains(srtype.getServiceRequestName())
								)
									) {
								SrTypeMasterKeyMapping localMap=new SrTypeMasterKeyMapping();
								localMap.setSrtype_Id(srtype.getId()+"");
								localMap.setMasterkey(cpm.getId());
								
								map.add(localMap);
								
							}
						}
						
					}
				//For Msci
					else if(Arrays.asList(Msci_MASTERKEYS).contains(cpm.getId())) {
						
						for (ServiceRequestType srtype : srtypes) {
							if(
								(	MSci_SRS.contains(srtype.getServiceRequestName())
								||  SrsCommonForAll.contains(srtype.getServiceRequestName())
								)
									) {
							
								SrTypeMasterKeyMapping localMap=new SrTypeMasterKeyMapping();
								localMap.setSrtype_Id(srtype.getId()+"");
								localMap.setMasterkey(cpm.getId());
								
								map.add(localMap);
								
							}
						}
						
					}
				//pg EPBM,MPDV
					else if(cpm.getCode().equalsIgnoreCase("EPBM") || cpm.getCode().equalsIgnoreCase("MPDV")) {
						
						for (ServiceRequestType srtype : srtypes) {
							if(!SrsNotApplicableForEpbm.contains(srtype.getServiceRequestName()) &&	!mbaWxSRs.contains(srtype.getServiceRequestName()) && !MSci_SRS.contains(srtype.getServiceRequestName())
									) {
								
								SrTypeMasterKeyMapping localMap=new SrTypeMasterKeyMapping();
								localMap.setSrtype_Id(srtype.getId()+"");
								localMap.setMasterkey(cpm.getId());
								
								
								if(cpm.getProgramStructure().equalsIgnoreCase("Jul2017")) {
									if(!SrsNotApplicableForJul2017.contains(srtype.getServiceRequestName())){
										map.add(localMap);
									}
									
								}else {
									map.add(localMap);
								}
							}
						}
					}
				//others pg
					else {
						for (ServiceRequestType srtype : srtypes) {
							if(!SrsNotApplicableForNormalPg.contains(srtype.getServiceRequestName())
									&&  !mbaWxSRs.contains(srtype.getServiceRequestName())
									&&  !MSci_SRS.contains(srtype.getServiceRequestName())
									) {
								SrTypeMasterKeyMapping localMap=new SrTypeMasterKeyMapping();
								localMap.setSrtype_Id(srtype.getId()+"");
								localMap.setMasterkey(cpm.getId());
								
								if(cpm.getProgramStructure().equalsIgnoreCase("Jul2017")) {
									if(!SrsNotApplicableForJul2017.contains(srtype.getServiceRequestName())) {
										map.add(localMap);
									}
									
								}else {
									map.add(localMap);
								}
							}
						}
					}
					
			}
			serviceRequestDao.truncateMasterKeyAndSrMappingTable();
			for(SrTypeMasterKeyMapping maptoInsert:map) {
				serviceRequestDao.insertMasterKeyAndSrMapping(maptoInsert);
			}
			return "Success";
		} catch (Exception e) {
			return "failed";
		}
		
		/*for testing
		 * select cps.id,p.code,st.serviceRequestName from
		 * portal.srtype_masterkey_mapping m
		 * 
		 * left join portal.service_request _types st on m.srtype_Id=st.id left join
		 * exam.consumer_program_structure cps on cps.id=m.masterkey left join
		 * exam.program p on cps.programId=p.id where m.masterkey=40
		 */
	}
	@RequestMapping(value = "/m/getSRTypesForMbaWx", method = RequestMethod.GET)
	public ResponseEntity<ArrayList<String>> getSRTypesForMbaWx(HttpServletRequest request){
		
		ArrayList<String> requestTypes=new ArrayList<String>(); 
	    requestTypes = serviceRequestDao.getSRTypesForConsumerProgramStructureId("111");
	    
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		return new ResponseEntity<ArrayList<String>>(requestTypes,headers,HttpStatus.OK);
	} 
	@RequestMapping(value = "SearchSrTypesForm", method = RequestMethod.GET)
	public ModelAndView SearchSrTypesForm(HttpServletRequest request)  {
		ArrayList<ConsumerProgramStructureStudentPortal> consumerType = serviceRequestDao.getConsumerTypeList();
		AssignmentStudentPortalFileBean assignmentStatus = new AssignmentStudentPortalFileBean();
		
	    ModelAndView modelnView = new ModelAndView("jsp/serviceRequest/SearchSrTypesForm");
	    ServiceRequestStudentPortal sr = new ServiceRequestStudentPortal();
		modelnView.addObject("consumerType", consumerType); 
		modelnView.addObject("assignmentStatus",assignmentStatus);
		modelnView.addObject("bean", sr); 
		return modelnView;
	} 
	@RequestMapping(value = "SearchSrTypes", method = RequestMethod.POST)
	public ModelAndView SearchSrTypes(HttpServletRequest request, @ModelAttribute AssignmentStudentPortalFileBean assignmentStatus)  {
		ArrayList<ConsumerProgramStructureStudentPortal> consumerType = serviceRequestDao.getConsumerTypeList();
		ArrayList<ServiceRequestType> requestTypes=new ArrayList<ServiceRequestType>(); 
		ArrayList<String> consumerProgramStructureIds = serviceRequestDao.getconsumerProgramStructureIds(assignmentStatus.getProgramId(),assignmentStatus.getProgramStructureId(),assignmentStatus.getConsumerTypeId());
		System.out.println("consumerProgramStructureIds"+consumerProgramStructureIds);
		
		try {
			requestTypes = serviceRequestDao.getSRMasterKeyMappingForCpsId(consumerProgramStructureIds.get(0));
		} catch (Exception e) { 
			e.printStackTrace();
		}
		
	    ModelAndView modelnView = new ModelAndView("jsp/serviceRequest/SearchSrTypesForm");
	    ServiceRequestStudentPortal sr = new ServiceRequestStudentPortal();
		modelnView.addObject("consumerType", consumerType); 
		modelnView.addObject("assignmentStatus",assignmentStatus);
		modelnView.addObject("requestTypes",requestTypes);
		modelnView.addObject("rowCount",requestTypes.size());
		modelnView.addObject("bean", sr); 
		return modelnView;
	} 
	@RequestMapping(value = "activateDeactivateSRsByCpsId", method = RequestMethod.POST)
	public void activateDeactivateSRsByCpsId(HttpServletRequest request, @RequestBody ServiceRequestType srTypeBean)  {
		 
		try {
			serviceRequestDao.activateDeactivateSRsByCpsId(srTypeBean);
		} catch (Exception e) { 
			e.printStackTrace();
		}
		 
	} 
 
	@RequestMapping(value = "/admin/searchSRDetailsByTrackId", method = RequestMethod.GET)
	public ModelAndView searchSRDetailsByTrackId(HttpServletRequest request, HttpServletResponse response) {
		if (!checkSession(request)) {
			return new ModelAndView("jsp/login");
		}

	ModelAndView mv = new ModelAndView("jsp/searchSRDetailsByTrackId");
	ArrayList<ServiceRequestStudentPortal> serviceRequestList = null;
	String trackId = request.getParameter("trackId");
	if(trackId != null && !"".equalsIgnoreCase(trackId)) {
		serviceRequestList = serviceRequestDao.findByTrackId(trackId);

	}
	mv.addObject("trackId", trackId);
	mv.addObject("serviceRequestList", serviceRequestList);
	return mv;
}
	/** 
	  * Downloads the pdf for SR stored locally
	  * @param request HttpServletRequest
	  * @param response HttpServletResponse
	  * @param m Model 
	  */
	@RequestMapping(value = "/admin/downloadFile", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView downloadFileForAdmin(HttpServletRequest request,HttpServletResponse response, Model m) {
		ModelAndView modelnView = new ModelAndView("jsp/serviceRequest/searchSR");


		String fullPath = request.getParameter("filePath");

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
//			e.printStackTrace();
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in downloading file.");
		}
		return modelnView;
	}
	
	/**
	 * userId is fetched from session and stored as a model attribute, and the view is returned
	 * @param request - HttpServletRequest
	 * @return view
	 */
	@GetMapping(value = "/student/changeFatherMotherSpouseNameSRForm")
	public ModelAndView changeFatherMotherSpouseNameSRForm(HttpServletRequest request) {
		ModelAndView modelView = new ModelAndView("jsp/serviceRequest/changeFatherMotherSpouseNameNew");
		modelView.addObject("studentSapid", request.getSession().getAttribute("userId"));
		return modelView;
	}
	
	/**
	 * Change in Father/Mother/Spouse Name Service Request raised by the student is validated and stored, 
	 * and a view is returned displaying a confirmation message on success.
	 * @param request - HttpServletRequest
	 * @param model - Object to add attributes to the view returned in case of Exception
	 * @param redirectAttributes - Object to add attributes to the view on redirect
	 * @param srDto - Bean containing details of the Change Details Service Request
	 * @return redirect to SR Confirmation page, in case of error return to the Change Father/Mother/Spouse Name SR Form
	 */
	@PostMapping(value="/student/changeFatherMotherSpouseNameSR") 
	public RedirectView changeFatherMotherSpouseNameSR(HttpServletRequest request, Model model, final RedirectAttributes redirectAttributes, 
												@ModelAttribute ChangeDetailsSRDto srDto) {
		try {
			logger.info("Validating and storing the Change Details Service Request raised by the student: {}", srDto.toString());
			ServiceRequestStudentPortal serviceRequestBean =  servReqServ.changeFatherMotherSpouseName(srDto);
			logger.info("Change Details Service Request successfully raised of Student: {}, with Service Request ID: {}", serviceRequestBean.getSapId(), serviceRequestBean.getId());
			redirectAttributes.addFlashAttribute("sr", serviceRequestBean);
			return new RedirectView("displaySrCreatedConfirmation");
		}
		catch(Exception ex) {
			ex.printStackTrace();
			model.addAttribute("studentSapid", request.getSession().getAttribute("userId"));
			
			//Add Error Message to display to the user, IllegalArgumentException thrown on BindingResult error
			String errorMessage = (ex instanceof IllegalArgumentException) ? ex.getMessage() : "Error while raising Service Request. Please try again!";
			redirectAttributes.addFlashAttribute("error", "true");
			redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
			logger.error("Error Message shown to user: {} Exception thrown: {}", errorMessage, ex.toString());
			
			return new RedirectView("changeFatherMotherSpouseNameSRForm");
		}
	}
	
	/**
	 * userId is fetched from session and stored as a model attribute, and the view is returned
	 * @param request - HttpServletRequest
	 * @return view
	 */
	@GetMapping(value = "/student/changeInContactDetailsSRForm")
	public ModelAndView changeInContactDetailsSRForm(HttpServletRequest request) {
		ModelAndView modelView = new ModelAndView("jsp/serviceRequest/changeInContactDetailsNew");
		modelView.addObject("studentSapid", request.getSession().getAttribute("userId"));
		return modelView;
	}
	
	/**
	 * Change in Contact Details Service Request raised by the student is validated and stored, 
	 * and a view is returned displaying a confirmation message on success.
	 * @param request - HttpServletRequest
	 * @param model - Object to add attributes to the view returned in case of Exception
	 * @param redirectAttributes - Object to add attributes to the view on redirect
	 * @param srDto - Bean containing details of the Change Details Service Request
	 * @return redirect to SR Confirmation page, in case of error return to the Change Contact Details SR Form
	 */
	@PostMapping(value="/student/changeInContactDetailsSR")
	public RedirectView changeInContactDetailsSR(HttpServletRequest request, Model model, final RedirectAttributes redirectAttributes, 
											@ModelAttribute ChangeDetailsSRDto srDto) {
		try {
			logger.info("Validating and storing the Change Details Service Request raised by the student: {}", srDto.toString());
			ServiceRequestStudentPortal serviceRequestBean =  servReqServ.changeContactDetails(srDto);
			logger.info("Change Details Service Request successfully raised of Student: {}, with Service Request ID: {}", serviceRequestBean.getSapId(), serviceRequestBean.getId());
			redirectAttributes.addFlashAttribute("sr", serviceRequestBean);
			return new RedirectView("displaySrCreatedConfirmation");
		}
		catch(Exception ex) {
//			ex.printStackTrace();
			model.addAttribute("studentSapid", request.getSession().getAttribute("userId"));
			
			//Add Error Message to display to the user, IllegalArgumentException thrown on BindingResult error
			String errorMessage = (ex instanceof IllegalArgumentException) ? ex.getMessage() : "Error while raising Service Request. Please try again!";
			redirectAttributes.addFlashAttribute("error", "true");
			redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
			logger.error("Error Message shown to user: {} Exception thrown: {}", errorMessage, ex.toString());
			
			return new RedirectView("changeInContactDetailsSRForm");
		}
	}
	
	/**
	 * Confirmation of the Service Request raised by the student is displayed to the user.
	 * @return view displaying the Service Request confirmation message
	 */
	@GetMapping(value="/student/displaySrCreatedConfirmation")
	public String displaySrCreatedConfirmation() {
		return "jsp/serviceRequest/srCreated";
	}
	
	@PostMapping(value = "/student/saveSpecialNeedSR")
	public ModelAndView saveSpecialNeedSR(@ModelAttribute ServiceRequestStudentPortal sr,
			@RequestParam(required = true) MultipartFile medical,
			Model m, HttpServletRequest request, ModelMap model,HttpServletResponse response) throws Exception {
		if (!checkSession(request)) {
			return new ModelAndView("jsp/login");
		}
		try
		{ 
		String sapid = (String) request.getSession().getAttribute("userId");
		sr.setSapId(sapid);
		StudentStudentPortalBean student=(StudentStudentPortalBean)request.getSession().getAttribute("student_studentportal");
		ServiceRequestStudentPortal result = servReqServ.saveSpecialNeedSR(sr,medical,student);
		if (StringUtils.isBlank(result.getError())) {
			List<String> srTypes =  new ArrayList<String>();
			m.addAttribute("requestTypes",srTypes);
			return new ModelAndView("redirect:/student/srCreated/"+sr.getId());
		}
		}
		catch(Exception e)
		{
			setError(request,e.getMessage());
			return selectSRForm(sr, request, response, m);
		}
		return new ModelAndView("jsp/serviceRequest/selectSRNew");
		
	}

	@PostMapping(value = "/student/saveScribeSR")
	public ModelAndView saveScribeSR(@ModelAttribute ServiceRequestStudentPortal sr,
			@RequestParam(required = true) MultipartFile resume,
			Model m, HttpServletRequest request, ModelMap model,HttpServletResponse response) throws Exception {
		if (!checkSession(request)) {
			return new ModelAndView("jsp/login");
		}
		try
		{ 
		String sapid = (String) request.getSession().getAttribute("userId");
		sr.setSapId(sapid);
		StudentStudentPortalBean student = (StudentStudentPortalBean)request.getSession().getAttribute("student_studentportal");
		ServiceRequestStudentPortal result = servReqServ.saveScribeSR(sr,resume,student);
		if (StringUtils.isBlank(result.getError())) {
			List<String> srTypes =  new ArrayList<String>();
			m.addAttribute("requestTypes",srTypes);
			return new ModelAndView("redirect:/student/srCreated/"+sr.getId());
		}
		}
		catch(Exception e)
		{
			setError(request,e.getMessage());
			return selectSRForm(sr, request, response, m);
		}
		return new ModelAndView("jsp/serviceRequest/selectSRNew");
		
	}
	public ModelAndView checkIfStudentAlreadySubmittedOrApprovedForSpecialNeedSR(HttpServletRequest request, String serviceRequestType) {
		if (!checkSession(request)) {
			return new ModelAndView("jsp/login");
		}
		String sapid=(String) request.getSession().getAttribute("userId");
			List<ServiceRequestStudentPortal> ListofSR=serviceRequestDao.getSRBySapIdandtype(sapid, serviceRequestType);
 			if(ListofSR.size()>0){
 				for (ServiceRequestStudentPortal SR : ListofSR) {
 					if(ServiceRequestStudentPortal.REQUEST_STATUS_IN_PROGRESS.equals(SR.getRequestStatus())){
 						setError(request,"Your SR for Special Need is In Progress.");
 						return new ModelAndView("jsp/serviceRequest/selectSRNew");
 					}else if(ServiceRequestStudentPortal.REQUEST_STATUS_SUBMITTED.equals(SR.getRequestStatus())){
 						setError(request,"Already submitted Special Need SR. Please Wait for Approval.");
 						return new ModelAndView("jsp/serviceRequest/selectSRNew");
 					}else{
 						setError(request,"Your SR for Special Need is already approved.");
 						return new ModelAndView("jsp/serviceRequest/selectSRNew");
 					}
 				}
 			}
 			return new ModelAndView("jsp/serviceRequest/specialNeedSRNew");
	}
	
	public ModelAndView checkIfStudentAlreadySubmittedOrApprovedForScribe(HttpServletRequest request, String serviceRequestType) {
		if (!checkSession(request)) {
			return new ModelAndView("jsp/login");
		}
		String exammonth="";
		String sapid=(String) request.getSession().getAttribute("userId");
			List<ServiceRequestStudentPortal> spceialNeedsSRList=serviceRequestDao.getSRBySapIdandtype(sapid, "Special Needs SR"); 
			if(spceialNeedsSRList.size()>0){
				for (ServiceRequestStudentPortal SR : spceialNeedsSRList) {
					if(!ServiceRequestStudentPortal.REQUEST_STATUS_CLOSED.equalsIgnoreCase(SR.getRequestStatus())){
						setError(request,"Your SR for Special Need is Not approved.");
 						return new ModelAndView("jsp/serviceRequest/selectSRNew");
					}else{
						String examdetails=serviceRequestDao.getExamMonthByAcadMonth(CURRENT_ACAD_MONTH,CURRENT_ACAD_YEAR);
						int listofresitsubject=serviceRequestDao.getpassfailstatus(sapid);
						if(listofresitsubject>0) {
							exammonth=serviceRequest.returnmonthforResitExam();
						}else {
							exammonth=examdetails;
						}
						List<ServiceRequestStudentPortal> checkalreadyregistered=serviceRequestDao.getSRByStudentDetails(sapid,serviceRequestType, exammonth, CURRENT_ACAD_YEAR);
						if(checkalreadyregistered.size()>0){
				 				for (ServiceRequestStudentPortal SR1 : checkalreadyregistered) {
				 					if(ServiceRequestStudentPortal.REQUEST_STATUS_IN_PROGRESS.equalsIgnoreCase(SR1.getRequestStatus())){
				 						setError(request,"Your SR for Scribe for Term End Exam is In Progress.");
				 						return new ModelAndView("jsp/serviceRequest/selectSRNew");
				 					}else if(ServiceRequestStudentPortal.REQUEST_STATUS_SUBMITTED.equals(SR1.getRequestStatus())){
				 						setError(request,"Already submitted Scribe for Term End Exam SR. Please Wait for Approval.");
				 						return new ModelAndView("jsp/serviceRequest/selectSRNew");
				 					}else{
				 						setError(request,"Your SR for Scribe for Term End Exam is already approved.");
				 						return new ModelAndView("jsp/serviceRequest/selectSRNew");
				 					}
				 				}
				 		}
					}
				}	
			}else {
				setError(request,"Please get approved Special Needs SR.");
				return new ModelAndView("jsp/serviceRequest/selectSRNew");
			}
 		return new ModelAndView("jsp/serviceRequest/ScribeDocumentUploadNew");
	}
	
	/**
	 * Return stack trace as String as the method name suggests
	 * @param throwable
	 * @return String stack trace
	 */
	public static String getStackTraceAsString(Throwable throwable) {
	    StringWriter stringWriter = new StringWriter();
	    throwable.printStackTrace(new PrintWriter(stringWriter));
	    return stringWriter.toString();
	}
	
	/**
	 * Return all payload from HttpServletRequest Object as String
	 * @param HttpServletRequest Object
	 * @return formatted payload as String 
	 */
	public static String getRequestPayloadAsString(HttpServletRequest request) {
	    StringBuilder builder = new StringBuilder(256);
	    
	    Map<String, String[]> parameterMap = request.getParameterMap();
	    Iterator<Entry<String, String[]>> iterator = parameterMap.entrySet().iterator();
	    
	    while (iterator.hasNext()) {
	        Map.Entry<String, String[]> entry = iterator.next();
	        String key = entry.getKey();
	        String value = entry.getValue()[0];
	        
	        builder.append(key).append(" : ").append(value);
	        
	        if (iterator.hasNext()) 
	            builder.append(" | ");
	    }
	    
	    return builder.toString();
	}

} 
