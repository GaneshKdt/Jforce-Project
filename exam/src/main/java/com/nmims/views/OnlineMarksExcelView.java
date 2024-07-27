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
import com.nmims.beans.OnlineExamMarksBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.daos.StudentMarksDAO;


public class OnlineMarksExcelView  extends AbstractXlsxStreamingView  implements ApplicationContextAware{
	private static ApplicationContext act = null;
	
	@Override
	protected void buildExcelDocument(Map model, Workbook workbook,	HttpServletRequest request, HttpServletResponse response)
		throws Exception {
 
		act = getApplicationContext();
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		HashMap<String, StudentExamBean> sapIdStudentsMap = dao.getAllStudents();
		HashMap<String, CenterExamBean> icLcMap = dao.getICLCMap();
		
		List<OnlineExamMarksBean> studentMarksList = (List<OnlineExamMarksBean>) model.get("studentMarksList");
		//create a wordsheet
		Sheet sheet = workbook.createSheet("Exam Bookings");
		int index = 0;
		Row header = sheet.createRow(0);
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("Year");
	    header.createCell(index++).setCellValue("Month");
		header.createCell(index++).setCellValue("SAP ID");
		header.createCell(index++).setCellValue("Student Name");
		header.createCell(index++).setCellValue("Subject");
		header.createCell(index++).setCellValue("Section 1 Marks");
		header.createCell(index++).setCellValue("Section 2 Marks");
		header.createCell(index++).setCellValue("Section 3 Marks");
		header.createCell(index++).setCellValue("Section 4 Marks");
		
		header.createCell(index++).setCellValue("Total");
		header.createCell(index++).setCellValue("Rounded Total");
		
		header.createCell(index++).setCellValue("IC");
		header.createCell(index++).setCellValue("LC");
		header.createCell(index++).setCellValue("Exam Mode");
		
	
 
		int rowNum = 1;
		for (int i = 0 ; i < studentMarksList.size(); i++) {
			index = 0;
			//create the row data
			Row row = sheet.createRow(rowNum++);
			OnlineExamMarksBean bean = studentMarksList.get(i);
			
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
			row.createCell(index++).setCellValue(new Double(bean.getYear()));
			row.createCell(index++).setCellValue(bean.getMonth());
			row.createCell(index++).setCellValue(new Double(bean.getSapid()));
			
			
			row.createCell(index++).setCellValue(bean.getName());
			row.createCell(index++).setCellValue(bean.getSubject());
			row.createCell(index++).setCellValue(bean.getPart1marks());
			row.createCell(index++).setCellValue(bean.getPart2marks());
			row.createCell(index++).setCellValue(bean.getPart3marks());
			row.createCell(index++).setCellValue(bean.getPart4marks());
			
			
			row.createCell(index++).setCellValue(bean.getTotal());
			row.createCell(index++).setCellValue(bean.getRoundedTotal());
			
			row.createCell(index++).setCellValue(ic);
			row.createCell(index++).setCellValue(lc);
			row.createCell(index++).setCellValue(examMode);
			
			
        }
		sheet.autoSizeColumn(0); sheet.autoSizeColumn(1);
		sheet.autoSizeColumn(2); sheet.autoSizeColumn(3);
		sheet.autoSizeColumn(4); sheet.autoSizeColumn(5);
		sheet.autoSizeColumn(6); sheet.autoSizeColumn(7);
		sheet.autoSizeColumn(8); sheet.autoSizeColumn(9);
		sheet.autoSizeColumn(10); sheet.autoSizeColumn(11);
		sheet.autoSizeColumn(12);sheet.autoSizeColumn(13);
		sheet.autoSizeColumn(14);sheet.autoSizeColumn(15);
		sheet.autoSizeColumn(16);sheet.autoSizeColumn(17);
		
	}

}
