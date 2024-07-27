package com.nmims.views;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;

import com.nmims.beans.CaseStudyExamBean;
import com.nmims.beans.ExamBookingExamBean;
import com.nmims.beans.StudentExamBean;

public class StudentCaseStudySubmissionReportExcelView extends AbstractXlsxStreamingView implements ApplicationContextAware{

	private static ApplicationContext act = null;
	
	@Override
	protected void buildExcelDocument(Map model, Workbook workbook, HttpServletRequest request, HttpServletResponse response) 
			throws Exception {
		String userId=(String) request.getSession().getAttribute("userId");
		List<String> ICLCRESTRICTED_USER_LIST=(List<String>) request.getSession().getAttribute("ICLCRESTRICTED_USER_LIST");
		act = getApplicationContext();
		
		List<CaseStudyExamBean> studentList = (List<CaseStudyExamBean>) model.get("submissions");
		Map<String,StudentExamBean> studentDetails = (Map<String,StudentExamBean>) model.get("studentDetails");
		
		Sheet sheet = workbook.createSheet("Student Case Study Submission Report");
		int index = 0;
		Row header = sheet.createRow(0);
		
		header.createCell(index++).setCellValue("Sr. No");
		header.createCell(index++).setCellValue("SAP ID");
		header.createCell(index++).setCellValue("Name");
		//header.createCell(index++).setCellValue("Email");
		//header.createCell(index++).setCellValue("Phone");
		if(!ICLCRESTRICTED_USER_LIST.contains(userId)) {
		header.createCell(index++).setCellValue("Center Id");
		header.createCell(index++).setCellValue("Center Name");
		header.createCell(index++).setCellValue("LC");
		}
		header.createCell(index++).setCellValue("Topic");
		header.createCell(index++).setCellValue("Status");
		header.createCell(index++).setCellValue("BatchYear"); 
		header.createCell(index++).setCellValue("BatchMonth"); 
	    header.createCell(index++).setCellValue("LastModifiedDate");
	    header.createCell(index++).setCellValue("LastModifiedBy");
	    
	    int rowNum = 1;
	    for (int i = 0; i < studentList.size(); i++) {
			index = 0;
			Row row = sheet.createRow(rowNum++);
			CaseStudyExamBean bean = studentList.get(i);
			StudentExamBean sbean = studentDetails.get(bean.getSapid());
			row.createCell(index++).setCellValue((i+1));
			row.createCell(index++).setCellValue(bean.getSapid());
			row.createCell(index++).setCellValue(sbean.getFirstName()+" "+sbean.getLastName());
			//row.createCell(index++).setCellValue(sbean.getEmailId());
			//row.createCell(index++).setCellValue(sbean.getMobile());
			if(!ICLCRESTRICTED_USER_LIST.contains(userId)) {
			row.createCell(index++).setCellValue(sbean.getCenterCode());
			row.createCell(index++).setCellValue(sbean.getCenterName());
			row.createCell(index++).setCellValue(sbean.getLc());
			}
			row.createCell(index++).setCellValue(bean.getTopic());
			row.createCell(index++).setCellValue(bean.getStatus());
			row.createCell(index++).setCellValue(bean.getBatchYear());
			row.createCell(index++).setCellValue(bean.getBatchMonth());
			row.createCell(index++).setCellValue(bean.getLastModifiedDate());
			row.createCell(index++).setCellValue(bean.getLastModifiedBy());
		}
	}

}
