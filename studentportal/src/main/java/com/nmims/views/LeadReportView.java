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

import com.nmims.beans.LeadStudentPortalBean;

@Component("leadReportView")
public class LeadReportView  extends AbstractXlsxStreamingView  implements ApplicationContextAware{

	@SuppressWarnings("rawtypes")
	@Override
	protected void buildExcelDocument(Map model, Workbook workbook,	HttpServletRequest request, HttpServletResponse response)
		throws Exception {
		
		@SuppressWarnings("unchecked")
		ArrayList<LeadStudentPortalBean> leadList = (ArrayList<LeadStudentPortalBean>) model.get("leadList");
		System.out.println("arrayList: "+leadList);
		
		//create a worksheet
		Sheet sheet = workbook.createSheet("Lost Focus Report");
		int index = 0;
		Row header = sheet.createRow(0);
		
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("Lead Id");
		header.createCell(index++).setCellValue("Registration Id");
		header.createCell(index++).setCellValue("Email Id");
		header.createCell(index++).setCellValue("First Name");
		header.createCell(index++).setCellValue("Last Name");
		header.createCell(index++).setCellValue("Mobile Number");
		header.createCell(index++).setCellValue("Program");
		header.createCell(index++).setCellValue("Created Date");
 
		int rowNum = 1;
		
		int i = 0;
		for (LeadStudentPortalBean bean : leadList) {
			index = 0;
			i++;
			Row row = sheet.createRow(rowNum++);
			
			row.createCell(index++).setCellValue((i));
			row.createCell(index++).setCellValue(bean.getLeadId());
			row.createCell(index++).setCellValue(bean.getRegistrationId());
			row.createCell(index++).setCellValue(bean.getEmailId());
			row.createCell(index++).setCellValue(bean.getFirstName());
			row.createCell(index++).setCellValue(bean.getLastName());
			row.createCell(index++).setCellValue(bean.getMobile());
			row.createCell(index++).setCellValue(bean.getProgram());
			row.createCell(index++).setCellValue(bean.getCreatedDate());
			
		}
	}
	
}
