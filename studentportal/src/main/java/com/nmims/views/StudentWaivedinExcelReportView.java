package com.nmims.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractExcelView;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;

import com.nmims.beans.CenterStudentPortalBean;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.beans.programStudentPortalBean;
import com.nmims.daos.PortalDao;

@Component("studentWaivedinExcelReportView")
public class StudentWaivedinExcelReportView extends AbstractExcelView  implements ApplicationContextAware{

	
	private static final Logger logger = LoggerFactory.getLogger(StudentWaivedinExcelReportView.class);

	@Override
	protected void buildExcelDocument(Map<String, Object> model, HSSFWorkbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		try {
			logger.info("StudentWaivedinExcelReportView Called");
			
			ArrayList<StudentStudentPortalBean> bean=(ArrayList<StudentStudentPortalBean>)model.get("StudentPortalBeanList");
			
			//create a wordsheet
			HSSFSheet sheet = (HSSFSheet) workbook.createSheet("Extra subject List");
			int index = 0;
			HSSFRow header = sheet.createRow(0);
			//headers
			header.createCell(index++).setCellValue("Sr. No.");
			header.createCell(index++).setCellValue("Full Name");
			header.createCell(index++).setCellValue("SAP Id");
			header.createCell(index++).setCellValue("Program Name");
			header.createCell(index++).setCellValue("Sem");
			header.createCell(index++).setCellValue("Subject");
			header.createCell(index++).setCellValue("Closed Won Date");
	 
			int rowNum = 1;
			for (int i = 0 ; i < bean.size(); i++) {
				index = 0;
				//create the row data
				HSSFRow row = sheet.createRow(rowNum++);
				//columns
				StudentStudentPortalBean data=bean.get(i);
				row.createCell(index++).setCellValue((i+1));
				row.createCell(index++).setCellValue(data.getFirstName()+" "+data.getLastName());
				row.createCell(index++).setCellValue(data.getSapid());
				row.createCell(index++).setCellValue(data.getProgram());
				row.createCell(index++).setCellValue(data.getSem());
				row.createCell(index++).setCellValue(data.getSubject());
				row.createCell(index++).setCellValue(data.getRegDate());
	        }
			}catch(Exception e) {
				e.printStackTrace();
			}
	}
}
