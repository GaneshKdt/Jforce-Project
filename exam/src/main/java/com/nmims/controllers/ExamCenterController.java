package com.nmims.controllers;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.beans.ExamCenterBean;
import com.nmims.beans.FileBean;
import com.nmims.beans.Page;
import com.nmims.beans.TimetableBean;
import com.nmims.daos.ExamCenterDAO;
import com.nmims.helpers.ExcelHelper;
import com.nmims.helpers.MailSender;


@Controller
public class ExamCenterController extends BaseController{

	@Autowired
	ApplicationContext act;
	
	@Value("#{'${CORPORATE_CENTERS}'.split(',')}")
	private List<String> corporateCenterList;
	
	private static final Logger logger = LoggerFactory.getLogger(ExamCenterController.class);
	private final int pageSize = 10;

//	private ArrayList<String> yearList = new ArrayList<String>(Arrays.asList( 
//			"2014","2015","2016","2017","2018","2019","2020","2021","2022")); 
	//reading from property file instead of hard coding
	@Value("#{'${ACAD_YEAR_LIST}'.split(',')}")
	private List<String> yearList;
	
	private ArrayList<String> monthList = new ArrayList<String>(Arrays.asList( 
			"Jan","Feb","Mar","Apr","Jun","Jul","Sep","Dec"));
	
	private ArrayList<String> stateList = new ArrayList<String>(Arrays.asList( 
			"Andhra Pradesh","Arunachal Pradesh","Assam","Bihar","Chhattisgarh","Goa","Gujarat","Haryana","Himachal Pradesh","Jammu and Kashmir",
			"Jharkhand","Karnataka","Kerala","Madhya Pradesh","Maharashtra","Manipur","Meghalaya","Mizoram","Nagaland","Odisha","Punjab","Rajasthan",
			"Sikkim","Tamil Nadu","Telangana","Tripura", "Uttar Pradesh", "Uttarakhand", "West Bengal", "Andaman and Nicobar Islands", 
			"Chandigarh", "Dadar and Nagar Haveli", "Daman and Diu", "Delhi", "Lakshadweep", "Pondicherry")); 
	private Map<String, String> examCenterIdNameMap = null;

	@RequestMapping(value = "/examCenterHome", method = {RequestMethod.GET, RequestMethod.POST})
	public String examCenterHome(HttpServletRequest request, HttpServletResponse respnse, Model m) {

		return "examCenterHome";
	}
	
	public Map<String, String> getExamCenterIdNameMap(){
		//if(this.examCenterIdNameMap == null || examCenterIdNameMap.size() == 0){
		ExamCenterDAO dao = (ExamCenterDAO)act.getBean("examCenterDAO");
		this.examCenterIdNameMap = dao.getExamCenterIdNameMap();
		//}
		return examCenterIdNameMap;
	}

	@RequestMapping(value = "/addExamCenterForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView addExamCenterForm(HttpServletRequest request, HttpServletResponse respnse,Model m) {
		ModelAndView modelNView = new ModelAndView("addExamCenter");
		modelNView.addObject("examCenter", new ExamCenterBean());
		Collections.sort(stateList);
		modelNView.addObject("stateList", stateList);
		modelNView.addObject("corporateCenterList", corporateCenterList);
		modelNView.addObject("yearList",yearList);
		return modelNView;
	}

	@RequestMapping(value = "/centerUserMappingForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView centerUserMappingForm(HttpServletRequest request, HttpServletResponse respnse) {
		ModelAndView modelAndView = new ModelAndView("centerUserMapping");
		modelAndView.addObject("fileBean", new FileBean());
		modelAndView.addObject("yearList", yearList);
		modelAndView.addObject("monthList", monthList);
		
		return modelAndView;
	}
	
	@RequestMapping(value = "/centerUserMapping", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView centerUserMapping(@ModelAttribute FileBean fileBean,HttpServletRequest request, HttpServletResponse response) {
		ExcelHelper excelHelper = new ExcelHelper();
		ExamCenterDAO dao = (ExamCenterDAO)act.getBean("examCenterDAO");
		ArrayList<List> resultList = new ArrayList<>();
		ArrayList<ExamCenterBean> centerUserMappingList = null;
		try{
			resultList = excelHelper.readCenterUserExcel(fileBean);
			centerUserMappingList = (ArrayList<ExamCenterBean>)resultList.get(0);
			dao.batchInsertCenterUserMapping(centerUserMappingList);
	
			setSuccess(request,"Successfully Uploaded");
			return centerUserMappingForm(request,response);
		}catch(Exception e){
			setError(request,e.getMessage());
			return centerUserMappingForm(request,response);
		}
		
		
	}
	
	@RequestMapping(value = "/addExamCenter", method = RequestMethod.POST)
	public ModelAndView addExamCenter(HttpServletRequest request, HttpServletResponse response, @ModelAttribute ExamCenterBean examCenter){
		ModelAndView modelnView = new ModelAndView("examCenter");
		try{
			ExamCenterDAO dao = (ExamCenterDAO)act.getBean("examCenterDAO");
			String userId = (String)request.getSession().getAttribute("userId");
			examCenter.setCreatedBy(userId);
			examCenter.setLastModifiedBy(userId);
			int centerId = 0;
			boolean isCorporate;
			if("All".equals(examCenter.getIc()) ){
				//Non-Corporate Exam, stores in examcenter table
				centerId = dao.insertExamCenter(examCenter,false);
				examCenter = dao.findById(centerId+"",false);
				isCorporate = false;
			}else{
				//Corporate Exam, stores in corporate_examcenter table
				centerId = dao.insertExamCenter(examCenter,true);
				examCenter = dao.findById(centerId+"",true);
				isCorporate = true;
			}
			
			examCenter.setCenterId(centerId+"");
			if("Online".equals(examCenter.getMode())){
				//Create one row per time slot for the given Exam Center with Given Capacity
				dao.createExamCenterSubjetCapacityRecords(centerId, examCenter,isCorporate);
				
			}
			
			
			modelnView.addObject("examCenter", examCenter);

			request.setAttribute("success","true");
			request.setAttribute("successMessage","Exam Center added successfully");
		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in adding Exam Center");
		}
		return modelnView;
	}
	
	@RequestMapping(value = "/viewExamCenterSlots", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView viewExamCenterSlots(HttpServletRequest request, HttpServletResponse response, @ModelAttribute ExamCenterBean ec){
		ModelAndView modelnView = new ModelAndView("examCenterSlots");
		try{
			ExamCenterDAO dao = (ExamCenterDAO)act.getBean("examCenterDAO");
			String userId = (String)request.getSession().getAttribute("userId");
			List<ExamCenterBean> examCentersList = new ArrayList<ExamCenterBean>();
			
			examCentersList = dao.getExamCenterSlots(ec);
			
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
	
	@RequestMapping(value = "/updateExamCenterCapacity", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView updateExamCenterCapacity(HttpServletRequest request, HttpServletResponse response, @ModelAttribute ExamCenterBean ec){
		ModelAndView modelnView = new ModelAndView("examCenterSlots");
		try{
			ExamCenterDAO dao = (ExamCenterDAO)act.getBean("examCenterDAO");
			String userId = (String)request.getSession().getAttribute("userId");
			
			String[] centers = request.getParameterValues("centerDetails");
			String[] capacity = request.getParameterValues("capacity");
			
			List<ExamCenterBean> centerSlotList = new ArrayList<>();
			for (int i = 0; i < centers.length; i++) {
				String centerInformation = centers[i];
				String date = centerInformation.substring(0, centerInformation.indexOf("|"));
				
				String time = centerInformation.substring(centerInformation.indexOf("|")+1, centerInformation.length());
				
				ExamCenterBean bean = new ExamCenterBean();
				bean.setYear(ec.getYear());
				bean.setMonth(ec.getMonth());
				bean.setCenterId(ec.getCenterId());
				bean.setDate(date);
				bean.setStarttime(time);
				bean.setCapacity(capacity[i]);
				
				centerSlotList.add(bean);
				
			}
			
			dao.updateCapacity(centerSlotList,ec.getIc());
			setSuccess(request, "Capacity updated successfully. Please verify new capacity below.");
			List<ExamCenterBean> examCentersList = dao.getExamCenterSlots(ec);
			ExamCenterBean examCenter = new ExamCenterBean();
			
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
	

	@RequestMapping(value = "/editExamCenter", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView editExamCenter(HttpServletRequest request, HttpServletResponse response){
		ModelAndView modelnView = new ModelAndView("addExamCenter");

		String centerId = request.getParameter("centerId");
		String ic = request.getParameter("ic");
		ExamCenterDAO dao = (ExamCenterDAO)act.getBean("examCenterDAO");
		ExamCenterBean examCenter = new ExamCenterBean();
		if("All".equals(ic)){
			examCenter = dao.findById(centerId+"",false);
		}else{
			examCenter = dao.findById(centerId+"",true);
		}
		

		modelnView.addObject("examCenter", examCenter);
		Collections.sort(stateList);
		modelnView.addObject("stateList", stateList);
		modelnView.addObject("corporateCenterList", corporateCenterList);
		modelnView.addObject("yearList", yearList);
		request.setAttribute("edit", "true");

		return modelnView;
	}


	@RequestMapping(value = "/updateExamCenter", method = RequestMethod.POST)
	public ModelAndView updateExamCenter(HttpServletRequest request, HttpServletResponse response, @ModelAttribute ExamCenterBean examCenter){
		ModelAndView modelnView = new ModelAndView("examCenter");
		try{
			ExamCenterDAO dao = (ExamCenterDAO)act.getBean("examCenterDAO");
			dao.updateExamCenter(examCenter);
			
			//This should no longer be done, but is done through a separate page provided.
			/*if("Online".equals(examCenter.getMode())){
				dao.updateExamCenterSubjetCapacityRecords(examCenter.getCenterId(), examCenter.getCapacity());
			}*/

			modelnView.addObject("examCenter", examCenter);
			request.setAttribute("success","true");
			request.setAttribute("successMessage","Exam Center updated successfully");
		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in updating Exam Center");
		}
		return modelnView;
	}

	@RequestMapping(value = "/searchExamCenterForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String searchExamCenterForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {

		ExamCenterBean examCenter = new ExamCenterBean();
		m.addAttribute("examCenter",examCenter);
		Collections.sort(stateList);
		m.addAttribute("stateList", stateList);
		m.addAttribute("yearList", yearList);

		return "searchExamCenter";
	}
	
	
	
	@RequestMapping(value = "/searchExamCenter",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView searchExamCenter(HttpServletRequest request, HttpServletResponse response, @ModelAttribute ExamCenterBean examCenter){
		ModelAndView modelnView = new ModelAndView("searchExamCenter");
		ExamCenterDAO dao = (ExamCenterDAO)act.getBean("examCenterDAO");
		request.getSession().setAttribute("examCenter", examCenter);
		List<ExamCenterBean> allExamCenters = new ArrayList<ExamCenterBean>();
		
		Page<ExamCenterBean> nonCorporateExamCenterPage = dao.getExamCentersPage(1,Integer.MAX_VALUE, examCenter,false);
		Page<ExamCenterBean> corporateExamCenterPage = dao.getExamCentersPage(1,Integer.MAX_VALUE, examCenter,true);
		
		List<ExamCenterBean> examCentersList = nonCorporateExamCenterPage.getPageItems();
		List<ExamCenterBean> corporateExamCentersList = corporateExamCenterPage.getPageItems();
		
		allExamCenters.addAll(examCentersList);
		allExamCenters.addAll(corporateExamCentersList);
		
		modelnView.addObject("page", nonCorporateExamCenterPage);
		modelnView.addObject("rowCount", allExamCenters.size());
		modelnView.addObject("examCenter", examCenter);
		Collections.sort(stateList);
		modelnView.addObject("stateList", stateList);
		modelnView.addObject("yearList", yearList);
		if(allExamCenters == null || allExamCenters.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No Exam Centers found.");
		}

		modelnView.addObject("examCentersList", allExamCenters);
		return modelnView;
	}

	/*@RequestMapping(value = "/searchExamCenterPage",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView searchExamCenterPage(HttpServletRequest request, HttpServletResponse response){
		ModelAndView modelnView = new ModelAndView("searchExamCenter");
		ExamCenterDAO dao = (ExamCenterDAO)act.getBean("examCenterDAO");
		ExamCenterBean examCenter = (ExamCenterBean)request.getSession().getAttribute("examCenter");
		int pageNo = Integer.parseInt(request.getParameter("pageNo"));

		Page<ExamCenterBean> page = dao.getExamCentersPage(pageNo, pageSize, examCenter);
		List<ExamCenterBean> examCentersList = page.getPageItems();

		modelnView.addObject("page", page);
		modelnView.addObject("rowCount", page.getRowCount());
		modelnView.addObject("examCenter", examCenter);
		modelnView.addObject("yearList", yearList);
		Collections.sort(stateList);
		modelnView.addObject("stateList", stateList);

		if(examCentersList == null || examCentersList.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No Exam Centers found.");
		}

		modelnView.addObject("examCentersList", examCentersList);
		return modelnView;
	}*/

	@RequestMapping(value = "/deleteExamCenter", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView deleteExamCenter(HttpServletRequest request, HttpServletResponse response){
		try{
			String centerId = request.getParameter("centerId");
			ExamCenterDAO dao = (ExamCenterDAO)act.getBean("examCenterDAO");
			String ic = request.getParameter("ic");
			if("All".equals(ic)){
				dao.deleteExamCenter(centerId,false);
			}else{
				dao.deleteExamCenter(centerId,true);
			}
			
			request.setAttribute("success","true");
			request.setAttribute("successMessage","Exam Center deleted successfully");
		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in deleting Exam Center.");
		}
		ExamCenterBean examCenter = (ExamCenterBean)request.getSession().getAttribute("examCenter");
		if(examCenter == null){
			examCenter = new ExamCenterBean();
		}
		return searchExamCenter(request,response, examCenter);
	}

	@RequestMapping(value = "/viewExamCenterDetails", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView viewExamCenterDetails(HttpServletRequest request, HttpServletResponse response){
		ModelAndView modelnView = new ModelAndView("examCenter");

		String centerId = request.getParameter("centerId");
		String ic = request.getParameter("ic");
		ExamCenterBean examCenter = new ExamCenterBean();
		ExamCenterDAO dao = (ExamCenterDAO)act.getBean("examCenterDAO");
		if("All".equals(ic)){
			examCenter = dao.findById(centerId+"",false);
		}else{
			examCenter = dao.findById(centerId+"",true);
		}
		

		modelnView.addObject("examCenter", examCenter);
		return modelnView;
	}
	
	//excel upload for examCenter :-START
	@RequestMapping(value = "/excelUploadForExamCentersForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView excelUploadForExamCentersForm(HttpServletRequest request, HttpServletResponse respnse) {
		ModelAndView modelAndView = new ModelAndView("excelUploadForExamCenters");
		modelAndView.addObject("fileBean", new FileBean());
		modelAndView.addObject("yearList", yearList);
		modelAndView.addObject("monthList", monthList);
		return modelAndView;
	}
	
	@RequestMapping(value = "/excelUploadForExamCenters", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView excelUploadForExamCenters(@ModelAttribute FileBean fileBean,HttpServletRequest request, HttpServletResponse response) {
		ExcelHelper excelHelper = new ExcelHelper();
		ExamCenterDAO dao = (ExamCenterDAO)act.getBean("examCenterDAO");
		ArrayList<List> resultList = new ArrayList<>();
		ArrayList<ExamCenterBean> nonCorporateCentersList = null;
		String userId = (String)request.getSession().getAttribute("userId");
		int centerId=0;
		try{
			resultList = excelHelper.readNonCorporateCentersExcel(fileBean,userId);
			nonCorporateCentersList = (ArrayList<ExamCenterBean>)resultList.get(0);
			for(ExamCenterBean e:nonCorporateCentersList)
			{
				centerId=dao.insertExamCenter(e,false);
				dao.createExamCenterSubjetCapacityRecords(centerId, e,false);
				
			}
			
			setSuccess(request,"Successfully Uploaded");
			return excelUploadForExamCentersForm(request,response);
		}catch(Exception e){
			setError(request,"Center Id Already Present In Database");
			return excelUploadForExamCentersForm(request,response);
		}
		
		
	}
	
	//END
	
	

}
