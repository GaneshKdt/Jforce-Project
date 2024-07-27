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

public class DissertationExcelView extends AbstractXlsxStreamingView implements ApplicationContextAware {

	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		List<DissertationResultBean> resultBean = (List<DissertationResultBean>) model.get("dissertationResult");
		Sheet sheet = workbook.createSheet("DissertationResult");
		int index = 0;
		Row header =  sheet.createRow(0);
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("Sapid");
		header.createCell(index++).setCellValue("Component A Score");
		header.createCell(index++).setCellValue("Component A Attempt");
		header.createCell(index++).setCellValue("Component B Score");
		header.createCell(index++).setCellValue("Component B Attempt");
		header.createCell(index++).setCellValue("isPass");
		header.createCell(index++).setCellValue("FailReason");
		header.createCell(index++).setCellValue("Grade");
		header.createCell(index++).setCellValue("Points");
	
		int rowNum =1;
		for(int i=0;i<resultBean.size();i++) {
			index =0;
			Row row = sheet.createRow(rowNum++);
			DissertationResultBean result = resultBean.get(i);
			row.createCell(index++).setCellValue(i+1);
			
			if(null== result.getIsPass()) {
				result.setIsPass("NA");
			}
			if(null == result.getFailReason()) {
				result.setFailReason("NA");
			}
			if(null == result.getGrade()) {
				result.setGrade("NA");
			}
			
			row.createCell(index++).setCellValue(result.getSapid());
			row.createCell(index++).setCellValue(result.getComponent_a_score());
			row.createCell(index++).setCellValue(result.getComponent_a_status());
			row.createCell(index++).setCellValue(result.getComponent_b_score());
			row.createCell(index++).setCellValue(result.getComponent_b_status());
			row.createCell(index++).setCellValue(result.getIsPass());
			row.createCell(index++).setCellValue(result.getFailReason());
			row.createCell(index++).setCellValue(result.getGrade());
			row.createCell(index++).setCellValue(result.getGradePoints());
		}
	}

}
