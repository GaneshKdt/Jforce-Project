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
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.view.document.AbstractExcelView;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;

import com.nmims.beans.AssignmentFileBean;
import com.nmims.beans.ResultDomain;

public class CopyCaseExcelView  extends AbstractXlsxStreamingView  implements ApplicationContextAware{
	
	@Override
	protected void buildExcelDocument(Map model, Workbook workbook,	HttpServletRequest request, HttpServletResponse response)
		throws Exception {
 

		List<ResultDomain> copyResult = (List<ResultDomain>)model.get("copyResult");
		//create a wordsheet
		Sheet sheet = workbook.createSheet("Copy Case Result");
		int index = 0;
		Row header = sheet.createRow(0);
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("Subject");
		header.createCell(index++).setCellValue("Student ID 1 ");
		header.createCell(index++).setCellValue("First Name 1");
		header.createCell(index++).setCellValue("Last Name 1 ");
		header.createCell(index++).setCellValue("Program 1");
		
		header.createCell(index++).setCellValue("Student ID 2 ");
		header.createCell(index++).setCellValue("First Name 2");
		header.createCell(index++).setCellValue("Last Name 2 ");
		header.createCell(index++).setCellValue("Program 2");
		/*header.createCell(index++).setCellValue("Number of Lines matched");*/
		header.createCell(index++).setCellValue("Matching %");
		
		
		int rowNum = 1;
		for (int i = 0 ; i < copyResult.size(); i++) {
			index = 0;
			//create the row data
			Row row = sheet.createRow(rowNum++);
			ResultDomain bean = copyResult.get(i);
			row.createCell(index++).setCellValue(rowNum-1);
			row.createCell(index++).setCellValue(bean.getSubject());
			
			row.createCell(index++).setCellValue(new Double(bean.getSapId1()));
			row.createCell(index++).setCellValue(bean.getFirstName1());
			row.createCell(index++).setCellValue(bean.getLastName1());
			row.createCell(index++).setCellValue(bean.getProgram1());
			
			row.createCell(index++).setCellValue(new Double(bean.getSapId2()));
			row.createCell(index++).setCellValue(bean.getFirstName2());
			row.createCell(index++).setCellValue(bean.getLastName2());
			row.createCell(index++).setCellValue(bean.getProgram2());
			/*row.createCell(index++).setCellValue(bean.getNoOfMatches());*/
			row.createCell(index++).setCellValue(bean.getMatching());
        }
		
		sheet.autoSizeColumn(0); sheet.autoSizeColumn(1);
		sheet.autoSizeColumn(2); sheet.autoSizeColumn(3);
		sheet.autoSizeColumn(4); sheet.autoSizeColumn(5);
		sheet.autoSizeColumn(6); sheet.autoSizeColumn(7);
		sheet.autoSizeColumn(8); sheet.autoSizeColumn(9);
		sheet.autoSizeColumn(10);
		
		
	
	}

}
