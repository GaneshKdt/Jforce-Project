package com.nmims.services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.nmims.beans.MBAExamBookingRequest;
import com.nmims.beans.MBAHallTicketBean;
import com.nmims.beans.MBAStudentDetailsBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.daos.MBAStudentDetailsDAO;
import com.nmims.daos.MBAXExamBookingDAO;
import com.nmims.daos.MBAXHallTicketDAO;
import com.nmims.helpers.HallTicketPDFCreatorMBAX;

@Component
public class HtServiceLayerMBAX {

	@Value("${SERVER_PATH}")
	private String SERVER_PATH;

	@Value("${CURRENT_MBAX_ACAD_MONTH}")
	private String CURRENT_MBAX_ACAD_MONTH; 
	
	@Value("${CURRENT_MBAX_ACAD_YEAR}")
	private String CURRENT_MBAX_ACAD_YEAR;
	
	@Autowired
	private MBAStudentDetailsDAO studentDetailsDAO;
	
	@Autowired
	private MBAXExamBookingDAO examBookingDAO;
	
	@Autowired
	private MBAXHallTicketDAO hallTicketDAO;

	@Autowired
	private HallTicketPDFCreatorMBAX hallTicketCreator;
	
	public MBAHallTicketBean getHallTicketData(String sapid,  HashMap<String, String> programMap) {
		return generateHallTicket(sapid, programMap); 
	}
	
	public MBAHallTicketBean createHallTicketDownload(String sapid,  HashMap<String, String> programMap) {
		MBAHallTicketBean hallTicketBean = generateHallTicket(sapid, programMap);
		if("success".equals(hallTicketBean.getStatus())) {
			try {
				String url = hallTicketCreator.createHallTicket(hallTicketBean);
				url = url.replace("HallTicket", "hallticket");
				url = url.split(":/")[1];
				url = SERVER_PATH + url;
				hallTicketBean.setDownloadURL(url);
				hallTicketBean.setStatus("success");
			} catch (Exception e) {
				hallTicketBean.setErrorMessage("Error generating Hall Ticket for download."); 
				hallTicketBean.setStatus("failure");
				
			}
		}
		return hallTicketBean;
	}

	private MBAHallTicketBean generateHallTicket(String sapid , HashMap<String, String> programMap) {

		MBAHallTicketBean hallTicketBean = new MBAHallTicketBean();
		try {
			StudentExamBean studentBean = hallTicketDAO.getSingleStudentsData(sapid);
			hallTicketBean.setStudent(studentBean);
			boolean isHallTicketAvailable = hallTicketDAO.checkIfHallTicketDownloadActive(sapid);
			if (!isHallTicketAvailable) {
				hallTicketBean.setErrorMessage("Hall Ticket is not available for download currently.");
				hallTicketBean.setStatus("failure");
				return hallTicketBean;
			}

			MBAStudentDetailsBean studentDetails = studentDetailsDAO.getTimeboundDetailsForStudentForMonthYear(sapid, CURRENT_MBAX_ACAD_MONTH, CURRENT_MBAX_ACAD_YEAR);
			List<MBAExamBookingRequest> bookingList = examBookingDAO.getAllStudentBookings(studentDetails);
			if (bookingList.size() == 0) {	
				hallTicketBean.setErrorMessage("No subjects booked for Exam. Hall Ticket not available."); 
				hallTicketBean.setStatus("failure");
				return hallTicketBean;
			}
			
			Map<String, MBAExamBookingRequest> subjectBookingMap = new HashMap<String, MBAExamBookingRequest>();
			for (int i = 0; i < bookingList.size(); i++) {
				MBAExamBookingRequest bean = bookingList.get(i);
				subjectBookingMap.put(bean.getTimeboundId(), bean);
			}

			Map<String, MBAExamBookingRequest> subjectDoubleBookingMap = new HashMap<String, MBAExamBookingRequest>();

			for (MBAExamBookingRequest bean : bookingList) {
				String key1 = bean.getSapid() + bean.getTimeboundId();
				String key2 = bean.getSapid() + bean.getExamStartDateTime();
				
				if (!subjectDoubleBookingMap.containsKey(key1) && !subjectDoubleBookingMap.containsKey(key2)) {
					subjectDoubleBookingMap.put(key1, bean);
					subjectDoubleBookingMap.put(key2, bean);
					subjectBookingMap.put(bean.getTimeboundId(), bean);
				} else {
					hallTicketBean.setErrorMessage("Error Generating Hall Ticket : Double bookings found!");
					hallTicketBean.setStatus("failure");
					return hallTicketBean;
				}
			}
//			hallTicketBean.setSubjectBookingMap(subjectBookingMap);
			
			MBAExamBookingRequest sbean = bookingList.get(0);
			String mostRecentTimetablePeriod = getFullMonthName(sbean.getMonth())+"-"+sbean.getYear();
			
			String title = "HALL TICKET for " + mostRecentTimetablePeriod;
			
			hallTicketBean.setTitle(title);
			
			for (MBAExamBookingRequest booking : bookingList) {
				formatDateTimeForBooking(booking);
			}
			hallTicketBean.setExamBookings(bookingList);
			
			boolean htDownloadedStatus = hallTicketDAO.hallTicketDownloadedStatus(sapid);
			hallTicketBean.setHtDownloadStatus(htDownloadedStatus);
			
			String studentProgramAbbrevation = studentBean.getProgram();
			String programFullName = programMap.get(studentProgramAbbrevation);

			hallTicketBean.setYear(sbean.getYear());
			hallTicketBean.setMonth(sbean.getMonth());
			hallTicketBean.setProgramFullName(programFullName);
			hallTicketBean.setExamination(mostRecentTimetablePeriod);
			hallTicketBean.setStatus("success");
		}catch (Exception e) {
			
			hallTicketBean.setErrorMessage("Error generating hall ticket.");
			hallTicketBean.setStatus("failure");
		}
		return hallTicketBean;
	}

	
	private void formatDateTimeForBooking(MBAExamBookingRequest booking) {
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy");
		SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");
		SimpleDateFormat dayFormatter = new SimpleDateFormat("EEEE");
		
		try {
			Date examStartDateObject = formatter.parse(booking.getExamStartDateTime());
			Date examEndDateObject = formatter.parse(booking.getExamEndDateTime());
			
			String examDay = dayFormatter.format(examStartDateObject);
			String examDate = dateFormatter.format(examStartDateObject);
			String examStartTime = timeFormatter.format(examStartDateObject);
			String examEndTime = timeFormatter.format(examEndDateObject);
			
			examStartTime = examStartTime.substring(0, 5);
			examEndTime = examEndTime.substring(0, 5);

			booking.setExamDay(examDay);
			booking.setExamDate(examDate);
			booking.setExamStartTime(examStartTime);
			booking.setExamEndTime(examEndTime);
		} catch (ParseException e) {
			
		}
	}
	
	private String getFullMonthName(String month) throws ParseException {
		Date date = new SimpleDateFormat("MMM").parse(month);
		String monthName = new SimpleDateFormat("MMMM").format(date);
		return monthName;
	}
}
