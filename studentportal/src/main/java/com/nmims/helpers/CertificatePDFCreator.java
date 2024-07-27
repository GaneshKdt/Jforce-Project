package com.nmims.helpers;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.RandomStringUtils;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.nmims.beans.ProgramsStudentPortalBean;
import com.nmims.beans.StudentStudentPortalBean;

public class CertificatePDFCreator {
	
	public static Map<String, String> generateEBonafideCertificate(StudentStudentPortalBean studentInfo, String SERVICE_REQUEST_FILES_PATH, 
		boolean isExitStudent,boolean isLogoRequired, Map<String, String> eBonafidePDFContent, String purpose, Long srId) throws DocumentException, MalformedURLException, IOException  {
		
		LocalDate currentDate = LocalDate.now();
		String certificateDate = currentDate.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
		String certificateDateMonth = currentDate.format(DateTimeFormatter.ofPattern("ddMM"));
		String studentFullName = studentInfo.getFirstName().replaceAll(" ", "_")+"_"+studentInfo.getLastName().replaceAll(" ", "_");
		
		String fileName = studentFullName + "_" +purpose.trim()+ "_Certificate_" + certificateDate + "_" + RandomStringUtils.randomAlphanumeric(5) + ".pdf";
		String folderPath = SERVICE_REQUEST_FILES_PATH + "IssuanceOfBonafide/";

		Document document = new Document(PageSize.LETTER);
		if (isLogoRequired)
			document.setMargins(55, 55, 20, 40);
		 else 
			 document.setMargins(50, 50, 20, 35);
		
		Files.createDirectories(Paths.get(folderPath));
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(folderPath + fileName));
		document.open();
		
		PdfPTable headerTable = new PdfPTable(1);
		if (isLogoRequired) {
			Image logo = Image.getInstance(new URL("https://staticfilesportal.s3.ap-south-1.amazonaws.com/assets/images/NMIMS-NGASCE+logo.png"));
//			logo.scaleAbsolute(142, 40);
			logo.scaleAbsolute(500,140);
			logo.setAlignment(Element.ALIGN_CENTER);

			PdfPCell logoCell = new PdfPCell();
			logoCell.addElement(logo);
			logoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			logoCell.setBorder(0);
			headerTable.addCell(logoCell);
			headerTable.completeRow();
			headerTable.setWidthPercentage(100);
			headerTable.setSpacingBefore(10);
			headerTable.setSpacingAfter(10);
		}
		document.add(headerTable);

		Paragraph blankLine = new Paragraph("\n");
		DateTimeFormatter Month = DateTimeFormatter.ofPattern("MMMM");
		DateTimeFormatter Year = DateTimeFormatter.ofPattern("yyyy");
		DateTimeFormatter Day = DateTimeFormatter.ofPattern("dd");
		LocalDateTime now = LocalDateTime.now();

		Font fb15 = new Font(FontFamily.TIMES_ROMAN, 15.0f, Font.BOLD);
		Font fb12 = new Font(FontFamily.TIMES_ROMAN, 12.0f, Font.BOLD);
		Font fb13 = new Font(FontFamily.TIMES_ROMAN, 13.0f, Font.BOLD);
		Font fb11 = new Font(FontFamily.TIMES_ROMAN, 11.0f, Font.BOLD);
		Font f12 = new Font(FontFamily.TIMES_ROMAN, 12.0f);
		Font f13 = new Font(FontFamily.TIMES_ROMAN, 13.0f);
		Font f10 = new Font(FontFamily.TIMES_ROMAN, 10.0f);
		Font f11 = new Font(FontFamily.TIMES_ROMAN, 11.0f);

		PdfPTable table = new PdfPTable(2); // 2 columns.
		table.setWidthPercentage(100);
		table.setSpacingBefore(10);
		table.setSpacingAfter(10);

		PdfPCell cell1 = new PdfPCell();
		Paragraph h1 = new Paragraph("Ref. No.: NGA-SCE/BON/" + Year.format(now) + "/" + srId, f12);
		h1.setAlignment(Element.ALIGN_LEFT);
		cell1.addElement(h1);
		cell1.setBorder(0);

		PdfPCell cell2 = new PdfPCell();
		Paragraph h2 = new Paragraph(Month.format(now) + " " + Day.format(now) + ", " + Year.format(now), f12);
		h2.setAlignment(Element.ALIGN_RIGHT);
		cell2.addElement(h2);
		cell2.setBorder(0);
		table.addCell(cell1);
		table.addCell(cell2);
		document.add(table);
		document.add(blankLine);

		Paragraph headerPara = new Paragraph("\n");
		Chunk headerUnderline = new Chunk("TO WHOMSOEVER IT MAY CONCERN", fb13);
		headerUnderline.setUnderline(0.1f, -2f);
		headerPara.add(headerUnderline);
		headerPara.setAlignment(Element.ALIGN_CENTER);
		document.add(headerPara);
		document.add(blankLine);

		Paragraph p1 = new Paragraph(eBonafidePDFContent.get("paragraphOne"),f13);
		document.add(p1);

		document.add(blankLine);

		Paragraph p2 = new Paragraph(eBonafidePDFContent.get("paragraphTwo"),f13);
		document.add(p2);

		if (isExitStudent && (studentInfo.getProgram().equals("CBM") || studentInfo.getProgram().equals("DBM"))) {
			document.add(blankLine);
			document.add(blankLine);
		}
		else {
			document.add(blankLine);
			document.add(blankLine);
			document.add(blankLine);
		}
			
		Paragraph p3 = new Paragraph("Regards, ", f13);
		document.add(p3);

		Image signature = Image.getInstance("https://d3q78eohsdsot3.cloudfront.net/assets/images/Rajivs_Sir_Signature.png");
		signature.scaleAbsolute(75, 39);
		signature.setAlignment(Element.ALIGN_LEFT);
		document.add(signature);
		document.add(blankLine);

		Paragraph p4 = new Paragraph("Authorized Signatory \n", fb12);
		document.add(p4);

		Paragraph p5 = new Paragraph("NGA-SCE ", fb13);
		document.add(p5);
		document.add(blankLine);
		
		PdfPTable footerTable = new PdfPTable(new float[] { 4, 0.5f }); // 2 columns.
		PdfPCell footerCell1 = new PdfPCell();
		PdfPCell footerCell2 = new PdfPCell();
		footerTable.setWidthPercentage(100);
		footerTable.completeRow();
		footerTable.setSpacingBefore(10);
		footerTable.setSpacingAfter(10);

		Paragraph footerP1 = new Paragraph();
		Chunk c1 = new Chunk("SVKM'S  \n", f10);
		footerP1.add(c1);
		
		Chunk c2 = new Chunk("Narsee Monjee Institute of Management Studies \n", fb15);
		c2.setUnderline(0.1f, -2f);
		footerP1.add(c2);
		
		Chunk c3 = new Chunk("Deemed to be UNIVERSITY \n", f12);
		Chunk c4 = new Chunk("2nd, Floor, NMIMS Building, V.L. Mehta	Road, Vile Parle(West), Mumbai-400 056, India.\n", f11);
		Chunk c5 = new Chunk("Tel: (91-22) 42355555 | Toll Free: 18001025136 \n", f11);
		Chunk c6 = new Chunk("Email: ngasce@nmims.edu | Web:", f11);
		Chunk c7 = new Chunk(" distance.nmims.edu", fb11);
		
		String certificateUniqueNumber = studentInfo.getSapid() + "-" + certificateDateMonth + RandomStringUtils.randomAlphanumeric(4);
		
		footerP1.add(c3);
		footerP1.add(c4);
		footerP1.add(c5);
		footerP1.add(c6);
		footerP1.add(c7);
		footerP1.setAlignment(Element.ALIGN_BOTTOM);
		footerCell1.addElement(footerP1);
		footerCell1.setBorder(0);
		footerTable.addCell(footerCell1);
		
		Image footerLogo = Image.getInstance(new URL("https://staticfilesportal.s3.ap-south-1.amazonaws.com/assets/images/Category-I+University.png"));
		footerLogo.scaleAbsolute(70,35);
		footerLogo.setAlignment(Element.ALIGN_CENTER);
		footerCell2.addElement(footerLogo);
		footerCell2.setBorder(0);
		footerCell2.setPaddingTop(25);
		footerTable.addCell(footerCell2);
		document.add(footerTable);
		
		PdfContentByte canvas = writer.getDirectContent();
		BaseColor brown = new BaseColor(170, 37, 7);
		canvas.setColorStroke(brown);
		canvas.moveTo(3, 6);
		canvas.lineTo(610, 6);
		canvas.setLineWidth(5);
		canvas.closePathStroke();
		document.close();

		Map<String, String> map = new HashMap<>();
		map.put("fileName", fileName);
		map.put("filePath", folderPath);
		map.put("certificateNumber", certificateUniqueNumber);
		return map;
	}
}
