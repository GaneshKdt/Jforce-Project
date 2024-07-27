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

public class AssignmntNormalizedScoreExcelView  extends AbstractXlsxStreamingView  implements ApplicationContextAware{
	
	@Override
	protected void buildExcelDocument(Map model, Workbook workbook,	HttpServletRequest request, HttpServletResponse response)
		throws Exception {
 
		try {
			
		

		List<AssignmentFileBean> studentList = (List<AssignmentFileBean>)model.get("assignmentSubmittedList");
		//create a wordsheet
		Sheet sheet = workbook.createSheet("Assignment Submitted List");
		int index = 0;
		Row header = sheet.createRow(0);
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("Year");
		header.createCell(index++).setCellValue("Month");
		
		header.createCell(index++).setCellValue("Student ID");
		header.createCell(index++).setCellValue("Name");
		header.createCell(index++).setCellValue("Program");
		header.createCell(index++).setCellValue("Subject");
		
		header.createCell(index++).setCellValue("Facult 1 ID");
		header.createCell(index++).setCellValue("Faculty 1 Score");
		
/*		header.createCell(index++).setCellValue("Facult 2 ID");
		header.createCell(index++).setCellValue("Faculty 2 Score");
		
		header.createCell(index++).setCellValue("Facult 3 ID");
		header.createCell(index++).setCellValue("Faculty 3 Score");*/
		
		header.createCell(index++).setCellValue("Normalized Score");
		header.createCell(index++).setCellValue("Normalized Score Rounded");
		
		
		
		int rowNum = 1;
		for (int i = 0 ; i < studentList.size(); i++) {
			index = 0;
			//create the row data
			Row row = sheet.createRow(rowNum++);
			AssignmentFileBean bean = studentList.get(i);
			row.createCell(index++).setCellValue(rowNum-1);
			row.createCell(index++).setCellValue(bean.getYear());
			row.createCell(index++).setCellValue(bean.getMonth());
			/*
			 * if(bean.getSapId() == null) { bean.setSapId(bean.getSapId()); }
			 */
			row.createCell(index++).setCellValue(bean.getSapId());
			row.createCell(index++).setCellValue(bean.getFirstName() + " " + bean.getLastName());
			row.createCell(index++).setCellValue(bean.getProgram());
			row.createCell(index++).setCellValue(bean.getSubject());
			row.createCell(index++).setCellValue(bean.getFacultyId());
			row.createCell(index++).setCellValue(bean.getScore()==null ? "0.0" : bean.getScore());
/*			row.createCell(index++).setCellValue(bean.getFaculty2());
			row.createCell(index++).setCellValue(bean.getFaculty2Score());
			row.createCell(index++).setCellValue(bean.getFaculty3());
			row.createCell(index++).setCellValue(bean.getFaculty3Score());*/
			row.createCell(index++).setCellValue(bean.getNormalizedScore() ==null ? "0.0" : bean.getNormalizedScore());
			row.createCell(index++).setCellValue(bean.getRoundedNormalizedScore() ==null ? "0.0" : bean.getRoundedNormalizedScore());
			
        }
		
		
		
		
		} catch (Exception e) {
			
		}
	}
		
		

}
