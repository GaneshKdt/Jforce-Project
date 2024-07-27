package com.nmims.views;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;

import com.nmims.beans.MBASlotBean;


public class MBAExamCenterCapacityReportExcelView  extends AbstractXlsxStreamingView  implements ApplicationContextAware{
	
	@SuppressWarnings("rawtypes")
	@Override
	protected void buildExcelDocument(Map model, Workbook workbook,	HttpServletRequest request, HttpServletResponse response)
		throws Exception {
 
		List<MBASlotBean> centerBookingsList = (List<MBASlotBean>) model.get("centerBookingsList");
		//create a wordsheet
		Sheet sheet = workbook.createSheet("Center Capacity");
 
		int cellNum = 0;

		int rowNum = 0;
		Row header = sheet.createRow(rowNum++);
		header.createCell(cellNum++).setCellValue("Sr. No.");
		
		// Exam Details
		header.createCell(cellNum++).setCellValue("Year");
		header.createCell(cellNum++).setCellValue("Month");
		header.createCell(cellNum++).setCellValue("Subject Name");
		
		
		// Center Details
		header.createCell(cellNum++).setCellValue("Exam Center Name");
		header.createCell(cellNum++).setCellValue("City");
		header.createCell(cellNum++).setCellValue("Address");
		
		// Time Table Details
		header.createCell(cellNum++).setCellValue("Exam Date");
	    header.createCell(cellNum++).setCellValue("Exam Start Time");
	    header.createCell(cellNum++).setCellValue("Exam End Time");
	    
		// Booking Count
	    header.createCell(cellNum++).setCellValue("Capacity");
	    header.createCell(cellNum++).setCellValue("Booked");
	    header.createCell(cellNum++).setCellValue("Available");

		for (MBASlotBean bean : centerBookingsList) {
			Row row = sheet.createRow(rowNum);
			
			cellNum = 0;
			row.createCell(cellNum++).setCellValue((rowNum++));
			
			// Exam Details
			row.createCell(cellNum++).setCellValue(new Double(bean.getExamYear()));
			row.createCell(cellNum++).setCellValue(bean.getExamMonth());
			row.createCell(cellNum++).setCellValue(bean.getSubjectName());
			
			// Center Details
			row.createCell(cellNum++).setCellValue(bean.getCenterName());
			row.createCell(cellNum++).setCellValue(bean.getCenterCity());
			row.createCell(cellNum++).setCellValue(bean.getCenterAddress());
			
			// Time Table Details
			row.createCell(cellNum++).setCellValue(bean.getExamDate());
			row.createCell(cellNum++).setCellValue(bean.getExamStartTime());
			row.createCell(cellNum++).setCellValue(bean.getExamEndTime());
			
			
			// Booking Count
			row.createCell(cellNum++).setCellValue(bean.getCapacity());
			row.createCell(cellNum++).setCellValue(bean.getBookedSlots());
			row.createCell(cellNum++).setCellValue(bean.getAvailableSlots());
		}
		
	}

}
