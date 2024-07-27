package com.nmims.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.stream.Collectors;


import org.apache.commons.codec.binary.Base64;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;
import com.ibm.icu.text.SimpleDateFormat;
import com.nmims.beans.DemoExamAttendanceBean;
import com.nmims.beans.Demoexam_keysBean;
import com.nmims.beans.ExamOrderExamBean;
import com.nmims.beans.MbaWxDemoExamAttendanceBean;
import com.nmims.beans.MbaWxDemoExamKeysBean;
import com.nmims.beans.MbaWxDemoExamScheduleDetailBean;
import com.nmims.beans.MettlScheduleExamBean;
import com.nmims.beans.ProgramSubjectMappingExamBean;
import com.nmims.beans.ResponseBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.daos.DemoExamDAO;
import com.nmims.daos.ExamBookingDAO;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.helpers.AESencrp;
import com.nmims.helpers.MettlHelper;
import com.nmims.beans.Person;


@Service("demoExamServices")
public class DemoExamServices extends MettlTeeMarksService{
	@Autowired
	ApplicationContext act;
	
	@Autowired
	DemoExamDAO demoExamDao;	
	
	@Value("#{'${PDDM_PROGRAMS_LIST}'.split(',')}")
	private List<String> programNameList;
	
	@Value("${CURRENT_PDDM_ACAD_YEAR}")
	private String CURRENT_PDDM_ACAD_YEAR;
	
	@Value("${CURRENT_PDDM_ACAD_MONTH}")
	private String CURRENT_PDDM_ACAD_MONTH;
	
	@Value("${CURRENT_MBAWX_ACAD_YEAR}")
	private String CURRENT_MBAWX_ACAD_YEAR;
	
	@Value("${CURRENT_MBAWX_ACAD_MONTH}")
	private String CURRENT_MBAWX_ACAD_MONTH;
	
	@Value("${START_METTL_DEMO_EXAM_MBAWX}")
	private String START_METTL_DEMO_EXAM_MBAWX;
	
	@Value("${END_METTL_DEMO_EXAM_MBAWX}")
	private String END_METTL_DEMO_EXAM_MBAWX;
	
	@Value("${DEMO_ASSESSMENT_ID_MBAWX}")
	private String DEMO_ASSESSMENT_ID_MBAWX;
	
	@Value("${DEMO_ASSESSMENT_ID_MSC}")
	private String DEMO_ASSESSMENT_ID_MSC;
	
	@Value("${DEMO_ASSESSMENT_ID_PDDM}")
	private String DEMO_ASSESSMENT_ID_PDDM;
	
	@Value("#{'${TIMEBOUND_ID_TEE}'.split(',')}")
	private String[] TIMEBOUND_ID_TEE;
	
	private String sourceAppName="NGASCE";
	
	public static final Logger demoExamMbaWXlogger = LoggerFactory.getLogger("demoExamCreationMbaWX");
	
	public ArrayList<Demoexam_keysBean> retriveSubjects(Person input) {
		//String userId = (String)request.getSession().getAttribute("userId");
		//StudentBean student = (StudentBean)request.getSession().getAttribute("student");
        String userId = input.getSapId();
		
		String Earlyaccess = input.getEarlyaccess();
		StudentMarksDAO sDao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		StudentExamBean student = sDao.getSingleStudentsData(userId);
		ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
		List<ExamOrderExamBean> liveFlagList = eDao.getLiveFlagDetails();
		ArrayList<StudentMarksBean> allStudentRegistrations = eDao.getAllRegistrationsFromSAPID(student.getSapid());
		HashMap<String, StudentMarksBean> monthYearAndStudentRegistrationMap = new HashMap<>();
		
		for (StudentMarksBean bean : allStudentRegistrations) {
			monthYearAndStudentRegistrationMap.put(bean.getMonth() + "-" + bean.getYear(), bean);
		}
		/*StudentMarksBean studentRegistrationForAcademicSession = getStudentRegistrationForForSpecificLiveSettings(monthYearAndStudentRegistrationMap, liveFlagList, "acadSessionLive");
		StudentMarksBean studentRegistrationForAssignment = getStudentRegistrationForForSpecificLiveSettings(monthYearAndStudentRegistrationMap, liveFlagList, "assignmentLive");
		 * */
		
		String liveTypeForCourses = "acadContentLive";
		if("Yes".equalsIgnoreCase(Earlyaccess)){
			liveTypeForCourses = "acadContentLiveNextBatch";
		}
		StudentMarksBean studentRegistrationForCourses = getStudentRegistrationForForSpecificLiveSettings(monthYearAndStudentRegistrationMap, liveFlagList, liveTypeForCourses);

		//getCourses(student,request,eDao, studentRegistrationForCourses);
	   // eDao.getSingleStudentsData(userId);
		
		//getcourse code
		ArrayList<String> allApplicableSubjects = new ArrayList<String>();
		ArrayList<String> currentSemSubjects = null;
		ArrayList<String> notPassedSubjects = null; //Subjects never appeared hence no entry in pass fail//
		 
		if(studentRegistrationForCourses != null){
			student.setSem(studentRegistrationForCourses.getSem());
			student.setProgram(studentRegistrationForCourses.getProgram());
			currentSemSubjects = getSubjectsForStudent(student);
		}

		if(currentSemSubjects == null){
			currentSemSubjects = new ArrayList<>();
		}
		allApplicableSubjects.addAll(currentSemSubjects);

		ArrayList<String> failedSubjects = eDao.getFailSubjectsNamesForAStudent(student.getSapid());


		if(failedSubjects != null){
			allApplicableSubjects.addAll(failedSubjects);
		}else{
			failedSubjects = new ArrayList<String>();
		}

		notPassedSubjects = eDao.getNotPassedSubjectsBasedOnSapid(student.getSapid());
		if(notPassedSubjects!=null && notPassedSubjects.size()>0){

			allApplicableSubjects.addAll(notPassedSubjects);
		}

		ArrayList<String> lstOfApplicableSubjects = new ArrayList<String>(new LinkedHashSet<String>(allApplicableSubjects));

		// remove WaiveOff Subject from applicable Subject list
		for(String subjects :allApplicableSubjects)
		{
			if(student.getWaivedOffSubjects().contains(subjects))
			{
				lstOfApplicableSubjects.remove(subjects);
			}
		}
		ArrayList<Demoexam_keysBean> subjectLinkMap =   eDao.getDemoExamKeysForSubjects(lstOfApplicableSubjects);
		return subjectLinkMap;
	}
	
	



	private StudentMarksBean getStudentRegistrationForForSpecificLiveSettings(
			HashMap<String, StudentMarksBean> monthYearAndStudentRegistrationMap,
			List<ExamOrderExamBean> liveFlagList, 
			String liveType) {

		double liveOrder = 0.0;
		String key = null;
		for (ExamOrderExamBean bean : liveFlagList) {
			double currentOrder = Double.parseDouble(bean.getOrder());

			if("acadSessionLive".equalsIgnoreCase(liveType)){
				if("Y".equalsIgnoreCase(bean.getAcadSessionLive()) && currentOrder > liveOrder){
					liveOrder = currentOrder;
					key = bean.getAcadMonth() + "-" + bean.getYear();
				}
			}else if("acadContentLive".equalsIgnoreCase(liveType)){
				if("Y".equalsIgnoreCase(bean.getAcadContentLive()) && currentOrder > liveOrder){
					liveOrder = currentOrder;
					key = bean.getAcadMonth() + "-" + bean.getYear();
				}
			}else if("assignmentLive".equalsIgnoreCase(liveType)){
				if("Y".equalsIgnoreCase(bean.getAssignmentLive()) && currentOrder > liveOrder){
					liveOrder = currentOrder;
					key = bean.getAcadMonth() + "-" + bean.getYear();
				}
			}else if("acadContentLiveNextBatch".equalsIgnoreCase(liveType)){
				if("Y".equalsIgnoreCase(bean.getAcadContentLive()) && currentOrder > liveOrder){
					liveOrder = currentOrder;
					key = bean.getAcadMonth() + "-" + bean.getYear();
				}
			}
		}

		if("acadContentLiveNextBatch".equalsIgnoreCase(liveType)){
			for (ExamOrderExamBean bean : liveFlagList) {
				double currentOrder = Double.parseDouble(bean.getOrder());
				if(currentOrder == (liveOrder + 1) ){
					key = bean.getAcadMonth() + "-" + bean.getYear();
				}
			}
		}

		return monthYearAndStudentRegistrationMap.get(key);
	}
	
	private ArrayList<String> getSubjectsForStudent(StudentExamBean student) {

		ArrayList<ProgramSubjectMappingExamBean> programSubjectMappingList = getProgramSubjectMappingList();
		ArrayList<String> subjects = new ArrayList<>();
		for (int i = 0; i < programSubjectMappingList.size(); i++) {
			ProgramSubjectMappingExamBean bean = programSubjectMappingList.get(i);

			if(
					bean.getPrgmStructApplicable().equals(student.getPrgmStructApplicable()) 
					&& bean.getProgram().equals(student.getProgram())
					&& bean.getSem().equals(student.getSem())
					&& !student.getWaivedOffSubjects().contains(bean.getSubject())//Subjects has not already cleared it
					){
				subjects.add(bean.getSubject());
			}
		}
		return subjects;
	}
	private ArrayList<ProgramSubjectMappingExamBean> programSubjectMappingList = null;
	public ArrayList<ProgramSubjectMappingExamBean> getProgramSubjectMappingList(){
		if(this.programSubjectMappingList == null || this.programSubjectMappingList.size() == 0){
			ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
			this.programSubjectMappingList = eDao.getProgramSubjectMappingList();
		}
		return programSubjectMappingList;
	}
	
	public MbaWxDemoExamScheduleDetailBean checkIfDemoExamKeyPresent(String sapid) throws Exception{
		
		MbaWxDemoExamScheduleDetailBean scheduleAndSapidDetail = demoExamDao.getStudentDetails(sapid);
		String programcode = scheduleAndSapidDetail.getProgram();
		String pogramName = programNameList.stream().anyMatch(program -> program.equalsIgnoreCase(programcode)) ? "PDDM" : programcode;
		String acadYear= programNameList.stream().anyMatch( program -> program.equalsIgnoreCase(programcode)) ? CURRENT_PDDM_ACAD_YEAR : CURRENT_MBAWX_ACAD_YEAR;
		String acadMonth= programNameList.stream().anyMatch( program -> program.equalsIgnoreCase(programcode)) ? CURRENT_PDDM_ACAD_MONTH : CURRENT_MBAWX_ACAD_MONTH;
		demoExamMbaWXlogger.info("Demo exam details for sapid:"+sapid+":(acadYear,acadMonth,Program):"+acadYear+","+acadMonth+","+pogramName);
		List<MbaWxDemoExamAttendanceBean> studentAttendanceList = demoExamDao.getDemoExamAttendanceRecords(sapid);
		List<MbaWxDemoExamKeysBean> demoExamKeyList = demoExamDao.getDemoExamKeyRecords(acadYear,acadMonth,pogramName);
		List<MbaWxDemoExamKeysBean> notAttemptedDemoExamKeyList = new ArrayList<MbaWxDemoExamKeysBean>();
		
		
		if(!demoExamKeyList.isEmpty()){
			notAttemptedDemoExamKeyList = (ArrayList<MbaWxDemoExamKeysBean>)demoExamKeyList.stream()
					.filter(demoExamKey -> studentAttendanceList.stream()
					.noneMatch(studentAttendance -> studentAttendance.getAccessKey().equalsIgnoreCase(demoExamKey.getAccessKey())))
					.collect(Collectors.toList());
			
			if(!notAttemptedDemoExamKeyList.isEmpty()) {
				MbaWxDemoExamKeysBean demoExamDetail = notAttemptedDemoExamKeyList.get(0);
				demoExamMbaWXlogger.info("demo exam exist in db for sapid:"+sapid+":Details"+demoExamDetail.toString());
				getScheduleDetail(scheduleAndSapidDetail,demoExamDetail.getId(),demoExamDetail.getAccessKey(),demoExamDetail.getLink(),demoExamDetail.getSubject());
			}else {
				demoExamMbaWXlogger.info("new demo exam creation for sapid:"+sapid+":(acadYear,acadMonth,Program):"+acadYear+","+acadMonth+","+pogramName);
				createDemoExamOnMettl(scheduleAndSapidDetail,acadYear,acadMonth,pogramName);
			}
		}else {
			demoExamMbaWXlogger.info("new demo exam creation for sapid:"+sapid+":(acadYear,acadMonth,Program):"+acadYear+","+acadMonth+","+pogramName);
			createDemoExamOnMettl(scheduleAndSapidDetail,acadYear,acadMonth,pogramName);
		}
		
		return scheduleAndSapidDetail;
		
	}
	
	
	public void createDemoExamOnMettl(MbaWxDemoExamScheduleDetailBean scheduleAndSapidDetail,String acadYear,String acadMonth,String programName) throws Exception {
		MettlScheduleExamBean mettlScheduleBean = createMettlDemoExamParameters(scheduleAndSapidDetail);
		MettlHelper mettlHelper = getMettlHelperFromProgram(scheduleAndSapidDetail);
		if(mettlHelper!=null) {
			MettlScheduleExamBean responseBean = mettlHelper.createSchedule(mettlScheduleBean, sourceAppName);
			if(responseBean.getStatus().equalsIgnoreCase("success")) {
				demoExamMbaWXlogger.info("response from schedule creation on mettl for sapid:"+scheduleAndSapidDetail.getSapid()+":"+responseBean.getStatus());
				MbaWxDemoExamScheduleDetailBean dbBean = createDbBean(responseBean,scheduleAndSapidDetail.getSapid(),acadYear,acadMonth,programName);
				demoExamMbaWXlogger.info("DB bean to be inserted for sapid:"+scheduleAndSapidDetail.getSapid()+":"+dbBean.toString());
				MbaWxDemoExamAttendanceBean result = demoExamDao.insertIntoDemoExamKeys(dbBean);
				demoExamMbaWXlogger.info("demo exam id from db for accesskey,sapid:"+dbBean.getScheduleAccessKey()+"-"+scheduleAndSapidDetail.getSapid()+"-"+result.getId()+":"+result.getMessage());
				if(result.getMessage().equalsIgnoreCase("success")) {
					demoExamMbaWXlogger.info("result after insertion in demo exam keys table for sapid:"+scheduleAndSapidDetail.getSapid()+":"+result.getMessage());
					getScheduleDetail(scheduleAndSapidDetail,result.getId(), dbBean.getScheduleAccessKey(),dbBean.getScheduleAccessUrl(),dbBean.getScheduleName());
				}else {
					demoExamMbaWXlogger.info("Error in saving data in db for sapid:"+scheduleAndSapidDetail.getSapid()+":"+result.getMessage());
					throw new Exception("Error in saving data in db:"+result.getMessage());
				}
			}else {
				demoExamMbaWXlogger.info("Error in creating demo exam schedule for sapid:"+scheduleAndSapidDetail.getSapid()+"-"+responseBean.getScheduleName()+"-"+responseBean.getMessage());
				throw new Exception("Error in creating demo exam schedule:"+responseBean.getScheduleName()+"-"+responseBean.getMessage());
			}
		}else {
			demoExamMbaWXlogger.info("Error in getting MettlHelper for sapid:"+scheduleAndSapidDetail.getSapid()+"-"+scheduleAndSapidDetail.getProgram());
			throw new Exception("Error in getting MettlHelper:"+scheduleAndSapidDetail.getProgram());
		}
	}
	
	public MettlScheduleExamBean createMettlDemoExamParameters(MbaWxDemoExamScheduleDetailBean scheduleAndSapidDetail) throws Exception{
		MettlScheduleExamBean mettlScheduleBean = new MettlScheduleExamBean();
		String assessmentId=getAssessmentIdByProgram(scheduleAndSapidDetail.getProgram());
		mettlScheduleBean.setAssessmentId(assessmentId);
		String dateTime = getCurrentDateTime();
		mettlScheduleBean.setCustomUrlId("Demo Exam-"+scheduleAndSapidDetail.getSapid()+"-"+dateTime);
		mettlScheduleBean.setSourceApp(sourceAppName);
		mettlScheduleBean.setTestStartNotificationUrl(START_METTL_DEMO_EXAM_MBAWX);
		mettlScheduleBean.setTestFinishNotificationUrl(END_METTL_DEMO_EXAM_MBAWX);
		return mettlScheduleBean;
	}
	
	public String getAssessmentIdByProgram(String program) throws Exception{
		if(program.equals("MBA - WX"))
			return DEMO_ASSESSMENT_ID_MBAWX;
		else if(program.equals("M.Sc. (AI & ML Ops)") || program.equals("M.Sc. (AI)"))
			return DEMO_ASSESSMENT_ID_MSC;
		else if(programNameList.contains(program))
			return DEMO_ASSESSMENT_ID_PDDM;
		else
			throw new Exception("Error while getting assessment id for program:"+program);
	}
	
	public String getCurrentDateTime() {
		 SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");  
		 Date date = new Date();  
		 return formatter.format(date);
	}
	
	public MettlHelper getMettlHelperFromProgram(MbaWxDemoExamScheduleDetailBean scheduleAndSapidDetail) {
		String program = scheduleAndSapidDetail.getProgram();
		if(program.equals("MBA - WX"))
			return (MettlHelper) act.getBean("mbaWxMettlHelper");
		else if(program.equals("M.Sc. (AI & ML Ops)") || program.equals("M.Sc. (AI)"))
			return (MettlHelper) act.getBean("mscMettlHelper");
		else if(programNameList.contains(program))
			return (MettlHelper) act.getBean("pddmMettlHelper");
		else
			return null;
	}
	
	public MbaWxDemoExamScheduleDetailBean createDbBean(MettlScheduleExamBean responseBean, String sapid,String acadYear,String acadMonth,String programName) {
		MbaWxDemoExamScheduleDetailBean dbBean =  new MbaWxDemoExamScheduleDetailBean();
		dbBean.setScheduleName(responseBean.getScheduleName());
		dbBean.setScheduleAccessKey(responseBean.getScheduleAccessKey());
		dbBean.setScheduleAccessUrl(responseBean.getScheduleAccessURL());
		dbBean.setAcadYear(acadYear);
		dbBean.setAcadMonth(acadMonth);
		dbBean.setProgram(programName);
		dbBean.setSapid(sapid);
		return dbBean;
	}
	
	public void getScheduleDetail(MbaWxDemoExamScheduleDetailBean scheduleAndSapidDetail,String demoExamId, String accessKey, String accessUrl ,String scheduleName) {
		scheduleAndSapidDetail.setScheduleAccessKey(accessKey);
		scheduleAndSapidDetail.setScheduleAccessUrl(accessUrl);
		scheduleAndSapidDetail.setScheduleName(scheduleName);
		scheduleAndSapidDetail.setScheduleId(demoExamId);
	}
	
	public void createAttendanceForDemoExamMBAWX(MbaWxDemoExamScheduleDetailBean scheduleAndSapidDetail) {
		demoExamDao.createAttendanceForDemoExamMBAWX(scheduleAndSapidDetail);
	}
	
	public String encryptParameters(TreeMap<String, String> params) throws Exception {
		try {
			List<String> valuesToEncrypt = new ArrayList<String>();
			for (Entry<String, String> entry : params.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				if(!StringUtils.isBlank(key) && !StringUtils.isBlank(value)) {
					valuesToEncrypt.add(key + "=" + value);	
				}
			}
			String data = StringUtils.join(valuesToEncrypt, "\n");
			
			String encryptedData = AESencrp.encrypt(data);
			String encryptedDataBase64 = getStringBase64Encoded(encryptedData);
			return encryptedDataBase64;
		}catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Error Encrypting Parameters");
		}
	}
	
	public String getStringBase64Encoded(String input) {
		return new String(Base64.encodeBase64(input.getBytes()));
	}
	
	public void checkDemoExamAttemptMbaWx() {

		try {
			String programArr [] = TIMEBOUND_ID_TEE;//Add program types in this list 
			for(int i=0;i<=programArr.length-1;i++)
			{
				demoExamMbaWXlogger.info("Running demo exam attempt status for progrm:"+programArr[i]);
				try {
					MettlHelper mettlHelper = getMettlHelperFromProgramType(programArr[i]);
					ArrayList<Integer> consumerProgramStructureIdList = getConsumerProgramStrucutreId(programArr[i]);
					DemoExamDAO demoExamDAO = (DemoExamDAO)act.getBean("demoExamDAO");
					ArrayList<DemoExamAttendanceBean> demoExamStudentBeanList = demoExamDAO.getStudentsByProgramMbaWX(consumerProgramStructureIdList);
					ArrayList<String> sapidList = new ArrayList<String>();
					demoExamStudentBeanList.stream().forEach(student -> sapidList.add(student.getSapid()));
					ArrayList<DemoExamAttendanceBean> demoExamPendingBeanList = demoExamDAO.getPendingAttendanceDataMbaWX(sapidList);

					if(demoExamPendingBeanList.size()>0) {
						Map<String,String> studentEmailMap = demoExamStudentBeanList.stream().collect(Collectors.toMap(DemoExamAttendanceBean::getSapid, DemoExamAttendanceBean::getEmailId));
						demoExamPendingBeanList.stream().forEach(pendingExam -> {
							if(studentEmailMap.containsKey(pendingExam.getSapid())) {
								pendingExam.setEmailId(studentEmailMap.get(pendingExam.getSapid()));
							}
						});

						for (DemoExamAttendanceBean demoExamAttendanceBean : demoExamPendingBeanList) {
							JsonObject jsonResponse = mettlHelper.getTestStatusMbaWX(demoExamAttendanceBean.getAccessKey(), demoExamAttendanceBean.getEmailId());
							String status = "pending";
							String endTime = null;
							String markAttend = null;
							try {
								if(jsonResponse != null) {
									JsonObject candidateObject = jsonResponse.get("candidate").getAsJsonObject();
									if("SUCCESS".equalsIgnoreCase(jsonResponse.get("status").getAsString())) {
										if(candidateObject != null) {
											JsonObject testStatusObject = candidateObject.get("testStatus").getAsJsonObject();
											if(testStatusObject != null && "Completed".equalsIgnoreCase(testStatusObject.get("status").getAsString())) {
												status = "success";
												markAttend = "Y";
												try {
													java.text.SimpleDateFormat df1 = new java.text.SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'");
													df1.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));
													java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
													df.setTimeZone(java.util.TimeZone.getTimeZone("Asia/Kolkata"));
													Date dateobj = df1.parse(testStatusObject.get("endTime").getAsString());
													endTime = df.format(dateobj);
												}
												catch (Exception e) {
													// TODO: handle exception
												}
											}else {
												status = testStatusObject.get("status").getAsString();
											}
										}else {
											status = "candidateObject null";
										}
									}else {
										status = jsonResponse.get("status").getAsString();
									}
								}
							}
							catch (Exception e) {
								// TODO: handle exception
								demoExamMbaWXlogger.info("Exception for student:"+demoExamAttendanceBean.getSapid()+"-"+demoExamAttendanceBean.getEmailId()+"-"+e);
								status = "Error: " + e.getMessage();
							}
							demoExamAttendanceBean.setStatus(status);
							demoExamAttendanceBean.setEndTime(endTime);
							demoExamAttendanceBean.setMarkAttend(markAttend);
							demoExamDAO.updateEndExamAttendanceByBatchJobMbaWX(demoExamAttendanceBean);
						}
					}
					else {
						demoExamMbaWXlogger.info("No pending records found for program:"+programArr[i]);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					demoExamMbaWXlogger.info("Exception for program:"+programArr[i]+"-"+e);
				}
			}
		}catch(Exception e) {
			demoExamMbaWXlogger.info("Exception"+e);
		}
	}
	
	public ResponseBean getDemoExamStatusForMbaWXStudent(String sapid) {
		ResponseBean responseBean = new ResponseBean();
		try {
				SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yy hh:mm aa");
				ArrayList<DemoExamAttendanceBean> demoExamAttendanceList = demoExamDao.getDemoExamStatusForMbaWXStudent(sapid);
				for(DemoExamAttendanceBean bean : demoExamAttendanceList) {
					if(bean.getStartedTime() != null) {
						Date dateStartTime = sdf1.parse(bean.getStartedTime());
						String newStartTime = sdf2.format(dateStartTime);
						bean.setStartedTime(newStartTime);
					}

					if(bean.getEndTime() != null) {
						Date dateEndTime = sdf1.parse(bean.getEndTime());
						String newEndTime = sdf2.format(dateEndTime);
						bean.setEndTime(newEndTime);
					}
				}
				responseBean.setDemoExamAttendanceList(demoExamAttendanceList);
				responseBean.setCode(200);
				return responseBean;

		}catch(Exception e) {
			logger.info("Exception in getDemoExamStatusForMbaWXStudent is :"+e.getMessage());
			responseBean.setCode(422);
			responseBean.setMessage("Error in Fetching records.  "+e.getMessage());
			return responseBean;
		}
	}
	
}