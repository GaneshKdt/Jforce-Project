package com.nmims.views;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;
import com.nmims.beans.RescheduledCancelledSlotChangeReportBean;

public class RescheduledCancelledSlotChangeReport extends AbstractXlsxStreamingView implements ApplicationContextAware {
	
	@SuppressWarnings("rawtypes")
	@Override
	protected void buildExcelDocument(Map model, Workbook workbook,	HttpServletRequest request, HttpServletResponse response)
		throws Exception {
		
		@SuppressWarnings("unchecked")
		List<RescheduledCancelledSlotChangeReportBean> list = (List<RescheduledCancelledSlotChangeReportBean>) model.get("sortedRLAndCLReport");

		//create a wordsheet :- START
		Sheet sheet = workbook.createSheet("Released Report");
		int index = 0;
		Row header = sheet.createRow(0);
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("Sap ID");
		header.createCell(index++).setCellValue("Subject");
		header.createCell(index++).setCellValue("Program");
		header.createCell(index++).setCellValue("Exam Date");
		header.createCell(index++).setCellValue("Exam Time");
		header.createCell(index++).setCellValue("Booking Status");
		header.createCell(index++).setCellValue("Lastest Cancelled / Slot Change Date");
		header.createCell(index++).setCellValue("Cancellation with/without refund");
		header.createCell(index++).setCellValue("LC");
		header.createCell(index++).setCellValue("IC");
		 
		int rowNum = 1;
		for (int i = 0 ; i < list.size(); i++) {
			//create the row data
			index = 0;
			Row row = sheet.createRow(rowNum++);
			RescheduledCancelledSlotChangeReportBean bean = list.get(i);
			row.createCell(index++).setCellValue(rowNum-1);
			row.createCell(index++).setCellValue(bean.getSapid());
			row.createCell(index++).setCellValue(bean.getSubject());
			row.createCell(index++).setCellValue(bean.getProgram());
			row.createCell(index++).setCellValue(bean.getExamDate());
			row.createCell(index++).setCellValue(bean.getExamTime());
			row.createCell(index++).setCellValue(bean.getTranStatus());
			row.createCell(index++).setCellValue(bean.getCreatedDate());
			row.createCell(index++).setCellValue(bean.getCancelStatus());
			row.createCell(index++).setCellValue(bean.getIc());
			row.createCell(index++).setCellValue(bean.getLc());
        }
		
		sheet.autoSizeColumn(0); sheet.autoSizeColumn(1);
		sheet.autoSizeColumn(2); sheet.autoSizeColumn(3);
		sheet.autoSizeColumn(4); sheet.autoSizeColumn(5);
	}
}
