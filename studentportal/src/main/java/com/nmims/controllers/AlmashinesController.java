package com.nmims.controllers;
 
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import javax.crypto.Cipher; 
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.codec.binary.Hex;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.HttpClientErrorException; 
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.nmims.beans.ResponseStudentPortalBean;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.daos.PortalDao;
import com.nmims.daos.ServiceRequestDao;

import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustStrategy; 
import org.apache.http.impl.client.HttpClients; 
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
 
import javax.net.ssl.SSLContext;

import java.net.URI;
import java.security.cert.X509Certificate;
 
@Controller
public class AlmashinesController extends BaseController{
	private static final Logger logger = LoggerFactory.getLogger(AlmashinesController.class);
    //private String API_KEY="2afb87bfae051dc";
	//private String API_SECRET="d425a1db696c7f4fb72f0544b7ca3accDKc4aecf80c7XAsd";
	@Value( "${ALMASHINES_API_KEY}" )
	private String API_KEY;
	@Value( "${ALMASHINES_API_SECRET}" )
	private String API_SECRET;
	@Autowired
	ApplicationContext act;
	@Value("${SERVER}")
	private String SERVER;

	@Value("${ENVIRONMENT}")
	private String ENVIRONMENT;
	
	@Autowired 
	ServiceRequestDao serviceRequestDao;
	public SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
	
	private static final String almashineUrl = "/student/welcomeToAlmashines";
	
	public SecretKeySpec generateMySQLAESKey (String key,String encoding) throws Exception {
    
		final byte[] finalKey = new byte[16];
		int i = 0;
		for(byte b : key.getBytes(encoding))
			finalKey[i++%16] ^= b;			
		return new SecretKeySpec(finalKey, "AES");
	}
	
	@RequestMapping(value = "/student/almashinesLogin", method = RequestMethod.GET) 
	public RedirectView almashinesLogin(HttpServletRequest request) throws Exception {
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		
        long unixTime = Instant.now().getEpochSecond();
        logger.info("unixTime before:"+unixTime);
		//unixTime=Math.subtractExact(unixTime,15); 
		//logger.info("unixTime after:"+unixTime);
        String plainText = null;
        PortalDao pDao = (PortalDao)act.getBean("portalDAO");
        StudentStudentPortalBean student = (StudentStudentPortalBean) request.getSession().getAttribute("student_studentportal");
        Integer uid = student.getAlmashinesId();
        if(uid==null) {
        	//return almashinesCreateAccount(request);
        	request.setAttribute("error", "true");
        	request.setAttribute("errorMessage", "Not Eligible");
        	String url = SERVER+almashineUrl;
        	return getRedirectUrl(url);
        }else {
        	plainText = "{\"uid\":"+uid+",\"timestamp\":"+unixTime+"}";
        }
		
        ResponseEntity<String> response = null;
		try {
			final Cipher encryptCipher = Cipher.getInstance("AES");	 
			encryptCipher.init(Cipher.ENCRYPT_MODE, generateMySQLAESKey(API_SECRET,"UTF-8"));
			String encryptedText =new String(Hex.encodeHex(encryptCipher.doFinal(plainText.getBytes("UTF-8"))));
			logger.info("almashines apikey:"+API_KEY);
			logger.info("almashines apisecret:"+API_SECRET);
			MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
			body.add("apikey", API_KEY);
			body.add("apisecret", API_SECRET);
			body.add("data", encryptedText);
			String url = "https://www.almashines.com/data/api/loginUser";
			HttpEntity<MultiValueMap<String, Object>> requestEntity= new HttpEntity<>(body, headers);
			TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
		    SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
		    SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);
		    //CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();
		    CloseableHttpClient httpClient = HttpClients.custom()
		    .setHostnameVerifier(new AllowAllHostnameVerifier())
		    .setSSLSocketFactory(csf)
		    .build();
		    HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		    requestFactory.setHttpClient(httpClient); 
			RestTemplate restTemplate = new RestTemplate();
			response = restTemplate.postForEntity(url, requestEntity, String.class);
			logger.info("almashines response:"+response);
		}  catch (Exception e) { 
			logger.info("error:"+e.getMessage());
			
		}
		String url = response.getHeaders().getLocation().toString();
	
		return getRedirectUrl(url);
//		return new ModelAndView("redirect:" + response.getHeaders().getLocation());
        
	}
	
	@RequestMapping(value = "/almashinesCreateAccount", method = RequestMethod.GET) 
	public JSONObject almashinesCreateAccount(StudentStudentPortalBean student) throws Exception {
		//StudentBean student = (StudentBean) request.getSession().getAttribute("student_studentportal");
		ModelAndView  modelnView = new ModelAndView("jsp/AlmashinesWelcomePage");
		
		JSONObject response_body = new JSONObject();
		
		 
		  if (!"PROD".equalsIgnoreCase(ENVIRONMENT)) { logger.
		  info("Couldnot create Almashines Account since this is not production "
		  +SERVER);
		  
		  System.out.
		  println("Couldnot create Almashines Account since this is not production "+
		  SERVER); return response_body; }
		 
		 
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		String yop="";
		if(student.getRole().equalsIgnoreCase("student")) {
			yop=student.getValidityEndYear();
		}else if(student.getRole().equalsIgnoreCase("alumni")) {
			if(student.getProgram().equalsIgnoreCase("MBA - WX")) {
				yop = pDao.getYearOfPassingForMbawx(student.getSapid());
			}else if(student.getProgram().equalsIgnoreCase("MBA - X")) {
				yop = pDao.getYearOfPassingForMbax(student.getSapid());
			}else {
				yop = pDao.getYearOfPassing(student.getSapid());
			} 
		}else {
			throw new Exception("Error");
		}
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		String url = "https://www.almashines.com/data/api/createUser";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		
		String degree = pDao.getProgramNameFromCpsId(student.getConsumerProgramStructureId());
			
		try {
			MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
			body.add("userData[first_name]", student.getFirstName());
			body.add("userData[last_name]", student.getLastName());
			body.add("userData[department]", student.getProgram());
			body.add("userData[yop]", yop);
			body.add("userData[college]", "NMIMS");
			body.add("userData[email]", student.getEmailId());
			body.add("userData[role]", student.getRole());
			body.add("userData[degree]", degree);
			body.add("userData[yoj]", student.getEnrollmentYear());
			body.add("send_mail", false);
			body.add("apikey", API_KEY);
			body.add("apisecret", API_SECRET);
			logger.info("body : "+body);
			
			  HttpEntity<MultiValueMap<String, Object>> requestEntity= new
			  HttpEntity<>(body, headers);
			  
			  RestTemplate restTemplate = new RestTemplate(); ResponseEntity<String>
			  response = restTemplate.postForEntity(url, requestEntity, String.class);
			  
			  response_body = new JSONObject(response.getBody());
			  logger.info("response_body : "+response_body);	

		} catch (HttpClientErrorException e) {
			
		}
		finally { 
			httpClient.close();
		} 

		/*
		 * modelnView.addObject("error", "true"); modelnView.addObject("errorMessage",
		 * "Error redirecting to AlmaShines! Please try again later!");
		 */
		return response_body;
	}  
	@RequestMapping(value = "/student/welcomeToAlmashines", method = RequestMethod.GET) 
	public ModelAndView welcomeToAlmashines(HttpServletRequest request) throws Exception {
		
		ModelAndView  modelnView = new ModelAndView("jsp/AlmashinesWelcomePage");
		return modelnView;
	}
 

	@RequestMapping(value = "/CreateAlmaAccountsJob", method = RequestMethod.GET)
	public ResponseEntity<ResponseStudentPortalBean> createAlmaAccountsJob(HttpServletRequest request) throws Exception {
 
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json"); 
		
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		ResponseStudentPortalBean response = new ResponseStudentPortalBean();
		ArrayList<StudentStudentPortalBean> students = pDao.getPendingStudentsToRegisterInAlma();
		logger.info("students not in almashines "+students.size()); 
		ArrayList<StudentStudentPortalBean> studentsToCreateAccount = new ArrayList<StudentStudentPortalBean>();
		ArrayList<String> subjectsPassed = new ArrayList<String>();
		for(StudentStudentPortalBean student : students) {
			
			try {
				String programCleared =student.getProgramCleared();  
				String currentSem=null;
				try {
					currentSem = pDao.getStudentsMostRecentRegistrationData(student.getSapid()).getSem(); 
					subjectsPassed = pDao.getPassSubjectsNamesForAStudent(student.getSapid());
					
				} catch (Exception e) {  
				} 
				if(currentSem != null) {
					boolean isValid = isStudentValid(student,student.getSapid());
					boolean validityExpired = !isValid; 
					//student.setValidityExpired(validityExpired);
					int passedSubjectCount=0;
					
			        //get passed subjects count for lateral
					if(student.getIsLateral().equalsIgnoreCase("Y") && student.getPreviousStudentId()!=null) {
						passedSubjectCount = pDao.getPassedSubjectForLateral(student);
					}else {
						passedSubjectCount=subjectsPassed.size();
					}
					//check for alumni
					if(programCleared.equalsIgnoreCase("Y") ||
					  (passedSubjectCount>=15 && validityExpired) ){
	                     student.setRole("alumni"); 
						 studentsToCreateAccount.add(student);
					 }
					//check for student
					else if(passedSubjectCount>=15 && currentSem.equalsIgnoreCase("4") ) {
						student.setRole("student");
						studentsToCreateAccount.add(student);
					} 
					
				}
			} catch (ParseException e) {
				logger.info("bean creation failed for sapid:"+student.getSapid());
			}
			
		}
		logger.info("studentBean list ready for account creation.");
		logger.info("size:"+studentsToCreateAccount.size());
		
		ArrayList<String> emailAlreadyExistsStudent = new ArrayList<String>();
		HashMap<String,String> accountCreationfailedList = new HashMap<String,String>();
		int accountCreationSuccessCount=0;
		
		logger.info("Started Account Creation...");
		try { 
			for(StudentStudentPortalBean student : studentsToCreateAccount) {
				
				try {
					JSONObject response_body = almashinesCreateAccount(student);
					logger.info("sapid : "+student.getSapid());
					if(response_body.get("success").equals(-10)){
						logger.info("Email Already exists "); 
						emailAlreadyExistsStudent.add(student.getSapid());
					}
					
					else if(response_body.get("success").equals(1)){
						logger.info("Account created");
						accountCreationSuccessCount++;
						Integer  uid = Integer.parseInt(response_body.get("uid").toString());
						pDao.SaveAlmashinesId(student.getSapid(),uid); 
					} else {
						accountCreationfailedList.put(student.getSapid(),response_body.toString());
						logger.info("AlmaShines Error returned ");
						logger.info("response_body : "+response_body);
					}
				} catch (Exception e) {
					accountCreationfailedList.put(student.getSapid(),"neither Alumni nor Senior Student");
				} 
				
			}
			logger.info("Email Already Exists Students");
			logger.info("emailAlreadyExistsStudent : "+emailAlreadyExistsStudent);
			
			logger.info("Account Creation Failed Students");
			logger.info("accountCreationfailedList : "+accountCreationfailedList);
			
			logger.info("Account Created Successfully. Count:"+accountCreationSuccessCount); 
		} catch (Exception e) {
			logger.info(e.getMessage());
			logger.info("Error. Account creation stopped. created: "+accountCreationSuccessCount+" accounts" );
		} 
		response.setStatus("completed.");
		return new ResponseEntity<ResponseStudentPortalBean>(response,headers, HttpStatus.OK);
	}  
	@RequestMapping(value = "/CreateAlmaAccountsJobForMbawx", method = RequestMethod.GET)
	public ResponseEntity<ResponseStudentPortalBean> CreateAlmaAccountsJobForMbawx(HttpServletRequest request) throws Exception {
 
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json"); 
		
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		ResponseStudentPortalBean response = new ResponseStudentPortalBean();
		ArrayList<StudentStudentPortalBean> students = pDao.getPendingStudentsToRegisterInAlmaMbawx();
		logger.info("students not in almashines "+students.size()); 
		ArrayList<StudentStudentPortalBean> studentsToCreateAccount = new ArrayList<StudentStudentPortalBean>();
		ArrayList<String> subjectsPassed = new ArrayList<String>();
		for(StudentStudentPortalBean student : students) {
			
			try {
				String programCleared =student.getProgramCleared();  
				String currentSem=null;
				try {
					currentSem = pDao.getStudentsMostRecentRegistrationData(student.getSapid()).getSem(); 
					subjectsPassed = pDao.getPassSubjectsForMbawxStudent(student.getSapid());
					
				} catch (Exception e) {  
				} 
				if(currentSem != null) {
					boolean isValid = isStudentValid(student,student.getSapid());
					boolean validityExpired = !isValid; 
			        //get passed subjects count
					int passedSubjectCount=subjectsPassed.size(); 
						
					//check for alumni
					if(programCleared.equalsIgnoreCase("Y") ||
					  ((passedSubjectCount>=21 ) && currentSem.equalsIgnoreCase("5")) ){
						
	                     student.setRole("alumni"); 
						 studentsToCreateAccount.add(student);
					 } 
					
				}
			} catch (ParseException e) {
				logger.info("bean creation failed for sapid:"+student.getSapid());
			}
			
		}
		logger.info("studentBean list ready for account creation.");
		logger.info("size:"+studentsToCreateAccount.size());
		
		ArrayList<String> emailAlreadyExistsStudent = new ArrayList<String>();
		HashMap<String,String> accountCreationfailedList = new HashMap<String,String>();
		int accountCreationSuccessCount=0;
		
		logger.info("Started Account Creation...");
		
		
		  try { for (StudentStudentPortalBean student : studentsToCreateAccount) {
		  
					try {
						JSONObject response_body = almashinesCreateAccount(student);
						logger.info("sapid : " + student.getSapid());
						if (response_body.get("success").equals(-10)) {
							logger.info("Email Already exists ");
							emailAlreadyExistsStudent.add(student.getSapid());
						}
						if (response_body.get("success").equals(-5)) {
							logger.info("uid:" + response_body.get("uid") + " sapid:" + student.getSapid());

						}
					
						else if (response_body.get("success").equals(1)) {
							logger.info("Account created");
							accountCreationSuccessCount++;
							Integer uid = (Integer) response_body.get("uid");
							pDao.SaveAlmashinesId(student.getSapid(), uid);
						}
						else {
							accountCreationfailedList.put(student.getSapid(), response_body.toString());
							logger.info("AlmaShines Error returned ");
							logger.info("response_body : "+response_body);
						}
					} catch (Exception e) {
						accountCreationfailedList.put(student.getSapid(), "neither Alumni nor Senior Student"); }
		  
		  } 
		  logger.info("Email Already Exists Students");
		  logger.info("emailAlreadyExistsStudent : "+emailAlreadyExistsStudent);
		  
		  logger.info("Account Creation Failed Students");
		  logger.info("accountCreationfailedList : "+accountCreationfailedList);
		  
		  logger.info("Account Created Successfully. Count:" +
		  accountCreationSuccessCount); } catch (Exception e) {
		  logger.info(e.getMessage());
		  logger.info("Error. Account creation stopped. created: " +
		  accountCreationSuccessCount + " accounts"); }
		 

		response.setStatus("completed.");
		return new ResponseEntity<ResponseStudentPortalBean>(response,headers, HttpStatus.OK);
	}  
	@RequestMapping(value = "/CreateAlmaAccountsJobForMbax", method = RequestMethod.GET)
	public ResponseEntity<ResponseStudentPortalBean> CreateAlmaAccountsJobForMbax(HttpServletRequest request) throws Exception {
 
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json"); 
		
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		ResponseStudentPortalBean response = new ResponseStudentPortalBean();
		ArrayList<StudentStudentPortalBean> students = pDao.getPendingStudentsToRegisterInAlmaMbax();
		logger.info("students not in almashines "+students.size()); 
		ArrayList<StudentStudentPortalBean> studentsToCreateAccount = new ArrayList<StudentStudentPortalBean>();
		ArrayList<String> subjectsPassed = new ArrayList<String>();
		for(StudentStudentPortalBean student : students) {
			
			try {
				String programCleared =student.getProgramCleared();  
				String currentSem=null;
				try {
					currentSem = pDao.getStudentsMostRecentRegistrationData(student.getSapid()).getSem(); 
					subjectsPassed = pDao.getPassSubjectsForMbaxStudent(student.getSapid());
				} catch (Exception e) {  
				} 
				if(currentSem != null) { 
			        //get passed subjects count
					int passedSubjectCount=subjectsPassed.size(); 
					
					ArrayList<String> applicableSubjects = serviceRequestDao.getApplicableSubjectNew(student.getConsumerProgramStructureId());	
					//check for alumni
					if(programCleared.equalsIgnoreCase("Y") ||
					  (passedSubjectCount==applicableSubjects.size()) ){
						
	                     student.setRole("alumni"); 
						 studentsToCreateAccount.add(student);
					 } 
					
				}
			} catch (Exception e) {
				logger.info("bean creation failed for sapid:"+student.getSapid());
			}
			
		}
		logger.info("studentBean list ready for account creation.");
		logger.info("size:"+studentsToCreateAccount.size());
		
		ArrayList<String> emailAlreadyExistsStudent = new ArrayList<String>();
		HashMap<String,String> accountCreationfailedList = new HashMap<String,String>();
		int accountCreationSuccessCount=0;
		
		logger.info("Started Account Creation...");
		
		
		  try { for (StudentStudentPortalBean student : studentsToCreateAccount) {
		  
					try {
						JSONObject response_body = almashinesCreateAccount(student);
						logger.info("sapid : " + student.getSapid());
						if (response_body.get("success").equals(-10)) {
							logger.info("Email Already exists ");
							emailAlreadyExistsStudent.add(student.getSapid());
						}
						if (response_body.get("success").equals(-5)) {
							logger.info("uid:" + response_body.get("uid") + " sapid:" + student.getSapid());

						}
						
						else if (response_body.get("success").equals(1)) {
							logger.info("Account created");
							accountCreationSuccessCount++;
							Integer uid = (Integer) response_body.get("uid");
							pDao.SaveAlmashinesId(student.getSapid(), uid);
						}
						else {
							accountCreationfailedList.put(student.getSapid(), response_body.toString());
							logger.info("AlmaShines Error returned ");
							logger.info("response_body : "+response_body);
						}
					} catch (Exception e) {
						accountCreationfailedList.put(student.getSapid(), "neither Alumni nor Senior Student"); }
		  
		  } 
		  logger.info("Email Already Exists Students");
		  logger.info("emailAlreadyExistsStudent : "+emailAlreadyExistsStudent);
		  
		  logger.info("Account Creation Failed Students");
		  logger.info("accountCreationfailedList : "+accountCreationfailedList);
		  
		  logger.info("Account Created Successfully. Count:" +
		  accountCreationSuccessCount); } catch (Exception e) {
		  logger.info(e.getMessage());
		  logger.info("Error. Account creation stopped. created: " +
		  accountCreationSuccessCount + " accounts"); }
		 

		response.setStatus("completed.");
		return new ResponseEntity<ResponseStudentPortalBean>(response,headers, HttpStatus.OK);
	}
	//Copied from home controller
	protected boolean isStudentValid(StudentStudentPortalBean student, String userId) throws ParseException {
		if(userId.startsWith("77")){
			String validityEndMonthStr = student.getValidityEndMonth();
			int validityEndYear = Integer.parseInt(student.getValidityEndYear());
			
			Date lastAllowedAcccessDate = null;
			int validityEndMonth = 0;
			if("Jun".equals(validityEndMonthStr)){
				validityEndMonth = 6;
				 String date = validityEndYear + "/" + validityEndMonth + "/" + "30";
				 lastAllowedAcccessDate = formatter.parse(date);
			}else if("Dec".equals(validityEndMonthStr)){
				validityEndMonth = 12;
				 String date = validityEndYear + "/" + validityEndMonth + "/" + "31";
				 lastAllowedAcccessDate = formatter.parse(date);
			}else if("Sep".equals(validityEndMonthStr)){
				validityEndMonth = 9;
				 String date = validityEndYear + "/" + validityEndMonth + "/" + "30";
				 lastAllowedAcccessDate = formatter.parse(date);
			}else if("Apr".equals(validityEndMonthStr)){
				validityEndMonth = 4;
				 String date = validityEndYear + "/" + validityEndMonth + "/" + "30";
				 lastAllowedAcccessDate = formatter.parse(date);
			}else if("Aug".equals(validityEndMonthStr)){
				validityEndMonth = 8;
				 String date = validityEndYear + "/" + validityEndMonth + "/" + "31";
				 lastAllowedAcccessDate = formatter.parse(date);
			}else if("Oct".equals(validityEndMonthStr)){
				validityEndMonth = 10;
				 String date = validityEndYear + "/" + validityEndMonth + "/" + "31";
				 lastAllowedAcccessDate = formatter.parse(date);
			}else if("Feb".equals(validityEndMonthStr)){
				validityEndMonth = 2;
				 String date = validityEndYear + "/" + validityEndMonth + "/" + "28";
				 lastAllowedAcccessDate = formatter.parse(date);
			}else if("Mar".equals(validityEndMonthStr)){
				validityEndMonth = 3;
				 String date = validityEndYear + "/" + validityEndMonth + "/" + "31";
				 lastAllowedAcccessDate = formatter.parse(date);
			}else if("Jan".equals(validityEndMonthStr)){
				validityEndMonth = 1;
				 String date = validityEndYear + "/" + validityEndMonth + "/" + "31";
				 lastAllowedAcccessDate = formatter.parse(date);
			}else if("May".equals(validityEndMonthStr)){
				validityEndMonth = 5;
				String date = validityEndYear + "/" + validityEndMonth + "/" + "31";
				lastAllowedAcccessDate = formatter.parse(date);
			}else if("Jul".equals(validityEndMonthStr)){
				validityEndMonth = 7;
				 String date = validityEndYear + "/" + validityEndMonth + "/" + "31";
				 lastAllowedAcccessDate = formatter.parse(date);
			}
			
			
			
			Calendar now = Calendar.getInstance();
		    int currentExamYear = now.get(Calendar.YEAR);
		    int currentExamMonth = (now.get(Calendar.MONTH) + 1);
		    
		    if(currentExamYear < validityEndYear  ){
		    	return true;
		    }else if(currentExamYear == validityEndYear && currentExamMonth <= validityEndMonth){
		    	return true;
		    }else{
				Date currentDate = new Date();
				GregorianCalendar cal = new GregorianCalendar();
				cal.setTime(lastAllowedAcccessDate);
				
				if (student.getProgram().equals("EPBM") && student.getEnrollmentYear().equals("2018") && student.getEnrollmentMonth().equals("Jan") ) {
					cal.add(Calendar.DATE, 242);//Allow access till 1 July 2019 For SAS-Jan/2018 batch --> Requirement of Nelson sir
					if(currentDate.before(cal.getTime())){
							return true;//Allow 242 additional days access from Validity End Date
						}
						
						
				}else if (student.getProgram().equals("MPDV") && student.getEnrollmentYear().equals("2018") && student.getEnrollmentMonth().equals("Jan") ) {
					cal.add(Calendar.DATE, 303);//Allow access till 1 July 2019 For SAS-Jan/2018 batch --> Requirement of Nelson sir
					if(currentDate.before(cal.getTime())){
						return true;//Allow 303 additional days access from Validity End Date
					}
					
					
				}else if (student.getProgram().equals("EPBM") && student.getEnrollmentYear().equals("2018") && student.getEnrollmentMonth().equals("Jul") ){
					cal.add(Calendar.DATE, 93);//Allow access  till 1 July 2019 For SAS-Jul/2018 batch --> Requirement of Nelson sir
					if(currentDate.before(cal.getTime())){
						return true;//Allow 63 additional days access from Validity End Date
					}
				
				}else if (student.getProgram().equals("MPDV") && student.getEnrollmentYear().equals("2018") && student.getEnrollmentMonth().equals("Jul") ){
					cal.add(Calendar.DATE, 182);//Allow access  till 1 July 2019 For SAS-Jul/2018 batch --> Requirement of Nelson sir
					if(currentDate.before(cal.getTime())){
						return true;//Allow 182 additional days access from Validity End Date
					}
				
				}else{
					cal.add(Calendar.DATE, 45);//Allow access 45 days after validity end date
						if(currentDate.before(cal.getTime())){
							return true;//Allow 45 additional days access from Validity End Date
						}
					}
				return false;
			}
			
			
		}else{
			//Admin Staff login
			return true;
		}

	} 
	
	public RedirectView getRedirectUrl(String url){
		RedirectView redirectView = new RedirectView();
    	redirectView.setUrl(url);
    	return redirectView;
	}
	 
}
 

