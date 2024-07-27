package com.nmims.stratergies.impl;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.StudentExamBean;
import com.nmims.beans.UFMNoticeBean;
import com.nmims.daos.UFMNoticeDAO;
import com.nmims.stratergies.UFMActionStrategyInterface;


@Service("ufmActionStrategy")
public class UFMActionStrategy implements UFMActionStrategyInterface {

	@Autowired
	UFMNoticeDAO dao;
	
	public static final Logger ufm = LoggerFactory.getLogger("ufm");
	
	@Override
	public List<UFMNoticeBean> readShowCauseList(UFMNoticeBean inputBean) throws Exception {

		int SAPID = 0;
		int SUBJECT = 1;
		int EXAM_DATE = 2;
		int EXAM_TIME = 3;
		
		ByteArrayInputStream bis = new ByteArrayInputStream(inputBean.getFileData().getBytes());
		Workbook workbook;

		int currentRow = 0;
		
		try {
			if (inputBean.getFileData().getOriginalFilename().toLowerCase().endsWith("xls")) {
				workbook = new HSSFWorkbook(bis);
			} else if (inputBean.getFileData().getOriginalFilename().toLowerCase().endsWith("xlsx")) {
				workbook = new XSSFWorkbook(bis);
			} else {
				ufm.info("Received file does not have a standard excel extension.");
				throw new IllegalArgumentException("Received file does not have a standard excel extension.");
			}
			
			
			XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();
			
			if(rowIterator.hasNext()){
				rowIterator.next();
			}

			List<UFMNoticeBean> toReturn = new ArrayList<UFMNoticeBean>();
			
			while(rowIterator.hasNext()) {
				Row row = rowIterator.next();
				if(row!=null){
					UFMNoticeBean bean = new UFMNoticeBean();
					currentRow++;
					
					row.getCell(SAPID, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					String sapid = row.getCell(SAPID, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					
					// If sapid is empty, dont process row.
					if(StringUtils.isBlank(sapid)) {
						continue;
					}
					bean.setSapid(sapid);
					
					row.getCell(SUBJECT, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					String subject = row.getCell(SUBJECT, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					bean.setSubject(subject);
					
					Date examDate = null, examTime = null;
					String examDateStr, examTimeStr;
					Cell dateCell = row.getCell(EXAM_DATE, Row.CREATE_NULL_AS_BLANK);
					Cell startTimeCell = row.getCell(EXAM_TIME, Row.CREATE_NULL_AS_BLANK);
					

					if(dateCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
						if (DateUtil.isCellDateFormatted(dateCell)) {
							examDate = dateCell.getDateCellValue();
						} else {
							examDate = HSSFDateUtil.getJavaDate(dateCell.getNumericCellValue());
						}
						SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
						examDateStr = sdfDate.format(examDate);
					}else {
						dateCell.setCellType(Cell.CELL_TYPE_STRING);
						examDateStr = dateCell.getStringCellValue();
					}

					if(startTimeCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
						if (DateUtil.isCellDateFormatted(startTimeCell)) {
							examTime = startTimeCell.getDateCellValue();
						} else {
							examTime = HSSFDateUtil.getJavaDate(startTimeCell.getNumericCellValue());
						}
						SimpleDateFormat sdfTime = new java.text.SimpleDateFormat("HH:mm:ss");
						 examTimeStr = sdfTime.format(examTime);

					}else {
						startTimeCell.setCellType(Cell.CELL_TYPE_STRING);
						examTimeStr = startTimeCell.getStringCellValue();
					}
					
					bean.setExamDate(examDateStr);
					bean.setExamTime(examTimeStr);
					
					bean.setYear(inputBean.getYear());
					bean.setMonth(inputBean.getMonth());
					bean.setStage(inputBean.getStage());

					bean.setCreatedBy(inputBean.getCreatedBy());
					bean.setLastModifiedBy(inputBean.getCreatedBy());
					
					//Added by shivam.pandey.EXT
					bean.setCategory(inputBean.getCategory());
					
					toReturn.add(bean);
				}
			}
			return toReturn;
		} catch (Exception e) {
			ufm.info("Error Reading uploaded file at row " + currentRow + ".\nError : " + e.getMessage());
			throw new Exception("Error Reading uploaded file at row " + currentRow + ".\nError : " + e.getMessage());
		}
	}

	public void checkUploadedUFMBean(UFMNoticeBean bean) {
		int count = dao.checkIfUFMShowCauseRecordExists(bean.getSapid(), bean.getSubject(), bean.getExamDate(), bean.getExamTime(), bean.getYear(), bean.getMonth(), bean.getCategory());
		if(count == 0) {
			bean.setError("No records found.");
			return;
		} else if (count > 1) {
			bean.setError("Multiple Bookings Found!");
			return;
		} else {
			return;
		}
	}

	public void updateUFMActionRecord(UFMNoticeBean bean) {
		dao.updateUFMActionRecord(bean);
	}

	public StudentExamBean getStudent(String sapid) {
		return dao.getStudent(sapid);
	}
	
	
	/**
	 * shivam.pandey.EXT COC - START
	 */
	@Override
	public void updateCOCActionRecord(UFMNoticeBean bean) {
		dao.updateUFMActionRecord(bean);
	}
	
	@Override
	public void checkUploadedCOCShowCauseFile(UFMNoticeBean bean) {
		int count = dao.checkIfUFMShowCauseRecordExists(bean.getSapid(), bean.getSubject(), bean.getExamDate(), bean.getExamTime(), bean.getYear(), bean.getMonth(), bean.getCategory());
		if(count == 0) {
			bean.setError("No records found for COC!");
			return;
		} else if (count > 1) {
			bean.setError("Duplicate record found for COC!");
			return;
		} else {
			return;
		}
	}
	/**
	 * shivam.pandey.EXT COC - END
	 */
	
	
	/**
	 * shivam.pandey.EXT Disconnect - START
	 */
	@Override
	public void updateDisconnectActionRecord(UFMNoticeBean bean) {
		dao.updateUFMActionRecord(bean);
	}
	
	@Override
	public void checkUploadedDisconnectShowCauseFile(UFMNoticeBean bean) {
		int count = dao.checkIfUFMShowCauseRecordExists(bean.getSapid(), bean.getSubject(), bean.getExamDate(), bean.getExamTime(), bean.getYear(), bean.getMonth(), bean.getCategory());
		if(count == 0) {
			bean.setError("No records found for Disconnect Above 15 Minutes!");
			return;
		} else if (count > 1) {
			bean.setError("Duplicate record found for Disconnect Above 15 Minutes!");
			return;
		} else {
			return;
		}
	}
	/**
	 * shivam.pandey.EXT Disconnect - END
	 */
}
