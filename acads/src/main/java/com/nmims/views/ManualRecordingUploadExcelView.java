package com.nmims.views;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;
import com.nmims.beans.VideoContentAcadsBean;


public class ManualRecordingUploadExcelView extends AbstractXlsxStreamingView implements ApplicationContextAware{

	private ApplicationContext act = null;
	private static final Logger logger = LoggerFactory.getLogger(ManualRecordingUploadExcelView.class);

	@SuppressWarnings("unchecked")
	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		this.act = getApplicationContext();
		int index = 0;

		List<VideoContentAcadsBean> manualRecordingUploadReportList = new ArrayList<>();		
		
		try {
			manualRecordingUploadReportList = (List<VideoContentAcadsBean>) model.get("manualRecordingUploadReport");
			
			SXSSFSheet sessionPlanSheet = (SXSSFSheet) workbook.createSheet("Recording Upload List");
			SXSSFRow header = (SXSSFRow) sessionPlanSheet.createRow(0);
			header.createCell(index++).setCellValue("Sr. No.");
			header.createCell(index++).setCellValue("Year");
			header.createCell(index++).setCellValue("Month");
			header.createCell(index++).setCellValue("Subject");
			header.createCell(index++).setCellValue("File Name");
			header.createCell(index++).setCellValue("Faculty Id");
			header.createCell(index++).setCellValue("Session Id");
			header.createCell(index++).setCellValue("Keywords");
			header.createCell(index++).setCellValue("Description");
			header.createCell(index++).setCellValue("Video Url HD");
			header.createCell(index++).setCellValue("Video Url SD1");
			header.createCell(index++).setCellValue("Video Url SD2");

			int rowNum = 1;
			for (int i = 0; i < manualRecordingUploadReportList.size(); i++) {
				index = 0;
				Row row = sessionPlanSheet.createRow(rowNum++);
				VideoContentAcadsBean bean = manualRecordingUploadReportList.get(i);

				row.createCell(index++).setCellValue(rowNum - 1);
				row.createCell(index++).setCellValue(bean.getYear());
				row.createCell(index++).setCellValue(bean.getMonth());
				row.createCell(index++).setCellValue(bean.getSubject());
				row.createCell(index++).setCellValue(bean.getFileName());

				row.createCell(index++).setCellValue(bean.getFacultyId());
				row.createCell(index++).setCellValue(bean.getSessionId());
				row.createCell(index++).setCellValue(bean.getKeywords());
				row.createCell(index++).setCellValue(bean.getDescription());

				row.createCell(index++).setCellValue(bean.getMobileUrlHd());
				row.createCell(index++).setCellValue(bean.getMobileUrlSd1());
				row.createCell(index++).setCellValue(bean.getMobileUrlSd2());
			}
		} catch (RuntimeException e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}
	}
}