package com.nmims.helpers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ibm.icu.text.SimpleDateFormat;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import com.nmims.beans.StudentExamBean;


@Component
public class FreeCourseCertificatePDFCreator {

	@Value("${CERTIFICATES_PATH}")
	private String CERTIFICATES_PATH;

	@Value("${TEMPLATE_FREE_COURSE_PARTICIPATION_CERTIFICATE}")
	private String TEMPLATE_FREE_COURSE_PARTICIPATION_CERTIFICATE;

	@Value("${TEMPLATE_FREE_COURSE_COMPLETION_CERTIFICATE}")
	private String TEMPLATE_FREE_COURSE_COMPLETION_CERTIFICATE;

	@Value("${SIGNATURE_DIRECTOR}")
	private String SIGNATURE_DIRECTOR;
	
	
	float width = 0;
    float height = 0;
    
    Document document;
    PdfWriter writer;
	public String createTrascript(StudentExamBean student, String nameOfProgram, String monthYear, String certificateType, String completionDate) throws Exception{
		Rectangle pageBounds = new Rectangle(2484, 3512);
		document = new Document(pageBounds);
		
		String sapid = student.getSapid();
		String fileName = monthYear + "/" + sapid + "_Certificate.pdf";

		try{
			File directory = new File(CERTIFICATES_PATH+monthYear + "/");
		    if (! directory.exists()){
		        directory.mkdir();
		    }
		    
			document.setMargins(0,0,0,0);
			writer = PdfWriter.getInstance(document, new FileOutputStream(CERTIFICATES_PATH+fileName));
			document.open();
			document.newPage();

			width = document.getPageSize().getWidth();
			height = document.getPageSize().getHeight();
			
			setBackground(certificateType);
			setStudentName(student.getFirstName() + " " + student.getLastName());
			setProgramName(nameOfProgram, certificateType);

			setDate(completionDate);
			setLocation();
			setSignature();
			
		}catch(Exception e){
			
			throw e;
		}finally{
			document.close(); 
		}
		return fileName;
	}
	
	private void setBackground(String certificateType) throws MalformedURLException, IOException, DocumentException {
		Image img;
		if(certificateType.equals("participation")) {
			img = Image.getInstance(TEMPLATE_FREE_COURSE_PARTICIPATION_CERTIFICATE);
		} else {
			img = Image.getInstance(TEMPLATE_FREE_COURSE_COMPLETION_CERTIFICATE);
		}
		img.setAlignment(Image.UNDERLYING);
		img.setAbsolutePosition(0, 0);
		float width = document.getPageSize().getWidth();
        float height = document.getPageSize().getHeight();

		writer.getDirectContentUnder().addImage(img, width, 0, 0, height, 0, 0);
	}
	
	private void setStudentName(String studentName) throws DocumentException {
		
		float left = width * (float) 0.18;
		float end = width * (float) 0.82;
		float bottom = height * (float) 0.48;
		float textHeight = height * (float) 0.02;
		
		addTextToDocument(studentName, left, bottom, end, textHeight);
	}
	private void setProgramName(String programName, String certificateType) throws DocumentException {
		
		float left = width * (float) 0.18;
		float end = width * (float) 0.82;
		float textHeight = height * (float) 0.015;
		float bottom = height * (float) (certificateType.equals("completion") ? 0.405 : 0.380);

		addTextToDocument(programName, left, bottom, end, textHeight);
	}
	
	private void setDate(String completionDate) throws DocumentException,Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
		/*String date = sdf.format(new Date());*/
		
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date cDate=sdf1.parse(completionDate);
		String date=sdf.format(cDate);
		
		float left = width * (float) 0.165;
		float end = width * (float) 0.41;
		float bottom = height * (float) 0.249;
		float textHeight = height * (float) 0.015;

		addTextToDocument(date, left, bottom, end, textHeight);
	}
	
	private void setLocation() throws DocumentException {

		float left = width * (float) 0.59;
		float end = width * (float) 0.835;
		float bottom = height * (float) 0.249;
		float textHeight = height * (float) 0.01;
		
        Font font = new Font(Font.FontFamily.TIMES_ROMAN, textHeight, Font.BOLD);
        
        PdfContentByte cb = writer.getDirectContent();
        ColumnText ct = new ColumnText(cb);
        Phrase text = new Phrase("NMIMS GLOBAL ACCESS\nSCHOOL FOR\nCONTINUING EDUCATION", font);
        ct.setSimpleColumn(text, left, bottom, end, bottom + (textHeight * 3), textHeight, Element.ALIGN_CENTER);
        ct.go();
	}
	
	private void setSignature() throws DocumentException, MalformedURLException, IOException {
		Image signature = Image.getInstance(new URL(SIGNATURE_DIRECTOR));
		signature.scaleAbsolute(400, 134);
		signature.setAbsolutePosition(width * (float) 0.4194, height * (float) 0.156);
		signature.setAlignment(Element.ALIGN_CENTER);
		document.add(signature);
	}

	private void addTextToDocument(String string, float left, float bottom, float end, float textHeight) throws DocumentException {

        Font font = new Font(Font.FontFamily.TIMES_ROMAN, textHeight, Font.BOLD);
        
        PdfContentByte cb = writer.getDirectContent();
        ColumnText ct = new ColumnText(cb);
        Phrase text = new Phrase(string, font);
        ct.setSimpleColumn(text, left, bottom, end, bottom + textHeight, textHeight, Element.ALIGN_CENTER);
        ct.go();
	}
	
	// To check the boundaries of the object
//	private void drawBounds(float left, float bottom, float end, float textHeight) throws DocumentException {
//		Rectangle rect= new Rectangle(left, bottom, end, bottom + (textHeight * 4));
//		rect.enableBorderSide(1);
//		rect.enableBorderSide(2);
//		rect.enableBorderSide(4);
//		rect.enableBorderSide(8);
//		rect.setBorderColor(BaseColor.BLACK);
//		rect.setBorderWidth(1);
//		document.add(rect);
//	}
}
