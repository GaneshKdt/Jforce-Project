package com.nmims.services;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.nmims.beans.MBAResponseBean;
import com.nmims.beans.MBAScheduleInfoBean;
import com.nmims.beans.MettlRegisterCandidateBeanMBAWX;
import com.nmims.beans.MettlSSOInfoBean;
import com.nmims.beans.ResponseBean;
import com.nmims.beans.TcsOnlineExamBean;
import com.nmims.beans.BatchExamBean;
import com.nmims.beans.ExamScheduleinfoBean;
import com.nmims.controllers.MBAWXTEERestController;
import com.nmims.daos.MBAWXTeeDAO;
import com.nmims.daos.MettlTeeDAO;
import com.nmims.helpers.MailSender;
import com.nmims.helpers.TeeSSOHelper;
import com.nmims.listeners.TEELinkScheduler;
@Service
public class MBAWXTeeService {
	
	@Value( "${SERVER}" )
	private String SERVER;
	
	@Autowired
	MBAWXTeeDAO teeDao;
	
	private static final Logger sendTeeLinklogger = LoggerFactory.getLogger(TEELinkScheduler.class);
	
	
	@Value("${ENVIRONMENT}")
	private String ENVIRONMENT;
	
	@Autowired
	MettlTeeDAO ssoDao;
	
	@Autowired
	TeeSSOHelper teeSSOHelper;
	
	@Autowired
	MailSender mailSender;
	
	@Autowired
	MBAWXTeeDAO mbaWXTeeDAO;

	@Autowired
	MettlTeeMarksService mettlTeeMarksService;
	
	private static final Logger logger = LoggerFactory.getLogger(MBAWXTEERestController.class);
	
	public MBAResponseBean getAssessmentDetails(MBAScheduleInfoBean input)
	{
		MBAResponseBean response = new MBAResponseBean();
		
		try{
			logger.info("\n"+SERVER+": "+new Date()+" getAssessmentDetails "+input);
			boolean isDataFromTempTable=false;
			MBAScheduleInfoBean scheduleInfo = teeDao.getScheduleInfoFromTempTable(input);
			if(scheduleInfo.getMaxMarks()!=0)
			{
				isDataFromTempTable=true;
			}
			else
			{
				scheduleInfo = teeDao.getScheduleInfo(input);
			}
			
			boolean examApplicable = teeDao.checkIfExamApplicableForStudent(scheduleInfo);
			
			if(examApplicable) {
				Date date = new Date();  
                long currentTimeStamp = date.getTime() / 1000;
                boolean examStarted = currentTimeStamp > scheduleInfo.getReportingStartTimeStamp();
                boolean examEnded = currentTimeStamp < scheduleInfo.getEndTimestamp();
                if(!(examStarted && examEnded)) {
					scheduleInfo.setCanStudentAttempt(false);
					scheduleInfo.setCantAttemptReason("Exam is not active at this time!");
                } else {
    				if(100 != scheduleInfo.getMaxMarks()) {
    					boolean isExamTakenByStudent=isDataFromTempTable?teeDao.checkIfExamTakenByStudentFromTempTable(scheduleInfo):teeDao.checkIfExamTakenByStudent(scheduleInfo);
    					if(isExamTakenByStudent) {
    						scheduleInfo.setCanStudentAttempt(true);
    					} else {
    						scheduleInfo.setCanStudentAttempt(false);
    						scheduleInfo.setCantAttemptReason("You have already attempted an Exam of this subject before!");
    					}
    				} else {
    					if(teeDao.checkIfExamBookedByStudent(scheduleInfo)) {
    						scheduleInfo.setCanStudentAttempt(true);
    					} else {
    						scheduleInfo.setCanStudentAttempt(false);
    						scheduleInfo.setCantAttemptReason("You have no bookings made for this subject!");
    					}
    				}
                }
			} else {
				scheduleInfo.setCanStudentAttempt(false);
				scheduleInfo.setCantAttemptReason("This Exam is not applicable for you!");
			}
			response.setResponse(scheduleInfo);
			response.setStatusSuccess();
			
		} catch(Exception e) {
			response.setStatusFail();
			response.setError("Internal Server Error");
			response.setErrorMessage(e.getMessage());
		}
		logger.info("\n"+SERVER+": "+new Date()+" getAssessmentDetails response "+response);
		
		return response;
	}
	
	
	public void sendExamJoinLinksForMbaWxStudents() {
		
			List<ExamScheduleinfoBean> successfulEmails = new ArrayList<ExamScheduleinfoBean>();
			List<ExamScheduleinfoBean> failedEmails = new ArrayList<ExamScheduleinfoBean>();

			try {
				List<ExamScheduleinfoBean> allStudentList = new ArrayList<ExamScheduleinfoBean>();
				List<ExamScheduleinfoBean> examAttemptedregularStudentList = ssoDao.getRegularLastExamAttemptdStudent();
				List<ExamScheduleinfoBean> resitStudentList = ssoDao.getResitStudentByExamStartTime();
				List<ExamScheduleinfoBean> regularStudentList = ssoDao.getRegularStudentByExamStartTime();
				
				List<ExamScheduleinfoBean> removeList = regularStudentList.stream()
					    .filter(studentBean -> examAttemptedregularStudentList.stream()
					        .anyMatch(attemptedStudent ->
					            attemptedStudent.getSapid().equalsIgnoreCase(studentBean.getSapid()) &&
					            attemptedStudent.getTimeboundId().equalsIgnoreCase(studentBean.getTimeboundId())
					        )
					    )
					    .collect(Collectors.toList());
				
				regularStudentList.removeAll(removeList);
				
				if (!(regularStudentList.isEmpty()))
					allStudentList.addAll(regularStudentList);
				if (!(resitStudentList.isEmpty()))
					allStudentList.addAll(resitStudentList);
				
				if ((allStudentList.isEmpty())) {
					return;
				} 
				
				sendTeeLinklogger.info("Total MbaWx students whose mail must be sent :: "+allStudentList.size() +" at time :: "+LocalDateTime.now());
				for (ExamScheduleinfoBean studentInfoBean : allStudentList) {
					
					String formattedDateTime = null;
					try {
						formattedDateTime = teeSSOHelper
								.getFormattedDateAndTimeForEmail(studentInfoBean.getExamStartDateTime());
						sendTeeLinklogger.info(SERVER + ": sendInvitationLinksForMbaWxUpcomingExams " + formattedDateTime
								+ " # bookings found :  " + allStudentList.size());
					} catch (Exception e) {
						sendTeeLinklogger.info(
								"formattedDateTime :: sendInvitationLinksForMbaWxUpcomingExams of getFormattedDateAndTimeForEmail call Error ::"
										+ e.getMessage());
						formattedDateTime = studentInfoBean.getExamStartDateTime() + "IST";
					}
					
					String joinLink = "";
					try {
						joinLink = teeSSOHelper.generateMettlExamLink(studentInfoBean);
						studentInfoBean.setJoinURL(joinLink);
						studentInfoBean.setFormattedDateStringForEmail(formattedDateTime);
					} catch (Exception e) {
						StringWriter error = new StringWriter();
						e.printStackTrace(new PrintWriter(error));
						sendTeeLinklogger.error(SERVER + ": Error while  generating Join Link for ForMbaWx - bean "
								+ studentInfoBean + ", Error : " + error.toString());

						studentInfoBean.setError("Error while generating ForMbaWx Join Link \n" + e.getMessage());
						failedEmails.add(studentInfoBean);
						continue;
					}

					try {
						Thread.sleep(50);
						teeSSOHelper.sendExamJoinLinkMail(studentInfoBean);
						successfulEmails.add(studentInfoBean);
					} catch (SocketException s) {
						StringWriter error = new StringWriter();
						s.printStackTrace(new PrintWriter(error));
						sendTeeLinklogger.error(
								SERVER + ": SocketException Occur while Sending Join Link ForMbaWx Retry Start - bean "
										+ studentInfoBean + ", Error : " + error.toString());

						try {
							Thread.sleep(5000);
							teeSSOHelper.sendExamJoinLinkMail(studentInfoBean);
							successfulEmails.add(studentInfoBean);
						} catch (Exception ex) {
							StringWriter errors = new StringWriter();
							ex.printStackTrace(new PrintWriter(errors));
							sendTeeLinklogger.error(
									SERVER + ": Exception Occur while Sending Join Link ForMbaWx Retry Fail - bean "
											+ studentInfoBean + ", Error : " + errors.toString());
							studentInfoBean.setError(
									"(Catch 2) :Error Sending Join Link ForMbaWx catch 1 \n" + ex.getMessage());
							failedEmails.add(studentInfoBean);
							continue;
						}
					}

				}

			} catch (Exception ex) {
				StringWriter errors = new StringWriter();
				ex.printStackTrace(new PrintWriter(errors));
				sendTeeLinklogger.error(SERVER + ": error in scheduler sendInvitationLinksForMbaWxUpcomingExams ERROR : "
						+ errors.toString());
			}

			sendTeeLinklogger.info(SERVER + ": sendInvitationLinksForMbaWxUpcomingExams FINISH: " + successfulEmails.size()
					+ " # bookings found :  " + failedEmails.size());
			sendTeeLinklogger.info(SERVER + ": sendInvitationLinksForMbaWxUpcomingExams FINISH: Fail List -" + failedEmails);
			try {
				mailSender.sendMbaWxExamJoinLinkStatusMail(successfulEmails, failedEmails);
			} catch (Exception e) {
				sendTeeLinklogger.info(SERVER + ": Error in sendExamJoinLinkStatusMail error" + failedEmails);
			}
		
	}
	
	public ResponseBean getMettlJoinLinkForSapid(MettlRegisterCandidateBeanMBAWX inputBean) {
		ResponseBean responseBean = new ResponseBean();
		try {
			TcsOnlineExamBean tcsOnlineExamBean = new TcsOnlineExamBean();	
			ExamScheduleinfoBean bean = new ExamScheduleinfoBean();
			bean.setSapid(inputBean.getSapid());
			bean.setScheduleId(inputBean.getSchedule_id());
			bean.setTimeboundId(inputBean.getTimebound_id());
			String joinUrl = teeSSOHelper.generateMettlExamLink(bean);
			tcsOnlineExamBean.setJoinUrl(joinUrl);
			responseBean.setTcsOnlineExamBean(tcsOnlineExamBean);
			responseBean.setCode(200);
			return responseBean;
		}catch(Exception e) {
			logger.info("Exception in getMettlJoinLinkForSapid is:"+e.getMessage());
			responseBean.setCode(422);
			responseBean.setMessage("Error while generating exam link:"+e.getMessage());
			return responseBean;
		}
	}
	
	public ResponseBean sendEmailJoinLinkMbaWx(MettlRegisterCandidateBeanMBAWX inputBean) {
		ResponseBean responseBean = new ResponseBean();
		try {
			String formattedDateTime = null;
			String joinLink = null;
			try {
				formattedDateTime = teeSSOHelper
						.getFormattedDateAndTimeForEmail(inputBean.getExamStartDateTime());

			}catch (Exception e) {
				formattedDateTime = inputBean.getExamStartDateTime() + "IST";
			}
			ExamScheduleinfoBean emailBean = createEmailBean(inputBean);
			joinLink = teeSSOHelper.generateMettlExamLink(emailBean);
			emailBean.setJoinURL(joinLink);
			emailBean.setFormattedDateStringForEmail(formattedDateTime);
			String status = teeSSOHelper.sendExamJoinLinkMail(emailBean);
			if(status.equalsIgnoreCase("Success")) {
				responseBean.setCode(200);
			}else {
				responseBean.setCode(422);
				responseBean.setMessage("Error in sending email to the student");
			}
		}catch(Exception e) {
			logger.info("Exception in sendEmailJoinLinkMbaWx"+e.getMessage());
			responseBean.setCode(422);
			responseBean.setMessage("Error:"+e.getMessage());	
		}
		return responseBean;
	}
	
	public ExamScheduleinfoBean createEmailBean(MettlRegisterCandidateBeanMBAWX inputBean) {
		ExamScheduleinfoBean bean = new ExamScheduleinfoBean();
		bean.setSapid(inputBean.getSapid());
		bean.setSubject(inputBean.getSubject());
		bean.setEmailId(inputBean.getEmailId());
		bean.setTimeboundId(inputBean.getTimebound_id());
		bean.setScheduleId(inputBean.getSchedule_id());
		return bean;
	}
	
	public ResponseBean getExamStatusMbaWx(String sapid,String scheduleId) {
		ResponseBean responseBean = new ResponseBean();
		try {
			ArrayList<MettlRegisterCandidateBeanMBAWX> mettlRegisterCandidateBeanMBAWX = mbaWXTeeDAO.getExamStatusMbaWx(sapid,scheduleId);
			responseBean.setMettlRegisterCandidateBeanMBAWX(mettlRegisterCandidateBeanMBAWX);
			responseBean.setCode(200);
		}catch(Exception e) {
			logger.info("Exception in getExamStatusMbaWx:"+e.getMessage());
			responseBean.setCode(422);
			responseBean.setMessage("Error in getting exam status:"+e.getMessage());	
		}
		return responseBean;
	}
	
	public ArrayList<MettlRegisterCandidateBeanMBAWX> getMbaWxExamData(String programType,String examTime,String examType,String sapid) throws Exception{

		ArrayList<MettlRegisterCandidateBeanMBAWX> programSpecificUserList = new ArrayList<MettlRegisterCandidateBeanMBAWX>();

		ArrayList<Integer> consumerProgramStructureIdList = mettlTeeMarksService.getConsumerProgramStrucutreId(programType);
		ArrayList<MettlRegisterCandidateBeanMBAWX> userList = mbaWXTeeDAO.getMbaWxStudentsExamDataOnExamTime(examType, examTime,sapid);
		ArrayList<String> batchList =  mbaWXTeeDAO.getBatchDataForDashboardMBAWX(consumerProgramStructureIdList);
		ArrayList<BatchExamBean> timeboundList  = mbaWXTeeDAO.getTimeBoundDataForDashboardMBAWX();

		programSpecificUserList = (ArrayList<MettlRegisterCandidateBeanMBAWX>)userList.stream().filter(user -> 

		timeboundList.stream().filter(timeboundId -> 
		batchList.stream().anyMatch(id -> id.equalsIgnoreCase(timeboundId.getBatchId()))).anyMatch(filteredTimeBoundId -> user.getTimebound_id().equalsIgnoreCase(Integer.toString(filteredTimeBoundId.getId())))).collect(Collectors.toList());		
		return programSpecificUserList;
	}
	
}