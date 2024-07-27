package com.nmims.strategies.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.transaction.interceptor.TransactionAspectSupport;


import com.nmims.beans.ContentAcadsBean;
import com.nmims.controllers.ContentController;
import com.nmims.daos.ContentDAO;
import com.nmims.strategies.UpdateContentStrategy;

@Service("updateContentWithoutSessionPlan")
public class UpdateContentWithoutSessionPlan implements UpdateContentStrategy {
	
	@Value("${CONTENT_PATH}")
	private String CONTENT_PATH;

	@Autowired
	ContentDAO contentDAO;

	HashMap<String, String> response;

	
	private static final Logger logger =Logger.getLogger("contentService");

	
	/*Whole Updation of a content */
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public HashMap<String, String> updateContent(ContentAcadsBean contentFromForm) throws Exception {
		// TODO Auto-generated method stub
		response  = new  HashMap<String,String>();
		//ContentBean content = contentDAO.findById(contentFromForm.getId());

		//try {
			/*if("true".equalsIgnoreCase(content.getAllowedToUpdate())) {
			int deleteMappingsrows = contentDAO.deleteContentIdMasterkeyMappingsById(content.getId());
			
				if(deleteMappingsrows < 1 ) {
					response.put("error", "true");
					response.put("request", "Error in deleting mappings of contentId and masterkey");
//					return searchContent(request,response, content);
				}
				
				String createMappingsError = createContentIdMasterkeyMappings(contentDAO,contentFromForm,content.getId());
			
				if(!StringUtils.isBlank(createMappingsError)) {
					response.put("error", "true");
					response.put("request", "Error in creating mappings of contentId and masterkey");
				}
			}*/
			
			//Update the Whole Content in content table

			int j =contentDAO.updateContent(contentFromForm);
			
			//Update the Whole Content in Temp table
			int i = contentDAO.updateWholeContentWithMappingInTemp(contentFromForm);
			
			/*It is not present in the temporary table */
			if(i == 0)
			{
				insertWholeContentIfNotPresentInTemp(contentFromForm);					
			}

			response.put("success","true");
			response.put("successMessage", j+ " Content details updated successfully");
		

		//return searchContent(request,response, content);
		return response;
		
		
		
		// TODO Auto-generated method stub
//		HashMap<String,String> response = new  HashMap<String,String>();

//		
//		try {
//			
//			ContentBean content =  contentDAO.findById(contentFromForm.getId());
//			content.setConsumerTypeId(contentFromForm.getConsumerTypeId());
//			content.setProgramStructureId(contentFromForm.getProgramStructureId());
//			content.setProgramId(contentFromForm.getProgramId());
//			content.setConsumerProgramStructureId(contentFromForm.getConsumerProgramStructureId());
//			content.setName(contentFromForm.getName());
//			content.setDescription(contentFromForm.getDescription());
//			content.setWebFileurl(contentFromForm.getWebFileurl());
//			content.setUrlType(contentFromForm.getUrlType());
//			content.setContentType(contentFromForm.getContentType());
//			
//			ContentFilesSetbean fileset = new ContentFilesSetbean();
//			fileset.setConsumerTypeId(content.getConsumerTypeId());
//			fileset.setProgramStructureId(content.getProgramStructureId());
//			fileset.setProgramId(content.getProgramId());
//			fileset.setConsumerProgramStructureId(Integer.parseInt(content.getConsumerProgramStructureId()));;
//			fileset.setSubject(content.getSubject());
//			
//
//			content.setCreatedBy(userId);
//			content.setLastModifiedBy(userId);
//
//			long newContentId = contentDAO.saveContentFileDetails(content, content.getSubject(), content.getYear(), content.getMonth());
//			
//			if(newContentId < 1 ) {
//				response.put("error", "true");
//				response.put("errorMessage", "Error in file saving file details to DB, FileName : "+content.getName()
//									+ "");
//			//	return "forward:/viewContentForSubject?subject="+content.getSubject();
//				
//			}else {//Create mappings of contentId and masterkey
//				
//				String createMappingsError = createContentIdMasterkeyMappings(dao,fileset,newContentId);
//				
//				if(!StringUtils.isBlank(createMappingsError)) {
//					int deleteContentRow = contentDAO.deleteContentById(newContentId);
//					response.put("error", "true");
//					response.put("errorMessage", "Error in creating mappings of contentId and masterkey, FileName : "+content.getName()+" Rows deleted of content details: "+deleteContentRow
//										+ "");
//					
//				}
//				
//			}
//			
//			//delete old mappings of masterkey and id 
//			int rowsDeletedOfOldMapping = dao.deleteContentIdMasterkeyMappingsByIdNMasterKey(content.getId(), contentFromForm.getConsumerProgramStructureId());
//			
//
//			if(rowsDeletedOfOldMapping < 1 ) {
//				request.setAttribute("error", "true");
//				request.setAttribute("errorMessage", "Error in file saving file details to DB, FileName : "+content.getName()
//									+ "");
//				return "forward:/viewContentForSubject?subject="+content.getSubject();
//				
//			}
//			
//			
//			setSuccess(request, "Content details created successfully");
//		} catch (Exception e) {
//			  
//			setError(request, "Error in updating content");
//		}
//		return "forward:/viewContentForSubject?subject="+contentFromForm.getSubject();
	//	return null;
		}

	private String createContentIdMasterkeyMappings(ContentDAO dao, ContentAcadsBean filesSet, String contentId) throws Exception{
		// TODO Auto-generated method stub
		
		if(filesSet.getProgramId().split(",").length>1 
				|| filesSet.getProgramStructureId().split(",").length>1
				|| filesSet.getConsumerTypeId().split(",").length>1 
			)
			{
				// If Any Option is Selected Is "All"
			ArrayList<ContentAcadsBean> consumerProgramStructureIds = dao.getconsumerProgramStructureIdsWithSubject(filesSet.getProgramId()
																										 ,filesSet.getProgramStructureId()
																										 ,filesSet.getConsumerTypeId()
																										 ,filesSet.getSubject());
			
			return dao.batchInsertContentIdConsumerProgramStructureIdMappingsForMultipleConsumerIds((long)Long.parseLong(contentId),consumerProgramStructureIds); 
			}
			else {
				
			ContentAcadsBean consumerProgramStructureId = dao.getConsumerProgramStructureIdByProgramProgramStructureConsumerTypeId(filesSet.getProgramId()
																									,filesSet.getProgramStructureId()
																									,filesSet.getConsumerTypeId()
																									,filesSet.getSubject());

			ArrayList<ContentAcadsBean> consumerProgramStructureIds = new ArrayList<>();
			consumerProgramStructureIds.add(consumerProgramStructureId);
			
			return dao.batchInsertContentIdConsumerProgramStructureIdMappingsForMultipleConsumerIds((long)Long.parseLong(contentId),consumerProgramStructureIds); 
			}
		
	
	}
	

	/*Single Updation of content */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public  HashMap<String,String> updateContentSingleSetup(ContentAcadsBean contentFromForm)
	{
		response  = new  HashMap<String,String>();

	
			
			/*Create new Content into content table */
			long contentId = contentDAO.saveContentFileDetails(contentFromForm, contentFromForm.getSubject(), contentFromForm.getYear(), contentFromForm.getMonth());
			
			/*Update the mapping table */

			int i =contentDAO.updateTheMappingTable(contentFromForm.getId(),contentId,contentFromForm.getConsumerProgramStructureId());
			
			
			/*Update into temp table  */		
			int j = contentDAO.updateSingleContentWithMappingInTemp(contentFromForm,contentId); 

			/* If  i == 0, means content is not present in temporary */
			if(j==0) {
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date date = new Date();
				contentFromForm.setId(String.valueOf(contentId));
				contentFromForm.setCreatedDate(formatter.format(date));
				contentDAO.insertIntoContentTempTable(contentFromForm);
			}
			

			response.put("success","true");
			response.put("successMessage", i+ " Content details updated successfully");


		
		//return searchContent(request,response, content);
		return response;
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public  HashMap<String,String> updateContentByDistinct(ContentAcadsBean contentFromForm,String masterKeys) throws Exception
	{
		response  = new  HashMap<String,String>();
			int updateCount = 0;
			//Find no. of mappings for that contentId
			int mappingCount = contentDAO.getNosMappingsByMasterKeys(contentFromForm.getId());
					
			//Find length of masterKeys applicable
			String[] masterKeyCount = masterKeys.split(",");
			
			if(mappingCount == masterKeyCount.length)  
			{
				
				//As the length is same , then update the whole content
				updateCount = contentDAO.updateContent(contentFromForm);
				response.put("successMessage", updateCount+ " Content details updated successfully");
				
				//Update the Whole Content in Temp table
				int i = contentDAO.updateWholeContentWithMappingInTemp(contentFromForm);
				
				/*It is not present in the temporary table */
				if(i == 0)
				{
					insertWholeContentIfNotPresentInTemp(contentFromForm);					
				}
					
				
			}
			else
			{
				/*Create new Content into content table */
				long contentId = contentDAO.saveContentFileDetails(contentFromForm, contentFromForm.getSubject(), contentFromForm.getYear(), contentFromForm.getMonth());
				
				/*Update the mapping table */

				updateCount = contentDAO.updateTheMappingTable(contentFromForm.getId(),contentId,masterKeys);
				

				/*Update into temp table  */		
				int i = contentDAO.updateSingleContentWithMappingInTemp(contentFromForm,contentId); 


				
				response.put("successMessage", updateCount+ " Mapping Content details created successfully");

				
				if(i==0) {
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date date = new Date();
					contentFromForm.setId(String.valueOf(contentId));
					contentFromForm.setCreatedDate(formatter.format(date));
					contentDAO.insertIntoContentTempTable(contentFromForm);
				}

			}
		response.put("success", "true");
		return response;


	}
	
	
	void insertWholeContentIfNotPresentInTemp(ContentAcadsBean contentFromForm)
	{
		
	
			/*Get the Mappings of that contentId */
			List<ContentAcadsBean> mappingList = contentDAO.getMappingOfContentId(contentFromForm.getId());

		
			/*insert into temp that content */
			contentDAO.insertionContentWithMappingInTemp(contentFromForm,Long.parseLong(contentFromForm.getId()),mappingList,contentFromForm.getYear(),contentFromForm.getMonth());
			


	}
	
	
}
