package com.nmims.helpers;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.nmims.beans.CenterStudentPortalBean;
import com.nmims.beans.EmbaPassFailStudentPortalBean;
import com.nmims.beans.ExamBookingTransactionStudentPortalBean;
import com.nmims.beans.PassFailBean;
import com.nmims.beans.ServiceRequestStudentPortal;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.controllers.BaseController;
import com.nmims.daos.ServiceRequestDao;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
@Component 
public class CreatePDF {
	
	@Value("${SERVER_PATH}")
	private String SERVER_PATH; 
	
	@Autowired(required=false)
	ApplicationContext act;

	@Autowired
	private ServiceRequestDao serviceRequestDao;
	
	private String SERVICE_REQUEST_FILES_PATH="D:/SR/withdrawal/";
	
	Font font3 = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
	Font font2 = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD);
	Font font4 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
	Font font1 = new Font(Font.FontFamily.TIMES_ROMAN, 11);
	Font font5 = new Font(Font.FontFamily.TIMES_ROMAN, 6);
	Font font6 = new Font(Font.FontFamily.TIMES_ROMAN, 8);
	Font font7 = new Font(Font.FontFamily.TIMES_ROMAN, 18);
	Font font8 = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD);
	Font font9 = new Font(Font.FontFamily.TIMES_ROMAN, 10);
	Font font10 = new Font(Font.FontFamily.TIMES_ROMAN, (float) 9.5, Font.BOLD);
	Font headerFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
	Font descBoldFont = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.BOLD);
	Font descFont = new Font(Font.FontFamily.TIMES_ROMAN, 9);
	Font footerBoldFont = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.BOLD);
	
	private static final Logger logger = LoggerFactory.getLogger(CreatePDF.class);
	
	public void test(){}
	
	public void createPDF(String sapid) throws Exception {
		Document document = new Document(PageSize.A4);
		document.setMargins(80, 50, 50, 30);
		String fileName="Withdrawal_Form";
		System.out.println("SERVICE_REQUEST_FILES_PATH:"+SERVICE_REQUEST_FILES_PATH);
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(SERVICE_REQUEST_FILES_PATH+fileName));
		document.open();
		List<String> sapidList = Arrays.asList(sapid.split(","));
		System.out.println("sapidList:"+sapidList);
		for(String sapId:sapidList) {
			System.out.println("sapid"+sapId);
			ServiceRequestStudentPortal sr =serviceRequestDao.findSRBySapIdAndType(sapId,"Program Withdrawal");	
			StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(sapId);
			
			generateHeaderTable(document);
			generateStudentInfoTable(document, student);
			generateWithdrawalReasonTable(document,sr);
			generateDeclaration(document);
			generateApprovalFied(document);
			
		}
		document.close(); 
	}
	 public void generateApprovalFied(Document document) throws DocumentException {
		Chunk section = new Chunk("Approved by:", descFont);
		Paragraph sectionPara = new Paragraph(section);
		sectionPara.setAlignment(Element.ALIGN_LEFT); 
		sectionPara.setSpacingBefore(10f);	
		sectionPara.setSpacingAfter(20f);	
		document.add(sectionPara);  
		Chunk aprov = new Chunk("___________________________________", descFont);
		Paragraph aprovPara = new Paragraph(aprov);
		aprovPara.setAlignment(Element.ALIGN_LEFT); 
		document.add(aprovPara);   
		Chunk ngasce = new Chunk("NGA- SCE", descFont);
		Paragraph ngascePara = new Paragraph(ngasce);
		ngascePara.setAlignment(Element.ALIGN_LEFT);
		document.add(ngascePara);    
	}

	 public void generateDeclaration(Document document) throws DocumentException {
		Chunk section = new Chunk("Section 3 � Declaration:", descFont);
		Paragraph sectionPara = new Paragraph();
		sectionPara.add(section);	
		sectionPara.setAlignment(Element.ALIGN_LEFT); 
		sectionPara.setSpacingAfter(8);	
		document.add(sectionPara);  
		
		
		PdfPTable declTable = new PdfPTable(1); // 2 columns.
		//declTable.setHorizontalAlignment(Element.ALIGN_LEFT);
		
		PdfPTable sigTable = new PdfPTable(1); // 2 columns.
		
		Chunk declChunk = new Chunk("I hereby agree and accept to completely withdraw from my program of study offered by " + 
				"SVKM�s NMIMS � NMIMS Global Access � School for Continuing Education. I also agree that in case " + 
				"of any dispute or differences about this withdrawal, the decision of the SVKM�s NMIMS � NMIMS Global " + 
				"Access � School for Continuing Education will be final and binding on me. I am aware that No fees pending " + 
				"or otherwise will be refunded.", descFont);	
		Paragraph declPara = new Paragraph(0,declChunk);	
		declPara.setMultipliedLeading(3f);
		
		PdfPCell declCell = new PdfPCell(declPara); 
		declCell.setBorder(0);
		sigTable.addCell(declCell);
		PdfPCell signatureHeadCell = new PdfPCell(new Paragraph("________________________")); 
		signatureHeadCell.setPaddingTop(20f);
		signatureHeadCell.setBorder(0);
		sigTable.addCell(signatureHeadCell);
		sigTable.setWidthPercentage(100);  
		sigTable.completeRow();
		Chunk signatureChunk = new Chunk("Student Signature", descFont);	
		PdfPCell signatureCell = new PdfPCell(new Paragraph(signatureChunk )); 
		signatureCell.setBorder(0);
		sigTable.addCell(signatureCell);
		sigTable.completeRow();
		declTable.addCell(sigTable);
	
		declTable.setWidthPercentage(100); 
		
		declTable.completeRow();
		
		document.add(declTable);  
	}

	 public void generateWithdrawalReasonTable(Document document, ServiceRequestStudentPortal sr) throws DocumentException, MalformedURLException, IOException {
		
		Chunk section = new Chunk("Section 2 � Reason for withdrawal:", descFont);
		
		Paragraph sectionPara = new Paragraph();
		sectionPara.add(section);	
		sectionPara.setAlignment(Element.ALIGN_LEFT); 
		sectionPara.setSpacingAfter(10f);	
		document.add(sectionPara);  
		
		PdfPTable infoTable = new PdfPTable(1); // 2 columns.
		infoTable.setWidthPercentage(100); 
		
		if(sr.getAdditionalInfo1()!=null) {
			Image check = Image.getInstance(new URL(SERVER_PATH+"studentportal/assets/images/check-circle.png"));
			Image uncheck = Image.getInstance(new URL(SERVER_PATH+"studentportal/assets/images/uncheck-circle.png")); 
			Image logo1 = sr.getAdditionalInfo1().equalsIgnoreCase("Shifting Abroad")?check:uncheck;
			Image logo2 = sr.getAdditionalInfo1().equalsIgnoreCase("Not interested")?check:uncheck;
			Image logo3 = sr.getAdditionalInfo1().equalsIgnoreCase("No time to study")?check:uncheck;
			Image logo4 = sr.getAdditionalInfo1().equalsIgnoreCase("Other")?check:uncheck;
			PdfPCell option1Cell = optionsTableRow(logo1,"Shifting Abroad"); 
			infoTable.addCell(option1Cell);
			infoTable.completeRow();
			PdfPCell option2Cell = optionsTableRow(logo2,"Not interested");
			infoTable.addCell(option2Cell);
			infoTable.completeRow();
			PdfPCell option3Cell = optionsTableRow(logo3,"No time to study");
			infoTable.addCell(option3Cell);
			infoTable.completeRow();
			PdfPCell option4Cell = optionsTableRow(logo4,"Other");
			infoTable.addCell(option4Cell);
			infoTable.completeRow();
		}
		
		Chunk informationHeadChunk = new Chunk("If other please specify: ", descFont);	
		Chunk informationChunk =  (sr.getAdditionalInfo1()!=null)? 
				new Chunk(sr.getAdditionalInfo1(), descFont)
				:new Chunk("_________________________________________________________________________", descBoldFont);
		Paragraph informationPara = new Paragraph();				
		informationPara.add(informationHeadChunk);
		informationPara.add(informationChunk);	
		PdfPCell informationCell = new PdfPCell(informationPara);
		informationCell.setBorder(0);
		informationCell.setPaddingTop(7);
		informationCell.setPaddingBottom(7);
		informationCell.setPaddingLeft(5);
		infoTable.addCell(informationCell);
		infoTable.completeRow();
		
		PdfPCell borderedcell = new PdfPCell();
		PdfPTable outerReasonTable = new PdfPTable(1);
		outerReasonTable.setWidthPercentage(100);
		borderedcell.addElement(infoTable);
		outerReasonTable.addCell(borderedcell);
		outerReasonTable.setHorizontalAlignment(Element.ALIGN_LEFT); 
		document.add(outerReasonTable);  
	}

	 public void generateStudentInfoTable(Document document, StudentStudentPortalBean student) throws DocumentException {

		Chunk section = new Chunk("Section 1 � Student Information:", descFont);
		
		Paragraph sectionPara = new Paragraph();
		sectionPara.add(section);	
		sectionPara.setAlignment(Element.ALIGN_LEFT); 
		sectionPara.setSpacingAfter(10f);	
		document.add(sectionPara);  
		
		PdfPTable infoTable = new PdfPTable(1); // 2 columns.
		infoTable.setWidthPercentage(100);
		Chunk nameHeadChunk = new Chunk("NAME: ", descFont);	
		//Chunk nameChunk = new Chunk("____________________________________________________________________", descBoldFont);
		Chunk nameChunk =  (student.getFirstName() !=null)? 
				new Chunk(student.getFirstName()+" "+student.getLastName(), descFont)
				:new Chunk("____________________________________________________________________", descBoldFont);
		Paragraph namePara = new Paragraph();				
		namePara.add(nameHeadChunk);
		namePara.add(nameChunk);
		PdfPCell nameCell = new PdfPCell(namePara);
		nameCell.setPaddingTop(7);
	
		nameCell.setPaddingLeft(5);
		nameCell.setBorder(0);
		
		infoTable.addCell(nameCell);
		infoTable.completeRow();
		infoTable.setHorizontalAlignment(Element.ALIGN_LEFT);
		Chunk rollNoHeadChunk = new Chunk("ROLL NO.: ", descFont);	
		//Chunk rollNoChunk = new Chunk("___________________________________________________________________________________", descBoldFont);
		Chunk rollNoChunk = (student.getSapid() !=null)?
				new Chunk(student.getSapid(), descFont)
				:new Chunk("___________________________________________________________________________________", descBoldFont);
		Paragraph rollNoPara = new Paragraph();				
		rollNoPara.add(rollNoHeadChunk);
		rollNoPara.add(rollNoChunk);	
		PdfPCell rollNoCell = new PdfPCell(rollNoPara);
		rollNoCell.setBorder(0);
		rollNoCell.setPaddingTop(7);
		
		rollNoCell.setPaddingLeft(5);
		infoTable.addCell(rollNoCell);
		infoTable.completeRow();
		
		Chunk programHeadChunk = new Chunk("Program Name: ", descFont);	
		//Chunk programChunk = new Chunk("________________________________________________________________________________", descBoldFont);
		Chunk programChunk = (student.getProgram() !=null)?
				new Chunk(student.getProgram(), descFont)
				:new Chunk("________________________________________________________________________________", descBoldFont);
		Paragraph programPara = new Paragraph();				
		programPara.add(programHeadChunk);
		programPara.add(programChunk);	
		PdfPCell programCell = new PdfPCell(programPara);
		programCell.setBorder(0);
		programCell.setPaddingTop(7);

		programCell.setPaddingLeft(5);
		infoTable.addCell(programCell);
		infoTable.completeRow();
		
		Chunk admissionHeadChunk = new Chunk("Admission Month and Year: ", descFont);	
	
		Chunk admissionChunk =(student.getEnrollmentYear() !=null)?
				new Chunk(student.getEnrollmentMonth()+" "+student.getEnrollmentYear(), descFont)
				:new Chunk("______________________________________________________________________", descBoldFont);
				
		Paragraph admissionPara = new Paragraph();				
		admissionPara.add(admissionHeadChunk);
		admissionPara.add(admissionChunk);	
		PdfPCell admissionCell = new PdfPCell(admissionPara);
		admissionCell.setBorder(0);
		admissionCell.setPaddingTop(7);
		
		admissionCell.setPaddingLeft(5);
		infoTable.addCell(admissionCell);
		infoTable.completeRow();
		
		Chunk informationHeadChunk = new Chunk("Information Centre: ", descFont);	
		
		Chunk informationChunk = (student.getCenterName() !=null)?
				new Chunk(student.getCenterName(), descFont)
				:new Chunk("_____________________________________________________________________________", descBoldFont);
		
		Paragraph informationPara = new Paragraph();				
		informationPara.add(informationHeadChunk);
		informationPara.add(informationChunk);	
		PdfPCell informationCell = new PdfPCell(informationPara);
		informationCell.setBorder(0);
		informationCell.setPaddingTop(7);
		informationCell.setPaddingBottom(7);
		informationCell.setPaddingLeft(5);
		infoTable.addCell(informationCell);
		infoTable.completeRow();
		
		PdfPCell borderedcell = new PdfPCell();
		PdfPTable outerInfoTable = new PdfPTable(1);
		outerInfoTable.setWidthPercentage(100);
		borderedcell.addElement(infoTable);
		outerInfoTable.addCell(borderedcell);
		outerInfoTable.setHorizontalAlignment(Element.ALIGN_LEFT); 
		document.add(outerInfoTable);  
	}

	 public void generateHeaderTable(Document document) throws Exception {
		Image logo = null;
		try {
			logo = Image.getInstance(new URL("https://distance.nmims.edu/wp-content/themes/NMIMS/html/images/nmims-logo.png"));
			
			logo.setAlignment(Element.ALIGN_LEFT);  
			PdfPTable headerTable = new PdfPTable(1); 
			PdfPCell logoCell = new PdfPCell();
			logoCell.addElement(logo);
			logoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			logoCell.setBorder(0);  
			headerTable.addCell(logoCell);
			headerTable.completeRow();
			headerTable.setHorizontalAlignment(Element.ALIGN_CENTER); 
			headerTable.setWidthPercentage(40);  
			headerTable.completeRow();
			headerTable.setSpacingAfter(10f);
			document.add(headerTable);  
			
			Chunk subhead = new Chunk("Application for Withdrawal from Program of Study", font2);
			
			Paragraph subheadPara = new Paragraph();
			subheadPara.add(subhead);
			subheadPara.setAlignment(Element.ALIGN_CENTER);
			subheadPara.setSpacingAfter(8);	
			document.add(subheadPara); 
			
		} catch (Exception e) {
			//
			logger.error("generateHeaderTable : "+ e.getClass() + " - " + e.getMessage());
			throw e;
		}
	}
	 PdfPCell optionsTableRow(Image logo, String reason) throws DocumentException {
		PdfPCell logoCell = new PdfPCell();
		logoCell.addElement(logo);
		logoCell.setVerticalAlignment(Element.ALIGN_BOTTOM);
		//Chunk option1Chunk = new Chunk("*", descBoldFont);
		Chunk option1HeadChunk = new Chunk(reason, descFont); 
		Paragraph namePara = new Paragraph();				
		
		//namePara.add(option1Chunk);
		namePara.add(option1HeadChunk);
		PdfPCell reasonCell = new PdfPCell();
		 
		reasonCell.addElement(namePara);
		 
		PdfPTable optionTable = new PdfPTable(2);
		float[] columnWidths = {0.4f, 12.2f};  
		optionTable.setWidths(columnWidths);
		logoCell.setBorder(0);
		reasonCell.setBorder(0);
		optionTable.addCell(logoCell);
		optionTable.addCell(reasonCell); 
		PdfPCell optionCell = new PdfPCell(optionTable);
		optionCell.setPaddingTop(0); 
		optionCell.setPaddingLeft(5); 
		optionCell.setBorder(0);
		return optionCell;
	}

	public void createSrFeeReceipt(String FEE_RECEIPT_PATH, ServiceRequestStudentPortal sr, StudentStudentPortalBean student,String courierAmount,ArrayList<ServiceRequestStudentPortal> marksheetDetailAndAmountToBePaidList) throws MalformedURLException, IOException, ParseException {
		try {
			SimpleDateFormat sdfr = new SimpleDateFormat("dd-MMM-yyyy"); 
			SimpleDateFormat sdfrMonth = new SimpleDateFormat("MMM"); 
			SimpleDateFormat sdfrYear = new SimpleDateFormat("yyyy"); 
			Date bookingDate =new Date();
			String month = (sr.getMonth()==null)?sdfrMonth.format(bookingDate):sr.getMonth(); 
			String year = (sr.getYear()==null)?sdfrYear.format(bookingDate):sr.getYear();
			String fileName = sr.getSapId()+"_SR_"+month.toUpperCase()+"_"+year.toUpperCase()+"_"+RandomStringUtils.randomAlphanumeric(6)+".pdf";
			Image logo = Image.getInstance(new URL("https://staticfilesexam.s3.ap-south-1.amazonaws.com/resources_2015/images/logo.jpg"));
			//Check in Fee Receipt folder if this folder by sapid exists. If no then create one//
			
			String studentSapidFolderPath = FEE_RECEIPT_PATH +month+year;
			//String description =(sr.getDescriptionList() != null)?sr.getDescriptionList():sr.getDescription();
			//String srId =(sr.getSrIdList() != null)?sr.getSrIdList():sr.getId()+"";
			
			File folderPath = new File(studentSapidFolderPath);
			if (!folderPath.exists()) {
				// System.out.println("Making Folder");
				boolean created = folderPath.mkdirs();
				// System.out.println("created = " + created);
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


			Chunk examPeriodChunk = new Chunk(sr.getServiceRequestType().toUpperCase()+" SERVICE REQUEST FEE RECEIPT", font2);
			examPeriodChunk.setUnderline(0.1f, -2f);
			Paragraph examPeriodPara = new Paragraph();
			examPeriodPara.add(examPeriodChunk);
			examPeriodPara.setAlignment(Element.ALIGN_CENTER);
			document.add(examPeriodPara);
			
			document.add(new Paragraph(" \n" , font4));
			document.add(new Paragraph("Student Name :  "+student.getFirstName()+" "+student.getLastName(), font4));
			document.add(new Paragraph("Student ID: "+student.getSapid(), font4));
			document.add(new Paragraph("Program :  "+student.getProgram(), font4));
				
			if(sr.getServiceRequestType().equalsIgnoreCase("Issuance of Marksheet")
		|| sr.getServiceRequestType().equalsIgnoreCase("Issuance of Gradesheet")	) {
				document.add(new Paragraph("Receipt Generation Date : "+sdfr.format(bookingDate), font4));
				PdfPTable bookingsTable = new PdfPTable(6); 
				PdfPCell srIdCell = new PdfPCell(new Paragraph("Service Request Number", font4)); 
				PdfPCell descriptionCell = new PdfPCell(new Paragraph("Service Request Description", font4));
				PdfPCell examYearCell = new PdfPCell(new Paragraph("Exam Year", font4));
				PdfPCell examMonthCell = new PdfPCell(new Paragraph("Exam Month", font4)); 
				PdfPCell paymentStatusCell = new PdfPCell(new Paragraph("Payment Status", font4)); 
				PdfPCell amountCell =new PdfPCell(new Paragraph("Amount", font4)); 
				
				srIdCell.setVerticalAlignment(Element.ALIGN_TOP); 
				descriptionCell.setVerticalAlignment(Element.ALIGN_TOP);
				descriptionCell.setFixedHeight(30);
				examYearCell.setVerticalAlignment(Element.ALIGN_TOP);
				examMonthCell.setVerticalAlignment(Element.ALIGN_TOP);
				paymentStatusCell.setVerticalAlignment(Element.ALIGN_TOP);  
				 
				bookingsTable.addCell(srIdCell); 
				bookingsTable.addCell(descriptionCell);
				bookingsTable.addCell(examYearCell);
				bookingsTable.addCell(examMonthCell);
				bookingsTable.addCell(paymentStatusCell); 
				bookingsTable.addCell(amountCell); 
				bookingsTable.completeRow();
				int count = 1; 
				  for (ServiceRequestStudentPortal bean:marksheetDetailAndAmountToBePaidList) {  
						srIdCell = new PdfPCell(new Paragraph(bean.getId()+"", font9)); 
						descriptionCell = new PdfPCell(new Paragraph(bean.getDescriptionToBeShownInMarksheetSummary()+ "", font9));
						examYearCell = new PdfPCell(new Paragraph(bean.getYear(), font9));
						examMonthCell = new PdfPCell(new Paragraph(bean.getMonth(), font9));
						amountCell = new PdfPCell(new Paragraph(bean.getAmountToBeDisplayedForMarksheetSummary(), font9));
						paymentStatusCell = new PdfPCell(new Paragraph(bean.getTranStatus(), font9)); 
						bookingsTable.addCell(srIdCell); 
						bookingsTable.addCell(descriptionCell);
						bookingsTable.addCell(examYearCell);
						bookingsTable.addCell(examMonthCell); 
						bookingsTable.addCell(new PdfPCell(new Paragraph("Payment Successful", font9)));
						bookingsTable.addCell(amountCell); 
						bookingsTable.completeRow();
				  } 
				  bookingsTable.addCell(new PdfPCell(new Paragraph("", font9))); 
				  bookingsTable.addCell(new PdfPCell(new Paragraph("Courier Amount", font9))); 
				  bookingsTable.addCell(new PdfPCell(new Paragraph("", font9))); 
				  bookingsTable.addCell(new PdfPCell(new Paragraph("", font9))); 
				  String paymentStatus= (courierAmount!=null)?"Payment Successful":"";
				  bookingsTable.addCell(new PdfPCell(new Paragraph(paymentStatus, font9))); 
				  bookingsTable.addCell(new PdfPCell(new Paragraph(courierAmount, font9))); 
				  bookingsTable.completeRow();
				  bookingsTable.setWidthPercentage(100);
				  bookingsTable.setSpacingBefore(15f);
				  bookingsTable.setSpacingAfter(15f);
				float[] marksColumnWidths = {3f, 7f, 2.5f, 2.5f, 2.5f, 2.5f};
				bookingsTable.setWidths(marksColumnWidths);
				document.add(bookingsTable); 
			}else{
				document.add(new Paragraph("Service Request Number : "+sr.getId(), font4));
				document.add(new Paragraph("Service Request Name : "+sr.getServiceRequestType(), font4));
				document.add(new Paragraph("Service Request Description : "+sr.getDescription(), font4));
				document.add(new Paragraph("Receipt Generation Date : "+sdfr.format(bookingDate), font4));
			}
			document.add(new Paragraph("\n", font4));
			document.add(new Paragraph("Amount Paid (INR) : "+sr.getAmount()+"/-", font4));
			 
			if(ServiceRequestStudentPortal.ISSUEANCE_OF_BONAFIDE.equals(sr.getServiceRequestType())){
				document.add(new Paragraph("Service Request Amount : "+ sr.getAmount(), font4));
				document.add(new Paragraph(" Number Of Copies : "+sr.getNoOfCopies(), font4));
				document.add(new Paragraph(" Program : "+student.getProgram(), font4));
				document.add(new Paragraph(" Student Address : "+student.getAddress(), font4)); 
			 }	
			String para ="";
			String srId = (sr.getSrIdList()!=null)?sr.getSrIdList():sr.getId()+"";
			if(ServiceRequestStudentPortal.ISSUEANCE_OF_CERTIFICATE.equals(sr.getServiceRequestType())){
				para="Service Request Number " +srId+ " been created, "
					 +  "this request will be initiated once we resume work as our employees are working from home and printing of Certificate requires physical presence at our campus. "
					 +	"Meanwhile we are in process of developing E-copy of Certificate with digital signatures which can be sent to you.  ";
			
			}else if (ServiceRequestStudentPortal.ISSUEANCE_OF_MARKSHEET.equals(sr.getServiceRequestType())) {
				para= "Service Request Number " +srId+ " been created, "
					 +	"this request will be initiated once we resume work as our employees are working from home and printing of Marksheet requires physical presence at our campus. "
					 + 	"Meanwhile we are in process of developing E-copy of Marksheet with digital signatures which can be sent to you.  ";
			
			}else if (ServiceRequestStudentPortal.ISSUEANCE_OF_GRADESHEET.equals(sr.getServiceRequestType())) {
				para= "Service Request Number " +srId+ " been created, "
					 +	"this request will be initiated once we resume work as our employees are working from home and printing of Gradesheet requires physical presence at our campus. "
					 + 	"Meanwhile we are in process of developing E-copy of Gradesheet with digital signatures which can be sent to you. ";
			
			}else if (ServiceRequestStudentPortal.ISSUEANCE_OF_BONAFIDE.equals(sr.getServiceRequestType())) {
				para= "Service Request Number " +srId+ " been created, "
					 +	"due to on-going Coivd-19 pandemic, physical copy of Bonafide will not be couriered. We will still email you a soft copy of the Bonafide "
					 +	"in 7 working days to help you further. ";
				
			}else if (ServiceRequestStudentPortal.CHANGE_IN_ID.equals(sr.getServiceRequestType()) || ServiceRequestStudentPortal.DUPLICATE_ID.equals(sr.getServiceRequestType())) {
				para= "Service Request Number " +srId+ " been created, "
					 +	"and this request will be initiated once we resume work as our employees are working from home and "
					 +  "printing on ID card requires physical presence at our campus. ";
			
			}else if (ServiceRequestStudentPortal.CHANGE_IN_DOB.equals(sr.getServiceRequestType()) || ServiceRequestStudentPortal.CHANGE_IN_NAME.equals(sr.getServiceRequestType())) {
				para= "Service Request Number " +srId+ " been created, "
					 +  "and will be closed in 3 working days subject to compliance on required documentations. ";
			
			}else if (ServiceRequestStudentPortal.ISSUEANCE_OF_TRANSCRIPT.equals(sr.getServiceRequestType())) {
				para= "Service Request Number " +srId+ " been created, "
					 +  "this request will be initiated once we resume work as our employees are working from home and printing of Transcript requires physical presence at our campus. "
					 +	"Meanwhile we are in process of developing E-copy of Transcript with digital signatures which can be sent to you.  ";
			}
			document.add(new Paragraph("\n", font4));
			document.add(new Paragraph("\n", font4));
			if(para != null && !para.isEmpty()) {
				document.add(new Paragraph("Note:", font4));
				document.add(new Paragraph(para, font4));
			} 
			document.close();
			serviceRequestDao.insertDocumentRecord(studentSapidFolderPath+"/"+fileName, year, month, student.getSapid(), "SR Fee Receipt",sr.getTrackId());
		
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			//
			logger.error("createSrFeeReceipt : "+ e.getClass() + " - " + e.getMessage());
		} 
	}
}
