package com.nmims.controllers;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.CenterExamBean;
import com.nmims.beans.FileBean;
import com.nmims.beans.OnlineExamMarksBean;
import com.nmims.beans.Page;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.helpers.ExcelHelper;

/**
 * Handles requests for the application home page.
 */
@Controller
public class OnlineExamMarksController extends BaseController{

	@Autowired(required=false)
	ApplicationContext act;
	

	private final int pageSize = 20;

	private static final Logger logger = LoggerFactory.getLogger(OnlineExamMarksController.class);
	
	private ArrayList<String> programList = null;

	@Value("#{'${PAST_YEAR_LIST}'.split(',')}")
	private ArrayList<String> yearList;

	private ArrayList<String> subjectList = null; 
	private ArrayList<CenterExamBean> centers = null; 

	
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
		
		centers = null;
		getCentersList();
		
		return null;
	}

	public ArrayList<CenterExamBean> getCentersList(){
		if(this.centers == null){
			StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			this.centers = dao.getAllCenters();
		}
		return centers;
	}
	
	public ArrayList<String> getSubjectList(){
		if(this.subjectList == null){
			StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			this.subjectList = dao.getAllSubjects();
		}
		return subjectList;
	}
	
	public ArrayList<String> getProgramList(){
		if(this.programList == null){
			StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			this.programList = dao.getAllPrograms();
		}
		return programList;
	}
	
	
	@RequestMapping(value = "/admin/uploadOnlineExamMarksForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String uploadOnlineExamMarksForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		logger.info("Add New Company Page");
		
		FileBean fileBean = new FileBean();
		m.addAttribute("fileBean",fileBean);
		m.addAttribute("yearList", yearList);
		m.addAttribute("subjectList", getSubjectList());
		
		return "uploadOnlineExamMarks";
	}
	
	@RequestMapping(value = "/admin/uploadOnlineExamMarks", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView uploadOnlineExamMarks(FileBean fileBean, BindingResult result,HttpServletRequest request, Model m){
			ModelAndView modelnView = new ModelAndView("uploadOnlineExamMarks");
			try{
				StudentMarksDAO sDao = (StudentMarksDAO)act.getBean("studentMarksDAO");
				String userId = (String)request.getSession().getAttribute("userId");
				ExcelHelper excelHelper = new ExcelHelper();
				ArrayList<List> resultList = null;
				if("".equals(fileBean.getFilePassword().trim())){
					resultList = excelHelper.readOnlineExamMarksExcelWithoutPassword(fileBean, userId, sDao);
				}else{
					resultList = excelHelper.readOnlineExamMarksExcelWithPassword(fileBean, userId, sDao);
				}
				
				List<OnlineExamMarksBean> marksList = (ArrayList<OnlineExamMarksBean>)resultList.get(0);
				List<OnlineExamMarksBean> errorBeanList = (ArrayList<OnlineExamMarksBean>)resultList.get(1);
				
				if(errorBeanList.size() > 0){
					request.setAttribute("errorBeanList", errorBeanList);
					return modelnView;
				}
				
				StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
				ArrayList<String> errorList = dao.batchUpsertOnlineExamMarks(marksList);
				
				if(errorList.size() == 0){
					request.setAttribute("success","true");
					request.setAttribute("successMessage",marksList.size() +" rows out of "+ marksList.size()+" inserted successfully.");
				}else{
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", errorList.size() + " records were NOT inserted. Please see row number of rows not inserted. Error row numbers "+errorList);
				}
				
			}catch(Exception e){
				
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Error in inserting marks records: "+e.getMessage());

			}
			fileBean = new FileBean();
			m.addAttribute("fileBean",fileBean);
			m.addAttribute("yearList", yearList);
			m.addAttribute("subjectList", getSubjectList());
		return modelnView;
	}
	
	
	@RequestMapping(value = "/admin/uploadOnlineWrittenRevalMarksForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String uploadOnlineWrittenRevalMarksForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		logger.info("Add New Company Page");
		
		FileBean fileBean = new FileBean();
		m.addAttribute("fileBean",fileBean);
		m.addAttribute("yearList", yearList);
		m.addAttribute("subjectList", getSubjectList());
		
		return "uploadOnlineWrittenRevalMarks";
	}
	
	@RequestMapping(value = "/admin/uploadOnlineWrittenRevalMarks", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView uploadOnlineWrittenRevalMarks(FileBean fileBean, BindingResult result,HttpServletRequest request, Model m){
			ModelAndView modelnView = new ModelAndView("uploadOnlineWrittenRevalMarks");
			try{
				StudentMarksDAO sDao = (StudentMarksDAO)act.getBean("studentMarksDAO");
				String userId = (String)request.getSession().getAttribute("userId");
				ExcelHelper excelHelper = new ExcelHelper();
				ArrayList<List> resultList = excelHelper.readOnlineRevalWrittenMarks(fileBean, userId, sDao);
				
				List<OnlineExamMarksBean> marksList = (ArrayList<OnlineExamMarksBean>)resultList.get(0);
				List<OnlineExamMarksBean> errorBeanList = (ArrayList<OnlineExamMarksBean>)resultList.get(1);
				
				if(errorBeanList.size() > 0){
					request.setAttribute("errorBeanList", errorBeanList);
					return modelnView;
				}
				
				StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
				ArrayList<String> errorList = dao.batchUpsertOnlineRevalMarks(marksList);
				
				if(errorList.size() == 0){
					request.setAttribute("success","true");
					request.setAttribute("successMessage",marksList.size() +" rows out of "+ marksList.size()+" inserted successfully.");
				}else{
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", errorList.size() + " records were NOT inserted. Please see row number of rows not inserted. Error row numbers "+errorList);
				}
				
			}catch(Exception e){
				
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Error in inserting marks records: "+e.getMessage());

			}
			fileBean = new FileBean();
			m.addAttribute("fileBean",fileBean);
			m.addAttribute("yearList", yearList);
			m.addAttribute("subjectList", getSubjectList());
		return modelnView;
	}
	
	
	@RequestMapping(value = "/admin/searchOnlineMarksForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String searchOnlineMarksForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		logger.info("Add New Company Page");
		
		OnlineExamMarksBean searchBean = new OnlineExamMarksBean();
		m.addAttribute("searchBean",searchBean);
		m.addAttribute("yearList", yearList);
		m.addAttribute("subjectList", getSubjectList());
		request.getSession().setAttribute("searchBean", searchBean);
		
		return "searchOnlineMarks";
	}
	
	@RequestMapping(value = "/admin/searchOnlineMarks", method = RequestMethod.POST)
	public ModelAndView searchOnlineMarks(HttpServletRequest request, HttpServletResponse response, @ModelAttribute OnlineExamMarksBean searchBean){
		ModelAndView modelnView = new ModelAndView("searchOnlineMarks");
		request.getSession().setAttribute("searchBean", searchBean);
		
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		Page<OnlineExamMarksBean> page = dao.getOnlineExamMarksPage(1, pageSize, searchBean);
		List<OnlineExamMarksBean> studentMarksList = page.getPageItems();
		
		modelnView.addObject("studentMarksList", studentMarksList);
		modelnView.addObject("page", page);
		
		modelnView.addObject("searchBean", searchBean);
		modelnView.addObject("rowCount", page.getRowCount());
		
		modelnView.addObject("programList", getProgramList());
		modelnView.addObject("yearList", yearList);
		modelnView.addObject("subjectList", getSubjectList());
		
		if(studentMarksList == null || studentMarksList.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No records found.");
		}
		return modelnView;
	}
	
	@RequestMapping(value = "/admin/searchOnlineMarksPage", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView searchOnlineMarksPage(HttpServletRequest request, HttpServletResponse response){
		ModelAndView modelnView = new ModelAndView("searchOnlineMarks");
		int pageNo = Integer.parseInt(request.getParameter("pageNo"));
		OnlineExamMarksBean searchBean = (OnlineExamMarksBean)request.getSession().getAttribute("searchBean");
		
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		
		Page<OnlineExamMarksBean> page = dao.getOnlineExamMarksPage(pageNo, pageSize, searchBean);
		List<OnlineExamMarksBean> studentMarksList = page.getPageItems();
		modelnView.addObject("studentMarksList", studentMarksList);
		modelnView.addObject("page", page);
		
		modelnView.addObject("searchBean", searchBean);
		modelnView.addObject("rowCount", page.getRowCount());
		
		modelnView.addObject("programList", getProgramList());
		modelnView.addObject("yearList", yearList);
		modelnView.addObject("subjectList", getSubjectList());
		
		if(studentMarksList == null || studentMarksList.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No records found.");
		}
		return modelnView;
	}
	
	@RequestMapping(value = "/admin/downloadOnlineMarksResults", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadOnlineMarksResults(HttpServletRequest request, HttpServletResponse response) {

		OnlineExamMarksBean searchBean = (OnlineExamMarksBean)request.getSession().getAttribute("searchBean");
		
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		Page<OnlineExamMarksBean> page = dao.getOnlineExamMarksPage(1, Integer.MAX_VALUE, searchBean);
		List<OnlineExamMarksBean> studentMarksList = page.getPageItems();
		
		return new ModelAndView("onlineMarksExcelView","studentMarksList",studentMarksList);
	}
	

}

