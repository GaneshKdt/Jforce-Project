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
import com.nmims.beans.MettlTEEAttendanceReportBean;

public class MettlTEEAttendanceReportExcelView extends AbstractXlsxStreamingView implements ApplicationContextAware{
	
	@SuppressWarnings("rawtypes")
	@Override
	protected void buildExcelDocument(Map model, Workbook workbook,	HttpServletRequest request, HttpServletResponse response)
		throws Exception {
		
		@SuppressWarnings("unchecked")
		List<MettlTEEAttendanceReportBean> list = (List<MettlTEEAttendanceReportBean>) model.get("teeAttendanceReport");
		
		//create a wordsheet :- START
		Sheet sheet = workbook.createSheet("TEE Attendance Report");
		
		int index = 0;
		Row header = sheet.createRow(0);
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("Sap ID");
		header.createCell(index++).setCellValue("Email Id");
		header.createCell(index++).setCellValue("Student Name");
		header.createCell(index++).setCellValue("Subject Name");
		header.createCell(index++).setCellValue("Exam Date");
		header.createCell(index++).setCellValue("Exam Time");
		header.createCell(index++).setCellValue("IC Name");
		header.createCell(index++).setCellValue("LC Name");
		header.createCell(index++).setCellValue("Exam Status");
		header.createCell(index++).setCellValue("Mettl Status");
		header.createCell(index++).setCellValue("Exam Year");
		header.createCell(index++).setCellValue("Exam Month");
		header.createCell(index++).setCellValue("Program Name");
		header.createCell(index++).setCellValue("Sem");
		 
		int rowNum = 1;
		for (int i = 0 ; i < list.size(); i++) {
			//create the row data
			index = 0;
			Row row = sheet.createRow(rowNum++);
			MettlTEEAttendanceReportBean bean = list.get(i);
			row.createCell(index++).setCellValue(rowNum-1);
			row.createCell(index++).setCellValue(bean.getSapid());
			row.createCell(index++).setCellValue(bean.getEmailId());
			row.createCell(index++).setCellValue(bean.getFirstName()+" "+bean.getLastName());
			row.createCell(index++).setCellValue(bean.getSubject());
			row.createCell(index++).setCellValue(bean.getExamDate());
			row.createCell(index++).setCellValue(bean.getExamTime());
			row.createCell(index++).setCellValue(bean.getIc());
			row.createCell(index++).setCellValue(bean.getLc());
			row.createCell(index++).setCellValue(bean.getExamStatus());
			row.createCell(index++).setCellValue(bean.getTestTaken());
			row.createCell(index++).setCellValue(bean.getYear());
			row.createCell(index++).setCellValue(bean.getMonth());
			row.createCell(index++).setCellValue(bean.getProgramCode());
			row.createCell(index++).setCellValue(bean.getSem());
        }
		
		sheet.autoSizeColumn(0); sheet.autoSizeColumn(1);
		sheet.autoSizeColumn(2); sheet.autoSizeColumn(3);
		sheet.autoSizeColumn(4); sheet.autoSizeColumn(5);
	}
}
