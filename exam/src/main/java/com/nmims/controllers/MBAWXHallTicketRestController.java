package com.nmims.controllers;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.nmims.beans.MBAHallTicketBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.helpers.SalesforceHelper;
import com.nmims.services.HtServiceLayerMBAWX;

@RestController
@RequestMapping("m")
public class MBAWXHallTicketRestController {
	
	@Autowired
	ApplicationContext act;

	@Autowired
	SalesforceHelper salesforceHelper;
	
	@Autowired
	HtServiceLayerMBAWX htServiceLayer;

	@Value("${MARKSHEETS_PATH}")
	private String MARKSHEETS_PATH;

	@Value("${STUDENT_PHOTOS_PATH}")
	private String STUDENT_PHOTOS_PATH;

	@Value("${HALLTICKET_PATH}")
	private String HALLTICKET_PATH;

	@Value("${SERVER_PATH}")
	private String SERVER_PATH;

	@Value("${FEE_RECEIPT_PATH}")
	private String FEE_RECEIPT_PATH;

	@Value("#{'${CORPORATE_CENTERS}'.split(',')}")
	private List<String> corporateCenterList;
	
	private HashMap<String, String> programCodeNameMap = null;

	public HashMap<String, String> getProgramMap() {
		if (this.programCodeNameMap == null || this.programCodeNameMap.size() == 0) {
			StudentMarksDAO dao = (StudentMarksDAO) act.getBean("studentMarksDAO");
			this.programCodeNameMap = dao.getProgramDetails();
		}
		return programCodeNameMap;
	}
	
	
	@PostMapping(path = "/previewHallTicket_MBAWX", produces = "application/json", consumes = "application/json")
	public ResponseEntity<MBAHallTicketBean> previewHallTicket_MBAWX(@RequestBody StudentExamBean input) throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");	

		// error handling done in service
		MBAHallTicketBean response = htServiceLayer.getHallTicketData(input.getSapid(), getProgramMap());
		return new ResponseEntity<MBAHallTicketBean>(response, headers, HttpStatus.OK);
	}
	
	@PostMapping(path = "/downloadHallTicket_MBAWX", consumes = "application/json", produces = "application/json")
	public ResponseEntity<MBAHallTicketBean> downloadHallTicket_MBAWX(@RequestBody StudentExamBean input) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");

		// error handling done in service
		MBAHallTicketBean hallTicketBean = htServiceLayer.createHallTicketDownload(input.getSapid(), getProgramMap());
		return new ResponseEntity<MBAHallTicketBean>(hallTicketBean, headers, HttpStatus.OK);
	}
	

	@PostMapping(path = "/downloadHallTicketForMbaWxStudent", consumes = "application/json", produces = "application/json")
	public ResponseEntity<MBAHallTicketBean> downloadHallTicketOld_MBAWX(@RequestBody StudentExamBean input) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");

		// error handling done in service
		MBAHallTicketBean hallTicketBean = htServiceLayer.createHallTicketDownload(input.getSapid(), getProgramMap());
		return new ResponseEntity<MBAHallTicketBean>(hallTicketBean, headers, HttpStatus.OK);
	}

}
