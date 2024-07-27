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
import com.nmims.beans.WebCopycaseBean;
import com.nmims.controllers.AssignmentController;

@Component("AssignmentWebCopyCaseReportExcelView")
public class AssignmentWebCopyCaseReportExcelView extends AbstractExcelView implements ApplicationContextAware{
	
	@Autowired
	AssignmentController assignController;
	
	private static final Logger assignCCLogger = LoggerFactory.getLogger("assignmentWebCopyCase");

	@Override
	protected void buildExcelDocument(Map model, HSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception{
		Logger CCLogger = (Logger) model.get("CCLogger");
		try {
			ResultDomain searchBean = (ResultDomain) model.get("searchBean");
			
			// Unique Threshold Sheet 1
			List<WebCopycaseBean> unique1CCList =  (List<WebCopycaseBean>) model.get("unique1CCList");
			if(unique1CCList != null && unique1CCList.size() > 0) {
				HSSFSheet sheet = workbook.createSheet("Unique Threshold 1 ");
				int index=0;
				HSSFRow header = sheet.createRow(0);
				header.createCell(index++).setCellValue("Sr. No.");
				header.createCell(index++).setCellValue("Subject");
				header.createCell(index++).setCellValue("Student ID");
				header.createCell(index++).setCellValue("Name");
				
				int rownum = 1;
				for(int i=0; i<unique1CCList.size(); i ++) {
					index=0;
					HSSFRow row = sheet.createRow(rownum++);
					WebCopycaseBean CCBean = unique1CCList.get(i);
					row.createCell(index++).setCellValue((i+1));
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
				HSSFSheet sheet = workbook.createSheet("Unique Threshold 1 ");
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
