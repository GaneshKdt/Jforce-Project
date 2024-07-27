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
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.view.document.AbstractExcelView;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;

import com.nmims.beans.PassFailExamBean;

public class GraceEligibleExcelView  extends AbstractXlsxStreamingView  implements ApplicationContextAware{
	
	@Override
	protected void buildExcelDocument(Map model, Workbook workbook,	HttpServletRequest request, HttpServletResponse response)
		throws Exception {
 
		List<PassFailExamBean> studentMarksList = (List<PassFailExamBean>) model.get("graceList");
		//create a wordsheet
		Sheet sheet = workbook.createSheet("Grace");
 
		Row header = sheet.createRow(0);
		header.createCell(0).setCellValue("Sr. No.");
		header.createCell(1).setCellValue("Written Year");
		header.createCell(2).setCellValue("Written Month");
		header.createCell(3).setCellValue("Assignment Year");
		header.createCell(4).setCellValue("Assignment Month");
		header.createCell(5).setCellValue("SAP ID");
		header.createCell(6).setCellValue("Student Name");
		header.createCell(7).setCellValue("Program");
		header.createCell(8).setCellValue("Sem");
		header.createCell(9).setCellValue("Subject");
		header.createCell(10).setCellValue("TEE");
		header.createCell(11).setCellValue("Assignment");
		header.createCell(12).setCellValue("Total");
 
		int rowNum = 1;
		for (int i = 0 ; i < studentMarksList.size(); i++) {
			//create the row data
			Row row = sheet.createRow(rowNum++);
			PassFailExamBean bean = studentMarksList.get(i);
			row.createCell(0).setCellValue(rowNum);
			row.createCell(1).setCellValue(bean.getWrittenYear());
			row.createCell(2).setCellValue(bean.getWrittenMonth());
			row.createCell(3).setCellValue(bean.getAssignmentYear());
			row.createCell(4).setCellValue(bean.getAssignmentMonth());
			row.createCell(5).setCellValue(bean.getSapid());
			row.createCell(6).setCellValue(bean.getName());
			row.createCell(7).setCellValue(bean.getProgram());
			row.createCell(8).setCellValue(bean.getSem());
			row.createCell(9).setCellValue(bean.getSubject());
			row.createCell(10).setCellValue(bean.getWrittenscore());
			row.createCell(11).setCellValue(bean.getAssignmentscore());
			row.createCell(12).setCellValue(bean.getTotal());
	 
        }
	}

}
