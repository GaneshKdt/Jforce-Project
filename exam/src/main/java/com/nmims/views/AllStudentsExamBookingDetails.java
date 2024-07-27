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

import com.nmims.beans.MBAExamBookingRequest;

public class AllStudentsExamBookingDetails  extends AbstractXlsxStreamingView  implements ApplicationContextAware{
	
	@Override
	protected void buildExcelDocument(Map model, Workbook workbook,	HttpServletRequest request, HttpServletResponse response)
		throws Exception {
 
		try {
			
		List<MBAExamBookingRequest> exambookingList = (List<MBAExamBookingRequest>)model.get("exambookingList");

		//create a wordsheet
		Sheet sheet = workbook.createSheet("Exam Booking List MBA-WX");
		int index = 0;
		Row header = sheet.createRow(0);
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("Student Number");
		header.createCell(index++).setCellValue("First Name");
		
		header.createCell(index++).setCellValue("Last Name");
		header.createCell(index++).setCellValue("Mobile");
		header.createCell(index++).setCellValue("Email Id");
		header.createCell(index++).setCellValue("Subject Name");
		
		header.createCell(index++).setCellValue("Center Name");
		header.createCell(index++).setCellValue("Exam Date");

		header.createCell(index++).setCellValue("Exam Start Time");
		header.createCell(index++).setCellValue("Exam End Time");
		
		
		
		int rowNum = 1;
		for (int i = 0 ; i < exambookingList.size(); i++) {
			index = 0;
			//create the row data
			Row row = sheet.createRow(rowNum++);
			MBAExamBookingRequest bean = exambookingList.get(i);
			
			row.createCell(index++).setCellValue(rowNum-1);
			
			row.createCell(index++).setCellValue(new Double(bean.getSapid()));
			row.createCell(index++).setCellValue(bean.getFirstName());
			row.createCell(index++).setCellValue(bean.getLastName());
			row.createCell(index++).setCellValue(bean.getMobile());
			row.createCell(index++).setCellValue(bean.getEmailId());
			row.createCell(index++).setCellValue(bean.getSubjectName());
			row.createCell(index++).setCellValue(bean.getCenterName());
			row.createCell(index++).setCellValue(bean.getExamDate());
			row.createCell(index++).setCellValue(bean.getExamStartDateTime());
			row.createCell(index++).setCellValue(bean.getExamEndDateTime()); 
		
        }
		
		} catch (Exception e) {
			
		}
	}
		
		

}
