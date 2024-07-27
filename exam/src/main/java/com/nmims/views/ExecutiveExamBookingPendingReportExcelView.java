package com.nmims.views;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;*/
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
/*import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellRangeAddress;*/
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
/*import org.springframework.web.servlet.view.document.AbstractExcelView;*/
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;

import com.nmims.beans.AssignmentStatusBean;
import com.nmims.beans.CenterExamBean;
import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.beans.ExamCenterBean;
import com.nmims.beans.ExamCenterSlotMappingBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.daos.StudentMarksDAO;


public class ExecutiveExamBookingPendingReportExcelView extends AbstractXlsxStreamingView  implements ApplicationContextAware{
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
		
		ArrayList<AssignmentStatusBean> regIncompleteStudetsList  = (ArrayList<AssignmentStatusBean>) model.get("regIncompleteStudetsList");

		Sheet sheet = workbook.createSheet("Registrations Not Done");
		int index = 0;
	    Row header = sheet.createRow(0);
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("Exam Year");
	    header.createCell(index++).setCellValue("Exam Month");
		header.createCell(index++).setCellValue("SAP ID");
		header.createCell(index++).setCellValue("First Name");
		header.createCell(index++).setCellValue("Last Name");
		header.createCell(index++).setCellValue("Email");
		header.createCell(index++).setCellValue("Mobile");
		header.createCell(index++).setCellValue("Alternate Phone");
		header.createCell(index++).setCellValue("Program");
	    header.createCell(index++).setCellValue("Subject");
	    header.createCell(index++).setCellValue("Sem");
	    
	    if(!ICLCRESTRICTED_USER_LIST.contains(userId)) {
	    header.createCell(index++).setCellValue("IC");
		header.createCell(index++).setCellValue("LC");
	    }
	    header.createCell(index++).setCellValue("Enrollment Month");
	    header.createCell(index++).setCellValue("Enrollment Year");
	    
	    
		
	    
 
		int rowNum = 1;
		for (int i = 0 ; i < regIncompleteStudetsList.size(); i++) {
			index = 0;
			//create the row data
			Row row = sheet.createRow(rowNum++);
			AssignmentStatusBean bean = regIncompleteStudetsList.get(i);
			
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
			row.createCell(index++).setCellValue(bean.getExamYear());
			row.createCell(index++).setCellValue(bean.getExamMonth());
			row.createCell(index++).setCellValue(new Double(bean.getSapid()));
		
			row.createCell(index++).setCellValue(bean.getFirstName());
			row.createCell(index++).setCellValue(bean.getLastName());
			row.createCell(index++).setCellValue(bean.getEmailId());
			row.createCell(index++).setCellValue(bean.getMobile());
			row.createCell(index++).setCellValue(bean.getAltPhone());
			
			
			row.createCell(index++).setCellValue(bean.getProgram());
			row.createCell(index++).setCellValue(bean.getSubject());
			row.createCell(index++).setCellValue(bean.getSem());
			
			if(!ICLCRESTRICTED_USER_LIST.contains(userId)) {
			row.createCell(index++).setCellValue(ic);
			row.createCell(index++).setCellValue(lc);
			}
			row.createCell(index++).setCellValue(bean.getEnrollmentMonth());
			row.createCell(index++).setCellValue(new Double(bean.getEnrollmentYear()));
			
			
			
        }
		
		//Written to write excel file to outputStream and flush it out of controller itself//
		ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
		workbook.write(outByteStream);
		byte [] outArray = outByteStream.toByteArray();
		response.setContentType("application/ms-excel");
		response.setContentLength(outArray.length);
		response.setHeader("Expires:", "0"); // eliminates browser caching
		response.setHeader("Content-Disposition", "attachment; filename=ExamBookingPending.xls");
		ServletOutputStream outStream = response.getOutputStream();
		outStream.write(outArray);
		outStream.flush();
		//End//
		
/*		sheet.setAutoFilter(CellRangeAddress.valueOf("A1:AP999999999"));*/
	}

}
