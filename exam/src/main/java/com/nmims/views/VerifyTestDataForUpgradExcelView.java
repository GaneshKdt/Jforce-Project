package com.nmims.views;

import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.view.document.AbstractExcelView;

import com.nmims.beans.UpgradAssessmentExamBean;

public class VerifyTestDataForUpgradExcelView extends AbstractExcelView implements ApplicationContextAware{

	@Override
	protected void buildExcelDocument(Map model, HSSFWorkbook workbook,	HttpServletRequest request, HttpServletResponse response) throws Exception {
		ArrayList<UpgradAssessmentExamBean> queryList = (ArrayList<UpgradAssessmentExamBean>) model.get("UpgradAssessmentList");
		String fileName = "VerifyTestDataForUPGRAD.xls"; //Your file name here.

		response.setContentType("application/vnd.ms-excel"); //Tell the browser to expect an excel file
		response.setHeader("Content-Disposition", "attachment; filename="+fileName); //Tell the browser it should be named as the custom file name

		HSSFSheet sheet = workbook.createSheet("Verify Test Data For Upgrad");
		int index = 0;
		HSSFRow header = sheet.createRow(0);
		header.createCell(index++).setCellValue("Sr. No."); 
		header.createCell(index++).setCellValue("SapId");
		header.createCell(index++).setCellValue("Name");
		header.createCell(index++).setCellValue("Email");
		header.createCell(index++).setCellValue("Contact No.");
		header.createCell(index++).setCellValue("Subject");
		header.createCell(index++).setCellValue("Batch");
		header.createCell(index++).setCellValue("Test");
		header.createCell(index++).setCellValue("Attempt");
		header.createCell(index++).setCellValue("Score");
		header.createCell(index++).setCellValue("Total Question");
		header.createCell(index++).setCellValue("Max Peer Penalty");
		header.createCell(index++).setCellValue("Max Online Penalty");
		header.createCell(index++).setCellValue("Test Started On ");
		header.createCell(index++).setCellValue("Test Ended On");
		header.createCell(index++).setCellValue("Remaining Time");
		header.createCell(index++).setCellValue("Show Result");
		int rowNum = 1;
		for (int i = 0 ; i < queryList.size(); i++) {
			index = 0;
			//create the row data
			HSSFRow row = sheet.createRow(rowNum++);
			UpgradAssessmentExamBean bean = queryList.get(i);
			row.createCell(index++).setCellValue((i+1));
			row.createCell(index++).setCellValue(bean.getSapid()); 
			row.createCell(index++).setCellValue(bean.getName()); 
			row.createCell(index++).setCellValue(bean.getEmailId()); 
			row.createCell(index++).setCellValue(bean.getMobile()); 
			row.createCell(index++).setCellValue(bean.getSubject());
			row.createCell(index++).setCellValue(bean.getBatchName()); 
			row.createCell(index++).setCellValue(bean.getTestName()); 
			if("CopyCase".equals(bean.getAttemptStatus())) {
			row.createCell(index++).setCellValue("Copy Case");
			}else if(bean.getAttempt() == 1) {
			row.createCell(index++).setCellValue("Y");
			}else if(bean.getAttempt() == 0) {
			row.createCell(index++).setCellValue("N");	
			}else {
			row.createCell(index++).setCellValue("");	
			}
			row.createCell(index++).setCellValue(bean.getScore()+" / "+bean.getMaxScore()); 
			row.createCell(index++).setCellValue(bean.getNoOfQuestionsAttempted()+" / "+bean.getQuestionNoCount() ); 
			row.createCell(index++).setCellValue(bean.getPeerPenalty() ); 
			row.createCell(index++).setCellValue(bean.getOnlinePenalty()); 
			row.createCell(index++).setCellValue(bean.getTestStartedOn()); 
			row.createCell(index++).setCellValue(bean.getTestEndedOn()); 
			row.createCell(index++).setCellValue(bean.getRemainingTime()); 
			row.createCell(index++).setCellValue(bean.getShowResult()); 
		}
		sheet.autoSizeColumn(0);
		sheet.autoSizeColumn(1);
		sheet.autoSizeColumn(2);
		sheet.autoSizeColumn(3);
		sheet.autoSizeColumn(4);
		sheet.autoSizeColumn(5);
		sheet.autoSizeColumn(8);
		sheet.autoSizeColumn(9);
		sheet.autoSizeColumn(10);
		sheet.autoSizeColumn(11);
		sheet.autoSizeColumn(12);
		sheet.autoSizeColumn(13);
		sheet.setAutoFilter(CellRangeAddress.valueOf("A1:N"+(queryList.size()+1)));
	}
		
}
