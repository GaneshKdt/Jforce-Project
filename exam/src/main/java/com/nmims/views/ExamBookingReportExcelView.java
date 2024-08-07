package com.nmims.views;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.view.document.AbstractExcelView;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;

import com.nmims.beans.CenterExamBean;
import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.beans.ExamCenterBean;
import com.nmims.beans.ExamCenterSlotMappingBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.UserAuthorizationExamBean;
import com.nmims.daos.StudentMarksDAO;



public class ExamBookingReportExcelView extends AbstractXlsxStreamingView  implements ApplicationContextAware{
	
	private static ApplicationContext act = null;
	
	@Override
	protected void buildExcelDocument(Map model, Workbook workbook,	HttpServletRequest request, HttpServletResponse response)
		throws Exception {
		String userId=(String) request.getSession().getAttribute("userId");
		List<String> ICLCRESTRICTED_USER_LIST=(List<String>) request.getSession().getAttribute("ICLCRESTRICTED_USER_LIST");
		
		act = getApplicationContext();
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		HashMap<String, StudentExamBean> sapIdStudentsMap = dao.getAllStudents();
		HashMap<String, CenterExamBean> icLcMap = dao.getICLCMap();
		
		UserAuthorizationExamBean userAuthorization= (UserAuthorizationExamBean)request.getSession().getAttribute("userAuthorization");
		
		List<ExamBookingTransactionBean> examBookingList = (List<ExamBookingTransactionBean>) model.get("examBookingList");
		//create a wordsheet
		Sheet sheet = workbook.createSheet("Exam Bookings");
		int index = 0;
		Row header = sheet.createRow(0);
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("Year");
	    header.createCell(index++).setCellValue("Month");
		header.createCell(index++).setCellValue("SAP ID");
		header.createCell(index++).setCellValue("Enrollment Year");
		header.createCell(index++).setCellValue("Enrollment Month");
		header.createCell(index++).setCellValue("First Name");
		header.createCell(index++).setCellValue("Last Name");
		if(userAuthorization.getRoles().contains("Exam Admin") || userAuthorization.getRoles().contains("Assignment Admin") ||  userAuthorization.getRoles().contains("TEE Admin") ) {
			header.createCell(index++).setCellValue("Email");
			header.createCell(index++).setCellValue("Mobile");
		}
		
		header.createCell(index++).setCellValue("Alternate Phone");
		header.createCell(index++).setCellValue("Payment Gateway");
		
		if(!ICLCRESTRICTED_USER_LIST.contains(userId)) {
		header.createCell(index++).setCellValue("IC Code");
		header.createCell(index++).setCellValue("IC Name");
		 }
		
		header.createCell(index++).setCellValue("Program");
		header.createCell(index++).setCellValue("Sem");
	    header.createCell(index++).setCellValue("Subject");
	    header.createCell(index++).setCellValue("SifyCode");
	    
	    if(!ICLCRESTRICTED_USER_LIST.contains(userId)) {
	    header.createCell(index++).setCellValue("Exam Mode");
	    header.createCell(index++).setCellValue("Exam City");
	    header.createCell(index++).setCellValue("Center Id");
	    header.createCell(index++).setCellValue("Exam Center Name");
	    }
	    header.createCell(index++).setCellValue("Amount");
	    header.createCell(index++).setCellValue("Exam Date");
	    header.createCell(index++).setCellValue("Exam Start Time");
	    header.createCell(index++).setCellValue("Exam End Time");
	    
	    header.createCell(index++).setCellValue("Booked");
	    header.createCell(index++).setCellValue("Booking Initiation Time");
	    header.createCell(index++).setCellValue("Booking Completion Time");
	    header.createCell(index++).setCellValue("trackId");
	    header.createCell(index++).setCellValue("Payment Mode");
	    header.createCell(index++).setCellValue("DD No");
	    header.createCell(index++).setCellValue("DD Bank");
	    header.createCell(index++).setCellValue("DD Amount");

	   
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
	    
	    if(!ICLCRESTRICTED_USER_LIST.contains(userId)) {
	    header.createCell(index++).setCellValue("IC");
		header.createCell(index++).setCellValue("LC");
		header.createCell(index++).setCellValue("Exam Mode");
	    }
	    
 
		int rowNum = 1;
		for (int i = 0 ; i < examBookingList.size(); i++) {
			index = 0;
			//create the row data
			Row row = sheet.createRow(rowNum++);
			ExamBookingTransactionBean bean = examBookingList.get(i);
			
			StudentExamBean student = sapIdStudentsMap.get(bean.getSapid());
			
			String ic = "";
			String lc = "";
			String examMode = "Offline";
			if(student != null){
				ic = student.getCenterName();
				CenterExamBean center = icLcMap.get(student.getCenterCode()); 
				if(center != null){
					lc = center.getLc();
				}
				
				if("Online".equals(student.getExamMode())){
					examMode = "Online";
				}
			}
			
			row.createCell(index++).setCellValue((i+1));
			row.createCell(index++).setCellValue(new Double(bean.getYear()));
			row.createCell(index++).setCellValue(bean.getMonth());
			row.createCell(index++).setCellValue(new Double(bean.getSapid()));
			
			row.createCell(index++).setCellValue(new Double(bean.getEnrollmentYear()));
			row.createCell(index++).setCellValue(bean.getEnrollmentMonth());
			row.createCell(index++).setCellValue(bean.getFirstName());
			row.createCell(index++).setCellValue(bean.getLastName());
			if(userAuthorization.getRoles().contains("Exam Admin") || userAuthorization.getRoles().contains("Assignment Admin") ||  userAuthorization.getRoles().contains("TEE Admin") ) {
			row.createCell(index++).setCellValue(bean.getEmailId());
			row.createCell(index++).setCellValue(bean.getMobile());
			}
			row.createCell(index++).setCellValue(bean.getAltPhone());
			row.createCell(index++).setCellValue(bean.getPaymentOption());
			
			if(!ICLCRESTRICTED_USER_LIST.contains(userId)) {
			row.createCell(index++).setCellValue(bean.getCenterCode());
			row.createCell(index++).setCellValue(bean.getCenterName());
			}
			
			row.createCell(index++).setCellValue(bean.getProgram());
			row.createCell(index++).setCellValue(new Double(bean.getSem()));
			row.createCell(index++).setCellValue(bean.getSubject());
			row.createCell(index++).setCellValue(bean.getSifySubjectCode());
			if(!ICLCRESTRICTED_USER_LIST.contains(userId)) {
			row.createCell(index++).setCellValue(bean.getExamMode());
			row.createCell(index++).setCellValue(bean.getCity());
			row.createCell(index++).setCellValue(new Double(bean.getCenterId()));
			row.createCell(index++).setCellValue(bean.getExamCenterName());
			}
			row.createCell(index++).setCellValue(new Double(bean.getAmount()));
			row.createCell(index++).setCellValue(bean.getExamDate());
			row.createCell(index++).setCellValue(bean.getExamTime());
			row.createCell(index++).setCellValue(bean.getExamEndTime());
			
			row.createCell(index++).setCellValue(bean.getBooked());
			row.createCell(index++).setCellValue(bean.getTranDateTime());
			row.createCell(index++).setCellValue(bean.getBookingCompleteTime());
			row.createCell(index++).setCellValue(bean.getTrackId());
			row.createCell(index++).setCellValue(bean.getPaymentMode());
			row.createCell(index++).setCellValue(bean.getDdno());
			row.createCell(index++).setCellValue(bean.getBank());
			row.createCell(index++).setCellValue(bean.getDdAmount());
			row.createCell(index++).setCellValue(bean.getTransactionID());
			row.createCell(index++).setCellValue(bean.getTranStatus());
			row.createCell(index++).setCellValue(bean.getPaymentID());
			row.createCell(index++).setCellValue(bean.getRequestID());
			row.createCell(index++).setCellValue(bean.getMerchantRefNo());
			row.createCell(index++).setCellValue(bean.getSecureHash());
			row.createCell(index++).setCellValue(bean.getRespAmount());
			row.createCell(index++).setCellValue(bean.getDescription());
			row.createCell(index++).setCellValue(bean.getResponseCode());
			row.createCell(index++).setCellValue(bean.getRespPaymentMethod());
			row.createCell(index++).setCellValue(bean.getIsFlagged());
			row.createCell(index++).setCellValue(bean.getResponseMessage());
			row.createCell(index++).setCellValue(bean.getError());
			row.createCell(index++).setCellValue(bean.getRespTranDateTime());
			
			if(!ICLCRESTRICTED_USER_LIST.contains(userId)) {
			row.createCell(index++).setCellValue(ic);
			row.createCell(index++).setCellValue(lc);
			row.createCell(index++).setCellValue(examMode);
			}
			
        }
		
	
	}

}
