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

public class AssignmentCopyCasesExcelView  extends AbstractXlsxStreamingView  implements ApplicationContextAware{
	private static ApplicationContext act = null;
	
	@Override
	protected void buildExcelDocument(Map model, Workbook workbook,	HttpServletRequest request, HttpServletResponse response)
		throws Exception {
 
		act = getApplicationContext();
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		HashMap<String, StudentExamBean> sapIdStudentsMap = dao.getAllStudents();
		HashMap<String, CenterExamBean> icLcMap = dao.getICLCMap();

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
		
		header.createCell(index++).setCellValue("Score");
		header.createCell(index++).setCellValue("IC");
		header.createCell(index++).setCellValue("LC");
		header.createCell(index++).setCellValue("Exam Mode");
		
		
		int rowNum = 1;
		for (int i = 0 ; i < studentList.size(); i++) {
			index = 0;
			//create the row data
			Row row = sheet.createRow(rowNum++);
			AssignmentFileBean bean = studentList.get(i);
			
			StudentExamBean student = sapIdStudentsMap.get(bean.getSapId());
			
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
			row.createCell(index++).setCellValue(rowNum-1);
			row.createCell(index++).setCellValue(new Double(bean.getYear()));
			row.createCell(index++).setCellValue(bean.getMonth());
			
			row.createCell(index++).setCellValue(new Double(bean.getSapId()));
			row.createCell(index++).setCellValue(bean.getFirstName() + " " + bean.getLastName());
			row.createCell(index++).setCellValue(bean.getProgram());
			row.createCell(index++).setCellValue(bean.getSubject());
			row.createCell(index++).setCellValue(new Double("0"));
			
			row.createCell(index++).setCellValue(ic);
			row.createCell(index++).setCellValue(lc);
			row.createCell(index++).setCellValue(examMode);
			
        }
		
		
	}

}
