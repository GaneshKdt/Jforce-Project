package com.nmims.views;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.web.servlet.view.document.AbstractExcelView;

import com.nmims.beans.FacultyAcadsBean;
import com.nmims.beans.SearchTimeBoundContent;
import com.nmims.beans.SessionDayTimeAcadsBean;

public class SearchTimeBoundContentExcelView extends AbstractExcelView
{
	
	
	@Override
	protected void buildExcelDocument(Map model, HSSFWorkbook workbook,	HttpServletRequest request, HttpServletResponse response)
		throws Exception {
		
		List<SearchTimeBoundContent> contentData = (List<SearchTimeBoundContent>) model.get("contentlist");
		HashMap<String,FacultyAcadsBean> mapOfFacultyIdAndFacultyRecord = (HashMap<String,FacultyAcadsBean>) request.getSession().getAttribute("mapOfFacultyIdAndFacultyRecord");
		
		//create a wordsheet
		HSSFSheet sheet = workbook.createSheet("TimeBound Content");
		
 		int index = 0;
		HSSFRow header = sheet.createRow(0);
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("Year");
	    header.createCell(index++).setCellValue("Month");
		header.createCell(index++).setCellValue("Session Date");
		header.createCell(index++).setCellValue("Session Start Time");
		header.createCell(index++).setCellValue("Day");
		header.createCell(index++).setCellValue("Subject");
		header.createCell(index++).setCellValue("Subject Code");
		header.createCell(index++).setCellValue("Session Name");
		header.createCell(index++).setCellValue("Session ID");
		header.createCell(index++).setCellValue("Faculty ID");
		header.createCell(index++).setCellValue("Faculty Location");
		header.createCell(index++).setCellValue("Faculty Name");
		header.createCell(index++).setCellValue("Faculty Email");
		header.createCell(index++).setCellValue("Alt Faculty Name");
		header.createCell(index++).setCellValue("Alt Faculty Email ");
		header.createCell(index++).setCellValue("Track");
		header.createCell(index++).setCellValue("ModuleId");
		header.createCell(index++).setCellValue("Content Name");
		header.createCell(index++).setCellValue("Content Created Date");
		header.createCell(index++).setCellValue("Content LastModified Date ");
		header.createCell(index++).setCellValue("No. of Delay Days");

 
		int rowNum = 1;
		for (int i = 0 ; i < contentData.size(); i++) {
			index = 0;
			//create the row data
			
			HSSFRow row = sheet.createRow(rowNum++);
			SearchTimeBoundContent bean = contentData.get(i);
			
			String faculty = mapOfFacultyIdAndFacultyRecord.get(bean.getFacultyId()) !=null ? mapOfFacultyIdAndFacultyRecord.get(bean.getFacultyId()).getFullName():"";
			String facultyEmail = mapOfFacultyIdAndFacultyRecord.get(bean.getFacultyId()) !=null ? mapOfFacultyIdAndFacultyRecord.get(bean.getFacultyId()).getEmail():"";
			String altFaculty = mapOfFacultyIdAndFacultyRecord.get(bean.getAltFacultyId()) !=null ? mapOfFacultyIdAndFacultyRecord.get(bean.getAltFacultyId()).getFullName():"";
			String altFacultyEmail = mapOfFacultyIdAndFacultyRecord.get(bean.getAltFacultyId()) !=null ? mapOfFacultyIdAndFacultyRecord.get(bean.getAltFacultyId()).getEmail():"";
	
			row.createCell(index++).setCellValue((i+1));
			row.createCell(index++).setCellValue(new Double(bean.getYear()));
			row.createCell(index++).setCellValue(bean.getMonth());
			row.createCell(index++).setCellValue(bean.getDate());
			row.createCell(index++).setCellValue(bean.getStartTime());
			row.createCell(index++).setCellValue(bean.getDay());
			row.createCell(index++).setCellValue(bean.getSubject());
			row.createCell(index++).setCellValue(bean.getSubjectcode());
			row.createCell(index++).setCellValue(bean.getSessionName());
			row.createCell(index++).setCellValue(bean.getSessionId());
			row.createCell(index++).setCellValue(bean.getFacultyId());
			row.createCell(index++).setCellValue(bean.getFacultyLocation());
			row.createCell(index++).setCellValue(faculty);
			row.createCell(index++).setCellValue(facultyEmail);
			row.createCell(index++).setCellValue(altFaculty);
			row.createCell(index++).setCellValue(altFacultyEmail);
			row.createCell(index++).setCellValue(bean.getTrack());
			row.createCell(index++).setCellValue(bean.getModuleid());
			row.createCell(index++).setCellValue(bean.getContentName());
			row.createCell(index++).setCellValue(bean.getCreatedDate());
			row.createCell(index++).setCellValue(bean.getLastModifiedDate());
			row.createCell(index++).setCellValue(bean.getDelayDays());
			
        }
		sheet.autoSizeColumn(0); sheet.autoSizeColumn(1);
		sheet.autoSizeColumn(2); sheet.autoSizeColumn(3);
		sheet.autoSizeColumn(4); sheet.autoSizeColumn(5);
		sheet.autoSizeColumn(6); sheet.autoSizeColumn(7);
		sheet.autoSizeColumn(8); sheet.autoSizeColumn(9);
		
		sheet.setAutoFilter(CellRangeAddress.valueOf("A1:AU"+(contentData.size()+1)));
	}


}
