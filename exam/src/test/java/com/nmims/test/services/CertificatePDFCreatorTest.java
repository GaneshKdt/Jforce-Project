//package com.nmims.test.services;
//
//import static org.junit.Assert.assertEquals;
//
//import java.io.FileOutputStream;
//
//import org.apache.commons.lang.RandomStringUtils;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import com.itextpdf.text.Document;
//import com.itextpdf.text.pdf.PdfWriter;
//import com.nmims.beans.MarksheetBean;
//import com.nmims.helpers.CertificatePDFCreator;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest
//public class CertificatePDFCreatorTest {
//
//	@Autowired
//	private CertificatePDFCreator pdfCreator;
//	
//	
//	@Test
//	public void generateCertificateAndReturnCertificateNumber()throws Exception
//	{
//		MarksheetBean student = new MarksheetBean();
//		student.setSapid("77220117397");
//		student.setExamMode("Offline");
//		student.setImageUrl("https://studentdocumentsngasce.s3.ap-south-1.amazonaws.com/StudentDocuments/00Q2j000002nGMc/Mx5w_photo.jpeg");
//		student.setFileName("Sahil");
//		student.setLastName("Gupta");
//		student.setMotherName("Neeru");
//		student.setFatherName("Subodh");
//		student.setHusbandName("No Husband");
//		student.setGender("Male");
//		student.setAdditionalInfo1("Spouse");
//		student.setPrgmStructApplicable("110");
//		student.setProgram("PD - DM");
//		student.setSpecialisation("No Specialisation");
//		student.setEnrollmentMonth("Jan");
//		student.setEnrollmentYear("2021");
//		
//		String completiondate = "2022-01-29";
//		
//		Document document = new Document();
//		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("C:/CertificateTest/" + "Certificate_Test_"+RandomStringUtils.randomAlphanumeric(5) + ".pdf"));
//		document.open();
//		
//		boolean expected = true;
//		boolean actual= false;
//		
//		pdfCreator.generateCertficateBackSide(document,student.getSapid(),student.getEnrollmentMonth(), student.getEnrollmentYear(),completiondate,false,writer);
//		actual = true;
//		
//		assertEquals(expected, actual);
//	}
//}
