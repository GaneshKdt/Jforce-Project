package com.nmims.services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.nmims.beans.StudentExamBean;
import com.nmims.daos.ExamBookingDAO;
import com.nmims.helpers.AESencrp;

@Service
public class ExamBookingStudentService {

	@Autowired
	ApplicationContext act;
	
	public SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");

	public boolean isExamRegistraionLive(String sapid, String sapIdEncrypted) {
		// Block to check if reg is live for this particular cycle
		ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
//		boolean isExamRegistraionLive = eDao.isConfigurationLive("Exam Registration");
		boolean isExamRegistraionLive = eDao.isBookingLiveInStudentCycleSubjectConfig(sapid);
		
		StudentExamBean student = eDao.getSingleStudentsData(sapid);

		String sapIdFromURL = null;
		try {
			if(sapIdEncrypted != null){
				sapIdFromURL = AESencrp.decrypt(sapIdEncrypted);
			}
		} catch (Exception e) {
			
		}
		// checking if system.now is between extended date time after exam registration End allowed to book seat 
		boolean isExtendedExamRegistrationLive = eDao.isExtendedExamRegistrationConfigurationLive("Exam Registration");

		if(sapIdFromURL != null && sapid.equals(sapIdFromURL) && isExtendedExamRegistrationLive){
			//If additional encrypted parameter is sent in URL, then allow to book after end date as well.
			isExamRegistraionLive = true;
		}
		try {
			if (isStudentValid(student, sapid)) {
				
				if(!isExamRegistraionLive){
					throw new Exception("Exam Registration is not Live Currently");
				}
			}
			else{
				throw new Exception("Your validity date is expired.");
			}
			
		} catch (Exception e) {
			
		}
				
		return isExamRegistraionLive;
	}
	
	//Check Student validity for disable Exam Registration link after validity expired  
	private boolean isStudentValid(StudentExamBean student, String userId) throws ParseException {
		String date = "";
		String validityEndMonthStr = student.getValidityEndMonth();
		int validityEndYear = Integer.parseInt(student.getValidityEndYear());
		
		String enrollmentMonth = student.getEnrollmentMonth();
		int enrollmentYear = Integer.parseInt(student.getEnrollmentYear());

		Date lastAllowedAcccessDate = null;
		Date enrollmentDate = null;
		String register = student.getRegDate();
		int validityEndMonth = 0;
		if("Jun".equals(validityEndMonthStr)){
			validityEndMonth = 6;
			date = validityEndYear + "/" + validityEndMonth + "/" + "30";
			lastAllowedAcccessDate = formatter.parse(date);
		}else if("Dec".equals(validityEndMonthStr)){
			validityEndMonth = 12;
			date = validityEndYear + "/" + validityEndMonth + "/" + "31";
			lastAllowedAcccessDate = formatter.parse(date);
		}else if("Feb".equals(validityEndMonthStr)){
			validityEndMonth = 2;
			date = validityEndYear + "/" + validityEndMonth + "/" + "28";
			lastAllowedAcccessDate = formatter.parse(date);
		}else if("Mar".equals(validityEndMonthStr)){
			validityEndMonth = 3;
			date = validityEndYear + "/" + validityEndMonth + "/" + "31";
			lastAllowedAcccessDate = formatter.parse(date);
		}else if("Apr".equals(validityEndMonthStr)){
			validityEndMonth = 4;
			date = validityEndYear + "/" + validityEndMonth + "/" + "30";
			lastAllowedAcccessDate = formatter.parse(date);
		}else if("Aug".equals(validityEndMonthStr)){
			validityEndMonth = 8;
			date = validityEndYear + "/" + validityEndMonth + "/" + "31";
			lastAllowedAcccessDate = formatter.parse(date);
		}else if("Sep".equals(validityEndMonthStr)){
			validityEndMonth = 9;
			date = validityEndYear + "/" + validityEndMonth + "/" + "30";
			lastAllowedAcccessDate = formatter.parse(date);
		}else if("Oct".equals(validityEndMonthStr)){
			validityEndMonth = 10;
			date = validityEndYear + "/" + validityEndMonth + "/" + "31";
			lastAllowedAcccessDate = formatter.parse(date);
		}else if("Jan".equals(validityEndMonthStr)){
			validityEndMonth = 1;
			date = validityEndYear + "/" + validityEndMonth + "/" + "31";
			lastAllowedAcccessDate = formatter.parse(date);
		}else if("May".equals(validityEndMonthStr)){
			validityEndMonth = 5;
			date = validityEndYear + "/" + validityEndMonth + "/" + "31";
			lastAllowedAcccessDate = formatter.parse(date);
		}else if("Jul".equals(validityEndMonthStr)){
			validityEndMonth = 7;
			date = validityEndYear + "/" + validityEndMonth + "/" + "31";
			lastAllowedAcccessDate = formatter.parse(date);
		}

		Calendar now = Calendar.getInstance();
		int currentYear = now.get(Calendar.YEAR);
		int currentMonth = (now.get(Calendar.MONTH) + 1);
		
		if(currentYear < validityEndYear  ){
			return true;
		}else if(currentYear == validityEndYear && currentMonth <= validityEndMonth){
			return true;
		}
			return false;
	}
}
