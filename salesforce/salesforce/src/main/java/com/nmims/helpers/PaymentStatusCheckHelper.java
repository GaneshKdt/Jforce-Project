package com.nmims.helpers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletConfigAware;

import com.nmims.beans.PaymentRecordBean;
import com.nmims.daos.StudentZoneDao;
import com.sforce.soap.partner.Connector;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectorConfig;

@Component
public class PaymentStatusCheckHelper implements ApplicationContextAware, ServletConfigAware{

	private static ApplicationContext act = null;
	private static ServletConfig sc = null;
	private PartnerConnection connection;

	@Value( "${SERVER_PATH}" )
	private String SERVER_PATH;
	
	@Value( "${SFDC_USERID}" )
	private String SFDC_USERID;

	@Value( "${SFDC_PASSWORD_TOKEN}" )
	private String SFDC_PASSWORD_TOKEN;
	
	@Value( "${ENVIRONMENT}" )
	private String ENVIRONMENT;

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
	
	public Map<String, PaymentRecordBean> getStatusForPaymentInitiaionIds(List<String> paymentInitiaionIds){
		Map<String, PaymentRecordBean> statusList = new HashMap<String, PaymentRecordBean>();
		for(String paymentInitiaionId : paymentInitiaionIds) {
			statusList.put(paymentInitiaionId, getStatusForPaymentInitiationId(paymentInitiaionId));
		}
		return statusList;
	}

	public PaymentRecordBean getStatusForPaymentInitiaionId(String paymentInitiaionId){
		return getStatusForPaymentInitiationId(paymentInitiaionId);
	}
	
	
	private PaymentRecordBean getStatusForPaymentInitiationId(String paymentInitiaionId){
		
		PaymentRecordBean latestInitiation = getLatestInitiation(paymentInitiaionId);

		return latestInitiation;
	}

	private PaymentRecordBean getLatestInitiation(String paymentInitiaionId) {
		

		PaymentRecordBean paymentRecord = new PaymentRecordBean();
		
		String soqlQuery =""
				+ "SELECT "
					+ "nm_Merchant_Track_Id__c, "	//Merchant track id
					+ "nm_PaymentType__c, "			//Payment Type(Career Service)
					+ "nm_PaymentStatus__c, "
					+ "CareerServiceProgram__c, "		//Payment Status : 
					+ "CreatedDate "
				+ "FROM "
				+ "nm_Payment_Initiated__c "
				+ "WHERE "
					+ "nm_PaymentType__c = 'Career Service' "
				+ "AND "
					+ "CareerServiceProgram__c = '" + paymentInitiaionId + "' "
//					+ "nm_Merchant_Track_Id__c = '" + merchantId + "' "
				+ "ORDER BY "
				+ "CreatedDate DESC "
				+ "LIMIT 1";
		paymentRecord = getPaymentRecordsFromQuery(soqlQuery);
		
		return paymentRecord;
	}

	private PaymentRecordBean getPaymentRecordsFromQuery(String soqlQuery) {
		try{
			//Create SFDC Query connector
			ConnectorConfig config = new ConnectorConfig();
			config.setUsername(SFDC_USERID);
			config.setPassword(SFDC_PASSWORD_TOKEN);
			
			//Results
			QueryResult qResult = new QueryResult();
			connection = Connector.newConnection(config);
			
			//Query for object
			qResult = connection.query(soqlQuery);
			if (qResult.getSize() > 0) {
				SObject[] records = qResult.getRecords();

				SObject resultFields = (SObject) records[0];
				
				//Map fields
				PaymentRecordBean paymentRecord = new PaymentRecordBean();
//				paymentRecord.setSapid((String)resultFields.getField("Student_number__c"));
				paymentRecord.setPaymentType((String)resultFields.getField("nm_PaymentType__c"));
				paymentRecord.setStatus((String)resultFields.getField("nm_PaymentStatus__c"));
				paymentRecord.setMerchantTrackId((String)resultFields.getField("nm_Merchant_Track_Id__c"));
				paymentRecord.setPaymentInitiaionId((String)resultFields.getField("CareerServiceProgram__c"));
				paymentRecord.setDate((String)resultFields.getField("CreatedDate"));
			
				return paymentRecord;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
