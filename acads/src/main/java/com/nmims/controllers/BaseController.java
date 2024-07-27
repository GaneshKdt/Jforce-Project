package com.nmims.controllers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import com.nmims.beans.ConsumerProgramStructureAcads;
import com.nmims.beans.CourseraMappingBean;
import com.nmims.beans.StudentAcadsBean;
import com.nmims.beans.TransactionBean;
import com.nmims.beans.UserAuthorizationBean;
import com.nmims.daos.CareerServicesDAO;
import com.nmims.daos.ContentDAO;
import com.nmims.daos.CourseraDao;
import com.nmims.daos.PCPBookingDAO;
import com.nmims.daos.TimeTableDAO;

public class BaseController {
	
	@Autowired
	ApplicationContext act;
	
	@Value("${SERVER_PATH}")
	private String SERVER_PATH;
	
	private ArrayList<ConsumerProgramStructureAcads> consumerTypeList;
	
	private Map<String,String> consumerTypeIdNameMap;
	
	private Map<String,String> programStructureIdNameMap;

	private Map<String,String> programIdNameMap;
	
	private ArrayList<ConsumerProgramStructureAcads> subjectCodeList;

	private Map<String, String> subjectCodeMap = null;
	
	private ArrayList<String> subjectCodesList = null;
	
	@Autowired
	CareerServicesDAO csDAO;
	
	@Autowired
	CourseraDao cDAO;
	
	private Map<String,String> subjectcodeIdMap;
	
	public void resetStudentInSession(HttpServletRequest request, HttpServletResponse response) {

		String userId = (String)request.getSession().getAttribute("userId_acads");

		ContentDAO cDao = (ContentDAO)act.getBean("contentDAO");
		StudentAcadsBean student = cDao.getSingleStudentsData(userId);
		
		request.getSession().setAttribute("student_acads", student);
		performCSStudentChecks(request, userId, student);
	}

	public void performCSStudentChecks(HttpServletRequest request, String userId, StudentAcadsBean student) {
		csDAO.performCSStudentChecks(request, student);
	}
	public boolean checkSession(HttpServletRequest request, HttpServletResponse respnse){
		String userId = (String)request.getSession().getAttribute("userId_acads");
		if(userId != null){
			return true;
		}else{
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Session Expired! Please login again.");
			return false;
		}
	}
	
	public boolean checkLead(HttpServletRequest request, HttpServletResponse respnse){
		String lead = (String)request.getSession().getAttribute("isLoginAsLead");
		if(lead.equals("true")){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean isStudentOfCertificate(String program){
		if(program.startsWith("C")){
			return true;
		}else{
			return false;
		}
	}
	
	public String generateAmountBasedOnCriteria(String amount,String criteria){
		double calculatedAmount = 0.0;
		switch(criteria){
		case "GST":
			calculatedAmount = Double.parseDouble(amount) + (0.18 * Double.parseDouble(amount));
			break;
		}
		return String.valueOf(calculatedAmount);
		
	}
	
	public void setSuccess(HttpServletRequest request, String successMessage){
		request.setAttribute("success","true");
		request.setAttribute("successMessage",successMessage);
	}
	
	public void setError(HttpServletRequest request, String errorMessage){
		request.setAttribute("error", "true");
		request.setAttribute("errorMessage", errorMessage);
	}
	
	public ModelAndView proceedToPayOptions(ModelMap model,String requestId,RedirectAttributes ra){
		//Redirects to a midway page which posts the request id to walletPayRequest method//
		PCPBookingDAO pcpDao = (PCPBookingDAO)act.getBean("pcpBookingDAO");
		
		TransactionBean transactionBean = new TransactionBean();
		populateTransactionBean(transactionBean,requestId,model);
		pcpDao.insertApiRequest(transactionBean);
		ra.addAttribute("requestId", requestId);
		return new ModelAndView(new RedirectView("/studentportal/payOptions"));
	}
	
	// Created to redirect from acads to  student home page without loosing sessions 
	public void redirectToStudentHome(HttpServletResponse httpServletResponse) {
				
		try{
				httpServletResponse.sendRedirect(SERVER_PATH+"studentportal/home");
			}catch (IOException e) {
				// TODO Auto-generated catch block
				  
			}
	}
	
	
	public String getAuthorizedCodes(HttpServletRequest request) {
		UserAuthorizationBean userAuthorization = (UserAuthorizationBean)request.getSession().getAttribute("userAuthorization");
		String commaSeparatedCenterCode = "";
		if(userAuthorization != null){
			ArrayList<String> authorizedCenterCodesList = userAuthorization.getAuthorizedCenterCodes();

			for (String centerCode : authorizedCenterCodesList) {
				commaSeparatedCenterCode = commaSeparatedCenterCode + "'" + centerCode + "',";
			}
			
			if(commaSeparatedCenterCode.endsWith(",")){
				return commaSeparatedCenterCode.substring(0, commaSeparatedCenterCode.length()-1);
			}
		}
		return commaSeparatedCenterCode;
	}
	
	public void populateTransactionBean(TransactionBean transactionBean,String requestId,ModelMap model){
			
			transactionBean.setRequestId(requestId);
			transactionBean.setChannel(model.get("channel").toString());
			transactionBean.setAccountId(model.get("account_id").toString());
			transactionBean.setReferenceNo(model.get("reference_no").toString());
			transactionBean.setAmount(model.get("amount").toString());
			transactionBean.setMode(model.get("mode").toString());
			transactionBean.setCurrency(model.get("currency").toString());
			transactionBean.setCurrencyCode(model.get("currency_code").toString());
			transactionBean.setDescription(model.get("description").toString());
			transactionBean.setReturnUrl(model.get("return_url").toString());
			transactionBean.setName(model.get("name").toString());
			transactionBean.setAddress(model.get("address").toString());
			transactionBean.setCity(model.get("city").toString());
			transactionBean.setCountry(model.get("country").toString());
			transactionBean.setPostalCode(model.get("postal_code").toString());
			transactionBean.setPhone(model.get("phone").toString());
			transactionBean.setEmail(model.get("email").toString());
			transactionBean.setSapid(model.get("studentNumber").toString());
			transactionBean.setPayApi(model.get("V3URL").toString());
			
		}
	
	public Map<String,String>  getProgramStructureIdNameMap(){
		if(this.programStructureIdNameMap == null){
			ContentDAO contentDao = (ContentDAO)act.getBean("contentDAO");
			this.programStructureIdNameMap = contentDao.getProgramStructureIdNameMap();
		}
		return programStructureIdNameMap;
	}
	public Map<String,String>  getProgramIdNameMap(){
		if(this.programIdNameMap == null){
			ContentDAO contentDao = (ContentDAO)act.getBean("contentDAO");
			this.programIdNameMap = contentDao.getProgramIdNameMap();
		}
		return programIdNameMap;
	}
	
	public ArrayList<ConsumerProgramStructureAcads> getConsumerTypeList(){
		if(this.consumerTypeList == null){
			ContentDAO contentDao = (ContentDAO)act.getBean("contentDAO");
			this.consumerTypeList = contentDao.getConsumerTypeList();
		}
		return consumerTypeList;
	}
	
	public ArrayList<ConsumerProgramStructureAcads> getSubjectCodeLists(){
		if(this.subjectCodeList == null){
			ContentDAO contentDao = (ContentDAO)act.getBean("contentDAO");
			this.subjectCodeList = contentDao.getSubjectCodeLists();
		}
	return subjectCodeList;
	
	}
	
	public void courseraCheck(HttpServletRequest request, String userId, StudentAcadsBean student) {

		// check only to show coursera option to the applicable masterkeys
		int countForMasterKeyCourseraMapping = cDAO.checkCourseraProgramApplicableForMasterKey(student.getConsumerProgramStructureId());
		if (countForMasterKeyCourseraMapping > 0) {
			request.getSession().setAttribute("isCourseraApplicable", true);
		} else {
			request.getSession().setAttribute("isCourseraApplicable", false);
		}
	}
	
	public Map<String, String> getsubjectCodeMap() {
		if (this.subjectCodeMap == null) {
			TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
			this.subjectCodeMap = dao.getsubjectCodeMap();
		}
		return subjectCodeMap;
	}
	
	public ArrayList<String> getSubjectCodesList() {
		if (this.subjectCodesList == null) {
			TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
			this.subjectCodesList = dao.getAllSubjectCodes();
		}
		return subjectCodesList;
	}
	
	public Map<String, String> getSubjectCodeIdsMap() {
		if(this.subjectCodeList == null){
			ContentDAO contentDao = (ContentDAO)act.getBean("contentDAO");
			this.subjectCodeList = contentDao.getAllSubjectCodeLists();
		}
		this.subjectcodeIdMap =  subjectCodeList.stream().collect(
                Collectors.toMap(ConsumerProgramStructureAcads::getSubjectCodeId, ConsumerProgramStructureAcads::getSubjectcode));
		
		return subjectcodeIdMap;
	}
	
	public ArrayList<ConsumerProgramStructureAcads> getAllSubjectCodeLists(){
		if(this.subjectCodeList == null){
			ContentDAO contentDao = (ContentDAO)act.getBean("contentDAO");
			this.subjectCodeList = contentDao.getAllSubjectCodeLists();
		}
	return subjectCodeList;
	
	}
}
