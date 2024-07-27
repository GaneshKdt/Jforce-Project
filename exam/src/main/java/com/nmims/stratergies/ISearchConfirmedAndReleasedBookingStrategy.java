package com.nmims.stratergies;

import java.util.List;

import com.nmims.dto.ExamBookingTransactionDTO;
/**
 * 
 * @author Siddheshwar_K
 *
 */
public interface ISearchConfirmedAndReleasedBookingStrategy {
	/**
	 * Search confirmed and released subjects exam booking details.
	 * @param exmBkingTxnDTO - Bean having exam booking search details like year,month and SAPID.
	 * @return Exam booking confirmed and released subjects details.
	 * @throws Exception If any exception raise while searching the confirmed and released subjects.
	 */
	List<ExamBookingTransactionDTO> searchConfirmedAndReleasedBookings(ExamBookingTransactionDTO exmBkingTxnDTO) throws Exception;
}
