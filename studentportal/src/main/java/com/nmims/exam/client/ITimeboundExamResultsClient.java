package com.nmims.exam.client;

import org.springframework.http.ResponseEntity;

import com.nmims.beans.MBAResponseBean;
import com.nmims.beans.MBAWXPortalExamResultsBean;

/**
 * 
 * @author Siddheshwar_Khanse
 *
 */
public interface ITimeboundExamResultsClient {
	
	public ResponseEntity<MBAWXPortalExamResultsBean> getSubjectWiseStudentMarksRecord(String sapId);
	
	public ResponseEntity<MBAWXPortalExamResultsBean> getStudentPassFailRecords(String sapId);
	
	public ResponseEntity<MBAResponseBean> getStudentAllExamBookings(String sapId);
	
}
