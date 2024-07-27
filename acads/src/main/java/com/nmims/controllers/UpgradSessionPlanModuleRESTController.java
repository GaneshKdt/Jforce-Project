package com.nmims.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.nmims.beans.ResponseAcadsBean;
import com.nmims.beans.SessionPlanModuleBean;
import com.nmims.services.UpgradSessionPlanModuleService;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UpgradSessionPlanModuleRESTController {
	
	@Autowired
	UpgradSessionPlanModuleService upgradSessionPlanModuleService;

	@RequestMapping(value = "/upsertSessionPlanModule", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<ResponseAcadsBean> saveSessionPlanModule(@RequestBody SessionPlanModuleBean module) {
		ResponseAcadsBean responseBean = new ResponseAcadsBean();
		if( "UPSERT".equals(module.getAction()) ) {
			responseBean  = upgradSessionPlanModuleService.saveSessionPlanModuleService(module);
			return new ResponseEntity<ResponseAcadsBean>(responseBean, HttpStatus.OK);
		} 
		 else if( "DELETE".equals(module.getAction()) ) {
			 responseBean  = upgradSessionPlanModuleService.deleteSessionPlanModuleCascadeSevice(module); 			
			return new ResponseEntity<ResponseAcadsBean>(responseBean, HttpStatus.OK);
		} else {
			responseBean.setStatus("fail");
			responseBean.setMessage("Action filed is wrong, Please check and Try Again !!!");
			responseBean.setCode(422);
			return new ResponseEntity<ResponseAcadsBean>(responseBean, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	@RequestMapping(value = "/m/upsertSessionPlanModule", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<ResponseAcadsBean> msaveSessionPlanModule(@RequestBody SessionPlanModuleBean module) {
		ResponseAcadsBean responseBean = new ResponseAcadsBean();
		if( "UPSERT".equals(module.getAction()) ) {
			responseBean  = upgradSessionPlanModuleService.saveSessionPlanModuleService(module);
			return new ResponseEntity<ResponseAcadsBean>(responseBean, HttpStatus.OK);
		} 
		 else if( "DELETE".equals(module.getAction()) ) {
			 responseBean  = upgradSessionPlanModuleService.deleteSessionPlanModuleCascadeSevice(module); 			
			return new ResponseEntity<ResponseAcadsBean>(responseBean, HttpStatus.OK);
		} else {
			responseBean.setStatus("fail");
			responseBean.setMessage("Action filed is wrong, Please check and Try Again !!!");
			responseBean.setCode(422);
			return new ResponseEntity<ResponseAcadsBean>(responseBean, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
}