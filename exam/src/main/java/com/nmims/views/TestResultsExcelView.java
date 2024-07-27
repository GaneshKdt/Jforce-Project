package com.nmims.views;

import java.util.HashMap;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.view.document.AbstractExcelView;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;

import com.nmims.beans.CenterExamBean;
import com.nmims.beans.ExecutiveExamOrderBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.beans.StudentsTestDetailsExamBean;
import com.nmims.daos.StudentMarksDAO;

public class TestResultsExcelView  extends AbstractXlsxStreamingView  implements ApplicationContextAware {
	
	@Override
	protected void buildExcelDocument(Map model, Workbook workbook,	HttpServletRequest request, HttpServletResponse response)
		throws Exception {
		
		List<StudentsTestDetailsExamBean> testDetailsList = (List<StudentsTestDetailsExamBean>) model.get("testDetailsList");
		
		//create a wordsheet :- START
		Sheet sheet = workbook.createSheet("Report Of Test Results List");
		
		int index = 0;
		Row header = sheet.createRow(0);
		header.createCell(index++).setCellValue("SAP ID");
		header.createCell(index++).setCellValue("First Name");
		header.createCell(index++).setCellValue("Last Name");
		header.createCell(index++).setCellValue("Test Name");
		header.createCell(index++).setCellValue("Start Date");
		header.createCell(index++).setCellValue("Subject");
		header.createCell(index++).setCellValue("Score");
		header.createCell(index++).setCellValue("Max Score");
		
		
 
		int rowNum = 1;
		for (int i = 0 ; i < testDetailsList.size(); i++) {
			//create the row data
			index = 0;
			Row row = sheet.createRow(rowNum++);
			StudentsTestDetailsExamBean bean = testDetailsList.get(i);
			
			row.createCell(index++).setCellValue(bean.getSapid());
			row.createCell(index++).setCellValue(bean.getFirstName());
			row.createCell(index++).setCellValue(bean.getLastName());
			row.createCell(index++).setCellValue(bean.getTestName());
			row.createCell(index++).setCellValue(bean.getStartDate());
			row.createCell(index++).setCellValue(bean.getSubject());
			row.createCell(index++).setCellValue(bean.getScore());
			row.createCell(index++).setCellValue(bean.getMaxScore());


		
        }
		
	
	}
}
