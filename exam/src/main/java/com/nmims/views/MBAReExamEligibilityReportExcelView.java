package com.nmims.views;

import java.util.HashMap;
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

import com.nmims.beans.CenterExamBean;
import com.nmims.beans.ReExamEligibleStudentBean;
import com.nmims.beans.ReExamEligibleStudentsResponseBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.UserAuthorizationExamBean;
import com.nmims.daos.StudentMarksDAO;



public class MBAReExamEligibilityReportExcelView extends AbstractXlsxStreamingView  implements ApplicationContextAware{
	
	private static ApplicationContext act = null;
	
	@SuppressWarnings("rawtypes")
	@Override
	protected void buildExcelDocument(Map model, Workbook workbook,	HttpServletRequest request, HttpServletResponse response)
		throws Exception {

		act = getApplicationContext();
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		HashMap<String, CenterExamBean> icLcMap = dao.getICLCMap();
		UserAuthorizationExamBean userAuthorization= (UserAuthorizationExamBean)request.getSession().getAttribute("userAuthorization");

		boolean showStudentContactDetails = true;
		
		if(userAuthorization != null && userAuthorization.getRoles() != null) {
			if(userAuthorization.getRoles().contains("Exam Admin") || userAuthorization.getRoles().contains("Assignment Admin") ||  userAuthorization.getRoles().contains("TEE Admin") ) {
				showStudentContactDetails = true;
			}
		}
		
		ReExamEligibleStudentsResponseBean reportBean = (ReExamEligibleStudentsResponseBean) model.get("reportBean");
		List<ReExamEligibleStudentBean> listOfRecords = reportBean.getListOfFailedOrResitStudentResults(); 
		//create a worksheet
		Sheet sheet = workbook.createSheet("Re Exam Eligibility Report");
		int index = 0;
		Row header = sheet.createRow(0);
		header.createCell(index++).setCellValue("Sr. No.");
		
	    // Student fields
		header.createCell(index++).setCellValue("SAP ID");
		header.createCell(index++).setCellValue("Enrollment Year");
		header.createCell(index++).setCellValue("Enrollment Month");
		header.createCell(index++).setCellValue("Program");
		header.createCell(index++).setCellValue("First Name");
		header.createCell(index++).setCellValue("Last Name");
		if(showStudentContactDetails) {
			header.createCell(index++).setCellValue("Email");
			header.createCell(index++).setCellValue("Mobile");
			header.createCell(index++).setCellValue("Alternate Phone");
		}
		header.createCell(index++).setCellValue("IC Code");
		header.createCell(index++).setCellValue("IC Name");
		

		// Booking details
		header.createCell(index++).setCellValue("Sem");
	    header.createCell(index++).setCellValue("Subject");

		header.createCell(index++).setCellValue("Exam Year");
	    header.createCell(index++).setCellValue("Exam Month");
	    header.createCell(index++).setCellValue("Role");

		header.createCell(index++).setCellValue("Is Pass");
//	    header.createCell(index++).setCellValue("Eligible For Re Exam");
//	    header.createCell(index++).setCellValue("Not Eligible Reason");

	    header.createCell(index++).setCellValue("Number Of Failed Subjects For Student");

		header.createCell(index++).setCellValue("IA Score");
		header.createCell(index++).setCellValue("TEE Score");
		header.createCell(index++).setCellValue("Total Score Obtained");
		
		header.createCell(index++).setCellValue("Image URL");
		
	    header.createCell(index++).setCellValue("IC");
		header.createCell(index++).setCellValue("LC");
 
		int rowNum = 1;
		
		int i = 0;
		for (ReExamEligibleStudentBean bean : listOfRecords) {
			index = 0;
			i++;
			Row row = sheet.createRow(rowNum++);
			
			String ic = "";
			String lc = "";
			

			StudentExamBean student = dao.getSingleStudentsData(bean.getSapid());
			if(student != null){
				ic = student.getCenterName();
				CenterExamBean center = icLcMap.get(student.getCenterCode()); 
				if(center != null){
					lc = center.getLc();
				}
			}
			
			row.createCell(index++).setCellValue((i));
			
		    // Student fields
			row.createCell(index++).setCellValue(student.getSapid());
			row.createCell(index++).setCellValue(new Double(student.getEnrollmentYear()));
			row.createCell(index++).setCellValue(student.getEnrollmentMonth());
			row.createCell(index++).setCellValue(student.getProgram());
			row.createCell(index++).setCellValue(student.getFirstName());
			row.createCell(index++).setCellValue(student.getLastName());
			if(showStudentContactDetails) {
				row.createCell(index++).setCellValue(student.getEmailId());
				row.createCell(index++).setCellValue(student.getMobile());
				row.createCell(index++).setCellValue(student.getAltPhone());
			}
			row.createCell(index++).setCellValue(student.getCenterCode());
			row.createCell(index++).setCellValue(student.getCenterName());
			
			// Booking details
			row.createCell(index++).setCellValue(new Double(student.getSem()));
			row.createCell(index++).setCellValue(bean.getSubject());

			row.createCell(index++).setCellValue(new Double(bean.getExamYear()));
			row.createCell(index++).setCellValue(bean.getExamMonth());

			row.createCell(index++).setCellValue(bean.getRole());
			
			row.createCell(index++).setCellValue(bean.getIsPass());
//			row.createCell(index++).setCellValue(bean.getEligibleForReExam());
//			row.createCell(index++).setCellValue(bean.getNotEligibleReason());

			row.createCell(index++).setCellValue(new Double(bean.getNumberOfSubjects()));

			row.createCell(index++).setCellValue(new Double(bean.getIaScore()));
			row.createCell(index++).setCellValue(new Double(bean.getTeeScore()));
			row.createCell(index++).setCellValue(new Double(bean.getTeeScore() + bean.getIaScore()));

			row.createCell(index++).setCellValue(student.getImageUrl());
			
			row.createCell(index++).setCellValue(ic);
			row.createCell(index++).setCellValue(lc);
		}
	}
}
