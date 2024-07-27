package com.nmims.services;

import java.math.BigDecimal;
import java.util.*;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;



import org.apache.commons.lang.RandomStringUtils;

import com.nmims.beans.PageAcads;
import com.nmims.beans.ConsumerProgramStructureAcads;
import com.nmims.beans.ContentAcadsBean;

import com.nmims.beans.ContentFilesSetbean;

import com.nmims.beans.ExamOrderAcadsBean;
import com.nmims.beans.ProgramSubjectMappingAcadsBean;
import com.nmims.beans.StudentAcadsBean;

import com.nmims.controllers.ContentController;

import com.nmims.beans.VideoContentAcadsBean;

import com.nmims.daos.ContentDAO;
import com.nmims.daos.VideoContentDAO;
import com.nmims.interfaces.ContentInterface;
import com.nmims.strategies.impl.DeleteWithoutSessionPlan;
import com.nmims.strategies.impl.MakeLiveWithoutSessionPlan;
import com.nmims.strategies.impl.SearchContentWithoutSessionPlan;
import com.nmims.strategies.impl.TransferContentWithoutSessionPlan;
import com.nmims.strategies.impl.UpdateContentWithoutSessionPlan;
import com.nmims.strategies.impl.UploadWithoutSessionPlan;
import com.nmims.util.ContentUtil;
import com.nmims.util.ExamOrderUtil;

@Component
public class ContentService implements ContentInterface {
	
	HashMap<String,String> response;
	
	@Autowired
	ContentDAO contentdao;
	
	@Autowired
	UploadWithoutSessionPlan uploadWithoutSessionPlan;
	
	@Autowired
	DeleteWithoutSessionPlan deleteWithoutSessionPlan;
	
	@Autowired
	UpdateContentWithoutSessionPlan updateContentWithoutSessionPlan;
	
	@Autowired
	MakeLiveWithoutSessionPlan makeLiveStratergy;
	
	@Autowired
	TransferContentWithoutSessionPlan  transferContentWithoutSessionPlan;
	
	@Autowired
	VideoContentDAO vdao;
	

	@Autowired
	SearchContentWithoutSessionPlan searchContentWithoutSessionPlan;
	private ArrayList<ProgramSubjectMappingAcadsBean> programSubjectMappingList = null;
	
	@Value("${CURRENT_ACAD_MONTH}")
	private String CURRENT_ACAD_MONTH;

	@Value("${CURRENT_ACAD_YEAR}")
	private String CURRENT_ACAD_YEAR;
	
	private static final Logger logger = Logger.getLogger(ContentController.class);
	
	private static final String studentType = "Regular";
	
	@Override
	public HashMap<String,String> createContent(ContentFilesSetbean filesSet) throws Exception 
	{
		return uploadWithoutSessionPlan.createContent(filesSet); 
	}

	@Override
	public HashMap<String,String> updateContent(ContentAcadsBean contentFromForm) throws Exception {

		return updateContentWithoutSessionPlan.updateContent(contentFromForm);
	}
	
	@Override
	public HashMap<String,String> deleteContent(String contentId) throws Exception{
		// TODO Auto-generated method stub
		return deleteWithoutSessionPlan.deleteContent(contentId);
	}

	@Override
	public HashMap<String, String> makeLiveContent(ContentAcadsBean searchBean) {
		// TODO Auto-generated method stub
		return makeLiveStratergy.makeLiveContent(searchBean);
	}
	
	@Override
	public ArrayList<String> getSubjects() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<String> readContent(String contentId, String consumerProgramStructureId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<String, String> transferContent(ContentAcadsBean searchBean) throws Exception{

		// TODO Auto-generated method stub
		return transferContentWithoutSessionPlan.transferContent(searchBean);
	}
	
	public ArrayList<ConsumerProgramStructureAcads> getSubjectCodeLists()
	{
		return contentdao.getSubjectCodeLists();
	}
	
	public ArrayList<ConsumerProgramStructureAcads> getMasterKeyMapSubjectCode()
	{
		return contentdao.getMasterKeyMapSubjectCode();
	}
	
	public String getSubjectNameByPssId(String pssIds)
	{
		return contentdao.getSubjectNameByPssId(pssIds);
	}
	
	public String getSubjectNameBySubjectCodeId(String subjectCode)
	{
		return contentdao.getSubjectNameBySubjectCodeId(subjectCode);
	}
	
	public  List<ContentAcadsBean> getContentsBySubjectCodeId(String subjectCodeId, String month, String year)
	{
		return contentdao.getContentsBySubjectCodeId(subjectCodeId,month,year);
	}
	
	public List<VideoContentAcadsBean> getVideoContentForSubject(ContentAcadsBean bean)
	{
		return vdao.getVideoContentForSubject(bean);
	}
	
	public List<ContentAcadsBean> addConsumerProgramProgramStructureNameToEachContentFile(List<ContentAcadsBean> contentList)
	{
		try {
			/*int size = 0, i = 1;
			String contentIds = "";
			
			if(contentList != null ) {
				size = contentList.size();
			}
			
			for(ContentBean c : contentList) {
				if(i == size) {
					contentIds += c.getId()+"";
				}else {
					contentIds += c.getId()+",";
				}
				i++;
			}*/
			List<Integer> contentIds = new ArrayList<Integer>();
			for(ContentAcadsBean c : contentList) {
				contentIds.add(Integer.parseInt(c.getId()));
			}
			
			
			Map<String,Integer> contentIdNCountOfProgramsApplicableToMap =  contentdao.getContentIdNCountOfProgramsApplicableToMap(contentIds);
			
			for(ContentAcadsBean t : contentList) {
				
					/*t.setConsumerType(getConsumerTypeIdNameMap().get(t.getConsumerTypeIdFormValue()));
					
					if(t.getProgramStructureIdFormValue().split(",").length>1) {
						t.setProgramStructure("All");;
					}else {
						t.setProgramStructure(getProgramStructureIdNameMap().get(t.getProgramStructureIdFormValue()));;
					}
					
					if(t.getProgramIdFormValue().split(",").length>1) {
						t.setProgram("All");;
					}else {
						t.setProgram(getProgramIdNameMap().get(t.getProgramIdFormValue()));;
					}*/
					
					t.setCountOfProgramsApplicableTo(contentIdNCountOfProgramsApplicableToMap.get(t.getId()));
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			  
		}
		return contentList;
		
	}
	
	public  ArrayList<ConsumerProgramStructureAcads> getFacultySubjectsCodes(String userId,String month,String year)
	{
		
		//return contentdao.getFacultySubjectsCodes(userId,getTwoContentLiveOrder());
		ArrayList<ConsumerProgramStructureAcads> allsubjects = new ArrayList<ConsumerProgramStructureAcads>();
	
		
		 allsubjects = contentdao.getFacultySubjectsCodesBySession(userId,month,year,"N");
		//if(allsubjects.size() == 0)
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
	
	public  ContentAcadsBean findById(String contentId)
	{
		return contentdao.findById(contentId);
	}
	
	@Override
	public HashMap<String,String> updateContentSingleSetup(ContentAcadsBean contentFromForm) throws Exception{
		return updateContentWithoutSessionPlan.updateContentSingleSetup(contentFromForm);
	} 
	
	public  HashMap<String,String> deleteContentSingleSetup(String contentId ,String consumerProgramStructureId) throws Exception
	{ 
		return deleteWithoutSessionPlan.deleteContentSingleSetup(contentId, consumerProgramStructureId);
	}
	

	public ArrayList<ConsumerProgramStructureAcads> getProgramStructureByConsumerType(String consumerTypeId)
	{
		return contentdao.getProgramStructureByConsumerType(consumerTypeId);
	}
	
	public  ArrayList<ConsumerProgramStructureAcads> getProgramByConsumerType(String consumerTypeId)
	{
		return contentdao.getProgramByConsumerType(consumerTypeId);
	}
	
	public  ArrayList<ConsumerProgramStructureAcads> getSubjectByConsumerType(String consumerTypeId,String programId,String programStructureId)
	{
		return contentdao.getSubjectByConsumerType(consumerTypeId,programId,programStructureId);
		
	}
	
    public ArrayList<ConsumerProgramStructureAcads> getProgramByConsumerTypeAndPrgmStructure(String consumerTypeId,String programStructureId){
        return contentdao.getProgramByConsumerTypeAndPrgmStructure(consumerTypeId,programStructureId);
    }
    
    public PageAcads<ContentAcadsBean> searchContent(int pageNo,ContentAcadsBean searchBean,String searchType)
    {
    	return searchContentWithoutSessionPlan.searchContent(pageNo,searchBean, searchType);
    }

	@Override
	public List<ContentAcadsBean> getRecordingForLastCycleBySubjectCode(String subjectCodeId,String month,String year) {

		// TODO Auto-generated method stub
		
		if(ContentUtil.findValidHistoryDate(year+month) >= 0)
			return contentdao.getRecordingForLastCycleBySubjectCode(subjectCodeId,month,year);
		else
			return contentdao.getRecordingForLastCycleBySubjectCodeInHistoryTable(subjectCodeId,month,year);
	}

	@Override
	public HashMap<String,String> updateContentByDistinct(ContentAcadsBean contentBean, String masterKeys) throws Exception
	{

		// TODO Auto-generated method stub
		return updateContentWithoutSessionPlan.updateContentByDistinct(contentBean, masterKeys);
	}
	
	@Override
	public HashMap<String,String> deleteContentByDistinct(String contentId, String masterKeys) throws Exception{
		// TODO Auto-generated method stub
		return deleteWithoutSessionPlan.deleteContentByDistinct(contentId, masterKeys);
	}

	
	
	public  List<ContentAcadsBean> getProgramsListForCommonContent(String id)
	{
		return contentdao.getProgramsListForCommonContent(id);
	}

	@Override
	public ArrayList<ContentAcadsBean> getCommonGroupProgramList(ContentAcadsBean bean) {
		// TODO Auto-generated method stub
		return contentdao.getCommonGroupProgramList(bean);
	}

	public String checkEarlyAccess(String userId) {
		String earlyAccess = "No";
		List<ExamOrderAcadsBean> liveFlagList = contentdao.getLiveFlagDetails();
		HashMap<String,BigDecimal> examOrderMap = generateExamOrderMap(liveFlagList);
		StudentAcadsBean student = contentdao.getSingleStudentsData(userId);
		double examOrderDifference = 0.0;
		double examOrderOfProspectiveBatch = examOrderMap.get(student.getEnrollmentMonth()+student.getEnrollmentYear()).doubleValue();
		double maxOrderWhereContentLive = getMaxOrderWhereContentLive(liveFlagList);
		examOrderDifference = examOrderOfProspectiveBatch - maxOrderWhereContentLive;

		if(examOrderDifference == 1){
			earlyAccess= "Yes";
		}
		return earlyAccess;
	}
	
	private HashMap<String, BigDecimal> generateExamOrderMap(List<ExamOrderAcadsBean> liveFlagList) {
		HashMap<String, BigDecimal> orderMap = new HashMap<String, BigDecimal>();
		for (ExamOrderAcadsBean row : liveFlagList) {
			orderMap.put(row.getMonth()+row.getYear(),BigDecimal.valueOf((long)Double.parseDouble( row.getOrder())));
			orderMap.put(row.getAcadMonth()+row.getYear(),BigDecimal.valueOf((long)Double.parseDouble( row.getOrder())));
		}
		return orderMap;
	}
	private double getMaxOrderWhereContentLive(List<ExamOrderAcadsBean> liveFlagList){
		double contentLiveOrder = 0.0;
		for (ExamOrderAcadsBean bean : liveFlagList) {
			double currentOrder = Double.parseDouble(bean.getOrder());
			if("Y".equalsIgnoreCase(bean.getAcadContentLive()) && currentOrder > contentLiveOrder){
				contentLiveOrder = currentOrder;
			}
		}
		return contentLiveOrder;
	}
	
	public ArrayList<ProgramSubjectMappingAcadsBean> getFailSubjects(StudentAcadsBean student) {
		ArrayList<ProgramSubjectMappingAcadsBean> failSubjectList;
		try {
			failSubjectList = contentdao.getFailSubjectsForAStudent(student.getSapid());
		} catch (Exception e) {
			failSubjectList=new ArrayList<ProgramSubjectMappingAcadsBean>();
			  
		}
		return failSubjectList;
	}
	
	public ArrayList<ProgramSubjectMappingAcadsBean> getSubjectsForStudent(StudentAcadsBean student) {
		ArrayList<ProgramSubjectMappingAcadsBean> programSubjectMappingList = getProgramSubjectMappingList();
		ArrayList<ProgramSubjectMappingAcadsBean> subjects = new ArrayList<>();
		for (int i = 0; i < programSubjectMappingList.size(); i++) {
			ProgramSubjectMappingAcadsBean bean = programSubjectMappingList.get(i);

			if(
					bean.getPrgmStructApplicable().equals(student.getPrgmStructApplicable()) 
					&& bean.getProgram().equals(student.getProgram())
					&& bean.getSem().equals(student.getSem())
					&& !student.getWaivedOffSubjects().contains(bean.getSubject())//Subjects has not already cleared it
					){
				subjects.add(bean);

			}
		}
		return subjects;
	}
	public ArrayList<ProgramSubjectMappingAcadsBean> getProgramSubjectMappingList(){
		if(this.programSubjectMappingList == null || this.programSubjectMappingList.size() == 0){
			this.programSubjectMappingList = contentdao.getProgramSubjectMappingList();
		}
		return programSubjectMappingList;
	} 
	
	
	public List<String> getCommonSubjectsMobile(String sapId) {
		
		StudentAcadsBean studentRegistrationData = vdao.getStudentsMostRecentRegistrationData(sapId);
		
		List<String> commonSubjectList = new ArrayList<String>();
		commonSubjectList.add("Guest Session: GST by CA. Bimal Jain");
		commonSubjectList.add("Assignment Preparation Session");
		commonSubjectList.add("Orientation");
		
		if("4".equals(studentRegistrationData.getSem())){
			commonSubjectList.add("Project Preparation Session");
		}
		return commonSubjectList;
	}
	
	public String[] getRecordedvsLiveCurrentYearMonthForSapid(StudentAcadsBean studentRegData) {
		String arr[]= new String[2];
		try {
			List<ExamOrderAcadsBean> liveFlagList = contentdao.getLiveFlagDetails();
			HashMap<String,BigDecimal> examOrderMap = generateExamOrderMap(liveFlagList);
			double acadSessionLiveOrder = getMaxOrderOfAcadSessionLive(liveFlagList);
			double reg_order =  examOrderMap.get(studentRegData.getMonth()+studentRegData.getYear()).doubleValue();
			if(acadSessionLiveOrder == reg_order) {
				arr[0] = studentRegData.getYear();
				arr[1] = studentRegData.getMonth();
				return arr;
			}else {
				arr[0] = CURRENT_ACAD_YEAR;
				arr[1] = CURRENT_ACAD_MONTH;
				return arr;
			}
		}catch (Exception e) {
			// TODO: handle exception
			arr[0] = CURRENT_ACAD_YEAR;
			arr[1] = CURRENT_ACAD_MONTH;
			return arr;
		}
		
		
	}
	
	public double getMaxOrderOfAcadSessionLive(List<ExamOrderAcadsBean> liveFlagList){
		double sessionLiveOrder = 0.0;
		for (ExamOrderAcadsBean bean : liveFlagList) {
			double currentOrder = Double.parseDouble(bean.getOrder());
			if("Y".equalsIgnoreCase(bean.getAcadSessionLive()) && currentOrder > sessionLiveOrder){
				sessionLiveOrder = currentOrder;
			}
		}
		return sessionLiveOrder;
	}



	//For Max Content Order Live
		public  String getTwoContentLiveOrder() {
				List<ExamOrderAcadsBean> liveFlagList = contentdao.getLiveFlagDetails();
				HashMap<String,BigDecimal> examOrderMap = generateExamOrderMap(liveFlagList);
				double maxLiveOrder = ExamOrderUtil.getMaxOrderOfAcadContentLive(liveFlagList);
				double currentCycle_order =  examOrderMap.get(CURRENT_ACAD_MONTH+CURRENT_ACAD_YEAR).doubleValue();
				StringBuffer order =new StringBuffer(String.valueOf(maxLiveOrder)+","+String.valueOf(currentCycle_order));
				return order.toString(); 
		}


		@Override
		public ArrayList<String> getLocationList() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<String> getFacultyIdsByPssIds(String year, String month, String programSemSubjectId) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<VideoContentAcadsBean> getVideoContentForSubjectAndFaculty(ContentAcadsBean bean,
				String facultyId) {
			return vdao.getVideoContentForSubjectAndFacultyId(bean, facultyId);
		}


}
