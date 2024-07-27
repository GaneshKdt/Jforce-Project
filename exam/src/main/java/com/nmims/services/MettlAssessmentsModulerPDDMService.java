package com.nmims.services;

import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ibm.icu.text.SimpleDateFormat;
import com.nmims.beans.ExamsAssessmentsBean;
import com.nmims.beans.MettlRegisterCandidateBean;
import com.nmims.beans.MettlResponseBean;
import com.nmims.beans.MettlScheduleAPIBean;
import com.nmims.beans.MettlScheduleExamBean;
import com.nmims.beans.ScheduleCreationBean;
import com.nmims.daos.ExamsAssessmentsDAO;
import com.nmims.helpers.MettlHelper;
import com.nmims.interfaces.MettlAssessments;

@Service
public class MettlAssessmentsModulerPDDMService implements MettlAssessments{
	
	@Autowired
	@Qualifier("pddmMettlHelper")
	MettlHelper pddmMettlHelper;
	
	@Autowired
	ExamsAssessmentsDAO examDao;
	
	@Value("${PG_NO_IMAGE_URL}")
	private String PG_NO_IMAGE_URL;
	
	@Value("${REPORTING_START_TIME_DIFF}")
	private Integer REPORTING_START_TIME_DIFF; 
	
	@Value("${REPORTING_FINISH_TIME_DIFF_TIMEBOUND}")
	private Integer REPORTING_FINISH_TIME_DIFF_TIMEBOUND;
	
	@Value("${START_METTL_EXAM_WEBHOOK_MBAWX}")
	private String START_METTL_EXAM_WEBHOOK_MBAWX;
	
	@Value("${END_METTL_EXAM_WEBHOOK_MBAWX}")
	private String END_METTL_EXAM_WEBHOOK_MBAWX;
	
	@Value("${RESUME_METTL_EXAM_WEBHOOK_MBAWX}")
	private String RESUME_METTL_EXAM_WEBHOOK_MBAWX;
	
	@Value("${GRADED_METTL_EXAM_WEBHOOK_MBAWX}")
	private String GRADED_METTL_EXAM_WEBHOOK_MBAWX;
	
	
	public static final Logger logger = LoggerFactory.getLogger("examRegisterPG");
	
	@Override
	public ArrayList<MettlResponseBean> getAllAssessments() {
		ArrayList<MettlResponseBean> mettlAssessmentResponseBeanList = new ArrayList<MettlResponseBean>();
		JsonObject jsonResponse = pddmMettlHelper.getAssessments();
		if(jsonResponse != null) {
			String status = jsonResponse.get("status").getAsString();
			if("SUCCESS".equalsIgnoreCase(status)) {
				JsonArray assessmentList = jsonResponse.get("assessments").getAsJsonArray();
				for (JsonElement assessmentElement : assessmentList) {
					JsonObject assessmentObject = assessmentElement.getAsJsonObject();
					MettlResponseBean tmp_responseBean = new MettlResponseBean();
					tmp_responseBean.setAssessments_id(assessmentObject.get("id").getAsInt());
					tmp_responseBean.setName(assessmentObject.get("name").getAsString());
					tmp_responseBean.setCustomAssessmentName(assessmentObject.get("customAssessmentName").getAsString());
					tmp_responseBean.setMax_marks(""+assessmentObject.get("maxMarks").getAsInt());
					tmp_responseBean.setDuration(assessmentObject.get("duration").getAsInt());
					mettlAssessmentResponseBeanList.add(tmp_responseBean);
				}
				
			}
		}
		return mettlAssessmentResponseBeanList;
	}

	public String createSchedule(HttpServletRequest request,ScheduleCreationBean scheduleBean,String userId) throws Exception
	{
		String result="";
		String sourceAppName = "NGASCE";
		MettlScheduleExamBean dbBean = null;
		String examStartDateTime="";
		String examEndDateTime="";
		MettlScheduleExamBean queryBean = null;
		String endTimeForWindowAccess="";
		if(scheduleBean.getWaitingRoom().equalsIgnoreCase("enabled")) {
			queryBean = createApiParametersWithWaitingRoom(scheduleBean);
		}else if(scheduleBean.getWaitingRoom().equalsIgnoreCase("disabled")) {
			queryBean = createApiParametersWithoutWaitingRoom(scheduleBean);
		}
		
		if(!scheduleBean.getMax_score().equalsIgnoreCase("100"))
		{
			if(scheduleBean.getWaitingRoom().equalsIgnoreCase("enabled")) {
				dbBean = pddmMettlHelper.createScheduleWithWaitingRoom(queryBean, sourceAppName);
			}else if(scheduleBean.getWaitingRoom().equalsIgnoreCase("disabled")){
				dbBean = pddmMettlHelper.createSchedule(queryBean, sourceAppName);
			}
			
			if(dbBean.getStatus().equalsIgnoreCase("success"))
			{
				ExamsAssessmentsBean examBean = createDbBean(dbBean,scheduleBean,userId);
				examStartDateTime=scheduleBean.getSlotDate_id()+" "+queryBean.getStartTime();
				
				if(scheduleBean.getWaitingRoom().equalsIgnoreCase("disabled")) {
					examEndDateTime=extendExamEndDateTime(scheduleBean.getSlotDate_id(),queryBean.getEndTime(),scheduleBean.getAssessmentDuration());
				}
				else if(scheduleBean.getWaitingRoom().equalsIgnoreCase("enabled")){
					examEndDateTime=scheduleBean.getSlotDate_id()+" "+queryBean.getEndTime();
				}
				
				examBean.setExam_start_date_time(examStartDateTime);
				examBean.setExam_end_date_time(examEndDateTime);
				
				if(scheduleBean.getWaitingRoom().equalsIgnoreCase("enabled")) {
					examBean.setReporting_start_date_time(scheduleBean.getSlotDate_id()+" "+queryBean.getReportingStartTime());
					examBean.setReporting_finish_date_time(scheduleBean.getSlotDate_id()+" "+queryBean.getReportingFinishTime());
				}
				
				int exists=examDao.checkIfAssessmentExists(examBean.getAssessments_id());
				if(exists>0)
				{
					result=examDao.insertIntoExamScheduleAndAssessmentTimebound(examBean);
				}
				else if(exists==0)
				{
					examBean.setName(scheduleBean.getAssessmentName());
					examBean.setCustomAssessmentName(scheduleBean.getAssessmentCustomName());
					examBean.setProgramType(scheduleBean.getProgramType());
					result=examDao.insertIntoExamScheduleAndAssessmentAndAssessmentTimebound(examBean);
				}
				if(result.equalsIgnoreCase("success"))
				{
					endTimeForWindowAccess=scheduleBean.getSlotDate_id()+" "+queryBean.getEndTime2();
					result=registerCandidates(request,examBean,scheduleBean.getSubject_id(),endTimeForWindowAccess,false,false,null);
				}
			}
			else
			{
				result="Error while creating schedule for Assessment: "+scheduleBean.getAssessmentName()+" and subject: "+scheduleBean.getSubject_id()+" and Batch: "+scheduleBean.getBatchName();
			}
		}
		else if(scheduleBean.getMax_score().equalsIgnoreCase("100"))
		{
			examStartDateTime=scheduleBean.getSlotDate_id()+" "+queryBean.getStartTime();
			
			if(scheduleBean.getWaitingRoom().equalsIgnoreCase("disabled")) {
				examEndDateTime=extendExamEndDateTime(scheduleBean.getSlotDate_id(),queryBean.getEndTime(),scheduleBean.getAssessmentDuration());
			}
			else if(scheduleBean.getWaitingRoom().equalsIgnoreCase("enabled")){
				examEndDateTime=scheduleBean.getSlotDate_id()+" "+queryBean.getEndTime();
			}

			int exist=examDao.checkIfScheduleExist(scheduleBean.getAssessments_id(), examStartDateTime, examEndDateTime);
			if(exist>0)
			{
				ExamsAssessmentsBean examBean = examDao.getExistingScheduleDetails(scheduleBean.getAssessments_id(), examStartDateTime, examEndDateTime);
				examBean.setTimebound_id(scheduleBean.getTimeboundId());
				examBean.setName(scheduleBean.getAssessmentName());
				examBean.setBatchName(scheduleBean.getBatchName());
				examBean.setCreatedBy(userId);
				examBean.setLastModifiedBy(userId);
				examBean.setDuration(Integer.parseInt(scheduleBean.getAssessmentDuration()));
				
				int assessmentExists=examDao.checkIfAssessmentExists(scheduleBean.getAssessments_id());
				if(assessmentExists>0)
				{
					result=examDao.insertIntoExamScheduleAndAssessmentTimebound(examBean);
				}
				else if(assessmentExists==0)
				{
					examBean.setName(scheduleBean.getAssessmentName());
					examBean.setCustomAssessmentName(scheduleBean.getAssessmentCustomName());
					examBean.setProgramType(scheduleBean.getProgramType());
					result=examDao.insertIntoExamScheduleAndAssessmentAndAssessmentTimebound(examBean);
				}
				if(result.equalsIgnoreCase("success"))
				{
					int id = examDao.getExamScheduleId(examBean);
					examBean.setId(""+id);
					String exam_start_date_time = examBean.getExam_start_date_time();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date startDate = sdf.parse(exam_start_date_time);
					String exam_end_date_time = sdf.format(DateUtils.addMinutes(startDate, Integer.parseInt(scheduleBean.getAssessmentDuration())));
					examBean.setExam_end_date_time(exam_end_date_time);
					result=examDao.updateScheduleIdInTimeTableForRegistration(examBean);
					if(result.equalsIgnoreCase("success"))
					{
						examBean.setExam_end_date_time(examEndDateTime);
						endTimeForWindowAccess=scheduleBean.getSlotDate_id()+" "+queryBean.getEndTime2();
						result=registerCandidates(request,examBean,scheduleBean.getSubject_id(),endTimeForWindowAccess,true,false,null);
					}
				}
			}
			else if(exist==0)
			{
				if(scheduleBean.getWaitingRoom().equalsIgnoreCase("enabled")) {
					dbBean = pddmMettlHelper.createScheduleWithWaitingRoom(queryBean, sourceAppName);
				}else if(scheduleBean.getWaitingRoom().equalsIgnoreCase("disabled")){
					dbBean = pddmMettlHelper.createSchedule(queryBean, sourceAppName);
				}
				
				if(dbBean.getStatus().equalsIgnoreCase("success"))
				{
					ExamsAssessmentsBean examBean = createDbBean(dbBean,scheduleBean,userId);
					examBean.setExam_start_date_time(examStartDateTime);
					examBean.setExam_end_date_time(examEndDateTime);
					
					if(scheduleBean.getWaitingRoom().equalsIgnoreCase("enabled")) {
						examBean.setReporting_start_date_time(scheduleBean.getSlotDate_id()+" "+queryBean.getReportingStartTime());
						examBean.setReporting_finish_date_time(scheduleBean.getSlotDate_id()+" "+queryBean.getReportingFinishTime());
					}
					
					int assessmentExists=examDao.checkIfAssessmentExists(examBean.getAssessments_id());
					if(assessmentExists>0)
					{
						result=examDao.insertIntoExamScheduleAndAssessmentTimebound(examBean);
					}
					else if(assessmentExists==0)
					{
						examBean.setName(scheduleBean.getAssessmentName());
						examBean.setCustomAssessmentName(scheduleBean.getAssessmentCustomName());
						examBean.setProgramType(scheduleBean.getProgramType());
						result=examDao.insertIntoExamScheduleAndAssessmentAndAssessmentTimebound(examBean);
					}
					if(result.equalsIgnoreCase("success"))
					{
						int id = examDao.getExamScheduleId(examBean);
						examBean.setId(""+id);
						String exam_start_date_time = examBean.getExam_start_date_time();
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						Date startDate = sdf.parse(exam_start_date_time);
						String exam_end_date_time = sdf.format(DateUtils.addMinutes(startDate, Integer.parseInt(scheduleBean.getAssessmentDuration())));
						examBean.setExam_end_date_time(exam_end_date_time);
						result=examDao.updateScheduleIdInTimeTableForRegistration(examBean);
						if(result.equalsIgnoreCase("success"))
						{
							examBean.setExam_end_date_time(examEndDateTime);
							endTimeForWindowAccess=scheduleBean.getSlotDate_id()+" "+queryBean.getEndTime2();
							result=registerCandidates(request,examBean,scheduleBean.getSubject_id(),endTimeForWindowAccess,true,false,null);
						}
					}
				}
				else
				{
					result="Error while creating schedule for Assessment: "+scheduleBean.getAssessmentName()+" and subject: "+scheduleBean.getSubject_id()+" and Batch: "+scheduleBean.getBatchName();
				}
			}
		}
		
		logger.info("Result after schedule and registration on mettl is: "+result);
		return result;
	}

	public String extendExamEndDateTime(String slotDate,String endTime,String assessmentDuration) throws Exception
	{
		SimpleDateFormat sdf= new SimpleDateFormat("HH:mm:ss");
		Date endDateTime=sdf.parse(endTime);
		String endTimeExtended=sdf.format(DateUtils.addMinutes(endDateTime, Integer.parseInt(assessmentDuration)));
		return slotDate+" "+endTimeExtended;
	}
	
//	public void extendExamEndDateTimeWithouSchedule(ScheduleCreationBean scheduleBean,MettlScheduleExamBean mettlScheduleBean) throws Exception
//	{
//		int duration=Integer.parseInt(scheduleBean.getAssessmentDuration());
//		mettlScheduleBean.setStartTime(scheduleBean.getSlotTime_id()+":00");
//		SimpleDateFormat sdf3 = new SimpleDateFormat("HH:mm:ss");
//		Date startTime=sdf3.parse(mettlScheduleBean.getStartTime());
//		String endTime=sdf3.format(DateUtils.addMinutes(startTime, duration));
//		mettlScheduleBean.setEndTime(endTime);
//		mettlScheduleBean.setEndTime2(endTime);
//	}
	
	public MettlScheduleExamBean createApiParametersWithoutWaitingRoom(ScheduleCreationBean scheduleBean) throws Exception
	{
			MettlScheduleExamBean mettlScheduleBean = new MettlScheduleExamBean();
			mettlScheduleBean.setTestStartNotificationUrl(START_METTL_EXAM_WEBHOOK_MBAWX);
			mettlScheduleBean.setTestFinishNotificationUrl(END_METTL_EXAM_WEBHOOK_MBAWX);
			mettlScheduleBean.setTestResumeEnabledForExpiredTestURL(RESUME_METTL_EXAM_WEBHOOK_MBAWX);
			mettlScheduleBean.setTestGradedNotificationUrl(GRADED_METTL_EXAM_WEBHOOK_MBAWX);
			
			mettlScheduleBean.setAssessmentId(scheduleBean.getAssessments_id());	
			if(scheduleBean.getWebProctoring().equalsIgnoreCase("disabled"))
			{
				  mettlScheduleBean.setEnabledWP(Boolean.FALSE);
				  mettlScheduleBean.setShowRemainingCountsWP(Boolean.FALSE);
				  mettlScheduleBean.setCountWP(0);
			}
			mettlScheduleBean.setCandidateAuthorizationVP(Boolean.FALSE);
			
			mettlScheduleBean.setScheduleType(MettlScheduleAPIBean.FIXED);
			mettlScheduleBean.setFixedAccessOptionSW(MettlScheduleAPIBean.EXACTTIME);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date date = sdf.parse(scheduleBean.getSlotDate_id());
			SimpleDateFormat sdf2 = new SimpleDateFormat("E, dd MMM yyyy");
			String startsOnDate= sdf2.format(date);
			
			mettlScheduleBean.setStartsOnDate(startsOnDate);
			mettlScheduleBean.setModeVP("PHOTO");
			mettlScheduleBean.setCandidateScreenCaptureVP(Boolean.TRUE);
			
			String endTime="";
			int duration=Integer.parseInt(scheduleBean.getAccessDuration());
			mettlScheduleBean.setStartTime(scheduleBean.getSlotTime_id()+":00");
			SimpleDateFormat sdf3 = new SimpleDateFormat("HH:mm:ss");
			Date startTime=sdf3.parse(mettlScheduleBean.getStartTime());
			endTime=sdf3.format(DateUtils.addMinutes(startTime, duration));
			
			mettlScheduleBean.setEndTime(endTime);
			mettlScheduleBean.setEndTime2(endTime);
			
			String customUrlId=scheduleBean.getSubject_id()+"-"+scheduleBean.getAssessments_id()+"-"+scheduleBean.getTimeboundId()+"-"+scheduleBean.getSlotDate_id()+"-"+mettlScheduleBean.getStartTime();
			mettlScheduleBean.setCustomUrlId(customUrlId);
		
		return mettlScheduleBean;
	}
	
	public MettlScheduleExamBean createApiParametersWithWaitingRoom(ScheduleCreationBean scheduleBean) throws Exception
	{
			MettlScheduleExamBean mettlScheduleBean = new MettlScheduleExamBean();
			mettlScheduleBean.setTestStartNotificationUrl(START_METTL_EXAM_WEBHOOK_MBAWX);
			mettlScheduleBean.setTestFinishNotificationUrl(END_METTL_EXAM_WEBHOOK_MBAWX);
			mettlScheduleBean.setTestResumeEnabledForExpiredTestURL(RESUME_METTL_EXAM_WEBHOOK_MBAWX);
			mettlScheduleBean.setTestGradedNotificationUrl(GRADED_METTL_EXAM_WEBHOOK_MBAWX);
			
			mettlScheduleBean.setAssessmentId(scheduleBean.getAssessments_id());	
			if(scheduleBean.getWebProctoring().equalsIgnoreCase("disabled"))
			{
				  mettlScheduleBean.setEnabledWP(Boolean.FALSE);
				  mettlScheduleBean.setShowRemainingCountsWP(Boolean.FALSE);
				  mettlScheduleBean.setCountWP(0);
			}
			mettlScheduleBean.setCandidateAuthorizationVP(Boolean.FALSE);
			
			//added for waiting room
			mettlScheduleBean.setTestLinkType(MettlScheduleAPIBean.SCHEDULED);
			
			mettlScheduleBean.setScheduleType(MettlScheduleAPIBean.FIXED);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date date = sdf.parse(scheduleBean.getSlotDate_id());
			SimpleDateFormat sdf2 = new SimpleDateFormat("E, dd MMM yyyy");
			String startsOnDate= sdf2.format(date);
			
			mettlScheduleBean.setStartsOnDate(startsOnDate);
			mettlScheduleBean.setModeVP("VIDEO");
			mettlScheduleBean.setCandidateScreenCaptureVP(Boolean.TRUE);
			
			String endTime="";
			int duration=Integer.parseInt(scheduleBean.getAssessmentDuration());
			mettlScheduleBean.setStartTime(scheduleBean.getSlotTime_id()+":00");
			SimpleDateFormat sdf3 = new SimpleDateFormat("HH:mm:ss");
			Date startTime=sdf3.parse(mettlScheduleBean.getStartTime());
			endTime=sdf3.format(DateUtils.addMinutes(startTime, duration));
			
			mettlScheduleBean.setEndTime(endTime);
			mettlScheduleBean.setEndTime2(endTime);
			
			//for reporting start and end time
			String reportingStartTime = sdf3.format(DateUtils.addMinutes(startTime, REPORTING_START_TIME_DIFF));
			String reportingFinishTime = sdf3.format(DateUtils.addMinutes(startTime, REPORTING_FINISH_TIME_DIFF_TIMEBOUND));
							
			mettlScheduleBean.setReportingStartTime(reportingStartTime);
			mettlScheduleBean.setReportingFinishTime(reportingFinishTime);
					
			
			String customUrlId=scheduleBean.getSubject_id()+"-"+scheduleBean.getAssessments_id()+"-"+scheduleBean.getTimeboundId()+"-"+scheduleBean.getSlotDate_id()+"-"+mettlScheduleBean.getStartTime();
			mettlScheduleBean.setCustomUrlId(customUrlId);
			
		return mettlScheduleBean;
	}
	
	public ExamsAssessmentsBean createDbBean(MettlScheduleExamBean dbBean,ScheduleCreationBean scheduleBean,String userId)
	{
		ExamsAssessmentsBean examBean = new ExamsAssessmentsBean();
		examBean.setAssessments_id(dbBean.getAssessmentId());
		examBean.setTimebound_id(scheduleBean.getTimeboundId());
		examBean.setSchedule_id(dbBean.getScheduleId());
		examBean.setSchedule_name(dbBean.getScheduleName());
		examBean.setSchedule_accessKey(dbBean.getScheduleAccessKey());
		examBean.setSchedule_accessUrl(dbBean.getScheduleAccessURL());
		examBean.setSchedule_status(dbBean.getScheduleStatus());
		examBean.setCreatedBy(userId);
		examBean.setLastModifiedBy(userId);
		examBean.setMax_score(scheduleBean.getMax_score());
		examBean.setName(scheduleBean.getAssessmentName());
		examBean.setBatchName(scheduleBean.getBatchName());
		examBean.setDuration(Integer.parseInt(scheduleBean.getAssessmentDuration()));
		return examBean;
	}
	
	public String registerCandidates(HttpServletRequest request,ExamsAssessmentsBean examBean,String subjectName,String endTime,boolean isReExam,boolean isExcelUpload,ArrayList<MettlRegisterCandidateBean> excelUserList)
	{
		String result="";
		ArrayList<MettlRegisterCandidateBean> userList = null;
		try
		{
			if(isExcelUpload)
			{
				userList = excelUserList;
			}
			else
			{
				if(isReExam)
				{
					userList = examDao.getTimeBoundUsersForReExam(examBean.getTimebound_id(),examBean.getExam_start_date_time());
				}
				else
				{
					userList = examDao.getTimeBoundUsers(examBean.getTimebound_id());
				}
			}
			if(userList!=null && userList.size()>0)
			{
				pddmMettlHelper.registerCandidatesForTimeBound(examDao,userList,examBean,PG_NO_IMAGE_URL,subjectName,endTime);
			}
			else
			{
				logger.info("Error while fetching Students for Registration for TimeBound Id: "+examBean.getTimebound_id()+" and access key :"+examBean.getSchedule_accessKey());
				result="Error while fetching Students for Registration for TimeBound Id: "+examBean.getTimebound_id()+" and access key :"+examBean.getSchedule_accessKey();
			}
		}
		catch(Exception e)
		{
			logger.error("Error while saving registration data on portal for TimeBound Id: "+examBean.getTimebound_id()+" and access key :"+examBean.getSchedule_accessKey());
		}
		finally
		{
			if(userList!=null && userList.size()>0)
			{
				int count=0;
				ArrayList<MettlRegisterCandidateBean> successfullUserList=null;
				if(isExcelUpload)
				{
					count = examDao.getRegisteredStudentsCountForExcelUpload(examBean.getTimebound_id(), examBean.getSchedule_accessKey(), userList);
				}
				else
				{
					successfullUserList = examDao.getRegisteredStudentsCount(examBean.getBatchName(),examBean.getTimebound_id(), examBean.getSchedule_accessKey());
					if(successfullUserList!=null && successfullUserList.size()>0)
					{
						request.getSession().setAttribute("successfullUserList", successfullUserList);
						request.setAttribute("successList", true);
						count=successfullUserList.size();
					}
				}
				request.setAttribute("count", count);
				logger.info("Count of registered students is:"+count+" for TimeBound Id: "+examBean.getTimebound_id()+" and access key :"+examBean.getSchedule_accessKey());
				/*if(userList.size()==count)
				{
					result="success";
				}
				else
				{*/
					if(isReExam)
					{
						userList = examDao.getTimeBoundUsersForReExamRetry(examBean.getTimebound_id(), examBean.getSchedule_accessKey(),examBean.getExam_start_date_time());
					}
					else
					{
						userList = examDao.getTimeBoundUsersRetry(examBean.getTimebound_id(), examBean.getSchedule_accessKey());
					}
					if(userList!=null && userList.size()>0)
					{
						request.getSession().setAttribute("failedRegistrations", userList);
						request.getSession().setAttribute("examDetails", examBean);
						request.getSession().setAttribute("subjectName", subjectName);
						request.getSession().setAttribute("endTime", endTime);
						result="Download Failed Registrations Excel";
					}
					else if(userList!=null && userList.size()==0)
					{
						logger.info("All candidates registered successfully, no new candidate found for TimeBound Id: "+examBean.getTimebound_id()+" and access key :"+examBean.getSchedule_accessKey());
						result="success";
					}
					else if(userList==null)
					{
						logger.info("Error while fetching Retry Students for Registration for TimeBound Id: "+examBean.getTimebound_id()+" and access key :"+examBean.getSchedule_accessKey());
						result="Error while fetching Retry Students for Registration for TimeBound Id: "+examBean.getTimebound_id()+" and access key :"+examBean.getSchedule_accessKey();
					}
				//}
			}
			else
			{
				logger.info("Error while fetching Students for Registration for TimeBound Id: "+examBean.getTimebound_id()+" and access key :"+examBean.getSchedule_accessKey());
				result="Error while fetching Students for Registration for TimeBound Id: "+examBean.getTimebound_id()+" and access key :"+examBean.getSchedule_accessKey();
			}
		}
		return result;
	}

}
