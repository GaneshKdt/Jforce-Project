package com.nmims.views;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import org.springframework.web.servlet.view.document.AbstractExcelView;

import com.nmims.beans.CenterAcadsBean;
import com.nmims.beans.SessionQueryAnswer;
import com.nmims.beans.StudentAcadsBean;
import com.nmims.daos.PCPBookingDAO;

public class PostQueryReportExcelView extends AbstractExcelView implements ApplicationContextAware{
	
	private ApplicationContext act = null;
	
	@Override
	protected void buildExcelDocument(Map model, HSSFWorkbook workbook,	HttpServletRequest request, HttpServletResponse response)
		throws Exception {
		
		act = getApplicationContext();
		List<SessionQueryAnswer> queryList = (List<SessionQueryAnswer>) model.get("queryList");
		PCPBookingDAO dao = (PCPBookingDAO)act.getBean("pcpBookingDAO");
		HashMap<String, StudentAcadsBean> sapIdStudentsMap = dao.getAllStudents();
		HashMap<String, CenterAcadsBean> icLcMap = dao.getICLCMap();
		HSSFSheet sheet = workbook.createSheet("Query & Answer");
		  
 		int index = 0;
		HSSFRow header = sheet.createRow(0);
		header.createCell(index++).setCellValue("Sr. No."); 
		header.createCell(index++).setCellValue("Subject");
		header.createCell(index++).setCellValue("Batch Name");
		header.createCell(index++).setCellValue("Faculty ID");
		header.createCell(index++).setCellValue("Faculty Name");
		header.createCell(index++).setCellValue("Faculty Email");
		header.createCell(index++).setCellValue("Query");
		header.createCell(index++).setCellValue("Is Public");
		header.createCell(index++).setCellValue("Posted Date");
		
		//If isAnswered add column to show answered date
		if("Y".equalsIgnoreCase(queryList.get(0).getIsAnswered())) {
			header.createCell(index++).setCellValue("Faculty Answer");
			header.createCell(index++).setCellValue("Has Attachment");
			header.createCell(index++).setCellValue("Answered Date");
			header.createCell(index++).setCellValue("Days_Since_Answered");
		}else if("N".equalsIgnoreCase(queryList.get(0).getIsAnswered())){
			header.createCell(index++).setCellValue("Days_Since_Not_Answered");
		} 
		header.createCell(index++).setCellValue("Raised By");
		header.createCell(index++).setCellValue("Year");
		header.createCell(index++).setCellValue("Month");
		header.createCell(index++).setCellValue("Query Type"); 
 
		int rowNum = 1;
		for (int i = 0 ; i < queryList.size(); i++) {
			index = 0;
			//create the row data
			HSSFRow row = sheet.createRow(rowNum++);
			SessionQueryAnswer bean = queryList.get(i);
			StudentAcadsBean student = sapIdStudentsMap.get(bean.getSapId());
			String ic = "";
			String lc = "";
			if(student != null){
				ic = student.getCenterName();
				CenterAcadsBean center = icLcMap.get(student.getCenterCode()); 
				if(center != null){
					lc = center.getLc();
				}
			
			}
			 
			row.createCell(index++).setCellValue((i+1));
			row.createCell(index++).setCellValue(bean.getSubject());
			row.createCell(index++).setCellValue(bean.getBatchName());
			row.createCell(index++).setCellValue(bean.getAssignedToFacultyId());
			row.createCell(index++).setCellValue(bean.getFirstName()+" "+bean.getLastName());
			row.createCell(index++).setCellValue(bean.getEmail());
			row.createCell(index++).setCellValue(bean.getQuery());  
			row.createCell(index++).setCellValue(bean.getIsPublic());
			row.createCell(index++).setCellValue(bean.getCreatedDate());   
			
			//If isAnswered add column to show answered date
			if("Y".equalsIgnoreCase(queryList.get(0).getIsAnswered())) {
				
				row.createCell(index++).setCellValue(bean.getAnswer());
				row.createCell(index++).setCellValue(bean.getHasAttachment());
				row.createCell(index++).setCellValue(bean.getLastModifiedDate());
				String createdDate= bean.getCreatedDate();
				String answeredDate= bean.getLastModifiedDate();
			 	DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			    Date d1 = new Date();
			    Date d2 = new Date();
			    
			    d1 = format.parse(createdDate);
				d2 = format.parse(answeredDate);

				//in milliseconds
				long diff = d2.getTime() - d1.getTime();

				long diffDays = diff / (24 * 60 * 60 * 1000);

				row.createCell(index++).setCellValue(diffDays);
			
				
			}else if("N".equalsIgnoreCase(queryList.get(0).getIsAnswered())){
				String createdDate= bean.getCreatedDate();
			 	DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			    Date d1 = new Date();
			    Date d2 = new Date();
			    String currentDate = format.format(d2);
			   	
			    d1 = format.parse(createdDate);
				d2 = format.parse(currentDate);

				//in milliseconds
				long diff = d2.getTime() - d1.getTime();
				long diffDays = diff / (24 * 60 * 60 * 1000);
				row.createCell(index++).setCellValue(diffDays);
			}

			row.createCell(index++).setCellValue(bean.getSapId()); 
			row.createCell(index++).setCellValue(bean.getYear()); 
			row.createCell(index++).setCellValue(bean.getMonth()); 
			row.createCell(index++).setCellValue(bean.getQueryType()); 
			
		}
		sheet.autoSizeColumn(0); sheet.autoSizeColumn(1);
		sheet.autoSizeColumn(2); sheet.autoSizeColumn(3);
		sheet.autoSizeColumn(4); sheet.autoSizeColumn(5);
		sheet.autoSizeColumn(8); sheet.autoSizeColumn(9);
		sheet.autoSizeColumn(10); sheet.autoSizeColumn(11);
		sheet.autoSizeColumn(12);sheet.autoSizeColumn(13);
		sheet.autoSizeColumn(14);sheet.autoSizeColumn(15);
		
		sheet.setAutoFilter(CellRangeAddress.valueOf("A1:N"+(queryList.size()+1)));
	}
}
