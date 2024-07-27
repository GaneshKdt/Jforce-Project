package com.nmims.services;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;

import com.nmims.beans.AssignmentFileBean;
import com.nmims.beans.AssignmentFilesSetbean;
import com.nmims.beans.ResponseBean;

@Service("qpAdmin")
public interface QpAdmin { 
	AssignmentFilesSetbean createFacultyReviewerMappingFromExcel(AssignmentFilesSetbean filesSet);
	AssignmentFilesSetbean createFacultyReviewerMappingFromForm(AssignmentFilesSetbean filesSet);
	AssignmentFilesSetbean addRemark(AssignmentFilesSetbean filesSet);
	AssignmentFilesSetbean checkPendingUploadAndReviewList();
	ResponseBean adminApproveQp(List<AssignmentFilesSetbean> fileSet);
	AssignmentFilesSetbean setStartDateEndDateForStudent(AssignmentFilesSetbean item); 
	AssignmentFilesSetbean batchUpdateQpFacultyMapping(List<AssignmentFilesSetbean> facultyQpBeanList);
	AssignmentFilesSetbean checkStartDateEndDateValid(AssignmentFilesSetbean filesSet) throws Exception;
	AssignmentFilesSetbean saveAssignmentFileInStagingTable(AssignmentFilesSetbean filesSet);
	AssignmentFilesSetbean updateAssignmentStartDateEndDate(AssignmentFilesSetbean fileset);
	AssignmentFilesSetbean checkStudentStartDateEndDateValid(AssignmentFilesSetbean filesSet) throws ParseException;
	ArrayList<String> getSubjectList(); 
	HashMap<String, String> getFacultyList();
	AssignmentFilesSetbean saveAssignmentQuestionsInQpTable(AssignmentFilesSetbean filesSet);
}
