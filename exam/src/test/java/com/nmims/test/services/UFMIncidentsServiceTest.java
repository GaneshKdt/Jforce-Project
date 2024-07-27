package com.nmims.test.services;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;

import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.nmims.services.UFMNoticeService;
import com.nmims.stratergies.impl.UFMShowCauseStrategy;

@SpringBootTest
public class UFMIncidentsServiceTest {

	private static final File UFMexcelFile = new File("C:\\Users\\Shivam.sangale.EXT\\Documents\\Test1.xlsx");

	@Autowired
	UFMNoticeService ufmNoticeService;

	@Autowired
	UFMShowCauseStrategy showCauseStatergy;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

//@Test
//public void ufmIncidentExcelFileFormatTest() throws Exception {
//	
//}

	@Test
	public void testExcelUpload() throws Exception {
		// Create a sample Excel file
		Workbook wb = new HSSFWorkbook();
		// creates an excel file at the specified location
		wb.createSheet("Test");
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		wb.write(outputStream);
		byte[] bytes = outputStream.toByteArray();

		// Create a mock MultipartFile
		MultipartFile multipartFile = new MockMultipartFile("test.xlsx", "test.xlsx", "application/vnd.ms-excel",
				bytes);
		assertEquals("test.xlsx", multipartFile.getOriginalFilename());
		assertEquals("application/vnd.ms-excel", multipartFile.getContentType());
	}

	@Test
	public void testPdfUpload() throws Exception {
	    File pdfFile = new File("C:\\Users\\Shivam.sangale.EXT\\Documents\\bmd.pdf");

	    // Create a byte array from the PDF file
	    byte[] bytes = FileUtils.readFileToByteArray(pdfFile);

	    // Create a new MultipartFile object from the byte array
	    String fileName = pdfFile.getName();
	    String contentType = Files.probeContentType(pdfFile.toPath()); // Guess content type
	    MultipartFile multipartFile = new MockMultipartFile(fileName, fileName, contentType, bytes);
	    
	    assertEquals("test.xlsx", multipartFile.getOriginalFilename());
	    assertEquals("application/vnd.ms-excel", multipartFile.getContentType());
	}

}
