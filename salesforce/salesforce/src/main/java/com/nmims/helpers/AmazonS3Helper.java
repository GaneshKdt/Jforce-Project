package com.nmims.helpers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nmims.beans.AmazonS3Bean;
import com.nmims.daos.AmazonS3Dao;
import com.nmims.dto.AWSDTO;
import com.nmims.services.AmazonS3Service;


@Component
public class AmazonS3Helper 
{
	@Value("${ENVIRONMENT}")
	private String ENVIRONMENT;

	@Value("${AWS_UPLOAD_URL}")
	private String AWS_UPLOAD_URL;
	
	@Value("${AWS_UPLOAD_URL_LOCAL}")
	private String AWS_UPLOAD_URL_LOCAL;
	
	private static final Logger aws_logger = LoggerFactory.getLogger("aws_document");
	private static final Logger logger = LoggerFactory.getLogger(AmazonS3Helper.class);
	
	@Autowired
	AmazonS3Dao amazonS3DAO;
	
	HashMap<String,String> s3_response = new HashMap<String,String>();
	
	private HttpHeaders getHeaders() {
		HttpHeaders headers =  new HttpHeaders();
		headers.add("Accept", "application/json");
		headers.add("Content-Type", "application/json");
		return headers;
	}
	
	/*To upload the MultipartFile files to s3 server
	 * @Param:- keyName - It is the name of file used in S3. eg. Content\Management Theory and Practice\TRY.pdf
	 * @param:- bucketName- Name of the bucket present in the s3
	 * @param:- folderPath- It is folder name which will be created in the s3. eg. Content\Management Theory and Practice\
	 */
	
	public HashMap<String,String> uploadFile(MultipartFile file,String folderPath,String bucketName,String fileName) {
		
//		try {
//			folderPath = URLEncoder.encode(folderPath,StandardCharsets.UTF_8.toString());
//			fileName = URLEncoder.encode(fileName,StandardCharsets.UTF_8.toString());
//			
//		}
//		catch (Exception e) {
//			// TODO: handle exception
//		}
		
		
		//String url = AWS_UPLOAD_URL+"/uploadFiles" + "?folderPath=" + folderPath + "&keyName=" + fileName + "&bucketName=" + bucketName ;
		String url = AWS_UPLOAD_URL+"/uploadFilesX";
		
		try {
			
			HttpHeaders headers =  this.getHeaders();	//get zoom required header
			
			RestTemplate restTemplate = new RestTemplate();
			
		    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		    LinkedMultiValueMap<String, String> pdfHeaderMap = new LinkedMultiValueMap<>();
		    pdfHeaderMap.add("Content-disposition", "form-data; name=filex; filename=" + file.getOriginalFilename());
		    pdfHeaderMap.add("Content-type", file.getContentType());
		    HttpEntity<byte[]> doc = new HttpEntity<byte[]>(file.getBytes(), pdfHeaderMap);

		    LinkedMultiValueMap<String, Object> multipartReqMap = new LinkedMultiValueMap<>();
		    multipartReqMap.add("filex", doc);
		    multipartReqMap.add("folderPath", folderPath);
		    multipartReqMap.add("bucketName", bucketName);
		    multipartReqMap.add("fileName", fileName);
		    

		    HttpEntity<LinkedMultiValueMap<String, Object>> reqEntity = new HttpEntity<>(multipartReqMap, headers);
			
		    
			
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, reqEntity, String.class);
			JsonObject jsonResponse = new JsonParser().parse(response.getBody()).getAsJsonObject();
			
			s3_response = new HashMap<String,String>();
			//System.out.println("json "+response.getStatusCode().toString());
			if ("200".equalsIgnoreCase(response.getStatusCode().toString())) {
				
				s3_response.put("status",jsonResponse.get("status").getAsString());
				s3_response.put("url", jsonResponse.get("fileUrl").getAsString());
				
			}else {
					s3_response.put("status","error");
					s3_response.put("url","Error Getting code other than 200 "+response.getStatusCode().toString());
								
			 }
			
			
		}
		catch (Exception e) {
			e.printStackTrace();
			aws_logger.error("Error in Getting data from api (uploadFile)",e);
			aws_logger.info("Error in Getting data from api.(uploadFile)");
			s3_response.put("status","error");
			s3_response.put("url", "Error in Getting data from api. "+e.getMessage());
			//sessions.updateStatus(sessionType,"error", e.getMessage(),date,webinarId,null);
		}
		return s3_response;
	
		
		
	}
	
	/*To Transfer the local files to s3 server
	 * @Param:- filePath - Whole Path File on local eg. E:\Content\Management Theory and Practice\TRY.pdf
	 * @Param:- keyName - It is the name of file used in S3. eg. Content\Management Theory and Practice\TRY.pdf
	 * @param:- bucketName- Name of the bucket present in the s3
	 * @param:- folderPath- It is folder name which will be created in the s3. eg. Content\Management Theory and Practice\
	 */
	public HashMap<String,String> uploadLocalFile(String filePath,String keyName,String bucketName,String folderPath) {
		
		//String url = AWS_UPLOAD_URL+ "/uploadLocalFile" + "?filePath=" + filePath + "&keyName=" + keyName + "&bucketName=" + bucketName + "&folderPath=" +folderPath;
		String url = AWS_UPLOAD_URL+ "/uploadLocalFileX";
		try {
			//HttpHeaders headers =  this.getHeaders();	
			
			AWSDTO documents = new AWSDTO();
			documents.setFilePath(filePath);
			documents.setBucketName(bucketName);
			documents.setKeyName(keyName);
			documents.setFolderPath(folderPath);
			
		
			
			RestTemplate restTemplate = new RestTemplate();
			
			ResponseEntity<String> response = restTemplate.postForEntity(url, documents, String.class);	
			JsonObject jsonResponse = new JsonParser().parse(response.getBody()).getAsJsonObject();
		
			if ("200".equalsIgnoreCase(response.getStatusCode().toString())) {
					s3_response.put("status",jsonResponse.get("status").getAsString());
					s3_response.put("url", jsonResponse.get("fileUrl").getAsString());
			}else {
				s3_response.put("status","error");
				s3_response.put("url","Error Getting code other than 200 "+response.getStatusCode().toString());
					
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			s3_response.put("status","error");
			s3_response.put("url", "Error in Getting data from api"+e.getMessage());
			aws_logger.error("Error in Getting data from api(uploadLocalFile)",e);
			aws_logger.info("Error in Getting data from api.(uploadLocalFile)");
			
		}
		return s3_response;
	}
	
	public HashMap<String,String> downloadFile(String filePath,String keyName,String bucketName) {
		
		//String url = AWS_UPLOAD_URL+ "/uploadLocalFile" + "?filePath=" + filePath + "&keyName=" + keyName + "&bucketName=" + bucketName + "&folderPath=" +folderPath;
		String url = AWS_UPLOAD_URL+ "/downloadFromS3";
		try {
			//HttpHeaders headers =  this.getHeaders();	
			
			AWSDTO documents = new AWSDTO();
			documents.setFilePath(filePath);
			documents.setBucketName(bucketName);
			documents.setKeyName(keyName);
			
			RestTemplate restTemplate = new RestTemplate();
			
			ResponseEntity<String> response = restTemplate.postForEntity(url, documents, String.class);	
			JsonObject jsonResponse = new JsonParser().parse(response.getBody()).getAsJsonObject();
		
			if ("200".equalsIgnoreCase(response.getStatusCode().toString())) {
					s3_response.put("status",jsonResponse.get("status").getAsString());
			}else {
				s3_response.put("status","error");
			}
		}
		catch (Exception e) {
			logger.info("Exception in downloadFile from AmazonS3Helper "+e);
			e.printStackTrace();
			s3_response.put("status","error");
		}
		return s3_response;
	}
	
	public String uploadOnAWS(String url) {
		try {
			HttpHeaders headers =  this.getHeaders();
			
			RestTemplate restTemplate = new RestTemplate();
			
			HttpEntity<String> entity = new HttpEntity<String>("",headers);
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
			logger.info("Response Body from upload method in AWS project:"+response.getBody());
			JsonObject jsonResponse = new JsonParser().parse(response.getBody()).getAsJsonObject();
			if ("200".equalsIgnoreCase(response.getStatusCode().toString())) {
				String status = jsonResponse.get("status").getAsString();
				if("success".equalsIgnoreCase(status)) {
					return jsonResponse.get("fileUrl").getAsString();
				}
			}
			//System.out.println("Response code: " + response.getStatusCode().toString());
			//System.out.println("Response: " + response.getBody().toString());
		}
		catch (Exception e) {
			  logger.info("Exception in uploadOnAws:"+e);
			//sessions.updateStatus(sessionType,"error", e.getMessage(),date,webinarId,null);
		}
		return null;
	}
	
	/**
	 * Uploading the file content stored as a byte array into Amazon S3
	 * @param fileContent - content of the file body stored as a byte array
	 * @param fileName - Original Name of the file
	 * @param fileMimeType - MIME Type of the file
	 * @param bucketName - Name of the Amazon S3 Bucket for the file to be stored in
	 * @param bucketFolderName - Folder name inside of the Amazon S3 Bucket where the file will be stored
	 * @param filePathName - complete Path of the file to be stored in Amazon S3
	 * @return - Map containing the response received from the AWS file upload API
	 */
	public Map<String, String> uploadByteArrayFile(byte[] fileContent, String fileName, String fileMimeType, String bucketName, String bucketFolderName, String filePathName) { 
		String url = (ENVIRONMENT.equals("PROD")) ? (AWS_UPLOAD_URL + "/uploadFilesX") : (AWS_UPLOAD_URL_LOCAL + "uploadFilesX");
		
		RestTemplate restTemplate = new RestTemplate();
		HttpEntity<MultiValueMap<String, Object>> requestEntity = createFileUploadRequestEntity(fileContent, fileName, fileMimeType, bucketName, bucketFolderName, filePathName);
		aws_logger.info("RequestEntity Object created with headers: {} and body: {}", requestEntity.getHeaders().toString(), requestEntity.getBody().toString());
		ParameterizedTypeReference<HashMap<String, String>> typeReference = new ParameterizedTypeReference<HashMap<String,String>>() {};
		
		aws_logger.info("Using RestTemplate for POST method request to url: {}", url);
		ResponseEntity<HashMap<String, String>> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, typeReference);
		s3_response = responseEntity.getBody();			//Storing the response body received from the AWS File Upload API
		aws_logger.info("Response body obtained from the AWS uploadFilesX method request: {}", s3_response.toString());
		return s3_response;
	}
	
	/**
	 * Creating an Request Entity consisting the headers and body required for an API Request 
	 * @param fileContent - content of the file body stored as a byte array
	 * @param fileName - Original Name of the file
	 * @param fileMimeType - MIME Type of the file
	 * @param bucketName - Name of the Amazon S3 Bucket for the file to be stored in
	 * @param bucketFolderName - Folder name inside of the Amazon S3 Bucket where the file will be stored
	 * @param filePathName - complete Path of the file to be stored in Amazon S3
	 * @return - created Request Entity
	 */
	private HttpEntity<MultiValueMap<String, Object>> createFileUploadRequestEntity(byte[] fileContent, String fileName, String fileMimeType, String bucketName, String bucketFolderName, String filePathName) {
		aws_logger.info("Creating a Request Entity Object for {} request.", MediaType.MULTIPART_FORM_DATA_VALUE);
		//Creating Headers required for the HttpEntity
		HttpHeaders headers =  new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		
		//Creating an HttpEntity consisting of the file and its attributes
	    MultiValueMap<String, String> fileHeaderMap = new LinkedMultiValueMap<>();
	    fileHeaderMap.add("Content-disposition", "form-data; name=filex; filename=" + fileName);
	    fileHeaderMap.add("Content-type", fileMimeType);
	    HttpEntity<byte[]> file = new HttpEntity<>(fileContent, fileHeaderMap);

	    //Creating a LinkedMultiValueMap of key/value pairs for the HttpEntity body
	    MultiValueMap<String, Object> multipartRequestBodyMap = new LinkedMultiValueMap<>();
	    multipartRequestBodyMap.add("filex", file);
	    multipartRequestBodyMap.add("folderPath", bucketFolderName);
	    multipartRequestBodyMap.add("bucketName", bucketName);
	    multipartRequestBodyMap.add("fileName", filePathName);

		return new HttpEntity<MultiValueMap<String, Object>>(multipartRequestBodyMap, headers);
	}
}
