package com.nmims.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.ConsumerProgramStructureAcads;
import com.nmims.beans.ContentAcadsBean;
import com.nmims.beans.ContentFilesSetbean;
import com.nmims.beans.PageAcads;
import com.nmims.beans.VideoContentAcadsBean;
import com.nmims.daos.ContentDAO;
import com.nmims.daos.TimeTableDAO;
import com.nmims.daos.VideoContentDAO;
import com.nmims.beans.SearchTimeBoundContent;
import com.nmims.interfaces.ContentInterface;
import com.nmims.strategies.impl.UploadWIthSessionPlan;
import com.nmims.util.ContentUtil;

@Service("contentMBAWXService")
public class ContentMBAWXService implements ContentInterface {
	
	@Autowired
	ContentDAO contentdao;
	
	@Autowired
	TimeTableDAO timeTableDAO;
	
	@Autowired
	VideoContentDAO vdao;
	
	private static final Logger logger = LoggerFactory.getLogger("contentService");
	
	
	private static final String studentType = "TimeBound";
	

	@Autowired
	UploadWIthSessionPlan uploadWithSessionPlan;
	
	@Override
	public ArrayList<String> getSubjects() {
		// TODO Auto-generated method stub
		return null;
	}

	

	@Override
	public HashMap<String, String> createContent(ContentFilesSetbean filesSet) {
		// TODO Auto-generated method stub
		return uploadWithSessionPlan.createContent(filesSet);
	}

	@Override
	public ArrayList<String> readContent(String contentId, String consumerProgramStructureId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<String, String> updateContent(ContentAcadsBean contentFromForm) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<String, String> makeLiveContent(ContentAcadsBean searchBean) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<String, String> transferContent(ContentAcadsBean searchBean) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	public  ArrayList<ConsumerProgramStructureAcads> getSubjectCodeLists()
	{
		// TODO Auto-generated method stub
		ArrayList<ConsumerProgramStructureAcads> subjectCode = new ArrayList<ConsumerProgramStructureAcads>();
		try {
			
			subjectCode = contentdao.getTimeBoundSubjectCodeLists();
			
		}catch(Exception e){
			
			logger.info(" Content MBA-WX Service "); 
			logger.info("Error in Getting Subject code lists ");
			logger.error("Method Name :- getSubjectCodeLists ",e);
			
		}
		return subjectCode;
	}
	
	public  ArrayList<ConsumerProgramStructureAcads> getMasterKeyMapSubjectCode()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	public  String getSubjectNameByPssId(String pssIds)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	public  String getSubjectNameBySubjectCodeId(String subjectCodeId)
	{
		// TODO Auto-generated method stub
		return null;
	}
	

	@Override
	public HashMap<String, String> deleteContent(String contentId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<VideoContentAcadsBean> getVideoContentForSubject(ContentAcadsBean bean) {
		// TODO Auto-generated method stub
		return vdao.getVideoContentForSubject(bean);
	}

	@Override
	public List<ContentAcadsBean> addConsumerProgramProgramStructureNameToEachContentFile(List<ContentAcadsBean> contentList) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<ConsumerProgramStructureAcads> getFacultySubjectsCodes(String userId, String month, String year) {
		// TODO Auto-generated method stub

		//return contentdao.getFacultySubjectsCodes(userId,getTwoContentLiveOrder());
		ArrayList<ConsumerProgramStructureAcads> allsubjects = new ArrayList<ConsumerProgramStructureAcads>();
	
		
		 allsubjects = contentdao.getFacultySubjectsCodesBySession(userId,month,year,"Y");
			allsubjects.addAll(contentdao.getFacultySubjectsCodesFromCMapping(userId,month,year,studentType));
			
			//Remove duplicates subjects from the list.
	        Map<Integer, ConsumerProgramStructureAcads> map = new LinkedHashMap<>();
	        for (ConsumerProgramStructureAcads ays : allsubjects) {
	          map.put(Integer.valueOf(ays.getSubjectCodeId()), ays);
	        }
	        allsubjects.clear();
	        allsubjects.addAll(map.values());
		 
     	return allsubjects;
	}

	@Override
	public ContentAcadsBean findById(String contentId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<String, String> updateContentSingleSetup(ContentAcadsBean searchBean) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<String, String> deleteContentSingleSetup(String contentId, String masterKey) {
		// TODO Auto-generated method stub
		return null;
	}




	@Override
	public ArrayList<ConsumerProgramStructureAcads> getProgramStructureByConsumerType(String consumerTypeId) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public ArrayList<ConsumerProgramStructureAcads> getProgramByConsumerType(String consumerTypeId) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public ArrayList<ConsumerProgramStructureAcads> getSubjectByConsumerType(String consumerTypeId, String programId,
			String programStructureId) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public ArrayList<ConsumerProgramStructureAcads> getProgramByConsumerTypeAndPrgmStructure(String consumerTypeId,
			String programStructureId) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public PageAcads<ContentAcadsBean> searchContent(int pageNo, ContentAcadsBean bean, String searchType) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public List<ContentAcadsBean> getRecordingForLastCycleBySubjectCode(String subjectCodeId,String month,String year) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public HashMap<String, String> updateContentByDistinct(ContentAcadsBean contentBean, String masterKeys) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public HashMap<String, String> deleteContentByDistinct(String contentId, String masterKeys) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public List<ContentAcadsBean> getProgramsListForCommonContent(String id) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public ArrayList<ContentAcadsBean> getCommonGroupProgramList(ContentAcadsBean bean) {
		// TODO Auto-generated method stub
		return null;
	}




	@Override
	public List<ContentAcadsBean> getContentsBySubjectCodeId(String subjectCodeId, String month, String year) {
		// TODO Auto-generated method stub

		// changing month for Oct & Apr cycle
		if(month.equals("Oct"))
			month = "Jul";
		
		if(month.equals("Apr"))
			month = "Jan";  
		 
		return contentdao.getWXContentsBySubjectCodeId(subjectCodeId,month,year);
		

	}


	/**
	 *  Get the scheduledContentList  Details 
	 * @param year
	 * @param month
	 * @param batchId
	 * @param programSemSubjectId
	 * @param facultyId
	 * @param date
	 * @return List<SearchTimeBoundContent> 
	 */
	
	public List<SearchTimeBoundContent> scheduledContentList(String year,String month,String batchId,String programSemSubjectId,String facultyId,String date)
	{
		List<SearchTimeBoundContent> contentList= new ArrayList<SearchTimeBoundContent>();
		try {
			//Check whether Month and year belong to current table or history table
			if(ContentUtil.findValidHistoryDate(year+month) >= 0)
			{
				contentList = contentdao.getTimeBoundContentPageCurrent(year,month,batchId,programSemSubjectId,facultyId,date);
				
				
			}else
			{
				contentList = contentdao.getTimeBoundContentPageHistory(year,month,batchId,programSemSubjectId,facultyId,date);
			}
			
		
			
		}catch (Exception e)
		{
			logger.info(" Content MBA-WX Service "); 
			logger.info("Error in Finding Scheduled Content List For Year: "+year+" Month: "+month+"	BatchId: "+batchId+"	ProgramSemSubjectId: "+programSemSubjectId+", FacultyId: "+facultyId+", Date: "+date);
			logger.error("Method Name :- scheduledContentList ",e);
		}
		return contentList;
	}
	
	
	public ArrayList<String> getLocationList() {
		
		ArrayList<String> locations = new ArrayList<String>();
		try {
			
			locations = timeTableDAO.getAllLocations();
		}
		catch (Exception e)
		{
			logger.info(" Content MBA-WX Service "); 
			logger.info("Error in getting  Locations  List ");
			logger.error("Method Name :- getLocationList ",e);
		}
		return locations;
	}
	
	/**
	 *  Get the Batch Details 
	 * @param year
	 * @param month
	 * @param programSemSubjectId
	 * @return List<SearchTimeBoundContent> 
	 */
	public List<SearchTimeBoundContent> getbatchDetails(String year,String month,String programSemSubjectId)
	{
		List<SearchTimeBoundContent> batchList= new ArrayList<SearchTimeBoundContent>();
		try {
			
			batchList = contentdao.getbatchDetails(year,month,programSemSubjectId);
			
		}catch (Exception e)
		{
			logger.info(" Content MBA-WX Service "); 
			logger.info("Error in getting  Batch Details  List Year: "+year+", Month: "+month+", ProgramSemSubjectId: "+programSemSubjectId);
			logger.error("Method Name :- getbatchDetails ",e);
		}
		return batchList;
	}
	
	/**
	 *  Get the Faculty Ids  Details 
	 * @param year
	 * @param month
	 * @param programSemSubjectId
	 * @return List<String> 
	 */
	public List<String> getFacultyIdsByPssIds(String year,String month,String programSemSubjectId)
	{
		List<String> facultyIds= new ArrayList<String>();
		try {
			
			//Check whether Month and year belong to current table or history table
			if(ContentUtil.findValidHistoryDate(year+month) >= 0)
			{
				facultyIds = contentdao.getFacultyIdsByPssIdsCurrent(year,month,programSemSubjectId);
				
				
			}else
			{
				facultyIds = contentdao.getFacultyIdsByPssIdsHistory(year,month,programSemSubjectId);
			}
			
			
		}catch (Exception e)
		{
			logger.info(" Content MBA-WX Service "); 
			logger.info("Error in getting  Faculty  List By PssId For year "+year +" month: "+month+" programSemSubjectId "+programSemSubjectId);
			logger.error("Method Name :- getFacultyIdsByPssIds ",e);
		}
		return facultyIds;

	}



	@Override
	public List<VideoContentAcadsBean> getVideoContentForSubjectAndFaculty(ContentAcadsBean bean, String facultyId) {
		// TODO Auto-generated method stub
		return null;
	}

}
