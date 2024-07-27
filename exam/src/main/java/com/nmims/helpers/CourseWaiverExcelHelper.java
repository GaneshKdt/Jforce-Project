package com.nmims.helpers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.nmims.beans.FileBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.dto.CustomCourseWaiverDTO;

public class CourseWaiverExcelHelper {

	
	public List<CustomCourseWaiverDTO> readrCourseWaiverSubject(FileBean fileBean,String userId){


		int  SAPID_INDEX = 0;
		int  SUBJECT_INDEX = 1;
		int  COURSE_WAIVER_INDEX = 2;
		int  SEM_INDEX = 3;

		ByteArrayInputStream bis = new ByteArrayInputStream(fileBean.getFileData().getBytes());
		Workbook workbook;
		List<CustomCourseWaiverDTO> courseWaiverList = new ArrayList<CustomCourseWaiverDTO>();
	

		try {
			if (fileBean.getFileData().getOriginalFilename().endsWith("xls")) {
				workbook = new HSSFWorkbook(bis);
			} else if (fileBean.getFileData().getOriginalFilename().endsWith("xlsx")) {
				workbook = new XSSFWorkbook(bis);
			} else {
				throw new IllegalArgumentException("Received file does not have a standard excel extension.");
			}

			//Get first/desired sheet from the workbook
			XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);

			Iterator<Row> rowIterator = sheet.iterator();

			CustomCourseWaiverDTO bean = null;
			int i = 0;
			//Skip first row since it contains column names, not data.
			if(rowIterator.hasNext()){
				Row row = rowIterator.next();
			}
			while(rowIterator.hasNext()) {
				i++;
				Row row = rowIterator.next();


				if(row!=null){
				
					row.getCell(SAPID_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(SUBJECT_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(COURSE_WAIVER_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(SEM_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					
			

					bean = new CustomCourseWaiverDTO();

		
					String sapid = row.getCell(SAPID_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String subject = row.getCell(SUBJECT_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String courseWaiver = row.getCell(COURSE_WAIVER_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String sem = row.getCell(SEM_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
				
			
					if("".equals(sapid.trim()) && "".equals(subject.trim()) && "".equals(courseWaiver)){
						break;
					}

					bean.setSapid(Long.valueOf(sapid));
				
					bean.setSubjectName(subject);
					
					bean.setCourseWaiver(courseWaiver);
					if("WaivedOff".equalsIgnoreCase(courseWaiver)) {
						bean.setSem(0);
					}else {
					bean.setSem(Integer.parseInt(sem));
					}

				courseWaiverList.add(bean);
					

					
				}
			}
		} catch (IOException e) {
			//logger.error("readWrittenMarksExcel : "+e.getMessage());//by Vilpesh 2022-02-25
		}

		
		return courseWaiverList;
	}
}
