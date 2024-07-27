package com.nmims.helpers;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.nmims.beans.CenterExamBean;
import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.beans.MBAExamBookingRequest;
import com.nmims.beans.MBAExamBookingRequest;
import com.nmims.beans.MarksheetBean;
import com.nmims.beans.PassFailExamBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.SubjectResultBean;
import com.nmims.beans.TimetableBean;
import com.nmims.daos.StudentMarksDAO;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
@Component
public class ExamBookingPDFCreator {

	
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
			String FEE_RECEIPT_PATH,
			StudentExamBean student, List<ExamBookingTransactionBean> confirmedOrReleasedExamBookings) throws FileNotFoundException, DocumentException, IOException, ParseException {
		// TODO Auto-generated method stub

		SimpleDateFormat sdfr = new SimpleDateFormat("dd-MMM-yyyy");
		/*String fileDate = sdfr.format(new Date());*/
		
		SimpleDateFormat fulldateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date ExamBookingDate = fulldateFormat.parse(examBookings.get(0).getYear()+"-01-01");
		Date enrollmentDate = null;
		for (ExamBookingTransactionBean ebean:examBookings) {
			
			if(ebean.getBookingCompleteTime()== null){
				enrollmentDate = new Date();
			}else{
				enrollmentDate = fulldateFormat.parse(ebean.getBookingCompleteTime());
			}
			
			if(ExamBookingDate.before(enrollmentDate)){
				ExamBookingDate	 = enrollmentDate;
			}
		}
		
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
			
			PdfPTable bookingsTable = new PdfPTable(8);
			
			
			PdfPCell srNoCell = new PdfPCell(new Paragraph("Sr. No", font4));
			PdfPCell subjectsCell = new PdfPCell(new Paragraph("Subjects", font4));
			PdfPCell semesterCell = new PdfPCell(new Paragraph("Sem", font4));
			PdfPCell dateCell = new PdfPCell(new Paragraph("Date", font4));
			PdfPCell startTimeCell = new PdfPCell(new Paragraph("Start Time", font4));
			PdfPCell endTimeCell = new PdfPCell(new Paragraph("End Time", font4));
			PdfPCell paymentModeCell = new PdfPCell(new Paragraph("Payment Mode", font4));
			PdfPCell examCenterCell = new PdfPCell(new Paragraph("Exam Center Booked", font4));

			srNoCell.setFixedHeight(30);
			srNoCell.setVerticalAlignment(Element.ALIGN_TOP);
			subjectsCell.setVerticalAlignment(Element.ALIGN_TOP);
			semesterCell.setVerticalAlignment(Element.ALIGN_TOP);
			dateCell.setVerticalAlignment(Element.ALIGN_TOP);
			startTimeCell.setVerticalAlignment(Element.ALIGN_TOP);
			endTimeCell.setVerticalAlignment(Element.ALIGN_TOP);
			paymentModeCell.setVerticalAlignment(Element.ALIGN_TOP);
			examCenterCell.setVerticalAlignment(Element.ALIGN_TOP);


			bookingsTable.addCell(srNoCell);
			bookingsTable.addCell(subjectsCell);
			bookingsTable.addCell(semesterCell);
			bookingsTable.addCell(dateCell);
			bookingsTable.addCell(startTimeCell);
			bookingsTable.addCell(endTimeCell);
			bookingsTable.addCell(paymentModeCell);
			bookingsTable.addCell(examCenterCell);
			bookingsTable.completeRow();
			
			SimpleDateFormat dateformatter = new SimpleDateFormat("EEEE, dd-MMM-yyyy");
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			
			int count = 1;
			
			Integer amount = getExamFeesPaid(confirmedOrReleasedExamBookings);
			
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
				
				String paymentMode = bean.getPaymentMode();
				if("FREE".equalsIgnoreCase(paymentMode)){
					paymentMode = "Exam Fees part of Registration Fees/Exam Fees Exempted";
				}
				
				String centerId = bean.getCenterId();
				examCenterCell = new PdfPCell(new Paragraph(examCenterIdNameMap.get(centerId), font9));
				paymentModeCell = new PdfPCell(new Paragraph(paymentMode, font9));
				paymentModeCell.setHorizontalAlignment(Element.ALIGN_CENTER);

				bookingsTable.addCell(srNoCell);
				bookingsTable.addCell(subjectsCell);
				bookingsTable.addCell(semesterCell);
				bookingsTable.addCell(dateCell);
				bookingsTable.addCell(startTimeCell);
				bookingsTable.addCell(endTimeCell);
				bookingsTable.addCell(paymentModeCell);
				bookingsTable.addCell(examCenterCell);
				bookingsTable.completeRow();

				
				count++;
				
				
			}

			bookingsTable.setWidthPercentage(100);
			bookingsTable.setSpacingBefore(15f);
			bookingsTable.setSpacingAfter(15f);
			float[] marksColumnWidths = {1f, 7f, 1.5f, 5.5f, 2.5f, 2.5f, 2.5f, 7f};
			bookingsTable.setWidths(marksColumnWidths);
			document.add(bookingsTable);
			
			if(amount != null){
				document.add(new Paragraph("Exam Fees Paid: INR. "+ amount +"/-", font4));
				document.add(new Paragraph("\n", font4));
				document.add(new Paragraph("\n", font4));
			}
			
			document.add(new Paragraph("Note:", font4));
			document.add(new Paragraph("1)  This document is NOT a Hall ticket.", font4));
//			document.add(new Paragraph("2)  Hall Ticket will be made available for download on Exam Portal 7 days prior to Exams.", font4));
//			document.add(new Paragraph("3)  NMIMS University reserves right to shift students to another available exam center in case of any unavoidable circumstances.", font4));
//			document.add(new Paragraph("4)  This is an auto-generated Exam Booking Receipt and requires no signature.", font4));
			document.add(new Paragraph("2)  NMIMS University reserves right to shift students to another available exam center in case of any unavoidable circumstances.", font4));
			document.add(new Paragraph("3)  This is an auto-generated Exam Booking Receipt and requires no signature.", font4));
			
		}catch(Exception e){
			
		}
		
		

		document.close();
		
		return studentSapidFolderPath+"/"+fileName;
		
	}

	public String createProjectBookedPDF(List<ExamBookingTransactionBean> examBookings, HashMap<String, String> examCenterIdNameMap, 
			String FEE_RECEIPT_PATH,StudentExamBean student, List<ExamBookingTransactionBean> confirmedOrReleasedExamBookings, String endDate) throws FileNotFoundException, DocumentException, IOException, ParseException {
		
		SimpleDateFormat sdfr = new SimpleDateFormat("dd-MMM-yyyy");
		
		SimpleDateFormat fulldateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date ExamBookingDate = fulldateFormat.parse(examBookings.get(0).getYear()+"-01-01");
		Date enrollmentDate = null;
		for (ExamBookingTransactionBean ebean:examBookings) {
			
			if(ebean.getBookingCompleteTime()== null){
				enrollmentDate = new Date();
			}else{
				enrollmentDate = fulldateFormat.parse(ebean.getBookingCompleteTime());
			}
			
			if(ExamBookingDate.before(enrollmentDate)){
				ExamBookingDate	 = enrollmentDate;
			}
		}
		
		String examPeriod = examBookings.get(0).getMonth().toUpperCase() + "-" + examBookings.get(0).getYear().toUpperCase();
		String fileName = student.getSapid()+"_ProjectBooking_"+examBookings.get(0).getMonth().toUpperCase()+"_"+examBookings.get(0).getYear().toUpperCase()+"_"+RandomStringUtils.randomAlphanumeric(6)+".pdf";
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


			Chunk examPeriodChunk = new Chunk(examPeriod + " PROJECT REGISTRATION BOOKING RECEIPT", font2);
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
			
			PdfPTable bookingsTable = new PdfPTable(8);
			
			
			PdfPCell srNoCell = new PdfPCell(new Paragraph("Sr. No", font4));
			PdfPCell subjectsCell = new PdfPCell(new Paragraph("Subjects", font4));
			PdfPCell semesterCell = new PdfPCell(new Paragraph("Sem", font4));
			PdfPCell examYearCell = new PdfPCell(new Paragraph("Exam Year", font4));
			PdfPCell examMonthCell = new PdfPCell(new Paragraph("Exam Month", font4));
			PdfPCell transactionStatusCell = new PdfPCell(new Paragraph("Transaction Status", font4));
			PdfPCell paymentModeCell = new PdfPCell(new Paragraph("Payment Mode", font4));
			PdfPCell bookingStatusCell = new PdfPCell(new Paragraph("Booking Status", font4));

			srNoCell.setFixedHeight(30);
			srNoCell.setVerticalAlignment(Element.ALIGN_TOP);
			subjectsCell.setVerticalAlignment(Element.ALIGN_TOP);
			semesterCell.setVerticalAlignment(Element.ALIGN_TOP);
			examYearCell.setVerticalAlignment(Element.ALIGN_TOP);
			examMonthCell.setVerticalAlignment(Element.ALIGN_TOP);
			transactionStatusCell.setVerticalAlignment(Element.ALIGN_TOP);
			paymentModeCell.setVerticalAlignment(Element.ALIGN_TOP);
			bookingStatusCell.setVerticalAlignment(Element.ALIGN_TOP);


			bookingsTable.addCell(srNoCell);
			bookingsTable.addCell(subjectsCell);
			bookingsTable.addCell(semesterCell);
			bookingsTable.addCell(examYearCell);
			bookingsTable.addCell(examMonthCell);
			bookingsTable.addCell(transactionStatusCell);
			bookingsTable.addCell(paymentModeCell);
			bookingsTable.addCell(bookingStatusCell);
			bookingsTable.completeRow();
			
			SimpleDateFormat dateformatter = new SimpleDateFormat("EEEE, dd-MMM-yyyy");
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			
			int count = 1;
			
			Integer amount = getExamFeesPaid(confirmedOrReleasedExamBookings);
			
			for (ExamBookingTransactionBean bean:examBookings) {
				
				srNoCell = new PdfPCell(new Paragraph(count+"", font9));
				srNoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				
				subjectsCell = new PdfPCell(new Paragraph(bean.getSubject(), font9));
				semesterCell = new PdfPCell(new Paragraph(bean.getSem(), font9));
				semesterCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				
				
				if("Project".equals(bean.getSubject())){
					examYearCell = new PdfPCell(new Paragraph(bean.getYear(), font9));
					examMonthCell = new PdfPCell(new Paragraph(bean.getMonth(), font9));
					transactionStatusCell = new PdfPCell(new Paragraph("Online Payment Successful", font9));
				}
				if("Module 4 - Project".equals(bean.getSubject())){
					examYearCell = new PdfPCell(new Paragraph(bean.getYear(), font9));
					examMonthCell = new PdfPCell(new Paragraph(bean.getMonth(), font9));
					transactionStatusCell = new PdfPCell(new Paragraph("Online Payment Successful", font9));
				}
				String paymentMode = bean.getPaymentMode();
				if("FREE".equalsIgnoreCase(paymentMode)){
					paymentMode = "Exam Fees part of Registration Fees/Exam Fees Exempted";
				}
				
				paymentModeCell = new PdfPCell(new Paragraph(paymentMode, font9));
				paymentModeCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				String bookingStatus = bean.getBooked();
				if(bookingStatus.equalsIgnoreCase("Y")){
					bookingStatus = "Booked";
				}else{
					bookingStatus = "Not Booked";
				}
				bookingStatusCell = new PdfPCell(new Paragraph(bookingStatus, font9));
				bookingStatusCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				
				bookingsTable.addCell(srNoCell);
				bookingsTable.addCell(subjectsCell);
				bookingsTable.addCell(semesterCell);
				bookingsTable.addCell(examYearCell);
				bookingsTable.addCell(examMonthCell);
				bookingsTable.addCell(transactionStatusCell);
				bookingsTable.addCell(paymentModeCell);
				bookingsTable.addCell(bookingStatusCell);
				bookingsTable.completeRow();

				
				count++;
				
				
			}

			bookingsTable.setWidthPercentage(100);
			bookingsTable.setSpacingBefore(15f);
			bookingsTable.setSpacingAfter(15f);
			float[] marksColumnWidths = {1f, 5f, 1.5f, 3.5f, 2.5f, 5.5f, 2.5f, 5f};
			bookingsTable.setWidths(marksColumnWidths);
			document.add(bookingsTable);
			
			if(amount != null){
				document.add(new Paragraph("Exam Fees Paid: INR. "+ amount +"/-", font4));
				document.add(new Paragraph("\n", font4));
				document.add(new Paragraph("\n", font4));
			}
			
			document.add(new Paragraph("Note:", font4));
			document.add(new Paragraph("1)  For "+examPeriod+" exam cycle - Last date of Project submission is "+endDate +" on or before 23.59hrs. (IST)", font4));
			document.add(new Paragraph("2)  In case of non-submission of Project after Project Registration and Payment of Fees: Exam Fees paid will not be refunded nor will be carry forwarded to next scheduled exam cycle. The student will be marked �Absent� in the Mark sheet issued.", font4));
			document.add(new Paragraph("3)  This is an auto generated Project Registration Receipt and requires no signature.", font4));
			
		}catch(Exception e){
			
		}
		
		

		document.close();
		
		return examBookings.get(0).getMonth()+examBookings.get(0).getYear()+"/"+fileName;
		
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

	public String createPDF_MBAWX(List<MBAExamBookingRequest> examBookings, List<MBAExamBookingRequest> approvedBookings, String FEE_RECEIPT_PATH, StudentExamBean student) throws FileNotFoundException, DocumentException, IOException, ParseException {

		
		SimpleDateFormat sdfr = new SimpleDateFormat("dd-MMM-yyyy");
		/*String fileDate = sdfr.format(new Date());*/
		
		SimpleDateFormat fulldateFormat = new SimpleDateFormat("yyyy-MM-dd");
//		Date ExamBookingDate = fulldateFormat.parse(examBookings.get(0).getYear()+"-01-01");
		Date ExamBookingDate = new Date();
		Date enrollmentDate = null;

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
			
			PdfPTable bookingsTable = new PdfPTable(8);
			
			
			PdfPCell srNoCell = new PdfPCell(new Paragraph("Sr. No", font4));
			PdfPCell subjectsCell = new PdfPCell(new Paragraph("Subjects", font4));
			PdfPCell semesterCell = new PdfPCell(new Paragraph("Sem", font4));
			PdfPCell dateCell = new PdfPCell(new Paragraph("Date", font4));
			PdfPCell startTimeCell = new PdfPCell(new Paragraph("Start Time", font4));
			PdfPCell endTimeCell = new PdfPCell(new Paragraph("End Time", font4));
			PdfPCell paymentModeCell = new PdfPCell(new Paragraph("Payment Mode", font4));
			PdfPCell examCenterCell = new PdfPCell(new Paragraph("Exam Center Booked", font4));

			srNoCell.setFixedHeight(30);
			srNoCell.setVerticalAlignment(Element.ALIGN_TOP);
			subjectsCell.setVerticalAlignment(Element.ALIGN_TOP);
			semesterCell.setVerticalAlignment(Element.ALIGN_TOP);
			dateCell.setVerticalAlignment(Element.ALIGN_TOP);
			startTimeCell.setVerticalAlignment(Element.ALIGN_TOP);
			endTimeCell.setVerticalAlignment(Element.ALIGN_TOP);
			paymentModeCell.setVerticalAlignment(Element.ALIGN_TOP);
			examCenterCell.setVerticalAlignment(Element.ALIGN_TOP);


			bookingsTable.addCell(srNoCell);
			bookingsTable.addCell(subjectsCell);
			bookingsTable.addCell(semesterCell);
			bookingsTable.addCell(dateCell);
			bookingsTable.addCell(startTimeCell);
			bookingsTable.addCell(endTimeCell);
			bookingsTable.addCell(paymentModeCell);
			bookingsTable.addCell(examCenterCell);
			bookingsTable.completeRow();


			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy");
			SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");
			
			int count = 1;
			
			Integer amount = getExamFeesPaid_MBAWX(approvedBookings);
			
			for (MBAExamBookingRequest bean : examBookings) {
				
				srNoCell = new PdfPCell(new Paragraph(count+"", font9));
				srNoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				
				subjectsCell = new PdfPCell(new Paragraph(bean.getSubjectName(), font9));
				semesterCell = new PdfPCell(new Paragraph(bean.getTerm(), font9));
				semesterCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				

				Date startDateTime = formatter.parse(bean.getExamStartDateTime());
				Date endDateTime = formatter.parse(bean.getExamEndDateTime());
				
				String examDate = dateFormatter.format(startDateTime);
				String startTime = timeFormatter.format(startDateTime);
				String endTime = timeFormatter.format(endDateTime);
				
				dateCell = new PdfPCell(new Paragraph(examDate, font9));
				startTimeCell = new PdfPCell(new Paragraph(startTime, font9));
				endTimeCell = new PdfPCell(new Paragraph(endTime, font9));

				String paymentMode = "Online";
				examCenterCell = new PdfPCell(new Paragraph(bean.getCenterName(), font9));
				paymentModeCell = new PdfPCell(new Paragraph(paymentMode, font9));
				paymentModeCell.setHorizontalAlignment(Element.ALIGN_CENTER);

				bookingsTable.addCell(srNoCell);
				bookingsTable.addCell(subjectsCell);
				bookingsTable.addCell(semesterCell);
				bookingsTable.addCell(dateCell);
				bookingsTable.addCell(startTimeCell);
				bookingsTable.addCell(endTimeCell);
				bookingsTable.addCell(paymentModeCell);
				bookingsTable.addCell(examCenterCell);
				bookingsTable.completeRow();

				
				count++;
				
				
			}

			bookingsTable.setWidthPercentage(100);
			bookingsTable.setSpacingBefore(15f);
			bookingsTable.setSpacingAfter(15f);
			float[] marksColumnWidths = {1f, 7f, 1.5f, 5.5f, 2.5f, 2.5f, 2.5f, 7f};
			bookingsTable.setWidths(marksColumnWidths);
			document.add(bookingsTable);
			
			if(amount != null){
				document.add(new Paragraph("Exam Fees Paid: INR. "+ amount +"/-", font4));
				document.add(new Paragraph("\n", font4));
				document.add(new Paragraph("\n", font4));
			}
			
			document.add(new Paragraph("Note:", font4));
			document.add(new Paragraph("1)  This document is NOT a Hall ticket.", font4));
			document.add(new Paragraph("2)  Hall Ticket will be made available for download on Exam Portal 1 day prior to Exams.", font4));
			document.add(new Paragraph("3)  NMIMS University reserves right to shift students to another available exam center in case of any unavoidable circumstances.", font4));
			document.add(new Paragraph("4)  This is an auto-generated Exam Booking Receipt and requires no signature.", font4));
			
		}catch(Exception e){
			
		}
		
		

		document.close();
		
		return studentSapidFolderPath+"/"+fileName;
		
	}
	
	private Integer getExamFeesPaid_MBAWX(List<MBAExamBookingRequest> approvedBookings) {
		Integer amount = 0;
		Map<String, String> trackIdAmountMap = new HashMap<String, String>();
		for (MBAExamBookingRequest bean : approvedBookings) {
			String trackId = bean.getTrackId();
			String amountPaid = bean.getAmount();
			if(trackId != null && amountPaid != null && StringUtils.isNumeric(amountPaid) && !trackIdAmountMap.containsKey(bean.getTrackId())) {
				amount = amount + Integer.parseInt(bean.getAmount());
				trackIdAmountMap.put(trackId, amountPaid);
			}
		}
		return amount;
	}

	public String createPDF_MBAX(List<MBAExamBookingRequest> examBookings, List<MBAExamBookingRequest> approvedBookings, String FEE_RECEIPT_PATH, StudentExamBean student) throws FileNotFoundException, DocumentException, IOException, ParseException {

		SimpleDateFormat sdfr = new SimpleDateFormat("dd-MMM-yyyy");
		/*String fileDate = sdfr.format(new Date());*/
		
		SimpleDateFormat fulldateFormat = new SimpleDateFormat("yyyy-MM-dd");
//		Date ExamBookingDate = fulldateFormat.parse(examBookings.get(0).getYear()+"-01-01");
		Date ExamBookingDate = new Date();

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
		
		PdfPTable bookingsTable = new PdfPTable(8);
		
		
		PdfPCell srNoCell = new PdfPCell(new Paragraph("Sr. No", font4));
		PdfPCell subjectsCell = new PdfPCell(new Paragraph("Subjects", font4));
		PdfPCell semesterCell = new PdfPCell(new Paragraph("Sem", font4));
		PdfPCell dateCell = new PdfPCell(new Paragraph("Date", font4));
		PdfPCell startTimeCell = new PdfPCell(new Paragraph("Start Time", font4));
		PdfPCell endTimeCell = new PdfPCell(new Paragraph("End Time", font4));
		PdfPCell paymentModeCell = new PdfPCell(new Paragraph("Payment Mode", font4));
		PdfPCell examCenterCell = new PdfPCell(new Paragraph("Exam Center Booked", font4));

		srNoCell.setFixedHeight(30);
		srNoCell.setVerticalAlignment(Element.ALIGN_TOP);
		subjectsCell.setVerticalAlignment(Element.ALIGN_TOP);
		semesterCell.setVerticalAlignment(Element.ALIGN_TOP);
		dateCell.setVerticalAlignment(Element.ALIGN_TOP);
		startTimeCell.setVerticalAlignment(Element.ALIGN_TOP);
		endTimeCell.setVerticalAlignment(Element.ALIGN_TOP);
		paymentModeCell.setVerticalAlignment(Element.ALIGN_TOP);
		examCenterCell.setVerticalAlignment(Element.ALIGN_TOP);


		bookingsTable.addCell(srNoCell);
		bookingsTable.addCell(subjectsCell);
		bookingsTable.addCell(semesterCell);
		bookingsTable.addCell(dateCell);
		bookingsTable.addCell(startTimeCell);
		bookingsTable.addCell(endTimeCell);
		bookingsTable.addCell(paymentModeCell);
		bookingsTable.addCell(examCenterCell);
		bookingsTable.completeRow();


		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy");
		SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");
		
		int count = 1;
		
		Integer amount = getExamFeesPaid_MBAX(approvedBookings);
		
		for (MBAExamBookingRequest bean : examBookings) {
			
			srNoCell = new PdfPCell(new Paragraph(count+"", font9));
			srNoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			
			subjectsCell = new PdfPCell(new Paragraph(bean.getSubjectName(), font9));
			semesterCell = new PdfPCell(new Paragraph(bean.getTerm(), font9));
			semesterCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			

			Date startDateTime = formatter.parse(bean.getExamStartDateTime());
			Date endDateTime = formatter.parse(bean.getExamEndDateTime());
			
			String examDate = dateFormatter.format(startDateTime);
			String startTime = timeFormatter.format(startDateTime);
			String endTime = timeFormatter.format(endDateTime);
			
			dateCell = new PdfPCell(new Paragraph(examDate, font9));
			startTimeCell = new PdfPCell(new Paragraph(startTime, font9));
			endTimeCell = new PdfPCell(new Paragraph(endTime, font9));

			String paymentMode = "Online";
			examCenterCell = new PdfPCell(new Paragraph(bean.getCenterName(), font9));
			paymentModeCell = new PdfPCell(new Paragraph(paymentMode, font9));
			paymentModeCell.setHorizontalAlignment(Element.ALIGN_CENTER);

			bookingsTable.addCell(srNoCell);
			bookingsTable.addCell(subjectsCell);
			bookingsTable.addCell(semesterCell);
			bookingsTable.addCell(dateCell);
			bookingsTable.addCell(startTimeCell);
			bookingsTable.addCell(endTimeCell);
			bookingsTable.addCell(paymentModeCell);
			bookingsTable.addCell(examCenterCell);
			bookingsTable.completeRow();

			
			count++;
			
			
		}

		bookingsTable.setWidthPercentage(100);
		bookingsTable.setSpacingBefore(15f);
		bookingsTable.setSpacingAfter(15f);
		float[] marksColumnWidths = {1f, 7f, 1.5f, 5.5f, 2.5f, 2.5f, 2.5f, 7f};
		bookingsTable.setWidths(marksColumnWidths);
		document.add(bookingsTable);
		
		if(amount != null){
			document.add(new Paragraph("Exam Fees Paid: INR. "+ amount +"/-", font4));
			document.add(new Paragraph("\n", font4));
			document.add(new Paragraph("\n", font4));
		}
		
		document.add(new Paragraph("Note:", font4));
		document.add(new Paragraph("1)  This document is NOT a Hall ticket.", font4));
		document.add(new Paragraph("2)  Hall Ticket will be made available for download on Exam Portal one day prior to Exams.", font4));
		document.add(new Paragraph("3)  NMIMS University reserves right to shift students to another available exam center in case of any unavoidable circumstances.", font4));
		document.add(new Paragraph("4)  This is an auto-generated Exam Booking Receipt and requires no signature.", font4));
		
		

		document.close();
		
		return studentSapidFolderPath+"/"+fileName;
		
	}
	
	private Integer getExamFeesPaid_MBAX(List<MBAExamBookingRequest> approvedBookings) {
		Integer amount = 0;
		Map<String, String> trackIdAmountMap = new HashMap<String, String>();
		for (MBAExamBookingRequest bean : approvedBookings) {
			String trackId = bean.getTrackId();
			String amountPaid = bean.getAmount();
			if(trackId != null && amountPaid != null && StringUtils.isNumeric(amountPaid) && !trackIdAmountMap.containsKey(bean.getTrackId())) {
				amount = amount + Integer.parseInt(bean.getAmount());
				trackIdAmountMap.put(trackId, amountPaid);
			}
		}
		return amount;
	}
	public String createAssignmentPDF(List<ExamBookingTransactionBean> examBookings, 
			String FEE_RECEIPT_PATH,
			StudentExamBean student) throws FileNotFoundException, DocumentException, IOException, ParseException {
		// TODO Auto-generated method stub
 
		SimpleDateFormat sdfr = new SimpleDateFormat("dd-MMM-yyyy");
		String fileDate = sdfr.format(new Date());
		
		//SimpleDateFormat fulldateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date ExamBookingDate = sdfr.parse(fileDate);
	
		String examPeriod = examBookings.get(0).getMonth().toUpperCase() + "-" + examBookings.get(0).getYear().toUpperCase();
		String fileName = student.getSapid()+"_Booking_"+examBookings.get(0).getMonth().toUpperCase()+"_"+examBookings.get(0).getYear().toUpperCase()+"_"+RandomStringUtils.randomAlphanumeric(6)+".pdf";
		Image logo = Image.getInstance(new URL("https://staticfilesexam.s3.ap-south-1.amazonaws.com/resources_2015/images/logo.jpg"));
		//Check in Fee Receipt folder if this folder by sapid exists. If no then create one//
		String studentSapidFolderPath = FEE_RECEIPT_PATH + examBookings.get(0).getMonth()+examBookings.get(0).getYear();
		
		File folderPath = new File(studentSapidFolderPath);
		if (!folderPath.exists()) {
			// Making Folder
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
 
			Chunk examPeriodChunk = new Chunk("Assignment Fee Receipt for "+examPeriod+" Re-Sit Term End Examination", font2);
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
			
			PdfPTable bookingsTable = new PdfPTable(8);
			 
			PdfPCell srNoCell = new PdfPCell(new Paragraph("Sr. No", font4));
			PdfPCell subjectsCell = new PdfPCell(new Paragraph("Subjects", font4));
			PdfPCell semesterCell = new PdfPCell(new Paragraph("Sem", font4));
			PdfPCell examYearCell = new PdfPCell(new Paragraph("Exam Year", font4));
			PdfPCell examMonthCell = new PdfPCell(new Paragraph("Exam Month", font4));
			PdfPCell transactionStatusCell = new PdfPCell(new Paragraph("Transaction Status", font4));
			PdfPCell bookingStatusCell = new PdfPCell(new Paragraph("Booking Status", font4));
			PdfPCell amountCell = new PdfPCell(new Paragraph("Amount", font4)); 
			
			srNoCell.setFixedHeight(30);
			srNoCell.setVerticalAlignment(Element.ALIGN_TOP);
			subjectsCell.setVerticalAlignment(Element.ALIGN_TOP);
			semesterCell.setVerticalAlignment(Element.ALIGN_TOP);
			examYearCell.setVerticalAlignment(Element.ALIGN_TOP);
			examMonthCell.setVerticalAlignment(Element.ALIGN_TOP);
			transactionStatusCell.setVerticalAlignment(Element.ALIGN_TOP); 
			bookingStatusCell.setVerticalAlignment(Element.ALIGN_TOP);
			amountCell.setVerticalAlignment(Element.ALIGN_TOP); 

			bookingsTable.addCell(srNoCell);
			bookingsTable.addCell(subjectsCell);
			bookingsTable.addCell(semesterCell);
			bookingsTable.addCell(amountCell);
			bookingsTable.addCell(examYearCell);
			bookingsTable.addCell(examMonthCell);
			bookingsTable.addCell(transactionStatusCell); 
			bookingsTable.addCell(bookingStatusCell);
			bookingsTable.completeRow();
			
			 
			int count = 1;
			 
			for (ExamBookingTransactionBean bean:examBookings) {
				
				srNoCell = new PdfPCell(new Paragraph(count+"", font9));
				srNoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				String paymentMode = bean.getPaymentMode();
				String bookingStatus = bean.getBooked();
				if(bookingStatus.equalsIgnoreCase("Y")){
					bookingStatus = "Booked";
				}else{
					bookingStatus = "Not Booked";
				}
				subjectsCell = new PdfPCell(new Paragraph(bean.getSubject(), font9));
				semesterCell = new PdfPCell(new Paragraph(bean.getSem(), font9));
				semesterCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				amountCell = new PdfPCell(new Paragraph(bean.getAmount()+"", font9));
				examYearCell = new PdfPCell(new Paragraph(bean.getYear(), font9));
				examMonthCell = new PdfPCell(new Paragraph(bean.getMonth(), font9));
				transactionStatusCell = new PdfPCell(new Paragraph(bean.getTranStatus(), font9));
				bookingStatusCell = new PdfPCell(new Paragraph(bookingStatus, font9));
				bookingStatusCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				bookingsTable.addCell(srNoCell);
				bookingsTable.addCell(subjectsCell);
				bookingsTable.addCell(semesterCell);
				bookingsTable.addCell(amountCell);
				bookingsTable.addCell(examYearCell);
				bookingsTable.addCell(examMonthCell);
				bookingsTable.addCell(transactionStatusCell); 
				bookingsTable.addCell(bookingStatusCell); 
				bookingsTable.completeRow();  
				count++;  
			}

			bookingsTable.setWidthPercentage(100);
			bookingsTable.setSpacingBefore(15f);
			bookingsTable.setSpacingAfter(15f);
			float[] marksColumnWidths = {1f, 7f, 2f, 3.5f, 2.5f, 2.5f, 5.5f, 5f};
			bookingsTable.setWidths(marksColumnWidths);
			document.add(bookingsTable); 
			document.add(new Paragraph("Note:", font4));
			document.add(new Paragraph("1)  This is an auto-generated Exam Booking Receipt and requires no signature.", font4));
			
		}catch(Exception e){
			
		} 
		document.close(); 
		return examBookings.get(0).getMonth()+examBookings.get(0).getYear()+"/"+fileName;
		
	}
}
