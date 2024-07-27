package com.nmims.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;

import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.daos.StudentMarksDAO;

public class NewPassFailReport extends AbstractXlsxStreamingView implements ApplicationContextAware{

	private static ApplicationContext act = null;
	
	@Override
	protected void buildExcelDocument(Map model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		act = getApplicationContext();
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		HashMap<String, StudentExamBean> sapIdStudentsMap = dao.getAllStudents();
		
		ArrayList<StudentMarksBean> passFailList = (ArrayList<StudentMarksBean>)model.get("passFailList");
		
		Sheet sheet = workbook.createSheet("New Pass Fail Report");
		int index = 0;
		Row header = sheet.createRow(0);
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("Exam Year");
		header.createCell(index++).setCellValue("Exam Month");
		header.createCell(index++).setCellValue("SAP ID");
		
		header.createCell(index++).setCellValue("Email ID");
		header.createCell(index++).setCellValue("Mobile No.");
		header.createCell(index++).setCellValue("Validity End Year");
		header.createCell(index++).setCellValue("Validity End Month");
		
		header.createCell(index++).setCellValue("Enrollment Year");
		header.createCell(index++).setCellValue("Enrollment Month");
		header.createCell(index++).setCellValue("Program Structure");
		
		header.createCell(index++).setCellValue("Student Name");
		header.createCell(index++).setCellValue("Program");
		header.createCell(index++).setCellValue("Sem");
		header.createCell(index++).setCellValue("Subject");
		
		header.createCell(index++).setCellValue("Best TEE");
		header.createCell(index++).setCellValue("Best Assignment");
		header.createCell(index++).setCellValue("Total-New Logic");
		header.createCell(index++).setCellValue("New Status");
		
		header.createCell(index++).setCellValue("Last TEE");
		header.createCell(index++).setCellValue("Last Assignment");
		header.createCell(index++).setCellValue("Total-Current Logic");
		header.createCell(index++).setCellValue("Current Status");
		header.createCell(index++).setCellValue("Grace");
	
		int rowNum = 1;
		for (int i = 0 ; i < passFailList.size(); i++) {
			//create the row data
			index = 0;
			Row row = sheet.createRow(rowNum++);
			StudentMarksBean bean = passFailList.get(i);
			StudentExamBean student = sapIdStudentsMap.get(bean.getSapid());
			
			row.createCell(index++).setCellValue(rowNum-1);

			row.createCell(index++).setCellValue(bean.getYear());
			row.createCell(index++).setCellValue(bean.getMonth());
			
			row.createCell(index++).setCellValue(bean.getSapid());
			
			row.createCell(index++).setCellValue(student.getEmailId());
			row.createCell(index++).setCellValue(student.getMobile());
			row.createCell(index++).setCellValue(student.getValidityEndMonth());
			row.createCell(index++).setCellValue(student.getValidityEndYear());
			
			row.createCell(index++).setCellValue(student.getEnrollmentMonth());
			row.createCell(index++).setCellValue(student.getEnrollmentYear());
			row.createCell(index++).setCellValue(student.getPrgmStructApplicable());
						
			row.createCell(index++).setCellValue(student.getFirstName()+" "+student.getLastName());
			row.createCell(index++).setCellValue(bean.getProgram());
			row.createCell(index++).setCellValue(bean.getSem());
			row.createCell(index++).setCellValue(bean.getSubject());
			
			row.createCell(index++).setCellValue(bean.getWritenscore());
			row.createCell(index++).setCellValue(bean.getAssignmentscore());
			row.createCell(index++).setCellValue(bean.getNewTotal());
			row.createCell(index++).setCellValue(bean.getNewStatus());
			
			row.createCell(index++).setCellValue(bean.getOldWrittenScore());
			row.createCell(index++).setCellValue(bean.getOldAssignmentScore());
			row.createCell(index++).setCellValue(bean.getTotal());
			row.createCell(index++).setCellValue(bean.getOldStatus());
			row.createCell(index++).setCellValue(bean.getGracemarks());
			
			
			
        }
		
	}

}
