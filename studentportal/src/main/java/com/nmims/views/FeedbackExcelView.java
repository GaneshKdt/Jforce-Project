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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractExcelView;



import com.nmims.beans.FeedbackBean;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.daos.ServiceRequestDao;
import com.nmims.daos.PortalDao;

@Component("feedbackExcelView")
public class FeedbackExcelView  extends AbstractExcelView implements ApplicationContextAware{
	
	
	private static ApplicationContext act = null;
	
	@Override
	protected void buildExcelDocument(Map model, HSSFWorkbook workbook,	HttpServletRequest request, HttpServletResponse response)
		throws Exception {
 
		act = getApplicationContext();
		PortalDao portalDao = (PortalDao)act.getBean("portalDAO");
		List<FeedbackBean> feedBackBeanList = (List<FeedbackBean>) model.get("feedbackList");
		//create a wordsheet
		HSSFSheet sheet = workbook.createSheet("Service Requests");
		int index = 0;
		HSSFRow header = sheet.createRow(0);
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("SAP ID");
		header.createCell(index++).setCellValue("Student Name");
		header.createCell(index++).setCellValue("Comments");
		header.createCell(index++).setCellValue("Category");
		header.createCell(index++).setCellValue("Rating");
		header.createCell(index++).setCellValue("Center Name");
		header.createCell(index++).setCellValue("Created Date");
		int rowNum = 1;
		for (int i = 0 ; i < feedBackBeanList.size(); i++) {
			index = 0;
			
			HSSFRow row = sheet.createRow(rowNum++);
			FeedbackBean feedBackBean = feedBackBeanList.get(i);
			System.out.println("Excel view TestSYSOUT--->"+feedBackBean.getCreatedBy());
			StudentStudentPortalBean studentBean = portalDao.getSingleStudentsData(feedBackBean.getCreatedBy());
			row.createCell(index++).setCellValue((i+1));
			row.createCell(index++).setCellValue(new Double(feedBackBean.getCreatedBy()));
			row.createCell(index++).setCellValue(studentBean.getFirstName());
			row.createCell(index++).setCellValue(feedBackBean.getComments());
			row.createCell(index++).setCellValue(feedBackBean.getCategory());
			row.createCell(index++).setCellValue(feedBackBean.getRating());
			row.createCell(index++).setCellValue(studentBean.getCenterName());
			row.createCell(index++).setCellValue(feedBackBean.getCreatedDate());
			
			
			
        }
		sheet.autoSizeColumn(0); sheet.autoSizeColumn(1);
		sheet.autoSizeColumn(2); sheet.autoSizeColumn(3);
		sheet.autoSizeColumn(4); sheet.autoSizeColumn(5);
		sheet.autoSizeColumn(6); sheet.autoSizeColumn(7);
		sheet.autoSizeColumn(8); sheet.autoSizeColumn(9);
		sheet.autoSizeColumn(10);
		sheet.setAutoFilter(CellRangeAddress.valueOf("A1:R"+feedBackBeanList.size()));
	}
}
