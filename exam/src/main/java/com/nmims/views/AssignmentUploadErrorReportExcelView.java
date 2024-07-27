package com.nmims.views;

import java.util.ArrayList;
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

import com.nmims.beans.AssignmentStatusBean;
import com.nmims.beans.PassFailExamBean;

public class AssignmentUploadErrorReportExcelView  extends AbstractXlsxStreamingView  implements ApplicationContextAware{
	
	@Override
	protected void buildExcelDocument(Map model, Workbook workbook,	HttpServletRequest request, HttpServletResponse response)
		throws Exception {
 
		List<AssignmentStatusBean> subjectMappingErrorBeanList = (List<AssignmentStatusBean>) model.get("subjectMappingErrorBeanList");
		//create a wordsheet
		Sheet sheet = workbook.createSheet("Report");
 
		Row header = sheet.createRow(0);
		header.createCell(0).setCellValue("Row Number");
		header.createCell(1).setCellValue("SAP ID");
		header.createCell(2).setCellValue("Subject");
		header.createCell(3).setCellValue("Assignment Submitted");
		header.createCell(4).setCellValue("Error Type");
		header.createCell(5).setCellValue("Error Message");

 
		int rowNum = 1;
		for (int i = 0 ; i < subjectMappingErrorBeanList.size(); i++) {
			//create the row data
			Row row = sheet.createRow(rowNum++);
			AssignmentStatusBean bean = subjectMappingErrorBeanList.get(i);
			row.createCell(0).setCellValue(new Double(bean.getRowNumber()));
			row.createCell(1).setCellValue(new Double(bean.getSapid()));
			row.createCell(2).setCellValue(bean.getSubject());
			row.createCell(3).setCellValue(bean.getSubmitted());
			row.createCell(4).setCellValue(bean.getErrorType());
			row.createCell(5).setCellValue(bean.getErrorMessage());
	 
        }
	}

}
