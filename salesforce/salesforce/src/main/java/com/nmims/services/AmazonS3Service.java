package com.nmims.services;

import java.io.File;
import java.net.URL;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.xmlbeans.impl.xb.xsdschema.LengthDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import com.nmims.beans.AmazonS3Bean;
import com.nmims.beans.DocumentBean;
import com.nmims.beans.DocumentFileSet;
import com.nmims.beans.FileMigrationBean;
import com.nmims.daos.AmazonS3Dao;
import com.nmims.daos.LDAPDao;
import com.nmims.helpers.AmazonS3Helper;

@Service
public class AmazonS3Service 
{
	
	@Value( "${AWS_STUDENTDOCXBUCKET}" )
	private String AWS_STUDENTDOCXBUCKET;
	
	@Value( "${AWS_ACCESS_DOCUMENT_URL}" )
	private String AWS_ACCESS_DOCUMENT_URL;
	
	@Autowired
	AmazonS3Dao amazonS3DAO;
	
	@Autowired
	AmazonS3Helper amazonS3;
	
	HashMap<String,String> s3_response = null;
	
	private final String BaseStudentDocumentPath = "StudentDocuments/";
	
	private static final Pattern NONLATIN = Pattern.compile("[^\\w_-]");  
	private static final Pattern SEPARATORS = Pattern.compile("[\\s\\p{Punct}&&[^-]]"); 
	
	private static final Logger aws_logger = LoggerFactory.getLogger("aws_document");
	
	private static final String studentDocumentDrive = "E:";
	
	public String uploadDocument(MultipartFile file, String sfdcRecordId,String documentId,String documentName)
	{
	
		s3_response = new HashMap<String,String>();
		
		try {
			
		//String fileName = file.getOriginalFilename();   
		//fileName = fileName.replaceFirst(" ", "_");
		//fileName = fileName.replaceAll(" ", "_"); // added to replace all spaces in the filename.
		//fileName = fileName.replaceFirst("&", "_");
		
		String noseparators = SEPARATORS.matcher(documentName).replaceAll("_");
		String normalized = Normalizer.normalize(noseparators, Form.NFD);
		String slug = NONLATIN.matcher(normalized).replaceAll("");
	
		String fileName = slug+"_"+RandomStringUtils.randomAlphanumeric(4);
		
		String folderName = BaseStudentDocumentPath + sfdcRecordId + "/";
		
		String filePath =  BaseStudentDocumentPath + sfdcRecordId + "/" + fileName+"."+FilenameUtils.getExtension(file.getOriginalFilename());
		
			//upload File Helper
			s3_response = amazonS3.uploadFile(file,folderName,AWS_STUDENTDOCXBUCKET,filePath);

			if(s3_response.get("status").equals("success")) {
					return s3_response.get("url");
				 				
			}else {
				 aws_logger.info("Error in uploading Documents.:- "+s3_response.get("url"));
				 return "";
			}
			
			
			
			
			
		}catch(Exception e)
		{
			aws_logger.error("Error in uploading Documents . ",e);
			
		}
		return "";
	}
	
	//Upload Photo File
	public String uploadPhotoFile(MultipartFile file, String id)
	{
		
		s3_response = new HashMap<String,String>();
		
		try {
		//String fileName = file.getOriginalFilename();   
		//fileName = fileName.replaceFirst(" ", "_");
		//fileName = fileName.replaceAll(" ", "_"); // added to replace all spaces in the filename.
		//fileName = fileName.replaceFirst("&", "_");

		
		String fileName = id + "_" +RandomStringUtils.randomAlphanumeric(4) + "_" + "Picture" ;

		String filePath = BaseStudentDocumentPath + id + "/" + fileName+"."+FilenameUtils.getExtension(file.getOriginalFilename());
		String folderName = BaseStudentDocumentPath + id + "/";
		

		
		
			//upload File Helper
			s3_response = amazonS3.uploadFile(file,folderName,AWS_STUDENTDOCXBUCKET,filePath);
			
			



			if(s3_response.get("status").equals("success")) {
			
					return s3_response.get("url");
				
			}else {
 				aws_logger.info("Error in getting response of photo upload :- "+s3_response.get("url"));
 				return "";
				
 			}
			
		}catch(Exception e)
		{
			aws_logger.error("Error in uploading photo File.",e);
			
		}
		return "";
	}
	
	//Upload Photo File
		public String uploadPaymentDocument(MultipartFile file, String id)
		{
		
			s3_response = new HashMap<String,String>();
			
			try {
			//String fileName = file.getOriginalFilename();   
			//fileName = fileName.replaceFirst(" ", "_");
			//fileName = fileName.replaceFirst("&", "_");
			//fileName = id + "_" + "Payment" + fileName.substring(fileName.lastIndexOf("."), fileName.length());
			
			String fileName = id + "_" +RandomStringUtils.randomAlphanumeric(4) + "_" + "Payment" ;
			

		

			String filePath = BaseStudentDocumentPath + "PaymentDocs" + "/" + fileName+"."+FilenameUtils.getExtension(file.getOriginalFilename());
			
			String folderName = BaseStudentDocumentPath + "PaymentDocs/";
			
			
			
		
				//upload File Helper
				s3_response = amazonS3.uploadFile(file,folderName,AWS_STUDENTDOCXBUCKET,filePath);
				
				
				if(s3_response.get("status").equals("success")) {
					
						return s3_response.get("url");
					
				}else {
	 				aws_logger.info("Error in getting response of payment  upload :- "+s3_response.get("url"));
	 				return "";
					
	 			}
				
			
				
			}catch(Exception e)
			{
				
				aws_logger.error("Error in uploading payment File.",e);
				
			}
			return "";
		}
		
		public String getFilePath(String folderName)
		{
			final int index = folderName.indexOf("/", folderName.indexOf("/"));
			return folderName.substring(index + 1);
				
		}

		
		public String batchJobForExistingDocuments(DocumentBean document)
		{
			s3_response = new HashMap<String,String>();
		
			try {
			URL aURL = new URL(document.getDocumentURL());
			
			String folderName = studentDocumentDrive+aURL.getFile();
			
			final File folder = new File(folderName);
			
			if(!folder.exists()) {
				aws_logger.info("File Not Found. "+document);
				
				return "error";
			}
			
			String baseUrl = FilenameUtils.getPath(folderName);
			String fileName = FilenameUtils.getBaseName(folderName)
			                + "." + FilenameUtils.getExtension(folderName);
			
			
			fileName = baseUrl + fileName;
			
			
			
		
			
			s3_response = amazonS3.uploadLocalFile(folderName,fileName,AWS_STUDENTDOCXBUCKET,baseUrl);
		
			

			if(s3_response.get("status").equals("success")) {
			
				
			
				if(document.getDocumentName().equals("Student Photograph"))
				{
					//update photo in student table
					int j = amazonS3DAO.updateStudentUrl(document.getDocumentURL(),s3_response.get("url"));
					if(j == 0)
						aws_logger.info("Error in Updating ImageUrl in Student Table : "+document.getSfdcDocumentRecordId()+" ,(OriginalImagePath =  "+document.getDocumentURL()+" S3URL:- "+s3_response.get("url"));
				 }
				return "success";
						
			}else {
				aws_logger.info("Error in getting data from uploadLocalFile amazon helper Method For  :- "+document);
				aws_logger.error("uploadLocalFile amazon helper Error:- "+s3_response.get("url"));
					
			}	
			
			
			}catch(Exception e)
			{
				e.printStackTrace();
				aws_logger.error("Upload File Error Document Details "+document,e);
				
				
			}//END OF TRY AND CATCH
			
			
			return "error";
			
			}//END OF BATCH JOB METHOD
		
		
		public void batchJobForExistingHallTicket()
		{
			s3_response = new HashMap<String,String>();
			
			List<FileMigrationBean> hallTickets = amazonS3DAO.getHallTickets();
			
			List<String> error_list = new ArrayList<String>();
			List<String> success_list  = new ArrayList<String>();
			List<String> success_delete_count = new ArrayList<String>();
			List<String> error_delete_count = new ArrayList<String>();
			int counter = 0;
			
			System.out.println("Total size: "+hallTickets.size());
			System.out.println("shiftingDataInS3 Start");
			
			
			for(FileMigrationBean hallTicket:hallTickets)
			{
			try 
			{
				System.out.println("SUCCESS/FAILURE COUNT "+success_list.size()+"/"+ error_list.size());
				
				
				System.out.println("Iteration:- "+(++counter)+"/"+hallTickets.size());
			
			
				final File folder = new File(hallTicket.getFilePath());
			
				if(!folder.exists()) {
					aws_logger.info("File Details Not Found:- " +hallTicket.toString());
					error_list.add("File Details Not Found:- " +hallTicket.toString());
					continue;
				}
			
				String baseUrl = FilenameUtils.getPath(hallTicket.getFilePath());
				String fileName = FilenameUtils.getBaseName(hallTicket.getFilePath())
			                + "." + FilenameUtils.getExtension(hallTicket.getFilePath());
			
			
			
				fileName = baseUrl + fileName;
			
			
				s3_response = amazonS3.uploadLocalFile(hallTicket.getFilePath(),fileName,"hallticket",baseUrl);
			
		
				String OriginalPath = hallTicket.getFilePath();

				if(s3_response.get("status").equals("success")) {
					
					if(hallTicket.getFilePath().startsWith("E:/FeeReceipts"))
						hallTicket.setFilePath(hallTicket.getFilePath().substring(15));
					else
						hallTicket.setFilePath(hallTicket.getFilePath().substring(14));
					
					//Update FilePath In DB
					if((amazonS3DAO.updateHallTicketsUrlLink(hallTicket)) > 0) {
						
						//Delete File From Local
						if(deleteFileFromLocal(OriginalPath) == 1)
						{
							aws_logger.info("SuccessFully Deleted File From Local "+OriginalPath);
							success_delete_count.add(OriginalPath);
						}
						else {
							aws_logger.info("Error in deleting File From Local "+OriginalPath);
							error_delete_count.add(OriginalPath);
							
						}
						aws_logger.info("SuccessFully added file in amazon s3 and DB.  "+hallTicket.toString());
						success_list.add("File Details " +hallTicket.toString());
					}
					else {
						aws_logger.info("Error In uploading in database.(S3 Uploaded SuccessFully) "+hallTicket.toString());
						error_list.add("File Details " +hallTicket.toString() +" DataBase Error");
					}
					
					
				
				
				}else {
					

					aws_logger.error("Error:- "+s3_response.get("url"));
					aws_logger.info("Error In getting data from s3 Service "+hallTicket.toString());
					error_list.add("File Details " +hallTicket.toString());
				
					
				}
			
			}catch(Exception e)
			{
				e.printStackTrace();
				aws_logger.error("Upload File Error ",e);
				aws_logger.info("Error in uploading File."+hallTicket.toString());
				error_list.add("File Details " +hallTicket.toString());
				
			}//END OF TRY AND CATCH
			
			
			
			}//END OF LOOP
			
			
			System.out.println("SUCCESS/FAILURE COUNT "+success_list.size()+"/"+ error_list.size());
			System.out.println("SUCCESS/FAILURE DELETE FROM LOCAL COUNT "+success_delete_count.size()+"/"+ error_delete_count.size());
			
			
			System.out.println("Total Files "+hallTickets.size());
			System.out.println("Success_count "+success_list.size());
			System.out.println("success_list "+success_list.toString());
			System.out.println("Error Count "+error_list.size());
			System.out.println("Error_list "+error_list.toString());
			
			aws_logger.info("Total Files "+hallTickets.size());
			aws_logger.info("Success_count "+success_list.size());
			aws_logger.info("success_list "+success_list.toString());
			aws_logger.info("Error Count "+error_list.size());
			aws_logger.info("Error_list "+error_list.toString());
			aws_logger.info("SUCCESS/FAILURE DELETE FROM LOCAL COUNT "+success_delete_count.size()+"/"+ error_delete_count.size());
			aws_logger.info("Error in Local Delete File  "+error_delete_count.toString());

			}//END OF BATCH JOB METHOD
		
		
			
			
	/*	public void batchJobFileDeleteFromLocal()
		{
			List<FileMigrationBean> files = amazonS3DAO.getFileFromLocal();
			List<String> success_count  = new ArrayList<String>();
			List<String> error_count = new ArrayList<String>();
			int counter = 0;
			
			System.out.println(" Deleting the Files From Local Started ---");
			System.out.println(" Total Files to be deleted."+files.size());
			aws_logger.info("Deleting the Files From Local Started.");
			aws_logger.info("Total Files to be deleted."+files.size());
			
			for(FileMigrationBean fileDetails:files)
			{
				System.out.println("SUCCESS/FAILURE COUNT "+success_count.size()+"/"+ error_count.size());
				
				
				System.out.println("Iteration:- "+(++counter)+"/"+files.size());
				
				if(deleteFileFromLocal("E:/HallTicket/"+fileDetails.getFilePath()) == 1)
				{
					aws_logger.info("SuccessFully Deleted File From Local "+fileDetails.toString());
					success_count.add("SuccessFully Deleted File From Local "+fileDetails.toString());
				}
				else {
					aws_logger.info("Error in deleting File From Local "+fileDetails.toString());
					error_count.add("Error in deleting File From Local "+fileDetails.toString());
				}
			}
			
			System.out.println("Deleting the Files From Local Ended-- ");
			System.out.println("SUCCESS/FAILURE COUNT "+success_count.size()+"/"+ error_count.size());
			System.out.println("Error_count "+error_count.size());
			System.out.println("Success_count "+success_count.size());
			System.out.println("Error_list:- "+error_count.size());
			
			aws_logger.info("Deleting the Files From Local Ended-- ");
			aws_logger.info("SUCCESS/FAILURE COUNT "+success_count.size()+"/"+ error_count.size());
			aws_logger.info("Error_count "+error_count.size());
			aws_logger.info("Success_count "+success_count.size());
			aws_logger.info("Error_list:- "+error_count.size());
			
		}*/
			public int deleteFileFromLocal(String filePath)
			{
				try
				{
					 File file = new File(filePath);
					 file.delete();
					 
					 File folder = new File(file.getParent());
						
					 if(folder.listFiles().length == 0) {
						 FileUtils.deleteDirectory(folder);
					 }
					 
					 return 1;
				}
				catch(Exception e)
				{
					   aws_logger.error("Error in Deleting File From Local",e);
					   return 0;
				}
					          
		    }
			
			/**
			 * 
			 * @param file - Multipartfile to upload
			 * @param fileName StudentProfileDocuments/PmQZ54_27042022.pdf
			 * @param bucketName StudentProfileDocuments
			 * @param folderName StudentProfileDocuments/
			 * @return
			 */
			
			//Upload Student Profile Document
			public String uploadDocumentToS3(MultipartFile file, String fileName,String bucketName,String folderName)
			{
			
				s3_response = new HashMap<String,String>();
			try {
				//upload File Helper
				
				s3_response = amazonS3.uploadFile(file,folderName,bucketName,fileName);
					
					
				if(s3_response.get("status").equals("success")) {
					return s3_response.get("url");
						
				}else {
		 			aws_logger.info("Error in getting response from upload For bucket:- "+bucketName+" , fileName:-  "+fileName+" and response is "+s3_response.get("url"));
		 			return s3_response.get("url");
		 		}
					
				
					
				}catch(Exception e)
				{
					
					aws_logger.error("Error in uploading File. For bucket:- "+bucketName+" , fileName:-  "+fileName,e);
					return e.getMessage();
				}
			}
		
		}
