package com.nmims.views;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.servlet.view.document.AbstractExcelView;

import com.nmims.beans.AssignmentFileBean;
import com.nmims.beans.ResultDomain;
import com.nmims.controllers.AssignmentController;

@Component("AssignmentCopyCaseReportExcelView")
public class AssignmentCopyCaseReportExcelView extends AbstractExcelView implements ApplicationContextAware{
	
	@Autowired
	AssignmentController assignController;
	
	private static final Logger assignCCLogger = LoggerFactory.getLogger("assignmentCopyCase");

	@Override
	protected void buildExcelDocument(Map model, HSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception{
		Logger CCLogger = (Logger) model.get("CCLogger");
		try {
			
			ResultDomain searchBean = (ResultDomain) model.get("searchBean");
			
			// Unique Threshold Sheet 1
			List<ResultDomain> unique1CCList =  (List<ResultDomain>) model.get("unique1CCList");
			if(unique1CCList != null && unique1CCList.size() > 0) {
				CCLogger.info(" CC Report Month-Year:{}{} Subject:{} Sheet:Unique Threshold 1 count:{} ",searchBean.getMonth(), searchBean.getYear(), searchBean.getSubject(), unique1CCList.size());
				HSSFSheet sheet = workbook.createSheet("Unique Threshold 1 ");
				int index=0;
				HSSFRow header = sheet.createRow(0);
				header.createCell(index++).setCellValue("Sr. No.");
//			header.createCell(index++).setCellValue("Month");
//			header.createCell(index++).setCellValue("Year");
				header.createCell(index++).setCellValue("Subject");
				header.createCell(index++).setCellValue("Student ID");
				header.createCell(index++).setCellValue("Name");
				
				int rownum = 1;
				for(int i=0; i<unique1CCList.size(); i ++) {
					index=0;
					HSSFRow row = sheet.createRow(rownum++);
					ResultDomain CCBean = unique1CCList.get(i);
					row.createCell(index++).setCellValue((i+1));
//					row.createCell(index++).setCellValue(CCBean.getMonth());
//					row.createCell(index++).setCellValue(CCBean.getYear());
					row.createCell(index++).setCellValue(CCBean.getSubject());
					row.createCell(index++).setCellValue(CCBean.getSapid());
					row.createCell(index++).setCellValue(CCBean.getName());
				}
				sheet.autoSizeColumn(0); sheet.autoSizeColumn(1);
				sheet.autoSizeColumn(2); sheet.autoSizeColumn(3);
				sheet.autoSizeColumn(4); sheet.autoSizeColumn(5);
				sheet.autoSizeColumn(6); sheet.autoSizeColumn(7);
				sheet.autoSizeColumn(8); sheet.autoSizeColumn(9);
				sheet.autoSizeColumn(10);
				sheet.setAutoFilter(CellRangeAddress.valueOf("A1:R"+unique1CCList.size()));
			}else {
				CCLogger.info("No records found CC Report Month-Year:{}{} Subject:{} Sheet:Unique Threshold 1 ",searchBean.getMonth(), searchBean.getYear(), searchBean.getSubject());
				HSSFSheet sheet = workbook.createSheet("Unique Threshold 1 ");
				int index=0;
				HSSFRow header = sheet.createRow(0);
				header.createCell(index++).setCellValue("No Records Found");
				sheet.autoSizeColumn(0);
			}
			
			// Students above 90% Sheet
			List<ResultDomain> above90CCList =  (List<ResultDomain>) model.get("above90CCList");
			if(above90CCList != null && above90CCList.size() > 0) {
				CCLogger.info(" CC Report Month-Year:{}{} Subject:{} Sheet:Students above 90% count:{} ",searchBean.getMonth(), searchBean.getYear(), searchBean.getSubject(), above90CCList.size());
				HSSFSheet sheet = workbook.createSheet("Students above 90% ");
				int index=0;
				HSSFRow header = sheet.createRow(0);
				header.createCell(index++).setCellValue("Sr. No.");
//			header.createCell(index++).setCellValue("Month");
//			header.createCell(index++).setCellValue("Year");
				header.createCell(index++).setCellValue("Subject");
				header.createCell(index++).setCellValue("Student ID");
				header.createCell(index++).setCellValue("Name");
				
				int rownum = 1;
				for(int i=0; i<above90CCList.size(); i ++) {
					index=0;
					HSSFRow row = sheet.createRow(rownum++);
					ResultDomain CCBean = above90CCList.get(i);
					row.createCell(index++).setCellValue((i+1));
//					row.createCell(index++).setCellValue(CCBean.getMonth());
//					row.createCell(index++).setCellValue(CCBean.getYear());
					row.createCell(index++).setCellValue(CCBean.getSubject());
					row.createCell(index++).setCellValue(CCBean.getSapid());
					row.createCell(index++).setCellValue(CCBean.getName());
				}
				sheet.autoSizeColumn(0); sheet.autoSizeColumn(1);
				sheet.autoSizeColumn(2); sheet.autoSizeColumn(3);
				sheet.autoSizeColumn(4); sheet.autoSizeColumn(5);
				sheet.autoSizeColumn(6); sheet.autoSizeColumn(7);
				sheet.autoSizeColumn(8); sheet.autoSizeColumn(9);
				sheet.autoSizeColumn(10);
				sheet.setAutoFilter(CellRangeAddress.valueOf("A1:R"+above90CCList.size()));
			}else {
				CCLogger.info("No records found CC Report Month-Year:{}{} Subject:{} Sheet:Students above 90% ",searchBean.getMonth(), searchBean.getYear(), searchBean.getSubject());
				HSSFSheet sheet = workbook.createSheet("Students above 90%");
				int index=0;
				HSSFRow header = sheet.createRow(0);
				header.createCell(index++).setCellValue("No Records Found");
				sheet.autoSizeColumn(0);
			}
			
			// Unique Threshold Sheet 2
			List<ResultDomain> unique2CCList =  (List<ResultDomain>) model.get("unique2CCList");
			if(unique2CCList != null && unique2CCList.size() > 0) {
				CCLogger.info(" CC Report Month-Year:{}{} Subject:{} Sheet:Unique Threshold 2 count:{} ",searchBean.getMonth(), searchBean.getYear(), searchBean.getSubject(), unique2CCList.size());
				HSSFSheet sheet = workbook.createSheet("Unique Threshold 2 ");
				int index=0;
				HSSFRow header = sheet.createRow(0);
				header.createCell(index++).setCellValue("Sr. No.");
//			header.createCell(index++).setCellValue("Month");
//			header.createCell(index++).setCellValue("Year");
				header.createCell(index++).setCellValue("Subject");
				header.createCell(index++).setCellValue("Student ID");
				header.createCell(index++).setCellValue("Name");
				
				int rownum = 1;
				for(int i=0; i<unique2CCList.size(); i ++) {
					index=0;
					HSSFRow row = sheet.createRow(rownum++);
					ResultDomain CCBean = unique2CCList.get(i);
					row.createCell(index++).setCellValue((i+1));
//					row.createCell(index++).setCellValue(CCBean.getMonth());
//					row.createCell(index++).setCellValue(CCBean.getYear());
					row.createCell(index++).setCellValue(CCBean.getSubject());
					row.createCell(index++).setCellValue(CCBean.getSapid());
					row.createCell(index++).setCellValue(CCBean.getName());
				}
				sheet.autoSizeColumn(0); sheet.autoSizeColumn(1);
				sheet.autoSizeColumn(2); sheet.autoSizeColumn(3);
				sheet.autoSizeColumn(4); sheet.autoSizeColumn(5);
				sheet.autoSizeColumn(6); sheet.autoSizeColumn(7);
				sheet.autoSizeColumn(8); sheet.autoSizeColumn(9);
				sheet.autoSizeColumn(10);
				sheet.setAutoFilter(CellRangeAddress.valueOf("A1:R"+unique2CCList.size()));
			}else {
				CCLogger.info("No records found CC Report Month-Year:{}{} Subject:{} Sheet:Unique Threshold 2 ",searchBean.getMonth(), searchBean.getYear(), searchBean.getSubject());
				HSSFSheet sheet = workbook.createSheet("Unique Threshold 2 ");
				int index=0;
				HSSFRow header = sheet.createRow(0);
				header.createCell(index++).setCellValue("No Records Found");
				sheet.autoSizeColumn(0);
			}
			
		} catch (Exception e) {
			assignController.setError(request, "Error while generating CC report");
			CCLogger.error("Exception Error while generating CC report Error:{}",e);
			Model m = null;
			assignController.getCopyCaseReportForm(request, response, m);
			//e.printStackTrace();
		}
	}
}
