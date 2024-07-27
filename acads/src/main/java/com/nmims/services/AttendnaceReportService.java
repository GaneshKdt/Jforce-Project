package com.nmims.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.nmims.beans.ParticipantReportBean;
import com.nmims.beans.SessionDayTimeAcadsBean;
import com.nmims.daos.FacultyDAO;
import com.nmims.daos.ReportsDAO;
import com.nmims.helpers.ZoomManager;
import com.nmims.listeners.SessionRecordingScheduler;
import com.nmims.util.ContentUtil;

@Service
public class AttendnaceReportService {
	
	@Autowired
	ZoomManager zoomManager;
	
	
	@Autowired
	ReportsDAO rDao;
	
	@Autowired
	FacultyDAO fDao;
	
	private static final Logger sessionAttendance = LoggerFactory.getLogger("sessionAttendanceReport");
	
	
	public List<ParticipantReportBean> getTodaysWebinarIdsForReport(String startDate, String endDate){
		List<ParticipantReportBean> participantsReportBeanList=new ArrayList<ParticipantReportBean>();
		try {
		participantsReportBeanList=rDao.getTodaysWebinarIdsForReport(startDate, endDate);
		}catch (Exception e) {
			sessionAttendance.error("Error while fetching getTodaysWebinarIdsForReport() : "+e.getStackTrace());
		}
		return participantsReportBeanList;
	}
	
	public List<ParticipantReportBean> getTodaysWebinarIdsForBatchJob(){
		List<ParticipantReportBean> participantsReportBeanList=new ArrayList<ParticipantReportBean>();
		try {
		participantsReportBeanList=rDao.getTodaysWebinarIdsForBatchJob();
		}catch (Exception e) {
			sessionAttendance.error("Error while fetching getTodaysWebinarIdsForReport() : "+e.getStackTrace());
		}
		return participantsReportBeanList;
	}
	
	public List<ParticipantReportBean> getParticipantsReportsDetailsByWebinarIdFromZoom(String webinarId, String sessionType){
		List<ParticipantReportBean> returnList=new ArrayList<ParticipantReportBean>();
		if ("1".equals(sessionType)) {
			returnList=zoomManager.getWebinarParticipantsDetails(webinarId);
		}else if("2".equals(sessionType)){
			returnList=zoomManager.getMeetingParticipantsDetails(webinarId);
		}
		sessionAttendance.info("Participants list size for meetingkey : "+webinarId+" : "+returnList.size());
		for(ParticipantReportBean bean: returnList) {
			String DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
            SimpleDateFormat newFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN);
            DateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date joinTime;
            Date leaveTime;
			try {
				joinTime = utcFormat.parse(bean.getJoin_time());
				 String joinTimeTemp = newFormat.format(joinTime);
				 joinTimeTemp=joinTimeTemp.replaceAll("T", " ");
				 joinTimeTemp=joinTimeTemp.substring(0, 19);
		         bean.setJoin_time(joinTimeTemp);
		         
		         leaveTime=utcFormat.parse(bean.getLeave_time());
		         String leaveTimeTemp = newFormat.format(leaveTime);
		         leaveTimeTemp=leaveTimeTemp.replaceAll("T", " ");
		         leaveTimeTemp=leaveTimeTemp.substring(0, 19);
		         bean.setLeave_time(leaveTimeTemp);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				sessionAttendance.error("Error in getParticipantsReportsDetailsByWebinarIdFromZoom() : "+e.getStackTrace());	
			}
            
		}
		return returnList;
	}
	
	public String getSapIdForParticpants(String emailId) {
		String sapId="NA";
		try {
			sapId=rDao.getsapIdforWebinarParticipant(emailId);
			return sapId;
		}catch (Exception e) {
			sessionAttendance.error("Error in getSapIdForParticpants() : "+e.getStackTrace());
		}
		return sapId;
	}
	
	public void insertParticipantReport(ParticipantReportBean bean, String webinarId, String sessionId,String sessionDate) {
		rDao.insertParticipantReport(bean, webinarId, sessionId,sessionDate);
	}
	
	public void insertparticipantsLogs(String webinarId, int participants_count, int inserted_count) {
		rDao.insertparticipantsLogs(webinarId, participants_count, inserted_count);
	}
	
	public HashMap<String, Float> getTotalDurationHashMap(SessionDayTimeAcadsBean searchBean) {
		HashMap<String, Float> getMapOfTotalDurationInSession=new HashMap<String, Float>();
		List<ParticipantReportBean> list=new ArrayList<ParticipantReportBean>();
		String month=searchBean.getMonth();
		if("Oct".equalsIgnoreCase(searchBean.getMonth()) || "Apr".equalsIgnoreCase(searchBean.getMonth())) {
		month=searchBean.getMonth().replace("Apr", "Jan");
		month=searchBean.getMonth().replace("Oct", "Jul");
		}
		
		String acadDateFormat=ContentUtil.prepareAcadDateFormat(month, searchBean.getYear());
		
		try {
		list=rDao.getTotalDuration(acadDateFormat, searchBean);
		}catch (Exception e) {
			sessionAttendance.error("Error in getTotalDurationHashMap() : "+e.getStackTrace());
		}
		for (ParticipantReportBean participantReportBean : list) {
			getMapOfTotalDurationInSession.put(participantReportBean.getSapId()+"-"+participantReportBean.getWebinarId() , participantReportBean.getTotalDuration()/60);
		}
		return getMapOfTotalDurationInSession;
	}
	
	public List<ParticipantReportBean> getReportDetails(SessionDayTimeAcadsBean searchBean){
		List<ParticipantReportBean> attendanceList=new ArrayList<ParticipantReportBean>();
		sessionAttendance.info("Report fetching for :"+searchBean.getYear()+" : "+searchBean.getMonth());
		try {
			attendanceList=rDao.getSessionReportDetailsBySearchFilters(searchBean);
			sessionAttendance.info("getReportDetails() : attendanceList size():"+attendanceList.size());
			HashMap<String, Float> mapOfTotalDurationInSession=getTotalDurationHashMap(searchBean);
			for(ParticipantReportBean bean: attendanceList) {
				if(mapOfTotalDurationInSession.containsKey(bean.getSapId()+"-"+bean.getWebinarId())) {
				Float total = mapOfTotalDurationInSession.get(bean.getSapId()+"-"+bean.getWebinarId());
				BigDecimal totalDuration = new BigDecimal(total).setScale(3, RoundingMode.HALF_UP);
				bean.setTotalDuration(totalDuration.floatValue());
				}
			}
		}catch (Exception e) {
			sessionAttendance.error("Error in getReportDetails() : "+e.getStackTrace());
		}
		return attendanceList;
		
	}
	
	public Map<String, String> getFacultyMap(){
		Map<String, String> facultyIdMap=new HashMap<String, String>();
		try {
			facultyIdMap = fDao.getFacultyMap();
		}catch (Exception e) {
			sessionAttendance.error("Error in getFacultyMap() : "+e.getStackTrace());
		}
		return facultyIdMap;
		
	}
	
	public List<ParticipantReportBean> getMapOfSessionReportDetailsForSearhFilters(SessionDayTimeAcadsBean searchBean) {
		List<ParticipantReportBean> returnList=new ArrayList<ParticipantReportBean>();
		try {
		returnList=rDao.getSessionReportDetailsBySearchFilters(searchBean);
		}catch (Exception e) {
			sessionAttendance.error("Error in getMapOfSessionReportDetailsForSearhFilters() : "+e.getStackTrace());
		}
		return returnList;
	}
	
	public void fetchSessionAttendnaceFromZoomAndUpdateToDB(String startDate,String endDate){
		String sapId = null;
		List<ParticipantReportBean> webinarIdList = getTodaysWebinarIdsForReport(startDate, endDate);
		sessionAttendance.info("Todays WebinarIds List Size: " + webinarIdList.size());
		for (ParticipantReportBean bean : webinarIdList) {
			try {
				List<ParticipantReportBean> participantsReportBeanList = getParticipantsReportsDetailsByWebinarIdFromZoom(bean.getWebinarId(), bean.getSessionType());
				sessionAttendance.info("Paticipants details for webinarId from Zoom Api : " + participantsReportBeanList.size());
				String acadDateFormat = ContentUtil.prepareAcadDateFormat(bean.getMonth(), bean.getYear());
				int participants_count = participantsReportBeanList.size();

				int inserted_count = 0;
				for (ParticipantReportBean participantBean : participantsReportBeanList) {
					sapId = getSapIdForParticpants(participantBean.getUser_email());
					participantBean.setSapId(sapId);
					participantBean.setSubjectCodeId(bean.getSubjectCodeId());
					participantBean.setAcadDateFormat(acadDateFormat);
					try {
					insertParticipantReport(participantBean, bean.getWebinarId(),bean.getSessionId(),bean.getSessionDate());
					sessionAttendance.info("Participants entry has been inserted in  database for sapiId : "+participantBean.getSapId());
					inserted_count++;
					}catch (Exception e) {
						sessionAttendance.error("Error while inserting :"+participantBean.getUser_email() +" entry for :"+bean.getSessionId()+" Error message : "+e.getMessage());
					}
				}
				insertparticipantsLogs(bean.getWebinarId(), participants_count, inserted_count);
			} catch (Exception e) {
				sessionAttendance.error("Error while inserting report Details fo MeetingKey: " + bean.getWebinarId()
						+ " Error message : " + e.getMessage());
				continue;
			}
		}
	}
	
	public void fetchSessionAttendnaceFromZoomAndUpdateToDBBatchJob(){
		String sapId = null;
		List<ParticipantReportBean> webinarIdList = getTodaysWebinarIdsForBatchJob();
		sessionAttendance.info("Todays WebinarIds List Size: " + webinarIdList.size());
		for (ParticipantReportBean bean : webinarIdList) {
			try {
				List<ParticipantReportBean> participantsReportBeanList = getParticipantsReportsDetailsByWebinarIdFromZoom(bean.getWebinarId(), bean.getSessionType());
				sessionAttendance.info("Paticipants details for webinarId from Zoom Api : " + participantsReportBeanList.size());
				String acadDateFormat = ContentUtil.prepareAcadDateFormat(bean.getMonth(), bean.getYear());
				int participants_count = participantsReportBeanList.size();

				int inserted_count = 0;
				for (ParticipantReportBean participantBean : participantsReportBeanList) {
					sapId = getSapIdForParticpants(participantBean.getUser_email());
					participantBean.setSapId(sapId);
					participantBean.setSubjectCodeId(bean.getSubjectCodeId());
					participantBean.setAcadDateFormat(acadDateFormat);
					try {
					insertParticipantReport(participantBean, bean.getWebinarId(),bean.getSessionId(),bean.getSessionDate());
					sessionAttendance.info("Participants entry has been inserted in  database for sapiId : "+participantBean.getSapId());
					inserted_count++;
					}catch (Exception e) {
						sessionAttendance.error("Error while inserting :"+participantBean.getUser_email() +" entry for :"+bean.getSessionId()+" Error message : "+e.getMessage());
					}
				}
				insertparticipantsLogs(bean.getWebinarId(), participants_count, inserted_count);
			} catch (Exception e) {
				sessionAttendance.error("Error while inserting report Details fo MeetingKey: " + bean.getWebinarId()
						+ " Error message : " + e.getMessage());
				continue;
			}
		}
	}
}
