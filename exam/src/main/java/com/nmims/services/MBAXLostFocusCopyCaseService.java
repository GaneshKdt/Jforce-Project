package com.nmims.services;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.nmims.beans.AuditTrailExamBean;
import com.nmims.beans.ErrorAnalyticsBean;
import com.nmims.beans.LostFocusLogExamBean;
import com.nmims.beans.NetworkLogsExamBean;
import com.nmims.beans.StudentsTestDetailsExamBean;
import com.nmims.beans.TestExamBean;
import com.nmims.helpers.MailSender;
import com.nmims.stratergies.impl.EmptyReasonStrategy;
import com.nmims.stratergies.impl.MiscellaneousReasonStrategy;
import com.nmims.stratergies.impl.NetworkConnectionStrategy;
import com.nmims.stratergies.impl.SystemIssueStrategy;

@Service("mbaxLostFocusCopyCaseService")
public class MBAXLostFocusCopyCaseService {

	 @Autowired
	    MBAXAuditTrailsService mbaxAuditTrailsService;

	    @Autowired
	    NetworkConnectionStrategy networkConnectionStrategy;

	    @Autowired
	    SystemIssueStrategy systemIssueStrategy;

	    @Autowired
	    MiscellaneousReasonStrategy miscellaneousReasonStrategy;

	    @Autowired
	    EmptyReasonStrategy emptyReasonStrategy;

		@Autowired
		ApplicationContext act;

		private static final Logger logger = LoggerFactory.getLogger("lostFocusCopyCase");

		final int durationCheckForSystemIssue = 60;
		final int durationCheckForNetworkConnection = 60;
		final int durationCheckForMiscellaneous = 30;
		final int durationCheckForEmptyReason = 60;
		final int durationCheckForAnswerUpdate = 5;

		@Value( "${IA_ASSIGNMENT_FILES_PATH}" )
		private String IA_ASSIGNMENT_FILES_PATH;
		
		@Async
		public void markCopyCaseBatchJob( TestExamBean test ){

			Long before = System.currentTimeMillis();
			SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
			logger.info("inMarkCopyCaseBatchJob started at dateTime: "+formater.format( new Date() ) );
			
			List<ErrorAnalyticsBean> errorAnalytics = new ArrayList<ErrorAnalyticsBean>();

			ArrayList<LostFocusLogExamBean> studentListForCopyCase = new ArrayList<LostFocusLogExamBean>();
			ArrayList<LostFocusLogExamBean> detailedStudentList = new ArrayList<LostFocusLogExamBean>();
			ArrayList<LostFocusLogExamBean> studentToMarkCopyCase = new ArrayList<LostFocusLogExamBean>();
			
			HashMap<String, List<NetworkLogsExamBean>> networkLogList = new HashMap<>();
			HashMap<String, ArrayList<LostFocusLogExamBean>> individualStudentLostFocusLogs = new HashMap<>();
			
			StudentsTestDetailsExamBean studentTestDetails = new StudentsTestDetailsExamBean();

			SimpleDateFormat errorformat = new SimpleDateFormat("yyyy-MM-dd");
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
			
			try {

				/*
				 * fetching all the error logs that are present for the start date of the test
				 * */
				
				errorAnalytics = mbaxAuditTrailsService.getErrorAnalyticsBySapidCreatedDate(errorformat.format( format.parse( test.getStartDate() ) ) );
				
				logger.info("inMarkCopyCaseBatchJob got totalErrorAnalyticsCount: "+errorAnalytics.size());
				
			} catch (Throwable throwable) {
				
				String stackTrace = ExceptionUtils.getStackTrace(throwable);
				logger.info("inMarkCopyCaseBatchJob got exceptionInFetchingErrorAnalytics: "+stackTrace);
				errorAnalytics = new ArrayList<ErrorAnalyticsBean>();
				
			}

			try {
				AuditTrailExamBean bean = new AuditTrailExamBean();
				bean.setTestId( test.getId() );
				
				/*
				 * fetching all the students that have a lost focus duration of above 15 seconds for particular test id
				 * */
				
				studentListForCopyCase = mbaxAuditTrailsService.getLostFocusDetailsForBatchJob( bean ) ;
				
				for( LostFocusLogExamBean logBean : studentListForCopyCase ) {

					LostFocusLogExamBean updatedBean = logBean;

					/*
					 * fetching student test details ( reason, contactedSuppport) for all the students that have a lost focus 
					 * duration of above 15 seconds to get the test started on and test ended status of the students
					 * */
					
					studentTestDetails = mbaxAuditTrailsService.getStudentsTestDetailsBySapidAndTestId( logBean.getSapid(), bean.getTestId());
					
					bean.setSapid( logBean.getSapid() );
					bean.setStartDate( studentTestDetails.getTestStartedOn() );
					updatedBean.setTestEndedStatus( studentTestDetails.getTestEndedStatus() );

					/*
					 * fetching a HashMap with sapid as its key and network logs as the value for network logs for sapid, 
					 * duration and start date of the test 
					 * */
					
					networkLogList.put( logBean.getSapid(), getNetworkLogsForSapid(bean)) ;
					logger.info("inMarkCopyCaseBatchJob got networkLogList: "+networkLogList.size());

					/*
					 * fetching a HashMap with sapid as its key and all the instances of lost focus as its value for that 
					 * particular test
					 * */
					individualStudentLostFocusLogs.put( logBean.getSapid(), getIndividualStudentLostFocusLogs(bean));
					logger.info("inMarkCopyCaseBatchJob got individualStudentLostFocusLogs: "+individualStudentLostFocusLogs.size());

					updatedBean.setFacultyId( test.getFacultyId() );
					updatedBean.setTestName( test.getTestName() );
					
					detailedStudentList.add( updatedBean );

				}

			} catch (Throwable throwable) {
				
				String stackTrace = ExceptionUtils.getStackTrace(throwable);
				logger.info("inMarkCopyCaseBatchJob got exceptionInFetchingDetailedList: "+stackTrace);
				
			}
			logger.info("inMarkCopyCaseBatchJob got totoalStudentListForCopyCaseCount: "+detailedStudentList.size());

			/*
			 * updating the list to get reason, attempt status and student details
			 * */
			
			studentListForCopyCase = mbaxAuditTrailsService.getLostFocusListWithStudentAndTestDetails( detailedStudentList );

			for( LostFocusLogExamBean bean : studentListForCopyCase ) {
				logger.info("inMarkCopyCaseBatchJob got students: "+bean.getSapid()+" duration: "+bean.getTimeAwayInSecs()+" testEndedStatus: "+
				bean.getTestEndedStatus()+" reason: "+bean.getReason());
			}
			
			/*
			 * performing a network connection check to verify if the student with lost focus and reason related to network 
			 * loss actually encountered a network issue based on the error analytics and after that updated the answer or 
			 * not
			 * */
			
			studentToMarkCopyCase.addAll( networkConnectionStrategy.performNetworkConnectionCheck( studentListForCopyCase, errorAnalytics, 
					networkLogList, individualStudentLostFocusLogs ) );
			studentListForCopyCase = updateStudentListToMarkCopyCase( studentListForCopyCase, studentToMarkCopyCase);

			/*
			 * performing a system issue check to verify if the student with lost focus instance took abnormally long time to 
			 * get back to the test after the lost focus instance was captured
			 * */
			
			studentToMarkCopyCase.addAll( systemIssueStrategy.performSystemIssueCheck( studentListForCopyCase, networkLogList, 
					individualStudentLostFocusLogs) );
			studentListForCopyCase = updateStudentListToMarkCopyCase( studentListForCopyCase, studentToMarkCopyCase);

			/*
			 * performing a miscellaneous check to verify if the student with lost focus instance and reason like misclick, 
			 * pop up, underling etc took abnormally long time to get back to the test after the lost focus instance 
			 * was captured
			 * */
			
			studentToMarkCopyCase.addAll( miscellaneousReasonStrategy.performMiscellaneousCheck( studentListForCopyCase, networkLogList, 
					individualStudentLostFocusLogs) );
			studentListForCopyCase = updateStudentListToMarkCopyCase( studentListForCopyCase, studentToMarkCopyCase);

			/*
			 * commented out to avoid marking student who have not mentioned the reason
			studentToMarkCopyCase.addAll( emptyReasonStrategy.performEmptyReasonCheck( studentListForCopyCase, networkLogList, 
					individualStudentLostFocusLogs) );
			studentListForCopyCase = updateStudentListToMarkCopyCase( studentListForCopyCase, studentToMarkCopyCase);
			*/
		
			logger.info("inMarkCopyCaseBatchJob got totalCountOfStudentsToMark: "+studentToMarkCopyCase.size());
			
			for(LostFocusLogExamBean bean : studentToMarkCopyCase) {
				logger.info("inMarkCopyCaseBatchJob markingStudents: "+bean.getSapid()+" duration: "+bean.getTimeAwayInSecs()+
						" reason: "+bean.getReason());
			}
			
			try {
				
				/*
				 * marking the students in the final updated list for lost focus copy case
				 * */
				
				markCopyCase( studentToMarkCopyCase );

				/*
				 * building the excel on test basis with all the students that have been marked for copy case
				 * */
				
				String fileName = buildLostFocusCopyCaseExcel( studentToMarkCopyCase, test.getTestName() );
				
				/*
				 * sending the mail for all the test for which the students have been marked for copy case
				 * */
				sendMailForCopyCase( fileName, test );
				
				logger.info( "inMarkCopyCaseBatchJob successfully marked copycase" );
			} catch (Throwable throwable) {
				String stackTrace = ExceptionUtils.getStackTrace(throwable);
				logger.info("inMarkCopyCaseBatchJob got exceptionInMarkingCopyCase: "+stackTrace);
			}
			
			
			Long after = System.currentTimeMillis();
			long uptime = after-before;
			long minutes = TimeUnit.MILLISECONDS.toMinutes(uptime);
			uptime -= TimeUnit.MINUTES.toMillis(minutes);
			long seconds = TimeUnit.MILLISECONDS.toSeconds(uptime);
			
			logger.info( "inMarkCopyCaseBatchJob got totalTimeTakenToMarkCopyCase: "+minutes+" min, "+seconds+" sec\n");

			return;
		}

		private String buildLostFocusCopyCaseExcel( ArrayList<LostFocusLogExamBean> studentToMarkCopyCase, String testName ) throws Exception {
			
			XSSFWorkbook workbook =new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet("Lostfocus Copycase Report");
			XSSFRow header = sheet.createRow(0);
			String dateForCopyCase = "";
			
			int index = 0;
			
			header.createCell(index++).setCellValue("Sr. No.");
			header.createCell(index++).setCellValue("SAPID"); 
			header.createCell(index++).setCellValue("Duration");
			header.createCell(index++).setCellValue("Reason");
			header.createCell(index++).setCellValue("Test Ended Status");
			header.createCell(index++).setCellValue("Status");
			
			int rowNum = 1;
			
			for( LostFocusLogExamBean bean : studentToMarkCopyCase ) {
				
				index = 0;
				dateForCopyCase = bean.getCreatedDate().toLocalDateTime().toString();
				
				XSSFRow row = sheet.createRow(rowNum++);
				row.createCell(index++).setCellValue( rowNum-1 ); 
				row.createCell(index++).setCellValue( bean.getSapid() ); 
				row.createCell(index++).setCellValue( bean.getTimeAwayInSecs().toString() ); 
				row.createCell(index++).setCellValue( bean.getReason() ); 
				row.createCell(index++).setCellValue( bean.getTestEndedStatus() ); 
				row.createCell(index++).setCellValue( "Marked" ); 
				
			}
			
			if( !StringUtils.isBlank( testName ) )
				testName = testName.substring(0,11).replaceAll(" ", "_").replaceAll(":", "").replaceAll("\\\\", "")
					  .replaceAll("/", "").replaceAll("\\*", "").replaceAll("\\?", "")
					  .replaceAll("\t", "").replaceAll("<", "").replaceAll(">", "").replaceAll("|", "");

			String date[] = dateForCopyCase.split("T");
			String fileName = "Copycase_Report_"+testName+"_"+date[0]+"_"+RandomStringUtils.randomAlphabetic(8)+".xlsx";
			String folderName = IA_ASSIGNMENT_FILES_PATH + "Lostfocus/";
			
			String filePath = folderName +fileName;
			
			File folder = new File(folderName);
			if (!folder.exists()) {   
				folder.mkdirs();   
			}
			
			FileOutputStream out = new FileOutputStream(new File(filePath));
			workbook.write(out);
			out.close();
			
			return fileName;
			
		}
		
		private void sendMailForCopyCase( String fileName, TestExamBean test ) {

			MailSender mailSender = (MailSender)act.getBean("mailer");
			
			String folderName = IA_ASSIGNMENT_FILES_PATH + "Lostfocus/";
			
			String[] emailIds = { "NGASCE.Exams@nmims.edu","Jigna.Patel@nmims.edu","nashrah.shaikh@nmims.edu",
					"harshalee.ullal@nmims.edu","pooja.jadhav@nmims.edu","christopher.kevin@nmims.edu","khatija.shaikh@nmims.edu","Ankita.Parmar@nmims.edu", "laxmi.raaj@nmims.edu","aziz.merchant@nmims.edu"}; 

			/*
			 * use while testing 
			 * String[] emailIds = {"jforcesolution@gmail.com"}; 
			 */
			mailSender.sendIALostfocusCopyCaseReport("Lostfocus CopyCase Report for "+test.getTestName(), fileName, folderName, emailIds, test );
			
		}

		private LostFocusLogExamBean markCopyCase( List<LostFocusLogExamBean> studentList ) throws Exception{

			LostFocusLogExamBean response = new LostFocusLogExamBean();
			
			for( LostFocusLogExamBean bean : studentList) {

				mbaxAuditTrailsService.markCopyCaseForLostFocus(bean);
		
			}
			
			return response;
		}
		
		private ArrayList<LostFocusLogExamBean> updateStudentListToMarkCopyCase( ArrayList<LostFocusLogExamBean> studentListForCopyCase, 
				ArrayList<LostFocusLogExamBean> studentToMarkCopyCase){
			
			ArrayList<LostFocusLogExamBean> updatedStudentList = new ArrayList<>();
			
			for( LostFocusLogExamBean bean: studentListForCopyCase ) {
				
				if( !studentToMarkCopyCase.contains(bean) ) {
					updatedStudentList.add(bean);
				}

			}
			
			return updatedStudentList;
			
		}
		
		private List<NetworkLogsExamBean> getNetworkLogsForSapid( AuditTrailExamBean bean ){

			List<NetworkLogsExamBean> networkLogs = new ArrayList<NetworkLogsExamBean>();
			
			try {
				networkLogs = mbaxAuditTrailsService.getNetworkLogsForSapid( bean );
			} catch (Exception e) {
				
			}
			
			return networkLogs;
			
		}
		
		private ArrayList<LostFocusLogExamBean> getIndividualStudentLostFocusLogs( AuditTrailExamBean bean ){

			ArrayList<LostFocusLogExamBean> lostFocusLogs = new ArrayList<LostFocusLogExamBean>();
			
			try {
				lostFocusLogs = mbaxAuditTrailsService.getIndividualStudentLostFocusLogs( bean );
			} catch (Exception e) {
				
			}
			
			return lostFocusLogs;
			
		}
	
}
