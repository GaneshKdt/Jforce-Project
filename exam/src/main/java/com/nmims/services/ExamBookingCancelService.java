package com.nmims.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.nmims.dto.ExamBookingTransactionDTO;

@Component
public interface ExamBookingCancelService {

	public List<ExamBookingTransactionDTO> cancelExamBookingSeats(ExamBookingTransactionDTO exmBkingTxnDTO,
			List<ExamBookingTransactionDTO> exmBkingTxnDTOList, boolean isCorporateUser) throws Exception;
	
	public ArrayList<ExamBookingTransactionDTO> getCancelledSubjectStartDateTime(ArrayList<String> cancelledSubjects,List<ExamBookingTransactionDTO> confirmedBookingsList);
	
}
