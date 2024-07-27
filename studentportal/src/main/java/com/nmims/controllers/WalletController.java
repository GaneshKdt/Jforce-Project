/*package com.nmims.controllers;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import com.google.gson.Gson;
import com.nmims.beans.AdhocPaymentBean;
import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.beans.ServiceRequest;
import com.nmims.beans.ServiceRequestDocumentBean;
import com.nmims.beans.StudentBean;
import com.nmims.beans.TransactionBean;
import com.nmims.beans.WalletBean;
import com.nmims.daos.PortalDao;
import com.nmims.daos.ServiceRequestDao;
import com.nmims.daos.WalletDAO;
import com.nmims.helpers.ExcelHelper;

@Controller
public class WalletController extends BaseController{
	
	@Value("${SECURE_SECRET}")
	private String SECURE_SECRET; // secret key;
	@Value("${ACCOUNT_ID}")
	private String ACCOUNT_ID;
	@Value("${V3URL}")
	private String V3URL;
	@Value("${WALLET_RETURN_URL}")
	private String WALLET_RETURN_URL;
	
	@Value("${SR_RETURN_URL}")
	private String SR_RETURN_URL;
	
	@Value("${SERVICE_REQUEST_FILES_PATH}")
	private String SERVICE_REQUEST_FILES_PATH;
	
	private ArrayList<String> requiredFieldNamesForWalletAPI = new ArrayList<String>(
			Arrays.asList("account_id","secure_hash","reference_no","amount","description","return_url","email",
							"studentNumber","channel","mode","currency","currency_code","name","address",
							"city","country","postal_code","phone","algo"));
	
	@Autowired
	ApplicationContext act;
	
	@Autowired
	WalletDAO walletDao;
	
	
	
	
	//View for transactions made and option to add money in wallet//
	@RequestMapping(value="/myWalletForm",method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView myWalletForm(HttpServletRequest request,HttpServletResponse response){
		ModelAndView modelnView = new ModelAndView("/wallet/wallet");;
		
		StudentBean student = (StudentBean)request.getSession().getAttribute("student_studentportal");
		WalletBean wallet = walletDao.getWalletRecord(student.getSapid());
		//First Check if wallet is 
		if(wallet == null){
			wallet = walletDao.insertWalletRecordAndReturnRecord(student.getSapid());
		}
			
			ArrayList<WalletBean> getTransactionsRelatedToWallet = (ArrayList<WalletBean>)walletDao.getTransactionsUnderWalletRecord(wallet.getId());
			
			modelnView.addObject("wallet",wallet);
			if(getTransactionsRelatedToWallet!=null && getTransactionsRelatedToWallet.size()>0){
				modelnView.addObject("walletTransactionList", getTransactionsRelatedToWallet);
				modelnView.addObject("rowCount", getTransactionsRelatedToWallet.size());
			}else{
				modelnView.addObject("rowCount",0);
			}
			return modelnView;
		
	}
	
	
	
	@RequestMapping(value="/saveMoneyToWallet",method={RequestMethod.POST})
	public ModelAndView saveMoneyToWallet(@ModelAttribute WalletBean walletRecord,HttpServletRequest request,HttpServletResponse response,ModelMap modelMap){
		System.out.println("saveMoneyToWallet called");
		StudentBean student = (StudentBean)request.getSession().getAttribute("student_studentportal");
		WalletBean wallet = (WalletBean)request.getSession().getAttribute("walletRecord");
		
		walletRecord.setId(wallet.getId());
		populateWalletObject(walletRecord,request,student);
		
		fillPaymentParametersInMapWithWalletParameters(modelMap,student,walletRecord,WALLET_RETURN_URL,"NONE");
		
		return new ModelAndView(new RedirectView("pay"),modelMap);
	}
	
	//Populate wallet object for initial money load//
	private void populateWalletObject(WalletBean wallet,
			HttpServletRequest request,StudentBean student) {
		String trackId = System.currentTimeMillis()+String.valueOf((int)(Math.random() * 99999 + 1));
		wallet.setTrackId(trackId);
		wallet.setTranStatus(WalletBean.ONLINE_PAYMENT_INITIATED);
		wallet.setCreatedBy(student.getSapid());
		wallet.setLastModifiedBy(student.getSapid());
		wallet.setTransactionType(WalletBean.CREDIT);
		request.getSession().setAttribute("trackId", trackId);
		request.getSession().setAttribute("amount", wallet.getAmount());
	}
	
	@RequestMapping(value="/payResponseForWallet",method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView payResponseForWallet(HttpServletRequest request,HttpServletResponse response,ModelMap model){
		if (!checkSession(request, response)) {
			return new ModelAndView("login");
		}
		String trackId = (String) request.getSession().getAttribute("trackId");
		String amount = (String) request.getSession().getAttribute("amount");
		WalletBean walletRecord = (WalletBean)request.getSession().getAttribute("walletRecord");
		
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
		System.out.println("Error Message = " + errorMessage);
		if(errorMessage!=null){
			return sendBackToWalletForm(request,response,errorMessage,walletRecord);
		}else{
			return saveDetailsInWalletTransactionTable(request,response,model);
		}
	}
	
	public ModelAndView sendBackToWalletForm(HttpServletRequest request,HttpServletResponse response,String errorMessage,WalletBean walletRecord){
		setError(request,errorMessage);
		ModelAndView modelAndView = new ModelAndView("wallet/wallet");
		modelAndView.addObject("walletRecord", walletRecord);
		return modelAndView;
		
	}
	
	@RequestMapping(value="walletTransactionReportForm",method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView  walletTransactionReportForm(HttpServletRequest request, HttpServletResponse response){
		ModelAndView modelNView = new ModelAndView("/wallet/walletTransactionReport");
		modelNView.addObject("wallet",new WalletBean());
		return modelNView;
	}
	
	@RequestMapping(value="/walletTransactionReport",method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView walletTransactionReport(@ModelAttribute WalletBean wallet,HttpServletRequest request, HttpServletResponse response){
		ModelAndView modelNView = new ModelAndView("/wallet/walletTransactionReport");
		WalletDAO wDao = (WalletDAO)act.getBean("walletDao");
		ArrayList<WalletBean> listOFWalletTransactions = wDao.getAllTransactionsBetweenDates(wallet);
		request.getSession().setAttribute("listOFWalletTransactions", listOFWalletTransactions);
		modelNView.addObject("listOFWalletTransactions", listOFWalletTransactions) ;
		modelNView.addObject("rowCount", listOFWalletTransactions.size());
		return modelNView;
	}
	
	@RequestMapping(value="/downloadWalletTransactionReport",method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView downloadWalletTransactionReport(@ModelAttribute WalletBean wallet,HttpServletRequest request, HttpServletResponse response){
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		ArrayList<WalletBean> listOFWalletTransactions = (ArrayList<WalletBean>)request.getSession().getAttribute("listOFWalletTransactions");
		
		
		String userId = (String)request.getSession().getAttribute("userId");
		ExcelHelper excelHelper = new ExcelHelper();
		try{
			if(listOFWalletTransactions!=null && listOFWalletTransactions.size()>0){
				excelHelper.buildWalletTransactionExcelReport(listOFWalletTransactions, response, pDao);
				setSuccess(request, "Sucessfully generated Report");
				return sendToPageBasedOnRole(request,response,userId);
			}else{
				setError(request, "No Wallet Transaction Records");
				return sendToPageBasedOnRole(request,response,userId);
			}
			
			
		}catch(Exception e){
			
			setError(request, "ERROR");
			return sendToPageBasedOnRole(request,response,userId);
		}
	}
	

	private ModelAndView saveDetailsInWalletTransactionTable(
			HttpServletRequest request, HttpServletResponse response,
			ModelMap model) {
		ModelAndView modelAndView = new ModelAndView("/wallet/walletSummary");
		
		String trackId = (String)request.getSession().getAttribute("trackId");
		String userId = (String)request.getSession().getAttribute("userId");
		WalletBean bean = (WalletBean)request.getSession().getAttribute("walletRecord");
		
		try{
			
			bean.setTrackId(trackId);
			bean.setResponseMessage(request.getParameter("ResponseMessage"));
			bean.setTransactionID(request.getParameter("TransactionID"));
			bean.setRequestID(request.getParameter("RequestID"));
			bean.setMerchantRefNo(request.getParameter("MerchantRefNo"));
			bean.setSecureHash(request.getParameter("SecureHash"));
			bean.setRespAmount(request.getParameter("Amount"));
			bean.setAmount(request.getParameter("Amount"));
			bean.setRespTranDateTime(request.getParameter("DateCreated"));
			bean.setTranDateTime(request.getParameter("DateCreated"));
			bean.setResponseCode(request.getParameter("ResponseCode"));
			bean.setRespPaymentMethod(request.getParameter("PaymentMethod"));
			bean.setIsFlagged(request.getParameter("IsFlagged"));
			bean.setPaymentID(request.getParameter("PaymentID"));
			bean.setError(request.getParameter("Error"));
			bean.setDescription(request.getParameter("Description"));
			bean.setWalletId(bean.getId()+"");
			bean.setUserId(userId);
			
			updateWalletBalance(bean,WalletBean.CREDIT);
			walletDao.updateWMoneyLoadAndInsertWTransactionAndUpdateWBalance(bean, true);
			
			modelAndView.addObject("walletBean", bean);
			request.setAttribute("Success", "true");
			request.setAttribute("successMessage", "Money added to wallet successfully");
			return modelAndView;
		}catch(Exception e){
			//Redirect to the wallet home since the transaction has failed.
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error occured");
			ModelAndView modelnView = new ModelAndView("/wallet/wallet");
			modelnView.addObject("wallet",bean);
			return modelnView;
		}
		
		
	}

	private ModelAndView showErrorOnWalletForm(HttpServletRequest request,
			HttpServletResponse response, String errorMessage) {
		request.setAttribute("error", "true");
		request.setAttribute("errorMessage", errorMessage);
		
		return myWalletForm(request,response);
	}

}
*/