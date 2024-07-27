package com.nmims.stratergies.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Throwables;
import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.controllers.ExamBookingController;
import com.nmims.daos.ExamCenterDAO;
import com.nmims.dto.ExamBookingTransactionDTO;
import com.nmims.services.RescheduledCancelledSlotChangeReportService;
import com.nmims.stratergies.ExamRegistrationRealTimeStrategy;
import com.nmims.stratergies.IExamBookingSeatReleaseStrategy;
/**
 * 
 * @author Siddheshwar_K
 *
 */
@Service("pgExamBookingSeatsReleaseStrgy")
public class PGExamBookingSeatReleaseStrategy implements IExamBookingSeatReleaseStrategy {
	@Autowired
	private ExamCenterDAO examCenterDAO;
	@Autowired
	private RescheduledCancelledSlotChangeReportService exambookingAuditService;
	
	@Autowired
	private ExamRegistrationRealTimeStrategy examRegistrationRealTimeStrategy;
	
	@Autowired
	public ExamBookingController examBookingController;
	
	public static final Logger exambookingAuditlogger = LoggerFactory.getLogger("examBookingAudit");
	public static final Logger examRegisterlogger = LoggerFactory.getLogger("examRegisterPG");
	
	@Override
	public List<ExamBookingTransactionDTO> releaseExamBookingSeats(ExamBookingTransactionDTO exmBkingTxnDTO,
			List<ExamBookingTransactionDTO> exmBkingTxnDTOList, boolean isCorporateUser) throws Exception {
		
		List<ExamBookingTransactionBean> exmBkingTxnBOList = new ArrayList<ExamBookingTransactionBean>();
		final List<ExamBookingTransactionDTO> exmBookingTxnDTOList = new ArrayList<ExamBookingTransactionDTO>();
		
		List<String> releaseSubjects = exmBkingTxnDTO.getReleaseSubjects();
		List<String> releaseReasonsList= exmBkingTxnDTO.getReleaseReasonsList();
		
		int reasonCount=0;
		for(ExamBookingTransactionDTO exmBookingTxnDTO : exmBkingTxnDTOList) {
			if("Y".equalsIgnoreCase(exmBookingTxnDTO.getBooked())) {
				//Prepare single string of subject and trackId
				String str = exmBookingTxnDTO.getSubject().replace(",", "")+"|"+exmBookingTxnDTO.getTrackId();

				//If prepared subject string is contains in released subjects list then only add to list along with reason.
				if(releaseSubjects.contains(str)){
					//Copy DTO class properties to BO class properties.
					ExamBookingTransactionBean exmBookingTxnBO = new ExamBookingTransactionBean();
					BeanUtils.copyProperties(exmBookingTxnDTO, exmBookingTxnBO);

					exmBookingTxnBO.setReleaseReason(releaseReasonsList.get(reasonCount));
					exmBkingTxnBOList.add(exmBookingTxnBO);
				}
				reasonCount++;
			}//if
		}//for
		
		//Copy DTO class properties to BO class properties.
		ExamBookingTransactionBean exmBookingTxnBO = new ExamBookingTransactionBean();
		BeanUtils.copyProperties(exmBkingTxnDTO, exmBookingTxnBO);
		
		//Added by shivam.pandey.EXT - START
		List<ExamBookingTransactionBean> toReleaseSubjectList = new ArrayList<>();
		
		ExamBookingTransactionBean booking = new ExamBookingTransactionBean();
		BeanUtils.copyProperties(exmBkingTxnDTO, booking);
		
		ArrayList<ExamBookingTransactionBean> confirmedOrReleasedBooking = examCenterDAO.getConfirmedOrReleasedBooking(booking);
		
		int audit_ReasonCount = 0;
		for(ExamBookingTransactionBean bean : confirmedOrReleasedBooking) {
			if("Y".equalsIgnoreCase(bean.getBooked())) {
				//Prepare single string of subject and trackId
				String str = bean.getSubject().replace(",", "")+"|"+bean.getTrackId();

				//If prepared subject string is contains in released subjects list then only add to list along with reason.
				if(releaseSubjects.contains(str)){
					bean.setReleaseReason(releaseReasonsList.get(audit_ReasonCount));
					toReleaseSubjectList.add(bean);
				}
				audit_ReasonCount++;
			}
		}
		//Added by shivam.pandey.EXT - END
		
		
		//Execute release booking 
		exmBkingTxnBOList = examCenterDAO.releaseBookings(exmBkingTxnDTO.getSapid(), exmBkingTxnBOList, 
				exmBookingTxnBO, exmBkingTxnDTO.getChargesStatus(), true, isCorporateUser);
		
		//Added by shivam.pandey.EXT - START
		exambookingAuditService.asyncInsertExamBookingAudit(toReleaseSubjectList, exmBkingTxnDTO.getChargesStatus(), exmBookingTxnBO.getLastModifiedBy());	
		//Added by shivam.pandey.EXT - END
		
		examRegisterlogger.info("Real Time Registartion called from releaseExamBookingSeats method"+examCenterDAO.getIsExtendedExamRegistrationLiveForRealTime());
		if(examCenterDAO.getIsExtendedExamRegistrationLiveForRealTime()) {
			examRegisterlogger.info("Real Time Registartion called");
		examRegistrationRealTimeStrategy.registrationOnMettlAndReleaseBooking(null,toReleaseSubjectList);
		}
		
		//Convert List of BO to List of DTO
		exmBkingTxnBOList.forEach((examBkingTransactionBO)->{
			ExamBookingTransactionDTO bean = new ExamBookingTransactionDTO();
			BeanUtils.copyProperties(examBkingTransactionBO, bean);
			exmBookingTxnDTOList.add(bean);
		});

		//return confirmed and bookings details.
		return exmBookingTxnDTOList;
	}

}
