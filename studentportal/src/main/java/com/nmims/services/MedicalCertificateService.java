package com.nmims.services;

import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.nmims.daos.ServiceRequestDao;
import com.nmims.helpers.AWSHelper;

@Service
public class MedicalCertificateService {
	@Autowired
	ApplicationContext act;
	@Value("${AWS_UPLOAD_URL}")
	private String AWS_UPLOAD_URL;
	
	@Value("${AWS_SR_FILES_BUCKET}")
	private String srBucket;
	
	@Value("${SR_FILES_S3_PATH}")
	private String SR_FILES_S3_PATH;
	
	@Autowired
	AWSHelper awsHelper;
	
	@Autowired
	ServiceRequestDao serviceRequestDao;

	private static final Logger logger = LoggerFactory.getLogger(CertificateService.class);
		
	public HashMap<String, String> UploadMedicalCertificate(String sapId, String filename,String filepath,String folder) {
		HashMap<String, String> srUpload=new HashMap<String,String>();
		try
		{
		HashMap<String, String> map = new HashMap<>();
			String fullPath = filepath;
			srUpload = awsHelper.uploadLocalFile(fullPath,filename,srBucket, folder);
			if ("success".equals(srUpload.get("status"))) {
				map.put("awsPath", srUpload.get("url"));
				map.put("localPath", fullPath);
			}
			return map;
	}
	catch(RuntimeException e)
	{
		logger.error("failed to upload Medical certificate pdf to AWS " + " For Sapid : "+sapId);
		
		throw new RuntimeException("Error in creating SR");
	}
	}
	}
