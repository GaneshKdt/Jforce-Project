package com.nmims.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ibm.icu.text.SimpleDateFormat;
import com.nmims.beans.ExamsAssessmentsBean;
import com.nmims.beans.FailedRegistrationBean;
import com.nmims.beans.FailedRegistrationResponseBean;
import com.nmims.beans.FailedregistrationExcelBean;
import com.nmims.beans.MettlRegisterCandidateBean;
import com.nmims.beans.MettlScheduleAPIBean;
import com.nmims.beans.MettlScheduleExamBean;
import com.nmims.beans.ScheduleCreationBean;
import com.nmims.daos.UpgradAssessmentDao;
import com.nmims.helpers.ExcelHelper;
import com.nmims.interfaces.MBAXScheduleCreationService;
import com.nmims.interfaces.MettlAssessments;

@Service
public class MBAXScheduleCreationServiceImp implements MBAXScheduleCreationService {

	@Value("${PG_NO_IMAGE_URL}")
	private String PG_NO_IMAGE_URL;
	
	@Value("${REPORTING_START_TIME_DIFF}")
	private Integer REPORTING_START_TIME_DIFF; 
	
	@Value("${REPORTING_FINISH_TIME_DIFF_TIMEBOUND}")
	private Integer REPORTING_FINISH_TIME_DIFF_TIMEBOUND;
	
	@Autowired
	UpgradAssessmentDao upgradAssessmentDao;
	
	private static final Logger logger = LoggerFactory.getLogger("mbaxScheduleCreation");

	
	@Override
	public MettlScheduleExamBean createApiParametersWithWaitingRoom(ScheduleCreationBean scheduleBean) throws Exception {
		MettlScheduleExamBean mettlScheduleBean = new MettlScheduleExamBean();
		
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

	@Override
	public MettlScheduleExamBean createApiParametersWithoutWaitingRoom(ScheduleCreationBean scheduleBean) throws Exception{
		MettlScheduleExamBean mettlScheduleBean = new MettlScheduleExamBean();
		
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
		mettlScheduleBean.setModeVP("VIDEO");
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

	@Override
	public ExamsAssessmentsBean createDbBean(MettlScheduleExamBean dbBean, ScheduleCreationBean scheduleBean,String userId) {
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

	@Override
	public String extendExamEndDateTime(String slotDate_id, String endTime, String assessmentDuration) throws Exception {
		SimpleDateFormat sdf= new SimpleDateFormat("HH:mm:ss");
		Date endDateTime=sdf.parse(endTime);
		String endTimeExtended=sdf.format(DateUtils.addMinutes(endDateTime, Integer.parseInt(assessmentDuration)));
		return slotDate_id+" "+endTimeExtended;
	}

	@Override
	public String createScheduleForMBAX(ScheduleCreationBean scheduleBean, MettlAssessments mettlAssesments,
			String userId, HttpServletRequest request) {
		
		String response ="";
		
		String timeBoundId = upgradAssessmentDao.getTimeBoundId(scheduleBean.getSubject_id(),
				scheduleBean.getBatch_id());
		logger.info("{} : mbaxScheduleCreation  - {} ",timeBoundId);
		
		if (timeBoundId != null) {
			ExamsAssessmentsBean examsAssessmentsBean = new ExamsAssessmentsBean();
			examsAssessmentsBean.setAssessments_id(scheduleBean.getAssessments_id());
			examsAssessmentsBean.setTimebound_id(scheduleBean.getTimeboundId());
			int mappingExists = upgradAssessmentDao.checkIfAssessmentTimeBoundMappingExists(examsAssessmentsBean);
			logger.info("{} : mappingExists for timebound  - {} ",mappingExists,timeBoundId);
			if (mappingExists > 0) {
				response = "Already Exists in Portal.";
			} else if (mappingExists < 0) {
				response = "Unable to find if mapping exists";
	
			} else if (mappingExists == 0) {
				try {
					response = mettlAssesments.createSchedule(request, scheduleBean, userId);	
				} catch (Exception e) {
						response = "Error In Schedule Creation";
						logger.info("{}- Error in Creating Schedule for Assesments {}   error -{}",e.getMessage(),scheduleBean.getAssessments_id(),e.getStackTrace());
				}
			}
	}
		return response;
	}

	@Override
	public FailedRegistrationResponseBean registerExcelStudent(FailedRegistrationBean bean,MettlAssessments assessments,HttpServletRequest request) throws Exception {
			FailedRegistrationResponseBean response = new FailedRegistrationResponseBean();
			logger.info("MBAX  Result is:"+bean);
			String result="";
			ExcelHelper excelHelper = new ExcelHelper();

			FailedregistrationExcelBean failedregistrationExcelBean = excelHelper.readFailedRegistrationExcel(bean);
			
			logger.info("MBAX Final Result is:"+failedregistrationExcelBean);
			if(failedregistrationExcelBean!=null && !StringUtils.isEmpty(failedregistrationExcelBean.getSubject()))
			{
				boolean isReExam = false;
				ExamsAssessmentsBean examBean = failedregistrationExcelBean.getExamBean();
				examBean.setCreatedBy(bean.getCreatedBy());
				examBean.setLastModifiedBy(bean.getLastModifiedBy());
				String subjectName = failedregistrationExcelBean.getSubject();
				String endTime = failedregistrationExcelBean.getEndTime();
				ArrayList<MettlRegisterCandidateBean> userList = failedregistrationExcelBean.getUserList(); 
				String timeBoundId =  failedregistrationExcelBean.getExamBean().getTimebound_id();
				String scheduleId =  failedregistrationExcelBean.getExamBean().getSchedule_id();
			
				
				List<String> timeboundMapSapid = upgradAssessmentDao.checkTimeBoundMapping(timeBoundId);
				List<MettlRegisterCandidateBean> notMappedStudent = userList.stream().filter(id ->!timeboundMapSapid.contains(id.getSapId())).collect(Collectors.toList());
				if(!notMappedStudent.isEmpty()) {
					String sapid = notMappedStudent.stream().map(id-> id.getSapId()).collect(Collectors.joining(","));
					  throw new Exception(sapid+" Student Not Mapped To Particular TimeBound : "+examBean.getTimebound_id());
				}
				
				List<String> registerSapid = upgradAssessmentDao.getRegisterStudentForScheduleAndTimeBound(timeBoundId, scheduleId);
				List<MettlRegisterCandidateBean> alreadyRegisteredStudent = userList.stream().filter(id ->registerSapid.contains(id.getSapId())).collect(Collectors.toList());
				if(!alreadyRegisteredStudent.isEmpty()) {
					String sapid = alreadyRegisteredStudent.stream().map(id-> id.getSapId()).collect(Collectors.joining(","));
					  throw new Exception(sapid+" Student Already Registered");
				}
				
				
				logger.info("MBAX user list excel:"+userList.toString());
				logger.info("MBAX excel subjectName:"+subjectName);
				logger.info("MBAX excel endTime:"+endTime);
				logger.info("MBAX excel exam Bean:"+examBean.toString());
				
				  if(examBean.getMax_score().equalsIgnoreCase("100")) 
				  { 
					  isReExam=true; 
				  }
				 
					if (StringUtils.isEmpty(examBean.getReporting_start_date_time())) {
						examBean.setReporting_start_date_time(null);

					}
					if (StringUtils.isEmpty(examBean.getReporting_finish_date_time())) {
						examBean.setReporting_finish_date_time(null);

					}
				  result=assessments.registerCandidates(request, examBean, subjectName, endTime, isReExam, true, userList); 
				  logger.info("MBAX Final Result is:"+result);
				  if(!"success".equalsIgnoreCase(result)) 
				  {
					  response.setError(result);
					  if(result.contains("Please Download failed Registered Student and Re-upload")) 
					  {
						  response.setDownloadExcel("true"); 
					  } 
				  }
				  if(!StringUtils.isEmpty(String.valueOf(request.getAttribute("count"))))
				  {
					  response.setCount(String.valueOf(request.getAttribute("count")));
				  }
			}
			else
			{
				logger.info("MBAX check excel file data");
				response.setError("check excel file data");
			}
			return response;
		
	}

	@Override
	public ArrayList<MettlRegisterCandidateBean> getNotRegisterStudentOfExcel(String timebound_id,
			String schedule_accessKey, ArrayList<MettlRegisterCandidateBean> userList) {
		String sapid = userList.stream().map(id -> id.getSapId()).collect(Collectors.joining(","));
		
		List<String> registeredSapid = upgradAssessmentDao.getNotRegisterStudentOfExcel(timebound_id,
				schedule_accessKey, sapid);
		
		userList.removeIf(id -> registeredSapid.contains(id.getSapId()));
		return userList;
	}



	
}
