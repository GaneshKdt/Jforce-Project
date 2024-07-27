package com.nmims.helpers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.nmims.beans.Specialisation;
import com.nmims.beans.StudentExamBean;
import com.nmims.util.StudentProfileUtils;
import com.sforce.soap.partner.Error;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.SaveResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.bind.XmlObject;

@Component
public class SalesforceHelper {
	public SalesforceHelper(SFConnection sf) {
		this.connection = sf.getConnection();
	}

	@Value("${SFDC_USERID}")
	private String SFDC_USERID;

	@Value("${SFDC_PASSWORD_TOKEN}")
	private String SFDC_PASSWORD_TOKEN;

	private PartnerConnection connection;
	
	private static final Logger logger = LoggerFactory.getLogger(SalesforceHelper.class);

	@Deprecated
	public String updateSalesforceProfile(StudentExamBean student, String userId) {		//use updateSalesforceAccountFields() instead
		 
		String errorMessage = "";
		QueryResult queryResults = new QueryResult();

		try {  
			String soqlQuery = "SELECT Id, nm_StudentNo__c FROM Account" + " where nm_StudentNo__c = '"
					+ student.getSapid() + "' ";

			queryResults = connection.query(soqlQuery);
			String accountRecordId = "";

			if (queryResults.getSize() > 0) {
				for (SObject s : queryResults.getRecords()) {
					accountRecordId = (String) s.getField("Id");
					break;
				}
				SObject sObject = new SObject();
				sObject.setType("Account");
				sObject.setId(accountRecordId);
				sObject.setField("PersonEmail", student.getEmailId());
				sObject.setField("PersonMobilePhone", student.getMobile());
				sObject.setField("nm_FathersName__c", student.getFatherName());
				sObject.setField("nm_MothersName__c", student.getMotherName());

				// end//
				SObject[] records = new SObject[1];
				records[0] = sObject;

				// update the records in Salesforce.com
				SaveResult[] saveResults = connection.update(records);
				if (saveResults[0].isSuccess()) {
				} else {
					errorMessage = "ERROR: ";
					Error[] errors = saveResults[0].getErrors();
					for (int j = 0; j < errors.length; j++) {
						errorMessage += errors[j].getMessage() + " ";
					}
				}
			}
		} catch (Exception e) {
			
		}

		return errorMessage;
	}

	public ArrayList<StudentExamBean> listOfPaymentsMade(String sapid) {
		 
		QueryResult queryResults = new QueryResult();
		ArrayList<StudentExamBean> listOfPaymentsMade = new ArrayList<StudentExamBean>();
		try {
			 
			String soqlQuery = "select id, nm_OpportunityNew__r.Account.nm_StudentNo__c,nm_OpportunityNew__r.StageName, nm_OpportunityNew__r.nm_Session__c, nm_OpportunityNew__r.nm_Year__c "
					+ ",nm_OpportunityNew__r.nm_Semester__c " + "from nm_Payment__c "
					+ " where nm_OpportunityNew__r.Account.nm_StudentNo__c = '" + sapid
					+ "' and nm_PaymentType__c = 'Admission' and ( nm_PaymentStatus__c  ='Payment Made' or nm_PaymentStatus__c = 'Payment Approved' ) order by nm_OpportunityNew__r.nm_Semester__c";
			queryResults = connection.query(soqlQuery);
			if (queryResults.getSize() > 0) {
				for (SObject s : queryResults.getRecords()) {
					StudentExamBean student = new StudentExamBean();
					XmlObject opportunityField = (XmlObject) s.getField("nm_OpportunityNew__r");
					student.setAcadYear((String) opportunityField.getChild("nm_Year__c").getValue());
					student.setAcadMonth((String) opportunityField.getChild("nm_Session__c").getValue());
					student.setSem((String) opportunityField.getChild("nm_Semester__c").getValue());
					String stageName = String.valueOf(opportunityField.getChild("StageName").getValue());
					student.setRegistered(stageName);
					if (!"Closed Won".equalsIgnoreCase(stageName)) {
						student.setRegistered("Payment Received");
					}

					XmlObject accountField = (XmlObject) opportunityField.getChild("Account");
					student.setSapid((String) accountField.getField("nm_StudentNo__c"));
					listOfPaymentsMade.add(student);
				}
			}
			for (StudentExamBean b : listOfPaymentsMade) {
			}

		} catch (Exception e) {
			
		}

		return listOfPaymentsMade;

	}

	public String updateSpecilisationDetails(Specialisation specialisation) {

		String errorMessage = "";
		try {
			specialisation.setSfdcProgram1(specialisation.getSpecialisation1());
			specialisation.setSfdcProgram2(specialisation.getSpecialisation2());

			// Replace Portal id with SFDC Specialisation Name
			specialisation.setSfdcProgram1(specialisation.getSfdcProgram1().replace("9", "MBA(WX) - M"));
			specialisation.setSfdcProgram1(specialisation.getSfdcProgram1().replace("10", "MBA(WX) - LS"));
			specialisation.setSfdcProgram1(specialisation.getSfdcProgram1().replace("11", "MBA(WX) - OSC"));
			specialisation.setSfdcProgram1(specialisation.getSfdcProgram1().replace("12", "MBA(WX) - AF"));
			specialisation.setSfdcProgram1(specialisation.getSfdcProgram1().replace("13", "MBA(WX) - DM"));
			
			if("Dual Specialization".equals(specialisation.getSpecializationType())) {
				specialisation.setSfdcProgram2(specialisation.getSfdcProgram2().replace("9", "MBA(WX) - M"));
				specialisation.setSfdcProgram2(specialisation.getSfdcProgram2().replace("10", "MBA(WX) - LS"));
				specialisation.setSfdcProgram2(specialisation.getSfdcProgram2().replace("11", "MBA(WX) - OSC"));
				specialisation.setSfdcProgram2(specialisation.getSfdcProgram2().replace("12", "MBA(WX) - AF"));
				specialisation.setSfdcProgram1(specialisation.getSfdcProgram2().replace("13", "MBA(WX) - DM"));
			}

			QueryResult specialisationList = new QueryResult();

			String soqlQuery2 = "SELECT id,Name FROM nm_Program__c WHERE nmIsActive__c=TRUE AND Specialisation__c='MBA (WX)'";
			specialisationList = connection.query(soqlQuery2);

			HashMap<String, String> specialisationMapping = new HashMap<String, String>();

			if (specialisationList.getSize() > 0) {
				for (SObject s : specialisationList.getRecords()) {
					String sfdcProgramId = (String) s.getField("Id");
					String sfdcProgramName = (String) s.getField("Name");
					specialisationMapping.put(sfdcProgramName, sfdcProgramId);
				}
			} 
			QueryResult queryResults = new QueryResult();
 
			String sutdentNo = specialisation.getSapid();

			String soqlQuery = "SELECT Id, nm_StudentNo__c FROM Account" + " where nm_StudentNo__c Like \'%" + sutdentNo
					+ "\'";

			queryResults = connection.query(soqlQuery);
			String accountRecordId = "";

			if (queryResults.getSize() > 0) {
				for (SObject s : queryResults.getRecords()) {
					accountRecordId = (String) s.getField("Id");
					break;
				}
			}

			SObject sObject = new SObject();
			sObject.setType("Account");
			sObject.setId(accountRecordId);

			// Set Updated Specialization Details to update in SFDC
			sObject.setField("Specialization_Type__c", specialisation.getSpecializationType());
			sObject.setField("nm_Program__c", specialisationMapping.get(specialisation.getSfdcProgram1()));

			if (specialisation.getSpecializationType().equalsIgnoreCase("Single Specialisation")) {
				sObject.setFieldsToNull(new String[] { "nm_Program2__c" });
			} else {
				sObject.setField("nm_Program2__c", specialisationMapping.get(specialisation.getSfdcProgram2()));
			}

			// End//
			SObject[] records = new SObject[1];
			records[0] = sObject;

			// update the records in Salesforce.com
			SaveResult[] saveResults = connection.update(records);
			if (saveResults[0].isSuccess()) {
				// Salesforce");
			} else {
				errorMessage = "ERROR: ";
				Error[] errors = saveResults[0].getErrors();
				for (int j = 0; j < errors.length; j++) {
					errorMessage += errors[j].getMessage() + " ";
				}
			}

		} catch (Exception e) {
			errorMessage = "ERROR: ";
			e.printStackTrace();
			
		}

		return errorMessage;
	}
	
	/**
	 * Sync student details with Salesforce. 
	 * The Id field of the student is retrieved from Salesforce by passing the Student Number (sapid).
	 * An Object is created with the retrieved Account Id and other details from the passed bean.
	 * The Object is then updated in Salesforce and a response is returned.
	 * @param student - bean containing the student details
	 * @return response Map
	 */
	public Map<String, String> updateSalesforceAccountFields(StudentExamBean student) {
		try {
			String soql = "SELECT Id FROM Account " +
						  "WHERE nm_StudentNo__c LIKE '%" + student.getSapid() + "'";	
			
			QueryResult queryResults = connection.query(soql);
			String accountId = "";
			
			if(queryResults.getSize() > 0 && queryResults.getRecords().length > 0) {
				for(SObject record: queryResults.getRecords()) {
					accountId = (String) record.getField("Id");
					break;
				}
				logger.info("AccountId of Student {} retrieved from Salesforce: {}", student.getSapid(), accountId);
			}
			else {
				logger.error("No results returned while fetching the AccountId of Student {} in Salesforce", student.getSapid());
				throw new RuntimeException("Unable to find AccountId of Student in Salesforce!");
			}
			
			SObject sObject = new SObject();
			sObject.setType("Account");
			sObject.setId(accountId);
			sObject.setField("Shipping_Address_Choice__c","--None--");		//This is done since this parameter is checked in account trigger to copy address.
			sObject.setField("FirstName", student.getFirstName());
			sObject.setField("nm_MiddleName__c", student.getMiddleName());
			sObject.setField("LastName", StudentProfileUtils.getValidLdapLastName(student.getLastName()));				//Salesforce acceptable last name
			sObject.setField("Father_First_Name__c", student.getFatherName());
//			sObject.setField("nm_FathersName__c", student.getFatherName());			//For Father firstName Salesforce Sandbox testing, comment for production
			sObject.setField("Mother_First_Name__c", student.getMotherName());
//			sObject.setField("nm_MothersName__c", student.getMotherName());			//For Mother firstName Salesforce Sandbox testing, comment for production
			sObject.setField("nm_SpouseName__c", student.getHusbandName());
			sObject.setField("nm_Gender__c", student.getGender());
			sObject.setField("nm_DateOfBirth__c", createSalesforceDate(student.getDob()));
//			sObject.setField("Age__c", student.getAge());			//Age field calculated via a Formula (using the Date Of Birth field)
			sObject.setField("PersonEmail", student.getEmailId());
			sObject.setField("PersonMobilePhone", student.getMobile());
			sObject.setField("Phone", student.getAltPhone());
			sObject.setField("House_No_Name_Shipping_Account__c", student.getHouseNoName());
			sObject.setField("Shipping_Street__c", student.getStreet());
			sObject.setField("Nearest_LandMark_Shipping__c", student.getLandMark());
			sObject.setField("Locality_Name_Shipping__c", student.getLocality());
			sObject.setField("Zip_Postal_Code_Shipping__c", student.getPin());
			sObject.setField("City_Shipping_Account__c", student.getCity());
			sObject.setField("State_Province_Shipping__c", student.getState());
			sObject.setField("Country_Shipping__c", student.getCountry());
			sObject.setField("Pass_Out__c", student.getProgramCleared().equals("Y") ? true : false);
//			sObject.setField("Highest_Qualification__c", student.getHighestQualification());		//Highest Qualification stored using a formula containing various fields
			sObject.setField("Industry", student.getIndustry());
			sObject.setField("nm_Designation__c", student.getDesignation());
			sObject.setFieldsToNull(checkNullFieldsForSalesforceUpdate(student));		//updates fields as null in Salesforce
			
			SObject[] records = new SObject[] {sObject};
			String responseCode, responseMessage;			//variables created to store the response information
			
			SaveResult[] saveResults = connection.update(records);		// update the records in Salesforce
			if (saveResults[0].isSuccess()) {		//onSuccess creating a success response
				responseCode = HttpStatus.OK.toString();
				responseMessage = saveResults[0].getId();
				logger.info("Details of Student {} with AccountId: {} updated successfully on Salesforce", student.getSapid(), responseMessage);
			}
			else {		//onError creating an error response
				StringBuilder errorMessage = new StringBuilder();
				responseCode = HttpStatus.BAD_REQUEST.toString();
				
				Error[] errors = saveResults[0].getErrors();
				for (int i = 0; i < errors.length; i++) {
					responseCode = errors[i].getStatusCode().toString();
					errorMessage.append(errors[i].getMessage() + "\n");
				}
				
				responseMessage = errorMessage.toString();
				logger.error("Error occured while updating Account of Student: {} on Salesforce, returned Error Code: {}, Error Message: {}", student.getSapid(), responseCode, responseMessage);
			}
			
			Map<String, String> responseMap = new HashMap<>();
			responseMap.put("responseCode", responseCode);
			responseMap.put("responseMessage", responseMessage);
			return responseMap;
		}
		catch(ConnectionException ex) {
//			ex.printStackTrace();
			logger.error("Connection Failed while trying to update Student Account on Salesforce, Exception thrown: {}", ex.toString());
			throw new RuntimeException("Connection Failed while trying to connect to Salesforce for update() call.");			
		}
		catch(Exception ex) {
//			ex.printStackTrace();
			logger.error("Exception occured while trying to update Student Account on Salesforce, Exception thrown: {}", ex.toString());
			throw new RuntimeException("Error occured while trying to update field of Student Account on Salesforce");
		}
	}

	/**
	 * Date is formatted and returned as a Calendar instance with the required Salesforce format and UTC TimeZone.
	 * @param date - Date as a String
	 * @return formatted Date as a Calendar instance
	 * @throws ParseException 
	 */
	private Calendar createSalesforceDate(String date) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));		//Salesforce defaults to UTC timeZone
		Date localDate = formatter.parse(date);
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(localDate);
		return calendar;
	}
	
	/**
	 * Check if the particular fields are null and add their corresponding Salesforce field names into an array.
	 * @param studentBean - bean containing the student details
	 * @return String array containing the salesforce field names which have null as their value 
	 */
	private String[] checkNullFieldsForSalesforceUpdate(StudentExamBean studentBean) {
		List<String> nullFieldSalesforceNameList = new ArrayList<>();
		
		if(Objects.isNull(studentBean.getMiddleName()))
			nullFieldSalesforceNameList.add("nm_MiddleName__c");
		
		if(Objects.isNull(studentBean.getHusbandName()))
			nullFieldSalesforceNameList.add("nm_SpouseName__c");
		
		if(Objects.isNull(studentBean.getAltPhone()))
			nullFieldSalesforceNameList.add("Phone");
		
		if(Objects.isNull(studentBean.getLandMark()))
			nullFieldSalesforceNameList.add("Nearest_LandMark_Shipping__c");
		
		if(Objects.isNull(studentBean.getIndustry()))
			nullFieldSalesforceNameList.add("Industry");
		
		if(Objects.isNull(studentBean.getDesignation()))
			nullFieldSalesforceNameList.add("nm_Designation__c");
		
		return nullFieldSalesforceNameList.toArray(new String[0]);
	}
}
