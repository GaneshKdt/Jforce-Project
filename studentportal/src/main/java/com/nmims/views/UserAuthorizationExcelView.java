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

 import com.nmims.beans.UserAuthorizationStudentPortalBean;
 
 @Component("userAuthorizationExcelView")
public class UserAuthorizationExcelView extends AbstractExcelView implements ApplicationContextAware {

	@Override
	protected void buildExcelDocument(Map<String, Object> model, HSSFWorkbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		List<UserAuthorizationStudentPortalBean> userAuthorizationList = (List<UserAuthorizationStudentPortalBean>) model.get("userAuthorizationList");

 		HSSFSheet sheet = workbook.createSheet("User Authorization");
		int index = 0;
		HSSFRow header = sheet.createRow(0);
		header.createCell(index++).setCellValue("Sr No.");
		header.createCell(index++).setCellValue("User Id");
		header.createCell(index++).setCellValue("Roles");
		
		int rowNum = 1;
		for (int i = 0 ; i < userAuthorizationList.size(); i++) {
			index = 0;
			//create the row data
         	
			try {
				HSSFRow row = sheet.createRow(rowNum++);
				UserAuthorizationStudentPortalBean bean = userAuthorizationList.get(i);

				row.createCell(index++).setCellValue((i + 1));
				row.createCell(index++).setCellValue((bean.getUserId()));
				row.createCell(index++).setCellValue(bean.getRoles());

			} catch (Exception e) {
				// e.printStackTrace();
			}
			sheet.autoSizeColumn(0);
			sheet.autoSizeColumn(1);
			sheet.autoSizeColumn(2);

			sheet.setAutoFilter(CellRangeAddress.valueOf("A1:D" + userAuthorizationList.size()));
        }
		
	}

}
