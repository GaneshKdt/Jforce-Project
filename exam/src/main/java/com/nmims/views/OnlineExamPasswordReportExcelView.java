package com.nmims.views;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.view.document.AbstractExcelView;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;

import com.nmims.beans.CenterExamBean;
import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.beans.ExamCenterBean;
import com.nmims.beans.ExamCenterSlotMappingBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.daos.StudentMarksDAO;


public class OnlineExamPasswordReportExcelView  extends AbstractXlsxStreamingView  implements ApplicationContextAware{
	private static ApplicationContext act = null;
	
	@Override
	protected void buildExcelDocument(Map model, Workbook workbook,	HttpServletRequest request, HttpServletResponse response)
		throws Exception {
		
		act = getApplicationContext();
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		HashMap<String, StudentExamBean> sapIdStudentsMap = dao.getAllStudents();
		HashMap<String, CenterExamBean> icLcMap = dao.getICLCMap();
		
		List<ExamBookingTransactionBean> examBookingList = (List<ExamBookingTransactionBean>) model.get("examBookingList");
		//create a wordsheet
		Sheet sheet = workbook.createSheet("Exam Bookings");
		int index = 0;
		Row header = sheet.createRow(0);
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("SAP ID");
		header.createCell(index++).setCellValue("First Name");
		header.createCell(index++).setCellValue("Last Name");
		header.createCell(index++).setCellValue("Program");
		header.createCell(index++).setCellValue("Sem");
	    header.createCell(index++).setCellValue("Subject");
	    header.createCell(index++).setCellValue("Sify Subject Code");
	    header.createCell(index++).setCellValue("Password");
	    header.createCell(index++).setCellValue("Sify Subject Code");
	    //header.createCell(index++).setCellValue("Email");
		//header.createCell(index++).setCellValue("Mobile");
		//header.createCell(index++).setCellValue("Alternate Phone");
		header.createCell(index++).setCellValue("Program Structure");
		header.createCell(index++).setCellValue("Enrollment Month");
		header.createCell(index++).setCellValue("Enrollment Year");
	    header.createCell(index++).setCellValue("Exam City");
	    header.createCell(index++).setCellValue("Exam Center Name");
	    header.createCell(index++).setCellValue("Exam Date");
	    header.createCell(index++).setCellValue("Exam Start Time");
	    header.createCell(index++).setCellValue("Exam End Time");
	    
	    //header.createCell(index++).setCellValue("IC");
		//header.createCell(index++).setCellValue("LC");
		header.createCell(index++).setCellValue("Exam Mode");
 
		int rowNum = 1;
		for (int i = 0 ; i < examBookingList.size(); i++) {
			index = 0;

			Row row = sheet.createRow(rowNum++);
			ExamBookingTransactionBean bean = examBookingList.get(i);
			
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
			
			row.createCell(index++).setCellValue((i+1));
			row.createCell(index++).setCellValue(new Double(bean.getSapid()));
			row.createCell(index++).setCellValue(bean.getFirstName());
			row.createCell(index++).setCellValue(bean.getLastName());
			row.createCell(index++).setCellValue(bean.getProgram());
			row.createCell(index++).setCellValue(new Double(bean.getSem()));
			row.createCell(index++).setCellValue(bean.getSubject());
			row.createCell(index++).setCellValue(bean.getSifySubjectCode());
			row.createCell(index++).setCellValue(new Double(bean.getPassword()));
			row.createCell(index++).setCellValue(bean.getSifySubjectCode());
			//row.createCell(index++).setCellValue(bean.getEmailId());
			//row.createCell(index++).setCellValue(bean.getMobile());
			//row.createCell(index++).setCellValue(bean.getAltPhone());
			row.createCell(index++).setCellValue(bean.getPrgmStructApplicable());
			row.createCell(index++).setCellValue(bean.getEnrollmentMonth());
			row.createCell(index++).setCellValue(bean.getEnrollmentYear());
			row.createCell(index++).setCellValue(bean.getCity());
			row.createCell(index++).setCellValue(bean.getExamCenterName());
			row.createCell(index++).setCellValue(bean.getExamDate());
			row.createCell(index++).setCellValue(bean.getExamTime());
			row.createCell(index++).setCellValue(bean.getExamEndTime());
			
			//row.createCell(index++).setCellValue(ic);
			//row.createCell(index++).setCellValue(lc);
			row.createCell(index++).setCellValue(examMode);
		
        }
	
	}

}
