package com.nmims.controllers;



import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.base.Throwables;
import com.nmims.beans.AssignmentFileBean;
import com.nmims.beans.CenterExamBean;
import com.nmims.beans.MarksheetBean;
import com.nmims.beans.Page;
import com.nmims.beans.PassFailExamBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.daos.DashboardDAO;
import com.nmims.daos.PassFailDAO;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.exceptions.NoRecordFoundException;
import com.nmims.helpers.CreatePDF;
import com.nmims.services.AssignmentService;
import com.nmims.services.PassFailExecutorService;
import com.nmims.services.PassFailService;
import com.nmims.services.RedisResultsStoreService;
import com.nmims.views.PassFailResultsExcelView;

/**
 * Handles requests for the application home page.
 */
@Controller
public class PassFailController extends BaseController{

	@Autowired(required=false)
	ApplicationContext act;

	@Value( "${MARKSHEETS_PATH}" )
	private String MARKSHEETS_PATH;

	@Value("#{'${ACAD_YEAR_LIST}'.split(',')}") 
	private List<String> ACAD_YEAR_LIST; 

	protected List<String> SAS_EXAM_MONTH_LIST = Arrays.asList("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec");
	
	private final int pageSize = 20;

	private static final Logger logger = LoggerFactory.getLogger(PassFailController.class);

	private static final Logger passFailLogger = LoggerFactory.getLogger("pg-passfail-process");
	
	private ArrayList<String> programList = null;

	@Value("#{'${PAST_YEAR_LIST}'.split(',')}")
	private ArrayList<String> yearList; 

	@Value("#{'${PAST_YEAR_LIST}'.split(',')}")
	private ArrayList<String> validityYearList; 

	@Value("#{'${PAST_YEAR_LIST}'.split(',')}")
	private ArrayList<String> graceYearList; 

	private ArrayList<String> subjectList = null; 
	private HashMap<String, String> programCodeNameMap = null;
	private ArrayList<CenterExamBean> centers = null; 
	private HashMap<String, CenterExamBean> centersMap = null; 
	private HashMap<String, String> centerCodeNameMap = null; 
	private ArrayList<String> progStructListFromProgramMaster = null;
	
	@Autowired
	AssignmentService asgService;
	
	@Autowired
	PassFailExecutorService passFailExecutorService; 
	
	@Autowired
	@Qualifier("passFailResultsExcelView")
	private PassFailResultsExcelView passFailResultsExcelView;
	
	@Autowired
	private PassFailService passFailService;
	
	@Autowired
	RedisResultsStoreService redisResultsStoreService;
	
	/**
	 * Refresh Cache function to refresh cache
	 * @param 
	 * none
	 * @return 
	 * none
	 * */
	public String RefreshCache() {
		subjectList = null;
		getSubjectList();
		
		programList = null;
		getProgramList();
		
		centerCodeNameMap = null;
		getCenterCodeNameMap();
		
		programCodeNameMap = null;
		getProgramMap();
		
		centers = null;
		getCentersList();
		
		return null;
	}
	
	public ArrayList<String> getProgStructListFromProgramMaster(){
		if(this.progStructListFromProgramMaster == null){
			DashboardDAO dao = (DashboardDAO)act.getBean("dashboardDAO");
			this.progStructListFromProgramMaster = dao.getProgStructListFromProgramMaster();
		}
		return progStructListFromProgramMaster;
	}
	
	public ArrayList<CenterExamBean> getCentersList(){
		if(this.centers == null){
			StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			this.centers = dao.getAllCenters();
		}
		return centers;
	}
	
	public HashMap<String, CenterExamBean> getCentersMap(){
		if(this.centers == null || this.centers.size() == 0){
			StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			this.centers = dao.getAllCenters();
		}
		centersMap = new HashMap<String, CenterExamBean>();
		for (int i = 0; i < centers.size(); i++) {
			CenterExamBean bean = centers.get(i);
			centersMap.put(bean.getCenterCode(), bean);
		}
		return centersMap;
	}

	public HashMap<String, String> getProgramMap(){
		if(this.programCodeNameMap == null || this.programCodeNameMap.size() == 0){
			StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			this.programCodeNameMap = dao.getProgramDetails();
		}
		return programCodeNameMap;
	}
	public ArrayList<String> getSubjectList(){
		if(this.subjectList == null || this.subjectList.size()==0){
			StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			this.subjectList = dao.getAllSubjects();
		}
		return subjectList;
	}

	public ArrayList<String> getProgramList(){
		if(this.programList == null || this.programList.size() == 0){
			StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			this.programList = dao.getAllPrograms();
		}
		return programList;
	}

	public HashMap<String, String> getCenterCodeNameMap(){
		if(this.centerCodeNameMap == null || this.centerCodeNameMap.size() == 0){
			StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			ArrayList<CenterExamBean> centers = dao.getAllCenters();
			centerCodeNameMap = new HashMap<>();
			for (int i = 0; i < centers.size(); i++) {
				CenterExamBean cBean = centers.get(i);
				centerCodeNameMap.put(cBean.getCenterCode(), cBean.getCenterName());
			}
		}
		return centerCodeNameMap;
	}
	
	@RequestMapping(value = "/admin/passFailTriggerSearchForm", method = RequestMethod.GET)
	public String passFailTriggerSearchForm(HttpServletRequest request, HttpServletResponse response, Model m) {
		passFailLogger.info("PassFailController : passFailTriggerSearchForm : START");// 
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		StudentExamBean bean = new StudentExamBean();
		m.addAttribute("bean",bean);
		m.addAttribute("programList", getProgramList());
		m.addAttribute("yearList", ACAD_YEAR_LIST);
		m.addAttribute("monthList", SAS_EXAM_MONTH_LIST);
		m.addAttribute("subjectList", getSubjectList());
		m.addAttribute("progStructListFromProgramMaster", getProgStructListFromProgramMaster());
		m.addAttribute("rowCount",0);
		//request.getSession().setAttribute("searchBean",bean); //Not needed, Commented by Vilpesh on 2021-12-16
		passFailLogger.info("PassFailController : passFailTriggerSearchForm : END");
		return "passFailTriggerSearchForm";
	}

	
	@RequestMapping(value = "/admin/processPassFailForm", method =  RequestMethod.POST)
	public ModelAndView processPassFailForm(HttpServletRequest request, HttpServletResponse respnse, @ModelAttribute StudentExamBean searchBean, Model m) {
		passFailLogger.info("PassFailController : processPassFailForm : START");
		ModelAndView modelnView = new ModelAndView("processPassFail");
		PassFailDAO dao = (PassFailDAO)act.getBean("passFailDAO");

		passFailLogger.info(
				"PassFailController : processPassFailForm : (resProcYear, resProcMonth, Pgm, PgmStruc, Consumer) : ("
						+ searchBean.getYear() + "," + searchBean.getMonth() + "," + searchBean.getProgram() + ","
						+ searchBean.getPrgmStructApplicable() + "," + searchBean.getConsumerType() + ")");
		
		int pendingRecordsBySAPid = dao.getPendingRecordsCountBySAPid(searchBean);
		int pendingRecordsByGRNO = dao.getPendingRecordsCountByGRNO();
		 // int projectCountOnline = 0 ;
		// int projectCountOffline = 0;

		
		int projectCountOnline = 0;
		int projectCountOffline = 0;
		int projectAbsentCount = 0;
		int absentCount = 0;
		int nvRiaCount = 0;
		int ansCount = 0;
		int offlineCount = 0;

		int onlineCount =  0;
		int writtenScoreOnlineCount= 0;
		int writtenScoreOfflineCount= 0;
		int assignmentScoreOnlineCount= 0;
		int assignemntScoreOfflineCount= 0;

		int projectNotBookedCount = 0;
		if(pendingRecordsBySAPid>0 || pendingRecordsByGRNO>0){
		
				
			HashMap<String,StudentExamBean> studentList = passFailService.getAllStudents(searchBean);
			
			HashMap<String,Integer> studentProjectcount = passFailService.getPendingCountForOnlineOfflineProject(searchBean,studentList);
			projectCountOnline = studentProjectcount.get("projectCountOnline");
			projectCountOffline = studentProjectcount.get("projectCountOffline");
			projectAbsentCount = studentProjectcount.get("projectAbsentCount");
			projectNotBookedCount = studentProjectcount.get("projectNotBookedCount");
				
			HashMap<String,Integer> studentWrittencount = passFailService.getPendingCountForOnlineOfflineWritten(searchBean,studentList);
			writtenScoreOnlineCount = studentWrittencount.get("writtenScoreOnlineCount");
			writtenScoreOfflineCount = studentWrittencount.get("writtenScoreOfflineCount");
			
		
			HashMap<String,Integer> studentAssignmentCount = passFailService.getPendingCountForOnlineOfflineAssignment(searchBean,studentList);
			assignmentScoreOnlineCount = studentAssignmentCount.get("assignmentScoreOnlineCount");
			assignemntScoreOfflineCount = studentAssignmentCount.get("assignemntScoreOfflineCount");
				
			absentCount = passFailService.getPendingRecordsForAbsent(searchBean);	  //Not Break the Query because its returning the count
	
			nvRiaCount = passFailService.getPendingRecordsForNVRIA(searchBean);//Not Break the Query because its returning the count
		
			ansCount = passFailService.getPendingRecordsForANS(searchBean);//Not Break the Query because its returning the count
			


			HashMap<String,Integer> studentOnlineOfflineCount = passFailService.getPendingCountForOnlineOffline(searchBean,studentList);
			onlineCount = studentOnlineOfflineCount.get("onlineCount");
			offlineCount =studentOnlineOfflineCount.get("offlineCount");

			//Clearing the Student List HashMap
			studentList = null;
					
		}
		
		
		//PassFailTriggerDashboard//
		//StudentMarksBean marks = new StudentMarksBean(); //Not needed, Commented by Vilpesh on 2021-12-17
		//m.addAttribute("studentMarks",marks); //Not needed, Commented by Vilpesh on 2021-12-17
		m.addAttribute("programList", getProgramList());
		m.addAttribute("yearList", yearList);
		m.addAttribute("subjectList", getSubjectList());
		//m.addAttribute("projectCount", projectCount);
		m.addAttribute("absentCount", absentCount);
		m.addAttribute("nvRiaCount", nvRiaCount);
		m.addAttribute("ansCount", ansCount);
		m.addAttribute("onlineCount", onlineCount);
		m.addAttribute("offlineCount", offlineCount);
		m.addAttribute("projectCountOnline", projectCountOnline);
		m.addAttribute("projectCountOffline", projectCountOffline);
		m.addAttribute("writtenScoreOnlineCount", writtenScoreOnlineCount);
		m.addAttribute("writtenScoreOfflineCount", writtenScoreOfflineCount);
		m.addAttribute("assignmentScoreOnlineCount", assignmentScoreOnlineCount);
		m.addAttribute("assignemntScoreOfflineCount", assignemntScoreOfflineCount);

		m.addAttribute("projectAbsentCount",projectAbsentCount);
		m.addAttribute("projectNotBookedCount",projectNotBookedCount);

		m.addAttribute("searchBean",searchBean);
		//request.getSession().setAttribute("studentMarks", marks); //Not needed, Commented by Vilpesh on 2021-12-17
		request.getSession().setAttribute("searchBean",searchBean);
		modelnView.addObject("pendingRecordsCount", pendingRecordsBySAPid+pendingRecordsByGRNO+"");
		passFailLogger.info("onlineCount "+onlineCount);
		passFailLogger.info("offlineCount "+offlineCount);
		passFailLogger.info("projectCountOnline "+projectCountOnline);
		passFailLogger.info("projectCountOffline "+projectCountOffline);
		passFailLogger.info("writtenScoreOnlineCount "+writtenScoreOnlineCount);
		passFailLogger.info("writtenScoreOfflineCount "+writtenScoreOfflineCount);
		passFailLogger.info("assignmentScoreOnlineCount "+assignmentScoreOnlineCount);
		passFailLogger.info("assignemntScoreOfflineCount "+assignemntScoreOfflineCount);
		passFailLogger.info("pendingRecordsCount "+ (pendingRecordsBySAPid+pendingRecordsByGRNO));
		passFailLogger.info("PassFailController : processPassFailForm : END");
		
		return modelnView;
	}

	@RequestMapping(value = "/admin/processPassFail", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView processPassFail(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		passFailLogger.info("processPassFail START");
		ModelAndView modelnView = new ModelAndView("processPassFail");
		PassFailDAO dao = (PassFailDAO)act.getBean("passFailDAO");
		StudentExamBean searchBean = (StudentExamBean)request.getSession().getAttribute("searchBean");

		StringBuilder builder = new StringBuilder();
		try{
			List<PassFailExamBean> processPassFailRecords = processRecordsBySAPID(searchBean);
			
			passFailLogger.info("{} {} received processPassFailRecords to filter for applicable records : {}", searchBean.getYear(),
					searchBean.getMonth(), processPassFailRecords.size());
			
			passFailLogger.info("{} {} filterApplyGraceStudents -- START",searchBean.getYear(),searchBean.getMonth());
			
			List<PassFailExamBean> filteredApplicableRecords = passFailService.filterApplyGraceStudents(processPassFailRecords);
			
			passFailLogger.info("{} {} filteredApplicableRecords : {} -- END",searchBean.getYear(),searchBean.getMonth(),filteredApplicableRecords.size());
			
			passFailLogger.info("{} {} applying grace now -- START ",searchBean.getYear(),searchBean.getMonth());
		
			int graceAppliedCount = dao.applyGraceToPassFailStaging(filteredApplicableRecords);
			
			request.getSession().setAttribute("totalGraceList", filteredApplicableRecords);
			
			passFailLogger.info("{} {} grace applied to : {} -- END",searchBean.getYear(),searchBean.getMonth(), graceAppliedCount);
			
			processRecordsByGRNO();
			
			
			builder.append("Pass/fail processing completed successfully. Please check pending records if any.");
			if(graceAppliedCount > 0) {
				builder.append(" Grace marks applied to : ")
				.append(graceAppliedCount)
				.append(" Records click <a href = 'downloadGraceEligible'>here</a> to download grace applied excel");
			}
			
			String successMessage = builder.toString();
			
			request.setAttribute("success","true");
			request.setAttribute("successMessage",successMessage);
			//dao.updatePassFailResultProcessedYearMonth(searchBean);
		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in processing pass fail logic.");
			passFailLogger.error("Error in processing pass fail logic : {}", Throwables.getStackTraceAsString(e));
		}finally{
			passFailLogger.info("processPassFail END");
			int pendingRecordsBySAPid = dao.getPendingRecordsCountBySAPid(searchBean);
			int pendingRecordsByGRNO = dao.getPendingRecordsCountByGRNO();
			passFailLogger.info("pendingRecordsBySAPid "+pendingRecordsBySAPid +" pendingRecordsByGRNO "+pendingRecordsByGRNO );
			modelnView.addObject("pendingRecordsCount", pendingRecordsBySAPid+pendingRecordsByGRNO+"");
			builder.setLength(0);
		}
		return modelnView;
	}
	
	@RequestMapping(value = "/admin/processPassFailForASubject", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView processPassFailForASubject(HttpServletRequest request, HttpServletResponse respnse, Model m,@ModelAttribute PassFailExamBean studentMarks) {
		logger.info("{} processPassFailForASubject :: passfail triggered by {}",studentMarks.getSapid(), request.getSession().getAttribute("userId"));
		PassFailDAO dao = (PassFailDAO)act.getBean("passFailDAO");
		String sapid=studentMarks.getSapid();
		String subject=studentMarks.getSubject();
		ArrayList<PassFailExamBean> passFailStudentList = null;
		try{
			//1. Check if student is BAJAJ(prev. ACBM) or General
			StudentMarksDAO sDao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			StudentExamBean student = sDao.getSingleStudentsData(studentMarks.getSapid());
			

//			if("ACBM".equalsIgnoreCase(student.getProgram())){
			boolean isBajaj = "Bajaj".equalsIgnoreCase(student.getConsumerType());
			boolean isDBMJul2014 = "Jul2014".equalsIgnoreCase(student.getPrgmStructApplicable()) && "DBM".equalsIgnoreCase(student.getProgram());
			if(isBajaj && !isDBMJul2014){
				//For Bajaj(prev. ACBM) pull all subjects for that sapid, sem (not subject)
				HashMap<String, ArrayList> bajajKeysMap = dao.getPendingRecordsForPassFailForBajajForASem(studentMarks);
				passFailStudentList = dao.processBajaj(bajajKeysMap);
				
				/*ArrayList<PassFailBean> tempBajajPassFailStudentList = dao.processBajaj(bajajKeysMap);
				passFailStudentList = dao.processBajajSemWise(tempBajajPassFailStudentList);*/
			}else{
				HashMap<String, ArrayList> keysMap = dao.getMarksRecordsForStudentForASubject(studentMarks);
				 logger.info("{} {} processPassFailForASubject :: keysMap size is :: {}",sapid,subject,keysMap.size());
				passFailStudentList = dao.processNew(keysMap);
				 
				 
				
			}
			
			if(passFailStudentList == null || passFailStudentList.isEmpty()) {
				throw new NoRecordFoundException("Passfail records not found!!!");
			}
			 logger.info("{} {} processPassFailForASubject :: passFailStudentList size is :: {}",sapid,subject,passFailStudentList.size());
			//get resultprocessing year and month from passfail table
			for(PassFailExamBean p : passFailStudentList) {
				p = dao.getResultProcessingYearMonthByBean(p);
				
			}
			Map<String,String> latestYearMonthMap=passFailService.getLatestYear(passFailStudentList);
			String leatestYear=latestYearMonthMap.get("year");
			String latestMonth= latestYearMonthMap.get("month");
			//3. Update in DB
			HashMap<String, String> keysBySAPID = dao.getKeysBySAPID();       
			dao.upsertPassFailRecordsBySAPID(passFailStudentList, keysBySAPID);
			
			List<PassFailExamBean> filterApplyGraceStudents = passFailService.filterApplyGraceStudents(passFailStudentList);
			//if found in filter then applyGrace()
			if((filterApplyGraceStudents!=null) && !(filterApplyGraceStudents.isEmpty())){
				dao.applyGrace(filterApplyGraceStudents);
				logger.info(" {} {} processPassFailForASubject :: grace applyed student",sapid,subject);
				List<PassFailExamBean> passFailRecord = dao.getPassFailRecord(studentMarks.getSapid(),studentMarks.getSubject());
				if((passFailRecord!=null) && !(passFailRecord.isEmpty())){
				passFailStudentList = new ArrayList<>(passFailRecord);
				}
			}
			
			asgService.updateQuickAssgTableOnPassfailProcess(passFailStudentList);
			
			logger.info(" {} {} processPassFailForASubject :: fetchAndStoreResultsInRedisForSingleStudent {} {} ",sapid,subject,leatestYear,latestMonth);
			//call make live
			redisResultsStoreService.fetchAndStoreResultsInRedisForSingleStudent(leatestYear, latestMonth,studentMarks.getSapid());
			request.setAttribute("success","true");
			request.setAttribute("successMessage","Pass/fail processing completed successfully for mentioned subject. ");

		}catch(NoRecordFoundException e){
			logger.info("{} {} processPassFailForASubject :: Exception while proccessing passFail exception is :: {}",sapid,subject ,Throwables.getStackTraceAsString(e));
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", e.getMessage());
		}catch(Exception e){
			 logger.info("{} {} processPassFailForASubject :: Exception while proccessing passFail exception is :: {}",sapid,subject,Throwables.getStackTraceAsString(e));
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in processing pass fail logic.");
		}
		return searchPassFailPage(request, respnse);
	}

	public void processRecordsByGRNO(){
		PassFailDAO dao = (PassFailDAO)act.getBean("passFailDAO");

		HashMap<String, ArrayList> keysMap = dao.getPendingRecordsForPassFailProcessingBYGRNO();
		ArrayList<PassFailExamBean> passFailStudentList = dao.process(keysMap);
		HashMap<String, String> keysByGRNO = dao.getStagingKeysByGRNO();

		dao.upsertPassFailStagingRecordsByGRNO(passFailStudentList, keysByGRNO);
	}


	public List<PassFailExamBean> processRecordsBySAPID(StudentExamBean searchBean){
		PassFailDAO dao = (PassFailDAO)act.getBean("passFailDAO");
		
		String resultProcessedYear = searchBean.getYear();
		String resultProcessedMonth = searchBean.getMonth();
		passFailLogger.info("getPendingRecordsForPassFailProcessingBYSAPID resultProcessedYear "+resultProcessedYear+" resultProcessedMonth "+resultProcessedMonth);
		HashMap<String, ArrayList> keysMap = dao.getPendingRecordsForPassFailProcessingBYSAPID(searchBean);
		
		passFailLogger.info("getPendingRecordsForPassFailProcessingBYSAPID size "+keysMap.size());
		/* Process Non-BAJAJ(prev. ACBM) Cases and upsert in Passfail table */
		
		//commented process new code and replaced the one with multithreading instead - Swarup Feb 2023
//		ArrayList<PassFailExamBean> passFailStudentList = dao.processNew(keysMap);
		ArrayList<PassFailExamBean> passFailStudentList = passFailExecutorService.processNewForPassFail(keysMap);
		
		
		passFailLogger.info("passFailStudentList size "+passFailStudentList.size());
		
//		HashMap<String, String> keysBySAPID = dao.getKeysBySAPID();
		
		HashMap<String, String> existingRecordsFromPassFailStaging = dao.getExistingRecordsFromPassFailStaging();
		
		passFailLogger.info("keysBySAPID size "+existingRecordsFromPassFailStaging.size());
		
		for(PassFailExamBean b : passFailStudentList) {
			//do be chagned later by PS 2feb19 after best of tee results are live
			if(StringUtils.isBlank(searchBean.getYear()) || StringUtils.isBlank(searchBean.getMonth())) {

				b.setResultProcessedYear("2018");
				b.setResultProcessedMonth("Dec");
			}else {
				b.setResultProcessedYear(resultProcessedYear);
				b.setResultProcessedMonth(resultProcessedMonth);
			}
		}
		
		
		passFailLogger.info("upsert PassFailRecords By SAPID  START ");
// 		replaced single threaded batch update with multithreaded one - Swarup Feb 2023		
//		dao.upsertPassFailRecordsBySAPID(passFailStudentList, keysBySAPID);
		passFailExecutorService.updatePassFailStagingTable(passFailStudentList, existingRecordsFromPassFailStaging);
		
		passFailLogger.info("upsert PassFailRecords By SAPID  END ");
//		
//		passFailLogger.info("update QuickAssgTable On Passfail Process START");
		//shifted to make life controller
//		asgService.updateQuickAssgTableOnPassfailProcess(passFailStudentList);

//		passFailLogger.info("update QuickAssgTable On Passfail Process END");
		
		passFailLogger.info("PassFail Process For BAJAJ START");
		/* Process BAJAJ(prev. ACBM) Cases and upsert in Passfail table */
		HashMap<String, ArrayList> bajajKeysMap = dao.getPendingRecordsForPassFailForBajaj(searchBean);
		passFailLogger.info("getPendingRecordsForPassFailForBajaj size "+bajajKeysMap.size());
		
		ArrayList<PassFailExamBean> bajajPassFailStudentList = dao.processBajaj(bajajKeysMap);
		passFailLogger.info("bajajPassFailStudentList size "+bajajPassFailStudentList.size());
		/*ArrayList<PassFailBean> tempBajajPassFailStudentList = dao.processBajaj(bajajKeysMap);
		ArrayList<PassFailBean> bajajPassFailStudentList = dao.processBajajSemWise(tempBajajPassFailStudentList);*/
		for(PassFailExamBean b : bajajPassFailStudentList) {
			b.setResultProcessedYear(resultProcessedYear);
			b.setResultProcessedMonth(resultProcessedMonth);
		}
		passFailLogger.info("upsert PassFailRecords By SAPID For BAJAJ  START ");
		dao.upsertPassFailStagingRecordsBySAPID(bajajPassFailStudentList, existingRecordsFromPassFailStaging);
		passFailLogger.info("upsert PassFailRecords By SAPID For BAJAJ  END ");
		
		return passFailStudentList;
	}

	@RequestMapping(value = "/admin/getResultNotice", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView getResultNotice(HttpServletRequest request, HttpServletResponse respnse) {
		ModelAndView mav=new ModelAndView("getResultNotice");
		
		return mav;
	}

	@RequestMapping(value = "/admin/getGraceEligibleForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView getGraceEligibleForm(HttpServletRequest request, HttpServletResponse respnse) {
		logger.info("PassFailController : getGraceEligibleForm : START");// 
		request.getSession().setAttribute("graceList",null);
		ModelAndView modelnView = new ModelAndView("graceEligibleForm");
		PassFailDAO dao = (PassFailDAO)act.getBean("passFailDAO");

		//List<PassFailBean> studentMarksList = dao.getStudentsEligibleForGrace();
		PassFailExamBean bean = new PassFailExamBean();
		//m.addAttribute("studentMarksList",studentMarksList);
		modelnView.addObject("yearList", graceYearList);
		modelnView.addObject("studentMarks", bean);
		modelnView.addObject("rowCount", 0);
		logger.info("PassFailController : getGraceEligibleForm : END");
		return modelnView;
	}


	@RequestMapping(value = "/admin/searchGrace", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView searchGrace(HttpServletRequest request, HttpServletResponse response, @ModelAttribute PassFailExamBean studentMarks) {
		passFailLogger.info("--------------- SEARCH GRACE : {} {} START ---------------",
				studentMarks.getWrittenYear(),studentMarks.getWrittenMonth());
		ModelAndView modelnView = new ModelAndView("graceEligibleStudents");
		PassFailDAO dao = (PassFailDAO)act.getBean("passFailDAO");
		

//		List<PassFailExamBean> studentMarksList = dao.getStudentsEligibleForGrace(studentMarks);
		List<PassFailExamBean> studentMarksList = dao.getStudentsEligibleForGraceFromStaging(studentMarks.getWrittenYear(),studentMarks.getWrittenMonth());
		passFailLogger.info("Grace eligible students found studentMarksList : {}", studentMarksList.size());
//		List<PassFailExamBean> bajajStudentMarksList = dao.getBajajStudentsEligibleForGrace(studentMarks);
		List<PassFailExamBean> bajajStudentMarksList = dao.getBajajStudentsEligibleForGraceFromStaging(studentMarks.getWrittenYear(),studentMarks.getWrittenMonth());
		passFailLogger.info("Grace eligible students found bajajStudentMarksList : {}", bajajStudentMarksList.size());
	
		studentMarksList.removeIf(c -> ("Copy Case".equals(c.getRemarks()) ));
		
		passFailLogger.info("studentMarksList after removing copy case students : {}", studentMarksList.size());
		
		PassFailExamBean bean = new PassFailExamBean();
		modelnView.addObject("studentMarksList",studentMarksList);
		modelnView.addObject("bajajStudentMarksList",bajajStudentMarksList);
		modelnView.addObject("rowCount", studentMarksList.size() + bajajStudentMarksList.size());
		request.getSession().setAttribute("graceList",studentMarksList);
		request.getSession().setAttribute("bajajGraceList",bajajStudentMarksList);
		
		List<PassFailExamBean> totalGraceList = new ArrayList<PassFailExamBean>();
		totalGraceList.addAll(studentMarksList);
		totalGraceList.addAll(bajajStudentMarksList);
		request.getSession().setAttribute("totalGraceList", totalGraceList);
		
		passFailLogger.info("total to be grace applied student : studentMarksList + bajajStudentMarksList = {}",totalGraceList.size());
		if(studentMarksList.size() == 0 && bajajStudentMarksList.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No records found that are eligible for grace.");
			return getGraceEligibleForm(request, response);
		}
		passFailLogger.info("--------------- SEARCH GRACE : {} {} END ---------------",
				studentMarks.getWrittenYear(),studentMarks.getWrittenMonth());
		return modelnView;
	}

	@RequestMapping(value = "/admin/applyGrace", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView applyGrace(HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		passFailLogger.info("--------------- APPLYING GRACE START ---------------");
		
		ModelAndView modelnView = new ModelAndView("graceEligibleStudents");
		PassFailDAO dao = (PassFailDAO)act.getBean("passFailDAO");
		List<PassFailExamBean> studentMarksList = (List<PassFailExamBean>)request.getSession().getAttribute("graceList");
		passFailLogger.info("studentMarksList : grace to be applied : {}",studentMarksList.size());
		List<PassFailExamBean> bajajStudentMarksList = (List<PassFailExamBean>)request.getSession().getAttribute("bajajGraceList");
		passFailLogger.info("bajajStudentMarksList : grace to be applied : {}",bajajStudentMarksList.size());
		
		try{
//			dao.applyGrace(studentMarksList);
//			dao.applyBajajGrace(bajajStudentMarksList);
			int applyGraceToPassFailStaging = dao.applyGraceToPassFailStaging(studentMarksList);
			passFailLogger.info("total rows affected in passfail : {}",applyGraceToPassFailStaging);
			dao.applyBajajGraceToPassFailStaging(bajajStudentMarksList);
//			asgService.updateQuickAssgTableOnApplyGraceMark(studentMarksList);
			request.setAttribute("success","true");
			request.setAttribute("successMessage","Grace applied successfully for "+(studentMarksList.size() +  bajajStudentMarksList.size() )  +" records.");
			modelnView.addObject("rowCount", 0);
			request.getSession().setAttribute("graceList",null);
			request.getSession().setAttribute("bajajGraceList",null);
			return modelnView;
		}catch(Exception e){
			passFailLogger.info("ERROR while applyin grace to passfail records : {}", Throwables.getStackTraceAsString(e));
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in applying grace.");
		}
		finally {
			passFailLogger.info("--------------- APPLYING GRACE END ---------------");
		}
		PassFailExamBean bean = new PassFailExamBean();
		modelnView.addObject("studentMarksList",studentMarksList);
		modelnView.addObject("rowCount", studentMarksList.size());

		return modelnView;
	}
	
	@RequestMapping(value = "/admin/downloadGraceEligible", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadGraceEligible(HttpServletRequest request, HttpServletResponse response) {
		logger.info("Process Pass Fail Page");
		List<PassFailExamBean> studentMarksList = (List<PassFailExamBean>)request.getSession().getAttribute("totalGraceList");
		return new ModelAndView("graceEligibleExcelView","graceList",studentMarksList);
	}


	private ArrayList<String> checkIfStudentsPresentInMasterDB(List<PassFailExamBean> studentMarksList,	HashMap<String, MarksheetBean> studentMap) {
		ArrayList<String> errorList = new ArrayList<>();
		for (int i = 0; i < studentMarksList.size(); i++) {
			String sapId = studentMarksList.get(i).getSapid().trim();
			if((!studentMap.containsKey(sapId)) && (!"Not Available".equals(sapId)) ){
				errorList.add(sapId);
			}
			
		}
		return errorList;
	}

	@RequestMapping(value = "/admin/searchPassFailForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String searchPassFailForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		logger.info("Add New Company Page");

		PassFailExamBean bean = new PassFailExamBean();
		m.addAttribute("studentMarks",bean);
		m.addAttribute("programList", getProgramList());
		m.addAttribute("yearList", yearList);
		m.addAttribute("subjectList", getSubjectList());
		m.addAttribute("centerList", getCenterCodeNameMap());
		request.getSession().setAttribute("passFailSearchBean", bean);

		return "searchPassFail";
	}

	@RequestMapping(value = "/admin/searchPassFail", method = RequestMethod.POST)
	public ModelAndView searchPassFail(HttpServletRequest request, HttpServletResponse response, @ModelAttribute PassFailExamBean studentMarks){
		if(!checkSession(request, response)){
			return new ModelAndView("login");
		}
		ModelAndView modelnView = new ModelAndView("searchPassFail");
		request.getSession().setAttribute("passFailSearchBean", studentMarks);
		
		PassFailDAO dao = (PassFailDAO)act.getBean("passFailDAO");
		Page<PassFailExamBean> page = dao.getPassFailPage(1, pageSize, studentMarks, getAuthorizedCodes(request));
		List<PassFailExamBean> studentMarksList = page.getPageItems();
		
		studentMarksList = getStudentAssignmentRemarks(studentMarksList,dao);
		
		/**
		 * Added By shivam.pandey.EXT - START
		 */
		//Check If studentMarksList is not null
		if(studentMarksList != null)
		{
			//To add result declared date in studentMarksList
			studentMarksList = passFailService.setResultDecDateInPassFailList(studentMarksList);
		}
		/**
		 * Added By shivam.pandey.EXT - END
		 */
		
		modelnView.addObject("studentMarksList", studentMarksList);
		modelnView.addObject("page", page);

		modelnView.addObject("studentMarks", studentMarks);
		modelnView.addObject("rowCount", page.getRowCount());

		modelnView.addObject("programList", getProgramList());
		modelnView.addObject("validityYearList", validityYearList);
		modelnView.addObject("yearList", yearList);
		modelnView.addObject("subjectList", getSubjectList());
		modelnView.addObject("centerList", getCenterCodeNameMap());

		if(studentMarksList == null || studentMarksList.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No records found.");
		}
		return modelnView;
	}
	
	@RequestMapping(value = "/admin/downloadPassFailResults", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadPassFailResults(HttpServletRequest request, HttpServletResponse response) {
		
		
		logger.info("Process Pass Fail Page");
		
		if (!checkSession(request, response)) {
			return new ModelAndView("studentPortalRediret");
		}

		PassFailExamBean studentMarks = (PassFailExamBean)request.getSession().getAttribute("passFailSearchBean");
		StudentMarksDAO studentMarksDao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		PassFailResultsExcelView helper = new PassFailResultsExcelView();
		PassFailDAO dao = (PassFailDAO)act.getBean("passFailDAO");
		Page<PassFailExamBean> page = dao.getPassFailPage(1, Integer.MAX_VALUE, studentMarks, getAuthorizedCodes(request));
		List<PassFailExamBean> studentMarksList = page.getPageItems();
		ModelAndView modelnView = new ModelAndView("searchPassFail");
		studentMarksList = getStudentAssignmentRemarks(studentMarksList,dao);
		
		/**
		 * Added By shivam.pandey.EXT - START
		 */
		//Check If studentMarksList is not null
		if(studentMarksList != null)
		{
			//To add result declared date in studentMarksList
			studentMarksList = passFailService.setResultDecDateInPassFailList(studentMarksList);
		}
		/**
		 * Added By shivam.pandey.EXT - END
		 */
		
		modelnView.addObject("studentMarksList", studentMarksList);
		modelnView.addObject("page", page);

		modelnView.addObject("studentMarks", studentMarks);
		modelnView.addObject("rowCount", page.getRowCount());

		modelnView.addObject("programList", getProgramList());
		modelnView.addObject("validityYearList", validityYearList);
		modelnView.addObject("yearList", yearList);
		modelnView.addObject("subjectList", getSubjectList());
		modelnView.addObject("centerList", getCenterCodeNameMap());
		
		try{
			request.setAttribute("success", "true");
			request.setAttribute("successMessage", "Report generated successfully");
			return new ModelAndView(this.passFailResultsExcelView,"studentMarksList",studentMarksList);
			//return new ModelAndView("passFailResultsExcelView","studentMarksList",studentMarksList);
			//helper.buildExcelDocument(studentMarksList,request,response);
			
			
		}catch(Exception e){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error generating report.");
				
		}
		
		
		return modelnView;
	}


	@RequestMapping(value = "/admin/searchPassFailPage", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView searchPassFailPage(HttpServletRequest request, HttpServletResponse response){
		ModelAndView modelnView = new ModelAndView("searchPassFail");
		int pageNo = 1;
		
		if(request.getParameter("pageNo") != null){
			pageNo = Integer.parseInt(request.getParameter("pageNo"));
		}
		
		PassFailExamBean studentMarks = (PassFailExamBean)request.getSession().getAttribute("passFailSearchBean");

		PassFailDAO dao = (PassFailDAO)act.getBean("passFailDAO");

		Page<PassFailExamBean> page = dao.getPassFailPage(pageNo, pageSize, studentMarks, getAuthorizedCodes(request));
		List<PassFailExamBean> studentMarksList = page.getPageItems();
		
		studentMarksList = getStudentAssignmentRemarks(studentMarksList,dao);
		
		/**
		 * Added By shivam.pandey.EXT - START
		 */
		//Check If studentMarksList is not null
		if(studentMarksList != null)
		{
			//To add result declared date in studentMarksList
			studentMarksList = passFailService.setResultDecDateInPassFailList(studentMarksList);
		}
		/**
		 * Added By shivam.pandey.EXT - END
		 */
		
		modelnView.addObject("studentMarksList", studentMarksList);
		modelnView.addObject("page", page);

		modelnView.addObject("studentMarks", studentMarks);
		modelnView.addObject("rowCount", page.getRowCount());

		modelnView.addObject("programList", getProgramList());
		modelnView.addObject("yearList", yearList);
		modelnView.addObject("subjectList", getSubjectList());
		modelnView.addObject("centerList", getCenterCodeNameMap());
		
		if(studentMarksList == null || studentMarksList.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No records found.");
		}
		return modelnView;
	}
	
	@RequestMapping(value = "/admin/getMarksheetSignatureExcel", method = RequestMethod.POST)
	public ModelAndView getMarksheetSignatureExcel(HttpServletRequest request, HttpServletResponse response, @ModelAttribute PassFailExamBean studentMarks){
		ModelAndView modelnView = new ModelAndView("marksheet");
		request.getSession().setAttribute("passFailSearchBean", studentMarks);

		PassFailDAO dao = (PassFailDAO)act.getBean("passFailDAO");
		//Page<PassFailBean> page = dao.getPassFailPage(1, pageSize, studentMarks);
		List<PassFailExamBean> studentMarksList = dao.getRecordsForMarksheet(studentMarks);

		modelnView.addObject("studentMarks", studentMarks);
		modelnView.addObject("programList", getProgramList());
		modelnView.addObject("yearList", yearList);
		modelnView.addObject("subjectList", getSubjectList());

		if(studentMarksList == null || studentMarksList.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No records found.");
			return modelnView;
		}

		HashMap<String, MarksheetBean> studentMap = dao.getStudentsData(studentMarks); 
		
		/*
		
		ArrayList<String> errorList = checkIfStudentsPresentInMasterDB(studentMarksList, studentMap);
		if(errorList.size() > 0){
			
			//Converting ArrayList to HashSet to remove duplicates
			HashSet<String> listToSet = new HashSet<String>(errorList);
			     
			//Creating Arraylist without duplicate values
			List<String> listWithoutDuplicates = new ArrayList<String>(listToSet);
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "SAPIDs given here not found in Students Master Database. Please contact academic team to add these."
					+ " Without students master data marksheet cannot be generated. "+listWithoutDuplicates);
			return modelnView;
		}*/
		


		StudentMarksDAO sDao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		HashMap<String, BigDecimal> examOrderMap = sDao.getExamOrderMap();
		String examMonth = studentMarks.getWrittenMonth();
		String examYear = studentMarks.getWrittenYear();
		
		CreatePDF helper = new CreatePDF();
		List<MarksheetBean> marksheetList = helper.generateMarksheetList(studentMarksList, studentMap, examMonth, examYear, examOrderMap, null, getCentersMap());
		Collections.sort(marksheetList);
			
		ArrayList<Object> data = new ArrayList<>();
		data.add(marksheetList);
		data.add(getProgramMap());
		data.add(getCentersMap());
		
		return new ModelAndView("marksheetSignatureExcelView","data",data);

	}
	
	@RequestMapping(value = "/admin/testPage", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView testPage (HttpServletRequest request, HttpServletResponse response){
		PassFailDAO dao = (PassFailDAO)act.getBean("passFailDAO");
		ArrayList<StudentMarksBean> passFailList = new ArrayList<>();
		try {
			HashMap<String, ArrayList> keysmap =  dao.getStudentsForReport();
			passFailList = dao.processForReport(keysmap);
		} catch (Exception e) {
			
		}
		return new ModelAndView("newPassFailReport","passFailList",passFailList);
		
	}
	
	/*Added by Stef
	 * 
	 *///Added by Stef
	  @RequestMapping(value = "/admin/applyGraceforValidityEnd", method = {RequestMethod.POST})
	public ResponseEntity<HashMap<String, String>> applyGraceforValidityEnd(@RequestParam String totalGracemarks,
  		  @RequestParam String program,
  		  @RequestParam String sapid,
  		  @RequestParam String studentType,
  		 @RequestParam String resultProcessedYear,
  		 @RequestParam String resultProcessedMonth,
  		  HttpServletRequest request){
		HashMap<String, String> response = new  HashMap<String, String>();
		try{
			passFailLogger.info(
					"/applyGraceforValidityEnd (sapid, studentType, program, resultProcessedYear, resultProcessedMonth) ("
							+ sapid + "," + studentType + "," + program + "," + resultProcessedYear + ","
							+ resultProcessedMonth + ")");
			//ModelAndView modelnView = new ModelAndView("graceToCompleteProgramReport");
			StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			PassFailDAO pdao = (PassFailDAO)act.getBean("passFailDAO");
			StudentExamBean student = dao.getSingleStudentsData(sapid);
			List<StudentMarksBean> studentMarksDetails = pdao.getSingleStudentPassFailMarksData(sapid,program);
			int totalApplicableGrace = Integer.parseInt(totalGracemarks);
			if(studentMarksDetails.isEmpty()) {
				response.put("Status", "Students PassFail marks not found.");
				 return new ResponseEntity(response, HttpStatus.OK);
			}
			
				for(StudentMarksBean bean:studentMarksDetails){
					passFailLogger.info("applyGraceforValidityEnd (sapid, totalApplicableGrace) (" + sapid + ","
							+ totalApplicableGrace + ")");
					if(totalApplicableGrace>0){
						ArrayList<PassFailExamBean> passFailStudentList = null;
						int grace = 50 - (Integer.parseInt(bean.getTotalMarks()));
						int writtenScore = Integer.parseInt(bean.getWritenscore()) + grace;
						int total = Integer.parseInt(bean.getTotalMarks()) + grace;
						
						passFailLogger.info("applyGraceforValidityEnd (grace, writtenScore, total) (" + grace + ","
								+ writtenScore + "," + total + ")");
						
						bean.setWritenscore(""+writtenScore);
						bean.setTotal(""+total);
						bean.setGracemarks(""+grace);
						bean.setRemarks("End of Program validity grace given");
						dao.updateStudentMarks(bean);
						//PassFailBean pb = convertBean(bean);
//						if("ACBM".equalsIgnoreCase(student.getProgram())){
						boolean isBajaj = "Bajaj".equalsIgnoreCase(student.getConsumerType());
						boolean isDBMJul2014 = "Jul2014".equalsIgnoreCase(student.getPrgmStructApplicable()) && "DBM".equalsIgnoreCase(student.getProgram());
						if(isBajaj && !isDBMJul2014){
							//For BAJAJ(prev. ACBM) pull all subjects for that sapid, sem (not subject)
							//HashMap<String, ArrayList> bajajKeysMap = pdao.getPendingRecordsForPassFailForBajajForASem(pb);
							HashMap<String, ArrayList> bajajKeysMap = pdao.getPendingRecordsForPassFailForBajajForASem(bean);
							passFailStudentList = pdao.processBajaj(bajajKeysMap);
					
						}else{
							HashMap<String, ArrayList> keysMap = pdao.getMarksRecordsForStudentForASubject(bean);
							
							passFailStudentList = pdao.processNew(keysMap);
							
						}
						
						for(PassFailExamBean b : passFailStudentList){
							b.setRemarks("End of Program validity grace given");
							b.setResultProcessedMonth(resultProcessedMonth);
							b.setResultProcessedYear(resultProcessedYear);
						}
						
						//Update in DB
						HashMap<String, String> keysBySAPID = pdao.getKeysBySAPID();
						pdao.upsertPassFailRecordsBySAPID(passFailStudentList, keysBySAPID);
						asgService.updateQuickAssgTableOnPassfailProcess(passFailStudentList);
						totalApplicableGrace = totalApplicableGrace - grace;
				}
			}
		response.put("Status", "Success"); 
		return new ResponseEntity(response, HttpStatus.OK);
			
		}catch(Exception e){
			passFailLogger.error("applyGraceforValidityEnd : Error : " + e.getMessage());
			 response.put("Status", "Fail");
			 return new ResponseEntity(response, HttpStatus.OK);
		}
	
		
	}

	
	public List<PassFailExamBean> getStudentAssignmentRemarks(List<PassFailExamBean> marksList,PassFailDAO dao){
		List<PassFailExamBean> studentMarksList = new ArrayList<PassFailExamBean>(); //student list who have assignment score as zero
		for(PassFailExamBean b : marksList){
			if("0".equalsIgnoreCase(b.getAssignmentscore())){
				studentMarksList.add(b);
			}
		} 
		marksList.removeAll(studentMarksList);
		for(PassFailExamBean b :studentMarksList){
			AssignmentFileBean assignmentRemarks = dao.getAssigmentRemarksForSingleStudentYearMonth(b.getSapid(),b.getAssignmentMonth(),b.getAssignmentYear(),b.getSubject());
			if(!StringUtils.isBlank(assignmentRemarks.getReason())){
				b.setAssignmentRemarks(assignmentRemarks.getReason());
			}
			if(!StringUtils.isBlank(assignmentRemarks.getFinalReason())){
				b.setAssignmentRemarks(assignmentRemarks.getFinalReason());
			}
			
		}
		marksList.addAll(studentMarksList);
		return marksList;
	}


//*/
	  //ProcessPassFailBestTeeForAllStudents whos validity ends in Dec18 :Start
/*	  
		@RequestMapping(value = "/processPassFailForFailedStudentsValidityEndsInDec18Form", method =  RequestMethod.GET)
		public ModelAndView processPassFailForFailedStudentsValidityEndsInDec18Form(HttpServletRequest request, HttpServletResponse respnse, Model m) {
			
			ModelAndView modelnView = new ModelAndView("processPassFailForFailedStudentsValidityEndsInDec18Form");
			PassFailDAO dao = (PassFailDAO)act.getBean("passFailDAO");
			int pendingRecordsBySAPid = dao.getPendingRecordsCountBySAPid(searchBean);
			int pendingRecordsByGRNO = dao.getPendingRecordsCountByGRNO();
			
			int projectCountOnline = 0;
			int projectCountOffline = 0;
			int absentCount = 0;
			int nvRiaCount = 0;
			int ansCount = 0;
			int offlineCount = 0;
			int onlineCount = 0;
			int writtenScoreOnlineCount=0;
			int writtenScoreOfflineCount=0;
			int assignmentScoreOnlineCount=0;
			int assignemntScoreOfflineCount=0;
			//PassFailTriggerDashboard//
			if(pendingRecordsBySAPid>0 || pendingRecordsByGRNO>0){
				//projectCount = dao.getPendingRecordsForProject();
				List<StudentMarksBean> studentProjectList = dao.getPendingRecordsForOnlineOfflineProject(searchBean);
				if(studentProjectList.size()>0){
					for(StudentMarksBean e:studentProjectList){
						StudentBean sb = new StudentBean();
						sb.setProgram(e.getProgram());
						sb.setPrgmStructApplicable(e.getProgramStructApplicable());
						e.setExamMode(sb.getExamMode());
						if("Online".equalsIgnoreCase(e.getExamMode())){
							projectCountOnline=projectCountOnline+1;
						}
						if("Offline".equalsIgnoreCase(e.getExamMode())){
							projectCountOffline=projectCountOffline+1;
						}
					}
				}
				List<StudentMarksBean> studentWrittenList = dao.getPendingRecordsForOnlineOfflineWritten(searchBean);
				if(studentWrittenList.size()>0){
					for(StudentMarksBean e:studentWrittenList){
						StudentBean sb = new StudentBean();
						sb.setProgram(e.getProgram());
						sb.setPrgmStructApplicable(e.getProgramStructApplicable());
						e.setExamMode(sb.getExamMode());
						if("Online".equalsIgnoreCase(e.getExamMode())){
							writtenScoreOnlineCount=writtenScoreOnlineCount+1;
						}
						if("Offline".equalsIgnoreCase(e.getExamMode())){
							writtenScoreOfflineCount=writtenScoreOfflineCount+1;
						}
					}
				}
				
				
				List<StudentMarksBean> studentAssignmentList = dao.getPendingRecordsForOnlineOfflineAssignment(searchBean);
				if(studentAssignmentList.size()>0){
					for(StudentMarksBean e:studentAssignmentList){
						StudentBean sb = new StudentBean();
						sb.setProgram(e.getProgram());
						sb.setPrgmStructApplicable(e.getProgramStructApplicable());
						e.setExamMode(sb.getExamMode());
						if("Online".equalsIgnoreCase(e.getExamMode())){
							assignmentScoreOnlineCount=assignmentScoreOnlineCount+1;
						}
						if("Offline".equalsIgnoreCase(e.getExamMode())){
							assignemntScoreOfflineCount=assignemntScoreOfflineCount+1;
						}
					}
				}
				 absentCount = dao.getPendingRecordsForAbsent(searchBean);
				 nvRiaCount = dao.getPendingRecordsForNVRIA(searchBean);
				 ansCount = dao.getPendingRecordsForANS(searchBean);
				
				List<StudentMarksBean> studentList = dao.getPendingRecordsForOnlineOffline(searchBean);
				if(studentList.size()>0){
					for(StudentMarksBean e:studentList){
						StudentBean sb = new StudentBean();
						sb.setProgram(e.getProgram());
						sb.setPrgmStructApplicable(e.getProgramStructApplicable());
						e.setExamMode(sb.getExamMode());
						if("Online".equalsIgnoreCase(e.getExamMode())){
							onlineCount=onlineCount+1;
						}
						if("Offline".equalsIgnoreCase(e.getExamMode())){
							offlineCount=offlineCount+1;
						}
					}
				}
			}
			//PassFailTriggerDashboard//
			StudentMarksBean marks = new StudentMarksBean();
			m.addAttribute("studentMarks",marks);
			m.addAttribute("programList", getProgramList());
			m.addAttribute("yearList", yearList);
			m.addAttribute("subjectList", getSubjectList());
			//m.addAttribute("projectCount", projectCount);
			m.addAttribute("absentCount", absentCount);
			m.addAttribute("nvRiaCount", nvRiaCount);
			m.addAttribute("ansCount", ansCount);
			m.addAttribute("onlineCount", onlineCount);
			m.addAttribute("offlineCount", offlineCount);
			m.addAttribute("projectCountOnline", projectCountOnline);
			m.addAttribute("projectCountOffline", projectCountOffline);
			m.addAttribute("writtenScoreOnlineCount", writtenScoreOnlineCount);
			m.addAttribute("writtenScoreOfflineCount", writtenScoreOfflineCount);
			m.addAttribute("assignmentScoreOnlineCount", assignmentScoreOnlineCount);
			m.addAttribute("assignemntScoreOfflineCount", assignemntScoreOfflineCount);
			request.getSession().setAttribute("studentMarks", marks);
			request.getSession().setAttribute("searchBean",searchBean);
			modelnView.addObject("pendingRecordsCount", pendingRecordsBySAPid+pendingRecordsByGRNO+"");
			return modelnView;
		}

		@RequestMapping(value = "/processPassFailForFailedStudentsValidityEndsInDec18", method =  RequestMethod.POST)
		public ModelAndView processPassFailForFailedStudentsValidityEndsInDec18(HttpServletRequest request, HttpServletResponse respnse, Model m) {
			logger.info("Add New Company Page");
			ModelAndView modelnView = new ModelAndView("processPassFail");
			PassFailDAO dao = (PassFailDAO)act.getBean("passFailDAO");
			StudentBean searchBean = (StudentBean)request.getSession().getAttribute("searchBean");

			try{
				processRecordsBySAPID(searchBean);
				processRecordsByGRNO();
				request.setAttribute("success","true");
				request.setAttribute("successMessage","Pass/fail processing completed successfully. Please check pending records if any.");
				//dao.updatePassFailResultProcessedYearMonth(searchBean);
			}catch(Exception e){
				
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Error in processing pass fail logic.");
			}finally{
				int pendingRecordsBySAPid = dao.getPendingRecordsCountBySAPid(searchBean);
				int pendingRecordsByGRNO = dao.getPendingRecordsCountByGRNO();
				modelnView.addObject("pendingRecordsCount", pendingRecordsBySAPid+pendingRecordsByGRNO+"");			
			}
			return modelnView;
		}*/
		//ProcessPassFailBestTeeForAllStudents whos validity ends in Dec18 :End
		
	@RequestMapping(value = "/admin/updateWritenScoreMarksForBS", method = RequestMethod.POST, consumes = "application/json", produces = "application/json") // only for jun2019 Business Statistics requested by Madhavi ma'am
	public ResponseEntity<HashMap<String, String>> updateWritenScoreMarksForBS(HttpServletRequest request){
		HashMap<String, String> response = new  HashMap<String, String>();
		try{
			StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			//PassFailDAO pdao = (PassFailDAO)act.getBean("passFailDAO");
			List<StudentMarksBean> studentsMarksList = dao.getListOfStudentApplicableforMax8Grace();
			
				for(StudentMarksBean bean:studentsMarksList){
					if((48 - Integer.parseInt(bean.getTotal()))<8) { // added extra check to be sure
						int newWrittenScor=Integer.parseInt(bean.getWritenscore())+(48 - Integer.parseInt(bean.getTotal())); 
						String newWrittenScore = ""+newWrittenScor;
						bean.setWritenscore(newWrittenScore);// passfail writenscore setting in marks writenscore
					}
				
				
			}
				dao.batchUpdate(studentsMarksList, "written");		
		response.put("Status", "Success"); 
		return new ResponseEntity(response, HttpStatus.OK);
			
		}catch(Exception e){
			
			 response.put("Status", "Fail");
			 return new ResponseEntity(response, HttpStatus.OK);
		}
	
		
	}
	
			@GetMapping("/admin/projectRecordOnline")
			public ModelAndView downloadPassFailReport(HttpServletRequest request,HttpServletResponse response){
				StudentExamBean searchBean = (StudentExamBean)request.getSession().getAttribute("searchBean");
				request.getSession().setAttribute("reportType", "ProjectRecordOnline");
				List<StudentMarksBean> ProjectRecordOnline = passFailService.getstudentProjectList(searchBean,"Online");
				return new ModelAndView("downloadReportForPassFail", "ProjectRecordOnline", ProjectRecordOnline);
			}
			
			@GetMapping("/admin/projectRecordOffline")
			public ModelAndView projectRecordOffline(HttpServletRequest request,HttpServletResponse response){
				StudentExamBean searchBean = (StudentExamBean)request.getSession().getAttribute("searchBean");
				request.getSession().setAttribute("reportType", "ProjectRecordOffline");
				List<StudentMarksBean> ProjectRecordOffline = passFailService.getstudentProjectList(searchBean,"Offline");
				return new ModelAndView("downloadReportForPassFail", "ProjectRecordOffline", ProjectRecordOffline);
			}
			
			
			@GetMapping("/admin/absentRecord")
			public ModelAndView absentRecord(HttpServletRequest request,HttpServletResponse response){
				StudentExamBean searchBean = (StudentExamBean)request.getSession().getAttribute("searchBean");
				request.getSession().setAttribute("reportType", "AbsentStudentRecord");
				List<StudentMarksBean> AbsentStudentRecord = passFailService.getAbsentStundentRecord(searchBean);
				return new ModelAndView("downloadReportForPassFail", "AbsentStudentRecord", AbsentStudentRecord);
			}
			
			
			@GetMapping("/admin/nvriaReportDownload")
			public ModelAndView nvriaReportDownload(HttpServletRequest request,HttpServletResponse response){
				StudentExamBean searchBean = (StudentExamBean)request.getSession().getAttribute("searchBean");
				request.getSession().setAttribute("reportType", "NVRIA");
				List<StudentMarksBean> NVRIA=passFailService.getPendingCountForNVRIA(searchBean);
				return new ModelAndView("downloadReportForPassFail", "NVRIA", NVRIA);
			}
			
			@GetMapping("/admin/ansReportDownload")
			public ModelAndView ansReportDownload(HttpServletRequest request,HttpServletResponse response){
				StudentExamBean searchBean = (StudentExamBean)request.getSession().getAttribute("searchBean");
				request.getSession().setAttribute("reportType", "ANSRecords");
				List<StudentMarksBean> ANSRecords=passFailService.getPendingListForANS(searchBean);
				return new ModelAndView("downloadReportForPassFail", "ANSRecords", ANSRecords);
			}
			
			@GetMapping("/admin/assignmentSubmitOnline")
			public ModelAndView assignmentSubmitOnline(HttpServletRequest request,HttpServletResponse response){
				StudentExamBean searchBean = (StudentExamBean)request.getSession().getAttribute("searchBean");
				request.getSession().setAttribute("reportType", "AssignmentSubmittedRecordsOnline");
				List<StudentMarksBean> AssignmentSubmittedRecordsOnline = passFailService.getAssignmentSubmittedRecords(searchBean,"Online");
				return new ModelAndView("downloadReportForPassFail", "AssignmentSubmittedRecordsOnline", AssignmentSubmittedRecordsOnline);
			}
			
			@GetMapping("/admin/assignmentSubmitOffline")
			public ModelAndView assignmentSubmitOffline(HttpServletRequest request,HttpServletResponse response){
				StudentExamBean searchBean = (StudentExamBean)request.getSession().getAttribute("searchBean");
				request.getSession().setAttribute("reportType", "AssignmentSubmittedRecordsOffline");
				List<StudentMarksBean> AssignmentSubmittedRecordsOffline = passFailService.getAssignmentSubmittedRecords(searchBean,"Offline");
				return new ModelAndView("downloadReportForPassFail", "AssignmentSubmittedRecordsOffline", AssignmentSubmittedRecordsOffline);
			}
			
			@GetMapping("/admin/writtenScoreRecordsOnline")
			public ModelAndView writtenScoreOnline(String id,HttpServletRequest request,HttpServletResponse response){
				StudentExamBean searchBean = (StudentExamBean)request.getSession().getAttribute("searchBean");
				request.getSession().setAttribute("reportType", "WrittenScoreRecordsOnline");
				List<StudentMarksBean> WrittenScoreRecordsOnline = passFailService.getWrittenScoreRecords(searchBean,"Online");
				return new ModelAndView("downloadReportForPassFail", "WrittenScoreRecordsOnline", WrittenScoreRecordsOnline);
			}
			
			@GetMapping("/admin/writtenScoreRecordsOffline")
			public ModelAndView writtenScoreOffline(HttpServletRequest request,HttpServletResponse response){
				StudentExamBean searchBean = (StudentExamBean)request.getSession().getAttribute("searchBean");
				request.getSession().setAttribute("reportType", "WrittenScoreRecordsOffline");
				List<StudentMarksBean> WrittenScoreRecordsOffline = passFailService.getWrittenScoreRecords(searchBean,"Offline");
				return new ModelAndView("downloadReportForPassFail", "WrittenScoreRecordsOffline", WrittenScoreRecordsOffline);
			}
			
			@GetMapping("/admin/onlineDownloadPassFailReport")
			public ModelAndView onlineDownloadPassFailReport(HttpServletRequest request,HttpServletResponse response){
				StudentExamBean searchBean = (StudentExamBean)request.getSession().getAttribute("searchBean");
				request.getSession().setAttribute("reportType", "Online");
				List<StudentMarksBean> Online = passFailService.getRecordsForOnlineOffline(searchBean,"Online");
				return new ModelAndView("downloadReportForPassFail", "Online", Online);
			}
			
			@GetMapping("/admin/offlineDownloadPassFailReport")
			public ModelAndView offlineDownloadPassFailReport(HttpServletRequest request,HttpServletResponse response){
				StudentExamBean searchBean = (StudentExamBean)request.getSession().getAttribute("searchBean");
				request.getSession().setAttribute("reportType", "Offline");
				List<StudentMarksBean> Offline = passFailService.getRecordsForOnlineOffline(searchBean,"Offline");
				return new ModelAndView("downloadReportForPassFail", "Offline", Offline);
			}
			
			@GetMapping("/admin/projectAbsent")
			public ModelAndView projectAbsentRecord(HttpServletRequest request, HttpServletResponse response) {
				StudentExamBean searchBean = (StudentExamBean) request.getSession().getAttribute("searchBean");
				request.getSession().setAttribute("reportType", "projectAbsent");
				List<StudentMarksBean> projectAbsent = passFailService.getStudentProjectAbsentList(searchBean);
				return new ModelAndView("downloadReportForPassFail","projectAbsent",projectAbsent);
			}
			
			@GetMapping("/admin/projectNotBookedStudentExcelReport")
			public ModelAndView projeccNotBookedStudentExcelReport(HttpServletRequest request,HttpServletResponse response) {
				StudentExamBean searchBean = (StudentExamBean) request.getSession().getAttribute("searchBean");
				request.getSession().setAttribute("reportType", "projectNotBookedCount");
				List<StudentMarksBean> projectAbsent = passFailService.getStudentNotBookedStudent(searchBean);
				return new ModelAndView("downloadReportForPassFail","projectNotBookedCount",projectAbsent);
			}


}

