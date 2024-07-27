package com.nmims.helpers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.nmims.beans.SessionDayTimeBean;
import com.nmims.beans.SessionQueryAnswerCareerservicesBean;
import com.nmims.beans.StudentCareerservicesBean;
import com.sforce.soap.partner.Connector;
import com.sforce.soap.partner.Error;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.SaveResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectorConfig;


@Component
public class SalesforceHelper_Sessions {

	@Value( "${SFDC_USERID}" )
	private String SFDC_USERID;

	@Value( "${SFDC_PASSWORD_TOKEN}" )
	private String SFDC_PASSWORD_TOKEN;

	private static final Logger logger = LoggerFactory.getLogger(SalesforceHelper_Sessions.class);
 
	public SessionQueryAnswerCareerservicesBean createCaseInSalesforce(StudentCareerservicesBean student,SessionQueryAnswerCareerservicesBean sessionQuery){

		ConnectorConfig config = new ConnectorConfig();
		config.setUsername(SFDC_USERID);
		config.setPassword(SFDC_PASSWORD_TOKEN);

		String errorMessage ="";
		QueryResult queryResults = new QueryResult();

		try {
			PartnerConnection connection = Connector.newConnection(config);
			// query for documents records for give SFDC record Id
			
			String soqlQuery = "SELECT Id, nm_StudentNo__c FROM Account"
					+ " where nm_StudentNo__c = '" + student.getSapid() + "' ";

			queryResults = connection.query(soqlQuery);
			String accountRecordId = "";
			
			String Description = "Session Name : "+sessionQuery.getSessionName()+"\n"
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
				}else{
					errorMessage = "ERROR: ";
					Error[] errors = saveResults[0].getErrors();
					for (int j=0; j< errors.length; j++) {
						errorMessage += errors[j].getMessage() + " ";
					}
				}
		  }
			
		}catch(Exception e){
			logger.info("exception : "+e.getMessage());
		}

		sessionQuery.setErrorMessage(errorMessage);
		return sessionQuery;
	}
	
	
	
	
	public SessionQueryAnswerCareerservicesBean createCaseInSalesforce(StudentCareerservicesBean student,SessionQueryAnswerCareerservicesBean sessionQuery,SessionDayTimeBean session){

		ConnectorConfig config = new ConnectorConfig();
		config.setUsername(SFDC_USERID);
		config.setPassword(SFDC_PASSWORD_TOKEN);

		String errorMessage ="";
		QueryResult queryResults = new QueryResult();

		try {
			PartnerConnection connection = Connector.newConnection(config);
			// query for documents records for give SFDC record Id
			
			String soqlQuery = "SELECT Id, nm_StudentNo__c FROM Account"
					+ " where nm_StudentNo__c = '" + student.getSapid() + "' ";

			queryResults = connection.query(soqlQuery);
			String accountRecordId = "";
			
			String Description = "Session Name : "+session.getSessionName()+"\n"
								+"Date :"+session.getDate()+"\n"
								+"StartTime : "+session.getStartTime()+"\n"
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
				}else{
					errorMessage = "ERROR: ";
					Error[] errors = saveResults[0].getErrors();
					for (int j=0; j< errors.length; j++) {
						errorMessage += errors[j].getMessage() + " ";
					}
				}
		  }
			
		}catch(Exception e){
			logger.info("exception : "+e.getMessage());
		}

		sessionQuery.setErrorMessage(errorMessage);
		return sessionQuery;
	}
	
}