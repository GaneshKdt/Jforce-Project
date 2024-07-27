package com.nmims.views;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.view.document.AbstractExcelView;

import com.nmims.beans.CenterAcadsBean;
import com.nmims.beans.PCPBookingTransactionBean;
import com.nmims.beans.StudentAcadsBean;
import com.nmims.daos.PCPBookingDAO;


public class PCPBookingReportExcelView extends AbstractExcelView implements ApplicationContextAware{
	private static ApplicationContext act = null;
	
	@Override
	protected void buildExcelDocument(Map model, HSSFWorkbook workbook,	HttpServletRequest request, HttpServletResponse response)
		throws Exception {
		
		String userId=(String) request.getSession().getAttribute("userId");
		List<String> ICLCRESTRICTED_USER_LIST=(List<String>) request.getSession().getAttribute("ICLCRESTRICTED_USER_LIST");
		String roles = (String)request.getAttribute("roles");
		act = getApplicationContext();
		PCPBookingDAO dao = (PCPBookingDAO)act.getBean("pcpBookingDAO");
		HashMap<String, StudentAcadsBean> sapIdStudentsMap = dao.getAllStudents();
		HashMap<String, CenterAcadsBean> icLcMap = dao.getICLCMap();
		
		
		List<PCPBookingTransactionBean> pcpBookingList = (List<PCPBookingTransactionBean>) model.get("pcpBookingList");
		//create a wordsheet
		HSSFSheet sheet = workbook.createSheet("PCP Bookings");
		int index = 0;
		HSSFRow header = sheet.createRow(0);
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("Year");
	    header.createCell(index++).setCellValue("Month");
		header.createCell(index++).setCellValue("SAP ID");
		header.createCell(index++).setCellValue("First Name");
		header.createCell(index++).setCellValue("Last Name");
	//	header.createCell(index++).setCellValue("Email");
	//	header.createCell(index++).setCellValue("Mobile");
		header.createCell(index++).setCellValue("Alternate Phone");
		if(!ICLCRESTRICTED_USER_LIST.contains(userId)) {
		header.createCell(index++).setCellValue("IC Name");
		}
		header.createCell(index++).setCellValue("Program");
		header.createCell(index++).setCellValue("Sem");
	    header.createCell(index++).setCellValue("Subject");
	    header.createCell(index++).setCellValue("PCP Center Name");
	    header.createCell(index++).setCellValue("Booked");
	    if(roles.indexOf("Admin") !=-1 || roles.indexOf("Learning Center") != -1 || roles.indexOf("Corporate Center") != -1) // allowed to access only Admin User and Learning Center
	    {
	    	if(!ICLCRESTRICTED_USER_LIST.contains(userId)) {
	    	header.createCell(index++).setCellValue("IC Code");
	    	}
	        header.createCell(index++).setCellValue("Amount");
	    
		    header.createCell(index++).setCellValue("Booking Initiation Time");
		    header.createCell(index++).setCellValue("Booking Completion Time");
		    header.createCell(index++).setCellValue("trackId");
		    header.createCell(index++).setCellValue("Payment Mode");
		   
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
	    if(!ICLCRESTRICTED_USER_LIST.contains(userId)) {
	    header.createCell(index++).setCellValue("IC");
		header.createCell(index++).setCellValue("LC");
		header.createCell(index++).setCellValue("Exam Mode");
	    }
 
		int rowNum = 1;
		for (int i = 0 ; i < pcpBookingList.size(); i++) {
			index = 0;
			//create the row data
			HSSFRow row = sheet.createRow(rowNum++);
			PCPBookingTransactionBean bean = pcpBookingList.get(i);
			
			StudentAcadsBean student = sapIdStudentsMap.get(bean.getSapid());
			
			String ic = "";
			String lc = "";
			String examMode = "Offline";
			if(student != null){
				ic = student.getCenterName();
				CenterAcadsBean center = icLcMap.get(student.getCenterCode()); 
				if(center != null){
					lc = center.getLc();
				}
				
				if("Online".equalsIgnoreCase(student.getPrgmStructApplicable())){
					examMode = "Online";
				}
			}
			row.createCell(index++).setCellValue((i+1));
			row.createCell(index++).setCellValue(new Double(bean.getYear()));
			row.createCell(index++).setCellValue(bean.getMonth());
			row.createCell(index++).setCellValue(new Double(bean.getSapid()));
			
			
			row.createCell(index++).setCellValue(bean.getFirstName());
			row.createCell(index++).setCellValue(bean.getLastName());
		//	row.createCell(index++).setCellValue(bean.getEmailId());
		//	row.createCell(index++).setCellValue(bean.getMobile());
			row.createCell(index++).setCellValue(bean.getAltPhone());
			if(!ICLCRESTRICTED_USER_LIST.contains(userId)) {
			row.createCell(index++).setCellValue(bean.getCenterName());
			}
			row.createCell(index++).setCellValue(bean.getProgram());
			row.createCell(index++).setCellValue(new Double(bean.getSem()));
			row.createCell(index++).setCellValue(bean.getSubject());
			if(!ICLCRESTRICTED_USER_LIST.contains(userId)) {
			row.createCell(index++).setCellValue(bean.getCenter());
			}
			row.createCell(index++).setCellValue(bean.getBooked());
			if(roles.indexOf("Admin") !=-1 || roles.indexOf("Learning Center") != -1 || roles.indexOf("Corporate Center") != -1)
		    {
				if(!ICLCRESTRICTED_USER_LIST.contains(userId)) {
				row.createCell(index++).setCellValue(bean.getCenterCode());
				}
				row.createCell(index++).setCellValue(new Double(bean.getAmount()));
				
				row.createCell(index++).setCellValue(bean.getTranDateTime());
				row.createCell(index++).setCellValue(bean.getBookingCompleteTime());
				row.createCell(index++).setCellValue(bean.getTrackId());
				row.createCell(index++).setCellValue(bean.getPaymentMode());
				
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
		    }else{
		    	row.createCell(index++).setCellValue(bean.getCenterCode());
				row.createCell(index++).setCellValue(new Double(bean.getAmount()));
				
				row.createCell(index++).setCellValue(bean.getTranDateTime());
				row.createCell(index++).setCellValue(bean.getBookingCompleteTime());
				row.createCell(index++).setCellValue(bean.getTrackId());
				row.createCell(index++).setCellValue(bean.getPaymentMode());
				
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
		    }
			if(!ICLCRESTRICTED_USER_LIST.contains(userId)) {
			row.createCell(index++).setCellValue(ic);
			row.createCell(index++).setCellValue(lc);
			row.createCell(index++).setCellValue(examMode);
			}
        }
		
		sheet.setAutoFilter(CellRangeAddress.valueOf("A1:AQ999999999"));
	}

}
