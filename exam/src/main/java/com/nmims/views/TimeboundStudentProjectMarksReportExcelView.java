package com.nmims.views;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;

import com.nmims.beans.TEEResultBean;

/**
 * 
 * @author Siddheshwar_Khanse
 *
 */
@Component("timeboundStudentProjectMarksReportExcelView")
public class TimeboundStudentProjectMarksReportExcelView extends AbstractXlsxStreamingView  implements ApplicationContextAware{
	
	public static final Logger timeboundProjectMarksUploadLogger = LoggerFactory.getLogger("timeboundProjectMarksUpload");
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void buildExcelDocument(Map model, Workbook workbook,	HttpServletRequest request, HttpServletResponse response)
		throws Exception {
		timeboundProjectMarksUploadLogger.info("TimeboundStudentProjectMarksReportExcelView.buildExcelDocument() - START");
		//Read student project marks data from the map.
		List<TEEResultBean> capstone_project_marks = (List<TEEResultBean>) model.get("capstoneProjectMarksList");
		
		//create a work sheet
		Sheet sheet = workbook.createSheet("Capstone Project Marks");
		
		int index = 0;
		Row header = sheet.createRow(0);
		
		//Column names of a sheet
		header.createCell(index++).setCellValue("Sr. No.");
		
		header.createCell(index++).setCellValue("SAP Id");
	    header.createCell(index++).setCellValue("Student Name");
		header.createCell(index++).setCellValue("Subject");
		header.createCell(index++).setCellValue("Batch Id");
		
		header.createCell(index++).setCellValue("Simulation Status");
		header.createCell(index++).setCellValue("Simulation Score");
		header.createCell(index++).setCellValue("Simulation Max Score");
		header.createCell(index++).setCellValue("CompXM Status");
		header.createCell(index++).setCellValue("CompXM Score");
		header.createCell(index++).setCellValue("CompXM Max Score");
	    
 
		int rowNum = 1;
		for (int i = 0 ; i < capstone_project_marks.size(); i++) {
			index = 0;
			
			//create the row data
			Row row = sheet.createRow(rowNum++);
			TEEResultBean resultBean = capstone_project_marks.get(i);
			
			row.createCell(index++).setCellValue((i+1));
			
			row.createCell(index++).setCellValue(resultBean.getSapid());
			row.createCell(index++).setCellValue(resultBean.getStudent_name());
			row.createCell(index++).setCellValue(resultBean.getSubject());
			row.createCell(index++).setCellValue(resultBean.getBatchId());
			
			row.createCell(index++).setCellValue(resultBean.getSimulation_status());
			row.createCell(index++).setCellValue(resultBean.getSimulation_score());
			row.createCell(index++).setCellValue(resultBean.getSimulation_max_score());
			row.createCell(index++).setCellValue(resultBean.getCompXM_status());
			row.createCell(index++).setCellValue(resultBean.getCompXM_score());
			row.createCell(index++).setCellValue(resultBean.getCompXM_max_score());
        }//for loop	
		timeboundProjectMarksUploadLogger.info("TimeboundStudentProjectMarksReportExcelView.buildExcelDocument() - END");
	}//buildExcelDocument(-,-,-,-)
}//class
