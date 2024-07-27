package com.nmims.interfaces;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.nmims.beans.PageAcads;
import com.nmims.beans.ConsumerProgramStructureAcads;
import com.nmims.beans.ContentAcadsBean;
import com.nmims.beans.ContentFilesSetbean;
import com.nmims.beans.VideoContentAcadsBean;
import com.nmims.daos.ContentDAO;

public interface ContentInterface {
	
	public abstract HashMap<String,String> createContent(ContentFilesSetbean filesSet) throws Exception;
	
	public abstract ArrayList<String> readContent(String contentId, String consumerProgramStructureId);
	

	public abstract HashMap<String,String> updateContent(ContentAcadsBean contentFromForm) throws Exception;

	
	public abstract HashMap<String,String> deleteContent(String contentId) throws Exception ;
	
	public abstract HashMap<String,String> makeLiveContent(ContentAcadsBean searchBean);
	

	public abstract HashMap<String,String> transferContent(ContentAcadsBean searchBean) throws Exception;

	 
	public abstract ArrayList<String> getSubjects();	 
	
	public abstract ArrayList<ConsumerProgramStructureAcads> getSubjectCodeLists();
	
	public abstract ArrayList<ConsumerProgramStructureAcads> getMasterKeyMapSubjectCode();
	
	public abstract String getSubjectNameByPssId(String pssIds);
	
	public abstract String getSubjectNameBySubjectCodeId(String subjectCodeId);
	
	public abstract List<ContentAcadsBean> getContentsBySubjectCodeId(String subjectCodeId,String month,String year);
	
	public abstract List<VideoContentAcadsBean> getVideoContentForSubject(ContentAcadsBean bean);
	
	public abstract List<ContentAcadsBean> addConsumerProgramProgramStructureNameToEachContentFile(List<ContentAcadsBean> contentList);
		
	public abstract ArrayList<ConsumerProgramStructureAcads> getFacultySubjectsCodes(String userId,String month,String year);
	
	public abstract ContentAcadsBean findById(String contentId);
	

	public abstract HashMap<String,String> updateContentSingleSetup(ContentAcadsBean searchBean) throws Exception;

	
	public abstract HashMap<String,String> deleteContentSingleSetup(String contentId,String masterKey) throws Exception;
	
	

	public abstract ArrayList<String> getLocationList();

	
	public abstract ArrayList<ConsumerProgramStructureAcads> getProgramStructureByConsumerType(String consumerTypeId);
		
	public abstract ArrayList<ConsumerProgramStructureAcads> getProgramByConsumerType(String consumerTypeId);
		
	public abstract ArrayList<ConsumerProgramStructureAcads> getSubjectByConsumerType(String consumerTypeId,String programId,String programStructureId);
				
	public abstract ArrayList<ConsumerProgramStructureAcads> getProgramByConsumerTypeAndPrgmStructure(String consumerTypeId,String programStructureId);
	    	
	public abstract PageAcads<ContentAcadsBean> searchContent(int pageNo,ContentAcadsBean bean,String searchType);
		
	public abstract List<ContentAcadsBean> getRecordingForLastCycleBySubjectCode(String subjectCodeId,String month,String year);
		
	public abstract HashMap<String,String> updateContentByDistinct(ContentAcadsBean contentBean,String masterKeys) throws Exception;
		
	public abstract HashMap<String,String> deleteContentByDistinct(String contentId,String masterKeys) throws Exception;
		
	public abstract ArrayList<ContentAcadsBean> getCommonGroupProgramList(ContentAcadsBean bean);
		

	public abstract List<ContentAcadsBean> getProgramsListForCommonContent(String id);


	public List<String> getFacultyIdsByPssIds(String year,String month,String programSemSubjectId);
	
	public abstract List<VideoContentAcadsBean> getVideoContentForSubjectAndFaculty(ContentAcadsBean bean, String facultyId);


}       
