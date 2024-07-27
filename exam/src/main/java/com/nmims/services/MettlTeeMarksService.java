package com.nmims.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.base.Throwables;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.beans.MettlEvaluatorInfo;
import com.nmims.beans.MettlFetchTestResultBean;
import com.nmims.beans.MettlPGResponseBean;
import com.nmims.beans.MettlRegisterCandidateBean;
import com.nmims.beans.MettlResponseBean;
import com.nmims.beans.MettlResultAPIResponseBean;
import com.nmims.beans.MettlResultCandidateBean;
import com.nmims.beans.MettlResultCandidateTestResultBean;
import com.nmims.beans.MettlResultCandidateTestStatusBean;
import com.nmims.beans.MettlResultEvaluatorData;
import com.nmims.beans.MettlResultPagingBean;
import com.nmims.beans.MettlResultQuestionLongAnswerTypeResponse;
import com.nmims.beans.MettlResultQuestionMcqTypeResponse;
import com.nmims.beans.MettlResultQuestionWiseResponse;
import com.nmims.beans.MettlResultSection;
import com.nmims.beans.MettlResultsSyncBean;
import com.nmims.beans.MettlSectionQuestionResponse;
import com.nmims.beans.MettlStudentSectionInfo;
import com.nmims.beans.MettlStudentTestInfo;
import com.nmims.beans.OnlineExamMarksBean;
import com.nmims.beans.ReExamEligibleStudentBean;
import com.nmims.beans.ResponseBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.beans.TEERescheduleExamBookingExcelBean;
import com.nmims.helpers.ExcelHelper;
import com.nmims.daos.ExamsAssessmentsDAO;
import com.nmims.daos.MettlPGResultProcessingDAO;
import com.nmims.helpers.DateHelper;
import com.nmims.helpers.MailSender;
import com.nmims.helpers.MettlHelper;

@Service("mettlTeeMarksService")
public class MettlTeeMarksService {

	@Value("${MettlBaseUrl}")
	private String MettlBaseUrl;
	
	@Value("${PG_METTL_PRIVATE_KEY}")
	private String PG_METTL_PRIVATE_KEY;
	
	@Value("${PG_METTL_PUBLIC_KEY}")
	private String PG_METTL_PUBLIC_KEY;
	
	@Value("${PG_RESULT_PULL_BASE_URL}")
	private String PG_RESULT_PULL_BASE_URL;
	
	@Value("${RESCHEDULE_EXAMBOOKING_FILES_PATH}")
	private String RESCHEDULE_EXAMBOOKING_FILES_PATH;

	@Value("#{'${RESCHEDULE_TO_EMAILID}'.split(',')}")
	private String[] RESCHEDULE_TO_EMAILID;
	
	@Value("#{'${RESCHEDULE_CC_EMAILID}'.split(',')}")
	private String[] RESCHEDULE_CC_EMAILID;
	
	@Value("#{'${TIMEBOUND_ID_TEE}'.split(',')}")
	private String[] TIMEBOUND_ID_TEE;
	
	@Autowired
	MailSender mailer;
	
	@Autowired
	private ExamsAssessmentsDAO examsAssessmentsDAO;
	
	@Autowired
	private MettlHelper mettlHelper ;

	@Autowired
	private MettlPGResultProcessingDAO resultProcessingDAO;

	@Autowired
	private MettlTestResultTaskExecutorService mettlTestResultTaskExecutorService;
	
	@Autowired
	private ExcelHelper excelHelper ;
	
	@Autowired
	private ApplicationContext act;
	
	@Value("${SERVER}")
	private String SERVER;
	
	@Value("${ENVIRONMENT}")
	private String ENVIRONMENT;
	
	public static final Logger logger = LoggerFactory.getLogger(MettlTeeMarksService.class);
	public static final Logger pullMettlMarks = LoggerFactory.getLogger("pullMettlMarks");
	public static final Logger transferMettlMarksToOnlineMarks = LoggerFactory.getLogger("transferMettlMarksToOnlineMarks");
	public static final Logger applybodPG = LoggerFactory.getLogger("applybod-PG");
	private static final Logger fetchMettlAllCandidateTestResult =LoggerFactory.getLogger("fetchMettlAllCandidateTestResult");

	public static final Logger pullTimeBoundMettlMarksLogger =LoggerFactory.getLogger("pullTimeBoundMettlMarks");

	private final List<String> CERTIFICATE_PROGRAM_LIST = Arrays.asList("CBA","B.Com","BBA","CGM");
	
	//sets lastmodified / created by if transferred at the time of pull
	private final String AUTO_TRANSFER_MARKS_STR = "autoTransferMarks";
	private final String APPLY_BOD_STR = "applyBod";

	//mettl_marks status column constants
	private final String METTL_ATTEMPTED = "Attempted";
	private final String METTL_NOT_ATTEMPTED = "Not Attempted";

//	
//	public String runMettlAbsentTeeListSchedular(String examDate) {
//		ArrayList<MettlPGResponseBean> MettlPGResponseBeanList = new ArrayList<MettlPGResponseBean>();
//		logger.info("\n MettlTeeMarksService >> call runMettlAbsentTeeListSchedular");
//		
//		ArrayList<MettlPGResponseBean> mettlResponseList = examsAssessmentsDAO.getMettlTeeStudentDataForSchedular(examDate);
//		logger.info("\n MettlTeeMarksService >> runMettlAbsentTeeListSchedular >> getMettlTeeStudentDataForSchedular size : "+mettlResponseList.size());
//		Integer count = 0;
//		for(MettlPGResponseBean MettlPGResponseBean: mettlResponseList) {
//			MettlPGResponseBean tmp_responseBean = new MettlPGResponseBean();
//			count++;
//			logger.info("\n MettlTeeMarksService >> runMettlAbsentTeeListSchedular >> readMettlMarksFromAPIService "+count+" request : "+MettlPGResponseBean);
//			tmp_responseBean =	readMettlMarksFromAPIService(MettlPGResponseBean);
//			logger.info("\n MettlTeeMarksService >> runMettlAbsentTeeListSchedular >> readMettlMarksFromAPIService "+count+" response : "+tmp_responseBean);
//			if(tmp_responseBean != null ) {
//				MettlPGResponseBeanList.add(tmp_responseBean);
//			}
//			
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				
//			}
//		}
//		ArrayList<String> errorList = new ArrayList<String>();
//		if(!MettlPGResponseBeanList.isEmpty()) { 
//			errorList = upsertMettlMarks(MettlPGResponseBeanList);
//			if(errorList.isEmpty()) { 
//				logger.info("\n MettlTeeMarksService >> runMettlAbsentTeeListSchedular >> Marks upserted successfully  for "+MettlPGResponseBeanList.size()+" students");
//				return  " Marks upserted successfully  for "+MettlPGResponseBeanList.size()+" students"; 
//			}else {
//				logger.info("\n MettlTeeMarksService >> runMettlAbsentTeeListSchedular >> Failed to upsert marks for "+errorList.size()+" students  emailIDs : "+errorList);
//				return	" Failed to upsert marks for "+errorList.size()+" students \n emailIDs : "+errorList; 
//			}
//		}else {
//			logger.info("\n MettlTeeMarksService >> runMettlAbsentTeeListSchedular >> Null or empty result found for upsert");
//				return 	" Null or empty result found for upsert";
//		}
//	}
//	
	
	public ResponseBean getTestTakenStatusOfExamBookingStudentService(String examDate){
		ResponseBean responseBean = new ResponseBean();
		List<ExamBookingTransactionBean> list = new ArrayList<>();
		try {
			logger.info("\n MettlTeeMarksService >> call getTestTakenStatusOfExamBookingStudentService ");
			list = examsAssessmentsDAO.getTestTakenStatusOfExamBookingStudent(examDate);
			
			responseBean.setExamBookingTransactionBean(list);
			responseBean.setCode(200);
			responseBean.setMessage("Success");

			logger.info("\n MettlTeeMarksService >> getTestTakenStatusOfExamBookingStudentService >> getTestTakenStatusOfExamBookingStudent size : "+list.size());			
			return responseBean;
		}catch(Exception e) {
			responseBean.setCode(422);
			responseBean.setMessage(" Error Message : "+e.getMessage());

			logger.error("\n MettlTeeMarksService >> getTestTakenStatusOfExamBookingStudentService >> Error Message : "+e.getMessage());
			
			return responseBean;
		}
	}
	
	private List<ExamBookingTransactionBean> getTestTakenStatusOfExamBookingStudent(String examDate) {
		List<ExamBookingTransactionBean> list = new ArrayList<>();
		try {
		String url = "https://ngasce-content.nmims.edu/exam/api/getMettlTeeStatusByExamDate";
//		String url = "http://localhost:8080/exam/api/getMettlTeeStatusByExamDate";
		HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json"); 
        HttpEntity<String> request = new HttpEntity<String>(examDate,headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange( url, HttpMethod.POST, request, String.class);
        JsonObject jsonObject = new JsonParser().parse(response.getBody()).getAsJsonObject();
		
        if(jsonObject.get("code").getAsInt() == 200) {
        	JsonArray jsonTestTakenStatusList = jsonObject.get("examBookingTransactionBean").getAsJsonArray();
        	for(int i = 0; i < jsonTestTakenStatusList.size(); i++) {
        		ExamBookingTransactionBean bean = new ExamBookingTransactionBean();
        		JsonObject jsonBean = jsonTestTakenStatusList.get(i).getAsJsonObject();
        		bean.setSapid(jsonBean.get("sapid").getAsString());
        		bean.setSubject(jsonBean.get("subject").getAsString());
        		bean.setExamStartDateTime(jsonBean.get("examStartDateTime").getAsString());
        		list.add(bean);
        	}
        	
        	return list;
        }
        
        return list;	
		}catch(Exception e) {
			
			return list;
		}
        
	}
	
	private XSSFWorkbook createRescheduleExcelReport(List<TEERescheduleExamBookingExcelBean> list , String title) {
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet(title); 
		Integer rownum = 0;
		Integer cellnum = 0;

		
		 
		Row row = sheet.createRow(rownum++);
		
		Cell cell = row.createCell(cellnum++);
		cell.setCellValue("Sr. No.");
		
		cell = row.createCell(cellnum++);
		cell.setCellValue("SapId");
		
		cell = row.createCell(cellnum++);
		cell.setCellValue("Subject");
		
		cell = row.createCell(cellnum++);
		cell.setCellValue("Updated Exam Date");
		
		cell = row.createCell(cellnum++);
		cell.setCellValue("Updated Exam Time");
		
		cell = row.createCell(cellnum++);
		cell.setCellValue("Updated Booked Status");
		
		cell = row.createCell(cellnum++);
		cell.setCellValue("Old Exam Date");
		
		cell = row.createCell(cellnum++);
		cell.setCellValue("Old Exam Time");
		
		cell = row.createCell(cellnum++);
		cell.setCellValue("Old Booked Status");
		
		cell = row.createCell(cellnum++);
		cell.setCellValue("Subject Code");
		
		cell = row.createCell(cellnum++);
		cell.setCellValue("Mettl Email Id");
		
		cell = row.createCell(cellnum++);
		cell.setCellValue("Old Schedule Id");
		
		cell = row.createCell(cellnum++);
		cell.setCellValue("New Schedule Id");

		cell = row.createCell(cellnum++);
		cell.setCellValue("Old Schedule AccessKey");
		
		cell = row.createCell(cellnum++);
		cell.setCellValue("New Schedule AccessKey");
		
		cell = row.createCell(cellnum++);
		cell.setCellValue("Assessment Id");

		cell = row.createCell(cellnum++);
		cell.setCellValue("Test Name");
		
		for(TEERescheduleExamBookingExcelBean rescheduleBean : list) {
			cellnum = 0;
			row = sheet.createRow(rownum++);
			
			cell = row.createCell(cellnum++);
			cell.setCellValue(rownum-1);
			
			cell = row.createCell(cellnum++);
			cell.setCellValue(rescheduleBean.getSapId());
			
			cell = row.createCell(cellnum++);
			cell.setCellValue(rescheduleBean.getSubject());
			
			cell = row.createCell(cellnum++);
			cell.setCellValue(rescheduleBean.getUpdatedExamDate());
			
			cell = row.createCell(cellnum++);
			cell.setCellValue(rescheduleBean.getUpdatedExamTime());
			
			cell = row.createCell(cellnum++);
			cell.setCellValue(rescheduleBean.getUpdatedBooked());
			
			cell = row.createCell(cellnum++);
			cell.setCellValue(rescheduleBean.getOldExamDate());
			
			cell = row.createCell(cellnum++);
			cell.setCellValue(rescheduleBean.getOldExamTime());
			
			cell = row.createCell(cellnum++);
			cell.setCellValue(rescheduleBean.getOldBooked());
			
			cell = row.createCell(cellnum++);
			cell.setCellValue(rescheduleBean.getSubjectCode());
			
			cell = row.createCell(cellnum++);
			cell.setCellValue(rescheduleBean.getEmailId());
			
			cell = row.createCell(cellnum++);
			cell.setCellValue(rescheduleBean.getOldScheduleId());
			
			cell = row.createCell(cellnum++);
			cell.setCellValue(rescheduleBean.getNewScheduleId());
			
			cell = row.createCell(cellnum++);
			cell.setCellValue(rescheduleBean.getOldScheduleAccessKey());
			
			cell = row.createCell(cellnum++);
			cell.setCellValue(rescheduleBean.getNewScheduleAccessKey());
			
			cell = row.createCell(cellnum++);
			cell.setCellValue(rescheduleBean.getAssessmentId());
			
			cell = row.createCell(cellnum++);
			cell.setCellValue(rescheduleBean.getTestName());
			
		}

		sheet.autoSizeColumn(1);
		sheet.autoSizeColumn(2);
		sheet.autoSizeColumn(3);
		sheet.autoSizeColumn(4);
		sheet.autoSizeColumn(5);
		sheet.autoSizeColumn(6);
		sheet.autoSizeColumn(7);
		sheet.autoSizeColumn(8);
		sheet.autoSizeColumn(9);
		sheet.autoSizeColumn(10);
		sheet.autoSizeColumn(11);
		sheet.autoSizeColumn(12);
		sheet.autoSizeColumn(13);
		sheet.autoSizeColumn(14);
		sheet.autoSizeColumn(15);
		sheet.autoSizeColumn(16);
		sheet.autoSizeColumn(17);
		return workbook;
	}
	
	private void createFile(String fileName,String folderName,XSSFWorkbook workbook) {
		try {
			String filePath = folderName +fileName;
			File folder = new File(folderName);
			if (!folder.exists()) {   
				folder.mkdirs();   
			} 
			FileOutputStream out = new FileOutputStream(new File(filePath));
			workbook.write(out);
			out.close();
		}catch(Exception e) {
			
		}
	} 
	
	public String runTeeRescheduleExambookingListSchedulerService(String examDate) {
		List<TEERescheduleExamBookingExcelBean> listOfAllReschedule = new ArrayList<>();
		List<TEERescheduleExamBookingExcelBean> listNotToBeEvaluate = new ArrayList<>();
		try {
			logger.info("\n MettlTeeMarksService >> call runTeeRescheduleExambookingListSchedulerService ");
			List<ExamBookingTransactionBean> testTakenStatusList = new ArrayList<>();
			testTakenStatusList = getTestTakenStatusOfExamBookingStudent(examDate);
			listOfAllReschedule = examsAssessmentsDAO.getTeeRescheduleExamBookingStudent(examDate);
			logger.info("\n MettlTeeMarksService >> runTeeRescheduleExambookingListSchedulerService >> getTeeRescheduleExamBookingStudent size : "+listOfAllReschedule.size());			
			
			for(TEERescheduleExamBookingExcelBean beanOfAllReschedule : listOfAllReschedule ) {
				for(ExamBookingTransactionBean beanOfNotToBeEvalute : testTakenStatusList ) {
					if(!"Certificate".equals(beanOfAllReschedule.getProgramType())) {
						if(beanOfNotToBeEvalute.getSapid().equals(beanOfAllReschedule.getSapId()) 
								&& beanOfNotToBeEvalute.getSubject().equals(beanOfAllReschedule.getSubject()) 
								) {
							listNotToBeEvaluate.add(beanOfAllReschedule);
						}
					}
				}
				
			}
			
			String folderName = RESCHEDULE_EXAMBOOKING_FILES_PATH+"/"+examDate+"/";
			
			XSSFWorkbook workbook1 = createRescheduleExcelReport(listOfAllReschedule, "TEE All Reschedule Exambooking List");
			String fileNameAllReschedule = "All_Reschedule_ExamBooking_List_"+examDate+".xlsx";
			createFile(fileNameAllReschedule,folderName,workbook1);
			
			logger.info("MettlTeeMarksService >> runTeeRescheduleExambookingListSchedulerService >> Excel created successfully listOfAllReschedule.size() "+listOfAllReschedule.size());
			
			
			XSSFWorkbook workbook2 = createRescheduleExcelReport(listNotToBeEvaluate, "TEE Not To Be Evaluate Reschedule Exambooking List");
			String fileNameNotToBeEvaluate = "Not_To_Be_Evaluate_Reschedule_ExamBooking_List_"+examDate+".xlsx";
			createFile(fileNameNotToBeEvaluate,folderName,workbook2);
			
			logger.info("MettlTeeMarksService >> runTeeRescheduleExambookingListSchedulerService >> Excel created successfully listNotToBeEvaluate size "+listNotToBeEvaluate.size());

			
			if(listOfAllReschedule.size() != 0 && listOfAllReschedule != null) {
				MailSender mailSender = (MailSender)act.getBean("mailer");
				
				mailSender.sendTeeRescheduleExamBookingReport("Reschedule ExamBooking List "+examDate, fileNameAllReschedule, fileNameNotToBeEvaluate, folderName, RESCHEDULE_TO_EMAILID, RESCHEDULE_CC_EMAILID);
				logger.info("\n MettlTeeMarksService >> runTeeRescheduleExambookingListSchedulerService >> Email Send successfully fileName : "+fileNameAllReschedule+" On RESCHEDULE_TO_EMAILID / RESCHEDULE_CC_EMAILID "+Arrays.toString(RESCHEDULE_TO_EMAILID)+" / "+Arrays.toString(RESCHEDULE_CC_EMAILID));
				logger.info("\n MettlTeeMarksService >> runTeeRescheduleExambookingListSchedulerService >> Email Send successfully fileName : "+fileNameNotToBeEvaluate+" On RESCHEDULE_TO_EMAILID / RESCHEDULE_CC_EMAILID "+Arrays.toString(RESCHEDULE_TO_EMAILID)+" / "+Arrays.toString(RESCHEDULE_CC_EMAILID));
				
				logger.info("\n Email Send Successfully!!! All Reschedule Report List size : "+listOfAllReschedule.size()+" and Not To be Evaluate Reschedule Report List size : "+listNotToBeEvaluate.size());
				
				return "Email Send Successfully!!! All Reschedule Report List size : "+listOfAllReschedule.size()+" and Not To be Evaluate Reschedule Report List size : "+listNotToBeEvaluate.size();
			}
			
			logger.info("\n Email Not Sent, All Reschedule Report List size : "+listOfAllReschedule.size());
			
			return "Email Not Sent, All Reschedule Report List size : "+listOfAllReschedule.size();
			
		}catch(Exception e) {
			logger.error("\n MettlTeeMarksService >> runTeeRescheduleExambookingListSchedulerService >> Error Message : "+e.getMessage());
			
			return " Reschedule all Report List size "+listOfAllReschedule.size()+" Error Message : "+e.getMessage();
		}
		
		
	}
	
	public ArrayList<String> upsertMettlMarks(ArrayList<MettlPGResponseBean> MettlPGResponseBeanList) {
		logger.info("\n MettlTeeMarksService >> runMettlAbsentTeeListSchedular >> upsertMettlMarks size : "+MettlPGResponseBeanList.size());
		int i = 0;
		ArrayList<String> errorList = new ArrayList<>();
		for (i = 0; i < MettlPGResponseBeanList.size(); i++) {
			MettlPGResponseBean bean = MettlPGResponseBeanList.get(i);
			try{
				upsertMettleMarksStatus(bean);
			}catch(Exception e){
				
				logger.error("\n MettlTeeMarksService >> runMettlAbsentTeeListSchedular >> upsertMettlMarks Error Message : "+e.getMessage());
				errorList.add(bean.getEmail()+"");
			}
		}
		return errorList;

	}
	
	
	public void upsertMettleMarksStatus(MettlPGResponseBean bean) {
			boolean recordExists = resultProcessingDAO.checkIfRecordExistsInMettlMarks(bean);
			if(recordExists){
				resultProcessingDAO.updateMettlMarksStatus(bean);
				logger.error("\n MettlTeeMarksService >> runMettlAbsentTeeListSchedular >> upsertMettlMarks >> call updateMettlMarksStatus");
				
			}else{
				resultProcessingDAO.insertMettlMarksStatus(bean);
				logger.error("\n MettlTeeMarksService >> runMettlAbsentTeeListSchedular >> upsertMettlMarks >> call insertMettlMarksStatus");
				
			}
	}
	
	public void readMettlMarksFromAPIService(MettlPGResponseBean bean, List<MettlStudentTestInfo> studentTestInfoList, List<MettlStudentSectionInfo> sectiontInfoList, List<MettlSectionQuestionResponse> questionList, List<MettlEvaluatorInfo> evaluatorInfo, boolean firstAttempt){
		try {
			mettlHelper.setBaseUrl(MettlBaseUrl);
			mettlHelper.setPrivateKey(PG_METTL_PRIVATE_KEY);
			mettlHelper.setPublicKey(PG_METTL_PUBLIC_KEY);
			String evalData = "true";
			String questionData =  "true";
			JsonObject jsonResponse = mettlHelper.getSingleStudentTestStatusForASchedule(bean.getSchedule_accessKey(), bean.getEmail(), evalData, questionData);
			if(jsonResponse != null) {
				JsonParser parser = new JsonParser(); 
				JsonElement mJson =  parser.parse(jsonResponse.toString());
				Gson gson = new Gson();
				MettlResultAPIResponseBean response = gson.fromJson(mJson, MettlResultAPIResponseBean.class);

				if("SUCCESS".equals(response.getStatus())) {

					// Initialize all marks to 0
					bean.setMax_marks(0);
					bean.setTotalMarks(0);
					bean.setSection1_marks(0);
					bean.setSection2_marks(0);
					bean.setSection3_marks(0);
					bean.setSection4_marks(0);
					
					MettlResultCandidateBean candidate = response.getCandidate();
					if(candidate != null) {
						MettlResultCandidateTestStatusBean testStatus = candidate.getTestStatus();
						if(testStatus != null) {
							if("Completed".equalsIgnoreCase(testStatus.getStatus())) {

								// Map the student result bean
								mapResultBean(bean, testStatus);

								// Map the data for analytics beans
								mapTestInfoBeans(
									bean, 
									testStatus, 
									studentTestInfoList,
									sectiontInfoList,
									questionList,
									evaluatorInfo
								);
							} else {
								bean.setStatus("Not Attempted");
							}
							
						} else {
							bean.setError("Test Status field is null!"  + " Full Response : " + jsonResponse);

							pullMettlMarks.info(
								"\n" + SERVER
								//+ ": "+new Date() //Vilpesh replaced on 2021-12-18
								+ ": "+ DateHelper.currentDateTime(null)
								+ " pullMarksFromMettlAPI " 
								+ " Error " + " Invalid Response" 
								+ " Resp Bean : " + bean
							);
						}
					} else {
						bean.setError("Candidate field is null!"  + " Full Response : " + jsonResponse);
						pullMettlMarks.info(
							"\n" + SERVER
							//+ ": "+new Date() //Vilpesh replaced on 2021-12-18
							+ ": "+ DateHelper.currentDateTime(null)
							+ " pullMarksFromMettlAPI " 
							+ " Error " + " Invalid Response" 
							+ " Resp Bean : " + bean
						);
					}
				} else {
					// Call fails in case of network error. Retry once in this case.
					if(firstAttempt) {
						readMettlMarksFromAPIService(bean, studentTestInfoList, sectiontInfoList, questionList, evaluatorInfo, false);
					} else {
						bean.setError("Invalid Response Status : " + response.getStatus() + " Full Response : " + jsonResponse);
						
						pullMettlMarks.info(
							"\n" + SERVER
							//+ ": "+new Date() //Vilpesh replaced on 2021-12-18
							+ ": "+ DateHelper.currentDateTime(null)
							+ " pullMarksFromMettlAPI " 
							+ " Error " + " Invalid Response" 
							+ " Resp Bean : " + bean
						);
					}
				}
			}
		}catch (Exception e) {
			
			pullMettlMarks.info(
				"\n" + SERVER
				//+ ": "+new Date() //Vilpesh replaced on 2021-12-18
				+ ": "+ DateHelper.currentDateTime(null)
				+ " pullMarksFromMettlAPI " 
				+ " Error " + e.getMessage()
				+ " Resp Bean : " + bean
				+ " Stack Trace : " + e.getStackTrace()
			);
			bean.setError(
				"\n" + SERVER
				//+ ": "+new Date() //Vilpesh replaced on 2021-12-18
				+ ": "+ DateHelper.currentDateTime(null)
				+ " pullMarksFromMettlAPI " 
				+ " Error " + e.getMessage()
				+ " Resp Bean : " + bean
				+ " Stack Trace : " + e.getStackTrace()
			);
		}
	}
	
	private void mapTestInfoBeans(MettlPGResponseBean bean, MettlResultCandidateTestStatusBean testStatus,
			List<MettlStudentTestInfo> studentTestInfoList, List<MettlStudentSectionInfo> sectiontInfoList,
			List<MettlSectionQuestionResponse> questionList, List<MettlEvaluatorInfo> evaluatorInfoList) {

		if(testStatus.getResult() != null) {
			MettlResultCandidateTestResultBean result = testStatus.getResult();
			
			mapStudentTestInfo(bean, testStatus, result, studentTestInfoList);
			
			if(result.getSectionMarks() != null && result.getSectionMarks().size() > 0) {
				List<MettlResultSection> sectionMarksList = result.getSectionMarks();
				
				for (int sectionIndex = 0; sectionIndex < sectionMarksList.size() ; sectionIndex++ ) {
					MettlResultSection sectionMarks = sectionMarksList.get(sectionIndex);
					
					mapSectionMarks(bean, sectionMarks, sectionIndex, sectiontInfoList);
					
					if(sectionMarks.getQuestionWiseResponse() != null && sectionMarks.getQuestionWiseResponse().size() > 0) {
						List<MettlResultQuestionWiseResponse> questionWiseResponseList = sectionMarks.getQuestionWiseResponse();
						for (int questionIndex = 0; questionIndex < questionWiseResponseList.size() ; questionIndex++ ) {
							MettlResultQuestionWiseResponse questionWiseResponse = questionWiseResponseList.get(questionIndex);
							mapQuestionResponse(bean, sectionMarks, questionWiseResponse, questionList);
							
							if(questionWiseResponse.getEvaluatorData() != null && questionWiseResponse.getEvaluatorData().size() > 0) {
//								mapEvaluatorData();
								List<MettlResultEvaluatorData> evaluatorList = questionWiseResponse.getEvaluatorData();
								for (MettlResultEvaluatorData evaluatorData : evaluatorList) {
									mapEvaluatorData(bean, sectionMarks, questionWiseResponse, evaluatorData, evaluatorInfoList);
								}
							}
						}
					}
				}
			}
		}
	}


	private void mapEvaluatorData(MettlPGResponseBean bean, MettlResultSection sectionMarks, MettlResultQuestionWiseResponse questionWiseResponse, MettlResultEvaluatorData evaluatorData, List<MettlEvaluatorInfo> evaluatorInfoList) {

		MettlEvaluatorInfo evaluatorInfo = new MettlEvaluatorInfo();
		evaluatorInfo.setSapid(bean.getSapid());
		evaluatorInfo.setScheduleAccessKey(bean.getSchedule_accessKey());
		evaluatorInfo.setSectionName(sectionMarks.getSectionName());
		evaluatorInfo.setQuestionId(questionWiseResponse.getQuestionId());
		evaluatorInfo.setEvaluatorEmail(evaluatorData.getEmail());
		if(StringUtils.isBlank(evaluatorInfo.getEvaluatorEmail())) {
			evaluatorInfo.setEvaluatorEmail("");
		}
		evaluatorInfo.setEvaluatorName(evaluatorData.getName());
		evaluatorInfo.setMarksAwarded(evaluatorData.getMarksAwarded());
		
		
		try {
			byte[] latin1 = evaluatorData.getEvaluationComments().getBytes("ISO-8859-1");
			String evaluationComments = new String(latin1);
			evaluatorInfo.setEvaluationComments(evaluationComments);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			evaluatorInfo.setEvaluationComments(evaluatorData.getEvaluationComments());
		}
		
		
		evaluatorInfo.setEvaluationTime(evaluatorData.getEvaluationTime());
		evaluatorInfo.setEvaluatorRole(evaluatorData.getEvaluatorRole());
		evaluatorInfoList.add(evaluatorInfo);
	}

	private void mapStudentTestInfo(MettlPGResponseBean bean, MettlResultCandidateTestStatusBean testStatus, MettlResultCandidateTestResultBean result2, List<MettlStudentTestInfo> studentTestInfoList) {

		MettlStudentTestInfo studentTestInfo = new MettlStudentTestInfo();
		studentTestInfo.setSapid(bean.getSapid());
		studentTestInfo.setScheduleAccessKey(bean.getSchedule_accessKey());
		studentTestInfo.setEmailId(bean.getEmail());
		studentTestInfo.setStartTime(testStatus.getStartTime());
		studentTestInfo.setEndTime(testStatus.getEndTime());
		studentTestInfo.setCompletionMode(testStatus.getCompletionMode());
		
		MettlResultCandidateTestResultBean result = testStatus.getResult();
		studentTestInfo.setTotalMarks(result.getTotalMarks());
		studentTestInfo.setMaxMarks(result.getMaxMarks());
		studentTestInfo.setPercentile(result.getPercentile());
		studentTestInfo.setAttemptTime(result.getAttemptTime());
		studentTestInfo.setCandidateCredibilityIndex(result.getCandidateCredibilityIndex());
		studentTestInfo.setTotalQuestion(result.getTotalQuestion());
		studentTestInfo.setTotalCorrectAnswers(result.getTotalCorrectAnswers());
		studentTestInfo.setTotalUnAnswered(result.getTotalUnAnswered());
		studentTestInfo.setPdfReport(testStatus.getPdfReport());
		studentTestInfo.setHtmlReport(testStatus.getHtmlReport());
		studentTestInfoList.add(studentTestInfo);
	
	}


	private void mapSectionMarks(MettlPGResponseBean bean, MettlResultSection sectionMarks, int sectionIndex, List<MettlStudentSectionInfo> sectiontInfoList) {

		MettlStudentSectionInfo sectionInfo = new MettlStudentSectionInfo();
		sectionInfo.setSapid(bean.getSapid());
		sectionInfo.setScheduleAccessKey(bean.getSchedule_accessKey());
		sectionInfo.setSectionName(sectionMarks.getSectionName());
		sectionInfo.setSectionNumber(sectionIndex + 1);
		sectionInfo.setTotalMarks(sectionMarks.getTotalMarks());
		sectionInfo.setMaxMarks(sectionMarks.getMaxMarks());
		sectionInfo.setTimeTaken(sectionMarks.getTimeTaken());
		sectionInfo.setTotalQuestion(sectionMarks.getTotalQuestion());
		sectionInfo.setTotalCorrectAnswers(sectionMarks.getTotalCorrectAnswers());
		sectionInfo.setTotalUnAnswered(sectionMarks.getTotalUnAnswered());
		sectiontInfoList.add(sectionInfo);
		
	}


	private void mapQuestionResponse(MettlPGResponseBean bean, MettlResultSection sectionMarks, MettlResultQuestionWiseResponse questionWiseResponse, List<MettlSectionQuestionResponse> questionList) {

		MettlSectionQuestionResponse questionResponse = new MettlSectionQuestionResponse();
		questionResponse.setSapid(bean.getSapid());
		questionResponse.setScheduleAccessKey(bean.getSchedule_accessKey());
		questionResponse.setSectionName(sectionMarks.getSectionName());
		questionResponse.setQuestionId(questionWiseResponse.getQuestionId());
		questionResponse.setApiQuestionType(questionWiseResponse.getApiQuestionType());
		questionResponse.setVersion(questionWiseResponse.getVersion());
		
		if(questionWiseResponse.getQuestionResponse() != null && questionWiseResponse.getApiQuestionType() != null) {
			switch(questionWiseResponse.getApiQuestionType().toUpperCase()) {
				case "LONG_ANSWER" :
					MettlResultQuestionLongAnswerTypeResponse laResp = questionWiseResponse.getQuestionResponse().getLongAnswerTypeResponse();
					if(laResp != null) {
						try {
							byte[] latin1 = laResp.getCandidateResponse().getBytes("ISO-8859-1");
							String studentResponse = new String(latin1);
							questionResponse.setStudentResponse(studentResponse);
						} catch (UnsupportedEncodingException e) {
							questionResponse.setStudentResponse(laResp.getCandidateResponse());
						}
						
					}
					break;
				case "MCQ" :
					MettlResultQuestionMcqTypeResponse mcqResp = questionWiseResponse.getQuestionResponse().getMcqTypeResponse();
					if(mcqResp != null) {
						String mcqResponseString = Integer.toString(mcqResp.getResponseIndex());
						questionResponse.setStudentResponse(mcqResponseString);
					}
					break;
					// No questions of these types currently.
//				case "SHORT_ANSWER" :
//					MettlResultQuestionShortAnswerTypeResponse saResp = questionWiseResponse.getQuestionResponse().getShortAnswerTypeResponse();
//					if(saResp != null) {
//						questionResponse.setStudentResponse(saResp.getCandidateResponse());
//					}
//					break;
//				case "MCA" :
//					MettlResultQuestionMcaTypeResponse mcaResp = questionWiseResponse.getQuestionResponse().getMcaTypeResponse();
//					if(mcaResp != null) {
//						String mcaResponseString = String.join(", ", mcaResp.getResponses());
//						questionResponse.setStudentResponse(mcaResponseString);
//					}
//					break;
				default : 
					questionResponse.setStudentResponse("");
			}
		}
		
		questionResponse.setMinMarks(questionWiseResponse.getMinMarks());
		questionResponse.setMaxMarks(questionWiseResponse.getMaxMarks());
		questionResponse.setMarksScored(questionWiseResponse.getMarksScored());
		questionResponse.setAttempted(questionWiseResponse.isAttempted());
		questionResponse.setTimeSpent(questionWiseResponse.getTimeSpent());
		questionList.add(questionResponse);
	}


	private void mapResultBean(MettlPGResponseBean bean, MettlResultCandidateTestStatusBean testStatus) {

		MettlResultCandidateTestResultBean result = testStatus.getResult();
		bean.setMax_marks(result.getMaxMarks());
		bean.setTotalMarks(result.getTotalMarks());
		
		List<MettlResultSection> sectionMarks = result.getSectionMarks();
		if(sectionMarks != null) {
			// Get marks obtained in each section.
			// Crashes if number of sections < 4 if sectionMarks.size() check is not present for each
			for (MettlResultSection section : sectionMarks) {
				mapSectionDataToSectionNumber(section, bean);
			}
		}

		double totalMarks = bean.getSection1_marks() + bean.getSection2_marks() + bean.getSection3_marks() + bean.getSection4_marks();
		double maxMarks = bean.getSection1_maxmarks() + bean.getSection2_maxmarks() + bean.getSection3_maxmarks() + bean.getSection4_maxmarks();
		
		bean.setMax_marks(maxMarks);
		bean.setTotalMarks(totalMarks);
		
		bean.setStatus("Attempted");
	}

	private void mapSectionDataToSectionNumber(MettlResultSection section, MettlPGResponseBean bean) {
		
		if(section == null || StringUtils.isBlank(section.getSectionName())) {
			bean.setError("Invalid bean format.");
		}
		
		if(section.getSectionName().startsWith("Section 1_")) {
			bean.setSection1_marks(section.getTotalMarks());
			bean.setSection1_maxmarks(section.getMaxMarks());
		} else if(section.getSectionName().startsWith("Section 2_")) {
			bean.setSection2_marks(section.getTotalMarks());
			bean.setSection2_maxmarks(section.getMaxMarks());
		} else if(section.getSectionName().startsWith("Section 3_")) {
			if(section.getSectionName().endsWith("DQ")) {
				calculateTotalAndMaxMarksForDQ(section, 3);
			}
			bean.setSection3_marks(section.getTotalMarks());
			bean.setSection3_maxmarks(section.getMaxMarks());
		} else if(section.getSectionName().startsWith("Section 4_")) {
			if(section.getSectionName().endsWith("DQ")) {
				if("Master".equals(bean.getProgramType()) 
					|| "Diploma".equals(bean.getProgramType())
					|| "Post Graduate Diploma".equals(bean.getProgramType()) 
					|| "Professional Diploma".equals(bean.getProgramType())
					) {
					calculateTotalAndMaxMarksForDQ(section, 3);
				}else {
					calculateTotalAndMaxMarksForDQ(section, 2);
				}
				bean.setSection4_marks(section.getTotalMarks());
				bean.setSection4_maxmarks(section.getMaxMarks());
			} else {
				bean.setSection4_marks(section.getTotalMarks());
				bean.setSection4_maxmarks(section.getMaxMarks());
			}
		}
	}

	private void calculateTotalAndMaxMarksForDQ(MettlResultSection section, int maxQuestions) {
		
		double totalMarks = 0;
		double maxMarks = 0;
		
		Double marks[] = new Double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		Double max[] = new Double[]{0.0, 0.0, 0.0, 0.0, 0.0};
		
		List<MettlResultQuestionWiseResponse> questionResponses = section.getQuestionWiseResponse();
		if(questionResponses != null && questionResponses.size() > 0) {
			for (int index = 0; index < questionResponses.size(); index++) {
				MettlResultQuestionWiseResponse questionResponse = questionResponses.get(index);
				marks[index] = questionResponse.getMarksScored();
				max[index] = questionResponse.getMaxMarks();
			}
		}

		Arrays.sort(marks, Collections.reverseOrder());
		Arrays.sort(max, Collections.reverseOrder());

		for(int i = 0; i < maxQuestions; i++) {
			totalMarks = totalMarks + marks[i];
			maxMarks = maxMarks + max[i];
		}

		section.setTotalMarks(totalMarks);
		section.setMaxMarks(maxMarks);
	}
	
	public void readExcelMettlExamInput(MettlResultsSyncBean inputBean){
		List<MettlPGResponseBean> studentsToFetchResultsFor = excelHelper.readExcelMettlExamInput(inputBean);
		inputBean.setCustomFetchInput(studentsToFetchResultsFor);
	}
	
	public MettlFetchTestResultBean pullMarksFromMettlAPI(MettlResultsSyncBean inputBean) {
		
	
		MettlFetchTestResultBean resultBean  = new MettlFetchTestResultBean();
//		MettlPGResponseBean bean = null;
//		List<MettlStudentTestInfo> studentTestInfoList = null;
//		List<MettlStudentSectionInfo> sectiontInfoList = null;
//		List<MettlSectionQuestionResponse> questionList = null;
//		List<MettlEvaluatorInfo> evaluatorInfo = null;
		List<MettlPGResponseBean> bookings = null;
		try {
			pullMettlMarks.info(
				"\n"+SERVER
				//+ ": "+new Date() //Vilpesh replaced on 2021-12-18
				+ ": "+ DateHelper.currentDateTime(null)
				+ " pullMarksFromMettlAPI Started : " + inputBean.toString()
			);
			
			bookings = resultProcessingDAO.getListOfBookingsToProcessResults(inputBean);
			resultBean.setBookingcount(bookings.size());
			resultBean.setExamYear(inputBean.getExamYear());
			resultBean.setExamMonth(inputBean.getExamMonth());
			
			
			pullMettlMarks.info(
				"\n" + SERVER
				//+ ": "+new Date() //Vilpesh replaced on 2021-12-18
				+ ": "+ DateHelper.currentDateTime(null)
				+ " pullMarksFromMettlAPI " 
				+ " =Query time taken= " 
				+ " bookings.size() - " + bookings.size()
			);
			
			Set<String> immutableQuestionIds = getImmutableBodQuestionIds(inputBean.getExamYear(), inputBean.getExamMonth());
			
			pullMettlMarks.info("Server {}, number of Bod questions found for cycle {}-{} : {} ",
									SERVER,
									resultBean.getExamMonth(),
									resultBean.getExamYear(),
									immutableQuestionIds.size());
			
			if(bookings.size() > 0) {
			
			if(inputBean.getCustomFetchInput() == null) {
				mettlTestResultTaskExecutorService.fetchTestResult(bookings, inputBean.getCreatedBy(), resultBean, immutableQuestionIds);
			}else {
				mettlTestResultTaskExecutorService.fetchMettlTestResultForCandidate(bookings, inputBean.getCreatedBy(), resultBean, immutableQuestionIds);
//			int bookingsSize = bookings.size();
//			for (int index = 0; index < bookingsSize ; index++) {
//				bean = bookings.get(index);
//				pullMettlMarks.info(
//					"\n"+SERVER
//					//+ ": "+new Date() //Vilpesh replaced on 2021-12-18
//					+ ": "+ DateHelper.currentDateTime(null)
//					+ " pullMarksFromMettlAPI "
//					+ "=Fetching Results= " 
//					+ (index + 1) + "/" + bookingsSize
//				);
//				// Loop for bookings and fetch results.
//				bean.setCreatedBy(inputBean.getCreatedBy());
//				bean.setLastModifiedBy(inputBean.getLastModifiedBy());
//	
//				studentTestInfoList = new ArrayList<MettlStudentTestInfo>();
//				sectiontInfoList = new ArrayList<MettlStudentSectionInfo>();
//				questionList = new ArrayList<MettlSectionQuestionResponse>(); 
//				evaluatorInfo = new ArrayList<MettlEvaluatorInfo>();
//				
//				readMettlMarksFromAPIService(
//					bean,
//					studentTestInfoList,
//					sectiontInfoList,
//					questionList,
//					evaluatorInfo,
//					true // flag for checking if this was the first attempt
//				);
//				
//				if(StringUtils.isBlank(bean.getError())) {
//					upsertMarks(bean, resultBean);
//					upsertMetrics(studentTestInfoList, sectiontInfoList, questionList, evaluatorInfo);
//				} else {
//					resultBean.getFailureResponse().add(bean);
//				}
//				
//				//Vilpesh added on 2022-02-18
//				if(null != studentTestInfoList) {
//					studentTestInfoList.clear();
//				}
//				if(null != sectiontInfoList) {
//					sectiontInfoList.clear();
//				}
//				if(null != questionList) {
//					questionList.clear();
//				}
//				if(null != evaluatorInfo) {
//					evaluatorInfo.clear();
//				}
//			}
			}
			}
		} catch (Exception e) {
			pullMettlMarks.info(
					"\n" + SERVER + ": " + DateHelper.currentDateTime(null) + ", pullMarksFromMettlAPI : Error : " + e.getMessage());
		} finally {
			
			
			//Vilpesh added on 2022-02-18
			if(null != bookings) {
				bookings.clear();
			}
		}
		
		return resultBean;
	}

	private int upsetMettlMarksStatus(MettlPGResponseBean bean) {
		
		boolean recordExists = resultProcessingDAO.checkIfRecordExistsInMettlMarks(bean);
		if(recordExists){
			return resultProcessingDAO.updateMettlMarksStatus(bean);
		}else{
			return resultProcessingDAO.insertMettlMarksStatus(bean);
		}
	}
	
	public void transferScoresToOnlineMarks(ModelAndView modelAndView,MettlPGResponseBean inputBean, String userId) throws ParseException {
		int marksListSize = 0;
		MettlPGResponseBean marks = null;
		OnlineExamMarksBean onlineExamMarksBean = null;
		StudentMarksBean bean = null;
		List<ExamBookingTransactionBean> examBookingList = null;
		transferMettlMarksToOnlineMarks
				.info("\n" + SERVER + ": " + DateHelper.currentDateTime(null) + " : transferScoresToOnlineMarks : START");
		List<MettlPGResponseBean> marksList = resultProcessingDAO.getMarksForTransfer(inputBean);
		ArrayList<OnlineExamMarksBean> listToTransfer = new ArrayList<>();
		ArrayList<OnlineExamMarksBean> successList = new ArrayList<>();
		ArrayList<OnlineExamMarksBean> errorList = new ArrayList<>();
		
		marksListSize = marksList.size();//by Vilpesh 2022-02-23
		
		transferMettlMarksToOnlineMarks.info(
			"\n"+SERVER+": "
			//+ new Date()+" transferScoresToOnlineMarks Started " 
			+ DateHelper.currentDateTime(null)+" : transferScoresToOnlineMarks : Started " //by Vilpesh 2022-02-23
			+ "#Records - " + marksListSize
		);

		for (int index = 0; index < marksListSize ; index++) {
			marks = marksList.get(index);
			transferMettlMarksToOnlineMarks.info(
				"\n"+SERVER+": "
				+ DateHelper.currentDateTime(null)+" : transferScoresToOnlineMarks : Collecting " 
				+ (index + 1) + "/" + marksListSize
			);
			onlineExamMarksBean=new OnlineExamMarksBean();
			bean = new StudentMarksBean();
			bean.setYear(marks.getYear());
			bean.setMonth(marks.getMonth());
			//Check if student had registered for exam
			examBookingList = resultProcessingDAO.getConfirmedBookingForGivenYearMonth(marks.getSapid(), marks.getSubject(), marks.getYear(), marks.getMonth());
		
			if(examBookingList == null || examBookingList.size() == 0 || examBookingList.size() > 1){
				transferMettlMarksToOnlineMarks.info(
					"\n"+SERVER+": "
					+ DateHelper.currentDateTime(null)+" : transferScoresToOnlineMarks : Error " 
					+ "Exam Registration not found for record with SAPID:" + marks.getSapid() + " & SUBJECT:" + marks.getSubject()
					+ new Gson().toJson(marks)
				);
				onlineExamMarksBean.setErrorMessage("Exam Registration not found for record with SAPID:" + marks.getSapid() + " & SUBJECT:" + marks.getSubject());
				onlineExamMarksBean.setErrorRecord(true);
				errorList.add(onlineExamMarksBean);
				//by Vilpesh 2022-02-23
				if(null != examBookingList) {
					examBookingList.clear();
				}
				continue;
			} else {
				onlineExamMarksBean.setPrgm_sem_subj_id(marks.getPrgm_sem_subj_id());
				
				onlineExamMarksBean.setSem(examBookingList.get(0).getSem());
				onlineExamMarksBean.setProgram(examBookingList.get(0).getProgram());
				onlineExamMarksBean.setSapid(marks.getSapid());
				onlineExamMarksBean.setName(marks.getStudent_name());
				double total = marks.getTotalMarks();
				onlineExamMarksBean.setTotal(total);
				if(total < 0){
					int roundedTotal = 0;
					onlineExamMarksBean.setRoundedTotal(roundedTotal+"");
				}else{
					int roundedTotal = (int) Math.ceil(total);
					onlineExamMarksBean.setRoundedTotal(roundedTotal+"");
				}
				onlineExamMarksBean.setPart1marks(marks.getSection1_marks());
				onlineExamMarksBean.setPart2marks(marks.getSection2_marks());
				//replaced static string equals check with list.contains check
//				if(examBookingList.get(0).getProgram().equals("BBA") || examBookingList.get(0).getProgram().equals("B.Com") || examBookingList.get(0).getProgram().equals("CBA")) {
				if(CERTIFICATE_PROGRAM_LIST.contains(examBookingList.get(0).getProgram())) {
					// For BBA/BCOM/CBA/CGM certificate program
					onlineExamMarksBean.setPart4marks(marks.getSection3_marks());
					onlineExamMarksBean.setPart3marks(marks.getSection4_marks());
				} else {
					onlineExamMarksBean.setPart3marks(marks.getSection3_marks());
					onlineExamMarksBean.setPart4marks(marks.getSection4_marks());
				}
				onlineExamMarksBean.setSubject(marks.getSubject());
				onlineExamMarksBean.setYear(marks.getYear());
				onlineExamMarksBean.setMonth(marks.getMonth());
				onlineExamMarksBean.setStudentType(marks.getStudentType());
				onlineExamMarksBean.setCreatedBy(userId);
				onlineExamMarksBean.setLastModifiedBy(userId);
				listToTransfer.add(onlineExamMarksBean);
				
				//by Vilpesh 2022-02-23
				if(null != examBookingList) {
					examBookingList.clear();
				}
			}
		}

		transferMettlMarksToOnlineMarks.info(
			"\n"+SERVER+": "
			+ DateHelper.currentDateTime(null)+" : transferScoresToOnlineMarks : Batch Upsert Starting " 
		);
		resultProcessingDAO.batchUpsertOnlineExamMarks(listToTransfer, successList, errorList);	
		
		for (OnlineExamMarksBean bean2 : successList) {
			resultProcessingDAO.changeTransferStatusInMettlMarks(bean2, userId);
		}
		
		modelAndView.addObject("successMessage", successList.size() + " records transferred." );
		modelAndView.addObject("success", "true");
		if(errorList.size()>0) {
			modelAndView.addObject("error", "true");
			modelAndView.addObject("errorMessage", errorList.size() + " records Not Transferred. Please check the table below for more info.");
		
			modelAndView.addObject("failureResponse", errorList);
		}
		
		//by Vilpesh 2022-02-23
		if(null != successList) {
			successList.clear();
		}
		transferMettlMarksToOnlineMarks.info(
				"\n" + SERVER + ": " + DateHelper.currentDateTime(null) + " : transferScoresToOnlineMarks : FINISH");
	}


	public void upsertMarks(MettlPGResponseBean resultBean, MettlFetchTestResultBean responseBean) {

//		pullMettlMarks.info(
//			"\n"+SERVER+": "
//			//+ new Date()+" pullMarksFromMettlAPI " //Vilpesh replaced on 2022-02-18
//			+ DateHelper.currentDateTime(null)+" pullMarksFromMettlAPI "
//			+  "=Upsert= : " + new Gson().toJson(resultBean)
//		);
		if(resultBean.getError() != null) {
			responseBean.getFailureResponse().add(resultBean);
			pullMettlMarks.error(
				"\n"+SERVER+":  pullMarksFromMettlAPI "
				+ "Upsert Failure - " + new Gson().toJson(resultBean)
			);
			return;
		}
		try { 
			upsetMettlMarksStatus(resultBean);
			responseBean.setSuccesscount( responseBean.getSuccesscount() + 1);
		}catch (Exception e) {
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			resultBean.setError(e.getMessage());
			responseBean.getFailureResponse().add(resultBean);
			pullMettlMarks.error(
					"\n"+SERVER+": upsetMettlMarksStatus Error :  " 
					+  "=Upsert= : " + errors.toString()
				);
		}
	}

	public void upsertMetrics(List<MettlStudentTestInfo> studentTestInfoList,
			List<MettlStudentSectionInfo> sectiontInfoList, List<MettlSectionQuestionResponse> questionList,
			List<MettlEvaluatorInfo> evaluatorInfo) {

		try {
			resultProcessingDAO.batchUpsertMettlStudentMarks(studentTestInfoList);
			resultProcessingDAO.batchUpsertMettlStudentSectionInfo(sectiontInfoList);
			resultProcessingDAO.batchUpsertMettlQuestionResponse(questionList);
			resultProcessingDAO.batchUpsertMettlEvaluatorInfo(evaluatorInfo);
		}catch (Exception e) {
			
		}
		
	}
	
	
	public List<MettlStudentTestInfo> grantBenefitOfDoubtToStudentsForQuestion(String questionId,String examYear,String examMonth) {

		applybodPG.info(SERVER+ " grantBenefitOfDoubtToStudentsForQuestion Started " + questionId
		);
		List<MettlStudentTestInfo> testsForBOD = new ArrayList<MettlStudentTestInfo>();

		List<MettlSectionQuestionResponse> listOfQuestionsForBonusMarks = resultProcessingDAO.getStudentResponsesForBenefitOfDoubtQuestion(questionId,examYear,examMonth);

		applybodPG.info(SERVER+ " grantBenefitOfDoubtToStudentsForQuestion " + questionId
			+ " Found total count : " + listOfQuestionsForBonusMarks.size()
		);
		

		for (int index = 0; index < listOfQuestionsForBonusMarks.size() ; index++ ) {

			MettlSectionQuestionResponse mettlSectionQuestionResponse = listOfQuestionsForBonusMarks.get(index);

			applybodPG.info(SERVER+ " grantBenefitOfDoubtToStudentsForQuestion " + questionId+" "
				+ (index + 1) + "/" + listOfQuestionsForBonusMarks.size()
			);
			MettlStudentTestInfo testInfo = resultProcessingDAO.getStudentTestInfo(mettlSectionQuestionResponse.getSapid(), mettlSectionQuestionResponse.getScheduleAccessKey());
			List<MettlStudentSectionInfo> sections = resultProcessingDAO.getStudentTestSectionInfoList(mettlSectionQuestionResponse.getSapid(), mettlSectionQuestionResponse.getScheduleAccessKey());
			for (MettlStudentSectionInfo sectionInfo : sections) {

				List<MettlSectionQuestionResponse> questionsForSection = resultProcessingDAO.getQuestionsForSection(
					sectionInfo.getSapid(), 
					sectionInfo.getScheduleAccessKey(),
					sectionInfo.getSectionName()
				);
				
				for (MettlSectionQuestionResponse question : questionsForSection) {
//
//					List<MettlEvaluatorInfo> evaluatorInfo = resultProcessingDAO.getEvaluatorInfoForQuestion(
//						question.getSapid(), 
//						question.getScheduleAccessKey(),
//						question.getSectionName(),
//						question.getQuestionId()
//					);
					if(questionId.equals(question.getQuestionId())) {
						question.setBenfitOfDoubtQuestion(true);
						double bonusMarksToAdd = 0;
						if(question.getMarksScored() != question.getMaxMarks()) {
							// Get the bonus marks to award for this question
							bonusMarksToAdd = question.getMaxMarks() - question.getMarksScored();
						}
						question.setBonusMarks(bonusMarksToAdd);
						sectionInfo.setBenfitOfDoubtSection(true);
					}
//					question.setEvaluatorInfo(evaluatorInfo);
				}
				sectionInfo.setQuestionsForSection(questionsForSection);
			}
			testInfo.setSections(sections);
			updateTestInfoForBenefitOfDoubtProcessing(testInfo);
			testsForBOD.add(testInfo);
		}
		
		return testsForBOD;
	}


	public List<MettlStudentTestInfo> checkAllDQQuestionScores() {

		List<MettlStudentTestInfo> testsForBOD = new ArrayList<MettlStudentTestInfo>();

		List<MettlStudentTestInfo> listOfUniqueTests = resultProcessingDAO.getAllStudentTestInfos();

		for (int index = 0; index < listOfUniqueTests.size() ; index++ ) {

			MettlStudentTestInfo testInfo = listOfUniqueTests.get(index);
			List<MettlStudentSectionInfo> sections = resultProcessingDAO.getStudentTestSectionInfoListForDQ(testInfo.getSapid(), testInfo.getScheduleAccessKey());
			testInfo.setSections(sections);
			
			for (MettlStudentSectionInfo sectionInfo : sections) {

				List<MettlSectionQuestionResponse> questionsForSection = resultProcessingDAO.getQuestionsForSection(
					sectionInfo.getSapid(), sectionInfo.getScheduleAccessKey(), sectionInfo.getSectionName()
				);
				sectionInfo.setQuestionsForSection(questionsForSection);
			}
			updateTestInfoForBenefitOfDoubtProcessing(testInfo);

			MettlPGResponseBean beanToCheck = new MettlPGResponseBean();
			beanToCheck.setSchedule_accessKey(testInfo.getScheduleAccessKey());
			for (MettlStudentSectionInfo section : sections) {
				updateMarksInResultBean(section, beanToCheck);
			}
			
			MettlPGResponseBean dataInDB = resultProcessingDAO.fetchMarksForStudentTestInfo(testInfo);
			Double dbDataSection4Score = dataInDB.getSection4_marks();
			Double actualSection4Score = beanToCheck.getSection4_marks();
			if(!dbDataSection4Score.equals(actualSection4Score)) {
				resultProcessingDAO.insertCorrectionInfo(dataInDB, beanToCheck);
			}
		}
		
		return testsForBOD;
	}
	
	
	public void updateMarksForBenefitOfDoubtQuestions( List<MettlStudentTestInfo> testInfoList, List<MettlStudentTestInfo> successList, List<MettlStudentTestInfo> errorList, String qid) {

		for (int index = 0; index < testInfoList.size() ; index++ ) {
			
			MettlStudentTestInfo mettlStudentTestInfo = testInfoList.get(index);
			applybodPG.info(SERVER+ " updateMarksForBenefitOfDoubtQuestions "+ (index + 1) + "/" + testInfoList.size()
			);
			applybodPG.info("qid "+qid+" Sapid "+mettlStudentTestInfo.getSapid()+" ScheduleAccessKey "+mettlStudentTestInfo.getScheduleAccessKey());
			updateMarksinMettlMarksTable(mettlStudentTestInfo, successList, errorList,qid);
			if(mettlStudentTestInfo.getError() == null) {
				updateMarksForTestInMettlSchema(mettlStudentTestInfo, successList, errorList,qid);
			}
		}
	}

	private void updateMarksinMettlMarksTable(MettlStudentTestInfo mettlStudentTestInfo, List<MettlStudentTestInfo> successList, List<MettlStudentTestInfo> errorList,String qid) {
		MettlPGResponseBean bean;
		try{
			bean = resultProcessingDAO.fetchMarksForStudentTestInfo(mettlStudentTestInfo);
		}catch (Exception e) {
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			applybodPG.error("Error Fetching Old student test info - Not Found " + errors.toString() );
			mettlStudentTestInfo.setError("Error Fetching Old student test info - Not Found " + e.getMessage() );
			mettlStudentTestInfo.setErrorQuestionId(qid);
			errorList.add(mettlStudentTestInfo);
			return;
		}
		if(bean != null) {
			List<MettlStudentSectionInfo> sections = mettlStudentTestInfo.getSections();
			for (MettlStudentSectionInfo section : sections) {
				updateMarksInResultBean(section, bean);
				bean.setLastModifiedBy(APPLY_BOD_STR);
				resultProcessingDAO.updateMettlMarksStatus(bean);
				applybodPG.info("updateMarksinMettlMarksTable Schedule_id " + bean.getSchedule_id()+" Subject "+bean.getSubject()+" Sapid "+ bean.getSapid());
			}
		}
		
	}
	
	private void updateMarksInResultBean(MettlStudentSectionInfo section, MettlPGResponseBean bean) {

		if(section.getSectionName().startsWith("Section 1_")) {
			bean.setSection1_marks(section.getTotalMarks() + section.getBonusMarks());
			bean.setSection1_maxmarks(section.getMaxMarks());
		} else if(section.getSectionName().startsWith("Section 2_")) {
			bean.setSection2_marks(section.getTotalMarks() + section.getBonusMarks());
			bean.setSection2_maxmarks(section.getMaxMarks());
		} else if(section.getSectionName().startsWith("Section 3_")) {
			if(section.getSectionName().endsWith("DQ")) {
				calculateTotalAndMaxMarksForDQForBOD(section, 3);
			}
			bean.setSection3_marks(section.getTotalMarks());
			bean.setSection3_maxmarks(section.getMaxMarks());
		} else if(section.getSectionName().startsWith("Section 4_")) {
			if(section.getSectionName().endsWith("DQ")) {
				if("Master".equals(bean.getProgramType()) 
						|| "Diploma".equals(bean.getProgramType())
						|| "Post Graduate Diploma".equals(bean.getProgramType()) 
						|| "Professional Diploma".equals(bean.getProgramType())
						) {
					calculateTotalAndMaxMarksForDQForBOD(section, 3);
					}else {
						calculateTotalAndMaxMarksForDQForBOD(section, 2);
					}
				bean.setSection4_marks(section.getTotalMarks());
				bean.setSection4_maxmarks(section.getMaxMarks());
			}
			bean.setSection4_marks(section.getTotalMarks());
			bean.setSection4_maxmarks(section.getMaxMarks());
		}

		double totalMarks = bean.getSection1_marks() + bean.getSection2_marks() + bean.getSection3_marks() + bean.getSection4_marks();
		double maxMarks = bean.getSection1_maxmarks() + bean.getSection2_maxmarks() + bean.getSection3_maxmarks() + bean.getSection4_maxmarks();
		
		bean.setMax_marks(maxMarks);
		bean.setTotalMarks(totalMarks);
	}
	
	private void calculateTotalAndMaxMarksForDQForBOD(MettlStudentSectionInfo section, int maxQuestions) {

		
		double totalMarks = 0;
		double maxMarks = 0;

		Double marks[] = new Double[]{0.0, 0.0, 0.0, 0.0, 0.0};
		Double max[] = new Double[]{0.0, 0.0, 0.0, 0.0, 0.0};
		
		List<MettlSectionQuestionResponse> questionResponses = section.getQuestionsForSection();
		if(questionResponses != null && questionResponses.size() > 0) {
			for (int index = 0; index < questionResponses.size(); index++) {
				MettlSectionQuestionResponse questionResponse = questionResponses.get(index);
				marks[index] = questionResponse.getMarksScored() + questionResponse.getBonusMarks();
				max[index] = questionResponse.getMaxMarks();
			}
		}

		Arrays.sort(marks, Collections.reverseOrder());
		Arrays.sort(max, Collections.reverseOrder());

		for(int i = 0; i < maxQuestions; i++) {
			totalMarks = totalMarks + marks[i];
			maxMarks = maxMarks + max[i];
		}

		section.setTotalMarks(totalMarks);
		section.setMaxMarks(maxMarks);
	}
	

	private void updateMarksForTestInMettlSchema(MettlStudentTestInfo mettlStudentTestInfo, List<MettlStudentTestInfo> successList, List<MettlStudentTestInfo> errorList, String qid) {
		try {
			List<MettlStudentSectionInfo> sections = mettlStudentTestInfo.getSections();
			for (MettlStudentSectionInfo section : sections) {
				List<MettlSectionQuestionResponse> questionsForSection = section.getQuestionsForSection();
				for (MettlSectionQuestionResponse question : questionsForSection) {
					if(qid.equals(question.getQuestionId())) {
					resultProcessingDAO.updateBonusMarksForQuestion(question);
					applybodPG.info("updateBonusMarksForQuestion - BonusMarks " + question.getBonusMarks()+" Sapid "+question.getSapid()+" QuestionId "+question.getQuestionId());
					}
				}
			}
			successList.add(mettlStudentTestInfo);
		}catch (Exception e) {
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			applybodPG.error("Error Updating in student test info - " + errors.toString() );
			mettlStudentTestInfo.setError("Error Updating in student test info - " + e.getMessage() );
			mettlStudentTestInfo.setErrorQuestionId(qid);
			errorList.add(mettlStudentTestInfo);
		}
	}
	
	private void updateTestInfoForBenefitOfDoubtProcessing(MettlStudentTestInfo studentTestInfo) {
		double bonusMarksForTest = 0;
		
		List<MettlStudentSectionInfo> sections = studentTestInfo.getSections();
		for (MettlStudentSectionInfo section : sections) {
			// if section is marked for benefit of doubt under the current query.
			double bonusMarksForSection = 0;
			List<MettlSectionQuestionResponse> questions = section.getQuestionsForSection();
			for (MettlSectionQuestionResponse question : questions) {
				bonusMarksForSection = bonusMarksForSection + question.getBonusMarks();
			}
			section.setBonusMarks(bonusMarksForSection);
			bonusMarksForTest = bonusMarksForTest + bonusMarksForSection;
		}
		studentTestInfo.setBonusMarks(bonusMarksForTest);
	}
	

	public List<MettlStudentTestInfo> getStudentTestInfo(MettlResultsSyncBean inputBean) {
		return resultProcessingDAO.getMettlMarksEvaluationInfo(inputBean);
	}
	public List<MettlStudentSectionInfo> getStudentTestSectionInfo(MettlResultsSyncBean inputBean) {
		return resultProcessingDAO.getMettlStudentTestInfoToBean(inputBean);
	}
	public List<MettlSectionQuestionResponse> getStudentTestSectionQuestionInfo(MettlResultsSyncBean inputBean) {
		return resultProcessingDAO.getQuestionInfo(inputBean);
	}
	public List<MettlEvaluatorInfo> getStudentTestSectionEvaluationInfo(MettlResultsSyncBean inputBean) {
		return resultProcessingDAO.getQuestionEvaluatorInfo(inputBean);
	}
	public Map<String, String> getQuestionMap() {
		return resultProcessingDAO.getAllQuestions();
	}
	
	
	public void getTestStatusForAllInSchedule(String schedule_accessKey, List<MettlResultCandidateBean> list, boolean firstAttempt, int limit){
		try {
			mettlHelper.setBaseUrl(MettlBaseUrl);
			mettlHelper.setPrivateKey(PG_METTL_PRIVATE_KEY);
			mettlHelper.setPublicKey(PG_METTL_PUBLIC_KEY);
			String evalData = "true";
			String questionData =  "true";
			JsonObject jsonResponse = mettlHelper.getTestStatusForAllInSchedule(schedule_accessKey, evalData, questionData, limit);
			String status = jsonResponse.get("status").getAsString();
			if("SUCCESS".equalsIgnoreCase(status)) {
				jsonResponseReceived(jsonResponse, list, true);
			}else {
				if(firstAttempt) {
					fetchMettlAllCandidateTestResult.warn("firstAttempt error response received : "+jsonResponse.toString()+" / Trying second Attempt to getTestStatusForAllInSchedule for schedule_accessKey "+schedule_accessKey);
					Thread.sleep(3000);
					getTestStatusForAllInSchedule(schedule_accessKey, list, false, 50);
				}else {
					fetchMettlAllCandidateTestResult.error("tried second attempt for schedule_accessKey "+schedule_accessKey +"  response received : "+jsonResponse.toString());
				}
			}
			
		}catch (Exception e) {
			if(firstAttempt) {
				fetchMettlAllCandidateTestResult.warn(" exception in firstAttempt / Trying second Attempt to getTestStatusForAllInSchedule for schedule_accessKey "+schedule_accessKey);
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				List<MettlResultCandidateBean> newlist = new ArrayList<>();
				getTestStatusForAllInSchedule(schedule_accessKey, newlist, false, 50);
			}else {
			
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				fetchMettlAllCandidateTestResult.error("tried second attempt for schedule_accessKey  "+schedule_accessKey +"  errors : "+errors.toString());
			}
		}
	}
	
	private void jsonResponseReceived(JsonObject jsonResponse, List<MettlResultCandidateBean> list, boolean firstAttempt) throws Exception{
		JsonParser parser = new JsonParser(); 
		JsonElement mJson =  parser.parse(jsonResponse.toString());
		Gson gson = new Gson();
		MettlResultAPIResponseBean response = gson.fromJson(mJson, MettlResultAPIResponseBean.class);
		fetchMettlAllCandidateTestResult.info("response.getStatus() "+response.getStatus()+" response.getCandidates() "+response.getCandidates().size()+" response.getPaging().getNext() "+response.getPaging().getNext());
		list.addAll(response.getCandidates());
		if(response.getPaging().getNext() != null) {
			JsonObject jsonNextResponse = mettlHelper.getTestStatusForAllInSchedule(response.getPaging().getNext());
			if("SUCCESS".equalsIgnoreCase(jsonNextResponse.get("status").getAsString())) {
					jsonResponseReceived(jsonNextResponse, list, true);
			}else {
				if(firstAttempt) {
					fetchMettlAllCandidateTestResult.warn("firstAttempt error response received : "+jsonNextResponse.toString()+" / Trying second Attempt to getTestStatusForAllInSchedule for Next url  "+response.getPaging().getNext());
					Thread.sleep(3000);
					jsonResponseReceived(jsonResponse, list, false);
				}else {
					fetchMettlAllCandidateTestResult.error("tried second attempt for Next url "+response.getPaging().getNext() +"  response received : "+jsonNextResponse.toString());
					throw new Exception("tried second attempt for Next url "+response.getPaging().getNext() );    
					
				}
			}
		}
	}
	
	protected void mapandupsertData(List<MettlPGResponseBean> bookings, MettlFetchTestResultBean responseBean,  String createdBy, Set<String> bodQuestionIds) {
		for (MettlPGResponseBean bean : bookings) {
			
			// Loop for bookings and fetch results.
			bean.setCreatedBy(createdBy);
			bean.setLastModifiedBy(createdBy);

			List<MettlStudentTestInfo> studentTestInfoList = new ArrayList<MettlStudentTestInfo>();
			List<MettlStudentSectionInfo> sectiontInfoList = new ArrayList<MettlStudentSectionInfo>();
			List<MettlSectionQuestionResponse> questionList = new ArrayList<MettlSectionQuestionResponse>(); 
			List<MettlEvaluatorInfo> evaluatorInfo = new ArrayList<MettlEvaluatorInfo>();
			
			mapJsontoBean(bean,studentTestInfoList, sectiontInfoList, questionList, evaluatorInfo );
			
			if (StringUtils.isBlank(bean.getError())) {
				//updates mettl_marks and section_question_response variables if bod applicable before upserting 
				applyBodIfApplicable(bean, sectiontInfoList, questionList, bodQuestionIds);

				// method was internally checking for error again if it had error, commented this
				// and called method below for collective exception handling
				// int mettlMarksUpserted = upsertMarks(bean, resultBean);
				
				//saves records in mettl_marks, online_marks, marks and mettl schema tables
				saveResponseFromMettl(bean, responseBean, studentTestInfoList, sectiontInfoList, questionList, evaluatorInfo);

				bean.setMettlResultCandidateBean(null);

			} else {
				responseBean.getFailureResponse().add(bean);
			}
			
			if(null != studentTestInfoList) {
				studentTestInfoList.clear();
			}
			if(null != sectiontInfoList) {
				sectiontInfoList.clear();
			}
			if(null != questionList) {
				questionList.clear();
			}
			if(null != evaluatorInfo) {
				evaluatorInfo.clear();
			}
		}		
	}
	
	/**
	 * Updates bonus marks in question marks and ultimately in mettl marks as well
	 * 
	 * @param mettlPGResponseBean to get and update mettl marks
	 * @param sectiontInfoList info to add bonus marks for each section
	 * @param questionList question list to compare with question Ids
	 * @param bodQuestionIds bodQuestionIds
	 * 
	 * @author Swarup Singh Rajpurohit
	 */
	public void applyBodIfApplicable(MettlPGResponseBean bean, List<MettlStudentSectionInfo> sectiontInfoList, 
			List<MettlSectionQuestionResponse> questionList, Set<String> bodQuestionIds) {
		
		// return if no bod questions are found, didn't throw exception as there might
		// be a case in near future where there are not BOD questions for an exam cycle
		if(bodQuestionIds == null || bodQuestionIds.isEmpty()) {
			return;
		}

		//return if student had not attempted exam on mettl since all the data will be empty anyway
		if (!METTL_ATTEMPTED.equalsIgnoreCase(bean.getStatus())) {
			pullMettlMarks.info("applyBodIfApplicable {} {} mettl status is not Attempted : {} so skipping record for bodcheck!",
					bean.getSapid(), bean.getSubject(), bean.getStatus());
			return;
		}

		//grouping by section name so we can iterate over sections and their questions to avoid iterating over all questions for all sections
		Map<String, List<MettlSectionQuestionResponse>> questionsPerSection = questionList.stream().collect(Collectors.groupingBy(MettlSectionQuestionResponse::getSectionName));

		for (int i = 0; i < sectiontInfoList.size(); i++) {

			MettlStudentSectionInfo section = sectiontInfoList.get(i);
			double sectionBonusMarks = 0.0;

			//getting questions by section name
			List<MettlSectionQuestionResponse> questionsFromSection = questionsPerSection.get(section.getSectionName());
			
			for (int k = 0; k < questionsFromSection.size(); k++) {
				MettlSectionQuestionResponse question = questionsFromSection.get(k);
				double questionBonusMarks = 0.0;
				
				//if satisfies condition i.e. questionIds set contains this questionId and student hasn't score full marks
				if (satisfiesBodApplicableConditions(question, bodQuestionIds)) {
					
					//if student hasn't scored full marks, remaining marks will be allotted as bonus marks
					questionBonusMarks = question.getMaxMarks() - question.getMarksScored();
					question.setBonusMarks(questionBonusMarks);
					
					//setting bod applied as true to differentiate record
					bean.setBodApplied(true);
					bean.setLastModifiedBy(APPLY_BOD_STR);
					
					pullMettlMarks.info("applyBodIfApplicable {} {} found for question id : {}, added bonus marks : {}",
											bean.getSapid(),
											bean.getSubject(),
											question.getQuestionId(),
											questionBonusMarks);
					
					//adding total bonus marks on section level
					sectionBonusMarks = sectionBonusMarks + questionBonusMarks;
				}
			}
			section.setQuestionsForSection(questionsFromSection);
			section.setBonusMarks(sectionBonusMarks);
			
			//updates MettlPGResponseBean bean to update marks in mettl_marks accordingly + best of logic
			updateMettlMarksForBod(section, bean);
		}
		
		//after iteration is complete set new total marks and marks scored to update in mettl_marks
		double totalMarks = bean.getSection1_marks() + bean.getSection2_marks() + bean.getSection3_marks() + bean.getSection4_marks();
		double maxMarks = bean.getSection1_maxmarks() + bean.getSection2_maxmarks() + bean.getSection3_maxmarks() + bean.getSection4_maxmarks();

		bean.setMax_marks(maxMarks);
		bean.setTotalMarks(totalMarks);

	}
	
	//transfers to marks and online marks
	private int transferToMarks(MettlPGResponseBean mettlPGResponseBean, MettlFetchTestResultBean mettlFetchTestResultBean) {
		
		int transferredRecordCount = 0;
		
		//prepared online marks bean from mettl_marks bean
		OnlineExamMarksBean onlineExamMarksBean = populateOnlineMarksBean(mettlPGResponseBean, AUTO_TRANSFER_MARKS_STR);
		
		int onlineMarksUpserted = resultProcessingDAO.upsertOnlineMarksAndMarks(onlineExamMarksBean);
		
		// based on data upserted in online marks updates moved on online marks flag as
		// 'Y' otherwise sets this record as error to show in error excel file on front end
		if(onlineMarksUpserted > 0) {
			transferredRecordCount = resultProcessingDAO.changeTransferStatusInMettlMarks(onlineExamMarksBean, mettlPGResponseBean.getLastModifiedBy());
		} else {
			
			pullMettlMarks.error("{} {} Marks updated in mettl_marks but not transferred!",
									mettlPGResponseBean.getSapid(),
									mettlPGResponseBean.getSubject());
			
			mettlPGResponseBean.setError("Marks Not upserted in online marks!!");
			mettlFetchTestResultBean.getFailureResponse().add(mettlPGResponseBean);
		}
		
		return transferredRecordCount;
	}

	//method as it copied from existing logic see method  : transferScoresToOnlineMarks in same class for reference
	private OnlineExamMarksBean populateOnlineMarksBean(final MettlPGResponseBean mettlPGResponseBean, final String createdBy) {
		OnlineExamMarksBean onlineExamMarksBean = new OnlineExamMarksBean();
		
		onlineExamMarksBean.setPrgm_sem_subj_id(mettlPGResponseBean.getPrgm_sem_subj_id());
		onlineExamMarksBean.setSem(mettlPGResponseBean.getSem());
		onlineExamMarksBean.setProgram(mettlPGResponseBean.getProgram());
		onlineExamMarksBean.setSapid(mettlPGResponseBean.getSapid());
		onlineExamMarksBean.setName(mettlPGResponseBean.getStudent_name());
		
		double total = mettlPGResponseBean.getTotalMarks();
		onlineExamMarksBean.setTotal(total);
		
		if (total < 0) {
			int roundedTotal = 0;
			onlineExamMarksBean.setRoundedTotal(roundedTotal + "");
		} else {
			int roundedTotal = (int) Math.ceil(total);
			onlineExamMarksBean.setRoundedTotal(roundedTotal + "");
		}
		
		onlineExamMarksBean.setPart1marks(mettlPGResponseBean.getSection1_marks());
		onlineExamMarksBean.setPart2marks(mettlPGResponseBean.getSection2_marks());
		
		if (CERTIFICATE_PROGRAM_LIST.contains(mettlPGResponseBean.getProgram())) {
			// For BBA/BCOM/CBA/CGM certificate program
			onlineExamMarksBean.setPart4marks(mettlPGResponseBean.getSection3_marks());
			onlineExamMarksBean.setPart3marks(mettlPGResponseBean.getSection4_marks());
		} else {
			onlineExamMarksBean.setPart3marks(mettlPGResponseBean.getSection3_marks());
			onlineExamMarksBean.setPart4marks(mettlPGResponseBean.getSection4_marks());
		}
		
		onlineExamMarksBean.setSubject(mettlPGResponseBean.getSubject());
		onlineExamMarksBean.setYear(mettlPGResponseBean.getYear());
		onlineExamMarksBean.setMonth(mettlPGResponseBean.getMonth());
		onlineExamMarksBean.setStudentType(mettlPGResponseBean.getStudentType());
		
		//updates created and modified by
		onlineExamMarksBean.setCreatedBy(createdBy);
		onlineExamMarksBean.setLastModifiedBy(createdBy);
		
		return onlineExamMarksBean;
	}
	
	//for best of logic and to update bonus marks in mettl_marks object
	private void updateMettlMarksForBod(MettlStudentSectionInfo section, MettlPGResponseBean bean) {

		if(section.getSectionName().startsWith("Section 1_")) {
			bean.setSection1_marks(section.getTotalMarks() + section.getBonusMarks());
			bean.setSection1_maxmarks(section.getMaxMarks());
		} else if(section.getSectionName().startsWith("Section 2_")) {
			bean.setSection2_marks(section.getTotalMarks() + section.getBonusMarks());
			bean.setSection2_maxmarks(section.getMaxMarks());
		} else if(section.getSectionName().startsWith("Section 3_")) {
			if(section.getSectionName().endsWith("DQ")) {
				calculateTotalAndMaxMarksForDQForBOD(section, 3);
				bean.setSection3_marks(section.getTotalMarks());
				bean.setSection3_maxmarks(section.getMaxMarks());
			} else {
				bean.setSection3_marks(section.getTotalMarks() + section.getBonusMarks());
				bean.setSection3_maxmarks(section.getMaxMarks());
			}
		} else if (section.getSectionName().startsWith("Section 4_")) {
			if (section.getSectionName().endsWith("DQ")) {
				if ("Master".equals(bean.getProgramType()) 
						|| "Diploma".equals(bean.getProgramType())
						|| "Post Graduate Diploma".equals(bean.getProgramType())
						|| "Professional Diploma".equals(bean.getProgramType())) {
					calculateTotalAndMaxMarksForDQForBOD(section, 3);
				} else {
					calculateTotalAndMaxMarksForDQForBOD(section, 2);
				}
				bean.setSection4_marks(section.getTotalMarks());
				bean.setSection4_maxmarks(section.getMaxMarks());
			} else {
				bean.setSection4_marks(section.getTotalMarks());
				bean.setSection4_maxmarks(section.getMaxMarks());
			}
		}

	}
	
	//static method that checks bod applicable conditions
	private static boolean satisfiesBodApplicableConditions(MettlSectionQuestionResponse question, Set<String> bodQuestionIds) {

		return bodQuestionIds.contains(question.getQuestionId()) && question.getMaxMarks() != question.getMarksScored();
	}

	private void mapJsontoBean(MettlPGResponseBean bean, List<MettlStudentTestInfo> studentTestInfoList,
			List<MettlStudentSectionInfo> sectiontInfoList, List<MettlSectionQuestionResponse> questionList,
			List<MettlEvaluatorInfo> evaluatorInfo) {

			// Initialize all marks to 0
			bean.setMax_marks(0);
			bean.setTotalMarks(0);
			bean.setSection1_marks(0);
			bean.setSection2_marks(0);
			bean.setSection3_marks(0);
			bean.setSection4_marks(0);
			
			MettlResultCandidateBean candidate = bean.getMettlResultCandidateBean();
			if(candidate != null) {
				MettlResultCandidateTestStatusBean testStatus = candidate.getTestStatus();
				if(testStatus != null) {
					if("Completed".equalsIgnoreCase(testStatus.getStatus())) {

						// Map the student result bean
						mapResultBean(bean, testStatus);

						// Map the data for analytics beans
						mapTestInfoBeans(
							bean, 
							testStatus, 
							studentTestInfoList,
							sectiontInfoList,
							questionList,
							evaluatorInfo
						);
					} else {
						bean.setStatus("Not Attempted");
					}
					
				} else {
					bean.setError("Test Status field is null! for emailId : "+bean.getEmail()+" / Schedule_accessKey :"+bean.getSchedule_accessKey() );
					pullMettlMarks.error("testStatus field is null! for emailId : "+bean.getEmail()+" / Schedule_accessKey :"+bean.getSchedule_accessKey() );
				}
			} else {
				bean.setError("Candidate field is null! for emailId "+bean.getEmail()+" / Schedule_accessKey :"+bean.getSchedule_accessKey() );
				pullMettlMarks.error("Candidate field is null! for emailId "+bean.getEmail()+" / Schedule_accessKey :"+bean.getSchedule_accessKey());
			}
	
	}
	
	protected void vaildateMettlResultWithConfirmBooking( List<MettlPGResponseBean> confirmBookingList,List<MettlResultCandidateBean> mettlCandidateResultList) {
		
		for(MettlPGResponseBean bean : confirmBookingList) {
			try {
				if(bean.getEmail() != null) {
					MettlResultCandidateBean  candidateBean = mettlCandidateResultList.stream()
							.filter(obj1 -> bean.getEmail().equalsIgnoreCase(obj1.getEmail()))
							.findFirst().get() ;
					bean.setMettlResultCandidateBean(candidateBean);
			   }
			}catch (Exception e) {
				// TODO: handle exception
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				pullMettlMarks.error( "Error in vaildateMettlResultWithConfirmBooking mettlCandidateResultList.toString():	"+mettlCandidateResultList.toString()+" / emailId :"+bean.getEmail()+" / Schedule_accessKey :"+bean.getSchedule_accessKey()+" errors "+errors.toString());
				
			}
		}
	
	}
	
	public MettlFetchTestResultBean pullMettlMarksRestCall(MettlResultsSyncBean bean) {
		try {
			bean.setFileData(null);
			String url = PG_RESULT_PULL_BASE_URL+"/exam/admin/m/pullMettlMarks";
			HttpHeaders headers = new HttpHeaders();
	        headers.add("Content-Type", "application/json"); 
	        HttpEntity<MettlResultsSyncBean> request = new HttpEntity<MettlResultsSyncBean>(bean,headers);
	        RestTemplate restTemplate = new RestTemplate();
	        ResponseEntity<String> response = restTemplate.exchange( url, HttpMethod.POST, request, String.class);
	        JsonObject jsonObject = new JsonParser().parse(response.getBody()).getAsJsonObject();
	        JsonParser parser = new JsonParser(); 
			JsonElement mJson =  parser.parse(jsonObject.toString());
			Gson gson = new Gson();
			MettlFetchTestResultBean resultBean = gson.fromJson(mJson, MettlFetchTestResultBean.class);
			
	        return resultBean;	
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
        
	}
	
	public MettlFetchTestResultBean getPullProcessStatus() {
		MettlFetchTestResultBean resultBean = new MettlFetchTestResultBean ();
		try {
			String url = PG_RESULT_PULL_BASE_URL+"/exam/admin/m/getPullProcessStatusRestCall";
			HttpHeaders headers = new HttpHeaders();
	        headers.add("Content-Type", "application/json"); 
	        HttpEntity<String> entity = new HttpEntity<String>("",headers);
	        RestTemplate restTemplate = new RestTemplate();
	        ResponseEntity<String> response = restTemplate.exchange( url, HttpMethod.GET, entity, String.class);
	        JsonObject jsonObject = new JsonParser().parse(response.getBody()).getAsJsonObject();
	        JsonParser parser = new JsonParser(); 
			JsonElement mJson =  parser.parse(jsonObject.toString());
			Gson gson = new Gson();
			resultBean = gson.fromJson(mJson, MettlFetchTestResultBean.class);
			
	        return resultBean;	
		}catch(Exception e) {
			e.printStackTrace();
			return resultBean;
		}
        
	}
	
	
	public MettlFetchTestResultBean  getPullTaskStatus() {
			return mettlTestResultTaskExecutorService.getPullTaskStatus();
	}
	
	protected void fetchMettlTestResultForCandidate(MettlPGResponseBean bean, String createdBy, MettlFetchTestResultBean resultBean, Set<String> bodQuestionIds) {
		
		List<MettlStudentTestInfo> studentTestInfoList = null;
		List<MettlStudentSectionInfo> sectiontInfoList = null;
		List<MettlSectionQuestionResponse> questionList = null;
		List<MettlEvaluatorInfo> evaluatorInfo = null;
		
		// Loop for bookings and fetch results.
		bean.setCreatedBy(createdBy);
		bean.setLastModifiedBy(createdBy);
		studentTestInfoList = new ArrayList<MettlStudentTestInfo>();
		sectiontInfoList = new ArrayList<MettlStudentSectionInfo>();
		questionList = new ArrayList<MettlSectionQuestionResponse>(); 
		evaluatorInfo = new ArrayList<MettlEvaluatorInfo>();
		
		readMettlMarksFromAPIService(
			bean,
			studentTestInfoList,
			sectiontInfoList,
			questionList,
			evaluatorInfo,
			true // flag for checking if this was the first attempt
		);
		
		if(StringUtils.isBlank(bean.getError())) {
			
			//adds bod bonus marks in bonus variable if applicable before upserting
			applyBodIfApplicable(bean, sectiontInfoList, questionList, bodQuestionIds);
			
			// method was internally checked for error again if it had error, commented this
			// and called method below for collective exception handling 
			//int mettlMarksUpserted = upsertMarks(bean, resultBean);
			
			//saves records in mettl_marks, online_marks, marks and mettl schema
			saveResponseFromMettl(bean, resultBean, studentTestInfoList, sectiontInfoList, questionList, evaluatorInfo);
			
			bean.setMettlResultCandidateBean(null);
		
		} else {
			resultBean.getFailureResponse().add(bean);
		}
		
		if(null != studentTestInfoList) {
			studentTestInfoList.clear();
		}
		if(null != sectiontInfoList) {
			sectiontInfoList.clear();
		}
		if(null != questionList) {
			questionList.clear();
		}
		if(null != evaluatorInfo) {
			evaluatorInfo.clear();
		}
		
	}

	//saves records in mettl_marks, transfers to online_marks and marks and upserts in mettl_schema 
	private void saveResponseFromMettl(MettlPGResponseBean bean, MettlFetchTestResultBean resultBean,
			List<MettlStudentTestInfo> studentTestInfoList, List<MettlStudentSectionInfo> sectiontInfoList,
			List<MettlSectionQuestionResponse> questionList, List<MettlEvaluatorInfo> evaluatorInfo) {

		int mettlMarksUpserted = 0;
		int marksTransferred = 0;
		
		try {
			
			//upserts in mettl marks 
			mettlMarksUpserted = upsetMettlMarksStatus(bean);
			
			// if attempted and inserted in mettl_marks, transfer to online marks and update
			// success count accordingly to show on front end
			if (mettlMarksUpserted > 0 && METTL_ATTEMPTED.equalsIgnoreCase(bean.getStatus())) {
				
				//transfers to online_marks and marks
				marksTransferred = transferToMarks(bean, resultBean);
				
				//update records in mettl schema
				upsertMetrics(studentTestInfoList, sectiontInfoList, questionList, evaluatorInfo);
				
				resultBean.setSuccesscount(resultBean.getSuccesscount() + marksTransferred);
				resultBean.setTransferredCount(resultBean.getTransferredCount() + marksTransferred);
				resultBean.setBodAppliedCount(resultBean.getBodAppliedCount() + (bean.isBodApplied() ? 1 : 0));

			// if not attempted but inserted to mettl_marks as not attempted, show success
			// on front end as the record was pulled / considered	
			} else if (mettlMarksUpserted > 0 && METTL_NOT_ATTEMPTED.equalsIgnoreCase(bean.getStatus())) {
				resultBean.setSuccesscount(resultBean.getSuccesscount() + 1);
			}

		// in case of exception adds exception in error field and adds mettl response to
		// failure list to show on front end excel	
		} catch (Exception e) {
			bean.setError(e.getMessage());
			resultBean.getFailureResponse().add(bean);
			
			pullMettlMarks.error("{} {} saveResponseFromMettl Error saving records : {}",
					bean.getSapid(),
					bean.getSubject(),
					Throwables.getStackTraceAsString(e));
			
		} finally {
			
			//logs final status on records being upserted, transferred and bod applied
			pullMettlMarks.info("{} {} saveResponseFromMettl upserted mettl marks : {} || Bod applied : {} || transfer step complete : {} || status : {}",
					bean.getSapid(),
					bean.getSubject(),
					mettlMarksUpserted,
					bean.isBodApplied(),
					marksTransferred,
					bean.getStatus());
		}
	}
	
	protected MettlHelper getMettlHelperFromProgramType(String programType) {
		if("MBA - WX".equals(programType)) {
			return (MettlHelper) act.getBean("mbaWxMettlHelper");
		} else if("M.Sc. (AI & ML Ops)".equals(programType) || "M.Sc. (AI)".equals(programType) ) {
			return (MettlHelper) act.getBean("mscMettlHelper");
		} else if("Modular PD-DM".equals(programType)) {
			return (MettlHelper) act.getBean("pddmMettlHelper");
		} else {
			// fallback
			return (MettlHelper) act.getBean("mbaWxMettlHelper");
		}
	}
	
	public ArrayList<Integer> getConsumerProgramStrucutreId(String programType) throws Exception{
		ArrayList<Integer> consumerProgramStructureId = new ArrayList<Integer>();
		switch (programType) {
		case "MBA - WX":
			consumerProgramStructureId.add(111);
			consumerProgramStructureId.add(151);
			consumerProgramStructureId.add(160); 
			return consumerProgramStructureId;
		case "M.Sc. (AI & ML Ops)":
			consumerProgramStructureId.add(131);
			return consumerProgramStructureId;
		case "M.Sc. (AI)":	
			consumerProgramStructureId.add(158);
			return consumerProgramStructureId;
		case "Modular PD-DM":
			consumerProgramStructureId.add(148);
			consumerProgramStructureId.add(144);
			consumerProgramStructureId.add(149);
			consumerProgramStructureId.add(142);
			consumerProgramStructureId.add(143);
			consumerProgramStructureId.add(147);
			consumerProgramStructureId.add(145);
			consumerProgramStructureId.add(146);
			return consumerProgramStructureId;
		default : 
			return null;
		}
	}
	

	public void checkAttemptStatus(String todayDate)
	{
		try
		{
				pullTimeBoundMettlMarksLogger.info("Running checkTimeBoundStudentsAttemptStatus for Date: "+todayDate);
				String programArr [] = TIMEBOUND_ID_TEE;//Add program types in this list 
				for(int i=0;i<=programArr.length-1;i++)
				{
					MettlHelper mettlHelper = getMettlHelperFromProgramType(programArr[i]);
					ArrayList<Integer> consumerProgramStructureIdList = getConsumerProgramStrucutreId(programArr[i]);
					ArrayList<MettlRegisterCandidateBean> candidateList = examsAssessmentsDAO.fetchTodayExamStudents(todayDate,consumerProgramStructureIdList);
					pullTimeBoundMettlMarksLogger.info("Total Records Found for program Type:"+programArr[i]+" is:"+candidateList.size());
					ArrayList<String> errorList = new ArrayList<String>();
					int attemptedCount=0;
					int notAttemptedCount=0;
					
					if(candidateList!=null && candidateList.size()>0)
					{
						ArrayList<MettlResponseBean> mettlResponseBeanList = new ArrayList<MettlResponseBean>();
						for(int j=0;j<=candidateList.size()-1;j++)
						{
							MettlRegisterCandidateBean candidate = candidateList.get(j);
							pullTimeBoundMettlMarksLogger.info("Get single student test status for access key:"+candidate.getScheduleAccessKey()+" and student email:"+candidate.getEmailAddress());
							JsonObject jsonResponse = mettlHelper.getSingleStudentTestStatusForASchedule(candidate.getScheduleAccessKey(),candidate.getEmailAddress());
							
							if(jsonResponse != null) {
								
								String status = jsonResponse.get("status").getAsString();
								MettlResponseBean tmp_responseBean = new MettlResponseBean();
								pullTimeBoundMettlMarksLogger.info("Mettl pull status:"+status+" for student email:"+candidate.getEmailAddress());
								if("SUCCESS".equalsIgnoreCase(status)) {
									
									JsonObject candidateObject = jsonResponse.get("candidate").getAsJsonObject();
									tmp_responseBean.setEmail(candidate.getEmailAddress());
									tmp_responseBean.setSchedule_accessKey(candidate.getScheduleAccessKey());
									JsonObject testObject = candidateObject.get("testStatus").getAsJsonObject();
								
									if("AccessRevoked".equalsIgnoreCase(testObject.get("status").getAsString())) {
										pullTimeBoundMettlMarksLogger.info("Access Revoked for student email:"+candidate.getEmailAddress());
										errorList.add("Student Email: "+candidate.getEmailAddress()+" - Schedule Access Key: "+candidate.getScheduleAccessKey());
									} else {
										pullTimeBoundMettlMarksLogger.info("Test status:"+testObject.get("status").getAsString()+" for student email:"+candidate.getEmailAddress());
										if("Completed".equalsIgnoreCase(testObject.get("status").getAsString())) {
											tmp_responseBean.setStatus("Attempted");
											attemptedCount++;
										}else {
											tmp_responseBean.setStatus("Not Attempted");
											notAttemptedCount++;
										}	
										tmp_responseBean.setSapid(candidate.getSapId());
										tmp_responseBean.setLastModifiedBy("checkStatus Scheduler");
										
										pullTimeBoundMettlMarksLogger.info("Mettl Result Bean Details:"+tmp_responseBean);
										mettlResponseBeanList.add(tmp_responseBean);
									}
								}else if("ERROR".equalsIgnoreCase(status)){
									pullTimeBoundMettlMarksLogger.info("Error status response for student email:"+candidate.getEmailAddress());
									errorList.add("Student Email: "+candidate.getEmailAddress()+" - Schedule Access Key: "+candidate.getScheduleAccessKey());
								}

							}else {
								pullTimeBoundMettlMarksLogger.info("Null json response for student email:"+candidate.getEmailAddress());
								errorList.add("Student Email: "+candidate.getEmailAddress()+" - Schedule Access Key: "+candidate.getScheduleAccessKey());
							}
						}
						
						if(!mettlResponseBeanList.isEmpty())
						{
							pullTimeBoundMettlMarksLogger.info("Updating in db for total:"+mettlResponseBeanList.size()+" records");
							ArrayList<String> updateErrorList = examsAssessmentsDAO.updateAttemptStatusForSchedule(mettlResponseBeanList);
							if(updateErrorList!=null && updateErrorList.size()>0)
							{
								errorList.addAll(updateErrorList);
							}
						}
						
					}
					else
					{
						pullTimeBoundMettlMarksLogger.info("No record found for program Type:"+programArr[i]+" for Today's date:"+todayDate);
					}
					
					emailAttemptStatusForScheduleReport(attemptedCount,notAttemptedCount,errorList,programArr[i],candidateList.size(),todayDate);
				}
		}
		catch(Exception e)
		{
			pullTimeBoundMettlMarksLogger.error("Error is:"+e.getMessage());
		}
	}
	
	private void emailAttemptStatusForScheduleReport(int attemptedCount,int notAttemptedCount,ArrayList<String> errorList,String program,int candidateListSize,String todayDate) {

		StringBuffer strBuf = null;
		String emailMessage = null;
		try 
		{
			strBuf = new StringBuffer();
			strBuf.append("Dear Team, <br>");
			strBuf.append("Candidates Attempt Status for Date: "+todayDate+"<br><br>");

			strBuf.append("<style type=text/css>");
			strBuf.append(".tg  {border-collapse:collapse;border-spacing:0;}");
			strBuf.append(
					".tg td{border-color:black;border-style:solid;border-width:1px;font-family:Arial, sans-serif;font-size:14px;");
			strBuf.append("overflow:hidden;padding:10px 5px;word-break:normal;}");
			strBuf.append(
					".tg th{border-color:black;border-style:solid;border-width:1px;font-family:Arial, sans-serif;font-size:14px;");
			strBuf.append("font-weight:normal;overflow:hidden;padding:10px 5px;word-break:normal;}");
			strBuf.append(
					".tg .tg-af47{background-color:#ffffff;border-color:inherit;color:#000000;text-align:center;vertical-align:top}");
			strBuf.append(
					".tg .tg-vvj0{background-color:#ecf4ff;border-color:inherit;color:#000000;font-weight:bold;text-align:center;vertical-align:top}");
			strBuf.append("</style>");
			
			strBuf.append("Program: "+program+"<br>");
			strBuf.append("Total Attempted Count: "+attemptedCount+"<br>");
			strBuf.append("Total Not Attempted Count: "+notAttemptedCount+"<br>");
			strBuf.append("Total Records For Today's Exam: "+candidateListSize+"<br>");
			

				strBuf.append("<table class=tg>");
				strBuf.append("<thead>");
				strBuf.append("<tr>");
				strBuf.append("<th class=tg-vvj0>Error List</th>");
				strBuf.append("</tr>");
				strBuf.append("</thead>");
				strBuf.append("<tbody>");
				if (errorList.isEmpty()) {
					strBuf.append("<tr>");
					strBuf.append("<td class=tg-af47 colspan=8>No Error(s)</td>");
					strBuf.append("</tr>");
				} else {
					for (String errorMessage : errorList) {
						strBuf.append("<tr>");
						strBuf.append("<td class=tg-af47>").append(errorMessage).append("</td>");
						strBuf.append("</tr>");
					}
				}
				strBuf.append("</tbody>");
				strBuf.append("</table>");

			strBuf.append("<br>Thanks & Regards,<br>");
			strBuf.append("NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION");
			strBuf.append("<br><br>");
			emailMessage = strBuf.toString();
			
			pullTimeBoundMettlMarksLogger.info(emailMessage);
			
			mailer.emailAttemptStatusForSchedule(emailMessage,todayDate);
		} catch (Exception e) {
			pullTimeBoundMettlMarksLogger.error("Error is:"+e.getMessage());
		} finally {
			emailMessage = null;
			emptyStringBuffer(strBuf);
		}
	}
	
	private void emptyStringBuffer(StringBuffer strBuf) {
		if (null != strBuf) {
			strBuf.delete(0, strBuf.length() - 1);
		}
	}

	public int insertBodQuestionIds(String examYear, String examMonth, String createdBy, List<String> questionIdsWithoutDuplicate) {
		return resultProcessingDAO.insertBodQuestionIds(examYear, examMonth, createdBy, questionIdsWithoutDuplicate);
	}

	public Set<String> getBodQuestionIds(String examYear, String examMonth) {
		return resultProcessingDAO.getBodQuestionIds(examYear, examMonth);
	}
	
	public Set<String> getImmutableBodQuestionIds(String examYear, String examMonth) {
		return Collections.unmodifiableSet(getBodQuestionIds(examYear, examMonth));
	}
	
}
