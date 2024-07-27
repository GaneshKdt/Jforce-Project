package com.nmims.services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.nmims.beans.ExamBookingMBAWX;
import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.beans.ServiceRequestBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.daos.ExamBookingDAO;
import com.nmims.daos.ServiceRequestDAO;


/*
 * The service layer for hall preview data
 * It will communicate with the serviceRequestDAO 
 * to fetch data from the database 
 * Then populate the serviceRequestBean and return the same
*/
@Service("htServiceLayer")
public class HtServiceLayer {

	@Autowired
	private ServiceRequestDAO serviceRequestDAO;
	@Autowired
	private ExamBookingDAO examBookingDAO;

	@Autowired
	ApplicationContext act;
	
	@SuppressWarnings("null")
	public ServiceRequestBean getHallTicketData(String sapid, String mostRecentTimetablePeriod, HashMap<String, String> corporateCenterUserMapping, HashMap<String, String> programMap) {
		
		ArrayList<String> errors = new ArrayList<>();
		
		ServiceRequestBean serviceRequestBean = new ServiceRequestBean();

		List<String> blockedSapids = serviceRequestDAO.getBlockedSapids();
		
		if (blockedSapids.contains(sapid)) {
			//errors.add(" sapid is blocked ");
			errors.add(" Your Hall Ticket is on hold. Please contact NGASCE to get access for same ");
			serviceRequestBean.setError(errors);
			return serviceRequestBean;
		}
		
		StudentExamBean studentBean = serviceRequestDAO.getSingleStudentsData(sapid);

		serviceRequestBean.setStudent(studentBean);
		
		boolean isHallTicketAvailable = serviceRequestDAO.isConfigurationLive("Hall Ticket Download");
	
		if (!isHallTicketAvailable) {
			errors.add(" Hall Ticket is not available for download currently ");
			serviceRequestBean.setError(errors);
			return serviceRequestBean;
		}
		
		ArrayList<ExamBookingTransactionBean> subjectsBooked = serviceRequestDAO.getConfirmedBooking(sapid);
		if (subjectsBooked.size() == 0) {
			errors.add(" No subjects booked for Exam. Hall Ticket not available ");	
			serviceRequestBean.setError(errors); 
			return serviceRequestBean;

		}

		HashMap<String, ExamBookingTransactionBean> subjectBookingMap = new HashMap<>();

		HashMap<String, ExamBookingTransactionBean> subjectDoubleBookingMap = new HashMap<>();

		for (int i = 0; i < subjectsBooked.size(); i++) {
			ExamBookingTransactionBean bean = subjectsBooked.get(i);
			
			String key1 = bean.getSapid() + bean.getSubject();
			String key2 = bean.getSapid() + bean.getExamDate() + bean.getExamTime();
			if (!subjectDoubleBookingMap.containsKey(key1) && !subjectDoubleBookingMap.containsKey(key2)) {
				subjectDoubleBookingMap.put(key1, bean);
				subjectDoubleBookingMap.put(key2, bean);
				subjectBookingMap.put(bean.getSubject(), bean);
			} else {
				
				errors.add("Double Booking Found.");
				serviceRequestBean.setError(errors);
				return serviceRequestBean;
				
			}
		}

		serviceRequestBean.setSubjectBookingMap(subjectBookingMap);
		
		String htMonth = mostRecentTimetablePeriod.substring(0, 3);
	
		mostRecentTimetablePeriod = mostRecentTimetablePeriod.replaceAll("Dec", "December");
		mostRecentTimetablePeriod = mostRecentTimetablePeriod.replaceAll("Jun", "June");
		mostRecentTimetablePeriod = mostRecentTimetablePeriod.replaceAll("Sep", "September");
		mostRecentTimetablePeriod = mostRecentTimetablePeriod.replaceAll("Apr", "April");

		String title = "HALL TICKET for " + mostRecentTimetablePeriod;
		if (mostRecentTimetablePeriod.contains("Sep") || mostRecentTimetablePeriod.contains("Apr")) {
			title = title + " Re-Sit Term End Examination";
		} else {
			title = title + " Term End Examination";
		}
	
		serviceRequestBean.setTitle(title);

		HashMap<String, String> studentExamCenterLocation;
		
		if (corporateCenterUserMapping.containsKey(studentBean.getSapid())) {
		
			studentExamCenterLocation = serviceRequestDAO.fetchCorporateExamCenterIdNameMap();
		} else {
			studentExamCenterLocation = serviceRequestDAO.fetchExamCenterIdNameMap();
		}
		
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy");
		
		for (ExamBookingTransactionBean examBooked : subjectsBooked) {
			Date formattedDate = null;
			try {
				formattedDate = formatter.parse(examBooked.getExamDate());
			} catch (ParseException e) {
			
				
			}
			String examDate = dateFormatter.format(formattedDate);
			String examDay = new SimpleDateFormat("EEEE").format(formattedDate);
			examBooked.setExamDate(examDate);
			examBooked.setDay(examDay);
			String examStartTime = examBooked.getExamTime();
			String examEndTime = examBooked.getExamEndTime();
			examStartTime = examStartTime.substring(0, 5);
			examEndTime = examEndTime.substring(0, 5);
			examBooked.setExamTime(examStartTime);
			examBooked.setExamEndTime(examEndTime);
			
			examBooked.setAddress(studentExamCenterLocation.get(examBooked.getCenterId()));
			

		}

		List<ExamBookingTransactionBean> passwordPresent = new ArrayList<ExamBookingTransactionBean>();
		List<ExamBookingTransactionBean> passwordAbsent = new ArrayList<ExamBookingTransactionBean>();
		for (ExamBookingTransactionBean bean : subjectsBooked) {
			if (StringUtils.isBlank(bean.getPassword())) {
				passwordAbsent.add(bean);
			} else {
				passwordPresent.add(bean);
			}
		}
		String password = "";
		if (passwordAbsent.size() > 0 && passwordPresent.size() > 0) {
			password = passwordPresent.get(0).getPassword();
			String month = passwordPresent.get(0).getMonth();
			String year = passwordPresent.get(0).getYear();
			examBookingDAO.assignPass(sapid, password, month, year);
		}

		if (passwordAbsent.size() > 0 && passwordPresent.size() == 0) {
			String month = passwordAbsent.get(0).getMonth();
			String year = passwordAbsent.get(0).getYear();
			password = generateRandomPass(sapid);
			examBookingDAO.assignPass(sapid, password, month, year);
		}

		if (passwordAbsent.size() == 0 && passwordPresent.size() > 0) {
			password = passwordPresent.get(0).getPassword();

		}
		
		serviceRequestBean.setPassword(password);
		serviceRequestBean.setExamBookedList(subjectsBooked);
		
		boolean htDownloadedStatus = serviceRequestDAO.hallTicketDownloadedStatus(htMonth, sapid);

		serviceRequestBean.setHtDownloadStatus( htDownloadedStatus);
		
		String studentProgramAbbrevation = studentBean.getProgram();
		
		String programFullName = programMap.get(studentProgramAbbrevation);

		serviceRequestBean.setProgramFullName(programFullName);

		serviceRequestBean.setExamination(mostRecentTimetablePeriod);
		
		return serviceRequestBean;
	}
	
	@SuppressWarnings("null")
	public ServiceRequestBean getHallTicketDataForMbaWx(String sapid,  HashMap<String, String> programMap) {
		
		ArrayList<String> errors = new ArrayList<>();
		
		ServiceRequestBean serviceRequestBean = new ServiceRequestBean();

		List<String> blockedSapids = serviceRequestDAO.getBlockedSapids();
		
		if (blockedSapids.contains(sapid)) {
			//errors.add(" sapid is blocked ");
			errors.add(" Your Hall Ticket is on hold. Please contact NGASCE to get access for same ");
			serviceRequestBean.setError(errors);
			return serviceRequestBean;
		}
		
		StudentExamBean studentBean = serviceRequestDAO.getSingleStudentsData(sapid);

		serviceRequestBean.setStudent(studentBean);
		
		boolean isHallTicketAvailable = serviceRequestDAO.isConfigurationLive("Hall Ticket Download");
	
		if (!isHallTicketAvailable) {
			errors.add(" Hall Ticket is not available for download currently ");
			serviceRequestBean.setError(errors);
			return serviceRequestBean;
		}
		ExamBookingDAO eDao = (ExamBookingDAO) act.getBean("examBookingDAO");
		ArrayList<ExamBookingMBAWX> subjectsBookedMbaBean = eDao.getConfirmedBookingForMBAWx(sapid);
		if (subjectsBookedMbaBean.size() == 0) {
			errors.add(" No subjects booked for Exam. Hall Ticket not available ");	
			serviceRequestBean.setError(errors); 
			return serviceRequestBean;

		}
		
		HashMap<String, ExamBookingMBAWX> subjectBookingMap = new HashMap<>();
		for (int i = 0; i < subjectsBookedMbaBean.size(); i++) {
			ExamBookingMBAWX bean = subjectsBookedMbaBean.get(i);
			subjectBookingMap.put(bean.getTimeboundId(), bean);
		} 
		
		HashMap<String, ExamBookingMBAWX> subjectDoubleBookingMap = new HashMap<>();

		for (int i = 0; i < subjectsBookedMbaBean.size(); i++) {
			ExamBookingMBAWX bean = subjectsBookedMbaBean.get(i);
			
			String key1 = bean.getSapid() + bean.getTimeboundId();
			String key2 = bean.getSapid() + bean.getExamDate() + bean.getExamStartTime();
			if (!subjectDoubleBookingMap.containsKey(key1) && !subjectDoubleBookingMap.containsKey(key2)) {
				subjectDoubleBookingMap.put(key1, bean);
				subjectDoubleBookingMap.put(key2, bean);
				subjectBookingMap.put(bean.getTimeboundId(), bean);
			} else {
				errors.add(" subjectDoubleBookingMap doesnt contain keys : "+key1+" and "+key2);
				serviceRequestBean.setError(errors);
				return serviceRequestBean;
				
			}
		}
		serviceRequestBean.setSubjectBookingMapMbaWx(subjectBookingMap);
		ExamBookingMBAWX sbean = subjectsBookedMbaBean.get(0);
		String mostRecentTimetablePeriod = sbean.getExamMonth()+"-"+sbean.getExamYear();
		String htMonth = mostRecentTimetablePeriod.substring(0, 3);
		
		mostRecentTimetablePeriod = mostRecentTimetablePeriod.replaceAll("Dec", "December");
		mostRecentTimetablePeriod = mostRecentTimetablePeriod.replaceAll("Jun", "June");
		mostRecentTimetablePeriod = mostRecentTimetablePeriod.replaceAll("Sep", "September");
		mostRecentTimetablePeriod = mostRecentTimetablePeriod.replaceAll("Apr", "April");
		
		String title = "HALL TICKET for " + mostRecentTimetablePeriod;
		if (mostRecentTimetablePeriod.contains("Sep") || mostRecentTimetablePeriod.contains("Apr")) {
			title = title + " Re-Sit Term End Examination";
		} else {
			title = title + " Term End Examination";
		}
		
		serviceRequestBean.setTitle(title);
		
		HashMap<String, String> studentExamCenterLocation;
		
		studentExamCenterLocation = serviceRequestDAO.fetchExamCenterIdNameMap();
		
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy");
		
		for (ExamBookingMBAWX examBooked : subjectsBookedMbaBean) {
			Date formattedDate = null;
			try {
				formattedDate = formatter.parse(examBooked.getExamDate());
			} catch (ParseException e) {
			
				
			}
			String subject = eDao.getSubjectByTimeboundId(examBooked.getTimeboundId());
			String examDate = dateFormatter.format(formattedDate);
			String examDay = new SimpleDateFormat("EEEE").format(formattedDate);
			examBooked.setExamDate(examDate);
			//examBooked.setDay(examDay);
			String examStartTime = examBooked.getExamStartTime();
			String examEndTime = examBooked.getExamEndTime();
			examStartTime = examStartTime.substring(0, 5);
			examEndTime = examEndTime.substring(0, 5);
			examBooked.setExamStartTime(examStartTime);
			examBooked.setExamEndTime(examEndTime);
			examBooked.setAddress(studentExamCenterLocation.get(examBooked.getCenterId()));
			examBooked.setSubject(subject);
		}
		
		serviceRequestBean.setExamBookedListMbaWx(subjectsBookedMbaBean);
		
		boolean htDownloadedStatus = serviceRequestDAO.hallTicketDownloadedStatusMbaWx(htMonth, sapid);
		
		serviceRequestBean.setHtDownloadStatus( htDownloadedStatus);
		
		String studentProgramAbbrevation = studentBean.getProgram();
		
		String programFullName = programMap.get(studentProgramAbbrevation);
		
		serviceRequestBean.setProgramFullName(programFullName);
		
		serviceRequestBean.setExamination(mostRecentTimetablePeriod);
		
		return serviceRequestBean; 
	}
	public String generateRandomPass(String sapid) {
		String generatedString =null;

		try {
			int randomNum = ThreadLocalRandom.current().nextInt(10, 99 + 1);
			String FirstString = String.valueOf(randomNum);
			String SecondString = RandomStringUtils.randomNumeric(8);
			generatedString = FirstString + SecondString;
		} catch (Exception e) {
			
			return generatedString;
		}
	
	return generatedString;
	}
}
