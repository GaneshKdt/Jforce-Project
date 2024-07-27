package com.nmims.stratergies.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.daos.ExamCenterDAO;
import com.nmims.dto.ExamBookingTransactionDTO;
import com.nmims.stratergies.ISearchConfirmedAndReleasedBookingStrategy;
/**
 * 
 * @author Siddheshwar_K
 *
 */
@Service("pgSearchConfirmedAndReleasedBkingStrgy")
public class PGSearchConfirmedAndReleasedBookingStrategy implements ISearchConfirmedAndReleasedBookingStrategy {

	@Autowired
	private ExamCenterDAO examCenterDAO;
	
	@Override
	public List<ExamBookingTransactionDTO> searchConfirmedAndReleasedBookings(ExamBookingTransactionDTO exmBkingTxnDTO)
			throws Exception {
		
		ExamBookingTransactionBean exmBkingTxnBO=null;
		final List<ExamBookingTransactionDTO> confirmedBookingsList= new ArrayList<ExamBookingTransactionDTO>();
		
		exmBkingTxnBO = new ExamBookingTransactionBean();
		
		//Convert DTO to BO class object.
		BeanUtils.copyProperties(exmBkingTxnDTO, exmBkingTxnBO);
		
		//Get the confirmed and released booking details for the given user , year and month.
		List<ExamBookingTransactionBean> confirmedBookings = examCenterDAO.getConfirmedOrReleasedBooking(exmBkingTxnBO);
		
		//Convert List of BO to List of DTO
		confirmedBookings.forEach((examBkingTransactionBO)->{
			ExamBookingTransactionDTO bean = new ExamBookingTransactionDTO();
			BeanUtils.copyProperties(examBkingTransactionBO, bean);
			confirmedBookingsList.add(bean);
		});
		
		//return confirmed and bookings details.
		return confirmedBookingsList;
	}

}
