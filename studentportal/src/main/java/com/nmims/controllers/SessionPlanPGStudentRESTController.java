package com.nmims.controllers;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nmims.beans.SessionPlanPgBean;
import com.nmims.interfaces.SessionPlanPGInterface;

@RestController
@RequestMapping("/m")
public class SessionPlanPGStudentRESTController {

	@Autowired
	SessionPlanPGInterface sessionPlanPGService;
	
	private static final Logger sessionPlanPG_logger = LoggerFactory.getLogger("sessionPlanPG");

	@GetMapping("/getStudentSessionPlanDetails")
	public ResponseEntity<SessionPlanPgBean> getSessionPlanPgDetails(@RequestParam String programSemSubjectId,
			@RequestParam String sapId) {
		SessionPlanPgBean sessionPlanPgBean = new SessionPlanPgBean();
		try {
			sessionPlanPgBean = sessionPlanPGService.fetchModuleDetails(programSemSubjectId,sapId);
			return new ResponseEntity<SessionPlanPgBean>(sessionPlanPgBean, HttpStatus.OK);
		} catch (Exception e) {
			sessionPlanPG_logger.error("METHOD : fetchModuleDetails(). Error occured while fetching module details for progarmSemSubjectId : "+programSemSubjectId + " Error : "+e.getMessage());
			return new ResponseEntity<SessionPlanPgBean>(sessionPlanPgBean, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
