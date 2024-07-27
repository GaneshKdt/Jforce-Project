package com.nmims.paymentgateways.helper;



import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import com.ccavenue.security.AesCryptUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nmims.paymentgateways.bean.TransactionStatusBean;
import com.nmims.paymentgateways.bean.TransactionsBean;
import com.nmims.paymentgateways.interfaces.PaymentInterface;


@Component
public class CCAvenueHelper  implements PaymentInterface {
	

	@Value("${CCAVENUE_MERCHANTID}")
	private String CCAVENUE_MERCHANTID;
	
	@Value("${CCAVENUE_ACCESS_CODE}")
	private String CCAVENUE_ACCESS_CODE;
	
	@Value("${CCAVENUE_WORKING_KEY}")
	private String CCAVENUE_WORKING_KEY;
	
	@Value("${PAYMENT_GATEWAY_RETURN_URL}")
	private String CCAVENUE_RETURN_URL;
	
	@Value("${CCAVENUE_URL}")
	private String CCAVENUE_URL;
	
	@Value("${PAYMENT_GATEWAY_CANCEL_URL}")
	private String CANCEL_URL;
	
	@Value("${CCAVENUE_API_URL}")
	private String CCAVENUE_API_URL;

	
	
	private static final String language="EN";
   
	public static ExceptionHelper exceptionHelper = new ExceptionHelper();
	
	private static final Logger ccAvenue_Logger = LoggerFactory.getLogger("ccavenue_logger");
	
	private static final DecimalFormat df = new DecimalFormat("0.0");
	

    
	@Override
	public String generateCheckSum(TransactionsBean transactionsBean, String returnUrl) {
		try {
			ccAvenue_Logger.info("Creating Checksum for"+" "+transactionsBean.getTrack_id());
		
			Float amt= Float.parseFloat(transactionsBean.getAmount());
			df.setMaximumFractionDigits(2);
			transactionsBean.setAmount(df.format(amt));
			
			// creating request parameter
			String requestParameters="merchant_id"+"="+CCAVENUE_MERCHANTID+"&"+"order_id"+"="+transactionsBean.getTrack_id()+"&"+"currency"+"="+"USD"+"&"+"amount"+"="+transactionsBean.getAmount()+"&"+"redirect_url"+"="+CCAVENUE_RETURN_URL+"&"+"cancel_url"+"="+CANCEL_URL+"&"+"language"+"="+language+"&"+"merchant_param1"+"="+transactionsBean.getDescription();
		
			
			AesCryptUtil aesUtil=new AesCryptUtil(CCAVENUE_WORKING_KEY);
			String encRequest = aesUtil.encrypt(requestParameters); // encrypting the request parameter using AES algorith provided by CC_Avenue
			ccAvenue_Logger.info(" Checksum  created for CC Avenue"+" "+ encRequest);
			
			transactionsBean.setSecure_hash(encRequest);
			System.out.println("checkSum Code"+ " "+ encRequest);
			return "true";
		}
		catch(Exception e) {
			exceptionHelper.createLog(e);
			ccAvenue_Logger.info(" Error in Checksum for CC Avenue"+" "+e);
			return (String) e.getMessage();
		}
	}

	@Override
	public ModelAndView createModelAndViewData(TransactionsBean transactionsBean, String returnUrl) {
		ModelAndView modelAndView = new ModelAndView("ccAvenue");
		try {
			// creating ModelAndView for CC Avenue initiate payment
				ccAvenue_Logger.info("Creating ModelAndView for"+" "+transactionsBean.getTrack_id());		
				modelAndView.addObject("transaction_url", CCAVENUE_URL);
				modelAndView.addObject("encRequest", transactionsBean.getSecure_hash());
				modelAndView.addObject("access_code", CCAVENUE_ACCESS_CODE);
				return modelAndView;
		}
		catch (Exception e) {
			ccAvenue_Logger.info("Error in creating ModelAndView"+" "+ e);
			return modelAndView;
		}
	}

	@Override
	public boolean verifyCheckSum(HttpServletRequest request) {
	try {
			TransactionsBean transactionsBean_session = (TransactionsBean) request.getSession().getAttribute("transactionsBean");
			ccAvenue_Logger.info("START CHECKSUM VERIFY "+" "+transactionsBean_session.getTrack_id());
			
			// checking the equality, the encrypt code generated while sending the request and after the getting is equal or not to verify checksum
			HashMap<String,String> checksum= new HashMap<String, String>();
			String encResp= request.getParameter("encResp");
			checksum=getResponseDecrypt(encResp);
			String checksumValue="merchant_id"+"="+CCAVENUE_MERCHANTID+"&"+"order_id"+"="+checksum.get("order_id")+"&"+"currency"+"="+"USD"+"&"+"amount"+"="+checksum.get("amount")+"&"+"redirect_url"+"="+CCAVENUE_RETURN_URL+"&"+"cancel_url"+"="+CANCEL_URL+"&"+"language"+"="+language+"&"+"merchant_param1"+"="+checksum.get("merchant_param1");
			AesCryptUtil aesUtil=new AesCryptUtil(CCAVENUE_WORKING_KEY);
			String encRequest = aesUtil.encrypt(checksumValue);
			
			ccAvenue_Logger.info("For TrackID"+" "+transactionsBean_session.getTrack_id()+" \n"+ transactionsBean_session.getSecure_hash()+"\n"+encRequest);
			if (transactionsBean_session.getSecure_hash().equalsIgnoreCase(encRequest)) {
				ccAvenue_Logger.info(" CHECKSUM VERIFIED "+" "+transactionsBean_session.getTrack_id());
				transactionsBean_session.setSecure_hash(encRequest);
				return true;
			} else {
				return false;
			}
		}
		catch (Exception e) {
			ccAvenue_Logger.info(" ERROR IN CHECKSUM VERIFY"+" "+ e);
			exceptionHelper.createLog(e);
			return false;
		}
	}

	@Override
	public TransactionsBean createResponseBean(HttpServletRequest request) {
		TransactionsBean session_transactionsBean = (TransactionsBean) request.getSession().getAttribute("transactionsBean");
		try {
			// getting the data in decrypt form
			String encResp= request.getParameter("encResp");
					HashMap<String,String> map=getResponseDecrypt(encResp);
					ccAvenue_Logger.info(" RESPONSE  CRAEATED FOR "+" "+ session_transactionsBean.getTrack_id()+": "+map);
				
					session_transactionsBean.setPayment_id(map.get("bank_ref_no"));
					//session_transactionsBean.setBank_name(request.getParameter("bankcode"));
					session_transactionsBean.setMerchant_ref_no(map.get("order_id"));		
					session_transactionsBean.setResponse_message(map.get("status_message"));
					session_transactionsBean.setResponse_code(map.get("status_code"));
					session_transactionsBean.setResponse_transaction_date_time(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
					session_transactionsBean.setResponse_amount(map.get("amount"));
					session_transactionsBean.setResponse_payment_method(map.get("payment_mode"));
					session_transactionsBean.setTransaction_id(map.get("tracking_id"));
					session_transactionsBean.setError(map.get("status_message"));
					session_transactionsBean.setTrack_id(map.get("order_id"));
								
				return session_transactionsBean;
			}
			catch(Exception e) {
				e.printStackTrace();
				ccAvenue_Logger.info(" ERROR IN CREATING RESPONSE FOR CC_AVENUE"+" "+session_transactionsBean.getTrack_id());
				session_transactionsBean.setError("Error Creating Response");
				return session_transactionsBean;
			}

	}
	

	@Override
	public TransactionsBean getTransactionStatus(TransactionsBean transactionsBean, List<String> paymentStatus) {
		try {			
				ccAvenue_Logger.info("Transaction Status for"+ " " + transactionsBean.getTrack_id());
				JsonObject json = new JsonObject();
				json.addProperty("order_no", transactionsBean.getTrack_id());
	
				AesCryptUtil aesUtil = new AesCryptUtil(CCAVENUE_WORKING_KEY);
				String encRequest = aesUtil.encrypt(json.toString());
				String requestParameters = "enc_request=" + encRequest + "&access_code=" + CCAVENUE_ACCESS_CODE+"&command=orderStatusTracker&request_type=JSON&response_type=JSON&version=1.1";
		
				RestTemplate restTemplate = new RestTemplate();
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
				HttpEntity<String> entity = new HttpEntity<String>(requestParameters, headers);
				String responseEntity = restTemplate.exchange(CCAVENUE_API_URL, HttpMethod.POST, entity, String.class).getBody();
	 
				ccAvenue_Logger.info("Status" +transactionsBean.getTrack_id() +" " +responseEntity);
		
				Map<String, String> responseData = getResponseStatus(responseEntity);

				ccAvenue_Logger.info("Status"+transactionsBean.getTrack_id() +" " + responseData);
		
				String STATUS =responseData.get("status");
//				System.out.println("status"+STATUS);
				if ("0".equals(STATUS)) {			
			   		if(!responseData.isEmpty()) {	
						if ("Shipped".equalsIgnoreCase(responseData.get("order_status"))) {
							ccAvenue_Logger.info("Status Success for"+" "+transactionsBean.getTrack_id()) ;
							transactionsBean.setTransaction_status(paymentStatus.get(1));
							transactionsBean.setTransaction_id(responseData.get("reference_no"));
							transactionsBean.setMerchant_ref_no(responseData.get("order_no"));
							transactionsBean.setResponse_code(responseData.get("status"));
							transactionsBean.setResponse_message(responseData.get("order_status"));
							transactionsBean.setResponse_amount(responseData.get("order_amt"));
							transactionsBean.setResponse_payment_method(responseData.get("order_option_type"));
							transactionsBean.setPayment_id(responseData.get("order_bank_ref_no"));
							transactionsBean.setUpdated_at(responseData.get("order_date_time"));
							transactionsBean.setType(responseData.get("order_option_type"));

						} else if ("Awaited".equalsIgnoreCase(responseData.get("order_status"))) {
							ccAvenue_Logger.info("Status Success for"+" "+transactionsBean.getTrack_id()) ;
							transactionsBean.setError("Invalid conditional status: " + responseData.get("order_bank_response "));
							transactionsBean.setTransaction_status(paymentStatus.get(3));
							
						} else if ("Intiated".equalsIgnoreCase(responseData.get("order_status"))) {
							ccAvenue_Logger.info("Status Initiated for"+" "+transactionsBean.getTrack_id()) ;
							transactionsBean.setError("Invalid conditional status: " + responseData.get("order_bank_response "));
							transactionsBean.setTransaction_status(paymentStatus.get(0));
							
						} else {
							ccAvenue_Logger.info("Status  for"+responseData.get("order_status")+" "+transactionsBean.getTrack_id());
							transactionsBean.setError("Invalid conditional status: " + responseData.get("error_desc"));
							transactionsBean.setTransaction_status(paymentStatus.get(2));
						}
					}
				} else {
					ccAvenue_Logger.info("Invalid conditional status: " + responseData.get("enc_response"));
					transactionsBean.setError("Invalid conditional status: " + responseData.get("enc_response"));
					transactionsBean.setTransaction_status(paymentStatus.get(3));
					
			}
		return transactionsBean;
		}
		catch(Exception e) {
			e.printStackTrace();
			ccAvenue_Logger.info("Status Error"+transactionsBean.getTrack_id() +" " + e);
			transactionsBean.setError("Invalid conditional status: Please Try Again");
			transactionsBean.setTransaction_status(paymentStatus.get(0));
			return transactionsBean;
		}
	
	}

	@Override
	public String checkErrorInTransaction(HttpServletRequest request) {
		try {
			TransactionsBean transactionsBean_session = (TransactionsBean) request.getSession().getAttribute("transactionsBean");
			String encResp= request.getParameter("encResp");
			HashMap<String,String> map =getResponseDecrypt(encResp);
			if(transactionsBean_session.getTrack_id() == null) {
				ccAvenue_Logger.info("Track ID NULL"+transactionsBean_session.getTrack_id());
				return "Error in processing payment. Error: TrackId session expired";
			}
			
			if(!transactionsBean_session.getTrack_id().equals(map.get("order_id"))) {
				ccAvenue_Logger.info("track id mismatch"+" "+transactionsBean_session.getTrack_id()) ;
				return "Error in processing payment. Error: TrackId not matching with transaction Id";
			}
			
			//isSuccess
			if(!"Success".equalsIgnoreCase(map.get("order_status"))) {
				ccAvenue_Logger.info("Status Response is Not Success for"+" "+transactionsBean_session.getTrack_id());
				return "Error in processing payment. Error:  " + map.get("order_status");
			}
			
			//checksum verify
			boolean verificationCheckSum = verifyCheckSum(request);
			if(!verificationCheckSum) {
				ccAvenue_Logger.info("Error In Checksum Verification"+transactionsBean_session.getTrack_id());
				exceptionHelper.createInfoLog("Tampering in response found. Track ID: " + transactionsBean_session.getTrack_id());
				return "Error in processing payment. Error: Hashvalue not matching. Tampering in response found. Track ID: " + transactionsBean_session.getTrack_id();
			}
			
			//amount check
			if(Float.parseFloat(transactionsBean_session.getAmount()) != Float.parseFloat(map.get("amount"))) {
			
				ccAvenue_Logger.info("Amount Mismatch for"+" "+transactionsBean_session.getTrack_id());
				exceptionHelper.createInfoLog("Fees " + transactionsBean_session.getAmount() + " not matching with amount paid ");
				return "Error in processing payment. Error: Fees " + transactionsBean_session.getResponse_amount() + " not matching with amount paid ";
			}
			return "true";
			
		}
		catch (Exception e) {
			exceptionHelper.createLog(e);
			System.out.println("step"+ e.getMessage());
			return "Error in processing payment. Error: " + e.getMessage();
		}
	}

	@Override
	public JsonObject refundInitiate(String tracking_id, String transaction_id, String refund_amount) {
	return null;				
	}

	@Override
	public JsonObject refundStatus(String tracking_id, String refId) {
		JsonObject resData = new JsonObject();
		ObjectMapper mapper=new ObjectMapper();
		ccAvenue_Logger.info("Refund Status for "+" "+ tracking_id);
		try {
			JsonObject json = new JsonObject();
			json.addProperty("reference_no", refId);
			json.addProperty("order_id", tracking_id);

			AesCryptUtil aesUtil = new AesCryptUtil(CCAVENUE_WORKING_KEY);
			String encRequest = aesUtil.encrypt(json.toString());
			ccAvenue_Logger.info("Encrypt Data for "+" "+ tracking_id+" "+encRequest);
			String requestParameters = "enc_request=" + encRequest + "&access_code="+CCAVENUE_ACCESS_CODE+"&command=orderStatusTracker&request_type=JSON&response_type=JSON&version=1.1";
			ccAvenue_Logger.info("Request URLfor"+" "+ tracking_id+" "+requestParameters);
			
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			HttpEntity<String> entity = new HttpEntity<String>(requestParameters, headers);
			String responseEntity = restTemplate.exchange(CCAVENUE_API_URL, HttpMethod.POST, entity, String.class).getBody();
			
			ccAvenue_Logger.info("Response URL for"+" "+ tracking_id+" "+responseEntity);
		
			Map<String, String> responseData = getResponseStatus(responseEntity);
	
			ccAvenue_Logger.info("Response Data for"+" "+ tracking_id+" "+responseData);
			
			String STATUS = responseData.get("status");
			
			if ("0".equals(STATUS)) {
				String mapData=mapper.writeValueAsString(responseData);
				ccAvenue_Logger.info("Hashmap to String"+" "+ tracking_id+" "+mapData);
				JsonParser parser = new JsonParser();
				resData = (JsonObject) parser.parse(mapData);
				ccAvenue_Logger.info("String to Json"+" "+ tracking_id+" "+responseData);
				
			} else {
				ccAvenue_Logger.info("Error IN Status API for"+" "+ tracking_id+" "+responseData.get("enc_response"));
				resData.addProperty("error", responseData.get("enc_response"));
			}
			
			return resData;
		} catch (Exception e) {
			e.printStackTrace();
			ccAvenue_Logger.info("Error found for"+" "+ tracking_id+" "+e);
			resData.addProperty("error", "Error in Initiating");
			return resData;
		}

	}
	
	public TransactionStatusBean refundIntitate(String track_id,String transaction_id,String amount) {
		TransactionStatusBean bean=new TransactionStatusBean();
		try {
			ccAvenue_Logger.info("Refund Initiate start for "+" "+ track_id);
			System.out.println("transaction_id"+transaction_id);
			String refId = track_id + System.currentTimeMillis();
			HashMap<String, String> map = new HashMap<>();
			map.put("reference_no", transaction_id);
			map.put("refund_amount", amount);
			map.put("refund_ref_no", refId);
			JSONObject jsonMap = new JSONObject(map);
			ccAvenue_Logger.info("Request Json Data for"+" "+ track_id + " "+jsonMap);

			AesCryptUtil aesUtil = new AesCryptUtil(CCAVENUE_WORKING_KEY);
			String encRequest = aesUtil.encrypt(jsonMap.toString());
			String requestParameters = "enc_request=" + encRequest + "&access_code=" +CCAVENUE_ACCESS_CODE+ "&command=refundOrder&request_type=JSON&response_type=JSON&version=1.1";
			ccAvenue_Logger.info("Request URL for"+" "+ track_id + " "+requestParameters);
			
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			HttpEntity<String> httpEntity = new HttpEntity<String>(requestParameters, headers);
			RestTemplate restTemplate = new RestTemplate();
			String responseEntity = restTemplate.exchange(CCAVENUE_API_URL, HttpMethod.POST, httpEntity, String.class).getBody();
		
			ccAvenue_Logger.info("Response URL for"+" "+ track_id + " "+responseEntity);
		
			Map<String, String> responseData = getResponseStatus(responseEntity);
		
			ccAvenue_Logger.info("Response Data for"+" "+ track_id + " "+responseData);
			
			String STATUS = responseData.get("refund_status");
			ccAvenue_Logger.info("Status Code should be 0 for success response as for"+" "+ track_id + " is "+STATUS);
			
			if ("0".equals(STATUS)) {
				bean.setRefundStatus(responseData.get("refund_status"));
				bean.setRefundId(refId);	
			} else {
				ccAvenue_Logger.info("Error for"+" "+ track_id + " "+responseData.get("enc_response"));
				if(!responseData.get("reason").isEmpty() && responseData.get("reason")!=null) {
					bean.setResponseMessage(responseData.get("reason"));
				}
				if(!responseData.get("refund_status").isEmpty() && responseData.get("refund_status")!=null) {
					bean.setRefundStatus(responseData.get("refund_status"));
				}
				if(!responseData.get("refund_status").isEmpty() && responseData.get("refund_status")!=null) {
					bean.setError(responseData.get("enc_response"));
				}
			}
			return bean;
		} catch (Exception e) {
			exceptionHelper.createLog(e);
			bean.setError("Error in refund Intitate");
			return bean;
		}
	}
	
	public HashMap<String, String> getResponseDecrypt(String encRequestData){
		HashMap<String, String> decryptResponse=new HashMap<String, String>();

		ccAvenue_Logger.info("CREATING RESPONSE FOR CC_AVENUE");

		ccAvenue_Logger.info("Response URL for Decryption" + " " + encRequestData);
		AesCryptUtil aesUtil = new AesCryptUtil(CCAVENUE_WORKING_KEY);
		String decResp = aesUtil.decrypt(encRequestData);
		StringTokenizer tokenizer = new StringTokenizer(decResp, "&");

		String pair = null, pname = null, pvalue = null;
		while (tokenizer.hasMoreTokens()) {
			pair = (String) tokenizer.nextToken();
			if (pair != null) {
				StringTokenizer strTok = new StringTokenizer(pair, "=");
				pname = "";
				pvalue = "";
				if (strTok.hasMoreTokens()) {
					pname = (String) strTok.nextToken();
					if (strTok.hasMoreTokens())
						pvalue = (String) strTok.nextToken();
					decryptResponse.put(pname, pvalue);
				}
			}
		}
		ccAvenue_Logger.info("Decrypt data" + " " + decryptResponse);
		// System.out.println(hs);
		return decryptResponse;
	}
	
	
	public  TransactionStatusBean getRefundStatus(String merchantRefNo, String refundId) {
		TransactionStatusBean bean=new TransactionStatusBean();
		JsonObject json=new JsonObject();
		try {
		json= refundStatus(merchantRefNo,refundId);
		ccAvenue_Logger.info("Refund Status Json Data for"+" "+merchantRefNo+" "+json);	
		
		if(!json.get("order_status").isJsonNull()) {
		ccAvenue_Logger.info("Refund Status  for"+" "+merchantRefNo+" "+json.get("order_status"));
		bean.setRefundStatus(json.get("order_status").getAsString());
		}
		
		if(!json.get("error").isJsonNull()) {
		ccAvenue_Logger.info("Refund Status Json Data for"+" "+merchantRefNo+" "+json);
		bean.setError(json.get("error").getAsString());
		}
		return bean;
		}catch(Exception e) {
			ccAvenue_Logger.info("Error In refundStatus Method for "+" "+merchantRefNo);
			bean.setError(json.get("error").getAsString());
			return bean;
		}
		
		
	}
	
	public Map<String,String> getResponseStatus(String responseStatus){

		ccAvenue_Logger.info("Response URL for Status "+" "+responseStatus);
		Map<String,String> apiStatus= new HashMap<String, String>();
	
	
		StringTokenizer tokenizer = new StringTokenizer(responseStatus, "&");
		String pair=null, pname=null, pvalue=null;
		while (tokenizer.hasMoreTokens()) {
			pair = (String)tokenizer.nextToken();
			if(pair!=null) {
				StringTokenizer strTok=new StringTokenizer(pair, "=");
				pname=""; pvalue=""; 
				if(strTok.hasMoreTokens()) {
					pname=(String)strTok.nextToken();
					if(strTok.hasMoreTokens())
						pvalue=(String)strTok.nextToken();
					apiStatus.put(pname, pvalue);
				}
			}
		}

	
		ccAvenue_Logger.info("Response status code for Status "+" "+apiStatus);
		try {
			String status = apiStatus.get("status");
			System.out.println(apiStatus);
			if ("0".equals(status)) {

				String paymentResponses = apiStatus.get("enc_response");
				AesCryptUtil util = new AesCryptUtil(CCAVENUE_WORKING_KEY);
				String decrypt = util.decrypt(paymentResponses.trim());
				System.out.println("decrypt" + decrypt);
				Gson gson = new Gson();
				Type type = new TypeToken<Map<String, String>>() {}.getType();
				Map<String, String> responseData = gson.fromJson(decrypt, type);
				
				ccAvenue_Logger.info("Response status code for Status "+" "+responseData);
				return responseData;
			} else {
				return apiStatus;
			}
		
		} catch (Exception e) {
			//e.printStackTrace();
			return apiStatus;
		}
	}

	public Map<String,String> getTransactionStatusByTrackID(String trackd) {
		// TODO Auto-generated method stub
		JsonObject json = new JsonObject();
		json.addProperty("order_no", trackd);

		AesCryptUtil aesUtil = new AesCryptUtil(CCAVENUE_WORKING_KEY);
		String encRequest = aesUtil.encrypt(json.toString());
		String requestParameters = "enc_request=" + encRequest + "&access_code=" + CCAVENUE_ACCESS_CODE+"&command=orderStatusTracker&request_type=JSON&response_type=JSON&version=1.1";

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		HttpEntity<String> entity = new HttpEntity<String>(requestParameters, headers);
		String responseEntity = restTemplate.exchange(CCAVENUE_API_URL, HttpMethod.POST, entity, String.class).getBody();

	
		Map<String, String> responseData = getResponseStatus(responseEntity);
		return responseData;
	}
	

	
}
