package com.nmims.listeners;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.ServletConfigAware;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.nmims.beans.RevenueReportField;
import com.nmims.beans.SchedulerApisBean;
import com.nmims.beans.StatusBean;
import com.nmims.beans.StudentBean;
import com.nmims.beans.StudentDataMismatchBean;
import com.nmims.beans.StudentLearningMetricsBean;
import com.nmims.daos.LDAPDao;
import com.nmims.daos.StudentZoneDao;
import com.nmims.helpers.MailSender;
import com.nmims.helpers.Person;
import com.nmims.helpers.SFConnection;
import com.nmims.interfaces.StudentIdCardInterface;
import com.nmims.services.SpecializationService;
import com.nmims.util.StudentProfileUtils;
import com.sforce.soap.partner.Connector;
import com.sforce.soap.partner.Error;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.SaveResult;
import com.sforce.soap.partner.fault.ApiFault;
import com.sforce.soap.partner.fault.LoginFault;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;
import com.sforce.ws.bind.XmlObject;
@Controller
@Component
public class SalesforceSyncScheduler implements ApplicationContextAware, ServletConfigAware{
	
	private static final Logger logger = LoggerFactory.getLogger(SalesforceSyncScheduler.class);

	private static ApplicationContext act = null;
	private static ServletConfig sc = null;
	private PartnerConnection connection;

	@Value( "${SERVER_PATH}" )
	private String SERVER_PATH;
	
	@Value( "${SFDC_USERID}" )
	private String SFDC_USERID;

	@Value( "${SFDC_PASSWORD_TOKEN}" )
	private String SFDC_PASSWORD_TOKEN;

	@Value( "${CURRENT_ACAD_MONTH}" )
	private String CURRENT_ACAD_MONTH;
	
	@Value( "${CURRENT_ACAD_YEAR}" )
	private String CURRENT_ACAD_YEAR;
	
	@Value( "${MISMATCH_ACAD_MONTH}" )
	private String MISMATCH_ACAD_MONTH;
	
	@Value( "${MISMATCH_ACAD_YEAR}" )
	private String MISMATCH_ACAD_YEAR;
	
	@Value("${SFDC_API_MAX_RETRY_COUNT}")
	private String SFDC_API_MAX_RETRY_COUNT;
	
	@Value( "${ENVIRONMENT}" )
	private String ENVIRONMENT;
	
	@Value( "${SERVER}" )
	private String SERVER;
	
	@Autowired
	SFConnection sfc; 
	
	@Autowired
	StudentIdCardInterface idCardService;
	
	@Override
	public void setServletConfig(ServletConfig sc) {
		this.sc = sc;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.act = applicationContext;
	}
	public static ApplicationContext getApplicationContext() {
		return act;
	}
	
	@Autowired
	SpecializationService specializationService;
	
	
	
	@Autowired
	StudentZoneDao studentZoneDAO;
	
	@Autowired
	MailSender mailer;
	
	private static final String ENVIRONMENT_PRODUCTION = "PROD";
	private static final String SERVER_TOMCAT_20 = "tomcat20";
	
	private ArrayList<String> yearList = new ArrayList<String>(Arrays.asList( 
			"2000","2001","2002","2003","2004","2005","2006","2007",
			"2008","2009","2010","2011","2012","2013","2014","2015","2016","2017", "2018", "2019", "2020", "2021", "2022", "2023", "2024","2025","2026","2027","2028","2029","2030"));
	
	public SalesforceSyncScheduler(SFConnection sf) {  
		this.connection = sf.getConnection();
	}
	public void init(){
		SFConnection sf= new SFConnection(SFDC_USERID,SFDC_PASSWORD_TOKEN); 
		this.connection = sf.getConnection();
	}
	//@Scheduled(fixedDelay=2*60*60*1000)//to run every 6 hours
	@Scheduled(cron = "0 1 6,13,18,22 * * *")//to run everyday at 6am,1pm,6pm,10pm
	//@Scheduled(fixedDelay=15*60*1000)//to run every 15 min
	public void syncSalesforceRegistrationDataScheduler(){
		if ("tomcat20".equalsIgnoreCase(SERVER)){
			syncSalesforceRegistrationData(Integer.parseInt(SFDC_API_MAX_RETRY_COUNT));
		}
	}
	
	public void syncSalesforceRegistrationData(int retryCount){	
		
		
		  if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){ 
		  System.out.println("Not Running syncSalesforceRegistrationData since it is not PROD ");
		  return; }
		 
		 
	    SchedulerApisBean sbean = new SchedulerApisBean(); 
		
		System.out.println("Querying Student Master details from SFDC"); 
		logger.info("--->SalesForce Portal Account/Opportunity Sync  ");
		 
		
		int UPDATE_BATCH_SIZE = 5;
		
		// list for updating back in salesforce
		ArrayList<SObject> opportunitySObjectListToUpdateBackInSalesforce = new ArrayList<SObject>();
		ArrayList<SObject> accountSObjectListToUpdateBackInSalesforce = new ArrayList<SObject>();
		ArrayList<StudentBean> studentToCreateInLDAP = new ArrayList<StudentBean>();
		
		//List for sending error emails 
		ArrayList<StudentBean> invalidSalesforceRecordsListForEmail = new ArrayList<StudentBean>();
		ArrayList<Person> ldapErrorListForEmail = new ArrayList<Person>();
		ArrayList<StudentBean> studentZoneUpdateErrorListForEmail = new ArrayList<>();
		ArrayList<String> salesforceUpdateErrorListForEmail = new ArrayList<>();
		ArrayList<StudentBean> studentZoneSuccessfulListForEmail = new ArrayList<>();
		ArrayList<StudentBean> idCardCreationErrorList = new ArrayList<>();
		ArrayList<StudentBean> syncSpecializationErrorList = new ArrayList<>();
		QueryResult qResult = new QueryResult();

		try { 
			// query for Student Master records for give SFDC record Id
			 
			// below query updates 27 fields in student table and 4 fields of reg table
			String soqlQuery =" SELECT Account.Id,id,"
					+ "	Account.nm_NewPassword__c," //Password ldap
					+ " Account.Synced_With_LDAP__c,"
					+ " Synced_With_StudentZone__c ,"
					
					//student personal information start 
					+ " Account.FirstName," //firstName
					+ " Account.Batch__c," // Batch Name
					+ " Account.LastName,"//lastName
					+ " Account.nm_Gender__c," // gender
					+ " Account.nm_DateOfBirth__c," //dob
					+ " Account.nm_StudentImageUrl__c ,"//studentImage
					+ " Account.Father_First_Name__c,"//father name
					+ " Account.Mother_First_Name__c," //mothername
					+ " Account.nm_BloodGroup__c," //student blood group
					//student personal information end
					
					//student contact information start
					+ " Account.PersonEmail," //emailId
					+ " Account.PersonMobilePhone," //mobile
					+ " Account.Phone," //altPhone
					+ " Account.Address__c, " //address
					+ " Account.nm_PermanentAddress__c, "
					/*Old fields that were synced
					 * + " Account.nm_PostalCode__c ,"//pin
					+ " Account.nm_City__c,"//city
					+ " Account.nm_StateProvince__c ,"//state
					+ " Account.nm_Country__c ,"//country
					*
					*/					
					+ " Account.House_No_Name_Shipping_Account__c," //houseNoName
					+ " Account.Shipping_Street__c,"//street
					+ " Account.Locality_Name_Shipping__c,"//locality
					+ " Account.Nearest_LandMark_Shipping__c,"//landMark
					+ " Account.City_Shipping_Account__c,"//city
					+ " Account.State_Province_Shipping__c,"//state
					+ " Account.Country_Shipping__c,"//country
					+ " Account.Zip_Postal_Code_Shipping__c,"//pin
					//student contact information end
					
					//Work experience/Education details start
					+ " Account.Highest_Qualification__c,"
					+ " Account.age_according_to_admission__c,"
					//Work experience/Education details end
						
					//student program information start
					+ " Account.nm_StudentNo__c," //Sapid
					+ " Account.Student_Number_Lateral__c, "//PreviousStudentId
					+ " ICID__c ,"//centerCode
					+ " Account.IC_Name_1__c ,"//centerName
					+ " Account.OldStudentForExecutive__c,"
					+ " Is_Re_Registration_Payment__c," //IsReReg
					+ " Account.Is_Lateral__c,"//isLateral
					+ " Account.Account_Confirm_Date__c,"//regDate
					+ " CloseDate,"
					+ " nm_Program__r.StudentZoneProgramCode__c,"//program
					+ " nm_StudentProgram__r.Previous_program_name__c," //old program name
					+ " Drive_Month__c,"//enrollmentMonth
					+ " Enrollment_Year__c ,"//enrollmentYear
					+ " Account.Validity_Month__c ," //validityEndMonth
					+ " Account.Validity_Year__c ,"//validityEndYear
					+ " Account.Extend_Validity_upto_Month__c ,"//validityEndMonth
					+ " Account.Extend_Validity_Upto_Year__c,"//validityEndYear
					+ " nm_Semester__c," //sem
					+ " Program_Structure__c ," //PrgmStructApplicable
					+ " nm_Session__c,"// reg table month
					+ " nm_Year__c,"// reg table year
					+ " Account.LC_Name__c,"// LC name
					+ " Account.Specialization_Type__c, "//Specialization Type (Single, Dual)
					+ " nm_Program__r.Name, "// Specialization 1
					+ " nm_Program2__r.Name, "// Specialization 2 

					//student program information end
					
					//CS field start
					+ " Career_Service__c"// the purchased CS package
					//CS field end
					
					+ " FROM Opportunity "
					+ " where Account.nm_StudentStatus__c = 'Confirmed' and StageName = 'Closed Won'  and ( Synced_With_StudentZone__c = false  or Account.Synced_With_LDAP__c = false) ";



/*
			String soqlQuery =" SELECT Account.Id,id,Account.nm_NewPassword__c,Account.Synced_With_LDAP__c,Account.nm_StudentNo__c,Account.OldStudentForExecutive__c,Is_Re_Registration_Payment__c, Account.FirstName, Account.nm_Gender__c , nm_Program__r.Name , Account.PersonEmail, Account.Account_Confirm_Date__c ,  "
							 +" Account.PersonMobilePhone , Account.Phone ,Account.nm_DateOfBirth__c , CloseDate , Account.Is_Lateral__c ,Account.nm_PermanentAddress__c , "
							 +" Account.LastName, Drive_Month__c, Enrollment_Year__c , nm_Semester__c  , Account.nm_City__c , Account.nm_StateProvince__c , Account.nm_Country__c , Synced_With_StudentZone__c ,"
							 +" Account.nm_PostalCode__c , ICID__c , Account.IC_Name_1__c ,nm_Session__c,nm_Year__c, Account.Validity_Month__c , Account.Validity_Year__c , Account.Extend_Validity_upto_Month__c ,  Account.Extend_Validity_Upto_Year__c, Program_Structure__c , Account.nm_StudentImageUrl__c , Account.Student_Number_Lateral__c "
							 +" FROM Opportunity "
							 +" where Account.nm_StudentStatus__c = 'Confirmed' and StageName = 'Closed Won'  and ( Synced_With_StudentZone__c = false  or Account.Synced_With_LDAP__c = false) " ;

*/

			qResult = connection.query(soqlQuery);
			boolean done = false;
			if (qResult.getSize() > 0) {
				ArrayList<StudentBean> salesforceStudentList = new ArrayList<>();
                int count=0;
				logger.info("Number to Records Sync: "+ qResult.getSize());
				while (!done) {
					SObject[] records = qResult.getRecords();
					for (int i = 0; i < records.length; ++i) {
						logger.info("Record: "+ i + "/" + qResult.getSize());
						count=i+1;
						try {
							SObject s = (SObject) records[i];
							
							StudentBean student = createStudentBeanFromSalesforceData(s);
							
							logger.info("Account " + (i + 1) + ": " + student.toString());
							//validate StudentBean record 
							try {
								validateStudentBean(student);

							} catch (Exception e) {
								logger.info("Error in validating studentBean:"+student.getSapid());
							}
							
							if(!student.isErrorRecord()){
								
								salesforceStudentList.add(student);
								
								if(!student.isAccountSyncedWithLDAP()){
									studentToCreateInLDAP.add(student);
								}
								
							}else{// adding invalid records in list and sending in mail
								logger.info("invalid student:"+student.getSapid()+",error message:"+student.getErrorMessage());
								invalidSalesforceRecordsListForEmail.add(student);
								
								// set Value to update in Salesforce 
								SObject sObject = new SObject();
								sObject.setType("Opportunity");
								sObject.setField("StudentZone_Sync_Error__c", student.getErrorMessage());
								sObject.setId(student.getOpportunityId());
								opportunitySObjectListToUpdateBackInSalesforce.add(sObject);
								
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							logger.info("Error " + (i + 1) + ": " + e.getMessage());
						}
					}
					
					if (qResult.isDone()) {
						done = true;
					} else {
						logger.info("Querying more.....");
						qResult = connection.queryMore(qResult.getQueryLocator());
					}
				}
				logger.info("sync completed. synced "+count+" out of"+qResult.getSize());
				
				/*if(studentToCreateInLDAP.size() >0){
					try {
					logger.info("Creating Id cards for "+studentToCreateInLDAP.size() + " students");
					idCardCreationErrorList = idCardService.generateIdCardForStudent(studentToCreateInLDAP,accountSObjectListToUpdateBackInSalesforce);
					}catch (Exception e) {
						logger.error("Error in IdCard generation.");
						logger.error(e.getMessage());
					}
				}*/
				try {
					logger.info("Inserting Specializations in MBA Specialization Table");
					syncSpecializationErrorList=specializationService.insertEntriesInMBASpecialization(salesforceStudentList);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					logger.error("Error in Inserting Specialization");
					logger.error(e1.getMessage());
				}

				// update Users in LDAP 
				if(studentToCreateInLDAP.size() >0){
					try {
						ldapErrorListForEmail = createUserInLDAP(accountSObjectListToUpdateBackInSalesforce, studentToCreateInLDAP,accountSObjectListToUpdateBackInSalesforce);
						logger.info(""+ldapErrorListForEmail);
						logger.info("LDAP Error students:");
						for(Person p : ldapErrorListForEmail) {
							logger.info(p.getUserId());
						}
						
					} catch (Exception e) {
						logger.info("Error creating student in LDAP:");
						logger.info(e.getMessage());
					}
				}
				
				logger.info("creating user in studentzone...");
				if(salesforceStudentList.size() > 0)
				{
					try {
						//update data in student zone
						studentZoneUpdateErrorListForEmail = createUserInStudentZone(salesforceStudentList, opportunitySObjectListToUpdateBackInSalesforce , studentZoneSuccessfulListForEmail);
					} catch (Exception e) {  
						logger.info(e.getMessage());
					}
				}	
				logger.info("updating records back in salesforce opportunity...");
				// update Record back in Salesforce 
				if(opportunitySObjectListToUpdateBackInSalesforce.size() > 0){
					try {
						salesforceUpdateErrorListForEmail.addAll(updateRecordsBackInSalesforce(opportunitySObjectListToUpdateBackInSalesforce,"Opportunity",UPDATE_BATCH_SIZE));
					} catch (Exception e) { 
						logger.info(e.getMessage());
					}
				}
				logger.info("updating records back in salesforce account...");
				if(accountSObjectListToUpdateBackInSalesforce.size() > 0){
					try {
						salesforceUpdateErrorListForEmail.addAll(updateRecordsBackInSalesforce(accountSObjectListToUpdateBackInSalesforce,"Account",UPDATE_BATCH_SIZE));
					} catch (Exception e) { 
						logger.info(e.getMessage());
					}
				}
				logger.info("sending error mail...");
				// sending Upsert fail record in Mail
				mailer.sendStudentZoneUpsertErrorEmail(invalidSalesforceRecordsListForEmail,ldapErrorListForEmail,salesforceUpdateErrorListForEmail,studentZoneUpdateErrorListForEmail,studentZoneSuccessfulListForEmail,idCardCreationErrorList);
			}
		} catch (ApiFault e) { 
			logger.info("Caught ApiFault");
			e.printStackTrace();
			if(retryCount > 0) {
				init();
				syncSalesforceRegistrationData(retryCount - 1);
			}
		}catch(Exception e){ 
			logger.info("Error:  " + e.getMessage());
			sbean.setError(e + "");
		}
		sbean.setSyncType("Salesforce Registration Data");
		studentZoneDAO.updateLastSyncedTime(sbean);
	}

	@RequestMapping(value = "/findSalesforceStudentZoneMismatch", method = { RequestMethod.GET, RequestMethod.POST })
	public void callFindSalesforceStudentZoneMismatch(HttpServletRequest request, HttpServletResponse response) {
		findSalesforceStudentZoneMismatch();
	}
	@Scheduled(fixedDelay=24*60*60*1000)
	public void findSalesforceStudentZoneMismatch(){
		
		//Commented for test server
		
		
		 
		
		  if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){ System.out.
		  println("Not Running findSalesforceStudentZoneMismatch since it is not PROD "
		  ); return; }
		SchedulerApisBean sbean = new SchedulerApisBean(); 
		 
		logger.info("--->Finding Salesforce StudentZone Mismatch...");
		logger.info("Querying documents details from SFDC");
		System.out.println("findSalesforceStudentZoneMismatch : Querying documents details from SFDC");
		 
		
		QueryResult qResult = new QueryResult();

		try { 
			String soqlQuery = "SELECT "
					+ " PersonEmail, "
					+ " nm_StudentNo__c, "
					+ " PersonMobilePhone, "
					+ " FirstName, "
					+ " LastName, "
					+ " nmStudent_Program__c,"
					+ " Extend_Validity_upto_Month__c, "
					+ " Extend_Validity_Upto_Year__c, "
					+ " Validity_Month__c, "
					+ " Validity_Year__c, "
					+ " Phone, "
					+ " nm_DateOfBirth__c, "
					+ " Account_Confirm_Date__c,"
					+ " Is_Lateral__c,"
					+ " City_Shipping_Account__c,"  //Changed 'nm_City__c' to shipping city
					+ " Country_Shipping__c, 	"  //Changed 'nm_Country__c' to shipping country
					+ " State_Province_Shipping__c, "  //Changed 'nm_StateProvince__c' to shipping state
					+ " Zip_Postal_Code_Shipping__c,"  //Changed 'nm_PostalCode__c' to shipping pin code
					+ " nm_Gender__c,"
					+ " nm_Centers__r.ID__c,"//to get the center code from centers object
					+ " IC_Name_1__c, "//to get the center name 
					+ " nm_Program__r.StudentZoneProgramCode__c, " //added to get program
					+ " nm_StudentImageUrl__c,"
					+ " OldStudentForExecutive__c,"
					+ " Student_Number_Lateral__c,"
					+ " nm_PermanentAddress__c,"
					+ " Shipping_Street__c,"
					+ " Locality_Name_Shipping__c,"
					+ " Nearest_LandMark_Shipping__c,"
					+ " House_No_Name_Shipping_Account__c"
					+ " FROM Account"
					+ " where nm_StudentStatus__c = 'Confirmed' ";

			qResult = connection.query(soqlQuery);
			boolean done = false;
			
			if (qResult.getSize() > 0) {
				ArrayList<StudentBean> salesforceStudentList = new ArrayList<>();

				logger.info("Total confirmed student accounts in Salesforce: "+ qResult.getSize()+ "  records.");
				
				/*
				 * System.out.println("Logged-in user can see a total of " + qResult.getSize() +
				 * " Account records.");
				 */
				int count =0;
				while (!done) {
					SObject[] records = qResult.getRecords();
					for (int i = 0; i < records.length; ++i) {
						count=i+1;
						SObject s = (SObject) records[i];
						StudentBean student = new StudentBean();
						try {
							XmlObject centers__r = (XmlObject)s.getField("nm_Centers__r");
							XmlObject program_r = (XmlObject)s.getField("nm_Program__r");
							String program = (String)program_r.getField("StudentZoneProgramCode__c");
							String IsSASProgramStudent = (String)s.getField("OldStudentForExecutive__c");
							 
							student.setFirstName((String)s.getField("FirstName"));
							student.setLastName((String)s.getField("LastName"));
							student.setSapid((String)s.getField("nm_StudentNo__c"));
							student.setEmailId((String)s.getField("PersonEmail"));
							student.setMobile((String)s.getField("PersonMobilePhone"));
							student.setProgramname(program);
							student.setAltPhone((String)s.getField("Phone"));
							student.setDob((String)s.getField("nm_DateOfBirth__c"));
							student.setRegDate((String)s.getField("Account_Confirm_Date__c"));
							student.setIsLateral((String)s.getField("Is_Lateral__c"));	
							student.setCity((String)s.getField("City_Shipping_Account__c"));		
							student.setCountry((String)s.getField("Country_Shipping__c"));			
							student.setState((String)s.getField("State_Province_Shipping__c"));	
							student.setPin((String)s.getField("Zip_Postal_Code_Shipping__c"));
							student.setGender((String)s.getField("nm_Gender__c"));
							//student.setCenterName((String)s.getField("nm_Centers__r.ID__c"));
							student.setCenterCode((String)centers__r.getField("ID__c"));
							student.setCenterName((String)s.getField("IC_Name_1__c"));
							student.setImageUrl((String)s.getField("nm_StudentImageUrl__c"));
							student.setPreviousStudentId((String)s.getField("Student_Number_Lateral__c"));
							student.setAddress((String)s.getField("nm_PermanentAddress__c"));	
							student.setStreet((String)s.getField("Shipping_Street__c"));
							student.setLocality((String)s.getField("Locality_Name_Shipping__c"));
							student.setLandMark((String)s.getField("Nearest_LandMark_Shipping__c"));
							student.setHouseNoName((String)s.getField("House_No_Name_Shipping_Account__c"));
							
							if("true".equalsIgnoreCase(IsSASProgramStudent)){
								student.setExistingStudentNoForDiscount(student.getPreviousStudentId());
								student.setPreviousStudentId(null);
							}
							
							String validityMonth = (String)s.getField("Extend_Validity_upto_Month__c");
							String validityYear = (String)s.getField("Extend_Validity_Upto_Year__c");
							if(!StringUtils.isBlank(validityYear)) {
								validityYear = validityYear.replaceAll(",", "");
								validityYear = validityYear.substring(0, 4);
							}
							if(StringUtils.isBlank(validityMonth)){
								student.setValidityEndMonth((String)s.getField("Validity_Month__c"));
							}else{
								student.setValidityEndMonth(validityMonth);
							}
							
							if(StringUtils.isBlank(validityYear)){
								student.setValidityEndYear((String)s.getField("Validity_Year__c"));
							}else{
								student.setValidityEndYear(validityYear);
							}
							//System.out.println("Account " + (i + 1) + ": " + student);

							salesforceStudentList.add(student);
							
						} catch (Exception e) {
							logger.info("Error in creating student bean for "+student);
							e.printStackTrace();
						}
					}
					/*
					 * System.out.
					 * println("IN findSalesforceStudentZoneMismatch salesforceStudentList size : "
					 * ); System.out.println(salesforceStudentList.size());
					 */
					if (qResult.isDone()) {
						done = true;
					} else {
						logger.info("Querying more.....");
						qResult = connection.queryMore(qResult.getQueryLocator());
					}
				}
				logger.info("Created studentbean list from salesforce records "+count+"/"+qResult.getSize());
				//Fetch data from student zone
				studentZoneDAO = (StudentZoneDao)act.getBean("studentZoneDAO");
				HashMap<String, StudentBean>  sapIdStudentsMap = studentZoneDAO.getAllStudents();
				
				//Compare each Salesforce record with Student Zone record
				ArrayList<StudentDataMismatchBean> mismatchList = new ArrayList<>();
				logger.info("Comparing each Salesforce record with Student Zone record");
				
				for (StudentBean studentBean : salesforceStudentList) {
					//System.out.println("*****************");
					//System.out.println(studentBean.toString());
					//System.out.println("*****************");
					boolean isMismatch = false;
					StudentDataMismatchBean mmBean = new StudentDataMismatchBean();
					
					mmBean.setSapid(studentBean.getSapid());mmBean.setFirstName(studentBean.getFirstName());mmBean.setLastName(studentBean.getLastName());
						
						//Added temporary for testing on test server.
						/*StudentBean studentFromStudentZone;
						try{
							 studentFromStudentZone = sapIdStudentsMap.get(studentBean.getSapid().trim());
						}catch(Exception e){
							continue;
						}*/
						//Added temporary for testing on test server.
					
					try {
						StudentBean studentFromStudentZone = sapIdStudentsMap.get(studentBean.getSapid().trim());
						if(studentFromStudentZone != null){
							 if(studentBean.getEmailId() == null || studentFromStudentZone.getEmailId() == null || (!studentBean.getEmailId().equalsIgnoreCase(studentFromStudentZone.getEmailId().trim()))){
								
								 if(StringUtils.isBlank(studentBean.getEmailId())  &&  StringUtils.isBlank(studentFromStudentZone.getEmailId())){
									 
								 }else{
									isMismatch = true;mmBean.setSapid(studentBean.getSapid());mmBean.setFirstName(studentBean.getFirstName());mmBean.setLastName(studentBean.getLastName());
									mmBean.setMismatchType("Email");
									mmBean.setSalesforceValue(studentBean.getEmailId());
									mmBean.setStudentZoneValue(studentFromStudentZone.getEmailId());
									mismatchList.add(mmBean); mmBean = new StudentDataMismatchBean();
								 }
							}
							
							if(studentBean.getMobile() == null || studentFromStudentZone.getMobile() == null || (!studentBean.getMobile().equalsIgnoreCase(studentFromStudentZone.getMobile()))){
								
								if(StringUtils.isBlank(studentBean.getMobile()) && StringUtils.isBlank(studentFromStudentZone.getMobile())){
									
								}else{
									isMismatch = true;mmBean.setSapid(studentBean.getSapid());mmBean.setFirstName(studentBean.getFirstName());mmBean.setLastName(studentBean.getLastName());
									mmBean.setMismatchType("Mobile");
									mmBean.setSalesforceValue(studentBean.getMobile());
									mmBean.setStudentZoneValue(studentFromStudentZone.getMobile());
									mismatchList.add(mmBean); mmBean = new StudentDataMismatchBean();
								}
							}
 
							if(studentBean.getProgramname() == null || studentFromStudentZone.getProgramname() == null || (!studentBean.getProgramname().equalsIgnoreCase(studentFromStudentZone.getProgramname().trim()))){
								if(studentBean.getSapid().equalsIgnoreCase("77120258131")) {
									logger.info("got student 77120258131 with program in salesforce:"+studentFromStudentZone.getProgramname()+" and program in portal:"+studentBean.getProgramname());
								}
								if(StringUtils.isBlank(studentBean.getProgramname()) && StringUtils.isBlank(studentFromStudentZone.getProgramname())){
									
								}else{
									isMismatch = true;mmBean.setSapid(studentBean.getSapid());mmBean.setFirstName(studentBean.getFirstName());mmBean.setLastName(studentBean.getLastName());
									mmBean.setMismatchType("Program");
									mmBean.setSalesforceValue(studentBean.getProgramname());
									mmBean.setStudentZoneValue(studentFromStudentZone.getProgramname());
									mismatchList.add(mmBean); mmBean = new StudentDataMismatchBean();
								}
							}
							
							if(studentBean.getValidityEndMonth() == null || studentFromStudentZone.getValidityEndMonth() == null || (!studentBean.getValidityEndMonth().substring(0, 3).equalsIgnoreCase(studentFromStudentZone.getValidityEndMonth()))){
								
								if(StringUtils.isBlank(studentBean.getValidityEndMonth()) && StringUtils.isBlank(studentFromStudentZone.getValidityEndMonth())){
									
								}else{
									isMismatch = true;mmBean.setSapid(studentBean.getSapid());mmBean.setFirstName(studentBean.getFirstName());mmBean.setLastName(studentBean.getLastName());
									mmBean.setMismatchType("Validity Month");
									mmBean.setSalesforceValue(studentBean.getValidityEndMonth());
									mmBean.setStudentZoneValue(studentFromStudentZone.getValidityEndMonth());
									mismatchList.add(mmBean); mmBean = new StudentDataMismatchBean();
								}
							}
							
							if(studentBean.getValidityEndYear() == null || studentFromStudentZone.getValidityEndYear() == null || (!studentBean.getValidityEndYear().equalsIgnoreCase(studentFromStudentZone.getValidityEndYear()))){
								
								if(StringUtils.isBlank(studentBean.getValidityEndYear()) && StringUtils.isBlank(studentFromStudentZone.getValidityEndYear())){
									
								}else{
									isMismatch = true;mmBean.setSapid(studentBean.getSapid());mmBean.setFirstName(studentBean.getFirstName());mmBean.setLastName(studentBean.getLastName());
									mmBean.setMismatchType("Validity Year");
									mmBean.setSalesforceValue(studentBean.getValidityEndYear());
									mmBean.setStudentZoneValue(studentFromStudentZone.getValidityEndYear());
									mismatchList.add(mmBean); mmBean = new StudentDataMismatchBean();
								}
							}
							
							if(studentBean.getAltPhone() == null || studentFromStudentZone.getAltPhone() == null || (!studentBean.getAltPhone().equalsIgnoreCase(studentFromStudentZone.getAltPhone()))){
								
								if(StringUtils.isBlank(studentBean.getAltPhone())  && StringUtils.isBlank(studentFromStudentZone.getAltPhone()) ){
									
								}else{
									isMismatch = true;mmBean.setSapid(studentBean.getSapid());mmBean.setFirstName(studentBean.getFirstName());mmBean.setLastName(studentBean.getLastName());
									mmBean.setMismatchType("Alt Phone");
									mmBean.setSalesforceValue(studentBean.getAltPhone());
									mmBean.setStudentZoneValue(studentFromStudentZone.getAltPhone());
									mismatchList.add(mmBean); mmBean = new StudentDataMismatchBean();
								}
							}
							
							if(studentBean.getDob() == null || studentFromStudentZone.getDob() == null || (!studentBean.getDob().equalsIgnoreCase(studentFromStudentZone.getDob()))){
								Date sfdcDate = new Date();
								Date portalDate = new Date();
								int date = 0;
								if(StringUtils.isBlank(studentBean.getDob()) && StringUtils.isBlank(studentFromStudentZone.getDob())){
									
								}else{
									boolean sfdcDob = StringUtils.isBlank(studentBean.getDob());
									boolean portalDob = StringUtils.isBlank(studentFromStudentZone.getDob());
									
									if(studentFromStudentZone.getExamMode().equalsIgnoreCase("Offline")){
										
									}else{
										if(sfdcDob){
											studentBean.setDob("No valid date found");
										}else{
											try{
												SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
												sfdcDate = formatter.parse(studentBean.getDob());
											}catch(Exception e){
												try{
													SimpleDateFormat formatter = new SimpleDateFormat("dd.mm.yyyy");
													sfdcDate = formatter.parse(studentBean.getDob());
												}catch(Exception e1){
													try{
														SimpleDateFormat formatter = new SimpleDateFormat("mm/dd/yyyy");
														sfdcDate = formatter.parse(studentBean.getDob());
													}catch(Exception e2){
														studentBean.setDob("No valid date found");
													}
													
												}
											}
										}
										if(portalDob){
											studentFromStudentZone.setDob("No valid date found");
										}else{
											try{
												SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
												portalDate = formatter.parse(studentFromStudentZone.getDob());
												date=sfdcDate.compareTo(portalDate);
											}catch(Exception e){
												try{
													SimpleDateFormat formatter = new SimpleDateFormat("dd.mm.yyyy");
													portalDate = formatter.parse(studentFromStudentZone.getDob());
													date=sfdcDate.compareTo(portalDate);
												}catch(Exception e1){
													try{
														SimpleDateFormat formatter = new SimpleDateFormat("mm/dd/yyyy");
														portalDate = formatter.parse(studentBean.getDob());
														date=sfdcDate.compareTo(portalDate);
													}catch(Exception e2){
														studentFromStudentZone.setDob("No valid date found");
													}
													
												}
											}
										}
									}
									if(date>0 || date<0){
										isMismatch = true;mmBean.setSapid(studentBean.getSapid());mmBean.setFirstName(studentBean.getFirstName());mmBean.setLastName(studentBean.getLastName());
										mmBean.setMismatchType("Date of Birth");
										mmBean.setSalesforceValue(studentBean.getDob());
										mmBean.setStudentZoneValue(studentFromStudentZone.getDob());
										mismatchList.add(mmBean); mmBean = new StudentDataMismatchBean();
									}else if(studentBean.getDob().equalsIgnoreCase("No valid date found") || studentFromStudentZone.getDob().equalsIgnoreCase("No valid date found") ){
										isMismatch = true;mmBean.setSapid(studentBean.getSapid());mmBean.setFirstName(studentBean.getFirstName());mmBean.setLastName(studentBean.getLastName());
										mmBean.setMismatchType("Date of Birth");
										mmBean.setSalesforceValue(studentBean.getDob());
										mmBean.setStudentZoneValue(studentFromStudentZone.getDob());
										mismatchList.add(mmBean); mmBean = new StudentDataMismatchBean();
									}
									/*old code
									 * isMismatch = true;mmBean.setSapid(studentBean.getSapid());mmBean.setFirstName(studentBean.getFirstName());mmBean.setLastName(studentBean.getLastName());
									mmBean.setMismatchType("Date of Birth");
									mmBean.setSalesforceValue(studentBean.getDob());
									mmBean.setStudentZoneValue(studentFromStudentZone.getDob());
									mismatchList.add(mmBean); mmBean = new StudentDataMismatchBean();*/
									}
							}
							
							
							if(studentBean.getRegDate() == null || studentFromStudentZone.getRegDate() == null || (!studentBean.getRegDate().equalsIgnoreCase(studentFromStudentZone.getRegDate()))){
								Date d1 = new Date();
								Date d2 = new Date();
								int d = 0;
								if(StringUtils.isBlank(studentBean.getRegDate())  && StringUtils.isBlank(studentFromStudentZone.getRegDate()) ){
									
								}else{
									boolean sfdcRegDate = StringUtils.isBlank(studentBean.getRegDate());
																	boolean portalRegDate = StringUtils.isBlank(studentFromStudentZone.getRegDate());
																	if(studentFromStudentZone.getExamMode().equalsIgnoreCase("Offline")){
																		
																	}else{
																		if(sfdcRegDate){
																			studentBean.setRegDate("No valid date found");
																		}else{
																			try{
																				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
																				 d1 = formatter.parse(studentBean.getRegDate());
																			}catch(Exception e){
																				try{
																					SimpleDateFormat formatter = new SimpleDateFormat("dd.mm.yyyy");
																					 d1 = formatter.parse(studentBean.getRegDate());
																				}catch(Exception e1){
																					try{
																						SimpleDateFormat formatter = new SimpleDateFormat("mm/dd/yyyy");
																						 d1 = formatter.parse(studentBean.getRegDate());
																					}catch(Exception e2){
																						studentBean.setRegDate("No valid date found");
																					}
																					
																				}
																			}
																		}
																		
																		if(portalRegDate){
																			studentFromStudentZone.setRegDate("No valid date found");
																		}else{
																			try{
																				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
																				 d2 = formatter.parse(studentFromStudentZone.getRegDate());
																				 d=d1.compareTo(d2);
																			}catch(Exception e){
																				try{
																					SimpleDateFormat formatter = new SimpleDateFormat("dd.mm.yyyy");
																					 d2 = formatter.parse(studentFromStudentZone.getRegDate());
																					 d=d1.compareTo(d2);
																				}catch(Exception e1){
																					try{
																						SimpleDateFormat formatter = new SimpleDateFormat("mm/dd/yyyy");
																						 d2 = formatter.parse(studentBean.getRegDate());
																						 d=d1.compareTo(d2);
																					}catch(Exception e2){
																						studentFromStudentZone.setRegDate("No valid date found");
																					}
																					
																				}
																			}
																		}
									 								}
																	
																	if(d>0 || d<0){
																		isMismatch = true;mmBean.setSapid(studentBean.getSapid());mmBean.setFirstName(studentBean.getFirstName());mmBean.setLastName(studentBean.getLastName());
																		mmBean.setMismatchType("Registration Date");
																		mmBean.setSalesforceValue(studentBean.getRegDate());
																		mmBean.setStudentZoneValue(studentFromStudentZone.getRegDate());
																		mismatchList.add(mmBean); mmBean = new StudentDataMismatchBean();
																	}else if(studentBean.getRegDate().equalsIgnoreCase("No valid date found") || studentFromStudentZone.getRegDate().equalsIgnoreCase("No valid date found") ){
																		isMismatch = true;mmBean.setSapid(studentBean.getSapid());mmBean.setFirstName(studentBean.getFirstName());mmBean.setLastName(studentBean.getLastName());
																		mmBean.setMismatchType("Registration Date");
																		mmBean.setSalesforceValue(studentBean.getRegDate());
																		mmBean.setStudentZoneValue(studentFromStudentZone.getRegDate());
																		mismatchList.add(mmBean); mmBean = new StudentDataMismatchBean();
																	}
																}
									 						}
							
							/*
							 * Changed by Pranit on 24 Dec 18
							 * Checking studentBean.getIsLateral() with studentFromStudentZone.getIsLateral()
							 * Converting SFDC islateral value to portal compatible format i.e. "true/false" to "Y/N" 
							 * */
							String convertedValueOfIsLateral = "";
							try {
								 if("true".equalsIgnoreCase(studentBean.getIsLateral())){
									 convertedValueOfIsLateral="Y";
								 }
								 else if("false".equalsIgnoreCase(studentBean.getIsLateral())){
									 convertedValueOfIsLateral="N";
								 }else {
									 convertedValueOfIsLateral = studentBean.getIsLateral();
								}
								
							} catch (Exception e) {
								// TODO Auto-generated catch block
							}
							
							if(convertedValueOfIsLateral == null || studentFromStudentZone.getIsLateral() == null || (!convertedValueOfIsLateral.equalsIgnoreCase(studentFromStudentZone.getIsLateral()))){
								
								if(StringUtils.isBlank(convertedValueOfIsLateral)  && StringUtils.isBlank(studentFromStudentZone.getIsLateral())){
									
								}else{
									isMismatch = true;mmBean.setSapid(studentBean.getSapid());mmBean.setFirstName(studentBean.getFirstName());mmBean.setLastName(studentBean.getLastName());
									mmBean.setMismatchType("Is Lateral");
									mmBean.setSalesforceValue(studentBean.getIsLateral());
									mmBean.setStudentZoneValue(studentFromStudentZone.getIsLateral());
									mismatchList.add(mmBean); mmBean = new StudentDataMismatchBean();
									}
							}
							
							if(studentBean.getCity() == null || studentFromStudentZone.getCity() == null || (!studentBean.getCity().equalsIgnoreCase(studentFromStudentZone.getCity().trim()))){
								
								if(StringUtils.isBlank(studentBean.getCity())  && StringUtils.isBlank(studentFromStudentZone.getCity())){
									
								}else{
									isMismatch = true;mmBean.setSapid(studentBean.getSapid());mmBean.setFirstName(studentBean.getFirstName());mmBean.setLastName(studentBean.getLastName());
									mmBean.setMismatchType("City");
									mmBean.setSalesforceValue(studentBean.getCity());
									mmBean.setStudentZoneValue(studentFromStudentZone.getCity());
									mismatchList.add(mmBean); mmBean = new StudentDataMismatchBean();
									}
							}
							
							if(studentBean.getCountry() == null || studentFromStudentZone.getCountry() == null || (!studentBean.getCountry().equalsIgnoreCase(studentFromStudentZone.getCountry().trim()))){
								
								if(StringUtils.isBlank(studentBean.getCountry()) && StringUtils.isBlank(studentFromStudentZone.getCountry()) ){
									
								}else{
									isMismatch = true;mmBean.setSapid(studentBean.getSapid());mmBean.setFirstName(studentBean.getFirstName());mmBean.setLastName(studentBean.getLastName());
									mmBean.setMismatchType("Country");
									mmBean.setSalesforceValue(studentBean.getCountry());
									mmBean.setStudentZoneValue(studentFromStudentZone.getCountry());
									mismatchList.add(mmBean); mmBean = new StudentDataMismatchBean();
									}
							}
							
							if(studentBean.getState() == null || studentFromStudentZone.getState() == null || (!studentBean.getState().equalsIgnoreCase(studentFromStudentZone.getState().trim()))){
								
								if(StringUtils.isBlank(studentBean.getState()) && StringUtils.isBlank(studentFromStudentZone.getState()) ){
									
								}else{
									isMismatch = true;mmBean.setSapid(studentBean.getSapid());mmBean.setFirstName(studentBean.getFirstName());mmBean.setLastName(studentBean.getLastName());
									mmBean.setMismatchType("State");
									mmBean.setSalesforceValue(studentBean.getState());
									mmBean.setStudentZoneValue(studentFromStudentZone.getState());
									mismatchList.add(mmBean); mmBean = new StudentDataMismatchBean();
									}
							}
							
							if(studentBean.getPin() == null || studentFromStudentZone.getPin() == null || (!studentBean.getPin().equalsIgnoreCase(studentFromStudentZone.getPin().trim()))){
								
								if(StringUtils.isBlank(studentBean.getPin()) && StringUtils.isBlank(studentFromStudentZone.getPin()) ){
									
								}else{
									isMismatch = true;mmBean.setSapid(studentBean.getSapid());mmBean.setFirstName(studentBean.getFirstName());mmBean.setLastName(studentBean.getLastName());
									mmBean.setMismatchType("Pin Code");
									mmBean.setSalesforceValue(studentBean.getPin());
									mmBean.setStudentZoneValue(studentFromStudentZone.getPin());
									mismatchList.add(mmBean); mmBean = new StudentDataMismatchBean();
									}
							}
							
							if(studentBean.getGender() == null || studentFromStudentZone.getGender() == null || (!studentBean.getGender().equalsIgnoreCase(studentFromStudentZone.getGender().trim()))){
								
								if(StringUtils.isBlank(studentBean.getGender())  && StringUtils.isBlank(studentFromStudentZone.getGender())){
									
								}else{
									isMismatch = true;mmBean.setSapid(studentBean.getSapid());mmBean.setFirstName(studentBean.getFirstName());mmBean.setLastName(studentBean.getLastName());
									mmBean.setMismatchType("Gender");
									mmBean.setSalesforceValue(studentBean.getGender());
									mmBean.setStudentZoneValue(studentFromStudentZone.getGender());
									mismatchList.add(mmBean); mmBean = new StudentDataMismatchBean();
									}
							}
							/*
							 * Changed by Pranit on 22 Dec
							 * Checking studentBean.getCenterName() with studentFromStudentZone.getCenterCode
							 * Checking first 15 of sfdc centerName
							 * */
							String trimedSFDCCenterName = "";
							try {
								trimedSFDCCenterName = studentBean.getCenterCode().substring(0, 15);
							} catch (Exception e) {
								// TODO Auto-generated catch block
							}
							
							if(StringUtils.isBlank(trimedSFDCCenterName) || studentFromStudentZone.getCenterCode() == null || (!trimedSFDCCenterName.equalsIgnoreCase(studentFromStudentZone.getCenterCode()))){
								
								if(StringUtils.isBlank(trimedSFDCCenterName)  && StringUtils.isBlank(studentFromStudentZone.getCenterCode()) ){
									
								}else{
									isMismatch = true;mmBean.setSapid(studentBean.getSapid());mmBean.setFirstName(studentBean.getFirstName());mmBean.setLastName(studentBean.getLastName());
									mmBean.setMismatchType("Center Code");
									mmBean.setSalesforceValue(trimedSFDCCenterName);
									mmBean.setStudentZoneValue(studentFromStudentZone.getCenterCode());
									mismatchList.add(mmBean); mmBean = new StudentDataMismatchBean();
									}
							}
							if(studentBean.getImageUrl() == null || studentFromStudentZone.getImageUrl() == null || (!studentBean.getImageUrl().equalsIgnoreCase(studentFromStudentZone.getImageUrl().trim()))){
								
								if(StringUtils.isBlank(studentBean.getImageUrl())  && StringUtils.isBlank(studentFromStudentZone.getImageUrl())  ){
									
								}else{
									isMismatch = true;mmBean.setSapid(studentBean.getSapid());mmBean.setFirstName(studentBean.getFirstName());mmBean.setLastName(studentBean.getLastName());
									mmBean.setMismatchType("Student Photo");
									mmBean.setSalesforceValue(studentBean.getImageUrl());
									mmBean.setStudentZoneValue(studentFromStudentZone.getImageUrl());
									mismatchList.add(mmBean); mmBean = new StudentDataMismatchBean();
									}
							}
							
							if((studentBean.getPreviousStudentId() == null || studentFromStudentZone.getPreviousStudentId() == null || !studentBean.getPreviousStudentId().equalsIgnoreCase(studentFromStudentZone.getPreviousStudentId().trim()))&& StringUtils.isBlank(studentBean.getExistingStudentNoForDiscount())){
								
								if(StringUtils.isBlank(studentBean.getPreviousStudentId())  && StringUtils.isBlank(studentFromStudentZone.getPreviousStudentId()) ){
									
								}else{
									isMismatch = true;mmBean.setSapid(studentBean.getSapid());mmBean.setFirstName(studentBean.getFirstName());mmBean.setLastName(studentBean.getLastName());
									mmBean.setMismatchType("Previous Student Id");
									mmBean.setSalesforceValue(studentBean.getPreviousStudentId());
									mmBean.setStudentZoneValue(studentFromStudentZone.getPreviousStudentId());
									mismatchList.add(mmBean); mmBean = new StudentDataMismatchBean();
									}
							}
							
							if((studentBean.getExistingStudentNoForDiscount() == null || studentFromStudentZone.getExistingStudentNoForDiscount() == null || !studentBean.getExistingStudentNoForDiscount().equalsIgnoreCase(studentFromStudentZone.getExistingStudentNoForDiscount().trim())) && StringUtils.isBlank(studentBean.getPreviousStudentId())){
								
								if(StringUtils.isBlank(studentBean.getExistingStudentNoForDiscount()) && StringUtils.isBlank(studentFromStudentZone.getExistingStudentNoForDiscount()) ){
									
								}else{
									isMismatch = true;mmBean.setSapid(studentBean.getSapid());mmBean.setFirstName(studentBean.getFirstName());mmBean.setLastName(studentBean.getLastName());
									mmBean.setMismatchType("ExistingStudentNoForDiscount");
									mmBean.setSalesforceValue(studentBean.getExistingStudentNoForDiscount());
									mmBean.setStudentZoneValue(studentFromStudentZone.getExistingStudentNoForDiscount());
									mismatchList.add(mmBean); mmBean = new StudentDataMismatchBean();
									}
							}
							
							//Added shipping address fields of Street/Locality/LandMark/HouseNo.Name for mismatch check start
							if(studentBean.getStreet() == null || studentFromStudentZone.getStreet() == null || (!studentBean.getStreet().equalsIgnoreCase(studentFromStudentZone.getStreet().trim()))){
								
								if(StringUtils.isBlank(studentBean.getStreet())  && StringUtils.isBlank(studentFromStudentZone.getStreet()) ){
									
								}else{
									isMismatch = true;mmBean.setSapid(studentBean.getSapid());mmBean.setFirstName(studentBean.getFirstName());mmBean.setLastName(studentBean.getLastName());
									mmBean.setMismatchType("Shipping Street");
									mmBean.setSalesforceValue(studentBean.getStreet());
									mmBean.setStudentZoneValue(studentFromStudentZone.getStreet());
									mismatchList.add(mmBean); mmBean = new StudentDataMismatchBean();
									}
							}
							
							if(studentBean.getLocality() == null || studentFromStudentZone.getLocality() == null || (!studentBean.getLocality().equalsIgnoreCase(studentFromStudentZone.getLocality().trim()))){
								
								if(StringUtils.isBlank(studentBean.getLocality())  && StringUtils.isBlank(studentFromStudentZone.getLocality()) ){
									
								}else{
									isMismatch = true;mmBean.setSapid(studentBean.getSapid());mmBean.setFirstName(studentBean.getFirstName());mmBean.setLastName(studentBean.getLastName());
									mmBean.setMismatchType("Locality");
									mmBean.setSalesforceValue(studentBean.getLocality());
									mmBean.setStudentZoneValue(studentFromStudentZone.getLocality());
									mismatchList.add(mmBean); mmBean = new StudentDataMismatchBean();
									}
							}
							
							if(studentBean.getLandMark() == null || studentFromStudentZone.getLandMark() == null || (!studentBean.getLandMark().equalsIgnoreCase(studentFromStudentZone.getLandMark().trim()))){
								
								if(StringUtils.isBlank(studentBean.getLandMark())  && StringUtils.isBlank(studentFromStudentZone.getLandMark()) ){
									
								}else{
									isMismatch = true;mmBean.setSapid(studentBean.getSapid());mmBean.setFirstName(studentBean.getFirstName());mmBean.setLastName(studentBean.getLastName());
									mmBean.setMismatchType("LandMark");
									mmBean.setSalesforceValue(studentBean.getLandMark());
									mmBean.setStudentZoneValue(studentFromStudentZone.getLandMark());
									mismatchList.add(mmBean); mmBean = new StudentDataMismatchBean();
									}
							}
							
							if(studentBean.getHouseNoName() == null || studentFromStudentZone.getHouseNoName() == null || (!studentBean.getHouseNoName().equalsIgnoreCase(studentFromStudentZone.getHouseNoName().trim()))){
								
								if(StringUtils.isBlank(studentBean.getHouseNoName())  && StringUtils.isBlank(studentFromStudentZone.getHouseNoName()) ){
									
								}else{
									isMismatch = true;mmBean.setSapid(studentBean.getSapid());mmBean.setFirstName(studentBean.getFirstName());mmBean.setLastName(studentBean.getLastName());
									mmBean.setMismatchType("House No./Name");
									mmBean.setSalesforceValue(studentBean.getHouseNoName());
									mmBean.setStudentZoneValue(studentFromStudentZone.getHouseNoName());
									mismatchList.add(mmBean); mmBean = new StudentDataMismatchBean();
									}
							}
							//Added shipping address fields of Street/Locality/LandMark/HouseNo.Name for mismatch check end
							
							
							if(studentBean.getCenterName() == null || studentFromStudentZone.getCenterName() == null || (!studentBean.getCenterName().equalsIgnoreCase(studentFromStudentZone.getCenterName().trim()))){
								
								if(StringUtils.isBlank(studentBean.getCenterName())  && StringUtils.isBlank(studentFromStudentZone.getCenterName())  ){
									
								}else{
									isMismatch = true;mmBean.setSapid(studentBean.getSapid());mmBean.setFirstName(studentBean.getFirstName());mmBean.setLastName(studentBean.getLastName());
									mmBean.setMismatchType("Center Name");
									mmBean.setSalesforceValue(studentBean.getCenterName());
									mmBean.setStudentZoneValue(studentFromStudentZone.getCenterName());
									mismatchList.add(mmBean); mmBean = new StudentDataMismatchBean();
									}
							}


						}else{
							isMismatch = true;mmBean.setSapid(studentBean.getSapid());mmBean.setFirstName(studentBean.getFirstName());mmBean.setLastName(studentBean.getLastName());
							mmBean.setMismatchType("Student Not Found in Student Zone");
							logger.info("Student Not Found in Student Zone");
						}
					} catch (Exception e) {
						logger.info("Error in comparing for student:"+studentBean);
					}
					/*Commented by PS as was giving extra records
					 * if(isMismatch){
						mismatchList.add(mmBean); mmBean = new StudentDataMismatchBean();
					}*/
				}
				
				Collections.sort(mismatchList);
				logger.info("salesforceStudentList size : "+salesforceStudentList.size());
				logger.info("mismatchList Size : "+mismatchList.size());
				/*for (StudentDataMismatchBean studentDataMismatchBean : mismatchList) {
					//System.out.println(studentDataMismatchBean);
				}*/
				
				// get All Student whose registration for current Acads Month and Year not present but record present in exam.students
				HashMap<String, StudentBean> getAllStudentsNotPresentInRegistrationDataTableForCurrentAcadMonthAndYear = studentZoneDAO.getAllStudentsNotPresentInRegistrationDataTableForCurrentAcadMonthAndYear(CURRENT_ACAD_MONTH, CURRENT_ACAD_YEAR);
				
				// get All Student whose program in registration different for current Acads Month and Year 
				HashMap<String, StudentBean> getAllStudentsProgramDifferentfromRegistrationDataTableForCurrentAcadMonthAndYear = studentZoneDAO.getAllStudentsProgramDifferentfromRegistrationDataTableForCurrentAcadMonthAndYear(CURRENT_ACAD_MONTH, CURRENT_ACAD_YEAR);
				List<ArrayList<StudentDataMismatchBean>> mismatchListSplitted = split(mismatchList);
				logger.info(""+mismatchList.size());
				
				//sendEmailOfMismatch(new ArrayList<StudentDataMismatchBean>(),bean,new HashMap<String, StudentBean>());
				for(ArrayList<StudentDataMismatchBean> bean : mismatchListSplitted) {
					logger.info("mismatchListSplitted[i] size"+bean.size()); 
					sendEmailOfMismatch(bean,getAllStudentsNotPresentInRegistrationDataTableForCurrentAcadMonthAndYear,getAllStudentsProgramDifferentfromRegistrationDataTableForCurrentAcadMonthAndYear);
				} 
			} else {
				logger.info("No records found.");
			}
		}catch (ApiFault e) {  
			init();
		}catch(ConnectionException ce){
			mailer.mailStackTrace("Error : Unable to connect to salesforce : findSalesforceStudentZoneMismatch ", ce);
			sbean.setError(ce + "");
			logger.info("Error : Unable to connect to salesforce : findSalesforceStudentZoneMismatch ");
		}catch(Exception e){
			e.printStackTrace();
			mailer.mailStackTrace("Error: Mismatch job not running: findSalesforceStudentZoneMismatch", e);
			sbean.setError(e + "");
			logger.info("Error: Mismatch job not running: findSalesforceStudentZoneMismatch");
		}
		sbean.setSyncType("Salesforce Studentzone Mismatch");
		studentZoneDAO.updateLastSyncedTime(sbean);
	}
	
	private void sendEmailOfMismatch(ArrayList<StudentDataMismatchBean> mismatchList,HashMap<String,StudentBean> getAllStudentsNotPresentInRegistrationDataTableForCurrentAcadMonthAndYear,HashMap<String, StudentBean> getAllStudentsProgramDifferentfromRegistrationDataTableForCurrentAcadMonthAndYear) {
		mailer.sendMismatchEmail(mismatchList,getAllStudentsNotPresentInRegistrationDataTableForCurrentAcadMonthAndYear,getAllStudentsProgramDifferentfromRegistrationDataTableForCurrentAcadMonthAndYear);
		
	}

	@RequestMapping(value = "/syncSalesforceRegistrationMismatchData", method = { RequestMethod.GET,
	RequestMethod.POST })
	public void callsyncSalesforceRegistrationMismatchData(HttpServletRequest request, HttpServletResponse response)
	throws IOException {
		syncSalesforceRegistrationMismatchData(Integer.parseInt(SFDC_API_MAX_RETRY_COUNT));
	}
	@Scheduled(fixedDelay=24*60*60*1000)
	public void syncSalesforceRegistrationMismatchDataScheduler(){
		syncSalesforceRegistrationMismatchData(Integer.parseInt(SFDC_API_MAX_RETRY_COUNT));
	}	
	public void syncSalesforceRegistrationMismatchData(int retryCount){	
		 
		  if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){ System.out.
		  println("Not Running syncSalesforceRegistrationMismatchData since it is not PROD "
		  ); return; }
		 
		 
		  
		logger.info("syncSalesforceRegistrationMismatchData: Querying Student Master details from SFDC ");
		//mailer.mailStackTrace("Prod: Mismatch job running: syncSalesforceRegistrationMismatchData",new Exception("Started Query"));
		/*
		 * ConnectorConfig config = new ConnectorConfig();
		 * config.setUsername(SFDC_USERID); config.setPassword(SFDC_PASSWORD_TOKEN);
		 * 
		 * System.out.println("SFDC_USERID = "+SFDC_USERID);
		 * System.out.println("SFDC_PASSWORD_TOKEN = "+SFDC_PASSWORD_TOKEN);
		 */
		
		int UPDATE_BATCH_SIZE = 5;
		
		QueryResult qResult = new QueryResult();

		try {
			
			HashMap<String, StudentBean> studentsMap = studentZoneDAO.getAllRegistrationDataTable();
			logger.info("getAllRegistrationDataTable List: "+studentsMap.size());
			ArrayList<StudentBean> studentNotInRegTable = new ArrayList<StudentBean>();
			
			/*
			 * connection = Connector.newConnection(config); // query for Student Master
			 * records for give SFDC record Id
			 * System.out.println("Auth EndPoint: "+config.getAuthEndpoint());
			 * System.out.println("Service EndPoint: "+config.getServiceEndpoint());
			 * System.out.println("Username: "+config.getUsername());
			 * System.out.println("SessionId: "+config.getSessionId());
			 */
			
			String soqlQuery =" SELECT "
					+ " Account.Id,id,"
					+ " Account.Synced_With_LDAP__c,"
					+ " Account.nm_StudentNo__c," // Sapid
					+ " Account.FirstName,"
					+ " nm_Program__r.StudentZoneProgramCode__c, " //Program
					+ " Account.Account_Confirm_Date__c,  "
					+ " CloseDate,"
					+ " Account.LastName, "
					+ " Drive_Month__c,"
					+ " nm_Semester__c," //Sem
					+ " Synced_With_StudentZone__c,"
					+ " nm_Session__c," // Month
					+ " nm_Year__c, " //Year
					+ " Program_Structure__c"
					//+ " nm_StudentProgram__r.nm_PreviousProgram__c,"
					//+ " nm_StudentProgram__r.nm_PreviousProgram__r.StudentZoneProgramCode__c" //Previous Program
					+ " FROM Opportunity "
					+"  where Account.nm_StudentStatus__c = 'Confirmed' and StageName = 'Closed Won' order by lastModifiedDate desc";
			
			//System.out.println("syncSalesforceRegistrationMismatchData Query: "+soqlQuery);
			qResult = connection.query(soqlQuery);
			boolean done = false;
			
			if (qResult.getSize() > 0) {

				logger.info("Logged-in user can see a total of "
						+ qResult.getSize() + " Opportunity records.");
				while (!done) {
					SObject[] records = qResult.getRecords();
					for (int i = 0; i < records.length; ++i) {
						
						SObject s = (SObject) records[i];
						XmlObject account_r = (XmlObject)s.getField("Account");
						XmlObject program_r = (XmlObject)s.getField("nm_Program__r");
						//XmlObject previous_program_r = (XmlObject)s.getField("nm_StudentProgram__r");
						//XmlObject previous_program_val_r = (XmlObject)s.getField("nm_StudentProgram__r.nm_PreviousProgram__r");
						
						String sapId = (String)account_r.getField("nm_StudentNo__c");
						String program = (String)program_r.getField("StudentZoneProgramCode__c");
						String sem = (String)s.getField("nm_Semester__c");
						/*String previousprogram = "";
						String previousprogramval = "";
						try{
						 previousprogram = (String)previous_program_r.getField("nm_PreviousProgram__c");
						 previousprogramval = (String)previous_program_val_r.getField("StudentZoneProgramCode__c");
						}catch(Exception e){
							
						}*/
						try {
						
						String year = (String)s.getField("nm_Year__c");
						year = year.replaceAll(",", "");
						year = year.substring(0, 4);
						String month = ((String)s.getField("nm_Session__c")).substring(0, 3);
						String key= sapId+"|"+program+"|"+sem+"|"+year+"|"+month;
							 
							if(!studentsMap.containsKey(key)){
							//	String key2 = sapId+"|"+previousprogramval+"|"+sem+"|"+year+"|"+month;
							//	if(!studentsMap.containsKey(key2)){
									System.out.println("Student not in Reg Table "+key);
									//System.out.println("PPC :: "+previousprogramval);	
									StudentBean student = new StudentBean();
									student.setFirstName((String)account_r.getField("FirstName"));
									student.setLastName((String)account_r.getField("LastName"));
									student.setSapid((String)account_r.getField("nm_StudentNo__c"));
									student.setProgram((String)program_r.getField("StudentZoneProgramCode__c"));
									student.setYear(year);
									student.setMonth(((String)s.getField("nm_Session__c")).substring(0, 3));
									student.setSem((String)s.getField("nm_Semester__c"));
									student.setCloseDate((String)s.getField("CloseDate"));
									
									studentNotInRegTable.add(student);
								//}
							  }
						} catch (Exception e) {
							logger.info("Failed to compare registration data for sapid:"+sapId+" sem:"+sem);
							e.printStackTrace();
						}
				}
					if (qResult.isDone()) {
						done = true;
					} else {
						logger.info("Querying more.....");
						qResult = connection.queryMore(qResult.getQueryLocator());
					}	
			}
				logger.info("studentNotInRegTable list size "+studentNotInRegTable.size());
				logger.info("sending  Email: Salesforce-StudentZone Registration MisMatch Error");
				mailer.sendSyncStudentZoneMissingRegistrationDataEmail(studentNotInRegTable);
			
			}
			
			
			/*Old Code
			 * connection = Connector.newConnection(config);
			// query for Student Master records for give SFDC record Id
			System.out.println("Auth EndPoint: "+config.getAuthEndpoint());
			System.out.println("Service EndPoint: "+config.getServiceEndpoint());
			System.out.println("Username: "+config.getUsername());
			System.out.println("SessionId: "+config.getSessionId());

			String soqlQuery =" SELECT Account.Id,id,Account.nm_Password__c,Account.Synced_With_LDAP__c,Account.nm_StudentNo__c,Account.OldStudentForExecutive__c,Is_Re_Registration_Payment__c, Account.FirstName, Account.nm_Gender__c , nm_Program__r.Name , Account.PersonEmail, Account.Account_Confirm_Date__c ,  "
							 +" Account.PersonMobilePhone , Account.Phone ,Account.nm_DateOfBirth__c , CloseDate , Account.Is_Lateral__c ,Account.nm_PermanentAddress__c , "
							 +" Account.LastName, Drive_Month__c, Enrollment_Year__c , nm_Semester__c  , Account.nm_City__c , Account.nm_StateProvince__c , Account.nm_Country__c , Synced_With_StudentZone__c ,"
							 +" Account.nm_PostalCode__c , ICID__c , Account.IC_Name_1__c ,nm_Session__c,nm_Year__c, Account.Validity_Month__c , Account.Validity_Year__c , Account.Extend_Validity_upto_Month__c ,  Account.Extend_Validity_Upto_Year__c , Program_Structure__c , Account.nm_StudentImageUrl__c , Account.Student_Number_Lateral__c "
							 +" FROM Opportunity "
							 +" where Account.nm_StudentStatus__c = 'Confirmed' and StageName = 'Closed Won'  and Synced_With_StudentZone__c = true and nm_Session__c =\'"+MISMATCH_ACAD_MONTH+"\' and nm_Year__c = "+MISMATCH_ACAD_YEAR+"" ;
			
			System.out.println("syncSalesforceRegistrationMismatchData Query: "+soqlQuery);
			qResult = connection.query(soqlQuery);
			boolean done = false;
			
			ArrayList<String> syncSapIdForDrive = new ArrayList<String>();
			if (qResult.getSize() > 0) {
				ArrayList<StudentBean> salesforceStudentList = new ArrayList<>();

				System.out.println("Logged-in user can see a total of "
						+ qResult.getSize() + " Account records.");
				while (!done) {
					SObject[] records = qResult.getRecords();
					for (int i = 0; i < records.length; ++i) {
						
						SObject s = (SObject) records[i];
						XmlObject account_r = (XmlObject)s.getField("Account");
						String sapId = (String)account_r.getField("nm_StudentNo__c");
						syncSapIdForDrive.add(sapId);
					}
				}
			}
			System.out.println("studentRegistration Mismatch :"+syncSapIdForDrive.size());
			// finding Mismacth Cases where sync with student zone checkbox true on Salesforce but record not present in Student zone
			studentZoneDAO = (StudentZoneDao)act.getBean("studentZoneDAO");
			HashMap<String, StudentBean> studentsMap = studentZoneDAO.getAllSyncStudentsNotPresentInRegistrationDataTable(CURRENT_ACAD_MONTH,CURRENT_ACAD_YEAR,syncSapIdForDrive);
			
			if(!studentsMap.isEmpty()){
				mailer.sendSyncStudentZoneMissingRegistrationDataEmail(studentsMap,MISMATCH_ACAD_MONTH,MISMATCH_ACAD_YEAR);
			}*/
		} catch (ApiFault e) { 
			logger.info("Caught ApiFault");
			e.printStackTrace();
			if(retryCount > 0) {
				init();
				syncSalesforceRegistrationMismatchData(retryCount - 1);
			}
		}catch(Exception e){
			logger.info(e.getMessage()); 
			e.printStackTrace();
			logger.info("Error: Mismatch job not running: syncSalesforceRegistrationMismatchData");
			mailer.mailStackTrace("Error: Mismatch job not running: syncSalesforceRegistrationMismatchData", e);
		}
		SchedulerApisBean sbean = new SchedulerApisBean();
		sbean.setSyncType("Salesforce Registration Data Mismatch");
		logger.info("updating last sync time");
		studentZoneDAO.updateLastSyncedTime(sbean);
	}

	public StudentBean createStudentBeanFromSalesforceData(SObject s)
	{
		StudentBean student = new StudentBean();
		HashMap<String, String> consumerTypeList = studentZoneDAO.getAllConsumerTypes();
		HashMap<String, String> prgmstrucTypeList = studentZoneDAO.getAllProgramStructureTypes();
		HashMap<String, String> prgmTypeList = studentZoneDAO.getAllProgramTypes();
		HashMap<String, String> consumerMasterTypeList = studentZoneDAO.getConsumerMasterList();
		XmlObject program2_r = (XmlObject)s.getField("nm_Program2__r");
		XmlObject program_r = (XmlObject)s.getField("nm_Program__r");
		XmlObject student_program_r = (XmlObject)s.getField("nm_StudentProgram__r");
		student.setProgram((String)program_r.getField("StudentZoneProgramCode__c"));
		student.setOldProgram((String)student_program_r.getField("Previous_program_name__c"));
		XmlObject account_r = (XmlObject)s.getField("Account");
		String extendedValidityEndMonth = "";
		String extendedValidityEndYear = "";
	try{
         extendedValidityEndMonth = (String)account_r.getField("Extend_Validity_upto_Month__c");
	}catch(Exception e){
		
	}
	try{
		extendedValidityEndYear = (String)account_r.getField("Extend_Validity_Upto_Year__c") ;
	}catch(Exception e){
		
	}
		String validityEndMonth = (String)account_r.getField("Validity_Month__c");
		String enrollmentMonth = (String)s.getField("Drive_Month__c");
		String batchName = (String)s.getField("Batch__c");
		String isLateral = (String)account_r.getField("Is_Lateral__c");
		String acadMonth = (String)s.getField("nm_Session__c");
		String IsSASProgramStudent = (String)account_r.getField("OldStudentForExecutive__c");
		
		// Student Validation 
		if("Jun".equalsIgnoreCase(validityEndMonth) || "June".equalsIgnoreCase(validityEndMonth)){
			validityEndMonth = "Jun";
		}else if("Dec".equalsIgnoreCase(validityEndMonth) || "December".equalsIgnoreCase(validityEndMonth)){
			validityEndMonth = "Dec";
		}else if("Sep".equalsIgnoreCase(validityEndMonth) || "September".equalsIgnoreCase(validityEndMonth)){
			validityEndMonth = "Sep";
		}else if("Apr".equalsIgnoreCase(validityEndMonth) || "April".equalsIgnoreCase(validityEndMonth)){
			validityEndMonth = "Apr";
		}else if("Oct".equalsIgnoreCase(validityEndMonth) || "October".equalsIgnoreCase(validityEndMonth)){
			validityEndMonth = "Oct";//For 22 month Advanced Certificate Program
		}else if("Aug".equalsIgnoreCase(validityEndMonth) || "August".equalsIgnoreCase(validityEndMonth)){
			validityEndMonth = "Aug";//For SAS program
		}else if("Feb".equalsIgnoreCase(validityEndMonth) || "February".equalsIgnoreCase(validityEndMonth)){
			validityEndMonth = "Feb";
		}else if("Mar".equalsIgnoreCase(validityEndMonth) || "March".equalsIgnoreCase(validityEndMonth)){
			validityEndMonth = "Mar";
		}else if("May".equalsIgnoreCase(validityEndMonth) || "May".equalsIgnoreCase(validityEndMonth)){
			validityEndMonth = "May";//For MBAX program
		}else if("Nov".equalsIgnoreCase(validityEndMonth) || "November".equalsIgnoreCase(validityEndMonth)){
			validityEndMonth = "Nov";//For MBAX program
		}
		
		
		if("Jun".equalsIgnoreCase(extendedValidityEndMonth) || "June".equalsIgnoreCase(extendedValidityEndMonth)){
			extendedValidityEndMonth = "Jun";
		}else if("Dec".equalsIgnoreCase(extendedValidityEndMonth) || "December".equalsIgnoreCase(extendedValidityEndMonth)){
			extendedValidityEndMonth = "Dec";
		}else if("Sep".equalsIgnoreCase(extendedValidityEndMonth) || "September".equalsIgnoreCase(extendedValidityEndMonth)){
			extendedValidityEndMonth = "Sep";
		}else if("Apr".equalsIgnoreCase(extendedValidityEndMonth) || "April".equalsIgnoreCase(extendedValidityEndMonth)){
			extendedValidityEndMonth = "Apr";
		}else if("Oct".equalsIgnoreCase(extendedValidityEndMonth) || "October".equalsIgnoreCase(extendedValidityEndMonth)){
			extendedValidityEndMonth = "Oct";//For 22 month Advanced Certificate Program
		}else if("Aug".equalsIgnoreCase(extendedValidityEndMonth) || "August".equalsIgnoreCase(extendedValidityEndMonth)){
			extendedValidityEndMonth = "Aug";//For SAS program
		}else if("Feb".equalsIgnoreCase(extendedValidityEndMonth) || "February".equalsIgnoreCase(extendedValidityEndMonth)){
			extendedValidityEndMonth = "Feb";
		}else if("Mar".equalsIgnoreCase(extendedValidityEndMonth) || "March".equalsIgnoreCase(extendedValidityEndMonth)){
			extendedValidityEndMonth = "Mar";
		}else if("May".equalsIgnoreCase(extendedValidityEndMonth) || "May".equalsIgnoreCase(extendedValidityEndMonth)){
			validityEndMonth = "May";//For MBAX program
		}else if("Nov".equalsIgnoreCase(extendedValidityEndMonth) || "November".equalsIgnoreCase(extendedValidityEndMonth)){
			validityEndMonth = "Nov";//For MBAX program
		}
		
		
		if("Jan".equalsIgnoreCase(enrollmentMonth)){
			enrollmentMonth = "Jan";
		}else if("Jul".equalsIgnoreCase(enrollmentMonth)){
			enrollmentMonth = "Jul";
		}else if("Oct".equalsIgnoreCase(enrollmentMonth)){
			enrollmentMonth = "Oct";
		}else if("Dec".equalsIgnoreCase(enrollmentMonth)){
			enrollmentMonth = "Dec";//For MBAX program
		}else if("Jun".equalsIgnoreCase(enrollmentMonth)){
			enrollmentMonth = "Jun";//For MBAX program
		}
		
		
		if("Jan".equalsIgnoreCase(acadMonth) || "January".equalsIgnoreCase(acadMonth)){
			acadMonth = "Jan";
		}else if("Jul".equalsIgnoreCase(acadMonth) || "July".equalsIgnoreCase(acadMonth)){
			acadMonth = "Jul";
		}else if("Oct".equalsIgnoreCase(acadMonth) || "October".equalsIgnoreCase(acadMonth)){
			acadMonth = "Oct";
		}else if("Dec".equalsIgnoreCase(acadMonth) || "December".equalsIgnoreCase(acadMonth)){
			acadMonth = "Dec";
		}else if("Sep".equalsIgnoreCase(acadMonth) || "September".equalsIgnoreCase(acadMonth)){
			acadMonth = "Sep";
		}else if("Apr".equalsIgnoreCase(acadMonth) || "April".equalsIgnoreCase(acadMonth)){
			acadMonth = "Apr";
		}else if("Mar".equalsIgnoreCase(acadMonth) || "March".equalsIgnoreCase(acadMonth)){
			acadMonth = "Mar";
		}else if("Jun".equalsIgnoreCase(acadMonth) || "June".equalsIgnoreCase(acadMonth)){
			acadMonth = "Jun";
		}
		
		
		if("true".equalsIgnoreCase(isLateral)){
			//Salesforce gives value as true/false not as Y/N, So convert to Y/N
			isLateral = "Y";
		}else if("false".equalsIgnoreCase(isLateral)){
			//Salesforce gives value as true/false not as Y/N, So convert to Y/N
			isLateral = "N";
		}
		student.setSpecializationType((String)account_r.getField("Specialization_Type__c"));
		student.setSpecialisation1((String)program_r.getField("Name"));
		try {
			student.setSpecialisation2((String)program2_r.getField("Name"));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		student.setFirstName((String)account_r.getField("FirstName"));
		String lastName = (String) account_r.getField("LastName");
		student.setLastName(StudentProfileUtils.isValidLastName(lastName) ? lastName : "");				//replaces lastName as empty String if dot (period), for card: 14965
		
		student.setDob((String)account_r.getField("nm_DateOfBirth__c"));
		student.setSapid((String)account_r.getField("nm_StudentNo__c"));
		student.setEmailId((String)account_r.getField("PersonEmail"));
		student.setMobile((String)account_r.getField("PersonMobilePhone"));
		student.setGender((String)account_r.getField("nm_Gender__c"));
		student.setAltPhone((String)account_r.getField("Phone"));
		student.setIsLateral(isLateral);
		student.setAddress((String)account_r.getField("nm_PermanentAddress__c"));
		/*student.setCountry((String)account_r.getField("nm_Country__c"));
		student.setPin((String)account_r.getField("nm_PostalCode__c"));*/
		student.setCenterName((String)account_r.getField("IC_Name_1__c"));

		/*student.setState((String)account_r.getField("nm_StateProvince__c"));
		student.setCity((String)account_r.getField("nm_City__c"));*/
		student.setPrgmStructApplicable((String)s.getField("Program_Structure__c"));
		student.setBloodGroup((String)account_r.getField("nm_BloodGroup__c"));
		student.setLc((String)account_r.getField("LC_Name__c"));
		
		try{
			String consumerTypeId ="";
			if(consumerTypeList.size()>0){
				if(consumerTypeList.containsValue(student.getCenterName().toUpperCase()) || consumerTypeList.containsValue(student.getCenterName().toLowerCase()) || consumerTypeList.containsValue(student.getCenterName())){
					student.setConsumerType(student.getCenterName());
				}else if("EPBM".equalsIgnoreCase(student.getProgram()) || "MPDV".equalsIgnoreCase(student.getProgram())){
					student.setConsumerType("SAS");
				}else{
					student.setConsumerType("Retail");
				}
				
				for(Map.Entry<String, String> entry : consumerTypeList.entrySet()){
					if (entry.getValue().equalsIgnoreCase(student.getConsumerType())){
						consumerTypeId = entry.getKey();
					}
				}
			}
			
			String programId = "";
			for(Map.Entry<String, String> entry : prgmTypeList.entrySet()){
				if (entry.getValue().equalsIgnoreCase(student.getProgram())){
					programId = entry.getKey();
				}
			}
			
			String program_struct_id = "";
			for(Map.Entry<String, String> entry : prgmstrucTypeList.entrySet()){
				if (entry.getValue().equalsIgnoreCase(student.getPrgmStructApplicable())){
					program_struct_id = entry.getKey();
				}
			}
			
			String consumer_master_key = programId+"|"+program_struct_id+"|"+consumerTypeId;
			System.out.println("consumer_master_key on first sync from salesforce p|ps|ct--- "+consumer_master_key);
		    student.setConsumerProgramStructureId(consumerMasterTypeList.get(consumer_master_key));
		}catch(Exception e){
			e.printStackTrace();
		}

		/*student.setState((String)account_r.getField("nm_StateProvince__c"));
		student.setCity((String)account_r.getField("nm_City__c"));*/
		student.setHouseNoName((String)account_r.getField("House_No_Name_Shipping_Account__c"));
		student.setStreet((String)account_r.getField("Shipping_Street__c"));
		student.setLocality((String)account_r.getField("Locality_Name_Shipping__c"));
		student.setLandMark((String)account_r.getField("Nearest_LandMark_Shipping__c"));
		student.setCity((String)account_r.getField("City_Shipping_Account__c"));		
		student.setState((String)account_r.getField("State_Province_Shipping__c"));	
		student.setCountry((String)account_r.getField("Country_Shipping__c"));			
		student.setPin((String)account_r.getField("Zip_Postal_Code_Shipping__c"));
		student.setHighestQualification((String)account_r.getField("Highest_Qualification__c"));
		student.setAge(((String)account_r.getField("age_according_to_admission__c")).substring(0, 2));

		
		if(!StringUtils.isBlank(extendedValidityEndYear)) {
			extendedValidityEndYear = extendedValidityEndYear.replaceAll(",", "");
			extendedValidityEndYear = extendedValidityEndYear.substring(0, 4);
		}
		
		
		if( StringUtils.isBlank(extendedValidityEndYear )  ){
			student.setValidityEndYear((String)account_r.getField("Validity_Year__c"));
		}else{
			student.setValidityEndYear(extendedValidityEndYear);
		}
		
		if(StringUtils.isBlank(extendedValidityEndMonth)){
			student.setValidityEndMonth(validityEndMonth);
		}else{
			student.setValidityEndMonth(extendedValidityEndMonth);
		}
		
	
		
		student.setImageUrl((String)account_r.getField("nm_StudentImageUrl__c"));
		student.setPreviousStudentId((String)account_r.getField("Student_Number_Lateral__c"));
		student.setAccountId((String)account_r.getField("Id"));
		student.setRegDate((String)account_r.getField("Account_Confirm_Date__c"));
		student.setAccountSyncedWithLDAP((String)account_r.getField("Synced_With_LDAP__c"));
		student.setPassword((String)account_r.getField("nm_NewPassword__c"));
		
		student.setBatchName(batchName);
		student.setEnrollmentMonth(enrollmentMonth);
		student.setEnrollmentYear((String)s.getField("Enrollment_Year__c"));
		student.setSem((String)s.getField("nm_Semester__c"));
		student.setCenterCode((String)s.getField("ICID__c"));
		student.setOpportunityId((String)s.getField("Id"));
		student.setOpportunitySyncedWithStudentZone((String)s.getField("Synced_With_StudentZone__c"));
		student.setYear((String)s.getField("nm_Year__c"));
		student.setMonth(acadMonth);
		student.setIsReReg((String)s.getField("Is_Re_Registration_Payment__c"));
		
		student.setCreatedBy("Admin User");
		student.setLastModifiedBy("Admin User");
		
		student.setFatherName((String)account_r.getField("Father_First_Name__c"));
		student.setMotherName((String)account_r.getField("Mother_First_Name__c"));
		
		// for SAS Student put previous student Id in existingStuentForDiscount field 
		if("true".equalsIgnoreCase(IsSASProgramStudent)){
			student.setExistingStudentNoForDiscount(student.getPreviousStudentId());
			student.setPreviousStudentId(null);
		}
		
		//check and add the value for the field for CS
		if((String)account_r.getField("Career_Service__c") != null) {
			student.setPurhcasedCSProduct((String)account_r.getField("Career_Service__c"));
		}
		
		return student;
	}
	
	// create User In LDAP and return Error Records 
	public ArrayList<Person> createUserInLDAP(List<SObject> lstAccountToUpdate , List<StudentBean> studentToCreateInLDAP, ArrayList<SObject> accountSObjectListToUpdateBackInSalesforce)
	{
		// Create Users in LDAP 
		LDAPDao dao = (LDAPDao)act.getBean("ldapdao");
		ArrayList<Person> ldapCreateUserList = mapStudentBeanWithPersonBean(studentToCreateInLDAP);
		ArrayList<Person> ldapCreationResultList = dao.batchInsertLDAPRecord(ldapCreateUserList);
		ArrayList<Person> ldapErrorList = new ArrayList<Person>();
		

		for(Person person : ldapCreationResultList){
			SObject accountObject = new SObject();
			accountObject.setType("Account");
			accountObject.setId(person.getAccountId());
			
			if(person.isErrorRecord()){ 
				accountObject.setField("LDAP_Sync_Error__c", person.getErrorMessage());
				ldapErrorList.add(person);
			}else{
				accountObject.setField("Synced_With_LDAP__c", true);
				accountObject.setField("LDAP_Sync_Error__c", "");
			}
			
			accountSObjectListToUpdateBackInSalesforce.add(accountObject);
		}
		
		return ldapErrorList;
	}
	
	public ArrayList<StudentBean> createUserInStudentZone(List<StudentBean> salesforceStudentList,List<SObject> opportunitySObjectListToUpdateBackInSalesforce , ArrayList<StudentBean> studentZoneSuccessfulListForEmail)
	{
		ArrayList<StudentBean> errorList = new ArrayList<>();

		studentZoneDAO = (StudentZoneDao)act.getBean("studentZoneDAO");
		logger.info("inserting in students and registration table");
		ArrayList<StudentBean> errotListStudentMaster = studentZoneDAO.batchUpsertStudentMaster(salesforceStudentList);
		ArrayList<StudentBean> errotListStudentRegistration = studentZoneDAO.batchUpsertRegistration(salesforceStudentList);
		errorList.addAll(errotListStudentMaster);
		errorList.addAll(errotListStudentRegistration);
		
		for(StudentBean student : salesforceStudentList)
		{
			try {
				logger.info("rest template call starting. updating assg table");
				HttpHeaders headers =  this.getHeaders();	
				
				RestTemplate restTemplate = new RestTemplate();
				
				HttpEntity<String> entity = new HttpEntity<String>("",headers);
				student.setYear(Math.round(Float.parseFloat(student.getYear()))+"");
				String url = "https://studentzone-ngasce.nmims.edu/exam/admin/submitAssignmentLiveDataForAStudent?sapid="+student.getSapid()+"&sem="+student.getSem()+"&month="+student.getMonth()+"&year="+student.getYear();
				logger.info("url"+url);
				ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
				JsonObject jsonResponse = new JsonParser().parse(response.getBody()).getAsJsonObject();
			} catch (Exception e) {
				logger.info("error in updating asg quick table."+e.getMessage()); 
			}			
			
			// set Value to update in Salesforce 
			SObject sObject = new SObject();
			sObject.setType("Opportunity");
			sObject.setId(student.getOpportunityId());
			
			if(student.isErrorRecord()){
				sObject.setField("StudentZone_Sync_Error__c", student.getErrorMessage());
			}else{
				sObject.setField("Synced_With_StudentZone__c", true);
				studentZoneSuccessfulListForEmail.add(student);
			}
			opportunitySObjectListToUpdateBackInSalesforce.add(sObject);
		}
		
		return errorList;
	}
	
	public void validateStudentBean(StudentBean student)
	{
		
		String sapId = student.getSapid();
		String sem = student.getSem();
		String programStructure = student.getPrgmStructApplicable();
		String firstName = student.getFirstName();
		String lastName = student.getLastName();
		String gender = student.getGender();
		String isLateral = student.getIsLateral();
		String enrollmentMonth = student.getEnrollmentMonth();
		String validityEndMonth = student.getValidityEndMonth();
		String validityEndYear = student.getValidityEndYear();
		String enrollmentYear = student.getEnrollmentYear();
		
		//ArrayList<String> validProgramStructureList = new ArrayList(Arrays.asList("Jul2009","Jul2013","Jul2014","Jul2017","Jan2018","Jul2018"));
		ArrayList<String> validProgramStructureList = studentZoneDAO.getAllProgramStructureList();
		ArrayList<String> validSemList = new ArrayList(Arrays.asList("1","2","3","4","5","6","7","8"));
		System.out.println(student.toString());
		studentZoneDAO = (StudentZoneDao)act.getBean("studentZoneDAO");
		ArrayList<String> programList = studentZoneDAO.getAllPrograms();
		ArrayList<String> centersList = studentZoneDAO.getAllCenters();
		
		if(sapId.length() != 11){
			student.setErrorMessage(student.getErrorMessage()+" Invalid SAPID '"+sapId+"'");
			student.setErrorRecord(true);
		}
		
		if(!validSemList.contains(sem)){
			student.setErrorMessage(student.getErrorMessage()+" Invalid Semester for record with SAPID:"+sapId+ " & Sem:"+sem);
			student.setErrorRecord(true);
		}

		if(!validProgramStructureList.contains(programStructure)){
			student.setErrorMessage(student.getErrorMessage()+" Invalid Program Structure for record with SAPID: "+sapId);
			student.setErrorRecord(true);
		}

		if(StringUtils.isBlank(firstName) || StringUtils.isBlank(gender)) {				//isBlank check for lastName not present, as dot (period) lastName is stored as empty, Card: 14965
			student.setErrorMessage(student.getErrorMessage() + " First Name / Gender cannot be empty.");
			student.setErrorRecord(true);
		}

		if(!("Male".equals(gender) || "Female".equals(gender))){
			student.setErrorMessage(student.getErrorMessage()+" Invalid value for Gender.");
			student.setErrorRecord(true);
		}

		if(!("Y".equals(isLateral) || "N".equals(isLateral))){
			student.setErrorMessage(student.getErrorMessage() +" Invalid value for Is Lateral Column. Enter Y/N");
			student.setErrorRecord(true);
		}

		if(StringUtils.isBlank(enrollmentMonth) || StringUtils.isBlank(enrollmentYear) || StringUtils.isBlank(validityEndMonth) | StringUtils.isBlank(validityEndYear)){
			student.setErrorMessage(student.getErrorMessage() +" Validity and Enrollment Month/Year cannot be empty.");
			student.setErrorRecord(true);
		}

		if(!programList.contains(student.getProgram())){
			student.setErrorMessage(student.getErrorMessage() +" Invalid Program for record with SAPID:"+student.getSapid()+ " & PROGRAM:"+student.getProgram());
			student.setErrorRecord(true);
		}

		if(!centersList.contains(student.getCenterCode())){
			student.setErrorMessage(student.getErrorMessage() +" Invalid Center Code for record with SAPID:"+student.getSapid()+ " & Center Code:"+student.getCenterCode() + " & Center Name: "+ student.getCenterName());
			student.setErrorRecord(true);
		}

		if(!(yearList.contains(enrollmentYear) && ("Jan".equalsIgnoreCase(enrollmentMonth) || "Jul".equalsIgnoreCase(enrollmentMonth) || "Oct".equalsIgnoreCase(enrollmentMonth)  
					 || "Sep".equalsIgnoreCase(enrollmentMonth)  || "Dec".equalsIgnoreCase(enrollmentMonth) || "Apr".equalsIgnoreCase(enrollmentMonth) 
					 || "Mar".equalsIgnoreCase(enrollmentMonth)  || "Jun".equalsIgnoreCase(enrollmentMonth) ))){
			student.setErrorMessage(student.getErrorMessage()+" Invalid Enrollment Year/Month for record with SAPID:"+student.getSapid()+" Valid Enrollment Month are Jan/Jul/Sep/Oct/May/Apr/Mar");
			student.setErrorRecord(true);
		}
		if(!(yearList.contains(validityEndYear) && ("Jun".equals(validityEndMonth) || "Dec".equals(validityEndMonth) || "Nov".equals(validityEndMonth) 
					|| "Apr".equals(validityEndMonth) || "Oct".equals(validityEndMonth) || "Aug".equals(validityEndMonth) || "Feb".equals(validityEndMonth) 
					|| "Mar".equals(validityEndMonth) || "May".equals(validityEndMonth) || "Sep".equals(validityEndMonth) || "Jul".equals(validityEndMonth)))){
			student.setErrorMessage(student.getErrorMessage() +" Invalid Validity Year/Month for record with SAPID:"+student.getSapid()+
									" Valid Validity Month are Feb/Mar/Apr/May/Jun/Jul/Aug/Sep/Oct/Nov/Dec");
			student.setErrorRecord(true);
		}
		
	}
	
	public ArrayList<SObject> updateStudentMetricsRecordsBackInSalesforce(ArrayList<SObject> recordsList ,String sObjectType,int UPDATE_BATCH_SIZE)
	{
		/*
		 * ConnectorConfig config = new ConnectorConfig();
		 * config.setUsername(SFDC_USERID); config.setPassword(SFDC_PASSWORD_TOKEN);
		 * System.out.println("Updating records back in SFDC");
		 * System.out.println("SFDC_USERID = "+SFDC_USERID);
		 * System.out.println("SFDC_PASSWORD_TOKEN = "+SFDC_PASSWORD_TOKEN);
		 */

		
		ArrayList<String> saleforceUpdationErrorList = new ArrayList<String>();
		ArrayList<SObject> saleforceUpdationFailedList =  new ArrayList<SObject>();
		try {
			/* connection = Connector.newConnection(config); */
			// query for documents records for give SFDC record Id
			/*System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
			System.out.println("Auth EndPoint: "+config.getAuthEndpoint());
			System.out.println("Service EndPoint: "+config.getServiceEndpoint());
			System.out.println("Username: "+config.getUsername());
			System.out.println("SessionId: "+config.getSessionId());
			System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");*/
			Boolean isErrorBatch=false;
			for(int i = 0; i < recordsList.size() ; i= i + UPDATE_BATCH_SIZE){
				int lastIndex =  (i + UPDATE_BATCH_SIZE) < recordsList.size() ? (i + UPDATE_BATCH_SIZE) : recordsList.size();
//				System.out.println("\n***** start = "+ i + " end = "+lastIndex +" *********");
//				System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
				ArrayList<SObject> recorsToUpdateSubList =  new ArrayList<SObject>(recordsList.subList(i, lastIndex));
				
				SObject[] records = new  SObject[recorsToUpdateSubList.size()];
				records = recorsToUpdateSubList.toArray(records);
				
				// update the records in Salesforce.com
				SaveResult[] saveResults = connection.update(records);
				// check the returned results for any errors
				for (int k=0; k< saveResults.length; k++) {
					if (saveResults[k].isSuccess()) {
						//System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
						//System.out.println(k+". Successfully updated record - Id: " + saveResults[k].getId());
						isErrorBatch = false;
					}else {
						Error[] errors = saveResults[k].getErrors();
						String errorMessage = "";
						for (int j=0; j< errors.length; j++) {
							//System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
							
//							System.out.println("ERROR updating record: " + records[k].getId() + " : "+ errors[j].getMessage() );
							errorMessage +=errors[j].getMessage();
						}
						isErrorBatch = true;
						logger.error("ERROR updating Student Metrics in Salesforce of record: {}, Error Message: {}", records[k].getId(), errorMessage);
						saleforceUpdationErrorList.add(sObjectType+":"+records[k].getId()+":"+errorMessage);
					}
				}
				logger.info("update() query of Student Metrics successfully called in Salesforce for {} records, out of {} records.", lastIndex, recordsList.size());
				
				if(isErrorBatch){
					saleforceUpdationFailedList.addAll(recorsToUpdateSubList);
				}
			}
		}
		catch(ApiFault ex) {
			logger.error("Caught ApiFault Exception");
		}
		catch(ConnectionException ex) {
			logger.error("Connection Exception while trying to update Student Metrics records in Salesforce");
		}
		catch(Exception ex) {
			logger.error("Error encountered while updating Student Metrics records in Salesforce: {}", ex.toString()); 
		}
		
		return saleforceUpdationFailedList;
	}
	
	public ArrayList<String> updateRecordsBackInSalesforce(ArrayList<SObject> recordsList ,String sObjectType,int UPDATE_BATCH_SIZE)
	{
		/*
		 * ConnectorConfig config = new ConnectorConfig();
		 * config.setUsername(SFDC_USERID); config.setPassword(SFDC_PASSWORD_TOKEN);
		 * System.out.println("Updating records back in SFDC");
		 * System.out.println("SFDC_USERID = "+SFDC_USERID);
		 * System.out.println("SFDC_PASSWORD_TOKEN = "+SFDC_PASSWORD_TOKEN);
		 */

		
		ArrayList<String> saleforceUpdationErrorList = new ArrayList<String>();
		try {
			/* connection = Connector.newConnection(config); */
			// query for documents records for give SFDC record Id
			/*System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
			System.out.println("Auth EndPoint: "+config.getAuthEndpoint());
			System.out.println("Service EndPoint: "+config.getServiceEndpoint());
			System.out.println("Username: "+config.getUsername());
			System.out.println("SessionId: "+config.getSessionId());
			System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");*/

			for(int i = 0; i < recordsList.size() ; i= i + UPDATE_BATCH_SIZE){
				int lastIndex =  (i + UPDATE_BATCH_SIZE) < recordsList.size() ? (i + UPDATE_BATCH_SIZE) : recordsList.size();
				//System.out.println("\n***** start = "+ i + " end = "+lastIndex +" *********");
				//System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
				ArrayList<SObject> recorsToUpdateSubList =  new ArrayList<SObject>(recordsList.subList(i, lastIndex));
				
				SObject[] records = new  SObject[recorsToUpdateSubList.size()];
				records = recorsToUpdateSubList.toArray(records);
				
				// update the records in Salesforce.com
				SaveResult[] saveResults = connection.update(records);
				// check the returned results for any errors
				for (int k=0; k< saveResults.length; k++) {
					if (saveResults[k].isSuccess()) {
						//System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
						//System.out.println(k+". Successfully updated record - Id: " + saveResults[k].getId());
					}else {
						Error[] errors = saveResults[k].getErrors();
						String errorMessage = "";
						for (int j=0; j< errors.length; j++) {
							//System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
							
							//System.out.println("ERROR updating record: " + records[k].getId() + " : "+ errors[j].getMessage() );
							errorMessage +=errors[j].getMessage();
						}
						logger.error("ERROR updating record in Salesforce: " + records[k].getId() + ", Error Message: " + errorMessage);
						saleforceUpdationErrorList.add(sObjectType+":"+records[k].getId()+":"+errorMessage);
					}   
				}
				logger.info("update() query successfully called in Salesforce for " + lastIndex + " records, out of " + recordsList.size() + " records."); 
			}
		} 
		catch (ApiFault ex) { 
			logger.error("Caught ApiFault Exception: " + ex.toString());
		}
		catch(ConnectionException ex) {
			logger.error("Connection Failed while trying to update record in Salesforce: " + ex.toString());
		}
		catch(Exception ex) {
			logger.error("Error encountered while updating record in Salesforce: " + ex.toString()); 
			//System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
			//System.out.println("ERROR updating record In Salesforce : "+e.getMessage());
		}
		
		return saleforceUpdationErrorList;
	}
	
	public ArrayList<Person> mapStudentBeanWithPersonBean(List<StudentBean> studentToCreateInLDAP){
		ArrayList<Person> personList = new ArrayList<Person>();
		
		if(studentToCreateInLDAP !=null && !studentToCreateInLDAP.isEmpty()){
			for(StudentBean student : studentToCreateInLDAP)
			{
				Person person = new Person();
				
				String displayName = StudentProfileUtils.getValidLdapDisplayName(student.getFirstName(), student.getLastName());			//LDAP acceptable display name
				String lastName = StudentProfileUtils.getValidLdapLastName(student.getLastName());											//LDAP acceptable last name
				
				person.setFirstName(student.getFirstName());
				person.setLastName(lastName);
				person.setDisplayName(displayName);
				person.setEmail(student.getEmailId());
				person.setContactNo(student.getMobile());
				person.setAltContactNo(student.getAltPhone());
				person.setPassword(student.getPassword());
				person.setUserId(student.getSapid());
				person.setPostalAddress(student.getAddress());
				person.setProgram(student.getProgram());
				person.setAccountId(student.getAccountId());
				personList.add(person);
			}
		}
		return personList;
	}

	@GetMapping(value = "/syncStudentLearningMetricsWithSalesforce")
	public void callSyncStudentLearningMetricsWithSalesforce() {
		syncStudentLearningMetricsWithSalesforce();
	}
	
	@Scheduled(cron = "0 10 3 1 * ?")		//runs first day of every month at 3:10 AM
	public void syncSalesforceStudentLearningMetricsScheduler() {
		if(SERVER_TOMCAT_20.equalsIgnoreCase(SERVER)) {
			logger.info("Calling syncStudentLearningMetricsWithSalesforce Scheduler!");
			syncStudentLearningMetricsWithSalesforce();
		}
	}
	
	public void syncStudentLearningMetricsWithSalesforce() {
		if(!ENVIRONMENT_PRODUCTION.equalsIgnoreCase(ENVIRONMENT)) {
			logger.info("Not Running syncStudentLearningMetricsWithSalesforce Scheduler, since current environment: {} is not {}", ENVIRONMENT, ENVIRONMENT_PRODUCTION);
			return;
		}
		
		SchedulerApisBean sbean = new SchedulerApisBean();
//		logger.info("Querying Student Learning Metrics");
		ArrayList<SObject> accountSObjectListToUpdateBackInSalesforce = new ArrayList<>();
		
		// fill Student Learning Metrics
		logger.info("updating learning metrics in studentZone");
		String url = SERVER_PATH + "exam/api/studentLearningMetrics";
//		HttpClient client = new DefaultHttpClient();		//Commented as DefaultHttpClient is deprecated

		// add header
		HttpPost post = new HttpPost(url);
//		post.setHeader("User-Agent", "");
		post.setHeader("Content-type", "application/json");
//	    post.setEntity(null);
        
		try(CloseableHttpClient httpClient = HttpClients.createDefault()) {			//using try-with-resources to auto close the HttpClient Object
			logger.info("Sending 'POST' request to URL: {}", url);
			HttpResponse response = httpClient.execute(post);
//			HttpResponse response = client.execute(post);
			logger.info("studentLearningMetrics Response Code: {}", response.getStatusLine().getStatusCode());
		}
		catch(ClientProtocolException e1) { 
//			e1.printStackTrace();
			logger.error("ClientProtocolException while calling studentLearningMetrics API");
		}
		catch(IOException e1) { 
//			e1.printStackTrace();
			logger.error("IOException while calling studentLearningMetrics API");
		}
		
		int UPDATE_BATCH_SIZE = 10;
		int UPDATE_BATCH_SIZE_RETRY = 5;
		
		// query Student Learning Metrics 
		studentZoneDAO = (StudentZoneDao)act.getBean("studentZoneDAO");
		Map<String,StudentLearningMetricsBean> mapOfSapIdAndStudentLearningMetrics = studentZoneDAO.getStudentLearningMetricsMap();
		logger.info("fetched "+ mapOfSapIdAndStudentLearningMetrics.size()+" learning metrics entries from studentZone");
		/*
		 * ConnectorConfig config = new ConnectorConfig();
		 * config.setUsername(SFDC_USERID); config.setPassword(SFDC_PASSWORD_TOKEN);
		 * 
		 * System.out.println("SFDC_USERID = "+SFDC_USERID);
		 * System.out.println("SFDC_PASSWORD_TOKEN = "+SFDC_PASSWORD_TOKEN);
		 */

		QueryResult qResult = new QueryResult();

		try {
			/*
			 * connection = Connector.newConnection(config); // query for documents records
			 * for give SFDC record Id System.out.println(
			 * "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
			 * ); System.out.println("Auth EndPoint: "+config.getAuthEndpoint());
			 * System.out.println("Service EndPoint: "+config.getServiceEndpoint());
			 * System.out.println("Username: "+config.getUsername());
			 * System.out.println("SessionId: "+config.getSessionId()); System.out.println(
			 * "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
			 * );
			 */
			String soqlQuery = "SELECT id,nm_StudentNo__c "
							+ " FROM Account"
							+ " where nm_StudentStatus__c = 'Confirmed' and nm_StudentNo__c !=null";
							//+ " where nm_StudentStatus__c = 'Confirmed' and nm_StudentNo__c = '77117195499'";

			qResult = connection.query(soqlQuery);
			boolean done = false;
			logger.info("Fetched "+qResult.getSize()+" confirmed students from salesforce");
			if (qResult.getSize() > 0) {

				//logger.info("Logged-in user can see a total of " + qResult.getSize() + " Account records.");
				logger.info("assigning latest metrics to salesforce student accounts");
				while (!done) {
					SObject[] records = qResult.getRecords();
					for (int i = 0; i < records.length; ++i) {
						SObject s = (SObject) records[i];
						String sapId = (String)s.getField("nm_StudentNo__c");
						
						if(mapOfSapIdAndStudentLearningMetrics.containsKey(sapId))
						{
							StudentLearningMetricsBean bean = mapOfSapIdAndStudentLearningMetrics.get(sapId);
							//System.out.println("StudentLearningMetricsBean :: "+bean.toString());
							SObject accountObject = new SObject();
							accountObject.setType("Account");
							accountObject.setField("Id",(String)s.getField("Id"));
							accountObject.setField("sem1NoOfANS__c",bean.getSem1NoOfANS() );
							accountObject.setField("sem2NoOfANS__c",bean.getSem2NoOfANS() );
							accountObject.setField("sem3NoOfANS__c",bean.getSem3NoOfANS() );
							accountObject.setField("sem4NoOfANS__c",bean.getSem4NoOfANS() );
							accountObject.setField("totalNoOfANS__c",bean.getTotalNoOfANS() );
							accountObject.setField("sem1NoOfAssignSubmitted__c", bean.getSem1NoOfAssignSubmitted());
							accountObject.setField("sem2NoOfAssignSubmitted__c", bean.getSem2NoOfAssignSubmitted());
							accountObject.setField("sem3NoOfAssignSubmitted__c", bean.getSem3NoOfAssignSubmitted());
							accountObject.setField("sem4NoOfAssignSubmitted__c", bean.getSem4NoOfAssignSubmitted());
							accountObject.setField("totalNoOfAssignSubmitted__c", bean.getTotalNoOfAssignSubmitted());
							
							accountObject.setField("sem_1_No_Of_Passed_Subjects__c", bean.getSem1NoOfPassedSubjects());
							accountObject.setField("sem_2_No_Of_Passed_Subjects__c", bean.getSem2NoOfPassedSubjects());
							accountObject.setField("sem_3_No_Of_Passed_Subjects__c", bean.getSem3NoOfPassedSubjects());
							accountObject.setField("sem_4_No_Of_Passed_Subjects__c", bean.getSem4NoOfPassedSubjects());
							accountObject.setField("Total_No_Of_Passed_Subjects__c", bean.getTotalNoOfPassedSubjects());
							accountObject.setField("sem_1_No_Of_Failed_Subjects__c", bean.getSem1NoOfFailedSubjects());
							accountObject.setField("sem_2_No_Of_Failed_Subjects__c", bean.getSem2NoOfFailedSubjects());
							accountObject.setField("sem_3_No_Of_Failed_Subjects__c", bean.getSem3NoOfFailedSubjects());
							accountObject.setField("sem_4_No_Of_Failed_Subjects__c", bean.getSem4NoOfFailedSubjects());
							accountObject.setField("Total_No_Of_Failed_Subjects__c", bean.getTotalNoOfFailedSubjects());
							
							accountObject.setField("sem_1_No_Of_Booked_Subjects__c", bean.getSem1NoOfBookedSubjects());
							accountObject.setField("sem_2_No_Of_Booked_Subjects__c", bean.getSem2NoOfBookedSubjects());
							accountObject.setField("sem_3_No_Of_Booked_Subjects__c", bean.getSem3NoOfBookedSubjects());
							accountObject.setField("sem_4_No_Of_Booked_Subjects__c", bean.getSem4NoOfBookedSubjects());
							accountObject.setField("Total_No_Of_Booked_Subjects__c", bean.getTotalNoOfBookedSubjects());
							accountObject.setField("sem_1_No_Of_Booking_Pending_Subjects__c", bean.getSem1NoOfBookingPendingSubjects());
							accountObject.setField("sem_2_No_Of_Booking_Pending_Subjects__c", bean.getSem2NoOfBookingPendingSubjects());
							accountObject.setField("sem_3_No_Of_Booking_Pending_Subjects__c", bean.getSem3NoOfBookingPendingSubjects());
							accountObject.setField("sem_4_No_Of_Booking_Pending_Subjects__c", bean.getSem4NoOfBookingPendingSubjects());
							accountObject.setField("Total_No_Of_Booking_Pending_Subjects__c", bean.getTotalNoOfBookingPendingSubjects());
							
							accountObject.setField("Re_Reg_Probability__c", bean.getReRegProbability());
							
							//System.out.println("Account " + (i + 1) + ":" + bean);
	
							accountSObjectListToUpdateBackInSalesforce.add(accountObject);
						}
					}
					
					if (qResult.isDone()) {
						done = true;
					} else {
//						System.out.println("Querying more.....");
						qResult = connection.queryMore(qResult.getQueryLocator());
					}
				}
			}
		} catch (ApiFault e) { 
			logger.info("Caught ApiFault");
//			e.printStackTrace();
		}catch(Exception e){
			logger.info(e.getMessage()); 
//			e.printStackTrace();
			sbean.setError(e + "");
		}
		logger.info("updating records back in salesforce");
		// update Records Back In Salesforce 
		if(accountSObjectListToUpdateBackInSalesforce.size() > 0)
		{
			ArrayList<SObject> errorList = updateStudentMetricsRecordsBackInSalesforce(accountSObjectListToUpdateBackInSalesforce, "Account",UPDATE_BATCH_SIZE);
			//System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
			logger.info("Failed to update StudentMetrics in Salesforce for "+errorList.size()+" students");
			//System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
			logger.info("Retrying  to update StudentMetrics in Salesforce for "+errorList.size()+" students");
			//System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
			ArrayList<SObject> errorListReTry = updateStudentMetricsRecordsBackInSalesforce(errorList, "Account",UPDATE_BATCH_SIZE_RETRY);
			logger.info("Retrying  to update StudentMetrics in Salesforce failed for "+errorListReTry.size()+" students");
			sbean.setError(sbean.getError() + ". Retrying  to update StudentMetrics in Salesforce failed for " + errorListReTry.size() + " students");
		}
		logger.info("updating last sync time");
		sbean.setSyncType("Student Learning Metrics With Salesforce"); 
		sbean.setError("");
		studentZoneDAO.updateLastSyncedTime(sbean); 
	}
	
	@RequestMapping(value = "/syncStagedRegistrationDataToRegistrationTable", method = { RequestMethod.GET,
	 RequestMethod.POST })
	public void callsyncStagedRegistrationDataToRegistrationTable(HttpServletRequest request,
	HttpServletResponse response) throws IOException {
		syncStagedRegistrationDataToRegistrationTable();
	}
	@Scheduled(fixedDelay=24*60*60*1000)
	public void syncStagedRegistrationDataToRegistrationTable(){
		
		if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
			System.out.println("Not Running syncSalesforceRegistrationData since it is not PROD ");
			return;
		}
		SchedulerApisBean sbean = new SchedulerApisBean();
		System.out.println("*********\n Started syncStagedRegistrationDataToRegistrationTable \n ***********"); 
		
		try {
			//get records to be moved to registration table
			studentZoneDAO = (StudentZoneDao)act.getBean("studentZoneDAO");
			List<StudentBean> studentList = new ArrayList<>();
			
			studentList = studentZoneDAO.getRegistrationDataFromStagingTable();
			
			ArrayList<StudentBean> errorList = studentZoneDAO.batchUpsertRegistration(studentList);
			
			if(errorList.size() > 0) {
				throw new Exception("Error while moving registration data from staging to registration table.");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			sbean.setError(e + "");
			mailer.mailStackTrace("Error: Moving Staged registration data to examRegistration table", e);
			
		}
		sbean.setSyncType("Staged Registration Data to Registration Table");
		studentZoneDAO.updateLastSyncedTime(sbean);
	}
	@RequestMapping(value = "/checkForConnection", method = RequestMethod.GET,produces = "application/json")
	public ResponseEntity<HashMap<String,String>> checkForConnection(){
	HttpHeaders headers = new HttpHeaders();
	headers.add("Content-Type", "application/json");
	HashMap<String,String> response = new HashMap<>();
	QueryResult qResult = new QueryResult();
	String soqlQuery =" SELECT Id FROM Account where nm_StudentStatus__c = 'Confirmed' and Synced_With_LDAP__c = false " ;

	try {
		qResult = connection.query(soqlQuery);
	} catch (ConnectionException e) { 
		e.printStackTrace();
	} 
	
	if (qResult.getSize() > 0) {
		response.put("Status", "Success");
		response.put("message", "Connection successfull !");
		return new ResponseEntity<>(response,headers, HttpStatus.OK);
	}
	return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
} 
    public  List split(List<StudentDataMismatchBean> mismatchList) 
    { 
    	 List result=new ArrayList<>();
        // find size of the list and put in size 
        int size =10000;
  
        // create new list and insert valuese which is returne by 
        // list.subList() method 
        final int listSize = mismatchList.size();
        for (int i = 0; i < listSize; i += size) {
            if (i + size < listSize) {
            	result.add(new  ArrayList<StudentDataMismatchBean>(mismatchList.subList(i, i+size))); 
            } 
        } 
        // return an List of array 
        return  result; 
    } 
	private HttpHeaders getHeaders() {
		HttpHeaders headers =  new HttpHeaders();
		headers.add("Accept", "application/json");
		headers.add("Content-Type", "application/json");
		return headers;
	}
}
