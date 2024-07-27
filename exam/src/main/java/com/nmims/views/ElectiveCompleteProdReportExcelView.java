package com.nmims.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;

import com.nmims.beans.MDMSubjectCodeMappingBean;
import com.nmims.beans.Specialisation;

@Component("electiveCompleteProdReportExcelView")
public class ElectiveCompleteProdReportExcelView extends AbstractXlsxStreamingView  implements ApplicationContextAware{

	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		int index = 0;

		List<Specialisation> electivePendingReport = new ArrayList<>();
		electivePendingReport = (List<Specialisation>)model.get("electiveCompleteProdReport");
		HashMap<String, String> specializationTypesMap = (HashMap<String, String>) request.getSession().getAttribute("getSpecializationTypesMap");
		HashMap<String, MDMSubjectCodeMappingBean> mdmMapping = (HashMap<String, MDMSubjectCodeMappingBean>) request.getSession().getAttribute("getMdmMappingMap");
		//create a wordsheet
		
		String[] headerNames = {"Sr. No.","Sapid","Name","Term","Email","Mobile No","Subject Name ","Subject Code ","Specialisation"};
		
		Sheet sheet = workbook.createSheet("Students");
		Row header = sheet.createRow(0);
		
		for(String headerName: headerNames) {
			header.createCell(index++).setCellValue(headerName);
		}
		
		
		int rowNum = 1;
		for (int i = 0; i < electivePendingReport.size(); i++) {
			index = 0;
			//create the row data
			Row row = sheet.createRow(rowNum++);
			Specialisation bean = electivePendingReport.get(i);
			
			row.createCell(index++).setCellValue(rowNum-1);
			row.createCell(index++).setCellValue(bean.getSapid());
			row.createCell(index++).setCellValue(bean.getName());
			row.createCell(index++).setCellValue(bean.getTerm());
			row.createCell(index++).setCellValue(bean.getEmailId());
			row.createCell(index++).setCellValue(bean.getMobile());
			row.createCell(index++).setCellValue(mdmMapping.get(bean.getProgram_sem_subject_id()).getSubjectName());
			row.createCell(index++).setCellValue(mdmMapping.get(bean.getProgram_sem_subject_id()).getSubjectCode());
			row.createCell(index++).setCellValue(specializationTypesMap.get(bean.getSpecializationType()));
			
			
			sheet.autoSizeColumn(0); sheet.autoSizeColumn(1);
			sheet.autoSizeColumn(2); sheet.autoSizeColumn(3);
			sheet.autoSizeColumn(4); sheet.autoSizeColumn(5);
			sheet.autoSizeColumn(6);
			
		}
		
	}

}
