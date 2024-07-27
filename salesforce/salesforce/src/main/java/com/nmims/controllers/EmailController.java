package com.nmims.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.DocumentBean;
import com.nmims.beans.DocumentFileSet;
import com.sforce.soap.partner.Connector;
import com.sforce.soap.partner.Error;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.SaveResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectorConfig;


@Controller
public class EmailController extends BaseController{

	@Autowired
	ApplicationContext act;

	@Value( "${SFDC_USERID}" )
	private String SFDC_USERID;

	@Value( "${SFDC_PASSWORD_TOKEN}" )
	private String SFDC_PASSWORD_TOKEN;

	@Value( "${ADMISSION_DOCUMENTS_PATH}" )
	private String ADMISSION_DOCUMENTS_PATH;
	
	/*	@Value( "${STUDENT_PHOTOS_FROM_SFDC_PATH}" )
	private String STUDENT_PHOTOS_FROM_SFDC_PATH;*/

	@Value( "${SFDC_DOCUMENTS_BASE_PATH}" )
	private String SFDC_DOCUMENTS_BASE_PATH;

	private PartnerConnection connection;


	public EmailController(){
		System.out.println("Email Controller initiated.");
	}

	@RequestMapping(value = "/sendMassPromotionalEmailForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView sendMassPromotionalEmailForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {

		return new ModelAndView("sendMassPromotionalEmail");
	}
/*
	private void readDocumentDetailsFromSFDC(DocumentFileSet filesSet) {
		System.out.println("Querying documents details from SFDC");

		ConnectorConfig config = new ConnectorConfig();
		config.setUsername(SFDC_USERID);
		config.setPassword(SFDC_PASSWORD_TOKEN);

		System.out.println("SFDC_USERID = "+SFDC_USERID);
		System.out.println("SFDC_PASSWORD_TOKEN = "+SFDC_PASSWORD_TOKEN);

		// display some current settings

		QueryResult queryResults = new QueryResult();

		try {
			connection = Connector.newConnection(config);
			// query for documents records for give SFDC record Id
			System.out.println("Auth EndPoint: "+config.getAuthEndpoint());
			System.out.println("Service EndPoint: "+config.getServiceEndpoint());
			System.out.println("Username: "+config.getUsername());
			System.out.println("SessionId: "+config.getSessionId());

			if("Account".equalsIgnoreCase(filesSet.getRecordType())){
				queryResults = connection.query("select id, Name, nm_URLforDocuments__c, nm_Status__c, nm_DocumentName__c "
						+ " from nm_LinksForDocuments__c where nm_Account__c = '" + filesSet.getSfdcRecordId() + "' "
						+ " and (nm_Status__c = 'Disapproved' or nm_Status__c = 'Admission Form & Documents Provisional'  or nm_URLforDocuments__c = null)");
			}else if("Lead".equalsIgnoreCase(filesSet.getRecordType())){
				queryResults = connection.query("select id, Name, nm_URLforDocuments__c, nm_Status__c, nm_DocumentName__c "
						+ " from nm_LinksForDocumentsLead__c where Lead__c = '" + filesSet.getSfdcRecordId() + "' "
						+ " and (nm_Status__c = 'Disapproved' or nm_Status__c = 'Admission Form & Documents Provisional' or nm_Status__c = '' or nm_URLforDocuments__c = null)" );
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

		} catch (Exception e) {
			e.printStackTrace();
		}   
	}


	@RequestMapping(value = "/uploadDocuments", method = RequestMethod.POST)
	public ModelAndView uploadDocuments(HttpServletRequest request, HttpServletResponse response, @ModelAttribute DocumentFileSet filesSet, Model m){
		List<SObject> documentsToUpdate = new ArrayList<SObject>();
		ArrayList<DocumentBean> documents = filesSet.getDocuments();
		if(documents != null && documents.size() > 0){
			for (DocumentBean documentBean : documents) {
				MultipartFile file = documentBean.getFile();
				if(file != null && !file.isEmpty()){//Check if File was attached
					String errorMessage = uploadDocument(documentBean, filesSet);

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
						String documentStatus = ("Disapproved".equalsIgnoreCase(documentBean.getDocumentStatus()) || "Is Provisional".equalsIgnoreCase(documentBean.getDocumentStatus())) ? "Re-Submited" : "";
						sObject.setField("nm_URLforDocuments__c", documentBean.getDocumentURL());
						sObject.setField("nm_Status__c", documentStatus);

						documentsToUpdate.add(sObject);
					}else{
						setError(request, errorMessage);
					}
				}
			}
		}

		//Update data back in Salesforce
		if(documentsToUpdate.size() > 0){
			SObject[] records = new  SObject[documentsToUpdate.size()];
			records = documentsToUpdate.toArray(records);
			updateDocumentsRecordsInSFDC(records, request);
		}

		request.setAttribute("filesSet",filesSet);
		m.addAttribute("filesSet",filesSet);

		return uploadDocumentForm(request, response, m);
	}

	private void updateDocumentsRecordsInSFDC(SObject[] records,HttpServletRequest request) {
		ConnectorConfig config = new ConnectorConfig();
		config.setUsername(SFDC_USERID);
		config.setPassword(SFDC_PASSWORD_TOKEN);
		System.out.println("Updating records back in SFDC");
		System.out.println("SFDC_USERID = "+SFDC_USERID);
		System.out.println("SFDC_PASSWORD_TOKEN = "+SFDC_PASSWORD_TOKEN);

		try {
			connection = Connector.newConnection(config);
			// query for documents records for give SFDC record Id
			System.out.println("Auth EndPoint: "+config.getAuthEndpoint());
			System.out.println("Service EndPoint: "+config.getServiceEndpoint());
			System.out.println("Username: "+config.getUsername());
			System.out.println("SessionId: "+config.getSessionId());


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

		} catch (Exception e) {
			setError(request, e.getMessage());
		}

	}

	private String uploadDocument(DocumentBean documentBean, DocumentFileSet filesSet) {
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
			String filePath = ADMISSION_DOCUMENTS_PATH + filesSet.getSfdcRecordId() + "/" + fileName;
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
			documentBean.setDocumentURL(SFDC_DOCUMENTS_BASE_PATH + filesSet.getSfdcRecordId() + "/" + fileName);
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
		String recordId = null;
		if("lead".equalsIgnoreCase(type) || "leads".equalsIgnoreCase(type)){
			recordId = request.getParameter("leadId");
		}else{
			recordId = request.getParameter("accountId");
		}

		m.addAttribute("id", recordId);
		m.addAttribute("type", type);
		return new ModelAndView("uploadPhoto");
	}

	@RequestMapping(value = "/uploadPhoto", method = RequestMethod.POST)
	public ModelAndView uploadPhoto(HttpServletRequest request, HttpServletResponse response, 
			@RequestParam("file") MultipartFile file, @RequestParam("id") String id, @RequestParam("type") String type, Model m){
		try {
			if(!file.isEmpty()){
				String fileUrl = uploadPhotoFile(file, id);
				if(fileUrl != null){
					updatePhotoUrlInSFDC(id, fileUrl, request, type);
					setSuccess(request, "Photo uploaded successfully");
				}
			}else{
				setError(request, "No file selected OR File is empty!");
			}

			m.addAttribute("id", id);
			m.addAttribute("type", type);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ModelAndView("uploadPhoto");
	}

	private void updatePhotoUrlInSFDC(String id, String fileUrl, HttpServletRequest request, String type) {
		ConnectorConfig config = new ConnectorConfig();
		config.setUsername(SFDC_USERID);
		config.setPassword(SFDC_PASSWORD_TOKEN);
		System.out.println("Updating records back in SFDC");
		System.out.println("SFDC_USERID = "+SFDC_USERID);
		System.out.println("SFDC_PASSWORD_TOKEN = "+SFDC_PASSWORD_TOKEN);

		try {
			connection = Connector.newConnection(config);
			// query for documents records for give SFDC record Id
			System.out.println("Auth EndPoint: "+config.getAuthEndpoint());
			System.out.println("Service EndPoint: "+config.getServiceEndpoint());
			System.out.println("Username: "+config.getUsername());
			System.out.println("SessionId: "+config.getSessionId());

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

		} catch (Exception e) {
			setError(request, e.getMessage());
		}

	}

	private String uploadPhotoFile(MultipartFile file, String id) {
		InputStream inputStream = null;   
		OutputStream outputStream = null;   
		String fileName = file.getOriginalFilename();   
		fileName = fileName.replaceFirst(" ", "_");
		fileName = fileName.replaceFirst("&", "_");

		fileName = id + "_" + "Picture" + fileName.substring(fileName.lastIndexOf("."), fileName.length());

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

		String startNo = request.getParameter("startNo");
		String endNo = request.getParameter("endNo");

		System.out.println("Querying Account details from SFDC");

		ConnectorConfig config = new ConnectorConfig();
		config.setUsername(SFDC_USERID);
		config.setPassword(SFDC_PASSWORD_TOKEN);

		System.out.println("SFDC_USERID = "+SFDC_USERID);
		System.out.println("SFDC_PASSWORD_TOKEN = "+SFDC_PASSWORD_TOKEN);

		QueryResult queryResults = new QueryResult();
		
		String errorMessage = "";

		try {
			connection = Connector.newConnection(config);
			// query for documents records for give SFDC record Id
			System.out.println("Auth EndPoint: "+config.getAuthEndpoint());
			System.out.println("Service EndPoint: "+config.getServiceEndpoint());
			System.out.println("Username: "+config.getUsername());
			System.out.println("SessionId: "+config.getSessionId());

			String query = "select id, Name, nm_StudentNo__c, nm_StudentImageUrl__c from Account where nm_StudentNo__c <= '" 
					+ endNo + "' and nm_StudentNo__c >= '"+startNo +"'";
			
			System.out.println("Query = "+query);
			queryResults = connection.query(query);

			if (queryResults.getSize() > 0) {
				ArrayList<String> studentNoImageFileList = new ArrayList<>();
				System.out.println("Student Records = "+queryResults.getSize());
				for (SObject s: queryResults.getRecords()) {

					String id = (String)s.getField("Id");
					String studentNo = (String)s.getField("nm_StudentNo__c");
					String fullURLOfImage = (String)s.getField("nm_StudentImageUrl__c");
					
					
					//Take substring from full URL e.g. http://admissions-ngasce.nmims.edu:4001/StudentDocuments/0019000001bGYAk/0019000001bGYAk_Picture.jpg
					String folderPathOnMachine = fullURLOfImage.substring(fullURLOfImage.indexOf("StudentDocuments"), fullURLOfImage.length());
					String fullPathOnmachine =  "E:/" + folderPathOnMachine;//Add folder and drive to get path on machine
					File photoFile = new File(fullPathOnmachine);

					System.out.println("fullPathOnmachine = "+fullPathOnmachine);
					//File photoFile = new File(ADMISSION_DOCUMENTS_PATH + id + "/" + id + "_Picture.jpg");

					if(!photoFile.exists()){
						photoFile = new File(ADMISSION_DOCUMENTS_PATH + id + "/" + id + "_Picture.jpeg");
					}
					
					if(!photoFile.exists()){
						errorMessage += "Picture file not found for " + studentNo + "<br/>";
						System.out.println("File not found for "+studentNo);
						continue;//Bypass rest of the activities if files not found.
					}
					//String newFilePath = ADMISSION_DOCUMENTS_PATH + id + "/" + studentNo + ".jpg";
					
					//Create new file path so that final file inside zip will have student number instead of record id
					String newFilePath = fullPathOnmachine.substring(0, fullPathOnmachine.lastIndexOf("/"))+ "/"  + studentNo + ".jpg";
					System.out.println("newFilePath = "+newFilePath);
					File studentNoFile = new File(newFilePath);
					Files.copy(photoFile.toPath(), studentNoFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
					studentNoImageFileList.add(newFilePath);
					System.out.println("Copied new file with student number");
				}
				// create byte buffer
				byte[] buffer = new byte[1024];

				String zipFileName = startNo +"-" + endNo + ".zip";
				FileOutputStream fos = new FileOutputStream(ADMISSION_DOCUMENTS_PATH + startNo +"-" + endNo + ".zip" );
				ZipOutputStream zos = new ZipOutputStream(fos);
				for (int i=0; i < studentNoImageFileList.size(); i++) {
					File srcFile = new File(studentNoImageFileList.get(i));
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
				}
				zos.close();
				
				//Download Zip file created
				File fileToDownload = new File(ADMISSION_DOCUMENTS_PATH + startNo +"-" + endNo + ".zip");
				InputStream inputStream = new FileInputStream(fileToDownload);
				response.setContentType("application/zip");
				response.setHeader("Content-Disposition", "attachment; filename=\""+zipFileName+"\"");
				
				
				IOUtils.copy(inputStream, response.getOutputStream());
				response.flushBuffer();
				
				if(errorMessage.length() > 0 ){
					setError(request, errorMessage);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			setError(request, e.getMessage());
			return modelnView;
		}   
		System.out.println("errorMessage = "+errorMessage);
		

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
				String fileUrl = uploadPaymentDocumentFile(file, id);
				if(fileUrl != null){
					updatePaymentDocumentUrlInSFDC(id, fileUrl, request);
					setSuccess(request, "Document uploaded successfully");
				}
			}else{
				setError(request, "No file selected OR File is empty!");
			}

			m.addAttribute("id", id);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		ConnectorConfig config = new ConnectorConfig();
		config.setUsername(SFDC_USERID);
		config.setPassword(SFDC_PASSWORD_TOKEN);
		System.out.println("Updating records back in SFDC");
		System.out.println("SFDC_USERID = "+SFDC_USERID);
		System.out.println("SFDC_PASSWORD_TOKEN = "+SFDC_PASSWORD_TOKEN);

		try {
			connection = Connector.newConnection(config);
			// query for documents records for give SFDC record Id
			System.out.println("Auth EndPoint: "+config.getAuthEndpoint());
			System.out.println("Service EndPoint: "+config.getServiceEndpoint());
			System.out.println("Username: "+config.getUsername());
			System.out.println("SessionId: "+config.getSessionId());

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

		} catch (Exception e) {
			setError(request, e.getMessage());
		}

	}*/
	
	public static void main(String[] args) {
		String fullURLOfImage = "http://admissions-ngasce.nmims.edu:4001/StudentDocuments/0019000001bGYAk/0019000001bGYAk_Picture.jpg";
		
		String folderPathOnMachine = fullURLOfImage.substring(fullURLOfImage.indexOf("StudentDocuments"), fullURLOfImage.length());
		String fullPathOnmachine = "E:/" + folderPathOnMachine;
		
		String newFilePath = fullPathOnmachine.substring(0, fullPathOnmachine.lastIndexOf("/")) + "/" + "771127789" + ".jpg";
		
		System.out.println(fullPathOnmachine);
		System.out.println(newFilePath);
	}

}


