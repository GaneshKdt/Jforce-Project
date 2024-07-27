package com.nmims.strategies.impl;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.nmims.beans.ContentAcadsBean;
import com.nmims.daos.ContentDAO;
import com.nmims.strategies.MakeLiveStratergy;

@Service("makeLiveStratergy")
public class MakeLiveWithoutSessionPlan implements MakeLiveStratergy {
	
	@Value( "${CONTENT_PATH}" )
	private String CONTENT_PATH;
	
	@Autowired
	ContentDAO contentDAO;
	
	HashMap<String, String> response;


	@Override
	public HashMap<String, String> makeLiveContent(ContentAcadsBean searchBean) {
		
		List<String> consumerProgramStructureIds = contentDAO.getconsumerProgramStructureIds(searchBean.getProgramId(),
																						   searchBean.getProgramStructureId(),
																						   searchBean.getConsumerTypeId());
				

		String errorMessage = contentDAO.batchInsertOfMakeContentLiveConfigs(searchBean,consumerProgramStructureIds);
				
		if(StringUtils.isBlank(errorMessage)) {
//			response.put("success","true");
//			response.put("successMessage", " Content Is Live Successfully. ");
//			m.addAttribute("searchBean", searchBean);	
//			return makeContentLiveForm(request, response, m);
			return response;

		}else {
//			request.setAttribute("error", "true");
//			request.setAttribute("errorMessage", errorMessage);
//			m.addAttribute("searchBean", searchBean);	
//			return makeContentLiveForm(request, response, m);
			return response;
		}

	}

}
