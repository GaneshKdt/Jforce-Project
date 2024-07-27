package com.nmims.helpers;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.nmims.beans.AssignmentFileBean;
import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.beans.ExamCenterBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.TimetableBean;
import com.nmims.controllers.AssignmentSubmissionController;
import com.nmims.daos.ExamBookingDAO;
import com.nmims.daos.ExamCenterDAO;
import com.nmims.daos.ProjectSubmissionDAO;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.services.ProjectStudentEligibilityService;

@Component
public class ExamBookingHelper {
	@Autowired
	HallTicketPDFCreator hallTicketCreator;

	@Autowired
	ExamBookingPDFCreator examFeeReceiptCreator;
	
	@Autowired
	ApplicationContext act;
	
	@Value("${MARKSHEETS_PATH}")
	private String MARKSHEETS_PATH;

	@Value("${FEE_RECEIPT_PATH}")
	private String FEE_RECEIPT_PATH;
	
	@Value("${HALLTICKET_PATH}")
	private String HALLTICKET_PATH;
	
	@Value("${STUDENT_PHOTOS_PATH}")
	private String STUDENT_PHOTOS_PATH;
	
	@Autowired
	ProjectSubmissionDAO projectSubmissionDAO;
	
	@Autowired
	FileUploadHelper fileuploadHelper;
	
	@Autowired
	AmazonS3Helper amazonS3;
	
	@Autowired
	ProjectStudentEligibilityService eligibilityService;
	
	private static final Logger logger = LoggerFactory.getLogger(AssignmentSubmissionController.class);
	private static final Logger projectSubmissionLogger = LoggerFactory.getLogger("projectSubmission");
	@Value( "${MARKSHEET_BUCKENAME}" )
	private String 	MARKSHEET_BUCKENAME;
	
	private final static String base_ReceiptPath = "FeeReceipts/";

	private final static String base_HallticketPath = "HallTicket/";
	
	public void createFeeReceiptAndEntryInReceiptHallTicketTable(String sapid,boolean isCorporate){

		String year=null, month=null, fileName="";
		ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
		ExamCenterDAO ecDao = (ExamCenterDAO)act.getBean("examCenterDAO");
		
		StudentExamBean student = dao.getSingleStudentWithValidity(sapid);
		List<ExamBookingTransactionBean> examBookings = dao.getConfirmedBooking(sapid);
		List<ExamBookingTransactionBean> confirmedOrReleasedExamBookings = dao.getConfirmedOrRelesedBooking(sapid);
		
		HashMap<String, String> getExamCenterIdNameHashMap = new HashMap<String,String>();
		if(examBookings!=null && examBookings.size()>0){
			year = examBookings.get(0).getYear();
			month = examBookings.get(0).getMonth();
		}
		try{
			if(isCorporate){
				getExamCenterIdNameHashMap = ecDao.getCorporateExamCenterIdNameMap();
				fileName = examFeeReceiptCreator.createPDF(examBookings, getExamCenterIdNameHashMap, FEE_RECEIPT_PATH, student, confirmedOrReleasedExamBookings);
			}else{
				getExamCenterIdNameHashMap = ecDao.getExamCenterIdNameMap();
				fileName = examFeeReceiptCreator.createPDF(examBookings, getExamCenterIdNameHashMap, FEE_RECEIPT_PATH, student, confirmedOrReleasedExamBookings);
			}
			
			//Upload local receipt file to s3
			String response = fileuploadHelper.uploadDocument(fileName, base_ReceiptPath,MARKSHEET_BUCKENAME ); 
			
			if(response.length() > 0)
				fileName = fileName.substring(15);
			
			dao.insertDocumentRecord(fileName, year, month, sapid, "Exam Fee Receipt");
		}catch(Exception e){
			
		}



	}
	public void createHallTicketAndEntryInReceiptHallTicketTable(String sapid,boolean isCorporate){
		ArrayList<String> subjects = new ArrayList<>();
		String year = null,month = null;
		ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		ExamCenterDAO ecDao = (ExamCenterDAO)act.getBean("examCenterDAO");
		
		HashMap<String, String> getProgramMap = dao.getProgramDetails();
		HashMap<String, ExamCenterBean> getExamCenterCenterDetailsMap = new HashMap<String, ExamCenterBean>();
		
		String getMostRecentTimetablePeriod = dao.getMostRecentTimeTablePeriod();
		ArrayList<ExamBookingTransactionBean> subjectsBooked = eDao.getConfirmedBooking(sapid);
		StudentExamBean student = eDao.getSingleStudentWithValidity(sapid);
		
		HashMap<String, ExamBookingTransactionBean> subjectBookingMap = new HashMap<>();
		getExamCenterCenterDetailsMap = ecDao.getExamCenterCenterDetailsMap(isCorporate);
		
		
		for (int i = 0; i < subjectsBooked.size(); i++) {
			ExamBookingTransactionBean bean = subjectsBooked.get(i);
			subjectBookingMap.put(bean.getSubject(), bean);
		}

		if(subjectsBooked!=null && subjectsBooked.size()>0){
			year = subjectsBooked.get(0).getYear();
			month = subjectsBooked.get(0).getMonth();
		}
		for (int i = 0; i < subjectsBooked.size(); i++) {
			subjects.add(subjectsBooked.get(i).getSubject());
		}
		
		List<ExamBookingTransactionBean> passwordPresent = new ArrayList<ExamBookingTransactionBean>();
		List<ExamBookingTransactionBean> passwordAbsent = new ArrayList<ExamBookingTransactionBean>();
		for(ExamBookingTransactionBean bean : subjectsBooked){
			if(StringUtils.isBlank(bean.getPassword())){
				passwordAbsent.add(bean);
			}else{
				passwordPresent.add(bean);
			}
		}
		String password = "";
		if(passwordAbsent.size()>0 && passwordPresent.size()>0 ){
			password = passwordPresent.get(0).getPassword();
			
			eDao.assignPass(sapid,password,month,year);
		}
		
		if(passwordAbsent.size()>0 && passwordPresent.size()==0 ){
	
		password = generateRandomPass(sapid);
			eDao.assignPass(sapid,password,month,year);
		}
		
		if(passwordAbsent.size()==0 && passwordPresent.size()>0 ){
			password = passwordPresent.get(0).getPassword();
		}
		
		for (int i = 0; i < subjectsBooked.size(); i++) {
			subjects.add(subjectsBooked.get(i).getSubject());
		}
		

		List<TimetableBean> timeTableList = eDao.getTimetableListForGivenSubjects(subjects, student);
		String fileName = "";
		try{
			fileName = hallTicketCreator.createHallTicket(timeTableList, subjectsBooked, getProgramMap, student, 
					HALLTICKET_PATH, getMostRecentTimetablePeriod,getExamCenterCenterDetailsMap, 
					subjectBookingMap, STUDENT_PHOTOS_PATH,password);
					
			//Upload local receipt file to s3
			String response = fileuploadHelper.uploadDocument(fileName, base_HallticketPath,MARKSHEET_BUCKENAME );
			
			if(response.length() > 0)
				fileName = fileName.substring(14);
			
			eDao.insertDocumentRecord(fileName, year, month, sapid, "Hall Ticket"); 	
			

		}catch(Exception e){
			
		}

	}
	
	@Async
	public void createAndUploadHallTicketAndExamFeeReceipt(String sapid,boolean isCorporate){

		createFeeReceiptAndEntryInReceiptHallTicketTable(sapid,isCorporate);
		createHallTicketAndEntryInReceiptHallTicketTable(sapid,isCorporate);

	}
	
	public String generateRandomPass(String sapid) {
		String generatedString =null;
		try {
			int randomNum = ThreadLocalRandom.current().nextInt(10, 99 + 1);
			String FirstString=  String.valueOf(randomNum);
			String SecondString = RandomStringUtils.randomNumeric(8);
			generatedString = FirstString+SecondString;
		}
		catch(Exception e) {
			
			return generatedString;
		}
	
	return generatedString;
	}
	public String createAndUploadAssignmentFeeReceipt(String sapid, String trackId) {
		
		String year=null, month=null, fileName="",folderName="";;
		
		ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO"); 
		StudentExamBean student = new StudentExamBean();
		List<ExamBookingTransactionBean> examBookings = new ArrayList<ExamBookingTransactionBean>();
		
		
		try {
			
			student = dao.getSingleStudentWithValidity(sapid); 
			examBookings = dao.getConfirmedAssignmentBooking(sapid,trackId);
			
		} catch (Exception e1) { 
			projectSubmissionLogger.info("Error. Filed to get Project Booking Details. "+e1.getMessage());
		}
	    
		if(examBookings!=null && examBookings.size()>0){
			year = examBookings.get(0).getYear();
			month = examBookings.get(0).getMonth();  
		}
		
		try{ 
			//generate and save the file in local storage
			fileName = examFeeReceiptCreator.createAssignmentPDF(examBookings, FEE_RECEIPT_PATH, student);
			folderName=FEE_RECEIPT_PATH+fileName;
			
			//moving file from local to Amazon S3 storage
			String s3FileName = uploadFeeReceiptInS3(folderName);
			if(s3FileName.equalsIgnoreCase("error")) {
				logger.info("Error. Failed to insert in S3");
				return "error";
			}
			//delete file saved in local 
			if(folderName!=null) {
				File file = new File(folderName);
		        boolean isDeleted = file.delete();
			}
			dao.insertDocumentRecord(fileName, year, month, sapid, "Assignment Fee Receipt",trackId);
		
		}catch(Exception e){
			logger.info(e.getMessage());
			return "error";
		}
		
		return fileName;
		
	}
	public String createAndUploadProjectFeeReceipt(String sapid,String trackId, String subject) {
		
		String year=null, month=null, folderName="", endDate="",fileName="";
		StudentExamBean student = new StudentExamBean();
		List<ExamBookingTransactionBean> examBookings = new ArrayList<ExamBookingTransactionBean>();
		List<ExamBookingTransactionBean> confirmedOrReleasedProjectExamBookings = new ArrayList<ExamBookingTransactionBean>();
		HashMap<String,String> getCorporateExamCenterIdNameMap = new HashMap<String,String>();
		AssignmentFileBean projectFile = new AssignmentFileBean();
		
		ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
		ExamCenterDAO examCenterDao = (ExamCenterDAO)act.getBean("examCenterDAO");
		
		try {
			
			student = dao.getSingleStudentWithValidity(sapid);
			
			// Set Student Applicable Exam Mont Year
			
//			examBookings = dao.getConfirmedProjectBooking(sapid); // Commented to make two cycle live
			String method = "createAndUploadProjectFeeReceipt()";
			AssignmentFileBean examMonthYearBean = eligibilityService.getStudentApplicableExamMonthYear(sapid, subject, method);
			examBookings = eligibilityService.getConfirmedProjectBookingApplicableCycle(sapid, examMonthYearBean.getMonth(), examMonthYearBean.getYear());
			
			confirmedOrReleasedProjectExamBookings = dao.getConfirmedOrReleasedProjectExamBookings(sapid,trackId);
			
			projectFile.setConsumerProgramStructureId(student.getConsumerProgramStructureId());
			projectFile.setSubject(subject);
//			projectFile = projectSubmissionDAO.findById(projectFile); // Commented to make two cycle live
			projectFile.setMonth(examMonthYearBean.getMonth());
			projectFile.setYear(examMonthYearBean.getYear());
			projectFile = projectSubmissionDAO.findProjectGuidelinesForApplicableCycle(projectFile);
			endDate = projectFile.getEndDate(); 
			
			getCorporateExamCenterIdNameMap = examCenterDao.getCorporateExamCenterIdNameMap();
			
		} catch (Exception e1) {  
			projectSubmissionLogger.info("Error. Failed to get Project Booking Details. "+e1.getMessage());
		}
		
		if(examBookings!=null && examBookings.size()>0){
			year = examBookings.get(0).getYear();
			month = examBookings.get(0).getMonth();
			subject = examBookings.get(0).getSubject(); 
		}
		
		try{  
			
			fileName = examFeeReceiptCreator.createProjectBookedPDF(examBookings,getCorporateExamCenterIdNameMap,  FEE_RECEIPT_PATH, student, confirmedOrReleasedProjectExamBookings,endDate);
			
			folderName=FEE_RECEIPT_PATH+fileName;
			String s3FileName = uploadFeeReceiptInS3(folderName);
			if(s3FileName.equalsIgnoreCase("error")) {
				projectSubmissionLogger.info("Error. Failed to insert in S3");
				return "error";
			}
			//delete file saved in local 
			if(folderName!=null) {
				File file = new File(folderName);
		        boolean isDeleted = file.delete();
			}
			dao.insertDocumentRecord(fileName, year, month, sapid, "Project Fee Receipt",trackId);
		
		}catch(Exception e){
			projectSubmissionLogger.info("Exception Error createAndUploadProjectFeeReceipt()  Sapid-{} Error- {}",sapid,e.getMessage());
			return "error";
		} 
		return fileName;
	}
	private String uploadFeeReceiptInS3(String folderName) {
		HashMap<String,String> s3_response = new HashMap<String,String>();
		
		
		final File folder = new File(folderName);
		
		if(!folder.exists()) {
			projectSubmissionLogger.info("File Not Found. "+folderName);
			//return "error";
		}
		
		String baseUrl = FilenameUtils.getPath(folderName);
		String fileName = FilenameUtils.getBaseName(folderName)
		                + "." + FilenameUtils.getExtension(folderName);
		
		baseUrl = baseUrl.substring(0,baseUrl.length());
		
		fileName = baseUrl + fileName;
		
	    s3_response = amazonS3.uploadLocalFile(folderName,fileName,"hallticket",baseUrl);
	    if(s3_response.get("status").equals("error")) {
			
	    	projectSubmissionLogger.info("Error:- "+s3_response.get("url"));
			return "error";
		}
	    return fileName;
	}
}
