package com.nmims.controllers;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.nmims.beans.MBAResponseBean;
import com.nmims.beans.MBAScheduleInfoBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.daos.MBAXTeeDAO;

@Controller
public class MBAXTEEController extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(MBAXTEEController.class);

	@Value( "${SERVER}" )
	private String SERVER;
	
	@Autowired
	MBAXTeeDAO teeDao;
	
	
//	to be deleted, api shifted to rest controller
//	@RequestMapping(value = "/m/getMBAXAssessmentDetails", method = { RequestMethod.POST, RequestMethod.GET }, produces = "application/json; charset=UTF-8")
//	public ResponseEntity<MBAResponseBean> getAllCenters(HttpServletRequest request, @RequestBody MBAScheduleInfoBean input) {
//		MBAResponseBean response = new MBAResponseBean();
//		
//		try{
//			logger.info("\n"+SERVER+": "+new Date()+" getMBAXAssessmentDetails "+input);
//			
//			MBAScheduleInfoBean scheduleInfo = teeDao.getScheduleInfo(input);
//			
//			boolean examApplicable = teeDao.checkIfExamApplicableForStudent(scheduleInfo);
//			
//			if(examApplicable) {
//				Date date = new Date();  
//                long currentTimeStamp = date.getTime() / 1000;
//                boolean examStarted = currentTimeStamp > scheduleInfo.getStartTimestamp();
//                boolean examEnded = currentTimeStamp < scheduleInfo.getEndTimestamp();
//                if(!(examStarted && examEnded)) {
//					scheduleInfo.setCanStudentAttempt(false);
//					scheduleInfo.setCantAttemptReason("Exam is not active at this time!");
//                } else {
//    				if(100 != scheduleInfo.getMaxMarks()) {
//    					if(teeDao.checkIfExamTakenByStudent(scheduleInfo.getTimeboundId(), scheduleInfo.getSapid())) {
//    						scheduleInfo.setCanStudentAttempt(true);
//    					} else {
//    						scheduleInfo.setCanStudentAttempt(false);
//    						scheduleInfo.setCantAttemptReason("You have already attempted an Exam of this subject before!");
//    					}
//    				} else {
//    					if(teeDao.checkIfExamBookedByStudent(scheduleInfo)) {
//    						scheduleInfo.setCanStudentAttempt(true);
//    					} else {
//    						scheduleInfo.setCanStudentAttempt(false);
//    						scheduleInfo.setCantAttemptReason("You have no bookings made for this subject!");
//    					}
//    				}
//                }
//			} else {
//				scheduleInfo.setCanStudentAttempt(false);
//				scheduleInfo.setCantAttemptReason("This Exam is not applicable for you!");
//			}
//			response.setResponse(scheduleInfo);
//			response.setStatusSuccess();
//
//		} catch(Exception e) {
//			response.setStatusFail();
//			response.setError("Internal Server Error");
//			response.setErrorMessage(e.getMessage());
//			
//		}
//		logger.info("\n"+SERVER+": "+new Date()+" getMBAXAssessmentDetails response "+response);
//		
//		return new ResponseEntity<MBAResponseBean>(response, HttpStatus.OK);
//	}
//	
//	@RequestMapping(value = "/m/getMBAXAssessmentsBySapid",  method = RequestMethod.POST)
//	public  ResponseEntity<MBAResponseBean> getFinishedTeeAssessmentsBySapid(@RequestBody StudentBean student) {
//		MBAResponseBean response = new MBAResponseBean();
//		
//		try {
//			List<MBAScheduleInfoBean> teeFinishedAssessmentsList = teeDao.getAssessmentsForSapid(student.getSapid());
//			response.setResponse(teeFinishedAssessmentsList);
//			response.setStatusSuccess();
//		}catch (Exception e) {
//			response.setStatusFail();
//			response.setErrorMessage("Internal Server Error!");
//			response.setError(e.getMessage());
//		}
//
//		return new ResponseEntity<MBAResponseBean>(response, HttpStatus.OK);
//	}
}
