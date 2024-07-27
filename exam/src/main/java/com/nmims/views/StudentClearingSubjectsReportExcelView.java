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

import com.nmims.beans.CenterExamBean;
import com.nmims.beans.PassFailExamBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.daos.StudentMarksDAO;

public class StudentClearingSubjectsReportExcelView  extends AbstractXlsxStreamingView  implements ApplicationContextAware{
	private static ApplicationContext act = null;
	
	@Override
	protected void buildExcelDocument(Map model, Workbook workbook,	HttpServletRequest request, HttpServletResponse response)
		throws Exception {
		String userId=(String) request.getSession().getAttribute("userId");
		List<String> ICLCRESTRICTED_USER_LIST=(List<String>) request.getSession().getAttribute("ICLCRESTRICTED_USER_LIST");
		act = getApplicationContext();
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		HashMap<String, StudentExamBean> sapIdStudentsMap = dao.getAllStudents();
		HashMap<String, CenterExamBean> icLcMap = dao.getICLCMap();
		
		int index = 0;
		List<StudentExamBean> studentList = (List<StudentExamBean>)model.get("studentList");
		//create a wordsheet
		Sheet sheet = workbook.createSheet("Students");
 
		Row header = sheet.createRow(0);
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("SAP ID");
		header.createCell(index++).setCellValue("Sem");
		header.createCell(index++).setCellValue("First Name");
		header.createCell(index++).setCellValue("Last Name");
/*		header.createCell(index++).setCellValue("Middle Name");
		header.createCell(index++).setCellValue("Father Name");
		header.createCell(index++).setCellValue("Mother Name");
		header.createCell(index++).setCellValue("Husband Name");
		header.createCell(index++).setCellValue("Gender");*/
		header.createCell(index++).setCellValue("Program");
		header.createCell(index++).setCellValue("# of Subjects Cleared");
		header.createCell(index++).setCellValue("Enrollment Month");
		header.createCell(index++).setCellValue("Enrollment Year");
		
		if(!ICLCRESTRICTED_USER_LIST.contains(userId)) {
		header.createCell(index++).setCellValue("IC Code");
		header.createCell(index++).setCellValue("IC Name");
		header.createCell(index++).setCellValue("Learning Center");
		}
		header.createCell(index++).setCellValue("Validity End Month");
		header.createCell(index++).setCellValue("Validity End Year");
	//	header.createCell(index++).setCellValue("Email ID");
	//	header.createCell(index++).setCellValue("Mobile");
		header.createCell(index++).setCellValue("Alt Phone");
/*		header.createCell(index++).setCellValue("DOB");
		header.createCell(index++).setCellValue("Registration Date");*/
		header.createCell(index++).setCellValue("Is Lateral");
		header.createCell(index++).setCellValue("Address");
		header.createCell(index++).setCellValue("City");
		header.createCell(index++).setCellValue("State");
		header.createCell(index++).setCellValue("Program Structure");
		/*header.createCell(index++).setCellValue("Country");
		header.createCell(index++).setCellValue("Pin");*/
		if(!ICLCRESTRICTED_USER_LIST.contains(userId)) {
		header.createCell(index++).setCellValue("IC");
		header.createCell(index++).setCellValue("LC");
		header.createCell(index++).setCellValue("Exam Mode");
		}
		
		
		
 
		int rowNum = 1;
		for (int i = 0 ; i < studentList.size(); i++) {
			index = 0;
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
			
			row.createCell(index++).setCellValue(rowNum-1);
			row.createCell(index++).setCellValue(new Double(bean.getSapid()));
			row.createCell(index++).setCellValue(new Double(bean.getSem()));
			row.createCell(index++).setCellValue(bean.getFirstName());
			row.createCell(index++).setCellValue(bean.getLastName());
/*			row.createCell(index++).setCellValue(bean.getMiddleName());
			row.createCell(index++).setCellValue(bean.getFatherName());
			row.createCell(index++).setCellValue(bean.getMotherName());
			row.createCell(index++).setCellValue(bean.getHusbandName());
			row.createCell(index++).setCellValue(bean.getGender());*/
			row.createCell(index++).setCellValue(bean.getProgram());
			row.createCell(index++).setCellValue(new Double(bean.getSubjectsCleared()));
			row.createCell(index++).setCellValue(bean.getEnrollmentMonth());
			row.createCell(index++).setCellValue(new Double(bean.getEnrollmentYear()));
			if(!ICLCRESTRICTED_USER_LIST.contains(userId)) {
			row.createCell(index++).setCellValue(bean.getCenterCode());
			row.createCell(index++).setCellValue(bean.getCenterName());
			row.createCell(index++).setCellValue(bean.getLc());
			}
			row.createCell(index++).setCellValue(bean.getValidityEndMonth());
			row.createCell(index++).setCellValue(new Double(bean.getValidityEndYear()));
		//	row.createCell(index++).setCellValue(bean.getEmailId());
		//	row.createCell(index++).setCellValue(bean.getMobile());
			row.createCell(index++).setCellValue(bean.getAltPhone());
/*			row.createCell(index++).setCellValue(bean.getDob());
			row.createCell(index++).setCellValue(bean.getRegDate());*/
			row.createCell(index++).setCellValue(bean.getIsLateral());
			row.createCell(index++).setCellValue(bean.getAddress());
			row.createCell(index++).setCellValue(bean.getCity());
			row.createCell(index++).setCellValue(bean.getState());
			row.createCell(index++).setCellValue(bean.getPrgmStructApplicable());
			/*row.createCell(index++).setCellValue(bean.getCountry());
			row.createCell(index++).setCellValue(bean.getPin());
			*/
			if(!ICLCRESTRICTED_USER_LIST.contains(userId)) {
			row.createCell(index++).setCellValue(ic);
			row.createCell(index++).setCellValue(lc);
			row.createCell(index++).setCellValue(examMode);
			}
        }
	/*	
		sheet.autoSizeColumn(0); sheet.autoSizeColumn(1);
		sheet.autoSizeColumn(2); sheet.autoSizeColumn(3);
		sheet.autoSizeColumn(4); sheet.autoSizeColumn(5);
		sheet.autoSizeColumn(6); sheet.autoSizeColumn(7);
		sheet.autoSizeColumn(8); sheet.autoSizeColumn(9);
		sheet.autoSizeColumn(10); sheet.autoSizeColumn(11);
		sheet.autoSizeColumn(12); sheet.autoSizeColumn(13);
		sheet.autoSizeColumn(14); sheet.autoSizeColumn(15);
		sheet.autoSizeColumn(16); sheet.autoSizeColumn(17);*/

		
	}

}
