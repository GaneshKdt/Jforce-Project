package com.nmims.daos;

import java.util.List;

import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.beans.RescheduledCancelledSlotChangeReportBean;

/**
 * 
 * @author shivam.pandey.EXT
 *
 */
public interface RescheduledCancelledSlotChangeReportDAO {

	//To Get List Of All 'Seat Released and Released - With No Charges' Booking Seat
	public List<RescheduledCancelledSlotChangeReportBean> getAllRLList()throws Exception;
	//To Get List Of All 'Cancelled With and Without Refund'  Booking Seat
	public List<RescheduledCancelledSlotChangeReportBean> getAllCLList()throws Exception;
	//To Get List Of All 'Cancelled With and Without Refund'  Booking Seat
	public List<RescheduledCancelledSlotChangeReportBean> getAllICList()throws Exception;
	//To Get List Of All 'Cancelled With and Without Refund'  Booking Seat
	public List<RescheduledCancelledSlotChangeReportBean> getAllLCList()throws Exception;
	//To Batch Insert Released and Cancelled Details in exam.exambookings_audit table
	public int batchInsertExamBookingAudit(List<ExamBookingTransactionBean> toReleaseSubjectList, 
			String releasedStatus, String createdBy)throws Exception;
}
