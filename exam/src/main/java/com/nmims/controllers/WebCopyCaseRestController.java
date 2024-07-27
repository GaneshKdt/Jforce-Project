package com.nmims.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.nmims.assembler.ObjectConverter;
import com.nmims.beans.AssignmentFileBean;
import com.nmims.beans.ExamAssignmentResponseBean;
import com.nmims.beans.Page;
import com.nmims.beans.ResultDomain;
import com.nmims.daos.AssignmentsDAO;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.dto.UnMarkCopyCasesDTO;
import com.nmims.helpers.CopyCaseHelper;
import com.nmims.helpers.ExcelHelper;
import com.nmims.services.AssignmentService;

@Controller
@RequestMapping("/m")
public class WebCopyCaseRestController {
	
	
	@Autowired
	CopyCaseHelper copyCaseHelper;
	
	@Autowired
	AssignmentController assignController;
	
	@Autowired
	AssignmentService asgService;
	
	@Autowired
	AssignmentsDAO asignmentsDAO;
	
	@Value("#{'${CURRENT_YEAR_LIST}'.split(',')}")
	private ArrayList<String> currentYearList;
	
	private final int pageSize = 20;
	private static final Logger assignmentWebCopyCaseCaseLogger = LoggerFactory.getLogger("assignmentWebCopyCase");
	
	
	@PostMapping(path = "/admin/webCopyCaseCheck")
	public ResponseEntity<ExamAssignmentResponseBean> WebCopyCaseCheck(HttpServletRequest request,final AssignmentFileBean searchBean) throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		ExamAssignmentResponseBean response = new ExamAssignmentResponseBean();
		//request.getSession().setAttribute("searchBean", searchBean);
		
		// Read subject list from excel report.
		ExcelHelper excelHelper = new ExcelHelper();
		List<String> subjectList=new ArrayList<String>();
		subjectList.add("Advanced Financial Accounting");
		try {
			subjectList= excelHelper.readCCSubjectListFromExcelForCheckWebCC(searchBean.getFileData());
		}
		catch (Exception e) {
			e.printStackTrace();
			assignmentWebCopyCaseCaseLogger.error("Error : While reading subject's from excel"+e.getMessage());
			response.setError("Error : While reading subject's from excel"+e.getMessage());
		}
		searchBean.setFileData(null); // To avoid SerializationFailedException 
		if(subjectList != null && subjectList.size() > 0 ) {
			searchBean.setSubjectList(subjectList);
			assignmentWebCopyCaseCaseLogger.info("------------------Started CopyCase Selected Subject Count:{}  || Month-Year:{}-{} || Threshold 1-Threshold 2: {}-{} || Selected Subject List:{}-----------------",
					searchBean.getSubjectList().size(), searchBean.getMonth(), searchBean.getYear(),searchBean.getMinMatchPercent(),searchBean.getThreshold2(),searchBean.getSubjectList());
			
			if(StringUtils.isBlank(searchBean.getYear()) || StringUtils.isBlank(searchBean.getMonth()) || StringUtils.isBlank(searchBean.getSubjectList().toString())){
				response.setError("Error : Please enter all the fields before processing");
			}
			
			try {
				String userId = (String) request.getSession().getAttribute("userId");
				searchBean.setLastModifiedBy(userId); // save in dao calls
				
				List<String> NoRecordsSubjectsList = new ArrayList<String>();
				List<String> OneRecordSubjectsList = new ArrayList<String>();
				List<String> CCProcessedSubjectsList = new ArrayList<String>();
				List<String> CCNotProcessedSubjectsList = new ArrayList<String>();
				int subjectCount = 1;
				
				
				for (String subject : searchBean.getSubjectList()) {
					searchBean.setSubject(subject);
					assignmentWebCopyCaseCaseLogger.info("------------------ Started CopyCase SubjectCount:{}/{} Subject:{}  || Month-Year:{}-{} || Threshold 1-Threshold 2: {}-{} -----------------",
							subjectCount, searchBean.getSubjectList().size(), searchBean.getSubject(), searchBean.getMonth(), searchBean.getYear(),searchBean.getMinMatchPercent(), searchBean.getThreshold2());
					
					//Page<AssignmentFileBean> page = new Page<AssignmentFileBean>();
					List<AssignmentFileBean> assignmentFilesList=new ArrayList<AssignmentFileBean>();
					try {
					//	page = dao.getAssignmentSubmissionPage(1, Integer.MAX_VALUE, searchBean);
						assignmentFilesList=asignmentsDAO.getAssignmentSubmissionPage(searchBean);
					} catch (Exception e1) {
						System.out.println("Error in get student in from db ");
						 e1.printStackTrace();
						assignmentWebCopyCaseCaseLogger.error("Exception Error getAssignmentSubmissionPage() SubjectCount:{}/{} Subject:{} || Month-Year:{}-{} || Threshold 1-Threshold 2: {}-{} || Error:{}",
								subjectCount, searchBean.getSubjectList().size(), searchBean.getSubject(), searchBean.getMonth(), searchBean.getYear(),searchBean.getMinMatchPercent(), searchBean.getThreshold2(), e1);
					}
					//List<AssignmentFileBean> assignmentFilesList = page.getPageItems();
					assignmentWebCopyCaseCaseLogger.info("Student Assignment files found size:{} || SubjectCount:{}/{} Subject:{} || Month-Year:{}-{} || Threshold 1-Threshold 2: {}-{}",
							assignmentFilesList.size(), subjectCount, searchBean.getSubjectList().size(), searchBean.getSubject(), searchBean.getMonth(),searchBean.getYear(), searchBean.getMinMatchPercent(), searchBean.getThreshold2());
					
					if (assignmentFilesList == null || assignmentFilesList.size() == 0) {
						assignmentWebCopyCaseCaseLogger.error("Error : No records found!!!! SubjectCount:{}/{} Subject:{} || Month-Year:{}-{} || Threshold 1-Threshold 2: {}-{}", subjectCount, searchBean.getSubjectList().size(), searchBean.getSubject(), searchBean.getMonth(),searchBean.getYear(), searchBean.getMinMatchPercent(), searchBean.getThreshold2());
						NoRecordsSubjectsList.add(subject);
					}
					else if(assignmentFilesList.size() == 1) {
						assignmentWebCopyCaseCaseLogger.error("Error : Only 1 records found!!!! SubjectCount:{}/{} Subject:{} || Month-Year:{}-{} || Threshold 1-Threshold 2: {}-{}", subjectCount, searchBean.getSubjectList().size(), searchBean.getSubject(), searchBean.getMonth(),searchBean.getYear(), searchBean.getMinMatchPercent(), searchBean.getThreshold2());
						OneRecordSubjectsList.add(subject);
					} else {
						//if subject students submitted list is more than 1 then process for further CC process.
						
						try {
							assignmentWebCopyCaseCaseLogger.info(
									"--------------------CopyCase Algorithm Started SubjectCount:{}/{} Subject:{} || Month-Year:{}-{} || Threshold 1-Threshold 2: {}-{}-------------------",
									subjectCount, searchBean.getSubjectList().size(), searchBean.getSubject(), searchBean.getMonth(), searchBean.getYear(),
									searchBean.getMinMatchPercent(), searchBean.getThreshold2());
							copyCaseHelper.cehckCopycaseOnWeb(assignmentFilesList, searchBean);
							
							CCProcessedSubjectsList.add(subject);
						} catch (Exception e) {
							assignmentWebCopyCaseCaseLogger.error(
									"Exception Error CopyCase SubjectCount:{}/{} Subject:{} || Month-Year:{}-{} || Threshold 1-Threshold 2: {}-{} || Error:{}",
									subjectCount, searchBean.getSubjectList().size(), searchBean.getSubject(), searchBean.getMonth(), searchBean.getYear(),
									searchBean.getMinMatchPercent(), searchBean.getThreshold2(), e);
							CCNotProcessedSubjectsList.add(subject);
						}
						
						
					}
					assignmentWebCopyCaseCaseLogger.info("------------------ Ended CopyCase SubjectCount:{}/{} Subject:{}  || Month-Year:{}-{} || Threshold 1-Threshold 2: {}-{} -----------------",
							subjectCount, searchBean.getSubjectList().size(), searchBean.getSubject(), searchBean.getMonth(), searchBean.getYear(),searchBean.getMinMatchPercent(), searchBean.getThreshold2());
					subjectCount++;
				}
				
				String NoRecordsSubjectsError = "";
				String CCErrorSubjects = "";
				String finalErrorMessage = "";
				if(NoRecordsSubjectsList.size() > 0) {
					NoRecordsSubjectsError = "Error : No records found!!!! Subject List: " +NoRecordsSubjectsList;
					assignmentWebCopyCaseCaseLogger.error(NoRecordsSubjectsError);
					finalErrorMessage = NoRecordsSubjectsError +"<br>";
				}
				if(CCNotProcessedSubjectsList.size() > 0) {
					CCErrorSubjects = "Error : CC Not Processed!!!! Subject List: " +CCNotProcessedSubjectsList;
					assignmentWebCopyCaseCaseLogger.error(CCErrorSubjects);
					finalErrorMessage = finalErrorMessage + CCErrorSubjects;
				}
				if(finalErrorMessage !="") {
					response.setError(finalErrorMessage);
				}
				if(CCProcessedSubjectsList.size() > 0) {
					response.setSuccess("true");
					response.setSuccessMessage(" Successfully Copy Case processed Subject List: " + CCProcessedSubjectsList);
					assignmentWebCopyCaseCaseLogger.info(" Successfully Copy Case processed Subject List: {}", CCProcessedSubjectsList);
				}
				//			response.setSuccess("Copy check procedure initiated successfully. File will be created and saved on Server disk");
				assignmentWebCopyCaseCaseLogger.info("------------------ Ended CopyCase Selected Subject Count:{}  || Month-Year:{}-{} || Threshold 1-Threshold 2: {}-{} || Selected Subject List:{}-----------------",
						searchBean.getSubjectList().size(), searchBean.getMonth(), searchBean.getYear(),searchBean.getMinMatchPercent(), searchBean.getThreshold2(), searchBean.getSubjectList());
				// return new ModelAndView("copyCaseExcelView","copyResult",copyResult);
			} catch (Exception e) {
				assignmentWebCopyCaseCaseLogger.error(
						"Exception Error CopyCase Selected Subject Count:{} || Month-Year:{}-{} || Threshold 1-Threshold 2: {}-{} || Selected Subject List:{} || Error:{}",
						searchBean.getSubjectList().size(), searchBean.getMonth(), searchBean.getYear(),
						searchBean.getMinMatchPercent(), searchBean.getThreshold2(), searchBean.getSubjectList(), e);
				response.setError("Error: " + e.getMessage());
			}
		}else {
			assignmentWebCopyCaseCaseLogger.error(
					"No Subjects are found in Excel || Month-Year:{}-{} || Threshold 1-Threshold 2: {}-{}",
					searchBean.getMonth(), searchBean.getYear(),
					searchBean.getMinMatchPercent(), searchBean.getThreshold2());
			response.setError("Error: No Subjects are found in Excel");
		}
		return new ResponseEntity<ExamAssignmentResponseBean>(response, headers, HttpStatus.OK);
		
	}
	
	

}
