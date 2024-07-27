package com.nmims.views;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.web.servlet.view.document.AbstractExcelView;

import com.nmims.beans.FacultyAcadsBean;

public class FacultyAllocationExcelView extends AbstractExcelView{
	
	@Override
	protected void buildExcelDocument(Map model, HSSFWorkbook workbook,	HttpServletRequest request, HttpServletResponse response)
		throws Exception {
 
		//Read list
		List<FacultyAcadsBean> facultyList = (List<FacultyAcadsBean>) model.get("facultyAllocationList");
		//create a wordsheet
		HSSFSheet sheet = workbook.createSheet("Faculty Allocation List");
		
 		int index = 0;
		HSSFRow header = sheet.createRow(0);
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("Acad Month");
		header.createCell(index++).setCellValue("Acad Year");
		header.createCell(index++).setCellValue("Exam Month");
		header.createCell(index++).setCellValue("Exam Year");
		header.createCell(index++).setCellValue("Faculty ID");
		header.createCell(index++).setCellValue("Faculty Allocated Role");
 
		int rowNum = 1;
		for (int i = 0 ; i < facultyList.size(); i++) {
			index = 0;
			//create the row data
			HSSFRow row = sheet.createRow(rowNum++);
			FacultyAcadsBean bean = facultyList.get(i);
			
			row.createCell(index++).setCellValue((i+1));
			row.createCell(index++).setCellValue(bean.getAcadMonth());
			row.createCell(index++).setCellValue(bean.getAcadYear());
			row.createCell(index++).setCellValue(bean.getExamMonth());
			row.createCell(index++).setCellValue(bean.getExamYear());
			row.createCell(index++).setCellValue(bean.getFacultyAllocated());
			row.createCell(index++).setCellValue(bean.getRoleForAllocation());
        }
		sheet.autoSizeColumn(0); sheet.autoSizeColumn(1);
		sheet.autoSizeColumn(2); sheet.autoSizeColumn(3);
		sheet.autoSizeColumn(4); sheet.autoSizeColumn(5);
		sheet.autoSizeColumn(6); sheet.autoSizeColumn(7);
		
		sheet.setAutoFilter(CellRangeAddress.valueOf("A1:G"+(facultyList.size()+1)));
	}
}