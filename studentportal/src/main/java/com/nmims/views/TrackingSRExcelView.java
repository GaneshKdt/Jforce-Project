package com.nmims.views;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractXlsView;

import com.nmims.beans.MassUploadTrackingSRBean;

@Component("trackingSRExcelView")
public class TrackingSRExcelView extends AbstractXlsView {

	@Override
	protected void buildExcelDocument(@SuppressWarnings("rawtypes") Map model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		@SuppressWarnings("unchecked")
		List<MassUploadTrackingSRBean> searchTrackingList = (List<MassUploadTrackingSRBean>) model.get("searchTrackingList");
		//create a wordsheet
		Sheet sheet = workbook.createSheet("trackingReport");
		
		int index = 0;
		Row header = sheet.createRow(0);
		header.createCell(index++).setCellValue("SR ID");
		header.createCell(index++).setCellValue("Student Number");
		header.createCell(index++).setCellValue("Student Name");
		header.createCell(index++).setCellValue("Tracking No");
		header.createCell(index++).setCellValue("Courier Partner Name");
		header.createCell(index++).setCellValue("Tracking Link");
		
		int rowNum = 1;
		for (int i = 0 ; i < searchTrackingList.size(); i++) {
			//create the row data
			index = 0;
			Row row = sheet.createRow(rowNum++);
			MassUploadTrackingSRBean bean = searchTrackingList.get(i);
			row.createCell(index++).setCellValue(bean.getServiceRequestId());
			row.createCell(index++).setCellValue(bean.getSapId());
			row.createCell(index++).setCellValue(bean.getStudentName());
			row.createCell(index++).setCellValue(bean.getTrackId());
			row.createCell(index++).setCellValue(bean.getCourierName());
			row.createCell(index++).setCellValue(bean.getUrl());
		}
	}
}
