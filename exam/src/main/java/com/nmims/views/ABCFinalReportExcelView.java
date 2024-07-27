package com.nmims.views;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;

import com.nmims.beans.FinalCertificateABCreportBean;
@Component
public class ABCFinalReportExcelView extends AbstractXlsxStreamingView implements ApplicationContextAware{
	
	

	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		

		
		List<FinalCertificateABCreportBean> ABCreport =(List<FinalCertificateABCreportBean>) request.getSession().getAttribute("ABCreport");
		SXSSFSheet ABCfinalReportSheet = (SXSSFSheet)workbook.createSheet("ABCFinalCertificateReport");
		int index = 0 ;
		SXSSFRow header =(SXSSFRow) ABCfinalReportSheet.createRow(0);
		header.createCell(index++).setCellValue("Sr No");
		header.createCell(index++).setCellValue("Program");
		header.createCell(index++).setCellValue("Enrollment Year");
		header.createCell(index++).setCellValue("Enrollment Month");
		header.createCell(index++).setCellValue("Sapid");
		header.createCell(index++).setCellValue("Student Name");
		header.createCell(index++).setCellValue("DOB");
		header.createCell(index++).setCellValue("Mobile");
		header.createCell(index++).setCellValue("Email");
		header.createCell(index++).setCellValue("Father Name");
		header.createCell(index++).setCellValue("Mother Name");
		header.createCell(index++).setCellValue("Result");
		header.createCell(index++).setCellValue("Year");
		header.createCell(index++).setCellValue("Month");
		header.createCell(index++).setCellValue("DOI");
		header.createCell(index++).setCellValue("Gender");
		header.createCell(index++).setCellValue("Certificate Number");
		int rowNumber = 1;
		for(FinalCertificateABCreportBean reportBean : ABCreport) {
			index = 0;
			SXSSFRow row = (SXSSFRow)ABCfinalReportSheet.createRow(rowNumber++);
			row.createCell(index++).setCellValue(rowNumber-1);
			row.createCell(index++).setCellValue(reportBean.getProgramName());
			row.createCell(index++).setCellValue(reportBean.getEnrollmentYear());
			row.createCell(index++).setCellValue(reportBean.getEnrollmentMonth());
			row.createCell(index++).setCellValue(reportBean.getSapid());
			row.createCell(index++).setCellValue(reportBean.getStudentName());
			row.createCell(index++).setCellValue(reportBean.getDateOfBirth());
			row.createCell(index++).setCellValue(reportBean.getMobile());
			row.createCell(index++).setCellValue(reportBean.getEmail());
			row.createCell(index++).setCellValue(reportBean.getFatherName());
			row.createCell(index++).setCellValue(reportBean.getMotherName());
			row.createCell(index++).setCellValue(reportBean.getResult());
			row.createCell(index++).setCellValue(reportBean.getPassingYear());
			row.createCell(index++).setCellValue(reportBean.getPassingMonth());
			row.createCell(index++).setCellValue(reportBean.getDeclareDate());
			row.createCell(index++).setCellValue(reportBean.getGender());
			row.createCell(index++).setCellValue(reportBean.getCertificateNumber());
		
		}
		
	}
}
