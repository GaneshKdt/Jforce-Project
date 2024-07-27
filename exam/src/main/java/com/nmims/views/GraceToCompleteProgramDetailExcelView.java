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

public class GraceToCompleteProgramDetailExcelView  extends AbstractXlsxStreamingView  implements ApplicationContextAware{
	private static ApplicationContext act = null;
	
	@Override
	protected void buildExcelDocument(Map model, Workbook workbook,	HttpServletRequest request, HttpServletResponse response)
		throws Exception {
 
		act = getApplicationContext();
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		HashMap<String, StudentExamBean> sapIdStudentsMap = dao.getAllStudents();
		HashMap<String, CenterExamBean> icLcMap = dao.getICLCMap();
		
		
		List<PassFailExamBean> studentMarksList = (List<PassFailExamBean>) model.get("programCompleteList");
		//create a wordsheet
		Sheet sheet = workbook.createSheet("Program Completed Students");
 
		Row header = sheet.createRow(0);
		header.createCell(0).setCellValue("Sr. No.");
		header.createCell(1).setCellValue("SAP ID");
		header.createCell(2).setCellValue("Student Name");
		header.createCell(3).setCellValue("Program");
		header.createCell(4).setCellValue("Subject");
		
		header.createCell(5).setCellValue("IC");
		header.createCell(6).setCellValue("LC");
		header.createCell(7).setCellValue("Exam Mode");
		header.createCell(8).setCellValue("GraceMarks");
		header.createCell(9).setCellValue("WrittenScore");
		header.createCell(10).setCellValue("AssignmentScore");
		header.createCell(11).setCellValue("Total");
		header.createCell(12).setCellValue("Remarks");

 
		int rowNum = 1;
		for (int i = 0 ; i < studentMarksList.size(); i++) {
			//create the row data
			Row row = sheet.createRow(rowNum++);
			PassFailExamBean bean = studentMarksList.get(i);
			
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
				
				if("Online".equals(student.getExamMode())){//ExamModeChange
					examMode = "Online";
				}
			}
			
			row.createCell(0).setCellValue(rowNum);
			row.createCell(1).setCellValue(bean.getSapid());
			row.createCell(2).setCellValue(bean.getName());
			row.createCell(3).setCellValue(bean.getProgram());
			row.createCell(4).setCellValue(bean.getSubject());
			
			row.createCell(5).setCellValue(ic);
			row.createCell(6).setCellValue(lc);
			row.createCell(7).setCellValue(examMode);
			row.createCell(8).setCellValue(bean.getGracemarks());
			row.createCell(9).setCellValue(bean.getWrittenscore());
			row.createCell(10).setCellValue(bean.getAssignmentscore());
			row.createCell(11).setCellValue(bean.getTotal());
			row.createCell(12).setCellValue(bean.getRemarks());
        }
	}

}
