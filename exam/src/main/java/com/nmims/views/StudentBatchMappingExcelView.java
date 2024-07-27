package com.nmims.views;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;

import com.nmims.beans.TimeBoundUserMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public class StudentBatchMappingExcelView extends AbstractXlsxStreamingView implements ApplicationContextAware {
    private static ApplicationContext act = null;

    @Override
    protected void buildExcelDocument(Map model, Workbook workbook, HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        act = getApplicationContext();
        List<TimeBoundUserMapping> studentBatchMapList = (List<TimeBoundUserMapping>) model.get("studentBatchMapList");
        //create a wordsheet
        Sheet sheet = workbook.createSheet("Student Batch Mapping");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Sr. No.");
        header.createCell(1).setCellValue("SAP ID");
        header.createCell(2).setCellValue("Term");
        header.createCell(3).setCellValue("Batch Name");
        header.createCell(4).setCellValue("Subject Name");
        header.createCell(5).setCellValue("Student Name");
        header.createCell(6).setCellValue("Email-Id");
        header.createCell(7).setCellValue("Image Url");
        header.createCell(8).setCellValue("Phone Number");

        int rowNum = 1;
        for (int i = 0; i < studentBatchMapList.size(); i++) {
            //create the row data
            Row row = sheet.createRow(rowNum++);
            TimeBoundUserMapping bean = studentBatchMapList.get(i);


            row.createCell(0).setCellValue(rowNum - 1);
            row.createCell(1).setCellValue(bean.getUserId());
            row.createCell(2).setCellValue(bean.getSem());
            row.createCell(3).setCellValue(bean.getName());
            row.createCell(4).setCellValue(bean.getSubject());
            row.createCell(5).setCellValue(bean.getStudentName());
            row.createCell(6).setCellValue(bean.getEmailId());
            row.createCell(7).setCellValue(bean.getImageUrl());
            row.createCell(8).setCellValue(bean.getMobile());
        }
    }

}
