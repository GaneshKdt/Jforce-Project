package com.nmims.services;

import java.util.List;

import com.nmims.beans.ExamBookingCancelBean;

/**
 * 
 * @author Manasi_T
 *
 */

import com.nmims.beans.ExamCenterSlotMappingBean;
import com.nmims.beans.StudentMarksBean;

public interface IReportsService {
	
	public List<ExamCenterSlotMappingBean> getexamCenterCapacityReport(StudentMarksBean studentMarks) throws Exception;

	public List<ExamBookingCancelBean> getExamBookingCanceledListReport(ExamBookingCancelBean searchBean) throws Exception;
	
}
