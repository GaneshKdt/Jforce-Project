package com.nmims.views;

import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;

import com.nmims.beans.UGConsentExcelReportBean;

@Component("ugConsentReportExcelView")
public class UGConsentReportExcelView extends AbstractXlsxStreamingView implements ApplicationContextAware {
	
	@Override
	protected void buildExcelDocument(Map model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
	
	ArrayList<UGConsentExcelReportBean> ugStudentList = (ArrayList<UGConsentExcelReportBean>) model.get("studentList");
	String type = (String) request.getSession().getAttribute("type");
		Sheet sheet = workbook.createSheet("UGStudentOption"+type);
		
		Row header = sheet.createRow(0);
		header.createCell(0).setCellValue("SAP ID ");
		header.createCell(1).setCellValue("Student Name");
		header.createCell(2).setCellValue("Email ID");
		header.createCell(3).setCellValue("Mobile No");
		header.createCell(4).setCellValue("Date Of Birth");
		header.createCell(5).setCellValue("Program Name");
		header.createCell(6).setCellValue("LC Name");
		header.createCell(7).setCellValue("Information Center");
		header.createCell(8).setCellValue("Program Status");
		if(type.equals("Submitted")) {
			header.createCell(9).setCellValue("Option No");
			header.createCell(10).setCellValue("Option Selected Details");
			header.createCell(11).setCellValue("Date & Time of Form Submission");
		}
			
		
		
		int rowNum = 1;
		for (UGConsentExcelReportBean bean :ugStudentList) {
			Row row = sheet.createRow(rowNum++);

			row.createCell(0).setCellValue(bean.getSapid());
			row.createCell(1).setCellValue(bean.getStudentName());
			row.createCell(2).setCellValue(bean.getEmailId());
			row.createCell(3).setCellValue(bean.getMobile());
			row.createCell(4).setCellValue(bean.getDob());
			row.createCell(5).setCellValue(bean.getProgram());
			row.createCell(6).setCellValue(bean.getLcName());
			row.createCell(7).setCellValue(bean.getInformation_center());
			row.createCell(8).setCellValue(bean.getProgramStatus());
			
			if(type.equals("Submitted")) {
			row.createCell(9).setCellValue(bean.getConsent_optionid());
			row.createCell(10).setCellValue(bean.getOption());
			row.createCell(11).setCellValue(bean.getDateOfSubmission());
			}
				
			
			}
		
	}
}