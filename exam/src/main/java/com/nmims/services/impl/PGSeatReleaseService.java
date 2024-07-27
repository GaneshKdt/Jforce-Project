package com.nmims.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.nmims.dto.ExamBookingTransactionDTO;
import com.nmims.services.IExamBookingSeatReleaseService;
import com.nmims.stratergies.IExamBookingSeatReleaseStrategy;
import com.nmims.stratergies.ISearchConfirmedAndReleasedBookingStrategy;
/**
 * 
 * @author Siddheshwar_K
 *
 */
@Service("pgBookingSeatReleaseService")
public final class PGSeatReleaseService implements IExamBookingSeatReleaseService {

	@Autowired
	@Qualifier("pgExamBookingSeatsReleaseStrgy")
	private IExamBookingSeatReleaseStrategy releaseExamBookingSeats;
	
	@Autowired
	@Qualifier("pgSearchConfirmedAndReleasedBkingStrgy")
	private ISearchConfirmedAndReleasedBookingStrategy searchConfirmedAndReleasedBking;
	
	@Override
	public List<ExamBookingTransactionDTO> releaseExamBookingSeats(ExamBookingTransactionDTO exmBkingTxnDTO,
			List<ExamBookingTransactionDTO> exmBkingTxnDTOList, boolean isCorporateUser) throws Exception {
		
		//return the latest updated confirmed and seat released exam booking subjects details. 
		return releaseExamBookingSeats.releaseExamBookingSeats(exmBkingTxnDTO, exmBkingTxnDTOList, isCorporateUser);
	}

	@Override
	public List<ExamBookingTransactionDTO> searchConfirmedAndReleasedBookings(ExamBookingTransactionDTO exmBkingTxnDTO)
			throws Exception {
		
		//return the confirmed and seat released exam booking subjects details. 
		return searchConfirmedAndReleasedBking.searchConfirmedAndReleasedBookings(exmBkingTxnDTO);
	}

}
