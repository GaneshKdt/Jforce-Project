package com.nmims.helpers;

import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream; 
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Map.Entry;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.controllers.BaseController;
import com.paytm.pg.merchant.CheckSumServiceHelper;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Enumeration;
import java.util.HashMap;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
//import com.nmims.daos.AssignmentsDAO;

@Service("paymentHelper")
public class PaymentHelper extends BaseController {
	
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
	
	@Value("${SECURE_SECRET}")
	private String SECURE_SECRET; 
	
	@Value("${ASSGN_PAYMENT_RETURN_URL}")
	private final String ASSIGNMENT_RETURN_URL = null;
	
	@Value( "${ASSGN_PAYMENT_RETURN_URL_MOBILE}" )
	private String ASSGN_PAYMENT_RETURN_URL_MOBILE = null;
	
	@Value("${RETURN_URL}")
	private final String EXAM_RETURN_URL = null;
	
	@Value("${RESIT_RETURN_URL}")
	private final String RESIT_RETURN_URL = null;
	
	private String RETURN_URL;
	
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
	
	/*private final String BILLDESK_TRANS_URL = "https://pgi.billdesk.com/pgidsk/PGIMerchantPayment";
	private final String BILLDESK_TRANS_STATUS_URL = "https://www.billdesk.com/pgidsk/PGIQueryController";
	private final String BILLDESK_NMIMSID = "NMIMS";
	private final String BILLDESK_SECURITY_ID = "eSTTsLAjCzks";*/
	
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
	
	/*public PaymentHelper(String modelType){
		if(modelType.equalsIgnoreCase("exam_registration")) {
			this.RETURN_URL = this.EXAM_RETURN_URL;
		}
		else if(modelType.equalsIgnoreCase("assignment")) {
			this.RETURN_URL = this.ASSIGNMENT_RETURN_URL;
		}
	}*/
	
	private static final Logger logger = LoggerFactory.getLogger("examBookingPayments");

	
	public void setPaymentReturnUrl(String modelType){
		if(modelType.equalsIgnoreCase("exam_registration")) {
			this.RETURN_URL = this.EXAM_RETURN_URL;
		}
		else if(modelType.equalsIgnoreCase("assignment")) {
			this.RETURN_URL = this.ASSIGNMENT_RETURN_URL;
		}else if(modelType.equalsIgnoreCase("assignment_mobile")) {
			this.RETURN_URL = this.ASSGN_PAYMENT_RETURN_URL_MOBILE;
		}
	}
	
	protected boolean isHashMatching(HttpServletRequest request) {
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

	protected boolean isAmountMatching(HttpServletRequest request, String totalFees) {
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

	protected boolean isTrackIdMatching(HttpServletRequest request, String trackId) {
		if(trackId != null && trackId.equals(request.getParameter("MerchantRefNo"))){
			return true;
		}else{
			return false;
		}
	}
	
	protected String md5(String str) throws Exception {
		MessageDigest m = MessageDigest.getInstance("MD5");

		byte[] data = str.getBytes();

		m.update(data,0,data.length);

		BigInteger i = new BigInteger(1,m.digest());

		String hash = String.format("%1$032X", i);

		return hash;
	}
	
	protected boolean isTransactionSuccessful(HttpServletRequest request) {
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
	//billdesk checksum
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
	//billdesk checksum
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
			
			return null;
		}
	}
	
	
	
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
	
	
	public String generatePayuCheckSum(HttpServletRequest request,StudentExamBean student, int totalFees, String trackId, String message) {
		try {
			String data = PAYU_SECRET_KEY + "|" + trackId + "|" + String.valueOf(totalFees) + "|" + message + "|" + student.getFirstName() + "|" + student.getEmailId() + "|||||||||||" + PAYU_SALT;
			String checksum = generatePayuCheckSumSHA512(data);
			request.getSession().setAttribute("SECURE_SECRET", checksum);
			logger.info(trackId + " " + student.getSapid()  + " PayU Checksum Generation Success " + checksum);

			return "true";
		}
		catch (Exception e) {
			logger.info(trackId + " " + student.getSapid()  + " PayU Checksum Generation Error " + e.getMessage());

			// TODO: handle exception
			return (String) e.getMessage();
		}
	}
	
	public String generateCheckSum(HttpServletRequest request,StudentExamBean student, int totalFees, String trackId, String message) {
		try {
			
			TreeMap<String, String> paytmParams = new TreeMap<String, String>();
			paytmParams.put("MID",PAYTM_MERCHANTMID);
			paytmParams.put("ORDER_ID",trackId);
			paytmParams.put("CHANNEL_ID",PAYTM_CHANNEL_ID);
			paytmParams.put("CUST_ID",student.getSapid());
			paytmParams.put("MOBILE_NO",student.getMobile());
			paytmParams.put("EMAIL",student.getEmailId());
			paytmParams.put("TXN_AMOUNT",String.valueOf(totalFees));
			paytmParams.put("WEBSITE",PAYTM_WEBSITE);
			paytmParams.put("INDUSTRY_TYPE_ID",PAYTM_INDUSTRY_TYPE_ID);
			paytmParams.put("CALLBACK_URL", RETURN_URL);
			paytmParams.put("MERC_UNQ_REF",message);
			String checkSum =  CheckSumServiceHelper.getCheckSumServiceHelper().genrateCheckSum(PAYTM_MERCHANTKEY, paytmParams);
			request.getSession().setAttribute("SECURE_SECRET", checkSum);
			logger.info(trackId + " " + student.getSapid()  + " PayTm Checksum Generation Success " + checkSum);

			return "true";
		}
		catch(Exception e) {
			logger.info(trackId + " " + student.getSapid()  + " PayTm Checksum Generation Error " + e.getMessage());

			return (String) e.getMessage();
			
		}
	}
	
	//generate checksum billdesk
	
	public String generateBillDeskCheckSum(HttpServletRequest request,StudentExamBean student, int totalFees, String trackId, String message) {
		try {
			String body = BILLDESK_NMIMSID + "|" + trackId + "|NA|" + totalFees + "|NA|NA|NA|INR|NA|R|" + BILLDESK_SECURITY_ID + "|NA|NA|F|NA|NA|"+ message +"|NA|NA|NA|NA|" + RETURN_URL;
			String checkSum = HmacSHA256(body,BILLDESK_CHECKSUM_KEY);
			request.getSession().setAttribute("SECURE_SECRET", checkSum);
			logger.info(trackId + " " + student.getSapid()  + " BillDesk Checksum Generation Success " + checkSum);

			
			return "true";
		}
		catch (Exception e) {
			// TODO: handle exception
			
			logger.info(trackId + " " + student.getSapid()  + " BillDesk Checksum Generation Error " + e.getMessage());

			return (String) e.getMessage();
		}
	}
	
	
	public ModelAndView setModelData(ModelAndView mv,HttpServletRequest request,StudentExamBean student, int totalFees, String trackId, String message) {
		mv.addObject("TRANS_URL", TRANS_URL);
		mv.addObject("WEBSITE", PAYTM_WEBSITE);
		mv.addObject("ORDER_ID",trackId);
		mv.addObject("CUST_ID", student.getSapid());
		mv.addObject("MOBILE_NO",student.getMobile());
		mv.addObject("EMAIL",student.getEmailId());
		mv.addObject("INDUSTRY_TYPE_ID",PAYTM_INDUSTRY_TYPE_ID);
		mv.addObject("CHANNEL_ID", PAYTM_CHANNEL_ID);
		mv.addObject("TXN_AMOUNT", String.valueOf(totalFees));
		mv.addObject("CALLBACK_URL",RETURN_URL);
		mv.addObject("CHECKSUMHASH", request.getSession().getAttribute("SECURE_SECRET"));
		mv.addObject("MERCHANTMID",PAYTM_MERCHANTMID);
		mv.addObject("MERC_UNQ_REF",message);
		
		logger.info(trackId + " " + student.getSapid()  + " PayTM Model Data " + mv.getModel());

		return mv;
	}
	
	public ModelAndView setPayuModelData(ModelAndView mv,HttpServletRequest request,StudentExamBean student, int totalFees, String trackId, String message) {
		mv.addObject("trans_url", PAYU_TRANS_URL);
		mv.addObject("key", PAYU_SECRET_KEY);
		mv.addObject("txnid", trackId);
		mv.addObject("amount", totalFees);
		mv.addObject("productinfo", message);
		mv.addObject("firstname", student.getFirstName());
		mv.addObject("email", student.getEmailId());
		mv.addObject("phone", student.getMobile());
		mv.addObject("surl", RETURN_URL);
		mv.addObject("furl", RETURN_URL);
		mv.addObject("hash", request.getSession().getAttribute("SECURE_SECRET"));
		
		logger.info(trackId + " " + student.getSapid()  + " PayU Model Data " + mv.getModel());
		return mv;
	}
	
	//billdesk setModel
	public ModelAndView setBillDeskModelData(ModelAndView mv,HttpServletRequest request,StudentExamBean student, int totalFees, String trackId, String message) {
		String body = BILLDESK_NMIMSID + "|" + trackId + "|NA|" + totalFees + "|NA|NA|NA|INR|NA|R|" + BILLDESK_SECURITY_ID + "|NA|NA|F|NA|NA|"+ message +"|NA|NA|NA|NA|" + RETURN_URL + "|" + request.getSession().getAttribute("SECURE_SECRET");
		mv.addObject("trans_url", BILLDESK_TRANS_URL);
		mv.addObject("msg", body);
		
		logger.info(trackId + " " + student.getSapid()  + " BillDesk Model Data " + mv.getModel());

		
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
			
			return false;
		}
	}
	//billdesk checksum verify
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
			// TODO: handle exception
			
			return false;
		}
	}
	
	public ExamBookingTransactionBean createPaytmResponseBean(HttpServletRequest request) {
		ExamBookingTransactionBean bean = new ExamBookingTransactionBean();
		
		//ExamBookingTransactionBean sr = (ExamBookingTransactionBean) request.getSession().getAttribute("sr");
		String sapid = (String) request.getSession().getAttribute("userId");
		String trackId = (String) request.getSession().getAttribute("trackId");
		
		bean.setSapid(sapid);
		bean.setTrackId(trackId);
		
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
		if(this.RETURN_URL.equalsIgnoreCase(this.EXAM_RETURN_URL)) {
			bean.setDescription("Exam fees for " + sapid);
		}else if(this.RETURN_URL.equalsIgnoreCase(this.ASSIGNMENT_RETURN_URL)){
			bean.setDescription("Assignment fees for " + sapid);
		}else if(this.RETURN_URL.equalsIgnoreCase(this.ASSGN_PAYMENT_RETURN_URL_MOBILE)){
			bean.setDescription("Assignment fees for " + sapid);
		}
		
		logger.info(" paytmResponseBean sapid "+sapid+" trackId "+trackId+" bean "+bean.toString());
		request.getSession().setAttribute("paymentResponseBean",bean);
		return bean;
	}
	
	
	public ExamBookingTransactionBean createPayuResponseBean(HttpServletRequest request) {
		ExamBookingTransactionBean bean = new ExamBookingTransactionBean();
		
		String sapid = (String) request.getSession().getAttribute("userId");
		String trackId = (String) request.getSession().getAttribute("trackId");
		
		bean.setSapid(sapid);
		bean.setTrackId(trackId);
		
		bean.setPaymentOption("payu");
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
		bean.setDescription( request.getParameter("productinfo"));
		logger.info(" PayuResponseBean sapid "+sapid+" trackId "+trackId+" bean "+bean.toString());
		request.getSession().setAttribute("paymentResponseBean",bean);
		return bean;
	}
	
	//billdesk
	public ExamBookingTransactionBean createBillDeskResponseBean(HttpServletRequest request) {
		ExamBookingTransactionBean bean = new ExamBookingTransactionBean();
		
		//ExamBookingTransactionBean sr = (ExamBookingTransactionBean) request.getSession().getAttribute("sr");
		String sapid = (String) request.getSession().getAttribute("userId");
		String trackId = (String) request.getSession().getAttribute("trackId");
		logger.info(" billdesk sapid "+sapid+" trackId "+trackId);
		bean.setSapid(sapid);
		bean.setTrackId(trackId);
		String[] responseList = request.getParameter("msg").split("\\|");
		logger.info(" billdesk msg "+request.getParameter("msg"));
		String responseDateTime = null;
		DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date date = format.parse(responseList[13]);
			responseDateTime = sf.format(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			
		}
		bean.setPaymentOption("billdesk");
		bean.setTransactionID(responseList[2]);
//		bean.setMerchantRefNo(responseList[1]);
		bean.setMerchantRefNo(responseList[3]);
		bean.setSecureHash(responseList[25]);
		bean.setRespAmount(responseList[4]);
		bean.setRespTranDateTime(responseDateTime);
		bean.setResponseCode(responseList[14]);
		if("0300".equalsIgnoreCase(bean.getResponseCode())) {
			bean.setResponseMessage("Success");
		}else {
			bean.setResponseMessage(responseList[24]);
		}
		bean.setRespPaymentMethod(responseList[5]);
		bean.setPaymentID(responseList[6]);
		bean.setBankName(responseList[5]);
		bean.setDescription("Exam fees for " + sapid);
		request.getSession().setAttribute("paymentResponseBean",bean);
		return bean;
	}
	
	public ExamBookingTransactionBean createHdfcResponseBean(HttpServletRequest request) {
		ExamBookingTransactionBean bean = new ExamBookingTransactionBean();
		//ServiceRequest sr = (ServiceRequest) request.getSession().getAttribute("sr");
		String sapid = (String) request.getSession().getAttribute("userId");
		String trackId = (String) request.getSession().getAttribute("trackId");
		
		bean.setSapid(sapid);
		bean.setTrackId(trackId);
		
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
		if(this.RETURN_URL.equalsIgnoreCase(this.EXAM_RETURN_URL)) {
			bean.setDescription("Exam fees for " + sapid);
		}else if(this.RETURN_URL.equalsIgnoreCase(this.ASSIGNMENT_RETURN_URL)){
			bean.setDescription("Assignment fees for " + sapid);
		}else if(this.RETURN_URL.equalsIgnoreCase(this.ASSGN_PAYMENT_RETURN_URL_MOBILE)){
			bean.setDescription("Assignment fees for " + sapid);
		}
		request.getSession().setAttribute("paymentResponseBean",bean);
		return bean;
	}
	
	
	public ExamBookingTransactionBean CreateResponseBean(HttpServletRequest request) {
		String paymentOption = (String) request.getSession().getAttribute("paymentOption");
		if("paytm".equalsIgnoreCase(paymentOption)) {
			return this.createPaytmResponseBean(request);
		}
		else if("hdfc".equalsIgnoreCase(paymentOption)) {
			return this.createHdfcResponseBean(request);
		}
		else if("billdesk".equalsIgnoreCase(paymentOption)) {
			return this.createBillDeskResponseBean(request);
		}
		else if("payu".equalsIgnoreCase(paymentOption)) {
			return this.createPayuResponseBean(request);
		}
		return null;
	}
	
	
	public String checkErrorInHdfcPayment(HttpServletRequest request) {
		
		String errorMessage = null;
		String trackId = (String) request.getSession().getAttribute("trackId");
		String amount = (String) request.getSession().getAttribute("totalFees");
		String typeOfPayment = (String)request.getParameter("PaymentMethod");
		boolean isHashMatching = isHashMatching(request);
		boolean isTrackIdMatching = isTrackIdMatching(request, trackId);
		boolean isAmountMatching = isAmountMatching(request, amount);
		boolean isSuccessful = isTransactionSuccessful(request);
		
		/*if(!"Wallet".equals(typeOfPayment)){
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
	
	
	public String checkErrorInPaytmPayment(HttpServletRequest request) {
		String errorMessage = null;
		String amount = (String) request.getSession().getAttribute("totalFees");
		String trackId = (String) request.getSession().getAttribute("trackId");
		try {
			if(!trackId.equals(request.getParameter("ORDERID"))) {
				return "Error in processing payment. Error: Mismatch in trackId";
			}
			//isSuccess
			if(!"Txn Success".equalsIgnoreCase(request.getParameter("RESPMSG"))) {
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
			
			return "Error in processing payment. Error: " + e.getMessage();
		}
	}
	
	
	
	/*public String mcheckErrorInPaytmPayment(ExamBookingTransactionBean bean) {
		String errorMessage = null;
		String amount = bean.getRequestAmount();
		String trackId = bean.getTrackId();

		try {
			//isSuccess
			if(!"Txn Success".equalsIgnoreCase(bean.getTranStatus())) {
				return "Error in processing payment. Error:  " + bean.getErrorMessage() + " Code: " + bean.getErrorCode();
			}
			boolean verificationCheckSum = verificationCheckSum(request);
			
			if(verificationCheckSum) {
				if(Float.parseFloat(request.getParameter("TXNAMOUNT")) != Float.parseFloat(amount)) {
					errorMessage = "Error in processing payment. Error: Fees " + amount + " not matching with amount paid ";
				}
				
				return errorMessage;
			}
			else {
				return "Error in processing payment. Error: Hashvalue not matching. Tampering in response found. Track ID: " + trackId;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
			return "Error in processing payment. Error: " + e.getMessage();
		}
	}*/
	
	
	
	
	
	public String checkErrorInPayuPayment(HttpServletRequest request) {
		String errorMessage = null;
		String amount = (String) request.getSession().getAttribute("totalFees");
		String trackId = (String) request.getSession().getAttribute("trackId");
		try {
			if(!trackId.equals(request.getParameter("txnid"))) {
				return "Error in processing payment. Error: Mismatch in trackId";
			}
			//isSuccess
			if(!"success".equalsIgnoreCase(request.getParameter("status"))) {
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
			
			return "Error in processing payment. Error: " + e.getMessage();
		}
	}
	
	//billdesk payment status
	public String checkErrorInBillDeskPayment(HttpServletRequest request) {
		String errorMessage = null;
		String amount = (String) request.getSession().getAttribute("totalFees");
		String trackId = (String) request.getSession().getAttribute("trackId");
		try {
			String[] responseList = request.getParameter("msg").split("\\|");
			if(!trackId.equals(responseList[1])) {
				return "Error in processing payment. Error: Mismatch in trackId";
			}
			if(!"0300".equalsIgnoreCase(responseList[14])) {
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
		}
		catch (Exception e) {
			// TODO: handle exception
			
			return "Error in processing payment. Error: " + e.getMessage();
		}
	}
	
	
	
	
	/**
	 * Common function to Check Any Error During Transaction
	 * */
	public String checkErrorInPayment(HttpServletRequest request) {
		String paymentOption = (String) request.getSession().getAttribute("paymentOption");
		if("paytm".equalsIgnoreCase(paymentOption)) {
			return this.checkErrorInPaytmPayment(request);
		}
		else if("billdesk".equalsIgnoreCase(paymentOption)) {
			return this.checkErrorInBillDeskPayment(request);
		}
		else if("hdfc".equalsIgnoreCase(paymentOption)) {
			return this.checkErrorInHdfcPayment(request);
		}
		else if("payu".equalsIgnoreCase(paymentOption)) {
			return this.checkErrorInPayuPayment(request);
		}
		return "invalid paymentGateway found";
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
		    }
		    JsonParser parser = new JsonParser();
		    JsonObject response = parser.parse(responseData).getAsJsonObject();
		    return response;
		} catch (Exception exception) {

			logger.info(""
				+ " doAutoBookingForConflictTransactions paytm null object returned from API : "
				+ " trackID " + trackId
				+ " exception " + exception.getMessage()
			);
		    return null;
		}
		
	}
	
	//billdesk
	public String getBillDeskTransactionStatus(String trackId) {
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
			Date date = new Date();
			String msg = "0122|"+ BILLDESK_NMIMSID +"|" + trackId + "|" + formatter.format(date);
			String checkSum = HmacSHA256(msg,BILLDESK_CHECKSUM_KEY);
			msg = msg + "|" + checkSum.toUpperCase();
			String postData = "msg="+msg;
			URL obj = new URL(BILLDESK_TRANS_STATUS_URL);
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
			
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
			
			return null;
		}
	}
	
	
	public String generateCommonCheckSum(HttpServletRequest request,StudentExamBean student,int totalFees,String  trackId,String message,String paymentOption) {
		if("payu".equalsIgnoreCase(paymentOption)) {
			return generatePayuCheckSum(request, student, totalFees, trackId, message);
		}
		else if("billdesk".equalsIgnoreCase(paymentOption)) {
			return generateBillDeskCheckSum(request, student, totalFees, trackId, message);
		}
		else if("paytm".equalsIgnoreCase(paymentOption)) {
			return generateCheckSum(request, student, totalFees, trackId, message);
		}
		return null;
	}
	
	public ModelAndView setCommonModelData(ModelAndView mv,HttpServletRequest request,StudentExamBean student,int totalFees,String trackId,String message,String paymentOption) {
		if("payu".equalsIgnoreCase(paymentOption)) {
			return setPayuModelData(mv, request, student, totalFees, trackId, message);
		}
		else if("billdesk".equalsIgnoreCase(paymentOption)) {
			return setBillDeskModelData(mv, request, student, totalFees, trackId, message);
		}
		else if("paytm".equalsIgnoreCase(paymentOption)) {
			return setModelData(mv, request, student, totalFees, trackId, message);
		} 
		return mv;
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
	        }
	        responseReader.close();
	        JsonParser parser = new JsonParser();
		    JsonObject response = parser.parse(responseData).getAsJsonObject();
		    return response;
        } catch (Exception exception) {
        	return null;
        }
	}
	
	
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
			}
			responseReader.close();
			JsonParser parser = new JsonParser();
			JsonObject response = parser.parse(responseData).getAsJsonObject();
			return response;
		} catch (Exception exception) {
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
			
			return null;
		}
	}
	
	
	/**
	 * End of payu refund api
	 * */
}
