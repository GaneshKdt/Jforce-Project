package com.nmims.controllers;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import com.nmims.beans.ProgramExamBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.TransactionBean;
import com.nmims.beans.UserAuthorizationExamBean;
import com.nmims.daos.CareerServicesDAO;
import com.nmims.daos.ExamBookingDAO;


public class BaseController {


	@Value("${SECURE_SECRET}")
	private String SECURE_SECRET; 
	
	@Value("${ACCOUNT_ID}")
	private String ACCOUNT_ID;
	
	@Value("${V3URL}")
	private String V3URL;

	@Value("${SERVER_PATH}")
	private String SERVER_PATH;
	
	@Value("${SERVER_HOST}")
	private String host;
	
	@Value("${SERVER_PORT}")
	private int[] port;
	
	private String path;
/*	@Value("${WALLET_API}")
	private String WALLET_API;*/
	
	@Autowired
	ApplicationContext act;

	@Autowired
	CareerServicesDAO csDAO;
	
	private String errorInPort = null;
	
	HashMap<String, ProgramExamBean> programProgramStructureProgramDetailsMap = null;
	
	/**
	 * Refresh Cache function to refresh cache
	 * @param 
	 * none
	 * @return 
	 * none
	 * */
	public String RefreshCache() {
		programProgramStructureProgramDetailsMap = null;
		getProgramDetails();
		
		return null;
	}
	
	public boolean checkLead(HttpServletRequest request, HttpServletResponse response){
		String isLead = (String)request.getSession().getAttribute("isLoginAsLead");
		if(isLead.equals("true")){
			return true;
		}else{
			return false;
		}
	}
	
	public HashMap<String, ProgramExamBean> getProgramDetails(){
		if(this.programProgramStructureProgramDetailsMap == null || this.programProgramStructureProgramDetailsMap.size() == 0){
			ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
			this.programProgramStructureProgramDetailsMap = eDao.getProgramDetailsMap();
		}
		return programProgramStructureProgramDetailsMap;
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
	
	public boolean checkSession(HttpServletRequest request, HttpServletResponse respnse){
		String userId = (String)request.getSession().getAttribute("userId");
		if(userId != null){
			return true;
		}else{
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Session Expired! Please login again.");
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
	private String generateCommaSeparatedList(String sapIdList) {
		String commaSeparatedList = sapIdList.replaceAll("(\\r|\\n|\\r\\n)+", ",");
		if(commaSeparatedList.endsWith(",")){
			commaSeparatedList = commaSeparatedList.substring(0,  commaSeparatedList.length()-1);
		}
		return commaSeparatedList;
	}

	public void setSuccess(HttpServletRequest request, String successMessage){
		request.setAttribute("success","true");
		request.setAttribute("successMessage",successMessage);
	}

	public void setError(HttpServletRequest request, String errorMessage){
		request.setAttribute("error", "true");
		request.setAttribute("errorMessage", errorMessage);
	}

	public String getAuthorizedCodes(HttpServletRequest request) {
		UserAuthorizationExamBean userAuthorization = (UserAuthorizationExamBean)request.getSession().getAttribute("userAuthorization");
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
	
	public ModelAndView proceedToPayOptions(ModelMap model,String requestId,RedirectAttributes ra){
		//Redirects to a midway page which posts the request id to walletPayRequest method//
		ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
		
		TransactionBean transactionBean = new TransactionBean();
		populateTransactionBean(transactionBean,requestId,model);
		eDao.insertApiRequest(transactionBean);
		ra.addAttribute("requestId", requestId);
		return new ModelAndView(new RedirectView("/studentportal/payOptions"));
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
	
	 
	public void redirectToPortalApp(HttpServletResponse httpServletResponse) {
		
		try {
			httpServletResponse.sendRedirect(SERVER_PATH+"studentportal/");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			
		}
	
	}
	
	// Created to redirect from exam to  student home page without loosing sessions 
	public void redirectToStudentHome(HttpServletResponse httpServletResponse) {
			
			try {
				httpServletResponse.sendRedirect(SERVER_PATH+"studentportal/home");
			} catch (IOException e) {
				
			}
		
		}
	
	/*
	 * Refresh Cache from all server
	 * */
	@SuppressWarnings("deprecation")
	public String TryRefreshCacheToAllServer(int portKey,String path) throws IOException {
		if(portKey == 0) {
			this.errorInPort = null;
		}
		if(path == "ExamBooking") {
			this.path = "/exam/admin/refreshExamBookingCache";
		}else if(path == "All") {
			this.path = "/exam/admin/cacheRefreshToOnlyServer";
		}
		int i = portKey;
		try {
			while(i < this.port.length)
			{
				URL obj = new URL("http://" + this.host + ":" + this.port[i] + this.path);
				HttpURLConnection  con = (HttpURLConnection ) obj.openConnection();
				con.setRequestMethod("GET");
				con.setRequestProperty("User-Agent", "Mozilla/5.0");
				int responseCode = con.getResponseCode();
				if (responseCode == HttpURLConnection.HTTP_OK) { // success
					// print result
				} else {
					if(errorInPort == null) {
						errorInPort = "" + port[i] + "";
					}else {
						errorInPort = errorInPort + " , " +  port[i];
					}
				}
				i++;
			}
			return errorInPort;
		}
		catch (Exception e) {
			if(i < this.port.length) {
				if(errorInPort == null) {
					errorInPort = "" + port[i] + "";
				}else {
					errorInPort = errorInPort + " , " +  port[i];
				}
				i++;
				TryRefreshCacheToAllServer(i,path);
			}
			return errorInPort;
		}
	}

	
	public void resetStudentInSession(HttpServletRequest request, HttpServletResponse response) {

		String userId = (String)request.getSession().getAttribute("userId");

		ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
		StudentExamBean student = eDao.getSingleStudentsData(userId);
		String programForHeader =  (String) request.getSession().getAttribute("programForHeaderExam");
		student.setProgramForHeader(programForHeader);
		request.getSession().setAttribute("studentExam", student);
		performCSStudentChecks(request, userId, student);
	}
	
	public void performCSStudentChecks(HttpServletRequest request, String userId, StudentExamBean student) {
		csDAO.performCSStudentChecks(request, student);
	}
	
}
