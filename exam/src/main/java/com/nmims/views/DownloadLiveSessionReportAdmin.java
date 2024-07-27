/**
 * 
 */
package com.nmims.views;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;

import com.nmims.beans.LiveSessionReportAdminDTO;

/**
 * @author vil_m
 *
 */
public class DownloadLiveSessionReportAdmin extends AbstractXlsxStreamingView implements ApplicationContextAware {
	
	public static final String COL_SRNO = "Sr. No.";
	public static final String COL_CENTER_NAME = "Center Name";
	public static final String COL_CONSUMER = "Consumer";
	public static final String COL_PROGRAM_STRUCTURE = "Program Structure";
	public static final String COL_PROGRAM = "Program";
	public static final String COL_ACAD_YEAR = "Acad Year";
	public static final String COL_ACAD_MONTH = "Acad Month";
	public static final String COL_SAPID = "SapId";
	public static final String COL_NAME = "Name";
	public static final String COL_SEM = "Sem";
	public static final String COL_SUBJECT = "Subject";
	public static final String COL_RL = "Recorded/Live";
	public static final String COL_EMAIL = "Email";
	public static final String COL_PHONE = "Phone";
	
	@Override
	protected void buildExcelDocument(Map<String, Object> arg0, Workbook arg1, HttpServletRequest arg2,
			HttpServletResponse arg3) throws Exception {
		// TODO Auto-generated method stub
		Row row = null;
		Row header = null;
		int index = 0;
		
		List<LiveSessionReportAdminDTO> list1 = (ArrayList<LiveSessionReportAdminDTO>) arg0.get("datalist");


		Sheet sheet = arg1.createSheet("DownloadLiveSessionReportAdmin Excel");
		
		header = sheet.createRow(0);
		
		header.createCell(index++).setCellValue(DownloadLiveSessionReportAdmin.COL_SRNO);
		header.createCell(index++).setCellValue(DownloadLiveSessionReportAdmin.COL_CENTER_NAME);
		header.createCell(index++).setCellValue(DownloadLiveSessionReportAdmin.COL_CONSUMER);
		header.createCell(index++).setCellValue(DownloadLiveSessionReportAdmin.COL_PROGRAM_STRUCTURE);
		header.createCell(index++).setCellValue(DownloadLiveSessionReportAdmin.COL_PROGRAM);
		header.createCell(index++).setCellValue(DownloadLiveSessionReportAdmin.COL_ACAD_YEAR);
		header.createCell(index++).setCellValue(DownloadLiveSessionReportAdmin.COL_ACAD_MONTH);
		header.createCell(index++).setCellValue(DownloadLiveSessionReportAdmin.COL_SAPID);
		header.createCell(index++).setCellValue(DownloadLiveSessionReportAdmin.COL_NAME);
		header.createCell(index++).setCellValue(DownloadLiveSessionReportAdmin.COL_SEM);
		header.createCell(index++).setCellValue(DownloadLiveSessionReportAdmin.COL_SUBJECT);
		header.createCell(index++).setCellValue(DownloadLiveSessionReportAdmin.COL_RL);
		header.createCell(index++).setCellValue(DownloadLiveSessionReportAdmin.COL_EMAIL);
		header.createCell(index++).setCellValue(DownloadLiveSessionReportAdmin.COL_PHONE);
		
		int rowNum = 1;
		for (int j = 0; j < list1.size(); j++) {
			index = 0;
			row = sheet.createRow(rowNum++);
			row.createCell(index++).setCellValue(j + 1);
			row.createCell(index++).setCellValue(list1.get(j).getCenterName());
			row.createCell(index++).setCellValue(list1.get(j).getConsumerType());
			row.createCell(index++).setCellValue(list1.get(j).getProgramStructure());
			row.createCell(index++).setCellValue(list1.get(j).getProgram());
			row.createCell(index++).setCellValue(list1.get(j).getAcadYear());
			row.createCell(index++).setCellValue(list1.get(j).getAcadMonth());
			row.createCell(index++).setCellValue(list1.get(j).getSapId());
			row.createCell(index++).setCellValue(list1.get(j).getStudentName());
			row.createCell(index++).setCellValue(list1.get(j).getSem());
			row.createCell(index++).setCellValue(list1.get(j).getSubjectName());
			row.createCell(index++).setCellValue(list1.get(j).getSessionType());
			row.createCell(index++).setCellValue(list1.get(j).getEmailId());
			row.createCell(index++).setCellValue(list1.get(j).getPhone());
		}
	}
}
