package com.nmims.views;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;

import com.nmims.beans.CenterExamBean;
import com.nmims.beans.DemoExamBean;

import com.nmims.daos.AssignmentsDAO;

@Component("demoExamReportExcelView")
public class DemoExamReportExcelView extends AbstractXlsxStreamingView  implements ApplicationContextAware{
	@Autowired
	private AssignmentsDAO asignmentsDAO;
	
	@Override
	protected void buildExcelDocument(Map model, Workbook workbook,	HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<DemoExamBean> demoExamBeanList =  (List<DemoExamBean>)model.get("listOfDemoExamAttendaceReport"); 
		HashMap<String,CenterExamBean> getICLCMap = asignmentsDAO.getICLCMap();
		
		String userId=(String) request.getSession().getAttribute("userId");
		List<String> ICLCRESTRICTED_USER_LIST=(List<String>) request.getSession().getAttribute("ICLCRESTRICTED_USER_LIST");
		
		String reportName = "Not attended student list";
		if(demoExamBeanList.get(0).getTotal() != null) {
			reportName = "Attended student list";
		}
		Sheet sheet = workbook.createSheet(reportName);
		int index = 0;
		
		Row header = sheet.createRow(0);
		header.createCell(index++).setCellValue("sapid");
		header.createCell(index++).setCellValue("first name");
		header.createCell(index++).setCellValue("last name");
		header.createCell(index++).setCellValue("email");
		header.createCell(index++).setCellValue("mobile");
		
		if(demoExamBeanList.get(0).getCount() != null) {
			header.createCell(index++).setCellValue("count");
		}
		if(!ICLCRESTRICTED_USER_LIST.contains(userId)) {
		header.createCell(index++).setCellValue("IC");
		header.createCell(index++).setCellValue("LC");
		}
		header.createCell(index++).setCellValue(" attemptStatus");
		header.createCell(index++).setCellValue("latestAttemptDateTime");
		
		
		int rowNum = 1;
		for(int j=0;j < demoExamBeanList.size();j++){
			index = 0;
			DemoExamBean demoExamBean = demoExamBeanList.get(j);
			
			String ic = "";
			String lc = "";
			if(demoExamBean != null){
				CenterExamBean center = getICLCMap.get(demoExamBean.getCenterCode()); 
				if(center != null){
					lc = center.getLc();
					ic = center.getCenterName();
				}//inner if
			}//outer if
			try {
			Row row = sheet.createRow(rowNum++);
			row.createCell(index++).setCellValue(demoExamBean.getSapid());
			row.createCell(index++).setCellValue(demoExamBean.getFirstName());
			row.createCell(index++).setCellValue(demoExamBean.getLastName());
			row.createCell(index++).setCellValue(demoExamBean.getEmailId());
			row.createCell(index++).setCellValue(demoExamBean.getMobile());
				
			
			if(demoExamBeanList.get(0).getCount() != null) {
 				row.createCell(index++).setCellValue(demoExamBean.getCount());
 			}
				else {
					row.createCell(index++).setCellValue(0);
				}
			if(!ICLCRESTRICTED_USER_LIST.contains(userId)) {
			row.createCell(index++).setCellValue(ic);
			row.createCell(index++).setCellValue(lc);
			}
			row.createCell(index++).setCellValue(demoExamBean.getAttemptStatus());
			row.createCell(index++).setCellValue(demoExamBean.getLatestAttemptDateTime());
			

		
			}catch (Exception e) {
				
				
				// TODO: handle exception
			}
		}
	
}
}