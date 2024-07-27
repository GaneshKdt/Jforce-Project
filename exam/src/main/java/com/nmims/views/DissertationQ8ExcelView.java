package com.nmims.views;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;

import com.nmims.beans.DissertationResultBean;

public class DissertationQ8ExcelView extends AbstractXlsxStreamingView implements ApplicationContextAware{

	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		List<DissertationResultBean> dissertationQ8Result =  (List<DissertationResultBean>) model.get("dissertationQ8Result");
		Sheet sheet = workbook.createSheet("DissertatinQ8Result");
		int index = 0;
		Row header = sheet.createRow(0);
		header.createCell(index++).setCellValue("Sr No");
		header.createCell(index++).setCellValue("Sapid");
		header.createCell(index++).setCellValue("Component_c_Score");
		header.createCell(index++).setCellValue("Component_c_Status");
		header.createCell(index++).setCellValue("Grace");
		header.createCell(index++).setCellValue("Total");
		header.createCell(index++).setCellValue("isPass");
		header.createCell(index++).setCellValue("Fail Reason");
		header.createCell(index++).setCellValue("Grade");
		header.createCell(index++).setCellValue("Points");
		
		int rowNum = 1;
		for(int i = 0 ; i < dissertationQ8Result.size(); i++) {
			index = 0;
			Row row = sheet.createRow(rowNum++);
			DissertationResultBean result = dissertationQ8Result.get(i);
			row.createCell(index++).setCellValue(i+1);
			row.createCell(index++).setCellValue(result.getSapid());
			row.createCell(index++).setCellValue(result.getComponent_c_score());
			row.createCell(index++).setCellValue(result.getComponent_c_status());
		
			if(null== result.getIsPass()) {
				result.setIsPass("NA");
			}
			if(null == result.getFailReason()) {
				result.setFailReason("NA");
			}
			
			if(null == result.getGrade()) {
				result.setGrade("NA");
			}
			
			row.createCell(index++).setCellValue(result.getGraceMarks());
			row.createCell(index++).setCellValue(result.getTotal());
			row.createCell(index++).setCellValue(result.getIsPass());
			row.createCell(index++).setCellValue(result.getFailReason());
			row.createCell(index++).setCellValue(result.getGrade());
			row.createCell(index++).setCellValue(result.getGradePoints());
		}
		
	}

}
