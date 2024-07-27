package com.nmims.services;

import java.io.FileNotFoundException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.nmims.beans.AuditTrailExamBean;
import com.nmims.beans.ErrorAnalyticsBean;
import com.nmims.beans.LogFileAnalysisBean;
import com.nmims.beans.AccumulateAuditTrailsBean;
import com.nmims.beans.LostFocusLogExamBean;
import com.nmims.beans.NetworkLogsExamBean;
import com.nmims.beans.PageVisitExamBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentQuestionResponseExamBean;
import com.nmims.beans.StudentsTestDetailsExamBean;
import com.nmims.beans.TestAuditTrailsApiResponseExamBean;
import com.nmims.beans.TestExamBean;
import com.nmims.beans.TestLogResponseExamBean;
import com.nmims.daos.AuditTrailsDAO;
import com.nmims.daos.TestDAO;
import com.nmims.helpers.AESencrp;
import com.nmims.helpers.ExamLogFileHelper;

@Service("auditTrailsService")
public class AuditTrailsService {

    @Autowired
    TestDAO tDao; 
    
    @Autowired
    AuditTrailsDAO auditDao;

	@Value("${SERVER_PATH}")
	private String SERVER_PATH;
	
	public LostFocusLogExamBean getStudentDetailsForUnfairMeans( LostFocusLogExamBean bean) {

		LostFocusLogExamBean studentDetails = new LostFocusLogExamBean();
		
		studentDetails = auditDao.getStudentDetailsForUnfairMeans(bean);
		
		return studentDetails;
	}
	
	public ArrayList<LostFocusLogExamBean> getTestForLostFocus() throws Exception{

		ArrayList<LostFocusLogExamBean> testList = new ArrayList<>();
		
		testList = auditDao.getTestForLostFocus();
		
		return testList;
		
	}

	public ArrayList<LostFocusLogExamBean> getSubjectList()  throws Exception{

		ArrayList<LostFocusLogExamBean> subjectList = new ArrayList<>();
		
		subjectList = tDao.getSubjectList();
		
		return subjectList;
		
	}
	
	public LostFocusLogExamBean getReasonForLostFocus( LostFocusLogExamBean bean ) throws Exception{

		LostFocusLogExamBean reasonForLostFocus = new LostFocusLogExamBean();
		
		reasonForLostFocus = auditDao.getReasonForLostFocus(bean);
				
		return reasonForLostFocus;
		
	}
	
	public TestExamBean getTestById ( Long testId ) throws Exception{
		
		TestExamBean test = new TestExamBean();
		
		test = tDao.getTestById(testId);
		
		return test;
	}

	public ArrayList<LostFocusLogExamBean> getRecentTest() throws Exception{

		ArrayList<LostFocusLogExamBean> recentTest = new ArrayList<>();
		
		recentTest = auditDao.getRecentTest();
		
		return recentTest;
	}

	public ArrayList<LostFocusLogExamBean> getTestForSubjectAndDuration(LostFocusLogExamBean bean) throws Exception{

		ArrayList<LostFocusLogExamBean> recentTest = new ArrayList<>();
		
		recentTest = auditDao.getTestForSubjectAndDuration( bean );
		
		return recentTest;
	}

	public ArrayList<AuditTrailExamBean> getTestForIABatchJob( ) throws Exception{

		ArrayList<AuditTrailExamBean> recentTest = new ArrayList<>();
		
		recentTest = auditDao.getTestForIABatchJob();
		
		return recentTest;
	}

	public ArrayList<AuditTrailExamBean> getDateForErrorAnalytics( ) throws Exception{

		ArrayList<AuditTrailExamBean> dateList = new ArrayList<>();
		
		dateList = auditDao.getDateForErrorAnalytics();
		
		return dateList;
	}

	public List<ErrorAnalyticsBean> getErrorAnalyticsBySapidCreatedDate( String date ) throws Exception{

		List<ErrorAnalyticsBean> recentTest = new ArrayList<>();
		
		recentTest = auditDao.getErrorAnalyticsBySapidCreatedDate( date );
		
		return recentTest;
	}

	public void updateLostFocusCheckByTestId( Long testId ) throws Exception{

		auditDao.updateLostFocusCheckByTestId( testId );
		
	}

	public void markCopyCaseForLostFocus( LostFocusLogExamBean bean ) throws Exception{

		auditDao.markCopyCaseForLostFocus( bean );

	}

	public void unmarkCopyCaseForLostFocus(LostFocusLogExamBean bean) throws Exception{

		auditDao.unmarkCopyCaseForLostFocus( bean );

	}

	public ArrayList<String> getStudentLogFileDetails( LogFileAnalysisBean bean ) {

		ArrayList<String> logDetails = new ArrayList<>();
		ExamLogFileHelper helper = new ExamLogFileHelper();
		
		ArrayList<LogFileAnalysisBean> questionIdList = tDao.getQuestionId( bean.getTestId() );
		
		try {
			logDetails = helper.getExamLogsFromLogFile(bean, questionIdList);
		} catch (FileNotFoundException e) {
			
		}

		return logDetails;
	}
	
	public TestExamBean updateStartDateTime(TestExamBean bean) throws Exception{
	
		bean.setType("SUPPORT_FOR_EXTENDINGTIME");
		
		auditDao.updateDate(bean);
		auditDao.updateRefreshCount(bean);
		auditDao.saveSupportForOtherIssues(bean);
		
		return bean;

	}

	public TestExamBean updateRefreshCount(TestExamBean bean) throws Exception{
	
		bean.setType("SUPPORT_FOR_REFRESHCOUNT");
		
		auditDao.updateRefreshCount(bean);
		auditDao.saveSupportForOtherIssues(bean);
		
		return bean;

	}

	public TestExamBean updateOtherIssues(TestExamBean bean) throws Exception{
	
		bean.setType("SUPPORT_FOR_OTHERISSUE");
		
		auditDao.saveSupportForOtherIssues(bean);
		
		return bean;

	}

	public TestExamBean updateTestTime(TestExamBean bean) throws Exception{

		bean.setType("SUPPORT_FOR_EXTENDINGTESTWINDOW");
		
		boolean status= auditDao.insertExtendedTestTime(bean);
		
		if( status ) {

			try {
				auditDao.saveSupportForOtherIssues(bean);
			} catch (Exception e) {
				
			}
			bean.setErrorRecord(false);
			
		}else {
			
			bean.setErrorRecord(true);
			bean.setErrorMessage("An error occured while updating the extended time for the student.");
			
		}
		
		return bean;

	}
	
	public List<StudentExamBean> getAllStudentsForAuditTrails() {

		List<Integer> consumerProgramStructureIdList = new ArrayList<>();
		List<StudentExamBean> studentsList = new ArrayList<StudentExamBean>();
		
		consumerProgramStructureIdList.add(111);	//mbawx_studentList_Jul19
		consumerProgramStructureIdList.add(151);	//mbawx_studentList_Oct20
		consumerProgramStructureIdList.add(131);	//msc_studentList
		consumerProgramStructureIdList.add(160);	//mbawx_studentList_newCycle
		consumerProgramStructureIdList.add(158);	//mscai_student_list
		
		consumerProgramStructureIdList.add(142);	//dhrm_student_list
		consumerProgramStructureIdList.add(143);	//dmm_student_list
		consumerProgramStructureIdList.add(144);	//drm_student_list
		consumerProgramStructureIdList.add(145);	//ditm_student_list
		consumerProgramStructureIdList.add(146);	//dscm_student_list
		consumerProgramStructureIdList.add(147);	//dom_student_list
		consumerProgramStructureIdList.add(148);	//pddm_student_list
		consumerProgramStructureIdList.add(149);	//pcdm_student_list

		consumerProgramStructureIdList.add(128);	//bba_student_list
		consumerProgramStructureIdList.add(92);		//cbm_student_list
		consumerProgramStructureIdList.add(112);	//pdwm_student_list
		consumerProgramStructureIdList.add(130);	//pgdbm_bfm_student_list
		consumerProgramStructureIdList.add(132);	//mba_bm_student_list
		consumerProgramStructureIdList.add(139);	//mba_scm_student_list
		consumerProgramStructureIdList.add(154);	//pcds_student_list
		consumerProgramStructureIdList.add(155);	//pdds_student_list
		
		try {
			studentsList = auditDao.getAllStudentsByMasterKey(consumerProgramStructureIdList);
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
		
		return studentsList;
	}
	
	public List<TestExamBean> getTestsBySapid( String sapid ) {

		List<TestExamBean> tests = tDao.getTestsBySapid( sapid );
		
		return tests;
	}
	
	public TestAuditTrailsApiResponseExamBean getAnalyticsDetails(AuditTrailExamBean bean) {

		TestAuditTrailsApiResponseExamBean testBean = new TestAuditTrailsApiResponseExamBean();
		List<NetworkLogsExamBean> networkLogs = new ArrayList<NetworkLogsExamBean>();
		List<LostFocusLogExamBean> lostFocusLog = new ArrayList<LostFocusLogExamBean>();
		List<PageVisitExamBean> pageVisits = new ArrayList<PageVisitExamBean>();
		RestTemplate restTemplate = new RestTemplate();
		
		try {
			
			String url = "https://ngasce-content.nmims.edu/ltidemo/api/getTestAuditTrails";
			//String url = "http://localhost:8080/ltidemo/api/getTestAuditTrails";
			
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			HttpEntity<AuditTrailExamBean> entity = new HttpEntity<AuditTrailExamBean>(bean, headers);
			ResponseEntity<String> response = restTemplate.exchange( url, HttpMethod.POST, entity, String.class);
			JsonObject jsonObject = new JsonParser().parse(response.getBody()).getAsJsonObject();
			Gson gson = new Gson();

			try {
				
				JsonArray pageVisit = jsonObject.get("pageVisits").getAsJsonArray();
				for(int i = 0; i<pageVisit.size(); i++) {
					
					PageVisitExamBean pageVisitBean = new PageVisitExamBean();
					JsonObject pages = pageVisit.get(i).getAsJsonObject();
					pageVisitBean.setPath(pages.get("path").getAsString());
					pageVisitBean.setVisiteddate(pages.get("visiteddate").getAsString());
					pageVisitBean.setTimespent(pages.get("timespent").getAsString());
					pageVisitBean.setDeviceName(pages.get("deviceName").getAsString());
					pageVisitBean.setDeviceOS(pages.get("deviceOS").getAsString());
					pageVisitBean.setDeviceSystemVersion(pages.get("deviceSystemVersion").getAsString());
					pageVisitBean.setIpAddress(pages.get("ipAddress").getAsString());
					pageVisitBean.setApplicationType(pages.get("applicationType").getAsString());
					pageVisitBean.setDescription(pages.get("description").getAsString());
					pageVisits.add(pageVisitBean);
					
				}
				
			} catch (Exception e) {
				
			}
			
			try {
				
				JsonArray lostFocus = jsonObject.get("lostFocusLog").getAsJsonArray();
				Date parsedDate = new Date();
				Calendar createdDate = Calendar.getInstance();
				Calendar lastModifiedDate = Calendar.getInstance();
				
				for(int i = 0; i<lostFocus.size(); i++) {
					
					LostFocusLogExamBean lostFocusLogBean = new LostFocusLogExamBean();
					JsonObject lostfocus = lostFocus.get(i).getAsJsonObject();
					lostFocusLogBean.setSapid(lostfocus.get("sapid").getAsString());
					lostFocusLogBean.setTestId(Long.toString(bean.getTestId()));
					lostFocusLogBean.setIpAddress(lostfocus.get("ipAddress").getAsString());
					createdDate.setTimeInMillis(lostfocus.get("createdDate").getAsLong());
					parsedDate = createdDate.getTime();
					lostFocusLogBean.setCreatedDate(new Timestamp(parsedDate.getTime()));
					lastModifiedDate.setTimeInMillis(lostfocus.get("lastModifiedDate").getAsLong());
					parsedDate = lastModifiedDate.getTime();
					lostFocusLogBean.setLastModifiedDate(new Timestamp(parsedDate.getTime()));
					lostFocusLogBean.setTimeAwayInMins(lostfocus.get("timeAwayInMins").getAsBigDecimal());
					lostFocusLogBean.setTimeAwayInSecs(lostfocus.get("timeAwayInSecs").getAsBigInteger());
					lostFocusLog.add(lostFocusLogBean);
					
				}
				
			} catch (Exception e) {
				
			}
			
			try {
				networkLogs = (List<NetworkLogsExamBean>) gson.fromJson(jsonObject.get("networkLogs"), 
						new TypeToken<List<NetworkLogsExamBean>>() {}.getType());
			}catch (Exception e) {
				
			}
			
			testBean.setNetworkLogs(networkLogs);
			testBean.setLostFocusLog(lostFocusLog);
			testBean.setPageVisits(pageVisits);
			
			return testBean;

		} catch (RestClientException e) {
			
			testBean.setErrorMessage("Error IN rest call got "+e.getMessage());
			return testBean;
		}

	}

	public String getTestJoinLink(String sapid, String testId) {
		
		String encryptedSapid = ""; 
		String encryptedTestId = "";
		String testJoinURL = "";
		
		try {
			encryptedSapid = encryptWithOutSpecialCharacters( sapid );
			encryptedTestId = encryptWithOutSpecialCharacters( testId );
		} catch (Exception e) {
			
		}
		
		testJoinURL = SERVER_PATH+"exam/assignmentGuidelinesForAllViews?testIdForUrl="+encryptedTestId+"&sapidForUrl="+encryptedSapid;
		
		return testJoinURL;
		
	}
	
	public StudentsTestDetailsExamBean getStudentsTestDetailsBySapidAndTestId( String sapid, Long testId ) {
		
		StudentsTestDetailsExamBean studentTestDetails = new StudentsTestDetailsExamBean();
		
		studentTestDetails = auditDao.getStudentsTestDetailsBySapidAndTestId(sapid,testId);
		
		return studentTestDetails;
	}
	
	public AccumulateAuditTrailsBean getLogDetailsForAttemptedStudents( AuditTrailExamBean bean ) {

		Long before = System.currentTimeMillis();
		
		StudentsTestDetailsExamBean studentTestDetails = new StudentsTestDetailsExamBean();
		AccumulateAuditTrailsBean response = new AccumulateAuditTrailsBean();
		AccumulateAuditTrailsBean data = new AccumulateAuditTrailsBean();
		
		List<StudentQuestionResponseExamBean> answers = new ArrayList<StudentQuestionResponseExamBean>();
		List<StudentQuestionResponseExamBean> answersAfterFetchingFromDbAndRedis = new ArrayList<StudentQuestionResponseExamBean>();
		List<ErrorAnalyticsBean> errorAnalytics = new ArrayList<ErrorAnalyticsBean>();
		
		studentTestDetails = tDao.getStudentsTestDetailsBySapidAndTestId(bean.getSapid(),bean.getTestId());
		data.setStudentTestDetails(studentTestDetails);
		
		answers = tDao.getAttemptAnswers(bean.getSapid(), bean.getTestId());
		answersAfterFetchingFromDbAndRedis = tDao.getAccumulatedAnswerFromDbAndRedis( studentTestDetails, answers );
		data.setAnswers(answersAfterFetchingFromDbAndRedis);
		
		errorAnalytics = auditDao.getErrorAnalyticsBySapidCreatedDate(bean.getSapid(), studentTestDetails.getTestStartedOn());
		data.setErrorAnalytics(errorAnalytics);

		TestAuditTrailsApiResponseExamBean testBean = (TestAuditTrailsApiResponseExamBean)getAnalyticsDetails(bean);
		
		Long after = System.currentTimeMillis();
		long uptime = after-before;
		long minutes = TimeUnit.MILLISECONDS.toMinutes(uptime);
		uptime -= TimeUnit.MINUTES.toMillis(minutes);
		long seconds = TimeUnit.MILLISECONDS.toSeconds(uptime);
		
		
		data.setNetworkLogs(testBean.getNetworkLogs());
		data.setPageVisits(testBean.getPageVisits());
		data.setLostFocusLog(testBean.getLostFocusLog());
		
		response = getAccumulateTestLogs( data );
		response.setAnswers(answersAfterFetchingFromDbAndRedis);
		
		after = System.currentTimeMillis();
		uptime = after-before;
		minutes = TimeUnit.MILLISECONDS.toMinutes(uptime);
		uptime -= TimeUnit.MINUTES.toMillis(minutes);
		seconds = TimeUnit.MILLISECONDS.toSeconds(uptime);
		

		return response;
	}
	
	public AccumulateAuditTrailsBean getLogDetailsForNotAttemptedStudents( AuditTrailExamBean bean ) {
		
		AccumulateAuditTrailsBean response = new AccumulateAuditTrailsBean();
		AccumulateAuditTrailsBean data = new AccumulateAuditTrailsBean();
		TestExamBean test = new TestExamBean();
		List<ErrorAnalyticsBean> errorAnalytics = new ArrayList<ErrorAnalyticsBean>();
		
		test = tDao.getTestById(bean.getTestId());

		errorAnalytics = auditDao.getErrorAnalyticsBySapidCreatedDate(bean.getSapid(), test.getStartDate());
		data.setErrorAnalytics(errorAnalytics);
		
		Long before = System.currentTimeMillis();
		
		TestAuditTrailsApiResponseExamBean testBean = (TestAuditTrailsApiResponseExamBean)getAnalyticsDetails(bean);
		data.setNetworkLogs(testBean.getNetworkLogs());
		data.setPageVisits(testBean.getPageVisits());
		data.setLostFocusLog(testBean.getLostFocusLog());
		
		response = getAccumulateTestLogs( data );
		response.setTest(test);
		
		Long after = System.currentTimeMillis();
		long uptime = after-before;
		long minutes = TimeUnit.MILLISECONDS.toMinutes(uptime);
		uptime -= TimeUnit.MINUTES.toMillis(minutes);
		long seconds = TimeUnit.MILLISECONDS.toSeconds(uptime);
		
		return response;
		
	}
	
	private AccumulateAuditTrailsBean getAccumulateTestLogs(AccumulateAuditTrailsBean data) {
		
		try {
			List<TestLogResponseExamBean> testLog = new ArrayList<TestLogResponseExamBean>();
			
			if(data.getAnswers() != null && data.getAnswers().size() != 0) {
				
				for(StudentQuestionResponseExamBean answerBean : data.getAnswers()) {
					
					TestLogResponseExamBean logBean = new TestLogResponseExamBean();
					logBean = getFormatedAnswerBean( answerBean );
					testLog.add(logBean);
					
				}
				
			}
		
			if(data.getErrorAnalytics() != null && data.getErrorAnalytics().size() != 0) {
				
				for(ErrorAnalyticsBean errorBean : data.getErrorAnalytics()) {
	
					TestLogResponseExamBean logBean = new TestLogResponseExamBean();
					logBean = getFormatedErrorLogs( errorBean );
					testLog.add(logBean);
					
				}
			}

			if(data.getNetworkLogs() != null && data.getNetworkLogs().size() != 0) {
				
				for(NetworkLogsExamBean networkLogBean : data.getNetworkLogs()){
						
					TestLogResponseExamBean logBean = new TestLogResponseExamBean();
					logBean = getFormatedNetworkLogBean( networkLogBean );
					testLog.add(logBean);
						
				}
			}

			if(data.getPageVisits() != null && data.getPageVisits().size() != 0) {
				
				for(PageVisitExamBean pageBean : data.getPageVisits()){
					
					TestLogResponseExamBean logBean = new TestLogResponseExamBean();
					logBean = getFormatedPageVisitBean( pageBean );
					testLog.add(logBean);
						
				}
			}

			if(data.getLostFocusLog() != null && data.getLostFocusLog().size() != 0) {
				
				for(LostFocusLogExamBean lostFocusBean : data.getLostFocusLog()){
						
					TestLogResponseExamBean logBean = new TestLogResponseExamBean();
					logBean = getFormatedLostfocusBean( lostFocusBean );
					testLog.add(logBean);
					
				}
			}
			
			if(data.getStudentTestDetails() != null) {
				
				TestLogResponseExamBean startDetails = new TestLogResponseExamBean();
				TestLogResponseExamBean endDetails = new TestLogResponseExamBean();
				
				startDetails = getFormatedTestStudentDetials( data, "start" );
				endDetails = getFormatedTestStudentDetials( data, "end" );
		
				testLog.add(startDetails);
				testLog.add(endDetails);
				
					
			}
			
			data.setTestLogs(testLog);
		}catch (Exception e) {
			
		}
		
		return data;
		
	}
	
	private TestLogResponseExamBean getFormatedAnswerBean( StudentQuestionResponseExamBean answerBean ) throws Exception {

		TestLogResponseExamBean logBean = new TestLogResponseExamBean();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date dateCreated = new Date();
		Date dateModifies = new Date();
		long createdDateInMillis = 0;
		long lastModifiedDateInMillis = 0;
		long timespent = 0;
		String createdDate = "";
		String lastModifiedDate = "";
		
		try {
			
			createdDate = answerBean.getCreatedDate().toString();
			lastModifiedDate = answerBean.getLastModifiedDate().toString();
			
			try {
				dateCreated = sdf.parse(createdDate);
				dateModifies = sdf.parse(lastModifiedDate);
			}catch (Exception e) {
				dateCreated = formater.parse(createdDate);
				dateModifies = formater.parse(lastModifiedDate);
			}
			createdDateInMillis = dateCreated.getTime();
			lastModifiedDateInMillis = dateModifies.getTime();
			timespent = lastModifiedDateInMillis - createdDateInMillis;
			
		}catch (Exception e) {
			
			
			dateCreated = formater.parse(createdDate);
			createdDateInMillis = dateCreated.getTime();
			lastModifiedDateInMillis = Calendar.getInstance().getTimeInMillis();
			timespent = lastModifiedDateInMillis - createdDateInMillis;
			
		}
		
		String checked = "No";

		logBean.setType("Question - Answer");
		logBean.setPage("-");
		logBean.setVisitedDate(Long.toString(createdDateInMillis));
		logBean.setTimespent(Long.toString(timespent/1000));
		logBean.setStatus("QNA");
		logBean.setNetwork("-");
		if(answerBean.getType() == 4) {
			if(answerBean.getIsChecked() == 1)
				checked = "Yes";
			
			logBean.setDetails("<b>Question:</b> "+answerBean.getQuestion()+"<br> <b>Answer:</b> "+answerBean.getAnswer()+"<br> <b>Checked:</b> "+
			checked+"<br> <b>Faculty: </b>"+answerBean.getFacultyId()+" ( "+answerBean.getFirstName()+" "+answerBean.getLastName()+
			" )<br> <b>Marks:</b> "+answerBean.getMarks()+"<br> <b>Remark:</b> "+answerBean.getRemark());
		}else
			logBean.setDetails("<b>Question:</b> "+answerBean.getQuestion()+"<br> <b>Answer:</b> "+answerBean.getOptionData());
		logBean.setApiName("-");
		logBean.setErrorMessage("");
		logBean.setDescription("NA");
		
		return logBean;
		
	}
	
	private TestLogResponseExamBean getFormatedErrorLogs( ErrorAnalyticsBean errorBean ) throws Exception {
		
		TestLogResponseExamBean logBean = new TestLogResponseExamBean();
		String createdDate = errorBean.getCreatedOn();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long millis = 0;
		
		try {
			
			Date date = sdf.parse(createdDate);
			millis = date.getTime();
			
		}catch (Exception e) {

			try {
				
				Date date = format.parse(createdDate);
				millis = date.getTime();
				
			}catch (Exception exception) {
				// TODO: handle exception
			}
			
		}
		
		String stackTrace = errorBean.getStackTrace();
		
		logBean.setVisitedDate(Long.toString(millis));
		logBean.setType("Error Analytics");
		logBean.setPage(errorBean.getModule());
		logBean.setTimespent("-");
		logBean.setNetwork(errorBean.getIpAddress());
		logBean.setDetails("-");
		logBean.setStatus("Error");
		logBean.setApiName("-");
		logBean.setErrorMessage("<b>Error:</b> "+stackTrace);
		
  		Pattern readyStateZero = Pattern.compile("(?=.*\"readyState\":0)", Pattern.CASE_INSENSITIVE );
  		Pattern wasOfflineAt = Pattern.compile("(?=.*wasOfflineAt)", Pattern.CASE_INSENSITIVE );
  		Pattern timeOver = Pattern.compile("(?=.*TimeOver! Your Test Was Started at)", Pattern.CASE_INSENSITIVE );
  		
  		if( readyStateZero.matcher( stackTrace ).find() || wasOfflineAt.matcher( stackTrace ).find() )
  			logBean.setDescription("Network fluctuation at students end.");
  		else if ( timeOver.matcher( stackTrace ).find() )
  			logBean.setDescription("Time Over.");
  		else
			logBean.setDescription("NA");

		return logBean;
	}
	
	private TestLogResponseExamBean getFormatedNetworkLogBean( NetworkLogsExamBean networkLogBean ) throws Exception {
		
		TestLogResponseExamBean logBean = new TestLogResponseExamBean();
		
		logBean.setType("Network Log");
		logBean.setTimespent(Long.toString(Math.round(Double.parseDouble((networkLogBean.getDuration()))/1000)));
		logBean.setStatus(networkLogBean.getStatus());
		logBean.setVisitedDate(networkLogBean.getCreated_at().toString());
		logBean.setApiName("-");
		
		if(StringUtils.isBlank(networkLogBean.getNetworkInfo()))
			logBean.setNetwork("-");
		else
			logBean.setNetwork(networkLogBean.getNetworkInfo());
		
		logBean.setDetails(networkLogBean.getError_message());
		logBean.setPage(networkLogBean.getName());

		if( StringUtils.isBlank(networkLogBean.getDescription()) )
			logBean.setDescription("NA");
		else
			logBean.setDescription(networkLogBean.getDescription());

		return logBean;
		
	}
	
	private TestLogResponseExamBean getFormatedPageVisitBean( PageVisitExamBean pageBean ) throws Exception {
		
		TestLogResponseExamBean logBean = new TestLogResponseExamBean();
		
		logBean.setType("Page Visit - "+pageBean.getApplicationType());
		logBean.setPage(pageBean.getPath());
		logBean.setTimespent(Long.toString(Long.parseLong(pageBean.getTimespent())/1000));
		logBean.setVisitedDate(pageBean.getVisiteddate().toString());
		logBean.setNetwork(pageBean.getIpAddress());
		logBean.setDetails("<b>Device Name:</b> "+pageBean.getDeviceName()+"<br> <b>OS:</b> "+
				pageBean.getDeviceOS()+"<br> <b>System Version:</b> "+pageBean.getDeviceSystemVersion());
		logBean.setStatus("-");
		logBean.setApiName("-");
		logBean.setErrorMessage("");
		
		if( StringUtils.isBlank(pageBean.getDescription()) )
			logBean.setDescription("NA");
		else
			logBean.setDescription(pageBean.getDescription());

		return logBean;
	}
	
	private TestLogResponseExamBean getFormatedLostfocusBean( LostFocusLogExamBean lostFocusBean) throws Exception {

		TestLogResponseExamBean logBean = new TestLogResponseExamBean();
		
		logBean.setType("Lost Focus");
		logBean.setTimespent(lostFocusBean.getTimeAwayInSecs().toString());
		String createdDate = lostFocusBean.getCreatedDate().toString();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		long millis = 0;
		
		try {
			
			Date date = sdf.parse(createdDate);
			millis = date.getTime();
			
		}catch (Exception e) {

			try {

				Date date = format.parse(createdDate);
				millis = date.getTime();
				
			}catch (Exception exception) {
				// TODO: handle exception
			}
			
		}
		
		logBean.setVisitedDate(Long.toString(millis));
		logBean.setStatus("<b>Away from test</b>");
		logBean.setApiName("-");
		logBean.setNetwork("-");
		logBean.setErrorMessage("");
		logBean.setDetails("");
		logBean.setPage("-");
		logBean.setDescription("Lost focus from test page.");
		
		return logBean;
	}

	private TestLogResponseExamBean getFormatedTestStudentDetials( AccumulateAuditTrailsBean bean , String state) {

		TestLogResponseExamBean logBean = new TestLogResponseExamBean();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		String dateTime;

		if( "start".equals(state) ) {
			logBean.setStatus("Started the test.");
			logBean.setType("Test Log");
			dateTime = bean.getStudentTestDetails().getTestStartedOn().toString();
		}else {
			logBean.setStatus("Finished the test.");
			logBean.setType("Test Log");
			
			if( bean.getStudentTestDetails().getTestEndedOn() != null )
				dateTime = bean.getStudentTestDetails().getTestEndedOn().toString();
			else
				dateTime = "NA";
				
		}

		logBean.setTimespent("");
		logBean.setApiName("");
		logBean.setNetwork("-");
		logBean.setErrorMessage("");
		logBean.setDetails("");
		logBean.setPage("");
		logBean.setDescription("Student test details");
		
		try {
			
			Date date = sdf.parse(dateTime);
			long millis = date.getTime();
			logBean.setVisitedDate(Long.toString(millis));
			
		}catch (Exception e) {
			
			try {
				
				Date date = format.parse(dateTime);
				long millis = date.getTime();
				logBean.setVisitedDate(Long.toString(millis));
				
			}catch (Exception exception) {
				
				logBean.setVisitedDate("NA");
				
			}
			
		}
		
		return logBean;
		
	}
	
	public ArrayList<LostFocusLogExamBean> getLostFocusListWithStudentAndTestDetails( List<LostFocusLogExamBean> studentList ){

		LostFocusLogExamBean studentDetails = new LostFocusLogExamBean();
		LostFocusLogExamBean reasonForLostFocus = new LostFocusLogExamBean();
		ArrayList<LostFocusLogExamBean> studentListForUnfairMeans = new ArrayList<>();
		
		for(LostFocusLogExamBean bean : studentList) {
			try {
				studentDetails = getStudentDetailsForUnfairMeans(bean);
			} catch (Exception e) {
				
			}
			try {
				reasonForLostFocus = getReasonForLostFocus(bean);
			} catch (Exception e) {
				
			}
			bean.setEmailId(studentDetails.getEmailId());
			bean.setFirstName(studentDetails.getFirstName());
			bean.setLastName(studentDetails.getLastName());
			bean.setMobile(studentDetails.getMobile());
			bean.setReason(reasonForLostFocus.getReason());
			bean.setAttemptStatus( reasonForLostFocus.getAttemptStatus() );

			studentListForUnfairMeans.add(bean);
		}
		
		return studentListForUnfairMeans;
	}

	@SuppressWarnings("unchecked")
	public List<NetworkLogsExamBean> getNetworkLogsForSapid(  AuditTrailExamBean bean ) throws Exception{

		List<NetworkLogsExamBean> networkLogs = new ArrayList<NetworkLogsExamBean>();
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		Gson gson = new Gson();
		
		headers.add("Content-Type", "application/json");
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

		String url = "https://ngasce-content.nmims.edu/ltidemo/m/getNetworkLogsForBatchJob";
		//String url = "http://localhost:8080/ltidemo/m/getNetworkLogsForBatchJob";
		
		HttpEntity<AuditTrailExamBean> entity = new HttpEntity<AuditTrailExamBean>(bean, headers);
		ResponseEntity<String> response = restTemplate.exchange( url, HttpMethod.POST, entity, String.class);
		JsonObject jsonObject = new JsonParser().parse(response.getBody()).getAsJsonObject();
		
		networkLogs = (List<NetworkLogsExamBean>) gson.fromJson(jsonObject.get("networkLogs"), new TypeToken<List<NetworkLogsExamBean>>() {}.getType());
		
		return networkLogs;
		
	}
	
	public ArrayList<LostFocusLogExamBean> getIndividualStudentLostFocusLogs(  AuditTrailExamBean bean ) throws Exception{

		ArrayList<LostFocusLogExamBean> lostFocusLogs = new ArrayList<LostFocusLogExamBean>();
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		
		headers.add("Content-Type", "application/json");
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

		String url = "https://ngasce-content.nmims.edu/ltidemo/m/getIndividualStudentLostFocusLogs";
		//String url = "http://localhost:8080/ltidemo/m/getIndividualStudentLostFocusLogs";
		
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		HttpEntity<AuditTrailExamBean> entity = new HttpEntity<AuditTrailExamBean>(bean, headers);
		ResponseEntity<String> response = restTemplate.exchange( url, HttpMethod.POST, entity, String.class);
		JsonObject jsonObject = new JsonParser().parse(response.getBody()).getAsJsonObject();

		JsonArray lostFocus = jsonObject.get("lostFocusLog").getAsJsonArray();

		lostFocusLogs = getLostFocusLogBean( lostFocus, bean.getTestId() );
		
		return lostFocusLogs;
		
	}
	
	public ArrayList<LostFocusLogExamBean> getLostFocusDetailsForBatchJob( AuditTrailExamBean bean ) throws Exception{

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		ArrayList<LostFocusLogExamBean> lostFocusLogs = new ArrayList<LostFocusLogExamBean>();

		String url = "https://ngasce-content.nmims.edu/ltidemo/m/getLostFocusDetailsForBatchJob";
		//String url = "http://localhost:8080/ltidemo/m/getLostFocusDetailsForBatchJob";
		
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		HttpEntity<AuditTrailExamBean> entity = new HttpEntity<AuditTrailExamBean>(bean, headers);
		ResponseEntity<String> response = restTemplate.exchange( url, HttpMethod.POST, entity, String.class);
		JsonObject jsonObject = new JsonParser().parse(response.getBody()).getAsJsonObject();

		JsonArray lostFocus = jsonObject.get("lostFocusLog").getAsJsonArray();

		lostFocusLogs = getLostFocusLogBean( lostFocus, bean.getTestId() );
		
		return lostFocusLogs;
	}
	
	private ArrayList<LostFocusLogExamBean> getLostFocusLogBean(JsonArray lostFocus, Long testId) {

		ArrayList<LostFocusLogExamBean> lostFocusLogs = new ArrayList<LostFocusLogExamBean>();
		Calendar createdDate = Calendar.getInstance();
		Date parsedDate = new Date();
		
		for(int i = 0; i<lostFocus.size(); i++) {
			
			LostFocusLogExamBean lostFocusLogBean = new LostFocusLogExamBean();
			JsonObject lostfocus = lostFocus.get(i).getAsJsonObject();
			
			lostFocusLogBean.setSapid( lostfocus.get("sapid").getAsString() );
			lostFocusLogBean.setTestId( Long.toString( testId ) );
			lostFocusLogBean.setTimeAwayInMins( lostfocus.get("timeAwayInMins").getAsBigDecimal() );
			lostFocusLogBean.setTimeAwayInSecs( lostfocus.get("timeAwayInSecs").getAsBigInteger() );
			
			createdDate.setTimeInMillis(lostfocus.get("createdDate").getAsLong());
			parsedDate = createdDate.getTime();
			
			lostFocusLogBean.setCreatedDate(new Timestamp(parsedDate.getTime()));
			
			lostFocusLogs.add(lostFocusLogBean);
			
		}
		
		return lostFocusLogs;
		
	}
	
	public String encryptWithOutSpecialCharacters(String stringToBeEncrypted) throws Exception{

		return AESencrp.encrypt(stringToBeEncrypted).replaceAll("\\+", "_plus_");
	}
	public String decryptWithOutSpecialCharacters(String stringToBeDecrypted) throws Exception{
		
		return AESencrp.decrypt(stringToBeDecrypted.replaceAll("_plus_", "\\+"));
	}
	
}
