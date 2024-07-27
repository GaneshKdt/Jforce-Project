package com.nmims.helpers;

import java.io.ByteArrayInputStream;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import com.nmims.beans.LevelBasedProjectBean;
@Component
public class LevelBasedProjectHelper {
	
	public void readStudentGuidMappingDataFromExcel(
		LevelBasedProjectBean projectSubmissionBean, List<LevelBasedProjectBean> projectSubmissionBeanList, List<LevelBasedProjectBean> levelBasedProjectErrorList
	) throws Exception{
		final int SAP_ID = 0;
		final int FACULTY_ID = 1;
		Sheet sheet;
		
		ByteArrayInputStream bis = new ByteArrayInputStream(projectSubmissionBean.getFileData().getBytes());
		Workbook workbook;
		if (projectSubmissionBean.getFileData().getOriginalFilename().toLowerCase().endsWith("xls")) {
			workbook = new HSSFWorkbook(bis);
			 sheet = (HSSFSheet) workbook.getSheetAt(0);
		} else if (projectSubmissionBean.getFileData().getOriginalFilename().toLowerCase().endsWith("xlsx")) {
			workbook = new XSSFWorkbook(bis);
			sheet = (XSSFSheet) workbook.getSheetAt(0);
		} else {
			throw new IllegalArgumentException("Received file does not have a standard excel extension.");
		}
		
		// sheet = (XSSFSheet) workbook.getSheetAt(0);
		Iterator<Row> rowIterator = sheet.iterator();
		int i = 0;
		//Skip first row since it contains column names, not data.
		if(rowIterator.hasNext()){
			rowIterator.next();
		}
		
		while(rowIterator.hasNext()) {
			i++;
			Row row = rowIterator.next();
			LevelBasedProjectBean tmp_bean = new LevelBasedProjectBean();
			
			if(row!=null){
				row.getCell(SAP_ID, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
				row.getCell(FACULTY_ID, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
				
				String sapid = row.getCell(SAP_ID, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
				String faculty_id = row.getCell(FACULTY_ID, Row.CREATE_NULL_AS_BLANK).getStringCellValue();

				tmp_bean.setSapId(sapid);
				tmp_bean.setFacultyId(faculty_id);
				tmp_bean.setSubject(projectSubmissionBean.getSubject());
				tmp_bean.setYear(projectSubmissionBean.getYear());
				tmp_bean.setMonth(projectSubmissionBean.getMonth());

				tmp_bean.setCreatedBy(projectSubmissionBean.getCreatedBy());
				tmp_bean.setLastModifiedBy(projectSubmissionBean.getLastModifiedBy());
				
				if(StringUtils.isBlank(sapid) && StringUtils.isBlank(faculty_id)) {
					continue;
				}

				if(StringUtils.isBlank(sapid)) {
					tmp_bean.setError("Sapid Blank for Row " + (i+1));
					levelBasedProjectErrorList.add(tmp_bean);
					continue;
				}

				if(StringUtils.isBlank(faculty_id)) {
					tmp_bean.setError("Faculty Id Blank for Row " + (i+1));
					levelBasedProjectErrorList.add(tmp_bean);
					continue;
				}
				projectSubmissionBeanList.add(tmp_bean);
			}
		}
		
	}

}
