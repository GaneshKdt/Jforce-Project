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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.view.document.AbstractExcelView;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;

import com.nmims.daos.StudentMarksDAO;
import com.nmims.beans.CenterExamBean;
import com.nmims.beans.PassFailExamBean;
import com.nmims.beans.StudentExamBean;

public class ActiveStudentSemWiseRegistrationExcelView  extends AbstractXlsxStreamingView  implements ApplicationContextAware{
	private ApplicationContext act = null;
	
	@Override
	protected void buildExcelDocument(Map model, Workbook workbook,	HttpServletRequest request, HttpServletResponse response)
		throws Exception {
		
		String userId=(String) request.getSession().getAttribute("userId");
		List<String> ICLCRESTRICTED_USER_LIST=(List<String>) request.getSession().getAttribute("ICLCRESTRICTED_USER_LIST");

		act = getApplicationContext();
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		HashMap<String, CenterExamBean> icLcMap = dao.getICLCMap();
		HashMap<String, StudentExamBean> sapIdStudentsMap = dao.getAllStudents();
		ArrayList<String> keysList = new ArrayList<String>();
		ArrayList<StudentExamBean> activeStudentsWithRecentRegistrationsList = (ArrayList<StudentExamBean>)model.get("activeStudentsWithRecentRegistrationsList");
		Sheet sheet = workbook.createSheet("Active Student With Recent Registration");
		Sheet passFailSheet = workbook.createSheet("Active Student Details");
		int index = 0;
		
		Row header = sheet.createRow(0);
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("SAP ID");
		header.createCell(index++).setCellValue("First Name");
		header.createCell(index++).setCellValue("Last Name");
		header.createCell(index++).setCellValue("Program");
		if(!ICLCRESTRICTED_USER_LIST.contains(userId)) {
		header.createCell(index++).setCellValue("Mode Of Exam");
		}
		header.createCell(index++).setCellValue("Program Structure");
	//	header.createCell(index++).setCellValue("Mobile Number");
	//	header.createCell(index++).setCellValue("Email Id");
		if(!ICLCRESTRICTED_USER_LIST.contains(userId)) {
		header.createCell(index++).setCellValue("Learning Center");
		header.createCell(index++).setCellValue("Information Center");
		}
		header.createCell(index++).setCellValue("Enrollment Month");
		header.createCell(index++).setCellValue("Enrollment Year");
		header.createCell(index++).setCellValue("Validity End Month");
		header.createCell(index++).setCellValue("Validity End Year");
		
		header.createCell(index++).setCellValue("Most Recent Registration Made");
		header.createCell(index++).setCellValue("GAP In Most Recent Reg and CurrentDate(In Days)");
		
		int rowNum = 1;
		int i =0;
		for(int j =0;j<activeStudentsWithRecentRegistrationsList.size();j++){ //Cannot use forEach since map cannot be cast into ArraList//
			index = 0;
			
			Row row = sheet.createRow(rowNum++);
			String learningCenter = "";
			StudentExamBean student = sapIdStudentsMap.get(activeStudentsWithRecentRegistrationsList.get(j).getSapid());
			student.setMostRecentRegistration(activeStudentsWithRecentRegistrationsList.get(j).getMostRecentRegistration());
			student.setGapInMostRecentRegistrationAndCurrentDateInDays(activeStudentsWithRecentRegistrationsList.get(j).getGapInMostRecentRegistrationAndCurrentDateInDays());
			CenterExamBean center = icLcMap.get(student.getCenterCode()); 
			keysList.add(student.getSapid()+"-"+student.getMostRecentRegistration());
			if(center != null){
				learningCenter = center.getLc();
			}
			if("4".equals(student.getMostRecentRegistration())){
				student.setGapInMostRecentRegistrationAndCurrentDateInDays("NA");
			}
			
			row.createCell(index++).setCellValue(i+1);
			row.createCell(index++).setCellValue(student.getSapid());
			row.createCell(index++).setCellValue(student.getFirstName());
			row.createCell(index++).setCellValue(student.getLastName());
			row.createCell(index++).setCellValue(student.getProgram());
			if(!ICLCRESTRICTED_USER_LIST.contains(userId)) {
			row.createCell(index++).setCellValue(student.getExamMode());
			}
			row.createCell(index++).setCellValue(student.getPrgmStructApplicable());
		//	row.createCell(index++).setCellValue(student.getMobile());
		//	row.createCell(index++).setCellValue(student.getEmailId());
			if(!ICLCRESTRICTED_USER_LIST.contains(userId)) {
			row.createCell(index++).setCellValue(learningCenter);
			row.createCell(index++).setCellValue(student.getCenterName());
			}
			row.createCell(index++).setCellValue(student.getEnrollmentMonth());
			row.createCell(index++).setCellValue(student.getEnrollmentYear());
			row.createCell(index++).setCellValue(student.getValidityEndMonth());
			row.createCell(index++).setCellValue(student.getValidityEndYear());
			
			row.createCell(index++).setCellValue(student.getMostRecentRegistration());
			row.createCell(index++).setCellValue(student.getGapInMostRecentRegistrationAndCurrentDateInDays());
			
		}
		
	
	}

}
