package com.nmims.services;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph; 
import com.itextpdf.text.pdf.PdfWriter;
import com.nmims.beans.ServiceRequestStudentPortal;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.daos.ServiceRequestDao;
import com.nmims.interfaces.FeeReceiptInterface;
@Service("gradesheetReceipt")
public class GradesheetSRFeeReceipt implements FeeReceiptInterface {
	
	@Value("${SERVER_PATH}")
	private String SERVER_PATH; 
	
	@Autowired(required=false)
	ApplicationContext act;

	@Autowired
	private ServiceRequestDao serviceRequestDao;
	
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
	
	@Override
	public void createSrFeeReceipt(String FEE_RECEIPT_PATH, ServiceRequestStudentPortal sr, StudentStudentPortalBean student, String courierAmount, ArrayList<ServiceRequestStudentPortal> marksheetDetailAndAmountToBePaidList) throws Exception {
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
				
			
				document.add(new Paragraph("Service Request Number : "+sr.getId(), font4));
				document.add(new Paragraph("Service Request Name : "+sr.getServiceRequestType(), font4));
				document.add(new Paragraph("Service Request Description : "+sr.getDescription(), font4));
				document.add(new Paragraph("Receipt Generation Date : "+sdfr.format(bookingDate), font4));
			
			document.add(new Paragraph("\n", font4));
			document.add(new Paragraph("Amount Paid (INR) : "+sr.getAmount()+"/-", font4));
			if(sr.getModeOfDispatch().equalsIgnoreCase("Courier")) { 
				document.add(new Paragraph("Student Address : "+student.getAddress(), font4)); 
		    }  
			/*
			 * String para =""; String srId = sr.getId()+"";
			 * 
			 * para= "Service Request Number " +srId+ " been created, " +
			 * "this request will be initiated once we resume work as our employees are working from home and printing of Gradesheet requires physical presence at our campus. "
			 * +
			 * "Meanwhile we are in process of developing E-copy of Gradesheet with digital signatures which can be sent to you. "
			 * ;
			 * 
			 * 
			 * document.add(new Paragraph("\n", font4)); document.add(new Paragraph("\n",
			 * font4)); if(para != null && !para.isEmpty()) { document.add(new
			 * Paragraph("Note:", font4)); document.add(new Paragraph(para, font4)); }
			 */
			document.close();
			serviceRequestDao.insertDocumentRecord(studentSapidFolderPath+"/"+fileName, year, month, student.getSapid(), "SR Fee Receipt",sr.getTrackId());
		
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			
		} 
	} 

}
