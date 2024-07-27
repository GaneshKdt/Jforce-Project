package com.nmims.views;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.view.document.AbstractExcelView;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;

import com.nmims.beans.CenterAcadsBean;
import com.nmims.beans.FeedPostsBean;
import com.nmims.beans.StudentAcadsBean;
import com.nmims.beans.TestAcadsBean;


public class FeedPostsExcelView  extends AbstractXlsxStreamingView  implements ApplicationContextAware {
	
	@Override
	protected void buildExcelDocument(Map model, Workbook workbook,	HttpServletRequest request, HttpServletResponse response)
		throws Exception {
		
		List<FeedPostsBean> feedPostsList = (List<FeedPostsBean>) model.get("feedPostsList");
		List<FeedPostsBean> feedCommentsList = (List<FeedPostsBean>) request.getSession().getAttribute("feedCommentsListForExcel");

		
		//create a wordsheet :- START
		Sheet sheet = workbook.createSheet("Report Of Feed Posts");		
		
		int index = 0;
		Row header = sheet.createRow(0);
		
		header.createCell(index++).setCellValue("Acad Year");
		header.createCell(index++).setCellValue("Acad Month");
		header.createCell(index++).setCellValue("Batch Name");
		header.createCell(index++).setCellValue("Subject");
		header.createCell(index++).setCellValue("Term");
		header.createCell(index++).setCellValue("Post URL");
		header.createCell(index++).setCellValue("Post Description");
		header.createCell(index++).setCellValue("No. of Comments");
		header.createCell(index++).setCellValue("Faculty Name");
		header.createCell(index++).setCellValue("Posted On");
		header.createCell(index++).setCellValue("Post Type");

		int rowNum = 1;
		for (int i = 0 ; i < feedPostsList.size(); i++) {
			//create the row data
			index = 0;
			Row row = sheet.createRow(rowNum++);
			FeedPostsBean bean = feedPostsList.get(i);
			

			row.createCell(index++).setCellValue(bean.getAcadYear());
			row.createCell(index++).setCellValue(bean.getAcadMonth());
			row.createCell(index++).setCellValue(bean.getBatch());
			row.createCell(index++).setCellValue(bean.getSubject());
			row.createCell(index++).setCellValue(bean.getTerm());
			row.createCell(index++).setCellValue(bean.getPostURL());
			row.createCell(index++).setCellValue(bean.getPostDescription());
			row.createCell(index++).setCellValue(bean.getNoOfComments());
			row.createCell(index++).setCellValue(bean.getFacultyName());
			row.createCell(index++).setCellValue(bean.getPostedOn());				
			row.createCell(index++).setCellValue(bean.getPostType());
		}
		
	
		Sheet sheet2 = workbook.createSheet("Report Of Feed Comments");
		
		int index2 = 0;
		Row header2 = sheet2.createRow(0);
		    
			header2.createCell(index2++).setCellValue("Acad Year");
			header2.createCell(index2++).setCellValue("Acad Month");
			header2.createCell(index2++).setCellValue("Batch Name");
			header2.createCell(index2++).setCellValue("Subject");
			header2.createCell(index2++).setCellValue("Term");
			header2.createCell(index2++).setCellValue("Post URL");
			header2.createCell(index2++).setCellValue("Post Description");		
			header2.createCell(index2++).setCellValue("Faculty Name");
			header2.createCell(index2++).setCellValue("Post Posted On");
			header2.createCell(index2++).setCellValue("Sapid");
			header2.createCell(index2++).setCellValue("Comment");
			header2.createCell(index2++).setCellValue("Comment Posted On");		
			
		

		int rowNum2 = 1;
		for (int i = 0 ; i < feedCommentsList.size(); i++) {
			//create the row data
			index = 0;
			Row row2 = sheet2.createRow(rowNum2++);
			FeedPostsBean bean = feedCommentsList.get(i);
			

			row2.createCell(index++).setCellValue(bean.getAcadYear());
			row2.createCell(index++).setCellValue(bean.getAcadMonth());
			row2.createCell(index++).setCellValue(bean.getBatch());
			row2.createCell(index++).setCellValue(bean.getSubject());
			row2.createCell(index++).setCellValue(bean.getTerm());
			row2.createCell(index++).setCellValue(bean.getPostURL());
			row2.createCell(index++).setCellValue(bean.getPostDescription());			
			row2.createCell(index++).setCellValue(bean.getFacultyName());
			row2.createCell(index++).setCellValue(bean.getPostPostedOn());			
			row2.createCell(index++).setCellValue(bean.getSapid());
			row2.createCell(index++).setCellValue(bean.getComment());
			row2.createCell(index++).setCellValue(bean.getCommentPostedOn());			
			
			
			
		}
		
		
	
	}
}

