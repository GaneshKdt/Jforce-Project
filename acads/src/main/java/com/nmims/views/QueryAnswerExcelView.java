package com.nmims.views;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.web.servlet.view.document.AbstractExcelView;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.nmims.beans.CenterAcadsBean;
import com.nmims.beans.SessionAttendanceFeedbackAcads;
import com.nmims.beans.SessionDayTimeAcadsBean;
import com.nmims.beans.SessionQueryAnswer;
import com.nmims.beans.StudentAcadsBean;
import com.nmims.daos.PCPBookingDAO;

public class QueryAnswerExcelView extends AbstractExcelView implements ApplicationContextAware{
	
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
		header.createCell(index++).setCellValue("Year");
	    header.createCell(index++).setCellValue("Month");
		header.createCell(index++).setCellValue("Subject");
		header.createCell(index++).setCellValue("Session Name");
		header.createCell(index++).setCellValue("Faculty");
		header.createCell(index++).setCellValue("Faculty Name");
		header.createCell(index++).setCellValue("Faculty Email");
		
		header.createCell(index++).setCellValue("Query");
		header.createCell(index++).setCellValue("Answer");
		header.createCell(index++).setCellValue("Query Type");
		header.createCell(index++).setCellValue("Is Public");
		header.createCell(index++).setCellValue("Hours Since Questions");
		
		header.createCell(index++).setCellValue("Raised By");
		header.createCell(index++).setCellValue("Query Posted Date");
		header.createCell(index++).setCellValue("Last Modified By");
		header.createCell(index++).setCellValue("Last Modified Date");
		header.createCell(index++).setCellValue("Hours Since Faculty Reply");
		header.createCell(index++).setCellValue("IC");
		header.createCell(index++).setCellValue("LC");
 
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
			row.createCell(index++).setCellValue(new Double(bean.getYear()));
			row.createCell(index++).setCellValue(bean.getMonth());
			row.createCell(index++).setCellValue(bean.getSubject());
			row.createCell(index++).setCellValue(bean.getSessionName());
			row.createCell(index++).setCellValue(bean.getFacultyId());
			row.createCell(index++).setCellValue(bean.getFirstName() + " " + bean.getLastName());
			row.createCell(index++).setCellValue(bean.getEmail());
			
			row.createCell(index++).setCellValue(bean.getQuery());
			row.createCell(index++).setCellValue(bean.getAnswer());
			row.createCell(index++).setCellValue(bean.getQueryType());
			row.createCell(index++).setCellValue(bean.getIsPublic());
			row.createCell(index++).setCellValue(new Double(bean.getHoursSinceQuestions()));
			row.createCell(index++).setCellValue(bean.getCreatedBy());
			row.createCell(index++).setCellValue(bean.getCreatedDate());
			row.createCell(index++).setCellValue(bean.getLastModifiedBy());
			row.createCell(index++).setCellValue(bean.getLastModifiedDate());
			row.createCell(index++).setCellValue(bean.getHoursSinceFacultyReply());
			row.createCell(index++).setCellValue(ic);
			row.createCell(index++).setCellValue(lc);
        }
		sheet.autoSizeColumn(0); sheet.autoSizeColumn(1);
		sheet.autoSizeColumn(2); sheet.autoSizeColumn(3);
		sheet.autoSizeColumn(4); sheet.autoSizeColumn(5);
		sheet.autoSizeColumn(8); sheet.autoSizeColumn(9);
		sheet.autoSizeColumn(10); sheet.autoSizeColumn(11);
		sheet.autoSizeColumn(12);sheet.autoSizeColumn(13);
		
		sheet.setAutoFilter(CellRangeAddress.valueOf("A1:N"+(queryList.size()+1)));
	}
}
