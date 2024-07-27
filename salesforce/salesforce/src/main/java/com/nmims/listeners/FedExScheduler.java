package com.nmims.listeners;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.comparator.LastModifiedFileComparator;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.ServletConfigAware;
 
import com.nmims.beans.SchedulerApisBean;
import com.nmims.beans.StatusBean;
import com.nmims.beans.StudentBean;
import com.nmims.daos.StudentZoneDao;
import com.nmims.helpers.EmailHelper;
import com.nmims.helpers.FedExShipClient;
import com.nmims.helpers.FedExTrackClient;
import com.nmims.helpers.MailSender;
import com.nmims.helpers.SFConnection;
import com.nmims.webservice.fedex.track.CarrierCodeType;
import com.nmims.webservice.fedex.track.ClientDetail;
import com.nmims.webservice.fedex.track.Notification;
import com.nmims.webservice.fedex.track.NotificationSeverityType;
import com.nmims.webservice.fedex.track.ObjectFactory;
import com.nmims.webservice.fedex.track.TrackEvent;
import com.nmims.webservice.fedex.track.TrackIdentifierType;
import com.nmims.webservice.fedex.track.TrackPackageIdentifier;
import com.nmims.webservice.fedex.track.TrackReply;
import com.nmims.webservice.fedex.track.TrackRequest;
import com.nmims.webservice.fedex.track.TrackRequestProcessingOptionType;
import com.nmims.webservice.fedex.track.TrackSelectionDetail;
import com.nmims.webservice.fedex.track.TrackStatusAncillaryDetail;
import com.nmims.webservice.fedex.track.TrackStatusDetail;
import com.nmims.webservice.fedex.track.TransactionDetail;
import com.nmims.webservice.fedex.track.VersionId;
import com.nmims.webservice.fedex.track.WebAuthenticationCredential;
import com.nmims.webservice.fedex.track.WebAuthenticationDetail;
import com.sforce.soap.partner.Connector;
import com.sforce.soap.partner.Error;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.SaveResult;
import com.sforce.soap.partner.fault.ApiFault;
import com.sforce.soap.partner.fault.LoginFault;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectorConfig;
@Controller
@Component
public class FedExScheduler implements ApplicationContextAware, ServletConfigAware{
	private static ApplicationContext act = null;
	private static ServletConfig sc = null;
	//private PartnerConnection connection;

	@Value( "${SFDC_USERID}" )
	private String SFDC_USERID;

	@Value( "${SFDC_PASSWORD_TOKEN}" )
	private String SFDC_PASSWORD_TOKEN;
	
	@Value( "${ADMISSION_DOCUMENTS_PATH}" )
	private String ADMISSION_DOCUMENTS_PATH;
	
	@Value( "${SFDC_DOCUMENTS_BASE_PATH}" )
	private String SFDC_DOCUMENTS_BASE_PATH;
	
	@Value( "${SHIPPER_TIN_NUMBER}" )
	private String SHIPPER_TIN_NUMBER;
	
	@Value( "${FEDEX_AUTH_KEY}" )
	private String FEDEX_AUTH_KEY;
	
	@Value( "${FEDEX_AUTH_PASSWORD}" )
	private String FEDEX_AUTH_PASSWORD;
	
	@Value( "${FEDEX_ACCOUNT_NUMBER}" )
	private String FEDEX_ACCOUNT_NUMBER;
	
	@Value( "${FEDEX_METER_NUMBER}" )
	private String FEDEX_METER_NUMBER;
	
	@Value("${SFDC_API_MAX_RETRY_COUNT}")
	private String SFDC_API_MAX_RETRY_COUNT;
	
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
	StudentZoneDao studentZoneDao;

	@Autowired
	FedExShipClient fedExShipClient; 
	
	@Autowired
	FedExTrackClient fedExTrackClient; 

	@Autowired
	MailSender mailer;
	@Autowired
	SFConnection sfc;
	@Autowired
	EmailHelper emailHelper;
	@Autowired
	SFConnection sfConnection;
	@Value( "${ENVIRONMENT}" )
	private String ENVIRONMENT;
	private static final Logger logger = LoggerFactory.getLogger(FedExScheduler.class); 
	PartnerConnection connection; 
	public FedExScheduler(SFConnection sf) {
		this.connection = sf.getConnection();
	}
	public void init(){
		SFConnection sf= new SFConnection(SFDC_USERID,SFDC_PASSWORD_TOKEN);
		this.connection = sf.getConnection();
	}
	//@RequestMapping(value = "/checkShipmentStatusForFedex", method = { RequestMethod.GET, RequestMethod.POST })
	public void callCheckShipmentStatus(HttpServletRequest request, HttpServletResponse response) throws IOException {
		checkShipmentStatus(Integer.parseInt(SFDC_API_MAX_RETRY_COUNT)); 
	}
	@RequestMapping(value = "/getLogFiles", method = { RequestMethod.GET, RequestMethod.POST })
	public ResponseEntity<StatusBean> getLogFiles(HttpServletRequest request, HttpServletResponse respons) throws IOException {
		File folder = new File("E:/Studentzone_logs/salesforce_logs");
		HashMap<String,String> logFileNameAndContentHashMap = new HashMap<String,String>();
		StatusBean response = new StatusBean();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");  
		if(request.getParameter("key") !=null && !request.getParameter("key").isEmpty()) {
			String filterKey = request.getParameter("key"); 
			System.out.println("calling getlogfiles api..");
		      if (folder.isDirectory()) {
		         File[] logFilesList = folder.listFiles();
		         if (logFilesList.length < 1) System.out.println("There is no File inside Folder");
		         else { 
		        	 Arrays.sort(logFilesList, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
		        	 for(int i =0; i<3;i++) { //get 3 latest log files
			        	 File logFile = logFilesList[i];  
			        	
			        	 Scanner scanner=new Scanner(logFile);
			        	 List<String> lineList=new ArrayList<>(); 
			        	 while(scanner.hasNextLine()){
			        		 String line = scanner.nextLine();
			        	     if(line.contains(filterKey)) { 
			        	    	 line =line.replaceAll(filterKey, "");
			        	    	 line =line.replaceAll(":", "");
			        	    	 lineList.add(line);
			        	     } 
			        	 }  
			        	 String msgString = StringUtils.join(lineList, "</br>") ;
			        	 logFileNameAndContentHashMap.put(logFile.getName(),msgString);
			         } 
		        	 System.out.println(logFileNameAndContentHashMap); 
		         } 
		      }   
		}
		response.setLogFileNameAndContentHashMap(logFileNameAndContentHashMap);
	      return new ResponseEntity<StatusBean>(response,headers, HttpStatus.OK);
     	 
	}
	//@Scheduled(cron = "0 1 6,13,18,22 * * *")//to run everyday at 6am,1pm,6pm,10pm
	public void checkShipmentStatusScheduler(){
		logger.info("called scheduler...");
		checkShipmentStatus(Integer.parseInt(SFDC_API_MAX_RETRY_COUNT));
	}
    public void checkShipmentStatus(int retryCount){
		logger.info("called checkShipmentStatus...");
		if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
		  System.out.println("Not Running checkShipmentStatus since it is not PROD ");
		  return; }
		logger.info("Finding Dispatch orders created today, but with no shipment request"); 
		  
		logger.info("Querying Dispatch order details from SFDC...");
		 
		SchedulerApisBean sbean = new SchedulerApisBean();
		QueryResult qResult = new QueryResult();

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
			if (qResult.getSize() > 0) {
				ArrayList<TrackRequest> requestList = new ArrayList<>();

				//System.out.println("Fetched " + qResult.getSize() + " DO records.");
				ObjectFactory objectFactory = new ObjectFactory();
				logger.info("Creating Track Request for each Dispatch orders.");
				logger.info("Preparing to send Track Requests to Fedex.");
				while (!done) {
					SObject[] records = qResult.getRecords();
					for (int i = 0; i < records.length; ++i) {
						SObject s = (SObject) records[i];
						StudentBean bean = new StudentBean();
						String dispatchOrderId = (String)s.getField("Id");
						String studentEmailId = (String)s.getField("Student_Email__c");
						String studentNumber = (String)s.getField("Student_Number__c");
						String studentSemester = (String)s.getField("Semester__c");
						//Added to check the value of self learning material and current status of dispatch//
						String selfLearningMaterial = (String)s.getField("Self_Learning_Material_For_Student__c");
						
						bean.setEmailId(studentEmailId);
						bean.setSapid(studentNumber);
						bean.setSem(studentSemester);
						
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

				int count = 0;
				ArrayList<SObject> dosToUpdate = new ArrayList<SObject>();
				ArrayList<String> deliveredStudentEmailIds = new ArrayList<String>();
				ArrayList<String> deliveredStudentSAPId = new ArrayList<String>();
				
				logger.info("Started sending track requests.....");
				for (TrackRequest trackRequest : requestList) {
					logger.info("fedex request count:"+count+"/"+requestList.size());
					trackRequest.setWebAuthenticationDetail(webAuthenticationDetail);
					trackRequest.setClientDetail(clientDetail);
					trackRequest.setVersion(version);
					TrackReply reply = fedExTrackClient.trackShipment(trackRequest);
					try {
						processTrackReply(connection, reply, dosToUpdate, deliveredStudentEmailIds , dispatchOrderIdStudentEmailMap);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					count++;
					
					//System.out.println("Shipment Status Check Count = "+count);
					
					/*
					if(count == 20){
						break;
					}*/
				}
				
				//Update data back in Salesforce
				logger.info("Records to update back in Salesforce: "+dosToUpdate.size());
				if(dosToUpdate.size() > 0){
					 
					connection.setAllOrNoneHeader(false);
					logger.info("Updating records back in Salesforce");
					for(int i = 0; i < dosToUpdate.size() ; i= i + 10){
						int lastIndex =  (i + 10) < dosToUpdate.size() ? (i + 10) : dosToUpdate.size();
						logger.info("\n***** start = "+ i + " end = "+lastIndex +" *********");
						ArrayList<SObject> dosToUpdateSublist =  new ArrayList<SObject>(dosToUpdate.subList(i, lastIndex));
						
						SObject[] records = new  SObject[dosToUpdateSublist.size()];
						records = dosToUpdateSublist.toArray(records);
						updateDORecordsInSFDC(connection, records, dispatchOrderIdStudentEmailMap, deliveredStudentEmailIds,dispatchOrderIdStudentNumberMap,deliveredStudentSAPId);
					}
				}
				logger.info("Sending StudyKit Delivered mails");
				//Uncomment this till Status update issue is resolved
				emailHelper.sendGenericKitDeliveredEmails(deliveredStudentEmailIds,deliveredStudentSAPId,studentNumberStudentMap);
				// create Record in My Communication Tab
				createRecordInUserMailTableAndMailTable(deliveredStudentSAPId,studentNumberStudentMap);
			}
		} catch (ApiFault e) { 
			logger.info("Caught ApiFault");
			e.printStackTrace();
			if(retryCount > 0) {
				init();
				checkShipmentStatus(retryCount - 1);
			}
		}catch (Exception e) {  
			e.printStackTrace();
			sbean.setError( e+"");
		}
		sbean.setSyncType("Fedex Check Shipment Status"); 
		studentZoneDao.updateLastSyncedTime(sbean);
	}
	
	public void createRecordInUserMailTableAndMailTable(ArrayList<String> deliveredStudentSAPId,HashMap<String,StudentBean> studentNumberStudentMap){
		studentZoneDao = (StudentZoneDao)act.getBean("studentZoneDAO");
		
		for(String sapId : deliveredStudentSAPId){
			System.out.println("Sapid In createRecordInUserMailTableAndMailTable--"+sapId);
			StudentBean student = studentNumberStudentMap.get(sapId);
			String htmlBody = emailHelper.getGenericKitDeliveredMailHtmlBody(student.getSapid(),student.getSem());
			long insertedMailId = studentZoneDao.insertMailRecord(student.getSapid(),"ngasce.exams@nmims.edu","Your Study Kit is Delivered",htmlBody);
			studentZoneDao.insertUserMailRecord(student.getSapid(),"ngasce.exams@nmims.edu",student.getSapid(),student.getEmailId(),insertedMailId);
		}
	}
	
	private void updateDORecordsInSFDC(PartnerConnection connection, SObject[] records, HashMap<String, String> dispatchOrderIdStudentEmailMap, ArrayList<String> deliveredStudentEmailIds,HashMap<String,String> dispatchOrderIdStudentNumberMap,ArrayList<String> deliveredStudentSAPId ) {
		

		//System.out.println("Updating records back in SFDC");
		
		try {
			
			for(int j=0; j<records.length; j++){
				//System.out.println("Incoming Records for update: "+records[j].getId());
			}

			// update the records in Salesforce.com
			SaveResult[] saveResults = connection.update(records);
			// check the returned results for any errors
			for (int i=0; i< saveResults.length; i++) {
				
				if (saveResults[i].isSuccess()) {
					System.out.println("Succesfully updated: "+saveResults[i].getId());
					if("Delivered".equals(records[i].getField("Status_Of_Dispatch__c"))){
						String studentEmailId = dispatchOrderIdStudentEmailMap.get(saveResults[i].getId());
						String studentNumber = dispatchOrderIdStudentNumberMap.get(saveResults[i].getId());
						System.out.println("Adding email in Delivered list: "+studentEmailId);
						deliveredStudentEmailIds.add(studentEmailId);
						deliveredStudentSAPId.add(studentNumber);
					}
					
				}else {
					System.out.println("Error updating Record ID "+saveResults[i].getId());
					String errorMessage = "";
					Error[] errors = saveResults[i].getErrors();
					for (int j=0; j< errors.length; j++) {
						errorMessage += errors[j].getMessage() + " ";
						System.out.println("ERROR updating record: " + errorMessage);
					}
				}   
			}

		} catch (Exception e) {
			e.printStackTrace();
			//Send Error Email Code here
		}
	}

	public void processTrackReply(PartnerConnection connection2, TrackReply reply, ArrayList<SObject> dosToUpdate,
			ArrayList<String> deliveredStudentEmailIds , HashMap<String, String> dispatchOrderIdStudentEmailMap) {
		String dispatchOrderId = reply.getTransactionDetail().getCustomerTransactionId();
		try {
			NotificationSeverityType highestError = reply.getHighestSeverity();
			if(highestError == NotificationSeverityType.ERROR || highestError == NotificationSeverityType.FAILURE){
				//Write code for Error handling and email
				String errorMessage = "";
				List<Notification> errorNotifications = reply.getNotifications();
				for (Notification notification : errorNotifications) {
					errorMessage = errorMessage + notification.getMessage() + "\n";
				}
				
				SObject sObject = new SObject();
				sObject.setType("Dispatch_Order__c");
				sObject.setId(dispatchOrderId);
				sObject.setField("Fed_Ex_Track_Error__c", errorMessage);
				
				dosToUpdate.add(sObject);
				logger.info("Error Notification from Fedex for dispatch order id:"+dispatchOrderId);
				logger.info(errorMessage);
				return;
			}else{
				TrackStatusDetail statusDetail = reply.getCompletedTrackDetails().get(0).getTrackDetails().get(0).getStatusDetail();
				String status = statusDetail.getCode();
				String statusDescription = statusDetail.getDescription(); 
				logger.info("dispatchOrderId = "+dispatchOrderId);
				logger.info("Status = "+status);
				
				SObject sObject = new SObject();
				sObject.setType("Dispatch_Order__c");
				sObject.setId(dispatchOrderId);
				sObject.setField("Current_Track_Details__c", statusDescription);
				
				if("DL".equalsIgnoreCase(status)){
					//If delivered then put additional details, otherwise just update Status Description
					//Commenting below line as API has changed. Sanket:23-Mar
					//String deliveryTime = reply.getCompletedTrackDetails().get(0).getTrackDetails().get(0).getActualDeliveryTimestamp().toString();
					String deliverySignature = reply.getCompletedTrackDetails().get(0).getTrackDetails().get(0).getDeliverySignatureName();
					
					//Changed to pick up timestamp from Event Tag
					String deliveryTime = reply.getCompletedTrackDetails().get(0).getTrackDetails().get(0).getEvents().get(0).getTimestamp().toString();
					sObject.setField("Status_Of_Dispatch__c", "Delivered");
					sObject.setField("Delivery_Signature__c", deliverySignature);
					sObject.setField("Delivery_Time__c", deliveryTime);
					
					/*Moving this logic to updateRecords method to send email only if record is updated successfully in Salesforce
					 * String studentEmailId = dispatchOrderIdStudentEmailMap.get(dispatchOrderId);
					System.out.println("Adding email in Delivered list: "+studentEmailId);
					deliveredStudentEmailIds.add(studentEmailId);*/
					
				}else if("DE".equalsIgnoreCase(status)){
					TrackStatusAncillaryDetail ancillaryDetail = statusDetail.getAncillaryDetails().get(0);
					String reasonCode = ancillaryDetail.getReason();
					String reasonDescription = ancillaryDetail.getReasonDescription();
					String action = ancillaryDetail.getAction();
					String actionDescription = ancillaryDetail.getActionDescription();
					
					String deliveryExceptionDetails = "Reason Code:"+reasonCode + "\n" + reasonDescription;
					if(action != null){
						deliveryExceptionDetails += "\nAction Code:" + action + "\n" + actionDescription;
					}
					sObject.setField("Delivery_Exception_Details__c", deliveryExceptionDetails);
					logger.info("Delivery Exception details= "+deliveryExceptionDetails);
				}
				ArrayList<TrackEvent> events = (ArrayList<TrackEvent>)reply.getCompletedTrackDetails().get(0).getTrackDetails().get(0).getEvents();
				String trackEventDetails = "";
				String fedExShipmentEventAndTime =""; //newly added//
				for (TrackEvent trackEvent : events) {
					String eventTime = trackEvent.getTimestamp().toString();
					String eventCode = trackEvent.getEventType();
					String eventDescription = trackEvent.getEventDescription();
					String statusExceptionCode = trackEvent.getStatusExceptionCode();
					String statusExceptionDescription = trackEvent.getStatusExceptionDescription();
					
					trackEventDetails += eventCode + ":" + eventDescription + ":" + eventTime +"\n";
					if("PU".equals(eventCode))//Date on which fedEx picked up package
					{
						fedExShipmentEventAndTime = eventTime;
						System.out.println("fedExShipmentEventAndTime = "+fedExShipmentEventAndTime);
						if(!"DL".equalsIgnoreCase(status)){
							sObject.setField("Status_Of_Dispatch__c", "Transit");
							logger.info("Status Changed to Transit");
						}
					}
					
					if("IT".equals(eventCode))//setting status of dispatch to transit if event code is IT
					{
						//fedExShipmentEventAndTime = eventTime;
						//System.out.println("fedExShipmentEventAndTime = "+fedExShipmentEventAndTime);
						if(!"DL".equalsIgnoreCase(status)){
							sObject.setField("Status_Of_Dispatch__c", "Transit");
							logger.info("Status Changed to Transit");
						}
					}
					
					
					if(statusExceptionCode != null){
						trackEventDetails += statusExceptionCode + ":" + statusExceptionDescription + "\n";
					}
				}
				
				logger.info("trackEventDetails = "+trackEventDetails);
				logger.info("fedExShipmentEventAndTime = "+fedExShipmentEventAndTime);
				
				sObject.setField("Tracking_Events__c", trackEventDetails);
				sObject.setField("FedEx_Shipment_Date__c",fedExShipmentEventAndTime);
				
				dosToUpdate.add(sObject);
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Fedex Track Error:"+e.getMessage());
			SObject sObject = new SObject();
			sObject.setType("Dispatch_Order__c");
			sObject.setId(dispatchOrderId);
			sObject.setField("Fed_Ex_Track_Error__c", e.getMessage());
			
			dosToUpdate.add(sObject);
		}
	}

	
}
