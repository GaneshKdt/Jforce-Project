package com.nmims.views;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;

import com.nmims.beans.CaseStudyExamBean;
import com.nmims.beans.CenterExamBean;
import com.nmims.beans.PassFailExamBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.daos.StudentMarksDAO;

public class EvaluatedCaseStudyReport  extends AbstractXlsxStreamingView  implements ApplicationContextAware{
	
	private static ApplicationContext act = null;
	
	@Override
	protected void buildExcelDocument(Map model, Workbook workbook,	HttpServletRequest request, HttpServletResponse response)
		throws Exception {
 
		act = getApplicationContext();
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		HashMap<String, StudentExamBean> sapIdProgramStudentsMap = dao.getAllStudentProgramMap();
		HashMap<String, CenterExamBean> icLcMap = dao.getICLCMap();
		
		List<CaseStudyExamBean> studentMarksList = (List<CaseStudyExamBean>) model.get("getEvaluatedCaseStudyList");
		//create a wordsheet
		Sheet sheet = workbook.createSheet("CaseStudy Completed Students");
		
		int index = 0;
		Row header = sheet.createRow(0);
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("SAP ID");
		header.createCell(index++).setCellValue("Program");
		header.createCell(index++).setCellValue("Program Structure");
		header.createCell(index++).setCellValue("Enrollment Month");
		header.createCell(index++).setCellValue("Enrollment Year");
		header.createCell(index++).setCellValue("Exam Mode");
		header.createCell(index++).setCellValue("Faculty Id ");
		header.createCell(index++).setCellValue("Faculty Name");
		header.createCell(index++).setCellValue("Topic");
		header.createCell(index++).setCellValue("Grade");
		header.createCell(index++).setCellValue("Total Score");
		header.createCell(index++).setCellValue("Remarks");
		header.createCell(index++).setCellValue("Reason");
		header.createCell(index++).setCellValue("Q1 Marks");
		header.createCell(index++).setCellValue("Q1 Remarks");
		header.createCell(index++).setCellValue("Q2 Marks");
		header.createCell(index++).setCellValue("Q2 Remarks");
		header.createCell(index++).setCellValue("Q3 Marks");
		header.createCell(index++).setCellValue("Q3 Remarks");
		header.createCell(index++).setCellValue("Q4 Marks");
		header.createCell(index++).setCellValue("Q4 Remarks");
		header.createCell(index++).setCellValue("Q5 Marks");
		header.createCell(index++).setCellValue("Q5 Remarks");
		header.createCell(index++).setCellValue("Q6 Marks");
		header.createCell(index++).setCellValue("Q6 Remarks");
 
		int rowNum = 1;
		for (int i = 0 ; i < studentMarksList.size(); i++) {
			//create the row data
			index = 0;
			Row row = sheet.createRow(rowNum++);
			CaseStudyExamBean bean = studentMarksList.get(i);
	
			StudentExamBean student = sapIdProgramStudentsMap.get(bean.getSapid().trim()+bean.getProgram().trim());
		
			String examMode = "Offline";
			String enrollmentMonth = "";
			String enrollmentYear = "";
			
			
			if(student != null){
				if("Online".equals(student.getExamMode())){
					examMode = "Online";
				}
				enrollmentMonth = student.getEnrollmentMonth();
				enrollmentYear = student.getEnrollmentYear();
			}
			
			row.createCell(index++).setCellValue(i+1);
			row.createCell(index++).setCellValue(new Double(bean.getSapid()));
			row.createCell(index++).setCellValue(bean.getProgram());
			row.createCell(index++).setCellValue(bean.getPrgmStructApplicable());
			row.createCell(index++).setCellValue(enrollmentMonth);
			row.createCell(index++).setCellValue(enrollmentYear);
			row.createCell(index++).setCellValue(examMode);
			row.createCell(index++).setCellValue(bean.getFacultyId());
			row.createCell(index++).setCellValue(bean.getFirstName() +" "+bean.getLastName());
			row.createCell(index++).setCellValue(bean.getTopic());
			row.createCell(index++).setCellValue(bean.getGrade());
			row.createCell(index++).setCellValue(bean.getScore());
			row.createCell(index++).setCellValue(bean.getRemarks());
			row.createCell(index++).setCellValue(bean.getReason());
			row.createCell(index++).setCellValue(bean.getQ1Marks());
			row.createCell(index++).setCellValue(bean.getQ1Remarks());
			row.createCell(index++).setCellValue(bean.getQ2Marks());
			row.createCell(index++).setCellValue(bean.getQ2Remarks());
			row.createCell(index++).setCellValue(bean.getQ3Marks());
			row.createCell(index++).setCellValue(bean.getQ3Remarks());
			row.createCell(index++).setCellValue(bean.getQ4Marks());
			row.createCell(index++).setCellValue(bean.getQ4Remarks());
			row.createCell(index++).setCellValue(bean.getQ5Marks());
			row.createCell(index++).setCellValue(bean.getQ5Remarks());
			row.createCell(index++).setCellValue(bean.getQ6Marks());
			row.createCell(index++).setCellValue(bean.getQ6Remarks());
        }
	}
	
	
	

}
