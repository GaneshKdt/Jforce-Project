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
import com.nmims.beans.StudentExamBean;
import com.nmims.daos.AssignmentsDAO;

public class AssignmentEvaluationLevel2ExcelView  extends AbstractXlsxStreamingView  implements ApplicationContextAware{

	private static ApplicationContext act = null;
	@Override
	protected void buildExcelDocument(Map model, Workbook workbook,	HttpServletRequest request, HttpServletResponse response)
		throws Exception {
		act = getApplicationContext();

		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		HashMap<String,StudentExamBean> getAllStudents = dao.getAllStudents();

		List<AssignmentFileBean> studentList =  (List<AssignmentFileBean>)model.get("assignmentEvaluatedList");
		
		//create a wordsheet
		Sheet sheet = workbook.createSheet("Assignment Level 3 Evaluation");
		int index = 0;
		Row header = sheet.createRow(0);
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("Year");
		header.createCell(index++).setCellValue("Month");
		header.createCell(index++).setCellValue("Subject");
		header.createCell(index++).setCellValue("Student ID");
		header.createCell(index++).setCellValue("Student Name");
		header.createCell(index++).setCellValue("Email Id");
		header.createCell(index++).setCellValue("Mobile");
		header.createCell(index++).setCellValue("Program");
		header.createCell(index++).setCellValue("Low Score Reason");
		header.createCell(index++).setCellValue("Is Pass?");
		header.createCell(index++).setCellValue("Original Score");
		header.createCell(index++).setCellValue("Reval Score");
		header.createCell(index++).setCellValue("Faculty 2 Id");
		
		int rowNum = 1;
		for (int i = 0 ; i < studentList.size(); i++) {
			index = 0;
			AssignmentFileBean bean = studentList.get(i);

			StudentExamBean student = getAllStudents.get(bean.getSapId());
			
			//create the row data
			Row row = sheet.createRow(rowNum++);
			
			row.createCell(index++).setCellValue(rowNum-1);
			row.createCell(index++).setCellValue(new Double(bean.getYear()));
			row.createCell(index++).setCellValue(bean.getMonth());
			row.createCell(index++).setCellValue(bean.getSubject());
			
			row.createCell(index++).setCellValue(new Double(bean.getSapId()));
			row.createCell(index++).setCellValue(student.getFirstName() + " " + bean.getLastName());
			row.createCell(index++).setCellValue(student.getEmailId());
			row.createCell(index++).setCellValue(student.getMobile());
			row.createCell(index++).setCellValue(bean.getProgram());
			
			row.createCell(index++).setCellValue(bean.getFinalReason());
			row.createCell(index++).setCellValue(bean.getIsPass());
			row.createCell(index++).setCellValue(bean.getAssignmentscore());
			row.createCell(index++).setCellValue(bean.getRevaluationScore());
			
			row.createCell(index++).setCellValue(bean.getFaculty2());
			
		}
		
		
	}

}
