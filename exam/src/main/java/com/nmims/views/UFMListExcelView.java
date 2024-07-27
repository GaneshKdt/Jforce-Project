package com.nmims.views;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;



import com.nmims.beans.UFMNoticeBean;

public class UFMListExcelView   extends AbstractXlsxStreamingView  implements ApplicationContextAware {
	private static ApplicationContext act = null;
	
	@SuppressWarnings("rawtypes")
	@Override
	protected void buildExcelDocument(Map model, Workbook workbook,	HttpServletRequest request, HttpServletResponse response)
		throws Exception {
		
		@SuppressWarnings("unchecked")
		List<UFMNoticeBean> list = (List<UFMNoticeBean>) model.get("UFMRecords");
		
		act = getApplicationContext();
		
		//create a wordsheet :- START
		Sheet sheet = workbook.createSheet("Report Of Executive Exam Bookings List");
		
		int index = 0;
		Row header = sheet.createRow(0);
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("SAP ID");
		header.createCell(index++).setCellValue("First Name");
		header.createCell(index++).setCellValue("Last Name");
		header.createCell(index++).setCellValue("Stage");
		header.createCell(index++).setCellValue("Subject");
		header.createCell(index++).setCellValue("Exam Date");
		header.createCell(index++).setCellValue("Exam Time");
		header.createCell(index++).setCellValue("Year");
		header.createCell(index++).setCellValue("Month");

		header.createCell(index++).setCellValue("LC Name");
		//Added by shivam.pandey.EXT - START
		header.createCell(index++).setCellValue("IC Name");
		//Added by shivam.pandey.EXT - START
		header.createCell(index++).setCellValue("Category");
		//Added by shivam.pandey.EXT - END
		header.createCell(index++).setCellValue("UFM Reason");
		header.createCell(index++).setCellValue("Show Cause Generation Date");
		header.createCell(index++).setCellValue("Show Cause Notice URL");
		header.createCell(index++).setCellValue("Show Cause Deadline");
		header.createCell(index++).setCellValue("Show Cause Response");
		header.createCell(index++).setCellValue("Show Cause Submission Date");
		header.createCell(index++).setCellValue("Decision Notice Url");
		 
		int rowNum = 1;
		for (int i = 0 ; i < list.size(); i++) {
			//create the row data
			index = 0;
			Row row = sheet.createRow(rowNum++);
			UFMNoticeBean bean = list.get(i);
			row.createCell(index++).setCellValue(rowNum-1);
			row.createCell(index++).setCellValue(bean.getSapid());
			row.createCell(index++).setCellValue(bean.getFirstName());
			row.createCell(index++).setCellValue(bean.getLastName());
			row.createCell(index++).setCellValue(bean.getStage());
			row.createCell(index++).setCellValue(bean.getSubject());
			row.createCell(index++).setCellValue(bean.getExamDate());
			row.createCell(index++).setCellValue(bean.getExamTime());
			
			row.createCell(index++).setCellValue(bean.getYear());
			row.createCell(index++).setCellValue(bean.getMonth());
			
			row.createCell(index++).setCellValue(bean.getLcName());
			//Added by shivam.pandey.EXT - START
			row.createCell(index++).setCellValue(bean.getIcName());
			//Added by shivam.pandey.EXT - START
			row.createCell(index++).setCellValue(bean.getCategory());
			//Added by shivam.pandey.EXT - END
			row.createCell(index++).setCellValue(bean.getUfmMarkReason());
			row.createCell(index++).setCellValue(bean.getShowCauseGenerationDate());
			
			String noticeUrl = bean.getShowCauseNoticeURL();
			row.createCell(index++).setCellValue(StringUtils.isBlank(noticeUrl) ? "NA" : noticeUrl);

			String showCauseDeadline = bean.getShowCauseDeadline();
			row.createCell(index++).setCellValue(StringUtils.isBlank(showCauseDeadline) ? "NA" : showCauseDeadline);
			

			String showCauseResponse = bean.getShowCauseResponse();
			row.createCell(index++).setCellValue(StringUtils.isBlank(showCauseResponse) ? "NA" : showCauseResponse);
			
			String showCauseSubmissionDate = bean.getShowCauseSubmissionDate();
			row.createCell(index++).setCellValue(StringUtils.isBlank(showCauseSubmissionDate) ? "NA" : showCauseSubmissionDate);

			String decisionUrl = bean.getDecisionNoticeURL();
			row.createCell(index++).setCellValue(StringUtils.isBlank(decisionUrl) ? "NA" : decisionUrl);
        }
		
		sheet.autoSizeColumn(0); sheet.autoSizeColumn(1);
		sheet.autoSizeColumn(2); sheet.autoSizeColumn(3);
		sheet.autoSizeColumn(4); sheet.autoSizeColumn(5);
		
	
	}
}
