/* Added to show a report of payments which were recieved twice from student
 * package com.nmims.views;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.view.document.AbstractExcelView;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;

import com.nmims.beans.AssignmentPaymentBean;
import com.nmims.beans.CenterBean;
import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.beans.StudentBean;

public class AssignmentDoublePaymentReportExcelView  extends AbstractXlsxStreamingView  implements ApplicationContextAware{
	
	@Override
	protected void buildExcelDocument(Map model, Workbook workbook,	HttpServletRequest request, HttpServletResponse response) throws Exception {
		
	List<AssignmentPaymentBean> assignmentPaymentList = (List<AssignmentPaymentBean>) model.get("assignmentPaymentList");
	Sheet sheet = workbook.createSheet("Assignment Payment");
	int index = 0;
	Row header = sheet.createRow(0);
	header.createCell(index++).setCellValue("Sr. No.");
	header.createCell(index++).setCellValue("Year");
    header.createCell(index++).setCellValue("Month");
	header.createCell(index++).setCellValue("SAP ID");
	header.createCell(index++).setCellValue("First Name");
	header.createCell(index++).setCellValue("Last Name");
	header.createCell(index++).setCellValue("Subject");
	header.createCell(index++).setCellValue("Booked");
	header.createCell(index++).setCellValue("TrackID");
	header.createCell(index++).setCellValue("Amount");
	header.createCell(index++).setCellValue("Transaction Date-Time");
	header.createCell(index++).setCellValue("Transaction Status");
	header.createCell(index++).setCellValue("Program");
	header.createCell(index++).setCellValue("Semester");
	header.createCell(index++).setCellValue("Transaction ID");
    header.createCell(index++).setCellValue("Request ID");
    header.createCell(index++).setCellValue("Merchant Reference Number");
    header.createCell(index++).setCellValue("Secure Hash");
    header.createCell(index++).setCellValue("Resp Amount");
    header.createCell(index++).setCellValue("Description");
    header.createCell(index++).setCellValue("Response Code");
    header.createCell(index++).setCellValue("Res Payment Method");
    header.createCell(index++).setCellValue("Is Flagged");
    header.createCell(index++).setCellValue("Payment ID");
    header.createCell(index++).setCellValue("Response Message");
    header.createCell(index++).setCellValue("Error");
    header.createCell(index++).setCellValue("Booking Completion Time");
    int rowNum = 1;
	for (int i = 0 ; i < assignmentPaymentList.size(); i++) {
		index = 0;
		//create the row data
		Row row = sheet.createRow(rowNum++);
		AssignmentPaymentBean bean = assignmentPaymentList.get(i);
		
		row.createCell(index++).setCellValue((i+1));
		row.createCell(index++).setCellValue(bean.getYear());
		row.createCell(index++).setCellValue(bean.getMonth());
		row.createCell(index++).setCellValue(bean.getSapid());
		row.createCell(index++).setCellValue(bean.getFirstName());
		row.createCell(index++).setCellValue(bean.getLastName());
		row.createCell(index++).setCellValue(bean.getSubject());
		row.createCell(index++).setCellValue(bean.getBooked());
		row.createCell(index++).setCellValue(bean.getTrackId());
		row.createCell(index++).setCellValue(bean.getAmount());
		row.createCell(index++).setCellValue(bean.getTranDateTime());
		row.createCell(index++).setCellValue(bean.getTranStatus());
		row.createCell(index++).setCellValue(bean.getProgram());
		row.createCell(index++).setCellValue(bean.getSem());
		row.createCell(index++).setCellValue(bean.getTransactionID());
		row.createCell(index++).setCellValue(bean.getRequestID());
		row.createCell(index++).setCellValue(bean.getMerchantRefNo());
		row.createCell(index++).setCellValue(bean.getSecureHash());
		row.createCell(index++).setCellValue(bean.getRespAmount());
		row.createCell(index++).setCellValue(bean.getDescription());
		row.createCell(index++).setCellValue(bean.getResponseCode());
		row.createCell(index++).setCellValue(bean.getRespPaymentMethod());
		row.createCell(index++).setCellValue(bean.getIsFlagged());
		row.createCell(index++).setCellValue(bean.getPaymentID());
		row.createCell(index++).setCellValue(bean.getResponseMessage());
		row.createCell(index++).setCellValue(bean.getError());
		row.createCell(index++).setCellValue(bean.getBookingCompleteTime());
		}

		
	}

}
*/