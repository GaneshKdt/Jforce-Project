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

import com.nmims.beans.ExecutiveExamCenter;

public class ExecutiveExamCenterCapacityReportExcelView  extends AbstractXlsxStreamingView  implements ApplicationContextAware{
	
	@Override
	protected void buildExcelDocument(Map model, Workbook workbook,	HttpServletRequest request, HttpServletResponse response)
		throws Exception {
 
		List<ExecutiveExamCenter> examCenterCapacityList = (List<ExecutiveExamCenter>) model.get("examCenterCapacityList");
		//create a wordsheet
		Sheet sheet = workbook.createSheet("Center Capacity");
 
		Row header = sheet.createRow(0);
		header.createCell(0).setCellValue("Sr. No.");
		header.createCell(1).setCellValue("Batch Year");
		header.createCell(2).setCellValue("Batch Month");
		header.createCell(3).setCellValue("Exam Year");
		header.createCell(4).setCellValue("Exam Month");
	    header.createCell(5).setCellValue("Exam Center Id");
		header.createCell(6).setCellValue("Exam Center Name");
		header.createCell(7).setCellValue("Exam Date");
	    header.createCell(8).setCellValue("Exam Start Time");
		header.createCell(9).setCellValue("City");
		header.createCell(10).setCellValue("Address");
	    header.createCell(11).setCellValue("Capacity");
	    header.createCell(12).setCellValue("Booked");
	    header.createCell(13).setCellValue("Oh Hold");
	    header.createCell(14).setCellValue("Available");

		int rowNum = 1;
		for (int i = 0 ; i < examCenterCapacityList.size(); i++) {
			//create the row data
			Row row = sheet.createRow(rowNum++);
			ExecutiveExamCenter bean = examCenterCapacityList.get(i);
			row.createCell(0).setCellValue((i+1));
			row.createCell(1).setCellValue(new Double(bean.getBatchYear()));
			row.createCell(2).setCellValue(bean.getBatchMonth());
			row.createCell(3).setCellValue(new Double(bean.getYear()));
			row.createCell(4).setCellValue(bean.getMonth());
			row.createCell(5).setCellValue(new Double(bean.getCenterId()));
			row.createCell(6).setCellValue(bean.getExamCenterName());
			row.createCell(7).setCellValue(bean.getDate());
			row.createCell(8).setCellValue(bean.getStarttime());
			row.createCell(9).setCellValue(bean.getCity());
			row.createCell(10).setCellValue(bean.getAddress());
			row.createCell(11).setCellValue(new Double(bean.getCapacity()));
			row.createCell(12).setCellValue(new Double(bean.getBooked()));
			row.createCell(13).setCellValue(new Double(bean.getOnHold()));
			int available = 0;
			try {
				available=(int) Integer.parseInt(bean.getCapacity()) - bean.getBooked();
			} catch (Exception e) {
				
			}
			
			row.createCell(14).setCellValue(new Double(available));
		 }
		
		
	}

}
