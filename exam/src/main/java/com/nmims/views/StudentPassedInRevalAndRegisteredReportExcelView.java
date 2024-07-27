package com.nmims.views;

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

import com.nmims.beans.ExamBookingExamBean;

public class StudentPassedInRevalAndRegisteredReportExcelView extends AbstractXlsxStreamingView implements ApplicationContextAware{

	private static ApplicationContext act = null;
	
	@Override
	protected void buildExcelDocument(Map model, Workbook workbook, HttpServletRequest request, HttpServletResponse response) 
			throws Exception {

		act = getApplicationContext();
		
		List<ExamBookingExamBean> studentList = (List<ExamBookingExamBean>) model.get("studentPassedInRevalAndRegisteredReport");
		
		Sheet sheet = workbook.createSheet("Student passed in reval and Register Report");
		int index = 0;
		Row header = sheet.createRow(0);
		
		header.createCell(index++).setCellValue("Sr. No");
		header.createCell(index++).setCellValue("SAP ID");
		header.createCell(index++).setCellValue("Program");
		header.createCell(index++).setCellValue("Sem");
		header.createCell(index++).setCellValue("Subject");
		header.createCell(index++).setCellValue("Exam Date");
		header.createCell(index++).setCellValue("Booked"); 
		
	    header.createCell(index++).setCellValue("Exam Booking Year");
	    header.createCell(index++).setCellValue("Exam Booking Month");
	    
	    int rowNum = 1;
	    for (int i = 0; i < studentList.size(); i++) {
			index = 0;
			Row row = sheet.createRow(rowNum++);
			ExamBookingExamBean bean = studentList.get(i);
			
			row.createCell(index++).setCellValue((i+1));
			row.createCell(index++).setCellValue(bean.getSapid());
			row.createCell(index++).setCellValue(bean.getProgram());
			row.createCell(index++).setCellValue(bean.getSem());
			row.createCell(index++).setCellValue(bean.getSubject());
			row.createCell(index++).setCellValue(bean.getExamDate());
			row.createCell(index++).setCellValue(bean.getBooked());
			
			row.createCell(index++).setCellValue(bean.getYear());
			row.createCell(index++).setCellValue(bean.getMonth());
			
		}
	}

}
