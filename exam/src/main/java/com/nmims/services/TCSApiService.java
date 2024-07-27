package com.nmims.services;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.nmims.beans.DemoExamAttendanceBean;
import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.beans.FileBean;
import com.nmims.beans.MettlRegisterCandidateBean;
import com.nmims.beans.MettlSSOInfoBean;
import com.nmims.beans.Page;
import com.nmims.beans.ResponseBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.beans.TCSExamBookingDataBean;
import com.nmims.beans.TcsOnlineExamBean;
import com.nmims.controllers.ExamBookingController;
import com.nmims.daos.MettlDAO;
import com.nmims.daos.TCSApiDAO;
import com.nmims.helpers.ExcelHelper;
import com.nmims.helpers.MailSender;
import com.nmims.helpers.MettlHelper;
import com.nmims.helpers.TCSApis;
import com.nmims.helpers.TeeSSOHelper;
import com.nmims.stratergies.ExamRegistrationRealTimeStrategy;

@Service("tcsApiService")
public class TCSApiService {
	@Autowired
	TCSApiDAO tcsDAO;
	
	@Autowired
	TCSApis tcsHelper;
	
	@Autowired
	ExcelHelper excelHelper ;
	
	@Autowired
	MettlHelper mettlHelper;
	
	@Value("${MettlBaseUrl}")
	String MettlBaseUrl;
	
	@Value("${PG_METTL_PRIVATE_KEY}")
	String PG_METTL_PRIVATE_KEY;
	
	@Value("${PG_METTL_PUBLIC_KEY}")
	String PG_METTL_PUBLIC_KEY;
	
	@Value( "${ENVIRONMENT}" )
	private String ENVIRONMENT;
	
	@Autowired
	TeeSSOHelper ssoHelper;
	
	@Autowired
	MettlDAO mettlDAO;
	
	@Autowired
	ExamBookingController examBookingController;
	
	@Autowired
	private ExamRegistrationRealTimeStrategy examRegistrationRealTimeStrategy;
	
	@Autowired
	ApplicationContext act;
	
	private final int pageSize = 100;
	
	public static final Logger examRegisterlogger = LoggerFactory.getLogger("examRegisterPG");
	
	public ResponseBean displayTCSExamBookingDataService(Integer pageNo ,StudentMarksBean studentMarksBean,String authorizedCenterCodes){
		ResponseBean responseBean = new ResponseBean();
		try {
			
			if (pageNo < 1) {
				pageNo = 1;
			}
			
			responseBean = tcsDAO.getTcsExamBookingDataToDisplay(pageNo, pageSize,studentMarksBean,authorizedCenterCodes);
			responseBean.setCode(200);
			return responseBean;
		}catch(Exception e) {
			responseBean.setCode(422);
			responseBean.setMessage("Error in Fetching records.  "+e.getMessage());
			return responseBean;
		}

	}
	
	public List<TcsOnlineExamBean> downloadExamBookingDataService(StudentMarksBean studentMarksBean,String authorizedCenterCodes){
		List<TcsOnlineExamBean> tcsOnlineExamBeanList = new ArrayList<TcsOnlineExamBean>();
		try {
			tcsOnlineExamBeanList = tcsDAO.getExamBookingListForDownloadExcel(studentMarksBean,authorizedCenterCodes);
			return tcsOnlineExamBeanList;
		}catch(Exception e) {
			
			return tcsOnlineExamBeanList;
		}
		
	}
	
	public ResponseBean getExamCenterDropdownService(TcsOnlineExamBean tcsOnlineExamBean) {
		ResponseBean responseBean = new ResponseBean();
		try {
			responseBean.setTcsOnlineExamBeanList( tcsDAO.getExamCenterDropdown(tcsOnlineExamBean) );
			return responseBean ;
		}catch(Exception e) {
			return responseBean ;
		}
	}
	public ResponseBean getExamDateDropdownService(TcsOnlineExamBean tcsOnlineExamBean) {
		ResponseBean responseBean = new ResponseBean();
		try {
			responseBean.setTcsOnlineExamBeanList( tcsDAO.getExamDateDropdown(tcsOnlineExamBean) );
			return responseBean ;
		}catch(Exception e) {
			return responseBean ;
		}
	}
	
	public ResponseBean getExamStartTimeDropdownService(TcsOnlineExamBean tcsOnlineExamBean) {
		ResponseBean responseBean = new ResponseBean();
		try {
			responseBean.setTcsOnlineExamBeanList( tcsDAO.getExamStartTimeDropdown(tcsOnlineExamBean) );
			return responseBean ;
		}catch(Exception e) {
			return responseBean ;
		}
	}
	
	public String getUpdatedEndTime(String startTime,String endTime, String updatedStartTime ) {

		try {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
		Date datestartTime = (Date)sdf.parse(startTime);
		Date dateendTime =(Date) sdf.parse(endTime);

		Timestamp timestamp1 = new Timestamp(datestartTime.getTime());

		Timestamp timestamp2 = new Timestamp(dateendTime.getTime());

		long milliseconds = timestamp2.getTime() - timestamp1.getTime();
		int seconds = (int) milliseconds / 1000;

		int hours = seconds / 3600;
		int minutes = (seconds % 3600) / 60;
		seconds = (seconds % 3600) % 60;
		
		Calendar now = Calendar.getInstance();
		now.setTime(sdfTime.parse(updatedStartTime));
		now.add(Calendar.SECOND,seconds);
	    now.add(Calendar.MINUTE,minutes);
	    now.add(Calendar.HOUR_OF_DAY,hours);
	    Date dateUpdatedEndTime = now.getTime(); 
		String updatedEndTime = sdfTime.format(dateUpdatedEndTime);	
		
		return updatedEndTime;
		}catch(Exception e) {
			return null;
		}
		
	}
	
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ResponseBean updateExamBookingSlotForSapidServiceRealTime(String lastModifiedBy, TcsOnlineExamBean tcsOnlineExamBean) {
		ResponseBean responseBean = new ResponseBean();
		StudentMarksBean studentMarks = new StudentMarksBean () ;
		MailSender mailSender = (MailSender) act.getBean("mailer");	
		try {
			ExamBookingTransactionBean prevExamBookingBean = new ExamBookingTransactionBean();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			SimpleDateFormat sdfCompare = new SimpleDateFormat("yyyy-MM-dd");
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			Random rand = new Random(); 
			Integer randomInt = rand.nextInt(100);
			List<ExamBookingTransactionBean> releasedSubjectList = new ArrayList<ExamBookingTransactionBean>();
			List<ExamBookingTransactionBean> newExamBookingList = new ArrayList<ExamBookingTransactionBean>();
			
				studentMarks.setYear(tcsOnlineExamBean.getExamYear());
				studentMarks.setMonth(tcsOnlineExamBean.getExamMonth());
				tcsDAO.decreaseCountExamBookedSlot(tcsOnlineExamBean);
				
				prevExamBookingBean = tcsDAO.getPrevExamBookingBean(tcsOnlineExamBean);
				String trackId = prevExamBookingBean.getTrackId();
				trackId += "_Reschedule_"+randomInt+sdf.format(timestamp);
				String prevExamDate = prevExamBookingBean.getExamDate();
				String prevExamTime = prevExamBookingBean.getExamTime();
				tcsDAO.updatePrevExamBookingSlotForSapid(tcsOnlineExamBean, trackId,lastModifiedBy);
				ExamBookingTransactionBean releasedSubject = new ExamBookingTransactionBean();
				try {
					BeanUtils.copyProperties(prevExamBookingBean, releasedSubject);
					releasedSubjectList.add(releasedSubject);
				}
				catch(Exception e) {
					//tcsDAO.deletePGScheduleinfoMettl(tcsOnlineExamBean);
				}
				prevExamBookingBean.setExamDate(tcsOnlineExamBean.getExamDate());
				prevExamBookingBean.setExamTime(tcsOnlineExamBean.getExamTime());
				prevExamBookingBean.setLastModifiedBy(lastModifiedBy);
				String startTime = prevExamDate+" "+prevExamTime;
				String endTime = prevExamDate+" "+prevExamBookingBean.getExamEndTime();
				String updatedEndTime =  getUpdatedEndTime(startTime,endTime,tcsOnlineExamBean.getExamTime());
				prevExamBookingBean.setExamEndTime(updatedEndTime);
				prevExamBookingBean.setEmailId("");
				newExamBookingList.add(prevExamBookingBean);
				tcsDAO.insertRescheduleExamBookingSlotForSapid(prevExamBookingBean);
				tcsDAO.increaseCountExamBookedSlot(tcsOnlineExamBean);
				
				examRegisterlogger.info("Real Time Registartion called from updateExamBookingSlotForSapidServiceRealTime method"+tcsDAO.getIsExtendedExamRegistrationLiveForRealTime());
				if(tcsDAO.getIsExtendedExamRegistrationLiveForRealTime()) {
					examRegisterlogger.info("Real Time Registartion called");
					examRegistrationRealTimeStrategy.registrationOnMettlAndReleaseBooking(newExamBookingList,releasedSubjectList);
				}
				
		        TcsOnlineExamBean tcsOnlineExamBeanListForEmail = tcsDAO.getRescheduleSapidDetailsForEmail(tcsOnlineExamBean);
				mailSender.sendRescheduleMailToStudent(tcsOnlineExamBeanListForEmail);
				
				responseBean.setCode(200);
				responseBean.setMessage("SuccessFully Updated Exambooking Slot For Sapid "+tcsOnlineExamBean.getUserId());
				return responseBean ;
		}catch(Exception e) {
			responseBean.setCode(422);
			responseBean.setMessage("Error in Update records. "+e.getMessage());
			return responseBean ;
		}
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ResponseBean updateExamBookingSlotForSapidService(String lastModifiedBy, TcsOnlineExamBean tcsOnlineExamBean) {
		ResponseBean responseBean = new ResponseBean();
		StudentMarksBean studentMarks = new StudentMarksBean () ;
		MailSender mailSender = (MailSender) act.getBean("mailer");	
		try {
			ExamBookingTransactionBean prevExamBookingBean = new ExamBookingTransactionBean();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			SimpleDateFormat sdfCompare = new SimpleDateFormat("yyyy-MM-dd");
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			Random rand = new Random(); 
			Integer randomInt = rand.nextInt(100);
			
			
				studentMarks.setYear(tcsOnlineExamBean.getExamYear());
				studentMarks.setMonth(tcsOnlineExamBean.getExamMonth());
				tcsDAO.decreaseCountExamBookedSlot(tcsOnlineExamBean);
				
				prevExamBookingBean = tcsDAO.getPrevExamBookingBean(tcsOnlineExamBean);
				String trackId = prevExamBookingBean.getTrackId();
				trackId += "_Reschedule_"+randomInt+sdf.format(timestamp);
				String prevExamDate = prevExamBookingBean.getExamDate();
				String prevExamTime = prevExamBookingBean.getExamTime();
				tcsDAO.updatePrevExamBookingSlotForSapid(tcsOnlineExamBean, trackId,lastModifiedBy);
				tcsDAO.deletePGScheduleinfoMettl(tcsOnlineExamBean);
				prevExamBookingBean.setExamDate(tcsOnlineExamBean.getExamDate());
				prevExamBookingBean.setExamTime(tcsOnlineExamBean.getExamTime());
				prevExamBookingBean.setLastModifiedBy(lastModifiedBy);
				String startTime = prevExamDate+" "+prevExamTime;
				String endTime = prevExamDate+" "+prevExamBookingBean.getExamEndTime();
				String updatedEndTime =  getUpdatedEndTime(startTime,endTime,tcsOnlineExamBean.getExamTime());
				prevExamBookingBean.setExamEndTime(updatedEndTime);
				
				
				String regMsg="";
				
				Date updatedDate = sdfCompare.parse(tcsOnlineExamBean.getExamDate()); 
		        Date currentDate = sdfCompare.parse(sdfCompare.format(timestamp)); 
		        
				
				if(currentDate.equals(updatedDate)) {
					
					if("PROD".equalsIgnoreCase(ENVIRONMENT)){  
						mettlHelper.setBaseUrl(MettlBaseUrl);
						mettlHelper.setPrivateKey(PG_METTL_PRIVATE_KEY);
						mettlHelper.setPublicKey(PG_METTL_PUBLIC_KEY);
						
						tcsDAO.insertRescheduleExamBookingSlotForSapid(prevExamBookingBean);
						tcsDAO.increaseCountExamBookedSlot(tcsOnlineExamBean);
						
						MettlRegisterCandidateBean mettlRegisterCandidateBean =	tcsDAO.getExamBookingSameDateRescheduleListForRegistration(tcsOnlineExamBean);
						List<MettlRegisterCandidateBean> mettlRegisterCandidateBeanList = new ArrayList<>();
						mettlRegisterCandidateBeanList.add(mettlRegisterCandidateBean);
						MettlRegisterCandidateBean[] candidateBeanArr = mettlHelper.registerCandidate(mettlRegisterCandidateBeanList, mettlRegisterCandidateBean.getScheduleAccessKey());
						
						if("error".equals(candidateBeanArr[0].getStatus())) {
							mettlDAO.saveCandidateRegisteredMettlInfo(mettlRegisterCandidateBean.getSapId(), mettlRegisterCandidateBean.getScheduleAccessKey(), 1, (candidateBeanArr[0].getStatus() + "|" + candidateBeanArr[0].getMessage()), null);
							regMsg = " But Registration Link not Generated cause : "+candidateBeanArr[0].getMessage();
						}else {
							regMsg = " And Registration Link Generated SuccessFully";
							TcsOnlineExamBean tcsOnlineExamBeanListForEmail = tcsDAO.getRescheduleSapidDetailsForEmail(tcsOnlineExamBean);
							mailSender.sendRescheduleMailToStudent(tcsOnlineExamBeanListForEmail);
						}
							
					}
					
				}else {
					prevExamBookingBean.setEmailId("");
					tcsDAO.insertRescheduleExamBookingSlotForSapid(prevExamBookingBean);
					tcsDAO.increaseCountExamBookedSlot(tcsOnlineExamBean);
					TcsOnlineExamBean tcsOnlineExamBeanListForEmail = tcsDAO.getRescheduleSapidDetailsForEmail(tcsOnlineExamBean);
					mailSender.sendRescheduleMailToStudent(tcsOnlineExamBeanListForEmail);
				}
				// start commented by Abhay for Mettl
				//	responseBean = syncUpdatedExamBookingDataService(studentMarks);
				// End commented by Abhay for Mettl
				responseBean.setCode(200);
				responseBean.setMessage("SuccessFully Updated Exambooking Slot For Sapid "+tcsOnlineExamBean.getUserId()+" "+regMsg);
				return responseBean ;
		}catch(Exception e) {
			responseBean.setCode(422);
			responseBean.setMessage("Error in Update records. "+e.getMessage());
			return responseBean ;
		}
	}
	
	public ResponseBean bulkExcelPreviewService(FileBean fileBean) {
		ResponseBean responseBean = new ResponseBean();
		try {
			ArrayList<TcsOnlineExamBean> tcsOnlineExamBeanList = new ArrayList<TcsOnlineExamBean>();
			tcsOnlineExamBeanList = excelHelper.readTcsBulkUpdateExamBooking(fileBean);
			
			TcsOnlineExamBean tcsOnlineExamList = new TcsOnlineExamBean();
			tcsOnlineExamList.setTcsOnlineExamList(tcsOnlineExamBeanList);
			
			ResponseBean validateResponseBean = validateUpdateTcsExamBookingService(tcsOnlineExamList,"NO");
			responseBean.setCode(validateResponseBean.getCode());
			responseBean.setMessage(validateResponseBean.getMessage());
			responseBean.setTcsOnlineExamBeanList(tcsOnlineExamBeanList);
			return responseBean;
		}catch(Exception e) {
			responseBean.setCode(422);
			responseBean.setMessage("Error in Excel Preview. "+e.getMessage());
			return responseBean ;
		}
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ResponseBean excelBulkSyncUpdateTcsExamBookingService(String lastModifiedBy, FileBean fileBean) {
		ResponseBean responseBean = new ResponseBean();
		MailSender mailSender = (MailSender) act.getBean("mailer");	
//		TcsOnlineExamBean tcsOnlineExamBean = new TcsOnlineExamBean();
		StudentMarksBean studentMarks = new StudentMarksBean () ;
		String errorMsg = "";
		String userId = "";
		Integer successCount = 0;
		Integer errorCount = 0;
		String regMsg="";
		Integer regSuccessCount=0;
		Integer regErrorCount=0;
		ArrayList<TcsOnlineExamBean> tcsOnlineExamBeanList = new ArrayList<TcsOnlineExamBean>();
		tcsOnlineExamBeanList = excelHelper.readTcsBulkUpdateExamBooking(fileBean);
		
		try {	
			for(TcsOnlineExamBean tcsOnlineExamBean: tcsOnlineExamBeanList) {
				
			if("UPDATE".equalsIgnoreCase(tcsOnlineExamBean.getBulkAction())) {	
				String isAnySubjectPresentResult = tcsDAO.isAnySubjectPresentSameSlotForSapid(tcsOnlineExamBean);
				if(isAnySubjectPresentResult.equals("NO")) { 
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
					SimpleDateFormat sdfCompare = new SimpleDateFormat("yyyy-MM-dd");
					Timestamp timestamp = new Timestamp(System.currentTimeMillis());
					ExamBookingTransactionBean prevExamBookingBean = new ExamBookingTransactionBean();
					Random rand = new Random(); 
					Integer randomInt = rand.nextInt(100);
					
					studentMarks.setYear(tcsOnlineExamBean.getExamYear());
					studentMarks.setMonth(tcsOnlineExamBean.getExamMonth());
					
					tcsDAO.decreaseCountExamBookedSlot(tcsOnlineExamBean);
					
					prevExamBookingBean = tcsDAO.getPrevExamBookingBean(tcsOnlineExamBean);
					String trackId = prevExamBookingBean.getTrackId();
					trackId += "_Reschedule_"+randomInt+sdf.format(timestamp);
					
					String prevExamDate = prevExamBookingBean.getExamDate();
					String prevExamTime = prevExamBookingBean.getExamTime();
					
					tcsDAO.updatePrevExamBookingSlotForSapid(tcsOnlineExamBean, trackId,lastModifiedBy);
					tcsDAO.deletePGScheduleinfoMettl(tcsOnlineExamBean);
					prevExamBookingBean.setExamDate(tcsOnlineExamBean.getExamDate());
					prevExamBookingBean.setExamTime(tcsOnlineExamBean.getExamTime());
					prevExamBookingBean.setLastModifiedBy(lastModifiedBy);
					
					String startTime = prevExamDate+" "+prevExamTime;
					String endTime = prevExamDate+" "+prevExamBookingBean.getExamEndTime();
					String updatedEndTime =  getUpdatedEndTime(startTime,endTime,tcsOnlineExamBean.getExamTime());
					prevExamBookingBean.setExamEndTime(updatedEndTime);
					
					Date updatedDate = sdfCompare.parse(tcsOnlineExamBean.getExamDate()); 
			        Date currentDate = sdfCompare.parse(sdfCompare.format(timestamp)); 
			        
					if(currentDate.equals(updatedDate)) {
						if("PROD".equalsIgnoreCase(ENVIRONMENT)){  
							mettlHelper.setBaseUrl(MettlBaseUrl);
							mettlHelper.setPrivateKey(PG_METTL_PRIVATE_KEY);
							mettlHelper.setPublicKey(PG_METTL_PUBLIC_KEY);
							
							tcsDAO.insertRescheduleExamBookingSlotForSapid(prevExamBookingBean);
							tcsDAO.increaseCountExamBookedSlot(tcsOnlineExamBean);
							
							MettlRegisterCandidateBean mettlRegisterCandidateBean =	tcsDAO.getExamBookingSameDateRescheduleListForRegistration(tcsOnlineExamBean);
							List<MettlRegisterCandidateBean> mettlRegisterCandidateBeanList = new ArrayList<>();
							mettlRegisterCandidateBeanList.add(mettlRegisterCandidateBean);
							MettlRegisterCandidateBean[] candidateBeanArr = mettlHelper.registerCandidate(mettlRegisterCandidateBeanList, mettlRegisterCandidateBean.getScheduleAccessKey());
							
							if("error".equals(candidateBeanArr[0].getStatus())) {
								regErrorCount++;
								mettlDAO.saveCandidateRegisteredMettlInfo(mettlRegisterCandidateBean.getSapId(), mettlRegisterCandidateBean.getScheduleAccessKey(), 1, (candidateBeanArr[0].getStatus() + "|" + candidateBeanArr[0].getMessage()), null);
								regMsg = " But For "+regErrorCount+" SapId's  Registration Link  not Generated cause : "+candidateBeanArr[0].getMessage();
							}else {
								regSuccessCount++;
								regMsg = " And For "+regSuccessCount+" SapId's Registration Link  Generated SuccessFully";
								TcsOnlineExamBean tcsOnlineExamBeanListForEmail = tcsDAO.getRescheduleSapidDetailsForEmail(tcsOnlineExamBean);
								mailSender.sendRescheduleMailToStudent(tcsOnlineExamBeanListForEmail);
							}
						}
					}else {
						prevExamBookingBean.setEmailId("");
						tcsDAO.insertRescheduleExamBookingSlotForSapid(prevExamBookingBean);
						tcsDAO.increaseCountExamBookedSlot(tcsOnlineExamBean);
						TcsOnlineExamBean tcsOnlineExamBeanListForEmail = tcsDAO.getRescheduleSapidDetailsForEmail(tcsOnlineExamBean);
						mailSender.sendRescheduleMailToStudent(tcsOnlineExamBeanListForEmail);
					}
					successCount++;
				}else {
					userId += tcsOnlineExamBean.getUserId()+" | " ;
					errorCount++;
					errorMsg = " And For "+errorCount+" Sapid Selected Slot Already Present In System For Another Subject,  "+userId ;				
				}
			
			}
		}
			// start commented by Abhay for Mettl
			// responseBean = syncUpdatedExamBookingDataService(studentMarks);	
			// End commented by Abhay for Mettl
			if(successCount > 0) {
				responseBean.setCode(200);
				responseBean.setMessage("SuccessFully Updated "+successCount+" Sapid Exambooking Slot "+errorMsg+" "+regMsg);
				return responseBean ;
			}else {
				responseBean.setCode(422);
				responseBean.setMessage("Exambooking Slot Are Not Updated For "+errorCount+" Sapid, Selected Slot Already Present In System For Another Subject For Sapid : "+userId);
				return responseBean ;
			}
		}catch(Exception e) {
			
			String msg = "";
			if(successCount != 0) {
				msg = "Only "+successCount+" SapId Updated ";
			}
			responseBean.setCode(422);
			responseBean.setMessage(msg+" Error in Update records. "+e.getMessage());
			return responseBean ;
		}
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ResponseBean excelBulkSyncUpdateTcsExamBookingServiceRealTime(String lastModifiedBy, FileBean fileBean) {
		ResponseBean responseBean = new ResponseBean();
		MailSender mailSender = (MailSender) act.getBean("mailer");	
//		TcsOnlineExamBean tcsOnlineExamBean = new TcsOnlineExamBean();
		StudentMarksBean studentMarks = new StudentMarksBean () ;
		String errorMsg = "";
		String userId = "";
		Integer successCount = 0;
		Integer errorCount = 0;
		String regMsg="";
		Integer regSuccessCount=0;
		Integer regErrorCount=0;
		ArrayList<TcsOnlineExamBean> tcsOnlineExamBeanList = new ArrayList<TcsOnlineExamBean>();
		tcsOnlineExamBeanList = excelHelper.readTcsBulkUpdateExamBooking(fileBean);
		
		try {	
			for(TcsOnlineExamBean tcsOnlineExamBean: tcsOnlineExamBeanList) {
				
			if("UPDATE".equalsIgnoreCase(tcsOnlineExamBean.getBulkAction())) {	
				String isAnySubjectPresentResult = tcsDAO.isAnySubjectPresentSameSlotForSapid(tcsOnlineExamBean);
				if(isAnySubjectPresentResult.equals("NO")) { 
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
					SimpleDateFormat sdfCompare = new SimpleDateFormat("yyyy-MM-dd");
					Timestamp timestamp = new Timestamp(System.currentTimeMillis());
					ExamBookingTransactionBean prevExamBookingBean = new ExamBookingTransactionBean();
					Random rand = new Random(); 
					Integer randomInt = rand.nextInt(100);
					List<ExamBookingTransactionBean> releasedSubjectList = new ArrayList<ExamBookingTransactionBean>();
					List<ExamBookingTransactionBean> newExamBookingList = new ArrayList<ExamBookingTransactionBean>();
					
					studentMarks.setYear(tcsOnlineExamBean.getExamYear());
					studentMarks.setMonth(tcsOnlineExamBean.getExamMonth());
					
					tcsDAO.decreaseCountExamBookedSlot(tcsOnlineExamBean);
					
					prevExamBookingBean = tcsDAO.getPrevExamBookingBean(tcsOnlineExamBean);
					String trackId = prevExamBookingBean.getTrackId();
					trackId += "_Reschedule_"+randomInt+sdf.format(timestamp);
					
					String prevExamDate = prevExamBookingBean.getExamDate();
					String prevExamTime = prevExamBookingBean.getExamTime();
					
					tcsDAO.updatePrevExamBookingSlotForSapid(tcsOnlineExamBean, trackId,lastModifiedBy);
					ExamBookingTransactionBean releasedSubject = new ExamBookingTransactionBean();
					try {
						BeanUtils.copyProperties(prevExamBookingBean, releasedSubject);
						releasedSubjectList.add(releasedSubject);
					}
					catch(Exception e) {
						//tcsDAO.deletePGScheduleinfoMettl(tcsOnlineExamBean);
					}
					prevExamBookingBean.setExamDate(tcsOnlineExamBean.getExamDate());
					prevExamBookingBean.setExamTime(tcsOnlineExamBean.getExamTime());
					prevExamBookingBean.setLastModifiedBy(lastModifiedBy);
					
					String startTime = prevExamDate+" "+prevExamTime;
					String endTime = prevExamDate+" "+prevExamBookingBean.getExamEndTime();
					String updatedEndTime =  getUpdatedEndTime(startTime,endTime,tcsOnlineExamBean.getExamTime());
					prevExamBookingBean.setExamEndTime(updatedEndTime);
					prevExamBookingBean.setEmailId("");
					newExamBookingList.add(prevExamBookingBean);
					tcsDAO.insertRescheduleExamBookingSlotForSapid(prevExamBookingBean);
					tcsDAO.increaseCountExamBookedSlot(tcsOnlineExamBean);
					
					examRegisterlogger.info("Real Time Registartion called from excelBulkSyncUpdateTcsExamBookingServiceRealTime method"+tcsDAO.getIsExtendedExamRegistrationLiveForRealTime());
					if(tcsDAO.getIsExtendedExamRegistrationLiveForRealTime()) {
						examRegisterlogger.info("Real Time Registartion called");
						examRegistrationRealTimeStrategy.registrationOnMettlAndReleaseBooking(newExamBookingList,releasedSubjectList);
						}
					
			        TcsOnlineExamBean tcsOnlineExamBeanListForEmail = tcsDAO.getRescheduleSapidDetailsForEmail(tcsOnlineExamBean);
					mailSender.sendRescheduleMailToStudent(tcsOnlineExamBeanListForEmail);
					successCount++;
				}else {
					userId += tcsOnlineExamBean.getUserId()+" | " ;
					errorCount++;
					errorMsg = " And For "+errorCount+" Sapid Selected Slot Already Present In System For Another Subject,  "+userId ;				
				}
			
			}
		}
			// start commented by Abhay for Mettl
			// responseBean = syncUpdatedExamBookingDataService(studentMarks);	
			// End commented by Abhay for Mettl
			if(successCount > 0) {
				responseBean.setCode(200);
				responseBean.setMessage("SuccessFully Updated "+successCount+" Sapid Exambooking Slot "+errorMsg);
				return responseBean ;
			}else {
				responseBean.setCode(422);
				responseBean.setMessage("Exambooking Slot Are Not Updated For "+errorCount+" Sapid, Selected Slot Already Present In System For Another Subject For Sapid : "+userId);
				return responseBean ;
			}
		}catch(Exception e) {
			
			String msg = "";
			if(successCount != 0) {
				msg = "Only "+successCount+" SapId Updated ";
			}
			responseBean.setCode(422);
			responseBean.setMessage(msg+" Error in Update records. "+e.getMessage());
			return responseBean ;
		}
	}
	
	public ResponseBean syncUpdatedExamBookingDataService(StudentMarksBean studentMarks) {
		ResponseBean responseBean = new ResponseBean();
		try {
			responseBean = tcsHelper.execute(studentMarks,"NO");
			if(responseBean.getStatus().equals("success")) {
				tcsDAO.resetSyncExamCenterProvider(studentMarks);
				return responseBean;
			}else {
				return responseBean;
			}
		} catch (Exception e) {
			
			responseBean.setCode(422);
			responseBean.setMessage("Error in uploading records.");
			return responseBean;
		}
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ResponseBean multipleUpdateTcsExamBookingService(String lastModifiedBy, TcsOnlineExamBean tcsOnlineExamList) {
		ResponseBean responseBean = new ResponseBean();
//		TcsOnlineExamBean tcsOnlineExamBean = new TcsOnlineExamBean();
		StudentMarksBean studentMarks = new StudentMarksBean () ;
		MailSender mailSender = (MailSender) act.getBean("mailer");	
		List<TcsOnlineExamBean> tcsOnlineExamBeanList = tcsOnlineExamList.getTcsOnlineExamList();
		Integer successCount = 0;
		String userId = "";
		Integer errorCount = 0;
		String errorMsg = "";
		String regMsg="";
		Integer regSuccessCount=0;
		Integer regErrorCount=0;
		try {
			
			for(TcsOnlineExamBean tcsOnlineExamBean: tcsOnlineExamBeanList) {
				tcsOnlineExamBean.setExamDate(tcsOnlineExamList.getExamDate());
				tcsOnlineExamBean.setExamTime(tcsOnlineExamList.getExamTime());
				String isAnySubjectPresentResult = tcsDAO.isAnySubjectPresentSameSlotForSapid(tcsOnlineExamBean);
				if(isAnySubjectPresentResult.equals("NO")) { 
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
					SimpleDateFormat sdfCompare = new SimpleDateFormat("yyyy-MM-dd");
					Timestamp timestamp = new Timestamp(System.currentTimeMillis());
					ExamBookingTransactionBean prevExamBookingBean = new ExamBookingTransactionBean();
					Random rand = new Random(); 
					Integer randomInt = rand.nextInt(100);
					
					studentMarks.setYear(tcsOnlineExamBean.getExamYear());
					studentMarks.setMonth(tcsOnlineExamBean.getExamMonth());
					tcsDAO.decreaseCountExamBookedSlot(tcsOnlineExamBean);
					
					prevExamBookingBean = tcsDAO.getPrevExamBookingBean(tcsOnlineExamBean);
					String trackId = prevExamBookingBean.getTrackId();
					trackId += "_Reschedule_"+randomInt+sdf.format(timestamp);
					
					String prevExamDate = prevExamBookingBean.getExamDate();
					String prevExamTime = prevExamBookingBean.getExamTime();
					
					String startTime = prevExamDate+" "+prevExamTime;
					String endTime = prevExamDate+" "+prevExamBookingBean.getExamEndTime();
					String updatedEndTime =  getUpdatedEndTime(startTime,endTime,tcsOnlineExamBean.getExamTime());
					prevExamBookingBean.setExamEndTime(updatedEndTime);
					
					tcsDAO.updatePrevExamBookingSlotForSapid(tcsOnlineExamBean, trackId,lastModifiedBy);
					tcsDAO.deletePGScheduleinfoMettl(tcsOnlineExamBean);
					prevExamBookingBean.setExamDate(tcsOnlineExamBean.getExamDate());
					prevExamBookingBean.setExamTime(tcsOnlineExamBean.getExamTime());
					prevExamBookingBean.setLastModifiedBy(lastModifiedBy);;
					
					Date updatedDate = sdfCompare.parse(tcsOnlineExamBean.getExamDate()); 
			        Date currentDate = sdfCompare.parse(sdfCompare.format(timestamp));
					
					if(currentDate.equals(updatedDate)) {
						 if("PROD".equalsIgnoreCase(ENVIRONMENT)){  
							mettlHelper.setBaseUrl(MettlBaseUrl);
							mettlHelper.setPrivateKey(PG_METTL_PRIVATE_KEY);
							mettlHelper.setPublicKey(PG_METTL_PUBLIC_KEY);
							
							tcsDAO.insertRescheduleExamBookingSlotForSapid(prevExamBookingBean);
							tcsDAO.increaseCountExamBookedSlot(tcsOnlineExamBean);
							
							MettlRegisterCandidateBean mettlRegisterCandidateBean =	tcsDAO.getExamBookingSameDateRescheduleListForRegistration(tcsOnlineExamBean);
							List<MettlRegisterCandidateBean> mettlRegisterCandidateBeanList = new ArrayList<>();
							mettlRegisterCandidateBeanList.add(mettlRegisterCandidateBean);
							MettlRegisterCandidateBean[] candidateBeanArr = mettlHelper.registerCandidate(mettlRegisterCandidateBeanList, mettlRegisterCandidateBean.getScheduleAccessKey());
							
							if("error".equals(candidateBeanArr[0].getStatus())) {
								regErrorCount++;
								mettlDAO.saveCandidateRegisteredMettlInfo(mettlRegisterCandidateBean.getSapId(), mettlRegisterCandidateBean.getScheduleAccessKey(), 1, (candidateBeanArr[0].getStatus() + "|" + candidateBeanArr[0].getMessage()), null);
								regMsg = " But For "+regErrorCount+" SapId's  Registration Link not Generated cause : "+candidateBeanArr[0].getMessage();
							}else {
								regSuccessCount++;
								regMsg = " And For "+regSuccessCount+" SapId's Registration Link  Generated SuccessFully";
								TcsOnlineExamBean tcsOnlineExamBeanListForEmail = tcsDAO.getRescheduleSapidDetailsForEmail(tcsOnlineExamBean);
								mailSender.sendRescheduleMailToStudent(tcsOnlineExamBeanListForEmail);
							}
						}
					}else {
						prevExamBookingBean.setEmailId("");
						tcsDAO.insertRescheduleExamBookingSlotForSapid(prevExamBookingBean);
						tcsDAO.increaseCountExamBookedSlot(tcsOnlineExamBean);
						TcsOnlineExamBean tcsOnlineExamBeanListForEmail = tcsDAO.getRescheduleSapidDetailsForEmail(tcsOnlineExamBean);
						mailSender.sendRescheduleMailToStudent(tcsOnlineExamBeanListForEmail);
					}
					successCount++;
				}else {
					userId += tcsOnlineExamBean.getUserId()+" | " ;
					errorCount++;
					errorMsg = " And For "+errorCount+" Sapid Selected Slot Already Present In System For Another Subject,  "+userId ;				
				}		
			}
			// start commented by Abhay for Mettl
			// responseBean = syncUpdatedExamBookingDataService(studentMarks);	
			// End commented by Abhay for Mettl
			
			if(successCount > 0) {
				responseBean.setCode(200);
				responseBean.setMessage("SuccessFully Updated "+successCount+" Sapid Exambooking Slot "+errorMsg+" "+regMsg);
				return responseBean ;
			}else {
				responseBean.setCode(422);
				responseBean.setMessage("Exambooking Slot Are Not Updated For "+errorCount+" Sapid, Selected Slot Already Present In System For Another Subject For Sapid : "+userId);
				return responseBean ;
			}
			
				
		}catch(Exception e) {
			
			String msg = "";
			
			if(successCount != 0) {
				msg = "Only "+successCount+" SapId Updated ";
			}
			responseBean.setCode(422);
			responseBean.setMessage(msg+" Error in Update records.  "+e.getMessage());
			return responseBean ;
		}
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ResponseBean multipleUpdateTcsExamBookingServiceRealTime(String lastModifiedBy, TcsOnlineExamBean tcsOnlineExamList) {
		ResponseBean responseBean = new ResponseBean();
//		TcsOnlineExamBean tcsOnlineExamBean = new TcsOnlineExamBean();
		StudentMarksBean studentMarks = new StudentMarksBean () ;
		MailSender mailSender = (MailSender) act.getBean("mailer");	
		List<TcsOnlineExamBean> tcsOnlineExamBeanList = tcsOnlineExamList.getTcsOnlineExamList();
		Integer successCount = 0;
		String userId = "";
		Integer errorCount = 0;
		String errorMsg = "";
		String regMsg="";
		Integer regSuccessCount=0;
		Integer regErrorCount=0;
		try {
			
			for(TcsOnlineExamBean tcsOnlineExamBean: tcsOnlineExamBeanList) {
				tcsOnlineExamBean.setExamDate(tcsOnlineExamList.getExamDate());
				tcsOnlineExamBean.setExamTime(tcsOnlineExamList.getExamTime());
				String isAnySubjectPresentResult = tcsDAO.isAnySubjectPresentSameSlotForSapid(tcsOnlineExamBean);
				if(isAnySubjectPresentResult.equals("NO")) { 
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
					SimpleDateFormat sdfCompare = new SimpleDateFormat("yyyy-MM-dd");
					Timestamp timestamp = new Timestamp(System.currentTimeMillis());
					ExamBookingTransactionBean prevExamBookingBean = new ExamBookingTransactionBean();
					Random rand = new Random(); 
					Integer randomInt = rand.nextInt(100);
					List<ExamBookingTransactionBean> releasedSubjectList = new ArrayList<ExamBookingTransactionBean>();
					List<ExamBookingTransactionBean> newExamBookingList = new ArrayList<ExamBookingTransactionBean>();
					
					studentMarks.setYear(tcsOnlineExamBean.getExamYear());
					studentMarks.setMonth(tcsOnlineExamBean.getExamMonth());
					tcsDAO.decreaseCountExamBookedSlot(tcsOnlineExamBean);
					
					prevExamBookingBean = tcsDAO.getPrevExamBookingBean(tcsOnlineExamBean);
					String trackId = prevExamBookingBean.getTrackId();
					trackId += "_Reschedule_"+randomInt+sdf.format(timestamp);
					
					String prevExamDate = prevExamBookingBean.getExamDate();
					String prevExamTime = prevExamBookingBean.getExamTime();
					
					String startTime = prevExamDate+" "+prevExamTime;
					String endTime = prevExamDate+" "+prevExamBookingBean.getExamEndTime();
					String updatedEndTime =  getUpdatedEndTime(startTime,endTime,tcsOnlineExamBean.getExamTime());
					prevExamBookingBean.setExamEndTime(updatedEndTime);
					
					tcsDAO.updatePrevExamBookingSlotForSapid(tcsOnlineExamBean, trackId,lastModifiedBy);
					ExamBookingTransactionBean releasedSubject = new ExamBookingTransactionBean();
					try {
						BeanUtils.copyProperties(prevExamBookingBean, releasedSubject);
						releasedSubjectList.add(releasedSubject);
					}
					catch(Exception e) {
						//tcsDAO.deletePGScheduleinfoMettl(tcsOnlineExamBean);
					}
					prevExamBookingBean.setExamDate(tcsOnlineExamBean.getExamDate());
					prevExamBookingBean.setExamTime(tcsOnlineExamBean.getExamTime());
					prevExamBookingBean.setLastModifiedBy(lastModifiedBy);;
					
					prevExamBookingBean.setEmailId("");
					newExamBookingList.add(prevExamBookingBean);
					tcsDAO.insertRescheduleExamBookingSlotForSapid(prevExamBookingBean);
					tcsDAO.increaseCountExamBookedSlot(tcsOnlineExamBean);
					
					examRegisterlogger.info("Real Time Registartion called from multipleUpdateTcsExamBookingServiceRealTime method"+tcsDAO.getIsExtendedExamRegistrationLiveForRealTime());
					if(tcsDAO.getIsExtendedExamRegistrationLiveForRealTime()) {
						examRegisterlogger.info("Real Time Registartion called");
						examRegistrationRealTimeStrategy.registrationOnMettlAndReleaseBooking(newExamBookingList,releasedSubjectList);
						}
					
			        TcsOnlineExamBean tcsOnlineExamBeanListForEmail = tcsDAO.getRescheduleSapidDetailsForEmail(tcsOnlineExamBean);
					mailSender.sendRescheduleMailToStudent(tcsOnlineExamBeanListForEmail);
					successCount++;
				}else {
					userId += tcsOnlineExamBean.getUserId()+" | " ;
					errorCount++;
					errorMsg = " And For "+errorCount+" Sapid Selected Slot Already Present In System For Another Subject,  "+userId ;				
				}		
			}
			// start commented by Abhay for Mettl
			// responseBean = syncUpdatedExamBookingDataService(studentMarks);	
			// End commented by Abhay for Mettl
			
			if(successCount > 0) {
				responseBean.setCode(200);
				responseBean.setMessage("SuccessFully Updated "+successCount+" Sapid Exambooking Slot "+errorMsg);
				return responseBean ;
			}else {
				responseBean.setCode(422);
				responseBean.setMessage("Exambooking Slot Are Not Updated For "+errorCount+" Sapid, Selected Slot Already Present In System For Another Subject For Sapid : "+userId);
				return responseBean ;
			}
			
				
		}catch(Exception e) {
			
			String msg = "";
			
			if(successCount != 0) {
				msg = "Only "+successCount+" SapId Updated ";
			}
			responseBean.setCode(422);
			responseBean.setMessage(msg+" Error in Update records.  "+e.getMessage());
			return responseBean ;
		}
	}
	
	public ResponseBean validateUpdateTcsExamBookingService(TcsOnlineExamBean tcsOnlineExamList, String isMultipleUpdate) {
		ResponseBean responseBean = new ResponseBean();
//		TcsOnlineExamBean tcsOnlineExamBean = new TcsOnlineExamBean();
		StudentMarksBean studentMarks = new StudentMarksBean () ;
		List<TcsOnlineExamBean> tcsOnlineExamBeanList = tcsOnlineExamList.getTcsOnlineExamList();
		String userId = "";
		Integer successCount = 0;
		Integer errorCount = 0;
		try {
			
			for(TcsOnlineExamBean tcsOnlineExamBean: tcsOnlineExamBeanList) {
				if(isMultipleUpdate.equals("YES")) {
				tcsOnlineExamBean.setExamDate(tcsOnlineExamList.getExamDate());
				tcsOnlineExamBean.setExamTime(tcsOnlineExamList.getExamTime());
				}
				studentMarks.setYear(tcsOnlineExamBean.getExamYear());
				studentMarks.setMonth(tcsOnlineExamBean.getExamMonth());
				String isAnySubjectPresentResult = tcsDAO.isAnySubjectPresentSameSlotForSapid(tcsOnlineExamBean);
				if(isAnySubjectPresentResult.equals("NO")) {
					String result  = tcsDAO.isExamBookingSlotEmpty(tcsOnlineExamBean);
					if(result.equals("YES")) {
						successCount++;
					}else{
							
							Integer extraAddedCount = tcsDAO.getExamBookingExtraAddedCount(tcsOnlineExamBean);
							
							String extraAddedCountMsg = "";
							
							if(extraAddedCount != 0) {
								extraAddedCountMsg += " Already Added "+extraAddedCount+" Extra Slot";
							}
							
							Integer errorCountSapid = tcsOnlineExamBeanList.size() - successCount ; 
							String bufferSuccessMsg = "";
							if(successCount !=0) {
								bufferSuccessMsg = "Only For "+successCount+" Sapid Exambooking Slot Is Available And";
							}
							
							responseBean.setCode(421);
							responseBean.setMessage("For This Slot Capacity is Full "+extraAddedCountMsg+" !!! "+bufferSuccessMsg+"For "+errorCountSapid+" Sapid Exambooking Slot Is Not Available !!! Do You Want To Proceed With Extending Capacity By "+errorCountSapid+" ? ");
							return responseBean ;	
					}	
				}else {
					userId += tcsOnlineExamBean.getUserId()+" | " ;
					errorCount++;
				}
		}
			if(errorCount > 0) {
				responseBean.setCode(422);
				responseBean.setMessage("Selected Slot Already Present In System For Another Subject For "+errorCount+" Sapid,  "+userId + " Remove This Sapid From List And Try Again !!!");
				return responseBean ;	
			}else {
				responseBean.setCode(200);
				return responseBean ;
			}
			
		}catch(Exception e) {
			
			responseBean.setCode(422);
			responseBean.setMessage("Error in Update records.  "+e.getMessage());
			return responseBean ;
		}
	}
	
	public ResponseBean getDemoExamLogBySapidService(String sapid) {
		ResponseBean responseBean = new ResponseBean();
		try {
			SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
			SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yy hh:mm aa");
			List<DemoExamAttendanceBean> demoExamAttendanceList = new ArrayList<>();
			demoExamAttendanceList = tcsDAO.getDemoExamLogBySapid(sapid);
			for(DemoExamAttendanceBean bean : demoExamAttendanceList) {
				if(bean.getStartedTime() != null) {
					Date dateStartTime = sdf1.parse(bean.getStartedTime());
					String newStartTime = sdf2.format(dateStartTime);
					bean.setStartedTime(newStartTime);
				}
				
				if(bean.getEndTime() != null) {
					Date dateEndTime = sdf1.parse(bean.getEndTime());
					String newEndTime = sdf2.format(dateEndTime);
					bean.setEndTime(newEndTime);
				}
			}
			responseBean.setDemoExamAttendanceList(demoExamAttendanceList);
			responseBean.setCode(200);
			return responseBean;
		}catch(Exception e) {
			//
			responseBean.setCode(422);
			responseBean.setMessage("Error in Fetching records.  "+e.getMessage());
			return responseBean;
		}
	}
	
	public ResponseBean getMettlJoinLinkForSapid(TcsOnlineExamBean input) {
		ResponseBean responseBean = new ResponseBean();
		try {
			TcsOnlineExamBean tcsOnlineExamBean = new TcsOnlineExamBean ();	
		MettlSSOInfoBean mettlSSOInfoBean = tcsDAO.getStudentDetailsForCopyJoinLink(input);
		String joinUrl = ssoHelper.generateMettlLink(mettlSSOInfoBean);
		tcsOnlineExamBean.setJoinUrl(joinUrl);
		responseBean.setTcsOnlineExamBean(tcsOnlineExamBean);
		responseBean.setCode(200);
		return responseBean;
		}catch(Exception e) {
			responseBean.setCode(422);
			responseBean.setMessage(e.getMessage());
			return responseBean;
		}
	}
	
	
}
