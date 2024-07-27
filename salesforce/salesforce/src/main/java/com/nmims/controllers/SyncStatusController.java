package com.nmims.controllers;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar; 
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map; 

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.ServletConfigAware;

import com.nmims.beans.RevenueReportField;
import com.nmims.beans.SchedulerApisBean;
import com.nmims.beans.StatusBean;
import com.nmims.beans.StudentBean;
import com.nmims.beans.StudentDataMismatchBean;
import com.nmims.beans.StudentLearningMetricsBean; 
import com.nmims.daos.StudentZoneDao;
import com.nmims.helpers.FedExTrackClient;
import com.nmims.helpers.IdCardHelper;
import com.nmims.helpers.MailSender;
import com.nmims.helpers.Person;
import com.nmims.helpers.RevenueReportHelper;
import com.nmims.helpers.SFConnection;
import com.nmims.listeners.FedExScheduler;
import com.nmims.listeners.SalesforceSyncScheduler;
import com.nmims.webservice.fedex.track.CarrierCodeType;
import com.nmims.webservice.fedex.track.ClientDetail; 
import com.nmims.webservice.fedex.track.NotificationSeverityType;
import com.nmims.webservice.fedex.track.ObjectFactory;
import com.nmims.webservice.fedex.track.TrackIdentifierType;
import com.nmims.webservice.fedex.track.TrackPackageIdentifier;
import com.nmims.webservice.fedex.track.TrackReply;
import com.nmims.webservice.fedex.track.TrackRequest;
import com.nmims.webservice.fedex.track.TrackRequestProcessingOptionType;
import com.nmims.webservice.fedex.track.TrackSelectionDetail; 
import com.nmims.webservice.fedex.track.TrackStatusDetail;
import com.nmims.webservice.fedex.track.TransactionDetail;
import com.nmims.webservice.fedex.track.VersionId;
import com.nmims.webservice.fedex.track.WebAuthenticationCredential;
import com.nmims.webservice.fedex.track.WebAuthenticationDetail; 
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.fault.ApiFault;
import com.sforce.soap.partner.fault.LoginFault;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException; 
import com.sforce.ws.bind.XmlObject;
@Controller
@Component
public class SyncStatusController implements ApplicationContextAware, ServletConfigAware{
	
	private static final Logger logger = LoggerFactory.getLogger(SyncStatusController.class);

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
	@Autowired
	SFConnection sfc; 
	@Autowired
	FedExScheduler scheduler;
	
	@Autowired
	IdCardHelper idHelper;
	
	@Value( "${FEDEX_AUTH_KEY}" )
	private String FEDEX_AUTH_KEY;
	
	@Value( "${FEDEX_AUTH_PASSWORD}" )
	private String FEDEX_AUTH_PASSWORD;
	
	@Value( "${FEDEX_ACCOUNT_NUMBER}" )
	private String FEDEX_ACCOUNT_NUMBER;
	
	@Value( "${FEDEX_METER_NUMBER}" )
	private String FEDEX_METER_NUMBER;
	
	@Autowired
	FedExTrackClient fedExTrackClient;
	@Autowired
	SalesforceSyncScheduler syncsSheduler;
	@Autowired
	private RevenueReportHelper revenueReportHelper;
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
	StudentZoneDao studentZoneDAO;
	
	@Autowired
	MailSender mailer;
  
	
	private ArrayList<String> yearList = new ArrayList<String>(Arrays.asList( 
			"2000","2001","2002","2003","2004","2005","2006","2007",
			"2008","2009","2010","2011","2012","2013","2014","2015","2016","2017", "2018", "2019", "2020", "2021", "2022", "2023", "2024","2025","2026","2027"));
	
	public SyncStatusController(SFConnection sf) {  
		this.connection = sf.getConnection();
	}
	public void init(){
		SFConnection sf= new SFConnection(SFDC_USERID,SFDC_PASSWORD_TOKEN); 
		this.connection = sf.getConnection();
	} 
	@RequestMapping(value = "/statusOfSalesforceRegistrationMismatchData", method = {RequestMethod.GET},produces = "application/json")
	public ResponseEntity<StatusBean> statusOfSalesforceRegistrationMismatchData( HttpServletRequest request,HttpServletResponse respons) throws IOException {
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		StatusBean response = new StatusBean(); 
		System.out.println("syncSalesforceRegistrationMismatchData: Querying Student Master details from SFDC ");
		logger.info("syncSalesforceRegistrationMismatchData: Querying Student Master details from SFDC ");
		  
		QueryResult qResult = new QueryResult();

		try {
			
			HashMap<String, StudentBean> studentsMap = studentZoneDAO.getAllRegistrationDataTable();
			logger.info("getAllRegistrationDataTable List: "+studentsMap.size());
			List<StatusBean> studentNotInRegTable = new ArrayList<StatusBean>();
			 
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
			
			qResult = connection.query(soqlQuery);
			boolean done = false;
			List<StudentBean> accountMismatchStudents= new ArrayList<StudentBean>();
			if (qResult.getSize() > 0) {

				logger.info("Logged-in user can see a total of "
						+ qResult.getSize() + " Opportunity records.");
				while (!done) {
					SObject[] records = qResult.getRecords();
					for (int i = 0; i < records.length; ++i) {
						
						SObject s = (SObject) records[i];
						XmlObject account_r = (XmlObject)s.getField("Account");
						XmlObject program_r = (XmlObject)s.getField("nm_Program__r");
						 
						String sapId = (String)account_r.getField("nm_StudentNo__c");
						String program = (String)program_r.getField("StudentZoneProgramCode__c");
						String sem = (String)s.getField("nm_Semester__c");
						
						try {
						
						String year = (String)s.getField("nm_Year__c");
						year = year.replaceAll(",", "");
						year = year.substring(0, 4);
						String month = ((String)s.getField("nm_Session__c")).substring(0, 3);
						String key= sapId+"|"+program+"|"+sem+"|"+year+"|"+month;
							 
							if(!studentsMap.containsKey(key)){ 
									System.out.println("Student not in Reg Table "+key); 
									StatusBean student = new StatusBean();
									student.setSapid((String)account_r.getField("nm_StudentNo__c"));
									student.setYear(year);
									student.setMonth(((String)s.getField("nm_Session__c")).substring(0, 3));
									student.setSem((String)s.getField("nm_Semester__c")); 
									student.setStatus("Registration"); 
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
				response.setMessage(studentsMap.size()+"/"+qResult.getSize()+" Synced <br> Mismatch :"+studentNotInRegTable.size());
				
				List<String> details= new ArrayList<String>();
				details.add("Registration;Data missing in Studentzone:"+studentNotInRegTable.size());
				details.add("Registration;Mismatch (Salesforce-Studentzone):"+Math.abs(qResult.getSize()-studentsMap.size()));
				response.setDetailedMessage(details);
				response.setStudents(studentNotInRegTable);   
				logger.info("studentNotInRegTable list size "+studentNotInRegTable.size());
				logger.info("sending  Email: Salesforce-StudentZone Registration MisMatch Error");
				
			}
			
		} catch (ApiFault e) {  
		}catch(Exception e){ 
		}
		
		return new ResponseEntity<StatusBean>(response,headers, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/statusOfSalesforceStudentZoneMismatch", method = {RequestMethod.GET},produces = "application/json")
	public ResponseEntity<StatusBean> statusOfSalesforceStudentZoneMismatch( HttpServletRequest request) throws IOException {
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		StatusBean response = new StatusBean();
		
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
					+ " nm_Program__r.StudentZoneProgramCode__c, " //Program
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
							student.setProgramname(program); //program as in portal 
							//student.setProgramname((String)s.getField("nmStudent_Program__c")); 
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
				//logger.info("sapIdStudentsMap:"+sapIdStudentsMap.size()); 
				//Compare each Salesforce record with Student Zone record
				ArrayList<StudentDataMismatchBean> mismatchList = new ArrayList<>();
				logger.info("Comparing each Salesforce record with Student Zone record");
				int emailMismatch=0;
				int mobileMismatch=0;
				int programMismatch=0;
				int validityEndMonth=0;
				int validityEndYear =0;
				int altPhn=0;
				int dob=0;
				int regdate=0;
				int isLateral=0;
				int city=0;
				int state=0;
				int country=0;  
				int pin=0;
				int centername=0;
				int previousStudentid=0;
				int gender=0;
				int imgUrl=0;
				List<StatusBean> accountMismatchStudents= new ArrayList<StatusBean>();
				
				for (StudentBean studentBean : salesforceStudentList) { 
					StudentDataMismatchBean mmBean = new StudentDataMismatchBean();
					boolean isMismatch = false; 	
					try { 
						StudentBean studentFromStudentZone = sapIdStudentsMap.get(studentBean.getSapid().trim());
						if(studentFromStudentZone != null){
							 if(studentBean.getEmailId() == null || studentFromStudentZone.getEmailId() == null || (!studentBean.getEmailId().equalsIgnoreCase(studentFromStudentZone.getEmailId().trim()))){
								
								 if(!(StringUtils.isBlank(studentBean.getEmailId())  &&  StringUtils.isBlank(studentFromStudentZone.getEmailId()))){
									 mismatchList.add(mmBean); 
									 emailMismatch++; 
									 StatusBean mismatchStudent = new StatusBean(); 
									 mismatchStudent.setSapid(studentBean.getSapid()); 
									 mismatchStudent.setStatus("Email"); 
									 accountMismatchStudents.add(mismatchStudent);
								 }
							}
							
							if(studentBean.getMobile() == null || studentFromStudentZone.getMobile() == null || (!studentBean.getMobile().equalsIgnoreCase(studentFromStudentZone.getMobile()))){
								
								if(!(StringUtils.isBlank(studentBean.getMobile()) && StringUtils.isBlank(studentFromStudentZone.getMobile()))){
									 mismatchList.add(mmBean); 
									 mobileMismatch++; 
									 StatusBean mismatchStudent = new StatusBean(); 
									 mismatchStudent.setSapid(studentBean.getSapid()); 
									 mismatchStudent.setStatus("Mobile"); 
									 accountMismatchStudents.add(mismatchStudent);
								}
							}
							//logger.info(studentBean.getSapid()+"---"+studentBean.getProgramname());
							
							if(studentBean.getProgramname() == null || studentFromStudentZone.getProgramname() == null || (!studentBean.getProgramname().equalsIgnoreCase(studentFromStudentZone.getProgramname().trim()))){
								
								if(!(StringUtils.isBlank(studentBean.getProgramname()) && StringUtils.isBlank(studentFromStudentZone.getProgramname()))){
									mismatchList.add(mmBean);
									programMismatch++; 
									StatusBean mismatchStudent = new StatusBean(); 
									mismatchStudent.setSapid(studentBean.getSapid()); 
									mismatchStudent.setStatus("Program"); 
									accountMismatchStudents.add(mismatchStudent);
								}
							}
							
							if(studentBean.getValidityEndMonth() == null || studentFromStudentZone.getValidityEndMonth() == null || (!studentBean.getValidityEndMonth().substring(0, 3).equalsIgnoreCase(studentFromStudentZone.getValidityEndMonth()))){
								
								if(!(StringUtils.isBlank(studentBean.getValidityEndMonth()) && StringUtils.isBlank(studentFromStudentZone.getValidityEndMonth()))){
									mismatchList.add(mmBean);
									validityEndMonth++; 
									StatusBean mismatchStudent = new StatusBean(); 
									mismatchStudent.setSapid(studentBean.getSapid()); 
									mismatchStudent.setStatus("Validity End Month"); 
									accountMismatchStudents.add(mismatchStudent);
								}
							}
							
							if(studentBean.getValidityEndYear() == null || studentFromStudentZone.getValidityEndYear() == null || (!studentBean.getValidityEndYear().equalsIgnoreCase(studentFromStudentZone.getValidityEndYear()))){
								
								if(!(StringUtils.isBlank(studentBean.getValidityEndYear()) && StringUtils.isBlank(studentFromStudentZone.getValidityEndYear()))){
									mismatchList.add(mmBean); 
									validityEndYear++; 
									StatusBean mismatchStudent = new StatusBean(); 
									mismatchStudent.setSapid(studentBean.getSapid()); 
									mismatchStudent.setStatus("Validity End Year"); 
									accountMismatchStudents.add(mismatchStudent);
								}
							}
							
							if(studentBean.getAltPhone() == null || studentFromStudentZone.getAltPhone() == null || (!studentBean.getAltPhone().equalsIgnoreCase(studentFromStudentZone.getAltPhone()))){
								
								if(!(StringUtils.isBlank(studentBean.getAltPhone())  && StringUtils.isBlank(studentFromStudentZone.getAltPhone())) ){
									mismatchList.add(mmBean); 
									altPhn++; 
									StatusBean mismatchStudent = new StatusBean(); 
									mismatchStudent.setSapid(studentBean.getSapid()); 
									mismatchStudent.setStatus("Alternate Phone"); 
									accountMismatchStudents.add(mismatchStudent);
								}
							}
							
							if(studentBean.getDob() == null || studentFromStudentZone.getDob() == null || (!studentBean.getDob().equalsIgnoreCase(studentFromStudentZone.getDob()))){
								Date sfdcDate = new Date();
								Date portalDate = new Date();
								int date = 0;
								if(!(StringUtils.isBlank(studentBean.getDob()) && StringUtils.isBlank(studentFromStudentZone.getDob()))){
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
										mismatchList.add(mmBean); 
									}else if(studentBean.getDob().equalsIgnoreCase("No valid date found") || studentFromStudentZone.getDob().equalsIgnoreCase("No valid date found") ){
										mismatchList.add(mmBean); 
										dob++; 
										StatusBean mismatchStudent = new StatusBean(); 
										mismatchStudent.setSapid(studentBean.getSapid()); 
										mismatchStudent.setStatus("Dob"); 
										accountMismatchStudents.add(mismatchStudent);
									}
									
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
																		mismatchList.add(mmBean);  
																	}else if(studentBean.getRegDate().equalsIgnoreCase("No valid date found") || studentFromStudentZone.getRegDate().equalsIgnoreCase("No valid date found") ){
																		 
																		mismatchList.add(mmBean);  
																		regdate++;
																		StatusBean mismatchStudent = new StatusBean(); 
																		mismatchStudent.setSapid(studentBean.getSapid()); 
																		mismatchStudent.setStatus("Registration Date"); 
																		accountMismatchStudents.add(mismatchStudent);
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
								
								if(!(StringUtils.isBlank(convertedValueOfIsLateral)  && StringUtils.isBlank(studentFromStudentZone.getIsLateral()))){
									mismatchList.add(mmBean); 
									isLateral++; 
									StatusBean mismatchStudent = new StatusBean(); 
									mismatchStudent.setSapid(studentBean.getSapid()); 
									mismatchStudent.setStatus("is Lateral"); 
									accountMismatchStudents.add(mismatchStudent);
								} 
							}
							
							if(studentBean.getCity() == null || studentFromStudentZone.getCity() == null || (!studentBean.getCity().equalsIgnoreCase(studentFromStudentZone.getCity().trim()))){
								
								if(!(StringUtils.isBlank(studentBean.getCity())  && StringUtils.isBlank(studentFromStudentZone.getCity()))){
									mismatchList.add(mmBean);
									city++;
									StatusBean mismatchStudent = new StatusBean(); 
									mismatchStudent.setSapid(studentBean.getSapid()); 
									mismatchStudent.setStatus("City"); 
									accountMismatchStudents.add(mismatchStudent);
								}
							}
							
							if(studentBean.getCountry() == null || studentFromStudentZone.getCountry() == null || (!studentBean.getCountry().equalsIgnoreCase(studentFromStudentZone.getCountry().trim()))){
								
								if(!(StringUtils.isBlank(studentBean.getCountry()) && StringUtils.isBlank(studentFromStudentZone.getCountry())) ){
									mismatchList.add(mmBean); 
									country++; 
									StatusBean mismatchStudent = new StatusBean(); 
									mismatchStudent.setSapid(studentBean.getSapid()); 
									mismatchStudent.setStatus("Country"); 
									accountMismatchStudents.add(mismatchStudent);
								}
							}
							
							if(studentBean.getState() == null || studentFromStudentZone.getState() == null || (!studentBean.getState().equalsIgnoreCase(studentFromStudentZone.getState().trim()))){
								
								if(!(StringUtils.isBlank(studentBean.getState()) && StringUtils.isBlank(studentFromStudentZone.getState())) ){
									mismatchList.add(mmBean); 
									state++; 
									StatusBean mismatchStudent = new StatusBean(); 
									mismatchStudent.setSapid(studentBean.getSapid()); 
									mismatchStudent.setStatus("State"); 
									accountMismatchStudents.add(mismatchStudent);
								}
							}
							
							if(studentBean.getPin() == null || studentFromStudentZone.getPin() == null || (!studentBean.getPin().equalsIgnoreCase(studentFromStudentZone.getPin().trim()))){
								
								if(!(StringUtils.isBlank(studentBean.getPin()) && StringUtils.isBlank(studentFromStudentZone.getPin())) ){
									mismatchList.add(mmBean);
									pin++; 
									StatusBean mismatchStudent = new StatusBean(); 
									mismatchStudent.setSapid(studentBean.getSapid()); 
									mismatchStudent.setStatus("Pin"); 
									accountMismatchStudents.add(mismatchStudent);
								}
							}
							
							if(studentBean.getGender() == null || studentFromStudentZone.getGender() == null || (!studentBean.getGender().equalsIgnoreCase(studentFromStudentZone.getGender().trim()))){
								
								if(!(StringUtils.isBlank(studentBean.getGender())  && StringUtils.isBlank(studentFromStudentZone.getGender()))){
									mismatchList.add(mmBean); 
									gender++; 
									StatusBean mismatchStudent = new StatusBean(); 
									mismatchStudent.setSapid(studentBean.getSapid()); 
									mismatchStudent.setStatus("Gender"); 
									accountMismatchStudents.add(mismatchStudent);
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
								
								if(!(StringUtils.isBlank(trimedSFDCCenterName)  && StringUtils.isBlank(studentFromStudentZone.getCenterCode())) ){
									mismatchList.add(mmBean); 
									StatusBean mismatchStudent = new StatusBean(); 
									mismatchStudent.setSapid(studentBean.getSapid()); 
									mismatchStudent.setStatus("Center Code"); 
									accountMismatchStudents.add(mismatchStudent);
								}
							}
							if(studentBean.getImageUrl() == null || studentFromStudentZone.getImageUrl() == null || (!studentBean.getImageUrl().equalsIgnoreCase(studentFromStudentZone.getImageUrl().trim()))){
								
								if(!(StringUtils.isBlank(studentBean.getImageUrl())  && StringUtils.isBlank(studentFromStudentZone.getImageUrl()))  ){
									mismatchList.add(mmBean);
									imgUrl++; 
									StatusBean mismatchStudent = new StatusBean(); 
									mismatchStudent.setSapid(studentBean.getSapid()); 
									mismatchStudent.setStatus("Image Url"); 
									accountMismatchStudents.add(mismatchStudent);
								}
							}
							
							if((studentBean.getPreviousStudentId() == null || studentFromStudentZone.getPreviousStudentId() == null || !studentBean.getPreviousStudentId().equalsIgnoreCase(studentFromStudentZone.getPreviousStudentId().trim()))&& StringUtils.isBlank(studentBean.getExistingStudentNoForDiscount())){
								
								if(!(StringUtils.isBlank(studentBean.getPreviousStudentId())  && StringUtils.isBlank(studentFromStudentZone.getPreviousStudentId())) ){
									mismatchList.add(mmBean);
									previousStudentid++; 
									StatusBean mismatchStudent = new StatusBean(); 
									mismatchStudent.setSapid(studentBean.getSapid()); 
									mismatchStudent.setStatus("Previous Student Id"); 
									accountMismatchStudents.add(mismatchStudent);
								}
							}
							
							if((studentBean.getExistingStudentNoForDiscount() == null || studentFromStudentZone.getExistingStudentNoForDiscount() == null || !studentBean.getExistingStudentNoForDiscount().equalsIgnoreCase(studentFromStudentZone.getExistingStudentNoForDiscount().trim())) && StringUtils.isBlank(studentBean.getPreviousStudentId())){
								
								if(!(StringUtils.isBlank(studentBean.getExistingStudentNoForDiscount()) && StringUtils.isBlank(studentFromStudentZone.getExistingStudentNoForDiscount())) ){
									mismatchList.add(mmBean); 
								}
							}
							
							//Added shipping address fields of Street/Locality/LandMark/HouseNo.Name for mismatch check start
							if(studentBean.getStreet() == null || studentFromStudentZone.getStreet() == null || (!studentBean.getStreet().equalsIgnoreCase(studentFromStudentZone.getStreet().trim()))){
								
								if(!(StringUtils.isBlank(studentBean.getStreet())  && StringUtils.isBlank(studentFromStudentZone.getStreet())) ){
									mismatchList.add(mmBean);
								}
							}
							
							if(studentBean.getLocality() == null || studentFromStudentZone.getLocality() == null || (!studentBean.getLocality().equalsIgnoreCase(studentFromStudentZone.getLocality().trim()))){
								
								if(!(StringUtils.isBlank(studentBean.getLocality())  && StringUtils.isBlank(studentFromStudentZone.getLocality())) ){
									mismatchList.add(mmBean);
								}
							}
							
							if(studentBean.getLandMark() == null || studentFromStudentZone.getLandMark() == null || (!studentBean.getLandMark().equalsIgnoreCase(studentFromStudentZone.getLandMark().trim()))){
								
								if(!(StringUtils.isBlank(studentBean.getLandMark())  && StringUtils.isBlank(studentFromStudentZone.getLandMark())) ){
									mismatchList.add(mmBean); 
								}
							}
							
							if(studentBean.getHouseNoName() == null || studentFromStudentZone.getHouseNoName() == null || (!studentBean.getHouseNoName().equalsIgnoreCase(studentFromStudentZone.getHouseNoName().trim()))){
								
								if(!(StringUtils.isBlank(studentBean.getHouseNoName())  && StringUtils.isBlank(studentFromStudentZone.getHouseNoName())) ){
									mismatchList.add(mmBean); 
								}
							}
							//Added shipping address fields of Street/Locality/LandMark/HouseNo.Name for mismatch check end
							
							
							if(studentBean.getCenterName() == null || studentFromStudentZone.getCenterName() == null || (!studentBean.getCenterName().equalsIgnoreCase(studentFromStudentZone.getCenterName().trim()))){
								
								if(!(StringUtils.isBlank(studentBean.getCenterName())  && StringUtils.isBlank(studentFromStudentZone.getCenterName()))  ){
									mismatchList.add(mmBean); 
								}
							} 

						}else{
							isMismatch = true;mmBean.setSapid(studentBean.getSapid());mmBean.setFirstName(studentBean.getFirstName());mmBean.setLastName(studentBean.getLastName());
							mmBean.setMismatchType("Student Not Found in Student Zone");
							//logger.info("Student Not Found in Student Zone");
						}
					} catch (Exception e) {
						logger.info("Error in comparing for student:"+studentBean);
					}
					/*Commented by PS as was giving extra records
					 * if(isMismatch){
						mismatchList.add(mmBean); mmBean = new StudentDataMismatchBean();
					}*/
				}
				logger.info("salesforceStudentList size : "+salesforceStudentList.size());
				logger.info("mismatchList Size : "+mismatchList.size());
				// get All Student whose registration for current Acads Month and Year not present but record present in exam.students
				HashMap<String, StudentBean> getAllStudentsNotPresentInRegistrationDataTableForCurrentAcadMonthAndYear = studentZoneDAO.getAllStudentsNotPresentInRegistrationDataTableForCurrentAcadMonthAndYear(CURRENT_ACAD_MONTH, CURRENT_ACAD_YEAR);
				
				// get All Student whose program in registration different for current Acads Month and Year 
				HashMap<String, StudentBean> getAllStudentsProgramDifferentfromRegistrationDataTableForCurrentAcadMonthAndYear = studentZoneDAO.getAllStudentsProgramDifferentfromRegistrationDataTableForCurrentAcadMonthAndYear(CURRENT_ACAD_MONTH, CURRENT_ACAD_YEAR);
				List<ArrayList<StudentDataMismatchBean>> mismatchListSplitted = split(mismatchList);
				logger.info("mismatchList.size:"+mismatchList.size());
				logger.info("getAllStudentsNotPresentInRegistrationDataTableForCurrentAcadMonthAndYear.size:"+getAllStudentsNotPresentInRegistrationDataTableForCurrentAcadMonthAndYear.size());
				logger.info("getAllStudentsProgramDifferentfromRegistrationDataTableForCurrentAcadMonthAndYear.size:"+getAllStudentsProgramDifferentfromRegistrationDataTableForCurrentAcadMonthAndYear.size());
				response.setMessage("Mismatch in Student Info: "+mismatchList.size() );
				List<String> details = new ArrayList<String>();
				details.add("Email;mismatch in email: "+emailMismatch);
				details.add("Mobile;mismatch in mobile: "+mobileMismatch);
				details.add("Program;mismatch in program: "+programMismatch);
				details.add("Validity End Month;mismatch in validity end month: "+validityEndMonth);
				details.add("Validity End Year;mismatch in validity end year: "+validityEndYear);
				details.add("Alternate Phone;mismatch in alternate mobile: "+altPhn);
				details.add("Dob;mismatch in dob: "+dob);
				details.add("Gender;mismatch in gender: "+gender);
				details.add("Registration;mismatch in registration date: "+regdate);
				details.add("is Lateral;mismatch in is lateral: "+isLateral);
				details.add("City;mismatch in city: "+city);
				details.add("State;mismatch in state: "+state);
				details.add("Country;mismatch in country: "+country);
				details.add("Pin;mismatch in pin: "+pin);
				details.add("Previous Student Id;mismatch in previous student id: "+previousStudentid);
				details.add("Center Code;mismatch in center code: "+centername);
				details.add("Image Url;mismatch in image Url: "+imgUrl);
				response.setDetailedMessage(details); 
				response.setStudents(accountMismatchStudents);
				
			} else {
				logger.info("No records found.");
			}
		}catch(ConnectionException ce){ 
			ce.printStackTrace();  
			logger.info("Error : Unable to connect to salesforce : findSalesforceStudentZoneMismatch ");
		}catch(Exception e){
			e.printStackTrace(); 
			logger.info("Error: Mismatch job not running: findSalesforceStudentZoneMismatch");
		}  
	    return new ResponseEntity<StatusBean>(response,headers, HttpStatus.OK);
	} 
	@RequestMapping(value = "/statusOfShipment", method = {RequestMethod.GET},produces = "application/json")
	public ResponseEntity<StatusBean> statusOfShipment( HttpServletRequest reqst,HttpServletResponse respons) throws IOException {
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		StatusBean response = new StatusBean(); 
		QueryResult qResult = new QueryResult();
        System.out.println("calling statusOfShipment");
		try {
			 
			String soqlQuery = "Select Id, Opportunity__c, Order_Type__c, Student_Email__c, Student_Number__c,Semester__c, "
					+ " Quantity__c, Shipment_Date__c, To_Student__c, To_Centers__c,Status_Of_Dispatch__c, Stock_Keeping_Unit__c,"
					+ " Fed_Ex_Shipment_Created__c,Self_Learning_Material_For_Student__c, To_Student__r.Name, Tracking_Number__c "
					+ " from Dispatch_Order__c where  Fed_Ex_Shipment_Created__c = true and ( Status_Of_Dispatch__c = 'Transit' OR Status_Of_Dispatch__c = 'AWB created' ) " ;

		
			qResult = connection.query(soqlQuery);
			boolean done = false;
			logger.info("Found "+qResult.getSize()+" Records from Salesforce.");
			HashMap<String, String> dispatchOrderIdStudentEmailMap = new HashMap<String, String>();
			HashMap<String,String> dispatchOrderIdStudentNumberMap = new HashMap<String, String>();
			HashMap<String,StudentBean> studentNumberStudentMap = new HashMap<String, StudentBean>();
			List<StudentBean> studentList = new ArrayList<StudentBean>();
		    int orderCreated=0;
		    int inTransit=0;
			if (qResult.getSize() > 0) {
				ArrayList<TrackRequest> requestList = new ArrayList<>();

				ObjectFactory objectFactory = new ObjectFactory();
				logger.info("Creating Track Request for each Dispatch orders."); 
				while (!done) {
					SObject[] records = qResult.getRecords();
					for (int i = 0; i < records.length; ++i) {
						SObject s = (SObject) records[i];
						StudentBean bean = new StudentBean();
						String dispatchOrderId = (String)s.getField("Id");
						String studentEmailId = (String)s.getField("Student_Email__c");
						String studentNumber = (String)s.getField("Student_Number__c");
						String studentSemester = (String)s.getField("Semester__c");
						String statusOfDispatch = (String)s.getField("Status_Of_Dispatch__c");
						//Added to check the value of self learning material and current status of dispatch//
						String selfLearningMaterial = (String)s.getField("Self_Learning_Material_For_Student__c");
						
						bean.setEmailId(studentEmailId);
						bean.setSapid(studentNumber);
						bean.setSem(studentSemester);
						bean.setStatus(statusOfDispatch);
						studentList.add(bean);
						//End//
						dispatchOrderIdStudentEmailMap.put(dispatchOrderId, studentEmailId);
						dispatchOrderIdStudentNumberMap.put(dispatchOrderId, studentNumber);//Map of dispatchOrder id and student number since required in generic email//
						studentNumberStudentMap.put(studentNumber, bean); // Map of Student Number and Semester 
						
						TrackRequest request = objectFactory.createTrackRequest();
						
						TrackSelectionDetail selectionDetail = new TrackSelectionDetail();
						selectionDetail.setCarrierCode(CarrierCodeType.FDXE);
						TrackPackageIdentifier packageIdentifier = new TrackPackageIdentifier();
						packageIdentifier.setType(TrackIdentifierType.TRACKING_NUMBER_OR_DOORTAG);
						packageIdentifier.setValue((String)s.getField("Tracking_Number__c"));
						selectionDetail.setPackageIdentifier(packageIdentifier);
						request.getSelectionDetails().add(selectionDetail);
						
						request.getProcessingOptions().add(TrackRequestProcessingOptionType.INCLUDE_DETAILED_SCANS);

						TransactionDetail transactionDetail = new TransactionDetail();
						transactionDetail.setCustomerTransactionId((String)s.getField("Id"));
						request.setTransactionDetail(transactionDetail);

						requestList.add(request);
					}
					if (qResult.isDone()) {
						done = true;
					} else {
						logger.info("Querying more.....");
						qResult = connection.queryMore(qResult.getQueryLocator());
					}
				}
				logger.info("Connecting to Fedex...");
				WebAuthenticationDetail webAuthenticationDetail = new WebAuthenticationDetail();
				WebAuthenticationCredential userCredential = new WebAuthenticationCredential();
				userCredential.setKey(FEDEX_AUTH_KEY);
				userCredential.setPassword(FEDEX_AUTH_PASSWORD);
				webAuthenticationDetail.setUserCredential(userCredential);

				ClientDetail clientDetail = new ClientDetail();
				clientDetail.setAccountNumber(FEDEX_ACCOUNT_NUMBER);
				clientDetail.setMeterNumber(FEDEX_METER_NUMBER);

				VersionId version = new VersionId();
				version.setServiceId("trck");
				version.setMajor(10);
				version.setIntermediate(0);
				version.setMinor(0);
 
				ArrayList<SObject> dosToUpdate = new ArrayList<SObject>();
				ArrayList<String> deliveredStudentEmailIds = new ArrayList<String>(); 
				logger.info("Started sending track requests.....");
				int errorCount=0;
				int statusChangeCount=0;
				int j = 0;
				for (TrackRequest trackRequest : requestList) {
					trackRequest.setWebAuthenticationDetail(webAuthenticationDetail);
					trackRequest.setClientDetail(clientDetail);
					trackRequest.setVersion(version);
					TrackReply reply = fedExTrackClient.trackShipment(trackRequest);
					System.out.println("reply"+reply);
					try { 
						NotificationSeverityType highestError = reply.getHighestSeverity();
						if(highestError == NotificationSeverityType.ERROR || highestError == NotificationSeverityType.FAILURE){
							errorCount++; 
						}else{
							TrackStatusDetail statusDetail = reply.getCompletedTrackDetails().get(0).getTrackDetails().get(0).getStatusDetail();
							String status = statusDetail.getCode(); 
							logger.info("Status = "+status);
							String prevStatus = studentList.get(j).getStatus();
							prevStatus = (prevStatus.equalsIgnoreCase("AWB created"))?"OC":"IT";
							if(!prevStatus.equalsIgnoreCase(status)){
								statusChangeCount++;
								if(prevStatus.equalsIgnoreCase("OC")) {
									orderCreated++;
								}else {
									inTransit++;
								}
							} 
						} 
					} catch (Exception e) { }  
				} 
				//Update data back in Salesforce
				response.setMessage("Mismatch: "+statusChangeCount); 
				List<String> details = new ArrayList<String>();
				details.add("pending to update status : "+statusChangeCount); 
				details.add("for Order Created : "+orderCreated); 
				details.add("for In Transit : "+inTransit); 
				response.setDetailedMessage(details); 
				//response.setFailureCount(errorCount);
			}
		} catch (ApiFault e) {  
		}catch (Exception e) {   
		}   
		
		return new ResponseEntity<StatusBean>(response,headers, HttpStatus.OK);
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
	@RequestMapping(value = "/statusOfSalesforceRegistrationData", method = {RequestMethod.GET},produces = "application/json")
	public ResponseEntity<StatusBean> statusOfSalesforceRegistrationData( HttpServletRequest reqst) throws IOException {
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		StatusBean response = new StatusBean(); 
		
		 SchedulerApisBean sbean = new SchedulerApisBean(); 
			
			System.out.println("Querying Student Master details from SFDC"); 
			logger.info("--->SalesForce Portal Account/Opportunity Sync  ");
			 
			
			int UPDATE_BATCH_SIZE = 5;
			
			// list for updating back in salesforce
			ArrayList<SObject> opportunitySObjectListToUpdateBackInSalesforce = new ArrayList<SObject>();
			ArrayList<SObject> accountSObjectListToUpdateBackInSalesforce = new ArrayList<SObject>();
			ArrayList<StudentBean> studentToCreateInLDAP = new ArrayList<StudentBean>();
			
			//List for sending error emails 
			ArrayList<StudentBean> invalidSalesforceRecordsList = new ArrayList<StudentBean>();
			ArrayList<Person> ldapErrorListForEmail = new ArrayList<Person>();
			ArrayList<StudentBean> studentZoneUpdateErrorListForEmail = new ArrayList<>();
			ArrayList<String> salesforceUpdateErrorListForEmail = new ArrayList<>();
			ArrayList<StudentBean> studentZoneSuccessfulListForEmail = new ArrayList<>();
			List<StudentBean> salesforceStudentList = new ArrayList<>();
			QueryResult qResult = new QueryResult();
			List<StatusBean> accountMismatchStudents= new ArrayList<StatusBean>();
			try { 
				// query for Student Master records for give SFDC record Id
				 System.out.println("query for Student Master records for give SFDC record Id"); 
				// below query updates 27 fields in student table and 4 fields of reg table
				String soqlQuery =" SELECT Account.Id,id,"
						+ "	Account.nm_NewPassword__c," //Password ldap
						+ " Account.Synced_With_LDAP__c,"
						+ " Synced_With_StudentZone__c ,"
						
						//student personal information start 
						+ " Account.FirstName," //firstName
						+ " Account.LastName,"//lastName
						+ " Account.nm_Gender__c," // gender
						+ " Account.nm_DateOfBirth__c," //dob
						+ " Account.nm_StudentImageUrl__c ,"//studentImage
						+ " Account.Father_First_Name__c,"//father name
						+ " Account.Mother_First_Name__c," //mothername
						//student personal information end
						
						//student contact information start
						+ " Account.PersonEmail," //emailId
						+ " Account.PersonMobilePhone," //mobile
						+ " Account.Phone," //altPhone
						+ " Account.Address__c, " //address
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
						//student program information end
						
						//CS field start
						+ " Career_Service__c"// the purchased CS package
						//CS field end
						
						+ " FROM Opportunity "
						+ " where Account.nm_StudentStatus__c = 'Confirmed' and StageName = 'Closed Won'  and ( Synced_With_StudentZone__c = false  or Account.Synced_With_LDAP__c = false) " ;

				qResult = connection.query(soqlQuery);
				boolean done = false;
				//System.out.println("qResult.getSize()"+qResult.getSize()); 
				
				if (qResult.getSize() > 0) { 
	                int count=0;
					logger.info("Number to Records Sync: "+ qResult.getSize());
					while (!done) {
						SObject[] records = qResult.getRecords();
						for (int i = 0; i < records.length; ++i) {
							StatusBean mismatchStudent = new StatusBean(); 
							
							logger.info("Record: "+ i + "/" + qResult.getSize());
							count=i+1;
							try {
								SObject s = (SObject) records[i];
								
								StudentBean student = syncsSheduler.createStudentBeanFromSalesforceData(s);
								mismatchStudent.setSapid(student.getSapid());
								logger.info("Account " + (i + 1) + ": " + student.toString());
								
								//validate StudentBean record 
								try {
									syncsSheduler.validateStudentBean(student);
								} catch (Exception e) {
									logger.info("Error in validating studentBean:"+student.getSapid());
								}
								
								if(!student.isErrorRecord()){
									
									salesforceStudentList.add(student);
									mismatchStudent.setStatus("Portal Sync");
									if(!student.isAccountSyncedWithLDAP()){
										studentToCreateInLDAP.add(student);
										  
									} 
									accountMismatchStudents.add(mismatchStudent);
								}else{// adding invalid records in list and sending in mail
									invalidSalesforceRecordsList.add(student);
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
					
				}
				
			} catch (ApiFault e) { 
			}catch(Exception e){  }
		
			response.setMessage(checkRegistrationCount());
			List<String> details= new ArrayList<String>();
			
			details.add("all students with sync flag false: "+qResult.getSize());
			details.add("Portal Sync;student pending to sync : "+salesforceStudentList.size());
			//details.add("Ldap Sync;student pending to sync in LDAP: "+studentToCreateInLDAP.size());
			response.setDetailedMessage(details);   
			response.setStudents(accountMismatchStudents);
			
		return new ResponseEntity<StatusBean>(response,headers, HttpStatus.OK);
	}
	public String checkRegistrationCount() throws IOException {
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		StatusBean response = new StatusBean(); 
		List<StudentBean> studentsRegisteredInPortal =  studentZoneDAO.getStudentsRegisteredForCurrentCycle(CURRENT_ACAD_YEAR,CURRENT_ACAD_MONTH);
		
		QueryResult qResult = new QueryResult();
		String soqlQuery =" SELECT Student_Number__c   FROM Opportunity  " + 
				" where  StageName='Closed Won' and " + 
				"  Drive_Month__c = '"+CURRENT_ACAD_MONTH+"' and Enrollment_Year__c ='"+CURRENT_ACAD_YEAR+"' and Account.nm_StudentStatus__c= 'Confirmed'" ;
        String msg="";
		try {
			qResult = connection.query(soqlQuery);
			System.out.println("qResult size:"+qResult.getSize());
		} catch (ConnectionException e) { 
			e.printStackTrace();
		}
		msg = studentsRegisteredInPortal.size()+"/"+ qResult.getSize()+" Synced (Current Cycle)<br>Mismatch: "+Math.abs(qResult.getSize()-studentsRegisteredInPortal.size());
		return msg;
	}
	@RequestMapping(value = "/statusOfRevenueSyncForPreviousDay", method = {RequestMethod.GET},produces = "application/json")
	public ResponseEntity<StatusBean> statusOfRevenueSyncForPreviousDay( HttpServletRequest request) throws IOException {
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		StatusBean response = new StatusBean();
		
		System.out.println("Running statusOfRevenueSyncForPreviousDay");
		  
		// Get yesterdays day
		Date today = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(today);
		calendar.add(Calendar.DATE, -1);
		
		Date yesterday = calendar.getTime();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

		String dateStr = formatter.format(yesterday);
		System.out.println("Fetching Revenew report for date:"+dateStr);
		List<RevenueReportField> revenueBeans = revenueReportHelper.getCollectionBeansForSync(dateStr);
	    List<RevenueReportField> errorRevenueRecords = new ArrayList<RevenueReportField>();
	    List<RevenueReportField> collectionsToInsert = new ArrayList<RevenueReportField>();
	    System.out.println("Revenue list size :"+revenueBeans.size());
	    int pendingToSyncCount=0;
	    // Loop through the beans. If any bean has an error or the amount is 0, add them to the fail list else to insert list
	    for (RevenueReportField revenue : revenueBeans) {
			if(!StringUtils.isBlank(revenue.getErrorMessage())) {
				errorRevenueRecords.add(revenue);
			} else if(revenue.getAmount().equals("0") && revenue.getRefundedAmount().equals("0")) {
				revenue.setErrorMessage("No revenue/refunds for the day.");
				errorRevenueRecords.add(revenue);
			} else {
				collectionsToInsert.add(revenue);
				SObject revenueObjectFromSFDC;
				try {
					revenueObjectFromSFDC = getSObjectForRevenueIfExists(revenue);
					if(revenueObjectFromSFDC==null) {
						pendingToSyncCount++;
					}
				} catch (ConnectionException e) { 
					e.printStackTrace();
				} 
			}
		}
	    List<String> details= new ArrayList<String>();
	    //response.setSuccessCount(collectionsToInsert.size());
	    //response.setFailureCount(pendingToSyncCount);
	    response.setMessage("Revenue Report For Today: "+revenueBeans.size());
	    details.add(revenueBeans.size()+" Records found for today");
	    if(pendingToSyncCount==0) {
	    	details.add("All Synced ");
	    }else {
	    	details.add("Pending to Sync: "+pendingToSyncCount);
	    }  
	    response.setSuccessCount(0); 
	    response.setFailureCount(0); 
	    response.setDetailedMessage(details); 
	    return new ResponseEntity<StatusBean>(response,headers, HttpStatus.OK);
	}
	private SObject getSObjectForRevenueIfExists(RevenueReportField revenue) throws ConnectionException {
		QueryResult qResult = new QueryResult();
		String soqlQuery =""
				+ " SELECT "
					+ " nm_payment__c.PaymentID__c, "
					+ " nm_Payment__c.Refunded_Amount__c, "
					+ " nm_Payment__c.nm_PaymentStatus__c, "
					+ " nm_Payment__c.nm_PaymentType__c, "
					+ " nm_Payment__c.nm_Amount__c, "
					+ " nm_Payment__c.nm_ActualPaymentAmmount__c, "
					+ " nm_Payment__c.Prospect__c, "
					+ " nm_Payment__c.nm_TransactionDate__c "
				+ " FROM nm_Payment__c "
				+ " WHERE nm_Payment__c.nm_PaymentType__c = '" + revenue.getType() + "' "
				+ " AND nm_Payment__c.nm_TransactionDate__c = " + revenue.getDate() + " ";

		qResult = connection.query(soqlQuery);
		if (qResult.getSize() > 0) {
			SObject[] records = qResult.getRecords();
			SObject s = (SObject) records[0];
			return s;
		}
		return null;
	}
	@RequestMapping(value = "/statusOfStudentLearningMetricsWithSalesforce", method = {RequestMethod.GET},produces = "application/json")
	public ResponseEntity<StatusBean> statusOfStudentLearningMetricsWithSalesforce( HttpServletRequest request,HttpServletResponse respons) throws IOException {
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		StatusBean response = new StatusBean(); 
		 
		//mismatch counts 
		int totalNoOfANSCount = 0;
		int totalNoOfAssignSubmittedCount = 0;
		int total_No_Of_Passed_SubjectsCount = 0;
		int total_No_Of_Failed_SubjectsCount = 0;
		int total_No_Of_Booked_SubjectsCount = 0;
		int total_No_Of_Booking_Pending_SubjectsCount = 0;
		int Re_Reg_ProbabilityCount =0; 
		// query Student Learning Metrics 
		studentZoneDAO = (StudentZoneDao)act.getBean("studentZoneDAO");
		Map<String,StudentLearningMetricsBean> mapOfSapIdAndStudentLearningMetrics = studentZoneDAO.getStudentLearningMetricsMap();
		logger.info("fetched "+ mapOfSapIdAndStudentLearningMetrics.size()+" learning metrics entries from studentZone");
		  
		QueryResult qResult = new QueryResult();
        List<String> studentList= new ArrayList<String>();
        List<StatusBean> accountMismatchStudents= new ArrayList<StatusBean>();
		try {
			 
			String soqlQuery = "SELECT id,nm_StudentNo__c,totalNoOfANS__c,totalNoOfAssignSubmitted__c,Total_No_Of_Passed_Subjects__c,Total_No_Of_Failed_Subjects__c,Total_No_Of_Booked_Subjects__c,Total_No_Of_Booking_Pending_Subjects__c,Re_Reg_Probability__c "
							+ " FROM Account"
							+ " where nm_StudentStatus__c = 'Confirmed' and nm_StudentNo__c !=null ";
				 
			qResult = connection.query(soqlQuery);
			boolean done = false;
			System.out.println("Fetched "+qResult.getSize()+" confirmed students from salesforce");
			if (qResult.getSize() > 0) {

				while (!done) {
					SObject[] records = qResult.getRecords();
					for (int i = 0; i < records.length; ++i) {
						SObject s = (SObject) records[i];
						String sapId = (String)s.getField("nm_StudentNo__c");
						StatusBean mismatchStudent = new StatusBean(); 
						mismatchStudent.setSapid(sapId);
						if(mapOfSapIdAndStudentLearningMetrics.containsKey(sapId))
						{ 
							StudentLearningMetricsBean bean = mapOfSapIdAndStudentLearningMetrics.get(sapId);
							  
							int totalNoOfANS = new Double((String) s.getField("totalNoOfANS__c")).intValue();
							int totalNoOfAssignSubmitted = new Double((String)s.getField("totalNoOfAssignSubmitted__c")).intValue();
							int total_No_Of_Passed_Subjects = new Double((String)s.getField("Total_No_Of_Passed_Subjects__c")).intValue();
							int total_No_Of_Failed_Subjects = new Double((String)s.getField("Total_No_Of_Failed_Subjects__c")).intValue();
							int total_No_Of_Booked_Subjects = new Double((String)s.getField("Total_No_Of_Booked_Subjects__c")).intValue();
							int total_No_Of_Booking_Pending_Subjects = new Double((String)s.getField("Total_No_Of_Booking_Pending_Subjects__c")).intValue();
							int Re_Reg_Probability = new Double((String)s.getField("Re_Reg_Probability__c")).intValue();
							if((totalNoOfANS!=bean.getTotalNoOfANS()) && bean.getTotalNoOfANS()!=0  ) {
								totalNoOfANSCount++;
								if(!studentList.contains(sapId)) {
									studentList.add(sapId);
									mismatchStudent.setStatus("Total No Of ANS Count"); 
									accountMismatchStudents.add(mismatchStudent);
								}
							} 
							if((totalNoOfAssignSubmitted!=bean.getTotalNoOfAssignSubmitted()) && bean.getTotalNoOfAssignSubmitted()!=0) {
								totalNoOfAssignSubmittedCount++;
								if(!studentList.contains(sapId)) {
									studentList.add(sapId);
									mismatchStudent.setStatus("Total No Of Assign Submitted"); 
									accountMismatchStudents.add(mismatchStudent);
								}
							} 
							if((total_No_Of_Passed_Subjects!=bean.getTotalNoOfPassedSubjects()) && bean.getTotalNoOfPassedSubjects()!=0) {
								total_No_Of_Passed_SubjectsCount++;
								if(!studentList.contains(sapId)) {
									studentList.add(sapId);
									mismatchStudent.setStatus("Total No Of Passed Subjects"); 
									accountMismatchStudents.add(mismatchStudent);
								}
							} 
							if((total_No_Of_Failed_Subjects!=bean.getTotalNoOfFailedSubjects()) && bean.getTotalNoOfFailedSubjects()!=0) {
								total_No_Of_Failed_SubjectsCount++;
								if(!studentList.contains(sapId)) {
									studentList.add(sapId);
									mismatchStudent.setStatus("Total No Of Failed Subjects"); 
									accountMismatchStudents.add(mismatchStudent);
								}
							} 
							if((total_No_Of_Booked_Subjects!=bean.getTotalNoOfBookedSubjects()) && bean.getTotalNoOfBookedSubjects()!=0) {
								total_No_Of_Booked_SubjectsCount++;
								if(!studentList.contains(sapId)) {
									studentList.add(sapId);
									mismatchStudent.setStatus("Total No Of Booked Subjects Count"); 
									accountMismatchStudents.add(mismatchStudent);
								}
							} 
							if((total_No_Of_Booking_Pending_Subjects!=bean.getTotalNoOfBookingPendingSubjects()) && bean.getTotalNoOfBookingPendingSubjects()!=0) {
								total_No_Of_Booking_Pending_SubjectsCount++;
								if(!studentList.contains(sapId)) {
									studentList.add(sapId);
									mismatchStudent.setStatus("Total No Of Booking Pending Subjects"); 
									accountMismatchStudents.add(mismatchStudent);
								}
							}
							if((Re_Reg_Probability!=bean.getReRegProbability()) && bean.getReRegProbability()!=0) {
								Re_Reg_ProbabilityCount++;
								if(!studentList.contains(sapId)) {
									studentList.add(sapId);
									mismatchStudent.setStatus("Re Reg Probability"); 
									accountMismatchStudents.add(mismatchStudent);
								}
							} 
						}
					}
					if (qResult.isDone()) {
						done = true;
					} else {
						System.out.println("Querying more.....");
						qResult = connection.queryMore(qResult.getQueryLocator());
					}
				}
			}
		} catch (ApiFault e) {
			System.out.println(e.getMessage());
		}catch(Exception e){ 
			System.out.println(e.getMessage());
		}  
		List<String> details= new ArrayList<String>();  
	    //response.setFailureCount(0);
	    response.setMessage("Mismatch: "+studentList.size());
	    details.add("Total No Of ANS Count;total ANS Count Mismatch: "+totalNoOfANSCount); 
	    details.add("Total No Of Assign Submitted;total AssignSubmitted Count Mismatch"+totalNoOfAssignSubmittedCount); 
	    details.add("Total No Of Passed Subjects;total Passed Subjects Count Mismatch"+total_No_Of_Passed_SubjectsCount);
	    details.add("Total No Of Failed Subjects;total Failed Subjects Count Mismatch"+total_No_Of_Failed_SubjectsCount);
	    details.add("Total No Of Booked Subjects Count;total Booked Subjects Count Mismatch"+total_No_Of_Booked_SubjectsCount);
	    details.add("Total No Of Booking Pending Subjects;total Booking Pending Subjects Count Mismatch"+total_No_Of_Booking_Pending_SubjectsCount);
	    details.add("Re Reg Probability;Re_Reg Probability Count Mismatch"+Re_Reg_ProbabilityCount); 
	    response.setDetailedMessage(details);
	    response.setStudents(accountMismatchStudents);
	    return new ResponseEntity<StatusBean>(response,headers, HttpStatus.OK);
	}
	@RequestMapping(value = "/statusOfStagedRegistrationData", method = {RequestMethod.GET},produces = "application/json")
	public ResponseEntity<StatusBean> statusOfStagedRegistrationData( HttpServletRequest request,HttpServletResponse respons) throws IOException {
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		StatusBean response = new StatusBean(); 
		
		List<StudentBean> studentList = new ArrayList<>();
		studentList = studentZoneDAO.getRegistrationDataFromStagingTable();
		//response.setFailureCount(0);
	    response.setMessage("Mismatch: "+studentList.size());   
	    return new ResponseEntity<StatusBean>(response,headers, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/test", method = {RequestMethod.GET})
	public String test( HttpServletRequest request,HttpServletResponse respons) throws IOException {
		System.out.println("Test Controller!");
		StudentBean studentBean=new StudentBean();
		studentBean.setSapid("7712589364");
		studentBean.setFirstName("Arpita");
		studentBean.setLastName("Jain");
		studentBean.setProgram("MBA(WX) - AF and LS");
		studentBean.setEnrollmentMonth("Jan");
		studentBean.setEnrollmentYear("2022");
		studentBean.setValidityEndMonth("Jul");
		studentBean.setValidityEndYear("2025");
		studentBean.setMobile("8888888888");
		studentBean.setDob("08.01.1988");
		studentBean.setBloodGroup("O+");
		studentBean.setCenterName("Hyderabad - Secunderabad");
		studentBean.setAddress("Flat No 002,Prudential Petunia, Housing Board, Gogol,South Goa, Goa, 403601, India");
		studentBean.setImageUrl("https://studentdocumentsngasce.s3.ap-south-1.amazonaws.com/StudentDocuments/00Q2j000005Rtxg/00Q2j000005Rtxg_Ah77_Picture.jpg");
		idHelper.generateIdCardURL(studentBean);
		 
	    return "";
	}
}
