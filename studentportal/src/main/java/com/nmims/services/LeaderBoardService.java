package com.nmims.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.collections.BidiMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.repository.query.QueryLookupStrategy.Key;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.StudentStudentPortalBean;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Multimap;
import com.nmims.beans.MBAPassFailResponseBean;
import com.nmims.beans.ProgramsStudentPortalBean;
import com.nmims.beans.StudentOpportunity;
import com.nmims.beans.StudentRankBean;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.beans.SubjectCodeBatchBean;
import com.nmims.daos.LeaderBoardDAO;
import com.nmims.exceptions.StudentNotFoundException;
import com.nmims.helpers.SFConnection;
import com.nmims.helpers.SalesforceHelper;
import com.thoughtworks.selenium.webdriven.commands.SetTimeout;

@Service
public class LeaderBoardService {
	
	@Autowired
	LeaderBoardDAO leaderBoardDAO;
	
	@Autowired
	SalesforceHelper salesforceHelper;

	private static final Logger rankLogger = LoggerFactory.getLogger("rankDenormalization");
	
	private static final List<String> BBA_MASTERKEY = Arrays.asList("128","159");
	private static final List<String> BBA_SEM = Arrays.asList("5","6");

	
	public List<StudentRankBean> getCycleWiseRankConfigList( String userId ) throws Exception{
		
		List<StudentRankBean> cycleWiseRankConfigList = new ArrayList<>();
		List<StudentRankBean> registrations = new ArrayList<>();
		List<StudentRankBean> examorder = new ArrayList<>();
		List<StudentRankBean> programDetails = new ArrayList<>();
		
		Map<String,StudentRankBean> examorderMap = new HashMap<>();
		Map<String,StudentRankBean> programDetailsMap = new HashMap<>();
		
		registrations = leaderBoardDAO.getStudentRegistration( userId );
		examorder = leaderBoardDAO.getLiveExamOrder();
		programDetails = leaderBoardDAO.getProgramDetails();
		
		Collections.reverse( registrations );
		
		for( StudentRankBean examorderBean : examorder ) {
		
			examorderMap.put( examorderBean.getAcadMonth()+examorderBean.getYear(), examorderBean);
			
		}

		for( StudentRankBean programBean : programDetails ) {
		
			programDetailsMap.put( programBean.getProgram()+"-"+programBean.getConsumerProgramStructureId(), programBean);
			
		}

		for( StudentRankBean bean : registrations ) {
			
			try {
				
				StudentRankBean rankconfigBean = new StudentRankBean();
	
				rankconfigBean.setSapid( bean.getSapid() );
				rankconfigBean.setSem( bean.getSem() );
				rankconfigBean.setProgram( bean.getProgram() );
				rankconfigBean.setConsumerProgramStructureId( bean.getConsumerProgramStructureId() );
				rankconfigBean.setMonth( examorderMap.get( bean.getMonth()+bean.getYear() ).getMonth() );
				rankconfigBean.setYear( examorderMap.get( bean.getMonth()+bean.getYear() ).getYear() );
				rankconfigBean.setSubjectsCount( programDetailsMap.get( bean.getProgram()+"-"+bean.getConsumerProgramStructureId() ).getSubjectsCount() );
				
				cycleWiseRankConfigList.add(rankconfigBean);
				
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
		
		return cycleWiseRankConfigList;
		
	}
	
	public List<StudentRankBean> getSubjectWiseRankConfigList( String userId ) throws Exception{
		
		System.out.println("in getSubjectWiseRankConfigList");
		
		List<StudentRankBean> subjectWiseRankConfigList = new ArrayList<>();
		List<StudentRankBean> registrations = new ArrayList<>();
		List<StudentRankBean> examorder = new ArrayList<>();
		List<StudentRankBean> subjectMapping = new ArrayList<>();
		
		Map<String,StudentRankBean> examorderMap = new HashMap<>();
		Map<String,List<StudentRankBean>> subjectMappingMap = new HashMap<>();
		
		registrations = leaderBoardDAO.getStudentRegistration( userId );
		examorder = leaderBoardDAO.getLiveExamOrder();
		subjectMapping = leaderBoardDAO.getSubjectDetails();
		
		Collections.reverse( registrations );
		
		for( StudentRankBean examorderBean : examorder ) {
		
			examorderMap.put( examorderBean.getAcadMonth()+examorderBean.getYear(), examorderBean);
			
		}

		for( StudentRankBean registration : registrations ) {

			List<StudentRankBean> subjectlist = new ArrayList<>();
			
			for( StudentRankBean subject : subjectMapping ) {
	
				if( registration.getSem().equals( subject.getSem() ) && 
						registration.getConsumerProgramStructureId().equals( subject.getConsumerProgramStructureId()) ) {
					
					subjectlist.add( subject );
					
				}
				
			}
			//Checking is BBA student or not
			if(BBA_MASTERKEY.contains(registration.getConsumerProgramStructureId()) && BBA_SEM.contains(registration.getSem()))
			{
				try
				{
					//Calling a method to filter another BBA elective subjects
					List<StudentRankBean> bbaElectiveSubjectList = getBBAElectiveSubjectList(subjectlist,userId);
					
					if(bbaElectiveSubjectList.size() > 0)
					{
						subjectlist.clear();
						subjectlist = bbaElectiveSubjectList;
					}
				}
				catch(Exception e)
				{
					throw new Exception("Error in filter another BBA Elective subjects: "+e);
				}
			}
			subjectMappingMap.put( registration.getConsumerProgramStructureId()+"-"+registration.getSem(), subjectlist );
		
		}
		
		for( StudentRankBean bean : registrations ) {

			for( StudentRankBean subject : subjectMappingMap.get( bean.getConsumerProgramStructureId()+"-"+bean.getSem() ) ) {
				
				if( bean.getSem().equals( subject.getSem() ) && 
						bean.getConsumerProgramStructureId().equals( subject.getConsumerProgramStructureId()) ) {

					try {
						
						StudentRankBean rankconfigBean = new StudentRankBean();
	
						rankconfigBean.setSapid( bean.getSapid() );
						rankconfigBean.setSem( bean.getSem() );
						rankconfigBean.setProgram( bean.getProgram() );
						rankconfigBean.setConsumerProgramStructureId( bean.getConsumerProgramStructureId() );
						rankconfigBean.setMonth( examorderMap.get( bean.getMonth()+bean.getYear() ).getMonth() );
						rankconfigBean.setYear( examorderMap.get( bean.getMonth()+bean.getYear() ).getYear() );
						rankconfigBean.setSubjectcodeMappingId( subject.getSubjectcodeMappingId() );
						rankconfigBean.setSubject( subject.getSubject() );
	
						subjectWiseRankConfigList.add(rankconfigBean);
					
					}catch (Exception e) {
						// TODO: handle exception
					}
					
				}
				
			}
			
		}

		return subjectWiseRankConfigList;
		
	}
	
	//To filter the other BBA Elective subjects from all BBA subject list
	public List<StudentRankBean> getBBAElectiveSubjectList(List<StudentRankBean> subjectList, String userId) throws Exception
	{
		List<String> pssIdList = new ArrayList<>();
		
		pssIdList = subjectList.stream().map(StudentRankBean::getSubjectcodeMappingId).collect(Collectors.toList());
		
		List<String> bbaElectivePssIdList = leaderBoardDAO.getBBAElectiveSubjectListByPssidAndSapid(pssIdList, userId);
		
		List<StudentRankBean> bbaElectiveSubjects = subjectList.stream().filter(bean -> bbaElectivePssIdList.contains(bean.getSubjectcodeMappingId())).collect(Collectors.toList());
		
		return bbaElectiveSubjects;
	}
	
	public StudentStudentPortalBean getStudentDetailsForRank( StudentStudentPortalBean bean ) throws Exception{
		
		StudentStudentPortalBean details = new StudentStudentPortalBean();
		
		details = leaderBoardDAO.getStudentDetailsForRank( bean );
		
		return details;
		
	}
	
	public StudentRankBean getDenormalizedCycleWiseRankBySapid(  String masterKey, String year, String month, 
			String sem, String sapid, String program ) throws Exception {
		
		StudentRankBean rankList = new StudentRankBean();
		List<StudentRankBean> topFiveRankList = new ArrayList<>();
		StudentRankBean studentsRank = new StudentRankBean();

		topFiveRankList = leaderBoardDAO.getTopFiveCycleWiseRank( masterKey, program, year, month, sem);
		
		try {
			studentsRank = leaderBoardDAO.getCycleWiseRankForStudent( masterKey, program, year, month, sem, sapid );
		}catch (Exception e) {
			studentsRank = new StudentRankBean();
		}
		
		rankList.setOverAllCycleWiseRank( topFiveRankList );
		rankList.setCycleWiseStudentsRank( studentsRank );
		
		return rankList;
	}
	
	public StudentRankBean getDenormalizedSubjectWiseRankBySapid(  String masterKey, String program, String year, String month, 
			String sem, String subject, String sapid ) throws Exception {
		
		StudentRankBean rankList = new StudentRankBean();
		List<StudentRankBean> topFiveRankList = new ArrayList<>();
		StudentRankBean studentsRank = new StudentRankBean();

		topFiveRankList = leaderBoardDAO.getTopFiveSubjectWiseRank( masterKey, program, year, month, sem, subject );

		try {
			studentsRank = leaderBoardDAO.getSubjectWiseRankForStudent( masterKey, program, year, month, sem, subject, sapid );
		}catch (Exception e) {
			studentsRank = new StudentRankBean();
		}
		
		rankList.setOverAllSubjectWiseRank( topFiveRankList );
		rankList.setSubjectWiseStudentsRank( studentsRank );
		
		return rankList;
	}
	
	public StudentRankBean getDenormalizedCycleWiseRankBySapidForLinkedIn(  String masterKey, String year, String month, 
			String sem, String sapid, String program ) throws Exception {
		
		StudentRankBean rankList = new StudentRankBean();
		List<StudentRankBean> topFiveRankList = new ArrayList<>();

		topFiveRankList = leaderBoardDAO.getTopFiveCycleWiseRankForLinkedIn( masterKey, program, year, month, sem);
		
		try {
			
			final StudentRankBean studentsRank = leaderBoardDAO.getCycleWiseRankForStudent( masterKey, program, year, month, sem, sapid );

			if( Integer.parseInt( studentsRank.getRank().split("/")[0] ) < 6 )
				if( !topFiveRankList.stream().anyMatch( rank -> rank.getRank().equals( studentsRank.getRank() ) && 
						rank.getSapid().equals( studentsRank.getSapid() ) ) ) {
					
					topFiveRankList.removeIf( rank -> rank.getRank().equals( studentsRank.getRank() ) );
					
					topFiveRankList.add(studentsRank);
				}

			rankList.setCycleWiseStudentsRank( studentsRank );
			
		}catch (Exception e) {
			// TODO: handle exception
			rankList.setCycleWiseStudentsRank( leaderBoardDAO.getStudentDetailsForSharingRank( sapid ) );
		}
		
		topFiveRankList.sort( ( rankA, rankB ) -> rankA.getRank().compareTo( rankB.getRank() ) );
		
		rankList.setOverAllCycleWiseRank( topFiveRankList );
		
		return rankList;
	}

	public StudentRankBean getDenormalizedSubjectWiseRankBySapidForLinkedIn(  String masterKey, String program, String year, String month, 
			String sem, String subject, String sapid ) throws Exception {
		
		StudentRankBean rankList = new StudentRankBean();
		List<StudentRankBean> topFiveRankList = new ArrayList<>();
		
		topFiveRankList = leaderBoardDAO.getTopFiveSubjectWiseRankForLinkedIn( masterKey, program, year, month, sem, subject );

		try {
			
			final StudentRankBean studentsRank = leaderBoardDAO.getSubjectWiseRankForStudent( masterKey, program, year, month, sem, subject, sapid );
	
			if( Integer.parseInt( studentsRank.getRank().split("/")[0] ) < 6 )
				if( !topFiveRankList.stream().anyMatch( rank -> rank.getRank().equals( studentsRank.getRank() ) && 
						rank.getSapid().equals( studentsRank.getSapid() ) ) ) {
					
					topFiveRankList.removeIf( rank -> rank.getRank().equals( studentsRank.getRank() ) );
					
					topFiveRankList.add(studentsRank);
				}
			
			rankList.setSubjectWiseStudentsRank( studentsRank );
			
		}catch (Exception e) {
			// TODO: handle exception
			rankList.setSubjectWiseStudentsRank( leaderBoardDAO.getStudentDetailsForSharingRank( sapid ) );
		}
		
		topFiveRankList.sort( ( rankA, rankB ) -> rankA.getRank().compareTo( rankB.getRank() ) );
		
		rankList.setOverAllSubjectWiseRank( topFiveRankList );
		
		return rankList;
	}

	public List<StudentRankBean> getCycleWiseConfigurationToMigrateRank( String examMonth, String examYear ){
		
        List<StudentRankBean> cycleWiseRankConfig = new ArrayList<>();   
        
        cycleWiseRankConfig = leaderBoardDAO.getCycleWiseConfigurationToMigrateRank( examMonth, examYear );
        
        return cycleWiseRankConfig;
	}

	public List<StudentRankBean> getCycleWiseConfigurationForRankDenormalization(){
		
        List<StudentRankBean> cycleWiseRankConfig = new ArrayList<>();   
        
        cycleWiseRankConfig = leaderBoardDAO.getCycleWiseConfigurationForRankDenormalization();
        
        return cycleWiseRankConfig;
	}

	public void fetchAndInsertCycleWiseRankDetails( StudentRankBean bean) throws Exception{
		
        ArrayList<StudentRankBean> cycleWiseRank = new ArrayList<StudentRankBean>();
        
        cycleWiseRank = leaderBoardDAO.getCycleWiseRankDetails( bean );
        	
    	rankLogger.info("in rankDenormalization got cycle wise rank for configuration: "+cycleWiseRank.size());
    		
    	leaderBoardDAO.insertCycleWiseRankDetails( cycleWiseRank );

	}

	public List<StudentRankBean> getSubjectWiseConfigurationForRankDenormalization(){
		
        List<StudentRankBean> subjectWiseRankConfig = new ArrayList<>();   
        
        subjectWiseRankConfig = leaderBoardDAO.getSubjectWiseConfigurationForRankDenormalization();
        
        return subjectWiseRankConfig;
	}

	public List<StudentRankBean> getSubjectWiseConfigurationToMigrateRank( String examMonth, String examYear ){
		
        List<StudentRankBean> subjectWiseRankConfig = new ArrayList<>();   
        
        subjectWiseRankConfig = leaderBoardDAO.getSubjectWiseConfigurationToMigrateRank( examMonth, examYear );
        
        return subjectWiseRankConfig;
	}

	public void fetchAndInsertSubjectWiseRankDetails( StudentRankBean bean) throws Exception{
		
        ArrayList<StudentRankBean> subjectWiseRank = new ArrayList<StudentRankBean>();
        
        subjectWiseRank = leaderBoardDAO.getSubjectWiseRankDetails( bean );
    	
		rankLogger.info("in rankDenormalization got subject wise rank for configuration: "+subjectWiseRank.size());
		
		leaderBoardDAO.insertSubjectWiseRankDetails( subjectWiseRank );

	}

	public List<StudentRankBean> getHomepageRankDetails( StudentStudentPortalBean bean ) throws Exception{
		
		List<StudentRankBean> homepageRank = new ArrayList<>();
		
		homepageRank = leaderBoardDAO.getStudentRankForHomepage( bean.getSapid() );
		
		return homepageRank;
		
	}

	public StudentStudentPortalBean getCycleWiseRankConfigForSharingRank( String userId, String sem ) throws Exception{

		StudentStudentPortalBean cycleWiseRankConfig = new StudentStudentPortalBean();
		StudentRankBean registration = new StudentRankBean();
		
		List<StudentRankBean> examorder = new ArrayList<>();
		List<StudentRankBean> programDetails = new ArrayList<>();
		
		Map<String,StudentRankBean> examorderMap = new HashMap<>();
		Map<String,StudentRankBean> programDetailsMap = new HashMap<>();
		
		registration = leaderBoardDAO.getRegistrationDetailsForSem( userId, sem );
		examorder = leaderBoardDAO.getLiveExamOrder();
		programDetails = leaderBoardDAO.getProgramDetails();
		
		for( StudentRankBean examorderBean : examorder ) {
		
			examorderMap.put( examorderBean.getAcadMonth()+examorderBean.getYear(), examorderBean);
			
		}

		for( StudentRankBean programBean : programDetails ) {
		
			programDetailsMap.put( programBean.getProgram()+"-"+programBean.getConsumerProgramStructureId(), programBean);
			
		}

		cycleWiseRankConfig.setSapid( registration.getSapid() );
		cycleWiseRankConfig.setSem( registration.getSem() );
		cycleWiseRankConfig.setProgram( registration.getProgram() );
		cycleWiseRankConfig.setConsumerProgramStructureId( registration.getConsumerProgramStructureId() );
		cycleWiseRankConfig.setMonth( examorderMap.get( registration.getMonth()+registration.getYear() ).getMonth() );
		cycleWiseRankConfig.setYear( examorderMap.get( registration.getMonth()+registration.getYear() ).getYear() );
		cycleWiseRankConfig.setSubjectsCount( programDetailsMap.get( registration.getProgram()+
				"-"+registration.getConsumerProgramStructureId() ).getSubjectsCount() );

		return cycleWiseRankConfig;
		
	}
	
	public StudentStudentPortalBean getSubjectWiseRankConfigForSharingRank( String userId, String sem, String subjectName ) throws Exception{
		
		System.out.println("in getSubjectWiseRankConfigList");
		StudentRankBean registration = new StudentRankBean();
		
		StudentStudentPortalBean subjectWiseRankConfig = new StudentStudentPortalBean();
		List<StudentRankBean> examorder = new ArrayList<>();
		List<StudentRankBean> subjectMapping = new ArrayList<>();
		List<StudentRankBean> subjectlist = new ArrayList<>();
		
		Map<String,StudentRankBean> examorderMap = new HashMap<>();
		Map<String,List<StudentRankBean>> subjectMappingMap = new HashMap<>();
		
		registration = leaderBoardDAO.getRegistrationDetailsForSem( userId, sem );
		examorder = leaderBoardDAO.getLiveExamOrder();
		subjectMapping = leaderBoardDAO.getSubjectDetails();
		
		for( StudentRankBean examorderBean : examorder ) {
		
			examorderMap.put( examorderBean.getAcadMonth()+examorderBean.getYear(), examorderBean);
			
		}

		for( StudentRankBean subject : subjectMapping ) {

			if( registration.getSem().equals( subject.getSem() ) && 
					registration.getConsumerProgramStructureId().equals( subject.getConsumerProgramStructureId()) ) {

				subjectlist.add( subject );
				subjectMappingMap.put(registration.getConsumerProgramStructureId()+"-"+registration.getSem(), subjectlist);

			}

		}

		for( StudentRankBean subject : subjectMappingMap.get( registration.getConsumerProgramStructureId()+"-"+registration.getSem() ) ) {

			if( registration.getSem().equals( subject.getSem() ) && 
					registration.getConsumerProgramStructureId().equals( subject.getConsumerProgramStructureId()) 
					&& subjectName.equals( subject.getSubject() ) ) {

				try {

					subjectWiseRankConfig.setSapid( registration.getSapid() );
					subjectWiseRankConfig.setSem( registration.getSem() );
					subjectWiseRankConfig.setProgram( registration.getProgram() );
					subjectWiseRankConfig.setConsumerProgramStructureId( registration.getConsumerProgramStructureId() );
					subjectWiseRankConfig.setMonth( examorderMap.get( registration.getMonth()+registration.getYear() ).getMonth() );
					subjectWiseRankConfig.setYear( examorderMap.get( registration.getMonth()+registration.getYear() ).getYear() );
					subjectWiseRankConfig.setSubject( subject.getSubject() );

				}catch (Exception e) {
					// TODO: handle exception
				}

			}

		}

		return subjectWiseRankConfig;
		
	}
	
	/**@implNote
	 * @author Gaurav Yadav
	 * @param month
	 * @param year
	 * @param list of masterkeys
	 * @return map of string and object as value
	 * @reason to migrate subject wise rank
	 * */
	@SuppressWarnings({"unchecked" })
	public Map<String,Object> migrateSubjectWiseRank(String month,String year,List<String> masterKeys) throws Exception{
		Map<String,Object> responseMap = new HashMap<>();
		if(leaderBoardDAO.checkAlreadyLiveSubjectWiseRank(month, year)>0) {
			responseMap.put("status", "error");
			responseMap.put("message", "Subject Wise Rank is already Migrated For Month : "+month+" And Year : "+year);
		}else {
			List<String> sapIdSubjectList = leaderBoardDAO.getSapIdAndSubjectList(month,year);
			
			List<String> passScore0PssIds = leaderBoardDAO.getPassScore0SubjectList();
			
			Map<String,String> timeboundIdPssIdMap = leaderBoardDAO.getTimeboundIdAndPssIdMap(month,year,passScore0PssIds);
			
			Map<String,List<String>> timeboundIdSapIdsMap = getTimeboundIdSapIdsMap(timeboundIdPssIdMap);
			
			List<String> sapIdList = new ArrayList<>();
			
			Map<String,Object> pssIdDetailsMap = leaderBoardDAO.getPssIdDetailsMap();
			
			Map<String,String> pssIdSubjectMap = (Map<String, String>) pssIdDetailsMap.get("pssIdSubjectMap");
			
			Map<String,String> pssIdSemMap = (Map<String, String>) pssIdDetailsMap.get("pssIdSemMap");
			
			Map<String, String> sapIdSemMapFromSFDC = salesforceHelper.getSapIdSemMap(month, year);
			
			Map<String, String> sapIdSemMapFromDB = leaderBoardDAO.getSapIdSemMap(month, year,masterKeys);
			
			Map<String,List<MBAPassFailResponseBean>> timeboundIdPassFailDetailsMap = getTimeboundIdPassFailDetailsMap(timeboundIdSapIdsMap,sapIdList,timeboundIdPssIdMap,sapIdSemMapFromSFDC,passScore0PssIds,sapIdSubjectList,pssIdSubjectMap);

			Map<String,StudentStudentPortalBean> studentMap = (Map<String,StudentStudentPortalBean>)leaderBoardDAO.getStudentDetailsUsingSapIds(sapIdList).get("studentMap");
			
			//below method will student pass fail details if student sapid is not present in exam.students table
			removePassFailDetailsForStudent(timeboundIdPassFailDetailsMap,studentMap);
			
			calculateRankInTimeboundIdMarksMap(timeboundIdPassFailDetailsMap,timeboundIdSapIdsMap);
			
			insertRankInDatabase(timeboundIdPassFailDetailsMap,pssIdSubjectMap,timeboundIdPssIdMap,studentMap,month,year,sapIdSemMapFromSFDC,sapIdSemMapFromDB,pssIdSemMap);
			
			responseMap.put("status", "success");
			responseMap.put("message", "Migrated Subject Wise Rank For Month : " + month + " And Year : " + year);
		}
		
		return responseMap;
	}
	
	/**@implNote
	 * @author Gaurav Yadav
	 * @param masterkey
	 * @param sapid
	 * @return map of string and object as value
	 * @reason to get subject wise rank for timebound students
	 * */
	@SuppressWarnings({ "unchecked"})
	public Map<String,Object> getSubjectWiseRankForStudent(String masterkey,String sapid) throws Exception,RuntimeException{
		List<String> masterKeys = new ArrayList<>();
		masterKeys.add(masterkey);
		
		List<MBAPassFailResponseBean> specificStudentRankList = (List<MBAPassFailResponseBean>)leaderBoardDAO.getStudentSubjectWiseRankForTimebound(sapid).get("rankList");
		
		List<String> timeboundIds = specificStudentRankList.stream().map(bean->bean.getTimeboundId()).collect(Collectors.toList());
		
		Map<String,MBAPassFailResponseBean> timeboundIdRankIdsMap = getTimeboundIdRankIdsMap(timeboundIds);
		
		List<String> rankIds = getRankIdsFromMap(timeboundIdRankIdsMap);
		
		Map<String,MBAPassFailResponseBean> rankIdDetailsMap = getRankIdsDetailsMap(rankIds);
		
		setTop5StudentRankIdsInSpecificStudentRankList(specificStudentRankList,timeboundIdRankIdsMap);

		sortSpecificStudentRankListBySem(specificStudentRankList);
		
		Map<String,Object> responseMap = new HashMap<>();
		
		responseMap.put("specificStudentRank", specificStudentRankList);
		
		responseMap.put("allStudentRank", rankIdDetailsMap);
		
		return responseMap;
	}
	
	/**@implNote
	 * @author Gaurav Yadav
	 * @param timeboundIdMarksMap
	 * @reason calculate rank
	 * */
	void calculateRankInTimeboundIdMarksMap(Map<String,List<MBAPassFailResponseBean>> timeboundIdMarksMap,Map<String,List<String>> timeboundIdSapIdsMap) {
		timeboundIdMarksMap.keySet().stream().forEach(timeboundId -> {
		    List<MBAPassFailResponseBean> backlogData = timeboundIdMarksMap.get(timeboundId).stream().filter(bean -> StringUtils.isBlank(bean.getIaScore()) || bean.getIsPass().equalsIgnoreCase("N")).collect(Collectors.toList());
		    List<MBAPassFailResponseBean> clearedData = timeboundIdMarksMap.get(timeboundId).stream().filter(bean -> !backlogData.stream().map(key -> key.getSapid()).collect(Collectors.toList()).contains(bean.getSapid())).collect(Collectors.toList());
		    clearedData.forEach(mark -> {
		        Integer teescore = StringUtils.isBlank(mark.getTeeScore()) ? 0 : Integer.parseInt(mark.getTeeScore());
		        Integer iascore = StringUtils.isBlank(mark.getIaScore()) ? 0 : Integer.parseInt(mark.getIaScore());
		        Integer gracemarks = StringUtils.isBlank(mark.getGraceMarks()) ? 0 : Integer.parseInt(mark.getGraceMarks());
		        mark.setTotal(teescore + iascore + gracemarks);
		        mark.setTimeboundId(timeboundId);
		    });

		    Collections.sort(clearedData, Collections.reverseOrder(Comparator.comparingInt(MBAPassFailResponseBean::getTotal)));

		    List<Integer> totalList = clearedData.stream().map(x -> x.getTotal()).distinct().collect(Collectors.toList());
		    clearedData.forEach(mark -> {
		        mark.setRank(String.valueOf(totalList.indexOf(mark.getTotal()) + 1) + "/" + timeboundIdSapIdsMap.get(timeboundId).size());
		    });
		    backlogData.stream().forEach(bean -> {
		        bean.setRank("0");
		    });

		    // Merge clearedData and backlogData into a single list
		    clearedData.addAll(backlogData);
		    // Update the timeboundIdMarksMap with the merged list
		    timeboundIdMarksMap.put(timeboundId, clearedData);
		});
	}
	
	/**@implNote
	 * Below method will be used to migrate cycle wise rank for timebound[MBA-WX] students
	 * @param month
	 * @param year
	 * @param masterkeys 
	 * */
	@SuppressWarnings("unchecked")
	public Map<String,Object> migrateCycleWiseRank(String month,String year,List<String> masterKeys) throws Exception{
		
		Map<String,Object> responseMap = new HashMap<>();

		if(leaderBoardDAO.checkRankForMonthYear(month, year)>0) {
			
			responseMap.put("status", "error");
			responseMap.put("message", "Cycle Wise Rank is already migrated For Month : "+month+" And Year : "+year);
			
		} else {
			
			Map<String,Integer> masterKeySemSubjectCountMap = getMasterKeySemSubjectCountMap();
			
			Map<String, String> sapIdSemMapFromSFDC = salesforceHelper.getSapIdSemMap(month, year);

			List<String> passScore0PssIds = leaderBoardDAO.getPassScore0SubjectList();
			
			List<String> batchIds = leaderBoardDAO.getBatchIds(month, year, masterKeys);
			
			List<String> sapIdListFromSFDC = new ArrayList<>(sapIdSemMapFromSFDC.keySet());	
			
			Map<String,Object> studentDetailsMap = getStudentDetailsMap(sapIdListFromSFDC);
			
			List<StudentStudentPortalBean> studentList = (List<StudentStudentPortalBean>)studentDetailsMap.get("studentList");
			
			setSemInStudentList(studentList,sapIdSemMapFromSFDC);
			
			Map<String, List<String>> masterKeySemSapIdsMapFromSFDC = getMasterKeySemSapIdsMapFromSFDC(studentList);
			
			Map<String,StudentStudentPortalBean> studentMap = getStudentMap(studentList);
			
			List<String> timeboundIds = getMasterKeySemTimeboundIdsMap(batchIds, passScore0PssIds, month, year);
			
			Map<String,List<String>> masterKeySemSapIdsMap = getMasterKeySemSapIdsMap(timeboundIds, masterKeySemSubjectCountMap, masterKeySemSapIdsMapFromSFDC, month, year);
			
			Map<String,List<String>> masterKeySemFailedSapIdsMap = new HashMap<>();
			
			Map<String,List<MBAPassFailResponseBean>> masterKeySemMarksMap = new HashMap<>();
			
			checkAndSeprateOutPassedAndFailedStudentList(masterKeySemSapIdsMap, timeboundIds, masterKeySemMarksMap, masterKeySemSubjectCountMap, masterKeySemFailedSapIdsMap);
			
			Map<String,List<MBAPassFailResponseBean>> masterKeySemFinalMarksMap = getMasterKeySemFinalMarksMap(masterKeySemSapIdsMap,masterKeySemMarksMap,masterKeySemSubjectCountMap,month,year,studentMap,masterKeySemFailedSapIdsMap);
			
			Map<String,List<Integer>> masterKeySemTotalMap = getMasterKeySemTotalMap(masterKeySemFinalMarksMap);
			
			insertCycleWiseRank(masterKeySemFinalMarksMap,masterKeySemTotalMap,month,year);

			responseMap.put("status", "success");
			responseMap.put("message", "Migrated Cycle Wise Rank For Month : "+month+" And Year : "+year);
				
			}
		
		return responseMap;
		
	}
	
	/**@implNote
	 * @author Gaurav Yadav
	 * @param SemPssIdCountMap
	 * @param month
	 * @param year
	 * @return map of key(masterkey/sem) and list of timeboundIds as a value
	 * @reason to get map of key(masterkey/sem) and list of timeboundIds as a value
	 * */
	public Map<String,List<String>> getSemTimeboundMap(Map<String,MBAPassFailResponseBean> SemPssIdCountMap,String month,String year) {
		Map<String,List<String>> semTimeboundMap = new HashMap<>();
		if(SemPssIdCountMap.size()>0) {
			SemPssIdCountMap.keySet().stream().forEach(key->{
				List<String> getSemTimeboundIdList = leaderBoardDAO.getSemTimeboundIdList(Arrays.asList(SemPssIdCountMap.get(key).getPssIds().split(",")),month,year);
				if(getSemTimeboundIdList.size()>0) {
					semTimeboundMap.put(key,getSemTimeboundIdList);	
				}	
			});
		}
		return semTimeboundMap;
	}
	
	/**@implNote
	 * Below method will be used to get MasterKey Sem Final Marks Map
	 * Which we will further use during cycle wise rank insertion
	 * @param masterKeySemSapIdsMap
	 * @param masterKeyMarksMap
	 * @param masterKeySemSubjectCountMap
	 * @param month
	 * @param year
	 * @param masterKeySemFailedSapIdsMap
	 * */
	public Map<String,List<MBAPassFailResponseBean>> getMasterKeySemFinalMarksMap(Map<String,List<String>> masterKeySemSapIdsMap,Map<String,List<MBAPassFailResponseBean>> masterkeySemMarksMap,Map<String,Integer> masterkeySemSubjectCountMap,String month,String year,Map<String,StudentStudentPortalBean> studentMap,Map<String,List<String>> masterKeySemFailedSapIdsMap) {
		Map<String,List<MBAPassFailResponseBean>> finalMarksMap = new HashMap<>();
		addPassedStudentBeanInFinalMarksMap(masterkeySemMarksMap, finalMarksMap, masterKeySemSapIdsMap, masterkeySemSubjectCountMap, studentMap, month, year);
		addFailedStudentBeanInFinalMarksMap(finalMarksMap, masterKeySemFailedSapIdsMap, masterkeySemSubjectCountMap, studentMap, month, year);
		return finalMarksMap;
	}
	
	/**@implNote
	 * Below method will be use to get masterKeySemTotalMap
	 * which we will further use to assign rank 
	 * @param finalMarksmap
	 * */
	public Map<String,List<Integer>> getMasterKeySemTotalMap(Map<String,List<MBAPassFailResponseBean>> finalMarksMap) {
		Map<String,List<Integer>> masterKeySemTotalMap=new HashMap<>();
		if(finalMarksMap.size()>0) {
			finalMarksMap.keySet().stream().forEach(sem->{
				List<MBAPassFailResponseBean> studentMarks = (List<MBAPassFailResponseBean>) finalMarksMap.get(sem);
				 Collections.sort(studentMarks,Collections.reverseOrder(Comparator.comparingInt(MBAPassFailResponseBean::getTotal)));
				 List<Integer> totalList =studentMarks.stream().map(x->x.getTotal()).distinct().collect(Collectors.toList());
				 if(totalList.size()>0) {
					 masterKeySemTotalMap.put(sem, totalList);
				 }
			});
		}
		 return masterKeySemTotalMap;
	}
	
	/**@implNote
	 * Below method will be use to insert cycle wise rank details in exam.cycle_wise_rank_timebound table
	 * @param masterKeySemFinalMarksMap
	 * @param masterKeySemTotalMap
	 * @param month
	 * @param year
	 * */
	public void insertCycleWiseRank(Map<String,List<MBAPassFailResponseBean>> masterKeySemFinalMarksMap,Map<String,List<Integer>> masterKeySemTotalMap,String month,String year) throws Exception{
		if(masterKeySemFinalMarksMap.size()>0) {
			masterKeySemFinalMarksMap.keySet().stream().forEach(bean->{
				if(masterKeySemFinalMarksMap.get(bean)!=null) {
					try {
						leaderBoardDAO.insertCycleWiseRankDetailsForTimebound(masterKeySemFinalMarksMap.get(bean),masterKeySemTotalMap.get(bean),masterKeySemFinalMarksMap.get(bean).size());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						throw new RuntimeException(" Error while Inserting cycle wise rank for month : "+month+" year : "+year,e);
					}
				}
			});
		}
	}
	
	public Map<String,Object> getRegistrationMap(String month,String year,List<String> masterkey) {
		Map<String, Object> registrationMap = new HashMap<>();;
		try {
			registrationMap = (Map<String,Object>)leaderBoardDAO.getSapidFromRegistrationMap(month, year, masterkey);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(" Error while fetch registration details for month : "+month+" year : "+year,e);
		}
		return registrationMap;
	}
	
	/**@implNote
	 * @author Gaurav Yadav
	 * @param list of timebound ids
	 * @param set of sapids
	 * @return list of object(MBAPassFailResponseBean) as a value
	 * @reason to get list of object(MBAPassFailResponseBean)
	 * */
	public List<MBAPassFailResponseBean> getMarksDetails(List<String> timeboundIds,Set<String> sapIds) {
		List<MBAPassFailResponseBean> marksDetails = new ArrayList<>();
		if(timeboundIds.size()>0&&sapIds.size()>0) {
			marksDetails.addAll(leaderBoardDAO.getPassFailDetailsForMBAWX(sapIds,timeboundIds));
		}
		return marksDetails;
	}
	
	/**@implNote
	 * @author Gaurav Yadav
	 * @param masterkey
	 * @param sapid
	 * @return map of string and object as value
	 * @reason to get subject wise rank for timebound students
	 * */
	public Map<String,Object> getCycleWiseRankForStudent(String masterkey,String sapid) throws Exception,RuntimeException{
		
		List<String> masterKeys = new ArrayList<>();
		
		masterKeys.add(masterkey);
		
		Map<String,Object> responseMap = new HashMap<>();
			
		List<MBAPassFailResponseBean> specificStudentRankList = leaderBoardDAO.getStudentCycleWiseRankForTimebound(sapid);

		setTop5StudentRankIdsInSpecificStudentRankList(specificStudentRankList);
			
		List<String> rankIds = getRankIdsList(specificStudentRankList);
			
		Map<String,MBAPassFailResponseBean> AllRankBasedMap = getRankMap(rankIds);
		
		sortSpecificStudentRankListBySem(specificStudentRankList);
			
		responseMap.put("specificStudentRank", specificStudentRankList);
		
		responseMap.put("allStudentRank", AllRankBasedMap);
			
		
		return responseMap;
	}
	
	/**@implNote
	 * Below method will be used to get rankIds List from rankList
	 * @param rankList
	 * */
	public List<String> getRankIdsList(List<MBAPassFailResponseBean> rankList) {
		List<String> rankIds = new ArrayList<>();
		if(rankList.size()>0) {
			rankIds.addAll(rankList.stream()
			        .map(bean -> bean.getRankIds())
			        .flatMap(List::stream)
			        .sorted()// Flatten the List<List<String>> to List<String>
			        .collect(Collectors.toList()));
		}
		return rankIds;
	}
	
	/**@implNote
	 * Below method will be used to get rankMap
	 * will we will further use in ui to display top 5 rank details
	 * @param rankIds
	 * */
	public Map<String,MBAPassFailResponseBean> getRankMap(List<String> rankIds) {
		Map<String,MBAPassFailResponseBean> AllRankBasedMap = new HashMap<>();
		if(rankIds.size()>0) {
			AllRankBasedMap = leaderBoardDAO.getAllCycleWiseRankForTimeboundStudents(rankIds);
		}
		return AllRankBasedMap;
	}
	
	/**@implNote
	 * @author Gaurav Yadav
	 * @param specificStudentsMarksMap
	 * @param rankIdsMap
	 * @param AllRankBasedMap
	 * @reason to put rank details based on its rank ids
	 * @param pssIdSubjectMap
	 * @param timeboundIdPssIdMap
	 * @param studentMap
	 * @param timeboundNoOfStudentsMap
	 * @reason to insert subject wise in database
	 * */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	void insertRankInDatabase(Map<String, List<MBAPassFailResponseBean>> timeboundIdPassFailDetailsMap,Map<String, String> pssIdSubjectMap,Map<String, String> timeboundIdPssIdMap,Map<String, StudentStudentPortalBean> studentMap,String month,String year,Map<String, String> sapIdSemMapFromSFDC,Map<String,String> sapIdSemMapFromDB,Map<String,String> pssIdSemMap) throws Exception {
		timeboundIdPassFailDetailsMap.keySet().stream().forEach(timeboundId->{
					try {
						if(timeboundIdPassFailDetailsMap.get(timeboundId)!=null) {
							leaderBoardDAO.insertSubjectWiseRankDetailsForTimebound(timeboundIdPassFailDetailsMap.get(timeboundId),pssIdSubjectMap.get(timeboundIdPssIdMap.get(timeboundId)),month,year,sapIdSemMapFromSFDC,studentMap,sapIdSemMapFromDB,pssIdSemMap,timeboundIdPssIdMap);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						throw new RuntimeException("Error While inserting subject wise rank : ",e);
					}
		});
	}
	
	public Map<String,List<MBAPassFailResponseBean>> getTimeboundIdPassFailDetailsMap(Map<String,List<String>> timeboundIdSapIdsMap,List<String> sapIdList,Map<String,String> timeboundIdPssIdMap,Map<String,String> sapIdSemMapFromSFDC,List<String> passScore0PssIds,List<String> sapIdSubjectList,Map<String,String> pssIdSubjectMap){
		Map<String,List<MBAPassFailResponseBean>> timeboundIdPassFailDetailsMap = new HashMap<>();
		timeboundIdSapIdsMap.keySet().stream().forEach(timeboundId->{
			sapIdList.addAll(timeboundIdSapIdsMap.get(timeboundId));
			List<MBAPassFailResponseBean> list = leaderBoardDAO.getPassFailDetails(timeboundId,timeboundIdSapIdsMap.get(timeboundId));
			List<String> sapIds = list.stream().map(mark->mark.getSapid()).distinct().collect(Collectors.toList());
			List<String> filteredSapIds = timeboundIdSapIdsMap.get(timeboundId).stream().filter(key->!sapIds.contains(key)).collect(Collectors.toList());
			if(filteredSapIds.size()>0) {
				filteredSapIds.stream().forEach(sapid->{
					MBAPassFailResponseBean markBean = new MBAPassFailResponseBean();
					markBean.setTimeboundId(timeboundId);
					markBean.setSapid(sapid);
					markBean.setPssId(timeboundIdPssIdMap.get(timeboundId));
					markBean.setGraceMarks("0");
					markBean.setTeeScore("0");
					markBean.setIaScore("0");
					markBean.setTotal(0);
					markBean.setRank("0");
					markBean.setIsPass("N");
					list.add(markBean);
				});
			}
			list.removeIf(bean->passScore0PssIds.contains(bean.getPssId())||!sapIdSemMapFromSFDC.keySet().contains(bean.getSapid()));
			if(list.size()>0) {
				if(sapIdSubjectList.size()>0) {
					timeboundIdSapIdsMap.get(timeboundId).stream().forEach(sapid->{
						List<String> sapIdSubjectListNew = sapIdSubjectList.stream().filter(key->key.indexOf(sapid)!=-1).collect(Collectors.toList());
						if(sapIdSubjectListNew.size()>0) {
							sapIdSubjectListNew.stream().forEach(key->{
								List<String> pssIds = pssIdSubjectMap.keySet().stream().filter(pssId->key.indexOf(pssIdSubjectMap.get(pssId))!=-1).collect(Collectors.toList());
								list.removeIf(mark->mark.getSapid().equalsIgnoreCase(sapid)&&pssIds.contains(mark.getPssId()));
							});
						}
					});
				}
				timeboundIdPassFailDetailsMap.put(timeboundId, list);
			}
		});
		return timeboundIdPassFailDetailsMap;
	}
	
	public void removePassFailDetailsForStudent(Map<String,List<MBAPassFailResponseBean>> timeboundIdPassFailDetailsMap,Map<String,StudentStudentPortalBean> studentMap) {
		if(timeboundIdPassFailDetailsMap.size()>0) {
			timeboundIdPassFailDetailsMap.keySet().stream().forEach(bean->{
				rankLogger.info("Student SapId is not presend in students table : "+timeboundIdPassFailDetailsMap.get(bean).stream().filter(key->!(studentMap.get(key.getSapid())!=null)).collect(Collectors.toList()));
				timeboundIdPassFailDetailsMap.get(bean).removeIf(key->!(studentMap.get(key.getSapid())!=null));
			});
		}
	}
	
	public Map<String,List<String>> getTimeboundIdSapIdsMap(Map<String,String> timeboundIdPssIdMap){
		Map<String,List<String>> timeboundIdSapIdsMap = new HashMap<>();
		timeboundIdPssIdMap.keySet().stream().forEach(timeboundId->{
			List<String> sapIds = leaderBoardDAO.getSapIdsFromTUM(timeboundId);
			if(sapIds.size()>0) {
				timeboundIdSapIdsMap.put(timeboundId,sapIds);
			}
		});
		return timeboundIdSapIdsMap;
	}
	
	public Map<String,MBAPassFailResponseBean> getTimeboundIdRankIdsMap(List<String> timeboundIds){
		Map<String,MBAPassFailResponseBean> timeboundIdRankIdsMap = new HashMap<>();
		if(timeboundIds.size()>0) {
			timeboundIdRankIdsMap.putAll(leaderBoardDAO.getRankIdsForTimeboundForAllStudents(timeboundIds));
		}
		return timeboundIdRankIdsMap;
	}
	
	public Map<String,MBAPassFailResponseBean> getRankIdsDetailsMap(List<String> rankIds){
		Map<String,MBAPassFailResponseBean> rankIdDetailsMap = new HashMap<>();
		if(rankIds.size()>0) {
			rankIdDetailsMap.putAll(leaderBoardDAO.getAllSubjectRankForTimeboundStudents(rankIds));
		}
		return rankIdDetailsMap;
	}
	
	public void setTop5StudentRankIdsInSpecificStudentRankList(List<MBAPassFailResponseBean> specificStudentRankList,Map<String,MBAPassFailResponseBean> timeboundIdRankIdsMap) {
		if (specificStudentRankList.size() > 0 && timeboundIdRankIdsMap.size() > 0) {
		    specificStudentRankList.stream().forEach(bean -> {
		        if (bean != null && bean.getTimeboundId() != null && timeboundIdRankIdsMap.get(bean.getTimeboundId()) != null) {
		            bean.setRankIds(timeboundIdRankIdsMap.get(bean.getTimeboundId()).getRankIds());
		        }
		    });
		}
	}
	
	/**@implNote
	 * Below method will be used to set top 5 rank ids in specificStudentRankList
	 * @param specificStudentRankList
	 * */
	public void setTop5StudentRankIdsInSpecificStudentRankList(List<MBAPassFailResponseBean> specificStudentRankList) {
		specificStudentRankList.stream().forEach(rank->{
			List<MBAPassFailResponseBean> list = leaderBoardDAO.getRankIdsForTimeboundForAllStudents(rank.getMasterkey(),rank.getMonth(),rank.getYear(),rank.getSem());
			if(list.size()>0&&list.get(0).getRankIds()!=null) {
				rank.setRankIds(list.get(0).getRankIds());
			}
			
		});
	}
	
	/**@implNote
	 * Below method will be use to sort Specific Student Rank List by sem
	 * @param specificStudentRankList
	 * */
	public void sortSpecificStudentRankListBySem(List<MBAPassFailResponseBean> specificStudentRankList) {
		if(specificStudentRankList.size()>0) {
			Collections.sort(specificStudentRankList,Comparator.comparing(MBAPassFailResponseBean::getSem));
		}
	}
	
	public List<String> getRankIdsFromMap(Map<String,MBAPassFailResponseBean> timeboundIdRankIdsMap){
		List<String> rankIds = new ArrayList<>();
		if(timeboundIdRankIdsMap.size()>0) {
			rankIds.addAll(timeboundIdRankIdsMap.keySet().stream().map(bean->timeboundIdRankIdsMap.get(bean).getRankIds()).flatMap(List::stream)
			        .collect(Collectors.toList()));
		}
		return rankIds;
	}
	
	/**@implNote
	 * Below method will be use to add failed sapids of student in final marks map
	 * which we will further use during cycle wise rank insertion
	 * @param finalMarksMap
	 * @param masterKeySemFailedSapIdsMap
	 * @param masterKeySemSubjectCountMap
	 * @param studentMap
	 * @param month
	 * @param year 
	 * */
	public void addFailedStudentBeanInFinalMarksMap(Map<String,List<MBAPassFailResponseBean>> finalMarksMap,Map<String,List<String>> masterKeySemFailedSapIdsMap,Map<String,Integer> masterKeySemSubjectCountMap,Map<String,StudentStudentPortalBean> studentMap,String month,String year) {
		masterKeySemFailedSapIdsMap.keySet().stream().forEach(key->{
			List<MBAPassFailResponseBean> finalMarks = new ArrayList<>();
			if(masterKeySemFailedSapIdsMap.get(key)!=null&&masterKeySemFailedSapIdsMap.get(key).size()>0&&masterKeySemSubjectCountMap.get(key)!=null&&masterKeySemSubjectCountMap.get(key)>0) {
				masterKeySemFailedSapIdsMap.get(key).stream().forEach(sapid->{
					if(studentMap.keySet().contains(sapid)) {
					MBAPassFailResponseBean finalBean = new MBAPassFailResponseBean();
					finalBean.setSapid(sapid);
					finalBean.setTotal(0);
					finalBean.setOutOfMarks(String.valueOf(masterKeySemSubjectCountMap.get(key)*100));
			        finalBean.setSem(studentMap.get(sapid).getSem());
			        finalBean.setMasterkey(studentMap.get(sapid).getConsumerProgramStructureId());
			        finalBean.setMonth(month);
			        finalBean.setYear(year);
			        finalBean.setRank("0");
			        finalBean.setName(studentMap.get(sapid).getFirstName()+" "+studentMap.get(sapid).getLastName());
			        finalBean.setImageUrl(studentMap.get(sapid).getImageUrl());
			        finalMarks.add(finalBean);
					}else {
						rankLogger.info("SapId is not present in student table : "+sapid);
					}
				});
				if(finalMarksMap.get(key)!=null) {
					finalMarksMap.get(key).addAll(finalMarks);
				}else {
					finalMarksMap.put(key,finalMarks);
				}
			}
		});
	}
	
	/**@implNote
	 * Below method will be use to add passed student in final marks map
	 * which we will further use during cycle wise rank insertion
	 * @param month
	 * @param year
	 * @param masterkeys 
	 * */
	public void addPassedStudentBeanInFinalMarksMap(Map<String,List<MBAPassFailResponseBean>> masterKeySemMarksMap,Map<String,List<MBAPassFailResponseBean>> finalMarksMap,Map<String,List<String>> masterKeySemSapIdsMap,Map<String,Integer> masterKeySemSubjectCountMap,Map<String,StudentStudentPortalBean> studentMap,String month,String year) {
		if(masterKeySemSapIdsMap.size()>0&& masterKeySemMarksMap.size()>0&&masterKeySemSubjectCountMap.size()>0) {
			masterKeySemSapIdsMap.keySet().stream().forEach(key->{
			List<MBAPassFailResponseBean> finalMarks = new ArrayList<>();
			if(masterKeySemSapIdsMap.get(key)!=null&&masterKeySemMarksMap.get(key)!=null) {
				masterKeySemSapIdsMap.get(key).stream().forEach(sapid->{
					List<MBAPassFailResponseBean> sapIdMarks = masterKeySemMarksMap.get(key).stream().filter(bean->bean.getSapid().equalsIgnoreCase(sapid)).collect(Collectors.toList());
					MBAPassFailResponseBean finalBean = new MBAPassFailResponseBean();
					int total = 0;
					for (MBAPassFailResponseBean mark : sapIdMarks) {
						if(mark.getTeeScore()!=null&&mark.getIaScore()!=null&&mark.getGraceMarks()!=null) {
							total=total+Integer.parseInt(mark.getIaScore())+Integer.parseInt(mark.getTeeScore())+Integer.parseInt(mark.getGraceMarks());
						}else if(mark.getTeeScore()!=null&&mark.getIaScore()!=null) {
							total=total+Integer.parseInt(mark.getIaScore())+Integer.parseInt(mark.getTeeScore());
						}else if(mark.getTeeScore()!=null) {
							total=total+Integer.parseInt(mark.getTeeScore());
						}else if(mark.getIaScore()!=null) {
							total=total+Integer.parseInt(mark.getIaScore());
						}else {
							continue;
						}
					}
			        finalBean.setTotal(total);
			        finalBean.setOutOfMarks(String.valueOf(masterKeySemSubjectCountMap.get(key)*100));
			        finalBean.setSapid(sapid);
			        finalBean.setSem(studentMap.get(sapid).getSem());
			        finalBean.setMasterkey(studentMap.get(sapid).getConsumerProgramStructureId());
			        finalBean.setMonth(month);
			        finalBean.setYear(year);
			        finalBean.setName(studentMap.get(sapid).getFirstName()+" "+studentMap.get(sapid).getLastName());
			        finalBean.setImageUrl(studentMap.get(sapid).getImageUrl());
			        if(!finalMarks.stream().map(mark->mark.getSapid()).collect(Collectors.toList()).contains(sapid)) {
			        	finalMarks.add(finalBean);
			        }
				});
				finalMarksMap.put(key,finalMarks);
				}
				
			});
		}
	}

	/**@implNote
	 * Below method will be use to check and separate out pass and failed student list based on their subject count
	 * which we will further use to calculate cycle wise rank 
	 * @param masterKeySemSapIdsMap
	 * @param masterKeySemTimeboundIdsMap
	 * @param masterKeySemMarksMap
	 * @param masterKeySemSubjectCountMap
	 * @param masterKeySemFailedSapIdsMap 
	 * */
	public void checkAndSeprateOutPassedAndFailedStudentList(Map<String,List<String>> masterKeySemSapIdsMap,List<String> timeboundIds,Map<String,List<MBAPassFailResponseBean>> masterKeySemMarksMap,Map<String,Integer> masterKeySemSubjectCountMap,Map<String,List<String>> masterKeySemFailedSapIdsMap) {
		if(masterKeySemSapIdsMap.size()>0&&timeboundIds.size()>0) {
			masterKeySemSapIdsMap.keySet().stream().forEach(key->{
				if(masterKeySemSapIdsMap.get(key)!=null&&masterKeySemSapIdsMap.get(key).size()>0){
					List<String> passStudentSapIds = leaderBoardDAO.getPassStudentSapIds(timeboundIds,masterKeySemSapIdsMap.get(key),masterKeySemSubjectCountMap.get(key));
					List<MBAPassFailResponseBean> marksList = new ArrayList<>();
					if(passStudentSapIds.size()>0) {
						marksList = leaderBoardDAO.getMarksDetailsTimeboundIdList(timeboundIds,passStudentSapIds);
					}
					List<String> failedSapIds = leaderBoardDAO.getFailedStudentSapIds(timeboundIds,masterKeySemSapIdsMap.get(key),passStudentSapIds,masterKeySemSubjectCountMap.get(key));
					if(marksList.size()>0) {
						masterKeySemMarksMap.put(key, marksList);
					}
					if(failedSapIds.size()>0) {
						masterKeySemFailedSapIdsMap.put(key, failedSapIds);
					}
					if(passStudentSapIds.size()>0) {
						masterKeySemSapIdsMap.get(key).removeIf(sapid->!passStudentSapIds.contains(sapid));
					}
				}
			});
		}
	}
	
	/**@implNote
	 * Below method will be use to remove student from masterKeySemSapIds who are not present in student table
	 * @param masterKeySemSapIdsMap
	 * @param studentMap
	 * */
	public void removeSapIdsNotPresentInStudentsTable(Map<String,List<String>> masterKeySemSapIdsMap,Map<String,StudentStudentPortalBean> studentMap) {
		if(masterKeySemSapIdsMap.size()>0&&studentMap.size()>0) {
			masterKeySemSapIdsMap.keySet().stream().forEach(key->{
				masterKeySemSapIdsMap.get(key).removeIf(sapid->!studentMap.keySet().contains(sapid));
			});
		}
	}

	/**@implNote
	 * Below method will be use to get masterKeySemSapIdsMap
	 * while we will further use to get masterKeySemMarksMap
	 * @param masterKeySemTimeboundIdsMap
	 * @param masterKeySemSubjectCountMap
	 * @param masterKeySemSapIdsMap 
	 * */
	public Map<String,List<String>> getMasterKeySemSapIdsMap(List<String> timeboundIds,Map<String,Integer> masterKeySemSubjectCountMap,Map<String,List<String>> masterKeySemSapIdsMapFromSFDC,String month,String year){
		Map<String,List<String>> masterKeySemSapIdsMap = new HashMap<>();
		if(masterKeySemSapIdsMapFromSFDC.size()>0){
			masterKeySemSapIdsMapFromSFDC.keySet().stream().forEach(key->{
				 List<String> sapIds = new ArrayList<>();
				try {
					sapIds = leaderBoardDAO.getSapIds(timeboundIds,masterKeySemSubjectCountMap.get(key),masterKeySemSapIdsMapFromSFDC.get(key));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new RuntimeException(" Error while fetching from Timebound User mapping for key : "+key+" month : "+month+" year : "+year,e);
				}
				 if(sapIds.size()>0) {
					 masterKeySemSapIdsMap.put(key,sapIds);
				 }
			});
		}
		return masterKeySemSapIdsMap;
	}
	/**@implNote
	 * Below method will be used to get masterKeySemTimeboundIdsMap
	 * While we will further use to get masterKeySemSapIdsMap,masterKeySemMarksMap
	 * @param masterKeySemBatchIdsMap
	 * @param passScore0PssIds
	 * @param month
	 * @param year 
	 * */
	public List<String> getMasterKeySemTimeboundIdsMap(List<String> batchIds,List<String> passScore0PssIds,String month,String year){
	List<String> list = leaderBoardDAO.getTimeboundIdsUsingBatchIds(month, year, batchIds,passScore0PssIds);
	return list;
	}
	
	/**@implNote
	 * Below method will be used to get studentMap
	 * which we will further use to get firstName,lastName,imageUrl
	 * @param studentList
	 * */
	public Map<String,StudentStudentPortalBean> getStudentMap(List<StudentStudentPortalBean> studentList){
		Map<String,StudentStudentPortalBean> studentMap = new HashMap<>();
		if(studentList.size()>0) {
			studentMap.putAll(studentList.stream().collect(Collectors.toMap(StudentStudentPortalBean::getSapid, Function.identity())));
		}
		return studentMap;
	}
	
	/**@implNote
	 * Below method will be use to get masterKeySemSapIdsMap from SFDC
	 * which we will further use to get masterKeySemSapIdsMap while passing studentList
	 * @param studentList
	 * */
	public Map<String,List<String>> getMasterKeySemSapIdsMapFromSFDC(List<StudentStudentPortalBean> studentList){
		Map<String, List<String>> masterKeySemSapIdsMapFromSFDC = new HashMap<>();
		if(studentList.size()>0) {
			masterKeySemSapIdsMapFromSFDC.putAll(studentList.stream()
				        .collect(Collectors.groupingBy(
				            student -> student.getConsumerProgramStructureId() + "/" + student.getSem(),
				            Collectors.mapping(StudentStudentPortalBean::getSapid, Collectors.toList()))
				        ));
		}

//		List<String> idToBeRemoved = new ArrayList<>();
//		
//		if(masterKeySemSapIdsMapFromSFDC.size()>0) {
//			masterKeySemSapIdsMapFromSFDC.keySet().stream().forEach(key->{
//				if(key.substring(0,key.indexOf("/")).equalsIgnoreCase("151")&&!key.substring(key.indexOf("/")+1,key.length()).equalsIgnoreCase("5")) {
//					List<String> masterKeySemIds = masterKeySemSapIdsMapFromSFDC.keySet().stream()
//						    .filter(masterKeySem -> 
//						        masterKeySem.indexOf("111") != -1 && 
//						        masterKeySem.indexOf(key.substring(key.indexOf("/") + 1, key.length())) != -1
//						    )
//						    .collect(Collectors.toList());
//					masterKeySemSapIdsMapFromSFDC.get(masterKeySemIds.get(0)).addAll(masterKeySemSapIdsMapFromSFDC.get(key));
//					idToBeRemoved.add(key);
//				}
//			});
//		}
//		masterKeySemSapIdsMapFromSFDC.keySet().removeIf(key->idToBeRemoved.contains(key));
		return masterKeySemSapIdsMapFromSFDC;
		
	}
	
	/**@implNote
	 * Below method will be used to set Sem in studentList 
	 * which we will further use to convert studentList to studentMap,masterKeySemSapIdsMap
	 * @param studentList from students table
	 * @param sapIdSemMap which we got during SFDC call
	 * */
	public void setSemInStudentList(List<StudentStudentPortalBean> studentList,Map<String,String> sapIdSemMapFromSFDC) {
		if(studentList.size()>0&&sapIdSemMapFromSFDC.size()>0) {
			studentList.stream().forEach(student->{
				student.setSem(sapIdSemMapFromSFDC.get(student.getSapid()));
			});
		}
	}
	
	/**@implNote
	 * Below method will be used to get Student Details map
	 * which will be further use to get student first name,last name,master key,imageUrl
	 * @param sapIdList which we got from SFDC while passing month,year
	 * */
	public Map<String,Object> getStudentDetailsMap(List<String> sapIdListFromSFDC){
		Map<String,Object> studentDetailsMap = new HashMap<>();
		if(sapIdListFromSFDC.size()>0) {
			Map<String,Object> map = leaderBoardDAO.getStudentDetailsUsingSapIds(sapIdListFromSFDC);
			if(map.size()>0) {
				studentDetailsMap.putAll(map);
			}
		}
		return studentDetailsMap;
	}

	/**@implNote
	 * Below method will be used to get MasterKey Sem Subject Count Map
	 * Which will be further use during rank calculation logic
	 * */
	public Map<String,Integer> getMasterKeySemSubjectCountMap(){
		Map<String,Integer> masterKeySemSubjectCountMap = new HashMap<>();
		masterKeySemSubjectCountMap.put("111/1", 5);
		masterKeySemSubjectCountMap.put("111/2", 5);
		masterKeySemSubjectCountMap.put("111/3", 5);
		masterKeySemSubjectCountMap.put("111/4", 5);
		masterKeySemSubjectCountMap.put("111/5", 1);
		
		masterKeySemSubjectCountMap.put("151/1", 5);
		masterKeySemSubjectCountMap.put("151/2", 5);
		masterKeySemSubjectCountMap.put("151/3", 5);
		masterKeySemSubjectCountMap.put("151/4", 5);
		masterKeySemSubjectCountMap.put("151/5", 3);
		
		masterKeySemSubjectCountMap.put("160/1", 4);
		masterKeySemSubjectCountMap.put("160/2", 4);
		masterKeySemSubjectCountMap.put("160/3", 4);
		masterKeySemSubjectCountMap.put("160/4", 5);
		masterKeySemSubjectCountMap.put("160/5", 5);
		return masterKeySemSubjectCountMap;
	}
}
