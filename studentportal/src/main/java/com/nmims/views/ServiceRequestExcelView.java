package com.nmims.views;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractExcelView;

import com.nmims.beans.CenterStudentPortalBean;
import com.nmims.beans.ServiceRequestStudentPortal;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.daos.ServiceRequestDao;

@Component("serviceRequestExcelView")
public class ServiceRequestExcelView extends AbstractExcelView implements ApplicationContextAware{

	private static ApplicationContext act = null;
	
	//applicable SR list for Academic Bank of Credits
	@Value("${ACADEMIC_BANK_OF_CREDITS_SR_LIST}")
	private List<String> ACADEMIC_BANK_OF_CREDITS_SR_LIST;
	
	@Override
	protected void buildExcelDocument(Map model, HSSFWorkbook workbook,	HttpServletRequest request, HttpServletResponse response)
		throws Exception {
		String roles = (String)request.getSession().getAttribute("roles");
		System.out.println("roles in SR Report Excel View "+roles);
		act = getApplicationContext();
		ServiceRequestDao dao = (ServiceRequestDao)act.getBean("serviceRequestDao");
		HashMap<String, StudentStudentPortalBean> sapIdStudentsMap = dao.getAllStudents();
		HashMap<String,String> mapOfActiveSRTypesAndTAT = dao.mapOfActiveSRTypesAndTAT();
		HashMap<String, CenterStudentPortalBean> icLcMap = dao.getICLCMap();
		List<ServiceRequestStudentPortal> srList = (List<ServiceRequestStudentPortal>) model.get("srList");
		
		for (ServiceRequestStudentPortal srBean : srList) {
			StudentStudentPortalBean student =sapIdStudentsMap.get(srBean.getSapId());
			if(student != null){  
				srBean.setCenter( student.getCenterName());  
			}
			if(srBean.getSem()==null) {
				srBean.setSem("");
			} 
		}
		  Collections.sort(srList, Comparator.comparing(ServiceRequestStudentPortal::getCenter)
		            .thenComparing(ServiceRequestStudentPortal::getSapId) 
		            .thenComparing(ServiceRequestStudentPortal::getSem));  
		//create a wordsheet
		HSSFSheet sheet = workbook.createSheet("Service Requests");
		int index = 0;
		HSSFRow header = sheet.createRow(0);
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("SAP ID");
		header.createCell(index++).setCellValue("Sem");
		header.createCell(index++).setCellValue("Student Name");
		
		header.createCell(index++).setCellValue("First Name");
		header.createCell(index++).setCellValue("Middle Name");
		header.createCell(index++).setCellValue("Last Name");
		header.createCell(index++).setCellValue("Father Name");
		header.createCell(index++).setCellValue("Mother Name");
		header.createCell(index++).setCellValue("Husband Name");
		
		header.createCell(index++).setCellValue("Program");
		header.createCell(index++).setCellValue("Month");
		header.createCell(index++).setCellValue("Year");
		header.createCell(index++).setCellValue("Track ID");
		//IC name needed//
		header.createCell(index++).setCellValue("Learning Center");
		header.createCell(index++).setCellValue("Service Request ID");
		header.createCell(index++).setCellValue("Service Request Creation Date");
		header.createCell(index++).setCellValue("Service Request Closed Date");
		header.createCell(index++).setCellValue("Expected Closed Date For SR");
		header.createCell(index++).setCellValue("Service Request Type");
		header.createCell(index++).setCellValue("Service Request Status");
		header.createCell(index++).setCellValue("Payment Status");
		header.createCell(index++).setCellValue("Amount");
		if(roles.indexOf("Learning Center") != -1 || roles.indexOf("Information Center") != -1 )
		{
		}else {
			header.createCell(index++).setCellValue("Email ID");
			header.createCell(index++).setCellValue("Contact No.");
		}
		header.createCell(index++).setCellValue("Address Of Students");
		header.createCell(index++).setCellValue("Description");
		header.createCell(index++).setCellValue("Online/Offline");
		header.createCell(index++).setCellValue("Additional Info");
		header.createCell(index++).setCellValue("Additional Copies Issued");
		header.createCell(index++).setCellValue("Mode Of Dispatch");
		header.createCell(index++).setCellValue("Collected(Yes/No)");
		header.createCell(index++).setCellValue("SR Attribute");
		//Added shipping address fields
		header.createCell(index++).setCellValue("HouseNoName");
		header.createCell(index++).setCellValue("Street");
		header.createCell(index++).setCellValue("LandMark");
		header.createCell(index++).setCellValue("Old LandMark");
		header.createCell(index++).setCellValue("Locality");
		header.createCell(index++).setCellValue("PinCode");
		header.createCell(index++).setCellValue("City");
		header.createCell(index++).setCellValue("State");
		header.createCell(index++).setCellValue("Country");
		header.createCell(index++).setCellValue("Lateral Entry");
		header.createCell(index++).setCellValue("Program Change");
		header.createCell(index++).setCellValue("Discontinuation of Program");
		//Added Academic Bank of Credits field
		if(ACADEMIC_BANK_OF_CREDITS_SR_LIST.contains(srList.get(0).getServiceRequestType()))
			header.createCell(index++).setCellValue("Academic Bank of Credits Id");
		int rowNum = 1;
		for (int i = 0 ; i < srList.size(); i++) {
			index = 0;
			//create the row data

         	
         	try {
			HSSFRow row = sheet.createRow(rowNum++);
			String expectedClosedDateOfSR = "";
			
			ServiceRequestStudentPortal bean = srList.get(i);
			
			StudentStudentPortalBean student = sapIdStudentsMap.get(bean.getSapId());
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
         	Date d = dateFormat.parse(bean.getCreatedDate());
         	Calendar c = Calendar.getInstance();
         	c.setTime(d);
             	String tat = mapOfActiveSRTypesAndTAT.get(bean.getServiceRequestType());
             	if(tat==null){
             		System.out.println("bean.getServiceRequestType()"+bean.getServiceRequestType());
                    expectedClosedDateOfSR = "-";
             	}else {
                 	c.add(Calendar.DATE, Integer.parseInt(tat));
                    expectedClosedDateOfSR = dateFormat.format(c.getTime()); 
             	}
         	
         	
         	
			//String ic = "";
			String lc = "";
			String examMode = "Offline";
			if(student != null){
				//ic = student.getCenterName();
				CenterStudentPortalBean center = icLcMap.get(student.getCenterCode()); 
				if(center != null){
					lc = center.getLc();
				}
				
				if("Online".equalsIgnoreCase(student.getExamMode())){
					examMode = "Online";
				}
			}
			
			
			row.createCell(index++).setCellValue((i+1));
			row.createCell(index++).setCellValue(new Double(bean.getSapId()));
			row.createCell(index++).setCellValue(bean.getSem());
			row.createCell(index++).setCellValue(bean.getFirstName() + " " + bean.getLastName());
			
			row.createCell(index++).setCellValue(bean.getFirstName());
			row.createCell(index++).setCellValue(bean.getMiddleName());
			row.createCell(index++).setCellValue(bean.getLastName());
			row.createCell(index++).setCellValue(bean.getFatherName());
			row.createCell(index++).setCellValue(bean.getMotherName());
			row.createCell(index++).setCellValue(bean.getHusbandName());
			
			row.createCell(index++).setCellValue(student.getProgram());
			row.createCell(index++).setCellValue(bean.getMonth());
			row.createCell(index++).setCellValue(bean.getYear());
			row.createCell(index++).setCellValue(bean.getTrackId());
			row.createCell(index++).setCellValue(bean.getCenter());
			row.createCell(index++).setCellValue(bean.getId());
			row.createCell(index++).setCellValue(bean.getCreatedDate());
			row.createCell(index++).setCellValue(bean.getRequestClosedDate());
			row.createCell(index++).setCellValue(expectedClosedDateOfSR);
			row.createCell(index++).setCellValue(bean.getServiceRequestType());
			row.createCell(index++).setCellValue(bean.getRequestStatus());
			row.createCell(index++).setCellValue(bean.getTranStatus());
			row.createCell(index++).setCellValue(bean.getRespAmount());
			if(roles.indexOf("Learning Center") != -1 || roles.indexOf("Information Center") != -1 )
			{
			}else {
			row.createCell(index++).setCellValue(bean.getEmailId());
			row.createCell(index++).setCellValue(bean.getMobile());
			}
			//row.createCell(index++).setCellValue(bean.getPostalAddress());
			row.createCell(index++).setCellValue(student.getAddress());
			row.createCell(index++).setCellValue(bean.getDescription());
			row.createCell(index++).setCellValue(examMode);
			row.createCell(index++).setCellValue(bean.getAdditionalInfo1());
			row.createCell(index++).setCellValue(bean.getNoOfCopies());
			row.createCell(index++).setCellValue(bean.getModeOfDispatch());
			row.createCell(index++).setCellValue(bean.getIssued());
			row.createCell(index++).setCellValue(bean.getSrAttribute()==null ? "" : bean.getSrAttribute());
			//Added shipping address fields
			row.createCell(index++).setCellValue(bean.getHouseNoName());
			row.createCell(index++).setCellValue(bean.getStreet());
			row.createCell(index++).setCellValue(bean.getLandMark());
			row.createCell(index++).setCellValue(bean.getOld_LandMark());
			row.createCell(index++).setCellValue(bean.getLocality());
			row.createCell(index++).setCellValue(bean.getPin());
			row.createCell(index++).setCellValue(bean.getCity());
			row.createCell(index++).setCellValue(bean.getState());
			row.createCell(index++).setCellValue(bean.getCountry());
			row.createCell(index++).setCellValue(bean.getIsLateral());
			row.createCell(index++).setCellValue(bean.getProgramChanged());
			row.createCell(index++).setCellValue(bean.getProgramStatus());
			//Added Academic Bank of Credits fields
			if(ACADEMIC_BANK_OF_CREDITS_SR_LIST.contains(bean.getServiceRequestType()))
				row.createCell(index++).setCellValue(bean.getAbcId());
         	} catch (Exception e) {
         		
			}
        }
		sheet.autoSizeColumn(0); sheet.autoSizeColumn(1);
		sheet.autoSizeColumn(2); sheet.autoSizeColumn(3);
		sheet.autoSizeColumn(4); sheet.autoSizeColumn(5);
		sheet.autoSizeColumn(6); sheet.autoSizeColumn(7);
		sheet.autoSizeColumn(8); sheet.autoSizeColumn(9);
		sheet.autoSizeColumn(10);
		sheet.setAutoFilter(CellRangeAddress.valueOf("A1:R"+srList.size()));
	}

}
