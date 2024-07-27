package com.nmims.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.nmims.beans.AdhocPaymentStudentPortalBean;
import com.nmims.beans.ServiceRequestStudentPortal;

public class XMLParser {

	public static void main(String[] args) throws Exception {
		String studentNumber ="77";
		String soqlQuery = "SELECT Id, nm_StudentNo__c,nm_FathersName__c,nm_MothersName__c,PersonMobilePhone,nm_SpouseName__c,PersonEmail,Shipping_Street__c,City_Shipping_Account__c,State_Province_Shipping__c,"
				+"Zip_Postal_Code_Shipping__c,Country_Shipping__c,Locality_Name_Shipping__c,"
				+"Nearest_LandMark_Shipping__c,House_No_Name_Shipping_Account__c"
				+" FROM Account"
				+ " where nm_StudentNo__c Like \'%" + studentNumber + "\'";
		System.out.println("SOQL QUERY-->"+soqlQuery);
		
		
	}
	
	
	public String queryTransactionStatus(String trackId, String accountId, String secretKey) throws Exception{
		String xmlResponse = null;
		try
		{

			//URL url = new URL( "https://secure.ebs.in/api/1_0" );
			URL url = new URL( "https://api.secure.ebs.in/api/1_0" );

			HttpURLConnection hConnection = (HttpURLConnection)
					url.openConnection();
			HttpURLConnection.setFollowRedirects( true );

			hConnection.setDoOutput( true );
			hConnection.setRequestMethod("POST");	

			StringBuilder parameters = new StringBuilder();
			parameters.append("AccountID=" + URLEncoder.encode(accountId, "UTF-8"));
			parameters.append("&");
			parameters.append("SecretKey=" + URLEncoder.encode(secretKey, "UTF-8"));
			parameters.append("&");
			parameters.append("RefNo=" + URLEncoder.encode(trackId, "UTF-8"));
			parameters.append("&");
			parameters.append("Action=statusByRef");
			
			PrintStream ps = new PrintStream( hConnection.getOutputStream() );
			ps.print(parameters);
			//ps.print("AccountID=15478&SecretKey=959a4be7d9e6c74a83714b457df5da06&RefNo=771140000521413734228080&Action=statusByRef");
			ps.close();

			hConnection.connect();
			//System.out.println("HTTP Status = "+hConnection.getResponseCode());
			if( HttpURLConnection.HTTP_OK == hConnection.getResponseCode() )
			{
				InputStream is = hConnection.getInputStream();

				xmlResponse = IOUtils.toString(is, "UTF-8");
				//System.out.println("xmlResponse = "+xmlResponse);
				is.close();
				hConnection.disconnect();
			}
		}

		catch(Exception ex)
		{
			//ex.printStackTrace();
			throw ex;
		}
		
		return xmlResponse;
	}
	
	public String initiateRefund(String trackId, String accountId,String refundAmount,String secretKey, AdhocPaymentStudentPortalBean bean) throws Exception{
		String xmlResponse = null;
		try
		{

			URL url = new URL( "https://secure.ebs.in/api/1_0" );

			HttpURLConnection hConnection = (HttpURLConnection)
					url.openConnection();
			HttpURLConnection.setFollowRedirects( true );

			hConnection.setDoOutput( true );
			hConnection.setRequestMethod("POST");	

			StringBuilder parameters = new StringBuilder();
			parameters.append("AccountID=" + URLEncoder.encode(accountId, "UTF-8"));
			parameters.append("&");
			parameters.append("SecretKey=" + URLEncoder.encode(secretKey, "UTF-8"));
			parameters.append("&");
			parameters.append("Amount=" + URLEncoder.encode(Integer.parseInt(bean.getRefundAmount())+"", "UTF-8"));
			parameters.append("&");
			parameters.append("PaymentID=" + URLEncoder.encode(bean.getPaymentID(), "UTF-8"));
			parameters.append("&");
			parameters.append("Amount=" + URLEncoder.encode(refundAmount, "UTF-8"));
			parameters.append("&");
			parameters.append("Action=refund");
			
			System.out.println("Parameters = "+parameters);
			PrintStream ps = new PrintStream( hConnection.getOutputStream() );
			ps.print(parameters);
			//ps.print("AccountID=15478&SecretKey=959a4be7d9e6c74a83714b457df5da06&RefNo=771140000521413734228080&Action=statusByRef");
			ps.close();

			hConnection.connect();
			//System.out.println("HTTP Status = "+hConnection.getResponseCode());
			if( HttpURLConnection.HTTP_OK == hConnection.getResponseCode() )
			{
				InputStream is = hConnection.getInputStream();

				xmlResponse = IOUtils.toString(is, "UTF-8");
				//System.out.println("xmlResponse = "+xmlResponse);
				is.close();
				hConnection.disconnect();
			}
		}

		catch(Exception ex)
		{
			//ex.printStackTrace();
			throw ex;
		}
		
		return xmlResponse;
	}
	
	// parse refund response 
	public AdhocPaymentStudentPortalBean parseRefundResponse(String xmlResponse, AdhocPaymentStudentPortalBean bean){
		SAXBuilder saxBuilder = new SAXBuilder();
		try {
			org.jdom.Document doc = saxBuilder.build(new StringReader(xmlResponse));
			Element root = doc.getRootElement();
			String transactionId = root.getAttributeValue("transactionId");
			String paymentId = root.getAttributeValue("paymentId");
			String amount = root.getAttributeValue("amount");
			String dateTime = root.getAttributeValue("dateTime");
			String mode = root.getAttributeValue("mode");
			String referenceNo = root.getAttributeValue("referenceNo");
			String transactionType = root.getAttributeValue("transactionType");
			String status = root.getAttributeValue("status");
			String isFlagged = root.getAttributeValue("isFlagged");


			String errorCode = root.getAttributeValue("errorCode");
			String error = root.getAttributeValue("error");
			
			
			if(error == null){
				bean.setTransactionID(transactionId);
				bean.setPaymentID(paymentId);
				bean.setRespAmount(amount);
				bean.setRespTranDateTime(dateTime);
				bean.setMerchantRefNo(referenceNo);
				bean.setTransactionType(transactionType);
				bean.setIsFlagged(isFlagged);
			}else{
				bean.setError(error);
			}
		} catch (JDOMException e) {
			// handle JDOMException
		} catch (IOException e) {
			// handle IOException
		}
		
		return bean;
	}
	
	public ServiceRequestStudentPortal parseResponse(String xmlResponse, ServiceRequestStudentPortal bean){
		SAXBuilder saxBuilder = new SAXBuilder();
		try {
			org.jdom.Document doc = saxBuilder.build(new StringReader(xmlResponse));
			Element root = doc.getRootElement();
			String transactionId = root.getAttributeValue("transactionId");
			String paymentId = root.getAttributeValue("paymentId");
			String amount = root.getAttributeValue("amount");
			String dateTime = root.getAttributeValue("dateTime");
			String mode = root.getAttributeValue("mode");
			String referenceNo = root.getAttributeValue("referenceNo");
			String transactionType = root.getAttributeValue("transactionType");
			String status = root.getAttributeValue("status");
			String isFlagged = root.getAttributeValue("isFlagged");


			String errorCode = root.getAttributeValue("errorCode");
			String error = root.getAttributeValue("error");
			
			
			if(error == null){
				bean.setTransactionID(transactionId);
				bean.setPaymentID(paymentId);
				bean.setRespAmount(amount);
				bean.setRespTranDateTime(dateTime);
				bean.setMerchantRefNo(referenceNo);
				bean.setTransactionType(transactionType);
				bean.setStatus(status);
				bean.setIsFlagged(isFlagged);
				bean.setRespMode(mode);
			}else{
				bean.setError(error);
				bean.setErrorCode(errorCode);
			}
		} catch (JDOMException e) {
			// handle JDOMException
		} catch (IOException e) {
			// handle IOException
		}
		
		return bean;
	}
	
	
	public String getTransactionTypeFromResponse(String xmlResponse, AdhocPaymentStudentPortalBean bean) throws Exception{
		SAXBuilder saxBuilder = new SAXBuilder();
		String transactionType = null;
		try {
			org.jdom.Document doc = saxBuilder.build(new StringReader(xmlResponse));
			Element root = doc.getRootElement();
			String transactionId = root.getAttributeValue("transactionId");
			String paymentId = root.getAttributeValue("paymentId");
			String amount = root.getAttributeValue("amount");
			String dateTime = root.getAttributeValue("dateTime");
			String mode = root.getAttributeValue("mode");
			String referenceNo = root.getAttributeValue("referenceNo");
			transactionType = root.getAttributeValue("transactionType");
			String status = root.getAttributeValue("status");
			String errorCode = root.getAttributeValue("errorCode");
			String error = root.getAttributeValue("error");
			
			if(error == null){
				return transactionType;
			}else{
				return error;
			}
		} catch (JDOMException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		}
				
	}


}
