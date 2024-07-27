package com.nmims.controllers;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ServletConfigAware;

import com.google.gson.Gson;
import com.nmims.beans.SchedulerApisBean;
import com.nmims.beans.StatusBean;
import com.nmims.beans.StudentBean;
import com.nmims.beans.StudentDetailsFromSFDCBean;
import com.nmims.daos.LDAPDao;
import com.nmims.daos.ProgramDao;
import com.nmims.daos.StudentZoneDao;
import com.nmims.daos.UserDao;
import com.nmims.helpers.MailSender;
import com.nmims.helpers.SFConnection;
import com.nmims.listeners.SalesforceSyncScheduler;
import com.sforce.soap.partner.Connector;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.fault.ApiFault;
import com.sforce.soap.partner.fault.LoginFault;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;
import com.sforce.ws.bind.XmlObject;
@Controller
public class SynchronizationController implements ApplicationContextAware, ServletConfigAware {
	
	@Autowired
	UserDao userDao;
	
	@Autowired
	ProgramDao programDao;
	
	@Autowired
	StudentZoneDao studentZoneDAO;
	
	@Autowired
	LDAPDao ldapDao;
	
	@Autowired
	SalesforceSyncScheduler salesforceSyncScheduler;
	
	private static ApplicationContext act = null;
	private static ServletConfig sc = null;
	private PartnerConnection connection;
	public SynchronizationController(SFConnection sf) {
		this.connection = sf.getConnection(); 
	}
	public void init(){
		SFConnection sf= new SFConnection(SFDC_USERID,SFDC_PASSWORD_TOKEN);
		this.connection = sf.getConnection();
	}
	private static final Logger logger = LoggerFactory.getLogger(SynchronizationController.class);

	@Autowired
	MailSender mailer;
	
	@Value( "${SFDC_USERID}" )
	private String SFDC_USERID;

	@Value( "${SFDC_PASSWORD_TOKEN}" )
	private String SFDC_PASSWORD_TOKEN;

	@Value( "${ENVIRONMENT}" )
	private String ENVIRONMENT;
	
	@Value("${SFDC_API_MAX_RETRY_COUNT}")
	private String SFDC_API_MAX_RETRY_COUNT;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.act = applicationContext;
	}
	@Override
	public void setServletConfig(ServletConfig sc) {
		this.sc = sc;
	}
	public static ApplicationContext getApplicationContext() {
		return act;
	}
	@Autowired
	SFConnection sfc;
	
	@RequestMapping(value="/studentBeanMasterCrudService",method={RequestMethod.GET,RequestMethod.POST})
	public @ResponseBody String StudentBeanMasterCrudService(@RequestBody String json){
		System.out.println("json in StudentBeanMasterCrudService :"+json);
		Gson gson = new Gson();
		HashMap<String,StudentBean> mapOfSapIdAndStudentBeanRecord = userDao.mapOfSapIdAndStudentBeanBean();
		HashMap<String,String> getMapOfProgramNameAndAbbr = programDao.mapOfProgramNameAndAbbr();
		StudentBean StudentBeanJsonArray[] = gson.fromJson(json, StudentBean[].class);
		List<StudentBean> StudentBeanList = Arrays.asList(StudentBeanJsonArray);
		List<StatusBean> obStatusList = new ArrayList<StatusBean>();
		System.out.println("StudentBeanList size :"+StudentBeanList.size());
		for(StudentBean s : StudentBeanList){
			Map<String,Object> mapOfParameters = new HashMap<String,Object>();
			StatusBean obStatus = new StatusBean();
			mapOfParameters.put("sapid", s.getSapid());
			mapOfParameters.put("firstName", s.getFirstName());
			mapOfParameters.put("gender",s.getGender());
			mapOfParameters.put("lastName", s.getLastName());
			mapOfParameters.put("emailId", s.getEmailId());
			mapOfParameters.put("mobile", s.getMobile());
			mapOfParameters.put("createdBy", "SyncApp");
			mapOfParameters.put("createdDate",String.valueOf(new Timestamp(System.currentTimeMillis())));
			mapOfParameters.put("lastModifiedBy","SyncApp");
			mapOfParameters.put("lastModifiedDate",String.valueOf(new Timestamp(System.currentTimeMillis())));
			mapOfParameters.put("program",getMapOfProgramNameAndAbbr.get(s.getProgram()));
			mapOfParameters.put("centerCode",s.getCenterCode());
			mapOfParameters.put("centerName",s.getCenterName());
			mapOfParameters.put("city",s.getCity());
			mapOfParameters.put("country",s.getCountry());
			mapOfParameters.put("address",s.getAddress());
			mapOfParameters.put("validityEndYear",s.getValidityEndYear());
			mapOfParameters.put("validityEndMonth",s.getValidityEndMonth());
			mapOfParameters.put("dob",s.getDob());
			mapOfParameters.put("isLateral",s.getIsLateral());
			mapOfParameters.put("PrgmStructApplicable", s.getPrgmStructApplicable());
			
			try{
				if(mapOfSapIdAndStudentBeanRecord.get(s.getSapid()) !=null){
					userDao.crudServiceFroStudentMaster("UPDATE", mapOfParameters);
				}else{
					userDao.crudServiceFroStudentMaster("INSERT", mapOfParameters);
				}
				obStatus.setSuccessCount(1);
				obStatus.setErrorDescription("SUCCESS");
				obStatus.setSapidForUpdate(s.getSapid());
			}catch(Exception e){
				obStatus.setFailureCount(1);
				obStatus.setErrorDescription("For JSON :"+json+" ERROR is :-"+e.getMessage());
				obStatus.setSapidForUpdate(s.getSapid());
			}
			obStatusList.add(obStatus);
			
		}
		
		return gson.toJson(obStatusList);
	}
	
	
	
	
	@RequestMapping(value="/registrationMasterCrudService",method={RequestMethod.GET,RequestMethod.POST})
	public @ResponseBody String registrationMasterCrudService(@RequestBody String json){
		System.out.println("json in registrationMasterCrudService :"+json);
		Gson gson = new Gson();
		HashMap<String,StudentBean> mapOfSapIdAndStudentBeanRecord = userDao.mapOfRegistrationSapIdAndStudentBeanBean();
		StudentBean StudentBeanJsonArray[] = gson.fromJson(json, StudentBean[].class);
		List<StudentBean> StudentBeanList = Arrays.asList(StudentBeanJsonArray);
		System.out.println("StudentBeanList size :"+StudentBeanList.size());
		List<StatusBean> obStatusList = new ArrayList<StatusBean>();
		for(StudentBean s : StudentBeanList){
			Map<String,Object> mapOfParameters = new HashMap<String,Object>();
			StatusBean obStatus = new StatusBean();
			mapOfParameters.put("sapid", s.getSapid());
			mapOfParameters.put("sem",s.getSem());
			mapOfParameters.put("year", s.getYear());
			mapOfParameters.put("month", s.getMonth());
			mapOfParameters.put("program",s.getProgram());
			mapOfParameters.put("createdBy", "SyncApp");
			mapOfParameters.put("createdDate",String.valueOf(new Timestamp(System.currentTimeMillis())));
			mapOfParameters.put("lastModifiedBy","SyncApp");
			mapOfParameters.put("lastModifiedDate",String.valueOf(new Timestamp(System.currentTimeMillis())));
			
			try{
				if(mapOfSapIdAndStudentBeanRecord.get(s.getSapid()) !=null){
					System.out.println("Updating registration record :"+s.getSapid());
					userDao.crudServiceForRegistration("UPDATE", mapOfParameters);
				}else{
					System.out.println("insert registration record :"+s.getSapid());
					userDao.crudServiceForRegistration("INSERT", mapOfParameters);
				}
				obStatus.setSuccessCount(1);
				obStatus.setErrorDescription("SUCCESS");
				obStatus.setOpportunityIdForUpdate(s.getId());
				
				
			}catch(Exception e){
				obStatus.setFailureCount(1);
				obStatus.setErrorDescription("For JSON :"+json+" ERROR is :-"+e.getMessage());
				obStatus.setOpportunityIdForUpdate(s.getId());
			}
			
			obStatusList.add(obStatus);
			
		}
		
		return gson.toJson(obStatusList);
	}
	
	//updateStudentDetailsFromSFDCToPortal start
	@RequestMapping(value = "/updateStudentDetailsFromSFDCToPortal", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<HashMap<String,String>> updateStudentDetailsFromSFDCToPortal(@RequestBody StudentDetailsFromSFDCBean bean){
		
		System.out.println("/m/updateStudentDetailsFromSFDCToPortal API Called");
		System.out.println("In updateStudentDetailsFromSFDCToPortal got config : "+bean.toString());
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		HashMap<String,String> response = new HashMap<>();
		if(!StringUtils.isBlank(bean.getOldProgram())){
			bean.setProgramChanged("Y");
		}else{
			bean.setProgramChanged("N");
		}
		String errorMessage = studentZoneDAO.updateAllStudentDetailsFromSFDC(bean);
		
		if(!StringUtils.isBlank(errorMessage)) {
			response.put("Status", "Failed");
			response.put("message", errorMessage);
			return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		else {
			response.put("Status", "Success");
			response.put("message", "Profile Updated Successfully !");
			return new ResponseEntity<>(response,headers, HttpStatus.OK);	
		}
		
		
	}
	//end
	
	//updateAllStudentsAddressDetailsFromSFDCToPortal start
		@RequestMapping(value = "/updateAllStudentsAddressDetailsFromSFDCToPortal", method = RequestMethod.GET,produces = "application/json")
		public ResponseEntity<HashMap<String,String>> updateAllStudentsAddressDetailsFromSFDCToPortal(){
			
			System.out.println("/updateAllStudentsAddressDetailsFromSFDCToPortal API Called");
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");
			HashMap<String,String> response = new HashMap<>();
			
			String errorMessage = getAllStudentsAddressDetailsFromSFDCAndUpdateToPortal( Integer.parseInt(SFDC_API_MAX_RETRY_COUNT));
			System.out.println("IN updateAllStudentsAddressDetailsFromSFDCToPortal got errorMessage : "+errorMessage);
			if(!StringUtils.isBlank(errorMessage)) {
				response.put("Status", "Failed");
				response.put("message", errorMessage);
				return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			else {
				response.put("Status", "Success");
				response.put("message", "updateAllStudentsAddressDetailsFromSFDCToPortal Successfully !");
				return new ResponseEntity<>(response,headers, HttpStatus.OK);	
			}
			
			
		}
		//end
		
		public String getAllStudentsAddressDetailsFromSFDCAndUpdateToPortal(int retryCount) {
			//Commented for test server
			
			  if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){ System.out.
			  println("Not Running findSalesforceStudentZoneMismatch since it is not PROD "
			  ); return "Not PROD"; }
			 
			
			System.out.println("getAllStudentsAddressDetailsFromSFDCAndUpdateToPortal : Querying documents details from SFDC");
			
			String errorMessage = "";

			try {
			ConnectorConfig config = new ConnectorConfig();
			config.setUsername(SFDC_USERID);
			config.setPassword(SFDC_PASSWORD_TOKEN);

			System.out.println("SFDC_USERID = "+SFDC_USERID);
			System.out.println("SFDC_PASSWORD_TOKEN = "+SFDC_PASSWORD_TOKEN);

			
			QueryResult qResult = new QueryResult();

			/*
			 * connection = Connector.newConnection(config); // query for documents records
			 * for give SFDC record Id
			 * System.out.println("Auth EndPoint: "+config.getAuthEndpoint());
			 * System.out.println("Service EndPoint: "+config.getServiceEndpoint());
			 * System.out.println("Username: "+config.getUsername());
			 * System.out.println("SessionId: "+config.getSessionId());
			 */
				String soqlQuery = "SELECT "
						+ " nm_StudentNo__c, "
						//Shipping address details start
						+ " House_No_Name_Shipping_Account__c,"
						+ " Shipping_Street__c,"
						+ " Locality_Name_Shipping__c,"
						+ " Nearest_LandMark_Shipping__c,"
						+ " City_Shipping_Account__c,"
						+ " State_Province_Shipping__c,"
						+ " Country_Shipping__c,"
						+ " Zip_Postal_Code_Shipping__c,"
						//+ " Phone, " //added for syncing altPhone with portal
						//Shipping address details end
						//Work experience/Education details start
						+ " Highest_Qualification__c,"
						+ " age_according_to_admission__c,"
						//Work experience/Education details end
						+ " nm_DateOfBirth__c,"
						+ " Account_Confirm_Date__c,"
						//Center details start
						+ " IC_Name_1__c,"
						+ " nm_Centers__r.ID__c"
						//Center details start
						+ " FROM Account"
						+ " where nm_StudentStatus__c = 'Confirmed' ";
						
				qResult = connection.query(soqlQuery);
				boolean done = false;
				
				if (qResult.getSize() > 0) {
					ArrayList<StudentBean> salesforceStudentList = new ArrayList<>();

					System.out.println("Logged-in user can see a total of "
							+ qResult.getSize() + " Account records.");
					while (!done) {
						SObject[] records = qResult.getRecords();
						for (int i = 0; i < records.length; ++i) {
							SObject s = (SObject) records[i];
							XmlObject centers__r = (XmlObject)s.getField("nm_Centers__r");
							StudentBean student = new StudentBean();
							student.setSapid((String)s.getField("nm_StudentNo__c"));
							student.setHouseNoName((String)s.getField("House_No_Name_Shipping_Account__c"));
							student.setStreet((String)s.getField("Shipping_Street__c"));
							student.setLocality((String)s.getField("Locality_Name_Shipping__c"));
							student.setLandMark((String)s.getField("Nearest_LandMark_Shipping__c"));
							student.setCity((String)s.getField("City_Shipping_Account__c"));		
							student.setState((String)s.getField("State_Province_Shipping__c"));	
							student.setCountry((String)s.getField("Country_Shipping__c"));			
							student.setPin((String)s.getField("Zip_Postal_Code_Shipping__c"));
							student.setHighestQualification((String)s.getField("Highest_Qualification__c"));
							student.setAge((String)s.getField("age_according_to_admission__c"));
							//student.setAltPhone((String)s.getField("Phone"));
							student.setRegDate((String)s.getField("Account_Confirm_Date__c"));
							student.setDob((String)s.getField("nm_DateOfBirth__c"));
							//System.out.println("Account " + (i + 1) + ": " + student);
							student.setCenterCode(((String)centers__r.getField("ID__c")).substring(0, 15));
							student.setCenterName((String)s.getField("IC_Name_1__c"));

							salesforceStudentList.add(student);
						}
						System.out.println("IN updateAllStudentsAddressDetailsFromSFDCToPortal salesforceStudentList size : ");
						System.out.println(salesforceStudentList.size());
						if (qResult.isDone()) {
							done = true;
						} else {
							System.out.println("Querying more.....");
							qResult = connection.queryMore(qResult.getQueryLocator());
						}
					}
					
					//Fetch data from student zone
					studentZoneDAO = (StudentZoneDao)act.getBean("studentZoneDAO");
					//HashMap<String, StudentBean>  sapIdStudentsMap = studentZoneDAO.getAllStudents();
					
					String butchUpadate = studentZoneDAO.batchUpdateAllStudentsAddress(salesforceStudentList);
					errorMessage= errorMessage+butchUpadate;
					
					
				} else {
					System.out.println("No records found.");
					errorMessage= errorMessage+" No records found.";
				}
			} catch (ApiFault e) {
				logger.info("Connection exception in updated SHIPPING ADDRESS");
				e.printStackTrace();
				if(retryCount > 0) {
					init();
					getAllStudentsAddressDetailsFromSFDCAndUpdateToPortal(retryCount - 1);
				}
			}catch(Exception e){ 
				e.printStackTrace();
				errorMessage= errorMessage+e.getMessage();
			}
			return errorMessage;
		}
		
	
	// update Student Details from SFDC to Portal 
		/*Present in studentportal App
		 * @RequestMapping(value = "/updateProfileFromSFDC", method = {RequestMethod.GET, RequestMethod.POST})
		public void updateProfileFromSFDC(HttpServletRequest request, HttpServletResponse response)
		{
			//System.out.println("Update Profile From SFDC to Portal-->");
			String userId =(String)request.getParameter("sapid");
			String emailId =(String)request.getParameter("emailId");
			String mobilePhone =(String)request.getParameter("mobileNo");
			String fatherName =(String)request.getParameter("fatherName");
			String motherName =(String)request.getParameter("motherName");
			String studentImageUrl = (String)request.getParameter("studentImage");
			String validityEndMonth = (String)request.getParameter("validityEndMonth");
			String validityEndYear = (String)request.getParameter("validityEndYear");
			System.out.println("UserId--->"+userId);
			
			StudentBean student =studentDao.getSingleStudentsData(userId);
			
			try{
				ldapDao.updateProfile(userId, emailId, mobilePhone, student.getAltPhone());//Update Details in LDAP//
				studentDao.updateStudentContactFromSFDC(userId, emailId, mobilePhone, "",student.getAltPhone(),fatherName, motherName,studentImageUrl,validityEndYear, validityEndMonth);
			}catch(Exception e)
			{
				e.printStackTrace();
				System.out.println("Error In Updating Student Profile of  -->"+userId);
			}
		}*/




			//testRegistrationStaging start
			@RequestMapping(value = "/testRegistrationStaging", method = RequestMethod.GET,produces = "application/json")
			public ResponseEntity<HashMap<String,String>> testRegistrationStaging(){
				
				  
				System.out.println("/testRegistrationStaging API Called");
				HttpHeaders headers = new HttpHeaders();
				headers.add("Content-Type", "application/json");
				HashMap<String,String> response = new HashMap<>();
				
				if("PROD".equalsIgnoreCase(ENVIRONMENT)){
					response.put("Status", "Success");
					response.put("message", "Will not run on prod !");
					return new ResponseEntity<>(response,headers, HttpStatus.OK);	
				}
				
				String errorMessage = "";
				studentZoneDAO = (StudentZoneDao)act.getBean("studentZoneDAO");
				List<StudentBean> studentList = new ArrayList<>();
				
				StudentBean test1 = new StudentBean();
				test1.setSapid("77777777777");
				test1.setProgram("EPBM");
				test1.setSem("1");
				test1.setYear("2019");
				test1.setMonth("Jan");
				test1.setCreatedBy("tester_PS");
				test1.setLastModifiedBy("tester_PS");
				
				StudentBean test2 = new StudentBean();
				test2.setSapid("77777777777");
				test2.setProgram("MPDV");
				test2.setSem("2");
				test2.setYear("2019");
				test2.setMonth("Jul");
				test2.setCreatedBy("tester_PS");
				test2.setLastModifiedBy("tester_PS");
				
				studentList.add(test1);
				studentList.add(test2);
				
				
				ArrayList<StudentBean> errorList = studentZoneDAO.batchUpsertRegistration(studentList);
				
				if(errorList.size() > 0) {
					errorMessage += "Error while saveing to DB. ";
				}
				
				System.out.println("IN testRegistrationStaging got errorMessage : "+errorMessage);
				if(!StringUtils.isBlank(errorMessage)) {
					response.put("Status", "Failed");
					response.put("message", errorMessage);
					return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
				}
				else {
					response.put("Status", "Success");
					response.put("message", "updateAllStudentsAddressDetailsFromSFDCToPortal Successfully !");
					return new ResponseEntity<>(response,headers, HttpStatus.OK);	
				}
				
				
			}
			//end

		//getPassedYearMonthBySapid start
			@RequestMapping(value = "/getPassedYearMonthBySapid", method = RequestMethod.POST,consumes = "application/json",produces = "application/json")
			public ResponseEntity<HashMap<String,String>> getPassedYearMonthBySapid(@RequestBody StudentBean input){
				
				System.out.println("/getPassedYearMonthBySapid API Called");
				HttpHeaders headers = new HttpHeaders();
				headers.add("Content-Type", "application/json");
				HashMap<String,String> response = new HashMap<>();
				
				studentZoneDAO = (StudentZoneDao)act.getBean("studentZoneDAO");
				
				String sapid = "";
				if(input != null) {
					System.out.println("In getPassedYearMonthBySapid got sapid :"+input.getSapid()); 
					sapid=input.getSapid();
				}
				
				if(StringUtils.isBlank(sapid)) {
					response.put("status", "Invalid");
					response.put("message", "Please enter sapid, sapid cannot be blank. ");
					response.put("yearOfPassing", "");
					response.put("monthOfPassing", "");
					response.put("program", "");
					response.put("sapid", sapid);
					return new ResponseEntity<>(response,headers, HttpStatus.OK);	
				
				}
				
				
				//Check if sapid is vaild
				StudentBean student = studentZoneDAO.getSingleStudentsData(sapid);
				if(student == null) {
					response.put("status", "Invalid");
					response.put("message", "Invalid SapID : "+sapid);
					response.put("yearOfPassing", "");
					response.put("monthOfPassing", "");
					response.put("program", "");
					response.put("sapid", sapid);
					return new ResponseEntity<>(response,headers, HttpStatus.OK);	
				
				}
				
				//Check if student has passed subjects to clear program
				String hasStudentPassedSubjectsToClearProgram = checkHasStudentPassedSubjectsToClearProgram(studentZoneDAO,sapid);
				if(!"true".equalsIgnoreCase(hasStudentPassedSubjectsToClearProgram)) {

					response.put("status", "Failed");
					response.put("message", "Student : "+sapid+" has not cleared program. Got error :"+hasStudentPassedSubjectsToClearProgram);
					response.put("yearOfPassing", "");
					response.put("monthOfPassing", "");
					response.put("program", "");
					response.put("sapid", sapid);
					return new ResponseEntity<>(response,headers, HttpStatus.OK);
				}
				
				//get passed year month
				StudentBean yearMonthBean = studentZoneDAO.getPassedYearMonthBySapid(sapid);
				if(yearMonthBean == null) {
					response.put("status", "Failed");
					response.put("message", "Error in getting passed year month. ");
					response.put("yearOfPassing", "");
					response.put("monthOfPassing", "");
					response.put("program", "");
					response.put("sapid", sapid);
					return new ResponseEntity<>(response,headers, HttpStatus.OK);	
				
				}
				

				response.put("status", "Success");
				response.put("message", "");
				response.put("yearOfPassing", yearMonthBean.getYear());
				response.put("monthOfPassing", getFullMonthNameByAbbrivation(yearMonthBean.getMonth()));
				response.put("program", yearMonthBean.getProgramname());
				response.put("sapid", sapid);
				return new ResponseEntity<>(response,headers, HttpStatus.OK);
				
			}
			//end
			
			private String getFullMonthNameByAbbrivation(String abb) {
				String month = "";
				
				if("Jan".equalsIgnoreCase(abb)) {
					return "January";
				}else if("Feb".equalsIgnoreCase(abb)) {
					return "February";
				}else if("Mar".equalsIgnoreCase(abb)) {
					return "March";
				}else if("Apr".equalsIgnoreCase(abb)) {
					return "April";
				}else if("May".equalsIgnoreCase(abb)) {
					return "May";
				}else if("Jun".equalsIgnoreCase(abb)) {
					return "June";
				}else if("Jul".equalsIgnoreCase(abb)) {
					return "July";
				}else if("Aug".equalsIgnoreCase(abb)) {
					return "August";
				}else if("Sep".equalsIgnoreCase(abb)) {
					return "September";
				}else if("Oct".equalsIgnoreCase(abb)) {
					return "October";
				}else if("Nov".equalsIgnoreCase(abb)) {
					return "November";
				}else if("Dec".equalsIgnoreCase(abb)) {
					return "December";
				}
				
				if(!StringUtils.isBlank(abb)) {
					return abb;
				}
				
				return month;
			}
			
			private String checkHasStudentPassedSubjectsToClearProgram(StudentZoneDao studentZoneDAO,String sapid) {
				
				String noOfSubjectsPassedInPassFailBySapid = studentZoneDAO.getNoOfSubjectsPassedInPassFailBySapid(sapid);
				if(!StringUtils.isNumeric(noOfSubjectsPassedInPassFailBySapid)) {
					return noOfSubjectsPassedInPassFailBySapid;
				}
				
				String noOfSubjectsToClearProgramBySapid = studentZoneDAO.getNoOfSubjectsToClearProgramBySapid(sapid);
				if(!StringUtils.isNumeric(noOfSubjectsToClearProgramBySapid)) {
					return noOfSubjectsToClearProgramBySapid;
				}
				
				try {
					if(Integer.parseInt(noOfSubjectsPassedInPassFailBySapid) == Integer.parseInt(noOfSubjectsToClearProgramBySapid)) {
						return "true";
					}else {
						return "noOfSubjectsPassedInPassFailBySapid : "
								+noOfSubjectsPassedInPassFailBySapid
								+" noOfSubjectsToClearProgramBySapid : "
								+noOfSubjectsToClearProgramBySapid
								+" Does not match.";
					}
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return e.getMessage();
				}
			}
			

			private String getPassedYearMonthBySapid(StudentZoneDao studentZoneDAO,String sapid) {
				StudentBean student = new StudentBean();
				
				return "";
			}


			//runStudentSyncJobApi start
			@RequestMapping(value = "/runStudentSyncJobApi", method = RequestMethod.GET,produces = "application/json")
			public ResponseEntity<HashMap<String,String>> runStudentSyncJobApi(){
				  
				System.out.println("/testRegistrationStaging API Called");
				HttpHeaders headers = new HttpHeaders();
				headers.add("Content-Type", "application/json");
				HashMap<String,String> response = new HashMap<>();
				SchedulerApisBean sbean = new SchedulerApisBean();
				
				
				
				  if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){ response.put("Status", "Success");
				  response.put("message", "Will not run on prod !"); return new
				  ResponseEntity<>(response,headers, HttpStatus.OK); }
				 
				 
				 
				
				salesforceSyncScheduler.syncSalesforceRegistrationData(Integer.parseInt(SFDC_API_MAX_RETRY_COUNT));				
				
					response.put("Status", "Success");
					response.put("message", "runStudentSyncJobApi Successfully !");
					sbean.setSyncType("Salesforce Registration Data"); 
					studentZoneDAO.updateLastSyncedTime(sbean);
					return new ResponseEntity<>(response,headers, HttpStatus.OK);	
				
			}
			//end
			
}
