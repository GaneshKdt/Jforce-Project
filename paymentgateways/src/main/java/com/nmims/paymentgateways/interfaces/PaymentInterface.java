package com.nmims.paymentgateways.interfaces;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.google.gson.JsonObject;
import com.nmims.paymentgateways.bean.TransactionsBean;


public interface PaymentInterface {
	
	
	/**
	 * @param: TransactionsBean,String
	 * @return: String  
	 * 
	 * Method used to generate checksum for payment.
	 * */
	public String generateCheckSum(TransactionsBean transactionsBean,String returnUrl);
	
	/**
	 * @param: TransactionsBean,String
	 * @return: ModelAndView  
	 * 
	 * Method used to prepare model and view data for payment.
	 * */
	public ModelAndView createModelAndViewData(TransactionsBean transactionsBean,String returnUrl);
	
	/**
	 * @param: HttpServletRequest
	 * @return: boolean  
	 * 
	 * Method used to verify response checksum with self generated checksum.
	 * */
	public boolean verifyCheckSum(HttpServletRequest request);
	
	/**
	 * @param: HttpServletRequest
	 * @return: TransactionsBean  
	 * 
	 * Method used to create transaction bean from httpServletRequest request.
	 * */
	public TransactionsBean createResponseBean(HttpServletRequest request);
	
	/**
	 * @param: TransactionsBean,List<String>
	 * @return: TransactionsBean  
	 * 
	 * Method used to get transaction status from trackId.
	 * */
	public TransactionsBean getTransactionStatus(TransactionsBean transactionsBean,List<String> paymentStatus); 
	
	/**
	 * @param: HttpServletRequest
	 * @return: String  
	 * 
	 * Method used to check any transaction error is there.
	 * */
	public String checkErrorInTransaction(HttpServletRequest request);
	
	
	/**
	 * @param: String,String,String
	 * @return JsonObject
	 * 
	 * Method used to initiate payment refund request to paymentgateway
	 * */
	public JsonObject refundInitiate(String tracking_id,String transaction_id,String refund_amount);
	
	
	/**
	 * @param: String,String
	 * @return JsonObject
	 * 
	 * Method used to check initiated refund status whether success or failed.
	 * */
	public JsonObject refundStatus(String tracking_id,String refId);
	
}
