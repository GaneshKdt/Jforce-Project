package com.nmims.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.nmims.beans.PCPBookingTransactionBean;
import com.nmims.beans.StudentAcadsBean;
import com.nmims.beans.UserAuthorizationBean;
import com.nmims.daos.ContentDAO;
import com.nmims.daos.PCPBookingDAO;
import com.nmims.helpers.AESencrp;
import com.nmims.helpers.MailSender;
import com.nmims.helpers.PCPPDFCreator;

/**
 * Handles requests for the application home page.
 */
@Controller
public class PCPController extends BaseController{

	@Autowired
	ApplicationContext act;

	private static final Logger logger = LoggerFactory.getLogger(PCPController.class);
	private final int pageSize = 10;
	private static final int FEES_PER_SUBJECT = 250;
	private final String ONLINE_PAYMENT_INITIATED = "Online Payment Initiated"; 
	private final String ONLINE_PAYMENT_SUCCESSFUL = "Online Payment Successful"; 
	
	@Value( "${SERVER_PATH}" )
	private String SERVER_PATH;
	
	@Value( "${MARKSHEETS_PATH}" )
	private String MARKSHEETS_PATH;
	
	private final String BOOKING_SUCCESS_MSG = "Your PCP registration is successfully completed. "
			+ "Please click <a href=\"selectPCPSubjectsForm\"> here </a> to verify subjects pending to be booked.";
	
	private ArrayList<String> yearList = new ArrayList<String>(Arrays.asList( 
			"2014","2015","2016","2017","2018" ));
	
	@Value("#{'${ACAD_YEAR_LIST}'.split(',')}")
	private List<String> ACAD_YEAR_LIST;
	@Value( "${SECURE_SECRET}" )
	private String SECURE_SECRET; // secret key;
	@Value( "${ACCOUNT_ID}" )
	private String ACCOUNT_ID;
	@Value( "${V3URL}" )
	private String V3URL;
	@Value( "${PCP_BOOKING_RETURN_URL}" )
	private String PCP_BOOKING_RETURN_URL;
	@Value( "${PCPTICKET_PATH}" )
	private String PCPTICKET_PATH;
	@Value( "${STUDENT_PHOTOS_PATH}" )
	private String STUDENT_PHOTOS_PATH;
	@Value( "${CURRENT_ACAD_MONTH}" )
	private String CURRENT_ACAD_MONTH;
	@Value( "${CURRENT_ACAD_YEAR}" )
	private String CURRENT_ACAD_YEAR;
	
	@Value( "${SERVICE_TAX_RULE}" )
	private String SERVICE_TAX_RULE;
	//commented 
	
	@Autowired
	PCPBookingDAO pcpBookingDAO;

	@Autowired
	ContentDAO contentDAO;

	private HashMap<String, String> programCodeNameMap = null;
	private ArrayList<String> currentYearList = new ArrayList<String>(Arrays.asList( 
			"2015","2016","2017")); 
	
	public HashMap<String, String> getProgramMap(){
		if(this.programCodeNameMap == null || this.programCodeNameMap.size() == 0){
			this.programCodeNameMap = pcpBookingDAO.getProgramDetails();
		}
		return programCodeNameMap;
	}

	@RequestMapping(value = "/student/selectPCPSubjectsForm", method = {RequestMethod.GET,RequestMethod.POST})
	public ModelAndView selectPCPSubjectsForm(HttpServletRequest request, HttpServletResponse respnse, Model m){
		if(!checkSession(request, respnse)){
			return new ModelAndView("studentPortalRediret");
		}
		ModelAndView modelnView = new ModelAndView("pcp/selectSubjects");
		String sapId = (String)request.getSession().getAttribute("userId_acads");
		//String sapId = request.getParameter("sapid");
		StudentAcadsBean student = (StudentAcadsBean)request.getSession().getAttribute("student_acads");
		boolean hasBookedSubject = false;
		boolean hasSubjectsToBook = false;
		String sapid = student.getSapid();
		HashMap<String, PCPBookingTransactionBean> subjectDetailsMap = new HashMap<>();
		PCPBookingTransactionBean pcpBooking = new PCPBookingTransactionBean();
		String encrypt = "";
		boolean isRegistraionLive = pcpBookingDAO.isConfigurationLive("PCP Registration");
		
		try{
			String encryption  = URLEncoder.encode(AESencrp.encrypt(sapid)); 
		}catch(Exception e){
			  
		}
		
		//Allow particular student to book PCP if window is closed.//
		
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
			isRegistraionLive = true;
		}
		if(!isRegistraionLive){
			setError(request, "PCP Registration is not Live currently");
		}
		modelnView.addObject("isRegistraionLive", isRegistraionLive);
		
		ArrayList<PCPBookingTransactionBean> tempSubjects = pcpBookingDAO.getSubjectsForCurrentCycle(sapId);
		ArrayList<PCPBookingTransactionBean> subjects = new ArrayList<>(); 
		if(tempSubjects == null){
			tempSubjects = new ArrayList<>();
		}
		
		if(student.getPreviousStudentId() != null && !"".equals(student.getPreviousStudentId()) && student.getIsLateral().equalsIgnoreCase("Y")){
			ArrayList<String> waivedOffSubjects = student.getWaivedOffSubjects();
			
			for (PCPBookingTransactionBean currentSemSubjectBean : tempSubjects) {
				if(!waivedOffSubjects.contains(currentSemSubjectBean.getSubject())){
					subjects.add(currentSemSubjectBean);//Consider current sem subject only if it is not waived off 
				}
			}
		}else{
			subjects.addAll(tempSubjects);//If no sujects are waived off then take all subjects
		}
		
		ArrayList<PCPBookingTransactionBean> failedSubjects = pcpBookingDAO.getFailedSubjects(sapId);
		ArrayList<PCPBookingTransactionBean> bookedSubjects = pcpBookingDAO.getConfirmedBooking(sapId);
		
		ArrayList<String> bookedSubjectsList = new ArrayList<>();
		for (PCPBookingTransactionBean bean : bookedSubjects) {
			bookedSubjectsList.add(bean.getSubject());
		}
		
		if(failedSubjects != null){
			subjects.addAll(0, failedSubjects);
		}
		
		if(subjects == null || subjects.size() == 0){
			setError(request, "No Subjects found for PCP Registration");
		}
		
		pcpBooking.setYear(CURRENT_ACAD_YEAR);
		pcpBooking.setMonth(CURRENT_ACAD_MONTH);
		
		for (PCPBookingTransactionBean bean : subjects) {
			subjectDetailsMap.put(bean.getSubject(), bean);
			if(bookedSubjectsList.contains(bean.getSubject())){
				bean.setBooked("Y");
				hasBookedSubject = true;
			}else{
				hasSubjectsToBook = true;
			}
		}
		request.getSession().setAttribute("subjectDetailsMap", subjectDetailsMap);
		int subjectsCount = subjects != null ? subjects.size() : 0;
		m.addAttribute("subjectsCount", subjectsCount);
		m.addAttribute("subjects", subjects);
		m.addAttribute("hasBookedSubject", hasBookedSubject);
		m.addAttribute("hasSubjectsToBook", hasSubjectsToBook);

		
		modelnView.addObject("pcpBooking", pcpBooking);

		return modelnView;
	}
	
	
	//Commented By Riya as logic is shifted in PCPAdminController
	/*
	@RequestMapping(value="/massPCPBookingDownloadForm",method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView massPCPBookingDownloadForm(HttpServletRequest request, HttpServletResponse respnse){
		ModelAndView modelAndView = new ModelAndView("massPCPBooking");
		modelAndView.addObject("yearList", ACAD_YEAR_LIST);
		modelAndView.addObject("pcpBookingBean", new PCPBookingTransactionBean());
		return modelAndView;
	}
	
	@RequestMapping(value="/massPCPBookingDownload",method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView massPCPBookingDownload(@ModelAttribute PCPBookingTransactionBean pcpBookingBean,HttpServletRequest request, HttpServletResponse respnse){
		
		PCPBookingDAO pcpDao = (PCPBookingDAO)act.getBean("pcpBookingDAO");
		PCPPDFCreator pcpPDFHelper = new PCPPDFCreator();
		
		ModelAndView modelAndView = new ModelAndView("massPCPBooking");
		modelAndView.addObject("yearList", ACAD_YEAR_LIST);
		modelAndView.addObject("pcpBookingBean", new PCPBookingTransactionBean());
		
		boolean alreadyDownloadedForSelectedYearAndMonth = false;
		alreadyDownloadedForSelectedYearAndMonth = pcpDao.alreadyDownloadedDocument(pcpBookingBean, "PCP Fee Receipt");
		
		if(alreadyDownloadedForSelectedYearAndMonth)
		{
			setError(request,"PCP Fee Receipt Already Generated For Selected Year-Month "+pcpBookingBean.getYear()+"-"+pcpBookingBean.getMonth());
			return modelAndView;
		}
		
		try{
		ArrayList<String> listOfDistincSapidBookingsFromMonthAndYear = pcpDao.listOfDistincSapidBookingsFromMonthAndYear(pcpBookingBean);
		HashMap<String,ArrayList<PCPBookingTransactionBean>> mapOfSapidAndPCPBookingListFromMonthAndYearWithoutAcadsFlag = pcpDao.mapOfSapidAndPCPBookingListFromMonthAndYearWithoutAcadsFlag(pcpBookingBean);
		ArrayList<PCPBookingTransactionBean> pcpBookingListForBatchUpdate = new ArrayList<PCPBookingTransactionBean>();
		
		for(String sapid : listOfDistincSapidBookingsFromMonthAndYear){
			
			StudentBean student = new StudentBean();
			PCPBookingTransactionBean pcpTransactionBean = new PCPBookingTransactionBean();
			ArrayList<PCPBookingTransactionBean> pcpBookingList = mapOfSapidAndPCPBookingListFromMonthAndYearWithoutAcadsFlag.get(sapid);
			
			student.setSapid(sapid);
			student.setProgram(pcpBookingList.get(0).getProgram());
			student.setFirstName(pcpBookingList.get(0).getFirstName());
			student.setLastName(pcpBookingList.get(0).getLastName());
			
				String fileName = pcpPDFHelper.createReceiptAndReturnFileName(pcpBookingList, MARKSHEETS_PATH, student);	
				pcpTransactionBean.setSapid(sapid);
				pcpTransactionBean.setFilePath(fileName);
				pcpTransactionBean.setMonth(pcpBookingBean.getMonth());
				pcpTransactionBean.setYear(pcpBookingBean.getYear());
			
			pcpBookingListForBatchUpdate.add(pcpTransactionBean);
			
		}
		pcpDao.batchInsertOfDocumentRecords(pcpBookingListForBatchUpdate, "PCP Fee Receipt");
		setSuccess(request,"Successfully generated PCP Receipts");
		}catch(Exception e){
			setError(request,"Error in generating PCP Receipts");
			  
		}	
		
		return modelAndView;
	}*/
	
	@RequestMapping(value = "/student/confirmSubjects", method = {RequestMethod.POST})
	public ModelAndView confirmSubjects(HttpServletRequest request, HttpServletResponse respnse, @ModelAttribute PCPBookingTransactionBean pcpBooking) {
		if(!checkSession(request, respnse)){
			return new ModelAndView("studentPortalRediret");
		}
		
		ModelAndView modelnView = new ModelAndView("pcp/confirmSubjects");
		ArrayList<String> subjects = pcpBooking.getApplicableSubjects();

		request.getSession().setAttribute("subjects", subjects);

		if(subjects == null || subjects.size() == 0){
			setError(request, "Please select at least one subject");
		}
		int subjectsCount = subjects != null ? subjects.size() : 0;
		modelnView.addObject("subjectsCount", subjectsCount);
		modelnView.addObject("subjects", subjects);
		modelnView.addObject("feesPerSubject", FEES_PER_SUBJECT);
		modelnView.addObject("totalFees", FEES_PER_SUBJECT * subjectsCount);
		pcpBooking.setAmount(String.valueOf(FEES_PER_SUBJECT * subjectsCount));
		modelnView.addObject("pcpBooking", pcpBooking);
		

		return modelnView;
	}

	@RequestMapping(value = "/student/goToGateway", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView goToGateway(HttpServletRequest request, HttpServletResponse respnse, @ModelAttribute PCPBookingTransactionBean pcpBooking, ModelMap model/*,RedirectAttributes ra*/) {

		if(!checkSession(request, respnse)){
			return new ModelAndView("studentPortalRediret");
		}
		
		HashMap<String, PCPBookingTransactionBean> subjectDetailsMap = (HashMap<String, PCPBookingTransactionBean>)request.getSession().getAttribute("subjectDetailsMap");
		ModelAndView modelnView = new ModelAndView("pcp/bookingStatus");
		String onlineSeatBookingComplete = (String)request.getSession().getAttribute("onlineSeatBookingComplete");
		if("true".equals(onlineSeatBookingComplete)){
			return modelnView;
		}
		int noOfSubjects = 0;

		ArrayList<String> selectedCenters = pcpBooking.getSelectedCenters();

		HashMap<String, String> subjectCenterMap = new HashMap<>();
		for (String subjectCenter : selectedCenters) {
			String[] data = subjectCenter.split("\\|");
			String subject = data[0];
			String center = data[1];
			subjectCenterMap.put(subject, center);
		}
		request.getSession().setAttribute("subjectCenterMap", subjectCenterMap);

		try{

			ArrayList<String> subjects = (ArrayList<String>)request.getSession().getAttribute("subjects");


			String sapid = (String)request.getSession().getAttribute("userId_acads");

			String trackId = sapid + System.currentTimeMillis() ;
			request.getSession().setAttribute("trackId", trackId);

			String message = "PCP fees for "+sapid;
			StudentAcadsBean student = (StudentAcadsBean)request.getSession().getAttribute("student_acads");

			List<PCPBookingTransactionBean> bookingsList = new ArrayList<>();

			String examYear = pcpBooking.getYear();
			String examMonth = pcpBooking.getMonth();

			noOfSubjects = subjects.size();
			for (int i = 0; i < subjects.size(); i++) {
				String subject = subjects.get(i);
				PCPBookingTransactionBean bean = new PCPBookingTransactionBean();
				PCPBookingTransactionBean subjectDetailsBean = subjectDetailsMap.get(subject);
				
				bean.setSapid(sapid);
				bean.setSubject(subject);
				bean.setYear(pcpBooking.getYear());
				bean.setMonth(pcpBooking.getMonth());
				bean.setProgram(subjectDetailsBean.getProgram());
				bean.setSem(subjectDetailsBean.getSem());
				bean.setCenter(subjectCenterMap.get(subject));

				bean.setTrackId(trackId);
				bean.setAmount(pcpBooking.getAmount());

				bean.setTranStatus(ONLINE_PAYMENT_INITIATED);
				bean.setBooked("N");
				bean.setPaymentMode("Online");

				bookingsList.add(bean);
			}
			
			//Not needed to expire it from here, it will be expired by batch job
			//pcpBookingDAO.clearOldOnlineInitiationTransaction(sapid, null);

			pcpBookingDAO.upsertOnlineInitiationTransaction(sapid, bookingsList);

			int totalFees = 0;

			totalFees = Integer.parseInt(pcpBooking.getAmount());
			request.getSession().setAttribute("totalFees", totalFees + "");
			boolean isCertificate = (boolean)request.getSession().getAttribute("isCertificate");
			fillPaymentParametersInMap(model, student, totalFees, trackId, message,isCertificate);

			request.getSession().setAttribute("SECURE_SECRET", SECURE_SECRET);
			return new ModelAndView(new RedirectView("pay"), model);
			/*return proceedToPayOptions(model,trackId,ra);*/

		}catch(Exception e){
			  
			modelnView = new ModelAndView("pcp/confirmSubjects");
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in initiating Online transaction. Error: "+e.getMessage());
			modelnView.addObject("pcpBooking", pcpBooking);
			return modelnView;
		}

	}


	@RequestMapping(value = "/student/pcpFeesReponse", method = {RequestMethod.POST})
	public ModelAndView pcpFeesReponse(HttpServletRequest request, HttpServletResponse respnse, ModelMap model, Model m) throws Exception {
		/*if(!checkSession(request, respnse)){
			return new ModelAndView("studentPortalRediret");
		}*/

		saveAllTransactionDetails(request);

		String onlineSeatBookingComplete = (String)request.getSession().getAttribute("onlineSeatBookingComplete");
		if("true".equals(onlineSeatBookingComplete)){
			return new ModelAndView("pcp/bookingStatus");
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
			return selectPCPSubjectsForm(request, respnse, m);
		}else{
			return saveSuccessfulTransaction(request, respnse, model);
		}
	}


	public ModelAndView saveSuccessfulTransaction(HttpServletRequest request, HttpServletResponse respnse, ModelMap model) {
		/*if(!checkSession(request, respnse)){
			return new ModelAndView("studentPortalRediret");
		}*/

		ModelAndView modelnView = new ModelAndView("pcp/bookingStatus");

		String sapid = (String)request.getSession().getAttribute("userId_acads");
		String trackId = (String)request.getSession().getAttribute("trackId");
		StudentAcadsBean student = (StudentAcadsBean)request.getSession().getAttribute("student_acads");
		
		try {
			PCPBookingTransactionBean bean = new PCPBookingTransactionBean();
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
			bean.setTranStatus(ONLINE_PAYMENT_SUCCESSFUL);

			List<PCPBookingTransactionBean> pcpBookings = pcpBookingDAO.updateSeatsForOnlineTransaction(bean);

			request.getSession().setAttribute("pcpBookings", pcpBookings);
			request.getSession().setAttribute("onlineSeatBookingComplete", "true");

			request.setAttribute("success","true");
			request.setAttribute("successMessage",BOOKING_SUCCESS_MSG);

			MailSender mailSender = (MailSender)act.getBean("mailer");
			mailSender.sendBookingSummaryEmail(student, pcpBookingDAO);

		} catch (Exception e) {
			MailSender mailSender = (MailSender)act.getBean("mailer");
			request.getSession().setAttribute("onlineSeatBookingComplete", "false");
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "PCP Registration not completed. Error in recording your transaction details. Please contact Head Office to get it sorted out.");
			return new ModelAndView("pcp/selectPCPSubjectsForm");
		}
		request.getSession().setAttribute("onlineSeatBookingComplete", "true");
		return modelnView;
	}

	private void saveAllTransactionDetails(HttpServletRequest request) {

		try {
			String sapid = (String)request.getSession().getAttribute("userId_acads");
			String trackId = (String)request.getSession().getAttribute("trackId");
			PCPBookingTransactionBean bean = new PCPBookingTransactionBean();
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

			pcpBookingDAO.insertOnlineTransaction(bean);

		} catch (Exception e) {
			  
		}
	}


	@RequestMapping(value = "/student/pay", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView pay(HttpServletRequest request, HttpServletResponse respnse, ModelMap model) {
		/*if(!checkSession(request, respnse)){
			return new ModelAndView("studentPortalRediret");
		}*/
		return new ModelAndView("pay");
	}

	private void fillPaymentParametersInMap(ModelMap model,	StudentAcadsBean student, int totalFees, String trackId, String message,boolean isCertificate) {

		String address = student.getAddress();
		if(address == null || address.trim().length() == 0){
			address = "Not Available";
		}else if(address.length() > 200){
			address = address.substring(0, 200);
		}

		String city = student.getCity();
		if(city == null || city.trim().length() == 0){
			city = "Not Available";
		}

		String pin = student.getPin();
		if(pin == null || pin.trim().length() == 0){
			pin = "000000";
		}

		String mobile = student.getMobile();
		if(mobile == null || mobile.trim().length() == 0){
			mobile = "0000000000";
		}

		String emailId = student.getEmailId();
		if(emailId == null || emailId.trim().length() == 0){
			emailId = "notavailable@email.com";
		}


		model.addAttribute("channel", "10");
		model.addAttribute("account_id", ACCOUNT_ID);
		model.addAttribute("reference_no", trackId);
		model.addAttribute("amount", isCertificate ? generateAmountBasedOnCriteria(String.valueOf(totalFees),SERVICE_TAX_RULE):totalFees);
		model.addAttribute("mode", "LIVE");
		model.addAttribute("currency", "INR");
		model.addAttribute("currency_code", "INR");
		model.addAttribute("description", message);
		model.addAttribute("return_url", PCP_BOOKING_RETURN_URL);
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
		}catch(Exception e){
			  ;
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
	
	
	@RequestMapping(value = "/student/downloadEntryPass", method = { RequestMethod.POST})
	public ModelAndView downloadEntryPass(HttpServletRequest request, HttpServletResponse response, Model m) {
		
		if(!checkSession(request, response)){
			return new ModelAndView("studentPortalRediret");
		}
		
		
		ModelAndView modelnView = new ModelAndView("studentHome");
		String sapid = (String)request.getSession().getAttribute("userId_acads");
		String userDownloadingHallTicket = sapid; //Logged in user
		if(request.getParameter("userId") != null){
			//Coming from Admin page used for downloaing student hall ticket
			sapid = request.getParameter("userId");
			modelnView = new ModelAndView("downloadHallTicket");
		}


		try{
			ArrayList<String> subjects = new ArrayList<>();


			ArrayList<PCPBookingTransactionBean> subjectsBooked = pcpBookingDAO.getConfirmedBooking(sapid);

			if(subjectsBooked.size() == 0){
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No subjects booked for PCP. Entry Pass not available.");
				return modelnView;

			}

			StudentAcadsBean student = contentDAO.getSingleStudentsData(sapid);
			HashMap<String, PCPBookingTransactionBean> subjectBookingMap = new HashMap<>();

			for (int i = 0; i < subjectsBooked.size(); i++) {
				PCPBookingTransactionBean bean = subjectsBooked.get(i);
				subjectBookingMap.put(bean.getSubject(), bean);
			}

			for (int i = 0; i < subjectsBooked.size(); i++) {
				subjects.add(subjectsBooked.get(i).getSubject());
			}

			PCPPDFCreator pcpPDFHelper = new PCPPDFCreator();
			String fileName = pcpPDFHelper.createPCPEntryPass(subjectsBooked, getProgramMap(), student, 
					PCPTICKET_PATH, subjectBookingMap, STUDENT_PHOTOS_PATH);

			//dao.saveHallTicketDownloaded(userDownloadingHallTicket, subjectsBooked);
			File fileToDownload = new File(fileName);
			InputStream inputStream = new FileInputStream(fileToDownload);
			response.setContentType("application/pdf");
			response.setHeader("Content-Disposition", "attachment; filename="+fileName); 
			IOUtils.copy(inputStream, response.getOutputStream());
			response.flushBuffer();

		}catch(Exception e){
			  
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in generating PCP Entry Pass.");
			
			return selectPCPSubjectsForm(request, response, m);
		}

		return modelnView;
	}
	
	//Commented By Riya as logic is shifted in PCPAdminController
	
	/*@RequestMapping(value = "/admin/pcpRegistrationReportForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String pcpRegistrationReportForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {

		PCPBookingTransactionBean bean = new PCPBookingTransactionBean();
		m.addAttribute("bean",bean);
		m.addAttribute("yearList", ACAD_YEAR_LIST);

		return "pcp/pcpBookingReport";
	}
	
	@RequestMapping(value = "/admin/pcpRegistrationReport", method = RequestMethod.POST)
	public ModelAndView pcpRegistrationReport(HttpServletRequest request, HttpServletResponse response, @ModelAttribute PCPBookingTransactionBean bean){
		ModelAndView modelnView = new ModelAndView("pcp/pcpBookingReport");
		request.getSession().setAttribute("bean", bean);
		try{
			ArrayList<PCPBookingTransactionBean> pcpBookingList = pcpBookingDAO.getConfirmedBookingForGivenYearMonth(bean, getAuthorizedCodes(request));
			request.getSession().setAttribute("pcpBookingList",pcpBookingList);

			if(pcpBookingList != null && pcpBookingList.size() > 0){
				modelnView.addObject("rowCount",pcpBookingList.size());
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
		modelnView.addObject("bean", bean);
		modelnView.addObject("yearList", ACAD_YEAR_LIST);

		return modelnView;
	}
	
	@RequestMapping(value = "/admin/downloadPCPBookingReport", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadPCPBookingReport(HttpServletRequest request, HttpServletResponse response) {

		if(!checkSession(request, response)){
			return new ModelAndView("studentPortalRediret");
			//redirectToPortalApp(response);
		
		}
		List<PCPBookingTransactionBean> pcpBookingList = (ArrayList<PCPBookingTransactionBean>)request.getSession().getAttribute("pcpBookingList");
		UserAuthorizationBean userAuthorization = (UserAuthorizationBean)request.getSession().getAttribute("userAuthorization");
       	String roles="";
		if(userAuthorization != null){
       	roles = (userAuthorization.getRoles() != null && !"".equals(userAuthorization.getRoles())) ? userAuthorization.getRoles() : roles;
       	}
		request.setAttribute("roles", roles);
		return new ModelAndView("pcpBookingReportExcelView","pcpBookingList",pcpBookingList);
	}
	*/
	
	@RequestMapping(value = "/student/downloadPCPRegistrationReceipt", method = {RequestMethod.GET, RequestMethod.POST})
	public String downloadPCPRegistrationReceipt(HttpServletRequest request, HttpServletResponse response) {

		try{

			String userId = (String)request.getSession().getAttribute("userId_acads");
			
			if(userId == null){
				//Link clicked from Support portal by Agent
				userId = request.getParameter("userId");
			}
			
			StudentAcadsBean student = contentDAO.getSingleStudentsData(userId);
			
			List<PCPBookingTransactionBean> pcpBookings = pcpBookingDAO.getConfirmedBookingForReceipts(userId);
			if(pcpBookings == null || pcpBookings.isEmpty()){
				setError(request, "No PCP/VC Bookings found current Academic Cycle");
				
				String redirectUrl = SERVER_PATH + "studentportal/home";
		        return "redirect:" + redirectUrl;
				
				
		        
			}
			PCPPDFCreator pdfCreator = new PCPPDFCreator();
			String fileName = pdfCreator.createReceiptAndReturnFileName(pcpBookings, MARKSHEETS_PATH, student);
			//String fileName = (String)request.getSession().getAttribute("fileName");

			String filePathToBeServed = fileName;
			File fileToDownload = new File(filePathToBeServed);
			InputStream inputStream = new FileInputStream(fileToDownload);
			response.setContentType("application/pdf");
			response.setHeader("Content-Disposition", "attachment; filename="+fileName); 
			IOUtils.copy(inputStream, response.getOutputStream());
			response.flushBuffer();

		}catch(Exception e){
			  
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Unable to download file.");
		}
		return null;

	}

}

