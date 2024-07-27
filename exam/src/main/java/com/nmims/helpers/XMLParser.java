package com.nmims.helpers;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.nmims.helpers.AESencrp;

import org.apache.commons.io.IOUtils;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.nmims.beans.ExamBookingTransactionBean;

public class XMLParser {
	
	public static void main(String[] args) throws Exception {
		try{
		String givenString =" MD".toLowerCase();
		String[] arr = givenString.split(" "); 
		
		StringBuffer sb = new StringBuffer(); 
		for (int i = 0; i < arr.length; i++) { 
			sb.append(Character.toUpperCase(arr[i].charAt(0))) .append(arr[i].substring(1)).append(" "); 
	    }
		}catch(Exception e)
		{
			
		}
	}
	
	
	public String queryTransactionStatus(String trackId, String accountId, String secretKey) throws Exception{
		String xmlResponse = null;
		try
		{

			//URL url = new URL( "https://secure.ebs.in/api/1_0" );//Old API
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
			if( HttpURLConnection.HTTP_OK == hConnection.getResponseCode() )
			{
				InputStream is = hConnection.getInputStream();

				xmlResponse = IOUtils.toString(is, "UTF-8");
				is.close();
				hConnection.disconnect();
			}
		}

		catch(Exception ex)
		{
			throw ex;
		}
		
		return xmlResponse;
	}
	public String initiateAdHocRefund(String trackId, String accountId, String secretKey, ExamBookingTransactionBean bean) throws Exception{
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
			parameters.append("Amount=" + URLEncoder.encode(Integer.parseInt(bean.getAmount())+"", "UTF-8"));
			parameters.append("&");
			parameters.append("PaymentID=" + URLEncoder.encode(bean.getPaymentID(), "UTF-8"));
			parameters.append("&");
			parameters.append("Action=refund");
			
			PrintStream ps = new PrintStream( hConnection.getOutputStream() );
			ps.print(parameters);
			//ps.print("AccountID=15478&SecretKey=959a4be7d9e6c74a83714b457df5da06&RefNo=771140000521413734228080&Action=statusByRef");
			ps.close();

			hConnection.connect();
			if( HttpURLConnection.HTTP_OK == hConnection.getResponseCode() )
			{
				InputStream is = hConnection.getInputStream();

				xmlResponse = IOUtils.toString(is, "UTF-8");
				is.close();
				hConnection.disconnect();
			}
		}

		catch(Exception ex)
		{
			throw ex;
		}
		return xmlResponse;
	}
	public String initiateRefund(String trackId, String accountId, String secretKey, ExamBookingTransactionBean bean) throws Exception{
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
			parameters.append("Amount=" + URLEncoder.encode(Integer.parseInt(bean.getRespAmount())+"", "UTF-8"));
			parameters.append("&");
			parameters.append("PaymentID=" + URLEncoder.encode(bean.getPaymentID(), "UTF-8"));
			parameters.append("&");
			parameters.append("Action=refund");
			
			PrintStream ps = new PrintStream( hConnection.getOutputStream() );
			ps.print(parameters);
			//ps.print("AccountID=15478&SecretKey=959a4be7d9e6c74a83714b457df5da06&RefNo=771140000521413734228080&Action=statusByRef");
			ps.close();

			hConnection.connect();
			if( HttpURLConnection.HTTP_OK == hConnection.getResponseCode() )
			{
				InputStream is = hConnection.getInputStream();

				xmlResponse = IOUtils.toString(is, "UTF-8");
				is.close();
				hConnection.disconnect();
			}
		}

		catch(Exception ex)
		{
			throw ex;
		}
		
		return xmlResponse;
	}

	public ExamBookingTransactionBean parseResponse(String xmlResponse, ExamBookingTransactionBean bean){
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
			
		} catch (IOException e) {
			
		}
		
		return bean;
	}
	
	
	public String getTransactionTypeFromResponse(String xmlResponse, ExamBookingTransactionBean bean) throws Exception{
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
