package com.nmims.services;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.nmims.beans.ContentAcadsBean;
import com.nmims.daos.ContentDAO;
import com.nmims.helpers.AWSHelper;

@Service
public class AdhocFileUploadService {
	
	@Autowired
	ContentDAO contentDAO;
	
	@Autowired
	AWSHelper awsHelper;
	

	@Value("${AWS_ADHOC_FILE_ACCESS_URL}")
	private String AWS_ADHOC_FILE_ACCESS_URL;
	
	private String baseFolderPath = "academics/";
	
	
	public String fileUpload(MultipartFile file, String userId, String title, Logger logger) throws IOException {

		String extension = "."+FilenameUtils.getExtension(file.getOriginalFilename());
		long currentUnixTime = System.currentTimeMillis() / 1000L;
		String fileName = currentUnixTime+RandomStringUtils.randomAlphanumeric(5)+extension;
		String filePathS3=baseFolderPath+fileName;
		
		boolean check=contentDAO.checkAdhocFileTitle(title);
		if(check) {
		//fileUploadHelper.uploadFile(file,fileName);
		Boolean status=awsHelper.uploadMultiPartFileToS3(file,baseFolderPath , "adhocfilesngasce", filePathS3,logger);
		if(status) {
		logger.info(fileName +" uploaded to s3 server!");
		contentDAO.insertAdhocDocument(fileName, userId, title, logger);
		logger.info("URL saved in Database with title "+title+" and file name :"+fileName);
		}
		}
		return fileName;
	}
	
	
	public ArrayList<ContentAcadsBean> getAllUploadedDocumentsURLByAdmin(String userId){
		ArrayList<ContentAcadsBean> urlList=contentDAO.getAllUploadedDocumentsURL(userId);
		for (ContentAcadsBean contentBean : urlList) {
			String webFileurl=contentBean.getWebFileurl();
			webFileurl=AWS_ADHOC_FILE_ACCESS_URL+webFileurl;
			contentBean.setWebFileurl(webFileurl);
		}
		return urlList;
	}
	
	public boolean deleteFileURLByAdmin(String id, String filePath, Logger logger) {
		boolean flag=false;

		String message=awsHelper.deleteAdhocFileObjectFromS3("adhocfilesngasce", "academics/"+filePath, logger);
		if("success".equals(message)) {
		logger.info(filePath + " deleted successfully from s3 server!");
		flag=contentDAO.deletAdhocFile(id, logger);
		logger.info(filePath + " deleted successfully from database!");
		}
		return flag;
	}
	
	public String getFilePath(String id) {
		
		String filePath=contentDAO.getAdhocFilePath(id);
	
		return filePath;
	}
}
