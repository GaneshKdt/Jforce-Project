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
import com.nmims.beans.MBAXExamCenterSlotMappingBean;


public class ExamCenterCapacityReportExcelViewMBAX  extends AbstractXlsxStreamingView  implements ApplicationContextAware{
	
	@Override
	protected void buildExcelDocument(Map model, Workbook workbook,	HttpServletRequest request, HttpServletResponse response)
		throws Exception {
 
		List<MBAXExamCenterSlotMappingBean> centerBookingsList = (List<MBAXExamCenterSlotMappingBean>) model.get("centerBookingsList");
		//create a wordsheet
		Sheet sheet = workbook.createSheet("Center Capacity");
 
		Row header = sheet.createRow(0);
		header.createCell(0).setCellValue("Sr. No.");
		header.createCell(1).setCellValue("Year");
		header.createCell(2).setCellValue("Month");
	    header.createCell(3).setCellValue("Exam Center Id");
		header.createCell(4).setCellValue("Exam Center Name");
		header.createCell(5).setCellValue("Exam Date");
	    header.createCell(6).setCellValue("Exam Start Time");
		header.createCell(7).setCellValue("City");
		header.createCell(8).setCellValue("Address");
	    header.createCell(9).setCellValue("Capacity");
	    header.createCell(10).setCellValue("Booked");
	    header.createCell(11).setCellValue("Oh Hold");
	    header.createCell(12).setCellValue("Available");

		int rowNum = 1;
		for (int i = 0 ; i < centerBookingsList.size(); i++) {
			//create the row data
			Row row = sheet.createRow(rowNum++);
			MBAXExamCenterSlotMappingBean bean = centerBookingsList.get(i);
			row.createCell(0).setCellValue((i+1));
			row.createCell(1).setCellValue(bean.getYear());
			row.createCell(2).setCellValue(bean.getMonth());
			row.createCell(3).setCellValue(new Double(bean.getCenterId()));
			row.createCell(4).setCellValue(bean.getExamCenterName());
			row.createCell(5).setCellValue(bean.getExamDate());
			row.createCell(6).setCellValue(bean.getExamStartTime());
			row.createCell(7).setCellValue(bean.getCity());
			row.createCell(8).setCellValue(bean.getAddress());
			row.createCell(9).setCellValue(new Double(bean.getCapacity()));
			row.createCell(10).setCellValue(new Double(bean.getBooked()));
			row.createCell(11).setCellValue(bean.getOnHold());
			row.createCell(12).setCellValue(new Double(bean.getAvailable()));
			
        }
		
		
	}

}
