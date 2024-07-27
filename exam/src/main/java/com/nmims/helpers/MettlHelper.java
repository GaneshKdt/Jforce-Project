package com.nmims.helpers;

import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.common.base.Throwables;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nmims.beans.ExamsAssessmentsBean;
import com.nmims.beans.MBAXMarksBean;
import com.nmims.beans.MBAXMarksPreviewBean;
import com.nmims.beans.MettlBean;
import com.nmims.beans.MettlRegisterCandidateBean;
import com.nmims.beans.MettlResponseBean;
import com.nmims.beans.MettlScheduleAPIBean;
import com.nmims.beans.MettlScheduleExamBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.TEEResultBean;
import com.nmims.controllers.MettlController;
import com.nmims.daos.ExamsAssessmentsDAO;
import com.nmims.daos.MettlDAO;
import com.nmims.daos.UpgradResultProcessingDao;

@Service("MettlHelper")
@Primary
public class MettlHelper {
	private static final String KEY_ERROR = "error";
	private static final String KEY_SUCCESS = "success";
	
	private static final String UTF8 = "UTF-8";
	private static final String CONTENT_TYPE = "Content-Type";
	private static final String CONTENT_TYPE_FORM_ENCODED = "application/x-www-form-urlencoded; charset=UTF-8";
	
	private static final Logger logger = LoggerFactory.getLogger("examRegisterPG");
	public static final Logger pullTimeBoundMettlMarksLogger =LoggerFactory.getLogger("pullTimeBoundMettlMarks");
	
	private static final Logger fetchMettlAllCandidateTestResult =LoggerFactory.getLogger("fetchMettlAllCandidateTestResult");
	
	@Autowired(required=false)
	ApplicationContext act;
	
	private String baseUrl;
	
	private String privateKey;
	
	private String publicKey;

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}
	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}
	public String getBaseUrl() {
		return baseUrl;
	}
	public String getPrivateKey() {
		return privateKey;
	}
	public String getPublicKey() {
		return publicKey;
	}
	
	private HttpHeaders getHeaders() {
		HttpHeaders headers =  new HttpHeaders();
		headers.add("Accept", "application/json");
		headers.add("Content-Type", "application/x-www-form-urlencoded");
		return headers;
	}
	
	private static String hmacSha(String KEY, String VALUE, String SHA_TYPE) {
        try {
            SecretKeySpec signingKey = new SecretKeySpec(KEY.getBytes("UTF-8"), SHA_TYPE);
            Mac mac = Mac.getInstance(SHA_TYPE);
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(VALUE.getBytes("UTF-8"));

            byte[] hexArray = {
                    (byte)'0', (byte)'1', (byte)'2', (byte)'3',
                    (byte)'4', (byte)'5', (byte)'6', (byte)'7',
                    (byte)'8', (byte)'9', (byte)'a', (byte)'b',
                    (byte)'c', (byte)'d', (byte)'e', (byte)'f'
            };
            byte[] hexChars = new byte[rawHmac.length * 2];
            for ( int j = 0; j < rawHmac.length; j++ ) {
                int v = rawHmac[j] & 0xFF;
                hexChars[j * 2] = hexArray[v >>> 4];
                hexChars[j * 2 + 1] = hexArray[v & 0x0F];
            }
            return new String(hexChars);
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
	
	private String sha256DemoExam(String s) {
		try {
	        SecretKeySpec key = new SecretKeySpec(("e62cdb6c-bf33-4020-8a56-d779eeb3e388").getBytes("UTF-8"), "HmacSHA1");
	        Mac mac = Mac.getInstance("HmacSHA256");
	        mac.init(key);
	        byte[] bytes = mac.doFinal(s.getBytes("UTF-8"));
	        return new String(Base64.getEncoder().encodeToString(bytes));
		}
		catch (Exception e) {
			// TODO: handle exception
			return null;
		}
    }
	
	private String sha256(String s) {
		try {
	        SecretKeySpec key = new SecretKeySpec((this.privateKey).getBytes("UTF-8"), "HmacSHA1");
	        Mac mac = Mac.getInstance("HmacSHA256");
	        mac.init(key);
	        byte[] bytes = mac.doFinal(s.getBytes("UTF-8"));
	        return new String(Base64.getEncoder().encodeToString(bytes));
		}
		catch (Exception e) {
			// TODO: handle exception
			return null;
		}
    }
	
	 @Deprecated
	 public HashMap<String, String> createAssessment(String mettlPublicKey, String mettlPrivateKey) {
		HashMap<String, String> responseHash = null;
		List<MettlBean> mList = null;
		String url = "https://api.mettl.com/v2/assessments";
		String generatedRequestSignature = null;
		try {
			responseHash = new HashMap<String, String>();
			
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
	        String unixTime = String.valueOf(timestamp.getTime() / 1000);
	        
			//mList = mettlDAO.fetchTimeTableURLList();
	        
	        HttpHeaders headers =  this.getHeaders();
	        RestTemplate restTemplate = new RestTemplate();
	        
	        JsonObject request = new JsonObject();
	        request.addProperty("name","Dummy Test assessment5999");
	        request.addProperty("duration","80");
	        request.addProperty("instructions","Compulsory");
	        request.addProperty("allowCopyPaste",false);
	        //request.addProperty("exitRedirectionURL", "");//Test completed, URL to redirected to,must be valid URL
	        request.addProperty("showReportToCandidateOnExit", false);
	        request.addProperty("onScreenCalculator",false);
	        
	        //section array list
	        ArrayList<JsonObject> sessionArrayList = new ArrayList<JsonObject>();
	        
	        JsonObject section = new JsonObject();
	        section.addProperty("name", "Dummy Assessment Q1");	//Name to section should be unique
	        //section.addProperty("duration", "");	// In case of timed section specify value, else give null/don’t send this property
	        section.addProperty("instructions", "Check and answer");	//description about section
	        section.addProperty("allQuestionsMandatory", true);	
	        section.addProperty("randomizeQuestions", true);
	        
	        //skills object
	        ArrayList<JsonObject> skillsArrayList = new ArrayList<JsonObject>();
	        JsonObject skills = new JsonObject();	
	        skills.addProperty("name", "Dummy Skill 1");		////Skill should exist in your question bank
	        skills.addProperty("level", "difficult");	//easy,medium and difficult
	        skills.addProperty("questionCount", "10");	//questionCount count
	        skills.addProperty("questionPooling", true);	//Randomization
	        skills.addProperty("questionType", "LONG_ANSWER");
	        skills.addProperty("correctGrade", 5);
	        skills.addProperty("incorrectGrade", 0);
	        skillsArrayList.add(skills);
	        
	        section.addProperty("skills", skillsArrayList.toString());
	        
	        sessionArrayList.add(section);
	        request.addProperty("sections", sessionArrayList.toString());
	        
	        JsonObject request2 = new JsonObject();
	        request2.addProperty("sc", request.toString());
	        request2.addProperty("ts", unixTime);
	        request2.addProperty("ak", mettlPublicKey);

	        generatedRequestSignature = sha1("POST"+url+"\n"+mettlPublicKey+"\n"+unixTime,mettlPrivateKey);
	        request2.addProperty("asgn", generatedRequestSignature);

	        url += "/?ak=" + mettlPublicKey;
	        url += "&asgn=" + generatedRequestSignature;
	        url += "&ts=" + unixTime;
	        
	        HttpEntity<String> entity = new HttpEntity<String>(request2.toString(),headers);
	        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
	        JsonObject responseJsonObj = new JsonParser().parse(response.getBody()).getAsJsonObject();
	        if ("200".equalsIgnoreCase(response.getStatusCode().toString())) {
	        	String status = responseJsonObj.get("status").getAsString();
	        	if("success".equalsIgnoreCase(status)) {
	        		responseHash.put("status", "success");
	        		responseHash.put("assessmentId",responseJsonObj.get("assessmentId").getAsString());
	        		return responseHash;
	        	}
	        	responseHash.put("status", "error");
	        	responseHash.put("message", responseJsonObj.get("error").getAsJsonObject().get("message").getAsString());
	        	return responseHash;
	        }
		}
		catch (Exception e) {
			
			// TODO: handle exception
			responseHash.put("status", "error");
        	responseHash.put("message",e.getMessage());
        	//return responseHash;
		}
		return responseHash;
	}
	
	public JsonObject getTestStatus(String scheduleKey,String emailId) {
		try {
			String publicKey = "72cdd358-e82c-495e-8ff6-40b2e9efc561";
			String url = "https://api.mettl.com/v2/schedules/"+ scheduleKey +"/candidates/" + emailId;
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
	        String unixTime = String.valueOf(timestamp.getTime() / 1000);
	        String urlParam = "GET" + url 
	        		+ "\n" + publicKey 
	        		+ "\n" + unixTime;
	        String generatedRequestSignature = this.sha256DemoExam(urlParam);
	        if(generatedRequestSignature == null) {
				return null;
			}
	        
	        String parameter = "?ak=" + publicKey 
					+ "&asgn=" + URLEncoder.encode(generatedRequestSignature) 
					+ "&ts=" + unixTime;
	        HttpHeaders headers =  this.getHeaders();
			RestTemplate restTemplate = new RestTemplate();
			HttpEntity<String> entity = new HttpEntity<String>("",headers);
			ResponseEntity<String> response = restTemplate.exchange(url + parameter, HttpMethod.GET, entity, String.class);
			JsonObject jsonResponse = new JsonParser().parse(response.getBody()).getAsJsonObject();
			return jsonResponse;
		}
		catch (Exception e) {
			// TODO: handle exception
			
			return null;
		}
	}
	 
	public JsonObject getAssessments() {
		try {
			String url = baseUrl + "assessments";
			
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
	        String unixTime = String.valueOf(timestamp.getTime() / 1000);
			int limit = 100;
			int offset = 20;
			String sort = "createdAt";
			String sortOrder = "asc";
			String urlParam = "GET" + url 
					+ "\n" + this.publicKey 
					+ "\n" + limit
//					+ "\n" + offset
//					+ "\n" + sort
//					+ "\n" + sortOrder
					+ "\n" + unixTime
					;
			String generatedRequestSignature = this.sha256(urlParam);
			if(generatedRequestSignature == null) {
				return null;
			}
			String limitStr = "&limit=" + limit; 
			String offsetStr = "&offset=" + offset; 
			String sortStr = "&sort=" + sort;
			String sortOrderStr = "&sort_order=" + sortOrder;
			
			
			String parameter = "?ak=" + publicKey 
					+ "&asgn=" + URLEncoder.encode(generatedRequestSignature) 
					+ limitStr
//					+ offsetStr
//					+ sortStr
//					+ sortOrderStr
					+ "&ts=" + unixTime
					;
			
			HttpHeaders headers =  this.getHeaders();
			RestTemplate restTemplate = new RestTemplate();
			HttpEntity<String> entity = new HttpEntity<String>("",headers);
			ResponseEntity<String> response = restTemplate.exchange(url + parameter, HttpMethod.GET, entity, String.class);
			JsonObject jsonResponse = new JsonParser().parse(response.getBody()).getAsJsonObject();
			return jsonResponse;
		}
		catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}
	

	public JsonObject getPGAssessments() {
		this.privateKey = "e62cdb6c-bf33-4020-8a56-d779eeb3e388";
		this.publicKey = "72cdd358-e82c-495e-8ff6-40b2e9efc561";
		try {
			//String url = baseUrl + "assessments";
			String url = "https://api.mettl.com/v2/" + "assessments";//Vilpesh on 2022-03-24
			
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
	        String unixTime = String.valueOf(timestamp.getTime() / 1000);
			int limit = 500;
			int offset = 20;
			String sort = "createdAt";
			String sortOrder = "asc";
			
			String urlParam = "GET" + url 
					+ "\n" + this.publicKey 
					+ "\n" + limit
//					+ "\n" + offset
//					+ "\n" + sort
//					+ "\n" + sortOrder
					+ "\n" + unixTime
					;
			String generatedRequestSignature = this.sha256(urlParam);
			if(generatedRequestSignature == null) {
				return null;
			}
			String limitStr = "&limit=" + limit; 
			String offsetStr = "&offset=" + offset; 
			String sortStr = "&sort=" + sort;
			String sortOrderStr = "&sort_order=" + sortOrder;
			
			
			String parameter = "?ak=" + publicKey 
					+ "&asgn=" + URLEncoder.encode(generatedRequestSignature) 
					+ limitStr
//					+ offsetStr
//					+ sortStr
//					+ sortOrderStr
					+ "&ts=" + unixTime
					;
			HttpHeaders headers =  this.getHeaders();
			RestTemplate restTemplate = new RestTemplate();
			HttpEntity<String> entity = new HttpEntity<String>("",headers);
			ResponseEntity<String> response = restTemplate.exchange(url + parameter, HttpMethod.GET, entity, String.class);
			JsonObject jsonResponse = new JsonParser().parse(response.getBody()).getAsJsonObject();
			logger.info("MettlHelper : getPGAssessments : jsonResponse : "+jsonResponse);//Vilpesh on 2022-03-24
			return jsonResponse;
		}
		catch (Exception e) {
			// TODO: handle exception
			logger.error("MettlHelper : getPGAssessments : "+e.getMessage());//Vilpesh on 2022-03-24
			return null;
		}
	}

	public String currentTimeLong() {
		Timestamp timestamp = null;
		timestamp = new Timestamp(System.currentTimeMillis());
        return String.valueOf(timestamp.getTime() / 1000);
	}
	
	/**
	 * HTTP POST via REST template.
	 * @param url - URL at which request is made at METTL.
	 * @param bodyAsStr - Body with ak,asgn,sc,ts query parameter values required at METTL.
	 * @param headers - HTTP Header info required at METTL.
	 * @return ResponseEntity Object with Success/Error info from METTL.
	 */
	protected ResponseEntity<String> postWithREST_API(final String url, final String bodyAsStr, final HttpHeaders headers) {
		RestTemplate restTemplate = null;
		HttpEntity<String> entity = null;
		ResponseEntity<String> response = null;
		
		try {
			restTemplate = new RestTemplate();
	        entity = new HttpEntity<String>(bodyAsStr, headers);
	        response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
		} finally {
			restTemplate = null;
			entity = null;
		}
		return response;
	}
	
	/**
	 * HTTP POST specific to METTL API.
	 * @param url - URL at which request is made at METTL.
	 * @param scPayLoad - Data in METTL API format.
	 * @param publicKey - Public Key specific to account at METTL.
	 * @param privateKey - Private Key specific to account at METTL.
	 * @return ResponseEntity Object returned.
	 * @throws UnsupportedEncodingException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 */
	protected ResponseEntity<String> postAtMettl(final String url, String scPayLoad, final String publicKey, final String privateKey) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
		String unixTime = null;
		String asgnStr = null;
		String bodyAsStr = null;
		HttpHeaders headers = null;
		ResponseEntity<String> response = null;
		String verb = null;
		try {
			verb = HttpMethod.POST.toString();
			headers = new HttpHeaders();
	        headers.add(CONTENT_TYPE, CONTENT_TYPE_FORM_ENCODED);
	        
	        unixTime = currentTimeLong();
	        asgnStr = sha1(verb+url+"\n"+publicKey+"\n"+scPayLoad+"\n"+unixTime, privateKey);
	        asgnStr = URLEncoder.encode(asgnStr, UTF8);
	        scPayLoad = URLEncoder.encode(scPayLoad, UTF8);
	        logger.info("----- URL : "+ url + "   SHA256 : " + asgnStr);
	        logger.info("-----  SC : " + scPayLoad);
	        bodyAsStr = "ak="+publicKey + "&asgn="+asgnStr+ "&sc="+scPayLoad+ "&ts="+unixTime;
	        logger.info("----- BODY : " + bodyAsStr);
	        response = postWithREST_API(url, bodyAsStr, headers);
		} finally {
	        headers.clear();
			headers = null;
			unixTime = null;
			asgnStr = null;
		}
		return response;
	}
	
	/**
	 * HTTP POST specific to METTL API.
	 * @param url - URL at which request is made at METTL.
	 * @param payLoadParamName - Parameter name for which data is passed (viz. sc,rd)
	 * @param payLoadValue - Data in METTL API format.
	 * @param publicKey - Public Key specific to account at METTL.
	 * @param privateKey - Private Key specific to account at METTL.
	 * @return  ResponseEntity Object.
	 * @throws UnsupportedEncodingException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 */
	protected ResponseEntity<String> postAtMettl(final String url, final String payLoadParamName, String payLoadValue, final String publicKey, final String privateKey) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
		String unixTime = null;
		String asgnStr = null;
		String bodyAsStr = null;
		HttpHeaders headers = null;
		ResponseEntity<String> response = null;
		String verb = null;
		try {
			verb = HttpMethod.POST.toString();
			headers = new HttpHeaders();
	        headers.add(CONTENT_TYPE, CONTENT_TYPE_FORM_ENCODED);
	        
	        unixTime = currentTimeLong();
	        asgnStr = sha1(verb+url+"\n"+publicKey+"\n"+payLoadValue+"\n"+unixTime, privateKey);
	        asgnStr = URLEncoder.encode(asgnStr, UTF8);
	        payLoadValue = URLEncoder.encode(payLoadValue, UTF8);
	        logger.info("----- URL : "+ url + "   SHA256 : " + asgnStr);
	        logger.info("----- "+ payLoadParamName + " : " + payLoadValue);
	        bodyAsStr = "ak="+publicKey + "&asgn="+asgnStr+ "&"+ payLoadParamName + "=" +payLoadValue+ "&ts="+unixTime;
	        logger.info("----- BODY : " + bodyAsStr);
	        response = postWithREST_API(url, bodyAsStr, headers);
		} finally {
	        headers.clear();
			headers = null;
			unixTime = null;
			asgnStr = null;
		}
		return response;
	}

	/**
	 * Data assembled in order and format according to METTL requirement.
	 * Data returned from METTL extracted.
	 * @param queryBean Contains data to be sent to METTL.
	 * @param sourceAppName Unique name of application requesting data from METTL.
	 * @return Contains data received from METTL.
	 */
	public MettlScheduleExamBean createSchedule(final MettlScheduleExamBean queryBean, final String sourceAppName) {
		ResponseEntity<String> response = null;
		Gson gson = null;
		String scPayLoad = null;
		String url = null;
		MettlScheduleAPIBean bean = null;
		JsonObject responseJsonObj = null;
		MettlScheduleExamBean dbBean = null;
		String message = null;
		try {
			gson = new Gson();
	        dbBean = new MettlScheduleExamBean();
			url = this.getBaseUrl() + "assessments/" + queryBean.getAssessmentId() + "/schedules";
			bean = transferIntoMettlScheduleAPI(queryBean);
	        scPayLoad = gson.toJson(bean);
	        logger.info("PayLoad : "+scPayLoad);
	        response = postAtMettl(url, scPayLoad, this.getPublicKey(), this.getPrivateKey());
	        
	        responseJsonObj = new JsonParser().parse(response.getBody()).getAsJsonObject();
	        logger.info("Response : "+responseJsonObj);
	        
	        if(null != responseJsonObj.get("status")) {
	        	if(KEY_SUCCESS.equalsIgnoreCase(responseJsonObj.get("status").getAsString())) {
	        		dbBean.setAssessmentId(responseJsonObj.get("createdSchedule").getAsJsonObject().get("assessmentId").getAsString());
	        		dbBean.setScheduleId(responseJsonObj.get("createdSchedule").getAsJsonObject().get("id").getAsString());
	        		dbBean.setScheduleName(responseJsonObj.get("createdSchedule").getAsJsonObject().get("name").getAsString());
	        		dbBean.setScheduleAccessKey(responseJsonObj.get("createdSchedule").getAsJsonObject().get("accessKey").getAsString());
	        		dbBean.setScheduleAccessURL(responseJsonObj.get("createdSchedule").getAsJsonObject().get("accessUrl").getAsString());
	        		dbBean.setScheduleStatus(responseJsonObj.get("createdSchedule").getAsJsonObject().get("status").getAsString());
	        		message = "Success for AssessmentId " + (queryBean.getAssessmentId());
	        		dbBean.setStatus(KEY_SUCCESS);
	        	} else if(KEY_ERROR.equalsIgnoreCase(responseJsonObj.get("status").getAsString())) {
	        		message = "Failure for AssessmentId " + (queryBean.getAssessmentId()) + " with ";
	        		message += responseJsonObj.get("error").getAsJsonObject().get("code").getAsString();
	        		message += " - " + responseJsonObj.get("error").getAsJsonObject().get("message").getAsString();
	        		dbBean.setStatus(KEY_ERROR);
	        	}
        		dbBean.setMessage(message);
	        }
		} 
			  catch(UnsupportedEncodingException | NoSuchAlgorithmException |
			  InvalidKeyException e) { dbBean.setStatus(KEY_ERROR);
			  dbBean.setMessage("SHA Error ? --------"+e.getMessage());
			  logger.error("SHA Error ? --------"+e.getMessage());
			  
			  }
			  catch (Exception ex) {
				 //ex.printStackTrace();
			dbBean.setStatus(KEY_ERROR);
			dbBean.setMessage("General Error ? --------"+ex.getMessage());
			logger.error("General Error ? --------"+ex.getMessage());
		} finally {
			response = null;
			url = null;
			bean = null;
			scPayLoad = null;
			gson = null;
		}
        return dbBean;
	}
	
	public MettlScheduleAPIBean transferIntoMettlScheduleAPI(MettlScheduleExamBean bean) {
		MettlScheduleAPIBean mettlBean = new MettlScheduleAPIBean();
		
		mettlBean.setAssessmentId(bean.getAssessmentId());
		mettlBean.setName(bean.getCustomUrlId());//"Dummy6015"
		
		mettlBean.setScheduleType(bean.getScheduleType());//Fixed/AlwaysOn
		
		mettlBean.setTestLinkSettings(null);

		if(MettlScheduleAPIBean.FIXED.equalsIgnoreCase(bean.getScheduleType())) {
			mettlBean.setScheduleWindow(mettlBean.new ScheduleWindow());
			mettlBean.getScheduleWindow().setFixedAccessOption(bean.getFixedAccessOptionSW());//SlotWise
			mettlBean.getScheduleWindow().setStartsOnDate(bean.getStartsOnDate());//"Thu, 09 Jul 2020"
			mettlBean.getScheduleWindow().setEndsOnDate(bean.getStartsOnDate());//"Thu, 09 Jul 2020"
			mettlBean.getScheduleWindow().setStartsOnTime(bean.getStartTime());//"17:00:00"
			//mettlBean.getScheduleWindow().setEndsOnTime(bean.getEndTime());//"19:30:00"
			mettlBean.getScheduleWindow().setEndsOnTime(bean.getEndTime2());//"19:30:00"
			bean.setEndTime(bean.getEndTime2());
			if(null != bean.getLocationtimeZoneSW()) {
				mettlBean.getScheduleWindow().setLocationtimeZone(bean.getLocationtimeZoneSW());
			}
			if(null != bean.getTimeZoneSW()) {
				mettlBean.getScheduleWindow().setTimeZone(bean.getTimeZoneSW());
			}
		}
		
		mettlBean.getWebProctoring().setCount(bean.getCountWP());
		mettlBean.getWebProctoring().setEnabled(bean.isEnabledWP());
		mettlBean.getWebProctoring().setShowRemainingCounts(bean.isShowRemainingCountsWP());
		
		mettlBean.getVisualProctoring().setMode(bean.getModeVP());
		mettlBean.getVisualProctoring().getOptions().setCandidateScreenCapture(bean.isCandidateScreenCaptureVP());
		mettlBean.getVisualProctoring().getOptions().setCandidateAuthorization(bean.isCandidateAuthorizationVP());
		
		if(MettlScheduleAPIBean.BYINVITATION.equalsIgnoreCase(bean.getTypeA())) {
			mettlBean.setAccess(null);
			mettlBean.setAccess(mettlBean.new Access(bean.getTypeA(), bean.getCandidatesA().length));
			mettlBean.getAccess().setSendEmail(bean.isSendEmailA());
			for(int j = 0; j < bean.getCandidatesA().length; j++) {
				mettlBean.getAccess().addCandidateInfo(j, bean.getCandidatesA()[j][0], bean.getCandidatesA()[j][1]);
			}
		}
		
		mettlBean.getIpAccessRestriction().setEnabled(bean.isEnabledIP());
		if(bean.isEnabledIP()) {
			if(MettlScheduleAPIBean.SINGLE.equalsIgnoreCase(bean.getTypeIP())) {
				mettlBean.getIpAccessRestriction().setIp(bean.getIpIP());
			} else if(MettlScheduleAPIBean.RANGE.equalsIgnoreCase(bean.getTypeIP())) {
				mettlBean.getIpAccessRestriction().setRanges(bean.getRangesIP());
			}
			mettlBean.getIpAccessRestriction().setType(bean.getTypeIP());
		}
		
		mettlBean.getTestGradeNotification().setEnabled(bean.isEnabledTG());
		if(bean.isEnabledTG()) {
			mettlBean.getTestGradeNotification().setRecipients(bean.getRecipientsTG());
		}
		
		mettlBean.setSourceApp(bean.getSourceApp());
		mettlBean.setTestStartNotificationUrl(bean.getTestStartNotificationUrl());
		mettlBean.setTestFinishNotificationUrl(bean.getTestFinishNotificationUrl());
		mettlBean.setTestGradedNotificationUrl(bean.getTestGradedNotificationUrl());
		mettlBean.setTestResumeEnabledForExpiredTestURL(bean.getTestResumeEnabledForExpiredTestURL());
		
		return mettlBean;
	}
	
	/**
	 * Data assembled in order and format according to METTL requirement with new METTL waiting room payload addon.
	 * Data returned from METTL extracted.
	 * @param queryBean Contains data to be sent to METTL.
	 * @param sourceAppName Unique name of application requesting data from METTL.
	 * @return Contains data received from METTL.
	 */
	public MettlScheduleExamBean createScheduleWithWaitingRoom(final MettlScheduleExamBean queryBean, final String sourceAppName) {
		ResponseEntity<String> response = null;
		Gson gson = null;
		String scPayLoad = null;
		String url = null;
		MettlScheduleAPIBean bean = null;
		JsonObject responseJsonObj = null;
		MettlScheduleExamBean dbBean = null;
		String message = null;
		try {
			gson = new Gson();
	        dbBean = new MettlScheduleExamBean();
			url = this.getBaseUrl() + "assessments/" + queryBean.getAssessmentId() + "/schedules";
	        
			bean = transferIntoMettlScheduleWaitingRoomAPI(queryBean);
	        scPayLoad = gson.toJson(bean);
	        logger.info(" PayLoad : {} ",scPayLoad);
	        
	        response = postAtMettl(url, scPayLoad, this.getPublicKey(), this.getPrivateKey());
	        
	        responseJsonObj = new JsonParser().parse(response.getBody()).getAsJsonObject();
	        logger.info("Response : {} ", responseJsonObj);
	        
	        if(null != responseJsonObj.get("status")) {
	        	if(KEY_SUCCESS.equalsIgnoreCase(responseJsonObj.get("status").getAsString())) {
	        		dbBean.setAssessmentId(responseJsonObj.get("createdSchedule").getAsJsonObject().get("assessmentId").getAsString());
	        		dbBean.setScheduleId(responseJsonObj.get("createdSchedule").getAsJsonObject().get("id").getAsString());
	        		dbBean.setScheduleName(responseJsonObj.get("createdSchedule").getAsJsonObject().get("name").getAsString());
	        		dbBean.setScheduleAccessKey(responseJsonObj.get("createdSchedule").getAsJsonObject().get("accessKey").getAsString());
	        		dbBean.setScheduleAccessURL(responseJsonObj.get("createdSchedule").getAsJsonObject().get("accessUrl").getAsString());
	        		dbBean.setScheduleStatus(responseJsonObj.get("createdSchedule").getAsJsonObject().get("status").getAsString());
	        		
	        		message = "Success for AssessmentId " + (queryBean.getAssessmentId());
	        		dbBean.setStatus(KEY_SUCCESS);
	        	} else if(KEY_ERROR.equalsIgnoreCase(responseJsonObj.get("status").getAsString())) {
	        		message = "Failure for AssessmentId " + (queryBean.getAssessmentId()) + " with ";
	        		message += responseJsonObj.get("error").getAsJsonObject().get("code").getAsString();
	        		message += " - " + responseJsonObj.get("error").getAsJsonObject().get("message").getAsString();
	        		dbBean.setStatus(KEY_ERROR);
	        	}
        		dbBean.setMessage(message);
	        }
		} catch(UnsupportedEncodingException | NoSuchAlgorithmException | InvalidKeyException e) {
			dbBean.setStatus(KEY_ERROR);
			dbBean.setMessage("SHA Error ? --------"+e.getMessage());
			logger.error("SHA Error ? -------- {} ", e.getMessage());
			
		} catch (Exception ex) {
			dbBean.setStatus(KEY_ERROR);
			dbBean.setMessage("General Error ? --------"+ex.getMessage());
			logger.error("General Error ? -------- {}", ex.getMessage());
		} finally {
			response = null;
			url = null;
			bean = null;
			scPayLoad = null;
			gson = null;
		}
        return dbBean;
	}
	
	public MettlScheduleAPIBean transferIntoMettlScheduleWaitingRoomAPI(MettlScheduleExamBean bean) {
		MettlScheduleAPIBean mettlBean = new MettlScheduleAPIBean();
		
		mettlBean.setAssessmentId(bean.getAssessmentId());
		mettlBean.setName(bean.getCustomUrlId());//"Dummy6015"
		
		//for testing
//		mettlBean.setName(bean.getCustomUrlId() + ThreadLocalRandom.current().nextInt(00, 99));//"Dummy6015"
		
		mettlBean.setTestLinkType(bean.getTestLinkType());
		
		mettlBean.setScheduleType(bean.getScheduleType());//Fixed/AlwaysOn
		
		if (MettlScheduleAPIBean.SCHEDULED.equalsIgnoreCase(bean.getTestLinkType())
				&& MettlScheduleAPIBean.FIXED.equalsIgnoreCase(bean.getScheduleType())) {
			mettlBean.setTestLinkSettings(mettlBean.new TestLinkSettings());
			mettlBean.getTestLinkSettings().setTestDate(bean.getStartsOnDate());
			mettlBean.getTestLinkSettings().setTestStartTime(bean.getStartTime());
			mettlBean.getTestLinkSettings().setTestEndTime(bean.getEndTime2());
			mettlBean.getTestLinkSettings().setReportingStartTime(bean.getReportingStartTime());
			mettlBean.getTestLinkSettings().setReportingFinishTime(bean.getReportingFinishTime());
			
			//it was already there in existing flow
//			bean.setEndTime(bean.getEndTime2());

			if (null != bean.getTimeZoneSW())
				mettlBean.getTestLinkSettings().setTimeZone(bean.getTimeZoneSW());

			if (null != bean.getLocationtimeZoneSW())
				mettlBean.getTestLinkSettings().setLocationTimeZone(bean.getLocationtimeZoneSW());
		}
		
		mettlBean.getWebProctoring().setCount(bean.getCountWP());
		mettlBean.getWebProctoring().setEnabled(bean.isEnabledWP());
		mettlBean.getWebProctoring().setShowRemainingCounts(bean.isShowRemainingCountsWP());
		
		mettlBean.getVisualProctoring().setMode(bean.getModeVP());
		mettlBean.getVisualProctoring().getOptions().setCandidateScreenCapture(bean.isCandidateScreenCaptureVP());
		mettlBean.getVisualProctoring().getOptions().setCandidateAuthorization(bean.isCandidateAuthorizationVP());
		mettlBean.getVisualProctoring().getOptions().setAudioProctoring(bean.isAudioProctoring());
		
		if(MettlScheduleAPIBean.BYINVITATION.equalsIgnoreCase(bean.getTypeA())) {
			mettlBean.setAccess(null);
			mettlBean.setAccess(mettlBean.new Access(bean.getTypeA(), bean.getCandidatesA().length));
			mettlBean.getAccess().setSendEmail(bean.isSendEmailA());
			for(int j = 0; j < bean.getCandidatesA().length; j++) {
				mettlBean.getAccess().addCandidateInfo(j, bean.getCandidatesA()[j][0], bean.getCandidatesA()[j][1]);
			}
		}
		
		mettlBean.getIpAccessRestriction().setEnabled(bean.isEnabledIP());
		if(bean.isEnabledIP()) {
			if(MettlScheduleAPIBean.SINGLE.equalsIgnoreCase(bean.getTypeIP())) {
				mettlBean.getIpAccessRestriction().setIp(bean.getIpIP());
			} else if(MettlScheduleAPIBean.RANGE.equalsIgnoreCase(bean.getTypeIP())) {
				mettlBean.getIpAccessRestriction().setRanges(bean.getRangesIP());
			}
			mettlBean.getIpAccessRestriction().setType(bean.getTypeIP());
		}
		
		mettlBean.getTestGradeNotification().setEnabled(bean.isEnabledTG());
		if(bean.isEnabledTG()) {
			mettlBean.getTestGradeNotification().setRecipients(bean.getRecipientsTG());
		}
		
		mettlBean.setSourceApp(bean.getSourceApp());
		mettlBean.setTestStartNotificationUrl(bean.getTestStartNotificationUrl());
		mettlBean.setTestFinishNotificationUrl(bean.getTestFinishNotificationUrl());
		mettlBean.setTestGradedNotificationUrl(bean.getTestGradedNotificationUrl());
		mettlBean.setTestResumeEnabledForExpiredTestURL(bean.getTestResumeEnabledForExpiredTestURL());
		
		return mettlBean;
	}
	
	@Deprecated
	public MettlRegisterCandidateBean[] registerCandidate(final List<MettlRegisterCandidateBean> regCandiBeanList, final String accessKey) {
		ResponseEntity<String> response = null;
		Gson gson = null;
		String payLoad = null;
		String url = null;
		JsonObject responseJsonObj = null;
		MettlRegisterCandidateBean[] candidateBeanArr = null;
		MettlRegisterCandidateBean candidateBean = null;
		String message = null;
		try {
			gson = new Gson();
			candidateBean = new MettlRegisterCandidateBean();
			url = this.getBaseUrl() + "schedules/" + accessKey + "/candidates";
	        
			//As per JSON format for data required at METTL.
			payLoad = "{\"registrationDetails\":[";
			for (int y = 0; y < regCandiBeanList.size(); y++) {
				if(y > 0) {
					payLoad += ",";
				}
				payLoad += "{";
				payLoad += "\"Email Address\":"+ "\"" + regCandiBeanList.get(y).getEmailAddress() +"\"";
				payLoad += ",\"First Name\":"+ "\"" + regCandiBeanList.get(y).getFirstName() +"\"";
				payLoad += ",\"Last Name\":"+ "\"" + regCandiBeanList.get(y).getLastName() +"\"";
				payLoad += ",\"Candidate Image(Verification)\":" +"\"" + regCandiBeanList.get(y).getCandidateImage() +"\"";
				payLoad += ",\"RegistrationImage\":"+ "\"" + regCandiBeanList.get(y).getRegistrationImage() +"\"";
				payLoad += ",\"SAP ID\":"+ "\"" + regCandiBeanList.get(y).getSapId() +"\"";
				payLoad += "}";
			}
			payLoad += "]}";
			
	        logger.info("PayLoad : "+payLoad);
	        response = postAtMettl(url, "rd", payLoad, this.getPublicKey(), this.getPrivateKey());
	        
	        responseJsonObj = new JsonParser().parse(response.getBody()).getAsJsonObject();
	        logger.info("Response : "+responseJsonObj);
	        
	        if(null != responseJsonObj.get("status") && KEY_ERROR.equalsIgnoreCase(responseJsonObj.get("status").getAsString())) {
	        	candidateBeanArr = new MettlRegisterCandidateBean[1];
	        	candidateBeanArr[0] = new MettlRegisterCandidateBean();
	        	candidateBeanArr[0].setStatus(KEY_ERROR);
	        	message = responseJsonObj.get("error").getAsJsonObject().get("code").getAsString();
	        	message += " : ";
	        	message += responseJsonObj.get("error").getAsJsonObject().get("message").getAsString();
	        	candidateBeanArr[0].setMessage(message);
	        } else {
	        	candidateBeanArr = gson.fromJson(responseJsonObj.get("registrationStatus").getAsJsonArray(), MettlRegisterCandidateBean[].class);
	        }
		} catch(UnsupportedEncodingException | NoSuchAlgorithmException | InvalidKeyException e) {
			candidateBean.setStatus(KEY_ERROR);
			candidateBean.setMessage("SHA Error ? --------"+e.getMessage());
			logger.error("SHA Error ? --------"+e.getMessage());
			
		} catch (Exception ex) {
			candidateBean.setStatus(KEY_ERROR);
			candidateBean.setMessage("General Error ? --------"+ex.getMessage());
			logger.error("General Error ? --------"+ex.getMessage());
		} finally {
			response = null;
			url = null;
			payLoad = null;
			gson = null;
		}
        return candidateBeanArr;
	}
	
	/**
	 * Register 1 Candidate at METTL of a Schedule.
	 * @param regCandidateBean - Contains Data sent to METTL.
	 * @param accessKey - Schedule's Access Key.
	 * @param compensatoryTime - extra time (in minutes), for Special Need Students.
	 * @param compensatoryTimeFlag - assign compensatoryTime, if Special Need Student.
	 * @return Instance of MettlRegisterCandidateBean in Array with relevant data.
	 */
	public MettlRegisterCandidateBean[] registerCandidate(final MettlRegisterCandidateBean regCandidateBean,
			final String accessKey, final String compensatoryTime, final boolean compensatoryTimeFlag) {
		ResponseEntity<String> response = null;
		Gson gson = null;
		String payLoad = null;
		String url = null;
		JsonObject responseJsonObj = null;
		MettlRegisterCandidateBean[] candidateBeanArr = null;
		MettlRegisterCandidateBean candidateBean = null;
		String message = null;
		String sapId = null;
		try {
			sapId = regCandidateBean.getSapId();
			gson = new Gson();
			candidateBean = new MettlRegisterCandidateBean();
			url = this.getBaseUrl() + "schedules/" + accessKey + "/candidates";
	        
			//As per JSON format for data required at METTL.
			payLoad = "{\"registrationDetails\":[";
			//for (int y = 0; y < regCandiBeanList.size(); y++) {
				//if(y > 0) {
					//payLoad += ",";
				//}
			payLoad += "{";
			payLoad += "\"Email Address\":"+ "\"" + regCandidateBean.getEmailAddress() +"\"";
			payLoad += ",\"First Name\":"+ "\"" + regCandidateBean.getFirstName() +"\"";
			payLoad += ",\"Last Name\":"+ "\"" + regCandidateBean.getLastName() +"\"";
			payLoad += ",\"Candidate Image(Verification)\":" +"\"" + regCandidateBean.getCandidateImage() +"\"";
			payLoad += ",\"RegistrationImage\":"+ "\"" + regCandidateBean.getRegistrationImage() +"\"";
			payLoad += ",\"SAP ID\":"+ "\"" + sapId +"\"";
			payLoad += "}";
			//}
			payLoad += "]";
			if(compensatoryTimeFlag) {
				payLoad += ",\"optionalParams\":[";
				payLoad += "{";
				payLoad += "\"email\":"+ "\"" + regCandidateBean.getEmailAddress() +"\"";
				payLoad += ",\"context_data\":\"tech\"";
				payLoad += ",\"compensatory_time\":"+ "\"" + compensatoryTime +"\"";
				payLoad += "}";
				payLoad += "]";
			}
			payLoad += "}";
			
	        logger.info("PayLoad : "+payLoad);
	        response = postAtMettl(url, "rd", payLoad, this.getPublicKey(), this.getPrivateKey());
	        
	        responseJsonObj = new JsonParser().parse(response.getBody()).getAsJsonObject();
	        logger.info("Response : "+responseJsonObj);
	        
	        if(null != responseJsonObj.get("status") && KEY_ERROR.equalsIgnoreCase(responseJsonObj.get("status").getAsString())) {
	        	candidateBeanArr = new MettlRegisterCandidateBean[1];
	        	candidateBeanArr[0] = new MettlRegisterCandidateBean();
	        	candidateBeanArr[0].setStatus(KEY_ERROR);
	        	message = responseJsonObj.get("error").getAsJsonObject().get("code").getAsString();
	        	message += " : ";
	        	message += responseJsonObj.get("error").getAsJsonObject().get("message").getAsString();
	        	candidateBeanArr[0].setMessage(message);
	        } else {
	        	candidateBeanArr = gson.fromJson(responseJsonObj.get("registrationStatus").getAsJsonArray(), MettlRegisterCandidateBean[].class);
	        }
		} catch(UnsupportedEncodingException | NoSuchAlgorithmException | InvalidKeyException e) {
			candidateBean.setStatus(KEY_ERROR);
			candidateBean.setMessage("SHA Error ? --------"+e.getMessage());
			candidateBeanArr = new MettlRegisterCandidateBean[1];
			candidateBeanArr[0] = candidateBean;
			logger.error("SHA Error ? --------"+e.getMessage());
			
		} catch (Exception ex) {
			candidateBean.setStatus(KEY_ERROR);
			candidateBean.setMessage("General Error ? --------"+ex.getMessage());
			logger.error("General Error ? --------"+ex.getMessage());
			candidateBeanArr = new MettlRegisterCandidateBean[1];
			candidateBeanArr[0] = candidateBean;
		} finally {
			response = null;
			url = null;
			payLoad = null;
			gson = null;
		}
        return candidateBeanArr;
	}
	
	/**
	 * Register 1 Candidate at METTL of a Schedule. Success/Error of METTL returned status saved in Database only if DAO not null.
	 * @param regCandidateBean - Contains Data sent to METTL.
	 * @param accessKey - Schedule's Access Key. 
	 * @param mettlDAO - METTL returned status saved in Database only if DAO not null.
	 * @return - True only, when data posted successfully at METTL and saved in database.
	 */
	public Boolean registerCandidate(final MettlRegisterCandidateBean regCandidateBean, final String accessKey, MettlDAO mettlDAO) {
		ResponseEntity<String> response = null;
		Gson gson = null;
		String payLoad = null;
		String url = null;
		JsonObject responseJsonObj = null;
		MettlRegisterCandidateBean[] candidateBeanArr = null;
		String message = null;
		String sapId = null;
		Boolean isError = Boolean.TRUE;
		try {
			sapId = regCandidateBean.getSapId();
			gson = new Gson();
			url = this.getBaseUrl() + "schedules/" + accessKey + "/candidates";
	        
			//As per JSON format for data required at METTL.
			payLoad = "{\"registrationDetails\":[";
			payLoad += "{";
			payLoad += "\"Email Address\":"+ "\"" + regCandidateBean.getEmailAddress() +"\"";
			payLoad += ",\"First Name\":"+ "\"" + regCandidateBean.getFirstName() +"\"";
			payLoad += ",\"Last Name\":"+ "\"" + regCandidateBean.getLastName() +"\"";
			payLoad += ",\"Candidate Image(Verification)\":" +"\"" + regCandidateBean.getCandidateImage() +"\"";
			payLoad += ",\"RegistrationImage\":"+ "\"" + regCandidateBean.getRegistrationImage() +"\"";
			payLoad += ",\"SAP ID\":"+ "\"" + sapId +"\"";
			payLoad += "}";
			payLoad += "]}";
			
	        logger.info("PayLoad : "+payLoad);
	        response = postAtMettl(url, "rd", payLoad, this.getPublicKey(), this.getPrivateKey());
	        
	        responseJsonObj = new JsonParser().parse(response.getBody()).getAsJsonObject();
	        logger.info("Response : "+responseJsonObj);
	        
	        if(null != responseJsonObj.get("status") && KEY_ERROR.equalsIgnoreCase(responseJsonObj.get("status").getAsString())) {
	        	message = responseJsonObj.get("error").getAsJsonObject().get("code").getAsString();
	        	message += " : ";
	        	message += responseJsonObj.get("error").getAsJsonObject().get("message").getAsString();
	        	if(null != mettlDAO) {
	        		mettlDAO.saveCandidateRegisteredMettlInfo(sapId, accessKey, 1, (KEY_ERROR + "|" + replace(message)), null);
	        	}
	        } else {
	        	candidateBeanArr = gson.fromJson(responseJsonObj.get("registrationStatus").getAsJsonArray(), MettlRegisterCandidateBean[].class);
	        	if(null != mettlDAO) {
	        		mettlDAO.saveCandidateRegisteredMettlInfo(sapId, accessKey, 0, (candidateBeanArr[0].getStatus() + "|" + replace(candidateBeanArr[0].getMessage())), candidateBeanArr[0].getUrl());
	        	}
	        	isError = Boolean.FALSE;
	        }
		} catch(UnsupportedEncodingException | NoSuchAlgorithmException | InvalidKeyException e) {
			logger.error("SHA Error ? --------"+e.getMessage());
			
		} catch (Exception ex) {
			logger.error("General Error ? --------"+ex.getMessage());
		} finally {
			responseJsonObj = null;
			response = null;
			message = null;
			url = null;
			payLoad = null;
			gson = null;
		}
        return isError;
	}
	
	@Deprecated
	/*public HashMap<String, String> createSchedule(String mettlPrivateKey, String mettlPublicKey, String userId) {
		HashMap<String, String> responseHash = null;
		List<MettlScheduleBean> assessmentList = null;
		Gson gson = null;
		JsonObject responseJsonObj = null;
		RestTemplate restTemplate = null;
		HttpEntity<String> entity = null;
		ResponseEntity<String> response = null;
		HttpHeaders headers = null;
		String unixTime = null;
		String asgnStr = null;
		String bodyAsStr = null;
		String scPayLoad = null;
		String verb = "POST";
		String sourceAppName = "NMIMSDummyTestApp";
		String url = null;
		MettlScheduleAPIBean bean = null;
		MettlScheduleBean dbBean = null;
		MettlScheduleBean queryBean = null;
		Boolean isError = Boolean.FALSE;
		String message = null;
		int rowsProcessed = 0;
		try {
			assessmentList = mettlDAO.fetchAssessmentsList();
			
			responseHash = new HashMap<String, String>();
			gson = new Gson();
			restTemplate = new RestTemplate();
			headers = new HttpHeaders();
	        headers.add("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			
	        for(int y = 0; y < assessmentList.size(); y++) {
	        	queryBean = assessmentList.get(y);
	        	url = "https://api.mettl.com/v2/assessments/" + queryBean.getAssessmentId() + "/schedules";
	        	
	        	bean = new MettlScheduleAPIBean();
		        bean.setAssessmentId(queryBean.getAssessmentId());
		        bean.setName(queryBean.getCustomUrlId());//"Dummy6015"
		        bean.setScheduleType(FIXED);//"Fixed");//AlwaysOn
		        bean.getScheduleWindow().setFixedAccessOption("ExactTime");//SlotWise
		        bean.getScheduleWindow().setStartsOnDate(queryBean.getStartsOnDate());//"Thu, 09 Jul 2020");
		        bean.getScheduleWindow().setEndsOnDate(queryBean.getStartsOnDate());//"Thu, 09 Jul 2020");
		        bean.getScheduleWindow().setStartsOnTime(queryBean.getStartTime());//"17:00:00");
		        bean.getScheduleWindow().setEndsOnTime(queryBean.getEndTime());//"19:30:00");
		        bean.getScheduleWindow().setLocationtimeZone("Asia/Kolkata");
		        bean.getScheduleWindow().setTimeZone("UTC+05:30");
		        bean.getWebProctoring().setEnabled(Boolean.TRUE);
		        bean.getWebProctoring().setCount(4);
		        bean.getWebProctoring().setShowRemainingCounts(Boolean.TRUE);
		        bean.getAccess().setType(OPENFORALL);//"OpenForAll");
		        bean.setSourceApp(sourceAppName);//"NMIMSDummyTestApp");
		        bean.setTestStartNotificationUrl("http://uat-studentzone-ngasce.nmims.edu/exam/startMettlTest");
		        bean.setTestFinishNotificationUrl("http://uat-studentzone-ngasce.nmims.edu/exam/endMettlTest");
		        bean.setTestGradedNotificationUrl("http://uat-studentzone-ngasce.nmims.edu/exam/endMettlTest");
		        bean.setTestResumeEnabledForExpiredTestURL("http://uat-studentzone-ngasce.nmims.edu/exam/startMettlTest");
		        
		        scPayLoad = gson.toJson(bean);
		        unixTime = currentTimeLong();
		        asgnStr = sha1(verb+url+"\n"+mettlPublicKey+"\n"+scPayLoad+"\n"+unixTime, mettlPrivateKey);
		        asgnStr = URLEncoder.encode(asgnStr,"UTF-8");
		        scPayLoad = URLEncoder.encode(scPayLoad, "UTF-8");
		        bodyAsStr = "ak="+mettlPublicKey + "&asgn="+asgnStr+ "&sc="+scPayLoad+ "&ts="+unixTime;
		        entity = new HttpEntity<String>(bodyAsStr, headers);
		        response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
		        responseJsonObj = new JsonParser().parse(response.getBody()).getAsJsonObject();
		        
		        if(null != responseJsonObj.get("status")) {
		        	if("success".equalsIgnoreCase(responseJsonObj.get("status").getAsString())) {
		        		dbBean = new MettlScheduleBean();
		        		dbBean.setAssessmentId(responseJsonObj.get("createdSchedule").getAsJsonObject().get("assessmentId").getAsString());
		        		dbBean.setScheduleId(responseJsonObj.get("createdSchedule").getAsJsonObject().get("id").getAsString());
		        		dbBean.setScheduleName(responseJsonObj.get("createdSchedule").getAsJsonObject().get("name").getAsString());
		        		dbBean.setScheduleAccessKey(responseJsonObj.get("createdSchedule").getAsJsonObject().get("accessKey").getAsString());
		        		dbBean.setScheduleAccessURL(responseJsonObj.get("createdSchedule").getAsJsonObject().get("accessUrl").getAsString());
		        		dbBean.setScheduleStatus(responseJsonObj.get("createdSchedule").getAsJsonObject().get("status").getAsString());
		        		dbBean.setActive(CONSTANT_Y);
		        		dbBean.setStartTime(queryBean.getDate() + " " + queryBean.getStartTime());
		        		dbBean.setEndTime(queryBean.getDate() + " " + queryBean.getEndTime());
		        		dbBean.setCreatedBy(userId);
		        		dbBean.setLastModifiedBy(userId);
		        		mettlDAO.saveSchedule(dbBean, userId);
		        		rowsProcessed++;
		        		dbBean = null;
		        	} else if("error".equalsIgnoreCase(responseJsonObj.get("status").getAsString())) {
		        		isError = Boolean.TRUE;
		        		message = "Failure at Row " + (y+1) + " with ";
		        		message += responseJsonObj.get("error").getAsJsonObject().get("code").getAsString();
		        		message += " - " + responseJsonObj.get("error").getAsJsonObject().get("message").getAsString();
		        		break;
		        	}
		        }
		        if(isError) {
		        	responseHash.put("status", "error");
		    		responseHash.put("message", message);
		        	break;
		        }
		        break;
	        }
	        if(!isError) {
	        	responseHash.put("status", "error");
	    		responseHash.put("message", "Total Schedule(s) created : "+rowsProcessed);
	        }
		} catch (Exception e) {
			responseHash.put("status", "error");
			responseHash.put("message","Schedule(s) creation Failed with " + e.getMessage());
			
		} finally {
			assessmentList.clear();
			headers.clear();
			assessmentList = null;
			gson = null;
			responseJsonObj = null;
			restTemplate = null;
			entity = null;
			response = null;
			headers = null;
			unixTime = null;
			asgnStr = null;
			scPayLoad = null;
			verb = null;
			sourceAppName = null;
			url = null;
			bean = null;
			dbBean = null;
			queryBean = null;
		}
		return responseHash;
	}*/

	
	public JsonObject getSchedulesFromAssessmentId(int assessmentId) {
		try {
			String url = baseUrl + "assessments/" + assessmentId + "/schedules";
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
	        String unixTime = String.valueOf(timestamp.getTime() / 1000);
			int limit = 100;
			//String generatedRequestSignature = this.generateSignature("GET", url, limit,unixTime);
			String urlParam = "GET" + url + "\n" + this.publicKey + "\n" + unixTime;
			String generatedRequestSignature = this.sha256(urlParam);
			if(generatedRequestSignature == null) {
				return null;
			}
			String parameter = "?ak=" + publicKey + "&asgn=" + URLEncoder.encode(generatedRequestSignature) + "&ts=" + unixTime;
			HttpHeaders headers =  this.getHeaders();
			RestTemplate restTemplate = new RestTemplate();
			HttpEntity<String> entity = new HttpEntity<String>("",headers);
			ResponseEntity<String> response = restTemplate.exchange(url + parameter, HttpMethod.GET, entity, String.class);
			JsonObject jsonResponse = new JsonParser().parse(response.getBody()).getAsJsonObject();
			return jsonResponse;
		}
		catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}
	
	private static String sha1(String s, String keyString) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec key = new SecretKeySpec((keyString).getBytes("UTF-8"), "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(key);
        byte[] bytes = mac.doFinal(s.getBytes("UTF-8"));
        return new String(Base64.getEncoder().encodeToString(bytes));
    }
	
	public JsonObject getStudentsTestStatusAllCandidatesForASchedule(String accessKey) throws InvalidKeyException, UnsupportedEncodingException, NoSuchAlgorithmException {
		
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String ts = String.valueOf(timestamp.getTime() / 1000);
        String verb = "GET";
        String uri = baseUrl +"schedules/" + accessKey + "/candidates";
        String value = verb + uri + "\n" + publicKey  +"\n"+ 500 + "\n" + ts;
        String asgn = sha1(value, privateKey);
        uri=uri+"?ak=" + publicKey + "&asgn=" + asgn +"&limit=500&ts=" + ts;
		HttpHeaders headers =  this.getHeaders();
		RestTemplate restTemplate = new RestTemplate();
		HttpEntity<String> entity = new HttpEntity<String>("",headers);
		ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
		JsonObject jsonResponse = new JsonParser().parse(response.getBody()).getAsJsonObject();
		return jsonResponse;
	}
	
	
	public String addExcelMarksIntoPortal(TEEResultBean teeResultBean) {
		int FIRST_NAME = 0;
		int LAST_NAME = 1;
		int SAP_ID = 2;
		int SCORE = 3;
		int MAX_SCORE = 4;
		
		ByteArrayInputStream bis = new ByteArrayInputStream(teeResultBean.getFileData().getBytes());
		Workbook workbook;
		try {
			if (teeResultBean.getFileData().getOriginalFilename().endsWith("xls")) {
				workbook = new HSSFWorkbook(bis);
			} else if (teeResultBean.getFileData().getOriginalFilename().endsWith("xlsx")) {
				workbook = new XSSFWorkbook(bis);
			} else {
				throw new IllegalArgumentException("Received file does not have a standard excel extension.");
			}
			
			XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();
			
			int i = 0;
			//Skip first row since it contains column names, not data.
			if(rowIterator.hasNext()){
				Row row = rowIterator.next();
			}
			ArrayList<TEEResultBean> teeResultBeanList = new ArrayList<TEEResultBean>();
			while(rowIterator.hasNext()) {
				i++;
				Row row = rowIterator.next();
				if(row!=null){
					row.getCell(FIRST_NAME, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(LAST_NAME, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(SAP_ID, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(SCORE, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(MAX_SCORE, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					
					TEEResultBean resultBean = new TEEResultBean();
					String sap_id = row.getCell(SAP_ID, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String first_name = row.getCell(FIRST_NAME, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String last_name = row.getCell(LAST_NAME, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String score = row.getCell(SCORE, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String max_score = row.getCell(MAX_SCORE, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					
					
					if(score == null || score.isEmpty() || score.trim() == "") {
						break;
					}
					
					resultBean.setSapId(sap_id);
					resultBean.setStudentname(first_name + " " + last_name);
					resultBean.setScore(Integer.valueOf(score));
					resultBean.setMax_score(max_score);
					resultBean.setCreatedBy(teeResultBean.getCreatedBy());
					resultBean.setLastModifiedBy(teeResultBean.getLastModifiedBy());
					resultBean.setTimebound_id(teeResultBean.getTimebound_id());
					resultBean.setSchedule_id(teeResultBean.getSchedule_id());
					teeResultBeanList.add(resultBean);
				}
			}
			ExamsAssessmentsDAO examsAssessmentsDAO = (ExamsAssessmentsDAO)act.getBean("examsAssessmentsDAO");

			int count = examsAssessmentsDAO.upsertTEEMarks(teeResultBeanList);
			if(count > 0) {
				return "Successfully recording created, Total: " + count + " records";
			}
			return "Error While creating recording";
		}
		catch (Exception e) {
			// TODO: handle exception
			
			return e.getMessage();
		}
	}
	
	
	public ArrayList<MBAXMarksBean> addExcelMBAXMarksIntoPortal(MBAXMarksBean mbaxMarksBean,HttpServletRequest request) {
		int tmp_count = 0;
		int NAME = tmp_count++;
		int EMAIL = tmp_count++;
		int SCORE = tmp_count++;
		int MAXSCORE = tmp_count++;
		int REPORTLINK = tmp_count++;
		
		
		ByteArrayInputStream bis = new ByteArrayInputStream(mbaxMarksBean.getFileData().getBytes());
		Workbook workbook;
		try {
			if (mbaxMarksBean.getFileData().getOriginalFilename().endsWith("xls")) {
				workbook = new HSSFWorkbook(bis);
			} else if (mbaxMarksBean.getFileData().getOriginalFilename().endsWith("xlsx")) {
				workbook = new XSSFWorkbook(bis);
			} else {
				throw new IllegalArgumentException("Received file does not have a standard excel extension.");
			}
			
			XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();
			
			int i = 0;
			//Skip first row since it contains column names, not data.
			if(rowIterator.hasNext()){
				Row row = rowIterator.next();
			}
			ArrayList<MBAXMarksBean> mbaxResultBeanList = new ArrayList<MBAXMarksBean>();
			ExamsAssessmentsDAO examsAssessmentsDAO = (ExamsAssessmentsDAO)act.getBean("examsAssessmentsDAO");
			while(rowIterator.hasNext()) {
				i++;
				Row row = rowIterator.next();
				if(row!=null){
					row.getCell(NAME, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(EMAIL, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(SCORE, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(MAXSCORE, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(REPORTLINK, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					
					
					MBAXMarksBean resultBean = new MBAXMarksBean();
					String name = row.getCell(NAME, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String email = row.getCell(EMAIL, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String score = row.getCell(SCORE, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String max_score = row.getCell(MAXSCORE, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String report_link = row.getCell(REPORTLINK, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					
					if(score == null || score.isEmpty() || score.trim() == "") {
						break;
					}
					resultBean.setStudent_name(name);
					resultBean.setEmail(email);
					resultBean.setScore(Integer.parseInt(score));
					resultBean.setMax_score(Integer.parseInt(max_score));
					resultBean.setReport_link(report_link);
					resultBean.setSchedule_id(mbaxMarksBean.getSchedule_id());
					resultBean.setStatus("Attempted");
					resultBean.setLastModifiedBy(mbaxMarksBean.getLastModifiedBy());
					resultBean.setTimebound_id(mbaxMarksBean.getTimebound_id());		//must be set to dynamic for testing purpose set static
					resultBean.setPrgm_sem_subj_id(mbaxMarksBean.getPrgm_sem_subj_id());
					String sapid = examsAssessmentsDAO.getSapidByEmailIDAndMasterKey(email,119);
					if(sapid != null) {
						resultBean.setSapid(sapid);
					}
					mbaxResultBeanList.add(resultBean);
				}
			}
			
			logger.info("mbaxResultBeanList :  "+ mbaxResultBeanList.size());
			UpgradResultProcessingDao upgradResultProcessingDao = (UpgradResultProcessingDao) act.getBean("upgradResultProcessingDao");
			ArrayList<MBAXMarksBean> mbaxResultBeanErrorList = upgradResultProcessingDao.upsertMBAXMarks(mbaxResultBeanList,request);
			logger.info("mbaxResultBeanErrorList :  "+ mbaxResultBeanErrorList.size());
			return mbaxResultBeanErrorList;
		}
		catch (Exception e) {
			// TODO: handle exception
			logger.error("Error in addExcelMBAXMarksIntoPortal() : ", e);
			//
			return null;
		}
	}
	
	public MBAXMarksPreviewBean previewExcelMBAXMarks(MBAXMarksBean mbaxMarksBean,HttpServletRequest request) {
		int tmp_count = 0;
		int NAME = tmp_count++;
		int EMAIL = tmp_count++;
		int SCORE = tmp_count++;
		int MAXSCORE = tmp_count++;
		int REPORTLINK = tmp_count++;
		
		MBAXMarksPreviewBean responseBean = new MBAXMarksPreviewBean();
		ByteArrayInputStream bis = new ByteArrayInputStream(mbaxMarksBean.getFileData().getBytes());
		Workbook workbook;
		try {
			if (mbaxMarksBean.getFileData().getOriginalFilename().endsWith("xls")) {
				workbook = new HSSFWorkbook(bis);
			} else if (mbaxMarksBean.getFileData().getOriginalFilename().endsWith("xlsx")) {
				workbook = new XSSFWorkbook(bis);
			} else {
				throw new IllegalArgumentException("Received file does not have a standard excel extension.");
			}
			
			XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();
			
			int i = 0;
			//Skip first row since it contains column names, not data.
			if(rowIterator.hasNext()){
				Row row = rowIterator.next();
			}
			ArrayList<MBAXMarksBean> mbaxResultBeanList = new ArrayList<MBAXMarksBean>();
			int totalRowsInserted = 0;
			while(rowIterator.hasNext()) {
				i++;
				Row row = rowIterator.next();
				if(row!=null){
					row.getCell(NAME, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(EMAIL, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(SCORE, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(MAXSCORE, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(REPORTLINK, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					
					
					MBAXMarksBean resultBean = new MBAXMarksBean();
					String name = row.getCell(NAME, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String email = row.getCell(EMAIL, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String score = row.getCell(SCORE, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String max_score = row.getCell(MAXSCORE, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String report_link = row.getCell(REPORTLINK, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					
					if(score == null || score.isEmpty() || score.trim() == "") {
						break;
					}
					if(email == null || email.isEmpty() || email.trim() == "") {
						throw new IllegalArgumentException("Email id missing for student: " + name);
					}
					resultBean.setStudent_name(name);
					resultBean.setEmail(email);
					resultBean.setScore(Integer.parseInt(score));
					resultBean.setMax_score(Integer.parseInt(max_score));
					resultBean.setReport_link(report_link);
					mbaxResultBeanList.add(resultBean);
					totalRowsInserted++;
				}
			}
			responseBean.setTotalColumns(5);
			responseBean.setTotalRows(totalRowsInserted);
			responseBean.setMbaxMarksBean(mbaxResultBeanList);
			responseBean.setStatus("success");
			return responseBean;
		}
		catch (Exception e) {
			// TODO: handle exception
			logger.error("previewExcelMBAXMarks() : ",e);
			responseBean.setStatus("error");
			responseBean.setMessage(e.getMessage());
			return responseBean;
		}
	}
	
	public JsonObject getSingleStudentTestStatusForASchedule(String accessKey,String emailId) throws InvalidKeyException, UnsupportedEncodingException, NoSuchAlgorithmException {
		return getSingleStudentTestStatusForASchedule(accessKey, emailId, "false", "false");
	}
	long temp = 0;
	public JsonObject getSingleStudentTestStatusForASchedule(String accessKey,String emailId, String evalData, String questionData) throws InvalidKeyException, UnsupportedEncodingException, NoSuchAlgorithmException {
		pullTimeBoundMettlMarksLogger.info("MettlHelper.getSingleStudentTestStatusForASchedule() - START");
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String ts = String.valueOf(timestamp.getTime() / 1000);
        String verb = "GET";
        String uri = baseUrl +"schedules/" + accessKey + "/candidates/"+emailId;

        int limit = 500;
        
        if(emailId.contains("?")) 
        	return getSingleStudentTestStatusUsingURI(uri,verb,questionData, evalData, limit, ts);
        			
        String value = verb + uri
        		+ "\n" + publicKey 
        		+ "\n" + questionData 
        		+ "\n" + evalData
        		+ "\n" + limit 
        		+ "\n" + ts;
        String asgn = sha1(value, privateKey);
        
        uri = uri + ""
        		+ "?ak=" + publicKey 
        		+ "&asgn=" + asgn
        		+ "&cr=" + questionData 
        		+ "&em=" + evalData
        		+ "&limit=" + limit 
        		+ "&ts=" + ts;
        pullTimeBoundMettlMarksLogger.info("Complete URL:"+uri);

		HttpHeaders headers =  this.getHeaders();
		RestTemplate restTemplate = new RestTemplate();
		HttpEntity<String> entity = new HttpEntity<String>("",headers);

		ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
		JsonObject jsonResponse = new JsonParser().parse(response.getBody()).getAsJsonObject();
		
		pullTimeBoundMettlMarksLogger.info("response received : "+jsonResponse.toString());
		return jsonResponse;
	}
	
	
	private JsonObject getSingleStudentTestStatusUsingURI(String uri, String verb, String questionData, String evalData,
			int limit, String ts) {
		// 
		pullTimeBoundMettlMarksLogger.info("MettlHelper.getSingleStudentTestStatusUsingURI() - START Because email id has ? : {}", uri);
		
		uri = uri.replace("?", "%3F");
		
		pullTimeBoundMettlMarksLogger.info("URI after replacing ? with %3f  : {} ", uri);
		
        String value = verb + uri
        		+ "\n" + publicKey 
        		+ "\n" + questionData 
        		+ "\n" + evalData
        		+ "\n" + limit 
        		+ "\n" + ts;
        
        String asgn = null;
        
		try {
			asgn = URLEncoder.encode(sha1(value, privateKey),"UTF-8");
		} catch (InvalidKeyException | UnsupportedEncodingException | NoSuchAlgorithmException e) {
			
			pullTimeBoundMettlMarksLogger.info(
					"ERROR : while trying to get test schedule data using URI private method : {}",
					Throwables.getStackTraceAsString(e));
			
			throw new RuntimeException(e.getMessage());
		}
        
        URI final_BASE_URI = UriComponentsBuilder.fromHttpUrl(uri)
        		.queryParam("ak", publicKey)
        		.queryParam("asgn", asgn)
        		.queryParam("cr", questionData)
        		.queryParam("em", evalData)
        		.queryParam("limit", limit)
        		.queryParam("ts", ts)
        		.build(true).toUri();
        
		HttpHeaders headers =  this.getHeaders();
		RestTemplate restTemplate = new RestTemplate();
		HttpEntity<String> entity = new HttpEntity<String>("",headers);
		pullTimeBoundMettlMarksLogger.info("Complete final_URI : "+final_BASE_URI.toString());
		ResponseEntity<String> response = restTemplate.exchange(final_BASE_URI, HttpMethod.GET, entity, String.class);
		
		JsonObject jsonResponse = new JsonParser().parse(response.getBody()).getAsJsonObject();
		pullTimeBoundMettlMarksLogger.info("response received : "+jsonResponse.toString());
		return jsonResponse;
	
	}
	String replace(String arg) {
		String d = null;
		d = arg.replace("(","");
		d = d.replace(")","");
		d = d.replace("'","");
		return d;
	}

	public ArrayList<MettlResponseBean> getMarksToUpsertForProject(TEEResultBean teeResultBean) {
		int SAP_ID = 0;
		int SCORE = 1;
		int MAX_SCORE = 2;
		
		ExamsAssessmentsDAO examsAssessmentsDAO = (ExamsAssessmentsDAO)act.getBean("examsAssessmentsDAO");
		
		ByteArrayInputStream bis = new ByteArrayInputStream(teeResultBean.getFileData().getBytes());
		Workbook workbook;
		try {
			if (teeResultBean.getFileData().getOriginalFilename().endsWith("xls")) {
				workbook = new HSSFWorkbook(bis);
			} else if (teeResultBean.getFileData().getOriginalFilename().endsWith("xlsx")) {
				workbook = new XSSFWorkbook(bis);
			} else {
				throw new IllegalArgumentException("Received file does not have a standard excel extension.");
			}
			
			XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();
			
			if(rowIterator.hasNext()){
				rowIterator.next();
			}
			ArrayList<MettlResponseBean> teeResultBeanList = new ArrayList<MettlResponseBean>();
			while(rowIterator.hasNext()) {
				Row row = rowIterator.next();
				if(row!=null){
					row.getCell(SAP_ID, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(SCORE, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(MAX_SCORE, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					
					MettlResponseBean resultBean = new MettlResponseBean();
					String score = row.getCell(SCORE, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					
					if(StringUtils.isEmpty(score)) {
						break;
					}
					resultBean.setSapid(row.getCell(SAP_ID, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
					
					StudentExamBean student = examsAssessmentsDAO.getSingleStudentsData(resultBean.getSapid());
					resultBean.setStudent_name(student.getFirstName() + " " + student.getLastName());
					
					if(!StringUtils.isNumeric(score)) {
						resultBean.setTotalMarks(0);
						resultBean.setStatus(score);
					} else {
						resultBean.setTotalMarks(Integer.valueOf(score));
						resultBean.setStatus("Attempted");
					}
					resultBean.setMax_marks(row.getCell(MAX_SCORE, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
					resultBean.setCreatedBy(teeResultBean.getCreatedBy());
					resultBean.setLastModifiedBy(teeResultBean.getLastModifiedBy());
					resultBean.setTimebound_id(teeResultBean.getTimebound_id());
					teeResultBeanList.add(resultBean);
				}
			}
			return teeResultBeanList;
		} catch (Exception e) {
			
		}
		return null;
	}
	
	public static void checkAndExtendAssessmentTimeIfRequired(ExamsAssessmentsBean examsAssessmentsBean) {
		// Get Exam Star and end Times
		String exam_start_date_time = examsAssessmentsBean.getExam_start_date_time();
		String exam_end_date_time = examsAssessmentsBean.getExam_end_date_time();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
//		Start Commented by Abhay  Taking exam duration from mettl API and calculating exam end time 
		/* 
		 * // Set default duration if required int defaultDurationInMinutes = 120;
		 * 
		 * // Currently commented out the condition to change time for 100 marks/resit
		 * exams but can be changed later to auto map to schedules
		 * if(examsAssessmentsBean.getMax_score().equalsIgnoreCase("100")) {
		 * defaultDurationInMinutes = 150; return; }
		 */
//		End Commented by Abhay  Taking exam duration from mettl API and calculating exam end time 
		
		try {
			// Convert to java date for comparison
//			Date startDate = sdf.parse(exam_start_date_time);
			Date endDate = sdf.parse(exam_end_date_time);

//			Start Commented by Abhay  Taking exam duration from mettl API and calculating exam end time 			
			/*
			 * long durationInMillis = endDate.getTime() - startDate.getTime(); int
			 * durationInMinutes = (int) (durationInMillis/(60*1000));
			 * 
			 * // If duration is less than the default duration, set duration to default
			 * duration. if(durationInMinutes < defaultDurationInMinutes) {
			 * exam_end_date_time = sdf.format(DateUtils.addMinutes(startDate,
			 * defaultDurationInMinutes)); }
			 */
//			End Commented by Abhay  Taking exam duration from mettl API and calculating exam end time 			
			
			exam_end_date_time = sdf.format(DateUtils.addMinutes(endDate, examsAssessmentsBean.getDuration()));
		}catch (Exception e) {
			
		}
		
		examsAssessmentsBean.setExam_end_date_time(exam_end_date_time);
	}
	
	public JsonObject getScheduleDetailsFromAccessKey(String accessKey) {
		try {
			String url = baseUrl + "schedules/" + accessKey ;
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
	        String unixTime = String.valueOf(timestamp.getTime() / 1000);
			String urlParam = "GET" + url + "\n" + this.publicKey + "\n" + unixTime;
			String generatedRequestSignature = this.sha256(urlParam);
			if(generatedRequestSignature == null) {
				return null;
			}
			String parameter = "?ak=" + publicKey + "&asgn=" + URLEncoder.encode(generatedRequestSignature) + "&ts=" + unixTime;
			logger.info("----- URL : "+ url );
			logger.info("----- parameter : "+ parameter );
			HttpHeaders headers =  this.getHeaders();
			RestTemplate restTemplate = new RestTemplate();
			HttpEntity<String> entity = new HttpEntity<String>("",headers);
			ResponseEntity<String> response = restTemplate.exchange(url + parameter, HttpMethod.GET, entity, String.class);
			JsonObject jsonResponse = new JsonParser().parse(response.getBody()).getAsJsonObject();
			return jsonResponse;
		}
		catch (Exception e) {
			// TODO: handle exception
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			logger.error("Exception Message : "+errors.toString());
			return null;
		}
	}
	
	public JsonObject getTestStatusForAllInSchedule(String accessKey, String evalData, String questionData, int limit) throws InvalidKeyException, UnsupportedEncodingException, NoSuchAlgorithmException {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String ts = String.valueOf(timestamp.getTime() / 1000);
        String verb = "GET";
        String uri = baseUrl +"schedules/" + accessKey + "/candidates";

        
        String value = verb + uri
        		+ "\n" + publicKey 
        		+ "\n" + questionData 
        		+ "\n" + evalData
        		+ "\n" + limit 
        		+ "\n0"  
        		+ "\ntestStartTime"
        		+ "\ndesc"
        		+ "\n" + ts;
        String asgn = sha1(value, privateKey);
        
        uri = uri + ""
        		+ "?ak=" + publicKey 
        		+ "&asgn=" + asgn
        		+ "&cr=" + questionData 
        		+ "&em=" + evalData
        		+ "&limit=" + limit 
        		+ "&offset=0"  
        		+ "&sort=testStartTime"  
        		+ "&sort_order=desc"  
        		+ "&ts=" + ts;
        fetchMettlAllCandidateTestResult.info("URL:"+uri);

		HttpHeaders headers =  this.getHeaders();
		RestTemplate restTemplate = new RestTemplate();
		HttpEntity<String> entity = new HttpEntity<String>("",headers);

		ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
		JsonObject jsonResponse = new JsonParser().parse(response.getBody()).getAsJsonObject();
		return jsonResponse;
	}
	
	public JsonObject getTestStatusForAllInSchedule(String uri){
		fetchMettlAllCandidateTestResult.info("URL:"+uri);

		HttpHeaders headers =  this.getHeaders();
		RestTemplate restTemplate = new RestTemplate();
		HttpEntity<String> entity = new HttpEntity<String>("",headers);

		ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
		JsonObject jsonResponse = new JsonParser().parse(response.getBody()).getAsJsonObject();
		return jsonResponse;
	}
	
	public void registerCandidatesForTimeBound(ExamsAssessmentsDAO examDao,ArrayList<MettlRegisterCandidateBean> candidatesList,ExamsAssessmentsBean examBean,String noImageUrl,String subjectName,String endTime)
	{
		MettlRegisterCandidateBean[] arr=null;
		MettlRegisterCandidateBean candidate=null;
		int retryForImage=0;
		int retryAtRemoteServer=0;
		int rowsProcessed=0;
		int rowsWithError=0;
		int retriedRows=0;
		int totalCandidates=0;
		Boolean retryAtMettl=Boolean.FALSE;
		Boolean configErrorMettl=Boolean.FALSE;
		Boolean finalSuccess=Boolean.TRUE;
		Boolean openLinkFlag = Boolean.FALSE;
		String message=null;
		String timeboundUrlMettl = null;
		String scheduleAccessURL = null;
		
		try
		{
			totalCandidates=candidatesList.size();
			logger.info("Total Candidates to be Registered : " + totalCandidates);
			for(int i=0;i<=candidatesList.size()-1;i++)
			{
				message = "REGISTER CANDIDATE -(Total) ("+ totalCandidates
						+ ") (S, F, R) (" + rowsProcessed + ", " + rowsWithError + ", " + retriedRows + ") - " + i;
				logger.info(message);
				
				candidate=candidatesList.get(i);
				scheduleAccessURL = examBean.getSchedule_accessUrl();
				openLinkFlag = candidate.getOpenLinkFlag();
				if(openLinkFlag)
				{
					candidate.setCandidateImage(noImageUrl);
					candidate.setRegistrationImage(noImageUrl);
				}
				
				retryForImage=1;
				retryAtRemoteServer=1;
				retryAtMettl=Boolean.FALSE;
				
				do {					
					arr=registerCandidate(candidate, examBean.getSchedule_accessKey(),"0",false);
					if(arr.length==1)
					{
						if(null != arr[0].getStatus() && arr[0].getStatus().equalsIgnoreCase("error"))
						{
							logger.error("Error: " + i + " - " + arr[0].getMessage());
							
							message = arr[0].getMessage().replace("\"", "");
							if (message.contains("SHA Error") || message.contains("E000")
									|| message.contains("E607") || message.contains("E401")) {
								// E000-Some Error Occurred, E607-Candidate registration limit exceeded,
								// E401-Authentication failed/Signature mismatch
								configErrorMettl = Boolean.TRUE;
								finalSuccess = Boolean.FALSE;
								break;
							}
							
							saveRegistrationStatus(examDao,examBean,candidate,arr[0].getStatus(),
									(arr[0].getStatus() + "|" + replace(arr[0].getMessage())),null,null,subjectName,endTime);
							finalSuccess = Boolean.FALSE;
							rowsWithError++;
							
							if (message.contains("E028")) {
								if (retryForImage != 0) {
									candidate.setCandidateImage(noImageUrl);
									candidate.setRegistrationImage(noImageUrl);
									retryAtMettl = Boolean.TRUE;
									retryForImage = retryForImage - 1;
									retriedRows = retriedRows + 1;
									message = "REGISTER CANDIDATE -(Total) ("
											+ totalCandidates + ") (S, F, R) (" + rowsProcessed + ", "
											+ rowsWithError + ", " + retriedRows + ") - " + i + " -RETRY-";
									logger.info(message);
									
								} else {
									retryAtMettl = Boolean.FALSE;
								}
							} else {
								if (retryAtRemoteServer != 0) {
									retryAtMettl = Boolean.TRUE;
									retryAtRemoteServer = retryAtRemoteServer - 1;
									retriedRows = retriedRows + 1;
									message = "REGISTER CANDIDATE -(Total) (" 
											+ totalCandidates + ") (S, F, R) (" + rowsProcessed + ", "
											+ rowsWithError + ", " + retriedRows + ") - " + i + " -RETRY-";
									logger.info(message);
									
								} else {
									retryAtMettl = Boolean.FALSE;
								}
							}
						}
						else
						{
							message = (arr[0].getStatus() + "|" + replace(arr[0].getMessage()));
							if (openLinkFlag || retryAtMettl) {
								timeboundUrlMettl = scheduleAccessURL;
							} else {
								timeboundUrlMettl = arr[0].getUrl();
							}
							saveRegistrationStatus(examDao,examBean,candidate,arr[0].getStatus(),
									message,arr[0].getUrl(),timeboundUrlMettl,subjectName,endTime);
							rowsProcessed++;
							retryAtMettl = Boolean.FALSE;
						}
					}
				}while(retryAtMettl);
				
				if(configErrorMettl)
				{
					message = "ABRUPT STOP................................";
					logger.info(message);
					
					message = "REGISTER CANDIDATE -(Total) ("
							+ totalCandidates + ") (S, F, R) (" + rowsProcessed + ", "
							+ rowsWithError + ", " + retriedRows + ") - " + i + " -STOP-";
					logger.info(message);
					
					message = "ABRUPT STOP................................";
					logger.info(message);
					break;
				}
			}
		}
		finally
		{
			if(finalSuccess)
			{
				message = "Total Candidates Registered : Count (Success/Failure/Retried) : ("
						+ rowsProcessed + "/" + rowsWithError + "/" + retriedRows + ")";
				logger.info(message);
			}
			else
			{
				message = "Partial Success/Failure/Retried : Count (Success/Failure/Retried) : ("
						+ rowsProcessed + "/" + rowsWithError + "/" + retriedRows + ")";
				logger.info(message);
			}
			
			message = null;
		}
		
	}
	
	public void saveRegistrationStatus(ExamsAssessmentsDAO examDao,ExamsAssessmentsBean examBean,MettlRegisterCandidateBean candidate,String status,String message,String urlSuccess,String urlGeneral,String subjectName,String endTime)
	{
		if(status.equalsIgnoreCase("error"))
		{
			examDao.saveTimeBoundCandidateRegisteredInfo(candidate.getSapId(),examBean.getSchedule_accessKey(), 1, message,examBean.getCreatedBy(),null);
		}
		else
		{
			examDao.saveCandidateRegisteredInfoAndScheduleInfo(candidate,examBean,0,message,urlSuccess,urlGeneral,subjectName,endTime);
		}
	}
	
	public void registerCandidatesForTimeBoundForMBAX(ExamsAssessmentsDAO examDao,
			ArrayList<MettlRegisterCandidateBean> candidatesList, ExamsAssessmentsBean examBean, String noImageUrl,
			String subjectName, String endTime) {

		MettlRegisterCandidateBean[] arr=null;
		MettlRegisterCandidateBean candidate=null;
		int retryForImage=0;
		int retryAtRemoteServer=0;
		int rowsProcessed=0;
		int rowsWithError=0;
		int retriedRows=0;
		int totalCandidates=0;
		Boolean retryAtMettl=Boolean.FALSE;
		Boolean configErrorMettl=Boolean.FALSE;
		Boolean finalSuccess=Boolean.TRUE;
		Boolean openLinkFlag = Boolean.FALSE;
		String message=null;
		String timeboundUrlMettl = null;
		String scheduleAccessURL = null;
		
		try
		{
			totalCandidates=candidatesList.size();
			logger.info("Total Candidates to be Registered : " + totalCandidates);
			for(int i=0;i<=candidatesList.size()-1;i++)
			{
				message = "REGISTER CANDIDATE -(Total) ("+ totalCandidates
						+ ") (S, F, R) (" + rowsProcessed + ", " + rowsWithError + ", " + retriedRows + ") - " + i;
				logger.info(message);
				
				candidate=candidatesList.get(i);
				scheduleAccessURL = examBean.getSchedule_accessUrl();
				openLinkFlag = candidate.getOpenLinkFlag();
				if(openLinkFlag)
				{
					candidate.setCandidateImage(noImageUrl);
					candidate.setRegistrationImage(noImageUrl);
				}
				
				retryForImage=1;
				retryAtRemoteServer=1;
				retryAtMettl=Boolean.FALSE;
				
				do {					
					arr=registerCandidate(candidate, examBean.getSchedule_accessKey(),"0",false);
					if(arr.length==1)
					{
						if(null != arr[0].getStatus() && arr[0].getStatus().equalsIgnoreCase("error"))
						{
							logger.error("Error: " + i + " - " + arr[0].getMessage());
							
							message = arr[0].getMessage().replace("\"", "");
							if (message.contains("SHA Error") || message.contains("E000")
									|| message.contains("E607") || message.contains("E401")) {
								// E000-Some Error Occurred, E607-Candidate registration limit exceeded,
								// E401-Authentication failed/Signature mismatch
								configErrorMettl = Boolean.TRUE;
								finalSuccess = Boolean.FALSE;
								break;
							}
							
							saveRegistrationStatusForMBAX(examDao,examBean,candidate,arr[0].getStatus(),
									(arr[0].getStatus() + "|" + replace(arr[0].getMessage())),null,null,subjectName,endTime);
							finalSuccess = Boolean.FALSE;
							rowsWithError++;
							
							if (message.contains("E028")) {
								if (retryForImage != 0) {
									candidate.setCandidateImage(noImageUrl);
									candidate.setRegistrationImage(noImageUrl);
									retryAtMettl = Boolean.TRUE;
									retryForImage = retryForImage - 1;
									retriedRows = retriedRows + 1;
									message = "REGISTER CANDIDATE -(Total) ("
											+ totalCandidates + ") (S, F, R) (" + rowsProcessed + ", "
											+ rowsWithError + ", " + retriedRows + ") - " + i + " -RETRY-";
									logger.info(message);
									
								} else {
									retryAtMettl = Boolean.FALSE;
								}
							} else {
								if (retryAtRemoteServer != 0) {
									retryAtMettl = Boolean.TRUE;
									retryAtRemoteServer = retryAtRemoteServer - 1;
									retriedRows = retriedRows + 1;
									message = "REGISTER CANDIDATE -(Total) (" 
											+ totalCandidates + ") (S, F, R) (" + rowsProcessed + ", "
											+ rowsWithError + ", " + retriedRows + ") - " + i + " -RETRY-";
									logger.info(message);
									
								} else {
									retryAtMettl = Boolean.FALSE;
								}
							}
						}
						else
						{
							message = (arr[0].getStatus() + "|" + replace(arr[0].getMessage()));
							if (openLinkFlag || retryAtMettl) {
								timeboundUrlMettl = scheduleAccessURL;
							} else {
								timeboundUrlMettl = arr[0].getUrl();
							}
							saveRegistrationStatusForMBAX(examDao,examBean,candidate,arr[0].getStatus(),
									message,arr[0].getUrl(),timeboundUrlMettl,subjectName,endTime);
							rowsProcessed++;
							retryAtMettl = Boolean.FALSE;
						}
					}
				}while(retryAtMettl);
				
				if(configErrorMettl)
				{
					message = "ABRUPT STOP................................";
					logger.info(message);
					
					message = "REGISTER CANDIDATE -(Total) ("
							+ totalCandidates + ") (S, F, R) (" + rowsProcessed + ", "
							+ rowsWithError + ", " + retriedRows + ") - " + i + " -STOP-";
					logger.info(message);
					
					message = "ABRUPT STOP................................";
					logger.info(message);
					break;
				}
			}
		}
		finally
		{
			if(finalSuccess)
			{
				message = "Total Candidates Registered : Count (Success/Failure/Retried) : ("
						+ rowsProcessed + "/" + rowsWithError + "/" + retriedRows + ")";
				logger.info(message);
			}
			else
			{
				message = "Partial Success/Failure/Retried : Count (Success/Failure/Retried) : ("
						+ rowsProcessed + "/" + rowsWithError + "/" + retriedRows + ")";
				logger.info(message);
			}
			
			message = null;
		}	
	}
	
	public void saveRegistrationStatusForMBAX(ExamsAssessmentsDAO examDao,ExamsAssessmentsBean examBean,MettlRegisterCandidateBean candidate,String status,String message,String urlSuccess,String urlGeneral,String subjectName,String endTime)
	{
		if(status.equalsIgnoreCase("error"))
		{
			examDao.saveTimeBoundCandidateRegisteredInfoForMBAX(candidate.getSapId(),examBean.getSchedule_accessKey(), 1, message,examBean.getCreatedBy(),null);
		}
		else
		{
			examDao.saveCandidateRegisteredInfoAndScheduleInfoForMBAX(candidate,examBean,0,message,urlSuccess,urlGeneral,subjectName,endTime);
		}
	}
	
	public JsonObject getTestStatusMbaWX(String scheduleKey,String emailId) {
		try {
			String url = "https://api.mettl.com/v2/schedules/"+ scheduleKey +"/candidates/" + emailId;
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
	        String unixTime = String.valueOf(timestamp.getTime() / 1000);
	        String urlParam = "GET" + url 
	        		+ "\n" + publicKey 
	        		+ "\n" + unixTime;
	        String generatedRequestSignature = this.sha1(urlParam,privateKey);
	        if(generatedRequestSignature == null) {
				return null;
			}
	        
	        String parameter = "?ak=" + publicKey 
					+ "&asgn=" + URLEncoder.encode(generatedRequestSignature) 
					+ "&ts=" + unixTime;
	        HttpHeaders headers =  this.getHeaders();
			RestTemplate restTemplate = new RestTemplate();
			HttpEntity<String> entity = new HttpEntity<String>("",headers);
			ResponseEntity<String> response = restTemplate.exchange(url + parameter, HttpMethod.GET, entity, String.class);
			JsonObject jsonResponse = new JsonParser().parse(response.getBody()).getAsJsonObject();
			return jsonResponse;
		}
		catch (Exception e) {
			// TODO: handle exception
			
			return null;
		}
	}
}