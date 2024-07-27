package com.nmims.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;

import com.nmims.beans.ConsumerProgramStructureAcads;
import com.nmims.beans.ContentAcadsBean;
import com.nmims.beans.ContentFilesSetbean;
import com.nmims.beans.PageAcads;
import com.nmims.beans.VideoContentAcadsBean;
import com.nmims.daos.ContentDAO;
import com.nmims.interfaces.ContentInterface;

@Service("contentLeadService")
public class ContentLeadService implements ContentInterface {

	@Override
	public ArrayList<String> getSubjects() {	
		// TODO Auto-generated method stub
		return null;
	}



	// mapping for leads 
		private String createContentIdMasterkeyMappingsForLeads(ContentDAO dao, ContentFilesSetbean filesSet, long contentId) {
			// TODO Auto-generated method stub
			
			if(filesSet.getProgramId().split(",").length>1 || filesSet.getProgramStructureId().split(",").length>1 || filesSet.getConsumerTypeId().split(",").length>1 )
				{
					// If Any Option is Selected Is "All"
				ArrayList<ContentAcadsBean> consumerProgramStructureIds = dao.getconsumerProgramStructureIdsWithSubject(filesSet.getProgramId()
																											 ,filesSet.getProgramStructureId()
																											 ,filesSet.getConsumerTypeId()
																											 ,filesSet.getSubject());
				
				return dao.batchInsertContentIdConsumerProgramStructureIdMappingsForMultipleConsumerIdsForLeads(contentId,consumerProgramStructureIds); 
				}
				else {
					
				ContentAcadsBean consumerProgramStructureId = dao.getConsumerProgramStructureIdByProgramProgramStructureConsumerTypeId(filesSet.getProgramId()
																										,filesSet.getProgramStructureId()
																										,filesSet.getConsumerTypeId()
																										,filesSet.getSubject());
				ArrayList<ContentAcadsBean> consumerProgramStructureIds = new ArrayList<>();
				consumerProgramStructureIds.add(consumerProgramStructureId);
				
				return dao.batchInsertContentIdConsumerProgramStructureIdMappingsForMultipleConsumerIdsForLeads(contentId,consumerProgramStructureIds); 
				}
			
		
		}

		@Override
		public HashMap<String, String> createContent(ContentFilesSetbean filesSet) {
			// TODO Auto-generated method stub
			return null;
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
						return null;
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
			return null;
		}


		@Override
		public List<ContentAcadsBean> addConsumerProgramProgramStructureNameToEachContentFile(
				List<ContentAcadsBean> contentList) {
			// TODO Auto-generated method stub
			return null;
		}


		@Override
		public ArrayList<ConsumerProgramStructureAcads> getFacultySubjectsCodes(String userId, String month, String year) {
			// TODO Auto-generated method stub
			return null;
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
			return null;
		}




		@Override
		public List<ContentAcadsBean> getContentsBySubjectCodeId(String subjectCodeId, String month, String year) {
			return null;
		}

		@Override
		public ArrayList<String> getLocationList() {
			// TODO Auto-generated method stub
			return null;
		}




		public List<String> getFacultyIdsByPssIds(String year, String month, String programSemSubjectId) {

			// TODO Auto-generated method stub
			return null;
		}



		@Override
		public List<VideoContentAcadsBean> getVideoContentForSubjectAndFaculty(ContentAcadsBean bean,
				String facultyId) {
			// TODO Auto-generated method stub
			return null;
		}

}
