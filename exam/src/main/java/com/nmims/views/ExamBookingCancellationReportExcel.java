package com.nmims.views;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;

import com.nmims.beans.ExamBookingCancelBean;

@Component
public class ExamBookingCancellationReportExcel extends AbstractXlsxStreamingView {

	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		int index = 0;
		List<ExamBookingCancelBean> canceledBookingList = (List<ExamBookingCancelBean>) model
				.get("canceledBookingList");

		Sheet sheet = workbook.createSheet("Students");

		Row header = sheet.createRow(0);
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("Exam Year");
		header.createCell(index++).setCellValue("Exam Month");
		header.createCell(index++).setCellValue("Subject");
		header.createCell(index++).setCellValue("Sap Id");
		header.createCell(index++).setCellValue("Release Reason");
		header.createCell(index++).setCellValue("Program");
		header.createCell(index++).setCellValue("Sem");
		header.createCell(index++).setCellValue("Amount Paid");
		header.createCell(index++).setCellValue("Seat Released Date/Time");
		header.createCell(index++).setCellValue("First Name");
		header.createCell(index++).setCellValue("Last Name");
		header.createCell(index++).setCellValue("Email");
		header.createCell(index++).setCellValue("Mobile");
		header.createCell(index++).setCellValue("Enrollment Year");
		header.createCell(index++).setCellValue("Enrollment Month");
		header.createCell(index++).setCellValue("IC");
		header.createCell(index++).setCellValue("LC");
		header.createCell(index++).setCellValue("Validity End Year");
		header.createCell(index++).setCellValue("Validity End Month");

		int rowNum = 1;

		for (int i = 0; i < canceledBookingList.size(); i++) {
			index = 0;
			// create the row data
			Row row = sheet.createRow(rowNum++);
			ExamBookingCancelBean bean = canceledBookingList.get(i);

			row.createCell(index++).setCellValue(rowNum - 1);
			row.createCell(index++).setCellValue(bean.getYear());
			row.createCell(index++).setCellValue(bean.getMonth());
			row.createCell(index++).setCellValue(bean.getSubject());
			row.createCell(index++).setCellValue(bean.getSapid());
			row.createCell(index++).setCellValue(bean.getReleaseReason());
			row.createCell(index++).setCellValue(bean.getProgram());
			row.createCell(index++).setCellValue(bean.getSem());
			row.createCell(index++).setCellValue(bean.getRespAmount());
			row.createCell(index++).setCellValue(bean.getLastModifiedDate());
			row.createCell(index++).setCellValue(bean.getFirstName());
			row.createCell(index++).setCellValue(bean.getLastName());
			row.createCell(index++).setCellValue(bean.getEmailId());
			row.createCell(index++).setCellValue(bean.getMobile());
			row.createCell(index++).setCellValue(bean.getEnrollmentYear());
			row.createCell(index++).setCellValue(bean.getEnrollmentMonth());
			row.createCell(index++).setCellValue(bean.getCenterName());
			row.createCell(index++).setCellValue(bean.getLc());
			row.createCell(index++).setCellValue(bean.getValidityEndYear());
			row.createCell(index++).setCellValue(bean.getValidityEndMonth());
			
			sheet.autoSizeColumn(0);
			sheet.autoSizeColumn(1);
			sheet.autoSizeColumn(2);
			sheet.autoSizeColumn(3);
			sheet.autoSizeColumn(4);
			sheet.autoSizeColumn(5);

		}

	}

}
