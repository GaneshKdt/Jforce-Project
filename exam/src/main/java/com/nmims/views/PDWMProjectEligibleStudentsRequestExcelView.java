package com.nmims.views;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractExcelView;

import com.nmims.beans.CenterExamBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.daos.StudentMarksDAO;




@Component("PDWMProjectEligibleStudentsRequestExcelView")
public class PDWMProjectEligibleStudentsRequestExcelView extends AbstractExcelView implements ApplicationContextAware{
	
	private static ApplicationContext act = null;
	
	@Override
	protected void buildExcelDocument(Map model, HSSFWorkbook workbook,	HttpServletRequest request, HttpServletResponse response)
		throws Exception {
		
		act = getApplicationContext();
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		HashMap<String, StudentExamBean> sapIdStudentsMap = dao.getAllStudents();
		HashMap<String, CenterExamBean> icLcMap = dao.getICLCMap();
		
		List<StudentExamBean> eligiblelist = (List<StudentExamBean>) model.get("eligiblelist");
		//create a wordsheet
		HSSFSheet sheet = workbook.createSheet("Eligible Students List");
		int index = 0;
		HSSFRow header = sheet.createRow(0);
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("SAP ID");
		header.createCell(index++).setCellValue("Name");
		header.createCell(index++).setCellValue("Program");
		header.createCell(index++).setCellValue("Sem");
		header.createCell(index++).setCellValue("Email ID");
		header.createCell(index++).setCellValue("Mobile");
		header.createCell(index++).setCellValue("Enroll Month/Year");
		header.createCell(index++).setCellValue("Validity End Month/year");
		header.createCell(index++).setCellValue("IC");
		header.createCell(index++).setCellValue("LC");
		header.createCell(index++).setCellValue("Exam Mode");
		header.createCell(index++).setCellValue("Project submission status");
		header.createCell(index++).setCellValue("Project payment status");
		
//		IC project submission status - submitted / not submitted , project payment received/ not received
		int rowNum = 1;
		for (int i = 0 ; i < eligiblelist.size(); i++) {
			index = 0;
			//create the row data

         	
         	try {
			HSSFRow row = sheet.createRow(rowNum++);
			StudentExamBean bean = eligiblelist.get(i);
			
			StudentExamBean student = sapIdStudentsMap.get(bean.getSapid());
			
			String ic = "";
			String lc = "";
			String examMode = "Offline";
			
			if(student != null){
				ic = student.getCenterName();
				CenterExamBean center = icLcMap.get(student.getCenterCode()); 
				if(center != null){
					lc = center.getLc();
				}
				
				if("Online".equals(student.getExamMode())){
					examMode = "Online";
				}
			}
			
			row.createCell(index++).setCellValue((i+1));
			row.createCell(index++).setCellValue(bean.getSapid());
			row.createCell(index++).setCellValue(bean.getFirstName() + " " + bean.getLastName());
			row.createCell(index++).setCellValue(bean.getProgram());
			row.createCell(index++).setCellValue(bean.getSem());
			row.createCell(index++).setCellValue(bean.getEmailId());
			row.createCell(index++).setCellValue(bean.getMobile());
			row.createCell(index++).setCellValue(bean.getEnrollmentMonth() + " " + bean.getEnrollmentYear());
			row.createCell(index++).setCellValue(bean.getValidityEndMonth() + " " + bean.getValidityEndYear());
			row.createCell(index++).setCellValue(ic);
			row.createCell(index++).setCellValue(lc);
			row.createCell(index++).setCellValue(examMode);
			row.createCell(index++).setCellValue(bean.getStatus());	
			if(bean.getBooked().equalsIgnoreCase("Y")) 
			row.createCell(index++).setCellValue("Recived");
			else
			row.createCell(index++).setCellValue("Not Recived");
         	} catch (Exception e) {
         		
			}
        }
		sheet.autoSizeColumn(0); sheet.autoSizeColumn(1);
		sheet.autoSizeColumn(2); sheet.autoSizeColumn(3);
		sheet.autoSizeColumn(4); sheet.autoSizeColumn(5);
		sheet.autoSizeColumn(6); sheet.autoSizeColumn(7);
		sheet.autoSizeColumn(8); sheet.autoSizeColumn(9);
		sheet.autoSizeColumn(10);sheet.autoSizeColumn(11);
		sheet.autoSizeColumn(12);
		sheet.setAutoFilter(CellRangeAddress.valueOf("A1:R"+eligiblelist.size()));
	}
	
}
