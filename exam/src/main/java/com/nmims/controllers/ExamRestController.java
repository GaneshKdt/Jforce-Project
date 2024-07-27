package com.nmims.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.base.Throwables;
import com.nmims.beans.BatchExamBean;
import com.nmims.beans.ExamScheduleinfoBean;
import com.nmims.beans.ExamsAssessmentsBean;

import com.nmims.beans.FailedRegistrationBean;
import com.nmims.beans.FailedRegistrationResponseBean;
import com.nmims.beans.FailedregistrationExcelBean;
import com.nmims.beans.MbaWxExamDashboardMettlResponseBean;
import com.nmims.beans.MettlRegisterCandidateBean;
import com.nmims.beans.MettlRegisterCandidateBeanMBAWX;
import com.nmims.beans.MettlResponseBeanWebHookMbaWx;
import com.nmims.beans.MettlStudentTestInfo;
import com.nmims.beans.ResponseBean;
import com.nmims.beans.ResponseListBean;

import com.nmims.beans.FailedSubjectCountCriteriaBean;

import com.nmims.beans.StudentExamBean;
import com.nmims.daos.ExamsAssessmentsDAO;
import com.nmims.daos.MBAWXTeeDAO;
import com.nmims.factory.MettlAssessmentsFactory;
import com.nmims.helpers.DateHelper;
import com.nmims.helpers.ExcelHelper;
import com.nmims.interfaces.MettlAssessments;
import com.nmims.services.DemoExamServices;
import com.nmims.services.EmbaPassFailService;

import com.nmims.services.MettlTeeMarksService;

import ch.qos.logback.core.joran.action.NewRuleAction;

import com.nmims.services.FailedSubjectCountCriteriaService;

import com.nmims.services.MBAWXTeeService;

import com.google.gson.Gson;



@RestController
@RequestMapping("m")
public class ExamRestController {
	
	@Autowired(required=false)
	ApplicationContext act;
	
	@Autowired
	EmbaPassFailService epfService;
	

	@Autowired
	MettlTeeMarksService mettlTeeMarksService;
	
	@Autowired
	MettlAssessmentsFactory mettlAssessmentsFactory;
	
	@Autowired
	ExamsAssessmentsDAO examsAssessmentsDAO;
	
	@Autowired
	DemoExamServices demoExamServices; 
	
	@Autowired
	MBAWXTeeService mbaWXTeeService;

	@Autowired
	MBAWXTeeDAO mbaWXTeeDAO;
	
	private static final String webhookModifier = "MettlWebHook";
	
	private static final Logger logger = LoggerFactory.getLogger("examRegisterPG");
	
	private static final Logger examLogger = LoggerFactory.getLogger(ExamRestController.class);

	@Autowired 
	@Qualifier("failedSubjectCountCriteria")
	FailedSubjectCountCriteriaService failedSubjectCountCriteriaService;

	
	@PostMapping(path = "/getPendingTeeAssessmentsBySapid")
	public  ResponseEntity<List<ExamsAssessmentsBean>> getPendingTeeAssessmentsBySapid(@RequestBody StudentExamBean student) {
		HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json"); 
        ExamsAssessmentsDAO examsAssessmentsDAO = (ExamsAssessmentsDAO)act.getBean("examsAssessmentsDAO");
        List<ExamsAssessmentsBean> teePendingAssessmentsList = new ArrayList<ExamsAssessmentsBean>();  
        List<ExamsAssessmentsBean> teePendingAssessmentsListFromScheduleInfo = new ArrayList<ExamsAssessmentsBean>();
        List<ExamsAssessmentsBean> teePendingAssessmentsListCombined = new ArrayList<ExamsAssessmentsBean>();
        List<ExamsAssessmentsBean> newTeeList = new ArrayList<ExamsAssessmentsBean>();    
        try {
        	teePendingAssessmentsListFromScheduleInfo = examsAssessmentsDAO.getPendingTeeAssessmentsFromScheduleInfo(student.getSapid());
        	teePendingAssessmentsList = examsAssessmentsDAO.getPendingTeeAssessmentsBySapid(student.getSapid());
        	teePendingAssessmentsListCombined.addAll(teePendingAssessmentsList);
        	teePendingAssessmentsListCombined.addAll(teePendingAssessmentsListFromScheduleInfo);
        	newTeeList=getListWithoutDuplicate(teePendingAssessmentsListCombined);
        }catch(Exception e) {
        	
        }
		return new ResponseEntity<List<ExamsAssessmentsBean>>(newTeeList, headers,  HttpStatus.OK);
	}
	
	
	@PostMapping(path = "/getFinishedTeeAssessmentsBySapid")
	public  ResponseEntity<List<ExamsAssessmentsBean>> getFinishedTeeAssessmentsBySapid(@RequestBody StudentExamBean student) {
		HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json"); 
        ExamsAssessmentsDAO examsAssessmentsDAO = (ExamsAssessmentsDAO)act.getBean("examsAssessmentsDAO");
        List<ExamsAssessmentsBean> teeFinishedAssessmentsList = new ArrayList<ExamsAssessmentsBean>();  
        List<ExamsAssessmentsBean> teeFinishedAssessmentsListFromScheduleInfo = new ArrayList<ExamsAssessmentsBean>(); 
        List<ExamsAssessmentsBean> teeFinishedAssessmentsListCombined = new ArrayList<ExamsAssessmentsBean>(); 
        List<ExamsAssessmentsBean> newTeeList = new ArrayList<ExamsAssessmentsBean>(); 
        try {
        	teeFinishedAssessmentsListFromScheduleInfo = examsAssessmentsDAO.getFinishedTeeAssessmentsBySapidFromScheduleInfo(student.getSapid());
        	teeFinishedAssessmentsList = examsAssessmentsDAO.getFinishedTeeAssessmentsBySapid(student.getSapid());
        	teeFinishedAssessmentsListCombined.addAll(teeFinishedAssessmentsList);
        	teeFinishedAssessmentsListCombined.addAll(teeFinishedAssessmentsListFromScheduleInfo);
        	newTeeList=getListWithoutDuplicate(teeFinishedAssessmentsListCombined);
        }catch(Exception e) {
        	
        }
		return new ResponseEntity<List<ExamsAssessmentsBean>>(newTeeList, headers,  HttpStatus.OK);
	}
	
	@PostMapping(path = "/getTeeAssessmentsByExamScheduleId",  consumes = "application/json", produces = "application/json")
	public  ResponseEntity<List<ExamsAssessmentsBean>> getTeeAssessmentsByExamScheduleId(@RequestParam("id") String id) {
		HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json"); 
        ExamsAssessmentsDAO examsAssessmentsDAO = (ExamsAssessmentsDAO)act.getBean("examsAssessmentsDAO");
        List<ExamsAssessmentsBean> teeAssessmentList = new ArrayList<ExamsAssessmentsBean>();      
        List<ExamsAssessmentsBean> teeAssessmentListFromScheduleInfo = new ArrayList<ExamsAssessmentsBean>();     
        List<ExamsAssessmentsBean> teeAssessmentListCombined = new ArrayList<ExamsAssessmentsBean>();
        List<ExamsAssessmentsBean> newTeeList = new ArrayList<ExamsAssessmentsBean>();
        try {
        	teeAssessmentListFromScheduleInfo = examsAssessmentsDAO.getTeeAssessmentsByExamScheduleIdFromScheduleInfo(id);
        	teeAssessmentList = examsAssessmentsDAO.getTeeAssessmentsByExamScheduleId(id);
        	teeAssessmentListCombined.addAll(teeAssessmentList);
        	teeAssessmentListCombined.addAll(teeAssessmentListFromScheduleInfo);
        	newTeeList=getListWithoutDuplicate(teeAssessmentListCombined);
        }catch(Exception e) {
        	
        }
		return new ResponseEntity<List<ExamsAssessmentsBean>>(newTeeList, headers,  HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/getTeeAssessmentsBySapid",  method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public  ResponseEntity<List<ExamsAssessmentsBean>> getTeeAssessmentsBySapid(@RequestBody StudentExamBean student) {
		HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json"); 
        ExamsAssessmentsDAO examsAssessmentsDAO = (ExamsAssessmentsDAO)act.getBean("examsAssessmentsDAO");
        List<ExamsAssessmentsBean> teeAssessmentsList = new ArrayList<ExamsAssessmentsBean>();      
        List<ExamsAssessmentsBean> teeAssessmentsListFromScheduleInfo = new ArrayList<ExamsAssessmentsBean>();
        List<ExamsAssessmentsBean> teeAssessmentsListCombined = new ArrayList<ExamsAssessmentsBean>();
        List<ExamsAssessmentsBean> newTeeList = new ArrayList<ExamsAssessmentsBean>();
        try {
        teeAssessmentsListFromScheduleInfo = examsAssessmentsDAO.getTeeAssessmentsBySapidFromScheduleInfo(student.getSapid());
        teeAssessmentsList = examsAssessmentsDAO.getTeeAssessmentsBySapid(student.getSapid());
    	teeAssessmentsListCombined.addAll(teeAssessmentsList);
    	teeAssessmentsListCombined.addAll(teeAssessmentsListFromScheduleInfo);
    	newTeeList=getListWithoutDuplicate(teeAssessmentsListCombined);
        }catch(Exception e) {
        	
        }
		return new ResponseEntity<List<ExamsAssessmentsBean>>(newTeeList, headers,  HttpStatus.OK);
	}
	
	@RequestMapping(value = "/getUpcomingTeeAssessmentsBySapid",  method = RequestMethod.POST)
	public  ResponseEntity<List<ExamsAssessmentsBean>> getUpcomingTeeAssessmentsBySapid(@RequestBody StudentExamBean student) {
		HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json"); 
        ExamsAssessmentsDAO examsAssessmentsDAO = (ExamsAssessmentsDAO)act.getBean("examsAssessmentsDAO");
        List<ExamsAssessmentsBean> teeUpcomingAssessmentsList = new ArrayList<ExamsAssessmentsBean>();  
        List<ExamsAssessmentsBean> teeUpcomingAssessmentsListFromScheduleInfo = new ArrayList<ExamsAssessmentsBean>();  
        List<ExamsAssessmentsBean> teeUpcomingAssessmentsListCombined = new ArrayList<ExamsAssessmentsBean>();  
        List<ExamsAssessmentsBean> newTeeList = new ArrayList<ExamsAssessmentsBean>();
        try {
        	teeUpcomingAssessmentsListFromScheduleInfo = examsAssessmentsDAO.getUpcomingTeeAssessmentsBySapidFromScheduleInfo(student.getSapid());
        	teeUpcomingAssessmentsList = examsAssessmentsDAO.getUpcomingTeeAssessmentsBySapid(student.getSapid());
        	teeUpcomingAssessmentsListCombined.addAll(teeUpcomingAssessmentsList);
        	teeUpcomingAssessmentsListCombined.addAll(teeUpcomingAssessmentsListFromScheduleInfo);
        	newTeeList=getListWithoutDuplicate(teeUpcomingAssessmentsListCombined);
        }catch(Exception e) {
        	
        }
		return new ResponseEntity<List<ExamsAssessmentsBean>>(newTeeList, headers,  HttpStatus.OK);
	}
	
	@PostMapping(value = "/getBatchList")
	public  ResponseEntity<List<BatchExamBean>> getBatchList(@RequestBody ExamsAssessmentsBean bean) {
		HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json"); 
        try {
        	List<BatchExamBean> list = epfService.getBatchesList(bean.getProgramType());
        	return new ResponseEntity<List<BatchExamBean>>(list, headers,  HttpStatus.OK);
        }catch(Exception e) {
        	List<BatchExamBean> list = new ArrayList<BatchExamBean>();
        	return new ResponseEntity<List<BatchExamBean>>(list, headers,  HttpStatus.INTERNAL_SERVER_ERROR);
        }
		
		
	}
	

	@PostMapping(value="/uploadRegistrationFailedMettl")
	public ResponseEntity<FailedRegistrationResponseBean> uploadRegistrationFailedMettl(HttpServletRequest request,FailedRegistrationBean bean)
	{
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		FailedRegistrationResponseBean response = new FailedRegistrationResponseBean();
		try
		{
			String result="";
			ExcelHelper excelHelper = new ExcelHelper();
			FailedregistrationExcelBean failedregistrationExcelBean = excelHelper.readFailedRegistrationExcel(bean);
			if(failedregistrationExcelBean!=null && !StringUtils.isEmpty(failedregistrationExcelBean.getSubject()))
			{
				boolean isReExam = false;
				ExamsAssessmentsBean examBean = failedregistrationExcelBean.getExamBean();
				examBean.setCreatedBy(bean.getCreatedBy());
				examBean.setLastModifiedBy(bean.getLastModifiedBy());
				String subjectName = failedregistrationExcelBean.getSubject();
				String endTime = failedregistrationExcelBean.getEndTime();
				ArrayList<MettlRegisterCandidateBean> userList = failedregistrationExcelBean.getUserList(); 

				logger.info("user list excel:"+userList.toString());
				logger.info("excel subjectName:"+subjectName);
				logger.info("excel endTime:"+endTime);
				logger.info("excel exam Bean:"+examBean.toString());
				
				  if(examBean.getMax_score().equalsIgnoreCase("100")) 
				  { 
					  isReExam=true; 
				  }
				  MettlAssessments assessments = mettlAssessmentsFactory.getProductType(bean.getProgramType());
				  result=assessments.registerCandidates(request, examBean, subjectName, endTime, isReExam, true, userList); 
				  logger.info("Final Result is:"+result);
				  if(!"success".equalsIgnoreCase(result)) 
				  {
					  response.setError(result);
					  if(result.contains("Download Failed Registrations Excel")) 
					  {
						  response.setDownloadExcel("true"); 
					  } 
				  }
				  if(!StringUtils.isEmpty(String.valueOf(request.getAttribute("count"))))
				  {
					  response.setCount(String.valueOf(request.getAttribute("count")));
				  }
			}
			else
			{
				logger.info("check excel file data");
				response.setError("check excel file data");
			}
			return new ResponseEntity<>(response,headers,HttpStatus.OK);
		}
		catch(Exception e)
		{
			logger.error("Error is:"+e.getMessage());
			response.setError("Error is:"+e.getMessage());
			return new ResponseEntity<>(response,headers,HttpStatus.OK);
		}
	}

	@PostMapping(value="/failedSubjectCriteria")
	public ResponseEntity<FailedSubjectCountCriteriaBean> insertfailedSubjectCriteria(@RequestBody FailedSubjectCountCriteriaBean bean)
	{
		try
		{
			failedSubjectCountCriteriaService.insertFailedSubjectCountCriteria(bean);
			bean.setFlag("success");
			bean.setMessage("Successfully inserted records");
		}
		catch(Exception e)
		{
			bean.setFlag("error");
			bean.setMessage("Error while inserting records is:"+e.getMessage());
		}
		return new ResponseEntity<FailedSubjectCountCriteriaBean>(bean,HttpStatus.OK);
	}
	
	@PostMapping(value="/updateFailedCriteria")
	public ResponseEntity<FailedSubjectCountCriteriaBean> updatefailedSubjectCriteria(@RequestBody FailedSubjectCountCriteriaBean bean)
	{
		try
		{
			failedSubjectCountCriteriaService.updateFailedSubjectCountCriteria(bean);
			bean.setFlag("success");
			bean.setMessage("Successfully updated records");
		}
		catch(Exception e)
		{
			bean.setFlag("error");
			bean.setMessage("Error while updating records is:"+e.getMessage());
		}
		return new ResponseEntity<FailedSubjectCountCriteriaBean>(bean,HttpStatus.OK);

	}
	
	@RequestMapping(value="/checkTimeBoundStudentsAttemptStatus",method=RequestMethod.GET)
	public void checkTimeBoundStudentsAttemptStatus(HttpServletRequest request)
	{
		String todayDate=request.getParameter("todayDate");
		mettlTeeMarksService.checkAttemptStatus(todayDate);
	}
	
	public List<ExamsAssessmentsBean> getListWithoutDuplicate(List<ExamsAssessmentsBean> teePendingAssessmentsListCombined)
	{
		List<ExamsAssessmentsBean> newTeeList = new ArrayList<ExamsAssessmentsBean>();
		boolean isDuplicate=false;
		for(int i=0;i<=teePendingAssessmentsListCombined.size()-1;i++)
      	{
      		isDuplicate=false;
      		for(int j=i+1;j<=teePendingAssessmentsListCombined.size()-1;j++)
      		{
      			if(teePendingAssessmentsListCombined.get(i).equals(teePendingAssessmentsListCombined.get(j)))
      			{
      				isDuplicate=true;
      				break;
      			}
      		}
      		
      		if(isDuplicate)
      		{
      			continue;
      		}
      		
      		newTeeList.add(teePendingAssessmentsListCombined.get(i));
      	}
		return newTeeList;
		  
	}
	
	@RequestMapping(value="/admin/refreshRealTimeRegistrationFlag",method= {RequestMethod.GET,RequestMethod.POST})
	public void refreshRealTimeRegistrationFlag(@RequestParam("flag") String flag)
	{
		try {
		if(flag.equalsIgnoreCase("Active")) {
			examsAssessmentsDAO.setExtendedExamRegistrationLiveForRealTime(true);	
			logger.info("Real time registration flag refreshed:"+examsAssessmentsDAO.getIsExtendedExamRegistrationLiveForRealTime());
		}
		else {
			examsAssessmentsDAO.setExtendedExamRegistrationLiveForRealTime(false);
			logger.info("Real time registration flag refreshed:"+examsAssessmentsDAO.getIsExtendedExamRegistrationLiveForRealTime());
		}
		}
		catch(Exception e) {	
			logger.info("Exception is:"+e);
			}
	}
	
	@PostMapping(value="/getActiveBatchList")
	public ResponseEntity<List<BatchExamBean>> getActiveBatchList(@RequestBody ExamsAssessmentsBean bean){
		List<BatchExamBean> list = new ArrayList<BatchExamBean>();
		try {
			list=epfService.getActiveBatchList(bean.getProgramType(),bean.getAcadMonth(),bean.getAcadYear());
			return new ResponseEntity<List<BatchExamBean>>(list, HttpStatus.OK);
		}
		catch(Exception e) {
			//e.printStackTrace();
			examLogger.info("Exception is:"+Throwables.getStackTraceAsString(e));
			return new ResponseEntity<List<BatchExamBean>>(list, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	

	@GetMapping(path="/getMbaWxExamData")
	public ResponseEntity<ArrayList<MettlRegisterCandidateBeanMBAWX>> getMbaWxExamData(@RequestParam("programType") String programType,@RequestParam("examTime") String examTime,@RequestParam("examType") String examType, @RequestParam("sapid") String sapid) {
		ArrayList<MettlRegisterCandidateBeanMBAWX> programSpecificUserList = new ArrayList<MettlRegisterCandidateBeanMBAWX>();
		try {
			programSpecificUserList=mbaWXTeeService.getMbaWxExamData(programType,examTime,examType,sapid);
			examLogger.info("Program Specific User List:"+programSpecificUserList);
			return new ResponseEntity<ArrayList<MettlRegisterCandidateBeanMBAWX>>(programSpecificUserList ,HttpStatus.OK);
		}catch(Exception e) {
			examLogger.info("Exception in getMbaWxExamData is:"+e.getMessage());
			return new ResponseEntity<ArrayList<MettlRegisterCandidateBeanMBAWX>>(programSpecificUserList ,HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping(path="/getDemoExamLogsMbaWx")
	public ResponseEntity<ResponseBean> getDemoExamData(@RequestParam String sapid) {
		ResponseBean responseBean = demoExamServices.getDemoExamStatusForMbaWXStudent(sapid);
		return new ResponseEntity<ResponseBean>(responseBean ,HttpStatus.OK);
	}
	
	@PostMapping(path="/getJoinLinksMbaWx")
	public ResponseEntity<ResponseBean> getJoinLinksMbaWx(@RequestBody MettlRegisterCandidateBeanMBAWX inputBean) {
		ResponseBean responseBean = mbaWXTeeService.getMettlJoinLinkForSapid(inputBean);
		return new ResponseEntity<ResponseBean>(responseBean,HttpStatus.OK);
	}
	
	@PostMapping(path="/sendEmailLinkMbaWxDashboard")
	public ResponseEntity<ResponseBean> sendEmailLinkMbaWxDashboard(@RequestBody MettlRegisterCandidateBeanMBAWX inputBean) {
		ResponseBean responseBean = mbaWXTeeService.sendEmailJoinLinkMbaWx(inputBean);
		return new ResponseEntity<ResponseBean>(responseBean,HttpStatus.OK);
	}
	
	@PostMapping(path="/getExamStatusMbaWx")
	public ResponseEntity<ResponseBean> getExamStatusMbaWx(@RequestBody MettlRegisterCandidateBeanMBAWX inputBean) {
		ResponseBean responseBean = mbaWXTeeService.getExamStatusMbaWx(inputBean.getSapid(),inputBean.getSchedule_id());
		return new ResponseEntity<ResponseBean>(responseBean,HttpStatus.OK);
	}
	

	@PostMapping(path="/startMettlExamProdMbaWX",consumes = "application/json", produces = "application/json")
	public ResponseEntity<MettlResponseBeanWebHookMbaWx> startMettlExamProdMbaWX(@RequestBody MettlResponseBeanWebHookMbaWx mettlResponseBeanWebHookMbaWx){
		try {
			
			examLogger.info("Mettl Start Web Hook Call for :"+mettlResponseBeanWebHookMbaWx.toString());
			String startJsonResponse = new Gson().toJson(mettlResponseBeanWebHookMbaWx);
			mbaWXTeeDAO.updateExamStartStatusMbaWx(MettlResponseBeanWebHookMbaWx.getTestStartedOnMettlMbawx(), startJsonResponse, mettlResponseBeanWebHookMbaWx.getInvitation_key(), mettlResponseBeanWebHookMbaWx.getEmail(), webhookModifier);
			mettlResponseBeanWebHookMbaWx.setStatus("success");
			return new ResponseEntity<MettlResponseBeanWebHookMbaWx>(mettlResponseBeanWebHookMbaWx,HttpStatus.OK);
			
		}catch(Exception e) {
			examLogger.info("Exception in start mettl exam web hook for:"+mettlResponseBeanWebHookMbaWx.getEmail()+"-"+mettlResponseBeanWebHookMbaWx.getInvitation_key()+"-"+e);
			mettlResponseBeanWebHookMbaWx.setStatus("error");
			mettlResponseBeanWebHookMbaWx.setErrorMessage("Exception in start mettl exam web hook for:"+mettlResponseBeanWebHookMbaWx.getEmail()+"-"+mettlResponseBeanWebHookMbaWx.getInvitation_key()+"-"+e);
			return new ResponseEntity<MettlResponseBeanWebHookMbaWx>(mettlResponseBeanWebHookMbaWx,HttpStatus.INTERNAL_SERVER_ERROR);
		}		
	}
	
	@PostMapping(path="/endMettlExamProdMbaWX",consumes = "application/json", produces = "application/json")
	public ResponseEntity<MettlResponseBeanWebHookMbaWx> endMettlExamProdMbaWX(@RequestBody MettlResponseBeanWebHookMbaWx mettlResponseBeanWebHookMbaWx){
		try {
		
			examLogger.info("Mettl End Web Hook Call for :"+mettlResponseBeanWebHookMbaWx.toString());
			String endJsonResponse = new Gson().toJson(mettlResponseBeanWebHookMbaWx);
			mbaWXTeeDAO.updateExamEndStatusMbaWx(MettlResponseBeanWebHookMbaWx.getTestEndedOnMettlMbawx(), endJsonResponse, mettlResponseBeanWebHookMbaWx.getFinish_mode(), mettlResponseBeanWebHookMbaWx.getInvitation_key(), mettlResponseBeanWebHookMbaWx.getEmail(), webhookModifier);
			mettlResponseBeanWebHookMbaWx.setStatus("success");
			return new ResponseEntity<MettlResponseBeanWebHookMbaWx>(mettlResponseBeanWebHookMbaWx,HttpStatus.OK);
			
		}catch(Exception e) {
			examLogger.info("Exception in end mettl exam web hook for:"+mettlResponseBeanWebHookMbaWx.getEmail()+"-"+mettlResponseBeanWebHookMbaWx.getInvitation_key()+"-"+e);
			mettlResponseBeanWebHookMbaWx.setStatus("error");
			mettlResponseBeanWebHookMbaWx.setErrorMessage("Exception in end mettl exam web hook for:"+mettlResponseBeanWebHookMbaWx.getEmail()+"-"+mettlResponseBeanWebHookMbaWx.getInvitation_key()+"-"+e);
			return new ResponseEntity<MettlResponseBeanWebHookMbaWx>(mettlResponseBeanWebHookMbaWx,HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping(path="/resumeMettlExamProdMbaWX",consumes = "application/json", produces = "application/json")
	public ResponseEntity<MettlResponseBeanWebHookMbaWx> resumeMettlExamProdMbaWX(@RequestBody MettlResponseBeanWebHookMbaWx mettlResponseBeanWebHookMbaWx){
		try {

			examLogger.info("Mettl Resume Web Hook Call for :"+mettlResponseBeanWebHookMbaWx.toString());
			String resumeJsonResponse = new Gson().toJson(mettlResponseBeanWebHookMbaWx);
			mbaWXTeeDAO.updateExamResumeStatusMbaWx(resumeJsonResponse,  mettlResponseBeanWebHookMbaWx.getInvitation_key(), mettlResponseBeanWebHookMbaWx.getEmail(), webhookModifier);
			mettlResponseBeanWebHookMbaWx.setStatus("success");
			return new ResponseEntity<MettlResponseBeanWebHookMbaWx>(mettlResponseBeanWebHookMbaWx,HttpStatus.OK);
			
		}catch(Exception e) {
			examLogger.info("Exception in resume mettl exam web hook for:"+mettlResponseBeanWebHookMbaWx.getEmail()+"-"+mettlResponseBeanWebHookMbaWx.getInvitation_key()+"-"+e);
			mettlResponseBeanWebHookMbaWx.setStatus("error");
			mettlResponseBeanWebHookMbaWx.setErrorMessage("Exception in resume mettl exam web hook for:"+mettlResponseBeanWebHookMbaWx.getEmail()+"-"+mettlResponseBeanWebHookMbaWx.getInvitation_key()+"-"+e);
			return new ResponseEntity<MettlResponseBeanWebHookMbaWx>(mettlResponseBeanWebHookMbaWx,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	@PostMapping(path="/gradedMettlExamProdMbaWX",consumes = "application/json", produces = "application/json")
	public ResponseEntity<Map<String, Object>> gradedMettlExamProdMbaWX(@RequestBody Map<String, Object> request){
		try {
			
			examLogger.info("Mettl graded Web Hook Call for :"+request.toString());
			String invitationKey = String.valueOf(request.get("invitation_key"));
			String emailId= String.valueOf(request.get("email"));
			String gradedJsonResponse = new Gson().toJson(request);
			mbaWXTeeDAO.updateExamGradedStatusMbaWx(gradedJsonResponse,  invitationKey, emailId, webhookModifier);
			request.put("status", "success");
			return new ResponseEntity<Map<String, Object>>(request,HttpStatus.OK);
			
		}catch(Exception e) {
			examLogger.info("Exception in graded mettl exam web hook for:"+String.valueOf(request.get("email"))+"-"+String.valueOf(request.get("invitation_key"))+"-"+e);
			request.put("status", "error");
			request.put("errorMessage","Exception in graded mettl exam web hook for:"+String.valueOf(request.get("email"))+"-"+String.valueOf(request.get("invitation_key"))+"-"+e);
			return new ResponseEntity<Map<String, Object>>(request,HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping(path="/mbaWxExamDashboardMettl")
	public ResponseEntity<MbaWxExamDashboardMettlResponseBean> mbaWxExamDashboardMettl(@RequestParam("examType") String examType, @RequestParam("programType") String programType,@RequestParam("examDate") String examDate,@RequestParam("examTime") String examTime) {
		
		MbaWxExamDashboardMettlResponseBean responseBean = new MbaWxExamDashboardMettlResponseBean();
		examLogger.info("DashBoard status call for program:"+programType+",examTime:"+examTime+",examType:"+examType);
		try {
			ArrayList<MettlRegisterCandidateBeanMBAWX> notAttemptedUserList = new ArrayList<MettlRegisterCandidateBeanMBAWX>();
			ArrayList<Integer> consumerProgramStructureIdList = mettlTeeMarksService.getConsumerProgramStrucutreId(programType);
			ArrayList<MettlRegisterCandidateBeanMBAWX> userList = mbaWXTeeDAO.getStudentDataForDashboardMBAWX(examTime, examType);
			ArrayList<String> batchList =  mbaWXTeeDAO.getBatchDataForDashboardMBAWX(consumerProgramStructureIdList);
			ArrayList<BatchExamBean> timeboundList  = mbaWXTeeDAO.getTimeBoundDataForDashboardMBAWX();
			ArrayList<MettlRegisterCandidateBeanMBAWX> attemptedUserList = mbaWXTeeDAO.getAttemptedDataForDashboardMBAWX(examTime);
			ArrayList<MettlRegisterCandidateBeanMBAWX> programSpecificUserList = (ArrayList<MettlRegisterCandidateBeanMBAWX>)userList.stream().filter(user -> 
			
			timeboundList.stream().filter(timeboundId -> 
			batchList.stream().anyMatch(id -> id.equalsIgnoreCase(timeboundId.getBatchId()))).anyMatch(filteredTimeBoundId -> user.getTimebound_id().equalsIgnoreCase(Integer.toString(filteredTimeBoundId.getId())))).collect(Collectors.toList());
			
			if(examType.equalsIgnoreCase("100")) {
				notAttemptedUserList = programSpecificUserList;
			}
			else {
				notAttemptedUserList = (ArrayList<MettlRegisterCandidateBeanMBAWX>)programSpecificUserList.stream().filter(user -> 
				attemptedUserList.stream().noneMatch(attemptedUser -> attemptedUser.getSapid().equalsIgnoreCase(user.getSapid()) && attemptedUser.getTimebound_id().equalsIgnoreCase(user.getTimebound_id()))).collect(Collectors.toList());
			}
			
			String mettlStartedList = String.valueOf(notAttemptedUserList.stream().filter(user -> user.getMettlStatus()!=null && user.getMettlStatus().equalsIgnoreCase(MettlResponseBeanWebHookMbaWx.getTestStartedOnMettlMbawx())).count());
			String mettlCompletedList = String.valueOf(notAttemptedUserList.stream().filter(user -> user.getMettlStatus()!=null && user.getMettlStatus().equalsIgnoreCase(MettlResponseBeanWebHookMbaWx.getTestEndedOnMettlMbawx())).count());
			String noActionList = String.valueOf(notAttemptedUserList.stream().filter(user -> user.getMettlStatus()==null).count());
			String portalStartedList = String.valueOf(notAttemptedUserList.stream().filter(user -> user.getPortalStatus()!=null && user.getPortalStatus().equalsIgnoreCase(MettlResponseBeanWebHookMbaWx.getTestStartedOnPortalMbawx())).count());
			
			responseBean.setMettl_started(mettlStartedList);
			responseBean.setMettl_completed(mettlCompletedList);
			responseBean.setPortal_started(portalStartedList);
			responseBean.setNo_action(noActionList);
			responseBean.setProgramType(programType);
			responseBean.setExamDate(examDate);
			responseBean.setExamType(examType);
			responseBean.setExamTime(examTime);

			return new ResponseEntity<MbaWxExamDashboardMettlResponseBean>(responseBean,HttpStatus.OK);

		}catch(Exception e) {
			examLogger.info("Exception in mbaWxExamDashboardMettl for program:"+programType+",examDate:"+examDate+",examType:"+examType+"-"+e);
			return new ResponseEntity<MbaWxExamDashboardMettlResponseBean>(responseBean,HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping(path="/getExamTimeByExamDate")
	public ResponseEntity<ArrayList<String>> getExamTimeByExamDate(@RequestParam("examDate") String examDate) {
		ArrayList<String> examTimeList = new ArrayList<String>();
		try {
			examTimeList=mbaWXTeeDAO.getExamTimeForDashboardMBAWX(examDate);
			return new ResponseEntity<ArrayList<String>>(examTimeList,HttpStatus.OK);
		}catch(Exception e) {
			examLogger.info("Exception in getExamTimeByExamDate for examDate:"+examDate+" is:"+e);
			return new ResponseEntity<ArrayList<String>>(examTimeList,HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
