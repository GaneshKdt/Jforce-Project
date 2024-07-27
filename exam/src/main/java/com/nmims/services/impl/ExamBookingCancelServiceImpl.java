package com.nmims.services.impl;

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
import com.nmims.services.ExamBookingCancelService;
import com.nmims.services.RescheduledCancelledSlotChangeReportService;
import com.nmims.stratergies.ExamRegistrationRealTimeStrategy;

@Service("examBookingCancelService")
public class ExamBookingCancelServiceImpl implements ExamBookingCancelService{

	private final String SEAT_CANCELLED_WITH_REFUND = "Cancellation With Refund";
	private final String SEAT_CANCELLED_WITHOUT_REFUND = "Cancellation Without Refund";
	
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
	public List<ExamBookingTransactionDTO> cancelExamBookingSeats(ExamBookingTransactionDTO exmBkingTxnDTO,
			List<ExamBookingTransactionDTO> exmBkingTxnDTOList, boolean isCorporateUser) throws Exception {
		
		List<ExamBookingTransactionBean> exmBkingTxnBOList = new ArrayList<ExamBookingTransactionBean>();
		final List<ExamBookingTransactionDTO> exmBookingTxnDTOList = new ArrayList<ExamBookingTransactionDTO>();
		
		List<String> cancelSubjects = exmBkingTxnDTO.getReleaseSubjects();
		List<String> cancelReasonsList= exmBkingTxnDTO.getReleaseReasonsList();

		
		int reasonCount=0;
		for(ExamBookingTransactionDTO exmBookingTxnDTO : exmBkingTxnDTOList) {
			if("Y".equalsIgnoreCase(exmBookingTxnDTO.getBooked())) {
				//Prepare single string of subject and trackId
				String str = exmBookingTxnDTO.getSubject().replace(",", "")+"|"+exmBookingTxnDTO.getTrackId();

				//If prepared subject string is contains in cancelled subjects list then only add to list along with reason.
				if(cancelSubjects.contains(str)){
					//Copy DTO class properties to BO class properties.
					ExamBookingTransactionBean exmBookingTxnBO = new ExamBookingTransactionBean();
					BeanUtils.copyProperties(exmBookingTxnDTO, exmBookingTxnBO);

					exmBookingTxnBO.setReleaseReason(cancelReasonsList.get(reasonCount));
					exmBkingTxnBOList.add(exmBookingTxnBO);
				}
				reasonCount++;
			}//if
		}//for
		
		//Copy DTO class properties to BO class properties.
		ExamBookingTransactionBean exmBookingTxnBO = new ExamBookingTransactionBean();
		BeanUtils.copyProperties(exmBkingTxnDTO, exmBookingTxnBO);
		
		String concatenate="";
		String status = "";
		String booked= "";
		if ("true".equalsIgnoreCase(exmBkingTxnDTO.getChargesStatus())) {
			status = SEAT_CANCELLED_WITH_REFUND;
			booked="N";
			concatenate=" to be refunded";
		} else {
			status = SEAT_CANCELLED_WITHOUT_REFUND;
			booked="CL";
		}
		
		//Added by shivam.pandey.EXT - START
		List<ExamBookingTransactionBean> toReleaseSubjectList = new ArrayList<>();
		
		ExamBookingTransactionBean booking = new ExamBookingTransactionBean();
		BeanUtils.copyProperties(exmBkingTxnDTO, booking);
		
		ArrayList<ExamBookingTransactionBean> confirmedOrReleasedBooking = examCenterDAO.getConfirmedOrReleasedBooking(booking);
		
		int audit_CancelCount = 0;
		for(ExamBookingTransactionBean bean : confirmedOrReleasedBooking) {
			if("Y".equalsIgnoreCase(bean.getBooked())) {
				//Prepare single string of subject and trackId
				String str = bean.getSubject().replace(",", "")+"|"+bean.getTrackId();

				//If prepared subject string is contains in released subjects list then only add to list along with reason.
				if(cancelSubjects.contains(str)){
					bean.setReleaseReason(cancelReasonsList.get(audit_CancelCount));
					toReleaseSubjectList.add(bean);
				}
				audit_CancelCount++;
			}
		}
		//Added by shivam.pandey.EXT - END
		
		//Execute cancel booking 
		exmBkingTxnBOList = examCenterDAO.cancelBookings(exmBkingTxnDTO.getSapid(), exmBkingTxnBOList, 
				exmBookingTxnBO, exmBkingTxnDTO.getChargesStatus(), true, isCorporateUser,booked,status,concatenate);
		
		//Added by shivam.pandey.EXT - START
		exambookingAuditService.asyncInsertExamBookingAudit(toReleaseSubjectList, "cancel"+exmBkingTxnDTO.getChargesStatus(), exmBookingTxnBO.getLastModifiedBy());	
		//Added by shivam.pandey.EXT - END
		
		examRegisterlogger.info("Real Time Registartion called from cancelExamBookingSeats method"+examCenterDAO.getIsExtendedExamRegistrationLiveForRealTime());
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

		//return confirmed and released and cancelled bookings details.
		return exmBookingTxnDTOList;
	}
	
	public ArrayList<ExamBookingTransactionDTO> getCancelledSubjectStartDateTime(ArrayList<String> cancelledSubjects,List<ExamBookingTransactionDTO> confirmedBookingsList)
	{
		ArrayList<ExamBookingTransactionDTO> subjectExamDateTime = new ArrayList<ExamBookingTransactionDTO>();
		for(ExamBookingTransactionDTO exmBookingTxnDTO : confirmedBookingsList) {
			if("Y".equalsIgnoreCase(exmBookingTxnDTO.getBooked())) {
				//Prepare single string of subject and trackId
				String str = exmBookingTxnDTO.getSubject().replace(",", "")+"|"+exmBookingTxnDTO.getTrackId();

				//If prepared subject string is contains in cancelled subjects list then only add to list along with reason.
				if(cancelledSubjects.contains(str)){
					subjectExamDateTime.add(exmBookingTxnDTO);
				}
			}
		}
		return subjectExamDateTime;
	}
	
}
