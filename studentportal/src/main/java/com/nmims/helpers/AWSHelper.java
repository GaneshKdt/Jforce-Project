package com.nmims.helpers;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
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

@Component
public class AWSHelper {
	
	@Value("${AWS_UPLOAD_URL}")
	private String AWS_UPLOAD_URL;
	
	private static final Logger aws_document = LoggerFactory.getLogger("aws_document");
	HashMap<String,String> s3_response = new HashMap<String,String>();
	private HttpHeaders getHeaders() {
		HttpHeaders headers =  new HttpHeaders();
		headers.add("Accept", "application/json");
		headers.add("Content-Type", "application/json");
		return headers;
	}
	
	/*Core function*/
	public String uploadOnAWS(String url) {
		try {
			HttpHeaders headers =  this.getHeaders();
			
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
		}
		catch (Exception e) {
			
			//sessions.updateStatus(sessionType,"error", e.getMessage(),date,webinarId,null);
		}
		return null;
	}
	
	
	
	public String uploadFile(MultipartFile file,String folderPath,String bucketName,String fileName) {


		String url = AWS_UPLOAD_URL+"/uploadFiles";
		
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
		    multipartReqMap.add("keyName", fileName);
		    

		    HttpEntity<LinkedMultiValueMap<String, Object>> reqEntity = new HttpEntity<>(multipartReqMap, headers);
			
		    
			
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, reqEntity, String.class);
			JsonObject jsonResponse = new JsonParser().parse(response.getBody()).getAsJsonObject();
			
			
			if ("200".equalsIgnoreCase(response.getStatusCode().toString())) {
				
				if(jsonResponse.get("status").getAsString().equals("success"))
					return jsonResponse.get("fileUrl").getAsString();
				
			}else
				aws_document.error(" Error Other than 200 status code  (Method Name uploadFile) For bucketName :- "+bucketName+" & fileName :-  "+fileName+" & status code :- ",response.getStatusCode()+" & Json Response :- "+jsonResponse.toString());
		}
		catch (Exception e) {
			
			aws_document.error(" Error in Uploading in aws (Method Name uploadFile) For bucketName :- "+bucketName+" & fileName :-  "+fileName,e);
			
		}
		
		return "";
	
		
		
	}

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
            e.printStackTrace();
            s3_response.put("status","error");
            s3_response.put("url", "Error in Getting data from api"+e.getMessage());
            aws_document.error("Error in Getting data from api(uploadLocalFile)",e);
            aws_document.info("Error in Getting data from api.(uploadLocalFile)");
            
        }
        return s3_response;
    }
	
	/*To Transfer the local files to s3 server
  	 * @Param:- filePath - Whole Path File on local eg. E:\Content\Management Theory and Practice\TRY.pdf
  	 * @Param:- keyName - It is the name of file used in S3. eg. Content\Management Theory and Practice\TRY.pdf
  	 * @param:- bucketName- Name of the bucket present in the s3
  	 * @param:- folderPath- It is folder name which will be created in the s3. eg. Content\Management Theory and Practice\
  	 */
    
	public HashMap<String,String> uploadLocalFileX(String filePath,String keyName,String bucketName,String folderPath) {
		
		HashMap<String,String> s3_response = new HashMap<String,String>();
		
		String url = AWS_UPLOAD_URL+ "/uploadLocalFileX";
//		try {			
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
				s3_response.put("url","");
					
			}
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//			s3_response.put("status","error");
//			s3_response.put("url", "Error in Getting data from api"+e.getMessage());
//			aws_document.error("Error in Getting data from api(uploadLocalFile)",e);
//
//			
//		}
		return s3_response;
	}
	
	public int deleteFileFromLocal(String filePath){
		try{
			 File file = new File(filePath);
			 file.delete();
			 return 1;
		}catch(Exception e){
			aws_document.error(" Error in deleting document from local server",e);
			   return 0;
		}
    }
	
}
