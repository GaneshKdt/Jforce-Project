package com.nmims.controllers;
import java.util.ArrayList;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.ExamCenterBean;
import com.nmims.beans.ExecutiveExamCenter;
import com.nmims.beans.FileBean;
import com.nmims.beans.Page;
import com.nmims.daos.ExamCenterDAO;
import com.nmims.daos.ExecutiveExamDao;
import com.nmims.helpers.ExcelHelper;

@Controller
public class ExecutiveExamController extends ExecutiveBaseController{
	
	@Value("#{'${SAS_EXAM_MONTH_LIST}'.split(',')}") 
	private List<String> SAS_EXAM_MONTH_LIST; 
	
	@Value("#{'${ACAD_YEAR_SAS_LIST}'.split(',')}") 
	private List<String> ACAD_YEAR_SAS_LIST; 
	
	@Autowired
	ApplicationContext act;
	
	@RequestMapping(value = "/addExecutiveExamCenterForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView addExamCenterForm(HttpServletRequest request, HttpServletResponse respnse,Model m) {
		ModelAndView mav = new ModelAndView("executiveAddExamCenter");
		mav.addObject("examCenter", new ExecutiveExamCenter());
		Collections.sort(stateList);
		mav.addObject("stateList", stateList);
		mav.addObject("yearList",ACAD_YEAR_SAS_LIST);
		mav.addObject("monthList",SAS_EXAM_MONTH_LIST);
		return mav;
	}
	
	@RequestMapping(value = "/addExecutiveExamCenter", method = RequestMethod.POST)
	public ModelAndView addExecutiveExamCenter(HttpServletRequest request, HttpServletResponse response, @ModelAttribute ExecutiveExamCenter examCenter){
		ModelAndView mav = new ModelAndView("executiveExamCenter");
		try{
			ExecutiveExamDao dao = (ExecutiveExamDao)act.getBean("executiveExamDao");
			String userId = (String)request.getSession().getAttribute("userId");
			examCenter.setCreatedBy(userId);
			examCenter.setLastModifiedBy(userId);
			int centerId = 0;
				centerId = dao.insertExecutiveExamCenter(examCenter);
				
				examCenter = dao.findById(centerId+"");
			
			examCenter.setCenterId(centerId+"");
			mav.addObject("examCenter", examCenter);

			request.setAttribute("success","true");
			request.setAttribute("successMessage","Exam Center added successfully");
		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in adding Exam Center");
		}
		return mav;
	}
	
	@RequestMapping(value = "/editSASExamCenter", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView editSASExamCenter(HttpServletRequest request, HttpServletResponse response,@ModelAttribute ExecutiveExamCenter examCenter ){
		ModelAndView mav = new ModelAndView("executiveEditExamCenter");

		String centerId = request.getParameter("centerId");
		//String ic = request.getParameter("ic");
		ExecutiveExamDao dao = (ExecutiveExamDao)act.getBean("executiveExamDao");
		
		examCenter = dao.findById(centerId+"");
		mav.addObject("examCenter", examCenter);
		Collections.sort(stateList);
		mav.addObject("stateList", stateList);
		mav.addObject("yearList", ACAD_YEAR_SAS_LIST);
		mav.addObject("monthList",SAS_EXAM_MONTH_LIST);
		request.setAttribute("edit", "true");

		return mav;
	}
	
	
	@RequestMapping(value = "/updateSASExamCenter", method = RequestMethod.POST)
	public ModelAndView updateSASExamCenter(HttpServletRequest request, HttpServletResponse response, @ModelAttribute ExecutiveExamCenter examCenter){
		ModelAndView mav = new ModelAndView("executiveExamCenter");
		try{
			ExecutiveExamDao dao = (ExecutiveExamDao)act.getBean("executiveExamDao");
			String userId = (String)request.getSession().getAttribute("userId");
			examCenter.setLastModifiedBy(userId);
			String centerId = "";
			String errorMessage = dao.updateExecutiveExamCenter(examCenter);
			centerId = examCenter.getCenterId();
			examCenter = dao.findById(centerId+"");
			
			examCenter.setCenterId(centerId+"");
			mav.addObject("examCenter", examCenter);
			
			if(errorMessage==null) {

				request.setAttribute("success","true");
				request.setAttribute("successMessage","Exam Center added successfully");	
			}else {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Error in adding Exam Center"+errorMessage);
				
			}
			
		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in adding Exam Center");
		}
		return mav;
	}
	
	@RequestMapping(value = "/searchExecutiveExamCenter",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView searchSASExamCenter(HttpServletRequest request, HttpServletResponse response, @ModelAttribute ExecutiveExamCenter examCenter){
		ModelAndView mav = new ModelAndView("searchExecutiveExamCenter");
		ExecutiveExamDao dao = (ExecutiveExamDao)act.getBean("executiveExamDao");
		request.getSession().setAttribute("examCenter", examCenter);
		List<ExecutiveExamCenter> allExamCenters = new ArrayList<ExecutiveExamCenter>();
		
		Page<ExecutiveExamCenter> executiveExamCenterPage = dao.getExecutiveExamCentersPage(1,Integer.MAX_VALUE, examCenter);
		
		List<ExecutiveExamCenter> examCentersList = executiveExamCenterPage.getPageItems();
		
		allExamCenters.addAll(examCentersList);

		mav.addObject("page", executiveExamCenterPage);
		mav.addObject("rowCount", allExamCenters.size());
		mav.addObject("examCenter", examCenter);
		Collections.sort(stateList);
		mav.addObject("stateList", stateList);
		mav.addObject("yearList", ACAD_YEAR_SAS_LIST);
		mav.addObject("monthList",SAS_EXAM_MONTH_LIST);
		if(allExamCenters == null || allExamCenters.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No Exam Centers found.");
		}
		mav.addObject("examCentersList", allExamCenters);
		return mav;
	}
	
	@RequestMapping(value = "/deleteSASExamCenter", method = {RequestMethod.GET,RequestMethod.POST})
	public ModelAndView deleteSASExamCenter(HttpServletRequest request, HttpServletResponse response){
		try{
		
			String centerId = request.getParameter("centerId");

			ExecutiveExamDao dao = (ExecutiveExamDao)act.getBean("executiveExamDao");
		

			String result= dao.deleteSASExamCenter(centerId);
			
			if(result == null) {
			request.setAttribute("success","true");
			request.setAttribute("successMessage","Exam Center deleted successfully");
			}else {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Error in deleting Exam Center. Error : "+result);
			}
		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in deleting Exam Center.");
		}
		ExecutiveExamCenter examCenter = (ExecutiveExamCenter)request.getSession().getAttribute("examCenter");
		if(examCenter == null){
			examCenter = new ExecutiveExamCenter();
		}
		return searchSASExamCenter(request,response, examCenter);
	}
	
	@RequestMapping(value = "/examExecutiveCenterHome", method = {RequestMethod.GET, RequestMethod.POST})
	public String examCenterHome(HttpServletRequest request, HttpServletResponse respnse, Model m) {

		return "examExecutiveCenterHome";
	}
	
	
	@RequestMapping(value = "/viewExecutiveExamCenterSlots", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView viewExecutiveExamCenterSlots(HttpServletRequest request, HttpServletResponse response, @ModelAttribute ExecutiveExamCenter ec){
		ModelAndView modelnView = new ModelAndView("examExecutiveCenterSlots");
		try{
			ExecutiveExamDao dao = (ExecutiveExamDao)act.getBean("executiveExamDao");
			//String userId = (String)request.getSession().getAttribute("userId");
			List<ExecutiveExamCenter> examCentersList = new ArrayList<ExecutiveExamCenter>();
			
			examCentersList = dao.getExecutiveExamCenterSlots(ec);
			
			modelnView.addObject("examCentersList", examCentersList);
			modelnView.addObject("examCenter", ec);
			modelnView.addObject("rowCount", examCentersList.size());
			if(examCentersList == null || examCentersList.size() == 0){
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No Exam Centers Slots found.");
			}

		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in getting Exam Center Slots");
		}
		return modelnView;
	}
	
	@RequestMapping(value = "/updateExecutiveExamCenterCapacity", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView updateExecutiveExamCenterCapacity(HttpServletRequest request, HttpServletResponse response, @ModelAttribute ExecutiveExamCenter ec){
		ModelAndView modelnView = new ModelAndView("examExecutiveCenterSlots");
		try{
			ExecutiveExamDao dao = (ExecutiveExamDao)act.getBean("executiveExamDao");
			//String userId = (String)request.getSession().getAttribute("userId");
			
			String[] centers = request.getParameterValues("centerDetails");
			String[] capacity = request.getParameterValues("capacity");
			
			List<ExecutiveExamCenter> centerSlotList = new ArrayList<>();
			for (int i = 0; i < centers.length; i++) {
				String centerInformation = centers[i];
				String date = centerInformation.substring(0, centerInformation.indexOf("|"));
				
				String time = centerInformation.substring(centerInformation.indexOf("|")+1, centerInformation.length());
				
				ExecutiveExamCenter bean = new ExecutiveExamCenter();
				bean.setYear(ec.getYear());
				bean.setMonth(ec.getMonth());
				bean.setCenterId(ec.getCenterId());
				bean.setDate(date);
				bean.setStarttime(time);
				bean.setCapacity(capacity[i]);
				
				centerSlotList.add(bean);
				
			}
			
			dao.updateCapacity(centerSlotList);
			setSuccess(request, "Capacity updated successfully. Please verify new capacity below.");
			List<ExecutiveExamCenter> examCentersList = dao.getExecutiveExamCenterSlots(ec);
			ExecutiveExamCenter examCenter = new ExecutiveExamCenter();
			
			modelnView.addObject("examCentersList", examCentersList);
			modelnView.addObject("examCenter", examCenter);
			modelnView.addObject("rowCount", examCentersList.size());
			if(examCentersList == null || examCentersList.size() == 0){
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No Exam Centers Slots found.");
			}

		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in getting Exam Center Slots");
		}
		return modelnView;
	}
	
	//added to upload executive exam centers  START
	
	@RequestMapping(value = "/addExecutiveExamCenterFormMassUpload", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView addExecutiveExamCenterFormMassUpload(HttpServletRequest request, HttpServletResponse respnse) {
		ModelAndView modelAndView = new ModelAndView("executiveExamCenterUpload");
		modelAndView.addObject("fileBean", new FileBean());
		modelAndView.addObject("yearList",ACAD_YEAR_SAS_LIST );
		modelAndView.addObject("monthList",SAS_EXAM_MONTH_LIST );
		return modelAndView;
	}
	
	
	@RequestMapping(value = "/excelUploadForExecutiveExamCenters", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView excelUploadForExecutiveExamCenters(@ModelAttribute FileBean fileBean,HttpServletRequest request, HttpServletResponse response) {
		ExcelHelper excelHelper = new ExcelHelper();
		ExecutiveExamDao dao = (ExecutiveExamDao)act.getBean("executiveExamDao");
		ArrayList<List> resultList = new ArrayList<>();
		ArrayList<ExecutiveExamCenter> centersList = null;
		String userId = (String)request.getSession().getAttribute("userId");
		boolean errorInInsert=false;
		try{
			resultList = excelHelper.readExecutiveExamCenters(fileBean,userId);
			centersList = (ArrayList<ExecutiveExamCenter>)resultList.get(0);
			for(ExecutiveExamCenter eBean:centersList)
			{
				int returnInt = dao.insertExecutiveExamCenter(eBean);
				if(returnInt == 0) {
					errorInInsert = true;
				}
			}
			if(errorInInsert) {
				setError(request,"Error While Uploading in DB. ");
			}else {
				setSuccess(request,"Successfully Uploaded");
					
			}
			return addExecutiveExamCenterFormMassUpload(request,response);
		}catch(Exception e){
			setError(request,"Center Id Already Present In Database");
			return addExecutiveExamCenterFormMassUpload(request,response);
		}
		
		
	}
	
	
	
	//END
	
}