package com.nmims.views;

import java.util.ArrayList;
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
import org.springframework.web.servlet.view.document.AbstractExcelView;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.nmims.beans.AssignmentStatusBean;
import com.nmims.beans.CenterExamBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.daos.AssignmentsDAO;
public class AssignmentStatusReportExcelView  extends AbstractXlsxStreamingView  implements ApplicationContextAware{
	
	private static ApplicationContext act = null;
	@Override
	protected void buildExcelDocument(Map model, Workbook workbook,	HttpServletRequest request, HttpServletResponse response)
		throws Exception {
		
		act = getApplicationContext();
		List<AssignmentStatusBean> subjectMappingErrorBeanList = (List<AssignmentStatusBean>) model.get("assignmentStatusList");
		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		HashMap<String,CenterExamBean> getICLCMap = dao.getICLCMap();
		HashMap<String, StudentExamBean> sapIdStudentsMap = dao.getAllStudents();
		Sheet sheet = workbook.createSheet("Report");
 
		Row header = sheet.createRow(0);
		header.createCell(0).setCellValue("Sr. No.");
		header.createCell(1).setCellValue("SAP ID");
		header.createCell(2).setCellValue("First Name");
		header.createCell(3).setCellValue("Last Name");
		//header.createCell(4).setCellValue("Mobile");
		//header.createCell(5).setCellValue("Email Id");

		header.createCell(4).setCellValue("Program Structure");
		header.createCell(5).setCellValue("Enrollment Month");
		header.createCell(6).setCellValue("Enrollment Year");

		
		header.createCell(7).setCellValue("Validity End Month");
		header.createCell(8).setCellValue("Validity End Year");
		
		header.createCell(9).setCellValue("Subject");
		header.createCell(10).setCellValue("Assignment Submitted");
		header.createCell(11).setCellValue("IC");
		header.createCell(12).setCellValue("LC");
		header.createCell(13).setCellValue("Exam Mode");
		header.createCell(14).setCellValue("Semester");
		int rowNum = 1;
		for (int i = 0 ; i < subjectMappingErrorBeanList.size(); i++) {

			Row row = sheet.createRow(rowNum++);
			AssignmentStatusBean bean = subjectMappingErrorBeanList.get(i);
			
			StudentExamBean student = sapIdStudentsMap.get(bean.getSapid());
			String ic = "";
			String lc = "";
			String examMode = "Offline";
			if(student != null){
				//ic = student.getCenterName();
				CenterExamBean center = getICLCMap.get(student.getCenterCode()); 
				if(center != null){
					lc = center.getLc();
					ic = center.getCenterName();
				}
				
			
			}
			if("Online".equals(student.getExamMode())){
				examMode = "Online";
			}
			row.createCell(0).setCellValue(new Double(i+1));
			row.createCell(1).setCellValue(new Double(bean.getSapid()));
			row.createCell(2).setCellValue(student.getFirstName());
			row.createCell(3).setCellValue(student.getLastName());
			//row.createCell(4).setCellValue(student.getMobile());
			//row.createCell(5).setCellValue(student.getEmailId());

			row.createCell(4).setCellValue(student.getPrgmStructApplicable());
			row.createCell(5).setCellValue(student.getEnrollmentMonth());
			row.createCell(6).setCellValue(student.getEnrollmentYear());
			row.createCell(7).setCellValue(student.getValidityEndMonth());
			row.createCell(8).setCellValue(student.getValidityEndYear());
			row.createCell(9).setCellValue(bean.getSubject()); 
			row.createCell(10).setCellValue(bean.getSubmitted());
			row.createCell(11).setCellValue(ic);
			row.createCell(12).setCellValue(lc);
			row.createCell(13).setCellValue(examMode);
			row.createCell(14).setCellValue(bean.getSem());
        }
		
		
	}

}
