package com.nmims.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import com.nmims.beans.AssignmentFileBean;
import com.nmims.beans.ExamAssignmentResponseBean;
import com.nmims.beans.Page;
import com.nmims.daos.AssignmentsDAO;
import com.nmims.helpers.CopyCaseHelper;
import com.nmims.services.ProjectStudentEligibilityService;

@RestController
@RequestMapping("m")
public class ProjectRESTController {

	@Autowired
	ApplicationContext act;
	
	@Autowired
	CopyCaseHelper copyCaseHelper;
	
	@Value("#{'${CURRENT_YEAR_LIST}'.split(',')}")
	private ArrayList<String> currentYearList;
	
	@Value("#{'${EXAM_MONTH_LIST}'.split(',')}")
	private List<String> EXAM_MONTH_LIST;
	
	@Autowired
	ProjectStudentEligibilityService projectService;
	
	private static final Logger projectCCLggger = LoggerFactory.getLogger("projectCopyCase");
	
	@PostMapping(path ="/admin/projectCopyCaseCheck", headers = "content-type=multipart/form-data")
	public ResponseEntity<ExamAssignmentResponseBean> projectCopyCaseCheck(HttpServletRequest request, final AssignmentFileBean searchBean) throws Exception{
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		ExamAssignmentResponseBean response = new ExamAssignmentResponseBean();
		request.getSession().setAttribute("searchBean", searchBean);
		projectCCLggger.info("------------------Started CopyCase Selected Subject Count:{}  || Month-Year:{}-{} || Threshold 1-Threshold 2: {}-{} || Selected Subject List:{}-----------------",
				searchBean.getSubjectList().size(), searchBean.getMonth(), searchBean.getYear(),searchBean.getMinMatchPercent(),searchBean.getThreshold2(),searchBean.getSubjectList());
		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		if(StringUtils.isBlank(searchBean.getYear()) || StringUtils.isBlank(searchBean.getMonth()) || StringUtils.isBlank(searchBean.getSubjectList().toString())){
			response.setError("true");
			response.setErrorMessage("Error : Please enter all the fields before processing");
		}
		try {
			String userId = (String) request.getSession().getAttribute("userId");
			searchBean.setLastModifiedBy(userId); // save in dao calls

			List<String> NoRecordsSubjectsList = new ArrayList<String>();
			List<String> OneRecordSubjectsList = new ArrayList<String>();
			List<String> CCProcessedSubjectsList = new ArrayList<String>();
			List<String> CCNotProcessedSubjectsList = new ArrayList<String>();
			for (String subject : searchBean.getSubjectList()) {
				searchBean.setSubject(subject);
				projectCCLggger.info("------------------ Started CopyCase Subject:{}  || Month-Year:{}-{} || Threshold 1-Threshold 2: {}-{} -----------------",
						searchBean.getSubject(), searchBean.getMonth(), searchBean.getYear(),searchBean.getMinMatchPercent(), searchBean.getThreshold2());

				List<AssignmentFileBean> projectFilesList = null;
				try {
					projectFilesList = projectService.getProjectSubmission(searchBean.getSubject(), searchBean.getMonth(), searchBean.getYear(), searchBean.getSapId());
				} catch (Exception e1) {
					// e1.printStackTrace();
					projectCCLggger.error("Exception Error getProjectSubmission() Subject:{} || Month-Year:{}-{} || Threshold 1-Threshold 2: {}-{} || Error:{}",
							searchBean.getSubject(), searchBean.getMonth(), searchBean.getYear(),searchBean.getMinMatchPercent(), searchBean.getThreshold2(), e1);
				}
				if (projectFilesList == null || projectFilesList.size() == 0) {
					projectCCLggger.error("Error : No records found!!!! Subject:{} || Month-Year:{}-{} || Threshold 1-Threshold 2: {}-{}", searchBean.getSubject(), searchBean.getMonth(),searchBean.getYear(), searchBean.getMinMatchPercent(), searchBean.getThreshold2());
					NoRecordsSubjectsList.add(subject);
				}
				else if(projectFilesList.size() == 1) {
					projectCCLggger.error("Error : Only 1 records found!!!! Subject:{} || Month-Year:{}-{} || Threshold 1-Threshold 2: {}-{}", searchBean.getSubject(), searchBean.getMonth(),searchBean.getYear(), searchBean.getMinMatchPercent(), searchBean.getThreshold2());
					OneRecordSubjectsList.add(subject);
				} else {
					
					//if subject students submitted list is more than 1 then process for further CC process.
					projectCCLggger.info("Student submitted files found size:{} || Subject:{} || Month-Year:{}-{} || Threshold 1-Threshold 2: {}-{}",
							projectFilesList.size(), searchBean.getSubject(), searchBean.getMonth(),searchBean.getYear(), searchBean.getMinMatchPercent(), searchBean.getThreshold2());
					try {
						projectCCLggger.info(
								"--------------------CopyCase Algorithm Started Subject:{} || Month-Year:{}-{} || Threshold 1-Threshold 2: {}-{}-------------------",
								searchBean.getSubject(), searchBean.getMonth(), searchBean.getYear(),
								searchBean.getMinMatchPercent(), searchBean.getThreshold2());
						copyCaseHelper.checkCopyCasesForProject(projectFilesList, searchBean);
						CCProcessedSubjectsList.add(subject);
					} catch (Exception e) {
						projectCCLggger.error(
								"Exception Error CopyCase Subject:{} || Month-Year:{}-{} || Threshold 1-Threshold 2: {}-{} || Error:{}",
								searchBean.getSubject(), searchBean.getMonth(), searchBean.getYear(),
								searchBean.getMinMatchPercent(), searchBean.getThreshold2(), e);
						CCNotProcessedSubjectsList.add(subject);
					}
				}
				projectCCLggger.info("------------------ Ended CopyCase Subject:{}  || Month-Year:{}-{} || Threshold 1-Threshold 2: {}-{} -----------------",
						searchBean.getSubject(), searchBean.getMonth(), searchBean.getYear(),searchBean.getMinMatchPercent(), searchBean.getThreshold2());
			}
			
			String NoRecordsSubjectsError = "";
			String OneRecordsSubjectsError = "";
			String CCErrorSubjects = "";
			String finalErrorMessage = "";
			if(NoRecordsSubjectsList.size() > 0) {
				NoRecordsSubjectsError = "Error : No records found!!!! Subject List: " +NoRecordsSubjectsList;
				projectCCLggger.error(NoRecordsSubjectsError);
				finalErrorMessage = NoRecordsSubjectsError +"<br>";
			}
			if(OneRecordSubjectsList.size() > 0) {
				OneRecordsSubjectsError = "Error : Only one record found!!!! Subject List: " +OneRecordSubjectsList;
				projectCCLggger.error(OneRecordsSubjectsError);
				finalErrorMessage = finalErrorMessage + OneRecordsSubjectsError +"<br>";
			}
			if(CCNotProcessedSubjectsList.size() > 0) {
				CCErrorSubjects = "Error : CC Not Processed!!!! Subject List: " +CCNotProcessedSubjectsList;
				projectCCLggger.error(CCErrorSubjects);
				finalErrorMessage = finalErrorMessage + CCErrorSubjects;
			}
			if(finalErrorMessage !="") {
				response.setError(finalErrorMessage);
			}
			if(CCProcessedSubjectsList.size() > 0) {
				response.setSuccess("true");
				response.setSuccessMessage(" Successfully Copy Case processed Subject List: " + CCProcessedSubjectsList);
				projectCCLggger.info(" Successfully Copy Case processed Subject List: {}", CCProcessedSubjectsList);
			}
			projectCCLggger.info("------------------ Ended CopyCase Selected Subject Count:{}  || Month-Year:{}-{} || Threshold 1-Threshold 2: {}-{} || Selected Subject List:{}-----------------",
					searchBean.getSubjectList().size(), searchBean.getMonth(), searchBean.getYear(),searchBean.getMinMatchPercent(), searchBean.getThreshold2(), searchBean.getSubjectList());
		} catch (Exception e) {
			projectCCLggger.error(
					"Exception Error CopyCase Selected Subject Count:{} || Month-Year:{}-{} || Threshold 1-Threshold 2: {}-{} || Selected Subject List:{} || Error:{}",
					searchBean.getSubjectList().size(), searchBean.getMonth(), searchBean.getYear(),
					searchBean.getMinMatchPercent(), searchBean.getThreshold2(), searchBean.getSubjectList(), e);
			response.setError("Error: " + e.getMessage());
		}
		return new ResponseEntity<>(response, headers, HttpStatus.OK);
	}
}
