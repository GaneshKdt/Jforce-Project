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
import com.nmims.daos.StudentMarksDAO;

public class SubjectReportExcelView   extends AbstractXlsxStreamingView  implements ApplicationContextAware {
	
	@Override
	protected void buildExcelDocument(Map model, Workbook workbook,	HttpServletRequest request, HttpServletResponse response)
		throws Exception {
		
		List<ExecutiveExamOrderBean> subjectEntryReportList = (List<ExecutiveExamOrderBean>) model.get("subjectEntryReportList");
		
		//create a wordsheet :- START
		Sheet sheet = workbook.createSheet("Report Of Subject Entry List");
		
		int index = 0;
		Row header = sheet.createRow(0);
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("Subject ");
		header.createCell(index++).setCellValue("Program ");
		header.createCell(index++).setCellValue("Program Struct Applicable");
		header.createCell(index++).setCellValue("Acad Month");
		header.createCell(index++).setCellValue("Acad Year");
		header.createCell(index++).setCellValue("Created By");
		header.createCell(index++).setCellValue("Created Date");
		header.createCell(index++).setCellValue("Last Modified By");
		header.createCell(index++).setCellValue("Last Modified Date");
 
		int rowNum = 1;
		for (int i = 0 ; i < subjectEntryReportList.size(); i++) {
			//create the row data
			index = 0;
			Row row = sheet.createRow(rowNum++);
			ExecutiveExamOrderBean bean = subjectEntryReportList.get(i);
			
			row.createCell(index++).setCellValue(rowNum-1);
			row.createCell(index++).setCellValue(bean.getSubject());
			row.createCell(index++).setCellValue(bean.getProgram());
			row.createCell(index++).setCellValue(bean.getPrgmStructApplicable());
			row.createCell(index++).setCellValue(bean.getAcadMonth());
			row.createCell(index++).setCellValue(bean.getAcadYear());
			row.createCell(index++).setCellValue(bean.getCreatedBy());
			row.createCell(index++).setCellValue(bean.getCreatedDate());
			row.createCell(index++).setCellValue(bean.getLastModifiedBy());
			row.createCell(index++).setCellValue(bean.getLastModifiedDate());
        }
		
		sheet.autoSizeColumn(0); sheet.autoSizeColumn(1);
		sheet.autoSizeColumn(2); sheet.autoSizeColumn(3);
		sheet.autoSizeColumn(4); sheet.autoSizeColumn(5);
		
	
	}
}
