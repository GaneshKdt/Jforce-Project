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

public class ExamRegisteredStudentMarksExcelView  extends AbstractXlsxStreamingView  implements ApplicationContextAware{
	private static ApplicationContext act = null;
	
	@Override
	protected void buildExcelDocument(Map model, Workbook workbook,	HttpServletRequest request, HttpServletResponse response)
		throws Exception {
 
		act = getApplicationContext();
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		HashMap<String, StudentExamBean> sapIdStudentsMap = dao.getAllStudents();
		HashMap<String, CenterExamBean> icLcMap = dao.getICLCMap();
		
		List<StudentMarksBean> studentMarksList = (List<StudentMarksBean>) model.get("studentMarksList");
		//create a wordsheet
		Sheet sheet = workbook.createSheet("Search Results");
 
		Row header = sheet.createRow(0);
		header.createCell(0).setCellValue("Sr. No.");
		header.createCell(1).setCellValue("Exam Year");
		header.createCell(2).setCellValue("Exam Month");
		header.createCell(3).setCellValue("Center");
		header.createCell(4).setCellValue("SAP ID");
		header.createCell(5).setCellValue("Last Name");
		header.createCell(6).setCellValue("First Name");
		header.createCell(7).setCellValue("Program");
		header.createCell(8).setCellValue("Sem");
		header.createCell(9).setCellValue("Subject");
		header.createCell(10).setCellValue("TEE");
		header.createCell(11).setCellValue("Assignment");
		header.createCell(12).setCellValue("Grace");
		header.createCell(13).setCellValue("Total");
		header.createCell(14).setCellValue("Exam Mode");
		header.createCell(15).setCellValue("IC");
		header.createCell(16).setCellValue("LC");
 
		int rowNum = 1;
		int counter = 1;
		for (int i = 0 ; i < studentMarksList.size(); i++) {
			//create the row data
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
			
			row.createCell(0).setCellValue(counter++);
			row.createCell(1).setCellValue(bean.getYear());
			row.createCell(2).setCellValue(bean.getMonth());
			row.createCell(3).setCellValue(bean.getExamCenterName());
			row.createCell(4).setCellValue(bean.getSapid());
			row.createCell(5).setCellValue(bean.getLastName());
			row.createCell(6).setCellValue(bean.getFirstName());
			row.createCell(7).setCellValue(bean.getProgram());
			row.createCell(8).setCellValue(bean.getSem());
			row.createCell(9).setCellValue(bean.getSubject());
			row.createCell(10).setCellValue(bean.getWritenscore());
			row.createCell(11).setCellValue(bean.getAssignmentscore());
			row.createCell(12).setCellValue(bean.getGracemarks());
			row.createCell(13).setCellValue(bean.getTotal());
			row.createCell(14).setCellValue(bean.getExamMode());
			row.createCell(15).setCellValue(ic);
			row.createCell(16).setCellValue(lc);
	 
        }
	}

}
