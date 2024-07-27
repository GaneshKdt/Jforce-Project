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
import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.daos.StudentMarksDAO;

public class ExecutiveExamBookingReportExcelView extends AbstractXlsxStreamingView  implements ApplicationContextAware{
	
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
		
		List<ExamBookingTransactionBean> examBookingList = (List<ExamBookingTransactionBean>) model.get("examBookingsForReport");
		//create a wordsheet
		Sheet sheet = workbook.createSheet("Exam Bookings");
		int index = 0;
		Row header = sheet.createRow(0);
		/*
		 *					 eeb.year, eeb.month, "
				+ "			s.sapid, s.firstName, s.lastName, s.program, eeb.sem, "
				+ "			eeb.subject, eeb.examDate, eeb.examTime, "
				+ "			ec.examCenterName as centerName "
		 * */
		/*
		1.	Email id
		2.	Mobile no.
		3.	Alternate mobile no.
		4.	LC
		5.	IC
		6.	Exam city 
		7.	Exam centre id
		8.	Exam End Time
		9.	Booking initiated time
		10.	Booking complete time
		
				 * */
		
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("Year");
	    header.createCell(index++).setCellValue("Month");
		header.createCell(index++).setCellValue("SAP ID");
		header.createCell(index++).setCellValue("First Name");
		header.createCell(index++).setCellValue("Last Name"); 
	//	header.createCell(index++).setCellValue("Email Id");
	//	header.createCell(index++).setCellValue("Mobile no.");
		header.createCell(index++).setCellValue("Alt Mobile no."); 
		header.createCell(index++).setCellValue("Batch Year "); 
		header.createCell(index++).setCellValue("Batch Month "); 
		
		header.createCell(index++).setCellValue("Program");
		header.createCell(index++).setCellValue("Sem");
	    header.createCell(index++).setCellValue("Subject");
	    if(!ICLCRESTRICTED_USER_LIST.contains(userId)) {
		header.createCell(index++).setCellValue("LC");
		header.createCell(index++).setCellValue("IC");
		header.createCell(index++).setCellValue("Center Id");
	    header.createCell(index++).setCellValue("Exam Center Name"); 
	    header.createCell(index++).setCellValue("City"); 
	    }
	    header.createCell(index++).setCellValue("Exam Date");
	    header.createCell(index++).setCellValue("Exam Start Time"); 
	    header.createCell(index++).setCellValue("Exam End Time"); 
	    
	    header.createCell(index++).setCellValue("Booked"); 
	    header.createCell(index++).setCellValue("Booking Initiated Time"); 
	    header.createCell(index++).setCellValue("Booking Complete Time"); 

	    
 
		int rowNum = 1;
		for (int i = 0 ; i < examBookingList.size(); i++) {
			index = 0;
			//create the row data
			Row row = sheet.createRow(rowNum++);
			ExamBookingTransactionBean bean = examBookingList.get(i);
			
			StudentExamBean student = sapIdStudentsMap.get(bean.getSapid());
			
			String ic = "";
			String lc = "";
			if(student != null){
				ic = student.getCenterName();
				CenterExamBean center = icLcMap.get(student.getCenterCode()); 
				if(center != null){
					lc = center.getLc();
				}
				
			}
			
			row.createCell(index++).setCellValue((i+1));
			row.createCell(index++).setCellValue(new Double(bean.getYear()));
			row.createCell(index++).setCellValue(bean.getMonth());
			row.createCell(index++).setCellValue(new Double(bean.getSapid()));
			
			
			row.createCell(index++).setCellValue(bean.getFirstName());
			row.createCell(index++).setCellValue(bean.getLastName());
		//	row.createCell(index++).setCellValue(bean.getEmailId());
		//	row.createCell(index++).setCellValue(bean.getMobile());
			row.createCell(index++).setCellValue(bean.getAltPhone());
			row.createCell(index++).setCellValue(bean.getEnrollmentYear());
			row.createCell(index++).setCellValue(bean.getEnrollmentMonth());
			
			
			row.createCell(index++).setCellValue(bean.getProgram());
			row.createCell(index++).setCellValue(new Double(bean.getSem()));
			row.createCell(index++).setCellValue(bean.getSubject());
			if(!ICLCRESTRICTED_USER_LIST.contains(userId)) {
			row.createCell(index++).setCellValue(lc);
			row.createCell(index++).setCellValue(ic);
			row.createCell(index++).setCellValue(bean.getCenterId());
			row.createCell(index++).setCellValue(bean.getCenterName());
			row.createCell(index++).setCellValue(bean.getCity());
			}
			row.createCell(index++).setCellValue(bean.getExamDate());
			row.createCell(index++).setCellValue(bean.getExamTime()); 
			row.createCell(index++).setCellValue(bean.getExamEndTime()); 
			
			row.createCell(index++).setCellValue(bean.getBooked());
			row.createCell(index++).setCellValue(bean.getCreatedDate());
			row.createCell(index++).setCellValue(bean.getCreatedDate());
			
			
        }
		
	
	}

}
