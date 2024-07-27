package com.nmims.views;

import java.util.ArrayList;
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
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractExcelView;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;

import com.nmims.beans.AISHEUGCExcelReportBean;
import com.nmims.beans.AISHEUGCReportsBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.daos.AISHEUGCReportsDao;
import com.nmims.daos.StudentMarksDAO;

@Component("aisheugcReportsExcelView")
public class AISHEUGCReportsExcelView extends AbstractXlsxStreamingView implements ApplicationContextAware {
	
	@Override
	protected void buildExcelDocument(Map model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		ArrayList<AISHEUGCExcelReportBean> AllListOfProgram = (ArrayList<AISHEUGCExcelReportBean>) model.get("AllListOfProgram");
    String enrollmentYear = (String) request.getSession().getAttribute("report_enrollmentYear");
   String enrollmentMonth = (String) request.getSession().getAttribute("report_enrollmentMonth");
		
		Sheet sheet = workbook.createSheet("StudentsData");
		
		Row header = sheet.createRow(0);
		header.createCell(0).setCellValue("Name Of School ");
		header.createCell(1).setCellValue("Enrollment Year");
		header.createCell(2).setCellValue("Enrollment Month");
		header.createCell(3).setCellValue("program Name");
		header.createCell(4).setCellValue("Total No Of Student's Appeared In Final Year");
		header.createCell(5).setCellValue("Girls Appeared In Final Year ");
		header.createCell(6).setCellValue("Total no of student's passed");
		header.createCell(7).setCellValue("Total No Of Girl's  students passed");
		header.createCell(8).setCellValue("Total no of student's  Above 60 Percentage");
		header.createCell(9).setCellValue("Total No Of  Girl's Above 60 Percentage");
		
		int rowNum = 1;
		int NGASCE = 1;
		for (AISHEUGCExcelReportBean bean :AllListOfProgram) {
			
	
			Row row = sheet.createRow(rowNum++);

			row.createCell(0).setCellValue(new String("NGASCE"));
			row.createCell(1).setCellValue(enrollmentYear);
			row.createCell(2).setCellValue(enrollmentMonth);
			row.createCell(3).setCellValue(bean.getProgram());
			row.createCell(4).setCellValue(bean.getTotalNoOfStudentsAppearedInFinalYear());
			row.createCell(5).setCellValue(bean.getTotalNoOfGirlsStudentsAppearedInFinalYear());
			row.createCell(6).setCellValue(bean.getTotalNoOfStudentsPassed());
			row.createCell(7).setCellValue(bean.getTotalNoOfGirlsStudentsPasseded());
			row.createCell(8).setCellValue(bean.getTotalNoOfStudentsAbove60percentage());
			row.createCell(9).setCellValue(bean.getTotalNoOfGirlsStudentsAbove60Percentage());
				}
			}

		

	}

			  
			  
				  
				  
				  
				  
			  
			 
			
		
		
		
	



