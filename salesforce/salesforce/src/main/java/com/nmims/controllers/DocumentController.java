package com.nmims.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import java.text.SimpleDateFormat;

import java.text.Normalizer;
import java.text.Normalizer.Form;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.SpringVersion;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;


import com.nmims.beans.AmazonS3Bean;
import com.nmims.beans.DocumentBean;
import com.nmims.beans.DocumentFileSet;
import com.nmims.beans.StudentBean;

import com.nmims.services.AmazonS3Service;
import com.nmims.util.fileUtils;
import com.nmims.beans.CaseBean;
import com.nmims.beans.DocumentBean;
import com.nmims.beans.DocumentFileSet;
import com.nmims.beans.StudentBean;
import com.nmims.helpers.AmazonS3Helper;
import com.nmims.helpers.MailSender;

import com.nmims.helpers.SFConnection;
import com.sforce.async.BulkConnection;
import com.sforce.async.JobInfo;
import com.sforce.async.OperationEnum;
import com.sforce.soap.partner.Connector;
import com.sforce.soap.partner.Error;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.SaveResult;
import com.sforce.soap.partner.fault.ApiFault;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;


@Controller
public class DocumentController extends BaseController{

	@Autowired
	ApplicationContext act;

	@Value( "${SFDC_USERID}" )
	private String SFDC_USERID;

	@Value( "${SFDC_PASSWORD_TOKEN}" )
	private String SFDC_PASSWORD_TOKEN;

	@Value( "${ADMISSION_DOCUMENTS_PATH}" )
	private String ADMISSION_DOCUMENTS_PATH;
	
	@Value( "${PROFILE_DOCUMENTS_PATH}" )
	private String PROFILE_DOCUMENTS_PATH;

	/*	@Value( "${STUDENT_PHOTOS_FROM_SFDC_PATH}" )
	private String STUDENT_PHOTOS_FROM_SFDC_PATH;*/

	@Value( "${SFDC_DOCUMENTS_BASE_PATH}" )
	private String SFDC_DOCUMENTS_BASE_PATH;
	
	@Value( "${SFDC_STUDENT_DOCUMENTS_BASE_PATH}" )
	private String SFDC_STUDENT_DOCUMENTS_BASE_PATH;

	@Value( "${AEP_DOCUMENTS_PATH}" )
	private String AEP_DOCUMENTS_PATH;

	@Value( "${AEP_DOCUMENTS_BASE_PATH}" )
	private String AEP_DOCUMENTS_BASE_PATH;

	@Value( "${AEP_SERVICE_UPLOAD_PATH}" )
	private String AEP_SERVICE_UPLOAD_PATH;

	@Value( "${AWS_AEP_SERVICE_BUCKET}" )
	private String AWS_AEP_SERVICE_BUCKET;

	private static final Logger aws_logger = LoggerFactory.getLogger("aws_document");
	
	private PartnerConnection connection;
	

	
	@Autowired
	MailSender mailer;
	
	@Autowired
	AmazonS3Service amazonS3Service;
	
	@Autowired 
	AmazonS3Helper amazonS3Helper;

	private static final String studentDocumentDrive = "E:";
	

	private static final String defaultStudentProfileDocument = "StudentProfileDocuments/";
	
	private static final String defaultStudentProfileDocument_bucket = "studentprofiledocuments";

	private static final Pattern NONLATIN = Pattern.compile("[^\\w_-]");  
	private static final Pattern SEPARATORS = Pattern.compile("[\\s\\p{Punct}&&[^-]]"); 
	private static final String studentPhotoDocument = "Student Photograph";

	public DocumentController(SFConnection sf) { 
		System.out.println("Document Controller initiated.");
		this.connection = sf.getConnection();
	} 
	public void init(){
		SFConnection sf= new SFConnection(SFDC_USERID,SFDC_PASSWORD_TOKEN);
		this.connection = sf.getConnection();
	}
	@RequestMapping(value = "/uploadStudentProfileDocument", method = RequestMethod.POST)
	public ResponseEntity<HashMap<String, String>> uploadStudentProfileDocument(HttpServletRequest request, HttpServletResponse response, @ModelAttribute DocumentBean filesSet, Model m){
		HashMap<String, String> responseBody = new HashMap<String,String>();
		try {
			System.out.println("=====>>>>  inside uploadInterViewDocument");
			MultipartFile file = filesSet.getFile();
			if(file != null && !file.isEmpty()){//Check if File was attached
				//String errorMessage = uploadDocumentForInterview(filesSet);
				//check file type
				//Check the file Extensions which are supported
				String result = checkFileType(file.getOriginalFilename());
				
				if(!StringUtils.isBlank(result)) {
					responseBody.put("status","fail");
					responseBody.put("message", result);
				}else {
					
					String fileUrl = uploadDocumentForInterviewTos3(filesSet);
					
					URL url = new URL(fileUrl);
					if(url.toURI() != null){
						filesSet.setDocumentURL(fileUrl);
						responseBody.put("status","success");
						responseBody.put("message", "successfully document uploaded");
						responseBody.put("documentUrl",filesSet.getDocumentURL());
					}else {
						responseBody.put("status","fail");
						responseBody.put("message", fileUrl);
					}
				
				/*	if(errorMessage != null) {
						responseBody.put("status","fail");
						responseBody.put("message", errorMessage);
					}else {
						responseBody.put("status","success");
						responseBody.put("message", "successfully document uploaded");
						responseBody.put("documentUrl",filesSet.getDocumentURL());
					}*/
				return new ResponseEntity<HashMap<String,String>>(responseBody,HttpStatus.OK);
			}
			}else {
			responseBody.put("status","fail");
			responseBody.put("message", "Kindly attached file to upload");
			}
			return new ResponseEntity<HashMap<String,String>>(responseBody,HttpStatus.OK);
		}
		catch (Exception e) {
			// TODO: handle exception
			responseBody.put("status","fail");
			responseBody.put("message", "Server error found,try again. ");
			return new ResponseEntity<HashMap<String,String>>(responseBody,HttpStatus.OK);
		}
	}
	
	
	private String uploadDocumentForInterview(DocumentBean documentBean) {
		InputStream inputStream = null;   
		OutputStream outputStream = null;   
		MultipartFile file = documentBean.getFile();

		String fileName = file.getOriginalFilename();   
		fileName = fileName.replaceFirst(" ", "_");
		fileName = fileName.replaceFirst("&", "_");

		System.out.println("fileName Size = "+file.getSize());

		System.out.println("fileName = "+fileName);

		try {  

			inputStream = file.getInputStream();  
			String randomNumber =RandomStringUtils.randomAlphanumeric(6);
			String filePath = PROFILE_DOCUMENTS_PATH + randomNumber+"_"+fileName;
			//Check if Folder exists, if not then create it
			File folderPath = new File(PROFILE_DOCUMENTS_PATH);
			if (!folderPath.exists()) {
				System.out.println("Making Folder");
				boolean created = folderPath.mkdirs();
				System.out.println("created = "+created);
			}   

			File newFile = new File(filePath);   
			
			outputStream = new FileOutputStream(newFile);   
			int read = 0;   
			byte[] bytes = new byte[1024];   

			while ((read = inputStream.read(bytes)) != -1) {   
				outputStream.write(bytes, 0, read);   
			}
			System.out.println("===========>>>>>  document URL : " + SFDC_STUDENT_DOCUMENTS_BASE_PATH + randomNumber+"_"+fileName);
			documentBean.setDocumentURL(SFDC_STUDENT_DOCUMENTS_BASE_PATH + randomNumber+"_"+fileName);
			outputStream.close();
			inputStream.close();
		} catch (IOException e) {   
			e.printStackTrace();   
			return e.getMessage();
		}   
		System.out.println("Uploded File");
		return null; //No Error
	}
	
	@RequestMapping(value = "/uploadDocumentForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView uploadDocumentForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		DocumentFileSet filesSet = new DocumentFileSet();
		String recordType = request.getParameter("type");
		if("leads".equalsIgnoreCase(recordType) || "lead".equalsIgnoreCase(recordType)){
			filesSet.setSfdcRecordId(request.getParameter("leadId"));
			filesSet.setRecordType("Lead");
		}else{
			filesSet.setSfdcRecordId(request.getParameter("accountId"));
			filesSet.setRecordType("Account");
		}

		readDocumentDetailsFromSFDC(filesSet);
		if(filesSet.getDocuments().size() == 0){
			String successMessage = (String)request.getAttribute("successMessage");
			if(successMessage == null){
				successMessage = "";
			}
			setSuccess(request, successMessage + " : No documents pending for submission");
		}

		request.setAttribute("filesSet",filesSet);
		m.addAttribute("filesSet",filesSet);
		return new ModelAndView("uploadDocuments");
	}

	private void readDocumentDetailsFromSFDC(DocumentFileSet filesSet) {
		/*
		 * System.out.println("Querying documents details from SFDC");
		 * 
		 * ConnectorConfig config = new ConnectorConfig();
		 * config.setUsername(SFDC_USERID); config.setPassword(SFDC_PASSWORD_TOKEN);
		 * 
		 * System.out.println("SFDC_USERID = "+SFDC_USERID);
		 * System.out.println("SFDC_PASSWORD_TOKEN = "+SFDC_PASSWORD_TOKEN);
		 */

		// display some current settings

		QueryResult queryResults = new QueryResult();

		try {
			/*
			 * connection = Connector.newConnection(config); // query for documents records
			 * for give SFDC record Id
			 * System.out.println("Auth EndPoint: "+config.getAuthEndpoint());
			 * System.out.println("Service EndPoint: "+config.getServiceEndpoint());
			 * System.out.println("Username: "+config.getUsername());
			 * System.out.println("SessionId: "+config.getSessionId());
			 */

			if("Account".equalsIgnoreCase(filesSet.getRecordType())){
				queryResults = connection.query("select id, Name, nm_URLforDocuments__c, nm_Status__c, nm_DocumentName__c "
						+ " from nm_LinksForDocuments__c where nm_Account__c = '" + filesSet.getSfdcRecordId() + "' "
						+ " and (nm_Status__c = 'Disapproved' or nm_Status__c = 'Documents Incorrect' or nm_Status__c = 'Admission Form & Documents Provisional'  or nm_URLforDocuments__c = null)");
			}else if("Lead".equalsIgnoreCase(filesSet.getRecordType())){
				queryResults = connection.query("select id, Name, nm_URLforDocuments__c, nm_Status__c, nm_DocumentName__c "
						+ " from nm_LinksForDocumentsLead__c where Lead__c = '" + filesSet.getSfdcRecordId() + "' "
						+ " and (nm_Status__c = 'Disapproved' or nm_Status__c = 'Documents Incorrect' or nm_Status__c = 'Admission Form & Documents Provisional' or nm_Status__c = '' or nm_URLforDocuments__c = null)" );
			}

			if (queryResults.getSize() > 0) {
				for (SObject s: queryResults.getRecords()) {
					DocumentBean document = new DocumentBean();
					document.setDocumentName((String)s.getField("nm_DocumentName__c"));
					document.setDocumentStatus((String)s.getField("nm_Status__c"));
					document.setSfdcDocumentRecordId((String)s.getField("Id"));
					filesSet.getDocuments().add(document);
					System.out.println("Document = "+document);
				}
			}
		} catch (ApiFault e) {
			init();
		} catch (Exception e) {
			e.printStackTrace();
		}   
	}

	@RequestMapping(value = "/uploadDocuments", method = RequestMethod.POST)
	public ModelAndView uploadDocuments(HttpServletRequest request, HttpServletResponse response, @ModelAttribute DocumentFileSet filesSet, Model m){
		List<SObject> documentsToUpdate = new ArrayList<SObject>();
		ArrayList<String> error_document = new ArrayList<String>();
		ArrayList<DocumentBean> documents = filesSet.getDocuments();
		if(documents != null && documents.size() > 0){
			for (DocumentBean documentBean : documents) {
				MultipartFile file = documentBean.getFile();
				if(file != null && !file.isEmpty()){//Check if File was attached
					//String errorMessage = uploadDocument(documentBean, filesSet);
					String errorMessage = uploadS3Document(documentBean, filesSet.getSfdcRecordId(),documentBean.getSfdcDocumentRecordId());
					

					if(errorMessage == null){
						SObject sObject = new SObject();
						//Set Object type to update
						if("Account".equalsIgnoreCase(filesSet.getRecordType())){
							sObject.setType("nm_LinksForDocuments__c");
						}else if("Lead".equalsIgnoreCase(filesSet.getRecordType())){
							sObject.setType("nm_LinksForDocumentsLead__c");
						}

						sObject.setId(documentBean.getSfdcDocumentRecordId());
						//Status needs to be changed for few cases
						String documentStatus = ("Disapproved".equalsIgnoreCase(documentBean.getDocumentStatus()) || 
								"Is Provisional".equalsIgnoreCase(documentBean.getDocumentStatus()) || 
								"Documents incorrect".equalsIgnoreCase(documentBean.getDocumentStatus())) ? "Re-Submited" : "";
						sObject.setField("nm_URLforDocuments__c", documentBean.getDocumentURL());
						sObject.setField("nm_Status__c", documentStatus);

						documentsToUpdate.add(sObject);
					}else{
						error_document.add("Error in uploading File "+documentBean.getDocumentName()+" . "+errorMessage);
					}
				}
			}
		}
		
		setError(request,error_document.toString());
		//Update data back in Salesforce
		try {
			if(documentsToUpdate.size() > 0){
				SObject[] records = new  SObject[documentsToUpdate.size()];
				records = documentsToUpdate.toArray(records);
				updateDocumentsRecordsInSFDC(records, request);
			}
		} catch (Exception e) {
			mailer.mailStackTrace(e.getMessage(), e);
			e.printStackTrace();
			setError(request,e.getMessage());
		}

		String studentPhotographURL = getStudentPhotograph(filesSet.getSfdcRecordId());
		request.setAttribute("filesSet",filesSet);
		m.addAttribute("filesSet",filesSet);
		m.addAttribute("studentPhotographURL", studentPhotographURL);
		return uploadDocumentForm(request, response, m);
	}

	@RequestMapping(value = "/AEPOnboardingDocumentUploadForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView AEPOnboardingDocumentUploadForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		
		DocumentFileSet filesSet = new DocumentFileSet();
		String aepid = request.getParameter("aepid");
		filesSet.setAepid(aepid);
		//System.out.println("inAEPOnboardingDocumentUploadForm - AEPID: "+aepid);
		try {
			readAEPDocumentDetailsFromSFDC(filesSet);
		} catch (Exception e) {
			mailer.mailStackTrace(e.getMessage(), e);
			e.printStackTrace();
		}
		if(filesSet.getDocuments().size() == 0){
			String successMessage = (String)request.getAttribute("successMessage");
			if(successMessage == null){
				successMessage = "";
			}
			setSuccess(request, successMessage + " : No documents pending for submission");
		}

		request.setAttribute("filesSet",filesSet);
		m.addAttribute("filesSet",filesSet);
		return new ModelAndView("aepOnboardingDocumentUpload");
		
	}

	private void readAEPDocumentDetailsFromSFDC(DocumentFileSet filesSet) throws Exception {

		try {

			String soql = "SELECT id, nm_DocumentName__c, nm_DocumentLink__c, Documents_Submited_Date__c, linkID__c, nm_ReasonforDisapprove__c, "
					+ "nm_Status__c, nm_URLforDocuments__c, AEP_site_inspection__c FROM nm_LinksForDocumentsLead__c WHERE AEP_site_inspection__c='"+filesSet.getAepid()+"'";
			QueryResult queryResults = connection.query(soql);

			if (queryResults.getSize() > 0) {
				for (SObject s: queryResults.getRecords()) {
					DocumentBean document = new DocumentBean();
					document.setDocumentName((String)s.getField("nm_DocumentName__c"));
					document.setDocumentStatus((String)s.getField("nm_Status__c"));
					document.setSfdcDocumentRecordId((String)s.getField("Id"));
					if( !( "Approved".equals(document.getDocumentStatus()) || "Submitted".equals(document.getDocumentStatus()) 
								|| "Re-Submited".equals(document.getDocumentStatus())) )
						filesSet.getDocuments().add(document);
				}
			}
		} catch (ApiFault e) {
			init();
			throw  new Exception(e.getMessage(),e); 
		} catch (Exception e) {
			e.printStackTrace();
			throw  new Exception(e.getMessage(),e);
		}   
	}

	@RequestMapping(value = "/AEPOnboardingDocumentUpload", method = RequestMethod.POST)
	public ModelAndView AEPOnboardingDocumentUpload(HttpServletRequest request, HttpServletResponse response, 
			@ModelAttribute DocumentFileSet filesSet, Model m){
		
		List<SObject> documentsToUpdate = new ArrayList<SObject>();
		ArrayList<DocumentBean> documents = filesSet.getDocuments();
		
		if(documents != null && documents.size() > 0){
			
			for (DocumentBean documentBean : documents) {
				
				MultipartFile file = documentBean.getFile();
				if(file != null && !file.isEmpty()){//Check if File was attached
					String errorMessage = uploadAEPDocument(documentBean, filesSet);

					if(errorMessage == null){
						SObject sObject = new SObject();
						//Set Object type to update
						sObject.setType("nm_LinksForDocumentsLead__c");
						sObject.setId(documentBean.getSfdcDocumentRecordId());
						//Status needs to be changed for few cases

						String documentStatus = "";
						switch(documentBean.getDocumentStatus()) {
							case "Disapproved":
							case "Provisional":
							case "Documents incorrect":
								documentStatus = "Re-Submited";
								break;
							default:
								documentStatus = "Submitted";
								break;
						}
						
						sObject.setField("nm_URLforDocuments__c", documentBean.getDocumentURL());
						sObject.setField("nm_Status__c", documentStatus);

						documentsToUpdate.add(sObject);
					}else{
						setError(request, errorMessage);
					}
				}
				
			}
			
		}
		try {
		//Update data back in Salesforce
			if(documentsToUpdate.size() > 0){
				SObject[] records = new  SObject[documentsToUpdate.size()];
				records = documentsToUpdate.toArray(records);
				updateAEPDocumentRecordsInSFDC(records, request);
			}
		} catch (Exception e) {
			mailer.mailStackTrace("Error in AEP Onboarding Document Upload", e);
			e.printStackTrace();
		}
		request.setAttribute("filesSet",filesSet);
		m.addAttribute("filesSet",filesSet);

		return AEPOnboardingDocumentUploadForm(request, response, m);
	}

	private String uploadAEPDocument(DocumentBean documentBean, DocumentFileSet filesSet) {
		 
		MultipartFile file = documentBean.getFile();

		//String fileName = file.getOriginalFilename();

		//fileName = fileName.replaceAll("'", "_");
		//fileName = fileName.replaceAll(",", "_");
		//fileName = fileName.replaceAll("&", "and");
		//fileName = fileName.replaceAll(" ", "_");
		//fileName = fileName.replaceAll(":", "_");
		//fileName = fileName.replaceAll("'", "_");
		
		//fileName = fileName.substring(0, fileName.lastIndexOf(".")) + "_" + 
				//RandomStringUtils.randomAlphanumeric(10) + fileName.substring(fileName.lastIndexOf("."), fileName.length());

		try {  
			String noseparators = SEPARATORS.matcher(documentBean.getDocumentName()).replaceAll("_");
			String normalized = Normalizer.normalize(noseparators, Form.NFD);
			String slug = NONLATIN.matcher(normalized).replaceAll("");
		
			String fileName = slug+"_"+RandomStringUtils.randomAlphanumeric(4);
			
			String folderName = AEP_DOCUMENTS_PATH + filesSet.getAepid()+ "/";
			
			
			String filePath = AEP_DOCUMENTS_PATH + filesSet.getAepid() + "/" +fileName+"."+FilenameUtils.getExtension(file.getOriginalFilename());
			
			HashMap<String,String> s3_response = amazonS3Helper.uploadFile(file, folderName , AWS_AEP_SERVICE_BUCKET, filePath);
		
			if( s3_response.get("status").equals("error") ) {
				
				/*
				responseBody.put("status","fail");
				responseBody.put("message", "Error in uploading File. ");
				*/
				documentBean.setDocumentURL("");
				
				return s3_response.get("url");
			}else {
				
				/*
				responseBody.put("status","success");
				responseBody.put("message", "successfully document uploaded");
				responseBody.put("documentUrl", s3_response.get("url") );
				*/
				documentBean.setDocumentURL(s3_response.get("url"));
				
			}

		} catch (Exception e) {   
			e.printStackTrace();   
			return e.getMessage();
		}   
		
		return null; //No Error
	}

	private void updateAEPDocumentRecordsInSFDC(SObject[] records,HttpServletRequest request) throws Exception {

		try {

			// update the records in Salesforce.com
			SaveResult[] saveResults = connection.update(records);
			// check the returned results for any errors
			for (int i=0; i< saveResults.length; i++) {
				if (saveResults[i].isSuccess()) {
					//System.out.println(i+". Successfully updated record - Id: " + saveResults[i].getId());
					setSuccess(request, "Documents Uploaded Successfully");
				}else {
					String errorMessage = "ERROR: ";
					Error[] errors = saveResults[i].getErrors();
					for (int j=0; j< errors.length; j++) {
						errorMessage += errors[j].getMessage() + " ";
						System.out.println("ERROR updating record: " + errors[j].getMessage());
					}
					setError(request, errorMessage);
				}   
			}
		} catch (ApiFault e) {
			init(); 
			throw  new Exception(e.getMessage(),e);
		} catch (Exception e) {
			setError(request, e.getMessage());
			throw  new Exception(e.getMessage(),e);
		}

	}

	private String getStudentPhotograph(String accountId){

		QueryResult qResult = new QueryResult();
		String soqlQuery =" SELECT nm_StudentImageUrl__c FROM Account WHERE Account_Unique_ID__c = '"+ accountId +"' " ;
		String url = "";
		try {
			qResult = connection.query(soqlQuery);
		} catch (ConnectionException e) { 
			e.printStackTrace();
		} 

		if (qResult.getSize() > 0) {
			SObject[] records = qResult.getRecords();
			for(SObject record : records) {
				url = (String)record.getField("nm_StudentImageUrl__c");
			}
		}

		return url;
		
	}
	 
	private void updateDocumentsRecordsInSFDC(SObject[] records,HttpServletRequest request) throws Exception {
		/*
		 * ConnectorConfig config = new ConnectorConfig();
		 * config.setUsername(SFDC_USERID); config.setPassword(SFDC_PASSWORD_TOKEN);
		 * System.out.println("Updating records back in SFDC");
		 * System.out.println("SFDC_USERID = "+SFDC_USERID);
		 * System.out.println("SFDC_PASSWORD_TOKEN = "+SFDC_PASSWORD_TOKEN);
		 */

		try {
			/*
			 * connection = Connector.newConnection(config); // query for documents records
			 * for give SFDC record Id
			 * System.out.println("Auth EndPoint: "+config.getAuthEndpoint());
			 * System.out.println("Service EndPoint: "+config.getServiceEndpoint());
			 * System.out.println("Username: "+config.getUsername());
			 * System.out.println("SessionId: "+config.getSessionId());
			 */
			System.out.println(" records "+records.toString());

			// update the records in Salesforce.com
			SaveResult[] saveResults = connection.update(records);
			// check the returned results for any errors
			for (int i=0; i< saveResults.length; i++) {
				if (saveResults[i].isSuccess()) {
					System.out.println(i+". Successfully updated record - Id: " + saveResults[i].getId());
					setSuccess(request, "Documents Uploaded Successfully");
				}else {
					String errorMessage = "ERROR: ";
					Error[] errors = saveResults[i].getErrors();
					for (int j=0; j< errors.length; j++) {
						errorMessage += errors[j].getMessage() + " ";
						System.out.println("ERROR updating record: " + errors[j].getMessage());
					}
					setError(request, errorMessage);
				}   
			}
		} catch (ApiFault e) {
			init();	
			setError(request, e.getMessage());
			throw  new Exception(e.getMessage(),e); 
		} catch (Exception e) {
			setError(request, e.getMessage());
			throw  new Exception(e.getMessage(),e); 
		}

	}

	private String uploadDocument(DocumentBean documentBean, DocumentFileSet filesSet) {
		InputStream inputStream = null;   
		OutputStream outputStream = null;   
		MultipartFile file = documentBean.getFile();

		String fileName = file.getOriginalFilename();   
		//fileName = fileName.replaceFirst(" ", "_");
		fileName = fileName.replaceAll(" ", "_"); // added to replace all spaces in the filename.
		fileName = fileName.replaceFirst("&", "_");

		System.out.println("fileName Size = "+file.getSize());

		System.out.println("fileName = "+fileName);

		try {  

			inputStream = file.getInputStream();  
			String randomNumber =RandomStringUtils.randomAlphanumeric(4);
			String filePath = ADMISSION_DOCUMENTS_PATH + filesSet.getSfdcRecordId() + "/" + randomNumber+"_"+fileName;
			//Check if Folder exists, if not then create it
			File folderPath = new File(ADMISSION_DOCUMENTS_PATH + filesSet.getSfdcRecordId());
			if (!folderPath.exists()) {
				System.out.println("Making Folder");
				boolean created = folderPath.mkdirs();
				System.out.println("created = "+created);
			}   

			File newFile = new File(filePath);   
			
			outputStream = new FileOutputStream(newFile);   
			int read = 0;   
			byte[] bytes = new byte[1024];   

			while ((read = inputStream.read(bytes)) != -1) {   
				outputStream.write(bytes, 0, read);   
			}
			documentBean.setDocumentURL(SFDC_DOCUMENTS_BASE_PATH + filesSet.getSfdcRecordId() + "/" +randomNumber+"_"+fileName);
			outputStream.close();
			inputStream.close();
		} catch (IOException e) {   
			e.printStackTrace();   
			return e.getMessage();
		}   
		System.out.println("Uploded File");
		return null; //No Error
	}

	@RequestMapping(value = "/uploadPhotoForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView uploadPhotoForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		String type = request.getParameter("type");
		String accountId = request.getParameter("uid");
		String recordId = null;
		if("lead".equalsIgnoreCase(type) || "leads".equalsIgnoreCase(type)){
			recordId = request.getParameter("leadId");
		}else{
			recordId = request.getParameter("accountId");
		}

		m.addAttribute("id", recordId);
		m.addAttribute("type", type);
		m.addAttribute("accountId", accountId);

		return new ModelAndView("uploadPhoto");
	}

	@RequestMapping(value = "/uploadPhoto", method = RequestMethod.POST)
	public ModelAndView uploadPhoto(HttpServletRequest request, HttpServletResponse response, @RequestParam("accountId") String accountId,
			@RequestParam("file") MultipartFile file, @RequestParam("id") String id, @RequestParam("type") String type, Model m){
		try {
			if(!file.isEmpty()){
				//String fileUrl = uploadPhotoFile(file, id);
				
				//check file type
				//Check the file Extensions which are supported
				String result = checkPhotoFileType(file.getOriginalFilename());
				if(!StringUtils.isBlank(result)) {
					setError(request, result);
				}else {
					
				String fileUrl = uploadS3PhotoOrPaymentFile(file,id,"Photo");
				URL url = new URL(fileUrl);
				if(url.toURI() != null){
					updatePhotoUrlInSFDC(id, fileUrl, request, type);
					setSuccess(request, "Photo uploaded successfully");
				}else {
					setError(request, fileUrl);
				}
				
				}
			}else{
				setError(request, "No file selected OR File is empty!");
			}

		
		} catch (Exception e) {
			e.printStackTrace();
			setError(request,"Error in uploading file. " +e.getMessage());
		}
		m.addAttribute("id", id);
		m.addAttribute("type", type);
		m.addAttribute("accountId", accountId);
		String studentPhotographURL = getStudentPhotograph(accountId);
		m.addAttribute("studentPhotographURL", studentPhotographURL);
		return new ModelAndView("uploadPhoto");
	}

	private void updatePhotoUrlInSFDC(String id, String fileUrl, HttpServletRequest request, String type) {
		/*
		 * ConnectorConfig config = new ConnectorConfig();
		 * config.setUsername(SFDC_USERID); config.setPassword(SFDC_PASSWORD_TOKEN);
		 * System.out.println("Updating records back in SFDC");
		 * System.out.println("SFDC_USERID = "+SFDC_USERID);
		 * System.out.println("SFDC_PASSWORD_TOKEN = "+SFDC_PASSWORD_TOKEN);
		 */

		try {
			/*
			 * connection = Connector.newConnection(config); // query for documents records
			 * for give SFDC record Id
			 * System.out.println("Auth EndPoint: "+config.getAuthEndpoint());
			 * System.out.println("Service EndPoint: "+config.getServiceEndpoint());
			 * System.out.println("Username: "+config.getUsername());
			 * System.out.println("SessionId: "+config.getSessionId());
			 */

			SObject[] records = new  SObject[1];
			SObject sObject = new SObject();

			if("lead".equalsIgnoreCase(type) || "leads".equalsIgnoreCase(type)){
				sObject.setType("Lead");
			}else{
				sObject.setType("Account");
			}

			sObject.setId(id);
			sObject.setField("nm_StudentImageUrl__c", fileUrl);
			records[0] = sObject;

			// update the records in Salesforce.com
			SaveResult[] saveResults = connection.update(records);
			// check the returned results for any errors
			for (int i=0; i< saveResults.length; i++) {
				if (saveResults[i].isSuccess()) {
					System.out.println(i+". Successfully updated record - Id: " + saveResults[i].getId());
					setSuccess(request, "Photo Uploaded Successfully");
				}else {
					String errorMessage = "ERROR: ";
					Error[] errors = saveResults[i].getErrors();
					for (int j=0; j< errors.length; j++) {
						errorMessage += errors[j].getMessage() + " ";
						System.out.println("ERROR updating record: " + errors[j].getMessage());
					}
					setError(request, errorMessage);
				}   
			}
			
			//updatePhotoUrlInSFDCDocuments(id, fileUrl, request,  type);
		} catch (ApiFault e) {
			init();
		} catch (Exception e) {
			setError(request, e.getMessage());
		}

	}

	private String uploadPhotoFile(MultipartFile file, String id) {
		InputStream inputStream = null;   
		OutputStream outputStream = null;   
		String fileName = file.getOriginalFilename();   
		//fileName = fileName.replaceFirst(" ", "_");
		fileName = fileName.replaceAll(" ", "_"); // added to replace all spaces in the filename.
		fileName = fileName.replaceFirst("&", "_");

		fileName = id + "_" +RandomStringUtils.randomAlphanumeric(4) + "_" + "Picture" + fileName.substring(fileName.lastIndexOf("."), fileName.length());

		System.out.println("fileName Size = "+file.getSize());
		System.out.println("fileName = "+fileName);

		try {  

			inputStream = file.getInputStream();   
			String filePath = ADMISSION_DOCUMENTS_PATH + id + "/" + fileName;
			//Check if Folder exists, if not then create it
			File folderPath = new File(ADMISSION_DOCUMENTS_PATH + id);
			if (!folderPath.exists()) {
				System.out.println("Making Folder");
				boolean created = folderPath.mkdirs();
				System.out.println("created = "+created);
			}   

			File newFile = new File(filePath);   

			outputStream = new FileOutputStream(newFile);   
			int read = 0;   
			byte[] bytes = new byte[1024];   

			while ((read = inputStream.read(bytes)) != -1) {   
				outputStream.write(bytes, 0, read);   
			}

			outputStream.close();
			inputStream.close();
			System.out.println("Uploded File");
			return SFDC_DOCUMENTS_BASE_PATH + id + "/" + fileName;
		} catch (IOException e) {   
			e.printStackTrace();   
			return null;
		}   
	}


	@RequestMapping(value = "/downloadPhotosForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadPhotosForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		return new ModelAndView("downloadPhoto");
	}

	@RequestMapping(value = "/downloadPhotos", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadPhotos(HttpServletRequest request, HttpServletResponse response, Model m) {
		ModelAndView modelnView =  new ModelAndView("downloadPhoto");

		String commaSeperatedStudentNumbers = request.getParameter("studentNumbers");
		

		System.out.println("Querying Account details from SFDC");

		/*
		 * ConnectorConfig config = new ConnectorConfig();
		 * config.setUsername(SFDC_USERID); config.setPassword(SFDC_PASSWORD_TOKEN);
		 * 
		 * System.out.println("SFDC_USERID = "+SFDC_USERID);
		 * System.out.println("SFDC_PASSWORD_TOKEN = "+SFDC_PASSWORD_TOKEN);
		 */

		QueryResult queryResults = new QueryResult();
		
		String errorMessage = "";

		try {
			/*
			 * connection = Connector.newConnection(config); // query for documents records
			 * for give SFDC record Id
			 * System.out.println("Auth EndPoint: "+config.getAuthEndpoint());
			 * System.out.println("Service EndPoint: "+config.getServiceEndpoint());
			 * System.out.println("Username: "+config.getUsername());
			 * System.out.println("SessionId: "+config.getSessionId());
			 */

			String query = "select id, Name, nm_StudentNo__c, nm_StudentImageUrl__c from Account where nm_StudentNo__c in (" + commaSeperatedStudentNumbers + ")";
			
			System.out.println("Query = "+query);
			queryResults = connection.query(query);

			if (queryResults.getSize() > 0) {
				Set<String> studentNoImageFileList = new HashSet<>();
				System.out.println("Student Records = "+queryResults.getSize());
				for (SObject s: queryResults.getRecords()) {

					String id = (String)s.getField("Id");
					String studentNo = (String)s.getField("nm_StudentNo__c");
					String fullURLOfImage = (String)s.getField("nm_StudentImageUrl__c");
					
					String folderPathOnMachine ="";
					String fullPathOnmachine = "";
					Pattern awsmatcher = Pattern.compile("(?=.*studentdocumentsngasce)", Pattern.CASE_INSENSITIVE);
					
					File photoFile = null;
					try {
						//Take substring from full URL e.g. http://admissions-ngasce.nmims.edu:4001/StudentDocuments/0019000001bGYAk/0019000001bGYAk_Picture.jpg
						 //folderPathOnMachine = fullURLOfImage.substring(fullURLOfImage.indexOf("StudentDocuments"), fullURLOfImage.length());
						final int index = fullURLOfImage.indexOf("/", fullURLOfImage.indexOf("/")+2);
						folderPathOnMachine  =  fullURLOfImage.substring(index + 1);
						 fullPathOnmachine =  "D:/" + folderPathOnMachine;//Add folder and drive to get path on machine
						 
						 String newFilePath = fullPathOnmachine.substring(0, fullPathOnmachine.lastIndexOf("/"))+ "/"  + studentNo + ".jpg";
						 File studentNoFile = new File(newFilePath);
						 
						 if( awsmatcher.matcher( fullURLOfImage ).find() ) {
							 URL url = new URL(fullURLOfImage);
							 FileUtils.copyURLToFile(url, studentNoFile);
						 }else {
							
					photoFile = new File(fullPathOnmachine);
					System.out.println("fullPathOnmachine = "+fullPathOnmachine);
					//File photoFile = new File(ADMISSION_DOCUMENTS_PATH + id + "/" + id + "_Picture.jpg");

					/*if(!photoFile.exists()){
						photoFile = new File(ADMISSION_DOCUMENTS_PATH + id + "/" + id + "_Picture.jpeg");
					}*/
					
					if(!photoFile.exists() || photoFile == null){
						errorMessage += "Picture file not found for " + studentNo + "<br/>";
						System.out.println("File not found for "+studentNo);
						continue;//Bypass rest of the activities if files not found.
					}
					//String newFilePath = ADMISSION_DOCUMENTS_PATH + id + "/" + studentNo + ".jpg";
					
					//Create new file path so that final file inside zip will have student number instead of record id
					
					Files.copy(photoFile.toPath(), studentNoFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
					
						 }
					studentNoImageFileList.add(newFilePath);
					
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					System.out.println("Copied new file with student number");
				}
				// create byte buffer
				byte[] buffer = new byte[1024];

				String zipFileName = "Photos-Download.zip";
				FileOutputStream fos = new FileOutputStream(ADMISSION_DOCUMENTS_PATH + "Photos-Download.zip" );
				ZipOutputStream zos = new ZipOutputStream(fos);
				//for (int i=0; i < studentNoImageFileList.size(); i++) {
				for (String s : studentNoImageFileList) {
					File srcFile = new File(s);
					FileInputStream fis = new FileInputStream(srcFile);
					// begin writing a new ZIP entry, positions the stream to the start of the entry data
					zos.putNextEntry(new ZipEntry(srcFile.getName()));
					int length;
					while ((length = fis.read(buffer)) > 0) {
						zos.write(buffer, 0, length);
					}
					zos.closeEntry();
					// close the InputStream
					fis.close();
					amazonS3Service.deleteFileFromLocal(srcFile.toString());
				}
				zos.close();
				
				//Download Zip file created
				File fileToDownload = new File(ADMISSION_DOCUMENTS_PATH +  "Photos-Download.zip");
				InputStream inputStream = new FileInputStream(fileToDownload);
				response.setContentType("application/zip");
				response.setHeader("Content-Disposition", "attachment; filename=\""+zipFileName+"\"");
				
				
				IOUtils.copy(inputStream, response.getOutputStream());
				response.flushBuffer();
				
				if(errorMessage.length() > 0 ){
					setError(request, errorMessage);
				}
			}
		} catch (ApiFault e) {
			init();
		} catch (Exception e) {
			e.printStackTrace();
			errorMessage += e.getMessage()+". <br> ";
			setError(request, errorMessage);
			return modelnView;
		}   
		System.out.println("errorMessage = "+errorMessage);
		
		if(errorMessage != null || "".equalsIgnoreCase(errorMessage)){
			setError(request, errorMessage);
		}

		return null;
	}

	@RequestMapping(value = "/uploadPaymentDocumentForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView uploadPaymentDocumentForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		String recordId = request.getParameter("id");
		m.addAttribute("id", recordId);
		return new ModelAndView("uploadPaymentDocument");
	}

	@RequestMapping(value = "/uploadPaymentDocument", method = RequestMethod.POST)
	public ModelAndView uploadPaymentDocument(HttpServletRequest request, HttpServletResponse response, 
			@RequestParam("file") MultipartFile file, @RequestParam("id") String id, Model m){
		try {
			
			if(!file.isEmpty()){
				//String fileUrl = uploadPaymentDocumentFile(file, id);
				//Check the file Extensions which are supported
				String result = checkFileType(file.getOriginalFilename());
				
				if(!StringUtils.isBlank(result)) {
					setError(request, result);
				}else {
					
				String fileUrl = uploadS3PhotoOrPaymentFile(file,id,"Payment");
				URL url = new URL(fileUrl);
				
				if(url.toURI() != null){
					updatePaymentDocumentUrlInSFDC(id, fileUrl, request);
					setSuccess(request, "Document uploaded successfully");
				}else {
					setError(request, fileUrl);
				}
				
				}
			}else{
				setError(request, "No file selected OR File is empty!");
			}

			
		} catch (Exception e) {
			e.printStackTrace();
			setError(request, "Error in file uploading. "+e.getMessage());
		}
		m.addAttribute("id", id);
		return new ModelAndView("uploadPaymentDocument");
	}
	
	private String uploadPaymentDocumentFile(MultipartFile file, String id) {
		InputStream inputStream = null;   
		OutputStream outputStream = null;   
		String fileName = file.getOriginalFilename();   
		fileName = fileName.replaceFirst(" ", "_");
		fileName = fileName.replaceFirst("&", "_");

		fileName = id + "_" + "Payment" + fileName.substring(fileName.lastIndexOf("."), fileName.length());

		System.out.println("fileName Size = "+file.getSize());
		System.out.println("fileName = "+fileName);

		try {  

			inputStream = file.getInputStream();   
			String filePath = ADMISSION_DOCUMENTS_PATH + "PaymentDocs" + "/" + fileName;
			//Check if Folder exists, if not then create it
			File folderPath = new File(ADMISSION_DOCUMENTS_PATH + "PaymentDocs");
			if (!folderPath.exists()) {
				System.out.println("Making Folder");
				boolean created = folderPath.mkdirs();
				System.out.println("created = "+created);
			}   

			File newFile = new File(filePath);   

			outputStream = new FileOutputStream(newFile);   
			int read = 0;   
			byte[] bytes = new byte[1024];   

			while ((read = inputStream.read(bytes)) != -1) {   
				outputStream.write(bytes, 0, read);   
			}

			outputStream.close();
			inputStream.close();
			System.out.println("Uploded File");
			return SFDC_DOCUMENTS_BASE_PATH + "PaymentDocs" + "/" + fileName;
		} catch (IOException e) {   
			e.printStackTrace();   
			return null;
		}   
	}
	
	private void updatePaymentDocumentUrlInSFDC(String id, String fileUrl, HttpServletRequest request) {
		/*
		 * ConnectorConfig config = new ConnectorConfig();
		 * config.setUsername(SFDC_USERID); config.setPassword(SFDC_PASSWORD_TOKEN);
		 * System.out.println("Updating records back in SFDC");
		 * System.out.println("SFDC_USERID = "+SFDC_USERID);
		 * System.out.println("SFDC_PASSWORD_TOKEN = "+SFDC_PASSWORD_TOKEN);
		 */

		try {
			/*
			 * connection = Connector.newConnection(config); // query for documents records
			 * for give SFDC record Id
			 * System.out.println("Auth EndPoint: "+config.getAuthEndpoint());
			 * System.out.println("Service EndPoint: "+config.getServiceEndpoint());
			 * System.out.println("Username: "+config.getUsername());
			 * System.out.println("SessionId: "+config.getSessionId());
			 */

			SObject[] records = new  SObject[1];
			SObject sObject = new SObject();

			sObject.setType("nm_Payment__c");

			sObject.setId(id);
			sObject.setField("Ref_Payment_Document__c", fileUrl);
			records[0] = sObject;

			// update the records in Salesforce.com
			SaveResult[] saveResults = connection.update(records);
			// check the returned results for any errors
			for (int i=0; i< saveResults.length; i++) {
				if (saveResults[i].isSuccess()) {
					System.out.println(i+". Successfully updated record - Id: " + saveResults[i].getId());
					setSuccess(request, "Payment Document Uploaded Successfully");
				}else {
					String errorMessage = "ERROR: ";
					Error[] errors = saveResults[i].getErrors();
					for (int j=0; j< errors.length; j++) {
						errorMessage += errors[j].getMessage() + " ";
						System.out.println("ERROR updating record: " + errors[j].getMessage());
					}
					setError(request, errorMessage);
				}   
			}
		} catch (ApiFault e) {
			init();
		} catch (Exception e) {
			setError(request, e.getMessage());
		}

	}
	
	
	
	private void updatePhotoUrlInSFDCDocuments(String id, String fileUrl, HttpServletRequest request, String type) {
		/*
		 * ConnectorConfig config = new ConnectorConfig();
		 * config.setUsername(SFDC_USERID); config.setPassword(SFDC_PASSWORD_TOKEN);
		 * System.out.println("Updating records back in SFDC");
		 * System.out.println("SFDC_USERID = "+SFDC_USERID);
		 * System.out.println("SFDC_PASSWORD_TOKEN = "+SFDC_PASSWORD_TOKEN);
		 */

		try {
			/*
			 * connection = Connector.newConnection(config); // query for documents records
			 * for give SFDC record Id
			 * System.out.println("Auth EndPoint: "+config.getAuthEndpoint());
			 * System.out.println("Service EndPoint: "+config.getServiceEndpoint());
			 * System.out.println("Username: "+config.getUsername());
			 * System.out.println("SessionId: "+config.getSessionId());
			 */
			QueryResult qResult = new QueryResult();
			String soqlQuery = null;
			if("lead".equalsIgnoreCase(type) || "leads".equalsIgnoreCase(type)){
				 soqlQuery = "Select id,Name,nm_DocumentName__c,nm_URLforDocuments__c "
				 		+ " from nm_LinksForDocumentsLead__c "
				 		+ " where nm_DocumentName__c='Student Photograph' and Lead__c = \'"+id+"\'";
			}else{
				 soqlQuery = "Select id,Name,nm_Account__c,nm_DocumentName__c,nm_URLforDocuments__c "
						+ " from nm_LinksForDocuments__c "
						+ " where nm_DocumentName__c='Student Photograph' and nm_Account__c = \'"+id+"\' ";

			}
			
			       qResult = connection.query(soqlQuery);
					SObject[] records = qResult.getRecords();
						SObject s = (SObject) records[0];
						
						System.out.println("Object created successfully  ");

				

			if("lead".equalsIgnoreCase(type) || "leads".equalsIgnoreCase(type)){
				s.setType("nm_LinksForDocumentsLead__c ");
				s.setField("nm_URLforDocuments__c", fileUrl);//sObject.setType("Lead");
			}else{
				s.setType("nm_LinksForDocuments__c ");
				s.setField("nm_URLforDocuments__c", fileUrl);
			}

		
			records[0] = s;

			// update the records in Salesforce.com
			SaveResult[] saveResults = connection.update(records);
			// check the returned results for any errors
			for (int i=0; i< saveResults.length; i++) {
				if (saveResults[i].isSuccess()) {
					System.out.println(i+". Successfully updated photo in documents record - Id: " + saveResults[i].getId());
					setSuccess(request, "Photo Uploaded Successfully");
				}else {
					String errorMessage = "ERROR: ";
					Error[] errors = saveResults[i].getErrors();
					for (int j=0; j< errors.length; j++) {
						errorMessage += errors[j].getMessage() + " ";
						System.out.println("ERROR updating record: " + errors[j].getMessage());
					}
					setError(request, errorMessage);
				}   
			}

		} catch (Exception e) {
			setError(request, e.getMessage());
		}

	}
	
	
	
	
	public static void main(String[] args) {
		String fullURLOfImage = "http://admissions-ngasce.nmims.edu:4001/StudentDocuments/0019000001bGYAk/0019000001bGYAk_Picture.jpg";
		
		String folderPathOnMachine = fullURLOfImage.substring(fullURLOfImage.indexOf("StudentDocuments"), fullURLOfImage.length());
		String fullPathOnmachine = "E:/" + folderPathOnMachine;
		
		String newFilePath = fullPathOnmachine.substring(0, fullPathOnmachine.lastIndexOf("/")) + "/" + "771127789" + ".jpg";
		
		System.out.println(fullPathOnmachine);
		System.out.println(newFilePath);
	}

	private String uploadS3Document(DocumentBean documentBean,String sfdcRecoredId,String documentId) {
		
		MultipartFile file = documentBean.getFile();
		if(documentBean.getDocumentName().equalsIgnoreCase(studentPhotoDocument)) {
			if(fileUtils.checkFileExtensionsForStudentImage(FilenameUtils.getExtension(file.getOriginalFilename())) == 0) 
				return "File Type Not Supported ";
		}else {
			if(fileUtils.checkFileExtensions(FilenameUtils.getExtension(file.getOriginalFilename())) == 0)
				return "File Type Not Supported ";
		}
		
		String url = amazonS3Service.uploadDocument(file,sfdcRecoredId,documentId,documentBean.getDocumentName());
		
		if(url.length() > 0) {
			documentBean.setDocumentURL(url);
			return null; //No Error
		}
		else
			return "Error in Uploading Document.";
		
	}
	
	public String checkFileType(String filename)
	{
		//Check the file Extensions which are supported
		if(fileUtils.checkFileExtensions(FilenameUtils.getExtension(filename)) == 0)
			return "File Type Not Supported ";
		return "";
	}
	
	public String checkPhotoFileType(String filename)
	{
		//Check the file Extensions which are supported
		if(fileUtils.checkFileExtensionsForStudentImage(FilenameUtils.getExtension(filename)) == 0)
			return "File Type Not Supported ";
		return "";
	}
	
	public String uploadS3PhotoOrPaymentFile(MultipartFile file, String id,String documentType)
	{	
		String url = null;
		
		if(documentType.equals("Photo"))
			url = amazonS3Service.uploadPhotoFile(file,id);
		else
			url = amazonS3Service.uploadPaymentDocument(file,id); 

		if(url.length() > 0)
			return url;
		else
			return null; 
	}
	
	public String uploadDocumentForInterviewTos3(DocumentBean documentBean)
	{	
		
		String todayAsString = new SimpleDateFormat("ddMMyyyy").format(new Date());
		String randomNumber =RandomStringUtils.randomAlphanumeric(6);
		String filePath =  defaultStudentProfileDocument + randomNumber+"_"+todayAsString+"."+FilenameUtils.getExtension(documentBean.getFile().getOriginalFilename());
		return  amazonS3Service.uploadDocumentToS3(documentBean.getFile(),filePath,defaultStudentProfileDocument_bucket,defaultStudentProfileDocument);
	}
	
	@RequestMapping(value = "/transferDocumentDetailsFromSFDCForS3", method = RequestMethod.GET)
	public void readDocumentDetailsFromSFDCForS3(HttpServletRequest request, HttpServletResponse response) 
	{
		String recordType = request.getParameter("recordType");
		
		List<DocumentBean> error_list = new ArrayList<DocumentBean>();
		List<String> accountIds = new ArrayList<String>();
		String month = request.getParameter("month");
		String year = request.getParameter("year");
		
		int success_count = 0;
		int error_count = 0;
		int i = 0;
		
		ConnectorConfig config = new ConnectorConfig();
		config.setUsername(SFDC_USERID);
		config.setPassword(SFDC_PASSWORD_TOKEN);

		System.out.println("SFDC_USERID = "+SFDC_USERID);
		System.out.println("SFDC_PASSWORD_TOKEN = "+SFDC_PASSWORD_TOKEN);

		// display some current settings

		QueryResult queryResults = new QueryResult();
		
		List<DocumentBean> documents = new ArrayList<DocumentBean>();
		
			String documentName = "";

		try {
			connection = Connector.newConnection(config);
			// query for documents records for give SFDC record Id
			System.out.println("Auth EndPoint: "+config.getAuthEndpoint());
			System.out.println("Service EndPoint: "+config.getServiceEndpoint());
			System.out.println("Username: "+config.getUsername());
			System.out.println("SessionId: "+config.getSessionId());
			 HashSet<String> lists = new HashSet<String>();
			boolean done = false;
		
			if("old".equalsIgnoreCase(recordType)){
				
				queryResults = connection.query(" select Id,Drive_Month__c,Enrollment_Year__c,AccountId   from Opportunity where Account.nm_StudentStatus__c = 'Confirmed'	" + 
						" and Drive_Month__c = '"+month+"' and Enrollment_Year__c = '"+year+"'");
				if (queryResults.getSize() > 0) {
				
						while(!done) {
							SObject[] records = queryResults.getRecords();
							
							for (int l = 0; l < records.length; ++l) {
							
								SObject s1 = (SObject) records[l];
							
								lists.add((String)s1.getField("AccountId"));
									
								}
						
							if (queryResults.isDone()) {
								done = true;
							} else {
								System.out.println("Querying more.....");
								queryResults = connection.queryMore(queryResults.getQueryLocator());
							}
						}
			}
				
				
				for(int k = 1 ; k <= 20 ; k++ ) {
					
					documentName += ",Document_Name_"+k+"__c,URL_for_Documents"+k+"__c ";
				}
				
				documentName = documentName.substring(1);
			int count_id = 0;
			for(String id:lists)
			{
				System.out.println(" Getting the list of Student Documents. "+(count_id/lists.size()));
				queryResults = connection.query("SELECT "+documentName+",Account__c,Id   FROM Archived_Document__b where Account__c  = '"+id+"'");
			
				if (queryResults.getSize() > 0) {
			
						
						SObject[] records = queryResults.getRecords();
					
						for (int l = 0; l < records.length; ++l) {
							SObject s1 = (SObject) records[l];
							for(int g=1;g<=20;g++) {
								
								DocumentBean document = new DocumentBean();
								
								if(!StringUtils.isBlank((String)s1.getField("URL_for_Documents"+g+"__c"))){
									
						
										document.setSfdcDocumentRecordId((String)s1.getField("Id"));
										document.setDocumentName((String)s1.getField("Document_Name_"+g+"__c"));
										document.setDocumentURL((String)s1.getField("URL_for_Documents"+g+"__c"));
										documents.add(document);
									
								}
							}
							}
					
				}
				count_id++;
			}
			
			}else if("Lead".equalsIgnoreCase(recordType)){
				
				queryResults = connection.query("Select id,Name,nm_Account__c,nm_DocumentName__c,nm_URLforDocuments__c , nm_Status__c from nm_LinksForDocumentsLead__c where nm_URLforDocuments__c!=null and nm_URLforDocuments__c!='' ");
				if (queryResults.getSize() > 0) {
					
					while(!done) {
						SObject[] records = queryResults.getRecords();
						for (int l = 1; l <= records.length; l++) {
							
							SObject s = (SObject) records[l];
							
						DocumentBean document = new DocumentBean();
						document.setSfdcDocumentRecordId((String)s.getField("Id"));
						document.setDocumentURL((String)s.getField("nm_URLforDocuments__c"));
						document.setDocumentName((String)s.getField("nm_DocumentName__c"));
						
						documents.add(document);
						
					}
					if (queryResults.isDone()) {
						done = true;
					} else {
						System.out.println("Querying more.....");
						queryResults = connection.queryMore(queryResults.getQueryLocator());
					}
					}
					
				}
				
			}else if("current".equalsIgnoreCase(recordType)){
				
				queryResults = connection.query("SELECT id,Name,nm_Account__c,nm_DocumentName__c,nm_URLforDocuments__c ,nm_Status__c,CreatedDate FROM nm_LinksForDocuments__c WHERE (nm_URLforDocuments__c!=null AND nm_URLforDocuments__c!='') AND CreatedDate > 2022-01-20T00:00:00Z AND CreatedDate <= TODAY ");
				
				if (queryResults.getSize() > 0) {
					
					while(!done) {
						SObject[] records = queryResults.getRecords();
						for (int l = 0; l < records.length; ++l) {
						
							SObject s = (SObject) records[l];
							
						DocumentBean document = new DocumentBean();
						document.setSfdcDocumentRecordId((String)s.getField("Id"));
						document.setDocumentURL((String)s.getField("nm_URLforDocuments__c"));
						document.setDocumentName((String)s.getField("nm_DocumentName__c"));
						
						documents.add(document);
					}
					if (queryResults.isDone()) {
						done = true;
					} else {
						System.out.println("Querying more.....");
						queryResults = connection.queryMore(queryResults.getQueryLocator());
					}
					}
					
				}
			}
			
			
			
		
		
		System.out.println("Student Documents Transfer To S3 Batch Job Started For recordType "+recordType);
		aws_logger.info("Student	 Documents Transfer To S3 Batch Job Started For recordType "+recordType);
		
		aws_logger.info("Total number of documents "+documents.size());

		System.out.println("No.of documents : "+documents.size());
		System.out.println("Batch Job Started");
		
		//Document Iteration Loop
		
		for(DocumentBean document:documents) {
		aws_logger.info("Document To be transfered "+document);
		try {
			System.out.println("Success/Failure : "+success_count+"/"+error_count);	
			System.out.println("Iteration : "+(++i)+"/"+documents.size());
	
		//batch job service to transfer the documents from local to s3
		String url = amazonS3Service.batchJobForExistingDocuments(document);

		
		 if(url.equals("success")) {
					 
			 accountIds.add(document.getSfdcDocumentRecordId());
		
					//delete file from local
				URL aURL = new URL(document.getDocumentURL());
				String folderName = studentDocumentDrive+aURL.getFile();
				amazonS3Service.deleteFileFromLocal(folderName);
				success_count++;
			
					
		 	}else {
		 		error_list.add(document);
		 		error_count++;
		 		continue;
		 	}				
		
		}catch (Exception e) {
			 error_list.add(document);
			 error_count++;
			 aws_logger.error(" Error in updating file in salesforce . Document - "+document+" . Error ",e);
		
		} 
		 
		}//END OF FOR LOOP
		
		//END OF DOCUMENT LOOP

		}catch (Exception e) {

			aws_logger.error(" Error in File Upload on S3 and getting data from salesforce.",e);
		}
		
		System.out.println("Success/Failure : "+success_count+"/"+error_count);
		System.out.println("Student Documents Transfer To S3 Batch Job Completed for recordType."+recordType);
		aws_logger.info("Student Documents Transfer To S3 Batch Job Completed for recordType."+recordType);
		aws_logger.info("Actual-count "+documents.size());
		aws_logger.info("Success-count "+success_count);
		aws_logger.info("Error-count "+error_count);
		aws_logger.info("Error-List "+error_list);
		aws_logger.info("Updated AccountId Lists "+accountIds.toString());
	}
	

	
	

	/*
	 * was added to have this to have AEP upload document
	 * */
	@RequestMapping(value = "/AEPServices", method = RequestMethod.POST)
	public ResponseEntity<HashMap<String, String>> uploadAEPDocuments( HttpServletRequest request, 
			HttpServletResponse response, @ModelAttribute DocumentBean filesSet, Model m){
		
		HashMap<String, String> responseBody = new HashMap<String,String>();
		
		try {

			MultipartFile file = filesSet.getFile();
			
			String fileName = file.getOriginalFilename();   
			fileName = fileName.replaceAll(" ", "_"); // added to replace all spaces in the filename.
			fileName = fileName.replaceFirst("&", "_");
			
			String randomNumber =RandomStringUtils.randomAlphanumeric(4);
			String filePath =  AEP_SERVICE_UPLOAD_PATH + randomNumber + "_" + fileName;
			
			if(file != null && !file.isEmpty()){//Check if File was attached
				
				//String errorMessage = uploadAEPDocument( filesSet );
				//updated to have the upload to s3 bucket
				HashMap<String,String> s3_response = amazonS3Helper.uploadFile(file, AEP_SERVICE_UPLOAD_PATH, AWS_AEP_SERVICE_BUCKET, filePath);
				
				if( s3_response.get("status") == "error" ) {
					responseBody.put("status","fail");
					responseBody.put("message", "Error in uploading File. ");
				}else {
					responseBody.put("status","success");
					responseBody.put("message", "successfully document uploaded");
					responseBody.put("documentUrl", s3_response.get("url") );
				}
				
				return new ResponseEntity<HashMap<String,String>>(responseBody,HttpStatus.OK);
			}
			
			responseBody.put("status","fail");
			responseBody.put("message", "Kindly attached file to upload");
			return new ResponseEntity<HashMap<String,String>>(responseBody,HttpStatus.OK);
			
		}catch (Exception e) {

			responseBody.put("status","fail");
			responseBody.put("message", "Server error found,try again");
			return new ResponseEntity<HashMap<String,String>>(responseBody,HttpStatus.OK);
			
		}
	}
	
	/*
	private String uploadAEPDocument(DocumentBean documentBean) {
		
		InputStream inputStream = null;   
		OutputStream outputStream = null;   
		MultipartFile file = documentBean.getFile();

		String fileName = file.getOriginalFilename();   
		fileName = fileName.replaceFirst(" ", "_");
		fileName = fileName.replaceFirst("&", "_");

		try {  

			inputStream = file.getInputStream();  
			String randomNumber =RandomStringUtils.randomAlphanumeric(6);
			String filePath = AEP_SERVICE_UPLOAD_PATH + randomNumber+"_"+fileName;
			
			//Check if Folder exists, if not then create it
			File folderPath = new File( AEP_SERVICE_UPLOAD_PATH );
			if (!folderPath.exists()) {
				boolean created = folderPath.mkdirs();
			}   

			File newFile = new File(filePath);   
			
			outputStream = new FileOutputStream(newFile);   
			int read = 0;   
			byte[] bytes = new byte[1024];   

			while ((read = inputStream.read(bytes)) != -1) {   
				outputStream.write(bytes, 0, read);   
			}

			documentBean.setDocumentURL(AEP_SERVICE_BASE_PATH + randomNumber+"_"+fileName);
			outputStream.close();
			inputStream.close();
			
		} catch (IOException e) {   
			e.printStackTrace();   
			return e.getMessage();
		}   

		return null; //No Error
	}
	*/
	
	
	
}


