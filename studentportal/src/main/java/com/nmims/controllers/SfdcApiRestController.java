package com.nmims.controllers;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.nmims.beans.CashFreeResponseBean;
import com.nmims.beans.MailStudentPortalBean;
import com.nmims.daos.PortalDao;
import com.nmims.dto.StudentProfileDto;
import com.nmims.helpers.AESencrp;
import com.nmims.helpers.EmailHelper;
import com.nmims.services.SfdcApiService;

/**
 * Contains Rest API's used by SFDC to perform operations on studentportal.
 * @author Raynal Dcunha
 */
@RestController
@RequestMapping("m")
public class SfdcApiRestController {
	private static final Logger profileSyncSfdcLogger = LoggerFactory.getLogger("profileSyncSFDC");
	private static final Logger loggerPFSyncPDDM = LoggerFactory.getLogger("salesforcePFSyncPDDM");
	private static final Logger sfdcEmailsToPortalLogger = LoggerFactory.getLogger("sfdcEmailsToPortal");

	private SfdcApiService sfdcApiService;
	
	@Autowired
	ApplicationContext act;

	@Value("${CANCELLED_PROGRAM_STATUS}")
	private List<String> CANCELLED_PROGRAM_STATUS;
	
	
	/**
	 * Using Constructor Dependency Injection to inject the required dependencies.
	 * @param instances of the required dependencies
	 */
	@Autowired
	public SfdcApiRestController(SfdcApiService sfdcApiService) {
		Objects.requireNonNull(sfdcApiService);			//Fail-fast approach, field is guaranteed be non-null.
		this.sfdcApiService = sfdcApiService;
	}

	/**
	 * Updates the specified student' details in LDAP and MySQL database from the data provided via SalesForce.
	 * @param studentProfileDto - requestBody containing fields of the required student details 
	 * @return A response containing the HttpStatus and updated student details in a JSON formatted String
	 */
	@PostMapping(value = "/updateProfileFromSFDC", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> updateProfileFromSFDC(@RequestBody StudentProfileDto studentProfileDto) {
		try {
			String responseStudentDetails = sfdcApiService.updateProfileSFDC(studentProfileDto);
			profileSyncSfdcLogger.info("[END] Updated details of Student: " + studentProfileDto.getSapid() + ", from Salesforce by SalesForce Admin");

			return new ResponseEntity<>(responseStudentDetails, HttpStatus.OK);
		}
		catch(IllegalArgumentException ex) {
			profileSyncSfdcLogger.error("[END] Validation Error! Failed to update details of Student: " + studentProfileDto.getSapid() + ", cause: " + ex.getMessage());
			return new ResponseEntity<>("Validation Error! Failed to Update Profile, cause: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
		}
		catch(Exception ex) {
			profileSyncSfdcLogger.error("[END] Failed to update details of Student: " + studentProfileDto.getSapid() + ", cause: " + ex.toString());
			return new ResponseEntity<>("Failed to Update Profile, cause: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping(value = "/checkPDDMPassSemForRereg", produces = "application/json")
	public ResponseEntity<String> checkPDDMPassSemForRereg(@RequestBody StudentProfileDto studentProfileDto) {
		try {
			String sem_reReg = sfdcApiService.checkPDDMPassSemForRereg(studentProfileDto.getSapid());
			return new ResponseEntity<String>(sem_reReg, HttpStatus.OK);
		}
		catch(IllegalArgumentException ex) {	
			loggerPFSyncPDDM.error("[END] checkPDDMPassSemForRereg: " + studentProfileDto.getSapid() + ", cause: " + ex.getMessage());
			return new ResponseEntity<>("checkPDDMPassSemForRereg, cause: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
		}
		catch (Exception ex) {
			loggerPFSyncPDDM.error("[END] checkPDDMPassSemForRereg: " + studentProfileDto.getSapid() + ", cause: " + ex.toString());
			return new ResponseEntity<String>("checkPDDMPassSemForRereg, cause: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping(path = "/updateStudentPortalProgramStatus")
	public @ResponseBody HashMap<String, String> updateStudentPortalProgramStatus(@RequestBody JSONObject inputJsonObj) {
		HashMap<String, String> response = new HashMap<String, String>();
		response.put("success", "false");
		String sapid = (String)inputJsonObj.get("sapid");
		String programStatus = (String)inputJsonObj.get("programStatus");
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		try {
			if(StringUtils.isBlank(sapid) && StringUtils.isBlank(programStatus)) {
				response.put("message", "Payload is Empty.Please check Following parameters.");
				return response;
			}
			
			//check whether program status is valid or not
			if(!CANCELLED_PROGRAM_STATUS.contains(programStatus)) {
				response.put("message", "Please enter valid program status. It should be from one of the following names(Program Terminated,Program Withdrawal,Program Suspension,Rusticated) ");
				return response;
			}
			if(pDao.updateProgramStatus(sapid,programStatus) == 1) {
				response.put("success", "true");
				response.put("message", "SuccessFully Added!");
			}else
				response.put("message", "Not able to update program status for this sapid "+sapid);
		}catch(Exception e) {
			e.printStackTrace();
			response.put("message", "Error while updating  program status for this sapid  "+sapid+"! Error:- "+e.getMessage());
			profileSyncSfdcLogger.error("Error while updating  program status for this sapid  "+sapid,e);
		}
		return response;
	}

	@PostMapping("/insertEmailsEntryToPortal")
	public ResponseEntity<Map<String, String>> insertEmailsEntryToPortal(@RequestBody MailStudentPortalBean mailStudentPortalBean){
		Map<String, String> response = new HashMap<>();
		sfdcEmailsToPortalLogger.info("START : insertEmailsEntryToPortal execution for sapId : "+mailStudentPortalBean.getSapid());
		sfdcEmailsToPortalLogger.info("EmailId  : Subject - "+mailStudentPortalBean.getMailId() +" : "+mailStudentPortalBean.getSubject());
		try {
			mailStudentPortalBean.setCreatedBy("Salesforce");
			mailStudentPortalBean.setLastModifiedBy("Salesforce");
			long mailTemplateId = sfdcApiService.createRecordInUserMailTableAndMailTable(mailStudentPortalBean);
			response.put("status", "success");
			response.put("message", "Data inserted successfully. The mailTemplateId is "+mailTemplateId);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}catch (Exception e) {
			sfdcEmailsToPortalLogger.error("Error in insertEmailsEntryToPortal execution : "+e.getMessage());
			response.put("status", "error");
			response.put("message", "Error : "+e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping(path = "/getCashFreeTokenForSFDC", produces="application/json")
	public ResponseEntity<CashFreeResponseBean> getCashFreeTokenForSFDC(@RequestParam final String type) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		CashFreeResponseBean responseBean = new CashFreeResponseBean();
		responseBean = sfdcApiService.getCashFreeToken(type);
		if(responseBean.getMessage()!=null && StringUtils.isNotBlank(responseBean.getMessage()) && responseBean.getStatus().equalsIgnoreCase("error")) {
			profileSyncSfdcLogger.error("CashFree Token Error: {}",responseBean.getMessage());
			return new ResponseEntity<CashFreeResponseBean>(responseBean,headers,HttpStatus.UNAUTHORIZED);
		}else {
			return new ResponseEntity<CashFreeResponseBean>(responseBean,headers,HttpStatus.OK);
		}
	}
}
