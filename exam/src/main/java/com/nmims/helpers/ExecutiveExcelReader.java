package com.nmims.helpers;

import java.io.ByteArrayInputStream;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import com.nmims.beans.FileBean;
import com.nmims.beans.ExecutiveTimetableBean;



public class ExecutiveExcelReader{
	@SuppressWarnings({ "rawtypes", "unused" })
	public ArrayList<List> readTimeTableExcel(FileBean fileBean, ArrayList<String> programList, ArrayList<String> subjectList, String userId, List<String> yearList, List<String> monthList){

		int  DATE_INDEX = 0;
		int  START_TIME_INDEX = 1;
		int  END_TIME_INDEX = 2;
	


		ByteArrayInputStream bis = new ByteArrayInputStream(fileBean.getFileData().getBytes());
		Workbook workbook;
		List<ExecutiveTimetableBean> marksBeanList = new ArrayList<ExecutiveTimetableBean>();
		List<ExecutiveTimetableBean> errorBeanList = new ArrayList<ExecutiveTimetableBean>();
		ArrayList<List> resultList = new ArrayList<>();
		HashMap<String, String> subjectDateMap = new HashMap<>();

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

			int i = 0;
			//Skip first row since it contains column names, not data.
			if(rowIterator.hasNext()){
				Row row = rowIterator.next();
			}
			while(rowIterator.hasNext()) {
				i++;
				Row row = rowIterator.next();

				if(row!=null){

					ExecutiveTimetableBean bean = new ExecutiveTimetableBean();

					Date date = null, startTime = null, endTime = null;
					Cell dateCell = row.getCell(DATE_INDEX);
					Cell startTimeCell = row.getCell(START_TIME_INDEX);
					Cell endTimeCell = row.getCell(END_TIME_INDEX);

					if(dateCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
						if (DateUtil.isCellDateFormatted(dateCell)) {
							date = dateCell.getDateCellValue();
						} else {
							date = HSSFDateUtil.getJavaDate(dateCell.getNumericCellValue());
						}
					}else{
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Format of cell : Not a date ");
						bean.setErrorRecord(true);
					}

					if(startTimeCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
						if (DateUtil.isCellDateFormatted(startTimeCell)) {
							startTime = startTimeCell.getDateCellValue();
						} else {
							startTime = HSSFDateUtil.getJavaDate(startTimeCell.getNumericCellValue());
						}
					}else{
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Format Start time cell : Not a date/time ");
						bean.setErrorRecord(true);
					}


					if(endTimeCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
						if (DateUtil.isCellDateFormatted(endTimeCell)) {
							endTime = endTimeCell.getDateCellValue();
						} else {
							endTime = HSSFDateUtil.getJavaDate(endTimeCell.getNumericCellValue());
						}
					}else{
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Format End time cell : Not a date/time ");
						bean.setErrorRecord(true);
					}

					if(bean.isErrorRecord()){
						errorBeanList.add(bean);
						continue;
					}

					SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
					String examDate = sdfDate.format(date);

					SimpleDateFormat sdfTime = new java.text.SimpleDateFormat("HH:mm");

					bean.setExamYear(fileBean.getYear());
					bean.setExamMonth(fileBean.getMonth());
					bean.setPrgmStructApplicable(fileBean.getPrgmStructApplicable());
					bean.setDate(examDate);
					bean.setStartTime(sdfTime.format(startTime));
					bean.setEndTime(sdfTime.format(endTime));
					bean.setCreatedBy(userId);
					bean.setLastModifiedBy(userId);
					
				
					if(!(yearList.contains(bean.getExamYear()) && monthList.contains(bean.getExamMonth()))){
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Year/Month for record with subject "+bean.getSubject()+" and program "+bean.getProgram());
						bean.setErrorRecord(true);
					}

					/*if(!"NA".equals(program.trim())){//NA mean timetable for Re-sit exam where program and subject is not applicable
						
						if(!subjectList.contains(bean.getSubject())){
							bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Subject ");
							bean.setErrorRecord(true);
						}
						
						if(!programList.contains(bean.getProgram())){
							bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Program ");
							bean.setErrorRecord(true);
						}
						
						if(!subjectDateMap.containsKey(subject.trim())){
							subjectDateMap.put(subject.trim(), examDate);
						}else{
							String examDateForSubject = subjectDateMap.get(subject.trim());
							if(!examDateForSubject.equals(examDate) && (!"PGDMM-MLI".equals(program))){
								bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Different exam dates for same SUBJECT:"+bean.getSubject());
								bean.setErrorRecord(true);
							}

						}
						
					}*/

					if(bean.isErrorRecord()){
						errorBeanList.add(bean);
					}else{
						marksBeanList.add(bean);
					}

					if(i % 1000 == 0){
					}
				}
			}
		} catch (IOException e) {
			
		}
		resultList.add(marksBeanList);
		resultList.add(errorBeanList);
		return resultList;
	}

}
