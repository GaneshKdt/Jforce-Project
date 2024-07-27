package com.nmims.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.BatchBean;
import com.nmims.beans.ConsumerProgramStructureAcads;
import com.nmims.beans.DummyUserBean;
import com.nmims.beans.ResponseAcadsBean;
import com.nmims.interfaces.IDummyUserMgmtService;



@Controller
@RequestMapping("admin")
public class DummyUsersController extends BaseController{
	
	@Autowired
	IDummyUserMgmtService dummyUserService;

	/*@Autowired(required=false)
	ApplicationContext act;*/
	
	
	@RequestMapping(value = "/dummyUsersForm", method = {RequestMethod.GET})
	public ModelAndView feedPostsReportForm(HttpServletRequest request, HttpServletResponse respnse) {
		
		ModelAndView mav = new ModelAndView("dummyUsersForm");
		BatchBean bean = new BatchBean();
		//DummyUsersDAO dummyUsersDAO = (DummyUsersDAO)act.getBean("dummyUsersDAO");
		//mav.addObject("batchList",dummyUsersDAO.getBatchList());
		mav.addObject("consumerType", dummyUserService.getAllConsumerType());
		mav.addObject("batchBean", bean);				
		request.getSession().setAttribute("dummyUsersList", null);		
			
		return mav;
		
	}
	
	@RequestMapping(value = "/dummyUserLogin", method = {RequestMethod.GET})
	public ModelAndView dummyUserLogin(HttpServletRequest request, HttpServletResponse respnse,@RequestParam String userId) {
		
		ModelAndView mav = new ModelAndView("dummyUserLogin");		
		/*DummyUsersDAO dummyUsersDAO = (DummyUsersDAO)act.getBean("dummyUsersDAO");
		String role = dummyUsersDAO.getRoleByUserId(userId);*/	
		
		String role = dummyUserService.getUserRoleById(userId);
		
		if(!"Test User".equals(role)) {			
			return new ModelAndView("studentPortalRediret");	
		}
		
		//userId = dummyUsersDAO.setConsumerPrograrmStructureId(userId);
		
		userId = dummyUserService.setConsumerPrograrmStructureId(userId);
		
		if(!userId.equals("")) {
			mav.addObject("userId", userId);
			return mav;
		}else {
			return new ModelAndView("studentPortalRediret");
		}
		
	}

	@RequestMapping(value = "/dummyUsersReport", method = {RequestMethod.POST})
	public ModelAndView dummyUsersReport(HttpServletRequest request, HttpServletResponse respnse,@ModelAttribute BatchBean bean) {		
		if(!checkSession(request, respnse)){
			return new ModelAndView("studentPortalRediret");
		}
		ModelAndView mav = new ModelAndView("dummyUsersForm");
		/*DummyUsersDAO dummyUsersDAO = (DummyUsersDAO)act.getBean("dummyUsersDAO");
		
		ArrayList<DummyUserBean> dummyUsersList = dummyUsersDAO.getDummyUsers(bean);*/
		
		ArrayList<DummyUserBean> dummyUsersList =(ArrayList<DummyUserBean>) dummyUserService.getApplicableDummyUsers(bean);
		
		bean = new BatchBean();
//		mav.addObject("batchList",dummyUsersDAO.getBatchList());
		mav.addObject("consumerType", dummyUserService.getAllConsumerType());		
		mav.addObject("batchBean", bean);		
		
		request.getSession().setAttribute("dummyUsersList", dummyUsersList);		
		mav.addObject("dummyUsersListSize", dummyUsersList.size());			
		
		if(dummyUsersList != null && dummyUsersList.size() > 0){
			request.setAttribute("success","true");
			request.setAttribute("successMessage",dummyUsersList.size() +" Records Found");
		}else{
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No records found.");
		}
		
		return mav;
		
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/downloadDummyUsers", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadDummyUsers(HttpServletRequest request, HttpServletResponse response) {
		
		//String userId = (String)request.getSession().getAttribute("userId_acads");
		List<DummyUserBean> dummyUsersList = (List<DummyUserBean>)request.getSession().getAttribute("dummyUsersList");
		
		return new ModelAndView("dummyUsersExcelView","dummyUsersList",dummyUsersList);
	}

	@RequestMapping(value = "/getDataBasedOnConsumerType",  method = RequestMethod.POST , consumes="application/json", produces="application/json")
	public ResponseEntity<ResponseAcadsBean> getDataByConsumerType(@RequestBody ConsumerProgramStructureAcads consumerProgramStructure){
		ResponseAcadsBean response = (ResponseAcadsBean) new ResponseAcadsBean();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		try {
			/*DummyUsersDAO dao = (DummyUsersDAO)act.getBean("dummyUsersDAO");
			
			ArrayList<ConsumerProgramStructure> programStructureData = dao.getProgramStructureByConsumerType(consumerProgramStructure.getConsumerTypeId());
			ArrayList<ConsumerProgramStructure> programData = dao.getProgramByConsumerType(consumerProgramStructure.getConsumerTypeId());
			ArrayList<ConsumerProgramStructure> programTypeData = dao.getProgramTypeByConsumerType(consumerProgramStructure.getConsumerTypeId());
			ArrayList<ConsumerProgramStructure> batchData = dao.getBatchByConsumerType(consumerProgramStructure.getConsumerTypeId());*/

			ArrayList<ConsumerProgramStructureAcads> programStructureData = (ArrayList<ConsumerProgramStructureAcads>) dummyUserService.getProgramStructureByConsumerType(consumerProgramStructure.getConsumerTypeId());
			ArrayList<ConsumerProgramStructureAcads> programData = (ArrayList<ConsumerProgramStructureAcads>) dummyUserService.getProgramByConsumerType(consumerProgramStructure.getConsumerTypeId());
			ArrayList<ConsumerProgramStructureAcads> programTypeData = (ArrayList<ConsumerProgramStructureAcads>) dummyUserService.getProgramTypeByConsumerType(consumerProgramStructure.getConsumerTypeId());
			ArrayList<ConsumerProgramStructureAcads> batchData = (ArrayList<ConsumerProgramStructureAcads>) dummyUserService.getBatchByConsumerType(consumerProgramStructure.getConsumerTypeId());

			response.setStatus("success");
			response.setProgramStructureData(programStructureData);
			response.setProgramsData(programData);
			response.setProgramTypeData(programTypeData);
			response.setBatchData(batchData);

		} catch (Exception e) {
			  
			response.setStatus("fail");
		}

		return new ResponseEntity<ResponseAcadsBean>(response, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/getDataBasedOnProgramType",  method = RequestMethod.POST , consumes="application/json", produces="application/json")
	public ResponseEntity<ResponseAcadsBean> getDataByProgramType(@RequestBody ConsumerProgramStructureAcads consumerProgramStructure){
		ResponseAcadsBean response = (ResponseAcadsBean) new ResponseAcadsBean();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		try {
			/*DummyUsersDAO dao = (DummyUsersDAO)act.getBean("dummyUsersDAO");
			
			ArrayList<ConsumerProgramStructure> programStructureData = dao.getProgramStructureByProgramTypeAndConsumerType(consumerProgramStructure);
			ArrayList<ConsumerProgramStructure> programData = dao.getProgramByProgramTypeAndConsumerType(consumerProgramStructure);
			ArrayList<ConsumerProgramStructure> batchData = dao.getBatchByProgramTypeAndConsumerType(consumerProgramStructure);*/

			ArrayList<ConsumerProgramStructureAcads> programStructureData = (ArrayList<ConsumerProgramStructureAcads>) dummyUserService.getProgramStructureByProgramTypeAndConsumerType(consumerProgramStructure);
			ArrayList<ConsumerProgramStructureAcads> programData = (ArrayList<ConsumerProgramStructureAcads>) dummyUserService.getProgramByProgramTypeAndConsumerType(consumerProgramStructure);
			ArrayList<ConsumerProgramStructureAcads> batchData = (ArrayList<ConsumerProgramStructureAcads>) dummyUserService.getBatchByProgramTypeAndConsumerType(consumerProgramStructure);

			response.setStatus("success");
			response.setProgramStructureData(programStructureData);
			response.setProgramsData(programData);			
			response.setBatchData(batchData);

		} catch (Exception e) {
			  
			response.setStatus("fail");
		}

		return new ResponseEntity<ResponseAcadsBean>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/getDataBasedOnProgramStructure",  method = RequestMethod.POST , consumes="application/json", produces="application/json")
	public ResponseEntity<ResponseAcadsBean> getDataByProgramStructure(@RequestBody ConsumerProgramStructureAcads consumerProgramStructure){
		ResponseAcadsBean response = (ResponseAcadsBean) new ResponseAcadsBean();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		try {
			/*DummyUsersDAO dao = (DummyUsersDAO)act.getBean("dummyUsersDAO");
			
			ArrayList<ConsumerProgramStructure> programData = dao.getProgramByProgramTypeAndConsumerTypeAndProgramStructure(consumerProgramStructure);
			ArrayList<ConsumerProgramStructure> batchData = dao.getBatchByProgramTypeAndConsumerTypeAndProgramStructure(consumerProgramStructure);*/

			ArrayList<ConsumerProgramStructureAcads> programData = (ArrayList<ConsumerProgramStructureAcads>) dummyUserService.getProgramByMasterKey(consumerProgramStructure);
			ArrayList<ConsumerProgramStructureAcads> batchData = (ArrayList<ConsumerProgramStructureAcads>) dummyUserService.getBatchByMasterKey(consumerProgramStructure);

			response.setStatus("success");
			response.setProgramsData(programData);
			response.setBatchData(batchData);

		} catch (Exception e) {
			  
			response.setStatus("fail");
		}

		return new ResponseEntity<ResponseAcadsBean>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/getDataBasedOnProgram",  method = RequestMethod.POST , consumes="application/json", produces="application/json")
	public ResponseEntity<ResponseAcadsBean> getDataByProgram(@RequestBody ConsumerProgramStructureAcads consumerProgramStructure){
		ResponseAcadsBean response = (ResponseAcadsBean) new ResponseAcadsBean();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		try {
			/*DummyUsersDAO dao = (DummyUsersDAO)act.getBean("dummyUsersDAO");
			
			ArrayList<ConsumerProgramStructure> batchData = dao.getBatchByProgramTypeAndConsumerTypeAndProgramStructureAndProgram(consumerProgramStructure);*/

			ArrayList<ConsumerProgramStructureAcads> batchData = (ArrayList<ConsumerProgramStructureAcads>) dummyUserService.getBatchByMasterKeyAndProgram(consumerProgramStructure);
			
			response.setStatus("success");
			response.setBatchData(batchData);

		} catch (Exception e) {
			  
			response.setStatus("fail");
		}

		return new ResponseEntity<ResponseAcadsBean>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/getDataBySem",  method = RequestMethod.POST , consumes="application/json", produces="application/json")
	public ResponseEntity<ResponseAcadsBean> getDataBySem(@RequestBody ConsumerProgramStructureAcads consumerProgramStructure){
		ResponseAcadsBean response = (ResponseAcadsBean) new ResponseAcadsBean();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		try {
			/*DummyUsersDAO dao = (DummyUsersDAO)act.getBean("dummyUsersDAO");
			
			ArrayList<ConsumerProgramStructure> batchData = dao.getBatchByProgramTypeAndConsumerTypeAndProgramStructureAndProgramAndSem(consumerProgramStructure);*/

			ArrayList<ConsumerProgramStructureAcads> batchData = (ArrayList<ConsumerProgramStructureAcads>) dummyUserService.getBatchByMasterKeyAndProgramAndSem(consumerProgramStructure);
			
			response.setStatus("success");
			response.setBatchData(batchData);

		} catch (Exception e) {
			  
			response.setStatus("fail");
		}

		return new ResponseEntity<ResponseAcadsBean>(response, HttpStatus.OK);
	}
	
}

	