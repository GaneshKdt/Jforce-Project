package com.nmims.strategies.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.ContentAcadsBean;
import com.nmims.controllers.ContentController;
import com.nmims.daos.ContentDAO;
import com.nmims.strategies.TransferContent;

@Service("transferContentWithoutSessionPlan")
public class TransferContentWithoutSessionPlan implements TransferContent{
	
	@Value("${CONTENT_PATH}")
	private String CONTENT_PATH;

	@Autowired
	ContentDAO contentDAO;

	HashMap<String, String> response;

	private static final Logger logger =Logger.getLogger("contentService");

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public HashMap<String, String> transferContent(ContentAcadsBean searchBean) throws Exception{

		// TODO Auto-generated method stub	
		
		int success_count = 0;
		int error_count = 0;
		ArrayList<String> error_files = new ArrayList<String>();
		//ModelAndView modelnView = new ModelAndView("transferContent");
		response  = new  HashMap<String,String>();
		//modelnView.addObject("searchBean", searchBean);


			
			if(searchBean.getContentToTransfer() == null || searchBean.getContentToTransfer().size() == 0){
				response.put("error", "true");
				response.put("errorMessage", "Please select at least one content to transfer.");
				return response;
			}
			

			
			/* Save the content details in the content table */


			List<ContentAcadsBean> contentToTransferList = contentDAO.getContentsForIds(searchBean.getContentToTransfer());
				
		
			
			/* Save the content details in the content table */

		//Get the Mapping List from subjectcode mapping table instead of contentMapping table.
		List<ContentAcadsBean> mappingList = contentDAO.getMasterKeyForContentMapping(searchBean.getSubjectCodeId(),"");
		
		for (ContentAcadsBean contentBean : contentToTransferList) {
			
			//List<ContentBean> mappingList = contentDAO.getMappingOfContentId(contentBean.getId());


			long id = contentDAO.saveContentDetails(contentBean, searchBean.getCreatedBy(),searchBean.getLastModifiedBy(), searchBean.getToYear(), searchBean.getToMonth());

			/* Populate the contentId - MasterKey table with the contentId */

			contentDAO.batchInsertContentIdConsumerProgramStructureIdMappingsForMultipleConsumerIds(id, mappingList);


			contentDAO.insertionContentWithMappingInTemp(contentBean, id, mappingList, searchBean.getToYear(), searchBean.getToMonth());



			
			success_count++;
			

		   }
		response.put("success","true");
		response.put("successMessage",success_count+" Transfered content successfully.");
		return response;

		
		}
		/*List<ContentBean> contentList = contentDAO.getContentsBySubjectCodeId(searchBean);
		
		response.put("contentList","contentList");
		int rowCount = (contentList == null ? 0 : contentList.size());
		response.put("rowCount", "rowCount");*/
		
		
		

}
