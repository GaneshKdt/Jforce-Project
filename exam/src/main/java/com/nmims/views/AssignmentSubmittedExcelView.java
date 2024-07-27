package com.nmims.views;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;

import com.nmims.beans.AssignmentFileBean;
import com.nmims.beans.CenterExamBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.UserAuthorizationExamBean;
import com.nmims.daos.StudentMarksDAO;

public class AssignmentSubmittedExcelView  extends AbstractXlsxStreamingView  implements ApplicationContextAware{
	private static ApplicationContext act = null;
	
	@Override
	protected void buildExcelDocument(Map model, Workbook workbook,	HttpServletRequest request, HttpServletResponse response)
		throws Exception {
		
		act = getApplicationContext();
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		HashMap<String, StudentExamBean> sapIdStudentsMap = dao.getAllStudents();
		HashMap<String, CenterExamBean> icLcMap = dao.getICLCMap();

		UserAuthorizationExamBean userAuthorization= (UserAuthorizationExamBean)request.getSession().getAttribute("userAuthorization");
		
		List<AssignmentFileBean> studentList = (List<AssignmentFileBean>)model.get("assignmentSubmittedList");
		String SUBMITTED_ASSIGNMENT_FILES_PATH = (String)model.get("SUBMITTED_ASSIGNMENT_FILES_PATH");
		//create a wordsheet
		Sheet sheet = workbook.createSheet("Assignment Submitted List");
		int index = 0;
		Row header = sheet.createRow(0);
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("Student ID");
		
		header.createCell(index++).setCellValue("First Name");
		header.createCell(index++).setCellValue("Last Name");
		header.createCell(index++).setCellValue("Email ID");
		header.createCell(index++).setCellValue("Mobile");
		header.createCell(index++).setCellValue("Alt Phone");
		
		header.createCell(index++).setCellValue("Program");
		//header.createCell(index++).setCellValue("Sem");
		header.createCell(index++).setCellValue("Subject");
		header.createCell(index++).setCellValue("Center Code");
		header.createCell(index++).setCellValue("Center Name");
		
		header.createCell(index++).setCellValue("Program Structure");
		header.createCell(index++).setCellValue("Enrollment Month");
		header.createCell(index++).setCellValue("Enrollment Year");
		
		header.createCell(index++).setCellValue("Validity End Month");
		header.createCell(index++).setCellValue("Validity End Year");
		
		header.createCell(index++).setCellValue("IC");
		header.createCell(index++).setCellValue("LC");
		header.createCell(index++).setCellValue("Exam Mode");
		
		boolean showDownloadUrl = userAuthorization != null && (userAuthorization.getRoles().contains("Exam Admin") || userAuthorization.getRoles().contains("Assignment Admin") ||  userAuthorization.getRoles().contains("TEE Admin") );
		if(showDownloadUrl) {
			header.createCell(index++).setCellValue("File Download URL");
		}
		
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
			row.createCell(index++).setCellValue(new Double(bean.getSapId()));
			
			row.createCell(index++).setCellValue(bean.getFirstName());
			row.createCell(index++).setCellValue(bean.getLastName());
			row.createCell(index++).setCellValue(bean.getEmailId());
			row.createCell(index++).setCellValue(bean.getMobile());
			row.createCell(index++).setCellValue(bean.getAltPhone());
			row.createCell(index++).setCellValue(bean.getProgram());
			
			//row.createCell(index++).setCellValue(new Double(bean.getSem()));
			row.createCell(index++).setCellValue(bean.getSubject());
			
			row.createCell(index++).setCellValue(bean.getCenterCode());
			row.createCell(index++).setCellValue(bean.getCenterName());
			
			row.createCell(index++).setCellValue(bean.getPrgmStructApplicable());
			row.createCell(index++).setCellValue(bean.getEnrollmentMonth());
			row.createCell(index++).setCellValue(new Double(bean.getEnrollmentYear()));
			
			row.createCell(index++).setCellValue(bean.getValidityEndMonth());
			row.createCell(index++).setCellValue(new Double(bean.getValidityEndYear()));
			
			row.createCell(index++).setCellValue(ic);
			row.createCell(index++).setCellValue(lc);
			row.createCell(index++).setCellValue(examMode);
			

			if(showDownloadUrl) {
				String downloadPath;
				if(bean != null && !StringUtils.isBlank(bean.getPreviewPath())) { 
					downloadPath = SUBMITTED_ASSIGNMENT_FILES_PATH + bean.getPreviewPath();
				} else {
					downloadPath = "File Not Available";
				}
				row.createCell(index++).setCellValue(downloadPath);
			}
        }	
	}
}