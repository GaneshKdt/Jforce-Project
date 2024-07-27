package com.nmims.helpers;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;

import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Map.Entry;

import com.google.common.base.Throwables;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nmims.beans.ServiceRequestStudentPortal;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.beans.TransactionsBean;
import com.nmims.controllers.BaseController;
import com.paytm.pg.merchant.CheckSumServiceHelper;
import java.math.BigInteger; 
import java.security.MessageDigest; 
import java.security.NoSuchAlgorithmException;
@Component
public class PaymentHelper extends BaseController{
	
	private static final Logger razorpayLogger = LoggerFactory.getLogger("razorpay_payments");
	private static final Logger PAYMENTS_LOGGER = LoggerFactory.getLogger("gateway_payments");
	
	/** For Transaction
	 	Staging: https://securegw-stage.paytm.in/theia/processTransaction
		Production: https://securegw.paytm.in/theia/processTransaction
	 * */
	
	/**
	 * For Transaction Status
	 * 
	 * Staging: https://securegw-stage.paytm.in/order/status
	 * Production: https://securegw.paytm.in/order/status
	 * */
	
	@Value( "${SECURE_SECRET}" )
	private String SECURE_SECRET; // secret key;
	
	@Value( "${ACCOUNT_ID}" )
	private String ACCOUNT_ID;
	
	@Value("${SR_RETURN_URL}")
	private String SR_RETURN_URL;
	//private final String SR_RETURN_URL = "http://localhost:8080/studentportal/srFeeResponse"; //testURL
	
	@Value("${SR_RETURN_URL_MOBILE}")
	private String SR_RETURN_URL_MOBILE;
	
	@Value("${SR_RETURN_URL_GATEWAY}")
	private String SR_RETURN_URL_GATEWAY;
	
	@Value("${PAYTM_TRANS_URL}")
	private String TRANS_URL;
	//private final String TRANS_URL = "https://securegw-stage.paytm.in/theia/processTransaction"; //testURL
	//private final String TRANS_URL = "https://securegw.paytm.in/theia/processTransaction"; //prodURL

	
	@Value("${PAYTM_TRANS_STATUS_URL}")
	private String TRANS_STATUS_URL;
	//private final String TRANS_STATUS_URL = "https://securegw-stage.paytm.in/merchant-status/getTxnStatus"; //testURL
	//private final String TRANS_URL = "https://securegw.paytm.in/theia/processTransaction"; //prodURL

	
	@Value("${PAYTM_TRANS_REFUND_URL}")
	private String TRANS_REFUND_URL;
	//private String TRANS_REFUND_URL = "https://securegw-stage.paytm.in/refund/apply";// testURL=
	//private final String TRANS_REFUND_URL = "https://securegw.paytm.in/refund/api/v1/async/refund"; //production url
	
	@Value("${PAYTM_TRANS_REFUND_STATUS_URL}")
	private String TRANS_REFUND_STATUS_URL;
	//private String TRANS_REFUND_STATUS_URL = "https://securegw-stage.paytm.in/v2/refund/status"; //testURL
	//private final String TRANS_REFUND_STATUS_URL = "https://securegw.paytm.in/v2/refund/status";	//production
	

	//PayTM Creds
	@Value("${PAYTM_MERCHANTMID}")
	private String PAYTM_MERCHANTMID;
	
	//private final String PAYTM_MERCHANTMID = "SVKMst48062445833229";//testCreds
	@Value("${PAYTM_MERCHANTKEY}")
	private String PAYTM_MERCHANTKEY;
	
	//private final String PAYTM_MERCHANTKEY = "A@fUFHY6cIrcfHNL"; //testCreds
	@Value("${PAYTM_INDUSTRY_TYPE_ID}")
	private String PAYTM_INDUSTRY_TYPE_ID;
	
	//private final String PAYTM_INDUSTRY_TYPE_ID = "Retail"; //testCreds
	@Value("${PAYTM_CHANNEL_ID}")
	private String PAYTM_CHANNEL_ID;
	
	//private final String PAYTM_CHANNEL_ID = "WEB"; //testCreds
	@Value("${PAYTM_WEBSITE}")
	private String PAYTM_WEBSITE;
	
	//private final String PAYTM_WEBSITE = "WEBSTAGING"; //testCreds
	
	@Value("${BILLDESK_NMIMSID}")
	private String BILLDESK_NMIMSID;
	
	@Value("${BILLDESK_SECURITY_ID}")
	private String BILLDESK_SECURITY_ID;
	
	@Value("${BILLDESK_CHECKSUM_KEY}")
	private String BILLDESK_CHECKSUM_KEY;
	
	@Value("${BILLDESK_TRANS_STATUS_URL}")
	private String BILLDESK_TRANS_STATUS_URL;
	
	@Value("${BILLDESK_TRANS_URL}")
	private String BILLDESK_TRANS_URL;
	
	@Value("${BILLDESK_REFUND_URL}")
	private String BILLDESK_REFUND_URL;
	
	//PAYU Creds
	@Value("${PAYU_SECRET_KEY}")
	private String PAYU_SECRET_KEY;
	
	//private final String PAYU_SECRET_KEY = "bWIMX5"; //testCreds
	@Value("${PAYU_SALT}")
	private String PAYU_SALT;
	
	//private final String PAYU_SALT = "2iAFCw0i"; //testCreds
	@Value("${PAYU_TRANS_URL}")
	private String PAYU_TRANS_URL;
	
	//private final String PAYU_TRANS_URL = "https://test.payu.in/_payment"; //testCreds
	@Value("${PAYU_TRANS_STATUS_URL}")
	private String PAYU_TRANS_STATUS_URL;
	
	//private final String PAYU_TRANS_STATUS_URL = "https://test.payu.in/merchant/postservice.php?form=2"; //testCreds
	
	@Value("${SERVER_PATH}")
	private String SERVER_PATH;
	
	private RestTemplate restTemplate = new RestTemplate();
	
	/**
	 * CheckSum generation function by bill desk
	 * */
	public void test() { 
		//System.out.println("-----====== >>> >> PAYU_SECRET_KEY : " + PAYU_SECRET_KEY );
		//System.out.println("-----====== >>> >> PAYU_SALT : " + PAYU_SALT );
		//System.out.println("-----====== >>> >> PAYU_TRANS_URL : " + PAYU_TRANS_URL );
	}
	public PaymentHelper() {
		//System.out.println("-----====== >>> >> PAYU_SECRET_KEY : " + PAYU_SECRET_KEY );
		//System.out.println("-----====== >>> >> PAYU_SALT : " + PAYU_SALT );
		//System.out.println("-----====== >>> >> PAYU_TRANS_URL : " + PAYU_TRANS_URL );
	}
	public static String HmacSHA256(String message,String secret)  {
		MessageDigest md = null;
		try {
			Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
			SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
			sha256_HMAC.init(secret_key);


			byte raw[] = sha256_HMAC.doFinal(message.getBytes());

			StringBuffer ls_sb=new StringBuffer();
			for(int i=0;i<raw.length;i++)
				ls_sb.append(char2hex(raw[i]));
			return ls_sb.toString(); //step 6
		}catch(Exception e){
			//e.printStackTrace();
			return null;
		}
	}

	public static String char2hex(byte x){
		 char arr[]={
				 '0','1','2','3',
		         '4','5','6','7',
		         '8','9','A','B',
		         'C','D','E','F'
		 };

		 char c[] = {arr[(x & 0xF0)>>4],arr[x & 0x0F]};
		 return (new String(c));
	}
	/**
	 * End of CheckSum generation function by bill desk
	 * */
	
	
	/**
	 * Generate checksum for payU
	 * */
	
	public String generatePayuCheckSumSHA512(String input) 
    { 
        try { 
            // getInstance() method is called with algorithm SHA-512 
            MessageDigest md = MessageDigest.getInstance("SHA-512"); 
  
            // digest() method is called 
            // to calculate message digest of the input string 
            // returned as array of byte 
            byte[] messageDigest = md.digest(input.getBytes()); 
  
            // Convert byte array into signum representation 
            BigInteger no = new BigInteger(1, messageDigest); 
  
            // Convert message digest into hex value 
            String hashtext = no.toString(16); 
  
            // Add preceding 0s to make it 32 bit 
            while (hashtext.length() < 128) { 
                hashtext = "0" + hashtext; 
            } 
  
            // return the HashText 
            return hashtext; 
        } 
  
        // For specifying wrong message digest algorithms 
        catch (NoSuchAlgorithmException e) { 
            throw new RuntimeException(e); 
        } 
    } 
	
	/**
	 * End of checksum for payU
	 * */
	
	
	public String generatePayuCheckSum(ServiceRequestStudentPortal sr,StudentStudentPortalBean student,String requestId) {
		try {
			String data = PAYU_SECRET_KEY + "|" + sr.getTrackId() + "|" + sr.getAmount() + "|" + sr.getDescription() + "|" + student.getFirstName() + "|" + student.getEmailId() + "|||||||||||" + PAYU_SALT;
			
			String checksum = generatePayuCheckSumSHA512(data);
			sr.setSecureHash(checksum);
			return "true";
		}
		catch (Exception e) {
			// TODO: handle exception
			return e.getMessage();
		}
	}
	
	//"MERCHANT|1000000000|NA|12.00|XXX|NA|NA|INR|DIRECT|R|NA|NA|NA|F|111111111|NA|NA|NA|NA|NA|NA|NA|",ChecksumKey
	
	public String generateBillDeskCheckSum(ServiceRequestStudentPortal sr) {
		try {
			String message = BILLDESK_NMIMSID + "|" + sr.getTrackId() + "|NA|" + sr.getAmount() + "|NA|NA|NA|INR|NA|R|" + BILLDESK_SECURITY_ID + "|NA|NA|F|NA|NA|"+ "SR_" + sr.getServiceRequestType() +"|NA|NA|NA|NA|";
			if(sr.getIsMobile()) {
				message = message  + SR_RETURN_URL_MOBILE;
			}else {
				message = message  + SR_RETURN_URL;
			}
			String checkSum = HmacSHA256(message,BILLDESK_CHECKSUM_KEY);
			sr.setSecureHash(checkSum);
			return "true";
		}
		catch (Exception e) {
			return (String) e.getMessage();
		}
	}
	
	
	
	public String generateCheckSum(ServiceRequestStudentPortal sr,StudentStudentPortalBean student,String requestId) {
		try {
			TreeMap<String, String> paytmParams = new TreeMap<String, String>();
			paytmParams.put("MID",PAYTM_MERCHANTMID);
			paytmParams.put("ORDER_ID",sr.getTrackId());
			paytmParams.put("CHANNEL_ID",PAYTM_CHANNEL_ID);
			paytmParams.put("CUST_ID",student.getSapid());
			paytmParams.put("MOBILE_NO",student.getMobile());
			paytmParams.put("EMAIL",student.getEmailId());
			paytmParams.put("TXN_AMOUNT",sr.getAmount());
			paytmParams.put("WEBSITE",PAYTM_WEBSITE);
			paytmParams.put("INDUSTRY_TYPE_ID",PAYTM_INDUSTRY_TYPE_ID);
			paytmParams.put("MERC_UNQ_REF","SR_" + sr.getServiceRequestType());
			if(sr.getIsMobile()) {
				paytmParams.put("CALLBACK_URL", SR_RETURN_URL_MOBILE);	
			}else {
				paytmParams.put("CALLBACK_URL", SR_RETURN_URL);
			}
			String checkSum =  CheckSumServiceHelper.getCheckSumServiceHelper().genrateCheckSum(PAYTM_MERCHANTKEY, paytmParams);
			sr.setSecureHash(checkSum);
			return "true";
		}
		catch(Exception e) {
			return (String) e.getMessage();
		}
	}

	
	public ModelAndView setModelData(ModelAndView mv,ServiceRequestStudentPortal sr,StudentStudentPortalBean student,String requestId) {
		mv.addObject("TRANS_URL", TRANS_URL);
		mv.addObject("WEBSITE", PAYTM_WEBSITE);
		mv.addObject("ORDER_ID",sr.getTrackId());
		mv.addObject("CUST_ID", student.getSapid());
		mv.addObject("MOBILE_NO",student.getMobile());
		mv.addObject("EMAIL",student.getEmailId());
		mv.addObject("INDUSTRY_TYPE_ID",PAYTM_INDUSTRY_TYPE_ID);
		mv.addObject("CHANNEL_ID", PAYTM_CHANNEL_ID);
		mv.addObject("TXN_AMOUNT", sr.getAmount());
		mv.addObject("MERC_UNQ_REF","SR_" + sr.getServiceRequestType());
		if(sr.getIsMobile()) {
			mv.addObject("CALLBACK_URL",SR_RETURN_URL_MOBILE);	
		}else {
			mv.addObject("CALLBACK_URL",SR_RETURN_URL);
		}
		mv.addObject("CHECKSUMHASH", sr.getSecureHash());
		mv.addObject("MERCHANTMID",PAYTM_MERCHANTMID);
		return mv;
	}
	
	
	public ModelAndView setBillDeskModelData(ModelAndView mv,ServiceRequestStudentPortal sr,StudentStudentPortalBean student,String requestId) {
		String message = BILLDESK_NMIMSID + "|" + sr.getTrackId() + "|NA|" + sr.getAmount() + "|NA|NA|NA|INR|NA|R|" + BILLDESK_SECURITY_ID + "|NA|NA|F|NA|NA|"+ "SR_" + sr.getServiceRequestType() +"|NA|NA|NA|NA|" ;
		
		if(sr.getIsMobile()) {
			message = message + SR_RETURN_URL_MOBILE + "|" + sr.getSecureHash();
		}else {
			message = message + SR_RETURN_URL + "|" + sr.getSecureHash();
		}
		mv.addObject("trans_url", BILLDESK_TRANS_URL);
		mv.addObject("msg", message);
		return mv;
	}
	
	public ModelAndView setPayuModelData(ModelAndView mv,ServiceRequestStudentPortal sr,StudentStudentPortalBean student,String requestId) {
		mv.addObject("trans_url", PAYU_TRANS_URL);
		mv.addObject("key", PAYU_SECRET_KEY);
		mv.addObject("txnid", sr.getTrackId());
		mv.addObject("amount", sr.getAmount());
		mv.addObject("productinfo", sr.getDescription());
		mv.addObject("firstname", student.getFirstName());
		mv.addObject("email", student.getEmailId());
		mv.addObject("phone", student.getMobile());
		if(sr.getIsMobile()) {
			mv.addObject("surl", SR_RETURN_URL_MOBILE);
			mv.addObject("furl", SR_RETURN_URL_MOBILE);	
		}else {
			mv.addObject("surl", SR_RETURN_URL);
			mv.addObject("furl", SR_RETURN_URL);
		}
		mv.addObject("hash", sr.getSecureHash());
		return mv;
	}
	
	
	public boolean verificationCheckSum(HttpServletRequest request) throws Exception {
		String paytmChecksum = null;
		// Create a tree map from the form post param
		TreeMap<String, String> paytmParams = new TreeMap<String, String>();
		// Request is HttpServletRequest
		for (Entry<String, String[]> requestParamsEntry : request.getParameterMap().entrySet()) {
		    if ("CHECKSUMHASH".equalsIgnoreCase(requestParamsEntry.getKey())){
		        paytmChecksum = requestParamsEntry.getValue()[0];
		    } else {
		        paytmParams.put(requestParamsEntry.getKey(), requestParamsEntry.getValue()[0]);
		    }
		}
		// Call the method for verification
		boolean isValidChecksum = CheckSumServiceHelper.getCheckSumServiceHelper().verifycheckSum(PAYTM_MERCHANTKEY, paytmParams, paytmChecksum);
		// If isValidChecksum is false, then checksum is not valid
		if(isValidChecksum){
		    return true;
		}else{
		    return false;
		}
	}

	
	
	public boolean verificationBillDeskCheckSum(String[] responseList){
		try {
			String message = ""; 
			for(int i=0;i < (responseList.length - 1);i++) {
				if(i == (responseList.length - 2)) {
					message = message + responseList[i];
				}else {
					message = message + responseList[i] + "|";
				}
				
			}
			String checkSum = HmacSHA256(message,BILLDESK_CHECKSUM_KEY);
			if(checkSum.equalsIgnoreCase(responseList[25])) {
				return true;
			}
			return false;
		}
		catch (Exception e) {
			//e.printStackTrace();
			return false;
		}
	}
	
	
	public boolean verificationPayuCheckSum(HttpServletRequest request){
		try {
			String data = PAYU_SALT + "|" + request.getParameter("status") + "|||||||||||" + request.getParameter("email") + "|" + request.getParameter("firstname") + "|" + request.getParameter("productinfo") + "|" + request.getParameter("amount") + "|" + request.getParameter("txnid") + "|" + request.getParameter("key");
			String checksum = generatePayuCheckSumSHA512(data);
			if(checksum.equalsIgnoreCase(request.getParameter("hash"))) {
				return true;
			}
			return false;
		}
		catch (Exception e) {
			//e.printStackTrace();
			return false;
		}
	}
	
	
	public ServiceRequestStudentPortal createPaytmResponseBean(HttpServletRequest request) {
		ServiceRequestStudentPortal bean = new ServiceRequestStudentPortal();
		
		ServiceRequestStudentPortal sr = (ServiceRequestStudentPortal) request.getSession().getAttribute("sr");
		String sapid = (String) request.getSession().getAttribute("userId");
		String trackId = (String) request.getSession().getAttribute("trackId");
		String device = (String) request.getSession().getAttribute("device");
		
		
		

		
		bean.setId(sr.getId());
		bean.setServiceRequestType(sr.getServiceRequestType());
		bean.setProductType(sr.getProductType());
		bean.setSapId(sapid);
		bean.setTrackId(trackId);
		bean.setDevice(device);
		bean.setAmount(sr.getAmount());
		
		bean.setPaymentOption("paytm");
		bean.setResponseMessage(request.getParameter("RESPMSG"));
		bean.setTransactionID(request.getParameter("TXNID"));
		//bean.setRequestID(request.getParameter("ORDERID"));
		bean.setMerchantRefNo(request.getParameter("ORDERID"));
		bean.setSecureHash(request.getParameter("CHECKSUMHASH"));
		bean.setRespAmount(request.getParameter("TXNAMOUNT"));
		bean.setRespTranDateTime(request.getParameter("TXNDATE"));
		bean.setResponseCode(request.getParameter("RESPCODE"));
		bean.setRespPaymentMethod(request.getParameter("PAYMENTMODE"));
		//bean.setIsFlagged(request.getParameter("IsFlagged"));
		bean.setPaymentID(request.getParameter("BANKTXNID"));
		bean.setBankName(request.getParameter("BANKNAME"));
		//bean.setError(request.getParameter("Error"));
		bean.setDescription( sr.getServiceRequestType() + " for " + sapid);
		
		request.getSession().setAttribute("paymentResponseBean",bean);
		return bean;
	}
	
	
	public ServiceRequestStudentPortal createBillDeskResponseBean(HttpServletRequest request) {
		ServiceRequestStudentPortal bean = new ServiceRequestStudentPortal();
		String[] responseList = request.getParameter("msg").split("\\|");
		ServiceRequestStudentPortal sr = (ServiceRequestStudentPortal) request.getSession().getAttribute("sr");
		String sapid = (String) request.getSession().getAttribute("userId");
		String trackId = (String) request.getSession().getAttribute("trackId");
		String device = (String) request.getSession().getAttribute("device");
		String responseDateTime = null;
		DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date date = format.parse(responseList[13]);
			responseDateTime = sf.format(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		bean.setSapId(sapid);
		bean.setTrackId(trackId);
		bean.setDevice(device);
		bean.setId(sr.getId());
		bean.setServiceRequestType(sr.getServiceRequestType());
		bean.setPaymentOption("billdesk");
		bean.setTransactionID(responseList[2]);
		bean.setMerchantRefNo(responseList[3]);
		bean.setSecureHash(responseList[25]);
		bean.setRespAmount(responseList[4]);
		bean.setRespTranDateTime(responseList[13]);
		bean.setResponseCode(responseList[14]);
		if(bean.getResponseCode().equalsIgnoreCase("0300")) {
			bean.setResponseMessage("Success");
		}else {
			bean.setResponseMessage(responseList[24]);
		}
		//auth - 16
		bean.setRespPaymentMethod(responseList[7]);
		//bean.setIsFlagged(request.getParameter("IsFlagged"));
		//bean.setPaymentID(request.getParameter("BANKTXNID"));
		bean.setBankName(responseList[5]);
		bean.setRespTranDateTime(responseDateTime);
		//bean.setError(request.getParameter("Error"));
		bean.setDescription( sr.getServiceRequestType() + " for " + sapid);
		request.getSession().setAttribute("paymentResponseBean",bean);
		return bean;
	}
	
	public ServiceRequestStudentPortal createPayuResponseBean(HttpServletRequest request) {
		ServiceRequestStudentPortal bean = new ServiceRequestStudentPortal();
		ServiceRequestStudentPortal sr = (ServiceRequestStudentPortal) request.getSession().getAttribute("sr");
		String sapid = (String) request.getSession().getAttribute("userId");
		String trackId = (String) request.getSession().getAttribute("trackId");
		String device = (String) request.getSession().getAttribute("device");
		
		bean.setSapId(sapid);
		bean.setTrackId(trackId);
		bean.setDevice(device);
		bean.setId(sr.getId());
		bean.setServiceRequestType(sr.getServiceRequestType());
		bean.setPaymentOption("payu");
		bean.setProductType(sr.getProductType());
		bean.setResponseMessage(request.getParameter("status"));
		bean.setTransactionID(request.getParameter("mihpayid"));
		bean.setMerchantRefNo(request.getParameter("txnid"));
		bean.setSecureHash(request.getParameter("hash"));
		bean.setRespAmount(request.getParameter("amount"));
		bean.setRespTranDateTime(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
		bean.setResponseCode(request.getParameter("status"));
		bean.setRespPaymentMethod(request.getParameter("mode"));
		//bean.setIsFlagged(request.getParameter("IsFlagged"));
		bean.setPaymentID(request.getParameter("bank_ref_num"));
		bean.setBankName(request.getParameter("bankcode"));
		bean.setError(request.getParameter("error"));
		bean.setDescription( sr.getServiceRequestType() + " for " + sapid);
		request.getSession().setAttribute("paymentResponseBean",bean);
		return bean;
	}
	
	public ServiceRequestStudentPortal createHdfcResponseBean(HttpServletRequest request) {
		ServiceRequestStudentPortal bean = new ServiceRequestStudentPortal();
		
		ServiceRequestStudentPortal sr = (ServiceRequestStudentPortal) request.getSession().getAttribute("sr");
		String sapid = (String) request.getSession().getAttribute("userId");
		String trackId = (String) request.getSession().getAttribute("trackId");
		String device = (String) request.getSession().getAttribute("device");
		
		bean.setSapId(sapid);
		bean.setTrackId(trackId);
		bean.setDevice(device);
		bean.setId(sr.getId());
		bean.setServiceRequestType(sr.getServiceRequestType());
		bean.setPaymentOption("hdfc"); 
		bean.setResponseMessage(request.getParameter("ResponseMessage"));
		bean.setTransactionID(request.getParameter("TransactionID"));
		bean.setRequestID(request.getParameter("RequestID"));
		bean.setMerchantRefNo(request.getParameter("MerchantRefNo"));
		bean.setSecureHash(request.getParameter("SecureHash"));
		bean.setRespAmount(request.getParameter("Amount"));
		bean.setRespTranDateTime(request.getParameter("DateCreated"));
		bean.setResponseCode(request.getParameter("ResponseCode"));
		bean.setRespPaymentMethod(request.getParameter("PaymentMethod"));
		//bean.setIsFlagged(request.getParameter("IsFlagged"));
		bean.setPaymentID(request.getParameter("PaymentID"));
		//bean.setError(request.getParameter("Error"));
		bean.setDescription(sr.getServiceRequestType() + ":" + sapid);
		request.getSession().setAttribute("paymentResponseBean",bean);
		return bean;
	}
	
	public ServiceRequestStudentPortal CreateResponseBean(HttpServletRequest request) {
		String paymentOption = (String) request.getSession().getAttribute("paymentOption");
		if(paymentOption.equalsIgnoreCase("paytm")) {
			return this.createPaytmResponseBean(request);
		}
		else if(paymentOption.equalsIgnoreCase("hdfc")) {
			return this.createHdfcResponseBean(request);
		}
		else if(paymentOption.equalsIgnoreCase("billdesk")) {
			return this.createBillDeskResponseBean(request);
		}
		else if(paymentOption.equalsIgnoreCase("payu")) {
			return this.createPayuResponseBean(request);
		}
		return null;
	}
	
	public String checkErrorInHdfcPayment(HttpServletRequest request) {
		
		String errorMessage = null;
		String trackId = (String) request.getSession().getAttribute("trackId");
		String amount = (String) request.getSession().getAttribute("amount");
		
		boolean isHashMatching = isHashMatching(request);
		boolean isTrackIdMatching = isTrackIdMatching(request, trackId);
		boolean isAmountMatching = isAmountMatching(request, amount);
		boolean isSuccessful = isTransactionSuccessful(request);
		
		/*boolean isHashMatching = true;
		boolean isTrackIdMatching = true;
		boolean isAmountMatching = false;
		boolean isSuccessful = true;*/
		
	/*	if(!"Wallet".equals(typeOfPayment)){
			 isHashMatching = isHashMatching(request);
			 isAmountMatching = isAmountMatching(request, amount);
			 isTrackIdMatching = isTrackIdMatching(request, trackId);
		}*/
		
		
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
		return errorMessage;
	}
	
	public JsonObject getPaytmTransactionStatus(String trackId) {
		
		TreeMap<String, String> paytmParams = new TreeMap<String, String>();
		paytmParams.put("MID", PAYTM_MERCHANTMID);
		paytmParams.put("ORDERID", trackId);
		
		try {
			String checkSum = CheckSumServiceHelper.getCheckSumServiceHelper().genrateCheckSum(PAYTM_MERCHANTKEY, paytmParams);
		    paytmParams.put("CHECKSUMHASH", checkSum);
		    JSONObject obj = new JSONObject(paytmParams);
		    String postData = "JsonData=" + obj.toString();
		    URL url =  new URL(TRANS_STATUS_URL);
		    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		    connection.setRequestMethod("POST");
		    connection.setRequestProperty("contentType", "application/json");
		    connection.setUseCaches(false);
		    connection.setDoOutput(true);

		    DataOutputStream requestWriter = new DataOutputStream(connection.getOutputStream());
		    requestWriter.writeBytes( postData);
		    requestWriter.close();
		    String responseData = "";
		    InputStream is = (InputStream) connection.getInputStream();
		    BufferedReader responseReader = new BufferedReader(new InputStreamReader(is));
		    if((responseData = responseReader.readLine()) != null) {
		    	System.out.append("Response Json = " + responseData);
		    }
		    System.out.append("Requested Json = " + postData + " ");
		    JsonParser parser = new JsonParser();
		    JsonObject response = parser.parse(responseData).getAsJsonObject();
		    return response;
		} catch (Exception exception) {
		    //exception.printStackTrace();
		    return null;
		}
		
	}
	
	public String getBillDeskTransactionStatus(String trackId) {
		try {
//			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
//			Date date = new Date();
//			System.out.println(formatter.format(date));  
//			String msg = "0122|NMIMS|" + trackId + "|" + formatter.format(date);
//			String checkSum = HmacSHA256(msg,BILLDESK_SECURITY_ID);
//			msg = msg + "|" + checkSum.toUpperCase();
//			String postData = "msg="+msg;
//			
//			RestTemplate restTemplate = new RestTemplate();
//			HttpHeaders headers =  new HttpHeaders();
//			headers.add("Content-Type", "application/x-www-form-urlencoded");
//			System.out.println(msg);
//			HttpEntity<String> entity = new HttpEntity<String>(postData,headers);
//			ResponseEntity<String> response = restTemplate.exchange(BILLDESK_TRANS_STATUS_URL, HttpMethod.POST, entity, String.class);
//			System.out.println(response.getBody());
//			return response.getBody();
			
			
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
			Date date = new Date();
			  
			String msg = "0122|"+ BILLDESK_NMIMSID +"|" + trackId + "|" + formatter.format(date);
			String checkSum = HmacSHA256(msg,BILLDESK_CHECKSUM_KEY);
			msg = msg + "|" + checkSum.toUpperCase();
			String postData = "msg="+msg;
		
			URL obj = new URL(BILLDESK_TRANS_STATUS_URL);
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

			//add reuqest header
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			
			// Send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(postData);
			wr.flush();
			wr.close();

			int responseCode = con.getResponseCode();
			

			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			
			//print result
			
			return response.toString();
		}
		catch (Exception e) {
			PAYMENTS_LOGGER.error("{} Error while fetching billdesk transaction status : {}", trackId, Throwables.getStackTraceAsString(e));
			// TODO: handle exception
			//e.printStackTrace();
			return null;
		}
	}
	
	
	public JsonObject getPayuTransactionStatus(String trackId) {
		try {  
			String msg = PAYU_SECRET_KEY + "|verify_payment|" + trackId + "|" + PAYU_SALT;
			String checkSum = generatePayuCheckSumSHA512(msg);
			String postData = "key="+ PAYU_SECRET_KEY + "&command=verify_payment&hash=" + checkSum + "&var1=" + trackId;
		
			URL obj = new URL(PAYU_TRANS_STATUS_URL);
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

			//add reuqest header
			con.setRequestMethod("POST");

			
			// Send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(postData);
			wr.flush();
			wr.close();

			int responseCode = con.getResponseCode();
			

			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			
			//print result
			
			JsonParser parser = new JsonParser();
		    JsonObject responseData = parser.parse(response.toString()).getAsJsonObject();
			return responseData;
		}
		catch (Exception e) {
			// TODO: handle exception
			//e.printStackTrace();
			return null;
		}
	}
	
	public String getHdfcTransactionStatus(String trackId) {
		try {
			XMLParser parser = new XMLParser();
			
			String xmlResponse = parser.queryTransactionStatus(trackId, ACCOUNT_ID, SECURE_SECRET);
			SAXBuilder saxBuilder = new SAXBuilder();
			org.jdom.Document doc = saxBuilder.build(new StringReader(xmlResponse));
			Element root = doc.getRootElement();
			return root.getAttributeValue("error");
		}
		catch (Exception e) {
			// TODO: handle exception
			//e.printStackTrace();
			return null;
		}
	}
	
		
	public String checkErrorInPaytmPayment(HttpServletRequest request) {
		
		//String errorMessage = null;
		
		String amount = (String) request.getSession().getAttribute("amount");
		String trackId = (String) request.getSession().getAttribute("trackId");
		
		try {
			if(!request.getParameter("ORDERID").equalsIgnoreCase(trackId)) {
				return "Error in processing payment. Error: Invalid trackId found,Transaction trackId: " + request.getParameter("ORDERID") + " and current trackId: " + trackId;
			}
			//isSuccess
			if(!request.getParameter("RESPMSG").equalsIgnoreCase("Txn Success")) {
				return "Error in processing payment. Error:  " + request.getParameter("RESPMSG") + " Code: " + request.getParameter("RESPCODE");
			}
			
			boolean verificationCheckSum = verificationCheckSum(request);
			
			if(verificationCheckSum) {
				if(Float.parseFloat(request.getParameter("TXNAMOUNT")) != Float.parseFloat(amount)) {
					return "Error in processing payment. Error: Fees " + amount + " not matching with amount paid ";
				}
				if("Txn Success".equalsIgnoreCase(request.getParameter("RESPMSG"))) {
					return null;	// success response;
				}
				return "Error in processing payment.";
			}
			else {
				return "Error in processing payment. Error: Hashvalue not matching. Tampering in response found. Track ID: " + trackId;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return "Error in processing payment. Error: " + e.getMessage();
		}
	}
	
	public String checkErrorInPayuPayment(HttpServletRequest request) {
		
		//String errorMessage = null;
		
		String amount = (String) request.getSession().getAttribute("amount");
		String trackId = (String) request.getSession().getAttribute("trackId");
		
		try {
			
			if(!request.getParameter("txnid").equalsIgnoreCase(trackId)) {
				return "Error in processing payment. Error: Invalid trackId found,Transaction trackId: " + request.getParameter("txnid") + " and current trackId: " + trackId;
			}
			
			//isSuccess
			if(!request.getParameter("status").equalsIgnoreCase("success")) {
				return "Error in processing payment. Error:  " + request.getParameter("error");
			}
			boolean verificationCheckSum = verificationPayuCheckSum(request);
			
			if(verificationCheckSum) {
				if(Float.parseFloat(request.getParameter("amount")) != Float.parseFloat(amount)) {
					return "Error in processing payment. Error: Fees " + amount + " not matching with amount paid ";
				}
				if("success".equalsIgnoreCase(request.getParameter("status"))) {
					return null;	// success response;
				}
				return "Error in payment response.";
			}
			else {
				return "Error in processing payment. Error: Hashvalue not matching. Tampering in response found. Track ID: " + trackId;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return "Error in processing payment. Error: " + e.getMessage();
		}
	}
	
	
	public String checkErrorInBillDeskPayment(HttpServletRequest request) {
		
		String errorMessage = null;
		
		String amount = (String) request.getSession().getAttribute("amount");
		String trackId = (String) request.getSession().getAttribute("trackId");
		
		try {
			String[] responseList = request.getParameter("msg").split("\\|");
			
			if(!responseList[1].equalsIgnoreCase(trackId)) {
				return "Error in processing payment. Error: Invalid trackId found,Transaction trackId: " + responseList[1] + " and current trackId: " + trackId;
			}
			//isSuccess
			
			if(!responseList[14].equalsIgnoreCase("0300")) {
				return "Error in processing payment. Error:  " + responseList[24] + " Code: " + responseList[14];
			}
			boolean verificationCheckSum = verificationBillDeskCheckSum(responseList);
			
			if(verificationCheckSum) {
				if(Float.parseFloat(responseList[4]) != Float.parseFloat(amount)) {
					errorMessage = "Error in processing payment. Error: Fees " + amount + " not matching with amount paid ";
				}
				return errorMessage;
			}
			else {
				return "Error in processing payment. Error: Hashvalue not matching. Tampering in response found. Track ID: " + trackId;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return "Error in processing payment. Error: " + e.getMessage();
		}
	}
	
	
	
	/**
	 * Common function to Check Any Error During Transaction
	 * */
	public String checkErrorInPayment(HttpServletRequest request) {
		String paymentOption = (String) request.getSession().getAttribute("paymentOption");
		
		if(paymentOption.equalsIgnoreCase("paytm")) {
			return this.checkErrorInPaytmPayment(request);
		}
		else if(paymentOption.equalsIgnoreCase("hdfc")) {
			return this.checkErrorInHdfcPayment(request);
		}
		else if(paymentOption.equalsIgnoreCase("billdesk")) {
			return this.checkErrorInBillDeskPayment(request);
		}
		else if(paymentOption.equalsIgnoreCase("payu")) {
			return this.checkErrorInPayuPayment(request);
		}
		return "Error Invalid Payment Gateway Found";
	}
	
	
	/**
	 * Common generate checksum code 
	 * */
	
	public String generateCommonCheckSum(ServiceRequestStudentPortal sr,StudentStudentPortalBean student,String requestId,String paymentOption) {
		if(paymentOption.equalsIgnoreCase("payu")) {
			return generatePayuCheckSum(sr, student, requestId);
		}
		else if(paymentOption.equalsIgnoreCase("billdesk")) {
			return generateBillDeskCheckSum(sr);
		}
		else if(paymentOption.equalsIgnoreCase("paytm")) {
			return generateCheckSum(sr,student,requestId);
		}
		return null;
	}
	
	/**
	 * Common function to set modeData
	 * */
	
	
	public ModelAndView setCommonModelData(ModelAndView mv,ServiceRequestStudentPortal sr,StudentStudentPortalBean student,String requestId,String paymentOption) {
		if(paymentOption.equalsIgnoreCase("payu")) {
			return setPayuModelData(mv, sr, student, requestId);
		}
		else if(paymentOption.equalsIgnoreCase("billdesk")) {
			return setBillDeskModelData(mv,sr,student,requestId);
		}
		else if(paymentOption.equalsIgnoreCase("paytm")) {
			return setModelData(mv,sr,student,requestId);
		} 
		return mv;
	}
	
	
	public ModelAndView populateGoToGateway(ServiceRequestStudentPortal sr,
			StudentStudentPortalBean student) {
		
		ModelAndView mav = new ModelAndView("jsp/payment");
		
		mav.addObject("track_id", sr.getTrackId());
		mav.addObject("sapid", sr.getSapId());
		mav.addObject("type", "Service_Request");
		mav.addObject("amount", sr.getAmount());
		mav.addObject("description", sr.getDescription());
		mav.addObject("portal_return_url", SR_RETURN_URL_GATEWAY);
		mav.addObject("created_by", sr.getSapId());
		mav.addObject("updated_by", sr.getSapId());
		mav.addObject("mobile", student.getMobile() );
		mav.addObject("email_id", student.getEmailId());
		mav.addObject("first_name", student.getFirstName());
		mav.addObject("source", "web");
		mav.addObject("response_method", "POST");
		
		return mav;
	}
	
	
	/**
	 * payment refund logic
	 * */
	
	public JsonObject refundInitiate(String tracking_id,String transaction_id,String refund_amount) {
		try {
			
//	        String paytmParams_body = "{\"mid\":\"" + PAYTM_MERCHANTMID + "\",\"txnType\":\"REFUND\",\"orderId\":\""+ tracking_id +"\",\"txnId\":\""+ transaction_id +"\",\"refId\":\""+ refId +"\",\"refundAmount\":\""+ refund_amount +"\"}";
//	        
//	        String checksum = "";
//	        
//	        String paytmParams_head = "{\"clientId\":\"C11\",\"version\":\"v1\",\"signature\":\"" + checksum + "\"}";
//	        
//	        URL url = new URL(TRANS_REFUND_URL);
//	        
//	        String post_data = "{\"body\":" + paytmParams_body + ",\"head\":" + paytmParams_head + "}";
	        
			String refId = tracking_id + System.currentTimeMillis();	//unique refId for refund payTm
			/* initialize an object */
			JSONObject paytmParams = new JSONObject();

			/* body parameters */
			JSONObject body = new JSONObject();

			/* Find your MID in your Paytm Dashboard at https://dashboard.paytm.com/next/apikeys */
			body.put("mid", PAYTM_MERCHANTMID);

			/* This has fixed value for refund transaction */
			body.put("txnType", "REFUND");

			/* Enter your order id for which refund needs to be initiated */
			body.put("orderId",tracking_id);

			/* Enter transaction id received from Paytm for respective successful order */
			body.put("txnId", transaction_id);

			/* Enter numeric or alphanumeric unique refund id */
			body.put("refId", refId);

			/* Enter amount that needs to be refunded, this must be numeric */
			body.put("refundAmount", refund_amount);

			/**
			* Generate checksum by parameters we have in body
			* You can get Checksum JAR from https://developer.paytm.com/docs/checksum/
			* Find your Merchant Key in your Paytm Dashboard at https://dashboard.paytm.com/next/apikeys 
			*/
			String checksum = CheckSumServiceHelper.getCheckSumServiceHelper().genrateCheckSum(PAYTM_MERCHANTKEY, body.toString());

			/* head parameters */
			JSONObject head = new JSONObject();

			/* This is used when you have two different merchant keys. In case you have only one please put - C11 */
			head.put("clientId", "C11");

			/* put generated checksum value here */
			head.put("signature", checksum);

			/* prepare JSON string for request */
			paytmParams.put("body", body);
			paytmParams.put("head", head);
			String post_data = paytmParams.toString();
			URL url = new URL(TRANS_REFUND_URL);
	        
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setRequestMethod("POST");
	        connection.setRequestProperty("Content-Type", "application/json");
	        connection.setUseCaches(false);
	        connection.setDoOutput(true);
	        
	        DataOutputStream requestWriter = new DataOutputStream(connection.getOutputStream());
	        requestWriter.writeBytes(post_data);
	        requestWriter.close();
	        String responseData = "";
	        InputStream is = connection.getInputStream();
	        BufferedReader responseReader = new BufferedReader(new InputStreamReader(is));
	        if ((responseData = responseReader.readLine()) != null) {
	        	System.out.append("Response: " + responseData);
	        }
	        responseReader.close();
	        JsonParser parser = new JsonParser();
		    JsonObject response = parser.parse(responseData).getAsJsonObject();
		    return response;
        } catch (Exception exception) {
        	//exception.printStackTrace();
        	return null;
        }
	}
	
	
	/*
	 * billdesk refund initiate
	 * */
	public String billdeskRefundInitiate(String trackId,String transactionRefNo,String refund_amount,String TxnAmount,String TxnDate,String refundId) {
		try {
			
			
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
			Date date = new Date();
			  
			//String message = "0400|" + BILLDESK_NMIMSID + "|" + transactionRefNo + "|" +  TxnDate + "|" + trackId + "|" + Float.parseFloat(TxnAmount) + "|" +Float.parseFloat(refund_amount) + "|" + formatter.format(date) + "|" + refundId + "|NA|NA|NA";
			DecimalFormat df = new DecimalFormat("0.00");
			String message = "0400|" + BILLDESK_NMIMSID + "|" + transactionRefNo + "|" +  TxnDate + "|" + trackId + "|"+ df.format(Float.parseFloat(TxnAmount)) +"|"+ df.format(Float.parseFloat(refund_amount)) +"|" + formatter.format(date) + "|" + refundId + "|NA|NA|NA";
			String checkSum = HmacSHA256(message,BILLDESK_CHECKSUM_KEY);
			message = message + "|" + checkSum.toUpperCase();
			
			String postData = "msg="+message;
			
			URL obj = new URL(BILLDESK_REFUND_URL);
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

			//add reuqest header
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			
			// Send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(postData);
			wr.flush();
			wr.close();

			int responseCode = con.getResponseCode();
			

			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			
			//print result
			
			return response.toString();
		}
		catch (Exception e) {
			// TODO: handle exception
			//e.printStackTrace();
			return null;
		}
	}
	
	/*
	 * end of billdesk refund
	 * */
	
	
	/**
	 * refund transaction status check 
	 * */
	
	public JsonObject refundStatus(String tracking_id,String refId) {
		try {
			/* initialize an object */
			JSONObject paytmParams = new JSONObject();
	
			/* body parameters */
			JSONObject body = new JSONObject();
	
			/* Find your MID in your Paytm Dashboard at https://dashboard.paytm.com/next/apikeys */
			body.put("mid", PAYTM_MERCHANTMID);
	
			/* Enter your order id for which refund needs to be initiated */
			body.put("orderId", tracking_id);
	
			/* Enter refund id which was used for initiating refund */
			body.put("refId", refId);
	
			/**
			* Generate checksum by parameters we have in body
			* You can get Checksum JAR from https://developer.paytm.com/docs/checksum/
			* Find your Merchant Key in your Paytm Dashboard at https://dashboard.paytm.com/next/apikeys 
			*/
			String checksum = CheckSumServiceHelper.getCheckSumServiceHelper().genrateCheckSum(PAYTM_MERCHANTKEY, body.toString());
	
			/* head parameters */
			JSONObject head = new JSONObject();
	
			/* This is used when you have two different merchant keys. In case you have only one please put - C11 */
			head.put("clientId", "C11");
	
			/* put generated checksum value here */
			head.put("signature", checksum);
	
			/* prepare JSON string for request */
			paytmParams.put("body", body);
			paytmParams.put("head", head);
			String post_data = paytmParams.toString();
	
			URL url = new URL(TRANS_REFUND_STATUS_URL);
		
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setDoOutput(true);

			DataOutputStream requestWriter = new DataOutputStream(connection.getOutputStream());
			requestWriter.writeBytes(post_data);
			requestWriter.close();
			String responseData = "";
			InputStream is = connection.getInputStream();
			BufferedReader responseReader = new BufferedReader(new InputStreamReader(is));
			if ((responseData = responseReader.readLine()) != null) {
				System.out.append("Response: " + responseData);
			}
			// System.out.append("Request: " + post_data);
			responseReader.close();
			JsonParser parser = new JsonParser();
			JsonObject response = parser.parse(responseData).getAsJsonObject();
			return response;
		} catch (Exception exception) {
			//exception.printStackTrace();
			return null;
		}
		
	}
	
	/**
	 * End of paytm refund api
	 * */
	
	
	
	/**
	 * start of payu refund api
	 * */
	
	public JsonObject payuRefundInitiate(String trackId,String transaction_id,String refund_amount) {
		try {  
			String formaction = "cancel_refund_transaction";
			String refId = trackId + System.currentTimeMillis();	//unique refId for refund payTm
			String msg = PAYU_SECRET_KEY + "|"+ formaction +"|" + transaction_id + "|" + PAYU_SALT;
			
			String checkSum = generatePayuCheckSumSHA512(msg);
			String postData = "key="+ PAYU_SECRET_KEY + "&command="+ formaction +"&hash=" + checkSum + "&var1=" + transaction_id + "&var2=" + refId + "&var3=" + refund_amount;
			
			URL obj = new URL(PAYU_TRANS_STATUS_URL);
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

			//add reuqest header
			con.setRequestMethod("POST");

			
			// Send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(postData);
			wr.flush();
			wr.close();

			int responseCode = con.getResponseCode();
			

			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			
			//print result
			
			JsonParser parser = new JsonParser();
		    JsonObject responseData = parser.parse(response.toString()).getAsJsonObject();
		    responseData.addProperty("refId", refId);
			return responseData;
		}
		catch (Exception e) {
			// TODO: handle exception
			//e.printStackTrace();
			return null;
		}
	}
	 
	
	public JsonObject payuRefundStatus(String refundId) {
		try {  
			String formaction = "check_action_status";
			String msg = PAYU_SECRET_KEY + "|"+ formaction +"|" + refundId + "|" + PAYU_SALT;
			
			String checkSum = generatePayuCheckSumSHA512(msg);
			String postData = "key="+ PAYU_SECRET_KEY + "&command="+ formaction +"&hash=" + checkSum + "&var1=" + refundId;
			
			URL obj = new URL(PAYU_TRANS_STATUS_URL);
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

			//add reuqest header
			con.setRequestMethod("POST");

			
			// Send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(postData);
			wr.flush();
			wr.close();

			int responseCode = con.getResponseCode();
			

			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			
			//print result
			
			JsonParser parser = new JsonParser();
		    JsonObject responseData = parser.parse(response.toString()).getAsJsonObject();
			return responseData;
		}
		catch (Exception e) {
			// TODO: handle exception
			//e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * End of payu refund api
	 * */
	
	public void createResponseBean(HttpServletRequest request, ServiceRequestStudentPortal bean) {
		
		ServiceRequestStudentPortal sr = (ServiceRequestStudentPortal) request.getSession().getAttribute("sr");
		String sapid = (String) request.getSession().getAttribute("userId");
		String trackId = (String) request.getSession().getAttribute("trackId");
		String device = (String) request.getSession().getAttribute("device");
		
		bean.setId(sr.getId());
		bean.setServiceRequestType(sr.getServiceRequestType());
		bean.setProductType(sr.getProductType());
		bean.setSapId(sapid);
		bean.setTrackId(trackId);
		bean.setDevice(device);
		bean.setAmount(sr.getAmount());
		
		bean.setResponseMessage(request.getParameter("response_message"));
		bean.setTransactionID(request.getParameter("transaction_id"));
		bean.setRequestID(request.getParameter("request_id"));
		bean.setMerchantRefNo(request.getParameter("merchant_ref_no"));
		bean.setSecureHash(request.getParameter("secure_hash"));
		bean.setRespAmount(request.getParameter("response_amount"));
		bean.setRespTranDateTime(request.getParameter("response_transaction_date_time"));
		bean.setResponseCode(request.getParameter("response_code"));
		bean.setRespPaymentMethod(request.getParameter("response_payment_method"));
		bean.setPaymentID(request.getParameter("payment_id"));
		bean.setError(request.getParameter("error"));
		bean.setDescription(request.getParameter("description"));
		bean.setPaymentOption(request.getParameter("payment_option"));
		bean.setBankName(request.getParameter("bank_name"));
		
		request.getSession().setAttribute("paymentResponseBean",bean);
		
	}
	
	/**
	 * Returns Transaction status along with Payment related information, calls
	 * payment gateways transaction API for the same
	 * 
	 * @param trackId
	 * @return Transaction Status Bean
	 */
	public TransactionsBean getTransactionStatusGateway(String trackId) {

		TransactionsBean transactionsBean = new TransactionsBean();
		transactionsBean.setTrack_id(trackId);

		String url = SERVER_PATH + "paymentgateways/m/getTransactionStatus";
//		String url = "http://localhost:8888/" + "paymentgateways/m/getTransactionStatus";
		HttpHeaders headers = new HttpHeaders();

		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

		HttpEntity<TransactionsBean> entity = new HttpEntity<TransactionsBean>(transactionsBean, headers);

		return restTemplate.exchange(url, HttpMethod.POST, entity, TransactionsBean.class).getBody();
	}
	
}
