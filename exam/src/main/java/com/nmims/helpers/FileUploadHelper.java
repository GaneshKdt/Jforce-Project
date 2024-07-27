package com.nmims.helpers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;



@Component
public class FileUploadHelper 
{
	
	@Autowired
	AmazonS3Helper amazonS3;
	@Autowired
	CopyCaseHelper copyCaseHelper;
	
	private static final Logger marsheetService = LoggerFactory.getLogger("marsheetService");
	
	public String uploadDocument(String filePath,String baseFolderPath,String bucketName)
	{
		try {
		
		final File folder = new File(filePath);
		if(!folder.exists()) {
			marsheetService.error(" File does not exist in local ",filePath);
			return "";
		}
		
		HashMap<String,String> s3_response = new HashMap<String,String>();
	
		String baseUrl = FilenameUtils.getPath(filePath);
		String fileName = FilenameUtils.getBaseName(filePath)
	                + "." + FilenameUtils.getExtension(filePath);
	
		
			//upload File Helper
			s3_response = amazonS3.uploadLocalFileX(filePath,baseUrl+fileName,bucketName,baseUrl);
				
			if(s3_response.get("status").equals("success"))
			{
				deleteFileFromLocal(filePath);
				return s3_response.get("url");
			}else {
				marsheetService.error(" Error in s3 response  ",s3_response.toString());
			}
		}catch(Exception e)
		{
			marsheetService.error(" Error in uploading document to s3 ",e);
		}
		return "";
	}
	

	public int deleteFileFromLocal(String filePath)
	{
		try
		{
			 File file = new File(filePath);
			 file.delete();
			 return 1;
		}
		catch(Exception e)
		{
			marsheetService.error(" Error in deleting document from local ",e);
			   return 0;
		}
			          
    }
	
	public boolean isFileCorrupted(String fileName) {
		try (InputStream is = new URL(fileName).openStream();
			PDDocument document = PDDocument.load(is)) {			//using try-with-resources block which automatically closes the initialized resources
			
			int count = document.getNumberOfPages();
			PDFTextStripper stripper = new PDFTextStripper();
			stripper.setStartPage(1);
			stripper.setEndPage(count);
			
			String documentText = stripper.getText(document);
			documentText = documentText.replaceAll("\\R", "");		//Regex for new line character (\R) 
			
//			documentText = documentText.replaceAll("\n", "");
//			documentText = documentText.replaceAll("\r", "");
			
			if(documentText.trim().isEmpty())
				return true;
		}
		catch(IOException ex) {
			//IO Exception is thrown when PDDocument is unable to parse the PDF file
			//or when the InputStream and PDDocument objects cannot be closed
//			ex.printStackTrace();
			return true;
		}
		catch(Exception ex) {
//			ex.printStackTrace();
			return true;
		}
		
		return false;
	}

	/**
	 * Checks if the passed Content Type matches the Content-Type: application/pdf
	 * String equality checked with String.equalsIgnoreCase() because, as per RFC 2045, matching of media type and subtype is always case-insensitive.
	 * @param contentType - file Content-Type as a String
	 * @return boolean value indicating the Content-Type matches the PDF Content-Type or not
	 */
	public static boolean checkPdfFileContentType(String contentType) {
		return MediaType.APPLICATION_PDF_VALUE.equalsIgnoreCase(contentType);
	}
	
	
	public boolean isQPFileUploaded(String studentFileNameURL,String QPFileNameURL, String subject, String SERVER_PATH) {
		try {
			ArrayList<String> allQPFileContent = new ArrayList<String>();
			String Project_MethodologyFileURl = "https://studentzone-ngasce.nmims.edu/exam/resources_2015/Project_Methodology.pdf";
			String Project_EvaluationFileURl = "https://studentzone-ngasce.nmims.edu/exam/resources_2015/Project_Evaluation.pdf";
			// taking all QP file  text 
			ArrayList<String> questionFileContent = (ArrayList<String>) copyCaseHelper.readLineByLine(null,QPFileNameURL, new ArrayList<String>());
			
			// Commented below code due to now comparing all merged content of all qp files and comparing only one time same as CC process
			// check all uploaded text file and comparing each line with QP content
			// if student uploaded file is having with ans it will add the lines in the studentFileContent
			/*ArrayList<String> studentFileContent = (ArrayList<String>) copyCaseHelper.readLineByLine(null, studentFileNameURL, questionFileContent);
			if( studentFileContent.size() == 0 || studentFileContent.isEmpty() ) {
				//student uploaded file matches with the QP file/ Project Guidelines File
				return true; 
			}else if( subject != null && !subject.isEmpty() && (subject.equalsIgnoreCase("Project") || subject.equalsIgnoreCase("Module 4 Project")) ) {
				
				
				// taking all Project Methodology file  text
				ArrayList<String> Project_MethodologyFile = (ArrayList<String>) copyCaseHelper.readLineByLine(null,Project_MethodologyFileURl, new ArrayList<String>());
				
				// check all uploaded text file and comparing each line with Project Methodology content
				// if student uploaded file is having with ans it will add the lines in the studentMethodologyCompareResultFileContent
				ArrayList<String> studentMethodologyCompareResultFileContent = (ArrayList<String>) copyCaseHelper.readLineByLine(null, studentFileNameURL, Project_MethodologyFile);
				if( studentMethodologyCompareResultFileContent.size() == 0 || studentMethodologyCompareResultFileContent.isEmpty() ) {
					//student uploaded file matches with the Project Methodology File
					return true; 
				}else {
					
					// taking all Project Evaluation file  text
					ArrayList<String> Project_EvaluationFile = (ArrayList<String>) copyCaseHelper.readLineByLine(null,Project_EvaluationFileURl, new ArrayList<String>());
					
					// check all uploaded text file and comparing each line with Project Evaluation content
					// if student uploaded file is having with ans it will add the lines in the studentEvaluationCompareResultFileContent
					ArrayList<String> studentEvaluationCompareResultFileContent = (ArrayList<String>) copyCaseHelper.readLineByLine(null, studentFileNameURL, Project_EvaluationFile);
					if( studentEvaluationCompareResultFileContent.size() == 0 || studentEvaluationCompareResultFileContent.isEmpty() ) {
						//student uploaded file matches with the Project Evaluation File
						return true; 
					}
				}
			}*/
			ArrayList<String> Project_MethodologyFile = (ArrayList<String>) copyCaseHelper.readLineByLine(null,Project_MethodologyFileURl, new ArrayList<String>());
			ArrayList<String> Project_EvaluationFile = (ArrayList<String>) copyCaseHelper.readLineByLine(null,Project_EvaluationFileURl, new ArrayList<String>());
			allQPFileContent.addAll(questionFileContent);
			allQPFileContent.addAll(Project_MethodologyFile);
			allQPFileContent.addAll(Project_EvaluationFile);
			ArrayList<String> studentFileContent = (ArrayList<String>) copyCaseHelper.readLineByLine(null, studentFileNameURL, allQPFileContent);
			if( studentFileContent.size() == 0 || studentFileContent.isEmpty() ) {
				//student uploaded file matches with the QP file/ Project Guidelines File
				return true; 
			}
		}catch(Exception ex) {
//			ex.printStackTrace();
			return false;
	
		}
		return false;
	}
public boolean checkIsEndDateExpired(String endDate) {
	
	try {
		Date EndDate = new Date();
		Date serverDate = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
		EndDate = formatter.parse(endDate);
		//Check if end date is expired or not
		if(serverDate.after(EndDate)) {
			return true;
		}
	} catch (ParseException e) {
		// TODO Auto-generated catch block
		return false;
	}catch (Exception e) {
		return false;
		// TODO: handle exception
	}
	return false;
}
}
