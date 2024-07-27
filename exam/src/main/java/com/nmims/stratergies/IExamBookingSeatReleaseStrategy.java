package com.nmims.stratergies;

import java.util.List;

import com.nmims.dto.ExamBookingTransactionDTO;
/**
 * 
 * @author Siddheshwar_K
 *
 */
public interface IExamBookingSeatReleaseStrategy {
	/**
	 * To release exam booking of a selected subjects with release reason.
	 * @param exmBkingTxnDTO - Bean having search details like year,month and SAPID and release subjects with reason.
	 * @param exmBkingTxnDTOList - Exam booking confirmed and released subjects details.
	 * @param isCorporateUser - User is corporate or not for which seat to be release.
	 * @return List of latest updated exam booking confirmed and released subjects details.
	 * @throws Exception If any exception raise while doing booking release.
	 */
	List<ExamBookingTransactionDTO> releaseExamBookingSeats(ExamBookingTransactionDTO exmBkingTxnDTO,
			List<ExamBookingTransactionDTO> exmBkingTxnDTOList,
			boolean isCorporateUser) throws Exception;
}