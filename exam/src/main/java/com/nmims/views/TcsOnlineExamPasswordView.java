package com.nmims.views;

import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;
import com.nmims.beans.TcsOnlineExamBean;


public class TcsOnlineExamPasswordView  extends AbstractXlsxStreamingView  implements ApplicationContextAware {
	@Override
	protected void buildExcelDocument(Map model, Workbook workbook,	HttpServletRequest request, HttpServletResponse response)
		throws Exception {
		
		ArrayList<TcsOnlineExamBean> tcsOnlineExamPasswordlist = (ArrayList<TcsOnlineExamBean>) model.get("tcsOnlineExamPasswordlist");
		
		//create a wordsheet :- START
		Sheet sheet = workbook.createSheet("Report Of Tcs Online Exam Password List");
		
		int index = 0;
		Row header = sheet.createRow(0);
		header.createCell(index++).setCellValue("Unique Request Id");
		header.createCell(index++).setCellValue("Exam Year");
		header.createCell(index++).setCellValue("Exam Month");
		header.createCell(index++).setCellValue("Program");
		header.createCell(index++).setCellValue("User Id");
		header.createCell(index++).setCellValue("Password");
		header.createCell(index++).setCellValue("Subject Id");
		header.createCell(index++).setCellValue("Subject");
		header.createCell(index++).setCellValue("LC");
		header.createCell(index++).setCellValue("IC");
		header.createCell(index++).setCellValue("First Name");
		header.createCell(index++).setCellValue("Last Name");
		header.createCell(index++).setCellValue("Registered Email Id");
		header.createCell(index++).setCellValue("Exam Date");
		header.createCell(index++).setCellValue("Exam Time");
		header.createCell(index++).setCellValue("Center Id");
		header.createCell(index++).setCellValue("Test Taken");
		header.createCell(index++).setCellValue("Action");

		int rowNum = 1;
		for (int i = 0 ; i < tcsOnlineExamPasswordlist.size(); i++) {
			//create the row data
			index = 0;
			Row row = sheet.createRow(rowNum++);
			TcsOnlineExamBean bean = tcsOnlineExamPasswordlist.get(i);
			
			row.createCell(index++).setCellValue(bean.getUniqueRequestId());
			row.createCell(index++).setCellValue(bean.getExamYear());
			row.createCell(index++).setCellValue(bean.getExamMonth());
			row.createCell(index++).setCellValue(bean.getProgram());
			row.createCell(index++).setCellValue(bean.getUserId());
			row.createCell(index++).setCellValue(bean.getPassword());
			row.createCell(index++).setCellValue(bean.getSubjectId());
			row.createCell(index++).setCellValue(bean.getSubject());
			row.createCell(index++).setCellValue(bean.getLc());
			row.createCell(index++).setCellValue(bean.getCenterName());
			row.createCell(index++).setCellValue(bean.getFirstName());
			row.createCell(index++).setCellValue(bean.getLastName());
			row.createCell(index++).setCellValue(bean.getRegisteredEmailId());
			row.createCell(index++).setCellValue(bean.getExamDate());
			row.createCell(index++).setCellValue(bean.getExamTime());
			row.createCell(index++).setCellValue(bean.getCenterId());
			row.createCell(index++).setCellValue(bean.getTestTaken());
			
			
			
        }
		
	
		
	
	}
}


