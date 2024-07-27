package com.nmims.helpers;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class QpHeaderPageEvent extends PdfPageEventHelper {

    private PdfTemplate t;
    private Image total;
	private String subject;
	private String year;
	private String month;
	Font font8 = new Font(Font.FontFamily.HELVETICA, 9);
	Font font8Bold = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD);
	Font font9 = new Font(Font.FontFamily.HELVETICA, 10);
	Font font9Bold = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD); 
	Font font9Italic = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC);
	Font font9BoldItalic = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLDITALIC);
	Font font12 = new Font(Font.FontFamily.HELVETICA, 13);
	Font font12Bold = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD);
	
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
    public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public void onOpenDocument(PdfWriter writer, Document document) {
        t = writer.getDirectContent().createTemplate(30, 16);
        try {
            total = Image.getInstance(t);
            total.setRole(PdfName.ARTIFACT);
        } catch (DocumentException de) {
            throw new ExceptionConverter(de);
        }
    }

    @Override
    public void onStartPage(PdfWriter writer, Document document) {
        addHeader(writer,document); 
    }

    private void addHeader(PdfWriter writer,Document document){ 
        try { 
        	PdfPTable headerTable = new PdfPTable(1);
			Image logo = null;
			logo = Image.getInstance(new URL("https://staticfilesexam.s3.ap-south-1.amazonaws.com/resources_2015/images/logo.jpg"));
			logo.setAbsolutePosition(0, 0);
			logo.scaleAbsolute(200,50);  
			logo.setAlignment(Element.ALIGN_CENTER);
			//logo.scalePercent(57);
			PdfPCell logoCell = new PdfPCell();
			logoCell.addElement(logo);
			logoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			logoCell.setBorder(0);
			headerTable.addCell(logoCell);
			
			headerTable.completeRow();
			Paragraph nmimsPara = new Paragraph("NMIMS Global Access", font9Bold); 
			nmimsPara.setAlignment(Element.ALIGN_CENTER);
			nmimsPara.setSpacingAfter(3);
			PdfPCell nmimsParaCell = new PdfPCell();
			nmimsParaCell.setBorder(0);
			nmimsParaCell.addElement(nmimsPara);
			headerTable.addCell(nmimsParaCell);  
			headerTable.completeRow();
			Paragraph schoolPara = new Paragraph("School for Continuing Education (NGA-SCE)", font9Bold); 
			schoolPara.setAlignment(Element.ALIGN_CENTER);
			schoolPara.setSpacingAfter(3);
			PdfPCell schoolParaCell = new PdfPCell();
			schoolParaCell.setBorder(0);
			schoolParaCell.addElement(schoolPara); 
			headerTable.addCell(schoolParaCell);
			headerTable.completeRow();
			PdfPCell courseParaCell = new PdfPCell();
			courseParaCell.setBorder(0);
			Chunk chunk1 = new Chunk("Course: ",font9Bold);
			Chunk chunk2 = new Chunk(subject,font9);
			chunk2.setUnderline(0.1f, -2f); //0.1 thick, -2 y-location
			Paragraph coursePara = new Paragraph();
			coursePara.add(chunk1);
			coursePara.add(chunk2); 
			coursePara.setAlignment(Element.ALIGN_CENTER);
			coursePara.setSpacingAfter(3);
			courseParaCell.addElement(coursePara);
			headerTable.addCell(courseParaCell);
			headerTable.completeRow();
			PdfPCell iaParaCell = new PdfPCell();
			iaParaCell.setBorder(0);
			Paragraph iaPara = new Paragraph("Internal Assignment Applicable for "+month+" "+year+" Examination", font9Bold); 
			iaPara.setAlignment(Element.ALIGN_CENTER);
			iaPara.setSpacingAfter(8); 
			iaParaCell.addElement(iaPara);
			headerTable.addCell(iaParaCell);
			document.add(headerTable); 
			//headerTable.writeSelectedRows(0, -1, 34, 403, writer.getDirectContent());
        } catch(DocumentException de) {
            throw new ExceptionConverter(de);
        } catch (MalformedURLException e) {
            throw new ExceptionConverter(e);
        } catch (IOException e) {
            throw new ExceptionConverter(e);
        } 
    }
 

    public void onCloseDocument(PdfWriter writer, Document document) {
        int totalLength = String.valueOf(writer.getPageNumber()).length();
        int totalWidth = totalLength * 5;
        ColumnText.showTextAligned(t, Element.ALIGN_RIGHT,
                new Phrase(String.valueOf(writer.getPageNumber()), new Font(Font.FontFamily.HELVETICA, 8)),
                totalWidth, 6, 0);
    }
}