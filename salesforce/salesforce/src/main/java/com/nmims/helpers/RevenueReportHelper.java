package com.nmims.helpers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nmims.beans.RevenueReportConstants;
import com.nmims.beans.RevenueReportField;
import com.nmims.beans.SObjectFields_nm_Payment_c;
import com.nmims.daos.RevenueReportsDAO;
import com.sforce.soap.partner.sobject.SObject;

@Component
public class RevenueReportHelper {

	@Autowired
	RevenueReportsDAO revenueReportDAO;

	public List<RevenueReportField> getCollectionBeansForSync(String date) {
		// Get a list of beans with amounts for yesterday.
	    List<RevenueReportField> collections = getCollectionForDate(date);
		return collections;
	}
	
	public List<RevenueReportField> getCollectionForDate(String date) {
		SimpleDateFormat f1 = new SimpleDateFormat("yyyy-MM-dd");
		Date dateIn = new Date();
		try {
			// SFDC parses 00:00:00 as the previous day. Set time to 23:59:00
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(f1.parse(date));
			calendar.add(Calendar.HOUR_OF_DAY, 23);
			calendar.add(Calendar.MINUTE, 59);
			dateIn = calendar.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		List<RevenueReportField> collections = new ArrayList<RevenueReportField>();
		
		// Get revenue report details for individual payment types.
		RevenueReportField sr = revenueReportDAO.getRevenueReportForSR(date);
		revenueReportDAO.getRefundAmountForDateAndType(sr, RevenueReportConstants.REFUND_TYPE_SERVICE_REQUEST, date);
		setPaymentStatusAndTransactionDate(sr, dateIn);
		collections.add(sr);
		
		RevenueReportField ah = revenueReportDAO.getRevenueReportForAdHoc(date);
		revenueReportDAO.getRefundAmountForDateAndType(ah, RevenueReportConstants.REFUND_TYPE_ADHOC_PAYMENT, date);
		setPaymentStatusAndTransactionDate(ah, dateIn);
		collections.add(ah);
		
		RevenueReportField assignment = revenueReportDAO.getRevenueReportForAssignment(date);
		revenueReportDAO.getRefundAmountForDateAndType(assignment, RevenueReportConstants.REFUND_TYPE_ASSIGNMENT, date);
		setPaymentStatusAndTransactionDate(assignment, dateIn);
		collections.add(assignment);
		
		RevenueReportField ebMBAWX = revenueReportDAO.getRevenueReportForExamBookingMBAWX(date);
		revenueReportDAO.getRefundAmountForDateAndType(ebMBAWX, RevenueReportConstants.REFUND_TYPE_EXAM_MBAWX, date);
		setPaymentStatusAndTransactionDate(ebMBAWX, dateIn);
		collections.add(ebMBAWX);
		
		RevenueReportField ebMBAX = revenueReportDAO.getRevenueReportForExamBookingMBAX(date);
		revenueReportDAO.getRefundAmountForDateAndType(ebMBAX, RevenueReportConstants.REFUND_TYPE_EXAM_MBAX, date);
		setPaymentStatusAndTransactionDate(ebMBAX, dateIn);
		collections.add(ebMBAX);
		
		RevenueReportField ebPG = revenueReportDAO.getRevenueReportForExamBookingPG(date);
		revenueReportDAO.getRefundAmountForDateAndType(ebPG, RevenueReportConstants.REFUND_TYPE_EXAM, date);
		setPaymentStatusAndTransactionDate(ebPG, dateIn);
		collections.add(ebPG);
		
		RevenueReportField pcp = revenueReportDAO.getRevenueReportForPCPBookings(date);
		revenueReportDAO.getRefundAmountForDateAndType(pcp, RevenueReportConstants.REFUND_TYPE_PCP_PAYMENT, date);
		setPaymentStatusAndTransactionDate(pcp, dateIn);
		collections.add(pcp);
		
		return collections;
	}

	private void setPaymentStatusAndTransactionDate(RevenueReportField object, Date dateIn) {
		// By default, the payment status is set as Payment Approved and prospect as Student Portal.
		object.setPaymentStatus(RevenueReportConstants.PAYMENT_STATUS_PAYMENT_APPROVED);
		object.setProspect(RevenueReportConstants.REVENUE_PROSPECT);
		object.setTransactionDate(dateIn);
	}
	
	public SObject createRevenueSObject(RevenueReportField revenue) {
		SObject revenueSObject = new SObject();
		revenueSObject.setType("nm_Payment__c");
		revenueSObject.setField(SObjectFields_nm_Payment_c.FIELD_PAYMENT_TYPE, revenue.getType());
		revenueSObject.setField(SObjectFields_nm_Payment_c.FIELD_PAYMENT_STATUS, revenue.getPaymentStatus());
		revenueSObject.setField(SObjectFields_nm_Payment_c.FIELD_AMOUNT, revenue.getAmount());
		revenueSObject.setField(SObjectFields_nm_Payment_c.FIELD_ACTUAL_PAYMENT_AMOUNT, revenue.getActualPaymentAmount());
		revenueSObject.setField(SObjectFields_nm_Payment_c.FIELD_REFUNDED_AMOUNT, revenue.getRefundedAmount());
		revenueSObject.setField(SObjectFields_nm_Payment_c.FIELD_PROSPECT, revenue.getProspect());
		revenueSObject.setField(SObjectFields_nm_Payment_c.FIELD_TRANSACTION_DATE, revenue.getTransactionDate());
		return revenueSObject;
	}
	
	public void updateRevenueSObject(RevenueReportField revenue, SObject revenueSObject) {
		revenueSObject.setType("nm_Payment__c");
		revenueSObject.setField(SObjectFields_nm_Payment_c.FIELD_PAYMENT_TYPE, revenue.getType());
		revenueSObject.setField(SObjectFields_nm_Payment_c.FIELD_PAYMENT_STATUS, revenue.getPaymentStatus());
		revenueSObject.setField(SObjectFields_nm_Payment_c.FIELD_AMOUNT, revenue.getAmount());
		revenueSObject.setField(SObjectFields_nm_Payment_c.FIELD_ACTUAL_PAYMENT_AMOUNT, revenue.getActualPaymentAmount());
		revenueSObject.setField(SObjectFields_nm_Payment_c.FIELD_REFUNDED_AMOUNT, revenue.getRefundedAmount());
//		revenueSObject.setField(SObjectFields_nm_Payment_c.FIELD_PROSPECT, revenue.getProspect());
		revenueSObject.setField(SObjectFields_nm_Payment_c.FIELD_TRANSACTION_DATE, revenue.getTransactionDate());
	}
}
