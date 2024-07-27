package com.nmims.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.nmims.assembler.ObjectConverter;
import com.nmims.beans.AssignmentFileBean;
import com.nmims.beans.ExamAssignmentResponseBean;
import com.nmims.beans.Page;
import com.nmims.daos.AssignmentsDAO;
import com.nmims.dto.UnMarkCopyCasesDTO;
import com.nmims.helpers.CopyCaseHelper;
import com.nmims.helpers.ExcelHelper;
import com.nmims.services.AssignmentService;

@RestController
@RequestMapping("m")
public class AssignmentRESTController {
	
	@Autowired
	ApplicationContext act;
	
	@Autowired
	CopyCaseHelper copyCaseHelper;
	
	@Autowired
	AssignmentController assignController;
	
	@Autowired
	AssignmentService asgService;
	
	@Value("#{'${CURRENT_YEAR_LIST}'.split(',')}")
	private ArrayList<String> currentYearList;
	
	private final int pageSize = 20;
	private static final Logger assignmentCopyCaseLogger = LoggerFactory.getLogger("assignmentCopyCase");

//	@PostMapping(path = "/admin/copyCaseCheck", headers = "content-type=multipart/form-data")
	@PostMapping(path = "/admin/copyCaseCheck")
	public ResponseEntity<ExamAssignmentResponseBean> copyCaseCheck(HttpServletRequest request,final AssignmentFileBean searchBean) throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		ExamAssignmentResponseBean response = new ExamAssignmentResponseBean();
		request.getSession().setAttribute("searchBean", searchBean);
		
		// Read subject list from excel report.
		ExcelHelper excelHelper = new ExcelHelper();
		List<String> subjectList= excelHelper.readCCSubjectListFromExcel(searchBean.getFileData());
		searchBean.setFileData(null); // To avoid SerializationFailedException 
		if(subjectList != null && subjectList.size() > 0 ) {
			searchBean.setSubjectList(subjectList);
			assignmentCopyCaseLogger.info("------------------Started CopyCase Selected Subject Count:{}  || Month-Year:{}-{} || Threshold 1-Threshold 2: {}-{} || Selected Subject List:{}-----------------",
					searchBean.getSubjectList().size(), searchBean.getMonth(), searchBean.getYear(),searchBean.getMinMatchPercent(),searchBean.getThreshold2(),searchBean.getSubjectList());
			
			AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
			if(StringUtils.isBlank(searchBean.getYear()) || StringUtils.isBlank(searchBean.getMonth()) || StringUtils.isBlank(searchBean.getSubjectList().toString())){
				response.setError("Error : Please enter all the fields before processing");
//				response.setErrorMessage("Error : Please enter all the fields before processing");
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
					assignmentCopyCaseLogger.info("------------------ Started CopyCase SubjectCount:{}/{} Subject:{}  || Month-Year:{}-{} || Threshold 1-Threshold 2: {}-{} -----------------",
							subjectCount, searchBean.getSubjectList().size(), searchBean.getSubject(), searchBean.getMonth(), searchBean.getYear(),searchBean.getMinMatchPercent(), searchBean.getThreshold2());
	
					Page<AssignmentFileBean> page = new Page<AssignmentFileBean>();
					try {
						page = dao.getAssignmentSubmissionPage(1, Integer.MAX_VALUE, searchBean);
					} catch (Exception e1) {
//						 e1.printStackTrace();
						assignmentCopyCaseLogger.error("Exception Error getAssignmentSubmissionPage() SubjectCount:{}/{} Subject:{} || Month-Year:{}-{} || Threshold 1-Threshold 2: {}-{} || Error:{}",
								subjectCount, searchBean.getSubjectList().size(), searchBean.getSubject(), searchBean.getMonth(), searchBean.getYear(),searchBean.getMinMatchPercent(), searchBean.getThreshold2(), e1);
					}
					List<AssignmentFileBean> assignmentFilesList = page.getPageItems();
					assignmentCopyCaseLogger.info("Student Assignment files found size:{} || SubjectCount:{}/{} Subject:{} || Month-Year:{}-{} || Threshold 1-Threshold 2: {}-{}",
							assignmentFilesList.size(), subjectCount, searchBean.getSubjectList().size(), searchBean.getSubject(), searchBean.getMonth(),searchBean.getYear(), searchBean.getMinMatchPercent(), searchBean.getThreshold2());
					
					if (assignmentFilesList == null || assignmentFilesList.size() == 0) {
						assignmentCopyCaseLogger.error("Error : No records found!!!! SubjectCount:{}/{} Subject:{} || Month-Year:{}-{} || Threshold 1-Threshold 2: {}-{}", subjectCount, searchBean.getSubjectList().size(), searchBean.getSubject(), searchBean.getMonth(),searchBean.getYear(), searchBean.getMinMatchPercent(), searchBean.getThreshold2());
						NoRecordsSubjectsList.add(subject);
					}
					else if(assignmentFilesList.size() == 1) {
						assignmentCopyCaseLogger.error("Error : Only 1 records found!!!! SubjectCount:{}/{} Subject:{} || Month-Year:{}-{} || Threshold 1-Threshold 2: {}-{}", subjectCount, searchBean.getSubjectList().size(), searchBean.getSubject(), searchBean.getMonth(),searchBean.getYear(), searchBean.getMinMatchPercent(), searchBean.getThreshold2());
						OneRecordSubjectsList.add(subject);
					} else {
						//if subject students submitted list is more than 1 then process for further CC process.
						Page<AssignmentFileBean> pageQuestions = new Page<AssignmentFileBean>();
						try {
							pageQuestions = dao.getAssignmentFilesPage(1, pageSize, searchBean, "distinct");
						} catch (Exception e1) {
//							 e1.printStackTrace();
							assignmentCopyCaseLogger.error(
									"Exception Error getAssignmentFilesPage() SubjectCount:{}/{} Subject:{} || Month-Year:{}-{} || Threshold 1-Threshold 2: {}-{} || Error:{}",
									subjectCount, searchBean.getSubjectList().size(), searchBean.getSubject(), searchBean.getMonth(), searchBean.getYear(),
									searchBean.getMinMatchPercent(), searchBean.getThreshold2(), e1);
						}
						List<AssignmentFileBean> assignmentQuestionFilesList = pageQuestions.getPageItems();
						assignmentCopyCaseLogger.info(
								"Assignment QP files found size:{} || SubjectCount:{}/{} Subject:{} || Month-Year:{}-{} || Threshold 1-Threshold 2: {}-{}",
								assignmentQuestionFilesList.size(), subjectCount, searchBean.getSubjectList().size(), searchBean.getSubject(), searchBean.getMonth(),
								searchBean.getYear(), searchBean.getMinMatchPercent(), searchBean.getThreshold2());
	
						try {
							assignmentCopyCaseLogger.info(
									"--------------------CopyCase Algorithm Started SubjectCount:{}/{} Subject:{} || Month-Year:{}-{} || Threshold 1-Threshold 2: {}-{}-------------------",
									subjectCount, searchBean.getSubjectList().size(), searchBean.getSubject(), searchBean.getMonth(), searchBean.getYear(),
									searchBean.getMinMatchPercent(), searchBean.getThreshold2());
							// copyResult = copyCaseHelper.checkCopyCases(assignmentFilesList);
							copyCaseHelper.checkAssignmentCopyCases(assignmentFilesList, searchBean, assignmentQuestionFilesList);
							CCProcessedSubjectsList.add(subject);
						} catch (Exception e) {
							assignmentCopyCaseLogger.error(
									"Exception Error CopyCase SubjectCount:{}/{} Subject:{} || Month-Year:{}-{} || Threshold 1-Threshold 2: {}-{} || Error:{}",
									subjectCount, searchBean.getSubjectList().size(), searchBean.getSubject(), searchBean.getMonth(), searchBean.getYear(),
									searchBean.getMinMatchPercent(), searchBean.getThreshold2(), e);
							CCNotProcessedSubjectsList.add(subject);
						}
						
						
					}
					assignmentCopyCaseLogger.info("------------------ Ended CopyCase SubjectCount:{}/{} Subject:{}  || Month-Year:{}-{} || Threshold 1-Threshold 2: {}-{} -----------------",
							subjectCount, searchBean.getSubjectList().size(), searchBean.getSubject(), searchBean.getMonth(), searchBean.getYear(),searchBean.getMinMatchPercent(), searchBean.getThreshold2());
					subjectCount++;
				}
				
				String NoRecordsSubjectsError = "";
				String OneRecordsSubjectsError = "";
				String CCErrorSubjects = "";
				String finalErrorMessage = "";
				if(NoRecordsSubjectsList.size() > 0) {
					NoRecordsSubjectsError = "Error : No records found!!!! Subject List: " +NoRecordsSubjectsList;
					assignmentCopyCaseLogger.error(NoRecordsSubjectsError);
					finalErrorMessage = NoRecordsSubjectsError +"<br>";
				}
				if(OneRecordSubjectsList.size() > 0) {
					OneRecordsSubjectsError = "Error : Only one record found!!!! Subject List: " +OneRecordSubjectsList;
					assignmentCopyCaseLogger.error(OneRecordsSubjectsError);
					finalErrorMessage = finalErrorMessage + OneRecordsSubjectsError +"<br>";
				}
				if(CCNotProcessedSubjectsList.size() > 0) {
					CCErrorSubjects = "Error : CC Not Processed!!!! Subject List: " +CCNotProcessedSubjectsList;
					assignmentCopyCaseLogger.error(CCErrorSubjects);
					finalErrorMessage = finalErrorMessage + CCErrorSubjects;
				}
				if(finalErrorMessage !="") {
					response.setError(finalErrorMessage);
				}
				if(CCProcessedSubjectsList.size() > 0) {
					response.setSuccess("true");
					response.setSuccessMessage(" Successfully Copy Case processed Subject List: " + CCProcessedSubjectsList);
					assignmentCopyCaseLogger.info(" Successfully Copy Case processed Subject List: {}", CCProcessedSubjectsList);
				}
				assignmentCopyCaseLogger.info("------------------ Ended CopyCase Selected Subject Count:{}  || Month-Year:{}-{} || Threshold 1-Threshold 2: {}-{} || Selected Subject List:{}-----------------",
						searchBean.getSubjectList().size(), searchBean.getMonth(), searchBean.getYear(),searchBean.getMinMatchPercent(), searchBean.getThreshold2(), searchBean.getSubjectList());
				
				// Auto mark CC Start
				assignmentCopyCaseLogger.info("------------------ Started Auto Mark Copy Case Selected Subject Count:{}  || Month-Year:{}-{} || Threshold 1-Threshold 2: {}-{} || Selected Subject List:{}-----------------",
						searchBean.getSubjectList().size(), searchBean.getMonth(), searchBean.getYear(),searchBean.getMinMatchPercent(), searchBean.getThreshold2(), searchBean.getSubjectList());
				boolean doneAutoMarkCC = false;
				doneAutoMarkCC = asgService.autoMarkCopyCase(searchBean.getMonth(), searchBean.getYear(), searchBean.getSubjectList(), assignmentCopyCaseLogger, searchBean.getLastModifiedBy());
				if(doneAutoMarkCC) {
					List<AssignmentFileBean> CCMarkSubjectWiseSapidsCountList = new ArrayList<AssignmentFileBean>();
					try {
						assignmentCopyCaseLogger.info("------------------ Started Getting CCMarkStudentCountSubjectWise || Subject Count:{} || Month-Year:{}-{} || Threshold 1-Threshold 2: {}-{} || Selected Subject List:{}-----------------",
								searchBean.getSubjectList().size(), searchBean.getMonth(), searchBean.getYear(),searchBean.getMinMatchPercent(), searchBean.getThreshold2(), searchBean.getSubjectList());
						CCMarkSubjectWiseSapidsCountList = asgService.getCCMarkStudentCountSubjectWise(searchBean.getMonth(), searchBean.getYear(), searchBean.getSubjectList());
						if(CCMarkSubjectWiseSapidsCountList != null && CCMarkSubjectWiseSapidsCountList.size() > 0) {
							response.setAllAssignmentFilesList(CCMarkSubjectWiseSapidsCountList);
						}
					} catch (Exception e) {
						assignmentCopyCaseLogger.error(
								"Exception Error getCCMarkStudentCountSubjectWise() || Month-Year:{}-{} || Threshold 1-Threshold 2: {}-{} || Selected Subject List:{} || Error:{}",
								searchBean.getMonth(), searchBean.getYear(),
								searchBean.getMinMatchPercent(), searchBean.getThreshold2(), 
								searchBean.getSubjectList(), 
								e);
						response.setError("Error: Error getting Marked CC subject wise student count.");
//						e.printStackTrace();
					}
				}
				assignmentCopyCaseLogger.info("------------------ Ended Auto Mark Copy Case Selected Subject Count:{}  || Month-Year:{}-{} || Threshold 1-Threshold 2: {}-{} || Selected Subject List:{}-----------------",
						searchBean.getSubjectList().size(), searchBean.getMonth(), searchBean.getYear(),searchBean.getMinMatchPercent(), searchBean.getThreshold2(), searchBean.getSubjectList());
				// Auto mark CC End
				
			} catch (Exception e) {
				assignmentCopyCaseLogger.error(
						"Exception Error CopyCase Selected Subject Count:{} || Month-Year:{}-{} || Threshold 1-Threshold 2: {}-{} || Selected Subject List:{} || Error:{}",
						searchBean.getSubjectList().size(), searchBean.getMonth(), searchBean.getYear(),
						searchBean.getMinMatchPercent(), searchBean.getThreshold2(), searchBean.getSubjectList(), e);
				response.setError("Error: " + e.getMessage());
			}
		}else {
			assignmentCopyCaseLogger.error(
					"No Subjects are found in Excel || Month-Year:{}-{} || Threshold 1-Threshold 2: {}-{}",
					searchBean.getMonth(), searchBean.getYear(),
					searchBean.getMinMatchPercent(), searchBean.getThreshold2());
			response.setError("Error: No Subjects are found in Excel");
		}
		return new ResponseEntity<ExamAssignmentResponseBean>(response, headers, HttpStatus.OK);
	}
	
	@PostMapping(path = "/admin/deletecopyCaseCheckFile")
	public ResponseEntity<ExamAssignmentResponseBean> deletecopyCaseCheckFile(@RequestBody  List<String> checkedValues) throws Exception {
		HttpHeaders headers = new HttpHeaders();
		ExamAssignmentResponseBean response = new ExamAssignmentResponseBean();
		headers.add("Content-Type", "application/json");
		Set<String> filepaths=new HashSet<String>();
		
		filepaths.addAll(checkedValues);
		StringJoiner joiner = new StringJoiner(",");
		for (String value : filepaths) {
			joiner.add("'" + value + "'");
		}
		String filepath = joiner.toString();

		assignmentCopyCaseLogger.info("questionFilePreviewPath :: "+checkedValues);
		String message=null;
		try {
			message = asgService.deleteUploadedQPFile(filepath);
			response.setSuccess("success");
			response.setSuccessMessage(" Successfully deleted  QP file : " + message);
		} catch (Exception e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			message = "Error deleting uploaded QP file "+e.getMessage();
			response.setError("Error: " + e.getMessage());
			assignmentCopyCaseLogger.info("Error deleting uploaded QP file "+e);
		}
		
		return new ResponseEntity<>(response, headers, HttpStatus.OK);
	}
	
	
	
	@PostMapping(path = "/admin/unMarkCopyCases")
	public ResponseEntity<ExamAssignmentResponseBean> unMarkCopyCase(HttpServletRequest request, @RequestBody UnMarkCopyCasesDTO unMarkCCList) throws Exception {
		ExamAssignmentResponseBean response = new ExamAssignmentResponseBean();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		
		AssignmentFileBean bean = ObjectConverter.convertObjToXXX(unMarkCCList, new TypeReference<AssignmentFileBean>() {});
		
		if(bean.getUnMarkCCList() != null || bean.getUnMarkCCList().size() > 0) {
			List<AssignmentFileBean> allocateList = new ArrayList<AssignmentFileBean>();
			List<AssignmentFileBean> otherReasonList = new ArrayList<AssignmentFileBean>();
			List<String> monthList = new ArrayList<String>();
			List<String> yearList = new ArrayList<String>();
			List<String> subjectList = new ArrayList<String>();
			String examMonth = "";
			String examYear = "";
			String subject = "";
			String OtherReasonSuccessDetail = "";
			String AllocateSuccessDetail = "";
			String finalSuccessMsg = "";
			
			monthList = bean.getUnMarkCCList().stream().map(monthBean -> monthBean.getMonth()).distinct().collect(Collectors.toList());
			yearList = bean.getUnMarkCCList().stream().map(yearBean -> yearBean.getYear()).distinct().collect(Collectors.toList());
			subjectList = bean.getUnMarkCCList().stream().map(subjectBean -> subjectBean.getSubject()).distinct().collect(Collectors.toList());
			
			examMonth = asgService.getSingleMonthYear(monthList);
			examYear = asgService.getSingleMonthYear(yearList);
			subject = asgService.getSingleMonthYear(subjectList);
			response.setYear(examYear);
			response.setMonth(examMonth);
			response.setSubject(subject);
			
			// Creating separate list to unmark CC with other reasons.
			otherReasonList = (ArrayList<AssignmentFileBean>) bean.getUnMarkCCList().stream().filter(
					unMarkCCBean -> !unMarkCCBean.getReason().equals("Allocation"))
					.collect(Collectors.toList());
			
			// Creating separate list to unmark CC for allocation process.
			allocateList = (ArrayList<AssignmentFileBean>) bean.getUnMarkCCList().stream().filter(
					unMarkCCBean -> unMarkCCBean.getReason().equals("Allocation"))
					.collect(Collectors.toList());
			
			// Unmarking CC with the reason and set same score of CC and set faculty id as "Sanket".
			if(otherReasonList != null && otherReasonList.size() > 0) {
				List<String> sapidList = otherReasonList.stream().map(sapid -> sapid.getSapId()).distinct().collect(Collectors.toList());
				assignmentCopyCaseLogger.info("UnMark CC process with reason || Month/Year-{}/{} || Subject-{} || Sapid's:{} ", examMonth, examYear, subjectList, sapidList);
				try {
					asgService.updateMarkedCCToUnmarkInTempTable(otherReasonList);
					asgService.updateMarkedCCToUnmarkInSubmissionTable(otherReasonList);
				} catch (Exception e) {
					assignmentCopyCaseLogger.error("Exception Error in UnMark Copy Cases process || Month/Year-{}/{} || Subject-{} || Sapid's:{} || Error:{}", examMonth, examYear, subjectList, sapidList, e);
					response.setError("Error: Error in UnMark Copy Cases process " + e.getMessage());
					return new ResponseEntity<>(response, headers, HttpStatus.OK);
//					e.printStackTrace();
				}
				
				// Checking if the selected month year result is live or not to mark as unprocessed in marks table for re-run passfail process
				boolean isResultLive = false;
				try {
					isResultLive = asgService.getResultLiveExamYear(examMonth, examYear);
				} catch (Exception e) {
					assignmentCopyCaseLogger.error("Exception Error getResultLiveExamYear() || Month/Year-{}/{} || Subject-{} || Sapid's:{} || Error:{}", examMonth, examYear, subjectList, sapidList, e);
					response.setError("Error getting Result Live flag for Exam Cycle - " +examMonth+" "+examYear +"Error: "+e.getMessage());
					return new ResponseEntity<>(response, headers, HttpStatus.OK);
//					e.printStackTrace();
				}
				if(isResultLive) {
					try {
						asgService.updateToUnprocessedInMarksTable(otherReasonList);
					} catch (Exception e) {
						assignmentCopyCaseLogger.error("Exception Error updateToUnprocessedInMarksTable() || Month/Year-{}/{} || Subject-{} || Sapid's:{} || Error:{}", examMonth, examYear, subjectList, sapidList, e);
						response.setError("Error: Error in UnMark Copy Case process in marks " + e.getMessage());
						return new ResponseEntity<>(response, headers, HttpStatus.OK);
//						e.printStackTrace();
					}
				}
				
				OtherReasonSuccessDetail = "Successfully UnMark with Other Reasons Sapid List: "+sapidList;
			}
			
			if(allocateList != null && allocateList.size() > 0) {
				List<String> sapidList = allocateList.stream().map(sapid -> sapid.getSapId()).distinct().collect(Collectors.toList());
				assignmentCopyCaseLogger.info("UnMark CC process to allocate || Month/Year-{}/{} || Subject-{} || Sapid's:{}", examMonth, examYear, subjectList, sapidList);
				try {
					asgService.updateMarkedCCToAllocateInTempTable(allocateList);
					asgService.updateMarkedCCToAllocateInSubmissionTable(allocateList);
				} catch (Exception e) {
					assignmentCopyCaseLogger.error("Exception Error in UnMark CC to allocate process || Month/Year-{}/{} || Subject-{} || Sapid's:{} || Error:{}", examMonth, examYear, subjectList, sapidList, e);
					response.setError("Error: Error in UnMark CC to allocate process " + e.getMessage());
					return new ResponseEntity<>(response, headers, HttpStatus.OK);
//					e.printStackTrace();
				}
				
				AllocateSuccessDetail = "Successfully UnMark to allocate process Sapid List: "+sapidList;
			}
			finalSuccessMsg = "Successfully completed UnMark Month-Year:"+examMonth+"-"+examYear +" Subject:"+subjectList +"\n"+ OtherReasonSuccessDetail +"\n"+ AllocateSuccessDetail;
			response.setSuccess("success");
			response.setSuccessMessage(finalSuccessMsg);
		}else {
			response.setError("Error no data found for UnMark process");
			return new ResponseEntity<>(response, headers, HttpStatus.OK);
		}
		
		return new ResponseEntity<>(response, headers, HttpStatus.OK);
	}
}
