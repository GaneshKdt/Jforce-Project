package com.nmims.services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.nmims.beans.ConsumerProgramStructureAcads;
import com.nmims.beans.FacultyAcadsBean;
import com.nmims.beans.PageAcads;
import com.nmims.beans.ProgramBean;
import com.nmims.beans.ProgramSubjectMappingAcadsBean;
import com.nmims.beans.SessionDayTimeAcadsBean;
import com.nmims.beans.VideoContentAcadsBean;
import com.nmims.daos.FacultyDAO;
import com.nmims.daos.TimeTableDAO;
import com.nmims.daos.VideoContentDAO;
import com.nmims.util.ContentUtil;

@Service("schedulingService")
public class SchedulingService {

	@Autowired
	TimeTableDAO timeTableDAO;
	
	@Autowired
	FacultyDAO facultyDAO;
	
	@Autowired(required = false)
	ApplicationContext act;
	
	public static final String DEFAULT_FORMAT = "yyyy-MM-dd";
	public static final String HISTORY_DATE = "2020-07-01";
	
	private ArrayList<String> sessionTypeList = null;
    private Map<String, String> sessionTypesMap = null;
	private ArrayList<String> subjectList = null;
	private ArrayList<String> locationList = null;
	private Map<String, String> subjectCodeMap = null;
	private ArrayList<ConsumerProgramStructureAcads> consumerTypesList = null;
	private HashMap<String, VideoContentAcadsBean> mapOfSessionIdAndVideoContentRecord = null;

	
	public HashMap<String, FacultyAcadsBean> mapOfFacultyIdAndFacultyDetails() {
		ArrayList<FacultyAcadsBean> listOfAllFaculties = facultyDAO.getAllFacultyRecords();
		HashMap<String, FacultyAcadsBean> mapOfFacultyIdAndFacultyRecord = new HashMap<String, FacultyAcadsBean>();
			for (FacultyAcadsBean faculty : listOfAllFaculties) {
				mapOfFacultyIdAndFacultyRecord.put(faculty.getFacultyId(), faculty);
			}
		return mapOfFacultyIdAndFacultyRecord;
	}
	
	public HashMap<String, VideoContentAcadsBean> getMapOfSessionIdAndVideoContentRecord() {
		VideoContentDAO dao = (VideoContentDAO) act.getBean("videoContentDAO");
		List<VideoContentAcadsBean> listOfAllVideoContent = dao.getAllVideoContentList();
//		if (this.mapOfSessionIdAndVideoContentRecord == null) {
			this.mapOfSessionIdAndVideoContentRecord = new HashMap<String, VideoContentAcadsBean>();
			for (VideoContentAcadsBean video : listOfAllVideoContent) {
				this.mapOfSessionIdAndVideoContentRecord.put(String.valueOf(video.getSessionId()), video);
			}
//		}
		return mapOfSessionIdAndVideoContentRecord;
	}
	
	public Map<String, String> getSessionTypesMap() {
		if (this.sessionTypesMap == null) {
			this.sessionTypesMap = timeTableDAO.getSessionTypesMap();
		}
		return sessionTypesMap;
	}
	
	public ArrayList<String> getSessionTypeList() {
		if (this.sessionTypeList == null) {
			this.sessionTypeList = timeTableDAO.getAllSessionTypes();
		}
		return sessionTypeList;
	}
	
	public ArrayList<String> getLocationList() {
		if (this.locationList == null) {
			this.locationList = timeTableDAO.getAllLocations();
		}
		return locationList;
	}
	
	public Map<String, String> getsubjectCodeMap() {
		if (this.subjectCodeMap == null) {
			this.subjectCodeMap = timeTableDAO.getsubjectCodeMap();
		}
		return subjectCodeMap;
	}
	
	public ArrayList<ConsumerProgramStructureAcads> getConsumerTypesList() {
		if (this.consumerTypesList == null) {
			this.consumerTypesList = timeTableDAO.getConsumerTypes();
		}
		return consumerTypesList;
	}
	
	public ArrayList<String> getSubjectList() {
		if (this.subjectList == null) {
			this.subjectList = timeTableDAO.getAllSubjects();
		}
		return subjectList;
	}
	
	public ArrayList<ProgramSubjectMappingAcadsBean> getSubjectProgramList() {
		ArrayList<ProgramSubjectMappingAcadsBean> subjectProgramList = timeTableDAO.getSubjectProgramList();
		return subjectProgramList;
	}
	
	public PageAcads<SessionDayTimeAcadsBean> getSessionDetailsPage(int pageNo, int pageSize, SessionDayTimeAcadsBean searchBean, String searchType) throws ParseException{
		
		//New Logic for session Configurable start
		if(searchBean.getConsumerProgramStructureId() == null) {
			ArrayList<String> consumerProgramStructureIds =new ArrayList<String>();
			if(!StringUtils.isBlank(searchBean.getProgramId()) && !StringUtils.isBlank(searchBean.getProgramStructureId()) && !StringUtils.isBlank(searchBean.getConsumerTypeId())){
				consumerProgramStructureIds = timeTableDAO.getconsumerProgramStructureIds(searchBean.getProgramId(),searchBean.getProgramStructureId(),searchBean.getConsumerTypeId());
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
		
		return getPage(pageNo, pageSize, searchBean, searchType);
	}
	
	public PageAcads<SessionDayTimeAcadsBean> getPage(int pageNo, int pageSize, SessionDayTimeAcadsBean searchBean, String searchType) throws ParseException{
		PageAcads<SessionDayTimeAcadsBean> page = new PageAcads<SessionDayTimeAcadsBean>();	
		String acadDateFormat = ContentUtil.prepareAcadDateFormat(searchBean.getMonth(), searchBean.getYear());
		SimpleDateFormat formatter = new SimpleDateFormat(DEFAULT_FORMAT);
		Date date = formatter.parse(acadDateFormat);
		Date historyDate = formatter.parse(HISTORY_DATE);
		
		if (date.compareTo(historyDate) >= 0){
			page = timeTableDAO.getScheduledSessionPage(pageNo, pageSize, searchBean, searchType);
		}else {
			page = timeTableDAO.getScheduledSessionPageFromHistory(pageNo, pageSize, searchBean, searchType);
		}
		return page;
	}
	
	public List<SessionDayTimeAcadsBean> getReportListOfScheduledSession(SessionDayTimeAcadsBean searchBean, String tempSearchType ) throws ParseException {
		List<SessionDayTimeAcadsBean> scheduledSessionList = new ArrayList<SessionDayTimeAcadsBean>();
		ArrayList<ProgramSubjectMappingAcadsBean> subjectProgramList = new ArrayList<ProgramSubjectMappingAcadsBean>();
		PageAcads<SessionDayTimeAcadsBean> page = new PageAcads<SessionDayTimeAcadsBean>();	
		
		page = getPage(1, Integer.MAX_VALUE, searchBean, tempSearchType);
		
		scheduledSessionList = page.getPageItems();
		if (scheduledSessionList != null) {
			
			ArrayList<SessionDayTimeAcadsBean> moduleIdandBatchList = timeTableDAO.getModuleIdAndBatchNameList();
			
			Map<String, String> moduleIdNBatchNameMap = moduleIdandBatchList.stream()
					 .collect(Collectors.toMap(SessionDayTimeAcadsBean::getId, SessionDayTimeAcadsBean::getBatchName,(x, y)-> x + ", " + y, LinkedHashMap::new));
			
			HashMap<String, VideoContentAcadsBean> mapOfSessionIdAndVideoContentRecord = getMapOfSessionIdAndVideoContentRecord();
			for (SessionDayTimeAcadsBean session : scheduledSessionList) {
				int group = 1;
				//Set Common session sem
				session.setSem("");
				
				//Set Batch Name
				if(!StringUtils.isBlank(session.getModuleId())) {
					session.setBatchName(moduleIdNBatchNameMap.get(session.getModuleId()));
				}else {
					session.setBatchName("");
				}
				
				//Old Logic commented by Somesh on 11-12-2020
				/*
				if ("Y".equalsIgnoreCase(session.getHasModuleId())) {
					ProgramSubjectMappingBean subjectProgramMappingForMBAWX = dao
							.getSubjectProgramListForMBAWX(session.getSubject(), session.getModuleId());
					session.setGroup1Sem(subjectProgramMappingForMBAWX.getSem());
					session.setGroup1Program(subjectProgramMappingForMBAWX.getPrgmStructApplicable() + " : "
							+ subjectProgramMappingForMBAWX.getProgram());
				} else {
					for (ProgramSubjectMappingBean programSubjectMappingBean : subjectProgramList) {
						if (session.getSubject().equals(programSubjectMappingBean.getSubject())) {
							if (group == 1) {
								session.setGroup1Sem(programSubjectMappingBean.getSem());
								session.setGroup1Program(programSubjectMappingBean.getPrgmStructApplicable() + ": "
										+ programSubjectMappingBean.getProgram());
								group++;
							} else if (group == 2) {
								session.setGroup2Sem(programSubjectMappingBean.getSem());
								session.setGroup2Program(programSubjectMappingBean.getPrgmStructApplicable() + ": "
										+ programSubjectMappingBean.getProgram());
								group++;
							} else if (group == 3) {
								session.setGroup3Sem(programSubjectMappingBean.getSem());
								session.setGroup3Program(programSubjectMappingBean.getPrgmStructApplicable() + ": "
										+ programSubjectMappingBean.getProgram());
								group++;
							} else if (group == 4) {
								session.setGroup4Sem(programSubjectMappingBean.getSem());
								session.setGroup4Program(programSubjectMappingBean.getPrgmStructApplicable() + ": "
										+ programSubjectMappingBean.getProgram());
								group++;
								break;
							}
						}
					}
				}
				*/
				
				Date vdate = new Date();
				Date sdate = new SimpleDateFormat(DEFAULT_FORMAT).parse(session.getDate());
				if (vdate.after(sdate)) {
					VideoContentAcadsBean videoBean = mapOfSessionIdAndVideoContentRecord.get(session.getId());
					if (videoBean != null) {
						session.setVideoLink(videoBean.getVideoLink());
						session.setSessionDuration(videoBean.getDuration());
					}
				}
				
				String acadDateFormat = ContentUtil.prepareAcadDateFormat(searchBean.getMonth(), searchBean.getYear());
				SimpleDateFormat formatter = new SimpleDateFormat(DEFAULT_FORMAT);
				Date date = formatter.parse(acadDateFormat);
				Date historyDate = formatter.parse(HISTORY_DATE);
				
				if (date.compareTo(historyDate) >= 0){
					subjectProgramList = timeTableDAO.getSubjectProgramListBySessionId(session.getId());
				}else {
					subjectProgramList = timeTableDAO.getSubjectProgramListBySessionIdFromHistory(session.getId());
				}

				for (ProgramSubjectMappingAcadsBean programSubjectMappingBean : subjectProgramList) {
					if (group == 1) {
						session.setGroup1Sem(programSubjectMappingBean.getSem());
						session.setGroup1Program(programSubjectMappingBean.getPrgmStructApplicable() + ": "
								+ programSubjectMappingBean.getProgram());
						group++;
					} else if (group == 2) {
						session.setGroup2Sem(programSubjectMappingBean.getSem());
						session.setGroup2Program(programSubjectMappingBean.getPrgmStructApplicable() + ": "
								+ programSubjectMappingBean.getProgram());
						group++;
					} else if (group == 3) {
						session.setGroup3Sem(programSubjectMappingBean.getSem());
						session.setGroup3Program(programSubjectMappingBean.getPrgmStructApplicable() + ": "
								+ programSubjectMappingBean.getProgram());
						group++;
					} else if (group == 4) {
						session.setGroup4Sem(programSubjectMappingBean.getSem());
						session.setGroup4Program(programSubjectMappingBean.getPrgmStructApplicable() + ": "
								+ programSubjectMappingBean.getProgram());
						group++;
					} else if (group == 5) {
						session.setGroup5Sem(programSubjectMappingBean.getSem());
						session.setGroup5Program(programSubjectMappingBean.getPrgmStructApplicable() + ": "
								+ programSubjectMappingBean.getProgram());
						group++;
					}else if (group == 6) {
						session.setGroup6Sem(programSubjectMappingBean.getSem());
						session.setGroup6Program(programSubjectMappingBean.getPrgmStructApplicable() + ": "
								+ programSubjectMappingBean.getProgram());
						group++;
						break;
					}
				}
			}
		}
		return scheduledSessionList;
	}
	
	//get ProgramName and ProgramCode Mapped 
	public LinkedHashMap<String, String> getProgramNameAndCodeMap(String consumerType){
				
		ArrayList<String> programIdListByConsumertypeId = timeTableDAO.getProgramIdListByConsumertypeId(consumerType);
		ArrayList<ProgramBean> programList = timeTableDAO.getProgramListByProgramId(programIdListByConsumertypeId);
		
		Map<String, String> programNameNCodeMap = programList.stream()
				 .collect(Collectors.toMap(ProgramBean::getName,ProgramBean::getCode,(x, y)-> x + ", " + y,LinkedHashMap::new));	
		 
		return (LinkedHashMap<String, String>) programNameNCodeMap;
	}
}
