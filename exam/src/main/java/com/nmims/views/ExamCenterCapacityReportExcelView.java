package com.nmims.views;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.view.document.AbstractExcelView;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;

import com.nmims.beans.ExamCenterSlotMappingBean;


public class ExamCenterCapacityReportExcelView  extends AbstractXlsxStreamingView  implements ApplicationContextAware{
	
	@Override
	protected void buildExcelDocument(Map model, Workbook workbook,	HttpServletRequest request, HttpServletResponse response)
		throws Exception {
 
		List<ExamCenterSlotMappingBean> centerBookingsList = (List<ExamCenterSlotMappingBean>) model.get("centerBookingsList");
		//create a wordsheet
		Sheet sheet = workbook.createSheet("Center Capacity");
 
		Row header = sheet.createRow(0);
		header.createCell(0).setCellValue("Sr. No.");
		header.createCell(1).setCellValue("Year");
		header.createCell(2).setCellValue("Month");
	    header.createCell(3).setCellValue("Exam Center Id");
		header.createCell(4).setCellValue("Exam Center Name");
		header.createCell(5).setCellValue("Exam Date");
		header.createCell(6).setCellValue("Exam Day");
	    header.createCell(7).setCellValue("Exam Start Time");
		header.createCell(8).setCellValue("City");
		header.createCell(9).setCellValue("Address");
	    header.createCell(10).setCellValue("Capacity");
	    header.createCell(11).setCellValue("Booked");
	    header.createCell(12).setCellValue("Oh Hold");
	    header.createCell(13).setCellValue("Available");

		int rowNum = 1;
		for (int i = 0 ; i < centerBookingsList.size(); i++) {
			//create the row data
			Row row = sheet.createRow(rowNum++);
			ExamCenterSlotMappingBean bean = centerBookingsList.get(i);
			row.createCell(0).setCellValue((i+1));
			row.createCell(1).setCellValue(new Double(bean.getYear()));
			row.createCell(2).setCellValue(bean.getMonth());
			row.createCell(3).setCellValue(new Double(bean.getExamcenterId()));
			row.createCell(4).setCellValue(bean.getExamCenterName());
			row.createCell(5).setCellValue(bean.getDate());
			row.createCell(6).setCellValue(bean.getDay());
			row.createCell(7).setCellValue(bean.getStarttime());
			row.createCell(8).setCellValue(bean.getCity());
			row.createCell(9).setCellValue(bean.getAddress());
			row.createCell(10).setCellValue(new Double(bean.getCapacity()));
			row.createCell(11).setCellValue(new Double(bean.getBooked()));
			row.createCell(12).setCellValue(new Double(bean.getOnHold()));
			row.createCell(13).setCellValue(new Double(bean.getAvailable()));
			
        }
		
		
	}

}
