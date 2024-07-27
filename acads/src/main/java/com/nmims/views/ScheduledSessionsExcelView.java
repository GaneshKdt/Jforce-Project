package com.nmims.views;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.web.servlet.view.document.AbstractExcelView;

import com.nmims.beans.FacultyAcadsBean;
import com.nmims.beans.SessionDayTimeAcadsBean;

public class ScheduledSessionsExcelView extends AbstractExcelView{
	
	@Override
	protected void buildExcelDocument(Map model, HSSFWorkbook workbook,	HttpServletRequest request, HttpServletResponse response)
		throws Exception {
 
		List<SessionDayTimeAcadsBean> sessionsList = (List<SessionDayTimeAcadsBean>) model.get("scheduledSessionList");
		HashMap<String,FacultyAcadsBean> mapOfFacultyIdAndFacultyRecord = (HashMap<String,FacultyAcadsBean>) request.getSession().getAttribute("mapOfFacultyIdAndFacultyRecord");
		//create a wordsheet
		HSSFSheet sheet = workbook.createSheet("Scheduled Sessions");
		
 		int index = 0;
		HSSFRow header = sheet.createRow(0);
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("Year");
	    header.createCell(index++).setCellValue("Month");
		header.createCell(index++).setCellValue("Date");
		header.createCell(index++).setCellValue("Start Time");
		header.createCell(index++).setCellValue("Day");
		header.createCell(index++).setCellValue("Subject");
		header.createCell(index++).setCellValue("Subject Code");
		header.createCell(index++).setCellValue("Session Name");
		header.createCell(index++).setCellValue("Session ID");
		header.createCell(index++).setCellValue("Faculty ID");
		header.createCell(index++).setCellValue("Faculty Location");
		header.createCell(index++).setCellValue("Faculty Name");
		header.createCell(index++).setCellValue("Faculty Email");
		header.createCell(index++).setCellValue("Program Group 1");
		header.createCell(index++).setCellValue("Sem Group 1");
		header.createCell(index++).setCellValue("Program Group 2");
		header.createCell(index++).setCellValue("Sem Group 2");
		header.createCell(index++).setCellValue("Program Group 3");
		header.createCell(index++).setCellValue("Sem Group 3");
		header.createCell(index++).setCellValue("Program Group 4");
		header.createCell(index++).setCellValue("Sem Group 4");
		header.createCell(index++).setCellValue("Program Group 5");
		header.createCell(index++).setCellValue("Sem Group 5");
		header.createCell(index++).setCellValue("Program Group 6");
		header.createCell(index++).setCellValue("Sem Group 6");
		header.createCell(index++).setCellValue("Meeting Number");
		header.createCell(index++).setCellValue("Room");
		header.createCell(index++).setCellValue("Host Key");
		header.createCell(index++).setCellValue("Meeting Password");
		header.createCell(index++).setCellValue("CISCO Status");
		header.createCell(index++).setCellValue("HOST ID");
		header.createCell(index++).setCellValue("HOST Password");
		header.createCell(index++).setCellValue("Alt Meeting Number 1");
		header.createCell(index++).setCellValue("Alt Faculty Id 1 ");
		header.createCell(index++).setCellValue("Alt Faculty Name ");
		header.createCell(index++).setCellValue("Alt Faculty Email ");
		header.createCell(index++).setCellValue("Alt Meeting Password 1");
		header.createCell(index++).setCellValue("Alt Meeting Number 2");
		header.createCell(index++).setCellValue("Alt Faculty Id 2 ");
		header.createCell(index++).setCellValue("Alt Faculty Name ");
		header.createCell(index++).setCellValue("Alt Faculty Email 2");
		header.createCell(index++).setCellValue("Alt Meeting Password 2");
		header.createCell(index++).setCellValue("Alt Meeting Number 3");
		header.createCell(index++).setCellValue("Alt Faculty Id 3");
		header.createCell(index++).setCellValue("Alt Faculty Name ");
		header.createCell(index++).setCellValue("Alt Faculty Email 3 ");
		header.createCell(index++).setCellValue("Alt Meeting Number 3");
		header.createCell(index++).setCellValue("Alt Meeting Password 3");
		header.createCell(index++).setCellValue("Corporate Name");
		header.createCell(index++).setCellValue("Track");
		header.createCell(index++).setCellValue("Batch Name");
		header.createCell(index++).setCellValue("Session Type");
		header.createCell(index++).setCellValue("HasModuleId");
		header.createCell(index++).setCellValue("Common Session Sem");
		header.createCell(index++).setCellValue("Common Session Program List");
		header.createCell(index++).setCellValue("VideoLink");
		header.createCell(index++).setCellValue("Session Duration");
		header.createCell(index++).setCellValue("Session Plan Module Created Date");
		header.createCell(index++).setCellValue("Session Plan Module LastModified Date");
		
 
		int rowNum = 1;
		for (int i = 0 ; i < sessionsList.size(); i++) {
			index = 0;
			//create the row data
			
			HSSFRow row = sheet.createRow(rowNum++);
			SessionDayTimeAcadsBean bean = sessionsList.get(i);
			
			String faculty = mapOfFacultyIdAndFacultyRecord.get(bean.getFacultyId()) !=null ? mapOfFacultyIdAndFacultyRecord.get(bean.getFacultyId()).getFullName():"";
			String facultyEmail = mapOfFacultyIdAndFacultyRecord.get(bean.getFacultyId()) !=null ? mapOfFacultyIdAndFacultyRecord.get(bean.getFacultyId()).getEmail():"";
			String altFaculty = mapOfFacultyIdAndFacultyRecord.get(bean.getAltFacultyId()) !=null ? mapOfFacultyIdAndFacultyRecord.get(bean.getAltFacultyId()).getFullName():"";
			String altFacultyEmail = mapOfFacultyIdAndFacultyRecord.get(bean.getAltFacultyId()) !=null ? mapOfFacultyIdAndFacultyRecord.get(bean.getAltFacultyId()).getEmail():"";
			String altFaculty2 =mapOfFacultyIdAndFacultyRecord.get(bean.getAltFacultyId2()) !=null ? mapOfFacultyIdAndFacultyRecord.get(bean.getAltFacultyId2()).getFullName():"";
			String altFacultyEmail2 =mapOfFacultyIdAndFacultyRecord.get(bean.getAltFacultyId2()) !=null ? mapOfFacultyIdAndFacultyRecord.get(bean.getAltFacultyId2()).getEmail():"";
			String altFaculty3 = mapOfFacultyIdAndFacultyRecord.get(bean.getAltFacultyId3()) !=null ? mapOfFacultyIdAndFacultyRecord.get(bean.getAltFacultyId3()).getFullName():"";
			String altFacultyEmail3 = mapOfFacultyIdAndFacultyRecord.get(bean.getAltFacultyId3()) !=null ? mapOfFacultyIdAndFacultyRecord.get(bean.getAltFacultyId3()).getEmail():"";
			String corporateName = StringUtils.isBlank(bean.getCorporateName()) ? "Retail" : bean.getCorporateName(); 
			
			row.createCell(index++).setCellValue((i+1));
			row.createCell(index++).setCellValue(new Double(bean.getYear()));
			row.createCell(index++).setCellValue(bean.getMonth());
			row.createCell(index++).setCellValue(bean.getDate());
			row.createCell(index++).setCellValue(bean.getStartTime());
			row.createCell(index++).setCellValue(bean.getDay());
			row.createCell(index++).setCellValue(bean.getSubject());
			row.createCell(index++).setCellValue(bean.getSubjectCode());
			row.createCell(index++).setCellValue(bean.getSessionName());
			row.createCell(index++).setCellValue(bean.getId());
			row.createCell(index++).setCellValue(bean.getFacultyId());
			row.createCell(index++).setCellValue(bean.getFacultyLocation());
			row.createCell(index++).setCellValue(faculty);
			row.createCell(index++).setCellValue(facultyEmail);
			row.createCell(index++).setCellValue(bean.getGroup1Program());
			row.createCell(index++).setCellValue(bean.getGroup1Sem());
			row.createCell(index++).setCellValue(bean.getGroup2Program());
			row.createCell(index++).setCellValue(bean.getGroup2Sem());
			row.createCell(index++).setCellValue(bean.getGroup3Program());
			row.createCell(index++).setCellValue(bean.getGroup3Sem());
			row.createCell(index++).setCellValue(bean.getGroup4Program());
			row.createCell(index++).setCellValue(bean.getGroup4Sem());
			row.createCell(index++).setCellValue(bean.getGroup5Program());
			row.createCell(index++).setCellValue(bean.getGroup5Sem());
			row.createCell(index++).setCellValue(bean.getGroup6Program());
			row.createCell(index++).setCellValue(bean.getGroup6Sem());
			row.createCell(index++).setCellValue(bean.getMeetingKey());
			row.createCell(index++).setCellValue(bean.getRoom());
			row.createCell(index++).setCellValue(bean.getHostKey());
			row.createCell(index++).setCellValue(bean.getMeetingPwd());
			row.createCell(index++).setCellValue(bean.getCiscoStatus());
			row.createCell(index++).setCellValue(bean.getHostId());
			row.createCell(index++).setCellValue(bean.getHostPassword());
			row.createCell(index++).setCellValue(bean.getAltMeetingKey());
			row.createCell(index++).setCellValue(bean.getAltFacultyId());
			row.createCell(index++).setCellValue(altFaculty);
			row.createCell(index++).setCellValue(altFacultyEmail);
			row.createCell(index++).setCellValue(bean.getAltMeetingPwd());
			row.createCell(index++).setCellValue(bean.getAltMeetingKey2());
			row.createCell(index++).setCellValue(bean.getAltFacultyId2());
			row.createCell(index++).setCellValue(altFaculty2);
			row.createCell(index++).setCellValue(altFacultyEmail2);
			row.createCell(index++).setCellValue(bean.getAltMeetingPwd2());
			row.createCell(index++).setCellValue(bean.getAltMeetingKey3());
			row.createCell(index++).setCellValue(bean.getAltFacultyId3());
			row.createCell(index++).setCellValue(altFaculty3);
			row.createCell(index++).setCellValue(altFacultyEmail3);
			row.createCell(index++).setCellValue(bean.getAltMeetingKey3());
			row.createCell(index++).setCellValue(bean.getAltMeetingPwd3());
			row.createCell(index++).setCellValue(corporateName);
			row.createCell(index++).setCellValue(bean.getTrack());
			row.createCell(index++).setCellValue(bean.getBatchName());
			bean.setSessionType(bean.getSessionType().replace("1", "Webinar"));
			bean.setSessionType(bean.getSessionType().replace("2", "Meeting"));
			row.createCell(index++).setCellValue(bean.getSessionType());
			row.createCell(index++).setCellValue(bean.getHasModuleId());
			row.createCell(index++).setCellValue(bean.getSem());
			row.createCell(index++).setCellValue(bean.getProgramList());
			row.createCell(index++).setCellValue(bean.getVideoLink());
			row.createCell(index++).setCellValue(bean.getSessionDuration());
			row.createCell(index++).setCellValue(bean.getSessionPlanModuleCreatedDate());
			row.createCell(index++).setCellValue(bean.getSessionPlanModuleLastModifiedDate());
			
        }
		sheet.autoSizeColumn(0); sheet.autoSizeColumn(1);
		sheet.autoSizeColumn(2); sheet.autoSizeColumn(3);
		sheet.autoSizeColumn(4); sheet.autoSizeColumn(5);
		sheet.autoSizeColumn(6); sheet.autoSizeColumn(7);
		sheet.autoSizeColumn(8); sheet.autoSizeColumn(9);
		
		sheet.setAutoFilter(CellRangeAddress.valueOf("A1:AU"+(sessionsList.size()+1)));
	}

}
