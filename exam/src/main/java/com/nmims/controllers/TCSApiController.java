package com.nmims.controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.FileBean;
import com.nmims.beans.Page;
import com.nmims.beans.ResponseBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.beans.TCSExamBookingDataBean;
import com.nmims.beans.TCSMarksBean;
import com.nmims.beans.TcsOnlineExamBean;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.daos.TCSApiDAO;
import com.nmims.helpers.TCSApis;
import com.nmims.services.MettlTeeMarksService;
import com.nmims.services.TCSApiService;



@Controller
public class TCSApiController extends BaseController{
	
	@Autowired
	TCSApis tcsHelper;
	
	@Autowired
	TCSApiService tcsApiService;
	
	@Autowired
	MettlTeeMarksService mettlTeeMarksService; 
	
	@Value("#{'${EXAM_MONTH_LIST}'.split(',')}")
	private List<String> EXAM_MONTH_LIST;

	@Value("#{'${CURRENT_YEAR_LIST}'.split(',')}")
	private List<String> yearList; 
	
	
	@RequestMapping(value = "/examResultsProcessingChecklistTCS")
	public ModelAndView examResultsProcessingChecklistTCS(HttpServletRequest request,
			HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
		return null;
		}
	    ModelAndView mav=new ModelAndView("examResultsProcessingChecklistTCS");
	    
		return mav;	
		
	}
	
	@RequestMapping(value = "/insertExamBookingDataFormTCS")
	public ModelAndView inserExamBookingDataForm(HttpServletRequest request,
			HttpServletResponse response,@ModelAttribute StudentMarksBean studentMarks) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
		return null;
		}
		
	    ModelAndView mav=new ModelAndView("tcs/insertExamBookingRecordsTCS");
	    mav.addObject("studentMarks", studentMarks);
	    mav.addObject("examYearList", yearList);
	    mav.addObject("examMonthList", EXAM_MONTH_LIST);
		
		return mav;	
		
	}
	@RequestMapping(value = "/uploadExamBookingData", method = {RequestMethod.POST})
	public @ResponseBody ResponseBean getExamBookingData(HttpServletRequest request,
			HttpServletResponse response,@RequestBody StudentMarksBean studentMarks ) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
		return null;
		}
	    HttpHeaders headers = new HttpHeaders();
//	    ModelAndView mav=new ModelAndView("tcs/insertExamBookingRecordsTCS");
//	    mav.addObject("studentMarks", studentMarks);
		headers.add("Content-Type", "application/json");
		ResponseBean responseBean = new ResponseBean();
		try {
			responseBean = tcsHelper.execute(studentMarks,"YES");
			if(responseBean.getStatus().equals("fail") && responseBean.getMessage().equals("") ) {
				responseBean.setMessage("Error in inserting records.");
			}
			
		} catch (Exception e) {
			
			responseBean.setCode(422);
			responseBean.setMessage("Error in inserting records.");
		}

		return responseBean;
		
	}
	
	@RequestMapping(value = "/updateExamBookingDataFormTCS")
	public ModelAndView updateExamBookingDataFormTCS(HttpServletRequest request,
			HttpServletResponse response,@ModelAttribute StudentMarksBean studentMarks) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
	    ModelAndView mav=new ModelAndView("tcs/updateExamBookingRecordsTCS");
	    mav.addObject("studentMarks", studentMarks);
	    mav.addObject("examYearList", yearList);
	    mav.addObject("examMonthList", EXAM_MONTH_LIST);
		return mav;	
		
	}
	
	@RequestMapping(value = "/displayTCSExamBookingData/{pageNo}", method = RequestMethod.POST)
	public @ResponseBody ResponseBean displayTCSExamBookingData(@RequestBody StudentMarksBean studentMarksBean,
									@PathVariable Integer pageNo,
									HttpServletRequest request,HttpServletResponse response
									) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ResponseBean responseBean = new ResponseBean();
		responseBean = tcsApiService.displayTCSExamBookingDataService(pageNo,studentMarksBean,getAuthorizedCodes(request)); 
		request.getSession().setAttribute("studentMarksBeanForExamBookingDownload", studentMarksBean);
			return responseBean;
	}
	
	@RequestMapping(value = "/runRescheduleReportScheduler", method = RequestMethod.POST, produces="application/json")
	public ResponseEntity<ResponseBean> runRescheduleReportScheduler(@RequestBody TcsOnlineExamBean tcsOnlineExamBean) {
		ResponseBean responseBean = new ResponseBean();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");

		String result = mettlTeeMarksService.runTeeRescheduleExambookingListSchedulerService(tcsOnlineExamBean.getExamDate());
		responseBean.setMessage(result);
		
		return new ResponseEntity<ResponseBean>(responseBean, headers, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/getExamCenterDropdown", method = RequestMethod.POST)
	public @ResponseBody ResponseBean getExamCenterDropdown(@RequestBody TcsOnlineExamBean tcsOnlineExamBean) {
		
		return tcsApiService.getExamCenterDropdownService(tcsOnlineExamBean);
	}
	
	@RequestMapping(value = "/getExamDateDropdown", method = RequestMethod.POST)
	public @ResponseBody ResponseBean getExamDateDropdown(@RequestBody TcsOnlineExamBean tcsOnlineExamBean) {
		
		return tcsApiService.getExamDateDropdownService(tcsOnlineExamBean);
	}
	
	@RequestMapping(value = "/getExamStartTimeDropdown", method = RequestMethod.POST)
	public @ResponseBody ResponseBean getExamStartTimeDropdown(@RequestBody TcsOnlineExamBean tcsOnlineExamBean) {
		
		return tcsApiService.getExamStartTimeDropdownService(tcsOnlineExamBean);
	}
	
	@RequestMapping(value = "/syncUpdatedExamBookingData", method = RequestMethod.POST)
	public @ResponseBody ResponseBean updateExamBookingSlotForSapid(@RequestBody TcsOnlineExamBean tcsOnlineExamBean,HttpServletRequest request) {
		String userId = (String) request.getSession().getAttribute("userId");
		return tcsApiService.updateExamBookingSlotForSapidServiceRealTime(userId,tcsOnlineExamBean);
	}
	
	@RequestMapping(value = "/downloadExamBookingData", method = RequestMethod.GET)
	public ModelAndView downloadExamBookingData(HttpServletRequest request, HttpServletResponse response){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		StudentMarksBean studentMarksBean = (StudentMarksBean)request.getSession().getAttribute("studentMarksBeanForExamBookingDownload");
		List<TcsOnlineExamBean> tcsOnlineExamBeanList  = tcsApiService.downloadExamBookingDataService(studentMarksBean,getAuthorizedCodes(request));
		return new ModelAndView("tcsOnlineExamPasswordView","tcsOnlineExamPasswordlist",tcsOnlineExamBeanList);
	}
	
	@RequestMapping(value = "/excelBulkPreview", method = RequestMethod.POST)
	public @ResponseBody ResponseBean excelBulkPreview(@RequestParam("fileData") CommonsMultipartFile fileData) {
		FileBean fileBean = new FileBean ();
		fileBean.setFileData(fileData);
		return tcsApiService.bulkExcelPreviewService(fileBean);
	}
	
	@RequestMapping(value = "/excelBulkSyncUpdateTcsExamBookingData", method = RequestMethod.POST)
	public @ResponseBody ResponseBean excelBulkSyncUpdateTcsExamBookingData(@RequestParam("fileData") CommonsMultipartFile fileData,HttpServletRequest request) {
		FileBean fileBean = new FileBean ();
		fileBean.setFileData(fileData);
		String userId = (String) request.getSession().getAttribute("userId");
		return tcsApiService.excelBulkSyncUpdateTcsExamBookingServiceRealTime(userId,fileBean);
	}
	
	@RequestMapping(value = "/multipleUpdateTcsExamBookingData", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public @ResponseBody ResponseBean multipleUpdateTcsExamBookingData(@RequestBody TcsOnlineExamBean tcsOnlineExamList,HttpServletRequest request) {
		String userId = (String) request.getSession().getAttribute("userId");
		return tcsApiService.multipleUpdateTcsExamBookingServiceRealTime(userId,tcsOnlineExamList);
	}

	@RequestMapping(value = "/validateUpdateTcsExamBookingData", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public @ResponseBody ResponseBean validateUpdateTcsExamBookingData(@RequestBody TcsOnlineExamBean tcsOnlineExamList) {
		
		return tcsApiService.validateUpdateTcsExamBookingService(tcsOnlineExamList,"YES");
	}
	
	@RequestMapping(value = "/getDemoExamLogBySapid", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<ResponseBean> getDemoExamLogBySapid(@RequestBody TcsOnlineExamBean tcsOnlineExamList) {
		ResponseBean responseBean = (ResponseBean)new ResponseBean ();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		responseBean = tcsApiService.getDemoExamLogBySapidService(tcsOnlineExamList.getUserId());
		return new ResponseEntity<ResponseBean>(responseBean, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/copyJoinLinkForSapid", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<ResponseBean> copyJoinLinkForSapid(@RequestBody TcsOnlineExamBean tcsOnlineExamBean) {
		ResponseBean responseBean = (ResponseBean)new ResponseBean ();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		responseBean = tcsApiService.getMettlJoinLinkForSapid(tcsOnlineExamBean);
		return new ResponseEntity<ResponseBean>(responseBean, headers, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/api/getMettlTeeStatusByExamDate", method = { RequestMethod.POST }, consumes = "application/json", produces = "application/json")
	public  ResponseEntity<ResponseBean> getMettlTeeStatusByExamDate(@RequestBody String examDate) {
		
		ResponseBean responseBean = new ResponseBean();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");

		responseBean = mettlTeeMarksService.getTestTakenStatusOfExamBookingStudentService(examDate);
		
		return new ResponseEntity<ResponseBean>(responseBean, headers, HttpStatus.OK);
	}

	
	@RequestMapping(value = "/pullMarksDataFromTCSForm")
	public ModelAndView pullMarksDataFromTCSForm(HttpServletRequest request,
			HttpServletResponse response,@ModelAttribute TCSMarksBean studentMarks) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
		return null;
		}
	    
	    ModelAndView mav=new ModelAndView("tcs/pullMarksDataFromTCS");
	    mav.addObject("studentMarks", studentMarks);
	    mav.addObject("yearList", yearList);
	    mav.addObject("monthList", EXAM_MONTH_LIST);
		return mav;	
		
	}
	
	@RequestMapping(value = "/readMarksDataFromTCSAPI",  method = {RequestMethod.POST})
	public ModelAndView readMarksDataFromTCSAPI(HttpServletRequest request,
			HttpServletResponse response,@ModelAttribute TCSMarksBean studentMarks) {
		if(!checkSession(request, response)){ //check if session is active.
			redirectToPortalApp(response);
		return null;
		}
	    ModelAndView mav=new ModelAndView("tcs/pullMarksDataFromTCS");
	    mav.addObject("studentMarks", studentMarks);
	    String userId = (String)request.getSession().getAttribute("userId"); //get user id from session.
	    studentMarks.setCreatedBy(userId);
	    HttpHeaders headers = new HttpHeaders();
		
		headers.add("Content-Type", "application/json");
		ResponseBean responseBean = new ResponseBean();
		try {
			SimpleDateFormat formatNew = new SimpleDateFormat("MM/dd/yyyy");
			SimpleDateFormat formatOld = new SimpleDateFormat("yyyy-MM-dd");
			Date date = formatOld.parse(studentMarks.getFromDate());
			Date date2 = formatOld.parse(studentMarks.getToDate());
			String fromDate = formatNew.format(date);
			String toDate = formatNew.format(date2);
			//get details from tcs and insert into tcs_marks table.
			responseBean = tcsHelper.executePullRequest(studentMarks.getApplicationStatus(), fromDate, toDate, studentMarks.getMonth(),studentMarks.getYear());
			if(responseBean.getStatus().equals("success")) {
				//prepare preview before inserting data.
				
				//get data inserted in to tcs_marks to show in report.
				List<TCSMarksBean> tcsMarksListSummary = tcsHelper.getTotalTCSDataSummary(studentMarks.getYear(), studentMarks.getMonth());
				mav.addObject("tcsMarksList",tcsMarksListSummary);
				mav.addObject("rowCount",tcsMarksListSummary.size());
				request.getSession().setAttribute("tcsMarksListSummary", tcsMarksListSummary);
				setSuccess(request, "Data Added Successfully");
			}else {
				setError(request,responseBean.getMessage());
				
				
			}
			
		} catch (Exception e) {
			
		}
	    mav.addObject("yearList", yearList);
	    mav.addObject("monthList", EXAM_MONTH_LIST);
		return mav;
		
	}
	
	
	
	@RequestMapping(value="/tcsSummaryReport",method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView tcsSummaryReport(HttpServletRequest request, HttpServletResponse response){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
		return null;
		}
		List<TCSMarksBean> tcsMarksListSummary = (List<TCSMarksBean>)request.getSession().getAttribute("tcsMarksListSummary");
		return new ModelAndView("tcsSummaryReportView","tcsMarksList",tcsMarksListSummary);
	}
	
	@RequestMapping(value="/transferTCSResultsToOnlineMarksForm")
	public ModelAndView transferTCSResultsToOnlineMarksForm(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute TCSMarksBean studentMarks)
		{
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
		return null;
		}
			ModelAndView mav=new ModelAndView("tcs/tcsResultsTransferToMarksTable");
			mav.addObject("studentMarks", studentMarks);
			mav.addObject("yearList", yearList);
			mav.addObject("monthList", EXAM_MONTH_LIST);
			return mav;
		}
	
	@RequestMapping(value="/transferTCSResultsToOnlineMarks")
	public ModelAndView transferTCSResultsToOnlineMarks(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute TCSMarksBean studentMarks)
	{
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
		return null;
		}
		String userId = (String)request.getSession().getAttribute("userId");
		ModelAndView mav=new ModelAndView("tcs/tcsResultsTransferToMarksTable");
	
		mav.addObject("studentMarks", studentMarks);
		mav.addObject("yearList", yearList);
		mav.addObject("monthList", EXAM_MONTH_LIST);
		try {
			tcsHelper.transferScoresToOnlineMarks(request, studentMarks, userId);
		}catch(ParseException e) {
			setError(request,e.getMessage());
		}
		String success = (String) request.getSession().getAttribute("successTcsUpsert");
		String error = (String)request.getSession().getAttribute("errorTcsUpsert");
		if(success.equalsIgnoreCase("true")) {
			String message =(String) request.getSession().getAttribute("successTcsUpsertMessage");
			setSuccess(request,message);
		}
		if(error.equalsIgnoreCase("true")) {
			String message =(String) request.getSession().getAttribute("errorTcsUpsertMessage");
			setError(request,message);
		}
		return mav;
		
	}
	
	@RequestMapping(value="/viewTCSMarks",method={RequestMethod.GET,RequestMethod.POST}) 
	public ModelAndView viewTCSMarks(HttpServletRequest request,HttpServletResponse response,
			@ModelAttribute TCSMarksBean tcsMarksBean) {
		ModelAndView mav=new ModelAndView("tcs/viewTCSMarks");
		try {
			mav.addObject("tcsMarksBean",tcsMarksBean);
			mav.addObject("subjectList", getSubjectList());
			mav.addObject("monthList", EXAM_MONTH_LIST);
			mav.addObject("yearList", yearList);
			mav.addObject("studentTypeList", tcsHelper.getConsumerTypesList());
			mav.addObject("subjectCodeList", getSubjectCodeList());
			mav.addObject("rowCount", 0);
		}
		catch (Exception e) {
			
			setError(request,"Error in fetching results");
		}
		return mav;
	}


	public ArrayList<String> getSubjectList(){
			StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			ArrayList<String> subjectList = dao.getActiveSubjects();
		return subjectList;
	}

	
	public ArrayList<Integer> getSubjectCodeList(){
			StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			ArrayList<Integer> subjectCodeList = dao.getSubjectsCodeList();
		return subjectCodeList;
	}
	
	@RequestMapping(value="/tcsData",method={RequestMethod.GET,RequestMethod.POST}) 
	public ModelAndView tcsData(HttpServletRequest request,HttpServletResponse response,
			@ModelAttribute  TCSMarksBean tcsMarksBean) {
		ModelAndView mav=new ModelAndView("tcs/viewTCSMarks");
		try {
			String year= tcsMarksBean.getYear();
			String month=tcsMarksBean.getMonth();
			String subject=tcsMarksBean.getSubject();
			String studentType=tcsMarksBean.getStudentType();
			int	 subjectCode = tcsMarksBean.getSubjectId();
			
			List<TCSMarksBean> tcsMarksList=tcsHelper.getTCSData(year,month,subject,subjectCode,studentType );
			List<TCSMarksBean> tcsMarksListDetails=tcsHelper.getTCSDataDetails(year,month,subject,subjectCode,studentType);
			if(tcsMarksList.isEmpty() || tcsMarksList.size() == 0 ){
				setError(request,"No Data Found !");
			}
			mav.addObject("tcsMarksList",tcsMarksList);
			mav.addObject("subjectList", getSubjectList());
			mav.addObject("monthList", EXAM_MONTH_LIST);
			mav.addObject("yearList", yearList);
			mav.addObject("tcsMarksBean",tcsMarksBean);
			mav.addObject("studentTypeList", tcsHelper.getConsumerTypesList());
			mav.addObject("subjectCodeList", getSubjectCodeList());
			mav.addObject("rowCount", tcsMarksList.size() );
			request.getSession().setAttribute("tcsMarksListSummary", tcsMarksListDetails);
			request.getSession().setAttribute("tcsMarksListDetails", tcsMarksList);
		}
		catch (Exception e) {
			
			setError(request,"Error in fetching results");
		}
		return mav;
	}
	
	@RequestMapping(value="/tcsMarksReport",method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView tcsMarksReport(HttpServletRequest request, HttpServletResponse response){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
		return null;
		}
		List<TCSMarksBean> tcsMarksListDetails = (List<TCSMarksBean>)request.getSession().getAttribute("tcsMarksListDetails");
		return new ModelAndView("tcsMarksReportView","tcsMarksListDetails",tcsMarksListDetails);
	}
	
	
}
