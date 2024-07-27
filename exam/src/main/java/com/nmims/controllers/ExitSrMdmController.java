package com.nmims.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

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
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.ConsumerType;
import com.nmims.beans.ProgramsBean;
import com.nmims.beans.ResponseBean;
import com.nmims.services.ExitSrMdmService;
import com.nmims.views.ExitSrProgramMappedExcelView;

@Controller
public class ExitSrMdmController extends BaseController{

	@Autowired 
	ExitSrMdmService exitSrMdmService;
	
	@Autowired
	ExitSrProgramMappedExcelView exitSrProgramMappedExcelView;
		
	@RequestMapping(value="/admin/exitSrCertificateMappingForm" ,method= { RequestMethod.GET})
	public ModelAndView exitSrCertificateMapping(HttpServletRequest request, HttpServletResponse response,@ModelAttribute ProgramsBean programsBean) {
		if (!checkSession(request, response)) {
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView modelview=new ModelAndView("mdm/exitSrCertificateMapping");
		try{
		
		ArrayList<ConsumerType> consumerTypeListData = exitSrMdmService.getConsumerTypeList();
		ArrayList<ProgramsBean>listOfSrExitData=exitSrMdmService.getListOfNewMappedPrograms();;
		
		modelview.addObject("getMappedCertificateData",listOfSrExitData);
		modelview.addObject("rowCount",listOfSrExitData.size());
		modelview.addObject("consumerTypeListData",consumerTypeListData);
		modelview.addObject("ProgramsBean",new ProgramsBean());		
		return modelview;
		}
		catch(Exception e) {}
		modelview.addObject("ProgramsBean",new ProgramsBean());		
		return modelview;
	}
	
	@RequestMapping(value = "/admin/exitSrCertificateMapping", method = {RequestMethod.POST })
	public ModelAndView saveExitSrCertificateMapping(HttpServletRequest request, HttpServletResponse response,@ModelAttribute ProgramsBean programsBean) throws IOException  {
		if (!checkSession(request, response)) {
			redirectToPortalApp(response);
			return null;
		}
		String userId = (String) request.getSession().getAttribute("userId");
		ModelAndView modelview=new ModelAndView("mdm/exitSrCertificateMapping");
		try {			
		String consumerprogramStructureId=exitSrMdmService.getMasterkey(programsBean.getProgram(),programsBean.getProgramStructure(),programsBean.getConsumerType());
          boolean saveCount=false;
	   if(!StringUtils.isBlank(consumerprogramStructureId)){
			saveCount = exitSrMdmService.insertCertificateByMasterkey(consumerprogramStructureId,programsBean.getSem(),programsBean.getNewConsumerProgramStructureId(),userId);
				if(saveCount) {
					request.setAttribute("success", "true");
					request.setAttribute("successMessage", "Entries Inserted Successfully");
				}else{
					request.setAttribute("error", "true");
                	request.setAttribute("errorMessage", "Already Entry Exist");					
		          }
	     	}		
	   
	     	ArrayList<ConsumerType> consumerTypeListData = exitSrMdmService.getConsumerTypeList();
	     	ArrayList<ProgramsBean>listOfSrExitData=exitSrMdmService.getListOfNewMappedPrograms();;
			
			modelview.addObject("getMappedCertificateData",listOfSrExitData);
			modelview.addObject("consumerTypeListData",consumerTypeListData);
			modelview.addObject("rowCount",listOfSrExitData.size());
			modelview.addObject("ProgramsBean",new ProgramsBean());					
			return modelview;	   
		} catch (Exception e) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Internal server issue please refresh page!");
			return modelview;
		}		
	}
	
	@RequestMapping(value = "/admin/getTotalSemByProgramName",  method = RequestMethod.POST , consumes="application/json", produces="application/json")
	public ResponseEntity<ResponseBean> getTotalSemByProgramName (@RequestBody ProgramsBean programsBean){
		ResponseBean response = (ResponseBean) new ResponseBean();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		try {
			String consumerprogramStructureId=exitSrMdmService.getMasterkey(programsBean.getProgramname(),programsBean.getProgramStructure(),programsBean.getConsumerType());
		    int sem=exitSrMdmService.getTotalSemByMasterKey(consumerprogramStructureId);
			response.setCode(sem);
			return new ResponseEntity(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setCode(0);
		  return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}		
	}
	
	
	@RequestMapping(value = "/admin/updateSemCertificateExitprogram",  method = RequestMethod.POST , consumes="application/json", produces="application/json")
	public ResponseEntity<ResponseBean> updateSemCertificateExitprogram (@RequestBody ProgramsBean programsBean){
		ResponseBean response = (ResponseBean) new ResponseBean();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		try {
		    exitSrMdmService.updateSemCertificateExitprogram(programsBean.getSem(),programsBean.getNewConsumerProgramStructureId(),programsBean.getId(),programsBean.getLastModifiedBy());
			response.setStatus("Success");		
		    return new ResponseEntity(response, HttpStatus.OK);
		}catch (Exception e) {
		    return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
		     }		
		}
	
	@RequestMapping(value = "/admin/deleteSemCertificateExitprogram",  method = RequestMethod.POST , consumes="application/json", produces="application/json")
	public ResponseEntity<ResponseBean> deleteSemCertificateExitprogram (@RequestBody ProgramsBean programsBean){
		ResponseBean response = (ResponseBean) new ResponseBean();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		try {
			exitSrMdmService.deleteSemCertificateExitprogram(programsBean.getId());
			response.setStatus("Success");
			return new ResponseEntity(response, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}
	  
	  @RequestMapping(value = "/admin/getProgramBySem",  method = RequestMethod.POST , consumes="application/json", produces="application/json")
		public ResponseEntity<ArrayList<ProgramsBean>> getProgramBySem (@RequestBody ProgramsBean programsBean){
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");
			HashMap<String,ProgramsBean> mappedIdAndProgram=exitSrMdmService.getmappedIdAndProgramBean();
			ArrayList<ProgramsBean> listofNewMasterKey=new ArrayList<ProgramsBean>();
			try {
				ArrayList<String> listofConsumerProgramStructureId=exitSrMdmService.getlistofConsumerProgramStructureIdbySem(programsBean.getSem());
				for(String id:listofConsumerProgramStructureId) {
					if(!StringUtils.isBlank(id)&&!id.equals("0")) {
					listofNewMasterKey.add(mappedIdAndProgram.get(id));
					}
				}
				return new ResponseEntity(listofNewMasterKey, HttpStatus.OK);				
			}catch (Exception e) {
				return new ResponseEntity(listofNewMasterKey, HttpStatus.INTERNAL_SERVER_ERROR);
			}			
		}
	  
	  @RequestMapping(value = "/admin/downloadSRReport", method = { RequestMethod.GET, RequestMethod.POST })
		public ModelAndView downloadSRReport(HttpServletRequest request, HttpServletResponse response) {	
		  ArrayList<ProgramsBean>listOfSrExitData=exitSrMdmService.getListOfNewMappedPrograms();
		  return new ModelAndView(exitSrProgramMappedExcelView, "exitSrProgramMappedList", listOfSrExitData);
		}
	  
}
