package com.nmims.views;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.web.servlet.view.document.AbstractExcelView;

import com.nmims.beans.FacultyAcadsBean;
import com.nmims.beans.SessionAttendanceFeedbackAcads;
import com.nmims.beans.SessionDayTimeAcadsBean;

public class FacultyExcelView extends AbstractExcelView {

	@Override
	protected void buildExcelDocument(Map model, HSSFWorkbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		// Read list
		List<FacultyAcadsBean> facultyList = (List<FacultyAcadsBean>) model.get("facultyList");
		// create a wordsheet
		HSSFSheet sheet = workbook.createSheet("Faculty List");

		int index = 0;
		HSSFRow header = sheet.createRow(0);
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("Faculty ID");
		header.createCell(index++).setCellValue("Title");
		header.createCell(index++).setCellValue("First Name");
		header.createCell(index++).setCellValue("Middle Name");
		header.createCell(index++).setCellValue("Last Name");
		header.createCell(index++).setCellValue("Email");
		header.createCell(index++).setCellValue("Secondary (Optional)");
		header.createCell(index++).setCellValue("Mobile");
		header.createCell(index++).setCellValue("Alternate Mobile");
		header.createCell(index++).setCellValue("Office (Optional)");
		header.createCell(index++).setCellValue("Home (Optional)");
		header.createCell(index++).setCellValue("DOB");
		header.createCell(index++).setCellValue("Education");
		header.createCell(index++).setCellValue("Subject Preference 1");
		header.createCell(index++).setCellValue("Subject Preference 2");
		header.createCell(index++).setCellValue("Subject Preference 3");
		header.createCell(index++).setCellValue("Years of NGASCE Experience");
		header.createCell(index++).setCellValue("Years of Teaching Experience");
		header.createCell(index++).setCellValue("Years of Corporate Experience");
		header.createCell(index++).setCellValue("Location");
		header.createCell(index++).setCellValue("Address");
		header.createCell(index++).setCellValue("Graduation Details");
		header.createCell(index++).setCellValue("Year of Passing Graduation");
		header.createCell(index++).setCellValue("PHD Details");
		header.createCell(index++).setCellValue("Year of Passing PHD");
		header.createCell(index++).setCellValue("CV URL");
		header.createCell(index++).setCellValue("IMAGE URL");
		header.createCell(index++).setCellValue("Current Organization");
		header.createCell(index++).setCellValue("Designation");		
		header.createCell(index++).setCellValue("Nature of Appointment");
		header.createCell(index++).setCellValue("Area of Specialisation");
		header.createCell(index++).setCellValue("Other Area of Specialisation");
		header.createCell(index++).setCellValue("Aadhar Number");
		header.createCell(index++).setCellValue("Program Group");
		header.createCell(index++).setCellValue("Program Name");
		header.createCell(index++).setCellValue("Approved in Slab");
		header.createCell(index++).setCellValue("Date of EC Meeting Approval Taken");
		header.createCell(index++).setCellValue("Consent for Marketing Collaterals or Photo and Profile Release");
		header.createCell(index++).setCellValue("Consent for Marketing Collaterals or Photo and Profile Release Reason");
		header.createCell(index++).setCellValue("Honors and Awards");
		header.createCell(index++).setCellValue("Memberships");
		header.createCell(index++).setCellValue("Research Interest");
		header.createCell(index++).setCellValue("Articles Published in International Journals");
		header.createCell(index++).setCellValue("Articles Published in National Journals");
		header.createCell(index++).setCellValue("Summary of Papers Published in ABCD Journals");
		header.createCell(index++).setCellValue("Papers Presentation at International Conference");
		header.createCell(index++).setCellValue("Papers Presentation at National Conference");
		header.createCell(index++).setCellValue("Case Studies Published");
		header.createCell(index++).setCellValue("Books Published");
		header.createCell(index++).setCellValue("Book Chapters Published");
		header.createCell(index++).setCellValue("List of Patents");
		header.createCell(index++).setCellValue("Consulting Projects");
		header.createCell(index++).setCellValue("Research Projects");
		header.createCell(index++).setCellValue("Active");
		int rowNum = 1;
		for (int i = 0; i < facultyList.size(); i++) {
			index = 0;
			// create the row data
			HSSFRow row = sheet.createRow(rowNum++);
			FacultyAcadsBean bean = facultyList.get(i);

			row.createCell(index++).setCellValue((i + 1));
			row.createCell(index++).setCellValue(bean.getFacultyId());
			row.createCell(index++).setCellValue(bean.getTitle());
			row.createCell(index++).setCellValue(bean.getFirstName());
			row.createCell(index++).setCellValue(bean.getMiddleName());
			row.createCell(index++).setCellValue(bean.getLastName());
			row.createCell(index++).setCellValue(bean.getEmail());
			row.createCell(index++).setCellValue(bean.getSecondaryEmail());
			row.createCell(index++).setCellValue(bean.getMobile());
			row.createCell(index++).setCellValue(bean.getAltContact());
			row.createCell(index++).setCellValue(bean.getOfficeContact());
			row.createCell(index++).setCellValue(bean.getHomeContact());
			row.createCell(index++).setCellValue(bean.getDob());
			row.createCell(index++).setCellValue(bean.getEducation());
			row.createCell(index++).setCellValue(bean.getSubjectPref1());
			row.createCell(index++).setCellValue(bean.getSubjectPref2());
			row.createCell(index++).setCellValue(bean.getSubjectPref3());
			row.createCell(index++).setCellValue(bean.getNgasceExp());
			row.createCell(index++).setCellValue(bean.getTeachingExp());
			row.createCell(index++).setCellValue(bean.getCorporateExp());
			row.createCell(index++).setCellValue(bean.getLocation());
			row.createCell(index++).setCellValue(bean.getAddress());
			row.createCell(index++).setCellValue(bean.getGraduationDetails());
			row.createCell(index++).setCellValue(bean.getYearOfPassingGraduation());
			row.createCell(index++).setCellValue(bean.getPhdDetails());
			row.createCell(index++).setCellValue(bean.getYearOfPassingPhd());
			row.createCell(index++).setCellValue(bean.getCvUrl());
			row.createCell(index++).setCellValue(bean.getImgUrl());
			row.createCell(index++).setCellValue(bean.getCurrentOrganization());
			row.createCell(index++).setCellValue(bean.getDesignation());			
			row.createCell(index++).setCellValue(bean.getNatureOfAppointment());
			row.createCell(index++).setCellValue(bean.getAreaOfSpecialisation());
			row.createCell(index++).setCellValue(bean.getOtherAreaOfSpecialisation());
			row.createCell(index++).setCellValue(bean.getAadharNumber());
			row.createCell(index++).setCellValue(bean.getProgramGroup());
			row.createCell(index++).setCellValue(bean.getProgramName());
			row.createCell(index++).setCellValue(bean.getApprovedInSlab());
			row.createCell(index++).setCellValue(bean.getDateOfECMeetingApprovalTaken());
			row.createCell(index++).setCellValue(bean.getConsentForMarketingCollateralsOrPhotoAndProfileRelease());
			row.createCell(index++).setCellValue(bean.getConsentForMarketingCollateralsOrPhotoAndProfileReleaseReason());
			row.createCell(index++).setCellValue(bean.getHonorsAndAwards());
			row.createCell(index++).setCellValue(bean.getMemberships());
			row.createCell(index++).setCellValue(bean.getResearchInterest());
			row.createCell(index++).setCellValue(bean.getArticlesPublishedInInternationalJournals());
			row.createCell(index++).setCellValue(bean.getArticlesPublishedInNationalJournals());
			row.createCell(index++).setCellValue(bean.getSummaryOfPapersPublishedInABDCJournals());
			row.createCell(index++).setCellValue(bean.getPaperPresentationsAtInternationalConference());
			row.createCell(index++).setCellValue(bean.getPaperPresentationAtNationalConference());
			row.createCell(index++).setCellValue(bean.getCaseStudiesPublished());
			row.createCell(index++).setCellValue(bean.getBooksPublished());
			row.createCell(index++).setCellValue(bean.getBookChaptersPublished());
			row.createCell(index++).setCellValue(bean.getListOfPatents());
			row.createCell(index++).setCellValue(bean.getConsultingProjects());
			row.createCell(index++).setCellValue(bean.getResearchProjects());
			row.createCell(index++).setCellValue(bean.getActive());
		}
		sheet.autoSizeColumn(0);
		sheet.autoSizeColumn(1);
		sheet.autoSizeColumn(2);
		sheet.autoSizeColumn(3);
		sheet.autoSizeColumn(4);
		sheet.autoSizeColumn(5);
		sheet.autoSizeColumn(6);
		sheet.autoSizeColumn(7);
		sheet.autoSizeColumn(8);
		sheet.autoSizeColumn(9);
		sheet.autoSizeColumn(10);
		sheet.autoSizeColumn(11);
		sheet.autoSizeColumn(12);
		sheet.autoSizeColumn(13);
		sheet.autoSizeColumn(14);
		sheet.autoSizeColumn(15);
		sheet.autoSizeColumn(16);
		sheet.autoSizeColumn(17);
		sheet.autoSizeColumn(18);
		sheet.autoSizeColumn(19);
		sheet.autoSizeColumn(20);
		sheet.autoSizeColumn(21);
		sheet.autoSizeColumn(22);
		sheet.autoSizeColumn(23);
		sheet.autoSizeColumn(24);
		sheet.autoSizeColumn(25);
		sheet.autoSizeColumn(26);
		sheet.autoSizeColumn(27);
		sheet.autoSizeColumn(28);
		sheet.autoSizeColumn(29);
		sheet.autoSizeColumn(30);
		sheet.autoSizeColumn(31);
		sheet.autoSizeColumn(32);
		sheet.autoSizeColumn(33);
		sheet.autoSizeColumn(34);
		sheet.autoSizeColumn(35);
		sheet.autoSizeColumn(36);
		sheet.autoSizeColumn(37);
		sheet.autoSizeColumn(38);
		sheet.autoSizeColumn(39);
		sheet.autoSizeColumn(40);
		sheet.autoSizeColumn(41);
		sheet.autoSizeColumn(42);
		sheet.autoSizeColumn(43);
		sheet.autoSizeColumn(44);
		sheet.autoSizeColumn(45);
		sheet.autoSizeColumn(46);
		sheet.autoSizeColumn(47);
		sheet.autoSizeColumn(48);
		sheet.autoSizeColumn(49);
		sheet.autoSizeColumn(50);
		sheet.autoSizeColumn(51);
		sheet.autoSizeColumn(52);
		sheet.autoSizeColumn(53);
		sheet.autoSizeColumn(54);		
		sheet.setAutoFilter(CellRangeAddress.valueOf("A1:G" + (facultyList.size() + 1)));
	}
}
