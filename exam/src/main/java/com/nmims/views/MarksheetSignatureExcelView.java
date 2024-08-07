package com.nmims.views;

import java.util.ArrayList;
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
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.view.document.AbstractExcelView;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;

import com.nmims.beans.AssignmentFileBean;
import com.nmims.beans.CenterExamBean;
import com.nmims.beans.MarksheetBean;

public class MarksheetSignatureExcelView  extends AbstractXlsxStreamingView  implements ApplicationContextAware {
	

	
	@Override
	protected void buildExcelDocument(Map model, Workbook workbook,	HttpServletRequest request, HttpServletResponse response)
		throws Exception {
 

		ArrayList<Object> data = (ArrayList<Object>)model.get("data");
		List<MarksheetBean> marksheetList = (List<MarksheetBean>)data.get(0);
		HashMap<String, String> programCodeNameMap = (HashMap<String, String>)data.get(1);
		HashMap<String, CenterExamBean> centersMap = (HashMap<String, CenterExamBean>)data.get(2);
		
				
		//create a wordsheet
		Sheet sheet = workbook.createSheet("Signature Sheet");
		int index = 0;
		Row header = sheet.createRow(0);
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("Learning Center Name");
		header.createCell(index++).setCellValue("Program");
		header.createCell(index++).setCellValue("Student ID");
		
		header.createCell(index++).setCellValue("First Name");
		header.createCell(index++).setCellValue("Last Name");
		header.createCell(index++).setCellValue("Signature");
		
		int rowNum = 1;
		for (int i = 0 ; i < marksheetList.size(); i++) {
			index = 0;
			//create the row data
			Row row = sheet.createRow(rowNum++);
			MarksheetBean bean = marksheetList.get(i);
			CenterExamBean center = centersMap.get(bean.getCenterCode());
			String centerName = "Not Available";
			if(center != null){
				centerName = center.getLc();
			}
			row.createCell(index++).setCellValue(rowNum-1);
			row.createCell(index++).setCellValue(centerName);
			row.createCell(index++).setCellValue(bean.getProgram());
			row.createCell(index++).setCellValue(new Double(bean.getSapid()));
			row.createCell(index++).setCellValue(bean.getFirstName());
			row.createCell(index++).setCellValue(bean.getLastName());
			row.createCell(index++).setCellValue("");
			
        }
		sheet.autoSizeColumn(0); sheet.autoSizeColumn(1);
		sheet.autoSizeColumn(2); sheet.autoSizeColumn(3);
		sheet.autoSizeColumn(4); sheet.autoSizeColumn(5);
		sheet.autoSizeColumn(6); 
		
	
	}

}
