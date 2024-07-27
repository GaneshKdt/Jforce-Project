package com.nmims.views;

import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.view.document.AbstractExcelView;

import com.nmims.beans.UFMIncidentBean;
import com.nmims.beans.UFMNoticeBean;


@Component
public class UFMIncidentDetailsReport extends AbstractExcelView implements ApplicationContextAware {

	@Override
	protected void buildExcelDocument(Map<String, Object> model, HSSFWorkbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
	try {
				
				ArrayList<UFMNoticeBean> reportData =(ArrayList<UFMNoticeBean>)request.getSession().getAttribute("UFMincidentReport");
				HSSFSheet sheet = workbook.createSheet("LockedBadgeReport");
				
				int index = 0;
				HSSFRow header = sheet.createRow(0);
				header.createCell(index++).setCellValue("Sr. No.");
				header.createCell(index++).setCellValue("sapid");
				header.createCell(index++).setCellValue("Subject");
				header.createCell(index++).setCellValue("Exam Year");
				header.createCell(index++).setCellValue("Exam Month");
				header.createCell(index++).setCellValue("Incidents");
				int rowNum = 1;
				for (UFMNoticeBean bean : reportData ) {
					index = 0;
						HSSFRow row = sheet.createRow(rowNum++);
						  row.createCell(index++).setCellValue(rowNum-1);
						  row.createCell(index++).setCellValue(bean.getSapid());
						  row.createCell(index++).setCellValue(bean.getSubject());
						  row.createCell(index++).setCellValue(bean.getYear());
						  row.createCell(index++).setCellValue(bean.getMonth());
						  if(CollectionUtils.isEmpty(bean.getIncidentBean())|| bean.getIncidentBean()== null) {
							  row.createCell(index++).setCellValue("");
							  
						  }
						  else {
						  for(UFMIncidentBean incidentBean : bean.getIncidentBean()){
								 row.createCell(index++).setCellValue("Incident : "+incidentBean.getIncident() + "   TimeStamp : "+ incidentBean.getTime_Stamp() + "  VideoNumber : "+incidentBean.getVideo_Number());
						  }
				}
				
			}
	}catch (Exception e) {
					
					e.printStackTrace();
				}
			
			
		}

}


