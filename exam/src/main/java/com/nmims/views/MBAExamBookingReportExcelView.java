package com.nmims.views;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;

import com.nmims.beans.CenterExamBean;
import com.nmims.beans.MBAExamBookingReportBean;
import com.nmims.beans.MBAExamBookingRequest;
import com.nmims.beans.MBAPaymentRequest;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.UserAuthorizationExamBean;
import com.nmims.daos.StudentMarksDAO;



public class MBAExamBookingReportExcelView extends AbstractXlsxStreamingView  implements ApplicationContextAware{
	
	private static ApplicationContext act = null;
	
	@SuppressWarnings("rawtypes")
	@Override
	protected void buildExcelDocument(Map model, Workbook workbook,	HttpServletRequest request, HttpServletResponse response)
		throws Exception {
 
		act = getApplicationContext();
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		HashMap<String, CenterExamBean> icLcMap = dao.getICLCMap();
		
		UserAuthorizationExamBean userAuthorization= (UserAuthorizationExamBean)request.getSession().getAttribute("userAuthorization");

		boolean showStudentContactDetails = true;
		boolean showPaymentDetails = true;
		
		if(userAuthorization != null && userAuthorization.getRoles() != null) {
			if(userAuthorization.getRoles().contains("Exam Admin") || userAuthorization.getRoles().contains("Assignment Admin") ||  userAuthorization.getRoles().contains("TEE Admin") ) {
				showStudentContactDetails = true;
				showPaymentDetails = true;
			}
		}
		
		List<MBAExamBookingReportBean> examBookingList = (List<MBAExamBookingReportBean>) model.get("examBookingList");
		
		//create a worksheet
		Sheet sheet = workbook.createSheet("Exam Bookings");
		int index = 0;
		Row header = sheet.createRow(0);
		header.createCell(index++).setCellValue("Sr. No.");
		
		header.createCell(index++).setCellValue("Year");
	    header.createCell(index++).setCellValue("Month");
	    
	    // Student fields
		header.createCell(index++).setCellValue("SAP ID");
		header.createCell(index++).setCellValue("Enrollment Year");
		header.createCell(index++).setCellValue("Enrollment Month");
		header.createCell(index++).setCellValue("Program");
		header.createCell(index++).setCellValue("First Name");
		header.createCell(index++).setCellValue("Last Name");
		if(showStudentContactDetails) {
			header.createCell(index++).setCellValue("Email");
			header.createCell(index++).setCellValue("Mobile");
			header.createCell(index++).setCellValue("Alternate Phone");
		}
		header.createCell(index++).setCellValue("IC Code");
		header.createCell(index++).setCellValue("IC Name");
		

		// Booking details
		header.createCell(index++).setCellValue("Sem");
	    header.createCell(index++).setCellValue("Subject");
	    header.createCell(index++).setCellValue("Exam Mode");
	    header.createCell(index++).setCellValue("Exam City");
	    header.createCell(index++).setCellValue("Exam Center Name");
	    header.createCell(index++).setCellValue("Amount");
	    header.createCell(index++).setCellValue("Exam Date");
	    header.createCell(index++).setCellValue("Exam Start Time");
	    header.createCell(index++).setCellValue("Exam End Time");
	    header.createCell(index++).setCellValue("Booked");
	    
	    // Transaction details
		if(showPaymentDetails) {
			header.createCell(index++).setCellValue("Payment Gateway");
		    header.createCell(index++).setCellValue("Booking Initiation Time");
		    header.createCell(index++).setCellValue("trackId");
		    header.createCell(index++).setCellValue("Transaction ID");
		    header.createCell(index++).setCellValue("Transaction Status");
		    header.createCell(index++).setCellValue("Payment ID");
		    header.createCell(index++).setCellValue("Request ID");
		    header.createCell(index++).setCellValue("Merchant Ref No");
		    header.createCell(index++).setCellValue("Secure Hash");
		    header.createCell(index++).setCellValue("respAmount");
		    header.createCell(index++).setCellValue("Description");
		    header.createCell(index++).setCellValue("Response Code");
		    header.createCell(index++).setCellValue("Response Payment Method");
		    header.createCell(index++).setCellValue("Is Flagged");
		    header.createCell(index++).setCellValue("Response Message");
		    header.createCell(index++).setCellValue("Error");
		    header.createCell(index++).setCellValue("Response Transaction Date Time");
		}

	    header.createCell(index++).setCellValue("imgUrl");
	    header.createCell(index++).setCellValue("IC");
		header.createCell(index++).setCellValue("LC");
 
		int rowNum = 1;
		
		int i = 0;
		for (MBAExamBookingReportBean bean : examBookingList) {
			index = 0;
			i++;
			Row row = sheet.createRow(rowNum++);
			
			StudentExamBean student = bean.getStudent();
			MBAExamBookingRequest booking = bean.getBooking();
			MBAPaymentRequest paymentDetails = bean.getPaymentDetails();
			
			String ic = "";
			String lc = "";
			if(student != null){
				ic = student.getCenterName();
				CenterExamBean center = icLcMap.get(student.getCenterCode()); 
				if(center != null){
					lc = center.getLc();
				}
			}
			
			row.createCell(index++).setCellValue((i));
			
		    // Student fields
			row.createCell(index++).setCellValue(new Double(booking.getYear()));
			row.createCell(index++).setCellValue(booking.getMonth());
			row.createCell(index++).setCellValue(new Double(booking.getSapid()));
			row.createCell(index++).setCellValue(new Double(student.getEnrollmentYear()));
			row.createCell(index++).setCellValue(student.getEnrollmentMonth());
			row.createCell(index++).setCellValue(student.getProgram());
			row.createCell(index++).setCellValue(student.getFirstName());
			row.createCell(index++).setCellValue(student.getLastName());
			if(showStudentContactDetails) {
				row.createCell(index++).setCellValue(student.getEmailId());
				row.createCell(index++).setCellValue(student.getMobile());
				row.createCell(index++).setCellValue(student.getAltPhone());
			}
			row.createCell(index++).setCellValue(student.getCenterCode());
			row.createCell(index++).setCellValue(student.getCenterName());
			
			// Booking details
			row.createCell(index++).setCellValue(new Double(student.getSem()));
			row.createCell(index++).setCellValue(booking.getSubjectName());
			row.createCell(index++).setCellValue(student.getExamMode());
			row.createCell(index++).setCellValue(booking.getCenterCity());
			row.createCell(index++).setCellValue(booking.getCenterName());
			row.createCell(index++).setCellValue(new Double(paymentDetails.getAmount()));
			row.createCell(index++).setCellValue(booking.getExamDate());
			row.createCell(index++).setCellValue(booking.getExamStartTime());
			row.createCell(index++).setCellValue(booking.getExamEndTime());
			row.createCell(index++).setCellValue(booking.getBookingStatus());

		    // Transaction details
			if(showPaymentDetails) {
				row.createCell(index++).setCellValue(paymentDetails.getPaymentOption());
				row.createCell(index++).setCellValue(paymentDetails.getTranDateTime());
				row.createCell(index++).setCellValue(paymentDetails.getTrackId());
				row.createCell(index++).setCellValue(paymentDetails.getTransactionID());
				row.createCell(index++).setCellValue(paymentDetails.getTranStatus());
				row.createCell(index++).setCellValue(paymentDetails.getPaymentID());
				row.createCell(index++).setCellValue(paymentDetails.getRequestID());
				row.createCell(index++).setCellValue(paymentDetails.getMerchantRefNo());
				row.createCell(index++).setCellValue(paymentDetails.getSecureHash());
				row.createCell(index++).setCellValue(paymentDetails.getRespAmount());
				row.createCell(index++).setCellValue(paymentDetails.getDescription());
				row.createCell(index++).setCellValue(paymentDetails.getResponseCode());
				row.createCell(index++).setCellValue(paymentDetails.getRespPaymentMethod());
				row.createCell(index++).setCellValue(paymentDetails.getIsFlagged());
				row.createCell(index++).setCellValue(paymentDetails.getResponseMessage());
				row.createCell(index++).setCellValue(paymentDetails.getError());
				row.createCell(index++).setCellValue(paymentDetails.getRespTranDateTime());
			}

			row.createCell(index++).setCellValue(student.getImageUrl());
			row.createCell(index++).setCellValue(ic);
			row.createCell(index++).setCellValue(lc);
		}
	}
}
