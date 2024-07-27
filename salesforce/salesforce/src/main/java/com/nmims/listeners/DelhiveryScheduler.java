package com.nmims.listeners;

import java.io.IOException;
import java.net.ConnectException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.LinkedHashSet;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nmims.beans.ProductDetails;
import com.nmims.beans.SchedulerApisBean;
import com.nmims.beans.ShipmentRequest;
import com.nmims.beans.StudentBean;
import com.nmims.daos.StudentZoneDao;
import com.nmims.helpers.DelhiveryManager;
import com.nmims.helpers.EmailHelper;
import com.nmims.helpers.SFConnection;
import com.nmims.services.DelhiveryServiceImpl;
import com.sforce.soap.partner.Connector;
import com.sforce.soap.partner.Error;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.SaveResult;
import com.sforce.soap.partner.fault.ApiFault;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;
import com.sforce.ws.bind.XmlObject;

@Controller
public class DelhiveryScheduler {
	
	@Value( "${SERVER}" )
	private String SERVER;
	
	@Value( "${SFDC_USERID}" )
	private String SFDC_USERID;

	@Value( "${SFDC_PASSWORD_TOKEN}" )
	private String SFDC_PASSWORD_TOKEN;
	
	@Value("${SFDC_API_MAX_RETRY_COUNT}")
	private String SFDC_API_MAX_RETRY_COUNT;
	
	@Value( "${ENVIRONMENT}" )
	private String ENVIRONMENT;
	
	public static final int SHIPMENT_STATUS_API_LIMIT=700;
	
	@Autowired
	StudentZoneDao studentZoneDao;
	
	@Autowired
	DelhiveryManager delhiveryManager;
	
	@Autowired
	EmailHelper emailHelper;
	
	@Autowired
	DelhiveryServiceImpl delhiveryService;
	
	private static final Logger logger = LoggerFactory.getLogger(DelhiveryScheduler.class);
	
	private PartnerConnection connection;
	
	public DelhiveryScheduler(SFConnection sf)
	{
		this.connection=sf.getConnection();
	}
	
	public void init()
	{
		SFConnection sf=new SFConnection(SFDC_USERID, SFDC_PASSWORD_TOKEN);
		this.connection=sf.getConnection();
	}
	
	@RequestMapping(value = "/checkShipmentStatusForDelhivery", method = { RequestMethod.GET, RequestMethod.POST })
	public void checkShipmentStatusForDelhiveryCall(HttpServletRequest request, HttpServletResponse response) throws IOException 
	{
		logger.info("checkShipmentStatusForDelhivery called");
		checkShipmentStatusForDelhivery(Integer.parseInt(SFDC_API_MAX_RETRY_COUNT)); 
	}
	
	
	/* @Scheduled(fixedDelay=2*60*60*1000) */ //to run every 2 hours
	@Scheduled(cron = "0 0 2,7,19,23 * * *") //to run everyday at 2am,7am,7pm,11pm
	public void checkShipmentStatusForDelhiveryScheduler()
	{
		if(!"tomcat11".equalsIgnoreCase(SERVER) || !"PROD".equalsIgnoreCase(ENVIRONMENT))
		{
			  logger.info("Not Running checkShipmentStatus since it is not PROD. This is "+SERVER);
			  System.out.println("Not Running checkShipmentStatus since it is not PROD. This is "+SERVER);
			  return; 
		}
		logger.info("checkShipmentStatusForDelhivery called");
		checkShipmentStatusForDelhivery(Integer.parseInt(SFDC_API_MAX_RETRY_COUNT));
	}
	
	public void checkShipmentStatusForDelhivery(int retryCount)
	{
		 
		logger.info("Finding Dispatch orders created today, but with no shipment request"); 
		logger.info("Querying Dispatch order details from SFDC...");
		SchedulerApisBean sbean = new SchedulerApisBean();
		QueryResult qResult = new QueryResult();
		int counter=0;
		try
		{	 
			String soqlQuery = "Select Id,Name_Of_Other_Courier_Service__c, Opportunity__c,SKU_Type__c, Order_Type__c, Student_Email__c, Student_Number__c,Semester__c, "
					+ " Quantity__c, Shipment_Date__c, To_Student__c, To_Centers__c,Status_Of_Dispatch__c, Stock_Keeping_Unit__c,"
					+ " Fed_Ex_Shipment_Created__c,Self_Learning_Material_For_Student__c, To_Student__r.Name, Tracking_Number__c, MobilePhone__c "
					+ " from Dispatch_Order__c where  Fed_Ex_Shipment_Created__c = true and ( Status_Of_Dispatch__c = 'Transit' OR Status_Of_Dispatch__c = 'AWB created' ) and Name_Of_Other_Courier_Service__c='Delhivery' " ;
			qResult = connection.query(soqlQuery);
			boolean done = false;
			logger.info("Found "+qResult.getSize()+" Records from Salesforce.");
			if (qResult.getSize() > 0) 
			{
				HashMap<String,StudentBean> orderIdStudentMap=new HashMap<String,StudentBean>();
				ArrayList<StudentBean> deliveredStudentList=new ArrayList<StudentBean>();
				ArrayList<ProductDetails> requestList=new ArrayList<ProductDetails>();
				while (!done) 
				{
					SObject[] records = qResult.getRecords();
					for (int i = 0; i < records.length; ++i)
					{
						String shippingMode="";
						SObject s = (SObject) records[i];
						String dispatchOrderId = (String)s.getField("Id");
						String trackingId=(String)s.getField("Tracking_Number__c");
						String studentEmailId = (String)s.getField("Student_Email__c");
						String studentNumber = (String)s.getField("Student_Number__c");
						String studentSemester = (String)s.getField("Semester__c");
						String studentMobile = (String)s.getField("MobilePhone__c");
						String skuType= (String)s.getField("SKU_Type__c");
						String orderType= (String)s.getField("Order_Type__c");
						if(skuType==null)
							skuType="";
						if(orderType==null)
							orderType="";
						StudentBean student=new StudentBean();
						student.setEmailId(studentEmailId);
						student.setSapid(studentNumber);
						student.setSem(studentSemester);
						student.setSkuType(skuType);
						student.setOrderType(orderType);
						student.setMobile(studentMobile);
						orderIdStudentMap.put(dispatchOrderId,student); 
						ProductDetails product=new ProductDetails();
						product.setDispatchOrderId(dispatchOrderId);
						product.setTrackingId(trackingId);
						if(orderType.equalsIgnoreCase("Student Order") && skuType.equalsIgnoreCase("Kit"))
							product.setShippingMode("Surface");
						else if(orderType.equalsIgnoreCase("Single Book") || (orderType.equalsIgnoreCase("Student Order") && skuType.equalsIgnoreCase("Book")))
							product.setShippingMode("Express");
						else 
							continue;
						requestList.add(product);	
					}
					if (qResult.isDone())
						done = true; 
					else 
					{
						logger.info("Querying more...");
						qResult = connection.queryMore(qResult.getQueryLocator());
					}
				}
				
				ArrayList<SObject> trackUpdate=new ArrayList<SObject>();
				logger.info("Started sending track requests.....");
				for(ProductDetails pd:requestList)
				{
					logger.info("track request for "+pd.getDispatchOrderId());
					counter++;
					ResponseEntity<String> response = delhiveryManager.trackOrder(pd.getTrackingId(),pd.getShippingMode());
					processReply(response,trackUpdate,pd.getDispatchOrderId(),pd.getShippingMode());
					if(counter==SHIPMENT_STATUS_API_LIMIT)
					{
						TimeUnit.MINUTES.sleep(5);
						counter=0;
						logger.info("counter reset after order id:"+pd.getDispatchOrderId());
					}
				}
				logger.info("number of rows to be updated back in sfdc: "+trackUpdate.size());
				if(trackUpdate.size() > 0)
				{	 
					connection.setAllOrNoneHeader(false);
					for(int i = 0; i < trackUpdate.size() ; i= i + 10)
					{
						int lastIndex =  (i + 10) < trackUpdate.size() ? (i + 10) : trackUpdate.size();
						ArrayList<SObject> trackUpdateSublist =  new ArrayList<SObject>(trackUpdate.subList(i, lastIndex));
						
						SObject[] records = new  SObject[trackUpdateSublist.size()];
						records = trackUpdateSublist.toArray(records);
						updateDORecordsInSFDC(records,orderIdStudentMap,deliveredStudentList);
					}
				}
				if(deliveredStudentList.size()>0) {
					emailHelper.sendGenericKitDeliveredEmailsFromDelhivery(deliveredStudentList);
					emailHelper.createRecordInUserMailTableAndMailTable(deliveredStudentList);
					delhiveryService.sendDeliveredSMSFromDelhivery(deliveredStudentList);
				}
			}
		}
		catch (ApiFault ex) 
		{ 
			logger.info("Caught ApiFault :"+ex);
			//ex.printStackTrace();
			if(retryCount > 0) 
			{
				init();
				checkShipmentStatusForDelhivery(retryCount - 1);
			}
		}
		catch (ConnectionException ce) 
		{ 
			logger.info("Caught ConnectionException :"+ce);
			//ex.printStackTrace();
			if(retryCount > 0) 
			{
				init();
				checkShipmentStatusForDelhivery(retryCount - 1);
			}
		}
		catch(Exception e)
		{
			logger.info("Exception in checkShipmentStatusForDelhivery method: "+e);
			//System.out.println(e.getMessage());
			//e.printStackTrace();
			sbean.setError(e.getMessage());
		}
		sbean.setSyncType("Fedex Check Shipment Status"); 
		studentZoneDao.updateLastSyncedTime(sbean);
		 
	}
	
	public void updateDORecordsInSFDC(SObject []records,HashMap<String,StudentBean> orderIdStudentMap,ArrayList<StudentBean> deliveredStudentList) throws Exception
	{
		try 
		{
			// update the records in Salesforce.com
			SaveResult[] saveResults = connection.update(records);
			// check the returned results for any errors
			
			for (int i=0; i< saveResults.length; i++) 
			{
				SObject s=(SObject)records[i];
				String orderId=(String)s.getField("Id");
				if (saveResults[i].isSuccess()) 
				{
					//System.out.println(i+". Successfully updated record - Id: " + orderId);
					logger.info(i+". Successfully updated record - Id: " + orderId);
					String isDelivered=(String)s.getField("Status_Of_Dispatch__c");
					//System.out.println("Status of dispatch:"+isDelivered);
					if(!(StringUtils.isBlank(isDelivered)) && isDelivered.equalsIgnoreCase("Delivered"))
					{
						String id=(String)s.getField("Id");
						StudentBean student=orderIdStudentMap.get(id);
						String deliveredDateTime = (String)s.getField("Delivery_Time__c");
						
						DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					    SimpleDateFormat deliveredDateFormat = new SimpleDateFormat("MMM-dd-yyyy HH:mm:aa");
					    Date dateAndTime = sdf.parse(deliveredDateTime);
					    String formattedDeliveredDateTime = deliveredDateFormat.format(dateAndTime);	
						student.setDeliveredDateTime(formattedDeliveredDateTime);
						deliveredStudentList.add(student);
					}
				}
				else 
				{
					//System.out.println("Error updating Record ID "+orderId);
					String errorMessage = "";
					Error[] errors = saveResults[i].getErrors();
					for (int j=0; j< errors.length; j++)
					{
						errorMessage += errors[j].getMessage() + " ";
						//System.out.println("ERROR updating record: " + errorMessage);
					}
					logger.info("Error updating Record ID "+orderId+" with message "+errorMessage);
				}   
			}
		}
		catch(Exception e)
		{
			logger.info("Exception in updateDORecordsInSFDC :"+e);
			e.printStackTrace();
			throw new IllegalArgumentException("Error in connection.update,check field");
		}
	}
	
	public void processReply(ResponseEntity<String> response,ArrayList<SObject> trackUpdate,String dispatchOrderId,String shippingMode)
	{
		try
		{
			if(response.getStatusCode().is4xxClientError())
			{
				String errorMessage=response.getBody();
				//System.out.println(errorMessage+":"+dispatchOrderId);
				//System.out.println("error process reply");
				SObject sObject = new SObject();
				sObject.setType("Dispatch_Order__c");
				sObject.setId(dispatchOrderId);
				sObject.setField("Fed_Ex_Track_Error__c", errorMessage);
				
				trackUpdate.add(sObject);
				logger.info("Error response from delhivery track order for: "+dispatchOrderId+" with error "+errorMessage);
				return;
			}
			else
			{
				//System.out.println("successfull process reply");
				logger.info("Success response from delhivery track order for: "+dispatchOrderId);
				JsonObject jsonObj=new JsonParser().parse(response.getBody()).getAsJsonObject();
				JsonObject status =jsonObj.get("ShipmentData").getAsJsonArray().get(0).getAsJsonObject().get("Shipment").getAsJsonObject().get("Status").getAsJsonObject();
				String instructions=status.get("Instructions").getAsString();
				String statusDescription=status.get("Status").getAsString();
				String statusType=status.get("StatusType").getAsString();
				SObject sObject = new SObject();
				sObject.setType("Dispatch_Order__c");
				sObject.setId(dispatchOrderId);
				logger.info("instructions in status for "+dispatchOrderId+":"+instructions);
				sObject.setField("Current_Track_Details__c", instructions);
				if(statusType.equalsIgnoreCase("DL"))
				{
					JsonObject consignee=jsonObj.get("ShipmentData").getAsJsonArray().get(0).getAsJsonObject().get("Shipment").getAsJsonObject().get("Consignee").getAsJsonObject();
					String destination_pin=consignee.get("PinCode").getAsString();
					String ss="";
					if(statusDescription.equalsIgnoreCase("Delivered"))
					{
						ss="Delivered";
						sObject.setField("Status_Of_Dispatch__c", "Delivered");
					}
					else if(statusDescription.equalsIgnoreCase("RTO"))
					{
						ss="RTO";
						sObject.setField("Delivery_Exception_Details__c", "Shipment returned back");
						sObject.setField("Status_Of_Dispatch__c", "Returned Back");
						sObject.setField("Reason_for_returned_back__c", "Shipment returned back");
					}
					//String shippingCharges=checkShippingChargesFromDelhivery(shippingMode,destination_pin,ss);
					//sObject.setField("Cost_Of_Shipement__c", shippingCharges);
					//sObject.setField("Other_Courier_Services_Dispatch__c", "Yes");
					String deliveryDateTime="";
					String deliveryTime=status.get("StatusDateTime").getAsString();
					deliveryDateTime=deliveryTime.replace("T", " ").substring(0,19);
//					Calendar calendar = new GregorianCalendar();
//				    DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
//				    Date date=dateFormatter.parse(deliveryDate);
//				    calendar.setTime(date);
					sObject.setField("Delivery_Time__c",deliveryDateTime);
//					sObject.setField("Delivery_Signature__c", deliverySignature);
				}
				else if(!(statusDescription.equalsIgnoreCase("Manifested") || statusDescription.equalsIgnoreCase("Not Picked")))
				{
					sObject.setField("Status_Of_Dispatch__c", "Transit");
				}
	
				String trackEventDetails="";
				JsonArray jsonArray=jsonObj.get("ShipmentData").getAsJsonArray().get(0).getAsJsonObject().get("Shipment").getAsJsonObject().get("Scans").getAsJsonArray();
				logger.info("JsonArray for tracking events for "+dispatchOrderId+":"+jsonArray.toString());
				for(int i=0;i<=jsonArray.size()-1;i++)
				{
					JsonObject scanDetail=jsonArray.get(i).getAsJsonObject().get("ScanDetail").getAsJsonObject();
					String scanTime=scanDetail.get("ScanDateTime").getAsString();
					String scanType=scanDetail.get("ScanType").getAsString();
					String scanDescription=scanDetail.get("Scan").getAsString();
					String scanInstructions=scanDetail.get("Instructions").getAsString();
					if(scanType.equalsIgnoreCase("UD") && scanDescription.contains("In Transit") && scanInstructions.contains("Shipment picked up"))
					{
						String shipmentDate="";
						if(scanTime.length()==19)
						{
							shipmentDate=scanTime+".000Z";
						}
						else
						{
							shipmentDate=scanTime.substring(0, scanTime.length()-3)+"000Z";
						}
						Calendar calendar = new GregorianCalendar();
					    DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
					    Date date=dateFormatter.parse(shipmentDate);
					    calendar.setTime(date);
					    sObject.setField("Shipment_Date__c", calendar);
					}
					trackEventDetails +=scanType+" : "+scanDescription+" : "+scanInstructions+" : "+scanTime.replace("T", " ").substring(0,19)+"\n";
				}
				logger.info("Track Event Details for "+dispatchOrderId+":"+trackEventDetails);
				sObject.setField("Tracking_Events__c", trackEventDetails);
				
				try{
					if(jsonObj.get("ShipmentData").getAsJsonArray().get(0).getAsJsonObject().get("Shipment").getAsJsonObject().get("FirstAttemptDate").getAsString()!=null){
						String firstAttemptDate=jsonObj.get("ShipmentData").getAsJsonArray().get(0).getAsJsonObject().get("Shipment").getAsJsonObject().get("FirstAttemptDate").getAsString();
						String firstAttemptDateTime=firstAttemptDate.replace("T", " ").substring(0,19);
						sObject.setField("First_Attempt_Date__c",firstAttemptDateTime);
					}
				}
				catch(Exception e){
					logger.info("Exception while setting first Attempt Date:"+e.getMessage());
				}
				
				trackUpdate.add(sObject);
				return;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			SObject sObject = new SObject();
			sObject.setType("Dispatch_Order__c");
			sObject.setId(dispatchOrderId);
			sObject.setField("Fed_Ex_Track_Error__c", e.getMessage());
			
			trackUpdate.add(sObject);
			logger.info("Error response from delhivery process reply for: "+dispatchOrderId+" with error "+e.getMessage());
			return;
		}
	}
	
	public String checkShippingChargesFromDelhivery(String mode,String destination_pin,String status,String origin_pin) throws Exception
	{
		try
		{
			ResponseEntity<String> res=delhiveryManager.shippingChargesCalculation(mode, destination_pin, status,origin_pin);
			if(res.getStatusCode().is4xxClientError())
			{
				logger.info("Exception in getting shipping charges");
				throw new Exception("Error in getting shipping charges");
			}
			else
			{
				String shippingCharges=res.getBody();
				return shippingCharges;
			}
		}
		catch(Exception e)
		{
			logger.info("Exception in getting shipping charges"+e);
			throw new Exception("Error in getting shipping charges");
		}
	}
	
	@RequestMapping(value = "/checkShippingChargesForDelhiveryCall", method = { RequestMethod.GET, RequestMethod.POST })
	public void checkShippingChargesForDelhiveryCall(HttpServletRequest request, HttpServletResponse response) throws IOException 
	{
		logger.info("checkShippingCharges called");
		checkShippingCharges(Integer.parseInt(SFDC_API_MAX_RETRY_COUNT));
	}
	
	@Scheduled(fixedDelay=1*60*60*1000) //to run every 1 hour
	public void checkShippingChargesForDelhiveryScheduler()
	{
		if(!"tomcat11".equalsIgnoreCase(SERVER) || !"PROD".equalsIgnoreCase(ENVIRONMENT))
		{
			  logger.info("Not Running checkShippingChargesForDelhiveryScheduler since it is not PROD. This is "+SERVER);
			  return; 
		}
		logger.info("checkShippingCharges called");
		checkShippingCharges(Integer.parseInt(SFDC_API_MAX_RETRY_COUNT));
	}
	
	public void checkShippingCharges(int retryCount)
	{
		QueryResult qResult = new QueryResult();
		int counter=0;
		try
		{
			String soqlQuery = "Select Id,Name_Of_Other_Courier_Service__c,SKU_Type__c, Order_Type__c, Status_Of_Dispatch__c, "
					+ " Stock_Keeping_Unit__c,To_Centers__r.nm_PostalCode__c,To_Student__r.Zip_Postal_Code_Shipping__c,From_Centers__r.nm_PostalCode__c,"
					+ " Fed_Ex_Shipment_Created__c,Self_Learning_Material_For_Student__c, Tracking_Number__c "
					+ " from Dispatch_Order__c where  Fed_Ex_Shipment_Created__c = true and ( Status_Of_Dispatch__c = 'Delivered' OR Status_Of_Dispatch__c = 'Returned Back' ) and Name_Of_Other_Courier_Service__c='Delhivery' and Cost_Of_Shipement__c=NULL and Other_Courier_Services_Dispatch__c='No' and Delivery_Time__c!=NULL " ;
			qResult = connection.query(soqlQuery);
			boolean done = false;
			logger.info("Found "+qResult.getSize()+" Records from Salesforce.");
			if (qResult.getSize() > 0) 
			{
				String origin_pin="";
				ArrayList<ProductDetails> requestList=new ArrayList<ProductDetails>();
				while (!done) 
				{
					SObject[] records = qResult.getRecords();
					for (int i = 0; i < records.length; ++i)
					{
						SObject s = (SObject) records[i];
						String dispatchOrderId = (String)s.getField("Id");
						String trackingId=(String)s.getField("Tracking_Number__c");
						String skuType= (String)s.getField("SKU_Type__c");
						String orderType= (String)s.getField("Order_Type__c");
						String statusOfDispatch=(String)s.getField("Status_Of_Dispatch__c");
						String placeToShip=(String)s.getField("Self_Learning_Material_For_Student__c");
						String customerPin="";
						if(skuType==null)
							skuType="";
						if(orderType==null)
							orderType="";
						ProductDetails product=new ProductDetails();
						product.setDispatchOrderId(dispatchOrderId);
						product.setTrackingId(trackingId);
						product.setStatusOfDispatch(statusOfDispatch);
			  			
						XmlObject from_Center=(XmlObject)s.getField("From_Centers__r");
						origin_pin = (String)from_Center.getChild("nm_PostalCode__c").getValue();
						product.setOriginPin(origin_pin);
						if(placeToShip.contains("Send to my shipping address"))
						{
							XmlObject to_student=(XmlObject)s.getField("To_Student__r");
							customerPin = (String)to_student.getChild("Zip_Postal_Code_Shipping__c").getValue();
							product.setCustomerPin(customerPin);
						}
						else if(placeToShip.contains("Send to my Information Centre"))
						{
							XmlObject toCenterRecord = (XmlObject)s.getField("To_Centers__r");
							customerPin = (String)toCenterRecord.getChild("nm_PostalCode__c").getValue();
							product.setCustomerPin(customerPin);
						}
						if(orderType.equalsIgnoreCase("Student Order") && skuType.equalsIgnoreCase("Kit"))
							product.setShippingMode("Surface");
						else if(orderType.equalsIgnoreCase("Single Book") || (orderType.equalsIgnoreCase("Student Order") && skuType.equalsIgnoreCase("Book")))
							product.setShippingMode("Express");
						else 
							continue;
						requestList.add(product);
					}
					if (qResult.isDone())
						done = true; 
					else 
					{
						logger.info("Querying more...");
						qResult = connection.queryMore(qResult.getQueryLocator());
					}
				}
				ArrayList<SObject> trackUpdate=new ArrayList<SObject>();
				for(ProductDetails pd:requestList)
				{
					logger.info("shipping charges request for "+pd.getDispatchOrderId());
					counter++;
					processReplyForDelhiveryShippingCharges(trackUpdate,pd.getDispatchOrderId(),pd.getShippingMode(),pd.getStatusOfDispatch(),pd.getCustomerPin(),pd.getOriginPin());
					if(counter==40)
					{
						TimeUnit.MINUTES.sleep(1);
						counter=0;
						logger.info("counter reset after order id:"+pd.getDispatchOrderId());
					}
				}
				logger.info("number of rows to be updated back in sfdc: "+trackUpdate.size());
				if(trackUpdate.size() > 0)
				{	 
					connection.setAllOrNoneHeader(false);
					for(int i = 0; i < trackUpdate.size() ; i= i + 10)
					{
						int lastIndex =  (i + 10) < trackUpdate.size() ? (i + 10) : trackUpdate.size();
						ArrayList<SObject> trackUpdateSublist =  new ArrayList<SObject>(trackUpdate.subList(i, lastIndex));
						
						SObject[] records = new  SObject[trackUpdateSublist.size()];
						records = trackUpdateSublist.toArray(records);
						updateDORecordsInSFDCForShippingCharges(records);
					}
				}
			}
		}
		catch (ApiFault ex) 
		{ 
			logger.info("Caught ApiFault :"+ex);
			if(retryCount > 0) 
			{
				init();
				checkShippingCharges(retryCount - 1);
			}
		}
		catch (ConnectionException ce) 
		{ 
			logger.info("Caught ConnectionException :"+ce);
			if(retryCount > 0) 
			{
				init();
				checkShippingCharges(retryCount - 1);
			}
		}
		catch(Exception e)
		{
			logger.info("Exception in checkShippingCharges method: "+e);
		}
	}
	
	public void processReplyForDelhiveryShippingCharges(ArrayList<SObject> trackUpdate,String dispatchOrderId,String shippingMode,String statusOfDispatch,String customerPin,String origin_pin)
	{
		try
		{
			String ss="";
			SObject sObject = new SObject();
			sObject.setType("Dispatch_Order__c");
			sObject.setId(dispatchOrderId);
			if(statusOfDispatch.equalsIgnoreCase("Delivered"))
				ss="Delivered";
			else if(statusOfDispatch.equalsIgnoreCase("Returned Back"))
				ss="RTO";
			String shippingCharges=checkShippingChargesFromDelhivery(shippingMode,customerPin,ss,origin_pin);
			sObject.setField("Cost_Of_Shipement__c", shippingCharges);
			sObject.setField("Other_Courier_Services_Dispatch__c", "Yes");
			
			trackUpdate.add(sObject);
			return;
		}
		catch(Exception e)
		{
			SObject sObject = new SObject();
			sObject.setType("Dispatch_Order__c");
			sObject.setId(dispatchOrderId);
			sObject.setField("Fed_Ex_Track_Error__c", e.getMessage());
			
			trackUpdate.add(sObject);
			logger.info("Error response from delhivery process reply for: "+dispatchOrderId+" with error "+e.getMessage());
			return;
		}
	}
	
	public void updateDORecordsInSFDCForShippingCharges(SObject []records) throws Exception
	{
		try 
		{
			// update the records in Salesforce.com
			SaveResult[] saveResults = connection.update(records);
			// check the returned results for any errors
			
			for (int i=0; i< saveResults.length; i++) 
			{
				SObject s=(SObject)records[i];
				String orderId=(String)s.getField("Id");
				if (saveResults[i].isSuccess()) 
				{
					//System.out.println(i+". Successfully updated record - Id: " + orderId);
					logger.info(i+". Successfully updated record - Id: " + orderId);
				}
				else 
				{
					//System.out.println("Error updating Record ID "+orderId);
					String errorMessage = "";
					Error[] errors = saveResults[i].getErrors();
					for (int j=0; j< errors.length; j++)
					{
						errorMessage += errors[j].getMessage() + " ";
						//System.out.println("ERROR updating record: " + errorMessage);
					}
					logger.info("Error updating Record ID "+orderId+" with message "+errorMessage);
				}   
			}
		}
		catch(Exception e)
		{
			logger.info("Exception in updateDORecordsInSFDCForShippingCharges :"+e);
			throw new IllegalArgumentException("Error in connection.update,check field");
		}
	}
}
