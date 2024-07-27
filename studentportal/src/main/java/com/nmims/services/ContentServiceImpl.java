package com.nmims.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.nmims.beans.ContentStudentPortalBean;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.daos.ContentDAO;
import com.nmims.daos.PortalDao;

import com.nmims.interfaces.ContentServiceInterFace;
import com.nmims.util.ContentUtil;

@Service("contentService")
public class ContentServiceImpl implements ContentServiceInterFace
{
		@Value("${CURRENT_ACAD_MONTH}")
		private  String CURRENT_ACAD_MONTH;

		@Value("${CURRENT_ACAD_YEAR}")
		private  String CURRENT_ACAD_YEAR;
		
		
		@Autowired
		ContentDAO contentDAO;
		
		@Autowired
		ApplicationContext act;
		
		public List<ContentStudentPortalBean> getContentByPssId(double reg_order,double acadContentLiveOrder,String reg_month,String reg_year,String programSemSubjectId,String sapid
				,boolean isCurrent)
		{
			 PortalDao pDao = (PortalDao) act.getBean("portalDAO");

			List<ContentStudentPortalBean> allLastCycleContentListForSubject = null;
		     
			try {
		    
		    //Get Correct Date Format for content
		    String acadDateFormat = ContentUtil.getCorrectOrderAccordTo2AcadContentLive(acadContentLiveOrder,reg_order,reg_month,reg_year,CURRENT_ACAD_MONTH,CURRENT_ACAD_YEAR,isCurrent);
		    
		    
		    allLastCycleContentListForSubject = pDao.getContentsForLR(programSemSubjectId,acadDateFormat);
		   
		 
	    	
		    if(allLastCycleContentListForSubject.size() > 0) {
		    	
		    	allLastCycleContentListForSubject = fetchAndInsertBookmarksInContent(allLastCycleContentListForSubject,sapid);
		    	
		    }
		    
			}catch(Exception e)
			{
				
			}
			
			return allLastCycleContentListForSubject;
		}
		
		
		
		  //Fetch The Bookmark For Content
	    public List<ContentStudentPortalBean> fetchAndInsertBookmarksInContent(List<ContentStudentPortalBean> ContentsList, String sapid){
			String commaSaperatedContentIds = null;
			commaSaperatedContentIds =	ContentsList.stream()
					.map(a -> String.valueOf(a.getId()))
					.collect(Collectors.joining(","));
			
			//get bookmark status of all Content
			List<ContentStudentPortalBean> contentBookmarkIds = contentDAO.getBookmarksOfContent(commaSaperatedContentIds,sapid);
			
			//set bookmarks status in original ContentList
			ContentsList.forEach(myObject1 -> contentBookmarkIds.stream()
		            .filter(myObject2 -> myObject1.getId().equals(myObject2.getId()))
		            .findAny().ifPresent(myObject2 -> myObject1.setBookmarked(myObject2.getBookmarked())));
			
			return ContentsList;
		}
	    
	    public List<ContentStudentPortalBean> getLeadsContent(StudentStudentPortalBean bean){
	    	
	    	PortalDao pDao = (PortalDao) act.getBean("portalDAO");
			List<ContentStudentPortalBean> allLastCycleContentListForSubject = null;
			try {
				allLastCycleContentListForSubject = pDao.getContentsForLeads(bean);
		   }catch(Exception e){
				
		   }
			
			return allLastCycleContentListForSubject;
		}
		
		
	    
	      
}
