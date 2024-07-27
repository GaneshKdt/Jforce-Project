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
import com.nmims.beans.BlockStudentExamCenterBean;

public class ExamCenterBlockReportExcelView extends AbstractXlsxStreamingView  implements ApplicationContextAware{
	
	@Override
	protected void buildExcelDocument(Map model, Workbook workbook,	HttpServletRequest request, HttpServletResponse response)
		throws Exception {
		
		List<BlockStudentExamCenterBean> centerNotAllowStudentsList = (List<BlockStudentExamCenterBean>) model.get("centerNotAllowStudentsList");

		Sheet sheet = workbook.createSheet("Reports");

		Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Sapid");

		int rowNum = 1;
		for (int i = 0 ; i < centerNotAllowStudentsList.size(); i++) {
			
			Row row = sheet.createRow(rowNum++);
			BlockStudentExamCenterBean bean = centerNotAllowStudentsList.get(i);
			
			row.createCell(0).setCellValue(new Double(bean.getSapid()));		
        }
	}

}
