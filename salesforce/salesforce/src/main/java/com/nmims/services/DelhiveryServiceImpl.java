package com.nmims.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.nmims.helpers.DelhiveryManager;
import com.nmims.helpers.ExcelHelper;
import com.nmims.helpers.MailSender;
import com.nmims.helpers.SFConnection;
import com.nmims.helpers.SMSSender;
import com.nmims.interfaces.CourierService;
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
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;
import com.nmims.beans.DispatchedDocumentMergeResponseBean;
import com.nmims.beans.FedExMergerBean;
import com.nmims.beans.ProductDetails;
import com.nmims.beans.ReceiverDetails;
import com.nmims.beans.ShipmentRequest;
import com.nmims.beans.ShipperDetails;
import com.nmims.beans.StudentBean;
import com.nmims.helpers.AmazonS3Helper;

@Service(value="delhiveryservice")
public class DelhiveryServiceImpl implements CourierService {
	
	@Value("${AWS_UPLOAD_URL_LOCAL_SALESFORCE}")
	private String AWS_UPLOAD_URL_LOCAL_SALESFORCE;
	
	@Value("${DelhiveryDocuments}")
	private String DelhiveryDocuments;
	
	@Value("${DelhiveryBucket}")
	private String DelhiveryBucket;
	
	@Value("${SFDC_USERID}")
	private String SFDC_USERID;
	
	@Value("${SFDC_PASSWORD_TOKEN}")
	private String SFDC_PASSWORD_TOKEN; 
	
	@Value( "${ADMISSION_DOCUMENTS_PATH}" )
	private String ADMISSION_DOCUMENTS_PATH;
	
	@Value( "${SFDC_DOCUMENTS_BASE_PATH}" )
	private String SFDC_DOCUMENTS_BASE_PATH;
	
	@Value( "${FEDEX_MERGE_DOC}" )
	private String FEDEX_MERGE_DOC;
	
	@Autowired
	DelhiveryManager deliveryObject;
	
	@Autowired
	AmazonS3Helper awsHelper;
	
	@Autowired
	MailSender mailer;
	
	@Autowired
	SMSSender smsSender;
	
	private static final Logger logger = LoggerFactory.getLogger(DelhiveryServiceImpl.class);
	private PartnerConnection connection;
	private static final String warehouseClientNameSurface="NMIMS SURFACE";
	private static final String warehouseClientNameExpress="NMIMS EXPRESS";
	private static final String warehousePickupNameTest="NMIMS SURFACE";
	private static final String warehouseClientNameTest="NMIMSSURFACE-B2C";
	
	private static final String DELIVERED_TIME="15 days";
	
	public DelhiveryServiceImpl(SFConnection sf)
	{
		this.connection=sf.getConnection();
	}
	public void init()
	{
		SFConnection sf=new SFConnection(SFDC_USERID, SFDC_PASSWORD_TOKEN);
		this.connection=sf.getConnection();
	}
	public ResponseEntity<String> checkPincode(String pincode,String shippingMode) throws IOException
	{
		return deliveryObject.checkPinCode(pincode,shippingMode);
	}
	
	public ResponseEntity<String> getTrackingId(HttpServletRequest request) throws IOException
	{
		return deliveryObject.getTrackingId(request);
	}
	
	public ResponseEntity<String> getBulkTrackingId(HttpServletRequest request) throws IOException
	{
		return deliveryObject.getBulkTrackingId(request);
	}
	public ResponseEntity<String> trackOrder(String trackingId,String shippingMode) throws IOException
	{
		return deliveryObject.trackOrder(trackingId,shippingMode);
	}
	public ResponseEntity<String> editOrder() throws IOException
	{
		return deliveryObject.editOrder();
	}
	public ResponseEntity<String> cancelOrder() throws IOException
	{
		return deliveryObject.cancelOrder();
	}
	public ResponseEntity<String> pickupRequestCreation() throws IOException
	{
		return deliveryObject.pickupRequestCreation();
	}
	
	public String orderCreationFromDelhivery(String dispatchOrderId,int retryCount)
	{
		logger.info("orderCreationFromDelhivery called");
		for(int i=0;i<=retryCount;i++)
		{
			//System.out.println(i);
			logger.info("orderCreation called for i="+i);
			String s=orderCreation(dispatchOrderId);
			if(!(s.equalsIgnoreCase("apiFault")))
			return s;
			init();
		}
		return "error in creating connection with salesforce";
	}
	
	public String orderCreation(String dispatchOrderId) 
	{
//		ConnectorConfig config = new ConnectorConfig();
//		config.setUsername(SFDC_USERID);
//		config.setPassword(SFDC_PASSWORD_TOKEN);
//		connection = Connector.newConnection(config);
		QueryResult qResult = new QueryResult();
		try
		{
			String soqlQuery = "Select Id, Opportunity__c, Student_Number__c, Student_Email__c, Semester__c, SKU_Type__c, Program__c, "
					+ " Order_Type__c, Quantity__c, Shipment_Date__c, Status_Of_Dispatch__c, Stock_Keeping_Unit__c,Stock_Keeping_Unit__r.Name,Name,"
					+ " Fed_Ex_Shipment_Created__c, Tracking_Number__c, Self_Learning_Material_For_Student__c, "
					+ " From_Centers__r.ICN__c,From_Centers__r.Contact_3__c,From_Centers__r.Name, From_Centers__r.nm_CenterCity__c, From_Centers__r.nm_Street__c, From_Centers__r.Contact_No_1__c, "
					+ " From_Centers__r.nm_StateProvince__c, From_Centers__r.Address__c, From_Centers__r.nm_PostalCode__c, From_Centers__r.nm_Country__c,"
					+ " To_Centers__r.Name, To_Centers__r.nm_CenterCity__c, To_Centers__r.nm_Street__c, To_Centers__r.Contact_No_1__c, "
					+ " To_Centers__r.nm_StateProvince__c, To_Centers__r.nm_PostalCode__c, To_Centers__r.nm_Country__c, To_Centers__r.Address__c,"
					+ " To_Student__r.Name, To_Student__r.Shipping_Street__c,To_Student__r.State_Province_Shipping__c,To_Student__r.City_Shipping_Account__c, "
					+ " To_Student__r.House_No_Name_Shipping_Account__c,To_Student__r.Nearest_LandMark_Shipping__c, To_Student__r.Country_Shipping__c,"
					+ " To_Student__r.Zip_Postal_Code_Shipping__c,To_Student__r.nmStudent_Program__c,To_Student__r.Locality_Name_Shipping__c,To_Student__r.PersonMobilePhone"	
					+ " from Dispatch_Order__c where Fed_Ex_Shipment_Created__c = false and ( Status_Of_Dispatch__c = 'Transit' OR Status_Of_Dispatch__c = 'Study Kit assigned' ) and Id = '"+dispatchOrderId +"'"; 
			
		    //System.out.println(soqlQuery);
			qResult = connection.query(soqlQuery);
			logger.info("Number of rows returned: "+qResult.getSize());
			if(qResult.getSize()>0)
			{
				String orderId="";
				String studentNumber="";
				String studentEmailId="";
				String skuType="";
				String semester="";
				String trackingNumber="";
				String customerPin="";
				String customerAddress="";
				String customerProgram="";
				String placeToShip="";
				String informationCenterAddress="";
				String orderType="";
				String shippingMode="";
				String sku="";
				String doName="";
				ReceiverDetails receiver=new ReceiverDetails();
				ArrayList<ShipmentRequest> requestList=new ArrayList<ShipmentRequest>();
				boolean done=false;
				while(!done)
				{
					SObject[] records = qResult.getRecords();
					for (int i = 0; i < records.length; ++i) 
					{
						SObject s = (SObject) records[i];
						orderId =(String)s.getField("Id");
						studentNumber = (String)s.getField("Student_Number__c");
						studentEmailId = (String)s.getField("Student_Email__c");
						skuType  = (String)s.getField("SKU_Type__c");
						semester  = (String)s.getField("Semester__c");
						orderType=(String)s.getField("Order_Type__c");
						placeToShip=(String)s.getField("Self_Learning_Material_For_Student__c");
						doName=(String)s.getField("Name");
						XmlObject skuUnit=(XmlObject)s.getField("Stock_Keeping_Unit__r");
						sku = (String)skuUnit.getChild("Name").getValue();
						if(StringUtils.isBlank(sku))
							sku="";
						if(StringUtils.isBlank(doName))
							doName="";
						if(orderType==null)
							orderType="";
						if(skuType==null)
							skuType="";
						
						logger.info("Student Number: "+studentNumber+"OrderId: "+orderId+"EmailId:"+studentEmailId+",skuType: "+skuType+",orderType: "+orderType+",placeToShip: "+placeToShip+",semester: "+semester);
						
						ShipperDetails shipper=new ShipperDetails();
						XmlObject from_centers=(XmlObject)s.getField("From_Centers__r");
						String warehousePickupName = (String)from_centers.getChild("ICN__c").getValue();
						String warehousePin= (String)from_centers.getChild("nm_PostalCode__c").getValue();
						String warehouseCity= (String)from_centers.getChild("nm_CenterCity__c").getValue();
						String warehouseState= (String)from_centers.getChild("nm_StateProvince__c").getValue();
						String warehouseCountry= (String)from_centers.getChild("nm_Country__c").getValue();
						String warehousePhone ="";
						if(warehousePickupName.contains("NMIMS Warehouse Bhiwandi"))
						{
							warehousePhone = (String)from_centers.getChild("Contact_No_1__c").getValue();
						}
						else if(warehousePickupName.contains("NMIMS - NGASCE"))
						{
							warehousePhone = (String)from_centers.getChild("Contact_3__c").getValue();
						}
						String warehouseAddress= (String)from_centers.getChild("Address__c").getValue();
						shipper.setPin(warehousePin);
						shipper.setCity(warehouseCity);
						shipper.setState(warehouseState);
						shipper.setCountry(warehouseCountry);
						shipper.setPhone(warehousePhone);
						shipper.setAdd(warehouseAddress);
						shipper.setPickupName(warehousePickupName);
						logger.info("Shipper details are:"+shipper.getPin()+":"+shipper.getCity()+":"+shipper.getState()+":"+shipper.getCountry()+":"+shipper.getPhone()+":"+shipper.getAdd()+":"+shipper.getPickupName());
						if(orderType.equalsIgnoreCase("Student Order") && skuType.equalsIgnoreCase("Kit"))
						{
							//shipper.setPickupName(warehousePickupNameTest);
							//shipper.setClientName(warehouseClientNameTest);
							shipper.setClientName(warehouseClientNameSurface);
							shippingMode="S";
						}
						else if(orderType.equalsIgnoreCase("Single Book") || (orderType.equalsIgnoreCase("Student Order") && skuType.equalsIgnoreCase("Book")))
						{
							shipper.setClientName(warehouseClientNameExpress);
							//shipper.setPickupName(warehousePickupNameTest);
							//shipper.setClientName(warehouseClientNameTest);
							shippingMode="E";
						}
						else
						{
							logger.info("Error: Cannot create shipment for this Disptach order as record does not match required criteria. Please check orderType");
							return "Error: Cannot create shipment for this Disptach order as record does not match required criteria. Please check orderType";
						}
						
						if(placeToShip.contains("Send to my shipping address"))
						{
								XmlObject to_student=(XmlObject)s.getField("To_Student__r");
								customerPin = (String)to_student.getChild("Zip_Postal_Code_Shipping__c").getValue();
								String customerName = (String)to_student.getChild("Name").getValue();
								String customerPhone = (String)to_student.getChild("PersonMobilePhone").getValue();
								customerProgram = (String)to_student.getChild("nmStudent_Program__c").getValue();
								String customerHouseLocality = (String)to_student.getChild("House_No_Name_Shipping_Account__c").getValue();
								String customerReceiverStreet = (String)to_student.getChild("Shipping_Street__c").getValue();
								//String customerLandmark = (String)to_student.getChild("Nearest_LandMark_Shipping__c").getValue();
								String customerLocality = (String)to_student.getChild("Locality_Name_Shipping__c").getValue();
								String customerCity = (String)to_student.getChild("City_Shipping_Account__c").getValue();
								String customerState = (String)to_student.getChild("State_Province_Shipping__c").getValue();
								String customerCountry = (String)to_student.getChild("Country_Shipping__c").getValue();
//								if(!StringUtils.isBlank(customerLandmark))
//								{
//									if((!StringUtils.isBlank(customerLocality)) && (!customerLocality.equalsIgnoreCase("NA")))
//										customerAddress = customerHouseLocality + "," +customerLocality +"," + customerReceiverStreet +"," + customerLandmark;
//									else
//										customerAddress = customerHouseLocality + "," + customerReceiverStreet +"," + customerLandmark;
//								}
//								else 
//								{	
//								}
								if((!StringUtils.isBlank(customerLocality)) && (!customerLocality.equalsIgnoreCase("NA")))
									customerAddress = customerHouseLocality + "," +customerLocality +"," + customerReceiverStreet;
								else
									customerAddress = customerHouseLocality + "," + customerReceiverStreet;
								receiver.setCustomerPhone(customerPhone);
								receiver.setCustomerProgram(customerProgram);
								receiver.setCustomerCountry(customerCountry);
								receiver.setCustomerAddress(customerAddress);
								receiver.setCustomerPin(customerPin);
								receiver.setCustomerState(customerState);
								receiver.setCustomerCity(customerCity);
								receiver.setCustomerOrderId(orderId);
								receiver.setCustomerPersonName(customerName+" ("+studentNumber+")");
								receiver.setCustomerCompanyName("Student Number: "+studentNumber);
						}
						else if(placeToShip.contains("Send to my Information Centre"))
						{
								customerProgram = (String)s.getField("Program__c");
								XmlObject toCenterRecord = (XmlObject)s.getField("To_Centers__r");
								customerPin = (String)toCenterRecord.getChild("nm_PostalCode__c").getValue();
								String customerName = (String)toCenterRecord.getChild("Name").getValue();
								String customerPhone = (String)toCenterRecord.getChild("Contact_No_1__c").getValue();
								customerAddress = (String)toCenterRecord.getChild("Address__c").getValue();
								String customerCity = (String)toCenterRecord.getChild("nm_CenterCity__c").getValue();
								String customerState = (String)toCenterRecord.getChild("nm_StateProvince__c").getValue();
								String customerCountry = (String)toCenterRecord.getChild("nm_Country__c").getValue();
								
								receiver.setCustomerPin(customerPin);
								receiver.setCustomerProgram(customerProgram);
								receiver.setCustomerPhone(customerPhone);
								receiver.setCustomerAddress(customerAddress);
								receiver.setCustomerCity(customerCity);
								receiver.setCustomerState(customerState);
								receiver.setCustomerCountry(customerCountry);
								receiver.setCustomerOrderId(orderId);
								receiver.setCustomerPersonName("NMIMS " +customerName+ " - Center");
								receiver.setCustomerCompanyName("Student Number: " +studentNumber);
								informationCenterAddress=customerAddress;
						}	
						else
						{
							logger.info("Error: Cannot create shipment for this Disptach order as record does not match required criteria. Please check Self_Learning_Material_For_Student__c field");
							return "Error: Cannot create shipment for this Disptach order as record does not match required criteria. Please check Self_Learning_Material_For_Student__c field";
						}

						
						ResponseEntity<String> checkPinForReceiverResponse=deliveryObject.checkPinCode(customerPin,shippingMode);
						String pickupForReceiver=checkPinForReceiverResponse.getBody();
						if(!(pickupForReceiver.equalsIgnoreCase("Y")))
						{
							logger.info("No service available for the specified receiver pincode: "+customerPin);
							return "No service available for the specified receiver pincode: "+customerPin;
						}
						
						ProductDetails product=new ProductDetails();
						Date today=new Date();
						SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
						String istDate = sdf.format(today);
						product.setOrderDate(istDate);
						product.setPaymentMode("Prepaid");
						
						if(orderType.equalsIgnoreCase("Student Order") && skuType.equalsIgnoreCase("Kit"))
						{
							product.setProductDescription("Study Kit");  
							product.setShippingMode("Surface");
							product.setTotalAmount("3000");
						}
						else if(orderType.equalsIgnoreCase("Single Book") || (orderType.equalsIgnoreCase("Student Order") && skuType.equalsIgnoreCase("Book")))
						{
							product.setProductDescription("Book");  
							product.setShippingMode("Express");
							product.setTotalAmount("500");
						}
						
						ShipmentRequest request=new ShipmentRequest();
						request.setShipper(shipper);
						request.setReceiver(receiver);
						request.setProductDetails(product);
	
						requestList.add(request);
					}
					if(qResult.isDone())
					done=true;
					else
					{
						logger.info("Querying more...");
						qResult=connection.queryMore(qResult.getQueryLocator());
					}
				}
				ArrayList<SObject> updateOrders = new ArrayList<SObject>();
				for(ShipmentRequest shipRequest:requestList)
				{
					ResponseEntity<String> res=deliveryObject.orderCreation(shipRequest);
					trackingNumber=processReply(res,updateOrders,shippingMode,sku,doName);
				}
				if(updateOrders.size() > 0)
				{
					SObject[] records = new  SObject[updateOrders.size()];
					records = updateOrders.toArray(records);
					updateDORecordsInSFDC(records);
				}
				mailer.sendDispatchInitiatedEmailForDelhivery(studentEmailId, studentNumber, semester, skuType, receiver, trackingNumber, customerProgram, placeToShip, informationCenterAddress,orderType);
				logger.info("success");
				return "success";
			}
			else
			{
				logger.info("Error: Cannot create shipment for this Disptach order as record does not match required criteria. Please check FedEx flag or Status");
				return "Error: Cannot create shipment for this Disptach order as record does not match required criteria. Please check FedEx flag or Status";
			}
		}
		catch (ApiFault apiEx) 
		{
			//apiEx.printStackTrace();
			logger.info("Caught ApiFault :"+apiEx);
			return "apiFault";
		 }
		catch (ConnectionException ce) 
		{ 
			logger.info("Caught ConnectionException :"+ce);
			return "apiFault";
		}
		catch(Exception e)
		{
			//e.printStackTrace();
			try
			{
				mailer.sendFedExOrderCreationErrorEmailFromDelhivery(ExceptionUtils.getStackTrace(e));
				logger.info("Exception in orderCreation: "+e);
				return e.getMessage();
			}
			catch(Exception ex)
			{
				logger.info("Exception in orderCreation: "+ex);
				return ex.getMessage();
			}
		}
	}
	
	public String processReply(ResponseEntity<String> response,ArrayList<SObject> updateOrder,String shippingMode,String sku,String doName) throws Exception
	{
		try
		{
			if(response.getStatusCode().is4xxClientError())
			{
				//System.out.println("error process reply");
				//System.out.println(response.getBody());
				String errorMessage=response.getBody();
				logger.info("Error response from delhivery: "+errorMessage);
				throw new Exception(errorMessage);
			}
			else
			{
				//System.out.println("successfull process reply");
				logger.info("successfull process reply");
				JsonObject obj=new JsonParser().parse(response.getBody()).getAsJsonObject();
				String trackingNumber=obj.get("packages").getAsJsonArray().get(0).getAsJsonObject().get("waybill").getAsString();
				String orderId=obj.get("packages").getAsJsonArray().get(0).getAsJsonObject().get("refnum").getAsString();
				SObject sObject = new SObject();
				sObject.setType("Dispatch_Order__c");
				sObject.setId(orderId);
				sObject.setField("Tracking_Number__c", trackingNumber);
				sObject.setField("Fed_Ex_Shipment_Created__c", true);
				sObject.setField("Name_Of_Other_Courier_Service__c", "Delhivery");
				sObject.setField("Other_Courier_Services_Dispatch__c", "No");
//				if(shippingMode.equalsIgnoreCase("S"))
//				{
//					sObject.setField("Cost_Of_Shipement__c", "3000");
//				}
//				else if(shippingMode.equalsIgnoreCase("E"))
//				{
//					sObject.setField("Cost_Of_Shipement__c", "500");
//				}
				//sObject.setField("Other_Courier_Services_Dispatch__c", "Yes");
				String randomString = RandomStringUtils.randomAlphanumeric(12);
				//String filePath = ADMISSION_DOCUMENTS_PATH + "AirwayBills/" + orderId + "_" + randomString + ".pdf";
				String filePath=DelhiveryDocuments+"PackingSlipFolder/"+orderId + "_" + randomString + ".pdf";
				String keyName="DelhiverySlip/"+orderId + "_" + randomString + ".pdf";
				String bucketName=DelhiveryBucket;
				String folderPath="DelhiverySlip/";
				String filePreviewPath=barcode(trackingNumber,filePath,keyName,bucketName,folderPath,shippingMode,sku,doName);
				//String filePreviewPath = SFDC_DOCUMENTS_BASE_PATH + "AirwayBills/" + orderId + "_" + randomString + ".pdf";
				sObject.setField("Airway_Bill_Image__c", filePreviewPath);
				updateOrder.add(sObject);
				logger.info("success response from delhivery");
				logger.info("Tracking number for "+orderId+" is "+trackingNumber);
				return trackingNumber;
			}
		}
		catch(Exception e)
		{
			//e.printStackTrace();
			logger.info("Exception in process reply :"+e);
			throw e;
		}
	}
	
	public void updateDORecordsInSFDC(SObject[] records) throws Exception
	{
		try 
		{ 
			// update the records in Salesforce.com
			SaveResult[] saveResults = connection.update(records);
			// check the returned results for any errors
			for (int i=0; i< saveResults.length; i++) 
			{
				if (saveResults[i].isSuccess()) 
				{
					//System.out.println(i+". Successfully updated record - Id: " + saveResults[i].getId());
					logger.info(i+". Successfully updated record - Id: " + saveResults[i].getId());
				}
				else 
				{
					String errorMessage = "ERROR: ";
					Error[] errors = saveResults[i].getErrors();
					for (int j=0; j< errors.length; j++) 
					{
						errorMessage += errors[j].getMessage() + " ";
						//System.out.println("ERROR updating record: " + errors[j].getMessage());
					}
					logger.info("ERROR updating record: " + errorMessage);
					throw new Exception(errorMessage);
				}   
			}
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			logger.info("Exception in update Records :"+e);
			throw e;
		}
	}
	
	public ResponseEntity<String> shippingChargesCalculation(String mode,String weight,String origin_pin,String destination_pin,String status) throws IOException
	{
		return deliveryObject.shippingChargesCalculation(mode,destination_pin, status,origin_pin);
	}
	
	public ResponseEntity<String> slipCreation(String trackingId,String shippingMode) throws IOException
	{
		return deliveryObject.slipCreation(trackingId,shippingMode);
	}
	
	public String barcode(String trackingId,String filePath,String keyName,String bucketName,String folderPath,String shippingMode,String sku,String doName) throws IOException
	{
		String HTML="";
		String deleteFilePath=filePath;
		FileOutputStream fos=new FileOutputStream(filePath);
		try
		{
			
			ResponseEntity<String>response=deliveryObject.slipCreation(trackingId,shippingMode);
			if(response.getStatusCode().is4xxClientError())
			{
				logger.info("Error in slip creation api for tracking Number : "+trackingId);
				throw new Exception("Error in slip creation for tracking Number : "+trackingId);
			}
			else
			{
				String encodedfile1 = null;
				String encodedfile2 = null;
				InputStream fileInputStreamReader1= new URL("https://d5wwg9za7ghrw.cloudfront.net/nmims_logo.jpg").openStream();
				byte[] bytes1= IOUtils.toByteArray(fileInputStreamReader1);
				encodedfile1 =  Base64.encodeBase64String(bytes1);
		        InputStream fileInputStreamReader2= new URL("https://d5wwg9za7ghrw.cloudfront.net/delhiveryNewLogo.jpg").openStream();
		        byte[] bytes2= IOUtils.toByteArray(fileInputStreamReader2);
		        encodedfile2 =  Base64.encodeBase64String(bytes2);
	            JsonObject jsonObj=new JsonParser().parse(response.getBody()).getAsJsonObject();
	            JsonObject packages=jsonObj.get("packages").getAsJsonArray().get(0).getAsJsonObject();
	            String mot="";
	            String symbol="";
	            String destination="";
	            String sortCode="";
	            if(!(packages.get("sort_code").isJsonNull()))
	            	sortCode=packages.get("sort_code").getAsString();
	            if(!(packages.get("destination").isJsonNull()))
	            	destination=packages.get("destination").getAsString();
	            
	            Currency curr = Currency.getInstance("INR");
	            symbol=curr.getSymbol();
//	            Locale loc = new Locale("en", "IN");
//	            Currency curr = Currency.getInstance(loc);
//	       	    symbol=curr.getSymbol(loc);
	            if(packages.get("mot").getAsString().equalsIgnoreCase("S"))
	            	mot="Surface";
	            else
	            	mot="Express";
	            
				HTML = "<html>\r\n" + 
						"<head>\r\n" + 
						"    <title>"+trackingId+"</title>\r\n" + 
						"</head>\r\n" + 
						"<body style=\"font-size: 16px;width:90%;font-family: Arial, Helvetica, sans-serif;\">\r\n" + 
						"    <table cellspacing=\"0px\" style=\"border:1px black solid;width:500px;margin:auto\">\r\n" + 
						"        <tr style=\"text-align: center;\">\r\n" + 
						"            <td colspan=\"1\" style=\"border:1px black solid;padding:0px\"><img style=\"height:80px;width: 90%\" src=\"data:image/jpg;base64,"+encodedfile1+"\"></td>\r\n" +
						"            <td colspan=\"2\" style=\"border:1px black solid;padding:0px\"><img style=\"height:80px;width: 90%\" src=\"data:image/jpg;base64,"+encodedfile2+"\"></td>\r\n" + 
						"        </tr>\r\n" + 
						"        <tr>\r\n" + 
						"            <td colspan=\"3\" style=\"border:1px black solid;padding:0px\" >\r\n" + 
						"            <div style=\"padding-top: 10px;padding-bottom: 15px;text-align: center;\">\r\n" + 
						"                <img style=\"height:120px;width: 70%;filter:contrast(1.75);\"  src=\""+packages.get("barcode").getAsString()+"\">\r\n" + 
						"            </div>\r\n" + 
						"            <div>\r\n" + 
						"                <span style=\"padding-left:5px;padding-right:350px\">"+packages.get("pin").getAsString()+"</span>\r\n" + 
						"                <span style=\"font-weight:700;font-size: 16px;\">"+sortCode+"</span>\r\n" + 
						"            </div>\r\n" + 
						"            </td>\r\n" + 
						"        </tr>\r\n" + 
						"        <tr>\r\n" + 
						"            <td colspan=\"2\" style=\"border:1px black solid;padding:0px\">\r\n" + 
						"                <div style=\"font-weight:600;font-size: 15px;padding-top: 5px;\">Shipping Address:</div>\r\n" + 
						"                <div style=\"font-weight:700;font-size: 15px;padding-top: 5px;padding-bottom: 10px;\">"+packages.get("name").getAsString().toUpperCase()+"</div>\r\n" + 
						"                <div>"+packages.get("address").getAsString()+"</div>\r\n" + 
						"                <div>"+destination+"</div>\r\n" + 
						"                <div style=\"font-weight:600;padding-top: 10px;\">PIN: "+packages.get("pin").getAsString()+"</div>\r\n" + 
						"            </td>\r\n" + 
						"            <td  style=\"border:1px black solid;padding:0px;text-align: center;\">\r\n" + 
						"                <div style=\"font-weight: 700;font-size: 25px;\">"+packages.get("pt").getAsString()+"</div>\r\n" + 
						"                <div style=\"font-weight: 700;font-size: 25px;\">"+mot+"</div>\r\n" + 
						"            </td>\r\n" + 
						"        </tr>\r\n" + 
						"        <tr>\r\n" + 
						"            <td colspan=\"3\" style=\"border:1px black solid;padding:0px\">\r\n" + 
						"                <div style=\"padding-top: 10px;font-weight: 700;\">Seller: "+packages.get("snm").getAsString()+"</div>\r\n" + 
						"                <div style=\"padding-top: 5px;padding-bottom: 5px;\">Address: "+packages.get("sadd").getAsString()+"</div>\r\n" + 
						"            </td>\r\n" + 
						"        </tr>\r\n" + 
						"        <tr style=\"text-align: center;\">\r\n" + 
						"            <td style=\"width:33%;border:1px black solid;padding:0px\">Product</td>\r\n" + 
						"            <td style=\"width:33%;border:1px black solid;padding:0px\">Price</td>\r\n" + 
						"            <td style=\"width:33%;border:1px black solid;padding:0px\">Total</td>\r\n" + 
						"        </tr>\r\n" + 
						"        <tr  style=\"text-align: center;\">\r\n" + 
						"			 <td style=\"width:33%;border:1px black solid;padding-top:20px;padding-bottom: 20px\">\r\n" + 
	            		"  				<div style=\"font-weight: 600\">"+packages.get("prd").getAsString()+"</div>\r\n" + 
	            		"  				<div style=\"font-weight: 500\">"+sku+"</div>\r\n" + 
	            		"            </td>\r\n" + 
						"            <td style=\"width:33%;border:1px black solid;padding-top:20px;padding-bottom: 20px\">"+symbol+packages.get("rs").getAsString()+"</td>\r\n" + 
						"            <td style=\"width:33%;border:1px black solid;padding-top:20px;padding-bottom: 20px\">"+symbol+packages.get("rs").getAsString()+"</td>\r\n" + 
						"        </tr>\r\n" + 
						"        <tr  style=\"text-align: center;font-weight: 800;font-size: 16px;\">\r\n" + 
						"            <td style=\"width:33%;border:1px black solid;padding-top:10px;padding-bottom: 10px;\">Total</td>\r\n" + 
						"            <td style=\"width:33%;border:1px black solid;padding-top:10px;padding-bottom: 10px\">"+symbol+packages.get("rs").getAsString()+"</td>\r\n" + 
						"            <td style=\"width:33%;border:1px black solid;padding-top:10px;padding-bottom: 10px\">"+symbol+packages.get("rs").getAsString()+"</td>\r\n" + 
						"        </tr>\r\n" + 
						"        <tr >\r\n" + 
						"            <td colspan=\"3\" style=\"width:33%;border:1px black solid\">\r\n" + 
						"                <div style=\"text-align: center;padding-top: 8px;padding-bottom: 8px;\">\r\n" + 
						"                    <img style=\"height:100px;width: 70%;filter: contrast(120%);\" src=\""+packages.get("oid_barcode").getAsString()+"\">\r\n" + 
						"                </div>\r\n" + 
						"  				<div style=\"text-align: center;padding-top: 5px;padding-bottom: 5px;font-weight: 600\">"+doName+"</div>\r\n" + 
						"            </td>\r\n" + 
						"        </tr>\r\n" + 
						"        <tr>\r\n" + 
						"            <td colspan=\"3\" style=\"width:33%;border:1px black solid\">\r\n" + 
						"                Return Address: "+packages.get("radd").getAsString() + 
						"            </td>\r\n" + 
						"        </tr>\r\n" + 
						"    </table>\r\n" + 
						"</body>\r\n" + 
						"</html>";
				
				HtmlConverter.convertToPdf(HTML, fos);
				logger.info("Barcode generated successfully with path "+filePath);
				try {
					filePath = URLEncoder.encode(filePath,StandardCharsets.UTF_8.toString());
				}	
				catch (Exception ex) {
					logger.info("Exception in encoding file"+ex);
				}
				String url = AWS_UPLOAD_URL_LOCAL_SALESFORCE + "upload?public=true&filePath="+ filePath +"&keyName="+ keyName +"&bucketName="+bucketName;
				logger.info("Aws upload url:" + url);
				long t1=System.currentTimeMillis();
				String fileUrl = awsHelper.uploadOnAWS(url);
				long t2=System.currentTimeMillis();
				long t3=t2-t1;
				logger.info("Response time from uploadOnAws in milli seconds:"+t3);
				if(fileUrl != null) {
					logger.info("successfull response from s3 helper:"+fileUrl);
					return  fileUrl;
				}else {
					logger.info("failed to upload file on aws");
					return "failed to upload file on aws";
				}
//				HashMap<String,String> s3Response = awsHelper.uploadLocalFile(filePath,keyName, bucketName, folderPath);
//				if(s3Response.get("status").equalsIgnoreCase("error"))
//					logger.info("error response from s3 helper"+s3Response.get("url"));
//				else
//					logger.info("successfull reponse from s3 helper"+s3Response.get("url"));
//				return s3Response.get("url");
				
			}
		}
		catch(Exception e)
		{
			logger.info("Exception in barcode is :"+e);
			//e.printStackTrace();
			return e.getMessage();
		}
		finally
		{
			try
			{
				fos.close();
				File f=new File(deleteFilePath);
				f.delete();
			}
			catch(Exception fe)
			{
				logger.info("Exception while deleting file:"+fe);
			}
		}
	}
	
	public String documentsMergeForDelhivery(HttpServletResponse response,String commaSepratedIds,int retryCount)
	{
		logger.info("documentsMergeForDelhivery called");
		for(int i=0;i<=retryCount;i++)
		{
			//System.out.println(i);
			logger.info("documentsMerge method called for i="+i);
			String s=documentsMerge(response,commaSepratedIds);
			if(!(s.equalsIgnoreCase("apiFault")))
			return s;
			init();
		}
		return "error in creating connection with salesforce";
	}
	public String documentsMerge(HttpServletResponse response,String commaSepratedIds)
	{
		QueryResult qResult = new QueryResult();
		try
		{
			
			String soqlQuery="Select Id,Student_Number__c,Airway_Bill_Image__c from Dispatch_Order__c where Fed_Ex_Shipment_Created__c= true and Id in (" + commaSepratedIds + ") and Name_Of_Other_Courier_Service__c= 'Delhivery' ";
			qResult = connection.query(soqlQuery);
			logger.info("Number of rows returned for zip folder creation: "+qResult.getSize());
			if(qResult.getSize()>0)
			{
				ArrayList<String> studentNoFileList = new ArrayList<>();
				boolean done=false;
				while(!done)
				{
					SObject[] records = qResult.getRecords();
					for (int i = 0; i < records.length; ++i) 
					{
						SObject s = (SObject) records[i];
						String id=(String)s.getField("Id");
						String studentNumber=(String)s.getField("Student_Number__c");
						String fullUrl=(String)s.getField("Airway_Bill_Image__c");
						//System.out.println(fullUrl);
						logger.info("Full packing slip url for order id:"+id+" is "+fullUrl);
						if(fullUrl==null || !fullUrl.contains("DelhiverySlip"))
							continue;
						String destinationFilePath=DelhiveryDocuments+"MergeFolder/"+id+".pdf";
						String keyName=fullUrl.substring(fullUrl.indexOf("DelhiverySlip"), fullUrl.length());
						HashMap<String,String> s3Response=awsHelper.downloadFile(destinationFilePath,keyName,DelhiveryBucket);
						if(s3Response.get("status").equalsIgnoreCase("error"))
							logger.info("error in downloading file for id :"+id);
						else
						{
							studentNoFileList.add(destinationFilePath);
						}
					}
					if(qResult.isDone())
						done=true;
					else
					{
						logger.info("Querying more...");
						qResult=connection.queryMore(qResult.getQueryLocator());
					}
				}
				
//				byte[] buffer = new byte[1024];
				//String zipFileName = "DelhiveryDocumentsMerged-Download.zip";
				
//				FileOutputStream fos = new FileOutputStream(DelhiveryDocuments+"DelhiveryDocumentsMerged-Download.zip");
//				ZipOutputStream zos = new ZipOutputStream(fos);
//				for (int i=0; i < studentNoFileList.size(); i++) {
//					File srcFile = new File(studentNoFileList.get(i));
//					FileInputStream fis = new FileInputStream(srcFile);
//					// begin writing a new ZIP entry, positions the stream to the start of the entry data
//					zos.putNextEntry(new ZipEntry(srcFile.getName()));
//					int length;
//					while ((length = fis.read(buffer)) > 0) {
//						zos.write(buffer, 0, length);
//					}
//					zos.closeEntry();
//					// close the InputStream
//					fis.close();
//				}
//				zos.close();
				
				//Download Zip file created
//				File fileToDownload = new File(DelhiveryDocuments+"DelhiveryDocumentsMerged-Download.zip");
//				InputStream inputStream = new FileInputStream(fileToDownload);
//				response.setContentType("application/zip");
//				response.setHeader("Content-Disposition", "attachment; filename=\""+zipFileName+"\"");
//				IOUtils.copy(inputStream, response.getOutputStream());
//				response.flushBuffer();
				Date date = Calendar.getInstance().getTime();  
		        DateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");  
		        String strDate = dateFormat.format(date); 
				String pdfFileName = "DocumentsMergedFolder/DelhiveryDocumentsMerged-"+strDate+".pdf";
				PDFMergerUtility merger=new PDFMergerUtility();
				for (int i=0; i < studentNoFileList.size(); i++)
				{
					File srcFile = new File(studentNoFileList.get(i));
					merger.addSource(srcFile);
				}
				merger.setDestinationFileName(DelhiveryDocuments+pdfFileName);
				merger.mergeDocuments(null);
				String keyName="DocumentsMerged/DelhiveryDocumentsMerged-"+strDate+".pdf";
				String bucketName=DelhiveryBucket;
				String folderPath="DocumentsMerged/";
				String filePath=DelhiveryDocuments+pdfFileName;
				HashMap<String,String> s3Response = awsHelper.uploadLocalFile(filePath,keyName, bucketName, folderPath);
				if(s3Response.get("status").equalsIgnoreCase("error"))
					logger.info("error response from s3 helper"+s3Response.get("url"));
				else
					logger.info("successfull reponse from s3 helper"+s3Response.get("url"));
				return s3Response.get("url");
						
//				//Download pdf file created
//				File fileToDownload = new File(DelhiveryDocuments+"DelhiveryDocumentsMerged-Download.pdf");
//				InputStream inputStream = new FileInputStream(fileToDownload);
//				response.setContentType("application/pdf");
//				response.setHeader("Content-Disposition", "attachment; filename=\""+pdfFileName+"\"");
//				IOUtils.copy(inputStream, response.getOutputStream());
//				response.flushBuffer();
				
			}
			else
			{
				logger.info("No records found for pdf folder creation");
				return "No records found for pdf folder creation";
			}
		}
		catch(ApiFault apiEx)
		{
			apiEx.printStackTrace();
			logger.info("Caught ApiFault :"+apiEx);
			return "apiFault";
		}
		catch(Exception e)
		{
			e.printStackTrace();
			logger.info("Exception is :"+e);
			return "error message is :"+e.getMessage();
		}
	}
	
	public String pdfCreationForDelhivery(String commaSepratedIds)
	{
		
		try
		{
			QueryResult qResult = new QueryResult();
			String soqlQuery="select Id,Name,Stock_Keeping_Unit__r.Name,Student_Number__c,Order_Type__c,SKU_Type__c,Tracking_Number__c,Cost_Of_Shipement__c from Dispatch_Order__c where Id in (" + commaSepratedIds + ")";
			qResult = connection.query(soqlQuery);
			logger.info("Number of rows returned for file path change: "+qResult.getSize());
			//TreeMap<String,String>filePathIdMap=new TreeMap<String,String>();
			if(qResult.getSize()>0)
			{
				HashMap<String,StudentBean> orderIdStudentMap=new HashMap<String,StudentBean>();
				boolean done=false;
				while(!done)
				{
					SObject[] records = qResult.getRecords();
					for (int i = 0; i < records.length; ++i) 
					{
						SObject s = (SObject) records[i];
						String orderId=(String)s.getField("Id");
						String trackingId=(String)s.getField("Tracking_Number__c");
						String skuType  = (String)s.getField("SKU_Type__c");
						String orderType=(String)s.getField("Order_Type__c");
						String randomString = RandomStringUtils.randomAlphanumeric(12);
						String filePath=DelhiveryDocuments+"PackingSlipFolder/"+orderId + "_" + randomString + ".pdf";
						String keyName="DelhiverySlip/"+orderId + "_" + randomString + ".pdf";
						String bucketName=DelhiveryBucket;
						String folderPath="DelhiverySlip/";
						XmlObject skuUnit=(XmlObject)s.getField("Stock_Keeping_Unit__r");
						String sku = (String)skuUnit.getChild("Name").getValue();
						String doName=(String)s.getField("Name");
						if(StringUtils.isBlank(sku))
							sku="";
						if(StringUtils.isBlank(doName))
							doName="";
						String shippingMode="";
						if(orderType.equalsIgnoreCase("Student Order") && skuType.equalsIgnoreCase("Kit"))
							shippingMode="S";
						else if(orderType.equalsIgnoreCase("Single Book") || (orderType.equalsIgnoreCase("Student Order") && skuType.equalsIgnoreCase("Book")))
							shippingMode="E";
						else 
						{
							logger.info("no such order type or skuType found for order Id:"+orderId);
							continue;
						}
						StudentBean student=new StudentBean();
						student.setTrackingNumber(trackingId);
						student.setShippingMode(shippingMode);
						String filePreviewPath=barcode(trackingId,filePath,keyName,bucketName,folderPath,shippingMode,sku,doName);
						student.setFilePath(filePreviewPath);
						logger.info("file path for orderId:"+orderId+" is "+filePreviewPath);
						orderIdStudentMap.put(orderId, student);
					}
					if(qResult.isDone())
						done=true;
					else
					{
						logger.info("Querying more...");
						qResult=connection.queryMore(qResult.getQueryLocator());
					}
				}
				
				ArrayList<SObject> trackUpdate=new ArrayList<SObject>();
				for(Map.Entry<String,StudentBean> entry : orderIdStudentMap.entrySet())
				{
					updateSfdcFields(trackUpdate,entry.getKey(),entry.getValue());
				}
				
				if(trackUpdate.size() > 0)
				{	 
					connection.setAllOrNoneHeader(false);
					for(int i = 0; i < trackUpdate.size() ; i= i + 10)
					{
						int lastIndex =  (i + 10) < trackUpdate.size() ? (i + 10) : trackUpdate.size();
						ArrayList<SObject> trackUpdateSublist =  new ArrayList<SObject>(trackUpdate.subList(i, lastIndex));
						
						SObject[] records = new  SObject[trackUpdateSublist.size()];
						records = trackUpdateSublist.toArray(records);
						updateFilePathInSFDC(records);
					}
				}
				return "success";
			}
			else
			{
				return "No records found";
			}
		}
		catch(Exception e)
		{
			logger.info("pdfCreationForDelhivery exception:"+e);
			return "pdfCreationForDelhivery exception:"+e;
		}
	}
	
	public void updateSfdcFields(ArrayList<SObject> trackUpdate,String orderId,StudentBean student)
	{
		SObject sObject = new SObject();
		sObject.setType("Dispatch_Order__c");
		sObject.setId(orderId);
		sObject.setField("Airway_Bill_Image__c",student.getFilePath());
		sObject.setField("Tracking_Number__c", student.getTrackingNumber());
		sObject.setField("Fed_Ex_Shipment_Created__c", true);
		sObject.setField("Name_Of_Other_Courier_Service__c", "Delhivery");
		sObject.setField("Other_Courier_Services_Dispatch__c", "No");
//		if(student.getShippingMode().equalsIgnoreCase("S"))
//		{
//			sObject.setField("Cost_Of_Shipement__c", "3000");
//		}
//		else if(student.getShippingMode().equalsIgnoreCase("E"))
//		{
//			sObject.setField("Cost_Of_Shipement__c", "500");
//		}
		trackUpdate.add(sObject);
		return;
	}
	
	public void updateFilePathInSFDC(SObject[] records) throws Exception
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
					//System.out.println(i+". Successfully updated record - Id: " + saveResults[i].getId());
					logger.info(i+". Successfully updated record - Id: " + orderId);
				}
				else 
				{
					String errorMessage = "ERROR: ";
					Error[] errors = saveResults[i].getErrors();
					for (int j=0; j< errors.length; j++) 
					{
						errorMessage += errors[j].getMessage() + " ";
					}
					logger.info("Error updating Record ID "+orderId+" with message "+errorMessage);
				}   
			}
		} 
		catch (Exception e)
		{
			logger.info("Exception in updateFilePathInSFDC :"+e);
			throw new IllegalArgumentException("Error in connection.update,check field");
		}
	}
	
	@Override
	public DispatchedDocumentMergeResponseBean dispatchOrderDocumnetMerge(List<FedExMergerBean> list) {
		List<FedExMergerBean> errorList = new ArrayList<FedExMergerBean>(); 
		List<FedExMergerBean> successList = new ArrayList<FedExMergerBean>(); 
		DispatchedDocumentMergeResponseBean mergerBean = new DispatchedDocumentMergeResponseBean();
		String deleteFilePath="";
		try {
//        	Object[] files = bean.getUrl().toArray();
          
            Document PDFCombineUsingJava = new Document();
            
            Date date = Calendar.getInstance().getTime();  
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");  
            String strDate = dateFormat.format(date); 
            String filePath=FEDEX_MERGE_DOC+"/"+strDate+".pdf";
            deleteFilePath=FEDEX_MERGE_DOC+"/"+strDate+".pdf";
            String keyName="DocumentsMerged/"+strDate+".pdf";
    		String bucketName=DelhiveryBucket;
            PdfCopy copy = new PdfCopy(PDFCombineUsingJava, new FileOutputStream(filePath));
            PDFCombineUsingJava.open();
          
            for(FedExMergerBean bean : list) {
    		  try {
        		  int number_of_pages,enteredNo = bean.getPageNo();
        		  PdfReader ReadInputPDF = new PdfReader(bean.getUrl()); 
        		  number_of_pages = ReadInputPDF.getNumberOfPages();
        		  
        		  if(enteredNo < number_of_pages){
        			  number_of_pages = enteredNo;
        		  }
        		  for (int page = 0; page < number_of_pages; ) {
        			  copy.addPage(copy.getImportedPage(ReadInputPDF, ++page));
        			  successList.add(bean);
        		  }
    		  } catch (Exception e) {
    			  errorList.add(getMergerErrorBean("Invalid url on row no : "+bean.getRow()));
    			  continue;
    			}
            }
            mergerBean.setErrorList(errorList);
            mergerBean.setSuccessList(successList);
            PDFCombineUsingJava.close();
            try {
				filePath = URLEncoder.encode(filePath,StandardCharsets.UTF_8.toString());
			}	
			catch (Exception ex) {
				logger.info("Exception in encoding file"+ex);
			}
			String url = AWS_UPLOAD_URL_LOCAL_SALESFORCE + "upload?public=true&filePath="+ filePath +"&keyName="+ keyName +"&bucketName="+bucketName;
			logger.info("Aws upload url:" + url);
			long t1=System.currentTimeMillis();
			String fileUrl = awsHelper.uploadOnAWS(url);
			long t2=System.currentTimeMillis();
			long t3=t2-t1;
			logger.info("Response time from uploadOnAws in milli seconds:"+t3);
			if(fileUrl != null) {
				logger.info("successfull response from s3 helper:"+fileUrl);
				mergerBean.setUrl(fileUrl);
			}
			else {
				logger.info("failed to upload file on aws");
				mergerBean.setUrl("failed to upload file on aws");
			}
		}
		catch (Exception i)
		{
			logger.info("Exception in dispatchOrderDocument merge is :"+i);
			i.printStackTrace(); 
			//System.out.println(i);
		}
	        finally
	        {
	        	try
				{
					File f=new File(deleteFilePath);
					f.delete();
				}
				catch(Exception fe)
				{
					logger.info("Exception while deleting file:"+fe);
				}
	        }
		logger.info("Merged pdf url is :"+mergerBean.getUrl());
		System.out.println("documents merged...");
		
		return  mergerBean;
	}
	
	private FedExMergerBean getMergerErrorBean(String errorMessage) {
		FedExMergerBean errorBean = new FedExMergerBean();
		errorBean.setErrorMessage(errorMessage);
		
		return errorBean;
	}
	
	@Override
	public DispatchedDocumentMergeResponseBean createFileFromExcelRecords(MultipartFile file)  {
		ExcelHelper excelHelper = new ExcelHelper();
		DispatchedDocumentMergeResponseBean mergedBean = new DispatchedDocumentMergeResponseBean();
		try {
			DispatchedDocumentMergeResponseBean mergerBean = excelHelper.readDispatchOrderDocumentMergeExcel(file);
			
			if(mergerBean.getErrorList().size() > 0) 
				return getErrorMessage(mergerBean.getErrorList());
			
			mergedBean = getDispatchOrderDocumentMergedBean(mergerBean.getSuccessList());
		} catch (Exception e) {
			logger.error("Error in uploading DispatchOrderDocumentMerge excel records due to {} ", e);
		}
		return mergedBean;
	}
	
	private DispatchedDocumentMergeResponseBean getDispatchOrderDocumentMergedBean(List<FedExMergerBean> successList) {
		DispatchedDocumentMergeResponseBean mergerResponse = new DispatchedDocumentMergeResponseBean();
		DispatchedDocumentMergeResponseBean errorBean = new DispatchedDocumentMergeResponseBean();
		DispatchedDocumentMergeResponseBean successBean = new DispatchedDocumentMergeResponseBean();
		
		DispatchedDocumentMergeResponseBean mergedBean = dispatchOrderDocumnetMerge(successList);
		
		if(mergedBean.getErrorList().size() > 0) {
			errorBean = getErrorMessage((mergedBean.getErrorList()));
			mergerResponse.setErrorMessage(errorBean.getErrorMessage());
		}
		
		if(mergedBean.getSuccessList().size() > 0) {
			successBean = getSuccessMessage(mergedBean.getSuccessList(), mergedBean);
			mergerResponse.setSuccessMessage(successBean.getSuccessMessage());
			mergerResponse.setUrl(successBean.getUrl());
		}
		return mergerResponse;
	}
	
	public DispatchedDocumentMergeResponseBean getErrorMessage(List<FedExMergerBean> errorList) {
		DispatchedDocumentMergeResponseBean errorBean = new DispatchedDocumentMergeResponseBean();
		StringBuilder errorMessage = new StringBuilder();
		String separator = "";
		for(FedExMergerBean bean : errorList) {
			 errorMessage.append(separator).append(bean.getErrorMessage());
			 separator=",<br>";
		}
		errorBean.setErrorMessage(errorMessage.toString());
		return errorBean;
	}

	public DispatchedDocumentMergeResponseBean getSuccessMessage(List<FedExMergerBean> successList,DispatchedDocumentMergeResponseBean mergedResponse) {
		DispatchedDocumentMergeResponseBean successBean = new DispatchedDocumentMergeResponseBean();
		
		successBean.setSuccessMessage("dispatchOrderDocumentMerge generated successfully for "+successList.size()+" records"
		+ ". Please click Download link below to get file.");
		successBean.setUrl(mergedResponse.getUrl());
    	
		return successBean;
	}
	
	private String deliveredSMSBody(String deliveredDateTime) {
		
		String message = "Your Study Kit was delivered to your registered address on "+deliveredDateTime+". "
				+ "If you have not received the shipment please contact the University on "
				+ "ngascelogistics@nmims.edu within "+DELIVERED_TIME+" of receiving this message.";
		
		return message;
	}
	
	@Override
	public void sendDeliveredInitiatedSMS(String mobileNumber, String message) {
		 try {
		 	  smsSender.sendDeliveredInitiatedSMS(mobileNumber, message);
		 	  
			} catch (Exception e) {
				logger.error("Error while sending Delivered SMS for mobileNumber {} and message {} due to {}", mobileNumber, message, e);
			}
	}
	
	public void sendDeliveredSMSFromDelhivery(List<StudentBean> deliveredStudentList) {
		for (StudentBean deliveredStudent : deliveredStudentList) {
			String mobileNumber = deliveredStudent.getMobile();
			String message = deliveredSMSBody(deliveredStudent.getDeliveredDateTime());
			
			sendDeliveredInitiatedSMS(mobileNumber, message);
		}
	}
	
}
