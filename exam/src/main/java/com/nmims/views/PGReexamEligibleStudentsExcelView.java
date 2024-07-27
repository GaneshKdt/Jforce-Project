package com.nmims.views;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractExcelView;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;

import com.nmims.beans.PGReexamEligibleStudentsBean;


public class PGReexamEligibleStudentsExcelView extends AbstractXlsxStreamingView  implements ApplicationContextAware{
	private static ApplicationContext act = null;
		
	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		act = getApplicationContext();
		List<PGReexamEligibleStudentsBean> pgReexamEligibleStudentsList =  (List<PGReexamEligibleStudentsBean>)model.get("pgReexamEligibleStudentsList");
		Sheet sheet = workbook.createSheet("Sheet1");
		int index = 0;
		Row header = sheet.createRow(0);
		header.createCell(index++).setCellValue("Subject");
		header.createCell(index++).setCellValue("Student ID");
		header.createCell(index++).setCellValue("Student Name");
		header.createCell(index++).setCellValue("Program");
		header.createCell(index++).setCellValue("Program Structure");
		header.createCell(index++).setCellValue("Fail reason");
		int rowNum = 1;
		for (int i = 0 ; i < pgReexamEligibleStudentsList.size(); i++) {
			index = 0;
			Row row = sheet.createRow(rowNum++);
			PGReexamEligibleStudentsBean pgReexamEligibleStudentsBean = pgReexamEligibleStudentsList.get(i);
			row.createCell(index++).setCellValue(pgReexamEligibleStudentsBean.getSubject());
			row.createCell(index++).setCellValue(pgReexamEligibleStudentsBean.getSapid());
			row.createCell(index++).setCellValue(pgReexamEligibleStudentsBean.getFirstName() + " " + pgReexamEligibleStudentsBean.getLastName());
			row.createCell(index++).setCellValue(pgReexamEligibleStudentsBean.getProgram());
			row.createCell(index++).setCellValue(pgReexamEligibleStudentsBean.getPrgmStructApplicable());
			row.createCell(index++).setCellValue(pgReexamEligibleStudentsBean.getFailReason());
		}
	}

}
