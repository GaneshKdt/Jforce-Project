package com.nmims.strategies.impl;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


import com.nmims.beans.ContentAcadsBean;
import com.nmims.beans.ContentFilesSetbean;
import com.nmims.daos.ContentDAO;
import com.nmims.helpers.FileUploadHelper;
import com.nmims.strategies.UploadStratergy;
import com.nmims.util.DateTimeUtil;

@Service("uploadWithoutSessionPlan")
@Transactional
public class UploadWithoutSessionPlan implements UploadStratergy {
	
	
	@Value( "${CONTENT_PATH}" )
	private String CONTENT_PATH;
	
	@Autowired
	ContentDAO contentDAO;
	
	@Autowired
	FileUploadHelper fileUploadHelper;
	
	HashMap<String,String> response;
	

	private static final Logger logger = LoggerFactory.getLogger("contentService");
	
	//Commented by Riya and rewrote again the method
/*
	
	@Override
	@Transactional(value="transactionManager",readOnly = false)
	public HashMap<String,String> createContent(ContentFilesSetbean filesSet) 
	{
		response  = new  HashMap<String,String>();
		// TODO Auto-generated method stub
		int successCount = 0;
		int errorCount = 0;

		
		List<ContentAcadsBean> contentFiles = filesSet.getContentFiles();
		

		String errorFileNames = "";
		String fileNames = "";	
		//int deleteContentRow = 0;
		String errorMessage = null;
		//String fileno = "";
		
		
		
		
		for (int i = 0; i < contentFiles.size(); i++) {

			ContentAcadsBean bean = contentFiles.get(i);

			String contentName = bean.getName();

			if(contentName == null || "".equals(contentName.trim()) || "".equals(bean.getSubject())){
				//If no name mentioned for Content, then do not store in Database
				continue;
			}

			if(bean.getFileData().isEmpty()){
//				//If no file is selected, do not upload any file
				errorFileNames = errorFileNames + " : "+bean.getName();
				errorMessage = "File Not selected for "+errorFileNames;
			}else{
				//errorMessage = uploadContentFile(bean, filesSet.getSubject());
				
				errorMessage = fileUploadHelper.uploadContentFileOnS3(bean, filesSet.getSubject());
				
				if(!errorMessage.equals("success"))
					errorMessage = uploadContentFile(bean, filesSet.getSubject());
			}

			//Check if file saved to Disk successfully
			if(errorMessage.equals("success")){			
				if(bean.getSessionPlanModuleId() == null) {
					bean.setSessionPlanModuleId(new Long("0"));
				}

				
				bean.setCreatedBy(filesSet.getCreatedBy());
				bean.setLastModifiedBy(filesSet.getLastModifiedBy());
//
				try {
					ArrayList<ContentAcadsBean> consumerProgramStructureIds = getContentIdMasterKeyMappings(filesSet.getSubjectCodeId(),filesSet.getMasterKey());
					long contentId = contentDAO.saveContentFileDetails(bean, filesSet.getSubject(), filesSet.getYear(), filesSet.getMonth());
					String createMappingsError = contentDAO.batchInsertContentIdConsumerProgramStructureIdMappingsForMultipleConsumerIds(contentId,consumerProgramStructureIds); 
					
				/*if (createMappingsError.contentEquals("0")) {
					 deleteContentRow = contentDAO.deleteContentById(contentId);
						errorFileNames = errorFileNames + " : "+bean.getName();
 
					 errorMessage = "Zero applicable ConsumerProgramStructureIds for : "+filesSet.getSubject()+" Rows deleted of content details: "+deleteContentRow;
				
						errorFileNames = errorFileNames  + errorMessage ;

				}*/
				
				/*if (createMappingsError.contentEquals("1")) {
					 deleteContentRow = contentDAO.deleteContentById(contentId);
						errorFileNames = errorFileNames + " : "+bean.getName();

					 errorMessage = "Error in getting ConsumerProgramStructureIds for : "+filesSet.getSubject()+" Rows deleted of content details: "+deleteContentRow;
						errorFileNames = errorFileNames  + errorMessage ;

				
				}*/
				
				/*if(!StringUtils.isBlank(createMappingsError)) {
					//deleteContentRow = contentDAO.deleteContentById(contentId);
					//errorCount++;
					//errorFileNames = errorFileNames + " : "+bean.getName();
					errorMessage = " Please upload again. ";
					errorMessage = "Error in creating mappings of contentId and masterkey,  Rows deleted of content details: "+deleteContentRow + 
												"fileNames"+errorFileNames;
					
					errorFileNames = errorFileNames  + errorMessage ;

			}else {*/
	
				
				//successCount++;
				//fileNames = fileNames + " : " +bean.getName() ;
				
			//}
			/*

				} catch (Exception e) {
					   

					
					contentService.info("Error in uploading content. Method name - createContent(Strategy) " ,e);

					//throw new Exception("Error in file Upload: " + errorMessage);
					errorCount++;
					//errorFileNames = errorFileNames + " : " + bean.getName() + ": " + e.getMessage() ;
					//errorFileNames = errorFileNames + " : "+bean.getName();
					errorMessage = e.getMessage();
					errorMessage = " Please upload again. ";
					errorFileNames = errorFileNames + " : " + bean.getName() + ": " + errorMessage ;
					TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
					
				}
			}else{
				//if (successCount > 0) {
					//response.put("success","true");
					//response.put("successMessage",successCount + " Files Uploaded successfully : File Names : "+fileNames);
				//}
				//response.put("error", "true");
				//response.put("errorMessage", "Files Uploaded successfully : File Names :");
			//	errorCount++;
			//	errorFileNames = errorFileNames +  errorMessage ;
				//return response;
		/*	}
		}
		response.put("success","true");
		response.put("successMessage",successCount + " Files Uploaded successfully : File Names : "+fileNames);
		response.put("error","true");
		response.put("errorMessage",errorCount + " Error in file Upload : "+errorFileNames);
		return response;
	}*/
	
	//Commented BY Riya
	/*private String createContentIdMasterkeyMappings(ContentFilesSetbean filesSet, long contentId) {
		// TODO Auto-generated method stub
		
		if(filesSet.getProgramId().split(",").length>1 
				|| filesSet.getProgramStructureId().split(",").length>1
				|| filesSet.getConsumerTypeId().split(",").length>1 
			)
			{
				// If Any Option is Selected Is "All"
			ArrayList<ContentBean> consumerProgramStructureIds = contentDAO.getconsumerProgramStructureIdsWithSubject(filesSet.getProgramId()
																										 ,filesSet.getProgramStructureId()
																										 ,filesSet.getConsumerTypeId()
																										 ,filesSet.getSubject());
			
			return contentDAO.batchInsertContentIdConsumerProgramStructureIdMappingsForMultipleConsumerIds(contentId,consumerProgramStructureIds); 
			}
			else {
				
			ContentBean consumerProgramStructureId = contentDAO.getConsumerProgramStructureIdByProgramProgramStructureConsumerTypeId(filesSet.getProgramId()
																									,filesSet.getProgramStructureId()
																									,filesSet.getConsumerTypeId()
																									,filesSet.getSubject());
			ArrayList<ContentBean> consumerProgramStructureIds = new ArrayList<>();
			consumerProgramStructureIds.add(consumerProgramStructureId);
			
			return contentDAO.batchInsertContentIdConsumerProgramStructureIdMappingsForMultipleConsumerIds(contentId,consumerProgramStructureIds); 
			}
		
	
	}*/

	private ArrayList<ContentAcadsBean> getContentIdMasterKeyMappings(String subjectCodeId, String masterKey)
	{
		if(!StringUtils.isBlank(subjectCodeId))
			return contentDAO.getMasterKeyForContentMapping(subjectCodeId,"");
		else
			return contentDAO.getMasterKeyForContentMapping("",masterKey);
			
		
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public HashMap<String,String> createContent(ContentFilesSetbean filesSet) throws Exception
	{
		response  = new  HashMap<String,String>();
		// TODO Auto-generated method stub
		int successCount = 0;
		int errorCount = 0;

		
		List<ContentAcadsBean> contentFiles = filesSet.getContentFiles();
		

		List<String> errorFileNames = new ArrayList<String>();
		String fileNames = "";	
		//int deleteContentRow = 0;
		String errorMessage = null;
		//String fileno = "";
		
		
		
		
		for (int i = 0; i < contentFiles.size(); i++) {

			ContentAcadsBean bean = contentFiles.get(i);

			String contentName = bean.getName();

			if(contentName == null || "".equals(contentName.trim()) || "".equals(bean.getSubject())){
				//If no name mentioned for Content, then do not store in Database
				continue;
			}

			if(bean.getFileData().isEmpty()){
//				//If no file is selected, do not upload any file
				errorFileNames.add(errorFileNames + " : "+bean.getName());
				errorMessage = "File Not selected for "+errorFileNames.toString();
			}else{
				//errorMessage = uploadContentFile(bean, filesSet.getSubject());
				
				errorMessage = fileUploadHelper.uploadContentFileOnS3(bean, filesSet.getSubject());
				
				if(!errorMessage.equals("success"))
					errorMessage = fileUploadHelper.uploadContentFile(bean, filesSet.getSubject());
			}

			//Check if file saved to Disk successfully
			if(errorMessage.equals("success")){			
				if(bean.getSessionPlanModuleId() == null) {
					bean.setSessionPlanModuleId(new Long("0"));
				}

				
				bean.setCreatedBy(filesSet.getCreatedBy());
				bean.setLastModifiedBy(filesSet.getLastModifiedBy());
			
				//check the active date of content(the time content should be visible), set the current date if it is null
				if(StringUtils.isBlank(bean.getActiveDate())) 
					bean.setActiveDate(DateTimeUtil.getTheactiveDateForContent(bean.getActiveDate()));
				
					ArrayList<ContentAcadsBean> consumerProgramStructureIds = getContentIdMasterKeyMappings(filesSet.getSubjectCodeId(),filesSet.getMasterKey());
					long contentId = contentDAO.saveContentFileDetails(bean, filesSet.getSubject(), filesSet.getYear(), filesSet.getMonth());
					String createMappingsError = contentDAO.batchInsertContentIdConsumerProgramStructureIdMappingsForMultipleConsumerIds(contentId,consumerProgramStructureIds); 
					
					bean.setSubject(filesSet.getSubject());
					contentDAO.insertionContentWithMappingInTemp(bean,contentId,consumerProgramStructureIds,filesSet.getYear(),filesSet.getMonth());
					
				
	
				
				successCount++;
				fileNames = fileNames + " : " +bean.getName() ;
				
			

			
			}else{
				
				errorCount++;
				errorFileNames.add(errorFileNames +  errorMessage) ;
		
			}
		}
		response.put("success","true");
		response.put("successMessage",successCount + " Files Uploaded successfully : File Names : "+fileNames);
		response.put("error","true");
		response.put("errorMessage",errorCount + " Error in file Upload : "+errorFileNames.toString());
		return response;
	}
}
