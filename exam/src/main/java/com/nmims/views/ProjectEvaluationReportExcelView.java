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
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.view.document.AbstractExcelView;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;

import com.nmims.beans.AssignmentFileBean;
import com.nmims.beans.CenterExamBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.daos.StudentMarksDAO;

public class ProjectEvaluationReportExcelView  extends AbstractXlsxStreamingView  implements ApplicationContextAware {
private static ApplicationContext act = null;
	
	@Override
	protected void buildExcelDocument(Map model, Workbook workbook,	HttpServletRequest request, HttpServletResponse response)
		throws Exception {
 
		act = getApplicationContext();
		

		List<AssignmentFileBean> projectEvaluationList = (List<AssignmentFileBean>)model.get("projectFileList");
		//create a wordsheet
		Sheet sheet = workbook.createSheet("Project Evaluation Report");
		int index = 0;
		Row header = sheet.createRow(0);
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("Year");
		header.createCell(index++).setCellValue("Month");
		header.createCell(index++).setCellValue("Program");
		header.createCell(index++).setCellValue("Student ID");
		header.createCell(index++).setCellValue("Student Name");
		header.createCell(index++).setCellValue("Subject");
		header.createCell(index++).setCellValue("Faculty ID");
		header.createCell(index++).setCellValue("Faculty Name");
		header.createCell(index++).setCellValue("Evaluation Date");
		header.createCell(index++).setCellValue("Status");
		header.createCell(index++).setCellValue("Attempts");
		header.createCell(index++).setCellValue("Evaluated");
		header.createCell(index++).setCellValue("Remarks");
		header.createCell(index++).setCellValue("Evaluation Count");
		header.createCell(index++).setCellValue("Evaluation Score");
		header.createCell(index++).setCellValue("Reason");
		header.createCell(index++).setCellValue("Re-evaluation Score");
		header.createCell(index++).setCellValue("Marked For Revaluation");
		header.createCell(index++).setCellValue("Re-evaluated");
		header.createCell(index++).setCellValue("Q1 Marks");
		header.createCell(index++).setCellValue("Q2 Marks");
		header.createCell(index++).setCellValue("Q3 Marks");
		header.createCell(index++).setCellValue("Q4 Marks");
		header.createCell(index++).setCellValue("Q5 Marks");
		header.createCell(index++).setCellValue("Q6 Marks");
		header.createCell(index++).setCellValue("Q7 Marks");
		header.createCell(index++).setCellValue("Q8 Marks");
		header.createCell(index++).setCellValue("Q9 Marks");
		header.createCell(index++).setCellValue("Q1 Remarks");
		header.createCell(index++).setCellValue("Q2 Remarks");
		header.createCell(index++).setCellValue("Q3 Remarks");
		header.createCell(index++).setCellValue("Q4 Remarks");
		header.createCell(index++).setCellValue("Q5 Remarks");
		header.createCell(index++).setCellValue("Q6 Remarks");
		header.createCell(index++).setCellValue("Q7 Remarks");
		header.createCell(index++).setCellValue("Q8 Remarks");
		header.createCell(index++).setCellValue("Q9 Remarks");
		header.createCell(index++).setCellValue("Q1 Re-Val Marks");
		header.createCell(index++).setCellValue("Q2 Re-Val Marks");
		header.createCell(index++).setCellValue("Q3 Re-Val Marks");
		header.createCell(index++).setCellValue("Q4 Re-Val Marks");
		header.createCell(index++).setCellValue("Q5 Re-Val Marks");
		header.createCell(index++).setCellValue("Q6 Re-Val Marks");
		header.createCell(index++).setCellValue("Q7 Re-Val Marks");
		header.createCell(index++).setCellValue("Q8 Re-Val Marks");
		header.createCell(index++).setCellValue("Q9 Re-Val Marks");
		header.createCell(index++).setCellValue("Q1 Re-Val Remarks");
		header.createCell(index++).setCellValue("Q2 Re-Val Remarks");
		header.createCell(index++).setCellValue("Q3 Re-Val Remarks");
		header.createCell(index++).setCellValue("Q4 Re-Val Remarks");
		header.createCell(index++).setCellValue("Q5 Re-Val Remarks");
		header.createCell(index++).setCellValue("Q6 Re-Val Remarks");
		header.createCell(index++).setCellValue("Q7 Re-Val Remarks");
		header.createCell(index++).setCellValue("Q8 Re-Val Remarks");
		header.createCell(index++).setCellValue("Q9 Re-Val Remarks");
		header.createCell(index++).setCellValue("Revaluation Faculty Id");
		header.createCell(index++).setCellValue("Re-Evaluation Count");
		header.createCell(index++).setCellValue("Re-Evaluation Reason");
		
			
		
		int rowNum = 1;
		for (int i = 0 ; i < projectEvaluationList.size(); i++) {
			index = 0;
			//create the row data
			Row row = sheet.createRow(rowNum++);
			AssignmentFileBean projectBean = projectEvaluationList.get(i);
			
			
		
			row.createCell(index++).setCellValue((i+1));
			row.createCell(index++).setCellValue(new Double(projectBean.getYear()));
			row.createCell(index++).setCellValue(projectBean.getMonth());
			
			row.createCell(index++).setCellValue(projectBean.getProgram());
			row.createCell(index++).setCellValue(new Double(projectBean.getSapId()));
			row.createCell(index++).setCellValue(projectBean.getSfName()+" "+projectBean.getSlName());
			row.createCell(index++).setCellValue(projectBean.getSubject());
			row.createCell(index++).setCellValue(projectBean.getFacultyId());
			row.createCell(index++).setCellValue(projectBean.getFirstName()+" "+projectBean.getLastName());
			if(projectBean.getEvaluationDate()==null) {
				projectBean.setEvaluationDate("");
			}
			row.createCell(index++).setCellValue(projectBean.getEvaluationDate());
			if(projectBean.getStatus()==null) {
				projectBean.setStatus("");
			}
			row.createCell(index++).setCellValue(projectBean.getStatus());
			row.createCell(index++).setCellValue(projectBean.getAttempts());
			row.createCell(index++).setCellValue(projectBean.getEvaluated());
			if(projectBean.getRemarks()==null) {
				projectBean.setRemarks("");
			}
			row.createCell(index++).setCellValue(projectBean.getRemarks());
			row.createCell(index++).setCellValue(projectBean.getEvaluationCount());
			row.createCell(index++).setCellValue(projectBean.getScore());
			row.createCell(index++).setCellValue(projectBean.getReason());
			row.createCell(index++).setCellValue(projectBean.getRevaluationScore());
			row.createCell(index++).setCellValue(projectBean.getMarkedForRevaluation());
			row.createCell(index++).setCellValue(projectBean.getRevaluated()); 
			
			row.createCell(index++).setCellValue(projectBean.getQ1Marks());
			row.createCell(index++).setCellValue(projectBean.getQ2Marks());
			row.createCell(index++).setCellValue(projectBean.getQ3Marks());
			row.createCell(index++).setCellValue(projectBean.getQ4Marks());
			row.createCell(index++).setCellValue(projectBean.getQ5Marks());
			row.createCell(index++).setCellValue(projectBean.getQ6Marks());
			row.createCell(index++).setCellValue(projectBean.getQ7Marks());
			row.createCell(index++).setCellValue(projectBean.getQ8Marks());
			row.createCell(index++).setCellValue(projectBean.getQ9Marks());
			row.createCell(index++).setCellValue(projectBean.getQ1Remarks());
			row.createCell(index++).setCellValue(projectBean.getQ2Remarks());
			row.createCell(index++).setCellValue(projectBean.getQ3Remarks());
			row.createCell(index++).setCellValue(projectBean.getQ4Remarks());
			row.createCell(index++).setCellValue(projectBean.getQ5Remarks());
			row.createCell(index++).setCellValue(projectBean.getQ6Remarks());
			row.createCell(index++).setCellValue(projectBean.getQ7Remarks());
			row.createCell(index++).setCellValue(projectBean.getQ8Remarks());
			row.createCell(index++).setCellValue(projectBean.getQ9Remarks());
			row.createCell(index++).setCellValue(projectBean.getQ1RevalMarks());
			row.createCell(index++).setCellValue(projectBean.getQ2RevalMarks());
			row.createCell(index++).setCellValue(projectBean.getQ3RevalMarks());
			row.createCell(index++).setCellValue(projectBean.getQ4RevalMarks());
			row.createCell(index++).setCellValue(projectBean.getQ5RevalMarks());
			row.createCell(index++).setCellValue(projectBean.getQ6RevalMarks());
			row.createCell(index++).setCellValue(projectBean.getQ7RevalMarks());
			row.createCell(index++).setCellValue(projectBean.getQ8RevalMarks());
			row.createCell(index++).setCellValue(projectBean.getQ9RevalMarks());
			row.createCell(index++).setCellValue(projectBean.getQ1RevalRemarks());
			row.createCell(index++).setCellValue(projectBean.getQ2RevalRemarks());
			row.createCell(index++).setCellValue(projectBean.getQ3RevalRemarks());
			row.createCell(index++).setCellValue(projectBean.getQ4RevalRemarks());
			row.createCell(index++).setCellValue(projectBean.getQ5RevalRemarks());
			row.createCell(index++).setCellValue(projectBean.getQ6RevalRemarks());
			row.createCell(index++).setCellValue(projectBean.getQ7RevalRemarks());
			row.createCell(index++).setCellValue(projectBean.getQ8RevalRemarks());
			row.createCell(index++).setCellValue(projectBean.getQ9RevalRemarks());
			row.createCell(index++).setCellValue(projectBean.getFacultyIdRevaluation());
			row.createCell(index++).setCellValue(projectBean.getRevaluationCount());
			row.createCell(index++).setCellValue(projectBean.getReason());
			
		}
		sheet.autoSizeColumn(0); 
		sheet.autoSizeColumn(1);
		sheet.autoSizeColumn(2); 
		sheet.autoSizeColumn(3);
		sheet.autoSizeColumn(4);
		sheet.autoSizeColumn(5);
		sheet.autoSizeColumn(6); 
		sheet.autoSizeColumn(7);
		sheet.autoSizeColumn(8);
		sheet.autoSizeColumn(9);
		sheet.autoSizeColumn(10);
	
			
}
}