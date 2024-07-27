package com.nmims.listeners;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.ServletConfigAware;

import com.nmims.beans.RevenueReportField;
import com.nmims.beans.SchedulerApisBean;
import com.nmims.beans.StatusBean;
import com.nmims.daos.StudentZoneDao;
import com.nmims.helpers.MailSender;
import com.nmims.helpers.RevenueReportHelper;
import com.nmims.helpers.SFConnection;
import com.sforce.soap.partner.Error;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.SaveResult;
import com.sforce.soap.partner.fault.ApiFault;
import com.sforce.soap.partner.fault.LoginFault;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;

@Component
@Controller
public class ReportsSyncScheduler implements ApplicationContextAware, ServletConfigAware{
	private static ApplicationContext act = null;
	private static ServletConfig sc = null;
	private PartnerConnection connection;
	public ReportsSyncScheduler(SFConnection sf) {
		this.connection = sf.getConnection();
	}
	public void init(){
		SFConnection sf= new SFConnection(SFDC_USERID,SFDC_PASSWORD_TOKEN);
		this.connection = sf.getConnection();
	}
	@Value("${SFDC_API_MAX_RETRY_COUNT}")
	private String SFDC_API_MAX_RETRY_COUNT;
	
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
	
	@Value( "${ENVIRONMENT}" )
	private String ENVIRONMENT;
	
	@Value("${SERVER}")
	private String SERVER;
	
	@Autowired
	private RevenueReportHelper revenueReportHelper;
	@Autowired
	SFConnection sfc;
	@Autowired
	MailSender mailer;
	
	@Autowired
	StudentZoneDao studentZoneDAO;

	@Override
	public void setServletConfig(ServletConfig sc) {
		this.sc = sc;
	} 
	private static final Logger logger = LoggerFactory.getLogger(ReportsSyncScheduler.class); 
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.act = applicationContext;
	}
	public static ApplicationContext getApplicationContext() {
		return act;
	}
	@RequestMapping(value = "/syncRevenueForPreviousDay", method = {RequestMethod.GET, RequestMethod.POST})
	public void callSyncRevenueForPreviousDay( HttpServletRequest request,HttpServletResponse response) throws IOException {
					 
 		syncRevenueForPreviousDay();
	}
//					   seconds minute hour date/s month_name/s year/s
	@Scheduled(cron = "0 0 2,3,4,5,6,7,8 * * *")//to run everyday at 2am
	public void syncRevenueForPreviousDay(){

		System.out.println("Running syncRevenueForPreviousDay");
		 
		  
		  if(!"tomcat11".equalsIgnoreCase(SERVER) || !"PROD".equalsIgnoreCase(ENVIRONMENT))
	        {
	              logger.info("Not Running checkShipmentStatus since it is not PROD ");
	              return; 
	        }
		 

		// Get yesterdays day
		Date today = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(today);
		calendar.add(Calendar.DATE, -1);
		
		Date yesterday = calendar.getTime();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

		String dateStr = formatter.format(yesterday);
		logger.info("Fetching Revenew report for date:"+dateStr);
		syncRevenueForDate(dateStr, Integer.parseInt(SFDC_API_MAX_RETRY_COUNT));
		SchedulerApisBean sbean = new SchedulerApisBean();
		sbean.setSyncType("Revenue For Previous Day"); 
		logger.info("updating last sync time..");
		sbean.setError(""); 
		studentZoneDAO.updateLastSyncedTime(sbean);
	}
	
	public void syncRevenueForPreviousDates(String startDateStr, String endDateStr) throws ParseException {

	    logger.info("Starting syncRevenueForPreviousDates Start Date : " + startDateStr + " End Date : " + endDateStr);

	    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate = formatter.parse(startDateStr);
		Date endDate = formatter.parse(endDateStr);

	    Calendar cal = Calendar.getInstance();
	    cal.setTime(startDate);
		// start
		while (cal.getTime().before(endDate)) {
			String dateStr = formatter.format(cal.getTime());
		    logger.info("syncRevenueForPreviousDates current sync date : " + dateStr);
			System.out.println(dateStr);
		    syncRevenueForDate(dateStr, 1);

		    cal.add(Calendar.DATE, 1);
		}
	}
	
	public void syncRevenueForDate(String date, int retryCount) {
		SchedulerApisBean sbean = new SchedulerApisBean();
		try {

			// Get yesterdays collection details
			List<RevenueReportField> revenueBeans = revenueReportHelper.getCollectionBeansForSync(date);
		    List<RevenueReportField> errorRevenueRecords = new ArrayList<RevenueReportField>();
		    List<RevenueReportField> collectionsToInsert = new ArrayList<RevenueReportField>();
		    List<RevenueReportField> insertSuccessList = new ArrayList<RevenueReportField>();
		    // Loop through the beans. If any bean has an error or the amount is 0, add them to the fail list else to insert list
		    for (RevenueReportField revenue : revenueBeans) {
				if(!StringUtils.isBlank(revenue.getErrorMessage())) {
					errorRevenueRecords.add(revenue);
				} else if(Float.parseFloat(revenue.getAmount())==0 && Float.parseFloat(revenue.getRefundedAmount())==0) {
					revenue.setErrorMessage("No revenue/refunds for the day.");
					errorRevenueRecords.add(revenue);
				} else {
					collectionsToInsert.add(revenue);
				}
			}
		    logger.info(""
	    		+ " Revenue list Before Insert "
	    		+ " Insert Size : "+collectionsToInsert.size()
	    		+ " Error List Size : "+errorRevenueRecords.size()
    		);
		    logger.info("Collections To Insert:"+collectionsToInsert);
		    /* 
		     * For every record, create an SObject and insert into SFDC. 
		     * If an error is returned, insert into errorList. 
		     * If no error, get the record id and put into successList
		     */
		    logger.info("Updating in sfdc");
			for (RevenueReportField revenue : collectionsToInsert) {
				createOrUpdateRevenueObjectInSFDC(revenue, insertSuccessList,errorRevenueRecords);
			}
			logger.info("Sending Report Mail");
		    mailer.sendRevenueReportMail(insertSuccessList, errorRevenueRecords);
		}catch (ApiFault e) {  
			logger.info("Caught ApiFault");
			e.printStackTrace();
			if(retryCount > 0) {
				init();
				syncRevenueForDate(date, retryCount - 1);
			}
		}catch(Exception e){
			e.printStackTrace();
			sbean.setError( e+"");
			mailer.mailStackTrace("Revenue Report Sync", e);
			
		}
		sbean.setSyncType("Revenue For Previous Day"); 
		logger.info("Updating Last Sync Time");
		studentZoneDAO.updateLastSyncedTime(sbean); 
	}
	private void createOrUpdateRevenueObjectInSFDC(RevenueReportField revenue, List<RevenueReportField> insertSuccessList, List<RevenueReportField> errorRevenueRecords) throws ConnectionException {

		// Check if Object exists in SFDC for this bean.
		// If it exists - Update, else Create.
		SObject revenueObjectFromSFDC = getSObjectForRevenueIfExists(revenue);
		if(revenueObjectFromSFDC == null) {
			createSObjectInSFDC(revenue, insertSuccessList, errorRevenueRecords);
		} else {
//			updateSObjectInSFDC(revenue, revenueObjectFromSFDC, insertSuccessList, errorRevenueRecords);
		}
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

	private void createSObjectInSFDC(RevenueReportField revenue, List<RevenueReportField> insertSuccessList, List<RevenueReportField> errorRevenueRecords) throws ConnectionException {
		SObject revenueSObject = revenueReportHelper.createRevenueSObject(revenue);
		SaveResult[] qResult;
		qResult = connection.create(new SObject[] { revenueSObject });
		SaveResult saveResult = qResult[0];
		boolean createSuccess = saveResult.getSuccess();
		if(createSuccess) {
			String generatedId = saveResult.getId();
			revenue.setId(generatedId);
			insertSuccessList.add(revenue);
		} else {
			String errorMessage = "Error creating record on SFDC : ";
			Error[] errors = saveResult.getErrors();
			for (Error error : errors) {
				String message = error.getMessage();
				errorMessage += "<br/>" + message;
//				StatusCode statusCode = error.getStatusCode();
//				String code = statusCode.name();
//				String[] fields = error.getFields();
				errorRevenueRecords.add(revenue);
			}
			revenue.setErrorMessage(errorMessage);
		}
	}
	
	private void updateSObjectInSFDC(RevenueReportField revenue, SObject revenueObjectFromSFDC, List<RevenueReportField> insertSuccessList, List<RevenueReportField> errorRevenueRecords) throws ConnectionException {
		revenueReportHelper.updateRevenueSObject(revenue, revenueObjectFromSFDC);
		SaveResult[] qResult;
		qResult = connection.update(new SObject[] { revenueObjectFromSFDC });
		SaveResult saveResult = qResult[0];
		boolean updateSuccess = saveResult.getSuccess();
		if(updateSuccess) {
			String generatedId = saveResult.getId();
			revenue.setId(generatedId);
			insertSuccessList.add(revenue);
		} else {
			String errorMessage = "Error updating record on SFDC : ";
			Error[] errors = saveResult.getErrors();
			for (Error error : errors) {
				String message = error.getMessage();
				errorMessage += "<br/>" + message;
//				StatusCode statusCode = error.getStatusCode();
//				String code = statusCode.name();
//				String[] fields = error.getFields();
				errorRevenueRecords.add(revenue);
			}
			revenue.setErrorMessage(errorMessage);
		}
	}
}
