package com.nmims.services;

import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.ConsumerProgramStructureAcads;
import com.nmims.beans.FacultyAcadsBean;
import com.nmims.beans.ProgramSubjectMappingAcadsBean;
import com.nmims.beans.SessionDayTimeAcadsBean;
import com.nmims.beans.StudentAcadsBean;
import com.nmims.beans.UserAuthorizationBean;
import com.nmims.daos.TimeTableDAO;


@Service
public class TimeTableService {

	@Autowired
	TimeTableDAO tDao;
	
	@Autowired
	StudentService studentService;
	
	@Value( "${CURRENT_ACAD_MONTH}" )
	private String CURRENT_ACAD_MONTH;

	@Value( "${CURRENT_ACAD_YEAR}" )
	private String CURRENT_ACAD_YEAR;
	
	@Value("${TIMEBOUND_PORTAL_LIST}")
	private List<String> TIMEBOUND_PORTAL_LIST;
	

	@Autowired
	StudentCourseMappingService studentCourseMappingService;
	
	private ArrayList<ProgramSubjectMappingAcadsBean> programSubjectMappingList = null;
	
	private static final String  nofilter = "noFilter";
	
	private static final String  noTrack = "noTrack";
	
	public ArrayList<ProgramSubjectMappingAcadsBean> getProgramSubjectMappingList(){
		if(this.programSubjectMappingList == null || this.programSubjectMappingList.size() == 0){
			this.programSubjectMappingList = tDao.getProgramSubjectMappingList();
		}
		return programSubjectMappingList;
	}
	
	private ArrayList<String> getSubjectsForStudent(StudentAcadsBean student) {
		ArrayList<ProgramSubjectMappingAcadsBean> programSubjectMappingList = getProgramSubjectMappingList();
		ArrayList<String> subjects = new ArrayList<>();
		for (int i = 0; i < programSubjectMappingList.size(); i++) {
			ProgramSubjectMappingAcadsBean bean = programSubjectMappingList.get(i);	
				if(
					bean.getPrgmStructApplicable().equals(student.getPrgmStructApplicable()) 
					&& bean.getProgram().equals(student.getProgram())
					&& bean.getSem().equals(student.getSem())
					&& !student.getWaivedOffSubjects().contains(bean.getSubject())//Subjects has not already cleared it
					){
				//Added temporary for PD - WM project lecture
				if (student.getProgram().equalsIgnoreCase("PD - WM") && bean.getSubject().equalsIgnoreCase("Module 4 - Project")) {
					subjects.add("Project");
				}else {
					subjects.add(bean.getSubject());
				}
			}
		}
		return subjects;
	}
	
	public ArrayList<SessionDayTimeAcadsBean> getAllScheduledSessions(StudentAcadsBean student, String year, String month){
		
		ArrayList<SessionDayTimeAcadsBean> unapplicableScheduledSessionList = new ArrayList<SessionDayTimeAcadsBean>();
		ArrayList<SessionDayTimeAcadsBean> commonscheduledSessionList = new ArrayList<SessionDayTimeAcadsBean>();
		ArrayList<SessionDayTimeAcadsBean> allSessionsByCourseMapping = new ArrayList<SessionDayTimeAcadsBean>();
		ArrayList<SessionDayTimeAcadsBean> scheduledSessionList = new ArrayList<SessionDayTimeAcadsBean>();
		ArrayList<String> subjects = new ArrayList<String>();
		//ArrayList<String> subjects = getSubjectsForStudent(student);
		if (TIMEBOUND_PORTAL_LIST.contains(student.getConsumerProgramStructureId())) {
			subjects = getSubjectsForStudent(student);
		}else {
			subjects = studentCourseMappingService.getCurrentCycleSubjectsForSessions(student.getSapid(),month,year,student.getProgram());
		}
		
		//Remove WaiveOff Subject from applicable Subject list
		/*ArrayList<String> waivedOffSubjects = studentService.mgetWaivedOffSubjects(student);
		subjects.removeAll(waivedOffSubjects);*/
		
		if("EPBM".equalsIgnoreCase(student.getProgram()) || "MPDV".equalsIgnoreCase(student.getProgram())) {
			//Commented By Somesh as Added session configurable
			//scheduledSessionList.addAll(dao.getScheduledSessionForStudentsForExecutive(subjects,student,year,month));
			scheduledSessionList.addAll(tDao.getScheduledSessionForStudentsByCPSIdV2(student,year,month, subjects));
		}else {
			//Commented By Somesh as Added session configurable
			//scheduledSessionList.addAll(dao.getScheduledSessionForStudents(subjects,student,year,month));
			scheduledSessionList.addAll(tDao.getScheduledSessionForStudentsByCPSIdV2(student,year,month, subjects));
			
			allSessionsByCourseMapping = tDao.getAllSessionsByCourseMapping(student.getSapid());
			
			if (TIMEBOUND_PORTAL_LIST.contains(student.getConsumerProgramStructureId())) {
				//Get Common Sessions for MBA-wx
				commonscheduledSessionList = tDao.getCommonSessionsForMBAWx(student, year, month, "All");
				for (SessionDayTimeAcadsBean bean : commonscheduledSessionList) {
					String sessionIds="33139,33140,33141,33142,33143,33144";
					if (sessionIds.contains(bean.getId())) {
						bean.setSubject("Ascend: A Masterclass Series");
						bean.setSessionName("Topic 5");
					}
				}
				
			}else if(!"BBA".equalsIgnoreCase(student.getProgram()) && !"B.Com".equalsIgnoreCase(student.getProgram())){
				//Get Common Sessions for PG
				commonscheduledSessionList = (ArrayList<SessionDayTimeAcadsBean>)tDao.getCommonSessionsSemesterBased(student.getSem(),student.getProgram(),student.getConsumerProgramStructureId());
			}else {
				//Get Common Sessions for UG
				commonscheduledSessionList = (ArrayList<SessionDayTimeAcadsBean>)tDao.getCommonSessionsSemesterBasedForUG(student.getSem(),student.getProgram(),student.getConsumerProgramStructureId());
			}
		}
		
		if (allSessionsByCourseMapping != null && allSessionsByCourseMapping.size() > 0) {
			scheduledSessionList.addAll(allSessionsByCourseMapping);
		}
		
		if(commonscheduledSessionList != null && commonscheduledSessionList.size() > 0){
			scheduledSessionList.addAll(commonscheduledSessionList);
		}
		
		ArrayList<SessionDayTimeAcadsBean> eventSessionList = (ArrayList<SessionDayTimeAcadsBean>)tDao.getEventsRegisteredByStudent(student.getSapid());
		if(eventSessionList != null && eventSessionList.size() > 0){
			scheduledSessionList.addAll(eventSessionList);
		}
		
		if(scheduledSessionList != null && scheduledSessionList.size() != 0){
			if("EPBM".equalsIgnoreCase(student.getProgram()) || "MPDV".equalsIgnoreCase(student.getProgram())){
				for(SessionDayTimeAcadsBean sessionList : scheduledSessionList){
					if("Assignment Preparation Session".equalsIgnoreCase(sessionList.getSessionName())) {
						unapplicableScheduledSessionList.add(sessionList);
					}
				}
			}
			
			if (unapplicableScheduledSessionList.size() > 0) {
				scheduledSessionList.removeAll(unapplicableScheduledSessionList);
			}	
		}
		
		//Added for sorting
		if(scheduledSessionList.size() > 0) {
			Collections.sort(scheduledSessionList, new Comparator<SessionDayTimeAcadsBean>() {
				@Override
				public int compare(SessionDayTimeAcadsBean sBean1, SessionDayTimeAcadsBean sBean2) {
					return sBean1.getDate().compareTo(sBean2.getDate());
				}
			});
		}
		
		return scheduledSessionList;
	}
	
	public ArrayList<SessionDayTimeAcadsBean> getScheduledSessionsFromToday (StudentAcadsBean student, String year, String month){
		
		ArrayList<SessionDayTimeAcadsBean> sessionsFromToday = new ArrayList<SessionDayTimeAcadsBean>();
		try {
			//ArrayList<String> subjects = getSubjectsForStudent(student);
			ArrayList<String> subjects = studentCourseMappingService.getCurrentCycleSubjectsForSessions(student.getSapid(),month,year,student.getProgram());
			sessionsFromToday.addAll(tDao.getScheduledSessionForStudentsFromTodayNew(subjects,year,month));
			sessionsFromToday.addAll(tDao.getAllSessionsByCourseMapping(student.getSapid()));
		} catch (Exception e) {
			  
		}
		return sessionsFromToday;
	}
	
	public ArrayList<SessionDayTimeAcadsBean> getAllScheduledSessionsNew(StudentAcadsBean student, String year, String month, List<Integer> currentSemPSSId){
		
		ArrayList<SessionDayTimeAcadsBean> commonscheduledSessionList = new ArrayList<SessionDayTimeAcadsBean>();
		ArrayList<SessionDayTimeAcadsBean> allSessionsByCourseMapping = new ArrayList<SessionDayTimeAcadsBean>();
		ArrayList<SessionDayTimeAcadsBean> scheduledSessionList = new ArrayList<SessionDayTimeAcadsBean>();
			
		scheduledSessionList.addAll(tDao.getScheduledSessionForStudentsByCPSIdV3(student,year,month, currentSemPSSId));
			
		allSessionsByCourseMapping = tDao.getAllSessionsByCourseMapping(student.getSapid());
			
		if (TIMEBOUND_PORTAL_LIST.contains(student.getConsumerProgramStructureId())) {
			//Get Common Sessions for MBA-wx
			commonscheduledSessionList = tDao.getCommonSessionsForMBAWx(student, year, month, "All");
		}else if(!"BBA".equalsIgnoreCase(student.getProgram()) && !"B.Com".equalsIgnoreCase(student.getProgram())){
			//Get Common Sessions for PG
			commonscheduledSessionList = (ArrayList<SessionDayTimeAcadsBean>)tDao.getCommonSessionsSemesterBased(student.getSem(),student.getProgram(),student.getConsumerProgramStructureId());
		}else {
			//Get Common Sessions for UG
			commonscheduledSessionList = (ArrayList<SessionDayTimeAcadsBean>)tDao.getCommonSessionsSemesterBasedForUG(student.getSem(),student.getProgram(),student.getConsumerProgramStructureId());
		}
		
		
		if (allSessionsByCourseMapping != null && allSessionsByCourseMapping.size() > 0) {
			scheduledSessionList.addAll(allSessionsByCourseMapping);
		}
		
		if(commonscheduledSessionList != null && commonscheduledSessionList.size() > 0){
			scheduledSessionList.addAll(commonscheduledSessionList);
		}
		
		ArrayList<SessionDayTimeAcadsBean> eventSessionList = (ArrayList<SessionDayTimeAcadsBean>)tDao.getEventsRegisteredByStudent(student.getSapid());
		if(eventSessionList != null && eventSessionList.size() > 0){
			scheduledSessionList.addAll(eventSessionList);
		}
		
		//Added for sorting
		if(scheduledSessionList.size() > 0) {
			Collections.sort(scheduledSessionList, new Comparator<SessionDayTimeAcadsBean>() {
				@Override
				public int compare(SessionDayTimeAcadsBean sBean1, SessionDayTimeAcadsBean sBean2) {
					return sBean1.getDate().compareTo(sBean2.getDate());
				}
			});
		}
		
		return scheduledSessionList;
	}
	
	public ArrayList<SessionDayTimeAcadsBean> getAllScheduledSessionsForPG(String sapid, String year, String month, String consumerProgramStructureId, 
			String sem, List<Integer> currentSemPSSId){
		
		ArrayList<SessionDayTimeAcadsBean> commonscheduledSessionList = new ArrayList<SessionDayTimeAcadsBean>();
		ArrayList<SessionDayTimeAcadsBean> allSessionsByCourseMapping = new ArrayList<SessionDayTimeAcadsBean>();
		ArrayList<SessionDayTimeAcadsBean> scheduledSessionList = new ArrayList<SessionDayTimeAcadsBean>();
			
//		scheduledSessionList.addAll(tDao.getScheduledSessionForStudentsByCPSIdV3(student,year,month, currentSemPSSId));
		//Added new dao for sessions from Quick_session table
		scheduledSessionList.addAll(tDao.getAllScheduledSessionsFromQuickSessions(year, month, currentSemPSSId));
			
		//Commented by Somesh as Added new table for Common Sessions
		/*
		if ("MBA - WX".equalsIgnoreCase(student.getProgram())) {
			//Get Common Sessions for MBA-wx
			commonscheduledSessionList = tDao.getCommonSessionsForMBAWx(student, year, month, "All");
		}else if(!"BBA".equalsIgnoreCase(student.getProgram()) && !"B.Com".equalsIgnoreCase(student.getProgram())){
			//Get Common Sessions for PG
			commonscheduledSessionList = (ArrayList<SessionDayTimeBean>)tDao.getCommonSessionsSemesterBased(student.getSem(),student.getProgram(),student.getConsumerProgramStructureId());
		}else {
			//Get Common Sessions for UG
			commonscheduledSessionList = (ArrayList<SessionDayTimeBean>)tDao.getCommonSessionsSemesterBasedForUG(student.getSem(),student.getProgram(),student.getConsumerProgramStructureId());
		}*/
		
		commonscheduledSessionList = tDao.getAllCommonSessionsFromCommonQuickSessions(consumerProgramStructureId, year, month, sem);
		
		if (allSessionsByCourseMapping != null && allSessionsByCourseMapping.size() > 0) {
			scheduledSessionList.addAll(allSessionsByCourseMapping);
		}
		
		allSessionsByCourseMapping = tDao.getAllSessionsByCourseMapping(sapid);
		
		if(commonscheduledSessionList != null && commonscheduledSessionList.size() > 0){
			scheduledSessionList.addAll(commonscheduledSessionList);
		}
		
		ArrayList<SessionDayTimeAcadsBean> eventSessionList = (ArrayList<SessionDayTimeAcadsBean>)tDao.getEventsRegisteredByStudent(sapid);
		if(eventSessionList != null && eventSessionList.size() > 0){
			scheduledSessionList.addAll(eventSessionList);
		}
		
		//Added for sorting
		if(scheduledSessionList.size() > 0) {
			Collections.sort(scheduledSessionList, new Comparator<SessionDayTimeAcadsBean>() {
				@Override
				public int compare(SessionDayTimeAcadsBean sBean1, SessionDayTimeAcadsBean sBean2) {
					return sBean1.getDate().compareTo(sBean2.getDate());
				}
			});
		}
		
		return scheduledSessionList;
	}
	
	public StudentAcadsBean checkStudentRegistration(String sapId, StudentAcadsBean student) {
		
		StudentAcadsBean studentRegistrationData = new StudentAcadsBean();
		if("EPBM".equalsIgnoreCase(student.getProgram()) || "MPDV".equalsIgnoreCase(student.getProgram())) {
			studentRegistrationData = tDao.getStudentRegistrationDataForExecutive(sapId);			
		}else{
			studentRegistrationData = tDao.getStudentRegistrationDetails(sapId);
		}
		
		if ((!"111".equalsIgnoreCase(studentRegistrationData.getConsumerProgramStructureId()))
				&& (!"151".equalsIgnoreCase(studentRegistrationData.getConsumerProgramStructureId()))
				&& (!"160".equalsIgnoreCase(studentRegistrationData.getConsumerProgramStructureId()))
				&& (!"131".equalsIgnoreCase(studentRegistrationData.getConsumerProgramStructureId()))
				&& (!"17".equalsIgnoreCase(studentRegistrationData.getConsumerProgramStructureId()))
				&& (!"153".equalsIgnoreCase(studentRegistrationData.getConsumerProgramStructureId()))
				&& (!"150".equalsIgnoreCase(studentRegistrationData.getConsumerProgramStructureId()))
				&& (!"154".equalsIgnoreCase(studentRegistrationData.getConsumerProgramStructureId()))
				&& (!"155".equalsIgnoreCase(studentRegistrationData.getConsumerProgramStructureId()))
				&& (!"156".equalsIgnoreCase(studentRegistrationData.getConsumerProgramStructureId()))
				&& (!"157".equalsIgnoreCase(studentRegistrationData.getConsumerProgramStructureId()))
				&& (!"158".equalsIgnoreCase(studentRegistrationData.getConsumerProgramStructureId()))) {
			String month_curr = "";
			String month_reg = "";
			if(studentRegistrationData.getMonth().equals("Jan"))
				month_reg = "JANUARY";
			else
				month_reg = "JULY";
			
			if(CURRENT_ACAD_MONTH.equals("Jan")) {
				month_curr = "JANUARY";}
			else 
				month_curr = "JULY";
			
			Month reg_m = Month.valueOf(month_reg);	
			Month curr_m = Month.valueOf(month_curr);
			
			YearMonth reg_date = YearMonth.of(Integer.parseInt(studentRegistrationData.getYear()) ,reg_m);
			YearMonth curr_date = YearMonth.of(Integer.parseInt(CURRENT_ACAD_YEAR) ,curr_m);
			
			if(reg_date.compareTo(curr_date) < 0) {
				studentRegistrationData = null;
			}
		}
		return studentRegistrationData;
	}
	
	public List<SessionDayTimeAcadsBean> getScheduledSessionPageNew(SessionDayTimeAcadsBean searchBean,Map<String, String> getSubjectCodeIdList) {
		
		List<SessionDayTimeAcadsBean> allSessions = tDao.getAllsessionsDetails();
		Set<String> set = new HashSet<>();
		HashMap<String,Integer> subjectcodes = tDao.getSubjectCodeDetails();
		allSessions.forEach(f -> f.setSubjectCodeId(subjectcodes.get(f.getId())));
		allSessions.forEach(f -> f.setSubjectCode(getSubjectCodeIdList.get(String.valueOf(f.getSubjectCodeId()))));
		return allSessions;
	}
	
	public List<SessionDayTimeAcadsBean> getScheduledSessionPageFilterByTrack(List<SessionDayTimeAcadsBean> allsessionList,String track) {
		if(!StringUtils.isBlank(track)) {
			if(track.equalsIgnoreCase(noTrack)) 
				allsessionList = allsessionList.stream().filter(e -> StringUtils.isBlank(e.getTrack())).collect(Collectors.toList());
			else 
				allsessionList = allsessionList.stream().filter(e -> !StringUtils.isBlank(e.getTrack())).filter(e -> track.equals(e.getTrack())).collect(Collectors.toList());
		}
		return allsessionList;
	}
	
	public Set<String> getTrackList(List<SessionDayTimeAcadsBean> allSessions) {
		Set<String> track = allSessions.stream().map(SessionDayTimeAcadsBean::getTrack).filter(a -> !StringUtils.isBlank(a)).collect(Collectors.toSet());
		return track;
	}
	
	public Set<ConsumerProgramStructureAcads> getAllSubjectList(List<SessionDayTimeAcadsBean> allSessions) {
		Set<ConsumerProgramStructureAcads> allSubjects = allSessions.stream().map(f -> new ConsumerProgramStructureAcads(String.valueOf(f.getSubjectCodeId()),f.getSubjectCode(),f.getSubject())).collect(Collectors.toSet());
		return allSubjects;
	}
	
	public List<SessionDayTimeAcadsBean> getScheduledSessionPageFilterBySubjectCodeId(List<SessionDayTimeAcadsBean> allsessionList,Integer subjectCodeId) {
		if(!(subjectCodeId == null)) {
			allsessionList = allsessionList.stream().filter(e -> e.getSubjectCodeId() != null).filter(e -> subjectCodeId.equals(e.getSubjectCodeId())).collect(Collectors.toList());
		}
		return allsessionList;
	}
	
	public List<SessionDayTimeAcadsBean> getScheduledSessionPageFilterBySem(List<SessionDayTimeAcadsBean> allsessionList,String sem) {
		if(!StringUtils.isBlank(sem)) {
			List<Integer> subjectcodeIds = tDao.getSubjectCodeIdBySem(sem);
			allsessionList = allsessionList.stream().filter(e -> e.getSubjectCodeId() != null).filter(e -> subjectcodeIds.contains(e.getSubjectCodeId())).collect(Collectors.toList());   
		}
		return allsessionList;
	}
	
	public List<SessionDayTimeAcadsBean> getScheduledSessionPageFilterByProgramId(List<SessionDayTimeAcadsBean> allsessionList,String programId) {
		if(!StringUtils.isBlank(programId)) {
			List<Integer> masterkeys =  tDao.getMasterKeyByProgramId(programId);
			List<Integer> subjectCodeIds = tDao.getSubjectCodeIdByMasterkey(masterkeys);
			allsessionList = allsessionList.stream().filter(e -> e.getSubjectCodeId() != null).filter(e -> subjectCodeIds.contains(e.getSubjectCodeId())).collect(Collectors.toList()); 	
		}
		return allsessionList;
	}
	
}
