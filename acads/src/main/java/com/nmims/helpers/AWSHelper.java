package com.nmims.helpers;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nmims.dto.AWSDTO;

import org.slf4j.LoggerFactory;


@Component
public class AWSHelper {
	
	@Value("${AWS_UPLOAD_URL}")
	private String AWS_UPLOAD_URL;
	


	private static final Logger contentService = LoggerFactory.getLogger("contentService");
	

	HashMap<String,String> s3_response = null;
	

	private static final Logger aws_logger = LoggerFactory.getLogger("fileMigrationService");
	
	private HttpHeaders getHeaders() {
		HttpHeaders headers =  new HttpHeaders();
		headers.add("Accept", "application/json");
		headers.add("Content-Type", "application/json");
		return headers;
	}
	
	/*Core function*/
	public String uploadOnAWS(String url) {
		try {
			HttpHeaders headers =  this.getHeaders();	//get zoom required header
			
			RestTemplate restTemplate = new RestTemplate();
			
			HttpEntity<String> entity = new HttpEntity<String>("",headers);
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
			JsonObject jsonResponse = new JsonParser().parse(response.getBody()).getAsJsonObject();
			if ("200".equalsIgnoreCase(response.getStatusCode().toString())) {
				String status = jsonResponse.get("status").getAsString();
				if("success".equalsIgnoreCase(status)) {
					return jsonResponse.get("fileUrl").getAsString();
				}
			}
			System.out.println("Response code: " + response.getStatusCode().toString());
			System.out.println("Response: " + response.getBody().toString());
		}
		catch (Exception e) {
			  
			//sessions.updateStatus(sessionType,"error", e.getMessage(),date,webinarId,null);
		}
		return null;
	}
	
	
	
/*
     * @Param:- keyName - It is the name of file used in S3. eg. Content\Management Theory and Practice\TRY.pdf
     * @param:- bucketName- Name of the bucket present in the s3
     * @param:- folderPath- It is folder name which will be created in the s3. eg. Content\Management Theory and Practice\
     */
    
    public Boolean uploadMultiPartFileToS3(MultipartFile file,String folderPath,String bucketName,String fileName, Logger logger) throws IOException
    {
    	boolean flag=false;
        String url = AWS_UPLOAD_URL+"/uploadFiles" + "?folderPath=" + folderPath + "&keyName=" + fileName + "&bucketName=" + bucketName ;
        
            HttpHeaders headers =  this.getHeaders();    //get zoom required header
            
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
            
            logger.info("Response status code from AWSFILEUPLOAD while upload file: "+response.getStatusCode().toString() +" upload status : "+jsonResponse.get("status").getAsString());
            
            if("200".equals(response.getStatusCode().toString()) && "success".equals(jsonResponse.get("status").getAsString())){
            	flag=true;
            	//return true if status code is 200 and stutus success
            }
            
        return flag; 
    }
    
    /*To Delete the MultipartFile files from s3 server
     * @Param:- keyName - It is the name of file used in S3. eg. academics\test.rar
     * @param:- bucketName- Name of the bucket present in the s3
     */
    
    public String deleteAdhocFileObjectFromS3(String bucketName,String keyName, Logger logger)
    {
        String url = AWS_UPLOAD_URL+"/deleteAdhocFiles" + "?keyName=" + keyName + "&bucketName=" + bucketName ;
        
        try {HttpHeaders headers =  this.getHeaders();

        RestTemplate restTemplate = new RestTemplate();

        HttpEntity<String> entity = new HttpEntity<String>("",headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        JsonObject jsonResponse = new JsonParser().parse(response.getBody()).getAsJsonObject();
        
        logger.info("Response status code from AWSFILEUPLOAD while deleting file: "+response.getStatusCode().toString());
        
        if ("200".equalsIgnoreCase(response.getStatusCode().toString())) {
            String status = jsonResponse.get("status").getAsString();
            logger.info(" Delete status : "+jsonResponse.get("status").getAsString());
            if("success".equalsIgnoreCase(status)) {
            	 return "success";
            }
        }}
        catch (Exception e) {
              
            logger.error("Error in Getting data from api (Delete API)",e);
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            logger.error("Error in Getting data from api (deleteAdhocFiles) from s3 server "+errors.toString());
        }
        return "error"; 
    }
    

//Upload File From Local System To S3
	
  	/*To Transfer the local files to s3 server
  	 * @Param:- filePath - Whole Path File on local eg. E:\Content\Management Theory and Practice\TRY.pdf
  	 * @Param:- keyName - It is the name of file used in S3. eg. Content\Management Theory and Practice\TRY.pdf
  	 * @param:- bucketName- Name of the bucket present in the s3
  	 * @param:- folderPath- It is folder name which will be created in the s3. eg. Content\Management Theory and Practice\
  	 */
    
	public HashMap<String,String> uploadLocalFileX(String filePath,String keyName,String bucketName,String folderPath) {
		
		HashMap<String,String> s3_response = new HashMap<String,String>();
		
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

			
		}
		return s3_response;
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

	 /*To upload the MultipartFile files to s3 server
		 * @Param:- fileName - It is the name of file used in S3. eg. Content\Management Theory and Practice\TRY.pdf
		 * @param:- bucketName- Name of the bucket present in the s3
		 * @param:- folderPath- It is folder name which will be created in the s3. eg. Content\Management Theory and Practice\
		 * 
		 */
		
		public HashMap<String,String> uploadFile(MultipartFile file,String folderPath,String bucketName,String fileName)
		{
			
			String url = AWS_UPLOAD_URL+"/uploadFilesX";
			s3_response = new HashMap<String,String>();
			try {
				
				HttpHeaders headers =  this.getHeaders();	
				
				RestTemplate restTemplate = new RestTemplate();
				
			    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

			    LinkedMultiValueMap<String, String> pdfHeaderMap = new LinkedMultiValueMap<>();
			    pdfHeaderMap.add("Content-disposition", "form-data; name=filex; filename=" + file.getOriginalFilename());
			    pdfHeaderMap.add("Content-type", file.getContentType());
			    HttpEntity<byte[]> doc = new HttpEntity<byte[]>(file.getBytes(), pdfHeaderMap);

			    LinkedMultiValueMap<String, Object> multipartReqMap = new LinkedMultiValueMap<>();
			    multipartReqMap.add("filex", doc);
			    multipartReqMap.add("bucketName", bucketName);
			    multipartReqMap.add("folderPath", folderPath);
			    multipartReqMap.add("fileName", fileName);
			    
			    
			    

			   // HttpEntity<LinkedMultiValueMap<String, Object>> reqEntity = new HttpEntity<>(multipartReqMap, headers);
			   
			    
				
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
				e.printStackTrace();
				contentService.error("Error in Getting data from Api (uploadFile)",e);
				s3_response.put("status","error");
				s3_response.put("url", "Error in Getting data from api. "+e.getMessage());
			}
			return s3_response;
		}
	
}
