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
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.view.document.AbstractExcelView;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;

import com.nmims.beans.CenterExamBean;
import com.nmims.beans.PassFailExamBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.daos.StudentMarksDAO;

public class StudentRegistrationsExcelView  extends AbstractXlsxStreamingView  implements ApplicationContextAware{
	private static ApplicationContext act = null;
	
	@Override
	protected void buildExcelDocument(Map model, Workbook workbook,	HttpServletRequest request, HttpServletResponse response)
		throws Exception {
 
		act = getApplicationContext();
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		HashMap<String, StudentExamBean> sapIdStudentsMap = dao.getAllStudents();
		HashMap<String, CenterExamBean> icLcMap = dao.getICLCMap();
		
		List<StudentMarksBean> studentList = (List<StudentMarksBean>) model.get("studentList");
		//create a wordsheet
		Sheet sheet = workbook.createSheet("Search Results");
 
		Row header = sheet.createRow(0);
		header.createCell(0).setCellValue("Sr. No.");
		header.createCell(1).setCellValue("Session Year");
		header.createCell(2).setCellValue("Session Month");
		header.createCell(3).setCellValue("Student ID");
		header.createCell(4).setCellValue("Program");
		header.createCell(5).setCellValue("Sem");
		header.createCell(6).setCellValue("IC");
		header.createCell(7).setCellValue("LC");
		header.createCell(8).setCellValue("Exam Mode");
 
		int rowNum = 1;
		int counter = 1;
		for (int i = 0 ; i < studentList.size(); i++) {
			//create the row data
			Row row = sheet.createRow(rowNum++);
			StudentMarksBean bean = studentList.get(i);
			
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
			row.createCell(0).setCellValue(new Double(counter++));
			row.createCell(1).setCellValue(new Double(bean.getYear()));
			row.createCell(2).setCellValue(bean.getMonth());
			row.createCell(3).setCellValue(new Double(bean.getSapid()));
			row.createCell(4).setCellValue(bean.getProgram());
			row.createCell(5).setCellValue(new Double(bean.getSem()));
			
			row.createCell(6).setCellValue(ic);
			row.createCell(7).setCellValue(lc);
			row.createCell(8).setCellValue(examMode);

	 
        }
	}

}
