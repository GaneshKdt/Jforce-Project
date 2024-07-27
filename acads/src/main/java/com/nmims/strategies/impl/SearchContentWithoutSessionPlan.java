package com.nmims.strategies.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;
import java.util.*;

import com.nmims.daos.ContentDAO;
import com.nmims.daos.TimeTableDAO;
import com.nmims.util.ContentUtil;
import com.nmims.beans.*;

@Service("searchContentWithoutSessionPlan")
public class SearchContentWithoutSessionPlan
{
	@Value("${CONTENT_PATH}")
	private String CONTENT_PATH;

	@Autowired
	ContentDAO contentDAO;

	@Autowired
	TimeTableDAO tDao;
	
	private final int pageSize = 10;
	
	
	public  PageAcads<ContentAcadsBean> searchContent(int pageNo,ContentAcadsBean searchBean,String searchType)
	{
		
		if(searchBean.getConsumerProgramStructureId() == null) {
			List<String> consumerProgramStructureIds =new ArrayList<String>();
			if(!StringUtils.isBlank(searchBean.getProgramId()) && !StringUtils.isBlank(searchBean.getProgramStructureId()) && !StringUtils.isBlank(searchBean.getConsumerTypeId())){
				consumerProgramStructureIds = tDao.getconsumerProgramStructureIds(searchBean.getProgramId(),searchBean.getProgramStructureId(),searchBean.getConsumerTypeId());
			}
			
			String consumerProgramStructureIdsSaperatedByComma = "";
			if(!consumerProgramStructureIds.isEmpty()){
				for(int i=0;i < consumerProgramStructureIds.size();i++){
					consumerProgramStructureIdsSaperatedByComma = consumerProgramStructureIdsSaperatedByComma + ""+ consumerProgramStructureIds.get(i) +",";
				}
				consumerProgramStructureIdsSaperatedByComma = consumerProgramStructureIdsSaperatedByComma.substring(0,consumerProgramStructureIdsSaperatedByComma.length()-1);
			}
			searchBean.setConsumerProgramStructureId(consumerProgramStructureIdsSaperatedByComma);
		}

		PageAcads<ContentAcadsBean> page= new PageAcads<ContentAcadsBean>();
		if(ContentUtil.findValidHistoryDate(searchBean.getYear()+searchBean.getMonth()) >= 0 )
			page = contentDAO.getResourcesContentBySubjectCodeOrPssIdCurrent(pageNo, pageSize, searchBean, searchType);
		else
			page = contentDAO.getResourcesContentBySubjectCodeOrPssIdHistory(pageNo, pageSize, searchBean, searchType);

		
		return page;
	}

}
