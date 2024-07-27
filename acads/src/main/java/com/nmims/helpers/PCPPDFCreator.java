package com.nmims.helpers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Component;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.nmims.beans.PCPBookingTransactionBean;
import com.nmims.beans.StudentAcadsBean;

@Component
public class PCPPDFCreator {
	Font font3 = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
	Font font2 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
	Font font4 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
	Font font1 = new Font(Font.FontFamily.TIMES_ROMAN, 12);
	Font font5 = new Font(Font.FontFamily.TIMES_ROMAN, 6);
	Font font6 = new Font(Font.FontFamily.TIMES_ROMAN, 8);
	Font font7 = new Font(Font.FontFamily.TIMES_ROMAN, 18);
	Font font8 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
	Font font9 = new Font(Font.FontFamily.TIMES_ROMAN, 10);
	Font font10 = new Font(Font.FontFamily.TIMES_ROMAN, 7);
	Font font11 = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.BOLD);
	

	
	public String createPCPEntryPass(ArrayList<PCPBookingTransactionBean> subjectsBooked,
						HashMap<String, String> programCodeNameMap, StudentAcadsBean student,
						String PCPTICKET_PATH,
						HashMap<String, PCPBookingTransactionBean> subjectBookingMap,
						String STUDENT_PHOTOS_PATH) throws Exception {
		
		Document document = new Document(PageSize.A4);
		String pcpYear = subjectsBooked.get(0).getYear();
		String pcpMonth = subjectsBooked.get(0).getMonth();
		String sapid = student.getSapid();
		String fileName = sapid + "_PCP_Entry_Pass.pdf";
		
		try{

			document.setMargins(30, 30, 30, 30);

			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(PCPTICKET_PATH+fileName));

			document.open();

			generateHeaderTable(document, student);

			generateStudentInfoTable(document, student, programCodeNameMap, writer, STUDENT_PHOTOS_PATH);

			generateSubjectsTable(document, subjectsBooked, subjectBookingMap);
			
			ArrayList<PCPBookingTransactionBean> bookings = new ArrayList<PCPBookingTransactionBean>(subjectBookingMap.values());
			Integer amount = getFeesPaid(bookings);

			generateInstructions(document, student, amount);

		}catch(Exception e){
			  
			throw e;
		}finally{
			document.close(); 
		}
		return PCPTICKET_PATH+fileName;
		// TODO Auto-generated method stub
	}
	
	private Integer getFeesPaid(List<PCPBookingTransactionBean> pcpBookings) {
		Integer amount = 0;
		HashMap<String, String> trackIdMap = new HashMap<>();
		for (PCPBookingTransactionBean bean : pcpBookings) {
			if(!trackIdMap.containsKey(bean.getTrackId())){
				amount = amount + Integer.parseInt(bean.getAmount());
				trackIdMap.put(bean.getTrackId(), null);
			}
		}
		
		return amount;
	}
	
	private void generateInstructions(Document document, StudentAcadsBean student, Integer amount) throws Exception {
		try {
			
			document.add(new Paragraph("Amount Paid: INR. "+amount +"/-", font9));
			document.add( Chunk.NEWLINE );
			
			document.add(new Paragraph("Important Instructions:	", font4));
			com.itextpdf.text.List orderedList = new com.itextpdf.text.List(com.itextpdf.text.List.ORDERED);

			orderedList.add(new ListItem("It is Mandatory for the student to carry Student ID Card and Entry Pass for Personal Contact Program. Without both the documents student will strictly not be allowed in the Class.", font9));
			orderedList.add(new ListItem("This Entry Pass is valid for the subject/s mentioned in the Entry Pass and PCP Center displayed.", font9));
			orderedList.add(new ListItem("The schedule for Personal Contact Programs (PCPs) will be updated in Announcement section of Student Portal. Students are requested to view the Announcement section for detailed schedule of PCP at respective location.", font9));
			orderedList.add(new ListItem("The PCP's shall be conducted at NGA-SCE University Regional Office.", font9));
			orderedList.add(new ListItem("The Personal Contact Programs per course shall be for 3 hours.", font9));
			orderedList.add(new ListItem("Enrolment to attend PCP program for any course shall be final and student will be not allowed to change / modify or cancel under any circumstances.", font9));
			orderedList.add(new ListItem("NGA-SCE reserves the right to cancel the personal contact program for any course at any University Regional Office where less than 15 students register for that course at that University Regional Office. In such case the PCP fee paid by the students for that course shall be refunded.", font9));
			orderedList.add(new ListItem("Students' failure to attend personal contact program that they have enrolled in will be treated as a lapse on part of the students. No refund will be given in this case.", font9));
			orderedList.add(new ListItem("The student shall be required to record his/her attendance at the PCP attended.", font9));
			orderedList.add(new ListItem("The school reserves the right to cancel or change the dates of PCP.", font9));

			document.add(orderedList);

		} catch (Exception e) {
			  
			throw e;
		}
	}

	
	private void generateSubjectsTable(Document document,ArrayList<PCPBookingTransactionBean> subjectsBooked,
			HashMap<String, PCPBookingTransactionBean> subjectBookingMap) throws Exception {
		try{

			PdfPTable subjectsTable = new PdfPTable(4);

			PdfPCell srNoCell = new PdfPCell(new Paragraph("Sr. No", font4));
			PdfPCell subjectsCell = new PdfPCell(new Paragraph("Subject", font4));
			PdfPCell semesterCell = new PdfPCell(new Paragraph("Sem", font4));
			/*PdfPCell dateCell = new PdfPCell(new Paragraph("Date", font4));
			PdfPCell startTimeCell = new PdfPCell(new Paragraph("Start Time", font4));
			PdfPCell endTimeCell = new PdfPCell(new Paragraph("End Time", font4));*/
			PdfPCell pcpCenterCell = new PdfPCell(new Paragraph("PCP Center", font4));

			srNoCell.setFixedHeight(30);
			srNoCell.setVerticalAlignment(Element.ALIGN_TOP);
			subjectsCell.setVerticalAlignment(Element.ALIGN_TOP);
			semesterCell.setVerticalAlignment(Element.ALIGN_TOP);
			pcpCenterCell.setVerticalAlignment(Element.ALIGN_TOP);


			srNoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			subjectsCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			semesterCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			pcpCenterCell.setHorizontalAlignment(Element.ALIGN_CENTER);

			subjectsTable.addCell(srNoCell);
			subjectsTable.addCell(subjectsCell);
			subjectsTable.addCell(semesterCell);
			subjectsTable.addCell(pcpCenterCell);
			subjectsTable.completeRow();


			int count = 1;
			for (int j = 0; j < subjectsBooked.size(); j++) {
				PCPBookingTransactionBean bean = subjectsBooked.get(j);
				String examMode = bean.getExamMode();
				
				if("Project".equals(bean.getSubject()) || "Module 4 - Project".equals(bean.getSubject())){
					continue;
				}

				srNoCell = new PdfPCell(new Paragraph(count+"", font9));
				subjectsCell = new PdfPCell(new Paragraph(bean.getSubject(), font9));
				semesterCell = new PdfPCell(new Paragraph(bean.getSem(), font9));
				String center = bean.getCenter();
				
				String centerNameWithAddress = getCenterAddress(center);
				
				pcpCenterCell = new PdfPCell(new Paragraph(centerNameWithAddress, font9));

				srNoCell.setHorizontalAlignment(Element.ALIGN_CENTER); srNoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				subjectsCell.setHorizontalAlignment(Element.ALIGN_CENTER); subjectsCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				semesterCell.setHorizontalAlignment(Element.ALIGN_CENTER); semesterCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				pcpCenterCell.setHorizontalAlignment(Element.ALIGN_UNDEFINED); pcpCenterCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

				srNoCell.setFixedHeight(30);
				
				subjectsTable.addCell(srNoCell);
				subjectsTable.addCell(subjectsCell);
				subjectsTable.addCell(semesterCell);
				subjectsTable.addCell(pcpCenterCell);
				subjectsTable.completeRow();

				count++;
			}

			subjectsTable.setWidthPercentage(100);
			subjectsTable.setSpacingBefore(5f);
			subjectsTable.setSpacingAfter(5f);
			float[] marksColumnWidths = {0.8f, 2.5f, 0.8f, 5f};
			subjectsTable.setWidths(marksColumnWidths);
			document.add(subjectsTable);

		}catch(Exception e){
			  
			throw e;
		}

	}
	


	private String getCenterAddress(String center) {
		if("Mumbai".contains(center)){
			return "NMIMS Global Access School for Continuing Education, 2nd Floor, V.L.Mehta Road, Vile Parle West, Mumbai � 400056 Maharashtra";
		}else if("Delhi".equalsIgnoreCase(center)){
			return "NMIMS Global Access School for Continuing Education, Upper Ground Floor, KP � 1, Pitampura, New Delhi � 110088 New Delhi";
		}else if("Ahmedabad".equalsIgnoreCase(center)){
			return "NMIMS Global Access School for Continuing Education, B-3, Ground Floor, �Safal Profitaire�, Corporate Road, Prahladnagar, Ahmedabad -380 007 Gujarat";
		}else if("Kolkata".equalsIgnoreCase(center)){
			return "NMIMS Global Access School for Continuing Education, Unit # 505, Merlin Infinite, DN-51, Salt Lake City, Sector V,Kolkata-700091 West Bengal";
		}else if("Pune".equalsIgnoreCase(center)){
			return "NMIMS Global Access School for Continuing Education, 365/6, Aaj Ka Anand Building, 2nd Floor, Opposite SSPS School, Narveer Tanaji Wadi, Shivajinagar, Pune -411005 Maharashtra";
		}else if("Hyderabad".equalsIgnoreCase(center)){
			return "NMIMS Global Access School for Continuing Education, 12-13-95, Taranaka , Street No. 3, Secunderabad � 500017 Andhra Pradesh";
		}else if("Banglore".equalsIgnoreCase(center)){
			return "NMIMS Global Access School for Continuing Education, 11, Kaveri Regent Coronet, 80 Feet Road, 7th Main, 3rd Block, Next to Raheja Residency, Koramanagla, Bangalore � 560034 Karnataka";
		}
			
			
		return null;
	}

	private void generateStudentInfoTable(Document document, StudentAcadsBean student, HashMap<String, String> programCodeNameMap, 
			PdfWriter writer, String STUDENT_PHOTOS_PATH) throws Exception {
		try {
			Image studentPhoto = null;
			PdfPTable studentInfoTable = null;
			if("Online".equals(student.getExamMode())){
				studentInfoTable = new PdfPTable(3);
			}else{
				studentInfoTable = new PdfPTable(2);
			}

			PdfPCell studentNoCell = new PdfPCell(new Paragraph("Student No: "+student.getSapid(), font9));
			PdfPCell genderCell = new PdfPCell(new Paragraph("Gender: "+student.getGender(), font9));

			studentNoCell.setFixedHeight(40);
			genderCell.setFixedHeight(40);

			studentInfoTable.addCell(studentNoCell);
			studentInfoTable.addCell(genderCell);
			//if("Jul2014".equals(student.getPrgmStructApplicable())){
				//Photo not required for Offline exam.
				
				try {
					//studentPhoto = Image.getInstance(new URL(student.getImageUrl()));
					studentPhoto = Image.getInstance(STUDENT_PHOTOS_PATH +student.getSapid()+".jpg");
					studentPhoto.scaleAbsolute(100, 120);

				} catch (Exception e) {
					  
					//If not .jpg, try .jpeg
					studentPhoto = Image.getInstance(STUDENT_PHOTOS_PATH +student.getSapid()+".jpeg");
					studentPhoto.scaleAbsolute(100, 120);
				}
				PdfPCell studentPhotoCell = null;
				if(studentPhoto == null){
					//In case URL was malformed or was not found
					studentPhotoCell = new PdfPCell();
				}else{
					studentPhotoCell = new PdfPCell(studentPhoto);
				}

				studentPhotoCell.setRowspan(6);
				studentPhotoCell.setBorder(0);
				studentPhotoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				studentInfoTable.addCell(studentPhotoCell);
			//}


			studentInfoTable.completeRow();

			PdfPCell studentNameCell = new PdfPCell(new Paragraph("Student Name: "+student.getLastName().toUpperCase()+" "+student.getFirstName().toUpperCase(), font9));
			studentNameCell.setColspan(2);
			studentNameCell.setFixedHeight(40);
			studentInfoTable.addCell(studentNameCell);
			studentInfoTable.completeRow();
			
			String programFullName = programCodeNameMap.get(student.getProgram());
			if("PGDMM-MLI".equals(student.getProgram())){
				programFullName = programFullName + " � MLI (Max Life Insurance) ";
			}
			PdfPCell programCell = new PdfPCell(new Paragraph("Program: "+programFullName, font9));


			programCell.setFixedHeight(40);
			programCell.setColspan(2);
			studentInfoTable.addCell(programCell);

			studentInfoTable.completeRow();
			studentInfoTable.setWidthPercentage(100);

			studentInfoTable.setSpacingBefore(10);
			studentInfoTable.setSpacingAfter(10);
			document.add(studentInfoTable);

			//if("Jul2014".equals(student.getPrgmStructApplicable()) && studentPhoto != null){
				Phrase watermark = new Phrase("AUTHENTIC PHOTOGRAPH", new Font(FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK));
				PdfContentByte canvas = writer.getDirectContent();
				ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, watermark, 490, 590, 20);
			//}
			
		} catch (Exception e) {
			  
			throw e;
		}
	}

	
	private void generateHeaderTable(Document document, StudentAcadsBean student) throws Exception {
		Image logo = null;
		try {
			logo = Image.getInstance(new URL("https://studentzone-ngasce.nmims.edu/exam/resources_2015/images/nmims_logo.jpg"));
			//Image signature = Image.getInstance(new URL("http://admissions-ngasce.nmims.edu:4001/StudentDocuments/studentPhotos/signature.jpg"));

			logo.scaleAbsolute(142,40);

			SimpleDateFormat sdfr = new SimpleDateFormat("dd-MMM-yyyy");
			String fileDate = sdfr.format(new Date());
			Paragraph datePara = new Paragraph("Date: "+fileDate, font6);
			datePara.setAlignment(Element.ALIGN_RIGHT);
			document.add(datePara);

			PdfPTable headerTable = new PdfPTable(2);

			PdfPCell logoCell = new PdfPCell();
			logoCell.addElement(logo);
			logoCell.setHorizontalAlignment(Element.ALIGN_LEFT);

			Chunk collegNameChunk = new Chunk("NMIMS GLOBAL ACCESS � SCHOOL FOR CONTINUING EDUCATION", font3);
			collegNameChunk.setUnderline(0.1f, -2f);
			Paragraph collegeName = new Paragraph();
			collegeName.add(collegNameChunk);
			collegeName.setAlignment(Element.ALIGN_CENTER);
			//document.add(collegeName);

			PdfPCell collegeNameCell = new PdfPCell();
			collegeNameCell.addElement(collegeName);
			collegeNameCell.setVerticalAlignment(Element.ALIGN_MIDDLE);



			headerTable.addCell(logoCell);
			headerTable.addCell(collegeNameCell);

			headerTable.completeRow();
			headerTable.setWidthPercentage(100);
			float[] headerColumnWidths = {2f, 4f};
			headerTable.setWidths(headerColumnWidths);
			headerTable.setSpacingBefore(10);
			headerTable.setSpacingAfter(10);
			document.add(headerTable);

			String title = "ENTRY PASS-CUM-ACKNOWLEDGEMENT RECEIPT for Personal Contact Program";
						
			Chunk stmtOfMarksChunk = new Chunk(title, font2);
			stmtOfMarksChunk.setUnderline(0.1f, -2f);
			Paragraph stmtOfMarksPara = new Paragraph();
			stmtOfMarksPara.add(stmtOfMarksChunk);
			stmtOfMarksPara.setAlignment(Element.ALIGN_CENTER);
			document.add(stmtOfMarksPara);

		} catch (Exception e) {
			  
			throw e;
		}
	}

	public String createReceiptAndReturnFileName(List<PCPBookingTransactionBean> pcpBookings, String MARKSHEETS_PATH, StudentAcadsBean student) throws MalformedURLException, IOException, DocumentException {
		SimpleDateFormat sdfr = new SimpleDateFormat("dd-MMM-yyyy");
		String fileDate = sdfr.format(new Date());
		String fileName = student.getSapid()+"_PCP_Booking.pdf";
		String examPeriod = pcpBookings.get(0).getMonth().toUpperCase() + "-" + pcpBookings.get(0).getYear();
		String folderPath = MARKSHEETS_PATH + pcpBookings.get(0).getMonth() + pcpBookings.get(0).getYear();
		File file = new File(folderPath);
		if(!file.exists()){
			boolean createFile = file.mkdirs();
		}
		
		Image logo = Image.getInstance(new URL("https://studentzone-ngasce.nmims.edu/exam/resources_2015/images/logo.jpg"));

		logo.scaleAbsolute(284, 80);

		Document document = new Document(PageSize.A4);
		document.setMargins(20, 20, 60, 40);

		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(folderPath+"/"+fileName));
		
		document.open();

		try {
			document.add(logo);
			document.add(new Paragraph(" \n" , font4));
			
			Chunk collegNameChunk = new Chunk("NMIMS GLOBAL ACCESS � SCHOOL FOR CONTINUING EDUCATION", font3);
			collegNameChunk.setUnderline(0.1f, -2f);
			Paragraph collegeName = new Paragraph();
			collegeName.add(collegNameChunk);
			collegeName.setAlignment(Element.ALIGN_CENTER);
			document.add(collegeName);


			Chunk examPeriodChunk = new Chunk(examPeriod + " PCP CONFERENCING  RECEIPT", font2);
			examPeriodChunk.setUnderline(0.1f, -2f);
			Paragraph examPeriodPara = new Paragraph();
			examPeriodPara.add(examPeriodChunk);
			examPeriodPara.setAlignment(Element.ALIGN_CENTER);
			document.add(examPeriodPara);
			
			document.add(new Paragraph(" \n" , font4));
			
			
			document.add(new Paragraph("PCP Receipt Generation Date : "+fileDate, font4));
			
			PdfPTable bookingsTable = new PdfPTable(6);
			
			
			PdfPCell srNoCell = new PdfPCell(new Paragraph("Sr. No", font4));
			PdfPCell subjectsCell = new PdfPCell(new Paragraph("Subject", font4));
			PdfPCell semesterCell = new PdfPCell(new Paragraph("Sem", font4));
			PdfPCell programCell = new PdfPCell(new Paragraph("Program", font4));
			PdfPCell paymentModeCell = new PdfPCell(new Paragraph("Payment Mode", font4));
			PdfPCell examCenterCell = new PdfPCell(new Paragraph("Center Booked", font4));

			srNoCell.setFixedHeight(30);
			srNoCell.setVerticalAlignment(Element.ALIGN_TOP);
			subjectsCell.setVerticalAlignment(Element.ALIGN_TOP);
			semesterCell.setVerticalAlignment(Element.ALIGN_TOP);
			programCell.setVerticalAlignment(Element.ALIGN_TOP);

			paymentModeCell.setVerticalAlignment(Element.ALIGN_TOP);
			examCenterCell.setVerticalAlignment(Element.ALIGN_TOP);
			

			bookingsTable.addCell(srNoCell);
			bookingsTable.addCell(subjectsCell);
			bookingsTable.addCell(semesterCell);
			bookingsTable.addCell(programCell);

			bookingsTable.addCell(paymentModeCell);
			bookingsTable.addCell(examCenterCell);
			bookingsTable.completeRow();
			
			SimpleDateFormat dateformatter = new SimpleDateFormat("EEEE, dd-MMM-yyyy");
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			
			int count = 1;
			
			Integer amount = getExamFeesPaid(pcpBookings);
			
			document.add(new Paragraph("Student Name :  "+student.getFirstName()+" "+student.getLastName(), font4));
			document.add(new Paragraph("Student ID: "+student.getSapid(), font4));
			
			
			for (int j = 0; j < pcpBookings.size(); j++) {
				PCPBookingTransactionBean bean = pcpBookings.get(j);
				srNoCell = new PdfPCell(new Paragraph(count+"", font9));
				srNoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				
				subjectsCell = new PdfPCell(new Paragraph(bean.getSubject(), font9));
				
				semesterCell = new PdfPCell(new Paragraph(bean.getSem(), font9));
				semesterCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				
				programCell = new PdfPCell(new Paragraph(bean.getProgram(), font9));
				programCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				
				
				

				String paymentMode = bean.getPaymentMode();
				/*if("FREE".equalsIgnoreCase(paymentMode)){
					paymentMode = "Exam Fees part of Registration Fees/Exam Fees Exempted";
				}*/
				
				String centerId = bean.getCenterId();
				examCenterCell = new PdfPCell(new Paragraph(bean.getCenter(), font9));
				paymentModeCell = new PdfPCell(new Paragraph(paymentMode, font9));
				paymentModeCell.setHorizontalAlignment(Element.ALIGN_CENTER);

				bookingsTable.addCell(srNoCell);
				bookingsTable.addCell(subjectsCell);
				bookingsTable.addCell(semesterCell);
				bookingsTable.addCell(programCell);
				bookingsTable.addCell(paymentModeCell);
				bookingsTable.addCell(examCenterCell);
				bookingsTable.completeRow();


				count++;
			}

			bookingsTable.setWidthPercentage(100);
			bookingsTable.setSpacingBefore(15f);
			bookingsTable.setSpacingAfter(15f);
			float[] marksColumnWidths = {1f, 7f, 1.5f, 5.5f, 2.5f,2.5f};
			bookingsTable.setWidths(marksColumnWidths);
			document.add(bookingsTable);
			
			if(amount != null){
				document.add(new Paragraph("PCP Registration Fees Paid: INR. "+ amount +"/-", font4));
				document.add(new Paragraph("\n", font4));
				document.add(new Paragraph("\n", font4));
			}
			
			document.add(new Paragraph("Note:", font4));
			document.add(new Paragraph("1)  This document is NOT a PCP Entry Pass.", font4));
			document.add(new Paragraph("2)  This is an auto-generated PCP Booking Receipt and requires no signature.", font4));
			
		}catch(Exception e){
			  
		}

		document.close();
		return folderPath+"/"+fileName;
	}
	
	private Integer getExamFeesPaid(List<PCPBookingTransactionBean> pcpBookings) {
		Integer amount = 0;
		HashMap<String, String> trackIdMap = new HashMap<>();
		for (PCPBookingTransactionBean bean : pcpBookings) {
			if(!trackIdMap.containsKey(bean.getTrackId())){
				amount = amount + Integer.parseInt(bean.getAmount());
				trackIdMap.put(bean.getTrackId(), null);
			}
		}
		
		return amount;
	}
	
}
