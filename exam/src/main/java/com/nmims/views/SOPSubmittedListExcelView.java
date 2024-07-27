package com.nmims.views;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractExcelView;

import com.nmims.beans.PaymentGatewayTransactionBean;
import com.nmims.beans.UploadProjectSOPBean;

@Component("SOPSubmittedListExcelView")
public class SOPSubmittedListExcelView extends AbstractExcelView implements ApplicationContextAware{
	
	@Override
		protected void buildExcelDocument(Map model, HSSFWorkbook workbook,	HttpServletRequest request, HttpServletResponse response)
			throws Exception {
			
			List<UploadProjectSOPBean> SOPSubmissionList = (List<UploadProjectSOPBean>) model.get("SOPSubmissionList");
			//SOP transaction list sheet
			HSSFSheet sheet = workbook.createSheet("SOP Submitted List");
			int index = 0;
			HSSFRow header = sheet.createRow(0);
			header.createCell(index++).setCellValue("Sr. No.");
			header.createCell(index++).setCellValue("Year");
			header.createCell(index++).setCellValue("Month");
			header.createCell(index++).setCellValue("Sap ID");
			header.createCell(index++).setCellValue("File Path");
			header.createCell(index++).setCellValue("Preview Path");
			header.createCell(index++).setCellValue("Faculty ID");
			header.createCell(index++).setCellValue("Status");
			header.createCell(index++).setCellValue("Payment Status");
			header.createCell(index++).setCellValue("Reason");
			header.createCell(index++).setCellValue("Track ID");
			header.createCell(index++).setCellValue("Attempt");
			header.createCell(index++).setCellValue("Created By");
			header.createCell(index++).setCellValue("Updated By");
			
			int rowNum = 1;
			for (int i = 0 ; i < SOPSubmissionList.size(); i++) {
				index = 0;
				//create the row data
				try {
				HSSFRow row = sheet.createRow(rowNum++);
				UploadProjectSOPBean bean = SOPSubmissionList.get(i);
				row.createCell(index++).setCellValue((i+1));
				row.createCell(index++).setCellValue(bean.getYear());
				row.createCell(index++).setCellValue(bean.getMonth());
				row.createCell(index++).setCellValue(bean.getSapId());
				row.createCell(index++).setCellValue(bean.getFilePath());
				row.createCell(index++).setCellValue(bean.getPreviewPath());
				row.createCell(index++).setCellValue(bean.getFacultyId());
				row.createCell(index++).setCellValue(bean.getStatus());
				row.createCell(index++).setCellValue(bean.getPayment_status());
				row.createCell(index++).setCellValue(bean.getReason());
				row.createCell(index++).setCellValue(bean.getTrack_id());
				row.createCell(index++).setCellValue(bean.getAttempt());
				row.createCell(index++).setCellValue(bean.getCreated_by());
				row.createCell(index++).setCellValue(bean.getUpdated_by());
				} catch (Exception e) {
	         		
				}
	        }
			sheet.autoSizeColumn(0); sheet.autoSizeColumn(1);
			sheet.autoSizeColumn(2); sheet.autoSizeColumn(3);
			sheet.autoSizeColumn(4); sheet.autoSizeColumn(5);
			sheet.autoSizeColumn(6); sheet.autoSizeColumn(7);
			sheet.autoSizeColumn(8); sheet.autoSizeColumn(9);
			sheet.autoSizeColumn(10);
			sheet.setAutoFilter(CellRangeAddress.valueOf("A1:R"+SOPSubmissionList.size()));
			
			//SOP transaction list sheet
			List<PaymentGatewayTransactionBean> SOPTransactionList = (List<PaymentGatewayTransactionBean>) model.get("SOPTransactionList");
			HSSFSheet sheet2 = workbook.createSheet("SOP Transaction List");
			int index2 = 0;
			HSSFRow header2 = sheet2.createRow(0);
			header2.createCell(index2++).setCellValue("Sr. No.");
			header2.createCell(index2++).setCellValue("Track ID");
			header2.createCell(index2++).setCellValue("Sap ID");
			header2.createCell(index2++).setCellValue("Amount");
			header2.createCell(index2++).setCellValue("Transaction Status");
			header2.createCell(index2++).setCellValue("Transaction ID");
			header2.createCell(index2++).setCellValue("Response Payment Method");
			header2.createCell(index2++).setCellValue("Payment Option");
			header2.createCell(index2++).setCellValue("Bank Name");
			header2.createCell(index2++).setCellValue("Created At");
			
			int rowNum2 = 1;
			for (int i = 0 ; i < SOPTransactionList.size(); i++) {
				index2 = 0;
				//create the row data
				try {
				HSSFRow row2 = sheet2.createRow(rowNum2++);
				PaymentGatewayTransactionBean bean = SOPTransactionList.get(i);
				row2.createCell(index2++).setCellValue((i+1));
				row2.createCell(index2++).setCellValue(bean.getTrack_id());
				row2.createCell(index2++).setCellValue(bean.getSapid());
				row2.createCell(index2++).setCellValue(bean.getAmount());
				row2.createCell(index2++).setCellValue(bean.getTransaction_status());
				row2.createCell(index2++).setCellValue(bean.getTrack_id());
				row2.createCell(index2++).setCellValue(bean.getResponse_payment_method());
				row2.createCell(index2++).setCellValue(bean.getPayment_option());
				row2.createCell(index2++).setCellValue(bean.getBank_name());
				row2.createCell(index2++).setCellValue(bean.getCreated_at());
				} catch (Exception e) {
	         		
				}
	        }
		}

}
