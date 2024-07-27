package com.nmims.controllers;

import java.util.Date;

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
import com.nmims.daos.MBAWXTeeDAO;

@Controller
public class MBAWXTEEController extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(MBAWXTEEController.class);

	@Value( "${SERVER}" )
	private String SERVER;
	
	@Autowired
	MBAWXTeeDAO teeDao;
	
	
//	to be deleted, api shifted to rest controller
//	@RequestMapping(value = "/m/getAssessmentDetails", method = { RequestMethod.POST, RequestMethod.GET }, produces = "application/json; charset=UTF-8")
//	public ResponseEntity<MBAResponseBean> getAllCenters(HttpServletRequest request, @RequestBody MBAScheduleInfoBean input) {
//		MBAResponseBean response = new MBAResponseBean();
//		
//		try{
//			logger.info("\n"+SERVER+": "+new Date()+" getAssessmentDetails "+input);
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
//    					if(teeDao.checkIfExamTakenByStudent(scheduleInfo)) {
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
//		logger.info("\n"+SERVER+": "+new Date()+" getAssessmentDetails response "+response);
//		
//		return new ResponseEntity<MBAResponseBean>(response, HttpStatus.OK);
//	}
}
