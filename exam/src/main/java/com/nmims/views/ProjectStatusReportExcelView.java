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
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.view.document.AbstractExcelView;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;

import com.nmims.beans.CenterExamBean;
import com.nmims.beans.PassFailExamBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.daos.StudentMarksDAO;

public class ProjectStatusReportExcelView  extends AbstractXlsxStreamingView  implements ApplicationContextAware{
	
	private static ApplicationContext act = null;
	
	@Override
	protected void buildExcelDocument(Map model, Workbook workbook,	HttpServletRequest request, HttpServletResponse response)
		throws Exception {
		
		String userId=(String) request.getSession().getAttribute("userId");
		List<String> ICLCRESTRICTED_USER_LIST=(List<String>) request.getSession().getAttribute("ICLCRESTRICTED_USER_LIST");
		
		act = getApplicationContext();
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		HashMap<String, StudentExamBean> sapIdProgramStudentsMap = dao.getAllStudents();
		HashMap<String, CenterExamBean> icLcMap = dao.getICLCMap();
		
		ArrayList<PassFailExamBean> studentMarksList = (ArrayList<PassFailExamBean>) model.get("projectStatusReport");
		//create a wordsheet
		Sheet sheet = workbook.createSheet("Project Submission Status Report");
		
		int index = 0;
		Row header = sheet.createRow(0);
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("Student Name");
		header.createCell(index++).setCellValue("SAP ID");
		header.createCell(index++).setCellValue("ValidityEndMonth");
		header.createCell(index++).setCellValue("ValidityEndYear");
		header.createCell(index++).setCellValue("Program");
		header.createCell(index++).setCellValue("Program Structure");
		header.createCell(index++).setCellValue("Enrollment Month");
		header.createCell(index++).setCellValue("Enrollment Year");
		header.createCell(index++).setCellValue("Subject");
		header.createCell(index++).setCellValue("Written Month");
		header.createCell(index++).setCellValue("Written Year");
		header.createCell(index++).setCellValue("Score");
		
		if(!ICLCRESTRICTED_USER_LIST.contains(userId)) {
		header.createCell(index++).setCellValue("IC");
		header.createCell(index++).setCellValue("LC");
		}
		header.createCell(index++).setCellValue("Exam Mode");
		header.createCell(index++).setCellValue("Email ID");
		header.createCell(index++).setCellValue("Contact No");
		

 
		int rowNum = 1;
		for (int i = 0 ; i < studentMarksList.size(); i++) {
			//create the row data
			index = 0;
			Row row = sheet.createRow(rowNum++);
			PassFailExamBean bean = studentMarksList.get(i);
			
			StudentExamBean student = sapIdProgramStudentsMap.get(bean.getSapid().trim());
			
			String ic = "";
			String lc = "";
			String examMode = "Offline";
			String enrollmentMonth = "";
			String enrollmentYear = "";
			String email = "";
			String mobile = "";
			String ValidityEndYear = "";
			String ValidityEndMonth = "";
			
			if(student != null){
				ic = student.getCenterName();
				CenterExamBean center = icLcMap.get(student.getCenterCode()); 
				if(center != null){
					lc = center.getLc();
				}
				if("Online".equals(student.getExamMode())){
					examMode = "Online";
				}
				
				enrollmentMonth = student.getEnrollmentMonth();
				enrollmentYear = student.getEnrollmentYear();
				email = student.getEmailId();
				mobile = student.getMobile();
				ValidityEndMonth = student.getValidityEndMonth();
				ValidityEndYear = student.getValidityEndYear();
				
			}
			
			row.createCell(index++).setCellValue(i+1);
			row.createCell(index++).setCellValue(bean.getName());
			row.createCell(index++).setCellValue(new Double(bean.getSapid()));
			row.createCell(index++).setCellValue(ValidityEndMonth);
			row.createCell(index++).setCellValue(ValidityEndYear);
			row.createCell(index++).setCellValue(bean.getProgram());
			row.createCell(index++).setCellValue(student.getPrgmStructApplicable());
			row.createCell(index++).setCellValue(enrollmentMonth);
			row.createCell(index++).setCellValue(enrollmentYear);
			row.createCell(index++).setCellValue(bean.getSubject());
			row.createCell(index++).setCellValue(bean.getWrittenMonth());
			row.createCell(index++).setCellValue(bean.getWrittenYear());
			row.createCell(index++).setCellValue(bean.getWrittenscore());
			
			if(!ICLCRESTRICTED_USER_LIST.contains(userId)) {
			row.createCell(index++).setCellValue(ic);
			row.createCell(index++).setCellValue(lc);
			}
			row.createCell(index++).setCellValue(examMode);
			row.createCell(index++).setCellValue(email);
			row.createCell(index++).setCellValue(mobile);
	 
        }
	}
	
	
	

}
