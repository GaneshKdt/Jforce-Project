package com.nmims.strategies.impl;

import java.util.ArrayList;

import com.nmims.util.DateTimeUtil;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.ContentAcadsBean;
import com.nmims.beans.ContentFilesSetbean;
import com.nmims.daos.ContentDAO;
import com.nmims.daos.SessionPlanDAO;
import com.nmims.helpers.FileUploadHelper;
import com.nmims.strategies.UploadStratergy;

@Service("uploadWIthSessionPlan")
public class UploadWIthSessionPlan implements  UploadStratergy
{

	@Autowired
	ContentDAO contentDAO;
	
	@Autowired
	SessionPlanDAO sDao;
	
	HashMap<String,String> response;
	
	private static final Logger logger = Logger.getLogger(UploadWIthSessionPlan.class);

	@Autowired
	FileUploadHelper fileUploadHelper;
	
	@Override
	public HashMap<String,String> createContent(ContentFilesSetbean filesSet) 
	{
		response  = new  HashMap<String,String>();
		List<String> errorFileNames = new ArrayList<String>();
		// TODO Auto-generated method stub
		
		List<ContentAcadsBean> contentFiles = filesSet.getContentFiles();
		int successCount = 0;
		
		String fileNames = "";
		for (int i = 0; i < contentFiles.size(); i++) {

			ContentAcadsBean bean = contentFiles.get(i);
			String fileName = bean.getFileData().getOriginalFilename();  
			String contentName = bean.getName();
			String errorMessage = null;
			if(contentName == null || "".equals(contentName.trim())  ){
				//If no name mentioned for Content, then do not store in Database
				continue;
			}

			//If no file is selected, do not upload any file
			if(fileName == null || "".equals(fileName.trim()) )
				errorFileNames.add("File Not selected for : "+bean.getName());
			else
				errorMessage = fileUploadHelper.uploadContentFileOnS3(bean, filesSet.getSubject());
			
		try {	
			//Check if file saved to Disk successfully
			if(errorMessage.equals("success")){
				bean.setSessionPlanModuleId(filesSet.getId());
				if(filesSet.getId()!=null) {
					String moduleName = sDao.getSessionPlanModuleById(bean.getSessionPlanModuleId()).getTopic();
					bean.setSessionPlanModuleName(moduleName);
				}
				
				if(StringUtils.isBlank(bean.getActiveDate())) 
					bean.setActiveDate(DateTimeUtil.getTheactiveDateForContent(bean.getActiveDate()));
				
				
				//Insert into content table, post table , redis
				long contentId = contentDAO.saveContentFileDetails(bean, filesSet.getSubject(), filesSet.getYear(), filesSet.getMonth());
				
				successCount++;
				fileNames = fileNames + " : " +bean.getName() ;
			}else{
				errorFileNames.add("File Not able to upload  : "+bean.getName());
				logger.info("Error in Uploading file on s3 :- createContent(Strategy for MBA_WX) "+bean.getFilePath());	
			}
		}catch(Exception e)
		{
			errorFileNames.add("Error in Uploading File . please Upload again for content "+bean.getName()+". Error Cause :-"+e.getMessage());
			logger.error("Error in Uploading With Session Plan Content Method name :- createContent(Strategy for MBA_WX)",e);	
		}	
	}
		response.put("error","true");
		response.put("errorMessage",errorFileNames.toString());
		
		response.put("success","true");
		response.put("successMessage",successCount + " Files Uploaded successfully : File Names : "+fileNames);
		return response;
	}
}
