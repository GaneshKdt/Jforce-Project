package com.nmims.paymentgateways.controller;


import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.paymentgateways.bean.TransactionStatusBean;
import com.nmims.paymentgateways.bean.TransactionsBean;
import com.nmims.paymentgateways.service.TransactionService;

@Controller
public class TransactionController {

	@Autowired
	private TransactionService transactionService;
	
	
	
	private static final Logger paymentSchedulerLogger = LoggerFactory.getLogger("payment_scheduler");
	
	@RequestMapping(value="/student/selectGatewayStageTransaction",method= {RequestMethod.POST})
	public ModelAndView selectGatewayStageTransaction(@ModelAttribute TransactionsBean transactionsBean,HttpServletRequest request) {
		return transactionService.selectGatewayStageTransaction(transactionsBean, request);
	}

	@RequestMapping(value = "/student/processTransaction", method = { RequestMethod.POST })
	public ModelAndView processTransaction(@ModelAttribute TransactionsBean transactionsBean,
			HttpServletRequest request, HttpServletResponse response) {
		response.setHeader("Set-Cookie", "JSESSIONID=" + request.getSession().getId()
				+ "; Path=/paymentgateways; HttpOnly; SameSite=none; Secure");
		return transactionService.processTransaction(transactionsBean, request);
	}

	@RequestMapping(value = "/student/responseTransaction", method = { RequestMethod.POST })
	public ModelAndView responseTransaction(HttpServletRequest request) {
		return transactionService.responseTransaction(request);
	}

	// cancel url for payment APIs, only required by razorpay as of now
	@RequestMapping(value = "/student/cancelPayment", method = { RequestMethod.GET })
	public ModelAndView cancelPayment(HttpServletRequest request) {
		return transactionService.cancelPayment(request);
	}
	
	@RequestMapping(value = "/student/getTransactionStatus",method = { RequestMethod.POST}, produces = "application/json")
	@ResponseBody public TransactionStatusBean getTransactionStatus(@RequestBody TransactionStatusBean bean) {
		return transactionService.getTransactionStatus(bean.getTrackId());
	}
	
	@RequestMapping(value = "/student/initiateRefund", method = {RequestMethod.POST},produces = "application/json")
	@ResponseBody public TransactionStatusBean initiateRefund(@RequestBody TransactionStatusBean bean) {
		return transactionService.initiateRefund(bean.getTrackId(), bean.getPaymentId(), bean.getAmount());
	}
	
	@RequestMapping(value = "/student/getRefundStatus",produces = "application/json",method = {RequestMethod.POST})
	@ResponseBody public TransactionStatusBean getRefundStatus (@RequestBody TransactionStatusBean bean) {
		return transactionService.getRefundStatus(bean.getMerchantRefNo(), bean.getRefundId());
	}
	@RequestMapping(value = "/m/getTransactionStatus",method= {RequestMethod.POST})
	public @ResponseBody TransactionsBean getTransactionStatus(@RequestBody TransactionsBean transactionsBean) {
		paymentSchedulerLogger.info("TransactionController.getTransactionStatus() - START");
		if(transactionsBean.getTrack_id() == null) {
			paymentSchedulerLogger.info("Invalid request found, null track_id received.");
			transactionsBean.setError("Invalid request found");
			transactionsBean.setTransaction_status("Invalid Request");
			return transactionsBean;
		}
		return transactionService.getTransactionStatus(transactionsBean);
	}

	@RequestMapping(value = "/m/getTransactionStatusBytrackId",method= {RequestMethod.POST})
	@ResponseBody public  Map<String,String> getTransactionStatus(@RequestParam String trackId) {
		return transactionService.getTransactionStatusByTrackID(trackId);
	}
	

	@RequestMapping(value = "/m/getRazorpayTransactionStatus/{track_id}",method = { RequestMethod.GET}, produces = "application/json")
	@ResponseBody String getRazorpayTransactionStatus(@PathVariable("track_id") String track_id) {
		return transactionService.getRazorpayTransactionStatus(track_id);
	}

}
