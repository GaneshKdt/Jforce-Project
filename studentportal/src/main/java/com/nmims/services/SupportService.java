package com.nmims.services;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.nmims.beans.MBAResponseBean;
import com.nmims.beans.MBAWXPortalExamResultForSubject;
import com.nmims.beans.MBAWXPortalExamResultsBean;
import com.nmims.beans.StudentsTestDetailsStudentPortalBean;
import com.nmims.beans.TimeboundExamBookingBean;
import com.nmims.daos.SupportDao;
import com.nmims.exam.client.ITimeboundExamResultsClient;
import com.nmims.helpers.AESencrp;
import com.nmims.util.ContentUtil;

@Service("supportService")
public class SupportService implements ISupportService{
	
	@Autowired
	SupportDao supportDao;

	private ArrayList<String> ugPrograms = new ArrayList<String>(Arrays.asList("BBA","B.Com","BBA-BA")); 
	private final static String applicableSem = "1";
	private final static String applicableAcadcycle = "Jul2022";
	private final static String bcomProgram = "B.Com";
	private final static String bcomProgramCourse = "Certificate Program in General Management";
	private final static String bbaProgramCourse = "Certificate in Business Administration";
	
	@Autowired
	private ITimeboundExamResultsClient timeboundExamResultsClient;
	
	private static final Logger logger = LoggerFactory.getLogger(SupportService.class);
	
	/**
	 * 
	 * @param program - Student's program
	 * @param sem - sem should be 1
	 * @param month - Student's reg month 
	 * @param year - student's reg year
	 * @param sapid 
	 * @return boolean - If the condition satisfy , which is sem should be 1 and month/year should be Jul 2022
	 *  				and for only program bcom and bba. Hence return true or else false (not applicable)
	 */
	@Override
	public boolean checkStudentUgOrNot(String program,String sem,String month,String year,String sapid) {
		if(ugPrograms.contains(program) && sem.equalsIgnoreCase(applicableSem) && applicableAcadcycle.equalsIgnoreCase(month+year)) {
			try {
				if(supportDao.checkStudentHasGivenConsent(sapid) == 0)
					return true;
			}catch(Exception e) {
				
			}
		}	
		return false;
	}
	
	@Override
	public void insertUgConsentForm(String optionId,String sapid) {
		supportDao.addUgConsentOption(optionId, sapid);
	}
	
	public String getCourseName(String program) {
		try {
			if(program.equalsIgnoreCase(bcomProgram))
				return bcomProgramCourse;
			else
				return bbaProgramCourse;
		}catch(Exception e) {
		
		}
		return "";	
	}
	
	@Override
	public boolean validateStudentMatrixSalesforceToken(String sapid, String token) throws Exception {
		final String key = "VDVlOGVadFNXJXJUKUswIw==";

		if (StringUtils.isBlank(decodeTokenByReplacingPlus(token)))
			return false;

		String salesforceDecryptToken =  getSalesforceDecryptToken(token, key);
		
		if (StringUtils.isBlank(salesforceDecryptToken))
			return false;

		if ((System.currentTimeMillis() - Long.valueOf(salesforceDecryptToken.split("~")[1])) > 100000
				|| (!salesforceDecryptToken.split("~")[0].equalsIgnoreCase(sapid)))
			return false;

		return true;
	}//validateStudentMatrixSalesforceToken(-,-)

	@SuppressWarnings("static-access")
	private String getSalesforceDecryptToken(String token, String key) throws Exception {
		return new AESencrp().decryptSalesforce(token, key);
	}

	private String decodeTokenByReplacingPlus(String token) throws Exception{
		return java.net.URLDecoder.decode(token, "UTF-8").replaceAll(" ", "+");
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TimeboundExamBookingBean> fetchTimeboundExamBookindRecords(String sapId){
		logger.info("SupportService.fetchTimeboundExamBookindRecords() - START");
		List<TimeboundExamBookingBean> bookingsList = new ArrayList<>();

		try {
			ResponseEntity<MBAResponseBean> examBookingList = timeboundExamResultsClient.getStudentAllExamBookings(sapId);
			logger.info("Exam Booking API Result:"+examBookingList.getBody().getStatus());
			if("success".equals(examBookingList.getBody().getStatus())) 
				bookingsList = (List<TimeboundExamBookingBean>) examBookingList.getBody().getResponse();
		} catch (Exception e) {
			logger.error("Error while fetching Exam Booking Records of student: {} for Student Metrics Details, Exception thrown: {}", sapId, e.toString());
		}
		logger.info("SupportService.fetchTimeboundExamBookindRecords() - END");
		return bookingsList;
	}//fetchTimeboundExamBookindRecords(-)
	
	@Override
	public List<MBAWXPortalExamResultForSubject> getSubjectWiseStudentMarksRecord(String sapid) {
		logger.info("SupportService.getSubjectWiseStudentMarksRecord() - START");
		List<MBAWXPortalExamResultForSubject> markRecordsList = new ArrayList<MBAWXPortalExamResultForSubject>();
		
		try {
			ResponseEntity<MBAWXPortalExamResultsBean> subjectWiseMarksList = timeboundExamResultsClient.getSubjectWiseStudentMarksRecord(sapid);
			logger.info("Subject Wise / TEE marks history API Status:"+subjectWiseMarksList.getBody().getStatus());
			if("success".equals(subjectWiseMarksList.getBody().getStatus())) 
				markRecordsList = subjectWiseMarksList.getBody().getData();
		} catch (Exception e) {
			logger.error("Error while fetching Subject wise marks or TEE Marks History of student: {} for Student Metrics Details, Exception thrown: {}", sapid, e.toString());
		}
		logger.info("SupportService.getSubjectWiseStudentMarksRecord() - END");
		return markRecordsList;
		
	}
	
	@Override
	public List<MBAWXPortalExamResultForSubject> getStudentPassFailRecords(String sapid) {
		logger.info("SupportService.getStudentPassFailRecords() - START");
		List<MBAWXPortalExamResultForSubject> passFailList = new ArrayList<MBAWXPortalExamResultForSubject>(); 
		
		try {
			ResponseEntity<MBAWXPortalExamResultsBean> passFailRecordsList = timeboundExamResultsClient.getStudentPassFailRecords(sapid);
			logger.info("Status of Pass-Fail API is:"+passFailRecordsList.getBody().getStatus());
			if("success".equals(passFailRecordsList.getBody().getStatus())) 
				passFailList = passFailRecordsList.getBody().getData();
		} catch (Exception e) {
			logger.error("Error while fetching Pass-Fail records of student: {} for Student Metrics Details, Exception thrown: {}", sapid, e.toString());
		}
		logger.info("SupportService.getStudentPassFailRecords() - END");
		return passFailList;
	}

	@Override
	public List<StudentsTestDetailsStudentPortalBean> fetchAllAttemptedIADetails(String sapid) throws SQLException {
		
		List<String> allApplicableTimeboundIds = supportDao.getAllapplicableTimeboundIds(sapid);
		
		String timeboundIds = ContentUtil.frameINClauseString(allApplicableTimeboundIds);
		
		List<StudentsTestDetailsStudentPortalBean> allApplicableIADetails = supportDao.getIAScoresForStudentSubject(sapid, timeboundIds);
		
		return allApplicableIADetails;
	}

	@Override
	public String getToken(String student_No) throws InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
		Date date = new Date();
	    long currentTimeInMiliseconds = date.getTime();
	    String data = student_No + "~" + currentTimeInMiliseconds;
		String token = AESencrp.encryptSalesforce(data);
	    return token;
	}
	
	
}
