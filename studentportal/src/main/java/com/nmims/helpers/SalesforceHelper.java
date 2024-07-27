package com.nmims.helpers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.nmims.beans.SalesforceStudentAccountBean;
import com.nmims.beans.SessionQueryAnswerStudentPortal;
import com.nmims.beans.StudentOpportunity;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.daos.StudentInfoCheckDAO;
import com.nmims.exceptions.StudentNotFoundException;
import com.sforce.soap.partner.Error;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.SaveResult;
import com.sforce.soap.partner.fault.ApiFault;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.bind.XmlObject;

@Component("salesforceHelper")
public class SalesforceHelper {
	
	public SalesforceHelper(SFConnection sf) {  
		this.connection = SFConnection.getConnection();
	}


	public void init(){ 
		SFConnection sf= new SFConnection(SFDC_USERID,SFDC_PASSWORD_TOKEN); 
		this.connection = SFConnection.getConnection(); 
	}
	
	@Value( "${SFDC_USERID}" )
	private String SFDC_USERID;

	@Value( "${SFDC_PASSWORD_TOKEN}" )
	private String SFDC_PASSWORD_TOKEN;
	
	@Value( "${ENVIRONMENT}" )
	private String ENVIRONMENT;
	
	@Autowired
	MailSender mailer;
	@Autowired
	SFConnection sfc;
	@Autowired
	StudentInfoCheckDAO sDao;
	private static final Logger logger = LoggerFactory.getLogger(SalesforceHelper.class);
	private static final Logger loggerPFSyncPDDM = LoggerFactory.getLogger("salesforcePFSyncPDDM");
	private static final String pddmProgram = "'C-SEM','C-SMM','C-DMA','C-SEM & DMA','C-SMM & DMA','C-SEM & SMM','PDDM','PC-DM'";
	private PartnerConnection connection;
	public SessionQueryAnswerStudentPortal createCaseInSalesforce(StudentStudentPortalBean student,SessionQueryAnswerStudentPortal sessionQuery){
		System.out.println("Creating Cases in SFDC");

		/*
		 * ConnectorConfig config = new ConnectorConfig();
		 * config.setUsername(SFDC_USERID); config.setPassword(SFDC_PASSWORD_TOKEN);
		 * 
		 * System.out.println("SFDC_USERID = "+SFDC_USERID);
		 * System.out.println("SFDC_PASSWORD_TOKEN = "+SFDC_PASSWORD_TOKEN);
		 */

		String errorMessage ="";
		QueryResult queryResults = new QueryResult();

		try {
			//PartnerConnection connection = Connector.newConnection(config);
			// query for documents records for give SFDC record Id
			/*
			 * System.out.println("Auth EndPoint: "+config.getAuthEndpoint());
			 * System.out.println("Service EndPoint: "+config.getServiceEndpoint());
			 * System.out.println("Username: "+config.getUsername());
			 * System.out.println("SessionId: "+config.getSessionId());
			 */
			String soqlQuery = "SELECT Id, nm_StudentNo__c FROM Account"
					+ " where nm_StudentNo__c = '" + student.getSapid() + "' ";

			queryResults = connection.query(soqlQuery);
			String accountRecordId = "";

			String Description = "Subject Name : "+sessionQuery.getSubject()+"\n"
					+"Query :"+"\n\n"
					+sessionQuery.getQuery();

			if (queryResults.getSize() > 0) {
				for (SObject s: queryResults.getRecords()) {
					accountRecordId = (String)s.getField("Id");
					break;
				}
				SObject sObject = new SObject();
				sObject.setType("Case");
				sObject.setField("AccountId", accountRecordId);
				sObject.setField("RecordTypeId", "01290000000A959");//set Record Type as Student Zone
				sObject.setField("purpose__c", "Enquiry");
				sObject.setField("nm_Category__c", "Academics");
				sObject.setField("sub_categories__c","Post a Query");
				sObject.setField("Description", Description);
				sObject.setField("Subject", "Post my query");
				sObject.setField("Origin", "Student Zone");
				sObject.setField("Email_from__c", "ngasce@nmims.edu");
				sObject.setField("StudentZone_QueryId__c", sessionQuery.getId());// store Query Id for Updating Ans from Salesforce
				sObject.setField("SuppliedEmail", student.getEmailId());
				sObject.setField("SuppliedPhone", student.getMobile());
				//end//
				SObject[] records = new SObject[1];
				records[0] = sObject;

				// Create Case records in Salesforce.com
				SaveResult[] saveResults = connection.create(records);
				if (saveResults[0].isSuccess()) {
					sessionQuery.setCaseId(saveResults[0].getId());
					System.out.println("Record Created successfully in Salesforce");
				}else{
					System.out.println("Error in Creating record in Salesforce");
					errorMessage = "ERROR: ";
					Error[] errors = saveResults[0].getErrors();
					for (int j=0; j< errors.length; j++) {
						errorMessage += errors[j].getMessage() + " ";
						System.out.println("ERROR updating record: " + errors[j].getMessage());
					}
				}
			}

		}catch(Exception e){
			//init();
			//createCaseInSalesforce(student,sessionQuery);
			
		}
		System.out.println("CaseId--->"+sessionQuery.getCaseId());
		sessionQuery.setErrorMessage(errorMessage);
		return sessionQuery;
	}
	
	public String updateStudentPhotoFieldOnSFDC(String registrationNo,String awsUrl,int retry) {
		try {
			QueryResult queryResult = new QueryResult();
			String soqlQuery = "select id,IsConverted from lead where nm_RegistrationNo__c = '"+ registrationNo +"'";
			queryResult = connection.query(soqlQuery);
			
			if (queryResult.getSize() <= 0) {
				return "Error: lead record not found";
			}
			String isConvereted = "false";
			String leadId = null;
			for (SObject s: queryResult.getRecords()) {
				isConvereted = (String)s.getField("IsConverted");
				leadId = (String) s.getField("Id");
				break;
			}
			
			if(leadId == null) {
				return "Error: Invalid lead details found";
			}
			if("false".equalsIgnoreCase(isConvereted)) {
				//udate lead table column
				SObject sObject = new SObject();
				sObject.setType("lead");
				sObject.setId(leadId);
				sObject.setField("nm_StudentImageUrl__c", awsUrl);
				SObject[] records = new SObject[1];
				records[0] = sObject;
				// update the records in Salesforce.com
				SaveResult[] saveResults = connection.update(records);
				if (saveResults[0].isSuccess()) {
					return "true";
				}
				return "Error: Failed to update record on CRM";
			}else {
				QueryResult queryResult2 = new QueryResult();
				String soqlQuery2 = "select id,nm_Status__c from nm_LinksForDocuments__c where Registration_No__c = '"+ registrationNo +"' and Name = 'Student Photograph'";
				queryResult2 = connection.query(soqlQuery2);
				if (queryResult.getSize() <= 0) {
					return "Error: document record not found";
				}
				String document_status = "";
				String documentId = null;
				for (SObject s: queryResult2.getRecords()) {
					document_status = (String) s.getField("nm_Status__c");
					documentId = (String) s.getField("Id");
					break;
				}
				if(documentId == null) {
					return "Error: document status or id not found";
				}
				if(document_status == null) {
					document_status = "";
				}
				if(document_status != null && !"approved".equalsIgnoreCase(document_status.toLowerCase())) {
					//update in document field;
					SObject sObject = new SObject();
					sObject.setType("nm_LinksForDocuments__c");
					sObject.setId(documentId);
					sObject.setField("nm_URLforDocuments__c", awsUrl);
					if(!"".equalsIgnoreCase(document_status.trim())) {
						sObject.setField("nm_Status__c", "Re-Submited");
					}
					SObject[] records = new SObject[1];
					records[0] = sObject;
					// update the records in Salesforce.com
					SaveResult[] saveResults = connection.update(records);
					if (saveResults[0].isSuccess()) {
						return "true";
					}
					return "Error: Failed to update record on CRM";
				}
				return "Error: Can't update document current status is " + document_status;
			}
		}
		catch (ApiFault e) {
			// TODO: handle exception
			if(retry > 0) {
				init();
				return updateStudentPhotoFieldOnSFDC(registrationNo,awsUrl,retry - 1);
			}
			return "Error: " + e.getMessage();
		}
		catch (Exception e) {
			// TODO: handle exception
			return "Error: " + e.getMessage();
		}
	}
	
	public String checkCanUpdateOrUploadStudentPhoto(String registrationNo, int retry) {
		try {
			QueryResult queryResult = new QueryResult();
			String soqlQuery = "select IsConverted,nm_StudentImageUrl__c from lead where nm_RegistrationNo__c = '"+ registrationNo +"'";
			queryResult = connection.query(soqlQuery);
			
			if (queryResult.getSize() <= 0) {
				return "Error: lead record not found";
			}
			String isConvereted = "false";
			for (SObject s: queryResult.getRecords()) {
				isConvereted = (String)s.getField("IsConverted");
				break;
			}
			if("false".equalsIgnoreCase(isConvereted)) {
				// update lead level data
				return "true";
			}else {
				// update document object data on account level
				QueryResult queryResult2 = new QueryResult();
				String soqlQuery2 = "select nm_Status__c from nm_LinksForDocuments__c where Registration_No__c = '"+ registrationNo +"' and Name = 'Student Photograph'";
				queryResult2 = connection.query(soqlQuery2);
				if (queryResult.getSize() <= 0) {
					return "Error: document record not found";
				}
				String document_status = "";
				for (SObject s: queryResult2.getRecords()) {
					document_status = (String) s.getField("nm_Status__c");
					break;
				}
				if(document_status == null) {
					document_status = "";
				}
				if(document_status != null && "approved".equalsIgnoreCase(document_status.toLowerCase())) {
					return "Error: Can't update student profile current status is " + document_status;
				}
				return "true";
			}
						
			/*
			 * for (SObject s: queryResult.getRecords()) {
					accountRecordId = (String)s.getField("Registration_No__c");
					System.out.println("id for record --- > "+accountRecordId);
					break;
				}
			 * */
		} 
		catch (ApiFault e) {
			// TODO: handle exception
			if(retry > 0) {
				init();
				return checkCanUpdateOrUploadStudentPhoto(registrationNo, retry - 1);
			}
			return "Error: " + e.getMessage();
		}
		catch (ConnectionException e) {
			// TODO Auto-generated catch block
			mailer.mailStackTrace("Unable to update stduent photo on sfdc", e);
			return "Error: " + e.getMessage();
		}
		
	}

	public String getReRegistrationDateFromSalesForce(StudentStudentPortalBean student){
		
		/*
		 * ConnectorConfig config = new ConnectorConfig();
		 * config.setUsername(SFDC_USERID); config.setPassword(SFDC_PASSWORD_TOKEN);
		 */
		
		
		
		Date date = new Date();
		String currentDate = new SimpleDateFormat("dd/mm/yyyy").format(date);
		
		String errorMessage = "";
		QueryResult queryResult = new QueryResult();
		try{
			/*
			 * PartnerConnection connection = Connector.newConnection(config);
			 * System.out.println("Auth EndPoint: "+config.getAuthEndpoint());
			 * System.out.println("Service EndPoint: "+config.getServiceEndpoint());
			 * System.out.println("Username: "+config.getUsername());
			 * System.out.println("SessionId: "+config.getSessionId());
			 */
			
			String soqlQuery = "select Session_End_Date__c, nm_Semester__c, Session_Start_Date__c, nm_Type__c "
					+ " , Session__c, Year__c from Calender__c where Category__c='Re-Registration' and nm_Type__c = '" + student.getSapid() + "' "
							+ " and nm_Semester__c = '"+ student.getSem()+ "' and Session_Start_Date__c <='"+currentDate+"' "
									+ " and Session_End_Date__c >= '"+currentDate+"' ";
			queryResult = connection.query(soqlQuery);
			
			if(queryResult.getSize() > 0){
				return "Re-Registration is currently active.";
				
			}else{
				return "Re-Registration is currently in-active.";
			}
		
		}catch(Exception e){
			//init();
			//getReRegistrationDateFromSalesForce(student); 
			
			
		}
		
		
		return "";
		
	}
	
	public String updateSalesforceProfile(String sutdentNo, String email, String mobile,String fatherName,String motherName,String shippingStreet,String shippingCity,
										String shippingState,String shippingPostalCode,String shippingCountry,String shippingLocalityName,
										String shippingHouseName,String altMobile){
		System.out.println("Querying documents details from SFDC");
		String errorMessage ="";
		try {
			/*
			 * ConnectorConfig config = new ConnectorConfig();
			 * config.setUsername(SFDC_USERID); config.setPassword(SFDC_PASSWORD_TOKEN);
			 * 
			 * System.out.println("SFDC_USERID = "+SFDC_USERID);
			 * System.out.println("SFDC_PASSWORD_TOKEN = "+SFDC_PASSWORD_TOKEN);
			 */

		
		QueryResult queryResults = new QueryResult();

		
		/*
		 * PartnerConnection connection = Connector.newConnection(config); // query for
		 * documents records for give SFDC record Id
		 * System.out.println("Auth EndPoint: "+config.getAuthEndpoint());
		 * System.out.println("Service EndPoint: "+config.getServiceEndpoint());
		 * System.out.println("Username: "+config.getUsername());
		 * System.out.println("SessionId: "+config.getSessionId());
		 */
			
			String soqlQuery = "SELECT Id, nm_StudentNo__c FROM Account"
					+ " where nm_StudentNo__c Like \'%" + sutdentNo + "\'";

			queryResults = connection.query(soqlQuery);
			String accountRecordId = "";
			
			if (queryResults.getSize() > 0) {
				for (SObject s: queryResults.getRecords()) {
					accountRecordId = (String)s.getField("Id");
					System.out.println("id for record --- > "+accountRecordId);
					break;
				}
			}
			
			SObject sObject = new SObject();
			sObject.setType("Account");
			sObject.setId(accountRecordId);
			sObject.setField("Shipping_Address_Choice__c","--None--");//This is done since this parameter is checked in account trigger to copy address.
			sObject.setField("PersonEmail", email);
			sObject.setField("PersonMobilePhone", mobile);
			sObject.setField("Father_First_Name__c", fatherName);
			sObject.setField("Mother_First_Name__c", motherName);

		
			System.out.println("Shipping Street for NULL VALUE --->"+shippingStreet);
			
			sObject.setField("Shipping_Street__c", shippingStreet);
			sObject.setField("City_Shipping_Account__c", shippingCity);
			sObject.setField("State_Province_Shipping__c", shippingState);
			sObject.setField("Zip_Postal_Code_Shipping__c", shippingPostalCode);
			sObject.setField("Country_Shipping__c", shippingCountry);
			sObject.setField("Locality_Name_Shipping__c", shippingLocalityName);
//			sObject.setField("Nearest_LandMark_Shipping__c", shippingNearestLandmark);
			sObject.setField("House_No_Name_Shipping_Account__c", shippingHouseName);
			
			//sObject.setField("", altMobile);
			
			//end//
			SObject[] records = new SObject[1];
			records[0] = sObject;
			
			// update the records in Salesforce.com
			SaveResult[] saveResults = connection.update(records);
			if (saveResults[0].isSuccess()) {
				//System.out.println("Student Profile Record Updated successfully in Salesforce");
			}else{
				//System.out.println("Student Profile Error in updating record in Salesforce");
				errorMessage = "ERROR: ";
				Error[] errors = saveResults[0].getErrors();
				for (int j=0; j< errors.length; j++) {
					errorMessage += errors[j].getMessage() + " ";
					//System.out.println("ERROR updating record: " + errors[j].getMessage());
				}
				errorMessage += updateSalesforceErrorFlag(sutdentNo,"Unable to update student profile");
			}
			
		}catch(ConnectionException  ce){
			//init();
			/*
			 * updateSalesforceProfile(sutdentNo,email,mobile,fatherName,motherName,
			 * shippingStreet,shippingCity,shippingState,shippingPostalCode,shippingCountry,
			 * shippingLocalityName,shippingNearestLandmark,shippingHouseName,altMobile);
			 */
			errorMessage = "Unable to connect to salesforce for update student profile.";			
		}catch(Exception e){
			//init();
			/*
			 * updateSalesforceProfile(sutdentNo,email,mobile,fatherName,motherName,
			 * shippingStreet,shippingCity,shippingState,shippingPostalCode,shippingCountry,
			 * shippingLocalityName,shippingNearestLandmark,shippingHouseName,altMobile);
			 */
			mailer.mailStackTrace("Unable to update stduent profile on sfdc", e);
		}
		
		return errorMessage;
	}
	
	
	
	public String updateSalesforceErrorFlag(String sutdentNo,String errorMessage){
		try{
			
			System.out.println("Invoked updateSalesforceErrorFlag");
			/*
			 * ConnectorConfig config = new ConnectorConfig();
			 * config.setUsername(SFDC_USERID); config.setPassword(SFDC_PASSWORD_TOKEN);
			 */
	
			QueryResult queryResults = new QueryResult();
				//PartnerConnection connection = Connector.newConnection(config);
				// query for documents records for give SFDC record Id
				String soqlQuery = "SELECT Id, nm_StudentNo__c FROM Account"
						+ " where nm_StudentNo__c Like \'%" + sutdentNo + "\'";
				queryResults = connection.query(soqlQuery);
				String accountRecordId = "";
				
				if (queryResults.getSize() > 0) {
					for (SObject s: queryResults.getRecords()) {
						accountRecordId = (String)s.getField("Id");
						System.out.println("id for record --- > "+accountRecordId);
						break;
					}
				}
				
			SObject sObject2 = new SObject();
			sObject2.setType("Account");
			sObject2.setId(accountRecordId);
			sObject2.setField("Error_Flag__c", true);
			sObject2.setField("Error_Message__c", errorMessage);
			
			SObject[] errorRecords = new SObject[1];
			errorRecords[0] = sObject2;
			
			// update the records in Salesforce.com
			SaveResult[] saveErrorResults = connection.update(errorRecords);
			if (saveErrorResults[0].isSuccess()) {
				//System.out.println("Success updating record flag in sfdc ");
				return "";
			}else{
				String error = "ERROR: ";
				Error[] errors = saveErrorResults[0].getErrors();
				for (int j=0; j< errors.length; j++) {
					error += errors[j].getMessage() + " ";
				//System.out.println("ERROR updating record flag in sfdc ");
				}
				return error;
			}
		}catch(ConnectionException  ce){
			//init();
			//updateSalesforceErrorFlag(sutdentNo,errorMessage);
			return errorMessage = "Unable to connect to salesforce for update student profile.";			
			}catch(Exception e){
			//init();
			//updateSalesforceErrorFlag(sutdentNo,errorMessage);
			mailer.mailStackTrace("Unable to update errorFlag on sfdc", e);
			return errorMessage = "Unable to update error flag in sfdc";
		}
	}
	
	
	public ArrayList<StudentStudentPortalBean> listOfPaymentsMade(String sapid){
		/*
		 * ConnectorConfig config = new ConnectorConfig();
		 * config.setUsername(SFDC_USERID); config.setPassword(SFDC_PASSWORD_TOKEN);
		 */
		
		
		QueryResult queryResults = new QueryResult();
		ArrayList<StudentStudentPortalBean> listOfPaymentsMade = new ArrayList<StudentStudentPortalBean>();
		try{
			/*
			 * PartnerConnection connection = Connector.newConnection(config);
			 * System.out.println("Auth EndPoint: "+config.getAuthEndpoint());
			 * System.out.println("Service EndPoint: "+config.getServiceEndpoint());
			 * System.out.println("Username: "+config.getUsername());
			 * System.out.println("SessionId: "+config.getSessionId());
			 */
			
			String soqlQuery = "select id, "
					+ " nm_OpportunityNew__r.Account.nm_StudentNo__c, "
					+ " nm_OpportunityNew__r.nm_Session__c, "
					+ " nm_OpportunityNew__r.nm_Year__c , "
					+ " nm_OpportunityNew__r.nm_Semester__c , "
					+ " nm_PaymentStatus__c "
					+ " from "
					+ " nm_Payment__c  "
					+ " where "
					+ " nm_OpportunityNew__r.Account.nm_StudentNo__c = '"+sapid+"' and nm_PaymentType__c ='Admission' and nm_PaymentStatus__c = 'Payment Approved'";
			System.out.println("SOQL-->"+soqlQuery);
			queryResults = connection.query(soqlQuery);
			if(queryResults.getSize()>0){
				for(SObject s :queryResults.getRecords()){
					StudentStudentPortalBean student = new StudentStudentPortalBean();
					XmlObject opportunityField = (XmlObject)s.getField("nm_OpportunityNew__r");
					student.setAcadYear((String)opportunityField.getChild("nm_Year__c").getValue());
					student.setAcadMonth((String)opportunityField.getChild("nm_Session__c").getValue());
					student.setSem((String)opportunityField.getChild("nm_Semester__c").getValue());
					listOfPaymentsMade.add(student);
				}
			}
			for(StudentStudentPortalBean b :listOfPaymentsMade){
				System.out.println(b.getAcadMonth()+"<--->"+b.getAcadYear()+"<-->"+b.getSem());
			}

		}catch(Exception e){
			//init();
			//listOfPaymentsMade(sapid);
			
		}
		System.out.println("List Of Payments size-->"+listOfPaymentsMade.size());

		return listOfPaymentsMade;
		
	}
	public HashMap<String,String> getMapOfDispatchParameterAndValues(String sapid){
		/*
		 * ConnectorConfig config = new ConnectorConfig();
		 * config.setUsername(SFDC_USERID); config.setPassword(SFDC_PASSWORD_TOKEN);
		 * 
		 * System.out.println("SFDC_USERID = "+SFDC_USERID);
		 * System.out.println("SFDC_PASSWORD_TOKEN = "+SFDC_PASSWORD_TOKEN);
		 */

		
		QueryResult queryResults = new QueryResult();
		HashMap<String,String> mapOfDispatchParametersAndValues = new HashMap<String,String>();
		try{
			/*
			 * PartnerConnection connection = Connector.newConnection(config); // query for
			 * documents records for give SFDC record Id
			 * System.out.println("Auth EndPoint: "+config.getAuthEndpoint());
			 * System.out.println("Service EndPoint: "+config.getServiceEndpoint());
			 * System.out.println("Username: "+config.getUsername());
			 * System.out.println("SessionId: "+config.getSessionId());
			 */
		
		String soqlQuery = "SELECT Id, Tracking_Number__c ,To_Centers__r.Address__c,Student_Number__c, Program__c, Order_Type__c,Self_Learning_Material_For_Student__c, Status_Of_Dispatch__c, Semester__c, Delivery_Time__c, Opportunity__c,Current_Track_Details__c , "
				+ " To_Student__r.Name, To_Student__r.Shipping_Street__c,To_Student__r.State_Province_Shipping__c,To_Student__r.City_Shipping_Account__c, "
				+ " To_Student__r.House_No_Name_Shipping_Account__c,To_Student__r.Nearest_LandMark_Shipping__c, To_Student__r.Country_Shipping__c,"
				+ " To_Student__r.Zip_Postal_Code_Shipping__c,To_Student__r.nmStudent_Program__c,To_Student__r.Locality_Name_Shipping__c,To_Student__r.PersonMobilePhone, "	
				+"From_Centers__r.Name"
				+" FROM Dispatch_Order__c"
				+ " where Student_Number__c   = '" + sapid + "' "
				+ "and Fed_Ex_Shipment_Created__c = true and Name_Of_Other_Courier_Service__c='Delhivery' ";
		//System.out.println("SOQL QUERY-->"+soqlQuery);
		queryResults = connection.query(soqlQuery);
		
		if (queryResults.getSize() > 0) {
			for (SObject s: queryResults.getRecords()) {
				//System.out.println("Delivery Date -->"+(String)s.getField("Delivery_Date__c"));
				String trackingNumber=(String)s.getField("Tracking_Number__c");
				mapOfDispatchParametersAndValues.put("trackingNumber", trackingNumber);
				mapOfDispatchParametersAndValues.put("trackingLink","https://www.delhivery.com/track/package/"+trackingNumber);
				//mapOfDispatchParametersAndValues.put("currentTrackDetails", (String)s.getField("Current_Track_Details__c"));
				String status=(String)s.getField("Status_Of_Dispatch__c");
				if(status!=null && status.equalsIgnoreCase("Returned Back"))
				{
					String placeToShip=(String)s.getField("Self_Learning_Material_For_Student__c");
					String studentNumber = (String)s.getField("Student_Number__c");
					String shippingAddress="";
					String customerAddress="";
					if(placeToShip.contains("Send to my shipping address"))
					{
							XmlObject to_student=(XmlObject)s.getField("To_Student__r");
							String customerPin = (String)to_student.getChild("Zip_Postal_Code_Shipping__c").getValue();
							String customerName = (String)to_student.getChild("Name").getValue();
							//String customerPhone = (String)to_student.getChild("PersonMobilePhone").getValue();
							//String customerProgram = (String)to_student.getChild("nmStudent_Program__c").getValue();
							String customerHouseLocality = (String)to_student.getChild("House_No_Name_Shipping_Account__c").getValue();
							String customerReceiverStreet = (String)to_student.getChild("Shipping_Street__c").getValue();
							String customerLandmark = (String)to_student.getChild("Nearest_LandMark_Shipping__c").getValue();
							String customerLocality = (String)to_student.getChild("Locality_Name_Shipping__c").getValue();
							String customerCity = (String)to_student.getChild("City_Shipping_Account__c").getValue();
							String customerState = (String)to_student.getChild("State_Province_Shipping__c").getValue();
							String customerCountry = (String)to_student.getChild("Country_Shipping__c").getValue();
							if(!StringUtils.isBlank(customerLandmark))
							{
								if((!StringUtils.isBlank(customerLocality)) && (!customerLocality.equalsIgnoreCase("NA")))
									customerAddress = customerHouseLocality + "," +customerLocality +"," + customerReceiverStreet +"," + customerLandmark;
								else
									customerAddress = customerHouseLocality + "," + customerReceiverStreet +"," + customerLandmark;
							}
							else 
							{	
								if((!StringUtils.isBlank(customerLocality)) && (!customerLocality.equalsIgnoreCase("NA")))
									customerAddress = customerHouseLocality + "," +customerLocality +"," + customerReceiverStreet;
								else
									customerAddress = customerHouseLocality + "," + customerReceiverStreet;
							}
		
							shippingAddress +=customerName+" ("+studentNumber+")"+", ";
							shippingAddress +="Student Number: "+studentNumber+", ";
							shippingAddress +=customerAddress+", ";
							shippingAddress +=customerCity+", ";
							shippingAddress +=customerState+", ";
							shippingAddress +=customerPin+", ";
							shippingAddress +=customerCountry;
							
					}
					else if(placeToShip.contains("Send to my Information Centre"))
					{
							XmlObject toCenterRecord = (XmlObject)s.getField("To_Centers__r");
							customerAddress = (String)toCenterRecord.getChild("Address__c").getValue();
							
							shippingAddress = customerAddress;
					}
					
					String sem=(String)s.getField("Semester__c");
					String program=(String)s.getField("Program__c");
					String orderType=(String)s.getField("Order_Type__c");
					if(orderType.equalsIgnoreCase("Student Order"))
					{
						String message="";
						if(placeToShip.contains("Send to my shipping address"))
						{
							message="Your study kit of Semester-"+sem+" for the program-"+program+", dispatched at your shipping address through Delhivery courier with Tracking number "+trackingNumber+" has returned to the University." +  
								"We request to kindly reconfirm your shipping address with the pin code and contact number at ngascelogistics@nmims.edu within 7 days for us to resend the study kit.";
						}
						else if(placeToShip.contains("Send to my Information Centre"))
						{
							message= (String)s.getField("Current_Track_Details__c");
						}
						mapOfDispatchParametersAndValues.put("currentTrackDetails", message);
					}
					else if(orderType.equalsIgnoreCase("Single Book"))
					{
						String message="";
						if(placeToShip.contains("Send to my shipping address"))
						{
							message="Your book of Semester-"+sem+" for the program-"+program+", dispatched at your shipping address through Delhivery courier with Tracking number "+trackingNumber+" has returned to the University." +  
								"We request to kindly reconfirm your shipping address with the pin code and contact number at ngascelogistics@nmims.edu within 7 days for us to resend the book.";
						}
						else if(placeToShip.contains("Send to my Information Centre"))
						{
							message = (String)s.getField("Current_Track_Details__c");
						}
						mapOfDispatchParametersAndValues.put("currentTrackDetails",message);
					}
					else
					{
						mapOfDispatchParametersAndValues.put("currentTrackDetails", (String)s.getField("Current_Track_Details__c"));
					}
				}
				else
				{
					mapOfDispatchParametersAndValues.put("currentTrackDetails", (String)s.getField("Current_Track_Details__c"));
				}
				mapOfDispatchParametersAndValues.put("semester",(String)s.getField("Semester__c"));
				if((String)s.getField("Delivery_Time__c") ==null){
					mapOfDispatchParametersAndValues.put("deliveryTime","Pending");
				}else{
					String deliveryDate=(String)s.getField("Delivery_Time__c");
					//deliveryDate=deliveryDate.substring(0,deliveryDate.indexOf('T'));
					mapOfDispatchParametersAndValues.put("deliveryTime",deliveryDate);
				}
				
				XmlObject fromCenterRecord = (XmlObject) s.getField("From_Centers__r");
				mapOfDispatchParametersAndValues.put("fromCenterName",(String)fromCenterRecord.getChild("Name").getValue());
			}
		}
		}catch(Exception e){
			e.printStackTrace();
			//init();
			//getMapOfDispatchParameterAndValues(sapid);
			
		}
		return mapOfDispatchParametersAndValues;
		
	}
	//Newly added to query the shipping address for student//
	public HashMap<String,String> getShippingAddressOfStudent(String studentNumber){
		System.out.println("Querying documents details from SFDC");

		/*
		 * ConnectorConfig config = new ConnectorConfig();
		 * config.setUsername(SFDC_USERID); config.setPassword(SFDC_PASSWORD_TOKEN);
		 * 
		 * System.out.println("SFDC_USERID = "+SFDC_USERID);
		 * System.out.println("SFDC_PASSWORD_TOKEN = "+SFDC_PASSWORD_TOKEN);
		 */

		
		QueryResult queryResults = new QueryResult();
		HashMap<String,String> mapOfShippingAddress = new HashMap<>();
		try {
			/*
			 * PartnerConnection connection = Connector.newConnection(config); // query for
			 * documents records for give SFDC record Id
			 * System.out.println("Auth EndPoint: "+config.getAuthEndpoint());
			 * System.out.println("Service EndPoint: "+config.getServiceEndpoint());
			 * System.out.println("Username: "+config.getUsername());
			 * System.out.println("SessionId: "+config.getSessionId());
			 */
			
			String soqlQuery = "SELECT Id, nm_StudentNo__c,Father_Name__c,Mother_Name__c,PersonMobilePhone,nm_SpouseName__c,PersonEmail,Shipping_Street__c,City_Shipping_Account__c,State_Province_Shipping__c,"
					+"Zip_Postal_Code_Shipping__c,Country_Shipping__c,Locality_Name_Shipping__c,"
					+"Nearest_LandMark_Shipping__c,House_No_Name_Shipping_Account__c"
					+" FROM Account"
					+ " where nm_StudentNo__c Like \'%" + studentNumber + "\'";
			System.out.println("SOQL QUERY-->"+soqlQuery);
			queryResults = connection.query(soqlQuery);
			
			if (queryResults.getSize() > 0) {
				for (SObject s: queryResults.getRecords()) {
					
					if((String)s.getField("Nearest_LandMark_Shipping__c")== null){
						mapOfShippingAddress.put("Nearest Landmark","NA");
					}else if((String)s.getField("Nearest_LandMark_Shipping__c")!=null){
						mapOfShippingAddress.put("Nearest Landmark", (String)s.getField("Nearest_LandMark_Shipping__c"));
					}if((String)s.getField("Shipping_Street__c")==null){
						mapOfShippingAddress.put("Street","NA");
					}else if((String)s.getField("Shipping_Street__c")!=null){
						mapOfShippingAddress.put("Street",(String)s.getField("Shipping_Street__c"));
					}if((String)s.getField("City_Shipping_Account__c")==null){
						mapOfShippingAddress.put("City","NA");
					}else if((String)s.getField("City_Shipping_Account__c")!=null){
						mapOfShippingAddress.put("City", (String)s.getField("City_Shipping_Account__c"));
					}if((String)s.getField("State_Province_Shipping__c")==null){
						mapOfShippingAddress.put("State","NA");
					}else if((String)s.getField("State_Province_Shipping__c")!=null){
						mapOfShippingAddress.put("State", (String)s.getField("State_Province_Shipping__c"));
					}if((String)s.getField("Zip_Postal_Code_Shipping__c")==null){
						mapOfShippingAddress.put("Postal Code","NA");
					}else if((String)s.getField("Zip_Postal_Code_Shipping__c")!=null){
						mapOfShippingAddress.put("Postal Code", (String)s.getField("Zip_Postal_Code_Shipping__c"));
					}
					if((String)s.getField("Country_Shipping__c")==null){
						mapOfShippingAddress.put("Country","NA");
					}else if((String)s.getField("Country_Shipping__c")!=null){
						mapOfShippingAddress.put("Country",(String)s.getField("Country_Shipping__c"));
					}
					if((String)s.getField("Locality_Name_Shipping__c")==null){
						mapOfShippingAddress.put("Locality Name","NA");
					}else if((String)s.getField("Locality_Name_Shipping__c")!=null){
						mapOfShippingAddress.put("Locality Name", (String)s.getField("Locality_Name_Shipping__c"));
					}if((String)s.getField("House_No_Name_Shipping_Account__c")==null){
						mapOfShippingAddress.put("House Name","NA");
					}else if((String)s.getField("House_No_Name_Shipping_Account__c")!=null){
						mapOfShippingAddress.put("House Name", (String)s.getField("House_No_Name_Shipping_Account__c"));
					}
					System.out.println("Map of shipping address-->"+mapOfShippingAddress);
					break;
				}
			}
			
	}catch(Exception e){
		//init();
		//getShippingAddressOfStudent(studentNumber);
		
	}
		return mapOfShippingAddress;
}
	
	
				public String updateSalesforceProfile(StudentStudentPortalBean student){
					System.out.println("Querying documents details from SFDC");
					String errorMessage ="";
					try {
						/*
						 * ConnectorConfig config = new ConnectorConfig();
						 * config.setUsername(SFDC_USERID); config.setPassword(SFDC_PASSWORD_TOKEN);
						 * 
						 * System.out.println("SFDC_USERID = "+SFDC_USERID);
						 * System.out.println("SFDC_PASSWORD_TOKEN = "+SFDC_PASSWORD_TOKEN);
						 */
						
						QueryResult queryResults = new QueryResult();
						
						
						/*
						 * PartnerConnection connection = Connector.newConnection(config); // query for
						 * documents records for give SFDC record Id
						 * System.out.println("Auth EndPoint: "+config.getAuthEndpoint());
						 * System.out.println("Service EndPoint: "+config.getServiceEndpoint());
						 * System.out.println("Username: "+config.getUsername());
						 * System.out.println("SessionId: "+config.getSessionId());
						 */
						
						String soqlQuery = "SELECT Id, nm_StudentNo__c FROM Account"
						+ " where nm_StudentNo__c Like \'%" + student.getSapid() + "\'";
						
						queryResults = connection.query(soqlQuery);
						String accountRecordId = "";
						
						if (queryResults.getSize() > 0) {
						for (SObject s: queryResults.getRecords()) {
						accountRecordId = (String)s.getField("Id");
						System.out.println("id for record --- > "+accountRecordId);
						break;
						}
						}
						
						SObject sObject = new SObject();
						sObject.setType("Account");
						sObject.setId(accountRecordId);
						sObject.setField("Shipping_Address_Choice__c","--None--");//This is done since this parameter is checked in account trigger to copy address.
//						sObject.setField("PersonEmail", student.getEmailId());			//Commented as student will update Email from Change Contact Details SR (cardNo: 2009)
//						sObject.setField("PersonMobilePhone", student.getMobile());		//Commented as student will update Mobile from Change Contact Details SR (cardNo: 2009)
						sObject.setField("Phone", student.getAltPhone());
//						sObject.setField("Father_First_Name__c", student.getFatherName());		//Commented as student will update FatherName from Change Contact Details SR (cardNo: 2004)
//						sObject.setField("Mother_First_Name__c", student.getMotherName());		//Commented as student will update MotherName from Change Contact Details SR (cardNo: 2004)
						
						sObject.setField("Shipping_Street__c", student.getStreet());
						sObject.setField("City_Shipping_Account__c", student.getCity());
						sObject.setField("State_Province_Shipping__c", student.getState());
						sObject.setField("Zip_Postal_Code_Shipping__c", student.getPin());
						sObject.setField("Country_Shipping__c", student.getCountry());
						sObject.setField("Locality_Name_Shipping__c", student.getLocality());
						//sObject.setField("Nearest_LandMark_Shipping__c", student.getLandMark());
						sObject.setField("House_No_Name_Shipping_Account__c", student.getHouseNoName());
						
						//sObject.setField("", altMobile);
						
						//end//
						SObject[] records = new SObject[1];
						records[0] = sObject;
						
						// update the records in Salesforce.com
						SaveResult[] saveResults = connection.update(records);
						if (saveResults[0].isSuccess()) {
						//System.out.println("Student Profile Record Updated successfully in Salesforce");
						}else{
						//System.out.println("Student Profile Error in updating record in Salesforce");
						errorMessage = "ERROR: ";
						Error[] errors = saveResults[0].getErrors();
						for (int j=0; j< errors.length; j++) {
						errorMessage += errors[j].getMessage() + " ";
						//System.out.println("ERROR updating record: " + errors[j].getMessage());
						}
						errorMessage += updateSalesforceErrorFlag(student.getSapid(),"Unable to update student profile");
						}
						
					}catch(ConnectionException  ce){
							init();
							//updateSalesforceProfile(student);
							errorMessage = "Unable to connect to salesforce for update student profile.";			
							}catch(Exception e){
							init();
							//updateSalesforceProfile(student);
							mailer.mailStackTrace("Confirm details : Unable to update stduent profile on sfdc", e);
							}
					
					return errorMessage;
				}
				public String updateStudentResultInSfdc(SalesforceStudentAccountBean sfdcBean) {

					String errorMessage = "";
					try {
						/*
						 * ConnectorConfig config = new ConnectorConfig();
						 * config.setUsername(SFDC_USERID); config.setPassword(SFDC_PASSWORD_TOKEN);
						 * 
						 * System.out.println("SFDC_USERID = " + SFDC_USERID);
						 * System.out.println("SFDC_PASSWORD_TOKEN = " + SFDC_PASSWORD_TOKEN);
						 */
						QueryResult queryResults = new QueryResult();

						//PartnerConnection connection = Connector.newConnection(config);

						SObject sObject = new SObject();

						sObject.setType("Account");
						sObject.setId(sfdcBean.getId());
						if(sfdcBean.getProgramType().equalsIgnoreCase("PG") ) {
							System.out.println("updating account...");
							System.out.println("sfdcBean"+sfdcBean);
							sObject.setField("Sem_1__c", sfdcBean.isSem1());
							sObject.setField("Sem_2__c", sfdcBean.isSem2());
							sObject.setField("Sem_3__c", sfdcBean.isSem3());
							sObject.setField("Sem_4__c", sfdcBean.isSem4());
						}
						sObject.setField("Pass_Out__c", sfdcBean.isPassout());
						// end//
						SObject[] records = new SObject[1];
						records[0] = sObject;

						// update the records in Salesforce.com
						SaveResult[] saveResults = connection.update(records);

						if (saveResults[0].isSuccess()) {
                            System.out.println("pass fail marked in sfdc");
						} else {
							errorMessage = "ERROR: ";
							Error[] errors = saveResults[0].getErrors();
							for (int j = 0; j < errors.length; j++) {
								errorMessage += errors[j].getMessage() + " ";
								System.out.println("ERROR updating record: " + errors[j].getMessage());
							}
							errorMessage += updateSalesforceErrorFlag(sfdcBean.getSapid(), "Unable to update student profile");
						}

					} catch (ConnectionException ce) {
						//init();
						//updateStudentResultInSfdc(sfdcBean);
						errorMessage = "Unable to connect to salesforce for update student profile.";
					} catch (Exception e) {
						//init();
						//updateStudentResultInSfdc(sfdcBean);
						System.out.println("Confirm details : Unable to update stduent profile on sfdc");
					}

					return errorMessage;
				}
				public void updateStudentResultInSfdcOppertunities(SalesforceStudentAccountBean sfdcBean) {

					/*
					 * if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){ System.out.
					 * println("Not Running syncSalesforceRegistrationData since it is not PROD ");
					 * return; }
					 */
								
								try {
									/*
									 * ConnectorConfig config = new ConnectorConfig();
									 * config.setUsername(SFDC_USERID); config.setPassword(SFDC_PASSWORD_TOKEN);
									 */
									
									QueryResult qResult = new QueryResult();
									
									//PartnerConnection connection = Connector.newConnection(config);
									String errorMessage = "";

									// fetch before update
									String soqlQuery ="select ID_opportunity__c from Opportunity where  Student_Number__c='"+sfdcBean.getSapid()+"' and nm_Year__c="+sfdcBean.getAcadYear()+" and nm_Session__c like  '"+sfdcBean.getAcadMonth()+"%' and nm_Semester__c='"+sfdcBean.getSem()+"'  " ;
                                    qResult = connection.query(soqlQuery);
									boolean done = false;
									SObject accountObject = new SObject();
									if (qResult.getSize() > 0) {

										System.out.println("updating "+qResult.getSize() + " Opportunity records...");
										while (!done) {
											SObject[] records = qResult.getRecords();
											for (int i = 0; i < records.length; ++i) {
												
												SObject s = (SObject) records[i];
												String opportunityid = (String)s.getField("ID_opportunity__c");
												accountObject.setType("Opportunity");
												accountObject.setId(opportunityid);
												accountObject.setField("Term_Cleared__c","Yes" );
												//update
												SObject[] records1 = new SObject[1];
												records1[0] = accountObject;

												// update the records in Salesforce.com
												SaveResult[] saveResults = connection.update(records1);

												if (saveResults[0].isSuccess()) {

												} else {
													errorMessage = "ERROR: ";
													Error[] errors = saveResults[0].getErrors();
													for (int j = 0; j < errors.length; j++) {
														errorMessage += errors[j].getMessage() + " ";
														System.out.println("ERROR updating record: " + errors[j].getMessage());
													}
													errorMessage += updateSalesforceErrorFlag(sfdcBean.getSapid(), "Unable to update student profile");
												}
												//end
												
											}
											if (qResult.isDone()) {
												done = true;
											}
										}
										
									}
								} catch (ConnectionException e) {
									//init();
									//updateStudentResultInSfdcOppertunities(sfdcBean);
									
								}
								
				}
				public ArrayList<SalesforceStudentAccountBean> getFailedStudentsFromsfdc(String ProgramType) { 
					System.out.println("fetching failed students from sfdc...");
					String errorMessage =""; 
					ArrayList<SalesforceStudentAccountBean> students = new ArrayList<SalesforceStudentAccountBean>();
					try {
						
						/*
						 * ConnectorConfig config = new ConnectorConfig(); config.setUsername(null);
						 * config.setPassword(null); System.out.println(""); System.out.println("");
						 */
						 
						QueryResult queryResults = new QueryResult();
						
						//PartnerConnection connection = Connector.newConnection(config);
						// query for documents records for give SFDC record Id
						
						String soqlQuery = "select id,Pass_Out__c,Sem_1__c,Sem_2__c,Sem_3__c,Sem_4__c ,nm_StudentNo__c " + 
								" from Account where  Pass_Out__c=false and nm_StudentNo__c !=null   ";        
						if(ProgramType.equalsIgnoreCase("MSC")) {
 							soqlQuery = "select id,Pass_Out__c,Sem_1__c,Sem_2__c,Sem_3__c,Sem_4__c,nm_StudentNo__c " +  
								" from Account where  Pass_Out__c=false and nm_StudentNo__c !=null and  (Program_Type__c like '%PD Term 4%' or Program_Type__c like '%PC Term 2%' or Program_Type__c like '%M.Sc. (AI)%' or Program_Type__c like '%M.Sc. (AI & ML Ops)%')  ";
 						}else if(ProgramType.equalsIgnoreCase("MBA (WX)")) {
							soqlQuery=soqlQuery+" and Program_Type__c ='MBA (WX)' ";
							
						}else if(ProgramType.equalsIgnoreCase("MBA (X)")) {
							soqlQuery=soqlQuery+" and Program_Type__c ='MBA (X)'";
							
						}else {
							soqlQuery=soqlQuery+" and Program_Type__c !='MBA (WX)' and Program_Type__c !='MBA (X)'";
							 if(ProgramType.equalsIgnoreCase("PDDM"))
									soqlQuery=soqlQuery+" and nm_Program__r.StudentZoneProgramCode__c in ("+pddmProgram+")";

							
						}
						System.out.println("soqlQuery"+soqlQuery);
						queryResults = connection.query(soqlQuery);
						boolean done = false;
						String sapid="";
						System.out.println("failed students count:"+queryResults.getSize()); 
						if (queryResults.getSize() > 0 ) {  
								while (!done) {
									SObject[] records =queryResults.getRecords();
									for (int i = 0; i < records.length; ++i) {
										SObject sObj =records[i];
										
										SalesforceStudentAccountBean sb= new SalesforceStudentAccountBean();
										
										sb.setSapid((String)sObj.getField("nm_StudentNo__c"));   
										
										sb.setId((String)sObj.getField("Id"));  
										
										boolean sem_1=(sObj.getField("Sem_1__c").equals("true") )?true:false;
										sb.setSem1(sem_1); 
										 
										boolean sem_2=(sObj.getField("Sem_2__c").equals("true"))?true:false;
										sb.setSem2(sem_2);
										
										boolean sem_3=(sObj.getField("Sem_3__c").equals("true"))?true:false;
										sb.setSem3(sem_3); 
										
										boolean sem_4=(sObj.getField("Sem_4__c").equals("true"))?true:false; 
										sb.setSem4(sem_4); 
										
										boolean pass_out=(sObj.getField("Pass_Out__c").equals("true"))?true:false; 
										sb.setPassout(pass_out);
										students.add( sb);
									}
									if (queryResults.isDone()) {
						               done = true;
						            } else {
						               queryResults = connection.queryMore(queryResults.getQueryLocator());
						            } 
								} 
						}else {
							System.out.println("No records found.");
					    }
						System.out.println("\nQuery successfully executed.");
					}catch(Exception e){
						    //sfc.createNewConnection();
						    //init();
						    //getFailedStudentsFromsfdc(ProgramType);
						    
							System.out.println("Confirm details : Unable to update stduent profile on sfdc");
							}
					return students;
				}
				public ArrayList<SalesforceStudentAccountBean> fetchSfdcOpportunitiesToUpdate(ArrayList<SalesforceStudentAccountBean> sfdcBeanList) {
					/*
					 * if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){ System.out.
					 * println("Not Running syncSalesforceRegistrationData since it is not PROD ");
					 * return; }
					 */
					/*
					 * ConnectorConfig config = new ConnectorConfig();
					 * config.setUsername(SFDC_USERID); config.setPassword(SFDC_PASSWORD_TOKEN);
					 * 
					 * System.out.println("SFDC_USERID = "+SFDC_USERID);
					 * System.out.println("SFDC_PASSWORD_TOKEN = "+SFDC_PASSWORD_TOKEN);
					 */
					int j=0;
					for(SalesforceStudentAccountBean sfdcBean : sfdcBeanList) {
						try {
							j++;
							logger.info("j: "+j);
							QueryResult qResult = new QueryResult();
							
							//connection = Connector.newConnection(config);
							
							String errorMessage = "";

							// fetch before update
							String soqlQuery ="select ID_opportunity__c from Opportunity where  Student_Number__c='"+sfdcBean.getSapid()+"' and nm_Year__c="+sfdcBean.getAcadYear()+" and nm_Session__c like  '"+sfdcBean.getAcadMonth()+"%' and nm_Semester__c='"+sfdcBean.getSem()+"'  " ;
                            qResult = connection.query(soqlQuery);
							boolean done = false;
							SObject accountObject = new SObject();
							if (qResult.getSize() > 0) {

								while (!done) {
									SObject[] records = qResult.getRecords();
									for (int i = 0; i < records.length; ++i) {
										
										SObject s = (SObject) records[i];
										sfdcBean.setOpportunityId((String)s.getField("ID_opportunity__c"));
									}
									if (qResult.isDone()) {
										done = true;
									}
								}
								
							}
						} catch (ConnectionException e) {
							//init();
							//fetchSfdcOpportunitiesToUpdate(sfdcBeanList);
							
						}
					}	
					return sfdcBeanList	;		
				}
				public ArrayList<String> updateRecordsBackInSalesforce(ArrayList<SObject> recordsList ,String sObjectType,int UPDATE_BATCH_SIZE)
				{
					/*
					 * ConnectorConfig config = new ConnectorConfig();
					 * config.setUsername(SFDC_USERID); config.setPassword(SFDC_PASSWORD_TOKEN);
					 */
					System.out.println("Updating records back in SFDC");
					/*
					 * System.out.println("SFDC_USERID = "+SFDC_USERID);
					 * System.out.println("SFDC_PASSWORD_TOKEN = "+SFDC_PASSWORD_TOKEN);
					 * 
					 */
					ArrayList<String> saleforceUpdationErrorList = new ArrayList<String>();
					try {
						//connection = Connector.newConnection(config);
						// query for documents records for give SFDC record Id
						/*System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
						System.out.println("Auth EndPoint: "+config.getAuthEndpoint());
						System.out.println("Service EndPoint: "+config.getServiceEndpoint());
						System.out.println("Username: "+config.getUsername());
						System.out.println("SessionId: "+config.getSessionId());
						System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");*/

						for(int i = 0; i < recordsList.size() ; i= i + UPDATE_BATCH_SIZE){
							int lastIndex =  (i + UPDATE_BATCH_SIZE) < recordsList.size() ? (i + UPDATE_BATCH_SIZE) : recordsList.size();
							System.out.println("\n***** start = "+ i + " end = "+lastIndex +" *********");
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
										
										System.out.println("ERROR updating record: " + records[k].getId() + " : "+ errors[j].getMessage() );
										errorMessage +=errors[j].getMessage();
									}
									saleforceUpdationErrorList.add(sObjectType+":"+records[k].getId()+":"+errorMessage);
								}   
							}
							
						}
						
					}catch(Exception e){
						//init();
						//updateRecordsBackInSalesforce(recordsList ,sObjectType,UPDATE_BATCH_SIZE);
					//	System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
						System.out.println("ERROR updating record In Salesforce : "+e.getMessage());
					}
					
					return saleforceUpdationErrorList;
				}
				
				public int checkPDDMPassSemForRereg(String sapid) throws Exception
				{
					StringBuffer sfdcQuery = new StringBuffer(" select count(id) sem_count from Account where  Sem_1__c = true and Sem_2__c = true and Sem_3__c = true and  nm_StudentNo__c = '"+sapid+"' ");
					QueryResult qResult = new QueryResult();
					qResult = connection.query(sfdcQuery.toString());
					int count = 0;
					if (qResult.getSize() > 0) {
				
						SObject[] records = qResult.getRecords();
						SObject s = (SObject) records[0];
						count = (Integer) s.getField("sem_count");		
					}
					return count;
				}

	/**
	 * Sync the particular student detail value with Salesforce. 
	 * The Id field of the student is retrieved from Salesforce by passing the Student Number (sapid).
	 * A Salesforce Object (SObject) is created with the retrieved Account Id and the detail to be updated.
	 * The Object is then updated in Salesforce and a response is returned.
	 * @param sapid - studentNo of the Student
	 * @param field - name of the field to be updated
	 * @param value - value to be updated
	 * @return Map containing the response from Salesforce
	 */
	public Map<String, String> updateSalesforceAccountField(Long sapid, String field, String value) {
		String salesforceField, responseCode, responseMessage;
		
		switch(field) {
			case "fatherName":
				salesforceField = ("PROD".equals(ENVIRONMENT)) ? "Father_First_Name__c" : "nm_FathersName__c";		//Fields different on prod and sandbox salesforce
				break;
			case "motherName":
				salesforceField = ("PROD".equals(ENVIRONMENT)) ? "Mother_First_Name__c" : "nm_MothersName__c";		//Fields different on prod and sandbox salesforce
				break;
			case "husbandName":
				salesforceField = "nm_SpouseName__c";
				break;
			case "emailId":
				salesforceField = "PersonEmail";
				break;
			case "mobile":
				salesforceField = "PersonMobilePhone";
				break;
			default:
				throw new IllegalArgumentException("Entered Field not supported in Salesforce!");
		}
		logger.info("Updating {} field in Salesforce with value: {}", salesforceField, value);
		
		try {
			String soql = "SELECT Id FROM Account " +
						  "WHERE nm_StudentNo__c LIKE '%" + sapid + "'";	
			
			QueryResult queryResults = connection.query(soql);
			String accountId = "";
			
			if (queryResults.getSize() > 0 && queryResults.getRecords().length > 0) {
				for (SObject record: queryResults.getRecords()) {
					accountId = (String) record.getField("Id");
					break;
				}
				logger.info("AccountId of Student {} retrieved from Salesforce: {}", sapid, accountId);
			}
			else {
				logger.error("No results returned while fetching the AccountId of Student {} in Salesforce", sapid);
				throw new RuntimeException("Unable to find AccountId of Student in Salesforce!");
			}
			
			SObject sObject = new SObject();
			sObject.setType("Account");
			sObject.setId(accountId);
			sObject.setField("Shipping_Address_Choice__c","--None--");		//This is done since this parameter is checked in account trigger to copy address.
			sObject.setField(salesforceField, value);
			
			SObject[] records = new SObject[] {sObject};
			SaveResult[] saveResults = connection.update(records);		// update the records in Salesforce
			if (saveResults[0].isSuccess()) {			//onSuccess creating a success response
				responseCode = HttpStatus.OK.toString();
				responseMessage = saveResults[0].getId();
				logger.info("{} field of Student {} with AccountId: {} updated successfully on Salesforce", salesforceField, sapid, responseMessage);
			}
			else{			//onError creating an error response
				StringBuilder errorMessage = new StringBuilder();
				responseCode = HttpStatus.BAD_REQUEST.toString();
				
				Error[] errors = saveResults[0].getErrors();
				for (int i = 0; i < errors.length; i++) {
					responseCode = errors[i].getStatusCode().toString();
					errorMessage.append(errors[i].getMessage() + "\n");
				}
				
				responseMessage = errorMessage.toString();
				logger.error("Error occured while updating {} field of Student: {} in Salesforce, returned Error Code: {}, Error Message: {}", salesforceField, sapid, responseCode, responseMessage);
			}
			
			Map<String, String> responseMap = new HashMap<>();
			responseMap.put("responseCode", responseCode);
			responseMap.put("responseMessage", responseMessage);
			return responseMap;
		}
		catch(ConnectionException ex) {
//			ex.printStackTrace();
			init();
			logger.error("Connection Failed while trying to update field in Student Account on Salesforce, Exception thrown: {}", ex.toString());
			mailer.mailStackTrace("Change Details ServiceRequest: Unable to update Student Account on SFDC", ex);
			throw new RuntimeException("Connection Failed while trying to connect to Salesforce for update() call.");
		}
		catch(Exception ex) {
//			ex.printStackTrace();
			logger.error("Exception occured while trying to update field in Student Account on Salesforce, Exception thrown: {}", ex.toString());
			mailer.mailStackTrace("Change Details ServiceRequest: Unable to update Student Account on SFDC", ex);
			throw new RuntimeException("Error occured while trying to update field of Student Account on Salesforce");
		}
	}
	

	/**
	 * Get all opportunities for a given student number. 			
	 * @param studentNumber - Student number[sapId] for which opportunities has to fetch. 
	 * @return QueryResult - returns all opportunities of a student.
	 * @throws StudentNotFoundException If the student opportunities not found on salesforce.
	 * @throws Exception If establishing the connection fails with salesforce . 
	 */
	public List<StudentOpportunity> getStudentOpportunities(String studentNumber) throws StudentNotFoundException,Exception {

		//Create QueryResult object.
		QueryResult queryResults = new QueryResult();
		//Create empty student opportunities list.
		List<StudentOpportunity> stdOpportunitesList = new ArrayList<>();
		
		String sql = "SELECT Student_Number__c,StageName,nm_Semester__c,Term_Cleared__c FROM Opportunity WHERE Student_Number__c='" + studentNumber + "'";		

		queryResults = connection.query(sql);
		
		//Throw StudentNotFoundException if student opportunities not found on salesforce
		if (queryResults == null || queryResults.getSize() <= 0)
			throw new StudentNotFoundException("Student opportunities not found on salesforce for:" + studentNumber);

		//Process Opportunities  
		for(SObject opptDetls: queryResults.getRecords()) {			
			//Create StudentOpportunity object.
			StudentOpportunity stdOpportunity = new StudentOpportunity();
			
			//Get opportunity details from SObject and set to StudentOpportunity
			stdOpportunity.setSapId((String)opptDetls.getField("Student_Number__c"));
			stdOpportunity.setTermCleared((String)opptDetls.getField("Term_Cleared__c"));
			stdOpportunity.setSemester(Integer.parseInt((String)opptDetls.getField("nm_Semester__c")));
			stdOpportunity.setStageName((String)opptDetls.getField("StageName"));
			
			//Add in student opportunities list
			stdOpportunitesList.add(stdOpportunity);
		}
	
		//Return all opportunities details of a student.
		return stdOpportunitesList;
}

	public HashMap<String, String> updateRecordsToSDFC(SObject object) throws Exception{
		HashMap<String, String> returnMap = new HashMap<String, String>();
		SObject[] records = {object};

		SaveResult[] saveResults = connection.update(records);
		// check the returned results for any errors
		for (int i=0; i< saveResults.length; i++) {
			if (saveResults[i].isSuccess()) {
				returnMap.put("status","success");
				returnMap.put("message", "Data Uploaded Successfully on SFDC");
			}else {
				String errorMessage = "ERROR: ";
				Error[] errors = saveResults[i].getErrors();
				for (int j=0; j< errors.length; j++) {
					errorMessage += errors[j].getMessage() + " ";
					System.out.println("ERROR updating record: " + errors[j].getMessage());
				}
				returnMap.put("status","error");
				returnMap.put("message", errorMessage);
			}   
		}
		return returnMap;
	}
	public Map<String, String> getSapIdSemMap(String month,String year) throws StudentNotFoundException,Exception {

		//Create QueryResult object.
		QueryResult queryResults = new QueryResult();
		
		boolean done = false;
		//Create empty student opportunities list.
		Set<StudentOpportunity> stdOpportunitesList = new HashSet<>();
		//System.out.println("month:year:"+month+year);
		String sql = "SELECT Student_Number__c,nm_Semester__c,stageName,CreatedDate FROM Opportunity WHERE drive2__c='" + month+""+year + "' and account.Program_Type__c='MBA (WX)' and stageName='Closed Won'";		
		queryResults = connection.query(sql);

		//Process Opportunities 
		if (queryResults.getSize() > 0) {
			while (!done) {
				for(SObject opptDetls: queryResults.getRecords()) {			
					//Create StudentOpportunity object.
					StudentOpportunity stdOpportunity = new StudentOpportunity();
					//Get opportunity details from SObject and set to StudentOpportunity
					stdOpportunity.setSapId((String)opptDetls.getField("Student_Number__c"));
					stdOpportunity.setSemester(Integer.parseInt((String)opptDetls.getField("nm_Semester__c")));
					Object createdDateValue = opptDetls.getField("CreatedDate");
					if (createdDateValue instanceof String) {
					    try {
					    	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
					        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
					        Date createdDate = dateFormat.parse((String) createdDateValue);
					        stdOpportunity.setCreatedDate(createdDate);
					    } catch (Exception e) {
					        // Handle the parsing exception here
					        e.printStackTrace();
					    }
					}

					
					//Add in student opportunities list
					stdOpportunitesList.add(stdOpportunity);
				}
				if (queryResults.isDone()) {
					done = true;
				} else {
					logger.info("Querying more.....");
					queryResults = connection.queryMore(queryResults.getQueryLocator());
				}
			}
		}

		stdOpportunitesList.removeIf(student -> student.getSapId() == null || student.getSemester() == null);
		
		List<String> sapIds = stdOpportunitesList.stream().map(bean->bean.getSapId()).distinct().collect(Collectors.toList());

	    // Extract SAP IDs and their corresponding semester values into the map
	    Map<String, String> sapIdSemMap = new HashMap<>();
	    sapIds.stream().forEach(sapId->{
	    	List<StudentOpportunity> studentList = stdOpportunitesList.stream().filter(student->student.getSapId().equalsIgnoreCase(sapId)).collect(Collectors.toList());
	    	Collections.sort(studentList,Comparator.comparing(StudentOpportunity::getCreatedDate));
	    	if(studentList.size()>0) {
	    		sapIdSemMap.put(sapId,String.valueOf(studentList.get(0).getSemester()));
	    	}
	    });

	    return sapIdSemMap;
}
}