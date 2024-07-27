package com.nmims.services;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.nmims.beans.AcadCycleFeedback;
import com.nmims.beans.PortalFeedbackBeanResponse;
import com.nmims.beans.SessionAttendanceFeedbackStudentPortal;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.daos.PortalDao;
import com.nmims.helpers.RegistrationHelper;

@Service("FeedbackService")
public class FeedbackService {

	@Autowired
	PortalDao portalDAO;
	
	@Autowired
	RegistrationHelper registrationHelper;

	@Autowired
	ApplicationContext act;

	public PortalFeedbackBeanResponse getPendingFeedbacks(String userId) {
		
		PortalFeedbackBeanResponse response = new PortalFeedbackBeanResponse(); // response bean
		ArrayList<SessionAttendanceFeedbackStudentPortal> pendingFeedback = new ArrayList<SessionAttendanceFeedbackStudentPortal>();
		StudentStudentPortalBean student = null;
		
		try {
			student = portalDAO.getSingleStudentsData(userId);
			StudentStudentPortalBean studentRegistrationForAcademicSession = registrationHelper.checkStudentRegistration(userId, student);

			if (student.getProgram().equalsIgnoreCase("EPBM") || student.getProgram().equalsIgnoreCase("MPDV")) {
				pendingFeedback = portalDAO.getPendingFeedbacksSAS(userId, student.getEnrollmentYear(),
						student.getEnrollmentMonth());
			} else {
				pendingFeedback = portalDAO.getPendingFeedbacks(userId, studentRegistrationForAcademicSession);
			}
//			System.out.println("pendingFeedback" + pendingFeedback);
		} catch (Exception e) {
			
//			System.out.println("sapid undefined");
			return response;
		}

		if (pendingFeedback != null && pendingFeedback.size() > 0) {

			// prepare session feedback to send
			response.setPendingFeedback(pendingFeedback);
			response.setFeedbackType("pendingSessionFeedback");
			return response;

		} else {
			// Check acadsfeedback
			AcadCycleFeedback surveyList = portalDAO.getLiveSurveyDetails();
			if (surveyList != null) {

				AcadCycleFeedback bean = portalDAO.getSingleStudentsRegistrationData(userId, surveyList);

				if (bean != null) {
					ArrayList<AcadCycleFeedback> pendingAcadFeedback = portalDAO.getPendingAcadCycleFeedbacks(userId, bean.getSem(),
							student.getProgram());

					if (pendingAcadFeedback.size() == 0) {
						response.setFeedbackType("pendingAcadFeedback");
						response.setAcadCycleFeedbackBean(bean);
						System.out.println("pendingAcadFeedback:" + response);
						return response;
					}
					
					
				} else {
					response.setFeedbackType("");
				}
			}
			return response;
		}

	}
	
	public boolean saveSessionFeedback(SessionAttendanceFeedbackStudentPortal feedback,String userId) {
		PortalDao pDao = (PortalDao) act.getBean("portalDAO");
		
		if (userId != null) {
			feedback.setSapId(userId);
			feedback.setFeedbackGiven("Y");
			feedback.setCreatedBy(userId);
			feedback.setLastModifiedBy(userId);
			
			try {
				pDao.saveFeedback(feedback);
				return true;

			} catch (Exception e) {
				// TODO Auto-generated catch block
				
				return false;
			}
		} else {
			return false;
		}
	}  
	
	public boolean saveAcadFeedback(AcadCycleFeedback feedback) {
		String feedbackVal = feedback.getFeedbackGiven();
		String userId  = feedback.getSapid();
		StudentStudentPortalBean student = portalDAO.getSingleStudentsData(userId);
		try {
			feedback.setProgram(student.getProgram());
			if (feedbackVal.equalsIgnoreCase("No")) {
				feedback.setFeedbackGiven("N");
			} else {
				feedback.setFeedbackGiven("Y");
			}
			feedback.setCreatedBy(userId);
			feedback.setLastModifiedBy(userId);

			portalDAO.saveAcadCycleFeedback(feedback);
			return true;
			
		} catch (Exception e) {
			
			return false;
			

		}
	}
}
