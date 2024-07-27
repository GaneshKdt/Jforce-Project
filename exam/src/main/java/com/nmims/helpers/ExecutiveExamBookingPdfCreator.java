package com.nmims.helpers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.beans.StudentExamBean;
@Component
public class ExecutiveExamBookingPdfCreator {

	
	@Autowired(required=false)
	ApplicationContext act;

	Font font3 = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
	Font font2 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
	Font font4 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
	Font font1 = new Font(Font.FontFamily.TIMES_ROMAN, 12);
	Font font5 = new Font(Font.FontFamily.TIMES_ROMAN, 6);
	Font font6 = new Font(Font.FontFamily.TIMES_ROMAN, 8);
	Font font7 = new Font(Font.FontFamily.TIMES_ROMAN, 18);
	Font font8 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
	Font font9 = new Font(Font.FontFamily.TIMES_ROMAN, 10);

	public void test(){}
	
	
	public String createPDF(List<ExamBookingTransactionBean> examBookings, 
			HashMap<String, String> examCenterIdNameMap, 
			String FEE_RECEIPT_PATH
			,StudentExamBean student //, List<ExamBookingTransactionBean> confirmedOrReleasedExamBookings
			) throws FileNotFoundException, DocumentException, IOException, ParseException {
		// TODO Auto-generated method stub

		SimpleDateFormat sdfr = new SimpleDateFormat("dd-MMM-yyyy");
		/*String fileDate = sdfr.format(new Date());*/
		
		SimpleDateFormat fulldateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date ExamBookingDate = fulldateFormat.parse(examBookings.get(0).getYear()+"-01-01");
		Date enrollmentDate = new Date();
		/*for (ExecutiveBean ebean:examBookings) {
			
			//if(ebean.getBookingCompleteTime()== null){
				enrollmentDate = new Date();
			}else{
				enrollmentDate = fulldateFormat.parse(ebean.getBookingCompleteTime());
			}
			
			if(ExamBookingDate.before(enrollmentDate)){
				ExamBookingDate	 = enrollmentDate;
			}
		}*/
		ExamBookingDate	 = enrollmentDate;
		
		String examPeriod = examBookings.get(0).getMonth().toUpperCase() + "-" + examBookings.get(0).getYear().toUpperCase();
		String fileName = student.getSapid()+"_Booking_"+examBookings.get(0).getMonth().toUpperCase()+"_"+examBookings.get(0).getYear().toUpperCase()+"_"+RandomStringUtils.randomAlphanumeric(6)+".pdf";
		Image logo = Image.getInstance(new URL("https://staticfilesexam.s3.ap-south-1.amazonaws.com/resources_2015/images/logo.jpg"));
		//Check in Fee Receipt folder if this folder by sapid exists. If no then create one//
		String studentSapidFolderPath = FEE_RECEIPT_PATH + examBookings.get(0).getMonth()+examBookings.get(0).getYear();
		
		File folderPath = new File(studentSapidFolderPath);
		if (!folderPath.exists()) {
			boolean created = folderPath.mkdirs();
		}
		
		logo.scaleAbsolute(284, 80);

		Document document = new Document(PageSize.A4);
		document.setMargins(20, 20, 60, 40);

		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(studentSapidFolderPath+"/"+fileName));
		
		document.open();

		try {
			document.add(logo);
			document.add(new Paragraph(" \n" , font4));
			
			Chunk collegNameChunk = new Chunk("NMIMS GLOBAL ACCESS - SCHOOL FOR CONTINUING EDUCATION", font3);
			collegNameChunk.setUnderline(0.1f, -2f);
			Paragraph collegeName = new Paragraph();
			collegeName.add(collegNameChunk);
			collegeName.setAlignment(Element.ALIGN_CENTER);
			document.add(collegeName);


			Chunk examPeriodChunk = new Chunk(examPeriod + " EXAM BOOKING RECEIPT", font2);
			examPeriodChunk.setUnderline(0.1f, -2f);
			Paragraph examPeriodPara = new Paragraph();
			examPeriodPara.add(examPeriodChunk);
			examPeriodPara.setAlignment(Element.ALIGN_CENTER);
			document.add(examPeriodPara);
			
			document.add(new Paragraph(" \n" , font4));
			
			document.add(new Paragraph("Student Name :  "+student.getFirstName()+" "+student.getLastName(), font4));
			document.add(new Paragraph("Student ID: "+student.getSapid(), font4));
			document.add(new Paragraph("Program :  "+student.getProgram(), font4));
			document.add(new Paragraph("Exam Receipt Generation Date : "+sdfr.format(ExamBookingDate), font4));
			
			PdfPTable bookingsTable = new PdfPTable(7);
			
			
			PdfPCell srNoCell = new PdfPCell(new Paragraph("Sr. No", font4));
			PdfPCell subjectsCell = new PdfPCell(new Paragraph("Subjects", font4));
			PdfPCell semesterCell = new PdfPCell(new Paragraph("Sem", font4));
			PdfPCell dateCell = new PdfPCell(new Paragraph("Date", font4));
			PdfPCell startTimeCell = new PdfPCell(new Paragraph("Start Time", font4));
			PdfPCell endTimeCell = new PdfPCell(new Paragraph("End Time", font4));
			/*PdfPCell paymentModeCell = new PdfPCell(new Paragraph("Payment Mode", font4));*/
			PdfPCell examCenterCell = new PdfPCell(new Paragraph("Exam Center Booked", font4));

			srNoCell.setFixedHeight(30);
			srNoCell.setVerticalAlignment(Element.ALIGN_TOP);
			subjectsCell.setVerticalAlignment(Element.ALIGN_TOP);
			semesterCell.setVerticalAlignment(Element.ALIGN_TOP);
			dateCell.setVerticalAlignment(Element.ALIGN_TOP);
			startTimeCell.setVerticalAlignment(Element.ALIGN_TOP);
			endTimeCell.setVerticalAlignment(Element.ALIGN_TOP);
			/*paymentModeCell.setVerticalAlignment(Element.ALIGN_TOP);*/
			examCenterCell.setVerticalAlignment(Element.ALIGN_TOP);


			bookingsTable.addCell(srNoCell);
			bookingsTable.addCell(subjectsCell);
			bookingsTable.addCell(semesterCell);
			bookingsTable.addCell(dateCell);
			bookingsTable.addCell(startTimeCell);
			bookingsTable.addCell(endTimeCell);
			/*bookingsTable.addCell(paymentModeCell);*/
			bookingsTable.addCell(examCenterCell);
			bookingsTable.completeRow();
			
			SimpleDateFormat dateformatter = new SimpleDateFormat("EEEE, dd-MMM-yyyy");
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			
			int count = 1;
			
			//Integer amount = getExamFeesPaid(confirmedOrReleasedExamBookings);
			Integer amount = 1500;
			for (ExamBookingTransactionBean bean:examBookings) {
				
				srNoCell = new PdfPCell(new Paragraph(count+"", font9));
				srNoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				
				subjectsCell = new PdfPCell(new Paragraph(bean.getSubject(), font9));
				semesterCell = new PdfPCell(new Paragraph(bean.getSem(), font9));
				semesterCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				
				
				if("Project".equals(bean.getSubject())){
					dateCell = new PdfPCell(new Paragraph("NA", font9));
					startTimeCell = new PdfPCell(new Paragraph("NA", font9));
					endTimeCell = new PdfPCell(new Paragraph("NA", font9));
				}else if("Module 4 - Project".equals(bean.getSubject())){
					dateCell = new PdfPCell(new Paragraph("NA", font9));
					startTimeCell = new PdfPCell(new Paragraph("NA", font9));
					endTimeCell = new PdfPCell(new Paragraph("NA", font9));
				}else{
					Date formattedDate = formatter.parse(bean.getExamDate());
					String examDate = dateformatter.format(formattedDate);
					
					dateCell = new PdfPCell(new Paragraph(examDate, font9));
					startTimeCell = new PdfPCell(new Paragraph(bean.getExamTime(), font9));
					endTimeCell = new PdfPCell(new Paragraph(bean.getExamEndTime(), font9));
				}
				
				String paymentMode = "Online";
				/*if("FREE".equalsIgnoreCase(paymentMode)){
					paymentMode = "Exam Fees part of Registration Fees/Exam Fees Exempted";
				}*/
				
				String centerId = bean.getCenterId().toString();
				examCenterCell = new PdfPCell(new Paragraph(examCenterIdNameMap.get(centerId), font9));
				/*paymentModeCell = new PdfPCell(new Paragraph(paymentMode, font9));
				paymentModeCell.setHorizontalAlignment(Element.ALIGN_CENTER);*/

				bookingsTable.addCell(srNoCell);
				bookingsTable.addCell(subjectsCell);
				bookingsTable.addCell(semesterCell);
				bookingsTable.addCell(dateCell);
				bookingsTable.addCell(startTimeCell);
				bookingsTable.addCell(endTimeCell);
				/*bookingsTable.addCell(paymentModeCell);*/
				bookingsTable.addCell(examCenterCell);
				bookingsTable.completeRow();

				
				count++;
				
				
			}

			bookingsTable.setWidthPercentage(100);
			bookingsTable.setSpacingBefore(15f);
			bookingsTable.setSpacingAfter(15f);
			float[] marksColumnWidths = {1f, 7f, 1.5f, 5.5f, 2.5f, 2.5f,  7f};
			bookingsTable.setWidths(marksColumnWidths);
			document.add(bookingsTable);
			
			if(amount != null){
				document.add(new Paragraph(" ", font4));
				document.add(new Paragraph("\n", font4));
				document.add(new Paragraph("\n", font4));
			}
			
			document.add(new Paragraph("Note:", font4));
			document.add(new Paragraph("1)  This document is NOT a Hall ticket.", font4));
			document.add(new Paragraph("2)  Hall Ticket will be made available for download on Exam Portal 3 days prior to Exams.", font4));
			document.add(new Paragraph("3)  NMIMS University reserves right to shift students to another available exam center in case of any unavoidable circumstances.", font4));
			document.add(new Paragraph("4)  This is an auto-generated Exam Booking Summary and requires no signature.", font4));
			
		}catch(Exception e){
			
		}
		
		

		document.close();
		
		return studentSapidFolderPath+"/"+fileName;
		
	}

	private Integer getExamFeesPaid(List<ExamBookingTransactionBean> confirmedOrReleasedExamBookings) {
		Integer amount = 0;
		HashMap<String, String> trackIdMap = new HashMap<>();
		for (ExamBookingTransactionBean bean : confirmedOrReleasedExamBookings) {
			if(!trackIdMap.containsKey(bean.getTrackId())){
				amount = amount + Integer.parseInt(bean.getAmount());
				trackIdMap.put(bean.getTrackId(), null);
			}
		}
		
		return amount;
	}

	

	


	

	
}
