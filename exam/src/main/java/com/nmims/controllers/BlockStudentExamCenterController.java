package com.nmims.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import com.nmims.beans.BlockStudentExamCenterBean;
import com.nmims.beans.ExamBookingRefundRequestReportBean;
import com.nmims.daos.MettlPGResultProcessingDAO;
import com.nmims.services.BlockStudentExamCenterService;

@Controller
public class BlockStudentExamCenterController 
{
	//Variables
	@Value("${SERVER_PATH}")
	private String SERVER_PATH;
	
	@Value("#{'${ACAD_YEAR_LIST}'.split(',')}") 
	private List<String> ACAD_YEAR_LIST;
	
	@Autowired
	private MettlPGResultProcessingDAO dao;
	
	@Autowired
	private BlockStudentExamCenterService blockStudentExamCenterService;
	
	private static final Logger logger = LoggerFactory.getLogger("blockStudentsCenter");
	
	
	
	//Methods
	public boolean checkSession(HttpServletRequest request, HttpServletResponse respnse){
		String userId = (String)request.getSession().getAttribute("userId");
		if(userId != null){
			return true;
		}else{
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Session Expired! Please login again.");
			return false;
		}

	}
	public void redirectToPortalApp(HttpServletResponse httpServletResponse) {
		
		try {
			httpServletResponse.sendRedirect(SERVER_PATH+"studentportal/");
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
	
	}
	
	
	
	//Mappings
	
	//To Show The Form For Search Students And Block Their CenterId
	@RequestMapping(value = "admin/centerNotAllowForm", method = {RequestMethod.GET})
	public ModelAndView centerNotAllowForm(HttpServletRequest request,HttpServletResponse response, Model m) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		ModelAndView mv = new ModelAndView("blockCenter/centerNotAllowedForm");
		mv.addObject("searchBean", new BlockStudentExamCenterBean());
		mv.addObject("consumerTypeList", dao.getConsumerTypeList());
		mv.addObject("yearList", ACAD_YEAR_LIST);
		mv.addObject("showStudents", "false");
		
		return mv;
	}
	
	//To Show The Students By ConsumerType, Program Structure and Program Name
	@RequestMapping(value = "admin/centerNotAllowSearchStudents", method = {RequestMethod.POST})
	public ModelAndView centerNotAllowSearchStudents(HttpServletRequest request,HttpServletResponse response, @ModelAttribute BlockStudentExamCenterBean searchBean, Model m) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ArrayList<BlockStudentExamCenterBean> studentList = new ArrayList<>();
		
		ModelAndView mv = new ModelAndView("blockCenter/centerNotAllowedForm");
		mv.addObject("searchBean", searchBean);
		mv.addObject("consumerTypeList", dao.getConsumerTypeList());
		mv.addObject("yearList", ACAD_YEAR_LIST);
		try
		{
			//Getting All Applicable Centers List
			List<BlockStudentExamCenterBean> allCentersList = blockStudentExamCenterService.getAllExamCenter();
			
			//Getting List of all Searched Students List
			studentList = blockStudentExamCenterService.getStudentsByFilters(searchBean);
			
			//If students size greater than 0 than only show the option of centers block on UI
			if(studentList.size()>0)
			{
				mv.addObject("examCenterList", allCentersList);
				mv.addObject("centerNotAllowStudentsList", studentList);
				mv.addObject("showStudents", "true");
			}
			else
			{
				mv.addObject("showStudents", "false");
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Students Not Found!");
			}
			
			//Getting List Of Students Who have marked UFM in their Last Two exam cycle
			request.getSession().setAttribute("centerNotAllowStudentsList", studentList);
		}
		catch(Exception e)
		{
			logger.error("Error In Getting All Students List For Center Block In centerNotAllowed : "+e);
			mv.addObject("showStudents", "false");
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Something Went Wrong In Showing Students, Please Contact To IT Department!!");
		}
		
		return mv;
	}
	
	//To Search The Students Who Have Marked UFM In Their Last Two Exam Cycle
	@RequestMapping(value = "admin/centerNotAllowSearchUFMStudents", method = {RequestMethod.POST})
	public ModelAndView centerNotAllowSearchUFMStudents(HttpServletRequest request,HttpServletResponse response, @ModelAttribute BlockStudentExamCenterBean searchBean, Model m) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ArrayList<BlockStudentExamCenterBean> studentList = new ArrayList<>();
		
		ModelAndView mv = new ModelAndView("blockCenter/centerNotAllowedForm");
		mv.addObject("searchBean", searchBean);
		mv.addObject("consumerTypeList", dao.getConsumerTypeList());
		mv.addObject("yearList", ACAD_YEAR_LIST);
		
		try
		{
			//Getting All Applicable Centers List
			List<BlockStudentExamCenterBean> allCentersList = blockStudentExamCenterService.getAllExamCenter();
			
			//Getting List Of Students Who have marked UFM in their Last Two exam cycle
			studentList = blockStudentExamCenterService.getUFMStudents(searchBean);
			
			//If students size greater than 0 than only show the option of centers block on UI
			if(studentList.size()>0)
			{
				mv.addObject("examCenterList", allCentersList);
				mv.addObject("centerNotAllowStudentsList", studentList);
				mv.addObject("showStudents", "true");
			}
			else
			{
				mv.addObject("showStudents", "false");
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Student Not Found!!");
			}
			
			//For UI Report in Tabular Form
			request.getSession().setAttribute("centerNotAllowStudentsList", studentList);
		}
		catch(Exception e)
		{
			logger.error("Error In Getting Students List Who Have Marked UFM In Their Last Two Exam Cycle : "+e);
			mv.addObject("showStudents", "false");
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Something Went Wrong In Showing Students Who Have Marked UFM In Their Last Two Exam Cycle, Please Contact To IT Department!!");
		}
		
		return mv;
	}
	
	//To Upload Excel And Read The Sapids In Excel And Reflects Sapids List On UI
	@RequestMapping(value = "/admin/centerNotAllowSearchExcelUploadStudents", method = { RequestMethod.POST })
	public ModelAndView centerNotAllowSearchExcelUploadStudents(HttpServletRequest request, @RequestParam(value="file") MultipartFile file) 
	{
		//Bean for hold the sapid list in self list of bean with error status like error message and isError or not
		BlockStudentExamCenterBean studentList;
		
		ModelAndView mv = new ModelAndView("blockCenter/centerNotAllowedForm");
		mv.addObject("searchBean", new BlockStudentExamCenterBean());
		mv.addObject("consumerTypeList", dao.getConsumerTypeList());
		mv.addObject("yearList", ACAD_YEAR_LIST);
		
		try {
			
			//Get list of sapid and error status
			studentList = blockStudentExamCenterService.getExcelData(file);
			
			//Check error status, if not
			if(!studentList.isErrorRecord()) {
				
				//Getting All Applicable Centers List
				List<BlockStudentExamCenterBean> allCentersList = blockStudentExamCenterService.getAllExamCenter();
				
				request.setAttribute("success", "true");
				request.setAttribute("successMessage", "Excel Uploaded Successfully, Kindly Check The Sapids Below And Then Block The Center");
				
				mv.addObject("centerNotAllowStudentsList", studentList.getStudentList());
				mv.addObject("examCenterList", allCentersList);
				mv.addObject("showStudents", "true");
				
				//Setting List of Sapid in session
				request.getSession().setAttribute("centerNotAllowStudentsList", studentList.getStudentList());
			}
			
			//Check error status, if any
			if(studentList.isErrorRecord()) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", studentList.getErrorMessage());
				mv.addObject("showStudents", "false");
			}
		}
		catch (Exception e) {
			logger.info("Error in inserting rows due to : {}", e);
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage","Error : "+ e.getMessage());
			mv.addObject("showStudents", "false");
		}
		
		return mv;
	}
	
	//To Block The Center Id For Students
	@RequestMapping(value = "admin/centerNotAllowBlock", method = {RequestMethod.POST})
	public ModelAndView centerNotAllowBlock(HttpServletRequest request,HttpServletResponse response, @ModelAttribute BlockStudentExamCenterBean searchBean, Model m) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ArrayList<BlockStudentExamCenterBean> studentList =(ArrayList<BlockStudentExamCenterBean>) request.getSession().getAttribute("centerNotAllowStudentsList");
		
		ModelAndView mv = new ModelAndView("blockCenter/centerNotAllowedForm");
		mv.addObject("searchBean", searchBean);
		mv.addObject("consumerTypeList", dao.getConsumerTypeList());
		mv.addObject("yearList", ACAD_YEAR_LIST);
		mv.addObject("showStudents", "false");

		try
		{
			//Getting All Applicable Centers List
			List<BlockStudentExamCenterBean> allCentersList = blockStudentExamCenterService.getAllExamCenter();
			
			//Getting List of Blocked Students with Blocked Center Names
			int countRow = blockStudentExamCenterService.blockStudentsCenter(studentList,searchBean,allCentersList);

			//If students size greater than 0 then only show the successful message on UI
			if(countRow>0)
			{
				request.setAttribute("success", "true");
				request.setAttribute("successMessage", "Selected Centers Are Blocked Successfully for "+studentList.size()+" Students!");				
			}
			else
			{
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Sorry! Centers are not available for blocked. Please deselect at least one and try again!!");
			}
		}
		catch(Exception e)
		{
			logger.error("Error In Creating Entry To Block The Center For Students : "+e);
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Something Went Wrong In Blocking Centers, Please Contact To IT Department!");
			mv.addObject("showStudents", "false");
		}		
		return mv;
	}
	
	//To Download The Excel Report Of Searched Students For Center Block
	@RequestMapping(value = "/admin/downloadBlockStudentsCenterReport", method = { RequestMethod.GET,
			RequestMethod.POST})
	public ModelAndView downloadBlockStudentsCenterReport(HttpServletRequest request, HttpServletResponse response) {
		if (!checkSession(request, response)) {
			redirectToPortalApp(response);
			return null;
		}

		List<ExamBookingRefundRequestReportBean> centerNotAllowStudentsList = (List<ExamBookingRefundRequestReportBean>) request
				.getSession().getAttribute("centerNotAllowStudentsList");

		return new ModelAndView("examCenterBlockReportExcelView", "centerNotAllowStudentsList",
				centerNotAllowStudentsList);
	}
	
	//To Show The Page For Search Students Who Have Already Any Blocked Center By Year And Month
	@RequestMapping(value = "admin/searchBlockStudentsCenterForm", method = {RequestMethod.GET})
	public ModelAndView searchBlockedStudentsCenter(HttpServletRequest request,HttpServletResponse response, Model m) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		ModelAndView mv = new ModelAndView("blockCenter/searchBlockedStudentsCenter");
		mv.addObject("searchBean", new BlockStudentExamCenterBean());
		mv.addObject("yearList", ACAD_YEAR_LIST);
		mv.addObject("showStudents", "false");
		
		return mv;
	}
	
	//To Search Search The Students Who Have Already Any Blocked Center
	@RequestMapping(value = "admin/searchBlockStudentsCenter", method = {RequestMethod.POST})
	public ModelAndView centerNotAllowedStudents(HttpServletRequest request,HttpServletResponse response, 
			@ModelAttribute BlockStudentExamCenterBean searchBean, Model m) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		ModelAndView mv = new ModelAndView("blockCenter/searchBlockedStudentsCenter");
		mv.addObject("searchBean", new BlockStudentExamCenterBean());
		mv.addObject("yearList", ACAD_YEAR_LIST);
		
		try
		{
			//To get the list of students who have already any blocked center in given year and month
			ArrayList<BlockStudentExamCenterBean> studentList = blockStudentExamCenterService.getStudentsBlockedCenter(searchBean);

			if(studentList.size() > 0)
			{
				mv.addObject("centerNotAllowStudentsList", studentList);
				mv.addObject("showStudents", "true");
			}
			else
			{
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Studnets Not Found!");
				mv.addObject("showStudents", "false");
			}
		}
		catch(Exception e)
		{
			logger.error("Error In Findng Blocked Centers Students : "+e);
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Studnets Not Found!");
			mv.addObject("showStudents", "false");
		}
		
		return mv;
	}
	
	//To Unblock The Center For Students By Sapid, Year, Month And CenterId
	@RequestMapping(value = "admin/unblockStudentsCenter", method = {RequestMethod.GET})
	public ModelAndView centerNotAllowUnblock(HttpServletRequest request,HttpServletResponse response, 
			@RequestParam("year") String year, 
			@RequestParam("month") String month, 
			@RequestParam("sapid") String sapid,
			@RequestParam("centerId") String centerId,
			@RequestParam("centerName") String centerName,
			Model m) 
	{
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		BlockStudentExamCenterBean urlData = new BlockStudentExamCenterBean();
		urlData.setYear(year);
		urlData.setMonth(month);
		urlData.setSapid(sapid);
		urlData.setCenterId(centerId);
		urlData.setCenterName(centerName);
		
		ModelAndView mv = new ModelAndView("blockCenter/searchBlockedStudentsCenter");
		mv.addObject("searchBean", new BlockStudentExamCenterBean());
		mv.addObject("yearList", ACAD_YEAR_LIST);
		try
		{
			//To unblock the centerId
			boolean isDeleted = blockStudentExamCenterService.unblockStudentsCenter(urlData);
			
			if(isDeleted)
			{
				request.setAttribute("success", "true");
				request.setAttribute("successMessage", urlData.getCenterName()+" Center Unblocked For "+urlData.getSapid()+" Succesfully!");
			}
			else
			{
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", urlData.getCenterName()+" Center Unblocked For "+urlData.getSapid()+" Unsuccesful!");
			}
			
			mv.addObject("showStudents", "false");
		}
		catch(Exception e)
		{
			logger.error("Error In Unblocking Center For Student : "+e);
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", urlData.getCenterName()+" Center Unblocked For "+urlData.getSapid()+" Unsuccesful!");
			mv.addObject("showStudents", "false");
		}
		
		return mv;
	}
}
