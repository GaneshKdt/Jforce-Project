package com.nmims.views;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
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

public class StudentsExcelView  extends AbstractXlsxStreamingView  implements ApplicationContextAware{
	private static ApplicationContext act = null;
	
	@Override
	protected void buildExcelDocument(Map model, Workbook workbook,	HttpServletRequest request, HttpServletResponse response)
		throws Exception {
		
		act = getApplicationContext();
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		HashMap<String, StudentExamBean> sapIdStudentsMap = dao.getAllStudents();
		HashMap<String, CenterExamBean> icLcMap = dao.getICLCMap();

		List<StudentExamBean> studentList = (List<StudentExamBean>)model.get("studentList");
		//create a wordsheet
		Sheet sheet = workbook.createSheet("Students");
 
		Row header = sheet.createRow(0);
		header.createCell(0).setCellValue("Sr. No.");
		header.createCell(1).setCellValue("SAP ID");
		header.createCell(2).setCellValue("Sem");
		header.createCell(3).setCellValue("First Name");
		header.createCell(4).setCellValue("Last Name");
		header.createCell(5).setCellValue("Middle Name");
		header.createCell(6).setCellValue("Father Name");
		header.createCell(7).setCellValue("Mother Name");
		header.createCell(8).setCellValue("Husband Name");
		header.createCell(9).setCellValue("Gender");
		header.createCell(10).setCellValue("Program");
		header.createCell(11).setCellValue("old Program");
		header.createCell(12).setCellValue("Program Structure");
		header.createCell(13).setCellValue("Enrollment Month");
		header.createCell(14).setCellValue("Enrollment Year");
		header.createCell(15).setCellValue("Center Code");
		header.createCell(16).setCellValue("Center Name");
		header.createCell(17).setCellValue("Validity End Month");
		header.createCell(18).setCellValue("Validity End Year");
		header.createCell(19).setCellValue("Email ID");
		header.createCell(20).setCellValue("Mobile");
		header.createCell(21).setCellValue("Alt Phone");
		header.createCell(22).setCellValue("DOB");
		header.createCell(23).setCellValue("Registration Date");
		header.createCell(24).setCellValue("Is Lateral");
		header.createCell(25).setCellValue("Address");
		header.createCell(26).setCellValue("City");
		header.createCell(27).setCellValue("State");
		header.createCell(28).setCellValue("Country");
		header.createCell(29).setCellValue("Pin");
		
		header.createCell(30).setCellValue("IC");
		header.createCell(31).setCellValue("LC");
		header.createCell(32).setCellValue("Exam Mode");
		header.createCell(33).setCellValue("Program Cleared");
		header.createCell(34).setCellValue("Is App Downloaded");
		header.createCell(35).setCellValue("Academic Bank of Credits Id");
		
 
		int rowNum = 1;
		for (int i = 0 ; i < studentList.size(); i++) {
			//create the row data
			Row row = sheet.createRow(rowNum++);
			StudentExamBean bean = studentList.get(i);
			
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
			String isAppDownloaded = "";
			
			if(StringUtils.isBlank(bean.getFirebaseToken()) && StringUtils.isBlank(bean.getOnesignalId())){
				isAppDownloaded = "No";
			}else {
				isAppDownloaded = "Yes";
			}
			
			
			row.createCell(0).setCellValue(rowNum-1);
			row.createCell(1).setCellValue(bean.getSapid());
			row.createCell(2).setCellValue(bean.getSem());
			row.createCell(3).setCellValue(bean.getFirstName());
			row.createCell(4).setCellValue(bean.getLastName());
			row.createCell(5).setCellValue(bean.getMiddleName());
			row.createCell(6).setCellValue(bean.getFatherName());
			row.createCell(7).setCellValue(bean.getMotherName());
			row.createCell(8).setCellValue(bean.getHusbandName());
			row.createCell(9).setCellValue(bean.getGender());
			row.createCell(10).setCellValue(bean.getProgram());
			row.createCell(11).setCellValue(bean.getOldProgram());
			row.createCell(12).setCellValue(bean.getPrgmStructApplicable());
			row.createCell(13).setCellValue(bean.getEnrollmentMonth());
			row.createCell(14).setCellValue(bean.getEnrollmentYear());
			row.createCell(15).setCellValue(bean.getCenterCode());
			row.createCell(16).setCellValue(bean.getCenterName());
			row.createCell(17).setCellValue(bean.getValidityEndMonth());
			row.createCell(18).setCellValue(bean.getValidityEndYear());
			row.createCell(19).setCellValue(bean.getEmailId());
			row.createCell(20).setCellValue(bean.getMobile());
			row.createCell(21).setCellValue(bean.getAltPhone());
			row.createCell(22).setCellValue(bean.getDob());
			row.createCell(23).setCellValue(bean.getRegDate());
			row.createCell(24).setCellValue(bean.getIsLateral());
			row.createCell(25).setCellValue(bean.getAddress());
			row.createCell(26).setCellValue(bean.getCity());
			row.createCell(27).setCellValue(bean.getState());
			row.createCell(28).setCellValue(bean.getCountry());
			row.createCell(29).setCellValue(bean.getPin());
			
			row.createCell(30).setCellValue(ic);
			row.createCell(31).setCellValue(lc);
			row.createCell(32).setCellValue(examMode);
			row.createCell(33).setCellValue(bean.getProgramCleared());
			row.createCell(34).setCellValue(isAppDownloaded);
			row.createCell(35).setCellValue(bean.getAbcId());
			
        }
	}

}
