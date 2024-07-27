package com.nmims.views;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;

import com.nmims.beans.CenterAcadsBean;
import com.nmims.beans.DummyUserBean;
import com.nmims.beans.StudentAcadsBean;
import com.nmims.daos.PCPBookingDAO;


public class DummyUsersExcelView  extends AbstractXlsxStreamingView  implements ApplicationContextAware{
	private static ApplicationContext act = null;
	
	@Override
	protected void buildExcelDocument(Map model, Workbook workbook,	HttpServletRequest request, HttpServletResponse response)
		throws Exception {
		
		act = getApplicationContext();
		PCPBookingDAO dao = (PCPBookingDAO)act.getBean("pcpBookingDAO");
		HashMap<String, StudentAcadsBean> sapIdStudentsMap = dao.getAllStudents();
		HashMap<String, CenterAcadsBean> icLcMap = dao.getICLCMap();

		List<DummyUserBean> dummyUsersList = (List<DummyUserBean>)model.get("dummyUsersList");
		//create a wordsheet
		Sheet sheet = workbook.createSheet("LoginAsForm Users"); 
 
		Row header = sheet.createRow(0);
		header.createCell(0).setCellValue("Sr. No.");
		header.createCell(1).setCellValue("SAP ID");
		header.createCell(2).setCellValue("Consumer Type");
		header.createCell(3).setCellValue("Program Type");
		header.createCell(4).setCellValue("Program Structure");
		header.createCell(5).setCellValue("Program");
		
 
		int rowNum = 1;
		for (int i = 0 ; i < dummyUsersList.size(); i++) {
			//create the row data
			Row row = sheet.createRow(rowNum++);
			DummyUserBean bean = dummyUsersList.get(i);
			
			StudentAcadsBean student = sapIdStudentsMap.get(bean.getUserId());
			
//			String ic = "";
//			String lc = "";
//			String examMode = "Offline";
//			if(bean != null){
//				ic = student.getCenterName();
//				CenterBean center = icLcMap.get(student.getCenterCode()); 
//				if(center != null){
//					lc = center.getLc();
//				}
//				
//				if("Online".equals(student.getExamMode())){
//					examMode = "Online";
//				}
//			}
//			String isAppDownloaded = "";
//			
//			if(StringUtils.isBlank(bean.getFirebaseToken()) && StringUtils.isBlank(bean.getOnesignalId())){
//				isAppDownloaded = "No";
//			}else {
//				isAppDownloaded = "Yes";
//			}
			
			
			row.createCell(0).setCellValue(rowNum-1);
			row.createCell(1).setCellValue(bean.getUserId());
			row.createCell(2).setCellValue(bean.getConsumerType());
			row.createCell(3).setCellValue(bean.getProgramType());
			row.createCell(4).setCellValue(bean.getProgramStructure());
			row.createCell(5).setCellValue(bean.getProgram());
        }
	}

}
