package com.nmims.services;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.nmims.beans.CallRepositoryBean;
import com.nmims.daos.CallRepositoryDAO;
import com.nmims.helpers.AmazonS3Helper;

@Service
public class CallRepositoryService {

	@Autowired
	CallRepositoryDAO callRepositoryDAO;

	@Autowired 
	AmazonS3Helper amazonS3Helper;

	@Value( "${AWS_CALL_REPOSITORY_BUCKET}" )
	private String AWS_CALL_REPOSITORY_BUCKET;

	public ArrayList<CallRepositoryBean> getAllUploadedCalls() throws Exception{

		ArrayList<CallRepositoryBean> callList = new ArrayList<CallRepositoryBean>();

		callList = callRepositoryDAO.getAllUploadedCalls();
		
		return callList;
	}

	public void createCallUploadRecord( CallRepositoryBean bean ) throws Exception {
		
		callRepositoryDAO.createCallRepositoryUploadRecord( bean );

	}
	
	public String getKeyNameForUpload( CallRepositoryBean bean, MultipartFile file ) {

		String call_category = bean.getCategory().substring(0, 1).toUpperCase() + bean.getCategory().substring(1);
		String fileName = file.getOriginalFilename();   
		String randomNumber =RandomStringUtils.randomAlphanumeric(4);
		
		fileName = fileName.replaceAll(" ", "_"); // added to replace all spaces in the filename.
		fileName = fileName.replaceFirst("&", "_");
		
		call_category = call_category.replaceAll("[^.,/,a-zA-Z0-9]+","_");
		fileName = fileName.replaceAll("[^.,/,a-zA-Z0-9]+", "_");
		
		// creating the file path with the folder for every category 
		String keyName = call_category + "/" + call_category + "_" + randomNumber + "_" + fileName;

		return keyName;
		
	}
	
	public String getFolderPathForUpload( CallRepositoryBean bean ) {

		String folderPath = bean.getCategory().substring(0, 1).toUpperCase() + 
				bean.getCategory().substring(1) + "/";
		
		folderPath = folderPath.replaceAll("[^.,/,a-zA-Z0-9]+", "_");;
		
		return folderPath;
		
	}

	public Boolean uploadToCallRepository( CallRepositoryBean bean, MultipartFile file, 
			String folderPath, String keyName ) throws Exception{
		
		Boolean success = Boolean.FALSE;
		
		if( file != null && !file.isEmpty() ){

			System.out.println("folderPath=" + folderPath + " &keyName=" + keyName);
			
			HashMap<String,String> s3_response = amazonS3Helper.uploadFile(file, folderPath, 
					AWS_CALL_REPOSITORY_BUCKET, keyName);
			
			if( s3_response.get("status") == "error" ) {
				
				success = false;
				
			}else {
				
				bean.setUrl( s3_response.get("url") );
				
				createCallUploadRecord( bean );
					
				success = true;
				
			}
			
			return success;

		}else {
			
			return success;
			
		}
		
	}
	
}
