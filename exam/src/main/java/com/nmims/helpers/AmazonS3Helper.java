package com.nmims.helpers;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import com.google.gson.JsonObject;
import org.springframework.http.HttpEntity;

import com.google.gson.JsonParser;
import com.nmims.beans.AWSDTO;

import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.http.MediaType;


@Component
public class AmazonS3Helper 
{

	@Value("${AWS_UPLOAD_URL}")
	private String AWS_UPLOAD_URL;
	
	private static final Logger aws_logger = LoggerFactory.getLogger("fileMigrationService");
	
	private static final Logger marsheetService = LoggerFactory.getLogger("marsheetService");
	
	HashMap<String,String> s3_response = new HashMap<String,String>();
	
	private HttpHeaders getHeaders() {
		HttpHeaders headers =  new HttpHeaders();
		headers.add("Accept", "application/json");
		headers.add("Content-Type", "application/json");
		return headers;
	}
	
	/*To Transfer the local files to s3 server
	 * @Param:- filePath - Whole Path File on local eg. E:\Content\Management Theory and Practice\TRY.pdf
	 * @Param:- keyName - It is the name of file used in S3. eg. Content\Management Theory and Practice\TRY.pdf
	 * @param:- bucketName- Name of the bucket present in the s3
	 * @param:- folderPath- It is folder name which will be created in the s3. eg. Content\Management Theory and Practice\
	 */
//	public HashMap<String,String> uploadLocalFile(String folderPath,String keyName,String bucketName) {
		
	public HashMap<String,String> uploadLocalFile(String filePath,String keyName,String bucketName,String folderPath) {

		String url = AWS_UPLOAD_URL+ "/uploadLocalFile" + "?filePath=" + filePath + "&keyName=" + keyName + "&bucketName=" + bucketName + "&folderPath=" +folderPath;
		
		try {
			HttpHeaders headers =  this.getHeaders();	
			
			RestTemplate restTemplate = new RestTemplate();
			
			HttpEntity<String> entity = new HttpEntity<String>("",headers);
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
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
			
			s3_response.put("status","error");
			s3_response.put("url", "Error in Getting data from api"+e.getMessage());
			aws_logger.error("Error in Getting data from api(uploadLocalFile)",e);
			aws_logger.info("Error in Getting data from api.(uploadLocalFile)");
			
		}
		return s3_response;
	}
	
	public HashMap<String,String> uploadFiles(CommonsMultipartFile file,String folderPath,String bucketName,String fileName) {
		String url = AWS_UPLOAD_URL+"/uploadFiles";
		try {
			HttpHeaders headers =  this.getHeaders();	
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);
			LinkedMultiValueMap<String, String> pdfHeaderMap = new LinkedMultiValueMap<>();
			pdfHeaderMap.add("Content-disposition", "form-data; name=filex; filename=" + fileName);
			pdfHeaderMap.add("Content-type", file.getContentType());
			HttpEntity<byte[]> doc = new HttpEntity<byte[]>(file.getBytes(), pdfHeaderMap);
			LinkedMultiValueMap<String, Object> multipartReqMap = new LinkedMultiValueMap<>();
			multipartReqMap.add("filex", doc);
			multipartReqMap.add("bucketName", bucketName);
			multipartReqMap.add("folderPath", folderPath);
			multipartReqMap.add("keyName", fileName);
			
			RestTemplate restTemplate = new RestTemplate();
			
			ResponseEntity<String> response = restTemplate.postForEntity(url,multipartReqMap, String.class);
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
			
			s3_response.put("status","error");
			s3_response.put("url", "Error in Getting data from api"+e.getMessage());
			aws_logger.error("Error in Getting data from api(uploadLocalFile)",e);
			aws_logger.info("Error in Getting data from api.(uploadLocalFile)");
			
		}
		return s3_response;
	}	


	public String uploadMiltipartFile(MultipartFile file, String folderPath, 
			String bucketName, String fileName) throws Exception{

		String url = AWS_UPLOAD_URL+"/uploadFiles" + "?folderPath=" + folderPath + "&keyName=" + fileName + "&bucketName=" + bucketName ;

		String returnUrl = "";

		HttpHeaders headers =  this.getHeaders();	//get zoom required header

		RestTemplate restTemplate = new RestTemplate();

		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		LinkedMultiValueMap<String, String> pdfHeaderMap = new LinkedMultiValueMap<>();
		pdfHeaderMap.add("Content-disposition", "form-data; name=filex; filename=" + file.getOriginalFilename());
		pdfHeaderMap.add("Content-type", file.getContentType());
		HttpEntity<byte[]> doc = new HttpEntity<byte[]>(file.getBytes(), pdfHeaderMap);

		LinkedMultiValueMap<String, Object> multipartReqMap = new LinkedMultiValueMap<>();
		multipartReqMap.add("filex", doc);

		HttpEntity<LinkedMultiValueMap<String, Object>> reqEntity = new HttpEntity<>(multipartReqMap, headers);
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, reqEntity, String.class);
		JsonObject jsonResponse = new JsonParser().parse(response.getBody()).getAsJsonObject();

		s3_response = new HashMap<String,String>();

		if ("200".equalsIgnoreCase(response.getStatusCode().toString())) {

			returnUrl = jsonResponse.get("fileUrl").getAsString();

		}else {
			throw new Exception("Error from aws : "+response.getBody());
		}

		return returnUrl;

	}

public HashMap<String,String> checkWhetherFilePresentOnS3(String bucketName,String keyName)
{
	 HashMap<String,String> s3_response = new HashMap<String,String>();
       String url = AWS_UPLOAD_URL+"/checkFilePresentOnS3" ;
       
    try {
       AWSDTO documents = new AWSDTO();
		documents.setBucketName(bucketName);
		documents.setKeyName(keyName);
			
       	
		RestTemplate restTemplate = new RestTemplate();
			
		ResponseEntity<String> response = restTemplate.postForEntity(url, documents, String.class);	
		JsonObject jsonResponse = new JsonParser().parse(response.getBody()).getAsJsonObject();
       
       
       if ("200".equalsIgnoreCase(response.getStatusCode().toString())) {
			s3_response.put("status",jsonResponse.get("status").getAsString());
			s3_response.put("url", jsonResponse.get("fileUrl").getAsString());
       }else {
       	s3_response.put("status","error");
       	s3_response.put("url","Error Getting code other than 200 "+response.getStatusCode().toString());
       	aws_logger.error("Error in Getting data from api(checkWhetherFilePresentOnS3)",response.getStatusCode().toString());
       }
     
    }catch (Exception e) {
           
           aws_logger.error("Error in Getting data from api(checkWhetherFilePresentOnS3)",e);
       }
       return s3_response; 
   }

//Using DTO

/*To Transfer the local files to s3 server
 * @Param:- filePath - Whole Path File on local eg. E:\Content\Management Theory and Practice\TRY.pdf
 * @Param:- keyName - It is the name of file used in S3. eg. Content\Management Theory and Practice\TRY.pdf
 * @param:- bucketName- Name of the bucket present in the s3
 * @param:- folderPath- It is folder name which will be created in the s3. eg. Content\Management Theory and Practice\
 */
public HashMap<String,String> uploadLocalFileX(String filePath,String keyName,String bucketName,String folderPath) {
	
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
		marsheetService.error("(uploadLocalFileX) jsonResponse:- ",jsonResponse.toString());
		marsheetService.error("(uploadLocalFileX) response:- ",response.toString());
	}
	catch (Exception e) {
		
		
		s3_response.put("status","error");
		s3_response.put("url", "Error in Getting data from api"+e.getMessage());
		marsheetService.error("Error in Getting data from api(uploadLocalFileX)",e);
		
	}
	return s3_response;
}
}
