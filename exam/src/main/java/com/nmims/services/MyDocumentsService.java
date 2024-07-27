package com.nmims.services;

import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.nmims.beans.DocumentResponseBean;
import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.daos.ExamBookingDAO;
import com.nmims.helpers.SalesforceHelper;

@Service("myDocumentsService")
public class MyDocumentsService implements MyDocumentsInterface {

	@Autowired
	SalesforceHelper salesforceHelper;

	@Autowired
	ExamBookingDAO examBookingDao;

	@Override
	public DocumentResponseBean getStudentsDocuments(String sapid) throws Exception {
		// TODO Auto-generated method stub
		
		DocumentResponseBean response = new DocumentResponseBean();
		
		ArrayList<ExamBookingTransactionBean> listOfFeeReceiptsBasedOnSapid = new ArrayList<ExamBookingTransactionBean>();
		ArrayList<ExamBookingTransactionBean> listOfHallTicketsBasedOnSapid = new ArrayList<ExamBookingTransactionBean>();
		ArrayList<ExamBookingTransactionBean> listOfPCPBookingsBasedOnSapid = new ArrayList<ExamBookingTransactionBean>();
		ArrayList<ExamBookingTransactionBean> listOfSRFeeReceiptBasedOnSapid = new ArrayList<ExamBookingTransactionBean>();
		ArrayList<ExamBookingTransactionBean> listOfAssignmentFeeReceiptBasedOnSapid = new ArrayList<ExamBookingTransactionBean>();
		ArrayList<ExamBookingTransactionBean> listOfProjectFeeReceiptBasedOnSapid = new ArrayList<ExamBookingTransactionBean>();
		ArrayList<StudentExamBean> listOfAdmissionPaymentReceipt = new ArrayList<StudentExamBean>();
				
		try {
			listOfAdmissionPaymentReceipt = getAdmissionFeeReceiptFromSapId(sapid);
			listOfFeeReceiptsBasedOnSapid = getExamFeeReceiptFromSapId(sapid);
			listOfHallTicketsBasedOnSapid = getHallTicketFromSapId(sapid);
			listOfPCPBookingsBasedOnSapid = getPCPBookingsFromSapid(sapid);
			listOfSRFeeReceiptBasedOnSapid = getSRFeeReceiptFromSapid(sapid);
			listOfAssignmentFeeReceiptBasedOnSapid = getAssignmentFeeReceiptFromSapid(sapid);
			listOfProjectFeeReceiptBasedOnSapid = getProjectFeeReceiptFromSapid(sapid);
			
			response.setAdmissionPaymentReceipt(listOfAdmissionPaymentReceipt);
			response.setFeeReceipts(listOfFeeReceiptsBasedOnSapid);
			response.setHallTickets(listOfHallTicketsBasedOnSapid);
			response.setPcpBookings(listOfPCPBookingsBasedOnSapid);
			response.setSrFeeReceipts(listOfSRFeeReceiptBasedOnSapid);
			response.setAssignmentFeeReceipts(listOfAssignmentFeeReceiptBasedOnSapid);
			response.setProjectFeeReceipts(listOfProjectFeeReceiptBasedOnSapid);
			
			return response;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		
	}

	private ArrayList<ExamBookingTransactionBean> getExamFeeReceiptFromSapId(String sapid) {
		ArrayList<ExamBookingTransactionBean> listOfFeeReceiptsBasedOnSapid = examBookingDao.listOfDocumentsBasedOnSapidAndDocType(sapid, "Exam Fee Receipt");
		return listOfFeeReceiptsBasedOnSapid;
	}

	private ArrayList<ExamBookingTransactionBean> getHallTicketFromSapId(String sapid) {
		ArrayList<ExamBookingTransactionBean> listOfHallTicketsBasedOnSapid = examBookingDao.listOfDocumentsBasedOnSapidAndDocType(sapid, "Hall Ticket");

		return listOfHallTicketsBasedOnSapid;
	}

	private ArrayList<ExamBookingTransactionBean> getPCPBookingsFromSapid(String sapid) {
		ArrayList<ExamBookingTransactionBean> listOfPCPBookingsBasedOnSapid = examBookingDao.listOfDocumentsBasedOnSapidAndDocType(sapid, "PCP Fee Receipt");
		return listOfPCPBookingsBasedOnSapid;
	}
	
	private ArrayList<ExamBookingTransactionBean> getSRFeeReceiptFromSapid(String sapid) {
		ArrayList<ExamBookingTransactionBean> listOfSRFeeReceiptBasedOnSapid = examBookingDao.listOfDocumentsBasedOnSapidAndDocType(sapid, "SR Fee Receipt");
		return listOfSRFeeReceiptBasedOnSapid;
	}
	
	private ArrayList<ExamBookingTransactionBean> getAssignmentFeeReceiptFromSapid(String sapid) {
		ArrayList<ExamBookingTransactionBean> listOfAssignmentFeeReceiptBasedOnSapid = examBookingDao.listOfDocumentsBasedOnSapidAndDocType(sapid, "Assignment Fee Receipt");
		return listOfAssignmentFeeReceiptBasedOnSapid;
	}
	
	private ArrayList<ExamBookingTransactionBean> getProjectFeeReceiptFromSapid(String sapid) {
		ArrayList<ExamBookingTransactionBean> listOfProjectFeeReceiptBasedOnSapid = examBookingDao.listOfDocumentsBasedOnSapidAndDocType(sapid, "Project Fee Receipt");
		return listOfProjectFeeReceiptBasedOnSapid;
	}

	private ArrayList<StudentExamBean> getAdmissionFeeReceiptFromSapId(String sapid) {
		ArrayList<StudentExamBean> listOfAdmissionPaymentReceipt = salesforceHelper.listOfPaymentsMade(sapid);
		return listOfAdmissionPaymentReceipt;
	}

}
