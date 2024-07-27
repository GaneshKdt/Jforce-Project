package com.nmims.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.nmims.beans.TestExamBean;

@Service("iATestHelper")
public class IATestHelper {
	@Value( "${SERVER_PATH}" )
	private String SERVER_PATH;
	

	@Value( "${ENVIRONMENT}" )
	private String ENVIRONMENT;

	
	//@Async Not keeping this async as for requirements
	public String setTestQuestionsInRedisByTestId(Long testId) {
		RestTemplate restTemplate = new RestTemplate();
		try {
			
			String url = "";
			if("PROD".equalsIgnoreCase(ENVIRONMENT)){ 
				url = SERVER_PATH+"timeline/api/test/setTestQuestionsInRedisByTestId";
			}else {
				url = "http://localhost:8181/timeline/api/test/setTestQuestionsInRedisByTestId";
			}
			
			TestExamBean test = new TestExamBean();
	    	test.setId(testId);
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			HttpEntity<TestExamBean> entity = new HttpEntity<TestExamBean>(test,headers);
			  
			  return restTemplate.exchange(
				 url,
			     HttpMethod.POST, entity, String.class).getBody();
		} catch (Exception e) {
			
			return "Error IN setTestQuestionsInRedisByTestId rest call got "+e.getMessage();
		}
	}

	/**
	 * Parsing the PDF Document from the InputStream and extracting text from the document. 
	 * @param url - URL of the PDF document
	 * @return extracted text of the PDF document
	 */
	public static String readPDFText(final String url) {
		try(InputStream inStream = new URL(url).openStream()) {				//Reads content from the connection and stores as an InputStream
			StringBuilder sb = new StringBuilder();
			PdfReader pdfReader = new PdfReader(inStream);					//Parses the PDF Document
			int noOfPages = pdfReader.getNumberOfPages();					//No of pages present in the PDF document
			int noOfPagesRead = 0;											//Counter variable to track the number of pages read
			
			while(noOfPagesRead < noOfPages) {								//Iterating through the noOfPages and reading text from each page of the document
				noOfPagesRead++;
				sb.append(PdfTextExtractor.getTextFromPage(pdfReader, noOfPagesRead));
			}
			
			return sb.toString();
		}
		catch(IOException ex) {
//			ex.printStackTrace();
			throw new RuntimeException("Unable to read text of PDF from URL: " + url);
		}
	}
}
