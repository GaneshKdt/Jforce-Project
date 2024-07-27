package com.nmims.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.nmims.beans.Page;
import com.nmims.beans.StudentMarksBean;
import com.nmims.beans.UFMIncidentBean;
import com.nmims.beans.UFMNoticeBean;
import com.nmims.beans.UFMResponseBean;
import com.nmims.services.UFMNoticeService;
import com.nmims.views.UFMIncidentDetailsReport;

@Controller
public class UFMNoticeController extends BaseController {

    @Value("#{'${CURRENT_YEAR_LIST}'.split(',')}")
	private ArrayList<String> yearList; 
	
	private ArrayList<String> monthList = new ArrayList<String>(
		Arrays.asList( 
			"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"
		)
	);
	
	private final int pageSize = 20;
	
	public static final Logger ufm = LoggerFactory.getLogger("ufm");
	@Autowired
	UFMIncidentDetailsReport IncidentReport;
	
	@Autowired
	UFMNoticeService ufmNoticeService;
	
	@RequestMapping(value = "/admin/ufmCheckListForm", method = {RequestMethod.GET})
	public ModelAndView ufmCheckListForm(HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView mv = new ModelAndView("ufm/ufmCheckListForm");
		return mv;
	}
	
	@RequestMapping(value = "/admin/uploadUFMShowCauseFileForm", method = {RequestMethod.GET})
	public ModelAndView uploadQuestionFileForm(HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		UFMNoticeBean fileBean = new UFMNoticeBean();
		ModelAndView mv = new ModelAndView("ufm/uploadShowCauseList");
		mv.addObject("fileBean", fileBean);
		mv.addObject("yearList", yearList);
		mv.addObject("monthList", monthList);
		return mv;
	}
	
	@RequestMapping(value = "/admin/uploadUFMShowCauseFile", method = {RequestMethod.POST})
	public ModelAndView uploadUFMFile(HttpServletRequest request, HttpServletResponse response, UFMNoticeBean bean) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		bean.setCreatedBy((String) request.getSession().getAttribute("userId"));
		bean.setLastModifiedBy((String) request.getSession().getAttribute("userId"));
		List<UFMNoticeBean> successList = new ArrayList<UFMNoticeBean>();
		List<UFMNoticeBean> successListDocuments = new ArrayList<UFMNoticeBean>();
		List<UFMNoticeBean> errorList = new ArrayList<UFMNoticeBean>();
		
		//Added by shivam.pandey.EXT - START
		if(("COC").equals(bean.getCategory())) {
			ufmNoticeService.performUploadCOCNoticeFiles(bean, successList, successListDocuments, errorList);
		}
		else if("DisconnectAbove15Min".equals(bean.getCategory())) {
			ufmNoticeService.performUploadDisconnectAboveNoticeFiles(bean, successList, successListDocuments, errorList);
		}
		else if("DisconnectBelow15Min".equals(bean.getCategory())) {
			ufmNoticeService.performUploadDisconnectBelowNoticeFiles(bean, successList, successListDocuments, errorList);
		}
		else {
			ufmNoticeService.performUploadUFMNoticeFiles(bean, successList, successListDocuments, errorList);
		}
		//Added by shivam.pandey.EXT - END
		
		ModelAndView mv = new ModelAndView("ufm/uploadShowCauseList");
		mv.addObject("fileBean", bean);
		mv.addObject("yearList", yearList);
		mv.addObject("monthList", monthList);
		mv.addObject("successList", successList);
		mv.addObject("successListDocuments", successListDocuments);
		
		mv.addObject("errorList", errorList);
		
		ufm.info("Successfully uploaded "+ successList.size() + " records.\nSuccessfully generated documents for " + successListDocuments.size() + " records Successfully Uploaded Incident "+ bean.getIncidentRowUpdated() );
		ufm.info("Error list size is:"+errorList.size());
		ufm.info("Error list size is:"+new Gson().toJson(errorList));

		
		setError(request, "Failed to upload " + errorList.size() + " records.\nPlease find the list of errors below.");
		if(!StringUtils.isBlank(bean.getError())) {
	
		setError(request,bean.getError());
		
		}
		setSuccess(request, "Successfully uploaded " + successList.size() + " records.\nSuccessfully generated documents for " + successListDocuments.size() + " records Successfully Uploaded Incidents "+ bean.getIncidentRowUpdated());
		return mv;
	}

	@RequestMapping(value = "/admin/listOfStudentsMarkedForUFMForm", method = {RequestMethod.GET})
	public ModelAndView listOfStudentsMarkedForUFMForm(HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}

		ModelAndView mv = new ModelAndView("ufm/markedStudentsList");
		mv.addObject("inputBean", new UFMNoticeBean());
		mv.addObject("yearList", yearList);
		mv.addObject("monthList", monthList);
		return mv;
	}

	@RequestMapping(value = "/admin/listOfStudentsMarkedForUFM", method = {RequestMethod.POST})
	public ModelAndView listOfStudentsMarkedForUFM(HttpServletRequest request, HttpServletResponse response, UFMNoticeBean bean) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView mv = new ModelAndView("ufm/markedStudentsList");
		request.getSession().setAttribute("inputBean", bean);
		
		mv.addObject("inputBean", bean);
		mv.addObject("yearList", yearList);
		mv.addObject("monthList", monthList);
		
		try {
			Page<UFMNoticeBean> page = ufmNoticeService.getListOfStudentsMarkedForUFM(bean,1,pageSize);
			List<UFMNoticeBean> listOfRecords = page.getPageItems();
			
			if(listOfRecords == null || listOfRecords.size() == 0){
				setError(request, "No Records Found.");
			}
			
			mv.addObject("records", listOfRecords);
			mv.addObject("page", page);
		} catch (Exception e) {
			ufm.info("Exception while getting listOfStudentsMarkedForUFM is :"+e.getMessage());
			setError(request, "Failed to fetch records " + e.getMessage());
		}
		return mv;
	}
	
	@RequestMapping(value = "/admin/listOfStudentsMarkedForUFMPage", method = {RequestMethod.GET})
	public ModelAndView searchProjectSubmissionPage(HttpServletRequest request, HttpServletResponse response){
		ModelAndView modelnView = new ModelAndView("ufm/markedStudentsList");
		
		UFMNoticeBean inputBean = (UFMNoticeBean)request.getSession().getAttribute("inputBean");
		int pageNo = Integer.parseInt(request.getParameter("pageNo"));
		
		modelnView.addObject("inputBean", inputBean);
		modelnView.addObject("yearList", yearList);
		modelnView.addObject("monthList", monthList);
		
		try
		{
			Page<UFMNoticeBean> page = ufmNoticeService.getListOfStudentsMarkedForUFM(inputBean,pageNo,pageSize);
			List<UFMNoticeBean> listOfRecords = page.getPageItems();
			
			if(listOfRecords == null || listOfRecords.size() == 0){
				setError(request, "No Records Found.");
			}
			
			modelnView.addObject("records", listOfRecords);
			modelnView.addObject("page", page);
			modelnView.addObject("rowCount", page.getRowCount());
		}
		catch(Exception e)
		{
			ufm.error("Error in fetching UFM record from pagination : "+e);
			setError(request, "Error in getting records, " + e.getMessage());
		}
		
		return modelnView;
	}
	
	@RequestMapping(value = "/admin/downloadUFMStudentsList",method = {RequestMethod.GET})
	public ModelAndView downloadUFMStudentsList(HttpServletRequest request,HttpServletResponse response){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
		}
		
		List<UFMNoticeBean> listOfRecords = new ArrayList<>();
		UFMNoticeBean inputBean = (UFMNoticeBean)request.getSession().getAttribute("inputBean");
		
		try
		{
			Page<UFMNoticeBean> page = ufmNoticeService.getListOfStudentsMarkedForUFM(inputBean,1,Integer.MAX_VALUE);
			listOfRecords = page.getPageItems();
		}
		catch(Exception e)
		{
			ufm.error("Error in fetching UFM record from pagination for Download Excel : "+e);
		}
		
		return new ModelAndView("UFMListExcelView", "UFMRecords", listOfRecords);
	}
	

	@RequestMapping(value = "/admin/uploadUFMActionFileForm", method = {RequestMethod.GET})
	public ModelAndView uploadActionFileForm(HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		UFMNoticeBean fileBean = new UFMNoticeBean();
		ModelAndView mv = new ModelAndView("ufm/uploadActionFile");
		mv.addObject("fileBean", fileBean);
		mv.addObject("yearList", yearList);
		mv.addObject("monthList", monthList);
		return mv;
	}

	@RequestMapping(value = "/admin/uploadUFMActionFile", method = {RequestMethod.POST})
	public ModelAndView uploadActionFile(HttpServletRequest request, HttpServletResponse response, UFMNoticeBean bean) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		bean.setCreatedBy((String) request.getSession().getAttribute("userId"));
		bean.setLastModifiedBy((String) request.getSession().getAttribute("userId"));
		List<UFMNoticeBean> successList = new ArrayList<UFMNoticeBean>();
		List<UFMNoticeBean> successListDocuments = new ArrayList<UFMNoticeBean>();
		List<UFMNoticeBean> errorList = new ArrayList<UFMNoticeBean>();
		
		//Added by shivam.pandey.EXT
		if(("COC").equals(bean.getCategory()))
			ufmNoticeService.performUploadCOCActionFiles(bean, successList, successListDocuments, errorList);
		else if("DisconnectAbove15Min".equals(bean.getCategory()))
			ufmNoticeService.performUploadDisconnectAboveActionFiles(bean, successList, successListDocuments, errorList);
		else if("DisconnectBelow15Min".equals(bean.getCategory()))
			ufmNoticeService.performUploadDisconnectBelowActionFiles(bean, successList, successListDocuments, errorList);
		else
			ufmNoticeService.performUploadUFMActionFiles(bean, successList, successListDocuments, errorList);
		//Added by shivam.pandey.EXT
		
		ModelAndView mv = new ModelAndView("ufm/uploadActionFile");
		mv.addObject("fileBean", bean);
		mv.addObject("yearList", yearList);
		mv.addObject("monthList", monthList);
		mv.addObject("successList", successList);
		mv.addObject("successListDocuments", successListDocuments);
		mv.addObject("errorList", errorList);
		
		ufm.info("Successfully uploaded " + successList.size() + " records.\nSuccessfully generated documents for " + successListDocuments.size() + " records");
		ufm.info("Error list size is:"+errorList.size());
		ufm.info("Error list size is:"+new Gson().toJson(errorList));
//		for(UFMNoticeBean error : errorList)
//		{
//			ufm.info("Error to upload "+error.getSapid()+":"+error.getSubject()+":"+error.getExamDate()+":"+error.getExamTime()+" is: "+error.getError());
//		}
		
		setError(request, "Failed to upload " + errorList.size() + " records.\nPlease find the list of errors below.");
		setSuccess(request, "Successfully uploaded " + successList.size() + " records.\nSuccessfully generated documents for " + successListDocuments.size() + " records");
		return mv;
	}	
	


	@RequestMapping(value = "/student/ufmStatus", method = {RequestMethod.GET})
	public ModelAndView ufmStatus(HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}

		String sapid = (String) request.getSession().getAttribute("userId");
		ModelAndView mv = new ModelAndView("ufm/ufmStatus");
		
		mv.addObject("responseBean", ufmNoticeService.getUfmResponseBean(sapid));
		return mv;
	}

	@RequestMapping(value = "/student/submitShowCause", method = {RequestMethod.POST})
	public ModelAndView submitShowCause(HttpServletRequest request, HttpServletResponse response, UFMNoticeBean bean) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		String sapid = (String) request.getSession().getAttribute("userId");
		bean.setLastModifiedBy(sapid);
		bean.setSapid(sapid);
		String error = ufmNoticeService.addStudentResponse(bean);

		ModelAndView mv = new ModelAndView("ufm/ufmStatus");
		
		if(StringUtils.isBlank(error)) {
			setSuccess(request, "Successfully submitted response!");
		} else {
			setError(request, "Error saving response! Error : " + error);
		}

		mv.addObject("responseBean", ufmNoticeService.getUfmResponseBean(sapid));
		return mv;
	}

//	to be deleted api shifted to rest controller
//	@RequestMapping(value = "/m/getStudentUFMList", method = { RequestMethod.POST }, produces = "application/json; charset=UTF-8")
//	public ResponseEntity<UFMResponseBean> getStudentUFMList(@RequestBody UFMNoticeBean student) {
//		return new ResponseEntity<UFMResponseBean>(ufmNoticeService.getUfmResponseBean(student.getSapid()), HttpStatus.OK);
//	}
//
//	@RequestMapping(value = "/m/submitShowCause", method = {RequestMethod.POST})
//	public ResponseEntity<UFMResponseBean> msubmitShowCause(HttpServletRequest request, HttpServletResponse response,@RequestBody UFMNoticeBean bean) {
//		UFMResponseBean responseBean = new UFMResponseBean();
//		bean.setLastModifiedBy(bean.getSapid());
//		
//		String error = ufmNoticeService.addStudentResponse(bean);
//		
////		ufmNoticeService.getUfmResponseBean(bean.getSapid());
//		
//		if(StringUtils.isBlank(error)) {
//			responseBean.setStatus("success");
//		} else {
//			responseBean.setStatus("error");
//			responseBean.setErrorMessage(error);
//		}
//		return new ResponseEntity<UFMResponseBean>(responseBean, HttpStatus.OK);
//	}
	
	@GetMapping(value = "/admin/markedRIANVForUFMForm")
	public ModelAndView markedRIANVForUFMForm(HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}

		ModelAndView mv = new ModelAndView("markRIANV");
		mv.addObject("yearList", yearList);
		mv.addObject("monthList", monthList);
		return mv;
	}
	
	@GetMapping(value = "/admin/downloadUFMMarkRIARecords")
	public ModelAndView downloadUFMMarkRIARecords(HttpServletRequest request, HttpServletResponse response){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		 List<StudentMarksBean> list = (List<StudentMarksBean>)request.getSession().getAttribute("downloadableRIANVRecords");
		 return new ModelAndView("UFMMarkRIAExcelView","ufmMarkRIARecords",list);
	}
	
	@GetMapping(value = "/admin/downloadUFMMarkNVRecords")
	public ModelAndView downloadUFMMarkNVRecords(HttpServletRequest request, HttpServletResponse response){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		 List<StudentMarksBean> list = (List<StudentMarksBean>)request.getSession().getAttribute("downloadableRIANVRecords");
		 return new ModelAndView("UFMMarkNVExcelView","ufmMarkNVRecords",list);
	}
	
	@GetMapping(value = "/admin/downloadUFMMarkScoredRecords")
	public ModelAndView downloadUFMMarkScoredRecords(HttpServletRequest request, HttpServletResponse response){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		 List<StudentMarksBean> list = (List<StudentMarksBean>)request.getSession().getAttribute("downloadableRIANVRecords");
		 return new ModelAndView("UFMMarkScoredExcelView","ufmMarkScoredRecords",list);
	}
	@GetMapping(value = "/admin/ufmIncidentReportForm")
	public ModelAndView ufmIncidentReportForm(HttpServletRequest request, HttpServletResponse response){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		UFMNoticeBean fileBean = new UFMNoticeBean();
		ModelAndView mv = new ModelAndView("ufm/ufmIncidentReport");
		mv.addObject("fileBean", fileBean);
		mv.addObject("yearList", yearList);
		mv.addObject("monthList", monthList);
		return mv;
	} 
		
	@PostMapping(value = "/admin/ufmIncidentReport")
	public ModelAndView ufmIncidentReport(HttpServletRequest request, HttpServletResponse response,UFMNoticeBean inputBean){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		 ModelAndView mv;
		 mv = new ModelAndView("ufm/ufmIncidentReport");
		try {
			UFMNoticeBean fileBean = new UFMNoticeBean();
			mv.addObject("fileBean", fileBean);
			mv.addObject("yearList", yearList);
			mv.addObject("monthList", monthList);
			
			List<UFMNoticeBean> incidentDetailsList = ufmNoticeService.getIncidentDetailsForReport(inputBean);
			request.getSession().setAttribute("UFMincidentReport", incidentDetailsList);
			mv.addObject("incidentDetails",incidentDetailsList);
		} catch (Exception e) {
			request.setAttribute("error", "true");
			 request.setAttribute("errorMessage",e.getMessage());
		}
		return mv;
	}
	@GetMapping(value = "/admin/UFMIncidentReport")
	public ModelAndView getUFMIncidentReport(HttpServletRequest request, HttpServletResponse response) {
		return new ModelAndView(IncidentReport) ;
	}
}