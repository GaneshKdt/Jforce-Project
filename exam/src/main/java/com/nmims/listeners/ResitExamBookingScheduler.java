/*package com.nmims.listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.context.ServletConfigAware;

import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.beans.ExamCenterBean;
import com.nmims.beans.StudentBean;
import com.nmims.beans.TimetableBean;
import com.nmims.daos.ExamBookingDAO;
import com.nmims.daos.ExamCenterDAO;
import com.nmims.helpers.MailSender;
import com.nmims.helpers.XMLParser;

public class ResitExamBookingScheduler implements ApplicationContextAware, ServletConfigAware{

	@Value( "${SECURE_SECRET}" )
	private String SECURE_SECRET; // secret key;
	@Value( "${ACCOUNT_ID}" )
	private String ACCOUNT_ID;

	@Value( "${SERVER}" )
	private String SERVER;

	@Value( "${ENVIRONMENT}" )
	private String ENVIRONMENT;

	private static ApplicationContext act = null;
	private static ServletConfig sc = null;


	@Override
	public void setServletConfig(ServletConfig sc) {
		this.sc = sc;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.act = applicationContext;
	}

	public static ApplicationContext getApplicationContext() {
		return act;
	}



	@Scheduled(fixedDelay=5*60*1000)
	public void clearOnHoldSeats(){


		if(!"tomcat4".equalsIgnoreCase(SERVER) || !"PROD".equalsIgnoreCase(ENVIRONMENT)){
			return;
		}
		ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
		boolean isExamRegistraionLive = dao.isConfigurationLivePost1Day("Re-sit Exam Registation");
		if(!isExamRegistraionLive){
			return;
		}

		

		dao.clearOldOnlineInitiationTransactionPeriodically();

	}


	@Scheduled(fixedDelay=60*60*1000)
	public void doAutoBookingForConflictTransactions(){

		if(!"tomcat4".equalsIgnoreCase(SERVER) || !"PROD".equalsIgnoreCase(ENVIRONMENT)){
			return;
		}

		ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
		boolean isExamRegistraionLive = dao.isConfigurationLivePost1Day("Re-sit Exam Registation");
		if(!isExamRegistraionLive){
			return;
		}
		
		int noOfConflictTransactions = 0;
		int noOfPendingRefunds = 0;
		double totalConflictAmount = 0;
		double refundDueAmount = 0;

		try{

			ArrayList<ExamBookingTransactionBean> unSuccessfulExamBookings = dao.getAllUnSuccessfulExamBookings();
			ArrayList<ExamBookingTransactionBean> successfulExamBookings = new ArrayList<>();
			ArrayList<ExamBookingTransactionBean> transactionFailedExamBookings = new ArrayList<>();

			for (int i = 0; i < unSuccessfulExamBookings.size(); i++) {
				ExamBookingTransactionBean bean = unSuccessfulExamBookings.get(i);
				String trackId = bean.getTrackId();
				XMLParser parser = new XMLParser();
				String xmlResponse = parser.queryTransactionStatus(trackId, ACCOUNT_ID, SECURE_SECRET);
				parser.parseResponse(xmlResponse, bean);
				String transactionType = bean.getTransactionType();
				String status = bean.getStatus();
				String error = bean.getError();
				String errorCode = bean.getErrorCode();

				if(("Authorized".equalsIgnoreCase(transactionType) || "Captured".equalsIgnoreCase(transactionType))&& "Processed".equalsIgnoreCase(status)){
					successfulExamBookings.add(bean);
				}else if("3".equals(errorCode) && ("Invalid Refrence No".equals(error) || "Invalid Reference No".equals(error))){
					transactionFailedExamBookings.add(bean);
				}
			}

			ArrayList<ExamBookingTransactionBean> successfulButCenterNotAvailableExamBookings = new ArrayList<>();
			ArrayList<ExamBookingTransactionBean> successfulButAlreadyBookedExamBookings = new ArrayList<>();
			for (int i = 0; i < successfulExamBookings.size(); i++) {
				ExamBookingTransactionBean bean = successfulExamBookings.get(i);
				String sapid = bean.getSapid();

				String trackId = bean.getTrackId();
				StudentBean student = dao.getSingleStudentWithValidity(sapid);
				ArrayList<ExamBookingTransactionBean> subjectsCentersList = dao.getSubjectsCentersForTrackId(trackId);
				ArrayList<String> subjectsCenters = new ArrayList<>();
				ArrayList<String> subjects = new ArrayList<>();

				for (int j = 0; j < subjectsCentersList.size(); j++) {
					subjectsCenters.add(subjectsCentersList.get(j).getSubject() 
							+"|"+subjectsCentersList.get(j).getCenterId() 
							+ "|" + subjectsCentersList.get(j).getExamDate() 
							+ "|" + subjectsCentersList.get(j).getExamTime());
					subjects.add(subjectsCentersList.get(j).getSubject());
				}

				ArrayList<String> alreadyBooked = dao.getSubjectsBookedForStudent(sapid);
				boolean subjectAlreadyBooked = false;
				for (int j = 0; j < subjectsCentersList.size(); j++) {
					if(alreadyBooked.contains(subjectsCentersList.get(j).getSubject())){
						subjectAlreadyBooked = true;
					}
				}
				if(subjectAlreadyBooked){
					successfulButAlreadyBookedExamBookings.add(bean);
					totalConflictAmount += Double.parseDouble(bean.getRespAmount());
					refundDueAmount += Double.parseDouble(bean.getRespAmount());
					noOfConflictTransactions++;
					noOfPendingRefunds++;
					continue;
				}

				boolean centerStillAvailable = checkIfCenterStillAvailable(subjectsCenters, sapid);
				if(!centerStillAvailable){
					successfulButCenterNotAvailableExamBookings.add(bean);
					totalConflictAmount += Double.parseDouble(bean.getRespAmount());
					noOfConflictTransactions++;
					continue;
				}else{
					List<ExamBookingTransactionBean> examBookings = dao.updateSeatsForConflictUsingSingleConnection(bean);
					MailSender mailSender = (MailSender)act.getBean("mailer");
					mailSender.sendBookingSummaryEmailForConflictBooking(student, examBookings, getExamCenterIdNameMap());
				}
			}

			dao.markTransactionsFailed(transactionFailedExamBookings);
			
			sc.getServletContext().setAttribute("successfulButCenterNotAvailableExamBookings", successfulButCenterNotAvailableExamBookings);
			sc.getServletContext().setAttribute("successfulButAlreadyBookedExamBookings", successfulButAlreadyBookedExamBookings);
			sc.getServletContext().setAttribute("conflictAmount", totalConflictAmount);
			sc.getServletContext().setAttribute("noOfConflictTransactions", noOfConflictTransactions);
			sc.getServletContext().setAttribute("refundDueAmount", refundDueAmount);
			sc.getServletContext().setAttribute("noOfPendingRefunds", noOfPendingRefunds);


		}catch(Exception e){
			
		}

	}

	@Scheduled(fixedDelay=360*60*1000)
	public void sendConflictEmail(){


		if(!"tomcat4".equalsIgnoreCase(SERVER) || !"PROD".equalsIgnoreCase(ENVIRONMENT)){
			return;
		}
		ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
		boolean isExamRegistraionLive = dao.isConfigurationLivePost1Day("Re-sit Exam Registation");
		if(!isExamRegistraionLive){
			return;
		}

		ArrayList<ExamBookingTransactionBean> successfulButCenterNotAvailableExamBookings = (ArrayList<ExamBookingTransactionBean>)sc.getServletContext().getAttribute("successfulButCenterNotAvailableExamBookings");
		ArrayList<ExamBookingTransactionBean> successfulButAlreadyBookedExamBookings = (ArrayList<ExamBookingTransactionBean>)sc.getServletContext().getAttribute("successfulButAlreadyBookedExamBookings");

		if(
				(successfulButCenterNotAvailableExamBookings != null && successfulButCenterNotAvailableExamBookings.size() > 0) || 
				(successfulButAlreadyBookedExamBookings != null && successfulButAlreadyBookedExamBookings.size() > 0)
				){
			MailSender mailSender = (MailSender)act.getBean("mailer");
			mailSender.sendConflictsEmail(successfulButCenterNotAvailableExamBookings, successfulButAlreadyBookedExamBookings);
		}
	}

	public Map<String, String> getExamCenterIdNameMap(){

		ExamCenterDAO dao = (ExamCenterDAO)act.getBean("examCenterDAO");
		return dao.getExamCenterIdNameMap();

	}

	private boolean checkIfCenterStillAvailable(StudentBean student, List<TimetableBean> timeTableList, ArrayList<String> selectedCenters) {
		String studentProgramStructure = student.getPrgmStructApplicable();
		Map<String, ArrayList<String>> subjectCenterIdListMap = new HashMap<String, ArrayList<String>>();
		ExamCenterDAO ecDao = (ExamCenterDAO)act.getBean("examCenterDAO");
		boolean centerStillAvailable = true;

		if("Jul2014".equals(studentProgramStructure)){
			subjectCenterIdListMap = ecDao.getAvailableCenterIDSForGivenSubjects(timeTableList);

			for (int i = 0; i < selectedCenters.size(); i++) {
				String subjectCenter = selectedCenters.get(i);
				String subject = subjectCenter.substring(0,subjectCenter.indexOf("|"));
				String centerId = subjectCenter.substring(subjectCenter.indexOf("|")+1, subjectCenter.length() );
				ArrayList<String> centerIdList = subjectCenterIdListMap.get(subject);

				if(!centerIdList.contains(centerId)){
					centerStillAvailable = false;
					break;
				}
			}

			return centerStillAvailable;
		}else{
			return true;
		}
	}
	
	
	private boolean checkIfCenterStillAvailable( ArrayList<String> selectedCenters, String sapId) {
		ExamCenterDAO ecDao = (ExamCenterDAO)act.getBean("examCenterDAO");
		boolean centerStillAvailable = true;

		List<ExamCenterBean> availableCenters = ecDao.getAvailableCentersForResitExam(sapId);

		for (int i = 0; i < selectedCenters.size(); i++) {
			String subjectCenter = selectedCenters.get(i);

			String[] data = subjectCenter.split("\\|");

			String subject = data[0];
			String centerId = data[1];
			String examDate = data[2];
			String examStartTime = data[3];
			ArrayList<String> centerIdList = new ArrayList<>();
			for (ExamCenterBean center : availableCenters) {
				if(center.getDate().equals(examDate) && center.getStarttime().equals(examStartTime)){
					centerIdList.add(center.getCenterId());
				}
			}

			if(!centerIdList.contains(centerId)){
				centerStillAvailable = false;
				break;
			}
		}
		return centerStillAvailable;
	}


	@Scheduled(fixedDelay=240*60*1000)
	public void findBookingsMismatch(){

		if(!"tomcat4".equalsIgnoreCase(SERVER) || !"PROD".equalsIgnoreCase(ENVIRONMENT)){
			return;
		}

		ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
		boolean isExamRegistraionLive = dao.isConfigurationLivePost1Day("Re-sit Exam Registation");
		if(!isExamRegistraionLive){
			return;
		}
		
		ExamCenterDAO ecDao = (ExamCenterDAO)act.getBean("examCenterDAO");
		try{

			ArrayList<ExamBookingTransactionBean> confirmedBookings = dao.getAllConfirmedBookings();
			List<ExamCenterBean> examCentersList = ecDao.getAllExamCenterSlots();

			HashMap<String, Integer> centerIdBookingsMap = new HashMap<>();

			int counter = 0;
			for (int i = 0; i < confirmedBookings.size(); i++) {
				ExamBookingTransactionBean bean = confirmedBookings.get(i);
				String centerId = bean.getCenterId();
				String examDate = bean.getExamDate();
				String examTime = bean.getExamTime();

				String key = centerId + examDate + examTime;
				if(!centerIdBookingsMap.containsKey(key)){
					centerIdBookingsMap.put(key, 1);
				}else{
					counter = centerIdBookingsMap.get(key);
					counter++;
					centerIdBookingsMap.put(key, counter);
				}
			} 

			String mismatch = "";
			ArrayList<ExamCenterBean> mismatchList = new ArrayList<>();
			for (int i = 0; i < examCentersList.size(); i++) {
				ExamCenterBean bean = examCentersList.get(i);
				String centerId = bean.getCenterId();
				String examDate = bean.getDate();
				String examTime = bean.getStarttime();
				int booked = bean.getBooked();
				String centerName = bean.getExamCenterName();
				String key = centerId + examDate + examTime;
				if(centerIdBookingsMap.containsKey(key)){
					counter = centerIdBookingsMap.get(key);

					if(booked != counter){
						mismatch = mismatch + "Center Id " + centerId + " Center: "+ centerName + " Date: "+ examDate 
								+ " Time: "+ examTime + " Students Booked: "+ counter +" Center Booked: "+ booked +  " \n";
						bean.setSlotsBooked(counter);
						mismatchList.add(bean);
					}
				}
			}
			if(mismatchList.size() > 0){
				MailSender mailSender = (MailSender)act.getBean("mailer");
				mailSender.sendBookingsMismatchEmail(mismatchList);
			}

		}catch(Exception e){
			
		}


	}

}
*/