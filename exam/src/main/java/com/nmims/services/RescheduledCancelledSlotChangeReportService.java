package com.nmims.services;

import java.util.List;

import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.beans.RescheduledCancelledSlotChangeReportBean;
import com.nmims.dto.ExamBookingTransactionDTO;

/**
 * 
 * @author shivam.pandey.EXT
 *
 */
public interface RescheduledCancelledSlotChangeReportService {
	//To Get All Released(Seat Release and With No Charges) and Cancelled(With and Without Refund) 
	public List<RescheduledCancelledSlotChangeReportBean> getAllReleasedAndCancelledList()throws Exception;
	public void asyncInsertExamBookingAudit(List<ExamBookingTransactionBean> toReleaseSubjectList,
			String releasedStatus, String createdBy)throws Exception;
	public List<ExamBookingTransactionBean> getConfirmedOrReleasedBooking(ExamBookingTransactionDTO bookingDTO);
}
