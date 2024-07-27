package com.nmims.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.nmims.beans.BodBean;
import com.nmims.beans.MettlFetchTestResultBean;
import com.nmims.beans.MettlResultsSyncBean;
import com.nmims.beans.MettlStudentTestInfo;
import com.nmims.beans.ResponseListBean;
import com.nmims.services.MettlTeeMarksService;
import com.nmims.services.PGApplyBODService;
import com.nmims.helpers.ExcelHelper;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/admin/m")
public class MettlResultsRestController {
	
	@Value("${SERVER}")
	private String SERVER;
	
	@Autowired
	private MettlTeeMarksService mettlTeeMarksService;
	
	@Autowired
	private PGApplyBODService pgApplyBODService;
	
	@Autowired private ExcelHelper excelHelper;
	
	public static final Logger applybodPG = LoggerFactory.getLogger("applybod-PG");

	@PostMapping(value = "/pullMettlMarksForTeeExams")
	public ResponseEntity<MettlFetchTestResultBean> pullMettlMarksForTeeExams(@ModelAttribute MettlResultsSyncBean inputBean) {
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		MettlFetchTestResultBean response =  new MettlFetchTestResultBean();
		if(inputBean.getFileData() != null && !inputBean.getFileData().isEmpty()) {
			mettlTeeMarksService.readExcelMettlExamInput(inputBean);
		}
		
		response = mettlTeeMarksService.pullMettlMarksRestCall(inputBean);
		
		return new ResponseEntity<>(response, headers, HttpStatus.OK);
	}
	
	@PostMapping(value = "/pullMettlMarks")
	public ResponseEntity<MettlFetchTestResultBean> pullMettlMarks(HttpServletRequest request, @RequestBody MettlResultsSyncBean inputBean) {
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		MettlFetchTestResultBean response =  new MettlFetchTestResultBean();
		
		response = mettlTeeMarksService.pullMarksFromMettlAPI(inputBean);
		return new ResponseEntity<>(response, headers, HttpStatus.OK);
	}
	
	@GetMapping(value = "/getPullProcessStatus")
	public ResponseEntity<MettlFetchTestResultBean> getPullProcessStatus() {
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		MettlFetchTestResultBean response =  new MettlFetchTestResultBean();
		
		response = mettlTeeMarksService.getPullProcessStatus();
		return new ResponseEntity<>(response, headers, HttpStatus.OK);
	}
	
	@GetMapping(value = "/getPullProcessStatusRestCall")
	public ResponseEntity<MettlFetchTestResultBean> getPullProcessStatusRestCall() {
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		MettlFetchTestResultBean response =  new MettlFetchTestResultBean();
		
		response = mettlTeeMarksService.getPullTaskStatus();
		return new ResponseEntity<>(response, headers, HttpStatus.OK);
	}
	
	@PostMapping(value="/applyBodForTEE")
	public ResponseEntity<ResponseListBean> applyBodForTEE(HttpServletRequest request, @RequestBody BodBean inputBod)
	{
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		ResponseListBean response = new ResponseListBean();
		try
		{
			List<MettlStudentTestInfo> successList = new ArrayList<MettlStudentTestInfo>();
			List<MettlStudentTestInfo> errorList = new ArrayList<MettlStudentTestInfo>();
			applybodPG.info("Applying Bod for:"+inputBod.getExamYear()+":"+inputBod.getExamMonth());
			response=pgApplyBODService.applyBOD(successList, errorList,inputBod.getQuestionIdList(),inputBod.getExamYear(),inputBod.getExamMonth());
			applybodPG.info(
					"\n"+SERVER+":  applyBOD " 
					+ " Finished Processing."
					+ " Successful inserts : " + response.getSuccessList().size()
					+ " Failed inserts : " + response.getErrorList().size()
					+ " Failed inserts JSON : " + new Gson().toJson(response.getErrorList())
				);
			return new ResponseEntity<>(response,headers,HttpStatus.OK);
		}
		catch(Exception e)
		{
			applybodPG.info("Exception is:"+e.getMessage());
			return new ResponseEntity<>(response,headers,HttpStatus.OK);
		}
	}
	
	@PostMapping(value="/applyBod")
	public ResponseEntity<ResponseListBean> applyBod(HttpServletRequest request,BodBean bod){
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		ResponseListBean response = new ResponseListBean();
		try
		{
			ArrayList<String> excelList = new ArrayList<String>();
			ArrayList<String> questionIdsList= excelHelper.readExcelBodInput(bod);
			if(questionIdsList!=null)
			{
				bod.setQuestionIdList(questionIdsList);
				response=pgApplyBODService.applyBodRestCall(bod);
				if(response.getErrorList()!=null && response.getErrorList().size()>0)
				{
					for(int i=0;i<=response.getErrorList().size()-1;i++)
					{
						MettlStudentTestInfo info = response.getErrorList().get(i);
						String questionId = info.getErrorQuestionId();
						if(!excelList.contains(questionId))
						{
							excelList.add(questionId);
						}
					}
					request.getSession().setAttribute("applyBodErrorListDownload", excelList);
				}
			}
			else
			{
				applybodPG.info("check excel file data");
				response.setError("check excel file data");
			}
			return new ResponseEntity<>(response,headers,HttpStatus.OK);
		}
		catch(Exception e)
		{
			applybodPG.info("Exception is:"+e.getMessage());
			response.setError(e.getMessage());
			return new ResponseEntity<>(response,headers,HttpStatus.OK);
		}	
		
	}
	
	@PostMapping(value = "/insertBodQuestionIds", produces = "application/json")
	public ResponseEntity<Map<String, String>> insertBodQuestionIds(BodBean requestBean) {

		List<String> questionIdsList = null;
		List<String> questionIdsWithoutDuplicate = null;
		int rowsInserted = 0;

		applybodPG.info("insertBodQuestionIds ----- START : {} ", requestBean);

		try {
			questionIdsList = excelHelper.readExcelBodInput(requestBean);

			questionIdsWithoutDuplicate = questionIdsList.stream().distinct().collect(Collectors.toList());

			rowsInserted = pgApplyBODService.insertBodQuestionIds(requestBean.getExamYear(), requestBean.getExamMonth(), requestBean.getCreatedBy(), questionIdsWithoutDuplicate);

			return createResponseEntityWithPayload("success", rowsInserted + " Question Ids inserted.", HttpStatus.OK);

		} catch (DuplicateKeyException de) {
			return createResponseEntityWithPayload("error", "Question Ids already exist for selected exam cycle!", HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			return createResponseEntityWithPayload("error", "error inserting question ids : " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			questionIdsList = null;
			questionIdsWithoutDuplicate = null;
		}

	}

	private static ResponseEntity<Map<String, String>> createResponseEntityWithPayload(String status, String message, HttpStatus httpStatus) {
		applybodPG.info("insertBodQuestionIds ----- {} : {}", status, message);
		Map<String, String> responseMap = new HashMap<>(2);
		responseMap.put(status, message);
		return new ResponseEntity<Map<String, String>>(responseMap, httpStatus);
	}
	
}
