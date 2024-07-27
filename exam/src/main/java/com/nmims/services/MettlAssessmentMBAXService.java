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
import com.nmims.beans.MettlScheduleExamBean;
import com.nmims.beans.ScheduleCreationBean;
import com.nmims.daos.ExamsAssessmentsDAO;
import com.nmims.daos.UpgradAssessmentDao;
import com.nmims.helpers.MettlHelper;
import com.nmims.interfaces.MBAXScheduleCreationService;
import com.nmims.interfaces.MettlAssessments;

@Service("mettlAssessmentMBAXService")
public class MettlAssessmentMBAXService implements MettlAssessments{

	@Autowired
	@Qualifier("mbaxMettlHelper")
	MettlHelper mbaxMettlHelper;
	
	@Autowired
	MBAXScheduleCreationService mbaxScheduleCreationService;
	
	@Value("${PG_NO_IMAGE_URL}")
	private String PG_NO_IMAGE_URL;

	@Autowired
	UpgradAssessmentDao upgradAssessmentDao;
	

	@Autowired
	ExamsAssessmentsDAO examDao;
	
	private static final Logger logger = LoggerFactory.getLogger("mbaxScheduleCreation");
	
	@Override
	public ArrayList<MettlResponseBean> getAllAssessments() {
		ArrayList<MettlResponseBean> mettlAssessmentResponseBeanList = new ArrayList<>();
		try {
				JsonObject jsonResponse = mbaxMettlHelper.getAssessments();
				if(jsonResponse != null) {
					String status = jsonResponse.get("status").getAsString();
					if("SUCCESS".equalsIgnoreCase(status)) {
						JsonArray assessmentList = jsonResponse.get("assessments").getAsJsonArray();
						for (JsonElement assessmentElement : assessmentList) {
							JsonObject assessmentObject = assessmentElement.getAsJsonObject();
							MettlResponseBean tmp_responseBean = new MettlResponseBean();
							tmp_responseBean.setAssessments_id(assessmentObject.get("id").getAsInt());
							tmp_responseBean.setName(assessmentObject.get("name").getAsString());
							tmp_responseBean.setCustomAssessmentName(assessmentObject.get("name").getAsString());
							tmp_responseBean.setDuration(assessmentObject.get("duration").getAsInt());
							mettlAssessmentResponseBeanList.add(tmp_responseBean);
						}
						
					}
				}
				return mettlAssessmentResponseBeanList;
			} catch (Exception e) {
				logger.info("{} : Error While Getting Assessments for MBAX  ",e.getMessage());
				//e.printStackTrace();
				
				return mettlAssessmentResponseBeanList;
				
			}
		}
	


	@Override
	public String createSchedule(HttpServletRequest request, ScheduleCreationBean scheduleBean, String userId)
			throws Exception {
		String result="";
		String sourceAppName = "NGASCE";
		MettlScheduleExamBean dbBean = null;
		String examStartDateTime="";
		String examEndDateTime="";
		MettlScheduleExamBean queryBean = null;
		String endTimeForWindowAccess="";
		ExamsAssessmentsBean examBean = null;

		if(scheduleBean.getWaitingRoom().equalsIgnoreCase("enabled")) {
			queryBean = mbaxScheduleCreationService.createApiParametersWithWaitingRoom(scheduleBean);
		}else if(scheduleBean.getWaitingRoom().equalsIgnoreCase("disabled")) {
			queryBean =mbaxScheduleCreationService.createApiParametersWithoutWaitingRoom(scheduleBean);
		}
		
		if(scheduleBean.getWaitingRoom().equalsIgnoreCase("disabled")) {
			examEndDateTime=mbaxScheduleCreationService.extendExamEndDateTime(scheduleBean.getSlotDate_id(),queryBean.getEndTime(),scheduleBean.getAssessmentDuration());
		}
		else if(scheduleBean.getWaitingRoom().equalsIgnoreCase("enabled")){
			examEndDateTime=scheduleBean.getSlotDate_id()+" "+queryBean.getEndTime();
		}
		
		examStartDateTime=scheduleBean.getSlotDate_id()+" "+queryBean.getStartTime();
		
		logger.info("{} : Created API line 113   - {} ",queryBean,scheduleBean.getTimeboundId());
		if(!"100".equalsIgnoreCase(scheduleBean.getMax_score())) {
			if(scheduleBean.getWaitingRoom().equalsIgnoreCase("enabled")) {
				 dbBean = mbaxMettlHelper.createScheduleWithWaitingRoom(queryBean,sourceAppName);
			}else if(scheduleBean.getWaitingRoom().equalsIgnoreCase("disabled")) {
				 dbBean = mbaxMettlHelper.createSchedule(queryBean,sourceAppName);
			}
			logger.info("{} :Response for Schedule Creation For less the 100 marks, line 120  - {} ",dbBean,scheduleBean.getTimeboundId());
			
			if("Success".equalsIgnoreCase(dbBean.getStatus())){
				 examBean = mbaxScheduleCreationService.createDbBean(dbBean,scheduleBean,userId);
				
				logger.info("{} :ExamBean  line 125 - {} ",examBean,scheduleBean.getBatch_id());
				logger.info("{} :ExamStartTime line 126  - {} ",examStartDateTime,scheduleBean.getTimeboundId());
				
				logger.info("{} :ExamEndTime  line 128 - {} ",examEndDateTime,scheduleBean.getTimeboundId());
				examBean.setExam_start_date_time(examStartDateTime);
				examBean.setExam_end_date_time(examEndDateTime);
				
				if(scheduleBean.getWaitingRoom().equalsIgnoreCase("enabled")) {
					examBean.setReporting_start_date_time(scheduleBean.getSlotDate_id()+" "+queryBean.getReportingStartTime());
					examBean.setReporting_finish_date_time(scheduleBean.getSlotDate_id()+" "+queryBean.getReportingFinishTime());
					logger.info("{} :Reporting_start_date_time  line 135 - {} ",examBean.getReporting_start_date_time(),scheduleBean.getTimeboundId());
					logger.info("{} :Reporting_finish_date_time  line 136 - {} ",examBean.getReporting_finish_date_time(),scheduleBean.getTimeboundId());
				}
				
				int assessmentExists = upgradAssessmentDao.checkIfAssessmentExists(examBean);
				logger.info("{} :Assessments Exist Check line 140 - {} ",assessmentExists,scheduleBean.getTimeboundId());

				if(assessmentExists>0) {
					result = upgradAssessmentDao.insertIntoExamScheduleAndAssessmentTimeboundWithReportTime(examBean);
					logger.info("{} : Data Base Response line 144  - {} ",result,scheduleBean.getTimeboundId());

				}else if(assessmentExists==0)
				{
					examBean.setName(scheduleBean.getAssessmentName());
					examBean.setCustomAssessmentName(scheduleBean.getAssessmentCustomName());
					examBean.setProgramType(scheduleBean.getProgramType());
					result=upgradAssessmentDao.insertIntoExamScheduleAndAssessmentAndAssessmentTimeboundWithReport(examBean);
					logger.info("{} : Data Base Response line 152  - {} ",result,scheduleBean.getBatch_id());

				}
				
				if("success".equalsIgnoreCase(result)) {
					//int id =upgradAssessmentDao.getExamScheduleId(examBean);
					endTimeForWindowAccess=scheduleBean.getSlotDate_id()+" "+queryBean.getEndTime2();
					result=registerCandidates(request,examBean,scheduleBean.getSubject_id(),endTimeForWindowAccess,false,false,null);

				}
			}else {
				result=dbBean.getMessage();
		logger.info("Error while creating schedule for Assessment: "+scheduleBean.getAssessmentName()+" and subject: "+scheduleBean.getSubject_id()+" and Batch: "+scheduleBean.getBatchName());

			}
		}else if("100".equalsIgnoreCase(scheduleBean.getMax_score())) {

			logger.info("{} :ExamStartTime  line 168 - {} ",examStartDateTime,scheduleBean.getTimeboundId());
			logger.info("{} :ExamEndTime   line 169- {} ",examEndDateTime,scheduleBean.getTimeboundId());
			int exist=upgradAssessmentDao.checkIfScheduleExist(scheduleBean.getAssessments_id(), examStartDateTime, examEndDateTime);

			if (exist > 0) {
				 examBean = upgradAssessmentDao.getExistingScheduleDetails(
						scheduleBean.getAssessments_id(), examStartDateTime, examEndDateTime);
				examBean.setTimebound_id(scheduleBean.getTimeboundId());
				examBean.setName(scheduleBean.getAssessmentName());
				examBean.setBatchName(scheduleBean.getBatchName());
				examBean.setCreatedBy(userId);
				examBean.setLastModifiedBy(userId);
				examBean.setDuration(Integer.parseInt(scheduleBean.getAssessmentDuration()));
				examBean.setAssessments_id(scheduleBean.getAssessments_id());

				int assessmentExists = upgradAssessmentDao.checkIfAssessmentExists(examBean);
				logger.info("{} :Assesmnets Check For 100 for Schedule Exist line 184  - {} ",assessmentExists,scheduleBean.getBatch_id());

				if (assessmentExists > 0) {
					result = upgradAssessmentDao.insertIntoExamScheduleAndAssessmentTimeboundWithReportTime(examBean);
				} else if (assessmentExists == 0) {
					examBean.setName(scheduleBean.getAssessmentName());
					examBean.setCustomAssessmentName(scheduleBean.getAssessmentCustomName());
					examBean.setProgramType(scheduleBean.getProgramType());
					result = upgradAssessmentDao.insertIntoExamScheduleAndAssessmentAndAssessmentTimeboundWithReport(examBean);
				}
				
			}else if(exist == 0) {
				if(scheduleBean.getWaitingRoom().equalsIgnoreCase("enabled")) {
					 dbBean = mbaxMettlHelper.createScheduleWithWaitingRoom(queryBean,sourceAppName);
				}else if(scheduleBean.getWaitingRoom().equalsIgnoreCase("disabled")) {
					 dbBean = mbaxMettlHelper.createSchedule(queryBean,sourceAppName);
				}
				
				if("Success".equalsIgnoreCase(dbBean.getStatus())){
					 examBean = mbaxScheduleCreationService.createDbBean(dbBean,scheduleBean,userId);
	
					examBean.setExam_start_date_time(examStartDateTime);
					examBean.setExam_end_date_time(examEndDateTime);
					
					logger.info("{} :ExamStartTime  line 208 - {} ",examStartDateTime,scheduleBean.getTimeboundId());
					logger.info("{} :ExamEndTime  line 209 - {} ",examEndDateTime,scheduleBean.getTimeboundId());
					
					if(scheduleBean.getWaitingRoom().equalsIgnoreCase("enabled")) {
						examBean.setReporting_start_date_time(scheduleBean.getSlotDate_id()+" "+queryBean.getReportingStartTime());
						examBean.setReporting_finish_date_time(scheduleBean.getSlotDate_id()+" "+queryBean.getReportingFinishTime());
						logger.info("{} :Reporting_start_date_time   line 214- {} ",examBean.getReporting_start_date_time(),scheduleBean.getTimeboundId());
						logger.info("{} :Reporting_finish_date_time  line 215 - {} ",examBean.getReporting_finish_date_time(),scheduleBean.getTimeboundId());
					
					}
					
					int assessmentExists = upgradAssessmentDao.checkIfAssessmentExists(examBean);
					logger.info("{} :Assesmnets Check For 100 for Schedule Not Exist  line 220 - {} ",assessmentExists,scheduleBean.getTimeboundId());

					if(assessmentExists>0) {
						result = upgradAssessmentDao.insertIntoExamScheduleAndAssessmentTimeboundWithReportTime(examBean);
					}else if(assessmentExists==0)
					{
						examBean.setName(scheduleBean.getAssessmentName());
						examBean.setCustomAssessmentName(scheduleBean.getAssessmentCustomName());
						examBean.setProgramType(scheduleBean.getProgramType());
						result=upgradAssessmentDao.insertIntoExamScheduleAndAssessmentAndAssessmentTimeboundWithReport(examBean);
					}
					logger.info("{} :database Response for 100 line 231   - {} ",result,scheduleBean.getTimeboundId());

				}else {
					result=dbBean.getMessage();
					logger.info("Error while creating schedule for Assessment: "+scheduleBean.getAssessmentName()+" and subject: "+scheduleBean.getSubject_id()+" and Batch: "+scheduleBean.getBatchName());

				}
			}
			if("success".equalsIgnoreCase(result)) {
				int id = upgradAssessmentDao.getExamScheduleId(examBean);
				examBean.setId(""+id);
				String exam_start_date_time = examBean.getExam_start_date_time();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date startDate = sdf.parse(exam_start_date_time);
				String exam_end_date_time = sdf.format(DateUtils.addMinutes(startDate, Integer.parseInt(scheduleBean.getAssessmentDuration())));
				examBean.setExam_end_date_time(exam_end_date_time);
				result=upgradAssessmentDao.updateScheduleIdInTimeTable(examBean);
				logger.info("{} : Data Base Response for 100 marks  line 247  - {} ",result,scheduleBean.getTimeboundId());
				if(result.equalsIgnoreCase("success"))
				{
					examBean.setExam_end_date_time(examEndDateTime);
					endTimeForWindowAccess=scheduleBean.getSlotDate_id()+" "+queryBean.getEndTime2();
					result=registerCandidates(request,examBean,scheduleBean.getSubject_id(),endTimeForWindowAccess,true,false,null);
				}
			}
		}
		logger.info("{} : Final Response   - {} ",result,scheduleBean.getTimeboundId());
		return result;
	}

	@Override
	public ExamsAssessmentsBean createDbBean(MettlScheduleExamBean dbBean, ScheduleCreationBean scheduleBean,
			String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String registerCandidates(HttpServletRequest request, ExamsAssessmentsBean examBean, String subjectName,
			String endTime, boolean isReExam, boolean isExcelUpload,
			ArrayList<MettlRegisterCandidateBean> excelUserList) {
		
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
						userList = examDao.getTimeBoundUsersForReExamForMBAX(examBean.getTimebound_id(),examBean.getExam_start_date_time());
					}
					else
					{
						userList = examDao.getTimeBoundUsers(examBean.getTimebound_id());
					}
				}
				
				if(userList!=null && userList.size()>0)
				{
					mbaxMettlHelper.registerCandidatesForTimeBoundForMBAX(examDao,userList,examBean,PG_NO_IMAGE_URL,subjectName,endTime);
				}
				else
				{
					logger.info("Error while fetching Students for Registration for TimeBound Id: "+examBean.getTimebound_id()+" and access key :"+examBean.getSchedule_accessKey());
					result="Error While Registering Students";
					if(isReExam) {
						result="Schedule Createt and Booking Not found";
					}
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
						count = examDao.getRegisteredStudentsCountForExcelUploadForMBAX(examBean.getTimebound_id(), examBean.getSchedule_accessKey(), userList);
					}
					else
					{
						successfullUserList = examDao.getRegisteredStudentsCountForMBAX(examBean.getBatchName(),examBean.getTimebound_id(), examBean.getSchedule_accessKey());
						if(successfullUserList!=null && successfullUserList.size()>0)
						{
							request.getSession().setAttribute("successfullUserList", successfullUserList);
							request.setAttribute("successList", true);
							count=successfullUserList.size();
						}
					}
					request.setAttribute("count", count);
					if(isExcelUpload)
					{
						userList = mbaxScheduleCreationService.getNotRegisterStudentOfExcel(examBean.getTimebound_id(), examBean.getSchedule_accessKey(),userList);

					}else if(isReExam)
						{
							userList = examDao.getTimeBoundUsersForReExamRetryForMBAX(examBean.getTimebound_id(), examBean.getSchedule_accessKey(),examBean.getExam_start_date_time());
						}
						else
						{
							userList = examDao.getTimeBoundUsersRetryForMBAX(examBean.getTimebound_id(), examBean.getSchedule_accessKey());
						}
						if(userList!=null && userList.size()>0)
						{
							request.getSession().setAttribute("failedRegistrations", userList);
							request.getSession().setAttribute("examDetails", examBean);
							request.getSession().setAttribute("subjectName", subjectName);
							request.getSession().setAttribute("endTime", endTime);
							request.setAttribute("downloadExcel", true);
							result="Please Download failed Registered Student and Re-upload";
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
					result="Error While Registering Students";
					if(isReExam) {
						result="Schedule Createt and Booking Not found";					}
				}
			}
			return result;
		}

	
}
