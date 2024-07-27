package com.nmims.strategies.impl;

import java.util.HashMap;

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
import com.nmims.strategies.DeleteStratergy;

@Service("deleteWithoutSessionPlan")
public class DeleteWithoutSessionPlan implements DeleteStratergy {

	@Value("${CONTENT_PATH}")
	private String CONTENT_PATH;

	@Autowired
	ContentDAO contentDAO;

	private static final Logger logger =Logger.getLogger("contentService");
	
	HashMap<String, String> response;

	/* Single Program Content deletion */
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public HashMap<String, String> deleteContentSingleSetup(String contentId, String consumerProgramStructureId) throws Exception{
		// TODO Auto-generated method stub
		response = new HashMap<String, String>();


		//ContentBean content = contentDAO.findById(contentId);
		//response.put("subject", content.getSubject());
		int countOfProgramsContentApplicableTo = contentDAO.getCountOfProgramsContentApplicableToById(contentId);
		
		if (countOfProgramsContentApplicableTo > 0) {
			int deletedRows = contentDAO.deleteContentIdAndMasterKeyMappingByIdAndMasterkey(contentId,
					consumerProgramStructureId);
			
			//Delete content and it's specific mappings in temp table
			contentDAO.deleteSingleContentIdInTemp(contentId,consumerProgramStructureId);
			
			if (deletedRows > 0) {
				if (countOfProgramsContentApplicableTo == 1) {
					//if no mapping for that content , delete that content.
					int deletedContentRows = contentDAO.deleteContentById((long) Long.parseLong(contentId));

					if (deletedContentRows < 1) {
						response.put("error", "true");
						response.put("errorMessage", "Error in deleting Content Record.");
						// return "forward:/viewContentForSubject?subject="+content.getSubject();
					}
				}
			} else {
				response.put("error", "true");
				response.put("errorMessage"," Error in deleting Mapping Record.");
				// return "forward:/viewContentForSubject?subject="+content.getSubject();
			}
			response.put("success", "true");
			response.put("successMessage",deletedRows + " Record deleted successfully");
		} else {
			response.put("error", "true");
			response.put("errorMessage", "Error in getting countOfProgramsContentApplicableTo.");
			// return "forward:/viewContentForSubject?subject="+content.getSubject();
		}

		
		return response;
	}
	

	/* Whole deletion of content table and its mapping */
	@Transactional(value="transactionManager",readOnly = false)
	public  HashMap<String,String> deleteContent(String contentId) throws Exception
	{
		response = new HashMap<String, String>();
		
			
			//Delete the Whole content and it's mappings

			 contentDAO.deleteContentIdMasterkeyMappingsById(contentId);

			 int  i = contentDAO.deleteContentById((long) Long.parseLong(contentId));

			//Delete Whole Content in temp table
			contentDAO.deleteWholeContentIdInTemp(contentId);
			

		response.put("success", "true");
		response.put("successMessage", i+" Record Deleted Successfully.");
		return response;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public HashMap<String, String> deleteContentByDistinct(String contentId, String consumerProgramStructureId) throws Exception{
		response = new HashMap<String, String>();
			int delete_count = 0;
		
			//Find no. of mappings for that contentId
			int mappingCount = contentDAO.getNosMappingsByMasterKeys(contentId);
			 
			//Find length of masterKeys applicable
			String[] masterKeyCount = consumerProgramStructureId.split(",");
				
			//Compare actual mapping count and applicable masterKey count 
			if(mappingCount == masterKeyCount.length) 
			{
				//If true , then delete  the content and its mappings 
				delete_count =  contentDAO.deleteContentById((long) Long.parseLong(contentId));
			

				contentDAO.deleteContentIdMasterkeyMappingsById(contentId);
				
				//Delete Whole Content in temp table
				contentDAO.deleteWholeContentIdInTemp(contentId);
			

			}
			else {
				
				//If not equal, delete the mapping for that particular id
				delete_count =  contentDAO.deleteContentIdAndMasterKeyMappingByIdAndMasterkey(contentId,
						consumerProgramStructureId);
				

				//Delete content and it's specific mappings in temp table
				contentDAO.deleteSingleContentIdInTemp(contentId,consumerProgramStructureId);
			

			}

			
			response.put("success", "true");
			response.put("successMessage", delete_count+" Record Deleted Successfully.");
			return response;
	}
}
