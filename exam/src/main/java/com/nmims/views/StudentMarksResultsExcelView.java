package com.nmims.views;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;

import com.nmims.beans.CenterExamBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.daos.StudentMarksDAO;

public class StudentMarksResultsExcelView extends AbstractXlsxStreamingView  implements ApplicationContextAware{
	
	private static ApplicationContext act = null;
	
	
	
	@Override
	protected void buildExcelDocument(Map model, Workbook workbook,	HttpServletRequest request, HttpServletResponse response)
		throws Exception {
		int index = 0;
		act = getApplicationContext();
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		HashMap<String, StudentExamBean> sapIdStudentsMap = dao.getAllStudents();
		HashMap<String, CenterExamBean> icLcMap = dao.getICLCMap();
		
		List<StudentMarksBean> studentMarksList = (List<StudentMarksBean>) model.get("studentMarksList");
		//create a wordsheet
		//HSSFSheet sheet = workbook.createSheet("Search Results");
		
		Sheet sheet = workbook.createSheet("Search Results");
 
		//HSSFRow header = sheet.createRow(0);
		Row header = sheet.createRow(0);
		
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("Exam Year");
		header.createCell(index++).setCellValue("Exam Month");
		header.createCell(index++).setCellValue("GR No.");
		header.createCell(index++).setCellValue("SAP ID");
		header.createCell(index++).setCellValue("Student Name");
		header.createCell(index++).setCellValue("Program");
		header.createCell(index++).setCellValue("Sem");
		header.createCell(index++).setCellValue("Subject");
		header.createCell(index++).setCellValue("Sify Subject Code");
		header.createCell(index++).setCellValue("TEE");
		header.createCell(index++).setCellValue("Assignment");
		header.createCell(index++).setCellValue("Grace");
		header.createCell(index++).setCellValue("Total");
		header.createCell(index++).setCellValue("Attempt");
		header.createCell(index++).setCellValue("Marked For Revaluation");
		header.createCell(index++).setCellValue("Marked For Photocopy");
		header.createCell(index++).setCellValue("IC");
		header.createCell(index++).setCellValue("LC");
		header.createCell(index++).setCellValue("Exam Mode");
		header.createCell(index++).setCellValue("Old TEE Score");
		header.createCell(index++).setCellValue("Old Assignment Score");
		header.createCell(index++).setCellValue("Assignment Fail Reason");
		int rowNum = 1;
		int rowCount=1;
		for (int i = 0 ; i < studentMarksList.size(); i++) {
			//create the row data
			//HSSFRow row = sheet.createRow(rowNum++);
			
			index = 0;
			Row row = sheet.createRow(rowNum++);
			StudentMarksBean bean = studentMarksList.get(i);
			
			StudentExamBean student = sapIdStudentsMap.get(bean.getSapid());
			
			String ic = "";
			String lc = "";
			String examMode = "Offline";
			if(student != null){
				ic = student.getCenterName();
				CenterExamBean center = icLcMap.get(student.getCenterCode()); 
				if(center != null){
					lc = center.getLc();
				}
				
				if("Online".equals(student.getExamMode())){
					examMode = "Online";
				}
			}
			
			row.createCell(index++).setCellValue(rowCount++);
			row.createCell(index++).setCellValue(bean.getYear());
			row.createCell(index++).setCellValue(bean.getMonth());
			row.createCell(index++).setCellValue(bean.getGrno());
			row.createCell(index++).setCellValue(bean.getSapid());
			row.createCell(index++).setCellValue(bean.getStudentname());
			row.createCell(index++).setCellValue(bean.getProgram());
			row.createCell(index++).setCellValue(bean.getSem());
			row.createCell(index++).setCellValue(bean.getSubject());
			row.createCell(index++).setCellValue(bean.getSifySubjectCode());
			row.createCell(index++).setCellValue(bean.getWritenscore());
			row.createCell(index++).setCellValue(bean.getAssignmentscore());
			row.createCell(index++).setCellValue(bean.getGracemarks());
			row.createCell(index++).setCellValue(bean.getTotal());
			row.createCell(index++).setCellValue(bean.getAttempt());
			row.createCell(index++).setCellValue(bean.getMarkedForRevaluation());
			row.createCell(index++).setCellValue(bean.getMarkedForPhotocopy());
			row.createCell(index++).setCellValue(ic);
			row.createCell(index++).setCellValue(lc);
			row.createCell(index++).setCellValue(examMode);
			row.createCell(index++).setCellValue(bean.getOldWrittenScore());
			row.createCell(index++).setCellValue(bean.getOldAssignmentScore());
			row.createCell(index++).setCellValue(bean.getAssignmentRemarks());
	 
        }
		

	}





}
