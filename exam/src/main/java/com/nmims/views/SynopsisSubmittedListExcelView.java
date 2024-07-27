package com.nmims.views;

import java.util.ArrayList;
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
import com.nmims.beans.UploadProjectSynopsisBean;

@Component("SynopsisSubmittedListExcelView")
public class SynopsisSubmittedListExcelView extends AbstractExcelView implements ApplicationContextAware{
	
	@Override
	protected void buildExcelDocument(Map model, HSSFWorkbook workbook,	HttpServletRequest request, HttpServletResponse response)
		throws Exception {
		
		List<UploadProjectSynopsisBean> SynopsisSubmissionList = (List<UploadProjectSynopsisBean>) model.get("SynopsisSubmissionList");
		List<UploadProjectSynopsisBean> SynopsisEvaluatedScoreList = (List<UploadProjectSynopsisBean>) model.get("SynopsisEvaluatedScoreList");
		List<UploadProjectSynopsisBean> SynopsisList = new ArrayList<UploadProjectSynopsisBean>();
		
		HSSFSheet sheet;
		try {
			if(SynopsisSubmissionList == null || SynopsisSubmissionList.size() == 0) {
				SynopsisList.addAll(SynopsisEvaluatedScoreList);
				sheet = workbook.createSheet("Synopsis Evaluated Score List");
			}else {
				SynopsisList.addAll(SynopsisSubmissionList);
				sheet = workbook.createSheet("Synopsis Submitted List");
			}
			//create a wordsheet
			//HSSFSheet sheet = workbook.createSheet("Synopsis Submitted List");
			int index = 0;
			HSSFRow header = sheet.createRow(0);
			header.createCell(index++).setCellValue("Sr. No.");
			header.createCell(index++).setCellValue("Submission ID");
			header.createCell(index++).setCellValue("Year");
			header.createCell(index++).setCellValue("Month");
			header.createCell(index++).setCellValue("Subject");
			header.createCell(index++).setCellValue("Sap ID");
			header.createCell(index++).setCellValue("File Path");
			header.createCell(index++).setCellValue("Preview Path");
			header.createCell(index++).setCellValue("Faculty ID");
			header.createCell(index++).setCellValue("Status");
			header.createCell(index++).setCellValue("Payment Status");
			header.createCell(index++).setCellValue("Reason");
			header.createCell(index++).setCellValue("Track ID");
			header.createCell(index++).setCellValue("Attempt");
			header.createCell(index++).setCellValue("Score");
			header.createCell(index++).setCellValue("Evaluation Date");
			header.createCell(index++).setCellValue("Evaluated");
			header.createCell(index++).setCellValue("Evaluation Count");
			header.createCell(index++).setCellValue("Created By");
			header.createCell(index++).setCellValue("Updated By");
			header.createCell(index++).setCellValue("Created At");
			header.createCell(index++).setCellValue("Updated At");
			
			int rowNum = 1;
			for (int i = 0 ; i < SynopsisList.size(); i++) {
				index = 0;
				//create the row data

	         	
	         	try {
				HSSFRow row = sheet.createRow(rowNum++);
				UploadProjectSynopsisBean bean = SynopsisList.get(i);
				row.createCell(index++).setCellValue((i+1));
				row.createCell(index++).setCellValue(bean.getId());
				row.createCell(index++).setCellValue(bean.getYear());
				row.createCell(index++).setCellValue(bean.getMonth());
				row.createCell(index++).setCellValue(bean.getSubject());
				row.createCell(index++).setCellValue(bean.getSapid());
				row.createCell(index++).setCellValue(bean.getFilePath());
				row.createCell(index++).setCellValue(bean.getPreviewPath());
				row.createCell(index++).setCellValue(bean.getFacultyId());
				row.createCell(index++).setCellValue(bean.getStatus());
				row.createCell(index++).setCellValue(bean.getPayment_status());
				row.createCell(index++).setCellValue(bean.getReason());
				row.createCell(index++).setCellValue(bean.getTrack_id());
				row.createCell(index++).setCellValue(bean.getAttempt());
				row.createCell(index++).setCellValue(bean.getScore());
				row.createCell(index++).setCellValue(bean.getEvaluationDate());
				row.createCell(index++).setCellValue(bean.getEvaluated());
				row.createCell(index++).setCellValue(bean.getEvaluationCount());
				row.createCell(index++).setCellValue(bean.getCreated_by());
				row.createCell(index++).setCellValue(bean.getUpdated_by());
				row.createCell(index++).setCellValue(bean.getCreated_at());
				row.createCell(index++).setCellValue(bean.getUpdated_at());
	         	} catch (Exception e) {
	         		
				}
	        }
			sheet.autoSizeColumn(0); sheet.autoSizeColumn(1);
			sheet.autoSizeColumn(2); sheet.autoSizeColumn(3);
			sheet.autoSizeColumn(4); sheet.autoSizeColumn(5);
			sheet.autoSizeColumn(6); sheet.autoSizeColumn(7);
			sheet.autoSizeColumn(8); sheet.autoSizeColumn(9);
			sheet.autoSizeColumn(10);
			sheet.setAutoFilter(CellRangeAddress.valueOf("A1:R"+SynopsisList.size()));
		
		}catch (Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error! Failed to generate report.");
		}
		
		//Synopsis transaction list sheet
		List<PaymentGatewayTransactionBean> SynopsisTransactionList = (List<PaymentGatewayTransactionBean>) model.get("SynopsisTransactionList");
		if(SynopsisTransactionList == null || SynopsisTransactionList.size() == 0) {
		}else {
		HSSFSheet sheet2 = workbook.createSheet("Synopsis Transaction List");
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
		for (int i = 0 ; i < SynopsisTransactionList.size(); i++) {
			index2 = 0;
			//create the row data
			try {
			HSSFRow row2 = sheet2.createRow(rowNum2++);
			PaymentGatewayTransactionBean bean = SynopsisTransactionList.get(i);
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

}
